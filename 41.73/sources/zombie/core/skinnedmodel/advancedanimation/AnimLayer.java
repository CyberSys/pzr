package zombie.core.skinnedmodel.advancedanimation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.joml.Math;
import zombie.GameProfiler;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.animation.AnimationMultiTrack;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.animation.BoneAxis;
import zombie.core.skinnedmodel.animation.IAnimListener;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.StringUtils;


public final class AnimLayer implements IAnimListener {
	private final AnimLayer m_parentLayer;
	private final IAnimatable m_Character;
	private AnimState m_State;
	private AnimNode m_CurrentNode;
	private IAnimEventCallback m_AnimEventsCallback;
	private LiveAnimNode m_currentSyncNode;
	private AnimationTrack m_currentSyncTrack;
	private final List m_reusableAnimNodes;
	private final List m_liveAnimNodes;
	private static final AnimEvent s_activeAnimLoopedEvent = new AnimEvent();
	private static final AnimEvent s_activeNonLoopedAnimFadeOutEvent;
	private static final AnimEvent s_activeAnimFinishingEvent;
	private static final AnimEvent s_activeNonLoopedAnimFinishedEvent;

	public AnimLayer(IAnimatable iAnimatable, IAnimEventCallback iAnimEventCallback) {
		this((AnimLayer)null, iAnimatable, iAnimEventCallback);
	}

	public AnimLayer(AnimLayer animLayer, IAnimatable iAnimatable, IAnimEventCallback iAnimEventCallback) {
		this.m_State = null;
		this.m_CurrentNode = null;
		this.m_reusableAnimNodes = new ArrayList();
		this.m_liveAnimNodes = new ArrayList();
		this.m_parentLayer = animLayer;
		this.m_Character = iAnimatable;
		this.m_AnimEventsCallback = iAnimEventCallback;
	}

	public String getCurrentStateName() {
		return this.m_State == null ? null : this.m_State.m_Name;
	}

	public boolean hasState() {
		return this.m_State != null;
	}

	public boolean isStateless() {
		return this.m_State == null;
	}

	public boolean isSubLayer() {
		return this.m_parentLayer != null;
	}

	public boolean isCurrentState(String string) {
		return this.m_State != null && StringUtils.equals(this.m_State.m_Name, string);
	}

	public AnimationMultiTrack getAnimationTrack() {
		if (this.m_Character == null) {
			return null;
		} else {
			AnimationPlayer animationPlayer = this.m_Character.getAnimationPlayer();
			return animationPlayer == null ? null : animationPlayer.getMultiTrack();
		}
	}

	public IAnimationVariableSource getVariableSource() {
		return this.m_Character;
	}

	public LiveAnimNode getCurrentSyncNode() {
		return this.m_currentSyncNode;
	}

	public AnimationTrack getCurrentSyncTrack() {
		return this.m_currentSyncTrack;
	}

	public void onAnimStarted(AnimationTrack animationTrack) {
	}

	public void onLoopedAnim(AnimationTrack animationTrack) {
		this.invokeAnimEvent(animationTrack, s_activeAnimLoopedEvent, false);
	}

	public void onNonLoopedAnimFadeOut(AnimationTrack animationTrack) {
		this.invokeAnimEvent(animationTrack, s_activeAnimFinishingEvent, true);
		this.invokeAnimEvent(animationTrack, s_activeNonLoopedAnimFadeOutEvent, true);
	}

	public void onNonLoopedAnimFinished(AnimationTrack animationTrack) {
		this.invokeAnimEvent(animationTrack, s_activeAnimFinishingEvent, false);
		this.invokeAnimEvent(animationTrack, s_activeNonLoopedAnimFinishedEvent, true);
	}

	public void onTrackDestroyed(AnimationTrack animationTrack) {
	}

	protected void invokeAnimEvent(AnimationTrack animationTrack, AnimEvent animEvent, boolean boolean1) {
		if (this.m_AnimEventsCallback != null) {
			int int1 = 0;
			for (int int2 = this.m_liveAnimNodes.size(); int1 < int2; ++int1) {
				LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
				if ((!liveAnimNode.m_TransitioningOut || boolean1) && liveAnimNode.getSourceNode().m_State == this.m_State && liveAnimNode.m_AnimationTracks.contains(animationTrack)) {
					this.invokeAnimEvent(animEvent);
					break;
				}
			}
		}
	}

