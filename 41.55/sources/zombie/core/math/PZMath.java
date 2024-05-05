package zombie.core.math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.debug.DebugLog;
import zombie.iso.Vector2;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;


public final class PZMath {
	public static final float PI = 3.1415927F;
	public static final float PI2 = 6.2831855F;
	public static final float degToRads = 0.017453292F;
	public static final float radToDegs = 57.295776F;
	public static final long microsToNanos = 1000L;
	public static final long millisToMicros = 1000L;
	public static final long secondsToMillis = 1000L;
	public static long secondsToNanos = 1000000000L;

	public static float clamp(float float1, float float2, float float3) {
		float float4 = float1;
		if (float1 < float2) {
			float4 = float2;
		}

		if (float4 > float3) {
			float4 = float3;
		}

		return float4;
	}

	public static int clamp(int int1, int int2, int int3) {
		int int4 = int1;
		if (int1 < int2) {
			int4 = int2;
		}

		if (int4 > int3) {
			int4 = int3;
		}

		return int4;
	}

	public static float clamp_01(float float1) {
		return clamp(float1, 0.0F, 1.0F);
	}

	public static float lerp(float float1, float float2, float float3) {
		return float1 + (float2 - float1) * float3;
	}

	public static float lerpAngle(float float1, float float2, float float3) {
		float float4 = getClosestAngle(float1, float2);
		float float5 = float1 + float3 * float4;
		return wrap(float5, -3.1415927F, 3.1415927F);
	}

	public static Vector3f lerp(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3, float float1) {
		vector3f.set(vector3f2.x + (vector3f3.x - vector3f2.x) * float1, vector3f2.y + (vector3f3.y - vector3f2.y) * float1, vector3f2.z + (vector3f3.z - vector3f2.z) * float1);
		return vector3f;
	}

	public static Vector2 lerp(Vector2 vector2, Vector2 vector22, Vector2 vector23, float float1) {
		vector2.set(vector22.x + (vector23.x - vector22.x) * float1, vector22.y + (vector23.y - vector22.y) * float1);
		return vector2;
	}

	public static float c_lerp(float float1, float float2, float float3) {
		float float4 = (float)(1.0 - Math.cos((double)(float3 * 3.1415927F))) / 2.0F;
		return float1 * (1.0F - float4) + float2 * float4;
	}

	public static Quaternion slerp(Quaternion quaternion, Quaternion quaternion2, Quaternion quaternion3, float float1) {
		double double1 = (double)(quaternion2.x * quaternion3.x + quaternion2.y * quaternion3.y + quaternion2.z * quaternion3.z + quaternion2.w * quaternion3.w);
		double double2 = double1 < 0.0 ? -double1 : double1;
		double double3 = (double)(1.0F - float1);
		double double4 = (double)float1;
		if (1.0 - double2 > 0.1) {
			double double5 = org.joml.Math.acos(double2);
			double double6 = org.joml.Math.sin(double5);
			double double7 = 1.0 / double6;
			double3 = org.joml.Math.sin(double5 * (1.0 - (double)float1)) * double7;
			double4 = org.joml.Math.sin(double5 * (double)float1) * double7;
		}

		if (double1 < 0.0) {
			double4 = -double4;
		}

		quaternion.set((float)(double3 * (double)quaternion2.x + double4 * (double)quaternion3.x), (float)(double3 * (double)quaternion2.y + double4 * (double)quaternion3.y), (float)(double3 * (double)quaternion2.z + double4 * (double)quaternion3.z), (float)(double3 * (double)quaternion2.w + double4 * (double)quaternion3.w));
		return quaternion;
	}

	public static float sqrt(float float1) {
		return org.joml.Math.sqrt(float1);
	}

	public static float lerpFunc_EaseOutQuad(float float1) {
		return float1 * float1;
	}

	public static float lerpFunc_EaseInQuad(float float1) {
		float float2 = 1.0F - float1;
		return 1.0F - float2 * float2;
	}

	public static float lerpFunc_EaseOutInQuad(float float1) {
		return float1 < 0.5F ? lerpFunc_EaseOutQuad(float1) * 2.0F : 0.5F + lerpFunc_EaseInQuad(2.0F * float1 - 1.0F) / 2.0F;
	}

