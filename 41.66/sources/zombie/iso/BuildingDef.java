package zombie.iso;

import gnu.trove.list.array.TShortArrayList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.core.stash.StashSystem;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.iso.areas.IsoRoom;
import zombie.network.GameServer;
import zombie.network.ServerMap;


public final class BuildingDef {
	static final ArrayList squareChoices = new ArrayList();
	public final ArrayList emptyoutside = new ArrayList();
	public KahluaTable table = null;
	public boolean seen = false;
	public boolean hasBeenVisited = false;
	public String stash = null;
	public int lootRespawnHour = -1;
	public TShortArrayList overlappedChunks;
	public boolean bAlarmed = false;
	public int x = 10000000;
	public int y = 10000000;
	public int x2 = -10000000;
	public int y2 = -10000000;
	public final ArrayList rooms = new ArrayList();
	public IsoMetaGrid.Zone zone;
	public int food;
	public ArrayList items = new ArrayList();
	public HashSet itemTypes = new HashSet();
	int ID = 0;
	private int keySpawned = 0;
	private int keyId = -1;

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

	public RoomDef getRoom(String string) {
		for (int int1 = 0; int1 < this.rooms.size(); ++int1) {
			RoomDef roomDef = (RoomDef)this.rooms.get(int1);
			if (roomDef.getName().equalsIgnoreCase(string)) {
				return roomDef;
			}
		}

		return null;
	}

	public boolean isAllExplored() {
		for (int int1 = 0; int1 < this.rooms.size(); ++int1) {
			if (!((RoomDef)this.rooms.get(int1)).bExplored) {
				return false;
			}
		}

		return true;
	}

	public void setAllExplored(boolean boolean1) {
		for (int int1 = 0; int1 < this.rooms.size(); ++int1) {
			RoomDef roomDef = (RoomDef)this.rooms.get(int1);
			roomDef.setExplored(boolean1);
		}
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

	public void refreshSquares() {
		for (int int1 = 0; int1 < this.rooms.size(); ++int1) {
			RoomDef roomDef = (RoomDef)this.rooms.get(int1);
			roomDef.refreshSquares();
		}
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

		int1 = this.x / 10;
		int int3 = this.y / 10;
		int2 = (this.x2 + 0) / 10;
		int int4 = (this.y2 + 0) / 10;
		this.overlappedChunks = new TShortArrayList((int2 - int1 + 1) * (int4 - int3 + 1) * 2);
		this.overlappedChunks.clear();
		arrayList.clear();
		arrayList.addAll(this.rooms);
		arrayList.addAll(this.emptyoutside);
		for (int int5 = 0; int5 < arrayList.size(); ++int5) {
			RoomDef roomDef2 = (RoomDef)arrayList.get(int5);
			for (int int6 = 0; int6 < roomDef2.rects.size(); ++int6) {
				RoomDef.RoomRect roomRect2 = (RoomDef.RoomRect)roomDef2.rects.get(int6);
				int1 = roomRect2.x / 10;
				int3 = roomRect2.y / 10;
				int2 = (roomRect2.x + roomRect2.w + 0) / 10;
				int4 = (roomRect2.y + roomRect2.h + 0) / 10;
				for (int int7 = int3; int7 <= int4; ++int7) {
					for (int int8 = int1; int8 <= int2; ++int8) {
						if (!this.overlapsChunk(int8, int7)) {
							this.overlappedChunks.add((short)int8);
							this.overlappedChunks.add((short)int7);
						}
					}
				}
			}
		}
	}

	public void recalculate() {
		this.food = 0;
		this.items.clear();
		this.itemTypes.clear();
		for (int int1 = 0; int1 < this.rooms.size(); ++int1) {
			IsoRoom room = ((RoomDef)this.rooms.get(int1)).getIsoRoom();
			for (int int2 = 0; int2 < room.Containers.size(); ++int2) {
				ItemContainer itemContainer = (ItemContainer)room.Containers.get(int2);
				for (int int3 = 0; int3 < itemContainer.Items.size(); ++int3) {
					InventoryItem inventoryItem = (InventoryItem)itemContainer.Items.get(int3);
					this.items.add(inventoryItem);
					this.itemTypes.add(inventoryItem.getFullType());
					if (inventoryItem instanceof Food) {
						++this.food;
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

	public boolean containsRoom(String string) {
		for (int int1 = 0; int1 < this.rooms.size(); ++int1) {
			RoomDef roomDef = (RoomDef)this.rooms.get(int1);
			if (roomDef.name.equals(string)) {
				return true;
			}
		}

		return false;
	}

	public boolean isFullyStreamedIn() {
		for (int int1 = 0; int1 < this.overlappedChunks.size(); int1 += 2) {
			short short1 = this.overlappedChunks.get(int1);
			short short2 = this.overlappedChunks.get(int1 + 1);
			IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(short1, short2) : IsoWorld.instance.CurrentCell.getChunk(short1, short2);
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

	public boolean isAlarmed() {
		return this.bAlarmed;
	}

	public void setAlarmed(boolean boolean1) {
		this.bAlarmed = boolean1;
	}

	public RoomDef getRandomRoom(int int1) {
		RoomDef roomDef = (RoomDef)this.getRooms().get(Rand.Next(0, this.getRooms().size()));
		if (int1 > 0 && roomDef.area >= int1) {
			return roomDef;
		} else {
			int int2 = 0;
			do {
				if (int2 > 20) {
					return roomDef;
				}

				++int2;
				roomDef = (RoomDef)this.getRooms().get(Rand.Next(0, this.getRooms().size()));
			}	 while (roomDef.area < int1);

			return roomDef;
		}
	}

	public void Dispose() {
		Iterator iterator = this.rooms.iterator();
		while (iterator.hasNext()) {
			RoomDef roomDef = (RoomDef)iterator.next();
			roomDef.Dispose();
		}

		this.emptyoutside.clear();
		this.rooms.clear();
	}
}
