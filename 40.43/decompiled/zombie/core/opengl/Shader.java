package zombie.core.opengl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.Util;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.IndieFileLoader;
import zombie.core.PerformanceSettings;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugLog;
import zombie.iso.sprite.SkyBox;

public class Shader {
   public static HashMap ShaderMap = new HashMap();
   public int ShaderID = 0;
   public int FragID = 0;
   public int VertID = 0;
   public Texture tex;
   public int timeOfDay = 0;
   public int width;
   public int height;
   private int PixelOffset;
   private int PixelSize;
   private int bloom;
   private int timer;
   private int BlurStrength;
   private int TextureSize;
   private int Zoom;
   private int Light;
   private int LightIntensity;
   private int NightValue;
   private int Exterior;
   private int NightVisionGoggles;
   private int DesaturationVal;
   private int FogMod;
   private int SkyBoxTime;
   private int SkyBoxParamCloudCount;
   private int SkyBoxParamCloudSize;
   private int SkyBoxParamSunLight;
   private int SkyBoxParamSunColor;
   private int SkyBoxParamSkyHColour;
   private int SkyBoxParamSkyLColour;
   private int SkyBoxParamCloudLight;
   private int SkyBoxParamStars;
   private int SkyBoxParamFog;
   private int SkyBoxParamWind;
   public int timerVal;
   boolean bAlt = false;
   private static float[][] floatArrs = new float[5][];

