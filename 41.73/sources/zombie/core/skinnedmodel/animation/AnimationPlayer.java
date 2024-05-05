package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.joml.Math;
import org.lwjgl.util.vector.Matrix;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import zombie.GameProfiler;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.core.math.PZMath;
import zombie.core.math.Vector3;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.core.skinnedmodel.animation.sharedskele.SharedSkeleAnimationRepository;
import zombie.core.skinnedmodel.animation.sharedskele.SharedSkeleAnimationTrack;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.Vector2;
import zombie.network.MPStatistic;
import zombie.util.IPooledObject;
import zombie.util.Lambda;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;


public final class AnimationPlayer extends PooledObject {
	private Model model;
	private final Matrix4f propTransforms = new Matrix4f();
	private boolean m_boneTransformsNeedFirstFrame = true;
	private TwistableBoneTransform[] m_boneTransforms;
	public Matrix4f[] modelTransforms;
	private AnimationPlayer.SkinTransformData m_skinTransformData = null;
	private AnimationPlayer.SkinTransformData m_skinTransformDataPool = null;
	private SkinningData m_skinningData;
	private SharedSkeleAnimationRepository m_sharedSkeleAnimationRepo = null;
	private SharedSkeleAnimationTrack m_currentSharedTrack;
	private AnimationClip m_currentSharedTrackClip;
	private float m_angle;
	private float m_targetAngle;
	private float m_twistAngle;
	private float m_shoulderTwistAngle;
	private float m_targetTwistAngle;
	private float m_maxTwistAngle = PZMath.degToRad(70.0F);
	private float m_excessTwist = 0.0F;
	private static final float angleStepBase = 0.15F;
	public float angleStepDelta = 1.0F;
	public float angleTwistDelta = 1.0F;
	public boolean bDoBlending = true;
	public boolean bUpdateBones = true;
	private final Vector2 m_lastSetDir = new Vector2();
	private final ArrayList m_reparentedBoneBindings = new ArrayList();
	private final List m_twistBones = new ArrayList();
	private AnimationBoneBinding m_counterRotationBone = null;
	public final ArrayList dismembered = new ArrayList();
	private final float m_minimumValidAnimWeight = 0.001F;
	private final int m_animBlendIndexCacheSize = 32;
	private final int[] m_animBlendIndices = new int[32];
	private final float[] m_animBlendWeights = new float[32];
	private final int[] m_animBlendLayers = new int[32];
	private final int[] m_animBlendPriorities = new int[32];
	private final int m_maxLayers = 4;
	private final int[] m_layerBlendCounts = new int[4];
	private final float[] m_layerWeightTotals = new float[4];
	private int m_totalAnimBlendCount = 0;
	public AnimationPlayer parentPlayer;
	private final Vector2 m_deferredMovement = new Vector2();
	private float m_deferredRotationWeight = 0.0F;
	private float m_deferredAngleDelta = 0.0F;
	private AnimationPlayerRecorder m_recorder = null;
	private static final AnimationTrack[] tempTracks = new AnimationTrack[0];
	private static final Vector2 tempo = new Vector2();
	private static final Pool s_pool = new Pool(AnimationPlayer::new);
	private final AnimationMultiTrack m_multiTrack = new AnimationMultiTrack();

	private AnimationPlayer() {
	}

	public static AnimationPlayer alloc(Model model) {
		AnimationPlayer animationPlayer = (AnimationPlayer)s_pool.alloc();
		animationPlayer.setModel(model);
		return animationPlayer;
	}

	public static float lerpBlendWeight(float float1, float float2, float float3) {
		if (PZMath.equal(float1, float2, 1.0E-4F)) {
			return float2;
		} else {
			float float4 = 1.0F / float3;
			float float5 = GameTime.getInstance().getTimeDelta();
			float float6 = float2 - float1;
			float float7 = (float)PZMath.sign(float6);
			float float8 = float1 + float7 * float4 * float5;
			float float9 = float2 - float8;
			float float10 = (float)PZMath.sign(float9);
			if (float10 != float7) {
				float8 = float2;
			}

			return float8;
		}
	}

	public void setModel(Model model) {
		Objects.requireNonNull(model);
		if (model != this.model) {
			this.model = model;
			this.initSkinningData();
		}
	}

	public Model getModel() {
		return this.model;
	}

	private void initSkinningData() {
		if (this.model.isReady()) {
			SkinningData skinningData = (SkinningData)this.model.Tag;
			if (skinningData != null) {
				if (this.m_skinningData != skinningData) {
					if (this.m_skinningData != null) {
						this.m_skinningData = null;
						this.m_multiTrack.reset();
					}

					this.m_skinningData = skinningData;
					Lambda.forEachFrom(PZArrayUtil::forEach, (List)this.m_reparentedBoneBindings, this.m_skinningData, AnimationBoneBindingPair::setSkinningData);
					Lambda.forEachFrom(PZArrayUtil::forEach, (List)this.m_twistBones, this.m_skinningData, AnimationBoneBinding::setSkinningData);
					if (this.m_counterRotationBone != null) {
						this.m_counterRotationBone.setSkinningData(this.m_skinningData);
					}

					int int1 = skinningData.numBones();
					this.modelTransforms = (Matrix4f[])PZArrayUtil.newInstance(Matrix4f.class, this.modelTransforms, int1, Matrix4f::new);
					this.m_boneTransforms = (TwistableBoneTransform[])PZArrayUtil.newInstance(TwistableBoneTransform.class, this.m_boneTransforms, int1);
					for (int int2 = 0; int2 < int1; ++int2) {
						if (this.m_boneTransforms[int2] == null) {
							this.m_boneTransforms[int2] = TwistableBoneTransform.alloc();
						}

						this.m_boneTransforms[int2].setIdentity();
					}

					this.m_boneTransformsNeedFirstFrame = true;
				}
			}
		}
	}

	public boolean isReady() {
		this.initSkinningData();
		return this.hasSkinningData();
	}

	public boolean hasSkinningData() {
		return this.m_skinningData != null;
	}

	public void addBoneReparent(String string, String string2) {
		if (!PZArrayUtil.contains((List)this.m_reparentedBoneBindings, Lambda.predicate(string, string2, AnimationBoneBindingPair::matches))) {
			AnimationBoneBindingPair animationBoneBindingPair = new AnimationBoneBindingPair(string, string2);
			animationBoneBindingPair.setSkinningData(this.m_skinningData);
			this.m_reparentedBoneBindings.add(animationBoneBindingPair);
		}
	}

