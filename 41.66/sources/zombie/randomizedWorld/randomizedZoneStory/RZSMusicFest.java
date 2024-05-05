package zombie.randomizedWorld.randomizedZoneStory;

import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;


public class RZSMusicFest extends RandomizedZoneStoryBase {

	public RZSMusicFest() {
		this.name = "Music Festival";
		this.chance = 100;
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.MusicFest.toString());
		this.alwaysDo = true;
	}

	public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
		int int1 = Rand.Next(20, 50);
		for (int int2 = 0; int2 < int1; ++int2) {
			int int3 = Rand.Next(0, 4);
			switch (int3) {
			case 0: 
				this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.BeerCan");
				break;
			
			case 1: 
				this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.BeerBottle");
				break;
			
			case 2: 
				this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.BeerCanEmpty");
				break;
			
			case 3: 
				this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.BeerEmpty");
			
			}
		}
	}
}
