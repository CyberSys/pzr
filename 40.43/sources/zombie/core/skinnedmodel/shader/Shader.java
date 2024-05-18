package zombie.core.skinnedmodel.shader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.IndieFileLoader;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.textures.Texture;


public class Shader {
	public String name;
	private int TransformMatrixID = 0;
	public int ShaderID = 0;
	public int FragID = 0;
	public int VertID = 0;
	int MatrixID = 0;
	int Light0Direction;
	int Light0Colour;
	int Light1Direction;
	int Light1Colour;
	int Light2Direction;
	int Light2Colour;
	int Ambient;
	int TintColour;
	int Texture0;
	int TexturePainColor;
	int TextureRust;
	int TextureRustA;
	int TextureMask;
	int TextureLights;
	int TextureDamage1Overlay;
	int TextureDamage1Shell;
	int TextureDamage2Overlay;
	int TextureDamage2Shell;
	int TextureUninstall1;
	int TextureUninstall2;
	int TextureLightsEnables2;
	int TextureDamage1Enables1;
	int TextureDamage1Enables2;
	int TextureDamage2Enables1;
	int TextureDamage2Enables2;
	int Alpha;
	int TextureReflectionA;
	int TextureReflectionB;
	int ReflectionParam;
	public int BoneIndicesAttrib;
	public int BoneWeightsAttrib;
	Texture tex;
	boolean bStatic = false;
	static FloatBuffer floatBuffer;
	private static Vector3f tempVec3f = new Vector3f();
	private FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);

	public Shader(String string, boolean boolean1) {
		this.ShaderID = ARBShaderObjects.glCreateProgramObjectARB();
		this.name = string;
		this.bStatic = boolean1;
		boolean boolean2;
		if (this.ShaderID != 0) {
			this.FragID = this.createFragShader("media/shaders/" + string + ".frag");
			if (!boolean1) {
				this.VertID = this.createVertShader("media/shaders/" + string + ".vert");
			} else {
				this.VertID = this.createVertShader("media/shaders/" + string + "_static.vert");
			}

			if (this.VertID != 0 && this.FragID != 0) {
				ARBShaderObjects.glAttachObjectARB(this.ShaderID, this.VertID);
				ARBShaderObjects.glAttachObjectARB(this.ShaderID, this.FragID);
				ARBShaderObjects.glLinkProgramARB(this.ShaderID);
				if (ARBShaderObjects.glGetObjectParameteriARB(this.ShaderID, 35714) == 0) {
					System.err.println(getLogInfo(this.ShaderID));
					this.VertID = 0;
					this.ShaderID = 0;
					this.FragID = 0;
					return;
				}

				ARBShaderObjects.glValidateProgramARB(this.ShaderID);
				if (ARBShaderObjects.glGetObjectParameteriARB(this.ShaderID, 35715) == 0) {
					System.err.println(getLogInfo(this.ShaderID));
					this.VertID = 0;
					this.ShaderID = 0;
					this.FragID = 0;
					return;
				}

				this.Start();
				if (!boolean1) {
					this.MatrixID = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "MatrixPalette");
				} else {
					this.TransformMatrixID = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "transform");
				}

				this.Light0Colour = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light0Colour");
				this.Light0Direction = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light0Direction");
				this.Light1Colour = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light1Colour");
				this.Light1Direction = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light1Direction");
				this.Light2Colour = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light2Colour");
				this.Light2Direction = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light2Direction");
				this.Ambient = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "AmbientColour");
				this.TintColour = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TintColour");
				this.Texture0 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Texture0");
				this.TexturePainColor = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TexturePainColor");
				this.TextureRust = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureRust");
				this.TextureMask = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureMask");
				this.TextureLights = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureLights");
				this.TextureDamage1Overlay = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage1Overlay");
				this.TextureDamage1Shell = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage1Shell");
				this.TextureDamage2Overlay = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage2Overlay");
				this.TextureDamage2Shell = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage2Shell");
				this.TextureRustA = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureRustA");
				this.TextureUninstall1 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureUninstall1");
				this.TextureUninstall2 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureUninstall2");
				this.TextureLightsEnables2 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureLightsEnables2");
				this.TextureDamage1Enables1 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage1Enables1");
				this.TextureDamage1Enables2 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage1Enables2");
				this.TextureDamage2Enables1 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage2Enables1");
				this.TextureDamage2Enables2 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage2Enables2");
				this.Alpha = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Alpha");
				this.TextureReflectionA = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureReflectionA");
				this.TextureReflectionB = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureReflectionB");
				this.ReflectionParam = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "ReflectionParam");
				if (this.Texture0 != -1) {
					ARBShaderObjects.glUniform1iARB(this.Texture0, 0);
				}

				if (this.TextureRust != -1) {
					ARBShaderObjects.glUniform1iARB(this.TextureRust, 1);
				}

				if (this.TextureMask != -1) {
					ARBShaderObjects.glUniform1iARB(this.TextureMask, 2);
				}

				if (this.TextureLights != -1) {
					ARBShaderObjects.glUniform1iARB(this.TextureLights, 3);
				}

				if (this.TextureDamage1Overlay != -1) {
					ARBShaderObjects.glUniform1iARB(this.TextureDamage1Overlay, 4);
				}

				if (this.TextureDamage1Shell != -1) {
					ARBShaderObjects.glUniform1iARB(this.TextureDamage1Shell, 5);
				}

				if (this.TextureDamage2Overlay != -1) {
					ARBShaderObjects.glUniform1iARB(this.TextureDamage2Overlay, 6);
				}

				if (this.TextureDamage2Shell != -1) {
					ARBShaderObjects.glUniform1iARB(this.TextureDamage2Shell, 7);
				}

				if (this.TextureReflectionA != -1) {
					ARBShaderObjects.glUniform1iARB(this.TextureReflectionA, 8);
				}

				if (this.TextureReflectionB != -1) {
					ARBShaderObjects.glUniform1iARB(this.TextureReflectionB, 9);
				}

				this.BoneIndicesAttrib = GL20.glGetAttribLocation(this.ShaderID, "boneIndices");
				this.BoneWeightsAttrib = GL20.glGetAttribLocation(this.ShaderID, "boneWeights");
				this.End();
			} else {
				boolean2 = false;
			}
		} else {
			boolean2 = false;
		}
	}

	private static String getLogInfo(int int1) {
		return ARBShaderObjects.glGetInfoLogARB(int1, ARBShaderObjects.glGetObjectParameteriARB(int1, 35716));
	}

	public void setTexture(Texture texture) {
		this.tex = texture;
		if (texture != null) {
			texture.bind();
		}
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
				String string3;
				while ((string3 = bufferedReader.readLine()) != null) {
					if (string2 == null) {
						string2 = string3.trim() + System.getProperty("line.separator");
					} else {
						string2 = string2 + string3.trim() + System.getProperty("line.separator");
					}
				}
			} catch (Exception exception) {
				System.out.println("Fail reading vertex shading code");
				return 0;
			}

			while (string2.indexOf("#") != 0) {
				string2 = string2.substring(1);
			}

			ARBShaderObjects.glShaderSourceARB(int1, string2);
			ARBShaderObjects.glCompileShaderARB(int1);
			if (!printLogInfo(int1)) {
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
				String string3;
				while ((string3 = bufferedReader.readLine()) != null) {
					if (string2 == null) {
						string2 = string3.trim() + System.getProperty("line.separator");
					} else {
						string2 = string2 + string3.trim() + System.getProperty("line.separator");
					}
				}
			} catch (Exception exception) {
				System.out.println("Fail reading fragment shading code");
				return 0;
			}

			while (string2.indexOf("#") != 0) {
				string2 = string2.substring(1);
			}

			ARBShaderObjects.glShaderSourceARB(int1, string2);
			ARBShaderObjects.glCompileShaderARB(int1);
			if (!printLogInfo(int1)) {
				int1 = 0;
			}

			return int1;
		}
	}

	private static boolean printLogInfo(int int1) {
		IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
		ARBShaderObjects.glGetObjectParameterARB(int1, 35716, intBuffer);
		int int2 = intBuffer.get();
		if (int2 > 1) {
			ByteBuffer byteBuffer = BufferUtils.createByteBuffer(int2);
			intBuffer.flip();
			ARBShaderObjects.glGetInfoLogARB(int1, intBuffer, byteBuffer);
			byte[] byteArray = new byte[int2];
			byteBuffer.get(byteArray);
			String string = new String(byteArray);
			System.out.println("Info log:\n" + string);
			return true;
		} else {
			return true;
		}
	}

	public void updateParams() {
	}

	public void updateParamsSkin() {
	}

	public void setMatrixPalette(Matrix4f[] matrix4fArray) {
		if (!this.bStatic) {
			if (floatBuffer == null) {
				floatBuffer = BufferUtils.createFloatBuffer(matrix4fArray.length * 64);
			}

			floatBuffer.clear();
			for (int int1 = 0; int1 < matrix4fArray.length; ++int1) {
				matrix4fArray[int1].get(floatBuffer);
				floatBuffer.position(floatBuffer.position() + 16);
			}

			floatBuffer.flip();
			ARBShaderObjects.glUniformMatrix4ARB(this.MatrixID, true, floatBuffer);
		}
	}

	public void setTint(float float1, float float2, float float3) {
		ARBShaderObjects.glUniform3fARB(this.TintColour, float1, float2, float3);
	}

	public void setTextureRustA(float float1) {
		ARBShaderObjects.glUniform1fARB(this.TextureRustA, float1);
	}

	public void setTexturePainColor(float float1, float float2, float float3, float float4) {
		ARBShaderObjects.glUniform4fARB(this.TexturePainColor, float1, float2, float3, float4);
	}

	public void setTexturePainColor(org.joml.Vector3f vector3f, float float1) {
		ARBShaderObjects.glUniform4fARB(this.TexturePainColor, vector3f.x(), vector3f.y(), vector3f.z(), float1);
	}

	public void setTexturePainColor(Vector4f vector4f) {
		ARBShaderObjects.glUniform4fARB(this.TexturePainColor, vector4f.x(), vector4f.y(), vector4f.z(), vector4f.w());
	}

	public void setReflectionParam(float float1, float float2, float float3) {
		ARBShaderObjects.glUniform3fARB(this.ReflectionParam, float1, float2, float3);
	}

	public void setTextureUninstall1(Matrix4f matrix4f) {
		this.setMatrix(this.TextureUninstall1, matrix4f);
	}

	public void setTextureUninstall2(Matrix4f matrix4f) {
		this.setMatrix(this.TextureUninstall2, matrix4f);
	}

	public void setTextureLightsEnables2(Matrix4f matrix4f) {
		this.setMatrix(this.TextureLightsEnables2, matrix4f);
	}

	public void setTextureDamage1Enables1(Matrix4f matrix4f) {
		this.setMatrix(this.TextureDamage1Enables1, matrix4f);
	}

	public void setTextureDamage1Enables2(Matrix4f matrix4f) {
		this.setMatrix(this.TextureDamage1Enables2, matrix4f);
	}

	public void setTextureDamage2Enables1(Matrix4f matrix4f) {
		this.setMatrix(this.TextureDamage2Enables1, matrix4f);
	}

	public void setTextureDamage2Enables2(Matrix4f matrix4f) {
		this.setMatrix(this.TextureDamage2Enables2, matrix4f);
	}

	public void setShaderAlpha(float float1) {
		ARBShaderObjects.glUniform1fARB(this.Alpha, float1);
	}

	public void setLight(int int1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, ModelInstance modelInstance) {
		int int2 = this.Light0Direction;
		int int3 = this.Light0Colour;
		if (int1 == 1) {
			int2 = this.Light1Direction;
			int3 = this.Light1Colour;
		}

		if (int1 == 2) {
			int2 = this.Light2Direction;
			int3 = this.Light2Colour;
		}

		Vector3f vector3f = tempVec3f;
		vector3f.set(float1 + 0.5F, float2 + 0.5F, float3 + 1.0F);
		vector3f.x -= modelInstance.object.x;
		vector3f.y -= modelInstance.object.y;
		vector3f.z -= modelInstance.object.z + 0.25F;
		float float8 = vector3f.length();
		float float9 = vector3f.y;
		vector3f.y = vector3f.z;
		vector3f.z = float9;
		if (vector3f.length() < 1.0E-4F) {
			vector3f.set(0.0F, 1.0F, 0.0F);
		}

		vector3f.normalise();
		float float10 = 1.0F - float8 / float7;
		if (float10 < 0.0F) {
			float10 = 0.0F;
		}

		if (float10 > 1.0F) {
			float10 = 1.0F;
		}

		float4 *= float10 * 0.55F;
		float5 *= float10 * 0.55F;
		float6 *= float10 * 0.55F;
		if (modelInstance.character == null) {
			this.doVector3(int2, vector3f.x, vector3f.y, vector3f.z, (FloatBuffer)null);
		} else {
			this.doVector3(int2, -vector3f.x, vector3f.y, vector3f.z, (FloatBuffer)null);
		}

		this.doVector3(int3, float4, float5, float6, (FloatBuffer)null);
	}

	private void doVector3(int int1, float float1, float float2, float float3, FloatBuffer floatBuffer) {
		ARBShaderObjects.glUniform3fARB(int1, float1, float2, float3);
	}

	public void setAmbient(float float1) {
		ARBShaderObjects.glUniform3fARB(this.Ambient, float1, float1, float1);
	}

	public void setTransformMatrix(Matrix4f matrix4f) {
		this.floatBuffer2.clear();
		matrix4f.get(this.floatBuffer2);
		this.floatBuffer2.position(16);
		this.floatBuffer2.flip();
		ARBShaderObjects.glUniformMatrix4ARB(this.TransformMatrixID, true, this.floatBuffer2);
	}

	public void setMatrix(int int1, Matrix4f matrix4f) {
		this.floatBuffer2.clear();
		matrix4f.get(this.floatBuffer2);
		this.floatBuffer2.position(16);
		this.floatBuffer2.flip();
		ARBShaderObjects.glUniformMatrix4ARB(int1, true, this.floatBuffer2);
	}
}
