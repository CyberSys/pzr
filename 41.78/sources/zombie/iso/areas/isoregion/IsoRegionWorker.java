package zombie.iso.areas.isoregion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import zombie.GameWindow;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.ThreadGroups;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.data.DataRoot;
import zombie.iso.areas.isoregion.jobs.JobApplyChanges;
import zombie.iso.areas.isoregion.jobs.JobChunkUpdate;
import zombie.iso.areas.isoregion.jobs.JobServerSendFullData;
import zombie.iso.areas.isoregion.jobs.JobSquareUpdate;
import zombie.iso.areas.isoregion.jobs.RegionJob;
import zombie.iso.areas.isoregion.jobs.RegionJobManager;
import zombie.iso.areas.isoregion.jobs.RegionJobType;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;


public final class IsoRegionWorker {
	private Thread thread;
	private boolean bFinished;
	protected static final AtomicBoolean isRequestingBufferSwap = new AtomicBoolean(false);
	private static IsoRegionWorker instance;
	private DataRoot rootBuffer = new DataRoot();
	private List discoveredChunks = new ArrayList();
	private final List threadDiscoveredChunks = new ArrayList();
	private int lastThreadDiscoveredChunksSize = 0;
	private final ConcurrentLinkedQueue jobQueue = new ConcurrentLinkedQueue();
	private final ConcurrentLinkedQueue jobOutgoingQueue = new ConcurrentLinkedQueue();
	private final List jobBatchedProcessing = new ArrayList();
	private final ConcurrentLinkedQueue finishedJobQueue = new ConcurrentLinkedQueue();
	private static final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

	protected IsoRegionWorker() {
		instance = this;
	}

