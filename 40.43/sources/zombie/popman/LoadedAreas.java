package zombie.popman;

import zombie.characters.IsoPlayer;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.iso.Vector3;
import zombie.network.GameServer;
import zombie.network.ServerMap;


final class LoadedAreas {
	public static final int MAX_AREAS = 64;
	public int[] areas = new int[256];
	public int count;
	public boolean changed;
	public int[] prevAreas = new int[256];
	public int prevCount;
	private boolean serverCells;

	public LoadedAreas(boolean boolean1) {
		this.serverCells = boolean1;
	}

	public boolean set() {
		this.setPrev();
		this.clear();
		int int1;
		if (GameServer.bServer) {
			if (this.serverCells) {
				for (int1 = 0; int1 < ServerMap.instance.LoadedCells.size(); ++int1) {
					ServerMap.ServerCell serverCell = (ServerMap.ServerCell)ServerMap.instance.LoadedCells.get(int1);
					this.add(serverCell.WX * 7, serverCell.WY * 7, 7, 7);
				}
			} else {
				int int2;
				for (int1 = 0; int1 < GameServer.Players.size(); ++int1) {
					IsoPlayer player = (IsoPlayer)GameServer.Players.get(int1);
					int2 = (int)player.x / 10;
					int int3 = (int)player.y / 10;
					this.add(int2 - player.OnlineChunkGridWidth / 2, int3 - player.OnlineChunkGridWidth / 2, player.OnlineChunkGridWidth, player.OnlineChunkGridWidth);
				}

				for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
					for (int2 = 0; int2 < 4; ++int2) {
						Vector3 vector3 = udpConnection.connectArea[int2];
						if (vector3 != null) {
							int int4 = (int)vector3.z;
							this.add((int)vector3.x - int4 / 2, (int)vector3.y - int4 / 2, int4, int4);
						}
					}
				}
			}
		} else {
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int1];
				if (!chunkMap.ignore) {
					this.add(chunkMap.getWorldXMin(), chunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth, IsoChunkMap.ChunkGridWidth);
				}
			}
		}

		return this.changed = this.compareWithPrev();
	}

	public void add(int int1, int int2, int int3, int int4) {
		if (this.count < 64) {
			int int5 = this.count * 4;
			this.areas[int5++] = int1;
			this.areas[int5++] = int2;
			this.areas[int5++] = int3;
			this.areas[int5++] = int4;
			++this.count;
		}
	}

	public void clear() {
		this.count = 0;
		this.changed = false;
	}

	public void copy(LoadedAreas loadedAreas) {
		this.count = loadedAreas.count;
		for (int int1 = 0; int1 < this.count; ++int1) {
			int int2 = int1 * 4;
			this.areas[int2] = loadedAreas.areas[int2++];
			this.areas[int2] = loadedAreas.areas[int2++];
			this.areas[int2] = loadedAreas.areas[int2++];
			this.areas[int2] = loadedAreas.areas[int2++];
		}
	}

	private void setPrev() {
		this.prevCount = this.count;
		for (int int1 = 0; int1 < this.count; ++int1) {
			int int2 = int1 * 4;
			this.prevAreas[int2] = this.areas[int2++];
			this.prevAreas[int2] = this.areas[int2++];
			this.prevAreas[int2] = this.areas[int2++];
			this.prevAreas[int2] = this.areas[int2++];
		}
	}

	private boolean compareWithPrev() {
		if (this.prevCount != this.count) {
			return true;
		} else {
			for (int int1 = 0; int1 < this.count; ++int1) {
				int int2 = int1 * 4;
				if (this.prevAreas[int2] != this.areas[int2++]) {
					return true;
				}

				if (this.prevAreas[int2] != this.areas[int2++]) {
					return true;
				}

				if (this.prevAreas[int2] != this.areas[int2++]) {
					return true;
				}

				if (this.prevAreas[int2] != this.areas[int2++]) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean isOnEdge(int int1, int int2) {
		if (int1 % 10 != 0 && (int1 + 1) % 10 != 0 && int2 % 10 != 0 && (int2 + 1) % 10 != 0) {
			return false;
		} else {
			int int3 = 0;
			while (int3 < this.count) {
				int int4 = int3 * 4;
				int int5 = this.areas[int4++] * 10;
				int int6 = this.areas[int4++] * 10;
				int int7 = int5 + this.areas[int4++] * 10;
				int int8 = int6 + this.areas[int4++] * 10;
				boolean boolean1 = int1 >= int5 && int1 < int7;
				boolean boolean2 = int2 >= int6 && int2 < int8;
				if (!boolean1 || int2 != int6 && int2 != int8 - 1) {
					if (!boolean2 || int1 != int5 && int1 != int7 - 1) {
						++int3;
						continue;
					}

					return true;
				}

				return true;
			}

			return false;
		}
	}
}
