package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;
import zombie.randomizedWorld.RandomizedBuildingBase;

public class RandomizedDeadSurvivorBase {
   public void randomizeDeadSurvivor(BuildingDef var1) {
   }

   public IsoDeadBody createRandomDeadBody(RoomDef var1) {
      return RandomizedBuildingBase.createRandomDeadBody(var1);
   }

   public IsoDeadBody createRandomDeadBody(int var1, int var2, int var3) {
      return RandomizedBuildingBase.createRandomDeadBody(var1, var2, var3);
   }
}