	protected void invokeAnimEvent(AnimEvent animEvent) {
		if (this.m_AnimEventsCallback == null) {
			DebugLog.Animation.warn("invokeAnimEvent. No listener. %s", animEvent.toDetailsString());
		} else {
			this.m_AnimEventsCallback.OnAnimEvent(this, animEvent);
		}
	}

	public String GetDebugString() {
		String string = this.m_Character.getAdvancedAnimator().animSet.m_Name;
		if (this.m_State != null) {
			string = string + "/" + this.m_State.m_Name;
			if (this.m_CurrentNode != null) {
				string = string + "/" + this.m_CurrentNode.m_Name + ": " + this.m_CurrentNode.m_AnimName;
			}
		}

		String string2 = "State: " + string;
		LiveAnimNode liveAnimNode;
		for (Iterator iterator = this.m_liveAnimNodes.iterator(); iterator.hasNext(); string2 = string2 + "\n  Node: " + liveAnimNode.getSourceNode().m_Name) {
			liveAnimNode = (LiveAnimNode)iterator.next();
		}

		AnimationMultiTrack animationMultiTrack = this.getAnimationTrack();
		if (animationMultiTrack != null) {
			string2 = string2 + "\n  AnimTrack:";
			AnimationTrack animationTrack;
			for (Iterator iterator2 = animationMultiTrack.getTracks().iterator(); iterator2.hasNext(); string2 = string2 + "\n	Anim: " + animationTrack.name + " Weight: " + animationTrack.BlendDelta) {
				animationTrack = (AnimationTrack)iterator2.next();
			}
		}

		return string2;
	}

	public void Reset() {
		AnimationMultiTrack animationMultiTrack = this.getAnimationTrack();
		for (int int1 = this.m_liveAnimNodes.size() - 1; int1 >= 0; --int1) {
			LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
			liveAnimNode.setActive(false);
			if (animationMultiTrack != null) {
				animationMultiTrack.removeTracks(liveAnimNode.m_AnimationTracks);
			}

			((LiveAnimNode)this.m_liveAnimNodes.remove(int1)).release();
		}

		this.m_State = null;
	}

	public boolean TransitionTo(AnimState animState, boolean boolean1) {
		AnimationMultiTrack animationMultiTrack = this.getAnimationTrack();
		if (animationMultiTrack == null) {
			if (this.m_Character == null) {
				DebugLog.General.error("AnimationTrack is null. Character is null.");
				this.m_State = null;
				return false;
			} else if (this.m_Character.getAnimationPlayer() == null) {
				DebugLog.General.error("AnimationTrack is null. Character ModelInstance.AnimPlayer is null.");
				this.m_State = null;
				return false;
			} else {
				DebugLog.General.error("AnimationTrack is null. Unknown reason.");
				return false;
			}
		} else if (animState == this.m_State && !boolean1) {
			return true;
		} else {
			if (DebugOptions.instance.Animation.AnimLayer.LogStateChanges.getValue()) {
				String string = this.m_parentLayer == null ? "" : AnimState.getStateName(this.m_parentLayer.m_State) + " | ";
				String string2 = String.format("State: %s%s => %s", string, AnimState.getStateName(this.m_State), AnimState.getStateName(animState));
				DebugLog.General.debugln(string2);
				if (this.m_Character instanceof IsoGameCharacter) {
					((IsoGameCharacter)this.m_Character).setSayLine(string2);
				}
			}

			this.m_State = animState;
			for (int int1 = 0; int1 < this.m_liveAnimNodes.size(); ++int1) {
				LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
				liveAnimNode.m_TransitioningOut = true;
			}

			return true;
		}
	}

	public void Update() {
		GameProfiler.getInstance().invokeAndMeasure("AnimLayer.Update", this, AnimLayer::updateInternal);
	}

