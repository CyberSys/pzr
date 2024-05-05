package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.HashMap;
import java.util.Iterator;
import zombie.SandboxOptions;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.vehicles.BaseVehicle;

public class RandomizedVehicleStoryBase extends RandomizedWorldBase {
   private int chance = 0;
   private static int totalChance = 0;
   private static HashMap rvsMap = new HashMap();
   protected boolean horizontalZone = false;
   public static final float baseChance = 12.5F;
   protected int minX = 0;
   protected int minY = 0;
   protected int maxX = 0;
   protected int maxY = 0;
   protected int minZoneWidth = 0;
   protected int minZoneHeight = 0;

   public static void initAllRVSMapChance(IsoMetaGrid.Zone var0, IsoChunk var1) {
      totalChance = 0;
      rvsMap.clear();

      for(int var2 = 0; var2 < IsoWorld.instance.getRandomizedVehicleStoryList().size(); ++var2) {
         RandomizedVehicleStoryBase var3 = (RandomizedVehicleStoryBase)IsoWorld.instance.getRandomizedVehicleStoryList().get(var2);
         if (var3.isValid(var0, var1, false) && var3.isTimeValid(false)) {
            totalChance += ((RandomizedVehicleStoryBase)IsoWorld.instance.getRandomizedVehicleStoryList().get(var2)).getChance();
            rvsMap.put((RandomizedVehicleStoryBase)IsoWorld.instance.getRandomizedVehicleStoryList().get(var2), ((RandomizedVehicleStoryBase)IsoWorld.instance.getRandomizedVehicleStoryList().get(var2)).getChance());
         }
      }

   }

