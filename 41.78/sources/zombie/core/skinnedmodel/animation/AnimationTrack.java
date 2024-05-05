package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.PerformanceSettings;
import zombie.core.math.PZMath;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.advancedanimation.AnimBoneWeight;
import zombie.core.skinnedmodel.advancedanimation.PooledAnimBoneWeightArray;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.util.IPooledObject;
import zombie.util.Lambda;
import zombie.util.Pool;
import zombie.util.PooledArrayObject;
import zombie.util.PooledFloatArrayObject;
import zombie.util.PooledObject;
import zombie.util.StringUtils;
import zombie.util.lambda.Consumers;
import zombie.util.list.PZArrayUtil;


public final class AnimationTrack extends PooledObject {
	public boolean IsPlaying;
	protected AnimationClip CurrentClip;
	public int priority;
	private float currentTimeValue;
	private float previousTimeValue;
	public boolean SyncTrackingEnabled;
	public boolean reverse;
	private boolean bLooping;
	private final AnimationTrack.KeyframeSpan[] m_pose = new AnimationTrack.KeyframeSpan[60];
	private final AnimationTrack.KeyframeSpan m_deferredPoseSpan = new AnimationTrack.KeyframeSpan();
	public float SpeedDelta;
	public float BlendDelta;
	public float blendFieldWeight;
	public String name;
	public float earlyBlendOutTime;
	public boolean triggerOnNonLoopedAnimFadeOutEvent;
	private int m_layerIdx;
	private PooledArrayObject m_boneWeightBindings;
	private PooledFloatArrayObject m_boneWeights;
	private final ArrayList listeners = new ArrayList();
	private final ArrayList listenersInvoking = new ArrayList();
	private SkinningBone m_deferredBone;
	private BoneAxis m_deferredBoneAxis;
	private boolean m_useDeferredRotation;
	private final AnimationTrack.DeferredMotionData m_deferredMotion = new AnimationTrack.DeferredMotionData();
	private static final Pool s_pool = new Pool(AnimationTrack::new);

	public static AnimationTrack alloc() {
		return (AnimationTrack)s_pool.alloc();
	}

	protected AnimationTrack() {
		PZArrayUtil.arrayPopulate(this.m_pose, AnimationTrack.KeyframeSpan::new);
		this.resetInternal();
	}

	private AnimationTrack resetInternal() {
		this.IsPlaying = false;
		this.CurrentClip = null;
		this.priority = 0;
		this.currentTimeValue = 0.0F;
		this.previousTimeValue = 0.0F;
		this.SyncTrackingEnabled = true;
		this.reverse = false;
		this.bLooping = false;
		PZArrayUtil.forEach((Object[])this.m_pose, AnimationTrack.KeyframeSpan::clear);
		this.m_deferredPoseSpan.clear();
		this.SpeedDelta = 1.0F;
		this.BlendDelta = 0.0F;
		this.blendFieldWeight = 0.0F;
		this.name = "!Empty!";
		this.earlyBlendOutTime = 0.0F;
		this.triggerOnNonLoopedAnimFadeOutEvent = false;
		this.m_layerIdx = -1;
		Pool.tryRelease((IPooledObject)this.m_boneWeightBindings);
		this.m_boneWeightBindings = null;
		Pool.tryRelease((IPooledObject)this.m_boneWeights);
		this.m_boneWeights = null;
		this.listeners.clear();
		this.listenersInvoking.clear();
		this.m_deferredBone = null;
		this.m_deferredBoneAxis = BoneAxis.Y;
		this.m_useDeferredRotation = false;
		this.m_deferredMotion.reset();
		return this;
	}

	public void get(int int1, Vector3f vector3f, Quaternion quaternion, Vector3f vector3f2) {
		this.m_pose[int1].lerp(this.getCurrentTime(), vector3f, quaternion, vector3f2);
	}

	private Keyframe getDeferredMovementFrameAt(int int1, float float1, Keyframe keyframe) {
		AnimationTrack.KeyframeSpan keyframeSpan = this.getKeyframeSpan(int1, float1, this.m_deferredPoseSpan);
		return keyframeSpan.lerp(float1, keyframe);
	}

