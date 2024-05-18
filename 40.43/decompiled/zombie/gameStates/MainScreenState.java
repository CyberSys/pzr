package zombie.gameStates;

import fmod.fmod.Audio;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.OpenGLException;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.ProxyPrintStream;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.logger.ZipLogs;
import zombie.core.raknet.VoiceManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;

public class MainScreenState extends GameState {
   public static String Version = "RC 3";
   public static Audio ambient;
   public static Audio musicTrack;
   public static float totalScale = 1.0F;
   public float alpha = 1.0F;
   public float alphaStep = 0.03F;
   public int creditID = 0;
   private int RestartDebounceClickTimer = 10;
   public ArrayList Elements = new ArrayList(16);
   public float targetAlpha = 1.0F;
   MainScreenState.ScreenElement City;
   MainScreenState.ScreenElement Cloud0;
   MainScreenState.ScreenElement Cloud1;
   MainScreenState.ScreenElement Cloud2;
   MainScreenState.Credit Current;
   MainScreenState.ScreenElement Ground;
   MainScreenState.ScreenElement Guy;
   int lastH;
   int lastW;
   MainScreenState.ScreenElement Logo;
   MainScreenState.Credit Next;
   MainScreenState.ScreenElement StoryModeText;
   MainScreenState.ScreenElement SandboxModeText;
   float creditDelay = 300.0F;
   static IngameState ingameState;
   private int vertShader = 0;
   private int fragShader = 0;
   static int shader = -1;
   public static MainScreenState instance;
   public boolean showLogo = false;
   private float FadeAlpha = 0.0F;
   float lightningTime = 0.0F;
   float lastLightningTime = 0.0F;
   long Checksum = 0L;
   public float lightningDelta = 0.0F;
   public float lightningTargetDelta = 0.0F;
   public float lightningFullTimer = 0.0F;
   public float lightningCount = 0.0F;
   public float lightOffCount = 0.0F;
   private ConnectToServerState connectToServerState;

   public static void main(String[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         if (var0[var1] != null && var0[var1].startsWith("-cachedir=")) {
            GameWindow.setCacheDir(var0[var1].replace("-cachedir=", "").trim());
         }
      }

      File var14 = new File(GameWindow.getCacheDir());
      if (!var14.exists()) {
         var14.mkdirs();
      }

      ZipLogs.addZipFile(false);
      String var2 = GameWindow.getCacheDir() + File.separator + "console.txt";

      try {
         FileOutputStream var3 = new FileOutputStream(var2);
         PrintStream var4 = new PrintStream(var3, true);
         System.setOut(new ProxyPrintStream(System.out, var4));
         System.setErr(new ProxyPrintStream(System.err, var4));
      } catch (FileNotFoundException var13) {
         var13.printStackTrace();
      }

      SimpleDateFormat var15 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
      System.out.println(var15.format(Calendar.getInstance().getTime()));
      System.out.println("cachedir is \"" + GameWindow.getCacheDir() + "\"");
      System.getProperties().list(System.out);
      System.out.println("-----");
      System.out.println("versionNumber=" + Core.getInstance().getVersionNumber() + " demo=false");
      Display.setIcon(loadIcons());
      Rand.init();

      for(int var16 = 0; var16 < var0.length; ++var16) {
         if (var0[var16] != null) {
            if (var0[var16].contains("safemode")) {
               Core.SafeMode = true;
               Core.SafeModeForced = true;
            } else if (var0[var16].equals("-nosound")) {
               Core.SoundDisabled = true;
            } else if (var0[var16].equals("-novoip")) {
               VoiceManager.VoipDisabled = true;
            } else if (var0[var16].equals("-debug")) {
               Core.bDebug = true;
            } else if (var0[var16].startsWith("-debuglog=")) {
               String[] var5 = var0[var16].replace("-debuglog=", "").split(",");
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  String var8 = var5[var7];

                  try {
                     DebugLog.enableLog(DebugType.valueOf(var8), true);
                  } catch (IllegalArgumentException var12) {
                  }
               }
            } else if (!var0[var16].startsWith("-cachedir=")) {
               if (var0[var16].equals("+connect")) {
                  if (var16 + 1 < var0.length) {
                     System.setProperty("args.server.connect", var0[var16 + 1]);
                  }

                  ++var16;
               } else if (var0[var16].equals("+password")) {
                  if (var16 + 1 < var0.length) {
                     System.setProperty("args.server.password", var0[var16 + 1]);
                  }

                  ++var16;
               } else if (var0[var16].contains("-debugtranslation")) {
                  Translator.debug = true;
               } else if ("-modfolders".equals(var0[var16])) {
                  if (var16 + 1 < var0.length) {
                     ZomboidFileSystem.instance.setModFoldersOrder(var0[var16 + 1]);
                  }

                  ++var16;
               } else if (var0[var16].equals("-nosteam")) {
                  System.setProperty("zomboid.steam", "0");
               } else {
                  DebugLog.log("unknown option \"" + var0[var16] + "\"");
               }
            }
         }
      }

