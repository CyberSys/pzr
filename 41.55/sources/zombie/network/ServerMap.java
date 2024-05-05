package zombie.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import zombie.GameTime;
import zombie.MapCollisionData;
import zombie.ReanimatedPlayers;
import zombie.VirtualZombieManager;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.core.stash.StashSystem;
import zombie.core.utils.OnceEvery;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.globalObjects.SGlobalObjects;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.popman.ZombiePopulationManager;
import zombie.savefile.ServerPlayerDB;
import zombie.vehicles.VehiclesDB2;


public class ServerMap {
	public boolean bUpdateLOSThisFrame = false;
	public static OnceEvery LOSTick = new OnceEvery(1.0F);
	public static OnceEvery TimeTick = new OnceEvery(600.0F);
	public static final int CellSize = 50;
	public static final int ChunksPerCellWidth = 5;
	public long LastSaved = 0L;
	private static boolean MapLoading;
	public final ServerMap.ZombieIDMap ZombieMap = new ServerMap.ZombieIDMap();
	public boolean bQueuedSaveAll = false;
	public boolean bQueuedQuit = false;
	public static ServerMap instance = new ServerMap();
	public ServerMap.ServerCell[] cellMap;
	public ArrayList LoadedCells = new ArrayList();
	public ArrayList ReleventNow = new ArrayList();
	int width;
	int height;
	IsoMetaGrid grid;
	ArrayList ToLoad = new ArrayList();
	static final ServerMap.DistToCellComparator distToCellComparator = new ServerMap.DistToCellComparator();
	private final ArrayList tempCells = new ArrayList();
	long lastTick = 0L;
	Vector2 start;

	public short getUniqueZombieId() {
		return this.ZombieMap.allocateID();
	}

	public Vector3 getStartLocation(ServerWorldDatabase.LogonResult logonResult) {
		short short1 = 9412;
		short short2 = 10745;
		byte byte1 = 0;
		return new Vector3((float)short2, (float)short1, (float)byte1);
	}

	public void SaveAll() {
		long long1 = System.nanoTime();
		for (int int1 = 0; int1 < this.LoadedCells.size(); ++int1) {
			((ServerMap.ServerCell)this.LoadedCells.get(int1)).Save();
		}

		this.grid.save();
		double double1 = (double)(System.nanoTime() - long1);
		DebugLog.log("SaveAll took " + double1 / 1000000.0 + " ms");
	}

	public void QueueSaveAll() {
		this.bQueuedSaveAll = true;
	}

	public void QueueQuit() {
		this.bQueuedSaveAll = true;
		this.bQueuedQuit = true;
	}

	public int toServerCellX(int int1) {
		int1 *= 300;
		int1 /= 50;
		return int1;
	}

	public int toServerCellY(int int1) {
		int1 *= 300;
		int1 /= 50;
		return int1;
	}

	public int toWorldCellX(int int1) {
		int1 *= 50;
		int1 /= 300;
		return int1;
	}

	public int toWorldCellY(int int1) {
		int1 *= 50;
		int1 /= 300;
		return int1;
	}

	public int getMaxX() {
		int int1 = this.toServerCellX(this.grid.maxX + 1);
		if ((this.grid.maxX + 1) * 300 % 50 == 0) {
			--int1;
		}

		return int1;
	}

	public int getMaxY() {
		int int1 = this.toServerCellY(this.grid.maxY + 1);
		if ((this.grid.maxY + 1) * 300 % 50 == 0) {
			--int1;
		}

		return int1;
	}

	public int getMinX() {
		int int1 = this.toServerCellX(this.grid.minX);
		return int1;
	}

	public int getMinY() {
		int int1 = this.toServerCellY(this.grid.minY);
		return int1;
	}

	public void init(IsoMetaGrid metaGrid) {
		this.grid = metaGrid;
		this.width = this.getMaxX() - this.getMinX() + 1;
		this.height = this.getMaxY() - this.getMinY() + 1;
		assert this.width * 50 >= metaGrid.getWidth() * 300;
		assert this.height * 50 >= metaGrid.getHeight() * 300;
		assert this.getMaxX() * 50 < (metaGrid.getMaxX() + 1) * 300;
		assert this.getMaxY() * 50 < (metaGrid.getMaxY() + 1) * 300;
		int int1 = this.width * this.height;
		this.cellMap = new ServerMap.ServerCell[int1];
		StashSystem.init();
	}

	public ServerMap.ServerCell getCell(int int1, int int2) {
		return !this.isValidCell(int1, int2) ? null : this.cellMap[int2 * this.width + int1];
	}

