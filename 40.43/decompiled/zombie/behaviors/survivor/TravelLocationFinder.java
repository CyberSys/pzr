package zombie.behaviors.survivor;

import zombie.characters.SurvivorDesc;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;

public class TravelLocationFinder {
   private static float ScoreLocation(SurvivorDesc var0, IsoGridSquare var1) {
      float var2 = 1.0F;
      if (var1.getRoom() != null) {
         var2 += 10.0F;
         if (var1.getRoom().building == null) {
            return var2;
         }

         var2 += var1.getRoom().building.ScoreBuildingPersonSpecific(var0, false);
      }

      return var2;
   }

   public static IsoGridSquare FindLocation(SurvivorDesc var0, float var1, float var2, float var3, float var4, int var5) {
      IsoGridSquare var6 = null;
      float var7 = 0.0F;
      int var8 = 100;

      for(int var9 = 0; var9 < var5; ++var9) {
         --var8;
         if (var8 <= 0) {
            return null;
         }

         float var10 = 0.0F;
         int var11 = Rand.Next((int)var1, (int)var3);
         int var12 = Rand.Next((int)var2, (int)var4);
         IsoGridSquare var13 = IsoWorld.instance.CurrentCell.getGridSquare(var11, var12, 0);
         if (var13 != null && !var13.getProperties().Is(IsoFlagType.solidtrans) && !var13.getProperties().Is(IsoFlagType.solid)) {
            var10 = ScoreLocation(var0, var13);
            if (var10 > var7) {
               var6 = var13;
               var7 = var10;
            }
         } else {
            --var9;
         }
      }

      if (var7 > 0.0F) {
         return var6;
      } else {
         return null;
      }
   }
}