	private AnimationTrack.KeyframeSpan getKeyframeSpan(int int1, float float1, AnimationTrack.KeyframeSpan keyframeSpan) {
		if (!keyframeSpan.isBone(int1)) {
			keyframeSpan.clear();
		}

		Keyframe[] keyframeArray = this.CurrentClip.getBoneFramesAt(int1);
		if (keyframeArray.length == 0) {
			keyframeSpan.clear();
			return keyframeSpan;
		} else if (keyframeSpan.containsTime(float1)) {
			return keyframeSpan;
		} else {
			Keyframe keyframe = keyframeArray[keyframeArray.length - 1];
			if (float1 >= keyframe.Time) {
				keyframeSpan.fromIdx = keyframeArray.length - 2;
				keyframeSpan.toIdx = keyframeArray.length - 1;
				keyframeSpan.from = keyframeArray[keyframeSpan.fromIdx];
				keyframeSpan.to = keyframeArray[keyframeSpan.toIdx];
				return keyframeSpan;
			} else {
				Keyframe keyframe2 = keyframeArray[0];
				if (float1 <= keyframe2.Time) {
					keyframeSpan.clear();
					keyframeSpan.toIdx = 0;
					keyframeSpan.to = keyframe2;
					return keyframeSpan;
				} else {
					int int2 = 0;
					if (keyframeSpan.isSpan() && keyframeSpan.to.Time <= float1) {
						int2 = keyframeSpan.toIdx;
					}

					keyframeSpan.clear();
					for (int int3 = int2; int3 < keyframeArray.length - 1; ++int3) {
						Keyframe keyframe3 = keyframeArray[int3];
						Keyframe keyframe4 = keyframeArray[int3 + 1];
						if (keyframe3.Time <= float1 && float1 <= keyframe4.Time) {
							keyframeSpan.fromIdx = int3;
							keyframeSpan.toIdx = int3 + 1;
							keyframeSpan.from = keyframe3;
							keyframeSpan.to = keyframe4;
							break;
						}
					}

					return keyframeSpan;
				}
			}
		}
	}

	public void removeListener(IAnimListener iAnimListener) {
		this.listeners.remove(iAnimListener);
	}

