package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoFireplace;
import zombie.util.StringUtils;
import zombie.util.Type;


public final class PlayerSitOnGroundState extends State {
	private static final PlayerSitOnGroundState _instance = new PlayerSitOnGroundState();
	private static final int RAND_EXT = 2500;
	private static final Integer PARAM_FIRE = 0;
	private static final Integer PARAM_SITGROUNDANIM = 1;
	private static final Integer PARAM_CHECK_FIRE = 2;
	private static final Integer PARAM_CHANGE_ANIM = 3;

	public static PlayerSitOnGroundState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.put(PARAM_FIRE, this.checkFire(gameCharacter));
		hashMap.put(PARAM_CHECK_FIRE, System.currentTimeMillis());
		hashMap.put(PARAM_CHANGE_ANIM, 0L);
		gameCharacter.setSitOnGround(true);
		if ((gameCharacter.getPrimaryHandItem() == null || !(gameCharacter.getPrimaryHandItem() instanceof HandWeapon)) && (gameCharacter.getSecondaryHandItem() == null || !(gameCharacter.getSecondaryHandItem() instanceof HandWeapon))) {
			gameCharacter.setHideWeaponModel(true);
		}

		if (gameCharacter.getStateMachine().getPrevious() == IdleState.instance()) {
			gameCharacter.clearVariable("SitGroundStarted");
			gameCharacter.clearVariable("forceGetUp");
			gameCharacter.clearVariable("SitGroundAnim");
		}
	}

	private boolean checkFire(IsoGameCharacter gameCharacter) {
		IsoGridSquare square = gameCharacter.getCurrentSquare();
		for (int int1 = -4; int1 < 4; ++int1) {
			for (int int2 = -4; int2 < 4; ++int2) {
				IsoGridSquare square2 = square.getCell().getGridSquare(square.x + int1, square.y + int2, square.z);
				if (square2 != null) {
					if (square2.haveFire()) {
						return true;
					}

					for (int int3 = 0; int3 < square2.getObjects().size(); ++int3) {
						IsoFireplace fireplace = (IsoFireplace)Type.tryCastTo((IsoObject)square2.getObjects().get(int3), IsoFireplace.class);
						if (fireplace != null && fireplace.isLit()) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoPlayer player = (IsoPlayer)gameCharacter;
		if (player.pressedMovement(false)) {
			gameCharacter.StopAllActionQueue();
			gameCharacter.setVariable("forceGetUp", true);
		}

		long long1 = System.currentTimeMillis();
		if (long1 > (Long)hashMap.get(PARAM_CHECK_FIRE) + 5000L) {
			hashMap.put(PARAM_FIRE, this.checkFire(gameCharacter));
			hashMap.put(PARAM_CHECK_FIRE, long1);
		}

		if (gameCharacter.hasTimedActions()) {
			hashMap.put(PARAM_FIRE, false);
			gameCharacter.setVariable("SitGroundAnim", "Idle");
		}

		boolean boolean1 = (Boolean)hashMap.get(PARAM_FIRE);
		if (boolean1) {
			boolean boolean2 = long1 > (Long)hashMap.get(PARAM_CHANGE_ANIM);
			if (boolean2) {
				if ("Idle".equals(gameCharacter.getVariableString("SitGroundAnim"))) {
					gameCharacter.setVariable("SitGroundAnim", "WarmHands");
				} else if ("WarmHands".equals(gameCharacter.getVariableString("SitGroundAnim"))) {
					gameCharacter.setVariable("SitGroundAnim", "Idle");
				}

				hashMap.put(PARAM_CHANGE_ANIM, long1 + (long)Rand.Next(30000, 90000));
			}
		} else if (gameCharacter.getVariableBoolean("SitGroundStarted")) {
			gameCharacter.clearVariable("FireNear");
			gameCharacter.setVariable("SitGroundAnim", "Idle");
		}

		if ("WarmHands".equals(gameCharacter.getVariableString("SitGroundAnim")) && Rand.Next(Rand.AdjustForFramerate(2500)) == 0) {
			hashMap.put(PARAM_SITGROUNDANIM, gameCharacter.getVariableString("SitGroundAnim"));
			gameCharacter.setVariable("SitGroundAnim", "rubhands");
		}

		player.setInitiateAttack(false);
		player.attackStarted = false;
		player.setAttackType((String)null);
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setHideWeaponModel(false);
		if (StringUtils.isNullOrEmpty(gameCharacter.getVariableString("HitReaction"))) {
			gameCharacter.clearVariable("SitGroundStarted");
			gameCharacter.clearVariable("forceGetUp");
			gameCharacter.clearVariable("SitGroundAnim");
			gameCharacter.setIgnoreMovement(false);
		}
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("SitGroundStarted")) {
			gameCharacter.setVariable("SitGroundStarted", true);
			boolean boolean1 = (Boolean)gameCharacter.getStateMachineParams(this).get(PARAM_FIRE);
			if (boolean1) {
				gameCharacter.setVariable("SitGroundAnim", "WarmHands");
			} else {
				gameCharacter.setVariable("SitGroundAnim", "Idle");
			}
		}

		if (animEvent.m_EventName.equalsIgnoreCase("ResetSitOnGroundAnim")) {
			gameCharacter.setVariable("SitGroundAnim", (String)gameCharacter.getStateMachineParams(this).get(PARAM_SITGROUNDANIM));
		}
	}
}