   public static boolean doRandomStory(IsoMetaGrid.Zone var0, IsoChunk var1, boolean var2) {
      float var3 = Rand.Next(0.0F, 500.0F);
      switch(SandboxOptions.instance.VehicleStoryChance.getValue()) {
      case 1:
         return false;
      case 2:
         var3 = Rand.Next(0.0F, 1000.0F);
      case 3:
      default:
         break;
      case 4:
         var3 = Rand.Next(0.0F, 300.0F);
         break;
      case 5:
         var3 = Rand.Next(0.0F, 175.0F);
         break;
      case 6:
         var3 = Rand.Next(0.0F, 50.0F);
      }

      if (var3 < 12.5F) {
         if (!var1.vehicles.isEmpty()) {
            return false;
         } else {
            RandomizedVehicleStoryBase var4 = null;
            initAllRVSMapChance(var0, var1);
            var4 = getRandomStory();
            if (var4 == null) {
               return false;
            } else {
               var4.randomizeVehicleStory(var0, var1);
               ++var0.hourLastSeen;
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private static RandomizedVehicleStoryBase getRandomStory() {
      int var0 = Rand.Next(totalChance);
      Iterator var1 = rvsMap.keySet().iterator();
      int var2 = 0;

      RandomizedVehicleStoryBase var3;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         var3 = (RandomizedVehicleStoryBase)var1.next();
         var2 += (Integer)rvsMap.get(var3);
      } while(var0 >= var2);

      return var3;
   }

   public void randomizeVehicleStory(IsoMetaGrid.Zone var1, IsoChunk var2) {
   }

   public IsoGridSquare getCenterOfChunk(IsoMetaGrid.Zone var1, IsoChunk var2) {
      int var3 = Math.max(var1.x, var2.wx * 10);
      int var4 = Math.max(var1.y, var2.wy * 10);
      int var5 = Math.min(var1.x + var1.w, (var2.wx + 1) * 10);
      int var6 = Math.min(var1.y + var1.h, (var2.wy + 1) * 10);
      boolean var7 = false;
      boolean var8 = false;
      int var9;
      int var10;
      if (this.horizontalZone) {
         var10 = (var1.y + var1.y + var1.h) / 2;
         var9 = (var3 + var5) / 2;
      } else {
         var10 = (var4 + var6) / 2;
         var9 = (var1.x + var1.x + var1.w) / 2;
      }

      return IsoCell.getInstance().getGridSquare(var9, var10, var1.z);
   }

   public boolean isValid(IsoMetaGrid.Zone var1, IsoChunk var2, boolean var3) {
      this.horizontalZone = false;
      this.debugLine = "";
      if (!var3 && var1.hourLastSeen != 0) {
         return false;
      } else if (!var3 && var1.haveConstruction) {
         return false;
      } else if (!"Nav".equals(var1.getType())) {
         this.debugLine = this.debugLine + "Not a 'Nav' zone.";
         return false;
      } else {
         this.minX = Math.max(var1.x, var2.wx * 10);
         this.minY = Math.max(var1.y, var2.wy * 10);
         this.maxX = Math.min(var1.x + var1.w, (var2.wx + 1) * 10);
         this.maxY = Math.min(var1.y + var1.h, (var2.wy + 1) * 10);
         int var4 = 10;
         int var5 = 5;
         if (this.minZoneWidth > 0) {
            var4 = this.minZoneWidth;
         }

         if (this.minZoneHeight > 0) {
            var5 = this.minZoneHeight;
         }

         if (var1.w > 30 && var1.h < 15) {
            this.horizontalZone = true;
            if (this.maxX - this.minX < var4 || this.maxY - this.minY < var5) {
               this.debugLine = this.debugLine + "Horizontal street is too small, x:" + (this.maxX - this.minX) + " y:" + (this.maxY - this.minY);
            }

            return this.maxX - this.minX >= var4 && this.maxY - this.minY >= var5;
         } else if (var1.h > 30 && var1.w < 15) {
            if (this.maxX - this.minX < var5 || this.maxY - this.minY < var4) {
               this.debugLine = this.debugLine + "Vertical street is too small, x:" + (this.maxX - this.minX) + " y:" + (this.maxY - this.minY);
            }

            return this.maxX - this.minX >= var5 && this.maxY - this.minY >= var4;
         } else {
            this.debugLine = this.debugLine + "Zone too small";
            return false;
         }
      }
   }

   public BaseVehicle[] addSmashedOverlay(BaseVehicle var1, BaseVehicle var2, int var3, int var4, boolean var5, boolean var6) {
      IsoDirections var7 = var1.getDir();
      IsoDirections var8 = var2.getDir();
      String var9 = null;
      String var10 = null;
      if (!var5) {
         var9 = "Front";
         if (var8 == IsoDirections.W) {
            if (var7 == IsoDirections.S) {
               var10 = "Right";
            } else {
               var10 = "Left";
            }
         } else if (var7 == IsoDirections.S) {
            var10 = "Left";
         } else {
            var10 = "Right";
         }
      } else {
         if (var7 == IsoDirections.S) {
            if (var3 > 0) {
               var9 = "Left";
            } else {
               var9 = "Right";
            }
         } else if (var3 < 0) {
            var9 = "Left";
         } else {
            var9 = "Right";
         }

         var10 = "Front";
      }

      var1 = var1.setSmashed(var9);
      var2 = var2.setSmashed(var10);
      if (var6) {
         var1.setBloodIntensity(var9, 1.0F);
         var2.setBloodIntensity(var10, 1.0F);
      }

      return new BaseVehicle[]{var1, var2};
   }

   public int getChance() {
      return this.chance;
   }

   public void setChance(int var1) {
      this.chance = var1;
   }

   public int getMinimumDays() {
      return this.minimumDays;
   }

   public void setMinimumDays(int var1) {
      this.minimumDays = var1;
   }

   public int getMaximumDays() {
      return this.maximumDays;
   }

   public void setMaximumDays(int var1) {
      this.maximumDays = var1;
   }

   public String getName() {
      return this.name;
   }

   public String getDebugLine() {
      return this.debugLine;
   }

   public void registerCustomOutfits() {
   }
}
