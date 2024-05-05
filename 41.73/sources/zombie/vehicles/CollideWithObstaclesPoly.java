package zombie.vehicles;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
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
import zombie.popman.ObjectPool;
import zombie.util.list.PZArrayUtil;


public class CollideWithObstaclesPoly {
	static final float RADIUS = 0.3F;
	private final ArrayList obstacles = new ArrayList();
	private final ArrayList nodes = new ArrayList();
	private final CollideWithObstaclesPoly.ImmutableRectF moveBounds = new CollideWithObstaclesPoly.ImmutableRectF();
	private final CollideWithObstaclesPoly.ImmutableRectF vehicleBounds = new CollideWithObstaclesPoly.ImmutableRectF();
	private static final Vector2 move = new Vector2();
	private static final Vector2 nodeNormal = new Vector2();
	private static final Vector2 edgeVec = new Vector2();
	private final ArrayList vehicles = new ArrayList();
	private Clipper clipper;
	private final ByteBuffer xyBuffer = ByteBuffer.allocateDirect(8192);
	private final CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge = new CollideWithObstaclesPoly.ClosestPointOnEdge();

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

	void getObstaclesInRect(float float1, float float2, float float3, float float4, int int1, int int2, int int3, boolean boolean1) {
		if (this.clipper == null) {
			this.clipper = new Clipper();
		}

		this.clipper.clear();
		this.moveBounds.init(float1 - 2.0F, float2 - 2.0F, float3 - float1 + 4.0F, float4 - float2 + 4.0F);
		int int4 = (int)(this.moveBounds.x / 10.0F);
		int int5 = (int)(this.moveBounds.y / 10.0F);
		int int6 = (int)Math.ceil((double)(this.moveBounds.right() / 10.0F));
		int int7 = (int)Math.ceil((double)(this.moveBounds.bottom() / 10.0F));
		if (Math.abs(float3 - float1) < 2.0F && Math.abs(float4 - float2) < 2.0F) {
			int4 = int1 / 10;
			int5 = int2 / 10;
			int6 = int4 + 1;
			int7 = int5 + 1;
		}

		for (int int8 = int5; int8 < int7; ++int8) {
			for (int int9 = int4; int9 < int6; ++int9) {
				IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int9, int8) : IsoWorld.instance.CurrentCell.getChunk(int9, int8);
				if (chunk != null) {
					CollideWithObstaclesPoly.ChunkDataZ chunkDataZ = chunk.collision.init(chunk, int3, this);
					ArrayList arrayList = boolean1 ? chunkDataZ.worldVehicleUnion : chunkDataZ.worldVehicleSeparate;
					for (int int10 = 0; int10 < arrayList.size(); ++int10) {
						CollideWithObstaclesPoly.CCObstacle cCObstacle = (CollideWithObstaclesPoly.CCObstacle)arrayList.get(int10);
						if (cCObstacle.bounds.intersects(this.moveBounds)) {
							this.obstacles.add(cCObstacle);
						}
					}

					this.nodes.addAll(chunkDataZ.nodes);
				}
			}
		}
	}

	public Vector2f resolveCollision(IsoGameCharacter gameCharacter, float float1, float float2, Vector2f vector2f) {
		vector2f.set(float1, float2);
		boolean boolean1 = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderObstacles.getValue();
		float float3 = gameCharacter.x;
		float float4 = gameCharacter.y;
		float float5 = float1;
		float float6 = float2;
		if (boolean1) {
			LineDrawer.addLine(float3, float4, (float)((int)gameCharacter.z), float1, float2, (float)((int)gameCharacter.z), 1.0F, 1.0F, 1.0F, (String)null, true);
		}

		if (float3 == float1 && float4 == float2) {
			return vector2f;
		} else {
			move.set(float1 - gameCharacter.x, float2 - gameCharacter.y);
			move.normalize();
			this.nodes.clear();
			this.obstacles.clear();
			this.getObstaclesInRect(Math.min(float3, float1), Math.min(float4, float2), Math.max(float3, float1), Math.max(float4, float2), (int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z, true);
			this.closestPointOnEdge.edge = null;
			this.closestPointOnEdge.node = null;
			this.closestPointOnEdge.distSq = Double.MAX_VALUE;
			for (int int1 = 0; int1 < this.obstacles.size(); ++int1) {
				CollideWithObstaclesPoly.CCObstacle cCObstacle = (CollideWithObstaclesPoly.CCObstacle)this.obstacles.get(int1);
				byte byte1 = 0;
				if (cCObstacle.isPointInside(gameCharacter.x, gameCharacter.y, byte1)) {
					cCObstacle.getClosestPointOnEdge(gameCharacter.x, gameCharacter.y, this.closestPointOnEdge);
				}
			}

			CollideWithObstaclesPoly.CCEdge cCEdge = this.closestPointOnEdge.edge;
			CollideWithObstaclesPoly.CCNode cCNode = this.closestPointOnEdge.node;
			float float7;
			if (cCEdge != null) {
				float7 = cCEdge.normal.dot(move);
				if (float7 >= 0.01F) {
					cCEdge = null;
				}
			}

			if (cCNode != null && cCNode.getNormalAndEdgeVectors(nodeNormal, edgeVec) && nodeNormal.dot(move) + 0.05F >= nodeNormal.dot(edgeVec)) {
				cCNode = null;
				cCEdge = null;
			}

			if (cCEdge == null) {
				this.closestPointOnEdge.edge = null;
				this.closestPointOnEdge.node = null;
				this.closestPointOnEdge.distSq = Double.MAX_VALUE;
				for (int int2 = 0; int2 < this.obstacles.size(); ++int2) {
					CollideWithObstaclesPoly.CCObstacle cCObstacle2 = (CollideWithObstaclesPoly.CCObstacle)this.obstacles.get(int2);
					cCObstacle2.lineSegmentIntersect(float3, float4, float5, float6, this.closestPointOnEdge, boolean1);
				}

				cCEdge = this.closestPointOnEdge.edge;
				cCNode = this.closestPointOnEdge.node;
			}

			if (cCNode != null) {
				move.set(float1 - gameCharacter.x, float2 - gameCharacter.y);
				move.normalize();
				CollideWithObstaclesPoly.CCEdge cCEdge2 = cCEdge;
				CollideWithObstaclesPoly.CCEdge cCEdge3 = null;
				for (int int3 = 0; int3 < cCNode.edges.size(); ++int3) {
					CollideWithObstaclesPoly.CCEdge cCEdge4 = (CollideWithObstaclesPoly.CCEdge)cCNode.edges.get(int3);
					if (cCEdge4 != cCEdge && (cCEdge2.node1.x != cCEdge4.node1.x || cCEdge2.node1.y != cCEdge4.node1.y || cCEdge2.node2.x != cCEdge4.node2.x || cCEdge2.node2.y != cCEdge4.node2.y) && (cCEdge2.node1.x != cCEdge4.node2.x || cCEdge2.node1.y != cCEdge4.node2.y || cCEdge2.node2.x != cCEdge4.node1.x || cCEdge2.node2.y != cCEdge4.node1.y) && (!cCEdge2.hasNode(cCEdge4.node1) || !cCEdge2.hasNode(cCEdge4.node2))) {
						cCEdge3 = cCEdge4;
					}
				}

				if (cCEdge2 != null && cCEdge3 != null) {
					CollideWithObstaclesPoly.CCNode cCNode2;
					if (cCEdge == cCEdge2) {
						cCNode2 = cCNode == cCEdge3.node1 ? cCEdge3.node2 : cCEdge3.node1;
						edgeVec.set(cCNode2.x - cCNode.x, cCNode2.y - cCNode.y);
						edgeVec.normalize();
						if (move.dot(edgeVec) >= 0.0F) {
							cCEdge = cCEdge3;
						}
					} else if (cCEdge == cCEdge3) {
						cCNode2 = cCNode == cCEdge2.node1 ? cCEdge2.node2 : cCEdge2.node1;
						edgeVec.set(cCNode2.x - cCNode.x, cCNode2.y - cCNode.y);
						edgeVec.normalize();
						if (move.dot(edgeVec) >= 0.0F) {
							cCEdge = cCEdge2;
						}
					}
				}
			}

			if (cCEdge != null) {
				if (boolean1) {
					float7 = cCEdge.node1.x;
					float float8 = cCEdge.node1.y;
					float float9 = cCEdge.node2.x;
					float float10 = cCEdge.node2.y;
					LineDrawer.addLine(float7, float8, (float)cCEdge.node1.z, float9, float10, (float)cCEdge.node1.z, 0.0F, 1.0F, 1.0F, (String)null, true);
				}

				this.closestPointOnEdge.distSq = Double.MAX_VALUE;
				cCEdge.getClosestPointOnEdge(float1, float2, this.closestPointOnEdge);
				vector2f.set(this.closestPointOnEdge.point.x, this.closestPointOnEdge.point.y);
			}

			return vector2f;
		}
	}

	boolean canStandAt(float float1, float float2, float float3, BaseVehicle baseVehicle, int int1) {
		boolean boolean1 = (int1 & 1) != 0;
		boolean boolean2 = (int1 & 2) != 0;
		float float4 = float1 - 0.3F;
		float float5 = float2 - 0.3F;
		float float6 = float1 + 0.3F;
		float float7 = float2 + 0.3F;
		this.nodes.clear();
		this.obstacles.clear();
		this.getObstaclesInRect(Math.min(float4, float6), Math.min(float5, float7), Math.max(float4, float6), Math.max(float5, float7), (int)float1, (int)float2, (int)float3, baseVehicle == null);
		for (int int2 = 0; int2 < this.obstacles.size(); ++int2) {
			CollideWithObstaclesPoly.CCObstacle cCObstacle = (CollideWithObstaclesPoly.CCObstacle)this.obstacles.get(int2);
			if ((baseVehicle == null || cCObstacle.vehicle != baseVehicle) && cCObstacle.isPointInside(float1, float2, int1)) {
				return false;
			}
		}

		return true;
	}

	public boolean isNotClear(float float1, float float2, float float3, float float4, int int1, boolean boolean1, BaseVehicle baseVehicle, boolean boolean2, boolean boolean3) {
		float float5 = float1;
		float float6 = float2;
		float float7 = float3;
		float float8 = float4;
		float1 /= 10.0F;
		float2 /= 10.0F;
		float3 /= 10.0F;
		float4 /= 10.0F;
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
			IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int2, int3) : IsoWorld.instance.CurrentCell.getChunk(int2, int3);
			if (chunk != null) {
				if (boolean1) {
					LineDrawer.addRect((float)(int2 * 10), (float)(int3 * 10), (float)int1, 10.0F, 10.0F, 1.0F, 1.0F, 1.0F);
				}

				CollideWithObstaclesPoly.ChunkDataZ chunkDataZ = chunk.collision.init(chunk, int1, this);
				ArrayList arrayList = baseVehicle == null ? chunkDataZ.worldVehicleUnion : chunkDataZ.worldVehicleSeparate;
				for (int int5 = 0; int5 < arrayList.size(); ++int5) {
					CollideWithObstaclesPoly.CCObstacle cCObstacle = (CollideWithObstaclesPoly.CCObstacle)arrayList.get(int5);
					if ((baseVehicle == null || cCObstacle.vehicle != baseVehicle) && cCObstacle.lineSegmentIntersects(float5, float6, float7, float8, boolean1)) {
						return true;
					}
				}
			}

			if (double3 > 0.0) {
				int3 += byte2;
				double3 -= double1;
			} else {
				int2 += byte1;
				double3 += double2;
			}
		}

		return false;
	}

	private void vehicleMoved(PolygonalMap2.VehiclePoly vehiclePoly) {
		byte byte1 = 2;
		int int1 = (int)Math.min(vehiclePoly.x1, Math.min(vehiclePoly.x2, Math.min(vehiclePoly.x3, vehiclePoly.x4)));
		int int2 = (int)Math.min(vehiclePoly.y1, Math.min(vehiclePoly.y2, Math.min(vehiclePoly.y3, vehiclePoly.y4)));
		int int3 = (int)Math.max(vehiclePoly.x1, Math.max(vehiclePoly.x2, Math.max(vehiclePoly.x3, vehiclePoly.x4)));
		int int4 = (int)Math.max(vehiclePoly.y1, Math.max(vehiclePoly.y2, Math.max(vehiclePoly.y3, vehiclePoly.y4)));
		int int5 = (int)vehiclePoly.z;
		int int6 = (int1 - byte1) / 10;
		int int7 = (int2 - byte1) / 10;
		int int8 = (int)Math.ceil((double)(((float)(int3 + byte1) - 1.0F) / 10.0F));
		int int9 = (int)Math.ceil((double)(((float)(int4 + byte1) - 1.0F) / 10.0F));
		for (int int10 = int7; int10 <= int9; ++int10) {
			for (int int11 = int6; int11 <= int8; ++int11) {
				IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(int11, int10);
				if (chunk != null && chunk.collision.data[int5] != null) {
					CollideWithObstaclesPoly.ChunkDataZ chunkDataZ = chunk.collision.data[int5];
					chunk.collision.data[int5] = null;
					chunkDataZ.clear();
					CollideWithObstaclesPoly.ChunkDataZ.pool.release((Object)chunkDataZ);
				}
			}
		}
	}

	public void vehicleMoved(PolygonalMap2.VehiclePoly vehiclePoly, PolygonalMap2.VehiclePoly vehiclePoly2) {
		this.vehicleMoved(vehiclePoly);
		this.vehicleMoved(vehiclePoly2);
	}

	public void render() {
		boolean boolean1 = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderObstacles.getValue();
		if (boolean1) {
			IsoPlayer player = IsoPlayer.getInstance();
			if (player == null) {
				return;
			}

			this.nodes.clear();
			this.obstacles.clear();
			this.getObstaclesInRect(player.x, player.y, player.x, player.y, (int)player.x, (int)player.y, (int)player.z, true);
			Iterator iterator;
			if (DebugOptions.instance.CollideWithObstaclesRenderNormals.getValue()) {
				iterator = this.nodes.iterator();
				while (iterator.hasNext()) {
					CollideWithObstaclesPoly.CCNode cCNode = (CollideWithObstaclesPoly.CCNode)iterator.next();
					if (cCNode.getNormalAndEdgeVectors(nodeNormal, edgeVec)) {
						LineDrawer.addLine(cCNode.x, cCNode.y, (float)cCNode.z, cCNode.x + nodeNormal.x, cCNode.y + nodeNormal.y, (float)cCNode.z, 0.0F, 0.0F, 1.0F, (String)null, true);
					}
				}
			}

			iterator = this.obstacles.iterator();
			while (iterator.hasNext()) {
				CollideWithObstaclesPoly.CCObstacle cCObstacle = (CollideWithObstaclesPoly.CCObstacle)iterator.next();
				cCObstacle.render();
			}
		}
	}

	private static final class ImmutableRectF {
		private float x;
		private float y;
		private float w;
		private float h;
		static final ArrayDeque pool = new ArrayDeque();

		CollideWithObstaclesPoly.ImmutableRectF init(float float1, float float2, float float3, float float4) {
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

		boolean intersects(CollideWithObstaclesPoly.ImmutableRectF immutableRectF) {
			return this.left() < immutableRectF.right() && this.right() > immutableRectF.left() && this.top() < immutableRectF.bottom() && this.bottom() > immutableRectF.top();
		}

		static CollideWithObstaclesPoly.ImmutableRectF alloc() {
			return pool.isEmpty() ? new CollideWithObstaclesPoly.ImmutableRectF() : (CollideWithObstaclesPoly.ImmutableRectF)pool.pop();
		}

		void release() {
			assert !pool.contains(this);
			pool.push(this);
		}
	}

	private static final class ClosestPointOnEdge {
		CollideWithObstaclesPoly.CCEdge edge;
		CollideWithObstaclesPoly.CCNode node;
		final Vector2f point = new Vector2f();
		double distSq;
	}

	public static final class ChunkData {
		final CollideWithObstaclesPoly.ChunkDataZ[] data = new CollideWithObstaclesPoly.ChunkDataZ[8];
		private boolean bClear = false;

		public CollideWithObstaclesPoly.ChunkDataZ init(IsoChunk chunk, int int1, CollideWithObstaclesPoly collideWithObstaclesPoly) {
			assert Thread.currentThread() == GameWindow.GameThread;
			if (this.bClear) {
				this.bClear = false;
				this.clearInner();
			}

			if (this.data[int1] == null) {
				this.data[int1] = (CollideWithObstaclesPoly.ChunkDataZ)CollideWithObstaclesPoly.ChunkDataZ.pool.alloc();
				this.data[int1].init(chunk, int1, collideWithObstaclesPoly);
			}

			return this.data[int1];
		}

		private void clearInner() {
			PZArrayUtil.forEach((Object[])this.data, (var0)->{
				if (var0 != null) {
					var0.clear();
					CollideWithObstaclesPoly.ChunkDataZ.pool.release((Object)var0);
				}
			});
			Arrays.fill(this.data, (Object)null);
		}

		public void clear() {
			this.bClear = true;
		}
	}

	public static final class ChunkDataZ {
		public final ArrayList worldVehicleUnion = new ArrayList();
		public final ArrayList worldVehicleSeparate = new ArrayList();
		public final ArrayList nodes = new ArrayList();
		public int z;
		public static final ObjectPool pool = new ObjectPool(CollideWithObstaclesPoly.ChunkDataZ::new);

		public void init(IsoChunk chunk, int int1, CollideWithObstaclesPoly collideWithObstaclesPoly) {
			this.z = int1;
			Clipper clipper = collideWithObstaclesPoly.clipper;
			clipper.clear();
			float float1 = 0.19800001F;
			int int2 = chunk.wx * 10;
			int int3 = chunk.wy * 10;
			int int4;
			for (int int5 = int3 - 2; int5 < int3 + 10 + 2; ++int5) {
				for (int4 = int2 - 2; int4 < int2 + 10 + 2; ++int4) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int1);
					if (square != null && !square.getObjects().isEmpty()) {
						if (square.isSolid() || square.isSolidTrans() && !square.isAdjacentToWindow()) {
							clipper.addAABBBevel((float)int4 - 0.3F, (float)int5 - 0.3F, (float)int4 + 1.0F + 0.3F, (float)int5 + 1.0F + 0.3F, float1);
						}

						boolean boolean1 = square.Is(IsoFlagType.collideW) || square.hasBlockedDoor(false) || square.HasStairsNorth();
						if (square.Is(IsoFlagType.windowW) || square.Is(IsoFlagType.WindowW)) {
							boolean1 = true;
						}

						boolean boolean2;
						boolean boolean3;
						if (boolean1) {
							if (!this.isCollideW(int4, int5 - 1, int1)) {
							}

							boolean2 = false;
							if (!this.isCollideW(int4, int5 + 1, int1)) {
							}

							boolean3 = false;
							clipper.addAABBBevel((float)int4 - 0.3F, (float)int5 - (boolean2 ? 0.0F : 0.3F), (float)int4 + 0.3F, (float)int5 + 1.0F + (boolean3 ? 0.0F : 0.3F), float1);
						}

						boolean2 = square.Is(IsoFlagType.collideN) || square.hasBlockedDoor(true) || square.HasStairsWest();
						if (square.Is(IsoFlagType.windowN) || square.Is(IsoFlagType.WindowN)) {
							boolean2 = true;
						}

						if (boolean2) {
							if (!this.isCollideN(int4 - 1, int5, int1)) {
							}

							boolean3 = false;
							if (!this.isCollideN(int4 + 1, int5, int1)) {
							}

							boolean boolean4 = false;
							clipper.addAABBBevel((float)int4 - (boolean3 ? 0.0F : 0.3F), (float)int5 - 0.3F, (float)int4 + 1.0F + (boolean4 ? 0.0F : 0.3F), (float)int5 + 0.3F, float1);
						}

						float float2;
						IsoGridSquare square2;
						IsoGridSquare square3;
						if (square.HasStairsNorth()) {
							square2 = IsoWorld.instance.CurrentCell.getGridSquare(int4 + 1, int5, int1);
							if (square2 != null) {
								clipper.addAABBBevel((float)(int4 + 1) - 0.3F, (float)int5 - 0.3F, (float)(int4 + 1) + 0.3F, (float)int5 + 1.0F + 0.3F, float1);
							}

							if (square.Has(IsoObjectType.stairsTN)) {
								square3 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int1 - 1);
								if (square3 == null || !square3.Has(IsoObjectType.stairsTN)) {
									clipper.addAABBBevel((float)int4 - 0.3F, (float)int5 - 0.3F, (float)int4 + 1.0F + 0.3F, (float)int5 + 0.3F, float1);
									float2 = 0.1F;
									clipper.clipAABB((float)int4 + 0.3F, (float)int5 - float2, (float)int4 + 1.0F - 0.3F, (float)int5 + 0.3F);
								}
							}
						}

						if (square.HasStairsWest()) {
							square2 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5 + 1, int1);
							if (square2 != null) {
								clipper.addAABBBevel((float)int4 - 0.3F, (float)(int5 + 1) - 0.3F, (float)int4 + 1.0F + 0.3F, (float)(int5 + 1) + 0.3F, float1);
							}

							if (square.Has(IsoObjectType.stairsTW)) {
								square3 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int1 - 1);
								if (square3 == null || !square3.Has(IsoObjectType.stairsTW)) {
									clipper.addAABBBevel((float)int4 - 0.3F, (float)int5 - 0.3F, (float)int4 + 0.3F, (float)int5 + 1.0F + 0.3F, float1);
									float2 = 0.1F;
									clipper.clipAABB((float)int4 - float2, (float)int5 + 0.3F, (float)int4 + 0.3F, (float)int5 + 1.0F - 0.3F);
								}
							}
						}
					}
				}
			}

			ByteBuffer byteBuffer = collideWithObstaclesPoly.xyBuffer;
			assert this.worldVehicleSeparate.isEmpty();
			this.clipperToObstacles(clipper, byteBuffer, this.worldVehicleSeparate);
			int4 = chunk.wx * 10;
			int int6 = chunk.wy * 10;
			int int7 = int4 + 10;
			int int8 = int6 + 10;
			int4 -= 2;
			int6 -= 2;
			int7 += 2;
			int8 += 2;
			CollideWithObstaclesPoly.ImmutableRectF immutableRectF = collideWithObstaclesPoly.moveBounds.init((float)int4, (float)int6, (float)(int7 - int4), (float)(int8 - int6));
			collideWithObstaclesPoly.getVehiclesInRect((float)(int4 - 5), (float)(int6 - 5), (float)(int7 + 5), (float)(int8 + 5), int1);
			for (int int9 = 0; int9 < collideWithObstaclesPoly.vehicles.size(); ++int9) {
				BaseVehicle baseVehicle = (BaseVehicle)collideWithObstaclesPoly.vehicles.get(int9);
				PolygonalMap2.VehiclePoly vehiclePoly = baseVehicle.getPolyPlusRadius();
				float float3 = Math.min(vehiclePoly.x1, Math.min(vehiclePoly.x2, Math.min(vehiclePoly.x3, vehiclePoly.x4)));
				float float4 = Math.min(vehiclePoly.y1, Math.min(vehiclePoly.y2, Math.min(vehiclePoly.y3, vehiclePoly.y4)));
				float float5 = Math.max(vehiclePoly.x1, Math.max(vehiclePoly.x2, Math.max(vehiclePoly.x3, vehiclePoly.x4)));
				float float6 = Math.max(vehiclePoly.y1, Math.max(vehiclePoly.y2, Math.max(vehiclePoly.y3, vehiclePoly.y4)));
				collideWithObstaclesPoly.vehicleBounds.init(float3, float4, float5 - float3, float6 - float4);
				if (immutableRectF.intersects(collideWithObstaclesPoly.vehicleBounds)) {
					clipper.addPolygon(vehiclePoly.x1, vehiclePoly.y1, vehiclePoly.x4, vehiclePoly.y4, vehiclePoly.x3, vehiclePoly.y3, vehiclePoly.x2, vehiclePoly.y2);
					CollideWithObstaclesPoly.CCNode cCNode = CollideWithObstaclesPoly.CCNode.alloc().init(vehiclePoly.x1, vehiclePoly.y1, int1);
					CollideWithObstaclesPoly.CCNode cCNode2 = CollideWithObstaclesPoly.CCNode.alloc().init(vehiclePoly.x2, vehiclePoly.y2, int1);
					CollideWithObstaclesPoly.CCNode cCNode3 = CollideWithObstaclesPoly.CCNode.alloc().init(vehiclePoly.x3, vehiclePoly.y3, int1);
					CollideWithObstaclesPoly.CCNode cCNode4 = CollideWithObstaclesPoly.CCNode.alloc().init(vehiclePoly.x4, vehiclePoly.y4, int1);
					CollideWithObstaclesPoly.CCObstacle cCObstacle = CollideWithObstaclesPoly.CCObstacle.alloc().init();
					cCObstacle.vehicle = baseVehicle;
					CollideWithObstaclesPoly.CCEdge cCEdge = CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode, cCNode2, cCObstacle);
					CollideWithObstaclesPoly.CCEdge cCEdge2 = CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode2, cCNode3, cCObstacle);
					CollideWithObstaclesPoly.CCEdge cCEdge3 = CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode3, cCNode4, cCObstacle);
					CollideWithObstaclesPoly.CCEdge cCEdge4 = CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode4, cCNode, cCObstacle);
					cCObstacle.outer.add(cCEdge);
					cCObstacle.outer.add(cCEdge2);
					cCObstacle.outer.add(cCEdge3);
					cCObstacle.outer.add(cCEdge4);
					cCObstacle.calcBounds();
					this.worldVehicleSeparate.add(cCObstacle);
					this.nodes.add(cCNode);
					this.nodes.add(cCNode2);
					this.nodes.add(cCNode3);
					this.nodes.add(cCNode4);
				}
			}

			assert this.worldVehicleUnion.isEmpty();
			this.clipperToObstacles(clipper, byteBuffer, this.worldVehicleUnion);
		}

		private void getEdgesFromBuffer(ByteBuffer byteBuffer, CollideWithObstaclesPoly.CCObstacle cCObstacle, boolean boolean1) {
			short short1 = byteBuffer.getShort();
			if (short1 < 3) {
				byteBuffer.position(byteBuffer.position() + short1 * 4 * 2);
			} else {
				CollideWithObstaclesPoly.CCEdgeRing cCEdgeRing = cCObstacle.outer;
				if (!boolean1) {
					cCEdgeRing = (CollideWithObstaclesPoly.CCEdgeRing)CollideWithObstaclesPoly.CCEdgeRing.pool.alloc();
					cCEdgeRing.clear();
					cCObstacle.inner.add(cCEdgeRing);
				}

				int int1 = this.nodes.size();
				int int2;
				for (int2 = 0; int2 < short1; ++int2) {
					float float1 = byteBuffer.getFloat();
					float float2 = byteBuffer.getFloat();
					CollideWithObstaclesPoly.CCNode cCNode = CollideWithObstaclesPoly.CCNode.alloc().init(float1, float2, this.z);
					this.nodes.add(int1, cCNode);
				}

				CollideWithObstaclesPoly.CCNode cCNode2;
				for (int2 = int1; int2 < this.nodes.size() - 1; ++int2) {
					cCNode2 = (CollideWithObstaclesPoly.CCNode)this.nodes.get(int2);
					CollideWithObstaclesPoly.CCNode cCNode3 = (CollideWithObstaclesPoly.CCNode)this.nodes.get(int2 + 1);
					CollideWithObstaclesPoly.CCEdge cCEdge = CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode2, cCNode3, cCObstacle);
					cCEdgeRing.add(cCEdge);
				}

				CollideWithObstaclesPoly.CCNode cCNode4 = (CollideWithObstaclesPoly.CCNode)this.nodes.get(this.nodes.size() - 1);
				cCNode2 = (CollideWithObstaclesPoly.CCNode)this.nodes.get(int1);
				cCEdgeRing.add(CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode4, cCNode2, cCObstacle));
			}
		}

		private void clipperToObstacles(Clipper clipper, ByteBuffer byteBuffer, ArrayList arrayList) {
			int int1 = clipper.generatePolygons();
			for (int int2 = 0; int2 < int1; ++int2) {
				byteBuffer.clear();
				clipper.getPolygon(int2, byteBuffer);
				CollideWithObstaclesPoly.CCObstacle cCObstacle = CollideWithObstaclesPoly.CCObstacle.alloc().init();
				this.getEdgesFromBuffer(byteBuffer, cCObstacle, true);
				short short1 = byteBuffer.getShort();
				for (int int3 = 0; int3 < short1; ++int3) {
					this.getEdgesFromBuffer(byteBuffer, cCObstacle, false);
				}

				cCObstacle.calcBounds();
				arrayList.add(cCObstacle);
			}
		}

		boolean isCollideW(int int1, int int2, int int3) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			return square != null && (square.Is(IsoFlagType.collideW) || square.hasBlockedDoor(false) || square.HasStairsNorth());
		}

		boolean isCollideN(int int1, int int2, int int3) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			return square != null && (square.Is(IsoFlagType.collideN) || square.hasBlockedDoor(true) || square.HasStairsWest());
		}

		boolean isOpenDoorAt(int int1, int int2, int int3, boolean boolean1) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			return square != null && square.getDoor(boolean1) != null && !square.hasBlockedDoor(boolean1);
		}

		public void clear() {
			CollideWithObstaclesPoly.CCNode.releaseAll(this.nodes);
			this.nodes.clear();
			CollideWithObstaclesPoly.CCObstacle.releaseAll(this.worldVehicleUnion);
			this.worldVehicleUnion.clear();
			CollideWithObstaclesPoly.CCObstacle.releaseAll(this.worldVehicleSeparate);
			this.worldVehicleSeparate.clear();
		}
	}

	private static final class CCObstacle {
		final CollideWithObstaclesPoly.CCEdgeRing outer = new CollideWithObstaclesPoly.CCEdgeRing();
		final ArrayList inner = new ArrayList();
		BaseVehicle vehicle = null;
		CollideWithObstaclesPoly.ImmutableRectF bounds;
		static final ObjectPool pool = new ObjectPool(CollideWithObstaclesPoly.CCObstacle::new){
    
    public void release(CollideWithObstaclesPoly.CCObstacle var1) {
        CollideWithObstaclesPoly.CCEdge.releaseAll(var1.outer);
        CollideWithObstaclesPoly.CCEdgeRing.releaseAll(var1.inner);
        var1.outer.clear();
        var1.inner.clear();
        var1.vehicle = null;
        super.release((Object)var1);
    }
};

		CollideWithObstaclesPoly.CCObstacle init() {
			this.outer.clear();
			this.inner.clear();
			this.vehicle = null;
			return this;
		}

		boolean isPointInside(float float1, float float2, int int1) {
			if (this.outer.isPointInPolygon_WindingNumber(float1, float2, int1) != CollideWithObstaclesPoly.EdgeRingHit.Inside) {
				return false;
			} else if (this.inner.isEmpty()) {
				return true;
			} else {
				for (int int2 = 0; int2 < this.inner.size(); ++int2) {
					CollideWithObstaclesPoly.CCEdgeRing cCEdgeRing = (CollideWithObstaclesPoly.CCEdgeRing)this.inner.get(int2);
					if (cCEdgeRing.isPointInPolygon_WindingNumber(float1, float2, int1) != CollideWithObstaclesPoly.EdgeRingHit.Outside) {
						return false;
					}
				}

				return true;
			}
		}

		boolean lineSegmentIntersects(float float1, float float2, float float3, float float4, boolean boolean1) {
			if (this.outer.lineSegmentIntersects(float1, float2, float3, float4, boolean1, true)) {
				return true;
			} else {
				for (int int1 = 0; int1 < this.inner.size(); ++int1) {
					CollideWithObstaclesPoly.CCEdgeRing cCEdgeRing = (CollideWithObstaclesPoly.CCEdgeRing)this.inner.get(int1);
					if (cCEdgeRing.lineSegmentIntersects(float1, float2, float3, float4, boolean1, false)) {
						return true;
					}
				}

				return false;
			}
		}

		void lineSegmentIntersect(float float1, float float2, float float3, float float4, CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge, boolean boolean1) {
			this.outer.lineSegmentIntersect(float1, float2, float3, float4, closestPointOnEdge, boolean1);
			for (int int1 = 0; int1 < this.inner.size(); ++int1) {
				CollideWithObstaclesPoly.CCEdgeRing cCEdgeRing = (CollideWithObstaclesPoly.CCEdgeRing)this.inner.get(int1);
				cCEdgeRing.lineSegmentIntersect(float1, float2, float3, float4, closestPointOnEdge, boolean1);
			}
		}

		void getClosestPointOnEdge(float float1, float float2, CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge) {
			this.outer.getClosestPointOnEdge(float1, float2, closestPointOnEdge);
			for (int int1 = 0; int1 < this.inner.size(); ++int1) {
				CollideWithObstaclesPoly.CCEdgeRing cCEdgeRing = (CollideWithObstaclesPoly.CCEdgeRing)this.inner.get(int1);
				cCEdgeRing.getClosestPointOnEdge(float1, float2, closestPointOnEdge);
			}
		}

		void calcBounds() {
			float float1 = Float.MAX_VALUE;
			float float2 = Float.MAX_VALUE;
			float float3 = Float.MIN_VALUE;
			float float4 = Float.MIN_VALUE;
			for (int int1 = 0; int1 < this.outer.size(); ++int1) {
				CollideWithObstaclesPoly.CCEdge cCEdge = (CollideWithObstaclesPoly.CCEdge)this.outer.get(int1);
				float1 = Math.min(float1, cCEdge.node1.x);
				float2 = Math.min(float2, cCEdge.node1.y);
				float3 = Math.max(float3, cCEdge.node1.x);
				float4 = Math.max(float4, cCEdge.node1.y);
			}

			if (this.bounds != null) {
				this.bounds.release();
			}

			float float5 = 0.01F;
			this.bounds = CollideWithObstaclesPoly.ImmutableRectF.alloc().init(float1 - float5, float2 - float5, float3 - float1 + float5 * 2.0F, float4 - float2 + float5 * 2.0F);
		}

		void render() {
			this.outer.render(true);
			for (int int1 = 0; int1 < this.inner.size(); ++int1) {
				((CollideWithObstaclesPoly.CCEdgeRing)this.inner.get(int1)).render(false);
			}
		}

		static CollideWithObstaclesPoly.CCObstacle alloc() {
			return (CollideWithObstaclesPoly.CCObstacle)pool.alloc();
		}

		void release() {
			pool.release((Object)this);
		}

		static void releaseAll(ArrayList arrayList) {
			pool.releaseAll(arrayList);
		}
	}

	private static final class CCEdge {
		CollideWithObstaclesPoly.CCNode node1;
		CollideWithObstaclesPoly.CCNode node2;
		CollideWithObstaclesPoly.CCObstacle obstacle;
		final Vector2 normal = new Vector2();
		static final ObjectPool pool = new ObjectPool(CollideWithObstaclesPoly.CCEdge::new);

		CollideWithObstaclesPoly.CCEdge init(CollideWithObstaclesPoly.CCNode cCNode, CollideWithObstaclesPoly.CCNode cCNode2, CollideWithObstaclesPoly.CCObstacle cCObstacle) {
			if (cCNode.x == cCNode2.x && cCNode.y == cCNode2.y) {
				boolean boolean1 = false;
			}

			this.node1 = cCNode;
			this.node2 = cCNode2;
			cCNode.edges.add(this);
			cCNode2.edges.add(this);
			this.obstacle = cCObstacle;
			this.normal.set(cCNode2.x - cCNode.x, cCNode2.y - cCNode.y);
			this.normal.normalize();
			this.normal.rotate((float)Math.toRadians(90.0));
			return this;
		}

		boolean hasNode(CollideWithObstaclesPoly.CCNode cCNode) {
			return cCNode == this.node1 || cCNode == this.node2;
		}

		void getClosestPointOnEdge(float float1, float float2, CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge) {
			float float3 = this.node1.x;
			float float4 = this.node1.y;
			float float5 = this.node2.x;
			float float6 = this.node2.y;
			double double1 = (double)((float1 - float3) * (float5 - float3) + (float2 - float4) * (float6 - float4)) / (Math.pow((double)(float5 - float3), 2.0) + Math.pow((double)(float6 - float4), 2.0));
			double double2 = (double)float3 + double1 * (double)(float5 - float3);
			double double3 = (double)float4 + double1 * (double)(float6 - float4);
			double double4 = 0.001;
			CollideWithObstaclesPoly.CCNode cCNode = null;
			if (double1 <= 0.0 + double4) {
				double2 = (double)float3;
				double3 = (double)float4;
				cCNode = this.node1;
			} else if (double1 >= 1.0 - double4) {
				double2 = (double)float5;
				double3 = (double)float6;
				cCNode = this.node2;
			}

			double double5 = ((double)float1 - double2) * ((double)float1 - double2) + ((double)float2 - double3) * ((double)float2 - double3);
			if (double5 < closestPointOnEdge.distSq) {
				closestPointOnEdge.point.set((float)double2, (float)double3);
				closestPointOnEdge.distSq = double5;
				closestPointOnEdge.edge = this;
				closestPointOnEdge.node = cCNode;
			}
		}

		boolean isPointOn(float float1, float float2) {
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

		static CollideWithObstaclesPoly.CCEdge alloc() {
			return (CollideWithObstaclesPoly.CCEdge)pool.alloc();
		}

		void release() {
			pool.release((Object)this);
		}

		static void releaseAll(ArrayList arrayList) {
			pool.releaseAll(arrayList);
		}
	}

	private static final class CCNode {
		float x;
		float y;
		int z;
		final ArrayList edges = new ArrayList();
		static final ObjectPool pool = new ObjectPool(CollideWithObstaclesPoly.CCNode::new);

		CollideWithObstaclesPoly.CCNode init(float float1, float float2, int int1) {
			this.x = float1;
			this.y = float2;
			this.z = int1;
			this.edges.clear();
			return this;
		}

		CollideWithObstaclesPoly.CCNode setXY(float float1, float float2) {
			this.x = float1;
			this.y = float2;
			return this;
		}

		boolean getNormalAndEdgeVectors(Vector2 vector2, Vector2 vector22) {
			CollideWithObstaclesPoly.CCEdge cCEdge = null;
			CollideWithObstaclesPoly.CCEdge cCEdge2 = null;
			for (int int1 = 0; int1 < this.edges.size(); ++int1) {
				CollideWithObstaclesPoly.CCEdge cCEdge3 = (CollideWithObstaclesPoly.CCEdge)this.edges.get(int1);
				if (cCEdge == null) {
					cCEdge = cCEdge3;
				} else if (!cCEdge.hasNode(cCEdge3.node1) || !cCEdge.hasNode(cCEdge3.node2)) {
					cCEdge2 = cCEdge3;
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

		static CollideWithObstaclesPoly.CCNode alloc() {
			return (CollideWithObstaclesPoly.CCNode)pool.alloc();
		}

		void release() {
			pool.release((Object)this);
		}

		static void releaseAll(ArrayList arrayList) {
			pool.releaseAll(arrayList);
		}
	}

	private static final class CCEdgeRing extends ArrayList {
		static final ObjectPool pool = new ObjectPool(CollideWithObstaclesPoly.CCEdgeRing::new){
    
    public void release(CollideWithObstaclesPoly.CCEdgeRing var1) {
        CollideWithObstaclesPoly.CCEdge.releaseAll(var1);
        this.clear();
        super.release((Object)var1);
    }
};

		float isLeft(float float1, float float2, float float3, float float4, float float5, float float6) {
			return (float3 - float1) * (float6 - float2) - (float5 - float1) * (float4 - float2);
		}

		CollideWithObstaclesPoly.EdgeRingHit isPointInPolygon_WindingNumber(float float1, float float2, int int1) {
			int int2 = 0;
			for (int int3 = 0; int3 < this.size(); ++int3) {
				CollideWithObstaclesPoly.CCEdge cCEdge = (CollideWithObstaclesPoly.CCEdge)this.get(int3);
				if ((int1 & 16) != 0 && cCEdge.isPointOn(float1, float2)) {
					return CollideWithObstaclesPoly.EdgeRingHit.OnEdge;
				}

				if (cCEdge.node1.y <= float2) {
					if (cCEdge.node2.y > float2 && this.isLeft(cCEdge.node1.x, cCEdge.node1.y, cCEdge.node2.x, cCEdge.node2.y, float1, float2) > 0.0F) {
						++int2;
					}
				} else if (cCEdge.node2.y <= float2 && this.isLeft(cCEdge.node1.x, cCEdge.node1.y, cCEdge.node2.x, cCEdge.node2.y, float1, float2) < 0.0F) {
					--int2;
				}
			}

			return int2 == 0 ? CollideWithObstaclesPoly.EdgeRingHit.Outside : CollideWithObstaclesPoly.EdgeRingHit.Inside;
		}

		boolean lineSegmentIntersects(float float1, float float2, float float3, float float4, boolean boolean1, boolean boolean2) {
			CollideWithObstaclesPoly.move.set(float3 - float1, float4 - float2);
			float float5 = CollideWithObstaclesPoly.move.getLength();
			CollideWithObstaclesPoly.move.normalize();
			float float6 = CollideWithObstaclesPoly.move.x;
			float float7 = CollideWithObstaclesPoly.move.y;
			for (int int1 = 0; int1 < this.size(); ++int1) {
				CollideWithObstaclesPoly.CCEdge cCEdge = (CollideWithObstaclesPoly.CCEdge)this.get(int1);
				if (!cCEdge.isPointOn(float1, float2) && !cCEdge.isPointOn(float3, float4)) {
					float float8 = cCEdge.normal.dot(CollideWithObstaclesPoly.move);
					if (!(float8 >= 0.01F)) {
						float float9 = cCEdge.node1.x;
						float float10 = cCEdge.node1.y;
						float float11 = cCEdge.node2.x;
						float float12 = cCEdge.node2.y;
						float float13 = float1 - float9;
						float float14 = float2 - float10;
						float float15 = float11 - float9;
						float float16 = float12 - float10;
						float float17 = 1.0F / (float16 * float6 - float15 * float7);
						float float18 = (float15 * float14 - float16 * float13) * float17;
						if (float18 >= 0.0F && float18 <= float5) {
							float float19 = (float14 * float6 - float13 * float7) * float17;
							if (float19 >= 0.0F && float19 <= 1.0F) {
								float float20 = float1 + float18 * float6;
								float float21 = float2 + float18 * float7;
								if (boolean1) {
									this.render(boolean2);
									LineDrawer.addRect(float20 - 0.05F, float21 - 0.05F, (float)cCEdge.node1.z, 0.1F, 0.1F, 1.0F, 1.0F, 1.0F);
								}

								return true;
							}
						}
					}
				}
			}

			if (this.isPointInPolygon_WindingNumber((float1 + float3) / 2.0F, (float2 + float4) / 2.0F, 0) != CollideWithObstaclesPoly.EdgeRingHit.Outside) {
				return true;
			} else {
				return false;
			}
		}

		void lineSegmentIntersect(float float1, float float2, float float3, float float4, CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge, boolean boolean1) {
			CollideWithObstaclesPoly.move.set(float3 - float1, float4 - float2).normalize();
			for (int int1 = 0; int1 < this.size(); ++int1) {
				CollideWithObstaclesPoly.CCEdge cCEdge = (CollideWithObstaclesPoly.CCEdge)this.get(int1);
				float float5 = cCEdge.normal.dot(CollideWithObstaclesPoly.move);
				if (!(float5 >= 0.0F)) {
					float float6 = cCEdge.node1.x;
					float float7 = cCEdge.node1.y;
					float float8 = cCEdge.node2.x;
					float float9 = cCEdge.node2.y;
					float float10 = float6 + 0.5F * (float8 - float6);
					float float11 = float7 + 0.5F * (float9 - float7);
					if (boolean1 && DebugOptions.instance.CollideWithObstaclesRenderNormals.getValue()) {
						LineDrawer.addLine(float10, float11, (float)cCEdge.node1.z, float10 + cCEdge.normal.x, float11 + cCEdge.normal.y, (float)cCEdge.node1.z, 0.0F, 0.0F, 1.0F, (String)null, true);
					}

					double double1 = (double)((float9 - float7) * (float3 - float1) - (float8 - float6) * (float4 - float2));
					if (double1 != 0.0) {
						double double2 = (double)((float8 - float6) * (float2 - float7) - (float9 - float7) * (float1 - float6)) / double1;
						double double3 = (double)((float3 - float1) * (float2 - float7) - (float4 - float2) * (float1 - float6)) / double1;
						if (double2 >= 0.0 && double2 <= 1.0 && double3 >= 0.0 && double3 <= 1.0) {
							if (double3 < 0.01 || double3 > 0.99) {
								CollideWithObstaclesPoly.CCNode cCNode = double3 < 0.01 ? cCEdge.node1 : cCEdge.node2;
								double double4 = (double)IsoUtils.DistanceToSquared(float1, float2, cCNode.x, cCNode.y);
								if (double4 >= closestPointOnEdge.distSq) {
									continue;
								}

								if (cCNode.getNormalAndEdgeVectors(CollideWithObstaclesPoly.nodeNormal, CollideWithObstaclesPoly.edgeVec)) {
									if (!(CollideWithObstaclesPoly.nodeNormal.dot(CollideWithObstaclesPoly.move) + 0.05F >= CollideWithObstaclesPoly.nodeNormal.dot(CollideWithObstaclesPoly.edgeVec))) {
										closestPointOnEdge.edge = cCEdge;
										closestPointOnEdge.node = cCNode;
										closestPointOnEdge.distSq = double4;
									}

									continue;
								}
							}

							float float12 = (float)((double)float1 + double2 * (double)(float3 - float1));
							float float13 = (float)((double)float2 + double2 * (double)(float4 - float2));
							double double5 = (double)IsoUtils.DistanceToSquared(float1, float2, float12, float13);
							if (double5 < closestPointOnEdge.distSq) {
								closestPointOnEdge.edge = cCEdge;
								closestPointOnEdge.node = null;
								closestPointOnEdge.distSq = double5;
							}
						}
					}
				}
			}
		}

		void getClosestPointOnEdge(float float1, float float2, CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge) {
			for (int int1 = 0; int1 < this.size(); ++int1) {
				CollideWithObstaclesPoly.CCEdge cCEdge = (CollideWithObstaclesPoly.CCEdge)this.get(int1);
				cCEdge.getClosestPointOnEdge(float1, float2, closestPointOnEdge);
			}
		}

		void render(boolean boolean1) {
			if (!this.isEmpty()) {
				float float1 = 0.0F;
				float float2 = boolean1 ? 1.0F : 0.5F;
				float float3 = boolean1 ? 0.0F : 0.5F;
				BaseVehicle.Vector3fObjectPool vector3fObjectPool = (BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get();
				Iterator iterator = this.iterator();
				while (iterator.hasNext()) {
					CollideWithObstaclesPoly.CCEdge cCEdge = (CollideWithObstaclesPoly.CCEdge)iterator.next();
					CollideWithObstaclesPoly.CCNode cCNode = cCEdge.node1;
					CollideWithObstaclesPoly.CCNode cCNode2 = cCEdge.node2;
					LineDrawer.addLine(cCNode.x, cCNode.y, (float)cCNode.z, cCNode2.x, cCNode2.y, (float)cCNode2.z, float1, float2, float3, (String)null, true);
					boolean boolean2 = false;
					if (boolean2) {
						Vector3f vector3f = ((Vector3f)vector3fObjectPool.alloc()).set(cCNode2.x - cCNode.x, cCNode2.y - cCNode.y, (float)(cCNode2.z - cCNode.z)).normalize();
						Vector3f vector3f2 = ((Vector3f)vector3fObjectPool.alloc()).set((Vector3fc)vector3f).cross(0.0F, 0.0F, 1.0F).normalize();
						vector3f.mul(0.9F);
						LineDrawer.addLine(cCNode2.x - vector3f.x * 0.1F - vector3f2.x * 0.1F, cCNode2.y - vector3f.y * 0.1F - vector3f2.y * 0.1F, (float)cCNode2.z, cCNode2.x, cCNode2.y, (float)cCNode2.z, float1, float2, float3, (String)null, true);
						LineDrawer.addLine(cCNode2.x - vector3f.x * 0.1F + vector3f2.x * 0.1F, cCNode2.y - vector3f.y * 0.1F + vector3f2.y * 0.1F, (float)cCNode2.z, cCNode2.x, cCNode2.y, (float)cCNode2.z, float1, float2, float3, (String)null, true);
						vector3fObjectPool.release(vector3f);
						vector3fObjectPool.release(vector3f2);
					}
				}

				CollideWithObstaclesPoly.CCNode cCNode3 = ((CollideWithObstaclesPoly.CCEdge)this.get(0)).node1;
				LineDrawer.addRect(cCNode3.x - 0.1F, cCNode3.y - 0.1F, (float)cCNode3.z, 0.2F, 0.2F, 1.0F, 0.0F, 0.0F);
			}
		}

		static void releaseAll(ArrayList arrayList) {
			pool.releaseAll(arrayList);
		}
	}

	private static enum EdgeRingHit {

		OnEdge,
		Inside,
		Outside;

		private static CollideWithObstaclesPoly.EdgeRingHit[] $values() {
			return new CollideWithObstaclesPoly.EdgeRingHit[]{OnEdge, Inside, Outside};
		}
	}
}
