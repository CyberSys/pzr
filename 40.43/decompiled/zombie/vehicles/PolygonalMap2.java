package zombie.vehicles;

import astar.ASearchNode;
import astar.AStar;
import astar.IGoalNode;
import astar.ISearchNode;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.awt.geom.Line2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.input.Mouse;
import zombie.Lua.LuaManager;
import zombie.ai.astar.Mover;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.physics.Transform;
import zombie.core.utils.BooleanGrid;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.input.GameKeyboard;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.scripting.objects.VehicleScript;

public class PolygonalMap2 {
   private static final float RADIUS = 0.3F;
   private static final float RADIUS_DIAGONAL = (float)Math.sqrt(0.18000000715255737D);
   public static final boolean CLOSE_TO_WALLS = true;
   private static Vector3f tempVec3f_1 = new Vector3f();
   private final ArrayList clusters = new ArrayList();
   private TIntObjectHashMap squareToNode = new TIntObjectHashMap();
   private final ArrayList tempSquares = new ArrayList();
   public static PolygonalMap2 instance = new PolygonalMap2();
   private final ArrayList graphs = new ArrayList();
   private final PolygonalMap2.AdjustStartEndNodeData adjustStartData = new PolygonalMap2.AdjustStartEndNodeData();
   private final PolygonalMap2.AdjustStartEndNodeData adjustGoalData = new PolygonalMap2.AdjustStartEndNodeData();
   private PolygonalMap2.LineClearCollide lcc = new PolygonalMap2.LineClearCollide();
   private PolygonalMap2.VGAStar astar = new PolygonalMap2.VGAStar();
   private final PolygonalMap2.TestRequest testRequest = new PolygonalMap2.TestRequest();
   private int testZ = 0;
   private final PathFindBehavior2.PointOnPath pointOnPath = new PathFindBehavior2.PointOnPath();
   private static final int SQUARES_PER_CHUNK = 10;
   private static final int LEVELS_PER_CHUNK = 8;
   private static final int SQUARES_PER_CELL = 300;
   private static final int CHUNKS_PER_CELL = 30;
   private static final short BIT_SOLID = 1;
   private static final short BIT_COLLIDE_W = 2;
   private static final short BIT_COLLIDE_N = 4;
   private static final short BIT_STAIR_TW = 8;
   private static final short BIT_STAIR_MW = 16;
   private static final short BIT_STAIR_BW = 32;
   private static final short BIT_STAIR_TN = 64;
   private static final short BIT_STAIR_MN = 128;
   private static final short BIT_STAIR_BN = 256;
   private static final short BIT_SOLID_FLOOR = 512;
   private static final short BIT_SOLID_TRANS = 1024;
   private static final short BIT_WINDOW_W = 2048;
   private static final short BIT_WINDOW_N = 4096;
   private static final short BIT_CAN_PATH_W = 8192;
   private static final short BIT_CAN_PATH_N = 16384;
   private static final short ALL_SOLID_BITS = 1025;
   private static final short ALL_STAIR_BITS = 504;
   private final ConcurrentLinkedQueue chunkTaskQueue = new ConcurrentLinkedQueue();
   private final ConcurrentLinkedQueue squareTaskQueue = new ConcurrentLinkedQueue();
   private final ConcurrentLinkedQueue vehicleTaskQueue = new ConcurrentLinkedQueue();
   private final ArrayList vehicles = new ArrayList();
   private final HashMap vehicleMap = new HashMap();
   private int minX;
   private int minY;
   private int width;
   private int height;
   private PolygonalMap2.Cell[][] cells;
   private final HashMap vehicleState = new HashMap();
   private final TObjectProcedure releaseNodeProc = new TObjectProcedure() {
      public boolean execute(PolygonalMap2.Node var1) {
         var1.release();
         return true;
      }
   };
   private boolean rebuild;
   private PolygonalMap2.Sync sync = new PolygonalMap2.Sync();
   private final Object renderLock = new Object();
   private PolygonalMap2.PMThread thread;
   private final ArrayDeque requests = new ArrayDeque();
   private final ConcurrentLinkedQueue requestToMain = new ConcurrentLinkedQueue();
   private final ConcurrentLinkedQueue requestTaskQueue = new ConcurrentLinkedQueue();
   private final HashMap requestMap = new HashMap();
   private PolygonalMap2.LineClearCollideMain lccMain = new PolygonalMap2.LineClearCollideMain();
   private final float[] tempFloats = new float[8];
   private CollideWithObstacles collideWithObstacles = new CollideWithObstacles();

   private void createVehicleCluster(PolygonalMap2.VehicleRect var1, ArrayList var2, ArrayList var3) {
      for(int var4 = 0; var4 < var2.size(); ++var4) {
         PolygonalMap2.VehicleRect var5 = (PolygonalMap2.VehicleRect)var2.get(var4);
         if (var1 != var5 && var1.z == var5.z && (var1.cluster == null || var1.cluster != var5.cluster) && var1.isAdjacent(var5)) {
            if (var1.cluster != null) {
               if (var5.cluster == null) {
                  var5.cluster = var1.cluster;
                  var5.cluster.rects.add(var5);
               } else {
                  var3.remove(var5.cluster);
                  var1.cluster.merge(var5.cluster);
               }
            } else if (var5.cluster != null) {
               if (var1.cluster == null) {
                  var1.cluster = var5.cluster;
                  var1.cluster.rects.add(var1);
               } else {
                  var3.remove(var1.cluster);
                  var5.cluster.merge(var1.cluster);
               }
            } else {
               PolygonalMap2.VehicleCluster var6 = PolygonalMap2.VehicleCluster.alloc().init();
               var1.cluster = var6;
               var5.cluster = var6;
               var6.rects.add(var1);
               var6.rects.add(var5);
               var3.add(var6);
            }
         }
      }

      if (var1.cluster == null) {
         PolygonalMap2.VehicleCluster var7 = PolygonalMap2.VehicleCluster.alloc().init();
         var1.cluster = var7;
         var7.rects.add(var1);
         var3.add(var7);
      }

   }

   private void createVehicleClusters() {
      this.clusters.clear();
      ArrayList var1 = new ArrayList();

      int var2;
      for(var2 = 0; var2 < this.vehicles.size(); ++var2) {
         PolygonalMap2.Vehicle var3 = (PolygonalMap2.Vehicle)this.vehicles.get(var2);
         PolygonalMap2.VehicleRect var4 = PolygonalMap2.VehicleRect.alloc();
         var3.polyPlusRadius.getAABB(var4);
         var4.vehicle = var3;
         var1.add(var4);
      }

      if (!var1.isEmpty()) {
         for(var2 = 0; var2 < var1.size(); ++var2) {
            PolygonalMap2.VehicleRect var5 = (PolygonalMap2.VehicleRect)var1.get(var2);
            this.createVehicleCluster(var5, var1, this.clusters);
         }

      }
   }

   private PolygonalMap2.Node getNodeForSquare(PolygonalMap2.Square var1) {
      PolygonalMap2.Node var2 = (PolygonalMap2.Node)this.squareToNode.get(var1.ID);
      if (var2 == null) {
         var2 = PolygonalMap2.Node.alloc().init(var1);
         this.squareToNode.put(var1.ID, var2);
      }

      return var2;
   }

   private PolygonalMap2.VisibilityGraph getVisGraphForSquare(PolygonalMap2.Square var1) {
      for(int var2 = 0; var2 < this.graphs.size(); ++var2) {
         PolygonalMap2.VisibilityGraph var3 = (PolygonalMap2.VisibilityGraph)this.graphs.get(var2);
         if (var3.contains(var1)) {
            return var3;
         }
      }

      return null;
   }

   private void connectTwoNodes(PolygonalMap2.Node var1, PolygonalMap2.Node var2) {
      var1.visible.add(var2);
      var2.visible.add(var1);
   }

   private void addStairNodes() {
      ArrayList var1 = this.tempSquares;
      var1.clear();

      int var2;
      for(var2 = 0; var2 < this.graphs.size(); ++var2) {
         PolygonalMap2.VisibilityGraph var3 = (PolygonalMap2.VisibilityGraph)this.graphs.get(var2);
         var3.getStairSquares(var1);
      }

      for(var2 = 0; var2 < var1.size(); ++var2) {
         PolygonalMap2.Square var15 = (PolygonalMap2.Square)var1.get(var2);
         PolygonalMap2.Square var4 = null;
         PolygonalMap2.Square var5 = null;
         PolygonalMap2.Square var6 = null;
         PolygonalMap2.Square var7 = null;
         PolygonalMap2.Square var8 = null;
         if (var15.has((short)8)) {
            var4 = this.getSquare(var15.x - 1, var15.y, var15.z + 1);
            var5 = var15;
            var6 = this.getSquare(var15.x + 1, var15.y, var15.z);
            var7 = this.getSquare(var15.x + 2, var15.y, var15.z);
            var8 = this.getSquare(var15.x + 3, var15.y, var15.z);
         }

         if (var15.has((short)64)) {
            var4 = this.getSquare(var15.x, var15.y - 1, var15.z + 1);
            var5 = var15;
            var6 = this.getSquare(var15.x, var15.y + 1, var15.z);
            var7 = this.getSquare(var15.x, var15.y + 2, var15.z);
            var8 = this.getSquare(var15.x, var15.y + 3, var15.z);
         }

         if (var4 != null && var5 != null && var6 != null && var7 != null && var8 != null) {
            PolygonalMap2.Node var9 = null;
            PolygonalMap2.Node var10 = null;
            PolygonalMap2.VisibilityGraph var11 = this.getVisGraphForSquare(var4);
            Iterator var12;
            PolygonalMap2.Obstacle var13;
            if (var11 == null) {
               var9 = this.getNodeForSquare(var4);
            } else {
               var9 = PolygonalMap2.Node.alloc().init(var4);
               var12 = var11.obstacles.iterator();

               while(var12.hasNext()) {
                  var13 = (PolygonalMap2.Obstacle)var12.next();
                  if (var13.isNodeInsideOf(var9)) {
                     var9.ignore = true;
                  }
               }

               var9.addGraph(var11);
               var11.addNode(var9);
               this.squareToNode.put(var4.ID, var9);
            }

            var11 = this.getVisGraphForSquare(var8);
            if (var11 == null) {
               var10 = this.getNodeForSquare(var8);
            } else {
               var10 = PolygonalMap2.Node.alloc().init(var8);
               var12 = var11.obstacles.iterator();

               while(var12.hasNext()) {
                  var13 = (PolygonalMap2.Obstacle)var12.next();
                  if (var13.isNodeInsideOf(var10)) {
                     var10.ignore = true;
                  }
               }

               var10.addGraph(var11);
               var11.addNode(var10);
               this.squareToNode.put(var8.ID, var10);
            }

            if (var9 != null && var10 != null) {
               PolygonalMap2.Node var16 = this.getNodeForSquare(var5);
               PolygonalMap2.Node var17 = this.getNodeForSquare(var6);
               PolygonalMap2.Node var14 = this.getNodeForSquare(var7);
               this.connectTwoNodes(var9, var16);
               this.connectTwoNodes(var16, var17);
               this.connectTwoNodes(var17, var14);
               this.connectTwoNodes(var14, var10);
            }
         }
      }

   }

   private void addWindowNodes() {
      ArrayList var1 = this.tempSquares;
      var1.clear();

      int var2;
      for(var2 = 0; var2 < this.graphs.size(); ++var2) {
         PolygonalMap2.VisibilityGraph var3 = (PolygonalMap2.VisibilityGraph)this.graphs.get(var2);
         var3.getWindowSquares(var1);
      }

      for(var2 = 0; var2 < var1.size(); ++var2) {
         PolygonalMap2.Square var12 = (PolygonalMap2.Square)var1.get(var2);
         if (!var12.isReallySolid() && !var12.has((short)504) && var12.has((short)512)) {
            int var4 = var12.has((short)8192) ? var12.x - 1 : var12.x;
            int var5 = var12.has((short)16384) ? var12.y - 1 : var12.y;
            PolygonalMap2.Square var6 = this.getSquare(var4, var5, var12.z);
            if (var6 != null && !var6.isReallySolid() && !var6.has((short)504) && var6.has((short)512)) {
               PolygonalMap2.VisibilityGraph var8 = this.getVisGraphForSquare(var12);
               PolygonalMap2.Node var7;
               if (var8 == null) {
                  var7 = this.getNodeForSquare(var12);
               } else {
                  var7 = PolygonalMap2.Node.alloc().init(var12);
                  Iterator var9 = var8.obstacles.iterator();

                  while(var9.hasNext()) {
                     PolygonalMap2.Obstacle var10 = (PolygonalMap2.Obstacle)var9.next();
                     if (var10.isNodeInsideOf(var7)) {
                        var7.ignore = true;
                     }
                  }

                  var8.addNode(var7);
                  this.squareToNode.put(var12.ID, var7);
               }

               var8 = this.getVisGraphForSquare(var6);
               PolygonalMap2.Node var13;
               if (var8 == null) {
                  var13 = this.getNodeForSquare(var6);
               } else {
                  var13 = PolygonalMap2.Node.alloc().init(var6);
                  Iterator var14 = var8.obstacles.iterator();

                  while(var14.hasNext()) {
                     PolygonalMap2.Obstacle var11 = (PolygonalMap2.Obstacle)var14.next();
                     if (var11.isNodeInsideOf(var13)) {
                        var13.ignore = true;
                     }
                  }

                  var8.addNode(var13);
                  this.squareToNode.put(var6.ID, var13);
               }

               this.connectTwoNodes(var7, var13);
            }
         }
      }

   }

   private void createVisibilityGraph(PolygonalMap2.VehicleCluster var1) {
      PolygonalMap2.VisibilityGraph var2 = PolygonalMap2.VisibilityGraph.alloc().init(var1);
      var2.addPerimeterEdges();
      this.graphs.add(var2);
   }

   private void createVisibilityGraphs() {
      this.createVehicleClusters();
      this.graphs.clear();
      this.squareToNode.clear();

      for(int var1 = 0; var1 < this.clusters.size(); ++var1) {
         PolygonalMap2.VehicleCluster var2 = (PolygonalMap2.VehicleCluster)this.clusters.get(var1);
         this.createVisibilityGraph(var2);
      }

      this.addStairNodes();
      this.addWindowNodes();
   }

