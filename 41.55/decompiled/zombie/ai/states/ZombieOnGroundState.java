package zombie.ai.states;

import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.model.Model;
import zombie.inventory.types.HandWeapon;
import zombie.iso.Vector3;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.packets.DeadBodyPacket;

public final class ZombieOnGroundState extends State {
   private static final ZombieOnGroundState _instance = new ZombieOnGroundState();
   static Vector3 tempVector = new Vector3();
   static Vector3 tempVectorBonePos = new Vector3();

   public static ZombieOnGroundState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      IsoZombie var2 = (IsoZombie)var1;
      var1.setCollidable(false);
      if (!var1.isDead()) {
         var1.setOnFloor(true);
      }

      if (!var1.isDead() && !var2.isFakeDead()) {
         if (!var2.isBecomeCrawler()) {
            if (!"Tutorial".equals(Core.GameMode)) {
               if ((GameServer.bServer || GameClient.bClient) && var2.networkAI.reanimateTimer > 0.0F) {
                  var1.setReanimateTimer(var2.networkAI.reanimateTimer);
               } else {
                  var1.setReanimateTimer((float)(Rand.Next(60) + 30));
               }
            }

         }
      } else {
         becomeCorpse(var2);
      }
   }

   public void execute(IsoGameCharacter var1) {
      IsoZombie var2 = (IsoZombie)var1;
      if (!var1.isDead() && !var2.isFakeDead()) {
         if (var2.isBecomeCrawler()) {
            if (!var2.isBeingSteppedOn() && !var2.isUnderVehicle()) {
               var2.bCrawling = true;
               var2.setCanWalk(false);
               var2.setReanimate(true);
               var2.setBecomeCrawler(false);
            }
         } else {
            if (var1.hasAnimationPlayer()) {
               var1.getAnimationPlayer().setTargetToAngle();
            }

            var1.setReanimateTimer(var1.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
            if (var1.getReanimateTimer() <= 2.0F && var1.isBeingSteppedOn() && var2.getReanimatedPlayer() == null) {
               var1.setReanimateTimer((float)(Rand.Next(60) + 30));
            }

         }
      } else {
         becomeCorpse(var2);
      }
   }

   private static void becomeCorpse(IsoZombie var0) {
      if (GameClient.bClient) {
         if (var0.networkAI.deadZombie == null) {
            if ((var0.lastPlayerHit & ~DeadBodyPacket.DIED_UNDER_VEHICLE) == IsoPlayer.getInstance().getOnlineID()) {
               GameClient.sendDeadZombie(var0);
            }
         } else if (var0.networkAI.deadZombie.isServer) {
            new IsoDeadBody(var0);
         }

      } else {
         if (!var0.isOnDeathDone()) {
            var0.setOnDeathDone(true);
            var0.DoZombieInventory();
            LuaEventManager.triggerEvent("OnZombieDead", var0);
            var0.DoDeath((HandWeapon)null, (IsoGameCharacter)null, false);
         }

         if (GameServer.bServer) {
            GameServer.sendDeadZombie(var0);
         }

         IsoDeadBody var1 = new IsoDeadBody(var0);
         if (!GameClient.bClient && var0.isFakeDead()) {
            var1.setCrawling(true);
         }

      }
   }

   public static boolean isCharacterStandingOnOther(IsoGameCharacter var0, IsoGameCharacter var1) {
      AnimationPlayer var2 = var1.getAnimationPlayer();
      int var3 = DoCollisionBoneCheck(var0, var1, var2.getSkinningBoneIndex("Bip01_Spine", -1), 0.32F);
      if (var3 == -1) {
         var3 = DoCollisionBoneCheck(var0, var1, var2.getSkinningBoneIndex("Bip01_L_Calf", -1), 0.18F);
      }

      if (var3 == -1) {
         var3 = DoCollisionBoneCheck(var0, var1, var2.getSkinningBoneIndex("Bip01_R_Calf", -1), 0.18F);
      }

      if (var3 == -1) {
         var3 = DoCollisionBoneCheck(var0, var1, var2.getSkinningBoneIndex("Bip01_Head", -1), 0.28F);
      }

      return var3 > -1;
   }

   private static int DoCollisionBoneCheck(IsoGameCharacter var0, IsoGameCharacter var1, int var2, float var3) {
      float var5 = 0.3F;
      Model.BoneToWorldCoords(var1, var2, tempVectorBonePos);

      for(int var6 = 1; var6 <= 10; ++var6) {
         float var7 = (float)var6 / 10.0F;
         tempVector.x = var0.x;
         tempVector.y = var0.y;
         tempVector.z = var0.z;
         Vector3 var10000 = tempVector;
         var10000.x += var0.getForwardDirection().x * var5 * var7;
         var10000 = tempVector;
         var10000.y += var0.getForwardDirection().y * var5 * var7;
         tempVector.x = tempVectorBonePos.x - tempVector.x;
         tempVector.y = tempVectorBonePos.y - tempVector.y;
         tempVector.z = 0.0F;
         boolean var8 = tempVector.getLength() < var3;
         if (var8) {
            return var2;
         }
      }

      return -1;
   }

   public void exit(IsoGameCharacter var1) {
   }
}