	private void updateInternal() {
		float float1 = GameTime.instance.getTimeDelta();
		this.removeFadedOutNodes();
		this.updateNodeActiveFlags();
		LiveAnimNode liveAnimNode = this.getHighestLiveNode();
		this.m_currentSyncNode = liveAnimNode;
		this.m_currentSyncTrack = null;
		if (liveAnimNode != null) {
			int int1 = 0;
			for (int int2 = this.m_liveAnimNodes.size(); int1 < int2; ++int1) {
				LiveAnimNode liveAnimNode2 = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
				liveAnimNode2.update(float1);
			}

			IAnimatable iAnimatable = this.m_Character;
			this.updateMaximumTwist(iAnimatable);
			boolean boolean1 = DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.getValue() && iAnimatable.getVariableBoolean("dbgForceAnim") && iAnimatable.getVariableBoolean("dbgForceAnimScalars");
			String string = boolean1 ? iAnimatable.getVariableString("dbgForceAnimNodeName") : null;
			AnimationTrack animationTrack = this.findSyncTrack(liveAnimNode);
			this.m_currentSyncTrack = animationTrack;
			float float2 = animationTrack != null ? animationTrack.getCurrentTimeFraction() : -1.0F;
			int int3 = 0;
			for (int int4 = this.m_liveAnimNodes.size(); int3 < int4; ++int3) {
				LiveAnimNode liveAnimNode3 = (LiveAnimNode)this.m_liveAnimNodes.get(int3);
				float float3 = 1.0F;
				int int5 = 0;
				for (int int6 = liveAnimNode3.getPlayingTrackCount(); int5 < int6; ++int5) {
					AnimationTrack animationTrack2 = liveAnimNode3.getPlayingTrackAt(int5);
					if (animationTrack2.IsPlaying) {
						if (animationTrack != null && animationTrack2.SyncTrackingEnabled && animationTrack2.isLooping() && animationTrack2 != animationTrack) {
							animationTrack2.moveCurrentTimeValueToFraction(float2);
						}

						if (animationTrack2.name.equals(liveAnimNode3.getSourceNode().m_AnimName)) {
							float3 = animationTrack2.getDuration();
							liveAnimNode3.m_NodeAnimTime = animationTrack2.getCurrentTimeValue();
						}
					}
				}

				if (this.m_AnimEventsCallback != null && liveAnimNode3.getSourceNode().m_Events.size() > 0) {
					float float4 = liveAnimNode3.m_NodeAnimTime / float3;
					float float5 = liveAnimNode3.m_PrevNodeAnimTime / float3;
					List list = liveAnimNode3.getSourceNode().m_Events;
					int int7 = 0;
					for (int int8 = list.size(); int7 < int8; ++int7) {
						AnimEvent animEvent = (AnimEvent)list.get(int7);
						if (animEvent.m_Time == AnimEvent.AnimEventTime.Percentage) {
							float float6 = animEvent.m_TimePc;
							if (float5 < float6 && float6 <= float4) {
								this.invokeAnimEvent(animEvent);
							} else {
								if (!liveAnimNode3.isLooped() && float4 < float6) {
									break;
								}

								if (liveAnimNode3.isLooped() && float5 > float4) {
									if (float5 < float6 && float6 <= float4 + 1.0F) {
										this.invokeAnimEvent(animEvent);
									} else if (float5 > float6 && float6 <= float4) {
										this.invokeAnimEvent(animEvent);
									}
								}
							}
						}
					}
				}

				if (liveAnimNode3.getPlayingTrackCount() != 0) {
					boolean boolean2 = boolean1 && StringUtils.equalsIgnoreCase(liveAnimNode3.getSourceNode().m_Name, string);
					String string2 = boolean2 ? "dbgForceScalar" : liveAnimNode3.getSourceNode().m_Scalar;
					String string3 = boolean2 ? "dbgForceScalar2" : liveAnimNode3.getSourceNode().m_Scalar2;
					float float7 = liveAnimNode3.getTransitionInWeight();
					liveAnimNode3.setTransitionInBlendDelta(float7);
					float float8;
					if (liveAnimNode3.m_AnimationTracks.size() > 1) {
						float7 = iAnimatable.getVariableFloat(string2, 0.0F);
						float8 = iAnimatable.getVariableFloat(string3, 0.0F);
						this.applyBlendField(liveAnimNode3, float7, float8);
					} else if (!liveAnimNode3.m_AnimationTracks.isEmpty()) {
						float7 = liveAnimNode3.getWeight();
						float8 = iAnimatable.getVariableFloat(string2, 1.0F);
						((AnimationTrack)liveAnimNode3.m_AnimationTracks.get(0)).BlendDelta = float7 * Math.abs(float8);
					}
				}
			}

			if (this.isRecording()) {
				this.logBlendWeights();
				this.logCurrentState();
			}
		}
	}

