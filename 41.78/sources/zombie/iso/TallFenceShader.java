package zombie.iso;

import org.lwjgl.opengl.ARBShaderObjects;
import zombie.IndieGL;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.ShaderProgram;


public final class TallFenceShader {
	public static final TallFenceShader instance = new TallFenceShader();
	private ShaderProgram shaderProgram;
	private int u_alpha;
	private int u_outlineColor;
	private int u_stepSize;

	public void initShader() {
		this.shaderProgram = ShaderProgram.createShaderProgram("tallFence", false, true);
		if (this.shaderProgram.isCompiled()) {
			this.u_alpha = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "u_alpha");
			this.u_stepSize = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "u_stepSize");
			this.u_outlineColor = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "u_outlineColor");
			ARBShaderObjects.glUseProgramObjectARB(this.shaderProgram.getShaderID());
			ARBShaderObjects.glUniform2fARB(this.u_stepSize, 0.001F, 0.001F);
			ARBShaderObjects.glUseProgramObjectARB(0);
		}
	}

	public void setAlpha(float float1) {
		SpriteRenderer.instance.ShaderUpdate1f(this.shaderProgram.getShaderID(), this.u_alpha, float1);
	}

	public void setOutlineColor(float float1, float float2, float float3, float float4) {
		SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.u_outlineColor, float1, float2, float3, float4);
	}

	public void setStepSize(float float1, int int1, int int2) {
		SpriteRenderer.instance.ShaderUpdate2f(this.shaderProgram.getShaderID(), this.u_stepSize, float1 / (float)int1, float1 / (float)int2);
	}

	public boolean StartShader() {
		if (this.shaderProgram == null) {
			RenderThread.invokeOnRenderContext(this::initShader);
		}

		if (this.shaderProgram.isCompiled()) {
			IndieGL.StartShader(this.shaderProgram.getShaderID(), 0);
			return true;
		} else {
			return false;
		}
	}
}
