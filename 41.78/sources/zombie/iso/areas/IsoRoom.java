package zombie.iso.areas;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import zombie.VirtualZombieManager;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoRoomLight;
import zombie.iso.IsoWorld;
import zombie.iso.MetaObject;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoLightSwitch;
import zombie.network.GameServer;


public final class IsoRoom {
	private static final ArrayList tempSquares = new ArrayList();
	public final Vector Beds = new Vector();
	public Rectangle bounds;
	public IsoBuilding building = null;
	public final ArrayList Containers = new ArrayList();
	public final ArrayList Windows = new ArrayList();
	public final Vector Exits = new Vector();
	public int layer;
	public String RoomDef = "none";
	public final Vector TileList = new Vector();
	public int transparentWalls = 0;
	public final ArrayList lightSwitches = new ArrayList();
	public final ArrayList roomLights = new ArrayList();
	public final ArrayList WaterSources = new ArrayList();
	public int seen = 1000000000;
	public int visited = 1000000000;
	public RoomDef def;
	public final ArrayList rects = new ArrayList(1);
	public final ArrayList Squares = new ArrayList();

	public IsoBuilding getBuilding() {
		return this.building;
	}

	public String getName() {
		return this.RoomDef;
	}

	public IsoBuilding CreateBuilding(IsoCell cell) {
		IsoBuilding building = new IsoBuilding(cell);
		this.AddToBuilding(building);
		return building;
	}

	public boolean isInside(int int1, int int2, int int3) {
		for (int int4 = 0; int4 < this.rects.size(); ++int4) {
			int int5 = ((RoomDef.RoomRect)this.rects.get(int4)).x;
			int int6 = ((RoomDef.RoomRect)this.rects.get(int4)).y;
			int int7 = ((RoomDef.RoomRect)this.rects.get(int4)).getX2();
			int int8 = ((RoomDef.RoomRect)this.rects.get(int4)).getY2();
			if (int1 >= int5 && int2 >= int6 && int1 < int7 && int2 < int8 && int3 == this.layer) {
				return true;
			}
		}

		return false;
	}

	public IsoGridSquare getFreeTile() {
		boolean boolean1 = false;
		IsoGridSquare square = null;
		int int1 = 100;
		while (!boolean1 && int1 > 0) {
			--int1;
			boolean1 = true;
			if (this.TileList.isEmpty()) {
				return null;
			}

			square = (IsoGridSquare)this.TileList.get(Rand.Next(this.TileList.size()));
			for (int int2 = 0; int2 < this.Exits.size(); ++int2) {
				if (square.getX() == ((IsoRoomExit)this.Exits.get(int2)).x && square.getY() == ((IsoRoomExit)this.Exits.get(int2)).y) {
					boolean1 = false;
				}
			}

			if (boolean1 && !square.isFree(true)) {
				boolean1 = false;
			}
		}

		return int1 < 0 ? null : square;
	}

	void AddToBuilding(IsoBuilding building) {
		this.building = building;
		building.AddRoom(this);
		Iterator iterator = this.Exits.iterator();
		while (iterator.hasNext()) {
			IsoRoomExit roomExit = (IsoRoomExit)iterator.next();
			if (roomExit.To.From != null && roomExit.To.From.building == null) {
				roomExit.To.From.AddToBuilding(building);
			}
		}
	}

	public ArrayList getWaterSources() {
		return this.WaterSources;
	}

	public void setWaterSources(ArrayList arrayList) {
		this.WaterSources.clear();
		this.WaterSources.addAll(arrayList);
	}

	public boolean hasWater() {
		if (this.WaterSources.isEmpty()) {
			return false;
		} else {
			Iterator iterator = this.WaterSources.iterator();
			while (iterator != null && iterator.hasNext()) {
				IsoObject object = (IsoObject)iterator.next();
				if (object.hasWater()) {
					return true;
				}
			}

			return false;
		}
	}

	public void useWater() {
		if (!this.WaterSources.isEmpty()) {
			Iterator iterator = this.WaterSources.iterator();
			while (iterator != null && iterator.hasNext()) {
				IsoObject object = (IsoObject)iterator.next();
				if (object.hasWater()) {
					object.useWater(1);
					break;
				}
			}
		}
	}

	public ArrayList getWindows() {
		return this.Windows;
	}

	public void addSquare(IsoGridSquare square) {
		if (!this.Squares.contains(square)) {
			this.Squares.add(square);
		}
	}

	public void refreshSquares() {
		this.Windows.clear();
		this.Containers.clear();
		this.WaterSources.clear();
		this.Exits.clear();
		tempSquares.clear();
		tempSquares.addAll(this.Squares);
		this.Squares.clear();
		for (int int1 = 0; int1 < tempSquares.size(); ++int1) {
			this.addSquare((IsoGridSquare)tempSquares.get(int1));
		}
	}

