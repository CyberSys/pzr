package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.PerformanceSettings;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.HelperFunctions;


@Deprecated
public class StaticAnimation {
	private int framesPerSecond;
	public String name;
	public Matrix4f[][] Matrices;
	private Matrix4f[] RootMotion;
	public AnimationClip Clip;
	private int currentKeyframe = 0;
	private float currentTimeValue = 0.0F;
	private Keyframe[] Pose;
	private Keyframe[] PrevPose;
	private float lastTime = 0.0F;

	public StaticAnimation(AnimationClip animationClip) {
		this.Clip = animationClip;
		this.framesPerSecond = PerformanceSettings.BaseStaticAnimFramerate;
		this.Matrices = new Matrix4f[(int)((float)this.framesPerSecond * this.Clip.Duration)][60];
		this.RootMotion = new Matrix4f[(int)((float)this.framesPerSecond * this.Clip.Duration)];
		this.Pose = new Keyframe[60];
		this.PrevPose = new Keyframe[60];
		this.Create();
		Arrays.fill(this.Pose, (Object)null);
		this.Pose = null;
		Arrays.fill(this.PrevPose, (Object)null);
		this.PrevPose = null;
	}

	private Keyframe getNextKeyFrame(int int1, int int2, Keyframe keyframe) {
		Keyframe[] keyframeArray = this.Clip.getKeyframes();
		for (int int3 = int2; int3 < keyframeArray.length; ++int3) {
			Keyframe keyframe2 = keyframeArray[int3];
			if (keyframe2.Bone == int1 && keyframe2.Time > this.currentTimeValue && keyframe != keyframe2) {
				return keyframe2;
			}
		}

		return null;
	}

	public Quaternion getRotation(Quaternion quaternion, int int1) {
		if (this.PrevPose[int1] != null && PerformanceSettings.InterpolateAnims) {
			float float1 = (this.currentTimeValue - this.PrevPose[int1].Time) / (this.Pose[int1].Time - this.PrevPose[int1].Time);
			if (this.Pose[int1].Time - this.PrevPose[int1].Time == 0.0F) {
				float1 = 0.0F;
			}

			return PZMath.slerp(quaternion, this.PrevPose[int1].Rotation, this.Pose[int1].Rotation, float1);
		} else {
			quaternion.set(this.Pose[int1].Rotation);
			return quaternion;
		}
	}

	public Vector3f getPosition(Vector3f vector3f, int int1) {
		if (this.PrevPose[int1] != null && PerformanceSettings.InterpolateAnims) {
			float float1 = (this.currentTimeValue - this.PrevPose[int1].Time) / (this.Pose[int1].Time - this.PrevPose[int1].Time);
			if (this.Pose[int1].Time - this.PrevPose[int1].Time == 0.0F) {
				float1 = 0.0F;
			}

			PZMath.lerp(vector3f, this.PrevPose[int1].Position, this.Pose[int1].Position, float1);
			return vector3f;
		} else {
			vector3f.set(this.Pose[int1].Position);
			return vector3f;
		}
	}

	public void getPose() {
		Keyframe[] keyframeArray = this.Clip.getKeyframes();
		for (this.currentKeyframe = 0; this.currentKeyframe < keyframeArray.length; ++this.currentKeyframe) {
			Keyframe keyframe = keyframeArray[this.currentKeyframe];
			if (this.currentKeyframe == keyframeArray.length - 1 || !(keyframe.Time <= this.currentTimeValue)) {
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

			this.lastTime = keyframe.Time;
		}
	}

	public void Create() {
		float float1 = (float)this.Matrices.length;
		double double1 = (double)this.Clip.Duration / (double)float1;
		double double2 = 0.0;
		int int1 = 0;
		for (Matrix4f matrix4f = new Matrix4f(); (float)int1 < float1; ++int1) {
			this.currentTimeValue = (float)double2;
			this.getPose();
			for (int int2 = 0; int2 < 60; ++int2) {
				if (this.Pose[int2] == null) {
					this.Matrices[int1][int2] = matrix4f;
				} else {
					Quaternion quaternion = new Quaternion();
					this.getRotation(quaternion, int2);
					Vector3f vector3f = new Vector3f();
					this.getPosition(vector3f, int2);
					Matrix4f matrix4f2 = HelperFunctions.CreateFromQuaternionPositionScale(vector3f, quaternion, new Vector3f(1.0F, 1.0F, 1.0F), new Matrix4f());
					this.Matrices[int1][int2] = matrix4f2;
				}
			}

			double2 += double1;
		}
	}

	public Keyframe interpolate(List list, float float1) {
		int int1 = 0;
		Keyframe keyframe = null;
		Keyframe keyframe2;
		for (Object object = null; int1 < list.size(); keyframe = keyframe2) {
			keyframe2 = (Keyframe)list.get(int1);
			if (keyframe2.Time > float1 && keyframe.Time <= float1) {
				Quaternion quaternion = new Quaternion();
				Vector3f vector3f = new Vector3f();
				float float2 = (float1 - keyframe.Time) / (keyframe2.Time - keyframe.Time);
				PZMath.slerp(quaternion, keyframe.Rotation, keyframe2.Rotation, float2);
				PZMath.lerp(vector3f, keyframe.Position, keyframe2.Position, float2);
				Keyframe keyframe3 = new Keyframe();
				keyframe3.Position = vector3f;
				keyframe3.Rotation = quaternion;
				keyframe3.Scale = new Vector3f(1.0F, 1.0F, 1.0F);
				keyframe3.Time = keyframe.Time + (keyframe2.Time - keyframe.Time) * float2;
				return keyframe3;
			}

			++int1;
		}

		return (Keyframe)list.get(list.size() - 1);
	}

	public void interpolate(List list) {
		if (!list.isEmpty()) {
			if (!((Keyframe)list.get(0)).Position.equals(((Keyframe)list.get(list.size() - 1)).Position)) {
				float float1 = (float)(this.Matrices.length + 1);
				double double1 = (double)this.Clip.Duration / (double)float1;
				double double2 = 0.0;
				ArrayList arrayList = new ArrayList();
				for (int int1 = 0; (float)int1 < float1 - 1.0F; double2 += double1) {
					Keyframe keyframe = this.interpolate(list, (float)double2);
					arrayList.add(keyframe);
					++int1;
				}

				list.clear();
				list.addAll(arrayList);
			}
		}
	}

	public void doRootMotion(List list) {
		float float1 = (float)this.Matrices.length;
		if (list.size() > 3) {
			for (int int1 = 0; (float)int1 < float1 && int1 < list.size(); ++int1) {
				Keyframe keyframe = (Keyframe)list.get(int1);
				Quaternion quaternion = keyframe.Rotation;
				Vector3f vector3f = keyframe.Position;
				Matrix4f matrix4f = HelperFunctions.CreateFromQuaternionPositionScale(vector3f, quaternion, keyframe.Scale, new Matrix4f());
				this.RootMotion[int1] = matrix4f;
			}
		}
	}
}
