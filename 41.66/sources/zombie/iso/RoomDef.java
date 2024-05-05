package zombie.iso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import zombie.iso.areas.IsoRoom;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.util.list.PZArrayUtil;


public final class RoomDef {
	private static final ArrayList squareChoices = new ArrayList();
	public boolean bExplored = false;
	public boolean bDoneSpawn = false;
	public int IndoorZombies = 0;
	public int spawnCount = -1;
	public boolean bLightsActive = false;
	public String name;
	public int level;
	public BuildingDef building;
	public int ID = -1;
	public final ArrayList rects = new ArrayList(1);
	public final ArrayList objects = new ArrayList(0);
	public int x = 100000;
	public int y = 100000;
	public int x2 = -10000;
	public int y2 = -10000;
	public int area;
	private final HashMap proceduralSpawnedContainer = new HashMap();
	private boolean roofFixed = false;

	public RoomDef(int int1, String string) {
		this.ID = int1;
		this.name = string;
	}

	public int getID() {
		return this.ID;
	}

	public boolean isExplored() {
		return this.bExplored;
	}

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

	public float getAreaOverlapping(IsoChunk chunk) {
		return this.getAreaOverlapping(chunk.wx * 10, chunk.wy * 10, 10, 10);
	}

	public float getAreaOverlapping(int int1, int int2, int int3, int int4) {
		int int5 = 0;
		int int6 = 0;
		for (int int7 = 0; int7 < this.rects.size(); ++int7) {
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)this.rects.get(int7);
			int5 += roomRect.w * roomRect.h;
			int int8 = Math.max(int1, roomRect.x);
			int int9 = Math.max(int2, roomRect.y);
			int int10 = Math.min(int1 + int3, roomRect.x + roomRect.w);
			int int11 = Math.min(int2 + int4, roomRect.y + roomRect.h);
			if (int10 >= int8 && int11 >= int9) {
				int6 += (int10 - int8) * (int11 - int9);
			}
		}

		if (int6 <= 0) {
			return 0.0F;
		} else {
			return (float)int6 / (float)int5;
		}
	}

	public void forEachChunk(BiConsumer biConsumer) {
		HashSet hashSet = new HashSet();
		for (int int1 = 0; int1 < this.rects.size(); ++int1) {
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)this.rects.get(int1);
			int int2 = roomRect.x / 10;
			int int3 = roomRect.y / 10;
			int int4 = (roomRect.x + roomRect.w) / 10;
			int int5 = (roomRect.y + roomRect.h) / 10;
			if ((roomRect.x + roomRect.w) % 10 == 0) {
				--int4;
			}

			if ((roomRect.y + roomRect.h) % 10 == 0) {
				--int5;
			}

			for (int int6 = int3; int6 <= int5; ++int6) {
				for (int int7 = int2; int7 <= int4; ++int7) {
					IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int7, int6) : IsoWorld.instance.CurrentCell.getChunk(int7, int6);
					if (chunk != null) {
						hashSet.add(chunk);
					}
				}
			}
		}

		hashSet.forEach((hashSetx)->{
			biConsumer.accept(this, hashSetx);
		});
		hashSet.clear();
	}

	public IsoRoom getIsoRoom() {
		return IsoWorld.instance.MetaGrid.getMetaGridFromTile(this.x, this.y).info.getRoom(this.ID);
	}

	public ArrayList getObjects() {
		return this.objects;
	}

	public ArrayList getMetaObjects() {
		return this.objects;
	}

	public void refreshSquares() {
		this.getIsoRoom().refreshSquares();
	}

	public BuildingDef getBuilding() {
		return this.building;
	}

	public void setBuilding(BuildingDef buildingDef) {
		this.building = buildingDef;
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

	public int getArea() {
		return this.area;
	}

	public void setExplored(boolean boolean1) {
		this.bExplored = boolean1;
	}

	public IsoGridSquare getFreeSquare() {
		return this.getRandomSquare((var0)->{
			return var0.isFree(false);
		});
	}

	public IsoGridSquare getRandomSquare(Predicate predicate) {
		squareChoices.clear();
		for (int int1 = 0; int1 < this.rects.size(); ++int1) {
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)this.rects.get(int1);
			for (int int2 = roomRect.getX(); int2 < roomRect.getX2(); ++int2) {
				for (int int3 = roomRect.getY(); int3 < roomRect.getY2(); ++int3) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, this.getZ());
					if (square != null && predicate != null && predicate.test(square) || predicate == null) {
						squareChoices.add(square);
					}
				}
			}
		}

		return (IsoGridSquare)PZArrayUtil.pickRandom((List)squareChoices);
	}

	public boolean isEmptyOutside() {
		return "emptyoutside".equalsIgnoreCase(this.name);
	}

	public HashMap getProceduralSpawnedContainer() {
		return this.proceduralSpawnedContainer;
	}

	public boolean isRoofFixed() {
		return this.roofFixed;
	}

	public void setRoofFixed(boolean boolean1) {
		this.roofFixed = boolean1;
	}

	public void Dispose() {
		this.building = null;
		this.rects.clear();
		this.objects.clear();
		this.proceduralSpawnedContainer.clear();
	}

	public static class RoomRect {
		public int x;
		public int y;
		public int w;
		public int h;

		public RoomRect(int int1, int int2, int int3, int int4) {
			this.x = int1;
			this.y = int2;
			this.w = int3;
			this.h = int4;
		}

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
	}
}
