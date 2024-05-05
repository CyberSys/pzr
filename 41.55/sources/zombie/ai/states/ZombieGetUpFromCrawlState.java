package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;


public final class ZombieGetUpFromCrawlState extends State {
	private static final ZombieGetUpFromCrawlState _instance = new ZombieGetUpFromCrawlState();

	public static ZombieGetUpFromCrawlState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoZombie zombie = (IsoZombie)gameCharacter;
		hashMap.put(1, gameCharacter.getStateMachine().getPrevious());
		if (zombie.isCrawling()) {
			zombie.toggleCrawling();
			zombie.setOnFloor(true);
			zombie.setFallOnFront(true);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoZombie zombie = (IsoZombie)gameCharacter;
		zombie.AllowRepathDelay = 0.0F;
		if (hashMap.get(1) == PathFindState.instance()) {
			if (gameCharacter.getPathFindBehavior2().getTargetChar() == null) {
				gameCharacter.setVariable("bPathfind", true);
				gameCharacter.setVariable("bMoving", false);
			} else if (zombie.isTargetLocationKnown()) {
				gameCharacter.pathToCharacter(gameCharacter.getPathFindBehavior2().getTargetChar());
			} else if (zombie.LastTargetSeenX != -1) {
				gameCharacter.pathToLocation(zombie.LastTargetSeenX, zombie.LastTargetSeenY, zombie.LastTargetSeenZ);
			}
		}
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
	}
}
