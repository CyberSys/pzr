package zombie.core.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;


public final class WorldSimulation {
	public static WorldSimulation instance = new WorldSimulation();
	public static final boolean LEVEL_ZERO_ONLY = true;
	public HashMap physicsObjectMap = new HashMap();
	public boolean created = false;
	public float offsetX = 0.0F;
	public float offsetY = 0.0F;
	public long time;
	private final ArrayList collideVehicles = new ArrayList(4);
	private final Vector3f tempVector3f = new Vector3f();
	private final Vector3f tempVector3f_2 = new Vector3f();
	private final Transform tempTransform = new Transform();
	private final Quaternionf javaxQuat4f = new Quaternionf();
	private final float[] ff = new float[8192];
	private final float[] wheelSteer = new float[4];
	private final float[] wheelRotation = new float[4];
	private final float[] wheelSkidInfo = new float[4];
	private final float[] wheelSuspensionLength = new float[4];

	public void create() {
		if (!this.created) {
			this.offsetX = (float)(IsoWorld.instance.MetaGrid.getMinX() * 300);
			this.offsetY = (float)(IsoWorld.instance.MetaGrid.getMinY() * 300);
			this.time = GameTime.getServerTimeMills();
			IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[0];
			Bullet.initWorld((int)this.offsetX, (int)this.offsetY, chunkMap.getWorldXMin(), chunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth);
			for (int int1 = 0; int1 < 4; ++int1) {
				this.wheelSteer[int1] = 0.0F;
				this.wheelRotation[int1] = 0.0F;
				this.wheelSkidInfo[int1] = 0.0F;
				this.wheelSuspensionLength[int1] = 0.0F;
			}

			this.created = true;
		}
	}

	public void destroy() {
		Bullet.destroyWorld();
	}

	private void updatePhysic(float float1) {
		MPStatistic.getInstance().Bullet.Start();
		Bullet.stepSimulation(float1, 2, 0.016666668F);
		MPStatistic.getInstance().Bullet.End();
		this.time = GameTime.getServerTimeMills();
	}

	public void update() {
		WorldSimulation.s_performance.worldSimulationUpdate.invokeAndMeasure(this, WorldSimulation::updateInternal);
	}

