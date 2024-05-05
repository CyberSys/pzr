package zombie.core.skinnedmodel.advancedanimation;

import java.util.ArrayList;
import java.util.List;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.animation.BoneAxis;
import zombie.core.skinnedmodel.animation.IAnimListener;
import zombie.debug.DebugOptions;
import zombie.util.Lambda;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;


public class LiveAnimNode extends PooledObject implements IAnimListener {
	private AnimNode m_sourceNode;
	private AnimLayer m_animLayer;
	private boolean m_active;
	private boolean m_wasActive;
	boolean m_TransitioningOut;
	private float m_Weight;
	private float m_RawWeight;
	private boolean m_isNew;
	private int m_layerIdx;
	private final LiveAnimNode.TransitionIn m_transitionIn = new LiveAnimNode.TransitionIn();
	final List m_AnimationTracks = new ArrayList();
	float m_NodeAnimTime;
	float m_PrevNodeAnimTime;
	private boolean m_blendingIn;
	private boolean m_blendingOut;
	private AnimTransition m_transitionOut;
	private static final Pool s_pool = new Pool(LiveAnimNode::new);

	protected LiveAnimNode() {
	}

	public static LiveAnimNode alloc(AnimLayer animLayer, AnimNode animNode, int int1) {
		LiveAnimNode liveAnimNode = (LiveAnimNode)s_pool.alloc();
		liveAnimNode.reset();
		liveAnimNode.m_sourceNode = animNode;
		liveAnimNode.m_animLayer = animLayer;
		liveAnimNode.m_layerIdx = int1;
		return liveAnimNode;
	}

	private void reset() {
		this.m_sourceNode = null;
		this.m_animLayer = null;
		this.m_active = false;
		this.m_wasActive = false;
		this.m_TransitioningOut = false;
		this.m_Weight = 0.0F;
		this.m_RawWeight = 0.0F;
		this.m_isNew = true;
		this.m_layerIdx = -1;
		this.m_transitionIn.reset();
		this.m_AnimationTracks.clear();
		this.m_NodeAnimTime = 0.0F;
		this.m_PrevNodeAnimTime = 0.0F;
		this.m_blendingIn = false;
		this.m_blendingOut = false;
		this.m_transitionOut = null;
	}

	public void onReleased() {
		this.reset();
	}

	public String getName() {
		return this.m_sourceNode.m_Name;
	}

	public boolean isTransitioningIn() {
		return this.m_transitionIn.m_active && this.m_transitionIn.m_track != null;
	}

	public void startTransitionIn(LiveAnimNode liveAnimNode, AnimTransition animTransition, AnimationTrack animationTrack) {
		this.startTransitionIn(liveAnimNode.getSourceNode(), animTransition, animationTrack);
	}

	public void startTransitionIn(AnimNode animNode, AnimTransition animTransition, AnimationTrack animationTrack) {
		this.m_transitionIn.m_active = animationTrack != null;
		this.m_transitionIn.m_transitionedFrom = animNode.m_Name;
		this.m_transitionIn.m_data = animTransition;
		this.m_transitionIn.m_track = animationTrack;
		this.m_transitionIn.m_weight = 0.0F;
		this.m_transitionIn.m_rawWeight = 0.0F;
		this.m_transitionIn.m_blendingIn = true;
		this.m_transitionIn.m_blendingOut = false;
		this.m_transitionIn.m_time = 0.0F;
		if (this.m_transitionIn.m_track != null) {
			this.m_transitionIn.m_track.addListener(this);
		}

		this.setMainTracksPlaying(false);
	}

	public void setTransitionOut(AnimTransition animTransition) {
		this.m_transitionOut = animTransition;
	}

	public void update(float float1) {
		this.m_isNew = false;
		if (this.m_active != this.m_wasActive) {
			this.m_blendingIn = this.m_active;
			this.m_blendingOut = !this.m_active;
			if (this.m_transitionIn.m_active) {
				this.m_transitionIn.m_blendingIn = this.m_active;
				this.m_transitionIn.m_blendingOut = !this.m_active;
			}

			this.m_wasActive = this.m_active;
		}

		boolean boolean1 = this.isMainAnimActive();
		if (this.isTransitioningIn()) {
			this.updateTransitioningIn(float1);
		}

		boolean boolean2 = this.isMainAnimActive();
		if (boolean2) {
			if (this.m_blendingOut && this.m_sourceNode.m_StopAnimOnExit) {
				this.setMainTracksPlaying(false);
			} else {
				this.setMainTracksPlaying(true);
			}
		} else {
			this.setMainTracksPlaying(false);
		}

		if (boolean2) {
			boolean boolean3 = !boolean1;
			if (boolean3 && this.isLooped()) {
				float float2 = this.getMainInitialRewindTime();
				PZArrayUtil.forEach(this.m_AnimationTracks, Lambda.consumer(float2, AnimationTrack::scaledRewind));
			}

			if (this.m_blendingIn) {
				this.updateBlendingIn(float1);
			} else if (this.m_blendingOut) {
				this.updateBlendingOut(float1);
			}

			this.m_PrevNodeAnimTime = this.m_NodeAnimTime;
			this.m_NodeAnimTime += float1;
			if (!this.m_transitionIn.m_active && this.m_transitionIn.m_track != null && this.m_transitionIn.m_track.BlendDelta <= 0.0F) {
				this.m_animLayer.getAnimationTrack().removeTrack(this.m_transitionIn.m_track);
				this.m_transitionIn.reset();
			}
		}
	}

