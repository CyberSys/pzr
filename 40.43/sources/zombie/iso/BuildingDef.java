package zombie.iso;

import gnu.trove.list.array.TShortArrayList;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.core.stash.StashSystem;
import zombie.network.GameServer;
import zombie.network.ServerMap;


public class BuildingDef {
	public KahluaTable table = null;
	private int keySpawned = 0;
	public boolean seen = false;
	public boolean hasBeenVisited = false;
	public String stash = null;
	public int lootRespawnHour = -1;
	public TShortArrayList overlappedChunks;
	public boolean bAlarmed = false;
	private int keyId = -1;
	int ID = 0;
	public int x = 10000000;
	public int y = 10000000;
	public int x2 = -10000000;
	public int y2 = -10000000;
	public ArrayList rooms = new ArrayList();
	public final ArrayList emptyoutside = new ArrayList();
	static ArrayList squareChoices = new ArrayList();
	public IsoMetaGrid.Zone zone;

	public BuildingDef() {
		this.table = LuaManager.platform.newTable();
		this.setKeyId(Rand.Next(100000000));
	}

	public KahluaTable getTable() {
		return this.table;
	}

	public ArrayList getRooms() {
		return this.rooms;
	}

	public void setAllExplored(boolean boolean1) {
		for (int int1 = 0; int1 < this.rooms.size(); ++int1) {
			RoomDef roomDef = (RoomDef)this.rooms.get(int1);
			roomDef.setExplored(boolean1);
		}
	}

	public boolean isAllExplored() {
		for (int int1 = 0; int1 < this.rooms.size(); ++int1) {
			if (!((RoomDef)this.rooms.get(int1)).bExplored) {
				return false;
			}
		}

		return true;
	}

	public RoomDef getFirstRoom() {
		return (RoomDef)this.rooms.get(0);
	}

	public int getChunkX() {
		return this.x / 10;
	}

	public int getChunkY() {
		return this.y / 10;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getX2() {
		return this.x2;
	}

	public int getY2() {
		return this.y2;
	}

	public int getW() {
		return this.x2 - this.x;
	}

	public int getH() {
		return this.y2 - this.y;
	}

	public int getID() {
		return this.ID;
	}

	public void CalculateBounds(ArrayList arrayList) {
		int int1;
		RoomDef roomDef;
		int int2;
		RoomDef.RoomRect roomRect;
		for (int1 = 0; int1 < this.rooms.size(); ++int1) {
			roomDef = (RoomDef)this.rooms.get(int1);
			for (int2 = 0; int2 < roomDef.rects.size(); ++int2) {
				roomRect = (RoomDef.RoomRect)roomDef.rects.get(int2);
				if (roomRect.x < this.x) {
					this.x = roomRect.x;
				}

				if (roomRect.y < this.y) {
					this.y = roomRect.y;
				}

				if (roomRect.x + roomRect.w > this.x2) {
					this.x2 = roomRect.x + roomRect.w;
				}

				if (roomRect.y + roomRect.h > this.y2) {
					this.y2 = roomRect.y + roomRect.h;
				}
			}
		}

		for (int1 = 0; int1 < this.emptyoutside.size(); ++int1) {
			roomDef = (RoomDef)this.emptyoutside.get(int1);
			for (int2 = 0; int2 < roomDef.rects.size(); ++int2) {
				roomRect = (RoomDef.RoomRect)roomDef.rects.get(int2);
				if (roomRect.x < this.x) {
					this.x = roomRect.x;
				}

				if (roomRect.y < this.y) {
					this.y = roomRect.y;
				}

				if (roomRect.x + roomRect.w > this.x2) {
					this.x2 = roomRect.x + roomRect.w;
				}

				if (roomRect.y + roomRect.h > this.y2) {
					this.y2 = roomRect.y + roomRect.h;
				}
			}
		}

		int int3 = this.x / 10;
		int2 = this.y / 10;
		int int4 = (this.x2 + 0) / 10;
		int int5 = (this.y2 + 0) / 10;
		this.overlappedChunks = new TShortArrayList((int4 - int3 + 1) * (int5 - int2 + 1) * 2);
		this.overlappedChunks.clear();
		arrayList.clear();
		arrayList.addAll(this.rooms);
		arrayList.addAll(this.emptyoutside);
		for (int int6 = 0; int6 < arrayList.size(); ++int6) {
			RoomDef roomDef2 = (RoomDef)arrayList.get(int6);
			for (int int7 = 0; int7 < roomDef2.rects.size(); ++int7) {
				RoomDef.RoomRect roomRect2 = (RoomDef.RoomRect)roomDef2.rects.get(int7);
				int3 = roomRect2.x / 10;
				int2 = roomRect2.y / 10;
				int4 = (roomRect2.x + roomRect2.w + 0) / 10;
				int5 = (roomRect2.y + roomRect2.h + 0) / 10;
				for (int int8 = int2; int8 <= int5; ++int8) {
					for (int int9 = int3; int9 <= int4; ++int9) {
						if (!this.overlapsChunk(int9, int8)) {
							this.overlappedChunks.add((short)int9);
							this.overlappedChunks.add((short)int8);
						}
					}
				}
			}
		}
	}

	public boolean overlapsChunk(int int1, int int2) {
		for (int int3 = 0; int3 < this.overlappedChunks.size(); int3 += 2) {
			if (int1 == this.overlappedChunks.get(int3) && int2 == this.overlappedChunks.get(int3 + 1)) {
				return true;
			}
		}

		return false;
	}

	public IsoGridSquare getFreeSquareInRoom() {
		squareChoices.clear();
		for (int int1 = 0; int1 < this.rooms.size(); ++int1) {
			RoomDef roomDef = (RoomDef)this.rooms.get(int1);
			for (int int2 = 0; int2 < roomDef.rects.size(); ++int2) {
				RoomDef.RoomRect roomRect = (RoomDef.RoomRect)roomDef.rects.get(int2);
				for (int int3 = roomRect.getX(); int3 < roomRect.getX2(); ++int3) {
					for (int int4 = roomRect.getY(); int4 < roomRect.getY2(); ++int4) {
						IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, roomDef.getZ());
						if (square != null && square.isFree(false)) {
							squareChoices.add(square);
						}
					}
				}
			}
		}

		if (!squareChoices.isEmpty()) {
			return (IsoGridSquare)squareChoices.get(Rand.Next(squareChoices.size()));
		} else {
			return null;
		}
	}

	public boolean isFullyStreamedIn() {
		for (int int1 = 0; int1 < this.overlappedChunks.size(); int1 += 2) {
			short short1 = this.overlappedChunks.get(int1);
			short short2 = this.overlappedChunks.get(int1 + 1);
			IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(short1, short2) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(short1 * 10, short2 * 10, 0);
			if (chunk == null) {
				return false;
			}
		}

		return true;
	}

	public IsoMetaGrid.Zone getZone() {
		return this.zone;
	}

	public int getKeyId() {
		return this.keyId;
	}

	public void setKeyId(int int1) {
		this.keyId = int1;
	}

	public int getKeySpawned() {
		return this.keySpawned;
	}

	public void setKeySpawned(int int1) {
		this.keySpawned = int1;
	}

	public boolean isHasBeenVisited() {
		return this.hasBeenVisited;
	}

	public void setHasBeenVisited(boolean boolean1) {
		if (boolean1 && !this.hasBeenVisited) {
			StashSystem.visitedBuilding(this);
		}

		this.hasBeenVisited = boolean1;
	}
}
