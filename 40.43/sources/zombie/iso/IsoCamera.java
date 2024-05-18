package zombie.iso;

import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.iso.areas.IsoRoom;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.MoodlesUI;
import zombie.ui.UIManager;
import zombie.vehicles.BaseVehicle;


public class IsoCamera {
	public static float[] OffX = new float[4];
	public static float[] OffY = new float[4];
	public static float[] TOffX = new float[4];
	public static float[] TOffY = new float[4];
	public static float[] lastOffX = new float[4];
	public static float[] lastOffY = new float[4];
	public static float[] RightClickTargetX = new float[4];
	public static float[] RightClickTargetY = new float[4];
	public static float[] RightClickX = new float[4];
	public static float[] RightClickY = new float[4];
	public static float[] DeferedX = new float[4];
	public static float[] DeferedY = new float[4];
	public static float DeferedSX = 0.0F;
	public static float DeferedSY = 0.0F;
	public static float WorldZoom = 1.0F;
	public static IsoGameCharacter CamCharacter = null;
	public static Vector2 FakePos = new Vector2();
	public static Vector2 FakePosVec = new Vector2();
	public static int TargetTileX = 0;
	public static int TargetTileY = 0;
	public static int PLAYER_OFFSET_X = 0;
	public static int PLAYER_OFFSET_Y;
	public static Vector2 offVec;
	public static final IsoCamera.FrameState frameState;

	public static void init() {
		PLAYER_OFFSET_Y = -56 / (2 / Core.TileScale);
	}

