package zombie.core;

import fmod.FMOD_DriverInfo;
import fmod.javafmod;
import java.awt.Canvas;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import se.krka.kahlua.vm.KahluaTable;
import zombie.FrameLoader;
import zombie.GameSounds;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.SVNRevision;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorFactory;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.raknet.VoiceManager;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.MultiTextureFBO2;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.core.textures.TextureID;
import zombie.core.utils.OnceEvery;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.IngameState;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.iso.IsoCamera;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.ui.ObjectTooltip;
import zombie.ui.UIManager;
import zombie.ui.UITextBox2;

public class Core {
   public static final boolean bDemo = false;
   public static boolean bTutorial;
   private static boolean fakefullscreen = false;
   public String versionNumber = "40.43";
   public String steamServerVersion = "1.0.0.0";
   public static int SVN_REVISION = SVNRevision.init();
   static Canvas canvas;
   static Canvas fullscreencanvas;
   public static boolean bMultithreadedRendering = true;
   public static boolean bDoubleSize = false;
   public static boolean bAltMoveMethod = false;
   public boolean rosewoodSpawnDone = false;
   private ColorInfo objectHighlitedColor = new ColorInfo(0.98F, 0.56F, 0.11F, 1.0F);
   public MultiTextureFBO2 OffscreenBuffer = new MultiTextureFBO2();
   public static float Zoom = 0.5F;
   public static boolean useLwjgl = true;
   public static boolean DoFiltering = false;
   private String saveFolder = null;
   public static boolean OptionZoom = true;
   public static boolean OptionModsEnabled = true;
   public static String OptionContextMenuFont = "Medium";
   public static String OptionInventoryFont = "Medium";
   public static String OptionTooltipFont = "Small";
   public static String OptionMeasurementFormat = "Metric";
   public static int OptionClockFormat = 1;
   public static boolean OptionClock24Hour = true;
   public static boolean OptionVSync = false;
   public static int OptionSoundVolume = 8;
   public static int OptionMusicVolume = 6;
   public static int OptionAmbientVolume = 5;
   public static int OptionMusicLibrary = 1;
   public static boolean OptionVoiceEnable = true;
   public static int OptionVoiceMode = 3;
   public static int OptionVoiceVADMode = 3;
   public static String OptionVoiceRecordDeviceName = "";
   public static int OptionVoiceVolumeMic = 10;
   public static int OptionVoiceVolumePlayers = 5;
   public static int OptionVehicleEngineVolume = 5;
   public static int OptionReloadDifficulty = 1;
   public static boolean OptionRackProgress = true;
   public static int OptionBloodDecals = 10;
   public static boolean OptionBorderlessWindow = false;
   public static boolean OptionTextureCompression = false;
   public static boolean OptionTexture2x = true;
   private static String OptionZoomLevels1x = "";
   private static String OptionZoomLevels2x = "";
   public static boolean OptionEnableContentTranslations = true;
   public static boolean OptionUIFBO = true;
   public static int OptionUIRenderFPS = 20;
   public static boolean OptionRadialMenuKeyToggle = true;
   public static boolean OptionPanCameraWhileAiming = true;
   public static boolean OptionShowChatTimestamp = false;
   public static boolean OptionShowChatTitle = false;
   public static String OptionChatFontSize = "medium";
   public static float OptionMinChatOpaque = 1.0F;
   public static float OptionMaxChatOpaque = 1.0F;
   public static float OptionChatFadeTime = 0.0F;
   public static boolean OptionChatOpaqueOnFocus = true;
   public static boolean OptionTemperatureDisplayCelsius = false;
   public static boolean OptionDoWindSpriteEffects = true;
   public static boolean OptionDoDoorSpriteEffects = true;
   private boolean showPing = true;
   private boolean forceSnow = false;
   private boolean zombieGroupSound = true;
   private String blinkingMoodle = null;
   private boolean tutorialDone = false;
   private boolean vehiclesWarningShow = false;
   private String poisonousBerry = null;
   private String poisonousMushroom = null;
   private boolean doneNewSaveFolder = false;
   private static String difficulty = "Hardcore";
   public static int TileScale = 2;
   private boolean isSelectingAll = false;
   private boolean showYourUsername = true;
   private ColorInfo mpTextColor = null;
   private boolean isAzerty = false;
   private String seenUpdateText = "";
   private boolean toggleToRun = false;
   private boolean celsius = false;
   private boolean warnMapConflict = false;
   private LinkedList mapOrder = null;
   private boolean riversideDone = false;
   private boolean noSave = false;
   private boolean showFirstTimeVehicleTutorial = false;
   private boolean showFirstTimeWeatherTutorial = true;
   boolean bChallenge;
   public static int width = 1280;
   public static int height = 720;
   public static int MaxJukeBoxesActive = 10;
   public static int NumJukeBoxesActive = 0;
   public static String GameMode = "Sandbox";
   private static String glVersion;
   private static int glMajorVersion = -1;
   private static Core core = new Core();
   public static boolean bDebug = false;
   public static boolean bHighSqualityShadows = true;
   public int lastHeight = 0;
   public int lastWidth = 0;
   public static UITextBox2 CurrentTextEntryBox = null;
   public static String storyDirectory = "mods/";
   public static String modRootDirectory = "mods/media/";
   public Shader RenderShader;
   private Map keyMaps = null;
   public boolean bUseShaders = true;
   private boolean bPerfSkybox = true;
   private boolean bPerfReflections = true;
   private boolean bPerfReflections_new = true;
   public int vidMem = 3;
   public int nGraphicLevel = 5;
   public boolean bSupportsFBO = true;
   public float maxZoom = 1.0F;
   int lviewwid = -1;
   int lviewhei = -1;
   public Vector2[] CircleVecs = new Vector2[32];
   public int UIRenderTick = 0;
   ByteBuffer buffer;
   BufferedImage image;
   Texture buffertexture;
   public boolean bInFrame = false;
   public int version = 1;
   public int fileversion = 7;
   public static final String SUN_JAVA_COMMAND = "sun.java.command";
   public static boolean fullScreen = false;
   static boolean[] bAutoZoom = new boolean[4];
   public static String GameMap = "DEFAULT";
   public static String GameSaveWorld = "";
   public static boolean SafeMode = false;
   public static boolean SafeModeForced = false;
   public static boolean SoundDisabled = false;
   public static boolean bIsSteam = true;
   public int frameStage = 0;
   int stack = 0;
   public static boolean bLastStand = false;
   public static boolean bLoadedWithMultithreaded = false;
   public static boolean bExiting = false;

   public boolean isMultiThread() {
      return bMultithreadedRendering;
   }

   public void setChallenge(boolean var1) {
      this.bChallenge = var1;
   }

   public boolean isChallenge() {
      return this.bChallenge;
   }

   public void setFramerate(int var1) {
      if (var1 == 1) {
         PerformanceSettings.LockFPS = 61;
      } else if (var1 == 2) {
         PerformanceSettings.LockFPS = 244;
      } else if (var1 == 3) {
         PerformanceSettings.LockFPS = 240;
      } else if (var1 == 4) {
         PerformanceSettings.LockFPS = 165;
      } else if (var1 == 5) {
         PerformanceSettings.LockFPS = 120;
      } else if (var1 == 6) {
         PerformanceSettings.LockFPS = 95;
      } else if (var1 == 7) {
         PerformanceSettings.LockFPS = 90;
      } else if (var1 == 8) {
         PerformanceSettings.LockFPS = 75;
      } else if (var1 == 9) {
         PerformanceSettings.LockFPS = 60;
      } else if (var1 == 10) {
         PerformanceSettings.LockFPS = 55;
      } else if (var1 == 11) {
         PerformanceSettings.LockFPS = 45;
      } else if (var1 == 12) {
         PerformanceSettings.LockFPS = 30;
      } else if (var1 == 13) {
         PerformanceSettings.LockFPS = 24;
      }

   }

