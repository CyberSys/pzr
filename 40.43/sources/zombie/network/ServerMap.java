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
import zombie.ai.astar.Mover;
import zombie.ai.astar.TileBasedMap;
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
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.popman.ZombiePopulationManager;


public class ServerMap implements TileBasedMap {
	public boolean bUpdateLOSThisFrame = false;
	public static OnceEvery LOSTick = new OnceEvery(1.0F);
	public static OnceEvery TimeTick = new OnceEvery(600.0F);
	public static final int CellSize = 70;
	public static final int ChunksPerCellWidth = 7;
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
	Vector2 start;
	Vector2 tempo = new Vector2();

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
		DebugLog.log("SaveAll took " + (double)(System.nanoTime() - long1) / 1000000.0 + " ms");
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
		int1 /= 70;
		return int1;
	}

	public int toServerCellY(int int1) {
		int1 *= 300;
		int1 /= 70;
		return int1;
	}

	public int toWorldCellX(int int1) {
		int1 *= 70;
		int1 /= 300;
		return int1;
	}

	public int toWorldCellY(int int1) {
		int1 *= 70;
		int1 /= 300;
		return int1;
	}

	public int getMaxX() {
		int int1 = this.toServerCellX(this.grid.maxX + 1);
		if ((this.grid.maxX + 1) * 300 % 70 == 0) {
			--int1;
		}

		return int1;
	}

	public int getMaxY() {
		int int1 = this.toServerCellY(this.grid.maxY + 1);
		if ((this.grid.maxY + 1) * 300 % 70 == 0) {
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
		assert this.width * 70 >= metaGrid.getWidth() * 300;
		assert this.height * 70 >= metaGrid.getHeight() * 300;
		assert this.getMaxX() * 70 < (metaGrid.getMaxX() + 1) * 300;
		assert this.getMaxY() * 70 < (metaGrid.getMaxY() + 1) * 300;
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
					DebugLog.log(DebugType.MapLoading, "Loading cell: " + serverCell.WX + ", " + serverCell.WY + " (" + this.toWorldCellX(serverCell.WX) + ", " + this.toWorldCellX(serverCell.WY) + ")");
				}

				this.cellMap[int2 * this.width + int1] = serverCell;
				this.ToLoad.add(serverCell);
				this.LoadedCells.add(serverCell);
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

		int int1 = (int)player.getX();
		int int2 = (int)player.getY();
		int1 = (int)((float)int1 / 70.0F);
		int2 = (int)((float)int2 / 70.0F);
		int1 -= this.getMinX();
		int2 -= this.getMinY();
		int int3 = (int)player.getX() % 70;
		int int4 = (int)player.getY() % 70;
		int int5 = player.OnlineChunkGridWidth / 2 * 10;
		int int6 = int1;
		int int7 = int2;
		int int8 = int1;
		int int9 = int2;
		if (int3 < int5) {
			int6 = int1 - 1;
		}

		if (int3 > 70 - int5) {
			int8 = int1 + 1;
		}

		if (int4 < int5) {
			int7 = int2 - 1;
		}

		if (int4 > 70 - int5) {
			int9 = int2 + 1;
		}

		for (int int10 = int7; int10 <= int9; ++int10) {
			for (int int11 = int6; int11 <= int8; ++int11) {
				this.loadOrKeepRelevent(int11, int10);
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
		int4 = (int)((float)int4 / 70.0F);
		int5 = (int)((float)int5 / 70.0F);
		int4 -= this.getMinX();
		int5 -= this.getMinY();
		int int6 = int1 * 10 % 70;
		int int7 = int2 * 10 % 70;
		int int8 = int3 / 2 * 10;
		int int9 = int4;
		int int10 = int5;
		int int11 = int4;
		int int12 = int5;
		if (int6 < int8) {
			int9 = int4 - 1;
		}

		if (int6 > 70 - int8) {
			int11 = int4 + 1;
		}

		if (int7 < int8) {
			int10 = int5 - 1;
		}

		if (int7 > 70 - int8) {
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

		int int3 = (int)((float)int1 / 70.0F);
		int int4 = (int)((float)int2 / 70.0F);
		int3 -= this.getMinX();
		int4 -= this.getMinY();
		this.loadOrKeepRelevent(int3, int4);
	}

	public void preupdate() {
		MapLoading = DebugType.Do(DebugType.MapLoading);
		int int1;
		ServerMap.ServerCell serverCell;
		for (int1 = 0; int1 < this.ToLoad.size(); ++int1) {
			serverCell = (ServerMap.ServerCell)this.ToLoad.get(int1);
			if (serverCell.bLoadingWasCancelled) {
				if (MapLoading) {
					DebugLog.log(DebugType.MapLoading, "MainThread: forgetting cancelled " + serverCell.WX + "," + serverCell.WY);
				}

				int int2 = serverCell.WX - this.getMinX();
				int int3 = serverCell.WY - this.getMinY();
				assert this.cellMap[int2 + int3 * this.width] == serverCell;
				this.cellMap[int2 + int3 * this.width] = null;
				this.LoadedCells.remove(serverCell);
				this.ReleventNow.remove(serverCell);
				this.ToLoad.remove(int1--);
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
				ServerMap.ServerCell serverCell2 = (ServerMap.ServerCell)ServerMap.ServerCell.loaded2.get(0);
				if (serverCell2.Load2()) {
					this.ToLoad.remove(serverCell2);
				}
			}
		}

		int1 = ServerOptions.instance.SaveWorldEveryMinutes.getValue();
		long long1;
		if (int1 > 0) {
			long1 = System.currentTimeMillis();
			if (long1 > this.LastSaved + (long)(int1 * 60 * 1000)) {
				this.bQueuedSaveAll = true;
				this.LastSaved = long1;
			}
		}

		if (this.bQueuedSaveAll) {
			this.bQueuedSaveAll = false;
			long1 = System.nanoTime();
			this.SaveAll();
			ServerMap.ServerCell.chunkLoader.saveLater(GameTime.instance);
			ReanimatedPlayers.instance.saveReanimatedPlayers();
			MapCollisionData.instance.save();
			SGlobalObjects.save();
			GameServer.UnPauseAllClients();
			System.out.println("Saving finish");
			DebugLog.log("Saving took " + (double)(System.nanoTime() - long1) / 1000000.0 + " ms");
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
			GameServer.udpEngine.Shutdown();
			ServerGUI.shutdown();
			SteamUtils.shutdown();
			System.exit(0);
		}

		ZombieUpdatePacker.instance.clearZombies();
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
			int1 = (int1 + this.getMinX()) * 70;
			int2 = (int2 + this.getMinY()) * 70;
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
		boolean boolean1 = false;
		try {
			for (int int1 = 0; int1 < this.LoadedCells.size(); ++int1) {
				ServerMap.ServerCell serverCell = (ServerMap.ServerCell)this.LoadedCells.get(int1);
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
					int int2;
					int int3;
					if (!boolean2) {
						int2 = serverCell.WX - this.getMinX();
						int3 = serverCell.WY - this.getMinY();
						if (!boolean1) {
							ServerLOS.instance.suspend();
							boolean1 = true;
						}

						this.cellMap[int3 * this.width + int2].Unload();
						this.cellMap[int3 * this.width + int2] = null;
						this.LoadedCells.remove(serverCell);
						--int1;
					} else if (serverCell.bPhysicsCheck) {
						for (int2 = 0; int2 < 7; ++int2) {
							for (int3 = 0; int3 < 7; ++int3) {
								if (serverCell.chunks[int3][int2] != null) {
									serverCell.chunks[int3][int2].update();
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

		try {
			ZombieUpdatePacker.instance.packZombiesIntoPackets();
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}

		ServerMap.ServerCell.chunkLoader.updateSaved();
	}

	public void physicsCheck(int int1, int int2) {
		int int3 = int1 / 70;
		int int4 = int2 / 70;
		int3 -= this.getMinX();
		int4 -= this.getMinY();
		ServerMap.ServerCell serverCell = this.getCell(int3, int4);
		if (serverCell != null && serverCell.bLoaded) {
			serverCell.bPhysicsCheck = true;
		}
	}

	private boolean outsidePlayerInfluence(ServerMap.ServerCell serverCell) {
		int int1 = serverCell.WX * 70;
		int int2 = serverCell.WY * 70;
		int int3 = (serverCell.WX + 1) * 70;
		int int4 = (serverCell.WY + 1) * 70;
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
		int int2 = serverCell.WX * 70;
		int int3 = serverCell.WY * 70;
		int int4 = (serverCell.WX + 1) * 70;
		int int5 = (serverCell.WY + 1) * 70;
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

	public boolean blocked(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6) {
		return false;
	}

	public float getCost(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6) {
		return 0.0F;
	}

	public int getElevInTiles() {
		return 0;
	}

	public int getHeightInTiles() {
		return 0;
	}

	public int getWidthInTiles() {
		return 0;
	}

	public void pathFinderVisited(int int1, int int2, int int3) {
	}

	public boolean isNull(int int1, int int2, int int3) {
		return false;
	}

	public boolean IsStairsNode(IsoGridSquare square, IsoGridSquare square2, IsoDirections directions) {
		return false;
	}

	public IsoGridSquare getGridSquare(int int1, int int2, int int3) {
		if (!IsoWorld.instance.isValidSquare(int1, int2, int3)) {
			return null;
		} else {
			int int4 = int1 / 70;
			int int5 = int2 / 70;
			int4 -= this.getMinX();
			int5 -= this.getMinY();
			int int6 = int1 / 10;
			int int7 = int2 / 10;
			int int8 = int6 % 7;
			int int9 = int7 % 7;
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
		int int4 = int1 / 70;
		int int5 = int2 / 70;
		int4 -= this.getMinX();
		int5 -= this.getMinY();
		int int6 = int1 / 10;
		int int7 = int2 / 10;
		int int8 = int6 % 7;
		int int9 = int7 % 7;
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
		int1 /= 70;
		int2 /= 70;
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
			int int3 = int1 / 7;
			int int4 = int2 / 7;
			int3 -= this.getMinX();
			int4 -= this.getMinY();
			int int5 = int1 % 7;
			int int6 = int2 % 7;
			ServerMap.ServerCell serverCell = this.getCell(int3, int4);
			return serverCell != null && serverCell.bLoaded ? serverCell.chunks[int5][int6] : null;
		} else {
			return null;
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
			int int1 = serverCell.WX * 70;
			int int2 = serverCell.WY * 70;
			int int3 = int1 + 70;
			int int4 = int2 + 70;
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

	public static class ServerCell {
		public int WX;
		public int WY;
		public boolean bLoaded = false;
		public boolean bPhysicsCheck = false;
		public final IsoChunk[][] chunks = new IsoChunk[7][7];
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
					try {
						ServerLOS.instance.suspend();
						this.RecalcAll2();
					} finally {
						ServerLOS.instance.resume();
					}

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
			int int1 = this.WX * 7 * 10;
			int int2 = this.WY * 7 * 10;
			int int3 = int1 + 70;
			int int4 = int2 + 70;
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
				for (int6 = -1; int6 < 71; ++int6) {
					square = ServerMap.instance.getGridSquare(int1 + int6, int2 - 1, int5);
					if (square != null && !square.getObjects().isEmpty()) {
						IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
					} else if (int6 >= 0 && int6 < 70) {
						square = ServerMap.instance.getGridSquare(int1 + int6, int2, int5);
						if (square != null && !square.getObjects().isEmpty()) {
							IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
						}
					}

					square = ServerMap.instance.getGridSquare(int1 + int6, int2 + 70, int5);
					if (square != null && !square.getObjects().isEmpty()) {
						IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
					} else if (int6 >= 0 && int6 < 70) {
						ServerMap.instance.getGridSquare(int1 + int6, int2 + 70 - 1, int5);
						if (square != null && !square.getObjects().isEmpty()) {
							IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
						}
					}
				}

				for (int6 = 0; int6 < 70; ++int6) {
					square = ServerMap.instance.getGridSquare(int1 - 1, int2 + int6, int5);
					if (square != null && !square.getObjects().isEmpty()) {
						IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
					} else {
						square = ServerMap.instance.getGridSquare(int1, int2 + int6, int5);
						if (square != null && !square.getObjects().isEmpty()) {
							IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
						}
					}

					square = ServerMap.instance.getGridSquare(int1 + 70, int2 + int6, int5);
					if (square != null && !square.getObjects().isEmpty()) {
						IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
					} else {
						square = ServerMap.instance.getGridSquare(int1 + 70 - 1, int2 + int6, int5);
						if (square != null && !square.getObjects().isEmpty()) {
							IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(square.x, square.y, int5);
						}
					}
				}
			}

			for (int5 = 0; int5 < 8; ++int5) {
				for (int6 = 0; int6 < 70; ++int6) {
					square = ServerMap.instance.getGridSquare(int1 + int6, int2 + 0, int5);
					if (square != null) {
						square.RecalcAllWithNeighbours(true);
					}

					square = ServerMap.instance.getGridSquare(int1 + int6, int4 - 1, int5);
					if (square != null) {
						square.RecalcAllWithNeighbours(true);
					}
				}

				for (int6 = 0; int6 < 70; ++int6) {
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
			for (int6 = 0; int6 < 7; ++int6) {
				for (int7 = 0; int7 < 7; ++int7) {
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
			for (int6 = 0; int6 < 7; ++int6) {
				for (int7 = 0; int7 < 7; ++int7) {
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
					DebugLog.log(DebugType.MapLoading, "Unloading cell: " + this.WX + ", " + this.WY + " (" + ServerMap.instance.toWorldCellX(this.WX) + ", " + ServerMap.instance.toWorldCellX(this.WY) + ")");
				}

				if (ChunkRevisions.USE_CHUNK_REVISIONS) {
					ChunkRevisions.instance.processRevisedSquares();
				}

				for (int int1 = 0; int1 < 7; ++int1) {
					for (int int2 = 0; int2 < 7; ++int2) {
						if (this.chunks[int1][int2] != null) {
							this.chunks[int1][int2].removeFromWorld();
							chunkLoader.addSaveUnloadedJob(this.chunks[int1][int2]);
							this.chunks[int1][int2] = null;
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
				for (int int1 = 0; int1 < 7; ++int1) {
					for (int int2 = 0; int2 < 7; ++int2) {
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
					throw new IllegalArgumentException("no zombie with id " + short1 + "");
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
}
