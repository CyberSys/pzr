package zombie.ui;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import zombie.IndieGL;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LotHeader;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.popman.ZombiePopulationManager;


public final class RadarPanel extends UIElement {
	private int playerIndex;
	private float xPos;
	private float yPos;
	private float offx;
	private float offy;
	private float zoom;
	private float draww;
	private float drawh;
	private Texture mask;
	private Texture border;
	private ArrayList zombiePos = new ArrayList();
	private RadarPanel.ZombiePosPool zombiePosPool = new RadarPanel.ZombiePosPool();
	private int zombiePosFrameCount;
	private boolean[] zombiePosOccupied = new boolean[360];

	public RadarPanel(int int1) {
		this.setX((double)(IsoCamera.getScreenLeft(int1) + 20));
		this.setY((double)(IsoCamera.getScreenTop(int1) + IsoCamera.getScreenHeight(int1) - 120 - 20));
		this.setWidth(120.0);
		this.setHeight(120.0);
		this.mask = Texture.getSharedTexture("media/ui/RadarMask.png");
		this.border = Texture.getSharedTexture("media/ui/RadarBorder.png");
		this.playerIndex = int1;
	}

	public void update() {
		byte byte1 = 0;
		if (IsoPlayer.players[this.playerIndex] != null && IsoPlayer.players[this.playerIndex].getJoypadBind() != -1) {
			byte1 = -72;
		}

		this.setX((double)(IsoCamera.getScreenLeft(this.playerIndex) + 20));
		this.setY((double)(IsoCamera.getScreenTop(this.playerIndex) + IsoCamera.getScreenHeight(this.playerIndex)) - this.getHeight() - 20.0 + (double)byte1);
	}

