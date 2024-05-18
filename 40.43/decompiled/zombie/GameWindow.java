package zombie;

import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundBank;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import sun.nio.cs.ArrayDecoder;
import sun.nio.cs.ArrayEncoder;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.audio.BaseSoundBank;
import zombie.audio.DummySoundBank;
import zombie.characters.IsoPlayer;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.Core;
import zombie.core.Language;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.input.Input;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.ZipLogs;
import zombie.core.opengl.RenderThread;
import zombie.core.physics.Bullet;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.raknet.VoiceManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.textures.TexturePackPage;
import zombie.core.znet.ServerBrowser;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.gameStates.AlphaWarningState;
import zombie.gameStates.GameLoadingState;
import zombie.gameStates.GameStateMachine;
import zombie.gameStates.MainScreenState;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LightingThread;
import zombie.iso.SliceY;
import zombie.iso.Vector2;
import zombie.iso.WorldStreamer;
import zombie.network.CoopMaster;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.scripting.ScriptManager;
import zombie.spnetwork.SinglePlayerClient;
import zombie.spnetwork.SinglePlayerServer;
import zombie.ui.TextManager;
import zombie.ui.UIManager;
import zombie.util.PublicServerUtil;
import zombie.vehicles.PolygonalMap2;

public class GameWindow {
   public static Input GameInput = new Input(1);
   static int PauseKeyDebounce = 0;
   public static int ActiveController = -1;
   public static int XLAxis = 0;
   public static int YLAxis = 0;
   public static int XRAxis = 0;
   public static int YRAxis = 0;
   public static boolean flipX = false;
   public static boolean flipY = false;
   public static boolean bFlipXY = false;
   public static boolean DEBUG_SAVE = false;
   private static boolean doRenderEvent = false;
   public static boolean OkToSaveOnExit = false;
   private static final int FRAMERATE = 60;
   public static String lastU = null;
   public static String lastP = null;
   public static String lastK = null;
   public static final String GAME_TITLE = "Project Zomboid";
   public static GameStateMachine states = new GameStateMachine();
   public static LineDrawer debugLine = new LineDrawer();
   public static boolean bDrawMouse = true;
   static Texture MousePointer;
   private static AlphaWarningState AlphaWarningState = null;
   public static boolean bServerDisconnected;
   public static boolean bLoadedAsClient = false;
   public static String kickReason;
   public static boolean DrawReloadingLua = false;
   private static String CacheDir = null;
   private static boolean finished;
   static boolean keyDown;
   public static JoypadManager.Joypad ActivatedJoyPad = null;
   public static boolean DoFrame = true;
   public static String version = "RC3";
   public static String UpDownR = "Y Rotation";
   public static String LeftRightR = "X Rotation";
   public static String UpDown = "Y Axis";
   public static String LeftRight = "X Axis";
   public static String TriggerL = "Z Axis";
   static int q = 0;
   static long lastFrame = 0L;
   static long lastFPS = 0L;
   public static double average10 = 0.0D;
   static float total10 = 0.0F;
   static long[] last10 = new long[200];
   static int last10index = 0;
   static long fps = 0L;
   static long last = 0L;
   private static int NUM_FPS = 10;
   public static volatile boolean closeRequested;
   private static int FPS = 60;
   private static long period;
   private static boolean running;
   private static int MAX_FRAME_SKIPS;
   private static final int NO_DELAYS_PER_YIELD = 20;
   public static float averageFPS;
   private static final ThreadLocal stringUTF;

   public static void initApplet() throws Exception {
      Vector2 var0 = new Vector2();
      var0.x = 0.5F;
      var0.y = 1.0F;
      var0.normalize();
      float var1 = var0.getLength();
      float var2 = var0.x / 2.0F;
      initShared();
   }

