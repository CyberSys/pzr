package zombie.core.skinnedmodel.model;

import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjglx.BufferUtils;
import zombie.GameProfiler;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderThread;
import zombie.core.particle.MuzzleFlash;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.shader.ShaderManager;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.iso.IsoLightSource;
import zombie.iso.Vector3;
import zombie.iso.sprite.SkyBox;
import zombie.scripting.objects.ModelAttachment;
import zombie.util.Lambda;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;


public final class Model extends Asset {
	public String Name;
	public final ModelMesh Mesh;
	public Shader Effect;
	public Object Tag;
	public boolean bStatic = false;
	public Texture tex = null;
	public SoftwareModelMesh softwareMesh;
	public static final FloatBuffer m_staticReusableFloatBuffer = BufferUtils.createFloatBuffer(128);
	private static final Matrix4f IDENTITY = new Matrix4f();
	public static final Color[] debugDrawColours = new Color[]{new Color(230, 25, 75), new Color(60, 180, 75), new Color(255, 225, 25), new Color(0, 130, 200), new Color(245, 130, 48), new Color(145, 30, 180), new Color(70, 240, 240), new Color(240, 50, 230), new Color(210, 245, 60), new Color(250, 190, 190), new Color(0, 128, 128), new Color(230, 190, 255), new Color(170, 110, 40), new Color(255, 250, 200), new Color(128, 0, 0), new Color(170, 255, 195), new Color(128, 128, 0), new Color(255, 215, 180), new Color(0, 0, 128), new Color(128, 128, 128), new Color(255, 255, 255), new Color(0, 0, 0)};
	public Model.ModelAssetParams assetParams;
	static Vector3 tempo = new Vector3();
	public static final AssetType ASSET_TYPE = new AssetType("Model");

	public Model(AssetPath assetPath, AssetManager assetManager, Model.ModelAssetParams modelAssetParams) {
		super(assetPath, assetManager);
		this.assetParams = modelAssetParams;
		this.bStatic = this.assetParams != null && this.assetParams.bStatic;
		ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
		meshAssetParams.bStatic = this.bStatic;
		meshAssetParams.animationsMesh = this.assetParams == null ? null : this.assetParams.animationsModel;
		this.Mesh = (ModelMesh)MeshAssetManager.instance.load(new AssetPath(modelAssetParams.meshName), meshAssetParams);
		if (!StringUtils.isNullOrWhitespace(modelAssetParams.textureName)) {
			if (modelAssetParams.textureName.contains("media/")) {
				this.tex = Texture.getSharedTexture(modelAssetParams.textureName, modelAssetParams.textureFlags);
			} else {
				this.tex = Texture.getSharedTexture("media/textures/" + modelAssetParams.textureName + ".png", modelAssetParams.textureFlags);
			}
		}

		if (!StringUtils.isNullOrWhitespace(modelAssetParams.shaderName)) {
			this.CreateShader(modelAssetParams.shaderName);
		}

		this.onCreated(this.Mesh.getState());
		this.addDependency(this.Mesh);
		if (this.isReady()) {
			this.Tag = this.Mesh.skinningData;
			this.softwareMesh = this.Mesh.softwareMesh;
			this.assetParams = null;
		}
	}

	public static void VectorToWorldCoords(IsoGameCharacter gameCharacter, Vector3 vector3) {
		AnimationPlayer animationPlayer = gameCharacter.getAnimationPlayer();
		float float1 = animationPlayer.getRenderedAngle();
		vector3.x = -vector3.x;
		vector3.rotatey(float1);
		float float2 = vector3.y;
		vector3.y = vector3.z;
		vector3.z = float2 * 0.6F;
		vector3.x *= 1.5F;
		vector3.y *= 1.5F;
		vector3.x += gameCharacter.x;
		vector3.y += gameCharacter.y;
		vector3.z += gameCharacter.z;
	}

	public static void BoneToWorldCoords(IsoGameCharacter gameCharacter, int int1, Vector3 vector3) {
		AnimationPlayer animationPlayer = gameCharacter.getAnimationPlayer();
		vector3.x = animationPlayer.modelTransforms[int1].m03;
		vector3.y = animationPlayer.modelTransforms[int1].m13;
		vector3.z = animationPlayer.modelTransforms[int1].m23;
		VectorToWorldCoords(gameCharacter, vector3);
	}

