package zombie.vehicles;

import zombie.ai.State;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.states.WalkTowardState;
import zombie.behaviors.Behavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.iso.IsoChunk;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.ServerMap;


public class PathFindState2 extends State {
	private static final PathFindState2 _instance = new PathFindState2();

	public static PathFindState2 instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
	}

	public void execute(IsoGameCharacter gameCharacter) {
		Behavior.BehaviorResult behaviorResult = gameCharacter.getPathFindBehavior2().update();
		if (behaviorResult == Behavior.BehaviorResult.Failed) {
			if (gameCharacter instanceof IsoZombie) {
				((IsoZombie)gameCharacter).AllowRepathDelay = 0.0F;
			}

			gameCharacter.setPathFindIndex(-1);
			gameCharacter.setDefaultState();
		} else if (behaviorResult == Behavior.BehaviorResult.Succeeded) {
			if (gameCharacter instanceof IsoZombie) {
				((IsoZombie)gameCharacter).AllowRepathDelay = 0.0F;
			}

			int int1 = (int)gameCharacter.getPathFindBehavior2().getTargetX();
			int int2 = (int)gameCharacter.getPathFindBehavior2().getTargetY();
			IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int1 / 10, int2 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int1, int2, 0);
			if (chunk == null) {
				gameCharacter.changeState(WalkTowardState.instance());
			} else {
				gameCharacter.setDefaultState();
				gameCharacter.setPath2((PolygonalMap2.Path)null);
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		if (gameCharacter.getFinder().progress == AStarPathFinder.PathFindProgress.notyetfound) {
			PolygonalMap2.instance.cancelRequest(gameCharacter);
			gameCharacter.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
		}

		gameCharacter.setPath2((PolygonalMap2.Path)null);
	}
}
