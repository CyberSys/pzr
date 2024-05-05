package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.vehicles.BaseVehicle;

public final class RVSPoliceBlockadeShooting extends RandomizedVehicleStoryBase {
   public RVSPoliceBlockadeShooting() {
      this.name = "Police Blockade Shooting";
      this.setChance(1);
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
      boolean var3 = Rand.NextBool(2);
      IsoDirections var4 = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
      if (!this.horizontalZone) {
         var4 = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
      }

      IsoDirections var5 = var4.RotLeft(4);
      int var6 = this.minX;
      int var7 = this.minY;
      if (var3) {
         var6 = this.minX;
         var7 = this.maxY;
         if (this.horizontalZone) {
            var6 = this.maxX;
            var7 = this.minY;
         }
      }

      IsoGridSquare var8 = IsoCell.getInstance().getGridSquare(var6, var7, var1.z);
      if (var8 != null) {
         byte var9 = 0;
         byte var10 = 0;
         if (!this.horizontalZone) {
            var9 = 5;
         } else {
            var10 = 5;
         }

         IsoGridSquare var11 = var8.getCell().getGridSquare(var8.x + var9, var8.y + var10, var8.z);
         if (var11 != null) {
            String var12 = "Base.CarLightsPolice";
            if (Rand.NextBool(3)) {
               var12 = "Base.PickUpVanLightsPolice";
            }

            BaseVehicle var13 = this.addVehicle(var1, var8, var2, (String)null, var12, var4);
            if (Rand.NextBool(3)) {
               var13.setHeadlightsOn(true);
               var13.setLightbarLightsMode(2);
            }

            BaseVehicle var14 = this.addVehicle(var1, var11, var2, (String)null, var12, var5);
            if (Rand.NextBool(3)) {
               var14.setHeadlightsOn(true);
               var14.setLightbarLightsMode(2);
            }

            String var15 = "Police";
            Integer var16 = null;
            if (Rand.NextBool(6)) {
               var15 = "PoliceRiot";
               var16 = 0;
            }

            var15 = "PoliceRiot";
            var16 = 0;
            this.addZombiesOnVehicle(Rand.Next(2, 4), var15, var16, var13);
            this.addZombiesOnVehicle(Rand.Next(2, 4), var15, var16, var14);
            int var17 = Rand.Next(7, 15);
            IsoDirections var18 = null;
            if (!this.horizontalZone) {
               if (var3) {
                  var18 = IsoDirections.S;
               } else {
                  var18 = IsoDirections.N;
               }
            } else if (var3) {
               var18 = IsoDirections.W;
            } else {
               var18 = IsoDirections.E;
            }

            int var23;
            for(int var19 = 0; var19 < var17; ++var19) {
               boolean var20 = false;
               boolean var21 = false;
               int var24;
               if (!this.horizontalZone) {
                  if (var3) {
                     var24 = var8.y + Rand.Next(3, 6);
                  } else {
                     var24 = var8.y + Rand.Next(-6, -3);
                  }

                  var23 = Rand.Next(var1.x, var1.x + var1.w);
               } else {
                  if (var3) {
                     var23 = var8.x + Rand.Next(3, 6);
                  } else {
                     var23 = var8.x + Rand.Next(-6, -3);
                  }

                  var24 = Rand.Next(var1.y, var1.y + var1.h);
               }

               createRandomDeadBody(var23, var24, var1.z, (IsoDirections)null, 10, 10);
               this.addTraitOfBlood(var18, 5, var23, var24, var1.z);
            }

            byte var22;
            IsoGridSquare var25;
            if (!this.horizontalZone) {
               var22 = -2;
               if (var3) {
                  var22 = 2;
               }

               for(var23 = var1.x - 1; var23 < var1.x + var1.w + 1; ++var23) {
                  var25 = IsoCell.getInstance().getGridSquare(var23, var8.y + var22, var1.z);
                  if (var25 != null) {
                     if (var23 != var1.x - 1 && var23 != var1.x + var1.w) {
                        var25.AddTileObject(IsoObject.getNew(var25, "construction_01_8", (String)null, false));
                     } else {
                        var25.AddTileObject(IsoObject.getNew(var25, "street_decoration_01_26", (String)null, false));
                     }
                  }
               }
            } else {
               var22 = -2;
               if (var3) {
                  var22 = 2;
               }

               for(var23 = var1.y - 1; var23 < var1.y + var1.h + 1; ++var23) {
                  var25 = IsoCell.getInstance().getGridSquare(var8.x + var22, var23, var1.z);
                  if (var25 != null) {
                     if (var23 != var1.y - 1 && var23 != var1.y + var1.h) {
                        var25.AddTileObject(IsoObject.getNew(var25, "construction_01_9", (String)null, false));
                     } else {
                        var25.AddTileObject(IsoObject.getNew(var25, "street_decoration_01_26", (String)null, false));
                     }
                  }
               }
            }

         }
      }
   }
}
