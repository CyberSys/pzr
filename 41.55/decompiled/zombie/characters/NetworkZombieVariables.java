package zombie.characters;

import zombie.ai.states.ZombieTurnAlerted;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.iso.IsoMovingObject;
import zombie.network.GameClient;

public class NetworkZombieVariables {
   public static int getInt(IsoZombie var0, int var1) {
      switch(var1) {
      case 0:
         return (int)(var0.Health * 1000.0F);
      case 1:
         if (var0.target == null) {
            return -1;
         }

         return ((IAnimatable)var0.target).getOnlineID();
      case 2:
         return var0.getHitHeadWhileOnFloor();
      case 3:
         return var0.thumpFlag;
      case 4:
         if (!(var0.eatBodyTarget instanceof IsoPlayer)) {
            return -1;
         }

         return ((IsoPlayer)var0.eatBodyTarget).getOnlineID();
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 14:
      case 15:
      case 16:
      default:
         return 0;
      case 13:
         return (int)var0.TimeSinceSeenFlesh;
      case 17:
         return (int)(var0.LungeTimer * 1000.0F);
      case 18:
         Float var2 = (Float)var0.getStateMachineParams(ZombieTurnAlerted.instance()).get(ZombieTurnAlerted.PARAM_TARGET_ANGLE);
         return var2 == null ? 0 : var2.intValue();
      }
   }

   public static void setInt(IsoZombie var0, short var1, int var2) {
      switch(var1) {
      case 0:
         var0.Health = (float)var2 / 1000.0F;
         break;
      case 1:
         if (var2 == -1) {
            var0.setTargetSeenTime(0.0F);
            var0.target = null;
         } else {
            IsoPlayer var3 = (IsoPlayer)GameClient.IDToPlayerMap.get(var2);
            if (var3 != var0.target) {
               var0.setTargetSeenTime(0.0F);
               var0.target = var3;
            }
         }
         break;
      case 2:
         var0.setHitHeadWhileOnFloor(var2);
         break;
      case 3:
         var0.thumpFlag = var2;
         break;
      case 4:
         if (var2 == -1) {
            var0.eatBodyTarget = null;
         } else if (var0.vectorToTarget.getLength() <= (var0.bCrawling ? 1.4F : 0.72F)) {
            var0.eatBodyTarget = (IsoMovingObject)GameClient.IDToPlayerMap.get(var2);
         }
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 14:
      case 15:
      case 16:
      default:
         break;
      case 13:
         var0.TimeSinceSeenFlesh = (float)var2;
         break;
      case 17:
         var0.LungeTimer = (float)var2 / 1000.0F;
         break;
      case 18:
         var0.getStateMachineParams(ZombieTurnAlerted.instance()).put(ZombieTurnAlerted.PARAM_TARGET_ANGLE, (float)var2);
      }

   }

   public static int getBooleanVariables(IsoZombie var0) {
      byte var1 = 0;
      int var2 = var1 | (var0.isFakeDead() ? 1 : 0);
      var2 |= var0.bLunger ? 2 : 0;
      var2 |= var0.bRunning ? 4 : 0;
      var2 |= var0.bCrawling ? 8 : 0;
      var2 |= var0.isSitAgainstWall() ? 16 : 0;
      var2 |= var0.isOnDeathDone() ? 32 : 0;
      var2 |= var0.isFallOnFront() ? 64 : 0;
      var2 |= var0.isReanimatedPlayer() ? 128 : 0;
      var2 |= var0.isOnFire() ? 256 : 0;
      var2 |= var0.bStaggerBack ? 512 : 0;
      var2 |= var0.bKnockedDown ? 1024 : 0;
      var2 |= var0.isUseless() ? 2048 : 0;
      var2 |= var0.alerted ? 4096 : 0;
      var2 |= var0.isDead() ? 8192 : 0;
      var2 |= var0.isOnFloor() ? 16384 : 0;
      return var2;
   }

   public static void setBooleanVariables(IsoZombie var0, int var1) {
      var0.setFakeDead((var1 & 1) != 0);
      var0.bLunger = (var1 & 2) != 0;
      var0.bRunning = (var1 & 4) != 0;
      var0.bCrawling = (var1 & 8) != 0;
      var0.setSitAgainstWall((var1 & 16) != 0);
      var0.setOnDeathDone((var1 & 32) != 0);
      var0.setReanimatedPlayer((var1 & 128) != 0);
      if ((var1 & 256) != 0) {
         var0.SetOnFire();
      } else {
         var0.StopBurning();
      }

      var0.bStaggerBack = (var1 & 512) != 0;
      var0.bKnockedDown = (var1 & 1024) != 0;
      var0.setUseless((var1 & 2048) != 0);
      var0.alerted = (var1 & 4096) != 0;
      if ((var1 & 8192) != 0) {
         var0.setHealth(0.0F);
      }

      if (var0.isReanimatedPlayer()) {
         var0.setOnFloor((var1 & 16384) != 0);
      }

   }

   public static class VariablesInt {
      public static final byte helth = 0;
      public static final byte target = 1;
      public static final byte hitHeadWhileOnFloor = 2;
      public static final byte thumpFlag = 3;
      public static final byte eatBodyTarget = 4;
      public static final byte timeSinceSeenFlesh = 13;
      public static final byte lungeTimer = 17;
      public static final byte smParamTargetAngle = 18;
      public static final byte MAX = 19;
   }
}
