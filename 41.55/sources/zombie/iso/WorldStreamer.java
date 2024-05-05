package zombie.iso;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import zombie.ChunkMapFilenames;
import zombie.GameWindow;
import zombie.SystemDisabler;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.erosion.categories.ErosionCategory;
import zombie.gameStates.GameLoadingState;
import zombie.network.ChunkChecksum;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.savefile.PlayerDB;
import zombie.vehicles.VehiclesDB2;


public final class WorldStreamer {
	static final WorldStreamer.ChunkComparator comp = new WorldStreamer.ChunkComparator();
	private static final int CRF_CANCEL = 1;
	private static final int CRF_CANCEL_SENT = 2;
	private static final int CRF_DELETE = 4;
	private static final int CRF_TIMEOUT = 8;
	private static final int CRF_RECEIVED = 16;
	private static final int BLOCK_SIZE = 1024;
	public static WorldStreamer instance = new WorldStreamer();
	private final ConcurrentLinkedQueue jobQueue = new ConcurrentLinkedQueue();
	private final Stack jobList = new Stack();
	private final ConcurrentLinkedQueue chunkRequests0 = new ConcurrentLinkedQueue();
	private final ArrayList chunkRequests1 = new ArrayList();
	private final ArrayList pendingRequests = new ArrayList();
	private final ArrayList pendingRequests1 = new ArrayList();
	private final ConcurrentLinkedQueue sentRequests = new ConcurrentLinkedQueue();
	private final CRC32 crc32 = new CRC32();
	private final ConcurrentLinkedQueue freeBuffers = new ConcurrentLinkedQueue();
	private final ConcurrentLinkedQueue waitingToSendQ = new ConcurrentLinkedQueue();
	private final ArrayList tempRequests = new ArrayList();
	private final Inflater decompressor = new Inflater();
	private final byte[] readBuf = new byte[1024];
	private final ConcurrentLinkedQueue waitingToCancelQ = new ConcurrentLinkedQueue();
	public Thread worldStreamer;
	public boolean bFinished = false;
	private IsoChunk chunkHeadMain;
	private int requestNumber;
	private boolean bCompare = false;
	private boolean NetworkFileDebug;
	private ByteBuffer inMemoryZip;
	private boolean requestingLargeArea = false;
	private volatile int largeAreaDownloads;
	private ByteBuffer bb1 = ByteBuffer.allocate(5120);
	private ByteBuffer bb2 = ByteBuffer.allocate(5120);

	private int bufferSize(int int1) {
		return (int1 + 1024 - 1) / 1024 * 1024;
	}

	private ByteBuffer ensureCapacity(ByteBuffer byteBuffer, int int1) {
		if (byteBuffer == null) {
			return ByteBuffer.allocate(this.bufferSize(int1));
		} else if (byteBuffer.capacity() < int1) {
			ByteBuffer byteBuffer2 = ByteBuffer.allocate(this.bufferSize(int1));
			return byteBuffer2.put(byteBuffer.array(), 0, byteBuffer.position());
		} else {
			return byteBuffer;
		}
	}

	private ByteBuffer getByteBuffer(int int1) {
		ByteBuffer byteBuffer = (ByteBuffer)this.freeBuffers.poll();
		if (byteBuffer == null) {
			return ByteBuffer.allocate(this.bufferSize(int1));
		} else {
			byteBuffer.clear();
			return this.ensureCapacity(byteBuffer, int1);
		}
	}

	private void releaseBuffer(ByteBuffer byteBuffer) {
		this.freeBuffers.add(byteBuffer);
	}

	private void sendRequests() throws IOException {
		if (!this.chunkRequests1.isEmpty()) {
			if (!this.requestingLargeArea || this.pendingRequests1.size() <= 20) {
				long long1 = System.currentTimeMillis();
				WorldStreamer.ChunkRequest chunkRequest = null;
				WorldStreamer.ChunkRequest chunkRequest2 = null;
				for (int int1 = this.chunkRequests1.size() - 1; int1 >= 0; --int1) {
					IsoChunk chunk = (IsoChunk)this.chunkRequests1.get(int1);
					WorldStreamer.ChunkRequest chunkRequest3 = WorldStreamer.ChunkRequest.alloc();
					chunkRequest3.chunk = chunk;
					chunkRequest3.requestNumber = this.requestNumber++;
					chunkRequest3.time = long1;
					chunkRequest3.crc = ChunkChecksum.getChecksum(chunk.wx, chunk.wy);
					if (chunkRequest == null) {
						chunkRequest = chunkRequest3;
					} else {
						chunkRequest2.next = chunkRequest3;
					}

					chunkRequest3.next = null;
					chunkRequest2 = chunkRequest3;
					this.pendingRequests1.add(chunkRequest3);
					this.chunkRequests1.remove(int1);
					if (this.requestingLargeArea && this.pendingRequests1.size() >= 40) {
						break;
					}
				}

				this.waitingToSendQ.add(chunkRequest);
			}
		}
	}