	public static void update() {
		float float1 = OffX[IsoPlayer.getPlayerIndex()];
		float float2 = OffY[IsoPlayer.getPlayerIndex()];
		if (CamCharacter != null) {
			IsoGameCharacter gameCharacter = CamCharacter;
			float1 = IsoUtils.XToScreen(gameCharacter.x + DeferedX[IsoPlayer.getPlayerIndex()], gameCharacter.y + DeferedY[IsoPlayer.getPlayerIndex()], gameCharacter.z, 0);
			float2 = IsoUtils.YToScreen(gameCharacter.x + DeferedX[IsoPlayer.getPlayerIndex()], gameCharacter.y + DeferedY[IsoPlayer.getPlayerIndex()], gameCharacter.z, 0);
			float1 -= (float)(getOffscreenWidth(IsoPlayer.getPlayerIndex()) / 2);
			float2 -= (float)(getOffscreenHeight(IsoPlayer.getPlayerIndex()) / 2);
			float2 -= gameCharacter.getOffsetY() * 1.5F;
			float1 = (float)((int)float1);
			float2 = (float)((int)float2);
			float1 += (float)PLAYER_OFFSET_X;
			float2 += (float)PLAYER_OFFSET_Y;
		}

		TOffX[IsoPlayer.getPlayerIndex()] = float1;
		TOffY[IsoPlayer.getPlayerIndex()] = float2;
		float float3 = (TOffX[IsoPlayer.getPlayerIndex()] - OffX[IsoPlayer.getPlayerIndex()]) / 5.0F;
		float float4 = (TOffY[IsoPlayer.getPlayerIndex()] - OffY[IsoPlayer.getPlayerIndex()]) / 5.0F;
		float[] floatArray = OffX;
		int int1 = IsoPlayer.getPlayerIndex();
		floatArray[int1] += float3;
		floatArray = OffY;
		int1 = IsoPlayer.getPlayerIndex();
		floatArray[int1] += float4;
		if (lastOffX[IsoPlayer.getPlayerIndex()] == 0.0F && lastOffY[IsoPlayer.getPlayerIndex()] == 0.0F) {
			lastOffX[IsoPlayer.getPlayerIndex()] = OffX[IsoPlayer.getPlayerIndex()];
			lastOffY[IsoPlayer.getPlayerIndex()] = OffY[IsoPlayer.getPlayerIndex()];
		}

		float float5 = (float)PerformanceSettings.LockFPS / 60.0F;
		floatArray = RightClickX;
		int1 = IsoPlayer.getPlayerIndex();
		floatArray[int1] += (RightClickTargetX[IsoPlayer.getPlayerIndex()] - RightClickX[IsoPlayer.getPlayerIndex()]) / (80.0F * float5);
		floatArray = RightClickY;
		int1 = IsoPlayer.getPlayerIndex();
		floatArray[int1] += (RightClickTargetY[IsoPlayer.getPlayerIndex()] - RightClickY[IsoPlayer.getPlayerIndex()]) / (80.0F * float5);
		int int2 = Core.getInstance().getKey("Aim");
		boolean boolean1 = GameKeyboard.isKeyDown(int2) || GameKeyboard.wasKeyDown(int2);
		boolean boolean2 = int2 == 29 || int2 == 157;
		boolean boolean3 = GameWindow.ActivatedJoyPad != null && IsoPlayer.instance != null && IsoPlayer.instance.JoypadBind != -1;
		float float6;
		float float7;
		if (IsoPlayer.instance != null && IsoPlayer.instance.getVehicle() != null) {
			IsoPlayer player = IsoPlayer.instance;
			BaseVehicle baseVehicle = player.getVehicle();
			float6 = baseVehicle.getCurrentSpeedKmHour() * BaseVehicle.getFakeSpeedModifier() / 10.0F;
			RightClickTargetX[IsoPlayer.getPlayerIndex()] = IsoUtils.XToScreen(player.angle.x * float6, player.angle.y * float6, player.z, 0);
			RightClickTargetY[IsoPlayer.getPlayerIndex()] = IsoUtils.YToScreen(player.angle.x * float6, player.angle.y * float6, player.z, 0);
			if (Math.abs(float6) < 5.0F) {
				float7 = 1.0F - Math.abs(float6) / 5.0F;
				RightClickTargetX[IsoPlayer.getPlayerIndex()] = 0.0F;
				RightClickTargetY[IsoPlayer.getPlayerIndex()] = 0.0F;
				floatArray = RightClickX;
				int1 = IsoPlayer.getPlayerIndex();
				floatArray[int1] += (RightClickTargetX[IsoPlayer.getPlayerIndex()] - RightClickX[IsoPlayer.getPlayerIndex()]) / (32.0F / float7 * float5);
				floatArray = RightClickY;
				int1 = IsoPlayer.getPlayerIndex();
				floatArray[int1] += (RightClickTargetY[IsoPlayer.getPlayerIndex()] - RightClickY[IsoPlayer.getPlayerIndex()]) / (32.0F / float7 * float5);
			}
		} else {
			float float8;
			if (boolean3 && IsoPlayer.instance.IsAiming() && JoypadManager.instance.isRBPressed(IsoPlayer.instance.JoypadBind)) {
				float8 = Core.getInstance().getZoom(IsoPlayer.instance.getPlayerNum());
				RightClickTargetX[IsoPlayer.getPlayerIndex()] = JoypadManager.instance.getAimingAxisX(IsoPlayer.instance.JoypadBind) * 500.0F * float8;
				RightClickTargetY[IsoPlayer.getPlayerIndex()] = JoypadManager.instance.getAimingAxisY(IsoPlayer.instance.JoypadBind) * 500.0F * float8;
				IsoPlayer.getInstance().dirtyRecalcGridStackTime = 2.0F;
			} else if (GameServer.bServer || !Core.OptionPanCameraWhileAiming || boolean3 || IsoPlayer.instance == null || IsoPlayer.instance.isDead() || boolean2 && boolean1 && UIManager.isMouseOverInventory() || !IsoPlayer.instance.isAiming() && (!IsoPlayer.instance.bRightClickMove || IsoPlayer.instance.JustMoved)) {
				RightClickTargetX[IsoPlayer.getPlayerIndex()] = 0.0F;
				RightClickTargetY[IsoPlayer.getPlayerIndex()] = 0.0F;
				if (RightClickTargetX[IsoPlayer.getPlayerIndex()] != RightClickX[IsoPlayer.getPlayerIndex()] || RightClickTargetY[IsoPlayer.getPlayerIndex()] != RightClickY[IsoPlayer.getPlayerIndex()]) {
					IsoPlayer.getInstance().dirtyRecalcGridStackTime = 2.0F;
				}

				floatArray = RightClickX;
				int1 = IsoPlayer.getPlayerIndex();
				floatArray[int1] += (RightClickTargetX[IsoPlayer.getPlayerIndex()] - RightClickX[IsoPlayer.getPlayerIndex()]) / (16.0F * float5);
				if (Math.abs(RightClickX[IsoPlayer.getPlayerIndex()]) < 0.01F) {
					RightClickX[IsoPlayer.getPlayerIndex()] = 0.0F;
				}

				floatArray = RightClickY;
				int1 = IsoPlayer.getPlayerIndex();
				floatArray[int1] += (RightClickTargetY[IsoPlayer.getPlayerIndex()] - RightClickY[IsoPlayer.getPlayerIndex()]) / (16.0F * float5);
				if (Math.abs(RightClickY[IsoPlayer.getPlayerIndex()]) < 0.01F) {
					RightClickY[IsoPlayer.getPlayerIndex()] = 0.0F;
				}
			} else {
				float8 = (float)Mouse.getX();
				float float9 = (float)Mouse.getY();
				float6 = IsoUtils.XToIsoTrue(float8, float9, (int)CamCharacter.getZ());
				float7 = IsoUtils.YToIsoTrue(float8, float9, (int)CamCharacter.getZ());
				float6 = (float)((double)float6 - 10.05);
				float7 = (float)((double)float7 - 10.05);
				--float6;
				float7 -= 0.3F;
				float6 -= 0.3F;
				float float10 = float6 - CamCharacter.x;
				float float11 = float7 - CamCharacter.y;
				float10 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
				float11 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
				offVec.x = float10;
				offVec.y = float11;
				if (offVec.getLength() < 7.0F) {
					offVec.setLength(0.0F);
				} else {
					offVec.setLength(offVec.getLength() - 7.0F);
				}

				if (offVec.getLength() > 70.0F) {
					offVec.setLength(70.0F);
				}

				float float12 = IsoUtils.XToScreen(offVec.x + CamCharacter.x, offVec.y + CamCharacter.y, (float)((int)CamCharacter.getZ()), 0);
				float float13 = IsoUtils.YToScreen(offVec.x + CamCharacter.x, offVec.y + CamCharacter.y, (float)((int)CamCharacter.getZ()), 0);
				float float14 = IsoUtils.XToScreen(CamCharacter.x, CamCharacter.y, (float)((int)CamCharacter.getZ()), 0);
				float float15 = IsoUtils.YToScreen(CamCharacter.x, CamCharacter.y, (float)((int)CamCharacter.getZ()), 0);
				float float16 = float12 - float14;
				float float17 = float13 - float15;
				int int3 = Core.getInstance().getScreenWidth();
				int int4 = Core.getInstance().getScreenHeight();
				if (IsoPlayer.numPlayers > 1) {
					int3 /= 2;
					if (IsoPlayer.numPlayers > 2) {
						int4 /= 2;
					}
				}

				float float18;
				float float19;
				if (int3 > int4) {
					float18 = (float)int3 / (float)int4;
					float19 = float17 * float18;
				} else {
					float18 = (float)int4 / (float)int3;
					float19 = float16 * float18;
				}

				int int5 = 0;
				int int6 = 0;
				if (IsoPlayer.numPlayers > 1) {
					if (IsoPlayer.getPlayerIndex() == 1 || IsoPlayer.getPlayerIndex() == 3) {
						int5 = int3;
					}

					if (IsoPlayer.getPlayerIndex() == 2 || IsoPlayer.getPlayerIndex() == 3) {
						int6 = int4;
					}
				}

				float16 = (float)(Mouse.getXA() - (int5 + int3 / 2));
				float17 = (float)(Mouse.getYA() - (int6 + int4 / 2));
				if (Math.abs(float16) < (float)(int3 / 4) && Math.abs(float17) < (float)(int4 / 4)) {
					float17 = 0.0F;
					float16 = 0.0F;
				}

				RightClickTargetX[IsoPlayer.getPlayerIndex()] = float16;
				RightClickTargetY[IsoPlayer.getPlayerIndex()] = float17;
				IsoPlayer.getInstance().dirtyRecalcGridStackTime = 2.0F;
			}
		}

		IsoSprite.globalOffsetX = -1;
	}

