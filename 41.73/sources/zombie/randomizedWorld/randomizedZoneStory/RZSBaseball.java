package zombie.randomizedWorld.randomizedZoneStory;

import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;


public class RZSBaseball extends RandomizedZoneStoryBase {
	public RZSBaseball() {
		this.name = "Baseball";
		this.chance = 100;
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Baseball.toString());
		this.minZoneWidth = 20;
		this.minZoneHeight = 20;
		this.alwaysDo = true;
	}

	public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
		int int1 = Rand.Next(0, 3);
		int int2;
		for (int2 = Rand.Next(0, 3); int1 == int2; int2 = Rand.Next(0, 3)) {
		}

		String string = "BaseballPlayer_KY";
		if (int1 == 1) {
			string = "BaseballPlayer_Rangers";
		}

		if (int1 == 2) {
			string = "BaseballPlayer_Z";
		}

		String string2 = "BaseballPlayer_KY";
		if (int2 == 1) {
			string2 = "BaseballPlayer_Rangers";
		}

		if (int2 == 2) {
			string2 = "BaseballPlayer_Z";
		}

		int int3;
		for (int3 = 0; int3 < 20; ++int3) {
			if (Rand.NextBool(4)) {
				this.addItemOnGround(this.getRandomFreeSquare(this, zone), "Base.BaseballBat");
			}

			if (Rand.NextBool(6)) {
				this.addItemOnGround(this.getRandomFreeSquare(this, zone), "Base.Baseball");
			}
		}

		for (int3 = 0; int3 <= 9; ++int3) {
			this.addZombiesOnSquare(1, string, 0, this.getRandomFreeSquare(this, zone));
			this.addZombiesOnSquare(1, string2, 0, this.getRandomFreeSquare(this, zone));
		}
	}
}