	private void updateMaximumTwist(IAnimationVariableSource iAnimationVariableSource) {
		IAnimationVariableSlot iAnimationVariableSlot = iAnimationVariableSource.getVariable("maxTwist");
		if (iAnimationVariableSlot != null) {
			float float1 = iAnimationVariableSlot.getValueFloat();
			float float2 = 0.0F;
			float float3 = 1.0F;
			for (int int1 = this.m_liveAnimNodes.size() - 1; int1 >= 0; --int1) {
				LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
				float float4 = liveAnimNode.getWeight();
				if (float3 <= 0.0F) {
					break;
				}

				float float5 = PZMath.clamp(float4, 0.0F, float3);
				float3 -= float5;
				float float6 = PZMath.clamp(liveAnimNode.getSourceNode().m_maxTorsoTwist, 0.0F, 70.0F);
				float2 += float6 * float5;
			}

			if (float3 > 0.0F) {
				float2 += float1 * float3;
			}

			iAnimationVariableSlot.setValue(float2);
		}
	}

	public void updateNodeActiveFlags() {
		for (int int1 = 0; int1 < this.m_liveAnimNodes.size(); ++int1) {
			LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
			liveAnimNode.setActive(false);
		}

		AnimState animState = this.m_State;
		IAnimatable iAnimatable = this.m_Character;
		if (animState != null && !iAnimatable.getVariableBoolean("AnimLocked")) {
			List list = animState.getAnimNodes(iAnimatable, this.m_reusableAnimNodes);
			int int2 = 0;
			for (int int3 = list.size(); int2 < int3; ++int2) {
				AnimNode animNode = (AnimNode)list.get(int2);
				this.getOrCreateLiveNode(animNode);
			}
		}

		this.updateNewNodeTransitions();
	}

	private void updateNewNodeTransitions() {
		GameProfiler.getInstance().invokeAndMeasure("updateNewNodeTransitions", this, AnimLayer::updateNewNodeTransitionsInternal);
	}

	private void updateNewNodeTransitionsInternal() {
		IAnimatable iAnimatable = this.m_Character;
		int int1 = 0;
		for (int int2 = this.m_liveAnimNodes.size(); int1 < int2; ++int1) {
			LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
			if (liveAnimNode.isNew() && liveAnimNode.wasActivated()) {
				LiveAnimNode liveAnimNode2 = this.findTransitionToNewNode(liveAnimNode, false);
				if (liveAnimNode2 != null) {
					AnimTransition animTransition = liveAnimNode2.findTransitionTo(iAnimatable, liveAnimNode.getName());
					float float1 = animTransition.m_speedScale;
					if (float1 == Float.POSITIVE_INFINITY) {
						float1 = liveAnimNode.getSpeedScale(this.m_Character);
					}

					AnimationTrack animationTrack = null;
					if (!StringUtils.isNullOrWhitespace(animTransition.m_AnimName)) {
						AnimLayer.StartAnimTrackParameters startAnimTrackParameters = AnimLayer.StartAnimTrackParameters.alloc();
						startAnimTrackParameters.subLayerBoneWeights = liveAnimNode2.getSubStateBoneWeights();
						startAnimTrackParameters.speedScale = float1;
						startAnimTrackParameters.deferredBoneName = liveAnimNode2.getDeferredBoneName();
						startAnimTrackParameters.deferredBoneAxis = liveAnimNode2.getDeferredBoneAxis();
						startAnimTrackParameters.priority = liveAnimNode2.getPriority();
						animationTrack = this.startAnimTrack(animTransition.m_AnimName, startAnimTrackParameters);
						startAnimTrackParameters.release();
						if (animationTrack == null) {
							if (DebugLog.isEnabled(DebugType.Animation)) {
								DebugLog.Animation.println("  TransitionTo failed to play transition track: %s -> %s -> %s", liveAnimNode2.getName(), animTransition.m_AnimName, liveAnimNode.getName());
							}

							continue;
						}

						if (DebugLog.isEnabled(DebugType.Animation)) {
							DebugLog.Animation.println("  TransitionTo found: %s -> %s -> %s", liveAnimNode2.getName(), animTransition.m_AnimName, liveAnimNode.getName());
						}
					} else if (DebugLog.isEnabled(DebugType.Animation)) {
						DebugLog.Animation.println("  TransitionTo found: %s -> <no anim> -> %s", liveAnimNode2.getName(), liveAnimNode.getName());
					}

					liveAnimNode.startTransitionIn(liveAnimNode2, animTransition, animationTrack);
					liveAnimNode2.setTransitionOut(animTransition);
				}
			}
		}
	}

