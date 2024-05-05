package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;

public final class RVSCarCrashCorpse extends RandomizedVehicleStoryBase {
   public RVSCarCrashCorpse() {
      this.name = "Basic Car Crash Corpse";
      this.minZoneWidth = 5;
      this.minZoneHeight = 3;
      this.setChance(10);
   }

   public void randomizeVehicleStory(IsoMetaGrid.Zone var1, IsoChunk var2) {
      IsoDirections var3 = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
      if (this.horizontalZone) {
         var3 = Rand.NextBool(2) ? IsoDirections.W : IsoDirections.E;
      }

      IsoGridSquare var4 = this.getCenterOfChunk(var1, var2);
      if (var4 != null) {
         int var5 = 0;
         int var6 = 0;
         if (var3 == IsoDirections.S) {
            var6 = Rand.Next(4, 7);
         }

         if (var3 == IsoDirections.N) {
            var6 = Rand.Next(-7, -4);
         }

         if (var3 == IsoDirections.W) {
            var5 = Rand.Next(-7, -4);
         }

         if (var3 == IsoDirections.E) {
            var5 = Rand.Next(4, 7);
         }

         BaseVehicle var7 = this.addVehicle(var1, var4, var2, "bad", (String)null, var3);
         var7.setSmashed("Front");
         var7.setBloodIntensity("Front", 1.0F);
         createRandomDeadBody(var4.x + var5, var4.y + var6, var4.z, var3.RotLeft(4), 35, 30);
         this.addTraitOfBlood(var3, 15, var4.x + var5, var4.y + var6, var4.z);
      }
   }
}