	protected void create() {
		if (this.thread == null) {
			this.bFinished = false;
			this.thread = new Thread(ThreadGroups.Workers, ()->{
				while (!this.bFinished) {
					try {
						this.thread_main_loop();
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			});

			this.thread.setPriority(5);
			this.thread.setDaemon(true);
			this.thread.setName("IsoRegionWorker");
			this.thread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
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

			if (this.jobQueue.size() > 0) {
				DebugLog.IsoRegion.warn("IsoRegionWorker -> JobQueue has items remaining");
			}

			if (this.jobBatchedProcessing.size() > 0) {
				DebugLog.IsoRegion.warn("IsoRegionWorker -> JobBatchedProcessing has items remaining");
			}

			this.jobQueue.clear();
			this.jobOutgoingQueue.clear();
			this.jobBatchedProcessing.clear();
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
		JobApplyChanges jobApplyChanges = RegionJobManager.allocApplyChanges(boolean1);
		this.jobQueue.add(jobApplyChanges);
	}

	private void thread_main_loop() throws InterruptedException, IsoRegionException {
		IsoRegions.PRINT_D = DebugLog.isEnabled(DebugType.IsoRegion);
		for (RegionJob regionJob = (RegionJob)this.jobQueue.poll(); regionJob != null; regionJob = (RegionJob)this.jobQueue.poll()) {
			switch (regionJob.getJobType()) {
			case ServerSendFullData: 
				if (!GameServer.bServer) {
					break;
				}

				UdpConnection udpConnection = ((JobServerSendFullData)regionJob).getTargetConn();
				if (udpConnection == null) {
					if (Core.bDebug) {
						throw new IsoRegionException("IsoRegion: Server send full data target connection == null");
					}

					IsoRegions.warn("IsoRegion: Server send full data target connection == null");
					break;
				}

				IsoRegions.log("IsoRegion: Server Send Full Data to " + udpConnection.idStr);
				ArrayList arrayList = new ArrayList();
				this.rootBuffer.getAllChunks(arrayList);
				JobChunkUpdate jobChunkUpdate = RegionJobManager.allocChunkUpdate();
				jobChunkUpdate.setTargetConn(udpConnection);
				Iterator iterator = arrayList.iterator();
				DataChunk dataChunk;
				for (; iterator.hasNext(); jobChunkUpdate.addChunkFromDataChunk(dataChunk)) {
					dataChunk = (DataChunk)iterator.next();
					if (!jobChunkUpdate.canAddChunk()) {
						this.jobOutgoingQueue.add(jobChunkUpdate);
						jobChunkUpdate = RegionJobManager.allocChunkUpdate();
						jobChunkUpdate.setTargetConn(udpConnection);
					}
				}

				if (jobChunkUpdate.getChunkCount() > 0) {
					this.jobOutgoingQueue.add(jobChunkUpdate);
				} else {
					RegionJobManager.release(jobChunkUpdate);
				}

				this.finishedJobQueue.add(regionJob);
				break;
			
			case DebugResetAllData: 
				IsoRegions.log("IsoRegion: Debug Reset All Data");
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
			
			case ChunkUpdate: 
			
			case ApplyChanges: 
				IsoRegions.log("IsoRegion: Queueing " + regionJob.getJobType() + " for batched processing.");
				this.jobBatchedProcessing.add(regionJob);
				if (regionJob.getJobType() == RegionJobType.ApplyChanges) {
					this.thread_run_batched_jobs();
					this.jobBatchedProcessing.clear();
				}

				break;
			
			default: 
				this.finishedJobQueue.add(regionJob);
			
			}
		}

		Thread.sleep(20L);
	}

	private void thread_run_batched_jobs() throws InterruptedException {
		IsoRegions.log("IsoRegion: Apply changes -> Batched processing " + this.jobBatchedProcessing.size() + " jobs.");
		for (int int1 = 0; int1 < 2; ++int1) {
			for (int int2 = 0; int2 < this.jobBatchedProcessing.size(); ++int2) {
				RegionJob regionJob = (RegionJob)this.jobBatchedProcessing.get(int2);
				switch (regionJob.getJobType()) {
				case SquareUpdate: 
					JobSquareUpdate jobSquareUpdate = (JobSquareUpdate)regionJob;
					this.rootBuffer.updateExistingSquare(jobSquareUpdate.getWorldSquareX(), jobSquareUpdate.getWorldSquareY(), jobSquareUpdate.getWorldSquareZ(), jobSquareUpdate.getNewSquareFlags());
					break;
				
				case ChunkUpdate: 
					JobChunkUpdate jobChunkUpdate = (JobChunkUpdate)regionJob;
					jobChunkUpdate.readChunksPacket(this.rootBuffer, this.threadDiscoveredChunks);
					break;
				
				case ApplyChanges: 
					this.rootBuffer.processDirtyChunks();
					if (int1 == 0) {
						isRequestingBufferSwap.set(true);
						while (isRequestingBufferSwap.get()) {
							Thread.sleep(5L);
						}
					} else {
						JobApplyChanges jobApplyChanges = (JobApplyChanges)regionJob;
						if (!GameClient.bClient && jobApplyChanges.isSaveToDisk()) {
							for (int int3 = this.jobBatchedProcessing.size() - 1; int3 >= 0; --int3) {
								RegionJob regionJob2 = (RegionJob)this.jobBatchedProcessing.get(int3);
								if (regionJob2.getJobType() == RegionJobType.ChunkUpdate || regionJob2.getJobType() == RegionJobType.SquareUpdate) {
									JobChunkUpdate jobChunkUpdate2;
									if (regionJob2.getJobType() == RegionJobType.SquareUpdate) {
										JobSquareUpdate jobSquareUpdate2 = (JobSquareUpdate)regionJob2;
										this.rootBuffer.select.reset(jobSquareUpdate2.getWorldSquareX(), jobSquareUpdate2.getWorldSquareY(), jobSquareUpdate2.getWorldSquareZ(), true, false);
										jobChunkUpdate2 = RegionJobManager.allocChunkUpdate();
										jobChunkUpdate2.addChunkFromDataChunk(this.rootBuffer.select.chunk);
									} else {
										this.jobBatchedProcessing.remove(int3);
										jobChunkUpdate2 = (JobChunkUpdate)regionJob2;
									}

									jobChunkUpdate2.saveChunksToDisk();
									if (GameServer.bServer) {
										this.jobOutgoingQueue.add(jobChunkUpdate2);
									}
								}
							}

							if (this.threadDiscoveredChunks.size() > 0 && this.threadDiscoveredChunks.size() > this.lastThreadDiscoveredChunksSize && !Core.getInstance().isNoSave()) {
								IsoRegions.log("IsoRegion: Apply changes -> Saving header file to disk.");
								File file = IsoRegions.getHeaderFile();
								try {
									DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
									dataOutputStream.writeInt(195);
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

						this.finishedJobQueue.addAll(this.jobBatchedProcessing);
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
		IsoRegions.log("IsoRegion: Load save map.");
		if (!GameClient.bClient) {
			this.loadSaveMap();
		} else {
			GameClient.sendIsoRegionDataRequest();
		}
	}

	protected void update() {
		for (RegionJob regionJob = (RegionJob)this.finishedJobQueue.poll(); regionJob != null; regionJob = (RegionJob)this.finishedJobQueue.poll()) {
			RegionJobManager.release(regionJob);
		}

		for (JobChunkUpdate jobChunkUpdate = (JobChunkUpdate)this.jobOutgoingQueue.poll(); jobChunkUpdate != null; jobChunkUpdate = (JobChunkUpdate)this.jobOutgoingQueue.poll()) {
			if (GameServer.bServer) {
				IsoRegions.log("IsoRegion: sending changed datachunks packet.");
				try {
					for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
						UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
						if (jobChunkUpdate.getTargetConn() == null || jobChunkUpdate.getTargetConn() == udpConnection) {
							ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
							PacketTypes.PacketType.IsoRegionServerPacket.doPacket(byteBufferWriter);
							ByteBuffer byteBuffer = byteBufferWriter.bb;
							byteBuffer.putLong(System.nanoTime());
							jobChunkUpdate.saveChunksToNetBuffer(byteBuffer);
							PacketTypes.PacketType.IsoRegionServerPacket.send(udpConnection);
						}
					}
				} catch (Exception exception) {
					DebugLog.log(exception.getMessage());
					exception.printStackTrace();
				}
			}

			RegionJobManager.release(jobChunkUpdate);
		}
	}

	protected void readServerUpdatePacket(ByteBuffer byteBuffer) {
		if (GameClient.bClient) {
			IsoRegions.log("IsoRegion: Receiving changed datachunk packet from server");
			try {
				JobChunkUpdate jobChunkUpdate = RegionJobManager.allocChunkUpdate();
				long long1 = byteBuffer.getLong();
				jobChunkUpdate.readChunksFromNetBuffer(byteBuffer, long1);
				this.EnqueueJob(jobChunkUpdate);
				this.ApplyChunkChanges();
			} catch (Exception exception) {
				DebugLog.log(exception.getMessage());
				exception.printStackTrace();
			}
		}
	}

	protected void readClientRequestFullUpdatePacket(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (GameServer.bServer && udpConnection != null) {
			IsoRegions.log("IsoRegion: Receiving request full data packet from client");
			try {
				JobServerSendFullData jobServerSendFullData = RegionJobManager.allocServerSendFullData(udpConnection);
				this.EnqueueJob(jobServerSendFullData);
			} catch (Exception exception) {
				DebugLog.log(exception.getMessage());
				exception.printStackTrace();
			}
		}
	}

	protected void addDebugResetJob() {
		if (!GameServer.bServer && !GameClient.bClient) {
			this.EnqueueJob(RegionJobManager.allocDebugResetAllData());
		}
	}

	protected void addSquareChangedJob(int int1, int int2, int int3, boolean boolean1, byte byte1) {
		int int4 = int1 / 10;
		int int5 = int2 / 10;
		int int6 = IsoRegions.hash(int4, int5);
		if (this.discoveredChunks.contains(int6)) {
			IsoRegions.log("Update square only, plus any unprocessed chunks in a 7x7 grid.", Colors.Magenta);
			JobSquareUpdate jobSquareUpdate = RegionJobManager.allocSquareUpdate(int1, int2, int3, byte1);
			this.EnqueueJob(jobSquareUpdate);
			this.readSurroundingChunks(int4, int5, 7, false);
			this.ApplyChunkChanges();
		} else {
			if (boolean1) {
				return;
			}

			IsoRegions.log("Adding new chunk, plus any unprocessed chunks in a 7x7 grid.", Colors.Magenta);
			this.readSurroundingChunks(int4, int5, 7, true);
		}
	}

	protected void readSurroundingChunks(int int1, int int2, int int3, boolean boolean1) {
		this.readSurroundingChunks(int1, int2, int3, boolean1, false);
	}

	protected void readSurroundingChunks(int int1, int int2, int int3, boolean boolean1, boolean boolean2) {
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
		JobChunkUpdate jobChunkUpdate = RegionJobManager.allocChunkUpdate();
		boolean boolean3 = false;
		for (int int9 = int5; int9 <= int7; ++int9) {
			for (int int10 = int6; int10 <= int8; ++int10) {
				IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int9, int10) : IsoWorld.instance.getCell().getChunk(int9, int10);
				if (chunk != null) {
					int int11 = IsoRegions.hash(chunk.wx, chunk.wy);
					if (boolean2 || !this.discoveredChunks.contains(int11)) {
						this.discoveredChunks.add(int11);
						if (!jobChunkUpdate.canAddChunk()) {
							this.EnqueueJob(jobChunkUpdate);
							jobChunkUpdate = RegionJobManager.allocChunkUpdate();
						}

						jobChunkUpdate.addChunkFromIsoChunk(chunk);
						boolean3 = true;
					}
				}
			}
		}

		if (jobChunkUpdate.getChunkCount() > 0) {
			this.EnqueueJob(jobChunkUpdate);
		} else {
			RegionJobManager.release(jobChunkUpdate);
		}

		if (boolean3 && boolean1) {
			this.ApplyChunkChanges();
		}
	}

	private void loadSaveMap() {
		try {
			boolean boolean1 = false;
			ArrayList arrayList = new ArrayList();
			File file = IsoRegions.getHeaderFile();
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

			File file2 = IsoRegions.getDirectory();
			File[] fileArray = file2.listFiles(new FilenameFilter(){
				
				public boolean accept(File boolean1, String arrayList) {
					return arrayList.startsWith("datachunk_") && arrayList.endsWith(".bin");
				}
			});

			JobChunkUpdate jobChunkUpdate = RegionJobManager.allocChunkUpdate();
			ByteBuffer byteBuffer = byteBuffer;
			boolean boolean2 = false;
			if (fileArray != null) {
				File[] fileArray2 = fileArray;
				int int5 = fileArray.length;
				for (int int6 = 0; int6 < int5; ++int6) {
					File file3 = fileArray2[int6];
					FileInputStream fileInputStream = new FileInputStream(file3);
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
						int int12 = IsoRegions.hash(int10, int11);
						if (!this.discoveredChunks.contains(int12)) {
							this.discoveredChunks.add(int12);
						}

						if (arrayList.contains(int12)) {
							arrayList.remove(arrayList.indexOf(int12));
						} else {
							IsoRegions.warn("IsoRegion: A chunk save has been found that was not in header known chunks list.");
						}

						if (!jobChunkUpdate.canAddChunk()) {
							this.EnqueueJob(jobChunkUpdate);
							jobChunkUpdate = RegionJobManager.allocChunkUpdate();
						}

						jobChunkUpdate.addChunkFromFile(byteBuffer);
						boolean2 = true;
					} catch (Throwable throwable) {
						try {
							fileInputStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					fileInputStream.close();
				}
			}

			if (jobChunkUpdate.getChunkCount() > 0) {
				this.EnqueueJob(jobChunkUpdate);
			} else {
				RegionJobManager.release(jobChunkUpdate);
			}

			if (boolean2) {
				this.ApplyChunkChanges(false);
			}

			if (boolean1 && arrayList.size() > 0) {
				IsoRegions.warn("IsoRegion: " + arrayList.size() + " previously discovered chunks have not been loaded.");
				throw new IsoRegionException("IsoRegion: " + arrayList.size() + " previously discovered chunks have not been loaded.");
			}
		} catch (Exception exception) {
			DebugLog.log(exception.getMessage());
			exception.printStackTrace();
		}
	}
}