      try {
         GameWindow.maina(false, Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight(), 5);
      } catch (OpenGLException var10) {
         File var17 = new File(GameWindow.getCacheDir() + File.separator + "options2.bin");
         var17.delete();
         var10.printStackTrace();
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }

   private int createVertShader(String var1) {
      this.vertShader = ARBShaderObjects.glCreateShaderObjectARB(35633);
      if (this.vertShader == 0) {
         return 0;
      } else {
         String var2 = "";

         String var3;
         try {
            for(BufferedReader var4 = new BufferedReader(new FileReader(var1)); (var3 = var4.readLine()) != null; var2 = var2 + var3 + "\n") {
            }
         } catch (Exception var5) {
            DebugLog.log("Fail reading vertex shading code");
            return 0;
         }

         ARBShaderObjects.glShaderSourceARB(this.vertShader, var2);
         ARBShaderObjects.glCompileShaderARB(this.vertShader);
         if (ARBShaderObjects.glGetObjectParameteriARB(this.vertShader, 35713) == 0) {
            this.vertShader = 0;
         }

         return this.vertShader;
      }
   }

   private int createFragShader(String var1) {
      this.fragShader = ARBShaderObjects.glCreateShaderObjectARB(35632);
      if (this.fragShader == 0) {
         return 0;
      } else {
         String var2 = "";

         String var3;
         try {
            for(BufferedReader var4 = new BufferedReader(new FileReader(var1)); (var3 = var4.readLine()) != null; var2 = var2 + var3 + "\n") {
            }
         } catch (Exception var5) {
            DebugLog.log("Fail reading fragment shading code");
            return 0;
         }

         ARBShaderObjects.glShaderSourceARB(this.fragShader, var2);
         ARBShaderObjects.glCompileShaderARB(this.fragShader);
         if (ARBShaderObjects.glGetObjectParameteriARB(this.fragShader, 35713) == 0) {
            System.err.println(this.fragShader);
            this.fragShader = 0;
         }

         return this.fragShader;
      }
   }

   public static void DrawTexture(Texture var0, int var1, int var2, int var3, int var4, float var5) {
      SpriteRenderer.instance.render(var0, var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, var5);
   }

   public static void DrawTexture(Texture var0, int var1, int var2, int var3, int var4, Color var5) {
      SpriteRenderer.instance.render(var0, var1, var2, var3, var4, var5.r, var5.g, var5.b, var5.a);
   }

