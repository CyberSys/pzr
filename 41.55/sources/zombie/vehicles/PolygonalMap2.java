package zombie.vehicles;

import astar.ASearchNode;
import astar.AStar;
import astar.IGoalNode;
import astar.ISearchNode;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.awt.geom.Line2D;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.Lua.LuaManager;
import zombie.ai.KnownBlockedEdges;
import zombie.ai.astar.Mover;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.physics.Transform;
import zombie.core.utils.BooleanGrid;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.network.ServerMap;
import zombie.popman.ObjectPool;
import zombie.scripting.objects.VehicleScript;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;


public final class PolygonalMap2 {
	public static final float RADIUS = 0.3F;
	private static final float RADIUS_DIAGONAL = (float)Math.sqrt(0.18000000715255737);
	public static final boolean CLOSE_TO_WALLS = true;
	public static final boolean PATHS_UNDER_VEHICLES = true;
	public static final boolean COLLIDE_CLIPPER = false;
	public static final boolean COLLIDE_BEVEL = false;
	public static final int CXN_FLAG_CAN_PATH = 1;
	public static final int CXN_FLAG_THUMP = 2;
	public static final int NODE_FLAG_CRAWL = 1;
	public static final int NODE_FLAG_CRAWL_INTERIOR = 2;
	public static final int NODE_FLAG_IN_CHUNK_DATA = 4;
	public static final int NODE_FLAG_PERIMETER = 8;
	public static final int NODE_FLAG_KEEP = 65536;
	private static final Vector3f tempVec3f_1 = new Vector3f();
	private final ArrayList clusters = new ArrayList();
	private PolygonalMap2.ClosestPointOnEdge closestPointOnEdge = new PolygonalMap2.ClosestPointOnEdge();
	private final TIntObjectHashMap squareToNode = new TIntObjectHashMap();
	private final ArrayList tempSquares = new ArrayList();
	public static final PolygonalMap2 instance = new PolygonalMap2();
	private final ArrayList graphs = new ArrayList();
	private Clipper clipperThread;
	private final ByteBuffer xyBufferThread = ByteBuffer.allocateDirect(8192);
	private final PolygonalMap2.AdjustStartEndNodeData adjustStartData = new PolygonalMap2.AdjustStartEndNodeData();
	private final PolygonalMap2.AdjustStartEndNodeData adjustGoalData = new PolygonalMap2.AdjustStartEndNodeData();
	private final PolygonalMap2.LineClearCollide lcc = new PolygonalMap2.LineClearCollide();
	private final PolygonalMap2.VGAStar astar = new PolygonalMap2.VGAStar();
	private final PolygonalMap2.TestRequest testRequest = new PolygonalMap2.TestRequest();
	private int testZ = 0;
	private final PathFindBehavior2.PointOnPath pointOnPath = new PathFindBehavior2.PointOnPath();
	private static final int SQUARES_PER_CHUNK = 10;
	private static final int LEVELS_PER_CHUNK = 8;
	private static final int SQUARES_PER_CELL = 300;
	private static final int CHUNKS_PER_CELL = 30;
	private static final int BIT_SOLID = 1;
	private static final int BIT_COLLIDE_W = 2;
	private static final int BIT_COLLIDE_N = 4;
	private static final int BIT_STAIR_TW = 8;
	private static final int BIT_STAIR_MW = 16;
	private static final int BIT_STAIR_BW = 32;
	private static final int BIT_STAIR_TN = 64;
	private static final int BIT_STAIR_MN = 128;
	private static final int BIT_STAIR_BN = 256;
	private static final int BIT_SOLID_FLOOR = 512;
	private static final int BIT_SOLID_TRANS = 1024;
	private static final int BIT_WINDOW_W = 2048;
	private static final int BIT_WINDOW_N = 4096;
	private static final int BIT_CAN_PATH_W = 8192;
	private static final int BIT_CAN_PATH_N = 16384;
	private static final int BIT_THUMP_W = 32768;
	private static final int BIT_THUMP_N = 65536;
	private static final int BIT_THUMPABLE = 131072;
	private static final int BIT_DOOR_E = 262144;
	private static final int BIT_DOOR_S = 524288;
	private static final int BIT_WINDOW_W_UNBLOCKED = 1048576;
	private static final int BIT_WINDOW_N_UNBLOCKED = 2097152;
	private static final int ALL_SOLID_BITS = 1025;
	private static final int ALL_STAIR_BITS = 504;
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
	private final TObjectProcedure releaseNodeProc = new TObjectProcedure(){
    
    public boolean execute(PolygonalMap2.Node var1) {
        var1.release();
        return true;
    }
};
	private boolean rebuild;
	private final PolygonalMap2.Path shortestPath = new PolygonalMap2.Path();
	private final PolygonalMap2.Sync sync = new PolygonalMap2.Sync();
	private final Object renderLock = new Object();
	private PolygonalMap2.PMThread thread;
	private final ArrayDeque requests = new ArrayDeque();
	private final ConcurrentLinkedQueue requestToMain = new ConcurrentLinkedQueue();
	private final ConcurrentLinkedQueue requestTaskQueue = new ConcurrentLinkedQueue();
	private final HashMap requestMap = new HashMap();
	public static final int LCC_ZERO = 0;
	public static final int LCC_IGNORE_DOORS = 1;
	public static final int LCC_CLOSE_TO_WALLS = 2;
	public static final int LCC_CHECK_COST = 4;
	public static final int LCC_RENDER = 8;
	public static final int LCC_ALLOW_ON_EDGE = 16;
	private final PolygonalMap2.LineClearCollideMain lccMain = new PolygonalMap2.LineClearCollideMain();
	private final float[] tempFloats = new float[8];
	private final CollideWithObstacles collideWithObstacles = new CollideWithObstacles();
	private final CollideWithObstaclesPoly collideWithObstaclesPoly = new CollideWithObstaclesPoly();

