package zombie.iso.weather;

import org.lwjgl.opengl.ARBShaderObjects;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.opengl.RenderSettings;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.TextureDraw;

public class WeatherShader extends Shader {
   public int timeOfDay = 0;
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
   private int timerVal;
   private boolean bAlt = false;
   private static float[][] floatArrs = new float[5][];

   public WeatherShader(String var1) {
      super(var1);
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
         if (Math.abs(var5) > 0.8F && var3 != null && var3.Traits.NightVision.isSet() && !var3.isWearingNightVisionGoggles()) {
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
         var1.col0 = var6;
         var1.col1 = var7;
         var1.col2 = Core.getInstance().getOffscreenTrueWidth();
         var1.col3 = Core.getInstance().getOffscreenTrueHeight();
         var1.bSingleCol = Core.getInstance().getZoom(var2) > 2.0F || (double)Core.getInstance().getZoom(var2) < 2.0D && Core.getInstance().getZoom(var2) >= 1.75F;
      }
   }

   public void startRenderThread(TextureDraw var1) {
      float var2 = var1.f1;
      boolean var3 = var1.flipped;
      int var4 = var1.col0;
      int var5 = var1.col1;
      int var6 = var1.col2;
      int var7 = var1.col3;
      float var8 = var1.bSingleCol ? 1.0F : 0.0F;
      ARBShaderObjects.glUniform1fARB(this.width, (float)var4);
      ARBShaderObjects.glUniform1fARB(this.height, (float)var5);
      ARBShaderObjects.glUniform3fARB(this.Light, var1.vars[0], var1.vars[1], var1.vars[2]);
      ARBShaderObjects.glUniform1fARB(this.LightIntensity, var1.vars[3]);
      ARBShaderObjects.glUniform1fARB(this.NightValue, var2);
      ARBShaderObjects.glUniform1fARB(this.DesaturationVal, var1.vars[4]);
      ARBShaderObjects.glUniform1fARB(this.NightVisionGoggles, var1.vars[5]);
      ARBShaderObjects.glUniform1fARB(this.Exterior, var3 ? 1.0F : 0.0F);
      ARBShaderObjects.glUniform1fARB(this.timer, (float)(this.timerVal / 2));
      if (PerformanceSettings.getLockFPS() >= 60) {
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
      ARBShaderObjects.glUniform2fARB(this.TextureSize, (float)var6, (float)var7);
      ARBShaderObjects.glUniform1fARB(this.Zoom, var8);
   }

   public void onCompileSuccess(ShaderProgram var1) {
      int var2 = this.getID();
      this.timeOfDay = ARBShaderObjects.glGetUniformLocationARB(var2, "TimeOfDay");
      this.bloom = ARBShaderObjects.glGetUniformLocationARB(var2, "BloomVal");
      this.PixelOffset = ARBShaderObjects.glGetUniformLocationARB(var2, "PixelOffset");
      this.PixelSize = ARBShaderObjects.glGetUniformLocationARB(var2, "PixelSize");
      this.BlurStrength = ARBShaderObjects.glGetUniformLocationARB(var2, "BlurStrength");
      this.width = ARBShaderObjects.glGetUniformLocationARB(var2, "bgl_RenderedTextureWidth");
      this.height = ARBShaderObjects.glGetUniformLocationARB(var2, "bgl_RenderedTextureHeight");
      this.timer = ARBShaderObjects.glGetUniformLocationARB(var2, "timer");
      this.TextureSize = ARBShaderObjects.glGetUniformLocationARB(var2, "TextureSize");
      this.Zoom = ARBShaderObjects.glGetUniformLocationARB(var2, "Zoom");
      this.Light = ARBShaderObjects.glGetUniformLocationARB(var2, "Light");
      this.LightIntensity = ARBShaderObjects.glGetUniformLocationARB(var2, "LightIntensity");
      this.NightValue = ARBShaderObjects.glGetUniformLocationARB(var2, "NightValue");
      this.Exterior = ARBShaderObjects.glGetUniformLocationARB(var2, "Exterior");
      this.NightVisionGoggles = ARBShaderObjects.glGetUniformLocationARB(var2, "NightVisionGoggles");
      this.DesaturationVal = ARBShaderObjects.glGetUniformLocationARB(var2, "DesaturationVal");
      this.FogMod = ARBShaderObjects.glGetUniformLocationARB(var2, "FogMod");
   }

   public void postRender(TextureDraw var1) {
      if (var1.vars != null) {
         returnFloatArray(var1.vars);
         var1.vars = null;
      }

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
}
