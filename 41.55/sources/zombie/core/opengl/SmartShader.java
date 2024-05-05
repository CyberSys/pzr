package zombie.core.opengl;

import org.lwjgl.util.vector.Matrix4f;
import zombie.core.textures.Texture;
import zombie.iso.Vector2;
import zombie.iso.Vector3;


public final class SmartShader {
	private final ShaderProgram m_shaderProgram;

	public SmartShader(String string) {
		this.m_shaderProgram = ShaderProgram.createShaderProgram(string, false, true);
	}

	public SmartShader(String string, boolean boolean1) {
		this.m_shaderProgram = ShaderProgram.createShaderProgram(string, boolean1, true);
	}

	public void Start() {
		this.m_shaderProgram.Start();
	}

	public void End() {
		this.m_shaderProgram.End();
	}

	public void setValue(String string, float float1) {
		this.m_shaderProgram.setValue(string, float1);
	}

	public void setValue(String string, int int1) {
		this.m_shaderProgram.setValue(string, int1);
	}

	public void setValue(String string, Vector3 vector3) {
		this.m_shaderProgram.setValue(string, vector3);
	}

	public void setValue(String string, Vector2 vector2) {
		this.m_shaderProgram.setValue(string, vector2);
	}

	public void setVector2f(String string, float float1, float float2) {
		this.m_shaderProgram.setVector2(string, float1, float2);
	}

	public void setVector3f(String string, float float1, float float2, float float3) {
		this.m_shaderProgram.setVector3(string, float1, float2, float3);
	}

	public void setVector4f(String string, float float1, float float2, float float3, float float4) {
		this.m_shaderProgram.setVector4(string, float1, float2, float3, float4);
	}

	public void setValue(String string, Matrix4f matrix4f) {
		this.m_shaderProgram.setValue(string, matrix4f);
	}

	public void setValue(String string, Texture texture, int int1) {
		this.m_shaderProgram.setValue(string, texture, int1);
	}
}
