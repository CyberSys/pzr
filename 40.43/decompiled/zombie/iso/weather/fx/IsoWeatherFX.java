package zombie.iso.weather.fx;

import java.io.File;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.textures.ImageData;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.iso.IsoCamera;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameServer;

public class IsoWeatherFX {
   private static boolean VERBOSE = false;
   protected static boolean DEBUG_BOUNDS = false;
   private static float DELTA;
   private ParticleRectangle cloudParticles;
   private ParticleRectangle fogParticles;
   private ParticleRectangle snowParticles;
   private ParticleRectangle rainParticles;
   private static int ID_CLOUD = 0;
   private static int ID_FOG = 1;
   private static int ID_SNOW = 2;
   private static int ID_RAIN = 3;
   public static float ZoomMod = 1.0F;
   private boolean playerIndoors = false;
   protected SteppedUpdateFloat windPrecipIntensity = new SteppedUpdateFloat(0.0F, 0.025F, 0.0F, 1.0F);
   protected SteppedUpdateFloat windIntensity = new SteppedUpdateFloat(0.0F, 0.005F, 0.0F, 1.0F);
   protected SteppedUpdateFloat windAngleIntensity = new SteppedUpdateFloat(0.0F, 0.005F, -1.0F, 1.0F);
   protected SteppedUpdateFloat precipitationIntensity = new SteppedUpdateFloat(0.0F, 0.005F, 0.0F, 1.0F);
   protected SteppedUpdateFloat precipitationIntensitySnow = new SteppedUpdateFloat(0.0F, 0.005F, 0.0F, 1.0F);
   protected SteppedUpdateFloat precipitationIntensityRain = new SteppedUpdateFloat(0.0F, 0.005F, 0.0F, 1.0F);
   protected SteppedUpdateFloat cloudIntensity = new SteppedUpdateFloat(0.0F, 0.005F, 0.0F, 1.0F);
   protected SteppedUpdateFloat fogIntensity = new SteppedUpdateFloat(0.0F, 0.005F, 0.0F, 1.0F);
   protected SteppedUpdateFloat windAngleMod = new SteppedUpdateFloat(0.0F, 0.005F, 0.0F, 1.0F);
   protected boolean precipitationIsSnow = true;
   private float fogOverlayAlpha = 0.0F;
   private float windSpeedMax = 6.0F;
   protected float windSpeed = 0.0F;
   protected float windSpeedFog = 0.0F;
   protected float windAngle = 90.0F;
   protected float windAngleClouds = 90.0F;
   private Texture texFogCircle;
   private Texture texFogWhite;
   private Color fogColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
   protected SteppedUpdateFloat indoorsAlphaMod = new SteppedUpdateFloat(1.0F, 0.05F, 0.0F, 1.0F);
   private ArrayList particleRectangles = new ArrayList(0);
   protected static IsoWeatherFX instance;
   private float windUpdCounter = 0.0F;

   public IsoWeatherFX() {
      instance = this;
   }

