package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;


public final class ZombieGetDownState extends State {
	private static final ZombieGetDownState _instance = new ZombieGetDownState();
	static final Integer PARAM_PREV_STATE = 1;
	static final Integer PARAM_WAIT_TIME = 2;
	static final Integer PARAM_START_X = 3;
	static final Integer PARAM_START_Y = 4;

	public static ZombieGetDownState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.put(PARAM_PREV_STATE, gameCharacter.getStateMachine().getPrevious());
		hashMap.put(PARAM_START_X, gameCharacter.getX());
		hashMap.put(PARAM_START_Y, gameCharacter.getY());
		gameCharacter.setStateEventDelayTimer((Float)hashMap.get(PARAM_WAIT_TIME));
	}

	public void execute(IsoGameCharacter gameCharacter) {
		gameCharacter.getStateMachineParams(this);
	}

	public void exit(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoZombie zombie = (IsoZombie)gameCharacter;
		zombie.setStateEventDelayTimer(0.0F);
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
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (animEvent.m_EventName.equalsIgnoreCase("StartCrawling") && !zombie.isCrawling()) {
			zombie.toggleCrawling();
		}
	}

	public boolean isNearStartXY(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		Float Float1 = (Float)hashMap.get(PARAM_START_X);
		Float Float2 = (Float)hashMap.get(PARAM_START_Y);
		if (Float1 != null && Float2 != null) {
			return gameCharacter.DistToSquared(Float1, Float2) <= 0.25F;
		} else {
			return false;
		}
	}

	public void setParams(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.put(PARAM_WAIT_TIME, Rand.Next(60.0F, 150.0F));
	}
}
