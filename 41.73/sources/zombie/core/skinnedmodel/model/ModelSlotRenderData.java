package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import zombie.GameProfiler;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderSettings;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;
import zombie.iso.IsoCamera;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.Vector3;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.popman.ObjectPool;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.VehicleScript;
import zombie.util.IPooledObject;
import zombie.util.Pool;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;


public final class ModelSlotRenderData extends TextureDraw.GenericDrawer {
	public IsoGameCharacter character;
	public IsoMovingObject object;
	private ModelManager.ModelSlot modelSlot;
	public final ModelInstanceRenderDataList modelData = new ModelInstanceRenderDataList();
	private final ModelInstanceRenderDataList readyModelData = new ModelInstanceRenderDataList();
	public ModelInstanceTextureCreator textureCreator;
	public AnimationPlayer animPlayer;
	public float animPlayerAngle;
	public float x;
	public float y;
	public float z;
	public float ambientR;
	public float ambientG;
	public float ambientB;
	public boolean bOutside;
	public final Matrix4f vehicleTransform = new Matrix4f();
	public boolean bInVehicle;
	public float inVehicleX;
	public float inVehicleY;
	public float inVehicleZ;
	public float vehicleAngleX;
	public float vehicleAngleY;
	public float vehicleAngleZ;
	public float alpha;
	private boolean bRendered;
	private boolean bReady;
	public final ModelInstance.EffectLight[] effectLights = new ModelInstance.EffectLight[5];
	public float centerOfMassY;
	public boolean RENDER_TO_TEXTURE;
	private static Shader solidColor;
	private static Shader solidColorStatic;
	private boolean bCharacterOutline = false;
	private final ColorInfo outlineColor = new ColorInfo(1.0F, 0.0F, 0.0F, 1.0F);
	private ModelSlotDebugRenderData m_debugRenderData;
	private static final ObjectPool pool = new ObjectPool(ModelSlotRenderData::new);

	public ModelSlotRenderData() {
		for (int int1 = 0; int1 < this.effectLights.length; ++int1) {
			this.effectLights[int1] = new ModelInstance.EffectLight();
		}
	}

