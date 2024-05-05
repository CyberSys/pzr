package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;


public final class RDSPrisonEscape extends RandomizedDeadSurvivorBase {

	public RDSPrisonEscape() {
		this.name = "Prison Escape";
		this.setChance(3);
		this.setMaximumDays(90);
		this.setUnique(true);
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getLivingRoomOrKitchen(buildingDef);
		this.addZombies(buildingDef, Rand.Next(2, 4), "InmateEscaped", 0, roomDef);
	}
}
