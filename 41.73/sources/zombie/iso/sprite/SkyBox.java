package zombie.iso.sprite;

import java.util.function.Consumer;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL;
import zombie.GameTime;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
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
	private final UpdateLimit renderLimit = new UpdateLimit(1000L);
	private boolean isUpdated = false;
	private int SkyBoxTime;
	private float SkyBoxParamCloudCount;
	private float SkyBoxParamCloudSize;
	private final Vector3f SkyBoxParamSunLight = new Vector3f();
	private final Color SkyBoxParamSunColor = new Color(1.0F, 1.0F, 1.0F);
	private final Color SkyBoxParamSkyHColour = new Color(1.0F, 1.0F, 1.0F);
	private final Color SkyBoxParamSkyLColour = new Color(1.0F, 1.0F, 1.0F);
	private float SkyBoxParamCloudLight;
	private float SkyBoxParamStars;
	private float SkyBoxParamFog;
	private final Vector3f SkyBoxParamWind;
	private boolean isSetAVG = false;
	private float SkyBoxParamCloudCountAVG;
	private float SkyBoxParamCloudSizeAVG;
	private final Vector3f SkyBoxParamSunLightAVG = new Vector3f();
	private final Color SkyBoxParamSunColorAVG = new Color(1.0F, 1.0F, 1.0F);
	private final Color SkyBoxParamSkyHColourAVG = new Color(1.0F, 1.0F, 1.0F);
	private final Color SkyBoxParamSkyLColourAVG = new Color(1.0F, 1.0F, 1.0F);
	private float SkyBoxParamCloudLightAVG;
	private float SkyBoxParamStarsAVG;
	private float SkyBoxParamFogAVG;
	private final Vector3f SkyBoxParamWindINT;
	private Texture texAM = Texture.getSharedTexture("media/textures/CMVehicleReflection/ref_am.png");
	private Texture texPM = Texture.getSharedTexture("media/textures/CMVehicleReflection/ref_am.png");
	private final Color SkyHColourDay = new Color(0.1F, 0.1F, 0.4F);
	private final Color SkyHColourDawn = new Color(0.2F, 0.2F, 0.3F);
	private final Color SkyHColourDusk = new Color(0.2F, 0.2F, 0.3F);
	private final Color SkyHColourNight = new Color(0.01F, 0.01F, 0.04F);
	private final Color SkyLColourDay = new Color(0.1F, 0.45F, 0.7F);
	private final Color SkyLColourDawn = new Color(0.1F, 0.4F, 0.6F);
	private final Color SkyLColourDusk = new Color(0.1F, 0.4F, 0.6F);
	private final Color SkyLColourNight = new Color(0.01F, 0.045F, 0.07F);
	private int apiId;

	public static synchronized SkyBox getInstance() {
		if (instance == null) {
			instance = new SkyBox();
		}

		return instance;
	}

	public void update(ClimateManager climateManager) {
		if (!this.isUpdated) {
			this.isUpdated = true;
			GameTime gameTime = GameTime.getInstance();
			ClimateManager.DayInfo dayInfo = climateManager.getCurrentDay();
			float float1 = dayInfo.season.getDawn();
			float float2 = dayInfo.season.getDusk();
			float float3 = dayInfo.season.getDayHighNoon();
			float float4 = gameTime.getTimeOfDay();
			float float5;
			if (!(float4 < float1) && !(float4 > float2)) {
				if (float4 < float3) {
					float5 = (float4 - float1) / (float3 - float1);
					this.SkyHColourDawn.interp(this.SkyHColourDay, float5, this.SkyBoxParamSkyHColour);
					this.SkyLColourDawn.interp(this.SkyLColourDay, float5, this.SkyBoxParamSkyLColour);
					this.SkyBoxParamSunLight.set(4.0F * float5 - 4.0F, 0.22F, 0.3F);
					this.SkyBoxParamSunLight.normalize();
					this.SkyBoxParamSunLight.mul(Math.min(1.0F, float5 * 10.0F));
					this.SkyBoxParamSunColor.set(climateManager.getGlobalLight().getExterior());
				} else {
					float5 = (float4 - float3) / (float2 - float3);
					this.SkyHColourDay.interp(this.SkyHColourDusk, float5, this.SkyBoxParamSkyHColour);
					this.SkyLColourDay.interp(this.SkyLColourDusk, float5, this.SkyBoxParamSkyLColour);
					this.SkyBoxParamSunLight.set(4.0F * float5, 0.22F, 0.3F);
					this.SkyBoxParamSunLight.normalize();
					this.SkyBoxParamSunLight.mul(Math.min(1.0F, (1.0F - float5) * 10.0F));
					this.SkyBoxParamSunColor.set(climateManager.getGlobalLight().getExterior());
				}
			} else {
				float5 = 24.0F - float2 + float1;
				float float6;
				if (float4 > float2) {
					float6 = (float4 - float2) / float5;
					this.SkyHColourDusk.interp(this.SkyHColourDawn, float6, this.SkyBoxParamSkyHColour);
					this.SkyLColourDusk.interp(this.SkyLColourDawn, float6, this.SkyBoxParamSkyLColour);
					this.SkyBoxParamSunLight.set(0.35F, 0.22F, 0.3F);
					this.SkyBoxParamSunLight.normalize();
					this.SkyBoxParamSunLight.mul(Math.min(1.0F, float6 * 5.0F));
				} else {
					float6 = (24.0F - float2 + float4) / float5;
					this.SkyHColourDusk.interp(this.SkyHColourDawn, float6, this.SkyBoxParamSkyHColour);
					this.SkyLColourDusk.interp(this.SkyLColourDawn, float6, this.SkyBoxParamSkyLColour);
					this.SkyBoxParamSunLight.set(0.35F, 0.22F, 0.3F);
					this.SkyBoxParamSunLight.normalize();
					this.SkyBoxParamSunLight.mul(Math.min(1.0F, (1.0F - float6) * 5.0F));
				}

				this.SkyBoxParamSunColor.set(climateManager.getGlobalLight().getExterior());
				this.SkyBoxParamSunColor.scale(climateManager.getNightStrength());
			}

			this.SkyBoxParamSkyHColour.interp(this.SkyHColourNight, climateManager.getNightStrength(), this.SkyBoxParamSkyHColour);
			this.SkyBoxParamSkyLColour.interp(this.SkyLColourNight, climateManager.getNightStrength(), this.SkyBoxParamSkyLColour);
			this.SkyBoxParamCloudCount = Math.min(Math.max(climateManager.getCloudIntensity(), climateManager.getPrecipitationIntensity() * 2.0F), 0.999F);
			this.SkyBoxParamCloudSize = 0.02F + climateManager.getTemperature() / 70.0F;
			this.SkyBoxParamFog = climateManager.getFogIntensity();
			this.SkyBoxParamStars = climateManager.getNightStrength();
			this.SkyBoxParamCloudLight = (float)(1.0 - (1.0 - 1.0 * Math.pow(1000.0, (double)(-climateManager.getPrecipitationIntensity() - climateManager.getNightStrength()))));
			float5 = (1.0F - (climateManager.getWindAngleIntensity() + 1.0F) * 0.5F + 0.25F) % 1.0F;
			float5 *= 360.0F;
			this.SkyBoxParamWind.set((float)Math.cos(Math.toRadians((double)float5)), 0.0F, (float)Math.sin(Math.toRadians((double)float5)));
			this.SkyBoxParamWind.mul(climateManager.getWindIntensity());
			if (!this.isSetAVG) {
				this.isSetAVG = true;
				this.SkyBoxParamCloudCountAVG = this.SkyBoxParamCloudCount;
				this.SkyBoxParamCloudSizeAVG = this.SkyBoxParamCloudSize;
				this.SkyBoxParamSunLightAVG.set((Vector3fc)this.SkyBoxParamSunLight);
				this.SkyBoxParamSunColorAVG.set(this.SkyBoxParamSunColor);
				this.SkyBoxParamSkyHColourAVG.set(this.SkyBoxParamSkyHColour);
				this.SkyBoxParamSkyLColourAVG.set(this.SkyBoxParamSkyLColour);
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
			Texture texture = new Texture(512, 512, 16);
			Texture texture2 = new Texture(512, 512, 16);
			this.textureFBOA = new TextureFBO(texture);
			this.textureFBOB = new TextureFBO(texture2);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		this.def = IsoSpriteInstance.get(this.sprite);
		this.SkyBoxTime = 0;
		this.SkyBoxParamSunLight.set(0.35F, 0.22F, 0.3F);
		this.SkyBoxParamSunColor.set(1.0F, 0.86F, 0.7F, 1.0F);
		this.SkyBoxParamSkyHColour.set(0.1F, 0.1F, 0.4F, 1.0F);
		this.SkyBoxParamSkyLColour.set(0.1F, 0.45F, 0.7F, 1.0F);
		this.SkyBoxParamCloudLight = 0.99F;
		this.SkyBoxParamCloudCount = 0.3F;
		this.SkyBoxParamCloudSize = 0.2F;
		this.SkyBoxParamFog = 0.0F;
		this.SkyBoxParamStars = 0.0F;
		this.SkyBoxParamWind = new Vector3f(0.0F);
		this.SkyBoxParamWindINT = new Vector3f(0.0F);
		RenderThread.invokeOnRenderContext(()->{
			if (Core.getInstance().getPerfSkybox() == 0) {
				this.Effect = new SkyBoxShader("skybox_hires");
			} else {
				this.Effect = new SkyBoxShader("skybox");
			}

			if (GL.getCapabilities().OpenGL30) {
				this.apiId = 1;
			}

			if (GL.getCapabilities().GL_ARB_framebuffer_object) {
				this.apiId = 2;
			}

			if (GL.getCapabilities().GL_EXT_framebuffer_object) {
				this.apiId = 3;
			}
		});
	}

	public ITexture getTextureCurrent() {
		if (Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox() != 2) {
			return this.isCurrentA ? this.textureFBOA.getTexture() : this.textureFBOB.getTexture();
		} else {
			return this.texAM;
		}
	}

	public ITexture getTexturePrev() {
		if (Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox() != 2) {
			return this.isCurrentA ? this.textureFBOB.getTexture() : this.textureFBOA.getTexture();
		} else {
			return this.texPM;
		}
	}

	public TextureFBO getTextureFBOPrev() {
		if (Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox() != 2) {
			return this.isCurrentA ? this.textureFBOB : this.textureFBOA;
		} else {
			return null;
		}
	}

	public float getTextureShift() {
		if (Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox() != 2) {
			float float1 = (float)this.renderLimit.getTimePeriod();
			return float1;
		} else {
			return 1.0F - GameTime.getInstance().getNight();
		}
	}

	public void swapTextureFBO() {
		this.renderLimit.updateTimePeriod();
		this.isCurrentA = !this.isCurrentA;
	}

	public void render() {
		if (Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox() != 2) {
			if (!this.renderLimit.Check()) {
				if (GameTime.getInstance().getMultiplier() >= 20.0F) {
					++this.SkyBoxTime;
				}
			} else {
				++this.SkyBoxTime;
				int int1 = IsoCamera.frameState.playerIndex;
				int int2 = IsoCamera.getOffscreenLeft(int1);
				int int3 = IsoCamera.getOffscreenTop(int1);
				int int4 = IsoCamera.getOffscreenWidth(int1);
				int int5 = IsoCamera.getOffscreenHeight(int1);
				SpriteRenderer.instance.drawSkyBox(this.Effect, int1, this.apiId, this.getTextureFBOPrev().getBufferId());
				this.isUpdated = false;
			}
		}
	}

	public void draw() {
		if (Core.bDebug && DebugOptions.instance.SkyboxShow.getValue()) {
			((Texture)this.getTextureCurrent()).render(0.0F, 0.0F, 512.0F, 512.0F, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
		}
	}
}
