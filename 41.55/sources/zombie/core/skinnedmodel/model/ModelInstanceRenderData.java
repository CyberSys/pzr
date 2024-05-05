package zombie.core.skinnedmodel.model;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.util.vector.Vector3f;
import org.lwjglx.BufferUtils;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.popman.ObjectPool;
import zombie.scripting.objects.ModelAttachment;
import zombie.util.IPooledObject;
import zombie.util.Pool;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;


public final class ModelInstanceRenderData {
	private static final Vector3f tempVector3f = new Vector3f();
	public Model model;
	public Texture tex;
	public float depthBias;
	public float hue;
	public float tintR;
	public float tintG;
	public float tintB;
	public int parentBone;
	public FloatBuffer matrixPalette;
	public final Matrix4f xfrm = new Matrix4f();
	public SoftwareModelMeshInstance softwareMesh;
	public ModelInstance modelInstance;
	public boolean m_muzzleFlash = false;
	protected ModelInstanceDebugRenderData m_debugRenderData;
	private static final ObjectPool pool = new ObjectPool(ModelInstanceRenderData::new);

	public ModelInstanceRenderData init(ModelInstance modelInstance) {
		this.model = modelInstance.model;
		this.tex = modelInstance.tex;
		this.depthBias = modelInstance.depthBias;
		this.hue = modelInstance.hue;
		this.parentBone = modelInstance.parentBone;
		assert modelInstance.character == null || modelInstance.AnimPlayer != null;
		this.m_muzzleFlash = false;
		this.xfrm.identity();
		if (modelInstance.AnimPlayer != null && !this.model.bStatic) {
			SkinningData skinningData = (SkinningData)this.model.Tag;
			if (Core.bDebug && skinningData == null) {
				DebugLog.General.warn("skinningData is null, matrixPalette may be invalid");
			}

			org.lwjgl.util.vector.Matrix4f[] matrix4fArray = modelInstance.AnimPlayer.getSkinTransforms(skinningData);
			if (this.matrixPalette == null || this.matrixPalette.capacity() < matrix4fArray.length * 16) {
				this.matrixPalette = BufferUtils.createFloatBuffer(matrix4fArray.length * 16);
			}

			this.matrixPalette.clear();
			for (int int1 = 0; int1 < matrix4fArray.length; ++int1) {
				matrix4fArray[int1].store(this.matrixPalette);
			}

			this.matrixPalette.flip();
		}

		VehicleSubModelInstance vehicleSubModelInstance = (VehicleSubModelInstance)Type.tryCastTo(modelInstance, VehicleSubModelInstance.class);
		if (modelInstance instanceof VehicleModelInstance || vehicleSubModelInstance != null) {
			if (modelInstance instanceof VehicleModelInstance) {
				this.xfrm.set((Matrix4fc)((BaseVehicle)modelInstance.object).renderTransform);
			} else {
				this.xfrm.set((Matrix4fc)vehicleSubModelInstance.modelInfo.renderTransform);
			}

			if (modelInstance.model.Mesh != null && modelInstance.model.Mesh.isReady() && modelInstance.model.Mesh.m_transform != null) {
				modelInstance.model.Mesh.m_transform.transpose();
				this.xfrm.mul((Matrix4fc)modelInstance.model.Mesh.m_transform);
				modelInstance.model.Mesh.m_transform.transpose();
			}
		}

		this.softwareMesh = modelInstance.softwareMesh;
		this.modelInstance = modelInstance;
		++modelInstance.renderRefCount;
		if (modelInstance.getTextureInitializer() != null) {
			modelInstance.getTextureInitializer().renderMain();
		}

		return this;
	}

	public void renderDebug() {
		if (this.m_debugRenderData != null) {
			this.m_debugRenderData.render();
		}
	}

	public void RenderCharacter(ModelSlotRenderData modelSlotRenderData) {
		this.tintR = this.modelInstance.tintR;
		this.tintG = this.modelInstance.tintG;
		this.tintB = this.modelInstance.tintB;
		this.tex = this.modelInstance.tex;
		if (this.tex != null || this.modelInstance.model.tex != null) {
			this.model.DrawChar(modelSlotRenderData, this);
		}
	}

	public void RenderVehicle(ModelSlotRenderData modelSlotRenderData) {
		this.tintR = this.modelInstance.tintR;
		this.tintG = this.modelInstance.tintG;
		this.tintB = this.modelInstance.tintB;
		this.tex = this.modelInstance.tex;
		if (this.tex != null || this.modelInstance.model.tex != null) {
			this.model.DrawVehicle(modelSlotRenderData, this);
		}
	}

	public static Matrix4f makeAttachmentTransform(ModelAttachment modelAttachment, Matrix4f matrix4f) {
		matrix4f.translation(modelAttachment.getOffset());
		org.joml.Vector3f vector3f = modelAttachment.getRotate();
		matrix4f.rotateXYZ(vector3f.x * 0.017453292F, vector3f.y * 0.017453292F, vector3f.z * 0.017453292F);
		return matrix4f;
	}