   public static void initShared() throws Exception {
      String var0 = getCacheDir() + File.separator;
      File var1 = new File(var0);
      if (!var1.exists()) {
         var1.mkdirs();
      }

      var0 = var0 + "2133243254543.log";
      TextureID.bUseCompression = false;
      TexturePackPage.bIgnoreWorldItemTextures = true;
      LoadTexturePack("UI");
      LoadTexturePack("UI2");
      LoadTexturePack("IconsMoveables");
      LoadTexturePack("RadioIcons");
      LoadTexturePack("ApComUI");
      LoadTexturePack("Mechanics");
      LoadTexturePack("WeatherFx");
      ScriptManager.instance.Load();
      IsoGridSquare.TotalChecksum = IsoGridSquare.Checksum;
      IndieLogger.init();
      IndieLogger.Log("PZ Debug Logging started");
      TraitFactory.init();
      ProfessionFactory.init();
      PerkFactory.init();
      Rand.init();
      TexturePackPage.bIgnoreWorldItemTextures = false;
      TextureID.bUseCompression = TextureID.bUseCompressionOption;
      TextManager.instance.Init();
      IndieLogger.Log("Initialised TextManager");
      MousePointer = Texture.getSharedTexture("media/ui/mouseArrow.png");
      AlphaWarningState = new AlphaWarningState();
      if (!GameClient.bClient) {
         states.States.add(AlphaWarningState);
      }

      if (GameClient.bClient) {
         states.States.add(new GameLoadingState());
      } else {
         states.States.add(new MainScreenState());
      }

      states.LoopToState = 1;
      GameInput.initControllers();
      int var2 = GameInput.getControllerCount();
      var1 = new File(getCacheDir() + "\\joypad.ini");
      if (var1.exists()) {
         BufferedReader var3 = new BufferedReader(new FileReader(var1.getAbsolutePath()));

         try {
            String var4 = "";

            while(var4 != null) {
               var4 = var3.readLine();
               if (var4 != null && var4.trim().length() != 0) {
                  String[] var5 = var4.split("=");
                  if (var5.length == 2) {
                     var5[0] = var5[0].trim();
                     var5[1] = var5[1].trim();
                     if (var5[0].equals("LUpDown")) {
                        UpDown = var5[1];
                     }

                     if (var5[0].equals("RLeftRight")) {
                        LeftRightR = var5[1];
                     }

                     if (var5[0].equals("RUpDown")) {
                        UpDownR = var5[1];
                     }

                     if (var5[0].equals("LLeftRight")) {
                        LeftRight = var5[1];
                     }

                     if (var5[0].equals("Triggers")) {
                        TriggerL = var5[1];
                     }
                  }
               }
            }
         } finally {
            var3.close();
         }
      }

      DebugLog.log("----------------------------------------------");
      DebugLog.log("--    Controller setup - use this info to     ");
      DebugLog.log("--    edit joypad.ini in save directory       ");
      DebugLog.log("----------------------------------------------");

      for(int var9 = 0; var9 < var2; ++var9) {
         DebugLog.log("----------------------------------------------");
         DebugLog.log("--  Joypad: " + GameInput.getController(var9).getName());
         DebugLog.log("----------------------------------------------");
         int var10 = GameInput.getAxisCount(var9);
         String var6;
         int var11;
         if (var10 > 1) {
            DebugLog.log("----------------------------------------------");
            DebugLog.log("--    Axis definitions for controller " + var9);
            DebugLog.log("----------------------------------------------");
            ActiveController = var9;

            for(var11 = 0; var11 < var10; ++var11) {
               var6 = GameInput.getAxisName(var9, var11);
               DebugLog.log("Testing for axis: " + var6);
               if (var6.equals(UpDown)) {
                  YLAxis = var11;
               }

               if (var6.equals(LeftRight)) {
                  XLAxis = var11;
               }

               if (var6.equals(LeftRightR)) {
                  XRAxis = var11;
               }

               if (var6.equals(UpDownR)) {
                  YRAxis = var11;
               }
            }
         }

         var10 = GameInput.getButtonCount(var9);
         if (var10 > 1) {
            GameInput.getController(var9).poll();
            DebugLog.log("----------------------------------------------");
            DebugLog.log("--    Button definitions for controller " + var9);
            DebugLog.log("----------------------------------------------");

            for(var11 = 0; var11 < var10; ++var11) {
               var6 = GameInput.getButtonName(var9, var11);
               DebugLog.log("Testing for button: " + var6);
            }
         }
      }

      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      TextureID.UseFiltering = false;
      Texture.getSharedTexture("media/white.png");
      TextureID.UseFiltering = true;
      SpriteRenderer.instance = new SpriteRenderer();
      SpriteRenderer.instance.create();
   }

   /** @deprecated */
   @Deprecated
   public static void initSharedServer() throws Exception {
      String var0 = getCacheDir() + File.separator;
      File var1 = new File(var0);
      if (!var1.exists()) {
         var1.mkdirs();
      }

      var0 = var0 + "2133243254543.log";
      IndieLogger.init();
      IndieLogger.Log("PZ Debug Logging started");
      TraitFactory.init();
      ProfessionFactory.init();
      PerkFactory.init();
      Rand.init();
      MousePointer = Texture.getSharedTexture("media/ui/mouseArrow.png");
      states.LoopToState = 1;
   }

   public static void logic() {
      if (GameClient.bClient) {
         try {
            GameClient.instance.update();
         } catch (Exception var6) {
            ExceptionLogger.logException(var6);
         }
      }

      try {
         SinglePlayerServer.update();
         SinglePlayerClient.update();
      } catch (Throwable var5) {
         ExceptionLogger.logException(var5);
      }

      SteamUtils.runLoop();
      Mouse.update();
      GameKeyboard.update();
      if (CoopMaster.instance != null) {
         CoopMaster.instance.update();
      }

      if (IsoPlayer.players[0] != null) {
         IsoPlayer.instance = IsoPlayer.players[0];
         IsoCamera.CamCharacter = IsoPlayer.players[0];
      }

      UIManager.update();
      VoiceManager.instance.update();
      LineDrawer var10000 = debugLine;
      LineDrawer.clear();
      if (!FrameLoader.bDedicated) {
         GameInput.poll(Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0));
      }

      if (!FrameLoader.bDedicated && JoypadManager.instance.isAPressed(-1)) {
         for(int var0 = 0; var0 < JoypadManager.instance.JoypadList.size(); ++var0) {
            JoypadManager.Joypad var1 = (JoypadManager.Joypad)JoypadManager.instance.JoypadList.get(var0);
            if (var1.isAPressed()) {
               if (ActivatedJoyPad == null) {
                  ActivatedJoyPad = var1;
               }

               if (IsoPlayer.instance != null) {
                  LuaEventManager.triggerEvent("OnJoypadActivate", var1.getID());
               } else {
                  AlphaWarningState.messageTime = 0.0F;
                  LuaEventManager.triggerEvent("OnJoypadActivateUI", var1.getID());
               }
               break;
            }
         }
      }

      if (!FrameLoader.bDedicated) {
         SoundManager.instance.Update();
      }

      boolean var7 = true;
      if (GameClient.IsClientPaused()) {
         var7 = false;
      }

      if (UIManager.getSpeedControls() != null && UIManager.getSpeedControls().getCurrentGameSpeed() == 0) {
         var7 = false;
      }

      MapCollisionData.instance.updateGameState();
      long var8 = System.nanoTime();
      if (var7) {
         states.update();
      }

      long var3 = System.nanoTime();
      if (!var7 && IsoPlayer.instance != null) {
         IsoCamera.update();
      }

      if (GameLoadingState.bDone) {
         LuaEventManager.triggerEvent("OnTickEvenPaused", 0.0D);
      }