	private void updateTransitioningIn(float float1) {
		float float2 = this.m_transitionIn.m_track.SpeedDelta;
		float float3 = this.m_transitionIn.m_track.getDuration();
		this.m_transitionIn.m_time = this.m_transitionIn.m_track.getCurrentTimeValue();
		if (this.m_transitionIn.m_time >= float3) {
			this.m_transitionIn.m_active = false;
			this.m_transitionIn.m_weight = 0.0F;
		} else {
			if (!this.m_transitionIn.m_blendingOut) {
				boolean boolean1 = AnimCondition.pass(this.m_animLayer.getVariableSource(), this.m_transitionIn.m_data.m_Conditions);
				if (!boolean1) {
					this.m_transitionIn.m_blendingIn = false;
					this.m_transitionIn.m_blendingOut = true;
				}
			}

			float float4 = this.getTransitionInBlendOutTime() * float2;
			if (this.m_transitionIn.m_time >= float3 - float4) {
				this.m_transitionIn.m_blendingIn = false;
				this.m_transitionIn.m_blendingOut = true;
			}

			float float5;
			float float6;
			float float7;
			if (this.m_transitionIn.m_blendingIn) {
				float5 = this.getTransitionInBlendInTime() * float2;
				float6 = this.incrementBlendTime(this.m_transitionIn.m_rawWeight, float5, float1 * float2);
				float7 = PZMath.clamp(float6 / float5, 0.0F, 1.0F);
				this.m_transitionIn.m_rawWeight = float7;
				this.m_transitionIn.m_weight = PZMath.lerpFunc_EaseOutInQuad(float7);
				this.m_transitionIn.m_blendingIn = float6 < float5;
				this.m_transitionIn.m_active = float6 < float3;
			}

			if (this.m_transitionIn.m_blendingOut) {
				float5 = this.getTransitionInBlendOutTime() * float2;
				float6 = this.incrementBlendTime(1.0F - this.m_transitionIn.m_rawWeight, float5, float1 * float2);
				float7 = PZMath.clamp(1.0F - float6 / float5, 0.0F, 1.0F);
				this.m_transitionIn.m_rawWeight = float7;
				this.m_transitionIn.m_weight = PZMath.lerpFunc_EaseOutInQuad(float7);
				this.m_transitionIn.m_blendingOut = float6 < float5;
				this.m_transitionIn.m_active = this.m_transitionIn.m_blendingOut;
			}
		}
	}

	public void addMainTrack(AnimationTrack animationTrack) {
		if (!this.isLooped() && !this.m_sourceNode.m_StopAnimOnExit && this.m_sourceNode.m_EarlyTransitionOut) {
			float float1 = this.getBlendOutTime();
			if (float1 > 0.0F && Float.isFinite(float1)) {
				animationTrack.earlyBlendOutTime = float1;
				animationTrack.triggerOnNonLoopedAnimFadeOutEvent = true;
			}
		}

		this.m_AnimationTracks.add(animationTrack);
	}

	private void setMainTracksPlaying(boolean boolean1) {
		Lambda.forEachFrom(PZArrayUtil::forEach, (List)this.m_AnimationTracks, boolean1, (var0,boolean1x)->{
			var0.IsPlaying = boolean1x;
		});
	}

	private void updateBlendingIn(float float1) {
		float float2 = this.getBlendInTime();
		if (float2 <= 0.0F) {
			this.m_Weight = 1.0F;
			this.m_RawWeight = 1.0F;
			this.m_blendingIn = false;
		} else {
			float float3 = this.incrementBlendTime(this.m_RawWeight, float2, float1);
			float float4 = PZMath.clamp(float3 / float2, 0.0F, 1.0F);
			this.m_RawWeight = float4;
			this.m_Weight = PZMath.lerpFunc_EaseOutInQuad(float4);
			this.m_blendingIn = float3 < float2;
		}
	}

