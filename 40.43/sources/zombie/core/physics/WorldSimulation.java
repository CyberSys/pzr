package zombie.core.physics;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;


public class WorldSimulation {
	public static WorldSimulation instance = new WorldSimulation();
	public static final boolean LEVEL_ZERO_ONLY = true;
	public float offsetX = 0.0F;
	public float offsetY = 0.0F;
	public int maxSubSteps;
	public boolean created = false;
	public long time;
	public HashMap physicsObjectMap = new HashMap();
	static final boolean DEBUG = false;
	BufferedWriter DebugInDataWriter;
	BufferedWriter DebugOutDataWriter;
	private float[] ff = new float[8192];
	private float[] wheelSteer = new float[4];
	private float[] wheelRotation = new float[4];
	private float[] wheelSkidInfo = new float[4];
	private float[] wheelSuspensionLength = new float[4];
	private final float[] tempFloats = new float[23];
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

	private void updatePhysic() {
		Bullet.stepSimulation(GameTime.instance.getRealworldSecondsSinceLastUpdate(), GameServer.bServer ? 5 : 2, 0.016666668F);
		if (GameTime.instance.getRealworldSecondsSinceLastUpdate() < 0.01F) {
			this.time = GameTime.getServerTime();
		} else {
			this.time += (long)(1.0E9F * GameTime.instance.getRealworldSecondsSinceLastUpdate());
		}
	}

