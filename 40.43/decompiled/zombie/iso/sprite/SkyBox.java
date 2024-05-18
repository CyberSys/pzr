package zombie.iso.sprite;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GLContext;
import zombie.GameTime;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.core.textures.TextureID;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugOptions;
import zombie.interfaces.ITexture;
import zombie.iso.IsoCamera;
import zombie.iso.IsoObject;
import zombie.iso.weather.ClimateManager;

public class SkyBox extends IsoObject {
   private static SkyBox instance;
   public IsoSpriteInstance def = null;
   private TextureFBO textureFBOA;
   private TextureFBO textureFBOB;
   private boolean isCurrentA;
   public Shader Effect;
   private UpdateLimit renderLimit = new UpdateLimit(1000L);
   private boolean isUpdated = false;
   private int SkyBoxTime;
   private float SkyBoxParamCloudCount;
   private float SkyBoxParamCloudSize;
   private Vector3f SkyBoxParamSunLight;
   private Color SkyBoxParamSunColor;
   private Color SkyBoxParamSkyHColour;
   private Color SkyBoxParamSkyLColour;
   private float SkyBoxParamCloudLight;
   private float SkyBoxParamStars;
   private float SkyBoxParamFog;
   private Vector3f SkyBoxParamWind;
   private boolean isSetAVG = false;
   private float SkyBoxParamCloudCountAVG;
   private float SkyBoxParamCloudSizeAVG;
   private Vector3f SkyBoxParamSunLightAVG;
   private Color SkyBoxParamSunColorAVG;
   private Color SkyBoxParamSkyHColourAVG;
   private Color SkyBoxParamSkyLColourAVG;
   private float SkyBoxParamCloudLightAVG;
   private float SkyBoxParamStarsAVG;
   private float SkyBoxParamFogAVG;
   private Vector3f SkyBoxParamWindINT;
   private Texture texAM = Texture.getSharedTexture("media/textures/CMVehicleReflection/ref_am.png");
   private Texture texPM = Texture.getSharedTexture("media/textures/CMVehicleReflection/ref_am.png");
   private Color SkyHColourDay = new Color(0.1F, 0.1F, 0.4F);
   private Color SkyHColourDawn = new Color(0.2F, 0.2F, 0.3F);
   private Color SkyHColourDusk = new Color(0.2F, 0.2F, 0.3F);
   private Color SkyHColourNight = new Color(0.01F, 0.01F, 0.04F);
   private Color SkyLColourDay = new Color(0.1F, 0.45F, 0.7F);
   private Color SkyLColourDawn = new Color(0.1F, 0.4F, 0.6F);
   private Color SkyLColourDusk = new Color(0.1F, 0.4F, 0.6F);
   private Color SkyLColourNight = new Color(0.01F, 0.045F, 0.07F);
   private int apiId;

   public static synchronized SkyBox getInstance() {
      if (instance == null) {
         instance = new SkyBox();
      }

      return instance;
   }

