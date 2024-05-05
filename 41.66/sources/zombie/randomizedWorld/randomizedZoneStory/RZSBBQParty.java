package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;


public class RZSBBQParty extends RandomizedZoneStoryBase {

	public RZSBBQParty() {
		this.name = "BBQ Party";
		this.chance = 10;
		this.minZoneHeight = 12;
		this.minZoneWidth = 12;
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Beach.toString());
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Lake.toString());
	}

	public static ArrayList getBeachClutter() {
		ArrayList arrayList = new ArrayList();
		arrayList.add("Base.Crisps");
		arrayList.add("Base.Crisps3");
		arrayList.add("Base.MuttonChop");
		arrayList.add("Base.PorkChop");
		arrayList.add("Base.Steak");
		arrayList.add("Base.Pop");
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
		IsoGridSquare square = this.getSq(int1, int2, zone.z);
		IsoBarbecue barbecue = new IsoBarbecue(IsoWorld.instance.getCell(), square, (IsoSprite)IsoSpriteManager.instance.NamedMap.get("appliances_cooking_01_35"));
		square.getObjects().add(barbecue);
		int int3 = Rand.Next(1, 4);
		for (int int4 = 0; int4 < int3; ++int4) {
			this.addTileObject(this.getRandomFreeSquare(this, zone), "furniture_seating_outdoor_01_" + Rand.Next(16, 20));
		}

		InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
		int int5 = Rand.Next(4, 8);
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
		for (int int7 = 0; int7 < int6; ++int7) {
			this.addZombiesOnSquare(1, "Tourist", (Integer)null, this.getRandomFreeSquare(this, zone));
		}
	}
}