   public Shader(String var1) {
      this.ShaderID = ARBShaderObjects.glCreateProgramObjectARB();
      boolean var2;
      if (this.ShaderID != 0) {
         ShaderMap.put(this.ShaderID, this);
         this.FragID = this.createFragShader("media/shaders/" + var1 + ".frag");
         this.VertID = this.createVertShader("media/shaders/" + var1 + ".vert");
         if (this.VertID != 0 && this.FragID != 0) {
            ARBShaderObjects.glAttachObjectARB(this.ShaderID, this.VertID);
            ARBShaderObjects.glAttachObjectARB(this.ShaderID, this.FragID);
            ARBShaderObjects.glLinkProgramARB(this.ShaderID);
            ARBShaderObjects.glValidateProgramARB(this.ShaderID);
            if (ARBShaderObjects.glGetObjectParameteriARB(this.ShaderID, 35715) == 0) {
               System.err.println(getLogInfo(this.ShaderID));
               this.VertID = 0;
               this.ShaderID = 0;
               this.FragID = 0;
               return;
            }

            this.timeOfDay = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TimeOfDay");
            this.bloom = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "BloomVal");
            this.PixelOffset = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "PixelOffset");
            this.PixelSize = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "PixelSize");
            this.BlurStrength = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "BlurStrength");
            this.width = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "bgl_RenderedTextureWidth");
            this.height = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "bgl_RenderedTextureHeight");
            this.timer = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "timer");
            this.TextureSize = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureSize");
            this.Zoom = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Zoom");
            this.Light = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light");
            this.LightIntensity = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "LightIntensity");
            this.NightValue = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "NightValue");
            this.Exterior = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Exterior");
            this.NightVisionGoggles = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "NightVisionGoggles");
            this.DesaturationVal = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "DesaturationVal");
            this.FogMod = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "FogMod");
            this.SkyBoxTime = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "SBTime");
            this.SkyBoxParamCloudCount = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "SBParamCloudCount");
            this.SkyBoxParamCloudSize = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "SBParamCloudSize");
            this.SkyBoxParamSunLight = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "SBParamSunLight");
            this.SkyBoxParamSunColor = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "SBParamSunColour");
            this.SkyBoxParamSkyHColour = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "SBParamSkyHColour");
            this.SkyBoxParamSkyLColour = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "SBParamSkyLColour");
            this.SkyBoxParamCloudLight = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "SBParamCloudLight");
            this.SkyBoxParamStars = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "SBParamStars");
            this.SkyBoxParamFog = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "SBParamFog");
            this.SkyBoxParamWind = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "SBParamWind");
         } else {
            var2 = false;
         }
      } else {
         var2 = false;
      }

   }

   private static String getLogInfo(int var0) {
      return ARBShaderObjects.glGetInfoLogARB(var0, ARBShaderObjects.glGetObjectParameteriARB(var0, 35716));
   }

   public void setTexture(Texture var1) {
      this.tex = var1;
   }

   public int getID() {
      return this.ShaderID;
   }

   public void Start() {
      ARBShaderObjects.glUseProgramObjectARB(this.ShaderID);
   }

   public void End() {
      ARBShaderObjects.glUseProgramObjectARB(0);
   }

   private int createVertShader(String var1) {
      int var2 = ARBShaderObjects.glCreateShaderObjectARB(35633);
      if (var2 == 0) {
         return 0;
      } else {
         String var3 = null;

         try {
            InputStreamReader var5 = IndieFileLoader.getStreamReader(var1, false);
            BufferedReader var6 = new BufferedReader(var5);

            while(true) {
               String var4;
               if ((var4 = var6.readLine()) == null) {
                  var5.close();
                  break;
               }

               if (var3 == null) {
                  var3 = var4.trim() + System.getProperty("line.separator");
               } else {
                  var3 = var3 + var4.trim() + System.getProperty("line.separator");
               }
            }
         } catch (Exception var7) {
            DebugLog.log("Fail reading vertex shading code");
            return 0;
         }

         while(var3.indexOf("#") != 0) {
            var3 = var3.substring(1);
         }

         ARBShaderObjects.glShaderSourceARB(var2, var3);
         ARBShaderObjects.glCompileShaderARB(var2);
         if (!printLogInfo("vertex shader", var2)) {
            var2 = 0;
         }

         return var2;
      }
   }

   private int createFragShader(String var1) {
      int var2 = ARBShaderObjects.glCreateShaderObjectARB(35632);
      if (var2 == 0) {
         return 0;
      } else {
         String var3 = null;

         try {
            InputStreamReader var5 = IndieFileLoader.getStreamReader(var1, false);
            BufferedReader var6 = new BufferedReader(var5);

            while(true) {
               String var4;
               if ((var4 = var6.readLine()) == null) {
                  var5.close();
                  break;
               }

               if (var3 == null) {
                  var3 = var4.trim() + System.getProperty("line.separator");
               } else {
                  var3 = var3 + var4.trim() + System.getProperty("line.separator");
               }
            }
         } catch (Exception var7) {
            DebugLog.log("Fail reading fragment shading code");
            return 0;
         }

         while(var3.indexOf("#") != 0) {
            var3 = var3.substring(1);
         }

         ARBShaderObjects.glShaderSourceARB(var2, var3);
         ARBShaderObjects.glCompileShaderARB(var2);
         if (!printLogInfo("fragment shader", var2)) {
            var2 = 0;
         }

         return var2;
      }
   }

   private static boolean printLogInfo(String var0, int var1) {
      IntBuffer var2 = BufferUtils.createIntBuffer(1);
      ARBShaderObjects.glGetObjectParameterARB(var1, 35716, var2);
      int var3 = var2.get();
      if (var3 > 1) {
         ByteBuffer var4 = BufferUtils.createByteBuffer(var3);
         var2.flip();
         ARBShaderObjects.glGetInfoLogARB(var1, var2, var4);
         byte[] var5 = new byte[var3];
         var4.get(var5);
         String var6 = new String(var5);
         DebugLog.log("Info log (" + var0 + "):\n" + var6 + "-----");
         return true;
      } else {
         return true;
      }
   }

   public void updateParams(TextureDraw var1) {
      float var2 = var1.f1;
      boolean var3 = var1.flipped;
      int var4 = var1.col[0];
      int var5 = var1.col[1];
      int var6 = var1.col[2];
      int var7 = var1.col[3];
      float var8 = var1.bSingleCol ? 1.0F : 0.0F;
      ARBShaderObjects.glUniform1fARB(this.timeOfDay, var2);
      ARBShaderObjects.glUniform1fARB(this.bloom, var3 ? 1.0F : 0.0F);
      ARBShaderObjects.glUniform1fARB(this.BlurStrength, 5.0F);
      ARBShaderObjects.glUniform1fARB(this.width, (float)var4);
      ARBShaderObjects.glUniform1fARB(this.height, (float)var5);
      ARBShaderObjects.glUniform3fARB(this.Light, var1.vars[0], var1.vars[1], var1.vars[2]);
      ARBShaderObjects.glUniform1fARB(this.LightIntensity, var1.vars[3]);
      ARBShaderObjects.glUniform1fARB(this.NightValue, var2);
      ARBShaderObjects.glUniform1fARB(this.DesaturationVal, var1.vars[4]);
      ARBShaderObjects.glUniform1fARB(this.NightVisionGoggles, var1.vars[5]);
      ARBShaderObjects.glUniform1fARB(this.FogMod, var1.vars[6]);
      ARBShaderObjects.glUniform1fARB(this.Exterior, var3 ? 1.0F : 0.0F);
      ARBShaderObjects.glUniform1fARB(this.timer, (float)(this.timerVal / 2));
      if (PerformanceSettings.LockFPS >= 60) {
         if (this.bAlt) {
            ++this.timerVal;
         }

         this.bAlt = !this.bAlt;
      } else {
         this.timerVal += 2;
      }

      float var9 = 0.0F;
      float var10 = 0.0F;
      float var11 = 1.0F / (float)var4;
      float var12 = 1.0F / (float)var5;
      ARBShaderObjects.glUniform2fARB(this.PixelOffset, var9, var10);
      ARBShaderObjects.glUniform2fARB(this.PixelSize, var11, var12);
      ARBShaderObjects.glUniform2fARB(this.TextureSize, (float)var6, (float)var7);
      ARBShaderObjects.glUniform1fARB(this.Zoom, var8);
      float[] var13 = var1.vars;
      var1.vars = null;
      returnFloatArray(var13);
   }

   public void updateSkyBoxParams(TextureDraw var1) {
      SkyBox var2 = SkyBox.getInstance();
      ARBShaderObjects.glUniform1fARB(this.SkyBoxTime, (float)var2.getShaderTime());
      ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudCount, var2.getShaderCloudCount());
      ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudSize, var2.getShaderCloudSize());
      ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSunLight, var2.getShaderSunLight().x, var2.getShaderSunLight().y, var2.getShaderSunLight().z);
      ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSunColor, var2.getShaderSunColor().r, var2.getShaderSunColor().g, var2.getShaderSunColor().b);
      ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSkyHColour, var2.getShaderSkyHColour().r, var2.getShaderSkyHColour().g, var2.getShaderSkyHColour().b);
      ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSkyLColour, var2.getShaderSkyLColour().r, var2.getShaderSkyLColour().g, var2.getShaderSkyLColour().b);
      ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudLight, var2.getShaderCloudLight());
      ARBShaderObjects.glUniform1fARB(this.SkyBoxParamStars, var2.getShaderStars());
      ARBShaderObjects.glUniform1fARB(this.SkyBoxParamFog, var2.getShaderFog());
      ARBShaderObjects.glUniform3fARB(this.SkyBoxParamWind, var2.getShaderWind().x, var2.getShaderWind().y, var2.getShaderWind().z);
   }

   public void destroy() {
      if (this.ShaderID != 0) {
         if (this.FragID != 0) {
            ARBShaderObjects.glDetachObjectARB(this.ShaderID, this.FragID);
            Util.checkGLError();
            ARBShaderObjects.glDeleteObjectARB(this.FragID);
            Util.checkGLError();
            this.FragID = 0;
         }

         if (this.VertID != 0) {
            ARBShaderObjects.glDetachObjectARB(this.ShaderID, this.VertID);
            Util.checkGLError();
            ARBShaderObjects.glDeleteObjectARB(this.VertID);
            Util.checkGLError();
            this.VertID = 0;
         }

         ShaderMap.remove(this.ShaderID);
         ARBShaderObjects.glDeleteObjectARB(this.ShaderID);
         Util.checkGLError();
         this.ShaderID = 0;
      }

   }

   public void updateParamsSkin() {
   }

   private static float[] getFreeFloatArray() {
      for(int var0 = 0; var0 < floatArrs.length; ++var0) {
         if (floatArrs[var0] != null) {
            float[] var1 = floatArrs[var0];
            floatArrs[var0] = null;
            return var1;
         }
      }

      return new float[7];
   }

   private static void returnFloatArray(float[] var0) {
      for(int var1 = 0; var1 < floatArrs.length; ++var1) {
         if (floatArrs[var1] == null) {
            floatArrs[var1] = var0;
            break;
         }
      }

   }

   public void startMainThread(TextureDraw var1, int var2) {
      if (var2 >= 0 && var2 < 4) {
         IsoPlayer var3 = IsoPlayer.players[var2];
         boolean var10000;
         if (var3 != null && var3.getCurrentSquare() != null && var3.getCurrentSquare().isOutside()) {
            var10000 = true;
         } else {
            var10000 = false;
         }

         float var5 = GameTime.instance.TimeOfDay / 12.0F - 1.0F;
         if (Math.abs(var5) > 0.8F && var3 != null && var3.HasTrait("NightVision") && !var3.isWearingNightVisionGoggles()) {
            var5 *= 0.8F;
         }

         int var6 = Core.getInstance().getOffscreenWidth(var2);
         int var7 = Core.getInstance().getOffscreenHeight(var2);
         if (var1.vars == null) {
            var1.vars = getFreeFloatArray();
            if (var1.vars == null) {
               var1.vars = new float[7];
            }
         }

         RenderSettings.PlayerRenderSettings var8 = RenderSettings.getInstance().getPlayerSettings(var2);
         var1.vars[0] = var8.getBlendColor().r;
         var1.vars[1] = var8.getBlendColor().g;
         var1.vars[2] = var8.getBlendColor().b;
         var1.vars[3] = var8.getBlendIntensity();
         var1.vars[4] = var8.getDesaturation();
         var1.vars[5] = var8.isApplyNightVisionGoggles() ? 1.0F : 0.0F;
         var1.vars[6] = var8.getFogMod();
         var1.flipped = var8.isExterior();
         var1.f1 = var8.getDarkness();
         var1.col[0] = var6;
         var1.col[1] = var7;
         var1.col[2] = Core.getInstance().getOffscreenTrueWidth();
         var1.col[3] = Core.getInstance().getOffscreenTrueHeight();
         var1.bSingleCol = Core.getInstance().getZoom(var2) > 2.0F || (double)Core.getInstance().getZoom(var2) < 2.0D && Core.getInstance().getZoom(var2) >= 1.75F;
      }
   }
}
