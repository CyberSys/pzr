package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.BuildingDef;
import zombie.iso.SpawnPoints;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;


public class RandomizedDeadSurvivorBase extends RandomizedBuildingBase {

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return !SpawnPoints.instance.isSpawnBuilding(buildingDef);
	}
}
