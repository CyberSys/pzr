package zombie.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.WorldReuserThread;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSprite;


public class ServerChunkLoader {
	private long debugSlowMapLoadingDelay = 0L;
	private boolean MapLoading = false;
	private ServerChunkLoader.LoaderThread threadLoad = new ServerChunkLoader.LoaderThread();
	private ServerChunkLoader.SaveChunkThread threadSave;
	private final CRC32 crcSave = new CRC32();
	private ServerChunkLoader.RecalcAllThread threadRecalc;

	public ServerChunkLoader() {
		this.threadLoad.setName("LoadChunk");
		this.threadLoad.setDaemon(true);
		this.threadLoad.start();
		this.threadRecalc = new ServerChunkLoader.RecalcAllThread();
		this.threadRecalc.setName("RecalcAll");
		this.threadRecalc.setDaemon(true);
		this.threadRecalc.start();
		this.threadSave = new ServerChunkLoader.SaveChunkThread();
		this.threadSave.setName("SaveChunk");
		this.threadSave.setDaemon(true);
		this.threadSave.start();
	}

	public void addJob(ServerMap.ServerCell serverCell) {
		this.MapLoading = DebugType.Do(DebugType.MapLoading);
		this.threadLoad.toThread.add(serverCell);
	}

	public void getLoaded(ArrayList arrayList) {
		this.threadLoad.fromThread.drainTo(arrayList);
	}

