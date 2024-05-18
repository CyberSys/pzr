package zombie.iso.areas.isoregion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;


public class IsoRegionWorker {
	private Thread thread;
	private boolean bFinished;
	protected static AtomicBoolean isRequestingBufferSwap = new AtomicBoolean(false);
	private static IsoRegionWorker instance;
	private DataRoot rootBuffer = new DataRoot();
	private List discoveredChunks = new ArrayList();
	private List threadDiscoveredChunks = new ArrayList();
	private int lastThreadDiscoveredChunksSize = 0;
	private final ConcurrentLinkedQueue jobQueue = new ConcurrentLinkedQueue();
	private final ConcurrentLinkedQueue jobOutgoingQueue = new ConcurrentLinkedQueue();
	private List jobProcessingList = new ArrayList();
	private final ConcurrentLinkedQueue finishedJobQueue = new ConcurrentLinkedQueue();
	protected static final int SINGLE_CHUNK_PACKET_SIZE = 1024;
	protected static final int CHUNKS_DATA_PACKET_SIZE = 65536;
	private static final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
	private String cacheDir;
	private File cacheDirFile;
	private File headDataFile;
	private final Map chunkFileNames = new HashMap();

	protected IsoRegionWorker() {
		instance = this;
	}

	protected void create() {
		if (this.thread == null) {
			this.bFinished = false;
			this.thread = new Thread(new Runnable(){
				
				public void run() {
					while (!IsoRegionWorker.this.bFinished) {
						try {
							IsoRegionWorker.this.threadRun();
						} catch (Exception var2) {
							var2.printStackTrace();
						}
					}
				}
			});

			this.thread.setPriority(5);
			this.thread.setDaemon(true);
			this.thread.setName("IsoRegionWorker");
			this.thread.start();
		}
	}

	protected void stop() {
		if (this.thread != null) {
			if (this.thread != null) {
				this.bFinished = true;
				while (true) {
					if (!this.thread.isAlive()) {
						this.thread = null;
						break;
					}
				}
			}

			this.jobQueue.clear();
			this.jobOutgoingQueue.clear();
			this.jobProcessingList.clear();
			this.finishedJobQueue.clear();
			this.rootBuffer = null;
			this.discoveredChunks = null;
		}
	}

	protected void EnqueueJob(RegionJob regionJob) {
		this.jobQueue.add(regionJob);
	}

	protected void ApplyChunkChanges() {
		this.ApplyChunkChanges(true);
	}

	protected void ApplyChunkChanges(boolean boolean1) {
		RegionJob regionJob = RegionJob.allocApplyChunkChanges(boolean1);
		this.jobQueue.add(regionJob);
	}

	protected static void EnqueueDirtyChunk(DataChunk dataChunk) {
		instance.rootBuffer.EnqueueDirtyChunk(dataChunk);
	}

	protected static void EnqueueDirtyMasterRegion(MasterRegion masterRegion) {
		instance.rootBuffer.EnqueueDirtyMasterRegion(masterRegion);
	}

	protected static void DequeueDirtyMasterRegion(MasterRegion masterRegion) {
		instance.rootBuffer.DequeueDirtyMasterRegion(masterRegion);
	}

	protected File getDirectory() {
		if (this.cacheDir == null) {
			String string = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "isoregiondata" + File.separator;
			File file = new File(string);
			if (!file.exists()) {
				file.mkdir();
			}

			this.cacheDirFile = file;
			this.cacheDir = string;
		}

		return this.cacheDirFile;
	}

	protected File getChunkFile(int int1, int int2) {
		int int3 = IsoRegion.hash(int1, int2);
		if (this.chunkFileNames.containsKey(int3)) {
			return (File)this.chunkFileNames.get(int3);
		} else {
			if (this.cacheDir == null) {
				this.getDirectory();
			}

			String string = this.cacheDir + "datachunk_" + int1 + "_" + int2 + ".bin";
			File file = new File(string);
			this.chunkFileNames.put(int3, file);
			return file;
		}
	}

