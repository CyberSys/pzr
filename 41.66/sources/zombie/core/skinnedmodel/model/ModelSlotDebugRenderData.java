package zombie.core.skinnedmodel.model;

import gnu.trove.list.array.TFloatArrayList;
import java.util.ArrayList;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.Color;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.debug.DebugOptions;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCamera;
import zombie.iso.IsoGridSquare;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;


public final class ModelSlotDebugRenderData extends PooledObject {
	private static final Pool s_pool = new Pool(ModelSlotDebugRenderData::new);
	private ModelSlotRenderData m_slotData;
	private final TFloatArrayList m_boneCoords = new TFloatArrayList();
	private final ArrayList m_boneMatrices = new ArrayList();
	private final TFloatArrayList m_squareLights = new TFloatArrayList();
	private Matrix4f m_weaponMatrix;
	private float m_weaponLength;

	public static ModelSlotDebugRenderData alloc() {
		return (ModelSlotDebugRenderData)s_pool.alloc();
	}

	public ModelSlotDebugRenderData init(ModelSlotRenderData modelSlotRenderData) {
		this.m_slotData = modelSlotRenderData;
		this.initBoneAxis();
		this.initSkeleton();
		this.initLights();
		this.initWeaponHitPoint();
		for (int int1 = 0; int1 < modelSlotRenderData.modelData.size(); ++int1) {
			ModelInstanceRenderData modelInstanceRenderData = (ModelInstanceRenderData)modelSlotRenderData.modelData.get(int1);
			modelInstanceRenderData.m_debugRenderData = ModelInstanceDebugRenderData.alloc().init(modelSlotRenderData, modelInstanceRenderData);
		}

		return this;
	}

	private void initBoneAxis() {
		for (int int1 = 0; int1 < this.m_boneMatrices.size(); ++int1) {
			HelperFunctions.returnMatrix((org.lwjgl.util.vector.Matrix4f)this.m_boneMatrices.get(int1));
		}

		this.m_boneMatrices.clear();
		if (this.m_slotData.animPlayer != null && this.m_slotData.animPlayer.hasSkinningData()) {
			if (DebugOptions.instance.Character.Debug.Render.Bip01.getValue()) {
				this.initBoneAxis("Bip01");
			}

			if (DebugOptions.instance.Character.Debug.Render.PrimaryHandBone.getValue()) {
				this.initBoneAxis("Bip01_Prop1");
			}

			if (DebugOptions.instance.Character.Debug.Render.SecondaryHandBone.getValue()) {
				this.initBoneAxis("Bip01_Prop2");
			}

			if (DebugOptions.instance.Character.Debug.Render.TranslationData.getValue()) {
				this.initBoneAxis("Translation_Data");
			}
		}
	}

	private void initBoneAxis(String string) {
		Integer integer = (Integer)this.m_slotData.animPlayer.getSkinningData().BoneIndices.get(string);
		if (integer != null) {
			org.lwjgl.util.vector.Matrix4f matrix4f = HelperFunctions.getMatrix();
			matrix4f.load(this.m_slotData.animPlayer.modelTransforms[integer]);
			this.m_boneMatrices.add(matrix4f);
		}
	}

	private void initSkeleton() {
		this.m_boneCoords.clear();
		if (DebugOptions.instance.ModelRenderBones.getValue()) {
			this.initSkeleton(this.m_slotData.animPlayer);
			if (this.m_slotData.object instanceof BaseVehicle) {
				for (int int1 = 0; int1 < this.m_slotData.modelData.size(); ++int1) {
					ModelInstanceRenderData modelInstanceRenderData = (ModelInstanceRenderData)this.m_slotData.modelData.get(int1);
					VehicleSubModelInstance vehicleSubModelInstance = (VehicleSubModelInstance)Type.tryCastTo(modelInstanceRenderData.modelInstance, VehicleSubModelInstance.class);
					if (vehicleSubModelInstance != null) {
						this.initSkeleton(vehicleSubModelInstance.AnimPlayer);
					}
				}
			}
		}
	}

	private void initSkeleton(AnimationPlayer animationPlayer) {
		if (animationPlayer != null && animationPlayer.hasSkinningData() && !animationPlayer.isBoneTransformsNeedFirstFrame()) {
			Integer integer = (Integer)animationPlayer.getSkinningData().BoneIndices.get("Translation_Data");
			for (int int1 = 0; int1 < animationPlayer.modelTransforms.length; ++int1) {
				if (integer == null || int1 != integer) {
					int int2 = (Integer)animationPlayer.getSkinningData().SkeletonHierarchy.get(int1);
					if (int2 >= 0) {
						this.initSkeleton(animationPlayer.modelTransforms, int1);
						this.initSkeleton(animationPlayer.modelTransforms, int2);
					}
				}
			}
		}
	}

