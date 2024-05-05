package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoMovingObject;
import zombie.network.GameClient;
import zombie.util.StringUtils;


public class AttackNetworkState extends State {
	private static final AttackNetworkState s_instance = new AttackNetworkState();
	private String attackOutcome;

	public static AttackNetworkState instance() {
		return s_instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.clear();
		hashMap.put(0, Boolean.FALSE);
		this.attackOutcome = gameCharacter.getVariableString("AttackOutcome");
		gameCharacter.setVariable("AttackOutcome", "start");
		gameCharacter.clearVariable("AttackDidDamage");
		gameCharacter.clearVariable("ZombieBiteDone");
		zombie.setTargetSeenTime(1.0F);
		if (!zombie.bCrawling) {
			zombie.setVariable("AttackType", "bite");
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoGameCharacter gameCharacter2 = (IsoGameCharacter)zombie.target;
		if (gameCharacter2 == null || !"Chainsaw".equals(gameCharacter2.getVariableString("ZombieHitReaction"))) {
			String string = gameCharacter.getVariableString("AttackOutcome");
			if ("success".equals(string) && !gameCharacter.getVariableBoolean("bAttack") && (gameCharacter2 == null || !gameCharacter2.isGodMod()) && !gameCharacter.getVariableBoolean("AttackDidDamage") && gameCharacter.getVariableString("ZombieBiteDone") != "true") {
				gameCharacter.setVariable("AttackOutcome", "interrupted");
			}

			if (gameCharacter2 == null || gameCharacter2.isDead()) {
				zombie.setTargetSeenTime(10.0F);
			}

			if (gameCharacter2 != null && hashMap.get(0) == Boolean.FALSE && !"started".equals(string) && !StringUtils.isNullOrEmpty(gameCharacter.getVariableString("PlayerHitReaction"))) {
				hashMap.put(0, Boolean.TRUE);
			}

			zombie.setShootable(true);
			if (zombie.target != null && !zombie.bCrawling) {
				if (!"fail".equals(string) && !"interrupted".equals(string)) {
					zombie.faceThisObject(zombie.target);
				}

				zombie.setOnFloor(false);
			}

			if (zombie.target != null) {
				zombie.target.setTimeSinceZombieAttack(0);
				zombie.target.setLastTargettedBy(zombie);
			}

			if (!zombie.bCrawling) {
				zombie.setVariable("AttackType", "bite");
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		gameCharacter.clearVariable("AttackOutcome");
		gameCharacter.clearVariable("AttackType");
		gameCharacter.clearVariable("PlayerHitReaction");
		gameCharacter.setStateMachineLocked(false);
		if (zombie.target != null && zombie.target.isOnFloor()) {
			zombie.setEatBodyTarget(zombie.target, true);
			zombie.setTarget((IsoMovingObject)null);
		}

		zombie.AllowRepathDelay = 0.0F;
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (GameClient.bClient && zombie.isRemoteZombie()) {
			if (animEvent.m_EventName.equalsIgnoreCase("SetAttackOutcome")) {
				zombie.setVariable("AttackOutcome", "fail".equals(this.attackOutcome) ? "fail" : "success");
			}

			if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck") && zombie.target instanceof IsoPlayer) {
				IsoPlayer player = (IsoPlayer)zombie.target;
				if (zombie.scratch) {
					zombie.getEmitter().playSoundImpl("ZombieScratch", zombie);
				} else if (zombie.laceration) {
					zombie.getEmitter().playSoundImpl("ZombieScratch", zombie);
				} else {
					zombie.getEmitter().playSoundImpl("ZombieBite", zombie);
					player.splatBloodFloorBig();
					player.splatBloodFloorBig();
					player.splatBloodFloorBig();
				}
			}

			if (animEvent.m_EventName.equalsIgnoreCase("EatBody")) {
				gameCharacter.setVariable("EatingStarted", true);
				((IsoZombie)gameCharacter).setEatBodyTarget(((IsoZombie)gameCharacter).target, true);
				((IsoZombie)gameCharacter).setTarget((IsoMovingObject)null);
			}
		}

		if (animEvent.m_EventName.equalsIgnoreCase("SetState")) {
			zombie.parameterZombieState.setState(ParameterZombieState.State.Attack);
		}
	}

	public boolean isAttacking(IsoGameCharacter gameCharacter) {
		return true;
	}
}