   public void enter() {
      this.Elements.clear();
      this.targetAlpha = 1.0F;
      TextureID.UseFiltering = true;
      this.RestartDebounceClickTimer = 100;
      totalScale = (float)Core.getInstance().getOffscreenHeight(0) / 1080.0F;
      this.lastW = Core.getInstance().getOffscreenWidth(0);
      this.lastH = Core.getInstance().getOffscreenHeight(0);
      this.alpha = 1.0F;
      this.showLogo = false;
      IsoPlayer.DemoMode = true;
      boolean var1 = !SoundManager.instance.isRemastered();
      SoundManager.instance.DoMusic(var1 ? "OldMusic_theme2" : "NewMusic_MainTheme", true);
      int var2 = (int)((float)Core.getInstance().getOffscreenHeight(0) * 0.7F);
      boolean var3 = false;
      MainScreenState.ScreenElement var4 = new MainScreenState.ScreenElement(Texture.getSharedTexture("media/ui/PZ_Logo.png"), Core.getInstance().getOffscreenWidth(0) / 2 - (int)((float)Texture.getSharedTexture("media/ui/PZ_Logo.png").getWidth() * totalScale) / 2, var2 - (int)(350.0F * totalScale), 0.0F, 0.0F, 1);
      var4.targetAlpha = 1.0F;
      var4.alphaStep *= 0.9F;
      this.Logo = var4;
      this.Elements.add(var4);
      TextureID.UseFiltering = false;
      LuaEventManager.triggerEvent("OnMainMenuEnter");
      instance = this;
      float var5 = TextureID.totalMemUsed / 1024.0F;
      float var6 = var5 / 1024.0F;
   }

   public static MainScreenState getInstance() {
      return instance;
   }

   public boolean ShouldShowLogo() {
      return this.showLogo;
   }

   public void exit() {
      DebugLog.log("LOADED UP A TOTAL OF " + Texture.totalTextureID + " TEXTURES");
      IsoPlayer.DemoMode = false;
      float var1 = (float)Core.getInstance().getOptionMusicVolume() / 10.0F;
      long var2 = Calendar.getInstance().getTimeInMillis();

      while(true) {
         this.FadeAlpha = Math.min(1.0F, (float)(Calendar.getInstance().getTimeInMillis() - var2) / 250.0F);
         this.render();
         if (this.FadeAlpha >= 1.0F) {
            SoundManager.instance.stopMusic("");
            SoundManager.instance.setMusicVolume(var1);
            return;
         }

         try {
            Thread.sleep(33L);
         } catch (Exception var5) {
         }

         SoundManager.instance.setMusicVolume(var1 * (1.0F - this.FadeAlpha));
         SoundManager.instance.Update();
      }
   }

