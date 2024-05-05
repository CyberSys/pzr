package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.debug.DebugOptions;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.vehicles.BaseVehicle;


public final class ModelInstanceDebugRenderData extends PooledObject {
	private static final Pool s_pool = new Pool(ModelInstanceDebugRenderData::new);
	private final ArrayList m_attachmentMatrices = new ArrayList();

	public static ModelInstanceDebugRenderData alloc() {
		return (ModelInstanceDebugRenderData)s_pool.alloc();
	}

	public ModelInstanceDebugRenderData init(ModelSlotRenderData modelSlotRenderData, ModelInstanceRenderData modelInstanceRenderData) {
		this.initAttachments(modelSlotRenderData, modelInstanceRenderData);
		return this;
	}

	public void render() {
		this.renderAttachments();
		if (DebugOptions.instance.ModelRenderAxis.getValue()) {
			Model.debugDrawAxis(0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
		}
	}

	private void initAttachments(ModelSlotRenderData modelSlotRenderData, ModelInstanceRenderData modelInstanceRenderData) {
		BaseVehicle.Matrix4fObjectPool matrix4fObjectPool = (BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get();
		matrix4fObjectPool.release(this.m_attachmentMatrices);
		this.m_attachmentMatrices.clear();
		if (DebugOptions.instance.ModelRenderAttachments.getValue()) {
			ModelScript modelScript = modelInstanceRenderData.modelInstance.m_modelScript;
			if (modelScript != null) {
				Matrix4f matrix4f = ((Matrix4f)matrix4fObjectPool.alloc()).set((Matrix4fc)modelInstanceRenderData.xfrm);
				Matrix4f matrix4f2 = (Matrix4f)matrix4fObjectPool.alloc();
				matrix4f.transpose();
				for (int int1 = 0; int1 < modelScript.getAttachmentCount(); ++int1) {
					ModelAttachment modelAttachment = modelScript.getAttachment(int1);
					Matrix4f matrix4f3 = (Matrix4f)matrix4fObjectPool.alloc();
					modelInstanceRenderData.modelInstance.getAttachmentMatrix(modelAttachment, matrix4f3);
					if (!modelInstanceRenderData.model.bStatic && modelAttachment.getBone() != null) {
						if (modelSlotRenderData.animPlayer != null && modelSlotRenderData.animPlayer.hasSkinningData()) {
							int int2 = modelSlotRenderData.animPlayer.getSkinningBoneIndex(modelAttachment.getBone(), 0);
							org.lwjgl.util.vector.Matrix4f matrix4f4 = modelSlotRenderData.animPlayer.modelTransforms[int2];
							PZMath.convertMatrix(matrix4f4, matrix4f2);
							matrix4f2.transpose();
							matrix4f2.mul((Matrix4fc)matrix4f3, matrix4f3);
							matrix4f.mul((Matrix4fc)matrix4f3, matrix4f3);
						}
					} else {
						matrix4f.mul((Matrix4fc)matrix4f3, matrix4f3);
					}

					this.m_attachmentMatrices.add(matrix4f3);
				}

				matrix4fObjectPool.release(matrix4f2);
				matrix4fObjectPool.release(matrix4f);
			}
		}
	}

	private void renderAttachments() {
		for (int int1 = 0; int1 < this.m_attachmentMatrices.size(); ++int1) {
			Matrix4f matrix4f = (Matrix4f)this.m_attachmentMatrices.get(int1);
			PZGLUtil.pushAndMultMatrix(5888, matrix4f);
			Model.debugDrawAxis(0.0F, 0.0F, 0.0F, 0.05F, 1.0F);
			PZGLUtil.popMatrix(5888);
		}
	}
}
