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

   public PathFindBehavior2(IsoGameCharacter var1) {
      this.goal = PathFindBehavior2.Goal.None;
      this.chr = var1;
   }

   public boolean isGoal2Location() {
      return this.goal == PathFindBehavior2.Goal.Location;
   }

   public void pathToCharacter(IsoGameCharacter var1) {
      this.isCancel = false;
      this.goal = PathFindBehavior2.Goal.Character;
      this.goalCharacter = var1;
      if (var1.getVehicle() != null) {
         Vector3f var2 = var1.getVehicle().chooseBestAttackPosition(var1, this.chr);
         if (var2 != null) {
            this.setData(var2.x, var2.y, var2.z);
            return;
         }
      }

      this.setData(var1.getX(), var1.getY(), var1.getZ());
   }

   public void pathToLocation(int var1, int var2, int var3) {
      this.isCancel = false;
      this.goal = PathFindBehavior2.Goal.Location;
      this.setData((float)var1 + 0.5F, (float)var2 + 0.5F, (float)var3);
   }

   public void pathToLocationF(float var1, float var2, float var3) {
      this.isCancel = false;
      this.goal = PathFindBehavior2.Goal.Location;
      this.setData(var1, var2, var3);
   }

   public void pathToVehicleArea(BaseVehicle var1, String var2) {
      Vector2 var3 = var1.getAreaCenter(var2);
      if (var3 == null) {
         this.targetX = this.chr.getX();
         this.targetY = this.chr.getY();
         this.targetZ = this.chr.getZ();
         this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
      } else {
         this.isCancel = false;
         this.goal = PathFindBehavior2.Goal.VehicleArea;
         this.goalVehicle = var1;
         this.goalVehicleArea = var2;
         this.setData(var3.getX(), var3.getY(), (float)((int)var1.getZ()));
         if (this.chr instanceof IsoPlayer && (int)this.chr.z == (int)this.targetZ && !PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, this.targetX, this.targetY, (int)this.targetZ, (IsoMovingObject)null)) {
            this.path.clear();
            this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
            this.path.addNode(this.targetX, this.targetY, this.targetZ);
            this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
         }

      }
   }

   public void pathToVehicleSeat(BaseVehicle var1, int var2) {
      VehicleScript.Position var3 = var1.getPassengerPosition(var2, "outside2");
      Vector3f var4;
      VehicleScript.Area var5;
      Vector2 var6;
      if (var3 != null) {
         var4 = new Vector3f();
         if (var3.area == null) {
            var4 = var1.getWorldPos(var3.offset, var4);
         } else {
            var5 = var1.script.getAreaById(var3.area);
            var6 = var1.areaPositionWorld(var5);
            var4.x = var6.x;
            var4.y = var6.y;
            var4.z = 0.0F;
         }

         var4.sub(this.chr.x, this.chr.y, this.chr.z);
         if (var4.length() < 2.0F) {
            var4 = var1.getWorldPos(var3.offset, new Vector3f());
            this.setData(var4.x(), var4.y(), (float)((int)var4.z()));
            if (this.chr instanceof IsoPlayer && (int)this.chr.z == (int)this.targetZ) {
               this.path.clear();
               this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
               this.path.addNode(this.targetX, this.targetY, this.targetZ);
               this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
               return;
            }
         }
      }

      var3 = var1.getPassengerPosition(var2, "outside");
      if (var3 == null) {
         VehiclePart var7 = var1.getPassengerDoor(var2);
         if (var7 == null) {
            this.targetX = this.chr.getX();
            this.targetY = this.chr.getY();
            this.targetZ = this.chr.getZ();
            this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
         } else {
            this.pathToVehicleArea(var1, var7.getArea());
         }
      } else {
         this.isCancel = false;
         this.goal = PathFindBehavior2.Goal.VehicleSeat;
         this.goalVehicle = var1;
         var4 = new Vector3f();
         if (var3.area == null) {
            var4 = var1.getWorldPos(var3.offset, var4);
         } else {
            var5 = var1.script.getAreaById(var3.area);
            var6 = var1.areaPositionWorld(var5);
            var4.x = var6.x;
            var4.y = var6.y;
            var4.z = 0.0F;
         }

         this.setData(var4.x(), var4.y(), (float)((int)var4.z()));
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

   private void setData(float var1, float var2, float var3) {
      this.startX = this.chr.getX();
      this.startY = this.chr.getY();
      this.startZ = this.chr.getZ();
      this.targetX = var1;
      this.targetY = var2;
      this.targetZ = var3;
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

   public boolean isTargetLocation(float var1, float var2, float var3) {
      return this.goal == PathFindBehavior2.Goal.Location && var1 == this.targetX && var2 == this.targetY && (int)var3 == (int)this.targetZ;
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
         State var1 = this.chr.getCurrentState();
         if (var1 != ClimbOverFenceState.instance() && var1 != ClimbOverFenceState2.instance() && var1 != ClimbThroughWindowState.instance() && var1 != ClimbThroughWindowState2.instance()) {
            if (this.chr.getVehicle() != null) {
               return Behavior.BehaviorResult.Failed;
            } else {
               this.chr.setPath2(this.path);
               if (this.goal == PathFindBehavior2.Goal.Character && this.chr instanceof IsoZombie && this.goalCharacter != null && this.goalCharacter.getVehicle() != null && this.chr.DistToSquared(this.targetX, this.targetY) < 16.0F) {
                  Vector3f var2 = this.goalCharacter.getVehicle().chooseBestAttackPosition(this.goalCharacter, this.chr);
                  if (var2 != null && (Math.abs(var2.x - this.targetX) > 0.1F || Math.abs(var2.y - this.targetY) > 0.1F)) {
                     if (Math.abs(this.goalCharacter.getVehicle().getCurrentSpeedKmHour()) > 0.1F) {
                        if (!PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, var2.x, var2.y, (int)this.targetZ, this.goalCharacter)) {
                           this.path.clear();
                           this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
                           this.path.addNode(var2.x, var2.y, var2.z);
                        } else if (IsoUtils.DistanceToSquared(var2.x, var2.y, this.targetX, this.targetY) > IsoUtils.DistanceToSquared(this.chr.x, this.chr.y, var2.x, var2.y)) {
                           return Behavior.BehaviorResult.Working;
                        }
                     } else if (((IsoZombie)this.chr).AllowRepathDelay <= 0.0F) {
                        ((IsoZombie)this.chr).AllowRepathDelay = 6.25F;
                        if (PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, var2.x, var2.y, (int)this.targetZ, (IsoMovingObject)null)) {
                           this.setData(var2.x, var2.y, this.targetZ);
                           return Behavior.BehaviorResult.Working;
                        }

                        this.path.clear();
                        this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
                        this.path.addNode(var2.x, var2.y, var2.z);
                     }
                  }
               }

               closestPointOnPath(this.chr.x, this.chr.y, this.chr.z, this.chr, this.path, pointOnPath);
               this.pathIndex = pointOnPath.pathIndex;
               PolygonalMap2.PathNode var11;
               if (this.pathIndex == this.path.nodes.size() - 2) {
                  var11 = (PolygonalMap2.PathNode)this.path.nodes.get(this.path.nodes.size() - 1);
                  if (IsoUtils.DistanceToSquared(this.chr.x, this.chr.y, var11.x, var11.y) <= 0.0025000002F) {
                     return Behavior.BehaviorResult.Succeeded;
                  }
               } else if (this.pathIndex < this.path.nodes.size() - 2 && pointOnPath.dist > 0.999F) {
                  ++this.pathIndex;
               }

               var11 = (PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex);
               PolygonalMap2.PathNode var3 = (PolygonalMap2.PathNode)this.path.nodes.get(this.pathIndex + 1);
               float var4 = var3.x;
               float var5 = var3.y;
               Vector2 var6 = tempVector2.set(var3.x - this.chr.x, var3.y - this.chr.y);
               var6.normalize();
               float var7 = this.chr.getPathSpeed();
               if (this.chr instanceof IsoZombie) {
                  ((IsoZombie)this.chr).bRunning = false;
                  if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
                     var7 = 0.08F;
                     ((IsoZombie)this.chr).bRunning = true;
                  }

                  this.chr.setIgnoreMovementForDirection(false);
               }

               float var8 = GameTime.instance.getMultiplier();
               float var9 = var7 * var8;
               float var10 = IsoUtils.DistanceTo(var4, var5, this.chr.x, this.chr.y);
               if (var9 >= var10) {
                  var7 *= var10 / var9;
                  ++this.pathIndex;
               }

               if (!(this.chr instanceof IsoZombie) && var10 >= 0.5F) {
                  if (this.checkDoorHoppableWindow(this.chr.x + var6.x * 0.5F, this.chr.y + var6.y * 0.5F, this.chr.z)) {
                     return Behavior.BehaviorResult.Failed;
                  }

                  if (var1 != this.chr.getCurrentState()) {
                     return Behavior.BehaviorResult.Working;
                  }
               }

               this.chr.MoveForward(var7, var6.x, var6.y, 1.0F);
               this.chr.faceLocation(var4 - 0.5F, var5 - 0.5F);
               this.chr.angle.set(var4 - this.chr.x, var5 - this.chr.y);
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

   private boolean checkDoorHoppableWindow(float var1, float var2, float var3) {
      IsoGridSquare var4 = this.chr.getCurrentSquare();
      if (var4 == null) {
         return false;
      } else {
         IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare((double)var1, (double)var2, (double)var3);
         if (var5 != null && var5 != var4) {
            int var6 = var5.x - var4.x;
            int var7 = var5.y - var4.y;
            if (var6 != 0 && var7 != 0) {
               return false;
            } else {
               IsoObject var8 = this.chr.getCurrentSquare().getDoorTo(var5);
               if (var8 instanceof IsoDoor) {
                  IsoDoor var9 = (IsoDoor)var8;
                  if (!var9.open) {
                     var9.ToggleDoor(this.chr);
                     if (!var9.open) {
                        return true;
                     }
                  }
               } else if (var8 instanceof IsoThumpable) {
                  IsoThumpable var12 = (IsoThumpable)var8;
                  if (!var12.open) {
                     var12.ToggleDoor(this.chr);
                     if (!var12.open) {
                        return true;
                     }
                  }
               }

               IsoWindow var13 = var4.getWindowTo(var5);
               if (var13 != null) {
                  if (var13.canClimbThrough(this.chr) && (!var13.isSmashed() || var13.isGlassRemoved())) {
                     this.chr.climbThroughWindow(var13);
                     return false;
                  } else {
                     return true;
                  }
               } else {
                  IsoThumpable var10 = var4.getWindowThumpableTo(var5);
                  if (var10 != null) {
                     if (var10.isBarricaded()) {
                        return true;
                     } else {
                        this.chr.climbThroughWindow(var10);
                        return false;
                     }
                  } else {
                     IsoObject var11 = var4.getWindowFrameTo(var5);
                     if (var11 != null) {
                        this.chr.climbThroughWindowFrame(var11);
                        return false;
                     } else {
                        if (var6 > 0 && var5.Is(IsoFlagType.HoppableW)) {
                           this.chr.StateMachineParams.put(0, IsoDirections.E);
                           this.chr.getStateMachine().changeState(ClimbOverFenceState.instance());
                        } else if (var6 < 0 && var4.Is(IsoFlagType.HoppableW)) {
                           this.chr.StateMachineParams.put(0, IsoDirections.W);
                           this.chr.getStateMachine().changeState(ClimbOverFenceState.instance());
                        } else if (var7 < 0 && var4.Is(IsoFlagType.HoppableN)) {
                           this.chr.StateMachineParams.put(0, IsoDirections.N);
                           this.chr.getStateMachine().changeState(ClimbOverFenceState.instance());
                        } else if (var7 > 0 && var5.Is(IsoFlagType.HoppableN)) {
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

   public static void closestPointOnPath(float var0, float var1, float var2, IsoMovingObject var3, PolygonalMap2.Path var4, PathFindBehavior2.PointOnPath var5) {
      IsoCell var6 = IsoWorld.instance.CurrentCell;
      var5.pathIndex = 0;
      float var7 = Float.MAX_VALUE;

      for(int var8 = 0; var8 < var4.nodes.size() - 1; ++var8) {
         PolygonalMap2.PathNode var9 = (PolygonalMap2.PathNode)var4.nodes.get(var8);
         PolygonalMap2.PathNode var10 = (PolygonalMap2.PathNode)var4.nodes.get(var8 + 1);
         if ((int)var9.z == (int)var2 || (int)var10.z == (int)var2) {
            float var11 = var9.x;
            float var12 = var9.y;
            float var13 = var10.x;
            float var14 = var10.y;
            double var15 = (double)((var0 - var11) * (var13 - var11) + (var1 - var12) * (var14 - var12)) / (Math.pow((double)(var13 - var11), 2.0D) + Math.pow((double)(var14 - var12), 2.0D));
            double var17 = (double)var11 + var15 * (double)(var13 - var11);
            double var19 = (double)var12 + var15 * (double)(var14 - var12);
            if (var15 <= 0.0D) {
               var17 = (double)var11;
               var19 = (double)var12;
               var15 = 0.0D;
            } else if (var15 >= 1.0D) {
               var17 = (double)var13;
               var19 = (double)var14;
               var15 = 1.0D;
            }

            int var21 = (int)var17 - (int)var0;
            int var22 = (int)var19 - (int)var1;
            IsoGridSquare var24;
            if ((var21 != 0 || var22 != 0) && Math.abs(var21) <= 1 && Math.abs(var22) <= 1) {
               IsoGridSquare var23 = var6.getGridSquare((int)var0, (int)var1, (int)var2);
               var24 = var6.getGridSquare((int)var17, (int)var19, (int)var2);
               if (var23 != null && var24 != null && var23.testCollideAdjacent(var3, var21, var22, 0)) {
                  continue;
               }
            }

            float var28 = var2;
            if (Math.abs(var21) <= 1 && Math.abs(var22) <= 1) {
               var24 = var6.getGridSquare((int)var9.x, (int)var9.y, (int)var9.z);
               IsoGridSquare var25 = var6.getGridSquare((int)var10.x, (int)var10.y, (int)var10.z);
               float var26 = var24 == null ? var9.z : PolygonalMap2.instance.getApparentZ(var24);
               float var27 = var25 == null ? var10.z : PolygonalMap2.instance.getApparentZ(var25);
               var28 = var26 + (var27 - var26) * (float)var15;
            }

            float var29 = IsoUtils.DistanceToSquared(var0, var1, var2, (float)var17, (float)var19, var28);
            if (var29 < var7) {
               var7 = var29;
               var5.pathIndex = var8;
               var5.dist = var15 == 1.0D ? 1.0F : (float)var15;
               var5.x = (float)var17;
               var5.y = (float)var19;
            }
         }
      }

   }

   void advanceAlongPath(float var1, float var2, float var3, float var4, PathFindBehavior2.PointOnPath var5) {
      closestPointOnPath(var1, var2, var3, this.chr, this.path, var5);

      for(int var6 = var5.pathIndex; var6 < this.path.nodes.size() - 1; ++var6) {
         PolygonalMap2.PathNode var7 = (PolygonalMap2.PathNode)this.path.nodes.get(var6);
         PolygonalMap2.PathNode var8 = (PolygonalMap2.PathNode)this.path.nodes.get(var6 + 1);
         double var9 = (double)IsoUtils.DistanceTo2D(var1, var2, var8.x, var8.y);
         if (!((double)var4 > var9)) {
            var5.pathIndex = var6;
            var5.dist += var4 / IsoUtils.DistanceTo2D(var7.x, var7.y, var8.x, var8.y);
            var5.x = var7.x + var5.dist * (var8.x - var7.x);
            var5.y = var7.y + var5.dist * (var8.y - var7.y);
            return;
         }

         var1 = var8.x;
         var2 = var8.y;
         var4 = (float)((double)var4 - var9);
         var5.dist = 0.0F;
      }

      var5.pathIndex = this.path.nodes.size() - 1;
      var5.dist = 1.0F;
      var5.x = ((PolygonalMap2.PathNode)this.path.nodes.get(var5.pathIndex)).x;
      var5.y = ((PolygonalMap2.PathNode)this.path.nodes.get(var5.pathIndex)).y;
   }

   public void render() {
      if (this.chr.getCurrentState() == WalkTowardState.instance()) {
         tempVector2.x = this.targetX - this.chr.x;
         tempVector2.y = this.targetY - this.chr.y;
         tempVector2.setLength(Math.min(100.0F, tempVector2.getLength()));
         LineDrawer.addLine(this.chr.x, this.chr.y, this.chr.z, this.chr.x + tempVector2.x, this.chr.y + tempVector2.y, this.targetZ, 1.0F, 1.0F, 1.0F, (String)null, true);
      } else if (this.chr.getPath2() != null) {
         int var1;
         PolygonalMap2.PathNode var2;
         float var4;
         float var5;
         for(var1 = 0; var1 < this.path.nodes.size() - 1; ++var1) {
            var2 = (PolygonalMap2.PathNode)this.path.nodes.get(var1);
            PolygonalMap2.PathNode var3 = (PolygonalMap2.PathNode)this.path.nodes.get(var1 + 1);
            var4 = 1.0F;
            var5 = 1.0F;
            if ((int)var2.z != (int)var3.z) {
               var5 = 0.0F;
            }

            LineDrawer.addLine(var2.x, var2.y, var2.z, var3.x, var3.y, var3.z, var4, var5, 0.0F, (String)null, true);
         }

         for(var1 = 0; var1 < this.path.nodes.size(); ++var1) {
            var2 = (PolygonalMap2.PathNode)this.path.nodes.get(var1);
            float var6 = 1.0F;
            var4 = 1.0F;
            var5 = 0.0F;
            if (var1 == 0) {
               var6 = 0.0F;
               var5 = 1.0F;
            }

            LineDrawer.addLine(var2.x - 0.05F, var2.y - 0.05F, var2.z, var2.x + 0.05F, var2.y + 0.05F, var2.z, var6, var4, var5, (String)null, false);
         }

         closestPointOnPath(this.chr.x, this.chr.y, this.chr.z, this.chr, this.path, pointOnPath);
         LineDrawer.addLine(pointOnPath.x - 0.05F, pointOnPath.y - 0.05F, this.chr.z, pointOnPath.x + 0.05F, pointOnPath.y + 0.05F, this.chr.z, 0.0F, 1.0F, 0.0F, (String)null, false);
      }
   }

   public void Succeeded(PolygonalMap2.Path var1, Mover var2) {
      this.path.copyFrom(var1);
      if (!this.isCancel) {
         this.chr.setPath2(this.path);
      }

      this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
   }

   public void Failed(Mover var1) {
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
