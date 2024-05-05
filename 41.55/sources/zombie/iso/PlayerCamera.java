package zombie.iso;

import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameServer;
import zombie.ui.UIManager;


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
		if (player == null) {
			Object object = null;
		} else {
			player.getVehicle();
		}

		if (boolean1 && player != null) {
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
			int int1;
			if (this.playerIndex == 0 && player != null && !player.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey("PanCamera"))) {
				int int2 = IsoCamera.getScreenWidth(this.playerIndex);
				int int3 = IsoCamera.getScreenHeight(this.playerIndex);
				int int4 = IsoCamera.getScreenLeft(this.playerIndex);
				int1 = IsoCamera.getScreenTop(this.playerIndex);
				float float4 = (float)Mouse.getXA() - ((float)int4 + (float)int2 / 2.0F);
				float float5 = (float)Mouse.getYA() - ((float)int1 + (float)int3 / 2.0F);
				float float6;
				if (int2 > int3) {
					float6 = (float)int3 / (float)int2;
					float4 *= float6;
				} else {
					float6 = (float)int2 / (float)int3;
					float5 *= float6;
				}

				float6 *= (float)int2 / 1366.0F;
				offVec.set(float4, float5);
				offVec.setLength(Math.min(offVec.getLength(), (float)Math.min(int2, int3) / 2.0F));
				float4 = offVec.x / float6;
				float5 = offVec.y / float6;
				this.RightClickTargetX = float4 * 2.0F;
				this.RightClickTargetY = float5 * 2.0F;
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
					int1 = IsoCamera.getScreenWidth(this.playerIndex);
					int int5 = IsoCamera.getScreenHeight(this.playerIndex);
					int int6 = IsoCamera.getScreenLeft(this.playerIndex);
					int int7 = IsoCamera.getScreenTop(this.playerIndex);
					float float7 = (float)Mouse.getXA() - ((float)int6 + (float)int1 / 2.0F);
					float float8 = (float)Mouse.getYA() - ((float)int7 + (float)int5 / 2.0F);
					float float9;
					if (int1 > int5) {
						float9 = (float)int5 / (float)int1;
						float7 *= float9;
					} else {
						float9 = (float)int1 / (float)int5;
						float8 *= float9;
					}

					float9 *= (float)int1 / 1366.0F;
					float float10 = (float)Math.min(int1, int5) / 6.0F;
					float float11 = (float)Math.min(int1, int5) / 2.0F - float10;
					offVec.set(float7, float8);
					if (offVec.getLength() < float11) {
						float8 = 0.0F;
						float7 = 0.0F;
					} else {
						offVec.setLength(Math.min(offVec.getLength(), (float)Math.min(int1, int5) / 2.0F) - float11);
						float7 = offVec.x / float9;
						float8 = offVec.y / float9;
					}

					this.RightClickTargetX = float7 * 7.0F;
					this.RightClickTargetY = float8 * 7.0F;
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
