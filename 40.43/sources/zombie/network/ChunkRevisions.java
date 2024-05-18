package zombie.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.iso.CellLoader;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLot;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LotHeader;
import zombie.iso.RoomDef;
import zombie.iso.SliceY;
import zombie.vehicles.BaseVehicle;


public class ChunkRevisions {
	public static boolean USE_CHUNK_REVISIONS = false;
	public static ChunkRevisions instance;
	public Object FileLock = new Object();
	public static int UpdateArea = 30;
	static boolean debug = true;
	static byte[] CTBL = new byte[]{67, 84, 66, 76};
	static byte[] SQRE = new byte[]{83, 81, 82, 69};
	static byte[] BEEF = new byte[]{66, 69, 69, 70};
	static byte[] CARS = new byte[]{67, 65, 82, 83};
	public ChunkRevisions.Chunk[][] chunks;
	public int minX;
	public int minY;
	public int width;
	public int height;
	public ArrayList revisedSquares = new ArrayList();
	public ArrayList revisedSquares2 = new ArrayList();
	public ArrayList revisedChunks = new ArrayList();
	public int[] lastRequestX = new int[4];
	public int[] lastRequestY = new int[4];
	public IsoPlayer[] lastRequestInit = new IsoPlayer[4];
	private ArrayList deadPatchJobs = new ArrayList();
	public OnceEvery updateTimer = new OnceEvery(2.0F);
	public ExecutorService executor = Executors.newCachedThreadPool();
	public ChunkRevisions.ClientChunkRevisionRequest clientChunkRequest = null;
	private IsoPlayer[] AddCoopPlayerRequests = new IsoPlayer[4];
	public ArrayList serverChunkRequest = new ArrayList();

	private static void noise(String string) {
		DebugLog.log("CHUNK: " + string);
	}

	static void checkBytes(ByteBuffer byteBuffer, byte[] byteArray) {
		if (debug) {
			for (int int1 = 0; int1 < byteArray.length; ++int1) {
				byte byte1 = byteBuffer.get();
				if (byte1 != byteArray[int1] && int1 == 0) {
					noise("bytes don\'t match");
				}
			}
		}
	}

	public ChunkRevisions() {
		this.minX = IsoWorld.instance.MetaGrid.getMinX() * 30;
		this.minY = IsoWorld.instance.MetaGrid.getMinY() * 30;
		this.width = IsoWorld.instance.MetaGrid.getWidth() * 30;
		this.height = IsoWorld.instance.MetaGrid.getHeight() * 30;
		this.chunks = new ChunkRevisions.Chunk[this.width][this.height];
		for (int int1 = 0; int1 < this.height; ++int1) {
			for (int int2 = 0; int2 < this.width; ++int2) {
				this.chunks[int2][int1] = new ChunkRevisions.Chunk(this.minX + int2, this.minY + int1);
			}
		}

		ChunkRevisions.MemoryFile.test();
	}

	public ChunkRevisions.Chunk getChunk(int int1, int int2) {
		int int3 = int1 - this.minX;
		int int4 = int2 - this.minY;
		return int3 >= 0 && int3 < this.width && int4 >= 0 && int4 < this.height ? this.chunks[int3][int4] : null;
	}

	public void revisionUp(IsoGridSquare square) {
		if (GameServer.bServer) {
			if (square.chunk != null) {
				noise("square " + square.getX() + "," + square.getY() + "," + square.getZ() + " revision " + square.revision + " -> " + (square.chunk.revision + 1L));
				square.revision = ++square.chunk.revision;
				if (!this.revisedSquares.contains(square)) {
					this.revisedSquares.add(square);
				}
			}
		}
	}

	public void clientPacket(short short1, ByteBuffer byteBuffer) {
		switch (short1) {
		case 3: 
			byte byte1 = byteBuffer.get();
			if (byte1 == 0) {
				short short2 = byteBuffer.getShort();
				int int1;
				for (int1 = 0; int1 < short2; ++int1) {
					short short3 = byteBuffer.getShort();
					short short4 = byteBuffer.getShort();
					ChunkRevisions.Chunk chunk = this.getChunk(short3, short4);
					if (chunk != null) {
						++chunk.serverUpdates;
					}
				}

				if (this.clientChunkRequest == null) {
					this.clientChunkRequest = new ChunkRevisions.ClientChunkRevisionRequest();
					for (int1 = 0; int1 < 4; ++int1) {
						IsoPlayer player = IsoPlayer.players[int1];
						if (player != null) {
							this.clientChunkRequest.setArea(int1, (int)player.getX() / 10 - UpdateArea / 2, (int)player.getY() / 10 - UpdateArea / 2, UpdateArea, UpdateArea);
						}
					}

					this.clientChunkRequest.start();
				}
			} else if (byte1 == 1) {
				if (this.clientChunkRequest != null) {
					this.clientChunkRequest.acknowledge();
				}
			} else if (byte1 == 2 && this.clientChunkRequest != null) {
				this.clientChunkRequest.emptyZip();
			}

		
		default: 
		
		}
	}

	public void serverPacket(short short1, ByteBuffer byteBuffer, UdpConnection udpConnection) {
		switch (short1) {
		case 3: 
			this.receiveChunkRevisionRequest(byteBuffer, udpConnection);
		
		default: 
		
		}
	}

	public void updateClient() {
		int int1;
		if (this.clientChunkRequest != null) {
			int1 = this.clientChunkRequest.coopRequest;
			if (!this.clientChunkRequest.isFailed() && !this.clientChunkRequest.isFinished()) {
				if (this.clientChunkRequest.state == ChunkRevisions.ClientChunkRevisionRequest.State.RUNTHREAD && System.currentTimeMillis() - this.clientChunkRequest.threadStartTime > 200L) {
					noise("request thread start() glitch??? state=" + this.clientChunkRequest.state);
					this.clientChunkRequest = null;
				} else if (this.clientChunkRequest.thread != null && this.clientChunkRequest.thread.getState() == java.lang.Thread.State.TERMINATED) {
					noise("request thread terminated??? state=" + this.clientChunkRequest.state);
					this.clientChunkRequest = null;
				}
			} else {
				this.clientChunkRequest = null;
			}

			if (this.clientChunkRequest == null && int1 != -1) {
				noise("finished coop request player=" + int1 + "/" + 4);
				this.AddCoopPlayerRequests[int1] = null;
			}
		}

		IsoPlayer player;
		int int2;
		int int3;
		if (this.clientChunkRequest == null) {
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				player = IsoPlayer.players[int1];
				if (player != null) {
					int2 = (int)player.getX() / 10;
					int3 = (int)player.getY() / 10;
					if (this.lastRequestInit[int1] != player) {
						this.lastRequestInit[int1] = player;
						this.lastRequestX[int1] = int2;
						this.lastRequestY[int1] = int3;
					}

					if (int2 != this.lastRequestX[int1] || int3 != this.lastRequestY[int1]) {
						if (this.clientChunkRequest == null) {
							this.clientChunkRequest = new ChunkRevisions.ClientChunkRevisionRequest();
						}

						this.clientChunkRequest.setArea(int1, int2 - UpdateArea / 2, int3 - UpdateArea / 2, UpdateArea, UpdateArea);
						this.lastRequestX[int1] = int2;
						this.lastRequestY[int1] = int3;
					}
				}
			}

			if (this.clientChunkRequest != null) {
				this.clientChunkRequest.start();
			}
		}

