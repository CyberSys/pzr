package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.iso.IsoMetaGrid;


public class RZSForestCamp extends RandomizedZoneStoryBase {

	public RZSForestCamp() {
		this.name = "Basic Forest Camp";
		this.chance = 10;
		this.minZoneHeight = 6;
		this.minZoneWidth = 6;
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Forest.toString());
	}

	public static ArrayList getForestClutter() {
		ArrayList arrayList = new ArrayList();
		arrayList.add("Base.Crisps");
		arrayList.add("Base.Crisps2");
		arrayList.add("Base.Crisps3");
		arrayList.add("Base.Crisps4");
		arrayList.add("Base.Pop");
		arrayList.add("Base.Pop2");
		arrayList.add("Base.WaterBottleFull");
		arrayList.add("Base.CannedSardines");
		arrayList.add("Base.CannedChili");
		arrayList.add("Base.CannedBolognese");
		arrayList.add("Base.CannedCornedBeef");
		arrayList.add("Base.TinnedSoup");
		arrayList.add("Base.TinnedBeans");
		arrayList.add("Base.TunaTin");
		arrayList.add("Base.WhiskeyFull");
		arrayList.add("Base.BeerBottle");
		arrayList.add("Base.BeerCan");
		arrayList.add("Base.BeerCan");
		return arrayList;
	}

	public static ArrayList getCoolerClutter() {
		ArrayList arrayList = new ArrayList();
		arrayList.add("Base.Pop");
		arrayList.add("Base.Pop2");
		arrayList.add("Base.BeefJerky");
		arrayList.add("Base.Ham");
		arrayList.add("Base.WaterBottleFull");
		arrayList.add("Base.BeerCan");
		arrayList.add("Base.BeerCan");
		arrayList.add("Base.BeerCan");
		arrayList.add("Base.BeerCan");
		return arrayList;
	}

	public static ArrayList getFireClutter() {
		ArrayList arrayList = new ArrayList();
		arrayList.add("Base.WaterPotRice");
		arrayList.add("Base.WaterPot");
		arrayList.add("Base.Pot");
		arrayList.add("Base.WaterSaucepanRice");
		arrayList.add("Base.WaterSaucepanPasta");
		arrayList.add("Base.PotOfStew");
		return arrayList;
	}

	public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
		int int1 = zone.pickedXForZoneStory;
		int int2 = zone.pickedYForZoneStory;
		ArrayList arrayList = getForestClutter();
		ArrayList arrayList2 = getCoolerClutter();
		ArrayList arrayList3 = getFireClutter();
		this.cleanAreaForStory(this, zone);
		this.addTileObject(int1, int2, zone.z, "camping_01_6");
		this.addItemOnGround(this.getSq(int1, int2, zone.z), (String)arrayList3.get(Rand.Next(arrayList3.size())));
		int int3 = Rand.Next(-1, 2);
		int int4 = Rand.Next(-1, 2);
		this.addTentWestEast(int1 + int3 - 2, int2 + int4, zone.z);
		if (Rand.Next(100) < 70) {
			this.addTentNorthSouth(int1 + int3, int2 + int4 - 2, zone.z);
		}

		if (Rand.Next(100) < 30) {
			this.addTentNorthSouth(int1 + int3 + 1, int2 + int4 - 2, zone.z);
		}

		this.addTileObject(int1 + 2, int2, zone.z, "furniture_seating_outdoor_01_19");
		InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
		int int5 = Rand.Next(2, 5);
		int int6;
		for (int6 = 0; int6 < int5; ++int6) {
			inventoryContainer.getItemContainer().AddItem((String)arrayList2.get(Rand.Next(arrayList2.size())));
		}

		this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer);
		int5 = Rand.Next(3, 7);
		for (int6 = 0; int6 < int5; ++int6) {
			this.addItemOnGround(this.getRandomFreeSquare(this, zone), (String)arrayList.get(Rand.Next(arrayList.size())));
		}

		this.addZombiesOnSquare(Rand.Next(1, 3), "Camper", (Integer)null, this.getRandomFreeSquare(this, zone));
	}
}
