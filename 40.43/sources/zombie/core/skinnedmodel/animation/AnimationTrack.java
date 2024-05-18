package zombie.core.skinnedmodel.animation;

import java.util.List;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.GameTime;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.iso.sprite.IsoAnim;
import zombie.iso.sprite.IsoSpriteInstance;


public class AnimationTrack {
	public AnimationClip CurrentClip;
	public float currentTimeValue;
	int currentKeyframe;
	public boolean reverse;
	public boolean bLooping = true;
	public Keyframe[] Pose = new Keyframe[60];
	public static Keyframe[] NextPose = new Keyframe[60];
	public Keyframe[] PrevPose = new Keyframe[60];
	public float SpeedDelta = 1.0F;
	public float BlendDelta = 0.0F;
	public float BlendTime = 0.1F;
	public float BlendCurrentTime = 0.0F;
	public AnimationTrack.Mode mode;
	public boolean bFinished;
	public boolean bAnim;
	public boolean StopOnFrameOneAfterLoop;

	public AnimationTrack() {
		this.mode = AnimationTrack.Mode.In;
		this.bFinished = false;
		this.bAnim = true;
		this.StopOnFrameOneAfterLoop = false;
	}

	public void syncToFrame(IsoSpriteInstance spriteInstance, IsoAnim anim) {
		this.currentTimeValue = spriteInstance.Frame / (float)anim.Frames.size();
		this.currentTimeValue *= this.CurrentClip.Duration;
		this.currentKeyframe = 0;
	}

	public Quaternionf getRotation(Quaternionf quaternionf, int int1) {
		if (this.PrevPose[int1] != null && PerformanceSettings.InterpolateAnims) {
			float float1 = (this.currentTimeValue - this.PrevPose[int1].Time) / (this.Pose[int1].Time - this.PrevPose[int1].Time);
			if (this.Pose[int1].Time - this.PrevPose[int1].Time == 0.0F) {
				float1 = 0.0F;
			}

			return AnimationPlayer.slerp(quaternionf, this.PrevPose[int1].Rotation, this.Pose[int1].Rotation, float1, true);
		} else {
			quaternionf.set((Quaternionfc)this.Pose[int1].Rotation);
			return quaternionf;
		}
	}

	public Vector3f getPosition(Vector3f vector3f, int int1) {
		if (this.PrevPose[int1] != null && PerformanceSettings.InterpolateAnims) {
			float float1 = (this.currentTimeValue - this.PrevPose[int1].Time) / (this.Pose[int1].Time - this.PrevPose[int1].Time);
			if (this.Pose[int1].Time - this.PrevPose[int1].Time == 0.0F) {
				float1 = 0.0F;
			}

			AnimationPlayer.slerp(vector3f, this.PrevPose[int1].Position, this.Pose[int1].Position, float1, true);
			return vector3f;
		} else {
			vector3f.set((Vector3fc)this.Pose[int1].Position);
			return vector3f;
		}
	}

	public float get2DFrame(IsoAnim anim) {
		return this.currentTimeValue / this.CurrentClip.Duration * (float)anim.Frames.size();
	}

