package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.vehicles.BaseVehicle;


public final class RBBurntFireman extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
		int int1 = Rand.Next(1, 4);
		buildingDef.setHasBeenVisited(true);
		IsoCell cell = IsoWorld.instance.CurrentCell;
		int int2;
		for (int int3 = buildingDef.x - 1; int3 < buildingDef.x2 + 1; ++int3) {
			for (int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int4 = 0; int4 < 8; ++int4) {
					IsoGridSquare square = cell.getGridSquare(int3, int2, int4);
					if (square != null && Rand.Next(100) < 70) {
						square.Burn(false);
					}
				}
			}
		}

		buildingDef.setAllExplored(true);
		ArrayList arrayList = this.addZombies(buildingDef, int1, "FiremanFullSuit", 35, this.getLivingRoomOrKitchen(buildingDef));
		for (int2 = 0; int2 < arrayList.size(); ++int2) {
			((IsoZombie)arrayList.get(int2)).getInventory().setExplored(true);
		}

		BaseVehicle baseVehicle;
		if (Rand.NextBool(2)) {
			baseVehicle = this.spawnCarOnNearestNav("Base.PickUpVanLightsFire", buildingDef);
		} else {
			baseVehicle = this.spawnCarOnNearestNav("Base.PickUpTruckLightsFire", buildingDef);
		}

		if (baseVehicle != null && !arrayList.isEmpty()) {
			((IsoZombie)arrayList.get(Rand.Next(arrayList.size()))).addItemToSpawnAtDeath(baseVehicle.createVehicleKey());
		}
	}

	public RBBurntFireman() {
		this.name = "Burnt Fireman";
		this.setChance(2);
	}
}
