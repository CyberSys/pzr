package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.Stack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import zombie.GameTime;
import zombie.core.skinnedmodel.model.SkinningData;


public class AnimationPlayer {
	public Matrix4f propTransforms = new Matrix4f();
	public Matrix4f[] boneTransforms;
	public Matrix4f[] worldTransforms;
	public Matrix4f[] skinTransforms;
	public SkinningData skinningDataValue;
	public float angle;
	public float targetAngle;
	public float angleStep = 0.15F;
	private static Matrix4f tempMatrix4f = new Matrix4f();
	int propBone = -1;
	public static Stack freeTracks = new Stack();
	private static Keyframe tempKeyframe;
	private static Quaternionf tempQuat = new Quaternionf();
	private static Vector3f tempVec3f = new Vector3f();
	public ArrayList Tracks = new ArrayList();
	static Matrix4f Identity = new Matrix4f();

	public AnimationPlayer(SkinningData skinningData) {
		if (skinningData == null) {
			throw new NullPointerException("skinningData");
		} else {
			this.skinningDataValue = skinningData;
			if (this.skinningDataValue.BoneIndices.containsKey("Bip01_Prop1")) {
				this.propBone = (Integer)this.skinningDataValue.BoneIndices.get("Bip01_Prop1");
			}

			this.boneTransforms = new Matrix4f[skinningData.BindPose.size()];
			this.worldTransforms = new Matrix4f[skinningData.BindPose.size()];
			this.skinTransforms = new Matrix4f[skinningData.BindPose.size()];
			for (int int1 = 0; int1 < skinningData.BindPose.size(); ++int1) {
				this.boneTransforms[int1] = new Matrix4f();
				this.worldTransforms[int1] = new Matrix4f();
				this.skinTransforms[int1] = new Matrix4f();
			}
		}
	}

	public Matrix4f GetPropBoneMatrix() {
		return this.propTransforms;
	}

	public AnimationTrack StartClip(AnimationClip animationClip, boolean boolean1, boolean boolean2, boolean boolean3) {
		if (animationClip == null) {
			return null;
		} else {
			AnimationTrack animationTrack;
			if (this.Tracks.size() > 0) {
				for (int int1 = 0; int1 < this.Tracks.size(); ++int1) {
					animationTrack = (AnimationTrack)this.Tracks.get(int1);
					if (animationTrack.CurrentClip != animationClip) {
						animationTrack.mode = AnimationTrack.Mode.Out;
					}
				}
			}

			AnimationTrack animationTrack2 = this.getTrackPlaying(animationClip);
			if (animationTrack2 != null) {
				if (animationTrack2.mode == AnimationTrack.Mode.Out) {
					animationTrack2.mode = AnimationTrack.Mode.In;
					animationTrack2.currentKeyframe = 0;
					animationTrack2.currentTimeValue = 0.0F;
					animationTrack2.BlendCurrentTime = animationTrack2.BlendTime - animationTrack2.BlendCurrentTime;
					animationTrack2.bFinished = false;
				}

				animationTrack2.bAnim = !boolean2;
				animationTrack2.StopOnFrameOneAfterLoop = boolean3;
				animationTrack2.bLooping = boolean1;
				return animationTrack2;
			} else {
				animationTrack = freeTracks.isEmpty() ? new AnimationTrack() : (AnimationTrack)freeTracks.pop();
				animationTrack.StartClip(animationClip, boolean1, boolean2);
				animationTrack.bAnim = !boolean2;
				this.Tracks.add(animationTrack);
				return animationTrack;
			}
		}
	}

	private AnimationTrack getTrackPlaying(AnimationClip animationClip) {
		for (int int1 = 0; int1 < this.Tracks.size(); ++int1) {
			AnimationTrack animationTrack = (AnimationTrack)this.Tracks.get(int1);
			if (animationTrack.CurrentClip == animationClip) {
				return animationTrack;
			}
		}

		return null;
	}