	public void setTwistBones(String[] stringArray) {
		ArrayList arrayList = AnimationPlayer.L_setTwistBones.boneNames;
		PZArrayUtil.listConvert(this.m_twistBones, arrayList, (var0)->{
			return var0.boneName;
		});
		if (!PZArrayUtil.sequenceEqual((Object[])stringArray, arrayList, PZArrayUtil.Comparators::equalsIgnoreCase)) {
			this.m_twistBones.clear();
			Lambda.forEachFrom(PZArrayUtil::forEach, (Object)stringArray, this, (var0,stringArrayx)->{
				AnimationBoneBinding arrayList = new AnimationBoneBinding((String)var0);
				arrayList.setSkinningData(stringArrayx.m_skinningData);
				stringArrayx.m_twistBones.add(arrayList);
			});
		}
	}

	public void setCounterRotationBone(String string) {
		if (this.m_counterRotationBone != null && StringUtils.equals(this.m_counterRotationBone.boneName, string)) {
		}

		this.m_counterRotationBone = new AnimationBoneBinding(string);
		this.m_counterRotationBone.setSkinningData(this.m_skinningData);
	}

	public AnimationBoneBinding getCounterRotationBone() {
		return this.m_counterRotationBone;
	}

	public void reset() {
		this.m_multiTrack.reset();
	}

	public void onReleased() {
		this.model = null;
		this.m_skinningData = null;
		this.propTransforms.setIdentity();
		this.m_boneTransformsNeedFirstFrame = true;
		IPooledObject.tryReleaseAndBlank(this.m_boneTransforms);
		PZArrayUtil.forEach((Object[])this.modelTransforms, Matrix::setIdentity);
		this.resetSkinTransforms();
		this.setAngle(0.0F);
		this.setTargetAngle(0.0F);
		this.m_twistAngle = 0.0F;
		this.m_shoulderTwistAngle = 0.0F;
		this.m_targetTwistAngle = 0.0F;
		this.m_maxTwistAngle = PZMath.degToRad(70.0F);
		this.m_excessTwist = 0.0F;
		this.angleStepDelta = 1.0F;
		this.angleTwistDelta = 1.0F;
		this.bDoBlending = true;
		this.bUpdateBones = true;
		this.m_lastSetDir.set(0.0F, 0.0F);
		this.m_reparentedBoneBindings.clear();
		this.m_twistBones.clear();
		this.m_counterRotationBone = null;
		this.dismembered.clear();
		Arrays.fill(this.m_animBlendIndices, 0);
		Arrays.fill(this.m_animBlendWeights, 0.0F);
		Arrays.fill(this.m_animBlendLayers, 0);
		Arrays.fill(this.m_layerBlendCounts, 0);
		Arrays.fill(this.m_layerWeightTotals, 0.0F);
		this.m_totalAnimBlendCount = 0;
		this.parentPlayer = null;
		this.m_deferredMovement.set(0.0F, 0.0F);
		this.m_deferredRotationWeight = 0.0F;
		this.m_deferredAngleDelta = 0.0F;
		this.m_recorder = null;
		this.m_multiTrack.reset();
	}

	public SkinningData getSkinningData() {
		return this.m_skinningData;
	}

	public HashMap getSkinningBoneIndices() {
		return this.m_skinningData != null ? this.m_skinningData.BoneIndices : null;
	}

	public int getSkinningBoneIndex(String string, int int1) {
		HashMap hashMap = this.getSkinningBoneIndices();
		return hashMap != null ? (Integer)hashMap.get(string) : int1;
	}

	private synchronized AnimationPlayer.SkinTransformData getSkinTransformData(SkinningData skinningData) {
		AnimationPlayer.SkinTransformData skinTransformData;
		for (skinTransformData = this.m_skinTransformData; skinTransformData != null; skinTransformData = skinTransformData.m_next) {
			if (skinningData == skinTransformData.m_skinnedTo) {
				return skinTransformData;
			}
		}

		if (this.m_skinTransformDataPool != null) {
			skinTransformData = this.m_skinTransformDataPool;
			skinTransformData.setSkinnedTo(skinningData);
			skinTransformData.dirty = true;
			this.m_skinTransformDataPool = this.m_skinTransformDataPool.m_next;
		} else {
			skinTransformData = AnimationPlayer.SkinTransformData.alloc(skinningData);
		}

		skinTransformData.m_next = this.m_skinTransformData;
		this.m_skinTransformData = skinTransformData;
		return skinTransformData;
	}

	private synchronized void resetSkinTransforms() {
		GameProfiler.getInstance().invokeAndMeasure("resetSkinTransforms", this, AnimationPlayer::resetSkinTransformsInternal);
	}

	private void resetSkinTransformsInternal() {
		if (this.m_skinTransformDataPool != null) {
			AnimationPlayer.SkinTransformData skinTransformData;
			for (skinTransformData = this.m_skinTransformDataPool; skinTransformData.m_next != null; skinTransformData = skinTransformData.m_next) {
			}

			skinTransformData.m_next = this.m_skinTransformData;
		} else {
			this.m_skinTransformDataPool = this.m_skinTransformData;
		}

		this.m_skinTransformData = null;
	}

	public Matrix4f GetPropBoneMatrix(int int1) {
		this.propTransforms.load(this.modelTransforms[int1]);
		return this.propTransforms;
	}

	private AnimationTrack startClip(AnimationClip animationClip, boolean boolean1) {
		if (animationClip == null) {
			throw new NullPointerException("Supplied clip is null.");
		} else {
			AnimationTrack animationTrack = AnimationTrack.alloc();
			animationTrack.startClip(animationClip, boolean1);
			animationTrack.name = animationClip.Name;
			animationTrack.IsPlaying = true;
			this.m_multiTrack.addTrack(animationTrack);
			return animationTrack;
		}
	}

	public static void releaseTracks(List list) {
		AnimationTrack[] animationTrackArray = (AnimationTrack[])list.toArray(tempTracks);
		PZArrayUtil.forEach((Object[])animationTrackArray, PooledObject::release);
	}

	public AnimationTrack play(String string, boolean boolean1) {
		if (this.m_skinningData == null) {
			return null;
		} else {
			AnimationClip animationClip = (AnimationClip)this.m_skinningData.AnimationClips.get(string);
			if (animationClip == null) {
				DebugLog.General.warn("Anim Clip not found: %s", string);
				return null;
			} else {
				AnimationTrack animationTrack = this.startClip(animationClip, boolean1);
				return animationTrack;
			}
		}
	}

	public void Update() {
		this.Update(GameTime.instance.getTimeDelta());
	}

	public void Update(float float1) {
		MPStatistic.getInstance().AnimationPlayerUpdate.Start();
		GameProfiler.getInstance().invokeAndMeasure("AnimationPlayer.Update", this, float1, AnimationPlayer::updateInternal);
		MPStatistic.getInstance().AnimationPlayerUpdate.End();
	}