   public void update(ClimateManager var1) {
      if (!this.isUpdated) {
         this.isUpdated = true;
         GameTime var2 = GameTime.getInstance();
         ClimateManager.DayInfo var3 = var1.getCurrentDay();
         float var4 = var3.season.getDawn();
         float var5 = var3.season.getDusk();
         float var6 = var3.season.getDayHighNoon();
         float var7 = var2.getTimeOfDay();
         float var8;
         if (!(var7 < var4) && !(var7 > var5)) {
            if (var7 < var6) {
               var8 = (var7 - var4) / (var6 - var4);
               this.SkyHColourDawn.interp(this.SkyHColourDay, var8, this.SkyBoxParamSkyHColour);
               this.SkyLColourDawn.interp(this.SkyLColourDay, var8, this.SkyBoxParamSkyLColour);
               this.SkyBoxParamSunLight = new Vector3f(4.0F * var8 - 4.0F, 0.22F, 0.3F);
               this.SkyBoxParamSunLight.normalize();
               this.SkyBoxParamSunLight.mul(Math.min(1.0F, var8 * 10.0F));
               this.SkyBoxParamSunColor = var1.getGlobalLight().getExterior();
            } else {
               var8 = (var7 - var6) / (var5 - var6);
               this.SkyHColourDay.interp(this.SkyHColourDusk, var8, this.SkyBoxParamSkyHColour);
               this.SkyLColourDay.interp(this.SkyLColourDusk, var8, this.SkyBoxParamSkyLColour);
               this.SkyBoxParamSunLight = new Vector3f(4.0F * var8, 0.22F, 0.3F);
               this.SkyBoxParamSunLight.normalize();
               this.SkyBoxParamSunLight.mul(Math.min(1.0F, (1.0F - var8) * 10.0F));
               this.SkyBoxParamSunColor = var1.getGlobalLight().getExterior();
            }
         } else {
            var8 = 24.0F - var5 + var4;
            float var9;
            if (var7 > var5) {
               var9 = (var7 - var5) / var8;
               this.SkyHColourDusk.interp(this.SkyHColourDawn, var9, this.SkyBoxParamSkyHColour);
               this.SkyLColourDusk.interp(this.SkyLColourDawn, var9, this.SkyBoxParamSkyLColour);
               this.SkyBoxParamSunLight = new Vector3f(0.35F, 0.22F, 0.3F);
               this.SkyBoxParamSunLight.normalize();
               this.SkyBoxParamSunLight.mul(Math.min(1.0F, var9 * 5.0F));
            } else {
               var9 = (24.0F - var5 + var7) / var8;
               this.SkyHColourDusk.interp(this.SkyHColourDawn, var9, this.SkyBoxParamSkyHColour);
               this.SkyLColourDusk.interp(this.SkyLColourDawn, var9, this.SkyBoxParamSkyLColour);
               this.SkyBoxParamSunLight = new Vector3f(0.35F, 0.22F, 0.3F);
               this.SkyBoxParamSunLight.normalize();
               this.SkyBoxParamSunLight.mul(Math.min(1.0F, (1.0F - var9) * 5.0F));
            }

            this.SkyBoxParamSunColor = new Color(var1.getGlobalLight().getExterior());
            this.SkyBoxParamSunColor.scale(var1.getNightStrength());
         }

         this.SkyBoxParamSkyHColour.interp(this.SkyHColourNight, var1.getNightStrength(), this.SkyBoxParamSkyHColour);
         this.SkyBoxParamSkyLColour.interp(this.SkyLColourNight, var1.getNightStrength(), this.SkyBoxParamSkyLColour);
         this.SkyBoxParamCloudCount = Math.min(Math.max(var1.getCloudIntensity(), var1.getPrecipitationIntensity() * 2.0F), 0.999F);
         this.SkyBoxParamCloudSize = 0.02F + var1.getTemperature() / 70.0F;
         this.SkyBoxParamFog = var1.getFogIntensity();
         this.SkyBoxParamStars = var1.getNightStrength();
         this.SkyBoxParamCloudLight = (float)(1.0D - (1.0D - 1.0D * Math.pow(1000.0D, (double)(-var1.getPrecipitationIntensity() - var1.getNightStrength()))));
         var8 = (1.0F - (var1.getWindAngleIntensity() + 1.0F) * 0.5F + 0.25F) % 1.0F;
         var8 *= 360.0F;
         this.SkyBoxParamWind.set((float)Math.cos(Math.toRadians((double)var8)), 0.0F, (float)Math.sin(Math.toRadians((double)var8)));
         this.SkyBoxParamWind.mul(var1.getWindIntensity());
         if (!this.isSetAVG) {
            this.isSetAVG = true;
            this.SkyBoxParamCloudCountAVG = this.SkyBoxParamCloudCount;
            this.SkyBoxParamCloudSizeAVG = this.SkyBoxParamCloudSize;
            this.SkyBoxParamSunLightAVG = new Vector3f(this.SkyBoxParamSunLight);
            this.SkyBoxParamSunColorAVG = new Color(this.SkyBoxParamSunColor);
            this.SkyBoxParamSkyHColourAVG = new Color(this.SkyBoxParamSkyHColour);
            this.SkyBoxParamSkyLColourAVG = new Color(this.SkyBoxParamSkyLColour);
            this.SkyBoxParamCloudLightAVG = this.SkyBoxParamCloudLight;
            this.SkyBoxParamStarsAVG = this.SkyBoxParamStars;
            this.SkyBoxParamFogAVG = this.SkyBoxParamFog;
            this.SkyBoxParamWindINT.set((Vector3fc)this.SkyBoxParamWind);
         } else {
            this.SkyBoxParamCloudCountAVG += (this.SkyBoxParamCloudCount - this.SkyBoxParamCloudCountAVG) * 0.1F;
            this.SkyBoxParamCloudSizeAVG += (this.SkyBoxParamCloudSizeAVG + this.SkyBoxParamCloudSize) * 0.1F;
            this.SkyBoxParamSunLightAVG.lerp(this.SkyBoxParamSunLight, 0.1F);
            this.SkyBoxParamSunColorAVG.interp(this.SkyBoxParamSunColor, 0.1F, this.SkyBoxParamSunColorAVG);
            this.SkyBoxParamSkyHColourAVG.interp(this.SkyBoxParamSkyHColour, 0.1F, this.SkyBoxParamSkyHColourAVG);
            this.SkyBoxParamSkyLColourAVG.interp(this.SkyBoxParamSkyLColour, 0.1F, this.SkyBoxParamSkyLColourAVG);
            this.SkyBoxParamCloudLightAVG += (this.SkyBoxParamCloudLight - this.SkyBoxParamCloudLightAVG) * 0.1F;
            this.SkyBoxParamStarsAVG += (this.SkyBoxParamStars - this.SkyBoxParamStarsAVG) * 0.1F;
            this.SkyBoxParamFogAVG += (this.SkyBoxParamFog - this.SkyBoxParamFogAVG) * 0.1F;
            this.SkyBoxParamWindINT.add(this.SkyBoxParamWind);
         }

      }
   }

