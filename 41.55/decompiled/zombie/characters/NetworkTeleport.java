package zombie.characters;

import java.util.Comparator;
import java.util.Map.Entry;
import zombie.GameTime;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.MPStatisticClient;
import zombie.network.packets.PlayerPacket;

public class NetworkTeleport {
   private NetworkTeleport.Type teleportType;
   private IsoGameCharacter character;
   private boolean setNewPos;
   private float nx;
   private float ny;
   private byte nz;
   public float ndirection;
   private float tx;
   private float ty;
   private byte tz;
   private long tt;
   private long startTime;
   private long duration;

   public NetworkTeleport(IsoGameCharacter var1, NetworkTeleport.Type var2, float var3, float var4, byte var5, float var6) {
      this.teleportType = NetworkTeleport.Type.none;
      this.character = null;
      this.setNewPos = false;
      this.nx = 0.0F;
      this.ny = 0.0F;
      this.nz = 0;
      this.tx = 0.0F;
      this.ty = 0.0F;
      this.tz = 0;
      this.tt = 0L;
      this.character = var1;
      this.setNewPos = false;
      this.nx = var3;
      this.ny = var4;
      this.nz = var5;
      this.teleportType = var2;
      this.startTime = System.currentTimeMillis();
      this.duration = (long)(1000.0D * (double)var6);
      var1.setTeleport(this);
      if (Core.bDebug && DebugOptions.instance.MultiplayerShowTeleport.getValue() && var1 instanceof IsoZombie) {
         NetworkCharacter.PredictionMoveTypes var7 = ((IsoZombie)var1).networkAI.predictionType;
         long var8 = (long)((IsoZombie)var1).networkAI.predictionTime;
         var1.debugData.entrySet().stream().sorted(Entry.comparingByKey(Comparator.naturalOrder())).forEach((var0) -> {
            DebugLog.log(DebugType.Multiplayer, "==> " + (String)var0.getValue());
         });
         DebugLog.log(DebugType.Multiplayer, String.format("NetworkTeleport Z_%d distance=%.3f, prediction=%s, time=%d", var1.getOnlineID(), IsoUtils.DistanceTo(var1.x, var1.y, var3, var4), var7, var8));
         var1.teleportDebug = new NetworkTeleport.NetworkTeleportDebug((short)var1.getOnlineID(), var1.x, var1.y, var1.z, var3, var4, (float)var5, var8, var7);
      }

   }

   public void process() {
      float var1 = Math.min(1.0F, (float)(System.currentTimeMillis() - this.startTime) / (float)this.duration);
      switch(this.teleportType) {
      case disappearing:
         if (var1 < 0.99F) {
            this.character.setAlphaAndTarget(1.0F - var1);
         } else {
            this.stop();
         }
         break;
      case teleportation:
         if (var1 < 0.5F) {
            if (this.character.isoPlayer == null || this.character.isoPlayer != null && this.character.isoPlayer.spottedByPlayer) {
               this.character.setAlphaAndTarget(1.0F - var1 * 2.0F);
            }
         } else if (var1 < 0.99F) {
            if (!this.setNewPos) {
               this.setNewPos = true;
               this.character.setX(this.nx);
               this.character.setY(this.ny);
               this.character.setZ((float)this.nz);
               this.character.ensureOnTile();
            }

            if (this.character.isoPlayer == null || this.character.isoPlayer != null && this.character.isoPlayer.spottedByPlayer) {
               this.character.setAlphaAndTarget((var1 - 0.5F) * 2.0F);
            }
         } else {
            this.stop();
         }
         break;
      case materialization:
         if (var1 < 0.99F) {
            this.character.setAlphaAndTarget(var1);
         } else {
            this.stop();
         }
      }

   }

   public void stop() {
      this.character.setTeleport((NetworkTeleport)null);
      switch(this.teleportType) {
      case disappearing:
         this.character.setTargetAlpha(0.0F);
         break;
      case teleportation:
      case materialization:
         this.character.setTargetAlpha(1.0F);
      }

      this.character = null;
   }

   public static boolean teleport(IsoGameCharacter var0, NetworkTeleport.Type var1, float var2, float var3, byte var4, float var5) {
      if (!var0.isTeleporting()) {
         if (var0 instanceof IsoZombie) {
            MPStatisticClient.getInstance().incrementZombiesTeleports();
         } else {
            MPStatisticClient.getInstance().incrementRemotePlayersTeleports();
         }

         new NetworkTeleport(var0, var1, var2, var3, var4, var5);
         return true;
      } else {
         return false;
      }
   }

