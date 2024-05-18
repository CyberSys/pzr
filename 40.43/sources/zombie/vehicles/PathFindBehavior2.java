package zombie.vehicles;

import org.joml.Vector3f;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.ai.State;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.Mover;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverFenceState2;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbThroughWindowState2;
import zombie.ai.states.WalkTowardState;
import zombie.behaviors.Behavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.debug.LineDrawer;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.scripting.objects.VehicleScript;


public class PathFindBehavior2 implements PolygonalMap2.IPathfinder {
	private static Vector2 tempVector2 = new Vector2();
	private static PathFindBehavior2.PointOnPath pointOnPath = new PathFindBehavior2.PointOnPath();
	private IsoGameCharacter chr;
	private float startX;
	private float startY;
	private float startZ;
	private float targetX;
	private float targetY;
	private float targetZ;
	private final PolygonalMap2.Path path = new PolygonalMap2.Path();
	private int pathIndex;
	private boolean isCancel = true;
	private PathFindBehavior2.Goal goal;
	private IsoGameCharacter goalCharacter;
	private BaseVehicle goalVehicle;
	private String goalVehicleArea;
	private int goalVehicleSeat;

	public PathFindBehavior2(IsoGameCharacter gameCharacter) {
		this.goal = PathFindBehavior2.Goal.None;
		this.chr = gameCharacter;
	}

	public boolean isGoal2Location() {
		return this.goal == PathFindBehavior2.Goal.Location;
	}

	public void pathToCharacter(IsoGameCharacter gameCharacter) {
		this.isCancel = false;
		this.goal = PathFindBehavior2.Goal.Character;
		this.goalCharacter = gameCharacter;
		if (gameCharacter.getVehicle() != null) {
			Vector3f vector3f = gameCharacter.getVehicle().chooseBestAttackPosition(gameCharacter, this.chr);
			if (vector3f != null) {
				this.setData(vector3f.x, vector3f.y, vector3f.z);
				return;
			}
		}

		this.setData(gameCharacter.getX(), gameCharacter.getY(), gameCharacter.getZ());
	}

	public void pathToLocation(int int1, int int2, int int3) {
		this.isCancel = false;
		this.goal = PathFindBehavior2.Goal.Location;
		this.setData((float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3);
	}

	public void pathToLocationF(float float1, float float2, float float3) {
		this.isCancel = false;
		this.goal = PathFindBehavior2.Goal.Location;
		this.setData(float1, float2, float3);
	}

	public void pathToVehicleArea(BaseVehicle baseVehicle, String string) {
		Vector2 vector2 = baseVehicle.getAreaCenter(string);
		if (vector2 == null) {
			this.targetX = this.chr.getX();
			this.targetY = this.chr.getY();
			this.targetZ = this.chr.getZ();
			this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
		} else {
			this.isCancel = false;
			this.goal = PathFindBehavior2.Goal.VehicleArea;
			this.goalVehicle = baseVehicle;
			this.goalVehicleArea = string;
			this.setData(vector2.getX(), vector2.getY(), (float)((int)baseVehicle.getZ()));
			if (this.chr instanceof IsoPlayer && (int)this.chr.z == (int)this.targetZ && !PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, this.targetX, this.targetY, (int)this.targetZ, (IsoMovingObject)null)) {
				this.path.clear();
				this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
				this.path.addNode(this.targetX, this.targetY, this.targetZ);
				this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
			}
		}
	}