	public void Update(float float1) {
		try {
			this.UpdateKeyframes(float1);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void UpdateKeyframes(float float1) {
		AnimationTrack.s_performance.updateKeyframes.invokeAndMeasure(this, float1, AnimationTrack::updateKeyframesInternal);
	}

	private void updateKeyframesInternal(float float1) {
		if (this.CurrentClip == null) {
			throw new RuntimeException("AnimationPlayer.Update was called before startClip");
		} else {
			if (float1 > 0.0F) {
				this.TickCurrentTime(float1);
			}

			if (!GameServer.bServer || ServerGUI.isCreated()) {
				this.updatePose();
			}

			this.updateDeferredValues();
		}
	}

	private void updatePose() {
		AnimationTrack.s_performance.updatePose.invokeAndMeasure(this, AnimationTrack::updatePoseInternal);
	}

	private void updatePoseInternal() {
		float float1 = this.getCurrentTime();
		for (int int1 = 0; int1 < 60; ++int1) {
			this.getKeyframeSpan(int1, float1, this.m_pose[int1]);
		}
	}

	private void updateDeferredValues() {
		AnimationTrack.s_performance.updateDeferredValues.invokeAndMeasure(this, AnimationTrack::updateDeferredValuesInternal);
	}

	private void updateDeferredValuesInternal() {
		if (this.m_deferredBone != null) {
			AnimationTrack.DeferredMotionData deferredMotionData = this.m_deferredMotion;
			deferredMotionData.m_deferredRotationDiff = 0.0F;
			deferredMotionData.m_deferredMovementDiff.set(0.0F, 0.0F);
			deferredMotionData.m_counterRotatedMovementDiff.set(0.0F, 0.0F);
			float float1 = this.getReversibleTimeValue(this.previousTimeValue);
			float float2 = this.getReversibleTimeValue(this.currentTimeValue);
			if (this.isLooping() && float1 > float2) {
				float float3 = this.getDuration();
				this.appendDeferredValues(deferredMotionData, float1, float3);
				float1 = 0.0F;
			}

			this.appendDeferredValues(deferredMotionData, float1, float2);
		}
	}

	private void appendDeferredValues(AnimationTrack.DeferredMotionData deferredMotionData, float float1, float float2) {
		Keyframe keyframe = this.getDeferredMovementFrameAt(this.m_deferredBone.Index, float1, AnimationTrack.L_updateDeferredValues.prevKeyFrame);
		Keyframe keyframe2 = this.getDeferredMovementFrameAt(this.m_deferredBone.Index, float2, AnimationTrack.L_updateDeferredValues.keyFrame);
		if (!GameServer.bServer) {
			deferredMotionData.m_prevDeferredRotation = this.getDeferredTwistRotation(keyframe.Rotation);
			deferredMotionData.m_targetDeferredRotationQ.set(keyframe2.Rotation);
			deferredMotionData.m_targetDeferredRotation = this.getDeferredTwistRotation(keyframe2.Rotation);
			float float3 = PZMath.getClosestAngle(deferredMotionData.m_prevDeferredRotation, deferredMotionData.m_targetDeferredRotation);
			deferredMotionData.m_deferredRotationDiff += float3;
		}

		this.getDeferredMovement(keyframe.Position, deferredMotionData.m_prevDeferredMovement);
		deferredMotionData.m_targetDeferredPosition.set(keyframe2.Position);
		this.getDeferredMovement(keyframe2.Position, deferredMotionData.m_targetDeferredMovement);
		Vector2 vector2 = AnimationTrack.L_updateDeferredValues.diff.set(deferredMotionData.m_targetDeferredMovement.x - deferredMotionData.m_prevDeferredMovement.x, deferredMotionData.m_targetDeferredMovement.y - deferredMotionData.m_prevDeferredMovement.y);
		Vector2 vector22 = AnimationTrack.L_updateDeferredValues.crDiff.set(vector2);
		if (this.getUseDeferredRotation()) {
			float float4 = vector22.normalize();
			vector22.rotate(-(deferredMotionData.m_targetDeferredRotation + 1.5707964F));
			vector22.scale(-float4);
		}

		Vector2 vector23 = deferredMotionData.m_deferredMovementDiff;
		vector23.x += vector2.x;
		vector23 = deferredMotionData.m_deferredMovementDiff;
		vector23.y += vector2.y;
		vector23 = deferredMotionData.m_counterRotatedMovementDiff;
		vector23.x += vector22.x;
		vector23 = deferredMotionData.m_counterRotatedMovementDiff;
		vector23.y += vector22.y;
	}

	public float getDeferredTwistRotation(Quaternion quaternion) {
		if (this.m_deferredBoneAxis == BoneAxis.Z) {
			return HelperFunctions.getRotationZ(quaternion);
		} else if (this.m_deferredBoneAxis == BoneAxis.Y) {
			return HelperFunctions.getRotationY(quaternion);
		} else {
			DebugLog.Animation.error("BoneAxis unhandled: %s", String.valueOf(this.m_deferredBoneAxis));
			return 0.0F;
		}
	}

	public Vector2 getDeferredMovement(Vector3f vector3f, Vector2 vector2) {
		if (this.m_deferredBoneAxis == BoneAxis.Y) {
			vector2.set(vector3f.x, -vector3f.z);
		} else {
			vector2.set(vector3f.x, vector3f.y);
		}

		return vector2;
	}

	public Vector3f getCurrentDeferredCounterPosition(Vector3f vector3f) {
		this.getCurrentDeferredPosition(vector3f);
		if (this.m_deferredBoneAxis == BoneAxis.Y) {
			vector3f.set(-vector3f.x, 0.0F, vector3f.z);
		} else {
			vector3f.set(-vector3f.x, -vector3f.y, 0.0F);
		}

		return vector3f;
	}

	public float getCurrentDeferredRotation() {
		return this.m_deferredMotion.m_targetDeferredRotation;
	}

	public Vector3f getCurrentDeferredPosition(Vector3f vector3f) {
		vector3f.set(this.m_deferredMotion.m_targetDeferredPosition);
		return vector3f;
	}

	public int getDeferredMovementBoneIdx() {
		return this.m_deferredBone != null ? this.m_deferredBone.Index : -1;
	}

	public float getCurrentTime() {
		return this.getReversibleTimeValue(this.currentTimeValue);
	}

	public float getPreviousTime() {
		return this.getReversibleTimeValue(this.previousTimeValue);
	}

	private float getReversibleTimeValue(float float1) {
		return this.reverse ? this.getDuration() - float1 : float1;
	}

	protected void TickCurrentTime(float float1) {
		AnimationTrack.s_performance.tickCurrentTime.invokeAndMeasure(this, float1, AnimationTrack::tickCurrentTimeInternal);
	}

	private void tickCurrentTimeInternal(float float1) {
		float1 *= this.SpeedDelta;
		if (!this.IsPlaying) {
			float1 = 0.0F;
		}

		float float2 = this.getDuration();
		this.previousTimeValue = this.currentTimeValue;
		this.currentTimeValue += float1;
		if (this.bLooping) {
			if (this.previousTimeValue == 0.0F && this.currentTimeValue > 0.0F) {
				this.invokeOnAnimStartedEvent();
			}

			if (this.currentTimeValue >= float2) {
				this.invokeOnLoopedAnimEvent();
				this.currentTimeValue %= float2;
				this.invokeOnAnimStartedEvent();
			}
		} else {
			if (this.currentTimeValue < 0.0F) {
				this.currentTimeValue = 0.0F;
			}

			if (this.previousTimeValue == 0.0F && this.currentTimeValue > 0.0F) {
				this.invokeOnAnimStartedEvent();
			}

			if (this.triggerOnNonLoopedAnimFadeOutEvent) {
				float float3 = float2 - this.earlyBlendOutTime;
				if (this.previousTimeValue < float3 && float3 <= this.currentTimeValue) {
					this.invokeOnNonLoopedAnimFadeOutEvent();
				}
			}

			if (this.currentTimeValue > float2) {
				this.currentTimeValue = float2;
			}

			if (this.previousTimeValue < float2 && this.currentTimeValue >= float2) {
				this.invokeOnLoopedAnimEvent();
				this.invokeOnNonLoopedAnimFinishedEvent();
			}
		}
	}

	public float getDuration() {
		return this.hasClip() ? this.CurrentClip.Duration : 0.0F;
	}

	private void invokeListeners(Consumer consumer) {
		if (!this.listeners.isEmpty()) {
			this.listenersInvoking.clear();
			this.listenersInvoking.addAll(this.listeners);
			for (int int1 = 0; int1 < this.listenersInvoking.size(); ++int1) {
				IAnimListener iAnimListener = (IAnimListener)this.listenersInvoking.get(int1);
				consumer.accept(iAnimListener);
			}
		}
	}

	private void invokeListeners(Object object, Consumers.Params1.ICallback iCallback) {
		Lambda.capture(this, object, iCallback, (var0,objectx,iCallbackx,var3)->{
			objectx.invokeListeners(var0.consumer(iCallbackx, var3));
		});
	}

	protected void invokeOnAnimStartedEvent() {
		this.invokeListeners(this, IAnimListener::onAnimStarted);
	}

	protected void invokeOnLoopedAnimEvent() {
		this.invokeListeners(this, IAnimListener::onLoopedAnim);
	}

	protected void invokeOnNonLoopedAnimFadeOutEvent() {
		this.invokeListeners(this, IAnimListener::onNonLoopedAnimFadeOut);
	}

	protected void invokeOnNonLoopedAnimFinishedEvent() {
		this.invokeListeners(this, IAnimListener::onNonLoopedAnimFinished);
	}

	public void onReleased() {
		if (!this.listeners.isEmpty()) {
			this.listenersInvoking.clear();
			this.listenersInvoking.addAll(this.listeners);
			for (int int1 = 0; int1 < this.listenersInvoking.size(); ++int1) {
				IAnimListener iAnimListener = (IAnimListener)this.listenersInvoking.get(int1);
				iAnimListener.onTrackDestroyed(this);
			}

			this.listeners.clear();
			this.listenersInvoking.clear();
		}

		this.reset();
	}

	public Vector2 getDeferredMovementDiff(Vector2 vector2) {
		vector2.set(this.m_deferredMotion.m_counterRotatedMovementDiff);
		return vector2;
	}

	public float getDeferredRotationDiff() {
		return this.m_deferredMotion.m_deferredRotationDiff;
	}

	public float getClampedBlendDelta() {
		return PZMath.clamp(this.BlendDelta, 0.0F, 1.0F);
	}

	public void addListener(IAnimListener iAnimListener) {
		this.listeners.add(iAnimListener);
	}

	public void startClip(AnimationClip animationClip, boolean boolean1) {
		if (animationClip == null) {
			throw new NullPointerException("Supplied clip is null.");
		} else {
			this.reset();
			this.IsPlaying = true;
			this.bLooping = boolean1;
			this.CurrentClip = animationClip;
		}
	}

	public AnimationTrack reset() {
		return this.resetInternal();
	}

	public void setBoneWeights(List list) {
		this.m_boneWeightBindings = PooledAnimBoneWeightArray.toArray(list);
		this.m_boneWeights = null;
	}

	public void initBoneWeights(SkinningData skinningData) {
		if (!this.hasBoneMask()) {
			if (this.m_boneWeightBindings != null) {
				if (this.m_boneWeightBindings.isEmpty()) {
					this.m_boneWeights = PooledFloatArrayObject.alloc(0);
				} else {
					this.m_boneWeights = PooledFloatArrayObject.alloc(skinningData.numBones());
					PZArrayUtil.arraySet(this.m_boneWeights.array(), 0.0F);
					for (int int1 = 0; int1 < this.m_boneWeightBindings.length(); ++int1) {
						AnimBoneWeight animBoneWeight = (AnimBoneWeight)this.m_boneWeightBindings.get(int1);
						this.initWeightBinding(skinningData, animBoneWeight);
					}
				}
			}
		}
	}

	protected void initWeightBinding(SkinningData skinningData, AnimBoneWeight animBoneWeight) {
		if (animBoneWeight != null && !StringUtils.isNullOrEmpty(animBoneWeight.boneName)) {
			String string = animBoneWeight.boneName;
			SkinningBone skinningBone = skinningData.getBone(string);
			if (skinningBone == null) {
				DebugLog.Animation.error("Bone not found: %s", string);
			} else {
				float float1 = animBoneWeight.weight;
				this.assignBoneWeight(float1, skinningBone.Index);
				if (animBoneWeight.includeDescendants) {
					Objects.requireNonNull(skinningBone);
					Lambda.forEach(skinningBone::forEachDescendant, this, float1, (var0,skinningDatax,animBoneWeightx)->{
						skinningDatax.assignBoneWeight(animBoneWeightx, var0.Index);
					});
				}
			}
		}
	}

	private void assignBoneWeight(float float1, int int1) {
		if (!this.hasBoneMask()) {
			throw new NullPointerException("Bone weights array not initialized.");
		} else {
			float float2 = this.m_boneWeights.get(int1);
			this.m_boneWeights.set(int1, Math.max(float1, float2));
		}
	}

	public float getBoneWeight(int int1) {
		if (!this.hasBoneMask()) {
			return 1.0F;
		} else {
			return DebugOptions.instance.Character.Debug.Animate.NoBoneMasks.getValue() ? 1.0F : PZArrayUtil.getOrDefault(this.m_boneWeights.array(), int1, 0.0F);
		}
	}

	public float getDeferredBoneWeight() {
		return this.m_deferredBone == null ? 0.0F : this.getBoneWeight(this.m_deferredBone.Index);
	}

	public void setLayerIdx(int int1) {
		this.m_layerIdx = int1;
	}

	public int getLayerIdx() {
		return this.m_layerIdx;
	}

	public boolean hasBoneMask() {
		return this.m_boneWeights != null;
	}

	public boolean isLooping() {
		return this.bLooping;
	}

	public void setDeferredBone(SkinningBone skinningBone, BoneAxis boneAxis) {
		this.m_deferredBone = skinningBone;
		this.m_deferredBoneAxis = boneAxis;
	}

	public void setUseDeferredRotation(boolean boolean1) {
		this.m_useDeferredRotation = boolean1;
	}

	public boolean getUseDeferredRotation() {
		return this.m_useDeferredRotation;
	}

	public boolean isFinished() {
		return !this.bLooping && this.getDuration() > 0.0F && this.currentTimeValue >= this.getDuration();
	}

	public float getCurrentTimeValue() {
		return this.currentTimeValue;
	}

	public void setCurrentTimeValue(float float1) {
		this.currentTimeValue = float1;
	}

	public float getPreviousTimeValue() {
		return this.previousTimeValue;
	}

	public void setPreviousTimeValue(float float1) {
		this.previousTimeValue = float1;
	}

	public void rewind(float float1) {
		this.advance(-float1);
	}

	public void scaledRewind(float float1) {
		this.scaledAdvance(-float1);
	}

	public void scaledAdvance(float float1) {
		this.advance(float1 * this.SpeedDelta);
	}

	public void advance(float float1) {
		this.currentTimeValue = PZMath.wrap(this.currentTimeValue + float1, 0.0F, this.getDuration());
		this.previousTimeValue = PZMath.wrap(this.previousTimeValue + float1, 0.0F, this.getDuration());
	}

	public void advanceFraction(float float1) {
		this.advance(this.getDuration() * float1);
	}

	public void moveCurrentTimeValueTo(float float1) {
		float float2 = float1 - this.currentTimeValue;
		this.advance(float2);
	}

	public void moveCurrentTimeValueToFraction(float float1) {
		float float2 = this.getDuration() * float1;
		this.moveCurrentTimeValueTo(float2);
	}

	public float getCurrentTimeFraction() {
		return this.hasClip() ? this.currentTimeValue / this.getDuration() : 0.0F;
	}

	public boolean hasClip() {
		return this.CurrentClip != null;
	}

	public AnimationClip getClip() {
		return this.CurrentClip;
	}

	public int getPriority() {
		return this.priority;
	}

	public static AnimationTrack createClone(AnimationTrack animationTrack, Supplier supplier) {
		AnimationTrack animationTrack2 = (AnimationTrack)supplier.get();
		animationTrack2.IsPlaying = animationTrack.IsPlaying;
		animationTrack2.CurrentClip = animationTrack.CurrentClip;
		animationTrack2.priority = animationTrack.priority;
		animationTrack2.currentTimeValue = animationTrack.currentTimeValue;
		animationTrack2.previousTimeValue = animationTrack.previousTimeValue;
		animationTrack2.SyncTrackingEnabled = animationTrack.SyncTrackingEnabled;
		animationTrack2.reverse = animationTrack.reverse;
		animationTrack2.bLooping = animationTrack.bLooping;
		animationTrack2.SpeedDelta = animationTrack.SpeedDelta;
		animationTrack2.BlendDelta = animationTrack.BlendDelta;
		animationTrack2.blendFieldWeight = animationTrack.blendFieldWeight;
		animationTrack2.name = animationTrack.name;
		animationTrack2.earlyBlendOutTime = animationTrack.earlyBlendOutTime;
		animationTrack2.triggerOnNonLoopedAnimFadeOutEvent = animationTrack.triggerOnNonLoopedAnimFadeOutEvent;
		animationTrack2.m_layerIdx = animationTrack.m_layerIdx;
		animationTrack2.m_boneWeightBindings = PooledAnimBoneWeightArray.toArray(animationTrack.m_boneWeightBindings);
		animationTrack2.m_boneWeights = PooledFloatArrayObject.toArray(animationTrack.m_boneWeights);
		animationTrack2.m_deferredBone = animationTrack.m_deferredBone;
		animationTrack2.m_deferredBoneAxis = animationTrack.m_deferredBoneAxis;
		animationTrack2.m_useDeferredRotation = animationTrack.m_useDeferredRotation;
		return animationTrack2;
	}

	private static class KeyframeSpan {
		Keyframe from;
		Keyframe to;
		int fromIdx = -1;
		int toIdx = -1;

		void clear() {
			this.from = null;
			this.to = null;
			this.fromIdx = -1;
			this.toIdx = -1;
		}

		Keyframe lerp(float float1, Keyframe keyframe) {
			keyframe.setIdentity();
			if (this.from == null && this.to == null) {
				return keyframe;
			} else if (this.to == null) {
				keyframe.set(this.from);
				return keyframe;
			} else if (this.from == null) {
				keyframe.set(this.to);
				return keyframe;
			} else {
				return Keyframe.lerp(this.from, this.to, float1, keyframe);
			}
		}

		void lerp(float float1, Vector3f vector3f, Quaternion quaternion, Vector3f vector3f2) {
			if (this.from == null && this.to == null) {
				Keyframe.setIdentity(vector3f, quaternion, vector3f2);
			} else if (this.to == null) {
				this.from.get(vector3f, quaternion, vector3f2);
			} else if (this.from == null) {
				this.to.get(vector3f, quaternion, vector3f2);
			} else if (!PerformanceSettings.InterpolateAnims) {
				this.to.get(vector3f, quaternion, vector3f2);
			} else {
				Keyframe.lerp(this.from, this.to, float1, vector3f, quaternion, vector3f2);
			}
		}

		boolean isSpan() {
			return this.from != null && this.to != null;
		}

		boolean isPost() {
			return (this.from == null || this.to == null) && this.from != this.to;
		}

		boolean isEmpty() {
			return this.from == null && this.to == null;
		}

		boolean containsTime(float float1) {
			return this.isSpan() && this.from.Time <= float1 && float1 <= this.to.Time;
		}

		public boolean isBone(int int1) {
			return this.from != null && this.from.Bone == int1 || this.to != null && this.to.Bone == int1;
		}
	}

	private static class DeferredMotionData {
		float m_targetDeferredRotation;
		float m_prevDeferredRotation;
		final Quaternion m_targetDeferredRotationQ = new Quaternion();
		final Vector3f m_targetDeferredPosition = new Vector3f();
		final Vector2 m_prevDeferredMovement = new Vector2();
		final Vector2 m_targetDeferredMovement = new Vector2();
		float m_deferredRotationDiff;
		final Vector2 m_deferredMovementDiff = new Vector2();
		final Vector2 m_counterRotatedMovementDiff = new Vector2();

		public void reset() {
			this.m_deferredRotationDiff = 0.0F;
			this.m_targetDeferredRotation = 0.0F;
			this.m_prevDeferredRotation = 0.0F;
			this.m_targetDeferredRotationQ.setIdentity();
			this.m_targetDeferredMovement.set(0.0F, 0.0F);
			this.m_targetDeferredPosition.set(0.0F, 0.0F, 0.0F);
			this.m_prevDeferredMovement.set(0.0F, 0.0F);
			this.m_deferredMovementDiff.set(0.0F, 0.0F);
			this.m_counterRotatedMovementDiff.set(0.0F, 0.0F);
		}
	}

	private static class s_performance {
		static final PerformanceProfileProbe tickCurrentTime = new PerformanceProfileProbe("AnimationTrack.tickCurrentTime");
		static final PerformanceProfileProbe updateKeyframes = new PerformanceProfileProbe("AnimationTrack.updateKeyframes");
		static final PerformanceProfileProbe updateDeferredValues = new PerformanceProfileProbe("AnimationTrack.updateDeferredValues");
		static final PerformanceProfileProbe updatePose = new PerformanceProfileProbe("AnimationTrack.updatePose");
	}

	private static class L_updateDeferredValues {
		static final Keyframe keyFrame = new Keyframe(new Vector3f(), new Quaternion(), new Vector3f(1.0F, 1.0F, 1.0F));
		static final Keyframe prevKeyFrame = new Keyframe(new Vector3f(), new Quaternion(), new Vector3f(1.0F, 1.0F, 1.0F));
		static final Vector2 crDiff = new Vector2();
		static final Vector2 diff = new Vector2();
	}

	private static class l_updatePoseInternal {
		static final AnimationTrack.KeyframeSpan span = new AnimationTrack.KeyframeSpan();
	}

	private static class l_getDeferredMovementFrameAt {
		static final AnimationTrack.KeyframeSpan span = new AnimationTrack.KeyframeSpan();
	}
}
