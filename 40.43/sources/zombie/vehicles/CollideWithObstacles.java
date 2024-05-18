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

	void getVehiclesInRect(float float1, float float2, float float3, float float4, int int1) {
		this.vehicles.clear();
		int int2 = (int)(float1 / 10.0F);
		int int3 = (int)(float2 / 10.0F);
		int int4 = (int)Math.ceil((double)(float3 / 10.0F));
		int int5 = (int)Math.ceil((double)(float4 / 10.0F));
		for (int int6 = int3; int6 < int5; ++int6) {
			for (int int7 = int2; int7 < int4; ++int7) {
				IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int7, int6) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int7 * 10, int6 * 10, 0);
				if (chunk != null) {
					for (int int8 = 0; int8 < chunk.vehicles.size(); ++int8) {
						BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int8);
						if (baseVehicle.getScript() != null && (int)baseVehicle.z == int1) {
							this.vehicles.add(baseVehicle);
						}
					}
				}
			}
		}
	}

	void getObstaclesInRect(float float1, float float2, float float3, float float4, int int1, int int2, int int3) {
		this.nodes.clear();
		this.obstacles.clear();
		this.moveBounds.init(float1 - 1.0F, float2 - 1.0F, float3 - float1 + 2.0F, float4 - float2 + 2.0F);
		this.getVehiclesInRect(float1 - 1.0F - 4.0F, float2 - 1.0F - 4.0F, float3 + 2.0F + 8.0F, float4 + 2.0F + 8.0F, int3);
		int int4;
		CollideWithObstacles.CCNode cCNode;
		CollideWithObstacles.CCNode cCNode2;
		CollideWithObstacles.CCNode cCNode3;
		for (int4 = 0; int4 < this.vehicles.size(); ++int4) {
			BaseVehicle baseVehicle = (BaseVehicle)this.vehicles.get(int4);
			PolygonalMap2.VehiclePoly vehiclePoly = baseVehicle.getPolyPlusRadius();
			float float5 = Math.min(vehiclePoly.x1, Math.min(vehiclePoly.x2, Math.min(vehiclePoly.x3, vehiclePoly.x4)));
			float float6 = Math.min(vehiclePoly.y1, Math.min(vehiclePoly.y2, Math.min(vehiclePoly.y3, vehiclePoly.y4)));
			float float7 = Math.max(vehiclePoly.x1, Math.max(vehiclePoly.x2, Math.max(vehiclePoly.x3, vehiclePoly.x4)));
			float float8 = Math.max(vehiclePoly.y1, Math.max(vehiclePoly.y2, Math.max(vehiclePoly.y3, vehiclePoly.y4)));
			this.vehicleBounds.init(float5, float6, float7 - float5, float8 - float6);
			if (this.moveBounds.intersects(this.vehicleBounds)) {
				int int5 = (int)vehiclePoly.z;
				cCNode = CollideWithObstacles.CCNode.alloc().init(vehiclePoly.x1, vehiclePoly.y1, int5);
				CollideWithObstacles.CCNode cCNode4 = CollideWithObstacles.CCNode.alloc().init(vehiclePoly.x2, vehiclePoly.y2, int5);
				cCNode2 = CollideWithObstacles.CCNode.alloc().init(vehiclePoly.x3, vehiclePoly.y3, int5);
				cCNode3 = CollideWithObstacles.CCNode.alloc().init(vehiclePoly.x4, vehiclePoly.y4, int5);
				CollideWithObstacles.CCObstacle cCObstacle = CollideWithObstacles.CCObstacle.alloc().init();
				CollideWithObstacles.CCEdge cCEdge = CollideWithObstacles.CCEdge.alloc().init(cCNode, cCNode4, cCObstacle);
				CollideWithObstacles.CCEdge cCEdge2 = CollideWithObstacles.CCEdge.alloc().init(cCNode4, cCNode2, cCObstacle);
				CollideWithObstacles.CCEdge cCEdge3 = CollideWithObstacles.CCEdge.alloc().init(cCNode2, cCNode3, cCObstacle);
				CollideWithObstacles.CCEdge cCEdge4 = CollideWithObstacles.CCEdge.alloc().init(cCNode3, cCNode, cCObstacle);
				cCObstacle.edges.add(cCEdge);
				cCObstacle.edges.add(cCEdge2);
				cCObstacle.edges.add(cCEdge3);
				cCObstacle.edges.add(cCEdge4);
				cCObstacle.calcBounds();
				this.obstacles.add(cCObstacle);
				this.nodes.add(cCNode);
				this.nodes.add(cCNode4);
				this.nodes.add(cCNode2);
				this.nodes.add(cCNode3);
			}
		}

		if (!this.obstacles.isEmpty()) {
			int4 = int1 - 2;
			int int6 = int2 - 2;
			int int7 = int1 + 2 + 1;
			int int8 = int2 + 2 + 1;
			int int9;
			int int10;
			for (int9 = int6; int9 < int8; ++int9) {
				for (int10 = int4; int10 < int7; ++int10) {
					CollideWithObstacles.CCObjectOutline.get(int10 - int4, int9 - int6, int3, this.oo).init(int10 - int4, int9 - int6, int3);
				}
			}

			for (int9 = int6; int9 < int8 - 1; ++int9) {
				for (int10 = int4; int10 < int7 - 1; ++int10) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int10, int9, int3);
					if (square != null) {
						if (square.isSolid() || square.isSolidTrans() && !square.isAdjacentToWindow() || square.Has(IsoObjectType.stairsMN) || square.Has(IsoObjectType.stairsTN) || square.Has(IsoObjectType.stairsMW) || square.Has(IsoObjectType.stairsTW)) {
							CollideWithObstacles.CCObjectOutline.setSolid(int10 - int4, int9 - int6, int3, this.oo);
						}

						if (square.Is(IsoFlagType.collideW) || square.hasBlockedDoor(false) || square.Has(IsoObjectType.stairsBN)) {
							CollideWithObstacles.CCObjectOutline.setWest(int10 - int4, int9 - int6, int3, this.oo);
						}

						if (square.Is(IsoFlagType.collideN) || square.hasBlockedDoor(true) || square.Has(IsoObjectType.stairsBW)) {
							CollideWithObstacles.CCObjectOutline.setNorth(int10 - int4, int9 - int6, int3, this.oo);
						}

						if (square.Has(IsoObjectType.stairsBN) && int10 != int7 - 2) {
							square = IsoWorld.instance.CurrentCell.getGridSquare(int10 + 1, int9, int3);
							if (square != null) {
								CollideWithObstacles.CCObjectOutline.setWest(int10 + 1 - int4, int9 - int6, int3, this.oo);
							}
						}

						if (square.Has(IsoObjectType.stairsBW) && int9 != int8 - 2) {
							square = IsoWorld.instance.CurrentCell.getGridSquare(int10, int9 + 1, int3);
							if (square != null) {
								CollideWithObstacles.CCObjectOutline.setNorth(int10 - int4, int9 + 1 - int6, int3, this.oo);
							}
						}
					}
				}
			}

			for (int9 = 0; int9 < int8 - int6; ++int9) {
				for (int10 = 0; int10 < int7 - int4; ++int10) {
					CollideWithObstacles.CCObjectOutline cCObjectOutline = CollideWithObstacles.CCObjectOutline.get(int10, int9, int3, this.oo);
					if (cCObjectOutline != null && cCObjectOutline.nw && cCObjectOutline.nw_w && cCObjectOutline.nw_n) {
						cCObjectOutline.trace(this.oo, this.obstacleTraceNodes);
						if (!cCObjectOutline.nodes.isEmpty()) {
							CollideWithObstacles.CCObstacle cCObstacle2 = CollideWithObstacles.CCObstacle.alloc().init();
							cCNode = (CollideWithObstacles.CCNode)cCObjectOutline.nodes.get(cCObjectOutline.nodes.size() - 1);
							for (int int11 = cCObjectOutline.nodes.size() - 1; int11 > 0; --int11) {
								cCNode2 = (CollideWithObstacles.CCNode)cCObjectOutline.nodes.get(int11);
								cCNode3 = (CollideWithObstacles.CCNode)cCObjectOutline.nodes.get(int11 - 1);
								cCNode2.x += (float)int4;
								cCNode2.y += (float)int6;
								CollideWithObstacles.CCEdge cCEdge5 = CollideWithObstacles.CCEdge.alloc().init(cCNode2, cCNode3, cCObstacle2);
								float float9 = cCNode3.x + (cCNode3 != cCNode ? (float)int4 : 0.0F);
								float float10 = cCNode3.y + (cCNode3 != cCNode ? (float)int6 : 0.0F);
								cCEdge5.normal.set(float9 - cCNode2.x, float10 - cCNode2.y);
								cCEdge5.normal.normalize();
								cCEdge5.normal.rotate((float)Math.toRadians(90.0));
								cCObstacle2.edges.add(cCEdge5);
								this.nodes.add(cCNode2);
							}

							cCObstacle2.calcBounds();
							this.obstacles.add(cCObstacle2);
						}
					}
				}
			}
		}
	}

	void checkEdgeIntersection() {
		boolean boolean1 = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRender.getValue();
		int int1;
		CollideWithObstacles.CCObstacle cCObstacle;
		int int2;
		int int3;
		for (int1 = 0; int1 < this.obstacles.size(); ++int1) {
			cCObstacle = (CollideWithObstacles.CCObstacle)this.obstacles.get(int1);
			for (int2 = int1 + 1; int2 < this.obstacles.size(); ++int2) {
				CollideWithObstacles.CCObstacle cCObstacle2 = (CollideWithObstacles.CCObstacle)this.obstacles.get(int2);
				if (cCObstacle.bounds.intersects(cCObstacle2.bounds)) {
					for (int3 = 0; int3 < cCObstacle.edges.size(); ++int3) {
						CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)cCObstacle.edges.get(int3);
						for (int int4 = 0; int4 < cCObstacle2.edges.size(); ++int4) {
							CollideWithObstacles.CCEdge cCEdge2 = (CollideWithObstacles.CCEdge)cCObstacle2.edges.get(int4);
							CollideWithObstacles.CCIntersection cCIntersection = this.getIntersection(cCEdge, cCEdge2);
							if (cCIntersection != null) {
								cCEdge.intersections.add(cCIntersection);
								cCEdge2.intersections.add(cCIntersection);
								if (boolean1) {
									LineDrawer.addLine(cCIntersection.nodeSplit.x - 0.1F, cCIntersection.nodeSplit.y - 0.1F, (float)cCEdge.node1.z, cCIntersection.nodeSplit.x + 0.1F, cCIntersection.nodeSplit.y + 0.1F, (float)cCEdge.node1.z, 1.0F, 0.0F, 0.0F, (String)null, false);
								}

								if (!cCEdge.hasNode(cCIntersection.nodeSplit) && !cCEdge2.hasNode(cCIntersection.nodeSplit)) {
									this.nodes.add(cCIntersection.nodeSplit);
								}

								this.intersections.add(cCIntersection);
							}
						}
					}
				}
			}
		}

		for (int1 = 0; int1 < this.obstacles.size(); ++int1) {
			cCObstacle = (CollideWithObstacles.CCObstacle)this.obstacles.get(int1);
			for (int2 = cCObstacle.edges.size() - 1; int2 >= 0; --int2) {
				CollideWithObstacles.CCEdge cCEdge3 = (CollideWithObstacles.CCEdge)cCObstacle.edges.get(int2);
				if (!cCEdge3.intersections.isEmpty()) {
					this.comparator.edge = cCEdge3;
					Collections.sort(cCEdge3.intersections, this.comparator);
					for (int3 = cCEdge3.intersections.size() - 1; int3 >= 0; --int3) {
						CollideWithObstacles.CCIntersection cCIntersection2 = (CollideWithObstacles.CCIntersection)cCEdge3.intersections.get(int3);
						cCIntersection2.split(cCEdge3);
					}
				}
			}
		}
	}

	boolean collinear(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = (float3 - float1) * (float6 - float2) - (float5 - float1) * (float4 - float2);
		return float7 >= -0.05F && float7 < 0.05F;
	}

	boolean within(float float1, float float2, float float3) {
		return float1 <= float2 && float2 <= float3 || float3 <= float2 && float2 <= float1;
	}

	boolean is_on(float float1, float float2, float float3, float float4, float float5, float float6) {
		boolean boolean1;
		label25: {
			if (this.collinear(float1, float2, float3, float4, float5, float6)) {
				if (float1 != float3) {
					if (this.within(float1, float5, float3)) {
						break label25;
					}
				} else if (this.within(float2, float6, float4)) {
					break label25;
				}
			}

			boolean1 = false;
			return boolean1;
		}
		boolean1 = true;
		return boolean1;
	}

	public CollideWithObstacles.CCIntersection getIntersection(CollideWithObstacles.CCEdge cCEdge, CollideWithObstacles.CCEdge cCEdge2) {
		float float1 = cCEdge.node1.x;
		float float2 = cCEdge.node1.y;
		float float3 = cCEdge.node2.x;
		float float4 = cCEdge.node2.y;
		float float5 = cCEdge2.node1.x;
		float float6 = cCEdge2.node1.y;
		float float7 = cCEdge2.node2.x;
		float float8 = cCEdge2.node2.y;
		double double1 = (double)((float8 - float6) * (float3 - float1) - (float7 - float5) * (float4 - float2));
		if (double1 > -0.01 && double1 < 0.01) {
			return null;
		} else {
			double double2 = (double)((float7 - float5) * (float2 - float6) - (float8 - float6) * (float1 - float5)) / double1;
			double double3 = (double)((float3 - float1) * (float2 - float6) - (float4 - float2) * (float1 - float5)) / double1;
			if (double2 >= 0.0 && double2 <= 1.0 && double3 >= 0.0 && double3 <= 1.0) {
				float float9 = (float)((double)float1 + double2 * (double)(float3 - float1));
				float float10 = (float)((double)float2 + double2 * (double)(float4 - float2));
				CollideWithObstacles.CCNode cCNode = null;
				CollideWithObstacles.CCNode cCNode2 = null;
				if (double2 < 0.009999999776482582) {
					cCNode = cCEdge.node1;
				} else if (double2 > 0.9900000095367432) {
					cCNode = cCEdge.node2;
				}

				if (double3 < 0.009999999776482582) {
					cCNode2 = cCEdge2.node1;
				} else if (double3 > 0.9900000095367432) {
					cCNode2 = cCEdge2.node2;
				}

				if (cCNode != null && cCNode2 != null) {
					CollideWithObstacles.CCIntersection cCIntersection = CollideWithObstacles.CCIntersection.alloc().init(cCEdge, cCEdge2, (float)double2, (float)double3, cCNode);
					cCEdge.intersections.add(cCIntersection);
					this.intersections.add(cCIntersection);
					cCIntersection = CollideWithObstacles.CCIntersection.alloc().init(cCEdge, cCEdge2, (float)double2, (float)double3, cCNode2);
					cCEdge2.intersections.add(cCIntersection);
					this.intersections.add(cCIntersection);
					LineDrawer.addLine(cCIntersection.nodeSplit.x - 0.1F, cCIntersection.nodeSplit.y - 0.1F, (float)cCEdge.node1.z, cCIntersection.nodeSplit.x + 0.1F, cCIntersection.nodeSplit.y + 0.1F, (float)cCEdge.node1.z, 1.0F, 0.0F, 0.0F, (String)null, false);
					return null;
				} else {
					return cCNode == null && cCNode2 == null ? CollideWithObstacles.CCIntersection.alloc().init(cCEdge, cCEdge2, (float)double2, (float)double3, float9, float10) : CollideWithObstacles.CCIntersection.alloc().init(cCEdge, cCEdge2, (float)double2, (float)double3, cCNode == null ? cCNode2 : cCNode);
				}
			} else {
				return null;
			}
		}
	}

	void checkNodesInObstacles() {
		for (int int1 = 0; int1 < this.nodes.size(); ++int1) {
			CollideWithObstacles.CCNode cCNode = (CollideWithObstacles.CCNode)this.nodes.get(int1);
			for (int int2 = 0; int2 < this.obstacles.size(); ++int2) {
				CollideWithObstacles.CCObstacle cCObstacle = (CollideWithObstacles.CCObstacle)this.obstacles.get(int2);
				boolean boolean1 = false;
				for (int int3 = 0; int3 < this.intersections.size(); ++int3) {
					CollideWithObstacles.CCIntersection cCIntersection = (CollideWithObstacles.CCIntersection)this.intersections.get(int3);
					if (cCIntersection.nodeSplit == cCNode) {
						if (cCIntersection.edge1.obstacle == cCObstacle || cCIntersection.edge2.obstacle == cCObstacle) {
							boolean1 = true;
						}

						break;
					}
				}

				if (!boolean1 && cCObstacle.isNodeInsideOf(cCNode)) {
					cCNode.ignore = true;
					break;
				}
			}
		}
	}

	boolean isVisible(CollideWithObstacles.CCNode cCNode, CollideWithObstacles.CCNode cCNode2) {
		if (cCNode.sharesEdge(cCNode2)) {
			return !cCNode.onSameShapeButDoesNotShareAnEdge(cCNode2);
		} else {
			return !cCNode.sharesShape(cCNode2);
		}
	}

	void calculateNodeVisibility() {
		for (int int1 = 0; int1 < this.obstacles.size(); ++int1) {
			CollideWithObstacles.CCObstacle cCObstacle = (CollideWithObstacles.CCObstacle)this.obstacles.get(int1);
			for (int int2 = 0; int2 < cCObstacle.edges.size(); ++int2) {
				CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)cCObstacle.edges.get(int2);
				if (!cCEdge.node1.ignore && !cCEdge.node2.ignore && this.isVisible(cCEdge.node1, cCEdge.node2)) {
					cCEdge.node1.visible.add(cCEdge.node2);
					cCEdge.node2.visible.add(cCEdge.node1);
				}
			}
		}
	}

	void resolveCollision(IsoGameCharacter gameCharacter, float float1, float float2) {
		if (gameCharacter.getCurrentSquare() == null || !gameCharacter.getCurrentSquare().HasStairs()) {
			boolean boolean1 = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRender.getValue();
			float float3 = gameCharacter.x;
			float float4 = gameCharacter.y;
			float float5 = float1;
			float float6 = float2;
			if (boolean1) {
				LineDrawer.addLine(float3, float4, (float)((int)gameCharacter.z), float1, float2, (float)((int)gameCharacter.z), 1.0F, 1.0F, 1.0F, (String)null, true);
			}

			if (float3 != float1 || float4 != float2) {
				this.move.set(float1 - gameCharacter.x, float2 - gameCharacter.y);
				this.move.normalize();
				int int1;
				for (int1 = 0; int1 < this.nodes.size(); ++int1) {
					((CollideWithObstacles.CCNode)this.nodes.get(int1)).release();
				}

				for (int1 = 0; int1 < this.obstacles.size(); ++int1) {
					CollideWithObstacles.CCObstacle cCObstacle = (CollideWithObstacles.CCObstacle)this.obstacles.get(int1);
					for (int int2 = 0; int2 < cCObstacle.edges.size(); ++int2) {
						CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)cCObstacle.edges.get(int2);
						((CollideWithObstacles.CCEdge)cCObstacle.edges.get(int2)).release();
					}

					cCObstacle.release();
				}

				for (int1 = 0; int1 < this.intersections.size(); ++int1) {
					((CollideWithObstacles.CCIntersection)this.intersections.get(int1)).release();
				}

				this.intersections.clear();
				this.getObstaclesInRect(Math.min(float3, float1), Math.min(float4, float2), Math.max(float3, float1), Math.max(float4, float2), (int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z);
				this.checkEdgeIntersection();
				this.checkNodesInObstacles();
				this.calculateNodeVisibility();
				CollideWithObstacles.CCNode cCNode;
				if (boolean1) {
					Iterator iterator = this.nodes.iterator();
					while (iterator.hasNext()) {
						cCNode = (CollideWithObstacles.CCNode)iterator.next();
						Iterator iterator2 = cCNode.visible.iterator();
						while (iterator2.hasNext()) {
							CollideWithObstacles.CCNode cCNode2 = (CollideWithObstacles.CCNode)iterator2.next();
							LineDrawer.addLine(cCNode.x, cCNode.y, (float)cCNode.z, cCNode2.x, cCNode2.y, (float)cCNode2.z, 0.0F, 1.0F, 0.0F, (String)null, true);
						}

						if (cCNode.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec)) {
							LineDrawer.addLine(cCNode.x, cCNode.y, (float)cCNode.z, cCNode.x + this.nodeNormal.x, cCNode.y + this.nodeNormal.y, (float)cCNode.z, 0.0F, 0.0F, 1.0F, (String)null, true);
						}

						if (cCNode.ignore) {
							LineDrawer.addLine(cCNode.x - 0.05F, cCNode.y - 0.05F, (float)cCNode.z, cCNode.x + 0.05F, cCNode.y + 0.05F, (float)cCNode.z, 1.0F, 1.0F, 0.0F, (String)null, false);
						}
					}
				}

				CollideWithObstacles.CCEdge cCEdge2 = null;
				cCNode = null;
				double double1 = Double.MAX_VALUE;
				int int3;
				CollideWithObstacles.CCEdge cCEdge3;
				for (int int4 = 0; int4 < this.obstacles.size(); ++int4) {
					CollideWithObstacles.CCObstacle cCObstacle2 = (CollideWithObstacles.CCObstacle)this.obstacles.get(int4);
					if (cCObstacle2.isPointInPolygon_WindingNumber(gameCharacter.x, gameCharacter.y)) {
						for (int3 = 0; int3 < cCObstacle2.edges.size(); ++int3) {
							cCEdge3 = (CollideWithObstacles.CCEdge)cCObstacle2.edges.get(int3);
							if (cCEdge3.node1.visible.contains(cCEdge3.node2)) {
								CollideWithObstacles.CCNode cCNode3 = cCEdge3.closestPoint(gameCharacter.x, gameCharacter.y, this.closest);
								double double2 = (double)((gameCharacter.x - this.closest.x) * (gameCharacter.x - this.closest.x) + (gameCharacter.y - this.closest.y) * (gameCharacter.y - this.closest.y));
								if (double2 < double1) {
									double1 = double2;
									cCEdge2 = cCEdge3;
									cCNode = cCNode3;
								}
							}
						}
					}
				}

				float float7;
				if (cCEdge2 != null) {
					float7 = cCEdge2.normal.dot(this.move);
					if (float7 >= 0.01F) {
						cCEdge2 = null;
					}
				}

				if (cCNode != null && cCNode.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec) && this.nodeNormal.dot(this.move) + 0.05F >= this.nodeNormal.dot(this.edgeVec)) {
					cCNode = null;
					cCEdge2 = null;
				}

				if (cCEdge2 == null) {
					double double3 = Double.MAX_VALUE;
					cCEdge2 = null;
					cCNode = null;
					for (int3 = 0; int3 < this.obstacles.size(); ++int3) {
						CollideWithObstacles.CCObstacle cCObstacle3 = (CollideWithObstacles.CCObstacle)this.obstacles.get(int3);
						for (int int5 = 0; int5 < cCObstacle3.edges.size(); ++int5) {
							CollideWithObstacles.CCEdge cCEdge4 = (CollideWithObstacles.CCEdge)cCObstacle3.edges.get(int5);
							if (cCEdge4.node1.visible.contains(cCEdge4.node2)) {
								float float8 = cCEdge4.node1.x;
								float float9 = cCEdge4.node1.y;
								float float10 = cCEdge4.node2.x;
								float float11 = cCEdge4.node2.y;
								float float12 = float8 + 0.5F * (float10 - float8);
								float float13 = float9 + 0.5F * (float11 - float9);
								if (boolean1) {
									LineDrawer.addLine(float12, float13, (float)cCEdge4.node1.z, float12 + cCEdge4.normal.x, float13 + cCEdge4.normal.y, (float)cCEdge4.node1.z, 0.0F, 0.0F, 1.0F, (String)null, true);
								}

								double double4 = (double)((float11 - float9) * (float5 - float3) - (float10 - float8) * (float6 - float4));
								if (double4 != 0.0) {
									double double5 = (double)((float10 - float8) * (float4 - float9) - (float11 - float9) * (float3 - float8)) / double4;
									double double6 = (double)((float5 - float3) * (float4 - float9) - (float6 - float4) * (float3 - float8)) / double4;
									float float14 = cCEdge4.normal.dot(this.move);
									if (!(float14 >= 0.0F) && double5 >= 0.0 && double5 <= 1.0 && double6 >= 0.0 && double6 <= 1.0) {
										if (double6 < 0.01 || double6 > 0.99) {
											CollideWithObstacles.CCNode cCNode4 = double6 < 0.01 ? cCEdge4.node1 : cCEdge4.node2;
											if (cCNode4.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec)) {
												if (!(this.nodeNormal.dot(this.move) + 0.05F >= this.nodeNormal.dot(this.edgeVec))) {
													cCEdge2 = cCEdge4;
													cCNode = cCNode4;
													break;
												}

												continue;
											}
										}

										float float15 = (float)((double)float3 + double5 * (double)(float5 - float3));
										float float16 = (float)((double)float4 + double5 * (double)(float6 - float4));
										double double7 = (double)IsoUtils.DistanceToSquared(float3, float4, float15, float16);
										if (double7 < double3) {
											double3 = double7;
											cCEdge2 = cCEdge4;
										}
									}
								}
							}
						}
					}
				}

				if (cCNode != null) {
					CollideWithObstacles.CCEdge cCEdge5 = cCEdge2;
					CollideWithObstacles.CCEdge cCEdge6 = null;
					for (int3 = 0; int3 < cCNode.edges.size(); ++int3) {
						cCEdge3 = (CollideWithObstacles.CCEdge)cCNode.edges.get(int3);
						if (cCEdge3.node1.visible.contains(cCEdge3.node2) && cCEdge3 != cCEdge2 && (cCEdge5.node1.x != cCEdge3.node1.x || cCEdge5.node1.y != cCEdge3.node1.y || cCEdge5.node2.x != cCEdge3.node2.x || cCEdge5.node2.y != cCEdge3.node2.y) && (cCEdge5.node1.x != cCEdge3.node2.x || cCEdge5.node1.y != cCEdge3.node2.y || cCEdge5.node2.x != cCEdge3.node1.x || cCEdge5.node2.y != cCEdge3.node1.y) && (!cCEdge5.hasNode(cCEdge3.node1) || !cCEdge5.hasNode(cCEdge3.node2))) {
							cCEdge6 = cCEdge3;
						}
					}

					if (cCEdge5 != null && cCEdge6 != null) {
						CollideWithObstacles.CCNode cCNode5;
						if (cCEdge2 == cCEdge5) {
							cCNode5 = cCNode == cCEdge6.node1 ? cCEdge6.node2 : cCEdge6.node1;
							this.edgeVec.set(cCNode5.x - cCNode.x, cCNode5.y - cCNode.y);
							this.edgeVec.normalize();
							if (this.move.dot(this.edgeVec) >= 0.0F) {
								cCEdge2 = cCEdge6;
							}
						} else if (cCEdge2 == cCEdge6) {
							cCNode5 = cCNode == cCEdge5.node1 ? cCEdge5.node2 : cCEdge5.node1;
							this.edgeVec.set(cCNode5.x - cCNode.x, cCNode5.y - cCNode.y);
							this.edgeVec.normalize();
							if (this.move.dot(this.edgeVec) >= 0.0F) {
								cCEdge2 = cCEdge5;
							}
						}
					}
				}

				if (cCEdge2 != null) {
					float7 = cCEdge2.node1.x;
					float float17 = cCEdge2.node1.y;
					float float18 = cCEdge2.node2.x;
					float float19 = cCEdge2.node2.y;
					if (boolean1) {
						LineDrawer.addLine(float7, float17, (float)cCEdge2.node1.z, float18, float19, (float)cCEdge2.node1.z, 0.0F, 1.0F, 1.0F, (String)null, true);
					}

					cCEdge2.closestPoint(float1, float2, this.closest);
					gameCharacter.nx = this.closest.x;
					gameCharacter.ny = this.closest.y;
				}
			}
		}
	}

	static class CompareIntersection implements Comparator {
		CollideWithObstacles.CCEdge edge;

		public int compare(CollideWithObstacles.CCIntersection cCIntersection, CollideWithObstacles.CCIntersection cCIntersection2) {
			float float1 = this.edge == cCIntersection.edge1 ? cCIntersection.dist1 : cCIntersection.dist2;
			float float2 = this.edge == cCIntersection2.edge1 ? cCIntersection2.dist1 : cCIntersection2.dist2;
			if (float1 < float2) {
				return -1;
			} else {
				return float1 > float2 ? 1 : 0;
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

		CollideWithObstacles.ImmutableRectF init(float float1, float float2, float float3, float float4) {
			this.x = float1;
			this.y = float2;
			this.w = float3;
			this.h = float4;
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

		boolean containsPoint(float float1, float float2) {
			return float1 >= this.left() && float1 < this.right() && float2 >= this.top() && float2 < this.bottom();
		}

		boolean intersects(CollideWithObstacles.ImmutableRectF immutableRectF) {
			return this.left() < immutableRectF.right() && this.right() > immutableRectF.left() && this.top() < immutableRectF.bottom() && this.bottom() > immutableRectF.top();
		}

		static CollideWithObstacles.ImmutableRectF alloc() {
			return pool.isEmpty() ? new CollideWithObstacles.ImmutableRectF() : (CollideWithObstacles.ImmutableRectF)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			pool.push(this);
		}

		ImmutableRectF(Object object) {
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

		CollideWithObstacles.CCIntersection init(CollideWithObstacles.CCEdge cCEdge, CollideWithObstacles.CCEdge cCEdge2, float float1, float float2, float float3, float float4) {
			this.edge1 = cCEdge;
			this.edge2 = cCEdge2;
			this.dist1 = float1;
			this.dist2 = float2;
			this.nodeSplit = CollideWithObstacles.CCNode.alloc().init(float3, float4, cCEdge.node1.z);
			return this;
		}

		CollideWithObstacles.CCIntersection init(CollideWithObstacles.CCEdge cCEdge, CollideWithObstacles.CCEdge cCEdge2, float float1, float float2, CollideWithObstacles.CCNode cCNode) {
			this.edge1 = cCEdge;
			this.edge2 = cCEdge2;
			this.dist1 = float1;
			this.dist2 = float2;
			this.nodeSplit = cCNode;
			return this;
		}

		CollideWithObstacles.CCEdge split(CollideWithObstacles.CCEdge cCEdge) {
			if (cCEdge.hasNode(this.nodeSplit)) {
				return null;
			} else if (cCEdge.node1.x == this.nodeSplit.x && cCEdge.node1.y == this.nodeSplit.y) {
				return null;
			} else {
				return cCEdge.node2.x == this.nodeSplit.x && cCEdge.node2.y == this.nodeSplit.y ? null : cCEdge.split(this.nodeSplit);
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

		CollideWithObstacles.CCObjectOutline init(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.z = int3;
			this.nw = this.nw_w = this.nw_n = this.nw_e = false;
			this.w_w = this.w_e = this.w_cutoff = false;
			this.n_n = this.n_s = this.n_cutoff = false;
			return this;
		}

		static void setSolid(int int1, int int2, int int3, CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray) {
			setWest(int1, int2, int3, cCObjectOutlineArrayArray);
			setNorth(int1, int2, int3, cCObjectOutlineArrayArray);
			setWest(int1 + 1, int2, int3, cCObjectOutlineArrayArray);
			setNorth(int1, int2 + 1, int3, cCObjectOutlineArrayArray);
		}

		static void setWest(int int1, int int2, int int3, CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray) {
			CollideWithObstacles.CCObjectOutline cCObjectOutline = get(int1, int2, int3, cCObjectOutlineArrayArray);
			if (cCObjectOutline != null) {
				if (cCObjectOutline.nw) {
					cCObjectOutline.nw_s = false;
				} else {
					cCObjectOutline.nw = true;
					cCObjectOutline.nw_w = true;
					cCObjectOutline.nw_n = true;
					cCObjectOutline.nw_e = true;
					cCObjectOutline.nw_s = false;
				}

				cCObjectOutline.w_w = true;
				cCObjectOutline.w_e = true;
			}

			CollideWithObstacles.CCObjectOutline cCObjectOutline2 = cCObjectOutline;
			cCObjectOutline = get(int1, int2 + 1, int3, cCObjectOutlineArrayArray);
			if (cCObjectOutline == null) {
				if (cCObjectOutline2 != null) {
					cCObjectOutline2.w_cutoff = true;
				}
			} else if (cCObjectOutline.nw) {
				cCObjectOutline.nw_n = false;
			} else {
				cCObjectOutline.nw = true;
				cCObjectOutline.nw_n = false;
				cCObjectOutline.nw_w = true;
				cCObjectOutline.nw_e = true;
				cCObjectOutline.nw_s = true;
			}
		}

		static void setNorth(int int1, int int2, int int3, CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray) {
			CollideWithObstacles.CCObjectOutline cCObjectOutline = get(int1, int2, int3, cCObjectOutlineArrayArray);
			if (cCObjectOutline != null) {
				if (cCObjectOutline.nw) {
					cCObjectOutline.nw_e = false;
				} else {
					cCObjectOutline.nw = true;
					cCObjectOutline.nw_w = true;
					cCObjectOutline.nw_n = true;
					cCObjectOutline.nw_e = false;
					cCObjectOutline.nw_s = true;
				}

				cCObjectOutline.n_n = true;
				cCObjectOutline.n_s = true;
			}

			CollideWithObstacles.CCObjectOutline cCObjectOutline2 = cCObjectOutline;
			cCObjectOutline = get(int1 + 1, int2, int3, cCObjectOutlineArrayArray);
			if (cCObjectOutline == null) {
				if (cCObjectOutline2 != null) {
					cCObjectOutline2.n_cutoff = true;
				}
			} else if (cCObjectOutline.nw) {
				cCObjectOutline.nw_w = false;
			} else {
				cCObjectOutline.nw = true;
				cCObjectOutline.nw_n = true;
				cCObjectOutline.nw_w = false;
				cCObjectOutline.nw_e = true;
				cCObjectOutline.nw_s = true;
			}
		}

		static CollideWithObstacles.CCObjectOutline get(int int1, int int2, int int3, CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray) {
			if (int1 >= 0 && int1 < cCObjectOutlineArrayArray.length) {
				if (int2 >= 0 && int2 < cCObjectOutlineArrayArray[0].length) {
					if (cCObjectOutlineArrayArray[int1][int2] == null) {
						cCObjectOutlineArrayArray[int1][int2] = alloc().init(int1, int2, int3);
					}

					return cCObjectOutlineArrayArray[int1][int2];
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		void trace_NW_N(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray, CollideWithObstacles.CCNode cCNode) {
			if (cCNode != null) {
				cCNode.setXY((float)this.x + 0.3F, (float)this.y - 0.3F);
			} else {
				CollideWithObstacles.CCNode cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)this.x + 0.3F, (float)this.y - 0.3F, this.z);
				this.nodes.add(cCNode2);
			}

			this.nw_n = false;
			if (this.nw_e) {
				this.trace_NW_E(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)null);
			} else if (this.n_n) {
				this.trace_N_N(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
			}
		}

		void trace_NW_S(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray, CollideWithObstacles.CCNode cCNode) {
			if (cCNode != null) {
				cCNode.setXY((float)this.x - 0.3F, (float)this.y + 0.3F);
			} else {
				CollideWithObstacles.CCNode cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(cCNode2);
			}

			this.nw_s = false;
			if (this.nw_w) {
				this.trace_NW_W(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)null);
			} else {
				CollideWithObstacles.CCObjectOutline cCObjectOutline = get(this.x - 1, this.y, this.z, cCObjectOutlineArrayArray);
				if (cCObjectOutline == null) {
					return;
				}

				if (cCObjectOutline.n_s) {
					cCObjectOutline.nodes = this.nodes;
					cCObjectOutline.trace_N_S(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
				}
			}
		}

		void trace_NW_W(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray, CollideWithObstacles.CCNode cCNode) {
			if (cCNode != null) {
				cCNode.setXY((float)this.x - 0.3F, (float)this.y - 0.3F);
			} else {
				CollideWithObstacles.CCNode cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)this.y - 0.3F, this.z);
				this.nodes.add(cCNode2);
			}

			this.nw_w = false;
			if (this.nw_n) {
				this.trace_NW_N(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)null);
			} else {
				CollideWithObstacles.CCObjectOutline cCObjectOutline = get(this.x, this.y - 1, this.z, cCObjectOutlineArrayArray);
				if (cCObjectOutline == null) {
					return;
				}

				if (cCObjectOutline.w_w) {
					cCObjectOutline.nodes = this.nodes;
					cCObjectOutline.trace_W_W(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
				}
			}
		}

		void trace_NW_E(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray, CollideWithObstacles.CCNode cCNode) {
			if (cCNode != null) {
				cCNode.setXY((float)this.x + 0.3F, (float)this.y + 0.3F);
			} else {
				CollideWithObstacles.CCNode cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)this.x + 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(cCNode2);
			}

			this.nw_e = false;
			if (this.nw_s) {
				this.trace_NW_S(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)null);
			} else if (this.w_e) {
				this.trace_W_E(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
			}
		}

		void trace_W_E(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray, CollideWithObstacles.CCNode cCNode) {
			CollideWithObstacles.CCNode cCNode2;
			if (cCNode != null) {
				cCNode.setXY((float)this.x + 0.3F, (float)(this.y + 1) - 0.3F);
			} else {
				cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)this.x + 0.3F, (float)(this.y + 1) - 0.3F, this.z);
				this.nodes.add(cCNode2);
			}

			this.w_e = false;
			if (this.w_cutoff) {
				cCNode2 = (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1);
				cCNode2.setXY((float)this.x + 0.3F, (float)(this.y + 1) + 0.3F);
				cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)(this.y + 1) + 0.3F, this.z);
				this.nodes.add(cCNode2);
				cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)(this.y + 1) - 0.3F, this.z);
				this.nodes.add(cCNode2);
				this.trace_W_W(cCObjectOutlineArrayArray, cCNode2);
			} else {
				CollideWithObstacles.CCObjectOutline cCObjectOutline = get(this.x, this.y + 1, this.z, cCObjectOutlineArrayArray);
				if (cCObjectOutline != null) {
					if (cCObjectOutline.nw && cCObjectOutline.nw_e) {
						cCObjectOutline.nodes = this.nodes;
						cCObjectOutline.trace_NW_E(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
					} else if (cCObjectOutline.n_n) {
						cCObjectOutline.nodes = this.nodes;
						cCObjectOutline.trace_N_N(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)null);
					}
				}
			}
		}

		void trace_W_W(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray, CollideWithObstacles.CCNode cCNode) {
			if (cCNode != null) {
				cCNode.setXY((float)this.x - 0.3F, (float)this.y + 0.3F);
			} else {
				CollideWithObstacles.CCNode cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(cCNode2);
			}

			this.w_w = false;
			if (this.nw_w) {
				this.trace_NW_W(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
			} else {
				CollideWithObstacles.CCObjectOutline cCObjectOutline = get(this.x - 1, this.y, this.z, cCObjectOutlineArrayArray);
				if (cCObjectOutline == null) {
					return;
				}

				if (cCObjectOutline.n_s) {
					cCObjectOutline.nodes = this.nodes;
					cCObjectOutline.trace_N_S(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)null);
				}
			}
		}

		void trace_N_N(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray, CollideWithObstacles.CCNode cCNode) {
			CollideWithObstacles.CCNode cCNode2;
			if (cCNode != null) {
				cCNode.setXY((float)(this.x + 1) - 0.3F, (float)this.y - 0.3F);
			} else {
				cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)(this.x + 1) - 0.3F, (float)this.y - 0.3F, this.z);
				this.nodes.add(cCNode2);
			}

			this.n_n = false;
			if (this.n_cutoff) {
				cCNode2 = (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1);
				cCNode2.setXY((float)(this.x + 1) + 0.3F, (float)this.y - 0.3F);
				cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)(this.x + 1) + 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(cCNode2);
				cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)(this.x + 1) - 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(cCNode2);
				this.trace_N_S(cCObjectOutlineArrayArray, cCNode2);
			} else {
				CollideWithObstacles.CCObjectOutline cCObjectOutline = get(this.x + 1, this.y, this.z, cCObjectOutlineArrayArray);
				if (cCObjectOutline != null) {
					if (cCObjectOutline.nw_n) {
						cCObjectOutline.nodes = this.nodes;
						cCObjectOutline.trace_NW_N(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
					} else {
						cCObjectOutline = get(this.x + 1, this.y - 1, this.z, cCObjectOutlineArrayArray);
						if (cCObjectOutline == null) {
							return;
						}

						if (cCObjectOutline.w_w) {
							cCObjectOutline.nodes = this.nodes;
							cCObjectOutline.trace_W_W(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)null);
						}
					}
				}
			}
		}

		void trace_N_S(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray, CollideWithObstacles.CCNode cCNode) {
			if (cCNode != null) {
				cCNode.setXY((float)this.x + 0.3F, (float)this.y + 0.3F);
			} else {
				CollideWithObstacles.CCNode cCNode2 = CollideWithObstacles.CCNode.alloc().init((float)this.x + 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(cCNode2);
			}

			this.n_s = false;
			if (this.nw_s) {
				this.trace_NW_S(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)this.nodes.get(this.nodes.size() - 1));
			} else if (this.w_e) {
				this.trace_W_E(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)null);
			}
		}

		void trace(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlineArrayArray, ArrayList arrayList) {
			arrayList.clear();
			this.nodes = arrayList;
			CollideWithObstacles.CCNode cCNode = CollideWithObstacles.CCNode.alloc().init((float)this.x - 0.3F, (float)this.y - 0.3F, this.z);
			arrayList.add(cCNode);
			this.trace_NW_N(cCObjectOutlineArrayArray, (CollideWithObstacles.CCNode)null);
			if (arrayList.size() != 2 && cCNode.x == ((CollideWithObstacles.CCNode)arrayList.get(arrayList.size() - 1)).x && cCNode.y == ((CollideWithObstacles.CCNode)arrayList.get(arrayList.size() - 1)).y) {
				((CollideWithObstacles.CCNode)arrayList.get(arrayList.size() - 1)).release();
				arrayList.set(arrayList.size() - 1, cCNode);
			} else {
				arrayList.clear();
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

		boolean hasNode(CollideWithObstacles.CCNode cCNode) {
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)this.edges.get(int1);
				if (cCEdge.hasNode(cCNode)) {
					return true;
				}
			}

			return false;
		}

		boolean hasAdjacentNodes(CollideWithObstacles.CCNode cCNode, CollideWithObstacles.CCNode cCNode2) {
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)this.edges.get(int1);
				if (cCEdge.hasNode(cCNode) && cCEdge.hasNode(cCNode2)) {
					return true;
				}
			}

			return false;
		}

		boolean isPointInPolygon_CrossingNumber(float float1, float float2) {
			int int1 = 0;
			for (int int2 = 0; int2 < this.edges.size(); ++int2) {
				CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)this.edges.get(int2);
				if (cCEdge.node1.y <= float2 && cCEdge.node2.y > float2 || cCEdge.node1.y > float2 && cCEdge.node2.y <= float2) {
					float float3 = (float2 - cCEdge.node1.y) / (cCEdge.node2.y - cCEdge.node1.y);
					if (float1 < cCEdge.node1.x + float3 * (cCEdge.node2.x - cCEdge.node1.x)) {
						++int1;
					}
				}
			}

			return int1 % 2 == 1;
		}

		float isLeft(float float1, float float2, float float3, float float4, float float5, float float6) {
			return (float3 - float1) * (float6 - float2) - (float5 - float1) * (float4 - float2);
		}

		boolean isPointInPolygon_WindingNumber(float float1, float float2) {
			int int1 = 0;
			for (int int2 = 0; int2 < this.edges.size(); ++int2) {
				CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)this.edges.get(int2);
				if (cCEdge.node1.y <= float2) {
					if (cCEdge.node2.y > float2 && this.isLeft(cCEdge.node1.x, cCEdge.node1.y, cCEdge.node2.x, cCEdge.node2.y, float1, float2) > 0.0F) {
						++int1;
					}
				} else if (cCEdge.node2.y <= float2 && this.isLeft(cCEdge.node1.x, cCEdge.node1.y, cCEdge.node2.x, cCEdge.node2.y, float1, float2) < 0.0F) {
					--int1;
				}
			}

			return int1 != 0;
		}

		boolean isNodeInsideOf(CollideWithObstacles.CCNode cCNode) {
			if (this.hasNode(cCNode)) {
				return false;
			} else {
				return !this.bounds.containsPoint(cCNode.x, cCNode.y) ? false : this.isPointInPolygon_WindingNumber(cCNode.x, cCNode.y);
			}
		}

		CollideWithObstacles.CCNode getClosestPointOnEdge(float float1, float float2, Vector2 vector2) {
			double double1 = Double.MAX_VALUE;
			CollideWithObstacles.CCNode cCNode = null;
			float float3 = Float.MAX_VALUE;
			float float4 = Float.MAX_VALUE;
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)this.edges.get(int1);
				if (cCEdge.node1.visible.contains(cCEdge.node2)) {
					CollideWithObstacles.CCNode cCNode2 = cCEdge.closestPoint(float1, float2, vector2);
					double double2 = (double)((float1 - vector2.x) * (float1 - vector2.x) + (float2 - vector2.y) * (float2 - vector2.y));
					if (double2 < double1) {
						float3 = vector2.x;
						float4 = vector2.y;
						cCNode = cCNode2;
						double1 = double2;
					}
				}
			}

			vector2.set(float3, float4);
			return cCNode;
		}

		void calcBounds() {
			float float1 = Float.MAX_VALUE;
			float float2 = Float.MAX_VALUE;
			float float3 = Float.MIN_VALUE;
			float float4 = Float.MIN_VALUE;
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)this.edges.get(int1);
				float1 = Math.min(float1, cCEdge.node1.x);
				float2 = Math.min(float2, cCEdge.node1.y);
				float3 = Math.max(float3, cCEdge.node1.x);
				float4 = Math.max(float4, cCEdge.node1.y);
			}

			if (this.bounds != null) {
				this.bounds.release();
			}

			float float5 = 0.01F;
			this.bounds = CollideWithObstacles.ImmutableRectF.alloc().init(float1 - float5, float2 - float5, float3 - float1 + float5 * 2.0F, float4 - float2 + float5 * 2.0F);
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

		CollideWithObstacles.CCEdge init(CollideWithObstacles.CCNode cCNode, CollideWithObstacles.CCNode cCNode2, CollideWithObstacles.CCObstacle cCObstacle) {
			if (cCNode.x == cCNode2.x && cCNode.y == cCNode2.y) {
				boolean boolean1 = false;
			}

			this.node1 = cCNode;
			this.node2 = cCNode2;
			cCNode.edges.add(this);
			cCNode2.edges.add(this);
			this.obstacle = cCObstacle;
			this.intersections.clear();
			this.normal.set(cCNode2.x - cCNode.x, cCNode2.y - cCNode.y);
			this.normal.normalize();
			this.normal.rotate((float)Math.toRadians(90.0));
			return this;
		}

		boolean hasNode(CollideWithObstacles.CCNode cCNode) {
			return cCNode == this.node1 || cCNode == this.node2;
		}

		CollideWithObstacles.CCEdge split(CollideWithObstacles.CCNode cCNode) {
			CollideWithObstacles.CCEdge cCEdge = alloc().init(cCNode, this.node2, this.obstacle);
			this.obstacle.edges.add(this.obstacle.edges.indexOf(this) + 1, cCEdge);
			this.node2.edges.remove(this);
			this.node2 = cCNode;
			this.node2.edges.add(this);
			return cCEdge;
		}

		CollideWithObstacles.CCNode closestPoint(float float1, float float2, Vector2 vector2) {
			float float3 = this.node1.x;
			float float4 = this.node1.y;
			float float5 = this.node2.x;
			float float6 = this.node2.y;
			double double1 = (double)((float1 - float3) * (float5 - float3) + (float2 - float4) * (float6 - float4)) / (Math.pow((double)(float5 - float3), 2.0) + Math.pow((double)(float6 - float4), 2.0));
			double double2 = 0.001;
			if (double1 <= 0.0 + double2) {
				vector2.set(float3, float4);
				return this.node1;
			} else if (double1 >= 1.0 - double2) {
				vector2.set(float5, float6);
				return this.node2;
			} else {
				double double3 = (double)float3 + double1 * (double)(float5 - float3);
				double double4 = (double)float4 + double1 * (double)(float6 - float4);
				vector2.set((float)double3, (float)double4);
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

		CollideWithObstacles.CCNode init(float float1, float float2, int int1) {
			this.x = float1;
			this.y = float2;
			this.z = int1;
			this.ignore = false;
			this.edges.clear();
			this.visible.clear();
			return this;
		}

		CollideWithObstacles.CCNode setXY(float float1, float float2) {
			this.x = float1;
			this.y = float2;
			return this;
		}

		boolean sharesEdge(CollideWithObstacles.CCNode cCNode) {
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)this.edges.get(int1);
				if (cCEdge.hasNode(cCNode)) {
					return true;
				}
			}

			return false;
		}

		boolean sharesShape(CollideWithObstacles.CCNode cCNode) {
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)this.edges.get(int1);
				for (int int2 = 0; int2 < cCNode.edges.size(); ++int2) {
					CollideWithObstacles.CCEdge cCEdge2 = (CollideWithObstacles.CCEdge)cCNode.edges.get(int2);
					if (cCEdge.obstacle != null && cCEdge.obstacle == cCEdge2.obstacle) {
						return true;
					}
				}
			}

			return false;
		}

		void getObstacles(ArrayList arrayList) {
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				CollideWithObstacles.CCEdge cCEdge = (CollideWithObstacles.CCEdge)this.edges.get(int1);
				if (!arrayList.contains(cCEdge.obstacle)) {
					arrayList.add(cCEdge.obstacle);
				}
			}
		}

		boolean onSameShapeButDoesNotShareAnEdge(CollideWithObstacles.CCNode cCNode) {
			tempObstacles.clear();
			this.getObstacles(tempObstacles);
			for (int int1 = 0; int1 < tempObstacles.size(); ++int1) {
				CollideWithObstacles.CCObstacle cCObstacle = (CollideWithObstacles.CCObstacle)tempObstacles.get(int1);
				if (cCObstacle.hasNode(cCNode) && !cCObstacle.hasAdjacentNodes(this, cCNode)) {
					return true;
				}
			}

			return false;
		}

		boolean getNormalAndEdgeVectors(Vector2 vector2, Vector2 vector22) {
			CollideWithObstacles.CCEdge cCEdge = null;
			CollideWithObstacles.CCEdge cCEdge2 = null;
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				CollideWithObstacles.CCEdge cCEdge3 = (CollideWithObstacles.CCEdge)this.edges.get(int1);
				if (cCEdge3.node1.visible.contains(cCEdge3.node2)) {
					if (cCEdge == null) {
						cCEdge = cCEdge3;
					} else if (!cCEdge.hasNode(cCEdge3.node1) || !cCEdge.hasNode(cCEdge3.node2)) {
						cCEdge2 = cCEdge3;
					}
				}
			}

			if (cCEdge != null && cCEdge2 != null) {
				float float1 = cCEdge.normal.x + cCEdge2.normal.x;
				float float2 = cCEdge.normal.y + cCEdge2.normal.y;
				vector2.set(float1, float2);
				vector2.normalize();
				if (cCEdge.node1 == this) {
					vector22.set(cCEdge.node2.x - cCEdge.node1.x, cCEdge.node2.y - cCEdge.node1.y);
				} else {
					vector22.set(cCEdge.node1.x - cCEdge.node2.x, cCEdge.node1.y - cCEdge.node2.y);
				}

				vector22.normalize();
				return true;
			} else {
				return false;
			}
		}

		static CollideWithObstacles.CCNode alloc() {
			boolean boolean1;
			if (pool.isEmpty()) {
				boolean1 = false;
			} else {
				boolean1 = false;
			}

			return pool.isEmpty() ? new CollideWithObstacles.CCNode() : (CollideWithObstacles.CCNode)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			pool.push(this);
		}
	}
}
