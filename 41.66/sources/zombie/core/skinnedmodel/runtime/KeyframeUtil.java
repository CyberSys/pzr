package zombie.core.skinnedmodel.runtime;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.skinnedmodel.animation.Keyframe;


public final class KeyframeUtil {
	static final Quaternion end = new Quaternion();

	public static Vector3f GetKeyFramePosition(Keyframe[] keyframeArray, float float1, double double1) {
		Vector3f vector3f = new Vector3f();
		if (keyframeArray.length == 0) {
			return vector3f;
		} else {
			int int1;
			for (int1 = 0; int1 < keyframeArray.length - 1 && !(float1 < keyframeArray[int1 + 1].Time); ++int1) {
			}

			int int2 = (int1 + 1) % keyframeArray.length;
			Keyframe keyframe = keyframeArray[int1];
			Keyframe keyframe2 = keyframeArray[int2];
			float float2 = keyframe.Time;
			float float3 = keyframe2.Time;
			float float4 = float3 - float2;
			if (float4 < 0.0F) {
				float4 = (float)((double)float4 + double1);
			}

			if (float4 > 0.0F) {
				float float5 = float3 - float2;
				float float6 = float1 - float2;
				float6 /= float5;
				float float7 = keyframe.Position.x;
				float float8 = keyframe2.Position.x;
				float float9 = float7 + float6 * (float8 - float7);
				float float10 = keyframe.Position.y;
				float float11 = keyframe2.Position.y;
				float float12 = float10 + float6 * (float11 - float10);
				float float13 = keyframe.Position.z;
				float float14 = keyframe2.Position.z;
				float float15 = float13 + float6 * (float14 - float13);
				vector3f.set(float9, float12, float15);
			} else {
				vector3f.set(keyframe.Position);
			}

			return vector3f;
		}
	}

	public static Quaternion GetKeyFrameRotation(Keyframe[] keyframeArray, float float1, double double1) {
		Quaternion quaternion = new Quaternion();
		if (keyframeArray.length == 0) {
			return quaternion;
		} else {
			int int1;
			for (int1 = 0; int1 < keyframeArray.length - 1 && !(float1 < keyframeArray[int1 + 1].Time); ++int1) {
			}

			int int2 = (int1 + 1) % keyframeArray.length;
			Keyframe keyframe = keyframeArray[int1];
			Keyframe keyframe2 = keyframeArray[int2];
			float float2 = keyframe.Time;
			float float3 = keyframe2.Time;
			float float4 = float3 - float2;
			if (float4 < 0.0F) {
				float4 = (float)((double)float4 + double1);
			}

			if (float4 > 0.0F) {
				float float5 = (float1 - float2) / float4;
				Quaternion quaternion2 = keyframe.Rotation;
				Quaternion quaternion3 = keyframe2.Rotation;
				double double2 = (double)(quaternion2.getX() * quaternion3.getX() + quaternion2.getY() * quaternion3.getY() + quaternion2.getZ() * quaternion3.getZ() + quaternion2.getW() * quaternion3.getW());
				end.set(quaternion3);
				if (double2 < 0.0) {
					double2 *= -1.0;
					end.setX(-end.getX());
					end.setY(-end.getY());
					end.setZ(-end.getZ());
					end.setW(-end.getW());
				}

				double double3;
				double double4;
				if (1.0 - double2 > 1.0E-4) {
					double double5 = Math.acos(double2);
					double double6 = Math.sin(double5);
					double3 = Math.sin((1.0 - (double)float5) * double5) / double6;
					double4 = Math.sin((double)float5 * double5) / double6;
				} else {
					double3 = 1.0 - (double)float5;
					double4 = (double)float5;
				}

				quaternion.set((float)(double3 * (double)quaternion2.getX() + double4 * (double)end.getX()), (float)(double3 * (double)quaternion2.getY() + double4 * (double)end.getY()), (float)(double3 * (double)quaternion2.getZ() + double4 * (double)end.getZ()), (float)(double3 * (double)quaternion2.getW() + double4 * (double)end.getW()));
			} else {
				quaternion.set(keyframe.Rotation);
			}

			return quaternion;
		}
	}
}
