package zombie.vehicles;

import gnu.trove.list.array.TFloatArrayList;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import zombie.ai.State;
import zombie.ai.WalkingOnTheSpot;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.Mover;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.CollideWithWallState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieGetDownState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.debug.DebugOptions;
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
import zombie.network.GameClient;
import zombie.popman.ObjectPool;
import zombie.scripting.objects.VehicleScript;
import zombie.util.Type;


public final class PathFindBehavior2 implements PolygonalMap2.IPathfinder {
	private static final Vector2 tempVector2 = new Vector2();
	private static final Vector2f tempVector2f = new Vector2f();
	private static final Vector2 tempVector2_2 = new Vector2();
	private static final Vector3f tempVector3f_1 = new Vector3f();
	private static final PathFindBehavior2.PointOnPath pointOnPath = new PathFindBehavior2.PointOnPath();
	public boolean pathNextIsSet = false;
	public float pathNextX;
	public float pathNextY;
	public ArrayList Listeners = new ArrayList();
	public PathFindBehavior2.NPCData NPCData = new PathFindBehavior2.NPCData();
	private IsoGameCharacter chr;
	private float startX;
	private float startY;
	private float startZ;
	private float targetX;
	private float targetY;
	private float targetZ;
	private final TFloatArrayList targetXYZ = new TFloatArrayList();
	private final PolygonalMap2.Path path = new PolygonalMap2.Path();
	private int pathIndex;
	private boolean isCancel = true;
	public boolean bStopping = false;
	public final WalkingOnTheSpot walkingOnTheSpot = new WalkingOnTheSpot();
	private final ArrayList actualPos = new ArrayList();
	private static final ObjectPool actualPool = new ObjectPool(PathFindBehavior2.DebugPt::new);
	private PathFindBehavior2.Goal goal;
	private IsoGameCharacter goalCharacter;
	private BaseVehicle goalVehicle;
	private String goalVehicleArea;
	private int goalVehicleSeat;

	public PathFindBehavior2(IsoGameCharacter gameCharacter) {
		this.goal = PathFindBehavior2.Goal.None;
		this.chr = gameCharacter;
	}

	public boolean isGoalNone() {
		return this.goal == PathFindBehavior2.Goal.None;
	}

	public boolean isGoalCharacter() {
		return this.goal == PathFindBehavior2.Goal.Character;
	}

	public boolean isGoalLocation() {
		return this.goal == PathFindBehavior2.Goal.Location;
	}

	public boolean isGoalSound() {
		return this.goal == PathFindBehavior2.Goal.Sound;
	}

	public boolean isGoalVehicleAdjacent() {
		return this.goal == PathFindBehavior2.Goal.VehicleAdjacent;
	}

	public boolean isGoalVehicleArea() {
		return this.goal == PathFindBehavior2.Goal.VehicleArea;
	}

	public boolean isGoalVehicleSeat() {
		return this.goal == PathFindBehavior2.Goal.VehicleSeat;
	}

	public void reset() {
		this.startX = this.chr.getX();
		this.startY = this.chr.getY();
		this.startZ = this.chr.getZ();
		this.targetX = this.startX;
		this.targetY = this.startY;
		this.targetZ = this.startZ;
		this.targetXYZ.resetQuick();
		this.pathIndex = 0;
		this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
		this.walkingOnTheSpot.reset(this.startX, this.startY);
	}