      UIManager.resize();
   }

   public static void render() {
      if (!PerformanceSettings.LightingThread) {
         try {
            GameTime.getInstance().lightingUpdate();
            boolean var0 = true;

            for(int var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
               if (IsoPlayer.players[var1] != null && !LightingThread.instance.DoLightingUpdate(IsoWorld.instance.CurrentCell, var1)) {
                  var0 = false;
                  break;
               }
            }

            if (var0) {
               IsoCell.bReadAltLight = !IsoCell.bReadAltLight;
            }
         } catch (InterruptedException var2) {
         }
      }

      ++IsoCamera.frameState.frameCount;
      IsoObjectPicker.Instance.StartRender();
      long var3 = System.nanoTime();
      states.render();
   }

   public static void mainServer() {
      LuaManager.init();

      try {
         LuaManager.LoadDirBase();
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      ZomboidGlobals.Load();
      GameServer.bServer = true;
      FrameLoader.bDedicated = true;

      try {
         initserver();
      } catch (Exception var1) {
         var1.printStackTrace();
      }

      runserver();
   }

   public static void mainaa(String[] var0) throws Exception {
      ZomboidFileSystem var1 = ZomboidFileSystem.instance;
      var1.init();
      String var2 = System.getProperty("server");
      String var3 = System.getProperty("client");
      String var4 = System.getProperty("fullscreen");
      String var5 = System.getProperty("debug");
      String var6 = System.getProperty("xres");
      String var7 = System.getProperty("yres");
      if (var4 != null) {
         FrameLoader.bFullscreen = true;
      }

      if (var5 != null) {
         Core.bDebug = true;
      }

      if (var6 != null) {
         FrameLoader.FullX = Integer.parseInt(var6);
      }

      if (var7 != null) {
         FrameLoader.FullY = Integer.parseInt(var7);
      }

      String var8 = System.getProperty("graphiclevel");
      if (var8 != null) {
         Core.getInstance().nGraphicLevel = Integer.parseInt(var8);
      }

      if (var2 != null && var2.equals("true")) {
         GameServer.bServer = true;
      }

      if (var3 != null) {
         FrameLoader.IP = var3;
      }

      if (GameServer.bServer) {
      }

      int var9 = Display.getDesktopDisplayMode().getWidth();
      int var10 = Display.getDesktopDisplayMode().getHeight();
      Preferences var11 = Preferences.userNodeForPackage(FrameLoader.class);
      Display.setResizable(true);
      init(false);
      run();
      System.exit(0);
   }

   public static void maina(boolean var0, int var1, int var2, int var3) throws Exception {
      String var4 = System.getProperty("debug");
      String var5 = System.getProperty("nosave");
      if (var5 != null) {
         Core.getInstance().setNoSave(true);
      }

      if (var4 != null) {
         Core.bDebug = true;
      }

      if (!Core.SoundDisabled) {
         FMODManager.instance.init();
      }

      DebugOptions.instance.load();
      SoundManager.instance = (BaseSoundManager)(Core.SoundDisabled ? new DummySoundManager() : new SoundManager());
      AmbientStreamManager.instance = (BaseAmbientStreamManager)(Core.SoundDisabled ? new DummyAmbientStreamManager() : new AmbientStreamManager());
      BaseSoundBank.instance = (BaseSoundBank)(Core.SoundDisabled ? new DummySoundBank() : new FMODSoundBank());
      Core.getInstance().nGraphicLevel = var3;
      int var6;
      if (!Core.getInstance().loadOptions()) {
         var6 = Runtime.getRuntime().availableProcessors();
         if (var6 == 1) {
            PerformanceSettings.LightingFrameSkip = 3;
         } else if (var6 == 2) {
            PerformanceSettings.LightingFrameSkip = 2;
         } else if (var6 <= 4) {
            PerformanceSettings.LightingFrameSkip = 1;
         }

         Display.setFullscreen(false);
         Display.setResizable(false);
         if (Display.getDesktopDisplayMode().getWidth() > 1280 && Display.getDesktopDisplayMode().getHeight() > 1080) {
            FrameLoader.FullX = 1280;
            FrameLoader.FullY = 720;
            Core.getInstance().init(1280, 720);
            Core.getInstance().saveOptions();
         } else {
            Core.getInstance().init(Core.width, Core.height);
         }

         if (!GLContext.getCapabilities().GL_ATI_meminfo && !GLContext.getCapabilities().GL_NVX_gpu_memory_info) {
            DebugLog.log("Unable to determine available GPU memory, texture compression defaults to on");
            TextureID.bUseCompressionOption = true;
            TextureID.bUseCompression = true;
         }

         DebugLog.log("Init language : " + System.getProperty("user.language"));
         Translator.setLanguage(Language.FromString(System.getProperty("user.language").toUpperCase()));
      } else {
         Core.getInstance().init(Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight());
      }

      VoiceManager.instance.loadConfig();
      TextureID.bUseCompressionOption = Core.SafeModeForced || Core.getInstance().getOptionTextureCompression();
      TextureID.bUseCompression = TextureID.bUseCompressionOption;
      if (GLContext.getCapabilities().GL_ATI_meminfo) {
         var6 = GL11.glGetInteger(34812);
         DebugLog.log("ATI: available texture memory is " + var6 / 1024 + " MB");
      }

      if (GLContext.getCapabilities().GL_NVX_gpu_memory_info) {
         var6 = GL11.glGetInteger(36937);
         DebugLog.log("NVIDIA: current available GPU memory is " + var6 / 1024 + " MB");
         var6 = GL11.glGetInteger(36935);
         DebugLog.log("NVIDIA: dedicated available GPU memory is " + var6 / 1024 + " MB");
         var6 = GL11.glGetInteger(36936);
         DebugLog.log("NVIDIA: total available GPU memory is " + var6 / 1024 + " MB");
      }

      SoundManager.instance.setSoundVolume((float)Core.getInstance().getOptionSoundVolume() / 10.0F);
      SoundManager.instance.setMusicVolume((float)Core.getInstance().getOptionMusicVolume() / 10.0F);
      SoundManager.instance.setAmbientVolume((float)Core.getInstance().getOptionAmbientVolume() / 10.0F);
      SoundManager.instance.setVehicleEngineVolume((float)Core.getInstance().getOptionVehicleEngineVolume() / 10.0F);
      ZomboidFileSystem.instance.init();
      String var12 = System.getProperty("server");
      String var7 = System.getProperty("client");
      FrameLoader.bFullscreen = var0;
      String var8 = System.getProperty("nozombies");
      if (var8 != null) {
         IsoWorld.NoZombies = true;
      }

      FrameLoader.FullX = var1;
      FrameLoader.FullY = var2;
      if (var12 != null && var12.equals("true")) {
         GameServer.bServer = true;
      }

      if (var7 != null) {
      }

      if (GameServer.bServer) {
      }

      int var9 = Display.getDesktopDisplayMode().getWidth();
      int var10 = Display.getDesktopDisplayMode().getHeight();
      Preferences var11 = Preferences.userNodeForPackage(FrameLoader.class);
      init(false);
      run();
      ZipLogs.addZipFile(true);
      System.exit(0);
   }

   public static long getTime() {
      return System.nanoTime() / 1000000L;
   }

   public int getDelta() {
      long var1 = getTime();
      int var3 = (int)(var1 - lastFrame);
      lastFrame = var1;
      return var3;
   }

   public static void start() {
      lastFPS = getTime();
   }

   public static void updateFPS(long var0) {
      last10[last10index++] = var0;
      if (last10index >= 5) {
         last10index = 0;
      }

      average10 = 0.0D;
      long var4 = 0L;
      long var6 = 0L;
      float var8 = 11110.0F;
      float var9 = -11110.0F;

      for(int var10 = 0; var10 < 50; ++var10) {
         if (last10[var10] != 0L) {
            ++var6;
            var4 += last10[var10];
            if ((float)last10[var10] < var8) {
               var8 = (float)last10[var10];
            }

            if ((float)last10[var10] > var9) {
               var9 = (float)last10[var10];
            }
         }
      }

      if (var6 > 0L) {
         average10 = (double)((float)(var4 / var6));
      } else {
         average10 = 0.01666666753590107D;
      }

      GameTime.instance.FPSMultiplier2 = 1.0F;
      fps = 0L;
      ++fps;
   }

   public static long readLong(DataInputStream var0) throws EOFException, IOException {
      int var1 = var0.read();
      int var2 = var0.read();
      int var3 = var0.read();
      int var4 = var0.read();
      int var5 = var0.read();
      int var6 = var0.read();
      int var7 = var0.read();
      int var8 = var0.read();
      if ((var1 | var2 | var3 | var4 | var5 | var6 | var7 | var8) < 0) {
         throw new EOFException();
      } else {
         return (long)((var1 << 0) + (var2 << 8) + (var3 << 16) + (var4 << 24) + (var5 << 32) + (var6 << 40) + (var7 << 48) + (var8 << 56));
      }
   }

   public static int readInt(DataInputStream var0) throws EOFException, IOException {
      int var1 = var0.read();
      int var2 = var0.read();
      int var3 = var0.read();
      int var4 = var0.read();
      if ((var1 | var2 | var3 | var4) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 0) + (var2 << 8) + (var3 << 16) + (var4 << 24);
      }
   }

   public static void run() {
      long var8 = 0L;
      int var10 = 0;
      long var11 = 0L;
      Core.TileScale = Core.getInstance().getOptionTexture2x() ? 2 : 1;
      if (Core.SafeModeForced) {
         Core.TileScale = 1;
      }

      IsoCamera.init();
      if (Core.TileScale == 1) {
         LoadTexturePack("Tiles1x");
         LoadTexturePack("JumboTrees1x");
         TextureID.bUseCompression = false;
         LoadTexturePack("Tiles1x.floor");
         TextureID.bUseCompression = TextureID.bUseCompressionOption;
      }

      if (Core.TileScale == 2) {
         LoadTexturePack("Tiles2x");
         LoadTexturePack("JumboTrees2x");
         TextureID.bUseCompression = false;
         LoadTexturePack("Tiles2x.floor");
         TextureID.bUseCompression = TextureID.bUseCompressionOption;
      }

      LoadTexturePackDDS("Characters");
      if (Texture.getSharedTexture("TileIndieStoneTentFrontLeft") == null) {
         throw new RuntimeException("Rebuild Tiles.pack with \"1 Include This in .pack\" as individual images not tilesheets");
      } else {
         DebugLog.log("LOADED UP A TOTAL OF " + Texture.totalTextureID + " TEXTURES");
         if (!SteamUtils.isSteamModeEnabled()) {
            PublicServerUtil.init();
         }

         VoiceManager.instance.InitVMClient();
         LuaEventManager.triggerEvent("OnGameBoot");
         boolean var13 = false;
         start();
         RenderThread.init();
         float var14 = 0.0F;
         long var0 = System.nanoTime();
         float[] var15 = new float[20];
         int var16 = 0;

         for(int var17 = 0; var17 < 20; ++var17) {
            var15[var17] = (float)PerformanceSettings.LockFPS;
         }

         while(!Display.isCloseRequested() && !closeRequested) {
            long var42 = System.nanoTime();
            period = 1000000000L / (long)PerformanceSettings.LockFPS;

            try {
               var11 = 0L;
               logic();
               long var19 = System.nanoTime();
               Core.getInstance().setScreenSize(Display.getWidth(), Display.getHeight());
               long var21 = System.nanoTime();
               render();
               if (doRenderEvent) {
                  LuaEventManager.triggerEvent("OnRenderTick");
               }

               Core.getInstance().DoFrameReady();
               long var23 = System.nanoTime();
               LightingThread.instance.update();
               long var25 = System.nanoTime();
               if (Core.bDebug && Keyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger")) && !var13) {
                  LuaManager.thread.bStep = true;
                  LuaManager.thread.bStepInto = true;
                  var13 = true;
               }

               if (!Keyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
                  var13 = false;
               }

               long var2 = System.nanoTime();
               long var4 = var2 - var0;
               updateFPS(var4);
               long var6 = period - var4 - var8;
               if (var6 > 0L && PerformanceSettings.LockFPS != 61) {
                  try {
                     Thread.sleep(var6 / 1000000L);
                  } catch (InterruptedException var40) {
                  }

                  var8 = System.nanoTime() - var2 - var6;
               } else {
                  var11 -= var6;
                  var8 = 0L;
                  ++var10;
                  if (var10 >= 20) {
                     Thread.yield();
                     var10 = 0;
                  }
               }

               var0 = System.nanoTime();
               int var27 = 0;
               if (PerformanceSettings.AutomaticFrameSkipping) {
                  while(var11 > period && var27 < PerformanceSettings.MaxAutomaticFrameSkips) {
                     var11 -= period;
                     logic();
                     ++var27;
                  }
               } else if (PerformanceSettings.ManualFrameSkips > 0) {
                  for(int var28 = 0; var28 < PerformanceSettings.ManualFrameSkips; ++var28) {
                     var11 -= period;
                     logic();
                  }
               }

               long var43 = System.nanoTime();
               float var30 = (float)(var43 - var42) / 1000000.0F;
               if (var30 > 0.0F) {
                  averageFPS = 0.0F;
                  float var31 = 1000.0F / (float)PerformanceSettings.LockFPS / var30;
                  var14 = var31 * (float)PerformanceSettings.LockFPS;
                  var15[var16] = var14;
                  ++var16;
                  if (var16 >= 5) {
                     var16 = 0;
                  }

                  for(int var32 = 0; var32 < 5; ++var32) {
                     averageFPS += var15[var32];
                  }

                  averageFPS /= 5.0F;
                  GameTime.instance.FPSMultiplier = 60.0F / var14;
               }

               long var44 = System.nanoTime();
               long var10000 = var44 - var42;
            } catch (Exception var41) {
               Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var41);
            }
         }

         if (GameClient.bClient) {
            WorldStreamer.instance.stop();
            GameClient.instance.doDisconnect("Quitting");
            VoiceManager.instance.DeinitVMClient();
         }

         if (OkToSaveOnExit) {
            try {
               WorldStreamer.instance.quit();
            } catch (Exception var39) {
               var39.printStackTrace();
            }

            try {
               if (GameClient.bClient && GameClient.connection != null) {
                  GameClient.connection.username = null;
               }

               save(true);
            } catch (Exception var38) {
               var38.printStackTrace();
            }

            try {
               if (IsoWorld.instance.CurrentCell != null) {
                  LuaEventManager.triggerEvent("OnPostSave");
               }
            } catch (Exception var37) {
               var37.printStackTrace();
            }

            try {
               LightingThread.instance.stop();
               MapCollisionData.instance.stop();
               ZombiePopulationManager.instance.stop();
               PolygonalMap2.instance.stop();
            } catch (Exception var36) {
               var36.printStackTrace();
            }
         }

         if (GameClient.bClient) {
            WorldStreamer.instance.stop();
            GameClient.instance.doDisconnect("Quitting");

            try {
               Thread.sleep(500L);
            } catch (InterruptedException var35) {
               var35.printStackTrace();
            }
         }

         RenderThread.borrowContext();
         Display.destroy();
         GameClient.instance.Shutdown();
         SteamUtils.shutdown();
         ZipLogs.addZipFile(true);
         System.exit(0);
      }
   }

   public static void runserver() {
      doServer();
      LoadTexturePack("Tiles");
      LuaEventManager.triggerEvent("OnGameBoot");
      start();

      while(true) {
         try {
            logic();
            Thread.sleep(16L);
         } catch (Exception var3) {
            JOptionPane.showMessageDialog((Component)null, var3.getStackTrace(), "Error: " + var3.getMessage(), 0);
            Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var3);

            try {
               save(true);
            } catch (FileNotFoundException var1) {
               Logger.getLogger(FrameLoader.class.getName()).log(Level.SEVERE, (String)null, var1);
            } catch (IOException var2) {
               Logger.getLogger(FrameLoader.class.getName()).log(Level.SEVERE, (String)null, var2);
            }

            Display.destroy();
            System.exit(0);
            return;
         }
      }
   }

   private static void doServer() {
   }

   public static TexturePackPage LoadTexturePack(String var0) {
      DebugLog.log("texturepack: loading " + var0);
      if (SpriteRenderer.instance != null && RenderThread.RenderThread == null) {
         Core.getInstance().StartFrame();
         Core.getInstance().EndFrame(0);
         Core.getInstance().StartFrameUI();
         SpriteRenderer.instance.render((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F);
         TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), Translator.getText("UI_Loading_Texturepack", var0), 1.0D, 1.0D, 1.0D, 1.0D);
         Core.getInstance().EndFrameUI();
      }

      FileInputStream var1 = null;

      try {
         var1 = new FileInputStream(ZomboidFileSystem.instance.getString("media/texturepacks/" + var0 + ".pack"));
      } catch (FileNotFoundException var16) {
         Logger.getLogger(GameLoadingState.class.getName()).log(Level.SEVERE, (String)null, var16);
         return null;
      }

      BufferedInputStream var2 = new BufferedInputStream(var1);

      try {
         RenderThread.borrowContext();
         int var3 = TexturePackPage.readInt(var2);
         TexturePackPage var4 = null;

         for(int var5 = 0; var5 < var3; ++var5) {
            if (var5 == 3716) {
               boolean var6 = false;
            }

            var4 = new TexturePackPage();
            if (var5 % 100 == 0 && SpriteRenderer.instance != null && RenderThread.RenderThread == null) {
               Core.getInstance().StartFrame();
               Core.getInstance().EndFrame();
               Core.getInstance().StartFrameUI();
               TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), Translator.getText("UI_Loading_Texturepack", var0), 1.0D, 1.0D, 1.0D, 1.0D);
               Core.getInstance().EndFrameUI();
               Display.update();
            }

            var4.loadFromPackFile(var2);
            if (var5 % 5 == 0) {
               Display.processMessages();
            }
         }

         DebugLog.log("texturepack: finished loading " + var0);
         TexturePackPage var19 = var4;
         return var19;
      } catch (Exception var17) {
         var17.printStackTrace();
      } finally {
         try {
            var2.close();
         } catch (Exception var15) {
         }

         RenderThread.returnContext();
      }

      Texture.nullTextures.clear();
      return null;
   }

   public static void LoadTexturePackDDS(String var0) {
      DebugLog.log("texturepack: loading " + var0);
      if (SpriteRenderer.instance != null) {
         Core.getInstance().StartFrame();
         Core.getInstance().EndFrame(0);
         Core.getInstance().StartFrameUI();
         SpriteRenderer.instance.render((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F);
         TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), Translator.getText("UI_Loading_Texturepack", var0), 1.0D, 1.0D, 1.0D, 1.0D);
         Core.getInstance().EndFrameUI();
      }

      FileInputStream var1 = null;

      try {
         var1 = new FileInputStream(ZomboidFileSystem.instance.getString("media/texturepacks/" + var0 + ".pack"));
      } catch (FileNotFoundException var15) {
         Logger.getLogger(GameLoadingState.class.getName()).log(Level.SEVERE, (String)null, var15);
      }

      BufferedInputStream var2 = new BufferedInputStream(var1);

      try {
         int var3 = TexturePackPage.readInt(var2);

         for(int var4 = 0; var4 < var3; ++var4) {
            if (var4 == 3716) {
               boolean var5 = false;
            }

            TexturePackPage var18 = new TexturePackPage();
            if (var4 % 100 == 0 && SpriteRenderer.instance != null) {
               Core.getInstance().StartFrame();
               Core.getInstance().EndFrame();
               Core.getInstance().StartFrameUI();
               TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), Translator.getText("UI_Loading_Texturepack", var0), 1.0D, 1.0D, 1.0D, 1.0D);
               Core.getInstance().EndFrameUI();
               Display.update();
            }

            var18.loadFromPackFileDDS(var2);
         }

         DebugLog.log("texturepack: finished loading " + var0);
      } catch (Exception var16) {
         DebugLog.log("media/texturepacks/" + var0 + ".pack");
         var16.printStackTrace();
      } finally {
         try {
            var2.close();
         } catch (Exception var14) {
         }

      }

      Texture.nullTextures.clear();
   }

   private static void cleanup() {
      Display.destroy();
      AL.destroy();
   }

   private static void installRequiredLibrary(String var0, String var1) {
      if ((new File(var0)).exists()) {
         DebugLog.log("Attempting to install " + var1);
         DebugLog.log("Running " + var0 + ".");
         ProcessBuilder var2 = new ProcessBuilder(new String[]{var0, "/quiet", "/norestart"});

         try {
            Process var3 = var2.start();
            int var4 = var3.waitFor();
            DebugLog.log("Process exited with code " + var4);
            return;
         } catch (IOException var5) {
            var5.printStackTrace();
         } catch (InterruptedException var6) {
            var6.printStackTrace();
         }
      }

      DebugLog.log("Please install " + var1);
   }

   private static void checkRequiredLibraries() {
      if (System.getProperty("os.name").startsWith("Win")) {
         String var0;
         String var1;
         String var2;
         String var3;
         if (System.getProperty("sun.arch.data.model").equals("64")) {
            var0 = "Lighting64";
            var1 = "_CommonRedist\\vcredist\\2010\\vcredist_x64.exe";
            var2 = "_CommonRedist\\vcredist\\2012\\vcredist_x64.exe";
            var3 = "_CommonRedist\\vcredist\\2013\\vcredist_x64.exe";
         } else {
            var0 = "Lighting32";
            var1 = "_CommonRedist\\vcredist\\2010\\vcredist_x86.exe";
            var2 = "_CommonRedist\\vcredist\\2012\\vcredist_x86.exe";
            var3 = "_CommonRedist\\vcredist\\2013\\vcredist_x86.exe";
         }

         try {
            System.loadLibrary(var0);
         } catch (UnsatisfiedLinkError var5) {
            DebugLog.log("Error loading " + var0 + ".dll.  Your system may be missing a required DLL.");
            installRequiredLibrary(var1, "the Microsoft Visual C++ 2010 Redistributable.");
            installRequiredLibrary(var2, "the Microsoft Visual C++ 2012 Redistributable.");
            installRequiredLibrary(var3, "the Microsoft Visual C++ 2013 Redistributable.");
         }
      }

   }

   private static void init(boolean var0) throws Exception {
      checkRequiredLibraries();
      SteamUtils.init();
      ServerBrowser.init();
      SteamFriends.init();
      SteamWorkshop.init();
      RakNetPeerInterface.init();
      LightingJNI.init();
      ZombiePopulationManager.init();
      Bullet.init();
      Display.setTitle("Project Zomboid");
      int var1 = Runtime.getRuntime().availableProcessors();
      if (Core.bMultithreadedRendering) {
         Core.bMultithreadedRendering = var1 > 1;
      }

      if (Core.SafeMode) {
      }

      String var2 = getCacheDir() + File.separator;
      File var3 = new File(var2);
      if (!var3.exists()) {
         var3.mkdirs();
      }

      var2 = var2 + "2133243254543.log";
      new File(var2);
      ZomboidFileSystem.instance.loadMods();
      Translator.loadFiles();
      initShared();
      DoLoadingText(Translator.getText("UI_Loading_Lua"));
      LuaManager.init();
      LuaManager.LoadDirBase();
      ZomboidGlobals.Load();
      LuaEventManager.triggerEvent("OnLoadSoundBanks");
   }

   private static void initserver() throws Exception {
      String var0 = getCacheDir() + File.separator;
      File var1 = new File(var0);
      if (!var1.exists()) {
         var1.mkdirs();
      }

      var0 = var0 + "2133243254543.log";
      new File(var0);
      initSharedServer();
   }

   public static void save(boolean var0) throws FileNotFoundException, IOException {
      save(var0, true);
   }

   public static void savePlayer() {
      try {
         IsoWorld.instance.CurrentCell.savePlayer();

         for(int var0 = 1; var0 < IsoPlayer.numPlayers; ++var0) {
            IsoPlayer var1 = IsoPlayer.players[var0];
            if (var1 != null && !var1.isDead()) {
               String var2 = var1.SaveFileName;
               if (var2 == null) {
                  var2 = IsoPlayer.getUniqueFileName();
               }

               var1.save(var2);
            }
         }
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   public static void save(boolean var0, boolean var1) throws FileNotFoundException, IOException {
      if (!Core.getInstance().isNoSave()) {
         if (IsoWorld.instance.CurrentCell != null && !"LastStand".equals(Core.getInstance().getGameMode()) && !"Tutorial".equals(Core.getInstance().getGameMode())) {
            File var2 = new File(getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_ver.bin");
            FileOutputStream var3 = new FileOutputStream(var2);
            DataOutputStream var4 = new DataOutputStream(var3);
            IsoWorld var10000 = IsoWorld.instance;
            short var5 = 143;
            var4.writeInt(var5);
            WriteString(var4, Core.GameMap);
            WriteString(var4, IsoWorld.instance.getDifficulty());
            var4.flush();
            var4.close();
            var2 = new File(getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_sand.bin");
            var3 = new FileOutputStream(var2);
            BufferedOutputStream var13 = new BufferedOutputStream(var3);
            if (SliceY.SliceBuffer == null) {
               SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
            }

            SliceY.SliceBuffer.rewind();
            SandboxOptions.instance.save(SliceY.SliceBuffer);
            var13.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
            var13.flush();
            var13.close();
            LuaEventManager.triggerEvent("OnSave");
            RenderThread.borrowContext();
            if (!FrameLoader.bDedicated) {
               Core.getInstance().TakeScreenshot();
            }

            var2 = new File(getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map.bin");
            var3 = null;
            var3 = new FileOutputStream(var2);

            try {
               var4 = new DataOutputStream(var3);
               IsoWorld.instance.CurrentCell.save(var4, var0);
               var3.close();
            } catch (Exception var11) {
               Logger.getLogger(FrameLoader.class.getName()).log(Level.SEVERE, (String)null, var11);
            } finally {
               var3.close();
            }

            try {
               MapCollisionData.instance.save();
               if (!bLoadedAsClient) {
                  SGlobalObjects.save();
               }
            } catch (Exception var10) {
               var10.printStackTrace();
            }

            ZomboidRadio.getInstance().Save();
            RenderThread.returnContext();
         }
      }
   }

   private static boolean validateUser(String var0, String var1, String var2) throws MalformedURLException, IOException {
      String var3 = null;

      try {
         URL var4 = new URL("http://127.0.0.1/external/games/projectzomboid.php");
         URLConnection var10 = var4.openConnection();
         InputStream var11 = var10.getInputStream();
         byte[] var12 = new byte[7];
         var11.read(var12);
         if (var12[0] != 115 || var12[1] != 117 || var12[2] != 99 || var12[3] != 99 || var12[4] != 101) {
            throw new NullPointerException(var3);
         }

         var11.close();
      } catch (Exception var9) {
         if (var0 != null && !var0.isEmpty()) {
            var3 = "http://www.desura.com/external/games/projectzomboid.php?username=" + var0 + "&password=" + var1;
         } else {
            var3 = "http://www.desura.com/external/games/projectzomboid.php?cdkey=" + var2;
         }

         URL var5 = new URL(var3);
         URLConnection var6 = var5.openConnection();
         BufferedReader var7 = new BufferedReader(new InputStreamReader(var6.getInputStream()));

         String var8;
         while((var8 = var7.readLine()) != null) {
            if (var8.contains("success")) {
               return true;
            }
         }
      }

      return false;
   }

   public static void setCacheDir(String var0) {
      var0 = var0.replace("/", File.separator);
      DebugLog.log("cachedir set to \"" + var0 + "\"");
      CacheDir = var0;
   }

   public static String getCacheDir() {
      if (CacheDir != null) {
         return CacheDir;
      } else {
         String var0 = System.getProperty("deployment.user.cachedir");
         if (var0 == null || System.getProperty("os.name").startsWith("Win")) {
            var0 = System.getProperty("user.home");
         }

         return var0 + File.separator + "Zomboid";
      }
   }

   public static String getSaveDir() {
      return getCacheDir() + File.separator + "Saves";
   }

   public static String getCoopServerHome() {
      File var0 = new File(getCacheDir());
      return var0.getParent();
   }

   public static void WriteString(ByteBuffer var0, String var1) {
      WriteStringUTF(var0, var1);
   }

   public static void WriteStringUTF(ByteBuffer var0, String var1) {
      try {
         ((GameWindow.StringUTF)stringUTF.get()).save(var0, var1);
      } catch (UnsupportedEncodingException var3) {
         throw new RuntimeException("Bad encoding!");
      }
   }

   public static void WriteString(DataOutputStream var0, String var1) throws IOException {
      if (var1 == null) {
         var0.writeInt(0);
      } else {
         var0.writeInt(var1.length());
         if (var1 != null && var1.length() >= 0) {
            var0.writeChars(var1);
         }

      }
   }

   public static String ReadStringUTF(ByteBuffer var0) {
      try {
         return ((GameWindow.StringUTF)stringUTF.get()).load(var0);
      } catch (UnsupportedEncodingException var2) {
         throw new RuntimeException("Bad encoding!");
      }
   }

   public static String ReadString(ByteBuffer var0) {
      return ReadStringUTF(var0);
   }

   public static String ReadString(DataInputStream var0) throws IOException {
      int var1 = var0.readInt();
      if (var1 == 0) {
         return "";
      } else if (var1 > 65536) {
         throw new RuntimeException("GameWindow.ReadString: string is too long, corrupted save?");
      } else {
         StringBuilder var2 = new StringBuilder(var1);

         for(int var3 = 0; var3 < var1; ++var3) {
            var2.append(var0.readChar());
         }

         return var2.toString();
      }
   }

   public static String getGameModeCacheDir() {
      String var0 = getSaveDir();
      if (Core.GameMode == null) {
         Core.GameMode = "Sandbox";
      }

      return Core.GameMode != null ? var0 + File.separator + Core.GameMode + File.separator : var0 + File.separator;
   }

   public static String getScreenshotDir() {
      String var0 = getCacheDir() + File.separator + "Screenshots";
      File var1 = new File(var0);
      if (!var1.exists()) {
         var1.mkdir();
      }

      return var0;
   }

   public static void doRenderEvent(boolean var0) {
      doRenderEvent = var0;
   }

   public static void DoLoadingText(String var0) {
      if (SpriteRenderer.instance != null) {
         Core.getInstance().StartFrame();
         Core.getInstance().EndFrame();
         Core.getInstance().StartFrameUI();
         SpriteRenderer.instance.render((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F);
         TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), var0, 1.0D, 1.0D, 1.0D, 1.0D);
         Core.getInstance().EndFrameUI();
      }

   }

   public static void copyFolder(File var0, File var1) throws IOException {
      String[] var2;
      String[] var3;
      int var5;
      if (var0.isDirectory()) {
         if (!var1.exists()) {
            var1.mkdirs();
         }

         var2 = var0.list();
         var3 = var2;
         int var4 = var2.length;

         for(var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            File var7 = new File(var0, var6);
            File var8 = new File(var1, var6);
            copyFolder(var7, var8);
         }
      } else {
         var2 = null;
         var3 = null;
         FileInputStream var9 = new FileInputStream(var0);
         FileOutputStream var10 = new FileOutputStream(var1);
         byte[] var11 = new byte[1024];

         while((var5 = var9.read(var11)) > 0) {
            var10.write(var11, 0, var5);
         }

         var9.close();
         var10.close();
      }

   }

   public static String copySaveDir(String var0) {
      File var1 = new File(getGameModeCacheDir() + File.separator + Core.GameSaveWorld);
      String var2 = GameClient.ip + "_" + GameClient.port + "_" + var0;
      File var3 = new File(getGameModeCacheDir() + File.separator + var2);
      if (var1.exists()) {
         if (var3.exists()) {
            return "The save " + var3.getPath() + " already exist.";
         } else {
            var3.mkdir();

            try {
               copyFolder(var1, var3);
               Core.GameSaveWorld = var2;
               return "Copied your current save " + var1.getPath() + " into " + var3.getPath();
            } catch (IOException var5) {
               var5.printStackTrace();
               return "An error occured while copying your save: " + var1.getPath() + " into " + var3.getPath();
            }
         }
      } else {
         return "Can't change copy your save: " + getGameModeCacheDir() + File.separator + Core.GameSaveWorld;
      }
   }

   static {
      period = 1000000000L / (long)FPS;
      running = true;
      MAX_FRAME_SKIPS = 24;
      averageFPS = (float)PerformanceSettings.LockFPS;
      stringUTF = new ThreadLocal() {
         protected GameWindow.StringUTF initialValue() {
            return new GameWindow.StringUTF();
         }
      };
   }

   private static class StringUTF {
      private char[] chars;
      private byte[] bytes;
      private CharsetEncoder ce;
      private CharsetDecoder cd;

      private StringUTF() {
      }

      private int encode(String var1) throws UnsupportedEncodingException {
         int var2;
         if (this.chars == null || this.chars.length < var1.length()) {
            var2 = (var1.length() + 128 - 1) / 128 * 128;
            this.chars = new char[var2];
         }

         var1.getChars(0, var1.length(), this.chars, 0);
         if (this.ce == null) {
            this.ce = Charset.forName("UTF-8").newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);

            assert this.ce instanceof ArrayEncoder;
         }

         this.ce.reset();
         var2 = (int)((double)var1.length() * (double)this.ce.maxBytesPerChar());
         var2 = (var2 + 128 - 1) / 128 * 128;
         if (this.bytes == null || this.bytes.length < var2) {
            this.bytes = new byte[var2];
         }

         return ((ArrayEncoder)this.ce).encode(this.chars, 0, var1.length(), this.bytes);
      }

      private String decode(int var1) {
         if (this.cd == null) {
            this.cd = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);

            assert this.cd instanceof ArrayDecoder;
         }

         this.cd.reset();
         int var2 = (int)((double)var1 * (double)this.cd.maxCharsPerByte());
         int var3;
         if (this.chars == null || this.chars.length < var2) {
            var3 = (var2 + 128 - 1) / 128 * 128;
            this.chars = new char[var3];
         }

         var3 = ((ArrayDecoder)this.cd).decode(this.bytes, 0, var1, this.chars);
         return new String(this.chars, 0, var3);
      }

      public void save(ByteBuffer var1, String var2) throws UnsupportedEncodingException {
         if (var2 != null && !var2.isEmpty()) {
            int var3 = this.encode(var2);
            var1.putShort((short)var3);
            var1.put(this.bytes, 0, var3);
         } else {
            var1.putShort((short)0);
         }
      }

      public String load(ByteBuffer var1) throws UnsupportedEncodingException {
         short var2 = var1.getShort();
         if (var2 <= 0) {
            return "";
         } else {
            int var3 = (var2 + 128 - 1) / 128 * 128;
            if (this.bytes == null || this.bytes.length < var3) {
               this.bytes = new byte[var3];
            }

            var1.get(this.bytes, 0, var2);
            return this.decode(var2);
         }
      }

      // $FF: synthetic method
      StringUTF(Object var1) {
         this();
      }
   }

   public static class OSValidator {
      private static String OS = System.getProperty("os.name").toLowerCase();

      public static boolean isWindows() {
         return OS.indexOf("win") >= 0;
      }

      public static boolean isMac() {
         return OS.indexOf("mac") >= 0;
      }

      public static boolean isUnix() {
         return OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0;
      }

      public static boolean isSolaris() {
         return OS.indexOf("sunos") >= 0;
      }
   }
}
