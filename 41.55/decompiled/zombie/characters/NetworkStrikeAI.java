package zombie.characters;

import zombie.Lua.LuaEventManager;
import zombie.ai.states.ZombieGetUpState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.core.Rand;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;

public class NetworkStrikeAI {
   public static final int objType_Zombie = 1;
   public static final int objType_Player = 2;
   public static final int objType_Vehicle = 3;
   public static final int objType_None = 4;
   public static final int strike_max_lifetime = 3000;

   public static void addStrike(NetworkStrikeAI.Strike var0) {
      DebugLog.log("NetworkStrikeAI.addStrike objType:" + var0.objType);
      var0.player.strikes.add(var0);
      if ((var0.objType == 1 || var0.objType == 2) && var0.zom.strike == null) {
         var0.zom.strike = var0;
      }

   }

   public static boolean canStrike(NetworkStrikeAI.Strike var0) {
      if (var0.objType != 1 && var0.objType != 2) {
         if (var0.objType == 3) {
            DebugLog.log("NetworkStrikeAI.canStrike objType:" + var0.objType);
            return true;
         } else if (var0.objType == 4) {
            DebugLog.log("NetworkStrikeAI.canStrike objType:" + var0.objType);
            return true;
         } else {
            DebugLog.General.error("NetworkStrikeAI: invalid objType");
            return false;
         }
      } else {
         Vector2 var1 = new Vector2(var0.zom.x - var0.player.x, var0.zom.y - var0.player.y);
         if (var0.item instanceof HandWeapon && ((HandWeapon)var0.item).isAimedFirearm()) {
            HandWeapon var2 = (HandWeapon)var0.item;
            float var3 = var2.getMaxRange() + var2.getAimingPerkRangeModifier() * (var0.aiming / 2.0F);
            return Math.sqrt((double)var0.player.getDistanceSq(var0.zom)) < (double)var3;
         } else {
            if (var0.player.getDistanceSq(var0.zom) > 5.0F && NetworkTeleport.teleport(var0.player, NetworkTeleport.Type.teleportation, var0.tx, var0.ty, (byte)((int)var0.tz), 1.0F)) {
               DebugLog.log("NetworkStrikeAI.canStrike Distance:" + var0.player.getDistanceSq(var0.zom));
            }

            if (var0.zom instanceof IsoZombie && var0.zom.getOnlineID() == -1) {
               DebugLog.log(DebugType.Multiplayer, String.format("Z%d: ( %.2f ; %.2f )", var0.zom.getOnlineID(), var0.zom.x, var0.zom.y));
            }

            if (var0.zom instanceof IsoPlayer) {
               return Math.sqrt((double)var0.player.getDistanceSq(var0.zom)) < 1.0D;
            } else {
               return Math.sqrt((double)var0.player.getDistanceSq(var0.zom)) < 1.0D && Math.abs(var0.player.getLookAngleRadians() - var1.getDirection()) < 0.1F;
            }
         }
      }
   }

   public static void deleteStrike(NetworkStrikeAI.Strike var0) {
      DebugLog.log("NetworkStrikeAI.deleteStrike objType:" + var0.objType);
      if (var0.objType == 1 || var0.objType == 2) {
         var0.zom.strike = null;
      }

      var0.player.strikes.remove(0);
   }