	private void updateInternal(float float1) {
		if (this.isReady()) {
			this.m_multiTrack.Update(float1);
			if (!this.bUpdateBones) {
				this.updateAnimation_NonVisualOnly();
			} else if (this.m_multiTrack.getTrackCount() > 0) {
				SharedSkeleAnimationTrack sharedSkeleAnimationTrack = this.determineCurrentSharedSkeleTrack();
				if (sharedSkeleAnimationTrack != null) {
					float float2 = this.m_multiTrack.getTrackAt(0).getCurrentTime();
					this.updateAnimation_SharedSkeleTrack(sharedSkeleAnimationTrack, float2);
				} else {
					this.updateAnimation_StandardAnimation();
				}
			}
		}
	}

	private SharedSkeleAnimationTrack determineCurrentSharedSkeleTrack() {
		if (this.m_sharedSkeleAnimationRepo == null) {
			return null;
		} else if (this.bDoBlending) {
			return null;
		} else if (!DebugOptions.instance.Animation.SharedSkeles.Enabled.getValue()) {
			return null;
		} else if (this.m_multiTrack.getTrackCount() != 1) {
			return null;
		} else if (!PZMath.equal(this.m_twistAngle, 0.0F, 114.59155F)) {
			return null;
		} else if (this.parentPlayer != null) {
			return null;
		} else {
			AnimationTrack animationTrack = this.m_multiTrack.getTrackAt(0);
			float float1 = animationTrack.blendFieldWeight;
			if (!PZMath.equal(float1, 0.0F, 0.1F)) {
				return null;
			} else {
				AnimationClip animationClip = animationTrack.getClip();
				if (animationClip == this.m_currentSharedTrackClip) {
					return this.m_currentSharedTrack;
				} else {
					SharedSkeleAnimationTrack sharedSkeleAnimationTrack = this.m_sharedSkeleAnimationRepo.getTrack(animationClip);
					if (sharedSkeleAnimationTrack == null) {
						DebugLog.Animation.debugln("Caching SharedSkeleAnimationTrack: %s", animationTrack.name);
						sharedSkeleAnimationTrack = new SharedSkeleAnimationTrack();
						ModelTransformSampler modelTransformSampler = ModelTransformSampler.alloc(this, animationTrack);
						try {
							sharedSkeleAnimationTrack.set(modelTransformSampler, 5.0F);
						} finally {
							modelTransformSampler.release();
						}

						this.m_sharedSkeleAnimationRepo.setTrack(animationClip, sharedSkeleAnimationTrack);
					}

					this.m_currentSharedTrackClip = animationClip;
					this.m_currentSharedTrack = sharedSkeleAnimationTrack;
					return sharedSkeleAnimationTrack;
				}
			}
		}
	}

	private void updateAnimation_NonVisualOnly() {
		this.updateMultiTrackBoneTransforms_DeferredMovementOnly();
		this.DoAngles();
		this.calculateDeferredMovement();
	}

	public void setSharedAnimRepo(SharedSkeleAnimationRepository sharedSkeleAnimationRepository) {
		this.m_sharedSkeleAnimationRepo = sharedSkeleAnimationRepository;
	}

	private void updateAnimation_SharedSkeleTrack(SharedSkeleAnimationTrack sharedSkeleAnimationTrack, float float1) {
		this.updateMultiTrackBoneTransforms_DeferredMovementOnly();
		this.DoAngles();
		this.calculateDeferredMovement();
		sharedSkeleAnimationTrack.moveToTime(float1);
		for (int int1 = 0; int1 < this.modelTransforms.length; ++int1) {
			sharedSkeleAnimationTrack.getBoneMatrix(int1, this.modelTransforms[int1]);
		}

		this.UpdateSkinTransforms();
	}

	private void updateAnimation_StandardAnimation() {
		if (this.parentPlayer == null) {
			this.updateMultiTrackBoneTransforms();
		} else {
			this.copyBoneTransformsFromParentPlayer();
		}

		this.DoAngles();
		this.calculateDeferredMovement();
		this.updateTwistBone();
		this.applyBoneReParenting();
		this.updateModelTransforms();
		this.UpdateSkinTransforms();
	}

	private void copyBoneTransformsFromParentPlayer() {
		this.m_boneTransformsNeedFirstFrame = false;
		for (int int1 = 0; int1 < this.m_boneTransforms.length; ++int1) {
			this.m_boneTransforms[int1].set(this.parentPlayer.m_boneTransforms[int1]);
		}
	}

	public static float calculateAnimPlayerAngle(Vector2 vector2) {
		return vector2.getDirection();
	}

	public void SetDir(Vector2 vector2) {
		if (this.m_lastSetDir.x != vector2.x || this.m_lastSetDir.y != vector2.y) {
			this.setTargetAngle(calculateAnimPlayerAngle(vector2));
			this.m_targetTwistAngle = PZMath.getClosestAngle(this.m_angle, this.m_targetAngle);
			float float1 = PZMath.clamp(this.m_targetTwistAngle, -this.m_maxTwistAngle, this.m_maxTwistAngle);
			this.m_excessTwist = PZMath.getClosestAngle(float1, this.m_targetTwistAngle);
			this.m_lastSetDir.set(vector2);
		}
	}

	public void SetForceDir(Vector2 vector2) {
		this.setTargetAngle(calculateAnimPlayerAngle(vector2));
		this.setAngleToTarget();
		this.m_targetTwistAngle = 0.0F;
		this.m_lastSetDir.set(vector2);
	}

	public void UpdateDir(IsoGameCharacter gameCharacter) {
		if (gameCharacter != null) {
			this.SetDir(gameCharacter.getForwardDirection());
		}
	}

	public void DoAngles() {
		GameProfiler.getInstance().invokeAndMeasure("AnimationPlayer.doAngles", this, AnimationPlayer::doAnglesInternal);
	}

	private void doAnglesInternal() {
		float float1 = 0.15F * GameTime.instance.getMultiplier();
		this.interpolateBodyAngle(float1);
		this.interpolateBodyTwist(float1);
		this.interpolateShoulderTwist(float1);
	}

