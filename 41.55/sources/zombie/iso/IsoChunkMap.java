package zombie.iso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.physics.WorldSimulation;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.areas.IsoRoom;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.TextManager;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleCache;
import zombie.vehicles.VehicleManager;


public final class IsoChunkMap {
	public static final int LEVELS = 8;
	public static final int ChunksPerWidth = 10;
	public static final HashMap SharedChunks = new HashMap();
	public static int MPWorldXA = 0;
	public static int MPWorldYA = 0;
	public static int MPWorldZA = 0;
	public static int WorldXA = 11702;
	public static int WorldYA = 6896;
	public static int WorldZA = 0;
	public static final int[] SWorldX = new int[4];
	public static final int[] SWorldY = new int[4];
	public static final ConcurrentLinkedQueue chunkStore = new ConcurrentLinkedQueue();
	public static final ReentrantLock bSettingChunk = new ReentrantLock(true);
	private static int StartChunkGridWidth = 13;
	public static int ChunkGridWidth;
	public static int ChunkWidthInTiles;
	private static final ColorInfo inf;
	private static final ArrayList saveList;
	private static final ArrayList splatByType;
	public int PlayerID = 0;
	public boolean ignore = false;
	public int WorldX;
	public int WorldY;
	public final ArrayList filenameServerRequests;
	protected IsoChunk[] chunksSwapB;
	protected IsoChunk[] chunksSwapA;
	boolean bReadBufferA;
	int XMinTiles;
	int YMinTiles;
	int XMaxTiles;
	int YMaxTiles;
	private IsoCell cell;
	private final UpdateLimit checkVehiclesFrequency;

	public IsoChunkMap(IsoCell cell) {
		this.WorldX = tileToChunk(WorldXA);
		this.WorldY = tileToChunk(WorldYA);
		this.filenameServerRequests = new ArrayList();
		this.bReadBufferA = true;
		this.XMinTiles = -1;
		this.YMinTiles = -1;
		this.XMaxTiles = -1;
		this.YMaxTiles = -1;
		this.checkVehiclesFrequency = new UpdateLimit(3000L);
		this.cell = cell;
		WorldReuserThread.instance.finished = false;
		this.chunksSwapB = new IsoChunk[ChunkGridWidth * ChunkGridWidth];
		this.chunksSwapA = new IsoChunk[ChunkGridWidth * ChunkGridWidth];
	}

	public static void CalcChunkWidth() {
		if (DebugOptions.instance.WorldChunkMap5x5.getValue()) {
			ChunkGridWidth = 5;
			ChunkWidthInTiles = ChunkGridWidth * 10;
		} else {
			float float1 = (float)Core.getInstance().getScreenWidth();
			float float2 = float1 / 1920.0F;
			if (float2 > 1.0F) {
				float2 = 1.0F;
			}

			ChunkGridWidth = (int)((double)((float)StartChunkGridWidth * float2) * 1.5);
			if (ChunkGridWidth / 2 * 2 == ChunkGridWidth) {
				++ChunkGridWidth;
			}

			ChunkWidthInTiles = ChunkGridWidth * 10;
		}
	}

	public static void setWorldStartPos(int int1, int int2) {
		SWorldX[IsoPlayer.getPlayerIndex()] = tileToChunk(int1);
		SWorldY[IsoPlayer.getPlayerIndex()] = tileToChunk(int2);
	}