	public ModelSlotRenderData init(ModelManager.ModelSlot modelSlot) {
		int int1 = IsoCamera.frameState.playerIndex;
		this.modelSlot = modelSlot;
		this.object = modelSlot.model.object;
		this.x = this.object.x;
		this.y = this.object.y;
		this.z = this.object.z;
		this.character = modelSlot.character;
		BaseVehicle baseVehicle = (BaseVehicle)Type.tryCastTo(this.object, BaseVehicle.class);
		int int2;
		Vector3f vector3f;
		if (baseVehicle != null) {
			this.textureCreator = null;
			this.animPlayer = baseVehicle.getAnimationPlayer();
			this.animPlayerAngle = Float.NaN;
			this.centerOfMassY = baseVehicle.jniTransform.origin.y - BaseVehicle.CENTER_OF_MASS_MAGIC;
			if (BaseVehicle.RENDER_TO_TEXTURE) {
				this.centerOfMassY = 0.0F - BaseVehicle.CENTER_OF_MASS_MAGIC;
			}

			this.alpha = this.object.getAlpha(int1);
			VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)modelSlot.model;
			IsoLightSource[] lightSourceArray = vehicleModelInstance.getLights();
			for (int2 = 0; int2 < this.effectLights.length; ++int2) {
				this.effectLights[int2].set(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0);
			}

			vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc();
			for (int int3 = 0; int3 < lightSourceArray.length; ++int3) {
				IsoLightSource lightSource = lightSourceArray[int3];
				if (lightSource != null) {
					Vector3f vector3f2 = baseVehicle.getLocalPos((float)lightSource.x + 0.5F, (float)lightSource.y + 0.5F, (float)lightSource.z + 0.75F, vector3f);
					baseVehicle.fixLightbarModelLighting(lightSource, vector3f);
					this.effectLights[int3].set(vector3f2.x, vector3f2.y, vector3f2.z, lightSource.r, lightSource.g, lightSource.b, lightSource.radius);
				}
			}

			((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f);
			float float1 = 1.0F - Math.min(RenderSettings.getInstance().getPlayerSettings(int1).getDarkness() * 0.6F, 0.8F);
			float1 *= 0.9F;
			this.ambientR = this.ambientG = this.ambientB = float1;
			this.vehicleTransform.set((Matrix4fc)baseVehicle.vehicleTransform);
		} else {
			this.textureCreator = this.character.getTextureCreator();
			if (this.textureCreator != null && this.textureCreator.isRendered()) {
				this.textureCreator = null;
			}

			ModelInstance.PlayerData playerData = modelSlot.model.playerData[int1];
			this.animPlayer = this.character.getAnimationPlayer();
			this.animPlayerAngle = this.animPlayer.getRenderedAngle();
			for (int int4 = 0; int4 < this.effectLights.length; ++int4) {
				ModelInstance.EffectLight effectLight = playerData.effectLightsMain[int4];
				this.effectLights[int4].set(effectLight.x, effectLight.y, effectLight.z, effectLight.r, effectLight.g, effectLight.b, effectLight.radius);
			}

			this.ambientR = playerData.currentAmbient.x;
			this.ambientG = playerData.currentAmbient.y;
			this.ambientB = playerData.currentAmbient.z;
			this.bOutside = this.character.getCurrentSquare() != null && this.character.getCurrentSquare().isOutside();
			this.alpha = this.character.getAlpha(int1);
			if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
				this.ambientR = this.ambientG = this.ambientB = 1.0F;
			}

			if (GameServer.bServer && ServerGUI.isCreated()) {
				this.ambientR = this.ambientG = this.ambientB = 1.0F;
			}

			this.bCharacterOutline = this.character.bOutline[int1];
			if (this.bCharacterOutline) {
				this.outlineColor.set(this.character.outlineColor[int1]);
			}

			this.bInVehicle = this.character.isSeatedInVehicle();
			if (this.bInVehicle) {
				this.animPlayerAngle = 0.0F;
				BaseVehicle baseVehicle2 = this.character.getVehicle();
				this.centerOfMassY = baseVehicle2.jniTransform.origin.y - BaseVehicle.CENTER_OF_MASS_MAGIC;
				this.x = baseVehicle2.x;
				this.y = baseVehicle2.y;
				this.z = baseVehicle2.z;
				vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc();
				baseVehicle2.getPassengerLocalPos(baseVehicle2.getSeat(this.character), vector3f);
				this.inVehicleX = vector3f.x;
				this.inVehicleY = vector3f.y;
				this.inVehicleZ = vector3f.z;
				((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f);
				Vector3f vector3f3 = baseVehicle2.vehicleTransform.getEulerAnglesZYX((Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc());
				this.vehicleAngleZ = (float)java.lang.Math.toDegrees((double)vector3f3.z);
				this.vehicleAngleY = (float)java.lang.Math.toDegrees((double)vector3f3.y);
				this.vehicleAngleX = (float)java.lang.Math.toDegrees((double)vector3f3.x);
				((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f3);
			}
		}

		this.RENDER_TO_TEXTURE = BaseVehicle.RENDER_TO_TEXTURE;
		this.modelData.clear();
		ModelInstanceRenderData modelInstanceRenderData = null;
		boolean boolean1;
		if (modelSlot.model.model.isReady() && (modelSlot.model.AnimPlayer == null || modelSlot.model.AnimPlayer.isReady())) {
			modelInstanceRenderData = ModelInstanceRenderData.alloc().init(modelSlot.model);
			this.modelData.add(modelInstanceRenderData);
			if (modelSlot.sub.size() != modelSlot.model.sub.size()) {
				boolean1 = true;
			}
		}

		this.initRenderData(modelSlot.model.sub, modelInstanceRenderData);
		boolean1 = false;
		for (int2 = 0; int2 < this.modelData.size(); ++int2) {
			ModelInstanceRenderData modelInstanceRenderData2 = (ModelInstanceRenderData)this.modelData.get(int2);
			if (this.character != null && modelInstanceRenderData2.modelInstance == this.character.primaryHandModel && this.character.isMuzzleFlash()) {
				modelInstanceRenderData2.m_muzzleFlash = true;
			}

			if (modelInstanceRenderData2.modelInstance != null && modelInstanceRenderData2.modelInstance.hasTextureCreator()) {
				boolean1 = true;
			}
		}

		if (this.textureCreator != null) {
			++this.textureCreator.renderRefCount;
		}

		if (this.character != null && (this.textureCreator != null || boolean1)) {
			assert this.readyModelData.isEmpty();
			ModelInstanceRenderData.release(this.readyModelData);
			this.readyModelData.clear();
			for (int2 = 0; int2 < this.character.getReadyModelData().size(); ++int2) {
				ModelInstance modelInstance = (ModelInstance)this.character.getReadyModelData().get(int2);
				ModelInstanceRenderData modelInstanceRenderData3 = ModelInstanceRenderData.alloc().init(modelInstance);
				modelInstanceRenderData3.transformToParent(this.getParentData(modelInstance));
				this.readyModelData.add(modelInstanceRenderData3);
			}
		}

		if (Core.bDebug) {
			this.m_debugRenderData = ModelSlotDebugRenderData.alloc().init(this);
		}

		this.bRendered = false;
		return this;
	}

	private ModelInstanceRenderData getParentData(ModelInstance modelInstance) {
		for (int int1 = 0; int1 < this.readyModelData.size(); ++int1) {
			ModelInstanceRenderData modelInstanceRenderData = (ModelInstanceRenderData)this.readyModelData.get(int1);
			if (modelInstanceRenderData.modelInstance == modelInstance.parent) {
				return modelInstanceRenderData;
			}
		}

		return null;
	}

	private ModelInstanceRenderData initRenderData(ModelInstance modelInstance, ModelInstanceRenderData modelInstanceRenderData) {
		ModelInstanceRenderData modelInstanceRenderData2 = ModelInstanceRenderData.alloc().init(modelInstance);
		modelInstanceRenderData2.transformToParent(modelInstanceRenderData);
		this.modelData.add(modelInstanceRenderData2);
		this.initRenderData(modelInstance.sub, modelInstanceRenderData2);
		return modelInstanceRenderData2;
	}

	private void initRenderData(ArrayList arrayList, ModelInstanceRenderData modelInstanceRenderData) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			ModelInstance modelInstance = (ModelInstance)arrayList.get(int1);
			if (modelInstance.model.isReady() && (modelInstance.AnimPlayer == null || modelInstance.AnimPlayer.isReady())) {
				this.initRenderData(modelInstance, modelInstanceRenderData);
			}
		}
	}

