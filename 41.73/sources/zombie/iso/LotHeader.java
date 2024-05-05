package zombie.iso;

import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;


public final class LotHeader {
	public int cellX;
	public int cellY;
	public int width = 0;
	public int height = 0;
	public int levels = 0;
	public int version = 0;
	public final HashMap Rooms = new HashMap();
	public final TLongObjectHashMap RoomByMetaID = new TLongObjectHashMap();
	public final ArrayList RoomList = new ArrayList();
	public final ArrayList Buildings = new ArrayList();
	public final TLongObjectHashMap BuildingByMetaID = new TLongObjectHashMap();
	public final HashMap isoRooms = new HashMap();
	public final HashMap isoBuildings = new HashMap();
	public boolean bFixed2x;
	protected final ArrayList tilesUsed = new ArrayList();

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getLevels() {
		return this.levels;
	}

	public IsoRoom getRoom(int int1) {
		RoomDef roomDef = (RoomDef)this.Rooms.get(int1);
		IsoRoom room;
		if (!this.isoRooms.containsKey(int1)) {
			room = new IsoRoom();
			room.rects.addAll(roomDef.rects);
			room.RoomDef = roomDef.name;
			room.def = roomDef;
			room.layer = roomDef.level;
			IsoWorld.instance.CurrentCell.getRoomList().add(room);
			if (roomDef.building == null) {
				roomDef.building = new BuildingDef();
				roomDef.building.ID = this.Buildings.size();
				roomDef.building.rooms.add(roomDef);
				roomDef.building.CalculateBounds(new ArrayList());
				roomDef.building.metaID = roomDef.building.calculateMetaID(this.cellX, this.cellY);
				this.Buildings.add(roomDef.building);
			}

			int int2 = roomDef.building.ID;
			this.isoRooms.put(int1, room);
			if (!this.isoBuildings.containsKey(int2)) {
				room.building = new IsoBuilding();
				room.building.def = roomDef.building;
				this.isoBuildings.put(int2, room.building);
				room.building.CreateFrom(roomDef.building, this);
			} else {
				room.building = (IsoBuilding)this.isoBuildings.get(int2);
			}

			return room;
		} else {
			room = (IsoRoom)this.isoRooms.get(int1);
			return room;
		}
	}

	@Deprecated
	public int getRoomAt(int int1, int int2, int int3) {
		Iterator iterator = this.Rooms.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			RoomDef roomDef = (RoomDef)entry.getValue();
			for (int int4 = 0; int4 < roomDef.rects.size(); ++int4) {
				RoomDef.RoomRect roomRect = (RoomDef.RoomRect)roomDef.rects.get(int4);
				if (roomRect.x <= int1 && roomRect.y <= int2 && roomDef.level == int3 && roomRect.getX2() > int1 && roomRect.getY2() > int2) {
					return (Integer)entry.getKey();
				}
			}
		}

		return -1;
	}

	public void Dispose() {
		this.Rooms.clear();
		this.RoomByMetaID.clear();
		this.RoomList.clear();
		this.Buildings.clear();
		this.BuildingByMetaID.clear();
		this.isoRooms.clear();
		this.isoBuildings.clear();
		this.tilesUsed.clear();
	}
}