	private boolean isPlaying(AnimationClip animationClip) {
		for (int int1 = 0; int1 < this.Tracks.size(); ++int1) {
			AnimationTrack animationTrack = (AnimationTrack)this.Tracks.get(int1);
			if (animationTrack.CurrentClip == animationClip) {
				return true;
			}
		}

		return false;
	}

	public void Update(float float1, boolean boolean1, Matrix4f matrix4f) {
		long long1 = System.nanoTime();
		float float2 = this.angle - this.targetAngle;
		float float3 = this.angle - (this.targetAngle + 6.2831855F);
		float float4 = this.angle - (this.targetAngle - 6.2831855F);
		for (int int1 = 0; int1 < this.Tracks.size(); ++int1) {
			AnimationTrack animationTrack = (AnimationTrack)this.Tracks.get(int1);
			animationTrack.Update(this, float1, boolean1, matrix4f);
			if (animationTrack.bFinished) {
				this.Tracks.remove(int1);
				freeTracks.push(animationTrack.reset());
				--int1;
			}
		}

		if (this.angle != this.targetAngle) {
			if (Math.abs(float2) <= Math.abs(float3) && Math.abs(float2) <= Math.abs(float4)) {
				if (this.angle < this.targetAngle) {
					this.angle += this.angleStep * GameTime.instance.getMultiplier();
					if (this.angle > this.targetAngle) {
						this.angle = this.targetAngle;
					}
				}

				if (this.angle > this.targetAngle) {
					this.angle -= this.angleStep * GameTime.instance.getMultiplier();
					if (this.angle < this.targetAngle) {
						this.angle = this.targetAngle;
					}
				}

				if ((double)this.angle > 6.283185307179586) {
					this.angle = (float)((double)this.angle - 6.283185307179586);
				}

				if (this.angle < 0.0F) {
					this.angle = (float)((double)this.angle + 6.283185307179586);
				}
			} else {
				float float5;
				float float6;
				if (Math.abs(float3) < Math.abs(float2) && Math.abs(float3) < Math.abs(float4)) {
					float5 = float3 < 0.0F ? 1.0F : -1.0F;
					float5 *= this.angleStep * GameTime.instance.getMultiplier();
					float6 = this.angle;
					this.angle += float5;
				} else if (Math.abs(float4) < Math.abs(float2) && Math.abs(float4) < Math.abs(float3)) {
					float5 = float4 < 0.0F ? 1.0F : -1.0F;
					float5 *= this.angleStep * GameTime.instance.getMultiplier();
					float6 = this.angle;
					this.angle += float5;
				}
			}

			if ((double)this.angle > 6.283185307179586) {
				this.angle = (float)((double)this.angle - 6.283185307179586);
			} else if (this.angle < 0.0F) {
				this.angle = (float)((double)this.angle + 6.283185307179586);
			}
		}

		try {
			this.UpdateBoneTransforms(float1, boolean1);
			this.UpdateWorldTransforms(matrix4f);
			this.UpdateSkinTransforms();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		this.propTransforms.set((Matrix4fc)this.worldTransforms[this.propBone]);
	}

	public void UpdateBoneTransforms(float float1, boolean boolean1) {
		if (this.Tracks.isEmpty()) {
			throw new RuntimeException("AnimationPlayer.Update was called before StartClip");
		} else {
			for (int int1 = 0; int1 < this.boneTransforms.length; ++int1) {
				Keyframe keyframe = this.interpolateKeyframe(int1);
				this.boneTransforms[int1].rotation((Quaternionfc)keyframe.Rotation).transpose();
				tempMatrix4f.translation(keyframe.Position).transpose();
				this.boneTransforms[int1].mulGeneric(tempMatrix4f, this.boneTransforms[int1]);
			}
		}
	}

	public static final Vector3f slerp(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3, float float1, boolean boolean1) {
		float float2 = vector3f3.x - vector3f2.x;
		float float3 = vector3f3.y - vector3f2.y;
		float float4 = vector3f3.z - vector3f2.z;
		float2 *= float1;
		float3 *= float1;
		float4 *= float1;
		vector3f.set(vector3f2.x + float2, vector3f2.y + float3, vector3f2.z + float4);
		return vector3f;
	}

	public static final Quaternionf slerp(Quaternionf quaternionf, Quaternionf quaternionf2, Quaternionf quaternionf3, float float1, boolean boolean1) {
		float float2 = quaternionf2.dot(quaternionf3);
		double double1;
		double double2;
		if (1.0 - (double)Math.abs(float2) < 0.01) {
			double1 = (double)(1.0F - float1);
			double2 = (double)float1;
		} else {
			double double3 = Math.acos((double)Math.abs(float2));
			double double4 = Math.sin(double3);
			double1 = Math.sin(double3 * (double)(1.0F - float1)) / double4;
			double2 = Math.sin(double3 * (double)float1) / double4;
		}

		if (boolean1 && (double)float2 < 0.0) {
			double1 = -double1;
		}

		quaternionf.set((float)(double1 * (double)quaternionf2.x + double2 * (double)quaternionf3.x), (float)(double1 * (double)quaternionf2.y + double2 * (double)quaternionf3.y), (float)(double1 * (double)quaternionf2.z + double2 * (double)quaternionf3.z), (float)(double1 * (double)quaternionf2.w + double2 * (double)quaternionf3.w));
		return quaternionf;
	}

	private Keyframe interpolateKeyframe(int int1) {
		if (tempKeyframe == null) {
			tempKeyframe = new Keyframe();
			tempKeyframe.Rotation = new Quaternionf();
			tempKeyframe.Position = new Vector3f();
		}

		Keyframe keyframe = tempKeyframe;
		((AnimationTrack)this.Tracks.get(0)).getRotation(keyframe.Rotation, int1);
		((AnimationTrack)this.Tracks.get(0)).getPosition(keyframe.Position, int1);
		for (int int2 = 1; int2 < this.Tracks.size(); ++int2) {
			AnimationTrack animationTrack = (AnimationTrack)this.Tracks.get(int2);
			float float1 = animationTrack.BlendDelta;
			keyframe.Rotation = slerp(keyframe.Rotation, keyframe.Rotation, animationTrack.getRotation(tempQuat, int1), float1, true);
			keyframe.Position = slerp(keyframe.Position, keyframe.Position, animationTrack.getPosition(tempVec3f, int1), float1, true);
		}

		return keyframe;
	}

	public void UpdateWorldTransforms(Matrix4f matrix4f) {
		Identity.identity();
		tempVec3f.set(0.0F, 1.0F, 0.0F);
		Identity.rotate(-this.angle, tempVec3f);
		this.boneTransforms[0].mul((Matrix4fc)Identity, this.worldTransforms[0]);
		for (int int1 = 1; int1 < this.worldTransforms.length; ++int1) {
			int int2 = (Integer)this.skinningDataValue.SkeletonHierarchy.get(int1);
			this.boneTransforms[int1].mul((Matrix4fc)this.worldTransforms[int2], this.worldTransforms[int1]);
		}
	}

	public void UpdateSkinTransforms() {
		for (int int1 = 0; int1 < this.worldTransforms.length; ++int1) {
			((Matrix4f)this.skinningDataValue.BoneOffset.get(int1)).mul((Matrix4fc)this.worldTransforms[int1], this.skinTransforms[int1]);
		}
	}

	public void ResetToFrameOne() {
		if (!this.Tracks.isEmpty()) {
			((AnimationTrack)this.Tracks.get(0)).bAnim = true;
			((AnimationTrack)this.Tracks.get(0)).currentKeyframe = 0;
			((AnimationTrack)this.Tracks.get(0)).currentTimeValue = 0.0F;
		}
	}

	public AnimationTrack getAnimTrack(String string) {
		for (int int1 = 0; int1 < this.Tracks.size(); ++int1) {
			AnimationTrack animationTrack = (AnimationTrack)this.Tracks.get(int1);
			if (animationTrack.CurrentClip.Name.equals(string)) {
				return animationTrack;
			}
		}

		return null;
	}
}
