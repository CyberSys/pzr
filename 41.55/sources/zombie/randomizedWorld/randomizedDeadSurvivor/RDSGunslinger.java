package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.iso.BuildingDef;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoDeadBody;


public final class RDSGunslinger extends RandomizedDeadSurvivorBase {

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		IsoGridSquare square = buildingDef.getFreeSquareInRoom();
		if (square != null) {
			IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(square.getX(), square.getY(), square.getZ(), (IsoDirections)null, 0);
			if (deadBody != null) {
				deadBody.setPrimaryHandItem(super.addRandomRangedWeapon(deadBody.getContainer(), true, false, false));
				int int1 = Rand.Next(1, 4);
				for (int int2 = 0; int2 < int1; ++int2) {
					deadBody.getContainer().AddItem((InventoryItem)super.addRandomRangedWeapon(deadBody.getContainer(), true, true, true));
				}
			}
		}
	}

	public RDSGunslinger() {
		this.name = "Gunslinger";
		this.setChance(5);
	}
}
