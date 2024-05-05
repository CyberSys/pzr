package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;


public final class RDSGunmanInBathroom extends RandomizedDeadSurvivorBase {

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = super.getRoom(buildingDef, "bathroom");
		IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef, Rand.Next(5, 10));
		if (deadBody != null) {
			deadBody.setPrimaryHandItem(super.addRandomRangedWeapon(deadBody.getContainer(), true, false, false));
			int int1 = Rand.Next(1, 4);
			for (int int2 = 0; int2 < int1; ++int2) {
				deadBody.getContainer().AddItem((InventoryItem)super.addRandomRangedWeapon(deadBody.getContainer(), true, true, true));
			}
		}
	}

	public RDSGunmanInBathroom() {
		this.name = "Bathroom Gunman";
		this.setChance(5);
	}
}