	public boolean isValidCell(int int1, int int2) {
		return int1 >= 0 && int2 >= 0 && int1 < this.width && int2 < this.height;
	}

	public void loadOrKeepRelevent(int int1, int int2) {
		if (this.isValidCell(int1, int2)) {
			ServerMap.ServerCell serverCell = this.getCell(int1, int2);
			if (serverCell == null) {
				serverCell = new ServerMap.ServerCell();
				serverCell.WX = int1 + this.getMinX();
				serverCell.WY = int2 + this.getMinY();
				if (MapLoading) {
					int int3 = serverCell.WX;
					DebugLog.log(DebugType.MapLoading, "Loading cell: " + int3 + ", " + serverCell.WY + " (" + this.toWorldCellX(serverCell.WX) + ", " + this.toWorldCellX(serverCell.WY) + ")");
				}

				this.cellMap[int2 * this.width + int1] = serverCell;
				this.ToLoad.add(serverCell);
				MPStatistic.getInstance().ServerMapToLoad.Added();
				this.LoadedCells.add(serverCell);
				MPStatistic.getInstance().ServerMapLoadedCells.Added();
				this.ReleventNow.add(serverCell);
			} else if (!this.ReleventNow.contains(serverCell)) {
				this.ReleventNow.add(serverCell);
			}
		}
	}

	public void characterIn(IsoPlayer player) {
		while (this.grid == null) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}