	public void updateMain() {
		UdpConnection udpConnection = GameClient.connection;
		if (this.chunkHeadMain != null) {
			this.chunkRequests0.add(this.chunkHeadMain);
			this.chunkHeadMain = null;
		}

		this.tempRequests.clear();
		WorldStreamer.ChunkRequest chunkRequest;
		WorldStreamer.ChunkRequest chunkRequest2;
		for (chunkRequest = (WorldStreamer.ChunkRequest)this.waitingToSendQ.poll(); chunkRequest != null; chunkRequest = (WorldStreamer.ChunkRequest)this.waitingToSendQ.poll()) {
			for (; chunkRequest != null; chunkRequest = chunkRequest2) {
				chunkRequest2 = chunkRequest.next;
				if ((chunkRequest.flagsWS & 1) != 0) {
					chunkRequest.flagsUDP |= 16;
				} else {
					this.tempRequests.add(chunkRequest);
				}
			}
		}

		WorldStreamer.ChunkRequest chunkRequest3;
		ByteBufferWriter byteBufferWriter;
		int int1;
		if (!this.tempRequests.isEmpty()) {
			byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)34, byteBufferWriter);
			byteBufferWriter.putInt(this.tempRequests.size());
			for (int1 = 0; int1 < this.tempRequests.size(); ++int1) {
				chunkRequest3 = (WorldStreamer.ChunkRequest)this.tempRequests.get(int1);
				byteBufferWriter.putInt(chunkRequest3.requestNumber);
				byteBufferWriter.putInt(chunkRequest3.chunk.wx);
				byteBufferWriter.putInt(chunkRequest3.chunk.wy);
				byteBufferWriter.putLong(chunkRequest3.crc);
				if (this.NetworkFileDebug) {
					DebugLog.log(DebugType.NetworkFileDebug, "requested " + chunkRequest3.chunk.wx + "," + chunkRequest3.chunk.wy + " crc=" + chunkRequest3.crc);
				}
			}