   public static void executeStrike(NetworkStrikeAI.Strike var0) {
      DebugLog.log("NetworkStrikeAI.executeStrike objType:" + var0.objType);
      var0.player.useChargeDelta = var0.charge;
      var0.player.setVariable("recoilVarX", var0.aiming / 10.0F);
      var0.player.setVariable("ZombieHitReaction", var0.zombieHitReaction);
      HandWeapon var1 = var0.player.bareHands;
      if (var0.item instanceof HandWeapon) {
         var1 = (HandWeapon)var0.item;
      }

      if (var0.objType == 1) {
         IsoZombie var2 = (IsoZombie)var0.zom;
         if (var2.getStateMachine().getCurrent() == ZombieOnGroundState.instance()) {
            var2.setReanimateTimer((float)(Rand.Next(60) + 30));
         }

         if (var2.getStateMachine().getCurrent() == ZombieGetUpState.instance()) {
            float var3 = 15.0F - var0.zom.def.Frame;
            if (var3 < 2.0F) {
               var3 = 2.0F;
            }

            var0.zom.def.Frame = var3;
            var2.setReanimateTimer((float)(Rand.Next(60) + 30));
         }
      }

      String var10001;
      if (var0.objType == 2) {
         ZLogger var10000 = LoggerManager.getLogger("pvp");
         var10001 = var0.player.username;
         var10000.write("user " + var10001 + " " + LoggerManager.getPlayerCoords(var0.player) + " hit user " + ((IsoPlayer)var0.zom).username + " " + LoggerManager.getPlayerCoords((IsoPlayer)var0.zom) + " with " + var0.item.getName());
      }

      if (var0.objType == 1 || var0.objType == 2) {
         var10001 = var0.player.username;
         DebugLog.log(DebugType.Combat, "player " + var10001 + " hit " + (var0.objType == 1 ? "zombie " + var0.player : "player " + ((IsoPlayer)var0.zom).username) + " health=" + var0.zom.getHealth() + (var0.bIgnoreDamage ? " for no dmg" : " for dmg " + var0.damageSplit));
         if (var0.objType == 1) {
            ((IsoZombie)var0.zom).bKnockedDown = (var0.zombieFlags & 1) != 0;
            ((IsoZombie)var0.zom).setFakeDead((var0.zombieFlags & 2) != 0);
            ((IsoZombie)var0.zom).setHitFromBehind((var0.zombieFlags & 4) != 0);
            ((IsoZombie)var0.zom).bStaggerBack = (var0.zombieFlags & 8) != 0;
            ((IsoZombie)var0.zom).setVariable("bKnifeDeath", (var0.zombieFlags & 16) != 0);
            ((IsoZombie)var0.zom).setFallOnFront((var0.zombieFlags & 32) != 0);
            ((IsoZombie)var0.zom).setCloseKilled(var0.bCloseKilled);
            if (var0.jawStabAttach) {
               ((IsoZombie)var0.zom).setAttachedItem("JawStab", var0.item);
               ((IsoZombie)var0.zom).setVariable("bKnifeDeath", true);
            }
         }

         var0.player.isCrit = var0.isCrit;
         if (var0.zom instanceof IsoPlayer && var0.isCrit) {
            ((IsoPlayer)var0.zom).setM_bKnockedDown(true);
         }

         var0.player.bDoShove = var0.doShove;
         if (var0.zombieHitReaction != null && !var0.zombieHitReaction.isEmpty()) {
            var0.player.setVariable("ZombieHitReaction", var0.zombieHitReaction);
         }

         var0.player.setAimAtFloor(var0.isAimAtFloor);
         var0.zom.Hit(var1, var0.player, var0.damageSplit, var0.bIgnoreDamage, var0.rangeDel, true);
         var0.zom.setHitForce(var0.ohit);
         var0.zom.getHitDir().x = var0.ohitx;
         var0.zom.getHitDir().y = var0.ohity;
         if (var0.objType == 1) {
            ((IsoZombie)var0.zom).bKnockedDown = (var0.zombieFlags & 1) != 0;
            ((IsoZombie)var0.zom).setFakeDead((var0.zombieFlags & 2) != 0);
            ((IsoZombie)var0.zom).setHitFromBehind((var0.zombieFlags & 4) != 0);
            ((IsoZombie)var0.zom).bStaggerBack = (var0.zombieFlags & 8) != 0;
            ((IsoZombie)var0.zom).setVariable("bKnifeDeath", (var0.zombieFlags & 16) != 0);
            ((IsoZombie)var0.zom).setFallOnFront((var0.zombieFlags & 32) != 0);
         }

         if (!(var0.zom instanceof IsoPlayer) && !(var0.player instanceof IsoPlayer)) {
            if (var0.zom.hasAnimationPlayer() && var0.zom.getAnimationPlayer().isReady() && !var0.zom.getAnimationPlayer().isBoneTransformsNeedFirstFrame()) {
               var0.zom.getAnimationPlayer().setAngle(var0.angle);
            } else {
               var0.zom.getForwardDirection().setDirection(var0.angle);
            }
         }

         if (var0.hitReaction != null && !var0.hitReaction.isEmpty()) {
            var0.zom.setHitReaction(var0.hitReaction);
         }

         LuaEventManager.triggerEvent("OnWeaponHitXp", var0.player, var1, var0.zom, var0.damageSplit);
         if (var0.zom.isAlive() && var0.dead) {
            var0.zom.setOnDeathDone(true);
            if (var0.zom instanceof IsoZombie) {
               ((IsoZombie)var0.zom).DoZombieInventory();
            }

            var0.zom.setHealth(0.0F);
            LuaEventManager.triggerEvent("OnZombieDead", var0.zom);
            var0.zom.DoDeath((HandWeapon)null, (IsoGameCharacter)null, false);
         }
      }

      if (var0.objType == 3) {
         var0.vehicle.hitVehicle(var0.player, var1);
      }

      if (var0.objType == 4) {
         var0.player.setAttackTargetSquare(var0.player.getCell().getGridSquare((double)var0.tx, (double)var0.ty, (double)var0.tz));
      }

      var0.player.pressedAttack(var0.attackType);
      var0.player.isCrit = var0.isCrit;
      if (var0.zom instanceof IsoPlayer && var0.isCrit) {
         ((IsoPlayer)var0.zom).setM_bKnockedDown(true);
      }

      if (var0.player.isAttackStarted() && var0.item instanceof HandWeapon && var1.isRanged() && !var0.player.bDoShove) {
         var0.player.startMuzzleFlash();
      }

   }

   public static class Strike {
      public IsoPlayer player;
      public byte objType;
      public boolean dead;
      public boolean doShove;
      public InventoryItem item;
      public IsoGameCharacter zom = null;
      public BaseVehicle vehicle = null;
      public float damageSplit;
      public boolean bIgnoreDamage;
      public boolean bCloseKilled;
      public boolean isCrit;
      public float rangeDel;
      public float tx;
      public float ty;
      public float tz;
      public float angle;
      public float ohit;
      public float ohitx;
      public float ohity;
      public float charge;
      public float aiming;
      public String zombieHitReaction;
      public String hitReaction;
      public long lifeTime;
      public String attackType;
      public short zombieFlags;
      public boolean helmetFall;
      public boolean jawStabAttach;
      public boolean isAimAtFloor;
   }
}
