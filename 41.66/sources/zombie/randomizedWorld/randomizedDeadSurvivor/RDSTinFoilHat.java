package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;


public final class RDSTinFoilHat extends RandomizedDeadSurvivorBase {

	public RDSTinFoilHat() {
		this.name = "Tin foil hat family";
		this.setUnique(true);
		this.setChance(2);
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getLivingRoomOrKitchen(buildingDef);
		this.addZombies(buildingDef, Rand.Next(2, 5), "TinFoilHat", (Integer)null, roomDef);
	}
}