	protected File getHeaderFile() {
		if (this.headDataFile == null) {
			if (this.cacheDir == null) {
				this.getDirectory();
			}

			String string = this.cacheDir + "RegionHeader.bin";
			this.headDataFile = new File(string);
		}

		return this.headDataFile;
	}

	private void threadRun() throws InterruptedException {
		for (RegionJob regionJob = (RegionJob)this.jobQueue.poll(); regionJob != null; regionJob = (RegionJob)this.jobQueue.poll()) {
			switch (regionJob.getJobType()) {
			case ServerSendFullData: 
				if (regionJob.getTargetConn() == null) {
					if (IsoRegion.PRINT_D) {
						DebugLog.log("IsoRegion: Server send full data target connection == null");
					}

					break;
				}

				ArrayList arrayList = new ArrayList();
				this.rootBuffer.getAllChunks(arrayList);
				RegionJob regionJob2 = RegionJob.allocReadChunksPacket();
				Iterator iterator = arrayList.iterator();
				DataChunk dataChunk;
				for (; iterator.hasNext(); regionJob2.addChunkFromDataChunk(dataChunk)) {
					dataChunk = (DataChunk)iterator.next();
					if (regionJob2 == null || !regionJob2.canAddChunk()) {
						if (regionJob2 != null) {
							this.jobOutgoingQueue.add(regionJob2);
						}

						regionJob2 = RegionJob.allocReadChunksPacket();
					}
				}

				if (regionJob2 != null) {
					if (regionJob2.getChunkCount() > 0) {
						this.jobOutgoingQueue.add(regionJob2);
					} else {
						RegionJob.release(regionJob2);
					}
				}

				this.finishedJobQueue.add(regionJob);
				break;
			
			case DebugResetAllData: 
				for (int int1 = 0; int1 < 2; ++int1) {
					this.rootBuffer.resetAllData();
					if (int1 == 0) {
						isRequestingBufferSwap.set(true);
						while (isRequestingBufferSwap.get() && !this.bFinished) {
							Thread.sleep(5L);
						}
					}
				}

				this.finishedJobQueue.add(regionJob);
				break;
			
			case SquareUpdate: 
			
			case ReadChunksPacket: 
			
			case ApplyChunkChanges: 
				this.jobProcessingList.add(regionJob);
				if (regionJob.getJobType() == RegionJobType.ApplyChunkChanges) {
					this.runJobsList();
					this.jobProcessingList.clear();
				}

				break;
			
			default: 
				this.finishedJobQueue.add(regionJob);
			
			}
		}

		Thread.sleep(20L);
	}

