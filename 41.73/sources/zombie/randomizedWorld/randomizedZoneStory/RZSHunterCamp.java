package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMetaGrid;


public class RZSHunterCamp extends RandomizedZoneStoryBase {

	public RZSHunterCamp() {
		this.name = "Hunter Forest Camp";
		this.chance = 5;
		this.minZoneHeight = 6;
		this.minZoneWidth = 6;
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Forest.toString());
	}

	public static ArrayList getForestClutter() {
		ArrayList arrayList = new ArrayList();
		arrayList.add("Base.VarmintRifle");
		arrayList.add("Base.223Box");
		arrayList.add("Base.HuntingRifle");
		arrayList.add("Base.308Box");
		arrayList.add("Base.Shotgun");
		arrayList.add("Base.ShotgunShellsBox");
		arrayList.add("Base.DoubleBarrelShotgun");
		arrayList.add("Base.AssaultRifle");
		arrayList.add("Base.556Box");
		return arrayList;
	}

	public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
		int int1 = zone.pickedXForZoneStory;
		int int2 = zone.pickedYForZoneStory;
		ArrayList arrayList = getForestClutter();
		this.cleanAreaForStory(this, zone);
		this.addVehicle(zone, this.getSq(zone.x, zone.y, zone.z), (IsoChunk)null, (String)null, "Base.OffRoad", (Integer)null, (IsoDirections)null, "Hunter");
		this.addTileObject(int1, int2, zone.z, "camping_01_6");
		int int3 = Rand.Next(-1, 2);
		int int4 = Rand.Next(-1, 2);
		this.addTentWestEast(int1 + int3 - 2, int2 + int4, zone.z);
		if (Rand.Next(100) < 70) {
			this.addTentNorthSouth(int1 + int3, int2 + int4 - 2, zone.z);
		}

		if (Rand.Next(100) < 30) {
			this.addTentNorthSouth(int1 + int3 + 1, int2 + int4 - 2, zone.z);
		}

		int int5 = Rand.Next(2, 5);
		for (int int6 = 0; int6 < int5; ++int6) {
			this.addItemOnGround(this.getRandomFreeSquare(this, zone), (String)arrayList.get(Rand.Next(arrayList.size())));
		}

		this.addZombiesOnSquare(Rand.Next(2, 5), "Hunter", 0, this.getRandomFreeSquare(this, zone));
	}
}
