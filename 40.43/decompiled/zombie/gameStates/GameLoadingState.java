package zombie.gameStates;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import zombie.AmbientStreamManager;
import zombie.ChunkMapFilenames;
import zombie.FrameLoader;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.Quests.QuestManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatManager;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.Texture;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.JoypadManager;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.SliceY;
import zombie.iso.WorldStreamer;
import zombie.iso.areas.SafeHouse;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.scripting.ScriptManager;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.vehicles.BaseVehicle;

public class GameLoadingState extends GameState {
   Thread loader = null;
   public static boolean newGame = true;
   private static long startTime;
   public static boolean build23Stop = false;
   public static boolean unexpectedError = false;
   public static String GameLoadingString = "";
   public static boolean playerWrongIP = false;
   private static boolean bShowedUI = false;
   public static boolean mapDownloadFailed = false;
   public static boolean playerCreated = false;
   public static boolean bDone = false;
   public static boolean convertingWorld = false;
   public static int convertingFileCount = -1;
   public static int convertingFileMax = -1;
   public int Stage = 0;
   float TotalTime = 33.0F;
   float loadingDotTick = 0.0F;
   String loadingDot = "";
   private float clickToSkipAlpha = 1.0F;
   private boolean clickToSkipFadeIn = false;
   public float Time = 0.0F;
   public boolean bForceDone = false;

   public void enter() {
      GameWindow.bLoadedAsClient = GameClient.bClient;
      GameWindow.OkToSaveOnExit = false;
      bShowedUI = false;
      ChunkMapFilenames.instance.clear();
      DebugLog.log("Savefile name is \"" + Core.GameSaveWorld + "\"");
      GameLoadingString = "";

      try {
         LuaManager.LoadDirBase("server");
         LuaManager.finishChecksum();
      } catch (Exception var8) {
         ExceptionLogger.logException(var8);
      }

      Core.getInstance().initFBOs();
      Core.getInstance().initShaders();
      ModelManager.instance.create();
      GameWindow.bServerDisconnected = false;
      if (GameClient.bClient && !GameClient.instance.bConnected) {
         GameClient.instance.init();

         for(Core.GameMode = "Multiplayer"; GameClient.instance.ID == -1; GameClient.instance.update()) {
            try {
               Thread.sleep(10L);
            } catch (InterruptedException var7) {
               var7.printStackTrace();
            }
         }

         Core.GameSaveWorld = "clienttest" + GameClient.instance.ID;
         LuaManager.GlobalObject.deleteSave("clienttest" + GameClient.instance.ID);
         LuaManager.GlobalObject.createWorld("clienttest" + GameClient.instance.ID);
      }

      if (Core.GameSaveWorld.isEmpty()) {
         DebugLog.log("No savefile directory was specified.  It's a bug.");
         GameWindow.DoLoadingText("No savefile directory was specified.  The game will now close.  Sorry!");

         try {
            Thread.sleep(4000L);
         } catch (Exception var6) {
         }

         System.exit(-1);
      }

      File var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld);
      if (!var1.exists() && !Core.getInstance().isNoSave()) {
         DebugLog.log("The savefile directory doesn't exist.  It's a bug.");
         GameWindow.DoLoadingText("The savefile directory doesn't exist.  The game will now close.  Sorry!");

         try {
            Thread.sleep(4000L);
         } catch (Exception var5) {
         }

         System.exit(-1);
      }