	public static void updateDemo() {
	}

	public static void SetCharacterToFollow(IsoGameCharacter gameCharacter) {
		if (!GameClient.bClient && !GameServer.bServer) {
			CamCharacter = gameCharacter;
			if (CamCharacter instanceof IsoPlayer && ((IsoPlayer)CamCharacter).isLocalPlayer() && UIManager.getMoodleUI((double)((IsoPlayer)CamCharacter).getPlayerNum()) != null) {
				int int1 = ((IsoPlayer)CamCharacter).getPlayerNum();
				UIManager.getUI().remove(UIManager.getMoodleUI((double)int1));
				UIManager.setMoodleUI((double)int1, new MoodlesUI());
				UIManager.getMoodleUI((double)int1).setCharacter(CamCharacter);
				UIManager.getUI().add(UIManager.getMoodleUI((double)int1));
			}
		}
	}

	public static float getRightClickOffX() {
		return RightClickX[IsoPlayer.getPlayerIndex()];
	}

	public static float getRightClickOffY() {
		return RightClickY[IsoPlayer.getPlayerIndex()];
	}

	public static float getOffX() {
		return (float)((int)(OffX[IsoPlayer.getPlayerIndex()] + RightClickX[IsoPlayer.getPlayerIndex()] + DeferedSX));
	}

	public static void setOffX(float float1) {
		OffX[IsoPlayer.getPlayerIndex()] = float1;
	}

