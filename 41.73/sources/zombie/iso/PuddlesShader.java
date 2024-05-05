package zombie.iso;

import org.joml.Vector4f;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.iso.sprite.SkyBox;


public final class PuddlesShader extends Shader {
	private int WaterGroundTex;
	private int PuddlesHM;
	private int WaterTextureReflectionA;
	private int WaterTextureReflectionB;
	private int WaterTime;
	private int WaterOffset;
	private int WaterViewport;
	private int WaterReflectionParam;
	private int PuddlesParams;

	public PuddlesShader(String string) {
		super(string);
	}

	protected void onCompileSuccess(ShaderProgram shaderProgram) {
		int int1 = shaderProgram.getShaderID();
		this.WaterGroundTex = ARBShaderObjects.glGetUniformLocationARB(int1, "WaterGroundTex");
		this.WaterTextureReflectionA = ARBShaderObjects.glGetUniformLocationARB(int1, "WaterTextureReflectionA");
		this.WaterTextureReflectionB = ARBShaderObjects.glGetUniformLocationARB(int1, "WaterTextureReflectionB");
		this.PuddlesHM = ARBShaderObjects.glGetUniformLocationARB(int1, "PuddlesHM");
		this.WaterTime = ARBShaderObjects.glGetUniformLocationARB(int1, "WTime");
		this.WaterOffset = ARBShaderObjects.glGetUniformLocationARB(int1, "WOffset");
		this.WaterViewport = ARBShaderObjects.glGetUniformLocationARB(int1, "WViewport");
		this.WaterReflectionParam = ARBShaderObjects.glGetUniformLocationARB(int1, "WReflectionParam");
		this.PuddlesParams = ARBShaderObjects.glGetUniformLocationARB(int1, "PuddlesParams");
		this.Start();
		if (this.WaterGroundTex != -1) {
			ARBShaderObjects.glUniform1iARB(this.WaterGroundTex, 0);
		}

		if (this.WaterTextureReflectionA != -1) {
			ARBShaderObjects.glUniform1iARB(this.WaterTextureReflectionA, 1);
		}

		if (this.WaterTextureReflectionB != -1) {
			ARBShaderObjects.glUniform1iARB(this.WaterTextureReflectionB, 2);
		}

		if (this.PuddlesHM != -1) {
			ARBShaderObjects.glUniform1iARB(this.PuddlesHM, 3);
		}

		this.End();
	}

	public void updatePuddlesParams(int int1, int int2) {
		IsoPuddles puddles = IsoPuddles.getInstance();
		SkyBox skyBox = SkyBox.getInstance();
		PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(int1);
		GL13.glActiveTexture(33985);
		skyBox.getTextureCurrent().bind();
		GL11.glTexParameteri(3553, 10240, 9729);
		GL11.glTexParameteri(3553, 10241, 9729);
		GL11.glTexEnvi(8960, 8704, 7681);
		GL13.glActiveTexture(33986);
		skyBox.getTexturePrev().bind();
		GL11.glTexParameteri(3553, 10240, 9729);
		GL11.glTexParameteri(3553, 10241, 9729);
		GL11.glTexEnvi(8960, 8704, 7681);
		GL13.glActiveTexture(33987);
		puddles.getHMTexture().bind();
		GL11.glTexParameteri(3553, 10240, 9729);
		GL11.glTexParameteri(3553, 10241, 9729);
		GL11.glTexEnvi(8960, 8704, 7681);
		ARBShaderObjects.glUniform1fARB(this.WaterTime, puddles.getShaderTime());
		Vector4f vector4f = puddles.getShaderOffset();
		ARBShaderObjects.glUniform4fARB(this.WaterOffset, vector4f.x - 90000.0F, vector4f.y - 640000.0F, vector4f.z, vector4f.w);
		ARBShaderObjects.glUniform4fARB(this.WaterViewport, (float)IsoCamera.getOffscreenLeft(int1), (float)IsoCamera.getOffscreenTop(int1), (float)playerCamera.OffscreenWidth / playerCamera.zoom, (float)playerCamera.OffscreenHeight / playerCamera.zoom);
		ARBShaderObjects.glUniform1fARB(this.WaterReflectionParam, skyBox.getTextureShift());
		ARBShaderObjects.glUniformMatrix4fvARB(this.PuddlesParams, true, puddles.getPuddlesParams(int2));
	}
}