	public void Update(AnimationPlayer animationPlayer, float float1, boolean boolean1, Matrix4f matrix4f) {
		float float2 = float1 * GameTime.instance.getUnmoddedMultiplier();
		float1 *= this.CurrentClip.Duration * GameTime.instance.getUnmoddedMultiplier();
		float1 *= this.SpeedDelta;
		try {
			this.UpdateKeyframes(float1, float2, boolean1);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void UpdateKeyframes(float float1, float float2, boolean boolean1) {
		if (this.CurrentClip == null) {
			throw new RuntimeException("AnimationPlayer.Update was called before StartClip");
		} else {
			if (this.mode == AnimationTrack.Mode.In || this.mode == AnimationTrack.Mode.Out) {
				this.BlendCurrentTime += float2;
				if (this.BlendCurrentTime > this.BlendTime) {
					if (this.mode == AnimationTrack.Mode.In) {
						this.BlendCurrentTime = 0.0F;
						this.mode = AnimationTrack.Mode.During;
					} else {
						this.BlendCurrentTime = this.BlendTime;
						this.bFinished = true;
					}
				}
			}

			if (this.mode == AnimationTrack.Mode.In) {
				this.BlendDelta = this.BlendCurrentTime / this.BlendTime;
			} else if (this.mode == AnimationTrack.Mode.Out) {
				this.BlendDelta = 1.0F - this.BlendCurrentTime / this.BlendTime;
				if (this.BlendDelta < 0.15F) {
					boolean boolean2 = false;
				}
			} else {
				this.BlendDelta = 1.0F;
			}

			if (!this.bAnim) {
				float1 = 0.0F;
			}

			if (boolean1) {
				if (this.reverse) {
					float1 = -float1;
					float1 += this.currentTimeValue;
				} else {
					float1 += this.currentTimeValue;
				}
			}

			if (!this.bAnim && this.StopOnFrameOneAfterLoop) {
				float1 = 0.0F;
			} else if (!this.bAnim && !this.StopOnFrameOneAfterLoop) {
				float1 = this.CurrentClip.Duration;
			}

			if (!this.bLooping) {
				if (float1 >= this.CurrentClip.Duration) {
					if (this.StopOnFrameOneAfterLoop) {
						float1 = 0.0F;
					} else {
						float1 = this.CurrentClip.Duration;
					}
				} else if (float1 < 0.0F) {
					if (this.StopOnFrameOneAfterLoop) {
						float1 = this.CurrentClip.Duration;
					} else {
						float1 = 0.0F;
					}
				}
			} else {
				while (float1 >= this.CurrentClip.Duration) {
					float1 -= this.CurrentClip.Duration;
				}

				while (float1 < 0.0F) {
					float1 += this.CurrentClip.Duration;
				}
			}

			if (this.reverse) {
				this.currentKeyframe = 0;
			} else if (float1 < this.currentTimeValue) {
				this.currentKeyframe = 0;
			}

			this.currentTimeValue = float1;
			for (List list = this.CurrentClip.Keyframes; this.currentKeyframe < list.size(); ++this.currentKeyframe) {
				Keyframe keyframe = (Keyframe)list.get(this.currentKeyframe);
				if (this.currentKeyframe == list.size() - 1 || keyframe.Time > this.currentTimeValue) {
					if (PerformanceSettings.InterpolateAnims) {
						for (int int1 = 0; int1 < 60; ++int1) {
							if (this.Pose[int1] == null || this.currentTimeValue >= this.Pose[int1].Time) {
								Keyframe keyframe2 = this.getNextKeyFrame(int1, this.currentKeyframe, this.Pose[int1]);
								if (keyframe2 != null) {
									this.PrevPose[keyframe2.Bone] = this.Pose[keyframe2.Bone];
									this.Pose[keyframe2.Bone] = keyframe2;
								} else {
									this.PrevPose[int1] = null;
								}
							}
						}
					}

					break;
				}

				if (keyframe.Bone >= 0) {
					this.Pose[keyframe.Bone] = keyframe;
				}
			}
		}
	}

	private Keyframe getNextKeyFrame(int int1, int int2, Keyframe keyframe) {
		for (int int3 = int2; int3 < this.CurrentClip.KeyframeArray.length; ++int3) {
			Keyframe keyframe2 = this.CurrentClip.KeyframeArray[int3];
			if (keyframe2.Bone == int1 && keyframe2.Time > this.currentTimeValue && keyframe != keyframe2) {
				return keyframe2;
			}
		}

		return null;
	}

	public void StartClip(AnimationClip animationClip, boolean boolean1, boolean boolean2) {
		if (animationClip != null) {
			if (this.CurrentClip != animationClip) {
				this.bLooping = boolean1;
				this.CurrentClip = animationClip;
				this.currentTimeValue = 0.0F;
				this.currentKeyframe = 0;
				this.bAnim = !boolean2;
				if (animationClip.Name.contains("ZombieWalk")) {
					this.currentTimeValue = (float)Rand.Next((int)animationClip.Duration * 10000) / 10000.0F * animationClip.Duration;
				}
			}
		}
	}

	public AnimationTrack reset() {
		this.CurrentClip = null;
		this.currentTimeValue = 0.0F;
		this.currentKeyframe = 0;
		this.reverse = false;
		this.bLooping = true;
		for (int int1 = 0; int1 < this.Pose.length; ++int1) {
			this.Pose[int1] = null;
			this.PrevPose[int1] = null;
		}

		this.SpeedDelta = 1.0F;
		this.BlendDelta = 0.0F;
		this.BlendTime = 0.1F;
		this.BlendCurrentTime = 0.0F;
		this.mode = AnimationTrack.Mode.In;
		this.bFinished = false;
		this.bAnim = true;
		this.StopOnFrameOneAfterLoop = false;
		return this;
	}
	public static enum Mode {

		In,
		During,
		Out;
	}
}