   private boolean findPath(PolygonalMap2.PathFindRequest var1, boolean var2) {
      if ((int)var1.startZ == (int)var1.targetZ && !this.lcc.isNotClear(this, var1.startX, var1.startY, var1.targetX, var1.targetY, (int)var1.startZ)) {
         var1.path.addNode(var1.startX, var1.startY, var1.startZ);
         var1.path.addNode(var1.targetX, var1.targetY, var1.targetZ);
         return true;
      } else {
         this.astar.init(this.graphs, this.squareToNode);
         PolygonalMap2.VisibilityGraph var3 = null;
         PolygonalMap2.VisibilityGraph var4 = null;
         PolygonalMap2.SearchNode var5 = null;
         PolygonalMap2.SearchNode var6 = null;
         boolean var7 = false;
         boolean var8 = false;
         boolean var27 = false;

         boolean var38;
         label1355: {
            int var13;
            boolean var33;
            label1356: {
               boolean var32;
               PolygonalMap2.VisibilityGraph var35;
               Iterator var40;
               int var43;
               label1357: {
                  PolygonalMap2.VisibilityGraph var10;
                  label1358: {
                     label1359: {
                        int var14;
                        boolean var34;
                        Iterator var39;
                        PolygonalMap2.VisibilityGraph var41;
                        label1360: {
                           try {
                              label1398: {
                                 var27 = true;
                                 PolygonalMap2.Square var9 = this.getSquare((int)var1.startX, (int)var1.startY, (int)var1.startZ);
                                 if (var9 == null || var9.isReallySolid()) {
                                    var32 = false;
                                    var27 = false;
                                    break label1357;
                                 }

                                 PolygonalMap2.Node var11;
                                 int var12;
                                 if (var9.has((short)504)) {
                                    var5 = this.astar.getSearchNode(var9);
                                 } else {
                                    var10 = this.astar.getVisGraphForSquare(var9);
                                    if (var10 != null) {
                                       if (!var10.created) {
                                          var10.create();
                                       }

                                       var11 = null;
                                       var12 = var10.getPointOutsideObstacles(var1.startX, var1.startY, var1.startZ, this.adjustStartData);
                                       if (var12 == -1) {
                                          var34 = false;
                                          var27 = false;
                                          break label1398;
                                       }

                                       if (var12 == 1) {
                                          var7 = true;
                                          var11 = this.adjustStartData.node;
                                          if (this.adjustStartData.isNodeNew) {
                                             var3 = var10;
                                          }
                                       }

                                       if (var11 == null) {
                                          var11 = PolygonalMap2.Node.alloc().init(var1.startX, var1.startY, (int)var1.startZ);
                                          var10.addNode(var11);
                                          var3 = var10;
                                       }

                                       var5 = this.astar.getSearchNode(var11);
                                    }
                                 }

                                 if (var5 == null) {
                                    var5 = this.astar.getSearchNode(var9);
                                 }

                                 if (!(var1.targetX < 0.0F) && !(var1.targetY < 0.0F) && this.getChunkFromSquarePos((int)var1.targetX, (int)var1.targetY) != null) {
                                    var9 = this.getSquare((int)var1.targetX, (int)var1.targetY, (int)var1.targetZ);
                                    if (var9 == null || var9.isReallySolid()) {
                                       var32 = false;
                                       var27 = false;
                                       break label1359;
                                    }

                                    if (var9.has((short)504)) {
                                       var6 = this.astar.getSearchNode(var9);
                                    } else {
                                       var10 = this.astar.getVisGraphForSquare(var9);
                                       if (var10 != null) {
                                          if (!var10.created) {
                                             var10.create();
                                          }

                                          var11 = null;
                                          var12 = var10.getPointOutsideObstacles(var1.targetX, var1.targetY, var1.targetZ, this.adjustGoalData);
                                          if (var12 == -1) {
                                             var34 = false;
                                             var27 = false;
                                             break label1360;
                                          }

                                          if (var12 == 1) {
                                             var8 = true;
                                             var11 = this.adjustGoalData.node;
                                             if (this.adjustGoalData.isNodeNew) {
                                                var4 = var10;
                                             }
                                          }

                                          if (var11 == null) {
                                             var11 = PolygonalMap2.Node.alloc().init(var1.targetX, var1.targetY, (int)var1.targetZ);
                                             var10.addNode(var11);
                                             var4 = var10;
                                          }

                                          var6 = this.astar.getSearchNode(var11);
                                       }
                                    }

                                    if (var6 == null) {
                                       var6 = this.astar.getSearchNode(var9);
                                    }
                                 } else {
                                    var6 = this.astar.getSearchNode((int)var1.targetX, (int)var1.targetY);
                                 }

                                 ArrayList var31 = this.astar.shortestPath(var1.mover, var5, var6);
                                 if (var31 != null) {
                                    if (var31.size() != 1) {
                                       PolygonalMap2.Square var36 = null;
                                       var12 = -123;
                                       var13 = -123;

                                       for(var14 = 0; var14 < var31.size(); ++var14) {
                                          PolygonalMap2.SearchNode var15 = (PolygonalMap2.SearchNode)var31.get(var14);
                                          float var16 = var15.getX();
                                          float var17 = var15.getY();
                                          float var18 = var15.getZ();
                                          PolygonalMap2.Square var19 = var15.square;
                                          boolean var20 = false;
                                          if (var19 != null && var36 != null && var19.z == var36.z) {
                                             int var21 = var19.x - var36.x;
                                             int var22 = var19.y - var36.y;
                                             if (var21 == var12 && var22 == var13) {
                                                if (var1.path.nodes.size() > 1) {
                                                   var20 = true;
                                                }
                                             } else {
                                                var12 = var21;
                                                var13 = var22;
                                             }
                                          } else {
                                             var13 = -123;
                                             var12 = -123;
                                          }

                                          if (var19 != null) {
                                             var36 = var19;
                                          } else {
                                             var36 = null;
                                          }

                                          if (!var8 && var15 == var6 && var15.square != null) {
                                             var16 = var1.targetX;
                                             var17 = var1.targetY;
                                             var20 = false;
                                          }

                                          PolygonalMap2.PathNode var47;
                                          if (var20) {
                                             var47 = (PolygonalMap2.PathNode)var1.path.nodes.get(var1.path.nodes.size() - 1);
                                             var47.x = (float)var19.x + 0.5F;
                                             var47.y = (float)var19.y + 0.5F;
                                          } else {
                                             if (var1.path.nodes.size() > 1) {
                                                var47 = (PolygonalMap2.PathNode)var1.path.nodes.get(var1.path.nodes.size() - 1);
                                                if (Math.abs(var47.x - var16) < 0.01F && Math.abs(var47.y - var17) < 0.01F && Math.abs(var47.z - var18) < 0.01F) {
                                                   var47.x = var16;
                                                   var47.y = var17;
                                                   var47.z = var18;
                                                   continue;
                                                }
                                             }

                                             var1.path.addNode(var16, var17, var18);
                                          }
                                       }

                                       if (var1.mover instanceof IsoPlayer) {
                                          this.smoothPath(var1.path);
                                       }

                                       var38 = true;
                                       var27 = false;
                                       break label1355;
                                    }

                                    var1.path.addNode(var5.getX(), var5.getY(), var5.getZ());
                                    var1.path.addNode(var6.getX(), var6.getY(), var6.getZ());
                                    var33 = true;
                                    var27 = false;
                                    break label1356;
                                 }

                                 var27 = false;
                                 break label1358;
                              }
                           } finally {
                              if (var27) {
                                 if (var2) {
                                    Iterator var24 = this.graphs.iterator();

                                    while(var24.hasNext()) {
                                       PolygonalMap2.VisibilityGraph var25 = (PolygonalMap2.VisibilityGraph)var24.next();
                                       var25.render();
                                    }
                                 }

                                 if (var3 != null) {
                                    var3.removeNode(var5.vgNode);
                                 }

                                 if (var4 != null) {
                                    var4.removeNode(var6.vgNode);
                                 }

                                 int var48;
                                 for(var48 = 0; var48 < this.astar.searchNodes.size(); ++var48) {
                                    ((PolygonalMap2.SearchNode)this.astar.searchNodes.get(var48)).release();
                                 }

                                 if (var7 && this.adjustStartData.isNodeNew) {
                                    for(var48 = 0; var48 < this.adjustStartData.node.edges.size(); ++var48) {
                                       ((PolygonalMap2.Edge)this.adjustStartData.node.edges.get(var48)).obstacle.unsplit(this.adjustStartData.node);
                                    }

                                    this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                                 }

                                 if (var8 && this.adjustGoalData.isNodeNew) {
                                    for(var48 = 0; var48 < this.adjustGoalData.node.edges.size(); ++var48) {
                                       ((PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(var48)).obstacle.unsplit(this.adjustGoalData.node);
                                    }

                                    this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                                 }

                              }
                           }

                           if (var2) {
                              var39 = this.graphs.iterator();

                              while(var39.hasNext()) {
                                 var41 = (PolygonalMap2.VisibilityGraph)var39.next();
                                 var41.render();
                              }
                           }

                           if (var3 != null) {
                              var3.removeNode(var5.vgNode);
                           }

                           if (var4 != null) {
                              var4.removeNode(var6.vgNode);
                           }

                           for(var14 = 0; var14 < this.astar.searchNodes.size(); ++var14) {
                              ((PolygonalMap2.SearchNode)this.astar.searchNodes.get(var14)).release();
                           }

                           if (var7 && this.adjustStartData.isNodeNew) {
                              for(var14 = 0; var14 < this.adjustStartData.node.edges.size(); ++var14) {
                                 ((PolygonalMap2.Edge)this.adjustStartData.node.edges.get(var14)).obstacle.unsplit(this.adjustStartData.node);
                              }

                              this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                           }

                           if (var8 && this.adjustGoalData.isNodeNew) {
                              for(var14 = 0; var14 < this.adjustGoalData.node.edges.size(); ++var14) {
                                 ((PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(var14)).obstacle.unsplit(this.adjustGoalData.node);
                              }

                              this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                           }

                           return var34;
                        }

                        if (var2) {
                           var39 = this.graphs.iterator();

                           while(var39.hasNext()) {
                              var41 = (PolygonalMap2.VisibilityGraph)var39.next();
                              var41.render();
                           }
                        }

                        if (var3 != null) {
                           var3.removeNode(var5.vgNode);
                        }

                        if (var4 != null) {
                           var4.removeNode(var6.vgNode);
                        }

                        for(var14 = 0; var14 < this.astar.searchNodes.size(); ++var14) {
                           ((PolygonalMap2.SearchNode)this.astar.searchNodes.get(var14)).release();
                        }

                        if (var7 && this.adjustStartData.isNodeNew) {
                           for(var14 = 0; var14 < this.adjustStartData.node.edges.size(); ++var14) {
                              ((PolygonalMap2.Edge)this.adjustStartData.node.edges.get(var14)).obstacle.unsplit(this.adjustStartData.node);
                           }

                           this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                        }

                        if (var8 && this.adjustGoalData.isNodeNew) {
                           for(var14 = 0; var14 < this.adjustGoalData.node.edges.size(); ++var14) {
                              ((PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(var14)).obstacle.unsplit(this.adjustGoalData.node);
                           }

                           this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                        }

                        return var34;
                     }

                     if (var2) {
                        var40 = this.graphs.iterator();

                        while(var40.hasNext()) {
                           var35 = (PolygonalMap2.VisibilityGraph)var40.next();
                           var35.render();
                        }
                     }

                     if (var3 != null) {
                        var3.removeNode(var5.vgNode);
                     }

                     if (var4 != null) {
                        var4.removeNode(var6.vgNode);
                     }

                     for(var43 = 0; var43 < this.astar.searchNodes.size(); ++var43) {
                        ((PolygonalMap2.SearchNode)this.astar.searchNodes.get(var43)).release();
                     }

                     if (var7 && this.adjustStartData.isNodeNew) {
                        for(var43 = 0; var43 < this.adjustStartData.node.edges.size(); ++var43) {
                           ((PolygonalMap2.Edge)this.adjustStartData.node.edges.get(var43)).obstacle.unsplit(this.adjustStartData.node);
                        }

                        this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                     }

                     if (var8 && this.adjustGoalData.isNodeNew) {
                        for(var43 = 0; var43 < this.adjustGoalData.node.edges.size(); ++var43) {
                           ((PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(var43)).obstacle.unsplit(this.adjustGoalData.node);
                        }

                        this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                     }

                     return var32;
                  }

                  if (var2) {
                     Iterator var29 = this.graphs.iterator();

                     while(var29.hasNext()) {
                        var10 = (PolygonalMap2.VisibilityGraph)var29.next();
                        var10.render();
                     }
                  }

                  if (var3 != null) {
                     var3.removeNode(var5.vgNode);
                  }

                  if (var4 != null) {
                     var4.removeNode(var6.vgNode);
                  }

                  int var30;
                  for(var30 = 0; var30 < this.astar.searchNodes.size(); ++var30) {
                     ((PolygonalMap2.SearchNode)this.astar.searchNodes.get(var30)).release();
                  }

                  if (var7 && this.adjustStartData.isNodeNew) {
                     for(var30 = 0; var30 < this.adjustStartData.node.edges.size(); ++var30) {
                        ((PolygonalMap2.Edge)this.adjustStartData.node.edges.get(var30)).obstacle.unsplit(this.adjustStartData.node);
                     }

                     this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                  }

                  if (var8 && this.adjustGoalData.isNodeNew) {
                     for(var30 = 0; var30 < this.adjustGoalData.node.edges.size(); ++var30) {
                        ((PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(var30)).obstacle.unsplit(this.adjustGoalData.node);
                     }

                     this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                  }

                  return false;
               }

               if (var2) {
                  var40 = this.graphs.iterator();

                  while(var40.hasNext()) {
                     var35 = (PolygonalMap2.VisibilityGraph)var40.next();
                     var35.render();
                  }
               }

               if (var3 != null) {
                  var3.removeNode(var5.vgNode);
               }

               if (var4 != null) {
                  var4.removeNode(var6.vgNode);
               }

               for(var43 = 0; var43 < this.astar.searchNodes.size(); ++var43) {
                  ((PolygonalMap2.SearchNode)this.astar.searchNodes.get(var43)).release();
               }

               if (var7 && this.adjustStartData.isNodeNew) {
                  for(var43 = 0; var43 < this.adjustStartData.node.edges.size(); ++var43) {
                     ((PolygonalMap2.Edge)this.adjustStartData.node.edges.get(var43)).obstacle.unsplit(this.adjustStartData.node);
                  }

                  this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
               }

               if (var8 && this.adjustGoalData.isNodeNew) {
                  for(var43 = 0; var43 < this.adjustGoalData.node.edges.size(); ++var43) {
                     ((PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(var43)).obstacle.unsplit(this.adjustGoalData.node);
                  }

                  this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
               }

               return var32;
            }

            if (var2) {
               Iterator var37 = this.graphs.iterator();

               while(var37.hasNext()) {
                  PolygonalMap2.VisibilityGraph var46 = (PolygonalMap2.VisibilityGraph)var37.next();
                  var46.render();
               }
            }

            if (var3 != null) {
               var3.removeNode(var5.vgNode);
            }

            if (var4 != null) {
               var4.removeNode(var6.vgNode);
            }

            for(var13 = 0; var13 < this.astar.searchNodes.size(); ++var13) {
               ((PolygonalMap2.SearchNode)this.astar.searchNodes.get(var13)).release();
            }

            if (var7 && this.adjustStartData.isNodeNew) {
               for(var13 = 0; var13 < this.adjustStartData.node.edges.size(); ++var13) {
                  ((PolygonalMap2.Edge)this.adjustStartData.node.edges.get(var13)).obstacle.unsplit(this.adjustStartData.node);
               }

               this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
            }

            if (var8 && this.adjustGoalData.isNodeNew) {
               for(var13 = 0; var13 < this.adjustGoalData.node.edges.size(); ++var13) {
                  ((PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(var13)).obstacle.unsplit(this.adjustGoalData.node);
               }

               this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
            }

            return var33;
         }

         if (var2) {
            Iterator var42 = this.graphs.iterator();

            while(var42.hasNext()) {
               PolygonalMap2.VisibilityGraph var45 = (PolygonalMap2.VisibilityGraph)var42.next();
               var45.render();
            }
         }

         if (var3 != null) {
            var3.removeNode(var5.vgNode);
         }

         if (var4 != null) {
            var4.removeNode(var6.vgNode);
         }

         int var44;
         for(var44 = 0; var44 < this.astar.searchNodes.size(); ++var44) {
            ((PolygonalMap2.SearchNode)this.astar.searchNodes.get(var44)).release();
         }

         if (var7 && this.adjustStartData.isNodeNew) {
            for(var44 = 0; var44 < this.adjustStartData.node.edges.size(); ++var44) {
               ((PolygonalMap2.Edge)this.adjustStartData.node.edges.get(var44)).obstacle.unsplit(this.adjustStartData.node);
            }

            this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
         }

         if (var8 && this.adjustGoalData.isNodeNew) {
            for(var44 = 0; var44 < this.adjustGoalData.node.edges.size(); ++var44) {
               ((PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(var44)).obstacle.unsplit(this.adjustGoalData.node);
            }

            this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
         }

         return var38;
      }
   }

   private void smoothPath(PolygonalMap2.Path var1) {
      int var2 = 0;

      while(true) {
         while(var2 < var1.nodes.size() - 2) {
            PolygonalMap2.PathNode var3 = (PolygonalMap2.PathNode)var1.nodes.get(var2);
            PolygonalMap2.PathNode var4 = (PolygonalMap2.PathNode)var1.nodes.get(var2 + 1);
            PolygonalMap2.PathNode var5 = (PolygonalMap2.PathNode)var1.nodes.get(var2 + 2);
            if ((int)var3.z == (int)var4.z && (int)var3.z == (int)var5.z) {
               if (!this.lcc.isNotClear(this, var3.x, var3.y, var5.x, var5.y, (int)var3.z)) {
                  var1.nodes.remove(var2 + 1);
                  var1.nodePool.push(var4);
               } else {
                  ++var2;
               }
            } else {
               ++var2;
            }
         }

         return;
      }
   }

   float getApparentZ(IsoGridSquare var1) {
      if (!var1.Has(IsoObjectType.stairsTW) && !var1.Has(IsoObjectType.stairsTN)) {
         if (!var1.Has(IsoObjectType.stairsMW) && !var1.Has(IsoObjectType.stairsMN)) {
            return !var1.Has(IsoObjectType.stairsBW) && !var1.Has(IsoObjectType.stairsBN) ? (float)var1.z : (float)var1.z + 0.25F;
         } else {
            return (float)var1.z + 0.5F;
         }
      } else {
         return (float)var1.z + 0.75F;
      }
   }

   public void render() {
      if (Core.bDebug) {
         if (DebugOptions.instance.PolymapRenderClusters.getValue()) {
            synchronized(this.renderLock) {
               Iterator var2 = this.clusters.iterator();

               while(var2.hasNext()) {
                  PolygonalMap2.VehicleCluster var3 = (PolygonalMap2.VehicleCluster)var2.next();
                  Iterator var4 = var3.rects.iterator();

                  while(var4.hasNext()) {
                     PolygonalMap2.VehicleRect var5 = (PolygonalMap2.VehicleRect)var4.next();
                     LineDrawer.addLine((float)var5.x, (float)var5.y, (float)var5.z, (float)var5.right(), (float)var5.bottom(), (float)var5.z, 0.0F, 0.0F, 1.0F, (String)null, false);
                  }

                  PolygonalMap2.VehicleRect var29 = var3.bounds();
                  var29.release();
               }

               var2 = this.graphs.iterator();

               while(var2.hasNext()) {
                  PolygonalMap2.VisibilityGraph var26 = (PolygonalMap2.VisibilityGraph)var2.next();
                  var26.render();
               }
            }
         }

         float var1;
         float var24;
         int var27;
         float var30;
         float var31;
         if (DebugOptions.instance.PolymapRenderLineClearCollide.getValue()) {
            var1 = (float)Mouse.getX() * Core.getInstance().getZoom(0);
            var24 = (float)Mouse.getY() * Core.getInstance().getZoom(0);
            var27 = (int)IsoPlayer.instance.getZ();
            var30 = IsoUtils.XToIso(var1, (float)Core.getInstance().getOffscreenHeight(0) - var24, (float)var27);
            var31 = IsoUtils.YToIso(var1, (float)Core.getInstance().getOffscreenHeight(0) - var24, (float)var27);
            LineDrawer.addLine(IsoPlayer.instance.x, IsoPlayer.instance.y, (float)var27, var30, var31, (float)var27, 1, 1, 1, (String)null);
            this.lccMain.isNotClear(this, IsoPlayer.instance.x, IsoPlayer.instance.y, var30, var31, var27, true, (BaseVehicle)null, true, true);
         }

         if (GameKeyboard.isKeyDown(209) && !GameKeyboard.wasKeyDown(209)) {
            this.testZ = Math.max(this.testZ - 1, 0);
         }

         if (GameKeyboard.isKeyDown(201) && !GameKeyboard.wasKeyDown(201)) {
            this.testZ = Math.min(this.testZ + 1, 7);
         }

         if (DebugOptions.instance.PolymapRenderPathToMouse.getValue()) {
            float var6;
            if (!this.testRequest.done && IsoPlayer.instance.getPath2() == null) {
               var1 = (float)Mouse.getX() * Core.getInstance().getZoom(0);
               var24 = (float)Mouse.getY() * Core.getInstance().getZoom(0);
               var27 = this.testZ;
               var30 = IsoUtils.XToIso(var1, (float)Core.getInstance().getOffscreenHeight(0) - var24, (float)var27);
               var31 = IsoUtils.YToIso(var1, (float)Core.getInstance().getOffscreenHeight(0) - var24, (float)var27);
               var6 = (float)var27;

               IsoGridSquare var12;
               for(int var7 = -1; var7 <= 1; ++var7) {
                  for(int var8 = -1; var8 <= 1; ++var8) {
                     float var9 = 0.3F;
                     float var10 = 0.3F;
                     float var11 = 0.3F;
                     var12 = IsoWorld.instance.CurrentCell.getGridSquare((int)var30 + var8, (int)var31 + var7, (int)var6);
                     if (var12 == null || var12.isSolid() || var12.isSolidTrans() || var12.HasStairs()) {
                        var11 = 0.0F;
                        var10 = 0.0F;
                     }

                     LineDrawer.addLine((float)((int)var30 + var8), (float)((int)var31 + var7), (float)((int)var6), (float)((int)var30 + var8 + 1), (float)((int)var31 + var7 + 1), (float)((int)var6), var9, var10, var11, (String)null, false);
                  }
               }

               if (var27 < (int)IsoPlayer.instance.getZ()) {
                  LineDrawer.addLine((float)((int)var30), (float)((int)var31), (float)((int)var6), (float)((int)var30), (float)((int)var31), (float)((int)IsoPlayer.instance.getZ()), 0.3F, 0.3F, 0.3F, (String)null, true);
               } else if (var27 > (int)IsoPlayer.instance.getZ()) {
                  LineDrawer.addLine((float)((int)var30), (float)((int)var31), (float)((int)var6), (float)((int)var30), (float)((int)var31), (float)((int)IsoPlayer.instance.getZ()), 0.3F, 0.3F, 0.3F, (String)null, true);
               }

               PolygonalMap2.PathFindRequest var32 = PolygonalMap2.PathFindRequest.alloc().init(this.testRequest, IsoPlayer.instance, IsoPlayer.instance.x, IsoPlayer.instance.y, IsoPlayer.instance.z, var30, var31, var6);
               this.testRequest.done = false;
               synchronized(this.renderLock) {
                  boolean var33 = DebugOptions.instance.PolymapRenderClusters.getValue();
                  if (this.findPath(var32, var33) && !var32.path.isEmpty()) {
                     IsoGridSquare var13;
                     float var15;
                     float var16;
                     PolygonalMap2.PathNode var36;
                     for(int var34 = 0; var34 < var32.path.nodes.size() - 1; ++var34) {
                        var36 = (PolygonalMap2.PathNode)var32.path.nodes.get(var34);
                        PolygonalMap2.PathNode var38 = (PolygonalMap2.PathNode)var32.path.nodes.get(var34 + 1);
                        var13 = IsoWorld.instance.CurrentCell.getGridSquare((double)var36.x, (double)var36.y, (double)var36.z);
                        IsoGridSquare var14 = IsoWorld.instance.CurrentCell.getGridSquare((double)var38.x, (double)var38.y, (double)var38.z);
                        var15 = var13 == null ? var36.z : this.getApparentZ(var13);
                        var16 = var14 == null ? var38.z : this.getApparentZ(var14);
                        float var17 = 1.0F;
                        float var18 = 1.0F;
                        float var19 = 0.0F;
                        if (var15 != (float)((int)var15) || var16 != (float)((int)var16)) {
                           var18 = 0.0F;
                        }

                        LineDrawer.addLine(var36.x, var36.y, var15, var38.x, var38.y, var16, var17, var18, var19, (String)null, true);
                     }

                     PathFindBehavior2.closestPointOnPath(IsoPlayer.instance.x, IsoPlayer.instance.y, IsoPlayer.instance.z, IsoPlayer.instance, var32.path, this.pointOnPath);
                     PolygonalMap2.PathNode var35 = (PolygonalMap2.PathNode)var32.path.nodes.get(this.pointOnPath.pathIndex);
                     var36 = (PolygonalMap2.PathNode)var32.path.nodes.get(this.pointOnPath.pathIndex + 1);
                     var12 = IsoWorld.instance.CurrentCell.getGridSquare((double)var35.x, (double)var35.y, (double)var35.z);
                     var13 = IsoWorld.instance.CurrentCell.getGridSquare((double)var36.x, (double)var36.y, (double)var36.z);
                     float var39 = var12 == null ? var35.z : this.getApparentZ(var12);
                     var15 = var13 == null ? var36.z : this.getApparentZ(var13);
                     var16 = var39 + (var15 - var39) * this.pointOnPath.dist;
                     LineDrawer.addLine(this.pointOnPath.x - 0.05F, this.pointOnPath.y - 0.05F, var16, this.pointOnPath.x + 0.05F, this.pointOnPath.y + 0.05F, var16, 0.0F, 1.0F, 0.0F, (String)null, true);
                     LineDrawer.addLine(this.pointOnPath.x - 0.05F, this.pointOnPath.y + 0.05F, var16, this.pointOnPath.x + 0.05F, this.pointOnPath.y - 0.05F, var16, 0.0F, 1.0F, 0.0F, (String)null, true);
                     if (GameKeyboard.isKeyDown(207) && !GameKeyboard.wasKeyDown(207)) {
                        Object var37 = LuaManager.env.rawget("ISPathFindAction_pathToLocationF");
                        if (var37 != null) {
                           LuaManager.caller.pcall(LuaManager.thread, var37, var30, var31, var6);
                        }
                     }
                  }

                  var32.release();
               }
            } else {
               for(int var23 = 0; var23 < this.testRequest.path.nodes.size() - 1; ++var23) {
                  PolygonalMap2.PathNode var25 = (PolygonalMap2.PathNode)this.testRequest.path.nodes.get(var23);
                  PolygonalMap2.PathNode var28 = (PolygonalMap2.PathNode)this.testRequest.path.nodes.get(var23 + 1);
                  var30 = 1.0F;
                  var31 = 1.0F;
                  var6 = 0.0F;
                  if (var25.z != (float)((int)var25.z) || var28.z != (float)((int)var28.z)) {
                     var31 = 0.0F;
                  }

                  LineDrawer.addLine(var25.x, var25.y, var25.z, var28.x, var28.y, var28.z, var30, var31, var6, (String)null, true);
               }

               this.testRequest.done = false;
            }
         }

         this.updateMain();
      }
   }

   public void squareChanged(IsoGridSquare var1) {
      PolygonalMap2.SquareUpdateTask var2 = PolygonalMap2.SquareUpdateTask.alloc().init(this, var1);
      this.squareTaskQueue.add(var2);
      this.thread.wake();
   }

   public void addChunkToWorld(IsoChunk var1) {
      PolygonalMap2.ChunkUpdateTask var2 = PolygonalMap2.ChunkUpdateTask.alloc().init(this, var1);
      this.chunkTaskQueue.add(var2);
      this.thread.wake();
   }

   public void removeChunkFromWorld(IsoChunk var1) {
      if (this.thread != null) {
         PolygonalMap2.ChunkRemoveTask var2 = PolygonalMap2.ChunkRemoveTask.alloc().init(this, var1);
         this.chunkTaskQueue.add(var2);
         this.thread.wake();
      }
   }

   public void addVehicleToWorld(BaseVehicle var1) {
      PolygonalMap2.VehicleAddTask var2 = PolygonalMap2.VehicleAddTask.alloc();
      var2.init(this, var1);
      this.vehicleTaskQueue.add(var2);
      PolygonalMap2.VehicleState var3 = PolygonalMap2.VehicleState.alloc().init(var1);
      this.vehicleState.put(var1, var3);
      this.thread.wake();
   }

   public void updateVehicle(BaseVehicle var1) {
      PolygonalMap2.VehicleUpdateTask var2 = PolygonalMap2.VehicleUpdateTask.alloc();
      var2.init(this, var1);
      this.vehicleTaskQueue.add(var2);
      this.thread.wake();
   }

   public void removeVehicleFromWorld(BaseVehicle var1) {
      if (this.thread != null) {
         PolygonalMap2.VehicleRemoveTask var2 = PolygonalMap2.VehicleRemoveTask.alloc();
         var2.init(this, var1);
         this.vehicleTaskQueue.add(var2);
         PolygonalMap2.VehicleState var3 = (PolygonalMap2.VehicleState)this.vehicleState.remove(var1);
         if (var3 != null) {
            var3.vehicle = null;
            var3.release();
         }

         this.thread.wake();
      }
   }

   private PolygonalMap2.Cell getCellFromSquarePos(int var1, int var2) {
      var1 -= this.minX * 300;
      var2 -= this.minY * 300;
      if (var1 >= 0 && var2 >= 0) {
         int var3 = var1 / 300;
         int var4 = var2 / 300;
         return var3 < this.width && var4 < this.height ? this.cells[var3][var4] : null;
      } else {
         return null;
      }
   }

   private PolygonalMap2.Cell getCellFromChunkPos(int var1, int var2) {
      return this.getCellFromSquarePos(var1 * 10, var2 * 10);
   }

   private PolygonalMap2.Chunk allocChunkIfNeeded(int var1, int var2) {
      PolygonalMap2.Cell var3 = this.getCellFromChunkPos(var1, var2);
      return var3 == null ? null : var3.allocChunkIfNeeded(var1, var2);
   }

   private PolygonalMap2.Chunk getChunkFromChunkPos(int var1, int var2) {
      PolygonalMap2.Cell var3 = this.getCellFromChunkPos(var1, var2);
      return var3 == null ? null : var3.getChunkFromChunkPos(var1, var2);
   }

   private PolygonalMap2.Chunk getChunkFromSquarePos(int var1, int var2) {
      PolygonalMap2.Cell var3 = this.getCellFromSquarePos(var1, var2);
      return var3 == null ? null : var3.getChunkFromChunkPos(var1 / 10, var2 / 10);
   }

   private PolygonalMap2.Square getSquare(int var1, int var2, int var3) {
      PolygonalMap2.Chunk var4 = this.getChunkFromSquarePos(var1, var2);
      return var4 == null ? null : var4.getSquare(var1, var2, var3);
   }

   public void init(IsoMetaGrid var1) {
      this.minX = var1.getMinX();
      this.minY = var1.getMinY();
      this.width = var1.getWidth();
      this.height = var1.getHeight();
      this.cells = new PolygonalMap2.Cell[this.width][this.height];

      for(int var2 = 0; var2 < this.height; ++var2) {
         for(int var3 = 0; var3 < this.width; ++var3) {
            this.cells[var3][var2] = PolygonalMap2.Cell.alloc().init(this, this.minX + var3, this.minY + var2);
         }
      }

      this.thread = new PolygonalMap2.PMThread();
      this.thread.setName("PolyPathThread");
      this.thread.setDaemon(true);
      this.thread.start();
   }

   public void stop() {
      this.thread.bStop = true;
      this.thread.wake();

      while(this.thread.isAlive()) {
         try {
            Thread.sleep(5L);
         } catch (InterruptedException var3) {
         }
      }

      int var1;
      for(var1 = 0; var1 < this.height; ++var1) {
         for(int var2 = 0; var2 < this.width; ++var2) {
            if (this.cells[var2][var1] != null) {
               this.cells[var2][var1].release();
            }
         }
      }

      for(PolygonalMap2.IChunkTask var4 = (PolygonalMap2.IChunkTask)this.chunkTaskQueue.poll(); var4 != null; var4 = (PolygonalMap2.IChunkTask)this.chunkTaskQueue.poll()) {
         var4.release();
      }

      for(PolygonalMap2.SquareUpdateTask var5 = (PolygonalMap2.SquareUpdateTask)this.squareTaskQueue.poll(); var5 != null; var5 = (PolygonalMap2.SquareUpdateTask)this.squareTaskQueue.poll()) {
         var5.release();
      }

      for(PolygonalMap2.IVehicleTask var7 = (PolygonalMap2.IVehicleTask)this.vehicleTaskQueue.poll(); var7 != null; var7 = (PolygonalMap2.IVehicleTask)this.vehicleTaskQueue.poll()) {
         var7.release();
      }

      for(PolygonalMap2.PathRequestTask var9 = (PolygonalMap2.PathRequestTask)this.requestTaskQueue.poll(); var9 != null; var9 = (PolygonalMap2.PathRequestTask)this.requestTaskQueue.poll()) {
         var9.release();
      }

      while(!this.requests.isEmpty()) {
         ((PolygonalMap2.PathFindRequest)this.requests.removeLast()).release();
      }

      while(!this.requestToMain.isEmpty()) {
         ((PolygonalMap2.PathFindRequest)this.requestToMain.remove()).release();
      }

      for(var1 = 0; var1 < this.vehicles.size(); ++var1) {
         PolygonalMap2.Vehicle var6 = (PolygonalMap2.Vehicle)this.vehicles.get(var1);
         var6.release();
      }

      Iterator var10 = this.vehicleState.values().iterator();

      while(var10.hasNext()) {
         PolygonalMap2.VehicleState var8 = (PolygonalMap2.VehicleState)var10.next();
         var8.release();
      }

      this.requestMap.clear();
      this.vehicles.clear();
      this.vehicleState.clear();
      this.vehicleMap.clear();
      this.cells = (PolygonalMap2.Cell[][])null;
      this.thread = null;
      this.rebuild = true;
   }

   public void updateMain() {
      ArrayList var1 = IsoWorld.instance.CurrentCell.getVehicles();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         BaseVehicle var3 = (BaseVehicle)var1.get(var2);
         PolygonalMap2.VehicleState var4 = (PolygonalMap2.VehicleState)this.vehicleState.get(var3);
         if (var4 != null && var4.check()) {
            this.updateVehicle(var3);
         }
      }

      for(PolygonalMap2.PathFindRequest var5 = (PolygonalMap2.PathFindRequest)this.requestToMain.poll(); var5 != null; var5 = (PolygonalMap2.PathFindRequest)this.requestToMain.poll()) {
         this.requestMap.remove(var5.mover);
         if (!var5.cancel) {
            if (var5.path.isEmpty()) {
               var5.finder.Failed(var5.mover);
            } else {
               var5.finder.Succeeded(var5.path, var5.mover);
            }
         }

         var5.release();
      }

   }

   public void updateThread() {
      for(PolygonalMap2.IChunkTask var1 = (PolygonalMap2.IChunkTask)this.chunkTaskQueue.poll(); var1 != null; var1 = (PolygonalMap2.IChunkTask)this.chunkTaskQueue.poll()) {
         var1.execute();
         var1.release();
         this.rebuild = true;
      }

      for(PolygonalMap2.SquareUpdateTask var5 = (PolygonalMap2.SquareUpdateTask)this.squareTaskQueue.poll(); var5 != null; var5 = (PolygonalMap2.SquareUpdateTask)this.squareTaskQueue.poll()) {
         var5.execute();
         var5.release();
      }

      for(PolygonalMap2.IVehicleTask var6 = (PolygonalMap2.IVehicleTask)this.vehicleTaskQueue.poll(); var6 != null; var6 = (PolygonalMap2.IVehicleTask)this.vehicleTaskQueue.poll()) {
         var6.execute();
         var6.release();
         this.rebuild = true;
      }

      for(PolygonalMap2.PathRequestTask var8 = (PolygonalMap2.PathRequestTask)this.requestTaskQueue.poll(); var8 != null; var8 = (PolygonalMap2.PathRequestTask)this.requestTaskQueue.poll()) {
         var8.execute();
         var8.release();
      }

      int var9;
      if (this.rebuild) {
         for(var9 = 0; var9 < this.graphs.size(); ++var9) {
            PolygonalMap2.VisibilityGraph var2 = (PolygonalMap2.VisibilityGraph)this.graphs.get(var9);
            var2.release();
         }

         this.squareToNode.forEachValue(this.releaseNodeProc);
         this.createVisibilityGraphs();
         this.rebuild = false;
      }

      var9 = 2;

      while(!this.requests.isEmpty()) {
         PolygonalMap2.PathFindRequest var7 = (PolygonalMap2.PathFindRequest)this.requests.removeFirst();
         if (var7.cancel) {
            this.requestToMain.add(var7);
         } else {
            try {
               this.findPath(var7, false);
            } catch (Exception var4) {
               var4.printStackTrace();
            }

            this.requestToMain.add(var7);
            --var9;
            if (var9 == 0) {
               break;
            }
         }
      }

   }

   public PolygonalMap2.PathFindRequest addRequest(PolygonalMap2.IPathfinder var1, Mover var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.cancelRequest(var2);
      PolygonalMap2.PathFindRequest var9 = PolygonalMap2.PathFindRequest.alloc().init(var1, var2, var3, var4, var5, var6, var7, var8);
      this.requestMap.put(var2, var9);
      PolygonalMap2.PathRequestTask var10 = PolygonalMap2.PathRequestTask.alloc().init(this, var9);
      this.requestTaskQueue.add(var10);
      this.thread.wake();
      return var9;
   }

   public void cancelRequest(Mover var1) {
      PolygonalMap2.PathFindRequest var2 = (PolygonalMap2.PathFindRequest)this.requestMap.remove(var1);
      if (var2 != null) {
         var2.cancel = true;
      }

   }

   private void supercover(float var1, float var2, float var3, float var4, int var5, PolygonalMap2.PointPool var6, ArrayList var7) {
      double var8 = (double)Math.abs(var3 - var1);
      double var10 = (double)Math.abs(var4 - var2);
      int var12 = (int)Math.floor((double)var1);
      int var13 = (int)Math.floor((double)var2);
      int var14 = 1;
      byte var15;
      double var17;
      if (var8 == 0.0D) {
         var15 = 0;
         var17 = Double.POSITIVE_INFINITY;
      } else if (var3 > var1) {
         var15 = 1;
         var14 += (int)Math.floor((double)var3) - var12;
         var17 = (Math.floor((double)var1) + 1.0D - (double)var1) * var10;
      } else {
         var15 = -1;
         var14 += var12 - (int)Math.floor((double)var3);
         var17 = ((double)var1 - Math.floor((double)var1)) * var10;
      }

      byte var16;
      if (var10 == 0.0D) {
         var16 = 0;
         var17 -= Double.POSITIVE_INFINITY;
      } else if (var4 > var2) {
         var16 = 1;
         var14 += (int)Math.floor((double)var4) - var13;
         var17 -= (Math.floor((double)var2) + 1.0D - (double)var2) * var8;
      } else {
         var16 = -1;
         var14 += var13 - (int)Math.floor((double)var4);
         var17 -= ((double)var2 - Math.floor((double)var2)) * var8;
      }

      for(; var14 > 0; --var14) {
         PolygonalMap2.Point var19 = var6.alloc().init(var12, var13);
         if (var7.contains(var19)) {
            var6.release(var19);
         } else {
            var7.add(var19);
         }

         if (var17 > 0.0D) {
            var13 += var16;
            var17 -= var8;
         } else {
            var12 += var15;
            var17 += var10;
         }
      }

   }

   public boolean lineClearCollide(float var1, float var2, float var3, float var4, int var5) {
      return this.lineClearCollide(var1, var2, var3, var4, var5, (IsoMovingObject)null);
   }

   public boolean lineClearCollide(float var1, float var2, float var3, float var4, int var5, IsoMovingObject var6) {
      return this.lineClearCollide(var1, var2, var3, var4, var5, var6, true, true);
   }

   public boolean lineClearCollide(float var1, float var2, float var3, float var4, int var5, IsoMovingObject var6, boolean var7, boolean var8) {
      BaseVehicle var9 = null;
      if (var6 instanceof IsoGameCharacter) {
         var9 = ((IsoGameCharacter)var6).getVehicle();
      } else if (var6 instanceof BaseVehicle) {
         var9 = (BaseVehicle)var6;
      }

      boolean var10 = Core.bDebug && DebugOptions.instance.PolymapRenderLineClearCollide.getValue();
      return this.lccMain.isNotClear(this, var1, var2, var3, var4, var5, var10, var9, var7, var8);
   }

   public boolean intersectLineWithVehicle(float var1, float var2, float var3, float var4, BaseVehicle var5, Vector2 var6) {
      float[] var7 = this.tempFloats;
      var7[0] = var5.getPoly().x1;
      var7[1] = var5.getPoly().y1;
      var7[2] = var5.getPoly().x2;
      var7[3] = var5.getPoly().y2;
      var7[4] = var5.getPoly().x3;
      var7[5] = var5.getPoly().y3;
      var7[6] = var5.getPoly().x4;
      var7[7] = var5.getPoly().y4;
      float var8 = Float.MAX_VALUE;

      for(int var9 = 0; var9 < 8; var9 += 2) {
         float var10 = var7[var9 % 8];
         float var11 = var7[(var9 + 1) % 8];
         float var12 = var7[(var9 + 2) % 8];
         float var13 = var7[(var9 + 3) % 8];
         double var14 = (double)((var13 - var11) * (var3 - var1) - (var12 - var10) * (var4 - var2));
         if (var14 == 0.0D) {
            return false;
         }

         double var16 = (double)((var12 - var10) * (var2 - var11) - (var13 - var11) * (var1 - var10)) / var14;
         double var18 = (double)((var3 - var1) * (var2 - var11) - (var4 - var2) * (var1 - var10)) / var14;
         if (var16 >= 0.0D && var16 <= 1.0D && var18 >= 0.0D && var18 <= 1.0D) {
            float var20 = (float)((double)var1 + var16 * (double)(var3 - var1));
            float var21 = (float)((double)var2 + var16 * (double)(var4 - var2));
            float var22 = IsoUtils.DistanceTo(var1, var2, var20, var21);
            if (var22 < var8) {
               var6.set(var20, var21);
               var8 = var22;
            }
         }
      }

      return var8 < Float.MAX_VALUE;
   }

   public void resolveCollision(IsoGameCharacter var1, float var2, float var3) {
      this.collideWithObstacles.resolveCollision(var1, var2, var3);
   }

   private static class ConnectedRegions {
      PolygonalMap2 map;
      HashSet doneChunks = new HashSet();
      int minX;
      int minY;
      int maxX;
      int maxY;
      int MINX;
      int MINY;
      int WIDTH;
      int HEIGHT;
      BooleanGrid visited;
      int[] stack;
      int stackLen;
      int[] choices;
      int choicesLen;

      private ConnectedRegions() {
         this.visited = new BooleanGrid(this.WIDTH, this.WIDTH);
      }

      void findAdjacentChunks(int var1, int var2) {
         this.doneChunks.clear();
         this.minX = this.minY = Integer.MAX_VALUE;
         this.maxX = this.maxY = Integer.MIN_VALUE;
         PolygonalMap2.Chunk var3 = this.map.getChunkFromSquarePos(var1, var2);
         this.findAdjacentChunks(var3);
      }

      void findAdjacentChunks(PolygonalMap2.Chunk var1) {
         if (var1 != null && !this.doneChunks.contains(var1)) {
            this.minX = Math.min(this.minX, var1.wx);
            this.minY = Math.min(this.minY, var1.wy);
            this.maxX = Math.max(this.maxX, var1.wx);
            this.maxY = Math.max(this.maxY, var1.wy);
            this.doneChunks.add(var1);
            PolygonalMap2.Chunk var2 = this.map.getChunkFromChunkPos(var1.wx - 1, var1.wy);
            PolygonalMap2.Chunk var3 = this.map.getChunkFromChunkPos(var1.wx, var1.wy - 1);
            PolygonalMap2.Chunk var4 = this.map.getChunkFromChunkPos(var1.wx + 1, var1.wy);
            PolygonalMap2.Chunk var5 = this.map.getChunkFromChunkPos(var1.wx, var1.wy + 1);
            this.findAdjacentChunks(var2);
            this.findAdjacentChunks(var3);
            this.findAdjacentChunks(var4);
            this.findAdjacentChunks(var5);
         }
      }

      void floodFill(int var1, int var2) {
         this.findAdjacentChunks(var1, var2);
         this.MINX = this.minX * 10;
         this.MINY = this.minY * 10;
         this.WIDTH = (this.maxX - this.minX + 1) * 10;
         this.HEIGHT = (this.maxY - this.minY + 1) * 10;
         this.visited = new BooleanGrid(this.WIDTH, this.WIDTH);
         this.stack = new int[this.WIDTH * this.WIDTH];
         this.choices = new int[this.WIDTH * this.HEIGHT];
         this.stackLen = 0;
         this.choicesLen = 0;
         if (this.push(var1, var2)) {
            int var3;
            label81:
            while((var3 = this.pop()) != -1) {
               int var4 = this.MINX + (var3 & '\uffff');

               int var5;
               for(var5 = this.MINY + (var3 >> 16) & '\uffff'; this.shouldVisit(var4, var5, var4, var5 - 1); --var5) {
               }

               boolean var6 = false;
               boolean var7 = false;

               while(this.visit(var4, var5)) {
                  if (!var6 && this.shouldVisit(var4, var5, var4 - 1, var5)) {
                     if (!this.push(var4 - 1, var5)) {
                        return;
                     }

                     var6 = true;
                  } else if (var6 && !this.shouldVisit(var4, var5, var4 - 1, var5)) {
                     var6 = false;
                  } else if (var6 && !this.shouldVisit(var4 - 1, var5, var4 - 1, var5 - 1) && !this.push(var4 - 1, var5)) {
                     return;
                  }

                  if (!var7 && this.shouldVisit(var4, var5, var4 + 1, var5)) {
                     if (!this.push(var4 + 1, var5)) {
                        return;
                     }

                     var7 = true;
                  } else if (var7 && !this.shouldVisit(var4, var5, var4 + 1, var5)) {
                     var7 = false;
                  } else if (var7 && !this.shouldVisit(var4 + 1, var5, var4 + 1, var5 - 1) && !this.push(var4 + 1, var5)) {
                     return;
                  }

                  ++var5;
                  if (!this.shouldVisit(var4, var5 - 1, var4, var5)) {
                     continue label81;
                  }
               }

               return;
            }

            System.out.println("#choices=" + this.choicesLen);
         }
      }

      boolean shouldVisit(int var1, int var2, int var3, int var4) {
         if (var3 < this.MINX + this.WIDTH && var3 >= this.MINX) {
            if (var4 < this.MINY + this.WIDTH && var4 >= this.MINY) {
               if (this.visited.getValue(this.gridX(var3), this.gridY(var4))) {
                  return false;
               } else {
                  PolygonalMap2.Square var5 = PolygonalMap2.instance.getSquare(var1, var2, 0);
                  PolygonalMap2.Square var6 = PolygonalMap2.instance.getSquare(var3, var4, 0);
                  if (var5 != null && var6 != null) {
                     return !this.isBlocked(var5, var6, false);
                  } else {
                     return false;
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      boolean visit(int var1, int var2) {
         if (this.choicesLen >= this.WIDTH * this.WIDTH) {
            return false;
         } else {
            this.choices[this.choicesLen++] = this.gridY(var2) << 16 | (short)this.gridX(var1);
            this.visited.setValue(this.gridX(var1), this.gridY(var2), true);
            return true;
         }
      }

      boolean push(int var1, int var2) {
         if (this.stackLen >= this.WIDTH * this.WIDTH) {
            return false;
         } else {
            this.stack[this.stackLen++] = this.gridY(var2) << 16 | (short)this.gridX(var1);
            return true;
         }
      }

      int pop() {
         return this.stackLen == 0 ? -1 : this.stack[--this.stackLen];
      }

      int gridX(int var1) {
         return var1 - this.MINX;
      }

      int gridY(int var1) {
         return var1 - this.MINY;
      }

      boolean isBlocked(PolygonalMap2.Square var1, PolygonalMap2.Square var2, boolean var3) {
         assert Math.abs(var1.x - var2.x) <= 1;

         assert Math.abs(var1.y - var2.y) <= 1;

         assert var1.z == var2.z;

         assert var1 != var2;

         boolean var4 = var2.x < var1.x;
         boolean var5 = var2.x > var1.x;
         boolean var6 = var2.y < var1.y;
         boolean var7 = var2.y > var1.y;
         if (var2.isReallySolid()) {
            return true;
         } else if (var2.y < var1.y && var1.has((short)64)) {
            return true;
         } else if (var2.x < var1.x && var1.has((short)8)) {
            return true;
         } else if (var2.y > var1.y && var2.x == var1.x && var2.has((short)64)) {
            return true;
         } else if (var2.x > var1.x && var2.y == var1.y && var2.has((short)8)) {
            return true;
         } else if (var2.x != var1.x && var2.has((short)448)) {
            return true;
         } else if (var2.y != var1.y && var2.has((short)56)) {
            return true;
         } else if (var2.x != var1.x && var1.has((short)448)) {
            return true;
         } else if (var2.y != var1.y && var1.has((short)56)) {
            return true;
         } else if (!var2.has((short)512) && !var2.has((short)504)) {
            return true;
         } else {
            boolean var8 = var6 && var1.has((short)4) && (var1.x != var2.x || var3 || !var1.has((short)16384));
            boolean var9 = var4 && var1.has((short)2) && (var1.y != var2.y || var3 || !var1.has((short)8192));
            boolean var10 = var7 && var2.has((short)4) && (var1.x != var2.x || var3 || !var2.has((short)16384));
            boolean var11 = var5 && var2.has((short)2) && (var1.y != var2.y || var3 || !var2.has((short)8192));
            if (!var8 && !var9 && !var10 && !var11) {
               boolean var12 = var2.x != var1.x && var2.y != var1.y;
               if (var12) {
                  PolygonalMap2.Square var13 = PolygonalMap2.instance.getSquare(var1.x, var2.y, var1.z);
                  PolygonalMap2.Square var14 = PolygonalMap2.instance.getSquare(var2.x, var1.y, var1.z);

                  assert var13 != var1 && var13 != var2;

                  assert var14 != var1 && var14 != var2;

                  if (var2.x == var1.x + 1 && var2.y == var1.y + 1 && var13 != null && var14 != null && var13.has((short)4096) && var14.has((short)2048)) {
                     return true;
                  } else if (var2.x == var1.x - 1 && var2.y == var1.y - 1 && var13 != null && var14 != null && var13.has((short)2048) && var14.has((short)4096)) {
                     return true;
                  } else if (var13 != null && this.isBlocked(var1, var13, true)) {
                     return true;
                  } else if (var14 != null && this.isBlocked(var1, var14, true)) {
                     return true;
                  } else if (var13 != null && this.isBlocked(var2, var13, true)) {
                     return true;
                  } else if (var14 != null && this.isBlocked(var2, var14, true)) {
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return true;
            }
         }
      }
   }

   private static class LineClearCollideMain {
      Vector2 perp = new Vector2();
      ArrayList pts = new ArrayList();
      PolygonalMap2.VehicleRect sweepAABB = new PolygonalMap2.VehicleRect();
      PolygonalMap2.VehicleRect vehicleAABB = new PolygonalMap2.VehicleRect();
      PolygonalMap2.VehiclePoly vehiclePoly = new PolygonalMap2.VehiclePoly();
      Vector2[] polyVec = new Vector2[4];
      Vector2[] vehicleVec = new Vector2[4];
      PolygonalMap2.PointPool pointPool = new PolygonalMap2.PointPool();
      PolygonalMap2.LiangBarsky LB = new PolygonalMap2.LiangBarsky();

      LineClearCollideMain() {
         for(int var1 = 0; var1 < 4; ++var1) {
            this.polyVec[var1] = new Vector2();
            this.vehicleVec[var1] = new Vector2();
         }

      }

      private float clamp(float var1, float var2, float var3) {
         if (var1 < var2) {
            var1 = var2;
         }

         if (var1 > var3) {
            var1 = var3;
         }

         return var1;
      }

      boolean canStandAt(PolygonalMap2 var1, float var2, float var3, float var4, BaseVehicle var5, boolean var6, boolean var7) {
         int var8 = (int)Math.floor((double)(var2 - 0.3F));
         int var9 = (int)Math.floor((double)(var3 - 0.3F));
         int var10 = (int)Math.ceil((double)(var2 + 0.3F));
         int var11 = (int)Math.ceil((double)(var3 + 0.3F));

         int var12;
         int var13;
         for(var12 = var9; var12 < var11; ++var12) {
            for(var13 = var8; var13 < var10; ++var13) {
               IsoGridSquare var14 = IsoWorld.instance.CurrentCell.getGridSquare(var13, var12, (int)var4);
               float var15;
               float var16;
               float var17;
               float var18;
               float var19;
               if (var14 != null && !var14.isSolid() && (!var14.isSolidTrans() || var14.isAdjacentToWindow()) && !var14.HasStairs()) {
                  label144: {
                     if (var14.SolidFloorCached) {
                        if (!var14.SolidFloor) {
                           break label144;
                        }
                     } else if (!var14.TreatAsSolidFloor()) {
                        break label144;
                     }

                     if (var7) {
                        continue;
                     }

                     if (var14.Is(IsoFlagType.collideW) || !var6 && var14.hasBlockedDoor(false)) {
                        var15 = (float)var13;
                        var16 = this.clamp(var3, (float)var12, (float)(var12 + 1));
                        var17 = var2 - var15;
                        var18 = var3 - var16;
                        var19 = var17 * var17 + var18 * var18;
                        if (var19 < 0.09F) {
                           return false;
                        }
                     }

                     if (!var14.Is(IsoFlagType.collideN) && (var6 || !var14.hasBlockedDoor(true))) {
                        continue;
                     }

                     var15 = this.clamp(var2, (float)var13, (float)(var13 + 1));
                     var16 = (float)var12;
                     var17 = var2 - var15;
                     var18 = var3 - var16;
                     var19 = var17 * var17 + var18 * var18;
                     if (var19 < 0.09F) {
                        return false;
                     }
                     continue;
                  }
               }

               if (var7) {
                  if (var2 >= (float)var13 && var3 >= (float)var12 && var2 < (float)(var13 + 1) && var3 < (float)(var12 + 1)) {
                     return false;
                  }
               } else {
                  var15 = this.clamp(var2, (float)var13, (float)(var13 + 1));
                  var16 = this.clamp(var3, (float)var12, (float)(var12 + 1));
                  var17 = var2 - var15;
                  var18 = var3 - var16;
                  var19 = var17 * var17 + var18 * var18;
                  if (var19 < 0.09F) {
                     return false;
                  }
               }
            }
         }

         var12 = ((int)var2 - 4) / 10 - 1;
         var13 = ((int)var3 - 4) / 10 - 1;
         int var21 = (int)Math.ceil((double)((var2 + 4.0F) / 10.0F)) + 1;
         int var22 = (int)Math.ceil((double)((var3 + 4.0F) / 10.0F)) + 1;

         for(int var23 = var13; var23 < var22; ++var23) {
            for(int var24 = var12; var24 < var21; ++var24) {
               IsoChunk var25 = GameServer.bServer ? ServerMap.instance.getChunk(var24, var23) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var24 * 10, var23 * 10, 0);
               if (var25 != null) {
                  for(int var26 = 0; var26 < var25.vehicles.size(); ++var26) {
                     BaseVehicle var20 = (BaseVehicle)var25.vehicles.get(var26);
                     if (var20 != var5 && var20.circleIntersects(var2, var3, var4, 0.3F)) {
                        return false;
                     }
                  }
               }
            }
         }

         return true;
      }

      public void drawCircle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         double var9 = (double)var1 + (double)var4 * Math.cos(Math.toRadians(0.0D));
         double var11 = (double)var2 + (double)var4 * Math.sin(Math.toRadians(0.0D));

         for(int var13 = 1; var13 <= 16; ++var13) {
            double var14 = (double)var1 + (double)var4 * Math.cos(Math.toRadians((double)(var13 * 360 / 16)));
            double var16 = (double)var2 + (double)var4 * Math.sin(Math.toRadians((double)(var13 * 360 / 16)));
            LineDrawer.addLine((float)var9, (float)var11, var3, (float)var14, (float)var16, var3, var5, var6, var7, (String)null, true);
            var9 = var14;
            var11 = var16;
         }

      }

      boolean isNotClear(PolygonalMap2 var1, float var2, float var3, float var4, float var5, int var6, boolean var7, BaseVehicle var8, boolean var9, boolean var10) {
         IsoGridSquare var11 = IsoWorld.instance.CurrentCell.getGridSquare((int)var2, (int)var3, var6);
         if (var11 != null && var11.HasStairs()) {
            return !var11.isSameStaircase((int)var4, (int)var5, var6);
         } else if (!this.canStandAt(var1, var4, var5, (float)var6, var8, var9, var10)) {
            if (var7) {
               this.drawCircle(var4, var5, (float)var6, 0.3F, 1.0F, 0.0F, 0.0F, 1.0F);
            }

            return true;
         } else {
            float var12 = var5 - var3;
            float var13 = -(var4 - var2);
            this.perp.set(var12, var13);
            this.perp.normalize();
            float var14 = var2 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float var15 = var3 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            float var16 = var4 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float var17 = var5 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            this.perp.set(-var12, -var13);
            this.perp.normalize();
            float var18 = var2 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float var19 = var3 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            float var20 = var4 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float var21 = var5 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;

            int var22;
            for(var22 = 0; var22 < this.pts.size(); ++var22) {
               this.pointPool.release((PolygonalMap2.Point)this.pts.get(var22));
            }

            this.pts.clear();
            this.pts.add(this.pointPool.alloc().init((int)var2, (int)var3));
            if ((int)var2 != (int)var4 || (int)var3 != (int)var5) {
               this.pts.add(this.pointPool.alloc().init((int)var4, (int)var5));
            }

            var1.supercover(var14, var15, var16, var17, var6, this.pointPool, this.pts);
            var1.supercover(var18, var19, var20, var21, var6, this.pointPool, this.pts);
            if (var7) {
               for(var22 = 0; var22 < this.pts.size(); ++var22) {
                  PolygonalMap2.Point var23 = (PolygonalMap2.Point)this.pts.get(var22);
                  LineDrawer.addLine((float)var23.x, (float)var23.y, (float)var6, (float)var23.x + 1.0F, (float)var23.y + 1.0F, (float)var6, 1.0F, 1.0F, 0.0F, (String)null, false);
               }
            }

            boolean var36 = false;

            float var25;
            float var26;
            for(int var37 = 0; var37 < this.pts.size(); ++var37) {
               PolygonalMap2.Point var24 = (PolygonalMap2.Point)this.pts.get(var37);
               var11 = IsoWorld.instance.CurrentCell.getGridSquare(var24.x, var24.y, var6);
               float var27;
               float var28;
               if (var11 != null && !var11.isSolid() && (!var11.isSolidTrans() || var11.isAdjacentToWindow()) && !var11.HasStairs()) {
                  label273: {
                     if (var11.SolidFloorCached) {
                        if (!var11.SolidFloor) {
                           break label273;
                        }
                     } else if (!var11.TreatAsSolidFloor()) {
                        break label273;
                     }

                     if (var11.Is(IsoFlagType.collideW) || !var9 && var11.hasBlockedDoor(false)) {
                        var25 = 0.3F;
                        var26 = 0.3F;
                        var27 = 0.3F;
                        var28 = 0.3F;
                        if (var2 < (float)var24.x && var4 < (float)var24.x) {
                           var25 = 0.0F;
                        } else if (var2 >= (float)var24.x && var4 >= (float)var24.x) {
                           var27 = 0.0F;
                        }

                        if (var3 < (float)var24.y && var5 < (float)var24.y) {
                           var26 = 0.0F;
                        } else if (var3 >= (float)(var24.y + 1) && var5 >= (float)(var24.y + 1)) {
                           var28 = 0.0F;
                        }

                        if (this.LB.lineRectIntersect(var2, var3, var4 - var2, var5 - var3, (float)var24.x - var25, (float)var24.y - var26, (float)var24.x + var27, (float)var24.y + 1.0F + var28)) {
                           if (!var7) {
                              return true;
                           }

                           LineDrawer.addLine((float)var24.x - var25, (float)var24.y - var26, (float)var6, (float)var24.x + var27, (float)var24.y + 1.0F + var28, (float)var6, 1.0F, 0.0F, 0.0F, (String)null, false);
                           var36 = true;
                        }
                     }

                     if (!var11.Is(IsoFlagType.collideN) && (var9 || !var11.hasBlockedDoor(true))) {
                        continue;
                     }

                     var25 = 0.3F;
                     var26 = 0.3F;
                     var27 = 0.3F;
                     var28 = 0.3F;
                     if (var2 < (float)var24.x && var4 < (float)var24.x) {
                        var25 = 0.0F;
                     } else if (var2 >= (float)(var24.x + 1) && var4 >= (float)(var24.x + 1)) {
                        var27 = 0.0F;
                     }

                     if (var3 < (float)var24.y && var5 < (float)var24.y) {
                        var26 = 0.0F;
                     } else if (var3 >= (float)var24.y && var5 >= (float)var24.y) {
                        var28 = 0.0F;
                     }

                     if (this.LB.lineRectIntersect(var2, var3, var4 - var2, var5 - var3, (float)var24.x - var25, (float)var24.y - var26, (float)var24.x + 1.0F + var27, (float)var24.y + var28)) {
                        if (!var7) {
                           return true;
                        }

                        LineDrawer.addLine((float)var24.x - var25, (float)var24.y - var26, (float)var6, (float)var24.x + 1.0F + var27, (float)var24.y + var28, (float)var6, 1.0F, 0.0F, 0.0F, (String)null, false);
                        var36 = true;
                     }
                     continue;
                  }
               }

               var25 = 0.3F;
               var26 = 0.3F;
               var27 = 0.3F;
               var28 = 0.3F;
               if (var2 < (float)var24.x && var4 < (float)var24.x) {
                  var25 = 0.0F;
               } else if (var2 >= (float)(var24.x + 1) && var4 >= (float)(var24.x + 1)) {
                  var27 = 0.0F;
               }

               if (var3 < (float)var24.y && var5 < (float)var24.y) {
                  var26 = 0.0F;
               } else if (var3 >= (float)(var24.y + 1) && var5 >= (float)(var24.y + 1)) {
                  var28 = 0.0F;
               }

               if (this.LB.lineRectIntersect(var2, var3, var4 - var2, var5 - var3, (float)var24.x - var25, (float)var24.y - var26, (float)var24.x + 1.0F + var27, (float)var24.y + 1.0F + var28)) {
                  if (!var7) {
                     return true;
                  }

                  LineDrawer.addLine((float)var24.x - var25, (float)var24.y - var26, (float)var6, (float)var24.x + 1.0F + var27, (float)var24.y + 1.0F + var28, (float)var6, 1.0F, 0.0F, 0.0F, (String)null, false);
                  var36 = true;
               }
            }

            this.perp.set(var12, var13);
            this.perp.normalize();
            var14 = var2 + this.perp.x * 0.3F;
            var15 = var3 + this.perp.y * 0.3F;
            var16 = var4 + this.perp.x * 0.3F;
            var17 = var5 + this.perp.y * 0.3F;
            this.perp.set(-var12, -var13);
            this.perp.normalize();
            var18 = var2 + this.perp.x * 0.3F;
            var19 = var3 + this.perp.y * 0.3F;
            var20 = var4 + this.perp.x * 0.3F;
            var21 = var5 + this.perp.y * 0.3F;
            float var38 = Math.min(var14, Math.min(var16, Math.min(var18, var20)));
            float var39 = Math.min(var15, Math.min(var17, Math.min(var19, var21)));
            var25 = Math.max(var14, Math.max(var16, Math.max(var18, var20)));
            var26 = Math.max(var15, Math.max(var17, Math.max(var19, var21)));
            this.sweepAABB.init((int)var38, (int)var39, (int)Math.ceil((double)var25) - (int)var38, (int)Math.ceil((double)var26) - (int)var39, var6);
            this.polyVec[0].set(var14, var15);
            this.polyVec[1].set(var16, var17);
            this.polyVec[2].set(var20, var21);
            this.polyVec[3].set(var18, var19);
            int var40 = this.sweepAABB.left() / 10 - 1;
            int var41 = this.sweepAABB.top() / 10 - 1;
            int var29 = (int)Math.ceil((double)((float)this.sweepAABB.right() / 10.0F)) + 1;
            int var30 = (int)Math.ceil((double)((float)this.sweepAABB.bottom() / 10.0F)) + 1;

            for(int var31 = var41; var31 < var30; ++var31) {
               for(int var32 = var40; var32 < var29; ++var32) {
                  IsoChunk var33 = GameServer.bServer ? ServerMap.instance.getChunk(var32, var31) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var32 * 10, var31 * 10, 0);
                  if (var33 != null) {
                     for(int var34 = 0; var34 < var33.vehicles.size(); ++var34) {
                        BaseVehicle var35 = (BaseVehicle)var33.vehicles.get(var34);
                        if (var35 != var8 && var35.VehicleID != -1) {
                           this.vehiclePoly.init(var35.getPoly());
                           this.vehiclePoly.getAABB(this.vehicleAABB);
                           if (this.vehicleAABB.intersects(this.sweepAABB) && this.polyVehicleIntersect(this.vehiclePoly, var7)) {
                              var36 = true;
                              if (!var7) {
                                 return true;
                              }
                           }
                        }
                     }
                  }
               }
            }

            return var36;
         }
      }

      boolean polyVehicleIntersect(PolygonalMap2.VehiclePoly var1, boolean var2) {
         this.vehicleVec[0].set(var1.x1, var1.y1);
         this.vehicleVec[1].set(var1.x2, var1.y2);
         this.vehicleVec[2].set(var1.x3, var1.y3);
         this.vehicleVec[3].set(var1.x4, var1.y4);
         boolean var3 = false;

         for(int var4 = 0; var4 < 4; ++var4) {
            Vector2 var5 = this.polyVec[var4];
            Vector2 var6 = var4 == 3 ? this.polyVec[0] : this.polyVec[var4 + 1];

            for(int var7 = 0; var7 < 4; ++var7) {
               Vector2 var8 = this.vehicleVec[var7];
               Vector2 var9 = var7 == 3 ? this.vehicleVec[0] : this.vehicleVec[var7 + 1];
               if (Line2D.linesIntersect((double)var5.x, (double)var5.y, (double)var6.x, (double)var6.y, (double)var8.x, (double)var8.y, (double)var9.x, (double)var9.y)) {
                  if (var2) {
                     LineDrawer.addLine(var8.x, var8.y, 0.0F, var9.x, var9.y, 0.0F, 1.0F, 0.0F, 0.0F, (String)null, true);
                  }

                  var3 = true;
               }
            }
         }

         return var3;
      }
   }

   private static class LineClearCollide {
      Vector2 perp = new Vector2();
      ArrayList pts = new ArrayList();
      PolygonalMap2.VehicleRect sweepAABB = new PolygonalMap2.VehicleRect();
      PolygonalMap2.VehicleRect vehicleAABB = new PolygonalMap2.VehicleRect();
      Vector2[] polyVec = new Vector2[4];
      Vector2[] vehicleVec = new Vector2[4];
      PolygonalMap2.PointPool pointPool = new PolygonalMap2.PointPool();
      PolygonalMap2.LiangBarsky LB = new PolygonalMap2.LiangBarsky();

      LineClearCollide() {
         for(int var1 = 0; var1 < 4; ++var1) {
            this.polyVec[var1] = new Vector2();
            this.vehicleVec[var1] = new Vector2();
         }

      }

      private float clamp(float var1, float var2, float var3) {
         if (var1 < var2) {
            var1 = var2;
         }

         if (var1 > var3) {
            var1 = var3;
         }

         return var1;
      }

      boolean canStandAt(PolygonalMap2 var1, float var2, float var3, float var4) {
         int var5 = (int)Math.floor((double)(var2 - 0.3F));
         int var6 = (int)Math.floor((double)(var3 - 0.3F));
         int var7 = (int)Math.ceil((double)(var2 + 0.3F));
         int var8 = (int)Math.ceil((double)(var3 + 0.3F));

         int var9;
         for(var9 = var6; var9 < var8; ++var9) {
            for(int var10 = var5; var10 < var7; ++var10) {
               PolygonalMap2.Square var11 = var1.getSquare(var10, var9, (int)var4);
               if ((var11 == null || var11.isReallySolid() || var11.has((short)504) || !var11.has((short)512)) && var2 >= (float)var10 && var3 >= (float)var9 && var2 < (float)(var10 + 1) && var3 < (float)(var9 + 1)) {
                  return false;
               }
            }
         }

         for(var9 = 0; var9 < var1.vehicles.size(); ++var9) {
            PolygonalMap2.Vehicle var12 = (PolygonalMap2.Vehicle)var1.vehicles.get(var9);
            if (this.isPointInPolygon_WindingNumber(var2, var3, var12.polyPlusRadius)) {
               return false;
            }
         }

         return true;
      }

      float isLeft(float var1, float var2, float var3, float var4, float var5, float var6) {
         return (var3 - var1) * (var6 - var2) - (var5 - var1) * (var4 - var2);
      }

      boolean isPointInPolygon_WindingNumber(float var1, float var2, PolygonalMap2.VehiclePoly var3) {
         this.polyVec[0].set(var3.x1, var3.y1);
         this.polyVec[1].set(var3.x2, var3.y2);
         this.polyVec[2].set(var3.x3, var3.y3);
         this.polyVec[3].set(var3.x4, var3.y4);
         int var4 = 0;

         for(int var5 = 0; var5 < 4; ++var5) {
            Vector2 var6 = this.polyVec[var5];
            Vector2 var7 = var5 == 3 ? this.polyVec[0] : this.polyVec[var5 + 1];
            if (var6.y <= var2) {
               if (var7.y > var2 && this.isLeft(var6.x, var6.y, var7.x, var7.y, var1, var2) > 0.0F) {
                  ++var4;
               }
            } else if (var7.y <= var2 && this.isLeft(var6.x, var6.y, var7.x, var7.y, var1, var2) < 0.0F) {
               --var4;
            }
         }

         return var4 != 0;
      }

      boolean isNotClear(PolygonalMap2 var1, float var2, float var3, float var4, float var5, int var6) {
         PolygonalMap2.Square var7 = var1.getSquare((int)var2, (int)var3, var6);
         if (var7 != null && var7.has((short)504)) {
            return true;
         } else if (!this.canStandAt(var1, var4, var5, (float)var6)) {
            return true;
         } else {
            float var8 = var5 - var3;
            float var9 = -(var4 - var2);
            this.perp.set(var8, var9);
            this.perp.normalize();
            float var10 = var2 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float var11 = var3 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            float var12 = var4 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float var13 = var5 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            this.perp.set(-var8, -var9);
            this.perp.normalize();
            float var14 = var2 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float var15 = var3 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            float var16 = var4 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float var17 = var5 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;

            int var18;
            for(var18 = 0; var18 < this.pts.size(); ++var18) {
               this.pointPool.release((PolygonalMap2.Point)this.pts.get(var18));
            }

            this.pts.clear();
            this.pts.add(this.pointPool.alloc().init((int)var2, (int)var3));
            if ((int)var2 != (int)var4 || (int)var3 != (int)var5) {
               this.pts.add(this.pointPool.alloc().init((int)var4, (int)var5));
            }

            var1.supercover(var10, var11, var12, var13, var6, this.pointPool, this.pts);
            var1.supercover(var14, var15, var16, var17, var6, this.pointPool, this.pts);

            float var20;
            float var21;
            for(var18 = 0; var18 < this.pts.size(); ++var18) {
               PolygonalMap2.Point var19 = (PolygonalMap2.Point)this.pts.get(var18);
               var7 = var1.getSquare(var19.x, var19.y, var6);
               float var22;
               float var23;
               if (var7 != null && !var7.isReallySolid() && !var7.has((short)504) && var7.has((short)512)) {
                  if (var7.has((short)2)) {
                     var20 = 0.3F;
                     var21 = 0.3F;
                     var22 = 0.3F;
                     var23 = 0.3F;
                     if (var2 < (float)var19.x && var4 < (float)var19.x) {
                        var20 = 0.0F;
                     } else if (var2 >= (float)var19.x && var4 >= (float)var19.x) {
                        var22 = 0.0F;
                     }

                     if (var3 < (float)var19.y && var5 < (float)var19.y) {
                        var21 = 0.0F;
                     } else if (var3 >= (float)(var19.y + 1) && var5 >= (float)(var19.y + 1)) {
                        var23 = 0.0F;
                     }

                     if (this.LB.lineRectIntersect(var2, var3, var4 - var2, var5 - var3, (float)var19.x - var20, (float)var19.y - var21, (float)var19.x + var22, (float)var19.y + 1.0F + var23)) {
                        return true;
                     }
                  }

                  if (var7.has((short)4)) {
                     var20 = 0.3F;
                     var21 = 0.3F;
                     var22 = 0.3F;
                     var23 = 0.3F;
                     if (var2 < (float)var19.x && var4 < (float)var19.x) {
                        var20 = 0.0F;
                     } else if (var2 >= (float)(var19.x + 1) && var4 >= (float)(var19.x + 1)) {
                        var22 = 0.0F;
                     }

                     if (var3 < (float)var19.y && var5 < (float)var19.y) {
                        var21 = 0.0F;
                     } else if (var3 >= (float)var19.y && var5 >= (float)var19.y) {
                        var23 = 0.0F;
                     }

                     if (this.LB.lineRectIntersect(var2, var3, var4 - var2, var5 - var3, (float)var19.x - var20, (float)var19.y - var21, (float)var19.x + 1.0F + var22, (float)var19.y + var23)) {
                        return true;
                     }
                  }
               } else {
                  var20 = 0.3F;
                  var21 = 0.3F;
                  var22 = 0.3F;
                  var23 = 0.3F;
                  if (var2 < (float)var19.x && var4 < (float)var19.x) {
                     var20 = 0.0F;
                  } else if (var2 >= (float)(var19.x + 1) && var4 >= (float)(var19.x + 1)) {
                     var22 = 0.0F;
                  }

                  if (var3 < (float)var19.y && var5 < (float)var19.y) {
                     var21 = 0.0F;
                  } else if (var3 >= (float)(var19.y + 1) && var5 >= (float)(var19.y + 1)) {
                     var23 = 0.0F;
                  }

                  if (this.LB.lineRectIntersect(var2, var3, var4 - var2, var5 - var3, (float)var19.x - var20, (float)var19.y - var21, (float)var19.x + 1.0F + var22, (float)var19.y + 1.0F + var23)) {
                     return true;
                  }
               }
            }

            this.perp.set(var8, var9);
            this.perp.normalize();
            var10 = var2 + this.perp.x * 0.3F;
            var11 = var3 + this.perp.y * 0.3F;
            var12 = var4 + this.perp.x * 0.3F;
            var13 = var5 + this.perp.y * 0.3F;
            this.perp.set(-var8, -var9);
            this.perp.normalize();
            var14 = var2 + this.perp.x * 0.3F;
            var15 = var3 + this.perp.y * 0.3F;
            var16 = var4 + this.perp.x * 0.3F;
            var17 = var5 + this.perp.y * 0.3F;
            float var25 = Math.min(var10, Math.min(var12, Math.min(var14, var16)));
            float var26 = Math.min(var11, Math.min(var13, Math.min(var15, var17)));
            var20 = Math.max(var10, Math.max(var12, Math.max(var14, var16)));
            var21 = Math.max(var11, Math.max(var13, Math.max(var15, var17)));
            this.sweepAABB.init((int)var25, (int)var26, (int)Math.ceil((double)var20) - (int)var25, (int)Math.ceil((double)var21) - (int)var26, var6);
            this.polyVec[0].set(var10, var11);
            this.polyVec[1].set(var12, var13);
            this.polyVec[2].set(var16, var17);
            this.polyVec[3].set(var14, var15);

            for(int var27 = 0; var27 < var1.vehicles.size(); ++var27) {
               PolygonalMap2.Vehicle var28 = (PolygonalMap2.Vehicle)var1.vehicles.get(var27);
               PolygonalMap2.VehicleRect var24 = var28.poly.getAABB(this.vehicleAABB);
               if (var24.intersects(this.sweepAABB) && this.polyVehicleIntersect(var28.poly)) {
                  return true;
               }
            }

            return false;
         }
      }

      boolean polyVehicleIntersect(PolygonalMap2.VehiclePoly var1) {
         this.vehicleVec[0].set(var1.x1, var1.y1);
         this.vehicleVec[1].set(var1.x2, var1.y2);
         this.vehicleVec[2].set(var1.x3, var1.y3);
         this.vehicleVec[3].set(var1.x4, var1.y4);
         boolean var2 = false;

         for(int var3 = 0; var3 < 4; ++var3) {
            Vector2 var4 = this.polyVec[var3];
            Vector2 var5 = var3 == 3 ? this.polyVec[0] : this.polyVec[var3 + 1];

            for(int var6 = 0; var6 < 4; ++var6) {
               Vector2 var7 = this.vehicleVec[var6];
               Vector2 var8 = var6 == 3 ? this.vehicleVec[0] : this.vehicleVec[var6 + 1];
               if (Line2D.linesIntersect((double)var4.x, (double)var4.y, (double)var5.x, (double)var5.y, (double)var7.x, (double)var7.y, (double)var8.x, (double)var8.y)) {
                  var2 = true;
               }
            }
         }

         return var2;
      }
   }

   private static class LiangBarsky {
      private double[] p;
      private double[] q;

      private LiangBarsky() {
         this.p = new double[4];
         this.q = new double[4];
      }

      private boolean lineRectIntersect(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         this.p[0] = (double)(-var3);
         this.p[1] = (double)var3;
         this.p[2] = (double)(-var4);
         this.p[3] = (double)var4;
         this.q[0] = (double)(var1 - var5);
         this.q[1] = (double)(var7 - var1);
         this.q[2] = (double)(var2 - var6);
         this.q[3] = (double)(var8 - var2);
         double var9 = 0.0D;
         double var11 = 1.0D;

         for(int var13 = 0; var13 < 4; ++var13) {
            if (this.p[var13] == 0.0D) {
               if (this.q[var13] < 0.0D) {
                  return false;
               }
            } else {
               double var14 = this.q[var13] / this.p[var13];
               if (this.p[var13] < 0.0D && var9 < var14) {
                  var9 = var14;
               } else if (this.p[var13] > 0.0D && var11 > var14) {
                  var11 = var14;
               }
            }
         }

         if (var9 > var11) {
            return false;
         } else {
            return true;
         }
      }

      // $FF: synthetic method
      LiangBarsky(Object var1) {
         this();
      }
   }

   private static class PointPool {
      ArrayDeque pool;

      private PointPool() {
         this.pool = new ArrayDeque();
      }

      PolygonalMap2.Point alloc() {
         return this.pool.isEmpty() ? new PolygonalMap2.Point() : (PolygonalMap2.Point)this.pool.pop();
      }

      void release(PolygonalMap2.Point var1) {
         this.pool.push(var1);
      }

      // $FF: synthetic method
      PointPool(Object var1) {
         this();
      }
   }

   private static class Point {
      int x;
      int y;

      private Point() {
      }

      PolygonalMap2.Point init(int var1, int var2) {
         this.x = var1;
         this.y = var2;
         return this;
      }

      public boolean equals(Object var1) {
         return var1 instanceof PolygonalMap2.Point && ((PolygonalMap2.Point)var1).x == this.x && ((PolygonalMap2.Point)var1).y == this.y;
      }

      // $FF: synthetic method
      Point(Object var1) {
         this();
      }
   }

   private static class PathRequestTask {
      PolygonalMap2 map;
      PolygonalMap2.PathFindRequest request;
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.PathRequestTask init(PolygonalMap2 var1, PolygonalMap2.PathFindRequest var2) {
         this.map = var1;
         this.request = var2;
         return this;
      }

      void execute() {
         if (this.request.mover instanceof IsoPlayer) {
            this.map.requests.addFirst(this.request);
         } else {
            this.map.requests.add(this.request);
         }

      }

      static PolygonalMap2.PathRequestTask alloc() {
         synchronized(pool) {
            return pool.isEmpty() ? new PolygonalMap2.PathRequestTask() : (PolygonalMap2.PathRequestTask)pool.pop();
         }
      }

      public void release() {
         synchronized(pool) {
            assert !pool.contains(this);

            pool.push(this);
         }
      }
   }

   static class PathFindRequest {
      PolygonalMap2.IPathfinder finder;
      Mover mover;
      float startX;
      float startY;
      float startZ;
      float targetX;
      float targetY;
      float targetZ;
      PolygonalMap2.Path path = new PolygonalMap2.Path();
      boolean cancel = false;
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.PathFindRequest init(PolygonalMap2.IPathfinder var1, Mover var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         this.finder = var1;
         this.mover = var2;
         this.startX = var3;
         this.startY = var4;
         this.startZ = var5;
         this.targetX = var6;
         this.targetY = var7;
         this.targetZ = var8;
         this.path.clear();
         this.cancel = false;
         return this;
      }

      static PolygonalMap2.PathFindRequest alloc() {
         return pool.isEmpty() ? new PolygonalMap2.PathFindRequest() : (PolygonalMap2.PathFindRequest)pool.pop();
      }

      public void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   public interface IPathfinder {
      void Succeeded(PolygonalMap2.Path var1, Mover var2);

      void Failed(Mover var1);
   }

   private class PMThread extends Thread {
      public boolean bStop;
      public final Object notifier;

      private PMThread() {
         this.notifier = new Object();
      }

      public void run() {
         while(!this.bStop) {
            try {
               this.runInner();
            } catch (Exception var2) {
               ExceptionLogger.logException(var2);
            }
         }

      }

      private void runInner() {
         PolygonalMap2.this.sync.startFrame();
         synchronized(PolygonalMap2.this.renderLock) {
            PolygonalMap2.instance.updateThread();
         }

         PolygonalMap2.this.sync.endFrame();

         while(this.shouldWait()) {
            synchronized(this.notifier) {
               try {
                  this.notifier.wait();
               } catch (InterruptedException var4) {
               }
            }
         }

      }

      private boolean shouldWait() {
         if (this.bStop) {
            return false;
         } else if (!PolygonalMap2.instance.chunkTaskQueue.isEmpty()) {
            return false;
         } else if (!PolygonalMap2.instance.squareTaskQueue.isEmpty()) {
            return false;
         } else if (!PolygonalMap2.instance.vehicleTaskQueue.isEmpty()) {
            return false;
         } else if (!PolygonalMap2.instance.requestTaskQueue.isEmpty()) {
            return false;
         } else {
            return PolygonalMap2.instance.requests.isEmpty();
         }
      }

      void wake() {
         synchronized(this.notifier) {
            this.notifier.notify();
         }
      }

      // $FF: synthetic method
      PMThread(Object var2) {
         this();
      }
   }

   private static class Sync {
      private int fps;
      private long period;
      private long excess;
      private long beforeTime;
      private long overSleepTime;

      private Sync() {
         this.fps = 20;
         this.period = 1000000000L / (long)this.fps;
         this.beforeTime = System.nanoTime();
         this.overSleepTime = 0L;
      }

      void begin() {
         this.beforeTime = System.nanoTime();
         this.overSleepTime = 0L;
      }

      void startFrame() {
         this.excess = 0L;
      }

      void endFrame() {
         long var1 = System.nanoTime();
         long var3 = var1 - this.beforeTime;
         long var5 = this.period - var3 - this.overSleepTime;
         if (var5 > 0L) {
            try {
               Thread.sleep(var5 / 1000000L);
            } catch (InterruptedException var8) {
            }

            this.overSleepTime = System.nanoTime() - var1 - var5;
         } else {
            this.excess -= var5;
            this.overSleepTime = 0L;
         }

         this.beforeTime = System.nanoTime();
      }

      // $FF: synthetic method
      Sync(Object var1) {
         this();
      }
   }

   private static class VehicleState {
      BaseVehicle vehicle;
      float x;
      float y;
      float z;
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.VehicleState init(BaseVehicle var1) {
         this.vehicle = var1;
         this.x = var1.x;
         this.y = var1.y;
         this.z = var1.z;
         return this;
      }

      boolean check() {
         boolean var1 = this.x != this.vehicle.x || this.y != this.vehicle.y || (int)this.z != (int)this.vehicle.z;
         if (var1) {
            this.x = this.vehicle.x;
            this.y = this.vehicle.y;
            this.z = this.vehicle.z;
         }

         return var1;
      }

      static PolygonalMap2.VehicleState alloc() {
         return pool.isEmpty() ? new PolygonalMap2.VehicleState() : (PolygonalMap2.VehicleState)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class Cell {
      PolygonalMap2 map;
      public short cx;
      public short cy;
      public PolygonalMap2.Chunk[][] chunks;
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.Cell init(PolygonalMap2 var1, int var2, int var3) {
         this.map = var1;
         this.cx = (short)var2;
         this.cy = (short)var3;
         return this;
      }

      PolygonalMap2.Chunk getChunkFromChunkPos(int var1, int var2) {
         if (this.chunks == null) {
            return null;
         } else {
            var1 -= this.cx * 30;
            var2 -= this.cy * 30;
            return var1 >= 0 && var1 < 30 && var2 >= 0 && var2 < 30 ? this.chunks[var1][var2] : null;
         }
      }

      PolygonalMap2.Chunk allocChunkIfNeeded(int var1, int var2) {
         var1 -= this.cx * 30;
         var2 -= this.cy * 30;
         if (var1 >= 0 && var1 < 30 && var2 >= 0 && var2 < 30) {
            if (this.chunks == null) {
               this.chunks = new PolygonalMap2.Chunk[30][30];
            }

            if (this.chunks[var1][var2] == null) {
               this.chunks[var1][var2] = PolygonalMap2.Chunk.alloc();
            }

            this.chunks[var1][var2].init(this.cx * 30 + var1, this.cy * 30 + var2);
            return this.chunks[var1][var2];
         } else {
            return null;
         }
      }

      void removeChunk(int var1, int var2) {
         if (this.chunks != null) {
            var1 -= this.cx * 30;
            var2 -= this.cy * 30;
            if (var1 >= 0 && var1 < 30 && var2 >= 0 && var2 < 30) {
               PolygonalMap2.Chunk var3 = this.chunks[var1][var2];
               if (var3 != null) {
                  var3.release();
                  this.chunks[var1][var2] = null;
               }

            }
         }
      }

      static PolygonalMap2.Cell alloc() {
         return pool.isEmpty() ? new PolygonalMap2.Cell() : (PolygonalMap2.Cell)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class Chunk {
      short wx;
      short wy;
      PolygonalMap2.Square[][][] squares = new PolygonalMap2.Square[10][10][8];
      static ArrayDeque pool = new ArrayDeque();

      void init(int var1, int var2) {
         this.wx = (short)var1;
         this.wy = (short)var2;
      }

      PolygonalMap2.Square getSquare(int var1, int var2, int var3) {
         var1 -= this.wx * 10;
         var2 -= this.wy * 10;
         return var1 >= 0 && var1 < 10 && var2 >= 0 && var2 < 10 && var3 >= 0 && var3 < 8 ? this.squares[var1][var2][var3] : null;
      }

      void setData(PolygonalMap2.ChunkUpdateTask var1) {
         for(int var2 = 0; var2 < 8; ++var2) {
            for(int var3 = 0; var3 < 10; ++var3) {
               for(int var4 = 0; var4 < 10; ++var4) {
                  PolygonalMap2.Square var5 = this.squares[var4][var3][var2];
                  short var6 = var1.data[var4][var3][var2];
                  if (var6 == 0) {
                     if (var5 != null) {
                        var5.release();
                        this.squares[var4][var3][var2] = null;
                     }
                  } else {
                     if (var5 == null) {
                        var5 = PolygonalMap2.Square.alloc();
                        this.squares[var4][var3][var2] = var5;
                     }

                     var5.init(this.wx * 10 + var4, this.wy * 10 + var3, var2);
                     var5.bits = var6;
                  }
               }
            }
         }

      }

      boolean setData(PolygonalMap2.SquareUpdateTask var1) {
         int var2 = var1.x - this.wx * 10;
         int var3 = var1.y - this.wy * 10;
         if (var2 >= 0 && var2 < 10) {
            if (var3 >= 0 && var3 < 10) {
               PolygonalMap2.Square var4 = this.squares[var2][var3][var1.z];
               if (var1.bits == 0) {
                  if (var4 != null) {
                     var4.release();
                     this.squares[var2][var3][var1.z] = null;
                     return true;
                  }
               } else {
                  if (var4 == null) {
                     var4 = PolygonalMap2.Square.alloc().init(var1.x, var1.y, var1.z);
                     this.squares[var2][var3][var1.z] = var4;
                  }

                  if (var4.bits != var1.bits) {
                     var4.bits = var1.bits;
                     return true;
                  }
               }

               return false;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      static PolygonalMap2.Chunk alloc() {
         return pool.isEmpty() ? new PolygonalMap2.Chunk() : (PolygonalMap2.Chunk)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class Square {
      static int nextID = 1;
      Integer ID;
      int x;
      int y;
      int z;
      short bits;
      static ArrayDeque pool = new ArrayDeque();

      Square() {
         this.ID = nextID++;
      }

      PolygonalMap2.Square init(int var1, int var2, int var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
         return this;
      }

      boolean has(short var1) {
         return (this.bits & var1) != 0;
      }

      boolean isReallySolid() {
         return this.has((short)1) || this.has((short)1024) && !this.isAdjacentToWindow();
      }

      boolean isAdjacentToWindow() {
         if (!this.has((short)2048) && !this.has((short)4096)) {
            PolygonalMap2.Square var1 = PolygonalMap2.instance.getSquare(this.x, this.y + 1, this.z);
            if (var1 != null && var1.has((short)4096)) {
               return true;
            } else {
               PolygonalMap2.Square var2 = PolygonalMap2.instance.getSquare(this.x + 1, this.y, this.z);
               return var2 != null && var2.has((short)2048);
            }
         } else {
            return true;
         }
      }

      static PolygonalMap2.Square alloc() {
         return pool.isEmpty() ? new PolygonalMap2.Square() : (PolygonalMap2.Square)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class VehicleRemoveTask implements PolygonalMap2.IVehicleTask {
      PolygonalMap2 map;
      BaseVehicle vehicle;
      static ArrayDeque pool = new ArrayDeque();

      public void init(PolygonalMap2 var1, BaseVehicle var2) {
         this.map = var1;
         this.vehicle = var2;
      }

      public void execute() {
         PolygonalMap2.Vehicle var1 = (PolygonalMap2.Vehicle)this.map.vehicleMap.remove(this.vehicle);
         if (var1 != null) {
            this.map.vehicles.remove(var1);
            var1.release();
         }

         this.vehicle = null;
      }

      static PolygonalMap2.VehicleRemoveTask alloc() {
         synchronized(pool) {
            return pool.isEmpty() ? new PolygonalMap2.VehicleRemoveTask() : (PolygonalMap2.VehicleRemoveTask)pool.pop();
         }
      }

      public void release() {
         synchronized(pool) {
            assert !pool.contains(this);

            pool.push(this);
         }
      }
   }

   private static class VehicleUpdateTask implements PolygonalMap2.IVehicleTask {
      PolygonalMap2 map;
      BaseVehicle vehicle;
      PolygonalMap2.VehiclePoly poly = new PolygonalMap2.VehiclePoly();
      PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
      static ArrayDeque pool = new ArrayDeque();

      public void init(PolygonalMap2 var1, BaseVehicle var2) {
         this.map = var1;
         this.vehicle = var2;
         this.poly.init(var2.getPoly());
         this.polyPlusRadius.init(var2.getPolyPlusRadius());
      }

      public void execute() {
         PolygonalMap2.Vehicle var1 = (PolygonalMap2.Vehicle)this.map.vehicleMap.get(this.vehicle);
         var1.poly.init(this.poly);
         var1.polyPlusRadius.init(this.polyPlusRadius);
         this.vehicle = null;
      }

      static PolygonalMap2.VehicleUpdateTask alloc() {
         synchronized(pool) {
            return pool.isEmpty() ? new PolygonalMap2.VehicleUpdateTask() : (PolygonalMap2.VehicleUpdateTask)pool.pop();
         }
      }

      public void release() {
         synchronized(pool) {
            assert !pool.contains(this);

            pool.push(this);
         }
      }
   }

   private static class VehicleAddTask implements PolygonalMap2.IVehicleTask {
      PolygonalMap2 map;
      BaseVehicle vehicle;
      PolygonalMap2.VehiclePoly poly = new PolygonalMap2.VehiclePoly();
      PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
      static ArrayDeque pool = new ArrayDeque();

      public void init(PolygonalMap2 var1, BaseVehicle var2) {
         this.map = var1;
         this.vehicle = var2;
         this.poly.init(var2.getPoly());
         this.polyPlusRadius.init(var2.getPolyPlusRadius());
      }

      public void execute() {
         PolygonalMap2.Vehicle var1 = PolygonalMap2.Vehicle.alloc();
         var1.poly.init(this.poly);
         var1.polyPlusRadius.init(this.polyPlusRadius);
         this.map.vehicles.add(var1);
         this.map.vehicleMap.put(this.vehicle, var1);
         this.vehicle = null;
      }

      static PolygonalMap2.VehicleAddTask alloc() {
         synchronized(pool) {
            return pool.isEmpty() ? new PolygonalMap2.VehicleAddTask() : (PolygonalMap2.VehicleAddTask)pool.pop();
         }
      }

      public void release() {
         synchronized(pool) {
            assert !pool.contains(this);

            pool.push(this);
         }
      }
   }

   private interface IVehicleTask {
      void init(PolygonalMap2 var1, BaseVehicle var2);

      void execute();

      void release();
   }

   private static class SquareUpdateTask {
      PolygonalMap2 map;
      int x;
      int y;
      int z;
      short bits;
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.SquareUpdateTask init(PolygonalMap2 var1, IsoGridSquare var2) {
         this.map = var1;
         this.x = var2.x;
         this.y = var2.y;
         this.z = var2.z;
         this.bits = getBits(var2);
         return this;
      }

      void execute() {
         PolygonalMap2.Chunk var1 = this.map.getChunkFromChunkPos(this.x / 10, this.y / 10);
         if (var1 != null && var1.setData(this)) {
            this.map.rebuild = true;
         }

      }

      static short getBits(IsoGridSquare var0) {
         short var1 = 0;
         if (var0.Is(IsoFlagType.solidfloor)) {
            var1 = (short)(var1 | 512);
         }

         if (var0.isSolid()) {
            var1 = (short)(var1 | 1);
         }

         if (var0.isSolidTrans()) {
            var1 = (short)(var1 | 1024);
         }

         if (var0.Is(IsoFlagType.collideW)) {
            var1 = (short)(var1 | 2);
         }

         if (var0.Is(IsoFlagType.collideN)) {
            var1 = (short)(var1 | 4);
         }

         if (var0.Has(IsoObjectType.stairsTW)) {
            var1 = (short)(var1 | 8);
         }

         if (var0.Has(IsoObjectType.stairsMW)) {
            var1 = (short)(var1 | 16);
         }

         if (var0.Has(IsoObjectType.stairsBW)) {
            var1 = (short)(var1 | 32);
         }

         if (var0.Has(IsoObjectType.stairsTN)) {
            var1 = (short)(var1 | 64);
         }

         if (var0.Has(IsoObjectType.stairsMN)) {
            var1 = (short)(var1 | 128);
         }

         if (var0.Has(IsoObjectType.stairsBN)) {
            var1 = (short)(var1 | 256);
         }

         if (var0.Is(IsoFlagType.windowW) || var0.Is(IsoFlagType.WindowW)) {
            var1 = (short)(var1 | 2048);
         }

         if (var0.Is(IsoFlagType.windowN) || var0.Is(IsoFlagType.WindowN)) {
            var1 = (short)(var1 | 4096);
         }

         if (var0.Is(IsoFlagType.canPathW)) {
            var1 = (short)(var1 | 8192);
         }

         if (var0.Is(IsoFlagType.canPathN)) {
            var1 = (short)(var1 | 16384);
         }

         if (var0.Is("DoorWallW")) {
            var1 = (short)(var1 | 2);
            var1 = (short)(var1 | 8192);
         }

         if (var0.Is("DoorWallN")) {
            var1 = (short)(var1 | 4);
            var1 = (short)(var1 | 16384);
         }

         return var1;
      }

      static PolygonalMap2.SquareUpdateTask alloc() {
         synchronized(pool) {
            return pool.isEmpty() ? new PolygonalMap2.SquareUpdateTask() : (PolygonalMap2.SquareUpdateTask)pool.pop();
         }
      }

      public void release() {
         synchronized(pool) {
            assert !pool.contains(this);

            pool.push(this);
         }
      }
   }

   private static class ChunkRemoveTask implements PolygonalMap2.IChunkTask {
      PolygonalMap2 map;
      int wx;
      int wy;
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.ChunkRemoveTask init(PolygonalMap2 var1, IsoChunk var2) {
         this.map = var1;
         this.wx = var2.wx;
         this.wy = var2.wy;
         return this;
      }

      public void execute() {
         PolygonalMap2.Cell var1 = this.map.getCellFromChunkPos(this.wx, this.wy);
         var1.removeChunk(this.wx, this.wy);
      }

      static PolygonalMap2.ChunkRemoveTask alloc() {
         synchronized(pool) {
            return pool.isEmpty() ? new PolygonalMap2.ChunkRemoveTask() : (PolygonalMap2.ChunkRemoveTask)pool.pop();
         }
      }

      public void release() {
         synchronized(pool) {
            assert !pool.contains(this);

            pool.push(this);
         }
      }
   }

   private static class ChunkUpdateTask implements PolygonalMap2.IChunkTask {
      PolygonalMap2 map;
      int wx;
      int wy;
      short[][][] data = new short[10][10][8];
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.ChunkUpdateTask init(PolygonalMap2 var1, IsoChunk var2) {
         this.map = var1;
         this.wx = var2.wx;
         this.wy = var2.wy;

         for(int var3 = 0; var3 < 8; ++var3) {
            for(int var4 = 0; var4 < 10; ++var4) {
               for(int var5 = 0; var5 < 10; ++var5) {
                  IsoGridSquare var6 = var2.getGridSquare(var5, var4, var3);
                  if (var6 == null) {
                     this.data[var5][var4][var3] = 0;
                  } else {
                     this.data[var5][var4][var3] = PolygonalMap2.SquareUpdateTask.getBits(var6);
                  }
               }
            }
         }

         return this;
      }

      public void execute() {
         PolygonalMap2.Chunk var1 = this.map.allocChunkIfNeeded(this.wx, this.wy);
         var1.setData(this);
      }

      static PolygonalMap2.ChunkUpdateTask alloc() {
         synchronized(pool) {
            return pool.isEmpty() ? new PolygonalMap2.ChunkUpdateTask() : (PolygonalMap2.ChunkUpdateTask)pool.pop();
         }
      }

      public void release() {
         synchronized(pool) {
            assert !pool.contains(this);

            pool.push(this);
         }
      }
   }

   private interface IChunkTask {
      void execute();

      void release();
   }

   private static class VGAStar extends AStar {
      ArrayList graphs;
      ArrayList searchNodes;
      TIntObjectHashMap nodeMap;
      PolygonalMap2.GoalNode goalNode;
      TIntObjectHashMap squareToNode;
      Mover mover;
      final PolygonalMap2.VGAStar.InitProc initProc;

      private VGAStar() {
         this.searchNodes = new ArrayList();
         this.nodeMap = new TIntObjectHashMap();
         this.goalNode = new PolygonalMap2.GoalNode();
         this.squareToNode = new TIntObjectHashMap();
         this.initProc = new PolygonalMap2.VGAStar.InitProc();
      }

      PolygonalMap2.VGAStar init(ArrayList var1, TIntObjectHashMap var2) {
         this.setMaxSteps(5000);
         this.graphs = var1;
         this.searchNodes.clear();
         this.nodeMap.clear();
         this.squareToNode.clear();
         this.mover = null;
         var2.forEachEntry(this.initProc);
         return this;
      }

      PolygonalMap2.VisibilityGraph getVisGraphForSquare(PolygonalMap2.Square var1) {
         for(int var2 = 0; var2 < this.graphs.size(); ++var2) {
            PolygonalMap2.VisibilityGraph var3 = (PolygonalMap2.VisibilityGraph)this.graphs.get(var2);
            if (var3.contains(var1)) {
               return var3;
            }
         }

         return null;
      }

      boolean isSquareInCluster(PolygonalMap2.Square var1) {
         return this.getVisGraphForSquare(var1) != null;
      }

      PolygonalMap2.SearchNode getSearchNode(PolygonalMap2.Node var1) {
         if (var1.square != null) {
            return this.getSearchNode(var1.square);
         } else {
            PolygonalMap2.SearchNode var2 = (PolygonalMap2.SearchNode)this.nodeMap.get(var1.ID);
            if (var2 == null) {
               var2 = PolygonalMap2.SearchNode.alloc().init(this, var1);
               this.searchNodes.add(var2);
               this.nodeMap.put(var1.ID, var2);
            }

            return var2;
         }
      }

      PolygonalMap2.SearchNode getSearchNode(PolygonalMap2.Square var1) {
         PolygonalMap2.SearchNode var2 = (PolygonalMap2.SearchNode)this.squareToNode.get(var1.ID);
         if (var2 == null) {
            var2 = PolygonalMap2.SearchNode.alloc().init(this, var1);
            this.searchNodes.add(var2);
            this.squareToNode.put(var1.ID, var2);
         }

         return var2;
      }

      PolygonalMap2.SearchNode getSearchNode(int var1, int var2) {
         PolygonalMap2.SearchNode var3 = PolygonalMap2.SearchNode.alloc().init(this, var1, var2);
         this.searchNodes.add(var3);
         return var3;
      }

      ArrayList shortestPath(Mover var1, PolygonalMap2.SearchNode var2, PolygonalMap2.SearchNode var3) {
         this.mover = var1;
         this.goalNode.init(var3);
         return this.shortestPath(var2, this.goalNode);
      }

      boolean canNotMoveBetween(PolygonalMap2.Square var1, PolygonalMap2.Square var2, boolean var3) {
         assert Math.abs(var1.x - var2.x) <= 1;

         assert Math.abs(var1.y - var2.y) <= 1;

         assert var1.z == var2.z;

         assert var1 != var2;

         boolean var4 = var2.x < var1.x;
         boolean var5 = var2.x > var1.x;
         boolean var6 = var2.y < var1.y;
         boolean var7 = var2.y > var1.y;
         if (var2.isReallySolid()) {
            return true;
         } else if (var2.y < var1.y && var1.has((short)64)) {
            return true;
         } else if (var2.x < var1.x && var1.has((short)8)) {
            return true;
         } else if (var2.y > var1.y && var2.x == var1.x && var2.has((short)64)) {
            return true;
         } else if (var2.x > var1.x && var2.y == var1.y && var2.has((short)8)) {
            return true;
         } else if (var2.x != var1.x && var2.has((short)448)) {
            return true;
         } else if (var2.y != var1.y && var2.has((short)56)) {
            return true;
         } else if (var2.x != var1.x && var1.has((short)448)) {
            return true;
         } else if (var2.y != var1.y && var1.has((short)56)) {
            return true;
         } else if (!var2.has((short)512) && !var2.has((short)504)) {
            return true;
         } else {
            boolean var8 = var6 && var1.has((short)4) && (var1.x != var2.x || var3 || !var1.has((short)16384));
            boolean var9 = var4 && var1.has((short)2) && (var1.y != var2.y || var3 || !var1.has((short)8192));
            boolean var10 = var7 && var2.has((short)4) && (var1.x != var2.x || var3 || !var2.has((short)16384));
            boolean var11 = var5 && var2.has((short)2) && (var1.y != var2.y || var3 || !var2.has((short)8192));
            if (!var8 && !var9 && !var10 && !var11) {
               boolean var12 = var2.x != var1.x && var2.y != var1.y;
               if (var12) {
                  PolygonalMap2.Square var13 = PolygonalMap2.instance.getSquare(var1.x, var2.y, var1.z);
                  PolygonalMap2.Square var14 = PolygonalMap2.instance.getSquare(var2.x, var1.y, var1.z);

                  assert var13 != var1 && var13 != var2;

                  assert var14 != var1 && var14 != var2;

                  if (var2.x == var1.x + 1 && var2.y == var1.y + 1 && var13 != null && var14 != null && var13.has((short)4096) && var14.has((short)2048)) {
                     return true;
                  } else if (var2.x == var1.x - 1 && var2.y == var1.y - 1 && var13 != null && var14 != null && var13.has((short)2048) && var14.has((short)4096)) {
                     return true;
                  } else if (var13 != null && this.canNotMoveBetween(var1, var13, true)) {
                     return true;
                  } else if (var14 != null && this.canNotMoveBetween(var1, var14, true)) {
                     return true;
                  } else if (var13 != null && this.canNotMoveBetween(var2, var13, true)) {
                     return true;
                  } else if (var14 != null && this.canNotMoveBetween(var2, var14, true)) {
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return true;
            }
         }
      }

      // $FF: synthetic method
      VGAStar(Object var1) {
         this();
      }

      final class InitProc implements TIntObjectProcedure {
         public boolean execute(int var1, PolygonalMap2.Node var2) {
            PolygonalMap2.SearchNode var3 = PolygonalMap2.SearchNode.alloc().init(VGAStar.this, (PolygonalMap2.Node)var2);
            var3.square = var2.square;
            VGAStar.this.squareToNode.put(var1, var3);
            VGAStar.this.nodeMap.put(var2.ID, var3);
            VGAStar.this.searchNodes.add(var3);
            return true;
         }
      }
   }

   private static class GoalNode implements IGoalNode {
      PolygonalMap2.SearchNode searchNode;

      private GoalNode() {
      }

      PolygonalMap2.GoalNode init(PolygonalMap2.SearchNode var1) {
         this.searchNode = var1;
         return this;
      }

      public boolean inGoal(ISearchNode var1) {
         if (this.searchNode.tx != -1) {
            PolygonalMap2.SearchNode var2 = (PolygonalMap2.SearchNode)var1;
            int var3 = (int)var2.getX();
            int var4 = (int)var2.getY();
            if (var3 % 10 == 0 && PolygonalMap2.instance.getChunkFromSquarePos(var3 - 1, var4) == null) {
               return true;
            } else if (var3 % 10 == 9 && PolygonalMap2.instance.getChunkFromSquarePos(var3 + 1, var4) == null) {
               return true;
            } else if (var4 % 10 == 0 && PolygonalMap2.instance.getChunkFromSquarePos(var3, var4 - 1) == null) {
               return true;
            } else {
               return var4 % 10 == 9 && PolygonalMap2.instance.getChunkFromSquarePos(var3, var4 + 1) == null;
            }
         } else {
            return var1 == this.searchNode;
         }
      }

      // $FF: synthetic method
      GoalNode(Object var1) {
         this();
      }
   }

   private static class SearchNode extends ASearchNode {
      PolygonalMap2.VGAStar astar;
      PolygonalMap2.Node vgNode;
      PolygonalMap2.Square square;
      int tx;
      int ty;
      PolygonalMap2.SearchNode parent;
      static int nextID = 1;
      Integer ID;
      private static final double SQRT2 = Math.sqrt(2.0D);
      static ArrayDeque pool = new ArrayDeque();

      SearchNode() {
         this.ID = nextID++;
      }

      PolygonalMap2.SearchNode init(PolygonalMap2.VGAStar var1, PolygonalMap2.Node var2) {
         this.setG(0.0D);
         this.astar = var1;
         this.vgNode = var2;
         this.square = null;
         this.tx = this.ty = -1;
         this.parent = null;
         return this;
      }

      PolygonalMap2.SearchNode init(PolygonalMap2.VGAStar var1, PolygonalMap2.Square var2) {
         this.setG(0.0D);
         this.astar = var1;
         this.vgNode = null;
         this.square = var2;
         this.tx = this.ty = -1;
         this.parent = null;
         return this;
      }

      PolygonalMap2.SearchNode init(PolygonalMap2.VGAStar var1, int var2, int var3) {
         this.setG(0.0D);
         this.astar = var1;
         this.vgNode = null;
         this.square = null;
         this.tx = var2;
         this.ty = var3;
         this.parent = null;
         return this;
      }

      public double h() {
         return this.dist(this.astar.goalNode.searchNode);
      }

      public double c(ISearchNode var1) {
         PolygonalMap2.SearchNode var2 = (PolygonalMap2.SearchNode)var1;
         double var3 = 0.0D;
         boolean var5 = !(this.astar.mover instanceof IsoZombie) || ((IsoZombie)this.astar.mover).bCrawling;
         if (var5 && this.square != null && var2.square != null) {
            if (this.square.x == var2.square.x - 1 && this.square.y == var2.square.y) {
               if (var2.square.has((short)2048)) {
                  var3 = 200.0D;
               }
            } else if (this.square.x == var2.square.x + 1 && this.square.y == var2.square.y) {
               if (this.square.has((short)2048)) {
                  var3 = 200.0D;
               }
            } else if (this.square.y == var2.square.y - 1 && this.square.x == var2.square.x) {
               if (var2.square.has((short)4096)) {
                  var3 = 200.0D;
               }
            } else if (this.square.y == var2.square.y + 1 && this.square.x == var2.square.x && this.square.has((short)4096)) {
               var3 = 200.0D;
            }
         }

         return this.dist(var2) + var3;
      }

      public void getSuccessors(ArrayList var1) {
         ArrayList var2 = var1;
         int var3;
         if (this.vgNode != null) {
            if (this.vgNode.graphs != null) {
               for(var3 = 0; var3 < this.vgNode.graphs.size(); ++var3) {
                  PolygonalMap2.VisibilityGraph var4 = (PolygonalMap2.VisibilityGraph)this.vgNode.graphs.get(var3);
                  if (!var4.created) {
                     var4.create();
                  }
               }
            }

            for(var3 = 0; var3 < this.vgNode.visible.size(); ++var3) {
               PolygonalMap2.Node var8 = (PolygonalMap2.Node)this.vgNode.visible.get(var3);
               PolygonalMap2.SearchNode var5 = this.astar.getSearchNode(var8);
               var2.add(var5);
            }
         }

         if (this.square != null) {
            for(var3 = -1; var3 <= 1; ++var3) {
               for(int var9 = -1; var9 <= 1; ++var9) {
                  if (var9 != 0 || var3 != 0) {
                     PolygonalMap2.Square var12 = PolygonalMap2.instance.getSquare(this.square.x + var9, this.square.y + var3, this.square.z);
                     if (var12 != null && !this.astar.isSquareInCluster(var12) && !this.astar.canNotMoveBetween(this.square, var12, false)) {
                        PolygonalMap2.SearchNode var6 = this.astar.getSearchNode(var12);
                        if (var2.contains(var6)) {
                           boolean var7 = false;
                        } else {
                           var2.add(var6);
                        }
                     }
                  }
               }
            }

            PolygonalMap2.Square var10;
            PolygonalMap2.SearchNode var11;
            boolean var13;
            if (this.square.z > 0) {
               var10 = PolygonalMap2.instance.getSquare(this.square.x, this.square.y + 1, this.square.z - 1);
               if (var10 != null && var10.has((short)64) && !this.astar.isSquareInCluster(var10)) {
                  var11 = this.astar.getSearchNode(var10);
                  if (var2.contains(var11)) {
                     var13 = false;
                  } else {
                     var2.add(var11);
                  }
               }

               var10 = PolygonalMap2.instance.getSquare(this.square.x + 1, this.square.y, this.square.z - 1);
               if (var10 != null && var10.has((short)8) && !this.astar.isSquareInCluster(var10)) {
                  var11 = this.astar.getSearchNode(var10);
                  if (var2.contains(var11)) {
                     var13 = false;
                  } else {
                     var2.add(var11);
                  }
               }
            }

            if (this.square.z < 8 && this.square.has((short)64)) {
               var10 = PolygonalMap2.instance.getSquare(this.square.x, this.square.y - 1, this.square.z + 1);
               if (var10 != null && !this.astar.isSquareInCluster(var10)) {
                  var11 = this.astar.getSearchNode(var10);
                  if (var2.contains(var11)) {
                     var13 = false;
                  } else {
                     var2.add(var11);
                  }
               }
            }

            if (this.square.z < 8 && this.square.has((short)8)) {
               var10 = PolygonalMap2.instance.getSquare(this.square.x - 1, this.square.y, this.square.z + 1);
               if (var10 != null && !this.astar.isSquareInCluster(var10)) {
                  var11 = this.astar.getSearchNode(var10);
                  if (var2.contains(var11)) {
                     var13 = false;
                  } else {
                     var2.add(var11);
                  }
               }
            }
         }

      }

      public ISearchNode getParent() {
         return this.parent;
      }

      public void setParent(ISearchNode var1) {
         this.parent = (PolygonalMap2.SearchNode)var1;
      }

      public Integer keyCode() {
         return this.ID;
      }

      public float getX() {
         if (this.square != null) {
            return (float)this.square.x + 0.5F;
         } else {
            return this.vgNode != null ? this.vgNode.x : (float)this.tx;
         }
      }

      public float getY() {
         if (this.square != null) {
            return (float)this.square.y + 0.5F;
         } else {
            return this.vgNode != null ? this.vgNode.y : (float)this.ty;
         }
      }

      public float getZ() {
         if (this.square != null) {
            return (float)this.square.z;
         } else {
            return this.vgNode != null ? (float)this.vgNode.z : 0.0F;
         }
      }

      public double dist(PolygonalMap2.SearchNode var1) {
         if (this.square != null && var1.square != null && Math.abs(this.square.x - var1.square.x) <= 1 && Math.abs(this.square.y - var1.square.y) <= 1) {
            return this.square.x != var1.square.x && this.square.y != var1.square.y ? SQRT2 : 1.0D;
         } else {
            float var2 = this.getX();
            float var3 = this.getY();
            float var4 = var1.getX();
            float var5 = var1.getY();
            return Math.sqrt(Math.pow((double)(var2 - var4), 2.0D) + Math.pow((double)(var3 - var5), 2.0D));
         }
      }

      float getApparentZ() {
         if (this.square == null) {
            return (float)this.vgNode.z;
         } else if (!this.square.has((short)8) && !this.square.has((short)64)) {
            if (!this.square.has((short)16) && !this.square.has((short)128)) {
               return !this.square.has((short)32) && !this.square.has((short)256) ? (float)this.square.z : (float)this.square.z + 0.25F;
            } else {
               return (float)this.square.z + 0.5F;
            }
         } else {
            return (float)this.square.z + 0.75F;
         }
      }

      static PolygonalMap2.SearchNode alloc() {
         return pool.isEmpty() ? new PolygonalMap2.SearchNode() : (PolygonalMap2.SearchNode)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class TestRequest implements PolygonalMap2.IPathfinder {
      PolygonalMap2.Path path;
      boolean done;

      private TestRequest() {
         this.path = new PolygonalMap2.Path();
      }

      public void Succeeded(PolygonalMap2.Path var1, Mover var2) {
         this.path.copyFrom(var1);
         this.done = true;
      }

      public void Failed(Mover var1) {
         this.path.clear();
         this.done = true;
      }

      // $FF: synthetic method
      TestRequest(Object var1) {
         this();
      }
   }

   private static class AdjustStartEndNodeData {
      PolygonalMap2.Obstacle obstacle;
      PolygonalMap2.Node node;
      PolygonalMap2.Edge newEdge;
      boolean isNodeNew;
      PolygonalMap2.VisibilityGraph graph;

      private AdjustStartEndNodeData() {
      }

      // $FF: synthetic method
      AdjustStartEndNodeData(Object var1) {
         this();
      }
   }

   public static class Path {
      ArrayList nodes = new ArrayList();
      ArrayDeque nodePool = new ArrayDeque();

      void clear() {
         for(int var1 = 0; var1 < this.nodes.size(); ++var1) {
            this.nodePool.push(this.nodes.get(var1));
         }

         this.nodes.clear();
      }

      boolean isEmpty() {
         return this.nodes.isEmpty();
      }

      PolygonalMap2.PathNode addNode(float var1, float var2, float var3) {
         PolygonalMap2.PathNode var4 = this.nodePool.isEmpty() ? new PolygonalMap2.PathNode() : (PolygonalMap2.PathNode)this.nodePool.pop();
         var4.init(var1, var2, var3);
         this.nodes.add(var4);
         return var4;
      }

      void copyFrom(PolygonalMap2.Path var1) {
         assert this != var1;

         this.clear();

         for(int var2 = 0; var2 < var1.nodes.size(); ++var2) {
            PolygonalMap2.PathNode var3 = (PolygonalMap2.PathNode)var1.nodes.get(var2);
            this.addNode(var3.x, var3.y, var3.z);
         }

      }
   }

   static class PathNode {
      float x;
      float y;
      float z;

      PolygonalMap2.PathNode init(float var1, float var2, float var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
         return this;
      }

      PolygonalMap2.PathNode init(PolygonalMap2.PathNode var1) {
         this.x = var1.x;
         this.y = var1.y;
         this.z = var1.z;
         return this;
      }
   }

   private static class VisibilityGraph {
      boolean created;
      PolygonalMap2.VehicleCluster cluster;
      ArrayList nodes = new ArrayList();
      ArrayList edges = new ArrayList();
      ArrayList obstacles = new ArrayList();
      ArrayList intersectNodes = new ArrayList();
      ArrayList perimeterNodes = new ArrayList();
      ArrayList perimeterEdges = new ArrayList();
      ArrayList obstacleTraceNodes = new ArrayList();
      static PolygonalMap2.VisibilityGraph.CompareIntersection comparator = new PolygonalMap2.VisibilityGraph.CompareIntersection();
      private static final PolygonalMap2.ClusterOutlineGrid clusterOutlineGrid = new PolygonalMap2.ClusterOutlineGrid();
      Vector2 tempVector2 = new Vector2();
      private static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.VisibilityGraph init(PolygonalMap2.VehicleCluster var1) {
         this.created = false;
         this.cluster = var1;
         this.edges.clear();
         this.nodes.clear();
         this.obstacles.clear();
         this.intersectNodes.clear();
         this.perimeterEdges.clear();
         this.perimeterNodes.clear();
         return this;
      }

      void addEdgesForVehicle(PolygonalMap2.Vehicle var1) {
         PolygonalMap2.VehiclePoly var2 = var1.polyPlusRadius;
         int var3 = (int)var2.z;
         PolygonalMap2.Node var4 = PolygonalMap2.Node.alloc().init(var2.x1, var2.y1, var3);
         PolygonalMap2.Node var5 = PolygonalMap2.Node.alloc().init(var2.x2, var2.y2, var3);
         PolygonalMap2.Node var6 = PolygonalMap2.Node.alloc().init(var2.x3, var2.y3, var3);
         PolygonalMap2.Node var7 = PolygonalMap2.Node.alloc().init(var2.x4, var2.y4, var3);
         PolygonalMap2.Obstacle var8 = PolygonalMap2.Obstacle.alloc().init(var1);
         this.obstacles.add(var8);
         PolygonalMap2.Edge var9 = PolygonalMap2.Edge.alloc().init(var4, var5, var8);
         PolygonalMap2.Edge var10 = PolygonalMap2.Edge.alloc().init(var5, var6, var8);
         PolygonalMap2.Edge var11 = PolygonalMap2.Edge.alloc().init(var6, var7, var8);
         PolygonalMap2.Edge var12 = PolygonalMap2.Edge.alloc().init(var7, var4, var8);
         var8.edges.add(var9);
         var8.edges.add(var10);
         var8.edges.add(var11);
         var8.edges.add(var12);
         var8.calcBounds();
         this.nodes.add(var4);
         this.nodes.add(var5);
         this.nodes.add(var6);
         this.nodes.add(var7);
         this.edges.add(var9);
         this.edges.add(var10);
         this.edges.add(var11);
         this.edges.add(var12);
      }

      boolean isVisible(PolygonalMap2.Node var1, PolygonalMap2.Node var2) {
         if (var1.sharesEdge(var2)) {
            return !var1.onSameShapeButDoesNotShareAnEdge(var2);
         } else if (var1.sharesShape(var2)) {
            return false;
         } else {
            int var3;
            PolygonalMap2.Edge var4;
            for(var3 = 0; var3 < this.edges.size(); ++var3) {
               var4 = (PolygonalMap2.Edge)this.edges.get(var3);
               if (this.intersects(var1, var2, var4)) {
                  return false;
               }
            }

            for(var3 = 0; var3 < this.perimeterEdges.size(); ++var3) {
               var4 = (PolygonalMap2.Edge)this.perimeterEdges.get(var3);
               if (this.intersects(var1, var2, var4)) {
                  return false;
               }
            }

            return true;
         }
      }

      boolean intersects(PolygonalMap2.Node var1, PolygonalMap2.Node var2, PolygonalMap2.Edge var3) {
         return !var3.hasNode(var1) && !var3.hasNode(var2) ? Line2D.linesIntersect((double)var1.x, (double)var1.y, (double)var2.x, (double)var2.y, (double)var3.node1.x, (double)var3.node1.y, (double)var3.node2.x, (double)var3.node2.y) : false;
      }

      public PolygonalMap2.Intersection getIntersection(PolygonalMap2.Edge var1, PolygonalMap2.Edge var2) {
         float var3 = var1.node1.x;
         float var4 = var1.node1.y;
         float var5 = var1.node2.x;
         float var6 = var1.node2.y;
         float var7 = var2.node1.x;
         float var8 = var2.node1.y;
         float var9 = var2.node2.x;
         float var10 = var2.node2.y;
         double var11 = (double)((var10 - var8) * (var5 - var3) - (var9 - var7) * (var6 - var4));
         if (var11 == 0.0D) {
            return null;
         } else {
            double var13 = (double)((var9 - var7) * (var4 - var8) - (var10 - var8) * (var3 - var7)) / var11;
            double var15 = (double)((var5 - var3) * (var4 - var8) - (var6 - var4) * (var3 - var7)) / var11;
            if (var13 >= 0.0D && var13 <= 1.0D && var15 >= 0.0D && var15 <= 1.0D) {
               float var17 = (float)((double)var3 + var13 * (double)(var5 - var3));
               float var18 = (float)((double)var4 + var13 * (double)(var6 - var4));
               return new PolygonalMap2.Intersection(var1, var2, (float)var13, (float)var15, var17, var18);
            } else {
               return null;
            }
         }
      }

      void addWorldObstacles() {
         PolygonalMap2.VehicleRect var1 = this.cluster.bounds();
         --var1.x;
         --var1.y;
         var1.w += 3;
         var1.h += 3;
         PolygonalMap2.ObjectOutline[][] var2 = new PolygonalMap2.ObjectOutline[var1.w][var1.h];
         int var3 = this.cluster.z;

         int var4;
         int var5;
         for(var4 = var1.top(); var4 < var1.bottom() - 1; ++var4) {
            for(var5 = var1.left(); var5 < var1.right() - 1; ++var5) {
               PolygonalMap2.Square var6 = PolygonalMap2.instance.getSquare(var5, var4, var3);
               if (var6 != null && this.contains(var6, 1)) {
                  if (var6.has((short)504) || var6.isReallySolid()) {
                     PolygonalMap2.ObjectOutline.setSolid(var5 - var1.left(), var4 - var1.top(), var3, var2);
                  }

                  if (var6.has((short)2)) {
                     PolygonalMap2.ObjectOutline.setWest(var5 - var1.left(), var4 - var1.top(), var3, var2);
                  }

                  if (var6.has((short)4)) {
                     PolygonalMap2.ObjectOutline.setNorth(var5 - var1.left(), var4 - var1.top(), var3, var2);
                  }
               }
            }
         }

         for(var4 = 0; var4 < var1.h; ++var4) {
            for(var5 = 0; var5 < var1.w; ++var5) {
               PolygonalMap2.ObjectOutline var12 = PolygonalMap2.ObjectOutline.get(var5, var4, var3, var2);
               if (var12 != null && var12.nw && var12.nw_w && var12.nw_n) {
                  var12.trace(var2, this.obstacleTraceNodes);
                  if (!var12.nodes.isEmpty()) {
                     PolygonalMap2.Obstacle var7 = PolygonalMap2.Obstacle.alloc().init((IsoGridSquare)null);

                     for(int var8 = 0; var8 < var12.nodes.size() - 1; ++var8) {
                        PolygonalMap2.Node var9 = (PolygonalMap2.Node)var12.nodes.get(var8);
                        PolygonalMap2.Node var10 = (PolygonalMap2.Node)var12.nodes.get(var8 + 1);
                        var9.x += (float)var1.left();
                        var9.y += (float)var1.top();
                        if (!this.contains(var9.x, var9.y, var9.z)) {
                           var9.ignore = true;
                        }

                        PolygonalMap2.Edge var11 = PolygonalMap2.Edge.alloc().init(var9, var10, var7);
                        var7.edges.add(var11);
                        this.nodes.add(var9);
                     }

                     var7.calcBounds();
                     this.obstacles.add(var7);
                     this.edges.addAll(var7.edges);
                  }
               }
            }
         }

         for(var4 = 0; var4 < var1.h; ++var4) {
            for(var5 = 0; var5 < var1.w; ++var5) {
               if (var2[var5][var4] != null) {
                  var2[var5][var4].release();
               }
            }
         }

         var1.release();
      }

      void trySplit(PolygonalMap2.Edge var1, PolygonalMap2.VehicleRect var2, ArrayList var3) {
         float var4;
         float var5;
         float var6;
         if (Math.abs(var1.node1.x - var1.node2.x) > Math.abs(var1.node1.y - var1.node2.y)) {
            var4 = Math.min(var1.node1.x, var1.node2.x);
            var5 = Math.max(var1.node1.x, var1.node2.x);
            var6 = var1.node1.y;
            if ((float)var2.left() > var4 && (float)var2.left() < var5 && (float)var2.top() < var6 && (float)var2.bottom() > var6 && !var3.contains(var2.left()) && !this.contains((float)var2.left() - 0.5F, var6, this.cluster.z)) {
               var3.add(var2.left());
            }

            if ((float)var2.right() > var4 && (float)var2.right() < var5 && (float)var2.top() < var6 && (float)var2.bottom() > var6 && !var3.contains(var2.right()) && !this.contains((float)var2.right() + 0.5F, var6, this.cluster.z)) {
               var3.add(var2.right());
            }
         } else {
            var4 = Math.min(var1.node1.y, var1.node2.y);
            var5 = Math.max(var1.node1.y, var1.node2.y);
            var6 = var1.node1.x;
            if ((float)var2.top() > var4 && (float)var2.top() < var5 && (float)var2.left() < var6 && (float)var2.right() > var6 && !var3.contains(var2.top()) && !this.contains(var6, (float)var2.top() - 0.5F, this.cluster.z)) {
               var3.add(var2.top());
            }

            if ((float)var2.bottom() > var4 && (float)var2.bottom() < var5 && (float)var2.left() < var6 && (float)var2.right() > var6 && !var3.contains(var2.bottom()) && !this.contains(var6, (float)var2.bottom() + 0.5F, this.cluster.z)) {
               var3.add(var2.bottom());
            }
         }

      }

      void splitWorldObstacleEdges() {
         ArrayList var1 = new ArrayList();

         for(int var2 = 0; var2 < this.obstacles.size(); ++var2) {
            PolygonalMap2.Obstacle var3 = (PolygonalMap2.Obstacle)this.obstacles.get(var2);
            if (var3.vehicle == null) {
               for(int var4 = var3.edges.size() - 1; var4 >= 0; --var4) {
                  PolygonalMap2.Edge var5 = (PolygonalMap2.Edge)var3.edges.get(var4);
                  var1.clear();

                  int var6;
                  for(var6 = 0; var6 < this.cluster.rects.size(); ++var6) {
                     PolygonalMap2.VehicleRect var7 = (PolygonalMap2.VehicleRect)this.cluster.rects.get(var6);
                     this.trySplit(var5, var7, var1);
                  }

                  if (!var1.isEmpty()) {
                     Collections.sort(var1);
                     PolygonalMap2.Edge var8;
                     PolygonalMap2.Node var9;
                     if (Math.abs(var5.node1.x - var5.node2.x) > Math.abs(var5.node1.y - var5.node2.y)) {
                        if (var5.node1.x < var5.node2.x) {
                           for(var6 = var1.size() - 1; var6 >= 0; --var6) {
                              var9 = PolygonalMap2.Node.alloc().init((float)(Integer)var1.get(var6), var5.node1.y, this.cluster.z);
                              var8 = var5.split(var9);
                              this.nodes.add(var9);
                              this.edges.add(var8);
                           }
                        } else {
                           for(var6 = 0; var6 < var1.size(); ++var6) {
                              var9 = PolygonalMap2.Node.alloc().init((float)(Integer)var1.get(var6), var5.node1.y, this.cluster.z);
                              var8 = var5.split(var9);
                              this.nodes.add(var9);
                              this.edges.add(var8);
                           }
                        }
                     } else if (var5.node1.y < var5.node2.y) {
                        for(var6 = var1.size() - 1; var6 >= 0; --var6) {
                           var9 = PolygonalMap2.Node.alloc().init(var5.node1.x, (float)(Integer)var1.get(var6), this.cluster.z);
                           var8 = var5.split(var9);
                           this.nodes.add(var9);
                           this.edges.add(var8);
                        }
                     } else {
                        for(var6 = 0; var6 < var1.size(); ++var6) {
                           var9 = PolygonalMap2.Node.alloc().init(var5.node1.x, (float)(Integer)var1.get(var6), this.cluster.z);
                           var8 = var5.split(var9);
                           this.nodes.add(var9);
                           this.edges.add(var8);
                        }
                     }
                  }
               }
            }
         }

      }

      void getStairSquares(ArrayList var1) {
         PolygonalMap2.VehicleRect var2 = this.cluster.bounds();
         var2.x -= 4;
         var2.w += 4;
         ++var2.w;
         var2.y -= 4;
         var2.h += 4;
         ++var2.h;

         for(int var3 = var2.top(); var3 < var2.bottom(); ++var3) {
            for(int var4 = var2.left(); var4 < var2.right(); ++var4) {
               PolygonalMap2.Square var5 = PolygonalMap2.instance.getSquare(var4, var3, this.cluster.z);
               if (var5 != null && var5.has((short)72) && !var1.contains(var5)) {
                  var1.add(var5);
               }
            }
         }

         var2.release();
      }

      void getWindowSquares(ArrayList var1) {
         PolygonalMap2.VehicleRect var2 = this.cluster.bounds();
         --var2.x;
         var2.w += 2;
         --var2.y;
         var2.h += 2;

         for(int var3 = var2.top(); var3 < var2.bottom(); ++var3) {
            for(int var4 = var2.left(); var4 < var2.right(); ++var4) {
               PolygonalMap2.Square var5 = PolygonalMap2.instance.getSquare(var4, var3, this.cluster.z);
               if (var5 != null && var5.has((short)24576) && !var1.contains(var5)) {
                  var1.add(var5);
               }
            }
         }

         var2.release();
      }

      void checkEdgeIntersection() {
         int var1;
         PolygonalMap2.Obstacle var2;
         int var3;
         int var5;
         for(var1 = 0; var1 < this.obstacles.size(); ++var1) {
            var2 = (PolygonalMap2.Obstacle)this.obstacles.get(var1);

            for(var3 = var1 + 1; var3 < this.obstacles.size(); ++var3) {
               PolygonalMap2.Obstacle var4 = (PolygonalMap2.Obstacle)this.obstacles.get(var3);
               if (var2.bounds.intersects(var4.bounds)) {
                  for(var5 = 0; var5 < var2.edges.size(); ++var5) {
                     PolygonalMap2.Edge var6 = (PolygonalMap2.Edge)var2.edges.get(var5);

                     for(int var7 = 0; var7 < var4.edges.size(); ++var7) {
                        PolygonalMap2.Edge var8 = (PolygonalMap2.Edge)var4.edges.get(var7);
                        if (this.intersects(var6.node1, var6.node2, var8)) {
                           PolygonalMap2.Intersection var9 = this.getIntersection(var6, var8);
                           if (var9 != null) {
                              var6.intersections.add(var9);
                              var8.intersections.add(var9);
                              this.nodes.add(var9.nodeSplit);
                              this.intersectNodes.add(var9.nodeSplit);
                           }
                        }
                     }
                  }
               }
            }
         }

         for(var1 = 0; var1 < this.obstacles.size(); ++var1) {
            var2 = (PolygonalMap2.Obstacle)this.obstacles.get(var1);

            for(var3 = var2.edges.size() - 1; var3 >= 0; --var3) {
               PolygonalMap2.Edge var10 = (PolygonalMap2.Edge)var2.edges.get(var3);
               if (!var10.intersections.isEmpty()) {
                  comparator.edge = var10;
                  Collections.sort(var10.intersections, comparator);

                  for(var5 = var10.intersections.size() - 1; var5 >= 0; --var5) {
                     PolygonalMap2.Intersection var11 = (PolygonalMap2.Intersection)var10.intersections.get(var5);
                     PolygonalMap2.Edge var12 = var11.split(var10);
                     this.edges.add(var12);
                  }
               }
            }
         }

      }

      void checkNodesInObstacles() {
         for(int var1 = 0; var1 < this.nodes.size(); ++var1) {
            PolygonalMap2.Node var2 = (PolygonalMap2.Node)this.nodes.get(var1);
            if (!this.intersectNodes.contains(var2)) {
               for(int var3 = 0; var3 < this.obstacles.size(); ++var3) {
                  PolygonalMap2.Obstacle var4 = (PolygonalMap2.Obstacle)this.obstacles.get(var3);
                  if (var4.isNodeInsideOf(var2)) {
                     var2.ignore = true;
                     break;
                  }
               }
            }
         }

      }

      void addPerimeterEdges() {
         PolygonalMap2.VehicleRect var1 = this.cluster.bounds();
         --var1.x;
         --var1.y;
         var1.w += 2;
         var1.h += 2;
         PolygonalMap2.ClusterOutlineGrid var2 = clusterOutlineGrid.setSize(var1.w, var1.h);
         int var3 = this.cluster.z;

         int var4;
         for(var4 = 0; var4 < this.cluster.rects.size(); ++var4) {
            PolygonalMap2.VehicleRect var5 = (PolygonalMap2.VehicleRect)this.cluster.rects.get(var4);
            var5 = PolygonalMap2.VehicleRect.alloc().init(var5.x - 1, var5.y - 1, var5.w + 2, var5.h + 2, var5.z);

            for(int var6 = var5.top(); var6 < var5.bottom(); ++var6) {
               for(int var7 = var5.left(); var7 < var5.right(); ++var7) {
                  var2.setInner(var7 - var1.left(), var6 - var1.top(), var3);
               }
            }

            var5.release();
         }

         int var12;
         PolygonalMap2.ClusterOutline var13;
         for(var4 = 0; var4 < var1.h; ++var4) {
            for(var12 = 0; var12 < var1.w; ++var12) {
               var13 = var2.get(var12, var4, var3);
               if (var13.inner) {
                  if (!var2.isInner(var12 - 1, var4, var3)) {
                     var13.w = true;
                  }

                  if (!var2.isInner(var12, var4 - 1, var3)) {
                     var13.n = true;
                  }

                  if (!var2.isInner(var12 + 1, var4, var3)) {
                     var13.e = true;
                  }

                  if (!var2.isInner(var12, var4 + 1, var3)) {
                     var13.s = true;
                  }
               }
            }
         }

         for(var4 = 0; var4 < var1.h; ++var4) {
            for(var12 = 0; var12 < var1.w; ++var12) {
               var13 = var2.get(var12, var4, var3);
               if (var13 != null && (var13.w || var13.n || var13.e || var13.s || var13.innerCorner)) {
                  PolygonalMap2.Square var14 = PolygonalMap2.instance.getSquare(var1.x + var12, var1.y + var4, var3);
                  if (var14 != null && !var14.isReallySolid() && !var14.has((short)504)) {
                     PolygonalMap2.Node var8 = PolygonalMap2.instance.getNodeForSquare(var14);
                     var8.addGraph(this);
                     this.perimeterNodes.add(var8);
                  }
               }

               if (var13 != null && var13.n && var13.w && var13.inner) {
                  ArrayList var15 = var2.trace(var13);
                  if (!var15.isEmpty()) {
                     for(int var16 = 0; var16 < var15.size() - 1; ++var16) {
                        PolygonalMap2.Node var9 = (PolygonalMap2.Node)var15.get(var16);
                        PolygonalMap2.Node var10 = (PolygonalMap2.Node)var15.get(var16 + 1);
                        var9.x += (float)var1.left();
                        var9.y += (float)var1.top();
                        PolygonalMap2.Edge var11 = PolygonalMap2.Edge.alloc().init(var9, var10, (PolygonalMap2.Obstacle)null);
                        this.perimeterEdges.add(var11);
                     }

                     if (var15.get(var15.size() - 1) != var15.get(0)) {
                        PolygonalMap2.Node var10000 = (PolygonalMap2.Node)var15.get(var15.size() - 1);
                        var10000.x += (float)var1.left();
                        var10000 = (PolygonalMap2.Node)var15.get(var15.size() - 1);
                        var10000.y += (float)var1.top();
                     }
                  }
               }
            }
         }

         var2.releaseElements();
         var1.release();
      }

      void calculateNodeVisibility() {
         ArrayList var1 = new ArrayList();
         var1.addAll(this.nodes);
         var1.addAll(this.perimeterNodes);

         for(int var2 = 0; var2 < var1.size(); ++var2) {
            PolygonalMap2.Node var3 = (PolygonalMap2.Node)var1.get(var2);
            if (!var3.ignore && (var3.square == null || !var3.square.has((short)504))) {
               for(int var4 = var2 + 1; var4 < var1.size(); ++var4) {
                  PolygonalMap2.Node var5 = (PolygonalMap2.Node)var1.get(var4);
                  if (!var5.ignore && (var5.square == null || !var5.square.has((short)504)) && (!this.perimeterNodes.contains(var3) || !this.perimeterNodes.contains(var5))) {
                     if (var3.visible.contains(var5)) {
                        assert var3.square != null && var3.square.has((short)24576) || var5.square != null && var5.square.has((short)24576);
                     } else if (this.isVisible(var3, var5)) {
                        var3.visible.add(var5);
                        var5.visible.add(var3);
                     }
                  }
               }
            }
         }

      }

      void addNode(PolygonalMap2.Node var1) {
         if (this.created && !var1.ignore) {
            ArrayList var2 = new ArrayList();
            var2.addAll(this.nodes);
            var2.addAll(this.perimeterNodes);

            for(int var3 = 0; var3 < var2.size(); ++var3) {
               PolygonalMap2.Node var4 = (PolygonalMap2.Node)var2.get(var3);
               if (!var4.ignore && this.isVisible(var4, var1)) {
                  var4.visible.add(var1);
                  var1.visible.add(var4);
               }
            }
         }

         this.nodes.add(var1);
      }

      void removeNode(PolygonalMap2.Node var1) {
         this.nodes.remove(var1);

         for(int var2 = 0; var2 < var1.visible.size(); ++var2) {
            PolygonalMap2.Node var3 = (PolygonalMap2.Node)var1.visible.get(var2);
            var3.visible.remove(var1);
         }

      }

      boolean contains(float var1, float var2, int var3) {
         for(int var4 = 0; var4 < this.cluster.rects.size(); ++var4) {
            PolygonalMap2.VehicleRect var5 = (PolygonalMap2.VehicleRect)this.cluster.rects.get(var4);
            if (var5.containsPoint(var1, var2, (float)var3)) {
               return true;
            }
         }

         return false;
      }

      boolean contains(PolygonalMap2.Square var1) {
         for(int var2 = 0; var2 < this.cluster.rects.size(); ++var2) {
            PolygonalMap2.VehicleRect var3 = (PolygonalMap2.VehicleRect)this.cluster.rects.get(var2);
            if (var3.containsPoint((float)var1.x + 0.5F, (float)var1.y + 0.5F, (float)var1.z)) {
               return true;
            }
         }

         return false;
      }

      boolean contains(PolygonalMap2.Square var1, int var2) {
         for(int var3 = 0; var3 < this.cluster.rects.size(); ++var3) {
            PolygonalMap2.VehicleRect var4 = (PolygonalMap2.VehicleRect)this.cluster.rects.get(var3);
            if (var4.containsPoint((float)var1.x + 0.5F, (float)var1.y + 0.5F, (float)var1.z, var2)) {
               return true;
            }
         }

         return false;
      }

      private int getPointOutsideObstacles(float var1, float var2, float var3, PolygonalMap2.AdjustStartEndNodeData var4) {
         double var5 = Double.MAX_VALUE;
         PolygonalMap2.Obstacle var7 = null;

         for(int var8 = 0; var8 < this.obstacles.size(); ++var8) {
            PolygonalMap2.Obstacle var9 = (PolygonalMap2.Obstacle)this.obstacles.get(var8);
            if (var9.bounds.containsPoint(var1, var2) && var9.isPointInPolygon_WindingNumber(var1, var2)) {
               var9.getClosestPointOnEdge(var1, var2, this.tempVector2);
               double var10 = (double)IsoUtils.DistanceToSquared(var1, var2, this.tempVector2.x, this.tempVector2.y);
               if (var10 < var5) {
                  var5 = var10;
                  var7 = var9;
               }
            }
         }

         if (var7 != null) {
            if (var7.splitEdgeAtNearestPoint(var1, var2, (int)var3, var4)) {
               var4.graph = this;
               if (var4.isNodeNew) {
                  this.edges.add(var4.newEdge);
                  this.addNode(var4.node);
               }

               return 1;
            } else {
               return -1;
            }
         } else {
            return 0;
         }
      }

      void create() {
         for(int var1 = 0; var1 < this.cluster.rects.size(); ++var1) {
            PolygonalMap2.VehicleRect var2 = (PolygonalMap2.VehicleRect)this.cluster.rects.get(var1);
            this.addEdgesForVehicle(var2.vehicle);
         }

         this.addWorldObstacles();
         this.splitWorldObstacleEdges();
         this.checkEdgeIntersection();
         this.checkNodesInObstacles();
         this.calculateNodeVisibility();
         this.created = true;
      }

      static PolygonalMap2.VisibilityGraph alloc() {
         return pool.isEmpty() ? new PolygonalMap2.VisibilityGraph() : (PolygonalMap2.VisibilityGraph)pool.pop();
      }

      void release() {
         int var1;
         for(var1 = 0; var1 < this.nodes.size(); ++var1) {
            if (!PolygonalMap2.instance.squareToNode.containsValue(this.nodes.get(var1))) {
               ((PolygonalMap2.Node)this.nodes.get(var1)).release();
            }
         }

         for(var1 = 0; var1 < this.perimeterEdges.size(); ++var1) {
            ((PolygonalMap2.Edge)this.perimeterEdges.get(var1)).node1.release();
            ((PolygonalMap2.Edge)this.perimeterEdges.get(var1)).release();
         }

         for(var1 = 0; var1 < this.obstacles.size(); ++var1) {
            PolygonalMap2.Obstacle var2 = (PolygonalMap2.Obstacle)this.obstacles.get(var1);

            for(int var3 = 0; var3 < var2.edges.size(); ++var3) {
               ((PolygonalMap2.Edge)var2.edges.get(var3)).release();
            }

            var2.release();
         }

         for(var1 = 0; var1 < this.cluster.rects.size(); ++var1) {
            ((PolygonalMap2.VehicleRect)this.cluster.rects.get(var1)).release();
         }

         this.cluster.release();

         assert !pool.contains(this);

         pool.push(this);
      }

      void render() {
         float var1 = 1.0F;

         Iterator var2;
         for(var2 = this.perimeterEdges.iterator(); var2.hasNext(); var1 = 1.0F - var1) {
            PolygonalMap2.Edge var3 = (PolygonalMap2.Edge)var2.next();
            LineDrawer.addLine(var3.node1.x, var3.node1.y, (float)this.cluster.z, var3.node2.x, var3.node2.y, (float)this.cluster.z, var1, 0.5F, 0.5F, (String)null, true);
         }

         var2 = this.obstacles.iterator();

         Iterator var4;
         while(var2.hasNext()) {
            PolygonalMap2.Obstacle var6 = (PolygonalMap2.Obstacle)var2.next();
            var1 = 1.0F;

            for(var4 = var6.edges.iterator(); var4.hasNext(); var1 = 1.0F - var1) {
               PolygonalMap2.Edge var5 = (PolygonalMap2.Edge)var4.next();
               LineDrawer.addLine(var5.node1.x, var5.node1.y, (float)this.cluster.z, var5.node2.x, var5.node2.y, (float)this.cluster.z, var1, 0.5F, 0.5F, (String)null, true);
            }
         }

         var2 = this.perimeterNodes.iterator();

         PolygonalMap2.Node var7;
         while(var2.hasNext()) {
            var7 = (PolygonalMap2.Node)var2.next();
            LineDrawer.addLine(var7.x - 0.05F, var7.y - 0.05F, (float)this.cluster.z, var7.x + 0.05F, var7.y + 0.05F, (float)this.cluster.z, 1.0F, 1.0F, 0.0F, (String)null, false);
         }

         var2 = this.nodes.iterator();

         while(var2.hasNext()) {
            var7 = (PolygonalMap2.Node)var2.next();
            var4 = var7.visible.iterator();

            while(var4.hasNext()) {
               PolygonalMap2.Node var8 = (PolygonalMap2.Node)var4.next();
               if (this.nodes.contains(var8)) {
                  LineDrawer.addLine(var7.x, var7.y, (float)this.cluster.z, var8.x, var8.y, (float)this.cluster.z, 0.0F, 1.0F, 0.0F, (String)null, true);
               }
            }

            if (var7.ignore) {
               LineDrawer.addLine(var7.x - 0.05F, var7.y - 0.05F, (float)this.cluster.z, var7.x + 0.05F, var7.y + 0.05F, (float)this.cluster.z, 1.0F, 1.0F, 0.0F, (String)null, false);
            }
         }

         var2 = this.intersectNodes.iterator();

         while(var2.hasNext()) {
            var7 = (PolygonalMap2.Node)var2.next();
            LineDrawer.addLine(var7.x - 0.1F, var7.y - 0.1F, (float)this.cluster.z, var7.x + 0.1F, var7.y + 0.1F, (float)this.cluster.z, 1.0F, 0.0F, 0.0F, (String)null, false);
         }

      }

      static class CompareIntersection implements Comparator {
         PolygonalMap2.Edge edge;

         public int compare(PolygonalMap2.Intersection var1, PolygonalMap2.Intersection var2) {
            float var3 = this.edge == var1.edge1 ? var1.dist1 : var1.dist2;
            float var4 = this.edge == var2.edge1 ? var2.dist1 : var2.dist2;
            if (var3 < var4) {
               return -1;
            } else {
               return var3 > var4 ? 1 : 0;
            }
         }
      }
   }

   private static class VehicleCluster {
      int z;
      ArrayList rects = new ArrayList();
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.VehicleCluster init() {
         this.rects.clear();
         return this;
      }

      void merge(PolygonalMap2.VehicleCluster var1) {
         for(int var2 = 0; var2 < var1.rects.size(); ++var2) {
            PolygonalMap2.VehicleRect var3 = (PolygonalMap2.VehicleRect)var1.rects.get(var2);
            var3.cluster = this;
         }

         this.rects.addAll(var1.rects);
         var1.rects.clear();
      }

      PolygonalMap2.VehicleRect bounds() {
         int var1 = Integer.MAX_VALUE;
         int var2 = Integer.MAX_VALUE;
         int var3 = Integer.MIN_VALUE;
         int var4 = Integer.MIN_VALUE;

         for(int var5 = 0; var5 < this.rects.size(); ++var5) {
            PolygonalMap2.VehicleRect var6 = (PolygonalMap2.VehicleRect)this.rects.get(var5);
            var1 = Math.min(var1, var6.left());
            var2 = Math.min(var2, var6.top());
            var3 = Math.max(var3, var6.right());
            var4 = Math.max(var4, var6.bottom());
         }

         return PolygonalMap2.VehicleRect.alloc().init(var1, var2, var3 - var1, var4 - var2, this.z);
      }

      static PolygonalMap2.VehicleCluster alloc() {
         return pool.isEmpty() ? new PolygonalMap2.VehicleCluster() : (PolygonalMap2.VehicleCluster)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class Vehicle {
      PolygonalMap2.VehiclePoly poly = new PolygonalMap2.VehiclePoly();
      PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
      static ArrayDeque pool = new ArrayDeque();

      static PolygonalMap2.Vehicle alloc() {
         return pool.isEmpty() ? new PolygonalMap2.Vehicle() : (PolygonalMap2.Vehicle)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   public static class VehiclePoly {
      public Transform t = new Transform();
      public float x1;
      public float y1;
      public float x2;
      public float y2;
      public float x3;
      public float y3;
      public float x4;
      public float y4;
      Vector2[] borders = new Vector2[4];
      float z;
      private static final Quaternionf tempQuat = new Quaternionf();

      PolygonalMap2.VehiclePoly init(PolygonalMap2.VehiclePoly var1) {
         this.x1 = var1.x1;
         this.y1 = var1.y1;
         this.x2 = var1.x2;
         this.y2 = var1.y2;
         this.x3 = var1.x3;
         this.y3 = var1.y3;
         this.x4 = var1.x4;
         this.y4 = var1.y4;
         this.z = var1.z;
         return this;
      }

      PolygonalMap2.VehiclePoly init(BaseVehicle var1, float var2) {
         VehicleScript var3 = var1.getScript();
         Vector3f var4 = var3.getExtents();
         Vector2[] var5 = new Vector2[4];
         Quaternionf var6 = tempQuat;
         var1.getWorldTransform(this.t);
         this.t.getRotation(var6);
         float var7 = var4.x + var2 * 2.0F;
         float var8 = var4.z + var2 * 2.0F;
         float var9 = var4.y + var2 * 2.0F;
         if (var6.x < 0.0F) {
            PolygonalMap2.tempVec3f_1.set(-var7 / 2.0F / var3.getModelScale(), 0.0F, var8 / 2.0F / var3.getModelScale());
            var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
            var5[0] = new Vector2(PolygonalMap2.tempVec3f_1.x, PolygonalMap2.tempVec3f_1.y);
            PolygonalMap2.tempVec3f_1.set(var7 / 2.0F / var3.getModelScale(), var9 / 2.0F / var3.getModelScale(), var8 / 2.0F / var3.getModelScale());
            var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
            var5[1] = new Vector2(PolygonalMap2.tempVec3f_1.x, PolygonalMap2.tempVec3f_1.y);
            PolygonalMap2.tempVec3f_1.set(var7 / 2.0F / var3.getModelScale(), var9 / 2.0F / var3.getModelScale(), -var8 / 2.0F / var3.getModelScale());
            var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
            var5[2] = new Vector2(PolygonalMap2.tempVec3f_1.x, PolygonalMap2.tempVec3f_1.y);
            PolygonalMap2.tempVec3f_1.set(-var7 / 2.0F / var3.getModelScale(), 0.0F, -var8 / 2.0F / var3.getModelScale());
            var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
            var5[3] = new Vector2(PolygonalMap2.tempVec3f_1.x, PolygonalMap2.tempVec3f_1.y);
            this.z = var1.z;
         } else {
            PolygonalMap2.tempVec3f_1.set(-var7 / 2.0F / var3.getModelScale(), var9 / 2.0F / var3.getModelScale(), var8 / 2.0F / var3.getModelScale());
            var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
            var5[0] = new Vector2(PolygonalMap2.tempVec3f_1.x, PolygonalMap2.tempVec3f_1.y);
            PolygonalMap2.tempVec3f_1.set(var7 / 2.0F / var3.getModelScale(), 0.0F, var8 / 2.0F / var3.getModelScale());
            var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
            var5[1] = new Vector2(PolygonalMap2.tempVec3f_1.x, PolygonalMap2.tempVec3f_1.y);
            PolygonalMap2.tempVec3f_1.set(var7 / 2.0F / var3.getModelScale(), 0.0F, -var8 / 2.0F / var3.getModelScale());
            var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
            var5[2] = new Vector2(PolygonalMap2.tempVec3f_1.x, PolygonalMap2.tempVec3f_1.y);
            PolygonalMap2.tempVec3f_1.set(-var7 / 2.0F / var3.getModelScale(), var9 / 2.0F / var3.getModelScale(), -var8 / 2.0F / var3.getModelScale());
            var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
            var5[3] = new Vector2(PolygonalMap2.tempVec3f_1.x, PolygonalMap2.tempVec3f_1.y);
            this.z = var1.z;
         }

         if (var2 < 0.1F) {
            if (var6.x < 0.0F) {
               PolygonalMap2.tempVec3f_1.set(-var1.getScript().getShadowOffset().x - var7 / 2.0F / var3.getModelScale(), 0.0F, var1.getScript().getShadowOffset().z + var8 / 2.0F / var3.getModelScale());
               var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
               var1.shadowCoord.x1 = PolygonalMap2.tempVec3f_1.x;
               var1.shadowCoord.y1 = PolygonalMap2.tempVec3f_1.y;
               PolygonalMap2.tempVec3f_1.set(var1.getScript().getShadowOffset().y + var7 / 2.0F / var3.getModelScale(), var9 / 2.0F / var3.getModelScale(), var1.getScript().getShadowOffset().z + var8 / 2.0F / var3.getModelScale());
               var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
               var1.shadowCoord.x2 = PolygonalMap2.tempVec3f_1.x;
               var1.shadowCoord.y2 = PolygonalMap2.tempVec3f_1.y;
               PolygonalMap2.tempVec3f_1.set(var1.getScript().getShadowOffset().y + var7 / 2.0F / var3.getModelScale(), var9 / 2.0F / var3.getModelScale(), -var1.getScript().getShadowOffset().w - var8 / 2.0F / var3.getModelScale());
               var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
               var1.shadowCoord.x3 = PolygonalMap2.tempVec3f_1.x;
               var1.shadowCoord.y3 = PolygonalMap2.tempVec3f_1.y;
               PolygonalMap2.tempVec3f_1.set(-var1.getScript().getShadowOffset().x - var7 / 2.0F / var3.getModelScale(), 0.0F, -var1.getScript().getShadowOffset().w - var8 / 2.0F / var3.getModelScale());
               var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
               var1.shadowCoord.x4 = PolygonalMap2.tempVec3f_1.x;
               var1.shadowCoord.y4 = PolygonalMap2.tempVec3f_1.y;
            } else {
               PolygonalMap2.tempVec3f_1.set(-var1.getScript().getShadowOffset().x - var7 / 2.0F / var3.getModelScale(), var9 / 2.0F / var3.getModelScale(), var1.getScript().getShadowOffset().z + var8 / 2.0F / var3.getModelScale());
               var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
               var1.shadowCoord.x1 = PolygonalMap2.tempVec3f_1.x;
               var1.shadowCoord.y1 = PolygonalMap2.tempVec3f_1.y;
               PolygonalMap2.tempVec3f_1.set(var1.getScript().getShadowOffset().y + var7 / 2.0F / var3.getModelScale(), 0.0F, var1.getScript().getShadowOffset().z + var8 / 2.0F / var3.getModelScale());
               var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
               var1.shadowCoord.x2 = PolygonalMap2.tempVec3f_1.x;
               var1.shadowCoord.y2 = PolygonalMap2.tempVec3f_1.y;
               PolygonalMap2.tempVec3f_1.set(var1.getScript().getShadowOffset().y + var7 / 2.0F / var3.getModelScale(), 0.0F, -var1.getScript().getShadowOffset().w - var8 / 2.0F / var3.getModelScale());
               var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
               var1.shadowCoord.x3 = PolygonalMap2.tempVec3f_1.x;
               var1.shadowCoord.y3 = PolygonalMap2.tempVec3f_1.y;
               PolygonalMap2.tempVec3f_1.set(-var1.getScript().getShadowOffset().x - var7 / 2.0F / var3.getModelScale(), var9 / 2.0F / var3.getModelScale(), -var1.getScript().getShadowOffset().w - var8 / 2.0F / var3.getModelScale());
               var1.getWorldPos(PolygonalMap2.tempVec3f_1, PolygonalMap2.tempVec3f_1);
               var1.shadowCoord.x4 = PolygonalMap2.tempVec3f_1.x;
               var1.shadowCoord.y4 = PolygonalMap2.tempVec3f_1.y;
            }
         }

         if (!var1.hasExtendOffset) {
            this.x1 = var5[0].x;
            this.y1 = var5[0].y;
            this.x2 = var5[1].x;
            this.y2 = var5[1].y;
            this.x3 = var5[2].x;
            this.y3 = var5[2].y;
            this.x4 = var5[3].x;
            this.y4 = var5[3].y;

            for(int var18 = 0; var18 < 4; ++var18) {
               this.borders[var18] = var5[var18];
            }

            return this;
         } else {
            Vector2 var10 = var5[0];
            Vector2 var11 = var5[0];
            Vector2 var12 = var5[0];
            Vector2 var13 = var5[0];

            for(int var14 = 1; var14 < 4; ++var14) {
               if (var5[var14].x + var5[var14].y < var10.x + var10.y) {
                  var10 = var5[var14];
               }

               if (var5[var14].x + var5[var14].y > var12.x + var12.y) {
                  var12 = var5[var14];
               }

               if (var5[var14].x - var5[var14].y < var13.x - var13.y) {
                  var13 = var5[var14];
               }

               if (var5[var14].x - var5[var14].y > var11.x - var11.y) {
                  var11 = var5[var14];
               }
            }

            Vector2f var19 = var3.getExtentsOffset();
            if (var10 != var11 && var11 != var12 && var12 != var13 && var13 != var10) {
               Vector2 var20 = new Vector2(var11);
               Vector2 var16 = new Vector2(var13);
               var10.x -= var19.x;
               var10.y -= var19.y;
               var11.x -= var19.x;
               var11.y -= var19.y;
               var13.x -= var19.x;
               var13.y -= var19.y;
               var11 = lineIntersection(var10, var11, var12, var20);
               var13 = lineIntersection(var10, var13, var12, var16);
               if (var11 != null && var13 != null) {
                  this.x1 = var12.x;
                  this.y1 = var12.y;
                  this.x2 = var11.x;
                  this.y2 = var11.y;
                  this.x3 = var10.x;
                  this.y3 = var10.y;
                  this.x4 = var13.x;
                  this.y4 = var13.y;
                  this.borders[0] = var12;
                  this.borders[1] = var11;
                  this.borders[2] = var10;
                  this.borders[3] = var13;
                  return this;
               } else {
                  this.x1 = var5[0].x;
                  this.y1 = var5[0].y;
                  this.x2 = var5[1].x;
                  this.y2 = var5[1].y;
                  this.x3 = var5[2].x;
                  this.y3 = var5[2].y;
                  this.x4 = var5[3].x;
                  this.y4 = var5[3].y;

                  for(int var17 = 0; var17 < 4; ++var17) {
                     this.borders[var17] = new Vector2(var5[var17]);
                  }

                  return this;
               }
            } else {
               var13 = var5[0];
               if (var10 == var13) {
                  var13 = var5[1];
               }

               int var15;
               for(var15 = 1; var15 < 4; ++var15) {
                  if (var5[var15].x + var5[var15].y < var13.x + var13.y && var5[var15] != var10) {
                     var13 = var5[var15];
                  }
               }

               var10.x -= var19.x;
               var10.y -= var19.y;
               var13.x -= var19.x;
               var13.y -= var19.y;
               this.x1 = var5[0].x;
               this.y1 = var5[0].y;
               this.x2 = var5[1].x;
               this.y2 = var5[1].y;
               this.x3 = var5[2].x;
               this.y3 = var5[2].y;
               this.x4 = var5[3].x;
               this.y4 = var5[3].y;

               for(var15 = 0; var15 < 4; ++var15) {
                  this.borders[var15] = new Vector2(var5[var15]);
               }

               return this;
            }
         }
      }

      public static Vector2 lineIntersection(Vector2 var0, Vector2 var1, Vector2 var2, Vector2 var3) {
         Vector2 var4 = new Vector2();
         float var5 = var0.y - var1.y;
         float var6 = var1.x - var0.x;
         float var7 = -var5 * var0.x - var6 * var0.y;
         float var8 = var2.y - var3.y;
         float var9 = var3.x - var2.x;
         float var10 = -var8 * var2.x - var9 * var2.y;
         float var11 = QuadranglesIntersection.det(var5, var6, var8, var9);
         if (var11 != 0.0F) {
            var4.x = -QuadranglesIntersection.det(var7, var6, var10, var9) * 1.0F / var11;
            var4.y = -QuadranglesIntersection.det(var5, var7, var8, var10) * 1.0F / var11;
            return var4;
         } else {
            return null;
         }
      }

      PolygonalMap2.VehicleRect getAABB(PolygonalMap2.VehicleRect var1) {
         float var2 = Math.min(this.x1, Math.min(this.x2, Math.min(this.x3, this.x4)));
         float var3 = Math.min(this.y1, Math.min(this.y2, Math.min(this.y3, this.y4)));
         float var4 = Math.max(this.x1, Math.max(this.x2, Math.max(this.x3, this.x4)));
         float var5 = Math.max(this.y1, Math.max(this.y2, Math.max(this.y3, this.y4)));
         return var1.init((PolygonalMap2.Vehicle)null, (int)var2, (int)var3, (int)Math.ceil((double)var4) - (int)var2, (int)Math.ceil((double)var5) - (int)var3, (int)this.z);
      }
   }

   private static class VehicleRect {
      PolygonalMap2.VehicleCluster cluster;
      PolygonalMap2.Vehicle vehicle;
      int x;
      int y;
      int w;
      int h;
      int z;
      static ArrayDeque pool = new ArrayDeque();

      private VehicleRect() {
      }

      PolygonalMap2.VehicleRect init(PolygonalMap2.Vehicle var1, int var2, int var3, int var4, int var5, int var6) {
         this.cluster = null;
         this.vehicle = var1;
         this.x = var2;
         this.y = var3;
         this.w = var4;
         this.h = var5;
         this.z = var6;
         return this;
      }

      PolygonalMap2.VehicleRect init(int var1, int var2, int var3, int var4, int var5) {
         this.cluster = null;
         this.vehicle = null;
         this.x = var1;
         this.y = var2;
         this.w = var3;
         this.h = var4;
         this.z = var5;
         return this;
      }

      int left() {
         return this.x;
      }

      int top() {
         return this.y;
      }

      int right() {
         return this.x + this.w;
      }

      int bottom() {
         return this.y + this.h;
      }

      boolean containsPoint(float var1, float var2, float var3) {
         return var1 >= (float)this.left() && var1 < (float)this.right() && var2 >= (float)this.top() && var2 < (float)this.bottom() && (int)var3 == this.z;
      }

      boolean containsPoint(float var1, float var2, float var3, int var4) {
         int var5 = this.x - var4;
         int var6 = this.y - var4;
         int var7 = this.right() + var4;
         int var8 = this.bottom() + var4;
         return var1 >= (float)var5 && var1 < (float)var7 && var2 >= (float)var6 && var2 < (float)var8 && (int)var3 == this.z;
      }

      boolean intersects(PolygonalMap2.VehicleRect var1) {
         return this.left() < var1.right() && this.right() > var1.left() && this.top() < var1.bottom() && this.bottom() > var1.top();
      }

      boolean isAdjacent(PolygonalMap2.VehicleRect var1) {
         --this.x;
         --this.y;
         this.w += 2;
         this.h += 2;
         boolean var2 = this.intersects(var1);
         ++this.x;
         ++this.y;
         this.w -= 2;
         this.h -= 2;
         return var2;
      }

      static PolygonalMap2.VehicleRect alloc() {
         boolean var0;
         if (pool.isEmpty()) {
            var0 = false;
         } else {
            var0 = false;
         }

         return pool.isEmpty() ? new PolygonalMap2.VehicleRect() : (PolygonalMap2.VehicleRect)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }

      // $FF: synthetic method
      VehicleRect(Object var1) {
         this();
      }
   }

   private static class ImmutableRectF {
      private float x;
      private float y;
      private float w;
      private float h;
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.ImmutableRectF init(float var1, float var2, float var3, float var4) {
         this.x = var1;
         this.y = var2;
         this.w = var3;
         this.h = var4;
         return this;
      }

      float left() {
         return this.x;
      }

      float top() {
         return this.y;
      }

      float right() {
         return this.x + this.w;
      }

      float bottom() {
         return this.y + this.h;
      }

      float width() {
         return this.w;
      }

      float height() {
         return this.h;
      }

      boolean containsPoint(float var1, float var2) {
         return var1 >= this.left() && var1 < this.right() && var2 >= this.top() && var2 < this.bottom();
      }

      boolean intersects(PolygonalMap2.ImmutableRectF var1) {
         return this.left() < var1.right() && this.right() > var1.left() && this.top() < var1.bottom() && this.bottom() > var1.top();
      }

      static PolygonalMap2.ImmutableRectF alloc() {
         return pool.isEmpty() ? new PolygonalMap2.ImmutableRectF() : (PolygonalMap2.ImmutableRectF)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class Intersection {
      PolygonalMap2.Edge edge1;
      PolygonalMap2.Edge edge2;
      float dist1;
      float dist2;
      PolygonalMap2.Node nodeSplit;

      Intersection(PolygonalMap2.Edge var1, PolygonalMap2.Edge var2, float var3, float var4, float var5, float var6) {
         this.edge1 = var1;
         this.edge2 = var2;
         this.dist1 = var3;
         this.dist2 = var4;
         this.nodeSplit = PolygonalMap2.Node.alloc().init(var5, var6, var1.node1.z);
      }

      Intersection(PolygonalMap2.Edge var1, PolygonalMap2.Edge var2, float var3, float var4, PolygonalMap2.Node var5) {
         this.edge1 = var1;
         this.edge2 = var2;
         this.dist1 = var3;
         this.dist2 = var4;
         this.nodeSplit = var5;
      }

      PolygonalMap2.Edge split(PolygonalMap2.Edge var1) {
         return var1.split(this.nodeSplit);
      }
   }

   private static final class ClusterOutlineGrid {
      PolygonalMap2.ClusterOutline[] elements;
      int W;
      int H;

      private ClusterOutlineGrid() {
      }

      PolygonalMap2.ClusterOutlineGrid setSize(int var1, int var2) {
         if (this.elements == null || this.elements.length < var1 * var2) {
            this.elements = new PolygonalMap2.ClusterOutline[var1 * var2];
         }

         this.W = var1;
         this.H = var2;
         return this;
      }

      void releaseElements() {
         for(int var1 = 0; var1 < this.H; ++var1) {
            for(int var2 = 0; var2 < this.W; ++var2) {
               if (this.elements[var2 + var1 * this.W] != null) {
                  this.elements[var2 + var1 * this.W].release();
                  this.elements[var2 + var1 * this.W] = null;
               }
            }
         }

      }

      void setInner(int var1, int var2, int var3) {
         PolygonalMap2.ClusterOutline var4 = this.get(var1, var2, var3);
         if (var4 != null) {
            var4.inner = true;
         }

      }

      void setWest(int var1, int var2, int var3) {
         PolygonalMap2.ClusterOutline var4 = this.get(var1, var2, var3);
         if (var4 != null) {
            var4.w = true;
         }

      }

      void setNorth(int var1, int var2, int var3) {
         PolygonalMap2.ClusterOutline var4 = this.get(var1, var2, var3);
         if (var4 != null) {
            var4.n = true;
         }

      }

      void setEast(int var1, int var2, int var3) {
         PolygonalMap2.ClusterOutline var4 = this.get(var1, var2, var3);
         if (var4 != null) {
            var4.e = true;
         }

      }

      void setSouth(int var1, int var2, int var3) {
         PolygonalMap2.ClusterOutline var4 = this.get(var1, var2, var3);
         if (var4 != null) {
            var4.s = true;
         }

      }

      boolean isInner(int var1, int var2, int var3) {
         PolygonalMap2.ClusterOutline var4 = this.get(var1, var2, var3);
         return var4 == null ? false : var4.start || var4.inner;
      }

      PolygonalMap2.ClusterOutline get(int var1, int var2, int var3) {
         if (var1 >= 0 && var1 < this.W) {
            if (var2 >= 0 && var2 < this.H) {
               if (this.elements[var1 + var2 * this.W] == null) {
                  this.elements[var1 + var2 * this.W] = PolygonalMap2.ClusterOutline.alloc().init(var1, var2, var3);
               }

               return this.elements[var1 + var2 * this.W];
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      void trace_W(PolygonalMap2.ClusterOutline var1, ArrayList var2, PolygonalMap2.Node var3) {
         int var4 = var1.x;
         int var5 = var1.y;
         int var6 = var1.z;
         if (var3 != null) {
            var3.setXY((float)var4, (float)var5);
         } else {
            PolygonalMap2.Node var7 = PolygonalMap2.Node.alloc().init((float)var4, (float)var5, var6);
            var2.add(var7);
         }

         var1.inner = false;
         PolygonalMap2.ClusterOutline var8 = this.get(var4, var5 - 1, var6);
         if (var8 == null || !var8.start) {
            if (this.isInner(var4 - 1, var5 - 1, var6)) {
               this.get(var4, var5 - 1, var6).innerCorner = true;
               this.trace_S(this.get(var4 - 1, var5 - 1, var6), var2, (PolygonalMap2.Node)null);
            } else if (this.isInner(var4, var5 - 1, var6)) {
               this.trace_W(this.get(var4, var5 - 1, var6), var2, (PolygonalMap2.Node)var2.get(var2.size() - 1));
            } else if (this.isInner(var4 + 1, var5, var6)) {
               this.trace_N(var1, var2, (PolygonalMap2.Node)null);
            }

         }
      }

      void trace_N(PolygonalMap2.ClusterOutline var1, ArrayList var2, PolygonalMap2.Node var3) {
         int var4 = var1.x;
         int var5 = var1.y;
         int var6 = var1.z;
         if (var3 != null) {
            var3.setXY((float)(var4 + 1), (float)var5);
         } else {
            PolygonalMap2.Node var7 = PolygonalMap2.Node.alloc().init((float)(var4 + 1), (float)var5, var6);
            var2.add(var7);
         }

         var1.inner = false;
         if (this.isInner(var4 + 1, var5 - 1, var6)) {
            this.get(var4 + 1, var5, var6).innerCorner = true;
            this.trace_W(this.get(var4 + 1, var5 - 1, var6), var2, (PolygonalMap2.Node)null);
         } else if (this.isInner(var4 + 1, var5, var6)) {
            this.trace_N(this.get(var4 + 1, var5, var6), var2, (PolygonalMap2.Node)var2.get(var2.size() - 1));
         } else if (this.isInner(var4, var5 + 1, var6)) {
            this.trace_E(var1, var2, (PolygonalMap2.Node)null);
         }

      }

      void trace_E(PolygonalMap2.ClusterOutline var1, ArrayList var2, PolygonalMap2.Node var3) {
         int var4 = var1.x;
         int var5 = var1.y;
         int var6 = var1.z;
         if (var3 != null) {
            var3.setXY((float)(var4 + 1), (float)(var5 + 1));
         } else {
            PolygonalMap2.Node var7 = PolygonalMap2.Node.alloc().init((float)(var4 + 1), (float)(var5 + 1), var6);
            var2.add(var7);
         }

         var1.inner = false;
         if (this.isInner(var4 + 1, var5 + 1, var6)) {
            this.get(var4, var5 + 1, var6).innerCorner = true;
            this.trace_N(this.get(var4 + 1, var5 + 1, var6), var2, (PolygonalMap2.Node)null);
         } else if (this.isInner(var4, var5 + 1, var6)) {
            this.trace_E(this.get(var4, var5 + 1, var6), var2, (PolygonalMap2.Node)var2.get(var2.size() - 1));
         } else if (this.isInner(var4 - 1, var5, var6)) {
            this.trace_S(var1, var2, (PolygonalMap2.Node)null);
         }

      }

      void trace_S(PolygonalMap2.ClusterOutline var1, ArrayList var2, PolygonalMap2.Node var3) {
         int var4 = var1.x;
         int var5 = var1.y;
         int var6 = var1.z;
         if (var3 != null) {
            var3.setXY((float)var4, (float)(var5 + 1));
         } else {
            PolygonalMap2.Node var7 = PolygonalMap2.Node.alloc().init((float)var4, (float)(var5 + 1), var6);
            var2.add(var7);
         }

         var1.inner = false;
         if (this.isInner(var4 - 1, var5 + 1, var6)) {
            this.get(var4 - 1, var5, var6).innerCorner = true;
            this.trace_E(this.get(var4 - 1, var5 + 1, var6), var2, (PolygonalMap2.Node)null);
         } else if (this.isInner(var4 - 1, var5, var6)) {
            this.trace_S(this.get(var4 - 1, var5, var6), var2, (PolygonalMap2.Node)var2.get(var2.size() - 1));
         } else if (this.isInner(var4, var5 - 1, var6)) {
            this.trace_W(var1, var2, (PolygonalMap2.Node)null);
         }

      }

      ArrayList trace(PolygonalMap2.ClusterOutline var1) {
         int var2 = var1.x;
         int var3 = var1.y;
         int var4 = var1.z;
         ArrayList var5 = new ArrayList();
         PolygonalMap2.Node var6 = PolygonalMap2.Node.alloc().init((float)var2, (float)var3, var4);
         var5.add(var6);
         var1.start = true;
         this.trace_N(var1, var5, (PolygonalMap2.Node)null);
         ((PolygonalMap2.Node)var5.get(var5.size() - 1)).release();
         var5.set(var5.size() - 1, var6);
         return var5;
      }

      // $FF: synthetic method
      ClusterOutlineGrid(Object var1) {
         this();
      }
   }

   private static class ClusterOutline {
      int x;
      int y;
      int z;
      boolean w;
      boolean n;
      boolean e;
      boolean s;
      boolean inner;
      boolean innerCorner;
      boolean start;
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.ClusterOutline init(int var1, int var2, int var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
         this.w = this.n = this.e = this.s = false;
         this.inner = this.innerCorner = this.start = false;
         return this;
      }

      static PolygonalMap2.ClusterOutline alloc() {
         return pool.isEmpty() ? new PolygonalMap2.ClusterOutline() : (PolygonalMap2.ClusterOutline)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class ObjectOutline {
      int x;
      int y;
      int z;
      boolean nw;
      boolean nw_w;
      boolean nw_n;
      boolean nw_e;
      boolean nw_s;
      boolean w_w;
      boolean w_e;
      boolean w_cutoff;
      boolean n_n;
      boolean n_s;
      boolean n_cutoff;
      ArrayList nodes;
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.ObjectOutline init(int var1, int var2, int var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
         this.nw = this.nw_w = this.nw_n = this.nw_e = false;
         this.w_w = this.w_e = this.w_cutoff = false;
         this.n_n = this.n_s = this.n_cutoff = false;
         return this;
      }

      static void setSolid(int var0, int var1, int var2, PolygonalMap2.ObjectOutline[][] var3) {
         setWest(var0, var1, var2, var3);
         setNorth(var0, var1, var2, var3);
         setWest(var0 + 1, var1, var2, var3);
         setNorth(var0, var1 + 1, var2, var3);
      }

      static void setWest(int var0, int var1, int var2, PolygonalMap2.ObjectOutline[][] var3) {
         PolygonalMap2.ObjectOutline var4 = get(var0, var1, var2, var3);
         if (var4 != null) {
            if (var4.nw) {
               var4.nw_s = false;
            } else {
               var4.nw = true;
               var4.nw_w = true;
               var4.nw_n = true;
               var4.nw_e = true;
               var4.nw_s = false;
            }

            var4.w_w = true;
            var4.w_e = true;
         }

         PolygonalMap2.ObjectOutline var5 = var4;
         var4 = get(var0, var1 + 1, var2, var3);
         if (var4 == null) {
            if (var5 != null) {
               var5.w_cutoff = true;
            }
         } else if (var4.nw) {
            var4.nw_n = false;
         } else {
            var4.nw = true;
            var4.nw_n = false;
            var4.nw_w = true;
            var4.nw_e = true;
            var4.nw_s = true;
         }

      }

      static void setNorth(int var0, int var1, int var2, PolygonalMap2.ObjectOutline[][] var3) {
         PolygonalMap2.ObjectOutline var4 = get(var0, var1, var2, var3);
         if (var4 != null) {
            if (var4.nw) {
               var4.nw_e = false;
            } else {
               var4.nw = true;
               var4.nw_w = true;
               var4.nw_n = true;
               var4.nw_e = false;
               var4.nw_s = true;
            }

            var4.n_n = true;
            var4.n_s = true;
         }

         PolygonalMap2.ObjectOutline var5 = var4;
         var4 = get(var0 + 1, var1, var2, var3);
         if (var4 == null) {
            if (var5 != null) {
               var5.n_cutoff = true;
            }
         } else if (var4.nw) {
            var4.nw_w = false;
         } else {
            var4.nw = true;
            var4.nw_n = true;
            var4.nw_w = false;
            var4.nw_e = true;
            var4.nw_s = true;
         }

      }

      static PolygonalMap2.ObjectOutline get(int var0, int var1, int var2, PolygonalMap2.ObjectOutline[][] var3) {
         if (var0 >= 0 && var0 < var3.length) {
            if (var1 >= 0 && var1 < var3[0].length) {
               if (var3[var0][var1] == null) {
                  var3[var0][var1] = alloc().init(var0, var1, var2);
               }

               return var3[var0][var1];
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      void trace_NW_N(PolygonalMap2.ObjectOutline[][] var1, PolygonalMap2.Node var2) {
         if (var2 != null) {
            var2.setXY((float)this.x + 0.3F, (float)this.y - 0.3F);
         } else {
            PolygonalMap2.Node var3 = PolygonalMap2.Node.alloc().init((float)this.x + 0.3F, (float)this.y - 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.nw_n = false;
         if (this.nw_e) {
            this.trace_NW_E(var1, (PolygonalMap2.Node)null);
         } else if (this.n_n) {
            this.trace_N_N(var1, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
         }

      }

      void trace_NW_S(PolygonalMap2.ObjectOutline[][] var1, PolygonalMap2.Node var2) {
         if (var2 != null) {
            var2.setXY((float)this.x - 0.3F, (float)this.y + 0.3F);
         } else {
            PolygonalMap2.Node var3 = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.nw_s = false;
         if (this.nw_w) {
            this.trace_NW_W(var1, (PolygonalMap2.Node)null);
         } else {
            PolygonalMap2.ObjectOutline var4 = get(this.x - 1, this.y, this.z, var1);
            if (var4 == null) {
               return;
            }

            if (var4.n_s) {
               var4.nodes = this.nodes;
               var4.trace_N_S(var1, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
            }
         }

      }

      void trace_NW_W(PolygonalMap2.ObjectOutline[][] var1, PolygonalMap2.Node var2) {
         if (var2 != null) {
            var2.setXY((float)this.x - 0.3F, (float)this.y - 0.3F);
         } else {
            PolygonalMap2.Node var3 = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)this.y - 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.nw_w = false;
         if (this.nw_n) {
            this.trace_NW_N(var1, (PolygonalMap2.Node)null);
         } else {
            PolygonalMap2.ObjectOutline var4 = get(this.x, this.y - 1, this.z, var1);
            if (var4 == null) {
               return;
            }

            if (var4.w_w) {
               var4.nodes = this.nodes;
               var4.trace_W_W(var1, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
            }
         }

      }

      void trace_NW_E(PolygonalMap2.ObjectOutline[][] var1, PolygonalMap2.Node var2) {
         if (var2 != null) {
            var2.setXY((float)this.x + 0.3F, (float)this.y + 0.3F);
         } else {
            PolygonalMap2.Node var3 = PolygonalMap2.Node.alloc().init((float)this.x + 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.nw_e = false;
         if (this.nw_s) {
            this.trace_NW_S(var1, (PolygonalMap2.Node)null);
         } else if (this.w_e) {
            this.trace_W_E(var1, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
         }

      }

      void trace_W_E(PolygonalMap2.ObjectOutline[][] var1, PolygonalMap2.Node var2) {
         PolygonalMap2.Node var3;
         if (var2 != null) {
            var2.setXY((float)this.x + 0.3F, (float)(this.y + 1) - 0.3F);
         } else {
            var3 = PolygonalMap2.Node.alloc().init((float)this.x + 0.3F, (float)(this.y + 1) - 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.w_e = false;
         if (this.w_cutoff) {
            var3 = (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1);
            var3.setXY((float)this.x + 0.3F, (float)(this.y + 1) + 0.3F);
            var3 = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)(this.y + 1) + 0.3F, this.z);
            this.nodes.add(var3);
            var3 = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)(this.y + 1) - 0.3F, this.z);
            this.nodes.add(var3);
            this.trace_W_W(var1, var3);
         } else {
            PolygonalMap2.ObjectOutline var4 = get(this.x, this.y + 1, this.z, var1);
            if (var4 != null) {
               if (var4.nw && var4.nw_e) {
                  var4.nodes = this.nodes;
                  var4.trace_NW_E(var1, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
               } else if (var4.n_n) {
                  var4.nodes = this.nodes;
                  var4.trace_N_N(var1, (PolygonalMap2.Node)null);
               }

            }
         }
      }

      void trace_W_W(PolygonalMap2.ObjectOutline[][] var1, PolygonalMap2.Node var2) {
         if (var2 != null) {
            var2.setXY((float)this.x - 0.3F, (float)this.y + 0.3F);
         } else {
            PolygonalMap2.Node var3 = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.w_w = false;
         if (this.nw_w) {
            this.trace_NW_W(var1, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
         } else {
            PolygonalMap2.ObjectOutline var4 = get(this.x - 1, this.y, this.z, var1);
            if (var4 == null) {
               return;
            }

            if (var4.n_s) {
               var4.nodes = this.nodes;
               var4.trace_N_S(var1, (PolygonalMap2.Node)null);
            }
         }

      }

      void trace_N_N(PolygonalMap2.ObjectOutline[][] var1, PolygonalMap2.Node var2) {
         PolygonalMap2.Node var3;
         if (var2 != null) {
            var2.setXY((float)(this.x + 1) - 0.3F, (float)this.y - 0.3F);
         } else {
            var3 = PolygonalMap2.Node.alloc().init((float)(this.x + 1) - 0.3F, (float)this.y - 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.n_n = false;
         if (this.n_cutoff) {
            var3 = (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1);
            var3.setXY((float)(this.x + 1) + 0.3F, (float)this.y - 0.3F);
            var3 = PolygonalMap2.Node.alloc().init((float)(this.x + 1) + 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
            var3 = PolygonalMap2.Node.alloc().init((float)(this.x + 1) - 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
            this.trace_N_S(var1, var3);
         } else {
            PolygonalMap2.ObjectOutline var4 = get(this.x + 1, this.y, this.z, var1);
            if (var4 != null) {
               if (var4.nw_n) {
                  var4.nodes = this.nodes;
                  var4.trace_NW_N(var1, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
               } else {
                  var4 = get(this.x + 1, this.y - 1, this.z, var1);
                  if (var4 == null) {
                     return;
                  }

                  if (var4.w_w) {
                     var4.nodes = this.nodes;
                     var4.trace_W_W(var1, (PolygonalMap2.Node)null);
                  }
               }

            }
         }
      }

      void trace_N_S(PolygonalMap2.ObjectOutline[][] var1, PolygonalMap2.Node var2) {
         if (var2 != null) {
            var2.setXY((float)this.x + 0.3F, (float)this.y + 0.3F);
         } else {
            PolygonalMap2.Node var3 = PolygonalMap2.Node.alloc().init((float)this.x + 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.n_s = false;
         if (this.nw_s) {
            this.trace_NW_S(var1, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
         } else if (this.w_e) {
            this.trace_W_E(var1, (PolygonalMap2.Node)null);
         }

      }

      void trace(PolygonalMap2.ObjectOutline[][] var1, ArrayList var2) {
         var2.clear();
         this.nodes = var2;
         PolygonalMap2.Node var3 = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)this.y - 0.3F, this.z);
         var2.add(var3);
         this.trace_NW_N(var1, (PolygonalMap2.Node)null);
         if (var2.size() != 2 && var3.x == ((PolygonalMap2.Node)var2.get(var2.size() - 1)).x && var3.y == ((PolygonalMap2.Node)var2.get(var2.size() - 1)).y) {
            ((PolygonalMap2.Node)var2.get(var2.size() - 1)).release();
            var2.set(var2.size() - 1, var3);
         } else {
            var2.clear();
         }

      }

      static PolygonalMap2.ObjectOutline alloc() {
         return pool.isEmpty() ? new PolygonalMap2.ObjectOutline() : (PolygonalMap2.ObjectOutline)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class Obstacle {
      PolygonalMap2.Vehicle vehicle;
      ArrayList edges = new ArrayList();
      PolygonalMap2.ImmutableRectF bounds;
      static Vector3f tempVector3f_1 = new Vector3f();
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.Obstacle init(PolygonalMap2.Vehicle var1) {
         this.vehicle = var1;
         this.edges.clear();
         return this;
      }

      PolygonalMap2.Obstacle init(IsoGridSquare var1) {
         this.vehicle = null;
         this.edges.clear();
         return this;
      }

      boolean hasNode(PolygonalMap2.Node var1) {
         for(int var2 = 0; var2 < this.edges.size(); ++var2) {
            PolygonalMap2.Edge var3 = (PolygonalMap2.Edge)this.edges.get(var2);
            if (var3.hasNode(var1)) {
               return true;
            }
         }

         return false;
      }

      boolean hasAdjacentNodes(PolygonalMap2.Node var1, PolygonalMap2.Node var2) {
         for(int var3 = 0; var3 < this.edges.size(); ++var3) {
            PolygonalMap2.Edge var4 = (PolygonalMap2.Edge)this.edges.get(var3);
            if (var4.hasNode(var1) && var4.hasNode(var2)) {
               return true;
            }
         }

         return false;
      }

      boolean isPointInPolygon_CrossingNumber(float var1, float var2) {
         int var3 = 0;

         for(int var4 = 0; var4 < this.edges.size(); ++var4) {
            PolygonalMap2.Edge var5 = (PolygonalMap2.Edge)this.edges.get(var4);
            if (var5.node1.y <= var2 && var5.node2.y > var2 || var5.node1.y > var2 && var5.node2.y <= var2) {
               float var6 = (var2 - var5.node1.y) / (var5.node2.y - var5.node1.y);
               if (var1 < var5.node1.x + var6 * (var5.node2.x - var5.node1.x)) {
                  ++var3;
               }
            }
         }

         return var3 % 2 == 1;
      }

      float isLeft(float var1, float var2, float var3, float var4, float var5, float var6) {
         return (var3 - var1) * (var6 - var2) - (var5 - var1) * (var4 - var2);
      }

      boolean isPointInPolygon_WindingNumber(float var1, float var2) {
         int var3 = 0;

         for(int var4 = 0; var4 < this.edges.size(); ++var4) {
            PolygonalMap2.Edge var5 = (PolygonalMap2.Edge)this.edges.get(var4);
            if (var5.node1.y <= var2) {
               if (var5.node2.y > var2 && this.isLeft(var5.node1.x, var5.node1.y, var5.node2.x, var5.node2.y, var1, var2) > 0.0F) {
                  ++var3;
               }
            } else if (var5.node2.y <= var2 && this.isLeft(var5.node1.x, var5.node1.y, var5.node2.x, var5.node2.y, var1, var2) < 0.0F) {
               --var3;
            }
         }

         return var3 != 0;
      }

      boolean isNodeInsideOf(PolygonalMap2.Node var1) {
         if (this.hasNode(var1)) {
            return false;
         } else {
            return !this.bounds.containsPoint(var1.x, var1.y) ? false : this.isPointInPolygon_WindingNumber(var1.x, var1.y);
         }
      }

      PolygonalMap2.Node getClosestPointOnEdge(float var1, float var2, Vector2 var3) {
         double var4 = Double.MAX_VALUE;
         PolygonalMap2.Node var6 = null;

         for(int var7 = 0; var7 < this.edges.size(); ++var7) {
            PolygonalMap2.Edge var8 = (PolygonalMap2.Edge)this.edges.get(var7);
            if (var8.node1.visible.contains(var8.node2)) {
               float var9 = var8.node1.x;
               float var10 = var8.node1.y;
               float var11 = var8.node2.x;
               float var12 = var8.node2.y;
               double var13 = (double)((var1 - var9) * (var11 - var9) + (var2 - var10) * (var12 - var10)) / (Math.pow((double)(var11 - var9), 2.0D) + Math.pow((double)(var12 - var10), 2.0D));
               double var15 = (double)var9 + var13 * (double)(var11 - var9);
               double var17 = (double)var10 + var13 * (double)(var12 - var10);
               PolygonalMap2.Node var19 = null;
               if (var13 <= 0.0D) {
                  var15 = (double)var9;
                  var17 = (double)var10;
                  var19 = var8.node1;
               } else if (var13 >= 1.0D) {
                  var15 = (double)var11;
                  var17 = (double)var12;
                  var19 = var8.node2;
               }

               double var20 = ((double)var1 - var15) * ((double)var1 - var15) + ((double)var2 - var17) * ((double)var2 - var17);
               if (var20 < var4) {
                  var3.set((float)var15, (float)var17);
                  var4 = var20;
                  if (var19 != null) {
                     var6 = var19;
                  } else {
                     var6 = null;
                  }
               }
            }
         }

         return var6;
      }

      boolean splitEdgeAtNearestPoint(float var1, float var2, int var3, PolygonalMap2.AdjustStartEndNodeData var4) {
         PolygonalMap2.Edge var5 = null;
         double var6 = Double.MAX_VALUE;
         float var8 = 0.0F;
         float var9 = 0.0F;
         PolygonalMap2.Node var10 = null;

         for(int var11 = 0; var11 < this.edges.size(); ++var11) {
            PolygonalMap2.Edge var12 = (PolygonalMap2.Edge)this.edges.get(var11);
            if (var12.node1.visible.contains(var12.node2)) {
               float var13 = var12.node1.x;
               float var14 = var12.node1.y;
               float var15 = var12.node2.x;
               float var16 = var12.node2.y;
               double var17 = (double)((var1 - var13) * (var15 - var13) + (var2 - var14) * (var16 - var14)) / (Math.pow((double)(var15 - var13), 2.0D) + Math.pow((double)(var16 - var14), 2.0D));
               double var19 = (double)var13 + var17 * (double)(var15 - var13);
               double var21 = (double)var14 + var17 * (double)(var16 - var14);
               PolygonalMap2.Node var23 = null;
               if (var17 <= 0.0D) {
                  var19 = (double)var13;
                  var21 = (double)var14;
                  var23 = var12.node1;
               } else if (var17 >= 1.0D) {
                  var19 = (double)var15;
                  var21 = (double)var16;
                  var23 = var12.node2;
               }

               double var24 = ((double)var1 - var19) * ((double)var1 - var19) + ((double)var2 - var21) * ((double)var2 - var21);
               if (var24 < var6) {
                  var8 = (float)var19;
                  var9 = (float)var21;
                  var6 = var24;
                  if (var23 != null) {
                     var10 = var23;
                  } else {
                     var10 = null;
                  }

                  var5 = var12;
               }
            }
         }

         if (var5 == null) {
            return false;
         } else {
            var4.obstacle = this;
            if (var10 == null) {
               var4.node = PolygonalMap2.Node.alloc().init(var8, var9, var3);
               var4.newEdge = var5.split(var4.node);
               var4.isNodeNew = true;
            } else {
               var4.node = var10;
               var4.newEdge = null;
               var4.isNodeNew = false;
            }

            return true;
         }
      }

      void unsplit(PolygonalMap2.Node var1) {
         for(int var2 = 0; var2 < this.edges.size(); ++var2) {
            PolygonalMap2.Edge var3 = (PolygonalMap2.Edge)this.edges.get(var2);
            if (var3.node1 == var1) {
               if (var2 > 0) {
                  PolygonalMap2.Edge var4 = (PolygonalMap2.Edge)this.edges.get(var2 - 1);
                  var4.node2 = var3.node2;

                  assert var3.node2.edges.contains(var3);

                  var3.node2.edges.remove(var3);

                  assert !var3.node2.edges.contains(var4);

                  var3.node2.edges.add(var4);
               } else {
                  ((PolygonalMap2.Edge)this.edges.get(var2 + 1)).node1 = ((PolygonalMap2.Edge)this.edges.get(this.edges.size() - 1)).node2;
               }

               var3.release();
               this.edges.remove(var2);
               break;
            }
         }

      }

      void calcBounds() {
         float var1 = Float.MAX_VALUE;
         float var2 = Float.MAX_VALUE;
         float var3 = Float.MIN_VALUE;
         float var4 = Float.MIN_VALUE;

         for(int var5 = 0; var5 < this.edges.size(); ++var5) {
            PolygonalMap2.Edge var6 = (PolygonalMap2.Edge)this.edges.get(var5);
            var1 = Math.min(var1, var6.node1.x);
            var2 = Math.min(var2, var6.node1.y);
            var3 = Math.max(var3, var6.node1.x);
            var4 = Math.max(var4, var6.node1.y);
         }

         if (this.bounds != null) {
            this.bounds.release();
         }

         float var7 = 0.01F;
         this.bounds = PolygonalMap2.ImmutableRectF.alloc().init(var1 - var7, var2 - var7, var3 - var1 + var7 * 2.0F, var4 - var2 + var7 * 2.0F);
      }

      static PolygonalMap2.Obstacle alloc() {
         return pool.isEmpty() ? new PolygonalMap2.Obstacle() : (PolygonalMap2.Obstacle)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class Edge {
      PolygonalMap2.Node node1;
      PolygonalMap2.Node node2;
      PolygonalMap2.Obstacle obstacle;
      ArrayList intersections = new ArrayList();
      static ArrayDeque pool = new ArrayDeque();

      PolygonalMap2.Edge init(PolygonalMap2.Node var1, PolygonalMap2.Node var2, PolygonalMap2.Obstacle var3) {
         this.node1 = var1;
         this.node2 = var2;
         var1.edges.add(this);
         var2.edges.add(this);
         this.obstacle = var3;
         this.intersections.clear();
         return this;
      }

      boolean hasNode(PolygonalMap2.Node var1) {
         return var1 == this.node1 || var1 == this.node2;
      }

      PolygonalMap2.Edge split(PolygonalMap2.Node var1) {
         PolygonalMap2.Edge var2 = alloc().init(var1, this.node2, this.obstacle);
         this.obstacle.edges.add(this.obstacle.edges.indexOf(this) + 1, var2);
         this.node2.edges.remove(this);
         this.node2 = var1;
         this.node2.edges.add(this);
         return var2;
      }

      static PolygonalMap2.Edge alloc() {
         return pool.isEmpty() ? new PolygonalMap2.Edge() : (PolygonalMap2.Edge)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class Node {
      static int nextID = 1;
      final int ID;
      float x;
      float y;
      int z;
      boolean ignore;
      PolygonalMap2.Square square;
      ArrayList graphs;
      ArrayList edges = new ArrayList();
      ArrayList visible = new ArrayList();
      static ArrayList tempObstacles = new ArrayList();
      static ArrayDeque pool = new ArrayDeque();

      Node() {
         this.ID = nextID++;
      }

      PolygonalMap2.Node init(float var1, float var2, int var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
         this.ignore = false;
         this.square = null;
         if (this.graphs != null) {
            this.graphs.clear();
         }

         this.edges.clear();
         this.visible.clear();
         return this;
      }

      PolygonalMap2.Node init(PolygonalMap2.Square var1) {
         this.x = (float)var1.x + 0.5F;
         this.y = (float)var1.y + 0.5F;
         this.z = var1.z;
         this.ignore = false;
         this.square = var1;
         if (this.graphs != null) {
            this.graphs.clear();
         }

         this.edges.clear();
         this.visible.clear();
         return this;
      }

      PolygonalMap2.Node setXY(float var1, float var2) {
         this.x = var1;
         this.y = var2;
         return this;
      }

      void addGraph(PolygonalMap2.VisibilityGraph var1) {
         if (this.graphs == null) {
            this.graphs = new ArrayList();
         }

         assert !this.graphs.contains(var1);

         this.graphs.add(var1);
      }

      boolean sharesEdge(PolygonalMap2.Node var1) {
         for(int var2 = 0; var2 < this.edges.size(); ++var2) {
            PolygonalMap2.Edge var3 = (PolygonalMap2.Edge)this.edges.get(var2);
            if (var3.hasNode(var1)) {
               return true;
            }
         }

         return false;
      }

      boolean sharesShape(PolygonalMap2.Node var1) {
         for(int var2 = 0; var2 < this.edges.size(); ++var2) {
            PolygonalMap2.Edge var3 = (PolygonalMap2.Edge)this.edges.get(var2);

            for(int var4 = 0; var4 < var1.edges.size(); ++var4) {
               PolygonalMap2.Edge var5 = (PolygonalMap2.Edge)var1.edges.get(var4);
               if (var3.obstacle != null && var3.obstacle == var5.obstacle) {
                  return true;
               }
            }
         }

         return false;
      }

      void getObstacles(ArrayList var1) {
         for(int var2 = 0; var2 < this.edges.size(); ++var2) {
            PolygonalMap2.Edge var3 = (PolygonalMap2.Edge)this.edges.get(var2);
            if (!var1.contains(var3.obstacle)) {
               var1.add(var3.obstacle);
            }
         }

      }

      boolean onSameShapeButDoesNotShareAnEdge(PolygonalMap2.Node var1) {
         tempObstacles.clear();
         this.getObstacles(tempObstacles);

         for(int var2 = 0; var2 < tempObstacles.size(); ++var2) {
            PolygonalMap2.Obstacle var3 = (PolygonalMap2.Obstacle)tempObstacles.get(var2);
            if (var3.hasNode(var1) && !var3.hasAdjacentNodes(this, var1)) {
               return true;
            }
         }

         return false;
      }

      static PolygonalMap2.Node alloc() {
         boolean var0;
         if (pool.isEmpty()) {
            var0 = false;
         } else {
            var0 = false;
         }

         return pool.isEmpty() ? new PolygonalMap2.Node() : (PolygonalMap2.Node)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }
}
