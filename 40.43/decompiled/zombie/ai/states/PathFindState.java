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

   public void enter(IsoGameCharacter var1) {
      var1.setPath((Path)null);
      PathfindManager.instance.AddJob(this, var1, (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), var1.getPathTargetX(), var1.getPathTargetY(), var1.getPathTargetZ());
      var1.getFinder().progress = AStarPathFinder.PathFindProgress.notyetfound;
      var1.setPathIndex(0);
   }

   public void execute(IsoGameCharacter var1) {
      if (var1 instanceof IsoZombie && !((IsoZombie)var1).bCrawling) {
         var1.setOnFloor(false);
         ((IsoZombie)var1).setRemoteMoveX(0.0F);
         ((IsoZombie)var1).setRemoteMoveY(0.0F);
         ((IsoZombie)var1).NetRemoteState = 1;
         ((IsoZombie)var1).movex = 0.0F;
         ((IsoZombie)var1).movey = 0.0F;
         if (GameClient.bClient && var1 != null) {
            return;
         }
      }

      if (var1 instanceof IsoZombie) {
         ((IsoZombie)var1).DoNetworkDirty();
      }

      if (IsoCamera.CamCharacter == var1) {
         boolean var2 = false;
      }

      if (var1.getFinder().progress != AStarPathFinder.PathFindProgress.notyetfound) {
         if (var1.getFinder().progress == AStarPathFinder.PathFindProgress.failed) {
            var1.setPathFindIndex(-1);
            this.Finished(var1);
         } else {
            if (var1.getFinder().progress == AStarPathFinder.PathFindProgress.found) {
               Path var22 = var1.getPath();
               if (var22 == null) {
                  var1.getStateMachine().RevertToPrevious();
                  return;
               }

               float var3 = var1.getX();
               float var4 = var1.getY();
               float var5 = var1.getZ();
               if (var1.getPathIndex() >= var22.getLength()) {
                  var22 = null;
                  this.Finished(var1);
                  return;
               }

               int var6 = var22.getX(var1.getPathIndex());
               int var7 = var22.getY(var1.getPathIndex());
               int var8 = var22.getZ(var1.getPathIndex());
               float var9 = 1.0F;
               float var10 = var1.getPathSpeed();
               int var11 = var1.getPathIndex();
               if (var22 != null) {
                  boolean var16 = false;
                  Vector2 var10000;
                  float var17;
                  float var18;
                  if ((int)var3 == var6 && (int)var4 == var7 && (int)var5 == var8) {
                     var17 = (float)var22.getX(var11) + 0.5F;
                     var18 = (float)var22.getY(var11) + 0.5F;
                     pathTarget.x = var17;
                     pathTarget.y = var18;
                     var1.angle.x = pathTarget.x;
                     var1.angle.y = pathTarget.y;
                     var1.angle.normalize();
                     if (var11 < var22.getLength() - 1) {
                        int var19 = var22.getX(var11 + 1);
                        int var20 = var22.getY(var11 + 1);
                        IsoWorld.instance.CurrentCell.getGridSquare(var19, var20, (int)var1.getZ());
                     }

                     if (var1 instanceof IsoZombie) {
                        if (var1.getPathTargetX() == (int)var1.getX() && var1.getPathTargetY() == (int)var1.getY() && var1.getPathTargetZ() == (int)var1.getZ()) {
                           var1.getStateMachine().changeState(ZombieStandState.instance());
                           return;
                        }
                     } else if (var1.getPathTargetX() == (int)var1.getX() && var1.getPathTargetY() == (int)var1.getY() && var1.getPathTargetZ() == (int)var1.getZ()) {
                        var1.getStateMachine().changeState(IdleState.instance());
                        return;
                     }

                     if (IsoUtils.DistanceManhatten(var3, var4, var17, var18) < var10 * 6.2F) {
                        var1.setPathIndex(var1.getPathIndex() + 1);
                        if (var1.getPathIndex() >= var22.getLength()) {
                           var22 = null;
                           this.Finished(var1);
                           return;
                        }

                        if (GameTime.instance.getMultiplier() >= 10.0F) {
                           var1.setX((float)var22.getX(var1.getPathIndex()) + 0.5F);
                           var1.setY((float)var22.getY(var1.getPathIndex()) + 0.5F);
                        } else {
                           var1.setX((float)var6 + 0.5F);
                           var1.setY((float)var7 + 0.5F);
                        }
                     }

                     var10000 = pathTarget;
                     var10000.x -= var3;
                     var10000 = pathTarget;
                     var10000.y -= var4;
                     if (pathTarget.getLength() > 0.0F) {
                        pathTarget.normalize();
                        var1.DirectionFromVector(pathTarget);
                     }

                     if (var1 instanceof IsoZombie) {
                        ((IsoZombie)var1).bRunning = false;
                        if (SandboxOptions.instance.Lore.Speed.getValue() == 1 && ((IsoZombie)var1).speedType != 3 || ((IsoZombie)var1).speedType == 1) {
                           var10 = 0.08F;
                           ((IsoZombie)var1).bRunning = true;
                        }

                        ((IsoZombie)var1).setIgnoreMovementForDirection(false);
                     }

                     var1.MoveForward(var10, pathTarget.x, pathTarget.y, var9);
                     var1.angle.x = pathTarget.x;
                     var1.angle.y = pathTarget.y;
                     var1.angle.normalize();
                     if (var1 instanceof IsoZombie) {
                        ((IsoZombie)var1).updateFrameSpeed();
                     }

                     if (var1 instanceof IsoZombie) {
                        ((IsoZombie)var1).reqMovement.x = var1.angle.x;
                        ((IsoZombie)var1).reqMovement.y = var1.angle.y;
                     }
                  } else {
                     var17 = (float)var6 + 0.5F;
                     var18 = (float)var7 + 0.5F;
                     pathTarget.x = var17;
                     pathTarget.y = var18;
                     var10000 = pathTarget;
                     var10000.x -= var3;
                     var10000 = pathTarget;
                     var10000.y -= var4;
                     if (pathTarget.getLength() > 0.0F) {
                        pathTarget.normalize();
                     }

                     var1.DirectionFromVector(pathTarget);
                     if (var1 instanceof IsoZombie) {
                        ((IsoZombie)var1).bRunning = false;
                        if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
                           var10 = 0.08F;
                           ((IsoZombie)var1).bRunning = true;
                        }
                     }

                     var1.MoveForward(var10, pathTarget.x, pathTarget.y, var9);
                     var1.angle.x = pathTarget.x;
                     var1.angle.y = pathTarget.y;
                     var1.angle.normalize();
                     if (var1 instanceof IsoZombie) {
                        ((IsoZombie)var1).reqMovement.x = var1.angle.x;
                        ((IsoZombie)var1).reqMovement.y = var1.angle.y;
                     }
                  }
               }
            }

         }
      }
   }

   public void exit(IsoGameCharacter var1) {
   }

   private void Finished(IsoGameCharacter var1) {
      var1.pathFinished();
   }

   public void Failed(Mover var1) {
      IsoGameCharacter var2 = (IsoGameCharacter)var1;
      var2.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
   }

   public void Succeeded(Path var1, Mover var2) {
      IsoGameCharacter var3 = (IsoGameCharacter)var2;
      var3.setPathIndex(0);
      Path var4 = var3.getPath();
      if (var4 != null) {
         for(int var5 = 0; var5 < var4.getLength(); ++var5) {
            Path.stepstore.push(var4.getStep(var5));
         }
      }

      var3.setPath(var1);
      var3.getFinder().progress = AStarPathFinder.PathFindProgress.found;
   }

   public String getName() {
      return "ZombiePathfinding";
   }
}