	public void pathToCharacter(IsoGameCharacter gameCharacter) {
		this.isCancel = false;
		this.goal = PathFindBehavior2.Goal.Character;
		this.goalCharacter = gameCharacter;
		if (gameCharacter.getVehicle() != null) {
			Vector3f vector3f = gameCharacter.getVehicle().chooseBestAttackPosition(gameCharacter, this.chr, tempVector3f_1);
			if (vector3f != null) {
				this.setData(vector3f.x, vector3f.y, (float)((int)gameCharacter.getVehicle().z));
				return;
			}

			this.setData(gameCharacter.getVehicle().x, gameCharacter.getVehicle().y, (float)((int)gameCharacter.getVehicle().z));
			if (this.chr.DistToSquared(gameCharacter.getVehicle()) < 100.0F) {
				IsoZombie zombie = (IsoZombie)Type.tryCastTo(this.chr, IsoZombie.class);
				if (zombie != null) {
					zombie.AllowRepathDelay = 100.0F;
				}

				this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
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

	public void pathToSound(int int1, int int2, int int3) {
		this.isCancel = false;
		this.goal = PathFindBehavior2.Goal.Sound;
		this.setData((float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3);
	}

	public void pathToNearest(TFloatArrayList tFloatArrayList) {
		if (tFloatArrayList != null && !tFloatArrayList.isEmpty()) {
			if (tFloatArrayList.size() % 3 != 0) {
				throw new IllegalArgumentException("locations should be multiples of x,y,z");
			} else {
				this.isCancel = false;
				this.goal = PathFindBehavior2.Goal.Location;
				this.setData(tFloatArrayList.get(0), tFloatArrayList.get(1), tFloatArrayList.get(2));
				for (int int1 = 3; int1 < tFloatArrayList.size(); int1 += 3) {
					this.targetXYZ.add(tFloatArrayList.get(int1));
					this.targetXYZ.add(tFloatArrayList.get(int1 + 1));
					this.targetXYZ.add(tFloatArrayList.get(int1 + 2));
				}
			}
		} else {
			throw new IllegalArgumentException("locations is null or empty");
		}
	}

	public void pathToNearestTable(KahluaTable kahluaTable) {
		if (kahluaTable != null && !kahluaTable.isEmpty()) {
			if (kahluaTable.len() % 3 != 0) {
				throw new IllegalArgumentException("locations table should be multiples of x,y,z");
			} else {
				TFloatArrayList tFloatArrayList = new TFloatArrayList(kahluaTable.size());
				int int1 = 1;
				for (int int2 = kahluaTable.len(); int1 <= int2; int1 += 3) {
					Double Double1 = (Double)Type.tryCastTo(kahluaTable.rawget(int1), Double.class);
					Double Double2 = (Double)Type.tryCastTo(kahluaTable.rawget(int1 + 1), Double.class);
					Double Double3 = (Double)Type.tryCastTo(kahluaTable.rawget(int1 + 2), Double.class);
					if (Double1 == null || Double2 == null || Double3 == null) {
						throw new IllegalArgumentException("locations table should be multiples of x,y,z");
					}

					tFloatArrayList.add(Double1.floatValue());
					tFloatArrayList.add(Double2.floatValue());
					tFloatArrayList.add(Double3.floatValue());
				}

				this.pathToNearest(tFloatArrayList);
			}
		} else {
			throw new IllegalArgumentException("locations table is null or empty");
		}
	}

	public void pathToVehicleAdjacent(BaseVehicle baseVehicle) {
		this.isCancel = false;
		this.goal = PathFindBehavior2.Goal.VehicleAdjacent;
		this.goalVehicle = baseVehicle;
		VehicleScript vehicleScript = baseVehicle.getScript();
		Vector3f vector3f = vehicleScript.getExtents();
		Vector3f vector3f2 = vehicleScript.getCenterOfMassOffset();
		float float1 = vector3f.x;
		float float2 = vector3f.z;
		float float3 = 0.3F;
		float float4 = vector3f2.x - float1 / 2.0F - float3;
		float float5 = vector3f2.z - float2 / 2.0F - float3;
		float float6 = vector3f2.x + float1 / 2.0F + float3;
		float float7 = vector3f2.z + float2 / 2.0F + float3;
		TFloatArrayList tFloatArrayList = new TFloatArrayList();
		Vector3f vector3f3 = baseVehicle.getWorldPos(float4, vector3f2.y, vector3f2.z, tempVector3f_1);
		if (PolygonalMap2.instance.canStandAt(vector3f3.x, vector3f3.y, (int)this.targetZ, baseVehicle, false, true)) {
			tFloatArrayList.add(vector3f3.x);
			tFloatArrayList.add(vector3f3.y);
			tFloatArrayList.add(this.targetZ);
		}

		vector3f3 = baseVehicle.getWorldPos(float6, vector3f2.y, vector3f2.z, tempVector3f_1);
		if (PolygonalMap2.instance.canStandAt(vector3f3.x, vector3f3.y, (int)this.targetZ, baseVehicle, false, true)) {
			tFloatArrayList.add(vector3f3.x);
			tFloatArrayList.add(vector3f3.y);
			tFloatArrayList.add(this.targetZ);
		}

		vector3f3 = baseVehicle.getWorldPos(vector3f2.x, vector3f2.y, float5, tempVector3f_1);
		if (PolygonalMap2.instance.canStandAt(vector3f3.x, vector3f3.y, (int)this.targetZ, baseVehicle, false, true)) {
			tFloatArrayList.add(vector3f3.x);
			tFloatArrayList.add(vector3f3.y);
			tFloatArrayList.add(this.targetZ);
		}

		vector3f3 = baseVehicle.getWorldPos(vector3f2.x, vector3f2.y, float7, tempVector3f_1);
		if (PolygonalMap2.instance.canStandAt(vector3f3.x, vector3f3.y, (int)this.targetZ, baseVehicle, false, true)) {
			tFloatArrayList.add(vector3f3.x);
			tFloatArrayList.add(vector3f3.y);
			tFloatArrayList.add(this.targetZ);
		}

		this.setData(tFloatArrayList.get(0), tFloatArrayList.get(1), tFloatArrayList.get(2));
		for (int int1 = 3; int1 < tFloatArrayList.size(); int1 += 3) {
			this.targetXYZ.add(tFloatArrayList.get(int1));
			this.targetXYZ.add(tFloatArrayList.get(int1 + 1));
			this.targetXYZ.add(tFloatArrayList.get(int1 + 2));
		}
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
		Vector2 vector2;
		VehicleScript.Area area;
		Vector2 vector22;
		if (position != null) {
			vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc();
			if (position.area == null) {
				baseVehicle.getPassengerPositionWorldPos(position, vector3f);
			} else {
				vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).alloc();
				area = baseVehicle.script.getAreaById(position.area);
				vector22 = baseVehicle.areaPositionWorld4PlayerInteract(area, vector2);
				vector3f.x = vector22.x;
				vector3f.y = vector22.y;
				vector3f.z = 0.0F;
				((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(vector2);
			}

			vector3f.sub(this.chr.x, this.chr.y, this.chr.z);
			if (vector3f.length() < 2.0F) {
				baseVehicle.getPassengerPositionWorldPos(position, vector3f);
				this.setData(vector3f.x(), vector3f.y(), (float)((int)vector3f.z()));
				if (this.chr instanceof IsoPlayer && (int)this.chr.z == (int)this.targetZ) {
					((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f);
					this.path.clear();
					this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
					this.path.addNode(this.targetX, this.targetY, this.targetZ);
					this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
					return;
				}
			}

			((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f);
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
			vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc();
			if (position.area == null) {
				baseVehicle.getPassengerPositionWorldPos(position, vector3f);
			} else {
				vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).alloc();
				area = baseVehicle.script.getAreaById(position.area);
				vector22 = baseVehicle.areaPositionWorld4PlayerInteract(area, vector2);
				vector3f.x = vector22.x;
				vector3f.y = vector22.y;
				vector3f.z = 0.0F;
				((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(vector2);
			}

			this.setData(vector3f.x(), vector3f.y(), (float)((int)vector3f.z()));
			((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f);
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

	public boolean getIsCancelled() {
		return this.isCancel;
	}

	public void setData(float float1, float float2, float float3) {
		this.startX = this.chr.getX();
		this.startY = this.chr.getY();
		this.startZ = this.chr.getZ();
		this.targetX = float1;
		this.targetY = float2;
		this.targetZ = float3;
		this.targetXYZ.resetQuick();
		this.pathIndex = 0;
		PolygonalMap2.instance.cancelRequest(this.chr);
		this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
		this.bStopping = false;
		actualPool.release((List)this.actualPos);
		this.actualPos.clear();
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

	public float getPathLength() {
		if (this.path != null && this.path.nodes.size() != 0) {
			if (this.pathIndex + 1 >= this.path.nodes.size()) {
				return (float)Math.sqrt((double)((this.chr.x - this.targetX) * (this.chr.x - this.targetX) + (this.chr.y - this.targetY) * (this.chr.y - this.targetY)));
			} else {
				float float1 = (float)Math.sqrt((double)((this.chr.x - ((PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex + 1)).x) * (this.chr.x - ((PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex + 1)).x) + (this.chr.y - ((PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex + 1)).y) * (this.chr.y - ((PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex + 1)).y)));
				for (int int1 = this.pathIndex + 2; int1 < this.path.nodes.size(); ++int1) {
					float1 += (float)Math.sqrt((double)((((PolygonalMap2.PathNode)this.path.nodes.get(int1 - 1)).x - ((PolygonalMap2.PathNode)this.path.nodes.get(int1)).x) * (((PolygonalMap2.PathNode)this.path.nodes.get(int1 - 1)).x - ((PolygonalMap2.PathNode)this.path.nodes.get(int1)).x) + (((PolygonalMap2.PathNode)this.path.nodes.get(int1 - 1)).y - ((PolygonalMap2.PathNode)this.path.nodes.get(int1)).y) * (((PolygonalMap2.PathNode)this.path.nodes.get(int1 - 1)).y - ((PolygonalMap2.PathNode)this.path.nodes.get(int1)).y)));
				}

				return float1;
			}
		} else {
			return (float)Math.sqrt((double)((this.chr.x - this.targetX) * (this.chr.x - this.targetX) + (this.chr.y - this.targetY) * (this.chr.y - this.targetY)));
		}
	}

	public IsoGameCharacter getTargetChar() {
		return this.goal == PathFindBehavior2.Goal.Character ? this.goalCharacter : null;
	}

	public boolean isTargetLocation(float float1, float float2, float float3) {
		return this.goal == PathFindBehavior2.Goal.Location && float1 == this.targetX && float2 == this.targetY && (int)float3 == (int)this.targetZ;
	}

	public PathFindBehavior2.BehaviorResult update() {
		if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.notrunning) {
			PolygonalMap2.PathFindRequest pathFindRequest = PolygonalMap2.instance.addRequest(this, this.chr, this.startX, this.startY, this.startZ, this.targetX, this.targetY, this.targetZ);
			pathFindRequest.targetXYZ.resetQuick();
			pathFindRequest.targetXYZ.addAll(this.targetXYZ);
			this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.notyetfound;
			this.walkingOnTheSpot.reset(this.chr.x, this.chr.y);
			this.updateWhileRunningPathfind();
			return PathFindBehavior2.BehaviorResult.Working;
		} else if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.notyetfound) {
			this.updateWhileRunningPathfind();
			return PathFindBehavior2.BehaviorResult.Working;
		} else if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.failed) {
			return PathFindBehavior2.BehaviorResult.Failed;
		} else {
			State state = this.chr.getCurrentState();
			if (Core.bDebug && DebugOptions.instance.PathfindRenderPath.getValue() && this.chr instanceof IsoPlayer) {
				this.actualPos.add(((PathFindBehavior2.DebugPt)actualPool.alloc()).init(this.chr.x, this.chr.y, this.chr.z, state == ClimbOverFenceState.instance() || state == ClimbThroughWindowState.instance()));
			}

			if (state != ClimbOverFenceState.instance() && state != ClimbThroughWindowState.instance()) {
				if (this.chr.getVehicle() != null) {
					return PathFindBehavior2.BehaviorResult.Failed;
				} else if (this.walkingOnTheSpot.check(this.chr.x, this.chr.y)) {
					return PathFindBehavior2.BehaviorResult.Failed;
				} else {
					this.chr.setMoving(true);
					this.chr.setPath2(this.path);
					IsoZombie zombie = (IsoZombie)Type.tryCastTo(this.chr, IsoZombie.class);
					if (this.goal == PathFindBehavior2.Goal.Character && zombie != null && this.goalCharacter != null && this.goalCharacter.getVehicle() != null && this.chr.DistToSquared(this.targetX, this.targetY) < 16.0F) {
						Vector3f vector3f = this.goalCharacter.getVehicle().chooseBestAttackPosition(this.goalCharacter, this.chr, tempVector3f_1);
						if (vector3f == null) {
							return PathFindBehavior2.BehaviorResult.Failed;
						}

						if (Math.abs(vector3f.x - this.targetX) > 0.1F || Math.abs(vector3f.y - this.targetY) > 0.1F) {
							if (Math.abs(this.goalCharacter.getVehicle().getCurrentSpeedKmHour()) > 0.1F) {
								if (!PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, vector3f.x, vector3f.y, (int)this.targetZ, this.goalCharacter)) {
									this.path.clear();
									this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
									this.path.addNode(vector3f.x, vector3f.y, vector3f.z);
								} else if (IsoUtils.DistanceToSquared(vector3f.x, vector3f.y, this.targetX, this.targetY) > IsoUtils.DistanceToSquared(this.chr.x, this.chr.y, vector3f.x, vector3f.y)) {
									return PathFindBehavior2.BehaviorResult.Working;
								}
							} else if (zombie.AllowRepathDelay <= 0.0F) {
								zombie.AllowRepathDelay = 6.25F;
								if (PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, vector3f.x, vector3f.y, (int)this.targetZ, (IsoMovingObject)null)) {
									this.setData(vector3f.x, vector3f.y, this.targetZ);
									return PathFindBehavior2.BehaviorResult.Working;
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
							this.chr.getDeferredMovement(tempVector2);
							if (!(tempVector2.getLength() > 0.0F)) {
								this.pathNextIsSet = false;
								return PathFindBehavior2.BehaviorResult.Succeeded;
							}

							if (zombie != null || this.chr instanceof IsoPlayer) {
								this.chr.setMoving(false);
							}

							this.bStopping = true;
							return PathFindBehavior2.BehaviorResult.Working;
						}
					} else if (this.pathIndex < this.path.nodes.size() - 2 && pointOnPath.dist > 0.999F) {
						++this.pathIndex;
					}

					pathNode = (PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex);
					PolygonalMap2.PathNode pathNode2 = (PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex + 1);
					this.pathNextX = pathNode2.x;
					this.pathNextY = pathNode2.y;
					this.pathNextIsSet = true;
					Vector2 vector2 = tempVector2.set(this.pathNextX - this.chr.x, this.pathNextY - this.chr.y);
					vector2.normalize();
					this.chr.getDeferredMovement(tempVector2_2);
					float float1 = tempVector2_2.getLength();
					if (zombie != null) {
						zombie.bRunning = false;
						if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
							zombie.bRunning = true;
						}
					}

					float float2 = 1.0F;
					float float3 = float1 * float2;
					float float4 = IsoUtils.DistanceTo(this.pathNextX, this.pathNextY, this.chr.x, this.chr.y);
					if (float3 >= float4) {
						float1 *= float4 / float3;
						++this.pathIndex;
					}

					if (zombie != null) {
						this.checkCrawlingTransition(pathNode, pathNode2, float4);
					}

					if (zombie == null && float4 >= 0.5F) {
						if (this.checkDoorHoppableWindow(this.chr.x + vector2.x * Math.max(0.5F, float1), this.chr.y + vector2.y * Math.max(0.5F, float1), this.chr.z)) {
							return PathFindBehavior2.BehaviorResult.Failed;
						}

						if (state != this.chr.getCurrentState()) {
							return PathFindBehavior2.BehaviorResult.Working;
						}
					}

					if (float1 <= 0.0F) {
						this.walkingOnTheSpot.reset(this.chr.x, this.chr.y);
						return PathFindBehavior2.BehaviorResult.Working;
					} else {
						tempVector2_2.set(vector2);
						tempVector2_2.setLength(float1);
						this.chr.MoveUnmodded(tempVector2_2);
						if (this.isStrafing()) {
							if ((this.goal == PathFindBehavior2.Goal.VehicleAdjacent || this.goal == PathFindBehavior2.Goal.VehicleArea || this.goal == PathFindBehavior2.Goal.VehicleSeat) && this.goalVehicle != null) {
								this.chr.faceThisObject(this.goalVehicle);
							}
						} else if (!this.chr.isAiming()) {
							this.chr.faceLocationF(this.pathNextX, this.pathNextY);
						}

						return PathFindBehavior2.BehaviorResult.Working;
					}
				}
			} else {
				if (GameClient.bClient && this.chr instanceof IsoPlayer && !((IsoPlayer)this.chr).isLocalPlayer()) {
					this.chr.getDeferredMovement(tempVector2_2);
					this.chr.MoveUnmodded(tempVector2_2);
				}

				return PathFindBehavior2.BehaviorResult.Working;
			}
		}
	}

	private void updateWhileRunningPathfind() {
		if (this.pathNextIsSet) {
			this.moveToPoint(this.pathNextX, this.pathNextY, 1.0F);
		}
	}

	public void moveToPoint(float float1, float float2, float float3) {
		if (!(this.chr instanceof IsoPlayer) || this.chr.getCurrentState() != CollideWithWallState.instance()) {
			IsoZombie zombie = (IsoZombie)Type.tryCastTo(this.chr, IsoZombie.class);
			Vector2 vector2 = tempVector2.set(float1 - this.chr.x, float2 - this.chr.y);
			if ((int)float1 != (int)this.chr.x || (int)float2 != (int)this.chr.y || !(vector2.getLength() <= 0.1F)) {
				vector2.normalize();
				this.chr.getDeferredMovement(tempVector2_2);
				float float4 = tempVector2_2.getLength();
				float4 *= float3;
				if (zombie != null) {
					zombie.bRunning = false;
					if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
						zombie.bRunning = true;
					}
				}

				if (!(float4 <= 0.0F)) {
					tempVector2_2.set(vector2);
					tempVector2_2.setLength(float4);
					this.chr.MoveUnmodded(tempVector2_2);
					this.chr.faceLocation(float1 - 0.5F, float2 - 0.5F);
					this.chr.getForwardDirection().set(float1 - this.chr.x, float2 - this.chr.y);
					this.chr.getForwardDirection().normalize();
				}
			}
		}
	}

	public void moveToDir(IsoMovingObject movingObject, float float1) {
		Vector2 vector2 = tempVector2.set(movingObject.x - this.chr.x, movingObject.y - this.chr.y);
		if (!(vector2.getLength() <= 0.1F)) {
			vector2.normalize();
			this.chr.getDeferredMovement(tempVector2_2);
			float float2 = tempVector2_2.getLength();
			float2 *= float1;
			if (this.chr instanceof IsoZombie) {
				((IsoZombie)this.chr).bRunning = false;
				if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
					((IsoZombie)this.chr).bRunning = true;
				}
			}

			if (!(float2 <= 0.0F)) {
				tempVector2_2.set(vector2);
				tempVector2_2.setLength(float2);
				this.chr.MoveUnmodded(tempVector2_2);
				this.chr.faceLocation(movingObject.x - 0.5F, movingObject.y - 0.5F);
				this.chr.getForwardDirection().set(movingObject.x - this.chr.x, movingObject.y - this.chr.y);
				this.chr.getForwardDirection().normalize();
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
									this.chr.climbOverFence(IsoDirections.E);
								} else if (int1 < 0 && square.Is(IsoFlagType.HoppableW)) {
									this.chr.climbOverFence(IsoDirections.W);
								} else if (int2 < 0 && square.Is(IsoFlagType.HoppableN)) {
									this.chr.climbOverFence(IsoDirections.N);
								} else if (int2 > 0 && square2.Is(IsoFlagType.HoppableN)) {
									this.chr.climbOverFence(IsoDirections.S);
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

	private void checkCrawlingTransition(PolygonalMap2.PathNode pathNode, PolygonalMap2.PathNode pathNode2, float float1) {
		IsoZombie zombie = (IsoZombie)this.chr;
		if (this.pathIndex < this.path.nodes.size() - 2) {
			pathNode = (PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex);
			pathNode2 = (PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex + 1);
			float1 = IsoUtils.DistanceTo(pathNode2.x, pathNode2.y, this.chr.x, this.chr.y);
		}

		if (zombie.isCrawling()) {
			if (!zombie.isCanWalk()) {
				return;
			}

			if (zombie.isBeingSteppedOn()) {
			}

			if (zombie.getStateMachine().getPrevious() == ZombieGetDownState.instance() && ZombieGetDownState.instance().isNearStartXY(zombie)) {
				return;
			}

			this.advanceAlongPath(this.chr.x, this.chr.y, this.chr.z, 0.5F, pointOnPath);
			if (!PolygonalMap2.instance.canStandAt(pointOnPath.x, pointOnPath.y, (int)zombie.z, (IsoMovingObject)null, false, true)) {
				return;
			}

			if (!pathNode2.hasFlag(1) && PolygonalMap2.instance.canStandAt(zombie.x, zombie.y, (int)zombie.z, (IsoMovingObject)null, false, true)) {
				zombie.setVariable("ShouldStandUp", true);
			}
		} else {
			if (pathNode.hasFlag(1) && pathNode2.hasFlag(1)) {
				zombie.setVariable("ShouldBeCrawling", true);
				ZombieGetDownState.instance().setParams(this.chr);
				return;
			}

			if (float1 < 0.4F && !pathNode.hasFlag(1) && pathNode2.hasFlag(1)) {
				zombie.setVariable("ShouldBeCrawling", true);
				ZombieGetDownState.instance().setParams(this.chr);
			}
		}
	}

	public boolean shouldGetUpFromCrawl() {
		return this.chr.getVariableBoolean("ShouldStandUp");
	}

	public boolean isStrafing() {
		if (this.chr.isZombie()) {
			return false;
		} else {
			return this.path.nodes.size() == 2 && IsoUtils.DistanceToSquared(this.startX, this.startY, this.startZ * 3.0F, this.targetX, this.targetY, this.targetZ * 3.0F) < 0.25F;
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
					if (movingObject instanceof IsoZombie) {
						boolean boolean1 = ((IsoZombie)movingObject).Ghost;
						((IsoZombie)movingObject).Ghost = true;
						try {
							if (square2 != null && square != null && square2.testCollideAdjacent(movingObject, int2, int3, 0)) {
								continue;
							}
						} finally {
							((IsoZombie)movingObject).Ghost = boolean1;
						}
					} else if (square2 != null && square != null && square2.testCollideAdjacent(movingObject, int2, int3, 0)) {
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
			WalkTowardState.instance().calculateTargetLocation((IsoZombie)this.chr, tempVector2);
			Vector2 vector2 = tempVector2;
			vector2.x -= this.chr.x;
			vector2 = tempVector2;
			vector2.y -= this.chr.y;
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
			for (int1 = 0; int1 < this.actualPos.size() - 1; ++int1) {
				PathFindBehavior2.DebugPt debugPt = (PathFindBehavior2.DebugPt)this.actualPos.get(int1);
				PathFindBehavior2.DebugPt debugPt2 = (PathFindBehavior2.DebugPt)this.actualPos.get(int1 + 1);
				LineDrawer.addLine(debugPt.x, debugPt.y, this.chr.z, debugPt2.x, debugPt2.y, this.chr.z, 1.0F, 1.0F, 1.0F, (String)null, true);
				LineDrawer.addLine(debugPt.x - 0.05F, debugPt.y - 0.05F, this.chr.z, debugPt.x + 0.05F, debugPt.y + 0.05F, this.chr.z, 1.0F, debugPt.climbing ? 1.0F : 0.0F, 0.0F, (String)null, false);
			}
		}
	}

	public void Succeeded(PolygonalMap2.Path path, Mover mover) {
		this.path.copyFrom(path);
		if (!this.isCancel) {
			this.chr.setPath2(this.path);
		}

		if (!path.isEmpty()) {
			PolygonalMap2.PathNode pathNode = (PolygonalMap2.PathNode)path.nodes.get(path.nodes.size() - 1);
			this.targetX = pathNode.x;
			this.targetY = pathNode.y;
			this.targetZ = pathNode.z;
		}

		this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
	}

	public void Failed(Mover mover) {
		this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
	}

	public boolean isMovingUsingPathFind() {
		return !this.bStopping && !this.isGoalNone() && !this.isCancel;
	}

	public class NPCData {
		public boolean doDirectMovement;
		public int MaxSteps;
		public int nextTileX;
		public int nextTileY;
		public int nextTileZ;
	}

	private static enum Goal {

		None,
		Character,
		Location,
		Sound,
		VehicleAdjacent,
		VehicleArea,
		VehicleSeat;

		private static PathFindBehavior2.Goal[] $values() {
			return new PathFindBehavior2.Goal[]{None, Character, Location, Sound, VehicleAdjacent, VehicleArea, VehicleSeat};
		}
	}
	public static enum BehaviorResult {

		Working,
		Failed,
		Succeeded;

		private static PathFindBehavior2.BehaviorResult[] $values() {
			return new PathFindBehavior2.BehaviorResult[]{Working, Failed, Succeeded};
		}
	}

	private static final class DebugPt {
		float x;
		float y;
		float z;
		boolean climbing;

		PathFindBehavior2.DebugPt init(float float1, float float2, float float3, boolean boolean1) {
			this.x = float1;
			this.y = float2;
			this.z = float3;
			this.climbing = boolean1;
			return this;
		}
	}

	public static final class PointOnPath {
		int pathIndex;
		float dist;
		float x;
		float y;
	}
}
