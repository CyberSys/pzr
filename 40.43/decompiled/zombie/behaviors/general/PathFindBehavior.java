package zombie.behaviors.general;

import zombie.GameTime;
import zombie.PathfindManager;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.AStarPathFinderResult;
import zombie.ai.astar.IPathfinder;
import zombie.ai.astar.Mover;
import zombie.ai.astar.Path;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCamera;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoPushableObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public class PathFindBehavior extends Behavior implements IPathfinder {
   public Path path;
   public boolean bDoClosest = false;
   public int pathIndex = 0;
   public int sx;
   public int sy;
   public int sz;
   public int tx;
   public int ty;
   public int tz;
   public int osx;
   public int osy;
   public int osz;
   public int otx;
   public int oty;
   public int otz;
   boolean useScriptXY = false;
   public AStarPathFinderResult finder = new AStarPathFinderResult();
   public String name = "unnamed";
   public int lastCancel = 1000000;
   IsoGameCharacter chr = null;
   static Vector2 tempo = new Vector2(0.0F, 0.0F);

   public String getName() {
      return this.name;
   }

   public PathFindBehavior() {
   }

   public PathFindBehavior(String var1) {
      this.name = var1;
   }

   public void setData(IsoGameCharacter var1, int var2, int var3, int var4) {
      this.sx = this.osx = (int)var1.getX();
      this.sy = this.osy = (int)var1.getY();
      this.sz = this.osz = (int)var1.getZ();
      this.tx = this.otx = var2;
      this.ty = this.oty = var3;
      this.tz = this.otz = var4;
   }

   public PathFindBehavior(boolean var1) {
      this.useScriptXY = var1;
   }

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      if (var2.getCurrentSquare() == null) {
         return Behavior.BehaviorResult.Failed;
      } else {
         this.chr = var2;
         this.finder.maxSearchDistance = 800;
         if (var2 instanceof IsoSurvivor && this.lastCancel > 120 && ((IsoSurvivor)var2).dangerTile - ((IsoSurvivor)var2).lastDangerTile > 30 && ((IsoSurvivor)var2).dangerTile > 0) {
            this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
            this.path = null;
            this.sx = (int)var2.getX();
            this.sy = (int)var2.getY();
            this.sz = (int)var2.getZ();
            this.lastCancel = 0;
            return Behavior.BehaviorResult.Working;
         } else {
            ++this.lastCancel;
            if (this.tx == 0 && this.ty == 0 && this.tz == 0) {
               this.reset();
               this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
               return Behavior.BehaviorResult.Failed;
            } else if (this.sx == this.tx && this.sy == this.ty && this.sz == this.tz) {
               this.reset();
               this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
               return Behavior.BehaviorResult.Succeeded;
            } else {
               boolean var3;
               boolean var4;
               int var5;
               int var6;
               int var7;
               int var21;
               if (this.tx != this.otx || this.ty != this.oty || this.tz != this.otz || this.sx != this.osx || this.sy != this.osy || this.sz != this.osz) {
                  if (this.tx == this.otx && this.ty == this.oty && this.tz == this.otz && this.path != null) {
                     var3 = false;
                     var4 = false;

                     for(var21 = this.pathIndex - 1; var21 < this.path.getLength(); ++var21) {
                        if (var21 < 0) {
                           var21 = 0;
                        }

                        var5 = this.path.getX(var21);
                        var6 = this.path.getY(var21);
                        var7 = this.path.getZ(var21);
                        if ((int)var2.getX() == this.sx && (int)var2.getY() == this.sy && (int)var2.getZ() == this.sz) {
                           var3 = true;
                           break;
                        }

                        if (var5 == (int)var2.getX() && var6 == (int)var2.getY() && var7 == (int)var2.getZ()) {
                           var3 = true;
                           break;
                        }

                        if (Math.abs(var5 - (int)var2.getX()) <= 1 && Math.abs(var6 - (int)var2.getY()) <= 1 && Math.abs(var7 - (int)var2.getZ()) <= 1) {
                           var3 = true;
                           break;
                        }
                     }

                     if (var3) {
                        this.pathIndex = var21;
                     } else {
                        this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
                        this.path = null;
                     }
                  } else {
                     this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
                     this.path = null;
                  }
               }

               if ((int)var2.getX() == this.tx && (int)var2.getY() == this.ty && (int)var2.getZ() == this.tz && Math.abs(var2.getX() - (float)this.tx - 0.5F) < 0.2F && Math.abs(var2.getY() - (float)this.ty - 0.5F) < 0.2F) {
                  if (var2 == IsoCamera.CamCharacter) {
                     var3 = false;
                  }

                  this.reset();
                  this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
                  return Behavior.BehaviorResult.Succeeded;
               } else {
                  if (this.path == null) {
                     if (this.finder.progress == AStarPathFinder.PathFindProgress.notyetfound) {
                        return Behavior.BehaviorResult.Working;
                     }

                     if (this.finder.progress == AStarPathFinder.PathFindProgress.failed) {
                        if (var2 == IsoCamera.CamCharacter) {
                           var3 = false;
                        }

                        this.reset();
                        this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
                        return Behavior.BehaviorResult.Failed;
                     }

                     if (this.finder.progress == AStarPathFinder.PathFindProgress.notrunning) {
                        if (this.sx != (int)var2.getX()) {
                           this.sx = (int)var2.getX();
                        }

                        if (this.sy != (int)var2.getY()) {
                           this.sy = (int)var2.getY();
                        }

                        if (this.sz != (int)var2.getZ()) {
                           this.sz = (int)var2.getZ();
                        }

                        PathfindManager.instance.AddJob(this, var2, this.sx, this.sy, this.sz, this.tx, this.ty, this.tz, this.bDoClosest);
                        this.osx = this.sx;
                        this.osy = this.sy;
                        this.osz = this.sz;
                        this.otx = this.tx;
                        this.oty = this.ty;
                        this.otz = this.tz;
                        this.finder.progress = AStarPathFinder.PathFindProgress.notyetfound;
                        return Behavior.BehaviorResult.Working;
                     }
                  }

                  if (this.path == null) {
                     if (var2 == IsoCamera.CamCharacter) {
                        var3 = false;
                     }

                     this.reset();
                     this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
                     return Behavior.BehaviorResult.Failed;
                  } else {
                     int var22 = this.pathIndex + 6;
                     if (var22 > this.path.getLength()) {
                        var22 = this.path.getLength();
                     }

                     int var9;
                     int var10;
                     for(var21 = this.pathIndex + 1; var21 < var22; ++var21) {
                        var5 = this.path.getX(var21);
                        var6 = this.path.getY(var21);
                        var7 = this.path.getZ(var21);
                        IsoGridSquare var8 = var2.getCell().getGridSquare(var5, var6, var7);
                        if (var2.getCurrentSquare() == null) {
                           return Behavior.BehaviorResult.Failed;
                        }

                        if (var8 == null) {
                           return Behavior.BehaviorResult.Failed;
                        }

                        if (var2.getCurrentSquare().getMovingObjects().size() < var8.getMovingObjects().size()) {
                           var9 = 0;
                           if (!var8.getMovingObjects().isEmpty()) {
                              for(var10 = 0; var10 < var8.getMovingObjects().size(); ++var10) {
                                 if (this.lastCancel > 120 && var8.getMovingObjects().get(var10) instanceof IsoZombie) {
                                    ++var9;
                                 }
                              }
                           }

                           if (var9 > 3) {
                              this.path = null;
                              this.sx = (int)var2.getX();
                              this.sy = (int)var2.getY();
                              this.sz = (int)var2.getZ();
                              this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
                              this.lastCancel = 0;
                              return Behavior.BehaviorResult.Working;
                           }
                        }
                     }

                     var4 = false;

                     int var27;
                     for(var5 = 0; var5 < this.path.getLength(); ++var5) {
                        var6 = this.path.getX(var5);
                        var7 = this.path.getY(var5);
                        var27 = this.path.getZ(var5);
                        if ((int)var2.getX() == this.sx && (int)var2.getY() == this.sy && (int)var2.getZ() == this.sz) {
                           var4 = true;
                           break;
                        }

                        if (var6 == (int)var2.getX() && var7 == (int)var2.getY() && var27 == (int)var2.getZ()) {
                           var4 = true;
                           break;
                        }

                        if (Math.abs(var6 - (int)var2.getX()) <= 1 && Math.abs(var7 - (int)var2.getY()) <= 1 && Math.abs(var27 - (int)var2.getZ()) <= 1) {
                           var4 = true;
                           break;
                        }
                     }

                     boolean var24;
                     if (!var4) {
                        if (var2 == IsoCamera.CamCharacter) {
                           var24 = false;
                        }

                        this.reset();
                        this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
                        this.path = null;
                        return Behavior.BehaviorResult.Failed;
                     } else {
                        var2.setPath(this.path);
                        int var11;
                        float var25;
                        float var26;
                        float var28;
                        float var30;
                        if (((IsoLivingCharacter)var2).isCollidedWithPushableThisFrame()) {
                           IsoPushableObject var23 = ((IsoSurvivor)var2).collidePushable;
                           tempo.x = var2.getMovementLastFrame().x;
                           tempo.y = var2.getMovementLastFrame().y;
                           tempo.normalize();
                           tempo.rotate((float)Math.toRadians(-90.0D));
                           var25 = var23.x + tempo.x * 5.0F;
                           var26 = var23.y + tempo.y * 5.0F;
                           var28 = var23.x - tempo.x * 5.0F;
                           var30 = var23.y - tempo.y * 5.0F;
                           var10 = LosUtil.lineClearCollideCount(this.chr, IsoWorld.instance.CurrentCell, (int)var23.x, (int)var23.y, (int)var23.z, (int)var25, (int)var26, (int)var23.z);
                           var11 = LosUtil.lineClearCollideCount(this.chr, IsoWorld.instance.CurrentCell, (int)var23.x, (int)var23.y, (int)var23.z, (int)var28, (int)var30, (int)var23.z);
                           if (var10 > var11) {
                              var23.setImpulsex(var23.getImpulsex() + tempo.x * 0.1F);
                              var23.setImpulsey(var23.getImpulsey() + tempo.y * 0.1F);
                           } else if (var10 < var11) {
                              var23.setImpulsex(var23.getImpulsex() - tempo.x * 0.1F);
                              var23.setImpulsey(var23.getImpulsey() - tempo.y * 0.1F);
                           }
                        }

                        if (var2.isCollidedThisFrame()) {
                           if (var2 == IsoCamera.CamCharacter) {
                              var24 = false;
                           }

                           if (var2.isCollidedE() || var2.isCollidedN() || var2.isCollidedS() || var2.isCollidedW()) {
                              var24 = false;
                              var6 = this.path.getX(this.pathIndex);
                              var7 = this.path.getY(this.pathIndex);
                              var27 = var6 - (int)var2.getX();
                              var9 = var7 - (int)var2.getY();
                              if (var27 > 0 && var2.isCollidedE()) {
                                 var24 = true;
                              } else if (var27 < 0 && var2.isCollidedW()) {
                                 var24 = true;
                              }

                              if (var9 > 0 && var2.isCollidedS()) {
                                 var24 = true;
                              } else if (var9 < 0 && var2.isCollidedN()) {
                                 var24 = true;
                              }

                              if (var24) {
                                 if (var2 == IsoCamera.CamCharacter) {
                                    boolean var31 = false;
                                 }

                                 this.reset();
                                 this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
                                 return Behavior.BehaviorResult.Failed;
                              }
                           }
                        }

                        if (this.pathIndex >= this.path.getLength()) {
                           return Behavior.BehaviorResult.Succeeded;
                        } else {
                           Vector2 var29 = new Vector2((float)this.tx, (float)this.ty);
                           var25 = (float)this.path.getX(this.pathIndex);
                           var26 = (float)this.path.getY(this.pathIndex);
                           var28 = 1.0F;
                           var30 = var2.getPathSpeed();
                           var10 = this.path.getX(this.pathIndex);
                           var11 = this.path.getY(this.pathIndex);
                           int var12 = this.path.getZ(this.pathIndex);
                           float var13;
                           float var14;
                           if ((int)var2.getX() == var10 && (int)var2.getY() == var11 && (int)var2.getZ() == var12) {
                              var13 = (float)this.path.getX(this.pathIndex) + 0.5F;
                              var14 = (float)this.path.getY(this.pathIndex) + 0.5F;
                              var29.x = var13;
                              var29.y = var14;
                              boolean var15 = true;
                              if (IsoUtils.DistanceManhatten(var2.getX(), var2.getY(), var13, var14) < var30 * GameTime.instance.getMultiplier() * 2.0F) {
                                 ++this.pathIndex;
                                 if (this.pathIndex >= this.path.getLength()) {
                                    this.path = null;
                                    this.reset();
                                    this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
                                    return Behavior.BehaviorResult.Succeeded;
                                 }

                                 if (GameTime.instance.getMultiplier() >= 10.0F && this.path.getZ(this.pathIndex) == (int)var2.getZ()) {
                                    var2.FaceNextPathNode(this.path.getX(this.pathIndex), this.path.getY(this.pathIndex));
                                    var15 = false;
                                    IsoGridSquare var16 = IsoWorld.instance.CurrentCell.getGridSquare(this.path.getX(this.pathIndex), this.path.getY(this.pathIndex), (int)var2.getZ());
                                    if (var16 != null && var2.getCurrentSquare() != null) {
                                       IsoObject var17 = var2.getCurrentSquare().getDoorTo(var16);
                                       boolean var18 = false;
                                       if (var17 instanceof IsoThumpable) {
                                          IsoThumpable var19 = (IsoThumpable)var17;
                                          if (!var19.IsOpen() && (var19.isBarricaded() || var19.isLocked())) {
                                             var18 = true;
                                          }
                                       } else if (var17 instanceof IsoDoor) {
                                          IsoDoor var37 = (IsoDoor)var17;
                                          if (!var37.IsOpen() && (var37.isBarricaded() || var37.isLocked())) {
                                             var18 = true;
                                          }
                                       }

                                       if (var18) {
                                          this.reset();
                                          return Behavior.BehaviorResult.Failed;
                                       }
                                    }

                                    var2.setX((float)this.path.getX(this.pathIndex) + 0.5F);
                                    var2.setY((float)this.path.getY(this.pathIndex) + 0.5F);
                                 } else {
                                    var2.setX((float)this.path.getX(this.pathIndex - 1) + 0.5F);
                                    var2.setY((float)this.path.getY(this.pathIndex - 1) + 0.5F);
                                 }

                                 int var32 = this.path.getX(this.pathIndex);
                                 int var34 = this.path.getY(this.pathIndex);
                                 var29.x = (float)var32;
                                 var29.y = (float)var34;
                                 IsoGridSquare var36 = IsoWorld.instance.CurrentCell.getGridSquare(var32, var34, (int)var2.getZ());
                                 if (var36 != null) {
                                    IsoObject var38 = var2.getCurrentSquare().getDoorTo(var36);
                                    if (var38 != null) {
                                       if (var38 instanceof IsoThumpable && !((IsoThumpable)var38).open) {
                                          ((IsoThumpable)var38).ToggleDoor(var2);
                                       } else if (var38 instanceof IsoDoor && !((IsoDoor)var38).open) {
                                          ((IsoDoor)var38).ToggleDoor(var2);
                                       }
                                    }

                                    IsoWindow var20 = var2.getCurrentSquare().getWindowTo(var36);
                                    if (var20 != null) {
                                       if (var20.Locked) {
                                          var20.WeaponHit(var2, (HandWeapon)null);
                                       } else {
                                          var20.ToggleWindow(var2);
                                       }
                                    }
                                 }
                              }

                              IsoDirections var33 = var2.dir;
                              if (var15) {
                                 var2.FaceNextPathNode(this.path.getX(this.pathIndex), this.path.getY(this.pathIndex));
                              }

                              if (IsoCamera.CamCharacter == var2 && IsoDirections.reverse(var33) == var2.dir) {
                                 boolean var35 = false;
                              }

                              var29.x -= var2.getX();
                              var29.y -= var2.getY();
                              if (var29.getLength() > 0.0F) {
                                 var29.normalize();
                              }

                              if (GameTime.instance.getMultiplier() < 10.0F) {
                                 var2.def.Finished = false;
                                 var2.MoveForward(var30, var29.x, var29.y, var28);
                              }
                           } else {
                              var2.FaceNextPathNode(this.path.getX(this.pathIndex), this.path.getY(this.pathIndex));
                              var13 = (float)this.path.getX(this.pathIndex) + 0.5F;
                              var14 = (float)this.path.getY(this.pathIndex) + 0.5F;
                              var29.x = var13;
                              var29.y = var14;
                              var29.x -= var2.getX();
                              var29.y -= var2.getY();
                              if (var29.getLength() > 0.0F) {
                                 var29.normalize();
                              }

                              var2.def.Finished = false;
                              var2.MoveForward(var30, var29.x, var29.y, var28);
                           }

                           if (this.useScriptXY) {
                              var2.setScriptnx(var2.getNx());
                              var2.setScriptny(var2.getNy());
                           }

                           return Behavior.BehaviorResult.Working;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void reset() {
      this.path = null;
      this.sx = 0;
      this.sy = 0;
      this.sz = 0;
      this.tx = 0;
      this.ty = 0;
      this.tz = 0;
      this.osx = 0;
      this.osy = 0;
      this.osz = 0;
      this.otx = 0;
      this.oty = 0;
      this.otz = 0;
      if (this.finder != null) {
         this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
      }

      this.lastCancel = 1000000000;
   }

   public boolean running(IsoGameCharacter var1) {
      if (this.finder == null) {
         return false;
      } else {
         return this.finder.progress != AStarPathFinder.PathFindProgress.notrunning;
      }
   }

   public boolean valid() {
      return true;
   }

   public int renderDebug(int var1) {
      short var2 = 300;
      byte var3 = 50;
      TextManager.instance.DrawString(UIFont.Small, (double)var2, (double)var3, "Pathfind", 1.0D, 1.0D, 1.0D, 1.0D);
      int var11 = var3 + 30;
      if (this.path == null) {
         return var1;
      } else {
         int var4 = -1000;
         int var5 = 0;
         int var6 = 0;

         for(int var7 = this.pathIndex; var7 < this.path.getLength(); ++var7) {
            Integer var8 = this.path.getX(var7);
            Integer var9 = this.path.getY(var7);
            Integer var10 = this.path.getZ(var7);
            if (var4 != -1000) {
               var8 = var8 - var4;
               var9 = var9 - var5;
               var10 = var10 - var6;
            }

            var4 = var8;
            var5 = var9;
            var6 = var10;
            TextManager.instance.DrawString(UIFont.Small, (double)var2, (double)var11, "PathNode " + var7 + " - x: " + var8 + " y: " + var9 + " z: " + var10, 0.0D, 1.0D, 1.0D, 0.4000000059604645D);
            var11 += 30;
         }

         return var1;
      }
   }

   public void Failed(Mover var1) {
      this.finder.progress = AStarPathFinder.PathFindProgress.failed;
      DebugLog.log("Pathfind failed");
      this.reset();
   }

   public void Succeeded(Path var1, Mover var2) {
      Path var3 = this.path;
      if (var3 != null) {
         for(int var4 = 0; var4 < var3.getLength(); ++var4) {
            Path.stepstore.push(var3.getStep(var4));
         }
      }

      this.path = var1;
      this.lastCancel = 0;
      this.pathIndex = 0;
      this.finder.progress = AStarPathFinder.PathFindProgress.found;
   }
}
