package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.RoomDef;
import zombie.vehicles.BaseVehicle;


public final class RDSPrisonEscapeWithPolice extends RandomizedDeadSurvivorBase {

	public RDSPrisonEscapeWithPolice() {
		this.name = "Prison Escape with Police";
		this.setChance(2);
		this.setMaximumDays(90);
		this.setUnique(true);
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getLivingRoomOrKitchen(buildingDef);
		this.addZombies(buildingDef, Rand.Next(2, 4), "InmateEscaped", 0, roomDef);
		ArrayList arrayList = this.addZombies(buildingDef, Rand.Next(2, 4), "Police", (Integer)null, roomDef);
		BaseVehicle baseVehicle = this.spawnCarOnNearestNav("Base.CarLightsPolice", buildingDef);
		if (baseVehicle != null) {
			baseVehicle.setAlarmed(false);
		}

		if (baseVehicle != null) {
			IsoGridSquare square = baseVehicle.getSquare().getCell().getGridSquare(baseVehicle.getSquare().x - 2, baseVehicle.getSquare().y - 2, 0);
			ArrayList arrayList2 = this.addZombiesOnSquare(3, "Police", (Integer)null, square);
			if (!arrayList.isEmpty()) {
				arrayList.addAll(arrayList2);
				((IsoZombie)arrayList.get(Rand.Next(arrayList.size()))).addItemToSpawnAtDeath(baseVehicle.createVehicleKey());
				buildingDef.bAlarmed = false;
			}
		}
	}
}