   public int getShaderTime() {
      return this.SkyBoxTime;
   }

   public float getShaderCloudCount() {
      return this.SkyBoxParamCloudCount;
   }

   public float getShaderCloudSize() {
      return this.SkyBoxParamCloudSize;
   }

   public Vector3f getShaderSunLight() {
      return this.SkyBoxParamSunLight;
   }

   public Color getShaderSunColor() {
      return this.SkyBoxParamSunColor;
   }

   public Color getShaderSkyHColour() {
      return this.SkyBoxParamSkyHColour;
   }

   public Color getShaderSkyLColour() {
      return this.SkyBoxParamSkyLColour;
   }

   public float getShaderCloudLight() {
      return this.SkyBoxParamCloudLight;
   }

   public float getShaderStars() {
      return this.SkyBoxParamStars;
   }

   public float getShaderFog() {
      return this.SkyBoxParamFog;
   }

   public Vector3f getShaderWind() {
      return this.SkyBoxParamWindINT;
   }

   public SkyBox() {
      try {
         TextureID.bUseCompression = false;
         Texture var1 = new Texture(512, 512);
         Texture var2 = new Texture(512, 512);
         this.textureFBOA = new TextureFBO(var1);
         this.textureFBOB = new TextureFBO(var2);
      } catch (Exception var6) {
         var6.printStackTrace();
      } finally {
         TextureID.bUseCompression = TextureID.bUseCompressionOption;
      }

      this.Effect = new Shader("skybox");
      this.def = IsoSpriteInstance.get(this.sprite);
      this.SkyBoxTime = 0;
      this.SkyBoxParamSunLight = new Vector3f(0.35F, 0.22F, 0.3F);
      this.SkyBoxParamSunColor = new Color(1.0F, 0.86F, 0.7F);
      this.SkyBoxParamSkyHColour = new Color(0.1F, 0.1F, 0.4F);
      this.SkyBoxParamSkyLColour = new Color(0.1F, 0.45F, 0.7F);
      this.SkyBoxParamCloudLight = 0.99F;
      this.SkyBoxParamCloudCount = 0.3F;
      this.SkyBoxParamCloudSize = 0.2F;
      this.SkyBoxParamFog = 0.0F;
      this.SkyBoxParamStars = 0.0F;
      this.SkyBoxParamWind = new Vector3f(0.0F);
      this.SkyBoxParamWindINT = new Vector3f(0.0F);
      RenderThread.borrowContext();
      if (GLContext.getCapabilities().OpenGL30) {
         this.apiId = 1;
      }

      if (GLContext.getCapabilities().GL_ARB_framebuffer_object) {
         this.apiId = 2;
      }

      if (GLContext.getCapabilities().GL_EXT_framebuffer_object) {
         this.apiId = 3;
      }

      RenderThread.returnContext();
   }

   public ITexture getTextureCurrent() {
      if (Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox()) {
         return this.isCurrentA ? this.textureFBOA.getTexture() : this.textureFBOB.getTexture();
      } else {
         return this.texAM;
      }
   }

   public ITexture getTexturePrev() {
      if (Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox()) {
         return this.isCurrentA ? this.textureFBOB.getTexture() : this.textureFBOA.getTexture();
      } else {
         return this.texPM;
      }
   }

   public TextureFBO getTextureFBOPrev() {
      if (Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox()) {
         return this.isCurrentA ? this.textureFBOB : this.textureFBOA;
      } else {
         return null;
      }
   }

   public float getTextureShift() {
      if (Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox()) {
         float var1 = (float)this.renderLimit.getTimePeriod();
         return var1;
      } else {
         return 1.0F - GameTime.getInstance().getNight();
      }
   }

   public void swapTextureFBO() {
      this.isCurrentA = !this.isCurrentA;
   }

   public void render() {
      if (Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox() && (this.renderLimit.Check() || !(GameTime.getInstance().getMultiplier() < 20.0F))) {
         ++this.SkyBoxTime;
         int var1 = IsoCamera.frameState.playerIndex;
         int var2 = IsoCamera.getOffscreenLeft(var1);
         int var3 = IsoCamera.getOffscreenTop(var1);
         int var4 = IsoCamera.getOffscreenWidth(var1);
         int var5 = IsoCamera.getOffscreenHeight(var1);
         SpriteRenderer.instance.drawSkyBox(this.Effect, var1, this.apiId, this.getTextureFBOPrev().getBufferId());
         this.isUpdated = false;
      }
   }

   public void draw() {
      if (Core.bDebug && DebugOptions.instance.SkyboxShow.getValue()) {
         ((Texture)this.getTextureCurrent()).render(0, 0, 512, 512, 1.0F, 1.0F, 1.0F, 1.0F);
      }

   }
}
