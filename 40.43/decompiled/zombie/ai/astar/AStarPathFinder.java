package zombie.ai.astar;

import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;
import zombie.GameWindow;
import zombie.PathfindManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.utils.ExpandableBooleanList;
import zombie.gameStates.IngameState;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirectionSet;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;

public class AStarPathFinder {
   public static int IDToUseInSort = 0;
   static Vector2 zfindangle = new Vector2();
   public boolean allowDiagMovement;
   public int cyclesPerSlice = 3;
   public int delay = 0;
   public int maxSearchDistance;
   public AStarPathFinder.PathFindProgress progress;
   public int startX;
   public int startY;
   public int startZ;
   public int targetX;
   public int targetY;
   public int targetZ;
   public static int NumPathfinds = 0;
   static AStarPathMap astarmap;
   Path foundPath;
   int maxDepth;
   Mover mover;
   public IsoGameCharacter character;
   private AStarHeuristic heuristic;
   private AStarPathFinder.SortedList open;
   private ExpandableBooleanList closed;
   public ArrayList IsoGridSquaresUnfurled;
   static int unfurledmax = 0;
   IsoDirectionSet set;
   private boolean bClosest;
   static Vector2 temp = new Vector2();

   private void PathfindIsoGridSquare(int var1, IsoGridSquare var2, Mover var3, IsoGridSquare var4, int var5, int var6, int var7, int var8) {
      if (var4.searchData[var1] == null) {
         var4.searchData[var1] = new SearchData();
      }

      float var9 = var2.searchData[var1].cost + astarmap.getMovementCost(var3, var2.x, var2.y, var2.z, var4.x, var4.y, var4.z);
      if (var9 < var4.searchData[var1].cost) {
         if (this.inOpenList(var4)) {
            this.removeFromOpen(var1, var4);
         }

         if (this.inClosedList(var4)) {
            this.removeFromClosed(var4);
         }
      }

      if (!this.inOpenList(var4) && !this.inClosedList(var4)) {
         var4.searchData[var1].cost = var9;
         var4.searchData[var1].heuristic = this.getHeuristicCost(var3, var4.x, var4.y, var4.z, var5, var6, var7);
         this.maxDepth = Math.max(this.maxDepth, var4.setParent(var1, 0, var2));
         this.addToOpen(var1, 0, var4);
         ++var8;
      }

   }

   public void findPathActualZombie(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      zfindangle.x = (float)var5 + 0.5F;
      zfindangle.y = (float)var6 + 0.5F;
      Vector2 var10000 = zfindangle;
      var10000.x -= ((IsoZombie)var1).getX();
      var10000 = zfindangle;
      var10000.y -= ((IsoZombie)var1).getY();
      zfindangle.normalize();
      IsoDirections var8 = IsoDirections.fromAngle(zfindangle);
      IsoGridSquare var9 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      IsoGridSquare var10 = IsoWorld.instance.CurrentCell.getGridSquare(var5, var6, var7);
      boolean var11 = true;

      while(var9 != var10) {
      }

   }

   private boolean IsInDirection(IsoGridSquare var1, IsoDirections var2) {
      if (this.targetX == -1) {
         return false;
      } else if (var1.z != this.targetZ) {
         return false;
      } else {
         switch(var2) {
         case N:
            if (var1.x == this.targetX && var1.y > this.targetY) {
               return true;
            }
            break;
         case S:
            if (var1.x == this.targetX && var1.y < this.targetY) {
               return true;
            }
            break;
         case NW:
            if (this.targetX < var1.x && this.targetY < var1.y && this.targetX - var1.x == this.targetY - var1.y) {
               return true;
            }
            break;
         case NE:
            if (this.targetX > var1.x && this.targetY < var1.y && -(this.targetX - var1.x) == this.targetY - var1.y) {
               return true;
            }
            break;
         case SW:
            if (this.targetX < var1.x && this.targetY > var1.y && this.targetX - var1.x == -(this.targetY - var1.y)) {
               return true;
            }
            break;
         case SE:
            if (this.targetX > var1.x && this.targetY > var1.y && -(this.targetX - var1.x) == -(this.targetY - var1.y)) {
               return true;
            }
            break;
         case E:
            if (this.targetX > var1.x && this.startY == this.targetY) {
               return true;
            }
            break;
         case W:
            if (this.targetX < var1.x && this.startY == this.targetY) {
               return true;
            }
         }

         return false;
      }
   }

