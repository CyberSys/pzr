package zombie.iso;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.math.PZMath;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameServer;
import zombie.ui.UIManager;
import zombie.vehicles.BaseVehicle;


public final class PlayerCamera {
	public final int playerIndex;
	public float OffX;
	public float OffY;
	public float TOffX;
	public float TOffY;
	public float lastOffX;
	public float lastOffY;
	public float RightClickTargetX;
	public float RightClickTargetY;
	public float RightClickX;
	public float RightClickY;
	private float RightClickX_f;
	private float RightClickY_f;
	public float DeferedX;
	public float DeferedY;
	public float zoom;
	public int OffscreenWidth;
	public int OffscreenHeight;
	private static final Vector2 offVec = new Vector2();
	private static float PAN_SPEED = 1.0F;
	private long panTime = -1L;
	private final Vector3f m_lastVehicleForwardDirection = new Vector3f();

	public PlayerCamera(int int1) {
		this.playerIndex = int1;
	}

	public void center() {
		float float1 = this.OffX;
		float float2 = this.OffY;
		if (IsoCamera.CamCharacter != null) {
			IsoGameCharacter gameCharacter = IsoCamera.CamCharacter;
			float1 = IsoUtils.XToScreen(gameCharacter.x + this.DeferedX, gameCharacter.y + this.DeferedY, gameCharacter.z, 0);
			float2 = IsoUtils.YToScreen(gameCharacter.x + this.DeferedX, gameCharacter.y + this.DeferedY, gameCharacter.z, 0);
			float1 -= (float)(IsoCamera.getOffscreenWidth(this.playerIndex) / 2);
			float2 -= (float)(IsoCamera.getOffscreenHeight(this.playerIndex) / 2);
			float2 -= gameCharacter.getOffsetY() * 1.5F;
			float1 += (float)IsoCamera.PLAYER_OFFSET_X;
			float2 += (float)IsoCamera.PLAYER_OFFSET_Y;
		}

		this.OffX = this.TOffX = float1;
		this.OffY = this.TOffY = float2;
	}

