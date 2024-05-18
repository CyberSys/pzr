package zombie.iso.areas;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import zombie.VirtualZombieManager;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoRoomLight;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.MetaObject;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoLightSwitch;
import zombie.network.GameServer;


public class IsoRoom {
	public Vector Beds = new Vector();
	public Rectangle bounds;
	public IsoBuilding building = null;
	public ArrayList Containers = new ArrayList();
	public ArrayList Windows = new ArrayList();
	public Vector Exits = new Vector();
	public int layer;
	public String RoomDef = "none";
	public Vector TileList = new Vector();
	public int transparentWalls = 0;
	public boolean lit = false;
	public ArrayList lightSwitches = new ArrayList();
	public ArrayList lights = new ArrayList();
	public ArrayList roomLights = new ArrayList();
	public ArrayList WaterSources = new ArrayList();
	public int seen = 1000000000;
	public int visited = 1000000000;
	public RoomDef def;
	public ArrayList rects = new ArrayList(1);
	public ArrayList Squares = new ArrayList();
	private Vector tempRects = new Vector();

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
		this.WaterSources = arrayList;
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
		IsoWorld.instance.getCell().roomSpotted(this);
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

	private void subdivide(int int1, int int2, int int3, int int4) {
		if (int3 <= 10 && int4 <= 10) {
			this.tempRects.add(new RoomDef.RoomRect(int1, int2, int3, int4));
		} else {
			if (int3 >= int4) {
				this.subdivide(int1, int2, int3 / 2, int4);
				this.subdivide(int1 + int3 / 2, int2, int3 - int3 / 2, int4);
			} else {
				this.subdivide(int1, int2, int3, int4 / 2);
				this.subdivide(int1, int2 + int4 / 2, int3, int4 - int4 / 2);
			}
		}
	}

	public void createLights1(boolean boolean1) {
		this.lights.clear();
		for (int int1 = 0; int1 < this.def.rects.size(); ++int1) {
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)this.def.rects.get(int1);
			int int2 = roomRect.w + roomRect.h;
			IsoLightSource lightSource = new IsoLightSource(roomRect.x + roomRect.w / 2, roomRect.y + roomRect.h / 2, this.def.level, 0.9F, 0.8F, 0.7F, (int)((float)int2 * 0.6F));
			lightSource.bActive = boolean1;
			lightSource.bWasActive = lightSource.bActive;
			lightSource.bHydroPowered = true;
			this.lights.add(lightSource);
		}
	}

	public void createLights2(boolean boolean1) {
		this.lights.clear();
		for (int int1 = 0; int1 < this.def.rects.size(); ++int1) {
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)this.def.rects.get(int1);
			this.tempRects.clear();
			this.subdivide(roomRect.x, roomRect.y, roomRect.w, roomRect.h);
			Iterator iterator = this.tempRects.iterator();
			while (iterator.hasNext()) {
				RoomDef.RoomRect roomRect2 = (RoomDef.RoomRect)iterator.next();
				int int2 = roomRect2.w + roomRect2.h;
				byte byte1 = 15;
				IsoLightSource lightSource = new IsoLightSource(roomRect2.x + roomRect2.w / 2, roomRect2.y + roomRect2.h / 2, this.def.level, 0.9F, 0.8F, 0.7F, (int)((float)byte1 * 0.6F));
				lightSource.bActive = true;
				lightSource.bWasActive = lightSource.bActive;
				lightSource.bHydroPowered = true;
				this.lights.add(lightSource);
			}
		}
	}

	public void createLights3(boolean boolean1) {
		this.lights.clear();
		int int1 = 10000000;
		int int2 = 1000000;
		int int3 = -1;
		int int4 = -1;
		int int5;
		for (int5 = 0; int5 < this.def.rects.size(); ++int5) {
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)this.def.rects.get(int5);
			int1 = Math.min(roomRect.x, int1);
			int2 = Math.min(roomRect.y, int2);
			int3 = Math.max(roomRect.x + roomRect.w, int3);
			int4 = Math.max(roomRect.y + roomRect.h, int4);
		}

		for (int5 = (int)Math.floor((double)((float)int1 / 5.0F)); (double)int5 <= Math.ceil((double)((float)int3 / 5.0F)); ++int5) {
			for (int int6 = (int)Math.floor((double)((float)int2 / 5.0F)); (double)int6 <= Math.ceil((double)((float)int4 / 5.0F)); ++int6) {
				int int7 = int5 * 5;
				int int8 = int6 * 5;
				byte byte1 = 5;
				byte byte2 = 5;
				for (int int9 = 0; int9 < this.def.rects.size(); ++int9) {
					RoomDef.RoomRect roomRect2 = (RoomDef.RoomRect)this.def.rects.get(int9);
					if (roomRect2.x + roomRect2.w > int7 && roomRect2.x < int7 + byte1 && roomRect2.y + roomRect2.h > int8 && roomRect2.y < int8 + byte2) {
						int int10 = Math.max(roomRect2.x, int7);
						int int11 = Math.min(roomRect2.x + roomRect2.w, int7 + byte1);
						int int12 = Math.max(roomRect2.y, int8);
						int int13 = Math.min(roomRect2.y + roomRect2.h, int8 + byte2);
						int int14 = int10 + (int11 - int10) / 2;
						int int15 = int12 + (int13 - int12) / 2;
						boolean boolean2 = false;
						Iterator iterator = this.lights.iterator();
						IsoLightSource lightSource;
						while (iterator.hasNext()) {
							lightSource = (IsoLightSource)iterator.next();
							if (IsoUtils.DistanceTo2D((float)int14, (float)int15, (float)lightSource.x, (float)lightSource.y) < 5.0F) {
								boolean2 = true;
								break;
							}
						}

						if (!boolean2) {
							byte byte3 = 10;
							lightSource = new IsoLightSource(int14, int15, this.def.level, 0.9F, 0.8F, 0.7F, (int)((float)byte3 * 0.6F));
							lightSource.bActive = true;
							lightSource.bWasActive = lightSource.bActive;
							lightSource.bHydroPowered = true;
							this.lights.add(lightSource);
						}
					}
				}
			}
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
}
