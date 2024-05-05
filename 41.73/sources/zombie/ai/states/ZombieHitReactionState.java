package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMovingObject;
import zombie.iso.objects.IsoZombieGiblets;


public final class ZombieHitReactionState extends State {
	private static final ZombieHitReactionState _instance = new ZombieHitReactionState();
	private static final int TURN_TO_PLAYER = 1;
	private static final int HIT_REACTION_TIMER = 2;

	public static ZombieHitReactionState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		zombie.collideWhileHit = true;
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.put(1, Boolean.FALSE);
		hashMap.put(2, 0.0F);
		gameCharacter.clearVariable("onknees");
		if (zombie.isSitAgainstWall()) {
			gameCharacter.setHitReaction((String)null);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		gameCharacter.setOnFloor(((IsoZombie)gameCharacter).isKnockedDown());
		hashMap.put(2, (Float)hashMap.get(2) + GameTime.getInstance().getMultiplier());
		if (hashMap.get(1) == Boolean.TRUE) {
			if (!gameCharacter.isHitFromBehind()) {
				gameCharacter.setDir(IsoDirections.reverse(IsoDirections.fromAngle(gameCharacter.getHitDir())));
			} else {
				gameCharacter.setDir(IsoDirections.fromAngle(gameCharacter.getHitDir()));
			}
		} else if (gameCharacter.hasAnimationPlayer()) {
			gameCharacter.getAnimationPlayer().setTargetToAngle();
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		zombie.collideWhileHit = true;
		if (zombie.target != null) {
			zombie.AllowRepathDelay = 0.0F;
			zombie.spotted(zombie.target, true);
		}

		zombie.setStaggerBack(false);
		zombie.setHitReaction("");
		zombie.setEatBodyTarget((IsoMovingObject)null, false);
		zombie.setSitAgainstWall(false);
		zombie.setShootable(true);
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (animEvent.m_EventName.equalsIgnoreCase("DoDeath") && Boolean.parseBoolean(animEvent.m_ParameterValue) && gameCharacter.isAlive()) {
			gameCharacter.Kill(gameCharacter.getAttackedBy());
			if (zombie.upKillCount && gameCharacter.getAttackedBy() != null) {
				gameCharacter.getAttackedBy().setZombieKills(gameCharacter.getAttackedBy().getZombieKills() + 1);
			}
		}

		if (animEvent.m_EventName.equalsIgnoreCase("PlayDeathSound")) {
			gameCharacter.setDoDeathSound(false);
			gameCharacter.playDeadSound();
		}

		if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
			gameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
		}

		if (animEvent.m_EventName.equalsIgnoreCase("Collide") && ((IsoZombie)gameCharacter).speedType == 1) {
			((IsoZombie)gameCharacter).collideWhileHit = false;
		}

		boolean boolean1;
		if (animEvent.m_EventName.equalsIgnoreCase("ZombieTurnToPlayer")) {
			boolean1 = Boolean.parseBoolean(animEvent.m_ParameterValue);
			hashMap.put(1, boolean1 ? Boolean.TRUE : Boolean.FALSE);
		}

		if (animEvent.m_EventName.equalsIgnoreCase("CancelKnockDown")) {
			boolean1 = Boolean.parseBoolean(animEvent.m_ParameterValue);
			if (boolean1) {
				((IsoZombie)gameCharacter).setKnockedDown(false);
			}
		}

		if (animEvent.m_EventName.equalsIgnoreCase("KnockDown")) {
			gameCharacter.setOnFloor(true);
			((IsoZombie)gameCharacter).setKnockedDown(true);
		}

		if (animEvent.m_EventName.equalsIgnoreCase("SplatBlood")) {
			zombie.addBlood((BloodBodyPartType)null, true, false, false);
			zombie.addBlood((BloodBodyPartType)null, true, false, false);
			zombie.addBlood((BloodBodyPartType)null, true, false, false);
			zombie.playBloodSplatterSound();
			for (int int1 = 0; int1 < 10; ++int1) {
				zombie.getCurrentSquare().getChunk().addBloodSplat(zombie.x + Rand.Next(-0.5F, 0.5F), zombie.y + Rand.Next(-0.5F, 0.5F), zombie.z, Rand.Next(8));
				if (Rand.Next(5) == 0) {
					new IsoZombieGiblets(IsoZombieGiblets.GibletType.B, zombie.getCell(), zombie.getX(), zombie.getY(), zombie.getZ() + 0.3F, Rand.Next(-0.2F, 0.2F) * 1.5F, Rand.Next(-0.2F, 0.2F) * 1.5F);
				} else {
					new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, zombie.getCell(), zombie.getX(), zombie.getY(), zombie.getZ() + 0.3F, Rand.Next(-0.2F, 0.2F) * 1.5F, Rand.Next(-0.2F, 0.2F) * 1.5F);
				}
			}
		}

		if (animEvent.m_EventName.equalsIgnoreCase("SetState") && !zombie.isDead()) {
			if (zombie.getAttackedBy() != null && zombie.getAttackedBy().getVehicle() != null && "Floor".equals(zombie.getHitReaction())) {
				zombie.parameterZombieState.setState(ParameterZombieState.State.RunOver);
				return;
			}

			zombie.parameterZombieState.setState(ParameterZombieState.State.Hit);
		}
	}
}