	public LiveAnimNode findTransitionToNewNode(LiveAnimNode liveAnimNode, boolean boolean1) {
		LiveAnimNode liveAnimNode2 = null;
		int int1 = 0;
		for (int int2 = this.m_liveAnimNodes.size(); int1 < int2; ++int1) {
			LiveAnimNode liveAnimNode3 = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
			if (liveAnimNode3 != liveAnimNode && (boolean1 || liveAnimNode3.wasDeactivated())) {
				AnimNode animNode = liveAnimNode3.getSourceNode();
				AnimTransition animTransition = animNode.findTransitionTo(this.m_Character, liveAnimNode.getName());
				if (animTransition != null) {
					liveAnimNode2 = liveAnimNode3;
					break;
				}
			}
		}

		if (liveAnimNode2 == null && this.isSubLayer()) {
			liveAnimNode2 = this.m_parentLayer.findTransitionToNewNode(liveAnimNode, true);
		}

		return liveAnimNode2;
	}

	public void removeFadedOutNodes() {
		for (int int1 = this.m_liveAnimNodes.size() - 1; int1 >= 0; --int1) {
			LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
			if (!liveAnimNode.isActive() && (!liveAnimNode.isTransitioningIn() || !(liveAnimNode.getTransitionInWeight() > 0.01F)) && !(liveAnimNode.getWeight() > 0.01F)) {
				this.removeLiveNodeAt(int1);
			}
		}
	}

	public void render() {
		IAnimatable iAnimatable = this.m_Character;
		boolean boolean1 = DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.getValue() && iAnimatable.getVariableBoolean("dbgForceAnim") && iAnimatable.getVariableBoolean("dbgForceAnimScalars");
		String string = boolean1 ? iAnimatable.getVariableString("dbgForceAnimNodeName") : null;
		int int1 = 0;
		for (int int2 = this.m_liveAnimNodes.size(); int1 < int2; ++int1) {
			LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
			if (liveAnimNode.m_AnimationTracks.size() > 1) {
				boolean boolean2 = boolean1 && StringUtils.equalsIgnoreCase(liveAnimNode.getSourceNode().m_Name, string);
				String string2 = boolean2 ? "dbgForceScalar" : liveAnimNode.getSourceNode().m_Scalar;
				String string3 = boolean2 ? "dbgForceScalar2" : liveAnimNode.getSourceNode().m_Scalar2;
				float float1 = iAnimatable.getVariableFloat(string2, 0.0F);
				float float2 = iAnimatable.getVariableFloat(string3, 0.0F);
				if (liveAnimNode.isActive()) {
					liveAnimNode.getSourceNode().m_picker.render(float1, float2);
				}
			}
		}
	}

	private void logBlendWeights() {
		AnimationPlayerRecorder animationPlayerRecorder = this.m_Character.getAnimationPlayer().getRecorder();
		int int1 = 0;
		for (int int2 = this.m_liveAnimNodes.size(); int1 < int2; ++int1) {
			LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
			animationPlayerRecorder.logAnimNode(liveAnimNode);
		}
	}

	private void logCurrentState() {
		AnimationPlayerRecorder animationPlayerRecorder = this.m_Character.getAnimationPlayer().getRecorder();
		animationPlayerRecorder.logAnimState(this.m_State);
	}

	private void removeLiveNodeAt(int int1) {
		LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
		AnimationMultiTrack animationMultiTrack = this.getAnimationTrack();
		animationMultiTrack.removeTracks(liveAnimNode.m_AnimationTracks);
		animationMultiTrack.removeTrack(liveAnimNode.getTransitionInTrack());
		((LiveAnimNode)this.m_liveAnimNodes.remove(int1)).release();
	}