	public static void applyBoneTransform(ModelInstance modelInstance, String string, Matrix4f matrix4f) {
		if (modelInstance != null && modelInstance.AnimPlayer != null) {
			if (!StringUtils.isNullOrWhitespace(string)) {
				int int1 = modelInstance.AnimPlayer.getSkinningBoneIndex(string, -1);
				if (int1 != -1) {
					org.lwjgl.util.vector.Matrix4f matrix4f2 = modelInstance.AnimPlayer.GetPropBoneMatrix(int1);
					Matrix4f matrix4f3 = (Matrix4f)((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).alloc();
					PZMath.convertMatrix(matrix4f2, matrix4f3);
					matrix4f3.transpose();
					matrix4f.mul((Matrix4fc)matrix4f3);
					((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).release(matrix4f3);
				}
			}
		}
	}

	public ModelInstanceRenderData transformToParent(ModelInstanceRenderData modelInstanceRenderData) {
		if (!(this.modelInstance instanceof VehicleModelInstance) && !(this.modelInstance instanceof VehicleSubModelInstance)) {
			if (modelInstanceRenderData == null) {
				return this;
			} else {
				this.xfrm.set((Matrix4fc)modelInstanceRenderData.xfrm);
				this.xfrm.transpose();
				Matrix4f matrix4f = (Matrix4f)((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).alloc();
				ModelAttachment modelAttachment = modelInstanceRenderData.modelInstance.getAttachmentById(this.modelInstance.attachmentNameParent);
				if (modelAttachment == null) {
					if (this.modelInstance.parentBoneName != null && modelInstanceRenderData.modelInstance.AnimPlayer != null) {
						applyBoneTransform(modelInstanceRenderData.modelInstance, this.modelInstance.parentBoneName, this.xfrm);
					}
				} else {
					applyBoneTransform(modelInstanceRenderData.modelInstance, modelAttachment.getBone(), this.xfrm);
					makeAttachmentTransform(modelAttachment, matrix4f);
					this.xfrm.mul((Matrix4fc)matrix4f);
				}

				ModelAttachment modelAttachment2 = this.modelInstance.getAttachmentById(this.modelInstance.attachmentNameSelf);
				if (modelAttachment2 != null) {
					makeAttachmentTransform(modelAttachment2, matrix4f);
					matrix4f.invert();
					this.xfrm.mul((Matrix4fc)matrix4f);
				}

				if (this.modelInstance.model.Mesh != null && this.modelInstance.model.Mesh.isReady() && this.modelInstance.model.Mesh.m_transform != null) {
					this.xfrm.mul((Matrix4fc)this.modelInstance.model.Mesh.m_transform);
				}

				if (this.modelInstance.scale != 1.0F) {
					this.xfrm.scale(this.modelInstance.scale);
				}

				this.xfrm.transpose();
				((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).release(matrix4f);
				return this;
			}
		} else {
			return this;
		}
	}

	private void testOnBackItem(ModelInstance modelInstance) {
		if (modelInstance.parent != null && modelInstance.parent.m_modelScript != null) {
			AnimationPlayer animationPlayer = modelInstance.parent.AnimPlayer;
			ModelAttachment modelAttachment = null;
			ModelAttachment modelAttachment2;
			for (int int1 = 0; int1 < modelInstance.parent.m_modelScript.getAttachmentCount(); ++int1) {
				modelAttachment2 = modelInstance.parent.getAttachment(int1);
				if (modelAttachment2.getBone() != null && this.parentBone == animationPlayer.getSkinningBoneIndex(modelAttachment2.getBone(), 0)) {
					modelAttachment = modelAttachment2;
					break;
				}
			}

			if (modelAttachment != null) {
				Matrix4f matrix4f = (Matrix4f)((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).alloc();
				makeAttachmentTransform(modelAttachment, matrix4f);
				this.xfrm.transpose();
				this.xfrm.mul((Matrix4fc)matrix4f);
				this.xfrm.transpose();
				modelAttachment2 = modelInstance.getAttachmentById(modelAttachment.getId());
				if (modelAttachment2 != null) {
					makeAttachmentTransform(modelAttachment2, matrix4f);
					matrix4f.invert();
					this.xfrm.transpose();
					this.xfrm.mul((Matrix4fc)matrix4f);
					this.xfrm.transpose();
				}

				((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).release(matrix4f);
			}
		}
	}

	public static ModelInstanceRenderData alloc() {
		return (ModelInstanceRenderData)pool.alloc();
	}

	public static void release(ArrayList arrayList) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			ModelInstanceRenderData modelInstanceRenderData = (ModelInstanceRenderData)arrayList.get(int1);
			if (modelInstanceRenderData.modelInstance.getTextureInitializer() != null) {
				modelInstanceRenderData.modelInstance.getTextureInitializer().postRender();
			}

			ModelManager.instance.derefModelInstance(modelInstanceRenderData.modelInstance);
			modelInstanceRenderData.modelInstance = null;
			modelInstanceRenderData.model = null;
			modelInstanceRenderData.tex = null;
			modelInstanceRenderData.softwareMesh = null;
			modelInstanceRenderData.m_debugRenderData = (ModelInstanceDebugRenderData)Pool.tryRelease((IPooledObject)modelInstanceRenderData.m_debugRenderData);
		}

		pool.release((List)arrayList);
	}
}
