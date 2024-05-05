package zombie.core.skinnedmodel.shader;

import java.nio.FloatBuffer;
import org.joml.Math;
import org.joml.Vector4f;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjglx.BufferUtils;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.ShaderProgram;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.model.ModelInstanceRenderData;
import zombie.core.skinnedmodel.model.ModelSlotRenderData;
import zombie.core.textures.SmartTexture;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.iso.IsoMovingObject;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.vehicles.BaseVehicle;


public final class Shader {
	private int HueChange;
	private int LightingAmount;
	private int MirrorXID;
	private int TransformMatrixID = 0;
	final String name;
	private final ShaderProgram m_shaderProgram;
	private int MatrixID = 0;
	private int Light0Direction;
	private int Light0Colour;
	private int Light1Direction;
	private int Light1Colour;
	private int Light2Direction;
	private int Light2Colour;
	private int Light3Direction;
	private int Light3Colour;
	private int Light4Direction;
	private int Light4Colour;
	private int TintColour;
	private int Texture0;
	private int TexturePainColor;
	private int TextureRust;
	private int TextureRustA;
	private int TextureMask;
	private int TextureLights;
	private int TextureDamage1Overlay;
	private int TextureDamage1Shell;
	private int TextureDamage2Overlay;
	private int TextureDamage2Shell;
	private int TextureUninstall1;
	private int TextureUninstall2;
	private int TextureLightsEnables1;
	private int TextureLightsEnables2;
	private int TextureDamage1Enables1;
	private int TextureDamage1Enables2;
	private int TextureDamage2Enables1;
	private int TextureDamage2Enables2;
	private int MatBlood1Enables1;
	private int MatBlood1Enables2;
	private int MatBlood2Enables1;
	private int MatBlood2Enables2;
	private int Alpha;
	private int TextureReflectionA;
	private int TextureReflectionB;
	private int ReflectionParam;
	public int BoneIndicesAttrib;
	public int BoneWeightsAttrib;
	private int UVScale;
	final boolean bStatic;
	private static FloatBuffer floatBuffer;
	private static final int MAX_BONES = 64;
	private static final Vector3f tempVec3f = new Vector3f();
	private final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);

	public Shader(String string, boolean boolean1) {
		this.name = string;
		this.m_shaderProgram = ShaderProgram.createShaderProgram(string, boolean1, false);
		this.m_shaderProgram.addCompileListener(this::onProgramCompiled);
		this.bStatic = boolean1;
		this.compile();
	}

	public boolean isStatic() {
		return this.bStatic;
	}

	public ShaderProgram getShaderProgram() {
		return this.m_shaderProgram;
	}

	private void onProgramCompiled(ShaderProgram shaderProgram) {
		this.Start();
		int int1 = this.m_shaderProgram.getShaderID();
		if (!this.bStatic) {
			this.MatrixID = ARBShaderObjects.glGetUniformLocationARB(int1, "MatrixPalette");
		} else {
			this.TransformMatrixID = ARBShaderObjects.glGetUniformLocationARB(int1, "transform");
		}

		this.HueChange = ARBShaderObjects.glGetUniformLocationARB(int1, "HueChange");
		this.LightingAmount = ARBShaderObjects.glGetUniformLocationARB(int1, "LightingAmount");
		this.Light0Colour = ARBShaderObjects.glGetUniformLocationARB(int1, "Light0Colour");
		this.Light0Direction = ARBShaderObjects.glGetUniformLocationARB(int1, "Light0Direction");
		this.Light1Colour = ARBShaderObjects.glGetUniformLocationARB(int1, "Light1Colour");
		this.Light1Direction = ARBShaderObjects.glGetUniformLocationARB(int1, "Light1Direction");
		this.Light2Colour = ARBShaderObjects.glGetUniformLocationARB(int1, "Light2Colour");
		this.Light2Direction = ARBShaderObjects.glGetUniformLocationARB(int1, "Light2Direction");
		this.Light3Colour = ARBShaderObjects.glGetUniformLocationARB(int1, "Light3Colour");
		this.Light3Direction = ARBShaderObjects.glGetUniformLocationARB(int1, "Light3Direction");
		this.Light4Colour = ARBShaderObjects.glGetUniformLocationARB(int1, "Light4Colour");
		this.Light4Direction = ARBShaderObjects.glGetUniformLocationARB(int1, "Light4Direction");
		this.TintColour = ARBShaderObjects.glGetUniformLocationARB(int1, "TintColour");
		this.Texture0 = ARBShaderObjects.glGetUniformLocationARB(int1, "Texture0");
		this.TexturePainColor = ARBShaderObjects.glGetUniformLocationARB(int1, "TexturePainColor");
		this.TextureRust = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureRust");
		this.TextureMask = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureMask");
		this.TextureLights = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureLights");
		this.TextureDamage1Overlay = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureDamage1Overlay");
		this.TextureDamage1Shell = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureDamage1Shell");
		this.TextureDamage2Overlay = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureDamage2Overlay");
		this.TextureDamage2Shell = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureDamage2Shell");
		this.TextureRustA = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureRustA");
		this.TextureUninstall1 = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureUninstall1");
		this.TextureUninstall2 = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureUninstall2");
		this.TextureLightsEnables1 = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureLightsEnables1");
		this.TextureLightsEnables2 = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureLightsEnables2");
		this.TextureDamage1Enables1 = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureDamage1Enables1");
		this.TextureDamage1Enables2 = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureDamage1Enables2");
		this.TextureDamage2Enables1 = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureDamage2Enables1");
		this.TextureDamage2Enables2 = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureDamage2Enables2");
		this.MatBlood1Enables1 = ARBShaderObjects.glGetUniformLocationARB(int1, "MatBlood1Enables1");
		this.MatBlood1Enables2 = ARBShaderObjects.glGetUniformLocationARB(int1, "MatBlood1Enables2");
		this.MatBlood2Enables1 = ARBShaderObjects.glGetUniformLocationARB(int1, "MatBlood2Enables1");
		this.MatBlood2Enables2 = ARBShaderObjects.glGetUniformLocationARB(int1, "MatBlood2Enables2");
		this.Alpha = ARBShaderObjects.glGetUniformLocationARB(int1, "Alpha");
		this.TextureReflectionA = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureReflectionA");
		this.TextureReflectionB = ARBShaderObjects.glGetUniformLocationARB(int1, "TextureReflectionB");
		this.ReflectionParam = ARBShaderObjects.glGetUniformLocationARB(int1, "ReflectionParam");
		this.UVScale = ARBShaderObjects.glGetUniformLocationARB(int1, "UVScale");
		this.m_shaderProgram.setSamplerUnit("Texture", 0);
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

		this.MirrorXID = ARBShaderObjects.glGetUniformLocationARB(int1, "MirrorX");
		this.BoneIndicesAttrib = GL20.glGetAttribLocation(int1, "boneIndices");
		this.BoneWeightsAttrib = GL20.glGetAttribLocation(int1, "boneWeights");
		this.End();
	}

	private void compile() {
		this.m_shaderProgram.compile();
	}

	public void setTexture(Texture texture, String string, int int1) {
		this.m_shaderProgram.setValue(string, texture, int1);
	}

	private void setUVScale(float float1, float float2) {
		if (this.UVScale > 0) {
			this.m_shaderProgram.setVector2(this.UVScale, float1, float2);
		}
	}

	public int getID() {
		return this.m_shaderProgram.getShaderID();
	}

	public void Start() {
		this.m_shaderProgram.Start();
	}

	public void End() {
		this.m_shaderProgram.End();
	}

	public void startCharacter(ModelSlotRenderData modelSlotRenderData, ModelInstanceRenderData modelInstanceRenderData) {
		if (this.bStatic) {
			this.setTransformMatrix(modelInstanceRenderData.xfrm, true);
		} else {
			this.setMatrixPalette(modelInstanceRenderData.matrixPalette);
		}

		float float1 = modelSlotRenderData.ambientR * 0.45F;
		float float2 = modelSlotRenderData.ambientG * 0.45F;
		float float3 = modelSlotRenderData.ambientB * 0.45F;
		this.setLights(modelSlotRenderData, 5);
		Texture texture = modelInstanceRenderData.tex != null ? modelInstanceRenderData.tex : modelInstanceRenderData.model.tex;
		if (DebugOptions.instance.IsoSprite.CharacterMipmapColors.getValue()) {
			Texture texture2 = texture instanceof SmartTexture ? ((SmartTexture)texture).result : texture;
			if (texture2 != null && texture2.getTextureId() != null && texture2.getTextureId().hasMipMaps()) {
				texture = Texture.getEngineMipmapTexture();
			}
		}

		this.setTexture(texture, "Texture", 0);
		this.setDepthBias(modelInstanceRenderData.depthBias / 50.0F);
		this.setAmbient(float1, float2, float3);
		this.setLightingAmount(1.0F);
		this.setHueShift(modelInstanceRenderData.hue);
		this.setTint(modelInstanceRenderData.tintR, modelInstanceRenderData.tintG, modelInstanceRenderData.tintB);
		this.setAlpha(modelSlotRenderData.alpha);
	}

	private void setLights(ModelSlotRenderData modelSlotRenderData, int int1) {
		for (int int2 = 0; int2 < int1; ++int2) {
			ModelInstance.EffectLight effectLight = modelSlotRenderData.effectLights[int2];
			if (GameServer.bServer && ServerGUI.isCreated()) {
				effectLight.r = effectLight.g = effectLight.b = 1.0F;
			}

			this.setLight(int2, effectLight.x, effectLight.y, effectLight.z, effectLight.r, effectLight.g, effectLight.b, (float)effectLight.radius, modelSlotRenderData.animPlayerAngle, modelSlotRenderData.x, modelSlotRenderData.y, modelSlotRenderData.z, modelSlotRenderData.object);
		}
	}

	public void updateAlpha(IsoGameCharacter gameCharacter, int int1) {
		if (gameCharacter != null) {
			this.setAlpha(gameCharacter.getAlpha(int1));
		}
	}

	public void setAlpha(float float1) {
		ARBShaderObjects.glUniform1fARB(this.Alpha, float1);
	}

	public void updateParams() {
	}

	public void setMatrixPalette(Matrix4f[] matrix4fArray) {
		if (!this.bStatic) {
			if (floatBuffer == null) {
				floatBuffer = BufferUtils.createFloatBuffer(1024);
			}

			floatBuffer.clear();
			Matrix4f[] matrix4fArray2 = matrix4fArray;
			int int1 = matrix4fArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				Matrix4f matrix4f = matrix4fArray2[int2];
				matrix4f.store(floatBuffer);
			}

			floatBuffer.flip();
			ARBShaderObjects.glUniformMatrix4fvARB(this.MatrixID, true, floatBuffer);
		}
	}

	public void setMatrixPalette(FloatBuffer floatBuffer) {
		this.setMatrixPalette(floatBuffer, true);
	}

	public void setMatrixPalette(FloatBuffer floatBuffer, boolean boolean1) {
		if (!this.bStatic) {
			ARBShaderObjects.glUniformMatrix4fvARB(this.MatrixID, boolean1, floatBuffer);
		}
	}

	public void setMatrixPalette(org.joml.Matrix4f[] matrix4fArray) {
		if (!this.bStatic) {
			if (floatBuffer == null) {
				floatBuffer = BufferUtils.createFloatBuffer(1024);
			}

			floatBuffer.clear();
			org.joml.Matrix4f[] matrix4fArray2 = matrix4fArray;
			int int1 = matrix4fArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				org.joml.Matrix4f matrix4f = matrix4fArray2[int2];
				matrix4f.get(floatBuffer);
				floatBuffer.position(floatBuffer.position() + 16);
			}

			floatBuffer.flip();
			ARBShaderObjects.glUniformMatrix4fvARB(this.MatrixID, true, floatBuffer);
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

	public void setTextureUninstall1(float[] floatArray) {
		this.setMatrix(this.TextureUninstall1, floatArray);
	}

	public void setTextureUninstall2(float[] floatArray) {
		this.setMatrix(this.TextureUninstall2, floatArray);
	}

	public void setTextureLightsEnables1(float[] floatArray) {
		this.setMatrix(this.TextureLightsEnables1, floatArray);
	}

	public void setTextureLightsEnables2(float[] floatArray) {
		this.setMatrix(this.TextureLightsEnables2, floatArray);
	}

	public void setTextureDamage1Enables1(float[] floatArray) {
		this.setMatrix(this.TextureDamage1Enables1, floatArray);
	}

	public void setTextureDamage1Enables2(float[] floatArray) {
		this.setMatrix(this.TextureDamage1Enables2, floatArray);
	}

	public void setTextureDamage2Enables1(float[] floatArray) {
		this.setMatrix(this.TextureDamage2Enables1, floatArray);
	}

	public void setTextureDamage2Enables2(float[] floatArray) {
		this.setMatrix(this.TextureDamage2Enables2, floatArray);
	}

	public void setMatrixBlood1(float[] floatArray, float[] floatArray2) {
		if (this.MatBlood1Enables1 != -1 && this.MatBlood1Enables2 != -1) {
			this.setMatrix(this.MatBlood1Enables1, floatArray);
			this.setMatrix(this.MatBlood1Enables2, floatArray2);
		}
	}

	public void setMatrixBlood2(float[] floatArray, float[] floatArray2) {
		if (this.MatBlood2Enables1 != -1 && this.MatBlood2Enables2 != -1) {
			this.setMatrix(this.MatBlood2Enables1, floatArray);
			this.setMatrix(this.MatBlood2Enables2, floatArray2);
		}
	}

	public void setShaderAlpha(float float1) {
		ARBShaderObjects.glUniform1fARB(this.Alpha, float1);
	}

	public void setLight(int int1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, ModelInstance modelInstance) {
		float float9 = 0.0F;
		float float10 = 0.0F;
		float float11 = 0.0F;
		IsoMovingObject movingObject = modelInstance.object;
		if (movingObject != null) {
			float9 = movingObject.x;
			float10 = movingObject.y;
			float11 = movingObject.z;
		}

		this.setLight(int1, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, movingObject);
	}

	public void setLight(int int1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, IsoMovingObject movingObject) {
		PZGLUtil.checkGLError(true);
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

		if (int1 == 3) {
			int2 = this.Light3Direction;
			int3 = this.Light3Colour;
		}

		if (int1 == 4) {
			int2 = this.Light4Direction;
			int3 = this.Light4Colour;
		}

		if (float4 + float5 + float6 != 0.0F && !(float7 <= 0.0F)) {
			Vector3f vector3f = tempVec3f;
			if (!Float.isNaN(float8)) {
				vector3f.set(float1, float2, float3);
				vector3f.x -= float9;
				vector3f.y -= float10;
				vector3f.z -= float11;
			} else {
				vector3f.set(float1, float2, float3);
			}

			float float12 = vector3f.length();
			if (float12 < 1.0E-4F) {
				vector3f.set(0.0F, 0.0F, 1.0F);
			} else {
				vector3f.normalise();
			}

			float float13;
			float float14;
			if (!Float.isNaN(float8)) {
				float13 = -float8;
				float14 = vector3f.x;
				float float15 = vector3f.y;
				vector3f.x = float14 * Math.cos(float13) - float15 * Math.sin(float13);
				vector3f.y = float14 * Math.sin(float13) + float15 * Math.cos(float13);
			}

			float13 = vector3f.y;
			vector3f.y = vector3f.z;
			vector3f.z = float13;
			if (vector3f.length() < 1.0E-4F) {
				vector3f.set(0.0F, 1.0F, 0.0F);
			}

			vector3f.normalise();
			float14 = 1.0F - float12 / float7;
			if (float14 < 0.0F) {
				float14 = 0.0F;
			}

			if (float14 > 1.0F) {
				float14 = 1.0F;
			}

			float4 *= float14;
			float5 *= float14;
			float6 *= float14;
			float4 = PZMath.clamp(float4, 0.0F, 1.0F);
			float5 = PZMath.clamp(float5, 0.0F, 1.0F);
			float6 = PZMath.clamp(float6, 0.0F, 1.0F);
			if (movingObject instanceof BaseVehicle) {
				this.doVector3(int2, -vector3f.x, vector3f.y, vector3f.z);
			} else {
				this.doVector3(int2, -vector3f.x, vector3f.y, vector3f.z);
			}

			if (movingObject instanceof IsoPlayer) {
				boolean boolean1 = false;
			}

			this.doVector3(int3, float4, float5, float6);
			PZGLUtil.checkGLErrorThrow("Shader.setLightInternal.");
		} else {
			this.doVector3(int2, 0.0F, 1.0F, 0.0F);
			this.doVector3(int3, 0.0F, 0.0F, 0.0F);
		}
	}

	private void doVector3(int int1, float float1, float float2, float float3) {
		this.m_shaderProgram.setVector3(int1, float1, float2, float3);
	}

	public void setHueShift(float float1) {
		if (this.HueChange > 0) {
			this.m_shaderProgram.setValue("HueChange", float1);
		}
	}

	public void setLightingAmount(float float1) {
		if (this.LightingAmount > 0) {
			this.m_shaderProgram.setValue("LightingAmount", float1);
		}
	}

	public void setDepthBias(float float1) {
		this.m_shaderProgram.setValue("DepthBias", float1 / 300.0F);
	}

	public void setAmbient(float float1) {
		this.m_shaderProgram.setVector3("AmbientColour", float1, float1, float1);
	}

	public void setAmbient(float float1, float float2, float float3) {
		this.m_shaderProgram.setVector3("AmbientColour", float1, float2, float3);
	}

	public void setTransformMatrix(Matrix4f matrix4f, boolean boolean1) {
		if (floatBuffer == null) {
			floatBuffer = BufferUtils.createFloatBuffer(1024);
		}

		floatBuffer.clear();
		matrix4f.store(floatBuffer);
		floatBuffer.flip();
		ARBShaderObjects.glUniformMatrix4fvARB(this.TransformMatrixID, boolean1, floatBuffer);
	}

	public void setTransformMatrix(org.joml.Matrix4f matrix4f, boolean boolean1) {
		this.floatBuffer2.clear();
		matrix4f.get(this.floatBuffer2);
		this.floatBuffer2.position(16);
		this.floatBuffer2.flip();
		ARBShaderObjects.glUniformMatrix4fvARB(this.TransformMatrixID, boolean1, this.floatBuffer2);
	}

	public void setMatrix(int int1, org.joml.Matrix4f matrix4f) {
		this.floatBuffer2.clear();
		matrix4f.get(this.floatBuffer2);
		this.floatBuffer2.position(16);
		this.floatBuffer2.flip();
		ARBShaderObjects.glUniformMatrix4fvARB(int1, true, this.floatBuffer2);
	}

	public void setMatrix(int int1, float[] floatArray) {
		this.floatBuffer2.clear();
		this.floatBuffer2.put(floatArray);
		this.floatBuffer2.flip();
		ARBShaderObjects.glUniformMatrix4fvARB(int1, true, this.floatBuffer2);
	}

	public boolean isVehicleShader() {
		return this.TextureRust != -1;
	}
}