			udpConnection.endPacket();
			for (int1 = 0; int1 < this.tempRequests.size(); ++int1) {
				chunkRequest3 = (WorldStreamer.ChunkRequest)this.tempRequests.get(int1);
				this.sentRequests.add(chunkRequest3);
			}
		}

		this.tempRequests.clear();
		for (chunkRequest = (WorldStreamer.ChunkRequest)this.waitingToCancelQ.poll(); chunkRequest != null; chunkRequest = (WorldStreamer.ChunkRequest)this.waitingToCancelQ.poll()) {
			this.tempRequests.add(chunkRequest);
		}

		if (!this.tempRequests.isEmpty()) {
			byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)36, byteBufferWriter);
			try {
				byteBufferWriter.putInt(this.tempRequests.size());
				for (int1 = 0; int1 < this.tempRequests.size(); ++int1) {
					chunkRequest3 = (WorldStreamer.ChunkRequest)this.tempRequests.get(int1);
					if (this.NetworkFileDebug) {
						DebugLog.log(DebugType.NetworkFileDebug, "cancelled " + chunkRequest3.chunk.wx + "," + chunkRequest3.chunk.wy);
					}

					byteBufferWriter.putInt(chunkRequest3.requestNumber);
					chunkRequest3.flagsMain |= 2;
				}

				udpConnection.endPacket();
			} catch (Exception exception) {
				exception.printStackTrace();
				udpConnection.cancelPacket();
			}
		}
	}

	private void loadReceivedChunks() throws DataFormatException, IOException {
		boolean boolean1 = false;
		int int1 = 0;
		int int2 = 0;
		for (int int3 = 0; int3 < this.pendingRequests1.size(); ++int3) {
			WorldStreamer.ChunkRequest chunkRequest = (WorldStreamer.ChunkRequest)this.pendingRequests1.get(int3);
			if ((chunkRequest.flagsUDP & 16) != 0) {
				if (boolean1) {
					++int1;
					if ((chunkRequest.flagsWS & 1) != 0) {
						++int2;
					}
				}

				if ((chunkRequest.flagsWS & 1) == 0 || (chunkRequest.flagsMain & 2) != 0) {
					this.pendingRequests1.remove(int3--);
					ChunkSaveWorker.instance.Update(chunkRequest.chunk);
					if ((chunkRequest.flagsUDP & 4) != 0) {
						File file = ChunkMapFilenames.instance.getFilename(chunkRequest.chunk.wx, chunkRequest.chunk.wy);
						if (file.exists()) {
							if (this.NetworkFileDebug) {
								DebugLog.log(DebugType.NetworkFileDebug, "deleting map_" + chunkRequest.chunk.wx + "_" + chunkRequest.chunk.wy + ".bin because it doesn\'t exist on the server");
							}

							file.delete();
							ChunkChecksum.setChecksum(chunkRequest.chunk.wx, chunkRequest.chunk.wy, 0L);
						}
					}

					ByteBuffer byteBuffer = (chunkRequest.flagsWS & 1) != 0 ? null : chunkRequest.bb;
					if (byteBuffer != null) {
						byteBuffer = this.decompress(byteBuffer);
						if (this.bCompare) {
							File file2 = ChunkMapFilenames.instance.getFilename(chunkRequest.chunk.wx, chunkRequest.chunk.wy);
							if (file2.exists()) {
								this.compare(chunkRequest, byteBuffer, file2);
							}
						}
					}

					if ((chunkRequest.flagsWS & 8) == 0) {
						if ((chunkRequest.flagsWS & 1) == 0 && !chunkRequest.chunk.refs.isEmpty()) {
							if (byteBuffer != null) {
								byteBuffer.position(0);
							}

							this.DoChunk(chunkRequest.chunk, byteBuffer);
						} else {
							if (this.NetworkFileDebug) {
								DebugLog.log(DebugType.NetworkFileDebug, chunkRequest.chunk.wx + "_" + chunkRequest.chunk.wy + " refs.isEmpty() SafeWrite=" + (byteBuffer != null));
							}

							if (byteBuffer != null) {
								long long1 = ChunkChecksum.getChecksumIfExists(chunkRequest.chunk.wx, chunkRequest.chunk.wy);
								this.crc32.reset();
								this.crc32.update(byteBuffer.array(), 0, byteBuffer.position());
								if (long1 != this.crc32.getValue()) {
									ChunkChecksum.setChecksum(chunkRequest.chunk.wx, chunkRequest.chunk.wy, this.crc32.getValue());
									IsoChunk.SafeWrite("map_", chunkRequest.chunk.wx, chunkRequest.chunk.wy, byteBuffer);
								}
							}

							chunkRequest.chunk.resetForStore();
							assert !IsoChunkMap.chunkStore.contains(chunkRequest.chunk);
							IsoChunkMap.chunkStore.add(chunkRequest.chunk);
						}
					}

					if (chunkRequest.bb != null) {
						this.releaseBuffer(chunkRequest.bb);
					}

					WorldStreamer.ChunkRequest.release(chunkRequest);
				}
			}
		}

		if (boolean1 && (int1 != 0 || int2 != 0 || !this.pendingRequests1.isEmpty())) {
			DebugLog.log("nReceived=" + int1 + " nCancel=" + int2 + " nPending=" + this.pendingRequests1.size());
		}
	}

	private ByteBuffer decompress(ByteBuffer byteBuffer) throws DataFormatException {
		this.decompressor.reset();
		this.decompressor.setInput(byteBuffer.array(), 0, byteBuffer.position());
		int int1 = 0;
		if (this.inMemoryZip != null) {
			this.inMemoryZip.clear();
		}

		while (!this.decompressor.finished()) {
			int int2 = this.decompressor.inflate(this.readBuf);
			this.inMemoryZip = this.ensureCapacity(this.inMemoryZip, int1 + int2);
			this.inMemoryZip.put(this.readBuf, 0, int2);
			int1 += int2;
		}

		this.inMemoryZip.limit(this.inMemoryZip.position());
		return this.inMemoryZip;
	}

	private void threadLoop() throws DataFormatException, InterruptedException, IOException {
		IsoChunk chunk;
		IsoChunk chunk2;
		if (GameClient.bClient && !SystemDisabler.doWorldSyncEnable) {
			this.NetworkFileDebug = DebugType.Do(DebugType.NetworkFileDebug);
			for (chunk = (IsoChunk)this.chunkRequests0.poll(); chunk != null; chunk = (IsoChunk)this.chunkRequests0.poll()) {
				while (chunk != null) {
					chunk2 = chunk.next;
					this.chunkRequests1.add(chunk);
					chunk = chunk2;
				}
			}

			if (!this.chunkRequests1.isEmpty()) {
				comp.init();
				Collections.sort(this.chunkRequests1, comp);
				this.sendRequests();
			}

			this.loadReceivedChunks();
			this.cancelOutOfBoundsRequests();
			this.resendTimedOutRequests();
		}

		for (chunk = (IsoChunk)this.jobQueue.poll(); chunk != null; chunk = (IsoChunk)this.jobQueue.poll()) {
			if (this.jobList.contains(chunk)) {
				DebugLog.log("Ignoring duplicate chunk added to WorldStreamer.jobList");
			} else {
				this.jobList.add(chunk);
			}
		}

		if (this.jobList.isEmpty()) {
			ChunkSaveWorker.instance.Update((IsoChunk)null);
			if (ChunkSaveWorker.instance.bSaving) {
				return;
			}

			if (!this.pendingRequests1.isEmpty()) {
				Thread.sleep(20L);
				return;
			}

			Thread.sleep(140L);
		} else {
			int int1 = this.jobList.size() - 1;
			while (true) {
				if (int1 < 0) {
					boolean boolean1 = !this.jobList.isEmpty();
					chunk2 = null;
					if (boolean1) {
						comp.init();
						Collections.sort(this.jobList, comp);
						chunk2 = (IsoChunk)this.jobList.remove(this.jobList.size() - 1);
					}

					ChunkSaveWorker.instance.Update(chunk2);
					if (chunk2 != null) {
						if (chunk2.refs.isEmpty()) {
							chunk2.resetForStore();
							assert !IsoChunkMap.chunkStore.contains(chunk2);
							IsoChunkMap.chunkStore.add(chunk2);
						} else {
							this.DoChunk(chunk2, (ByteBuffer)null);
						}
					}

					if (boolean1 || ChunkSaveWorker.instance.bSaving) {
						return;
					}

					break;
				}

				chunk2 = (IsoChunk)this.jobList.get(int1);
				if (chunk2.refs.isEmpty()) {
					this.jobList.remove(int1);
					chunk2.resetForStore();
					assert !IsoChunkMap.chunkStore.contains(chunk2);
					IsoChunkMap.chunkStore.add(chunk2);
				}

				--int1;
			}
		}

		if (!GameClient.bClient && !GameWindow.bLoadedAsClient && PlayerDB.isAvailable()) {
			PlayerDB.getInstance().updateWorldStreamer();
		}

		VehiclesDB2.instance.updateWorldStreamer();
		if (IsoPlayer.getInstance() != null) {
			Thread.sleep(140L);
		} else {
			Thread.sleep(0L);
		}
	}

	public void create() {
		if (this.worldStreamer == null) {
			if (!GameServer.bServer) {
				this.bFinished = false;
				this.worldStreamer = new Thread(ThreadGroups.Workers, ()->{
					while (!this.bFinished) {
						try {
							this.threadLoop();
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});

				this.worldStreamer.setPriority(5);
				this.worldStreamer.setDaemon(true);
				this.worldStreamer.setName("World Streamer");
				this.worldStreamer.setUncaughtExceptionHandler(GameWindow::uncaughtException);
				this.worldStreamer.start();
			}
		}
	}

	public void addJob(IsoChunk chunk, int int1, int int2, boolean boolean1) {
		if (!GameServer.bServer) {
			chunk.wx = int1;
			chunk.wy = int2;
			if (GameClient.bClient && !SystemDisabler.doWorldSyncEnable && boolean1) {
				chunk.next = this.chunkHeadMain;
				this.chunkHeadMain = chunk;
			} else {
				assert !this.jobQueue.contains(chunk);
				assert !this.jobList.contains(chunk);
				this.jobQueue.add(chunk);
			}
		}
	}

	public void DoChunk(IsoChunk chunk, ByteBuffer byteBuffer) {
		if (!GameServer.bServer) {
			this.DoChunkAlways(chunk, byteBuffer);
		}
	}

	public void DoChunkAlways(IsoChunk chunk, ByteBuffer byteBuffer) {
		if (Core.bDebug && DebugOptions.instance.WorldStreamerSlowLoad.getValue()) {
			try {
				Thread.sleep(50L);
			} catch (InterruptedException interruptedException) {
			}
		}

		if (chunk != null) {
			try {
				if (!chunk.LoadOrCreate(chunk.wx, chunk.wy, byteBuffer)) {
					if (GameClient.bClient) {
						ChunkChecksum.setChecksum(chunk.wx, chunk.wy, 0L);
					}

					chunk.Blam(chunk.wx, chunk.wy);
					if (!chunk.LoadBrandNew(chunk.wx, chunk.wy)) {
						return;
					}
				}

				if (byteBuffer == null) {
					VehiclesDB2.instance.loadChunk(chunk);
				}
			} catch (Exception exception) {
				DebugLog.General.error("Exception thrown while trying to load chunk: " + chunk.wx + ", " + chunk.wy);
				exception.printStackTrace();
				if (GameClient.bClient) {
					ChunkChecksum.setChecksum(chunk.wx, chunk.wy, 0L);
				}

				chunk.Blam(chunk.wx, chunk.wy);
				if (!chunk.LoadBrandNew(chunk.wx, chunk.wy)) {
					return;
				}
			}

			if (chunk.jobType != IsoChunk.JobType.Convert && chunk.jobType != IsoChunk.JobType.SoftReset) {
				try {
					if (!chunk.refs.isEmpty()) {
						chunk.loadInWorldStreamerThread();
					}
				} catch (Exception exception2) {
					exception2.printStackTrace();
				}

				IsoChunk.loadGridSquare.add(chunk);
			} else {
				chunk.doLoadGridsquare();
				chunk.bLoaded = true;
			}
		}
	}

	public void addJobInstant(IsoChunk chunk, int int1, int int2, int int3, int int4) {
		if (!GameServer.bServer) {
			chunk.wx = int3;
			chunk.wy = int4;
			try {
				this.DoChunkAlways(chunk, (ByteBuffer)null);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void addJobConvert(IsoChunk chunk, int int1, int int2, int int3, int int4) {
		if (!GameServer.bServer) {
			chunk.wx = int3;
			chunk.wy = int4;
			chunk.jobType = IsoChunk.JobType.Convert;
			try {
				this.DoChunk(chunk, (ByteBuffer)null);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void addJobWipe(IsoChunk chunk, int int1, int int2, int int3, int int4) {
		chunk.wx = int3;
		chunk.wy = int4;
		chunk.jobType = IsoChunk.JobType.SoftReset;
		try {
			this.DoChunkAlways(chunk, (ByteBuffer)null);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public boolean isBusy() {
		if (GameClient.bClient && (!this.chunkRequests0.isEmpty() || !this.chunkRequests1.isEmpty() || this.chunkHeadMain != null || !this.waitingToSendQ.isEmpty() || !this.waitingToCancelQ.isEmpty() || !this.sentRequests.isEmpty() || !this.pendingRequests.isEmpty() || !this.pendingRequests1.isEmpty())) {
			return true;
		} else {
			return !this.jobQueue.isEmpty() || !this.jobList.isEmpty();
		}
	}

	public void stop() {
		DebugLog.log("EXITDEBUG: WorldStreamer.stop 1");
		if (this.worldStreamer != null) {
			this.bFinished = true;
			DebugLog.log("EXITDEBUG: WorldStreamer.stop 2");
			while (this.worldStreamer.isAlive()) {
			}

			DebugLog.log("EXITDEBUG: WorldStreamer.stop 3");
			this.worldStreamer = null;
			this.jobList.clear();
			this.jobQueue.clear();
			DebugLog.log("EXITDEBUG: WorldStreamer.stop 4");
			ChunkSaveWorker.instance.SaveNow();
			ChunkChecksum.Reset();
			DebugLog.log("EXITDEBUG: WorldStreamer.stop 5");
		}
	}

	public void quit() {
		this.stop();
	}

	public void requestLargeAreaZip(int int1, int int2, int int3) throws IOException {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.doPacket((short)24, byteBufferWriter);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putInt(IsoChunkMap.ChunkGridWidth);
		GameClient.connection.endPacketImmediate();
		this.requestingLargeArea = true;
		this.largeAreaDownloads = 0;
		GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_RequestMapData");
		int int4 = 0;
		int int5 = int1 - int3;
		int int6 = int2 - int3;
		int int7 = int1 + int3;
		int int8 = int2 + int3;
		for (int int9 = int6; int9 <= int8; ++int9) {
			for (int int10 = int5; int10 <= int7; ++int10) {
				if (IsoWorld.instance.MetaGrid.isValidChunk(int10, int9)) {
					IsoChunk chunk = (IsoChunk)IsoChunkMap.chunkStore.poll();
					if (chunk == null) {
						chunk = new IsoChunk(IsoWorld.instance.CurrentCell);
					}

					this.addJob(chunk, int10, int9, true);
					++int4;
				}
			}
		}

		DebugLog.log("Requested " + int4 + " chunks from the server");
		long long1 = System.currentTimeMillis();
		long long2 = long1;
		int int11 = 0;
		int int12 = 0;
		while (this.isBusy()) {
			long long3 = System.currentTimeMillis();
			if (long3 - long2 > 60000L) {
				GameLoadingState.mapDownloadFailed = true;
				throw new IOException("map download from server timed out");
			}

			int int13 = this.largeAreaDownloads;
			GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_DownloadedMapData", int13, int4);
			long long4 = long3 - long1;
			if (long4 / 1000L > (long)int11) {
				DebugLog.log("Received " + int13 + " / " + int4 + " chunks");
				int11 = (int)(long4 / 1000L);
			}

			if (int12 < int13) {
				long2 = long3;
				int12 = int13;
			}

			try {
				Thread.sleep(100L);
			} catch (InterruptedException interruptedException) {
			}
		}

		DebugLog.log("Received " + this.largeAreaDownloads + " / " + int4 + " chunks");
		this.requestingLargeArea = false;
	}

	private void cancelOutOfBoundsRequests() {
		if (!this.requestingLargeArea) {
			for (int int1 = 0; int1 < this.pendingRequests1.size(); ++int1) {
				WorldStreamer.ChunkRequest chunkRequest = (WorldStreamer.ChunkRequest)this.pendingRequests1.get(int1);
				if ((chunkRequest.flagsWS & 1) == 0 && chunkRequest.chunk.refs.isEmpty()) {
					chunkRequest.flagsWS |= 1;
					this.waitingToCancelQ.add(chunkRequest);
				}
			}
		}
	}

	private void resendTimedOutRequests() {
		long long1 = System.currentTimeMillis();
		for (int int1 = 0; int1 < this.pendingRequests1.size(); ++int1) {
			WorldStreamer.ChunkRequest chunkRequest = (WorldStreamer.ChunkRequest)this.pendingRequests1.get(int1);
			if ((chunkRequest.flagsWS & 1) == 0 && chunkRequest.time + 8000L < long1) {
				if (this.NetworkFileDebug) {
					DebugLog.log(DebugType.NetworkFileDebug, "chunk request timed out " + chunkRequest.chunk.wx + "," + chunkRequest.chunk.wy);
				}

				this.chunkRequests1.add(chunkRequest.chunk);
				chunkRequest.flagsWS |= 9;
				chunkRequest.flagsMain |= 2;
			}
		}
	}

	public void receiveChunkPart(ByteBuffer byteBuffer) {
		for (WorldStreamer.ChunkRequest chunkRequest = (WorldStreamer.ChunkRequest)this.sentRequests.poll(); chunkRequest != null; chunkRequest = (WorldStreamer.ChunkRequest)this.sentRequests.poll()) {
			this.pendingRequests.add(chunkRequest);
		}

		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		int int5 = byteBuffer.getInt();
		int int6 = byteBuffer.getInt();
		for (int int7 = 0; int7 < this.pendingRequests.size(); ++int7) {
			WorldStreamer.ChunkRequest chunkRequest2 = (WorldStreamer.ChunkRequest)this.pendingRequests.get(int7);
			if ((chunkRequest2.flagsWS & 1) != 0) {
				this.pendingRequests.remove(int7--);
				chunkRequest2.flagsUDP |= 16;
			} else if (chunkRequest2.requestNumber == int1) {
				if (chunkRequest2.bb == null) {
					chunkRequest2.bb = this.getByteBuffer(int4);
				}

				System.arraycopy(byteBuffer.array(), byteBuffer.position(), chunkRequest2.bb.array(), int5, int6);
				if (int3 == int2 - 1) {
					if (this.NetworkFileDebug) {
						DebugLog.log(DebugType.NetworkFileDebug, "received all parts for " + chunkRequest2.chunk.wx + "," + chunkRequest2.chunk.wy);
					}

					chunkRequest2.bb.position(int4);
					this.pendingRequests.remove(int7);
					chunkRequest2.flagsUDP |= 16;
					if (this.requestingLargeArea) {
						++this.largeAreaDownloads;
					}
				}

				break;
			}
		}
	}

	public void receiveNotRequired(ByteBuffer byteBuffer) {
		for (WorldStreamer.ChunkRequest chunkRequest = (WorldStreamer.ChunkRequest)this.sentRequests.poll(); chunkRequest != null; chunkRequest = (WorldStreamer.ChunkRequest)this.sentRequests.poll()) {
			this.pendingRequests.add(chunkRequest);
		}

		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			int int3 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			for (int int4 = 0; int4 < this.pendingRequests.size(); ++int4) {
				WorldStreamer.ChunkRequest chunkRequest2 = (WorldStreamer.ChunkRequest)this.pendingRequests.get(int4);
				if ((chunkRequest2.flagsWS & 1) != 0) {
					this.pendingRequests.remove(int4--);
					chunkRequest2.flagsUDP |= 16;
				} else if (chunkRequest2.requestNumber == int3) {
					if (this.NetworkFileDebug) {
						DebugLog.log(DebugType.NetworkFileDebug, "NotRequiredInZip " + chunkRequest2.chunk.wx + "," + chunkRequest2.chunk.wy + " delete=" + !boolean1);
					}

					if (!boolean1) {
						chunkRequest2.flagsUDP |= 4;
					}

					this.pendingRequests.remove(int4);
					chunkRequest2.flagsUDP |= 16;
					if (this.requestingLargeArea) {
						++this.largeAreaDownloads;
					}

					break;
				}
			}
		}
	}

	private void compare(WorldStreamer.ChunkRequest chunkRequest, ByteBuffer byteBuffer, File file) throws IOException {
		IsoChunk chunk = (IsoChunk)IsoChunkMap.chunkStore.poll();
		if (chunk == null) {
			chunk = new IsoChunk(IsoWorld.instance.getCell());
		}

		chunk.wx = chunkRequest.chunk.wx;
		chunk.wy = chunkRequest.chunk.wy;
		IsoChunk chunk2 = (IsoChunk)IsoChunkMap.chunkStore.poll();
		if (chunk2 == null) {
			chunk2 = new IsoChunk(IsoWorld.instance.getCell());
		}

		chunk2.wx = chunkRequest.chunk.wx;
		chunk2.wy = chunkRequest.chunk.wy;
		int int1 = byteBuffer.position();
		byteBuffer.position(0);
		chunk.LoadFromBuffer(chunkRequest.chunk.wx, chunkRequest.chunk.wy, byteBuffer);
		byteBuffer.position(int1);
		this.crc32.reset();
		this.crc32.update(byteBuffer.array(), 0, int1);
		long long1 = this.crc32.getValue();
		DebugLog.log("downloaded crc=" + long1 + " on-disk crc=" + ChunkChecksum.getChecksumIfExists(chunkRequest.chunk.wx, chunkRequest.chunk.wy));
		chunk2.LoadFromDisk();
		DebugLog.log("downloaded size=" + int1 + " on-disk size=" + file.length());
		this.compareChunks(chunk, chunk2);
		chunk.resetForStore();
		assert !IsoChunkMap.chunkStore.contains(chunk);
		IsoChunkMap.chunkStore.add(chunk);
		chunk2.resetForStore();
		assert !IsoChunkMap.chunkStore.contains(chunk2);
		IsoChunkMap.chunkStore.add(chunk2);
	}

	private void compareChunks(IsoChunk chunk, IsoChunk chunk2) {
		DebugLog.log("comparing " + chunk.wx + "," + chunk.wy);
		try {
			this.compareErosion(chunk, chunk2);
			if (chunk.lootRespawnHour != chunk2.lootRespawnHour) {
				DebugLog.log("lootRespawnHour " + chunk.lootRespawnHour + " != " + chunk2.lootRespawnHour);
			}

			for (int int1 = 0; int1 < 10; ++int1) {
				for (int int2 = 0; int2 < 10; ++int2) {
					IsoGridSquare square = chunk.getGridSquare(int2, int1, 0);
					IsoGridSquare square2 = chunk2.getGridSquare(int2, int1, 0);
					this.compareSquares(square, square2);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void compareErosion(IsoChunk chunk, IsoChunk chunk2) {
		if (chunk.getErosionData().init != chunk2.getErosionData().init) {
			boolean boolean1 = chunk.getErosionData().init;
			DebugLog.log("init " + boolean1 + " != " + chunk2.getErosionData().init);
		}

		int int1;
		if (chunk.getErosionData().eTickStamp != chunk2.getErosionData().eTickStamp) {
			int1 = chunk.getErosionData().eTickStamp;
			DebugLog.log("eTickStamp " + int1 + " != " + chunk2.getErosionData().eTickStamp);
		}

		float float1;
		if (chunk.getErosionData().moisture != chunk2.getErosionData().moisture) {
			float1 = chunk.getErosionData().moisture;
			DebugLog.log("moisture " + float1 + " != " + chunk2.getErosionData().moisture);
		}

		if (chunk.getErosionData().minerals != chunk2.getErosionData().minerals) {
			float1 = chunk.getErosionData().minerals;
			DebugLog.log("minerals " + float1 + " != " + chunk2.getErosionData().minerals);
		}

		if (chunk.getErosionData().epoch != chunk2.getErosionData().epoch) {
			int1 = chunk.getErosionData().epoch;
			DebugLog.log("epoch " + int1 + " != " + chunk2.getErosionData().epoch);
		}

		if (chunk.getErosionData().soil != chunk2.getErosionData().soil) {
			int1 = chunk.getErosionData().soil;
			DebugLog.log("soil " + int1 + " != " + chunk2.getErosionData().soil);
		}
	}

	private void compareSquares(IsoGridSquare square, IsoGridSquare square2) {
		if (square != null && square2 != null) {
			try {
				this.bb1.clear();
				square.save(this.bb1, (ObjectOutputStream)null);
				this.bb1.flip();
				this.bb2.clear();
				square2.save(this.bb2, (ObjectOutputStream)null);
				this.bb2.flip();
				if (this.bb1.compareTo(this.bb2) != 0) {
					boolean boolean1 = true;
					int int1 = -1;
					int int2;
					if (this.bb1.limit() == this.bb2.limit()) {
						for (int2 = 0; int2 < this.bb1.limit(); ++int2) {
							if (this.bb1.get(int2) != this.bb2.get(int2)) {
								int1 = int2;
								break;
							}
						}

						for (int2 = 0; int2 < square.getErosionData().regions.size(); ++int2) {
							if (((ErosionCategory.Data)square.getErosionData().regions.get(int2)).dispSeason != ((ErosionCategory.Data)square2.getErosionData().regions.get(int2)).dispSeason) {
								int int3 = ((ErosionCategory.Data)square.getErosionData().regions.get(int2)).dispSeason;
								DebugLog.log("season1=" + int3 + " season2=" + ((ErosionCategory.Data)square2.getErosionData().regions.get(int2)).dispSeason);
								boolean1 = false;
							}
						}
					}

					DebugLog.log("square " + square.x + "," + square.y + " mismatch at " + int1 + " seasonMatch=" + boolean1 + " #regions=" + square.getErosionData().regions.size());
					IsoObject object;
					String string;
					if (square.getObjects().size() == square2.getObjects().size()) {
						for (int2 = 0; int2 < square.getObjects().size(); ++int2) {
							object = (IsoObject)square.getObjects().get(int2);
							IsoObject object2 = (IsoObject)square2.getObjects().get(int2);
							this.bb1.clear();
							object.save(this.bb1);
							this.bb1.flip();
							this.bb2.clear();
							object2.save(this.bb2);
							this.bb2.flip();
							if (this.bb1.compareTo(this.bb2) != 0) {
								string = object.getClass().getName();
								DebugLog.log("  1: " + string + " " + object.getName() + " " + (object.sprite == null ? "no sprite" : object.sprite.name));
								string = object2.getClass().getName();
								DebugLog.log("  2: " + string + " " + object2.getName() + " " + (object2.sprite == null ? "no sprite" : object2.sprite.name));
							}
						}
					} else {
						for (int2 = 0; int2 < square.getObjects().size(); ++int2) {
							object = (IsoObject)square.getObjects().get(int2);
							string = object.getClass().getName();
							DebugLog.log("  " + string + " " + object.getName() + " " + (object.sprite == null ? "no sprite" : object.sprite.name));
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			if (square != null || square2 != null) {
				DebugLog.log("one square is null, the other isn\'t");
			}
		}
	}

	private static final class ChunkRequest {
		static final ArrayDeque pool = new ArrayDeque();
		IsoChunk chunk;
		int requestNumber;
		long crc;
		ByteBuffer bb;
		transient int flagsMain;
		transient int flagsUDP;
		transient int flagsWS;
		long time;
		WorldStreamer.ChunkRequest next;

		static WorldStreamer.ChunkRequest alloc() {
			return pool.isEmpty() ? new WorldStreamer.ChunkRequest() : (WorldStreamer.ChunkRequest)pool.pop();
		}

		static void release(WorldStreamer.ChunkRequest chunkRequest) {
			chunkRequest.chunk = null;
			chunkRequest.bb = null;
			chunkRequest.flagsMain = 0;
			chunkRequest.flagsUDP = 0;
			chunkRequest.flagsWS = 0;
			pool.push(chunkRequest);
		}
	}

	private static class ChunkComparator implements Comparator {
		private Vector2[] pos = new Vector2[4];

		public ChunkComparator() {
			for (int int1 = 0; int1 < 4; ++int1) {
				this.pos[int1] = new Vector2();
			}
		}

		public void init() {
			for (int int1 = 0; int1 < 4; ++int1) {
				Vector2 vector2 = this.pos[int1];
				vector2.x = vector2.y = -1.0F;
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null) {
					if (player.lx == player.x && player.ly == player.y) {
						vector2.x = player.x;
						vector2.y = player.y;
					} else {
						vector2.x = player.x - player.lx;
						vector2.y = player.y - player.ly;
						vector2.normalize();
						vector2.setLength(10.0F);
						vector2.x += player.x;
						vector2.y += player.y;
					}
				}
			}
		}

		public int compare(IsoChunk chunk, IsoChunk chunk2) {
			float float1 = Float.MAX_VALUE;
			float float2 = Float.MAX_VALUE;
			for (int int1 = 0; int1 < 4; ++int1) {
				if (this.pos[int1].x != -1.0F || this.pos[int1].y != -1.0F) {
					float float3 = this.pos[int1].x;
					float float4 = this.pos[int1].y;
					float1 = Math.min(float1, IsoUtils.DistanceTo(float3, float4, (float)(chunk.wx * 10 + 5), (float)(chunk.wy * 10 + 5)));
					float2 = Math.min(float2, IsoUtils.DistanceTo(float3, float4, (float)(chunk2.wx * 10 + 5), (float)(chunk2.wy * 10 + 5)));
				}
			}

			if (float1 < float2) {
				return 1;
			} else if (float1 > float2) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