      try {
         if (!GameClient.bClient && !GameServer.bServer && !Core.bTutorial && !Core.isLastStand() && !"Multiplayer".equals(Core.GameMode)) {
            FileWriter var2 = new FileWriter(new File(GameWindow.getCacheDir() + File.separator + "latestSave.ini"));
            var2.write(IsoWorld.instance.getWorld() + "\r\n");
            var2.write(Core.getInstance().getGameMode() + "\r\n");
            var2.write(IsoWorld.instance.getDifficulty() + "\r\n");
            var2.flush();
            var2.close();
         }
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      bDone = false;
      this.bForceDone = false;
      IsoChunkMap.CalcChunkWidth();
      LosUtil.init(IsoChunkMap.ChunkGridWidth * 10, IsoChunkMap.ChunkGridWidth * 10);
      this.Time = 0.0F;
      this.Stage = 0;
      this.clickToSkipAlpha = 1.0F;
      this.clickToSkipFadeIn = false;
      startTime = System.currentTimeMillis();
      if (SliceY.SliceBuffer == null) {
         SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
      }

      SoundManager.instance.Purge();
      boolean var9 = "earlyaccess".equals(SoundManager.instance.getCurrentMusicLibrary());
      SoundManager.instance.DoMusic(var9 ? "OldMusic_preface" : "NewMusic_Introduction", false);
      ZomboidFileSystem.instance.loadModPackFiles();
      ScriptManager.instance.Trigger("OnPreMapLoad");
      LuaEventManager.triggerEvent("OnPreMapLoad");
      newGame = true;
      build23Stop = false;
      unexpectedError = false;
      mapDownloadFailed = false;
      playerCreated = false;
      convertingWorld = false;
      convertingFileCount = 0;
      convertingFileMax = -1;
      File var3 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_ver.bin");
      if (var3.exists()) {
         newGame = false;
      }

      if (GameClient.bClient) {
         newGame = false;
      }

      this.loader = new Thread(new Runnable() {
         public void run() {
            try {
               this.runInner();
            } catch (Exception var2) {
               GameLoadingState.unexpectedError = true;
               var2.printStackTrace();
            }

         }

         private void runInner() throws Exception {
            boolean var1 = (new File(GameWindow.getGameModeCacheDir() + File.separator)).mkdir();
            BaseVehicle.LoadAllVehicleTextures();
            if (GameClient.bClient) {
               GameClient.instance.GameLoadingRequestData();
            }

            TutorialManager.instance = new TutorialManager();
            QuestManager.instance = new QuestManager();
            GameTime.setInstance(new GameTime());
            ClimateManager.setInstance(new ClimateManager());
            IsoWorld.instance = new IsoWorld();
            IsoWorld.instance.init();
            if (GameWindow.bServerDisconnected) {
               GameLoadingState.bDone = true;
            } else if (!GameLoadingState.playerWrongIP) {
               if (!GameLoadingState.build23Stop) {
                  LuaEventManager.triggerEvent("OnGameTimeLoaded");
                  SGlobalObjects.initSystems();
                  CGlobalObjects.initSystems();
                  IsoObjectPicker.Instance.Init();
                  TutorialManager.instance.init();
                  TutorialManager.instance.CreateQuests();
                  File var2 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_t.bin");
                  if (var2.exists()) {
                  }

                  if (!GameServer.bServer) {
                     var2 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_ver.bin");
                     boolean var3 = !var2.exists();
                     if (var3 || IsoWorld.SavedWorldVersion != 143) {
                        if (!var3 && IsoWorld.SavedWorldVersion != 143) {
                           GameLoadingState.GameLoadingString = "Saving converted world.";
                        }

                        try {
                           GameWindow.save(true);
                        } catch (Exception var5) {
                           var5.printStackTrace();
                        }
                     }
                  }

                  ChatManager.getInstance().init(true, IsoPlayer.getInstance());
                  UIManager.bSuspend = false;
                  GameLoadingState.playerCreated = true;
                  GameLoadingState.GameLoadingString = "";
                  GameLoadingState.Done();
               }
            }
         }
      });
      UIManager.bSuspend = true;
      this.loader.setName("GameLoadingThread");
      this.loader.start();
   }

   public static void Done() {
      bDone = true;
      DebugLog.log("game loading took " + (System.currentTimeMillis() - startTime + 999L) / 1000L + " seconds");
   }

   public GameState redirectState() {
      return new IngameState();
   }