	public void render() {
		if (this.character == null) {
			this.renderVehicle();
		} else {
			this.renderCharacter();
		}
	}

	public void renderDebug() {
		if (this.m_debugRenderData != null) {
			this.m_debugRenderData.render();
		}
	}

	private void renderCharacter() {
		this.bReady = true;
		if (this.textureCreator != null && !this.textureCreator.isRendered()) {
			this.textureCreator.render();
			if (!this.textureCreator.isRendered()) {
				this.bReady = false;
			}
		}

		int int1;
		for (int1 = 0; int1 < this.modelData.size(); ++int1) {
			ModelInstanceRenderData modelInstanceRenderData = (ModelInstanceRenderData)this.modelData.get(int1);
			ModelInstanceTextureInitializer modelInstanceTextureInitializer = modelInstanceRenderData.modelInstance.getTextureInitializer();
			if (modelInstanceTextureInitializer != null && !modelInstanceTextureInitializer.isRendered()) {
				modelInstanceTextureInitializer.render();
				if (!modelInstanceTextureInitializer.isRendered()) {
					this.bReady = false;
				}
			}
		}

		if (this.bReady || !this.readyModelData.isEmpty()) {
			if (this.bCharacterOutline) {
				ModelCamera.instance.bDepthMask = false;
				GameProfiler.getInstance().invokeAndMeasure("performRenderCharacterOutline", this, ModelSlotRenderData::performRenderCharacterOutline);
			}

			ModelCamera.instance.bDepthMask = true;
			GameProfiler.getInstance().invokeAndMeasure("renderCharacter", this, ModelSlotRenderData::performRenderCharacter);
			int1 = SpriteRenderer.instance.getRenderingPlayerIndex();
			IsoPlayer player = (IsoPlayer)Type.tryCastTo(this.character, IsoPlayer.class);
			if (player != null && !this.bCharacterOutline && player == IsoPlayer.players[int1]) {
				ModelOutlines.instance.setPlayerRenderData(this);
			}

			this.bRendered = this.bReady;
		}
	}

