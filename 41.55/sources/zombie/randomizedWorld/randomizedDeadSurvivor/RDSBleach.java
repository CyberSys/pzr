package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;


public final class RDSBleach extends RandomizedDeadSurvivorBase {

	public RDSBleach() {
		this.name = "Suicide by Bleach";
		this.setChance(10);
		this.setMinimumDays(60);
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getLivingRoomOrKitchen(buildingDef);
		IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef, 0);
		if (deadBody != null) {
			int int1 = Rand.Next(1, 3);
			for (int int2 = 0; int2 < int1; ++int2) {
				InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.BleachEmpty");
				deadBody.getSquare().AddWorldInventoryItem(inventoryItem, Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), 0.0F);
			}

			deadBody.setPrimaryHandItem(InventoryItemFactory.CreateItem("Base.BleachEmpty"));
		}
	}
}
