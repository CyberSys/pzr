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

	public Shader(String string) {
		this.ShaderID = ARBShaderObjects.glCreateProgramObjectARB();
		boolean boolean1;
		if (this.ShaderID != 0) {
			ShaderMap.put(this.ShaderID, this);
			this.FragID = this.createFragShader("media/shaders/" + string + ".frag");
			this.VertID = this.createVertShader("media/shaders/" + string + ".vert");
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
				boolean1 = false;
			}
		} else {
			boolean1 = false;
		}
	}

	private static String getLogInfo(int int1) {
		return ARBShaderObjects.glGetInfoLogARB(int1, ARBShaderObjects.glGetObjectParameteriARB(int1, 35716));
	}

	public void setTexture(Texture texture) {
		this.tex = texture;
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

	private int createVertShader(String string) {
		int int1 = ARBShaderObjects.glCreateShaderObjectARB(35633);
		if (int1 == 0) {
			return 0;
		} else {
			String string2 = null;
			try {
				InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader(string, false);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				while (true) {
					String string3;
					if ((string3 = bufferedReader.readLine()) == null) {
						inputStreamReader.close();
						break;
					}

					if (string2 == null) {
						string2 = string3.trim() + System.getProperty("line.separator");
					} else {
						string2 = string2 + string3.trim() + System.getProperty("line.separator");
					}
				}
			} catch (Exception exception) {
				DebugLog.log("Fail reading vertex shading code");
				return 0;
			}

			while (string2.indexOf("#") != 0) {
				string2 = string2.substring(1);
			}

			ARBShaderObjects.glShaderSourceARB(int1, string2);
			ARBShaderObjects.glCompileShaderARB(int1);
			if (!printLogInfo("vertex shader", int1)) {
				int1 = 0;
			}

			return int1;
		}
	}

	private int createFragShader(String string) {
		int int1 = ARBShaderObjects.glCreateShaderObjectARB(35632);
		if (int1 == 0) {
			return 0;
		} else {
			String string2 = null;
			try {
				InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader(string, false);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				while (true) {
					String string3;
					if ((string3 = bufferedReader.readLine()) == null) {
						inputStreamReader.close();
						break;
					}

					if (string2 == null) {
						string2 = string3.trim() + System.getProperty("line.separator");
					} else {
						string2 = string2 + string3.trim() + System.getProperty("line.separator");
					}
				}
			} catch (Exception exception) {
				DebugLog.log("Fail reading fragment shading code");
				return 0;
			}

			while (string2.indexOf("#") != 0) {
				string2 = string2.substring(1);
			}

			ARBShaderObjects.glShaderSourceARB(int1, string2);
			ARBShaderObjects.glCompileShaderARB(int1);
			if (!printLogInfo("fragment shader", int1)) {
				int1 = 0;
			}

			return int1;
		}
	}

	private static boolean printLogInfo(String string, int int1) {
		IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
		ARBShaderObjects.glGetObjectParameterARB(int1, 35716, intBuffer);
		int int2 = intBuffer.get();
		if (int2 > 1) {
			ByteBuffer byteBuffer = BufferUtils.createByteBuffer(int2);
			intBuffer.flip();
			ARBShaderObjects.glGetInfoLogARB(int1, intBuffer, byteBuffer);
			byte[] byteArray = new byte[int2];
			byteBuffer.get(byteArray);
			String string2 = new String(byteArray);
			DebugLog.log("Info log (" + string + "):\n" + string2 + "-----");
			return true;
		} else {
			return true;
		}
	}

	public void updateParams(TextureDraw textureDraw) {
		float float1 = textureDraw.f1;
		boolean boolean1 = textureDraw.flipped;
		int int1 = textureDraw.col[0];
		int int2 = textureDraw.col[1];
		int int3 = textureDraw.col[2];
		int int4 = textureDraw.col[3];
		float float2 = textureDraw.bSingleCol ? 1.0F : 0.0F;
		ARBShaderObjects.glUniform1fARB(this.timeOfDay, float1);
		ARBShaderObjects.glUniform1fARB(this.bloom, boolean1 ? 1.0F : 0.0F);
		ARBShaderObjects.glUniform1fARB(this.BlurStrength, 5.0F);
		ARBShaderObjects.glUniform1fARB(this.width, (float)int1);
		ARBShaderObjects.glUniform1fARB(this.height, (float)int2);
		ARBShaderObjects.glUniform3fARB(this.Light, textureDraw.vars[0], textureDraw.vars[1], textureDraw.vars[2]);
		ARBShaderObjects.glUniform1fARB(this.LightIntensity, textureDraw.vars[3]);
		ARBShaderObjects.glUniform1fARB(this.NightValue, float1);
		ARBShaderObjects.glUniform1fARB(this.DesaturationVal, textureDraw.vars[4]);
		ARBShaderObjects.glUniform1fARB(this.NightVisionGoggles, textureDraw.vars[5]);
		ARBShaderObjects.glUniform1fARB(this.FogMod, textureDraw.vars[6]);
		ARBShaderObjects.glUniform1fARB(this.Exterior, boolean1 ? 1.0F : 0.0F);
		ARBShaderObjects.glUniform1fARB(this.timer, (float)(this.timerVal / 2));
		if (PerformanceSettings.LockFPS >= 60) {
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
		ARBShaderObjects.glUniform2fARB(this.PixelOffset, float3, float4);
		ARBShaderObjects.glUniform2fARB(this.PixelSize, float5, float6);
		ARBShaderObjects.glUniform2fARB(this.TextureSize, (float)int3, (float)int4);
		ARBShaderObjects.glUniform1fARB(this.Zoom, float2);
		float[] floatArray = textureDraw.vars;
		textureDraw.vars = null;
		returnFloatArray(floatArray);
	}

	public void updateSkyBoxParams(TextureDraw textureDraw) {
		SkyBox skyBox = SkyBox.getInstance();
		ARBShaderObjects.glUniform1fARB(this.SkyBoxTime, (float)skyBox.getShaderTime());
		ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudCount, skyBox.getShaderCloudCount());
		ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudSize, skyBox.getShaderCloudSize());
		ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSunLight, skyBox.getShaderSunLight().x, skyBox.getShaderSunLight().y, skyBox.getShaderSunLight().z);
		ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSunColor, skyBox.getShaderSunColor().r, skyBox.getShaderSunColor().g, skyBox.getShaderSunColor().b);
		ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSkyHColour, skyBox.getShaderSkyHColour().r, skyBox.getShaderSkyHColour().g, skyBox.getShaderSkyHColour().b);
		ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSkyLColour, skyBox.getShaderSkyLColour().r, skyBox.getShaderSkyLColour().g, skyBox.getShaderSkyLColour().b);
		ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudLight, skyBox.getShaderCloudLight());
		ARBShaderObjects.glUniform1fARB(this.SkyBoxParamStars, skyBox.getShaderStars());
		ARBShaderObjects.glUniform1fARB(this.SkyBoxParamFog, skyBox.getShaderFog());
		ARBShaderObjects.glUniform3fARB(this.SkyBoxParamWind, skyBox.getShaderWind().x, skyBox.getShaderWind().y, skyBox.getShaderWind().z);
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
		for (int int1 = 0; int1 < floatArrs.length; ++int1) {
			if (floatArrs[int1] != null) {
				float[] floatArray = floatArrs[int1];
				floatArrs[int1] = null;
				return floatArray;
			}
		}

		return new float[7];
	}

	private static void returnFloatArray(float[] floatArray) {
		for (int int1 = 0; int1 < floatArrs.length; ++int1) {
			if (floatArrs[int1] == null) {
				floatArrs[int1] = floatArray;
				break;
			}
		}
	}

	public void startMainThread(TextureDraw textureDraw, int int1) {
		if (int1 >= 0 && int1 < 4) {
			IsoPlayer player = IsoPlayer.players[int1];
			boolean boolean1;
			if (player != null && player.getCurrentSquare() != null && player.getCurrentSquare().isOutside()) {
				boolean1 = true;
			} else {
				boolean1 = false;
			}

			float float1 = GameTime.instance.TimeOfDay / 12.0F - 1.0F;
			if (Math.abs(float1) > 0.8F && player != null && player.HasTrait("NightVision") && !player.isWearingNightVisionGoggles()) {
				float1 *= 0.8F;
			}

			int int2 = Core.getInstance().getOffscreenWidth(int1);
			int int3 = Core.getInstance().getOffscreenHeight(int1);
			if (textureDraw.vars == null) {
				textureDraw.vars = getFreeFloatArray();
				if (textureDraw.vars == null) {
					textureDraw.vars = new float[7];
				}
			}

			RenderSettings.PlayerRenderSettings playerRenderSettings = RenderSettings.getInstance().getPlayerSettings(int1);
			textureDraw.vars[0] = playerRenderSettings.getBlendColor().r;
			textureDraw.vars[1] = playerRenderSettings.getBlendColor().g;
			textureDraw.vars[2] = playerRenderSettings.getBlendColor().b;
			textureDraw.vars[3] = playerRenderSettings.getBlendIntensity();
			textureDraw.vars[4] = playerRenderSettings.getDesaturation();
			textureDraw.vars[5] = playerRenderSettings.isApplyNightVisionGoggles() ? 1.0F : 0.0F;
			textureDraw.vars[6] = playerRenderSettings.getFogMod();
			textureDraw.flipped = playerRenderSettings.isExterior();
			textureDraw.f1 = playerRenderSettings.getDarkness();
			textureDraw.col[0] = int2;
			textureDraw.col[1] = int3;
			textureDraw.col[2] = Core.getInstance().getOffscreenTrueWidth();
			textureDraw.col[3] = Core.getInstance().getOffscreenTrueHeight();
			textureDraw.bSingleCol = Core.getInstance().getZoom(int1) > 2.0F || (double)Core.getInstance().getZoom(int1) < 2.0 && Core.getInstance().getZoom(int1) >= 1.75F;
		}
	}
}