	public void pathToVehicleSeat(BaseVehicle baseVehicle, int int1) {
		VehicleScript.Position position = baseVehicle.getPassengerPosition(int1, "outside2");
		Vector3f vector3f;
		VehicleScript.Area area;
		Vector2 vector2;
		if (position != null) {
			vector3f = new Vector3f();
			if (position.area == null) {
				vector3f = baseVehicle.getWorldPos(position.offset, vector3f);
			} else {
				area = baseVehicle.script.getAreaById(position.area);
				vector2 = baseVehicle.areaPositionWorld(area);
				vector3f.x = vector2.x;
				vector3f.y = vector2.y;
				vector3f.z = 0.0F;
			}

			vector3f.sub(this.chr.x, this.chr.y, this.chr.z);
			if (vector3f.length() < 2.0F) {
				vector3f = baseVehicle.getWorldPos(position.offset, new Vector3f());
				this.setData(vector3f.x(), vector3f.y(), (float)((int)vector3f.z()));
				if (this.chr instanceof IsoPlayer && (int)this.chr.z == (int)this.targetZ) {
					this.path.clear();
					this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
					this.path.addNode(this.targetX, this.targetY, this.targetZ);
					this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
					return;
				}
			}
		}

		position = baseVehicle.getPassengerPosition(int1, "outside");
		if (position == null) {
			VehiclePart vehiclePart = baseVehicle.getPassengerDoor(int1);
			if (vehiclePart == null) {
				this.targetX = this.chr.getX();
				this.targetY = this.chr.getY();
				this.targetZ = this.chr.getZ();
				this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
			} else {
				this.pathToVehicleArea(baseVehicle, vehiclePart.getArea());
			}
		} else {
			this.isCancel = false;
			this.goal = PathFindBehavior2.Goal.VehicleSeat;
			this.goalVehicle = baseVehicle;
			vector3f = new Vector3f();
			if (position.area == null) {
				vector3f = baseVehicle.getWorldPos(position.offset, vector3f);
			} else {
				area = baseVehicle.script.getAreaById(position.area);
				vector2 = baseVehicle.areaPositionWorld(area);
				vector3f.x = vector2.x;
				vector3f.y = vector2.y;
				vector3f.z = 0.0F;
			}

			this.setData(vector3f.x(), vector3f.y(), (float)((int)vector3f.z()));
			if (this.chr instanceof IsoPlayer && (int)this.chr.z == (int)this.targetZ && !PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, this.targetX, this.targetY, (int)this.targetZ, (IsoMovingObject)null)) {
				this.path.clear();
				this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
				this.path.addNode(this.targetX, this.targetY, this.targetZ);
				this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
			}
		}
	}

	public void cancel() {
		this.isCancel = true;
	}

	private void setData(float float1, float float2, float float3) {
		this.startX = this.chr.getX();
		this.startY = this.chr.getY();
		this.startZ = this.chr.getZ();
		this.targetX = float1;
		this.targetY = float2;
		this.targetZ = float3;
		this.pathIndex = 0;
		this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
	}

	public float getTargetX() {
		return this.targetX;
	}

	public float getTargetY() {
		return this.targetY;
	}

	public float getTargetZ() {
		return this.targetZ;
	}

	public IsoGameCharacter getTargetChar() {
		return this.goal == PathFindBehavior2.Goal.Character ? this.goalCharacter : null;
	}

	public boolean isTargetLocation(float float1, float float2, float float3) {
		return this.goal == PathFindBehavior2.Goal.Location && float1 == this.targetX && float2 == this.targetY && (int)float3 == (int)this.targetZ;
	}

	public Behavior.BehaviorResult update() {
		if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.notrunning) {
			PolygonalMap2.instance.addRequest(this, this.chr, this.startX, this.startY, this.startZ, this.targetX, this.targetY, this.targetZ);
			this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.notyetfound;
			return Behavior.BehaviorResult.Working;
		} else if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.notyetfound) {
			return Behavior.BehaviorResult.Working;
		} else if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.failed) {
			return Behavior.BehaviorResult.Failed;
		} else {
			State state = this.chr.getCurrentState();
			if (state != ClimbOverFenceState.instance() && state != ClimbOverFenceState2.instance() && state != ClimbThroughWindowState.instance() && state != ClimbThroughWindowState2.instance()) {
				if (this.chr.getVehicle() != null) {
					return Behavior.BehaviorResult.Failed;
				} else {
					this.chr.setPath2(this.path);
					if (this.goal == PathFindBehavior2.Goal.Character && this.chr instanceof IsoZombie && this.goalCharacter != null && this.goalCharacter.getVehicle() != null && this.chr.DistToSquared(this.targetX, this.targetY) < 16.0F) {
						Vector3f vector3f = this.goalCharacter.getVehicle().chooseBestAttackPosition(this.goalCharacter, this.chr);
						if (vector3f != null && (Math.abs(vector3f.x - this.targetX) > 0.1F || Math.abs(vector3f.y - this.targetY) > 0.1F)) {
							if (Math.abs(this.goalCharacter.getVehicle().getCurrentSpeedKmHour()) > 0.1F) {
								if (!PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, vector3f.x, vector3f.y, (int)this.targetZ, this.goalCharacter)) {
									this.path.clear();
									this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
									this.path.addNode(vector3f.x, vector3f.y, vector3f.z);
								} else if (IsoUtils.DistanceToSquared(vector3f.x, vector3f.y, this.targetX, this.targetY) > IsoUtils.DistanceToSquared(this.chr.x, this.chr.y, vector3f.x, vector3f.y)) {
									return Behavior.BehaviorResult.Working;
								}
							} else if (((IsoZombie)this.chr).AllowRepathDelay <= 0.0F) {
								((IsoZombie)this.chr).AllowRepathDelay = 6.25F;
								if (PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, vector3f.x, vector3f.y, (int)this.targetZ, (IsoMovingObject)null)) {
									this.setData(vector3f.x, vector3f.y, this.targetZ);
									return Behavior.BehaviorResult.Working;
								}

								this.path.clear();
								this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
								this.path.addNode(vector3f.x, vector3f.y, vector3f.z);
							}
						}
					}

					closestPointOnPath(this.chr.x, this.chr.y, this.chr.z, this.chr, this.path, pointOnPath);
					this.pathIndex = pointOnPath.pathIndex;
					PolygonalMap2.PathNode pathNode;
					if (this.pathIndex == this.path.nodes.size() - 2) {
						pathNode = (PolygonalMap2.PathNode)this.path.nodes.get(this.path.nodes.size() - 1);
						if (IsoUtils.DistanceToSquared(this.chr.x, this.chr.y, pathNode.x, pathNode.y) <= 0.0025000002F) {
							return Behavior.BehaviorResult.Succeeded;
						}
					} else if (this.pathIndex < this.path.nodes.size() - 2 && pointOnPath.dist > 0.999F) {
						++this.pathIndex;
					}

					pathNode = (PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex);
					PolygonalMap2.PathNode pathNode2 = (PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex + 1);
					float float1 = pathNode2.x;
					float float2 = pathNode2.y;
					Vector2 vector2 = tempVector2.set(pathNode2.x - this.chr.x, pathNode2.y - this.chr.y);
					vector2.normalize();
					float float3 = this.chr.getPathSpeed();
					if (this.chr instanceof IsoZombie) {
						((IsoZombie)this.chr).bRunning = false;
						if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
							float3 = 0.08F;
							((IsoZombie)this.chr).bRunning = true;
						}

						this.chr.setIgnoreMovementForDirection(false);
					}

					float float4 = GameTime.instance.getMultiplier();
					float float5 = float3 * float4;
					float float6 = IsoUtils.DistanceTo(float1, float2, this.chr.x, this.chr.y);
					if (float5 >= float6) {
						float3 *= float6 / float5;
						++this.pathIndex;
					}

					if (!(this.chr instanceof IsoZombie) && float6 >= 0.5F) {
						if (this.checkDoorHoppableWindow(this.chr.x + vector2.x * 0.5F, this.chr.y + vector2.y * 0.5F, this.chr.z)) {
							return Behavior.BehaviorResult.Failed;
						}

						if (state != this.chr.getCurrentState()) {
							return Behavior.BehaviorResult.Working;
						}
					}

					this.chr.MoveForward(float3, vector2.x, vector2.y, 1.0F);
					this.chr.faceLocation(float1 - 0.5F, float2 - 0.5F);
					this.chr.angle.set(float1 - this.chr.x, float2 - this.chr.y);
					this.chr.angle.normalize();
					if (this.chr instanceof IsoZombie) {
						((IsoZombie)this.chr).reqMovement.x = this.chr.angle.x;
						((IsoZombie)this.chr).reqMovement.y = this.chr.angle.y;
					}

					return Behavior.BehaviorResult.Working;
				}
			} else {
				return Behavior.BehaviorResult.Working;
			}
		}
	}

	private boolean checkDoorHoppableWindow(float float1, float float2, float float3) {
		IsoGridSquare square = this.chr.getCurrentSquare();
		if (square == null) {
			return false;
		} else {
			IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
			if (square2 != null && square2 != square) {
				int int1 = square2.x - square.x;
				int int2 = square2.y - square.y;
				if (int1 != 0 && int2 != 0) {
					return false;
				} else {
					IsoObject object = this.chr.getCurrentSquare().getDoorTo(square2);
					if (object instanceof IsoDoor) {
						IsoDoor door = (IsoDoor)object;
						if (!door.open) {
							door.ToggleDoor(this.chr);
							if (!door.open) {
								return true;
							}
						}
					} else if (object instanceof IsoThumpable) {
						IsoThumpable thumpable = (IsoThumpable)object;
						if (!thumpable.open) {
							thumpable.ToggleDoor(this.chr);
							if (!thumpable.open) {
								return true;
							}
						}
					}

					IsoWindow window = square.getWindowTo(square2);
					if (window != null) {
						if (window.canClimbThrough(this.chr) && (!window.isSmashed() || window.isGlassRemoved())) {
							this.chr.climbThroughWindow(window);
							return false;
						} else {
							return true;
						}
					} else {
						IsoThumpable thumpable2 = square.getWindowThumpableTo(square2);
						if (thumpable2 != null) {
							if (thumpable2.isBarricaded()) {
								return true;
							} else {
								this.chr.climbThroughWindow(thumpable2);
								return false;
							}
						} else {
							IsoObject object2 = square.getWindowFrameTo(square2);
							if (object2 != null) {
								this.chr.climbThroughWindowFrame(object2);
								return false;
							} else {
								if (int1 > 0 && square2.Is(IsoFlagType.HoppableW)) {
									this.chr.StateMachineParams.put(0, IsoDirections.E);
									this.chr.getStateMachine().changeState(ClimbOverFenceState.instance());
								} else if (int1 < 0 && square.Is(IsoFlagType.HoppableW)) {
									this.chr.StateMachineParams.put(0, IsoDirections.W);
									this.chr.getStateMachine().changeState(ClimbOverFenceState.instance());
								} else if (int2 < 0 && square.Is(IsoFlagType.HoppableN)) {
									this.chr.StateMachineParams.put(0, IsoDirections.N);
									this.chr.getStateMachine().changeState(ClimbOverFenceState.instance());
								} else if (int2 > 0 && square2.Is(IsoFlagType.HoppableN)) {
									this.chr.StateMachineParams.put(0, IsoDirections.S);
									this.chr.getStateMachine().changeState(ClimbOverFenceState.instance());
								}

								return false;
							}
						}
					}
				}
			} else {
				return false;
			}
		}
	}

	public static void closestPointOnPath(float float1, float float2, float float3, IsoMovingObject movingObject, PolygonalMap2.Path path, PathFindBehavior2.PointOnPath pointOnPath) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		pointOnPath.pathIndex = 0;
		float float4 = Float.MAX_VALUE;
		for (int int1 = 0; int1 < path.nodes.size() - 1; ++int1) {
			PolygonalMap2.PathNode pathNode = (PolygonalMap2.PathNode)path.nodes.get(int1);
			PolygonalMap2.PathNode pathNode2 = (PolygonalMap2.PathNode)path.nodes.get(int1 + 1);
			if ((int)pathNode.z == (int)float3 || (int)pathNode2.z == (int)float3) {
				float float5 = pathNode.x;
				float float6 = pathNode.y;
				float float7 = pathNode2.x;
				float float8 = pathNode2.y;
				double double1 = (double)((float1 - float5) * (float7 - float5) + (float2 - float6) * (float8 - float6)) / (Math.pow((double)(float7 - float5), 2.0) + Math.pow((double)(float8 - float6), 2.0));
				double double2 = (double)float5 + double1 * (double)(float7 - float5);
				double double3 = (double)float6 + double1 * (double)(float8 - float6);
				if (double1 <= 0.0) {
					double2 = (double)float5;
					double3 = (double)float6;
					double1 = 0.0;
				} else if (double1 >= 1.0) {
					double2 = (double)float7;
					double3 = (double)float8;
					double1 = 1.0;
				}

				int int2 = (int)double2 - (int)float1;
				int int3 = (int)double3 - (int)float2;
				IsoGridSquare square;
				if ((int2 != 0 || int3 != 0) && Math.abs(int2) <= 1 && Math.abs(int3) <= 1) {
					IsoGridSquare square2 = cell.getGridSquare((int)float1, (int)float2, (int)float3);
					square = cell.getGridSquare((int)double2, (int)double3, (int)float3);
					if (square2 != null && square != null && square2.testCollideAdjacent(movingObject, int2, int3, 0)) {
						continue;
					}
				}

				float float9 = float3;
				if (Math.abs(int2) <= 1 && Math.abs(int3) <= 1) {
					square = cell.getGridSquare((int)pathNode.x, (int)pathNode.y, (int)pathNode.z);
					IsoGridSquare square3 = cell.getGridSquare((int)pathNode2.x, (int)pathNode2.y, (int)pathNode2.z);
					float float10 = square == null ? pathNode.z : PolygonalMap2.instance.getApparentZ(square);
					float float11 = square3 == null ? pathNode2.z : PolygonalMap2.instance.getApparentZ(square3);
					float9 = float10 + (float11 - float10) * (float)double1;
				}

				float float12 = IsoUtils.DistanceToSquared(float1, float2, float3, (float)double2, (float)double3, float9);
				if (float12 < float4) {
					float4 = float12;
					pointOnPath.pathIndex = int1;
					pointOnPath.dist = double1 == 1.0 ? 1.0F : (float)double1;
					pointOnPath.x = (float)double2;
					pointOnPath.y = (float)double3;
				}
			}
		}
	}

	void advanceAlongPath(float float1, float float2, float float3, float float4, PathFindBehavior2.PointOnPath pointOnPath) {
		closestPointOnPath(float1, float2, float3, this.chr, this.path, pointOnPath);
		for (int int1 = pointOnPath.pathIndex; int1 < this.path.nodes.size() - 1; ++int1) {
			PolygonalMap2.PathNode pathNode = (PolygonalMap2.PathNode)this.path.nodes.get(int1);
			PolygonalMap2.PathNode pathNode2 = (PolygonalMap2.PathNode)this.path.nodes.get(int1 + 1);
			double double1 = (double)IsoUtils.DistanceTo2D(float1, float2, pathNode2.x, pathNode2.y);
			if (!((double)float4 > double1)) {
				pointOnPath.pathIndex = int1;
				pointOnPath.dist += float4 / IsoUtils.DistanceTo2D(pathNode.x, pathNode.y, pathNode2.x, pathNode2.y);
				pointOnPath.x = pathNode.x + pointOnPath.dist * (pathNode2.x - pathNode.x);
				pointOnPath.y = pathNode.y + pointOnPath.dist * (pathNode2.y - pathNode.y);
				return;
			}

			float1 = pathNode2.x;
			float2 = pathNode2.y;
			float4 = (float)((double)float4 - double1);
			pointOnPath.dist = 0.0F;
		}

		pointOnPath.pathIndex = this.path.nodes.size() - 1;
		pointOnPath.dist = 1.0F;
		pointOnPath.x = ((PolygonalMap2.PathNode)this.path.nodes.get(pointOnPath.pathIndex)).x;
		pointOnPath.y = ((PolygonalMap2.PathNode)this.path.nodes.get(pointOnPath.pathIndex)).y;
	}

	public void render() {
		if (this.chr.getCurrentState() == WalkTowardState.instance()) {
			tempVector2.x = this.targetX - this.chr.x;
			tempVector2.y = this.targetY - this.chr.y;
			tempVector2.setLength(Math.min(100.0F, tempVector2.getLength()));
			LineDrawer.addLine(this.chr.x, this.chr.y, this.chr.z, this.chr.x + tempVector2.x, this.chr.y + tempVector2.y, this.targetZ, 1.0F, 1.0F, 1.0F, (String)null, true);
		} else if (this.chr.getPath2() != null) {
			int int1;
			PolygonalMap2.PathNode pathNode;
			float float1;
			float float2;
			for (int1 = 0; int1 < this.path.nodes.size() - 1; ++int1) {
				pathNode = (PolygonalMap2.PathNode)this.path.nodes.get(int1);
				PolygonalMap2.PathNode pathNode2 = (PolygonalMap2.PathNode)this.path.nodes.get(int1 + 1);
				float1 = 1.0F;
				float2 = 1.0F;
				if ((int)pathNode.z != (int)pathNode2.z) {
					float2 = 0.0F;
				}

				LineDrawer.addLine(pathNode.x, pathNode.y, pathNode.z, pathNode2.x, pathNode2.y, pathNode2.z, float1, float2, 0.0F, (String)null, true);
			}

			for (int1 = 0; int1 < this.path.nodes.size(); ++int1) {
				pathNode = (PolygonalMap2.PathNode)this.path.nodes.get(int1);
				float float3 = 1.0F;
				float1 = 1.0F;
				float2 = 0.0F;
				if (int1 == 0) {
					float3 = 0.0F;
					float2 = 1.0F;
				}

				LineDrawer.addLine(pathNode.x - 0.05F, pathNode.y - 0.05F, pathNode.z, pathNode.x + 0.05F, pathNode.y + 0.05F, pathNode.z, float3, float1, float2, (String)null, false);
			}

			closestPointOnPath(this.chr.x, this.chr.y, this.chr.z, this.chr, this.path, pointOnPath);
			LineDrawer.addLine(pointOnPath.x - 0.05F, pointOnPath.y - 0.05F, this.chr.z, pointOnPath.x + 0.05F, pointOnPath.y + 0.05F, this.chr.z, 0.0F, 1.0F, 0.0F, (String)null, false);
		}
	}

	public void Succeeded(PolygonalMap2.Path path, Mover mover) {
		this.path.copyFrom(path);
		if (!this.isCancel) {
			this.chr.setPath2(this.path);
		}

		this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
	}

	public void Failed(Mover mover) {
		this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
	}

	public static class PointOnPath {
		int pathIndex;
		float dist;
		float x;
		float y;
	}
	private static enum Goal {

		None,
		Character,
		Location,
		VehicleArea,
		VehicleSeat;
	}
}
