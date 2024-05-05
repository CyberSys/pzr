package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;


public class RZSBeachParty extends RandomizedZoneStoryBase {

	public RZSBeachParty() {
		this.name = "Beach Party";
		this.chance = 10;
		this.minZoneHeight = 13;
		this.minZoneWidth = 13;
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Beach.toString());
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Lake.toString());
	}

	public static ArrayList getBeachClutter() {
		ArrayList arrayList = new ArrayList();
		arrayList.add("Base.Crisps");
		arrayList.add("Base.Crisps3");
		arrayList.add("Base.Pop");
		arrayList.add("Base.WhiskeyFull");
		arrayList.add("Base.Cigarettes");
		arrayList.add("Base.BeerBottle");
		arrayList.add("Base.BeerBottle");
		arrayList.add("Base.BeerCan");
		arrayList.add("Base.BeerCan");
		arrayList.add("Base.BeerCan");
		arrayList.add("Base.BeerCan");
		arrayList.add("Base.BeerCan");
		arrayList.add("Base.BeerCan");
		return arrayList;
	}

	public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
		int int1 = zone.pickedXForZoneStory;
		int int2 = zone.pickedYForZoneStory;
		ArrayList arrayList = getBeachClutter();
		ArrayList arrayList2 = RZSForestCamp.getCoolerClutter();
		if (Rand.NextBool(2)) {
			this.addTileObject(int1, int2, zone.z, "camping_01_6");
		}

		int int3 = Rand.Next(1, 4);
		int int4;
		int int5;
		for (int4 = 0; int4 < int3; ++int4) {
			int5 = Rand.Next(4) + 1;
			switch (int5) {
			case 1: 
				int5 = 25;
				break;
			
			case 2: 
				int5 = 26;
				break;
			
			case 3: 
				int5 = 28;
				break;
			
			case 4: 
				int5 = 31;
			
			}

			IsoGridSquare square = this.getRandomFreeSquare(this, zone);
			this.addTileObject(square, "furniture_seating_outdoor_01_" + int5);
			if (int5 == 25) {
				square = this.getSq(square.x, square.y + 1, square.z);
				this.addTileObject(square, "furniture_seating_outdoor_01_24");
			} else if (int5 == 26) {
				square = this.getSq(square.x + 1, square.y, square.z);
				this.addTileObject(square, "furniture_seating_outdoor_01_27");
			} else if (int5 == 28) {
				square = this.getSq(square.x, square.y - 1, square.z);
				this.addTileObject(square, "furniture_seating_outdoor_01_29");
			} else {
				square = this.getSq(square.x - 1, square.y, square.z);
				this.addTileObject(square, "furniture_seating_outdoor_01_30");
			}
		}

		int3 = Rand.Next(1, 3);
		for (int4 = 0; int4 < int3; ++int4) {
			this.addTileObject(this.getRandomFreeSquare(this, zone), "furniture_seating_outdoor_01_" + Rand.Next(16, 20));
		}

		InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
		int5 = Rand.Next(4, 8);
		int int6;
		for (int6 = 0; int6 < int5; ++int6) {
			inventoryContainer.getItemContainer().AddItem((String)arrayList2.get(Rand.Next(arrayList2.size())));
		}

		this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer);
		int5 = Rand.Next(3, 7);
		for (int6 = 0; int6 < int5; ++int6) {
			this.addItemOnGround(this.getRandomFreeSquare(this, zone), (String)arrayList.get(Rand.Next(arrayList.size())));
		}

		int6 = Rand.Next(3, 8);
		int int7;
		for (int7 = 0; int7 < int6; ++int7) {
			this.addZombiesOnSquare(1, "Swimmer", (Integer)null, this.getRandomFreeSquare(this, zone));
		}

		int6 = Rand.Next(1, 3);
		for (int7 = 0; int7 < int6; ++int7) {
			this.addZombiesOnSquare(1, "Tourist", (Integer)null, this.getRandomFreeSquare(this, zone));
		}
	}
}
