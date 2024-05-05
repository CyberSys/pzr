package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.VirtualZombieManager;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;


public final class RDSZombiesEating extends RandomizedDeadSurvivorBase {

	public RDSZombiesEating() {
		this.name = "Eating zombies";
		this.setChance(7);
		this.setMaximumDays(60);
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return IsoWorld.getZombiesEnabled() && super.isValid(buildingDef, boolean1);
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getLivingRoomOrKitchen(buildingDef);
		IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef, Rand.Next(5, 10));
		if (deadBody != null) {
			VirtualZombieManager.instance.createEatingZombies(deadBody, Rand.Next(1, 3));
			RoomDef roomDef2 = this.getRoom(buildingDef, "kitchen");
			RoomDef roomDef3 = this.getRoom(buildingDef, "livingroom");
			if ("kitchen".equals(roomDef.name) && roomDef3 != null && Rand.Next(3) == 0) {
				deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef3, Rand.Next(5, 10));
				if (deadBody == null) {
					return;
				}

				VirtualZombieManager.instance.createEatingZombies(deadBody, Rand.Next(1, 3));
			}

			if ("livingroom".equals(roomDef.name) && roomDef2 != null && Rand.Next(3) == 0) {
				deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef2, Rand.Next(5, 10));
				if (deadBody == null) {
					return;
				}

				VirtualZombieManager.instance.createEatingZombies(deadBody, Rand.Next(1, 3));
			}
		}
	}
}
