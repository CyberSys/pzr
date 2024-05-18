package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoDeadBody;


public class RDSPileOfBurntBodies extends RandomizedDeadSurvivorBase {

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		IsoGridSquare square = buildingDef.getFreeSquareInRoom();
		if (square != null) {
			IsoDeadBody deadBody = super.createRandomDeadBody(square.getX(), square.getY(), square.getZ());
			int int1 = Rand.Next(2, 5);
			for (int int2 = 0; int2 < int1; ++int2) {
				InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.WhiskeyEmpty");
				deadBody.getSquare().AddWorldInventoryItem(inventoryItem, Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F));
			}

			InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem("Base.WhiskeyEmpty");
			deadBody.getContainer().addItem(inventoryItem2);
		}
	}
}
