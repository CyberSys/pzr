package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.gameStates.IngameState;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.NetworkVariables;
import zombie.network.ServerMap;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;


public class WalkTowardNetworkState extends State {
	static WalkTowardNetworkState _instance = new WalkTowardNetworkState();
	private static final Integer PARAM_TICK_COUNT = 2;

	public static WalkTowardNetworkState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.put(PARAM_TICK_COUNT, IngameState.instance.numberTicks);
		gameCharacter.setVariable("bMoving", true);
		gameCharacter.setVariable("bPathfind", false);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		PathFindBehavior2 pathFindBehavior2 = zombie.getPathFindBehavior2();
		zombie.vectorToTarget.x = zombie.networkAI.targetX - zombie.x;
		zombie.vectorToTarget.y = zombie.networkAI.targetY - zombie.y;
		pathFindBehavior2.walkingOnTheSpot.reset(zombie.x, zombie.y);
		if (zombie.z != (float)zombie.networkAI.targetZ || zombie.networkAI.predictionType != NetworkVariables.PredictionTypes.Thump && zombie.networkAI.predictionType != NetworkVariables.PredictionTypes.Climb) {
			if (zombie.z == (float)zombie.networkAI.targetZ && !PolygonalMap2.instance.lineClearCollide(zombie.x, zombie.y, zombie.networkAI.targetX, zombie.networkAI.targetY, zombie.networkAI.targetZ, (IsoMovingObject)null)) {
				if (zombie.networkAI.usePathFind) {
					pathFindBehavior2.reset();
					zombie.setPath2((PolygonalMap2.Path)null);
					zombie.networkAI.usePathFind = false;
				}

				pathFindBehavior2.moveToPoint(zombie.networkAI.targetX, zombie.networkAI.targetY, 1.0F);
				zombie.setVariable("bMoving", IsoUtils.DistanceManhatten(zombie.networkAI.targetX, zombie.networkAI.targetY, zombie.nx, zombie.ny) > 0.5F);
			} else {
				if (!zombie.networkAI.usePathFind) {
					pathFindBehavior2.pathToLocationF(zombie.networkAI.targetX, zombie.networkAI.targetY, (float)zombie.networkAI.targetZ);
					pathFindBehavior2.walkingOnTheSpot.reset(zombie.x, zombie.y);
					zombie.networkAI.usePathFind = true;
				}

				PathFindBehavior2.BehaviorResult behaviorResult = pathFindBehavior2.update();
				if (behaviorResult == PathFindBehavior2.BehaviorResult.Failed) {
					zombie.setPathFindIndex(-1);
					return;
				}

				if (behaviorResult == PathFindBehavior2.BehaviorResult.Succeeded) {
					int int1 = (int)zombie.getPathFindBehavior2().getTargetX();
					int int2 = (int)zombie.getPathFindBehavior2().getTargetY();
					IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int1 / 10, int2 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int1, int2, 0);
					if (chunk == null) {
						zombie.setVariable("bMoving", true);
						return;
					}

					zombie.setPath2((PolygonalMap2.Path)null);
					zombie.setVariable("bMoving", true);
					return;
				}
			}
		} else {
			if (zombie.networkAI.usePathFind) {
				pathFindBehavior2.reset();
				zombie.setPath2((PolygonalMap2.Path)null);
				zombie.networkAI.usePathFind = false;
			}

			pathFindBehavior2.moveToPoint(zombie.networkAI.targetX, zombie.networkAI.targetY, 1.0F);
			zombie.setVariable("bMoving", IsoUtils.DistanceManhatten(zombie.networkAI.targetX, zombie.networkAI.targetY, zombie.nx, zombie.ny) > 0.5F);
		}

		if (!((IsoZombie)gameCharacter).bCrawling) {
			gameCharacter.setOnFloor(false);
		}

		boolean boolean1 = gameCharacter.isCollidedWithVehicle();
		if (zombie.target instanceof IsoGameCharacter && ((IsoGameCharacter)zombie.target).getVehicle() != null && ((IsoGameCharacter)zombie.target).getVehicle().isCharacterAdjacentTo(gameCharacter)) {
			boolean1 = false;
		}

		if (gameCharacter.isCollidedThisFrame() || boolean1) {
			zombie.AllowRepathDelay = 0.0F;
			zombie.pathToLocation(gameCharacter.getPathTargetX(), gameCharacter.getPathTargetY(), gameCharacter.getPathTargetZ());
			if (!"true".equals(zombie.getVariableString("bPathfind"))) {
				zombie.setVariable("bPathfind", true);
				zombie.setVariable("bMoving", false);
			}
		}

		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		long long1 = (Long)hashMap.get(PARAM_TICK_COUNT);
		if (IngameState.instance.numberTicks - long1 == 2L) {
			zombie.parameterZombieState.setState(ParameterZombieState.State.Idle);
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setVariable("bMoving", false);
	}
}