	private void updateBlendingOut(float float1) {
		float float2 = this.getBlendOutTime();
		if (float2 <= 0.0F) {
			this.m_Weight = 0.0F;
			this.m_RawWeight = 0.0F;
			this.m_blendingOut = false;
		} else {
			float float3 = this.incrementBlendTime(1.0F - this.m_RawWeight, float2, float1);
			float float4 = PZMath.clamp(1.0F - float3 / float2, 0.0F, 1.0F);
			this.m_RawWeight = float4;
			this.m_Weight = PZMath.lerpFunc_EaseOutInQuad(float4);
			this.m_blendingOut = float3 < float2;
		}
	}

	private float incrementBlendTime(float float1, float float2, float float3) {
		float float4 = float1 * float2;
		return float4 + float3;
	}

	public float getTransitionInBlendInTime() {
		return this.m_transitionIn.m_data != null && this.m_transitionIn.m_data.m_blendInTime != Float.POSITIVE_INFINITY ? this.m_transitionIn.m_data.m_blendInTime : 0.0F;
	}

	public float getMainInitialRewindTime() {
		float float1 = 0.0F;
		float float2;
		if (this.m_sourceNode.m_randomAdvanceFraction > 0.0F) {
			float2 = Rand.Next(0.0F, this.m_sourceNode.m_randomAdvanceFraction);
			float1 = float2 * this.getMaxDuration();
		}

		if (this.m_transitionIn.m_data == null) {
			return 0.0F - float1;
		} else {
			float2 = this.getTransitionInBlendOutTime();
			float float3 = this.m_transitionIn.m_data.m_SyncAdjustTime;
			return this.m_transitionIn.m_track != null ? float2 - float3 : float2 - float3 - float1;
		}
	}

	private float getMaxDuration() {
		float float1 = 0.0F;
		int int1 = 0;
		for (int int2 = this.m_AnimationTracks.size(); int1 < int2; ++int1) {
			AnimationTrack animationTrack = (AnimationTrack)this.m_AnimationTracks.get(int1);
			float float2 = animationTrack.getDuration();
			float1 = PZMath.max(float2, float1);
		}

		return float1;
	}

	public float getTransitionInBlendOutTime() {
		return this.getBlendInTime();
	}

	public float getBlendInTime() {
		if (this.m_transitionIn.m_data == null) {
			return this.m_sourceNode.m_BlendTime;
		} else if (this.m_transitionIn.m_track != null && this.m_transitionIn.m_data.m_blendOutTime != Float.POSITIVE_INFINITY) {
			return this.m_transitionIn.m_data.m_blendOutTime;
		} else {
			if (this.m_transitionIn.m_track == null) {
				if (this.m_transitionIn.m_data.m_blendInTime != Float.POSITIVE_INFINITY) {
					return this.m_transitionIn.m_data.m_blendInTime;
				}

				if (this.m_transitionIn.m_data.m_blendOutTime != Float.POSITIVE_INFINITY) {
					return this.m_transitionIn.m_data.m_blendOutTime;
				}
			}

			return this.m_sourceNode.m_BlendTime;
		}
	}

	public float getBlendOutTime() {
		if (this.m_transitionOut == null) {
			return this.m_sourceNode.getBlendOutTime();
		} else if (!StringUtils.isNullOrWhitespace(this.m_transitionOut.m_AnimName) && this.m_transitionOut.m_blendInTime != Float.POSITIVE_INFINITY) {
			return this.m_transitionOut.m_blendInTime;
		} else {
			if (StringUtils.isNullOrWhitespace(this.m_transitionOut.m_AnimName)) {
				if (this.m_transitionOut.m_blendOutTime != Float.POSITIVE_INFINITY) {
					return this.m_transitionOut.m_blendOutTime;
				}

				if (this.m_transitionOut.m_blendInTime != Float.POSITIVE_INFINITY) {
					return this.m_transitionOut.m_blendInTime;
				}
			}

			return this.m_sourceNode.getBlendOutTime();
		}
	}

	public void onAnimStarted(AnimationTrack animationTrack) {
		this.invokeAnimStartTimeEvent();
	}

	public void onLoopedAnim(AnimationTrack animationTrack) {
		if (!this.m_TransitioningOut) {
			this.invokeAnimEndTimeEvent();
		}
	}

	public void onNonLoopedAnimFadeOut(AnimationTrack animationTrack) {
		if (DebugOptions.instance.Animation.AllowEarlyTransitionOut.getValue()) {
			this.invokeAnimEndTimeEvent();
			this.m_TransitioningOut = true;
		}
	}

	public void onNonLoopedAnimFinished(AnimationTrack animationTrack) {
		if (!this.m_TransitioningOut) {
			this.invokeAnimEndTimeEvent();
		}
	}