	public void Dispose() {
		WorldReuserThread.instance.finished = true;
		IsoChunk.loadGridSquare.clear();
		this.chunksSwapA = null;
		this.chunksSwapB = null;
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

			if (this.checkVehiclesFrequency.Check() && GameClient.bClient) {
				this.checkVehicles();
			}

			return;
		}
	}

	private void checkVehicles() {
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

	public IsoChunk LoadChunkForLater(int int1, int int2, int int3, int int4) {
		if (!IsoWorld.instance.getMetaGrid().isValidChunk(int1, int2)) {
			return null;
		} else {
			IsoChunk chunk;
			if (SharedChunks.containsKey((int1 << 16) + int2)) {
				chunk = (IsoChunk)SharedChunks.get((int1 << 16) + int2);
				if (!chunk.refs.contains(this)) {
					chunk.refs.add(this);
					chunk.lightCheck[this.PlayerID] = true;
				}

				if (!chunk.bLoaded) {
					return chunk;
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

			return chunk;
		}
	}

	public IsoChunk getChunkForGridSquare(int int1, int int2) {
		int1 = this.gridSquareToTileX(int1);
		int2 = this.gridSquareToTileY(int2);
		return !this.isTileOutOfrange(int1) && !this.isTileOutOfrange(int2) ? this.getChunk(tileToChunk(int1), tileToChunk(int2)) : null;
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
		int int4 = this.gridSquareToTileX(int1);
		int int5 = this.gridSquareToTileY(int2);
		if (!this.isTileOutOfrange(int4) && !this.isTileOutOfrange(int5) && !this.isGridSquareOutOfRangeZ(int3)) {
			IsoChunk chunk = this.getChunk(tileToChunk(int4), tileToChunk(int5));
			if (chunk != null) {
				if (int3 > chunk.maxLevel) {
					chunk.maxLevel = int3;
				}

				chunk.setSquare(this.tileToGridSquare(int4), this.tileToGridSquare(int5), int3, square);
			}
		}
	}

	public IsoGridSquare getGridSquare(int int1, int int2, int int3) {
		int1 = this.gridSquareToTileX(int1);
		int2 = this.gridSquareToTileY(int2);
		return this.getGridSquareDirect(int1, int2, int3);
	}

	public IsoGridSquare getGridSquareDirect(int int1, int int2, int int3) {
		if (!this.isTileOutOfrange(int1) && !this.isTileOutOfrange(int2) && !this.isGridSquareOutOfRangeZ(int3)) {
			IsoChunk chunk = this.getChunk(tileToChunk(int1), tileToChunk(int2));
			return chunk == null ? null : chunk.getGridSquare(this.tileToGridSquare(int1), this.tileToGridSquare(int2), int3);
		} else {
			return null;
		}
	}

	private int tileToGridSquare(int int1) {
		return int1 % 10;
	}

	private static int tileToChunk(int int1) {
		return int1 / 10;
	}

	private boolean isTileOutOfrange(int int1) {
		return int1 < 0 || int1 >= this.getWidthInTiles();
	}

	private boolean isGridSquareOutOfRangeZ(int int1) {
		return int1 < 0 || int1 >= 8;
	}

	private int gridSquareToTileX(int int1) {
		int int2 = int1 - (this.WorldX - ChunkGridWidth / 2) * 10;
		return int2;
	}

	private int gridSquareToTileY(int int1) {
		int int2 = int1 - (this.WorldY - ChunkGridWidth / 2) * 10;
		return int2;
	}

	public IsoChunk getChunk(int int1, int int2) {
		if (int1 >= 0 && int1 < ChunkGridWidth && int2 >= 0 && int2 < ChunkGridWidth) {
			return this.bReadBufferA ? this.chunksSwapA[ChunkGridWidth * int2 + int1] : this.chunksSwapB[ChunkGridWidth * int2 + int1];
		} else {
			return null;
		}
	}

	private void setChunk(int int1, int int2, IsoChunk chunk) {
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

	private void LoadLeft() {
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

	private void LoadRight() {
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
		LightingThread.instance.scrollRight(this.PlayerID);
	}

	private void LoadUp() {
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
		LightingThread.instance.scrollUp(this.PlayerID);
	}

	private void LoadDown() {
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
		LightingThread.instance.scrollDown(this.PlayerID);
	}

	private void UpdateCellCache() {
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

	private void Up() {
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

	private void Down() {
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

	private void Left() {
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

	private void Right() {
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
		int int1 = (int)gameCharacter.getX();
		int int2 = (int)gameCharacter.getY();
		int int3 = (int)gameCharacter.getZ();
		if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().getVehicle() != null) {
			IsoPlayer player = IsoPlayer.getInstance();
			BaseVehicle baseVehicle = player.getVehicle();
			float float1 = baseVehicle.getCurrentSpeedKmHour() / 5.0F;
			int1 += Math.round(player.getForwardDirection().x * float1);
			int2 += Math.round(player.getForwardDirection().y * float1);
		}

		int1 /= 10;
		int2 /= 10;
		if (int1 != this.WorldX || int2 != this.WorldY) {
			long long1 = System.nanoTime();
			double double1 = 0.0;
			bSettingChunk.lock();
			long long2 = System.nanoTime();
			try {
				if (Math.abs(int1 - this.WorldX) < ChunkGridWidth && Math.abs(int2 - this.WorldY) < ChunkGridWidth) {
					if (int1 != this.WorldX) {
						if (int1 < this.WorldX) {
							this.LoadLeft();
						} else {
							this.LoadRight();
						}
					} else if (int2 != this.WorldY) {
						if (int2 < this.WorldY) {
							this.LoadUp();
						} else {
							this.LoadDown();
						}
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
					if (!IsoWorld.instance.getCell().getObjectList().contains(player2)) {
						IsoWorld.instance.getCell().getAddList().add(player2);
					}
				}
			} finally {
				bSettingChunk.unlock();
			}

			double1 = (double)(System.nanoTime() - long2) / 1000000.0;
			double double2 = (double)(System.nanoTime() - long1) / 1000000.0;
			if (LightingThread.DebugLockTime && double2 > 10.0) {
				DebugLog.log("ProcessChunkPos time " + double1 + "/" + double2 + " ms");
			}
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

	public void renderBloodForChunks(int int1) {
		if (DebugOptions.instance.Terrain.RenderTiles.BloodDecals.getValue()) {
			if (!((float)int1 > IsoCamera.CamCharacter.z)) {
				if (Core.OptionBloodDecals != 0) {
					float float1 = (float)GameTime.getInstance().getWorldAgeHours();
					int int2 = IsoCamera.frameState.playerIndex;
					int int3;
					for (int3 = 0; int3 < IsoFloorBloodSplat.FloorBloodTypes.length; ++int3) {
						((ArrayList)splatByType.get(int3)).clear();
					}

					for (int3 = 0; int3 < ChunkGridWidth; ++int3) {
						for (int int4 = 0; int4 < ChunkGridWidth; ++int4) {
							IsoChunk chunk = this.getChunk(int3, int4);
							if (chunk != null) {
								int int5;
								IsoFloorBloodSplat floorBloodSplat;
								for (int5 = 0; int5 < chunk.FloorBloodSplatsFade.size(); ++int5) {
									floorBloodSplat = (IsoFloorBloodSplat)chunk.FloorBloodSplatsFade.get(int5);
									if ((floorBloodSplat.index < 1 || floorBloodSplat.index > 10 || IsoChunk.renderByIndex[Core.OptionBloodDecals - 1][floorBloodSplat.index - 1] != 0) && (int)floorBloodSplat.z == int1 && floorBloodSplat.Type >= 0 && floorBloodSplat.Type < IsoFloorBloodSplat.FloorBloodTypes.length) {
										floorBloodSplat.chunk = chunk;
										((ArrayList)splatByType.get(floorBloodSplat.Type)).add(floorBloodSplat);
									}
								}

								if (!chunk.FloorBloodSplats.isEmpty()) {
									for (int5 = 0; int5 < chunk.FloorBloodSplats.size(); ++int5) {
										floorBloodSplat = (IsoFloorBloodSplat)chunk.FloorBloodSplats.get(int5);
										if ((floorBloodSplat.index < 1 || floorBloodSplat.index > 10 || IsoChunk.renderByIndex[Core.OptionBloodDecals - 1][floorBloodSplat.index - 1] != 0) && (int)floorBloodSplat.z == int1 && floorBloodSplat.Type >= 0 && floorBloodSplat.Type < IsoFloorBloodSplat.FloorBloodTypes.length) {
											floorBloodSplat.chunk = chunk;
											((ArrayList)splatByType.get(floorBloodSplat.Type)).add(floorBloodSplat);
										}
									}
								}
							}
						}
					}

					for (int3 = 0; int3 < splatByType.size(); ++int3) {
						ArrayList arrayList = (ArrayList)splatByType.get(int3);
						if (!arrayList.isEmpty()) {
							String string = IsoFloorBloodSplat.FloorBloodTypes[int3];
							IsoSprite sprite = null;
							if (!IsoFloorBloodSplat.SpriteMap.containsKey(string)) {
								IsoSprite sprite2 = IsoSprite.CreateSprite(IsoSpriteManager.instance);
								sprite2.LoadFramesPageSimple(string, string, string, string);
								IsoFloorBloodSplat.SpriteMap.put(string, sprite2);
								sprite = sprite2;
							} else {
								sprite = (IsoSprite)IsoFloorBloodSplat.SpriteMap.get(string);
							}

							for (int int6 = 0; int6 < arrayList.size(); ++int6) {
								IsoFloorBloodSplat floorBloodSplat2 = (IsoFloorBloodSplat)arrayList.get(int6);
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
								} else {
									colorInfo = inf;
									colorInfo.r *= 0.2F;
									colorInfo = inf;
									colorInfo.g *= 0.2F;
									colorInfo = inf;
									colorInfo.b *= 0.2F;
									colorInfo = inf;
									colorInfo.a *= 0.25F;
								}

								if (floorBloodSplat2.fade > 0) {
									colorInfo = inf;
									colorInfo.a *= (float)floorBloodSplat2.fade / ((float)PerformanceSettings.getLockFPS() * 5.0F);
									if (--floorBloodSplat2.fade == 0) {
										floorBloodSplat2.chunk.FloorBloodSplatsFade.remove(floorBloodSplat2);
									}
								}

								IsoGridSquare square = floorBloodSplat2.chunk.getGridSquare((int)floorBloodSplat2.x, (int)floorBloodSplat2.y, (int)floorBloodSplat2.z);
								if (square != null) {
									int int7 = square.getVertLight(0, int2);
									int int8 = square.getVertLight(1, int2);
									int int9 = square.getVertLight(2, int2);
									int int10 = square.getVertLight(3, int2);
									float float7 = Color.getRedChannelFromABGR(int7);
									float float8 = Color.getGreenChannelFromABGR(int7);
									float float9 = Color.getBlueChannelFromABGR(int7);
									float float10 = Color.getRedChannelFromABGR(int8);
									float float11 = Color.getGreenChannelFromABGR(int8);
									float float12 = Color.getBlueChannelFromABGR(int8);
									float float13 = Color.getRedChannelFromABGR(int9);
									float float14 = Color.getGreenChannelFromABGR(int9);
									float float15 = Color.getBlueChannelFromABGR(int9);
									float float16 = Color.getRedChannelFromABGR(int10);
									float float17 = Color.getGreenChannelFromABGR(int10);
									float float18 = Color.getBlueChannelFromABGR(int10);
									colorInfo = inf;
									colorInfo.r *= (float7 + float10 + float13 + float16) / 4.0F;
									colorInfo = inf;
									colorInfo.g *= (float8 + float11 + float14 + float17) / 4.0F;
									colorInfo = inf;
									colorInfo.b *= (float9 + float12 + float15 + float18) / 4.0F;
								}

								sprite.renderBloodSplat((float)(floorBloodSplat2.chunk.wx * 10) + floorBloodSplat2.x, (float)(floorBloodSplat2.chunk.wy * 10) + floorBloodSplat2.y, floorBloodSplat2.z, inf);
							}
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
		ChunkWidthInTiles = 10 * ChunkGridWidth;
		inf = new ColorInfo();
		saveList = new ArrayList();
		splatByType = new ArrayList();
	for (int var0 = 0; var0 < IsoFloorBloodSplat.FloorBloodTypes.length; ++var0) {
		splatByType.add(new ArrayList());
	}
	}
}