	private void runJobsList() throws InterruptedException {
		for (int int1 = 0; int1 < 2; ++int1) {
			for (int int2 = 0; int2 < this.jobProcessingList.size(); ++int2) {
				RegionJob regionJob = (RegionJob)this.jobProcessingList.get(int2);
				switch (regionJob.getJobType()) {
				case SquareUpdate: 
					this.rootBuffer.select.reset(regionJob.getWorldSquareX(), regionJob.getWorldSquareY(), regionJob.getWorldSquareZ(), true, false);
					if (this.rootBuffer.select.chunk != null) {
						byte byte1 = -1;
						if (this.rootBuffer.select.square != -1) {
							byte1 = this.rootBuffer.select.square;
						}

						if (regionJob.getNewSquareFlags() != byte1) {
							this.rootBuffer.select.chunk.setOrAddSquare(this.rootBuffer.select.chunkSquareX, this.rootBuffer.select.chunkSquareY, this.rootBuffer.select.z, regionJob.getNewSquareFlags(), true);
							this.rootBuffer.select.chunk.setDirty(this.rootBuffer.select.z);
						}
					} else if (IsoRegion.PRINT_D) {
						DebugLog.log("IsoRegion: trying to change a square on a unknown chunk");
					}

					break;
				
				case ReadChunksPacket: 
					regionJob.readChunksPacket(this.rootBuffer, this.threadDiscoveredChunks);
					break;
				
				case ApplyChunkChanges: 
					this.rootBuffer.processDirtyChunks();
					if (int1 == 0) {
						isRequestingBufferSwap.set(true);
						while (isRequestingBufferSwap.get()) {
							Thread.sleep(5L);
						}
					} else {
						RegionJob.printStats();
						ChunkRegion.printStats();
						MasterRegion.printStats();
						RegionJob regionJob2;
						if (!GameClient.bClient && regionJob.getSaveToDisk()) {
							for (int int3 = this.jobProcessingList.size() - 1; int3 >= 0; --int3) {
								regionJob2 = (RegionJob)this.jobProcessingList.get(int3);
								if (regionJob2.getJobType() == RegionJobType.ReadChunksPacket || regionJob2.getJobType() == RegionJobType.SquareUpdate) {
									if (regionJob2.getJobType() == RegionJobType.SquareUpdate) {
										this.rootBuffer.select.reset(regionJob2.getWorldSquareX(), regionJob2.getWorldSquareY(), regionJob2.getWorldSquareZ(), true, false);
										regionJob2 = RegionJob.allocReadChunksPacket();
										regionJob2.addChunkFromDataChunk(this.rootBuffer.select.chunk);
									} else if (regionJob2.getJobType() == RegionJobType.ReadChunksPacket) {
										this.jobProcessingList.remove(int3);
									}

									regionJob2.saveChunksToDisk(this);
									if (GameServer.bServer) {
										this.jobOutgoingQueue.add(regionJob2);
									}
								}
							}

							if (this.threadDiscoveredChunks.size() > 0 && this.threadDiscoveredChunks.size() > this.lastThreadDiscoveredChunksSize) {
								File file = this.getHeaderFile();
								try {
									DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
									dataOutputStream.writeInt(143);
									dataOutputStream.writeInt(this.threadDiscoveredChunks.size());
									Iterator iterator = this.threadDiscoveredChunks.iterator();
									while (iterator.hasNext()) {
										Integer integer = (Integer)iterator.next();
										dataOutputStream.writeInt(integer);
									}

									dataOutputStream.flush();
									dataOutputStream.close();
									this.lastThreadDiscoveredChunksSize = this.threadDiscoveredChunks.size();
								} catch (Exception exception) {
									DebugLog.log(exception.getMessage());
									exception.printStackTrace();
								}
							}
						}

						Iterator iterator2 = this.jobProcessingList.iterator();
						while (iterator2.hasNext()) {
							regionJob2 = (RegionJob)iterator2.next();
							this.finishedJobQueue.add(regionJob2);
						}
					}

				
				}
			}
		}
	}

	protected DataRoot getRootBuffer() {
		return this.rootBuffer;
	}

	protected void setRootBuffer(DataRoot dataRoot) {
		this.rootBuffer = dataRoot;
	}

	protected void load() {
		if (IsoRegion.PRINT_D) {
			DebugLog.log("IsoRegion: Load save map.");
		}

		if (!GameClient.bClient) {
			this.loadSaveMap();
		} else {
			GameClient.sendIsoRegionDataRequest();
		}
	}