	private void updateInternal() {
		if (this.created) {
			this.updatePhysic(GameTime.instance.getRealworldSecondsSinceLastUpdate());
			this.collideVehicles.clear();
			int int1 = Bullet.getVehicleCount();
			int int2 = 0;
			int int3;
			int int4;
			int int5;
			int int6;
			float float1;
			float float2;
			float float3;
			while (int2 < int1) {
				MPStatistic.getInstance().Bullet.Start();
				int3 = Bullet.getVehiclePhysics(int2, this.ff);
				MPStatistic.getInstance().Bullet.End();
				if (int3 <= 0) {
					break;
				}

				int2 += int3;
				int4 = 0;
				for (int5 = 0; int5 < int3; ++int5) {
					int6 = (int)this.ff[int4++];
					float1 = this.ff[int4++];
					float2 = this.ff[int4++];
					float3 = this.ff[int4++];
					this.tempTransform.origin.set(float1, float2, float3);
					float float4 = this.ff[int4++];
					float float5 = this.ff[int4++];
					float float6 = this.ff[int4++];
					float float7 = this.ff[int4++];
					this.javaxQuat4f.set(float4, float5, float6, float7);
					this.tempTransform.setRotation(this.javaxQuat4f);
					float float8 = this.ff[int4++];
					float float9 = this.ff[int4++];
					float float10 = this.ff[int4++];
					this.tempVector3f.set(float8, float9, float10);
					float float11 = this.ff[int4++];
					float float12 = this.ff[int4++];
					int int7 = (int)this.ff[int4++];
					int int8;
					for (int8 = 0; int8 < int7; ++int8) {
						this.wheelSteer[int8] = this.ff[int4++];
						this.wheelRotation[int8] = this.ff[int4++];
						this.wheelSkidInfo[int8] = this.ff[int4++];
						this.wheelSuspensionLength[int8] = this.ff[int4++];
					}

					int8 = (int)(float1 * 100.0F + float2 * 100.0F + float3 * 100.0F + float4 * 100.0F + float5 * 100.0F + float6 * 100.0F + float7 * 100.0F);
					BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID((short)int6);
					if (baseVehicle != null && (!GameClient.bClient || !baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Remote) && !baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.RemoteCollide))) {
						if (baseVehicle.VehicleID == int6 && float12 > 0.5F) {
							this.collideVehicles.add(baseVehicle);
							baseVehicle.authSimulationHash = int8;
						}

						if (GameClient.bClient && baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.LocalCollide)) {
							if (baseVehicle.authSimulationHash != int8) {
								baseVehicle.authSimulationTime = System.currentTimeMillis();
								baseVehicle.authSimulationHash = int8;
							}

							if (System.currentTimeMillis() - baseVehicle.authSimulationTime > 1000L) {
								VehicleManager.instance.sendCollide(baseVehicle, baseVehicle.getDriver(), false);
								baseVehicle.authSimulationTime = 0L;
							}
						}

						if (!baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Remote) || !baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.RemoteCollide)) {
							if (GameClient.bClient && baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Server)) {
								baseVehicle.jniSpeed = 0.0F;
							} else {
								baseVehicle.jniSpeed = float11;
							}
						}

						if (!GameClient.bClient || !baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Server) && !baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Remote) && !baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.RemoteCollide)) {
							if (this.compareTransform(this.tempTransform, baseVehicle.getPoly().t)) {
								baseVehicle.polyDirty = true;
							}

							baseVehicle.jniTransform.set(this.tempTransform);
							baseVehicle.jniLinearVelocity.set((Vector3fc)this.tempVector3f);
							baseVehicle.jniIsCollide = float12 > 0.5F;
							for (int int9 = 0; int9 < int7; ++int9) {
								baseVehicle.wheelInfo[int9].steering = this.wheelSteer[int9];
								baseVehicle.wheelInfo[int9].rotation = this.wheelRotation[int9];
								baseVehicle.wheelInfo[int9].skidInfo = this.wheelSkidInfo[int9];
								baseVehicle.wheelInfo[int9].suspensionLength = this.wheelSuspensionLength[int9];
							}
						}
					}
				}
			}

			if (GameClient.bClient) {
				IsoPlayer player = IsoPlayer.players[IsoPlayer.getPlayerIndex()];
				if (player != null) {
					BaseVehicle baseVehicle2 = player.getVehicle();
					if (baseVehicle2 != null && baseVehicle2.isNetPlayerId(player.getOnlineID()) && this.collideVehicles.contains(baseVehicle2)) {
						Iterator iterator = this.collideVehicles.iterator();
						while (iterator.hasNext()) {
							BaseVehicle baseVehicle3 = (BaseVehicle)iterator.next();
							if (baseVehicle3.DistTo(baseVehicle2) < 8.0F && baseVehicle3.isNetPlayerAuthorization(BaseVehicle.Authorization.Server)) {
								VehicleManager.instance.sendCollide(baseVehicle3, player, true);
								baseVehicle3.authorizationClientCollide(player);
							}
						}
					}
				}
			}

			MPStatistic.getInstance().Bullet.Start();
			int3 = Bullet.getObjectPhysics(this.ff);
			MPStatistic.getInstance().Bullet.End();
			int4 = 0;
			for (int5 = 0; int5 < int3; ++int5) {
				int6 = (int)this.ff[int4++];
				float1 = this.ff[int4++];
				float2 = this.ff[int4++];
				float3 = this.ff[int4++];
				float1 += this.offsetX;
				float3 += this.offsetY;
				IsoMovingObject movingObject = (IsoMovingObject)this.physicsObjectMap.get(int6);
				if (movingObject != null) {
					movingObject.removeFromSquare();
					movingObject.setX(float1 + 0.18F);
					movingObject.setY(float3);
					movingObject.setZ(Math.max(0.0F, float2 / 3.0F / 0.82F));
					movingObject.setCurrent(IsoWorld.instance.getCell().getGridSquare((double)movingObject.getX(), (double)movingObject.getY(), (double)movingObject.getZ()));
				}
			}
		}
	}

	private boolean compareTransform(Transform transform, Transform transform2) {
		if (!(Math.abs(transform.origin.x - transform2.origin.x) > 0.01F) && !(Math.abs(transform.origin.z - transform2.origin.z) > 0.01F) && (int)transform.origin.y == (int)transform2.origin.y) {
			byte byte1 = 2;
			transform.basis.getColumn(byte1, this.tempVector3f_2);
			float float1 = this.tempVector3f_2.x;
			float float2 = this.tempVector3f_2.z;
			transform2.basis.getColumn(byte1, this.tempVector3f_2);
			float float3 = this.tempVector3f_2.x;
			float float4 = this.tempVector3f_2.z;
			return Math.abs(float1 - float3) > 0.001F || Math.abs(float2 - float4) > 0.001F;
		} else {
			return true;
		}
	}

	public int setOwnVehiclePhysics(int int1, float[] floatArray) {
		return Bullet.setOwnVehiclePhysics(int1, floatArray);
	}

	public void activateChunkMap(int int1) {
		this.create();
		IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int1];
		if (!GameServer.bServer) {
			Bullet.activateChunkMap(int1, chunkMap.getWorldXMin(), chunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth);
		}
	}

	public void deactivateChunkMap(int int1) {
		if (this.created) {
			Bullet.deactivateChunkMap(int1);
		}
	}

	public void scrollGroundLeft(int int1) {
		if (this.created) {
			Bullet.scrollChunkMapLeft(int1);
		}
	}

	public void scrollGroundRight(int int1) {
		if (this.created) {
			Bullet.scrollChunkMapRight(int1);
		}
	}

	public void scrollGroundUp(int int1) {
		if (this.created) {
			Bullet.scrollChunkMapUp(int1);
		}
	}

	public void scrollGroundDown(int int1) {
		if (this.created) {
			Bullet.scrollChunkMapDown(int1);
		}
	}

	public static TextureDraw.GenericDrawer getDrawer(int int1) {
		PhysicsDebugRenderer physicsDebugRenderer = PhysicsDebugRenderer.alloc();
		physicsDebugRenderer.init(IsoPlayer.players[int1]);
		return physicsDebugRenderer;
	}

	private static class s_performance {
		static final PerformanceProfileProbe worldSimulationUpdate = new PerformanceProfileProbe("WorldSimulation.update");
	}
}
