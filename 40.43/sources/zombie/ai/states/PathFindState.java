package zombie.ai.states;

import zombie.GameTime;
import zombie.PathfindManager;
import zombie.SandboxOptions;
import zombie.ai.State;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.IPathfinder;
import zombie.ai.astar.Mover;
import zombie.ai.astar.Path;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.vehicles.PathFindState2;


public class PathFindState extends State implements IPathfinder {
	static PathFindState2 _instance = new PathFindState2();
	static Vector2 pathTarget = new Vector2(0.0F, 0.0F);

	public static PathFindState2 instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setPath((Path)null);
		PathfindManager.instance.AddJob(this, gameCharacter, (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), gameCharacter.getPathTargetX(), gameCharacter.getPathTargetY(), gameCharacter.getPathTargetZ());
		gameCharacter.getFinder().progress = AStarPathFinder.PathFindProgress.notyetfound;
		gameCharacter.setPathIndex(0);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoZombie && !((IsoZombie)gameCharacter).bCrawling) {
			gameCharacter.setOnFloor(false);
			((IsoZombie)gameCharacter).setRemoteMoveX(0.0F);
			((IsoZombie)gameCharacter).setRemoteMoveY(0.0F);
			((IsoZombie)gameCharacter).NetRemoteState = 1;
			((IsoZombie)gameCharacter).movex = 0.0F;
			((IsoZombie)gameCharacter).movey = 0.0F;
			if (GameClient.bClient && gameCharacter != null) {
				return;
			}
		}

		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).DoNetworkDirty();
		}

		if (IsoCamera.CamCharacter == gameCharacter) {
			boolean boolean1 = false;
		}

		if (gameCharacter.getFinder().progress != AStarPathFinder.PathFindProgress.notyetfound) {
			if (gameCharacter.getFinder().progress == AStarPathFinder.PathFindProgress.failed) {
				gameCharacter.setPathFindIndex(-1);
				this.Finished(gameCharacter);
			} else {
				if (gameCharacter.getFinder().progress == AStarPathFinder.PathFindProgress.found) {
					Path path = gameCharacter.getPath();
					if (path == null) {
						gameCharacter.getStateMachine().RevertToPrevious();
						return;
					}

					float float1 = gameCharacter.getX();
					float float2 = gameCharacter.getY();
					float float3 = gameCharacter.getZ();
					if (gameCharacter.getPathIndex() >= path.getLength()) {
						path = null;
						this.Finished(gameCharacter);
						return;
					}

					int int1 = path.getX(gameCharacter.getPathIndex());
					int int2 = path.getY(gameCharacter.getPathIndex());
					int int3 = path.getZ(gameCharacter.getPathIndex());
					float float4 = 1.0F;
					float float5 = gameCharacter.getPathSpeed();
					int int4 = gameCharacter.getPathIndex();
					if (path != null) {
						boolean boolean2 = false;
						Vector2 vector2;
						float float6;
						float float7;
						if ((int)float1 == int1 && (int)float2 == int2 && (int)float3 == int3) {
							float6 = (float)path.getX(int4) + 0.5F;
							float7 = (float)path.getY(int4) + 0.5F;
							pathTarget.x = float6;
							pathTarget.y = float7;
							gameCharacter.angle.x = pathTarget.x;
							gameCharacter.angle.y = pathTarget.y;
							gameCharacter.angle.normalize();
							if (int4 < path.getLength() - 1) {
								int int5 = path.getX(int4 + 1);
								int int6 = path.getY(int4 + 1);
								IsoWorld.instance.CurrentCell.getGridSquare(int5, int6, (int)gameCharacter.getZ());
							}

							if (gameCharacter instanceof IsoZombie) {
								if (gameCharacter.getPathTargetX() == (int)gameCharacter.getX() && gameCharacter.getPathTargetY() == (int)gameCharacter.getY() && gameCharacter.getPathTargetZ() == (int)gameCharacter.getZ()) {
									gameCharacter.getStateMachine().changeState(ZombieStandState.instance());
									return;
								}
							} else if (gameCharacter.getPathTargetX() == (int)gameCharacter.getX() && gameCharacter.getPathTargetY() == (int)gameCharacter.getY() && gameCharacter.getPathTargetZ() == (int)gameCharacter.getZ()) {
								gameCharacter.getStateMachine().changeState(IdleState.instance());
								return;
							}

							if (IsoUtils.DistanceManhatten(float1, float2, float6, float7) < float5 * 6.2F) {
								gameCharacter.setPathIndex(gameCharacter.getPathIndex() + 1);
								if (gameCharacter.getPathIndex() >= path.getLength()) {
									path = null;
									this.Finished(gameCharacter);
									return;
								}

								if (GameTime.instance.getMultiplier() >= 10.0F) {
									gameCharacter.setX((float)path.getX(gameCharacter.getPathIndex()) + 0.5F);
									gameCharacter.setY((float)path.getY(gameCharacter.getPathIndex()) + 0.5F);
								} else {
									gameCharacter.setX((float)int1 + 0.5F);
									gameCharacter.setY((float)int2 + 0.5F);
								}
							}

							vector2 = pathTarget;
							vector2.x -= float1;
							vector2 = pathTarget;
							vector2.y -= float2;
							if (pathTarget.getLength() > 0.0F) {
								pathTarget.normalize();
								gameCharacter.DirectionFromVector(pathTarget);
							}

							if (gameCharacter instanceof IsoZombie) {
								((IsoZombie)gameCharacter).bRunning = false;
								if (SandboxOptions.instance.Lore.Speed.getValue() == 1 && ((IsoZombie)gameCharacter).speedType != 3 || ((IsoZombie)gameCharacter).speedType == 1) {
									float5 = 0.08F;
									((IsoZombie)gameCharacter).bRunning = true;
								}

								((IsoZombie)gameCharacter).setIgnoreMovementForDirection(false);
							}

							gameCharacter.MoveForward(float5, pathTarget.x, pathTarget.y, float4);
							gameCharacter.angle.x = pathTarget.x;
							gameCharacter.angle.y = pathTarget.y;
							gameCharacter.angle.normalize();
							if (gameCharacter instanceof IsoZombie) {
								((IsoZombie)gameCharacter).updateFrameSpeed();
							}

							if (gameCharacter instanceof IsoZombie) {
								((IsoZombie)gameCharacter).reqMovement.x = gameCharacter.angle.x;
								((IsoZombie)gameCharacter).reqMovement.y = gameCharacter.angle.y;
							}
						} else {
							float6 = (float)int1 + 0.5F;
							float7 = (float)int2 + 0.5F;
							pathTarget.x = float6;
							pathTarget.y = float7;
							vector2 = pathTarget;
							vector2.x -= float1;
							vector2 = pathTarget;
							vector2.y -= float2;
							if (pathTarget.getLength() > 0.0F) {
								pathTarget.normalize();
							}

							gameCharacter.DirectionFromVector(pathTarget);
							if (gameCharacter instanceof IsoZombie) {
								((IsoZombie)gameCharacter).bRunning = false;
								if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
									float5 = 0.08F;
									((IsoZombie)gameCharacter).bRunning = true;
								}
							}

							gameCharacter.MoveForward(float5, pathTarget.x, pathTarget.y, float4);
							gameCharacter.angle.x = pathTarget.x;
							gameCharacter.angle.y = pathTarget.y;
							gameCharacter.angle.normalize();
							if (gameCharacter instanceof IsoZombie) {
								((IsoZombie)gameCharacter).reqMovement.x = gameCharacter.angle.x;
								((IsoZombie)gameCharacter).reqMovement.y = gameCharacter.angle.y;
							}
						}
					}
				}
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}

	private void Finished(IsoGameCharacter gameCharacter) {
		gameCharacter.pathFinished();
	}

	public void Failed(Mover mover) {
		IsoGameCharacter gameCharacter = (IsoGameCharacter)mover;
		gameCharacter.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
	}

	public void Succeeded(Path path, Mover mover) {
		IsoGameCharacter gameCharacter = (IsoGameCharacter)mover;
		gameCharacter.setPathIndex(0);
		Path path2 = gameCharacter.getPath();
		if (path2 != null) {
			for (int int1 = 0; int1 < path2.getLength(); ++int1) {
				Path.stepstore.push(path2.getStep(int1));
			}
		}

		gameCharacter.setPath(path);
		gameCharacter.getFinder().progress = AStarPathFinder.PathFindProgress.found;
	}

	public String getName() {
		return "ZombiePathfinding";
	}
}
