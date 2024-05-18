package zombie.vehicles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.joml.Vector3f;
import zombie.characters.IsoGameCharacter;
import zombie.core.Core;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public class CollideWithObstacles {
   static final float RADIUS = 0.3F;
   private ArrayList obstacles = new ArrayList();
   private ArrayList nodes = new ArrayList();
   private ArrayList intersections = new ArrayList();
   private CollideWithObstacles.ImmutableRectF moveBounds = new CollideWithObstacles.ImmutableRectF();
   private CollideWithObstacles.ImmutableRectF vehicleBounds = new CollideWithObstacles.ImmutableRectF();
   private Vector2 move = new Vector2();
   private Vector2 closest = new Vector2();
   private Vector2 nodeNormal = new Vector2();
   private Vector2 edgeVec = new Vector2();
   private ArrayList vehicles = new ArrayList();
   CollideWithObstacles.CCObjectOutline[][] oo = new CollideWithObstacles.CCObjectOutline[5][5];
   ArrayList obstacleTraceNodes = new ArrayList();
   CollideWithObstacles.CompareIntersection comparator = new CollideWithObstacles.CompareIntersection();

   void getVehiclesInRect(float var1, float var2, float var3, float var4, int var5) {
      this.vehicles.clear();
      int var6 = (int)(var1 / 10.0F);
      int var7 = (int)(var2 / 10.0F);
      int var8 = (int)Math.ceil((double)(var3 / 10.0F));
      int var9 = (int)Math.ceil((double)(var4 / 10.0F));

      for(int var10 = var7; var10 < var9; ++var10) {
         for(int var11 = var6; var11 < var8; ++var11) {
            IsoChunk var12 = GameServer.bServer ? ServerMap.instance.getChunk(var11, var10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var11 * 10, var10 * 10, 0);
            if (var12 != null) {
               for(int var13 = 0; var13 < var12.vehicles.size(); ++var13) {
                  BaseVehicle var14 = (BaseVehicle)var12.vehicles.get(var13);
                  if (var14.getScript() != null && (int)var14.z == var5) {
                     this.vehicles.add(var14);
                  }
               }
            }
         }
      }

   }

   void getObstaclesInRect(float var1, float var2, float var3, float var4, int var5, int var6, int var7) {
      this.nodes.clear();
      this.obstacles.clear();
      this.moveBounds.init(var1 - 1.0F, var2 - 1.0F, var3 - var1 + 2.0F, var4 - var2 + 2.0F);
      this.getVehiclesInRect(var1 - 1.0F - 4.0F, var2 - 1.0F - 4.0F, var3 + 2.0F + 8.0F, var4 + 2.0F + 8.0F, var7);

      int var8;
      CollideWithObstacles.CCNode var16;
      CollideWithObstacles.CCNode var18;
      CollideWithObstacles.CCNode var19;
      for(var8 = 0; var8 < this.vehicles.size(); ++var8) {
         BaseVehicle var9 = (BaseVehicle)this.vehicles.get(var8);
         PolygonalMap2.VehiclePoly var10 = var9.getPolyPlusRadius();
         float var11 = Math.min(var10.x1, Math.min(var10.x2, Math.min(var10.x3, var10.x4)));
         float var12 = Math.min(var10.y1, Math.min(var10.y2, Math.min(var10.y3, var10.y4)));
         float var13 = Math.max(var10.x1, Math.max(var10.x2, Math.max(var10.x3, var10.x4)));
         float var14 = Math.max(var10.y1, Math.max(var10.y2, Math.max(var10.y3, var10.y4)));
         this.vehicleBounds.init(var11, var12, var13 - var11, var14 - var12);
         if (this.moveBounds.intersects(this.vehicleBounds)) {
            int var15 = (int)var10.z;
            var16 = CollideWithObstacles.CCNode.alloc().init(var10.x1, var10.y1, var15);
            CollideWithObstacles.CCNode var17 = CollideWithObstacles.CCNode.alloc().init(var10.x2, var10.y2, var15);
            var18 = CollideWithObstacles.CCNode.alloc().init(var10.x3, var10.y3, var15);
            var19 = CollideWithObstacles.CCNode.alloc().init(var10.x4, var10.y4, var15);
            CollideWithObstacles.CCObstacle var20 = CollideWithObstacles.CCObstacle.alloc().init();
            CollideWithObstacles.CCEdge var21 = CollideWithObstacles.CCEdge.alloc().init(var16, var17, var20);
            CollideWithObstacles.CCEdge var22 = CollideWithObstacles.CCEdge.alloc().init(var17, var18, var20);
            CollideWithObstacles.CCEdge var23 = CollideWithObstacles.CCEdge.alloc().init(var18, var19, var20);
            CollideWithObstacles.CCEdge var24 = CollideWithObstacles.CCEdge.alloc().init(var19, var16, var20);
            var20.edges.add(var21);
            var20.edges.add(var22);
            var20.edges.add(var23);
            var20.edges.add(var24);
            var20.calcBounds();
            this.obstacles.add(var20);
            this.nodes.add(var16);
            this.nodes.add(var17);
            this.nodes.add(var18);
            this.nodes.add(var19);
         }
      }

      if (!this.obstacles.isEmpty()) {
         var8 = var5 - 2;
         int var25 = var6 - 2;
         int var26 = var5 + 2 + 1;
         int var27 = var6 + 2 + 1;

         int var28;
         int var29;
         for(var28 = var25; var28 < var27; ++var28) {
            for(var29 = var8; var29 < var26; ++var29) {
               CollideWithObstacles.CCObjectOutline.get(var29 - var8, var28 - var25, var7, this.oo).init(var29 - var8, var28 - var25, var7);
            }
         }

         for(var28 = var25; var28 < var27 - 1; ++var28) {
            for(var29 = var8; var29 < var26 - 1; ++var29) {
               IsoGridSquare var30 = IsoWorld.instance.CurrentCell.getGridSquare(var29, var28, var7);
               if (var30 != null) {
                  if (var30.isSolid() || var30.isSolidTrans() && !var30.isAdjacentToWindow() || var30.Has(IsoObjectType.stairsMN) || var30.Has(IsoObjectType.stairsTN) || var30.Has(IsoObjectType.stairsMW) || var30.Has(IsoObjectType.stairsTW)) {
                     CollideWithObstacles.CCObjectOutline.setSolid(var29 - var8, var28 - var25, var7, this.oo);
                  }

                  if (var30.Is(IsoFlagType.collideW) || var30.hasBlockedDoor(false) || var30.Has(IsoObjectType.stairsBN)) {
                     CollideWithObstacles.CCObjectOutline.setWest(var29 - var8, var28 - var25, var7, this.oo);
                  }

                  if (var30.Is(IsoFlagType.collideN) || var30.hasBlockedDoor(true) || var30.Has(IsoObjectType.stairsBW)) {
                     CollideWithObstacles.CCObjectOutline.setNorth(var29 - var8, var28 - var25, var7, this.oo);
                  }

                  if (var30.Has(IsoObjectType.stairsBN) && var29 != var26 - 2) {
                     var30 = IsoWorld.instance.CurrentCell.getGridSquare(var29 + 1, var28, var7);
                     if (var30 != null) {
                        CollideWithObstacles.CCObjectOutline.setWest(var29 + 1 - var8, var28 - var25, var7, this.oo);
                     }
                  }

                  if (var30.Has(IsoObjectType.stairsBW) && var28 != var27 - 2) {
                     var30 = IsoWorld.instance.CurrentCell.getGridSquare(var29, var28 + 1, var7);
                     if (var30 != null) {
                        CollideWithObstacles.CCObjectOutline.setNorth(var29 - var8, var28 + 1 - var25, var7, this.oo);
                     }
                  }
               }
            }
         }

         for(var28 = 0; var28 < var27 - var25; ++var28) {
            for(var29 = 0; var29 < var26 - var8; ++var29) {
               CollideWithObstacles.CCObjectOutline var32 = CollideWithObstacles.CCObjectOutline.get(var29, var28, var7, this.oo);
               if (var32 != null && var32.nw && var32.nw_w && var32.nw_n) {
                  var32.trace(this.oo, this.obstacleTraceNodes);
                  if (!var32.nodes.isEmpty()) {
                     CollideWithObstacles.CCObstacle var31 = CollideWithObstacles.CCObstacle.alloc().init();
                     var16 = (CollideWithObstacles.CCNode)var32.nodes.get(var32.nodes.size() - 1);

                     for(int var33 = var32.nodes.size() - 1; var33 > 0; --var33) {
                        var18 = (CollideWithObstacles.CCNode)var32.nodes.get(var33);
                        var19 = (CollideWithObstacles.CCNode)var32.nodes.get(var33 - 1);
                        var18.x += (float)var8;
                        var18.y += (float)var25;
                        CollideWithObstacles.CCEdge var34 = CollideWithObstacles.CCEdge.alloc().init(var18, var19, var31);
                        float var35 = var19.x + (var19 != var16 ? (float)var8 : 0.0F);
                        float var36 = var19.y + (var19 != var16 ? (float)var25 : 0.0F);
                        var34.normal.set(var35 - var18.x, var36 - var18.y);
                        var34.normal.normalize();
                        var34.normal.rotate((float)Math.toRadians(90.0D));
                        var31.edges.add(var34);
                        this.nodes.add(var18);
                     }

                     var31.calcBounds();
                     this.obstacles.add(var31);
                  }
               }
            }
         }

      }
   }

   void checkEdgeIntersection() {
      boolean var1 = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRender.getValue();

      int var2;
      CollideWithObstacles.CCObstacle var3;
      int var4;
      int var6;
      for(var2 = 0; var2 < this.obstacles.size(); ++var2) {
         var3 = (CollideWithObstacles.CCObstacle)this.obstacles.get(var2);

         for(var4 = var2 + 1; var4 < this.obstacles.size(); ++var4) {
            CollideWithObstacles.CCObstacle var5 = (CollideWithObstacles.CCObstacle)this.obstacles.get(var4);
            if (var3.bounds.intersects(var5.bounds)) {
               for(var6 = 0; var6 < var3.edges.size(); ++var6) {
                  CollideWithObstacles.CCEdge var7 = (CollideWithObstacles.CCEdge)var3.edges.get(var6);

                  for(int var8 = 0; var8 < var5.edges.size(); ++var8) {
                     CollideWithObstacles.CCEdge var9 = (CollideWithObstacles.CCEdge)var5.edges.get(var8);
                     CollideWithObstacles.CCIntersection var10 = this.getIntersection(var7, var9);
                     if (var10 != null) {
                        var7.intersections.add(var10);
                        var9.intersections.add(var10);
                        if (var1) {
                           LineDrawer.addLine(var10.nodeSplit.x - 0.1F, var10.nodeSplit.y - 0.1F, (float)var7.node1.z, var10.nodeSplit.x + 0.1F, var10.nodeSplit.y + 0.1F, (float)var7.node1.z, 1.0F, 0.0F, 0.0F, (String)null, false);
                        }

                        if (!var7.hasNode(var10.nodeSplit) && !var9.hasNode(var10.nodeSplit)) {
                           this.nodes.add(var10.nodeSplit);
                        }

                        this.intersections.add(var10);
                     }
                  }
               }
            }
         }
      }

      for(var2 = 0; var2 < this.obstacles.size(); ++var2) {
         var3 = (CollideWithObstacles.CCObstacle)this.obstacles.get(var2);

         for(var4 = var3.edges.size() - 1; var4 >= 0; --var4) {
            CollideWithObstacles.CCEdge var11 = (CollideWithObstacles.CCEdge)var3.edges.get(var4);
            if (!var11.intersections.isEmpty()) {
               this.comparator.edge = var11;
               Collections.sort(var11.intersections, this.comparator);

               for(var6 = var11.intersections.size() - 1; var6 >= 0; --var6) {
                  CollideWithObstacles.CCIntersection var12 = (CollideWithObstacles.CCIntersection)var11.intersections.get(var6);
                  var12.split(var11);
               }
            }
         }
      }

   }

   boolean collinear(float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = (var3 - var1) * (var6 - var2) - (var5 - var1) * (var4 - var2);
      return var7 >= -0.05F && var7 < 0.05F;
   }

   boolean within(float var1, float var2, float var3) {
      return var1 <= var2 && var2 <= var3 || var3 <= var2 && var2 <= var1;
   }

   boolean is_on(float var1, float var2, float var3, float var4, float var5, float var6) {
      boolean var10000;
      label25: {
         if (this.collinear(var1, var2, var3, var4, var5, var6)) {
            if (var1 != var3) {
               if (this.within(var1, var5, var3)) {
                  break label25;
               }
            } else if (this.within(var2, var6, var4)) {
               break label25;
            }
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   public CollideWithObstacles.CCIntersection getIntersection(CollideWithObstacles.CCEdge var1, CollideWithObstacles.CCEdge var2) {
      float var3 = var1.node1.x;
      float var4 = var1.node1.y;
      float var5 = var1.node2.x;
      float var6 = var1.node2.y;
      float var7 = var2.node1.x;
      float var8 = var2.node1.y;
      float var9 = var2.node2.x;
      float var10 = var2.node2.y;
      double var11 = (double)((var10 - var8) * (var5 - var3) - (var9 - var7) * (var6 - var4));
      if (var11 > -0.01D && var11 < 0.01D) {
         return null;
      } else {
         double var13 = (double)((var9 - var7) * (var4 - var8) - (var10 - var8) * (var3 - var7)) / var11;
         double var15 = (double)((var5 - var3) * (var4 - var8) - (var6 - var4) * (var3 - var7)) / var11;
         if (var13 >= 0.0D && var13 <= 1.0D && var15 >= 0.0D && var15 <= 1.0D) {
            float var17 = (float)((double)var3 + var13 * (double)(var5 - var3));
            float var18 = (float)((double)var4 + var13 * (double)(var6 - var4));
            CollideWithObstacles.CCNode var19 = null;
            CollideWithObstacles.CCNode var20 = null;
            if (var13 < 0.009999999776482582D) {
               var19 = var1.node1;
            } else if (var13 > 0.9900000095367432D) {
               var19 = var1.node2;
            }

            if (var15 < 0.009999999776482582D) {
               var20 = var2.node1;
            } else if (var15 > 0.9900000095367432D) {
               var20 = var2.node2;
            }

            if (var19 != null && var20 != null) {
               CollideWithObstacles.CCIntersection var21 = CollideWithObstacles.CCIntersection.alloc().init(var1, var2, (float)var13, (float)var15, var19);
               var1.intersections.add(var21);
               this.intersections.add(var21);
               var21 = CollideWithObstacles.CCIntersection.alloc().init(var1, var2, (float)var13, (float)var15, var20);
               var2.intersections.add(var21);
               this.intersections.add(var21);
               LineDrawer.addLine(var21.nodeSplit.x - 0.1F, var21.nodeSplit.y - 0.1F, (float)var1.node1.z, var21.nodeSplit.x + 0.1F, var21.nodeSplit.y + 0.1F, (float)var1.node1.z, 1.0F, 0.0F, 0.0F, (String)null, false);
               return null;
            } else {
               return var19 == null && var20 == null ? CollideWithObstacles.CCIntersection.alloc().init(var1, var2, (float)var13, (float)var15, var17, var18) : CollideWithObstacles.CCIntersection.alloc().init(var1, var2, (float)var13, (float)var15, var19 == null ? var20 : var19);
            }
         } else {
            return null;
         }
      }
   }

   void checkNodesInObstacles() {
      for(int var1 = 0; var1 < this.nodes.size(); ++var1) {
         CollideWithObstacles.CCNode var2 = (CollideWithObstacles.CCNode)this.nodes.get(var1);

         for(int var3 = 0; var3 < this.obstacles.size(); ++var3) {
            CollideWithObstacles.CCObstacle var4 = (CollideWithObstacles.CCObstacle)this.obstacles.get(var3);
            boolean var5 = false;

            for(int var6 = 0; var6 < this.intersections.size(); ++var6) {
               CollideWithObstacles.CCIntersection var7 = (CollideWithObstacles.CCIntersection)this.intersections.get(var6);
               if (var7.nodeSplit == var2) {
                  if (var7.edge1.obstacle == var4 || var7.edge2.obstacle == var4) {
                     var5 = true;
                  }
                  break;
               }
            }

            if (!var5 && var4.isNodeInsideOf(var2)) {
               var2.ignore = true;
               break;
            }
         }
      }

   }

   boolean isVisible(CollideWithObstacles.CCNode var1, CollideWithObstacles.CCNode var2) {
      if (var1.sharesEdge(var2)) {
         return !var1.onSameShapeButDoesNotShareAnEdge(var2);
      } else {
         return !var1.sharesShape(var2);
      }
   }

   void calculateNodeVisibility() {
      for(int var1 = 0; var1 < this.obstacles.size(); ++var1) {
         CollideWithObstacles.CCObstacle var2 = (CollideWithObstacles.CCObstacle)this.obstacles.get(var1);

         for(int var3 = 0; var3 < var2.edges.size(); ++var3) {
            CollideWithObstacles.CCEdge var4 = (CollideWithObstacles.CCEdge)var2.edges.get(var3);
            if (!var4.node1.ignore && !var4.node2.ignore && this.isVisible(var4.node1, var4.node2)) {
               var4.node1.visible.add(var4.node2);
               var4.node2.visible.add(var4.node1);
            }
         }
      }

   }

   void resolveCollision(IsoGameCharacter var1, float var2, float var3) {
      if (var1.getCurrentSquare() == null || !var1.getCurrentSquare().HasStairs()) {
         boolean var4 = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRender.getValue();
         float var5 = var1.x;
         float var6 = var1.y;
         float var7 = var2;
         float var8 = var3;
         if (var4) {
            LineDrawer.addLine(var5, var6, (float)((int)var1.z), var2, var3, (float)((int)var1.z), 1.0F, 1.0F, 1.0F, (String)null, true);
         }

         if (var5 != var2 || var6 != var3) {
            this.move.set(var2 - var1.x, var3 - var1.y);
            this.move.normalize();

            int var9;
            for(var9 = 0; var9 < this.nodes.size(); ++var9) {
               ((CollideWithObstacles.CCNode)this.nodes.get(var9)).release();
            }

            for(var9 = 0; var9 < this.obstacles.size(); ++var9) {
               CollideWithObstacles.CCObstacle var10 = (CollideWithObstacles.CCObstacle)this.obstacles.get(var9);

               for(int var11 = 0; var11 < var10.edges.size(); ++var11) {
                  CollideWithObstacles.CCEdge var12 = (CollideWithObstacles.CCEdge)var10.edges.get(var11);
                  ((CollideWithObstacles.CCEdge)var10.edges.get(var11)).release();
               }

               var10.release();
            }

            for(var9 = 0; var9 < this.intersections.size(); ++var9) {
               ((CollideWithObstacles.CCIntersection)this.intersections.get(var9)).release();
            }

            this.intersections.clear();
            this.getObstaclesInRect(Math.min(var5, var2), Math.min(var6, var3), Math.max(var5, var2), Math.max(var6, var3), (int)var1.x, (int)var1.y, (int)var1.z);
            this.checkEdgeIntersection();
            this.checkNodesInObstacles();
            this.calculateNodeVisibility();
            CollideWithObstacles.CCNode var36;
            if (var4) {
               Iterator var37 = this.nodes.iterator();

               while(var37.hasNext()) {
                  var36 = (CollideWithObstacles.CCNode)var37.next();
                  Iterator var39 = var36.visible.iterator();

                  while(var39.hasNext()) {
                     CollideWithObstacles.CCNode var41 = (CollideWithObstacles.CCNode)var39.next();
                     LineDrawer.addLine(var36.x, var36.y, (float)var36.z, var41.x, var41.y, (float)var41.z, 0.0F, 1.0F, 0.0F, (String)null, true);
                  }

                  if (var36.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec)) {
                     LineDrawer.addLine(var36.x, var36.y, (float)var36.z, var36.x + this.nodeNormal.x, var36.y + this.nodeNormal.y, (float)var36.z, 0.0F, 0.0F, 1.0F, (String)null, true);
                  }

                  if (var36.ignore) {
                     LineDrawer.addLine(var36.x - 0.05F, var36.y - 0.05F, (float)var36.z, var36.x + 0.05F, var36.y + 0.05F, (float)var36.z, 1.0F, 1.0F, 0.0F, (String)null, false);
                  }
               }
            }

            CollideWithObstacles.CCEdge var38 = null;
            var36 = null;
            double var40 = Double.MAX_VALUE;

            int var15;
            CollideWithObstacles.CCEdge var16;
            for(int var13 = 0; var13 < this.obstacles.size(); ++var13) {
               CollideWithObstacles.CCObstacle var14 = (CollideWithObstacles.CCObstacle)this.obstacles.get(var13);
               if (var14.isPointInPolygon_WindingNumber(var1.x, var1.y)) {
                  for(var15 = 0; var15 < var14.edges.size(); ++var15) {
                     var16 = (CollideWithObstacles.CCEdge)var14.edges.get(var15);
                     if (var16.node1.visible.contains(var16.node2)) {
                        CollideWithObstacles.CCNode var17 = var16.closestPoint(var1.x, var1.y, this.closest);
                        double var18 = (double)((var1.x - this.closest.x) * (var1.x - this.closest.x) + (var1.y - this.closest.y) * (var1.y - this.closest.y));
                        if (var18 < var40) {
                           var40 = var18;
                           var38 = var16;
                           var36 = var17;
                        }
                     }
                  }
               }
            }

            float var42;
            if (var38 != null) {
               var42 = var38.normal.dot(this.move);
               if (var42 >= 0.01F) {
                  var38 = null;
               }
            }

            if (var36 != null && var36.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec) && this.nodeNormal.dot(this.move) + 0.05F >= this.nodeNormal.dot(this.edgeVec)) {
               var36 = null;
               var38 = null;
            }

            if (var38 == null) {
               double var43 = Double.MAX_VALUE;
               var38 = null;
               var36 = null;

               for(var15 = 0; var15 < this.obstacles.size(); ++var15) {
                  CollideWithObstacles.CCObstacle var47 = (CollideWithObstacles.CCObstacle)this.obstacles.get(var15);

                  for(int var50 = 0; var50 < var47.edges.size(); ++var50) {
                     CollideWithObstacles.CCEdge var52 = (CollideWithObstacles.CCEdge)var47.edges.get(var50);
                     if (var52.node1.visible.contains(var52.node2)) {
                        float var19 = var52.node1.x;
                        float var20 = var52.node1.y;
                        float var21 = var52.node2.x;
                        float var22 = var52.node2.y;
                        float var23 = var19 + 0.5F * (var21 - var19);
                        float var24 = var20 + 0.5F * (var22 - var20);
                        if (var4) {
                           LineDrawer.addLine(var23, var24, (float)var52.node1.z, var23 + var52.normal.x, var24 + var52.normal.y, (float)var52.node1.z, 0.0F, 0.0F, 1.0F, (String)null, true);
                        }

                        double var25 = (double)((var22 - var20) * (var7 - var5) - (var21 - var19) * (var8 - var6));
                        if (var25 != 0.0D) {
                           double var27 = (double)((var21 - var19) * (var6 - var20) - (var22 - var20) * (var5 - var19)) / var25;
                           double var29 = (double)((var7 - var5) * (var6 - var20) - (var8 - var6) * (var5 - var19)) / var25;
                           float var31 = var52.normal.dot(this.move);
                           if (!(var31 >= 0.0F) && var27 >= 0.0D && var27 <= 1.0D && var29 >= 0.0D && var29 <= 1.0D) {
                              if (var29 < 0.01D || var29 > 0.99D) {
                                 CollideWithObstacles.CCNode var32 = var29 < 0.01D ? var52.node1 : var52.node2;
                                 if (var32.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec)) {
                                    if (!(this.nodeNormal.dot(this.move) + 0.05F >= this.nodeNormal.dot(this.edgeVec))) {
                                       var38 = var52;
                                       var36 = var32;
                                       break;
                                    }
                                    continue;
                                 }
                              }

                              float var53 = (float)((double)var5 + var27 * (double)(var7 - var5));
                              float var33 = (float)((double)var6 + var27 * (double)(var8 - var6));
                              double var34 = (double)IsoUtils.DistanceToSquared(var5, var6, var53, var33);
                              if (var34 < var43) {
                                 var43 = var34;
                                 var38 = var52;
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (var36 != null) {
               CollideWithObstacles.CCEdge var45 = var38;
               CollideWithObstacles.CCEdge var44 = null;

               for(var15 = 0; var15 < var36.edges.size(); ++var15) {
                  var16 = (CollideWithObstacles.CCEdge)var36.edges.get(var15);
                  if (var16.node1.visible.contains(var16.node2) && var16 != var38 && (var45.node1.x != var16.node1.x || var45.node1.y != var16.node1.y || var45.node2.x != var16.node2.x || var45.node2.y != var16.node2.y) && (var45.node1.x != var16.node2.x || var45.node1.y != var16.node2.y || var45.node2.x != var16.node1.x || var45.node2.y != var16.node1.y) && (!var45.hasNode(var16.node1) || !var45.hasNode(var16.node2))) {
                     var44 = var16;
                  }
               }

               if (var45 != null && var44 != null) {
                  CollideWithObstacles.CCNode var49;
                  if (var38 == var45) {
                     var49 = var36 == var44.node1 ? var44.node2 : var44.node1;
                     this.edgeVec.set(var49.x - var36.x, var49.y - var36.y);
                     this.edgeVec.normalize();
                     if (this.move.dot(this.edgeVec) >= 0.0F) {
                        var38 = var44;
                     }
                  } else if (var38 == var44) {
                     var49 = var36 == var45.node1 ? var45.node2 : var45.node1;
                     this.edgeVec.set(var49.x - var36.x, var49.y - var36.y);
                     this.edgeVec.normalize();
                     if (this.move.dot(this.edgeVec) >= 0.0F) {
                        var38 = var45;
                     }
                  }
               }
            }

            if (var38 != null) {
               var42 = var38.node1.x;
               float var46 = var38.node1.y;
               float var51 = var38.node2.x;
               float var48 = var38.node2.y;
               if (var4) {
                  LineDrawer.addLine(var42, var46, (float)var38.node1.z, var51, var48, (float)var38.node1.z, 0.0F, 1.0F, 1.0F, (String)null, true);
               }

               var38.closestPoint(var2, var3, this.closest);
               var1.nx = this.closest.x;
               var1.ny = this.closest.y;
            }

         }
      }
   }

   static class CompareIntersection implements Comparator {
      CollideWithObstacles.CCEdge edge;

      public int compare(CollideWithObstacles.CCIntersection var1, CollideWithObstacles.CCIntersection var2) {
         float var3 = this.edge == var1.edge1 ? var1.dist1 : var1.dist2;
         float var4 = this.edge == var2.edge1 ? var2.dist1 : var2.dist2;
         if (var3 < var4) {
            return -1;
         } else {
            return var3 > var4 ? 1 : 0;
         }
      }
   }

   private static class ImmutableRectF {
      private float x;
      private float y;
      private float w;
      private float h;
      static ArrayDeque pool = new ArrayDeque();

      private ImmutableRectF() {
      }

      CollideWithObstacles.ImmutableRectF init(float var1, float var2, float var3, float var4) {
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

      boolean intersects(CollideWithObstacles.ImmutableRectF var1) {
         return this.left() < var1.right() && this.right() > var1.left() && this.top() < var1.bottom() && this.bottom() > var1.top();
      }

      static CollideWithObstacles.ImmutableRectF alloc() {
         return pool.isEmpty() ? new CollideWithObstacles.ImmutableRectF() : (CollideWithObstacles.ImmutableRectF)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }

      // $FF: synthetic method
      ImmutableRectF(Object var1) {
         this();
      }
   }

   private static class CCIntersection {
      CollideWithObstacles.CCEdge edge1;
      CollideWithObstacles.CCEdge edge2;
      float dist1;
      float dist2;
      CollideWithObstacles.CCNode nodeSplit;
      static ArrayDeque pool = new ArrayDeque();

      CollideWithObstacles.CCIntersection init(CollideWithObstacles.CCEdge var1, CollideWithObstacles.CCEdge var2, float var3, float var4, float var5, float var6) {
         this.edge1 = var1;
         this.edge2 = var2;
         this.dist1 = var3;
         this.dist2 = var4;
         this.nodeSplit = CollideWithObstacles.CCNode.alloc().init(var5, var6, var1.node1.z);
         return this;
      }

      CollideWithObstacles.CCIntersection init(CollideWithObstacles.CCEdge var1, CollideWithObstacles.CCEdge var2, float var3, float var4, CollideWithObstacles.CCNode var5) {
         this.edge1 = var1;
         this.edge2 = var2;
         this.dist1 = var3;
         this.dist2 = var4;
         this.nodeSplit = var5;
         return this;
      }

      CollideWithObstacles.CCEdge split(CollideWithObstacles.CCEdge var1) {
         if (var1.hasNode(this.nodeSplit)) {
            return null;
         } else if (var1.node1.x == this.nodeSplit.x && var1.node1.y == this.nodeSplit.y) {
            return null;
         } else {
            return var1.node2.x == this.nodeSplit.x && var1.node2.y == this.nodeSplit.y ? null : var1.split(this.nodeSplit);
         }
      }

      static CollideWithObstacles.CCIntersection alloc() {
         return pool.isEmpty() ? new CollideWithObstacles.CCIntersection() : (CollideWithObstacles.CCIntersection)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class CCObjectOutline {
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

      CollideWithObstacles.CCObjectOutline init(int var1, int var2, int var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
         this.nw = this.nw_w = this.nw_n = this.nw_e = false;
         this.w_w = this.w_e = this.w_cutoff = false;
         this.n_n = this.n_s = this.n_cutoff = false;
         return this;
      }

      static void setSolid(int var0, int var1, int var2, CollideWithObstacles.CCObjectOutline[][] var3) {
         setWest(var0, var1, var2, var3);
         setNorth(var0, var1, var2, var3);
         setWest(var0 + 1, var1, var2, var3);
         setNorth(var0, var1 + 1, var2, var3);
      }

      static void setWest(int var0, int var1, int var2, CollideWithObstacles.CCObjectOutline[][] var3) {
         CollideWithObstacles.CCObjectOutline var4 = get(var0, var1, var2, var3);
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

         CollideWithObstacles.CCObjectOutline var5 = var4;
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

      static void setNorth(int var0, int var1, int var2, CollideWithObstacles.CCObjectOutline[][] var3) {
         CollideWithObstacles.CCObjectOutline var4 = get(var0, var1, var2, var3);
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

         CollideWithObstacles.CCObjectOutline var5 = var4;
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

      static CollideWithObstacles.CCObjectOutline get(int var0, int var1, int var2, CollideWithObstacles.CCObjectOutline[][] var3) {
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

      void trace_NW_N(CollideWithObstacles.CCObjectOutline[][] var1, CollideWithObstacles.CCNode var2) {
         if (var2 != null) {
            var2.setXY((float)this.x + 0.3F, (float)this.y - 0.3F);
         } else {
            CollideWithObstacles.CCNode var3 = CollideWithObstacles.CCNode.alloc().init((float)this.x + 0.3F, (float)this.y - 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.nw_n = false;
         if (this.nw_e) {
            this.trace_NW_E(var1, (CollideWithObstacles.CCNode)null);
         } else if (this.n_n) {
            this.trace_N_N(var1, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
         }

      }

      void trace_NW_S(CollideWithObstacles.CCObjectOutline[][] var1, CollideWithObstacles.CCNode var2) {
         if (var2 != null) {
            var2.setXY((float)this.x - 0.3F, (float)this.y + 0.3F);
         } else {
            CollideWithObstacles.CCNode var3 = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.nw_s = false;
         if (this.nw_w) {
            this.trace_NW_W(var1, (CollideWithObstacles.CCNode)null);
         } else {
            CollideWithObstacles.CCObjectOutline var4 = get(this.x - 1, this.y, this.z, var1);
            if (var4 == null) {
               return;
            }

            if (var4.n_s) {
               var4.nodes = this.nodes;
               var4.trace_N_S(var1, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
            }
         }

      }

      void trace_NW_W(CollideWithObstacles.CCObjectOutline[][] var1, CollideWithObstacles.CCNode var2) {
         if (var2 != null) {
            var2.setXY((float)this.x - 0.3F, (float)this.y - 0.3F);
         } else {
            CollideWithObstacles.CCNode var3 = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)this.y - 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.nw_w = false;
         if (this.nw_n) {
            this.trace_NW_N(var1, (CollideWithObstacles.CCNode)null);
         } else {
            CollideWithObstacles.CCObjectOutline var4 = get(this.x, this.y - 1, this.z, var1);
            if (var4 == null) {
               return;
            }

            if (var4.w_w) {
               var4.nodes = this.nodes;
               var4.trace_W_W(var1, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
            }
         }

      }

      void trace_NW_E(CollideWithObstacles.CCObjectOutline[][] var1, CollideWithObstacles.CCNode var2) {
         if (var2 != null) {
            var2.setXY((float)this.x + 0.3F, (float)this.y + 0.3F);
         } else {
            CollideWithObstacles.CCNode var3 = CollideWithObstacles.CCNode.alloc().init((float)this.x + 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.nw_e = false;
         if (this.nw_s) {
            this.trace_NW_S(var1, (CollideWithObstacles.CCNode)null);
         } else if (this.w_e) {
            this.trace_W_E(var1, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
         }

      }

      void trace_W_E(CollideWithObstacles.CCObjectOutline[][] var1, CollideWithObstacles.CCNode var2) {
         CollideWithObstacles.CCNode var3;
         if (var2 != null) {
            var2.setXY((float)this.x + 0.3F, (float)(this.y + 1) - 0.3F);
         } else {
            var3 = CollideWithObstacles.CCNode.alloc().init((float)this.x + 0.3F, (float)(this.y + 1) - 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.w_e = false;
         if (this.w_cutoff) {
            var3 = (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1);
            var3.setXY((float)this.x + 0.3F, (float)(this.y + 1) + 0.3F);
            var3 = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)(this.y + 1) + 0.3F, this.z);
            this.nodes.add(var3);
            var3 = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)(this.y + 1) - 0.3F, this.z);
            this.nodes.add(var3);
            this.trace_W_W(var1, var3);
         } else {
            CollideWithObstacles.CCObjectOutline var4 = get(this.x, this.y + 1, this.z, var1);
            if (var4 != null) {
               if (var4.nw && var4.nw_e) {
                  var4.nodes = this.nodes;
                  var4.trace_NW_E(var1, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
               } else if (var4.n_n) {
                  var4.nodes = this.nodes;
                  var4.trace_N_N(var1, (CollideWithObstacles.CCNode)null);
               }

            }
         }
      }

      void trace_W_W(CollideWithObstacles.CCObjectOutline[][] var1, CollideWithObstacles.CCNode var2) {
         if (var2 != null) {
            var2.setXY((float)this.x - 0.3F, (float)this.y + 0.3F);
         } else {
            CollideWithObstacles.CCNode var3 = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.w_w = false;
         if (this.nw_w) {
            this.trace_NW_W(var1, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
         } else {
            CollideWithObstacles.CCObjectOutline var4 = get(this.x - 1, this.y, this.z, var1);
            if (var4 == null) {
               return;
            }

            if (var4.n_s) {
               var4.nodes = this.nodes;
               var4.trace_N_S(var1, (CollideWithObstacles.CCNode)null);
            }
         }

      }

      void trace_N_N(CollideWithObstacles.CCObjectOutline[][] var1, CollideWithObstacles.CCNode var2) {
         CollideWithObstacles.CCNode var3;
         if (var2 != null) {
            var2.setXY((float)(this.x + 1) - 0.3F, (float)this.y - 0.3F);
         } else {
            var3 = CollideWithObstacles.CCNode.alloc().init((float)(this.x + 1) - 0.3F, (float)this.y - 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.n_n = false;
         if (this.n_cutoff) {
            var3 = (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1);
            var3.setXY((float)(this.x + 1) + 0.3F, (float)this.y - 0.3F);
            var3 = CollideWithObstacles.CCNode.alloc().init((float)(this.x + 1) + 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
            var3 = CollideWithObstacles.CCNode.alloc().init((float)(this.x + 1) - 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
            this.trace_N_S(var1, var3);
         } else {
            CollideWithObstacles.CCObjectOutline var4 = get(this.x + 1, this.y, this.z, var1);
            if (var4 != null) {
               if (var4.nw_n) {
                  var4.nodes = this.nodes;
                  var4.trace_NW_N(var1, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
               } else {
                  var4 = get(this.x + 1, this.y - 1, this.z, var1);
                  if (var4 == null) {
                     return;
                  }

                  if (var4.w_w) {
                     var4.nodes = this.nodes;
                     var4.trace_W_W(var1, (CollideWithObstacles.CCNode)null);
                  }
               }

            }
         }
      }

      void trace_N_S(CollideWithObstacles.CCObjectOutline[][] var1, CollideWithObstacles.CCNode var2) {
         if (var2 != null) {
            var2.setXY((float)this.x + 0.3F, (float)this.y + 0.3F);
         } else {
            CollideWithObstacles.CCNode var3 = CollideWithObstacles.CCNode.alloc().init((float)this.x + 0.3F, (float)this.y + 0.3F, this.z);
            this.nodes.add(var3);
         }

         this.n_s = false;
         if (this.nw_s) {
            this.trace_NW_S(var1, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
         } else if (this.w_e) {
            this.trace_W_E(var1, (CollideWithObstacles.CCNode)null);
         }

      }

      void trace(CollideWithObstacles.CCObjectOutline[][] var1, ArrayList var2) {
         var2.clear();
         this.nodes = var2;
         CollideWithObstacles.CCNode var3 = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)this.y - 0.3F, this.z);
         var2.add(var3);
         this.trace_NW_N(var1, (CollideWithObstacles.CCNode)null);
         if (var2.size() != 2 && var3.x == ((CollideWithObstacles.CCNode)var2.get(var2.size() - 1)).x && var3.y == ((CollideWithObstacles.CCNode)var2.get(var2.size() - 1)).y) {
            ((CollideWithObstacles.CCNode)var2.get(var2.size() - 1)).release();
            var2.set(var2.size() - 1, var3);
         } else {
            var2.clear();
         }

      }

      static CollideWithObstacles.CCObjectOutline alloc() {
         return pool.isEmpty() ? new CollideWithObstacles.CCObjectOutline() : (CollideWithObstacles.CCObjectOutline)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class CCObstacle {
      ArrayList edges = new ArrayList();
      CollideWithObstacles.ImmutableRectF bounds;
      static Vector3f tempVector3f_1 = new Vector3f();
      static ArrayDeque pool = new ArrayDeque();

      CollideWithObstacles.CCObstacle init() {
         this.edges.clear();
         return this;
      }

      boolean hasNode(CollideWithObstacles.CCNode var1) {
         for(int var2 = 0; var2 < this.edges.size(); ++var2) {
            CollideWithObstacles.CCEdge var3 = (CollideWithObstacles.CCEdge)this.edges.get(var2);
            if (var3.hasNode(var1)) {
               return true;
            }
         }

         return false;
      }

      boolean hasAdjacentNodes(CollideWithObstacles.CCNode var1, CollideWithObstacles.CCNode var2) {
         for(int var3 = 0; var3 < this.edges.size(); ++var3) {
            CollideWithObstacles.CCEdge var4 = (CollideWithObstacles.CCEdge)this.edges.get(var3);
            if (var4.hasNode(var1) && var4.hasNode(var2)) {
               return true;
            }
         }

         return false;
      }

      boolean isPointInPolygon_CrossingNumber(float var1, float var2) {
         int var3 = 0;

         for(int var4 = 0; var4 < this.edges.size(); ++var4) {
            CollideWithObstacles.CCEdge var5 = (CollideWithObstacles.CCEdge)this.edges.get(var4);
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
            CollideWithObstacles.CCEdge var5 = (CollideWithObstacles.CCEdge)this.edges.get(var4);
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

      boolean isNodeInsideOf(CollideWithObstacles.CCNode var1) {
         if (this.hasNode(var1)) {
            return false;
         } else {
            return !this.bounds.containsPoint(var1.x, var1.y) ? false : this.isPointInPolygon_WindingNumber(var1.x, var1.y);
         }
      }

      CollideWithObstacles.CCNode getClosestPointOnEdge(float var1, float var2, Vector2 var3) {
         double var4 = Double.MAX_VALUE;
         CollideWithObstacles.CCNode var6 = null;
         float var7 = Float.MAX_VALUE;
         float var8 = Float.MAX_VALUE;

         for(int var9 = 0; var9 < this.edges.size(); ++var9) {
            CollideWithObstacles.CCEdge var10 = (CollideWithObstacles.CCEdge)this.edges.get(var9);
            if (var10.node1.visible.contains(var10.node2)) {
               CollideWithObstacles.CCNode var11 = var10.closestPoint(var1, var2, var3);
               double var12 = (double)((var1 - var3.x) * (var1 - var3.x) + (var2 - var3.y) * (var2 - var3.y));
               if (var12 < var4) {
                  var7 = var3.x;
                  var8 = var3.y;
                  var6 = var11;
                  var4 = var12;
               }
            }
         }

         var3.set(var7, var8);
         return var6;
      }

      void calcBounds() {
         float var1 = Float.MAX_VALUE;
         float var2 = Float.MAX_VALUE;
         float var3 = Float.MIN_VALUE;
         float var4 = Float.MIN_VALUE;

         for(int var5 = 0; var5 < this.edges.size(); ++var5) {
            CollideWithObstacles.CCEdge var6 = (CollideWithObstacles.CCEdge)this.edges.get(var5);
            var1 = Math.min(var1, var6.node1.x);
            var2 = Math.min(var2, var6.node1.y);
            var3 = Math.max(var3, var6.node1.x);
            var4 = Math.max(var4, var6.node1.y);
         }

         if (this.bounds != null) {
            this.bounds.release();
         }

         float var7 = 0.01F;
         this.bounds = CollideWithObstacles.ImmutableRectF.alloc().init(var1 - var7, var2 - var7, var3 - var1 + var7 * 2.0F, var4 - var2 + var7 * 2.0F);
      }

      static CollideWithObstacles.CCObstacle alloc() {
         return pool.isEmpty() ? new CollideWithObstacles.CCObstacle() : (CollideWithObstacles.CCObstacle)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class CCEdge {
      CollideWithObstacles.CCNode node1;
      CollideWithObstacles.CCNode node2;
      CollideWithObstacles.CCObstacle obstacle;
      ArrayList intersections = new ArrayList();
      Vector2 normal = new Vector2();
      static ArrayDeque pool = new ArrayDeque();

      CollideWithObstacles.CCEdge init(CollideWithObstacles.CCNode var1, CollideWithObstacles.CCNode var2, CollideWithObstacles.CCObstacle var3) {
         if (var1.x == var2.x && var1.y == var2.y) {
            boolean var4 = false;
         }

         this.node1 = var1;
         this.node2 = var2;
         var1.edges.add(this);
         var2.edges.add(this);
         this.obstacle = var3;
         this.intersections.clear();
         this.normal.set(var2.x - var1.x, var2.y - var1.y);
         this.normal.normalize();
         this.normal.rotate((float)Math.toRadians(90.0D));
         return this;
      }

      boolean hasNode(CollideWithObstacles.CCNode var1) {
         return var1 == this.node1 || var1 == this.node2;
      }

      CollideWithObstacles.CCEdge split(CollideWithObstacles.CCNode var1) {
         CollideWithObstacles.CCEdge var2 = alloc().init(var1, this.node2, this.obstacle);
         this.obstacle.edges.add(this.obstacle.edges.indexOf(this) + 1, var2);
         this.node2.edges.remove(this);
         this.node2 = var1;
         this.node2.edges.add(this);
         return var2;
      }

      CollideWithObstacles.CCNode closestPoint(float var1, float var2, Vector2 var3) {
         float var4 = this.node1.x;
         float var5 = this.node1.y;
         float var6 = this.node2.x;
         float var7 = this.node2.y;
         double var8 = (double)((var1 - var4) * (var6 - var4) + (var2 - var5) * (var7 - var5)) / (Math.pow((double)(var6 - var4), 2.0D) + Math.pow((double)(var7 - var5), 2.0D));
         double var10 = 0.001D;
         if (var8 <= 0.0D + var10) {
            var3.set(var4, var5);
            return this.node1;
         } else if (var8 >= 1.0D - var10) {
            var3.set(var6, var7);
            return this.node2;
         } else {
            double var12 = (double)var4 + var8 * (double)(var6 - var4);
            double var14 = (double)var5 + var8 * (double)(var7 - var5);
            var3.set((float)var12, (float)var14);
            return null;
         }
      }

      static CollideWithObstacles.CCEdge alloc() {
         return pool.isEmpty() ? new CollideWithObstacles.CCEdge() : (CollideWithObstacles.CCEdge)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }

   private static class CCNode {
      float x;
      float y;
      int z;
      boolean ignore;
      ArrayList edges = new ArrayList();
      ArrayList visible = new ArrayList();
      static ArrayList tempObstacles = new ArrayList();
      static ArrayDeque pool = new ArrayDeque();

      CollideWithObstacles.CCNode init(float var1, float var2, int var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
         this.ignore = false;
         this.edges.clear();
         this.visible.clear();
         return this;
      }

      CollideWithObstacles.CCNode setXY(float var1, float var2) {
         this.x = var1;
         this.y = var2;
         return this;
      }

      boolean sharesEdge(CollideWithObstacles.CCNode var1) {
         for(int var2 = 0; var2 < this.edges.size(); ++var2) {
            CollideWithObstacles.CCEdge var3 = (CollideWithObstacles.CCEdge)this.edges.get(var2);
            if (var3.hasNode(var1)) {
               return true;
            }
         }

         return false;
      }

      boolean sharesShape(CollideWithObstacles.CCNode var1) {
         for(int var2 = 0; var2 < this.edges.size(); ++var2) {
            CollideWithObstacles.CCEdge var3 = (CollideWithObstacles.CCEdge)this.edges.get(var2);

            for(int var4 = 0; var4 < var1.edges.size(); ++var4) {
               CollideWithObstacles.CCEdge var5 = (CollideWithObstacles.CCEdge)var1.edges.get(var4);
               if (var3.obstacle != null && var3.obstacle == var5.obstacle) {
                  return true;
               }
            }
         }

         return false;
      }

      void getObstacles(ArrayList var1) {
         for(int var2 = 0; var2 < this.edges.size(); ++var2) {
            CollideWithObstacles.CCEdge var3 = (CollideWithObstacles.CCEdge)this.edges.get(var2);
            if (!var1.contains(var3.obstacle)) {
               var1.add(var3.obstacle);
            }
         }

      }

      boolean onSameShapeButDoesNotShareAnEdge(CollideWithObstacles.CCNode var1) {
         tempObstacles.clear();
         this.getObstacles(tempObstacles);

         for(int var2 = 0; var2 < tempObstacles.size(); ++var2) {
            CollideWithObstacles.CCObstacle var3 = (CollideWithObstacles.CCObstacle)tempObstacles.get(var2);
            if (var3.hasNode(var1) && !var3.hasAdjacentNodes(this, var1)) {
               return true;
            }
         }

         return false;
      }

      boolean getNormalAndEdgeVectors(Vector2 var1, Vector2 var2) {
         CollideWithObstacles.CCEdge var3 = null;
         CollideWithObstacles.CCEdge var4 = null;

         for(int var5 = 0; var5 < this.edges.size(); ++var5) {
            CollideWithObstacles.CCEdge var6 = (CollideWithObstacles.CCEdge)this.edges.get(var5);
            if (var6.node1.visible.contains(var6.node2)) {
               if (var3 == null) {
                  var3 = var6;
               } else if (!var3.hasNode(var6.node1) || !var3.hasNode(var6.node2)) {
                  var4 = var6;
               }
            }
         }

         if (var3 != null && var4 != null) {
            float var7 = var3.normal.x + var4.normal.x;
            float var8 = var3.normal.y + var4.normal.y;
            var1.set(var7, var8);
            var1.normalize();
            if (var3.node1 == this) {
               var2.set(var3.node2.x - var3.node1.x, var3.node2.y - var3.node1.y);
            } else {
               var2.set(var3.node1.x - var3.node2.x, var3.node1.y - var3.node2.y);
            }

            var2.normalize();
            return true;
         } else {
            return false;
         }
      }

      static CollideWithObstacles.CCNode alloc() {
         boolean var0;
         if (pool.isEmpty()) {
            var0 = false;
         } else {
            var0 = false;
         }

         return pool.isEmpty() ? new CollideWithObstacles.CCNode() : (CollideWithObstacles.CCNode)pool.pop();
      }

      void release() {
         assert !pool.contains(this);

         pool.push(this);
      }
   }
}
