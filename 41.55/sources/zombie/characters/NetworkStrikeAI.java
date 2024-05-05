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

	public static void addStrike(NetworkStrikeAI.Strike strike) {
		DebugLog.log("NetworkStrikeAI.addStrike objType:" + strike.objType);
		strike.player.strikes.add(strike);
		if ((strike.objType == 1 || strike.objType == 2) && strike.zom.strike == null) {
			strike.zom.strike = strike;
		}
	}

	public static boolean canStrike(NetworkStrikeAI.Strike strike) {
		if (strike.objType != 1 && strike.objType != 2) {
			if (strike.objType == 3) {
				DebugLog.log("NetworkStrikeAI.canStrike objType:" + strike.objType);
				return true;
			} else if (strike.objType == 4) {
				DebugLog.log("NetworkStrikeAI.canStrike objType:" + strike.objType);
				return true;
			} else {
				DebugLog.General.error("NetworkStrikeAI: invalid objType");
				return false;
			}
		} else {
			Vector2 vector2 = new Vector2(strike.zom.x - strike.player.x, strike.zom.y - strike.player.y);
			if (strike.item instanceof HandWeapon && ((HandWeapon)strike.item).isAimedFirearm()) {
				HandWeapon handWeapon = (HandWeapon)strike.item;
				float float1 = handWeapon.getMaxRange() + handWeapon.getAimingPerkRangeModifier() * (strike.aiming / 2.0F);
				return Math.sqrt((double)strike.player.getDistanceSq(strike.zom)) < (double)float1;
			} else {
				if (strike.player.getDistanceSq(strike.zom) > 5.0F && NetworkTeleport.teleport(strike.player, NetworkTeleport.Type.teleportation, strike.tx, strike.ty, (byte)((int)strike.tz), 1.0F)) {
					DebugLog.log("NetworkStrikeAI.canStrike Distance:" + strike.player.getDistanceSq(strike.zom));
				}

				if (strike.zom instanceof IsoZombie && strike.zom.getOnlineID() == -1) {
					DebugLog.log(DebugType.Multiplayer, String.format("Z%d: ( %.2f ; %.2f )", strike.zom.getOnlineID(), strike.zom.x, strike.zom.y));
				}

				if (strike.zom instanceof IsoPlayer) {
					return Math.sqrt((double)strike.player.getDistanceSq(strike.zom)) < 1.0;
				} else {
					return Math.sqrt((double)strike.player.getDistanceSq(strike.zom)) < 1.0 && Math.abs(strike.player.getLookAngleRadians() - vector2.getDirection()) < 0.1F;
				}
			}
		}
	}

	public static void deleteStrike(NetworkStrikeAI.Strike strike) {
		DebugLog.log("NetworkStrikeAI.deleteStrike objType:" + strike.objType);
		if (strike.objType == 1 || strike.objType == 2) {
			strike.zom.strike = null;
		}

		strike.player.strikes.remove(0);
	}

	public static void executeStrike(NetworkStrikeAI.Strike strike) {
		DebugLog.log("NetworkStrikeAI.executeStrike objType:" + strike.objType);
		strike.player.useChargeDelta = strike.charge;
		strike.player.setVariable("recoilVarX", strike.aiming / 10.0F);
		strike.player.setVariable("ZombieHitReaction", strike.zombieHitReaction);
		HandWeapon handWeapon = strike.player.bareHands;
		if (strike.item instanceof HandWeapon) {
			handWeapon = (HandWeapon)strike.item;
		}

		if (strike.objType == 1) {
			IsoZombie zombie = (IsoZombie)strike.zom;
			if (zombie.getStateMachine().getCurrent() == ZombieOnGroundState.instance()) {
				zombie.setReanimateTimer((float)(Rand.Next(60) + 30));
			}

			if (zombie.getStateMachine().getCurrent() == ZombieGetUpState.instance()) {
				float float1 = 15.0F - strike.zom.def.Frame;
				if (float1 < 2.0F) {
					float1 = 2.0F;
				}

				strike.zom.def.Frame = float1;
				zombie.setReanimateTimer((float)(Rand.Next(60) + 30));
			}
		}

		String string;
		if (strike.objType == 2) {
			ZLogger zLogger = LoggerManager.getLogger("pvp");
			string = strike.player.username;
			zLogger.write("user " + string + " " + LoggerManager.getPlayerCoords(strike.player) + " hit user " + ((IsoPlayer)strike.zom).username + " " + LoggerManager.getPlayerCoords((IsoPlayer)strike.zom) + " with " + strike.item.getName());
		}

		if (strike.objType == 1 || strike.objType == 2) {
			string = strike.player.username;
			DebugLog.log(DebugType.Combat, "player " + string + " hit " + (strike.objType == 1 ? "zombie " + strike.player : "player " + ((IsoPlayer)strike.zom).username) + " health=" + strike.zom.getHealth() + (strike.bIgnoreDamage ? " for no dmg" : " for dmg " + strike.damageSplit));
			if (strike.objType == 1) {
				((IsoZombie)strike.zom).bKnockedDown = (strike.zombieFlags & 1) != 0;
				((IsoZombie)strike.zom).setFakeDead((strike.zombieFlags & 2) != 0);
				((IsoZombie)strike.zom).setHitFromBehind((strike.zombieFlags & 4) != 0);
				((IsoZombie)strike.zom).bStaggerBack = (strike.zombieFlags & 8) != 0;
				((IsoZombie)strike.zom).setVariable("bKnifeDeath", (strike.zombieFlags & 16) != 0);
				((IsoZombie)strike.zom).setFallOnFront((strike.zombieFlags & 32) != 0);
				((IsoZombie)strike.zom).setCloseKilled(strike.bCloseKilled);
				if (strike.jawStabAttach) {
					((IsoZombie)strike.zom).setAttachedItem("JawStab", strike.item);
					((IsoZombie)strike.zom).setVariable("bKnifeDeath", true);
				}
			}

			strike.player.isCrit = strike.isCrit;
			if (strike.zom instanceof IsoPlayer && strike.isCrit) {
				((IsoPlayer)strike.zom).setM_bKnockedDown(true);
			}

			strike.player.bDoShove = strike.doShove;
			if (strike.zombieHitReaction != null && !strike.zombieHitReaction.isEmpty()) {
				strike.player.setVariable("ZombieHitReaction", strike.zombieHitReaction);
			}

			strike.player.setAimAtFloor(strike.isAimAtFloor);
			strike.zom.Hit(handWeapon, strike.player, strike.damageSplit, strike.bIgnoreDamage, strike.rangeDel, true);
			strike.zom.setHitForce(strike.ohit);
			strike.zom.getHitDir().x = strike.ohitx;
			strike.zom.getHitDir().y = strike.ohity;
			if (strike.objType == 1) {
				((IsoZombie)strike.zom).bKnockedDown = (strike.zombieFlags & 1) != 0;
				((IsoZombie)strike.zom).setFakeDead((strike.zombieFlags & 2) != 0);
				((IsoZombie)strike.zom).setHitFromBehind((strike.zombieFlags & 4) != 0);
				((IsoZombie)strike.zom).bStaggerBack = (strike.zombieFlags & 8) != 0;
				((IsoZombie)strike.zom).setVariable("bKnifeDeath", (strike.zombieFlags & 16) != 0);
				((IsoZombie)strike.zom).setFallOnFront((strike.zombieFlags & 32) != 0);
			}

			if (!(strike.zom instanceof IsoPlayer) && !(strike.player instanceof IsoPlayer)) {
				if (strike.zom.hasAnimationPlayer() && strike.zom.getAnimationPlayer().isReady() && !strike.zom.getAnimationPlayer().isBoneTransformsNeedFirstFrame()) {
					strike.zom.getAnimationPlayer().setAngle(strike.angle);
				} else {
					strike.zom.getForwardDirection().setDirection(strike.angle);
				}
			}

			if (strike.hitReaction != null && !strike.hitReaction.isEmpty()) {
				strike.zom.setHitReaction(strike.hitReaction);
			}

			LuaEventManager.triggerEvent("OnWeaponHitXp", strike.player, handWeapon, strike.zom, strike.damageSplit);
			if (strike.zom.isAlive() && strike.dead) {
				strike.zom.setOnDeathDone(true);
				if (strike.zom instanceof IsoZombie) {
					((IsoZombie)strike.zom).DoZombieInventory();
				}

				strike.zom.setHealth(0.0F);
				LuaEventManager.triggerEvent("OnZombieDead", strike.zom);
				strike.zom.DoDeath((HandWeapon)null, (IsoGameCharacter)null, false);
			}
		}

		if (strike.objType == 3) {
			strike.vehicle.hitVehicle(strike.player, handWeapon);
		}

		if (strike.objType == 4) {
			strike.player.setAttackTargetSquare(strike.player.getCell().getGridSquare((double)strike.tx, (double)strike.ty, (double)strike.tz));
		}

		strike.player.pressedAttack(strike.attackType);
		strike.player.isCrit = strike.isCrit;
		if (strike.zom instanceof IsoPlayer && strike.isCrit) {
			((IsoPlayer)strike.zom).setM_bKnockedDown(true);
		}

		if (strike.player.isAttackStarted() && strike.item instanceof HandWeapon && handWeapon.isRanged() && !strike.player.bDoShove) {
			strike.player.startMuzzleFlash();
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