	public void render() {
		if (this.isVisible()) {
			if (IsoPlayer.players[this.playerIndex] != null) {
				if (!GameClient.bClient) {
					this.draww = (float)this.getWidth().intValue();
					this.drawh = (float)this.getHeight().intValue();
					this.xPos = IsoPlayer.players[this.playerIndex].getX();
					this.yPos = IsoPlayer.players[this.playerIndex].getY();
					this.offx = (float)this.getAbsoluteX().intValue();
					this.offy = (float)this.getAbsoluteY().intValue();
					this.zoom = 3.0F;
					this.stencilOn();
					SpriteRenderer.instance.render((Texture)null, this.offx, this.offy, (float)this.getWidth().intValue(), this.drawh, 0.0F, 0.2F, 0.0F, 0.66F, (Consumer)null);
					this.renderBuildings();
					this.renderRect(this.xPos - 0.5F, this.yPos - 0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
					this.stencilOff();
					this.renderZombies();
					SpriteRenderer.instance.render(this.border, this.offx - 4.0F, this.offy - 4.0F, this.draww + 8.0F, this.drawh + 8.0F, 1.0F, 1.0F, 1.0F, 0.25F, (Consumer)null);
				}
			}
		}
	}

	private void stencilOn() {
		IndieGL.glStencilMask(255);
		IndieGL.glClear(1280);
		IndieGL.enableStencilTest();
		IndieGL.glStencilFunc(519, 128, 255);
		IndieGL.glStencilOp(7680, 7680, 7681);
		IndieGL.enableAlphaTest();
		IndieGL.glAlphaFunc(516, 0.1F);
		IndieGL.glColorMask(false, false, false, false);
		SpriteRenderer.instance.renderi(this.mask, (int)this.x, (int)this.y, (int)this.width, (int)this.height, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
		IndieGL.glColorMask(true, true, true, true);
		IndieGL.glAlphaFunc(516, 0.0F);
		IndieGL.glStencilFunc(514, 128, 128);
		IndieGL.glStencilOp(7680, 7680, 7680);
	}

	private void stencilOff() {
		IndieGL.glAlphaFunc(519, 0.0F);
		IndieGL.disableStencilTest();
		IndieGL.disableAlphaTest();
		IndieGL.glStencilFunc(519, 255, 255);
		IndieGL.glStencilOp(7680, 7680, 7680);
		IndieGL.glClear(1280);
	}

	private void renderBuildings() {
		IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
		IsoMetaCell[][] metaCellArrayArray = metaGrid.Grid;
		int int1 = (int)((this.xPos - 100.0F) / 300.0F) - metaGrid.minX;
		int int2 = (int)((this.yPos - 100.0F) / 300.0F) - metaGrid.minY;
		int int3 = (int)((this.xPos + 100.0F) / 300.0F) - metaGrid.minX;
		int int4 = (int)((this.yPos + 100.0F) / 300.0F) - metaGrid.minY;
		int1 = Math.max(int1, 0);
		int2 = Math.max(int2, 0);
		int3 = Math.min(int3, metaCellArrayArray.length - 1);
		int4 = Math.min(int4, metaCellArrayArray[0].length - 1);
		for (int int5 = int1; int5 <= int3; ++int5) {
			for (int int6 = int2; int6 <= int4; ++int6) {
				LotHeader lotHeader = metaCellArrayArray[int5][int6].info;
				if (lotHeader != null) {
					for (int int7 = 0; int7 < lotHeader.Buildings.size(); ++int7) {
						BuildingDef buildingDef = (BuildingDef)lotHeader.Buildings.get(int7);
						for (int int8 = 0; int8 < buildingDef.rooms.size(); ++int8) {
							if (((RoomDef)buildingDef.rooms.get(int8)).level <= 0) {
								ArrayList arrayList = ((RoomDef)buildingDef.rooms.get(int8)).getRects();
								for (int int9 = 0; int9 < arrayList.size(); ++int9) {
									RoomDef.RoomRect roomRect = (RoomDef.RoomRect)arrayList.get(int9);
									this.renderRect((float)roomRect.getX(), (float)roomRect.getY(), (float)roomRect.getW(), (float)roomRect.getH(), 0.5F, 0.5F, 0.8F, 0.3F);
								}
							}
						}
					}
				}
			}
		}
	}

	private void renderZombies() {
		float float1 = this.offx + this.draww / 2.0F;
		float float2 = this.offy + this.drawh / 2.0F;
		float float3 = this.draww / 2.0F;
		float float4 = 0.5F * this.zoom;
		int int1;
		if (++this.zombiePosFrameCount >= PerformanceSettings.getLockFPS() / 5) {
			this.zombiePosFrameCount = 0;
			this.zombiePosPool.release(this.zombiePos);
			this.zombiePos.clear();
			Arrays.fill(this.zombiePosOccupied, false);
			ArrayList arrayList = IsoWorld.instance.CurrentCell.getZombieList();
			float float5;
			float float6;
			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				IsoZombie zombie = (IsoZombie)arrayList.get(int1);
				float float7 = this.worldToScreenX(zombie.getX());
				float5 = this.worldToScreenY(zombie.getY());
				float6 = IsoUtils.DistanceToSquared(float1, float2, float7, float5);
				if (float6 > float3 * float3) {
					double double1 = Math.atan2((double)(float5 - float2), (double)(float7 - float1)) + 3.141592653589793;
					double double2 = (Math.toDegrees(double1) + 180.0) % 360.0;
					this.zombiePosOccupied[(int)double2] = true;
				} else {
					this.zombiePos.add(this.zombiePosPool.alloc(zombie.x, zombie.y));
				}
			}

			if (Core.bLastStand) {
				if (ZombiePopulationManager.instance.radarXY == null) {
					ZombiePopulationManager.instance.radarXY = new float[2048];
				}

				float[] floatArray = ZombiePopulationManager.instance.radarXY;
				synchronized (floatArray) {
					for (int int2 = 0; int2 < ZombiePopulationManager.instance.radarCount; ++int2) {
						float5 = floatArray[int2 * 2 + 0];
						float6 = floatArray[int2 * 2 + 1];
						float float8 = this.worldToScreenX(float5);
						float float9 = this.worldToScreenY(float6);
						float float10 = IsoUtils.DistanceToSquared(float1, float2, float8, float9);
						if (float10 > float3 * float3) {
							double double3 = Math.atan2((double)(float9 - float2), (double)(float8 - float1)) + 3.141592653589793;
							double double4 = (Math.toDegrees(double3) + 180.0) % 360.0;
							this.zombiePosOccupied[(int)double4] = true;
						} else {
							this.zombiePos.add(this.zombiePosPool.alloc(float5, float6));
						}
					}

					ZombiePopulationManager.instance.radarRenderFlag = true;
				}
			}
		}

		int int3 = this.zombiePos.size();
		for (int1 = 0; int1 < int3; ++int1) {
			RadarPanel.ZombiePos zombiePos = (RadarPanel.ZombiePos)this.zombiePos.get(int1);
			this.renderRect(zombiePos.x - 0.5F, zombiePos.y - 0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
		}

		for (int1 = 0; int1 < this.zombiePosOccupied.length; ++int1) {
			if (this.zombiePosOccupied[int1]) {
				double double5 = Math.toRadians((double)((float)int1 / (float)this.zombiePosOccupied.length * 360.0F));
				SpriteRenderer.instance.render((Texture)null, float1 + (float3 + 1.0F) * (float)Math.cos(double5) - float4, float2 + (float3 + 1.0F) * (float)Math.sin(double5) - float4, 1.0F * this.zoom, 1.0F * this.zoom, 1.0F, 1.0F, 0.0F, 1.0F, (Consumer)null);
			}
		}
	}

	private float worldToScreenX(float float1) {
		float1 -= this.xPos;
		float1 *= this.zoom;
		float1 += this.offx;
		float1 += this.draww / 2.0F;
		return float1;
	}

	private float worldToScreenY(float float1) {
		float1 -= this.yPos;
		float1 *= this.zoom;
		float1 += this.offy;
		float1 += this.drawh / 2.0F;
		return float1;
	}

	private void renderRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = this.worldToScreenX(float1);
		float float10 = this.worldToScreenY(float2);
		float float11 = this.worldToScreenX(float1 + float3);
		float float12 = this.worldToScreenY(float2 + float4);
		float3 = float11 - float9;
		float4 = float12 - float10;
		if (!(float9 >= this.offx + this.draww) && !(float11 < this.offx) && !(float10 >= this.offy + this.drawh) && !(float12 < this.offy)) {
			SpriteRenderer.instance.render((Texture)null, float9, float10, float3, float4, float5, float6, float7, float8, (Consumer)null);
		}
	}

	private static class ZombiePosPool {
		private ArrayDeque pool = new ArrayDeque();

		public RadarPanel.ZombiePos alloc(float float1, float float2) {
			return this.pool.isEmpty() ? new RadarPanel.ZombiePos(float1, float2) : ((RadarPanel.ZombiePos)this.pool.pop()).set(float1, float2);
		}

		public void release(Collection collection) {
			this.pool.addAll(collection);
		}
	}

	private static final class ZombiePos {
		public float x;
		public float y;

		public ZombiePos(float float1, float float2) {
			this.x = float1;
			this.y = float2;
		}

		public RadarPanel.ZombiePos set(float float1, float float2) {
			this.x = float1;
			this.y = float2;
			return this;
		}
	}
}
