package zombie.iso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import zombie.GameTime;
import zombie.TileAccessibilityWorker;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.bucket.BucketManager;
import zombie.core.physics.WorldSimulation;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.iso.areas.IsoRoom;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.TextManager;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleCache;
import zombie.vehicles.VehicleManager;


public class IsoChunkMap {
	public static int ChunkDiv = 10;
	public static final int ChunksPerWidth = 10;
	public static int StartChunkGridWidth = 13;
	public static int ChunkGridWidth;
	public static int MPWorldXA;
	public static int MPWorldYA;
	public static int MPWorldZA;
	public static int ChunkWidthInTiles;
	public int PlayerID = 0;
	public boolean ignore = false;
	static int WorldCellX;
	static int WorldCellY;
	static int PosX;
	static int PosY;
	public static int WorldXA;
	public static int WorldYA;
	public static int WorldZA;
	public static int[] SWorldX;
	public int WorldX;
	public int WorldY;
	public static int[] SWorldY;
	IsoCell cell;
	boolean bReadBufferA;
	protected IsoChunk[] chunksSwapB;
	protected IsoChunk[] chunksSwapA;
	private UpdateLimit checkVehiclesFrequency;
	public static ConcurrentLinkedQueue chunkStore;
	public static ReentrantLock bSettingChunk;
	public static ReentrantLock bSettingChunkLighting;
	public static HashMap SharedChunks;
	int MovedInform;
	boolean bMovingPos;
	public ArrayList filenameServerRequests;
	int XMinTiles;
	int YMinTiles;
	int XMaxTiles;
	int YMaxTiles;
	static ArrayList saveList;
	protected static ColorInfo inf;
	private static ArrayList splatByType;

	public static void CalcChunkWidth() {
		float float1 = (float)Core.getInstance().getScreenWidth();
		float float2 = (float)Core.getInstance().getScreenHeight();
		float float3 = float1 / 1920.0F;
		if (float3 > 1.0F) {
			float3 = 1.0F;
		}

		ChunkGridWidth = (int)((double)((float)StartChunkGridWidth * float3) * 1.5);
		if (ChunkGridWidth / 2 * 2 == ChunkGridWidth) {
			++ChunkGridWidth;
		}

		ChunkWidthInTiles = ChunkGridWidth * 10;
	}

	public IsoChunkMap(IsoCell cell) {
		this.WorldX = WorldXA / 10;
		this.WorldY = WorldYA / 10;
		this.bReadBufferA = true;
		this.checkVehiclesFrequency = new UpdateLimit(3000L);
		this.MovedInform = 0;
		this.bMovingPos = false;
		this.filenameServerRequests = new ArrayList();
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		this.cell = cell;
		WorldReuserThread.instance.finished = false;
		this.chunksSwapB = new IsoChunk[ChunkGridWidth * ChunkGridWidth];
		this.chunksSwapA = new IsoChunk[ChunkGridWidth * ChunkGridWidth];
	}

	public void Dispose() {
		WorldReuserThread.instance.finished = true;
		IsoChunk.loadGridSquare.clear();
		this.chunksSwapA = null;
		this.chunksSwapB = null;
	}

	public static void setWorldStartPos(int int1, int int2) {
		SWorldX[IsoPlayer.getPlayerIndex()] = int1 / 10;
		SWorldY[IsoPlayer.getPlayerIndex()] = int2 / 10;
	}

	public void setInitialPos(int int1, int int2) {
		this.WorldX = int1;
		this.WorldY = int2;
		this.XMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMinTiles = -1;
		this.YMaxTiles = -1;
	}

