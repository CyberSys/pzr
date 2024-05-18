package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;


public class RDSBleach extends RandomizedDeadSurvivorBase {

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		for (int int1 = 0; int1 < buildingDef.rooms.size(); ++int1) {
			RoomDef roomDef = (RoomDef)buildingDef.rooms.get(int1);
			if ("kitchen".equals(roomDef.name)) {
				IsoDeadBody deadBody = super.createRandomDeadBody(roomDef);
				if (deadBody != null) {
					int int2 = Rand.Next(2, 5);
					for (int int3 = 0; int3 < int2; ++int3) {
						InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.BleachEmpty");
						deadBody.getSquare().AddWorldInventoryItem(inventoryItem, Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
					}

					InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem("Base.BleachEmpty");
					deadBody.getContainer().addItem(inventoryItem2);
					return;
				}
			}
		}
	}
}