		int int1 = player.OnlineChunkGridWidth / 2 * 10;
		int int2 = (int)(Math.floor((double)((player.getX() - (float)int1) / 50.0F)) - (double)this.getMinX());
		int int3 = (int)(Math.floor((double)((player.getX() + (float)int1) / 50.0F)) - (double)this.getMinX());
		int int4 = (int)(Math.floor((double)((player.getY() - (float)int1) / 50.0F)) - (double)this.getMinY());
		int int5 = (int)(Math.floor((double)((player.getY() + (float)int1) / 50.0F)) - (double)this.getMinY());
		for (int int6 = int4; int6 <= int5; ++int6) {
			for (int int7 = int2; int7 <= int3; ++int7) {
				this.loadOrKeepRelevent(int7, int6);
			}
		}
	}

	public void characterIn(int int1, int int2, int int3) {
		while (this.grid == null) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}

		int int4 = int1 * 10;
		int int5 = int2 * 10;
		int4 = (int)((float)int4 / 50.0F);
		int5 = (int)((float)int5 / 50.0F);
		int4 -= this.getMinX();
		int5 -= this.getMinY();
		int int6 = int1 * 10 % 50;
		int int7 = int2 * 10 % 50;
		int int8 = int3 / 2 * 10;
		int int9 = int4;
		int int10 = int5;
		int int11 = int4;
		int int12 = int5;
		if (int6 < int8) {
			int9 = int4 - 1;
		}

		if (int6 > 50 - int8) {
			int11 = int4 + 1;
		}

		if (int7 < int8) {
			int10 = int5 - 1;
		}

		if (int7 > 50 - int8) {
			int12 = int5 + 1;
		}

		for (int int13 = int10; int13 <= int12; ++int13) {
			for (int int14 = int9; int14 <= int11; ++int14) {
				this.loadOrKeepRelevent(int14, int13);
			}
		}
	}

	public void loadMapChunk(int int1, int int2) {
		while (this.grid == null) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}

		int int3 = (int)((float)int1 / 50.0F);
		int int4 = (int)((float)int2 / 50.0F);
		int3 -= this.getMinX();
		int4 -= this.getMinY();
		this.loadOrKeepRelevent(int3, int4);
	}

	public void preupdate() {
		long long1 = System.nanoTime();
		long long2 = long1 - this.lastTick;
		double double1 = (double)long2 * 1.0E-6;
		this.lastTick = long1;
		MapLoading = DebugType.Do(DebugType.MapLoading);
		int int1;
		ServerMap.ServerCell serverCell;
		int int2;
		int int3;
		for (int1 = 0; int1 < this.ToLoad.size(); ++int1) {
			serverCell = (ServerMap.ServerCell)this.ToLoad.get(int1);
			if (serverCell.bLoadingWasCancelled) {
				if (MapLoading) {
					DebugLog.log(DebugType.MapLoading, "MainThread: forgetting cancelled " + serverCell.WX + "," + serverCell.WY);
				}

				int2 = serverCell.WX - this.getMinX();
				int3 = serverCell.WY - this.getMinY();
				assert this.cellMap[int2 + int3 * this.width] == serverCell;
				this.cellMap[int2 + int3 * this.width] = null;
				this.LoadedCells.remove(serverCell);
				this.ReleventNow.remove(serverCell);
				ServerMap.ServerCell.loaded2.remove(serverCell);
				this.ToLoad.remove(int1--);
				MPStatistic.getInstance().ServerMapToLoad.Canceled();
			}
		}

		for (int1 = 0; int1 < this.LoadedCells.size(); ++int1) {
			serverCell = (ServerMap.ServerCell)this.LoadedCells.get(int1);
			if (serverCell.bCancelLoading) {
				if (MapLoading) {
					DebugLog.log(DebugType.MapLoading, "MainThread: forgetting cancelled " + serverCell.WX + "," + serverCell.WY);
				}

				int2 = serverCell.WX - this.getMinX();
				int3 = serverCell.WY - this.getMinY();
				assert this.cellMap[int2 + int3 * this.width] == serverCell;
				this.cellMap[int2 + int3 * this.width] = null;
				this.LoadedCells.remove(int1--);
				this.ReleventNow.remove(serverCell);
				ServerMap.ServerCell.loaded2.remove(serverCell);
				this.ToLoad.remove(serverCell);
				MPStatistic.getInstance().ServerMapLoadedCells.Canceled();
			}
		}

		for (int1 = 0; int1 < ServerMap.ServerCell.loaded2.size(); ++int1) {
			serverCell = (ServerMap.ServerCell)ServerMap.ServerCell.loaded2.get(int1);
			if (serverCell.bCancelLoading) {
				if (MapLoading) {
					DebugLog.log(DebugType.MapLoading, "MainThread: forgetting cancelled " + serverCell.WX + "," + serverCell.WY);
				}

				int2 = serverCell.WX - this.getMinX();
				int3 = serverCell.WY - this.getMinY();
				assert this.cellMap[int2 + int3 * this.width] == serverCell;
				this.cellMap[int2 + int3 * this.width] = null;
				this.LoadedCells.remove(serverCell);
				this.ReleventNow.remove(serverCell);
				ServerMap.ServerCell.loaded2.remove(serverCell);
				this.ToLoad.remove(serverCell);
				MPStatistic.getInstance().ServerMapLoaded2.Canceled();
			}
		}

		if (!this.ToLoad.isEmpty()) {
			this.tempCells.clear();
			for (int1 = 0; int1 < this.ToLoad.size(); ++int1) {
				serverCell = (ServerMap.ServerCell)this.ToLoad.get(int1);
				if (!serverCell.bCancelLoading && !serverCell.startedLoading) {
					this.tempCells.add(serverCell);
				}
			}

			if (!this.tempCells.isEmpty()) {
				distToCellComparator.init();
				Collections.sort(this.tempCells, distToCellComparator);
				for (int1 = 0; int1 < this.tempCells.size(); ++int1) {
					serverCell = (ServerMap.ServerCell)this.tempCells.get(int1);
					ServerMap.ServerCell.chunkLoader.addJob(serverCell);
					serverCell.startedLoading = true;
				}
			}

			ServerMap.ServerCell.chunkLoader.getLoaded(ServerMap.ServerCell.loaded);
			for (int1 = 0; int1 < ServerMap.ServerCell.loaded.size(); ++int1) {
				serverCell = (ServerMap.ServerCell)ServerMap.ServerCell.loaded.get(int1);
				if (!serverCell.doingRecalc) {
					ServerMap.ServerCell.chunkLoader.addRecalcJob(serverCell);
					serverCell.doingRecalc = true;
				}
			}

			ServerMap.ServerCell.loaded.clear();
			ServerMap.ServerCell.chunkLoader.getRecalc(ServerMap.ServerCell.loaded2);
			if (!ServerMap.ServerCell.loaded2.isEmpty()) {
				try {
					ServerLOS.instance.suspend();
					for (int1 = 0; int1 < ServerMap.ServerCell.loaded2.size(); ++int1) {
						serverCell = (ServerMap.ServerCell)ServerMap.ServerCell.loaded2.get(int1);
						long long3 = System.nanoTime();
						if (serverCell.Load2()) {
							long3 = System.nanoTime();
							--int1;
							this.ToLoad.remove(serverCell);
						}
					}
				} finally {
					ServerLOS.instance.resume();
				}
			}
		}

		int1 = ServerOptions.instance.SaveWorldEveryMinutes.getValue();
		long long4;
		if (int1 > 0) {
			long4 = System.currentTimeMillis();
			if (long4 > this.LastSaved + (long)(int1 * 60 * 1000)) {
				this.bQueuedSaveAll = true;
				this.LastSaved = long4;
			}
		}

		if (this.bQueuedSaveAll) {
			this.bQueuedSaveAll = false;
			long4 = System.nanoTime();
			this.SaveAll();
			ServerMap.ServerCell.chunkLoader.saveLater(GameTime.instance);
			ReanimatedPlayers.instance.saveReanimatedPlayers();
			MapCollisionData.instance.save();
			SGlobalObjects.save();
			GameServer.UnPauseAllClients();
			System.out.println("Saving finish");
			double double2 = (double)(System.nanoTime() - long4);
			DebugLog.log("Saving took " + double2 / 1000000.0 + " ms");
		}

		if (this.bQueuedQuit) {
			ByteBufferWriter byteBufferWriter = GameServer.udpEngine.startPacket();
			PacketTypes.doPacket((short)52, byteBufferWriter);
			GameServer.udpEngine.endPacketBroadcast();
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}

			MapCollisionData.instance.stop();
			ZombiePopulationManager.instance.stop();
			RCONServer.shutdown();
			ServerMap.ServerCell.chunkLoader.quit();
			ServerWorldDatabase.instance.close();
			ServerPlayersVehicles.instance.stop();
			ServerPlayerDB.getInstance().close();
			VehiclesDB2.instance.Reset();
			GameServer.udpEngine.Shutdown();
			ServerGUI.shutdown();
			SteamUtils.shutdown();
			System.exit(0);
		}

		ZombieUpdatePacker.instance.clearPacket();
		this.ReleventNow.clear();
		this.bUpdateLOSThisFrame = LOSTick.Check();
		if (TimeTick.Check()) {
			ServerMap.ServerCell.chunkLoader.saveLater(GameTime.instance);
		}
	}

	private IsoGridSquare getRandomSquareFromCell(int int1, int int2) {
		this.loadOrKeepRelevent(int1, int2);
		int int3 = int1;
		int int4 = int2;
		ServerMap.ServerCell serverCell = this.getCell(int1, int2);
		if (serverCell == null) {
			throw new RuntimeException("Cannot find a random square.");
		} else {
			int1 = (int1 + this.getMinX()) * 50;
			int2 = (int2 + this.getMinY()) * 50;
			IsoGridSquare square = null;
			int int5 = 100;
			do {
				square = this.getGridSquare(Rand.Next(int1, int1 + 50), Rand.Next(int2, int2 + 50), 0);
				--int5;
				if (square == null) {
					this.loadOrKeepRelevent(int3, int4);
				}
			}	 while (square == null && int5 > 0);

			return square;
		}
	}

	public void postupdate() {
		int int1 = this.LoadedCells.size();
		boolean boolean1 = false;
		try {
			for (int int2 = 0; int2 < this.LoadedCells.size(); ++int2) {
				ServerMap.ServerCell serverCell = (ServerMap.ServerCell)this.LoadedCells.get(int2);
				boolean boolean2 = this.ReleventNow.contains(serverCell) || !this.outsidePlayerInfluence(serverCell);
				if (!serverCell.bLoaded) {
					if (!boolean2 && !serverCell.bCancelLoading) {
						if (MapLoading) {
							DebugLog.log(DebugType.MapLoading, "MainThread: cancelling " + serverCell.WX + "," + serverCell.WY + " cell.startedLoading=" + serverCell.startedLoading);
						}

						if (!serverCell.startedLoading) {
							serverCell.bLoadingWasCancelled = true;
						}

						serverCell.bCancelLoading = true;
					}
				} else {
					int int3;
					int int4;
					if (!boolean2) {
						int3 = serverCell.WX - this.getMinX();
						int4 = serverCell.WY - this.getMinY();
						if (!boolean1) {
							ServerLOS.instance.suspend();
							boolean1 = true;
						}

						this.cellMap[int4 * this.width + int3].Unload();
						this.cellMap[int4 * this.width + int3] = null;
						this.LoadedCells.remove(serverCell);
						--int2;
					} else if (serverCell.bPhysicsCheck) {
						for (int3 = 0; int3 < 5; ++int3) {
							for (int4 = 0; int4 < 5; ++int4) {
								if (serverCell.chunks[int4][int3] != null) {
									serverCell.chunks[int4][int3].update();
								}
							}
						}

						serverCell.bPhysicsCheck = false;
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (boolean1) {
				ServerLOS.instance.resume();
			}
		}

		ZombieUpdatePacker.instance.postupdate();
		ServerMap.ServerCell.chunkLoader.updateSaved();
	}

	public void physicsCheck(int int1, int int2) {
		int int3 = int1 / 50;
		int int4 = int2 / 50;
		int3 -= this.getMinX();
		int4 -= this.getMinY();
		ServerMap.ServerCell serverCell = this.getCell(int3, int4);
		if (serverCell != null && serverCell.bLoaded) {
			serverCell.bPhysicsCheck = true;
		}
	}

	private boolean outsidePlayerInfluence(ServerMap.ServerCell serverCell) {
		int int1 = serverCell.WX * 50;
		int int2 = serverCell.WY * 50;
		int int3 = (serverCell.WX + 1) * 50;
		int int4 = (serverCell.WY + 1) * 50;
		for (int int5 = 0; int5 < GameServer.udpEngine.connections.size(); ++int5) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int5);
			if (udpConnection.ReleventTo((float)int1, (float)int2)) {
				return false;
			}

			if (udpConnection.ReleventTo((float)int3, (float)int2)) {
				return false;
			}

			if (udpConnection.ReleventTo((float)int3, (float)int4)) {
				return false;
			}

			if (udpConnection.ReleventTo((float)int1, (float)int4)) {
				return false;
			}
		}

		return true;
	}

	public void saveZoneInsidePlayerInfluence(int int1) {
		for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int2);
			for (int int3 = 0; int3 < udpConnection.players.length; ++int3) {
				if (udpConnection.players[int3] != null && udpConnection.players[int3].OnlineID == int1) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)udpConnection.players[int3].x, (double)udpConnection.players[int3].y, (double)udpConnection.players[int3].z);
					if (square != null) {
						ServerMap.ServerCell.chunkLoader.addSaveLoadedJob(square.chunk);
						return;
					}
				}
			}
		}

		ServerMap.ServerCell.chunkLoader.updateSaved();
	}

	private boolean InsideThePlayerInfluence(ServerMap.ServerCell serverCell, int int1) {
		int int2 = serverCell.WX * 50;
		int int3 = serverCell.WY * 50;
		int int4 = (serverCell.WX + 1) * 50;
		int int5 = (serverCell.WY + 1) * 50;
		for (int int6 = 0; int6 < GameServer.udpEngine.connections.size(); ++int6) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int6);
			for (int int7 = 0; int7 < udpConnection.players.length; ++int7) {
				if (udpConnection.players[int7] != null && udpConnection.players[int7].OnlineID == int1) {
					if (udpConnection.ReleventToPlayerIndex(int7, (float)int2, (float)int3)) {
						return true;
					}

					if (udpConnection.ReleventToPlayerIndex(int7, (float)int4, (float)int3)) {
						return true;
					}

					if (udpConnection.ReleventToPlayerIndex(int7, (float)int4, (float)int5)) {
						return true;
					}

					if (udpConnection.ReleventToPlayerIndex(int7, (float)int2, (float)int5)) {
						return true;
					}

					return false;
				}
			}
		}

		return false;
	}

	public IsoGridSquare getGridSquare(int int1, int int2, int int3) {
		if (!IsoWorld.instance.isValidSquare(int1, int2, int3)) {
			return null;
		} else {
			int int4 = int1 / 50;
			int int5 = int2 / 50;
			int4 -= this.getMinX();
			int5 -= this.getMinY();
			int int6 = int1 / 10;
			int int7 = int2 / 10;
			int int8 = int6 % 5;
			int int9 = int7 % 5;
			int int10 = int1 % 10;
			int int11 = int2 % 10;
			ServerMap.ServerCell serverCell = this.getCell(int4, int5);
			if (serverCell != null && serverCell.bLoaded) {
				IsoChunk chunk = serverCell.chunks[int8][int9];
				return chunk == null ? null : chunk.getGridSquare(int10, int11, int3);
			} else {
				return null;
			}
		}
	}

	public void setGridSquare(int int1, int int2, int int3, IsoGridSquare square) {
		int int4 = int1 / 50;
		int int5 = int2 / 50;
		int4 -= this.getMinX();
		int5 -= this.getMinY();
		int int6 = int1 / 10;
		int int7 = int2 / 10;
		int int8 = int6 % 5;
		int int9 = int7 % 5;
		int int10 = int1 % 10;
		int int11 = int2 % 10;
		ServerMap.ServerCell serverCell = this.getCell(int4, int5);
		if (serverCell != null) {
			IsoChunk chunk = serverCell.chunks[int8][int9];
			if (chunk != null) {
				chunk.setSquare(int10, int11, int3, square);
			}
		}
	}

	public boolean isInLoaded(float float1, float float2) {
		int int1 = (int)float1;
		int int2 = (int)float2;
		int1 /= 50;
		int2 /= 50;
		int1 -= this.getMinX();
		int2 -= this.getMinY();
		if (this.ToLoad.contains(this.getCell(int1, int2))) {
			return false;
		} else {
			return this.getCell(int1, int2) != null;
		}
	}

	public IsoChunk getChunk(int int1, int int2) {
		if (int1 >= 0 && int2 >= 0) {
			int int3 = int1 / 5;
			int int4 = int2 / 5;
			int3 -= this.getMinX();
			int4 -= this.getMinY();
			int int5 = int1 % 5;
			int int6 = int2 % 5;
			ServerMap.ServerCell serverCell = this.getCell(int3, int4);
			return serverCell != null && serverCell.bLoaded ? serverCell.chunks[int5][int6] : null;
		} else {
			return null;
		}
	}

	public static class ZombieIDMap {
		private static int MAX_ZOMBIES = 32767;
		private static int RESIZE_COUNT = 1024;
		private int capacity = 1024;
		private IsoZombie[] idToZombie;
		private short[] freeID;
		private short freeIDSize;
		private int warnCount = 0;

		ZombieIDMap() {
			this.idToZombie = new IsoZombie[this.capacity];
			this.freeID = new short[this.capacity];
			for (int int1 = 0; int1 < this.capacity; ++int1) {
				short[] shortArray = this.freeID;
				short short1 = this.freeIDSize;
				this.freeIDSize = (short)(short1 + 1);
				shortArray[short1] = (short)int1;
			}
		}

		public void put(short short1, IsoZombie zombie) {
			if (short1 >= 0 && short1 < this.capacity) {
				if (this.idToZombie[short1] != null) {
					throw new IllegalArgumentException("duplicate zombie with id " + short1);
				} else if (zombie == null) {
					throw new IllegalArgumentException("zombie is null");
				} else {
					this.idToZombie[short1] = zombie;
				}
			} else {
				throw new IllegalArgumentException("invalid zombie id " + short1 + " max=" + this.capacity);
			}
		}

		public void remove(short short1) {
			if (short1 >= 0 && short1 < this.capacity) {
				if (this.idToZombie[short1] == null) {
					throw new IllegalArgumentException("no zombie with id " + short1);
				} else {
					this.idToZombie[short1] = null;
					short[] shortArray = this.freeID;
					short short2 = this.freeIDSize;
					this.freeIDSize = (short)(short2 + 1);
					shortArray[short2] = short1;
				}
			} else {
				throw new IllegalArgumentException("invalid zombie id=" + short1 + " max=" + this.capacity);
			}
		}

		public IsoZombie get(short short1) {
			return this.idToZombie[short1];
		}

		private short allocateID() {
			if (this.freeIDSize > 0) {
				return this.freeID[--this.freeIDSize];
			} else if (this.capacity >= MAX_ZOMBIES) {
				if (this.warnCount < 100) {
					DebugLog.log("warning: ran out of unique zombie ids");
					++this.warnCount;
				}

				return -1;
			} else {
				this.resize(this.capacity + RESIZE_COUNT);
				return this.allocateID();
			}
		}

		private void resize(int int1) {
			int int2 = this.capacity;
			this.capacity = Math.min(int1, MAX_ZOMBIES);
			this.capacity = Math.min(int1, 32767);
			this.idToZombie = (IsoZombie[])Arrays.copyOf(this.idToZombie, this.capacity);
			this.freeID = Arrays.copyOf(this.freeID, this.capacity);
			for (int int3 = int2; int3 < this.capacity; ++int3) {
				short[] shortArray = this.freeID;
				short short1 = this.freeIDSize;
				this.freeIDSize = (short)(short1 + 1);
				shortArray[short1] = (short)int3;
			}
		}
	}

	public static class ServerCell {
		public int WX;
		public int WY;
		public boolean bLoaded = false;
		public boolean bPhysicsCheck = false;
		public final IsoChunk[][] chunks = new IsoChunk[5][5];
		private HashSet UnexploredRooms = new HashSet();
		private static ServerChunkLoader chunkLoader = new ServerChunkLoader();
		private static ArrayList loaded = new ArrayList();
		private boolean startedLoading = false;
		public boolean bCancelLoading = false;
		public boolean bLoadingWasCancelled = false;
		private static ArrayList loaded2 = new ArrayList();
		private boolean doingRecalc = false;

		public boolean Load2() {
			chunkLoader.getRecalc(loaded2);
			for (int int1 = 0; int1 < loaded2.size(); ++int1) {
				if (loaded2.get(int1) == this) {
					long long1 = System.nanoTime();
					this.RecalcAll2();
					loaded2.remove(int1);
					if (ServerMap.MapLoading) {
						DebugLog.log(DebugType.MapLoading, "loaded2=" + loaded2);
					}

					float float1 = (float)(System.nanoTime() - long1) / 1000000.0F;
					if (ServerMap.MapLoading) {
						DebugLog.log(DebugType.MapLoading, "finish loading cell " + this.WX + "," + this.WY + " ms=" + float1);
					}

					return true;
				}
			}

			return false;
		}

		public void RecalcAll2() {
			int int1 = this.WX * 5 * 10;
			int int2 = this.WY * 5 * 10;
			int int3 = int1 + 50;
			int int4 = int2 + 50;
			RoomDef roomDef;
			for (Iterator iterator = this.UnexploredRooms.iterator(); iterator.hasNext(); --roomDef.IndoorZombies) {
				roomDef = (RoomDef)iterator.next();
			}

			this.UnexploredRooms.clear();
			this.bLoaded = true;
			IsoGridSquare square;
			int int5;
			int int6;
			for (int5 = 1; int5 < 8; ++int5) {
				for (int6 = -1; int6 < 51; ++int6) {
					square = ServerMap.instance.getGridSquare(int1 + int6, int2 - 1, int5);
					if (square != null && !square.getObjects().isEmpty()) {
						IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
					} else if (int6 >= 0 && int6 < 50) {
						square = ServerMap.instance.getGridSquare(int1 + int6, int2, int5);
						if (square != null && !square.getObjects().isEmpty()) {
							IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
						}
					}

					square = ServerMap.instance.getGridSquare(int1 + int6, int2 + 50, int5);
					if (square != null && !square.getObjects().isEmpty()) {
						IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
					} else if (int6 >= 0 && int6 < 50) {
						ServerMap.instance.getGridSquare(int1 + int6, int2 + 50 - 1, int5);
						if (square != null && !square.getObjects().isEmpty()) {
							IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
						}
					}
				}

				for (int6 = 0; int6 < 50; ++int6) {
					square = ServerMap.instance.getGridSquare(int1 - 1, int2 + int6, int5);
					if (square != null && !square.getObjects().isEmpty()) {
						IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
					} else {
						square = ServerMap.instance.getGridSquare(int1, int2 + int6, int5);
						if (square != null && !square.getObjects().isEmpty()) {
							IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
						}
					}

					square = ServerMap.instance.getGridSquare(int1 + 50, int2 + int6, int5);
					if (square != null && !square.getObjects().isEmpty()) {
						IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
					} else {
						square = ServerMap.instance.getGridSquare(int1 + 50 - 1, int2 + int6, int5);
						if (square != null && !square.getObjects().isEmpty()) {
							IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
						}
					}
				}
			}

			for (int5 = 0; int5 < 8; ++int5) {
				for (int6 = 0; int6 < 50; ++int6) {
					square = ServerMap.instance.getGridSquare(int1 + int6, int2 + 0, int5);
					if (square != null) {
						square.RecalcAllWithNeighbours(true);
					}

					square = ServerMap.instance.getGridSquare(int1 + int6, int4 - 1, int5);
					if (square != null) {
						square.RecalcAllWithNeighbours(true);
					}
				}

				for (int6 = 0; int6 < 50; ++int6) {
					square = ServerMap.instance.getGridSquare(int1 + 0, int2 + int6, int5);
					if (square != null) {
						square.RecalcAllWithNeighbours(true);
					}

					square = ServerMap.instance.getGridSquare(int3 - 1, int2 + int6, int5);
					if (square != null) {
						square.RecalcAllWithNeighbours(true);
					}
				}
			}

			byte byte1 = 100;
			int int7;
			for (int6 = 0; int6 < 5; ++int6) {
				for (int7 = 0; int7 < 5; ++int7) {
					IsoChunk chunk = this.chunks[int6][int7];
					if (chunk != null) {
						chunk.bLoaded = true;
						for (int int8 = 0; int8 < byte1; ++int8) {
							for (int int9 = 0; int9 <= chunk.maxLevel; ++int9) {
								IsoGridSquare square2 = chunk.squares[int9][int8];
								if (square2 != null) {
									if (square2.getRoom() != null && !square2.getRoom().def.bExplored) {
										this.UnexploredRooms.add(square2.getRoom().def);
									}

									square2.propertiesDirty = true;
								}
							}
						}
					}
				}
			}

			WorldSimulation.instance.createServerCell(this);
			for (int6 = 0; int6 < 5; ++int6) {
				for (int7 = 0; int7 < 5; ++int7) {
					if (this.chunks[int6][int7] != null) {
						this.chunks[int6][int7].doLoadGridsquare();
					}
				}
			}

			Iterator iterator2 = this.UnexploredRooms.iterator();
			while (iterator2.hasNext()) {
				RoomDef roomDef2 = (RoomDef)iterator2.next();
				++roomDef2.IndoorZombies;
				if (roomDef2.IndoorZombies == 1) {
					try {
						VirtualZombieManager.instance.tryAddIndoorZombies(roomDef2, false);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}

			this.bLoaded = true;
		}

		public void Unload() {
			if (this.bLoaded) {
				if (ServerMap.MapLoading) {
					int int1 = this.WX;
					DebugLog.log(DebugType.MapLoading, "Unloading cell: " + int1 + ", " + this.WY + " (" + ServerMap.instance.toWorldCellX(this.WX) + ", " + ServerMap.instance.toWorldCellX(this.WY) + ")");
				}

				for (int int2 = 0; int2 < 5; ++int2) {
					for (int int3 = 0; int3 < 5; ++int3) {
						if (this.chunks[int2][int3] != null) {
							this.chunks[int2][int3].removeFromWorld();
							chunkLoader.addSaveUnloadedJob(this.chunks[int2][int3]);
							this.chunks[int2][int3] = null;
						}
					}
				}

				RoomDef roomDef;
				for (Iterator iterator = this.UnexploredRooms.iterator(); iterator.hasNext(); --roomDef.IndoorZombies) {
					roomDef = (RoomDef)iterator.next();
					if (roomDef.IndoorZombies == 1) {
					}
				}

				WorldSimulation.instance.removeServerCell(this);
			}
		}

		public void Save() {
			if (this.bLoaded) {
				for (int int1 = 0; int1 < 5; ++int1) {
					for (int int2 = 0; int2 < 5; ++int2) {
						if (this.chunks[int1][int2] != null) {
							try {
								chunkLoader.addSaveLoadedJob(this.chunks[int1][int2]);
							} catch (Exception exception) {
								exception.printStackTrace();
								LoggerManager.getLogger("map").write(exception);
							}
						}
					}
				}

				chunkLoader.updateSaved();
			}
		}
	}

	private static class DistToCellComparator implements Comparator {
		private Vector2[] pos = new Vector2[1024];
		private int posCount;

		public DistToCellComparator() {
			for (int int1 = 0; int1 < this.pos.length; ++int1) {
				this.pos[int1] = new Vector2();
			}
		}

		public void init() {
			this.posCount = 0;
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection.isFullyConnected()) {
					for (int int2 = 0; int2 < 4; ++int2) {
						if (udpConnection.players[int2] != null) {
							this.pos[this.posCount].set(udpConnection.players[int2].x, udpConnection.players[int2].y);
							++this.posCount;
						}
					}
				}
			}
		}

		public int compare(ServerMap.ServerCell serverCell, ServerMap.ServerCell serverCell2) {
			float float1 = Float.MAX_VALUE;
			float float2 = Float.MAX_VALUE;
			for (int int1 = 0; int1 < this.posCount; ++int1) {
				float float3 = this.pos[int1].x;
				float float4 = this.pos[int1].y;
				float1 = Math.min(float1, this.distToCell(float3, float4, serverCell));
				float2 = Math.min(float2, this.distToCell(float3, float4, serverCell2));
			}

			if (float1 < float2) {
				return -1;
			} else {
				return float1 > float2 ? 1 : 0;
			}
		}

		private float distToCell(float float1, float float2, ServerMap.ServerCell serverCell) {
			int int1 = serverCell.WX * 50;
			int int2 = serverCell.WY * 50;
			int int3 = int1 + 50;
			int int4 = int2 + 50;
			float float3 = float1;
			float float4 = float2;
			if (float1 < (float)int1) {
				float3 = (float)int1;
			} else if (float1 > (float)int3) {
				float3 = (float)int3;
			}

			if (float2 < (float)int2) {
				float4 = (float)int2;
			} else if (float2 > (float)int4) {
				float4 = (float)int4;
			}

			return IsoUtils.DistanceToSquared(float1, float2, float3, float4);
		}
	}
}