	public static void BoneYDirectionToWorldCoords(IsoGameCharacter gameCharacter, int int1, Vector3 vector3, float float1) {
		AnimationPlayer animationPlayer = gameCharacter.getAnimationPlayer();
		vector3.x = animationPlayer.modelTransforms[int1].m01 * float1;
		vector3.y = animationPlayer.modelTransforms[int1].m11 * float1;
		vector3.z = animationPlayer.modelTransforms[int1].m21 * float1;
		vector3.x += animationPlayer.modelTransforms[int1].m03;
		vector3.y += animationPlayer.modelTransforms[int1].m13;
		vector3.z += animationPlayer.modelTransforms[int1].m23;
		VectorToWorldCoords(gameCharacter, vector3);
	}

	public static void VectorToWorldCoords(ModelSlotRenderData modelSlotRenderData, Vector3 vector3) {
		float float1 = modelSlotRenderData.animPlayerAngle;
		vector3.x = -vector3.x;
		vector3.rotatey(float1);
		float float2 = vector3.y;
		vector3.y = vector3.z;
		vector3.z = float2 * 0.6F;
		vector3.x *= 1.5F;
		vector3.y *= 1.5F;
		vector3.x += modelSlotRenderData.x;
		vector3.y += modelSlotRenderData.y;
		vector3.z += modelSlotRenderData.z;
	}

	public static void BoneToWorldCoords(ModelSlotRenderData modelSlotRenderData, int int1, Vector3 vector3) {
		AnimationPlayer animationPlayer = modelSlotRenderData.animPlayer;
		vector3.x = animationPlayer.modelTransforms[int1].m03;
		vector3.y = animationPlayer.modelTransforms[int1].m13;
		vector3.z = animationPlayer.modelTransforms[int1].m23;
		VectorToWorldCoords(modelSlotRenderData, vector3);
	}