	public static float tryParseFloat(String string, float float1) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return float1;
		} else {
			try {
				return Float.parseFloat(string.trim());
			} catch (NumberFormatException numberFormatException) {
				return float1;
			}
		}
	}

	public static boolean canParseFloat(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return false;
		} else {
			try {
				Float.parseFloat(string.trim());
				return true;
			} catch (NumberFormatException numberFormatException) {
				return false;
			}
		}
	}

	public static int tryParseInt(String string, int int1) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return int1;
		} else {
			try {
				return Integer.parseInt(string.trim());
			} catch (NumberFormatException numberFormatException) {
				return int1;
			}
		}
	}

	public static float degToRad(float float1) {
		return 0.017453292F * float1;
	}

	public static float radToDeg(float float1) {
		return 57.295776F * float1;
	}

	public static float getClosestAngle(float float1, float float2) {
		float float3 = wrap(float1, 6.2831855F);
		float float4 = wrap(float2, 6.2831855F);
		float float5 = float4 - float3;
		float float6 = wrap(float5, -3.1415927F, 3.1415927F);
		return float6;
	}

	public static float getClosestAngleDegrees(float float1, float float2) {
		float float3 = degToRad(float1);
		float float4 = degToRad(float2);
		float float5 = getClosestAngle(float3, float4);
		return radToDeg(float5);
	}

	public static int sign(float float1) {
		return float1 > 0.0F ? 1 : (float1 < 0.0F ? -1 : 0);
	}

	public static float floor(float float1) {
		return float1 >= 0.0F ? (float)((int)(float1 + 1.0E-7F)) : (float)((int)(float1 - 0.9999999F));
	}

	public static float ceil(float float1) {
		return float1 >= 0.0F ? (float)((int)(float1 + 0.9999999F)) : (float)((int)(float1 - 1.0E-7F));
	}

	public static float frac(float float1) {
		float float2 = floor(float1);
		float float3 = float1 - float2;
		return float3;
	}

	public static float wrap(float float1, float float2) {
		if (float2 == 0.0F) {
			return 0.0F;
		} else if (float2 < 0.0F) {
			return 0.0F;
		} else {
			float float3;
			float float4;
			float float5;
			if (float1 < 0.0F) {
				float3 = -float1 / float2;
				float4 = 1.0F - frac(float3);
				float5 = float4 * float2;
				return float5;
			} else {
				float3 = float1 / float2;
				float4 = frac(float3);
				float5 = float4 * float2;
				return float5;
			}
		}
	}

	public static float wrap(float float1, float float2, float float3) {
		float float4 = max(float3, float2);
		float float5 = min(float3, float2);
		float float6 = float4 - float5;
		float float7 = float1 - float5;
		float float8 = wrap(float7, float6);
		float float9 = float5 + float8;
		return float9;
	}

	public static float max(float float1, float float2) {
		return float1 > float2 ? float1 : float2;
	}

	public static int max(int int1, int int2) {
		return int1 > int2 ? int1 : int2;
	}

	public static float min(float float1, float float2) {
		return float1 > float2 ? float2 : float1;
	}

	public static int min(int int1, int int2) {
		return int1 > int2 ? int2 : int1;
	}

	public static float abs(float float1) {
		return float1 * (float)sign(float1);
	}

	public static boolean equal(float float1, float float2) {
		return equal(float1, float2, 1.0E-7F);
	}

	public static boolean equal(float float1, float float2, float float3) {
		float float4 = float2 - float1;
		float float5 = abs(float4);
		return float5 < float3;
	}

	public static Matrix4f convertMatrix(org.joml.Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (matrix4f2 == null) {
			matrix4f2 = new Matrix4f();
		}

		matrix4f2.m00 = matrix4f.m00();
		matrix4f2.m01 = matrix4f.m01();
		matrix4f2.m02 = matrix4f.m02();
		matrix4f2.m03 = matrix4f.m03();
		matrix4f2.m10 = matrix4f.m10();
		matrix4f2.m11 = matrix4f.m11();
		matrix4f2.m12 = matrix4f.m12();
		matrix4f2.m13 = matrix4f.m13();
		matrix4f2.m20 = matrix4f.m20();
		matrix4f2.m21 = matrix4f.m21();
		matrix4f2.m22 = matrix4f.m22();
		matrix4f2.m23 = matrix4f.m23();
		matrix4f2.m30 = matrix4f.m30();
		matrix4f2.m31 = matrix4f.m31();
		matrix4f2.m32 = matrix4f.m32();
		matrix4f2.m33 = matrix4f.m33();
		return matrix4f2;
	}

	public static org.joml.Matrix4f convertMatrix(Matrix4f matrix4f, org.joml.Matrix4f matrix4f2) {
		if (matrix4f2 == null) {
			matrix4f2 = new org.joml.Matrix4f();
		}

		return matrix4f2.set(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m03, matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m13, matrix4f.m20, matrix4f.m21, matrix4f.m22, matrix4f.m23, matrix4f.m30, matrix4f.m31, matrix4f.m32, matrix4f.m33);
	}

	public static float step(float float1, float float2, float float3) {
		if (float1 > float2) {
			return max(float1 + float3, float2);
		} else {
			return float1 < float2 ? min(float1 + float3, float2) : float1;
		}
	}

	public static PZMath.SideOfLine testSideOfLine(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = (float5 - float1) * (float4 - float2) - (float6 - float2) * (float3 - float1);
		return float7 > 0.0F ? PZMath.SideOfLine.Left : (float7 < 0.0F ? PZMath.SideOfLine.Right : PZMath.SideOfLine.OnLine);
	}

	public static float roundToNearest(float float1) {
		int int1 = sign(float1);
		return floor(float1 + 0.5F * (float)int1);
	}

	public static int roundToInt(float float1) {
		return (int)(roundToNearest(float1) + 1.0E-4F);
	}

	static  {
		PZMath.UnitTests.runAll();
	}

	public static enum SideOfLine {

		Left,
		OnLine,
		Right;

		private static PZMath.SideOfLine[] $values() {
			return new PZMath.SideOfLine[]{Left, OnLine, Right};
		}
	}

	private static final class UnitTests {
		private static final Runnable[] s_unitTests = new Runnable[0];

		private static void runAll() {
			PZArrayUtil.forEach((Object[])s_unitTests, Runnable::run);
		}

		public static final class vector2 {

			public static void run() {
				runUnitTest_direction();
			}

			private static void runUnitTest_direction() {
				DebugLog.General.println("runUnitTest_direction");
				DebugLog.General.println("x, y, angle, length, rdir.x, rdir.y, rangle, rlength, pass");
				checkDirection(1.0F, 0.0F);
				checkDirection(1.0F, 1.0F);
				checkDirection(0.0F, 1.0F);
				checkDirection(-1.0F, 1.0F);
				checkDirection(-1.0F, 0.0F);
				checkDirection(-1.0F, -1.0F);
				checkDirection(0.0F, -1.0F);
				checkDirection(1.0F, -1.0F);
				DebugLog.General.println("runUnitTest_direction. Complete");
			}

			private static void checkDirection(float float1, float float2) {
				Vector2 vector2 = new Vector2(float1, float2);
				float float3 = vector2.getDirection();
				float float4 = vector2.getLength();
				Vector2 vector22 = Vector2.fromLengthDirection(float4, float3);
				float float5 = vector22.getDirection();
				float float6 = vector22.getLength();
				boolean boolean1 = PZMath.equal(vector2.x, vector22.x, 1.0E-4F) && PZMath.equal(vector2.y, vector22.y, 1.0E-4F) && PZMath.equal(float3, float5, 1.0E-4F) && PZMath.equal(float4, float6, 1.0E-4F);
				DebugLog.General.println("%f, %f, %f, %f, %f, %f, %f, %f, %s", float1, float2, float3, float4, vector22.x, vector22.y, float5, float6, boolean1 ? "true" : "false");
			}
		}

		private static final class getClosestAngle {

			public static void run() {
				DebugLog.General.println("runUnitTests_getClosestAngle");
				DebugLog.General.println("a, b, result, expected, pass");
				runUnitTest(0.0F, 0.0F, 0.0F);
				runUnitTest(0.0F, 15.0F, 15.0F);
				runUnitTest(15.0F, 0.0F, -15.0F);
				runUnitTest(0.0F, 179.0F, 179.0F);
				runUnitTest(180.0F, 180.0F, 0.0F);
				runUnitTest(180.0F, 359.0F, 179.0F);
				runUnitTest(90.0F, 180.0F, 90.0F);
				runUnitTest(180.0F, 90.0F, -90.0F);
				for (int int1 = -360; int1 < 360; int1 += 10) {
					for (int int2 = -360; int2 < 360; int2 += 10) {
						float float1 = (float)int1;
						float float2 = (float)int2;
						runUnitTest_noexp(float1, float2);
					}
				}

				DebugLog.General.println("runUnitTests_getClosestAngle. Complete");
			}

			private static void runUnitTest_noexp(float float1, float float2) {
				float float3 = PZMath.getClosestAngleDegrees(float1, float2);
				logResult(float1, float2, float3, "N/A", "N/A");
			}

			private static void runUnitTest(float float1, float float2, float float3) {
				float float4 = PZMath.getClosestAngleDegrees(float1, float2);
				boolean boolean1 = PZMath.equal(float3, float4, 1.0E-4F);
				String string = boolean1 ? "pass" : "fail";
				logResult(float1, float2, float4, String.valueOf(float3), string);
			}

			private static void logResult(float float1, float float2, float float3, String string, String string2) {
				DebugLog.General.println("%f, %f, %f, %s, %s", float1, float2, float3, string, string2);
			}
		}

		private static final class lerpFunctions {

			public static void run() {
				DebugLog.General.println("UnitTest_lerpFunctions");
				DebugLog.General.println("x,Sqrt,EaseOutQuad,EaseInQuad,EaseOutInQuad");
				for (int int1 = 0; int1 < 100; ++int1) {
					float float1 = (float)int1 / 100.0F;
					DebugLog.General.println("%f,%f,%f,%f", float1, PZMath.lerpFunc_EaseOutQuad(float1), PZMath.lerpFunc_EaseInQuad(float1), PZMath.lerpFunc_EaseOutInQuad(float1));
				}

				DebugLog.General.println("UnitTest_lerpFunctions. Complete");
			}
		}
	}
}
