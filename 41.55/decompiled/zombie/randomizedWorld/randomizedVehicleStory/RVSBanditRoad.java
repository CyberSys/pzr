package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;

public final class RVSBanditRoad extends RandomizedVehicleStoryBase {
   public RVSBanditRoad() {
      this.name = "Bandits on Road";
      this.minZoneWidth = 5;
      this.minZoneHeight = 3;
      this.setMinimumDays(30);
      this.setChance(3);
   }

   public void randomizeVehicleStory(IsoMetaGrid.Zone var1, IsoChunk var2) {
      IsoDirections var3 = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
      boolean var4 = Rand.NextBool(2);
      IsoDirections var5 = var4 ? var3.RotLeft(2) : var3.RotRight(2);
      if (var3 == IsoDirections.S) {
         var5 = var4 ? var3.RotRight(2) : var3.RotLeft(2);
      }

      IsoGridSquare var6 = this.getCenterOfChunk(var1, var2);
      if (var6 != null) {
         byte var7 = 0;
         byte var8 = 0;
         if (this.horizontalZone) {
            var7 = 3;
            if (!var4) {
               var7 = -3;
            }
         } else {
            var8 = -3;
            if (var3 == IsoDirections.S) {
               var8 = 3;
            }
         }

         IsoGridSquare var9 = var6.getCell().getGridSquare(var6.x + var7, var6.y + var8, var6.z);
         if (var9 != null) {
            BaseVehicle var10 = this.addVehicle(var1, var6, var2, "bad", (String)null, var3);
            BaseVehicle var11 = this.addVehicle(var1, var9, var2, "bad", (String)null, var5);
            var10.setSmashed("Front");
            this.addZombiesOnVehicle(Rand.Next(3, 6), "Bandit", (Integer)null, var10);
            this.addZombiesOnVehicle(Rand.Next(3, 5), (String)null, (Integer)null, var11);
            int var12 = Rand.Next(3, 6);

            for(int var13 = 0; var13 < var12; ++var13) {
               int var14 = Rand.Next((int)var11.x - 3, (int)var11.x + 3);
               int var15 = Rand.Next((int)var11.y - 3, (int)var11.y + 3);
               this.addTraitOfBlood(var3, 15, var14, var15, 0);
               createRandomDeadBody(var14, var15, 0, (IsoDirections)null, 6);
            }

         }
      }
   }
}