   public AStarPathFinder(IsoGameCharacter var1, AStarPathMap var2, int var3, boolean var4, AStarHeuristic var5) {
      this.progress = AStarPathFinder.PathFindProgress.notrunning;
      this.startX = 0;
      this.startY = 0;
      this.startZ = 0;
      this.targetX = 0;
      this.targetY = 0;
      this.targetZ = 0;
      this.maxDepth = 0;
      this.character = null;
      this.open = new AStarPathFinder.SortedList();
      this.closed = new ExpandableBooleanList(1);
      this.IsoGridSquaresUnfurled = new ArrayList(0);
      this.set = new IsoDirectionSet();
      this.character = var1;
      astarmap = var2;
      this.heuristic = var5;
      this.maxSearchDistance = var3;
      this.allowDiagMovement = var4;
   }

   public AStarPathFinder.PathFindProgress Cycle(int var1) {
      if (astarmap == null) {
         astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
         IsoWorld.instance.CurrentCell.setPathMap(astarmap);
      }

      ++NumPathfinds;
      this.foundPath = this.findPath(var1, this.mover, this.startX, this.startY, this.startZ, this.targetX, this.targetY, this.targetZ);
      --NumPathfinds;
      if (this.foundPath != null && this.foundPath.getLength() > 0) {
         this.progress = AStarPathFinder.PathFindProgress.found;
      } else {
         this.progress = AStarPathFinder.PathFindProgress.failed;
      }

      return this.progress;
   }

   public AStarPathFinder.PathFindProgress Cycle(int var1, PathfindManager.PathfindJob var2) {
      if (astarmap == null) {
         astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
         IsoWorld.instance.CurrentCell.setPathMap(astarmap);
      }

      ++NumPathfinds;
      this.foundPath = this.findPath(var1, this.mover, this.startX, this.startY, this.startZ, this.targetX, this.targetY, this.targetZ, var2);
      --NumPathfinds;
      if (this.foundPath != null && this.foundPath.getLength() > 0) {
         this.progress = AStarPathFinder.PathFindProgress.found;
      } else {
         this.progress = AStarPathFinder.PathFindProgress.failed;
      }

      return this.progress;
   }