   public void init() {
      if (!GameServer.bServer) {
         byte var1 = 0;
         Texture[] var2 = new Texture[6];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = Texture.getSharedTexture("media/textures/weather/clouds_" + var3 + ".png");
            if (var2[var3] == null) {
               DebugLog.log("Missing texture: media/textures/weather/clouds_" + var3 + ".png");
            }
         }

         this.cloudParticles = new ParticleRectangle(8192, 4096);
         WeatherParticle[] var14 = new WeatherParticle[16];

         for(int var4 = 0; var4 < var14.length; ++var4) {
            Texture var5 = var2[Rand.Next(var2.length)];
            CloudParticle var6 = new CloudParticle(var5, var5.getWidth() * 8, var5.getHeight() * 8);
            var6.position.set((float)Rand.Next(0, this.cloudParticles.getWidth()), (float)Rand.Next(0, this.cloudParticles.getHeight()));
            var6.speed = Rand.Next(0.01F, 0.1F);
            var6.angleOffset = 180.0F - Rand.Next(0.0F, 360.0F);
            var6.alpha = Rand.Next(0.25F, 0.75F);
            var14[var4] = var6;
         }

         this.cloudParticles.SetParticles(var14);
         this.cloudParticles.SetParticlesStrength(1.0F);
         this.particleRectangles.add(var1, this.cloudParticles);
         int var13 = var1 + 1;
         ID_CLOUD = var1;
         File var15;
         ImageData var16;
         short var19;
         if (this.texFogCircle == null) {
            this.texFogCircle = Texture.getSharedTexture("media/textures/weather/fogcircle_tex.png");
            var15 = new File("media/textures/weather/fogcircle_tex.png");

            try {
               RenderThread.borrowContext();
               var16 = new ImageData(var15.getAbsolutePath());
               GL11.glBindTexture(3553, Texture.lastTextureID = this.texFogCircle.getID());
               GL11.glTexParameteri(3553, 10242, 33071);
               GL11.glTexParameteri(3553, 10243, 33071);
               GL11.glTexParameteri(3553, 10241, 9728);
               GL11.glTexParameteri(3553, 10240, 9728);
               var19 = 6408;
               GL11.glTexImage2D(3553, 0, var19, this.texFogCircle.getWidthHW(), this.texFogCircle.getHeightHW(), 0, 6408, 5121, var16.getData().getBuffer());
               RenderThread.returnContext();
            } catch (Exception var12) {
               var12.printStackTrace();
            }
         }

         if (this.texFogWhite == null) {
            this.texFogWhite = Texture.getSharedTexture("media/textures/weather/fogwhite_tex.png");
            var15 = new File("media/textures/weather/fogwhite_tex.png");

            try {
               RenderThread.borrowContext();
               var16 = new ImageData(var15.getAbsolutePath());
               GL11.glBindTexture(3553, Texture.lastTextureID = this.texFogWhite.getID());
               GL11.glTexParameteri(3553, 10242, 33071);
               GL11.glTexParameteri(3553, 10243, 33071);
               GL11.glTexParameteri(3553, 10241, 9728);
               GL11.glTexParameteri(3553, 10240, 9728);
               var19 = 6408;
               GL11.glTexImage2D(3553, 0, var19, this.texFogWhite.getWidthHW(), this.texFogWhite.getHeightHW(), 0, 6408, 5121, var16.getData().getBuffer());
               RenderThread.returnContext();
            } catch (Exception var11) {
               var11.printStackTrace();
            }
         }

         Texture[] var17 = new Texture[6];

         for(int var18 = 0; var18 < var17.length; ++var18) {
            var17[var18] = Texture.getSharedTexture("media/textures/weather/fog_" + var18 + ".png");
            if (var17[var18] == null) {
               DebugLog.log("Missing texture: media/textures/weather/fog_" + var18 + ".png");
            }
         }

         this.fogParticles = new ParticleRectangle(2048, 1024);
         WeatherParticle[] var20 = new WeatherParticle[16];

         for(int var21 = 0; var21 < var20.length; ++var21) {
            Texture var7 = var17[Rand.Next(var17.length)];
            FogParticle var8 = new FogParticle(var7, var7.getWidth() * 2, var7.getHeight() * 2);
            var8.position.set((float)Rand.Next(0, this.fogParticles.getWidth()), (float)Rand.Next(0, this.fogParticles.getHeight()));
            var8.speed = Rand.Next(0.01F, 0.1F);
            var8.angleOffset = 180.0F - Rand.Next(0.0F, 360.0F);
            var8.alpha = Rand.Next(0.05F, 0.25F);
            var20[var21] = var8;
         }

         this.fogParticles.SetParticles(var20);
         this.fogParticles.SetParticlesStrength(1.0F);
         this.particleRectangles.add(var13, this.fogParticles);
         ID_FOG = var13++;
         Texture[] var23 = new Texture[3];

         for(int var22 = 0; var22 < var23.length; ++var22) {
            var23[var22] = Texture.getSharedTexture("media/textures/weather/snow_" + (var22 + 1) + ".png");
            if (var23[var22] == null) {
               DebugLog.log("Missing texture: media/textures/weather/snow_" + (var22 + 1) + ".png");
            }
         }

         this.snowParticles = new ParticleRectangle(512, 512);
         WeatherParticle[] var24 = new WeatherParticle[1024];

         for(int var25 = 0; var25 < var24.length; ++var25) {
            SnowParticle var9 = new SnowParticle(var23[Rand.Next(var23.length)]);
            var9.position.set((float)Rand.Next(0, this.snowParticles.getWidth()), (float)Rand.Next(0, this.snowParticles.getHeight()));
            var9.speed = Rand.Next(1.0F, 2.0F);
            var9.angleOffset = 15.0F - Rand.Next(0.0F, 30.0F);
            var9.alpha = Rand.Next(0.5F, 1.0F);
            var24[var25] = var9;
         }

         this.snowParticles.SetParticles(var24);
         this.particleRectangles.add(var13, this.snowParticles);
         ID_SNOW = var13++;
         this.rainParticles = new ParticleRectangle(512, 512);
         WeatherParticle[] var26 = new WeatherParticle[1024];

         for(int var27 = 0; var27 < var26.length; ++var27) {
            RainParticle var10 = new RainParticle(this.texFogWhite, Rand.Next(5, 12));
            var10.position.set((float)Rand.Next(0, this.rainParticles.getWidth()), (float)Rand.Next(0, this.rainParticles.getHeight()));
            var10.speed = (float)Rand.Next(7, 12);
            var10.angleOffset = 3.0F - Rand.Next(0.0F, 6.0F);
            var10.alpha = Rand.Next(0.5F, 0.8F);
            var10.color = new Color(Rand.Next(0.75F, 0.8F), Rand.Next(0.85F, 0.9F), Rand.Next(0.95F, 1.0F), 1.0F);
            var26[var27] = var10;
         }

         this.rainParticles.SetParticles(var26);
         this.particleRectangles.add(var13, this.rainParticles);
         ID_RAIN = var13++;
      }
   }

   public void update() {
      if (!GameServer.bServer) {
         this.playerIndoors = IsoCamera.frameState.CamCharacterSquare != null && !IsoCamera.frameState.CamCharacterSquare.Is(IsoFlagType.exterior);
         GameTime var1 = GameTime.getInstance();
         DELTA = var1.getMultiplier();
         if (!WeatherFxMask.hasMaskToDraw) {
            if (this.playerIndoors && this.indoorsAlphaMod.value() > 0.0F) {
               this.indoorsAlphaMod.setTarget(this.indoorsAlphaMod.value() - 0.05F * DELTA);
            } else if (!this.playerIndoors && this.indoorsAlphaMod.value() < 1.0F) {
               this.indoorsAlphaMod.setTarget(this.indoorsAlphaMod.value() + 0.05F * DELTA);
            }
         } else {
            this.indoorsAlphaMod.setTarget(1.0F);
         }

         this.indoorsAlphaMod.update(DELTA);
         this.cloudIntensity.update(DELTA);
         this.windIntensity.update(DELTA);
         this.windPrecipIntensity.update(DELTA);
         this.windAngleIntensity.update(DELTA);
         this.precipitationIntensity.update(DELTA);
         this.fogIntensity.update(DELTA);
         if (this.precipitationIsSnow) {
            this.precipitationIntensitySnow.setTarget(this.precipitationIntensity.getTarget());
         } else {
            this.precipitationIntensitySnow.setTarget(0.0F);
         }

         if (!this.precipitationIsSnow) {
            this.precipitationIntensityRain.setTarget(this.precipitationIntensity.getTarget());
         } else {
            this.precipitationIntensityRain.setTarget(0.0F);
         }

         if (this.precipitationIsSnow) {
            this.windAngleMod.setTarget(0.3F);
         } else {
            this.windAngleMod.setTarget(0.6F);
         }

         this.precipitationIntensitySnow.update(DELTA);
         this.precipitationIntensityRain.update(DELTA);
         this.windAngleMod.update(DELTA);
         float var2 = this.fogIntensity.value() * this.indoorsAlphaMod.value();
         this.fogOverlayAlpha = 0.8F * var2;
         if (++this.windUpdCounter > 15.0F) {
            this.windUpdCounter = 0.0F;
            if (this.windAngleIntensity.value() > 0.0F) {
               this.windAngle = lerp(this.windPrecipIntensity.value(), 90.0F, 0.0F + 54.0F * this.windAngleMod.value());
               if (this.windAngleIntensity.value() < 0.5F) {
                  this.windAngleClouds = lerp(this.windAngleIntensity.value() * 2.0F, 90.0F, 0.0F);
               } else {
                  this.windAngleClouds = lerp((this.windAngleIntensity.value() - 0.5F) * 2.0F, 360.0F, 270.0F);
               }
            } else if (this.windAngleIntensity.value() < 0.0F) {
               this.windAngle = lerp(Math.abs(this.windPrecipIntensity.value()), 90.0F, 180.0F - 54.0F * this.windAngleMod.value());
               this.windAngleClouds = lerp(Math.abs(this.windAngleIntensity.value()), 90.0F, 270.0F);
            } else {
               this.windAngle = 90.0F;
            }

            this.windSpeed = this.windSpeedMax * this.windPrecipIntensity.value();
            this.windSpeedFog = this.windSpeedMax * this.windIntensity.value() * (4.0F + 16.0F * Math.abs(this.windAngleIntensity.value()));
            if (this.windSpeed < 1.0F) {
               this.windSpeed = 1.0F;
            }

            if (this.windSpeedFog < 1.0F) {
               this.windSpeedFog = 1.0F;
            }
         }

         float var3 = Core.getInstance().getZoom(IsoPlayer.instance.getPlayerNum());
         float var4 = 1.0F - (var3 - 0.5F) * 0.5F * 0.75F;
         ZoomMod = 0.0F;
         if (Core.getInstance().isZoomEnabled() && var3 > 1.0F) {
            ZoomMod = ClimateManager.clamp(0.0F, 1.0F, (var3 - 1.0F) * 0.6666667F);
         }

         if (this.cloudIntensity.value() <= 0.0F) {
            this.cloudParticles.SetParticlesStrength(0.0F);
         } else {
            this.cloudParticles.SetParticlesStrength(1.0F);
         }

         if (this.fogIntensity.value() <= 0.0F) {
            this.fogParticles.SetParticlesStrength(0.0F);
         } else {
            this.fogParticles.SetParticlesStrength(1.0F);
         }

         this.snowParticles.SetParticlesStrength(this.precipitationIntensitySnow.value() * var4);
         this.rainParticles.SetParticlesStrength(this.precipitationIntensityRain.value() * var4);

         for(int var5 = 0; var5 < this.particleRectangles.size(); ++var5) {
            if (((ParticleRectangle)this.particleRectangles.get(var5)).requiresUpdate()) {
               ((ParticleRectangle)this.particleRectangles.get(var5)).update(DELTA);
            }
         }

      }
   }

   public void setDebugBounds(boolean var1) {
      DEBUG_BOUNDS = var1;
   }

   public boolean isDebugBounds() {
      return DEBUG_BOUNDS;
   }

   public void setWindAngleIntensity(float var1) {
      this.windAngleIntensity.setTarget(var1);
      if (VERBOSE) {
         DebugLog.log("Wind angle intensity = " + this.windAngleIntensity.getTarget());
      }

   }

   public float getWindAngleIntensity() {
      return this.windAngleIntensity.value();
   }

   public float getRenderWindAngleRain() {
      return this.windAngle;
   }

   public void setWindPrecipIntensity(float var1) {
      this.windPrecipIntensity.setTarget(var1);
      if (VERBOSE) {
         DebugLog.log("Wind Precip intensity = " + this.windPrecipIntensity.getTarget());
      }

   }

   public float getWindPrecipIntensity() {
      return this.windPrecipIntensity.value();
   }

   public void setWindIntensity(float var1) {
      this.windIntensity.setTarget(var1);
      if (VERBOSE) {
         DebugLog.log("Wind intensity = " + this.windIntensity.getTarget());
      }

   }

   public float getWindIntensity() {
      return this.windIntensity.value();
   }

   public void setFogIntensity(float var1) {
      if (SandboxOptions.instance.MaxFogIntensity.getValue() == 2) {
         var1 = Math.min(var1, 0.75F);
      } else if (SandboxOptions.instance.MaxFogIntensity.getValue() == 3) {
         var1 = Math.min(var1, 0.5F);
      }

      this.fogIntensity.setTarget(var1);
      if (VERBOSE) {
         DebugLog.log("Fog intensity = " + this.fogIntensity.getTarget());
      }

   }

   public float getFogIntensity() {
      return this.fogIntensity.value();
   }

   public void setCloudIntensity(float var1) {
      this.cloudIntensity.setTarget(var1);
      if (VERBOSE) {
         DebugLog.log("Cloud intensity = " + this.cloudIntensity.getTarget());
      }

   }

   public float getCloudIntensity() {
      return this.cloudIntensity.value();
   }

   public void setPrecipitationIntensity(float var1) {
      if (SandboxOptions.instance.MaxRainFxIntensity.getValue() == 2) {
         var1 *= 0.75F;
      } else if (SandboxOptions.instance.MaxRainFxIntensity.getValue() == 3) {
         var1 *= 0.5F;
      }

      if (var1 > 0.0F) {
         var1 = 0.05F + 0.95F * var1;
      }

      this.precipitationIntensity.setTarget(var1);
      if (VERBOSE) {
         DebugLog.log("Precipitation intensity = " + this.precipitationIntensity.getTarget());
      }

   }

   public float getPrecipitationIntensity() {
      return this.precipitationIntensity.value();
   }

   public void setPrecipitationIsSnow(boolean var1) {
      this.precipitationIsSnow = var1;
   }

   public boolean getPrecipitationIsSnow() {
      return this.precipitationIsSnow;
   }

   public boolean hasCloudsToRender() {
      return this.cloudIntensity.value() > 0.0F || ((ParticleRectangle)this.particleRectangles.get(ID_CLOUD)).requiresUpdate();
   }

   public boolean hasPrecipitationToRender() {
      return this.precipitationIntensity.value() > 0.0F || ((ParticleRectangle)this.particleRectangles.get(ID_SNOW)).requiresUpdate() || ((ParticleRectangle)this.particleRectangles.get(ID_RAIN)).requiresUpdate();
   }

   public boolean hasFogToRender() {
      return this.fogIntensity.value() > 0.0F || ((ParticleRectangle)this.particleRectangles.get(ID_FOG)).requiresUpdate();
   }

   public void render() {
      if (!GameServer.bServer) {
         for(int var1 = 0; var1 < this.particleRectangles.size(); ++var1) {
            if (var1 == ID_FOG) {
               this.renderFogCircle();
            }

            if (((ParticleRectangle)this.particleRectangles.get(var1)).requiresUpdate()) {
               ((ParticleRectangle)this.particleRectangles.get(var1)).render();
            }
         }

      }
   }

   public void renderLayered(boolean var1, boolean var2, boolean var3) {
      if (var1) {
         this.renderClouds();
      } else if (var2) {
         this.renderFog();
      } else if (var3) {
         this.renderPrecipitation();
      }

   }

   public void renderClouds() {
      if (!GameServer.bServer) {
         if (((ParticleRectangle)this.particleRectangles.get(ID_CLOUD)).requiresUpdate()) {
            ((ParticleRectangle)this.particleRectangles.get(ID_CLOUD)).render();
         }

      }
   }

   public void renderFog() {
      if (!GameServer.bServer) {
         this.renderFogCircle();
         if (((ParticleRectangle)this.particleRectangles.get(ID_FOG)).requiresUpdate()) {
            ((ParticleRectangle)this.particleRectangles.get(ID_FOG)).render();
         }

      }
   }

   public void renderPrecipitation() {
      if (!GameServer.bServer) {
         if (((ParticleRectangle)this.particleRectangles.get(ID_SNOW)).requiresUpdate()) {
            ((ParticleRectangle)this.particleRectangles.get(ID_SNOW)).render();
         }

         if (((ParticleRectangle)this.particleRectangles.get(ID_RAIN)).requiresUpdate()) {
            ((ParticleRectangle)this.particleRectangles.get(ID_RAIN)).render();
         }

      }
   }

   private void renderFogCircle() {
      if (!(this.fogOverlayAlpha <= 0.0F)) {
         int var1 = IsoCamera.frameState.playerIndex;
         int var2 = Core.getInstance().getOffscreenWidth(var1);
         int var3 = Core.getInstance().getOffscreenHeight(var1);
         int var4 = 2048 - (int)(512.0F * this.fogIntensity.value());
         int var5 = 1024 - (int)(256.0F * this.fogIntensity.value());
         int var6 = var2 / 2 - var4 / 2;
         int var7 = var3 / 2 - var5 / 2;
         var6 = (int)((float)var6 - IsoCamera.getRightClickOffX());
         var7 = (int)((float)var7 - IsoCamera.getRightClickOffY());
         int var8 = var6 + var4;
         int var9 = var7 + var5;
         IndieGL.glTexParameteri(3553, 10241, 9728);
         IndieGL.glTexParameteri(3553, 10240, 9728);
         SpriteRenderer.instance.render(this.texFogCircle, var6, var7, var4, var5, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha);
         SpriteRenderer.instance.render(this.texFogWhite, 0, 0, var6, var3, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha);
         SpriteRenderer.instance.render(this.texFogWhite, var6, 0, var4, var7, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha);
         SpriteRenderer.instance.render(this.texFogWhite, var8, 0, var2 - var8, var3, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha);
         SpriteRenderer.instance.render(this.texFogWhite, var6, var9, var4, var3 - var9, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha);
         if (Core.getInstance().getOffscreenBuffer() != null) {
            if (Core.getInstance().isZoomEnabled() && Core.getInstance().getZoom(var1) > 0.5F) {
               IndieGL.glTexParameteri(3553, 10241, 9729);
            } else {
               IndieGL.glTexParameteri(3553, 10241, 9728);
            }

            if (Core.getInstance().getZoom(var1) == 0.5F) {
               IndieGL.glTexParameteri(3553, 10240, 9728);
            } else {
               IndieGL.glTexParameteri(3553, 10240, 9729);
            }
         }

      }
   }

   public static float clamp(float var0, float var1, float var2) {
      var2 = Math.min(var1, var2);
      var2 = Math.max(var0, var2);
      return var2;
   }

   public static float lerp(float var0, float var1, float var2) {
      return var1 + var0 * (var2 - var1);
   }

   public static float clerp(float var0, float var1, float var2) {
      float var3 = (float)(1.0D - Math.cos((double)var0 * 3.141592653589793D)) / 2.0F;
      return var1 * (1.0F - var3) + var2 * var3;
   }
}
