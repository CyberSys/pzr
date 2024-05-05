package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.RoomDef;
import zombie.vehicles.BaseVehicle;


public final class RDSPoliceAtHouse extends RandomizedDeadSurvivorBase {

	public RDSPoliceAtHouse() {
		this.name = "Police at House";
		this.setChance(4);
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getLivingRoomOrKitchen(buildingDef);
		this.addZombies(buildingDef, Rand.Next(2, 4), (String)null, 0, roomDef);
		ArrayList arrayList = this.addZombies(buildingDef, Rand.Next(1, 3), "Police", (Integer)null, roomDef);
		BaseVehicle baseVehicle = this.spawnCarOnNearestNav("Base.CarLightsPolice", buildingDef);
		if (baseVehicle != null) {
			IsoGridSquare square = baseVehicle.getSquare().getCell().getGridSquare(baseVehicle.getSquare().x - 2, baseVehicle.getSquare().y - 2, 0);
			ArrayList arrayList2 = this.addZombiesOnSquare(2, "Police", (Integer)null, square);
			createRandomDeadBody(roomDef, Rand.Next(7, 10));
			createRandomDeadBody(roomDef, Rand.Next(7, 10));
			if (!arrayList.isEmpty()) {
				arrayList.addAll(arrayList2);
				((IsoZombie)arrayList.get(Rand.Next(arrayList.size()))).addItemToSpawnAtDeath(baseVehicle.createVehicleKey());
			}
		}
	}
}
