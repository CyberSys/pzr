package zombie.ai.states;

import java.util.HashMap;
import org.joml.Vector3f;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.gameStates.IngameState;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.util.Type;
import zombie.vehicles.PolygonalMap2;


public final class WalkTowardState extends State {
	private static final WalkTowardState _instance = new WalkTowardState();
	private static final Integer PARAM_IGNORE_OFFSET = 0;
	private static final Integer PARAM_IGNORE_TIME = 1;
	private static final Integer PARAM_TICK_COUNT = 2;
	private final Vector2 temp = new Vector2();
	private final Vector3f worldPos = new Vector3f();

	public static WalkTowardState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (hashMap.get(PARAM_IGNORE_OFFSET) == null) {
			hashMap.put(PARAM_IGNORE_OFFSET, Boolean.FALSE);
			hashMap.put(PARAM_IGNORE_TIME, 0L);
		}

		if (hashMap.get(PARAM_IGNORE_OFFSET) == Boolean.TRUE && System.currentTimeMillis() - (Long)hashMap.get(PARAM_IGNORE_TIME) > 3000L) {
			hashMap.put(PARAM_IGNORE_OFFSET, Boolean.FALSE);
			hashMap.put(PARAM_IGNORE_TIME, 0L);
		}

		hashMap.put(PARAM_TICK_COUNT, IngameState.instance.numberTicks);
		if (((IsoZombie)gameCharacter).isUseless()) {
			gameCharacter.changeState(ZombieIdleState.instance());
		}

		gameCharacter.getPathFindBehavior2().walkingOnTheSpot.reset(gameCharacter.x, gameCharacter.y);
		((IsoZombie)gameCharacter).networkAI.extraUpdate();
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (!zombie.bCrawling) {
			gameCharacter.setOnFloor(false);
		}

		IsoGameCharacter gameCharacter2 = (IsoGameCharacter)Type.tryCastTo(zombie.target, IsoGameCharacter.class);
		if (zombie.target != null) {
			if (zombie.isTargetLocationKnown()) {
				if (gameCharacter2 != null) {
					zombie.getPathFindBehavior2().pathToCharacter(gameCharacter2);
					if (gameCharacter2.getVehicle() != null && zombie.DistToSquared(zombie.target) < 16.0F) {
						Vector3f vector3f = gameCharacter2.getVehicle().chooseBestAttackPosition(gameCharacter2, zombie, this.worldPos);
						if (vector3f == null) {
							zombie.setVariable("bMoving", false);
							return;
						}

						if (Math.abs(gameCharacter.x - zombie.getPathFindBehavior2().getTargetX()) > 0.1F || Math.abs(gameCharacter.y - zombie.getPathFindBehavior2().getTargetY()) > 0.1F) {
							zombie.setVariable("bPathfind", true);
							zombie.setVariable("bMoving", false);
							return;
						}
					}
				}
			} else if (zombie.LastTargetSeenX != -1 && !gameCharacter.getPathFindBehavior2().isTargetLocation((float)zombie.LastTargetSeenX + 0.5F, (float)zombie.LastTargetSeenY + 0.5F, (float)zombie.LastTargetSeenZ)) {
				gameCharacter.pathToLocation(zombie.LastTargetSeenX, zombie.LastTargetSeenY, zombie.LastTargetSeenZ);
			}
		}

		if (gameCharacter.getPathTargetX() == (int)gameCharacter.getX() && gameCharacter.getPathTargetY() == (int)gameCharacter.getY()) {
			if (zombie.target == null) {
				zombie.setVariable("bPathfind", false);
				zombie.setVariable("bMoving", false);
				return;
			}

			if ((int)zombie.target.getZ() != (int)gameCharacter.getZ()) {
				zombie.setVariable("bPathfind", true);
				zombie.setVariable("bMoving", false);
				return;
			}
		}

		boolean boolean1 = gameCharacter.isCollidedWithVehicle();
		if (gameCharacter2 != null && gameCharacter2.getVehicle() != null && gameCharacter2.getVehicle().isCharacterAdjacentTo(gameCharacter)) {
			boolean1 = false;
		}

		boolean boolean2 = gameCharacter.isCollidedThisFrame();
		float float1;
		float float2;
		float float3;
		if (boolean2 && hashMap.get(PARAM_IGNORE_OFFSET) == Boolean.FALSE) {
			hashMap.put(PARAM_IGNORE_OFFSET, Boolean.TRUE);
			hashMap.put(PARAM_IGNORE_TIME, System.currentTimeMillis());
			float1 = zombie.getPathFindBehavior2().getTargetX();
			float2 = zombie.getPathFindBehavior2().getTargetY();
			float3 = zombie.z;
			boolean2 = !this.isPathClear(gameCharacter, float1, float2, float3);
		}