	public void onTrackDestroyed(AnimationTrack animationTrack) {
		this.m_AnimationTracks.remove(animationTrack);
		if (this.m_transitionIn.m_track == animationTrack) {
			this.m_transitionIn.m_track = null;
			this.m_transitionIn.m_active = false;
			this.m_transitionIn.m_weight = 0.0F;
			this.setMainTracksPlaying(true);
		}
	}

	private void invokeAnimStartTimeEvent() {
		this.invokeAnimTimeEvent(AnimEvent.AnimEventTime.Start);
	}

	private void invokeAnimEndTimeEvent() {
		this.invokeAnimTimeEvent(AnimEvent.AnimEventTime.End);
	}

	private void invokeAnimTimeEvent(AnimEvent.AnimEventTime animEventTime) {
		List list = this.getSourceNode().m_Events;
		int int1 = 0;
		for (int int2 = list.size(); int1 < int2; ++int1) {
			AnimEvent animEvent = (AnimEvent)list.get(int1);
			if (animEvent.m_Time == animEventTime) {
				this.m_animLayer.invokeAnimEvent(animEvent);
			}
		}
	}

	public AnimNode getSourceNode() {
		return this.m_sourceNode;
	}

	public boolean isIdleAnimActive() {
		return this.m_active && this.m_sourceNode.isIdleAnim();
	}

	public boolean isActive() {
		return this.m_active;
	}

	public void setActive(boolean boolean1) {
		this.m_active = boolean1;
	}

	public boolean isLooped() {
		return this.m_sourceNode.m_Looped;
	}

	public float getWeight() {
		return this.m_Weight;
	}

	public float getTransitionInWeight() {
		return this.m_transitionIn.m_weight;
	}

	public boolean wasActivated() {
		return this.m_active != this.m_wasActive && this.m_active;
	}

	public boolean wasDeactivated() {
		return this.m_active != this.m_wasActive && this.m_wasActive;
	}

	public boolean isNew() {
		return this.m_isNew;
	}

	public int getPlayingTrackCount() {
		int int1 = 0;
		if (this.isMainAnimActive()) {
			int1 += this.m_AnimationTracks.size();
		}

		if (this.isTransitioningIn()) {
			++int1;
		}

		return int1;
	}

	public boolean isMainAnimActive() {
		return !this.isTransitioningIn() || this.m_transitionIn.m_blendingOut;
	}

	public AnimationTrack getPlayingTrackAt(int int1) {
		int int2 = this.getPlayingTrackCount();
		if (int1 >= 0 && int1 < int2) {
			return this.isTransitioningIn() && int1 == int2 - 1 ? this.m_transitionIn.m_track : (AnimationTrack)this.m_AnimationTracks.get(int1);
		} else {
			throw new IndexOutOfBoundsException("TrackIdx out of bounds 0 - " + this.getPlayingTrackCount());
		}
	}

	public String getTransitionFrom() {
		return this.m_transitionIn.m_transitionedFrom;
	}

	public void setTransitionInBlendDelta(float float1) {
		if (this.m_transitionIn.m_track != null) {
			this.m_transitionIn.m_track.BlendDelta = float1;
		}
	}

	public AnimationTrack getTransitionInTrack() {
		return this.m_transitionIn.m_track;
	}

	public int getTransitionLayerIdx() {
		return this.m_transitionIn.m_track != null ? this.m_transitionIn.m_track.getLayerIdx() : -1;
	}

	public int getLayerIdx() {
		return this.m_layerIdx;
	}

	public int getPriority() {
		return this.m_sourceNode.getPriority();
	}

	public String getDeferredBoneName() {
		return this.m_sourceNode.getDeferredBoneName();
	}

	public BoneAxis getDeferredBoneAxis() {
		return this.m_sourceNode.getDeferredBoneAxis();
	}

	public List getSubStateBoneWeights() {
		return this.m_sourceNode.m_SubStateBoneWeights;
	}

	public AnimTransition findTransitionTo(IAnimationVariableSource iAnimationVariableSource, String string) {
		return this.m_sourceNode.findTransitionTo(iAnimationVariableSource, string);
	}

	public float getSpeedScale(IAnimationVariableSource iAnimationVariableSource) {
		return this.m_sourceNode.getSpeedScale(iAnimationVariableSource);
	}

	private static class TransitionIn {
		private float m_time;
		private String m_transitionedFrom;
		private boolean m_active;
		private AnimationTrack m_track;
		private AnimTransition m_data;
		private float m_weight;
		private float m_rawWeight;
		private boolean m_blendingIn;
		private boolean m_blendingOut;

		private void reset() {
			this.m_time = 0.0F;
			this.m_transitionedFrom = null;
			this.m_active = false;
			this.m_track = null;
			this.m_data = null;
			this.m_weight = 0.0F;
			this.m_rawWeight = 0.0F;
			this.m_blendingIn = false;
			this.m_blendingOut = false;
		}
	}
}