		if (this.clientChunkRequest == null) {
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				player = this.AddCoopPlayerRequests[int1];
				if (player != null) {
					int2 = (int)player.getX() / 10;
					int3 = (int)player.getY() / 10;
					noise("starting coop request player=" + int1 + "/" + 4);
					this.clientChunkRequest = new ChunkRevisions.ClientChunkRevisionRequest(int1, int2 - UpdateArea / 2, int3 - UpdateArea / 2, UpdateArea, UpdateArea);
					this.clientChunkRequest.coopRequest = int1;
					this.clientChunkRequest.start();
					break;
				}
			}
		}
	}

	public void updateServer() {
		for (int int1 = 0; int1 < this.serverChunkRequest.size(); ++int1) {
			ChunkRevisions.ServerChunkRevisionRequest serverChunkRevisionRequest = (ChunkRevisions.ServerChunkRevisionRequest)this.serverChunkRequest.get(int1);
			if (!serverChunkRevisionRequest.isFailed() && !serverChunkRevisionRequest.isFinished()) {
				if (serverChunkRevisionRequest.state == ChunkRevisions.ServerChunkRevisionRequest.State.RUNTHREAD && System.currentTimeMillis() - serverChunkRevisionRequest.threadStartTime > 200L) {
					noise("request thread start() glitch??? state=" + serverChunkRevisionRequest.state);
					ChunkRevisions.ServerChunkRevisionRequestInfo.release(serverChunkRevisionRequest.chunks);
					this.serverChunkRequest.remove(int1--);
				} else if (serverChunkRevisionRequest.thread != null && serverChunkRevisionRequest.thread.getState() == java.lang.Thread.State.TERMINATED) {
					noise("request thread terminated??? state=" + serverChunkRevisionRequest.state);
					ChunkRevisions.ServerChunkRevisionRequestInfo.release(serverChunkRevisionRequest.chunks);
					this.serverChunkRequest.remove(int1--);
				}
			} else {
				ChunkRevisions.ServerChunkRevisionRequestInfo.release(serverChunkRevisionRequest.chunks);
				this.serverChunkRequest.remove(int1--);
			}
		}

		if (this.updateTimer.Check()) {
			this.processRevisedSquares();
		}
	}

	public void patchChunkIfNeeded(IsoChunk chunk) {
		if (GameClient.bClient) {
			if (System.currentTimeMillis() - chunk.modificationTime >= 20000L) {
				ChunkRevisions.Chunk chunk2 = this.getChunk(chunk.wx, chunk.wy);
				if (chunk2 != null) {
					if (chunk2.patchJob != null) {
						if (chunk2.patchJob.status == -1) {
							chunk2.patchJob.release();
							chunk2.patchJob = null;
						} else if (chunk2.patchJob.status == 1) {
							chunk2.patchJob.patch();
							chunk2.patchJob.release();
							chunk2.patchJob = null;
						}
					} else {
						if (chunk.revision < chunk2.patchRevision) {
							chunk2.patchJob = new ChunkRevisions.PatchJob(chunk);
							instance.executor.submit(chunk2.patchJob);
						}
					}
				}
			}
		}
	}

	public void chunkRemovedFromWorld(IsoChunk chunk) {
		ChunkRevisions.Chunk chunk2 = this.getChunk(chunk.wx, chunk.wy);
		if (chunk2 != null) {
			if (chunk2.patchJob != null) {
				if (chunk2.patchJob.status != 0) {
					chunk2.patchJob.release();
				} else {
					this.deadPatchJobs.add(chunk2.patchJob);
				}

				chunk2.patchJob = null;
			}
		}
	}

	public void processRevisedSquares() {
		if (!this.revisedSquares.isEmpty()) {
			this.revisedChunks.clear();
			while (!this.revisedSquares.isEmpty()) {
				IsoGridSquare square = (IsoGridSquare)this.revisedSquares.get(0);
				this.revisedSquares2.clear();
				for (int int1 = 0; int1 < this.revisedSquares.size(); ++int1) {
					IsoGridSquare square2 = (IsoGridSquare)this.revisedSquares.get(int1);
					if (square2.chunk == square.chunk) {
						this.revisedSquares2.add(square2);
						this.revisedSquares.remove(int1--);
					}
				}

				noise(this.revisedSquares2.size() + " squares revised @ " + square.chunk.wx + "," + square.chunk.wy);
				ChunkRevisions.ChunkRevisionFile chunkRevisionFile = new ChunkRevisions.ChunkRevisionFile(square.chunk.wx, square.chunk.wy);
				if (chunkRevisionFile.addSquares(this.revisedSquares2)) {
					this.revisedChunks.add(square.chunk);
				}

				ChunkRevisions.Chunk chunk = this.getChunk(square.chunk.wx, square.chunk.wy);
				if (chunk != null) {
					synchronized (chunk) {
						chunk.patchRevision = -1L;
					}
				}
			}

			this.revisedSquares.clear();
			for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int2);
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)3, byteBufferWriter);
				byteBufferWriter.putByte((byte)0);
				byteBufferWriter.putShort((short)this.revisedChunks.size());
				for (int int3 = 0; int3 < this.revisedChunks.size(); ++int3) {
					byteBufferWriter.putShort((short)((IsoChunk)this.revisedChunks.get(int3)).wx);
					byteBufferWriter.putShort((short)((IsoChunk)this.revisedChunks.get(int3)).wy);
				}

				udpConnection.endPacket();
			}
		}
	}

	public void loadChunkRevision(int int1, int int2) {
		ByteBuffer byteBuffer = ChunkRevisions.Buffers.get();
		try {
			ChunkRevisions.Chunk chunk = this.getChunk(int1, int2);
			if (chunk == null) {
				return;
			}

			synchronized (chunk) {
				chunk.chunkRandomID = 0;
				IsoChunk chunk2;
				if (GameServer.bServer) {
					chunk2 = ServerMap.instance.getChunk(int1, int2);
					if (chunk2 != null) {
						chunk.chunkRandomID = chunk2.randomID;
					}
				} else {
					IsoChunkMap.bSettingChunk.lock();
					chunk2 = IsoWorld.instance.CurrentCell.getChunkForGridSquare(int1 * 10, int2 * 10, 0);
					if (chunk2 != null) {
						chunk.chunkRandomID = chunk2.randomID;
					}

					IsoChunkMap.bSettingChunk.unlock();
				}

				if (chunk.fileRevision == -1L) {
					ChunkRevisions.ChunkFile chunkFile = new ChunkRevisions.ChunkFile(int1, int2);
					if (chunkFile.loadChunkRevision(byteBuffer)) {
						chunk.fileRandomID = chunkFile.randomID;
						chunk.fileRevision = chunkFile.revision;
					} else {
						chunk.fileRandomID = 0;
						chunk.fileRevision = -1L;
					}
				}

				if (chunk.patchRevision == -1L) {
					ChunkRevisions.ChunkRevisionFile chunkRevisionFile = new ChunkRevisions.ChunkRevisionFile(int1, int2);
					if (chunkRevisionFile.loadChunkRevision(byteBuffer)) {
						chunk.patchRandomID = chunkRevisionFile.randomID;
						chunk.patchRevision = chunkRevisionFile.revision;
					} else {
						chunk.patchRandomID = 0;
						chunk.patchRevision = -1L;
					}
				}
			}
		} finally {
			ChunkRevisions.Buffers.release(byteBuffer);
		}
	}

	public void requestStartupChunkRevisions(int int1, int int2, int int3, int int4) {
		if (this.clientChunkRequest != null) {
			noise("already have a request");
		} else {
			this.clientChunkRequest = new ChunkRevisions.ClientChunkRevisionRequest(0, int1, int2, int3, int4);
			this.clientChunkRequest.timeout = 20000;
			this.clientChunkRequest.start();
			while (!this.clientChunkRequest.isFailed() && !this.clientChunkRequest.isFinished()) {
				try {
					Thread.sleep(200L);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}

			this.clientChunkRequest = null;
		}
	}

	public void requestCoopStartupChunkRevisions(IsoPlayer player) {
		this.AddCoopPlayerRequests[player.PlayerIndex] = player;
	}

	public boolean isCoopRequestComplete(IsoPlayer player) {
		return this.AddCoopPlayerRequests[player.PlayerIndex] == null;
	}

	public void receiveChunkRevisionRequest(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		try {
			ArrayList arrayList = new ArrayList();
			for (int int1 = 0; int1 < this.serverChunkRequest.size(); ++int1) {
				if (((ChunkRevisions.ServerChunkRevisionRequest)this.serverChunkRequest.get(int1)).connection == udpConnection) {
					noise(udpConnection.username + " request ignored because another request exists for this client");
					return;
				}
			}

			short short1 = byteBuffer.getShort();
			for (int int2 = 0; int2 < short1; ++int2) {
				short short2 = byteBuffer.getShort();
				short short3 = byteBuffer.getShort();
				int int3 = byteBuffer.getInt();
				long long1 = byteBuffer.getLong();
				ChunkRevisions.ServerChunkRevisionRequestInfo serverChunkRevisionRequestInfo = ChunkRevisions.ServerChunkRevisionRequestInfo.get();
				serverChunkRevisionRequestInfo.wx = short2;
				serverChunkRevisionRequestInfo.wy = short3;
				serverChunkRevisionRequestInfo.randomID = int3;
				serverChunkRevisionRequestInfo.revision = long1;
				arrayList.add(serverChunkRevisionRequestInfo);
			}

			noise(udpConnection.username + " requested " + arrayList.size() + " chunks to be checked (" + byteBuffer.position() + " bytes)");
			this.serverChunkRequest.add(new ChunkRevisions.ServerChunkRevisionRequest(udpConnection, arrayList));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static class MemoryFile {
		private static final int BLOCK_SIZE = 1024;
		private byte[] buf;
		private int position;
		private int size;

		private MemoryFile() {
		}

		public ChunkRevisions.MemoryFile position(int int1) {
			if (int1 <= this.size && int1 >= 0) {
				this.position = int1;
				return this;
			} else {
				throw new IllegalArgumentException();
			}
		}

		public int read(byte[] byteArray, int int1, int int2) {
			int int3 = this.bytesToRead(this.position, int2);
			if (int3 == -1) {
				return int3;
			} else {
				System.arraycopy(this.buf, this.position, byteArray, int1, int3);
				return int3;
			}
		}

		public int write(byte[] byteArray, int int1, int int2) {
			this.resize(this.position + int2);
			System.arraycopy(byteArray, int1, this.buf, this.position, int2);
			this.size = Math.max(this.size, this.position + int2);
			this.position += int2;
			return int2;
		}

		public int length() {
			return this.size;
		}

		private void resize(int int1) {
			int int2 = 1024 * ((int1 + 1024 - 1) / 1024);
			if (this.buf == null || int1 > this.buf.length) {
				byte[] byteArray = new byte[int2];
				if (this.buf != null) {
					System.arraycopy(this.buf, 0, byteArray, 0, this.size);
				}

				this.buf = byteArray;
			}
		}

		private int bytesToRead(int int1, int int2) {
			int int3 = this.size - int1;
			return int3 <= 0 ? -1 : Math.min(int3, int2);
		}

		public static void test() {
			ChunkRevisions.MemoryFile memoryFile = new ChunkRevisions.MemoryFile();
			ChunkRevisions.MemoryFile memoryFile2 = new ChunkRevisions.MemoryFile();
			ByteBuffer byteBuffer = ChunkRevisions.Buffers.get();
			byteBuffer.rewind();
			byteBuffer.putInt(1234);
			byteBuffer.putInt(5678);
			memoryFile.write(byteBuffer.array(), 0, byteBuffer.position());
			ChunkRevisions.Buffers.release(byteBuffer);
			byteBuffer = ChunkRevisions.Buffers.get();
			byteBuffer.rewind();
			memoryFile.position(0);
			memoryFile.read(byteBuffer.array(), 0, byteBuffer.limit());
			memoryFile2.write(byteBuffer.array(), 4, 4);
			memoryFile2.write(byteBuffer.array(), 0, 4);
			memoryFile2.position(0);
			memoryFile2.read(byteBuffer.array(), 0, byteBuffer.limit());
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			ChunkRevisions.Buffers.release(byteBuffer);
		}

		MemoryFile(Object object) {
			this();
		}
	}

	private static class PatchJob implements Runnable {
		public int status = 0;
		public IsoChunk chunk;
		public IsoChunk resetChunk;
		public ByteBuffer bb;
		public ChunkRevisions.ChunkTable chunkTable;
		public ChunkRevisions.ChunkRevisionFile revFile;
		public short worldVersion;

		public PatchJob(IsoChunk chunk) {
			this.chunk = chunk;
		}

		public void release() {
			if (this.chunkTable != null) {
				ChunkRevisions.ChunkTableEntry.release(this.chunkTable.entries);
				ChunkRevisions.ChunkTable.release(this.chunkTable);
				this.chunkTable = null;
			}

			if (this.bb != null) {
				ChunkRevisions.Buffers.release(this.bb);
				this.bb = null;
			}
		}

		private void fail() {
			this.status = -1;
		}

		private void succeed() {
			this.status = 1;
		}

		public void run() {
			this.bb = ChunkRevisions.Buffers.get();
			this.chunkTable = ChunkRevisions.ChunkTable.get();
			this.revFile = new ChunkRevisions.ChunkRevisionFile(this.chunk.wx, this.chunk.wy);
			try {
				if (this.revFile.read(this.bb.array()) == -1) {
					this.fail();
					return;
				}

				this.bb.rewind();
				this.worldVersion = this.bb.getShort();
				this.revFile.randomID = this.bb.getInt();
				this.revFile.revision = this.bb.getLong();
				if (!this.chunkTable.read(this.bb)) {
					this.fail();
					return;
				}

				if (this.revFile.randomID != this.chunk.randomID) {
					this.resetChunk = new IsoChunk(IsoWorld.instance.CurrentCell);
					if (!CellLoader.LoadCellBinaryChunk(IsoWorld.instance.CurrentCell, this.chunk.wx, this.chunk.wy, this.resetChunk)) {
						this.fail();
						return;
					}
				}

				this.succeed();
			} catch (Exception exception) {
				exception.printStackTrace();
				this.fail();
			}
		}

		public boolean patch() {
			this.status = 2;
			try {
				if (GameClient.bClient && this.chunk.randomID == 0) {
					this.chunk.randomID = this.revFile.randomID;
				}

				int int1;
				if (this.resetChunk != null) {
					ChunkRevisions.noise("randomID mismatch, resetting chunk " + this.chunk.wx + "," + this.chunk.wy + " before patching (was " + this.chunk.randomID + " now " + this.revFile.randomID + ")");
					this.chunk.randomID = this.revFile.randomID;
					IsoChunkMap.bSettingChunk.lock();
					try {
						this.chunk.removeFromWorld();
						this.chunk.doReuseGridsquares();
						this.chunk.getErosionData().init = false;
						for (int1 = 0; int1 < 8; ++int1) {
							for (int int2 = 0; int2 < 10; ++int2) {
								for (int int3 = 0; int3 < 10; ++int3) {
									this.chunk.setSquare(int3, int2, int1, this.resetChunk.getGridSquare(int3, int2, int1));
								}
							}
						}

						this.chunk.setCacheIncludingNull();
						this.chunk.updateBuildings();
						this.chunk.recalcNeighboursNow();
						this.chunk.doLoadGridsquare();
					} finally {
						IsoChunkMap.bSettingChunk.unlock();
					}
				}

				ChunkRevisions.noise("patching " + this.chunk.wx + "," + this.chunk.wy + " randomID=" + this.chunk.randomID + " from revision " + this.chunk.revision + " to " + this.revFile.revision + " with " + this.chunkTable.entries.size() + " squares");
				ChunkRevisions.ChunkTableEntry chunkTableEntry;
				IsoGridSquare square;
				try {
					for (int1 = 0; int1 < this.chunkTable.entries.size(); ++int1) {
						chunkTableEntry = (ChunkRevisions.ChunkTableEntry)this.chunkTable.entries.get(int1);
						square = this.chunk.getGridSquare(chunkTableEntry.x, chunkTableEntry.y, chunkTableEntry.z);
						int int4;
						IsoObject object;
						if (square == null) {
							square = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, (SliceY)null, this.chunk.wx * 10 + chunkTableEntry.x, this.chunk.wy * 10 + chunkTableEntry.y, chunkTableEntry.z);
							square.chunk = this.chunk;
						} else {
							for (int4 = square.getObjects().size() - 1; int4 >= 0; --int4) {
								object = (IsoObject)square.getObjects().get(int4);
								object.removeFromWorld();
								object.removeFromSquare();
							}

							for (int4 = square.getStaticMovingObjects().size() - 1; int4 >= 0; --int4) {
								IsoMovingObject movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(int4);
								movingObject.removeFromWorld();
								movingObject.removeFromSquare();
							}
						}

						square.revision = chunkTableEntry.revision;
						if (this.chunk.lotheader != null) {
							RoomDef roomDef = IsoWorld.instance.getMetaChunkFromTile(square.x, square.y).getRoomAt(square.x, square.y, square.z);
							int int5 = roomDef != null ? roomDef.ID : -1;
							square.setRoomID(int5);
						}

						square.ResetMasterRegion();
						ChunkRevisions.checkBytes(this.bb, ChunkRevisions.SQRE);
						square.load(this.bb, this.worldVersion);
						if (this.bb.position() != chunkTableEntry.position + chunkTableEntry.length) {
							ChunkRevisions.noise("***** square didn\'t read as much as it wrote");
						}

						this.chunk.setSquare(chunkTableEntry.x, chunkTableEntry.y, chunkTableEntry.z, square);
						for (int4 = 0; int4 < square.getObjects().size(); ++int4) {
							object = (IsoObject)square.getObjects().get(int4);
							object.addToWorld();
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
					return false;
				}

				this.chunk.revision = this.revFile.revision;
				for (int1 = 0; int1 < this.chunkTable.entries.size(); ++int1) {
					chunkTableEntry = (ChunkRevisions.ChunkTableEntry)this.chunkTable.entries.get(int1);
					square = this.chunk.getGridSquare(chunkTableEntry.x, chunkTableEntry.y, chunkTableEntry.z);
					square.RecalcAllWithNeighbours(true);
				}

				try {
					LuaEventManager.triggerEvent("OnContainerUpdate", this);
				} catch (Exception exception2) {
					exception2.printStackTrace();
				}

				return true;
			} catch (Exception exception3) {
				exception3.printStackTrace();
				return false;
			}
		}
	}

	public static class ChunkRevisionFile {
		public int wx;
		public int wy;
		public String fileName;
		public ChunkRevisions.MemoryFile memoryFile;
		public int randomID;
		public long revision;

		public ChunkRevisionFile(int int1, int int2) {
			this.wx = int1;
			this.wy = int2;
			this.fileName = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_" + int1 + "_" + int2 + "_rev.bin";
			if (GameServer.bServer) {
				ChunkRevisions.Chunk chunk = ChunkRevisions.instance.getChunk(int1, int2);
				if (chunk != null) {
					synchronized (chunk) {
						if (chunk.memoryFile == null) {
							chunk.memoryFile = new ChunkRevisions.MemoryFile();
						}

						this.memoryFile = chunk.memoryFile;
					}
				}
			}
		}

		public boolean exists() {
			synchronized (ChunkRevisions.instance.FileLock) {
				if (this.memoryFile != null) {
					return true;
				}
			}
			return (new File(this.fileName)).exists();
		}

		private int read(byte[] byteArray, int int1, int int2) {
			if (!this.exists()) {
				return -1;
			} else {
				int int3 = -1;
				if (this.memoryFile != null) {
					synchronized (ChunkRevisions.instance.FileLock) {
						try {
							this.memoryFile.position(0);
							int3 = this.memoryFile.read(byteArray, int1, int2);
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						return int3;
					}
				} else {
					synchronized (ChunkRevisions.instance.FileLock) {
						try {
							FileInputStream fileInputStream = new FileInputStream(this.fileName);
							int3 = fileInputStream.read(byteArray, int1, int2);
							fileInputStream.close();
						} catch (Exception exception2) {
							exception2.printStackTrace();
						}

						return int3;
					}
				}
			}
		}

		private int read(byte[] byteArray) {
			return this.read(byteArray, 0, byteArray.length);
		}

		private boolean write(byte[] byteArray, int int1, int int2) {
			boolean boolean1;
			if (this.memoryFile != null) {
				synchronized (ChunkRevisions.instance.FileLock) {
					try {
						this.memoryFile.position(0);
						this.memoryFile.write(byteArray, int1, int2);
						boolean1 = true;
					} catch (Exception exception) {
						exception.printStackTrace();
						return false;
					}

					return boolean1;
				}
			} else {
				synchronized (ChunkRevisions.instance.FileLock) {
					try {
						FileOutputStream fileOutputStream = new FileOutputStream(this.fileName);
						fileOutputStream.write(byteArray, int1, int2);
						fileOutputStream.close();
						boolean1 = true;
					} catch (Exception exception2) {
						exception2.printStackTrace();
						return false;
					}

					return boolean1;
				}
			}
		}

		public boolean loadChunkRevision(ByteBuffer byteBuffer) {
			byteBuffer.rewind();
			if (this.read(byteBuffer.array(), 0, 128) == -1) {
				return false;
			} else {
				short short1 = byteBuffer.getShort();
				this.randomID = byteBuffer.getInt();
				this.revision = byteBuffer.getLong();
				return true;
			}
		}

		public ChunkRevisions.ChunkTable loadChunkTable(ByteBuffer byteBuffer, long long1) {
			byteBuffer.rewind();
			if (this.read(byteBuffer.array()) == -1) {
				return null;
			} else {
				short short1 = byteBuffer.getShort();
				int int1 = byteBuffer.getInt();
				long long2 = byteBuffer.getLong();
				ChunkRevisions.ChunkTable chunkTable = ChunkRevisions.ChunkTable.get();
				if (!chunkTable.read(byteBuffer, long1, long2)) {
					ChunkRevisions.ChunkTable.release(chunkTable);
					return null;
				} else {
					return chunkTable;
				}
			}
		}

		public boolean addNewRevisions(int int1, ByteBuffer byteBuffer) {
			ByteBuffer byteBuffer2 = ChunkRevisions.Buffers.get();
			ByteBuffer byteBuffer3 = ChunkRevisions.Buffers.get();
			ChunkRevisions.ChunkTable chunkTable = ChunkRevisions.ChunkTable.get();
			ChunkRevisions.ChunkTable chunkTable2 = ChunkRevisions.ChunkTable.get();
			ChunkRevisions.ChunkTable chunkTable3 = ChunkRevisions.ChunkTable.get();
			boolean boolean1;
			try {
				boolean boolean2 = true;
				short short1;
				int int2;
				if (this.read(byteBuffer2.array()) != -1) {
					byteBuffer2.rewind();
					short1 = byteBuffer2.getShort();
					int2 = byteBuffer2.getInt();
					boolean2 = short1 != 143 || int2 != int1;
				}

				byteBuffer2.rewind();
				if (boolean2) {
					byteBuffer2.putShort((short)143);
					byteBuffer2.putInt(int1);
					byteBuffer2.putLong(0L);
					if (ChunkRevisions.debug) {
						byteBuffer2.put(ChunkRevisions.CTBL);
					}

					byteBuffer2.putShort((short)0);
					byteBuffer2.rewind();
				}

				short1 = byteBuffer2.getShort();
				int2 = byteBuffer2.getInt();
				long long1 = byteBuffer2.getLong();
				if (chunkTable.read(byteBuffer2)) {
					if (!chunkTable2.read(byteBuffer)) {
						boolean1 = false;
						return boolean1;
					}

					chunkTable3.merge(chunkTable, chunkTable2);
					long long2 = chunkTable3.entries.isEmpty() ? 0L : ((ChunkRevisions.ChunkTableEntry)chunkTable3.entries.get(0)).revision;
					byteBuffer3.rewind();
					byteBuffer3.putShort(short1);
					byteBuffer3.putInt(int1);
					byteBuffer3.putLong(long2);
					chunkTable3.write(byteBuffer3);
					for (int int3 = 0; int3 < chunkTable3.entries.size(); ++int3) {
						ChunkRevisions.ChunkTableEntry chunkTableEntry = (ChunkRevisions.ChunkTableEntry)chunkTable3.entries.get(int3);
						if (chunkTable.entries.contains(chunkTableEntry)) {
							byteBuffer3.put(byteBuffer2.array(), chunkTableEntry.position, chunkTableEntry.length);
						} else {
							byteBuffer3.put(byteBuffer.array(), chunkTableEntry.position, chunkTableEntry.length);
						}
					}

					if (!this.write(byteBuffer3.array(), 0, byteBuffer3.position())) {
						boolean boolean3 = false;
						return boolean3;
					}

					ChunkRevisions.noise("patch-file " + this.wx + "," + this.wy + " randomID=" + int1 + " (was " + (boolean2 ? 0 : int2) + ") revision " + long1 + " -> " + long2 + ", #squares " + chunkTable.entries.size() + " -> " + chunkTable3.entries.size());
					this.randomID = int1;
					this.revision = long2;
					return true;
				}

				boolean1 = false;
			} finally {
				if (chunkTable != null) {
					ChunkRevisions.ChunkTableEntry.release(chunkTable.entries);
					ChunkRevisions.ChunkTable.release(chunkTable);
				}

				if (chunkTable2 != null) {
					ChunkRevisions.ChunkTableEntry.release(chunkTable2.entries);
					ChunkRevisions.ChunkTable.release(chunkTable2);
				}

				ChunkRevisions.ChunkTable.release(chunkTable3);
				ChunkRevisions.Buffers.release(byteBuffer2);
				ChunkRevisions.Buffers.release(byteBuffer3);
			}

			return boolean1;
		}

		public boolean addSquares(ArrayList arrayList) {
			ByteBuffer byteBuffer = ChunkRevisions.Buffers.get();
			ByteBuffer byteBuffer2 = ChunkRevisions.Buffers.get();
			ChunkRevisions.ChunkTable chunkTable = ChunkRevisions.ChunkTable.get();
			boolean boolean1;
			try {
				byteBuffer.rewind();
				int int1;
				for (int1 = 0; int1 < arrayList.size(); ++int1) {
					IsoGridSquare square = (IsoGridSquare)arrayList.get(int1);
					int int2 = byteBuffer.position();
					if (ChunkRevisions.debug) {
						byteBuffer.put(ChunkRevisions.SQRE);
					}

					square.save(byteBuffer, (ObjectOutputStream)null);
					ChunkRevisions.ChunkTableEntry chunkTableEntry = ChunkRevisions.ChunkTableEntry.get();
					chunkTableEntry.x = (byte)(square.x - square.chunk.wx * 10);
					chunkTableEntry.y = (byte)(square.y - square.chunk.wy * 10);
					chunkTableEntry.z = (byte)square.z;
					chunkTableEntry.position = int2;
					chunkTableEntry.length = (short)(byteBuffer.position() - int2);
					chunkTableEntry.revision = square.revision;
					chunkTable.entries.add(chunkTableEntry);
				}

				byteBuffer2.rewind();
				chunkTable.write(byteBuffer2);
				for (int1 = 0; int1 < chunkTable.entries.size(); ++int1) {
					ChunkRevisions.ChunkTableEntry chunkTableEntry2 = (ChunkRevisions.ChunkTableEntry)chunkTable.entries.get(int1);
					byteBuffer2.put(byteBuffer.array(), chunkTableEntry2.position, chunkTableEntry2.length);
				}

				byteBuffer2.rewind();
				boolean boolean2 = this.addNewRevisions(((IsoGridSquare)arrayList.get(0)).chunk.randomID, byteBuffer2);
				return boolean2;
			} catch (Exception exception) {
				exception.printStackTrace();
				boolean1 = false;
			} finally {
				ChunkRevisions.ChunkTableEntry.release(chunkTable.entries);
				ChunkRevisions.ChunkTable.release(chunkTable);
				ChunkRevisions.Buffers.release(byteBuffer);
				ChunkRevisions.Buffers.release(byteBuffer2);
			}

			return boolean1;
		}

		public boolean patchChunk(IsoChunk chunk) {
			if (!GameClient.bClient) {
				return true;
			} else if (!this.exists()) {
				return true;
			} else {
				ByteBuffer byteBuffer = ChunkRevisions.Buffers.get();
				ChunkRevisions.ChunkTable chunkTable = ChunkRevisions.ChunkTable.get();
				try {
					if (this.read(byteBuffer.array()) == -1) {
						boolean boolean1 = false;
						return boolean1;
					} else {
						byteBuffer.rewind();
						short short1 = byteBuffer.getShort();
						int int1 = byteBuffer.getInt();
						long long1 = byteBuffer.getLong();
						boolean boolean2;
						if (!chunkTable.read(byteBuffer)) {
							boolean2 = false;
							return boolean2;
						} else {
							if (GameClient.bClient && chunk.randomID == 0) {
								chunk.randomID = int1;
							}

							int int2;
							if (int1 != chunk.randomID) {
								ChunkRevisions.noise("randomID mismatch, resetting chunk " + chunk.wx + "," + chunk.wy + " before patching (was " + chunk.randomID + " now " + int1);
								chunk.randomID = int1;
								int2 = 0;
								while (true) {
									if (int2 >= chunk.squares.length) {
										chunk.getErosionData().init = false;
										if (!CellLoader.LoadCellBinaryChunk(IsoWorld.instance.CurrentCell, this.wx, this.wy, chunk)) {
											boolean2 = false;
											return boolean2;
										}

										break;
									}

									for (int int3 = 0; int3 < chunk.squares[int2].length; ++int3) {
										chunk.squares[int2][int3] = null;
									}

									++int2;
								}
							}

							ChunkRevisions.noise("patching " + chunk.wx + "," + chunk.wy + " randomID=" + chunk.randomID + " from revision " + chunk.revision + " to " + long1 + " with " + chunkTable.entries.size() + " squares");
							try {
								for (int2 = 0; int2 < chunkTable.entries.size(); ++int2) {
									ChunkRevisions.ChunkTableEntry chunkTableEntry = (ChunkRevisions.ChunkTableEntry)chunkTable.entries.get(int2);
									IsoGridSquare square = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, (SliceY)null, this.wx * 10 + chunkTableEntry.x, this.wy * 10 + chunkTableEntry.y, chunkTableEntry.z);
									square.chunk = chunk;
									square.revision = chunkTableEntry.revision;
									if (chunk.lotheader != null) {
										RoomDef roomDef = IsoWorld.instance.getMetaChunkFromTile(square.x, square.y).getRoomAt(square.x, square.y, square.z);
										int int4 = roomDef != null ? roomDef.ID : -1;
										square.setRoomID(int4);
									}

									square.ResetMasterRegion();
									ChunkRevisions.checkBytes(byteBuffer, ChunkRevisions.SQRE);
									square.load(byteBuffer, short1);
									if (byteBuffer.position() != chunkTableEntry.position + chunkTableEntry.length) {
										ChunkRevisions.noise("***** square didn\'t read as much as it wrote");
									}

									IsoGridSquare square2 = chunk.getGridSquare(chunkTableEntry.x, chunkTableEntry.y, chunkTableEntry.z);
									if (square2 != null) {
									}

									chunk.setSquare(chunkTableEntry.x, chunkTableEntry.y, chunkTableEntry.z, square);
								}
							} catch (Exception exception) {
								exception.printStackTrace();
								boolean boolean3 = false;
								return boolean3;
							}

							chunk.revision = long1;
							boolean2 = true;
							return boolean2;
						}
					}
				} finally {
					ChunkRevisions.ChunkTableEntry.release(chunkTable.entries);
					ChunkRevisions.ChunkTable.release(chunkTable);
					ChunkRevisions.Buffers.release(byteBuffer);
				}
			}
		}

		public boolean removeFile() {
			ChunkRevisions.Chunk chunk = ChunkRevisions.instance.getChunk(this.wx, this.wy);
			if (chunk != null) {
				synchronized (chunk) {
					chunk.patchRandomID = 0;
					chunk.patchRevision = -1L;
				}
			}

			synchronized (ChunkRevisions.instance.FileLock) {
				boolean boolean1;
				if (this.memoryFile != null) {
					try {
						if (this.exists()) {
							if (this.memoryFile.length() > 0) {
								ChunkRevisions.noise("removing patch-file " + this.wx + "," + this.wy);
							}

							this.memoryFile = null;
							if (chunk != null) {
								synchronized (chunk) {
									chunk.memoryFile = null;
								}
							}
						}

						boolean1 = true;
					} catch (Exception exception) {
						exception.printStackTrace();
						return false;
					}

					return boolean1;
				} else {
					try {
						File file = new File(this.fileName);
						if (file.exists()) {
							ChunkRevisions.noise("removing patch-file " + this.wx + "," + this.wy);
							boolean1 = file.delete();
							return boolean1;
						}

						boolean1 = true;
					} catch (Exception exception2) {
						exception2.printStackTrace();
						return false;
					}

					return boolean1;
				}
			}
		}
	}

	public static class ChunkFile {
		public int wx;
		public int wy;
		public String fileName;
		public int randomID;
		public long revision;

		public ChunkFile(int int1, int int2) {
			this.wx = int1;
			this.wy = int2;
			this.fileName = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_" + int1 + "_" + int2 + "_new.bin";
		}

		public boolean saveChunk(IsoChunk chunk) {
			ByteBuffer byteBuffer = ChunkRevisions.Buffers.get();
			ByteBuffer byteBuffer2 = ChunkRevisions.Buffers.get();
			ChunkRevisions.ChunkTable chunkTable = ChunkRevisions.ChunkTable.get();
			try {
				byteBuffer.rewind();
				int int1;
				ChunkRevisions.ChunkTableEntry chunkTableEntry;
				for (int int2 = 0; int2 < 8; ++int2) {
					for (int int3 = 0; int3 < 10; ++int3) {
						for (int int4 = 0; int4 < 10; ++int4) {
							IsoGridSquare square = chunk.getGridSquare(int4, int3, int2);
							if (square != null && square.shouldSave()) {
								try {
									int1 = byteBuffer.position();
									if (ChunkRevisions.debug) {
										byteBuffer.put(ChunkRevisions.SQRE);
									}

									square.save(byteBuffer, (ObjectOutputStream)null);
									chunkTableEntry = ChunkRevisions.ChunkTableEntry.get();
									chunkTableEntry.x = (byte)int4;
									chunkTableEntry.y = (byte)int3;
									chunkTableEntry.z = (byte)int2;
									chunkTableEntry.position = int1;
									chunkTableEntry.length = (short)(byteBuffer.position() - int1);
									chunkTableEntry.revision = square.revision;
									chunkTable.entries.add(chunkTableEntry);
								} catch (Exception exception) {
									exception.printStackTrace();
									boolean boolean1 = false;
									return boolean1;
								}
							}
						}
					}
				}

				byteBuffer2.rewind();
				byteBuffer2.putShort((short)143);
				byteBuffer2.putInt(chunk.randomID);
				byteBuffer2.putLong(chunk.revision);
				chunkTable.write(byteBuffer2);
				synchronized (ChunkRevisions.instance.FileLock) {
					try {
						File file = new File(this.fileName);
						FileOutputStream fileOutputStream = new FileOutputStream(file);
						BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
						bufferedOutputStream.write(byteBuffer2.array(), 0, byteBuffer2.position());
						for (int1 = 0; int1 < chunkTable.entries.size(); ++int1) {
							chunkTableEntry = (ChunkRevisions.ChunkTableEntry)chunkTable.entries.get(int1);
							bufferedOutputStream.write(byteBuffer.array(), chunkTableEntry.position, chunkTableEntry.length);
						}

						if (!GameClient.bClient) {
							byteBuffer2.rewind();
							byteBuffer2.put(ChunkRevisions.CARS);
							byteBuffer2.putShort((short)chunk.vehicles.size());
							for (int1 = 0; int1 < chunk.vehicles.size(); ++int1) {
								BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int1);
								byteBuffer2.put((byte)((int)baseVehicle.getX() - this.wx * 10));
								byteBuffer2.put((byte)((int)baseVehicle.getY() - this.wy * 10));
								byteBuffer2.put((byte)((int)baseVehicle.getZ()));
								baseVehicle.save(byteBuffer2);
							}

							bufferedOutputStream.write(byteBuffer2.array(), 0, byteBuffer2.position());
						}

						bufferedOutputStream.close();
					} catch (Exception exception2) {
						exception2.printStackTrace();
						boolean boolean2 = false;
						return boolean2;
					}
				}

				ChunkRevisions.Chunk chunk2 = ChunkRevisions.instance.getChunk(chunk.wx, chunk.wy);
				if (chunk2 != null) {
					chunk2.fileRandomID = chunk.randomID;
					chunk2.fileRevision = chunk.revision;
				}

				boolean boolean3 = true;
				return boolean3;
			} finally {
				ChunkRevisions.ChunkTableEntry.release(chunkTable.entries);
				ChunkRevisions.ChunkTable.release(chunkTable);
				ChunkRevisions.Buffers.release(byteBuffer);
				ChunkRevisions.Buffers.release(byteBuffer2);
			}
		}

		public IsoChunk loadChunk() {
			File file = new File(this.fileName);
			if (!file.exists()) {
				return null;
			} else {
				ByteBuffer byteBuffer = ChunkRevisions.Buffers.get();
				ChunkRevisions.ChunkTable chunkTable = ChunkRevisions.ChunkTable.get();
				try {
					synchronized (ChunkRevisions.instance.FileLock) {
						BufferedInputStream bufferedInputStream;
						try {
							FileInputStream fileInputStream = new FileInputStream(file);
							bufferedInputStream = new BufferedInputStream(fileInputStream);
							byteBuffer.rewind();
							bufferedInputStream.read(byteBuffer.array());
							bufferedInputStream.close();
						} catch (Exception exception) {
							exception.printStackTrace();
							bufferedInputStream = null;
							return bufferedInputStream;
						}
					}

					int int1 = this.wx * 10 / 300;
					int int2 = this.wy * 10 / 300;
					String string = int1 + "_" + int2 + ".lotheader";
					LotHeader lotHeader = null;
					if (IsoLot.InfoHeaders.containsKey(string)) {
						lotHeader = (LotHeader)IsoLot.InfoHeaders.get(string);
					}

					IsoChunk chunk = new IsoChunk(IsoWorld.instance.CurrentCell);
					chunk.wx = this.wx;
					chunk.wy = this.wy;
					chunk.lotheader = lotHeader;
					short short1 = byteBuffer.getShort();
					int int3 = byteBuffer.getInt();
					long long1 = byteBuffer.getLong();
					chunk.randomID = int3;
					chunk.revision = long1;
					IsoChunk chunk2;
					if (!chunkTable.read(byteBuffer)) {
						chunk2 = null;
						return chunk2;
					} else {
						ChunkRevisions.ChunkTableEntry chunkTableEntry;
						try {
							for (int int4 = 0; int4 < chunkTable.entries.size(); ++int4) {
								chunkTableEntry = (ChunkRevisions.ChunkTableEntry)chunkTable.entries.get(int4);
								IsoGridSquare square = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, (SliceY)null, this.wx * 10 + chunkTableEntry.x, this.wy * 10 + chunkTableEntry.y, chunkTableEntry.z);
								square.chunk = chunk;
								square.revision = chunkTableEntry.revision;
								if (lotHeader != null) {
									RoomDef roomDef = IsoWorld.instance.getMetaChunkFromTile(square.x, square.y).getRoomAt(square.x, square.y, square.z);
									int int5 = roomDef != null ? roomDef.ID : -1;
									square.setRoomID(int5);
								}

								square.ResetMasterRegion();
								chunk.setSquare(chunkTableEntry.x, chunkTableEntry.y, chunkTableEntry.z, square);
								ChunkRevisions.checkBytes(byteBuffer, ChunkRevisions.SQRE);
								square.load(byteBuffer, short1);
							}

							if (!GameClient.bClient) {
								ChunkRevisions.checkBytes(byteBuffer, ChunkRevisions.CARS);
								chunk.vehicles.clear();
								short short2 = byteBuffer.getShort();
								for (int int6 = 0; int6 < short2; ++int6) {
									byte byte1 = byteBuffer.get();
									byte byte2 = byteBuffer.get();
									byte byte3 = byteBuffer.get();
									IsoObject object = IsoObject.factoryFromFileInput(IsoWorld.instance.CurrentCell, byteBuffer);
									if (object != null && object instanceof BaseVehicle) {
										IsoGridSquare square2 = chunk.getGridSquare(byte1, byte2, byte3);
										object.square = square2;
										((IsoMovingObject)object).setCurrent(square2);
										object.load(byteBuffer, short1);
										chunk.vehicles.add((BaseVehicle)object);
										IsoChunk.addFromCheckedVehicles((BaseVehicle)object);
									}
								}
							}
						} catch (Exception exception2) {
							exception2.printStackTrace();
							chunkTableEntry = null;
							return chunkTableEntry;
						}

						chunk2 = chunk;
						return chunk2;
					}
				} finally {
					ChunkRevisions.ChunkTableEntry.release(chunkTable.entries);
					ChunkRevisions.ChunkTable.release(chunkTable);
					ChunkRevisions.Buffers.release(byteBuffer);
				}
			}
		}

		public boolean loadChunkRevision(ByteBuffer byteBuffer) {
			File file = new File(this.fileName);
			if (!file.exists()) {
				return false;
			} else {
				synchronized (ChunkRevisions.instance.FileLock) {
					try {
						FileInputStream fileInputStream = new FileInputStream(file);
						BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
						byteBuffer.rewind();
						bufferedInputStream.read(byteBuffer.array(), 0, 128);
						bufferedInputStream.close();
					} catch (Exception exception) {
						exception.printStackTrace();
						return false;
					}
				}

				short short1 = byteBuffer.getShort();
				this.randomID = byteBuffer.getInt();
				this.revision = byteBuffer.getLong();
				return true;
			}
		}

		public ChunkRevisions.ChunkTable loadChunkTable(ByteBuffer byteBuffer, long long1) {
			File file = new File(this.fileName);
			if (!file.exists()) {
				return null;
			} else {
				synchronized (ChunkRevisions.instance.FileLock) {
					try {
						FileInputStream fileInputStream = new FileInputStream(file);
						BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
						byteBuffer.rewind();
						bufferedInputStream.read(byteBuffer.array());
						bufferedInputStream.close();
					} catch (Exception exception) {
						exception.printStackTrace();
						return null;
					}
				}

				short short1 = byteBuffer.getShort();
				int int1 = byteBuffer.getInt();
				long long2 = byteBuffer.getLong();
				ChunkRevisions.ChunkTable chunkTable = ChunkRevisions.ChunkTable.get();
				if (!chunkTable.read(byteBuffer, long1, long2)) {
					ChunkRevisions.ChunkTable.release(chunkTable);
					return null;
				} else {
					return chunkTable;
				}
			}
		}
	}

	public static class ChunkTable {
		public ArrayList entries = new ArrayList();
		public static final ThreadLocal pool = new ThreadLocal(){
    
    protected Stack initialValue() {
        return new Stack();
    }
};

		public boolean read(ByteBuffer byteBuffer) {
			ChunkRevisions.checkBytes(byteBuffer, ChunkRevisions.CTBL);
			short short1 = byteBuffer.getShort();
			this.entries.ensureCapacity(short1);
			try {
				if (short1 > 0) {
					int int1;
					for (int1 = 0; int1 < short1; ++int1) {
						byte byte1 = byteBuffer.get();
						byte byte2 = byteBuffer.get();
						byte byte3 = byteBuffer.get();
						short short2 = byteBuffer.getShort();
						long long1 = byteBuffer.getLong();
						ChunkRevisions.ChunkTableEntry chunkTableEntry = ChunkRevisions.ChunkTableEntry.get();
						chunkTableEntry.x = byte1;
						chunkTableEntry.y = byte2;
						chunkTableEntry.z = byte3;
						chunkTableEntry.length = short2;
						chunkTableEntry.revision = long1;
						this.entries.add(chunkTableEntry);
					}

					ChunkRevisions.checkBytes(byteBuffer, ChunkRevisions.BEEF);
					int1 = byteBuffer.position();
					for (int int2 = 0; int2 < this.entries.size(); ++int2) {
						ChunkRevisions.ChunkTableEntry chunkTableEntry2 = (ChunkRevisions.ChunkTableEntry)this.entries.get(int2);
						chunkTableEntry2.position = int1;
						int1 += chunkTableEntry2.length;
					}
				}

				return true;
			} catch (Exception exception) {
				exception.printStackTrace();
				ChunkRevisions.ChunkTableEntry.release(this.entries);
				this.entries.clear();
				return false;
			}
		}

		public boolean read(ByteBuffer byteBuffer, long long1, long long2) {
			ChunkRevisions.checkBytes(byteBuffer, ChunkRevisions.CTBL);
			short short1 = byteBuffer.getShort();
			this.entries.ensureCapacity(short1);
			try {
				if (short1 > 0) {
					int int1 = byteBuffer.position();
					int int2;
					for (int2 = 0; int2 < short1; ++int2) {
						byte byte1 = byteBuffer.get();
						byte byte2 = byteBuffer.get();
						byte byte3 = byteBuffer.get();
						short short2 = byteBuffer.getShort();
						long long3 = byteBuffer.getLong();
						if (long3 <= long1) {
							break;
						}

						if (long3 != long1 || long1 >= long2) {
							ChunkRevisions.ChunkTableEntry chunkTableEntry = ChunkRevisions.ChunkTableEntry.get();
							chunkTableEntry.x = byte1;
							chunkTableEntry.y = byte2;
							chunkTableEntry.z = byte3;
							chunkTableEntry.length = short2;
							chunkTableEntry.revision = long3;
							this.entries.add(chunkTableEntry);
						}
					}

					int1 += short1 * 13;
					if (ChunkRevisions.debug) {
						int1 += 4;
					}

					for (int2 = 0; int2 < this.entries.size(); ++int2) {
						ChunkRevisions.ChunkTableEntry chunkTableEntry2 = (ChunkRevisions.ChunkTableEntry)this.entries.get(int2);
						chunkTableEntry2.position = int1;
						int1 += chunkTableEntry2.length;
					}
				}

				return true;
			} catch (Exception exception) {
				exception.printStackTrace();
				ChunkRevisions.ChunkTableEntry.release(this.entries);
				this.entries.clear();
				return false;
			}
		}

		public boolean write(ByteBuffer byteBuffer) {
			if (ChunkRevisions.debug) {
				byteBuffer.put(ChunkRevisions.CTBL);
			}

			if (this.entries == null) {
				byteBuffer.putShort((short)0);
				return true;
			} else {
				byteBuffer.putShort((short)this.entries.size());
				if (!this.entries.isEmpty()) {
					Collections.sort(this.entries);
					for (int int1 = 0; int1 < this.entries.size(); ++int1) {
						ChunkRevisions.ChunkTableEntry chunkTableEntry = (ChunkRevisions.ChunkTableEntry)this.entries.get(int1);
						byteBuffer.put(chunkTableEntry.x);
						byteBuffer.put(chunkTableEntry.y);
						byteBuffer.put(chunkTableEntry.z);
						byteBuffer.putShort(chunkTableEntry.length);
						byteBuffer.putLong(chunkTableEntry.revision);
					}

					if (ChunkRevisions.debug) {
						byteBuffer.put(ChunkRevisions.BEEF);
					}
				}

				return true;
			}
		}

		public void merge(ChunkRevisions.ChunkTable chunkTable, ChunkRevisions.ChunkTable chunkTable2) {
			HashMap hashMap = new HashMap();
			int int1;
			ChunkRevisions.ChunkTableEntry chunkTableEntry;
			for (int1 = 0; int1 < chunkTable.entries.size(); ++int1) {
				chunkTableEntry = (ChunkRevisions.ChunkTableEntry)chunkTable.entries.get(int1);
				hashMap.put(chunkTableEntry.x + "_" + chunkTableEntry.y + "_" + chunkTableEntry.z, chunkTableEntry);
			}

			for (int1 = 0; int1 < chunkTable2.entries.size(); ++int1) {
				chunkTableEntry = (ChunkRevisions.ChunkTableEntry)chunkTable2.entries.get(int1);
				hashMap.put(chunkTableEntry.x + "_" + chunkTableEntry.y + "_" + chunkTableEntry.z, chunkTableEntry);
			}

			this.entries.addAll(hashMap.values());
			Collections.sort(this.entries);
		}

		public static ChunkRevisions.ChunkTable get() {
			Stack stack = (Stack)pool.get();
			return stack.isEmpty() ? new ChunkRevisions.ChunkTable() : (ChunkRevisions.ChunkTable)stack.pop();
		}

		public static void release(ChunkRevisions.ChunkTable chunkTable) {
			if (chunkTable != null) {
				assert !((Stack)pool.get()).contains(chunkTable);
				chunkTable.entries.clear();
				((Stack)pool.get()).push(chunkTable);
			}
		}
	}

	public static class ChunkTableEntry implements Comparable {
		public byte x;
		public byte y;
		public byte z;
		public int position;
		public short length;
		public long revision;
		public static final ThreadLocal pool = new ThreadLocal(){
    
    protected Stack initialValue() {
        return new Stack();
    }
};

		public int compareTo(ChunkRevisions.ChunkTableEntry chunkTableEntry) {
			if (this.revision < chunkTableEntry.revision) {
				return 1;
			} else {
				return this.revision > chunkTableEntry.revision ? -1 : 0;
			}
		}

		public static ChunkRevisions.ChunkTableEntry get() {
			Stack stack = (Stack)pool.get();
			return stack.isEmpty() ? new ChunkRevisions.ChunkTableEntry() : (ChunkRevisions.ChunkTableEntry)stack.pop();
		}

		public static void release(ChunkRevisions.ChunkTableEntry chunkTableEntry) {
			((Stack)pool.get()).push(chunkTableEntry);
		}

		public static void release(ArrayList arrayList) {
			if (arrayList != null) {
				((Stack)pool.get()).addAll(arrayList);
			}
		}
	}

	public static class UploadFileToClient {
		public String fileName;
		public UdpConnection connection;
		public int port;
		public ServerSocket serverSocket;
		public Socket connectionSocket;
		public BufferedOutputStream outputStream;

		public UploadFileToClient(UdpConnection udpConnection, int int1, String string) {
			this.connection = udpConnection;
			this.port = int1;
			this.fileName = string;
		}

		public boolean init() {
			ChunkRevisions.noise("creating socket on port " + this.port);
			try {
				this.serverSocket = new ServerSocket();
				this.serverSocket.setSoTimeout(8000);
				this.serverSocket.setReuseAddress(true);
				this.serverSocket.bind(new InetSocketAddress(this.port));
				return true;
			} catch (Exception exception) {
				exception.printStackTrace();
				return false;
			}
		}

		public boolean connect() {
			if (this.serverSocket == null) {
				return false;
			} else {
				ChunkRevisions.noise("waiting for client to connect to upload port " + this.port);
				try {
					this.connectionSocket = this.serverSocket.accept();
					this.outputStream = new BufferedOutputStream(this.connectionSocket.getOutputStream());
					return true;
				} catch (SocketTimeoutException socketTimeoutException) {
					ChunkRevisions.noise(this.connection.username + ": " + socketTimeoutException.getMessage());
					return false;
				} catch (Exception exception) {
					exception.printStackTrace();
					return false;
				}
			}
		}

		public boolean upload() {
			if (this.outputStream == null) {
				return false;
			} else {
				ChunkRevisions.noise("uploading on port " + this.port);
				File file = new File(this.fileName);
				if (!file.exists()) {
					return false;
				} else {
					int int1 = (int)file.length();
					byte[] byteArray = new byte[int1];
					FileInputStream fileInputStream = null;
					label91: {
						boolean boolean1;
						try {
							fileInputStream = new FileInputStream(file);
							BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
							int1 = bufferedInputStream.read(byteArray);
							break label91;
						} catch (Exception exception) {
							exception.printStackTrace();
							boolean1 = false;
						} finally {
							if (fileInputStream != null) {
								try {
									fileInputStream.close();
								} catch (Exception exception2) {
									exception2.printStackTrace();
									return false;
								}
							}
						}

						return boolean1;
					}

					try {
						this.outputStream.write(int1 >>> 24 & 255);
						this.outputStream.write(int1 >>> 16 & 255);
						this.outputStream.write(int1 >>> 8 & 255);
						this.outputStream.write(int1 >>> 0 & 255);
						this.outputStream.write(byteArray);
						this.outputStream.flush();
						this.outputStream.close();
						this.connectionSocket.close();
						return true;
					} catch (Exception exception3) {
						exception3.printStackTrace();
						return false;
					}
				}
			}
		}

		public void cleanup() {
			try {
				if (this.connectionSocket != null) {
					this.connectionSocket.close();
					this.connectionSocket = null;
					this.outputStream = null;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				if (this.serverSocket != null) {
					this.serverSocket.close();
					this.serverSocket = null;
				}
			} catch (Exception exception2) {
				exception2.printStackTrace();
			}
		}
	}

	public static class ServerChunkRevisionRequest {
		public UdpConnection connection;
		public int port;
		public String fileName;
		public ArrayList chunks;
		public Thread thread;
		public ChunkRevisions.UploadFileToClient uftc;
		public long threadStartTime;
		public ChunkRevisions.ServerChunkRevisionRequest.State state;

		public ServerChunkRevisionRequest(UdpConnection udpConnection, ArrayList arrayList) {
			this.state = ChunkRevisions.ServerChunkRevisionRequest.State.RUNTHREAD;
			this.connection = udpConnection;
			this.port = udpConnection.playerDownloadServer.port;
			this.fileName = GameWindow.getCacheDir() + File.separator + "tmp" + this.port + ".zip";
			this.chunks = arrayList;
			ChunkRevisions.instance.executor.submit(new Runnable(){
				
				public void run() {
					while (!ServerChunkRevisionRequest.this.isFailed() && !ServerChunkRevisionRequest.this.isFinished()) {
						ServerChunkRevisionRequest.this.update();
					}
				}
			});
			this.threadStartTime = System.currentTimeMillis();
		}

		public void update() {
			switch (this.state) {
			case RUNTHREAD: 
				this.state = ChunkRevisions.ServerChunkRevisionRequest.State.INIT;
				break;
			
			case INIT: 
				this.createFile();
				break;
			
			case CREATEDFILE: 
				this.uftc = new ChunkRevisions.UploadFileToClient(this.connection, this.port, this.fileName);
				try {
					if (!this.uftc.init()) {
						this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
						return;
					}

					this.acknowledge();
					if (!this.uftc.connect()) {
						this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
						return;
					}

					if (!this.uftc.upload()) {
						this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
						return;
					}
				} finally {
					this.uftc.cleanup();
				}

				this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FINISHED;
			
			case FINISHED: 
			
			case FAILED: 
			
			}
		}

		public void createFile() {
			ZipOutputStream zipOutputStream = null;
			boolean boolean1 = true;
			label263: {
				try {
					File file = new File(this.fileName);
					zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
					int int1 = 0;
					while (true) {
						if (int1 >= this.chunks.size()) {
							break label263;
						}

						ChunkRevisions.ServerChunkRevisionRequestInfo serverChunkRevisionRequestInfo = (ChunkRevisions.ServerChunkRevisionRequestInfo)this.chunks.get(int1);
						ChunkRevisions.Chunk chunk = ChunkRevisions.instance.getChunk(serverChunkRevisionRequestInfo.wx, serverChunkRevisionRequestInfo.wy);
						if (chunk != null) {
							ChunkRevisions.instance.loadChunkRevision(serverChunkRevisionRequestInfo.wx, serverChunkRevisionRequestInfo.wy);
							int int2 = 0;
							long long1 = 0L;
							synchronized (chunk) {
								if (chunk.patchRevision != -1L) {
									int2 = chunk.patchRandomID;
									long1 = chunk.patchRevision;
								} else if (chunk.fileRevision != -1L) {
									int2 = chunk.fileRandomID;
									long1 = chunk.fileRevision;
								} else if (chunk.chunkRandomID != 0) {
									int2 = chunk.chunkRandomID;
								}
							}

							if ((int2 != 0 || serverChunkRevisionRequestInfo.randomID != 0) && (int2 != serverChunkRevisionRequestInfo.randomID || long1 > serverChunkRevisionRequestInfo.revision)) {
								ByteBuffer byteBuffer = ChunkRevisions.Buffers.get();
								try {
									byteBuffer.rewind();
									int int3 = this.addChunkRevisionsToBuffer(byteBuffer, chunk, serverChunkRevisionRequestInfo);
									if (int3 != -1) {
										ChunkRevisions.noise("adding " + int3 + " squares from chunk " + chunk.wx + "," + chunk.wy + " randomID=" + int2 + " (client=" + serverChunkRevisionRequestInfo.randomID + ") rev=" + long1 + " (client=" + serverChunkRevisionRequestInfo.revision + ") to zip");
										zipOutputStream.putNextEntry(new ZipEntry(chunk.wx + "_" + chunk.wy));
										zipOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
										boolean1 = false;
									}
								} finally {
									ChunkRevisions.Buffers.release(byteBuffer);
								}
							}
						}

						++int1;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
					this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
				} finally {
					if (zipOutputStream != null) {
						try {
							zipOutputStream.close();
						} catch (Exception exception2) {
							exception2.printStackTrace();
							this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
							return;
						}
					}
				}

				return;
			}
			if (boolean1) {
				this.notifyEmpty();
				ChunkRevisions.noise("nothing to send");
				this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FINISHED;
			} else {
				this.state = ChunkRevisions.ServerChunkRevisionRequest.State.CREATEDFILE;
			}
		}

		private int addChunkRevisionsToBuffer(ByteBuffer byteBuffer, ChunkRevisions.Chunk chunk, ChunkRevisions.ServerChunkRevisionRequestInfo serverChunkRevisionRequestInfo) {
			ByteBuffer byteBuffer2 = ChunkRevisions.Buffers.get();
			ByteBuffer byteBuffer3 = ChunkRevisions.Buffers.get();
			ChunkRevisions.ChunkTable chunkTable = ChunkRevisions.ChunkTable.get();
			ChunkRevisions.ChunkTable chunkTable2 = ChunkRevisions.ChunkTable.get();
			ChunkRevisions.ChunkTable chunkTable3 = ChunkRevisions.ChunkTable.get();
			try {
				int int1 = 0;
				synchronized (chunk) {
					if (chunk.chunkRandomID != 0) {
						int1 = chunk.chunkRandomID;
					} else if (chunk.fileRandomID != 0) {
						int1 = chunk.fileRandomID;
					}
				}

				if (int1 == 0 && serverChunkRevisionRequestInfo.randomID == 0) {
					byte byte1 = -1;
					return byte1;
				} else {
					ChunkRevisions.ChunkFile chunkFile = new ChunkRevisions.ChunkFile(chunk.wx, chunk.wy);
					ChunkRevisions.ChunkRevisionFile chunkRevisionFile = new ChunkRevisions.ChunkRevisionFile(chunk.wx, chunk.wy);
					long long1 = serverChunkRevisionRequestInfo.revision;
					if (int1 != serverChunkRevisionRequestInfo.randomID) {
						long1 = 0L;
					}

					chunkTable = chunkFile.loadChunkTable(byteBuffer2, long1);
					chunkTable2 = chunkRevisionFile.loadChunkTable(byteBuffer3, long1);
					if (chunkTable != null && chunk.fileRandomID != int1) {
						ChunkRevisions.ChunkTableEntry.release(chunkTable.entries);
						ChunkRevisions.ChunkTable.release(chunkTable);
						chunkTable = null;
					}

					if (chunkTable2 != null && chunk.patchRandomID != int1) {
						ChunkRevisions.ChunkTableEntry.release(chunkTable2.entries);
						ChunkRevisions.ChunkTable.release(chunkTable2);
						chunkTable2 = null;
					}

					if (chunkTable == null || chunkTable2 == null) {
						if (chunkTable == null && chunkTable2 == null) {
							byteBuffer.rewind();
							byteBuffer.putInt(int1);
							chunkTable3.write(byteBuffer);
							byte byte2 = -1;
							return byte2;
						} else {
							ChunkRevisions.ChunkTable chunkTable4 = chunkTable != null ? chunkTable : chunkTable2;
							ByteBuffer byteBuffer4 = chunkTable != null ? byteBuffer2 : byteBuffer3;
							byteBuffer.rewind();
							byteBuffer.putInt(int1);
							chunkTable4.write(byteBuffer);
							int int2;
							for (int2 = 0; int2 < chunkTable4.entries.size(); ++int2) {
								ChunkRevisions.ChunkTableEntry chunkTableEntry = (ChunkRevisions.ChunkTableEntry)chunkTable4.entries.get(int2);
								byteBuffer.put(byteBuffer4.array(), chunkTableEntry.position, chunkTableEntry.length);
							}

							int2 = chunkTable4.entries.size();
							return int2;
						}
					} else {
						chunkTable3.merge(chunkTable, chunkTable2);
						byteBuffer.rewind();
						byteBuffer.putInt(int1);
						chunkTable3.write(byteBuffer);
						int int3;
						for (int3 = 0; int3 < chunkTable3.entries.size(); ++int3) {
							ChunkRevisions.ChunkTableEntry chunkTableEntry2 = (ChunkRevisions.ChunkTableEntry)chunkTable3.entries.get(int3);
							if (chunkTable.entries.contains(chunkTableEntry2)) {
								byteBuffer.put(byteBuffer2.array(), chunkTableEntry2.position, chunkTableEntry2.length);
							} else {
								byteBuffer.put(byteBuffer3.array(), chunkTableEntry2.position, chunkTableEntry2.length);
							}
						}

						int3 = chunkTable3.entries.size();
						return int3;
					}
				}
			} finally {
				if (chunkTable != null) {
					ChunkRevisions.ChunkTableEntry.release(chunkTable.entries);
					ChunkRevisions.ChunkTable.release(chunkTable);
				}

				if (chunkTable2 != null) {
					ChunkRevisions.ChunkTableEntry.release(chunkTable2.entries);
					ChunkRevisions.ChunkTable.release(chunkTable2);
				}

				ChunkRevisions.ChunkTable.release(chunkTable3);
				ChunkRevisions.Buffers.release(byteBuffer2);
				ChunkRevisions.Buffers.release(byteBuffer3);
			}
		}

		public void acknowledge() {
			ByteBufferWriter byteBufferWriter = this.connection.startPacket();
			PacketTypes.doPacket((short)3, byteBufferWriter);
			byteBufferWriter.putByte((byte)1);
			this.connection.endPacket();
		}

		public void notifyEmpty() {
			ByteBufferWriter byteBufferWriter = this.connection.startPacket();
			PacketTypes.doPacket((short)3, byteBufferWriter);
			byteBufferWriter.putByte((byte)2);
			this.connection.endPacket();
		}

		public boolean isFinished() {
			return this.state == ChunkRevisions.ServerChunkRevisionRequest.State.FINISHED;
		}

		public boolean isFailed() {
			return this.state == ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
		}
		public static enum State {

			RUNTHREAD,
			INIT,
			CREATEDFILE,
			FINISHED,
			FAILED;
		}
	}

	public static class ServerChunkRevisionRequestInfo {
		short wx;
		short wy;
		int randomID;
		long revision;
		public static final ThreadLocal pool = new ThreadLocal(){
    
    protected Stack initialValue() {
        return new Stack();
    }
};

		public static ChunkRevisions.ServerChunkRevisionRequestInfo get() {
			Stack stack = (Stack)pool.get();
			return stack.isEmpty() ? new ChunkRevisions.ServerChunkRevisionRequestInfo() : (ChunkRevisions.ServerChunkRevisionRequestInfo)stack.pop();
		}

		public static void release(ChunkRevisions.ServerChunkRevisionRequestInfo serverChunkRevisionRequestInfo) {
			((Stack)pool.get()).push(serverChunkRevisionRequestInfo);
		}

		public static void release(ArrayList arrayList) {
			if (arrayList != null) {
				((Stack)pool.get()).addAll(arrayList);
			}
		}
	}

	public static class DownloadFileFromServer {
		public UdpConnection connection;
		public int port;
		public String fileName;
		public Socket socket;
		public InputStream socketInputStream;
		public ByteBuffer data;

		public DownloadFileFromServer(UdpConnection udpConnection, String string) {
			this.connection = udpConnection;
			this.fileName = string;
		}

		public boolean connect() {
			ChunkRevisions.noise("connecting to download port");
			try {
				InetAddress inetAddress = this.connection.getInetSocketAddress().getAddress();
				this.socket = new Socket();
				if (this.connection.getInetSocketAddress().toString().contains("127.0.0.1")) {
					InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 111);
					InetAddress inetAddress2 = inetSocketAddress.getAddress();
					int int1 = Rand.Next(10000) + 23456;
					ChunkRevisions.noise("using random local port " + int1);
					this.socket.bind(new InetSocketAddress(inetAddress2, int1));
					this.socket.connect(new InetSocketAddress(inetAddress, this.port), 8000);
				} else {
					this.socket.connect(new InetSocketAddress(inetAddress, this.port), 8000);
				}

				this.socketInputStream = this.socket.getInputStream();
				return true;
			} catch (Exception exception) {
				exception.printStackTrace();
				if (this.socket != null) {
					try {
						this.socket.close();
						this.socket = null;
					} catch (Exception exception2) {
						exception2.printStackTrace();
					}
				}

				return false;
			}
		}

		public boolean download() {
			ChunkRevisions.noise("downloading file");
			FileOutputStream fileOutputStream = null;
			BufferedOutputStream bufferedOutputStream = null;
			boolean boolean1;
			try {
				int int1 = this.socketInputStream.read();
				int int2 = this.socketInputStream.read();
				int int3 = this.socketInputStream.read();
				int int4 = this.socketInputStream.read();
				if ((int1 | int2 | int3 | int4) < 0) {
					throw new IOException();
				}

				int int5 = (int1 << 24) + (int2 << 16) + (int3 << 8) + (int4 << 0);
				if (this.fileName != null) {
					File file = new File(this.fileName);
					fileOutputStream = new FileOutputStream(file.getAbsoluteFile());
					bufferedOutputStream = new BufferedOutputStream(fileOutputStream, int5);
				} else {
					this.data = ByteBuffer.wrap(new byte[int5]);
				}

				int int6 = int5;
				int int7;
				for (byte[] byteArray = new byte[1]; int6 > 0; int6 -= int7) {
					int7 = this.socketInputStream.read(byteArray);
					if (int7 == -1) {
						throw new IOException();
					}

					if (this.fileName != null) {
						bufferedOutputStream.write(byteArray[0]);
					} else {
						this.data.put(byteArray[0]);
					}
				}

				return true;
			} catch (Exception exception) {
				exception.printStackTrace();
				boolean1 = false;
			} finally {
				try {
					if (bufferedOutputStream != null) {
						bufferedOutputStream.flush();
						bufferedOutputStream.close();
					}
				} catch (Exception exception2) {
					exception2.printStackTrace();
					return false;
				}
			}

			return boolean1;
		}
	}

	public static class ClientChunkRevisionRequest {
		public UdpConnection connection;
		public String fileName;
		public int[] area = new int[16];
		public Thread thread;
		public int timeout = 8000;
		public long requestTime;
		public ArrayList chunks = new ArrayList();
		public int coopRequest = -1;
		public ChunkRevisions.ClientChunkRevisionRequest.State state;
		public long threadStartTime;

		public ClientChunkRevisionRequest() {
			this.state = ChunkRevisions.ClientChunkRevisionRequest.State.RUNTHREAD;
			this.connection = GameClient.connection;
		}

		public ClientChunkRevisionRequest(int int1, int int2, int int3, int int4, int int5) {
			this.state = ChunkRevisions.ClientChunkRevisionRequest.State.RUNTHREAD;
			this.connection = GameClient.connection;
			this.setArea(int1, int2, int3, int4, int5);
		}

		public void setArea(int int1, int int2, int int3, int int4, int int5) {
			this.area[int1 * 4 + 0] = int2;
			this.area[int1 * 4 + 1] = int3;
			this.area[int1 * 4 + 2] = int4;
			this.area[int1 * 4 + 3] = int5;
		}

		public void start() {
			ChunkRevisions.instance.executor.submit(new Runnable(){
				
				public void run() {
					while (!ClientChunkRevisionRequest.this.isFailed() && !ClientChunkRevisionRequest.this.isFinished()) {
						ClientChunkRevisionRequest.this.update();
					}
				}
			});
			this.threadStartTime = System.currentTimeMillis();
		}

		public void update() {
			switch (this.state) {
			case RUNTHREAD: 
				this.state = ChunkRevisions.ClientChunkRevisionRequest.State.INIT;
				break;
			
			case INIT: 
				this.request();
				break;
			
			case REQUEST: 
				try {
					Thread.sleep(10L);
				} catch (InterruptedException interruptedException) {
				}

				if (System.currentTimeMillis() - this.requestTime > (long)this.timeout) {
					ChunkRevisions.noise("request timeout, aborting");
					this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FAILED;
				}

				break;
			
			case ACKNOWLEDGED: 
				this.download();
			
			case DOWNLOAD: 
			
			case FINISHED: 
			
			case FAILED: 
			
			}
		}

		public void request() {
			int int1;
			int int2;
			for (int int3 = 0; int3 < 4; ++int3) {
				int1 = this.area[int3 * 4 + 0];
				int int4 = this.area[int3 * 4 + 1];
				int2 = this.area[int3 * 4 + 2];
				int int5 = this.area[int3 * 4 + 3];
				for (int int6 = 0; int6 < int5; ++int6) {
					for (int int7 = 0; int7 < int2; ++int7) {
						ChunkRevisions.Chunk chunk = ChunkRevisions.instance.getChunk(int1 + int7, int4 + int6);
						if (chunk != null && !this.chunks.contains(chunk) && chunk.clientUpdates != chunk.serverUpdates) {
							chunk.clientRequest = chunk.serverUpdates;
							ChunkRevisions.instance.loadChunkRevision(int1 + int7, int4 + int6);
							this.chunks.add(chunk);
						}
					}
				}
			}

			if (this.chunks.isEmpty()) {
				ChunkRevisions.noise("no chunks need to be checked");
				this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FINISHED;
			} else {
				ByteBufferWriter byteBufferWriter = this.connection.startPacket();
				PacketTypes.doPacket((short)3, byteBufferWriter);
				byteBufferWriter.putShort((short)this.chunks.size());
				for (int1 = 0; int1 < this.chunks.size(); ++int1) {
					ChunkRevisions.Chunk chunk2 = (ChunkRevisions.Chunk)this.chunks.get(int1);
					int2 = 0;
					long long1 = 0L;
					synchronized (chunk2) {
						if (chunk2.chunkRandomID != 0) {
							int2 = chunk2.chunkRandomID;
						} else if (chunk2.patchRandomID != 0) {
							int2 = chunk2.patchRandomID;
						} else if (chunk2.fileRandomID != 0) {
							int2 = chunk2.fileRandomID;
						}

						if (chunk2.patchRevision != -1L && chunk2.patchRandomID == int2) {
							long1 = chunk2.patchRevision;
						} else if (chunk2.fileRevision != -1L && chunk2.fileRandomID == int2) {
							long1 = chunk2.fileRevision;
						}
					}

					byteBufferWriter.putShort((short)chunk2.wx);
					byteBufferWriter.putShort((short)chunk2.wy);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putLong(long1);
				}

				this.connection.endPacket();
				ChunkRevisions.noise("requesting " + this.chunks.size() + " chunks to be checked");
				this.requestTime = System.currentTimeMillis();
				this.state = ChunkRevisions.ClientChunkRevisionRequest.State.REQUEST;
			}
		}

		public void acknowledge() {
			this.state = ChunkRevisions.ClientChunkRevisionRequest.State.ACKNOWLEDGED;
		}

		public void emptyZip() {
			ChunkRevisions.noise("nothing to download");
			for (int int1 = 0; int1 < this.chunks.size(); ++int1) {
				((ChunkRevisions.Chunk)this.chunks.get(int1)).clientUpdates = ((ChunkRevisions.Chunk)this.chunks.get(int1)).clientRequest;
			}

			this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FINISHED;
		}

		public void download() {
			this.state = ChunkRevisions.ClientChunkRevisionRequest.State.DOWNLOAD;
			ChunkRevisions.DownloadFileFromServer downloadFileFromServer = new ChunkRevisions.DownloadFileFromServer(this.connection, (String)null);
			if (downloadFileFromServer.connect() && downloadFileFromServer.download()) {
				this.unzip(downloadFileFromServer.data);
			} else {
				this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FAILED;
			}
		}

		public void unzip(ByteBuffer byteBuffer) {
			ChunkRevisions.noise("downloaded, now unzipping");
			ByteBuffer byteBuffer2 = ChunkRevisions.Buffers.get();
			label99: {
				try {
					byte[] byteArray = new byte[1024];
					ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(byteBuffer.array()));
					for (ZipEntry zipEntry = zipInputStream.getNextEntry(); zipEntry != null; zipEntry = zipInputStream.getNextEntry()) {
						String[] stringArray = zipEntry.getName().split("_");
						int int1 = Integer.parseInt(stringArray[0]);
						int int2 = Integer.parseInt(stringArray[1]);
						byteBuffer2.rewind();
						int int3;
						while ((int3 = zipInputStream.read(byteArray)) > 0) {
							byteBuffer2.put(byteArray, 0, int3);
						}

						byteBuffer2.rewind();
						int int4 = byteBuffer2.getInt();
						ChunkRevisions.ChunkRevisionFile chunkRevisionFile = new ChunkRevisions.ChunkRevisionFile(int1, int2);
						boolean boolean1 = chunkRevisionFile.addNewRevisions(int4, byteBuffer2);
						if (boolean1) {
							ChunkRevisions.Chunk chunk = ChunkRevisions.instance.getChunk(int1, int2);
							chunk.patchRandomID = chunkRevisionFile.randomID;
							chunk.patchRevision = chunkRevisionFile.revision;
						} else {
							ChunkRevisions.noise("***** failed to add new revisions");
						}
					}

					zipInputStream.closeEntry();
					zipInputStream.close();
					int int5 = 0;
					while (true) {
						if (int5 >= this.chunks.size()) {
							break label99;
						}

						((ChunkRevisions.Chunk)this.chunks.get(int5)).clientUpdates = ((ChunkRevisions.Chunk)this.chunks.get(int5)).clientRequest;
						++int5;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
					this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FAILED;
				} finally {
					ChunkRevisions.Buffers.release(byteBuffer2);
				}

				return;
			}
			this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FINISHED;
		}

		public boolean isFinished() {
			return this.state == ChunkRevisions.ClientChunkRevisionRequest.State.FINISHED;
		}

		public boolean isFailed() {
			return this.state == ChunkRevisions.ClientChunkRevisionRequest.State.FAILED;
		}
		public static enum State {

			RUNTHREAD,
			INIT,
			REQUEST,
			ACKNOWLEDGED,
			DOWNLOAD,
			FINISHED,
			FAILED;
		}
	}

	public static class Buffers {
		public static Stack buffers = new Stack();

		public static synchronized ByteBuffer get() {
			ByteBuffer byteBuffer;
			if (buffers.isEmpty()) {
				byteBuffer = ByteBuffer.allocate(102400);
			} else {
				byteBuffer = (ByteBuffer)buffers.pop();
			}

			if (ChunkRevisions.debug) {
				for (int int1 = 0; int1 < byteBuffer.capacity(); ++int1) {
					byteBuffer.array()[int1] = 66;
				}
			}

			return byteBuffer;
		}

		public static synchronized void release(ByteBuffer byteBuffer) {
			buffers.push(byteBuffer);
		}
	}

	public class Chunk {
		public int wx;
		public int wy;
		public int chunkRandomID;
		public int fileRandomID;
		public long fileRevision = -1L;
		public int patchRandomID;
		public long patchRevision = -1L;
		public ChunkRevisions.MemoryFile memoryFile;
		public short serverUpdates = 1;
		public short clientRequest;
		public short clientUpdates;
		public ChunkRevisions.PatchJob patchJob;

		public Chunk(int int1, int int2) {
			this.wx = int1;
			this.wy = int2;
		}
	}
}
