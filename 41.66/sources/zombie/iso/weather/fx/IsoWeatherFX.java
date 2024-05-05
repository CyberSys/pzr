package zombie.iso.weather.fx;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.shader.ShaderManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
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
	protected boolean playerIndoors = false;
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
	static Shader s_shader;
	static final IsoWeatherFX.Drawer[][] s_drawer = new IsoWeatherFX.Drawer[4][3];

	public IsoWeatherFX() {
		instance = this;
	}

	public void init() {
		if (!GameServer.bServer) {
			byte byte1 = 0;
			Texture[] textureArray = new Texture[6];
			for (int int1 = 0; int1 < textureArray.length; ++int1) {
				textureArray[int1] = Texture.getSharedTexture("media/textures/weather/clouds_" + int1 + ".png");
				if (textureArray[int1] == null) {
					DebugLog.log("Missing texture: media/textures/weather/clouds_" + int1 + ".png");
				}
			}

			this.cloudParticles = new ParticleRectangle(8192, 4096);
			WeatherParticle[] weatherParticleArray = new WeatherParticle[16];
			for (int int2 = 0; int2 < weatherParticleArray.length; ++int2) {
				Texture texture = textureArray[Rand.Next(textureArray.length)];
				CloudParticle cloudParticle = new CloudParticle(texture, texture.getWidth() * 8, texture.getHeight() * 8);
				cloudParticle.position.set((float)Rand.Next(0, this.cloudParticles.getWidth()), (float)Rand.Next(0, this.cloudParticles.getHeight()));
				cloudParticle.speed = Rand.Next(0.01F, 0.1F);
				cloudParticle.angleOffset = 180.0F - Rand.Next(0.0F, 360.0F);
				cloudParticle.alpha = Rand.Next(0.25F, 0.75F);
				weatherParticleArray[int2] = cloudParticle;
			}

			this.cloudParticles.SetParticles(weatherParticleArray);
			this.cloudParticles.SetParticlesStrength(1.0F);
			this.particleRectangles.add(byte1, this.cloudParticles);
			int int3 = byte1 + 1;
			ID_CLOUD = byte1;
			if (this.texFogCircle == null) {
				this.texFogCircle = Texture.getSharedTexture("media/textures/weather/fogcircle_tex.png", 35);
			}

			if (this.texFogWhite == null) {
				this.texFogWhite = Texture.getSharedTexture("media/textures/weather/fogwhite_tex.png", 35);
			}

			Texture[] textureArray2 = new Texture[6];
			for (int int4 = 0; int4 < textureArray2.length; ++int4) {
				textureArray2[int4] = Texture.getSharedTexture("media/textures/weather/fog_" + int4 + ".png");
				if (textureArray2[int4] == null) {
					DebugLog.log("Missing texture: media/textures/weather/fog_" + int4 + ".png");
				}
			}

			this.fogParticles = new ParticleRectangle(2048, 1024);
			WeatherParticle[] weatherParticleArray2 = new WeatherParticle[16];
			for (int int5 = 0; int5 < weatherParticleArray2.length; ++int5) {
				Texture texture2 = textureArray2[Rand.Next(textureArray2.length)];
				FogParticle fogParticle = new FogParticle(texture2, texture2.getWidth() * 2, texture2.getHeight() * 2);
				fogParticle.position.set((float)Rand.Next(0, this.fogParticles.getWidth()), (float)Rand.Next(0, this.fogParticles.getHeight()));
				fogParticle.speed = Rand.Next(0.01F, 0.1F);
				fogParticle.angleOffset = 180.0F - Rand.Next(0.0F, 360.0F);
				fogParticle.alpha = Rand.Next(0.05F, 0.25F);
				weatherParticleArray2[int5] = fogParticle;
			}

			this.fogParticles.SetParticles(weatherParticleArray2);
			this.fogParticles.SetParticlesStrength(1.0F);
			this.particleRectangles.add(int3, this.fogParticles);
			ID_FOG = int3++;
			Texture[] textureArray3 = new Texture[3];
			for (int int6 = 0; int6 < textureArray3.length; ++int6) {
				textureArray3[int6] = Texture.getSharedTexture("media/textures/weather/snow_" + (int6 + 1) + ".png");
				if (textureArray3[int6] == null) {
					DebugLog.log("Missing texture: media/textures/weather/snow_" + (int6 + 1) + ".png");
				}
			}

			this.snowParticles = new ParticleRectangle(512, 512);
			WeatherParticle[] weatherParticleArray3 = new WeatherParticle[1024];
			for (int int7 = 0; int7 < weatherParticleArray3.length; ++int7) {
				SnowParticle snowParticle = new SnowParticle(textureArray3[Rand.Next(textureArray3.length)]);
				snowParticle.position.set((float)Rand.Next(0, this.snowParticles.getWidth()), (float)Rand.Next(0, this.snowParticles.getHeight()));
				snowParticle.speed = Rand.Next(1.0F, 2.0F);
				snowParticle.angleOffset = 15.0F - Rand.Next(0.0F, 30.0F);
				snowParticle.alpha = Rand.Next(0.25F, 0.6F);
				weatherParticleArray3[int7] = snowParticle;
			}

			this.snowParticles.SetParticles(weatherParticleArray3);
			this.particleRectangles.add(int3, this.snowParticles);
			ID_SNOW = int3++;
			this.rainParticles = new ParticleRectangle(512, 512);
			WeatherParticle[] weatherParticleArray4 = new WeatherParticle[1024];
			for (int int8 = 0; int8 < weatherParticleArray4.length; ++int8) {
				RainParticle rainParticle = new RainParticle(this.texFogWhite, Rand.Next(5, 12));
				rainParticle.position.set((float)Rand.Next(0, this.rainParticles.getWidth()), (float)Rand.Next(0, this.rainParticles.getHeight()));
				rainParticle.speed = (float)Rand.Next(7, 12);
				rainParticle.angleOffset = 3.0F - Rand.Next(0.0F, 6.0F);
				rainParticle.alpha = Rand.Next(0.5F, 0.8F);
				rainParticle.color = new Color(Rand.Next(0.75F, 0.8F), Rand.Next(0.85F, 0.9F), Rand.Next(0.95F, 1.0F), 1.0F);
				weatherParticleArray4[int8] = rainParticle;
			}

			this.rainParticles.SetParticles(weatherParticleArray4);
			this.particleRectangles.add(int3, this.rainParticles);
			ID_RAIN = int3++;
		}
	}

	public void update() {
		if (!GameServer.bServer) {
			this.playerIndoors = IsoCamera.frameState.CamCharacterSquare != null && !IsoCamera.frameState.CamCharacterSquare.Is(IsoFlagType.exterior);
			GameTime gameTime = GameTime.getInstance();
			DELTA = gameTime.getMultiplier();
			if (!WeatherFxMask.playerHasMaskToDraw(IsoCamera.frameState.playerIndex)) {
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
			float float1 = this.fogIntensity.value() * this.indoorsAlphaMod.value();
			this.fogOverlayAlpha = 0.8F * float1;
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

			float float2 = Core.getInstance().getZoom(IsoPlayer.getInstance().getPlayerNum());
			float float3 = 1.0F - (float2 - 0.5F) * 0.5F * 0.75F;
			ZoomMod = 0.0F;
			if (Core.getInstance().isZoomEnabled() && float2 > 1.0F) {
				ZoomMod = ClimateManager.clamp(0.0F, 1.0F, (float2 - 1.0F) * 0.6666667F);
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

			this.snowParticles.SetParticlesStrength(this.precipitationIntensitySnow.value() * float3);
			this.rainParticles.SetParticlesStrength(this.precipitationIntensityRain.value() * float3);
			for (int int1 = 0; int1 < this.particleRectangles.size(); ++int1) {
				if (((ParticleRectangle)this.particleRectangles.get(int1)).requiresUpdate()) {
					((ParticleRectangle)this.particleRectangles.get(int1)).update(DELTA);
				}
			}
		}
	}

	public void setDebugBounds(boolean boolean1) {
		DEBUG_BOUNDS = boolean1;
	}

	public boolean isDebugBounds() {
		return DEBUG_BOUNDS;
	}

	public void setWindAngleIntensity(float float1) {
		this.windAngleIntensity.setTarget(float1);
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

	public void setWindPrecipIntensity(float float1) {
		this.windPrecipIntensity.setTarget(float1);
		if (VERBOSE) {
			DebugLog.log("Wind Precip intensity = " + this.windPrecipIntensity.getTarget());
		}
	}

	public float getWindPrecipIntensity() {
		return this.windPrecipIntensity.value();
	}

	public void setWindIntensity(float float1) {
		this.windIntensity.setTarget(float1);
		if (VERBOSE) {
			DebugLog.log("Wind intensity = " + this.windIntensity.getTarget());
		}
	}

	public float getWindIntensity() {
		return this.windIntensity.value();
	}

	public void setFogIntensity(float float1) {
		if (SandboxOptions.instance.MaxFogIntensity.getValue() == 2) {
			float1 = Math.min(float1, 0.75F);
		} else if (SandboxOptions.instance.MaxFogIntensity.getValue() == 3) {
			float1 = Math.min(float1, 0.5F);
		}

		this.fogIntensity.setTarget(float1);
		if (VERBOSE) {
			DebugLog.log("Fog intensity = " + this.fogIntensity.getTarget());
		}
	}

	public float getFogIntensity() {
		return this.fogIntensity.value();
	}

	public void setCloudIntensity(float float1) {
		this.cloudIntensity.setTarget(float1);
		if (VERBOSE) {
			DebugLog.log("Cloud intensity = " + this.cloudIntensity.getTarget());
		}
	}

	public float getCloudIntensity() {
		return this.cloudIntensity.value();
	}

	public void setPrecipitationIntensity(float float1) {
		if (SandboxOptions.instance.MaxRainFxIntensity.getValue() == 2) {
			float1 *= 0.75F;
		} else if (SandboxOptions.instance.MaxRainFxIntensity.getValue() == 3) {
			float1 *= 0.5F;
		}

		if (float1 > 0.0F) {
			float1 = 0.05F + 0.95F * float1;
		}

		this.precipitationIntensity.setTarget(float1);
		if (VERBOSE) {
			DebugLog.log("Precipitation intensity = " + this.precipitationIntensity.getTarget());
		}
	}

	public float getPrecipitationIntensity() {
		return this.precipitationIntensity.value();
	}

	public void setPrecipitationIsSnow(boolean boolean1) {
		this.precipitationIsSnow = boolean1;
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
			for (int int1 = 0; int1 < this.particleRectangles.size(); ++int1) {
				if (int1 == ID_FOG) {
					if (PerformanceSettings.FogQuality != 2) {
						continue;
					}

					this.renderFogCircle();
				}

				if ((int1 != ID_RAIN && int1 != ID_SNOW || Core.OptionRenderPrecipitation <= 2) && ((ParticleRectangle)this.particleRectangles.get(int1)).requiresUpdate()) {
					((ParticleRectangle)this.particleRectangles.get(int1)).render();
				}
			}
		}
	}

	public void renderLayered(boolean boolean1, boolean boolean2, boolean boolean3) {
		if (boolean1) {
			this.renderClouds();
		} else if (boolean2) {
			this.renderFog();
		} else if (boolean3) {
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
			int int1 = IsoCamera.frameState.playerIndex;
			float float1 = Core.getInstance().getCurrentPlayerZoom();
			int int2 = IsoCamera.getScreenWidth(int1);
			int int3 = IsoCamera.getScreenHeight(int1);
			int int4 = 2048 - (int)(512.0F * this.fogIntensity.value());
			int int5 = 1024 - (int)(256.0F * this.fogIntensity.value());
			int4 = (int)((float)int4 / float1);
			int5 = (int)((float)int5 / float1);
			int int6 = int2 / 2 - int4 / 2;
			int int7 = int3 / 2 - int5 / 2;
			int6 = (int)((float)int6 - IsoCamera.getRightClickOffX() / float1);
			int7 = (int)((float)int7 - IsoCamera.getRightClickOffY() / float1);
			int int8 = int6 + int4;
			int int9 = int7 + int5;
			SpriteRenderer.instance.glBind(this.texFogWhite.getID());
			IndieGL.glTexParameteri(3553, 10241, 9728);
			IndieGL.glTexParameteri(3553, 10240, 9728);
			if (s_shader == null) {
				RenderThread.invokeOnRenderContext(()->{
					s_shader = ShaderManager.instance.getOrCreateShader("fogCircle", false);
				});
			}

			if (s_shader.getShaderProgram().isCompiled()) {
				IndieGL.StartShader(s_shader.getID(), int1);
				int int10 = SpriteRenderer.instance.getMainStateIndex();
				if (s_drawer[int1][int10] == null) {
					s_drawer[int1][int10] = new IsoWeatherFX.Drawer();
				}

				s_drawer[int1][int10].init(int2, int3);
			}

			SpriteRenderer.instance.renderi(this.texFogCircle, int6, int7, int4, int5, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha, (Consumer)null);
			SpriteRenderer.instance.renderi(this.texFogWhite, 0, 0, int6, int3, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha, (Consumer)null);
			SpriteRenderer.instance.renderi(this.texFogWhite, int6, 0, int4, int7, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha, (Consumer)null);
			SpriteRenderer.instance.renderi(this.texFogWhite, int8, 0, int2 - int8, int3, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha, (Consumer)null);
			SpriteRenderer.instance.renderi(this.texFogWhite, int6, int9, int4, int3 - int9, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha, (Consumer)null);
			if (s_shader.getShaderProgram().isCompiled()) {
				IndieGL.EndShader();
			}

			if (Core.getInstance().getOffscreenBuffer() != null) {
				if (Core.getInstance().isZoomEnabled() && Core.getInstance().getZoom(int1) > 0.5F) {
					IndieGL.glTexParameteri(3553, 10241, 9729);
				} else {
					IndieGL.glTexParameteri(3553, 10241, 9728);
				}

				if (Core.getInstance().getZoom(int1) == 0.5F) {
					IndieGL.glTexParameteri(3553, 10240, 9728);
				} else {
					IndieGL.glTexParameteri(3553, 10240, 9729);
				}
			}
		}
	}

	public static float clamp(float float1, float float2, float float3) {
		float3 = Math.min(float2, float3);
		float3 = Math.max(float1, float3);
		return float3;
	}

	public static float lerp(float float1, float float2, float float3) {
		return float2 + float1 * (float3 - float2);
	}

	public static float clerp(float float1, float float2, float float3) {
		float float4 = (float)(1.0 - Math.cos((double)float1 * 3.141592653589793)) / 2.0F;
		return float2 * (1.0F - float4) + float3 * float4;
	}

	private static final class Drawer extends TextureDraw.GenericDrawer {
		static final Matrix4f s_matrix4f = new Matrix4f();
		final org.lwjgl.util.vector.Matrix4f m_mvp = new org.lwjgl.util.vector.Matrix4f();
		int m_width;
		int m_height;
		boolean m_bSet = false;

		void init(int int1, int int2) {
			if (int1 != this.m_width || int2 != this.m_height || !this.m_bSet) {
				this.m_width = int1;
				this.m_height = int2;
				this.m_bSet = false;
				s_matrix4f.setOrtho(0.0F, (float)this.m_width, (float)this.m_height, 0.0F, -1.0F, 1.0F);
				PZMath.convertMatrix(s_matrix4f, this.m_mvp);
				this.m_mvp.transpose();
				SpriteRenderer.instance.drawGeneric(this);
			}
		}

		public void render() {
			IsoWeatherFX.s_shader.getShaderProgram().setValue("u_mvp", this.m_mvp);
			this.m_bSet = true;
		}
	}
}