	private void interpolateBodyAngle(float float1) {
		float float2 = PZMath.getClosestAngle(this.m_angle, this.m_targetAngle);
		if (PZMath.equal(float2, 0.0F, 0.001F)) {
			this.setAngleToTarget();
			this.m_targetTwistAngle = 0.0F;
		} else {
			float float3 = (float)PZMath.sign(float2);
			float float4 = float1 * float3 * this.angleStepDelta;
			float float5;
			if (DebugOptions.instance.Character.Debug.Animate.DeferredRotationOnly.getValue()) {
				float5 = this.m_deferredAngleDelta;
			} else if (this.m_deferredRotationWeight > 0.0F) {
				float5 = this.m_deferredAngleDelta;
			} else {
				float5 = float4;
			}

			float float6 = (float)PZMath.sign(float5);
			float float7 = this.m_angle;
			float float8 = float7 + float5;
			float float9 = PZMath.getClosestAngle(float8, this.m_targetAngle);
			float float10 = (float)PZMath.sign(float9);
			if (float10 != float3 && float6 == float3) {
				this.setAngleToTarget();
				this.m_targetTwistAngle = 0.0F;
			} else {
				this.setAngle(float8);
				this.m_targetTwistAngle = float9;
			}
		}
	}

	private void interpolateBodyTwist(float float1) {
		float float2 = PZMath.wrap(this.m_targetTwistAngle, -3.1415927F, 3.1415927F);
		float float3 = PZMath.clamp(float2, -this.m_maxTwistAngle, this.m_maxTwistAngle);
		this.m_excessTwist = PZMath.getClosestAngle(float3, float2);
		float float4 = PZMath.getClosestAngle(this.m_twistAngle, float3);
		if (PZMath.equal(float4, 0.0F, 0.001F)) {
			this.m_twistAngle = float3;
		} else {
			float float5 = (float)PZMath.sign(float4);
			float float6 = float1 * float5 * this.angleTwistDelta;
			float float7 = this.m_twistAngle;
			float float8 = float7 + float6;
			float float9 = PZMath.getClosestAngle(float8, float3);
			float float10 = (float)PZMath.sign(float9);
			if (float10 == float5) {
				this.m_twistAngle = float8;
			} else {
				this.m_twistAngle = float3;
			}
		}
	}

	private void interpolateShoulderTwist(float float1) {
		float float2 = PZMath.wrap(this.m_twistAngle, -3.1415927F, 3.1415927F);
		float float3 = PZMath.getClosestAngle(this.m_shoulderTwistAngle, float2);
		if (PZMath.equal(float3, 0.0F, 0.001F)) {
			this.m_shoulderTwistAngle = float2;
		} else {
			float float4 = (float)PZMath.sign(float3);
			float float5 = float1 * float4 * this.angleTwistDelta * 0.55F;
			float float6 = this.m_shoulderTwistAngle;
			float float7 = float6 + float5;
			float float8 = PZMath.getClosestAngle(float7, float2);
			float float9 = (float)PZMath.sign(float8);
			if (float9 == float4) {
				this.m_shoulderTwistAngle = float7;
			} else {
				this.m_shoulderTwistAngle = float2;
			}
		}
	}

	private void updateTwistBone() {
		GameProfiler.getInstance().invokeAndMeasure("updateTwistBone", this, AnimationPlayer::updateTwistBoneInternal);
	}

	private void updateTwistBoneInternal() {
		if (!this.m_twistBones.isEmpty()) {
			float float1 = PZMath.degToRad(1.0F);
			if (!PZMath.equal(this.m_twistAngle, 0.0F, float1)) {
				if (!DebugOptions.instance.Character.Debug.Animate.NoBoneTwists.getValue()) {
					int int1 = this.m_twistBones.size();
					int int2 = int1 - 1;
					float float2 = -this.m_shoulderTwistAngle;
					float float3 = float2 / (float)int2;
					for (int int3 = 0; int3 < int2; ++int3) {
						SkinningBone skinningBone = ((AnimationBoneBinding)this.m_twistBones.get(int3)).getBone();
						this.applyTwistBone(skinningBone, float3);
					}

					float float4 = -this.m_twistAngle;
					float float5 = PZMath.getClosestAngle(float2, float4);
					if (PZMath.abs(float5) > 1.0E-4F) {
						SkinningBone skinningBone2 = ((AnimationBoneBinding)this.m_twistBones.get(int2)).getBone();
						this.applyTwistBone(skinningBone2, float5);
					}
				}
			}
		}
	}

	private void applyTwistBone(SkinningBone skinningBone, float float1) {
		if (skinningBone != null) {
			int int1 = skinningBone.Index;
			int int2 = skinningBone.Parent.Index;
			Matrix4f matrix4f = this.getBoneModelTransform(int2, AnimationPlayer.L_applyTwistBone.twistParentBoneTrans);
			Matrix4f matrix4f2 = Matrix4f.invert(matrix4f, AnimationPlayer.L_applyTwistBone.twistParentBoneTransInv);
			if (matrix4f2 != null) {
				Matrix4f matrix4f3 = this.getBoneModelTransform(int1, AnimationPlayer.L_applyTwistBone.twistBoneTrans);
				Quaternion quaternion = AnimationPlayer.L_applyTwistBone.twistBoneTargetRot;
				Matrix4f matrix4f4 = AnimationPlayer.L_applyTwistBone.twistRotDiffTrans;
				matrix4f4.setIdentity();
				AnimationPlayer.L_applyTwistBone.twistRotDiffTransAxis.set(0.0F, 1.0F, 0.0F);
				float float2 = PZMath.getClosestAngle(this.m_boneTransforms[int1].Twist, float1);
				this.m_boneTransforms[int1].Twist = float1;
				matrix4f4.rotate(float2, AnimationPlayer.L_applyTwistBone.twistRotDiffTransAxis);
				Matrix4f matrix4f5 = AnimationPlayer.L_applyTwistBone.twistBoneTargetTrans;
				Matrix4f.mul(matrix4f3, matrix4f4, matrix4f5);
				HelperFunctions.getRotation(matrix4f5, quaternion);
				Quaternion quaternion2 = AnimationPlayer.L_applyTwistBone.twistBoneNewRot;
				quaternion2.set(quaternion);
				Vector3f vector3f = HelperFunctions.getPosition(matrix4f3, AnimationPlayer.L_applyTwistBone.twistBonePos);
				Vector3f vector3f2 = AnimationPlayer.L_applyTwistBone.twistBoneScale;
				vector3f2.set(1.0F, 1.0F, 1.0F);
				Matrix4f matrix4f6 = AnimationPlayer.L_applyTwistBone.twistBoneNewTrans;
				HelperFunctions.CreateFromQuaternionPositionScale(vector3f, quaternion2, vector3f2, matrix4f6);
				this.m_boneTransforms[int1].mul(matrix4f6, matrix4f2);
			}
		}
	}

