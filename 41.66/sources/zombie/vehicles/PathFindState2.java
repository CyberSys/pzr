package zombie.vehicles;

import java.util.HashMap;
import zombie.ai.State;
import zombie.ai.astar.AStarPathFinder;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.gameStates.IngameState;
import zombie.iso.IsoChunk;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.ServerMap;


public final class PathFindState2 extends State {
	private static final Integer PARAM_TICK_COUNT = 0;

	public void enter(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		gameCharacter.setVariable("bPathfind", true);
		gameCharacter.setVariable("bMoving", false);
		((IsoZombie)gameCharacter).networkAI.extraUpdate();
		hashMap.put(PARAM_TICK_COUNT, IngameState.instance.numberTicks);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		PathFindBehavior2.BehaviorResult behaviorResult = gameCharacter.getPathFindBehavior2().update();
		if (behaviorResult == PathFindBehavior2.BehaviorResult.Failed) {
			gameCharacter.setPathFindIndex(-1);
			gameCharacter.setVariable("bPathfind", false);
			gameCharacter.setVariable("bMoving", false);
		} else if (behaviorResult == PathFindBehavior2.BehaviorResult.Succeeded) {
			int int1 = (int)gameCharacter.getPathFindBehavior2().getTargetX();
			int int2 = (int)gameCharacter.getPathFindBehavior2().getTargetY();
			IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int1 / 10, int2 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int1, int2, 0);
			if (chunk == null) {
				gameCharacter.setVariable("bPathfind", false);
				gameCharacter.setVariable("bMoving", true);
			} else {
				gameCharacter.setVariable("bPathfind", false);
				gameCharacter.setVariable("bMoving", false);
				gameCharacter.setPath2((PolygonalMap2.Path)null);
			}
		} else {
			if (gameCharacter instanceof IsoZombie) {
				long long1 = (Long)hashMap.get(PARAM_TICK_COUNT);
				if (IngameState.instance.numberTicks - long1 == 2L) {
					((IsoZombie)gameCharacter).parameterZombieState.setState(ParameterZombieState.State.Idle);
				}
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).networkAI.extraUpdate();
			((IsoZombie)gameCharacter).AllowRepathDelay = 0.0F;
		}

		gameCharacter.setVariable("bPathfind", false);
		gameCharacter.setVariable("bMoving", false);
		gameCharacter.setVariable("ShouldBeCrawling", false);
		PolygonalMap2.instance.cancelRequest(gameCharacter);
		gameCharacter.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
		gameCharacter.setPath2((PolygonalMap2.Path)null);
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
	}

	public boolean isMoving(IsoGameCharacter gameCharacter) {
		return gameCharacter.isMoving();
	}
}