	public void update() {
		if (this.created) {
			if (GameServer.bServer) {
				if (GameTime.instance.getRealworldSecondsSinceLastUpdate() > 0.01F) {
					if ((float)(GameTime.getServerTime() - this.time) > 5.0E9F * GameTime.instance.getRealworldSecondsSinceLastUpdate()) {
						this.time = GameTime.getServerTime();
					}

					while ((float)(GameTime.getServerTime() - this.time) > 1.0E9F * GameTime.instance.getRealworldSecondsSinceLastUpdate()) {
						this.updatePhysic();
					}
				}
			} else {
				this.updatePhysic();
			}

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
				int3 = Bullet.getVehiclePhysics(int2, this.ff);
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
								for (int10 = 0; int10 < 4; ++int10) {
									baseVehicle2.wheelInfo[int10].steering = this.wheelSteer[int10];
									baseVehicle2.wheelInfo[int10].rotation = this.wheelRotation[int10];
									baseVehicle2.wheelInfo[int10].skidInfo = this.wheelSkidInfo[int10];
									baseVehicle2.wheelInfo[int10].suspensionLength = this.wheelSuspensionLength[int10];
								}
							} else {
								for (int10 = 0; int10 < 4; ++int10) {
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

			int3 = Bullet.getObjectPhysics(this.ff);
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
		if (!GameServer.bServer && !GameClient.bClient) {
			for (int int1 = 0; int1 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++int1) {
				BaseVehicle baseVehicle = (BaseVehicle)IsoWorld.instance.CurrentCell.getVehicles().get(int1);
				if (baseVehicle.VehicleID == short1) {
					return baseVehicle;
				}
			}

			return null;
		} else {
			return VehicleManager.instance.getVehicleByID(short1);
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

	public void createServerCell(ServerMap.ServerCell serverCell) {
		this.create();
		Bullet.createServerCell(serverCell.WX, serverCell.WY);
	}

	public void removeServerCell(ServerMap.ServerCell serverCell) {
		Bullet.removeServerCell(serverCell.WX, serverCell.WY);
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
		if (WorldSimulation.Drawer3.instance[int1] == null) {
			WorldSimulation.Drawer3.instance[int1] = new WorldSimulation.Drawer3(int1);
		}

		WorldSimulation.Drawer3.instance[int1].init();
		return WorldSimulation.Drawer3.instance[int1];
	}

	public static class Drawer3 extends TextureDraw.GenericDrawer {
		public static WorldSimulation.Drawer3[] instance = new WorldSimulation.Drawer3[4];
		private float camOffX;
		private float camOffY;
		private int drawOffsetX;
		private int drawOffsetY;
		private int playerIndex;
		private float playerX;
		private float playerY;
		private float playerZ;

		public Drawer3(int int1) {
			this.playerIndex = int1;
		}

		public void init() {
			this.camOffX = IsoCamera.RightClickX[IsoPlayer.getPlayerIndex()] + (float)IsoCamera.PLAYER_OFFSET_X;
			this.camOffY = IsoCamera.RightClickY[IsoPlayer.getPlayerIndex()] + (float)IsoCamera.PLAYER_OFFSET_Y;
			this.camOffX += this.XToScreenExact(IsoPlayer.instance.x - (float)((int)IsoPlayer.instance.x), IsoPlayer.instance.y - (float)((int)IsoPlayer.instance.y), 0.0F, 0);
			this.camOffY += this.YToScreenExact(IsoPlayer.instance.x - (float)((int)IsoPlayer.instance.x), IsoPlayer.instance.y - (float)((int)IsoPlayer.instance.y), 0.0F, 0);
			this.drawOffsetX = (int)IsoPlayer.instance.x;
			this.drawOffsetY = (int)IsoPlayer.instance.y;
			this.playerX = IsoPlayer.instance.x;
			this.playerY = IsoPlayer.instance.y;
			this.playerZ = IsoPlayer.instance.z;
		}

		public void render() {
			GL11.glPushAttrib(1048575);
			GL11.glDisable(3553);
			GL11.glDisable(3042);
			GL11.glMatrixMode(5889);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0, (double)Core.getInstance().getOffscreenWidth(this.playerIndex), (double)Core.getInstance().getOffscreenHeight(this.playerIndex), 0.0, 10000.0, -10000.0);
			GL11.glMatrixMode(5888);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			int int1 = -this.drawOffsetX;
			int int2 = -this.drawOffsetY;
			byte byte1 = 0;
			byte byte2 = 0;
			float float1 = 0.0F;
			float float2 = 0.0F;
			GL11.glTranslatef((float)(Core.getInstance().getOffscreenWidth(this.playerIndex) / 2), (float)(Core.getInstance().getOffscreenHeight(this.playerIndex) / 2), 0.0F);
			float1 = this.XToScreenExact((float)byte1, (float)byte2, this.playerZ, 0);
			float2 = this.YToScreenExact((float)byte1, (float)byte2, this.playerZ, 0);
			float1 += this.camOffX;
			float2 += this.camOffY;
			GL11.glTranslatef(-float1, -float2, 0.0F);
			int1 = (int)((float)int1 + WorldSimulation.instance.offsetX);
			int2 = (int)((float)int2 + WorldSimulation.instance.offsetY);
			int int3 = 32 * Core.TileScale;
			float float3 = (float)Math.sqrt((double)(int3 * int3 + int3 * int3));
			GL11.glScalef(float3, float3, float3);
			GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
			Bullet.debugDrawWorld(int1, int2);
			GL11.glBegin(1);
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			GL11.glVertex3d(0.0, 0.0, 0.0);
			GL11.glVertex3d(1.0, 0.0, 0.0);
			GL11.glVertex3d(0.0, 0.0, 0.0);
			GL11.glVertex3d(0.0, 1.0, 0.0);
			GL11.glVertex3d(0.0, 0.0, 0.0);
			GL11.glVertex3d(0.0, 0.0, 1.0);
			GL11.glEnd();
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			GL11.glMatrixMode(5889);
			GL11.glPopMatrix();
			GL11.glMatrixMode(5888);
			GL11.glPopMatrix();
			GL11.glEnable(3042);
			GL11.glEnable(3553);
			GL11.glPopAttrib();
		}

		public float YToScreenExact(float float1, float float2, float float3, int int1) {
			float float4 = IsoUtils.YToScreen(float1, float2, float3, int1);
			return float4;
		}

		public float XToScreenExact(float float1, float float2, float float3, int int1) {
			float float4 = IsoUtils.XToScreen(float1, float2, float3, int1);
			return float4;
		}
	}
}
