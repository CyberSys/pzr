package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.util.StringUtils;


public final class ZombieGetUpState extends State {
	private static final ZombieGetUpState _instance = new ZombieGetUpState();
	static final Integer PARAM_STANDING = 1;
	static final Integer PARAM_PREV_STATE = 2;

	public static ZombieGetUpState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.put(PARAM_STANDING, Boolean.FALSE);
		State state = gameCharacter.getStateMachine().getPrevious();
		if (state == ZombieGetUpFromCrawlState.instance()) {
			state = (State)gameCharacter.getStateMachineParams(ZombieGetUpFromCrawlState.instance()).get(1);
		}

		hashMap.put(PARAM_PREV_STATE, state);
		zombie.parameterZombieState.setState(ParameterZombieState.State.GettingUp);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		boolean boolean1 = hashMap.get(PARAM_STANDING) == Boolean.TRUE;
		gameCharacter.setOnFloor(!boolean1);
		((IsoZombie)gameCharacter).bKnockedDown = !boolean1;
	}

	public void exit(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoZombie zombie = (IsoZombie)gameCharacter;
		gameCharacter.setCollidable(true);
		gameCharacter.setOnFloor(false);
		gameCharacter.setFallOnFront(false);
		gameCharacter.clearVariable("SprinterTripped");
		gameCharacter.clearVariable("ShouldStandUp");
		if (StringUtils.isNullOrEmpty(gameCharacter.getHitReaction())) {
			zombie.setSitAgainstWall(false);
		}

		zombie.bKnockedDown = false;
		zombie.AllowRepathDelay = 0.0F;
		if (hashMap.get(PARAM_PREV_STATE) == PathFindState.instance()) {
			if (gameCharacter.getPathFindBehavior2().getTargetChar() == null) {
				gameCharacter.setVariable("bPathfind", true);
				gameCharacter.setVariable("bMoving", false);
			} else if (zombie.isTargetLocationKnown()) {
				gameCharacter.pathToCharacter(gameCharacter.getPathFindBehavior2().getTargetChar());
			} else if (zombie.LastTargetSeenX != -1) {
				gameCharacter.pathToLocation(zombie.LastTargetSeenX, zombie.LastTargetSeenY, zombie.LastTargetSeenZ);
			}
		} else if (hashMap.get(PARAM_PREV_STATE) == WalkTowardState.instance()) {
			gameCharacter.setVariable("bPathFind", false);
			gameCharacter.setVariable("bMoving", true);
		}
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (animEvent.m_EventName.equalsIgnoreCase("IsAlmostUp")) {
			hashMap.put(1, Boolean.TRUE);
		}
	}
}
