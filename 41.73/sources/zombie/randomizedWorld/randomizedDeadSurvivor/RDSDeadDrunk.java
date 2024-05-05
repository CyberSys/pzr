package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;


public final class RDSDeadDrunk extends RandomizedDeadSurvivorBase {
	final ArrayList alcoholList = new ArrayList();

	public RDSDeadDrunk() {
		this.name = "Dead Drunk";
		this.setChance(10);
		this.alcoholList.add("Base.WhiskeyFull");
		this.alcoholList.add("Base.WhiskeyEmpty");
		this.alcoholList.add("Base.Wine");
		this.alcoholList.add("Base.WineEmpty");
		this.alcoholList.add("Base.Wine2");
		this.alcoholList.add("Base.WineEmpty2");
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getLivingRoomOrKitchen(buildingDef);
		IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef, 0);
		if (deadBody != null) {
			int int1 = Rand.Next(2, 4);
			for (int int2 = 0; int2 < int1; ++int2) {
				InventoryItem inventoryItem = InventoryItemFactory.CreateItem((String)this.alcoholList.get(Rand.Next(0, this.alcoholList.size())));
				deadBody.getSquare().AddWorldInventoryItem(inventoryItem, Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), 0.0F);
			}

			deadBody.setPrimaryHandItem(InventoryItemFactory.CreateItem("Base.WhiskeyEmpty"));
		}
	}
}
