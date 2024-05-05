package zombie.iso;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.TextureDraw;


public final class SmokeShader extends Shader {
	private int mvpMatrix;
	private int FireTime;
	private int FireParam;
	private int FireTexture;

	public SmokeShader(String string) {
		super(string);
	}

	protected void onCompileSuccess(ShaderProgram shaderProgram) {
		int int1 = shaderProgram.getShaderID();
		this.FireTexture = ARBShaderObjects.glGetUniformLocationARB(int1, "FireTexture");
		this.mvpMatrix = ARBShaderObjects.glGetUniformLocationARB(int1, "mvpMatrix");
		this.FireTime = ARBShaderObjects.glGetUniformLocationARB(int1, "FireTime");
		this.FireParam = ARBShaderObjects.glGetUniformLocationARB(int1, "FireParam");
		this.Start();
		if (this.FireTexture != -1) {
			ARBShaderObjects.glUniform1iARB(this.FireTexture, 0);
		}

		this.End();
	}

	public void updateSmokeParams(TextureDraw textureDraw, int int1, float float1) {
		ParticlesFire particlesFire = ParticlesFire.getInstance();
		GL13.glActiveTexture(33984);
		particlesFire.getFireSmokeTexture().bind();
		GL11.glTexEnvi(8960, 8704, 7681);
		ARBShaderObjects.glUniformMatrix4fvARB(this.mvpMatrix, true, particlesFire.getMVPMatrix());
		ARBShaderObjects.glUniform1fARB(this.FireTime, float1);
		ARBShaderObjects.glUniformMatrix3fvARB(this.FireParam, true, particlesFire.getParametersFire());
		if (this.FireTexture != -1) {
			ARBShaderObjects.glUniform1iARB(this.FireTexture, 0);
		}
	}
}
