package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.objects.IsoDeadBody;
import zombie.vehicles.BaseVehicle;

public final class RVSCrashHorde extends RandomizedVehicleStoryBase {
   public RVSCrashHorde() {
      this.name = "Crash Horde";
      this.setChance(1);
      this.setMinimumDays(60);
   }

   public void randomizeVehicleStory(IsoMetaGrid.Zone var1, IsoChunk var2) {
      boolean var3 = Rand.NextBool(5);
      IsoGridSquare var4 = IsoCell.getInstance().getGridSquare(this.minX, this.minY, var1.z);
      if (var4 != null) {
         IsoDirections var5 = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
         if (this.horizontalZone) {
            var5 = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
         }

         BaseVehicle var6 = this.addVehicle(var1, var4, var2, "bad", (String)null, (Integer)null, var5, (String)null);
         int var7 = Rand.Next(4);
         String var8 = null;
         switch(var7) {
         case 0:
            var8 = "Front";
            break;
         case 1:
            var8 = "Rear";
            break;
         case 2:
            var8 = "Left";
            break;
         case 3:
            var8 = "Right";
         }

         var6 = var6.setSmashed(var8);
         var6.setBloodIntensity("Front", Rand.Next(0.7F, 1.0F));
         var6.setBloodIntensity("Rear", Rand.Next(0.7F, 1.0F));
         var6.setBloodIntensity("Left", Rand.Next(0.7F, 1.0F));
         var6.setBloodIntensity("Right", Rand.Next(0.7F, 1.0F));
         ArrayList var9 = this.addZombiesOnVehicle(Rand.Next(5, 10), (String)null, (Integer)null, var6);
         if (var9 != null) {
            for(int var10 = 0; var10 < var9.size(); ++var10) {
               IsoZombie var11 = (IsoZombie)var9.get(var10);
               var11.upKillCount = false;
               this.addBloodSplat(var11.getSquare(), Rand.Next(10, 20));
               if (var3) {
                  var11.setSkeleton(true);
                  var11.getHumanVisual().setSkinTextureIndex(0);
               } else {
                  var11.DoZombieInventory();
                  if (Rand.NextBool(10)) {
                     var11.setFakeDead(true);
                     var11.bCrawling = true;
                     var11.setCanWalk(false);
                     var11.setCrawlerType(1);
                  }
               }

               new IsoDeadBody(var11, false);
            }

            this.addZombiesOnVehicle(Rand.Next(12, 20), (String)null, (Integer)null, var6);
         }
      }
   }
}