	private void renderVehicleDebug() {
		if (Core.bDebug) {
			Vector3 vector3 = Model.tempo;
			ModelCamera.instance.Begin();
			GL11.glMatrixMode(5888);
			GL11.glTranslatef(0.0F, this.centerOfMassY, 0.0F);
			if (this.m_debugRenderData != null && !this.modelData.isEmpty()) {
				PZGLUtil.pushAndMultMatrix(5888, ((ModelInstanceRenderData)this.modelData.get(0)).xfrm);
				this.m_debugRenderData.render();
				PZGLUtil.popMatrix(5888);
			}

			BaseVehicle baseVehicle;
			if (DebugOptions.instance.ModelRenderAttachments.getValue()) {
				baseVehicle = (BaseVehicle)this.object;
				ModelInstanceRenderData modelInstanceRenderData = (ModelInstanceRenderData)this.modelData.get(0);
				PZGLUtil.pushAndMultMatrix(5888, this.vehicleTransform);
				float float1 = baseVehicle.getScript().getModelScale();
				float float2 = modelInstanceRenderData.modelInstance.scale;
				Matrix4f matrix4f = (Matrix4f)((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).alloc();
				matrix4f.scaling(1.0F / float1);
				Matrix4f matrix4f2 = (Matrix4f)((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).alloc();
				for (int int1 = 0; int1 < baseVehicle.getScript().getAttachmentCount(); ++int1) {
					ModelAttachment modelAttachment = baseVehicle.getScript().getAttachment(int1);
					modelInstanceRenderData.modelInstance.getAttachmentMatrix(modelAttachment, matrix4f2);
					matrix4f.mul((Matrix4fc)matrix4f2, matrix4f2);
					PZGLUtil.pushAndMultMatrix(5888, matrix4f2);
					Model.debugDrawAxis(0.0F, 0.0F, 0.0F, 1.0F, 2.0F);
					PZGLUtil.popMatrix(5888);
				}

				((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).release(matrix4f2);
				((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).release(matrix4f);
				PZGLUtil.popMatrix(5888);
			}

			if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue() && !this.modelData.isEmpty()) {
				baseVehicle = (BaseVehicle)this.object;
				GL11.glMatrixMode(5888);
				Vector3f vector3f = this.vehicleTransform.getEulerAnglesZYX((Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc());
				GL11.glRotatef((float)java.lang.Math.toDegrees((double)vector3f.z), 0.0F, 0.0F, 1.0F);
				GL11.glRotatef((float)java.lang.Math.toDegrees((double)vector3f.y), 0.0F, 1.0F, 0.0F);
				GL11.glRotatef((float)java.lang.Math.toDegrees((double)vector3f.x), 1.0F, 0.0F, 0.0F);
				((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f);
				Model.debugDrawAxis(0.0F, 0.0F, 0.0F, 1.0F, 4.0F);
				for (int int2 = 1; int2 < this.modelData.size(); ++int2) {
					VehicleSubModelInstance vehicleSubModelInstance = (VehicleSubModelInstance)Type.tryCastTo(((ModelInstanceRenderData)this.modelData.get(int2)).modelInstance, VehicleSubModelInstance.class);
					if (vehicleSubModelInstance != null && vehicleSubModelInstance.modelInfo.wheelIndex >= 0) {
						float float3 = 1.0F;
						VehicleScript.Wheel wheel = baseVehicle.getScript().getWheel(vehicleSubModelInstance.modelInfo.wheelIndex);
						byte byte1 = -1;
						vector3.set(wheel.offset.x * (float)byte1, baseVehicle.getScript().getModel().offset.y + wheel.offset.y + baseVehicle.getScript().getSuspensionRestLength(), wheel.offset.z);
						Model.debugDrawAxis(vector3.x / float3, vector3.y / float3, vector3.z / float3, baseVehicle.getScript().getSuspensionRestLength() / float3, 2.0F);
					}
				}
			}

			ModelCamera.instance.End();
		}
	}

	private void performRenderCharacter() {
		GL11.glPushClientAttrib(-1);
		GL11.glPushAttrib(1048575);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.0F);
		GL11.glEnable(2929);
		GL11.glDisable(3089);
		ModelInstanceRenderDataList modelInstanceRenderDataList = this.modelData;
		if (this.character != null && !this.bReady) {
			modelInstanceRenderDataList = this.readyModelData;
		}

		Model.CharacterModelCameraBegin(this);
		int int1;
		ModelInstanceRenderData modelInstanceRenderData;
		for (int1 = 0; int1 < modelInstanceRenderDataList.size(); ++int1) {
			modelInstanceRenderData = (ModelInstanceRenderData)modelInstanceRenderDataList.get(int1);
			modelInstanceRenderData.RenderCharacter(this);
		}

		if (Core.bDebug) {
			this.renderDebug();
			for (int1 = 0; int1 < modelInstanceRenderDataList.size(); ++int1) {
				modelInstanceRenderData = (ModelInstanceRenderData)modelInstanceRenderDataList.get(int1);
				modelInstanceRenderData.renderDebug();
			}
		}

		Model.CharacterModelCameraEnd();
		GL11.glPopAttrib();
		GL11.glPopClientAttrib();
		Texture.lastTextureID = -1;
		GL11.glEnable(3553);
		SpriteRenderer.ringBuffer.restoreVBOs = true;
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.0F);
	}

	protected void performRenderCharacterOutline() {
		GL11.glPushClientAttrib(-1);
		GL11.glPushAttrib(1048575);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.0F);
		GL11.glEnable(2929);
		GL11.glDisable(3089);
		ModelInstanceRenderDataList modelInstanceRenderDataList = this.modelData;
		if (this.character != null && !this.bReady) {
			modelInstanceRenderDataList = this.readyModelData;
		}

		if (solidColor == null) {
			solidColor = new Shader("aim_outline_solid", false);
			solidColorStatic = new Shader("aim_outline_solid", true);
		}

		solidColor.Start();
		solidColor.getShaderProgram().setVector4("u_color", this.outlineColor.r, this.outlineColor.g, this.outlineColor.b, this.outlineColor.a);
		solidColor.End();
		solidColorStatic.Start();
		solidColorStatic.getShaderProgram().setVector4("u_color", this.outlineColor.r, this.outlineColor.g, this.outlineColor.b, this.outlineColor.a);
		solidColorStatic.End();
		boolean boolean1 = ModelOutlines.instance.beginRenderOutline(this.outlineColor);
		ModelOutlines.instance.m_fboA.startDrawing(boolean1, true);
		Model.CharacterModelCameraBegin(this);
		for (int int1 = 0; int1 < modelInstanceRenderDataList.size(); ++int1) {
			ModelInstanceRenderData modelInstanceRenderData = (ModelInstanceRenderData)modelInstanceRenderDataList.get(int1);
			Shader shader = modelInstanceRenderData.model.Effect;
			try {
				modelInstanceRenderData.model.Effect = modelInstanceRenderData.model.bStatic ? solidColorStatic : solidColor;
				modelInstanceRenderData.RenderCharacter(this);
			} finally {
				modelInstanceRenderData.model.Effect = shader;
			}
		}

		Model.CharacterModelCameraEnd();
		ModelOutlines.instance.m_fboA.endDrawing();
		GL11.glPopAttrib();
		GL11.glPopClientAttrib();
		Texture.lastTextureID = -1;
		GL11.glEnable(3553);
		SpriteRenderer.ringBuffer.restoreVBOs = true;
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.0F);
	}

	private void renderVehicle() {
		GL11.glPushClientAttrib(-1);
		GL11.glPushAttrib(1048575);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.0F);
		if (this.RENDER_TO_TEXTURE) {
			GL11.glClear(256);
		}

		GL11.glEnable(2929);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.0F);
		GL11.glDisable(3089);
		if (this.RENDER_TO_TEXTURE) {
			ModelManager.instance.bitmap.startDrawing(true, true);
			GL11.glViewport(0, 0, ModelManager.instance.bitmap.getWidth(), ModelManager.instance.bitmap.getHeight());
		}