	public void update() {
		this.center();
		float float1 = (this.TOffX - this.OffX) / 15.0F;
		float float2 = (this.TOffY - this.OffY) / 15.0F;
		this.OffX += float1;
		this.OffY += float2;
		if (this.lastOffX == 0.0F && this.lastOffY == 0.0F) {
			this.lastOffX = this.OffX;
			this.lastOffY = this.OffY;
		}

		long long1 = System.currentTimeMillis();
		PAN_SPEED = 110.0F;
		float float3 = this.panTime < 0L ? 1.0F : (float)(long1 - this.panTime) / 1000.0F * PAN_SPEED;
		float3 = 1.0F / float3;
		this.panTime = long1;
		IsoPlayer player = IsoPlayer.players[this.playerIndex];
		boolean boolean1 = GameWindow.ActivatedJoyPad != null && player != null && player.JoypadBind != -1;
		BaseVehicle baseVehicle = player == null ? null : player.getVehicle();
		if (baseVehicle != null && baseVehicle.getCurrentSpeedKmHour() <= 1.0F) {
			baseVehicle.getForwardVector(this.m_lastVehicleForwardDirection);
		}

		int int1;
		float float4;
		float float5;
		float float6;
		float float7;
		float float8;
		if (Core.getInstance().getOptionPanCameraWhileDriving() && baseVehicle != null && baseVehicle.getCurrentSpeedKmHour() > 1.0F) {
			float float9 = Core.getInstance().getZoom(this.playerIndex);
			float float10 = baseVehicle.getCurrentSpeedKmHour() * BaseVehicle.getFakeSpeedModifier() / 10.0F;
			float10 *= float9;
			Vector3f vector3f = baseVehicle.getForwardVector((Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc());
			float float11 = this.m_lastVehicleForwardDirection.angle(vector3f) * 57.295776F;
			if (float11 > 1.0F) {
				float8 = float11 / 180.0F / (float)PerformanceSettings.getLockFPS();
				float8 = PZMath.max(float8, 0.1F);
				this.m_lastVehicleForwardDirection.lerp(vector3f, float8, vector3f);
				this.m_lastVehicleForwardDirection.set((Vector3fc)vector3f);
			}

			this.RightClickTargetX = (float)((int)IsoUtils.XToScreen(vector3f.x * float10, vector3f.z * float10, player.z, 0));
			this.RightClickTargetY = (float)((int)IsoUtils.YToScreen(vector3f.x * float10, vector3f.z * float10, player.z, 0));
			((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f);
			byte byte1 = 0;
			byte byte2 = 0;
			int1 = IsoCamera.getOffscreenWidth(this.playerIndex);
			int int2 = IsoCamera.getOffscreenHeight(this.playerIndex);
			float4 = (float)byte1 + (float)int1 / 2.0F;
			float5 = (float)byte2 + (float)int2 / 2.0F;
			float6 = 150.0F * float9;
			this.RightClickTargetX = (float)((int)PZMath.clamp(float4 + this.RightClickTargetX, float6, (float)int1 - float6)) - float4;
			this.RightClickTargetY = (float)((int)PZMath.clamp(float5 + this.RightClickTargetY, float6, (float)int2 - float6)) - float5;
			if (Math.abs(float10) < 5.0F) {
				float7 = 1.0F - Math.abs(float10) / 5.0F;
				this.returnToCenter(1.0F / (16.0F * float3 / float7));
			} else {
				float3 /= 0.5F * float9;
				float7 = IsoUtils.XToScreenExact(player.x, player.y, player.z, 0);
				float float12 = IsoUtils.YToScreenExact(player.x, player.y, player.z, 0);
				if (float7 < float6 / 2.0F || float7 > (float)int1 - float6 / 2.0F || float12 < float6 / 2.0F || float12 > (float)int2 - float6 / 2.0F) {
					float3 /= 4.0F;
				}

				this.RightClickX_f = PZMath.step(this.RightClickX_f, this.RightClickTargetX, 1.875F * (float)PZMath.sign(this.RightClickTargetX - this.RightClickX_f) / float3);
				this.RightClickY_f = PZMath.step(this.RightClickY_f, this.RightClickTargetY, 1.875F * (float)PZMath.sign(this.RightClickTargetY - this.RightClickY_f) / float3);
				this.RightClickX = (float)((int)this.RightClickX_f);
				this.RightClickY = (float)((int)this.RightClickY_f);
			}
		} else if (boolean1 && player != null) {
			if ((player.IsAiming() || player.isLookingWhileInVehicle()) && JoypadManager.instance.isRBPressed(player.JoypadBind) && !player.bJoypadIgnoreAimUntilCentered) {
				this.RightClickTargetX = JoypadManager.instance.getAimingAxisX(player.JoypadBind) * 1500.0F;
				this.RightClickTargetY = JoypadManager.instance.getAimingAxisY(player.JoypadBind) * 1500.0F;
				float3 /= 0.5F * Core.getInstance().getZoom(this.playerIndex);
				this.RightClickX_f = PZMath.step(this.RightClickX_f, this.RightClickTargetX, (this.RightClickTargetX - this.RightClickX_f) / (80.0F * float3));
				this.RightClickY_f = PZMath.step(this.RightClickY_f, this.RightClickTargetY, (this.RightClickTargetY - this.RightClickY_f) / (80.0F * float3));
				this.RightClickX = (float)((int)this.RightClickX_f);
				this.RightClickY = (float)((int)this.RightClickY_f);
				player.dirtyRecalcGridStackTime = 2.0F;
			} else {
				this.returnToCenter(1.0F / (16.0F * float3));
			}
		} else {
			int int3;
			if (this.playerIndex == 0 && player != null && !player.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey("PanCamera"))) {
				int int4 = IsoCamera.getScreenWidth(this.playerIndex);
				int int5 = IsoCamera.getScreenHeight(this.playerIndex);
				int int6 = IsoCamera.getScreenLeft(this.playerIndex);
				int3 = IsoCamera.getScreenTop(this.playerIndex);
				float8 = (float)Mouse.getXA() - ((float)int6 + (float)int4 / 2.0F);
				float float13 = (float)Mouse.getYA() - ((float)int3 + (float)int5 / 2.0F);
				float float14;
				if (int4 > int5) {
					float14 = (float)int5 / (float)int4;
					float8 *= float14;
				} else {
					float14 = (float)int4 / (float)int5;
					float13 *= float14;
				}

				float14 *= (float)int4 / 1366.0F;
				offVec.set(float8, float13);
				offVec.setLength(Math.min(offVec.getLength(), (float)Math.min(int4, int5) / 2.0F));
				float8 = offVec.x / float14;
				float13 = offVec.y / float14;
				this.RightClickTargetX = float8 * 2.0F;
				this.RightClickTargetY = float13 * 2.0F;
				float3 /= 0.5F * Core.getInstance().getZoom(this.playerIndex);
				this.RightClickX_f = PZMath.step(this.RightClickX_f, this.RightClickTargetX, (this.RightClickTargetX - this.RightClickX_f) / (80.0F * float3));
				this.RightClickY_f = PZMath.step(this.RightClickY_f, this.RightClickTargetY, (this.RightClickTargetY - this.RightClickY_f) / (80.0F * float3));
				this.RightClickX = (float)((int)this.RightClickX_f);
				this.RightClickY = (float)((int)this.RightClickY_f);
				player.dirtyRecalcGridStackTime = 2.0F;
				IsoSprite.globalOffsetX = -1.0F;
			} else if (this.playerIndex == 0 && Core.getInstance().getOptionPanCameraWhileAiming()) {
				boolean boolean2 = !GameServer.bServer;
				boolean boolean3 = !UIManager.isMouseOverInventory() && player != null && player.isAiming();
				boolean boolean4 = !boolean1 && player != null && !player.isDead();
				if (boolean2 && boolean3 && boolean4) {
					int3 = IsoCamera.getScreenWidth(this.playerIndex);
					int int7 = IsoCamera.getScreenHeight(this.playerIndex);
					int int8 = IsoCamera.getScreenLeft(this.playerIndex);
					int1 = IsoCamera.getScreenTop(this.playerIndex);
					float float15 = (float)Mouse.getXA() - ((float)int8 + (float)int3 / 2.0F);
					float4 = (float)Mouse.getYA() - ((float)int1 + (float)int7 / 2.0F);
					if (int3 > int7) {
						float5 = (float)int7 / (float)int3;
						float15 *= float5;
					} else {
						float5 = (float)int3 / (float)int7;
						float4 *= float5;
					}

					float5 *= (float)int3 / 1366.0F;
					float6 = (float)Math.min(int3, int7) / 6.0F;
					float7 = (float)Math.min(int3, int7) / 2.0F - float6;
					offVec.set(float15, float4);
					if (offVec.getLength() < float7) {
						float4 = 0.0F;
						float15 = 0.0F;
					} else {
						offVec.setLength(Math.min(offVec.getLength(), (float)Math.min(int3, int7) / 2.0F) - float7);
						float15 = offVec.x / float5;
						float4 = offVec.y / float5;
					}

					this.RightClickTargetX = float15 * 7.0F;
					this.RightClickTargetY = float4 * 7.0F;
					float3 /= 0.5F * Core.getInstance().getZoom(this.playerIndex);
					this.RightClickX_f = PZMath.step(this.RightClickX_f, this.RightClickTargetX, (this.RightClickTargetX - this.RightClickX_f) / (80.0F * float3));
					this.RightClickY_f = PZMath.step(this.RightClickY_f, this.RightClickTargetY, (this.RightClickTargetY - this.RightClickY_f) / (80.0F * float3));
					this.RightClickX = (float)((int)this.RightClickX_f);
					this.RightClickY = (float)((int)this.RightClickY_f);
					player.dirtyRecalcGridStackTime = 2.0F;
				} else {
					this.returnToCenter(1.0F / (16.0F * float3));
				}

				IsoSprite.globalOffsetX = -1.0F;
			} else {
				this.returnToCenter(1.0F / (16.0F * float3));
			}
		}

		this.zoom = Core.getInstance().getZoom(this.playerIndex);
	}

	private void returnToCenter(float float1) {
		this.RightClickTargetX = 0.0F;
		this.RightClickTargetY = 0.0F;
		if (this.RightClickTargetX != this.RightClickX || this.RightClickTargetY != this.RightClickY) {
			this.RightClickX_f = PZMath.step(this.RightClickX_f, this.RightClickTargetX, (this.RightClickTargetX - this.RightClickX_f) * float1);
			this.RightClickY_f = PZMath.step(this.RightClickY_f, this.RightClickTargetY, (this.RightClickTargetY - this.RightClickY_f) * float1);
			this.RightClickX = (float)((int)this.RightClickX_f);
			this.RightClickY = (float)((int)this.RightClickY_f);
			if (Math.abs(this.RightClickTargetX - this.RightClickX_f) < 0.001F) {
				this.RightClickX = (float)((int)this.RightClickTargetX);
				this.RightClickX_f = this.RightClickX;
			}

			if (Math.abs(this.RightClickTargetY - this.RightClickY_f) < 0.001F) {
				this.RightClickY = (float)((int)this.RightClickTargetY);
				this.RightClickY_f = this.RightClickY;
			}

			IsoPlayer player = IsoPlayer.players[this.playerIndex];
			player.dirtyRecalcGridStackTime = 2.0F;
		}
	}

	public float getOffX() {
		return (float)((int)(this.OffX + this.RightClickX));
	}

	public float getOffY() {
		return (float)((int)(this.OffY + this.RightClickY));
	}

	public float getTOffX() {
		float float1 = this.TOffX - this.OffX;
		return (float)((int)(this.OffX + this.RightClickX - float1));
	}

	public float getTOffY() {
		float float1 = this.TOffY - this.OffY;
		return (float)((int)(this.OffY + this.RightClickY - float1));
	}

	public float getLastOffX() {
		return (float)((int)(this.lastOffX + this.RightClickX));
	}

	public float getLastOffY() {
		return (float)((int)(this.lastOffY + this.RightClickY));
	}

	public float XToIso(float float1, float float2, float float3) {
		float1 = (float)((int)float1);
		float2 = (float)((int)float2);
		float float4 = float1 + this.getOffX();
		float float5 = float2 + this.getOffY();
		float float6 = (float4 + 2.0F * float5) / (64.0F * (float)Core.TileScale);
		float6 += 3.0F * float3;
		return float6;
	}

	public float YToIso(float float1, float float2, float float3) {
		float1 = (float)((int)float1);
		float2 = (float)((int)float2);
		float float4 = float1 + this.getOffX();
		float float5 = float2 + this.getOffY();
		float float6 = (float4 - 2.0F * float5) / (-64.0F * (float)Core.TileScale);
		float6 += 3.0F * float3;
		return float6;
	}

	public float YToScreenExact(float float1, float float2, float float3, int int1) {
		float float4 = IsoUtils.YToScreen(float1, float2, float3, int1);
		float4 -= this.getOffY();
		return float4;
	}

	public float XToScreenExact(float float1, float float2, float float3, int int1) {
		float float4 = IsoUtils.XToScreen(float1, float2, float3, int1);
		float4 -= this.getOffX();
		return float4;
	}

	public void copyFrom(PlayerCamera playerCamera) {
		this.OffX = playerCamera.OffX;
		this.OffY = playerCamera.OffY;
		this.TOffX = playerCamera.TOffX;
		this.TOffY = playerCamera.TOffY;
		this.lastOffX = playerCamera.lastOffX;
		this.lastOffY = playerCamera.lastOffY;
		this.RightClickTargetX = playerCamera.RightClickTargetX;
		this.RightClickTargetY = playerCamera.RightClickTargetY;
		this.RightClickX = playerCamera.RightClickX;
		this.RightClickY = playerCamera.RightClickY;
		this.DeferedX = playerCamera.DeferedX;
		this.DeferedY = playerCamera.DeferedY;
		this.zoom = playerCamera.zoom;
		this.OffscreenWidth = playerCamera.OffscreenWidth;
		this.OffscreenHeight = playerCamera.OffscreenHeight;
	}

	public void initFromIsoCamera(int int1) {
		this.copyFrom(IsoCamera.cameras[int1]);
		this.zoom = Core.getInstance().getZoom(int1);
		this.OffscreenWidth = IsoCamera.getOffscreenWidth(int1);
		this.OffscreenHeight = IsoCamera.getOffscreenHeight(int1);
	}
}