   public Path findPath(int var1, Mover var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this.maxSearchDistance = 600;
      this.IsoGridSquaresUnfurled.clear();
      this.targetX = var6;
      this.targetY = var7;
      this.targetZ = var8;
      byte var9 = 0;
      if (astarmap == null) {
         astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
         IsoWorld.instance.CurrentCell.setPathMap(astarmap);
      }

      if (var6 >= 0 && var7 >= 0 && var6 < IsoWorld.instance.CurrentCell.getWidthInTiles() && var7 < IsoWorld.instance.CurrentCell.getHeightInTiles()) {
         try {
            if (var2 == IsoCamera.CamCharacter) {
               boolean var10 = true;
            }

            IsoGridSquare var23 = astarmap.getNode(var3, var4, var5);
            if (var23 == null) {
               return null;
            } else {
               IsoGridSquare var11 = IsoWorld.instance.CurrentCell.getGridSquare(var6, var7, var8);
               if (var11 == null && !this.bClosest) {
                  return null;
               } else if (!var11.isFree(false) && !this.bClosest) {
                  return null;
               } else if (var11.getN() == null && var11.getS() == null && var11.getE() == null && var11.getW() == null && !this.bClosest) {
                  return null;
               } else {
                  var23.searchData[var1].cost = 0.0F;
                  var23.searchData[var1].depth = 0;
                  var23.searchData[var1].parent = null;
                  this.open.clear();
                  this.closed.clear();
                  this.open.add(var1, 0, var23);
                  IsoGridSquare var12 = astarmap.getNode(var6, var7, var8);
                  if (var12.searchData[var1] == null) {
                     var12.searchData[var1] = new SearchData();
                  }

                  var12.searchData[var1].parent = null;
                  this.maxDepth = 0;
                  Thread var13 = Thread.currentThread();

                  while(true) {
                     int var16;
                     int var17;
                     if (this.maxDepth < this.maxSearchDistance && this.open.size() != 0) {
                        if (PathfindManager.instance.threads[var1].SwitchingCells) {
                           return null;
                        }

                        if (var2 == IsoCamera.CamCharacter) {
                           boolean var14 = false;
                        }

                        if (IngameState.DebugPathfinding && var2 == IsoCamera.CamCharacter && !(var13 instanceof PathfindManager.PathfindThread) || IngameState.AlwaysDebugPathfinding) {
                           Core.getInstance().StartFrame();
                        }

                        IsoGridSquare var24 = this.getFirstInOpen();
                        if (IngameState.AlwaysDebugPathfinding && !this.IsoGridSquaresUnfurled.contains(var24)) {
                           this.IsoGridSquaresUnfurled.add(var24);
                        }

                        if (var24 != var12) {
                           this.removeFromOpen(var1, var24);
                           this.addToClosed(var24);

                           for(int var26 = -1; var26 <= 1; ++var26) {
                              for(var16 = -1; var16 <= 1; ++var16) {
                                 for(var17 = -1; var17 <= 1; ++var17) {
                                    if (var17 == -1 && var16 == 0 && var26 == 0) {
                                       boolean var27 = false;
                                    }

                                    if (astarmap.isValidLocation(var2, var24.x, var24.y, var24.z, var24.x + var16, var24.y + var17, var24.z + var26, var24.x, var24.y, var24.z)) {
                                       IsoGridSquare var28 = astarmap.getNode(var24.x + var16, var24.y + var17, var24.z + var26);
                                       if (var28 != null) {
                                          this.PathfindIsoGridSquare(var1, var24, var2, var28, var6, var7, var8, var9);
                                       }
                                    }
                                 }
                              }
                           }

                           if (IngameState.AlwaysDebugPathfinding) {
                              Core.getInstance().EndFrame(0);
                              Core.getInstance().StartFrameUI();
                              this.renderOverhead();
                              Core.getInstance().EndFrameUI();
                              Display.update();
                              Thread.sleep(30L);
                           }
                           continue;
                        }
                     }

                     if (this.IsoGridSquaresUnfurled.size() > unfurledmax) {
                        unfurledmax = this.IsoGridSquaresUnfurled.size();
                     }

                     if (var12.searchData[var1].parent == null) {
                        return null;
                     }

                     Path var25 = new Path();
                     IsoGridSquare var15 = var12;
                     var25.cost += var12.searchData[var1].cost;
                     var16 = var12.x;
                     var17 = var12.y;

                     int var18;
                     for(var18 = var12.z; var15 != var23; var15 = var15.searchData[var1].parent) {
                        int var19 = var15.x;
                        int var20 = var15.y;
                        int var21 = var15.z;
                        if (var19 == var16 && var20 == var17 && var21 == var18) {
                           var25.prependStep(var16, var17, var18);
                        }

                        for(; var19 != var16 || var20 != var17 || var21 != var18; var25.prependStep(var16, var17, var18)) {
                           if (var19 > var16) {
                              ++var16;
                           } else if (var19 < var16) {
                              --var16;
                           }

                           if (var20 > var17) {
                              ++var17;
                           } else if (var20 < var17) {
                              --var17;
                           }

                           if (var21 > var18) {
                              ++var18;
                           } else if (var21 < var18) {
                              --var18;
                           }
                        }

                        var16 = var15.x;
                        var17 = var15.y;
                        var18 = var15.z;
                     }

                     if (var3 == var16 && var4 == var17 && var5 == var18) {
                        var25.prependStep(var16, var17, var18);
                     }

                     for(; var16 != var3 || var17 != var4 || var18 != var5; var25.prependStep(var16, var17, var18)) {
                        if (var3 > var16) {
                           ++var16;
                        } else if (var3 < var16) {
                           --var16;
                        }

                        if (var4 > var17) {
                           ++var17;
                        } else if (var4 < var17) {
                           --var17;
                        }

                        if (var5 > var18) {
                           ++var18;
                        } else if (var5 < var18) {
                           --var18;
                        }
                     }

                     if (IngameState.DebugPathfinding && var2 == IsoCamera.CamCharacter && !(var13 instanceof PathfindManager.PathfindThread) || IngameState.AlwaysDebugPathfinding) {
                        Core.getInstance().StartFrame();
                        this.renderOverhead();
                        this.renderPath(var25);
                        Core.getInstance().EndFrame(0);
                        Display.update();
                        Thread.sleep(2000L);
                        IngameState.DebugPathfinding = false;
                     }

                     return var25;
                  }
               }
            }
         } catch (Exception var22) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, var22);
            return null;
         }
      } else {
         return null;
      }
   }

   public Path findPath(int var1, Mover var2, int var3, int var4, int var5, int var6, int var7, int var8, PathfindManager.PathfindJob var9) {
      int var10 = var3;
      int var11 = var4;
      int var12 = var6;
      int var13 = var7;
      temp.x = (float)(var6 - var3);
      temp.y = (float)(var7 - var4);
      temp.setLength((float)IsoWorld.instance.CurrentCell.ChunkMap[0].getWidthInTiles() / 2.5F);
      this.maxSearchDistance = 600;
      this.IsoGridSquaresUnfurled.clear();
      this.targetX = var6;
      this.targetY = var7;
      this.targetZ = var8;
      byte var14 = 0;
      if (astarmap == null) {
         astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
         IsoWorld.instance.CurrentCell.setPathMap(astarmap);
      }

      IsoGridSquare var15 = null;
      float var16 = 1.0E9F;

      try {
         if (var2 == IsoCamera.CamCharacter) {
            boolean var17 = true;
         }

         IsoGridSquare var32 = astarmap.getNode(var10, var11, var5);
         if (var32 == null) {
            return null;
         } else {
            IsoGridSquare var18 = IsoWorld.instance.CurrentCell.getGridSquare(var6, var7, var8);
            if (var18 == null && !this.bClosest) {
               return null;
            } else if (var18 != null && !var18.isFree(false) && !this.bClosest) {
               return null;
            } else if (var18 != null && var18.getN() == null && var18.getS() == null && var18.getE() == null && var18.getW() == null && !this.bClosest) {
               return null;
            } else {
               if (var32.searchData == null) {
                  var32.searchData = new SearchData[PathfindManager.instance.MaxThreads];
               }

               if (var32.searchData[var1] == null) {
                  var32.searchData[var1] = new SearchData();
               }

               var32.searchData[var1].cost = 0.0F;
               var32.searchData[var1].depth = 0;
               var32.searchData[var1].parent = null;
               this.open.clear();
               this.closed.clear();
               this.open.add(var1, 0, var32);
               IsoGridSquare var19 = astarmap.getNode(var12, var13, var8);
               if (var19 == null) {
                  return null;
               } else {
                  if (var19.searchData[var1] == null) {
                     var19.searchData[var1] = new SearchData();
                  }

                  var19.searchData[var1].parent = null;
                  this.maxDepth = 0;
                  Thread var20 = Thread.currentThread();

                  while(true) {
                     int var23;
                     int var24;
                     if (this.maxDepth < this.maxSearchDistance && this.open.size() != 0) {
                        if (PathfindManager.instance.threads[var1].SwitchingCells) {
                           return null;
                        }

                        if (var9.finished) {
                           return null;
                        }

                        if (var2 == IsoCamera.CamCharacter) {
                           boolean var21 = false;
                        }

                        if (IngameState.DebugPathfinding && var2 == IsoCamera.CamCharacter && !(var20 instanceof PathfindManager.PathfindThread) || IngameState.AlwaysDebugPathfinding) {
                           Core.getInstance().StartFrame();
                        }

                        IsoGridSquare var33 = this.getFirstInOpen();
                        if (IngameState.AlwaysDebugPathfinding && !this.IsoGridSquaresUnfurled.contains(var33)) {
                           this.IsoGridSquaresUnfurled.add(var33);
                        }

                        if (var33 != var19) {
                           this.removeFromOpen(var1, var33);
                           this.addToClosed(var33);

                           for(int var35 = -1; var35 <= 1; ++var35) {
                              for(var23 = -1; var23 <= 1; ++var23) {
                                 for(var24 = -1; var24 <= 1; ++var24) {
                                    if (var24 == -1 && var23 == 0 && var35 == 0) {
                                       boolean var37 = false;
                                    }

                                    if (astarmap.isValidLocation(var2, var33.x, var33.y, var33.z, var33.x + var23, var33.y + var24, var33.z + var35, var33.x, var33.y, var33.z)) {
                                       IsoGridSquare var38 = astarmap.getNode(var33.x + var23, var33.y + var24, var33.z + var35);
                                       if (var38 != null) {
                                          if (this.bClosest) {
                                             float var36 = IsoUtils.DistanceManhatten((float)var19.x, (float)var19.y, (float)var33.x, (float)var33.y, 0.0F, 0.0F);
                                             if (var36 < var16 && var33 != null) {
                                                var16 = var36;
                                                var15 = var33;
                                             }
                                          }

                                          this.PathfindIsoGridSquare(var1, var33, var2, var38, var6, var7, var8, var14);
                                       }
                                    }
                                 }
                              }
                           }

                           if (IngameState.AlwaysDebugPathfinding) {
                              Core.getInstance().EndFrame(0);
                              Core.getInstance().StartFrameUI();
                              this.renderOverhead();
                              Core.getInstance().EndFrameUI();
                              Display.update();
                              Thread.sleep(30L);
                           }
                           continue;
                        }
                     }

                     if (this.IsoGridSquaresUnfurled.size() > unfurledmax) {
                        unfurledmax = this.IsoGridSquaresUnfurled.size();
                     }

                     if (var19.searchData[var1].parent == null) {
                        if (this.bClosest && var15 != null) {
                           return this.findPath(var1, var2, var3, var4, var5, var15.x, var15.y, var15.z);
                        }

                        return null;
                     }

                     Path var34 = new Path();
                     IsoGridSquare var22 = var19;
                     var34.cost += var19.searchData[var1].cost;
                     var23 = var19.x;
                     var24 = var19.y;
                     int var25 = var19.z;

                     for(long var26 = System.currentTimeMillis(); var22 != var32; var22 = var22.searchData[var1].parent) {
                        int var28 = var22.x;
                        int var29 = var22.y;
                        int var30 = var22.z;
                        if (var28 == var23 && var29 == var24 && var30 == var25) {
                           var34.prependStep(var23, var24, var25);
                        }

                        if (System.currentTimeMillis() - var26 > 500L) {
                           throw new RuntimeException("AStarPathFinder infinite loop?");
                        }

                        for(; var28 != var23 || var29 != var24 || var30 != var25; var34.prependStep(var23, var24, var25)) {
                           if (var28 > var23) {
                              ++var23;
                           } else if (var28 < var23) {
                              --var23;
                           }

                           if (var29 > var24) {
                              ++var24;
                           } else if (var29 < var24) {
                              --var24;
                           }

                           if (var30 > var25) {
                              ++var25;
                           } else if (var30 < var25) {
                              --var25;
                           }
                        }

                        var23 = var22.x;
                        var24 = var22.y;
                        var25 = var22.z;
                     }

                     if (var3 == var23 && var4 == var24 && var5 == var25) {
                        var34.prependStep(var23, var24, var25);
                     }

                     for(; var23 != var3 || var24 != var4 || var25 != var5; var34.prependStep(var23, var24, var25)) {
                        if (var3 > var23) {
                           ++var23;
                        } else if (var3 < var23) {
                           --var23;
                        }

                        if (var4 > var24) {
                           ++var24;
                        } else if (var4 < var24) {
                           --var24;
                        }

                        if (var5 > var25) {
                           ++var25;
                        } else if (var5 < var25) {
                           --var25;
                        }
                     }

                     if (IngameState.DebugPathfinding && var2 == IsoCamera.CamCharacter && !(var20 instanceof PathfindManager.PathfindThread) || IngameState.AlwaysDebugPathfinding) {
                        Core.getInstance().StartFrame();
                        this.renderOverhead();
                        this.renderPath(var34);
                        Core.getInstance().EndFrame(0);
                        Display.update();
                        Thread.sleep(2000L);
                        IngameState.DebugPathfinding = false;
                     }

                     return var34;
                  }
               }
            }
         }
      } catch (Exception var31) {
         Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, var31);
         return null;
      }
   }

   private void renderPath(Path var1) {
      TextureID.UseFiltering = true;
      Texture.getSharedTexture("media/ui/white.png");
      IsoCell var2 = IsoWorld.instance.CurrentCell;
      Texture var3 = Texture.getSharedTexture("media/ui/white.png");
      boolean var4 = false;
      byte var5 = 2;
      int var6 = Core.getInstance().getOffscreenWidth(0) - var2.getWidthInTiles() * var5;
      int var7 = Core.getInstance().getOffscreenHeight(0) - var2.getHeightInTiles() * var5;

      for(int var8 = 0; var8 < var1.getLength() - 1; ++var8) {
         int var9 = var1.getX(var8);
         int var10 = var1.getY(var8);
         int var11 = var1.getX(var8 + 1);

         for(int var12 = var1.getY(var8 + 1); var9 != var11 || var10 != var12; var3.render(var6 + var9 * var5, var7 + var10 * var5, var5, var5, 0.0F, 0.0F, 1.0F, 1.0F)) {
            if (var9 < var11) {
               ++var9;
            }

            if (var9 > var11) {
               --var9;
            }

            if (var10 < var12) {
               ++var10;
            }

            if (var10 > var12) {
               --var10;
            }
         }
      }

   }

   private void renderOverhead() {
      TextureID.UseFiltering = true;
      Texture.getSharedTexture("media/ui/white.png");
      IsoCell var1 = IsoWorld.instance.CurrentCell;
      Texture var2 = Texture.getSharedTexture("media/ui/white.png");
      byte var3 = 0;
      byte var4 = 2;
      int var5 = Core.getInstance().getOffscreenWidth(0) - var1.getWidthInTiles() * var4;
      int var6 = Core.getInstance().getOffscreenHeight(0) - var1.getHeightInTiles() * var4;
      var2.render(var5, var6, var4 * var1.getWidthInTiles(), var4 * var1.getHeightInTiles(), 0.7F, 0.7F, 0.7F, 1.0F);

      int var7;
      for(var7 = 0; var7 < var1.getWidthInTiles(); ++var7) {
         for(int var8 = 0; var8 < var1.getHeightInTiles(); ++var8) {
            IsoGridSquare var9 = var1.getGridSquare(var7, var8, var3);
            if (var9 != null) {
               if (!var9.getProperties().Is(IsoFlagType.solid) && !var9.getProperties().Is(IsoFlagType.solidtrans)) {
                  if (!var9.getProperties().Is(IsoFlagType.exterior)) {
                     var2.render(var5 + var7 * var4, var6 + var8 * var4, var4, var4, 0.8F, 0.8F, 0.8F, 1.0F);
                  }
               } else {
                  var2.render(var5 + var7 * var4, var6 + var8 * var4, var4, var4, 0.5F, 0.5F, 0.5F, 255.0F);
               }

               if (var9.getProperties().Is(IsoFlagType.collideN)) {
                  var2.render(var5 + var7 * var4, var6 + var8 * var4, var4, 1, 0.2F, 0.2F, 0.2F, 1.0F);
               }

               if (var9.getProperties().Is(IsoFlagType.collideW)) {
                  var2.render(var5 + var7 * var4, var6 + var8 * var4, 1, var4, 0.2F, 0.2F, 0.2F, 1.0F);
               }
            }
         }
      }

      for(var7 = 0; var7 < this.IsoGridSquaresUnfurled.size(); ++var7) {
         IsoGridSquare var10 = (IsoGridSquare)this.IsoGridSquaresUnfurled.get(var7);
         var2.render(var5 + var10.x * var4, var6 + var10.y * var4, var4, var4, 1.0F, 0.0F, 0.0F, 1.0F);
      }

      var2.render(var5 + this.startX * var4, var6 + this.startY * var4, var4, var4, 0.0F, 1.0F, 0.0F, 1.0F);
      var2.render(var5 + this.targetX * var4, var6 + this.targetY * var4, var4, var4, 1.0F, 1.0F, 0.0F, 1.0F);
      TextureID.UseFiltering = false;
   }

   public AStarPathFinder.PathFindProgress findPathActual(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (astarmap == null) {
         astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
         IsoWorld.instance.CurrentCell.setPathMap(astarmap);
      }

      return this.findPathActual(var1, var2, var3, var4, var5, var6, var7, false);
   }

   public AStarPathFinder.PathFindProgress findPathActual(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      if (astarmap == null) {
         astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
         IsoWorld.instance.CurrentCell.setPathMap(astarmap);
      }

      this.foundPath = null;
      this.targetX = var5;
      this.targetY = var6;
      this.targetZ = var7;
      this.startX = var2;
      this.startY = var3;
      this.startZ = var4;
      this.mover = var1;
      this.bClosest = var8;
      this.progress = AStarPathFinder.PathFindProgress.notyetfound;
      return AStarPathFinder.PathFindProgress.notyetfound;
   }

   public AStarPathFinder.PathFindProgress findPathSlice(int var1, IPathfinder var2, Mover var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      if (!IngameState.DebugPathfinding && !IngameState.AlwaysDebugPathfinding) {
         PathfindManager.instance.AddJob(var2, var3, var4, var5, var6, var7, var8, var9);
         this.progress = AStarPathFinder.PathFindProgress.notyetfound;
         return AStarPathFinder.PathFindProgress.notyetfound;
      } else {
         this.startX = var4;
         this.startY = var5;
         this.startZ = var6;
         this.targetX = var7;
         this.targetY = var8;
         this.targetZ = var9;
         this.mover = var3;
         this.maxSearchDistance = 1000;
         this.Cycle(var1);
         if (this.progress == AStarPathFinder.PathFindProgress.found) {
            var2.Succeeded(this.getPath(), var3);
         } else {
            var2.Failed(var3);
         }

         return this.progress;
      }
   }

   public int getFreeIndex(IsoGameCharacter var1) {
      if (astarmap == null) {
         astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
         IsoWorld.instance.CurrentCell.setPathMap(astarmap);
      }

      return 0;
   }

   public float getHeuristicCost(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return this.heuristic.getCost(astarmap.map, var1, var2, var3, var4, var5, var6, var7);
   }

   public Path getPath() {
      return this.foundPath;
   }

   protected void addToClosed(IsoGridSquare var1) {
      this.closed.setValue(var1.ID, true);
   }

   protected void addToOpen(int var1, int var2, IsoGridSquare var3) {
      this.open.add(var1, var2, var3);
   }

   protected IsoGridSquare getFirstInOpen() {
      return this.open.first();
   }

   protected boolean inClosedList(IsoGridSquare var1) {
      return this.closed.getValue(var1.ID);
   }

   protected boolean inOpenList(IsoGridSquare var1) {
      return this.open.contains(var1);
   }

   protected void removeFromClosed(IsoGridSquare var1) {
      this.closed.setValue(var1.ID, false);
   }

   protected void removeFromOpen(int var1, IsoGridSquare var2) {
      this.open.remove(var1, var2);
   }

   private class SortedList {
      private PriorityQueue list;
      TIntHashSet set;

      private SortedList() {
         this.list = new PriorityQueue(10000);
         this.set = new TIntHashSet(10000);
      }

      public void add(int var1, int var2, IsoGridSquare var3) {
         this.list.add(var3);
         this.set.add(var3.ID);
         AStarPathFinder.IDToUseInSort = var2;
      }

      public void clear() {
         this.list.clear();
         this.set.clear();
      }

      public boolean contains(IsoGridSquare var1) {
         return this.set.contains(var1.ID);
      }

      public IsoGridSquare first() {
         return (IsoGridSquare)this.list.peek();
      }

      public void remove(int var1, IsoGridSquare var2) {
         this.list.remove(var2);
         this.set.remove(var2.ID);
      }

      public int size() {
         return this.list.size();
      }

      // $FF: synthetic method
      SortedList(Object var2) {
         this();
      }
   }

   public static enum PathFindProgress {
      notrunning,
      failed,
      found,
      notyetfound;
   }
}