		for (int int1 = 0; int1 < this.modelData.size(); ++int1) {
			ModelInstanceRenderData modelInstanceRenderData = (ModelInstanceRenderData)this.modelData.get(int1);
			modelInstanceRenderData.RenderVehicle(this);
		}

		this.renderVehicleDebug();
		if (this.RENDER_TO_TEXTURE) {
			ModelManager.instance.bitmap.endDrawing();
		}

		GL11.glPopAttrib();
		GL11.glPopClientAttrib();
		Texture.lastTextureID = -1;
		GL11.glEnable(3553);
		SpriteRenderer.ringBuffer.restoreBoundTextures = true;
		SpriteRenderer.ringBuffer.restoreVBOs = true;
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.0F);
	}

	private void doneWithTextureCreator(ModelInstanceTextureCreator modelInstanceTextureCreator) {
		if (modelInstanceTextureCreator != null) {
			if (modelInstanceTextureCreator.testNotReady > 0) {
				--modelInstanceTextureCreator.testNotReady;
			}

			if (modelInstanceTextureCreator.renderRefCount <= 0) {
				if (modelInstanceTextureCreator.isRendered()) {
					modelInstanceTextureCreator.postRender();
					if (modelInstanceTextureCreator == this.character.getTextureCreator()) {
						this.character.setTextureCreator((ModelInstanceTextureCreator)null);
					}
				} else if (modelInstanceTextureCreator != this.character.getTextureCreator()) {
					modelInstanceTextureCreator.postRender();
				}
			}
		}
	}

	public void postRender() {
		assert this.modelSlot.renderRefCount > 0;
		--this.modelSlot.renderRefCount;
		if (this.textureCreator != null) {
			--this.textureCreator.renderRefCount;
			this.doneWithTextureCreator(this.textureCreator);
			this.textureCreator = null;
		}

		ModelInstanceRenderData.release(this.readyModelData);
		this.readyModelData.clear();
		if (this.bRendered) {
			ModelManager.instance.derefModelInstances(this.character.getReadyModelData());
			this.character.getReadyModelData().clear();
			for (int int1 = 0; int1 < this.modelData.size(); ++int1) {
				ModelInstance modelInstance = ((ModelInstanceRenderData)this.modelData.get(int1)).modelInstance;
				++modelInstance.renderRefCount;
				this.character.getReadyModelData().add(modelInstance);
			}
		}

		this.character = null;
		this.object = null;
		this.animPlayer = null;
		this.m_debugRenderData = (ModelSlotDebugRenderData)Pool.tryRelease((IPooledObject)this.m_debugRenderData);
		ModelInstanceRenderData.release(this.modelData);
		pool.release((Object)this);
	}

	public static ModelSlotRenderData alloc() {
		return (ModelSlotRenderData)pool.alloc();
	}
}