   public void render() {
      Core.getInstance();
      this.lightningTime += 1.0F * GameTime.instance.getMultipliedSecondsSinceLastUpdate();
      Core.getInstance().StartFrame();
      Core.getInstance().EndFrame();
      boolean var1 = UIManager.useUIFBO;
      UIManager.useUIFBO = false;
      Core.getInstance().StartFrameUI();
      IndieGL.glBlendFunc(770, 771);
      SpriteRenderer.instance.render((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F);
      IndieGL.glBlendFunc(770, 770);
      float var2 = SoundManager.instance.getMusicPosition();
      if (this.lightningTargetDelta == 0.0F && this.lightningDelta != 0.0F && this.lightningDelta < 0.6F && this.lightningCount == 0.0F) {
         this.lightningTargetDelta = 1.0F;
         this.lightningCount = 1.0F;
      }

      float var3 = "OldMusic_theme2".equals(SoundManager.instance.getCurrentMusicName()) ? 12000.0F : 29500.0F;
      float var4 = "OldMusic_theme2".equals(SoundManager.instance.getCurrentMusicName()) ? 45000.0F : 107000.0F;
      float var5 = 0.0F;
      if (var2 >= var3 && this.lastLightningTime < var3) {
      }

      if (var2 >= var3 + var5 && this.lastLightningTime < var3 + var5) {
         this.lightningTargetDelta = 1.0F;
      }

      if (var2 >= var4 && this.lastLightningTime < var4) {
      }

      if (var2 >= var4 + var5 && this.lastLightningTime < var4 + var5) {
         this.lightningTargetDelta = 1.0F;
      }

      if (this.lightningTargetDelta == 1.0F && this.lightningDelta == 1.0F && (this.lightningFullTimer > 1.0F && this.lightningCount == 0.0F || this.lightningFullTimer > 10.0F)) {
         this.lightningTargetDelta = 0.0F;
         this.lightningFullTimer = 0.0F;
      }

      if (this.lightningTargetDelta == 1.0F && this.lightningDelta == 1.0F) {
         this.lightningFullTimer += GameTime.getInstance().getMultiplier();
      }

      if (this.lightningDelta != this.lightningTargetDelta) {
         if (this.lightningDelta < this.lightningTargetDelta) {
            this.lightningDelta += 0.17F * GameTime.getInstance().getMultiplier();
            if (this.lightningDelta > this.lightningTargetDelta) {
               this.lightningDelta = this.lightningTargetDelta;
               if (this.lightningDelta == 1.0F) {
                  this.showLogo = true;
               }
            }
         }

         if (this.lightningDelta > this.lightningTargetDelta) {
            this.lightningDelta -= 0.025F * GameTime.getInstance().getMultiplier();
            if (this.lightningCount == 0.0F) {
               this.lightningDelta -= 0.1F;
            }

            if (this.lightningDelta < this.lightningTargetDelta) {
               this.lightningDelta = this.lightningTargetDelta;
               this.lightningCount = 0.0F;
            }
         }
      }

      this.lastLightningTime = var2;
      Texture var6 = Texture.getSharedTexture("media/ui/Title.png");
      Texture var7 = Texture.getSharedTexture("media/ui/Title2.png");
      Texture var8 = Texture.getSharedTexture("media/ui/Title3.png");
      Texture var9 = Texture.getSharedTexture("media/ui/Title4.png");
      if (Rand.Next(150) == 0) {
         this.lightOffCount = 10.0F;
      }

      Texture var10 = Texture.getSharedTexture("media/ui/Title_lightning.png");
      Texture var11 = Texture.getSharedTexture("media/ui/Title_lightning2.png");
      Texture var12 = Texture.getSharedTexture("media/ui/Title_lightning3.png");
      Texture var13 = Texture.getSharedTexture("media/ui/Title_lightning4.png");
      float var14 = (float)Core.getInstance().getScreenHeight() / 1080.0F;
      float var15 = (float)var6.getWidth() * var14;
      float var16 = (float)var7.getWidth() * var14;
      float var17 = (float)Core.getInstance().getScreenWidth() - (var15 + var16);
      if (var17 >= 0.0F) {
         var17 = 0.0F;
      }

      float var18 = 1.0F - this.lightningDelta * 0.6F;
      float var19 = 1024.0F * var14;
      float var20 = 56.0F * var14;
      DrawTexture(var6, (int)var17, 0, (int)var15, (int)var19, var18);
      DrawTexture(var7, (int)var17 + (int)var15, 0, (int)var15, (int)var19, var18);
      DrawTexture(var8, (int)var17, (int)var19, (int)var15, (int)var19, var18);
      DrawTexture(var9, (int)var17 + (int)var15, (int)var19, (int)var15, (int)var19, var18);
      IndieGL.glBlendFunc(770, 1);
      DrawTexture(var10, (int)var17, 0, (int)var15, (int)var19, this.lightningDelta);
      DrawTexture(var11, (int)var17 + (int)var15, 0, (int)var15, (int)var19, this.lightningDelta);
      DrawTexture(var12, (int)var17, (int)var19, (int)var15, (int)var19, this.lightningDelta);
      DrawTexture(var13, (int)var17 + (int)var15, (int)var19, (int)var15, (int)var19, this.lightningDelta);
      IndieGL.glBlendFunc(770, 771);
      UIManager.render();
      if (GameWindow.DrawReloadingLua) {
         int var21 = TextManager.instance.MeasureStringX(UIFont.Small, "Reloading Lua") + 32;
         int var22 = TextManager.instance.font.getLineHeight();
         int var23 = (int)Math.ceil((double)var22 * 1.5D);
         SpriteRenderer.instance.render((Texture)null, Core.getInstance().getScreenWidth() - var21 - 12, 12, var21, var23, 0.0F, 0.0F, 0.0F, 0.5F);
         TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() - var21 / 2 - 12), (double)(12 + (var23 - var22) / 2), "Reloading Lua", 1.0D, 1.0D, 1.0D, 1.0D);
      }