	public static void CharacterModelCameraBegin(ModelSlotRenderData modelSlotRenderData) {
		ModelCamera.instance.Begin();
		if (modelSlotRenderData.bInVehicle) {
			GL11.glMatrixMode(5888);
			GL11.glTranslatef(0.0F, modelSlotRenderData.centerOfMassY, 0.0F);
			GL11.glMatrixMode(5888);
			GL11.glRotatef(modelSlotRenderData.vehicleAngleZ, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(modelSlotRenderData.vehicleAngleY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(modelSlotRenderData.vehicleAngleX, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			byte byte1 = -1;
			GL11.glTranslatef(modelSlotRenderData.inVehicleX, modelSlotRenderData.inVehicleY, modelSlotRenderData.inVehicleZ * (float)byte1);
			GL11.glScalef(1.5F, 1.5F, 1.5F);
		}
	}

	public static void CharacterModelCameraEnd() {
		ModelCamera.instance.End();
	}

	public void DrawChar(ModelSlotRenderData modelSlotRenderData, ModelInstanceRenderData modelInstanceRenderData) {
		if (!DebugOptions.instance.Character.Debug.Render.SkipCharacters.getValue()) {
			if (modelSlotRenderData.character == IsoPlayer.getInstance()) {
				boolean boolean1 = false;
			}

			if (!(modelSlotRenderData.alpha < 0.01F)) {
				if (modelSlotRenderData.animPlayer != null) {
					if (Core.bDebug && GameKeyboard.isKeyDown(199)) {
						this.Effect = null;
					}

					if (this.Effect == null) {
						this.CreateShader("basicEffect");
					}

					Shader shader = this.Effect;
					GL11.glEnable(2884);
					GL11.glCullFace(1028);
					GL11.glEnable(2929);
					GL11.glEnable(3008);
					GL11.glDepthFunc(513);
					GL11.glAlphaFunc(516, 0.01F);
					GL11.glBlendFunc(770, 771);
					if (Core.bDebug && DebugOptions.instance.ModelRenderWireframe.getValue()) {
						GL11.glPolygonMode(1032, 6913);
						GL11.glEnable(2848);
						GL11.glLineWidth(0.75F);
						Shader shader2 = ShaderManager.instance.getOrCreateShader("vehicle_wireframe", this.bStatic);
						if (shader2 != null) {
							shader2.Start();
							if (this.bStatic) {
								shader2.setTransformMatrix(modelInstanceRenderData.xfrm, true);
							} else {
								shader2.setMatrixPalette(modelInstanceRenderData.matrixPalette, true);
							}

							this.Mesh.Draw(shader2);
							shader2.End();
						}

						GL11.glPolygonMode(1032, 6914);
						GL11.glDisable(2848);
					} else {
						if (shader != null) {
							shader.Start();
							shader.startCharacter(modelSlotRenderData, modelInstanceRenderData);
						}

						if (!DebugOptions.instance.DebugDraw_SkipDrawNonSkinnedModel.getValue()) {
							GameProfiler.getInstance().invokeAndMeasure("Mesh.Draw.Call", shader, this.Mesh, (var0,modelSlotRenderDatax)->{
								modelSlotRenderDatax.Draw(var0);
							});
						}

						if (shader != null) {
							shader.End();
						}

						this.drawMuzzleFlash(modelInstanceRenderData);
					}
				}
			}
		}
	}

	private void drawMuzzleFlash(ModelInstanceRenderData modelInstanceRenderData) {
		if (modelInstanceRenderData.m_muzzleFlash) {
			ModelAttachment modelAttachment = modelInstanceRenderData.modelInstance.getAttachmentById("muzzle");
			if (modelAttachment != null) {
				BaseVehicle.Matrix4fObjectPool matrix4fObjectPool = (BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get();
				Matrix4f matrix4f = ((Matrix4f)matrix4fObjectPool.alloc()).set((Matrix4fc)modelInstanceRenderData.xfrm);
				matrix4f.transpose();
				Matrix4f matrix4f2 = modelInstanceRenderData.modelInstance.getAttachmentMatrix(modelAttachment, (Matrix4f)matrix4fObjectPool.alloc());
				matrix4f.mul((Matrix4fc)matrix4f2, matrix4f2);
				MuzzleFlash.render(matrix4f2);
				matrix4fObjectPool.release(matrix4f);
				matrix4fObjectPool.release(matrix4f2);
			}
		}
	}

	private void drawVehicleLights(ModelSlotRenderData modelSlotRenderData) {
		int int1;
		for (int1 = 7; int1 >= 0; --int1) {
			GL13.glActiveTexture('蓀' + int1);
			GL11.glDisable(3553);
		}

		GL11.glLineWidth(1.0F);
		GL11.glColor3f(1.0F, 1.0F, 0.0F);
		GL11.glDisable(2929);
		for (int1 = 0; int1 < 3; ++int1) {
			ModelInstance.EffectLight effectLight = modelSlotRenderData.effectLights[int1];
			if (!((float)effectLight.radius <= 0.0F)) {
				float float1 = effectLight.x;
				float float2 = effectLight.y;
				float float3 = effectLight.z;
				float float4 = float3;
				float3 = float2;
				float1 *= -54.0F;
				float2 = float4 * 54.0F;
				float3 *= 54.0F;
				GL11.glBegin(1);
				GL11.glVertex3f(float1, float2, float3);
				GL11.glVertex3f(0.0F, 0.0F, 0.0F);
				GL11.glEnd();
			}
		}

		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glEnable(3553);
		GL11.glEnable(2929);
	}

	public static void drawBoneMtx(org.lwjgl.util.vector.Matrix4f matrix4f) {
		GL11.glDisable(2929);
		GL11.glDisable(3553);
		GL11.glBegin(1);
		drawBoneMtxInternal(matrix4f);
		GL11.glEnd();
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glEnable(2929);
	}

	private static void drawBoneMtxInternal(org.lwjgl.util.vector.Matrix4f matrix4f) {
		float float1 = 0.5F;
		float float2 = 0.15F;
		float float3 = 0.1F;
		float float4 = matrix4f.m03;
		float float5 = matrix4f.m13;
		float float6 = matrix4f.m23;
		float float7 = matrix4f.m00;
		float float8 = matrix4f.m10;
		float float9 = matrix4f.m20;
		float float10 = matrix4f.m01;
		float float11 = matrix4f.m11;
		float float12 = matrix4f.m21;
		float float13 = matrix4f.m02;
		float float14 = matrix4f.m12;
		float float15 = matrix4f.m22;
		drawArrowInternal(float4, float5, float6, float7, float8, float9, float13, float14, float15, float1, float2, float3, 1.0F, 0.0F, 0.0F);
		drawArrowInternal(float4, float5, float6, float10, float11, float12, float13, float14, float15, float1, float2, float3, 0.0F, 1.0F, 0.0F);
		drawArrowInternal(float4, float5, float6, float13, float14, float15, float7, float8, float9, float1, float2, float3, 0.0F, 0.0F, 1.0F);
	}

	private static void drawArrowInternal(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15) {
		float float16 = 1.0F - float11;
		GL11.glColor3f(float13, float14, float15);
		GL11.glVertex3f(float1, float2, float3);
		GL11.glVertex3f(float1 + float4 * float10, float2 + float5 * float10, float3 + float6 * float10);
		GL11.glVertex3f(float1 + float4 * float10, float2 + float5 * float10, float3 + float6 * float10);
		GL11.glVertex3f(float1 + (float4 * float16 + float7 * float12) * float10, float2 + (float5 * float16 + float8 * float12) * float10, float3 + (float6 * float16 + float9 * float12) * float10);
		GL11.glVertex3f(float1 + float4 * float10, float2 + float5 * float10, float3 + float6 * float10);
		GL11.glVertex3f(float1 + (float4 * float16 - float7 * float12) * float10, float2 + (float5 * float16 - float8 * float12) * float10, float3 + (float6 * float16 - float9 * float12) * float10);
	}

	public void debugDrawLightSource(IsoLightSource lightSource, float float1, float float2, float float3, float float4) {
		debugDrawLightSource((float)lightSource.x, (float)lightSource.y, (float)lightSource.z, float1, float2, float3, float4);
	}

	public static void debugDrawLightSource(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		float float8 = float1 - float4 + 0.5F;
		float float9 = float2 - float5 + 0.5F;
		float float10 = float3 - float6 + 0.0F;
		float8 *= 0.67F;
		float9 *= 0.67F;
		float float11 = float8;
		float8 = (float)((double)float8 * Math.cos((double)float7) - (double)float9 * Math.sin((double)float7));
		float9 = (float)((double)float11 * Math.sin((double)float7) + (double)float9 * Math.cos((double)float7));
		float8 *= -1.0F;
		GL11.glDisable(3553);
		GL11.glDisable(2929);
		GL11.glBegin(1);
		GL11.glColor3f(1.0F, 1.0F, 0.0F);
		GL11.glVertex3f(float8, float10, float9);
		GL11.glVertex3f(0.0F, 0.0F, 0.0F);
		GL11.glVertex3f(float8, float10, float9);
		GL11.glVertex3f(float8 + 0.1F, float10, float9);
		GL11.glVertex3f(float8, float10, float9);
		GL11.glVertex3f(float8, float10 + 0.1F, float9);
		GL11.glVertex3f(float8, float10, float9);
		GL11.glVertex3f(float8, float10, float9 + 0.1F);
		GL11.glEnd();
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glEnable(3553);
		GL11.glEnable(2929);
	}

	public void DrawVehicle(ModelSlotRenderData modelSlotRenderData, ModelInstanceRenderData modelInstanceRenderData) {
		if (!DebugOptions.instance.ModelRenderSkipVehicles.getValue()) {
			ModelInstance modelInstance = modelInstanceRenderData.modelInstance;
			float float1 = modelSlotRenderData.ambientR;
			Texture texture = modelInstanceRenderData.tex;
			float float2 = modelInstanceRenderData.tintR;
			float float3 = modelInstanceRenderData.tintG;
			float float4 = modelInstanceRenderData.tintB;
			PZGLUtil.checkGLErrorThrow("Model.drawVehicle Enter inst: %s, instTex: %s, slotData: %s", modelInstance, texture, modelSlotRenderData);
			GL11.glEnable(2884);
			GL11.glCullFace(modelInstance.m_modelScript != null && modelInstance.m_modelScript.invertX ? 1029 : 1028);
			GL11.glEnable(2929);
			GL11.glDepthFunc(513);
			ModelCamera.instance.Begin();
			GL11.glMatrixMode(5888);
			GL11.glTranslatef(0.0F, modelSlotRenderData.centerOfMassY, 0.0F);
			Shader shader = this.Effect;
			PZGLUtil.pushAndMultMatrix(5888, modelInstanceRenderData.xfrm);
			if (Core.bDebug && DebugOptions.instance.ModelRenderWireframe.getValue()) {
				GL11.glPolygonMode(1032, 6913);
				GL11.glEnable(2848);
				GL11.glLineWidth(0.75F);
				shader = ShaderManager.instance.getOrCreateShader("vehicle_wireframe", this.bStatic);
				if (shader != null) {
					shader.Start();
					if (this.bStatic) {
						shader.setTransformMatrix(IDENTITY, false);
					} else {
						shader.setMatrixPalette(modelInstanceRenderData.matrixPalette, true);
					}

					this.Mesh.Draw(shader);
					shader.End();
				}

				GL11.glDisable(2848);
				PZGLUtil.popMatrix(5888);
				ModelCamera.instance.End();
			} else {
				if (shader != null) {
					shader.Start();
					this.setLights(modelSlotRenderData, 3);
					if (shader.isVehicleShader()) {
						VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)Type.tryCastTo(modelInstance, VehicleModelInstance.class);
						if (modelInstance instanceof VehicleSubModelInstance) {
							vehicleModelInstance = (VehicleModelInstance)Type.tryCastTo(modelInstance.parent, VehicleModelInstance.class);
						}

						shader.setTexture(vehicleModelInstance.tex, "Texture0", 0);
						GL11.glTexEnvi(8960, 8704, 7681);
						shader.setTexture(vehicleModelInstance.textureRust, "TextureRust", 1);
						GL11.glTexEnvi(8960, 8704, 7681);
						shader.setTexture(vehicleModelInstance.textureMask, "TextureMask", 2);
						GL11.glTexEnvi(8960, 8704, 7681);
						shader.setTexture(vehicleModelInstance.textureLights, "TextureLights", 3);
						GL11.glTexEnvi(8960, 8704, 7681);
						shader.setTexture(vehicleModelInstance.textureDamage1Overlay, "TextureDamage1Overlay", 4);
						GL11.glTexEnvi(8960, 8704, 7681);
						shader.setTexture(vehicleModelInstance.textureDamage1Shell, "TextureDamage1Shell", 5);
						GL11.glTexEnvi(8960, 8704, 7681);
						shader.setTexture(vehicleModelInstance.textureDamage2Overlay, "TextureDamage2Overlay", 6);
						GL11.glTexEnvi(8960, 8704, 7681);
						shader.setTexture(vehicleModelInstance.textureDamage2Shell, "TextureDamage2Shell", 7);
						GL11.glTexEnvi(8960, 8704, 7681);
						try {
							if (Core.getInstance().getPerfReflectionsOnLoad()) {
								shader.setTexture((Texture)SkyBox.getInstance().getTextureCurrent(), "TextureReflectionA", 8);
								GL11.glTexEnvi(8960, 8704, 7681);
								GL11.glGetError();
							}
						} catch (Throwable throwable) {
						}

						try {
							if (Core.getInstance().getPerfReflectionsOnLoad()) {
								shader.setTexture((Texture)SkyBox.getInstance().getTexturePrev(), "TextureReflectionB", 9);
								GL11.glTexEnvi(8960, 8704, 7681);
								GL11.glGetError();
							}
						} catch (Throwable throwable2) {
						}

						shader.setReflectionParam(SkyBox.getInstance().getTextureShift(), vehicleModelInstance.refWindows, vehicleModelInstance.refBody);
						shader.setTextureUninstall1(vehicleModelInstance.textureUninstall1);
						shader.setTextureUninstall2(vehicleModelInstance.textureUninstall2);
						shader.setTextureLightsEnables1(vehicleModelInstance.textureLightsEnables1);
						shader.setTextureLightsEnables2(vehicleModelInstance.textureLightsEnables2);
						shader.setTextureDamage1Enables1(vehicleModelInstance.textureDamage1Enables1);
						shader.setTextureDamage1Enables2(vehicleModelInstance.textureDamage1Enables2);
						shader.setTextureDamage2Enables1(vehicleModelInstance.textureDamage2Enables1);
						shader.setTextureDamage2Enables2(vehicleModelInstance.textureDamage2Enables2);
						shader.setMatrixBlood1(vehicleModelInstance.matrixBlood1Enables1, vehicleModelInstance.matrixBlood1Enables2);
						shader.setMatrixBlood2(vehicleModelInstance.matrixBlood2Enables1, vehicleModelInstance.matrixBlood2Enables2);
						shader.setTextureRustA(vehicleModelInstance.textureRustA);
						shader.setTexturePainColor(vehicleModelInstance.painColor, modelSlotRenderData.alpha);
						if (this.bStatic) {
							shader.setTransformMatrix(IDENTITY, false);
						} else {
							shader.setMatrixPalette(modelInstanceRenderData.matrixPalette, true);
						}
					} else if (modelInstance instanceof VehicleSubModelInstance) {
						GL13.glActiveTexture(33984);
						shader.setTexture(texture, "Texture", 0);
						shader.setShaderAlpha(modelSlotRenderData.alpha);
						if (this.bStatic) {
							shader.setTransformMatrix(IDENTITY, false);
						}
					} else {
						GL13.glActiveTexture(33984);
						shader.setTexture(texture, "Texture", 0);
					}

					shader.setAmbient(float1);
					shader.setTint(float2, float3, float4);
					this.Mesh.Draw(shader);
					shader.End();
				}

				if (Core.bDebug && DebugOptions.instance.ModelRenderLights.getValue() && modelInstanceRenderData == modelSlotRenderData.modelData.get(0)) {
					this.drawVehicleLights(modelSlotRenderData);
				}

				PZGLUtil.popMatrix(5888);
				ModelCamera.instance.End();
				PZGLUtil.checkGLErrorThrow("Model.drawVehicle Exit inst: %s, instTex: %s, slotData: %s", modelInstance, texture, modelSlotRenderData);
			}
		}
	}

	public static void debugDrawAxis(float float1, float float2, float float3, float float4, float float5) {
		for (int int1 = 0; int1 < 8; ++int1) {
			GL13.glActiveTexture('蓀' + int1);
			GL11.glDisable(3553);
		}

		GL11.glDisable(2929);
		GL11.glLineWidth(float5);
		GL11.glBegin(1);
		GL11.glColor3f(1.0F, 0.0F, 0.0F);
		GL11.glVertex3f(float1, float2, float3);
		GL11.glVertex3f(float1 + float4, float2, float3);
		GL11.glColor3f(0.0F, 1.0F, 0.0F);
		GL11.glVertex3f(float1, float2, float3);
		GL11.glVertex3f(float1, float2 + float4, float3);
		GL11.glColor3f(0.0F, 0.0F, 1.0F);
		GL11.glVertex3f(float1, float2, float3);
		GL11.glVertex3f(float1, float2, float3 + float4);
		GL11.glEnd();
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glEnable(2929);
		GL13.glActiveTexture(33984);
		GL11.glEnable(3553);
	}

	private void setLights(ModelSlotRenderData modelSlotRenderData, int int1) {
		for (int int2 = 0; int2 < int1; ++int2) {
			ModelInstance.EffectLight effectLight = modelSlotRenderData.effectLights[int2];
			this.Effect.setLight(int2, effectLight.x, effectLight.y, effectLight.z, effectLight.r, effectLight.g, effectLight.b, (float)effectLight.radius, modelSlotRenderData.animPlayerAngle, modelSlotRenderData.x, modelSlotRenderData.y, modelSlotRenderData.z, modelSlotRenderData.object);
		}
	}

	public void CreateShader(String string) {
		if (!ModelManager.NoOpenGL) {
			Lambda.invoke(RenderThread::invokeOnRenderContext, this, string, (var0,stringx)->{
				var0.Effect = ShaderManager.instance.getOrCreateShader(stringx, var0.bStatic);
			});
		}
	}

	public AssetType getType() {
		return ASSET_TYPE;
	}

	protected void onBeforeReady() {
		super.onBeforeReady();
		this.Tag = this.Mesh.skinningData;
		this.softwareMesh = this.Mesh.softwareMesh;
		this.assetParams = null;
	}

	public static final class ModelAssetParams extends AssetManager.AssetParams {
		public String meshName;
		public String textureName;
		public int textureFlags;
		public String shaderName;
		public boolean bStatic = false;
		public ModelMesh animationsModel;
	}
}
