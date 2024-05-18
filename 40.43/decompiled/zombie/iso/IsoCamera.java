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
      float var0 = OffX[IsoPlayer.getPlayerIndex()];
      float var1 = OffY[IsoPlayer.getPlayerIndex()];
      if (CamCharacter != null) {
         IsoGameCharacter var2 = CamCharacter;
         var0 = IsoUtils.XToScreen(var2.x + DeferedX[IsoPlayer.getPlayerIndex()], var2.y + DeferedY[IsoPlayer.getPlayerIndex()], var2.z, 0);
         var1 = IsoUtils.YToScreen(var2.x + DeferedX[IsoPlayer.getPlayerIndex()], var2.y + DeferedY[IsoPlayer.getPlayerIndex()], var2.z, 0);
         var0 -= (float)(getOffscreenWidth(IsoPlayer.getPlayerIndex()) / 2);
         var1 -= (float)(getOffscreenHeight(IsoPlayer.getPlayerIndex()) / 2);
         var1 -= var2.getOffsetY() * 1.5F;
         var0 = (float)((int)var0);
         var1 = (float)((int)var1);
         var0 += (float)PLAYER_OFFSET_X;
         var1 += (float)PLAYER_OFFSET_Y;
      }

      TOffX[IsoPlayer.getPlayerIndex()] = var0;
      TOffY[IsoPlayer.getPlayerIndex()] = var1;
      float var25 = (TOffX[IsoPlayer.getPlayerIndex()] - OffX[IsoPlayer.getPlayerIndex()]) / 5.0F;
      float var3 = (TOffY[IsoPlayer.getPlayerIndex()] - OffY[IsoPlayer.getPlayerIndex()]) / 5.0F;
      float[] var10000 = OffX;
      int var10001 = IsoPlayer.getPlayerIndex();
      var10000[var10001] += var25;
      var10000 = OffY;
      var10001 = IsoPlayer.getPlayerIndex();
      var10000[var10001] += var3;
      if (lastOffX[IsoPlayer.getPlayerIndex()] == 0.0F && lastOffY[IsoPlayer.getPlayerIndex()] == 0.0F) {
         lastOffX[IsoPlayer.getPlayerIndex()] = OffX[IsoPlayer.getPlayerIndex()];
         lastOffY[IsoPlayer.getPlayerIndex()] = OffY[IsoPlayer.getPlayerIndex()];
      }

      float var4 = (float)PerformanceSettings.LockFPS / 60.0F;
      var10000 = RightClickX;
      var10001 = IsoPlayer.getPlayerIndex();
      var10000[var10001] += (RightClickTargetX[IsoPlayer.getPlayerIndex()] - RightClickX[IsoPlayer.getPlayerIndex()]) / (80.0F * var4);
      var10000 = RightClickY;
      var10001 = IsoPlayer.getPlayerIndex();
      var10000[var10001] += (RightClickTargetY[IsoPlayer.getPlayerIndex()] - RightClickY[IsoPlayer.getPlayerIndex()]) / (80.0F * var4);
      int var5 = Core.getInstance().getKey("Aim");
      boolean var6 = GameKeyboard.isKeyDown(var5) || GameKeyboard.wasKeyDown(var5);
      boolean var7 = var5 == 29 || var5 == 157;
      boolean var8 = GameWindow.ActivatedJoyPad != null && IsoPlayer.instance != null && IsoPlayer.instance.JoypadBind != -1;
      float var11;
      float var12;
      if (IsoPlayer.instance != null && IsoPlayer.instance.getVehicle() != null) {
         IsoPlayer var26 = IsoPlayer.instance;
         BaseVehicle var27 = var26.getVehicle();
         var11 = var27.getCurrentSpeedKmHour() * BaseVehicle.getFakeSpeedModifier() / 10.0F;
         RightClickTargetX[IsoPlayer.getPlayerIndex()] = IsoUtils.XToScreen(var26.angle.x * var11, var26.angle.y * var11, var26.z, 0);
         RightClickTargetY[IsoPlayer.getPlayerIndex()] = IsoUtils.YToScreen(var26.angle.x * var11, var26.angle.y * var11, var26.z, 0);
         if (Math.abs(var11) < 5.0F) {
            var12 = 1.0F - Math.abs(var11) / 5.0F;
            RightClickTargetX[IsoPlayer.getPlayerIndex()] = 0.0F;
            RightClickTargetY[IsoPlayer.getPlayerIndex()] = 0.0F;
            var10000 = RightClickX;
            var10001 = IsoPlayer.getPlayerIndex();
            var10000[var10001] += (RightClickTargetX[IsoPlayer.getPlayerIndex()] - RightClickX[IsoPlayer.getPlayerIndex()]) / (32.0F / var12 * var4);
            var10000 = RightClickY;
            var10001 = IsoPlayer.getPlayerIndex();
            var10000[var10001] += (RightClickTargetY[IsoPlayer.getPlayerIndex()] - RightClickY[IsoPlayer.getPlayerIndex()]) / (32.0F / var12 * var4);
         }
      } else {
         float var9;
         if (var8 && IsoPlayer.instance.IsAiming() && JoypadManager.instance.isRBPressed(IsoPlayer.instance.JoypadBind)) {
            var9 = Core.getInstance().getZoom(IsoPlayer.instance.getPlayerNum());
            RightClickTargetX[IsoPlayer.getPlayerIndex()] = JoypadManager.instance.getAimingAxisX(IsoPlayer.instance.JoypadBind) * 500.0F * var9;
            RightClickTargetY[IsoPlayer.getPlayerIndex()] = JoypadManager.instance.getAimingAxisY(IsoPlayer.instance.JoypadBind) * 500.0F * var9;
            IsoPlayer.getInstance().dirtyRecalcGridStackTime = 2.0F;
         } else if (GameServer.bServer || !Core.OptionPanCameraWhileAiming || var8 || IsoPlayer.instance == null || IsoPlayer.instance.isDead() || var7 && var6 && UIManager.isMouseOverInventory() || !IsoPlayer.instance.isAiming() && (!IsoPlayer.instance.bRightClickMove || IsoPlayer.instance.JustMoved)) {
            RightClickTargetX[IsoPlayer.getPlayerIndex()] = 0.0F;
            RightClickTargetY[IsoPlayer.getPlayerIndex()] = 0.0F;
            if (RightClickTargetX[IsoPlayer.getPlayerIndex()] != RightClickX[IsoPlayer.getPlayerIndex()] || RightClickTargetY[IsoPlayer.getPlayerIndex()] != RightClickY[IsoPlayer.getPlayerIndex()]) {
               IsoPlayer.getInstance().dirtyRecalcGridStackTime = 2.0F;
            }

            var10000 = RightClickX;
            var10001 = IsoPlayer.getPlayerIndex();
            var10000[var10001] += (RightClickTargetX[IsoPlayer.getPlayerIndex()] - RightClickX[IsoPlayer.getPlayerIndex()]) / (16.0F * var4);
            if (Math.abs(RightClickX[IsoPlayer.getPlayerIndex()]) < 0.01F) {
               RightClickX[IsoPlayer.getPlayerIndex()] = 0.0F;
            }

            var10000 = RightClickY;
            var10001 = IsoPlayer.getPlayerIndex();
            var10000[var10001] += (RightClickTargetY[IsoPlayer.getPlayerIndex()] - RightClickY[IsoPlayer.getPlayerIndex()]) / (16.0F * var4);
            if (Math.abs(RightClickY[IsoPlayer.getPlayerIndex()]) < 0.01F) {
               RightClickY[IsoPlayer.getPlayerIndex()] = 0.0F;
            }
         } else {
            var9 = (float)Mouse.getX();
            float var10 = (float)Mouse.getY();
            var11 = IsoUtils.XToIsoTrue(var9, var10, (int)CamCharacter.getZ());
            var12 = IsoUtils.YToIsoTrue(var9, var10, (int)CamCharacter.getZ());
            var11 = (float)((double)var11 - 10.05D);
            var12 = (float)((double)var12 - 10.05D);
            --var11;
            var12 -= 0.3F;
            var11 -= 0.3F;
            float var13 = var11 - CamCharacter.x;
            float var14 = var12 - CamCharacter.y;
            var13 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
            var14 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
            offVec.x = var13;
            offVec.y = var14;
            if (offVec.getLength() < 7.0F) {
               offVec.setLength(0.0F);
            } else {
               offVec.setLength(offVec.getLength() - 7.0F);
            }

            if (offVec.getLength() > 70.0F) {
               offVec.setLength(70.0F);
            }

            float var15 = IsoUtils.XToScreen(offVec.x + CamCharacter.x, offVec.y + CamCharacter.y, (float)((int)CamCharacter.getZ()), 0);
            float var16 = IsoUtils.YToScreen(offVec.x + CamCharacter.x, offVec.y + CamCharacter.y, (float)((int)CamCharacter.getZ()), 0);
            float var17 = IsoUtils.XToScreen(CamCharacter.x, CamCharacter.y, (float)((int)CamCharacter.getZ()), 0);
            float var18 = IsoUtils.YToScreen(CamCharacter.x, CamCharacter.y, (float)((int)CamCharacter.getZ()), 0);
            float var19 = var15 - var17;
            float var20 = var16 - var18;
            int var21 = Core.getInstance().getScreenWidth();
            int var22 = Core.getInstance().getScreenHeight();
            if (IsoPlayer.numPlayers > 1) {
               var21 /= 2;
               if (IsoPlayer.numPlayers > 2) {
                  var22 /= 2;
               }
            }

            float var23;
            float var28;
            if (var21 > var22) {
               var23 = (float)var21 / (float)var22;
               var28 = var20 * var23;
            } else {
               var23 = (float)var22 / (float)var21;
               var28 = var19 * var23;
            }

            int var29 = 0;
            int var24 = 0;
            if (IsoPlayer.numPlayers > 1) {
               if (IsoPlayer.getPlayerIndex() == 1 || IsoPlayer.getPlayerIndex() == 3) {
                  var29 = var21;
               }

               if (IsoPlayer.getPlayerIndex() == 2 || IsoPlayer.getPlayerIndex() == 3) {
                  var24 = var22;
               }
            }

            var19 = (float)(Mouse.getXA() - (var29 + var21 / 2));
            var20 = (float)(Mouse.getYA() - (var24 + var22 / 2));
            if (Math.abs(var19) < (float)(var21 / 4) && Math.abs(var20) < (float)(var22 / 4)) {
               var20 = 0.0F;
               var19 = 0.0F;
            }

            RightClickTargetX[IsoPlayer.getPlayerIndex()] = var19;
            RightClickTargetY[IsoPlayer.getPlayerIndex()] = var20;
            IsoPlayer.getInstance().dirtyRecalcGridStackTime = 2.0F;
         }
      }

      IsoSprite.globalOffsetX = -1;
   }

   public static void updateDemo() {
   }

   public static void SetCharacterToFollow(IsoGameCharacter var0) {
      if (!GameClient.bClient && !GameServer.bServer) {
         CamCharacter = var0;
         if (CamCharacter instanceof IsoPlayer && ((IsoPlayer)CamCharacter).isLocalPlayer() && UIManager.getMoodleUI((double)((IsoPlayer)CamCharacter).getPlayerNum()) != null) {
            int var1 = ((IsoPlayer)CamCharacter).getPlayerNum();
            UIManager.getUI().remove(UIManager.getMoodleUI((double)var1));
            UIManager.setMoodleUI((double)var1, new MoodlesUI());
            UIManager.getMoodleUI((double)var1).setCharacter(CamCharacter);
            UIManager.getUI().add(UIManager.getMoodleUI((double)var1));
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

   public static void setOffX(float var0) {
      OffX[IsoPlayer.getPlayerIndex()] = var0;
   }

   public static float getOffY() {
      return (float)((int)(OffY[IsoPlayer.getPlayerIndex()] + RightClickY[IsoPlayer.getPlayerIndex()] + DeferedSY));
   }

   public static void setOffY(float var0) {
      OffY[IsoPlayer.getPlayerIndex()] = var0;
   }

   public static float getLastOffX() {
      return (float)((int)(lastOffX[IsoPlayer.getPlayerIndex()] + RightClickX[IsoPlayer.getPlayerIndex()]));
   }

   public static void setLastOffX(float var0) {
      lastOffX[IsoPlayer.getPlayerIndex()] = var0;
   }

   public static float getLastOffY() {
      return (float)((int)(lastOffY[IsoPlayer.getPlayerIndex()] + RightClickY[IsoPlayer.getPlayerIndex()]));
   }

   public static void setLastOffY(float var0) {
      lastOffY[IsoPlayer.getPlayerIndex()] = var0;
   }

   public static IsoGameCharacter getCamCharacter() {
      return CamCharacter;
   }

   public static void setCamCharacter(IsoGameCharacter var0) {
      CamCharacter = var0;
   }

   public static Vector2 getFakePos() {
      return FakePos;
   }

   public static void setFakePos(Vector2 var0) {
      FakePos = var0;
   }

   public static Vector2 getFakePosVec() {
      return FakePosVec;
   }

   public static void setFakePosVec(Vector2 var0) {
      FakePosVec = var0;
   }

   public static int getTargetTileX() {
      return TargetTileX;
   }

   public static void setTargetTileX(int var0) {
      TargetTileX = var0;
   }

   public static int getTargetTileY() {
      return TargetTileY;
   }

   public static void setTargetTileY(int var0) {
      TargetTileY = var0;
   }

   public static int getScreenLeft(int var0) {
      return var0 != 1 && var0 != 3 ? 0 : Core.getInstance().getScreenWidth() / 2;
   }

   public static int getScreenWidth(int var0) {
      return IsoPlayer.numPlayers > 1 ? Core.getInstance().getScreenWidth() / 2 : Core.getInstance().getScreenWidth();
   }

   public static int getScreenTop(int var0) {
      return var0 != 2 && var0 != 3 ? 0 : Core.getInstance().getScreenHeight() / 2;
   }

   public static int getScreenHeight(int var0) {
      return IsoPlayer.numPlayers > 2 ? Core.getInstance().getScreenHeight() / 2 : Core.getInstance().getScreenHeight();
   }

   public static int getOffscreenLeft(int var0) {
      return var0 != 1 && var0 != 3 ? 0 : Core.getInstance().getOffscreenTrueWidth() / 2;
   }

   public static int getOffscreenWidth(int var0) {
      return Core.getInstance().getOffscreenWidth(var0);
   }

   public static int getOffscreenTop(int var0) {
      return (var0 == 0 || var0 == 1) && IsoPlayer.numPlayers > 2 ? Core.getInstance().getOffscreenTrueHeight() / 2 : 0;
   }

   public static int getOffscreenHeight(int var0) {
      return Core.getInstance().getOffscreenHeight(var0);
   }

   static {
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

      public void set(int var1) {
         this.Paused = UIManager.getSpeedControls() != null && UIManager.getSpeedControls().getCurrentGameSpeed() == 0;
         this.playerIndex = var1;
         this.CamCharacter = IsoPlayer.players[var1];
         this.CamCharacterX = this.CamCharacter.getX();
         this.CamCharacterY = this.CamCharacter.getY();
         this.CamCharacterZ = this.CamCharacter.getZ();
         this.CamCharacterSquare = this.CamCharacter.getCurrentSquare();
         this.CamCharacterRoom = this.CamCharacterSquare == null ? null : this.CamCharacterSquare.getRoom();
         this.OffX = IsoCamera.getOffX();
         this.OffY = IsoCamera.getOffY();
         this.OffscreenWidth = IsoCamera.getOffscreenWidth(var1);
         this.OffscreenHeight = IsoCamera.getOffscreenHeight(var1);
      }
   }
}