	private void addExitTo(IsoGridSquare square, IsoGridSquare square2) {
		IsoRoom room = null;
		IsoRoom room2 = null;
		if (square != null) {
			room = square.getRoom();
		}

		if (square2 != null) {
			room2 = square2.getRoom();
		}

		if (room != null || room2 != null) {
			IsoRoom room3 = room;
			if (room == null) {
				room3 = room2;
			}

			IsoRoomExit roomExit = new IsoRoomExit(room3, square.getX(), square.getY(), square.getZ());
			roomExit.type = IsoRoomExit.ExitType.Door;
			if (room3 == room) {
				if (room2 != null) {
					IsoRoomExit roomExit2 = room2.getExitAt(square2.getX(), square2.getY(), square2.getZ());
					if (roomExit2 == null) {
						roomExit2 = new IsoRoomExit(room2, square2.getX(), square2.getY(), square2.getZ());
						room2.Exits.add(roomExit2);
					}

					roomExit.To = roomExit2;
				} else {
					room.building.Exits.add(roomExit);
					if (square2 != null) {
						roomExit.To = new IsoRoomExit(roomExit, square2.getX(), square2.getY(), square2.getZ());
					}
				}

				room.Exits.add(roomExit);
			} else {
				room2.building.Exits.add(roomExit);
				if (square2 != null) {
					roomExit.To = new IsoRoomExit(roomExit, square2.getX(), square2.getY(), square2.getZ());
				}

				room2.Exits.add(roomExit);
			}
		}
	}

	private IsoRoomExit getExitAt(int int1, int int2, int int3) {
		for (int int4 = 0; int4 < this.Exits.size(); ++int4) {
			IsoRoomExit roomExit = (IsoRoomExit)this.Exits.get(int4);
			if (roomExit.x == int1 && roomExit.y == int2 && roomExit.layer == int3) {
				return roomExit;
			}
		}

		return null;
	}

	public void removeSquare(IsoGridSquare square) {
		this.Squares.remove(square);
		IsoRoomExit roomExit = this.getExitAt(square.getX(), square.getY(), square.getZ());
		if (roomExit != null) {
			this.Exits.remove(roomExit);
			if (roomExit.To != null) {
				roomExit.From = null;
			}

			if (this.building.Exits.contains(roomExit)) {
				this.building.Exits.remove(roomExit);
			}
		}

		for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
			IsoObject object = (IsoObject)square.getObjects().get(int1);
			if (object instanceof IsoLightSwitch) {
				this.lightSwitches.remove(object);
			}
		}
	}

	public void spawnZombies() {
		VirtualZombieManager.instance.addZombiesToMap(1, this.def, false);
	}

	public void onSee() {
		for (int int1 = 0; int1 < this.getBuilding().Rooms.size(); ++int1) {
			IsoRoom room = (IsoRoom)this.getBuilding().Rooms.elementAt(int1);
			if (room != null && !room.def.bExplored) {
				room.def.bExplored = true;
			}

			IsoWorld.instance.getCell().roomSpotted(room);
		}
	}

	public Vector getTileList() {
		return this.TileList;
	}

	public ArrayList getSquares() {
		return this.Squares;
	}

	public ArrayList getContainer() {
		return this.Containers;
	}

	public IsoGridSquare getRandomSquare() {
		return this.Squares.isEmpty() ? null : (IsoGridSquare)this.Squares.get(Rand.Next(this.Squares.size()));
	}

	public IsoGridSquare getRandomFreeSquare() {
		int int1 = 100;
		IsoGridSquare square = null;
		if (GameServer.bServer) {
			while (int1 > 0) {
				square = IsoWorld.instance.CurrentCell.getGridSquare(this.def.getX() + Rand.Next(this.def.getW()), this.def.getY() + Rand.Next(this.def.getH()), this.def.level);
				if (square != null && square.getRoom() == this && square.isFree(true)) {
					return square;
				}

				--int1;
			}

			return null;
		} else if (this.Squares.isEmpty()) {
			return null;
		} else {
			while (int1 > 0) {
				square = (IsoGridSquare)this.Squares.get(Rand.Next(this.Squares.size()));
				if (square.isFree(true)) {
					return square;
				}

				--int1;
			}

			return null;
		}
	}

	public boolean hasLightSwitches() {
		if (!this.lightSwitches.isEmpty()) {
			return true;
		} else {
			for (int int1 = 0; int1 < this.def.objects.size(); ++int1) {
				if (((MetaObject)this.def.objects.get(int1)).getType() == 7) {
					return true;
				}
			}

			return false;
		}
	}

	public void createLights(boolean boolean1) {
		if (this.roomLights.isEmpty()) {
			for (int int1 = 0; int1 < this.def.rects.size(); ++int1) {
				RoomDef.RoomRect roomRect = (RoomDef.RoomRect)this.def.rects.get(int1);
				IsoRoomLight roomLight = new IsoRoomLight(this, roomRect.x, roomRect.y, this.def.level, roomRect.w, roomRect.h);
				this.roomLights.add(roomLight);
			}
		}
	}

	public RoomDef getRoomDef() {
		return this.def;
	}

	public ArrayList getLightSwitches() {
		return this.lightSwitches;
	}
}