      if (this.FadeAlpha > 0.0F) {
         UIManager.DrawTexture(UIManager.getBlack(), 0.0D, 0.0D, (double)Core.getInstance().getScreenWidth(), (double)Core.getInstance().getScreenHeight(), (double)this.FadeAlpha);
      }

      Core.getInstance().EndFrameUI();
      UIManager.useUIFBO = var1;
   }

   public GameStateMachine.StateAction update() {
      if (this.connectToServerState != null) {
         GameStateMachine.StateAction var1 = this.connectToServerState.update();
         if (var1 == GameStateMachine.StateAction.Continue) {
            this.connectToServerState.exit();
            this.connectToServerState = null;
            return GameStateMachine.StateAction.Remain;
         }
      }

      LuaEventManager.triggerEvent("OnFETick", 0);
      if (ingameState != null) {
      }

      if (this.RestartDebounceClickTimer > 0) {
         --this.RestartDebounceClickTimer;
      }

      for(int var3 = 0; var3 < this.Elements.size(); ++var3) {
         MainScreenState.ScreenElement var2 = (MainScreenState.ScreenElement)this.Elements.get(var3);
         var2.update();
      }

      this.lastW = Core.getInstance().getOffscreenWidth(0);
      this.lastH = Core.getInstance().getOffscreenHeight(0);
      return GameStateMachine.StateAction.Remain;
   }

   public void setConnectToServerState(ConnectToServerState var1) {
      this.connectToServerState = var1;
   }

   public GameState redirectState() {
      return null;
   }

   public static ByteBuffer[] loadIcons() {
      BufferedImage var0 = null;
      ByteBuffer[] var1 = null;
      String var2 = System.getProperty("os.name").toUpperCase();
      if (var2.contains("WIN")) {
         try {
            var1 = new ByteBuffer[2];
            var0 = ImageIO.read(new File("media" + File.separator + "ui" + File.separator + "zomboidIcon16.png"));
            var1[0] = loadInstance(var0, 16);
            var0 = ImageIO.read(new File("media" + File.separator + "ui" + File.separator + "zomboidIcon32.png"));
            var1[1] = loadInstance(var0, 32);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      } else if (var2.contains("MAC")) {
         try {
            var1 = new ByteBuffer[1];
            var0 = ImageIO.read(new File("media" + File.separator + "ui" + File.separator + "zomboidIcon128.png"));
            var1[0] = loadInstance(var0, 128);
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      } else {
         try {
            var1 = new ByteBuffer[1];
            var0 = ImageIO.read(new File("media" + File.separator + "ui" + File.separator + "zomboidIcon32.png"));
            var1[0] = loadInstance(var0, 32);
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      }

      return var1;
   }

   private static ByteBuffer loadInstance(BufferedImage var0, int var1) {
      BufferedImage var2 = new BufferedImage(var1, var1, 3);
      Graphics2D var3 = var2.createGraphics();
      double var4 = getIconRatio(var0, var2);
      double var6 = (double)var0.getWidth() * var4;
      double var8 = (double)var0.getHeight() * var4;
      var3.drawImage(var0, (int)(((double)var2.getWidth() - var6) / 2.0D), (int)(((double)var2.getHeight() - var8) / 2.0D), (int)var6, (int)var8, (ImageObserver)null);
      var3.dispose();
      return convertToByteBuffer(var2);
   }

   private static double getIconRatio(BufferedImage var0, BufferedImage var1) {
      double var2 = 1.0D;
      if (var0.getWidth() > var1.getWidth()) {
         var2 = (double)var1.getWidth() / (double)var0.getWidth();
      } else {
         var2 = (double)(var1.getWidth() / var0.getWidth());
      }

      double var4;
      if (var0.getHeight() > var1.getHeight()) {
         var4 = (double)var1.getHeight() / (double)var0.getHeight();
         if (var4 < var2) {
            var2 = var4;
         }
      } else {
         var4 = (double)(var1.getHeight() / var0.getHeight());
         if (var4 < var2) {
            var2 = var4;
         }
      }

      return var2;
   }

   public static ByteBuffer convertToByteBuffer(BufferedImage var0) {
      byte[] var1 = new byte[var0.getWidth() * var0.getHeight() * 4];
      int var2 = 0;

      for(int var3 = 0; var3 < var0.getHeight(); ++var3) {
         for(int var4 = 0; var4 < var0.getWidth(); ++var4) {
            int var5 = var0.getRGB(var4, var3);
            var1[var2 + 0] = (byte)(var5 << 8 >> 24);
            var1[var2 + 1] = (byte)(var5 << 16 >> 24);
            var1[var2 + 2] = (byte)(var5 << 24 >> 24);
            var1[var2 + 3] = (byte)(var5 >> 24);
            var2 += 4;
         }
      }

      return ByteBuffer.wrap(var1);
   }

   public static class ScreenElement {
      public float alpha = 0.0F;
      public float alphaStep = 0.2F;
      public boolean jumpBack = true;
      public float sx = 0.0F;
      public float sy = 0.0F;
      public float targetAlpha = 0.0F;
      public Texture tex;
      public int TicksTillTargetAlpha = 0;
      public float x = 0.0F;
      public int xCount = 1;
      public float xVel = 0.0F;
      public float xVelO = 0.0F;
      public float y = 0.0F;
      public float yVel = 0.0F;
      public float yVelO = 0.0F;

      public ScreenElement(Texture var1, int var2, int var3, float var4, float var5, int var6) {
         this.x = this.sx = (float)var2;
         this.y = this.sy = (float)var3 - (float)var1.getHeight() * MainScreenState.totalScale;
         this.xVel = var4;
         this.yVel = var5;
         this.tex = var1;
         this.xCount = var6;
      }

      public void render() {
         int var1 = (int)this.x;
         int var2 = (int)this.y;

         for(int var3 = 0; var3 < this.xCount; ++var3) {
            MainScreenState.DrawTexture(this.tex, var1, var2, (int)((float)this.tex.getWidth() * MainScreenState.totalScale), (int)((float)this.tex.getHeight() * MainScreenState.totalScale), this.alpha);
            var1 = (int)((float)var1 + (float)this.tex.getWidth() * MainScreenState.totalScale);
         }

         TextManager.instance.DrawStringRight((double)(Core.getInstance().getOffscreenWidth(0) - 5), (double)(Core.getInstance().getOffscreenHeight(0) - 15), "Version: " + MainScreenState.Version, 1.0D, 1.0D, 1.0D, 1.0D);
      }

      public void setY(float var1) {
         this.y = this.sy = var1 - (float)this.tex.getHeight() * MainScreenState.totalScale;
      }

      public void update() {
         this.x += this.xVel * MainScreenState.totalScale;
         this.y += this.yVel * MainScreenState.totalScale;
         --this.TicksTillTargetAlpha;
         if (this.TicksTillTargetAlpha <= 0) {
            this.targetAlpha = 1.0F;
         }

         if (this.jumpBack && this.sx - this.x > (float)this.tex.getWidth() * MainScreenState.totalScale) {
            this.x += (float)this.tex.getWidth() * MainScreenState.totalScale;
         }

         if (this.alpha < this.targetAlpha) {
            this.alpha += this.alphaStep;
            if (this.alpha > this.targetAlpha) {
               this.alpha = this.targetAlpha;
            }
         } else if (this.alpha > this.targetAlpha) {
            this.alpha -= this.alphaStep;
            if (this.alpha < this.targetAlpha) {
               this.alpha = this.targetAlpha;
            }
         }

      }
   }

   public class Credit {
      public int disappearDelay = 200;
      public Texture name;
      public float nameAlpha = 0.0F;
      public float nameAppearDelay = 40.0F;
      public float nameTargetAlpha = 0.0F;
      public Texture title;
      public float titleAlpha = 0.0F;
      public float titleTargetAlpha = 1.0F;

      public Credit(Texture var2, Texture var3) {
         this.title = var2;
         this.name = var3;
      }
   }
}
