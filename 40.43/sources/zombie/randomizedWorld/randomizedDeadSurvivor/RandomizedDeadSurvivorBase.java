package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;
import zombie.randomizedWorld.RandomizedBuildingBase;


public class RandomizedDeadSurvivorBase {

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
	}

	public IsoDeadBody createRandomDeadBody(RoomDef roomDef) {
		return RandomizedBuildingBase.createRandomDeadBody(roomDef);
	}

	public IsoDeadBody createRandomDeadBody(int int1, int int2, int int3) {
		return RandomizedBuildingBase.createRandomDeadBody(int1, int2, int3);
	}
}
