package zombie.network;

import java.util.Iterator;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.iso.IsoUtils;

public class MPStatisticClient {
   public static MPStatisticClient instance = new MPStatisticClient();
   private boolean needUpdate = true;
   private int zombiesLocalOwnership = 0;
   private float zombiesDesyncAVG = 0.0F;
   private float zombiesDesyncMax = 0.0F;
   private int zombiesTeleports = 0;
   private float remotePlayersDesyncAVG = 0.0F;
   private float remotePlayersDesyncMax = 0.0F;
   private int remotePlayersTeleports = 0;

   public static MPStatisticClient getInstance() {
      return instance;
   }

   public void incrementZombiesTeleports() {
      ++this.zombiesTeleports;
   }

   public void incrementRemotePlayersTeleports() {
      ++this.remotePlayersTeleports;
   }

   public void update() {
      if (this.needUpdate) {
         this.needUpdate = false;

         float var3;
         for(int var1 = 0; var1 < GameClient.IDToZombieMap.values().length; ++var1) {
            IsoZombie var2 = (IsoZombie)GameClient.IDToZombieMap.values()[var1];
            if (var2.networkAI.isLocalControl()) {
               ++this.zombiesLocalOwnership;
            } else {
               var3 = IsoUtils.DistanceTo(var2.x, var2.y, var2.z, var2.realx, var2.realy, (float)var2.realz);
               this.zombiesDesyncAVG += (var3 - this.zombiesDesyncAVG) * 0.05F;
               if (var3 > this.zombiesDesyncMax) {
                  this.zombiesDesyncMax = var3;
               }
            }
         }

         Iterator var4 = GameClient.IDToPlayerMap.values().iterator();

         while(var4.hasNext()) {
            IsoPlayer var5 = (IsoPlayer)var4.next();
            if (!var5.isLocalPlayer()) {
               var3 = IsoUtils.DistanceTo(var5.x, var5.y, var5.z, var5.realx, var5.realy, (float)var5.realz);
               this.remotePlayersDesyncAVG += (var3 - this.remotePlayersDesyncAVG) * 0.05F;
               if (var3 > this.remotePlayersDesyncMax) {
                  this.remotePlayersDesyncMax = var3;
               }
            }
         }
      }

   }

   public void send(ByteBufferWriter var1) {
      var1.putInt(GameClient.IDToZombieMap.size());
      var1.putInt(this.zombiesLocalOwnership);
      var1.putFloat(this.zombiesDesyncAVG);
      var1.putFloat(this.zombiesDesyncMax);
      var1.putInt(this.zombiesTeleports);
      var1.putInt(GameClient.IDToPlayerMap.size());
      var1.putFloat(this.remotePlayersDesyncAVG);
      var1.putFloat(this.remotePlayersDesyncMax);
      var1.putInt(this.remotePlayersTeleports);
      this.zombiesDesyncMax = 0.0F;
      this.zombiesTeleports = 0;
      this.remotePlayersDesyncMax = 0.0F;
      this.remotePlayersTeleports = 0;
      this.zombiesLocalOwnership = 0;
      this.needUpdate = true;
   }
}