   public static boolean teleport(IsoGameCharacter var0, PlayerPacket var1, float var2) {
      if (!var0.isTeleporting()) {
         if (var0 instanceof IsoZombie) {
            MPStatisticClient.getInstance().incrementZombiesTeleports();
         } else {
            MPStatisticClient.getInstance().incrementRemotePlayersTeleports();
         }

         IsoGridSquare var3 = IsoWorld.instance.CurrentCell.getGridSquare((double)var0.x, (double)var0.y, (double)var0.z);
         if (var3 == null) {
            IsoGridSquare var8 = IsoWorld.instance.CurrentCell.getGridSquare((double)var1.realx, (double)var1.realy, (double)var1.realz);
            var0.setAlphaAndTarget(0.0F);
            var0.setX(var1.realx);
            var0.setY(var1.realy);
            var0.setZ((float)var1.realz);
            var0.ensureOnTile();
            int var9 = (int)(GameTime.getServerTime() / 1000000L);
            float var10 = 0.5F * Math.min(1.0F, Math.max(0.0F, ((float)var9 + var2 * 1000.0F - (float)var1.realt) / (float)(var1.t - var1.realt)));
            NetworkTeleport var7 = new NetworkTeleport(var0, NetworkTeleport.Type.materialization, var10 * var1.x + (1.0F - var10) * var1.realx, var10 * var1.y + (1.0F - var10) * var1.realy, (byte)((int)(var10 * (float)var1.z + (1.0F - var10) * (float)var1.realz)), var2);
            var7.ndirection = var1.direction;
            var7.tx = var1.x;
            var7.ty = var1.y;
            var7.tz = var1.z;
            var7.tt = (long)var1.t;
            return true;
         } else {
            int var4 = (int)(GameTime.getServerTime() / 1000000L);
            float var5 = 0.5F * Math.min(1.0F, Math.max(0.0F, ((float)var4 + var2 * 1000.0F - (float)var1.realt) / (float)(var1.t - var1.realt)));
            NetworkTeleport var6 = new NetworkTeleport(var0, NetworkTeleport.Type.teleportation, var5 * var1.x + (1.0F - var5) * var1.realx, var5 * var1.y + (1.0F - var5) * var1.realy, (byte)((int)(var5 * (float)var1.z + (1.0F - var5) * (float)var1.realz)), var2);
            var6.ndirection = var1.direction;
            var6.tx = var1.x;
            var6.ty = var1.y;
            var6.tz = var1.z;
            var6.tt = (long)var1.t;
            return true;
         }
      } else {
         return false;
      }
   }

   public static void update(IsoGameCharacter var0, PlayerPacket var1) {
      if (var0.isTeleporting()) {
         NetworkTeleport var2 = var0.getTeleport();
         if (var2.teleportType == NetworkTeleport.Type.teleportation) {
            float var3 = Math.min(1.0F, (float)(System.currentTimeMillis() - var2.startTime) / (float)var2.duration);
            if (var3 < 0.5F) {
               int var4 = (int)(GameTime.getServerTime() / 1000000L);
               float var5 = 0.5F * Math.min(1.0F, Math.max(0.0F, ((float)var4 + (float)var2.duration * 1000.0F - (float)var1.realt) / (float)(var1.t - var1.realt)));
               var2.nx = var5 * var1.x + (1.0F - var5) * var1.realx;
               var2.ny = var5 * var1.y + (1.0F - var5) * var1.realy;
               var2.nz = (byte)((int)(var5 * (float)var1.z + (1.0F - var5) * (float)var1.realz));
            }

            var2.ndirection = var1.direction;
            var2.tx = var1.x;
            var2.ty = var1.y;
            var2.tz = var1.z;
            var2.tt = (long)var1.t;
         }
      }
   }

   public static enum Type {
      none,
      disappearing,
      teleportation,
      materialization;

      // $FF: synthetic method
      private static NetworkTeleport.Type[] $values() {
         return new NetworkTeleport.Type[]{none, disappearing, teleportation, materialization};
      }
   }

   public static class NetworkTeleportDebug {
      short id;
      float nx;
      float ny;
      float nz;
      float lx;
      float ly;
      float lz;
      long time;
      NetworkCharacter.PredictionMoveTypes type;

      public NetworkTeleportDebug(short var1, float var2, float var3, float var4, float var5, float var6, float var7, long var8, NetworkCharacter.PredictionMoveTypes var10) {
         this.id = var1;
         this.nx = var5;
         this.ny = var6;
         this.nz = var7;
         this.lx = var2;
         this.ly = var3;
         this.lz = var4;
         this.time = var8;
         this.type = var10;
      }
   }
}