   public void exit() {
      UIManager.init();
      if (!FrameLoader.bDedicated) {
         LuaEventManager.triggerEvent("OnCreatePlayer", 0, IsoPlayer.players[0]);
      }

      this.loader = null;
      bDone = false;
      this.Stage = 0;
      ScriptManager.instance.Trigger("OnGameStart");
      IsoCamera.SetCharacterToFollow(IsoPlayer.getInstance());
      if (GameClient.bClient && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
         SafeHouse var1 = SafeHouse.isSafeHouse(IsoPlayer.getInstance().getCurrentSquare(), GameClient.username, true);
         if (var1 != null) {
            IsoPlayer.getInstance().setX((float)(var1.getX() - 1));
            IsoPlayer.getInstance().setY((float)(var1.getY() - 1));
         }
      }

      if (!FrameLoader.bDedicated) {
         SoundManager.instance.stopMusic("");
      }

      if (!FrameLoader.bDedicated) {
         AmbientStreamManager.instance.init();
      }

      if (IsoPlayer.instance != null && IsoPlayer.instance.isAsleep()) {
         UIManager.setFadeBeforeUI(IsoPlayer.instance.getPlayerNum(), true);
         UIManager.FadeOut((double)IsoPlayer.instance.getPlayerNum(), 2.0D);
         UIManager.setFadeTime((double)IsoPlayer.instance.getPlayerNum(), 0.0D);
         UIManager.getSpeedControls().SetCurrentGameSpeed(3);
      }

      GameWindow.OkToSaveOnExit = true;
   }

