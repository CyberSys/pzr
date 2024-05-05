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
import zombie.iso.IsoCamera;
import zombie.iso.PlayerCamera;
import zombie.iso.SearchMode;


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
	private int SearchModeID;
	private int ScreenInfo;
	private int ParamInfo;
	private int VarInfo;
	private int timerVal;
	private boolean bAlt = false;
	private static final int texdVarsSize = 22;
	private static float[][] floatArrs = new float[5][];

	public WeatherShader(String string) {
		super(string);
	}

	public void startMainThread(TextureDraw textureDraw, int int1) {
		if (int1 >= 0 && int1 < 4) {
			RenderSettings.PlayerRenderSettings playerRenderSettings = RenderSettings.getInstance().getPlayerSettings(int1);
			IsoPlayer player = IsoPlayer.players[int1];
			boolean boolean1 = playerRenderSettings.isExterior();
			float float1 = GameTime.instance.TimeOfDay / 12.0F - 1.0F;
			if (Math.abs(float1) > 0.8F && player != null && player.Traits.NightVision.isSet() && !player.isWearingNightVisionGoggles()) {
				float1 *= 0.8F;
			}

			int int2 = Core.getInstance().getOffscreenWidth(int1);
			int int3 = Core.getInstance().getOffscreenHeight(int1);
			if (textureDraw.vars == null) {
				textureDraw.vars = getFreeFloatArray();
				if (textureDraw.vars == null) {
					textureDraw.vars = new float[22];
				}
			}

			textureDraw.vars[0] = playerRenderSettings.getBlendColor().r;
			textureDraw.vars[1] = playerRenderSettings.getBlendColor().g;
			textureDraw.vars[2] = playerRenderSettings.getBlendColor().b;
			textureDraw.vars[3] = playerRenderSettings.getBlendIntensity();
			textureDraw.vars[4] = playerRenderSettings.getDesaturation();
			textureDraw.vars[5] = playerRenderSettings.isApplyNightVisionGoggles() ? 1.0F : 0.0F;
			SearchMode.PlayerSearchMode playerSearchMode = SearchMode.getInstance().getSearchModeForPlayer(int1);
			textureDraw.vars[6] = playerSearchMode.getShaderBlur();
			textureDraw.vars[7] = playerSearchMode.getShaderRadius();
			textureDraw.vars[8] = (float)IsoCamera.getOffscreenLeft(int1);
			textureDraw.vars[9] = (float)IsoCamera.getOffscreenTop(int1);
			PlayerCamera playerCamera = IsoCamera.cameras[int1];
			textureDraw.vars[10] = (float)IsoCamera.getOffscreenWidth(int1);
			textureDraw.vars[11] = (float)IsoCamera.getOffscreenHeight(int1);
			textureDraw.vars[12] = playerCamera.RightClickX;
			textureDraw.vars[13] = playerCamera.RightClickY;
			textureDraw.vars[14] = Core.getInstance().getZoom(int1);
			textureDraw.vars[15] = Core.TileScale == 2 ? 64.0F : 32.0F;
			textureDraw.vars[16] = playerSearchMode.getShaderGradientWidth() * textureDraw.vars[15] / 2.0F;
			textureDraw.vars[17] = playerSearchMode.getShaderDesat();
			textureDraw.vars[18] = playerSearchMode.isShaderEnabled() ? 1.0F : 0.0F;
			textureDraw.vars[19] = playerSearchMode.getShaderDarkness();
			textureDraw.flipped = playerRenderSettings.isExterior();
			textureDraw.f1 = playerRenderSettings.getDarkness();
			textureDraw.col0 = int2;
			textureDraw.col1 = int3;
			textureDraw.col2 = Core.getInstance().getOffscreenTrueWidth();
			textureDraw.col3 = Core.getInstance().getOffscreenTrueHeight();
			textureDraw.bSingleCol = Core.getInstance().getZoom(int1) > 2.0F || (double)Core.getInstance().getZoom(int1) < 2.0 && Core.getInstance().getZoom(int1) >= 1.75F;
		}
	}

	public void startRenderThread(TextureDraw textureDraw) {
		float float1 = textureDraw.f1;
		boolean boolean1 = textureDraw.flipped;
		int int1 = textureDraw.col0;
		int int2 = textureDraw.col1;
		int int3 = textureDraw.col2;
		int int4 = textureDraw.col3;
		float float2 = textureDraw.bSingleCol ? 1.0F : 0.0F;
		ARBShaderObjects.glUniform1fARB(this.width, (float)int1);
		ARBShaderObjects.glUniform1fARB(this.height, (float)int2);
		ARBShaderObjects.glUniform3fARB(this.Light, textureDraw.vars[0], textureDraw.vars[1], textureDraw.vars[2]);
		ARBShaderObjects.glUniform1fARB(this.LightIntensity, textureDraw.vars[3]);
		ARBShaderObjects.glUniform1fARB(this.NightValue, float1);
		ARBShaderObjects.glUniform1fARB(this.DesaturationVal, textureDraw.vars[4]);
		ARBShaderObjects.glUniform1fARB(this.NightVisionGoggles, textureDraw.vars[5]);
		ARBShaderObjects.glUniform1fARB(this.Exterior, boolean1 ? 1.0F : 0.0F);
		ARBShaderObjects.glUniform1fARB(this.timer, (float)(this.timerVal / 2));
		if (PerformanceSettings.getLockFPS() >= 60) {
			if (this.bAlt) {
				++this.timerVal;
			}

			this.bAlt = !this.bAlt;
		} else {
			this.timerVal += 2;
		}

		float float3 = 0.0F;
		float float4 = 0.0F;
		float float5 = 1.0F / (float)int1;
		float float6 = 1.0F / (float)int2;
		ARBShaderObjects.glUniform2fARB(this.TextureSize, (float)int3, (float)int4);
		ARBShaderObjects.glUniform1fARB(this.Zoom, float2);
		ARBShaderObjects.glUniform4fARB(this.SearchModeID, textureDraw.vars[6], textureDraw.vars[7], textureDraw.vars[8], textureDraw.vars[9]);
		ARBShaderObjects.glUniform4fARB(this.ScreenInfo, textureDraw.vars[10], textureDraw.vars[11], textureDraw.vars[12], textureDraw.vars[13]);
		ARBShaderObjects.glUniform4fARB(this.ParamInfo, textureDraw.vars[14], textureDraw.vars[15], textureDraw.vars[16], textureDraw.vars[17]);
		ARBShaderObjects.glUniform4fARB(this.VarInfo, textureDraw.vars[18], textureDraw.vars[19], textureDraw.vars[20], textureDraw.vars[21]);
	}

	public void onCompileSuccess(ShaderProgram shaderProgram) {
		int int1 = this.getID();
		this.timeOfDay = ARBShaderObjects.glGetUniformLocationARB(int1, "TimeOfDay");
		this.bloom = ARBShaderObjects.glGetUniformLocationARB(int1, "BloomVal");
		this.PixelOffset = ARBShaderObjects.glGetUniformLocationARB(int1, "PixelOffset");
		this.PixelSize = ARBShaderObjects.glGetUniformLocationARB(int1, "PixelSize");
		this.BlurStrength = ARBShaderObjects.glGetUniformLocationARB(int1, "BlurStrength");
		this.width = ARBShaderObjects.glGetUniformLocationARB(int1, "bgl_RenderedTextureWidth");
		this.height = ARBShaderObjects.glGetUniformLocationARB(int1, "bgl_RenderedTextureHeight");
		this.timer = ARBShaderObjects.glGetUniformLocationARB(int1, "timer");
		this.TextureSize = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureSize");
		this.Zoom = ARBShaderObjects.glGetUniformLocationARB(int1, "Zoom");
		this.Light = ARBShaderObjects.glGetUniformLocationARB(int1, "Light");
		this.LightIntensity = ARBShaderObjects.glGetUniformLocationARB(int1, "LightIntensity");
		this.NightValue = ARBShaderObjects.glGetUniformLocationARB(int1, "NightValue");
		this.Exterior = ARBShaderObjects.glGetUniformLocationARB(int1, "Exterior");
		this.NightVisionGoggles = ARBShaderObjects.glGetUniformLocationARB(int1, "NightVisionGoggles");
		this.DesaturationVal = ARBShaderObjects.glGetUniformLocationARB(int1, "DesaturationVal");
		this.FogMod = ARBShaderObjects.glGetUniformLocationARB(int1, "FogMod");
		this.SearchModeID = ARBShaderObjects.glGetUniformLocationARB(int1, "SearchMode");
		this.ScreenInfo = ARBShaderObjects.glGetUniformLocationARB(int1, "ScreenInfo");
		this.ParamInfo = ARBShaderObjects.glGetUniformLocationARB(int1, "ParamInfo");
		this.VarInfo = ARBShaderObjects.glGetUniformLocationARB(int1, "VarInfo");
	}

	public void postRender(TextureDraw textureDraw) {
		if (textureDraw.vars != null) {
			returnFloatArray(textureDraw.vars);
			textureDraw.vars = null;
		}
	}

	private static float[] getFreeFloatArray() {
		for (int int1 = 0; int1 < floatArrs.length; ++int1) {
			if (floatArrs[int1] != null) {
				float[] floatArray = floatArrs[int1];
				floatArrs[int1] = null;
				return floatArray;
			}
		}

		return new float[22];
	}

	private static void returnFloatArray(float[] floatArray) {
		for (int int1 = 0; int1 < floatArrs.length; ++int1) {
			if (floatArrs[int1] == null) {
				floatArrs[int1] = floatArray;
				break;
			}
		}
	}
}