	private void initSkeleton(org.lwjgl.util.vector.Matrix4f[] matrix4fArray, int int1) {
		float float1 = matrix4fArray[int1].m03;
		float float2 = matrix4fArray[int1].m13;
		float float3 = matrix4fArray[int1].m23;
		this.m_boneCoords.add(float1);
		this.m_boneCoords.add(float2);
		this.m_boneCoords.add(float3);
	}

	private void initLights() {
		this.m_squareLights.clear();
		if (DebugOptions.instance.ModelRenderLights.getValue()) {
			if (this.m_slotData.character != null) {
				if (this.m_slotData.character.getCurrentSquare() != null) {
					int int1 = IsoCamera.frameState.playerIndex;
					IsoGridSquare.ILighting iLighting = this.m_slotData.character.getCurrentSquare().lighting[int1];
					for (int int2 = 0; int2 < iLighting.resultLightCount(); ++int2) {
						IsoGridSquare.ResultLight resultLight = iLighting.getResultLight(int2);
						this.m_squareLights.add((float)resultLight.x);
						this.m_squareLights.add((float)resultLight.y);
						this.m_squareLights.add((float)resultLight.z);
					}
				}
			}
		}
	}

	private void initWeaponHitPoint() {
		if (this.m_weaponMatrix != null) {
			((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).release(this.m_weaponMatrix);
			this.m_weaponMatrix = null;
		}

		if (DebugOptions.instance.ModelRenderWeaponHitPoint.getValue()) {
			if (this.m_slotData.animPlayer != null && this.m_slotData.animPlayer.hasSkinningData()) {
				if (this.m_slotData.character != null) {
					Integer integer = (Integer)this.m_slotData.animPlayer.getSkinningData().BoneIndices.get("Bip01_Prop1");
					if (integer != null) {
						HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(this.m_slotData.character.getPrimaryHandItem(), HandWeapon.class);
						if (handWeapon != null) {
							this.m_weaponLength = handWeapon.WeaponLength;
							org.lwjgl.util.vector.Matrix4f matrix4f = this.m_slotData.animPlayer.modelTransforms[integer];
							this.m_weaponMatrix = (Matrix4f)((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).alloc();
							PZMath.convertMatrix(matrix4f, this.m_weaponMatrix);
							this.m_weaponMatrix.transpose();
						}
					}
				}
			}
		}
	}

	public void render() {
		this.renderBonesAxis();
		this.renderSkeleton();
		this.renderLights();
		this.renderWeaponHitPoint();
	}

	private void renderBonesAxis() {
		for (int int1 = 0; int1 < this.m_boneMatrices.size(); ++int1) {
			Model.drawBoneMtx((org.lwjgl.util.vector.Matrix4f)this.m_boneMatrices.get(int1));
		}
	}

	private void renderSkeleton() {
		if (!this.m_boneCoords.isEmpty()) {
			GL11.glDisable(2929);
			int int1;
			for (int1 = 7; int1 >= 0; --int1) {
				GL13.glActiveTexture('蓀' + int1);
				GL11.glDisable(3553);
			}

			GL11.glLineWidth(1.0F);
			GL11.glBegin(1);
			for (int1 = 0; int1 < this.m_boneCoords.size(); int1 += 6) {
				Color color = Model.debugDrawColours[int1 % Model.debugDrawColours.length];
				GL11.glColor3f(color.r, color.g, color.b);
				float float1 = this.m_boneCoords.get(int1);
				float float2 = this.m_boneCoords.get(int1 + 1);
				float float3 = this.m_boneCoords.get(int1 + 2);
				GL11.glVertex3f(float1, float2, float3);
				float1 = this.m_boneCoords.get(int1 + 3);
				float2 = this.m_boneCoords.get(int1 + 4);
				float3 = this.m_boneCoords.get(int1 + 5);
				GL11.glVertex3f(float1, float2, float3);
			}

			GL11.glEnd();
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			GL11.glEnable(2929);
		}
	}

	private void renderLights() {
		for (int int1 = 0; int1 < this.m_squareLights.size(); int1 += 3) {
			float float1 = this.m_squareLights.get(int1);
			float float2 = this.m_squareLights.get(int1 + 1);
			float float3 = this.m_squareLights.get(int1 + 2);
			Model.debugDrawLightSource(float1, float2, float3, this.m_slotData.x, this.m_slotData.y, this.m_slotData.z, -this.m_slotData.animPlayerAngle);
		}
	}

	private void renderWeaponHitPoint() {
		if (this.m_weaponMatrix != null) {
			PZGLUtil.pushAndMultMatrix(5888, this.m_weaponMatrix);
			Model.debugDrawAxis(0.0F, this.m_weaponLength, 0.0F, 0.05F, 1.0F);
			PZGLUtil.popMatrix(5888);
		}
	}
}