   public void render() {
      this.loadingDotTick += GameTime.getInstance().getMultiplier();
      if (this.loadingDotTick > 20.0F) {
         this.loadingDot = ".";
      }

      if (this.loadingDotTick > 40.0F) {
         this.loadingDot = "..";
      }

      if (this.loadingDotTick > 60.0F) {
         this.loadingDot = "...";
      }

      if (this.loadingDotTick > 80.0F) {
         this.loadingDot = "";
         this.loadingDotTick = 0.0F;
      }

      this.Time += GameTime.getInstance().getMultiplier() / 60.0F;
      float var1 = 0.0F;
      float var2 = 0.0F;
      float var3 = 0.0F;
      float var4;
      float var5;
      float var6;
      float var7;
      float var8;
      float var9;
      if (this.Stage == 0) {
         var4 = this.Time;
         var5 = 0.0F;
         var6 = 1.0F;
         var7 = 5.0F;
         var8 = 7.0F;
         var9 = 0.0F;
         if (var4 > var5 && var4 < var6) {
            var9 = (var4 - var5) / (var6 - var5);
         }

         if (var4 >= var6 && var4 <= var7) {
            var9 = 1.0F;
         }

         if (var4 > var7 && var4 < var8) {
            var9 = 1.0F - (var4 - var7) / (var8 - var7);
         }

         if (var4 >= var8) {
            ++this.Stage;
         }

         var1 = var9;
      }

      if (this.Stage == 1) {
         var4 = this.Time;
         var5 = 7.0F;
         var6 = 8.0F;
         var7 = 13.0F;
         var8 = 15.0F;
         var9 = 0.0F;
         if (var4 > var5 && var4 < var6) {
            var9 = (var4 - var5) / (var6 - var5);
         }

         if (var4 >= var6 && var4 <= var7) {
            var9 = 1.0F;
         }

         if (var4 > var7 && var4 < var8) {
            var9 = 1.0F - (var4 - var7) / (var8 - var7);
         }

         if (var4 >= var8) {
            ++this.Stage;
         }

         var2 = var9;
      }

      if (this.Stage == 2) {
         var4 = this.Time;
         var5 = 15.0F;
         var6 = 16.0F;
         var7 = 31.0F;
         var8 = this.TotalTime;
         var9 = 0.0F;
         if (var4 > var5 && var4 < var6) {
            var9 = (var4 - var5) / (var6 - var5);
         }

         if (var4 >= var6 && var4 <= var7) {
            var9 = 1.0F;
         }

         if (var4 > var7 && var4 < var8) {
            var9 = 1.0F - (var4 - var7) / (var8 - var7);
         }

         if (var4 >= var8) {
            ++this.Stage;
         }

         var3 = var9;
      }

      Core.getInstance().StartFrame();
      Core.getInstance().EndFrame();
      boolean var14 = UIManager.useUIFBO;
      UIManager.useUIFBO = false;
      Core.getInstance().StartFrameUI();
      SpriteRenderer.instance.render((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F);
      int var16;
      int var17;
      int var18;
      if (mapDownloadFailed) {
         var16 = Core.getInstance().getScreenWidth() / 2;
         var17 = Core.getInstance().getScreenHeight() / 2;
         var18 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
         int var22 = var17 - var18 / 2;
         String var25 = Translator.getText("UI_GameLoad_MapDownloadFailed");
         TextManager.instance.DrawStringCentre(UIFont.Medium, (double)var16, (double)var22, var25, 0.8D, 0.1D, 0.1D, 1.0D);
         UIManager.render();
         Core.getInstance().EndFrameUI();
      } else {
         byte var21;
         int var24;
         if (unexpectedError) {
            var16 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
            var17 = TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight();
            byte var20 = 8;
            var21 = 2;
            var24 = var16 + var20 + var17 + var21 + var17;
            int var23 = Core.getInstance().getScreenWidth() / 2;
            int var11 = Core.getInstance().getScreenHeight() / 2;
            int var12 = var11 - var24 / 2;
            TextManager.instance.DrawStringCentre(UIFont.Medium, (double)var23, (double)var12, Translator.getText("UI_GameLoad_UnexpectedError1"), 0.8D, 0.1D, 0.1D, 1.0D);
            TextManager.instance.DrawStringCentre(UIFont.Small, (double)var23, (double)(var12 + var16 + var20), Translator.getText("UI_GameLoad_UnexpectedError2"), 1.0D, 1.0D, 1.0D, 1.0D);
            String var13 = GameWindow.getCacheDir() + File.separator + "console.txt";
            TextManager.instance.DrawStringCentre(UIFont.Small, (double)var23, (double)(var12 + var16 + var20 + var17 + var21), var13, 1.0D, 1.0D, 1.0D, 1.0D);
            UIManager.render();
            Core.getInstance().EndFrameUI();
         } else {
            String var10;
            if (GameWindow.bServerDisconnected) {
               var16 = Core.getInstance().getScreenWidth() / 2;
               var17 = Core.getInstance().getScreenHeight() / 2;
               var18 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
               var21 = 2;
               var24 = var17 - (var18 + var21 + var18) / 2;
               var10 = GameWindow.kickReason;
               if (var10 == null) {
                  var10 = Translator.getText("UI_OnConnectFailed_ConnectionLost");
               }

               TextManager.instance.DrawStringCentre(UIFont.Medium, (double)var16, (double)var24, var10, 0.8D, 0.1D, 0.1D, 1.0D);
               UIManager.render();
               Core.getInstance().EndFrameUI();
            } else {
               if (build23Stop) {
                  TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 100), "This save is incompatible. Please switch to Steam branch \"build23\" to continue this save.", 0.8D, 0.1D, 0.1D, 1.0D);
               } else if (convertingWorld) {
                  TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 100), Translator.getText("UI_ConvertWorld"), 0.5D, 0.5D, 0.5D, 1.0D);
                  if (convertingFileMax != -1) {
                     TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 100 + TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight() + 8), convertingFileCount + " / " + convertingFileMax, 0.5D, 0.5D, 0.5D, 1.0D);
                  }
               }

               if (playerWrongIP) {
                  var16 = Core.getInstance().getScreenWidth() / 2;
                  var17 = Core.getInstance().getScreenHeight() / 2;
                  var18 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
                  var21 = 2;
                  var24 = var17 - (var18 + var21 + var18) / 2;
                  var10 = GameLoadingString;
                  if (GameLoadingString == null) {
                     var10 = "";
                  }

                  TextManager.instance.DrawStringCentre(UIFont.Medium, (double)var16, (double)var24, var10, 0.8D, 0.1D, 0.1D, 1.0D);
                  UIManager.render();
                  Core.getInstance().EndFrameUI();
               } else {
                  if (GameClient.bClient) {
                     String var15 = GameLoadingString;
                     if (GameLoadingString == null) {
                        var15 = "";
                     }

                     TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 60), var15, 0.5D, 0.5D, 0.5D, 1.0D);
                  } else if (!playerCreated && newGame && !Core.isLastStand()) {
                     TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 60), Translator.getText("UI_Loading").replace(".", ""), 0.5D, 0.5D, 0.5D, 1.0D);
                     TextManager.instance.DrawString(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2 + TextManager.instance.MeasureStringX(UIFont.Small, Translator.getText("UI_Loading").replace(".", "")) / 2 + 1), (double)(Core.getInstance().getScreenHeight() - 60), this.loadingDot, 0.5D, 0.5D, 0.5D, 1.0D);
                  }

                  if (this.Stage == 0) {
                     var16 = Core.getInstance().getScreenWidth() / 2;
                     var17 = Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2;
                     TextManager.instance.DrawStringCentre(UIFont.Intro, (double)var16, (double)var17, Translator.getText("UI_Intro1"), 1.0D, 1.0D, 1.0D, (double)var1);
                  }

                  if (this.Stage == 1) {
                     var16 = Core.getInstance().getScreenWidth() / 2;
                     var17 = Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2;
                     TextManager.instance.DrawStringCentre(UIFont.Intro, (double)var16, (double)var17, Translator.getText("UI_Intro2"), 1.0D, 1.0D, 1.0D, (double)var2);
                  }

                  if (this.Stage == 2) {
                     var16 = Core.getInstance().getScreenWidth() / 2;
                     var17 = Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2;
                     TextManager.instance.DrawStringCentre(UIFont.Intro, (double)var16, (double)var17, Translator.getText("UI_Intro3"), 1.0D, 1.0D, 1.0D, (double)var3);
                  }

                  if (playerCreated && (!newGame || this.Time >= this.TotalTime || Core.isLastStand())) {
                     if (this.clickToSkipFadeIn) {
                        this.clickToSkipAlpha += 1.0F / (float)PerformanceSettings.LockFPS;
                        if (this.clickToSkipAlpha > 1.0F) {
                           this.clickToSkipAlpha = 1.0F;
                           this.clickToSkipFadeIn = false;
                        }
                     } else {
                        this.clickToSkipAlpha -= 1.0F / (float)PerformanceSettings.LockFPS;
                        if (this.clickToSkipAlpha < 0.25F) {
                           this.clickToSkipFadeIn = true;
                        }
                     }

                     if (GameWindow.ActivatedJoyPad != null && !JoypadManager.instance.JoypadList.isEmpty()) {
                        Texture var19 = Texture.getSharedTexture("media/ui/abutton.png");
                        if (var19 != null) {
                           var17 = TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight();
                           SpriteRenderer.instance.render(var19, Core.getInstance().getScreenWidth() / 2 - TextManager.instance.MeasureStringX(UIFont.Small, Translator.getText("UI_PressAToStart")) / 2 - 8 - var19.getWidth(), Core.getInstance().getScreenHeight() - 60 + var17 / 2 - var19.getHeight() / 2, var19.getWidth(), var19.getHeight(), 1.0F, 1.0F, 1.0F, this.clickToSkipAlpha);
                        }

                        TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 60), Translator.getText("UI_PressAToStart"), 1.0D, 1.0D, 1.0D, (double)this.clickToSkipAlpha);
                     } else {
                        TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 60), Translator.getText("UI_ClickToSkip"), 1.0D, 1.0D, 1.0D, (double)this.clickToSkipAlpha);
                     }
                  }

                  Core.getInstance().EndFrameUI();
                  UIManager.useUIFBO = var14;
               }
            }
         }
      }
   }

   public GameStateMachine.StateAction update() {
      if (!unexpectedError && !GameWindow.bServerDisconnected && !playerWrongIP) {
         if (!bDone) {
            return GameStateMachine.StateAction.Remain;
         } else if (WorldStreamer.instance.isBusy()) {
            return GameStateMachine.StateAction.Remain;
         } else {
            if (FrameLoader.bDedicated || Mouse.isButtonDown(0)) {
               this.bForceDone = true;
            }

            if (GameWindow.ActivatedJoyPad != null && GameWindow.ActivatedJoyPad.isAPressed()) {
               this.bForceDone = true;
            }

            if (this.bForceDone) {
               this.bForceDone = false;
               return GameStateMachine.StateAction.Continue;
            } else {
               return GameStateMachine.StateAction.Remain;
            }
         }
      } else {
         if (!bShowedUI) {
            bShowedUI = true;
            IsoPlayer.instance = null;
            IsoPlayer.players[0] = null;
            UIManager.UI.clear();
            LuaEventManager.Reset();
            LuaManager.call("ISGameLoadingUI_OnGameLoadingUI", "");
            UIManager.bSuspend = false;
         }

         if (Keyboard.isKeyDown(1)) {
            GameClient.instance.Shutdown();
            SteamUtils.shutdown();
            System.exit(1);
         }

         return GameStateMachine.StateAction.Remain;
      }
   }
}
