package zombie.iso;

import java.util.ArrayList;
import zombie.core.Rand;


public class RoomDef {
	public boolean bExplored = false;
	public int IndoorZombies = 0;
	public boolean bLightsActive = false;
	public String name;
	public int level;
	public BuildingDef building;
	public int ID = -1;
	public ArrayList rects = new ArrayList(1);
	public ArrayList objects = new ArrayList(0);
	public int x = 100000;
	public int y = 100000;
	public int x2 = -10000;
	public int y2 = -10000;
	public int area;

	public boolean isInside(int int1, int int2, int int3) {
		int int4 = this.building.x;
		int int5 = this.building.y;
		for (int int6 = 0; int6 < this.rects.size(); ++int6) {
			int int7 = ((RoomDef.RoomRect)this.rects.get(int6)).x;
			int int8 = ((RoomDef.RoomRect)this.rects.get(int6)).y;
			int int9 = ((RoomDef.RoomRect)this.rects.get(int6)).getX2();
			int int10 = ((RoomDef.RoomRect)this.rects.get(int6)).getY2();
			if (int1 >= int7 && int2 >= int8 && int1 < int9 && int2 < int10 && int3 == this.level) {
				return true;
			}
		}

		return false;
	}

	public boolean intersects(int int1, int int2, int int3, int int4) {
		for (int int5 = 0; int5 < this.rects.size(); ++int5) {
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)this.rects.get(int5);
			if (int1 + int3 > roomRect.getX() && int1 < roomRect.getX2() && int2 + int4 > roomRect.getY() && int2 < roomRect.getY2()) {
				return true;
			}
		}

		return false;
	}

	public ArrayList getObjects() {
		return this.objects;
	}

	public ArrayList getMetaObjects() {
		return this.objects;
	}

	public BuildingDef getBuilding() {
		return this.building;
	}

	public String getName() {
		return this.name;
	}

	public ArrayList getRects() {
		return this.rects;
	}

	public int getY() {
		return this.y;
	}

	public int getX() {
		return this.x;
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

	public int getZ() {
		return this.level;
	}

	public void CalculateBounds() {
		for (int int1 = 0; int1 < this.rects.size(); ++int1) {
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)this.rects.get(int1);
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

			this.area += roomRect.w * roomRect.h;
		}
	}

	public RoomDef(int int1, String string) {
		this.ID = int1;
		this.name = string;
	}

	public void setBuilding(BuildingDef buildingDef) {
		this.building = buildingDef;
	}

	public int getArea() {
		return this.area;
	}

	public void setExplored(boolean boolean1) {
		this.bExplored = boolean1;
	}

	public IsoGridSquare getFreeSquare() {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < this.rects.size(); ++int1) {
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)this.rects.get(int1);
			for (int int2 = roomRect.getX(); int2 < roomRect.getX2(); ++int2) {
				for (int int3 = roomRect.getY(); int3 < roomRect.getY2(); ++int3) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, this.getZ());
					if (square != null && square.isFree(false)) {
						arrayList.add(square);
					}
				}
			}
		}

		if (!arrayList.isEmpty()) {
			return (IsoGridSquare)arrayList.get(Rand.Next(arrayList.size()));
		} else {
			return null;
		}
	}

	public boolean isEmptyOutside() {
		return "emptyoutside".equalsIgnoreCase(this.name);
	}

	public static class RoomRect {
		public int x;
		public int y;
		public int w;
		public int h;

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public int getX2() {
			return this.x + this.w;
		}

		public int getY2() {
			return this.y + this.h;
		}

		public int getW() {
			return this.w;
		}

		public int getH() {
			return this.h;
		}

		public RoomRect(int int1, int int2, int int3, int int4) {
			this.x = int1;
			this.y = int2;
			this.w = int3;
			this.h = int4;
		}
	}
}