	public static float getOffY() {
		return (float)((int)(OffY[IsoPlayer.getPlayerIndex()] + RightClickY[IsoPlayer.getPlayerIndex()] + DeferedSY));
	}

	public static void setOffY(float float1) {
		OffY[IsoPlayer.getPlayerIndex()] = float1;
	}

	public static float getLastOffX() {
		return (float)((int)(lastOffX[IsoPlayer.getPlayerIndex()] + RightClickX[IsoPlayer.getPlayerIndex()]));
	}

	public static void setLastOffX(float float1) {
		lastOffX[IsoPlayer.getPlayerIndex()] = float1;
	}

	public static float getLastOffY() {
		return (float)((int)(lastOffY[IsoPlayer.getPlayerIndex()] + RightClickY[IsoPlayer.getPlayerIndex()]));
	}

	public static void setLastOffY(float float1) {
		lastOffY[IsoPlayer.getPlayerIndex()] = float1;
	}

	public static IsoGameCharacter getCamCharacter() {
		return CamCharacter;
	}

	public static void setCamCharacter(IsoGameCharacter gameCharacter) {
		CamCharacter = gameCharacter;
	}

	public static Vector2 getFakePos() {
		return FakePos;
	}

	public static void setFakePos(Vector2 vector2) {
		FakePos = vector2;
	}

	public static Vector2 getFakePosVec() {
		return FakePosVec;
	}

	public static void setFakePosVec(Vector2 vector2) {
		FakePosVec = vector2;
	}

	public static int getTargetTileX() {
		return TargetTileX;
	}

	public static void setTargetTileX(int int1) {
		TargetTileX = int1;
	}

	public static int getTargetTileY() {
		return TargetTileY;
	}

	public static void setTargetTileY(int int1) {
		TargetTileY = int1;
	}

	public static int getScreenLeft(int int1) {
		return int1 != 1 && int1 != 3 ? 0 : Core.getInstance().getScreenWidth() / 2;
	}

	public static int getScreenWidth(int int1) {
		return IsoPlayer.numPlayers > 1 ? Core.getInstance().getScreenWidth() / 2 : Core.getInstance().getScreenWidth();
	}

	public static int getScreenTop(int int1) {
		return int1 != 2 && int1 != 3 ? 0 : Core.getInstance().getScreenHeight() / 2;
	}

	public static int getScreenHeight(int int1) {
		return IsoPlayer.numPlayers > 2 ? Core.getInstance().getScreenHeight() / 2 : Core.getInstance().getScreenHeight();
	}

	public static int getOffscreenLeft(int int1) {
		return int1 != 1 && int1 != 3 ? 0 : Core.getInstance().getOffscreenTrueWidth() / 2;
	}

	public static int getOffscreenWidth(int int1) {
		return Core.getInstance().getOffscreenWidth(int1);
	}

	public static int getOffscreenTop(int int1) {
		return (int1 == 0 || int1 == 1) && IsoPlayer.numPlayers > 2 ? Core.getInstance().getOffscreenTrueHeight() / 2 : 0;
	}

	public static int getOffscreenHeight(int int1) {
		return Core.getInstance().getOffscreenHeight(int1);
	}

	static  {
		PLAYER_OFFSET_Y = -56 / (2 / Core.TileScale);
		offVec = new Vector2();
		frameState = new IsoCamera.FrameState();
	}

	public static class FrameState {
		public int frameCount;
		public boolean Paused;
		public int playerIndex;
		public float CamCharacterX;
		public float CamCharacterY;
		public float CamCharacterZ;
		public IsoGameCharacter CamCharacter;
		public IsoGridSquare CamCharacterSquare;
		public IsoRoom CamCharacterRoom;
		public float OffX;
		public float OffY;
		public int OffscreenWidth;
		public int OffscreenHeight;

		public void set(int int1) {
			this.Paused = UIManager.getSpeedControls() != null && UIManager.getSpeedControls().getCurrentGameSpeed() == 0;
			this.playerIndex = int1;
			this.CamCharacter = IsoPlayer.players[int1];
			this.CamCharacterX = this.CamCharacter.getX();
			this.CamCharacterY = this.CamCharacter.getY();
			this.CamCharacterZ = this.CamCharacter.getZ();
			this.CamCharacterSquare = this.CamCharacter.getCurrentSquare();
			this.CamCharacterRoom = this.CamCharacterSquare == null ? null : this.CamCharacterSquare.getRoom();
			this.OffX = IsoCamera.getOffX();
			this.OffY = IsoCamera.getOffY();
			this.OffscreenWidth = IsoCamera.getOffscreenWidth(int1);
			this.OffscreenHeight = IsoCamera.getOffscreenHeight(int1);
		}
	}
}