	protected void update() {
		RegionJob regionJob;
		for (regionJob = (RegionJob)this.finishedJobQueue.poll(); regionJob != null; regionJob = (RegionJob)this.finishedJobQueue.poll()) {
			RegionJob.release(regionJob);
		}

		for (regionJob = (RegionJob)this.jobOutgoingQueue.poll(); regionJob != null; regionJob = (RegionJob)this.jobOutgoingQueue.poll()) {
			if (GameServer.bServer) {
				if (IsoRegion.PRINT_D) {
					DebugLog.log("IsoRegion: sending changed datachunks packet.");
				}

				try {
					for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
						UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
						if (regionJob.getTargetConn() == null || regionJob.getTargetConn() == udpConnection) {
							ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
							PacketTypes.doPacket((short)201, byteBufferWriter);
							ByteBuffer byteBuffer = byteBufferWriter.bb;
							byteBuffer.putLong(System.nanoTime());
							regionJob.saveChunksToNetBuffer(byteBuffer);
							udpConnection.endPacketImmediate();
						}
					}
				} catch (Exception exception) {
					DebugLog.log(exception.getMessage());
					exception.printStackTrace();
				}
			}

			RegionJob.release(regionJob);
		}
	}

	protected void readServerUpdatePacket(ByteBuffer byteBuffer) {
		if (GameClient.bClient) {
			if (IsoRegion.PRINT_D) {
				DebugLog.log("IsoRegion: Receiving changed datachunk packet from server");
			}

			try {
				RegionJob regionJob = RegionJob.allocReadChunksPacket();
				long long1 = byteBuffer.getLong();
				regionJob.readChunksFromNetBuffer(byteBuffer, long1);
				this.EnqueueJob(regionJob);
				this.ApplyChunkChanges();
			} catch (Exception exception) {
				DebugLog.log(exception.getMessage());
				exception.printStackTrace();
			}
		}
	}

	protected void readClientRequestFullUpdatePacket(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (GameServer.bServer && udpConnection != null) {
			if (IsoRegion.PRINT_D) {
				DebugLog.log("IsoRegion: Receiving request full data packet from client");
			}

			try {
				RegionJob regionJob = RegionJob.allocServerSendFullData(udpConnection);
				this.EnqueueJob(regionJob);
			} catch (Exception exception) {
				DebugLog.log(exception.getMessage());
				exception.printStackTrace();
			}
		}
	}

	protected void addDebugResetJob() {
		if (!GameServer.bServer && !GameClient.bClient) {
			this.EnqueueJob(RegionJob.allocDebugResetAllData());
		}
	}

	protected void addSquareChangedJob(int int1, int int2, int int3, boolean boolean1, byte byte1) {
		int int4 = int1 / 10;
		int int5 = int2 / 10;
		int int6 = IsoRegion.hash(int4, int5);
		if (this.discoveredChunks.contains(int6)) {
			if (IsoRegion.PRINT_D) {
				DebugLog.log("Update square only, plus any unprocessed chunks in a 7x7 grid.");
			}

			RegionJob regionJob = RegionJob.allocSquareUpdate(int1, int2, int3, byte1);
			this.EnqueueJob(regionJob);
			this.readSurroundingChunks(int4, int5, 7, false);
			this.ApplyChunkChanges();
		} else {
			if (boolean1) {
				return;
			}

			if (IsoRegion.PRINT_D) {
				DebugLog.log("Adding new chunk, plus any unprocessed chunks in a 7x7 grid.");
			}

			this.readSurroundingChunks(int4, int5, 7, true);
		}
	}

	protected void readSurroundingChunks(int int1, int int2, int int3, boolean boolean1) {
		int int4 = 1;
		if (int3 > 0 && int3 <= IsoChunkMap.ChunkGridWidth) {
			int4 = int3 / 2;
			if (int4 + int4 >= IsoChunkMap.ChunkGridWidth) {
				--int4;
			}
		}

		int int5 = int1 - int4;
		int int6 = int2 - int4;
		int int7 = int1 + int4;
		int int8 = int2 + int4;
		RegionJob regionJob = null;
		for (int int9 = int5; int9 <= int7; ++int9) {
			for (int int10 = int6; int10 <= int8; ++int10) {
				IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int9, int10) : IsoWorld.instance.getCell().getChunk(int9, int10);
				if (chunk != null) {
					int int11 = IsoRegion.hash(chunk.wx, chunk.wy);
					if (!this.discoveredChunks.contains(int11)) {
						this.discoveredChunks.add(int11);
						if (regionJob == null || !regionJob.canAddChunk()) {
							if (regionJob != null) {
								this.EnqueueJob(regionJob);
							}

							regionJob = RegionJob.allocReadChunksPacket();
						}

						regionJob.addChunkFromIsoChunk(chunk);
					}
				}
			}
		}

		if (regionJob != null) {
			if (regionJob.getChunkCount() > 0) {
				this.EnqueueJob(regionJob);
				if (boolean1) {
					this.ApplyChunkChanges();
				}
			} else {
				RegionJob.release(regionJob);
			}
		}
	}

	private void loadSaveMap() {
		try {
			boolean boolean1 = false;
			ArrayList arrayList = new ArrayList();
			File file = this.getHeaderFile();
			if (file.exists()) {
				DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
				boolean1 = true;
				int int1 = dataInputStream.readInt();
				int int2 = dataInputStream.readInt();
				int int3 = 0;
				while (true) {
					if (int3 >= int2) {
						dataInputStream.close();
						break;
					}

					int int4 = dataInputStream.readInt();
					arrayList.add(int4);
					++int3;
				}
			}

			File file2 = this.getDirectory();
			File[] fileArray = file2.listFiles(new FilenameFilter(){
				
				public boolean accept(File boolean1, String arrayList) {
					return arrayList.startsWith("datachunk_") && arrayList.endsWith(".bin");
				}
			});

			RegionJob regionJob = null;
			ByteBuffer byteBuffer = byteBuffer;
			boolean boolean2 = false;
			if (fileArray != null) {
				File[] fileArray2 = fileArray;
				int int5 = fileArray.length;
				for (int int6 = 0; int6 < int5; ++int6) {
					File file3 = fileArray2[int6];
					FileInputStream fileInputStream = new FileInputStream(file3);
					Throwable throwable = null;
					try {
						byteBuffer.clear();
						int int7 = fileInputStream.read(byteBuffer.array());
						byteBuffer.limit(int7);
						byteBuffer.mark();
						int int8 = byteBuffer.getInt();
						int int9 = byteBuffer.getInt();
						int int10 = byteBuffer.getInt();
						int int11 = byteBuffer.getInt();
						byteBuffer.reset();
						int int12 = IsoRegion.hash(int10, int11);
						if (!this.discoveredChunks.contains(int12)) {
							this.discoveredChunks.add(int12);
						}

						if (arrayList.contains(int12)) {
							arrayList.remove(arrayList.indexOf(int12));
						} else {
							DebugLog.log("IsoRegion: A chunk save has been found that was not in header known chunks list.");
						}

						if (regionJob == null || !regionJob.canAddChunk()) {
							if (regionJob != null) {
								this.EnqueueJob(regionJob);
							}

							regionJob = RegionJob.allocReadChunksPacket();
						}

						regionJob.addChunkFromFile(byteBuffer);
						boolean2 = true;
					} catch (Throwable throwable2) {
						throwable = throwable2;
						throw throwable2;
					} finally {
						if (fileInputStream != null) {
							if (throwable != null) {
								try {
									fileInputStream.close();
								} catch (Throwable throwable3) {
									throwable.addSuppressed(throwable3);
								}
							} else {
								fileInputStream.close();
							}
						}
					}
				}
			}

			if (regionJob != null) {
				if (regionJob.getChunkCount() > 0) {
					this.EnqueueJob(regionJob);
				} else {
					RegionJob.release(regionJob);
				}
			}

			if (boolean2) {
				this.ApplyChunkChanges(false);
			}

			if (boolean1 && arrayList.size() > 0) {
				DebugLog.log("IsoRegion: " + arrayList.size() + " previously discovered chunks have not been loaded.");
			}
		} catch (Exception exception) {
			DebugLog.log(exception.getMessage());
			exception.printStackTrace();
		}
	}
}
