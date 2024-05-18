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
					this.SkyBoxParamSunLight = new Vector3f(4.0F * float5 - 4.0F, 0.22F, 0.3F);
					this.SkyBoxParamSunLight.normalize();
					this.SkyBoxParamSunLight.mul(Math.min(1.0F, float5 * 10.0F));
					this.SkyBoxParamSunColor = climateManager.getGlobalLight().getExterior();
				} else {
					float5 = (float4 - float3) / (float2 - float3);
					this.SkyHColourDay.interp(this.SkyHColourDusk, float5, this.SkyBoxParamSkyHColour);
					this.SkyLColourDay.interp(this.SkyLColourDusk, float5, this.SkyBoxParamSkyLColour);
					this.SkyBoxParamSunLight = new Vector3f(4.0F * float5, 0.22F, 0.3F);
					this.SkyBoxParamSunLight.normalize();
					this.SkyBoxParamSunLight.mul(Math.min(1.0F, (1.0F - float5) * 10.0F));
					this.SkyBoxParamSunColor = climateManager.getGlobalLight().getExterior();
				}
			} else {
				float5 = 24.0F - float2 + float1;
				float float6;
				if (float4 > float2) {
					float6 = (float4 - float2) / float5;
					this.SkyHColourDusk.interp(this.SkyHColourDawn, float6, this.SkyBoxParamSkyHColour);
					this.SkyLColourDusk.interp(this.SkyLColourDawn, float6, this.SkyBoxParamSkyLColour);
					this.SkyBoxParamSunLight = new Vector3f(0.35F, 0.22F, 0.3F);
					this.SkyBoxParamSunLight.normalize();
					this.SkyBoxParamSunLight.mul(Math.min(1.0F, float6 * 5.0F));
				} else {
					float6 = (24.0F - float2 + float4) / float5;
					this.SkyHColourDusk.interp(this.SkyHColourDawn, float6, this.SkyBoxParamSkyHColour);
					this.SkyLColourDusk.interp(this.SkyLColourDawn, float6, this.SkyBoxParamSkyLColour);
					this.SkyBoxParamSunLight = new Vector3f(0.35F, 0.22F, 0.3F);
					this.SkyBoxParamSunLight.normalize();
					this.SkyBoxParamSunLight.mul(Math.min(1.0F, (1.0F - float6) * 5.0F));
				}

				this.SkyBoxParamSunColor = new Color(climateManager.getGlobalLight().getExterior());
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
			Texture texture = new Texture(512, 512);
			Texture texture2 = new Texture(512, 512);
			this.textureFBOA = new TextureFBO(texture);
			this.textureFBOB = new TextureFBO(texture2);
		} catch (Exception exception) {
			exception.printStackTrace();
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
			float float1 = (float)this.renderLimit.getTimePeriod();
			return float1;
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
			int int1 = IsoCamera.frameState.playerIndex;
			int int2 = IsoCamera.getOffscreenLeft(int1);
			int int3 = IsoCamera.getOffscreenTop(int1);
			int int4 = IsoCamera.getOffscreenWidth(int1);
			int int5 = IsoCamera.getOffscreenHeight(int1);
			SpriteRenderer.instance.drawSkyBox(this.Effect, int1, this.apiId, this.getTextureFBOPrev().getBufferId());
			this.isUpdated = false;
		}
	}

	public void draw() {
		if (Core.bDebug && DebugOptions.instance.SkyboxShow.getValue()) {
			((Texture)this.getTextureCurrent()).render(0, 0, 512, 512, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
