package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMetaGrid;
import zombie.iso.objects.IsoDeadBody;


public class RZSBuryingCamp extends RandomizedZoneStoryBase {

	public RZSBuryingCamp() {
		this.name = "Burying Camp";
		this.chance = 7;
		this.minZoneHeight = 6;
		this.minZoneWidth = 6;
		this.minimumDays = 20;
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Forest.toString());
	}

	public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
		this.cleanAreaForStory(this, zone);
		boolean boolean1 = Rand.NextBool(2);
		int int1 = zone.x + 1;
		int int2 = zone.y + 1;
		int int3 = 0;
		int int4 = 0;
		int int5 = Rand.Next(3, 7);
		for (int int6 = 0; int6 < int5; ++int6) {
			if (boolean1) {
				this.addTileObject(int1 + int6, zone.y + 2, zone.z, "location_community_cemetary_01_22");
				if (int6 == 2) {
					this.addTileObject(int1 + int6, zone.y + 3, zone.z, "location_community_cemetary_01_35");
					this.addTileObject(int1 + int6, zone.y + 4, zone.z, "location_community_cemetary_01_34");
					int3 = int1 + int6;
					int4 = zone.y + 5;
				} else {
					this.addTileObject(int1 + int6, zone.y + 3, zone.z, "location_community_cemetary_01_43");
					this.addTileObject(int1 + int6, zone.y + 4, zone.z, "location_community_cemetary_01_42");
					if (Rand.NextBool(2)) {
						this.addTileObject(int1 + int6, zone.y + 6, zone.z, "vegetation_ornamental_01_" + Rand.Next(16, 19));
					}
				}
			} else {
				this.addTileObject(zone.x + 2, int2 + int6, zone.z, "location_community_cemetary_01_23");
				if (int6 == 2) {
					this.addTileObject(zone.x + 3, int2 + int6, zone.z, "location_community_cemetary_01_32");
					this.addTileObject(zone.x + 4, int2 + int6, zone.z, "location_community_cemetary_01_33");
					int3 = zone.x + 5;
					int4 = int2 + int6;
				} else {
					this.addTileObject(zone.x + 3, int2 + int6, zone.z, "location_community_cemetary_01_40");
					this.addTileObject(zone.x + 4, int2 + int6, zone.z, "location_community_cemetary_01_41");
					if (Rand.NextBool(2)) {
						this.addTileObject(zone.x + 6, int2 + int6, zone.z, "vegetation_ornamental_01_" + Rand.Next(16, 19));
					}
				}
			}
		}

		this.addItemOnGround(this.getSq(int3 + 1, int4 + 1, zone.z), "Base.Shovel");
		ArrayList arrayList = this.addZombiesOnSquare(1, (String)null, (Integer)null, this.getRandomFreeSquare(this, zone));
		if (arrayList != null && !arrayList.isEmpty()) {
			IsoZombie zombie = (IsoZombie)arrayList.get(0);
			IsoDeadBody deadBody = createRandomDeadBody(this.getSq(int3, int4, zone.z), (IsoDirections)null, Rand.Next(7, 12), 0, (String)null);
			if (deadBody != null) {
				this.addBloodSplat(deadBody.getSquare(), 10);
				zombie.faceLocationF(deadBody.x, deadBody.y);
				zombie.setX(deadBody.x + 1.0F);
				zombie.setY(deadBody.y);
				zombie.setEatBodyTarget(deadBody, true);
			}
		}

		this.addItemOnGround(this.getRandomFreeSquare(this, zone), "Base.WhiskeyEmpty");
		this.addItemOnGround(this.getRandomFreeSquare(this, zone), "Base.WineEmpty");
	}
}
