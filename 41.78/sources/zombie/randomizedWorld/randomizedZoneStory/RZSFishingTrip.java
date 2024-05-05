package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMetaGrid;


public class RZSFishingTrip extends RandomizedZoneStoryBase {

	public RZSFishingTrip() {
		this.name = "Fishing Trip";
		this.chance = 10;
		this.minZoneHeight = 8;
		this.minZoneWidth = 8;
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Beach.toString());
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Lake.toString());
	}

	public static ArrayList getFishes() {
		ArrayList arrayList = new ArrayList();
		arrayList.add("Base.Catfish");
		arrayList.add("Base.Bass");
		arrayList.add("Base.Perch");
		arrayList.add("Base.Crappie");
		arrayList.add("Base.Panfish");
		arrayList.add("Base.Pike");
		arrayList.add("Base.Trout");
		arrayList.add("Base.BaitFish");
		return arrayList;
	}

	public static ArrayList getFishingTools() {
		ArrayList arrayList = new ArrayList();
		arrayList.add("Base.FishingTackle");
		arrayList.add("Base.FishingTackle");
		arrayList.add("Base.FishingTackle2");
		arrayList.add("Base.FishingTackle2");
		arrayList.add("Base.FishingLine");
		arrayList.add("Base.FishingLine");
		arrayList.add("Base.FishingNet");
		arrayList.add("Base.Worm");
		arrayList.add("Base.Worm");
		arrayList.add("Base.Worm");
		arrayList.add("Base.Worm");
		return arrayList;
	}

	public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
		ArrayList arrayList = getFishes();
		ArrayList arrayList2 = getFishingTools();
		this.cleanAreaForStory(this, zone);
		this.addVehicle(zone, this.getSq(zone.x, zone.y, zone.z), (IsoChunk)null, (String)null, "Base.PickUpTruck", (Integer)null, (IsoDirections)null, "Fisherman");
		int int1 = Rand.Next(1, 3);
		for (int int2 = 0; int2 < int1; ++int2) {
			this.addTileObject(this.getRandomFreeSquare(this, zone), "furniture_seating_outdoor_01_" + Rand.Next(16, 20));
		}

		InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
		int int3 = Rand.Next(4, 10);
		for (int int4 = 0; int4 < int3; ++int4) {
			inventoryContainer.getItemContainer().AddItem((String)arrayList.get(Rand.Next(arrayList.size())));
		}

		this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer);
		InventoryContainer inventoryContainer2 = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Toolbox");
		int3 = Rand.Next(3, 8);
		int int5;
		for (int5 = 0; int5 < int3; ++int5) {
			inventoryContainer2.getItemContainer().AddItem((String)arrayList2.get(Rand.Next(arrayList2.size())));
		}

		this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer2);
		int3 = Rand.Next(2, 5);
		for (int5 = 0; int5 < int3; ++int5) {
			this.addItemOnGround(this.getRandomFreeSquare(this, zone), "FishingRod");
		}

		this.addZombiesOnSquare(Rand.Next(2, 5), "Fisherman", 0, this.getRandomFreeSquare(this, zone));
	}
}
