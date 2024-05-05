package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;

public final class RVSTrailerCrash extends RandomizedVehicleStoryBase {
   public RVSTrailerCrash() {
      this.name = "Trailer Crash";
      this.minZoneWidth = 5;
      this.minZoneHeight = 3;
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
            BaseVehicle var10 = this.addVehicle(var1, var6, var2, (String)null, "Base.PickUpVan", var3);
            BaseVehicle var11 = this.addVehicle(var1, var9, var2, "bad", (String)null, var5);
            if (var10 != null) {
               if (Rand.Next(10) < 4) {
                  this.addZombiesOnVehicle(Rand.Next(2, 5), (String)null, (Integer)null, var10);
               }

               String var12 = Rand.NextBool(2) ? "Base.Trailer" : "Base.TrailerCover";
               if (Rand.NextBool(6)) {
                  var12 = "Base.TrailerAdvert";
               }

               BaseVehicle[] var13 = this.addSmashedOverlay(var10, var11, var7, var8, this.horizontalZone, true);
               var10 = var13[0];
               BaseVehicle var14 = this.addTrailer(var10, var1, var2, (String)null, (String)null, var12);
               if (var14 != null && Rand.NextBool(3)) {
                  var14.setAngles(var14.getAngleX(), Rand.Next(90.0F, 110.0F), var14.getAngleZ());
               }

            }
         }
      }
   }
}