	private void createVehicleCluster(PolygonalMap2.VehicleRect vehicleRect, ArrayList arrayList, ArrayList arrayList2) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			PolygonalMap2.VehicleRect vehicleRect2 = (PolygonalMap2.VehicleRect)arrayList.get(int1);
			if (vehicleRect != vehicleRect2 && vehicleRect.z == vehicleRect2.z && (vehicleRect.cluster == null || vehicleRect.cluster != vehicleRect2.cluster) && vehicleRect.isAdjacent(vehicleRect2)) {
				if (vehicleRect.cluster != null) {
					if (vehicleRect2.cluster == null) {
						vehicleRect2.cluster = vehicleRect.cluster;
						vehicleRect2.cluster.rects.add(vehicleRect2);
					} else {
						arrayList2.remove(vehicleRect2.cluster);
						vehicleRect.cluster.merge(vehicleRect2.cluster);
					}
				} else if (vehicleRect2.cluster != null) {
					if (vehicleRect.cluster == null) {
						vehicleRect.cluster = vehicleRect2.cluster;
						vehicleRect.cluster.rects.add(vehicleRect);
					} else {
						arrayList2.remove(vehicleRect.cluster);
						vehicleRect2.cluster.merge(vehicleRect.cluster);
					}
				} else {
					PolygonalMap2.VehicleCluster vehicleCluster = PolygonalMap2.VehicleCluster.alloc().init();
					vehicleRect.cluster = vehicleCluster;
					vehicleRect2.cluster = vehicleCluster;
					vehicleCluster.rects.add(vehicleRect);
					vehicleCluster.rects.add(vehicleRect2);
					arrayList2.add(vehicleCluster);
				}
			}
		}

		if (vehicleRect.cluster == null) {
			PolygonalMap2.VehicleCluster vehicleCluster2 = PolygonalMap2.VehicleCluster.alloc().init();
			vehicleRect.cluster = vehicleCluster2;
			vehicleCluster2.rects.add(vehicleRect);
			arrayList2.add(vehicleCluster2);
		}
	}

	private void createVehicleClusters() {
		this.clusters.clear();
		ArrayList arrayList = new ArrayList();
		int int1;
		for (int1 = 0; int1 < this.vehicles.size(); ++int1) {
			PolygonalMap2.Vehicle vehicle = (PolygonalMap2.Vehicle)this.vehicles.get(int1);
			PolygonalMap2.VehicleRect vehicleRect = PolygonalMap2.VehicleRect.alloc();
			vehicle.polyPlusRadius.getAABB(vehicleRect);
			vehicleRect.vehicle = vehicle;
			arrayList.add(vehicleRect);
		}

		if (!arrayList.isEmpty()) {
			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				PolygonalMap2.VehicleRect vehicleRect2 = (PolygonalMap2.VehicleRect)arrayList.get(int1);
				this.createVehicleCluster(vehicleRect2, arrayList, this.clusters);
			}
		}
	}

	private PolygonalMap2.Node getNodeForSquare(PolygonalMap2.Square square) {
		PolygonalMap2.Node node = (PolygonalMap2.Node)this.squareToNode.get(square.ID);
		if (node == null) {
			node = PolygonalMap2.Node.alloc().init(square);
			this.squareToNode.put(square.ID, node);
		}

		return node;
	}

	private PolygonalMap2.VisibilityGraph getVisGraphAt(float float1, float float2, int int1) {
		for (int int2 = 0; int2 < this.graphs.size(); ++int2) {
			PolygonalMap2.VisibilityGraph visibilityGraph = (PolygonalMap2.VisibilityGraph)this.graphs.get(int2);
			if (visibilityGraph.contains(float1, float2, int1)) {
				return visibilityGraph;
			}
		}

		return null;
	}

	private PolygonalMap2.VisibilityGraph getVisGraphAt(float float1, float float2, int int1, int int2) {
		for (int int3 = 0; int3 < this.graphs.size(); ++int3) {
			PolygonalMap2.VisibilityGraph visibilityGraph = (PolygonalMap2.VisibilityGraph)this.graphs.get(int3);
			if (visibilityGraph.contains(float1, float2, int1, int2)) {
				return visibilityGraph;
			}
		}

		return null;
	}

	private PolygonalMap2.VisibilityGraph getVisGraphForSquare(PolygonalMap2.Square square) {
		for (int int1 = 0; int1 < this.graphs.size(); ++int1) {
			PolygonalMap2.VisibilityGraph visibilityGraph = (PolygonalMap2.VisibilityGraph)this.graphs.get(int1);
			if (visibilityGraph.contains(square)) {
				return visibilityGraph;
			}
		}

		return null;
	}

	private PolygonalMap2.Connection connectTwoNodes(PolygonalMap2.Node node, PolygonalMap2.Node node2, int int1) {
		PolygonalMap2.Connection connection = PolygonalMap2.Connection.alloc().init(node, node2, int1);
		node.visible.add(connection);
		node2.visible.add(connection);
		return connection;
	}

	private PolygonalMap2.Connection connectTwoNodes(PolygonalMap2.Node node, PolygonalMap2.Node node2) {
		return this.connectTwoNodes(node, node2, 0);
	}

	private void breakConnection(PolygonalMap2.Connection connection) {
		connection.node1.visible.remove(connection);
		connection.node2.visible.remove(connection);
		connection.release();
	}

	private void breakConnection(PolygonalMap2.Node node, PolygonalMap2.Node node2) {
		for (int int1 = 0; int1 < node.visible.size(); ++int1) {
			PolygonalMap2.Connection connection = (PolygonalMap2.Connection)node.visible.get(int1);
			if (connection.otherNode(node) == node2) {
				this.breakConnection(connection);
				break;
			}
		}
	}

	private void addStairNodes() {
		ArrayList arrayList = this.tempSquares;
		arrayList.clear();
		int int1;
		for (int1 = 0; int1 < this.graphs.size(); ++int1) {
			PolygonalMap2.VisibilityGraph visibilityGraph = (PolygonalMap2.VisibilityGraph)this.graphs.get(int1);
			visibilityGraph.getStairSquares(arrayList);
		}

		for (int1 = 0; int1 < arrayList.size(); ++int1) {
			PolygonalMap2.Square square = (PolygonalMap2.Square)arrayList.get(int1);
			PolygonalMap2.Square square2 = null;
			PolygonalMap2.Square square3 = null;
			PolygonalMap2.Square square4 = null;
			PolygonalMap2.Square square5 = null;
			PolygonalMap2.Square square6 = null;
			if (square.has(8)) {
				square2 = this.getSquare(square.x - 1, square.y, square.z + 1);
				square3 = square;
				square4 = this.getSquare(square.x + 1, square.y, square.z);
				square5 = this.getSquare(square.x + 2, square.y, square.z);
				square6 = this.getSquare(square.x + 3, square.y, square.z);
			}

			if (square.has(64)) {
				square2 = this.getSquare(square.x, square.y - 1, square.z + 1);
				square3 = square;
				square4 = this.getSquare(square.x, square.y + 1, square.z);
				square5 = this.getSquare(square.x, square.y + 2, square.z);
				square6 = this.getSquare(square.x, square.y + 3, square.z);
			}

			if (square2 != null && square3 != null && square4 != null && square5 != null && square6 != null) {
				PolygonalMap2.Node node = null;
				PolygonalMap2.Node node2 = null;
				PolygonalMap2.VisibilityGraph visibilityGraph2 = this.getVisGraphForSquare(square2);
				Iterator iterator;
				PolygonalMap2.Obstacle obstacle;
				if (visibilityGraph2 == null) {
					node = this.getNodeForSquare(square2);
				} else {
					node = PolygonalMap2.Node.alloc().init(square2);
					iterator = visibilityGraph2.obstacles.iterator();
					while (iterator.hasNext()) {
						obstacle = (PolygonalMap2.Obstacle)iterator.next();
						if (obstacle.isNodeInsideOf(node)) {
							node.ignore = true;
						}
					}

					node.addGraph(visibilityGraph2);
					visibilityGraph2.addNode(node);
					this.squareToNode.put(square2.ID, node);
				}

				visibilityGraph2 = this.getVisGraphForSquare(square6);
				if (visibilityGraph2 == null) {
					node2 = this.getNodeForSquare(square6);
				} else {
					node2 = PolygonalMap2.Node.alloc().init(square6);
					iterator = visibilityGraph2.obstacles.iterator();
					while (iterator.hasNext()) {
						obstacle = (PolygonalMap2.Obstacle)iterator.next();
						if (obstacle.isNodeInsideOf(node2)) {
							node2.ignore = true;
						}
					}

					node2.addGraph(visibilityGraph2);
					visibilityGraph2.addNode(node2);
					this.squareToNode.put(square6.ID, node2);
				}

				if (node != null && node2 != null) {
					PolygonalMap2.Node node3 = this.getNodeForSquare(square3);
					PolygonalMap2.Node node4 = this.getNodeForSquare(square4);
					PolygonalMap2.Node node5 = this.getNodeForSquare(square5);
					this.connectTwoNodes(node, node3);
					this.connectTwoNodes(node3, node4);
					this.connectTwoNodes(node4, node5);
					this.connectTwoNodes(node5, node2);
				}
			}
		}
	}

	private void addCanPathNodes() {
		ArrayList arrayList = this.tempSquares;
		arrayList.clear();
		int int1;
		for (int1 = 0; int1 < this.graphs.size(); ++int1) {
			PolygonalMap2.VisibilityGraph visibilityGraph = (PolygonalMap2.VisibilityGraph)this.graphs.get(int1);
			visibilityGraph.getCanPathSquares(arrayList);
		}

		for (int1 = 0; int1 < arrayList.size(); ++int1) {
			PolygonalMap2.Square square = (PolygonalMap2.Square)arrayList.get(int1);
			if (!square.isNonThumpableSolid() && !square.has(504) && square.has(512)) {
				int int2;
				int int3;
				PolygonalMap2.Square square2;
				PolygonalMap2.Node node;
				PolygonalMap2.Node node2;
				int int4;
				if (square.isCanPathW()) {
					int2 = square.x - 1;
					int3 = square.y;
					square2 = this.getSquare(int2, int3, square.z);
					if (square2 != null && !square2.isNonThumpableSolid() && !square2.has(504) && square2.has(512)) {
						node = this.getOrCreateCanPathNode(square);
						node2 = this.getOrCreateCanPathNode(square2);
						int4 = 1;
						if (square.has(163840) || square2.has(131072)) {
							int4 |= 2;
						}

						this.connectTwoNodes(node, node2, int4);
					}
				}

				if (square.isCanPathN()) {
					int2 = square.x;
					int3 = square.y - 1;
					square2 = this.getSquare(int2, int3, square.z);
					if (square2 != null && !square2.isNonThumpableSolid() && !square2.has(504) && square2.has(512)) {
						node = this.getOrCreateCanPathNode(square);
						node2 = this.getOrCreateCanPathNode(square2);
						int4 = 1;
						if (square.has(196608) || square2.has(131072)) {
							int4 |= 2;
						}

						this.connectTwoNodes(node, node2, int4);
					}
				}
			}
		}
	}

	private PolygonalMap2.Node getOrCreateCanPathNode(PolygonalMap2.Square square) {
		PolygonalMap2.VisibilityGraph visibilityGraph = this.getVisGraphForSquare(square);
		PolygonalMap2.Node node = this.getNodeForSquare(square);
		if (visibilityGraph != null && !visibilityGraph.nodes.contains(node)) {
			Iterator iterator = visibilityGraph.obstacles.iterator();
			while (iterator.hasNext()) {
				PolygonalMap2.Obstacle obstacle = (PolygonalMap2.Obstacle)iterator.next();
				if (obstacle.isNodeInsideOf(node)) {
					node.ignore = true;
					break;
				}
			}

			visibilityGraph.addNode(node);
		}

		return node;
	}

	private PolygonalMap2.Node getPointOutsideObjects(PolygonalMap2.Square square, float float1, float float2) {
		PolygonalMap2.Square square2 = instance.getSquare(square.x - 1, square.y, square.z);
		PolygonalMap2.Square square3 = instance.getSquare(square.x - 1, square.y - 1, square.z);
		PolygonalMap2.Square square4 = instance.getSquare(square.x, square.y - 1, square.z);
		PolygonalMap2.Square square5 = instance.getSquare(square.x + 1, square.y - 1, square.z);
		PolygonalMap2.Square square6 = instance.getSquare(square.x + 1, square.y, square.z);
		PolygonalMap2.Square square7 = instance.getSquare(square.x + 1, square.y + 1, square.z);
		PolygonalMap2.Square square8 = instance.getSquare(square.x, square.y + 1, square.z);
		PolygonalMap2.Square square9 = instance.getSquare(square.x - 1, square.y + 1, square.z);
		float float3 = (float)square.x;
		float float4 = (float)square.y;
		float float5 = (float)(square.x + 1);
		float float6 = (float)(square.y + 1);
		if (square.isCollideW()) {
			float3 += 0.35000002F;
		}

		if (square.isCollideN()) {
			float4 += 0.35000002F;
		}

		if (square6 != null && (square6.has(2) || square6.has(504) || square6.isReallySolid())) {
			float5 -= 0.35000002F;
		}

		if (square8 != null && (square8.has(4) || square8.has(504) || square8.isReallySolid())) {
			float6 -= 0.35000002F;
		}

		float float7 = PZMath.clamp(float1, float3, float5);
		float float8 = PZMath.clamp(float2, float4, float6);
		boolean boolean1;
		float float9;
		float float10;
		if (float7 <= (float)square.x + 0.3F && float8 <= (float)square.y + 0.3F) {
			boolean1 = square3 != null && (square3.has(504) || square3.isReallySolid());
			boolean1 |= square4 != null && square4.has(2);
			boolean1 |= square2 != null && square2.has(4);
			if (boolean1) {
				float9 = (float)square.x + 0.3F + 0.05F;
				float10 = (float)square.y + 0.3F + 0.05F;
				if (float9 - float7 <= float10 - float8) {
					float7 = float9;
				} else {
					float8 = float10;
				}
			}
		}

		if (float7 >= (float)(square.x + 1) - 0.3F && float8 <= (float)square.y + 0.3F) {
			boolean1 = square5 != null && (square5.has(2) || square5.has(504) || square5.isReallySolid());
			boolean1 |= square6 != null && square6.has(4);
			if (boolean1) {
				float9 = (float)(square.x + 1) - 0.3F - 0.05F;
				float10 = (float)square.y + 0.3F + 0.05F;
				if (float7 - float9 <= float10 - float8) {
					float7 = float9;
				} else {
					float8 = float10;
				}
			}
		}

		if (float7 <= (float)square.x + 0.3F && float8 >= (float)(square.y + 1) - 0.3F) {
			boolean1 = square9 != null && (square9.has(4) || square9.has(504) || square9.isReallySolid());
			boolean1 |= square8 != null && square8.has(2);
			if (boolean1) {
				float9 = (float)square.x + 0.3F + 0.05F;
				float10 = (float)(square.y + 1) - 0.3F - 0.05F;
				if (float9 - float7 <= float8 - float10) {
					float7 = float9;
				} else {
					float8 = float10;
				}
			}
		}

		if (float7 >= (float)(square.x + 1) - 0.3F && float8 >= (float)(square.y + 1) - 0.3F) {
			boolean1 = square7 != null && (square7.has(2) || square7.has(4) || square7.has(504) || square7.isReallySolid());
			if (boolean1) {
				float9 = (float)(square.x + 1) - 0.3F - 0.05F;
				float10 = (float)(square.y + 1) - 0.3F - 0.05F;
				if (float7 - float9 <= float8 - float10) {
					float7 = float9;
				} else {
					float8 = float10;
				}
			}
		}

		return PolygonalMap2.Node.alloc().init(float7, float8, square.z);
	}

	private void createVisibilityGraph(PolygonalMap2.VehicleCluster vehicleCluster) {
		PolygonalMap2.VisibilityGraph visibilityGraph = PolygonalMap2.VisibilityGraph.alloc().init(vehicleCluster);
		visibilityGraph.addPerimeterEdges();
		this.graphs.add(visibilityGraph);
	}

	private void createVisibilityGraphs() {
		this.createVehicleClusters();
		this.graphs.clear();
		this.squareToNode.clear();
		for (int int1 = 0; int1 < this.clusters.size(); ++int1) {
			PolygonalMap2.VehicleCluster vehicleCluster = (PolygonalMap2.VehicleCluster)this.clusters.get(int1);
			this.createVisibilityGraph(vehicleCluster);
		}

		this.addStairNodes();
		this.addCanPathNodes();
	}

	private boolean findPath(PolygonalMap2.PathFindRequest pathFindRequest, boolean boolean1) {
		int int1 = 16;
		if (!(pathFindRequest.mover instanceof IsoZombie)) {
			int1 |= 4;
		}

		PolygonalMap2.VisibilityGraph visibilityGraph;
		if ((int)pathFindRequest.startZ == (int)pathFindRequest.targetZ && !this.lcc.isNotClear(this, pathFindRequest.startX, pathFindRequest.startY, pathFindRequest.targetX, pathFindRequest.targetY, (int)pathFindRequest.startZ, int1)) {
			pathFindRequest.path.addNode(pathFindRequest.startX, pathFindRequest.startY, pathFindRequest.startZ);
			pathFindRequest.path.addNode(pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ);
			if (boolean1) {
				Iterator iterator = this.graphs.iterator();
				while (iterator.hasNext()) {
					visibilityGraph = (PolygonalMap2.VisibilityGraph)iterator.next();
					visibilityGraph.render();
				}
			}

			return true;
		} else {
			this.astar.init(this.graphs, this.squareToNode);
			this.astar.knownBlockedEdges.clear();
			for (int int2 = 0; int2 < pathFindRequest.knownBlockedEdges.size(); ++int2) {
				KnownBlockedEdges knownBlockedEdges = (KnownBlockedEdges)pathFindRequest.knownBlockedEdges.get(int2);
				PolygonalMap2.Square square = this.getSquare(knownBlockedEdges.x, knownBlockedEdges.y, knownBlockedEdges.z);
				if (square != null) {
					this.astar.knownBlockedEdges.put(square.ID, knownBlockedEdges);
				}
			}

			PolygonalMap2.VisibilityGraph visibilityGraph2 = null;
			visibilityGraph = null;
			PolygonalMap2.SearchNode searchNode = null;
			PolygonalMap2.SearchNode searchNode2 = null;
			boolean boolean2 = false;
			boolean boolean3 = false;
			boolean boolean4 = false;
			boolean boolean5;
			label1511: {
				boolean boolean6;
				int int3;
				Iterator iterator2;
				PolygonalMap2.VisibilityGraph visibilityGraph3;
				PolygonalMap2.Edge edge;
				label1512: {
					boolean boolean7;
					PolygonalMap2.VisibilityGraph visibilityGraph4;
					Iterator iterator3;
					int int4;
					PolygonalMap2.Edge edge2;
					label1513: {
						label1514: {
							label1515: {
								label1516: {
									PolygonalMap2.VisibilityGraph visibilityGraph5;
									label1517: {
										int int5;
										boolean boolean8;
										try {
											boolean4 = true;
											PolygonalMap2.Square square2 = this.getSquare((int)pathFindRequest.startX, (int)pathFindRequest.startY, (int)pathFindRequest.startZ);
											if (square2 == null || square2.isReallySolid()) {
												boolean6 = false;
												boolean4 = false;
												break label1516;
											}

											PolygonalMap2.Node node;
											if (square2.has(504)) {
												searchNode = this.astar.getSearchNode(square2);
											} else {
												visibilityGraph5 = this.astar.getVisGraphForSquare(square2);
												if (visibilityGraph5 != null) {
													if (!visibilityGraph5.created) {
														visibilityGraph5.create();
													}

													node = null;
													int5 = visibilityGraph5.getPointOutsideObstacles(pathFindRequest.startX, pathFindRequest.startY, pathFindRequest.startZ, this.adjustStartData);
													if (int5 == -1) {
														boolean7 = false;
														boolean4 = false;
														break label1515;
													}

													if (int5 == 1) {
														boolean2 = true;
														node = this.adjustStartData.node;
														if (this.adjustStartData.isNodeNew) {
															visibilityGraph2 = visibilityGraph5;
														}
													}

													if (node == null) {
														node = PolygonalMap2.Node.alloc().init(pathFindRequest.startX, pathFindRequest.startY, (int)pathFindRequest.startZ);
														visibilityGraph5.addNode(node);
														visibilityGraph2 = visibilityGraph5;
													}

													searchNode = this.astar.getSearchNode(node);
												}
											}

											if (searchNode == null) {
												searchNode = this.astar.getSearchNode(square2);
											}

											if (!(pathFindRequest.targetX < 0.0F) && !(pathFindRequest.targetY < 0.0F) && this.getChunkFromSquarePos((int)pathFindRequest.targetX, (int)pathFindRequest.targetY) != null) {
												square2 = this.getSquare((int)pathFindRequest.targetX, (int)pathFindRequest.targetY, (int)pathFindRequest.targetZ);
												if (square2 == null || square2.isReallySolid()) {
													boolean6 = false;
													boolean4 = false;
													break label1512;
												}

												if (((int)pathFindRequest.startX != (int)pathFindRequest.targetX || (int)pathFindRequest.startY != (int)pathFindRequest.targetY || (int)pathFindRequest.startZ != (int)pathFindRequest.targetZ) && this.isBlockedInAllDirections((int)pathFindRequest.targetX, (int)pathFindRequest.targetY, (int)pathFindRequest.targetZ)) {
													boolean6 = false;
													boolean4 = false;
													break label1514;
												}

												if (square2.has(504)) {
													searchNode2 = this.astar.getSearchNode(square2);
												} else {
													visibilityGraph5 = this.astar.getVisGraphForSquare(square2);
													if (visibilityGraph5 != null) {
														if (!visibilityGraph5.created) {
															visibilityGraph5.create();
														}

														node = null;
														int5 = visibilityGraph5.getPointOutsideObstacles(pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ, this.adjustGoalData);
														if (int5 == -1) {
															boolean7 = false;
															boolean4 = false;
															break label1513;
														}

														if (int5 == 1) {
															boolean3 = true;
															node = this.adjustGoalData.node;
															if (this.adjustGoalData.isNodeNew) {
																visibilityGraph = visibilityGraph5;
															}
														}

														if (node == null) {
															node = PolygonalMap2.Node.alloc().init(pathFindRequest.targetX, pathFindRequest.targetY, (int)pathFindRequest.targetZ);
															visibilityGraph5.addNode(node);
															visibilityGraph = visibilityGraph5;
														}

														searchNode2 = this.astar.getSearchNode(node);
													} else {
														for (int3 = 0; int3 < this.graphs.size(); ++int3) {
															visibilityGraph5 = (PolygonalMap2.VisibilityGraph)this.graphs.get(int3);
															if (visibilityGraph5.contains(square2, 1)) {
																PolygonalMap2.Node node2 = this.getPointOutsideObjects(square2, pathFindRequest.targetX, pathFindRequest.targetY);
																visibilityGraph5.addNode(node2);
																if (node2.x != pathFindRequest.targetX || node2.y != pathFindRequest.targetY) {
																	boolean3 = true;
																	this.adjustGoalData.isNodeNew = false;
																}

																visibilityGraph = visibilityGraph5;
																searchNode2 = this.astar.getSearchNode(node2);
																break;
															}
														}
													}
												}

												if (searchNode2 == null) {
													searchNode2 = this.astar.getSearchNode(square2);
												}
											} else {
												searchNode2 = this.astar.getSearchNode((int)pathFindRequest.targetX, (int)pathFindRequest.targetY);
											}

											ArrayList arrayList = this.astar.shortestPath(pathFindRequest, searchNode, searchNode2);
											if (arrayList == null) {
												boolean4 = false;
												break label1517;
											}

											if (arrayList.size() == 1) {
												pathFindRequest.path.addNode(searchNode);
												if (!boolean3 && searchNode2.square != null && (float)searchNode2.square.x + 0.5F != pathFindRequest.targetX && (float)searchNode2.square.y + 0.5F != pathFindRequest.targetY) {
													pathFindRequest.path.addNode(pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ, 0);
												} else {
													pathFindRequest.path.addNode(searchNode2);
												}

												boolean5 = true;
												boolean4 = false;
												break label1511;
											}

											this.cleanPath(arrayList, pathFindRequest, boolean2, boolean3, searchNode2);
											if (pathFindRequest.mover instanceof IsoPlayer && !((IsoPlayer)pathFindRequest.mover).isNPC()) {
												this.smoothPath(pathFindRequest.path);
											}

											boolean8 = true;
											boolean4 = false;
										} finally {
											if (boolean4) {
												if (boolean1) {
													Iterator iterator4 = this.graphs.iterator();
													while (iterator4.hasNext()) {
														PolygonalMap2.VisibilityGraph visibilityGraph6 = (PolygonalMap2.VisibilityGraph)iterator4.next();
														visibilityGraph6.render();
													}
												}

												if (visibilityGraph2 != null) {
													visibilityGraph2.removeNode(searchNode.vgNode);
												}

												if (visibilityGraph != null) {
													visibilityGraph.removeNode(searchNode2.vgNode);
												}

												int int6;
												for (int6 = 0; int6 < this.astar.searchNodes.size(); ++int6) {
													((PolygonalMap2.SearchNode)this.astar.searchNodes.get(int6)).release();
												}

												PolygonalMap2.Edge edge3;
												if (boolean2 && this.adjustStartData.isNodeNew) {
													for (int6 = 0; int6 < this.adjustStartData.node.edges.size(); ++int6) {
														edge3 = (PolygonalMap2.Edge)this.adjustStartData.node.edges.get(int6);
														edge3.obstacle.unsplit(this.adjustStartData.node, edge3.edgeRing);
													}

													this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
												}

												if (boolean3 && this.adjustGoalData.isNodeNew) {
													for (int6 = 0; int6 < this.adjustGoalData.node.edges.size(); ++int6) {
														edge3 = (PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(int6);
														edge3.obstacle.unsplit(this.adjustGoalData.node, edge3.edgeRing);
													}

													this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
												}
											}
										}

										if (boolean1) {
											Iterator iterator5 = this.graphs.iterator();
											while (iterator5.hasNext()) {
												PolygonalMap2.VisibilityGraph visibilityGraph7 = (PolygonalMap2.VisibilityGraph)iterator5.next();
												visibilityGraph7.render();
											}
										}

										if (visibilityGraph2 != null) {
											visibilityGraph2.removeNode(searchNode.vgNode);
										}

										if (visibilityGraph != null) {
											visibilityGraph.removeNode(searchNode2.vgNode);
										}

										for (int5 = 0; int5 < this.astar.searchNodes.size(); ++int5) {
											((PolygonalMap2.SearchNode)this.astar.searchNodes.get(int5)).release();
										}

										PolygonalMap2.Edge edge4;
										if (boolean2 && this.adjustStartData.isNodeNew) {
											for (int5 = 0; int5 < this.adjustStartData.node.edges.size(); ++int5) {
												edge4 = (PolygonalMap2.Edge)this.adjustStartData.node.edges.get(int5);
												edge4.obstacle.unsplit(this.adjustStartData.node, edge4.edgeRing);
											}

											this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
										}

										if (boolean3 && this.adjustGoalData.isNodeNew) {
											for (int5 = 0; int5 < this.adjustGoalData.node.edges.size(); ++int5) {
												edge4 = (PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(int5);
												edge4.obstacle.unsplit(this.adjustGoalData.node, edge4.edgeRing);
											}

											this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
										}

										return boolean8;
									}

									if (boolean1) {
										Iterator iterator6 = this.graphs.iterator();
										while (iterator6.hasNext()) {
											visibilityGraph5 = (PolygonalMap2.VisibilityGraph)iterator6.next();
											visibilityGraph5.render();
										}
									}

									if (visibilityGraph2 != null) {
										visibilityGraph2.removeNode(searchNode.vgNode);
									}

									if (visibilityGraph != null) {
										visibilityGraph.removeNode(searchNode2.vgNode);
									}

									int int7;
									for (int7 = 0; int7 < this.astar.searchNodes.size(); ++int7) {
										((PolygonalMap2.SearchNode)this.astar.searchNodes.get(int7)).release();
									}

									PolygonalMap2.Edge edge5;
									if (boolean2 && this.adjustStartData.isNodeNew) {
										for (int7 = 0; int7 < this.adjustStartData.node.edges.size(); ++int7) {
											edge5 = (PolygonalMap2.Edge)this.adjustStartData.node.edges.get(int7);
											edge5.obstacle.unsplit(this.adjustStartData.node, edge5.edgeRing);
										}

										this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
									}

									if (boolean3 && this.adjustGoalData.isNodeNew) {
										for (int7 = 0; int7 < this.adjustGoalData.node.edges.size(); ++int7) {
											edge5 = (PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(int7);
											edge5.obstacle.unsplit(this.adjustGoalData.node, edge5.edgeRing);
										}

										this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
									}

									return false;
								}

								if (boolean1) {
									iterator2 = this.graphs.iterator();
									while (iterator2.hasNext()) {
										visibilityGraph3 = (PolygonalMap2.VisibilityGraph)iterator2.next();
										visibilityGraph3.render();
									}
								}

								if (visibilityGraph2 != null) {
									visibilityGraph2.removeNode(searchNode.vgNode);
								}

								if (visibilityGraph != null) {
									visibilityGraph.removeNode(searchNode2.vgNode);
								}

								for (int3 = 0; int3 < this.astar.searchNodes.size(); ++int3) {
									((PolygonalMap2.SearchNode)this.astar.searchNodes.get(int3)).release();
								}

								if (boolean2 && this.adjustStartData.isNodeNew) {
									for (int3 = 0; int3 < this.adjustStartData.node.edges.size(); ++int3) {
										edge = (PolygonalMap2.Edge)this.adjustStartData.node.edges.get(int3);
										edge.obstacle.unsplit(this.adjustStartData.node, edge.edgeRing);
									}

									this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
								}

								if (boolean3 && this.adjustGoalData.isNodeNew) {
									for (int3 = 0; int3 < this.adjustGoalData.node.edges.size(); ++int3) {
										edge = (PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(int3);
										edge.obstacle.unsplit(this.adjustGoalData.node, edge.edgeRing);
									}

									this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
								}

								return boolean6;
							}

							if (boolean1) {
								iterator3 = this.graphs.iterator();
								while (iterator3.hasNext()) {
									visibilityGraph4 = (PolygonalMap2.VisibilityGraph)iterator3.next();
									visibilityGraph4.render();
								}
							}

							if (visibilityGraph2 != null) {
								visibilityGraph2.removeNode(searchNode.vgNode);
							}

							if (visibilityGraph != null) {
								visibilityGraph.removeNode(searchNode2.vgNode);
							}

							for (int4 = 0; int4 < this.astar.searchNodes.size(); ++int4) {
								((PolygonalMap2.SearchNode)this.astar.searchNodes.get(int4)).release();
							}

							if (boolean2 && this.adjustStartData.isNodeNew) {
								for (int4 = 0; int4 < this.adjustStartData.node.edges.size(); ++int4) {
									edge2 = (PolygonalMap2.Edge)this.adjustStartData.node.edges.get(int4);
									edge2.obstacle.unsplit(this.adjustStartData.node, edge2.edgeRing);
								}

								this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
							}

							if (boolean3 && this.adjustGoalData.isNodeNew) {
								for (int4 = 0; int4 < this.adjustGoalData.node.edges.size(); ++int4) {
									edge2 = (PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(int4);
									edge2.obstacle.unsplit(this.adjustGoalData.node, edge2.edgeRing);
								}

								this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
							}

							return boolean7;
						}

						if (boolean1) {
							iterator2 = this.graphs.iterator();
							while (iterator2.hasNext()) {
								visibilityGraph3 = (PolygonalMap2.VisibilityGraph)iterator2.next();
								visibilityGraph3.render();
							}
						}

						if (visibilityGraph2 != null) {
							visibilityGraph2.removeNode(searchNode.vgNode);
						}

						if (visibilityGraph != null) {
							visibilityGraph.removeNode(searchNode2.vgNode);
						}

						for (int3 = 0; int3 < this.astar.searchNodes.size(); ++int3) {
							((PolygonalMap2.SearchNode)this.astar.searchNodes.get(int3)).release();
						}

						if (boolean2 && this.adjustStartData.isNodeNew) {
							for (int3 = 0; int3 < this.adjustStartData.node.edges.size(); ++int3) {
								edge = (PolygonalMap2.Edge)this.adjustStartData.node.edges.get(int3);
								edge.obstacle.unsplit(this.adjustStartData.node, edge.edgeRing);
							}

							this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
						}

						if (boolean3 && this.adjustGoalData.isNodeNew) {
							for (int3 = 0; int3 < this.adjustGoalData.node.edges.size(); ++int3) {
								edge = (PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(int3);
								edge.obstacle.unsplit(this.adjustGoalData.node, edge.edgeRing);
							}

							this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
						}

						return boolean6;
					}

					if (boolean1) {
						iterator3 = this.graphs.iterator();
						while (iterator3.hasNext()) {
							visibilityGraph4 = (PolygonalMap2.VisibilityGraph)iterator3.next();
							visibilityGraph4.render();
						}
					}

					if (visibilityGraph2 != null) {
						visibilityGraph2.removeNode(searchNode.vgNode);
					}

					if (visibilityGraph != null) {
						visibilityGraph.removeNode(searchNode2.vgNode);
					}

					for (int4 = 0; int4 < this.astar.searchNodes.size(); ++int4) {
						((PolygonalMap2.SearchNode)this.astar.searchNodes.get(int4)).release();
					}

					if (boolean2 && this.adjustStartData.isNodeNew) {
						for (int4 = 0; int4 < this.adjustStartData.node.edges.size(); ++int4) {
							edge2 = (PolygonalMap2.Edge)this.adjustStartData.node.edges.get(int4);
							edge2.obstacle.unsplit(this.adjustStartData.node, edge2.edgeRing);
						}

						this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
					}

					if (boolean3 && this.adjustGoalData.isNodeNew) {
						for (int4 = 0; int4 < this.adjustGoalData.node.edges.size(); ++int4) {
							edge2 = (PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(int4);
							edge2.obstacle.unsplit(this.adjustGoalData.node, edge2.edgeRing);
						}

						this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
					}

					return boolean7;
				}

				if (boolean1) {
					iterator2 = this.graphs.iterator();
					while (iterator2.hasNext()) {
						visibilityGraph3 = (PolygonalMap2.VisibilityGraph)iterator2.next();
						visibilityGraph3.render();
					}
				}

				if (visibilityGraph2 != null) {
					visibilityGraph2.removeNode(searchNode.vgNode);
				}

				if (visibilityGraph != null) {
					visibilityGraph.removeNode(searchNode2.vgNode);
				}

				for (int3 = 0; int3 < this.astar.searchNodes.size(); ++int3) {
					((PolygonalMap2.SearchNode)this.astar.searchNodes.get(int3)).release();
				}

				if (boolean2 && this.adjustStartData.isNodeNew) {
					for (int3 = 0; int3 < this.adjustStartData.node.edges.size(); ++int3) {
						edge = (PolygonalMap2.Edge)this.adjustStartData.node.edges.get(int3);
						edge.obstacle.unsplit(this.adjustStartData.node, edge.edgeRing);
					}

					this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
				}

				if (boolean3 && this.adjustGoalData.isNodeNew) {
					for (int3 = 0; int3 < this.adjustGoalData.node.edges.size(); ++int3) {
						edge = (PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(int3);
						edge.obstacle.unsplit(this.adjustGoalData.node, edge.edgeRing);
					}

					this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
				}

				return boolean6;
			}

			if (boolean1) {
				Iterator iterator7 = this.graphs.iterator();
				while (iterator7.hasNext()) {
					PolygonalMap2.VisibilityGraph visibilityGraph8 = (PolygonalMap2.VisibilityGraph)iterator7.next();
					visibilityGraph8.render();
				}
			}

			if (visibilityGraph2 != null) {
				visibilityGraph2.removeNode(searchNode.vgNode);
			}

			if (visibilityGraph != null) {
				visibilityGraph.removeNode(searchNode2.vgNode);
			}

			int int8;
			for (int8 = 0; int8 < this.astar.searchNodes.size(); ++int8) {
				((PolygonalMap2.SearchNode)this.astar.searchNodes.get(int8)).release();
			}

			PolygonalMap2.Edge edge6;
			if (boolean2 && this.adjustStartData.isNodeNew) {
				for (int8 = 0; int8 < this.adjustStartData.node.edges.size(); ++int8) {
					edge6 = (PolygonalMap2.Edge)this.adjustStartData.node.edges.get(int8);
					edge6.obstacle.unsplit(this.adjustStartData.node, edge6.edgeRing);
				}

				this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
			}

			if (boolean3 && this.adjustGoalData.isNodeNew) {
				for (int8 = 0; int8 < this.adjustGoalData.node.edges.size(); ++int8) {
					edge6 = (PolygonalMap2.Edge)this.adjustGoalData.node.edges.get(int8);
					edge6.obstacle.unsplit(this.adjustGoalData.node, edge6.edgeRing);
				}

				this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
			}

			return boolean5;
		}
	}

	private void cleanPath(ArrayList arrayList, PolygonalMap2.PathFindRequest pathFindRequest, boolean boolean1, boolean boolean2, PolygonalMap2.SearchNode searchNode) {
		boolean boolean3 = pathFindRequest.mover instanceof IsoPlayer && ((IsoPlayer)pathFindRequest.mover).isNPC();
		PolygonalMap2.Square square = null;
		int int1 = -123;
		int int2 = -123;
		for (int int3 = 0; int3 < arrayList.size(); ++int3) {
			PolygonalMap2.SearchNode searchNode2 = (PolygonalMap2.SearchNode)arrayList.get(int3);
			float float1 = searchNode2.getX();
			float float2 = searchNode2.getY();
			float float3 = searchNode2.getZ();
			int int4 = searchNode2.vgNode == null ? 0 : searchNode2.vgNode.flags;
			PolygonalMap2.Square square2 = searchNode2.square;
			boolean boolean4 = false;
			int int5;
			if (square2 != null && square != null && square2.z == square.z) {
				int int6 = square2.x - square.x;
				int5 = square2.y - square.y;
				if (int6 == int1 && int5 == int2) {
					if (pathFindRequest.path.nodes.size() > 1) {
						boolean4 = true;
						if (pathFindRequest.path.getLastNode().hasFlag(65536)) {
							boolean4 = false;
						}
					}

					if (int6 == 0 && int5 == -1 && square.has(16384)) {
						boolean4 = false;
					} else if (int6 == 0 && int5 == 1 && square2.has(16384)) {
						boolean4 = false;
					} else if (int6 == -1 && int5 == 0 && square.has(8192)) {
						boolean4 = false;
					} else if (int6 == 1 && int5 == 0 && square2.has(8192)) {
						boolean4 = false;
					}
				} else {
					int1 = int6;
					int2 = int5;
				}
			} else {
				int2 = -123;
				int1 = -123;
			}

			if (square2 != null) {
				square = square2;
			} else {
				square = null;
			}

			if (boolean3) {
				boolean4 = false;
			}

			PolygonalMap2.PathNode pathNode;
			if (boolean4) {
				pathNode = pathFindRequest.path.getLastNode();
				pathNode.x = (float)square2.x + 0.5F;
				pathNode.y = (float)square2.y + 0.5F;
			} else {
				if (pathFindRequest.path.nodes.size() > 1) {
					pathNode = pathFindRequest.path.getLastNode();
					if (Math.abs(pathNode.x - float1) < 0.01F && Math.abs(pathNode.y - float2) < 0.01F && Math.abs(pathNode.z - float3) < 0.01F) {
						pathNode.x = float1;
						pathNode.y = float2;
						pathNode.z = float3;
						continue;
					}
				}

				if (int3 > 0 && searchNode2.square != null) {
					PolygonalMap2.SearchNode searchNode3 = (PolygonalMap2.SearchNode)arrayList.get(int3 - 1);
					if (searchNode3.square != null) {
						int5 = searchNode2.square.x - searchNode3.square.x;
						int int7 = searchNode2.square.y - searchNode3.square.y;
						if (int5 == 0 && int7 == -1 && searchNode3.square.has(16384)) {
							int4 |= 65536;
						} else if (int5 == 0 && int7 == 1 && searchNode2.square.has(16384)) {
							int4 |= 65536;
						} else if (int5 == -1 && int7 == 0 && searchNode3.square.has(8192)) {
							int4 |= 65536;
						} else if (int5 == 1 && int7 == 0 && searchNode2.square.has(8192)) {
							int4 |= 65536;
						}
					}
				}

				pathFindRequest.path.addNode(float1, float2, float3, int4);
			}
		}

		if (pathFindRequest.mover instanceof IsoPlayer && !boolean3) {
			if (pathFindRequest.path.isEmpty()) {
				Object object = null;
			} else {
				pathFindRequest.path.getNode(0);
			}

			if (!boolean2 && searchNode.square != null && (double)IsoUtils.DistanceToSquared((float)searchNode.square.x + 0.5F, (float)searchNode.square.y + 0.5F, pathFindRequest.targetX, pathFindRequest.targetY) > 0.010000000000000002) {
				pathFindRequest.path.addNode(pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ, 0);
			}
		}

		PolygonalMap2.PathNode pathNode2 = null;
		for (int int8 = 0; int8 < pathFindRequest.path.nodes.size(); ++int8) {
			PolygonalMap2.PathNode pathNode3 = (PolygonalMap2.PathNode)pathFindRequest.path.nodes.get(int8);
			PolygonalMap2.PathNode pathNode4 = int8 < pathFindRequest.path.nodes.size() - 1 ? (PolygonalMap2.PathNode)pathFindRequest.path.nodes.get(int8 + 1) : null;
			if (pathNode3.hasFlag(1)) {
				boolean boolean5 = pathNode2 != null && pathNode2.hasFlag(2) || pathNode4 != null && pathNode4.hasFlag(2);
				if (!boolean5) {
					pathNode3.flags &= -4;
				}
			}

			pathNode2 = pathNode3;
		}
	}

	private void smoothPath(PolygonalMap2.Path path) {
		int int1 = 0;
		while (true) {
			while (int1 < path.nodes.size() - 2) {
				PolygonalMap2.PathNode pathNode = (PolygonalMap2.PathNode)path.nodes.get(int1);
				PolygonalMap2.PathNode pathNode2 = (PolygonalMap2.PathNode)path.nodes.get(int1 + 1);
				PolygonalMap2.PathNode pathNode3 = (PolygonalMap2.PathNode)path.nodes.get(int1 + 2);
				if ((int)pathNode.z == (int)pathNode2.z && (int)pathNode.z == (int)pathNode3.z) {
					if (!this.lcc.isNotClear(this, pathNode.x, pathNode.y, pathNode3.x, pathNode3.y, (int)pathNode.z, 20)) {
						path.nodes.remove(int1 + 1);
						path.nodePool.push(pathNode2);
					} else {
						++int1;
					}
				} else {
					++int1;
				}
			}

			return;
		}
	}

	float getApparentZ(IsoGridSquare square) {
		if (!square.Has(IsoObjectType.stairsTW) && !square.Has(IsoObjectType.stairsTN)) {
			if (!square.Has(IsoObjectType.stairsMW) && !square.Has(IsoObjectType.stairsMN)) {
				return !square.Has(IsoObjectType.stairsBW) && !square.Has(IsoObjectType.stairsBN) ? (float)square.z : (float)square.z + 0.25F;
			} else {
				return (float)square.z + 0.5F;
			}
		} else {
			return (float)square.z + 0.75F;
		}
	}

	public void render() {
		if (Core.bDebug) {
			boolean boolean1 = DebugOptions.instance.PathfindPathToMouseEnable.getValue() && !this.testRequest.done && IsoPlayer.getInstance().getPath2() == null;
			if (DebugOptions.instance.PolymapRenderClusters.getValue()) {
				synchronized (this.renderLock) {
					Iterator iterator = this.clusters.iterator();
					while (iterator.hasNext()) {
						PolygonalMap2.VehicleCluster vehicleCluster = (PolygonalMap2.VehicleCluster)iterator.next();
						Iterator iterator2 = vehicleCluster.rects.iterator();
						while (iterator2.hasNext()) {
							PolygonalMap2.VehicleRect vehicleRect = (PolygonalMap2.VehicleRect)iterator2.next();
							LineDrawer.addLine((float)vehicleRect.x, (float)vehicleRect.y, (float)vehicleRect.z, (float)vehicleRect.right(), (float)vehicleRect.bottom(), (float)vehicleRect.z, 0.0F, 0.0F, 1.0F, (String)null, false);
						}

						PolygonalMap2.VehicleRect vehicleRect2 = vehicleCluster.bounds();
						vehicleRect2.release();
					}

					if (!boolean1) {
						iterator = this.graphs.iterator();
						while (iterator.hasNext()) {
							PolygonalMap2.VisibilityGraph visibilityGraph = (PolygonalMap2.VisibilityGraph)iterator.next();
							visibilityGraph.render();
						}
					}
				}
			}

			float float1;
			float float2;
			int int1;
			float float3;
			float float4;
			if (DebugOptions.instance.PolymapRenderLineClearCollide.getValue()) {
				float1 = (float)Mouse.getX();
				float2 = (float)Mouse.getY();
				int1 = (int)IsoPlayer.getInstance().getZ();
				float3 = IsoUtils.XToIso(float1, float2, (float)int1);
				float4 = IsoUtils.YToIso(float1, float2, (float)int1);
				LineDrawer.addLine(IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, (float)int1, float3, float4, (float)int1, 1, 1, 1, (String)null);
				byte byte1 = 9;
				int int2 = byte1 | 2;
				if (this.lccMain.isNotClear(this, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, float3, float4, int1, (BaseVehicle)null, int2)) {
					Vector2f vector2f = this.resolveCollision(IsoPlayer.getInstance(), float3, float4, PolygonalMap2.L_render.vector2f);
					LineDrawer.addLine(vector2f.x - 0.05F, vector2f.y - 0.05F, (float)int1, vector2f.x + 0.05F, vector2f.y + 0.05F, (float)int1, 1.0F, 1.0F, 0.0F, (String)null, false);
				}
			}

			if (GameKeyboard.isKeyDown(209) && !GameKeyboard.wasKeyDown(209)) {
				this.testZ = Math.max(this.testZ - 1, 0);
			}

			if (GameKeyboard.isKeyDown(201) && !GameKeyboard.wasKeyDown(201)) {
				this.testZ = Math.min(this.testZ + 1, 7);
			}

			float float5;
			if (boolean1) {
				float1 = (float)Mouse.getX();
				float2 = (float)Mouse.getY();
				int1 = this.testZ;
				float3 = IsoUtils.XToIso(float1, float2, (float)int1);
				float4 = IsoUtils.YToIso(float1, float2, (float)int1);
				float5 = (float)int1;
				int int3;
				for (int3 = -1; int3 <= 2; ++int3) {
					LineDrawer.addLine((float)((int)float3 - 1), (float)((int)float4 + int3), (float)((int)float5), (float)((int)float3 + 2), (float)((int)float4 + int3), (float)((int)float5), 0.3F, 0.3F, 0.3F, (String)null, false);
				}

				for (int3 = -1; int3 <= 2; ++int3) {
					LineDrawer.addLine((float)((int)float3 + int3), (float)((int)float4 - 1), (float)((int)float5), (float)((int)float3 + int3), (float)((int)float4 + 2), (float)((int)float5), 0.3F, 0.3F, 0.3F, (String)null, false);
				}

				IsoGridSquare square;
				for (int3 = -1; int3 <= 1; ++int3) {
					for (int int4 = -1; int4 <= 1; ++int4) {
						float float6 = 0.3F;
						float float7 = 0.0F;
						float float8 = 0.0F;
						square = IsoWorld.instance.CurrentCell.getGridSquare((int)float3 + int4, (int)float4 + int3, (int)float5);
						if (square == null || square.isSolid() || square.isSolidTrans() || square.HasStairs()) {
							LineDrawer.addLine((float)((int)float3 + int4), (float)((int)float4 + int3), (float)((int)float5), (float)((int)float3 + int4 + 1), (float)((int)float4 + int3 + 1), (float)((int)float5), float6, float7, float8, (String)null, false);
						}
					}
				}

				if (int1 < (int)IsoPlayer.getInstance().getZ()) {
					LineDrawer.addLine((float)((int)float3), (float)((int)float4), (float)((int)float5), (float)((int)float3), (float)((int)float4), (float)((int)IsoPlayer.getInstance().getZ()), 0.3F, 0.3F, 0.3F, (String)null, true);
				} else if (int1 > (int)IsoPlayer.getInstance().getZ()) {
					LineDrawer.addLine((float)((int)float3), (float)((int)float4), (float)((int)float5), (float)((int)float3), (float)((int)float4), (float)((int)IsoPlayer.getInstance().getZ()), 0.3F, 0.3F, 0.3F, (String)null, true);
				}

				PolygonalMap2.PathFindRequest pathFindRequest = PolygonalMap2.PathFindRequest.alloc().init(this.testRequest, IsoPlayer.getInstance(), IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, IsoPlayer.getInstance().z, float3, float4, float5);
				if (DebugOptions.instance.PathfindPathToMouseAllowCrawl.getValue()) {
					pathFindRequest.bCanCrawl = true;
					if (DebugOptions.instance.PathfindPathToMouseIgnoreCrawlCost.getValue()) {
						pathFindRequest.bIgnoreCrawlCost = true;
					}
				}

				if (DebugOptions.instance.PathfindPathToMouseAllowThump.getValue()) {
					pathFindRequest.bCanThump = true;
				}

				this.testRequest.done = false;
				synchronized (this.renderLock) {
					boolean boolean2 = DebugOptions.instance.PolymapRenderClusters.getValue();
					if (this.findPath(pathFindRequest, boolean2) && !pathFindRequest.path.isEmpty()) {
						IsoGridSquare square2;
						float float9;
						float float10;
						PolygonalMap2.PathNode pathNode;
						for (int int5 = 0; int5 < pathFindRequest.path.nodes.size() - 1; ++int5) {
							pathNode = (PolygonalMap2.PathNode)pathFindRequest.path.nodes.get(int5);
							PolygonalMap2.PathNode pathNode2 = (PolygonalMap2.PathNode)pathFindRequest.path.nodes.get(int5 + 1);
							square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)pathNode.x, (double)pathNode.y, (double)pathNode.z);
							IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare((double)pathNode2.x, (double)pathNode2.y, (double)pathNode2.z);
							float9 = square2 == null ? pathNode.z : this.getApparentZ(square2);
							float10 = square3 == null ? pathNode2.z : this.getApparentZ(square3);
							float float11 = 1.0F;
							float float12 = 1.0F;
							float float13 = 0.0F;
							if (float9 != (float)((int)float9) || float10 != (float)((int)float10)) {
								float12 = 0.0F;
							}

							LineDrawer.addLine(pathNode.x, pathNode.y, float9, pathNode2.x, pathNode2.y, float10, float11, float12, float13, (String)null, true);
							LineDrawer.addRect(pathNode.x - 0.05F, pathNode.y - 0.05F, float9, 0.1F, 0.1F, float11, float12, float13);
						}

						PathFindBehavior2.closestPointOnPath(IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, IsoPlayer.getInstance().z, IsoPlayer.getInstance(), pathFindRequest.path, this.pointOnPath);
						PolygonalMap2.PathNode pathNode3 = (PolygonalMap2.PathNode)pathFindRequest.path.nodes.get(this.pointOnPath.pathIndex);
						pathNode = (PolygonalMap2.PathNode)pathFindRequest.path.nodes.get(this.pointOnPath.pathIndex + 1);
						square = IsoWorld.instance.CurrentCell.getGridSquare((double)pathNode3.x, (double)pathNode3.y, (double)pathNode3.z);
						square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)pathNode.x, (double)pathNode.y, (double)pathNode.z);
						float float14 = square == null ? pathNode3.z : this.getApparentZ(square);
						float9 = square2 == null ? pathNode.z : this.getApparentZ(square2);
						float10 = float14 + (float9 - float14) * this.pointOnPath.dist;
						LineDrawer.addLine(this.pointOnPath.x - 0.05F, this.pointOnPath.y - 0.05F, float10, this.pointOnPath.x + 0.05F, this.pointOnPath.y + 0.05F, float10, 0.0F, 1.0F, 0.0F, (String)null, true);
						LineDrawer.addLine(this.pointOnPath.x - 0.05F, this.pointOnPath.y + 0.05F, float10, this.pointOnPath.x + 0.05F, this.pointOnPath.y - 0.05F, float10, 0.0F, 1.0F, 0.0F, (String)null, true);
						if (GameKeyboard.isKeyDown(207) && !GameKeyboard.wasKeyDown(207)) {
							Object object = LuaManager.env.rawget("ISPathFindAction_pathToLocationF");
							if (object != null) {
								LuaManager.caller.pcall(LuaManager.thread, object, float3, float4, float5);
							}
						}
					}

					pathFindRequest.release();
				}
			} else {
				for (int int6 = 0; int6 < this.testRequest.path.nodes.size() - 1; ++int6) {
					PolygonalMap2.PathNode pathNode4 = (PolygonalMap2.PathNode)this.testRequest.path.nodes.get(int6);
					PolygonalMap2.PathNode pathNode5 = (PolygonalMap2.PathNode)this.testRequest.path.nodes.get(int6 + 1);
					float3 = 1.0F;
					float4 = 1.0F;
					float5 = 0.0F;
					if (pathNode4.z != (float)((int)pathNode4.z) || pathNode5.z != (float)((int)pathNode5.z)) {
						float4 = 0.0F;
					}

					LineDrawer.addLine(pathNode4.x, pathNode4.y, pathNode4.z, pathNode5.x, pathNode5.y, pathNode5.z, float3, float4, float5, (String)null, true);
				}

				this.testRequest.done = false;
			}

			if (DebugOptions.instance.PolymapRenderConnections.getValue()) {
				float1 = (float)Mouse.getX();
				float2 = (float)Mouse.getY();
				int1 = this.testZ;
				float3 = IsoUtils.XToIso(float1, float2, (float)int1);
				float4 = IsoUtils.YToIso(float1, float2, (float)int1);
				PolygonalMap2.VisibilityGraph visibilityGraph2 = this.getVisGraphAt(float3, float4, int1, 1);
				if (visibilityGraph2 != null) {
					PolygonalMap2.Node node = visibilityGraph2.getClosestNodeTo(float3, float4);
					if (node != null) {
						Iterator iterator3 = node.visible.iterator();
						while (iterator3.hasNext()) {
							PolygonalMap2.Connection connection = (PolygonalMap2.Connection)iterator3.next();
							PolygonalMap2.Node node2 = connection.otherNode(node);
							LineDrawer.addLine(node.x, node.y, (float)int1, node2.x, node2.y, (float)int1, 1.0F, 0.0F, 0.0F, (String)null, true);
						}
					}
				}
			}

			this.updateMain();
		}
	}

	public void squareChanged(IsoGridSquare square) {
		PolygonalMap2.SquareUpdateTask squareUpdateTask = PolygonalMap2.SquareUpdateTask.alloc().init(this, square);
		this.squareTaskQueue.add(squareUpdateTask);
		this.thread.wake();
	}

	public void addChunkToWorld(IsoChunk chunk) {
		PolygonalMap2.ChunkUpdateTask chunkUpdateTask = PolygonalMap2.ChunkUpdateTask.alloc().init(this, chunk);
		this.chunkTaskQueue.add(chunkUpdateTask);
		this.thread.wake();
	}

	public void removeChunkFromWorld(IsoChunk chunk) {
		if (this.thread != null) {
			PolygonalMap2.ChunkRemoveTask chunkRemoveTask = PolygonalMap2.ChunkRemoveTask.alloc().init(this, chunk);
			this.chunkTaskQueue.add(chunkRemoveTask);
			this.thread.wake();
		}
	}

	public void addVehicleToWorld(BaseVehicle baseVehicle) {
		PolygonalMap2.VehicleAddTask vehicleAddTask = PolygonalMap2.VehicleAddTask.alloc();
		vehicleAddTask.init(this, baseVehicle);
		this.vehicleTaskQueue.add(vehicleAddTask);
		PolygonalMap2.VehicleState vehicleState = PolygonalMap2.VehicleState.alloc().init(baseVehicle);
		this.vehicleState.put(baseVehicle, vehicleState);
		this.thread.wake();
	}

	public void updateVehicle(BaseVehicle baseVehicle) {
		PolygonalMap2.VehicleUpdateTask vehicleUpdateTask = PolygonalMap2.VehicleUpdateTask.alloc();
		vehicleUpdateTask.init(this, baseVehicle);
		this.vehicleTaskQueue.add(vehicleUpdateTask);
		this.thread.wake();
	}

	public void removeVehicleFromWorld(BaseVehicle baseVehicle) {
		if (this.thread != null) {
			PolygonalMap2.VehicleRemoveTask vehicleRemoveTask = PolygonalMap2.VehicleRemoveTask.alloc();
			vehicleRemoveTask.init(this, baseVehicle);
			this.vehicleTaskQueue.add(vehicleRemoveTask);
			PolygonalMap2.VehicleState vehicleState = (PolygonalMap2.VehicleState)this.vehicleState.remove(baseVehicle);
			if (vehicleState != null) {
				vehicleState.vehicle = null;
				vehicleState.release();
			}

			this.thread.wake();
		}
	}

	private PolygonalMap2.Cell getCellFromSquarePos(int int1, int int2) {
		int1 -= this.minX * 300;
		int2 -= this.minY * 300;
		if (int1 >= 0 && int2 >= 0) {
			int int3 = int1 / 300;
			int int4 = int2 / 300;
			return int3 < this.width && int4 < this.height ? this.cells[int3][int4] : null;
		} else {
			return null;
		}
	}

	private PolygonalMap2.Cell getCellFromChunkPos(int int1, int int2) {
		return this.getCellFromSquarePos(int1 * 10, int2 * 10);
	}

	private PolygonalMap2.Chunk allocChunkIfNeeded(int int1, int int2) {
		PolygonalMap2.Cell cell = this.getCellFromChunkPos(int1, int2);
		return cell == null ? null : cell.allocChunkIfNeeded(int1, int2);
	}

	private PolygonalMap2.Chunk getChunkFromChunkPos(int int1, int int2) {
		PolygonalMap2.Cell cell = this.getCellFromChunkPos(int1, int2);
		return cell == null ? null : cell.getChunkFromChunkPos(int1, int2);
	}

	private PolygonalMap2.Chunk getChunkFromSquarePos(int int1, int int2) {
		PolygonalMap2.Cell cell = this.getCellFromSquarePos(int1, int2);
		return cell == null ? null : cell.getChunkFromChunkPos(int1 / 10, int2 / 10);
	}

	private PolygonalMap2.Square getSquare(int int1, int int2, int int3) {
		PolygonalMap2.Chunk chunk = this.getChunkFromSquarePos(int1, int2);
		return chunk == null ? null : chunk.getSquare(int1, int2, int3);
	}

	private boolean isBlockedInAllDirections(int int1, int int2, int int3) {
		PolygonalMap2.Square square = this.getSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			PolygonalMap2.Square square2 = this.getSquare(int1, int2 - 1, int3);
			PolygonalMap2.Square square3 = this.getSquare(int1, int2 + 1, int3);
			PolygonalMap2.Square square4 = this.getSquare(int1 - 1, int2, int3);
			PolygonalMap2.Square square5 = this.getSquare(int1 + 1, int2, int3);
			boolean boolean1 = square2 != null && this.astar.canNotMoveBetween(square, square2, false);
			boolean boolean2 = square3 != null && this.astar.canNotMoveBetween(square, square3, false);
			boolean boolean3 = square4 != null && this.astar.canNotMoveBetween(square, square4, false);
			boolean boolean4 = square5 != null && this.astar.canNotMoveBetween(square, square5, false);
			return boolean1 && boolean2 && boolean3 && boolean4;
		}
	}

	public void init(IsoMetaGrid metaGrid) {
		this.minX = metaGrid.getMinX();
		this.minY = metaGrid.getMinY();
		this.width = metaGrid.getWidth();
		this.height = metaGrid.getHeight();
		this.cells = new PolygonalMap2.Cell[this.width][this.height];
		for (int int1 = 0; int1 < this.height; ++int1) {
			for (int int2 = 0; int2 < this.width; ++int2) {
				this.cells[int2][int1] = PolygonalMap2.Cell.alloc().init(this, this.minX + int2, this.minY + int1);
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
		while (this.thread.isAlive()) {
			try {
				Thread.sleep(5L);
			} catch (InterruptedException interruptedException) {
			}
		}

		int int1;
		for (int1 = 0; int1 < this.height; ++int1) {
			for (int int2 = 0; int2 < this.width; ++int2) {
				if (this.cells[int2][int1] != null) {
					this.cells[int2][int1].release();
				}
			}
		}

		for (PolygonalMap2.IChunkTask iChunkTask = (PolygonalMap2.IChunkTask)this.chunkTaskQueue.poll(); iChunkTask != null; iChunkTask = (PolygonalMap2.IChunkTask)this.chunkTaskQueue.poll()) {
			iChunkTask.release();
		}

		for (PolygonalMap2.SquareUpdateTask squareUpdateTask = (PolygonalMap2.SquareUpdateTask)this.squareTaskQueue.poll(); squareUpdateTask != null; squareUpdateTask = (PolygonalMap2.SquareUpdateTask)this.squareTaskQueue.poll()) {
			squareUpdateTask.release();
		}

		for (PolygonalMap2.IVehicleTask iVehicleTask = (PolygonalMap2.IVehicleTask)this.vehicleTaskQueue.poll(); iVehicleTask != null; iVehicleTask = (PolygonalMap2.IVehicleTask)this.vehicleTaskQueue.poll()) {
			iVehicleTask.release();
		}

		for (PolygonalMap2.PathRequestTask pathRequestTask = (PolygonalMap2.PathRequestTask)this.requestTaskQueue.poll(); pathRequestTask != null; pathRequestTask = (PolygonalMap2.PathRequestTask)this.requestTaskQueue.poll()) {
			pathRequestTask.release();
		}

		while (!this.requests.isEmpty()) {
			((PolygonalMap2.PathFindRequest)this.requests.removeLast()).release();
		}

		while (!this.requestToMain.isEmpty()) {
			((PolygonalMap2.PathFindRequest)this.requestToMain.remove()).release();
		}

		for (int1 = 0; int1 < this.vehicles.size(); ++int1) {
			PolygonalMap2.Vehicle vehicle = (PolygonalMap2.Vehicle)this.vehicles.get(int1);
			vehicle.release();
		}

		Iterator iterator = this.vehicleState.values().iterator();
		while (iterator.hasNext()) {
			PolygonalMap2.VehicleState vehicleState = (PolygonalMap2.VehicleState)iterator.next();
			vehicleState.release();
		}

		this.requestMap.clear();
		this.vehicles.clear();
		this.vehicleState.clear();
		this.vehicleMap.clear();
		this.cells = null;
		this.thread = null;
		this.rebuild = true;
	}

	public void updateMain() {
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getVehicles();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			BaseVehicle baseVehicle = (BaseVehicle)arrayList.get(int1);
			PolygonalMap2.VehicleState vehicleState = (PolygonalMap2.VehicleState)this.vehicleState.get(baseVehicle);
			if (vehicleState != null && vehicleState.check()) {
				this.updateVehicle(baseVehicle);
			}
		}

		for (PolygonalMap2.PathFindRequest pathFindRequest = (PolygonalMap2.PathFindRequest)this.requestToMain.poll(); pathFindRequest != null; pathFindRequest = (PolygonalMap2.PathFindRequest)this.requestToMain.poll()) {
			if (this.requestMap.get(pathFindRequest.mover) == pathFindRequest) {
				this.requestMap.remove(pathFindRequest.mover);
			}

			if (!pathFindRequest.cancel) {
				if (pathFindRequest.path.isEmpty()) {
					pathFindRequest.finder.Failed(pathFindRequest.mover);
				} else {
					pathFindRequest.finder.Succeeded(pathFindRequest.path, pathFindRequest.mover);
				}
			}

			pathFindRequest.release();
		}
	}

	public void updateThread() {
		for (PolygonalMap2.IChunkTask iChunkTask = (PolygonalMap2.IChunkTask)this.chunkTaskQueue.poll(); iChunkTask != null; iChunkTask = (PolygonalMap2.IChunkTask)this.chunkTaskQueue.poll()) {
			iChunkTask.execute();
			iChunkTask.release();
			this.rebuild = true;
		}

		for (PolygonalMap2.SquareUpdateTask squareUpdateTask = (PolygonalMap2.SquareUpdateTask)this.squareTaskQueue.poll(); squareUpdateTask != null; squareUpdateTask = (PolygonalMap2.SquareUpdateTask)this.squareTaskQueue.poll()) {
			squareUpdateTask.execute();
			squareUpdateTask.release();
		}

		for (PolygonalMap2.IVehicleTask iVehicleTask = (PolygonalMap2.IVehicleTask)this.vehicleTaskQueue.poll(); iVehicleTask != null; iVehicleTask = (PolygonalMap2.IVehicleTask)this.vehicleTaskQueue.poll()) {
			iVehicleTask.execute();
			iVehicleTask.release();
			this.rebuild = true;
		}

		for (PolygonalMap2.PathRequestTask pathRequestTask = (PolygonalMap2.PathRequestTask)this.requestTaskQueue.poll(); pathRequestTask != null; pathRequestTask = (PolygonalMap2.PathRequestTask)this.requestTaskQueue.poll()) {
			pathRequestTask.execute();
			pathRequestTask.release();
		}

		int int1;
		if (this.rebuild) {
			for (int1 = 0; int1 < this.graphs.size(); ++int1) {
				PolygonalMap2.VisibilityGraph visibilityGraph = (PolygonalMap2.VisibilityGraph)this.graphs.get(int1);
				visibilityGraph.release();
			}

			this.squareToNode.forEachValue(this.releaseNodeProc);
			this.createVisibilityGraphs();
			this.rebuild = false;
			++PolygonalMap2.ChunkDataZ.EPOCH;
		}

		int1 = 2;
		while (!this.requests.isEmpty()) {
			PolygonalMap2.PathFindRequest pathFindRequest = (PolygonalMap2.PathFindRequest)this.requests.removeFirst();
			if (pathFindRequest.cancel) {
				this.requestToMain.add(pathFindRequest);
			} else {
				try {
					this.findPath(pathFindRequest, false);
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}

				if (!pathFindRequest.targetXYZ.isEmpty()) {
					this.shortestPath.copyFrom(pathFindRequest.path);
					float float1 = pathFindRequest.targetX;
					float float2 = pathFindRequest.targetY;
					float float3 = pathFindRequest.targetZ;
					float float4 = this.shortestPath.isEmpty() ? Float.MAX_VALUE : this.shortestPath.length();
					for (int int2 = 0; int2 < pathFindRequest.targetXYZ.size(); int2 += 3) {
						pathFindRequest.targetX = pathFindRequest.targetXYZ.get(int2);
						pathFindRequest.targetY = pathFindRequest.targetXYZ.get(int2 + 1);
						pathFindRequest.targetZ = pathFindRequest.targetXYZ.get(int2 + 2);
						pathFindRequest.path.clear();
						this.findPath(pathFindRequest, false);
						if (!pathFindRequest.path.isEmpty()) {
							float float5 = pathFindRequest.path.length();
							if (float5 < float4) {
								float4 = float5;
								this.shortestPath.copyFrom(pathFindRequest.path);
								float1 = pathFindRequest.targetX;
								float2 = pathFindRequest.targetY;
								float3 = pathFindRequest.targetZ;
							}
						}
					}

					pathFindRequest.path.copyFrom(this.shortestPath);
					pathFindRequest.targetX = float1;
					pathFindRequest.targetY = float2;
					pathFindRequest.targetZ = float3;
				}

				this.requestToMain.add(pathFindRequest);
				--int1;
				if (int1 == 0) {
					break;
				}
			}
		}
	}

	public PolygonalMap2.PathFindRequest addRequest(PolygonalMap2.IPathfinder iPathfinder, Mover mover, float float1, float float2, float float3, float float4, float float5, float float6) {
		this.cancelRequest(mover);
		PolygonalMap2.PathFindRequest pathFindRequest = PolygonalMap2.PathFindRequest.alloc().init(iPathfinder, mover, float1, float2, float3, float4, float5, float6);
		this.requestMap.put(mover, pathFindRequest);
		PolygonalMap2.PathRequestTask pathRequestTask = PolygonalMap2.PathRequestTask.alloc().init(this, pathFindRequest);
		this.requestTaskQueue.add(pathRequestTask);
		this.thread.wake();
		return pathFindRequest;
	}

	public void cancelRequest(Mover mover) {
		PolygonalMap2.PathFindRequest pathFindRequest = (PolygonalMap2.PathFindRequest)this.requestMap.remove(mover);
		if (pathFindRequest != null) {
			pathFindRequest.cancel = true;
		}
	}

	public ArrayList getPointInLine(float float1, float float2, float float3, float float4, int int1) {
		PolygonalMap2.PointPool pointPool = new PolygonalMap2.PointPool();
		ArrayList arrayList = new ArrayList();
		this.supercover(float1, float2, float3, float4, int1, pointPool, arrayList);
		return arrayList;
	}

	private void supercover(float float1, float float2, float float3, float float4, int int1, PolygonalMap2.PointPool pointPool, ArrayList arrayList) {
		double double1 = (double)Math.abs(float3 - float1);
		double double2 = (double)Math.abs(float4 - float2);
		int int2 = (int)Math.floor((double)float1);
		int int3 = (int)Math.floor((double)float2);
		int int4 = 1;
		byte byte1;
		double double3;
		if (double1 == 0.0) {
			byte1 = 0;
			double3 = Double.POSITIVE_INFINITY;
		} else if (float3 > float1) {
			byte1 = 1;
			int4 += (int)Math.floor((double)float3) - int2;
			double3 = (Math.floor((double)float1) + 1.0 - (double)float1) * double2;
		} else {
			byte1 = -1;
			int4 += int2 - (int)Math.floor((double)float3);
			double3 = ((double)float1 - Math.floor((double)float1)) * double2;
		}

		byte byte2;
		if (double2 == 0.0) {
			byte2 = 0;
			double3 -= Double.POSITIVE_INFINITY;
		} else if (float4 > float2) {
			byte2 = 1;
			int4 += (int)Math.floor((double)float4) - int3;
			double3 -= (Math.floor((double)float2) + 1.0 - (double)float2) * double1;
		} else {
			byte2 = -1;
			int4 += int3 - (int)Math.floor((double)float4);
			double3 -= ((double)float2 - Math.floor((double)float2)) * double1;
		}

		for (; int4 > 0; --int4) {
			PolygonalMap2.Point point = pointPool.alloc().init(int2, int3);
			if (arrayList.contains(point)) {
				pointPool.release(point);
			} else {
				arrayList.add(point);
			}

			if (double3 > 0.0) {
				int3 += byte2;
				double3 -= double1;
			} else {
				int2 += byte1;
				double3 += double2;
			}
		}
	}

	public boolean lineClearCollide(float float1, float float2, float float3, float float4, int int1) {
		return this.lineClearCollide(float1, float2, float3, float4, int1, (IsoMovingObject)null);
	}

	public boolean lineClearCollide(float float1, float float2, float float3, float float4, int int1, IsoMovingObject movingObject) {
		return this.lineClearCollide(float1, float2, float3, float4, int1, movingObject, true, true);
	}

	public boolean lineClearCollide(float float1, float float2, float float3, float float4, int int1, IsoMovingObject movingObject, boolean boolean1, boolean boolean2) {
		int int2 = 0;
		if (boolean1) {
			int2 |= 1;
		}

		if (boolean2) {
			int2 |= 2;
		}

		if (Core.bDebug && DebugOptions.instance.PolymapRenderLineClearCollide.getValue()) {
			int2 |= 8;
		}

		return this.lineClearCollide(float1, float2, float3, float4, int1, movingObject, int2);
	}

	public boolean lineClearCollide(float float1, float float2, float float3, float float4, int int1, IsoMovingObject movingObject, int int2) {
		BaseVehicle baseVehicle = null;
		if (movingObject instanceof IsoGameCharacter) {
			baseVehicle = ((IsoGameCharacter)movingObject).getVehicle();
		} else if (movingObject instanceof BaseVehicle) {
			baseVehicle = (BaseVehicle)movingObject;
		}

		return this.lccMain.isNotClear(this, float1, float2, float3, float4, int1, baseVehicle, int2);
	}

	public Vector2 getCollidepoint(float float1, float float2, float float3, float float4, int int1, IsoMovingObject movingObject, int int2) {
		BaseVehicle baseVehicle = null;
		if (movingObject instanceof IsoGameCharacter) {
			baseVehicle = ((IsoGameCharacter)movingObject).getVehicle();
		} else if (movingObject instanceof BaseVehicle) {
			baseVehicle = (BaseVehicle)movingObject;
		}

		return this.lccMain.getCollidepoint(this, float1, float2, float3, float4, int1, baseVehicle, int2);
	}

	public boolean canStandAt(float float1, float float2, int int1, IsoMovingObject movingObject, boolean boolean1, boolean boolean2) {
		BaseVehicle baseVehicle = null;
		if (movingObject instanceof IsoGameCharacter) {
			baseVehicle = ((IsoGameCharacter)movingObject).getVehicle();
		} else if (movingObject instanceof BaseVehicle) {
			baseVehicle = (BaseVehicle)movingObject;
		}

		int int2 = 0;
		if (boolean1) {
			int2 |= 1;
		}

		if (boolean2) {
			int2 |= 2;
		}

		if (Core.bDebug && DebugOptions.instance.PolymapRenderLineClearCollide.getValue()) {
			int2 |= 8;
		}

		return this.canStandAt(float1, float2, int1, baseVehicle, int2);
	}

	public boolean canStandAt(float float1, float float2, int int1, BaseVehicle baseVehicle, int int2) {
		return this.lccMain.canStandAtOld(this, float1, float2, (float)int1, baseVehicle, int2);
	}

	public boolean intersectLineWithVehicle(float float1, float float2, float float3, float float4, BaseVehicle baseVehicle, Vector2 vector2) {
		if (baseVehicle != null && baseVehicle.getScript() != null) {
			float[] floatArray = this.tempFloats;
			floatArray[0] = baseVehicle.getPoly().x1;
			floatArray[1] = baseVehicle.getPoly().y1;
			floatArray[2] = baseVehicle.getPoly().x2;
			floatArray[3] = baseVehicle.getPoly().y2;
			floatArray[4] = baseVehicle.getPoly().x3;
			floatArray[5] = baseVehicle.getPoly().y3;
			floatArray[6] = baseVehicle.getPoly().x4;
			floatArray[7] = baseVehicle.getPoly().y4;
			float float5 = Float.MAX_VALUE;
			for (int int1 = 0; int1 < 8; int1 += 2) {
				float float6 = floatArray[int1 % 8];
				float float7 = floatArray[(int1 + 1) % 8];
				float float8 = floatArray[(int1 + 2) % 8];
				float float9 = floatArray[(int1 + 3) % 8];
				double double1 = (double)((float9 - float7) * (float3 - float1) - (float8 - float6) * (float4 - float2));
				if (double1 == 0.0) {
					return false;
				}

				double double2 = (double)((float8 - float6) * (float2 - float7) - (float9 - float7) * (float1 - float6)) / double1;
				double double3 = (double)((float3 - float1) * (float2 - float7) - (float4 - float2) * (float1 - float6)) / double1;
				if (double2 >= 0.0 && double2 <= 1.0 && double3 >= 0.0 && double3 <= 1.0) {
					float float10 = (float)((double)float1 + double2 * (double)(float3 - float1));
					float float11 = (float)((double)float2 + double2 * (double)(float4 - float2));
					float float12 = IsoUtils.DistanceTo(float1, float2, float10, float11);
					if (float12 < float5) {
						vector2.set(float10, float11);
						float5 = float12;
					}
				}
			}

			return float5 < Float.MAX_VALUE;
		} else {
			return false;
		}
	}

	public Vector2f resolveCollision(IsoGameCharacter gameCharacter, float float1, float float2, Vector2f vector2f) {
		return this.collideWithObstacles.resolveCollision(gameCharacter, float1, float2, vector2f);
	}

	private static final class ClosestPointOnEdge {
		PolygonalMap2.Edge edge;
		PolygonalMap2.Node node;
		final Vector2f point = new Vector2f();
		double distSq;
	}

	private static final class AdjustStartEndNodeData {
		PolygonalMap2.Obstacle obstacle;
		PolygonalMap2.Node node;
		PolygonalMap2.Edge newEdge;
		boolean isNodeNew;
		PolygonalMap2.VisibilityGraph graph;
	}

	private static final class LineClearCollide {
		final Vector2 perp = new Vector2();
		final ArrayList pts = new ArrayList();
		final PolygonalMap2.VehicleRect sweepAABB = new PolygonalMap2.VehicleRect();
		final PolygonalMap2.VehicleRect vehicleAABB = new PolygonalMap2.VehicleRect();
		final Vector2[] polyVec = new Vector2[4];
		final Vector2[] vehicleVec = new Vector2[4];
		final PolygonalMap2.PointPool pointPool = new PolygonalMap2.PointPool();
		final PolygonalMap2.LiangBarsky LB = new PolygonalMap2.LiangBarsky();

		LineClearCollide() {
			for (int int1 = 0; int1 < 4; ++int1) {
				this.polyVec[int1] = new Vector2();
				this.vehicleVec[int1] = new Vector2();
			}
		}

		private float clamp(float float1, float float2, float float3) {
			if (float1 < float2) {
				float1 = float2;
			}

			if (float1 > float3) {
				float1 = float3;
			}

			return float1;
		}

		@Deprecated
		boolean canStandAt(PolygonalMap2 polygonalMap2, float float1, float float2, float float3, float float4, float float5, PolygonalMap2.Vehicle vehicle) {
			if (((int)float1 != (int)float3 || (int)float2 != (int)float4) && polygonalMap2.isBlockedInAllDirections((int)float3, (int)float4, (int)float5)) {
				return false;
			} else {
				int int1 = (int)Math.floor((double)(float3 - 0.3F));
				int int2 = (int)Math.floor((double)(float4 - 0.3F));
				int int3 = (int)Math.ceil((double)(float3 + 0.3F));
				int int4 = (int)Math.ceil((double)(float4 + 0.3F));
				int int5;
				for (int5 = int2; int5 < int4; ++int5) {
					for (int int6 = int1; int6 < int3; ++int6) {
						PolygonalMap2.Square square = polygonalMap2.getSquare(int6, int5, (int)float5);
						boolean boolean1 = float3 >= (float)int6 && float4 >= (float)int5 && float3 < (float)(int6 + 1) && float4 < (float)(int5 + 1);
						boolean boolean2 = false;
						if (!boolean1 && square != null && square.has(448)) {
							boolean2 = float3 < (float)square.x || float3 >= (float)(square.x + 1) || square.has(64) && float4 < (float)square.y;
						} else if (!boolean1 && square != null && square.has(56)) {
							boolean2 = float4 < (float)square.y || float4 >= (float)(square.y + 1) || square.has(8) && float3 < (float)square.x;
						}

						if ((square == null || square.isReallySolid() || boolean2 || !square.has(512)) && boolean1) {
							return false;
						}
					}
				}

				for (int5 = 0; int5 < polygonalMap2.vehicles.size(); ++int5) {
					PolygonalMap2.Vehicle vehicle2 = (PolygonalMap2.Vehicle)polygonalMap2.vehicles.get(int5);
					if (vehicle2 != vehicle && (int)vehicle2.polyPlusRadius.z == (int)float5 && vehicle2.polyPlusRadius.containsPoint(float3, float4)) {
						return false;
					}
				}

				return true;
			}
		}

		boolean canStandAtClipper(PolygonalMap2 polygonalMap2, float float1, float float2, float float3, float float4, float float5, PolygonalMap2.Vehicle vehicle, int int1) {
			if (((int)float1 != (int)float3 || (int)float2 != (int)float4) && polygonalMap2.isBlockedInAllDirections((int)float3, (int)float4, (int)float5)) {
				return false;
			} else {
				PolygonalMap2.Chunk chunk = polygonalMap2.getChunkFromSquarePos((int)float3, (int)float4);
				if (chunk == null) {
					return false;
				} else {
					PolygonalMap2.ChunkDataZ chunkDataZ = chunk.collision.init(chunk, (int)float5);
					for (int int2 = 0; int2 < chunkDataZ.obstacles.size(); ++int2) {
						PolygonalMap2.Obstacle obstacle = (PolygonalMap2.Obstacle)chunkDataZ.obstacles.get(int2);
						if ((vehicle == null || obstacle.vehicle != vehicle) && obstacle.bounds.containsPoint(float3, float4) && obstacle.isPointInside(float3, float4, int1)) {
							return false;
						}
					}

					return true;
				}
			}
		}

		float isLeft(float float1, float float2, float float3, float float4, float float5, float float6) {
			return (float3 - float1) * (float6 - float2) - (float5 - float1) * (float4 - float2);
		}

		boolean isPointInPolygon_WindingNumber(float float1, float float2, PolygonalMap2.VehiclePoly vehiclePoly) {
			this.polyVec[0].set(vehiclePoly.x1, vehiclePoly.y1);
			this.polyVec[1].set(vehiclePoly.x2, vehiclePoly.y2);
			this.polyVec[2].set(vehiclePoly.x3, vehiclePoly.y3);
			this.polyVec[3].set(vehiclePoly.x4, vehiclePoly.y4);
			int int1 = 0;
			for (int int2 = 0; int2 < 4; ++int2) {
				Vector2 vector2 = this.polyVec[int2];
				Vector2 vector22 = int2 == 3 ? this.polyVec[0] : this.polyVec[int2 + 1];
				if (vector2.y <= float2) {
					if (vector22.y > float2 && this.isLeft(vector2.x, vector2.y, vector22.x, vector22.y, float1, float2) > 0.0F) {
						++int1;
					}
				} else if (vector22.y <= float2 && this.isLeft(vector2.x, vector2.y, vector22.x, vector22.y, float1, float2) < 0.0F) {
					--int1;
				}
			}

			return int1 != 0;
		}

		@Deprecated
		boolean isNotClearOld(PolygonalMap2 polygonalMap2, float float1, float float2, float float3, float float4, int int1, int int2) {
			boolean boolean1 = (int2 & 4) != 0;
			PolygonalMap2.Square square = polygonalMap2.getSquare((int)float1, (int)float2, int1);
			if (square != null && square.has(504)) {
				return true;
			} else if (!this.canStandAt(polygonalMap2, float1, float2, float3, float4, (float)int1, (PolygonalMap2.Vehicle)null)) {
				return true;
			} else {
				float float5 = float4 - float2;
				float float6 = -(float3 - float1);
				this.perp.set(float5, float6);
				this.perp.normalize();
				float float7 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
				float float8 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
				float float9 = float3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
				float float10 = float4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
				this.perp.set(-float5, -float6);
				this.perp.normalize();
				float float11 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
				float float12 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
				float float13 = float3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
				float float14 = float4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
				int int3;
				for (int3 = 0; int3 < this.pts.size(); ++int3) {
					this.pointPool.release((PolygonalMap2.Point)this.pts.get(int3));
				}

				this.pts.clear();
				this.pts.add(this.pointPool.alloc().init((int)float1, (int)float2));
				if ((int)float1 != (int)float3 || (int)float2 != (int)float4) {
					this.pts.add(this.pointPool.alloc().init((int)float3, (int)float4));
				}

				polygonalMap2.supercover(float7, float8, float9, float10, int1, this.pointPool, this.pts);
				polygonalMap2.supercover(float11, float12, float13, float14, int1, this.pointPool, this.pts);
				float float15;
				float float16;
				float float17;
				for (int3 = 0; int3 < this.pts.size(); ++int3) {
					PolygonalMap2.Point point = (PolygonalMap2.Point)this.pts.get(int3);
					square = polygonalMap2.getSquare(point.x, point.y, int1);
					if (boolean1 && square != null && square.cost > 0) {
						return true;
					}

					float float18;
					if (square != null && !square.isReallySolid() && !square.has(504) && square.has(512)) {
						if (square.isCollideW()) {
							float15 = 0.3F;
							float16 = 0.3F;
							float17 = 0.3F;
							float18 = 0.3F;
							if (float1 < (float)point.x && float3 < (float)point.x) {
								float15 = 0.0F;
							} else if (float1 >= (float)point.x && float3 >= (float)point.x) {
								float17 = 0.0F;
							}

							if (float2 < (float)point.y && float4 < (float)point.y) {
								float16 = 0.0F;
							} else if (float2 >= (float)(point.y + 1) && float4 >= (float)(point.y + 1)) {
								float18 = 0.0F;
							}

							if (this.LB.lineRectIntersect(float1, float2, float3 - float1, float4 - float2, (float)point.x - float15, (float)point.y - float16, (float)point.x + float17, (float)point.y + 1.0F + float18)) {
								return true;
							}
						}

						if (square.isCollideN()) {
							float15 = 0.3F;
							float16 = 0.3F;
							float17 = 0.3F;
							float18 = 0.3F;
							if (float1 < (float)point.x && float3 < (float)point.x) {
								float15 = 0.0F;
							} else if (float1 >= (float)(point.x + 1) && float3 >= (float)(point.x + 1)) {
								float17 = 0.0F;
							}

							if (float2 < (float)point.y && float4 < (float)point.y) {
								float16 = 0.0F;
							} else if (float2 >= (float)point.y && float4 >= (float)point.y) {
								float18 = 0.0F;
							}

							if (this.LB.lineRectIntersect(float1, float2, float3 - float1, float4 - float2, (float)point.x - float15, (float)point.y - float16, (float)point.x + 1.0F + float17, (float)point.y + float18)) {
								return true;
							}
						}
					} else {
						float15 = 0.3F;
						float16 = 0.3F;
						float17 = 0.3F;
						float18 = 0.3F;
						if (float1 < (float)point.x && float3 < (float)point.x) {
							float15 = 0.0F;
						} else if (float1 >= (float)(point.x + 1) && float3 >= (float)(point.x + 1)) {
							float17 = 0.0F;
						}

						if (float2 < (float)point.y && float4 < (float)point.y) {
							float16 = 0.0F;
						} else if (float2 >= (float)(point.y + 1) && float4 >= (float)(point.y + 1)) {
							float18 = 0.0F;
						}

						if (this.LB.lineRectIntersect(float1, float2, float3 - float1, float4 - float2, (float)point.x - float15, (float)point.y - float16, (float)point.x + 1.0F + float17, (float)point.y + 1.0F + float18)) {
							return true;
						}
					}
				}

				float float19 = BaseVehicle.PLUS_RADIUS;
				this.perp.set(float5, float6);
				this.perp.normalize();
				float7 = float1 + this.perp.x * float19;
				float8 = float2 + this.perp.y * float19;
				float9 = float3 + this.perp.x * float19;
				float10 = float4 + this.perp.y * float19;
				this.perp.set(-float5, -float6);
				this.perp.normalize();
				float11 = float1 + this.perp.x * float19;
				float12 = float2 + this.perp.y * float19;
				float13 = float3 + this.perp.x * float19;
				float14 = float4 + this.perp.y * float19;
				float float20 = Math.min(float7, Math.min(float9, Math.min(float11, float13)));
				float15 = Math.min(float8, Math.min(float10, Math.min(float12, float14)));
				float16 = Math.max(float7, Math.max(float9, Math.max(float11, float13)));
				float17 = Math.max(float8, Math.max(float10, Math.max(float12, float14)));
				this.sweepAABB.init((int)float20, (int)float15, (int)Math.ceil((double)float16) - (int)float20, (int)Math.ceil((double)float17) - (int)float15, int1);
				this.polyVec[0].set(float7, float8);
				this.polyVec[1].set(float9, float10);
				this.polyVec[2].set(float13, float14);
				this.polyVec[3].set(float11, float12);
				for (int int4 = 0; int4 < polygonalMap2.vehicles.size(); ++int4) {
					PolygonalMap2.Vehicle vehicle = (PolygonalMap2.Vehicle)polygonalMap2.vehicles.get(int4);
					PolygonalMap2.VehicleRect vehicleRect = vehicle.poly.getAABB(this.vehicleAABB);
					if (vehicleRect.intersects(this.sweepAABB) && this.polyVehicleIntersect(vehicle.poly)) {
						return true;
					}
				}

				return false;
			}
		}

		boolean isNotClearClipper(PolygonalMap2 polygonalMap2, float float1, float float2, float float3, float float4, int int1, int int2) {
			boolean boolean1 = (int2 & 4) != 0;
			PolygonalMap2.Square square = polygonalMap2.getSquare((int)float1, (int)float2, int1);
			if (square != null && square.has(504)) {
				return true;
			} else if (!this.canStandAtClipper(polygonalMap2, float1, float2, float3, float4, (float)int1, (PolygonalMap2.Vehicle)null, int2)) {
				return true;
			} else {
				float float5 = float1 / 10.0F;
				float float6 = float2 / 10.0F;
				float float7 = float3 / 10.0F;
				float float8 = float4 / 10.0F;
				double double1 = (double)Math.abs(float7 - float5);
				double double2 = (double)Math.abs(float8 - float6);
				int int3 = (int)Math.floor((double)float5);
				int int4 = (int)Math.floor((double)float6);
				int int5 = 1;
				byte byte1;
				double double3;
				if (double1 == 0.0) {
					byte1 = 0;
					double3 = Double.POSITIVE_INFINITY;
				} else if (float7 > float5) {
					byte1 = 1;
					int5 += (int)Math.floor((double)float7) - int3;
					double3 = (Math.floor((double)float5) + 1.0 - (double)float5) * double2;
				} else {
					byte1 = -1;
					int5 += int3 - (int)Math.floor((double)float7);
					double3 = ((double)float5 - Math.floor((double)float5)) * double2;
				}

				byte byte2;
				if (double2 == 0.0) {
					byte2 = 0;
					double3 -= Double.POSITIVE_INFINITY;
				} else if (float8 > float6) {
					byte2 = 1;
					int5 += (int)Math.floor((double)float8) - int4;
					double3 -= (Math.floor((double)float6) + 1.0 - (double)float6) * double1;
				} else {
					byte2 = -1;
					int5 += int4 - (int)Math.floor((double)float8);
					double3 -= ((double)float6 - Math.floor((double)float6)) * double1;
				}

				for (; int5 > 0; --int5) {
					PolygonalMap2.Chunk chunk = PolygonalMap2.instance.getChunkFromChunkPos(int3, int4);
					if (chunk != null) {
						PolygonalMap2.ChunkDataZ chunkDataZ = chunk.collision.init(chunk, int1);
						ArrayList arrayList = chunkDataZ.obstacles;
						for (int int6 = 0; int6 < arrayList.size(); ++int6) {
							PolygonalMap2.Obstacle obstacle = (PolygonalMap2.Obstacle)arrayList.get(int6);
							if (obstacle.lineSegmentIntersects(float1, float2, float3, float4)) {
								return true;
							}
						}
					}

					if (double3 > 0.0) {
						int4 += byte2;
						double3 -= double1;
					} else {
						int3 += byte1;
						double3 += double2;
					}
				}

				if (boolean1 && this.isNotClearCost(float1, float2, float3, float4, int1)) {
					return true;
				} else {
					return false;
				}
			}
		}

		boolean isNotClearCost(float float1, float float2, float float3, float float4, int int1) {
			float float5 = float4 - float2;
			float float6 = -(float3 - float1);
			this.perp.set(float5, float6);
			this.perp.normalize();
			float float7 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
			float float8 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
			float float9 = float3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
			float float10 = float4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
			this.perp.set(-float5, -float6);
			this.perp.normalize();
			float float11 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
			float float12 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
			float float13 = float3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
			float float14 = float4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
			int int2;
			for (int2 = 0; int2 < this.pts.size(); ++int2) {
				this.pointPool.release((PolygonalMap2.Point)this.pts.get(int2));
			}

			this.pts.clear();
			this.pts.add(this.pointPool.alloc().init((int)float1, (int)float2));
			if ((int)float1 != (int)float3 || (int)float2 != (int)float4) {
				this.pts.add(this.pointPool.alloc().init((int)float3, (int)float4));
			}

			PolygonalMap2.instance.supercover(float7, float8, float9, float10, int1, this.pointPool, this.pts);
			PolygonalMap2.instance.supercover(float11, float12, float13, float14, int1, this.pointPool, this.pts);
			for (int2 = 0; int2 < this.pts.size(); ++int2) {
				PolygonalMap2.Point point = (PolygonalMap2.Point)this.pts.get(int2);
				PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(point.x, point.y, int1);
				if (square != null && square.cost > 0) {
					return true;
				}
			}

			return false;
		}

		boolean isNotClear(PolygonalMap2 polygonalMap2, float float1, float float2, float float3, float float4, int int1, int int2) {
			return this.isNotClearOld(polygonalMap2, float1, float2, float3, float4, int1, int2);
		}

		boolean polyVehicleIntersect(PolygonalMap2.VehiclePoly vehiclePoly) {
			this.vehicleVec[0].set(vehiclePoly.x1, vehiclePoly.y1);
			this.vehicleVec[1].set(vehiclePoly.x2, vehiclePoly.y2);
			this.vehicleVec[2].set(vehiclePoly.x3, vehiclePoly.y3);
			this.vehicleVec[3].set(vehiclePoly.x4, vehiclePoly.y4);
			boolean boolean1 = false;
			for (int int1 = 0; int1 < 4; ++int1) {
				Vector2 vector2 = this.polyVec[int1];
				Vector2 vector22 = int1 == 3 ? this.polyVec[0] : this.polyVec[int1 + 1];
				for (int int2 = 0; int2 < 4; ++int2) {
					Vector2 vector23 = this.vehicleVec[int2];
					Vector2 vector24 = int2 == 3 ? this.vehicleVec[0] : this.vehicleVec[int2 + 1];
					if (Line2D.linesIntersect((double)vector2.x, (double)vector2.y, (double)vector22.x, (double)vector22.y, (double)vector23.x, (double)vector23.y, (double)vector24.x, (double)vector24.y)) {
						boolean1 = true;
					}
				}
			}

			return boolean1;
		}
	}

	private static final class VGAStar extends AStar {
		ArrayList graphs;
		final ArrayList searchNodes = new ArrayList();
		final TIntObjectHashMap nodeMap = new TIntObjectHashMap();
		final PolygonalMap2.GoalNode goalNode = new PolygonalMap2.GoalNode();
		final TIntObjectHashMap squareToNode = new TIntObjectHashMap();
		Mover mover;
		boolean bCanCrawl;
		boolean bIgnoreCrawlCost;
		boolean bCanThump;
		final TIntObjectHashMap knownBlockedEdges = new TIntObjectHashMap();
		final PolygonalMap2.VGAStar.InitProc initProc = new PolygonalMap2.VGAStar.InitProc();

		PolygonalMap2.VGAStar init(ArrayList arrayList, TIntObjectHashMap tIntObjectHashMap) {
			this.setMaxSteps(5000);
			this.graphs = arrayList;
			this.searchNodes.clear();
			this.nodeMap.clear();
			this.squareToNode.clear();
			this.mover = null;
			tIntObjectHashMap.forEachEntry(this.initProc);
			return this;
		}

		PolygonalMap2.VisibilityGraph getVisGraphForSquare(PolygonalMap2.Square square) {
			for (int int1 = 0; int1 < this.graphs.size(); ++int1) {
				PolygonalMap2.VisibilityGraph visibilityGraph = (PolygonalMap2.VisibilityGraph)this.graphs.get(int1);
				if (visibilityGraph.contains(square)) {
					return visibilityGraph;
				}
			}

			return null;
		}

		boolean isSquareInCluster(PolygonalMap2.Square square) {
			return this.getVisGraphForSquare(square) != null;
		}

		PolygonalMap2.SearchNode getSearchNode(PolygonalMap2.Node node) {
			if (node.square != null) {
				return this.getSearchNode(node.square);
			} else {
				PolygonalMap2.SearchNode searchNode = (PolygonalMap2.SearchNode)this.nodeMap.get(node.ID);
				if (searchNode == null) {
					searchNode = PolygonalMap2.SearchNode.alloc().init(this, node);
					this.searchNodes.add(searchNode);
					this.nodeMap.put(node.ID, searchNode);
				}

				return searchNode;
			}
		}

		PolygonalMap2.SearchNode getSearchNode(PolygonalMap2.Square square) {
			PolygonalMap2.SearchNode searchNode = (PolygonalMap2.SearchNode)this.squareToNode.get(square.ID);
			if (searchNode == null) {
				searchNode = PolygonalMap2.SearchNode.alloc().init(this, square);
				this.searchNodes.add(searchNode);
				this.squareToNode.put(square.ID, searchNode);
			}

			return searchNode;
		}

		PolygonalMap2.SearchNode getSearchNode(int int1, int int2) {
			PolygonalMap2.SearchNode searchNode = PolygonalMap2.SearchNode.alloc().init(this, int1, int2);
			this.searchNodes.add(searchNode);
			return searchNode;
		}

		ArrayList shortestPath(PolygonalMap2.PathFindRequest pathFindRequest, PolygonalMap2.SearchNode searchNode, PolygonalMap2.SearchNode searchNode2) {
			this.mover = pathFindRequest.mover;
			this.bCanCrawl = pathFindRequest.bCanCrawl;
			this.bIgnoreCrawlCost = pathFindRequest.bIgnoreCrawlCost;
			this.bCanThump = pathFindRequest.bCanThump;
			this.goalNode.init(searchNode2);
			return this.shortestPath(searchNode, this.goalNode);
		}

		boolean canNotMoveBetween(PolygonalMap2.Square square, PolygonalMap2.Square square2, boolean boolean1) {
			assert Math.abs(square.x - square2.x) <= 1;
			assert Math.abs(square.y - square2.y) <= 1;
			assert square.z == square2.z;
			assert square != square2;
			boolean boolean2;
			if (square.x == 10921 && square.y == 10137 && square2.x == square.x - 1 && square2.y == square.y) {
				boolean2 = true;
			}

			boolean2 = square2.x < square.x;
			boolean boolean3 = square2.x > square.x;
			boolean boolean4 = square2.y < square.y;
			boolean boolean5 = square2.y > square.y;
			if (!square2.isNonThumpableSolid() && (this.bCanThump || !square2.isReallySolid())) {
				if (square2.y < square.y && square.has(64)) {
					return true;
				} else if (square2.x < square.x && square.has(8)) {
					return true;
				} else if (square2.y > square.y && square2.x == square.x && square2.has(64)) {
					return true;
				} else if (square2.x > square.x && square2.y == square.y && square2.has(8)) {
					return true;
				} else if (square2.x != square.x && square2.has(448)) {
					return true;
				} else if (square2.y != square.y && square2.has(56)) {
					return true;
				} else if (square2.x != square.x && square.has(448)) {
					return true;
				} else if (square2.y != square.y && square.has(56)) {
					return true;
				} else if (!square2.has(512) && !square2.has(504)) {
					return true;
				} else if (this.isKnownBlocked(square, square2)) {
					return true;
				} else {
					boolean boolean6;
					if (square.x == 11920 && square2.y == 6803 && square2.has(131072)) {
						boolean6 = true;
					}

					boolean6 = square.isCanPathN() && (this.bCanThump || !square.isThumpN());
					boolean boolean7 = square.isCanPathW() && (this.bCanThump || !square.isThumpW());
					boolean boolean8 = boolean4 && square.isCollideN() && (square.x != square2.x || boolean1 || !boolean6);
					boolean boolean9 = boolean2 && square.isCollideW() && (square.y != square2.y || boolean1 || !boolean7);
					boolean6 = square2.isCanPathN() && (this.bCanThump || !square2.isThumpN());
					boolean7 = square2.isCanPathW() && (this.bCanThump || !square2.isThumpW());
					boolean boolean10 = boolean5 && square2.has(131076) && (square.x != square2.x || boolean1 || !boolean6);
					boolean boolean11 = boolean3 && square2.has(131074) && (square.y != square2.y || boolean1 || !boolean7);
					if (!boolean8 && !boolean9 && !boolean10 && !boolean11) {
						boolean boolean12 = square2.x != square.x && square2.y != square.y;
						if (boolean12) {
							PolygonalMap2.Square square3 = PolygonalMap2.instance.getSquare(square.x, square2.y, square.z);
							PolygonalMap2.Square square4 = PolygonalMap2.instance.getSquare(square2.x, square.y, square.z);
							assert square3 != square && square3 != square2;
							assert square4 != square && square4 != square2;
							if (square2.x == square.x + 1 && square2.y == square.y + 1 && square3 != null && square4 != null) {
								if (square3.has(4096) && square4.has(2048)) {
									return true;
								}

								if (square3.isThumpN() && square4.isThumpW()) {
									return true;
								}
							}

							if (square2.x == square.x - 1 && square2.y == square.y - 1 && square3 != null && square4 != null) {
								if (square3.has(2048) && square4.has(4096)) {
									return true;
								}

								if (square3.isThumpW() && square4.isThumpN()) {
									return true;
								}
							}

							if (square3 != null && this.canNotMoveBetween(square, square3, true)) {
								return true;
							}

							if (square4 != null && this.canNotMoveBetween(square, square4, true)) {
								return true;
							}

							if (square3 != null && this.canNotMoveBetween(square2, square3, true)) {
								return true;
							}

							if (square4 != null && this.canNotMoveBetween(square2, square4, true)) {
								return true;
							}
						}

						return false;
					} else {
						return true;
					}
				}
			} else {
				return true;
			}
		}

		boolean isKnownBlocked(PolygonalMap2.Square square, PolygonalMap2.Square square2) {
			if (square.z != square2.z) {
				return false;
			} else {
				KnownBlockedEdges knownBlockedEdges = (KnownBlockedEdges)this.knownBlockedEdges.get(square.ID);
				KnownBlockedEdges knownBlockedEdges2 = (KnownBlockedEdges)this.knownBlockedEdges.get(square2.ID);
				if (knownBlockedEdges != null && knownBlockedEdges.isBlocked(square2.x, square2.y)) {
					return true;
				} else {
					return knownBlockedEdges2 != null && knownBlockedEdges2.isBlocked(square.x, square.y);
				}
			}
		}

		final class InitProc implements TIntObjectProcedure {

			public boolean execute(int int1, PolygonalMap2.Node node) {
				PolygonalMap2.SearchNode searchNode = PolygonalMap2.SearchNode.alloc().init(VGAStar.this, (PolygonalMap2.Node)node);
				searchNode.square = node.square;
				VGAStar.this.squareToNode.put(int1, searchNode);
				VGAStar.this.nodeMap.put(node.ID, searchNode);
				VGAStar.this.searchNodes.add(searchNode);
				return true;
			}
		}
	}

	private static final class TestRequest implements PolygonalMap2.IPathfinder {
		final PolygonalMap2.Path path = new PolygonalMap2.Path();
		boolean done;

		public void Succeeded(PolygonalMap2.Path path, Mover mover) {
			this.path.copyFrom(path);
			this.done = true;
		}

		public void Failed(Mover mover) {
			this.path.clear();
			this.done = true;
		}
	}

	public static final class Path {
		final ArrayList nodes = new ArrayList();
		final ArrayDeque nodePool = new ArrayDeque();

		void clear() {
			for (int int1 = 0; int1 < this.nodes.size(); ++int1) {
				this.nodePool.push((PolygonalMap2.PathNode)this.nodes.get(int1));
			}

			this.nodes.clear();
		}

		boolean isEmpty() {
			return this.nodes.isEmpty();
		}

		PolygonalMap2.PathNode addNode(float float1, float float2, float float3) {
			return this.addNode(float1, float2, float3, 0);
		}

		PolygonalMap2.PathNode addNode(float float1, float float2, float float3, int int1) {
			PolygonalMap2.PathNode pathNode = this.nodePool.isEmpty() ? new PolygonalMap2.PathNode() : (PolygonalMap2.PathNode)this.nodePool.pop();
			pathNode.init(float1, float2, float3, int1);
			this.nodes.add(pathNode);
			return pathNode;
		}

		PolygonalMap2.PathNode addNode(PolygonalMap2.SearchNode searchNode) {
			return this.addNode(searchNode.getX(), searchNode.getY(), searchNode.getZ(), searchNode.vgNode == null ? 0 : searchNode.vgNode.flags);
		}

		PolygonalMap2.PathNode getNode(int int1) {
			return (PolygonalMap2.PathNode)this.nodes.get(int1);
		}

		PolygonalMap2.PathNode getLastNode() {
			return (PolygonalMap2.PathNode)this.nodes.get(this.nodes.size() - 1);
		}

		void copyFrom(PolygonalMap2.Path path) {
			assert this != path;
			this.clear();
			for (int int1 = 0; int1 < path.nodes.size(); ++int1) {
				PolygonalMap2.PathNode pathNode = (PolygonalMap2.PathNode)path.nodes.get(int1);
				this.addNode(pathNode.x, pathNode.y, pathNode.z, pathNode.flags);
			}
		}

		float length() {
			float float1 = 0.0F;
			for (int int1 = 0; int1 < this.nodes.size() - 1; ++int1) {
				PolygonalMap2.PathNode pathNode = (PolygonalMap2.PathNode)this.nodes.get(int1);
				PolygonalMap2.PathNode pathNode2 = (PolygonalMap2.PathNode)this.nodes.get(int1 + 1);
				float1 += IsoUtils.DistanceTo(pathNode.x, pathNode.y, pathNode.z, pathNode2.x, pathNode2.y, pathNode2.z);
			}

			return float1;
		}

		public boolean crossesSquare(int int1, int int2, int int3) {
			for (int int4 = 0; int4 < this.nodes.size() - 1; ++int4) {
				PolygonalMap2.PathNode pathNode = (PolygonalMap2.PathNode)this.nodes.get(int4);
				PolygonalMap2.PathNode pathNode2 = (PolygonalMap2.PathNode)this.nodes.get(int4 + 1);
				if ((int)pathNode.z == int3 || (int)pathNode2.z == int3) {
					if (Line2D.linesIntersect((double)pathNode.x, (double)pathNode.y, (double)pathNode2.x, (double)pathNode2.y, (double)int1, (double)int2, (double)(int1 + 1), (double)int2)) {
						return true;
					}

					if (Line2D.linesIntersect((double)pathNode.x, (double)pathNode.y, (double)pathNode2.x, (double)pathNode2.y, (double)(int1 + 1), (double)int2, (double)(int1 + 1), (double)(int2 + 1))) {
						return true;
					}

					if (Line2D.linesIntersect((double)pathNode.x, (double)pathNode.y, (double)pathNode2.x, (double)pathNode2.y, (double)(int1 + 1), (double)(int2 + 1), (double)int1, (double)(int2 + 1))) {
						return true;
					}

					if (Line2D.linesIntersect((double)pathNode.x, (double)pathNode.y, (double)pathNode2.x, (double)pathNode2.y, (double)int1, (double)(int2 + 1), (double)int1, (double)int2)) {
						return true;
					}
				}
			}

			return false;
		}
	}

	private static final class Sync {
		private int fps = 20;
		private long period;
		private long excess;
		private long beforeTime;
		private long overSleepTime;

		private Sync() {
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
			long long1 = System.nanoTime();
			long long2 = long1 - this.beforeTime;
			long long3 = this.period - long2 - this.overSleepTime;
			if (long3 > 0L) {
				try {
					Thread.sleep(long3 / 1000000L);
				} catch (InterruptedException interruptedException) {
				}

				this.overSleepTime = System.nanoTime() - long1 - long3;
			} else {
				this.excess -= long3;
				this.overSleepTime = 0L;
			}

			this.beforeTime = System.nanoTime();
		}
	}

	private static final class LineClearCollideMain {
		final Vector2 perp = new Vector2();
		final ArrayList pts = new ArrayList();
		final PolygonalMap2.VehicleRect sweepAABB = new PolygonalMap2.VehicleRect();
		final PolygonalMap2.VehicleRect vehicleAABB = new PolygonalMap2.VehicleRect();
		final PolygonalMap2.VehiclePoly vehiclePoly = new PolygonalMap2.VehiclePoly();
		final Vector2[] polyVec = new Vector2[4];
		final Vector2[] vehicleVec = new Vector2[4];
		final PolygonalMap2.PointPool pointPool = new PolygonalMap2.PointPool();
		final PolygonalMap2.LiangBarsky LB = new PolygonalMap2.LiangBarsky();

		LineClearCollideMain() {
			for (int int1 = 0; int1 < 4; ++int1) {
				this.polyVec[int1] = new Vector2();
				this.vehicleVec[int1] = new Vector2();
			}
		}

		private float clamp(float float1, float float2, float float3) {
			if (float1 < float2) {
				float1 = float2;
			}

			if (float1 > float3) {
				float1 = float3;
			}

			return float1;
		}

		@Deprecated
		boolean canStandAtOld(PolygonalMap2 polygonalMap2, float float1, float float2, float float3, BaseVehicle baseVehicle, int int1) {
			boolean boolean1 = (int1 & 1) != 0;
			boolean boolean2 = (int1 & 2) != 0;
			int int2 = (int)Math.floor((double)(float1 - 0.3F));
			int int3 = (int)Math.floor((double)(float2 - 0.3F));
			int int4 = (int)Math.ceil((double)(float1 + 0.3F));
			int int5 = (int)Math.ceil((double)(float2 + 0.3F));
			int int6;
			int int7;
			for (int6 = int3; int6 < int5; ++int6) {
				for (int7 = int2; int7 < int4; ++int7) {
					boolean boolean3 = float1 >= (float)int7 && float2 >= (float)int6 && float1 < (float)(int7 + 1) && float2 < (float)(int6 + 1);
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int7, int6, (int)float3);
					boolean boolean4 = false;
					if (!boolean3 && square != null && square.HasStairsNorth()) {
						boolean4 = float1 < (float)square.x || float1 >= (float)(square.x + 1) || square.Has(IsoObjectType.stairsTN) && float2 < (float)square.y;
					} else if (!boolean3 && square != null && square.HasStairsWest()) {
						boolean4 = float2 < (float)square.y || float2 >= (float)(square.y + 1) || square.Has(IsoObjectType.stairsTW) && float1 < (float)square.x;
					}

					float float4;
					float float5;
					float float6;
					float float7;
					float float8;
					if (square != null && !square.isSolid() && (!square.isSolidTrans() || square.isAdjacentToWindow()) && !boolean4) {
						label241: {
							if (square.SolidFloorCached) {
								if (!square.SolidFloor) {
									break label241;
								}
							} else if (!square.TreatAsSolidFloor()) {
								break label241;
							}

							if (boolean2) {
								continue;
							}

							if (square.Is(IsoFlagType.collideW) || !boolean1 && square.hasBlockedDoor(false)) {
								float4 = (float)int7;
								float5 = this.clamp(float2, (float)int6, (float)(int6 + 1));
								float6 = float1 - float4;
								float7 = float2 - float5;
								float8 = float6 * float6 + float7 * float7;
								if (float8 < 0.09F) {
									return false;
								}
							}

							if (!square.Is(IsoFlagType.collideN) && (boolean1 || !square.hasBlockedDoor(true))) {
								continue;
							}

							float4 = this.clamp(float1, (float)int7, (float)(int7 + 1));
							float5 = (float)int6;
							float6 = float1 - float4;
							float7 = float2 - float5;
							float8 = float6 * float6 + float7 * float7;
							if (float8 < 0.09F) {
								return false;
							}

							continue;
						}
					}

					if (boolean2) {
						if (boolean3) {
							return false;
						}
					} else {
						float4 = this.clamp(float1, (float)int7, (float)(int7 + 1));
						float5 = this.clamp(float2, (float)int6, (float)(int6 + 1));
						float6 = float1 - float4;
						float7 = float2 - float5;
						float8 = float6 * float6 + float7 * float7;
						if (float8 < 0.09F) {
							return false;
						}
					}
				}
			}

			int6 = ((int)float1 - 4) / 10 - 1;
			int7 = ((int)float2 - 4) / 10 - 1;
			int int8 = (int)Math.ceil((double)((float1 + 4.0F) / 10.0F)) + 1;
			int int9 = (int)Math.ceil((double)((float2 + 4.0F) / 10.0F)) + 1;
			for (int int10 = int7; int10 < int9; ++int10) {
				for (int int11 = int6; int11 < int8; ++int11) {
					IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int11, int10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int11 * 10, int10 * 10, 0);
					if (chunk != null) {
						for (int int12 = 0; int12 < chunk.vehicles.size(); ++int12) {
							BaseVehicle baseVehicle2 = (BaseVehicle)chunk.vehicles.get(int12);
							if (baseVehicle2 != baseVehicle && baseVehicle2.addedToWorld && (int)baseVehicle2.z == (int)float3 && baseVehicle2.getPolyPlusRadius().containsPoint(float1, float2)) {
								return false;
							}
						}
					}
				}
			}

			return true;
		}

		boolean canStandAtClipper(PolygonalMap2 polygonalMap2, float float1, float float2, float float3, BaseVehicle baseVehicle, int int1) {
			return PolygonalMap2.instance.collideWithObstaclesPoly.canStandAt(float1, float2, float3, baseVehicle, int1);
		}

		public void drawCircle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
			LineDrawer.DrawIsoCircle(float1, float2, float3, float4, 16, float5, float6, float7, float8);
		}

		boolean isNotClearOld(PolygonalMap2 polygonalMap2, float float1, float float2, float float3, float float4, int int1, BaseVehicle baseVehicle, int int2) {
			boolean boolean1 = (int2 & 1) != 0;
			boolean boolean2 = (int2 & 2) != 0;
			boolean boolean3 = (int2 & 4) != 0;
			boolean boolean4 = (int2 & 8) != 0;
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1, (int)float2, int1);
			if (square != null && square.HasStairs()) {
				return !square.isSameStaircase((int)float3, (int)float4, int1);
			} else if (!this.canStandAtOld(polygonalMap2, float3, float4, (float)int1, baseVehicle, int2)) {
				if (boolean4) {
					this.drawCircle(float3, float4, (float)int1, 0.3F, 1.0F, 0.0F, 0.0F, 1.0F);
				}

				return true;
			} else {
				float float5 = float4 - float2;
				float float6 = -(float3 - float1);
				this.perp.set(float5, float6);
				this.perp.normalize();
				float float7 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
				float float8 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
				float float9 = float3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
				float float10 = float4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
				this.perp.set(-float5, -float6);
				this.perp.normalize();
				float float11 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
				float float12 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
				float float13 = float3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
				float float14 = float4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
				int int3;
				for (int3 = 0; int3 < this.pts.size(); ++int3) {
					this.pointPool.release((PolygonalMap2.Point)this.pts.get(int3));
				}

				this.pts.clear();
				this.pts.add(this.pointPool.alloc().init((int)float1, (int)float2));
				if ((int)float1 != (int)float3 || (int)float2 != (int)float4) {
					this.pts.add(this.pointPool.alloc().init((int)float3, (int)float4));
				}

				polygonalMap2.supercover(float7, float8, float9, float10, int1, this.pointPool, this.pts);
				polygonalMap2.supercover(float11, float12, float13, float14, int1, this.pointPool, this.pts);
				if (boolean4) {
					for (int3 = 0; int3 < this.pts.size(); ++int3) {
						PolygonalMap2.Point point = (PolygonalMap2.Point)this.pts.get(int3);
						LineDrawer.addLine((float)point.x, (float)point.y, (float)int1, (float)point.x + 1.0F, (float)point.y + 1.0F, (float)int1, 1.0F, 1.0F, 0.0F, (String)null, false);
					}
				}

				boolean boolean5 = false;
				float float15;
				float float16;
				float float17;
				for (int int4 = 0; int4 < this.pts.size(); ++int4) {
					PolygonalMap2.Point point2 = (PolygonalMap2.Point)this.pts.get(int4);
					square = IsoWorld.instance.CurrentCell.getGridSquare(point2.x, point2.y, int1);
					if (boolean3 && square != null && PolygonalMap2.SquareUpdateTask.getCost(square) > 0) {
						return true;
					}

					float float18;
					if (square != null && !square.isSolid() && (!square.isSolidTrans() || square.isAdjacentToWindow()) && !square.HasStairs()) {
						label311: {
							if (square.SolidFloorCached) {
								if (!square.SolidFloor) {
									break label311;
								}
							} else if (!square.TreatAsSolidFloor()) {
								break label311;
							}

							if (square.Is(IsoFlagType.collideW) || !boolean1 && square.hasBlockedDoor(false)) {
								float15 = 0.3F;
								float16 = 0.3F;
								float17 = 0.3F;
								float18 = 0.3F;
								if (float1 < (float)point2.x && float3 < (float)point2.x) {
									float15 = 0.0F;
								} else if (float1 >= (float)point2.x && float3 >= (float)point2.x) {
									float17 = 0.0F;
								}

								if (float2 < (float)point2.y && float4 < (float)point2.y) {
									float16 = 0.0F;
								} else if (float2 >= (float)(point2.y + 1) && float4 >= (float)(point2.y + 1)) {
									float18 = 0.0F;
								}

								if (this.LB.lineRectIntersect(float1, float2, float3 - float1, float4 - float2, (float)point2.x - float15, (float)point2.y - float16, (float)point2.x + float17, (float)point2.y + 1.0F + float18)) {
									if (!boolean4) {
										return true;
									}

									LineDrawer.addLine((float)point2.x - float15, (float)point2.y - float16, (float)int1, (float)point2.x + float17, (float)point2.y + 1.0F + float18, (float)int1, 1.0F, 0.0F, 0.0F, (String)null, false);
									boolean5 = true;
								}
							}

							if (!square.Is(IsoFlagType.collideN) && (boolean1 || !square.hasBlockedDoor(true))) {
								continue;
							}

							float15 = 0.3F;
							float16 = 0.3F;
							float17 = 0.3F;
							float18 = 0.3F;
							if (float1 < (float)point2.x && float3 < (float)point2.x) {
								float15 = 0.0F;
							} else if (float1 >= (float)(point2.x + 1) && float3 >= (float)(point2.x + 1)) {
								float17 = 0.0F;
							}

							if (float2 < (float)point2.y && float4 < (float)point2.y) {
								float16 = 0.0F;
							} else if (float2 >= (float)point2.y && float4 >= (float)point2.y) {
								float18 = 0.0F;
							}

							if (this.LB.lineRectIntersect(float1, float2, float3 - float1, float4 - float2, (float)point2.x - float15, (float)point2.y - float16, (float)point2.x + 1.0F + float17, (float)point2.y + float18)) {
								if (!boolean4) {
									return true;
								}

								LineDrawer.addLine((float)point2.x - float15, (float)point2.y - float16, (float)int1, (float)point2.x + 1.0F + float17, (float)point2.y + float18, (float)int1, 1.0F, 0.0F, 0.0F, (String)null, false);
								boolean5 = true;
							}

							continue;
						}
					}

					float15 = 0.3F;
					float16 = 0.3F;
					float17 = 0.3F;
					float18 = 0.3F;
					if (float1 < (float)point2.x && float3 < (float)point2.x) {
						float15 = 0.0F;
					} else if (float1 >= (float)(point2.x + 1) && float3 >= (float)(point2.x + 1)) {
						float17 = 0.0F;
					}

					if (float2 < (float)point2.y && float4 < (float)point2.y) {
						float16 = 0.0F;
					} else if (float2 >= (float)(point2.y + 1) && float4 >= (float)(point2.y + 1)) {
						float18 = 0.0F;
					}

					if (this.LB.lineRectIntersect(float1, float2, float3 - float1, float4 - float2, (float)point2.x - float15, (float)point2.y - float16, (float)point2.x + 1.0F + float17, (float)point2.y + 1.0F + float18)) {
						if (!boolean4) {
							return true;
						}

						LineDrawer.addLine((float)point2.x - float15, (float)point2.y - float16, (float)int1, (float)point2.x + 1.0F + float17, (float)point2.y + 1.0F + float18, (float)int1, 1.0F, 0.0F, 0.0F, (String)null, false);
						boolean5 = true;
					}
				}

				float float19 = BaseVehicle.PLUS_RADIUS;
				this.perp.set(float5, float6);
				this.perp.normalize();
				float7 = float1 + this.perp.x * float19;
				float8 = float2 + this.perp.y * float19;
				float9 = float3 + this.perp.x * float19;
				float10 = float4 + this.perp.y * float19;
				this.perp.set(-float5, -float6);
				this.perp.normalize();
				float11 = float1 + this.perp.x * float19;
				float12 = float2 + this.perp.y * float19;
				float13 = float3 + this.perp.x * float19;
				float14 = float4 + this.perp.y * float19;
				float float20 = Math.min(float7, Math.min(float9, Math.min(float11, float13)));
				float15 = Math.min(float8, Math.min(float10, Math.min(float12, float14)));
				float16 = Math.max(float7, Math.max(float9, Math.max(float11, float13)));
				float17 = Math.max(float8, Math.max(float10, Math.max(float12, float14)));
				this.sweepAABB.init((int)float20, (int)float15, (int)Math.ceil((double)float16) - (int)float20, (int)Math.ceil((double)float17) - (int)float15, int1);
				this.polyVec[0].set(float7, float8);
				this.polyVec[1].set(float9, float10);
				this.polyVec[2].set(float13, float14);
				this.polyVec[3].set(float11, float12);
				int int5 = this.sweepAABB.left() / 10 - 1;
				int int6 = this.sweepAABB.top() / 10 - 1;
				int int7 = (int)Math.ceil((double)((float)this.sweepAABB.right() / 10.0F)) + 1;
				int int8 = (int)Math.ceil((double)((float)this.sweepAABB.bottom() / 10.0F)) + 1;
				for (int int9 = int6; int9 < int8; ++int9) {
					for (int int10 = int5; int10 < int7; ++int10) {
						IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int10, int9) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int10 * 10, int9 * 10, 0);
						if (chunk != null) {
							for (int int11 = 0; int11 < chunk.vehicles.size(); ++int11) {
								BaseVehicle baseVehicle2 = (BaseVehicle)chunk.vehicles.get(int11);
								if (baseVehicle2 != baseVehicle && baseVehicle2.VehicleID != -1) {
									this.vehiclePoly.init(baseVehicle2.getPoly());
									this.vehiclePoly.getAABB(this.vehicleAABB);
									if (this.vehicleAABB.intersects(this.sweepAABB) && this.polyVehicleIntersect(this.vehiclePoly, boolean4)) {
										boolean5 = true;
										if (!boolean4) {
											return true;
										}
									}
								}
							}
						}
					}
				}

				return boolean5;
			}
		}

		boolean isNotClearClipper(PolygonalMap2 polygonalMap2, float float1, float float2, float float3, float float4, int int1, BaseVehicle baseVehicle, int int2) {
			boolean boolean1 = (int2 & 1) != 0;
			boolean boolean2 = (int2 & 2) != 0;
			boolean boolean3 = (int2 & 4) != 0;
			boolean boolean4 = (int2 & 8) != 0;
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1, (int)float2, int1);
			if (square != null && square.HasStairs()) {
				return !square.isSameStaircase((int)float3, (int)float4, int1);
			} else if (!this.canStandAtClipper(polygonalMap2, float3, float4, (float)int1, baseVehicle, int2)) {
				if (boolean4) {
					this.drawCircle(float3, float4, (float)int1, 0.3F, 1.0F, 0.0F, 0.0F, 1.0F);
				}

				return true;
			} else {
				return PolygonalMap2.instance.collideWithObstaclesPoly.isNotClear(float1, float2, float3, float4, int1, boolean4, baseVehicle, boolean1, boolean2);
			}
		}

		boolean isNotClear(PolygonalMap2 polygonalMap2, float float1, float float2, float float3, float float4, int int1, BaseVehicle baseVehicle, int int2) {
			return this.isNotClearOld(polygonalMap2, float1, float2, float3, float4, int1, baseVehicle, int2);
		}

		Vector2 getCollidepoint(PolygonalMap2 polygonalMap2, float float1, float float2, float float3, float float4, int int1, BaseVehicle baseVehicle, int int2) {
			boolean boolean1 = (int2 & 1) != 0;
			boolean boolean2 = (int2 & 2) != 0;
			boolean boolean3 = (int2 & 4) != 0;
			boolean boolean4 = (int2 & 8) != 0;
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1, (int)float2, int1);
			float float5 = float4 - float2;
			float float6 = -(float3 - float1);
			this.perp.set(float5, float6);
			this.perp.normalize();
			float float7 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
			float float8 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
			float float9 = float3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
			float float10 = float4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
			this.perp.set(-float5, -float6);
			this.perp.normalize();
			float float11 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
			float float12 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
			float float13 = float3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
			float float14 = float4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
			int int3;
			for (int3 = 0; int3 < this.pts.size(); ++int3) {
				this.pointPool.release((PolygonalMap2.Point)this.pts.get(int3));
			}

			this.pts.clear();
			this.pts.add(this.pointPool.alloc().init((int)float1, (int)float2));
			if ((int)float1 != (int)float3 || (int)float2 != (int)float4) {
				this.pts.add(this.pointPool.alloc().init((int)float3, (int)float4));
			}

			polygonalMap2.supercover(float7, float8, float9, float10, int1, this.pointPool, this.pts);
			polygonalMap2.supercover(float11, float12, float13, float14, int1, this.pointPool, this.pts);
			if (boolean4) {
				for (int3 = 0; int3 < this.pts.size(); ++int3) {
					PolygonalMap2.Point point = (PolygonalMap2.Point)this.pts.get(int3);
					LineDrawer.addLine((float)point.x, (float)point.y, (float)int1, (float)point.x + 1.0F, (float)point.y + 1.0F, (float)int1, 1.0F, 1.0F, 0.0F, (String)null, false);
				}
			}

			boolean boolean5 = false;
			for (int int4 = 0; int4 < this.pts.size(); ++int4) {
				PolygonalMap2.Point point2 = (PolygonalMap2.Point)this.pts.get(int4);
				square = IsoWorld.instance.CurrentCell.getGridSquare(point2.x, point2.y, int1);
				if (boolean3 && square != null && PolygonalMap2.SquareUpdateTask.getCost(square) > 0) {
					return new Vector2((float)point2.x + 0.5F, (float)point2.y + 0.5F);
				}

				float float15;
				float float16;
				float float17;
				float float18;
				label234: {
					if (square != null && !square.isSolid() && (!square.isSolidTrans() || square.isAdjacentToWindow()) && !square.HasStairs()) {
						if (square.SolidFloorCached) {
							if (square.SolidFloor) {
								break label234;
							}
						} else if (square.TreatAsSolidFloor()) {
							break label234;
						}
					}

					float15 = 0.3F;
					float16 = 0.3F;
					float17 = 0.3F;
					float18 = 0.3F;
					if (float1 < (float)point2.x && float3 < (float)point2.x) {
						float15 = 0.0F;
					} else if (float1 >= (float)(point2.x + 1) && float3 >= (float)(point2.x + 1)) {
						float17 = 0.0F;
					}

					if (float2 < (float)point2.y && float4 < (float)point2.y) {
						float16 = 0.0F;
					} else if (float2 >= (float)(point2.y + 1) && float4 >= (float)(point2.y + 1)) {
						float18 = 0.0F;
					}

					if (this.LB.lineRectIntersect(float1, float2, float3 - float1, float4 - float2, (float)point2.x - float15, (float)point2.y - float16, (float)point2.x + 1.0F + float17, (float)point2.y + 1.0F + float18)) {
						if (!boolean4) {
							return new Vector2((float)point2.x + 0.5F, (float)point2.y + 0.5F);
						}

						LineDrawer.addLine((float)point2.x - float15, (float)point2.y - float16, (float)int1, (float)point2.x + 1.0F + float17, (float)point2.y + 1.0F + float18, (float)int1, 1.0F, 0.0F, 0.0F, (String)null, false);
						boolean5 = true;
					}

					continue;
				}

				if (square.Is(IsoFlagType.collideW) || !boolean1 && square.hasBlockedDoor(false)) {
					float15 = 0.3F;
					float16 = 0.3F;
					float17 = 0.3F;
					float18 = 0.3F;
					if (float1 < (float)point2.x && float3 < (float)point2.x) {
						float15 = 0.0F;
					} else if (float1 >= (float)point2.x && float3 >= (float)point2.x) {
						float17 = 0.0F;
					}

					if (float2 < (float)point2.y && float4 < (float)point2.y) {
						float16 = 0.0F;
					} else if (float2 >= (float)(point2.y + 1) && float4 >= (float)(point2.y + 1)) {
						float18 = 0.0F;
					}

					if (this.LB.lineRectIntersect(float1, float2, float3 - float1, float4 - float2, (float)point2.x - float15, (float)point2.y - float16, (float)point2.x + float17, (float)point2.y + 1.0F + float18)) {
						if (!boolean4) {
							return new Vector2((float)point2.x + 0.5F, (float)point2.y + 0.5F);
						}

						LineDrawer.addLine((float)point2.x - float15, (float)point2.y - float16, (float)int1, (float)point2.x + float17, (float)point2.y + 1.0F + float18, (float)int1, 1.0F, 0.0F, 0.0F, (String)null, false);
						boolean5 = true;
					}
				}

				if (square.Is(IsoFlagType.collideN) || !boolean1 && square.hasBlockedDoor(true)) {
					float15 = 0.3F;
					float16 = 0.3F;
					float17 = 0.3F;
					float18 = 0.3F;
					if (float1 < (float)point2.x && float3 < (float)point2.x) {
						float15 = 0.0F;
					} else if (float1 >= (float)(point2.x + 1) && float3 >= (float)(point2.x + 1)) {
						float17 = 0.0F;
					}

					if (float2 < (float)point2.y && float4 < (float)point2.y) {
						float16 = 0.0F;
					} else if (float2 >= (float)point2.y && float4 >= (float)point2.y) {
						float18 = 0.0F;
					}

					if (this.LB.lineRectIntersect(float1, float2, float3 - float1, float4 - float2, (float)point2.x - float15, (float)point2.y - float16, (float)point2.x + 1.0F + float17, (float)point2.y + float18)) {
						if (!boolean4) {
							return new Vector2((float)point2.x + 0.5F, (float)point2.y + 0.5F);
						}

						LineDrawer.addLine((float)point2.x - float15, (float)point2.y - float16, (float)int1, (float)point2.x + 1.0F + float17, (float)point2.y + float18, (float)int1, 1.0F, 0.0F, 0.0F, (String)null, false);
						boolean5 = true;
					}
				}
			}

			return new Vector2(float3, float4);
		}

		boolean polyVehicleIntersect(PolygonalMap2.VehiclePoly vehiclePoly, boolean boolean1) {
			this.vehicleVec[0].set(vehiclePoly.x1, vehiclePoly.y1);
			this.vehicleVec[1].set(vehiclePoly.x2, vehiclePoly.y2);
			this.vehicleVec[2].set(vehiclePoly.x3, vehiclePoly.y3);
			this.vehicleVec[3].set(vehiclePoly.x4, vehiclePoly.y4);
			boolean boolean2 = false;
			for (int int1 = 0; int1 < 4; ++int1) {
				Vector2 vector2 = this.polyVec[int1];
				Vector2 vector22 = int1 == 3 ? this.polyVec[0] : this.polyVec[int1 + 1];
				for (int int2 = 0; int2 < 4; ++int2) {
					Vector2 vector23 = this.vehicleVec[int2];
					Vector2 vector24 = int2 == 3 ? this.vehicleVec[0] : this.vehicleVec[int2 + 1];
					if (Line2D.linesIntersect((double)vector2.x, (double)vector2.y, (double)vector22.x, (double)vector22.y, (double)vector23.x, (double)vector23.y, (double)vector24.x, (double)vector24.y)) {
						if (boolean1) {
							LineDrawer.addLine(vector2.x, vector2.y, 0.0F, vector22.x, vector22.y, 0.0F, 1.0F, 0.0F, 0.0F, (String)null, true);
							LineDrawer.addLine(vector23.x, vector23.y, 0.0F, vector24.x, vector24.y, 0.0F, 1.0F, 0.0F, 0.0F, (String)null, true);
						}

						boolean2 = true;
					}
				}
			}

			return boolean2;
		}
	}

	private static final class VehicleRect {
		PolygonalMap2.VehicleCluster cluster;
		PolygonalMap2.Vehicle vehicle;
		int x;
		int y;
		int w;
		int h;
		int z;
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.VehicleRect init(PolygonalMap2.Vehicle vehicle, int int1, int int2, int int3, int int4, int int5) {
			this.cluster = null;
			this.vehicle = vehicle;
			this.x = int1;
			this.y = int2;
			this.w = int3;
			this.h = int4;
			this.z = int5;
			return this;
		}

		PolygonalMap2.VehicleRect init(int int1, int int2, int int3, int int4, int int5) {
			this.cluster = null;
			this.vehicle = null;
			this.x = int1;
			this.y = int2;
			this.w = int3;
			this.h = int4;
			this.z = int5;
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

		boolean containsPoint(float float1, float float2, float float3) {
			return float1 >= (float)this.left() && float1 < (float)this.right() && float2 >= (float)this.top() && float2 < (float)this.bottom() && (int)float3 == this.z;
		}

		boolean containsPoint(float float1, float float2, float float3, int int1) {
			int int2 = this.x - int1;
			int int3 = this.y - int1;
			int int4 = this.right() + int1;
			int int5 = this.bottom() + int1;
			return float1 >= (float)int2 && float1 < (float)int4 && float2 >= (float)int3 && float2 < (float)int5 && (int)float3 == this.z;
		}

		boolean intersects(PolygonalMap2.VehicleRect vehicleRect) {
			return this.left() < vehicleRect.right() && this.right() > vehicleRect.left() && this.top() < vehicleRect.bottom() && this.bottom() > vehicleRect.top();
		}

		boolean isAdjacent(PolygonalMap2.VehicleRect vehicleRect) {
			--this.x;
			--this.y;
			this.w += 2;
			this.h += 2;
			boolean boolean1 = this.intersects(vehicleRect);
			++this.x;
			++this.y;
			this.w -= 2;
			this.h -= 2;
			return boolean1;
		}

		static PolygonalMap2.VehicleRect alloc() {
			boolean boolean1;
			if (pool.isEmpty()) {
				boolean1 = false;
			} else {
				boolean1 = false;
			}

			return pool.isEmpty() ? new PolygonalMap2.VehicleRect() : (PolygonalMap2.VehicleRect)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			pool.push(this);
		}
	}

	private static final class VehicleCluster {
		int z;
		final ArrayList rects = new ArrayList();
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.VehicleCluster init() {
			this.rects.clear();
			return this;
		}

		void merge(PolygonalMap2.VehicleCluster vehicleCluster) {
			for (int int1 = 0; int1 < vehicleCluster.rects.size(); ++int1) {
				PolygonalMap2.VehicleRect vehicleRect = (PolygonalMap2.VehicleRect)vehicleCluster.rects.get(int1);
				vehicleRect.cluster = this;
			}

			this.rects.addAll(vehicleCluster.rects);
			vehicleCluster.rects.clear();
		}

		PolygonalMap2.VehicleRect bounds() {
			int int1 = Integer.MAX_VALUE;
			int int2 = Integer.MAX_VALUE;
			int int3 = Integer.MIN_VALUE;
			int int4 = Integer.MIN_VALUE;
			for (int int5 = 0; int5 < this.rects.size(); ++int5) {
				PolygonalMap2.VehicleRect vehicleRect = (PolygonalMap2.VehicleRect)this.rects.get(int5);
				int1 = Math.min(int1, vehicleRect.left());
				int2 = Math.min(int2, vehicleRect.top());
				int3 = Math.max(int3, vehicleRect.right());
				int4 = Math.max(int4, vehicleRect.bottom());
			}

			return PolygonalMap2.VehicleRect.alloc().init(int1, int2, int3 - int1, int4 - int2, this.z);
		}

		static PolygonalMap2.VehicleCluster alloc() {
			return pool.isEmpty() ? new PolygonalMap2.VehicleCluster() : (PolygonalMap2.VehicleCluster)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			pool.push(this);
		}
	}

	private static final class Vehicle {
		final PolygonalMap2.VehiclePoly poly = new PolygonalMap2.VehiclePoly();
		final PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
		final TFloatArrayList crawlOffsets = new TFloatArrayList();
		float upVectorDot;
		static final ArrayDeque pool = new ArrayDeque();

		static PolygonalMap2.Vehicle alloc() {
			return pool.isEmpty() ? new PolygonalMap2.Vehicle() : (PolygonalMap2.Vehicle)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			pool.push(this);
		}
	}

	public static final class VehiclePoly {
		public Transform t = new Transform();
		public float x1;
		public float y1;
		public float x2;
		public float y2;
		public float x3;
		public float y3;
		public float x4;
		public float y4;
		public float z;
		public final Vector2[] borders = new Vector2[4];
		private static final Quaternionf tempQuat = new Quaternionf();

		VehiclePoly() {
			for (int int1 = 0; int1 < this.borders.length; ++int1) {
				this.borders[int1] = new Vector2();
			}
		}

		PolygonalMap2.VehiclePoly init(PolygonalMap2.VehiclePoly vehiclePoly) {
			this.x1 = vehiclePoly.x1;
			this.y1 = vehiclePoly.y1;
			this.x2 = vehiclePoly.x2;
			this.y2 = vehiclePoly.y2;
			this.x3 = vehiclePoly.x3;
			this.y3 = vehiclePoly.y3;
			this.x4 = vehiclePoly.x4;
			this.y4 = vehiclePoly.y4;
			this.z = vehiclePoly.z;
			return this;
		}

		PolygonalMap2.VehiclePoly init(BaseVehicle baseVehicle, float float1) {
			VehicleScript vehicleScript = baseVehicle.getScript();
			Vector3f vector3f = vehicleScript.getExtents();
			Vector3f vector3f2 = vehicleScript.getCenterOfMassOffset();
			float float2 = 1.0F;
			Vector2[] vector2Array = this.borders;
			Quaternionf quaternionf = tempQuat;
			baseVehicle.getWorldTransform(this.t);
			this.t.getRotation(quaternionf);
			float float3 = vector3f.x * float2 + float1 * 2.0F;
			float float4 = vector3f.z * float2 + float1 * 2.0F;
			float float5 = vector3f.y * float2 + float1 * 2.0F;
			float3 /= 2.0F;
			float4 /= 2.0F;
			float5 /= 2.0F;
			Vector3f vector3f3 = PolygonalMap2.tempVec3f_1;
			if (quaternionf.x < 0.0F) {
				baseVehicle.getWorldPos(vector3f2.x - float3, 0.0F, vector3f2.z + float4, vector3f3);
				vector2Array[0].set(vector3f3.x, vector3f3.y);
				baseVehicle.getWorldPos(vector3f2.x + float3, float5, vector3f2.z + float4, vector3f3);
				vector2Array[1].set(vector3f3.x, vector3f3.y);
				baseVehicle.getWorldPos(vector3f2.x + float3, float5, vector3f2.z - float4, vector3f3);
				vector2Array[2].set(vector3f3.x, vector3f3.y);
				baseVehicle.getWorldPos(vector3f2.x - float3, 0.0F, vector3f2.z - float4, vector3f3);
				vector2Array[3].set(vector3f3.x, vector3f3.y);
				this.z = baseVehicle.z;
			} else {
				baseVehicle.getWorldPos(vector3f2.x - float3, float5, vector3f2.z + float4, vector3f3);
				vector2Array[0].set(vector3f3.x, vector3f3.y);
				baseVehicle.getWorldPos(vector3f2.x + float3, 0.0F, vector3f2.z + float4, vector3f3);
				vector2Array[1].set(vector3f3.x, vector3f3.y);
				baseVehicle.getWorldPos(vector3f2.x + float3, 0.0F, vector3f2.z - float4, vector3f3);
				vector2Array[2].set(vector3f3.x, vector3f3.y);
				baseVehicle.getWorldPos(vector3f2.x - float3, float5, vector3f2.z - float4, vector3f3);
				vector2Array[3].set(vector3f3.x, vector3f3.y);
				this.z = baseVehicle.z;
			}

			int int1 = 0;
			Vector2 vector2;
			Vector2 vector22;
			for (int int2 = 0; int2 < vector2Array.length; ++int2) {
				vector2 = vector2Array[int2];
				vector22 = vector2Array[(int2 + 1) % vector2Array.length];
				int1 = (int)((float)int1 + (vector22.x - vector2.x) * (vector22.y + vector2.y));
			}

			if (int1 < 0) {
				Vector2 vector23 = vector2Array[1];
				vector2 = vector2Array[2];
				vector22 = vector2Array[3];
				vector2Array[1] = vector22;
				vector2Array[2] = vector2;
				vector2Array[3] = vector23;
			}

			this.x1 = vector2Array[0].x;
			this.y1 = vector2Array[0].y;
			this.x2 = vector2Array[1].x;
			this.y2 = vector2Array[1].y;
			this.x3 = vector2Array[2].x;
			this.y3 = vector2Array[2].y;
			this.x4 = vector2Array[3].x;
			this.y4 = vector2Array[3].y;
			return this;
		}

		public static Vector2 lineIntersection(Vector2 vector2, Vector2 vector22, Vector2 vector23, Vector2 vector24) {
			Vector2 vector25 = new Vector2();
			float float1 = vector2.y - vector22.y;
			float float2 = vector22.x - vector2.x;
			float float3 = -float1 * vector2.x - float2 * vector2.y;
			float float4 = vector23.y - vector24.y;
			float float5 = vector24.x - vector23.x;
			float float6 = -float4 * vector23.x - float5 * vector23.y;
			float float7 = QuadranglesIntersection.det(float1, float2, float4, float5);
			if (float7 != 0.0F) {
				vector25.x = -QuadranglesIntersection.det(float3, float2, float6, float5) * 1.0F / float7;
				vector25.y = -QuadranglesIntersection.det(float1, float3, float4, float6) * 1.0F / float7;
				return vector25;
			} else {
				return null;
			}
		}

		PolygonalMap2.VehicleRect getAABB(PolygonalMap2.VehicleRect vehicleRect) {
			float float1 = Math.min(this.x1, Math.min(this.x2, Math.min(this.x3, this.x4)));
			float float2 = Math.min(this.y1, Math.min(this.y2, Math.min(this.y3, this.y4)));
			float float3 = Math.max(this.x1, Math.max(this.x2, Math.max(this.x3, this.x4)));
			float float4 = Math.max(this.y1, Math.max(this.y2, Math.max(this.y3, this.y4)));
			return vehicleRect.init((PolygonalMap2.Vehicle)null, (int)float1, (int)float2, (int)Math.ceil((double)float3) - (int)float1, (int)Math.ceil((double)float4) - (int)float2, (int)this.z);
		}

		float isLeft(float float1, float float2, float float3, float float4, float float5, float float6) {
			return (float3 - float1) * (float6 - float2) - (float5 - float1) * (float4 - float2);
		}

		public boolean containsPoint(float float1, float float2) {
			int int1 = 0;
			for (int int2 = 0; int2 < 4; ++int2) {
				Vector2 vector2 = this.borders[int2];
				Vector2 vector22 = int2 == 3 ? this.borders[0] : this.borders[int2 + 1];
				if (vector2.y <= float2) {
					if (vector22.y > float2 && this.isLeft(vector2.x, vector2.y, vector22.x, vector22.y, float1, float2) > 0.0F) {
						++int1;
					}
				} else if (vector22.y <= float2 && this.isLeft(vector2.x, vector2.y, vector22.x, vector22.y, float1, float2) < 0.0F) {
					--int1;
				}
			}

			return int1 != 0;
		}
	}

	private static final class Square {
		static int nextID = 1;
		Integer ID;
		int x;
		int y;
		int z;
		int bits;
		short cost;
		static final ArrayDeque pool = new ArrayDeque();

		Square() {
			this.ID = nextID++;
		}

		PolygonalMap2.Square init(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.z = int3;
			return this;
		}

		boolean has(int int1) {
			return (this.bits & int1) != 0;
		}

		boolean isReallySolid() {
			return this.has(1) || this.has(1024) && !this.isAdjacentToWindow();
		}

		boolean isNonThumpableSolid() {
			return this.isReallySolid() && !this.has(131072);
		}

		boolean isCanPathW() {
			if (this.has(8192)) {
				return true;
			} else {
				PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(this.x - 1, this.y, this.z);
				return square != null && (square.has(131072) || square.has(262144));
			}
		}

		boolean isCanPathN() {
			if (this.has(16384)) {
				return true;
			} else {
				PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(this.x, this.y - 1, this.z);
				return square != null && (square.has(131072) || square.has(524288));
			}
		}

		boolean isCollideW() {
			if (this.has(2)) {
				return true;
			} else {
				PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(this.x - 1, this.y, this.z);
				return square != null && (square.has(262144) || square.has(448) || square.isReallySolid());
			}
		}

		boolean isCollideN() {
			if (this.has(4)) {
				return true;
			} else {
				PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(this.x, this.y - 1, this.z);
				return square != null && (square.has(524288) || square.has(56) || square.isReallySolid());
			}
		}

		boolean isThumpW() {
			if (this.has(32768)) {
				return true;
			} else {
				PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(this.x - 1, this.y, this.z);
				return square != null && square.has(131072);
			}
		}

		boolean isThumpN() {
			if (this.has(65536)) {
				return true;
			} else {
				PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(this.x, this.y - 1, this.z);
				return square != null && square.has(131072);
			}
		}

		boolean isAdjacentToWindow() {
			if (!this.has(2048) && !this.has(4096)) {
				PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(this.x, this.y + 1, this.z);
				if (square != null && square.has(4096)) {
					return true;
				} else {
					PolygonalMap2.Square square2 = PolygonalMap2.instance.getSquare(this.x + 1, this.y, this.z);
					return square2 != null && square2.has(2048);
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

	private static final class Node {
		static int nextID = 1;
		final int ID;
		float x;
		float y;
		int z;
		boolean ignore;
		PolygonalMap2.Square square;
		ArrayList graphs;
		final ArrayList edges = new ArrayList();
		final ArrayList visible = new ArrayList();
		int flags = 0;
		static final ArrayList tempObstacles = new ArrayList();
		static final ArrayDeque pool = new ArrayDeque();

		Node() {
			this.ID = nextID++;
		}

		PolygonalMap2.Node init(float float1, float float2, int int1) {
			this.x = float1;
			this.y = float2;
			this.z = int1;
			this.ignore = false;
			this.square = null;
			if (this.graphs != null) {
				this.graphs.clear();
			}

			this.edges.clear();
			this.visible.clear();
			this.flags = 0;
			return this;
		}

		PolygonalMap2.Node init(PolygonalMap2.Square square) {
			this.x = (float)square.x + 0.5F;
			this.y = (float)square.y + 0.5F;
			this.z = square.z;
			this.ignore = false;
			this.square = square;
			if (this.graphs != null) {
				this.graphs.clear();
			}

			this.edges.clear();
			this.visible.clear();
			this.flags = 0;
			return this;
		}

		PolygonalMap2.Node setXY(float float1, float float2) {
			this.x = float1;
			this.y = float2;
			return this;
		}

		void addGraph(PolygonalMap2.VisibilityGraph visibilityGraph) {
			if (this.graphs == null) {
				this.graphs = new ArrayList();
			}

			assert !this.graphs.contains(visibilityGraph);
			this.graphs.add(visibilityGraph);
		}

		boolean sharesEdge(PolygonalMap2.Node node) {
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)this.edges.get(int1);
				if (edge.hasNode(node)) {
					return true;
				}
			}

			return false;
		}

		boolean sharesShape(PolygonalMap2.Node node) {
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)this.edges.get(int1);
				for (int int2 = 0; int2 < node.edges.size(); ++int2) {
					PolygonalMap2.Edge edge2 = (PolygonalMap2.Edge)node.edges.get(int2);
					if (edge.obstacle != null && edge.obstacle == edge2.obstacle) {
						return true;
					}
				}
			}

			return false;
		}

		void getObstacles(ArrayList arrayList) {
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)this.edges.get(int1);
				if (!arrayList.contains(edge.obstacle)) {
					arrayList.add(edge.obstacle);
				}
			}
		}

		boolean onSameShapeButDoesNotShareAnEdge(PolygonalMap2.Node node) {
			tempObstacles.clear();
			this.getObstacles(tempObstacles);
			for (int int1 = 0; int1 < tempObstacles.size(); ++int1) {
				PolygonalMap2.Obstacle obstacle = (PolygonalMap2.Obstacle)tempObstacles.get(int1);
				if (obstacle.hasNode(node) && !obstacle.hasAdjacentNodes(this, node)) {
					return true;
				}
			}

			return false;
		}

		boolean hasFlag(int int1) {
			return (this.flags & int1) != 0;
		}

		boolean isConnectedTo(PolygonalMap2.Node node) {
			if (this.hasFlag(4)) {
				return true;
			} else {
				for (int int1 = 0; int1 < this.visible.size(); ++int1) {
					PolygonalMap2.Connection connection = (PolygonalMap2.Connection)this.visible.get(int1);
					if (connection.node1 == node || connection.node2 == node) {
						return true;
					}
				}

				return false;
			}
		}

		static PolygonalMap2.Node alloc() {
			boolean boolean1;
			if (pool.isEmpty()) {
				boolean1 = false;
			} else {
				boolean1 = false;
			}

			return pool.isEmpty() ? new PolygonalMap2.Node() : (PolygonalMap2.Node)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			for (int int1 = this.visible.size() - 1; int1 >= 0; --int1) {
				PolygonalMap2.instance.breakConnection((PolygonalMap2.Connection)this.visible.get(int1));
			}

			pool.push(this);
		}

		static void releaseAll(ArrayList arrayList) {
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				((PolygonalMap2.Node)arrayList.get(int1)).release();
			}
		}
	}

	private static final class VisibilityGraph {
		boolean created;
		PolygonalMap2.VehicleCluster cluster;
		final ArrayList nodes = new ArrayList();
		final ArrayList edges = new ArrayList();
		final ArrayList obstacles = new ArrayList();
		final ArrayList intersectNodes = new ArrayList();
		final ArrayList perimeterNodes = new ArrayList();
		final ArrayList perimeterEdges = new ArrayList();
		final ArrayList obstacleTraceNodes = new ArrayList();
		final TIntArrayList splitXY = new TIntArrayList();
		static final PolygonalMap2.VisibilityGraph.CompareIntersection comparator = new PolygonalMap2.VisibilityGraph.CompareIntersection();
		private static final PolygonalMap2.ClusterOutlineGrid clusterOutlineGrid = new PolygonalMap2.ClusterOutlineGrid();
		private static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.VisibilityGraph init(PolygonalMap2.VehicleCluster vehicleCluster) {
			this.created = false;
			this.cluster = vehicleCluster;
			this.edges.clear();
			this.nodes.clear();
			this.obstacles.clear();
			this.intersectNodes.clear();
			this.perimeterEdges.clear();
			this.perimeterNodes.clear();
			return this;
		}

		void addEdgesForVehicle(PolygonalMap2.Vehicle vehicle) {
			PolygonalMap2.VehiclePoly vehiclePoly = vehicle.polyPlusRadius;
			int int1 = (int)vehiclePoly.z;
			PolygonalMap2.Node node = PolygonalMap2.Node.alloc().init(vehiclePoly.x1, vehiclePoly.y1, int1);
			PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init(vehiclePoly.x2, vehiclePoly.y2, int1);
			PolygonalMap2.Node node3 = PolygonalMap2.Node.alloc().init(vehiclePoly.x3, vehiclePoly.y3, int1);
			PolygonalMap2.Node node4 = PolygonalMap2.Node.alloc().init(vehiclePoly.x4, vehiclePoly.y4, int1);
			PolygonalMap2.Obstacle obstacle = PolygonalMap2.Obstacle.alloc().init(vehicle);
			this.obstacles.add(obstacle);
			PolygonalMap2.Edge edge = PolygonalMap2.Edge.alloc().init(node, node2, obstacle, obstacle.outer);
			PolygonalMap2.Edge edge2 = PolygonalMap2.Edge.alloc().init(node2, node3, obstacle, obstacle.outer);
			PolygonalMap2.Edge edge3 = PolygonalMap2.Edge.alloc().init(node3, node4, obstacle, obstacle.outer);
			PolygonalMap2.Edge edge4 = PolygonalMap2.Edge.alloc().init(node4, node, obstacle, obstacle.outer);
			obstacle.outer.add(edge);
			obstacle.outer.add(edge2);
			obstacle.outer.add(edge3);
			obstacle.outer.add(edge4);
			obstacle.calcBounds();
			this.nodes.add(node);
			this.nodes.add(node2);
			this.nodes.add(node3);
			this.nodes.add(node4);
			this.edges.add(edge);
			this.edges.add(edge2);
			this.edges.add(edge3);
			this.edges.add(edge4);
			if (!(vehicle.upVectorDot < 0.95F)) {
				obstacle.nodeCrawlFront = PolygonalMap2.Node.alloc().init((vehiclePoly.x1 + vehiclePoly.x2) / 2.0F, (vehiclePoly.y1 + vehiclePoly.y2) / 2.0F, int1);
				obstacle.nodeCrawlRear = PolygonalMap2.Node.alloc().init((vehiclePoly.x3 + vehiclePoly.x4) / 2.0F, (vehiclePoly.y3 + vehiclePoly.y4) / 2.0F, int1);
				PolygonalMap2.Node node5 = obstacle.nodeCrawlFront;
				node5.flags |= 1;
				node5 = obstacle.nodeCrawlRear;
				node5.flags |= 1;
				this.nodes.add(obstacle.nodeCrawlFront);
				this.nodes.add(obstacle.nodeCrawlRear);
				PolygonalMap2.Edge edge5 = edge.split(obstacle.nodeCrawlFront);
				PolygonalMap2.Edge edge6 = edge3.split(obstacle.nodeCrawlRear);
				this.edges.add(edge5);
				this.edges.add(edge6);
				BaseVehicle.Vector2fObjectPool vector2fObjectPool = (BaseVehicle.Vector2fObjectPool)BaseVehicle.TL_vector2f_pool.get();
				Vector2f vector2f = (Vector2f)vector2fObjectPool.alloc();
				Vector2f vector2f2 = (Vector2f)vector2fObjectPool.alloc();
				obstacle.crawlNodes.clear();
				for (int int2 = 0; int2 < vehicle.crawlOffsets.size(); ++int2) {
					float float1 = vehicle.crawlOffsets.get(int2);
					vector2f.set(node3.x, node3.y);
					vector2f2.set(node2.x, node2.y);
					vector2f2.sub(vector2f).mul(float1).add(vector2f);
					PolygonalMap2.Node node6 = PolygonalMap2.Node.alloc().init(vector2f2.x, vector2f2.y, int1);
					node6.flags |= 1;
					vector2f.set(node4.x, node4.y);
					vector2f2.set(node.x, node.y);
					vector2f2.sub(vector2f).mul(float1).add(vector2f);
					PolygonalMap2.Node node7 = PolygonalMap2.Node.alloc().init(vector2f2.x, vector2f2.y, int1);
					node7.flags |= 1;
					PolygonalMap2.Node node8 = PolygonalMap2.Node.alloc().init((node6.x + node7.x) / 2.0F, (node6.y + node7.y) / 2.0F, int1);
					node8.flags |= 3;
					obstacle.crawlNodes.add(node6);
					obstacle.crawlNodes.add(node8);
					obstacle.crawlNodes.add(node7);
					this.nodes.add(node6);
					this.nodes.add(node8);
					this.nodes.add(node7);
					PolygonalMap2.Edge edge7 = edge2.split(node6);
					edge4 = edge4.split(node7);
					this.edges.add(edge7);
					this.edges.add(edge4);
				}

				vector2fObjectPool.release(vector2f);
				vector2fObjectPool.release(vector2f2);
			}
		}

		boolean isVisible(PolygonalMap2.Node node, PolygonalMap2.Node node2) {
			if (node.sharesEdge(node2)) {
				return !node.onSameShapeButDoesNotShareAnEdge(node2);
			} else if (node.sharesShape(node2)) {
				return false;
			} else {
				int int1;
				PolygonalMap2.Edge edge;
				for (int1 = 0; int1 < this.edges.size(); ++int1) {
					edge = (PolygonalMap2.Edge)this.edges.get(int1);
					if (this.intersects(node, node2, edge)) {
						return false;
					}
				}

				for (int1 = 0; int1 < this.perimeterEdges.size(); ++int1) {
					edge = (PolygonalMap2.Edge)this.perimeterEdges.get(int1);
					if (this.intersects(node, node2, edge)) {
						return false;
					}
				}

				return true;
			}
		}

		boolean intersects(PolygonalMap2.Node node, PolygonalMap2.Node node2, PolygonalMap2.Edge edge) {
			return !edge.hasNode(node) && !edge.hasNode(node2) ? Line2D.linesIntersect((double)node.x, (double)node.y, (double)node2.x, (double)node2.y, (double)edge.node1.x, (double)edge.node1.y, (double)edge.node2.x, (double)edge.node2.y) : false;
		}

		public PolygonalMap2.Intersection getIntersection(PolygonalMap2.Edge edge, PolygonalMap2.Edge edge2) {
			float float1 = edge.node1.x;
			float float2 = edge.node1.y;
			float float3 = edge.node2.x;
			float float4 = edge.node2.y;
			float float5 = edge2.node1.x;
			float float6 = edge2.node1.y;
			float float7 = edge2.node2.x;
			float float8 = edge2.node2.y;
			double double1 = (double)((float8 - float6) * (float3 - float1) - (float7 - float5) * (float4 - float2));
			if (double1 == 0.0) {
				return null;
			} else {
				double double2 = (double)((float7 - float5) * (float2 - float6) - (float8 - float6) * (float1 - float5)) / double1;
				double double3 = (double)((float3 - float1) * (float2 - float6) - (float4 - float2) * (float1 - float5)) / double1;
				if (double2 >= 0.0 && double2 <= 1.0 && double3 >= 0.0 && double3 <= 1.0) {
					float float9 = (float)((double)float1 + double2 * (double)(float3 - float1));
					float float10 = (float)((double)float2 + double2 * (double)(float4 - float2));
					return new PolygonalMap2.Intersection(edge, edge2, (float)double2, (float)double3, float9, float10);
				} else {
					return null;
				}
			}
		}

		@Deprecated
		void addWorldObstacles() {
			PolygonalMap2.VehicleRect vehicleRect = this.cluster.bounds();
			--vehicleRect.x;
			--vehicleRect.y;
			vehicleRect.w += 3;
			vehicleRect.h += 3;
			PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray = new PolygonalMap2.ObjectOutline[vehicleRect.w][vehicleRect.h];
			int int1 = this.cluster.z;
			int int2;
			int int3;
			for (int2 = vehicleRect.top(); int2 < vehicleRect.bottom() - 1; ++int2) {
				for (int3 = vehicleRect.left(); int3 < vehicleRect.right() - 1; ++int3) {
					PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int3, int2, int1);
					if (square != null && this.contains(square, 1)) {
						if (square.has(504) || square.isReallySolid()) {
							PolygonalMap2.ObjectOutline.setSolid(int3 - vehicleRect.left(), int2 - vehicleRect.top(), int1, objectOutlineArrayArray);
						}

						if (square.has(2)) {
							PolygonalMap2.ObjectOutline.setWest(int3 - vehicleRect.left(), int2 - vehicleRect.top(), int1, objectOutlineArrayArray);
						}

						if (square.has(4)) {
							PolygonalMap2.ObjectOutline.setNorth(int3 - vehicleRect.left(), int2 - vehicleRect.top(), int1, objectOutlineArrayArray);
						}

						if (square.has(262144)) {
							PolygonalMap2.ObjectOutline.setWest(int3 - vehicleRect.left() + 1, int2 - vehicleRect.top(), int1, objectOutlineArrayArray);
						}

						if (square.has(524288)) {
							PolygonalMap2.ObjectOutline.setNorth(int3 - vehicleRect.left(), int2 - vehicleRect.top() + 1, int1, objectOutlineArrayArray);
						}
					}
				}
			}

			for (int2 = 0; int2 < vehicleRect.h; ++int2) {
				for (int3 = 0; int3 < vehicleRect.w; ++int3) {
					PolygonalMap2.ObjectOutline objectOutline = PolygonalMap2.ObjectOutline.get(int3, int2, int1, objectOutlineArrayArray);
					if (objectOutline != null && objectOutline.nw && objectOutline.nw_w && objectOutline.nw_n) {
						objectOutline.trace(objectOutlineArrayArray, this.obstacleTraceNodes);
						if (!objectOutline.nodes.isEmpty()) {
							PolygonalMap2.Obstacle obstacle = PolygonalMap2.Obstacle.alloc().init((IsoGridSquare)null);
							for (int int4 = 0; int4 < objectOutline.nodes.size() - 1; ++int4) {
								PolygonalMap2.Node node = (PolygonalMap2.Node)objectOutline.nodes.get(int4);
								PolygonalMap2.Node node2 = (PolygonalMap2.Node)objectOutline.nodes.get(int4 + 1);
								node.x += (float)vehicleRect.left();
								node.y += (float)vehicleRect.top();
								if (!this.contains(node.x, node.y, node.z)) {
									node.ignore = true;
								}

								PolygonalMap2.Edge edge = PolygonalMap2.Edge.alloc().init(node, node2, obstacle, obstacle.outer);
								obstacle.outer.add(edge);
								this.nodes.add(node);
							}

							obstacle.calcBounds();
							this.obstacles.add(obstacle);
							this.edges.addAll(obstacle.outer);
						}
					}
				}
			}

			for (int2 = 0; int2 < vehicleRect.h; ++int2) {
				for (int3 = 0; int3 < vehicleRect.w; ++int3) {
					if (objectOutlineArrayArray[int3][int2] != null) {
						objectOutlineArrayArray[int3][int2].release();
					}
				}
			}

			vehicleRect.release();
		}

		void addWorldObstaclesClipper() {
			PolygonalMap2.VehicleRect vehicleRect = this.cluster.bounds();
			--vehicleRect.x;
			--vehicleRect.y;
			vehicleRect.w += 2;
			vehicleRect.h += 2;
			if (PolygonalMap2.instance.clipperThread == null) {
				PolygonalMap2.instance.clipperThread = new Clipper();
			}

			Clipper clipper = PolygonalMap2.instance.clipperThread;
			clipper.clear();
			int int1 = this.cluster.z;
			int int2;
			for (int int3 = vehicleRect.top(); int3 < vehicleRect.bottom(); ++int3) {
				for (int2 = vehicleRect.left(); int2 < vehicleRect.right(); ++int2) {
					PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int2, int3, int1);
					if (square != null && this.contains(square, 1)) {
						if (square.has(504) || square.isReallySolid()) {
							clipper.addAABB((float)int2 - 0.3F, (float)int3 - 0.3F, (float)(int2 + 1) + 0.3F, (float)(int3 + 1) + 0.3F);
						}

						if (square.has(2)) {
							clipper.addAABB((float)int2 - 0.3F, (float)int3 - 0.3F, (float)int2 + 0.3F, (float)(int3 + 1) + 0.3F);
						}

						if (square.has(4)) {
							clipper.addAABB((float)int2 - 0.3F, (float)int3 - 0.3F, (float)(int2 + 1) + 0.3F, (float)int3 + 0.3F);
						}
					}
				}
			}

			vehicleRect.release();
			ByteBuffer byteBuffer = PolygonalMap2.instance.xyBufferThread;
			int2 = clipper.generatePolygons();
			for (int int4 = 0; int4 < int2; ++int4) {
				byteBuffer.clear();
				clipper.getPolygon(int4, byteBuffer);
				PolygonalMap2.Obstacle obstacle = PolygonalMap2.Obstacle.alloc().init((IsoGridSquare)null);
				this.getEdgesFromBuffer(byteBuffer, obstacle, true, int1);
				short short1 = byteBuffer.getShort();
				int int5;
				for (int5 = 0; int5 < short1; ++int5) {
					this.getEdgesFromBuffer(byteBuffer, obstacle, false, int1);
				}

				obstacle.calcBounds();
				this.obstacles.add(obstacle);
				this.edges.addAll(obstacle.outer);
				for (int5 = 0; int5 < obstacle.inner.size(); ++int5) {
					this.edges.addAll((Collection)obstacle.inner.get(int5));
				}
			}
		}

		void getEdgesFromBuffer(ByteBuffer byteBuffer, PolygonalMap2.Obstacle obstacle, boolean boolean1, int int1) {
			short short1 = byteBuffer.getShort();
			if (short1 < 3) {
				byteBuffer.position(byteBuffer.position() + short1 * 4 * 2);
			} else {
				PolygonalMap2.EdgeRing edgeRing = obstacle.outer;
				if (!boolean1) {
					edgeRing = PolygonalMap2.EdgeRing.alloc();
					edgeRing.clear();
					obstacle.inner.add(edgeRing);
				}

				int int2 = this.nodes.size();
				int int3;
				for (int3 = short1 - 1; int3 >= 0; --int3) {
					float float1 = byteBuffer.getFloat();
					float float2 = byteBuffer.getFloat();
					PolygonalMap2.Node node = PolygonalMap2.Node.alloc().init(float1, float2, int1);
					this.nodes.add(node);
				}

				PolygonalMap2.Node node2;
				for (int3 = int2; int3 < this.nodes.size() - 1; ++int3) {
					node2 = (PolygonalMap2.Node)this.nodes.get(int3);
					PolygonalMap2.Node node3 = (PolygonalMap2.Node)this.nodes.get(int3 + 1);
					if (!this.contains(node2.x, node2.y, node2.z)) {
						node2.ignore = true;
					}

					PolygonalMap2.Edge edge = PolygonalMap2.Edge.alloc().init(node2, node3, obstacle, edgeRing);
					edgeRing.add(edge);
				}

				PolygonalMap2.Node node4 = (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1);
				node2 = (PolygonalMap2.Node)this.nodes.get(int2);
				PolygonalMap2.Edge edge2 = PolygonalMap2.Edge.alloc().init(node4, node2, obstacle, edgeRing);
				edgeRing.add(edge2);
			}
		}

		void trySplit(PolygonalMap2.Edge edge, PolygonalMap2.VehicleRect vehicleRect, TIntArrayList tIntArrayList) {
			float float1;
			float float2;
			float float3;
			if (Math.abs(edge.node1.x - edge.node2.x) > Math.abs(edge.node1.y - edge.node2.y)) {
				float1 = Math.min(edge.node1.x, edge.node2.x);
				float2 = Math.max(edge.node1.x, edge.node2.x);
				float3 = edge.node1.y;
				if ((float)vehicleRect.left() > float1 && (float)vehicleRect.left() < float2 && (float)vehicleRect.top() < float3 && (float)vehicleRect.bottom() > float3 && !tIntArrayList.contains(vehicleRect.left()) && !this.contains((float)vehicleRect.left() - 0.5F, float3, this.cluster.z)) {
					tIntArrayList.add(vehicleRect.left());
				}

				if ((float)vehicleRect.right() > float1 && (float)vehicleRect.right() < float2 && (float)vehicleRect.top() < float3 && (float)vehicleRect.bottom() > float3 && !tIntArrayList.contains(vehicleRect.right()) && !this.contains((float)vehicleRect.right() + 0.5F, float3, this.cluster.z)) {
					tIntArrayList.add(vehicleRect.right());
				}
			} else {
				float1 = Math.min(edge.node1.y, edge.node2.y);
				float2 = Math.max(edge.node1.y, edge.node2.y);
				float3 = edge.node1.x;
				if ((float)vehicleRect.top() > float1 && (float)vehicleRect.top() < float2 && (float)vehicleRect.left() < float3 && (float)vehicleRect.right() > float3 && !tIntArrayList.contains(vehicleRect.top()) && !this.contains(float3, (float)vehicleRect.top() - 0.5F, this.cluster.z)) {
					tIntArrayList.add(vehicleRect.top());
				}

				if ((float)vehicleRect.bottom() > float1 && (float)vehicleRect.bottom() < float2 && (float)vehicleRect.left() < float3 && (float)vehicleRect.right() > float3 && !tIntArrayList.contains(vehicleRect.bottom()) && !this.contains(float3, (float)vehicleRect.bottom() + 0.5F, this.cluster.z)) {
					tIntArrayList.add(vehicleRect.bottom());
				}
			}
		}

		void splitWorldObstacleEdges(PolygonalMap2.EdgeRing edgeRing) {
			for (int int1 = edgeRing.size() - 1; int1 >= 0; --int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)edgeRing.get(int1);
				this.splitXY.clear();
				int int2;
				for (int2 = 0; int2 < this.cluster.rects.size(); ++int2) {
					PolygonalMap2.VehicleRect vehicleRect = (PolygonalMap2.VehicleRect)this.cluster.rects.get(int2);
					this.trySplit(edge, vehicleRect, this.splitXY);
				}

				if (!this.splitXY.isEmpty()) {
					this.splitXY.sort();
					PolygonalMap2.Edge edge2;
					PolygonalMap2.Node node;
					if (Math.abs(edge.node1.x - edge.node2.x) > Math.abs(edge.node1.y - edge.node2.y)) {
						if (edge.node1.x < edge.node2.x) {
							for (int2 = this.splitXY.size() - 1; int2 >= 0; --int2) {
								node = PolygonalMap2.Node.alloc().init((float)this.splitXY.get(int2), edge.node1.y, this.cluster.z);
								edge2 = edge.split(node);
								this.nodes.add(node);
								this.edges.add(edge2);
							}
						} else {
							for (int2 = 0; int2 < this.splitXY.size(); ++int2) {
								node = PolygonalMap2.Node.alloc().init((float)this.splitXY.get(int2), edge.node1.y, this.cluster.z);
								edge2 = edge.split(node);
								this.nodes.add(node);
								this.edges.add(edge2);
							}
						}
					} else if (edge.node1.y < edge.node2.y) {
						for (int2 = this.splitXY.size() - 1; int2 >= 0; --int2) {
							node = PolygonalMap2.Node.alloc().init(edge.node1.x, (float)this.splitXY.get(int2), this.cluster.z);
							edge2 = edge.split(node);
							this.nodes.add(node);
							this.edges.add(edge2);
						}
					} else {
						for (int2 = 0; int2 < this.splitXY.size(); ++int2) {
							node = PolygonalMap2.Node.alloc().init(edge.node1.x, (float)this.splitXY.get(int2), this.cluster.z);
							edge2 = edge.split(node);
							this.nodes.add(node);
							this.edges.add(edge2);
						}
					}
				}
			}
		}

		void getStairSquares(ArrayList arrayList) {
			PolygonalMap2.VehicleRect vehicleRect = this.cluster.bounds();
			vehicleRect.x -= 4;
			vehicleRect.w += 4;
			++vehicleRect.w;
			vehicleRect.y -= 4;
			vehicleRect.h += 4;
			++vehicleRect.h;
			for (int int1 = vehicleRect.top(); int1 < vehicleRect.bottom(); ++int1) {
				for (int int2 = vehicleRect.left(); int2 < vehicleRect.right(); ++int2) {
					PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int2, int1, this.cluster.z);
					if (square != null && square.has(72) && !arrayList.contains(square)) {
						arrayList.add(square);
					}
				}
			}

			vehicleRect.release();
		}

		void getCanPathSquares(ArrayList arrayList) {
			PolygonalMap2.VehicleRect vehicleRect = this.cluster.bounds();
			--vehicleRect.x;
			vehicleRect.w += 2;
			--vehicleRect.y;
			vehicleRect.h += 2;
			for (int int1 = vehicleRect.top(); int1 < vehicleRect.bottom(); ++int1) {
				for (int int2 = vehicleRect.left(); int2 < vehicleRect.right(); ++int2) {
					PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int2, int1, this.cluster.z);
					if (square != null && (square.isCanPathW() || square.isCanPathN()) && !arrayList.contains(square)) {
						arrayList.add(square);
					}
				}
			}

			vehicleRect.release();
		}

		void connectVehicleCrawlNodes() {
			for (int int1 = 0; int1 < this.obstacles.size(); ++int1) {
				PolygonalMap2.Obstacle obstacle = (PolygonalMap2.Obstacle)this.obstacles.get(int1);
				if (obstacle.vehicle != null && obstacle.nodeCrawlFront != null) {
					int int2;
					PolygonalMap2.Node node;
					for (int2 = 0; int2 < obstacle.crawlNodes.size(); int2 += 3) {
						node = (PolygonalMap2.Node)obstacle.crawlNodes.get(int2);
						PolygonalMap2.Node node2 = (PolygonalMap2.Node)obstacle.crawlNodes.get(int2 + 1);
						PolygonalMap2.Node node3 = (PolygonalMap2.Node)obstacle.crawlNodes.get(int2 + 2);
						PolygonalMap2.instance.connectTwoNodes(node, node2);
						PolygonalMap2.instance.connectTwoNodes(node3, node2);
						if (int2 + 3 < obstacle.crawlNodes.size()) {
							PolygonalMap2.Node node4 = (PolygonalMap2.Node)obstacle.crawlNodes.get(int2 + 3 + 1);
							PolygonalMap2.instance.connectTwoNodes(node2, node4);
						}
					}

					if (!obstacle.crawlNodes.isEmpty()) {
						int2 = obstacle.crawlNodes.size() - 2;
						node = (PolygonalMap2.Node)obstacle.crawlNodes.get(int2);
						PolygonalMap2.instance.connectTwoNodes(obstacle.nodeCrawlFront, node);
						byte byte1 = 1;
						node = (PolygonalMap2.Node)obstacle.crawlNodes.get(byte1);
						PolygonalMap2.instance.connectTwoNodes(obstacle.nodeCrawlRear, node);
					}

					if (!obstacle.crawlNodes.isEmpty()) {
						PolygonalMap2.ImmutableRectF immutableRectF = obstacle.bounds;
						int int3 = (int)immutableRectF.x;
						int int4 = (int)immutableRectF.y;
						int int5 = (int)Math.ceil((double)immutableRectF.right());
						int int6 = (int)Math.ceil((double)immutableRectF.bottom());
						for (int int7 = int4; int7 < int6; ++int7) {
							for (int int8 = int3; int8 < int5; ++int8) {
								PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int8, int7, this.cluster.z);
								if (square != null && obstacle.isPointInside((float)int8 + 0.5F, (float)int7 + 0.5F)) {
									PolygonalMap2.Node node5 = PolygonalMap2.instance.getNodeForSquare(square);
									for (int int9 = node5.visible.size() - 1; int9 >= 0; --int9) {
										PolygonalMap2.Connection connection = (PolygonalMap2.Connection)node5.visible.get(int9);
										if (connection.has(1)) {
											PolygonalMap2.Node node6 = connection.otherNode(node5);
											PolygonalMap2.Node node7 = obstacle.getClosestInteriorCrawlNode(node5.x, node5.y);
											for (int int10 = 0; int10 < obstacle.outer.size(); ++int10) {
												PolygonalMap2.Edge edge = (PolygonalMap2.Edge)obstacle.outer.get(int10);
												float float1 = edge.node1.x;
												float float2 = edge.node1.y;
												float float3 = edge.node2.x;
												float float4 = edge.node2.y;
												float float5 = connection.node1.x;
												float float6 = connection.node1.y;
												float float7 = connection.node2.x;
												float float8 = connection.node2.y;
												double double1 = (double)((float8 - float6) * (float3 - float1) - (float7 - float5) * (float4 - float2));
												if (double1 != 0.0) {
													double double2 = (double)((float7 - float5) * (float2 - float6) - (float8 - float6) * (float1 - float5)) / double1;
													double double3 = (double)((float3 - float1) * (float2 - float6) - (float4 - float2) * (float1 - float5)) / double1;
													if (double2 >= 0.0 && double2 <= 1.0 && double3 >= 0.0 && double3 <= 1.0) {
														float float9 = (float)((double)float1 + double2 * (double)(float3 - float1));
														float float10 = (float)((double)float2 + double2 * (double)(float4 - float2));
														PolygonalMap2.Node node8 = PolygonalMap2.Node.alloc().init(float9, float10, this.cluster.z);
														node8.flags |= 1;
														boolean boolean1 = edge.node1.isConnectedTo(edge.node2);
														PolygonalMap2.Edge edge2 = edge.split(node8);
														if (boolean1) {
															PolygonalMap2.instance.connectTwoNodes(edge.node1, edge.node2);
															PolygonalMap2.instance.connectTwoNodes(edge2.node1, edge2.node2);
														}

														this.edges.add(edge2);
														this.nodes.add(node8);
														PolygonalMap2.instance.connectTwoNodes(node6, node8, connection.flags & 2 | 1);
														PolygonalMap2.instance.connectTwoNodes(node8, node7, 0);
														break;
													}
												}
											}

											PolygonalMap2.instance.breakConnection(connection);
										}
									}
								}
							}
						}
					}

					for (int2 = int1 + 1; int2 < this.obstacles.size(); ++int2) {
						PolygonalMap2.Obstacle obstacle2 = (PolygonalMap2.Obstacle)this.obstacles.get(int2);
						if (obstacle2.vehicle != null && obstacle2.nodeCrawlFront != null) {
							obstacle.connectCrawlNodes(this, obstacle2);
							obstacle2.connectCrawlNodes(this, obstacle);
						}
					}
				}
			}
		}

		void checkEdgeIntersection() {
			int int1;
			PolygonalMap2.Obstacle obstacle;
			int int2;
			for (int1 = 0; int1 < this.obstacles.size(); ++int1) {
				obstacle = (PolygonalMap2.Obstacle)this.obstacles.get(int1);
				for (int2 = int1 + 1; int2 < this.obstacles.size(); ++int2) {
					PolygonalMap2.Obstacle obstacle2 = (PolygonalMap2.Obstacle)this.obstacles.get(int2);
					if (obstacle.bounds.intersects(obstacle2.bounds)) {
						this.checkEdgeIntersection(obstacle.outer, obstacle2.outer);
						int int3;
						PolygonalMap2.EdgeRing edgeRing;
						for (int3 = 0; int3 < obstacle2.inner.size(); ++int3) {
							edgeRing = (PolygonalMap2.EdgeRing)obstacle2.inner.get(int3);
							this.checkEdgeIntersection(obstacle.outer, edgeRing);
						}

						for (int3 = 0; int3 < obstacle.inner.size(); ++int3) {
							edgeRing = (PolygonalMap2.EdgeRing)obstacle.inner.get(int3);
							this.checkEdgeIntersection(edgeRing, obstacle2.outer);
							for (int int4 = 0; int4 < obstacle2.inner.size(); ++int4) {
								PolygonalMap2.EdgeRing edgeRing2 = (PolygonalMap2.EdgeRing)obstacle2.inner.get(int4);
								this.checkEdgeIntersection(edgeRing, edgeRing2);
							}
						}
					}
				}
			}

			for (int1 = 0; int1 < this.obstacles.size(); ++int1) {
				obstacle = (PolygonalMap2.Obstacle)this.obstacles.get(int1);
				this.checkEdgeIntersectionSplit(obstacle.outer);
				for (int2 = 0; int2 < obstacle.inner.size(); ++int2) {
					this.checkEdgeIntersectionSplit((PolygonalMap2.EdgeRing)obstacle.inner.get(int2));
				}
			}
		}

		void checkEdgeIntersection(PolygonalMap2.EdgeRing edgeRing, PolygonalMap2.EdgeRing edgeRing2) {
			for (int int1 = 0; int1 < edgeRing.size(); ++int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)edgeRing.get(int1);
				for (int int2 = 0; int2 < edgeRing2.size(); ++int2) {
					PolygonalMap2.Edge edge2 = (PolygonalMap2.Edge)edgeRing2.get(int2);
					if (this.intersects(edge.node1, edge.node2, edge2)) {
						PolygonalMap2.Intersection intersection = this.getIntersection(edge, edge2);
						if (intersection != null) {
							edge.intersections.add(intersection);
							edge2.intersections.add(intersection);
							this.nodes.add(intersection.nodeSplit);
							this.intersectNodes.add(intersection.nodeSplit);
						}
					}
				}
			}
		}

		void checkEdgeIntersectionSplit(PolygonalMap2.EdgeRing edgeRing) {
			for (int int1 = edgeRing.size() - 1; int1 >= 0; --int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)edgeRing.get(int1);
				if (!edge.intersections.isEmpty()) {
					comparator.edge = edge;
					Collections.sort(edge.intersections, comparator);
					for (int int2 = edge.intersections.size() - 1; int2 >= 0; --int2) {
						PolygonalMap2.Intersection intersection = (PolygonalMap2.Intersection)edge.intersections.get(int2);
						PolygonalMap2.Edge edge2 = intersection.split(edge);
						this.edges.add(edge2);
					}
				}
			}
		}

		void checkNodesInObstacles() {
			int int1;
			PolygonalMap2.Node node;
			int int2;
			PolygonalMap2.Obstacle obstacle;
			for (int1 = 0; int1 < this.nodes.size(); ++int1) {
				node = (PolygonalMap2.Node)this.nodes.get(int1);
				for (int2 = 0; int2 < this.obstacles.size(); ++int2) {
					obstacle = (PolygonalMap2.Obstacle)this.obstacles.get(int2);
					if (obstacle.isNodeInsideOf(node)) {
						node.ignore = true;
						break;
					}
				}
			}

			for (int1 = 0; int1 < this.perimeterNodes.size(); ++int1) {
				node = (PolygonalMap2.Node)this.perimeterNodes.get(int1);
				for (int2 = 0; int2 < this.obstacles.size(); ++int2) {
					obstacle = (PolygonalMap2.Obstacle)this.obstacles.get(int2);
					if (obstacle.isNodeInsideOf(node)) {
						node.ignore = true;
						break;
					}
				}
			}
		}

		void addPerimeterEdges() {
			PolygonalMap2.VehicleRect vehicleRect = this.cluster.bounds();
			--vehicleRect.x;
			--vehicleRect.y;
			vehicleRect.w += 2;
			vehicleRect.h += 2;
			PolygonalMap2.ClusterOutlineGrid clusterOutlineGrid = clusterOutlineGrid.setSize(vehicleRect.w, vehicleRect.h);
			int int1 = this.cluster.z;
			int int2;
			for (int2 = 0; int2 < this.cluster.rects.size(); ++int2) {
				PolygonalMap2.VehicleRect vehicleRect2 = (PolygonalMap2.VehicleRect)this.cluster.rects.get(int2);
				vehicleRect2 = PolygonalMap2.VehicleRect.alloc().init(vehicleRect2.x - 1, vehicleRect2.y - 1, vehicleRect2.w + 2, vehicleRect2.h + 2, vehicleRect2.z);
				for (int int3 = vehicleRect2.top(); int3 < vehicleRect2.bottom(); ++int3) {
					for (int int4 = vehicleRect2.left(); int4 < vehicleRect2.right(); ++int4) {
						clusterOutlineGrid.setInner(int4 - vehicleRect.left(), int3 - vehicleRect.top(), int1);
					}
				}

				vehicleRect2.release();
			}

			int int5;
			PolygonalMap2.ClusterOutline clusterOutline;
			for (int2 = 0; int2 < vehicleRect.h; ++int2) {
				for (int5 = 0; int5 < vehicleRect.w; ++int5) {
					clusterOutline = clusterOutlineGrid.get(int5, int2, int1);
					if (clusterOutline.inner) {
						if (!clusterOutlineGrid.isInner(int5 - 1, int2, int1)) {
							clusterOutline.w = true;
						}

						if (!clusterOutlineGrid.isInner(int5, int2 - 1, int1)) {
							clusterOutline.n = true;
						}

						if (!clusterOutlineGrid.isInner(int5 + 1, int2, int1)) {
							clusterOutline.e = true;
						}

						if (!clusterOutlineGrid.isInner(int5, int2 + 1, int1)) {
							clusterOutline.s = true;
						}
					}
				}
			}

			for (int2 = 0; int2 < vehicleRect.h; ++int2) {
				for (int5 = 0; int5 < vehicleRect.w; ++int5) {
					clusterOutline = clusterOutlineGrid.get(int5, int2, int1);
					if (clusterOutline != null && (clusterOutline.w || clusterOutline.n || clusterOutline.e || clusterOutline.s || clusterOutline.innerCorner)) {
						PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(vehicleRect.x + int5, vehicleRect.y + int2, int1);
						if (square != null && !square.isNonThumpableSolid() && !square.has(504)) {
							PolygonalMap2.Node node = PolygonalMap2.instance.getNodeForSquare(square);
							node.flags |= 8;
							node.addGraph(this);
							this.perimeterNodes.add(node);
						}
					}

					if (clusterOutline != null && clusterOutline.n && clusterOutline.w && clusterOutline.inner && !(clusterOutline.tw | clusterOutline.tn | clusterOutline.te | clusterOutline.ts)) {
						ArrayList arrayList = clusterOutlineGrid.trace(clusterOutline);
						if (!arrayList.isEmpty()) {
							for (int int6 = 0; int6 < arrayList.size() - 1; ++int6) {
								PolygonalMap2.Node node2 = (PolygonalMap2.Node)arrayList.get(int6);
								PolygonalMap2.Node node3 = (PolygonalMap2.Node)arrayList.get(int6 + 1);
								node2.x += (float)vehicleRect.left();
								node2.y += (float)vehicleRect.top();
								PolygonalMap2.Edge edge = PolygonalMap2.Edge.alloc().init(node2, node3, (PolygonalMap2.Obstacle)null, (PolygonalMap2.EdgeRing)null);
								this.perimeterEdges.add(edge);
							}

							if (arrayList.get(arrayList.size() - 1) != arrayList.get(0)) {
								PolygonalMap2.Node node4 = (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1);
								node4.x += (float)vehicleRect.left();
								node4 = (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1);
								node4.y += (float)vehicleRect.top();
							}
						}
					}
				}
			}

			clusterOutlineGrid.releaseElements();
			vehicleRect.release();
		}

		void calculateNodeVisibility() {
			ArrayList arrayList = new ArrayList();
			arrayList.addAll(this.nodes);
			arrayList.addAll(this.perimeterNodes);
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				PolygonalMap2.Node node = (PolygonalMap2.Node)arrayList.get(int1);
				if (!node.ignore && (node.square == null || !node.square.has(504))) {
					for (int int2 = int1 + 1; int2 < arrayList.size(); ++int2) {
						PolygonalMap2.Node node2 = (PolygonalMap2.Node)arrayList.get(int2);
						if (!node2.ignore && (node2.square == null || !node2.square.has(504)) && (!node.hasFlag(8) || !node2.hasFlag(8))) {
							if (node.isConnectedTo(node2)) {
								assert node.square != null && (node.square.isCanPathW() || node.square.isCanPathN()) || node2.square != null && (node2.square.isCanPathW() || node2.square.isCanPathN());
							} else if (this.isVisible(node, node2)) {
								PolygonalMap2.instance.connectTwoNodes(node, node2);
							}
						}
					}
				}
			}
		}

		void addNode(PolygonalMap2.Node node) {
			if (this.created && !node.ignore) {
				ArrayList arrayList = new ArrayList();
				arrayList.addAll(this.nodes);
				arrayList.addAll(this.perimeterNodes);
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					PolygonalMap2.Node node2 = (PolygonalMap2.Node)arrayList.get(int1);
					if (!node2.ignore && this.isVisible(node2, node)) {
						PolygonalMap2.instance.connectTwoNodes(node, node2);
					}
				}
			}

			this.nodes.add(node);
		}

		void removeNode(PolygonalMap2.Node node) {
			this.nodes.remove(node);
			for (int int1 = node.visible.size() - 1; int1 >= 0; --int1) {
				PolygonalMap2.Connection connection = (PolygonalMap2.Connection)node.visible.get(int1);
				PolygonalMap2.instance.breakConnection(connection);
			}
		}

		boolean contains(float float1, float float2, int int1) {
			for (int int2 = 0; int2 < this.cluster.rects.size(); ++int2) {
				PolygonalMap2.VehicleRect vehicleRect = (PolygonalMap2.VehicleRect)this.cluster.rects.get(int2);
				if (vehicleRect.containsPoint(float1, float2, (float)int1)) {
					return true;
				}
			}

			return false;
		}

		boolean contains(float float1, float float2, int int1, int int2) {
			for (int int3 = 0; int3 < this.cluster.rects.size(); ++int3) {
				PolygonalMap2.VehicleRect vehicleRect = (PolygonalMap2.VehicleRect)this.cluster.rects.get(int3);
				if (vehicleRect.containsPoint(float1, float2, (float)int1, int2)) {
					return true;
				}
			}

			return false;
		}

		boolean contains(PolygonalMap2.Square square) {
			for (int int1 = 0; int1 < this.cluster.rects.size(); ++int1) {
				PolygonalMap2.VehicleRect vehicleRect = (PolygonalMap2.VehicleRect)this.cluster.rects.get(int1);
				if (vehicleRect.containsPoint((float)square.x + 0.5F, (float)square.y + 0.5F, (float)square.z)) {
					return true;
				}
			}

			return false;
		}

		boolean contains(PolygonalMap2.Square square, int int1) {
			for (int int2 = 0; int2 < this.cluster.rects.size(); ++int2) {
				PolygonalMap2.VehicleRect vehicleRect = (PolygonalMap2.VehicleRect)this.cluster.rects.get(int2);
				if (vehicleRect.containsPoint((float)square.x + 0.5F, (float)square.y + 0.5F, (float)square.z, int1)) {
					return true;
				}
			}

			return false;
		}

		private int getPointOutsideObstacles(float float1, float float2, float float3, PolygonalMap2.AdjustStartEndNodeData adjustStartEndNodeData) {
			PolygonalMap2.ClosestPointOnEdge closestPointOnEdge = PolygonalMap2.instance.closestPointOnEdge;
			double double1 = Double.MAX_VALUE;
			PolygonalMap2.Edge edge = null;
			PolygonalMap2.Node node = null;
			float float4 = 0.0F;
			float float5 = 0.0F;
			for (int int1 = 0; int1 < this.obstacles.size(); ++int1) {
				PolygonalMap2.Obstacle obstacle = (PolygonalMap2.Obstacle)this.obstacles.get(int1);
				if (obstacle.bounds.containsPoint(float1, float2) && obstacle.isPointInside(float1, float2)) {
					obstacle.getClosestPointOnEdge(float1, float2, closestPointOnEdge);
					if (closestPointOnEdge.edge != null && closestPointOnEdge.distSq < double1) {
						double1 = closestPointOnEdge.distSq;
						edge = closestPointOnEdge.edge;
						node = closestPointOnEdge.node;
						float4 = closestPointOnEdge.point.x;
						float5 = closestPointOnEdge.point.y;
					}
				}
			}

			if (edge != null) {
				closestPointOnEdge.edge = edge;
				closestPointOnEdge.node = node;
				closestPointOnEdge.point.set(float4, float5);
				closestPointOnEdge.distSq = double1;
				if (edge.obstacle.splitEdgeAtNearestPoint(closestPointOnEdge, (int)float3, adjustStartEndNodeData)) {
					adjustStartEndNodeData.graph = this;
					if (adjustStartEndNodeData.isNodeNew) {
						this.edges.add(adjustStartEndNodeData.newEdge);
						this.addNode(adjustStartEndNodeData.node);
					}

					return 1;
				} else {
					return -1;
				}
			} else {
				return 0;
			}
		}

		PolygonalMap2.Node getClosestNodeTo(float float1, float float2) {
			PolygonalMap2.Node node = null;
			float float3 = Float.MAX_VALUE;
			for (int int1 = 0; int1 < this.nodes.size(); ++int1) {
				PolygonalMap2.Node node2 = (PolygonalMap2.Node)this.nodes.get(int1);
				float float4 = IsoUtils.DistanceToSquared(node2.x, node2.y, float1, float2);
				if (float4 < float3) {
					node = node2;
					float3 = float4;
				}
			}

			return node;
		}

		void create() {
			int int1;
			for (int1 = 0; int1 < this.cluster.rects.size(); ++int1) {
				PolygonalMap2.VehicleRect vehicleRect = (PolygonalMap2.VehicleRect)this.cluster.rects.get(int1);
				this.addEdgesForVehicle(vehicleRect.vehicle);
			}

			this.addWorldObstaclesClipper();
			for (int1 = 0; int1 < this.obstacles.size(); ++int1) {
				PolygonalMap2.Obstacle obstacle = (PolygonalMap2.Obstacle)this.obstacles.get(int1);
				if (obstacle.vehicle == null) {
					this.splitWorldObstacleEdges(obstacle.outer);
					for (int int2 = 0; int2 < obstacle.inner.size(); ++int2) {
						this.splitWorldObstacleEdges((PolygonalMap2.EdgeRing)obstacle.inner.get(int2));
					}
				}
			}

			this.checkEdgeIntersection();
			this.checkNodesInObstacles();
			this.calculateNodeVisibility();
			this.connectVehicleCrawlNodes();
			this.created = true;
		}

		static PolygonalMap2.VisibilityGraph alloc() {
			return pool.isEmpty() ? new PolygonalMap2.VisibilityGraph() : (PolygonalMap2.VisibilityGraph)pool.pop();
		}

		void release() {
			int int1;
			for (int1 = 0; int1 < this.nodes.size(); ++int1) {
				if (!PolygonalMap2.instance.squareToNode.containsValue(this.nodes.get(int1))) {
					((PolygonalMap2.Node)this.nodes.get(int1)).release();
				}
			}

			for (int1 = 0; int1 < this.perimeterEdges.size(); ++int1) {
				((PolygonalMap2.Edge)this.perimeterEdges.get(int1)).node1.release();
				((PolygonalMap2.Edge)this.perimeterEdges.get(int1)).release();
			}

			for (int1 = 0; int1 < this.obstacles.size(); ++int1) {
				PolygonalMap2.Obstacle obstacle = (PolygonalMap2.Obstacle)this.obstacles.get(int1);
				obstacle.release();
			}

			for (int1 = 0; int1 < this.cluster.rects.size(); ++int1) {
				((PolygonalMap2.VehicleRect)this.cluster.rects.get(int1)).release();
			}

			this.cluster.release();
			assert !pool.contains(this);
			pool.push(this);
		}

		void render() {
			float float1 = 1.0F;
			Iterator iterator;
			for (iterator = this.perimeterEdges.iterator(); iterator.hasNext(); float1 = 1.0F - float1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)iterator.next();
				LineDrawer.addLine(edge.node1.x, edge.node1.y, (float)this.cluster.z, edge.node2.x, edge.node2.y, (float)this.cluster.z, float1, 0.5F, 0.5F, (String)null, true);
			}

			iterator = this.obstacles.iterator();
			while (true) {
				Iterator iterator2;
				Iterator iterator3;
				PolygonalMap2.Obstacle obstacle;
				do {
					if (!iterator.hasNext()) {
						iterator = this.perimeterNodes.iterator();
						PolygonalMap2.Node node;
						PolygonalMap2.Connection connection;
						PolygonalMap2.Node node2;
						while (iterator.hasNext()) {
							node = (PolygonalMap2.Node)iterator.next();
							if (DebugOptions.instance.PolymapRenderConnections.getValue()) {
								iterator2 = node.visible.iterator();
								while (iterator2.hasNext()) {
									connection = (PolygonalMap2.Connection)iterator2.next();
									node2 = connection.otherNode(node);
									LineDrawer.addLine(node.x, node.y, (float)this.cluster.z, node2.x, node2.y, (float)this.cluster.z, 0.0F, 0.25F, 0.0F, (String)null, true);
								}
							}

							if (DebugOptions.instance.PolymapRenderNodes.getValue()) {
								float float2 = 1.0F;
								float float3 = 0.5F;
								float float4 = 0.0F;
								if (node.ignore) {
									float3 = 1.0F;
								}

								LineDrawer.addLine(node.x - 0.05F, node.y - 0.05F, (float)this.cluster.z, node.x + 0.05F, node.y + 0.05F, (float)this.cluster.z, float2, float3, float4, (String)null, false);
							}
						}

						iterator = this.nodes.iterator();
						while (true) {
							do {
								if (!iterator.hasNext()) {
									iterator = this.intersectNodes.iterator();
									while (iterator.hasNext()) {
										node = (PolygonalMap2.Node)iterator.next();
										LineDrawer.addLine(node.x - 0.1F, node.y - 0.1F, (float)this.cluster.z, node.x + 0.1F, node.y + 0.1F, (float)this.cluster.z, 1.0F, 0.0F, 0.0F, (String)null, false);
									}

									return;
								}

								node = (PolygonalMap2.Node)iterator.next();
								if (DebugOptions.instance.PolymapRenderConnections.getValue()) {
									iterator2 = node.visible.iterator();
									while (iterator2.hasNext()) {
										connection = (PolygonalMap2.Connection)iterator2.next();
										node2 = connection.otherNode(node);
										if (this.nodes.contains(node2)) {
											LineDrawer.addLine(node.x, node.y, (float)this.cluster.z, node2.x, node2.y, (float)this.cluster.z, 0.0F, 1.0F, 0.0F, (String)null, true);
										}
									}
								}
							}				 while (!DebugOptions.instance.PolymapRenderNodes.getValue() && !node.ignore);

							LineDrawer.addLine(node.x - 0.05F, node.y - 0.05F, (float)this.cluster.z, node.x + 0.05F, node.y + 0.05F, (float)this.cluster.z, 1.0F, 1.0F, 0.0F, (String)null, false);
						}
					}

					obstacle = (PolygonalMap2.Obstacle)iterator.next();
					float1 = 1.0F;
					for (iterator2 = obstacle.outer.iterator(); iterator2.hasNext(); float1 = 1.0F - float1) {
						PolygonalMap2.Edge edge2 = (PolygonalMap2.Edge)iterator2.next();
						LineDrawer.addLine(edge2.node1.x, edge2.node1.y, (float)this.cluster.z, edge2.node2.x, edge2.node2.y, (float)this.cluster.z, float1, 0.5F, 0.5F, (String)null, true);
					}

					iterator2 = obstacle.inner.iterator();
					while (iterator2.hasNext()) {
						PolygonalMap2.EdgeRing edgeRing = (PolygonalMap2.EdgeRing)iterator2.next();
						for (iterator3 = edgeRing.iterator(); iterator3.hasNext(); float1 = 1.0F - float1) {
							PolygonalMap2.Edge edge3 = (PolygonalMap2.Edge)iterator3.next();
							LineDrawer.addLine(edge3.node1.x, edge3.node1.y, (float)this.cluster.z, edge3.node2.x, edge3.node2.y, (float)this.cluster.z, float1, 0.5F, 0.5F, (String)null, true);
						}
					}
				}	 while (!DebugOptions.instance.PolymapRenderCrawling.getValue());

				iterator2 = obstacle.crawlNodes.iterator();
				while (iterator2.hasNext()) {
					PolygonalMap2.Node node3 = (PolygonalMap2.Node)iterator2.next();
					LineDrawer.addLine(node3.x - 0.05F, node3.y - 0.05F, (float)this.cluster.z, node3.x + 0.05F, node3.y + 0.05F, (float)this.cluster.z, 0.5F, 1.0F, 0.5F, (String)null, false);
					iterator3 = node3.visible.iterator();
					while (iterator3.hasNext()) {
						PolygonalMap2.Connection connection2 = (PolygonalMap2.Connection)iterator3.next();
						PolygonalMap2.Node node4 = connection2.otherNode(node3);
						if (node4.hasFlag(1)) {
							LineDrawer.addLine(node3.x, node3.y, (float)this.cluster.z, node4.x, node4.y, (float)this.cluster.z, 0.5F, 1.0F, 0.5F, (String)null, true);
						}
					}
				}
			}
		}

		static final class CompareIntersection implements Comparator {
			PolygonalMap2.Edge edge;

			public int compare(PolygonalMap2.Intersection intersection, PolygonalMap2.Intersection intersection2) {
				float float1 = this.edge == intersection.edge1 ? intersection.dist1 : intersection.dist2;
				float float2 = this.edge == intersection2.edge1 ? intersection2.dist1 : intersection2.dist2;
				if (float1 < float2) {
					return -1;
				} else {
					return float1 > float2 ? 1 : 0;
				}
			}
		}
	}

	private static final class Connection {
		PolygonalMap2.Node node1;
		PolygonalMap2.Node node2;
		int flags;
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.Connection init(PolygonalMap2.Node node, PolygonalMap2.Node node2, int int1) {
			this.node1 = node;
			this.node2 = node2;
			this.flags = int1;
			return this;
		}

		PolygonalMap2.Node otherNode(PolygonalMap2.Node node) {
			assert node == this.node1 || node == this.node2;
			return node == this.node1 ? this.node2 : this.node1;
		}

		boolean has(int int1) {
			return (this.flags & int1) != 0;
		}

		static PolygonalMap2.Connection alloc() {
			boolean boolean1;
			if (pool.isEmpty()) {
				boolean1 = false;
			} else {
				boolean1 = false;
			}

			return pool.isEmpty() ? new PolygonalMap2.Connection() : (PolygonalMap2.Connection)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			pool.push(this);
		}
	}

	private static final class Obstacle {
		PolygonalMap2.Vehicle vehicle;
		final PolygonalMap2.EdgeRing outer = new PolygonalMap2.EdgeRing();
		final ArrayList inner = new ArrayList();
		PolygonalMap2.ImmutableRectF bounds;
		PolygonalMap2.Node nodeCrawlFront;
		PolygonalMap2.Node nodeCrawlRear;
		final ArrayList crawlNodes = new ArrayList();
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.Obstacle init(PolygonalMap2.Vehicle vehicle) {
			this.vehicle = vehicle;
			this.outer.clear();
			this.inner.clear();
			this.nodeCrawlFront = this.nodeCrawlRear = null;
			this.crawlNodes.clear();
			return this;
		}

		PolygonalMap2.Obstacle init(IsoGridSquare square) {
			this.vehicle = null;
			this.outer.clear();
			this.inner.clear();
			this.nodeCrawlFront = this.nodeCrawlRear = null;
			this.crawlNodes.clear();
			return this;
		}

		boolean hasNode(PolygonalMap2.Node node) {
			if (this.outer.hasNode(node)) {
				return true;
			} else {
				for (int int1 = 0; int1 < this.inner.size(); ++int1) {
					PolygonalMap2.EdgeRing edgeRing = (PolygonalMap2.EdgeRing)this.inner.get(int1);
					if (edgeRing.hasNode(node)) {
						return true;
					}
				}

				return false;
			}
		}

		boolean hasAdjacentNodes(PolygonalMap2.Node node, PolygonalMap2.Node node2) {
			if (this.outer.hasAdjacentNodes(node, node2)) {
				return true;
			} else {
				for (int int1 = 0; int1 < this.inner.size(); ++int1) {
					PolygonalMap2.EdgeRing edgeRing = (PolygonalMap2.EdgeRing)this.inner.get(int1);
					if (edgeRing.hasAdjacentNodes(node, node2)) {
						return true;
					}
				}

				return false;
			}
		}

		boolean isPointInside(float float1, float float2, int int1) {
			if (this.outer.isPointInPolygon_WindingNumber(float1, float2, int1) != PolygonalMap2.EdgeRingHit.Inside) {
				return false;
			} else if (this.inner.isEmpty()) {
				return true;
			} else {
				for (int int2 = 0; int2 < this.inner.size(); ++int2) {
					PolygonalMap2.EdgeRing edgeRing = (PolygonalMap2.EdgeRing)this.inner.get(int2);
					if (edgeRing.isPointInPolygon_WindingNumber(float1, float2, int1) != PolygonalMap2.EdgeRingHit.Outside) {
						return false;
					}
				}

				return true;
			}
		}

		boolean isPointInside(float float1, float float2) {
			byte byte1 = 0;
			return this.isPointInside(float1, float2, byte1);
		}

		boolean lineSegmentIntersects(float float1, float float2, float float3, float float4) {
			if (this.outer.lineSegmentIntersects(float1, float2, float3, float4)) {
				return true;
			} else {
				for (int int1 = 0; int1 < this.inner.size(); ++int1) {
					PolygonalMap2.EdgeRing edgeRing = (PolygonalMap2.EdgeRing)this.inner.get(int1);
					if (edgeRing.lineSegmentIntersects(float1, float2, float3, float4)) {
						return true;
					}
				}

				return false;
			}
		}

		boolean isNodeInsideOf(PolygonalMap2.Node node) {
			if (this.hasNode(node)) {
				return false;
			} else {
				return !this.bounds.containsPoint(node.x, node.y) ? false : this.isPointInside(node.x, node.y);
			}
		}

		void getClosestPointOnEdge(float float1, float float2, PolygonalMap2.ClosestPointOnEdge closestPointOnEdge) {
			closestPointOnEdge.edge = null;
			closestPointOnEdge.node = null;
			closestPointOnEdge.distSq = Double.MAX_VALUE;
			this.outer.getClosestPointOnEdge(float1, float2, closestPointOnEdge);
			for (int int1 = 0; int1 < this.inner.size(); ++int1) {
				PolygonalMap2.EdgeRing edgeRing = (PolygonalMap2.EdgeRing)this.inner.get(int1);
				edgeRing.getClosestPointOnEdge(float1, float2, closestPointOnEdge);
			}
		}

		boolean splitEdgeAtNearestPoint(PolygonalMap2.ClosestPointOnEdge closestPointOnEdge, int int1, PolygonalMap2.AdjustStartEndNodeData adjustStartEndNodeData) {
			if (closestPointOnEdge.edge == null) {
				return false;
			} else {
				adjustStartEndNodeData.obstacle = this;
				if (closestPointOnEdge.node == null) {
					adjustStartEndNodeData.node = PolygonalMap2.Node.alloc().init(closestPointOnEdge.point.x, closestPointOnEdge.point.y, int1);
					adjustStartEndNodeData.newEdge = closestPointOnEdge.edge.split(adjustStartEndNodeData.node);
					adjustStartEndNodeData.isNodeNew = true;
				} else {
					adjustStartEndNodeData.node = closestPointOnEdge.node;
					adjustStartEndNodeData.newEdge = null;
					adjustStartEndNodeData.isNodeNew = false;
				}

				return true;
			}
		}

		void unsplit(PolygonalMap2.Node node, ArrayList arrayList) {
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)arrayList.get(int1);
				if (edge.node1 == node) {
					if (int1 > 0) {
						PolygonalMap2.Edge edge2 = (PolygonalMap2.Edge)arrayList.get(int1 - 1);
						edge2.node2 = edge.node2;
						assert edge.node2.edges.contains(edge);
						edge.node2.edges.remove(edge);
						assert !edge.node2.edges.contains(edge2);
						edge.node2.edges.add(edge2);
						PolygonalMap2.instance.connectTwoNodes(edge2.node1, edge2.node2);
					} else {
						((PolygonalMap2.Edge)arrayList.get(int1 + 1)).node1 = ((PolygonalMap2.Edge)arrayList.get(arrayList.size() - 1)).node2;
					}

					edge.release();
					arrayList.remove(int1);
					break;
				}
			}
		}

		void calcBounds() {
			float float1 = Float.MAX_VALUE;
			float float2 = Float.MAX_VALUE;
			float float3 = Float.MIN_VALUE;
			float float4 = Float.MIN_VALUE;
			for (int int1 = 0; int1 < this.outer.size(); ++int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)this.outer.get(int1);
				float1 = Math.min(float1, edge.node1.x);
				float2 = Math.min(float2, edge.node1.y);
				float3 = Math.max(float3, edge.node1.x);
				float4 = Math.max(float4, edge.node1.y);
			}

			if (this.bounds != null) {
				this.bounds.release();
			}

			float float5 = 0.01F;
			this.bounds = PolygonalMap2.ImmutableRectF.alloc().init(float1 - float5, float2 - float5, float3 - float1 + float5 * 2.0F, float4 - float2 + float5 * 2.0F);
		}

		void render(ArrayList arrayList, boolean boolean1) {
			if (!arrayList.isEmpty()) {
				float float1 = 0.0F;
				float float2 = boolean1 ? 1.0F : 0.5F;
				float float3 = boolean1 ? 0.0F : 0.5F;
				for (Iterator iterator = arrayList.iterator(); iterator.hasNext(); float1 = 1.0F - float1) {
					PolygonalMap2.Edge edge = (PolygonalMap2.Edge)iterator.next();
					PolygonalMap2.Node node = edge.node1;
					PolygonalMap2.Node node2 = edge.node2;
					LineDrawer.addLine(node.x, node.y, (float)node.z, node2.x, node2.y, (float)node2.z, float1, float2, float3, (String)null, true);
					Vector3f vector3f = (new Vector3f(node2.x - node.x, node2.y - node.y, (float)(node2.z - node.z))).normalize();
					Vector3f vector3f2 = (new Vector3f(vector3f)).cross(0.0F, 0.0F, 1.0F).normalize();
					vector3f.mul(0.9F);
					LineDrawer.addLine(node2.x - vector3f.x * 0.1F - vector3f2.x * 0.1F, node2.y - vector3f.y * 0.1F - vector3f2.y * 0.1F, (float)node2.z, node2.x, node2.y, (float)node2.z, float1, float2, float3, (String)null, true);
					LineDrawer.addLine(node2.x - vector3f.x * 0.1F + vector3f2.x * 0.1F, node2.y - vector3f.y * 0.1F + vector3f2.y * 0.1F, (float)node2.z, node2.x, node2.y, (float)node2.z, float1, float2, float3, (String)null, true);
				}

				PolygonalMap2.Node node3 = ((PolygonalMap2.Edge)arrayList.get(0)).node1;
				LineDrawer.addLine(node3.x - 0.1F, node3.y - 0.1F, (float)node3.z, node3.x + 0.1F, node3.y + 0.1F, (float)node3.z, 1.0F, 0.0F, 0.0F, (String)null, false);
			}
		}

		void render() {
			this.render(this.outer, true);
			for (int int1 = 0; int1 < this.inner.size(); ++int1) {
				this.render((ArrayList)this.inner.get(int1), false);
			}
		}

		void connectCrawlNodes(PolygonalMap2.VisibilityGraph visibilityGraph, PolygonalMap2.Obstacle obstacle) {
			this.connectCrawlNode(visibilityGraph, obstacle, this.nodeCrawlFront, obstacle.nodeCrawlFront);
			this.connectCrawlNode(visibilityGraph, obstacle, this.nodeCrawlFront, obstacle.nodeCrawlRear);
			this.connectCrawlNode(visibilityGraph, obstacle, this.nodeCrawlRear, obstacle.nodeCrawlFront);
			this.connectCrawlNode(visibilityGraph, obstacle, this.nodeCrawlRear, obstacle.nodeCrawlRear);
			for (int int1 = 0; int1 < this.crawlNodes.size(); int1 += 3) {
				PolygonalMap2.Node node = (PolygonalMap2.Node)this.crawlNodes.get(int1);
				PolygonalMap2.Node node2 = (PolygonalMap2.Node)this.crawlNodes.get(int1 + 2);
				for (int int2 = 0; int2 < obstacle.crawlNodes.size(); int2 += 3) {
					PolygonalMap2.Node node3 = (PolygonalMap2.Node)obstacle.crawlNodes.get(int2);
					PolygonalMap2.Node node4 = (PolygonalMap2.Node)obstacle.crawlNodes.get(int2 + 2);
					this.connectCrawlNode(visibilityGraph, obstacle, node, node3);
					this.connectCrawlNode(visibilityGraph, obstacle, node, node4);
					this.connectCrawlNode(visibilityGraph, obstacle, node2, node3);
					this.connectCrawlNode(visibilityGraph, obstacle, node2, node4);
				}
			}
		}

		void connectCrawlNode(PolygonalMap2.VisibilityGraph visibilityGraph, PolygonalMap2.Obstacle obstacle, PolygonalMap2.Node node, PolygonalMap2.Node node2) {
			if (this.isNodeInsideOf(node2)) {
				node2.flags |= 2;
				node = this.getClosestInteriorCrawlNode(node2.x, node2.y);
				if (node != null) {
					if (!node.isConnectedTo(node2)) {
						PolygonalMap2.instance.connectTwoNodes(node, node2);
					}
				}
			} else if (!node.ignore && !node2.ignore) {
				if (!node.isConnectedTo(node2)) {
					if (visibilityGraph.isVisible(node, node2)) {
						PolygonalMap2.instance.connectTwoNodes(node, node2);
					}
				}
			}
		}

		PolygonalMap2.Node getClosestInteriorCrawlNode(float float1, float float2) {
			PolygonalMap2.Node node = null;
			float float3 = Float.MAX_VALUE;
			for (int int1 = 0; int1 < this.crawlNodes.size(); int1 += 3) {
				PolygonalMap2.Node node2 = (PolygonalMap2.Node)this.crawlNodes.get(int1 + 1);
				float float4 = IsoUtils.DistanceToSquared(node2.x, node2.y, float1, float2);
				if (float4 < float3) {
					node = node2;
					float3 = float4;
				}
			}

			return node;
		}

		static PolygonalMap2.Obstacle alloc() {
			return pool.isEmpty() ? new PolygonalMap2.Obstacle() : (PolygonalMap2.Obstacle)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			this.outer.release();
			this.outer.clear();
			PolygonalMap2.EdgeRing.releaseAll(this.inner);
			this.inner.clear();
			pool.push(this);
		}

		static void releaseAll(ArrayList arrayList) {
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				((PolygonalMap2.Obstacle)arrayList.get(int1)).release();
			}
		}
	}

	static final class PathFindRequest {
		PolygonalMap2.IPathfinder finder;
		Mover mover;
		boolean bCanCrawl;
		boolean bIgnoreCrawlCost;
		boolean bCanThump;
		final ArrayList knownBlockedEdges = new ArrayList();
		float startX;
		float startY;
		float startZ;
		float targetX;
		float targetY;
		float targetZ;
		final TFloatArrayList targetXYZ = new TFloatArrayList();
		final PolygonalMap2.Path path = new PolygonalMap2.Path();
		boolean cancel = false;
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.PathFindRequest init(PolygonalMap2.IPathfinder iPathfinder, Mover mover, float float1, float float2, float float3, float float4, float float5, float float6) {
			this.finder = iPathfinder;
			this.mover = mover;
			this.bCanCrawl = false;
			this.bIgnoreCrawlCost = false;
			this.bCanThump = false;
			IsoZombie zombie = (IsoZombie)Type.tryCastTo(mover, IsoZombie.class);
			if (zombie != null) {
				this.bCanCrawl = zombie.isCrawling() || zombie.isCanCrawlUnderVehicle();
				this.bIgnoreCrawlCost = zombie.isCrawling() && !zombie.isCanWalk();
				this.bCanThump = true;
			}

			this.startX = float1;
			this.startY = float2;
			this.startZ = float3;
			this.targetX = float4;
			this.targetY = float5;
			this.targetZ = float6;
			this.targetXYZ.resetQuick();
			this.path.clear();
			this.cancel = false;
			IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(mover, IsoGameCharacter.class);
			if (gameCharacter != null) {
				ArrayList arrayList = gameCharacter.getMapKnowledge().getKnownBlockedEdges();
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					KnownBlockedEdges knownBlockedEdges = (KnownBlockedEdges)arrayList.get(int1);
					this.knownBlockedEdges.add(KnownBlockedEdges.alloc().init(knownBlockedEdges));
				}
			}

			return this;
		}

		void addTargetXYZ(float float1, float float2, float float3) {
			this.targetXYZ.add(float1);
			this.targetXYZ.add(float2);
			this.targetXYZ.add(float3);
		}

		static PolygonalMap2.PathFindRequest alloc() {
			return pool.isEmpty() ? new PolygonalMap2.PathFindRequest() : (PolygonalMap2.PathFindRequest)pool.pop();
		}

		public void release() {
			KnownBlockedEdges.releaseAll(this.knownBlockedEdges);
			this.knownBlockedEdges.clear();
			assert !pool.contains(this);
			pool.push(this);
		}
	}

	static final class PathNode {
		float x;
		float y;
		float z;
		int flags;

		PolygonalMap2.PathNode init(float float1, float float2, float float3, int int1) {
			this.x = float1;
			this.y = float2;
			this.z = float3;
			this.flags = int1;
			return this;
		}

		PolygonalMap2.PathNode init(PolygonalMap2.PathNode pathNode) {
			this.x = pathNode.x;
			this.y = pathNode.y;
			this.z = pathNode.z;
			this.flags = pathNode.flags;
			return this;
		}

		boolean hasFlag(int int1) {
			return (this.flags & int1) != 0;
		}
	}

	private static final class SearchNode extends ASearchNode {
		PolygonalMap2.VGAStar astar;
		PolygonalMap2.Node vgNode;
		PolygonalMap2.Square square;
		int tx;
		int ty;
		PolygonalMap2.SearchNode parent;
		static int nextID = 1;
		Integer ID;
		private static final double SQRT2 = Math.sqrt(2.0);
		static final ArrayDeque pool = new ArrayDeque();

		SearchNode() {
			this.ID = nextID++;
		}

		PolygonalMap2.SearchNode init(PolygonalMap2.VGAStar vGAStar, PolygonalMap2.Node node) {
			this.setG(0.0);
			this.astar = vGAStar;
			this.vgNode = node;
			this.square = null;
			this.tx = this.ty = -1;
			this.parent = null;
			return this;
		}

		PolygonalMap2.SearchNode init(PolygonalMap2.VGAStar vGAStar, PolygonalMap2.Square square) {
			this.setG(0.0);
			this.astar = vGAStar;
			this.vgNode = null;
			this.square = square;
			this.tx = this.ty = -1;
			this.parent = null;
			return this;
		}

		PolygonalMap2.SearchNode init(PolygonalMap2.VGAStar vGAStar, int int1, int int2) {
			this.setG(0.0);
			this.astar = vGAStar;
			this.vgNode = null;
			this.square = null;
			this.tx = int1;
			this.ty = int2;
			this.parent = null;
			return this;
		}

		public double h() {
			return this.dist(this.astar.goalNode.searchNode);
		}

		public double c(ISearchNode iSearchNode) {
			PolygonalMap2.SearchNode searchNode = (PolygonalMap2.SearchNode)iSearchNode;
			double double1 = 0.0;
			boolean boolean1 = this.astar.mover instanceof IsoZombie && ((IsoZombie)this.astar.mover).bCrawling;
			boolean boolean2 = !(this.astar.mover instanceof IsoZombie) || ((IsoZombie)this.astar.mover).bCrawling;
			if (boolean2 && this.square != null && searchNode.square != null) {
				if (this.square.x == searchNode.square.x - 1 && this.square.y == searchNode.square.y) {
					if (searchNode.square.has(2048)) {
						double1 = !boolean1 && searchNode.square.has(1048576) ? 20.0 : 200.0;
					}
				} else if (this.square.x == searchNode.square.x + 1 && this.square.y == searchNode.square.y) {
					if (this.square.has(2048)) {
						double1 = !boolean1 && this.square.has(1048576) ? 20.0 : 200.0;
					}
				} else if (this.square.y == searchNode.square.y - 1 && this.square.x == searchNode.square.x) {
					if (searchNode.square.has(4096)) {
						double1 = !boolean1 && searchNode.square.has(2097152) ? 20.0 : 200.0;
					}
				} else if (this.square.y == searchNode.square.y + 1 && this.square.x == searchNode.square.x && this.square.has(4096)) {
					double1 = !boolean1 && this.square.has(2097152) ? 20.0 : 200.0;
				}
			}

			if (searchNode.square != null && searchNode.square.has(131072)) {
				double1 = 20.0;
			}

			if (this.vgNode != null && searchNode.vgNode != null) {
				for (int int1 = 0; int1 < this.vgNode.visible.size(); ++int1) {
					PolygonalMap2.Connection connection = (PolygonalMap2.Connection)this.vgNode.visible.get(int1);
					if (connection.otherNode(this.vgNode) == searchNode.vgNode) {
						if ((this.vgNode.square == null || !this.vgNode.square.has(131072)) && connection.has(2)) {
							double1 = 20.0;
						}

						break;
					}
				}
			}

			PolygonalMap2.Square square = this.square == null ? PolygonalMap2.instance.getSquare((int)this.vgNode.x, (int)this.vgNode.y, 0) : this.square;
			PolygonalMap2.Square square2 = searchNode.square == null ? PolygonalMap2.instance.getSquare((int)searchNode.vgNode.x, (int)searchNode.vgNode.y, 0) : searchNode.square;
			if (square != null && square2 != null) {
				if (square.x == square2.x - 1 && square.y == square2.y) {
					if (square2.has(32768)) {
						double1 = 20.0;
					}
				} else if (square.x == square2.x + 1 && square.y == square2.y) {
					if (square.has(32768)) {
						double1 = 20.0;
					}
				} else if (square.y == square2.y - 1 && square.x == square2.x) {
					if (square2.has(65536)) {
						double1 = 20.0;
					}
				} else if (square.y == square2.y + 1 && square.x == square2.x && square.has(65536)) {
					double1 = 20.0;
				}

				if (boolean1) {
					if (square.x == square2.x - 1 && square.y == square2.y) {
						if (square2.has(2) && square2.has(8192)) {
							double1 = 20.0;
						}
					} else if (square.x == square2.x + 1 && square.y == square2.y) {
						if (square.has(2) && square.has(8192)) {
							double1 = 20.0;
						}
					} else if (square.y == square2.y - 1 && square.x == square2.x) {
						if (square2.has(4) && square2.has(16384)) {
							double1 = 20.0;
						}
					} else if (square.y == square2.y + 1 && square.x == square2.x && square.has(4) && square.has(16384)) {
						double1 = 20.0;
					}
				}
			}

			boolean boolean3 = this.vgNode != null && this.vgNode.hasFlag(2);
			boolean boolean4 = searchNode.vgNode != null && searchNode.vgNode.hasFlag(2);
			if (!boolean3 && boolean4 && !this.astar.bIgnoreCrawlCost) {
				double1 += 10.0;
			}

			if (searchNode.square != null) {
				double1 += (double)searchNode.square.cost;
			}

			return this.dist(searchNode) + double1;
		}

		public void getSuccessors(ArrayList arrayList) {
			ArrayList arrayList2 = arrayList;
			int int1;
			PolygonalMap2.SearchNode searchNode;
			if (this.vgNode != null) {
				if (this.vgNode.graphs != null) {
					for (int1 = 0; int1 < this.vgNode.graphs.size(); ++int1) {
						PolygonalMap2.VisibilityGraph visibilityGraph = (PolygonalMap2.VisibilityGraph)this.vgNode.graphs.get(int1);
						if (!visibilityGraph.created) {
							visibilityGraph.create();
						}
					}
				}

				for (int1 = 0; int1 < this.vgNode.visible.size(); ++int1) {
					PolygonalMap2.Connection connection = (PolygonalMap2.Connection)this.vgNode.visible.get(int1);
					PolygonalMap2.Node node = connection.otherNode(this.vgNode);
					searchNode = this.astar.getSearchNode(node);
					if ((this.vgNode.square == null || searchNode.square == null || !this.astar.isKnownBlocked(this.vgNode.square, searchNode.square)) && (this.astar.bCanCrawl || !node.hasFlag(2)) && (this.astar.bCanThump || !connection.has(2))) {
						arrayList2.add(searchNode);
					}
				}

				if (!this.vgNode.hasFlag(8)) {
					return;
				}
			}

			if (this.square != null) {
				for (int1 = -1; int1 <= 1; ++int1) {
					for (int int2 = -1; int2 <= 1; ++int2) {
						if (int2 != 0 || int1 != 0) {
							PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(this.square.x + int2, this.square.y + int1, this.square.z);
							if (square != null && !this.astar.isSquareInCluster(square) && !this.astar.canNotMoveBetween(this.square, square, false)) {
								searchNode = this.astar.getSearchNode(square);
								if (arrayList2.contains(searchNode)) {
									boolean boolean1 = false;
								} else {
									arrayList2.add(searchNode);
								}
							}
						}
					}
				}

				PolygonalMap2.Square square2;
				PolygonalMap2.SearchNode searchNode2;
				boolean boolean2;
				if (this.square.z > 0) {
					square2 = PolygonalMap2.instance.getSquare(this.square.x, this.square.y + 1, this.square.z - 1);
					if (square2 != null && square2.has(64) && !this.astar.isSquareInCluster(square2)) {
						searchNode2 = this.astar.getSearchNode(square2);
						if (arrayList2.contains(searchNode2)) {
							boolean2 = false;
						} else {
							arrayList2.add(searchNode2);
						}
					}

					square2 = PolygonalMap2.instance.getSquare(this.square.x + 1, this.square.y, this.square.z - 1);
					if (square2 != null && square2.has(8) && !this.astar.isSquareInCluster(square2)) {
						searchNode2 = this.astar.getSearchNode(square2);
						if (arrayList2.contains(searchNode2)) {
							boolean2 = false;
						} else {
							arrayList2.add(searchNode2);
						}
					}
				}

				if (this.square.z < 8 && this.square.has(64)) {
					square2 = PolygonalMap2.instance.getSquare(this.square.x, this.square.y - 1, this.square.z + 1);
					if (square2 != null && !this.astar.isSquareInCluster(square2)) {
						searchNode2 = this.astar.getSearchNode(square2);
						if (arrayList2.contains(searchNode2)) {
							boolean2 = false;
						} else {
							arrayList2.add(searchNode2);
						}
					}
				}

				if (this.square.z < 8 && this.square.has(8)) {
					square2 = PolygonalMap2.instance.getSquare(this.square.x - 1, this.square.y, this.square.z + 1);
					if (square2 != null && !this.astar.isSquareInCluster(square2)) {
						searchNode2 = this.astar.getSearchNode(square2);
						if (arrayList2.contains(searchNode2)) {
							boolean2 = false;
						} else {
							arrayList2.add(searchNode2);
						}
					}
				}
			}
		}

		public ISearchNode getParent() {
			return this.parent;
		}

		public void setParent(ISearchNode iSearchNode) {
			this.parent = (PolygonalMap2.SearchNode)iSearchNode;
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

		public double dist(PolygonalMap2.SearchNode searchNode) {
			if (this.square != null && searchNode.square != null && Math.abs(this.square.x - searchNode.square.x) <= 1 && Math.abs(this.square.y - searchNode.square.y) <= 1) {
				return this.square.x != searchNode.square.x && this.square.y != searchNode.square.y ? SQRT2 : 1.0;
			} else {
				float float1 = this.getX();
				float float2 = this.getY();
				float float3 = searchNode.getX();
				float float4 = searchNode.getY();
				return Math.sqrt(Math.pow((double)(float1 - float3), 2.0) + Math.pow((double)(float2 - float4), 2.0));
			}
		}

		float getApparentZ() {
			if (this.square == null) {
				return (float)this.vgNode.z;
			} else if (!this.square.has(8) && !this.square.has(64)) {
				if (!this.square.has(16) && !this.square.has(128)) {
					return !this.square.has(32) && !this.square.has(256) ? (float)this.square.z : (float)this.square.z + 0.25F;
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

	private static final class Edge {
		PolygonalMap2.Node node1;
		PolygonalMap2.Node node2;
		PolygonalMap2.Obstacle obstacle;
		PolygonalMap2.EdgeRing edgeRing;
		final ArrayList intersections = new ArrayList();
		final Vector2 normal = new Vector2();
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.Edge init(PolygonalMap2.Node node, PolygonalMap2.Node node2, PolygonalMap2.Obstacle obstacle, PolygonalMap2.EdgeRing edgeRing) {
			if (node == null) {
				boolean boolean1 = true;
			}

			this.node1 = node;
			this.node2 = node2;
			node.edges.add(this);
			node2.edges.add(this);
			this.obstacle = obstacle;
			this.edgeRing = edgeRing;
			this.intersections.clear();
			this.normal.set(node2.x - node.x, node2.y - node.y);
			this.normal.normalize();
			this.normal.rotate(1.5707964F);
			return this;
		}

		boolean hasNode(PolygonalMap2.Node node) {
			return node == this.node1 || node == this.node2;
		}

		void getClosestPointOnEdge(float float1, float float2, PolygonalMap2.ClosestPointOnEdge closestPointOnEdge) {
			if (this.node1.isConnectedTo(this.node2)) {
				float float3 = this.node1.x;
				float float4 = this.node1.y;
				float float5 = this.node2.x;
				float float6 = this.node2.y;
				double double1 = (double)((float1 - float3) * (float5 - float3) + (float2 - float4) * (float6 - float4)) / (Math.pow((double)(float5 - float3), 2.0) + Math.pow((double)(float6 - float4), 2.0));
				double double2 = (double)float3 + double1 * (double)(float5 - float3);
				double double3 = (double)float4 + double1 * (double)(float6 - float4);
				PolygonalMap2.Node node = null;
				if (double1 <= 0.0) {
					double2 = (double)float3;
					double3 = (double)float4;
					node = this.node1;
				} else if (double1 >= 1.0) {
					double2 = (double)float5;
					double3 = (double)float6;
					node = this.node2;
				}

				double double4 = ((double)float1 - double2) * ((double)float1 - double2) + ((double)float2 - double3) * ((double)float2 - double3);
				if (double4 < closestPointOnEdge.distSq) {
					closestPointOnEdge.point.set((float)double2, (float)double3);
					closestPointOnEdge.distSq = double4;
					closestPointOnEdge.edge = this;
					closestPointOnEdge.node = node;
				}
			}
		}

		boolean isPointOn(float float1, float float2) {
			if (!this.node1.isConnectedTo(this.node2)) {
				return false;
			} else {
				float float3 = this.node1.x;
				float float4 = this.node1.y;
				float float5 = this.node2.x;
				float float6 = this.node2.y;
				double double1 = (double)((float1 - float3) * (float5 - float3) + (float2 - float4) * (float6 - float4)) / (Math.pow((double)(float5 - float3), 2.0) + Math.pow((double)(float6 - float4), 2.0));
				double double2 = (double)float3 + double1 * (double)(float5 - float3);
				double double3 = (double)float4 + double1 * (double)(float6 - float4);
				if (double1 <= 0.0) {
					double2 = (double)float3;
					double3 = (double)float4;
				} else if (double1 >= 1.0) {
					double2 = (double)float5;
					double3 = (double)float6;
				}

				double double4 = ((double)float1 - double2) * ((double)float1 - double2) + ((double)float2 - double3) * ((double)float2 - double3);
				return double4 < 1.0E-6;
			}
		}

		PolygonalMap2.Edge split(PolygonalMap2.Node node) {
			PolygonalMap2.Edge edge = alloc().init(node, this.node2, this.obstacle, this.edgeRing);
			this.edgeRing.add(this.edgeRing.indexOf(this) + 1, edge);
			PolygonalMap2.instance.breakConnection(this.node1, this.node2);
			this.node2.edges.remove(this);
			this.node2 = node;
			this.node2.edges.add(this);
			return edge;
		}

		static PolygonalMap2.Edge alloc() {
			return pool.isEmpty() ? new PolygonalMap2.Edge() : (PolygonalMap2.Edge)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			this.node1 = null;
			this.node2 = null;
			this.obstacle = null;
			this.edgeRing = null;
			this.intersections.clear();
			pool.push(this);
		}

		static void releaseAll(ArrayList arrayList) {
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				((PolygonalMap2.Edge)arrayList.get(int1)).release();
			}
		}
	}

	private static final class EdgeRing extends ArrayList {
		static final ArrayDeque pool = new ArrayDeque();

		public boolean add(PolygonalMap2.Edge edge) {
			assert !this.contains(edge);
			return super.add(edge);
		}

		public boolean hasNode(PolygonalMap2.Node node) {
			for (int int1 = 0; int1 < this.size(); ++int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)this.get(int1);
				if (edge.hasNode(node)) {
					return true;
				}
			}

			return false;
		}

		boolean hasAdjacentNodes(PolygonalMap2.Node node, PolygonalMap2.Node node2) {
			for (int int1 = 0; int1 < this.size(); ++int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)this.get(int1);
				if (edge.hasNode(node) && edge.hasNode(node2)) {
					return true;
				}
			}

			return false;
		}

		boolean isPointInPolygon_CrossingNumber(float float1, float float2) {
			int int1 = 0;
			for (int int2 = 0; int2 < this.size(); ++int2) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)this.get(int2);
				if (edge.node1.y <= float2 && edge.node2.y > float2 || edge.node1.y > float2 && edge.node2.y <= float2) {
					float float3 = (float2 - edge.node1.y) / (edge.node2.y - edge.node1.y);
					if (float1 < edge.node1.x + float3 * (edge.node2.x - edge.node1.x)) {
						++int1;
					}
				}
			}

			return int1 % 2 == 1;
		}

		float isLeft(float float1, float float2, float float3, float float4, float float5, float float6) {
			return (float3 - float1) * (float6 - float2) - (float5 - float1) * (float4 - float2);
		}

		PolygonalMap2.EdgeRingHit isPointInPolygon_WindingNumber(float float1, float float2, int int1) {
			int int2 = 0;
			for (int int3 = 0; int3 < this.size(); ++int3) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)this.get(int3);
				if ((int1 & 16) != 0 && edge.isPointOn(float1, float2)) {
					return PolygonalMap2.EdgeRingHit.OnEdge;
				}

				if (edge.node1.y <= float2) {
					if (edge.node2.y > float2 && this.isLeft(edge.node1.x, edge.node1.y, edge.node2.x, edge.node2.y, float1, float2) > 0.0F) {
						++int2;
					}
				} else if (edge.node2.y <= float2 && this.isLeft(edge.node1.x, edge.node1.y, edge.node2.x, edge.node2.y, float1, float2) < 0.0F) {
					--int2;
				}
			}

			return int2 == 0 ? PolygonalMap2.EdgeRingHit.Outside : PolygonalMap2.EdgeRingHit.Inside;
		}

		boolean lineSegmentIntersects(float float1, float float2, float float3, float float4) {
			Vector2 vector2 = PolygonalMap2.L_lineSegmentIntersects.v1;
			vector2.set(float3 - float1, float4 - float2);
			float float5 = vector2.getLength();
			vector2.normalize();
			float float6 = vector2.x;
			float float7 = vector2.y;
			for (int int1 = 0; int1 < this.size(); ++int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)this.get(int1);
				if (!edge.isPointOn(float1, float2) && !edge.isPointOn(float3, float4)) {
					float float8 = edge.normal.dot(vector2);
					if (float8 >= 0.01F) {
					}

					float float9 = edge.node1.x;
					float float10 = edge.node1.y;
					float float11 = edge.node2.x;
					float float12 = edge.node2.y;
					float float13 = float1 - float9;
					float float14 = float2 - float10;
					float float15 = float11 - float9;
					float float16 = float12 - float10;
					float float17 = 1.0F / (float16 * float6 - float15 * float7);
					float float18 = (float15 * float14 - float16 * float13) * float17;
					if (float18 >= 0.0F && float18 <= float5) {
						float float19 = (float14 * float6 - float13 * float7) * float17;
						if (float19 >= 0.0F && float19 <= 1.0F) {
							return true;
						}
					}
				}
			}

			if (this.isPointInPolygon_WindingNumber((float1 + float3) / 2.0F, (float2 + float4) / 2.0F, 0) != PolygonalMap2.EdgeRingHit.Outside) {
				return true;
			} else {
				return false;
			}
		}

		void getClosestPointOnEdge(float float1, float float2, PolygonalMap2.ClosestPointOnEdge closestPointOnEdge) {
			for (int int1 = 0; int1 < this.size(); ++int1) {
				PolygonalMap2.Edge edge = (PolygonalMap2.Edge)this.get(int1);
				edge.getClosestPointOnEdge(float1, float2, closestPointOnEdge);
			}
		}

		static PolygonalMap2.EdgeRing alloc() {
			return pool.isEmpty() ? new PolygonalMap2.EdgeRing() : (PolygonalMap2.EdgeRing)pool.pop();
		}

		public void release() {
			PolygonalMap2.Edge.releaseAll(this);
		}

		static void releaseAll(ArrayList arrayList) {
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				((PolygonalMap2.EdgeRing)arrayList.get(int1)).release();
			}
		}
	}

	private static final class Chunk {
		short wx;
		short wy;
		PolygonalMap2.Square[][][] squares = new PolygonalMap2.Square[10][10][8];
		final PolygonalMap2.ChunkData collision = new PolygonalMap2.ChunkData();
		static final ArrayDeque pool = new ArrayDeque();

		void init(int int1, int int2) {
			this.wx = (short)int1;
			this.wy = (short)int2;
		}

		PolygonalMap2.Square getSquare(int int1, int int2, int int3) {
			int1 -= this.wx * 10;
			int2 -= this.wy * 10;
			return int1 >= 0 && int1 < 10 && int2 >= 0 && int2 < 10 && int3 >= 0 && int3 < 8 ? this.squares[int1][int2][int3] : null;
		}

		void setData(PolygonalMap2.ChunkUpdateTask chunkUpdateTask) {
			for (int int1 = 0; int1 < 8; ++int1) {
				for (int int2 = 0; int2 < 10; ++int2) {
					for (int int3 = 0; int3 < 10; ++int3) {
						PolygonalMap2.Square square = this.squares[int3][int2][int1];
						int int4 = chunkUpdateTask.data[int3][int2][int1];
						if (int4 == 0) {
							if (square != null) {
								square.release();
								this.squares[int3][int2][int1] = null;
							}
						} else {
							if (square == null) {
								square = PolygonalMap2.Square.alloc();
								this.squares[int3][int2][int1] = square;
							}

							square.init(this.wx * 10 + int3, this.wy * 10 + int2, int1);
							square.bits = int4;
							square.cost = chunkUpdateTask.cost[int3][int2][int1];
						}
					}
				}
			}

			++PolygonalMap2.ChunkDataZ.EPOCH;
		}

		boolean setData(PolygonalMap2.SquareUpdateTask squareUpdateTask) {
			int int1 = squareUpdateTask.x - this.wx * 10;
			int int2 = squareUpdateTask.y - this.wy * 10;
			if (int1 >= 0 && int1 < 10) {
				if (int2 >= 0 && int2 < 10) {
					PolygonalMap2.Square square = this.squares[int1][int2][squareUpdateTask.z];
					if (squareUpdateTask.bits == 0) {
						if (square != null) {
							square.release();
							this.squares[int1][int2][squareUpdateTask.z] = null;
							return true;
						}
					} else {
						if (square == null) {
							square = PolygonalMap2.Square.alloc().init(squareUpdateTask.x, squareUpdateTask.y, squareUpdateTask.z);
							this.squares[int1][int2][squareUpdateTask.z] = square;
						}

						if (square.bits != squareUpdateTask.bits || square.cost != squareUpdateTask.cost) {
							square.bits = squareUpdateTask.bits;
							square.cost = squareUpdateTask.cost;
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

	private static final class L_render {
		static final Vector2f vector2f = new Vector2f();
	}

	public interface IPathfinder {

		void Succeeded(PolygonalMap2.Path path, Mover mover);

		void Failed(Mover mover);
	}

	private static final class SquareUpdateTask {
		PolygonalMap2 map;
		int x;
		int y;
		int z;
		int bits;
		short cost;
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.SquareUpdateTask init(PolygonalMap2 polygonalMap2, IsoGridSquare square) {
			this.map = polygonalMap2;
			this.x = square.x;
			this.y = square.y;
			this.z = square.z;
			this.bits = getBits(square);
			this.cost = getCost(square);
			return this;
		}

		void execute() {
			PolygonalMap2.Chunk chunk = this.map.getChunkFromChunkPos(this.x / 10, this.y / 10);
			if (chunk != null && chunk.setData(this)) {
				++PolygonalMap2.ChunkDataZ.EPOCH;
				this.map.rebuild = true;
			}
		}

		static int getBits(IsoGridSquare square) {
			int int1 = 0;
			if (square.Is(IsoFlagType.solidfloor)) {
				int1 |= 512;
			}

			if (square.isSolid()) {
				int1 |= 1;
			}

			if (square.isSolidTrans()) {
				int1 |= 1024;
			}

			if (square.Is(IsoFlagType.collideW)) {
				int1 |= 2;
			}

			if (square.Is(IsoFlagType.collideN)) {
				int1 |= 4;
			}

			if (square.Has(IsoObjectType.stairsTW)) {
				int1 |= 8;
			}

			if (square.Has(IsoObjectType.stairsMW)) {
				int1 |= 16;
			}

			if (square.Has(IsoObjectType.stairsBW)) {
				int1 |= 32;
			}

			if (square.Has(IsoObjectType.stairsTN)) {
				int1 |= 64;
			}

			if (square.Has(IsoObjectType.stairsMN)) {
				int1 |= 128;
			}

			if (square.Has(IsoObjectType.stairsBN)) {
				int1 |= 256;
			}

			if (square.Is(IsoFlagType.windowW) || square.Is(IsoFlagType.WindowW)) {
				int1 |= 2050;
				if (isWindowUnblocked(square, false)) {
					int1 |= 1048576;
				}
			}

			if (square.Is(IsoFlagType.windowN) || square.Is(IsoFlagType.WindowN)) {
				int1 |= 4100;
				if (isWindowUnblocked(square, true)) {
					int1 |= 2097152;
				}
			}

			if (square.Is(IsoFlagType.canPathW)) {
				int1 |= 8192;
			}

			if (square.Is(IsoFlagType.canPathN)) {
				int1 |= 16384;
			}

			for (int int2 = 0; int2 < square.getSpecialObjects().size(); ++int2) {
				IsoObject object = (IsoObject)square.getSpecialObjects().get(int2);
				IsoDirections directions = IsoDirections.Max;
				if (object instanceof IsoDoor) {
					directions = ((IsoDoor)object).getSpriteEdge(false);
					if (((IsoDoor)object).IsOpen()) {
						directions = IsoDirections.Max;
					}
				} else if (object instanceof IsoThumpable && ((IsoThumpable)object).isDoor()) {
					directions = ((IsoThumpable)object).getSpriteEdge(false);
					if (((IsoThumpable)object).IsOpen()) {
						directions = IsoDirections.Max;
					}
				}

				if (directions == IsoDirections.W) {
					int1 |= 8192;
					int1 |= 2;
				} else if (directions == IsoDirections.N) {
					int1 |= 16384;
					int1 |= 4;
				} else if (directions == IsoDirections.S) {
					int1 |= 524288;
				} else if (directions == IsoDirections.E) {
					int1 |= 262144;
				}
			}

			if (square.Is(IsoFlagType.DoorWallW)) {
				int1 |= 8192;
				int1 |= 2;
			}

			if (square.Is(IsoFlagType.DoorWallN)) {
				int1 |= 16384;
				int1 |= 4;
			}

			if (hasSquareThumpable(square)) {
				int1 |= 8192;
				int1 |= 16384;
				int1 |= 131072;
			}

			if (hasWallThumpableN(square)) {
				int1 |= 81920;
			}

			if (hasWallThumpableW(square)) {
				int1 |= 40960;
			}

			return int1;
		}

		static boolean isWindowUnblocked(IsoGridSquare square, boolean boolean1) {
			for (int int1 = 0; int1 < square.getSpecialObjects().size(); ++int1) {
				IsoObject object = (IsoObject)square.getSpecialObjects().get(int1);
				if (object instanceof IsoThumpable) {
					IsoThumpable thumpable = (IsoThumpable)object;
					if (thumpable.isWindow() && boolean1 == thumpable.north) {
						if (thumpable.isBarricaded()) {
							return false;
						}

						return true;
					}
				}

				if (object instanceof IsoWindow) {
					IsoWindow window = (IsoWindow)object;
					if (boolean1 == window.north) {
						if (window.isBarricaded()) {
							return false;
						}

						if (window.isInvincible()) {
							return false;
						}

						if (window.IsOpen()) {
							return true;
						}

						if (window.isDestroyed() && window.isGlassRemoved()) {
							return true;
						}

						return false;
					}
				}
			}

			IsoObject object2 = square.getWindowFrame(boolean1);
			if (IsoWindowFrame.canClimbThrough(object2, (IsoGameCharacter)null)) {
				return true;
			} else {
				return false;
			}
		}

		static boolean hasSquareThumpable(IsoGridSquare square) {
			for (int int1 = 0; int1 < square.getSpecialObjects().size(); ++int1) {
				IsoThumpable thumpable = (IsoThumpable)Type.tryCastTo((IsoObject)square.getSpecialObjects().get(int1), IsoThumpable.class);
				if (thumpable != null && thumpable.isThumpable() && thumpable.isBlockAllTheSquare()) {
					return true;
				}
			}

			return false;
		}

		static boolean hasWallThumpableN(IsoGridSquare square) {
			IsoGridSquare square2 = square.getAdjacentSquare(IsoDirections.N);
			if (square2 == null) {
				return false;
			} else {
				for (int int1 = 0; int1 < square.getSpecialObjects().size(); ++int1) {
					IsoThumpable thumpable = (IsoThumpable)Type.tryCastTo((IsoObject)square.getSpecialObjects().get(int1), IsoThumpable.class);
					if (thumpable != null && !thumpable.canClimbThrough((IsoGameCharacter)null) && !thumpable.canClimbOver((IsoGameCharacter)null) && thumpable.isThumpable() && !thumpable.isBlockAllTheSquare() && !thumpable.isDoor() && thumpable.TestCollide((IsoMovingObject)null, square, square2)) {
						return true;
					}
				}

				return false;
			}
		}

		static boolean hasWallThumpableW(IsoGridSquare square) {
			IsoGridSquare square2 = square.getAdjacentSquare(IsoDirections.W);
			if (square2 == null) {
				return false;
			} else {
				for (int int1 = 0; int1 < square.getSpecialObjects().size(); ++int1) {
					IsoThumpable thumpable = (IsoThumpable)Type.tryCastTo((IsoObject)square.getSpecialObjects().get(int1), IsoThumpable.class);
					if (thumpable != null && !thumpable.canClimbThrough((IsoGameCharacter)null) && !thumpable.canClimbOver((IsoGameCharacter)null) && thumpable.isThumpable() && !thumpable.isBlockAllTheSquare() && !thumpable.isDoor() && thumpable.TestCollide((IsoMovingObject)null, square, square2)) {
						return true;
					}
				}

				return false;
			}
		}

		static short getCost(IsoGridSquare square) {
			short short1 = 0;
			if (square.HasTree() || square.getProperties().Is("Bush")) {
				short1 = (short)(short1 + 5);
			}

			return short1;
		}

		static PolygonalMap2.SquareUpdateTask alloc() {
			synchronized (pool) {
				return pool.isEmpty() ? new PolygonalMap2.SquareUpdateTask() : (PolygonalMap2.SquareUpdateTask)pool.pop();
			}
		}

		public void release() {
			synchronized (pool) {
				assert !pool.contains(this);
				pool.push(this);
			}
		}
	}

	private final class PMThread extends Thread {
		public boolean bStop;
		public final Object notifier = new Object();

		public void run() {
			while (!this.bStop) {
				try {
					this.runInner();
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}
			}
		}

		private void runInner() {
			MPStatistic.getInstance().PolyPathThread.Start();
			PolygonalMap2.this.sync.startFrame();
			synchronized (PolygonalMap2.this.renderLock) {
				PolygonalMap2.instance.updateThread();
			}
			PolygonalMap2.this.sync.endFrame();
			MPStatistic.getInstance().PolyPathThread.End();
			while (this.shouldWait()) {
				synchronized (this.notifier) {
					try {
						this.notifier.wait();
					} catch (InterruptedException interruptedException) {
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
			synchronized (this.notifier) {
				this.notifier.notify();
			}
		}
	}

	private static final class ChunkUpdateTask implements PolygonalMap2.IChunkTask {
		PolygonalMap2 map;
		int wx;
		int wy;
		final int[][][] data = new int[10][10][8];
		final short[][][] cost = new short[10][10][8];
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.ChunkUpdateTask init(PolygonalMap2 polygonalMap2, IsoChunk chunk) {
			this.map = polygonalMap2;
			this.wx = chunk.wx;
			this.wy = chunk.wy;
			for (int int1 = 0; int1 < 8; ++int1) {
				for (int int2 = 0; int2 < 10; ++int2) {
					for (int int3 = 0; int3 < 10; ++int3) {
						IsoGridSquare square = chunk.getGridSquare(int3, int2, int1);
						if (square == null) {
							this.data[int3][int2][int1] = 0;
							this.cost[int3][int2][int1] = 0;
						} else {
							this.data[int3][int2][int1] = PolygonalMap2.SquareUpdateTask.getBits(square);
							this.cost[int3][int2][int1] = PolygonalMap2.SquareUpdateTask.getCost(square);
						}
					}
				}
			}

			return this;
		}

		public void execute() {
			PolygonalMap2.Chunk chunk = this.map.allocChunkIfNeeded(this.wx, this.wy);
			chunk.setData(this);
		}

		static PolygonalMap2.ChunkUpdateTask alloc() {
			synchronized (pool) {
				return pool.isEmpty() ? new PolygonalMap2.ChunkUpdateTask() : (PolygonalMap2.ChunkUpdateTask)pool.pop();
			}
		}

		public void release() {
			synchronized (pool) {
				assert !pool.contains(this);
				pool.push(this);
			}
		}
	}

	private static final class ChunkRemoveTask implements PolygonalMap2.IChunkTask {
		PolygonalMap2 map;
		int wx;
		int wy;
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.ChunkRemoveTask init(PolygonalMap2 polygonalMap2, IsoChunk chunk) {
			this.map = polygonalMap2;
			this.wx = chunk.wx;
			this.wy = chunk.wy;
			return this;
		}

		public void execute() {
			PolygonalMap2.Cell cell = this.map.getCellFromChunkPos(this.wx, this.wy);
			cell.removeChunk(this.wx, this.wy);
		}

		static PolygonalMap2.ChunkRemoveTask alloc() {
			synchronized (pool) {
				return pool.isEmpty() ? new PolygonalMap2.ChunkRemoveTask() : (PolygonalMap2.ChunkRemoveTask)pool.pop();
			}
		}

		public void release() {
			synchronized (pool) {
				assert !pool.contains(this);
				pool.push(this);
			}
		}
	}

	private static final class VehicleAddTask implements PolygonalMap2.IVehicleTask {
		PolygonalMap2 map;
		BaseVehicle vehicle;
		final PolygonalMap2.VehiclePoly poly = new PolygonalMap2.VehiclePoly();
		final PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
		final TFloatArrayList crawlOffsets = new TFloatArrayList();
		float upVectorDot;
		static final ArrayDeque pool = new ArrayDeque();

		public void init(PolygonalMap2 polygonalMap2, BaseVehicle baseVehicle) {
			this.map = polygonalMap2;
			this.vehicle = baseVehicle;
			this.poly.init(baseVehicle.getPoly());
			this.polyPlusRadius.init(baseVehicle.getPolyPlusRadius());
			this.crawlOffsets.resetQuick();
			this.crawlOffsets.addAll(baseVehicle.getScript().getCrawlOffsets());
			this.upVectorDot = baseVehicle.getUpVectorDot();
		}

		public void execute() {
			PolygonalMap2.Vehicle vehicle = PolygonalMap2.Vehicle.alloc();
			vehicle.poly.init(this.poly);
			vehicle.polyPlusRadius.init(this.polyPlusRadius);
			vehicle.crawlOffsets.resetQuick();
			vehicle.crawlOffsets.addAll(this.crawlOffsets);
			vehicle.upVectorDot = this.upVectorDot;
			this.map.vehicles.add(vehicle);
			this.map.vehicleMap.put(this.vehicle, vehicle);
			this.vehicle = null;
		}

		static PolygonalMap2.VehicleAddTask alloc() {
			synchronized (pool) {
				return pool.isEmpty() ? new PolygonalMap2.VehicleAddTask() : (PolygonalMap2.VehicleAddTask)pool.pop();
			}
		}

		public void release() {
			synchronized (pool) {
				assert !pool.contains(this);
				pool.push(this);
			}
		}
	}

	private static final class VehicleState {
		BaseVehicle vehicle;
		float x;
		float y;
		float z;
		final Vector3f forward = new Vector3f();
		final PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.VehicleState init(BaseVehicle baseVehicle) {
			this.vehicle = baseVehicle;
			this.x = baseVehicle.x;
			this.y = baseVehicle.y;
			this.z = baseVehicle.z;
			baseVehicle.getForwardVector(this.forward);
			this.polyPlusRadius.init(baseVehicle.getPolyPlusRadius());
			return this;
		}

		boolean check() {
			boolean boolean1 = this.x != this.vehicle.x || this.y != this.vehicle.y || (int)this.z != (int)this.vehicle.z;
			if (!boolean1) {
				BaseVehicle.Vector3fObjectPool vector3fObjectPool = (BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get();
				Vector3f vector3f = this.vehicle.getForwardVector((Vector3f)vector3fObjectPool.alloc());
				boolean1 = this.forward.dot(vector3f) < 0.999F;
				if (boolean1) {
					this.forward.set((Vector3fc)vector3f);
				}

				vector3fObjectPool.release(vector3f);
			}

			if (boolean1) {
				this.x = this.vehicle.x;
				this.y = this.vehicle.y;
				this.z = this.vehicle.z;
			}

			return boolean1;
		}

		static PolygonalMap2.VehicleState alloc() {
			return pool.isEmpty() ? new PolygonalMap2.VehicleState() : (PolygonalMap2.VehicleState)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			pool.push(this);
		}
	}

	private static final class VehicleUpdateTask implements PolygonalMap2.IVehicleTask {
		PolygonalMap2 map;
		BaseVehicle vehicle;
		final PolygonalMap2.VehiclePoly poly = new PolygonalMap2.VehiclePoly();
		final PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
		float upVectorDot;
		static final ArrayDeque pool = new ArrayDeque();

		public void init(PolygonalMap2 polygonalMap2, BaseVehicle baseVehicle) {
			this.map = polygonalMap2;
			this.vehicle = baseVehicle;
			this.poly.init(baseVehicle.getPoly());
			this.polyPlusRadius.init(baseVehicle.getPolyPlusRadius());
			this.upVectorDot = baseVehicle.getUpVectorDot();
		}

		public void execute() {
			PolygonalMap2.Vehicle vehicle = (PolygonalMap2.Vehicle)this.map.vehicleMap.get(this.vehicle);
			vehicle.poly.init(this.poly);
			vehicle.polyPlusRadius.init(this.polyPlusRadius);
			vehicle.upVectorDot = this.upVectorDot;
			this.vehicle = null;
		}

		static PolygonalMap2.VehicleUpdateTask alloc() {
			synchronized (pool) {
				return pool.isEmpty() ? new PolygonalMap2.VehicleUpdateTask() : (PolygonalMap2.VehicleUpdateTask)pool.pop();
			}
		}

		public void release() {
			synchronized (pool) {
				assert !pool.contains(this);
				pool.push(this);
			}
		}
	}

	private static final class VehicleRemoveTask implements PolygonalMap2.IVehicleTask {
		PolygonalMap2 map;
		BaseVehicle vehicle;
		static final ArrayDeque pool = new ArrayDeque();

		public void init(PolygonalMap2 polygonalMap2, BaseVehicle baseVehicle) {
			this.map = polygonalMap2;
			this.vehicle = baseVehicle;
		}

		public void execute() {
			PolygonalMap2.Vehicle vehicle = (PolygonalMap2.Vehicle)this.map.vehicleMap.remove(this.vehicle);
			if (vehicle != null) {
				this.map.vehicles.remove(vehicle);
				vehicle.release();
			}

			this.vehicle = null;
		}

		static PolygonalMap2.VehicleRemoveTask alloc() {
			synchronized (pool) {
				return pool.isEmpty() ? new PolygonalMap2.VehicleRemoveTask() : (PolygonalMap2.VehicleRemoveTask)pool.pop();
			}
		}

		public void release() {
			synchronized (pool) {
				assert !pool.contains(this);
				pool.push(this);
			}
		}
	}

	private static final class Cell {
		PolygonalMap2 map;
		public short cx;
		public short cy;
		public PolygonalMap2.Chunk[][] chunks;
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.Cell init(PolygonalMap2 polygonalMap2, int int1, int int2) {
			this.map = polygonalMap2;
			this.cx = (short)int1;
			this.cy = (short)int2;
			return this;
		}

		PolygonalMap2.Chunk getChunkFromChunkPos(int int1, int int2) {
			if (this.chunks == null) {
				return null;
			} else {
				int1 -= this.cx * 30;
				int2 -= this.cy * 30;
				return int1 >= 0 && int1 < 30 && int2 >= 0 && int2 < 30 ? this.chunks[int1][int2] : null;
			}
		}

		PolygonalMap2.Chunk allocChunkIfNeeded(int int1, int int2) {
			int1 -= this.cx * 30;
			int2 -= this.cy * 30;
			if (int1 >= 0 && int1 < 30 && int2 >= 0 && int2 < 30) {
				if (this.chunks == null) {
					this.chunks = new PolygonalMap2.Chunk[30][30];
				}

				if (this.chunks[int1][int2] == null) {
					this.chunks[int1][int2] = PolygonalMap2.Chunk.alloc();
				}

				this.chunks[int1][int2].init(this.cx * 30 + int1, this.cy * 30 + int2);
				return this.chunks[int1][int2];
			} else {
				return null;
			}
		}

		void removeChunk(int int1, int int2) {
			if (this.chunks != null) {
				int1 -= this.cx * 30;
				int2 -= this.cy * 30;
				if (int1 >= 0 && int1 < 30 && int2 >= 0 && int2 < 30) {
					PolygonalMap2.Chunk chunk = this.chunks[int1][int2];
					if (chunk != null) {
						chunk.release();
						this.chunks[int1][int2] = null;
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

	private interface IChunkTask {

		void execute();

		void release();
	}

	private interface IVehicleTask {

		void init(PolygonalMap2 polygonalMap2, BaseVehicle baseVehicle);

		void execute();

		void release();
	}

	private static final class PathRequestTask {
		PolygonalMap2 map;
		PolygonalMap2.PathFindRequest request;
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.PathRequestTask init(PolygonalMap2 polygonalMap2, PolygonalMap2.PathFindRequest pathFindRequest) {
			this.map = polygonalMap2;
			this.request = pathFindRequest;
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
			synchronized (pool) {
				return pool.isEmpty() ? new PolygonalMap2.PathRequestTask() : (PolygonalMap2.PathRequestTask)pool.pop();
			}
		}

		public void release() {
			synchronized (pool) {
				assert !pool.contains(this);
				pool.push(this);
			}
		}
	}

	private static final class ChunkDataZ {
		public PolygonalMap2.Chunk chunk;
		public final ArrayList obstacles = new ArrayList();
		public final ArrayList nodes = new ArrayList();
		public int z;
		static short EPOCH = 0;
		short epoch;
		public static final ObjectPool pool = new ObjectPool(PolygonalMap2.ChunkDataZ::new);

		public void init(PolygonalMap2.Chunk chunk, int int1) {
			this.chunk = chunk;
			this.z = int1;
			this.epoch = EPOCH;
			if (PolygonalMap2.instance.clipperThread == null) {
				PolygonalMap2.instance.clipperThread = new Clipper();
			}

			Clipper clipper = PolygonalMap2.instance.clipperThread;
			clipper.clear();
			int int2 = chunk.wx * 10;
			int int3 = chunk.wy * 10;
			int int4;
			for (int int5 = int3 - 2; int5 < int3 + 10 + 2; ++int5) {
				for (int4 = int2 - 2; int4 < int2 + 10 + 2; ++int4) {
					PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int4, int5, int1);
					if (square != null && square.has(512)) {
						if (square.isReallySolid() || square.has(128) || square.has(64) || square.has(16) || square.has(8)) {
							clipper.addAABBBevel((float)int4 - 0.3F, (float)int5 - 0.3F, (float)int4 + 1.0F + 0.3F, (float)int5 + 1.0F + 0.3F, 0.19800001F);
						}

						if (square.has(2) || square.has(256)) {
							clipper.addAABBBevel((float)int4 - 0.3F, (float)int5 - 0.3F, (float)int4 + 0.3F, (float)int5 + 1.0F + 0.3F, 0.19800001F);
						}

						if (square.has(4) || square.has(32)) {
							clipper.addAABBBevel((float)int4 - 0.3F, (float)int5 - 0.3F, (float)int4 + 1.0F + 0.3F, (float)int5 + 0.3F, 0.19800001F);
						}

						PolygonalMap2.Square square2;
						if (square.has(256)) {
							square2 = PolygonalMap2.instance.getSquare(int4 + 1, int5, int1);
							if (square2 != null) {
								clipper.addAABBBevel((float)(int4 + 1) - 0.3F, (float)int5 - 0.3F, (float)(int4 + 1) + 0.3F, (float)int5 + 1.0F + 0.3F, 0.19800001F);
							}
						}

						if (square.has(32)) {
							square2 = PolygonalMap2.instance.getSquare(int4, int5 + 1, int1);
							if (square2 != null) {
								clipper.addAABBBevel((float)int4 - 0.3F, (float)(int5 + 1) - 0.3F, (float)int4 + 1.0F + 0.3F, (float)(int5 + 1) + 0.3F, 0.19800001F);
							}
						}
					} else {
						clipper.addAABB((float)int4, (float)int5, (float)int4 + 1.0F, (float)int5 + 1.0F);
					}
				}
			}

			ByteBuffer byteBuffer = PolygonalMap2.instance.xyBufferThread;
			int4 = clipper.generatePolygons();
			int int6;
			int int7;
			for (int7 = 0; int7 < int4; ++int7) {
				byteBuffer.clear();
				clipper.getPolygon(int7, byteBuffer);
				PolygonalMap2.Obstacle obstacle = PolygonalMap2.Obstacle.alloc().init((IsoGridSquare)null);
				this.getEdgesFromBuffer(byteBuffer, obstacle, true);
				short short1 = byteBuffer.getShort();
				for (int6 = 0; int6 < short1; ++int6) {
					this.getEdgesFromBuffer(byteBuffer, obstacle, false);
				}

				obstacle.calcBounds();
				this.obstacles.add(obstacle);
			}

			int7 = chunk.wx * 10;
			int int8 = chunk.wy * 10;
			int int9 = int7 + 10;
			int6 = int8 + 10;
			int7 -= 2;
			int8 -= 2;
			int9 += 2;
			int6 += 2;
			PolygonalMap2.ImmutableRectF immutableRectF = PolygonalMap2.ImmutableRectF.alloc();
			immutableRectF.init((float)int7, (float)int8, (float)(int9 - int7), (float)(int6 - int8));
			PolygonalMap2.ImmutableRectF immutableRectF2 = PolygonalMap2.ImmutableRectF.alloc();
			for (int int10 = 0; int10 < PolygonalMap2.instance.vehicles.size(); ++int10) {
				PolygonalMap2.Vehicle vehicle = (PolygonalMap2.Vehicle)PolygonalMap2.instance.vehicles.get(int10);
				PolygonalMap2.VehiclePoly vehiclePoly = vehicle.polyPlusRadius;
				float float1 = Math.min(vehiclePoly.x1, Math.min(vehiclePoly.x2, Math.min(vehiclePoly.x3, vehiclePoly.x4)));
				float float2 = Math.min(vehiclePoly.y1, Math.min(vehiclePoly.y2, Math.min(vehiclePoly.y3, vehiclePoly.y4)));
				float float3 = Math.max(vehiclePoly.x1, Math.max(vehiclePoly.x2, Math.max(vehiclePoly.x3, vehiclePoly.x4)));
				float float4 = Math.max(vehiclePoly.y1, Math.max(vehiclePoly.y2, Math.max(vehiclePoly.y3, vehiclePoly.y4)));
				immutableRectF2.init(float1, float2, float3 - float1, float4 - float2);
				if (immutableRectF.intersects(immutableRectF2)) {
					this.addEdgesForVehicle(vehicle);
				}
			}

			immutableRectF.release();
			immutableRectF2.release();
		}

		private void getEdgesFromBuffer(ByteBuffer byteBuffer, PolygonalMap2.Obstacle obstacle, boolean boolean1) {
			short short1 = byteBuffer.getShort();
			if (short1 < 3) {
				byteBuffer.position(byteBuffer.position() + short1 * 4 * 2);
			} else {
				PolygonalMap2.EdgeRing edgeRing = obstacle.outer;
				if (!boolean1) {
					edgeRing = PolygonalMap2.EdgeRing.alloc();
					edgeRing.clear();
					obstacle.inner.add(edgeRing);
				}

				int int1 = this.nodes.size();
				int int2;
				for (int2 = 0; int2 < short1; ++int2) {
					float float1 = byteBuffer.getFloat();
					float float2 = byteBuffer.getFloat();
					PolygonalMap2.Node node = PolygonalMap2.Node.alloc().init(float1, float2, this.z);
					node.flags |= 4;
					this.nodes.add(int1, node);
				}

				PolygonalMap2.Node node2;
				for (int2 = int1; int2 < this.nodes.size() - 1; ++int2) {
					node2 = (PolygonalMap2.Node)this.nodes.get(int2);
					PolygonalMap2.Node node3 = (PolygonalMap2.Node)this.nodes.get(int2 + 1);
					PolygonalMap2.Edge edge = PolygonalMap2.Edge.alloc().init(node2, node3, obstacle, edgeRing);
					edgeRing.add(edge);
				}

				PolygonalMap2.Node node4 = (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1);
				node2 = (PolygonalMap2.Node)this.nodes.get(int1);
				PolygonalMap2.Edge edge2 = PolygonalMap2.Edge.alloc().init(node4, node2, obstacle, edgeRing);
				edgeRing.add(edge2);
			}
		}

		private void addEdgesForVehicle(PolygonalMap2.Vehicle vehicle) {
			PolygonalMap2.VehiclePoly vehiclePoly = vehicle.polyPlusRadius;
			int int1 = (int)vehiclePoly.z;
			PolygonalMap2.Node node = PolygonalMap2.Node.alloc().init(vehiclePoly.x1, vehiclePoly.y1, int1);
			PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init(vehiclePoly.x2, vehiclePoly.y2, int1);
			PolygonalMap2.Node node3 = PolygonalMap2.Node.alloc().init(vehiclePoly.x3, vehiclePoly.y3, int1);
			PolygonalMap2.Node node4 = PolygonalMap2.Node.alloc().init(vehiclePoly.x4, vehiclePoly.y4, int1);
			node.flags |= 4;
			node2.flags |= 4;
			node3.flags |= 4;
			node4.flags |= 4;
			PolygonalMap2.Obstacle obstacle = PolygonalMap2.Obstacle.alloc().init(vehicle);
			this.obstacles.add(obstacle);
			PolygonalMap2.Edge edge = PolygonalMap2.Edge.alloc().init(node, node2, obstacle, obstacle.outer);
			PolygonalMap2.Edge edge2 = PolygonalMap2.Edge.alloc().init(node2, node3, obstacle, obstacle.outer);
			PolygonalMap2.Edge edge3 = PolygonalMap2.Edge.alloc().init(node3, node4, obstacle, obstacle.outer);
			PolygonalMap2.Edge edge4 = PolygonalMap2.Edge.alloc().init(node4, node, obstacle, obstacle.outer);
			obstacle.outer.add(edge);
			obstacle.outer.add(edge2);
			obstacle.outer.add(edge3);
			obstacle.outer.add(edge4);
			obstacle.calcBounds();
			this.nodes.add(node);
			this.nodes.add(node2);
			this.nodes.add(node3);
			this.nodes.add(node4);
		}

		public void clear() {
			PolygonalMap2.Node.releaseAll(this.nodes);
			this.nodes.clear();
			PolygonalMap2.Obstacle.releaseAll(this.obstacles);
			this.obstacles.clear();
		}
	}

	private static final class PointPool {
		final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.Point alloc() {
			return this.pool.isEmpty() ? new PolygonalMap2.Point() : (PolygonalMap2.Point)this.pool.pop();
		}

		void release(PolygonalMap2.Point point) {
			this.pool.push(point);
		}
	}

	public static final class Point {
		public int x;
		public int y;

		PolygonalMap2.Point init(int int1, int int2) {
			this.x = int1;
			this.y = int2;
			return this;
		}

		public boolean equals(Object object) {
			return object instanceof PolygonalMap2.Point && ((PolygonalMap2.Point)object).x == this.x && ((PolygonalMap2.Point)object).y == this.y;
		}
	}

	private static final class ConnectedRegions {
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

		void findAdjacentChunks(int int1, int int2) {
			this.doneChunks.clear();
			this.minX = this.minY = Integer.MAX_VALUE;
			this.maxX = this.maxY = Integer.MIN_VALUE;
			PolygonalMap2.Chunk chunk = this.map.getChunkFromSquarePos(int1, int2);
			this.findAdjacentChunks(chunk);
		}

		void findAdjacentChunks(PolygonalMap2.Chunk chunk) {
			if (chunk != null && !this.doneChunks.contains(chunk)) {
				this.minX = Math.min(this.minX, chunk.wx);
				this.minY = Math.min(this.minY, chunk.wy);
				this.maxX = Math.max(this.maxX, chunk.wx);
				this.maxY = Math.max(this.maxY, chunk.wy);
				this.doneChunks.add(chunk);
				PolygonalMap2.Chunk chunk2 = this.map.getChunkFromChunkPos(chunk.wx - 1, chunk.wy);
				PolygonalMap2.Chunk chunk3 = this.map.getChunkFromChunkPos(chunk.wx, chunk.wy - 1);
				PolygonalMap2.Chunk chunk4 = this.map.getChunkFromChunkPos(chunk.wx + 1, chunk.wy);
				PolygonalMap2.Chunk chunk5 = this.map.getChunkFromChunkPos(chunk.wx, chunk.wy + 1);
				this.findAdjacentChunks(chunk2);
				this.findAdjacentChunks(chunk3);
				this.findAdjacentChunks(chunk4);
				this.findAdjacentChunks(chunk5);
			}
		}

		void floodFill(int int1, int int2) {
			this.findAdjacentChunks(int1, int2);
			this.MINX = this.minX * 10;
			this.MINY = this.minY * 10;
			this.WIDTH = (this.maxX - this.minX + 1) * 10;
			this.HEIGHT = (this.maxY - this.minY + 1) * 10;
			this.visited = new BooleanGrid(this.WIDTH, this.WIDTH);
			this.stack = new int[this.WIDTH * this.WIDTH];
			this.choices = new int[this.WIDTH * this.HEIGHT];
			this.stackLen = 0;
			this.choicesLen = 0;
			if (this.push(int1, int2)) {
				int int3;
				label81: while ((int3 = this.pop()) != -1) {
					int int4 = this.MINX + (int3 & '￿');
					int int5;
					for (int5 = this.MINY + (int3 >> 16) & '￿'; this.shouldVisit(int4, int5, int4, int5 - 1); --int5) {
					}

					boolean boolean1 = false;
					boolean boolean2 = false;
					while (this.visit(int4, int5)) {
						if (!boolean1 && this.shouldVisit(int4, int5, int4 - 1, int5)) {
							if (!this.push(int4 - 1, int5)) {
								return;
							}

							boolean1 = true;
						} else if (boolean1 && !this.shouldVisit(int4, int5, int4 - 1, int5)) {
							boolean1 = false;
						} else if (boolean1 && !this.shouldVisit(int4 - 1, int5, int4 - 1, int5 - 1) && !this.push(int4 - 1, int5)) {
							return;
						}

						if (!boolean2 && this.shouldVisit(int4, int5, int4 + 1, int5)) {
							if (!this.push(int4 + 1, int5)) {
								return;
							}

							boolean2 = true;
						} else if (boolean2 && !this.shouldVisit(int4, int5, int4 + 1, int5)) {
							boolean2 = false;
						} else if (boolean2 && !this.shouldVisit(int4 + 1, int5, int4 + 1, int5 - 1) && !this.push(int4 + 1, int5)) {
							return;
						}

						++int5;
						if (!this.shouldVisit(int4, int5 - 1, int4, int5)) {
							continue label81;
						}
					}

					return;
				}

				System.out.println("#choices=" + this.choicesLen);
			}
		}

		boolean shouldVisit(int int1, int int2, int int3, int int4) {
			if (int3 < this.MINX + this.WIDTH && int3 >= this.MINX) {
				if (int4 < this.MINY + this.WIDTH && int4 >= this.MINY) {
					if (this.visited.getValue(this.gridX(int3), this.gridY(int4))) {
						return false;
					} else {
						PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int1, int2, 0);
						PolygonalMap2.Square square2 = PolygonalMap2.instance.getSquare(int3, int4, 0);
						if (square != null && square2 != null) {
							return !this.isBlocked(square, square2, false);
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

		boolean visit(int int1, int int2) {
			if (this.choicesLen >= this.WIDTH * this.WIDTH) {
				return false;
			} else {
				this.choices[this.choicesLen++] = this.gridY(int2) << 16 | (short)this.gridX(int1);
				this.visited.setValue(this.gridX(int1), this.gridY(int2), true);
				return true;
			}
		}

		boolean push(int int1, int int2) {
			if (this.stackLen >= this.WIDTH * this.WIDTH) {
				return false;
			} else {
				this.stack[this.stackLen++] = this.gridY(int2) << 16 | (short)this.gridX(int1);
				return true;
			}
		}

		int pop() {
			return this.stackLen == 0 ? -1 : this.stack[--this.stackLen];
		}

		int gridX(int int1) {
			return int1 - this.MINX;
		}

		int gridY(int int1) {
			return int1 - this.MINY;
		}

		boolean isBlocked(PolygonalMap2.Square square, PolygonalMap2.Square square2, boolean boolean1) {
			assert Math.abs(square.x - square2.x) <= 1;
			assert Math.abs(square.y - square2.y) <= 1;
			assert square.z == square2.z;
			assert square != square2;
			boolean boolean2 = square2.x < square.x;
			boolean boolean3 = square2.x > square.x;
			boolean boolean4 = square2.y < square.y;
			boolean boolean5 = square2.y > square.y;
			if (square2.isReallySolid()) {
				return true;
			} else if (square2.y < square.y && square.has(64)) {
				return true;
			} else if (square2.x < square.x && square.has(8)) {
				return true;
			} else if (square2.y > square.y && square2.x == square.x && square2.has(64)) {
				return true;
			} else if (square2.x > square.x && square2.y == square.y && square2.has(8)) {
				return true;
			} else if (square2.x != square.x && square2.has(448)) {
				return true;
			} else if (square2.y != square.y && square2.has(56)) {
				return true;
			} else if (square2.x != square.x && square.has(448)) {
				return true;
			} else if (square2.y != square.y && square.has(56)) {
				return true;
			} else if (!square2.has(512) && !square2.has(504)) {
				return true;
			} else {
				boolean boolean6 = boolean4 && square.has(4) && (square.x != square2.x || boolean1 || !square.has(16384));
				boolean boolean7 = boolean2 && square.has(2) && (square.y != square2.y || boolean1 || !square.has(8192));
				boolean boolean8 = boolean5 && square2.has(4) && (square.x != square2.x || boolean1 || !square2.has(16384));
				boolean boolean9 = boolean3 && square2.has(2) && (square.y != square2.y || boolean1 || !square2.has(8192));
				if (!boolean6 && !boolean7 && !boolean8 && !boolean9) {
					boolean boolean10 = square2.x != square.x && square2.y != square.y;
					if (boolean10) {
						PolygonalMap2.Square square3 = PolygonalMap2.instance.getSquare(square.x, square2.y, square.z);
						PolygonalMap2.Square square4 = PolygonalMap2.instance.getSquare(square2.x, square.y, square.z);
						assert square3 != square && square3 != square2;
						assert square4 != square && square4 != square2;
						if (square2.x == square.x + 1 && square2.y == square.y + 1 && square3 != null && square4 != null && square3.has(4096) && square4.has(2048)) {
							return true;
						} else if (square2.x == square.x - 1 && square2.y == square.y - 1 && square3 != null && square4 != null && square3.has(2048) && square4.has(4096)) {
							return true;
						} else if (square3 != null && this.isBlocked(square, square3, true)) {
							return true;
						} else if (square4 != null && this.isBlocked(square, square4, true)) {
							return true;
						} else if (square3 != null && this.isBlocked(square2, square3, true)) {
							return true;
						} else if (square4 != null && this.isBlocked(square2, square4, true)) {
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

	private static final class LiangBarsky {
		private final double[] p = new double[4];
		private final double[] q = new double[4];

		private boolean lineRectIntersect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
			this.p[0] = (double)(-float3);
			this.p[1] = (double)float3;
			this.p[2] = (double)(-float4);
			this.p[3] = (double)float4;
			this.q[0] = (double)(float1 - float5);
			this.q[1] = (double)(float7 - float1);
			this.q[2] = (double)(float2 - float6);
			this.q[3] = (double)(float8 - float2);
			double double1 = 0.0;
			double double2 = 1.0;
			for (int int1 = 0; int1 < 4; ++int1) {
				if (this.p[int1] == 0.0) {
					if (this.q[int1] < 0.0) {
						return false;
					}
				} else {
					double double3 = this.q[int1] / this.p[int1];
					if (this.p[int1] < 0.0 && double1 < double3) {
						double1 = double3;
					} else if (this.p[int1] > 0.0 && double2 > double3) {
						double2 = double3;
					}
				}
			}

			if (double1 > double2) {
				return false;
			} else {
				return true;
			}
		}
	}

	private static final class ChunkData {
		final PolygonalMap2.ChunkDataZ[] data = new PolygonalMap2.ChunkDataZ[8];

		public PolygonalMap2.ChunkDataZ init(PolygonalMap2.Chunk chunk, int int1) {
			if (this.data[int1] == null) {
				this.data[int1] = (PolygonalMap2.ChunkDataZ)PolygonalMap2.ChunkDataZ.pool.alloc();
				this.data[int1].init(chunk, int1);
			} else if (this.data[int1].epoch != PolygonalMap2.ChunkDataZ.EPOCH) {
				this.data[int1].clear();
				this.data[int1].init(chunk, int1);
			}

			return this.data[int1];
		}

		public void clear() {
			PZArrayUtil.forEach((Object[])this.data, (var0)->{
				if (var0 != null) {
					var0.clear();
					PolygonalMap2.ChunkDataZ.pool.release((Object)var0);
				}
			});
			Arrays.fill(this.data, (Object)null);
		}
	}

	private static final class GoalNode implements IGoalNode {
		PolygonalMap2.SearchNode searchNode;

		PolygonalMap2.GoalNode init(PolygonalMap2.SearchNode searchNode) {
			this.searchNode = searchNode;
			return this;
		}

		public boolean inGoal(ISearchNode iSearchNode) {
			if (this.searchNode.tx != -1) {
				PolygonalMap2.SearchNode searchNode = (PolygonalMap2.SearchNode)iSearchNode;
				int int1 = (int)searchNode.getX();
				int int2 = (int)searchNode.getY();
				if (int1 % 10 == 0 && PolygonalMap2.instance.getChunkFromSquarePos(int1 - 1, int2) == null) {
					return true;
				} else if (int1 % 10 == 9 && PolygonalMap2.instance.getChunkFromSquarePos(int1 + 1, int2) == null) {
					return true;
				} else if (int2 % 10 == 0 && PolygonalMap2.instance.getChunkFromSquarePos(int1, int2 - 1) == null) {
					return true;
				} else {
					return int2 % 10 == 9 && PolygonalMap2.instance.getChunkFromSquarePos(int1, int2 + 1) == null;
				}
			} else {
				return iSearchNode == this.searchNode;
			}
		}
	}

	private static final class ImmutableRectF {
		private float x;
		private float y;
		private float w;
		private float h;
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.ImmutableRectF init(float float1, float float2, float float3, float float4) {
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

		boolean intersects(PolygonalMap2.ImmutableRectF immutableRectF) {
			return this.left() < immutableRectF.right() && this.right() > immutableRectF.left() && this.top() < immutableRectF.bottom() && this.bottom() > immutableRectF.top();
		}

		static PolygonalMap2.ImmutableRectF alloc() {
			return pool.isEmpty() ? new PolygonalMap2.ImmutableRectF() : (PolygonalMap2.ImmutableRectF)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			pool.push(this);
		}
	}

	private static final class Intersection {
		PolygonalMap2.Edge edge1;
		PolygonalMap2.Edge edge2;
		float dist1;
		float dist2;
		PolygonalMap2.Node nodeSplit;

		Intersection(PolygonalMap2.Edge edge, PolygonalMap2.Edge edge2, float float1, float float2, float float3, float float4) {
			this.edge1 = edge;
			this.edge2 = edge2;
			this.dist1 = float1;
			this.dist2 = float2;
			this.nodeSplit = PolygonalMap2.Node.alloc().init(float3, float4, edge.node1.z);
		}

		Intersection(PolygonalMap2.Edge edge, PolygonalMap2.Edge edge2, float float1, float float2, PolygonalMap2.Node node) {
			this.edge1 = edge;
			this.edge2 = edge2;
			this.dist1 = float1;
			this.dist2 = float2;
			this.nodeSplit = node;
		}

		PolygonalMap2.Edge split(PolygonalMap2.Edge edge) {
			return edge.split(this.nodeSplit);
		}
	}

	private static final class ClusterOutlineGrid {
		PolygonalMap2.ClusterOutline[] elements;
		int W;
		int H;

		PolygonalMap2.ClusterOutlineGrid setSize(int int1, int int2) {
			if (this.elements == null || this.elements.length < int1 * int2) {
				this.elements = new PolygonalMap2.ClusterOutline[int1 * int2];
			}

			this.W = int1;
			this.H = int2;
			return this;
		}

		void releaseElements() {
			for (int int1 = 0; int1 < this.H; ++int1) {
				for (int int2 = 0; int2 < this.W; ++int2) {
					if (this.elements[int2 + int1 * this.W] != null) {
						this.elements[int2 + int1 * this.W].release();
						this.elements[int2 + int1 * this.W] = null;
					}
				}
			}
		}

		void setInner(int int1, int int2, int int3) {
			PolygonalMap2.ClusterOutline clusterOutline = this.get(int1, int2, int3);
			if (clusterOutline != null) {
				clusterOutline.inner = true;
			}
		}

		void setWest(int int1, int int2, int int3) {
			PolygonalMap2.ClusterOutline clusterOutline = this.get(int1, int2, int3);
			if (clusterOutline != null) {
				clusterOutline.w = true;
			}
		}

		void setNorth(int int1, int int2, int int3) {
			PolygonalMap2.ClusterOutline clusterOutline = this.get(int1, int2, int3);
			if (clusterOutline != null) {
				clusterOutline.n = true;
			}
		}

		void setEast(int int1, int int2, int int3) {
			PolygonalMap2.ClusterOutline clusterOutline = this.get(int1, int2, int3);
			if (clusterOutline != null) {
				clusterOutline.e = true;
			}
		}

		void setSouth(int int1, int int2, int int3) {
			PolygonalMap2.ClusterOutline clusterOutline = this.get(int1, int2, int3);
			if (clusterOutline != null) {
				clusterOutline.s = true;
			}
		}

		boolean canTrace_W(int int1, int int2, int int3) {
			PolygonalMap2.ClusterOutline clusterOutline = this.get(int1, int2, int3);
			return clusterOutline != null && clusterOutline.inner && clusterOutline.w && !clusterOutline.tw;
		}

		boolean canTrace_N(int int1, int int2, int int3) {
			PolygonalMap2.ClusterOutline clusterOutline = this.get(int1, int2, int3);
			return clusterOutline != null && clusterOutline.inner && clusterOutline.n && !clusterOutline.tn;
		}

		boolean canTrace_E(int int1, int int2, int int3) {
			PolygonalMap2.ClusterOutline clusterOutline = this.get(int1, int2, int3);
			return clusterOutline != null && clusterOutline.inner && clusterOutline.e && !clusterOutline.te;
		}

		boolean canTrace_S(int int1, int int2, int int3) {
			PolygonalMap2.ClusterOutline clusterOutline = this.get(int1, int2, int3);
			return clusterOutline != null && clusterOutline.inner && clusterOutline.s && !clusterOutline.ts;
		}

		boolean isInner(int int1, int int2, int int3) {
			PolygonalMap2.ClusterOutline clusterOutline = this.get(int1, int2, int3);
			return clusterOutline != null && (clusterOutline.start || clusterOutline.inner);
		}

		PolygonalMap2.ClusterOutline get(int int1, int int2, int int3) {
			if (int1 >= 0 && int1 < this.W) {
				if (int2 >= 0 && int2 < this.H) {
					if (this.elements[int1 + int2 * this.W] == null) {
						this.elements[int1 + int2 * this.W] = PolygonalMap2.ClusterOutline.alloc().init(int1, int2, int3);
					}

					return this.elements[int1 + int2 * this.W];
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		void trace_W(PolygonalMap2.ClusterOutline clusterOutline, ArrayList arrayList, PolygonalMap2.Node node) {
			int int1 = clusterOutline.x;
			int int2 = clusterOutline.y;
			int int3 = clusterOutline.z;
			if (node != null) {
				node.setXY((float)int1, (float)int2);
			} else {
				PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init((float)int1, (float)int2, int3);
				arrayList.add(node2);
			}

			clusterOutline.tw = true;
			if (this.canTrace_S(int1 - 1, int2 - 1, int3)) {
				this.get(int1, int2 - 1, int3).innerCorner = true;
				this.trace_S(this.get(int1 - 1, int2 - 1, int3), arrayList, (PolygonalMap2.Node)null);
			} else if (this.canTrace_W(int1, int2 - 1, int3)) {
				this.trace_W(this.get(int1, int2 - 1, int3), arrayList, (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1));
			} else if (this.canTrace_N(int1, int2, int3)) {
				this.trace_N(clusterOutline, arrayList, (PolygonalMap2.Node)null);
			}
		}

		void trace_N(PolygonalMap2.ClusterOutline clusterOutline, ArrayList arrayList, PolygonalMap2.Node node) {
			int int1 = clusterOutline.x;
			int int2 = clusterOutline.y;
			int int3 = clusterOutline.z;
			if (node != null) {
				node.setXY((float)(int1 + 1), (float)int2);
			} else {
				PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init((float)(int1 + 1), (float)int2, int3);
				arrayList.add(node2);
			}

			clusterOutline.tn = true;
			if (this.canTrace_W(int1 + 1, int2 - 1, int3)) {
				this.get(int1 + 1, int2, int3).innerCorner = true;
				this.trace_W(this.get(int1 + 1, int2 - 1, int3), arrayList, (PolygonalMap2.Node)null);
			} else if (this.canTrace_N(int1 + 1, int2, int3)) {
				this.trace_N(this.get(int1 + 1, int2, int3), arrayList, (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1));
			} else if (this.canTrace_E(int1, int2, int3)) {
				this.trace_E(clusterOutline, arrayList, (PolygonalMap2.Node)null);
			}
		}

		void trace_E(PolygonalMap2.ClusterOutline clusterOutline, ArrayList arrayList, PolygonalMap2.Node node) {
			int int1 = clusterOutline.x;
			int int2 = clusterOutline.y;
			int int3 = clusterOutline.z;
			if (node != null) {
				node.setXY((float)(int1 + 1), (float)(int2 + 1));
			} else {
				PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init((float)(int1 + 1), (float)(int2 + 1), int3);
				arrayList.add(node2);
			}

			clusterOutline.te = true;
			if (this.canTrace_N(int1 + 1, int2 + 1, int3)) {
				this.get(int1, int2 + 1, int3).innerCorner = true;
				this.trace_N(this.get(int1 + 1, int2 + 1, int3), arrayList, (PolygonalMap2.Node)null);
			} else if (this.canTrace_E(int1, int2 + 1, int3)) {
				this.trace_E(this.get(int1, int2 + 1, int3), arrayList, (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1));
			} else if (this.canTrace_S(int1, int2, int3)) {
				this.trace_S(clusterOutline, arrayList, (PolygonalMap2.Node)null);
			}
		}

		void trace_S(PolygonalMap2.ClusterOutline clusterOutline, ArrayList arrayList, PolygonalMap2.Node node) {
			int int1 = clusterOutline.x;
			int int2 = clusterOutline.y;
			int int3 = clusterOutline.z;
			if (node != null) {
				node.setXY((float)int1, (float)(int2 + 1));
			} else {
				PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init((float)int1, (float)(int2 + 1), int3);
				arrayList.add(node2);
			}

			clusterOutline.ts = true;
			if (this.canTrace_E(int1 - 1, int2 + 1, int3)) {
				this.get(int1 - 1, int2, int3).innerCorner = true;
				this.trace_E(this.get(int1 - 1, int2 + 1, int3), arrayList, (PolygonalMap2.Node)null);
			} else if (this.canTrace_S(int1 - 1, int2, int3)) {
				this.trace_S(this.get(int1 - 1, int2, int3), arrayList, (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1));
			} else if (this.canTrace_W(int1, int2, int3)) {
				this.trace_W(clusterOutline, arrayList, (PolygonalMap2.Node)null);
			}
		}

		ArrayList trace(PolygonalMap2.ClusterOutline clusterOutline) {
			int int1 = clusterOutline.x;
			int int2 = clusterOutline.y;
			int int3 = clusterOutline.z;
			ArrayList arrayList = new ArrayList();
			PolygonalMap2.Node node = PolygonalMap2.Node.alloc().init((float)int1, (float)int2, int3);
			arrayList.add(node);
			clusterOutline.start = true;
			this.trace_N(clusterOutline, arrayList, (PolygonalMap2.Node)null);
			PolygonalMap2.Node node2 = (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1);
			float float1 = 0.1F;
			if ((int)(node.x + float1) == (int)(node2.x + float1) && (int)(node.y + float1) == (int)(node2.y + float1)) {
				node2.release();
				arrayList.set(arrayList.size() - 1, node);
			}

			return arrayList;
		}
	}

	private static final class ClusterOutline {
		int x;
		int y;
		int z;
		boolean w;
		boolean n;
		boolean e;
		boolean s;
		boolean tw;
		boolean tn;
		boolean te;
		boolean ts;
		boolean inner;
		boolean innerCorner;
		boolean start;
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.ClusterOutline init(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.z = int3;
			this.w = this.n = this.e = this.s = false;
			this.tw = this.tn = this.te = this.ts = false;
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

	@Deprecated
	private static final class ObjectOutline {
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
		static final ArrayDeque pool = new ArrayDeque();

		PolygonalMap2.ObjectOutline init(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.z = int3;
			this.nw = this.nw_w = this.nw_n = this.nw_e = false;
			this.w_w = this.w_e = this.w_cutoff = false;
			this.n_n = this.n_s = this.n_cutoff = false;
			return this;
		}

		static void setSolid(int int1, int int2, int int3, PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray) {
			setWest(int1, int2, int3, objectOutlineArrayArray);
			setNorth(int1, int2, int3, objectOutlineArrayArray);
			setWest(int1 + 1, int2, int3, objectOutlineArrayArray);
			setNorth(int1, int2 + 1, int3, objectOutlineArrayArray);
		}

		static void setWest(int int1, int int2, int int3, PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray) {
			PolygonalMap2.ObjectOutline objectOutline = get(int1, int2, int3, objectOutlineArrayArray);
			if (objectOutline != null) {
				if (objectOutline.nw) {
					objectOutline.nw_s = false;
				} else {
					objectOutline.nw = true;
					objectOutline.nw_w = true;
					objectOutline.nw_n = true;
					objectOutline.nw_e = true;
					objectOutline.nw_s = false;
				}

				objectOutline.w_w = true;
				objectOutline.w_e = true;
			}

			PolygonalMap2.ObjectOutline objectOutline2 = objectOutline;
			objectOutline = get(int1, int2 + 1, int3, objectOutlineArrayArray);
			if (objectOutline == null) {
				if (objectOutline2 != null) {
					objectOutline2.w_cutoff = true;
				}
			} else if (objectOutline.nw) {
				objectOutline.nw_n = false;
			} else {
				objectOutline.nw = true;
				objectOutline.nw_n = false;
				objectOutline.nw_w = true;
				objectOutline.nw_e = true;
				objectOutline.nw_s = true;
			}
		}

		static void setNorth(int int1, int int2, int int3, PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray) {
			PolygonalMap2.ObjectOutline objectOutline = get(int1, int2, int3, objectOutlineArrayArray);
			if (objectOutline != null) {
				if (objectOutline.nw) {
					objectOutline.nw_e = false;
				} else {
					objectOutline.nw = true;
					objectOutline.nw_w = true;
					objectOutline.nw_n = true;
					objectOutline.nw_e = false;
					objectOutline.nw_s = true;
				}

				objectOutline.n_n = true;
				objectOutline.n_s = true;
			}

			PolygonalMap2.ObjectOutline objectOutline2 = objectOutline;
			objectOutline = get(int1 + 1, int2, int3, objectOutlineArrayArray);
			if (objectOutline == null) {
				if (objectOutline2 != null) {
					objectOutline2.n_cutoff = true;
				}
			} else if (objectOutline.nw) {
				objectOutline.nw_w = false;
			} else {
				objectOutline.nw = true;
				objectOutline.nw_n = true;
				objectOutline.nw_w = false;
				objectOutline.nw_e = true;
				objectOutline.nw_s = true;
			}
		}

		static PolygonalMap2.ObjectOutline get(int int1, int int2, int int3, PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray) {
			if (int1 >= 0 && int1 < objectOutlineArrayArray.length) {
				if (int2 >= 0 && int2 < objectOutlineArrayArray[0].length) {
					if (objectOutlineArrayArray[int1][int2] == null) {
						objectOutlineArrayArray[int1][int2] = alloc().init(int1, int2, int3);
					}

					return objectOutlineArrayArray[int1][int2];
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		void trace_NW_N(PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray, PolygonalMap2.Node node) {
			if (node != null) {
				node.setXY((float)this.x + 0.3F, (float)this.y - 0.3F);
			} else {
				PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init((float)this.x + 0.3F, (float)this.y - 0.3F, this.z);
				this.nodes.add(node2);
			}

			this.nw_n = false;
			if (this.nw_e) {
				this.trace_NW_E(objectOutlineArrayArray, (PolygonalMap2.Node)null);
			} else if (this.n_n) {
				this.trace_N_N(objectOutlineArrayArray, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
			}
		}

		void trace_NW_S(PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray, PolygonalMap2.Node node) {
			if (node != null) {
				node.setXY((float)this.x - 0.3F, (float)this.y + 0.3F);
			} else {
				PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(node2);
			}

			this.nw_s = false;
			if (this.nw_w) {
				this.trace_NW_W(objectOutlineArrayArray, (PolygonalMap2.Node)null);
			} else {
				PolygonalMap2.ObjectOutline objectOutline = get(this.x - 1, this.y, this.z, objectOutlineArrayArray);
				if (objectOutline == null) {
					return;
				}

				if (objectOutline.n_s) {
					objectOutline.nodes = this.nodes;
					objectOutline.trace_N_S(objectOutlineArrayArray, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
				}
			}
		}

		void trace_NW_W(PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray, PolygonalMap2.Node node) {
			if (node != null) {
				node.setXY((float)this.x - 0.3F, (float)this.y - 0.3F);
			} else {
				PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)this.y - 0.3F, this.z);
				this.nodes.add(node2);
			}

			this.nw_w = false;
			if (this.nw_n) {
				this.trace_NW_N(objectOutlineArrayArray, (PolygonalMap2.Node)null);
			} else {
				PolygonalMap2.ObjectOutline objectOutline = get(this.x, this.y - 1, this.z, objectOutlineArrayArray);
				if (objectOutline == null) {
					return;
				}

				if (objectOutline.w_w) {
					objectOutline.nodes = this.nodes;
					objectOutline.trace_W_W(objectOutlineArrayArray, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
				}
			}
		}

		void trace_NW_E(PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray, PolygonalMap2.Node node) {
			if (node != null) {
				node.setXY((float)this.x + 0.3F, (float)this.y + 0.3F);
			} else {
				PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init((float)this.x + 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(node2);
			}

			this.nw_e = false;
			if (this.nw_s) {
				this.trace_NW_S(objectOutlineArrayArray, (PolygonalMap2.Node)null);
			} else if (this.w_e) {
				this.trace_W_E(objectOutlineArrayArray, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
			}
		}

		void trace_W_E(PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray, PolygonalMap2.Node node) {
			PolygonalMap2.Node node2;
			if (node != null) {
				node.setXY((float)this.x + 0.3F, (float)(this.y + 1) - 0.3F);
			} else {
				node2 = PolygonalMap2.Node.alloc().init((float)this.x + 0.3F, (float)(this.y + 1) - 0.3F, this.z);
				this.nodes.add(node2);
			}

			this.w_e = false;
			if (this.w_cutoff) {
				node2 = (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1);
				node2.setXY((float)this.x + 0.3F, (float)(this.y + 1) + 0.3F);
				node2 = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)(this.y + 1) + 0.3F, this.z);
				this.nodes.add(node2);
				node2 = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)(this.y + 1) - 0.3F, this.z);
				this.nodes.add(node2);
				this.trace_W_W(objectOutlineArrayArray, node2);
			} else {
				PolygonalMap2.ObjectOutline objectOutline = get(this.x, this.y + 1, this.z, objectOutlineArrayArray);
				if (objectOutline != null) {
					if (objectOutline.nw && objectOutline.nw_e) {
						objectOutline.nodes = this.nodes;
						objectOutline.trace_NW_E(objectOutlineArrayArray, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
					} else if (objectOutline.n_n) {
						objectOutline.nodes = this.nodes;
						objectOutline.trace_N_N(objectOutlineArrayArray, (PolygonalMap2.Node)null);
					}
				}
			}
		}

		void trace_W_W(PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray, PolygonalMap2.Node node) {
			if (node != null) {
				node.setXY((float)this.x - 0.3F, (float)this.y + 0.3F);
			} else {
				PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(node2);
			}

			this.w_w = false;
			if (this.nw_w) {
				this.trace_NW_W(objectOutlineArrayArray, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
			} else {
				PolygonalMap2.ObjectOutline objectOutline = get(this.x - 1, this.y, this.z, objectOutlineArrayArray);
				if (objectOutline == null) {
					return;
				}

				if (objectOutline.n_s) {
					objectOutline.nodes = this.nodes;
					objectOutline.trace_N_S(objectOutlineArrayArray, (PolygonalMap2.Node)null);
				}
			}
		}

		void trace_N_N(PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray, PolygonalMap2.Node node) {
			PolygonalMap2.Node node2;
			if (node != null) {
				node.setXY((float)(this.x + 1) - 0.3F, (float)this.y - 0.3F);
			} else {
				node2 = PolygonalMap2.Node.alloc().init((float)(this.x + 1) - 0.3F, (float)this.y - 0.3F, this.z);
				this.nodes.add(node2);
			}

			this.n_n = false;
			if (this.n_cutoff) {
				node2 = (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1);
				node2.setXY((float)(this.x + 1) + 0.3F, (float)this.y - 0.3F);
				node2 = PolygonalMap2.Node.alloc().init((float)(this.x + 1) + 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(node2);
				node2 = PolygonalMap2.Node.alloc().init((float)(this.x + 1) - 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(node2);
				this.trace_N_S(objectOutlineArrayArray, node2);
			} else {
				PolygonalMap2.ObjectOutline objectOutline = get(this.x + 1, this.y, this.z, objectOutlineArrayArray);
				if (objectOutline != null) {
					if (objectOutline.nw_n) {
						objectOutline.nodes = this.nodes;
						objectOutline.trace_NW_N(objectOutlineArrayArray, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
					} else {
						objectOutline = get(this.x + 1, this.y - 1, this.z, objectOutlineArrayArray);
						if (objectOutline == null) {
							return;
						}

						if (objectOutline.w_w) {
							objectOutline.nodes = this.nodes;
							objectOutline.trace_W_W(objectOutlineArrayArray, (PolygonalMap2.Node)null);
						}
					}
				}
			}
		}

		void trace_N_S(PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray, PolygonalMap2.Node node) {
			if (node != null) {
				node.setXY((float)this.x + 0.3F, (float)this.y + 0.3F);
			} else {
				PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init((float)this.x + 0.3F, (float)this.y + 0.3F, this.z);
				this.nodes.add(node2);
			}

			this.n_s = false;
			if (this.nw_s) {
				this.trace_NW_S(objectOutlineArrayArray, (PolygonalMap2.Node)this.nodes.get(this.nodes.size() - 1));
			} else if (this.w_e) {
				this.trace_W_E(objectOutlineArrayArray, (PolygonalMap2.Node)null);
			}
		}

		void trace(PolygonalMap2.ObjectOutline[][] objectOutlineArrayArray, ArrayList arrayList) {
			arrayList.clear();
			this.nodes = arrayList;
			PolygonalMap2.Node node = PolygonalMap2.Node.alloc().init((float)this.x - 0.3F, (float)this.y - 0.3F, this.z);
			arrayList.add(node);
			this.trace_NW_N(objectOutlineArrayArray, (PolygonalMap2.Node)null);
			if (arrayList.size() != 2 && node.x == ((PolygonalMap2.Node)arrayList.get(arrayList.size() - 1)).x && node.y == ((PolygonalMap2.Node)arrayList.get(arrayList.size() - 1)).y) {
				((PolygonalMap2.Node)arrayList.get(arrayList.size() - 1)).release();
				arrayList.set(arrayList.size() - 1, node);
			} else {
				arrayList.clear();
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

	private static enum EdgeRingHit {

		OnEdge,
		Inside,
		Outside;

		private static PolygonalMap2.EdgeRingHit[] $values() {
			return new PolygonalMap2.EdgeRingHit[]{OnEdge, Inside, Outside};
		}
	}

	static final class L_lineSegmentIntersects {
		static final Vector2 v1 = new Vector2();
	}
}