		if (!boolean2 && !boolean1) {
			this.temp.x = zombie.getPathFindBehavior2().getTargetX();
			this.temp.y = zombie.getPathFindBehavior2().getTargetY();
			Vector2 vector2 = this.temp;
			vector2.x -= zombie.getX();
			vector2 = this.temp;
			vector2.y -= zombie.getY();
			float1 = this.temp.getLength();
			if (float1 < 0.25F) {
				gameCharacter.x = zombie.getPathFindBehavior2().getTargetX();
				gameCharacter.y = zombie.getPathFindBehavior2().getTargetY();
				gameCharacter.nx = gameCharacter.x;
				gameCharacter.ny = gameCharacter.y;
				float1 = 0.0F;
			}

			if (float1 < 0.025F) {
				zombie.setVariable("bPathfind", false);
				zombie.setVariable("bMoving", false);
			} else {
				if (!GameServer.bServer && !zombie.bCrawling && hashMap.get(PARAM_IGNORE_OFFSET) == Boolean.FALSE) {
					float2 = Math.min(float1 / 2.0F, 4.0F);
					float3 = (float)((gameCharacter.getID() + zombie.ZombieID) % 20) / 10.0F - 1.0F;
					float float4 = (float)((zombie.getID() + zombie.ZombieID) % 20) / 10.0F - 1.0F;
					vector2 = this.temp;
					vector2.x += zombie.getX();
					vector2 = this.temp;
					vector2.y += zombie.getY();
					vector2 = this.temp;
					vector2.x += float3 * float2;
					vector2 = this.temp;
					vector2.y += float4 * float2;
					vector2 = this.temp;
					vector2.x -= zombie.getX();
					vector2 = this.temp;
					vector2.y -= zombie.getY();
				}

				zombie.bRunning = false;
				this.temp.normalize();
				if (zombie.bCrawling) {
					if (zombie.getVariableString("TurnDirection").isEmpty()) {
						zombie.setForwardDirection(this.temp);
					}
				} else {
					zombie.setDir(IsoDirections.fromAngle(this.temp));
					zombie.setForwardDirection(this.temp);
				}

				if (gameCharacter.getPathFindBehavior2().walkingOnTheSpot.check(gameCharacter.x, gameCharacter.y)) {
					gameCharacter.setVariable("bMoving", false);
				}

				long long1 = (Long)hashMap.get(PARAM_TICK_COUNT);
				if (IngameState.instance.numberTicks - long1 == 2L) {
					zombie.parameterZombieState.setState(ParameterZombieState.State.Idle);
				}
			}
		} else {
			zombie.AllowRepathDelay = 0.0F;
			zombie.pathToLocation(gameCharacter.getPathTargetX(), gameCharacter.getPathTargetY(), gameCharacter.getPathTargetZ());
			if (!zombie.getVariableBoolean("bPathfind")) {
				zombie.setVariable("bPathfind", true);
				zombie.setVariable("bMoving", false);
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setVariable("bMoving", false);
		((IsoZombie)gameCharacter).networkAI.extraUpdate();
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
	}

	public boolean isMoving(IsoGameCharacter gameCharacter) {
		return true;
	}

	private boolean isPathClear(IsoGameCharacter gameCharacter, float float1, float float2, float float3) {
		int int1 = (int)float1 / 10;
		int int2 = (int)float2 / 10;
		IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int1, int2) : IsoWorld.instance.CurrentCell.getChunkForGridSquare((int)float1, (int)float2, (int)float3);
		if (chunk != null) {
			byte byte1 = 1;
			int int3 = byte1 | 2;
			return !PolygonalMap2.instance.lineClearCollide(gameCharacter.getX(), gameCharacter.getY(), float1, float2, (int)float3, gameCharacter.getPathFindBehavior2().getTargetChar(), int3);
		} else {
			return false;
		}
	}

	public boolean calculateTargetLocation(IsoZombie zombie, Vector2 vector2) {
		assert zombie.isCurrentState(this);
		HashMap hashMap = zombie.getStateMachineParams(this);
		vector2.x = zombie.getPathFindBehavior2().getTargetX();
		vector2.y = zombie.getPathFindBehavior2().getTargetY();
		this.temp.set(vector2);
		Vector2 vector22 = this.temp;
		vector22.x -= zombie.getX();
		vector22 = this.temp;
		vector22.y -= zombie.getY();
		float float1 = this.temp.getLength();
		if (float1 < 0.025F) {
			return false;
		} else if (!GameServer.bServer && !zombie.bCrawling && hashMap.get(PARAM_IGNORE_OFFSET) == Boolean.FALSE) {
			float float2 = Math.min(float1 / 2.0F, 4.0F);
			float float3 = (float)((zombie.getID() + zombie.ZombieID) % 20) / 10.0F - 1.0F;
			float float4 = (float)((zombie.getID() + zombie.ZombieID) % 20) / 10.0F - 1.0F;
			vector2.x += float3 * float2;
			vector2.y += float4 * float2;
			return true;
		} else {
			return false;
		}
	}
}
