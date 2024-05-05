package zombie.core.physics;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.network.ServerMap;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;


public final class WorldSimulation {
	public static WorldSimulation instance = new WorldSimulation();
	public static final boolean LEVEL_ZERO_ONLY = true;
	public float offsetX = 0.0F;
	public float offsetY = 0.0F;
	public int maxSubSteps;
	public boolean created = false;
	public long time;
	public HashMap physicsObjectMap = new HashMap();
	int count = 0;
	static final boolean DEBUG = false;
	BufferedWriter DebugInDataWriter;
	BufferedWriter DebugOutDataWriter;
	private final float[] ff = new float[8192];
	private final float[] wheelSteer = new float[4];
	private final float[] wheelRotation = new float[4];
	private final float[] wheelSkidInfo = new float[4];
	private final float[] wheelSuspensionLength = new float[4];
	ArrayList collideVehicles = new ArrayList(4);
	protected Transform tempTransform = new Transform();
	protected Quaternionf javaxQuat4f = new Quaternionf();
	private Vector3f tempVector3f = new Vector3f();
	private Vector3f tempVector3f_2 = new Vector3f();

	public void create() {
		if (!this.created) {
			this.offsetX = (float)(IsoWorld.instance.MetaGrid.getMinX() * 300);
			this.offsetY = (float)(IsoWorld.instance.MetaGrid.getMinY() * 300);
			this.time = GameTime.getServerTime();
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
		this.time = GameTime.getServerTime();
	}

	public void update() {
		WorldSimulation.s_performance.worldSimulationUpdate.invokeAndMeasure(this, WorldSimulation::updateInternal);
	}

	private void updateInternal() {
		if (this.created) {
			this.updatePhysic(GameTime.instance.getRealworldSecondsSinceLastUpdate());
			this.collideVehicles.clear();
			BaseVehicle baseVehicle = null;
			IsoPlayer player = IsoPlayer.players[IsoPlayer.getPlayerIndex()];
			if (player != null) {
				baseVehicle = player.getVehicle();
			}

			int int1 = Bullet.getVehicleCount();
			int int2 = 0;
			int int3;
			int int4;
			int int5;
			float float1;
			float float2;
			int int6;
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
					boolean boolean1 = false;
					int int7 = (int)this.ff[int4++];
					float1 = this.ff[int4++];
					float2 = this.ff[int4++];
					float float3 = this.ff[int4++];
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
					int int8 = (int)this.ff[int4++];
					for (int int9 = 0; int9 < int8; ++int9) {
						this.wheelSteer[int9] = this.ff[int4++];
						this.wheelRotation[int9] = this.ff[int4++];
						this.wheelSkidInfo[int9] = this.ff[int4++];
						this.wheelSuspensionLength[int9] = this.ff[int4++];
					}

					int6 = (int)(float1 * 100.0F + float2 * 100.0F + float3 * 100.0F + float4 * 100.0F + float5 * 100.0F + float6 * 100.0F + float7 * 100.0F);
					BaseVehicle baseVehicle2 = this.getVehicleById((short)int7);
					if (baseVehicle2 != null) {
						if (baseVehicle2.VehicleID == int7) {
							if (float12 > 0.5F) {
								this.collideVehicles.add(baseVehicle2);
								baseVehicle2.authSimulationHash = int6;
							}

							if (GameServer.bServer) {
								baseVehicle2.authorizationServerUpdate();
							}
						}

						if (baseVehicle2 != null) {
							if (GameClient.bClient && baseVehicle2.netPlayerAuthorization == 1) {
								if (baseVehicle2.authSimulationHash != int6) {
									baseVehicle2.authSimulationTime = System.currentTimeMillis();
									baseVehicle2.authSimulationHash = int6;
								}

								if (System.currentTimeMillis() - baseVehicle2.authSimulationTime > 1000L) {
									VehicleManager.instance.sendCollide(baseVehicle2, player, false);
									baseVehicle2.authSimulationTime = 0L;
								}
							}

							int int10;
							if (!GameClient.bClient || baseVehicle2.netPlayerAuthorization != 0 && baseVehicle2.netPlayerAuthorization != 4) {
								if (this.compareTransform(this.tempTransform, baseVehicle2.getPoly().t)) {
									baseVehicle2.polyDirty = true;
								}

								baseVehicle2.jniTransform.set(this.tempTransform);
								baseVehicle2.jniLinearVelocity.set((Vector3fc)this.tempVector3f);
								baseVehicle2.jniSpeed = float11;
								baseVehicle2.jniIsCollide = float12 > 0.5F;
								for (int10 = 0; int10 < baseVehicle2.getScript().getWheelCount(); ++int10) {
									baseVehicle2.wheelInfo[int10].steering = this.wheelSteer[int10];
									baseVehicle2.wheelInfo[int10].rotation = this.wheelRotation[int10];
									baseVehicle2.wheelInfo[int10].skidInfo = this.wheelSkidInfo[int10];
									baseVehicle2.wheelInfo[int10].suspensionLength = this.wheelSuspensionLength[int10];
								}
							} else {
								for (int10 = 0; int10 < baseVehicle2.getScript().getWheelCount(); ++int10) {
									baseVehicle2.wheelInfo[int10].suspensionLength = this.wheelSuspensionLength[int10];
								}
							}
						}
					}
				}
			}

			if (GameClient.bClient && baseVehicle != null) {
				for (int3 = 0; int3 < this.collideVehicles.size(); ++int3) {
					BaseVehicle baseVehicle3 = (BaseVehicle)this.collideVehicles.get(int3);
					if (baseVehicle3.DistTo(baseVehicle) < 8.0F && baseVehicle3.netPlayerAuthorization == 0) {
						VehicleManager.instance.sendCollide(baseVehicle3, player, true);
						baseVehicle3.authorizationClientForecast(true);
						baseVehicle3.authSimulationTime = System.currentTimeMillis();
					}
				}
			}

			MPStatistic.getInstance().Bullet.Start();
			int3 = Bullet.getObjectPhysics(this.ff);
			MPStatistic.getInstance().Bullet.End();
			int4 = 0;
			for (int5 = 0; int5 < int3; ++int5) {
				int6 = (int)this.ff[int4++];
				float float13 = this.ff[int4++];
				float1 = this.ff[int4++];
				float2 = this.ff[int4++];
				float13 += this.offsetX;
				float2 += this.offsetY;
				IsoMovingObject movingObject = (IsoMovingObject)this.physicsObjectMap.get(int6);
				if (movingObject != null) {
					movingObject.removeFromSquare();
					movingObject.setX(float13 + 0.18F);
					movingObject.setY(float2);
					movingObject.setZ(Math.max(0.0F, float1 / 3.0F / 0.82F));
					movingObject.setCurrent(IsoWorld.instance.getCell().getGridSquare((double)movingObject.getX(), (double)movingObject.getY(), (double)movingObject.getZ()));
				}
			}
		}
	}

	private BaseVehicle getVehicleById(short short1) {
		return VehicleManager.instance.getVehicleByID(short1);
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

	public void createServerCell(ServerMap.ServerCell serverCell) {
		this.create();
		MPStatistic.getInstance().Bullet.Start();
		Bullet.createServerCell(serverCell.WX, serverCell.WY);
		MPStatistic.getInstance().Bullet.End();
	}

	public void removeServerCell(ServerMap.ServerCell serverCell) {
		MPStatistic.getInstance().Bullet.Start();
		Bullet.removeServerCell(serverCell.WX, serverCell.WY);
		MPStatistic.getInstance().Bullet.End();
	}

	public int getOwnVehiclePhysics(int int1, ByteBufferWriter byteBufferWriter) {
		if (Bullet.getOwnVehiclePhysics(int1, this.ff) != 0) {
			return -1;
		} else {
			for (int int2 = 0; int2 < 23; ++int2) {
				byteBufferWriter.bb.putFloat(this.ff[int2]);
			}

			return 1;
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
