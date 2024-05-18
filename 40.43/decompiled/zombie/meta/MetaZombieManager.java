package zombie.meta;

import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class MetaZombieManager {
   public static MetaZombieManager instance = new MetaZombieManager();

   public void decayBloodAndCorpse(IsoMetaGrid.Zone var1) {
      if (!GameClient.bClient) {
         int var2 = 20 - var1.hourLastSeen;
         if (var2 < 3) {
            var2 = 3;
         }

         for(int var3 = var1.x; var3 < var1.x + var1.w; ++var3) {
            for(int var4 = var1.y; var4 < var1.y + var1.h; ++var4) {
               IsoGridSquare var5 = IsoWorld.instance.getCell().getGridSquare(var3, var4, 0);
               if (var5 != null) {
                  int var6;
                  for(var6 = 0; var6 < var5.getStaticMovingObjects().size(); ++var6) {
                     IsoMovingObject var7 = (IsoMovingObject)var5.getStaticMovingObjects().get(var6);
                     if (var7 instanceof IsoDeadBody && Rand.Next(var2) == 0) {
                        if (GameServer.bServer) {
                           GameServer.removeCorpseFromMap((IsoDeadBody)var7);
                        }

                        var7.square = var5;
                        var7.removeFromWorld();
                        var7.removeFromSquare();
                        --var6;
                     }
                  }

                  for(var6 = 0; var6 < var5.getChunk().FloorBloodSplats.size(); ++var6) {
                     if (Rand.Next(var2) == 0) {
                        var5.getChunk().FloorBloodSplats.remove(var6);
                        --var6;
                     }
                  }
               }
            }
         }

      }
   }
}