	public void processAllLoadGridSquare() {
		for (IsoChunk chunk = (IsoChunk)IsoChunk.loadGridSquare.poll(); chunk != null; chunk = (IsoChunk)IsoChunk.loadGridSquare.poll()) {
			bSettingChunk.lock();
			try {
				boolean boolean1 = false;
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int1];
					if (!chunkMap.ignore && chunkMap.setChunkDirect(chunk, false)) {
						boolean1 = true;
					}
				}

				if (!boolean1) {
					WorldReuserThread.instance.addReuseChunk(chunk);
				} else {
					chunk.doLoadGridsquare();
				}
			} finally {
				bSettingChunk.unlock();
			}
		}
	}

	public void update() {
		int int1 = IsoChunk.loadGridSquare.size();
		if (int1 != 0) {
			int1 = 1 + int1 * 3 / ChunkGridWidth;
		}

		while (true) {
			IsoChunk chunk;
			int int2;
			while (int1 > 0) {
				chunk = null;
				chunk = (IsoChunk)IsoChunk.loadGridSquare.poll();
				if (chunk != null) {
					boolean boolean1 = false;
					for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
						IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int2];
						if (!chunkMap.ignore && chunkMap.setChunkDirect(chunk, false)) {
							boolean1 = true;
						}
					}

					if (!boolean1) {
						WorldReuserThread.instance.addReuseChunk(chunk);
						--int1;
						continue;
					}

					chunk.bLoaded = true;
					if (bSettingChunk.isLocked()) {
					}

					bSettingChunk.lock();
					try {
						chunk.doLoadGridsquare();
						if (GameClient.bClient) {
							List list = VehicleCache.vehicleGet(chunk.wx, chunk.wy);
							VehicleManager.instance.sendReqestGetFull(list);
						}
					} finally {
						bSettingChunk.unlock();
					}

					for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
						IsoPlayer player = IsoPlayer.players[int2];
						if (player != null) {
							player.dirtyRecalcGridStackTime = 20.0F;
						}
					}
				}

				--int1;
			}

			for (int int3 = 0; int3 < ChunkGridWidth; ++int3) {
				for (int2 = 0; int2 < ChunkGridWidth; ++int2) {
					chunk = this.getChunk(int2, int3);
					if (chunk != null) {
						chunk.update();
					}
				}
			}

			if (GameClient.bClient && this.checkVehiclesFrequency.Check()) {
				this.checkVehicles();
			}

			return;
		}
	}

	public void checkVehicles() {
		for (int int1 = 0; int1 < ChunkGridWidth; ++int1) {
			for (int int2 = 0; int2 < ChunkGridWidth; ++int2) {
				IsoChunk chunk = this.getChunk(int2, int1);
				if (chunk != null && chunk.bLoaded) {
					List list = VehicleCache.vehicleGet(chunk.wx, chunk.wy);
					if (list != null && chunk.vehicles.size() != list.size()) {
						for (int int3 = 0; int3 < list.size(); ++int3) {
							short short1 = ((VehicleCache)list.get(int3)).id;
							boolean boolean1 = false;
							for (int int4 = 0; int4 < chunk.vehicles.size(); ++int4) {
								if (((BaseVehicle)chunk.vehicles.get(int4)).getId() == short1) {
									boolean1 = true;
									break;
								}
							}

							if (!boolean1 && VehicleManager.instance.getVehicleByID(short1) == null) {
								VehicleManager.instance.sendReqestGetFull(short1);
							}
						}
					}
				}
			}
		}
	}

	public void checkIntegrity() {
		IsoWorld.instance.CurrentCell.ChunkMap[0].XMinTiles = -1;
		for (int int1 = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles(); int1 < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMaxTiles(); ++int1) {
			for (int int2 = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMinTiles(); int2 < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMaxTiles(); ++int2) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, 0);
				if (square != null && (square.getX() != int1 || square.getY() != int2)) {
					int int3 = int1 / 10;
					int int4 = int2 / 10;
					int3 -= IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMin();
					int4 -= IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMin();
					IsoChunk chunk = null;
					chunk = new IsoChunk(IsoWorld.instance.CurrentCell);
					chunk.refs.add(IsoWorld.instance.CurrentCell.ChunkMap[0]);
					WorldStreamer.instance.addJob(chunk, int1 / 10, int2 / 10, false);
					while (!chunk.bLoaded) {
						try {
							Thread.sleep(13L);
						} catch (InterruptedException interruptedException) {
							interruptedException.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void checkIntegrityThread() {
		IsoWorld.instance.CurrentCell.ChunkMap[0].XMinTiles = -1;
		for (int int1 = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles(); int1 < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMaxTiles(); ++int1) {
			for (int int2 = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMinTiles(); int2 < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMaxTiles(); ++int2) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, 0);
				if (square != null && (square.getX() != int1 || square.getY() != int2)) {
					int int3 = int1 / 10;
					int int4 = int2 / 10;
					int3 -= IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMin();
					int4 -= IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMin();
					IsoChunk chunk = new IsoChunk(IsoWorld.instance.CurrentCell);
					chunk.refs.add(IsoWorld.instance.CurrentCell.ChunkMap[0]);
					WorldStreamer.instance.addJobInstant(chunk, int1, int2, int1 / 10, int2 / 10);
				}

				if (square != null) {
				}
			}
		}
	}

	public void LoadChunk(int int1, int int2, int int3, int int4) {
		IsoChunk chunk = null;
		if (SharedChunks.containsKey((int1 << 16) + int2)) {
			chunk = (IsoChunk)SharedChunks.get((int1 << 16) + int2);
			chunk.setCache();
			this.setChunk(int3, int4, chunk);
			chunk.refs.add(this);
		} else {
			chunk = (IsoChunk)chunkStore.poll();
			if (chunk == null) {
				chunk = new IsoChunk(this.cell);
			}

			SharedChunks.put((int1 << 16) + int2, chunk);
			chunk.refs.add(this);
			WorldStreamer.instance.addJob(chunk, int1, int2, false);
		}
	}

	public void LoadChunkForLater(int int1, int int2, int int3, int int4) {
		if (IsoWorld.instance.getMetaGrid().isValidChunk(int1, int2)) {
			IsoChunk chunk = null;
			if (SharedChunks.containsKey((int1 << 16) + int2)) {
				chunk = (IsoChunk)SharedChunks.get((int1 << 16) + int2);
				if (!chunk.refs.contains(this)) {
					chunk.refs.add(this);
					chunk.lightCheck[this.PlayerID] = true;
				}

				if (!chunk.bLoaded) {
					return;
				}

				this.setChunk(int3, int4, chunk);
			} else {
				chunk = (IsoChunk)chunkStore.poll();
				if (chunk == null) {
					chunk = new IsoChunk(this.cell);
				}

				SharedChunks.put((int1 << 16) + int2, chunk);
				chunk.refs.add(this);
				WorldStreamer.instance.addJob(chunk, int1, int2, true);
			}
		}
	}

	public IsoChunk getChunkForGridSquare(int int1, int int2) {
		int1 -= (this.WorldX - ChunkGridWidth / 2) * 10;
		int2 -= (this.WorldY - ChunkGridWidth / 2) * 10;
		if (int1 >= 0 && int2 >= 0 && int1 < 300 && int2 < 300) {
			IsoChunk chunk = this.getChunk(int1 / 10, int2 / 10);
			return chunk;
		} else {
			return null;
		}
	}

	public void setGridSquare(IsoGridSquare square, int int1, int int2, int int3, int int4, int int5) {
		int3 -= int1 * 10;
		int4 -= int2 * 10;
		if (int3 >= 0 && int4 >= 0 && int3 < 300 && int4 < 300 && int5 >= 0 && int5 <= 16) {
			IsoChunk chunk = this.getChunk(int3 / 10, int4 / 10);
			if (chunk != null) {
				chunk.setSquare(int3 % 10, int4 % 10, int5, square);
			}
		}
	}

	public IsoChunk getChunkCurrent(int int1, int int2) {
		if (int1 >= 0 && int1 < ChunkGridWidth && int2 >= 0 && int2 < ChunkGridWidth) {
			return !this.bReadBufferA ? this.chunksSwapA[ChunkGridWidth * int2 + int1] : this.chunksSwapB[ChunkGridWidth * int2 + int1];
		} else {
			return null;
		}
	}

	public void setGridSquare(IsoGridSquare square, int int1, int int2, int int3) {
		assert square == null || square.x == int1 && square.y == int2 && square.z == int3;
		int1 -= (this.WorldX - ChunkGridWidth / 2) * 10;
		int2 -= (this.WorldY - ChunkGridWidth / 2) * 10;
		if (int1 >= 0 && int2 >= 0 && int1 < this.getWidthInTiles() && int2 < this.getWidthInTiles() && int3 >= 0 && int3 < 8) {
			IsoChunk chunk = this.getChunk(int1 / 10, int2 / 10);
			if (chunk != null) {
				if (int3 > chunk.maxLevel) {
					chunk.maxLevel = int3;
				}

				chunk.setSquare(int1 % 10, int2 % 10, int3, square);
			}
		}
	}

	public IsoGridSquare getGridSquare(int int1, int int2, int int3) {
		int1 -= (this.WorldX - ChunkGridWidth / 2) * 10;
		int2 -= (this.WorldY - ChunkGridWidth / 2) * 10;
		if (int1 >= 0 && int2 >= 0 && int1 < 300 && int2 < 300 && int3 >= 0 && int3 <= 8) {
			IsoChunk chunk = this.getChunk(int1 / 10, int2 / 10);
			return chunk == null ? null : chunk.getGridSquare(int1 % 10, int2 % 10, int3);
		} else {
			return null;
		}
	}

	public IsoGridSquare getGridSquareDirect(int int1, int int2, int int3) {
		if (int1 >= 0 && int1 < this.getWidthInTiles() && int2 >= 0 && int2 < this.getWidthInTiles()) {
			IsoChunk chunk = this.getChunk(int1 / 10, int2 / 10);
			return chunk == null ? null : chunk.getGridSquare(int1 % 10, int2 % 10, int3);
		} else {
			return null;
		}
	}

	public IsoChunk getChunk(int int1, int int2) {
		if (int1 >= 0 && int1 < ChunkGridWidth && int2 >= 0 && int2 < ChunkGridWidth) {
			return this.bReadBufferA ? this.chunksSwapA[ChunkGridWidth * int2 + int1] : this.chunksSwapB[ChunkGridWidth * int2 + int1];
		} else {
			return null;
		}
	}

	public void setChunk(int int1, int int2, IsoChunk chunk) {
		if (!this.bReadBufferA) {
			this.chunksSwapA[ChunkGridWidth * int2 + int1] = chunk;
		} else {
			this.chunksSwapB[ChunkGridWidth * int2 + int1] = chunk;
		}
	}

	public boolean setChunkDirect(IsoChunk chunk, boolean boolean1) {
		long long1 = System.nanoTime();
		if (boolean1) {
			bSettingChunk.lock();
			synchronized (LightingThread.instance.bHasLock) {
				if (LightingThread.instance.bHasLock) {
					LightingThread.instance.Interrupted = true;
					LightingThread.instance.lightingThread.interrupt();
				}

				bSettingChunkLighting.lock();
			}
		}

		long long2 = System.nanoTime();
		int int1 = chunk.wx - this.WorldX;
		int int2 = chunk.wy - this.WorldY;
		int1 += ChunkGridWidth / 2;
		int2 += ChunkGridWidth / 2;
		if (chunk.jobType == IsoChunk.JobType.Convert) {
			int1 = 0;
			int2 = 0;
		}

		if (!chunk.refs.isEmpty() && int1 >= 0 && int2 >= 0 && int1 < ChunkGridWidth && int2 < ChunkGridWidth) {
			try {
				if (this.bReadBufferA) {
					this.chunksSwapA[ChunkGridWidth * int2 + int1] = chunk;
				} else {
					this.chunksSwapB[ChunkGridWidth * int2 + int1] = chunk;
				}

				chunk.bLoaded = true;
				if (chunk.jobType == IsoChunk.JobType.None) {
					chunk.setCache();
					chunk.updateBuildings();
				}

				double double1 = (double)(System.nanoTime() - long2) / 1000000.0;
				double double2 = (double)(System.nanoTime() - long1) / 1000000.0;
				if (LightingThread.DebugLockTime && double2 > 10.0) {
					DebugLog.log("setChunkDirect time " + double1 + "/" + double2 + " ms");
				}
			} finally {
				if (boolean1) {
					bSettingChunkLighting.unlock();
					bSettingChunk.unlock();
				}
			}

			return true;
		} else {
			if (chunk.refs.contains(this)) {
				chunk.refs.remove(this);
				if (chunk.refs.isEmpty()) {
					SharedChunks.remove((chunk.wx << 16) + chunk.wy);
				}
			}

			if (boolean1) {
				bSettingChunkLighting.unlock();
				bSettingChunk.unlock();
			}

			return false;
		}
	}

	public void drawDebugChunkMap() {
		int int1 = 64;
		boolean boolean1 = false;
		for (int int2 = 0; int2 < ChunkGridWidth; ++int2) {
			int int3 = 0;
			for (int int4 = 0; int4 < ChunkGridWidth; ++int4) {
				int3 += 64;
				IsoChunk chunk = this.getChunk(int2, int4);
				if (chunk != null) {
					IsoGridSquare square = chunk.getGridSquare(0, 0, 0);
					if (square == null) {
						TextManager.instance.DrawString((double)int1, (double)int3, "wx:" + chunk.wx + " wy:" + chunk.wy);
					}
				}
			}

			int1 += 128;
		}
	}

	public void LoadLeft() {
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		TileAccessibilityWorker.instance.startingNew = true;
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		this.Left();
		WorldSimulation.instance.scrollGroundLeft(this.PlayerID);
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		for (int int1 = -(ChunkGridWidth / 2); int1 <= ChunkGridWidth / 2; ++int1) {
			this.LoadChunkForLater(this.WorldX - ChunkGridWidth / 2, this.WorldY + int1, 0, int1 + ChunkGridWidth / 2);
		}

		this.SwapChunkBuffers();
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		this.UpdateCellCache();
		LightingThread.instance.bMovedMap = true;
		LightingThread.instance.scrollLeft(this.PlayerID);
	}

	public void SwapChunkBuffers() {
		for (int int1 = 0; int1 < ChunkGridWidth * ChunkGridWidth; ++int1) {
			if (this.bReadBufferA) {
				this.chunksSwapA[int1] = null;
			} else {
				this.chunksSwapB[int1] = null;
			}
		}

		this.XMinTiles = this.XMaxTiles = -1;
		this.YMinTiles = this.YMaxTiles = -1;
		this.bReadBufferA = !this.bReadBufferA;
	}

	private void setChunk(int int1, IsoChunk chunk) {
		if (!this.bReadBufferA) {
			this.chunksSwapA[int1] = chunk;
		} else {
			this.chunksSwapB[int1] = chunk;
		}
	}

	private IsoChunk getChunk(int int1) {
		return this.bReadBufferA ? this.chunksSwapA[int1] : this.chunksSwapB[int1];
	}

	public void LoadRight() {
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		TileAccessibilityWorker.instance.startingNew = true;
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		this.Right();
		WorldSimulation.instance.scrollGroundRight(this.PlayerID);
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		for (int int1 = -(ChunkGridWidth / 2); int1 <= ChunkGridWidth / 2; ++int1) {
			this.LoadChunkForLater(this.WorldX + ChunkGridWidth / 2, this.WorldY + int1, ChunkGridWidth - 1, int1 + ChunkGridWidth / 2);
		}

		this.SwapChunkBuffers();
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		this.UpdateCellCache();
		LightingThread.instance.bMovedMap = true;
		LightingThread.instance.scrollRight(this.PlayerID);
	}

	public void LoadUp() {
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		TileAccessibilityWorker.instance.startingNew = true;
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		this.Up();
		WorldSimulation.instance.scrollGroundUp(this.PlayerID);
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		for (int int1 = -(ChunkGridWidth / 2); int1 <= ChunkGridWidth / 2; ++int1) {
			this.LoadChunkForLater(this.WorldX + int1, this.WorldY - ChunkGridWidth / 2, int1 + ChunkGridWidth / 2, 0);
		}

		this.SwapChunkBuffers();
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		this.UpdateCellCache();
		LightingThread.instance.bMovedMap = true;
		LightingThread.instance.scrollUp(this.PlayerID);
	}

	public void LoadDown() {
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		TileAccessibilityWorker.instance.startingNew = true;
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		this.Down();
		WorldSimulation.instance.scrollGroundDown(this.PlayerID);
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		for (int int1 = -(ChunkGridWidth / 2); int1 <= ChunkGridWidth / 2; ++int1) {
			this.LoadChunkForLater(this.WorldX + int1, this.WorldY + ChunkGridWidth / 2, int1 + ChunkGridWidth / 2, ChunkGridWidth - 1);
		}

		this.SwapChunkBuffers();
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		this.UpdateCellCache();
		LightingThread.instance.bMovedMap = true;
		LightingThread.instance.scrollDown(this.PlayerID);
	}

	public void UpdateCellCache() {
		if (IsoCell.ENABLE_SQUARE_CACHE) {
			int int1 = this.getWidthInTiles();
			for (int int2 = 0; int2 < int1; ++int2) {
				for (int int3 = 0; int3 < int1; ++int3) {
					for (int int4 = 0; int4 < 8; ++int4) {
						IsoGridSquare square = this.getGridSquare(int2 + this.getWorldXMinTiles(), int3 + this.getWorldYMinTiles(), int4);
						IsoWorld.instance.CurrentCell.setCacheGridSquareLocal(int2, int3, int4, square, this.PlayerID);
					}
				}
			}
		}
	}

	void Up() {
		for (int int1 = 0; int1 < ChunkGridWidth; ++int1) {
			for (int int2 = ChunkGridWidth - 1; int2 > 0; --int2) {
				IsoChunk chunk = this.getChunk(int1, int2);
				if (chunk == null && int2 == ChunkGridWidth - 1) {
					int int3 = this.WorldX - ChunkGridWidth / 2 + int1;
					int int4 = this.WorldY - ChunkGridWidth / 2 + int2;
					chunk = (IsoChunk)SharedChunks.get((int3 << 16) + int4);
					if (chunk != null) {
						if (chunk.refs.contains(this)) {
							chunk.refs.remove(this);
							if (chunk.refs.isEmpty()) {
								SharedChunks.remove((chunk.wx << 16) + chunk.wy);
							}
						}

						chunk = null;
					}
				}

				if (chunk != null && int2 == ChunkGridWidth - 1) {
					chunk.refs.remove(this);
					if (chunk.refs.isEmpty()) {
						SharedChunks.remove((chunk.wx << 16) + chunk.wy);
						chunk.removeFromWorld();
						ChunkSaveWorker.instance.Add(chunk);
					}
				}

				this.setChunk(int1, int2, this.getChunk(int1, int2 - 1));
			}

			this.setChunk(int1, 0, (IsoChunk)null);
		}

		--this.WorldY;
	}

	void Down() {
		for (int int1 = 0; int1 < ChunkGridWidth; ++int1) {
			for (int int2 = 0; int2 < ChunkGridWidth - 1; ++int2) {
				IsoChunk chunk = this.getChunk(int1, int2);
				if (chunk == null && int2 == 0) {
					int int3 = this.WorldX - ChunkGridWidth / 2 + int1;
					int int4 = this.WorldY - ChunkGridWidth / 2 + int2;
					chunk = (IsoChunk)SharedChunks.get((int3 << 16) + int4);
					if (chunk != null) {
						if (chunk.refs.contains(this)) {
							chunk.refs.remove(this);
							if (chunk.refs.isEmpty()) {
								SharedChunks.remove((chunk.wx << 16) + chunk.wy);
							}
						}

						chunk = null;
					}
				}

				if (chunk != null && int2 == 0) {
					chunk.refs.remove(this);
					if (chunk.refs.isEmpty()) {
						SharedChunks.remove((chunk.wx << 16) + chunk.wy);
						chunk.removeFromWorld();
						ChunkSaveWorker.instance.Add(chunk);
					}
				}

				this.setChunk(int1, int2, this.getChunk(int1, int2 + 1));
			}

			this.setChunk(int1, ChunkGridWidth - 1, (IsoChunk)null);
		}

		++this.WorldY;
	}

	void Left() {
		for (int int1 = 0; int1 < ChunkGridWidth; ++int1) {
			for (int int2 = ChunkGridWidth - 1; int2 > 0; --int2) {
				IsoChunk chunk = this.getChunk(int2, int1);
				if (chunk == null && int2 == ChunkGridWidth - 1) {
					int int3 = this.WorldX - ChunkGridWidth / 2 + int2;
					int int4 = this.WorldY - ChunkGridWidth / 2 + int1;
					chunk = (IsoChunk)SharedChunks.get((int3 << 16) + int4);
					if (chunk != null) {
						if (chunk.refs.contains(this)) {
							chunk.refs.remove(this);
							if (chunk.refs.isEmpty()) {
								SharedChunks.remove((chunk.wx << 16) + chunk.wy);
							}
						}

						chunk = null;
					}
				}

				if (chunk != null && int2 == ChunkGridWidth - 1) {
					chunk.refs.remove(this);
					if (chunk.refs.isEmpty()) {
						SharedChunks.remove((chunk.wx << 16) + chunk.wy);
						chunk.removeFromWorld();
						ChunkSaveWorker.instance.Add(chunk);
					}
				}

				this.setChunk(int2, int1, this.getChunk(int2 - 1, int1));
			}

			this.setChunk(0, int1, (IsoChunk)null);
		}

		--this.WorldX;
	}

	public void Right() {
		for (int int1 = 0; int1 < ChunkGridWidth; ++int1) {
			for (int int2 = 0; int2 < ChunkGridWidth - 1; ++int2) {
				IsoChunk chunk = this.getChunk(int2, int1);
				if (chunk == null && int2 == 0) {
					int int3 = this.WorldX - ChunkGridWidth / 2 + int2;
					int int4 = this.WorldY - ChunkGridWidth / 2 + int1;
					chunk = (IsoChunk)SharedChunks.get((int3 << 16) + int4);
					if (chunk != null) {
						if (chunk.refs.contains(this)) {
							chunk.refs.remove(this);
							if (chunk.refs.isEmpty()) {
								SharedChunks.remove((chunk.wx << 16) + chunk.wy);
							}
						}

						chunk = null;
					}
				}

				if (chunk != null && int2 == 0) {
					chunk.refs.remove(this);
					if (chunk.refs.isEmpty()) {
						SharedChunks.remove((chunk.wx << 16) + chunk.wy);
						chunk.removeFromWorld();
						ChunkSaveWorker.instance.Add(chunk);
					}
				}

				this.setChunk(int2, int1, this.getChunk(int2 + 1, int1));
			}

			this.setChunk(ChunkGridWidth - 1, int1, (IsoChunk)null);
		}

		++this.WorldX;
	}

	public int getWorldXMin() {
		return this.WorldX - ChunkGridWidth / 2;
	}

	public int getWorldYMin() {
		return this.WorldY - ChunkGridWidth / 2;
	}

	public void ProcessChunkPos(IsoGameCharacter gameCharacter) {
		boolean boolean1 = false;
		int int1 = (int)gameCharacter.getX();
		int int2 = (int)gameCharacter.getY();
		int int3 = (int)gameCharacter.getZ();
		if (IsoPlayer.instance != null && IsoPlayer.instance.getVehicle() != null) {
			IsoPlayer player = IsoPlayer.instance;
			BaseVehicle baseVehicle = player.getVehicle();
			float float1 = baseVehicle.getCurrentSpeedKmHour() / 5.0F;
			int1 += Math.round(player.angle.x * float1);
			int2 += Math.round(player.angle.y * float1);
		}

		int1 /= 10;
		int2 /= 10;
		if (int1 != this.WorldX || int2 != this.WorldY) {
			long long1 = System.nanoTime();
			double double1 = 0.0;
			bSettingChunk.lock();
			synchronized (LightingThread.instance.bHasLock) {
				if (LightingThread.instance.bHasLock) {
					LightingThread.instance.Interrupted = true;
					LightingThread.instance.lightingThread.interrupt();
				}

				bSettingChunkLighting.lock();
			}

			long long2 = System.nanoTime();
			try {
				if (Math.abs(int1 - this.WorldX) < ChunkGridWidth && Math.abs(int2 - this.WorldY) < ChunkGridWidth) {
					if (int1 != this.WorldX) {
						if (int1 < this.WorldX) {
							this.LoadLeft();
						} else {
							this.LoadRight();
						}

						this.bMovingPos = false;
					} else if (int2 != this.WorldY) {
						if (int2 < this.WorldY) {
							this.LoadUp();
						} else {
							this.LoadDown();
						}

						this.bMovingPos = false;
					}
				} else {
					if (LightingJNI.init) {
						LightingJNI.teleport(this.PlayerID, int1 - ChunkGridWidth / 2, int2 - ChunkGridWidth / 2);
					}

					this.Unload();
					IsoPlayer player2 = IsoPlayer.players[this.PlayerID];
					player2.removeFromSquare();
					player2.square = null;
					this.WorldX = int1;
					this.WorldY = int2;
					WorldSimulation.instance.activateChunkMap(this.PlayerID);
					int int4 = this.WorldX - ChunkGridWidth / 2;
					int int5 = this.WorldY - ChunkGridWidth / 2;
					int int6 = this.WorldX + ChunkGridWidth / 2;
					int int7 = this.WorldY + ChunkGridWidth / 2;
					for (int int8 = int4; int8 <= int6; ++int8) {
						for (int int9 = int5; int9 <= int7; ++int9) {
							this.LoadChunkForLater(int8, int9, int8 - int4, int9 - int5);
						}
					}

					this.SwapChunkBuffers();
					this.UpdateCellCache();
					LightingThread.instance.bMovedMap = true;
					if (!IsoWorld.instance.getCell().getObjectList().contains(player2)) {
						IsoWorld.instance.getCell().getAddList().add(player2);
					}
				}
			} finally {
				bSettingChunkLighting.unlock();
				bSettingChunk.unlock();
			}

			double1 = (double)(System.nanoTime() - long2) / 1000000.0;
			double double2 = (double)(System.nanoTime() - long1) / 1000000.0;
			if (LightingThread.DebugLockTime && double2 > 10.0) {
				DebugLog.log("ProcessChunkPos time " + double1 + "/" + double2 + " ms");
			}
		}
	}

	private void SendRequestForZip(int int1, int int2) {
		if (this.WorldX > int1) {
		}
	}

	public IsoRoom getRoom(int int1) {
		return null;
	}

	public int getWidthInTiles() {
		return ChunkWidthInTiles;
	}

	public int getWorldXMinTiles() {
		if (this.XMinTiles != -1) {
			return this.XMinTiles;
		} else {
			this.XMinTiles = this.getWorldXMin() * 10;
			return this.XMinTiles;
		}
	}

	public int getWorldYMinTiles() {
		if (this.YMinTiles != -1) {
			return this.YMinTiles;
		} else {
			this.YMinTiles = this.getWorldYMin() * 10;
			return this.YMinTiles;
		}
	}

	public int getWorldXMaxTiles() {
		if (this.XMaxTiles != -1) {
			return this.XMaxTiles;
		} else {
			this.XMaxTiles = this.getWorldXMin() * 10 + this.getWidthInTiles();
			return this.XMaxTiles;
		}
	}

	public int getWorldYMaxTiles() {
		if (this.YMaxTiles != -1) {
			return this.YMaxTiles;
		} else {
			this.YMaxTiles = this.getWorldYMin() * 10 + this.getWidthInTiles();
			return this.YMaxTiles;
		}
	}

	public void Save() {
		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < ChunkGridWidth; ++int1) {
				for (int int2 = 0; int2 < ChunkGridWidth; ++int2) {
					IsoChunk chunk = this.getChunk(int1, int2);
					if (chunk != null && !saveList.contains(chunk)) {
						try {
							chunk.Save(true);
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}
					}
				}
			}
		}
	}

	public static void DoSave() {
		for (int int1 = 0; int1 < saveList.size(); ++int1) {
		}
	}

	public void renderBloodForChunks(int int1) {
		if (!((float)int1 > IsoCamera.CamCharacter.z)) {
			if (Core.OptionBloodDecals != 0) {
				float float1 = (float)GameTime.getInstance().getWorldAgeHours();
				int int2;
				if (splatByType == null) {
					splatByType = new ArrayList();
					for (int2 = 0; int2 < IsoFloorBloodSplat.FloorBloodTypes.length; ++int2) {
						splatByType.add(new ArrayList());
					}
				}

				for (int2 = 0; int2 < IsoFloorBloodSplat.FloorBloodTypes.length; ++int2) {
					((ArrayList)splatByType.get(int2)).clear();
				}

				for (int2 = 0; int2 < ChunkGridWidth; ++int2) {
					for (int int3 = 0; int3 < ChunkGridWidth; ++int3) {
						IsoChunk chunk = this.getChunk(int2, int3);
						if (chunk != null) {
							int int4;
							IsoFloorBloodSplat floorBloodSplat;
							for (int4 = 0; int4 < chunk.FloorBloodSplatsFade.size(); ++int4) {
								floorBloodSplat = (IsoFloorBloodSplat)chunk.FloorBloodSplatsFade.get(int4);
								if ((floorBloodSplat.index < 1 || floorBloodSplat.index > 10 || IsoChunk.renderByIndex[Core.OptionBloodDecals - 1][floorBloodSplat.index - 1] != 0) && (int)floorBloodSplat.z == int1 && floorBloodSplat.Type >= 0 && floorBloodSplat.Type < IsoFloorBloodSplat.FloorBloodTypes.length) {
									floorBloodSplat.chunk = chunk;
									((ArrayList)splatByType.get(floorBloodSplat.Type)).add(floorBloodSplat);
								}
							}

							if (!chunk.FloorBloodSplats.isEmpty()) {
								for (int4 = 0; int4 < chunk.FloorBloodSplats.size(); ++int4) {
									floorBloodSplat = (IsoFloorBloodSplat)chunk.FloorBloodSplats.get(int4);
									if ((floorBloodSplat.index < 1 || floorBloodSplat.index > 10 || IsoChunk.renderByIndex[Core.OptionBloodDecals - 1][floorBloodSplat.index - 1] != 0) && (int)floorBloodSplat.z == int1 && floorBloodSplat.Type >= 0 && floorBloodSplat.Type < IsoFloorBloodSplat.FloorBloodTypes.length) {
										floorBloodSplat.chunk = chunk;
										((ArrayList)splatByType.get(floorBloodSplat.Type)).add(floorBloodSplat);
									}
								}
							}
						}
					}
				}

				for (int2 = 0; int2 < splatByType.size(); ++int2) {
					ArrayList arrayList = (ArrayList)splatByType.get(int2);
					if (!arrayList.isEmpty()) {
						String string = IsoFloorBloodSplat.FloorBloodTypes[int2];
						IsoSprite sprite = null;
						if (!IsoFloorBloodSplat.SpriteMap.containsKey(string)) {
							IsoSprite sprite2 = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
							sprite2.LoadFramesPageSimple(string, string, string, string);
							IsoFloorBloodSplat.SpriteMap.put(string, sprite2);
							sprite = sprite2;
						} else {
							sprite = (IsoSprite)IsoFloorBloodSplat.SpriteMap.get(string);
						}

						for (int int5 = 0; int5 < arrayList.size(); ++int5) {
							IsoFloorBloodSplat floorBloodSplat2 = (IsoFloorBloodSplat)arrayList.get(int5);
							inf.r = 1.0F;
							inf.g = 1.0F;
							inf.b = 1.0F;
							inf.a = 0.27F;
							float float2 = (floorBloodSplat2.x + floorBloodSplat2.y / floorBloodSplat2.x) * (float)(floorBloodSplat2.Type + 1);
							float float3 = float2 * floorBloodSplat2.x / floorBloodSplat2.y * (float)(floorBloodSplat2.Type + 1) / (float2 + floorBloodSplat2.y);
							float float4 = float3 * float2 * float3 * floorBloodSplat2.x / (floorBloodSplat2.y + 2.0F);
							float2 *= 42367.543F;
							float3 *= 6367.123F;
							float4 *= 23367.133F;
							float2 %= 1000.0F;
							float3 %= 1000.0F;
							float4 %= 1000.0F;
							float2 /= 1000.0F;
							float3 /= 1000.0F;
							float4 /= 1000.0F;
							if (float2 > 0.25F) {
								float2 = 0.25F;
							}

							ColorInfo colorInfo = inf;
							colorInfo.r -= float2 * 2.0F;
							colorInfo = inf;
							colorInfo.g -= float2 * 2.0F;
							colorInfo = inf;
							colorInfo.b -= float2 * 2.0F;
							colorInfo = inf;
							colorInfo.r += float3 / 3.0F;
							colorInfo = inf;
							colorInfo.g -= float4 / 3.0F;
							colorInfo = inf;
							colorInfo.b -= float4 / 3.0F;
							float float5 = float1 - floorBloodSplat2.worldAge;
							if (float5 >= 0.0F && float5 < 72.0F) {
								float float6 = 1.0F - float5 / 72.0F;
								colorInfo = inf;
								colorInfo.r *= 0.2F + float6 * 0.8F;
								colorInfo = inf;
								colorInfo.g *= 0.2F + float6 * 0.8F;
								colorInfo = inf;
								colorInfo.b *= 0.2F + float6 * 0.8F;
								colorInfo = inf;
								colorInfo.a *= 0.25F + float6 * 0.75F;
								if (floorBloodSplat2.fade > 0) {
									colorInfo = inf;
									colorInfo.a *= (float)floorBloodSplat2.fade / ((float)PerformanceSettings.LockFPS * 5.0F);
									if (--floorBloodSplat2.fade == 0) {
										floorBloodSplat2.chunk.FloorBloodSplatsFade.remove(floorBloodSplat2);
									}
								}
							} else {
								colorInfo = inf;
								colorInfo.r *= 0.2F;
								colorInfo = inf;
								colorInfo.g *= 0.2F;
								colorInfo = inf;
								colorInfo.b *= 0.2F;
								colorInfo = inf;
								colorInfo.a *= 0.25F;
								if (floorBloodSplat2.fade > 0) {
									colorInfo = inf;
									colorInfo.a *= (float)floorBloodSplat2.fade / ((float)PerformanceSettings.LockFPS * 5.0F);
									if (--floorBloodSplat2.fade == 0) {
										floorBloodSplat2.chunk.FloorBloodSplatsFade.remove(floorBloodSplat2);
									}
								}
							}

							sprite.renderBloodSplat((float)(floorBloodSplat2.chunk.wx * 10) + floorBloodSplat2.x, (float)(floorBloodSplat2.chunk.wy * 10) + floorBloodSplat2.y, floorBloodSplat2.z, inf);
						}
					}
				}
			}
		}
	}

	public void copy(IsoChunkMap chunkMap) {
		IsoChunkMap chunkMap2 = this;
		this.WorldX = chunkMap.WorldX;
		this.WorldY = chunkMap.WorldY;
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		for (int int1 = 0; int1 < ChunkGridWidth * ChunkGridWidth; ++int1) {
			chunkMap2.bReadBufferA = chunkMap.bReadBufferA;
			if (chunkMap2.bReadBufferA) {
				if (chunkMap.chunksSwapA[int1] != null) {
					chunkMap.chunksSwapA[int1].refs.add(chunkMap2);
					chunkMap2.chunksSwapA[int1] = chunkMap.chunksSwapA[int1];
				}
			} else if (chunkMap.chunksSwapB[int1] != null) {
				chunkMap.chunksSwapB[int1].refs.add(chunkMap2);
				chunkMap2.chunksSwapB[int1] = chunkMap.chunksSwapB[int1];
			}
		}
	}

	public void Unload() {
		for (int int1 = 0; int1 < ChunkGridWidth; ++int1) {
			for (int int2 = 0; int2 < ChunkGridWidth; ++int2) {
				IsoChunk chunk = this.getChunk(int2, int1);
				if (chunk != null) {
					if (chunk.refs.contains(this)) {
						chunk.refs.remove(this);
						if (chunk.refs.isEmpty()) {
							SharedChunks.remove((chunk.wx << 16) + chunk.wy);
							chunk.removeFromWorld();
							ChunkSaveWorker.instance.Add(chunk);
						}
					}

					this.chunksSwapA[int1 * ChunkGridWidth + int2] = null;
					this.chunksSwapB[int1 * ChunkGridWidth + int2] = null;
				}
			}
		}

		WorldSimulation.instance.deactivateChunkMap(this.PlayerID);
		this.XMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMinTiles = -1;
		this.YMaxTiles = -1;
		if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
			IsoWorld.instance.CurrentCell.clearCacheGridSquare(this.PlayerID);
		}
	}

	static  {
		ChunkGridWidth = StartChunkGridWidth;
		MPWorldXA = 0;
		MPWorldYA = 0;
		MPWorldZA = 0;
		ChunkWidthInTiles = 10 * ChunkGridWidth;
		WorldCellX = 10;
		WorldCellY = 7;
		PosX = 259;
		PosY = 209;
		WorldXA = 11702;
		WorldYA = 6896;
		WorldZA = 0;
		SWorldX = new int[4];
		SWorldY = new int[4];
		chunkStore = new ConcurrentLinkedQueue();
		bSettingChunk = new ReentrantLock(true);
		bSettingChunkLighting = new ReentrantLock(true);
		SharedChunks = new HashMap();
		saveList = new ArrayList();
		inf = new ColorInfo();
	}
}