	private void applyBlendField(LiveAnimNode liveAnimNode, float float1, float float2) {
		if (liveAnimNode.isActive()) {
			AnimNode animNode = liveAnimNode.getSourceNode();
			Anim2DBlendPicker anim2DBlendPicker = animNode.m_picker;
			Anim2DBlendPicker.PickResults pickResults = anim2DBlendPicker.Pick(float1, float2);
			Anim2DBlend anim2DBlend = pickResults.node1;
			Anim2DBlend anim2DBlend2 = pickResults.node2;
			Anim2DBlend anim2DBlend3 = pickResults.node3;
			if (Float.isNaN(pickResults.scale1)) {
				pickResults.scale1 = 0.5F;
			}

			if (Float.isNaN(pickResults.scale2)) {
				pickResults.scale2 = 0.5F;
			}

			if (Float.isNaN(pickResults.scale3)) {
				pickResults.scale3 = 0.5F;
			}

			float float3 = pickResults.scale1;
			float float4 = pickResults.scale2;
			float float5 = pickResults.scale3;
			for (int int1 = 0; int1 < liveAnimNode.m_AnimationTracks.size(); ++int1) {
				Anim2DBlend anim2DBlend4 = (Anim2DBlend)animNode.m_2DBlends.get(int1);
				AnimationTrack animationTrack = (AnimationTrack)liveAnimNode.m_AnimationTracks.get(int1);
				if (anim2DBlend4 == anim2DBlend) {
					animationTrack.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack.blendFieldWeight, float3, 0.15F);
				} else if (anim2DBlend4 == anim2DBlend2) {
					animationTrack.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack.blendFieldWeight, float4, 0.15F);
				} else if (anim2DBlend4 == anim2DBlend3) {
					animationTrack.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack.blendFieldWeight, float5, 0.15F);
				} else {
					animationTrack.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack.blendFieldWeight, 0.0F, 0.15F);
				}

				if (animationTrack.blendFieldWeight < 1.0E-4F) {
					animationTrack.blendFieldWeight = 0.0F;
				}

				animationTrack.blendFieldWeight = PZMath.clamp(animationTrack.blendFieldWeight, 0.0F, 1.0F);
			}
		}

		float float6 = liveAnimNode.getWeight();
		for (int int2 = 0; int2 < liveAnimNode.m_AnimationTracks.size(); ++int2) {
			AnimationTrack animationTrack2 = (AnimationTrack)liveAnimNode.m_AnimationTracks.get(int2);
			animationTrack2.BlendDelta = animationTrack2.blendFieldWeight * float6;
		}
	}

	private void getOrCreateLiveNode(AnimNode animNode) {
		LiveAnimNode liveAnimNode = this.findLiveNode(animNode);
		if (liveAnimNode != null) {
			liveAnimNode.setActive(true);
		} else {
			liveAnimNode = LiveAnimNode.alloc(this, animNode, this.getDepth());
			if (animNode.m_2DBlends.size() > 0) {
				int int1 = 0;
				for (int int2 = animNode.m_2DBlends.size(); int1 < int2; ++int1) {
					Anim2DBlend anim2DBlend = (Anim2DBlend)animNode.m_2DBlends.get(int1);
					this.startAnimTrack(anim2DBlend.m_AnimName, liveAnimNode);
				}
			} else {
				this.startAnimTrack(animNode.m_AnimName, liveAnimNode);
			}

			liveAnimNode.setActive(true);
			this.m_liveAnimNodes.add(liveAnimNode);
		}
	}

	private LiveAnimNode findLiveNode(AnimNode animNode) {
		LiveAnimNode liveAnimNode = null;
		int int1 = 0;
		for (int int2 = this.m_liveAnimNodes.size(); int1 < int2; ++int1) {
			LiveAnimNode liveAnimNode2 = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
			if (!liveAnimNode2.m_TransitioningOut) {
				if (liveAnimNode2.getSourceNode() == animNode) {
					liveAnimNode = liveAnimNode2;
					break;
				}

				if (liveAnimNode2.getSourceNode().m_State == animNode.m_State && liveAnimNode2.getSourceNode().m_Name.equals(animNode.m_Name)) {
					liveAnimNode = liveAnimNode2;
					break;
				}
			}
		}

		return liveAnimNode;
	}

	private void startAnimTrack(String string, LiveAnimNode liveAnimNode) {
		AnimNode animNode = liveAnimNode.getSourceNode();
		float float1 = animNode.getSpeedScale(this.m_Character);
		float float2 = Rand.Next(0.0F, 1.0F);
		float float3 = animNode.m_SpeedScaleRandomMultiplierMin;
		float float4 = animNode.m_SpeedScaleRandomMultiplierMax;
		float float5 = PZMath.lerp(float3, float4, float2);
		AnimLayer.StartAnimTrackParameters startAnimTrackParameters = AnimLayer.StartAnimTrackParameters.alloc();
		startAnimTrackParameters.subLayerBoneWeights = animNode.m_SubStateBoneWeights;
		startAnimTrackParameters.syncTrackingEnabled = animNode.m_SyncTrackingEnabled;
		startAnimTrackParameters.speedScale = float1 * float5;
		startAnimTrackParameters.initialWeight = liveAnimNode.getWeight();
		startAnimTrackParameters.isLooped = liveAnimNode.isLooped();
		startAnimTrackParameters.isReversed = animNode.m_AnimReverse;
		startAnimTrackParameters.deferredBoneName = animNode.getDeferredBoneName();
		startAnimTrackParameters.deferredBoneAxis = animNode.getDeferredBoneAxis();
		startAnimTrackParameters.useDeferredRotation = animNode.m_useDeferedRotation;
		startAnimTrackParameters.priority = animNode.getPriority();
		AnimationTrack animationTrack = this.startAnimTrack(string, startAnimTrackParameters);
		startAnimTrackParameters.release();
		if (animationTrack != null) {
			animationTrack.addListener(liveAnimNode);
			liveAnimNode.addMainTrack(animationTrack);
		}
	}

	private AnimationTrack startAnimTrack(String string, AnimLayer.StartAnimTrackParameters startAnimTrackParameters) {
		AnimationPlayer animationPlayer = this.m_Character.getAnimationPlayer();
		if (!animationPlayer.isReady()) {
			return null;
		} else {
			AnimationTrack animationTrack = animationPlayer.play(string, startAnimTrackParameters.isLooped);
			if (animationTrack == null) {
				return null;
			} else {
				SkinningData skinningData = animationPlayer.getSkinningData();
				if (this.isSubLayer()) {
					animationTrack.setBoneWeights(startAnimTrackParameters.subLayerBoneWeights);
					animationTrack.initBoneWeights(skinningData);
				} else {
					animationTrack.setBoneWeights((List)null);
				}

				SkinningBone skinningBone = skinningData.getBone(startAnimTrackParameters.deferredBoneName);
				if (skinningBone == null) {
					DebugLog.Animation.error("Deferred bone not found: \"%s\"", startAnimTrackParameters.deferredBoneName);
				}

				animationTrack.SpeedDelta = startAnimTrackParameters.speedScale;
				animationTrack.SyncTrackingEnabled = startAnimTrackParameters.syncTrackingEnabled;
				animationTrack.setDeferredBone(skinningBone, startAnimTrackParameters.deferredBoneAxis);
				animationTrack.setUseDeferredRotation(startAnimTrackParameters.useDeferredRotation);
				animationTrack.BlendDelta = startAnimTrackParameters.initialWeight;
				animationTrack.setLayerIdx(this.getDepth());
				animationTrack.reverse = startAnimTrackParameters.isReversed;
				animationTrack.priority = startAnimTrackParameters.priority;
				animationTrack.addListener(this);
				return animationTrack;
			}
		}
	}

	public int getDepth() {
		return this.m_parentLayer != null ? this.m_parentLayer.getDepth() + 1 : 0;
	}

	private LiveAnimNode getHighestLiveNode() {
		if (this.m_liveAnimNodes.isEmpty()) {
			return null;
		} else {
			LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(0);
			for (int int1 = this.m_liveAnimNodes.size() - 1; int1 >= 0; --int1) {
				LiveAnimNode liveAnimNode2 = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
				if (liveAnimNode2.getWeight() > liveAnimNode.getWeight()) {
					liveAnimNode = liveAnimNode2;
				}
			}

			return liveAnimNode;
		}
	}

	private AnimationTrack findSyncTrack(LiveAnimNode liveAnimNode) {
		AnimationTrack animationTrack = null;
		if (this.m_parentLayer != null) {
			animationTrack = this.m_parentLayer.getCurrentSyncTrack();
			if (animationTrack != null) {
				return animationTrack;
			}
		}

		int int1 = 0;
		for (int int2 = liveAnimNode.getPlayingTrackCount(); int1 < int2; ++int1) {
			AnimationTrack animationTrack2 = liveAnimNode.getPlayingTrackAt(int1);
			if (animationTrack2.SyncTrackingEnabled && animationTrack2.hasClip() && (animationTrack == null || animationTrack2.BlendDelta > animationTrack.BlendDelta)) {
				animationTrack = animationTrack2;
			}
		}

		return animationTrack;
	}

	public String getDebugNodeName() {
		String string = this.m_Character.getAdvancedAnimator().animSet.m_Name;
		if (this.m_State != null) {
			string = string + "/" + this.m_State.m_Name;
			if (this.m_CurrentNode != null) {
				string = string + "/" + this.m_CurrentNode.m_Name + ": " + this.m_CurrentNode.m_AnimName;
			} else if (!this.m_liveAnimNodes.isEmpty()) {
				for (int int1 = 0; int1 < this.m_liveAnimNodes.size(); ++int1) {
					LiveAnimNode liveAnimNode = (LiveAnimNode)this.m_liveAnimNodes.get(int1);
					if (this.m_State.m_Nodes.contains(liveAnimNode.getSourceNode())) {
						string = string + "/" + liveAnimNode.getName();
						break;
					}
				}
			}
		}

		return string;
	}

	public List getLiveAnimNodes() {
		return this.m_liveAnimNodes;
	}

	public boolean isRecording() {
		return this.m_Character.getAdvancedAnimator().isRecording();
	}

	static  {
		s_activeAnimLoopedEvent.m_TimePc = 1.0F;
		s_activeAnimLoopedEvent.m_EventName = "ActiveAnimLooped";
		s_activeNonLoopedAnimFadeOutEvent = new AnimEvent();
		s_activeNonLoopedAnimFadeOutEvent.m_TimePc = 1.0F;
		s_activeNonLoopedAnimFadeOutEvent.m_EventName = "NonLoopedAnimFadeOut";
		s_activeAnimFinishingEvent = new AnimEvent();
		s_activeAnimFinishingEvent.m_Time = AnimEvent.AnimEventTime.End;
		s_activeAnimFinishingEvent.m_EventName = "ActiveAnimFinishing";
		s_activeNonLoopedAnimFinishedEvent = new AnimEvent();
		s_activeNonLoopedAnimFinishedEvent.m_Time = AnimEvent.AnimEventTime.End;
		s_activeNonLoopedAnimFinishedEvent.m_EventName = "ActiveAnimFinished";
	}

	private static class StartAnimTrackParameters extends PooledObject {
		public int priority;
		List subLayerBoneWeights;
		boolean syncTrackingEnabled;
		float speedScale;
		float initialWeight;
		boolean isLooped;
		boolean isReversed;
		String deferredBoneName;
		BoneAxis deferredBoneAxis;
		boolean useDeferredRotation;
		private static final Pool s_pool = new Pool(AnimLayer.StartAnimTrackParameters::new);

		private void reset() {
			this.priority = 0;
			this.subLayerBoneWeights = null;
			this.syncTrackingEnabled = false;
			this.speedScale = 1.0F;
			this.initialWeight = 0.0F;
			this.isLooped = false;
			this.isReversed = false;
			this.deferredBoneName = null;
			this.deferredBoneAxis = BoneAxis.Y;
			this.useDeferredRotation = false;
		}

		public void onReleased() {
			this.reset();
		}

		protected StartAnimTrackParameters() {
		}

		public static AnimLayer.StartAnimTrackParameters alloc() {
			return (AnimLayer.StartAnimTrackParameters)s_pool.alloc();
		}
	}
}