	public void quit() {
		this.threadLoad.quit();
		while (this.threadLoad.isAlive()) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException interruptedException) {
			}
		}

		this.threadSave.quit();
		while (this.threadSave.isAlive()) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException interruptedException2) {
			}
		}
	}

	public void addSaveUnloadedJob(IsoChunk chunk) {
		this.threadSave.addUnloadedJob(chunk);
	}

	public void addSaveLoadedJob(IsoChunk chunk) {
		this.threadSave.addLoadedJob(chunk);
	}

	public void saveLater(GameTime gameTime) {
		this.threadSave.saveLater(gameTime);
	}

	public void updateSaved() {
		this.threadSave.update();
	}

	public void addRecalcJob(ServerMap.ServerCell serverCell) {
		this.threadRecalc.toThread.add(serverCell);
	}

	public void getRecalc(ArrayList arrayList) {
		this.threadRecalc.fromThread.drainTo(arrayList);
	}

	private class RecalcAllThread extends Thread {
		private final LinkedBlockingQueue toThread;
		private final LinkedBlockingQueue fromThread;
		private final ServerChunkLoader.GetSquare serverCellGetSquare;

		private RecalcAllThread() {
			this.toThread = new LinkedBlockingQueue();
			this.fromThread = new LinkedBlockingQueue();
			this.serverCellGetSquare = ServerChunkLoader.this.new GetSquare();
		}

		public void run() {
			while (true) {
				try {
					this.runInner();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		private void runInner() throws InterruptedException {
			ServerMap.ServerCell serverCell = (ServerMap.ServerCell)this.toThread.take();
			if (serverCell.bCancelLoading && !this.hasAnyBrandNewChunks(serverCell)) {
				for (int int1 = 0; int1 < 7; ++int1) {
					for (int int2 = 0; int2 < 7; ++int2) {
						IsoChunk chunk = serverCell.chunks[int2][int1];
						if (chunk != null) {
							serverCell.chunks[int2][int1] = null;
							WorldReuserThread.instance.addReuseChunk(chunk);
						}
					}
				}

				if (ServerChunkLoader.this.MapLoading) {
					DebugLog.log(DebugType.MapLoading, "RecalcAllThread: cancelled " + serverCell.WX + "," + serverCell.WY);
				}

				serverCell.bLoadingWasCancelled = true;
			} else {
				long long1 = System.nanoTime();
				this.serverCellGetSquare.cell = serverCell;
				int int3 = serverCell.WX * 70;
				int int4 = serverCell.WY * 70;
				int int5 = int3 + 70;
				int int6 = int4 + 70;
				int int7 = 0;
				byte byte1 = 100;
				int int8;
				int int9;
				IsoChunk chunk2;
				int int10;
				int int11;
				IsoGridSquare square;
				for (int8 = 0; int8 < 7; ++int8) {
					for (int9 = 0; int9 < 7; ++int9) {
						chunk2 = serverCell.chunks[int8][int9];
						if (chunk2 != null) {
							chunk2.bLoaded = false;
							for (int10 = 0; int10 < byte1; ++int10) {
								for (int11 = 0; int11 <= chunk2.maxLevel; ++int11) {
									square = chunk2.squares[int11][int10];
									if (int11 == 0) {
										if (square == null) {
											int int12 = chunk2.wx * 10 + int10 % 10;
											int int13 = chunk2.wy * 10 + int10 / 10;
											square = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, (SliceY)null, int12, int13, int11);
											chunk2.setSquare(int12 % 10, int13 % 10, int11, square);
										}

										if (square.getFloor() == null) {
											DebugLog.log("ERROR: added floor at " + square.x + "," + square.y + "," + square.z + " because there wasn\'t one");
											IsoObject object = IsoObject.getNew();
											object.sprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, (String)"carpentry_02_58", 0);
											object.square = square;
											square.getObjects().add(0, object);
										}
									}

									if (square != null) {
										square.RecalcProperties();
									}
								}
							}

							if (chunk2.maxLevel > int7) {
								int7 = chunk2.maxLevel;
							}
						}
					}
				}

				for (int8 = 0; int8 < 7; ++int8) {
					for (int9 = 0; int9 < 7; ++int9) {
						chunk2 = serverCell.chunks[int8][int9];
						if (chunk2 != null) {
							for (int10 = 0; int10 < byte1; ++int10) {
								for (int11 = 0; int11 <= chunk2.maxLevel; ++int11) {
									square = chunk2.squares[int11][int10];
									if (square != null) {
										if (int11 > 0 && !square.getObjects().isEmpty()) {
											this.serverCellGetSquare.EnsureSurroundNotNull(square.x - int3, square.y - int4, int11);
										}

										square.RecalcAllWithNeighbours(true, this.serverCellGetSquare);
									}
								}
							}
						}
					}
				}

				for (int8 = 0; int8 < 7; ++int8) {
					for (int9 = 0; int9 < 7; ++int9) {
						chunk2 = serverCell.chunks[int8][int9];
						if (chunk2 != null) {
							label149: for (int10 = 0; int10 < byte1; ++int10) {
								for (int11 = chunk2.maxLevel; int11 > 0; --int11) {
									square = chunk2.squares[int11][int10];
									if (square != null && square.Is(IsoFlagType.solidfloor)) {
										--int11;
										while (true) {
											if (int11 < 0) {
												continue label149;
											}

											square = chunk2.squares[int11][int10];
											if (square != null) {
												square.haveRoof = true;
												square.getProperties().UnSet(IsoFlagType.exterior);
											}

											--int11;
										}
									}
								}
							}
						}
					}
				}

				if (GameServer.bDebug && ServerChunkLoader.this.debugSlowMapLoadingDelay > 0L) {
					Thread.sleep(ServerChunkLoader.this.debugSlowMapLoadingDelay);
				}

				float float1 = (float)(System.nanoTime() - long1) / 1000000.0F;
				if (ServerChunkLoader.this.MapLoading) {
					DebugLog.log(DebugType.MapLoading, "RecalcAll for cell " + serverCell.WX + "," + serverCell.WY + " ms=" + float1);
				}

				this.fromThread.add(serverCell);
			}
		}

		private boolean hasAnyBrandNewChunks(ServerMap.ServerCell serverCell) {
			for (int int1 = 0; int1 < 7; ++int1) {
				for (int int2 = 0; int2 < 7; ++int2) {
					IsoChunk chunk = serverCell.chunks[int2][int1];
					if (chunk != null && !chunk.getErosionData().init) {
						return true;
					}
				}
			}

			return false;
		}

		RecalcAllThread(Object object) {
			this();
		}
	}

	private class GetSquare implements IsoGridSquare.GetSquare {
		ServerMap.ServerCell cell;

		private GetSquare() {
		}

		public IsoGridSquare getGridSquare(int int1, int int2, int int3) {
			int1 -= this.cell.WX * 70;
			int2 -= this.cell.WY * 70;
			if (int1 >= 0 && int1 < 70) {
				if (int2 >= 0 && int2 < 70) {
					IsoChunk chunk = this.cell.chunks[int1 / 10][int2 / 10];
					return chunk == null ? null : chunk.getGridSquare(int1 % 10, int2 % 10, int3);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		public boolean contains(int int1, int int2, int int3) {
			if (int1 >= 0 && int1 < 70) {
				return int2 >= 0 && int2 < 70;
			} else {
				return false;
			}
		}

		public IsoChunk getChunkForSquare(int int1, int int2) {
			int1 -= this.cell.WX * 70;
			int2 -= this.cell.WY * 70;
			if (int1 >= 0 && int1 < 70) {
				return int2 >= 0 && int2 < 70 ? this.cell.chunks[int1 / 10][int2 / 10] : null;
			} else {
				return null;
			}
		}

		public void EnsureSurroundNotNull(int int1, int int2, int int3) {
			int int4 = this.cell.WX * 70;
			int int5 = this.cell.WY * 70;
			for (int int6 = -1; int6 <= 1; ++int6) {
				for (int int7 = -1; int7 <= 1; ++int7) {
					if ((int6 != 0 || int7 != 0) && this.contains(int1 + int6, int2 + int7, int3)) {
						IsoGridSquare square = this.getGridSquare(int4 + int1 + int6, int5 + int2 + int7, int3);
						if (square == null) {
							square = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, (SliceY)null, int4 + int1 + int6, int5 + int2 + int7, int3);
							int int8 = (int1 + int6) / 10;
							int int9 = (int2 + int7) / 10;
							int int10 = (int1 + int6) % 10;
							int int11 = (int2 + int7) % 10;
							if (this.cell.chunks[int8][int9] != null) {
								this.cell.chunks[int8][int9].setSquare(int10, int11, int3, square);
							}
						}
					}
				}
			}
		}

		GetSquare(Object object) {
			this();
		}
	}

	private class SaveChunkThread extends Thread {
		private final LinkedBlockingQueue toThread;
		private final LinkedBlockingQueue fromThread;
		private boolean quit;
		private final CRC32 crc32;
		private final ClientChunkRequest ccr;
		private final ArrayList toSaveChunk;
		private final ArrayList savedChunks;

		private SaveChunkThread() {
			this.toThread = new LinkedBlockingQueue();
			this.fromThread = new LinkedBlockingQueue();
			this.quit = false;
			this.crc32 = new CRC32();
			this.ccr = new ClientChunkRequest();
			this.toSaveChunk = new ArrayList();
			this.savedChunks = new ArrayList();
		}

		public void run() {
			do {
				ServerChunkLoader.SaveTask saveTask = null;
				try {
					saveTask = (ServerChunkLoader.SaveTask)this.toThread.take();
					saveTask.save();
					this.fromThread.add(saveTask);
				} catch (InterruptedException interruptedException) {
				} catch (Exception exception) {
					exception.printStackTrace();
					if (saveTask != null) {
						LoggerManager.getLogger("map").write("Error saving chunk " + saveTask.wx() + "," + saveTask.wy());
					}

					LoggerManager.getLogger("map").write(exception);
				}
			} while (!this.quit || !this.toThread.isEmpty());
		}

		public void addUnloadedJob(IsoChunk chunk) {
			this.toThread.add(ServerChunkLoader.this.new SaveUnloadedTask(chunk));
		}

		public void addLoadedJob(IsoChunk chunk) {
			ClientChunkRequest.Chunk chunk2 = this.ccr.getChunk();
			chunk2.wx = chunk.wx;
			chunk2.wy = chunk.wy;
			this.ccr.getByteBuffer(chunk2);
			try {
				chunk.SaveLoadedChunk(chunk2, this.crc32);
			} catch (Exception exception) {
				exception.printStackTrace();
				LoggerManager.getLogger("map").write(exception);
				this.ccr.releaseChunk(chunk2);
				return;
			}

			this.toThread.add(ServerChunkLoader.this.new SaveLoadedTask(this.ccr, chunk2));
		}

		public void saveLater(GameTime gameTime) {
			this.toThread.add(ServerChunkLoader.this.new SaveGameTimeTask(gameTime));
		}

		public void saveNow(int int1, int int2) {
			this.toSaveChunk.clear();
			this.toThread.drainTo(this.toSaveChunk);
			for (int int3 = 0; int3 < this.toSaveChunk.size(); ++int3) {
				ServerChunkLoader.SaveTask saveTask = (ServerChunkLoader.SaveTask)this.toSaveChunk.get(int3);
				if (saveTask.wx() == int1 && saveTask.wy() == int2) {
					try {
						this.toSaveChunk.remove(int3--);
						saveTask.save();
					} catch (Exception exception) {
						exception.printStackTrace();
						LoggerManager.getLogger("map").write("Error saving chunk " + int1 + "," + int2);
						LoggerManager.getLogger("map").write(exception);
					}

					this.fromThread.add(saveTask);
				}
			}

			this.toThread.addAll(this.toSaveChunk);
		}

		public void quit() {
			this.toThread.add(ServerChunkLoader.this.new QuitThreadTask());
		}

		public void update() {
			this.savedChunks.clear();
			this.fromThread.drainTo(this.savedChunks);
			for (int int1 = 0; int1 < this.savedChunks.size(); ++int1) {
				((ServerChunkLoader.SaveTask)this.savedChunks.get(int1)).release();
			}

			this.savedChunks.clear();
		}

		SaveChunkThread(Object object) {
			this();
		}
	}

	private class QuitThreadTask implements ServerChunkLoader.SaveTask {

		private QuitThreadTask() {
		}

		public void save() throws Exception {
			ServerChunkLoader.this.threadSave.quit = true;
		}

		public void release() {
		}

		public int wx() {
			return 0;
		}

		public int wy() {
			return 0;
		}

		QuitThreadTask(Object object) {
			this();
		}
	}

	private class SaveGameTimeTask implements ServerChunkLoader.SaveTask {
		private byte[] bytes;

		public SaveGameTimeTask(GameTime gameTime) {
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(32768);
				Throwable throwable = null;
				try {
					DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
					Throwable throwable2 = null;
					try {
						gameTime.save(dataOutputStream);
						dataOutputStream.close();
						this.bytes = byteArrayOutputStream.toByteArray();
					} catch (Throwable throwable3) {
						throwable2 = throwable3;
						throw throwable3;
					} finally {
						if (dataOutputStream != null) {
							if (throwable2 != null) {
								try {
									dataOutputStream.close();
								} catch (Throwable throwable4) {
									throwable2.addSuppressed(throwable4);
								}
							} else {
								dataOutputStream.close();
							}
						}
					}
				} catch (Throwable throwable5) {
					throwable = throwable5;
					throw throwable5;
				} finally {
					if (byteArrayOutputStream != null) {
						if (throwable != null) {
							try {
								byteArrayOutputStream.close();
							} catch (Throwable throwable6) {
								throwable.addSuppressed(throwable6);
							}
						} else {
							byteArrayOutputStream.close();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		public void save() throws Exception {
			if (this.bytes != null) {
				File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_t.bin");
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					Throwable throwable = null;
					try {
						fileOutputStream.write(this.bytes);
					} catch (Throwable throwable2) {
						throwable = throwable2;
						throw throwable2;
					} finally {
						if (fileOutputStream != null) {
							if (throwable != null) {
								try {
									fileOutputStream.close();
								} catch (Throwable throwable3) {
									throwable.addSuppressed(throwable3);
								}
							} else {
								fileOutputStream.close();
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
					return;
				}
			}
		}

		public void release() {
		}

		public int wx() {
			return 0;
		}

		public int wy() {
			return 0;
		}
	}

	private class SaveLoadedTask implements ServerChunkLoader.SaveTask {
		private final ClientChunkRequest ccr;
		private final ClientChunkRequest.Chunk chunk;

		public SaveLoadedTask(ClientChunkRequest clientChunkRequest, ClientChunkRequest.Chunk chunk) {
			this.ccr = clientChunkRequest;
			this.chunk = chunk;
		}

		public void save() throws Exception {
			long long1 = ChunkChecksum.getChecksumIfExists(this.chunk.wx, this.chunk.wy);
			ServerChunkLoader.this.crcSave.reset();
			ServerChunkLoader.this.crcSave.update(this.chunk.bb.array(), 0, this.chunk.bb.position());
			if (long1 != ServerChunkLoader.this.crcSave.getValue()) {
				ChunkChecksum.setChecksum(this.chunk.wx, this.chunk.wy, ServerChunkLoader.this.crcSave.getValue());
				IsoChunk.SafeWrite("map_", this.chunk.wx, this.chunk.wy, this.chunk.bb);
			}
		}

		public void release() {
			this.ccr.releaseChunk(this.chunk);
		}

		public int wx() {
			return this.chunk.wx;
		}

		public int wy() {
			return this.chunk.wy;
		}
	}

	private class SaveUnloadedTask implements ServerChunkLoader.SaveTask {
		private final IsoChunk chunk;

		public SaveUnloadedTask(IsoChunk chunk) {
			this.chunk = chunk;
		}

		public void save() throws Exception {
			this.chunk.Save(false);
		}

		public void release() {
			WorldReuserThread.instance.addReuseChunk(this.chunk);
		}

		public int wx() {
			return this.chunk.wx;
		}

		public int wy() {
			return this.chunk.wy;
		}
	}

	private interface SaveTask {

		void save() throws Exception;

		void release();

		int wx();

		int wy();
	}

	private class LoaderThread extends Thread {
		private final LinkedBlockingQueue toThread;
		private final LinkedBlockingQueue fromThread;

		private LoaderThread() {
			this.toThread = new LinkedBlockingQueue();
			this.fromThread = new LinkedBlockingQueue();
		}

		public void run() {
			while (true) {
				while (true) {
					try {
						ServerMap.ServerCell serverCell = (ServerMap.ServerCell)this.toThread.take();
						if (serverCell.WX == -1 && serverCell.WY == -1) {
							return;
						}

						if (!serverCell.bCancelLoading) {
							long long1 = System.nanoTime();
							for (int int1 = 0; int1 < 7; ++int1) {
								for (int int2 = 0; int2 < 7; ++int2) {
									int int3 = serverCell.WX * 7 + int1;
									int int4 = serverCell.WY * 7 + int2;
									if (IsoWorld.instance.MetaGrid.isValidChunk(int3, int4)) {
										IsoChunk chunk = (IsoChunk)IsoChunkMap.chunkStore.poll();
										if (chunk == null) {
											chunk = new IsoChunk((IsoCell)null);
										}

										ServerChunkLoader.this.threadSave.saveNow(int3, int4);
										try {
											if (chunk.LoadOrCreate(int3, int4, (ByteBuffer)null)) {
												chunk.bLoaded = true;
											} else {
												ChunkChecksum.setChecksum(int3, int4, 0L);
												chunk.Blam(int3, int4);
												if (chunk.LoadBrandNew(int3, int4)) {
													chunk.bLoaded = true;
												}
											}
										} catch (Exception exception) {
											exception.printStackTrace();
											LoggerManager.getLogger("map").write(exception);
										}

										if (chunk.bLoaded) {
											serverCell.chunks[int1][int2] = chunk;
										}
									}
								}
							}

							if (GameServer.bDebug && ServerChunkLoader.this.debugSlowMapLoadingDelay > 0L) {
								Thread.sleep(ServerChunkLoader.this.debugSlowMapLoadingDelay);
							}

							float float1 = (float)(System.nanoTime() - long1) / 1000000.0F;
							if (ServerChunkLoader.this.MapLoading) {
								DebugLog.log(DebugType.MapLoading, "loaded chunks for cell " + serverCell.WX + "," + serverCell.WY + " ms=" + float1);
							}

							this.fromThread.add(serverCell);
						} else {
							if (ServerChunkLoader.this.MapLoading) {
								DebugLog.log(DebugType.MapLoading, "LoaderThread: cancelled " + serverCell.WX + "," + serverCell.WY);
							}

							serverCell.bLoadingWasCancelled = true;
						}
					} catch (Exception exception2) {
						exception2.printStackTrace();
						LoggerManager.getLogger("map").write(exception2);
					}
				}
			}
		}

		public void quit() {
			ServerMap.ServerCell serverCell = new ServerMap.ServerCell();
			serverCell.WX = -1;
			serverCell.WY = -1;
			this.toThread.add(serverCell);
		}

		LoaderThread(Object object) {
			this();
		}
	}
}
