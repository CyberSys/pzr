package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMetaGrid;
import zombie.iso.objects.IsoDeadBody;


public class RZSForestCampEaten extends RandomizedZoneStoryBase {

	public RZSForestCampEaten() {
		this.name = "Forest Camp Eaten";
		this.chance = 10;
		this.minZoneHeight = 6;
		this.minZoneWidth = 10;
		this.minimumDays = 30;
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Forest.toString());
	}

	public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
		int int1 = zone.pickedXForZoneStory;
		int int2 = zone.pickedYForZoneStory;
		ArrayList arrayList = RZSForestCamp.getForestClutter();
		ArrayList arrayList2 = RZSForestCamp.getCoolerClutter();
		ArrayList arrayList3 = RZSForestCamp.getFireClutter();
		this.cleanAreaForStory(this, zone);
		this.addTileObject(int1, int2, zone.z, "camping_01_6");
		this.addItemOnGround(this.getSq(int1, int2, zone.z), (String)arrayList3.get(Rand.Next(arrayList3.size())));
		byte byte1 = 0;
		byte byte2 = 0;
		this.addTentNorthSouth(int1 - 4, int2 + byte2 - 2, zone.z);
		int int3 = byte1 + Rand.Next(1, 3);
		this.addTentNorthSouth(int1 - 3 + int3, int2 + byte2 - 2, zone.z);
		int3 += Rand.Next(1, 3);
		this.addTentNorthSouth(int1 - 2 + int3, int2 + byte2 - 2, zone.z);
		if (Rand.NextBool(1)) {
			int3 += Rand.Next(1, 3);
			this.addTentNorthSouth(int1 - 1 + int3, int2 + byte2 - 2, zone.z);
		}

		if (Rand.NextBool(2)) {
			int3 += Rand.Next(1, 3);
			this.addTentNorthSouth(int1 + int3, int2 + byte2 - 2, zone.z);
		}

		InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
		int int4 = Rand.Next(2, 5);
		int int5;
		for (int5 = 0; int5 < int4; ++int5) {
			inventoryContainer.getItemContainer().AddItem((String)arrayList2.get(Rand.Next(arrayList2.size())));
		}

		this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer);
		int4 = Rand.Next(3, 7);
		for (int5 = 0; int5 < int4; ++int5) {
			this.addItemOnGround(this.getRandomFreeSquare(this, zone), (String)arrayList.get(Rand.Next(arrayList.size())));
		}

		ArrayList arrayList4 = this.addZombiesOnSquare(1, "Camper", (Integer)null, this.getRandomFreeSquare(this, zone));
		IsoZombie zombie = arrayList4.isEmpty() ? null : (IsoZombie)arrayList4.get(0);
		int int6 = Rand.Next(3, 7);
		IsoDeadBody deadBody = null;
		for (int int7 = 0; int7 < int6; ++int7) {
			deadBody = createRandomDeadBody(this.getRandomFreeSquare(this, zone), (IsoDirections)null, Rand.Next(5, 10), 0, "Camper");
			if (deadBody != null) {
				this.addBloodSplat(deadBody.getSquare(), 10);
			}
		}

		deadBody = createRandomDeadBody(this.getSq(int1, int2 + 3, zone.z), (IsoDirections)null, Rand.Next(5, 10), 0, "Camper");
		if (deadBody != null) {
			this.addBloodSplat(deadBody.getSquare(), 10);
			if (zombie != null) {
				zombie.faceLocationF(deadBody.x, deadBody.y);
				zombie.setX(deadBody.x + 1.0F);
				zombie.setY(deadBody.y);
				zombie.setEatBodyTarget(deadBody, true);
			}
		}
	}
}