   public void setMultiThread(boolean var1) {
      bMultithreadedRendering = var1;

      try {
         this.saveOptions();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   public boolean loadedShader() {
      return this.RenderShader != null;
   }

   public static int getGLMajorVersion() {
      if (glMajorVersion == -1) {
         getOpenGLVersions();
      }

      return glMajorVersion;
   }

   public boolean isDedicated() {
      return FrameLoader.bDedicated;
   }

   public boolean getUseShaders() {
      return this.bUseShaders;
   }

   public boolean getPerfSkybox() {
      return this.bPerfSkybox;
   }

   public void setPerfSkybox(boolean var1) {
      this.bPerfSkybox = var1;
   }

   public boolean getPerfReflections() {
      return this.bPerfReflections_new;
   }

   public boolean getPerfReflectionsOnLoad() {
      return this.bPerfReflections;
   }

   public void setPerfReflections(boolean var1) {
      this.bPerfReflections_new = var1;
   }

   public int getVidMem() {
      return SafeMode ? 5 : this.vidMem;
   }

   public void setVidMem(int var1) {
      if (SafeMode) {
         this.vidMem = 5;
      }

      this.vidMem = var1;

      try {
         this.saveOptions();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   public void setUseShaders(boolean var1) {
      this.bUseShaders = var1;

      try {
         this.saveOptions();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   public void shadersOptionChanged() {
      RenderThread.borrowContext();
      if (this.bUseShaders && !SafeModeForced) {
         try {
            if (!FrameLoader.bDedicated && this.RenderShader == null) {
               this.RenderShader = new Shader("screen");
            }

            if (this.RenderShader != null && (this.RenderShader.FragID == 0 || this.RenderShader.VertID == 0)) {
               this.RenderShader = null;
            }
         } catch (Exception var3) {
            this.RenderShader = null;
         }
      } else if (this.RenderShader != null) {
         try {
            this.RenderShader.destroy();
         } catch (Exception var2) {
            var2.printStackTrace();
         }

         this.RenderShader = null;
      }

      RenderThread.returnContext();
   }

   public void initShaders() {
      try {
         if (!FrameLoader.bDedicated && this.RenderShader == null && !SafeMode && !SafeModeForced) {
            RenderThread.borrowContext();
            this.RenderShader = new Shader("screen");
            RenderThread.returnContext();
         }

         if (this.RenderShader == null || this.RenderShader.FragID == 0 || this.RenderShader.VertID == 0) {
            this.RenderShader = null;
         }
      } catch (Exception var2) {
         this.RenderShader = null;
         var2.printStackTrace();
      }

   }

   public static String getGLVersion() {
      if (glVersion == null) {
         getOpenGLVersions();
      }

      return glVersion;
   }

   public String getGameMode() {
      return GameMode;
   }

   public static Core getInstance() {
      return core;
   }

   public static void getOpenGLVersions() {
      glVersion = GL11.glGetString(7938);
      glMajorVersion = glVersion.charAt(0) - 48;
   }

   public boolean getDebug() {
      return bDebug;
   }

   public static void setFullScreen(boolean var0) throws LWJGLException {
      fullScreen = var0;
   }

   public static int[] flipPixels(int[] var0, int var1, int var2) {
      int[] var3 = null;
      if (var0 != null) {
         var3 = new int[var1 * var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            for(int var5 = 0; var5 < var1; ++var5) {
               var3[(var2 - var4 - 1) * var1 + var5] = var0[var4 * var1 + var5];
            }
         }
      }

      return var3;
   }

   public void TakeScreenshot() {
      GL11.glPixelStorei(3333, 1);
      GL11.glReadBuffer(1028);
      int var1 = Display.getDisplayMode().getWidth();
      int var2 = Display.getDisplayMode().getHeight();
      int var3 = 0;
      int var4 = 0;
      if (var1 > 256) {
         var3 = var1 / 2 - 128;
         var1 = 256;
      }

      if (var2 > 256) {
         var4 = var2 / 2 - 128;
         var2 = 256;
      }

      var4 += 32;
      var3 += 10;
      if (var4 < 0) {
         var4 = 0;
      }

      byte var5 = 3;
      ByteBuffer var6 = BufferUtils.createByteBuffer(var1 * var2 * var5);
      GL11.glReadPixels(var3, var4, var1, var2, 6407, 5121, var6);
      int[] var7 = new int[var1 * var2];
      File var9 = new File(GameWindow.getGameModeCacheDir() + File.separator + GameSaveWorld + File.separator + "thumb.png");
      String var10 = "png";

      for(int var11 = 0; var11 < var7.length; ++var11) {
         int var8 = var11 * 3;
         var7[var11] = -16777216 | (var6.get(var8) & 255) << 16 | (var6.get(var8 + 1) & 255) << 8 | (var6.get(var8 + 2) & 255) << 0;
      }

      var6 = null;
      var7 = flipPixels(var7, var1, var2);
      BufferedImage var14 = new BufferedImage(var1, var2, 2);
      var14.setRGB(0, 0, var1, var2, var7, 0, var1);

      try {
         ImageIO.write(var14, "png", var9);
      } catch (IOException var13) {
         var13.printStackTrace();
      }

      Texture.forgetTexture(GameWindow.getGameModeCacheDir() + File.separator + GameSaveWorld + File.separator + "thumb.png");
   }

   public void TakeFullScreenshot(String var1) {
      try {
         RenderThread.borrowContext();
         GL11.glPixelStorei(3333, 1);
         GL11.glReadBuffer(1028);
         int var2 = Display.getDisplayMode().getWidth();
         int var3 = Display.getDisplayMode().getHeight();
         byte var4 = 0;
         byte var5 = 0;
         byte var6 = 3;
         ByteBuffer var7 = BufferUtils.createByteBuffer(var2 * var3 * var6);
         GL11.glReadPixels(var4, var5, var2, var3, 6407, 5121, var7);
         int[] var8 = new int[var2 * var3];
         if (var1 == null) {
            SimpleDateFormat var10 = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            var1 = "screenshot_" + var10.format(Calendar.getInstance().getTime()) + ".png";
         }

         File var18 = new File(GameWindow.getScreenshotDir() + File.separator + var1);
         int var11 = 0;

         while(true) {
            if (var11 >= var8.length) {
               var7 = null;
               var8 = flipPixels(var8, var2, var3);
               BufferedImage var19 = new BufferedImage(var2, var3, 2);
               var19.setRGB(0, 0, var2, var3, var8, 0, var2);

               try {
                  ImageIO.write(var19, "png", var18);
               } catch (IOException var16) {
                  var16.printStackTrace();
               }
               break;
            }

            int var9 = var11 * 3;
            var8[var11] = -16777216 | (var7.get(var9) & 255) << 16 | (var7.get(var9 + 1) & 255) << 8 | (var7.get(var9 + 2) & 255) << 0;
            ++var11;
         }
      } finally {
         RenderThread.returnContext();
      }

   }

   public static boolean supportNPTTexture() {
      return false;
   }

   public boolean supportsFBO() {
      if (FrameLoader.bDedicated) {
         return false;
      } else if (SafeMode) {
         this.OffscreenBuffer.bZoomEnabled = false;
         return false;
      } else if (!this.bSupportsFBO) {
         return false;
      } else if (this.OffscreenBuffer.Current != null) {
         return true;
      } else {
         try {
            if (TextureFBO.checkFBOSupport() && this.setupMultiFBO()) {
               return true;
            } else {
               this.bSupportsFBO = false;
               getInstance();
               SafeMode = true;
               this.OffscreenBuffer.bZoomEnabled = false;
               return false;
            }
         } catch (Exception var2) {
            var2.printStackTrace();
            this.bSupportsFBO = false;
            getInstance();
            SafeMode = true;
            this.OffscreenBuffer.bZoomEnabled = false;
            return false;
         }
      }
   }

   void sharedInit() {
      if (this.supportsFBO()) {
      }

   }

   public void MoveMethodToggle() {
      bAltMoveMethod = !bAltMoveMethod;
   }

   public void doubleSizeToggle() {
      bDoubleSize = !bDoubleSize;
      UIManager.resize();
   }

   public void EndFrameText(int var1) {
      if (!LuaManager.thread.bStep) {
         if (this.OffscreenBuffer.Current != null) {
         }

         IndieGL.glDoEndFrame();
         this.frameStage = 2;
      }
   }

   public void EndFrame(int var1) {
      if (!LuaManager.thread.bStep) {
         if (this.OffscreenBuffer.Current != null) {
            SpriteRenderer.instance.glBuffer(0, var1);
         }

         IndieGL.glDoEndFrame();
         this.frameStage = 2;
      }
   }

   public void EndFrame() {
      IndieGL.glDoEndFrame();
      if (this.OffscreenBuffer.Current != null) {
         SpriteRenderer.instance.glBuffer(0, 0);
      }

   }

   public void CalcCircle() {
      Vector2 var1 = new Vector2(0.0F, -1.0F);

      for(int var2 = 0; var2 < 32; ++var2) {
         this.CircleVecs[var2] = new Vector2(var1.x, var1.y);
         var1.rotate(0.19634955F);
      }

   }

   public void DrawCircle(float var1, float var2, float var3) {
   }

   public void EndFrameUI() {
      if (UIManager.useUIFBO && UIManager.UIFBO == null) {
         UIManager.CreateFBO(width, height);
      }

      if (LuaManager.thread != null && LuaManager.thread.bStep) {
         SpriteRenderer.instance.clearSprites();
         SpriteRenderer.instance.numSprites = 0;
      } else {
         ExceptionLogger.render();
         if (UIManager.useUIFBO && this.UIRenderTick <= 0) {
            SpriteRenderer.instance.glBuffer(3, 0);
            this.UIRenderTick = (int)Math.ceil((double)((float)PerformanceSettings.LockFPS / (float)OptionUIRenderFPS));
         }

         if (UIManager.useUIFBO) {
            SpriteRenderer.instance.bDoAdditive = true;
            SpriteRenderer.instance.render((Texture)UIManager.UIFBO.getTexture(), 0, height, width, -height, 1.0F, 1.0F, 1.0F, 1.0F);
            SpriteRenderer.instance.bDoAdditive = false;
         }

         IndieGL.glDoEndFrame();
         RenderThread.Ready();
         this.frameStage = 0;
      }
   }

   public static void UnfocusActiveTextEntryBox() {
      if (CurrentTextEntryBox != null && !CurrentTextEntryBox.getUIName().contains("chat text entry")) {
         CurrentTextEntryBox.DoingTextEntry = false;
         if (CurrentTextEntryBox.Frame != null) {
            CurrentTextEntryBox.Frame.Colour = CurrentTextEntryBox.StandardFrameColour;
         }

         CurrentTextEntryBox = null;
      }

   }

   public int getOffscreenWidth(int var1) {
      if (this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null) {
         return this.OffscreenBuffer.getWidth(var1);
      } else {
         return IsoPlayer.numPlayers > 1 ? this.getScreenWidth() / 2 : this.getScreenWidth();
      }
   }

   public int getOffscreenHeight(int var1) {
      if (this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null) {
         return this.OffscreenBuffer.getHeight(var1);
      } else {
         return IsoPlayer.numPlayers > 2 ? this.getScreenHeight() / 2 : this.getScreenHeight();
      }
   }

   public int getOffscreenTrueWidth() {
      return this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null ? this.OffscreenBuffer.getTexture(0).getWidth() : this.getScreenWidth();
   }

   public int getOffscreenTrueHeight() {
      return this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null ? this.OffscreenBuffer.getTexture(0).getHeight() : this.getScreenHeight();
   }

   public int getScreenHeight() {
      return height;
   }

   public int getScreenWidth() {
      return width;
   }

   public void setResolutionAndFullScreen(int var1, int var2, boolean var3) {
      setDisplayMode(var1, var2, var3);
      this.setScreenSize(Display.getWidth(), Display.getHeight());
   }

   public void setResolution(String var1) {
      String[] var2 = var1.split("x");
      int var3 = Integer.parseInt(var2[0].trim());
      int var4 = Integer.parseInt(var2[1].trim());
      if (fullScreen) {
         setDisplayMode(var3, var4, true);
      } else {
         setDisplayMode(var3, var4, false);
      }

      this.setScreenSize(Display.getWidth(), Display.getHeight());

      try {
         this.saveOptions();
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   public void changeCursor(String var1) {
   }

   public boolean loadOptions() throws IOException {
      File var1 = new File(GameWindow.getCacheDir() + File.separator + "options.bin");
      File var2 = new File(GameWindow.getCacheDir() + File.separator + "options.ini");
      int var7;
      int var33;
      if (!var2.exists() && !var1.exists()) {
         this.saveFolder = getMyDocumentFolder();
         File var24 = new File(this.saveFolder);
         var24.mkdir();
         this.copyPasteFolders("mods");
         Translator.setLanguage(Language.FromString(System.getProperty("user.language").toUpperCase()));
         if (Translator.getAzertyMap().contains(Translator.getLanguage().name())) {
            this.setAzerty(true);
         }

         JoypadManager.instance.setControllerActive("XInputController0", true);
         JoypadManager.instance.setControllerActive("XInputController1", true);
         JoypadManager.instance.setControllerActive("XInputController2", true);
         JoypadManager.instance.setControllerActive("XInputController3", true);
         if (!GameServer.bServer) {
            try {
               int var31 = 0;
               var33 = 0;
               DisplayMode[] var26 = Display.getAvailableDisplayModes();

               for(var7 = 0; var7 < var26.length; ++var7) {
                  if (var26[var7].getWidth() > var31 && var26[var7].getWidth() <= 1920) {
                     var31 = var26[var7].getWidth();
                     var33 = var26[var7].getHeight();
                  }
               }

               width = var31;
               height = var33;
            } catch (LWJGLException var18) {
               var18.printStackTrace();
            }
         }

         this.setOptionZoomLevels2x("50;75;125;150;175;200");
         this.setOptionZoomLevels1x("50;75;125;150;175;200");
         this.saveOptions();
         return false;
      } else {
         if (!var2.exists() && var1.exists()) {
            var2.createNewFile();
            FileWriter var3 = new FileWriter(var2);
            FileInputStream var4 = new FileInputStream(var1);
            DataInputStream var5 = new DataInputStream(var4);
            this.version = var5.readInt();
            var3.write("version=" + this.version + "\r\n");
            width = var5.readInt();
            var3.write("width=" + width + "\r\n");
            height = var5.readInt();
            var3.write("height=" + height + "\r\n");
            fullScreen = var5.readBoolean();
            var3.write("fullScreen=" + fullScreen + "\r\n");
            this.bUseShaders = var5.readBoolean();
            var3.write("bUseShaders=" + this.bUseShaders + "\r\n");
            if (this.fileversion > 1) {
               this.vidMem = var5.readInt();
            }

            if (this.fileversion > 2) {
               bMultithreadedRendering = var5.readBoolean();
            }

            try {
               if (this.version > 3) {
                  Translator.setLanguage(var5.readInt());
               }

               this.saveFolder = getMyDocumentFolder();
            } catch (Exception var17) {
            }

            var3.write("bMultithreadedRendering=" + bMultithreadedRendering + "\r\n");
            if (this.version < 6) {
               this.copyPasteFolders("mods");
            }

            if (Translator.language == null) {
               Translator.setLanguage(Language.FromString(System.getProperty("user.language").toUpperCase()));
            }

            if (Translator.language == null) {
               Translator.setLanguage(Translator.getDefaultLanguage().index());
            }

            var4.close();
            var3.write("language=" + Translator.language + "\r\n");
            var3.close();
         }

         if (var1.exists()) {
            var1.delete();
         }

         for(int var22 = 0; var22 < 4; ++var22) {
            this.setAutoZoom(var22, false);
         }

         BufferedReader var23 = new BufferedReader(new FileReader(var2));

         try {
            String var25;
            while((var25 = var23.readLine()) != null) {
               if (var25.startsWith("version=")) {
                  this.version = new Integer(var25.replaceFirst("version=", ""));
               } else if (var25.startsWith("width=")) {
                  width = new Integer(var25.replaceFirst("width=", ""));
               } else if (var25.startsWith("height=")) {
                  height = new Integer(var25.replaceFirst("height=", ""));
               } else if (var25.startsWith("fullScreen=")) {
                  fullScreen = new Boolean(var25.replaceFirst("fullScreen=", ""));
               } else if (var25.startsWith("frameRate=")) {
                  PerformanceSettings.LockFPS = Integer.parseInt(var25.replaceFirst("frameRate=", ""));
               } else if (var25.startsWith("lighting=")) {
                  PerformanceSettings.LightingFrameSkip = Integer.parseInt(var25.replaceFirst("lighting=", ""));
               } else if (var25.startsWith("lightFPS=")) {
                  PerformanceSettings.instance.setLightingFPS(Integer.parseInt(var25.replaceFirst("lightFPS=", "")));
               } else if (var25.startsWith("newRoofHiding=")) {
                  PerformanceSettings.instance.setNewRoofHiding(Boolean.parseBoolean(var25.replaceFirst("newRoofHiding=", "")));
               } else if (var25.startsWith("bUseShaders=")) {
                  this.bUseShaders = new Boolean(var25.replaceFirst("bUseShaders=", ""));
               } else if (var25.startsWith("bPerfSkybox=")) {
                  this.bPerfSkybox = new Boolean(var25.replaceFirst("bPerfSkybox=", ""));
               } else if (var25.startsWith("bPerfReflections=")) {
                  this.bPerfReflections = new Boolean(var25.replaceFirst("bPerfReflections=", ""));
                  this.bPerfReflections_new = this.bPerfReflections;
               } else if (var25.startsWith("bMultithreadedRendering=")) {
                  bMultithreadedRendering = new Boolean(var25.replaceFirst("bMultithreadedRendering=", ""));
               } else if (var25.startsWith("language=")) {
                  Translator.setLanguage(Language.FromString(var25.replaceFirst("language=", "")));
               } else if (var25.startsWith("zoom=")) {
                  OptionZoom = Boolean.parseBoolean(var25.replaceFirst("zoom=", ""));
               } else {
                  String[] var27;
                  if (var25.startsWith("autozoom=")) {
                     var27 = var25.replaceFirst("autozoom=", "").split(",");

                     for(var33 = 0; var33 < var27.length; ++var33) {
                        if (!var27[var33].isEmpty()) {
                           var7 = Integer.parseInt(var27[var33]);
                           if (var7 >= 1 && var7 <= 4) {
                              this.setAutoZoom(var7 - 1, true);
                           }
                        }
                     }
                  } else if (var25.startsWith("contextMenuFont=")) {
                     OptionContextMenuFont = var25.replaceFirst("contextMenuFont=", "").trim();
                  } else if (var25.startsWith("inventoryFont=")) {
                     OptionInventoryFont = var25.replaceFirst("inventoryFont=", "").trim();
                  } else if (var25.startsWith("tooltipFont=")) {
                     OptionTooltipFont = var25.replaceFirst("tooltipFont=", "").trim();
                  } else if (var25.startsWith("measurementsFormat=")) {
                     OptionMeasurementFormat = var25.replaceFirst("measurementsFormat=", "").trim();
                  } else if (var25.startsWith("clockFormat=")) {
                     OptionClockFormat = Integer.parseInt(var25.replaceFirst("clockFormat=", ""));
                  } else if (var25.startsWith("clock24Hour=")) {
                     OptionClock24Hour = Boolean.parseBoolean(var25.replaceFirst("clock24Hour=", ""));
                  } else if (var25.startsWith("vsync=")) {
                     OptionVSync = Boolean.parseBoolean(var25.replaceFirst("vsync=", ""));
                  } else if (var25.startsWith("voiceEnable=")) {
                     OptionVoiceEnable = Boolean.parseBoolean(var25.replaceFirst("voiceEnable=", ""));
                  } else if (var25.startsWith("voiceMode=")) {
                     OptionVoiceMode = Integer.parseInt(var25.replaceFirst("voiceMode=", ""));
                  } else if (var25.startsWith("voiceVADMode=")) {
                     OptionVoiceVADMode = Integer.parseInt(var25.replaceFirst("voiceVADMode=", ""));
                  } else if (var25.startsWith("voiceVolumeMic=")) {
                     OptionVoiceVolumeMic = Integer.parseInt(var25.replaceFirst("voiceVolumeMic=", ""));
                  } else if (var25.startsWith("voiceVolumePlayers=")) {
                     OptionVoiceVolumePlayers = Integer.parseInt(var25.replaceFirst("voiceVolumePlayers=", ""));
                  } else if (var25.startsWith("voiceRecordDeviceName=")) {
                     OptionVoiceRecordDeviceName = var25.replaceFirst("voiceRecordDeviceName=", "");
                  } else if (var25.startsWith("soundVolume=")) {
                     OptionSoundVolume = Integer.parseInt(var25.replaceFirst("soundVolume=", ""));
                  } else if (var25.startsWith("musicVolume=")) {
                     OptionMusicVolume = Integer.parseInt(var25.replaceFirst("musicVolume=", ""));
                  } else if (var25.startsWith("ambientVolume=")) {
                     OptionAmbientVolume = Integer.parseInt(var25.replaceFirst("ambientVolume=", ""));
                  } else if (var25.startsWith("musicLibrary=")) {
                     OptionMusicLibrary = Integer.parseInt(var25.replaceFirst("musicLibrary=", ""));
                  } else if (var25.startsWith("vehicleEngineVolume=")) {
                     OptionVehicleEngineVolume = Integer.parseInt(var25.replaceFirst("vehicleEngineVolume=", ""));
                  } else if (var25.startsWith("reloadDifficulty=")) {
                     OptionReloadDifficulty = Integer.parseInt(var25.replaceFirst("reloadDifficulty=", ""));
                  } else if (var25.startsWith("rackProgress=")) {
                     OptionRackProgress = Boolean.parseBoolean(var25.replaceFirst("rackProgress=", ""));
                  } else if (var25.startsWith("controller=")) {
                     String var28 = var25.replaceFirst("controller=", "");
                     if (!var28.isEmpty()) {
                        JoypadManager.instance.setControllerActive(var28, true);
                     }
                  } else if (var25.startsWith("numberOf3D=")) {
                     PerformanceSettings.numberOf3D = Integer.parseInt(var25.replaceFirst("numberOf3D=", ""));
                     if (PerformanceSettings.numberOf3D < 0) {
                        PerformanceSettings.numberOf3D = 0;
                     }

                     if (PerformanceSettings.numberOf3D > 9) {
                        PerformanceSettings.numberOf3D = 9;
                     }
                  } else if (var25.startsWith("modelsEnabled")) {
                     PerformanceSettings.modelsEnabled = Boolean.parseBoolean(var25.replaceFirst("modelsEnabled=", ""));
                  } else if (var25.startsWith("corpses3D")) {
                     PerformanceSettings.corpses3D = Boolean.parseBoolean(var25.replaceFirst("corpses3D=", ""));
                  } else if (var25.startsWith("tutorialDone=")) {
                     this.tutorialDone = Boolean.parseBoolean(var25.replaceFirst("tutorialDone=", ""));
                  } else if (var25.startsWith("vehiclesWarningShow=")) {
                     this.vehiclesWarningShow = Boolean.parseBoolean(var25.replaceFirst("vehiclesWarningShow=", ""));
                  } else if (var25.startsWith("bloodDecals=")) {
                     this.setOptionBloodDecals(Integer.parseInt(var25.replaceFirst("bloodDecals=", "")));
                  } else if (var25.startsWith("borderless=")) {
                     OptionBorderlessWindow = Boolean.parseBoolean(var25.replaceFirst("borderless=", ""));
                  } else if (var25.startsWith("textureCompression=")) {
                     OptionTextureCompression = Boolean.parseBoolean(var25.replaceFirst("textureCompression=", ""));
                  } else if (var25.startsWith("texture2x=")) {
                     OptionTexture2x = Boolean.parseBoolean(var25.replaceFirst("texture2x=", ""));
                  } else if (var25.startsWith("zoomLevels1x=")) {
                     OptionZoomLevels1x = var25.replaceFirst("zoomLevels1x=", "");
                  } else if (var25.startsWith("zoomLevels2x=")) {
                     OptionZoomLevels2x = var25.replaceFirst("zoomLevels2x=", "");
                  } else if (var25.startsWith("showChatTimestamp=")) {
                     OptionShowChatTimestamp = Boolean.parseBoolean(var25.replaceFirst("showChatTimestamp=", ""));
                  } else if (var25.startsWith("showChatTitle=")) {
                     OptionShowChatTitle = Boolean.parseBoolean(var25.replaceFirst("showChatTitle=", ""));
                  } else if (var25.startsWith("chatFontSize=")) {
                     OptionChatFontSize = var25.replaceFirst("chatFontSize=", "");
                  } else if (var25.startsWith("minChatOpaque=")) {
                     OptionMinChatOpaque = Float.parseFloat(var25.replaceFirst("minChatOpaque=", ""));
                  } else if (var25.startsWith("maxChatOpaque=")) {
                     OptionMaxChatOpaque = Float.parseFloat(var25.replaceFirst("maxChatOpaque=", ""));
                  } else if (var25.startsWith("chatFadeTime=")) {
                     OptionChatFadeTime = Float.parseFloat(var25.replaceFirst("chatFadeTime=", ""));
                  } else if (var25.startsWith("chatOpaqueOnFocus=")) {
                     OptionChatOpaqueOnFocus = Boolean.parseBoolean(var25.replaceFirst("chatOpaqueOnFocus=", ""));
                  } else if (var25.startsWith("doneNewSaveFolder=")) {
                     this.doneNewSaveFolder = Boolean.parseBoolean(var25.replaceFirst("doneNewSaveFolder=", ""));
                  } else if (var25.startsWith("contentTranslationsEnabled=")) {
                     OptionEnableContentTranslations = Boolean.parseBoolean(var25.replaceFirst("contentTranslationsEnabled=", ""));
                  } else if (var25.startsWith("showYourUsername=")) {
                     this.showYourUsername = Boolean.parseBoolean(var25.replaceFirst("showYourUsername=", ""));
                  } else if (var25.startsWith("riversideDone=")) {
                     this.riversideDone = Boolean.parseBoolean(var25.replaceFirst("riversideDone=", ""));
                  } else if (var25.startsWith("rosewoodSpawnDone=")) {
                     this.rosewoodSpawnDone = Boolean.parseBoolean(var25.replaceFirst("rosewoodSpawnDone=", ""));
                  } else {
                     float var29;
                     float var32;
                     float var34;
                     if (var25.startsWith("mpTextColor=")) {
                        var27 = var25.replaceFirst("mpTextColor=", "").split(",");
                        var29 = Float.parseFloat(var27[0]);
                        var32 = Float.parseFloat(var27[1]);
                        var34 = Float.parseFloat(var27[2]);
                        if (var29 < 0.19F) {
                           var29 = 0.19F;
                        }

                        if (var32 < 0.19F) {
                           var32 = 0.19F;
                        }

                        if (var34 < 0.19F) {
                           var34 = 0.19F;
                        }

                        this.mpTextColor = new ColorInfo(var29, var32, var34, 1.0F);
                     } else if (var25.startsWith("objHighlightColor=")) {
                        var27 = var25.replaceFirst("objHighlightColor=", "").split(",");
                        var29 = Float.parseFloat(var27[0]);
                        var32 = Float.parseFloat(var27[1]);
                        var34 = Float.parseFloat(var27[2]);
                        if (var29 < 0.19F) {
                           var29 = 0.19F;
                        }

                        if (var32 < 0.19F) {
                           var32 = 0.19F;
                        }

                        if (var34 < 0.19F) {
                           var34 = 0.19F;
                        }

                        this.objectHighlitedColor = new ColorInfo(var29, var32, var34, 1.0F);
                     } else if (var25.startsWith("seenNews=")) {
                        this.setSeenUpdateText(var25.replaceFirst("seenNews=", ""));
                     } else if (var25.startsWith("toggleToRun=")) {
                        this.setToggleToRun(Boolean.parseBoolean(var25.replaceFirst("toggleToRun=", "")));
                     } else if (var25.startsWith("celsius=")) {
                        this.setCelsius(Boolean.parseBoolean(var25.replaceFirst("celsius=", "")));
                     } else if (var25.startsWith("mapOrder=")) {
                        this.mapOrder = new LinkedList();
                        if (this.version < 7) {
                           var25 = "mapOrder=";
                        }

                        var27 = var25.replaceFirst("mapOrder=", "").split(";");
                        String[] var6 = var27;
                        var7 = var27.length;

                        for(int var8 = 0; var8 < var7; ++var8) {
                           String var9 = var6[var8];
                           if (!var9.isEmpty()) {
                              this.mapOrder.add(var9);
                           }
                        }
                     } else if (var25.startsWith("showFirstTimeWeatherTutorial=")) {
                        this.setShowFirstTimeWeatherTutorial(Boolean.parseBoolean(var25.replaceFirst("showFirstTimeWeatherTutorial=", "")));
                     } else if (var25.startsWith("uiRenderOffscreen=")) {
                        OptionUIFBO = Boolean.parseBoolean(var25.replaceFirst("uiRenderOffscreen=", ""));
                     } else if (var25.startsWith("uiRenderFPS=")) {
                        OptionUIRenderFPS = Integer.parseInt(var25.replaceFirst("uiRenderFPS=", ""));
                     } else if (var25.startsWith("radialMenuKeyToggle=")) {
                        OptionRadialMenuKeyToggle = Boolean.parseBoolean(var25.replaceFirst("radialMenuKeyToggle=", ""));
                     } else if (var25.startsWith("panCameraWhileAiming=")) {
                        OptionPanCameraWhileAiming = Boolean.parseBoolean(var25.replaceFirst("panCameraWhileAiming=", ""));
                     } else if (var25.startsWith("temperatureDisplayCelsius=")) {
                        OptionTemperatureDisplayCelsius = Boolean.parseBoolean(var25.replaceFirst("temperatureDisplayCelsius=", ""));
                     } else if (var25.startsWith("doWindSpriteEffects=")) {
                        OptionDoWindSpriteEffects = Boolean.parseBoolean(var25.replaceFirst("doWindSpriteEffects=", ""));
                     } else if (var25.startsWith("doDoorSpriteEffects=")) {
                        OptionDoDoorSpriteEffects = Boolean.parseBoolean(var25.replaceFirst("doDoorSpriteEffects=", ""));
                     }
                  }
               }
            }

            OnceEvery.FPS = PerformanceSettings.LockFPS;
            if (Translator.language == null) {
               Translator.setLanguage(Language.FromString(System.getProperty("user.language").toUpperCase()));
            }

            if (Translator.language == null) {
               Translator.setLanguage(Translator.getDefaultLanguage().index());
            }

            if (!this.doneNewSaveFolder) {
               File var30 = new File(GameWindow.getSaveDir());
               var30.mkdir();
               ArrayList var35 = new ArrayList();
               var35.add("Beginner");
               var35.add("Survival");
               var35.add("A Really CD DA");
               var35.add("LastStand");
               var35.add("Opening Hours");
               var35.add("Sandbox");
               var35.add("Tutorial");
               var35.add("Winter is Coming");
               var35.add("You Have One Day");
               File var37 = null;
               File var38 = null;

               try {
                  Iterator var36 = var35.iterator();

                  while(var36.hasNext()) {
                     String var10 = (String)var36.next();
                     var37 = new File(GameWindow.getCacheDir() + File.separator + var10);
                     var38 = new File(GameWindow.getSaveDir() + File.separator + var10);
                     if (var37.exists()) {
                        var38.mkdir();
                        Files.move(var37.toPath(), var38.toPath(), StandardCopyOption.REPLACE_EXISTING);
                     }
                  }
               } catch (Exception var19) {
               }

               this.doneNewSaveFolder = true;
            }
         } catch (Exception var20) {
            var20.printStackTrace();
         } finally {
            var23.close();
         }

         this.saveOptions();
         return true;
      }
   }

   private void copyPasteFolders(String var1) {
      File var2 = (new File(var1)).getAbsoluteFile();
      if (var2.exists()) {
         this.searchFolders(var2, var1);
      }

   }

   private void searchFolders(File var1, String var2) {
      if (var1.isDirectory()) {
         File var3 = new File(this.saveFolder + File.separator + var2);
         var3.mkdir();
         String[] var4 = var1.list();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            this.searchFolders(new File(var1.getAbsolutePath() + File.separator + var4[var5]), var2 + File.separator + var4[var5]);
         }
      } else {
         this.copyPasteFile(var1, var2);
      }

   }

   private void copyPasteFile(File var1, String var2) {
      FileOutputStream var3 = null;
      FileInputStream var4 = null;

      try {
         File var5 = new File(this.saveFolder + File.separator + var2);
         var5.createNewFile();
         var3 = new FileOutputStream(var5);
         var4 = new FileInputStream(var1);
         var3.getChannel().transferFrom(var4.getChannel(), 0L, var1.length());
      } catch (Exception var14) {
         var14.printStackTrace();
      } finally {
         try {
            if (var4 != null) {
               var4.close();
            }

            if (var3 != null) {
               var3.close();
            }
         } catch (IOException var13) {
            var13.printStackTrace();
         }

      }

   }

   public static String getMyDocumentFolder() {
      return GameWindow.getCacheDir();
   }

   public void saveOptions() throws IOException {
      File var1 = new File(GameWindow.getCacheDir() + File.separator + "options.ini");
      if (!var1.exists()) {
         var1.createNewFile();
      }

      FileWriter var2 = new FileWriter(var1);

      try {
         var2.write("version=" + this.fileversion + "\r\n");
         var2.write("width=" + this.getScreenWidth() + "\r\n");
         var2.write("height=" + this.getScreenHeight() + "\r\n");
         var2.write("fullScreen=" + fullScreen + "\r\n");
         var2.write("frameRate=" + PerformanceSettings.LockFPS + "\r\n");
         var2.write("lighting=" + PerformanceSettings.LightingFrameSkip + "\r\n");
         var2.write("lightFPS=" + PerformanceSettings.LightingFPS + "\r\n");
         var2.write("newRoofHiding=" + PerformanceSettings.NewRoofHiding + "\r\n");
         var2.write("bUseShaders=" + this.bUseShaders + "\r\n");
         var2.write("bPerfSkybox=" + this.bPerfSkybox + "\r\n");
         var2.write("bPerfReflections=" + this.bPerfReflections_new + "\r\n");
         var2.write("vidMem=" + this.vidMem + "\r\n");
         var2.write("bMultithreadedRendering=" + bMultithreadedRendering + "\r\n");
         var2.write("language=" + Translator.getLanguage() + "\r\n");
         var2.write("zoom=" + OptionZoom + "\r\n");
         var2.write("contextMenuFont=" + OptionContextMenuFont + "\r\n");
         var2.write("inventoryFont=" + OptionInventoryFont + "\r\n");
         var2.write("tooltipFont=" + OptionTooltipFont + "\r\n");
         var2.write("clockFormat=" + OptionClockFormat + "\r\n");
         var2.write("clock24Hour=" + OptionClock24Hour + "\r\n");
         var2.write("measurementsFormat=" + OptionMeasurementFormat + "\r\n");
         String var3 = "";

         for(int var4 = 0; var4 < 4; ++var4) {
            if (bAutoZoom[var4]) {
               if (!var3.isEmpty()) {
                  var3 = var3 + ",";
               }

               var3 = var3 + (var4 + 1);
            }
         }

         var2.write("autozoom=" + var3 + "\r\n");
         var2.write("vsync=" + OptionVSync + "\r\n");
         var2.write("soundVolume=" + OptionSoundVolume + "\r\n");
         var2.write("ambientVolume=" + OptionAmbientVolume + "\r\n");
         var2.write("musicVolume=" + OptionMusicVolume + "\r\n");
         var2.write("musicLibrary=" + OptionMusicLibrary + "\r\n");
         var2.write("vehicleEngineVolume=" + OptionVehicleEngineVolume + "\r\n");
         var2.write("voiceEnable=" + OptionVoiceEnable + "\r\n");
         var2.write("voiceMode=" + OptionVoiceMode + "\r\n");
         var2.write("voiceVADMode=" + OptionVoiceVADMode + "\r\n");
         var2.write("voiceVolumeMic=" + OptionVoiceVolumeMic + "\r\n");
         var2.write("voiceVolumePlayerse=" + OptionVoiceVolumePlayers + "\r\n");
         var2.write("voiceRecordDeviceName=" + OptionVoiceRecordDeviceName + "\r\n");
         var2.write("reloadDifficulty=" + OptionReloadDifficulty + "\r\n");
         var2.write("rackProgress=" + OptionRackProgress + "\r\n");
         Iterator var11 = JoypadManager.instance.ActiveControllerNames.iterator();

         while(var11.hasNext()) {
            String var5 = (String)var11.next();
            var2.write("controller=" + var5 + "\r\n");
         }

         var2.write("numberOf3D=" + PerformanceSettings.numberOf3D + "\r\n");
         var2.write("modelsEnabled=" + PerformanceSettings.modelsEnabled + "\r\n");
         var2.write("corpses3D=" + PerformanceSettings.corpses3D + "\r\n");
         var2.write("tutorialDone=" + this.isTutorialDone() + "\r\n");
         var2.write("vehiclesWarningShow=" + this.isVehiclesWarningShow() + "\r\n");
         var2.write("bloodDecals=" + OptionBloodDecals + "\r\n");
         var2.write("borderless=" + OptionBorderlessWindow + "\r\n");
         var2.write("textureCompression=" + OptionTextureCompression + "\r\n");
         var2.write("texture2x=" + OptionTexture2x + "\r\n");
         var2.write("zoomLevels1x=" + OptionZoomLevels1x + "\r\n");
         var2.write("zoomLevels2x=" + OptionZoomLevels2x + "\r\n");
         var2.write("showChatTimestamp=" + OptionShowChatTimestamp + "\r\n");
         var2.write("showChatTitle=" + OptionShowChatTitle + "\r\n");
         var2.write("chatFontSize=" + OptionChatFontSize + "\r\n");
         var2.write("minChatOpaque=" + OptionMinChatOpaque + "\r\n");
         var2.write("maxChatOpaque=" + OptionMaxChatOpaque + "\r\n");
         var2.write("chatFadeTime=" + OptionChatFadeTime + "\r\n");
         var2.write("chatOpaqueOnFocus=" + OptionChatOpaqueOnFocus + "\r\n");
         var2.write("doneNewSaveFolder=" + this.doneNewSaveFolder + "\r\n");
         var2.write("contentTranslationsEnabled=" + OptionEnableContentTranslations + "\r\n");
         var2.write("showYourUsername=" + this.showYourUsername + "\r\n");
         var2.write("rosewoodSpawnDone=" + this.rosewoodSpawnDone + "\r\n");
         if (this.mpTextColor != null) {
            var2.write("mpTextColor=" + this.mpTextColor.r + "," + this.mpTextColor.g + "," + this.mpTextColor.b + "\r\n");
         }

         var2.write("objHighlightColor=" + this.objectHighlitedColor.r + "," + this.objectHighlitedColor.g + "," + this.objectHighlitedColor.b + "\r\n");
         var2.write("seenNews=" + this.getSeenUpdateText() + "\r\n");
         var2.write("toggleToRun=" + this.isToggleToRun() + "\r\n");
         var2.write("celsius=" + this.isCelsius() + "\r\n");
         var2.write("riversideDone=" + this.isRiversideDone() + "\r\n");
         var2.write("mapOrder=");
         String var12 = "";
         if (this.mapOrder != null) {
            for(int var13 = 0; var13 < this.mapOrder.size(); ++var13) {
               var12 = var12 + (String)this.mapOrder.get(var13) + (var13 < this.mapOrder.size() - 1 ? ";" : "");
            }

            var2.write(var12 + "\r\n");
         }

         var2.write("showFirstTimeWeatherTutorial=" + this.isShowFirstTimeWeatherTutorial() + "\r\n");
         var2.write("uiRenderOffscreen=" + OptionUIFBO + "\r\n");
         var2.write("uiRenderFPS=" + OptionUIRenderFPS + "\r\n");
         var2.write("radialMenuKeyToggle=" + OptionRadialMenuKeyToggle + "\r\n");
         var2.write("panCameraWhileAiming=" + OptionPanCameraWhileAiming + "\r\n");
         var2.write("temperatureDisplayCelsius=" + OptionTemperatureDisplayCelsius + "\r\n");
         var2.write("doWindSpriteEffects=" + OptionDoWindSpriteEffects + "\r\n");
         var2.write("doDoorSpriteEffects=" + OptionDoDoorSpriteEffects + "\r\n");
      } catch (Exception var9) {
         var9.printStackTrace();
      } finally {
         var2.close();
      }

      OnceEvery.FPS = PerformanceSettings.LockFPS;
   }

   public void setWindowed(boolean var1) {
      RenderThread.borrowContext();
      if (var1 != fullScreen) {
         setDisplayMode(this.getScreenWidth(), this.getScreenHeight(), var1);
      }

      fullScreen = var1;
      if (fakefullscreen) {
         Display.setResizable(false);
      } else {
         Display.setResizable(!var1);
      }

      try {
         this.saveOptions();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      RenderThread.returnContext();
   }

   public boolean isFullScreen() {
      return fullScreen;
   }

   public static void restartApplication(Runnable var0) throws IOException {
      try {
         String var1 = System.getProperty("java.home") + "/bin/java";
         List var2 = ManagementFactory.getRuntimeMXBean().getInputArguments();
         StringBuffer var3 = new StringBuffer();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            if (!var5.contains("-agentlib")) {
               var3.append(var5);
               var3.append(" ");
            }
         }

         final StringBuffer var8 = new StringBuffer(var1 + " " + var3);
         String[] var9 = System.getProperty("sun.java.command").split(" ");
         if (var9[0].endsWith(".jar")) {
            var8.append("-jar " + (new File(var9[0])).getPath());
         } else {
            var8.append("-cp " + System.getProperty("java.class.path") + " " + var9[0]);
         }

         for(int var6 = 1; var6 < var9.length; ++var6) {
            var8.append(" ");
            var8.append(var9[var6]);
         }

         Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
               try {
                  DebugLog.log("Relaunching: " + var8.toString());
                  Runtime.getRuntime().exec(var8.toString());
               } catch (IOException var2) {
                  var2.printStackTrace();
               }

            }
         });
         if (var0 != null) {
            var0.run();
         }

         System.exit(0);
      } catch (Exception var7) {
         throw new IOException("Error while trying to restart the application", var7);
      }
   }

   public KahluaTable getScreenModes() {
      ArrayList var1 = new ArrayList();
      KahluaTable var2 = LuaManager.platform.newTable();
      File var3 = new File(LuaManager.getLuaCacheDir() + File.separator + "screenresolution.ini");
      int var4 = 1;

      try {
         Integer var6;
         if (!var3.exists()) {
            var3.createNewFile();
            FileWriter var5 = new FileWriter(var3);
            var6 = 0;
            Integer var7 = 0;
            DisplayMode[] var8 = Display.getAvailableDisplayModes();

            for(int var9 = 0; var9 < var8.length; ++var9) {
               var6 = var8[var9].getWidth();
               var7 = var8[var9].getHeight();
               if (!var1.contains(var6 + " x " + var7)) {
                  var2.rawset(var4, var6 + " x " + var7);
                  var5.write(var6 + " x " + var7 + " \r\n");
                  var1.add(var6 + " x " + var7);
                  ++var4;
               }
            }

            var5.close();
         } else {
            BufferedReader var11 = new BufferedReader(new FileReader(var3));

            String var12;
            for(var6 = null; (var12 = var11.readLine()) != null; ++var4) {
               var2.rawset(var4, var12.trim());
            }

            var11.close();
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }

      return var2;
   }

   public static void setDisplayMode(int var0, int var1, boolean var2) {
      if (Display.getWidth() != var0 || Display.getHeight() != var1 || Display.isFullscreen() != var2) {
         RenderThread.borrowContext();
         fullScreen = var2;

         try {
            DisplayMode var3 = null;
            if (!var2) {
               var3 = new DisplayMode(var0, var1);
            } else {
               DisplayMode[] var4 = Display.getAvailableDisplayModes();
               int var5 = 0;
               DisplayMode var6 = null;

               for(int var7 = 0; var7 < var4.length; ++var7) {
                  DisplayMode var8 = var4[var7];
                  if (var8.getWidth() == var0 && var8.getHeight() == var1 && var8.isFullscreenCapable()) {
                     if ((var3 == null || var8.getFrequency() >= var5) && (var3 == null || var8.getBitsPerPixel() > var3.getBitsPerPixel())) {
                        var3 = var8;
                        var5 = var8.getFrequency();
                     }

                     if (var8.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel() && var8.getFrequency() == Display.getDesktopDisplayMode().getFrequency()) {
                        var3 = var8;
                        break;
                     }
                  }

                  if (var8.isFullscreenCapable() && (var6 == null || Math.abs(var8.getWidth() - var0) < Math.abs(var6.getWidth() - var0) || var8.getWidth() == var6.getWidth() && var8.getFrequency() > var5)) {
                     var6 = var8;
                     var5 = var8.getFrequency();
                     System.out.println("closest width=" + var8.getWidth() + " freq=" + var8.getFrequency());
                  }
               }

               if (var3 == null && var6 != null) {
                  var3 = var6;
               }
            }

            if (var3 == null) {
               RenderThread.returnContext();
               DebugLog.log("Failed to find value mode: " + var0 + "x" + var1 + " fs=" + var2);
               return;
            }

            if (var2) {
               Display.setDisplayModeAndFullscreen(var3);
            } else {
               Display.setDisplayMode(var3);
               Display.setFullscreen(var2);
            }

            if (!var2 && OptionBorderlessWindow) {
               Display.setResizable(false);
            } else if (!var2 && !fakefullscreen) {
               Display.setResizable(false);
               Display.setResizable(true);
            }

            if (Display.isCreated()) {
               DebugLog.log("Display mode changed to " + Display.getWidth() + "x" + Display.getHeight() + " freq=" + Display.getDisplayMode().getFrequency() + " fullScreen=" + Display.isFullscreen());
            }
         } catch (LWJGLException var9) {
            DebugLog.log("Unable to setup mode " + var0 + "x" + var1 + " fullscreen=" + var2 + var9);
         }

         RenderThread.returnContext();
      }
   }

   public void updateKeyboard() {
      if (CurrentTextEntryBox == null) {
         while(true) {
            if (Keyboard.next()) {
               continue;
            }
         }
      } else if (CurrentTextEntryBox.IsEditable && CurrentTextEntryBox.DoingTextEntry) {
         while(true) {
            boolean var10;
            do {
               do {
                  while(true) {
                     do {
                        if (!Keyboard.next()) {
                           if (CurrentTextEntryBox != null && CurrentTextEntryBox.ignoreFirst) {
                              CurrentTextEntryBox.ignoreFirst = false;
                           }

                           return;
                        }
                     } while(!Keyboard.getEventKeyState());

                     int var8;
                     if (Keyboard.getEventKey() == 28) {
                        var10 = false;
                        if (UIManager.getDebugConsole() != null && CurrentTextEntryBox == UIManager.getDebugConsole().CommandLine) {
                           var10 = true;
                        }

                        if (CurrentTextEntryBox != null) {
                           UITextBox2 var7 = CurrentTextEntryBox;
                           if (var7.multipleLine) {
                              if (var7.Lines.size() < var7.getMaxLines()) {
                                 if (var7.TextEntryCursorPos != var7.ToSelectionIndex) {
                                    var8 = Math.min(var7.TextEntryCursorPos, var7.ToSelectionIndex);
                                    int var4 = Math.max(var7.TextEntryCursorPos, var7.ToSelectionIndex);
                                    if (var7.internalText.length() > 0) {
                                       var7.internalText = var7.internalText.substring(0, var8) + "\n" + var7.internalText.substring(var4);
                                    } else {
                                       var7.internalText = "\n";
                                    }

                                    var7.TextEntryCursorPos = var8 + 1;
                                 } else {
                                    var8 = var7.TextEntryCursorPos;
                                    String var9 = var7.internalText.substring(0, var8) + "\n" + var7.internalText.substring(var8);
                                    var7.SetText(var9);
                                    var7.TextEntryCursorPos = var8 + 1;
                                 }

                                 var7.ToSelectionIndex = var7.TextEntryCursorPos;
                                 var7.CursorLine = var7.toDisplayLine(var7.TextEntryCursorPos);
                              }
                           } else {
                              CurrentTextEntryBox.onCommandEntered();
                           }
                        }
                        break;
                     }

                     if (Keyboard.getEventKey() == 1) {
                        if (CurrentTextEntryBox != null) {
                           CurrentTextEntryBox.onOtherKey(1);
                           GameKeyboard.eatKeyPress(1);
                        }
                     } else if (Keyboard.getEventKey() == 15) {
                        if (CurrentTextEntryBox != null) {
                           CurrentTextEntryBox.onOtherKey(15);
                        }

                        LuaEventManager.triggerEvent("SwitchChatStream");
                     } else if (Keyboard.getEventKey() != 58) {
                        if (Keyboard.getEventKey() == 199) {
                           CurrentTextEntryBox.TextEntryCursorPos = 0;
                           if (!CurrentTextEntryBox.Lines.isEmpty()) {
                              CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine);
                           }

                           if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
                              CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
                           }

                           CurrentTextEntryBox.resetBlink();
                        } else if (Keyboard.getEventKey() == 207) {
                           CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.internalText.length();
                           if (!CurrentTextEntryBox.Lines.isEmpty()) {
                              CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine) + ((String)CurrentTextEntryBox.Lines.get(CurrentTextEntryBox.CursorLine)).length();
                           }

                           if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
                              CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
                           }

                           CurrentTextEntryBox.resetBlink();
                        } else {
                           int var1;
                           if (Keyboard.getEventKey() == 200) {
                              if (CurrentTextEntryBox.CursorLine > 0) {
                                 var1 = CurrentTextEntryBox.TextEntryCursorPos - CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine);
                                 --CurrentTextEntryBox.CursorLine;
                                 if (var1 > ((String)CurrentTextEntryBox.Lines.get(CurrentTextEntryBox.CursorLine)).length()) {
                                    var1 = ((String)CurrentTextEntryBox.Lines.get(CurrentTextEntryBox.CursorLine)).length();
                                 }

                                 CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine) + var1;
                                 if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
                                    CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
                                 }
                              }

                              CurrentTextEntryBox.onPressUp();
                           } else if (Keyboard.getEventKey() == 208) {
                              if (CurrentTextEntryBox.Lines.size() - 1 > CurrentTextEntryBox.CursorLine && CurrentTextEntryBox.CursorLine + 1 < CurrentTextEntryBox.getMaxLines()) {
                                 var1 = CurrentTextEntryBox.TextEntryCursorPos - CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine);
                                 ++CurrentTextEntryBox.CursorLine;
                                 if (var1 > ((String)CurrentTextEntryBox.Lines.get(CurrentTextEntryBox.CursorLine)).length()) {
                                    var1 = ((String)CurrentTextEntryBox.Lines.get(CurrentTextEntryBox.CursorLine)).length();
                                 }

                                 CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine) + var1;
                                 if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
                                    CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
                                 }
                              }

                              CurrentTextEntryBox.onPressDown();
                           } else if (Keyboard.getEventKey() != 29 && Keyboard.getEventKey() != 157 && Keyboard.getEventKey() != 56 && Keyboard.getEventKey() != 184) {
                              if (Keyboard.getEventKey() == 203) {
                                 --CurrentTextEntryBox.TextEntryCursorPos;
                                 if (CurrentTextEntryBox.TextEntryCursorPos < 0) {
                                    CurrentTextEntryBox.TextEntryCursorPos = 0;
                                 }

                                 if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
                                    CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
                                 }

                                 CurrentTextEntryBox.resetBlink();
                              } else if (Keyboard.getEventKey() == 205) {
                                 ++CurrentTextEntryBox.TextEntryCursorPos;
                                 if (CurrentTextEntryBox.TextEntryCursorPos > CurrentTextEntryBox.internalText.length()) {
                                    CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.internalText.length();
                                 }

                                 if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
                                    CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
                                 }

                                 CurrentTextEntryBox.resetBlink();
                              } else if (CurrentTextEntryBox != null && CurrentTextEntryBox.DoingTextEntry) {
                                 int var2;
                                 if ((Keyboard.getEventKey() == 211 || Keyboard.getEventKey() == 14) && CurrentTextEntryBox.TextEntryCursorPos != CurrentTextEntryBox.ToSelectionIndex) {
                                    var1 = Math.min(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
                                    var2 = Math.max(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
                                    CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, var1) + CurrentTextEntryBox.internalText.substring(var2);
                                    CurrentTextEntryBox.CursorLine = CurrentTextEntryBox.toDisplayLine(var1);
                                    CurrentTextEntryBox.ToSelectionIndex = var1;
                                    CurrentTextEntryBox.TextEntryCursorPos = var1;
                                    CurrentTextEntryBox.onTextChange();
                                 } else if (Keyboard.getEventKey() == 211) {
                                    if (CurrentTextEntryBox.internalText.length() > 0 && CurrentTextEntryBox.TextEntryCursorPos < CurrentTextEntryBox.internalText.length()) {
                                       if (CurrentTextEntryBox.TextEntryCursorPos > 0) {
                                          CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, CurrentTextEntryBox.TextEntryCursorPos) + CurrentTextEntryBox.internalText.substring(CurrentTextEntryBox.TextEntryCursorPos + 1);
                                       } else {
                                          CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(1);
                                       }

                                       CurrentTextEntryBox.onTextChange();
                                    }
                                 } else if (Keyboard.getEventKey() == 14) {
                                    if (CurrentTextEntryBox.internalText.length() > 0 && CurrentTextEntryBox.TextEntryCursorPos > 0) {
                                       if (CurrentTextEntryBox.TextEntryCursorPos > CurrentTextEntryBox.internalText.length()) {
                                          CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, CurrentTextEntryBox.internalText.length() - 1);
                                       } else {
                                          var1 = CurrentTextEntryBox.TextEntryCursorPos;
                                          CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, var1 - 1) + CurrentTextEntryBox.internalText.substring(var1);
                                       }

                                       if (CurrentTextEntryBox.TextEntryCursorPos > 0) {
                                          --CurrentTextEntryBox.TextEntryCursorPos;
                                          CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
                                       }

                                       CurrentTextEntryBox.onTextChange();
                                    }
                                 } else if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.getEventKey() == 47) {
                                    String var6 = Clipboard.getClipboard();
                                    if (var6 != null) {
                                       if (CurrentTextEntryBox.TextEntryCursorPos != CurrentTextEntryBox.ToSelectionIndex) {
                                          var2 = Math.min(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
                                          var8 = Math.max(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
                                          CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, var2) + var6 + CurrentTextEntryBox.internalText.substring(var8);
                                          CurrentTextEntryBox.ToSelectionIndex = var2 + var6.length();
                                          CurrentTextEntryBox.TextEntryCursorPos = var2 + var6.length();
                                       } else {
                                          if (CurrentTextEntryBox.TextEntryCursorPos < CurrentTextEntryBox.internalText.length()) {
                                             CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, CurrentTextEntryBox.TextEntryCursorPos) + var6 + CurrentTextEntryBox.internalText.substring(CurrentTextEntryBox.TextEntryCursorPos);
                                          } else {
                                             CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText + var6;
                                          }

                                          UITextBox2 var11 = CurrentTextEntryBox;
                                          var11.TextEntryCursorPos += var6.length();
                                          var11 = CurrentTextEntryBox;
                                          var11.ToSelectionIndex += var6.length();
                                       }

                                       CurrentTextEntryBox.onTextChange();
                                    }
                                 } else {
                                    String var3;
                                    if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.getEventKey() == 46) {
                                       if (CurrentTextEntryBox.TextEntryCursorPos != CurrentTextEntryBox.ToSelectionIndex) {
                                          var1 = Math.min(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
                                          var2 = Math.max(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
                                          CurrentTextEntryBox.updateText();
                                          var3 = CurrentTextEntryBox.Text.substring(var1, var2);
                                          if (var3 != null && var3.length() > 0) {
                                             Clipboard.setClipboard(var3);
                                          }
                                       }
                                    } else if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.getEventKey() == 45) {
                                       if (CurrentTextEntryBox.TextEntryCursorPos != CurrentTextEntryBox.ToSelectionIndex) {
                                          var1 = Math.min(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
                                          var2 = Math.max(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
                                          CurrentTextEntryBox.updateText();
                                          var3 = CurrentTextEntryBox.Text.substring(var1, var2);
                                          if (var3 != null && var3.length() > 0) {
                                             Clipboard.setClipboard(var3);
                                          }

                                          CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, var1) + CurrentTextEntryBox.internalText.substring(var2);
                                          CurrentTextEntryBox.ToSelectionIndex = var1;
                                          CurrentTextEntryBox.TextEntryCursorPos = var1;
                                       }
                                    } else if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.getEventKey() == 30) {
                                       CurrentTextEntryBox.TextEntryCursorPos = 0;
                                       CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.internalText.length();
                                    } else if (!CurrentTextEntryBox.ignoreFirst && Keyboard.getEventKey() != 42 && Keyboard.getEventKey() != 54 && CurrentTextEntryBox.internalText.length() <= CurrentTextEntryBox.TextEntryMaxLength) {
                                       if (CurrentTextEntryBox.isOnlyNumbers() && Keyboard.getEventCharacter() != '.' && Keyboard.getEventCharacter() != '-') {
                                          try {
                                             Double.parseDouble(String.valueOf(Keyboard.getEventCharacter()));
                                          } catch (Exception var5) {
                                             return;
                                          }
                                       }

                                       if (CurrentTextEntryBox.TextEntryCursorPos != CurrentTextEntryBox.ToSelectionIndex) {
                                          var1 = Math.min(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
                                          var2 = Math.max(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
                                          if (CurrentTextEntryBox.internalText.length() > 0) {
                                             CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, var1) + Keyboard.getEventCharacter() + CurrentTextEntryBox.internalText.substring(var2);
                                          } else {
                                             CurrentTextEntryBox.internalText = "" + Keyboard.getEventCharacter();
                                          }

                                          CurrentTextEntryBox.ToSelectionIndex = var1 + 1;
                                          CurrentTextEntryBox.TextEntryCursorPos = var1 + 1;
                                       } else {
                                          var1 = CurrentTextEntryBox.TextEntryCursorPos;
                                          if (var1 < CurrentTextEntryBox.internalText.length()) {
                                             CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, var1) + Keyboard.getEventCharacter() + CurrentTextEntryBox.internalText.substring(var1);
                                          } else {
                                             StringBuilder var10000 = new StringBuilder();
                                             UITextBox2 var10002 = CurrentTextEntryBox;
                                             var10002.internalText = var10000.append(var10002.internalText).append(Keyboard.getEventCharacter()).toString();
                                          }

                                          ++CurrentTextEntryBox.TextEntryCursorPos;
                                          ++CurrentTextEntryBox.ToSelectionIndex;
                                          CurrentTextEntryBox.onTextChange();
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               } while(!var10);
            } while(GameClient.bClient && GameClient.accessLevel.equals("") && (GameClient.connection == null || !GameClient.connection.isCoopHost));

            UIManager.getDebugConsole().ProcessCommand();
         }
      }

   }

   public void quit() {
      if (IsoPlayer.instance != null) {
         bExiting = true;
      } else {
         try {
            this.saveOptions();
         } catch (IOException var2) {
            var2.printStackTrace();
         }

         GameClient.instance.Shutdown();
         SteamUtils.shutdown();
         System.exit(0);
      }

   }

   public void exitToMenu() {
      bExiting = true;
   }

   public void quitToDesktop() {
      GameWindow.closeRequested = true;
   }

   public boolean supportRes(int var1, int var2) throws LWJGLException {
      DisplayMode[] var3 = Display.getAvailableDisplayModes();
      boolean var4 = false;

      for(int var5 = 0; var5 < var3.length; ++var5) {
         if (var3[var5].getWidth() == var1 && var3[var5].getHeight() == var2 && var3[var5].isFullscreenCapable()) {
            return true;
         }
      }

      return false;
   }

   public void init(int var1, int var2) throws LWJGLException {
      System.setProperty("org.lwjgl.opengl.Window.undecorated", OptionBorderlessWindow ? "true" : "false");
      Display.setVSyncEnabled(OptionVSync);
      if (!System.getProperty("os.name").contains("OS X") && !System.getProperty("os.name").startsWith("Win")) {
         DebugLog.log("Creating display. If this fails, you may need to install xrandr.");
      }

      setDisplayMode(var1, var2, fullScreen);

      try {
         Display.create(new PixelFormat(32, 0, 24, 8, 0));
      } catch (LWJGLException var4) {
         Display.destroy();
         Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
         Display.create(new PixelFormat(32, 0, 24, 8, 0));
      }

      fullScreen = Display.isFullscreen();
      DebugLog.log("OpenGL version: " + GL11.glGetString(7938));
      DebugLog.log("Desktop resolution " + Display.getDesktopDisplayMode().getWidth() + "x" + Display.getDesktopDisplayMode().getHeight());
      DebugLog.log("Initial resolution " + width + "x" + height + " fullScreen=" + fullScreen);
      GLVertexBufferObject.init();
      if (bIsSteam && GameWindow.OSValidator.isWindows()) {
      }

      Display.setVSyncEnabled(OptionVSync);
      GL11.glEnable(3553);
      IndieGL.glBlendFunc(770, 771);
      GL11.glTexEnvf(8960, 8704, 8448.0F);
      GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
   }

   private boolean setupMultiFBO() {
      try {
         if (!this.OffscreenBuffer.test()) {
            return false;
         } else {
            this.OffscreenBuffer.setZoomLevelsFromOption(TileScale == 2 ? OptionZoomLevels2x : OptionZoomLevels1x);
            this.OffscreenBuffer.create(Display.getWidth(), Display.getHeight());
            return true;
         }
      } catch (Exception var2) {
         var2.printStackTrace();
         return false;
      }
   }

   public void init(int var1, int var2, int var3, int var4, Canvas var5, Canvas var6) throws LWJGLException {
      width = var1;
      height = var2;
      if (var2 > 768 && this.supportsFBO() && this.OffscreenBuffer == null) {
         bDoubleSize = true;
      }

      canvas = var5;
      fullscreencanvas = var6;
      Display.setVSyncEnabled(false);
      GL11.glEnable(3553);
      GL11.glTexEnvf(8960, 8704, 8448.0F);
      IndieGL.glBlendFunc(770, 771);
      GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      this.sharedInit();
   }

   public void setScreenSize(int var1, int var2) {
      if (width != var1 || var2 != height) {
         int var3 = width;
         int var4 = height;
         DebugLog.log("Screen resolution changed from " + var3 + "x" + var4 + " to " + var1 + "x" + var2 + " fullScreen=" + fullScreen);
         width = var1;
         height = var2;
         if (this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null) {
            this.OffscreenBuffer.destroy();

            try {
               this.OffscreenBuffer.setZoomLevelsFromOption(TileScale == 2 ? OptionZoomLevels2x : OptionZoomLevels1x);
               this.OffscreenBuffer.create(var1, var2);
            } catch (Exception var8) {
               var8.printStackTrace();
            }
         }

         try {
            LuaEventManager.triggerEvent("OnResolutionChange", var3, var4, var1, var2);
         } catch (Exception var7) {
            var7.printStackTrace();
         }

         for(int var5 = 0; var5 < IsoPlayer.numPlayers; ++var5) {
            IsoPlayer var6 = IsoPlayer.players[var5];
            if (var6 != null) {
               var6.dirtyRecalcGridStackTime = 2.0F;
            }
         }
      }

   }

   public void setForceScreenSize() {
   }

   public static boolean supportCompressedTextures() {
      return GLContext.getCapabilities().GL_EXT_texture_compression_latc;
   }

   public void refreshOffscreen() {
   }

   public void StartFrame() {
      if (LuaManager.thread == null || !LuaManager.thread.bStep) {
         if (this.RenderShader != null && this.OffscreenBuffer.Current != null) {
            this.RenderShader.setTexture(this.OffscreenBuffer.getTexture(0));
         }

         SpriteRenderer.instance.preRender();
         UIManager.resize();
         boolean var1 = false;
         TextureID.TextureIDStack.clear();
         Texture.BindCount = 0;
         if (!var1) {
            IndieGL.glClear(18176);
         }

         if (this.OffscreenBuffer.Current != null) {
            SpriteRenderer.instance.glBuffer(1, 0);
         }

         IndieGL.glDoStartFrame(this.getOffscreenWidth(0), this.getOffscreenHeight(0), 0);
         if (DoFiltering) {
         }

         this.frameStage = 1;
      }
   }

   public void StartFrame(int var1, boolean var2) {
      if (!LuaManager.thread.bStep) {
         this.OffscreenBuffer.update();
         if (this.RenderShader != null && this.OffscreenBuffer.Current != null) {
            this.RenderShader.setTexture(this.OffscreenBuffer.getTexture(var1));
         }

         if (var2) {
            SpriteRenderer.instance.preRender();
         }

         boolean var3 = false;
         TextureID.TextureIDStack.clear();
         Texture.BindCount = 0;
         IndieGL.glLoadIdentity();
         if (this.OffscreenBuffer.Current != null) {
            SpriteRenderer.instance.glBuffer(1, var1);
         }

         IndieGL.glDoStartFrame(this.getOffscreenWidth(var1), this.getOffscreenHeight(var1), var1);
         IndieGL.glClear(17664);
         this.frameStage = 1;
      }
   }

   public TextureFBO getOffscreenBuffer() {
      return this.OffscreenBuffer.getCurrent(0);
   }

   public TextureFBO getOffscreenBuffer(int var1) {
      return this.OffscreenBuffer.getCurrent(var1);
   }

   public void setLastRenderedFBO(TextureFBO var1) {
      this.OffscreenBuffer.FBOrendered = var1;
   }

   public void DoStartFrameStuff(int var1, int var2, int var3) {
      this.DoStartFrameStuff(var1, var2, var3, false);
   }

   public void DoStartFrameStuff(int var1, int var2, int var3, boolean var4) {
      GL11.glEnable(3042);
      GL11.glDepthFunc(519);

      while(this.stack > 0) {
         GL11.glPopMatrix();
         GL11.glPopAttrib();
         this.stack -= 2;
      }

      GL11.glAlphaFunc(516, 0.0F);
      GL11.glPushAttrib(2048);
      ++this.stack;
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      ++this.stack;
      GL11.glLoadIdentity();
      GLU.gluOrtho2D(0.0F, (float)var1, (float)var2, 0.0F);
      GL11.glMatrixMode(5888);
      if (var3 != -1) {
         int var5;
         int var6;
         if (var4) {
            var5 = var1;
            var6 = var2;
         } else {
            var5 = this.getOffscreenTrueWidth();
            var6 = this.getOffscreenTrueHeight();
            if (IsoPlayer.numPlayers > 1) {
               var5 /= 2;
            }

            if (IsoPlayer.numPlayers > 2) {
               var6 /= 2;
            }
         }

         float var9 = 0.0F;
         float var10 = (float)(var5 * (var3 % 2));
         if (var3 < 2 && IsoPlayer.numPlayers > 2) {
            var9 += (float)var6;
         }

         GL11.glViewport((int)var10, (int)var9, var1, var2);
         GL11.glEnable(3089);
         GL11.glScissor((int)var10, (int)var9, var1, var2);
      } else {
         GL11.glViewport(0, 0, var1, var2);
      }

      GL11.glLoadIdentity();
      SpriteRenderer.instance.states[2].playerIndex = var3;
   }

   public void DoEndFrameStuffFx(int var1, int var2, int var3) {
      GL11.glPopAttrib();
      --this.stack;
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      --this.stack;
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
   }

   public void DoStartFrameStuffFx(int var1, int var2, int var3) {
      GL11.glEnable(3042);
      GL11.glDepthFunc(519);
      GL11.glAlphaFunc(516, 0.0F);
      GL11.glPushAttrib(2048);
      ++this.stack;
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      ++this.stack;
      GL11.glLoadIdentity();
      GLU.gluOrtho2D(0.0F, (float)var1, (float)var2, 0.0F);
      GL11.glMatrixMode(5888);
      if (var3 != -1) {
         int var4 = this.getOffscreenTrueWidth();
         int var5 = this.getOffscreenTrueHeight();
         if (IsoPlayer.numPlayers > 1) {
            var4 /= 2;
         }

         if (IsoPlayer.numPlayers > 2) {
            var5 /= 2;
         }

         float var8 = 0.0F;
         float var9 = (float)(var4 * (var3 % 2));
         if (var3 < 2 && IsoPlayer.numPlayers > 2) {
            var8 += (float)var5;
         }

         GL11.glViewport((int)var9, (int)var8, var1, var2);
         GL11.glEnable(3089);
         GL11.glScissor((int)var9, (int)var8, var1, var2);
      } else {
         GL11.glViewport(0, 0, var1, var2);
      }

      GL11.glLoadIdentity();
      SpriteRenderer.instance.states[2].playerIndex = var3;
   }

   public void PushIso() {
      SpriteRenderer.instance.PushIso();
   }

   public void PopIso() {
      SpriteRenderer.instance.PopIso();
   }

   public void DoPushIsoStuff() {
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      float var1 = 10.0F;
      float var2 = IsoCamera.CamCharacter.getX();
      float var3 = IsoCamera.CamCharacter.getY();
      float var4 = IsoCamera.CamCharacter.getZ();
      GL11.glOrtho(100.0D, -100.0D, -100.0D, 100.0D, -500.0D, 500.0D);
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GL11.glRotatef(25.264F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(225.0F, 0.0F, 1.0F, 0.0F);
      GL11.glTranslatef(80.0F, 0.0F, -80.0F);
      GL11.glScalef(110.0F, 110.0F, -110.0F);
   }

   public void DoPopIsoStuff() {
      GL11.glDepthFunc(519);
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
   }

   public void DoEndFrameStuff(int var1, int var2) {
      GL11.glPopAttrib();
      --this.stack;
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      --this.stack;
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glDisable(3089);
   }

   public void RenderOffScreenBuffer() {
      if (LuaManager.thread == null || !LuaManager.thread.bStep) {
         if (this.OffscreenBuffer.Current != null) {
            IndieGL.disableStencilTest();
            IndieGL.glDoStartFrame(width, height, -1);
            IndieGL.glDisable(3042);
            this.OffscreenBuffer.render();
            IndieGL.glDoEndFrame();
         }
      }
   }

   public void StartFrameText(int var1) {
      if (LuaManager.thread == null || !LuaManager.thread.bStep) {
         IndieGL.glDoStartFrame(IsoCamera.getScreenWidth(var1), IsoCamera.getScreenHeight(var1), var1, true);
         IndieGL.glTexParameteri(3553, 10241, 9729);
         IndieGL.glTexParameteri(3553, 10240, 9728);
         this.frameStage = 2;
      }
   }

   public boolean StartFrameUI() {
      if (LuaManager.thread != null && LuaManager.thread.bStep) {
         return false;
      } else {
         boolean var1 = true;
         if (UIManager.useUIFBO) {
            --this.UIRenderTick;
            if (this.UIRenderTick <= 0) {
               SpriteRenderer.instance.glBuffer(2, 0);
            } else {
               var1 = false;
            }
         }

         IndieGL.glDoStartFrame(width, height, -1);
         IndieGL.glClear(1024);
         UIManager.resize();
         IndieGL.glTexParameteri(3553, 10241, 9729);
         IndieGL.glTexParameteri(3553, 10240, 9728);
         this.frameStage = 3;
         return var1;
      }
   }

   public void StartFrameUIOld() {
      if (LuaManager.thread == null || !LuaManager.thread.bStep) {
         if (this.OffscreenBuffer.Current != null) {
            IndieGL.disableStencilTest();
         }

         if (this.OffscreenBuffer.Current != null) {
            IndieGL.glClear(17664);
         }

         IndieGL.glTexParameteri(3553, 10241, 9729);
         IndieGL.glTexParameteri(3553, 10240, 9728);
         IndieGL.glDoStartFrame(width, height, -1);
         if (DoFiltering) {
         }

         UIManager.resize();
         this.OffscreenBuffer.render();
         IndieGL.glTexParameteri(3553, 10241, 9729);
         IndieGL.glTexParameteri(3553, 10240, 9728);
         this.frameStage = 3;
      }
   }

   public Map getKeyMaps() {
      return this.keyMaps;
   }

   public void setKeyMaps(Map var1) {
      this.keyMaps = var1;
   }

   public void reinitKeyMaps() {
      this.keyMaps = new HashMap();
   }

   public int getKey(String var1) {
      if (this.keyMaps == null) {
         return 0;
      } else {
         return this.keyMaps.get(var1) != null ? (Integer)this.keyMaps.get(var1) : 0;
      }
   }

   public void addKeyBinding(String var1, Integer var2) {
      if (this.keyMaps == null) {
         this.keyMaps = new HashMap();
      }

      this.keyMaps.put(var1, var2);
   }

   public static boolean isLastStand() {
      return bLastStand;
   }

   public String getVersionNumber() {
      return this.versionNumber;
   }

   public String getSteamServerVersion() {
      return this.steamServerVersion;
   }

   public void DoFrameReady() {
      if (GameWindow.OSValidator.isMac()) {
         if (Mouse.isCreated()) {
            Mouse.poll();
            Mouse.updateCursor();
         }

         if (Keyboard.isCreated()) {
            Keyboard.poll();
         }

         if (Controllers.isCreated()) {
            Controllers.poll();
         }
      } else {
         Display.processMessages();
      }

      getInstance().updateKeyboard();
   }

   public float getZoom(int var1) {
      return this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null ? this.OffscreenBuffer.zoom[var1] : 1.0F;
   }

   public float getNextZoom(int var1, int var2) {
      return this.OffscreenBuffer != null ? this.OffscreenBuffer.getNextZoom(var1, var2) : 1.0F;
   }

   public float getMinZoom() {
      return this.OffscreenBuffer != null ? this.OffscreenBuffer.getMinZoom() : 1.0F;
   }

   public float getMaxZoom() {
      return this.OffscreenBuffer != null ? this.OffscreenBuffer.getMaxZoom() : 1.0F;
   }

   public void doZoomScroll(int var1, int var2) {
      if (this.OffscreenBuffer != null) {
         this.OffscreenBuffer.doZoomScroll(var1, var2);
      }

   }

   public String getSaveFolder() {
      return this.saveFolder;
   }

   public void setSaveFolder(String var1) {
      if (!this.saveFolder.equals(var1)) {
         File var2 = new File(var1);
         if (!var2.exists()) {
            var2.mkdir();
         }

         var2 = new File(var1 + File.separator + "mods");
         if (!var2.exists()) {
            var2.mkdir();
         }

         String var3 = this.saveFolder + File.separator;
         this.saveFolder = var1;
         this.copyPasteFolders(var3 + "mods");
         this.deleteDirectoryRecusrively(var3);
      }

   }

   public void deleteDirectoryRecusrively(String var1) {
      File var2 = new File(var1);
      String[] var3 = var2.list();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         File var5 = new File(var1 + File.separator + var3[var4]);
         if (var5.isDirectory()) {
            this.deleteDirectoryRecusrively(var1 + File.separator + var3[var4]);
         } else {
            var5.delete();
         }
      }

      var2.delete();
   }

   public boolean getOptionZoom() {
      return OptionZoom;
   }

   public void setOptionZoom(boolean var1) {
      OptionZoom = var1;
   }

   public void zoomOptionChanged(boolean var1) {
      if (!var1) {
         SafeMode = SafeModeForced;
         this.OffscreenBuffer.bZoomEnabled = OptionZoom && !SafeModeForced;
      } else {
         RenderThread.borrowContext();
         if (OptionZoom && !SafeModeForced) {
            SafeMode = false;
            this.bSupportsFBO = true;
            this.OffscreenBuffer.bZoomEnabled = true;
            this.supportsFBO();
         } else {
            this.OffscreenBuffer.destroy();
            SafeMode = true;
            this.bSupportsFBO = false;
            this.OffscreenBuffer.bZoomEnabled = false;
         }

         RenderThread.returnContext();
         DebugLog.log("SafeMode is " + (SafeMode ? "on" : "off"));
      }
   }

   public void zoomLevelsChanged() {
      if (this.OffscreenBuffer.Current != null) {
         RenderThread.borrowContext();
         this.OffscreenBuffer.destroy();
         this.zoomOptionChanged(true);
         RenderThread.returnContext();
      }

   }

   public boolean isZoomEnabled() {
      return this.OffscreenBuffer.bZoomEnabled;
   }

   public void initFBOs() {
      if (OptionZoom && !SafeModeForced) {
         RenderThread.borrowContext();
         this.supportsFBO();
         RenderThread.returnContext();
      } else {
         SafeMode = true;
         this.OffscreenBuffer.bZoomEnabled = false;
      }

      DebugLog.log("SafeMode is " + (SafeMode ? "on" : "off"));
   }

   public boolean getAutoZoom(int var1) {
      return bAutoZoom[var1];
   }

   public void setAutoZoom(int var1, boolean var2) {
      bAutoZoom[var1] = var2;
      if (this.OffscreenBuffer != null) {
         this.OffscreenBuffer.bAutoZoom[var1] = var2;
      }

   }

   public boolean getOptionVSync() {
      return OptionVSync;
   }

   public void setOptionVSync(boolean var1) {
      OptionVSync = var1;
      RenderThread.borrowContext();

      try {
         Display.setVSyncEnabled(var1);
      } finally {
         RenderThread.returnContext();
      }

   }

   public int getOptionSoundVolume() {
      return OptionSoundVolume;
   }

   public float getRealOptionSoundVolume() {
      return (float)OptionSoundVolume / 10.0F;
   }

   public void setOptionSoundVolume(int var1) {
      OptionSoundVolume = Math.max(0, Math.min(10, var1));
      if (!GameClient.bClient || !GameSounds.soundIsPaused) {
         if (SoundManager.instance != null) {
            SoundManager.instance.setSoundVolume((float)var1 / 10.0F);
         }

      }
   }

   public int getOptionMusicVolume() {
      return OptionMusicVolume;
   }

   public void setOptionMusicVolume(int var1) {
      OptionMusicVolume = Math.max(0, Math.min(10, var1));
      if (!GameClient.bClient || !GameSounds.soundIsPaused) {
         if (SoundManager.instance != null) {
            SoundManager.instance.setMusicVolume((float)var1 / 10.0F);
         }

      }
   }

   public int getOptionAmbientVolume() {
      return OptionAmbientVolume;
   }

   public void setOptionAmbientVolume(int var1) {
      OptionAmbientVolume = Math.max(0, Math.min(10, var1));
      if (!GameClient.bClient || !GameSounds.soundIsPaused) {
         if (SoundManager.instance != null) {
            SoundManager.instance.setAmbientVolume((float)var1 / 10.0F);
         }

      }
   }

   public int getOptionMusicLibrary() {
      return OptionMusicLibrary;
   }

   public void setOptionMusicLibrary(int var1) {
      if (var1 < 1) {
         var1 = 1;
      }

      if (var1 > 3) {
         var1 = 3;
      }

      OptionMusicLibrary = var1;
   }

   public int getOptionVehicleEngineVolume() {
      return OptionVehicleEngineVolume;
   }

   public void setOptionVehicleEngineVolume(int var1) {
      OptionVehicleEngineVolume = Math.max(0, Math.min(10, var1));
      if (!GameClient.bClient || !GameSounds.soundIsPaused) {
         if (SoundManager.instance != null) {
            SoundManager.instance.setVehicleEngineVolume((float)OptionVehicleEngineVolume / 10.0F);
         }

      }
   }

   public boolean getOptionVoiceEnable() {
      return OptionVoiceEnable;
   }

   public void setOptionVoiceEnable(boolean var1) {
      OptionVoiceEnable = var1;
   }

   public int getOptionVoiceMode() {
      return OptionVoiceMode;
   }

   public void setOptionVoiceMode(int var1) {
      OptionVoiceMode = var1;
      VoiceManager.instance.setMode(var1);
   }

   public int getOptionVoiceVADMode() {
      return OptionVoiceVADMode;
   }

   public void setOptionVoiceVADMode(int var1) {
      OptionVoiceVADMode = var1;
      VoiceManager.instance.setVADMode(var1);
   }

   public int getOptionVoiceVolumeMic() {
      return OptionVoiceVolumeMic;
   }

   public void setOptionVoiceVolumeMic(int var1) {
      OptionVoiceVolumeMic = var1;
      VoiceManager.instance.setVolumeMic(var1);
   }

   public int getOptionVoiceVolumePlayers() {
      return OptionVoiceVolumePlayers;
   }

   public void setOptionVoiceVolumePlayers(int var1) {
      OptionVoiceVolumePlayers = var1;
      VoiceManager.instance.setVolumePlayers(var1);
   }

   public String getOptionVoiceRecordDeviceName() {
      return OptionVoiceRecordDeviceName;
   }

   public void setOptionVoiceRecordDeviceName(String var1) {
      OptionVoiceRecordDeviceName = var1;
      VoiceManager.instance.UpdateRecordDevice();
   }

   public int getOptionVoiceRecordDevice() {
      if (!SoundDisabled && !VoiceManager.VoipDisabled) {
         int var1 = javafmod.FMOD_System_GetRecordNumDrivers();

         for(int var2 = 0; var2 < var1; ++var2) {
            FMOD_DriverInfo var3 = new FMOD_DriverInfo();
            javafmod.FMOD_System_GetRecordDriverInfo(var2, var3);
            if (var3.name.equals(OptionVoiceRecordDeviceName)) {
               return var2 + 1;
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   public void setOptionVoiceRecordDevice(int var1) {
      if (!SoundDisabled && !VoiceManager.VoipDisabled) {
         if (var1 >= 1) {
            FMOD_DriverInfo var2 = new FMOD_DriverInfo();
            javafmod.FMOD_System_GetRecordDriverInfo(var1 - 1, var2);
            OptionVoiceRecordDeviceName = var2.name;
            VoiceManager.instance.UpdateRecordDevice();
         }
      }
   }

   public int getMicVolumeIndicator() {
      return VoiceManager.instance.getMicVolumeIndicator();
   }

   public boolean getMicVolumeError() {
      return VoiceManager.instance.getMicVolumeError();
   }

   public boolean getServerVOIPEnable() {
      return VoiceManager.instance.getServerVOIPEnable();
   }

   public void setTestingMicrophone(boolean var1) {
      VoiceManager.instance.setTestingMicrophone(var1);
   }

   public int getOptionReloadDifficulty() {
      return OptionReloadDifficulty;
   }

   public void setOptionReloadDifficulty(int var1) {
      OptionReloadDifficulty = Math.max(1, Math.min(3, var1));
   }

   public boolean getOptionRackProgress() {
      return OptionRackProgress;
   }

   public void setOptionRackProgress(boolean var1) {
      OptionRackProgress = var1;
   }

   public String getOptionContextMenuFont() {
      return OptionContextMenuFont;
   }

   public void setOptionContextMenuFont(String var1) {
      OptionContextMenuFont = var1;
   }

   public String getOptionInventoryFont() {
      return OptionInventoryFont;
   }

   public void setOptionInventoryFont(String var1) {
      OptionInventoryFont = var1;
   }

   public String getOptionTooltipFont() {
      return OptionTooltipFont;
   }

   public void setOptionTooltipFont(String var1) {
      OptionTooltipFont = var1;
      ObjectTooltip.checkFont();
   }

   public String getOptionMeasurementFormat() {
      return OptionMeasurementFormat;
   }

   public void setOptionMeasurementFormat(String var1) {
      OptionMeasurementFormat = var1;
   }

   public int getOptionClockFormat() {
      return OptionClockFormat;
   }

   public void setOptionClockFormat(int var1) {
      if (var1 < 1) {
         var1 = 1;
      }

      if (var1 > 2) {
         var1 = 2;
      }

      OptionClockFormat = var1;
   }

   public boolean getOptionClock24Hour() {
      return OptionClock24Hour;
   }

   public void setOptionClock24Hour(boolean var1) {
      OptionClock24Hour = var1;
   }

   public boolean getOptionModsEnabled() {
      return OptionModsEnabled;
   }

   public void setOptionModsEnabled(boolean var1) {
      OptionModsEnabled = var1;
   }

   public int getOptionBloodDecals() {
      return OptionBloodDecals;
   }

   public void setOptionBloodDecals(int var1) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var1 > 10) {
         var1 = 10;
      }

      OptionBloodDecals = var1;
   }

   public boolean getOptionBorderlessWindow() {
      return OptionBorderlessWindow;
   }

   public void setOptionBorderlessWindow(boolean var1) {
      OptionBorderlessWindow = var1;
   }

   public boolean getOptionTextureCompression() {
      return OptionTextureCompression;
   }

   public void setOptionTextureCompression(boolean var1) {
      OptionTextureCompression = var1;
   }

   public boolean getOptionTexture2x() {
      return OptionTexture2x;
   }

   public void setOptionTexture2x(boolean var1) {
      OptionTexture2x = var1;
   }

   public String getOptionZoomLevels1x() {
      return OptionZoomLevels1x;
   }

   public void setOptionZoomLevels1x(String var1) {
      OptionZoomLevels1x = var1 == null ? "" : var1;
   }

   public String getOptionZoomLevels2x() {
      return OptionZoomLevels2x;
   }

   public void setOptionZoomLevels2x(String var1) {
      OptionZoomLevels2x = var1 == null ? "" : var1;
   }

   public ArrayList getDefaultZoomLevels() {
      return this.OffscreenBuffer.getDefaultZoomLevels();
   }

   public void setOptionActiveController(int var1, boolean var2) {
      if (var1 >= 0 && var1 < GameWindow.GameInput.getControllerCount()) {
         Controller var3 = GameWindow.GameInput.getController(var1);
         if (var3 != null) {
            JoypadManager.instance.setControllerActive(var3.getName(), var2);
         }

      }
   }

   public boolean getOptionActiveController(String var1) {
      return JoypadManager.instance.ActiveControllerNames.contains(var1);
   }

   public boolean isOptionShowChatTimestamp() {
      return OptionShowChatTimestamp;
   }

   public void setOptionShowChatTimestamp(boolean var1) {
      OptionShowChatTimestamp = var1;
   }

   public boolean isOptionShowChatTitle() {
      return OptionShowChatTitle;
   }

   public String getOptionChatFontSize() {
      return OptionChatFontSize;
   }

   public void setOptionChatFontSize(String var1) {
      OptionChatFontSize = var1;
   }

   public void setOptionShowChatTitle(boolean var1) {
      OptionShowChatTitle = var1;
   }

   public float getOptionMinChatOpaque() {
      return OptionMinChatOpaque;
   }

   public void setOptionMinChatOpaque(float var1) {
      OptionMinChatOpaque = var1;
   }

   public float getOptionMaxChatOpaque() {
      return OptionMaxChatOpaque;
   }

   public void setOptionMaxChatOpaque(float var1) {
      OptionMaxChatOpaque = var1;
   }

   public float getOptionChatFadeTime() {
      return OptionChatFadeTime;
   }

   public void setOptionChatFadeTime(float var1) {
      OptionChatFadeTime = var1;
   }

   public boolean getOptionChatOpaqueOnFocus() {
      return OptionChatOpaqueOnFocus;
   }

   public void setOptionChatOpaqueOnFocus(boolean var1) {
      OptionChatOpaqueOnFocus = var1;
   }

   public boolean getOptionUIFBO() {
      return OptionUIFBO;
   }

   public void setOptionUIFBO(boolean var1) {
      OptionUIFBO = var1;
      if (GameWindow.states.current == IngameState.instance) {
         UIManager.useUIFBO = getInstance().supportsFBO() && OptionUIFBO;
      }

   }

   public int getOptionUIRenderFPS() {
      return OptionUIRenderFPS;
   }

   public void setOptionUIRenderFPS(int var1) {
      OptionUIRenderFPS = var1;
   }

   public void setOptionRadialMenuKeyToggle(boolean var1) {
      OptionRadialMenuKeyToggle = var1;
   }

   public boolean getOptionRadialMenuKeyToggle() {
      return OptionRadialMenuKeyToggle;
   }

   public void setOptionPanCameraWhileAiming(boolean var1) {
      OptionPanCameraWhileAiming = var1;
   }

   public boolean getOptionPanCameraWhileAiming() {
      return OptionPanCameraWhileAiming;
   }

   public void ResetLua(boolean var1, String var2) {
      if (SpriteRenderer.instance != null) {
         GameWindow.DrawReloadingLua = true;
         GameWindow.render();
         GameWindow.DrawReloadingLua = false;
      }

      ScriptManager.instance.Reset();
      GameSounds.Reset();
      LuaEventManager.Reset();
      MapObjects.Reset();
      UIManager.init();
      SurvivorFactory.Reset();
      ProfessionFactory.Reset();
      TraitFactory.Reset();
      ChooseGameInfo.Reset();
      LuaHookManager.Reset();
      LuaManager.init();
      JoypadManager.instance.Reset();
      GameKeyboard.doLuaKeyPressed = true;
      Texture.nullTextures.clear();
      ZomboidFileSystem.instance.Reset();
      ZomboidFileSystem.instance.init();
      ZomboidFileSystem.instance.loadMods();
      Translator.loadFiles();
      ScriptManager.instance.Load();

      try {
         LuaManager.LoadDirBase();
      } catch (Exception var6) {
         ExceptionLogger.logException(var6);
         GameWindow.DoLoadingText("Reloading Lua - ERRORS!");

         try {
            Thread.sleep(2000L);
         } catch (InterruptedException var5) {
         }
      }

      ZomboidGlobals.Load();
      LuaEventManager.triggerEvent("OnGameBoot");
      LuaEventManager.triggerEvent("OnMainMenuEnter");
      LuaEventManager.triggerEvent("OnResetLua", var2);
   }

   public boolean isShowPing() {
      return this.showPing;
   }

   public void setShowPing(boolean var1) {
      this.showPing = var1;
   }

   public boolean isForceSnow() {
      return this.forceSnow;
   }

   public void setForceSnow(boolean var1) {
      this.forceSnow = var1;
   }

   public boolean isZombieGroupSound() {
      return this.zombieGroupSound;
   }

   public void setZombieGroupSound(boolean var1) {
      this.zombieGroupSound = var1;
   }

   public String getBlinkingMoodle() {
      return this.blinkingMoodle;
   }

   public void setBlinkingMoodle(String var1) {
      this.blinkingMoodle = var1;
   }

   public boolean isTutorialDone() {
      return this.tutorialDone;
   }

   public void setTutorialDone(boolean var1) {
      this.tutorialDone = var1;
   }

   public boolean isVehiclesWarningShow() {
      return this.vehiclesWarningShow;
   }

   public void setVehiclesWarningShow(boolean var1) {
      this.vehiclesWarningShow = var1;
   }

   public void initPoisonousBerry() {
      ArrayList var1 = new ArrayList();
      var1.add("Base.BerryGeneric1");
      var1.add("Base.BerryGeneric2");
      var1.add("Base.BerryGeneric3");
      var1.add("Base.BerryGeneric4");
      var1.add("Base.BerryGeneric5");
      var1.add("Base.BerryPoisonIvy");
      this.setPoisonousBerry((String)var1.get(Rand.Next(0, var1.size() - 1)));
   }

   public void initPoisonousMushroom() {
      ArrayList var1 = new ArrayList();
      var1.add("Base.MushroomGeneric1");
      var1.add("Base.MushroomGeneric2");
      var1.add("Base.MushroomGeneric3");
      var1.add("Base.MushroomGeneric4");
      var1.add("Base.MushroomGeneric5");
      var1.add("Base.MushroomGeneric6");
      var1.add("Base.MushroomGeneric7");
      this.setPoisonousMushroom((String)var1.get(Rand.Next(0, var1.size() - 1)));
   }

   public String getPoisonousBerry() {
      return this.poisonousBerry;
   }

   public void setPoisonousBerry(String var1) {
      this.poisonousBerry = var1;
   }

   public String getPoisonousMushroom() {
      return this.poisonousMushroom;
   }

   public void setPoisonousMushroom(String var1) {
      this.poisonousMushroom = var1;
   }

   public static String getDifficulty() {
      return difficulty;
   }

   public static void setDifficulty(String var0) {
      difficulty = var0;
   }

   public boolean isDoneNewSaveFolder() {
      return this.doneNewSaveFolder;
   }

   public void setDoneNewSaveFolder(boolean var1) {
      this.doneNewSaveFolder = var1;
   }

   public static int getTileScale() {
      return TileScale;
   }

   public boolean isSelectingAll() {
      return this.isSelectingAll;
   }

   public void setIsSelectingAll(boolean var1) {
      this.isSelectingAll = var1;
   }

   public boolean getContentTranslationsEnabled() {
      return OptionEnableContentTranslations;
   }

   public void setContentTranslationsEnabled(boolean var1) {
      OptionEnableContentTranslations = var1;
   }

   public boolean isShowYourUsername() {
      return this.showYourUsername;
   }

   public void setShowYourUsername(boolean var1) {
      this.showYourUsername = var1;
   }

   public ColorInfo getMpTextColor() {
      if (this.mpTextColor == null) {
         this.mpTextColor = new ColorInfo((float)(Rand.Next(135) + 120) / 255.0F, (float)(Rand.Next(135) + 120) / 255.0F, (float)(Rand.Next(135) + 120) / 255.0F, 1.0F);
      }

      return this.mpTextColor;
   }

   public void setMpTextColor(ColorInfo var1) {
      if (var1.r < 0.19F) {
         var1.r = 0.19F;
      }

      if (var1.g < 0.19F) {
         var1.g = 0.19F;
      }

      if (var1.b < 0.19F) {
         var1.b = 0.19F;
      }

      this.mpTextColor = var1;
   }

   public boolean isAzerty() {
      return this.isAzerty;
   }

   public void setAzerty(boolean var1) {
      this.isAzerty = var1;
   }

   public ColorInfo getObjectHighlitedColor() {
      return this.objectHighlitedColor;
   }

   public void setObjectHighlitedColor(ColorInfo var1) {
      this.objectHighlitedColor = var1;
   }

   public String getSeenUpdateText() {
      return this.seenUpdateText;
   }

   public void setSeenUpdateText(String var1) {
      this.seenUpdateText = var1;
   }

   public boolean isToggleToRun() {
      return this.toggleToRun;
   }

   public void setToggleToRun(boolean var1) {
      this.toggleToRun = var1;
   }

   public int getXAngle(int var1, float var2) {
      double var3 = Math.toRadians((double)(225.0F + var2));
      int var5 = (new Long(Math.round((Math.sqrt(2.0D) * Math.cos(var3) + 1.0D) * (double)(var1 / 2)))).intValue();
      return var5;
   }

   public int getYAngle(int var1, float var2) {
      double var3 = Math.toRadians((double)(225.0F + var2));
      int var5 = (new Long(Math.round((Math.sqrt(2.0D) * Math.sin(var3) + 1.0D) * (double)(var1 / 2)))).intValue();
      return var5;
   }

   public boolean isCelsius() {
      return this.celsius;
   }

   public void setCelsius(boolean var1) {
      this.celsius = var1;
   }

   public boolean isInDebug() {
      return bDebug;
   }

   public boolean doWarnMapConflict() {
      return this.warnMapConflict;
   }

   public void setWarnMapConflict(boolean var1) {
      this.warnMapConflict = var1;
   }

   public void setMapOrder(LinkedList var1) {
      this.mapOrder = var1;
   }

   public LinkedList getMapOrder() {
      return this.mapOrder;
   }

   public boolean isRiversideDone() {
      return this.riversideDone;
   }

   public void setRiversideDone(boolean var1) {
      this.riversideDone = var1;
   }

   public boolean isNoSave() {
      return this.noSave;
   }

   public void setNoSave(boolean var1) {
      this.noSave = var1;
   }

   public boolean isShowFirstTimeVehicleTutorial() {
      return this.showFirstTimeVehicleTutorial;
   }

   public void setShowFirstTimeVehicleTutorial(boolean var1) {
      this.showFirstTimeVehicleTutorial = var1;
   }

   public boolean getOptionDisplayAsCelsius() {
      return OptionTemperatureDisplayCelsius;
   }

   public void setOptionDisplayAsCelsius(boolean var1) {
      OptionTemperatureDisplayCelsius = var1;
   }

   public boolean isShowFirstTimeWeatherTutorial() {
      return this.showFirstTimeWeatherTutorial;
   }

   public void setShowFirstTimeWeatherTutorial(boolean var1) {
      this.showFirstTimeWeatherTutorial = var1;
   }

   public boolean getOptionDoWindSpriteEffects() {
      return OptionDoWindSpriteEffects;
   }

   public void setOptionDoWindSpriteEffects(boolean var1) {
      OptionDoWindSpriteEffects = var1;
   }

   public boolean getOptionDoDoorSpriteEffects() {
      return OptionDoDoorSpriteEffects;
   }

   public void setOptionDoDoorSpriteEffects(boolean var1) {
      OptionDoDoorSpriteEffects = var1;
   }
}