	public void resetBoneModelTransforms() {
		if (this.m_skinningData != null && this.modelTransforms != null) {
			this.m_boneTransformsNeedFirstFrame = true;
			int int1 = this.m_boneTransforms.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				this.m_boneTransforms[int2].BlendWeight = 0.0F;
				this.m_boneTransforms[int2].setIdentity();
				this.modelTransforms[int2].setIdentity();
			}
		}
	}

	public boolean isBoneTransformsNeedFirstFrame() {
		return this.m_boneTransformsNeedFirstFrame;
	}

	private void updateMultiTrackBoneTransforms() {
		GameProfiler.getInstance().invokeAndMeasure("updateMultiTrackBoneTransforms", this, AnimationPlayer::updateMultiTrackBoneTransformsInternal);
	}

	private void updateMultiTrackBoneTransformsInternal() {
		int int1;
		for (int1 = 0; int1 < this.modelTransforms.length; ++int1) {
			this.modelTransforms[int1].setIdentity();
		}

		this.updateLayerBlendWeightings();
		if (this.m_totalAnimBlendCount != 0) {
			if (this.isRecording()) {
				this.m_recorder.logAnimWeights(this.m_multiTrack.getTracks(), this.m_animBlendIndices, this.m_animBlendWeights, this.m_deferredMovement);
			}

			for (int1 = 0; int1 < this.m_boneTransforms.length; ++int1) {
				if (!this.isBoneReparented(int1)) {
					this.updateBoneAnimationTransform(int1, (AnimationBoneBindingPair)null);
				}
			}

			this.m_boneTransformsNeedFirstFrame = false;
		}
	}

	private void updateLayerBlendWeightings() {
		List list = this.m_multiTrack.getTracks();
		int int1 = list.size();
		PZArrayUtil.arraySet(this.m_animBlendIndices, -1);
		PZArrayUtil.arraySet(this.m_animBlendWeights, 0.0F);
		PZArrayUtil.arraySet(this.m_animBlendLayers, -1);
		PZArrayUtil.arraySet(this.m_animBlendPriorities, 0);
		int int2;
		float float1;
		int int3;
		int int4;
		for (int2 = 0; int2 < int1; ++int2) {
			AnimationTrack animationTrack = (AnimationTrack)list.get(int2);
			float1 = animationTrack.BlendDelta;
			int3 = animationTrack.getLayerIdx();
			int4 = animationTrack.getPriority();
			if (int3 >= 0 && int3 < 4) {
				if (!(float1 < 0.001F) && (int3 <= 0 || !animationTrack.isFinished())) {
					int int5 = -1;
					for (int int6 = 0; int6 < this.m_animBlendIndices.length; ++int6) {
						if (this.m_animBlendIndices[int6] == -1) {
							int5 = int6;
							break;
						}

						if (int3 <= this.m_animBlendLayers[int6]) {
							if (int3 < this.m_animBlendLayers[int6]) {
								int5 = int6;
								break;
							}

							if (int4 <= this.m_animBlendPriorities[int6]) {
								if (int4 < this.m_animBlendPriorities[int6]) {
									int5 = int6;
									break;
								}

								if (float1 < this.m_animBlendWeights[int6]) {
									int5 = int6;
									break;
								}
							}
						}
					}

					if (int5 < 0) {
						DebugLog.General.error("Buffer overflow. Insufficient anim blends in cache. More than %d animations are being blended at once. Will be truncated to %d.", this.m_animBlendIndices.length, this.m_animBlendIndices.length);
					} else {
						PZArrayUtil.insertAt(this.m_animBlendIndices, int5, int2);
						PZArrayUtil.insertAt(this.m_animBlendWeights, int5, float1);
						PZArrayUtil.insertAt(this.m_animBlendLayers, int5, int3);
						PZArrayUtil.insertAt(this.m_animBlendPriorities, int5, int4);
					}
				}
			} else {
				DebugLog.General.error("Layer index is out of range: %d. Range: 0 - %d", int3, 3);
			}
		}

		PZArrayUtil.arraySet(this.m_layerBlendCounts, 0);
		PZArrayUtil.arraySet(this.m_layerWeightTotals, 0.0F);
		this.m_totalAnimBlendCount = 0;
		int int7;
		float[] floatArray;
		for (int2 = 0; int2 < this.m_animBlendIndices.length && this.m_animBlendIndices[int2] >= 0; ++int2) {
			int7 = this.m_animBlendLayers[int2];
			floatArray = this.m_layerWeightTotals;
			floatArray[int7] += this.m_animBlendWeights[int2];
			int int8 = this.m_layerBlendCounts[int7]++;
			++this.m_totalAnimBlendCount;
		}

		if (this.m_totalAnimBlendCount != 0) {
			if (this.m_boneTransformsNeedFirstFrame) {
				int2 = this.m_animBlendLayers[0];
				int7 = this.m_layerBlendCounts[0];
				float1 = this.m_layerWeightTotals[0];
				if (float1 < 1.0F) {
					for (int3 = 0; int3 < this.m_totalAnimBlendCount; ++int3) {
						int4 = this.m_animBlendLayers[int3];
						if (int4 != int2) {
							break;
						}

						if (float1 > 0.0F) {
							floatArray = this.m_animBlendWeights;
							floatArray[int3] /= float1;
						} else {
							this.m_animBlendWeights[int3] = 1.0F / (float)int7;
						}
					}
				}
			}
		}
	}

	private void calculateDeferredMovement() {
		GameProfiler.getInstance().invokeAndMeasure("calculateDeferredMovement", this, AnimationPlayer::calculateDeferredMovementInternal);
	}

	private void calculateDeferredMovementInternal() {
		List list = this.m_multiTrack.getTracks();
		this.m_deferredMovement.set(0.0F, 0.0F);
		this.m_deferredAngleDelta = 0.0F;
		this.m_deferredRotationWeight = 0.0F;
		float float1 = 1.0F;
		for (int int1 = this.m_totalAnimBlendCount - 1; int1 >= 0 && !(float1 <= 0.001F); --int1) {
			int int2 = this.m_animBlendIndices[int1];
			AnimationTrack animationTrack = (AnimationTrack)list.get(int2);
			if (!animationTrack.isFinished()) {
				float float2 = animationTrack.getDeferredBoneWeight();
				if (!(float2 <= 0.001F)) {
					float float3 = this.m_animBlendWeights[int1] * float2;
					if (!(float3 <= 0.001F)) {
						float float4 = PZMath.clamp(float3, 0.0F, float1);
						float1 -= float3;
						float1 = Math.max(0.0F, float1);
						Vector2.addScaled(this.m_deferredMovement, animationTrack.getDeferredMovementDiff(tempo), float4, this.m_deferredMovement);
						if (animationTrack.getUseDeferredRotation()) {
							this.m_deferredAngleDelta += animationTrack.getDeferredRotationDiff() * float4;
							this.m_deferredRotationWeight += float4;
						}
					}
				}
			}
		}

		this.applyRotationToDeferredMovement(this.m_deferredMovement);
		Vector2 vector2 = this.m_deferredMovement;
		vector2.x *= AdvancedAnimator.s_MotionScale;
		vector2 = this.m_deferredMovement;
		vector2.y *= AdvancedAnimator.s_MotionScale;
		this.m_deferredAngleDelta *= AdvancedAnimator.s_RotationScale;
	}

	private void applyRotationToDeferredMovement(Vector2 vector2) {
		float float1 = vector2.normalize();
		float float2 = this.getRenderedAngle();
		vector2.rotate(float2);
		vector2.setLength(-float1);
	}

	private void applyBoneReParenting() {
		GameProfiler.getInstance().invokeAndMeasure("applyBoneReParenting", this, AnimationPlayer::applyBoneReParentingInternal);
	}

	private void applyBoneReParentingInternal() {
		int int1 = 0;
		for (int int2 = this.m_reparentedBoneBindings.size(); int1 < int2; ++int1) {
			AnimationBoneBindingPair animationBoneBindingPair = (AnimationBoneBindingPair)this.m_reparentedBoneBindings.get(int1);
			if (!animationBoneBindingPair.isValid()) {
				DebugLog.Animation.warn("Animation binding pair is not valid: %s", animationBoneBindingPair);
			} else {
				this.updateBoneAnimationTransform(animationBoneBindingPair.getBoneIdxA(), animationBoneBindingPair);
			}
		}
	}

	private void updateBoneAnimationTransform(int int1, AnimationBoneBindingPair animationBoneBindingPair) {
		this.updateBoneAnimationTransform_Internal(int1, animationBoneBindingPair);
	}

	private void updateBoneAnimationTransform_Internal(int int1, AnimationBoneBindingPair animationBoneBindingPair) {
		List list = this.m_multiTrack.getTracks();
		Vector3f vector3f = AnimationPlayer.L_updateBoneAnimationTransform.pos;
		Quaternion quaternion = AnimationPlayer.L_updateBoneAnimationTransform.rot;
		Vector3f vector3f2 = AnimationPlayer.L_updateBoneAnimationTransform.scale;
		Keyframe keyframe = AnimationPlayer.L_updateBoneAnimationTransform.key;
		int int2 = this.m_totalAnimBlendCount;
		AnimationBoneBinding animationBoneBinding = this.m_counterRotationBone;
		boolean boolean1 = animationBoneBinding != null && animationBoneBinding.getBone() != null && animationBoneBinding.getBone().Index == int1;
		keyframe.setIdentity();
		float float1 = 0.0F;
		boolean boolean2 = true;
		float float2 = 1.0F;
		for (int int3 = int2 - 1; int3 >= 0 && float2 > 0.0F && !(float2 <= 0.001F); --int3) {
			int int4 = this.m_animBlendIndices[int3];
			AnimationTrack animationTrack = (AnimationTrack)list.get(int4);
			float float3 = animationTrack.getBoneWeight(int1);
			if (!(float3 <= 0.001F)) {
				float float4 = this.m_animBlendWeights[int3] * float3;
				if (!(float4 <= 0.001F)) {
					float float5 = PZMath.clamp(float4, 0.0F, float2);
					float2 -= float4;
					float2 = Math.max(0.0F, float2);
					this.getTrackTransform(int1, animationTrack, animationBoneBindingPair, vector3f, quaternion, vector3f2);
					if (boolean1 && animationTrack.getUseDeferredRotation()) {
						Vector3f vector3f3;
						if (DebugOptions.instance.Character.Debug.Animate.ZeroCounterRotationBone.getValue()) {
							vector3f3 = AnimationPlayer.L_updateBoneAnimationTransform.rotAxis;
							Matrix4f matrix4f = AnimationPlayer.L_updateBoneAnimationTransform.rotMat;
							matrix4f.setIdentity();
							vector3f3.set(0.0F, 1.0F, 0.0F);
							matrix4f.rotate(-1.5707964F, vector3f3);
							vector3f3.set(1.0F, 0.0F, 0.0F);
							matrix4f.rotate(-1.5707964F, vector3f3);
							HelperFunctions.getRotation(matrix4f, quaternion);
						} else {
							vector3f3 = HelperFunctions.ToEulerAngles(quaternion, AnimationPlayer.L_updateBoneAnimationTransform.rotEulers);
							HelperFunctions.ToQuaternion((double)vector3f3.x, (double)vector3f3.y, 1.5707963705062866, quaternion);
						}
					}

					boolean boolean3 = animationTrack.getDeferredMovementBoneIdx() == int1;
					if (boolean3) {
						Vector3f vector3f4 = animationTrack.getCurrentDeferredCounterPosition(AnimationPlayer.L_updateBoneAnimationTransform.deferredPos);
						vector3f.x += vector3f4.x;
						vector3f.y += vector3f4.y;
						vector3f.z += vector3f4.z;
					}

					if (boolean2) {
						Vector3.setScaled(vector3f, float5, keyframe.Position);
						keyframe.Rotation.set(quaternion);
						float1 = float5;
						boolean2 = false;
					} else {
						float float6 = float5 / (float5 + float1);
						float1 += float5;
						Vector3.addScaled(keyframe.Position, vector3f, float5, keyframe.Position);
						PZMath.slerp(keyframe.Rotation, keyframe.Rotation, quaternion, float6);
					}
				}
			}
		}

		if (float2 > 0.0F && !this.m_boneTransformsNeedFirstFrame) {
			this.m_boneTransforms[int1].getPRS(vector3f, quaternion, vector3f2);
			Vector3.addScaled(keyframe.Position, vector3f, float2, keyframe.Position);
			PZMath.slerp(keyframe.Rotation, quaternion, keyframe.Rotation, float1);
			PZMath.lerp(keyframe.Scale, vector3f2, keyframe.Scale, float1);
		}

		this.m_boneTransforms[int1].set(keyframe.Position, keyframe.Rotation, keyframe.Scale);
		this.m_boneTransforms[int1].BlendWeight = float1;
		TwistableBoneTransform twistableBoneTransform = this.m_boneTransforms[int1];
		twistableBoneTransform.Twist *= 1.0F - float1;
	}

	private void getTrackTransform(int int1, AnimationTrack animationTrack, AnimationBoneBindingPair animationBoneBindingPair, Vector3f vector3f, Quaternion quaternion, Vector3f vector3f2) {
		if (animationBoneBindingPair == null) {
			animationTrack.get(int1, vector3f, quaternion, vector3f2);
		} else {
			Matrix4f matrix4f = AnimationPlayer.L_getTrackTransform.result;
			SkinningBone skinningBone = animationBoneBindingPair.getBoneA();
			Matrix4f matrix4f2 = getUnweightedBoneTransform(animationTrack, skinningBone.Index, AnimationPlayer.L_getTrackTransform.Pa);
			SkinningBone skinningBone2 = skinningBone.Parent;
			SkinningBone skinningBone3 = animationBoneBindingPair.getBoneB();
			Matrix4f matrix4f3 = this.getBoneModelTransform(skinningBone2.Index, AnimationPlayer.L_getTrackTransform.mA);
			Matrix4f matrix4f4 = Matrix4f.invert(matrix4f3, AnimationPlayer.L_getTrackTransform.mAinv);
			Matrix4f matrix4f5 = this.getBoneModelTransform(skinningBone3.Index, AnimationPlayer.L_getTrackTransform.mB);
			Matrix4f matrix4f6 = this.getUnweightedModelTransform(animationTrack, skinningBone2.Index, AnimationPlayer.L_getTrackTransform.umA);
			Matrix4f matrix4f7 = this.getUnweightedModelTransform(animationTrack, skinningBone3.Index, AnimationPlayer.L_getTrackTransform.umB);
			Matrix4f matrix4f8 = Matrix4f.invert(matrix4f7, AnimationPlayer.L_getTrackTransform.umBinv);
			Matrix4f.mul(matrix4f2, matrix4f6, matrix4f);
			Matrix4f.mul(matrix4f, matrix4f8, matrix4f);
			Matrix4f.mul(matrix4f, matrix4f5, matrix4f);
			Matrix4f.mul(matrix4f, matrix4f4, matrix4f);
			HelperFunctions.getPosition(matrix4f, vector3f);
			HelperFunctions.getRotation(matrix4f, quaternion);
			vector3f2.set(1.0F, 1.0F, 1.0F);
		}
	}

	public boolean isBoneReparented(int int1) {
		return PZArrayUtil.contains((List)this.m_reparentedBoneBindings, Lambda.predicate(int1, (var0,int1x)->{
			return var0.getBoneIdxA() == int1x;
		}));
	}

	public void updateMultiTrackBoneTransforms_DeferredMovementOnly() {
		this.m_deferredMovement.set(0.0F, 0.0F);
		if (this.parentPlayer == null) {
			this.updateLayerBlendWeightings();
			if (this.m_totalAnimBlendCount != 0) {
				int[] intArray = AnimationPlayer.updateMultiTrackBoneTransforms_DeferredMovementOnly.boneIndices;
				int int1 = 0;
				List list = this.m_multiTrack.getTracks();
				int int2 = list.size();
				int int3;
				for (int3 = 0; int3 < int2; ++int3) {
					AnimationTrack animationTrack = (AnimationTrack)list.get(int3);
					int int4 = animationTrack.getDeferredMovementBoneIdx();
					if (int4 != -1 && !PZArrayUtil.contains(intArray, int1, int4)) {
						intArray[int1++] = int4;
					}
				}

				for (int3 = 0; int3 < int1; ++int3) {
					this.updateBoneAnimationTransform(intArray[int3], (AnimationBoneBindingPair)null);
				}
			}
		}
	}

	public boolean isRecording() {
		return this.m_recorder != null && this.m_recorder.isRecording();
	}

	public void setRecorder(AnimationPlayerRecorder animationPlayerRecorder) {
		this.m_recorder = animationPlayerRecorder;
	}

	public AnimationPlayerRecorder getRecorder() {
		return this.m_recorder;
	}

	public void dismember(int int1) {
		this.dismembered.add(int1);
	}

	private void updateModelTransforms() {
		GameProfiler.getInstance().invokeAndMeasure("updateModelTransforms", this, AnimationPlayer::updateModelTransformsInternal);
	}

	private void updateModelTransformsInternal() {
		this.m_boneTransforms[0].getMatrix(this.modelTransforms[0]);
		for (int int1 = 1; int1 < this.modelTransforms.length; ++int1) {
			SkinningBone skinningBone = this.m_skinningData.getBoneAt(int1);
			SkinningBone skinningBone2 = skinningBone.Parent;
			BoneTransform.mul(this.m_boneTransforms[skinningBone.Index], this.modelTransforms[skinningBone2.Index], this.modelTransforms[skinningBone.Index]);
		}
	}

	public Matrix4f getBoneModelTransform(int int1, Matrix4f matrix4f) {
		Matrix4f matrix4f2 = AnimationPlayer.L_getBoneModelTransform.boneTransform;
		matrix4f.setIdentity();
		SkinningBone skinningBone = this.m_skinningData.getBoneAt(int1);
		for (SkinningBone skinningBone2 = skinningBone; skinningBone2 != null; skinningBone2 = skinningBone2.Parent) {
			this.getBoneTransform(skinningBone2.Index, matrix4f2);
			Matrix4f.mul(matrix4f, matrix4f2, matrix4f);
		}

		return matrix4f;
	}

	public Matrix4f getBoneTransform(int int1, Matrix4f matrix4f) {
		this.m_boneTransforms[int1].getMatrix(matrix4f);
		return matrix4f;
	}

	public Matrix4f getUnweightedModelTransform(AnimationTrack animationTrack, int int1, Matrix4f matrix4f) {
		Matrix4f matrix4f2 = AnimationPlayer.L_getUnweightedModelTransform.boneTransform;
		matrix4f2.setIdentity();
		matrix4f.setIdentity();
		SkinningBone skinningBone = this.m_skinningData.getBoneAt(int1);
		for (SkinningBone skinningBone2 = skinningBone; skinningBone2 != null; skinningBone2 = skinningBone2.Parent) {
			getUnweightedBoneTransform(animationTrack, skinningBone2.Index, matrix4f2);
			Matrix4f.mul(matrix4f, matrix4f2, matrix4f);
		}

		return matrix4f;
	}

	public static Matrix4f getUnweightedBoneTransform(AnimationTrack animationTrack, int int1, Matrix4f matrix4f) {
		Vector3f vector3f = AnimationPlayer.L_getUnweightedBoneTransform.pos;
		Quaternion quaternion = AnimationPlayer.L_getUnweightedBoneTransform.rot;
		Vector3f vector3f2 = AnimationPlayer.L_getUnweightedBoneTransform.scale;
		animationTrack.get(int1, vector3f, quaternion, vector3f2);
		HelperFunctions.CreateFromQuaternionPositionScale(vector3f, quaternion, vector3f2, matrix4f);
		return matrix4f;
	}

	public void UpdateSkinTransforms() {
		this.resetSkinTransforms();
	}

	public Matrix4f[] getSkinTransforms(SkinningData skinningData) {
		if (skinningData == null) {
			return this.modelTransforms;
		} else {
			AnimationPlayer.SkinTransformData skinTransformData = this.getSkinTransformData(skinningData);
			Matrix4f[] matrix4fArray = skinTransformData.transforms;
			if (skinTransformData.dirty) {
				for (int int1 = 0; int1 < this.modelTransforms.length; ++int1) {
					if (skinningData.BoneOffset != null && skinningData.BoneOffset.get(int1) != null) {
						Matrix4f.mul((Matrix4f)skinningData.BoneOffset.get(int1), this.modelTransforms[int1], matrix4fArray[int1]);
					} else {
						matrix4fArray[int1].setIdentity();
					}
				}

				skinTransformData.dirty = false;
			}

			return matrix4fArray;
		}
	}

	public void getDeferredMovement(Vector2 vector2) {
		vector2.set(this.m_deferredMovement);
	}

	public float getDeferredAngleDelta() {
		return this.m_deferredAngleDelta;
	}

	public float getDeferredRotationWeight() {
		return this.m_deferredRotationWeight;
	}

	public AnimationMultiTrack getMultiTrack() {
		return this.m_multiTrack;
	}

	public void setRecording(boolean boolean1) {
		this.m_recorder.setRecording(boolean1);
	}

	public void discardRecording() {
		if (this.m_recorder != null) {
			this.m_recorder.discardRecording();
		}
	}

	public float getRenderedAngle() {
		return this.m_angle + 1.5707964F;
	}

	public float getAngle() {
		return this.m_angle;
	}

	public void setAngle(float float1) {
		this.m_angle = float1;
	}

	public void setAngleToTarget() {
		this.setAngle(this.getTargetAngle());
	}

	public void setTargetToAngle() {
		float float1 = this.getAngle();
		this.setTargetAngle(float1);
	}

	public float getTargetAngle() {
		return this.m_targetAngle;
	}

	public void setTargetAngle(float float1) {
		this.m_targetAngle = float1;
	}

	public float getMaxTwistAngle() {
		return this.m_maxTwistAngle;
	}

	public void setMaxTwistAngle(float float1) {
		this.m_maxTwistAngle = float1;
	}

	public float getExcessTwistAngle() {
		return this.m_excessTwist;
	}

	public float getTwistAngle() {
		return this.m_twistAngle;
	}

	public float getShoulderTwistAngle() {
		return this.m_shoulderTwistAngle;
	}

	public float getTargetTwistAngle() {
		return this.m_targetTwistAngle;
	}

	private static class SkinTransformData extends PooledObject {
		public Matrix4f[] transforms;
		private SkinningData m_skinnedTo;
		public boolean dirty;
		private AnimationPlayer.SkinTransformData m_next;
		private static Pool s_pool = new Pool(AnimationPlayer.SkinTransformData::new);

		public void setSkinnedTo(SkinningData skinningData) {
			if (this.m_skinnedTo != skinningData) {
				this.dirty = true;
				this.m_skinnedTo = skinningData;
				this.transforms = (Matrix4f[])PZArrayUtil.newInstance(Matrix4f.class, this.transforms, skinningData.numBones(), Matrix4f::new);
			}
		}

		public static AnimationPlayer.SkinTransformData alloc(SkinningData skinningData) {
			AnimationPlayer.SkinTransformData skinTransformData = (AnimationPlayer.SkinTransformData)s_pool.alloc();
			skinTransformData.setSkinnedTo(skinningData);
			skinTransformData.dirty = true;
			return skinTransformData;
		}
	}

	private static final class L_setTwistBones {
		static final ArrayList boneNames = new ArrayList();
	}

	private static class L_applyTwistBone {
		static final Matrix4f twistParentBoneTrans = new Matrix4f();
		static final Matrix4f twistParentBoneTransInv = new Matrix4f();
		static final Matrix4f twistBoneTrans = new Matrix4f();
		static final Quaternion twistBoneRot = new Quaternion();
		static final Quaternion twistBoneTargetRot = new Quaternion();
		static final Matrix4f twistRotDiffTrans = new Matrix4f();
		static final Vector3f twistRotDiffTransAxis = new Vector3f(0.0F, 1.0F, 0.0F);
		static final Matrix4f twistBoneTargetTrans = new Matrix4f();
		static final Quaternion twistBoneNewRot = new Quaternion();
		static final Vector3f twistBonePos = new Vector3f();
		static final Vector3f twistBoneScale = new Vector3f();
		static final Matrix4f twistBoneNewTrans = new Matrix4f();
	}

	private static final class L_updateBoneAnimationTransform {
		static final Quaternion rot = new Quaternion();
		static final Vector3f pos = new Vector3f();
		static final Vector3f scale = new Vector3f();
		static final Keyframe key = new Keyframe(new Vector3f(0.0F, 0.0F, 0.0F), new Quaternion(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F));
		static final Matrix4f boneMat = new Matrix4f();
		static final Matrix4f rotMat = new Matrix4f();
		static final Vector3f rotAxis = new Vector3f(1.0F, 0.0F, 0.0F);
		static final Quaternion crRot = new Quaternion();
		static final Vector4f crRotAA = new Vector4f();
		static final Matrix4f crMat = new Matrix4f();
		static final Vector3f rotEulers = new Vector3f();
		static final Vector3f deferredPos = new Vector3f();
	}

	private static final class L_getTrackTransform {
		static final Matrix4f Pa = new Matrix4f();
		static final Matrix4f mA = new Matrix4f();
		static final Matrix4f mB = new Matrix4f();
		static final Matrix4f umA = new Matrix4f();
		static final Matrix4f umB = new Matrix4f();
		static final Matrix4f mAinv = new Matrix4f();
		static final Matrix4f umBinv = new Matrix4f();
		static final Matrix4f result = new Matrix4f();
	}

	private static final class updateMultiTrackBoneTransforms_DeferredMovementOnly {
		static int[] boneIndices = new int[60];
	}

	private static class L_getBoneModelTransform {
		static final Matrix4f boneTransform = new Matrix4f();
	}

	private static class L_getUnweightedModelTransform {
		static final Matrix4f boneTransform = new Matrix4f();
	}

	private static class L_getUnweightedBoneTransform {
		static final Vector3f pos = new Vector3f();
		static final Quaternion rot = new Quaternion();
		static final Vector3f scale = new Vector3f();
	}
}
