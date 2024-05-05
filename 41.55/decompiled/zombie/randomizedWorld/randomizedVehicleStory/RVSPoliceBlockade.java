package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;

public final class RVSPoliceBlockade extends RandomizedVehicleStoryBase {
   public RVSPoliceBlockade() {
      this.name = "Police Blockade";
      this.setChance(3);
      this.setMaximumDays(30);
   }

   public boolean isValid(IsoMetaGrid.Zone var1, IsoChunk var2, boolean var3) {
      boolean var4 = super.isValid(var1, var2, var3);
      if (!var4) {
         return false;
      } else {
         byte var5 = 10;
         if (this.horizontalZone) {
            return var2.wy * var5 <= var1.y && (var2.wy + 1) * var5 >= var1.y + var1.h;
         } else {
            return var2.wx * var5 <= var1.x && (var2.wx + 1) * var5 >= var1.x + var1.w;
         }
      }
   }

   public void randomizeVehicleStory(IsoMetaGrid.Zone var1, IsoChunk var2) {
      IsoDirections var3 = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
      if (!this.horizontalZone) {
         var3 = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
      }

      IsoDirections var4 = var3.RotLeft(4);
      IsoGridSquare var5 = IsoCell.getInstance().getGridSquare(this.minX, this.minY, var1.z);
      if (var5 != null) {
         byte var6 = 0;
         byte var7 = 0;
         if (!this.horizontalZone) {
            var6 = 5;
         } else {
            var7 = 5;
         }

         IsoGridSquare var8 = var5.getCell().getGridSquare(var5.x + var6, var5.y + var7, var5.z);
         if (var8 != null) {
            String var9 = "Base.CarLightsPolice";
            if (Rand.NextBool(3)) {
               var9 = "Base.PickUpVanLightsPolice";
            }

            BaseVehicle var10 = this.addVehicle(var1, var5, var2, (String)null, var9, var3);
            if (Rand.NextBool(3)) {
               var10.setHeadlightsOn(true);
               var10.setLightbarLightsMode(2);
            }

            BaseVehicle var11 = this.addVehicle(var1, var8, var2, (String)null, var9, var4);
            if (Rand.NextBool(3)) {
               var11.setHeadlightsOn(true);
               var11.setLightbarLightsMode(2);
            }

            this.addZombiesOnVehicle(Rand.Next(2, 4), "police", (Integer)null, var10);
            this.addZombiesOnVehicle(Rand.Next(2, 4), "police", (Integer)null, var11);
         }
      }
   }
}
