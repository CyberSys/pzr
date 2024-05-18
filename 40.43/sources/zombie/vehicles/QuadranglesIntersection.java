package zombie.vehicles;

import org.joml.Vector4f;
import zombie.iso.Vector2;


public class QuadranglesIntersection {
	private static final float EPS = 0.001F;

	public static boolean IsQuadranglesAreIntersected(Vector2[] vector2Array, Vector2[] vector2Array2) {
		if (vector2Array != null && vector2Array2 != null && vector2Array.length == 4 && vector2Array2.length == 4) {
			if (lineIntersection(vector2Array[0], vector2Array[1], vector2Array2[0], vector2Array2[1])) {
				return true;
			} else if (lineIntersection(vector2Array[0], vector2Array[1], vector2Array2[1], vector2Array2[2])) {
				return true;
			} else if (lineIntersection(vector2Array[0], vector2Array[1], vector2Array2[2], vector2Array2[3])) {
				return true;
			} else if (lineIntersection(vector2Array[0], vector2Array[1], vector2Array2[3], vector2Array2[0])) {
				return true;
			} else if (lineIntersection(vector2Array[1], vector2Array[2], vector2Array2[0], vector2Array2[1])) {
				return true;
			} else if (lineIntersection(vector2Array[1], vector2Array[2], vector2Array2[1], vector2Array2[2])) {
				return true;
			} else if (lineIntersection(vector2Array[1], vector2Array[2], vector2Array2[2], vector2Array2[3])) {
				return true;
			} else if (lineIntersection(vector2Array[1], vector2Array[2], vector2Array2[3], vector2Array2[0])) {
				return true;
			} else if (lineIntersection(vector2Array[2], vector2Array[3], vector2Array2[0], vector2Array2[1])) {
				return true;
			} else if (lineIntersection(vector2Array[2], vector2Array[3], vector2Array2[1], vector2Array2[2])) {
				return true;
			} else if (lineIntersection(vector2Array[2], vector2Array[3], vector2Array2[2], vector2Array2[3])) {
				return true;
			} else if (lineIntersection(vector2Array[2], vector2Array[3], vector2Array2[3], vector2Array2[0])) {
				return true;
			} else if (lineIntersection(vector2Array[3], vector2Array[0], vector2Array2[0], vector2Array2[1])) {
				return true;
			} else if (lineIntersection(vector2Array[3], vector2Array[0], vector2Array2[1], vector2Array2[2])) {
				return true;
			} else if (lineIntersection(vector2Array[3], vector2Array[0], vector2Array2[2], vector2Array2[3])) {
				return true;
			} else if (lineIntersection(vector2Array[3], vector2Array[0], vector2Array2[3], vector2Array2[0])) {
				return true;
			} else if (!IsPointInTriangle(vector2Array[0], vector2Array2[0], vector2Array2[1], vector2Array2[2]) && !IsPointInTriangle(vector2Array[0], vector2Array2[0], vector2Array2[2], vector2Array2[3])) {
				return IsPointInTriangle(vector2Array2[0], vector2Array[0], vector2Array[1], vector2Array[2]) || IsPointInTriangle(vector2Array2[0], vector2Array[0], vector2Array[2], vector2Array[3]);
			} else {
				return true;
			}
		} else {
			System.out.println("ERROR: IsQuadranglesAreIntersected");
			return false;
		}
	}

	public static boolean IsPointInTriangle(Vector2 vector2, Vector2[] vector2Array) {
		return IsPointInTriangle(vector2, vector2Array[0], vector2Array[1], vector2Array[2]) || IsPointInTriangle(vector2, vector2Array[0], vector2Array[2], vector2Array[3]);
	}

	public static float det(float float1, float float2, float float3, float float4) {
		return float1 * float4 - float2 * float3;
	}

	private static boolean between(float float1, float float2, double double1) {
		return (double)Math.min(float1, float2) <= double1 + 0.0010000000474974513 && double1 <= (double)(Math.max(float1, float2) + 0.001F);
	}

	private static boolean intersect_1(float float1, float float2, float float3, float float4) {
		float float5;
		float float6;
		if (float1 > float2) {
			float6 = float1;
			float5 = float2;
		} else {
			float5 = float1;
			float6 = float2;
		}

		float float7;
		float float8;
		if (float3 > float4) {
			float8 = float3;
			float7 = float4;
		} else {
			float7 = float3;
			float8 = float4;
		}

		return Math.max(float5, float7) <= Math.min(float6, float8);
	}

	public static boolean lineIntersection(Vector2 vector2, Vector2 vector22, Vector2 vector23, Vector2 vector24) {
		float float1 = vector2.y - vector22.y;
		float float2 = vector22.x - vector2.x;
		float float3 = -float1 * vector2.x - float2 * vector2.y;
		float float4 = vector23.y - vector24.y;
		float float5 = vector24.x - vector23.x;
		float float6 = -float4 * vector23.x - float5 * vector23.y;
		float float7 = det(float1, float2, float4, float5);
		if (float7 != 0.0F) {
			double double1 = (double)(-det(float3, float2, float6, float5)) * 1.0 / (double)float7;
			double double2 = (double)(-det(float1, float3, float4, float6)) * 1.0 / (double)float7;
			return between(vector2.x, vector22.x, double1) && between(vector2.y, vector22.y, double2) && between(vector23.x, vector24.x, double1) && between(vector23.y, vector24.y, double2);
		} else {
			return det(float1, float3, float4, float6) == 0.0F && det(float2, float3, float5, float6) == 0.0F && intersect_1(vector2.x, vector22.x, vector23.x, vector24.x) && intersect_1(vector2.y, vector22.y, vector23.y, vector24.y);
		}
	}

	public static boolean IsQuadranglesAreTransposed2(Vector4f vector4f, Vector4f vector4f2) {
		if (IsPointInQuadrilateral(new Vector2(vector4f.x, vector4f.y), vector4f2.x, vector4f2.z, vector4f2.y, vector4f2.w)) {
			return true;
		} else if (IsPointInQuadrilateral(new Vector2(vector4f.z, vector4f.y), vector4f2.x, vector4f2.z, vector4f2.y, vector4f2.w)) {
			return true;
		} else if (IsPointInQuadrilateral(new Vector2(vector4f.x, vector4f.w), vector4f2.x, vector4f2.z, vector4f2.y, vector4f2.w)) {
			return true;
		} else if (IsPointInQuadrilateral(new Vector2(vector4f.z, vector4f.w), vector4f2.x, vector4f2.z, vector4f2.y, vector4f2.w)) {
			return true;
		} else if (IsPointInQuadrilateral(new Vector2(vector4f2.x, vector4f2.y), vector4f.x, vector4f.z, vector4f.y, vector4f.w)) {
			return true;
		} else if (IsPointInQuadrilateral(new Vector2(vector4f2.z, vector4f2.y), vector4f.x, vector4f.z, vector4f.y, vector4f.w)) {
			return true;
		} else if (IsPointInQuadrilateral(new Vector2(vector4f2.x, vector4f2.w), vector4f.x, vector4f.z, vector4f.y, vector4f.w)) {
			return true;
		} else {
			return IsPointInQuadrilateral(new Vector2(vector4f2.z, vector4f2.w), vector4f.x, vector4f.z, vector4f.y, vector4f.w);
		}
	}

	private static boolean IsPointInQuadrilateral(Vector2 vector2, float float1, float float2, float float3, float float4) {
		if (IsPointInTriangle(vector2, new Vector2(float1, float3), new Vector2(float1, float4), new Vector2(float2, float4))) {
			return true;
		} else {
			return IsPointInTriangle(vector2, new Vector2(float2, float4), new Vector2(float2, float3), new Vector2(float1, float3));
		}
	}

	private static boolean IsPointInTriangle(Vector2 vector2, Vector2 vector22, Vector2 vector23, Vector2 vector24) {
		float float1 = (vector22.x - vector2.x) * (vector23.y - vector22.y) - (vector23.x - vector22.x) * (vector22.y - vector2.y);
		float float2 = (vector23.x - vector2.x) * (vector24.y - vector23.y) - (vector24.x - vector23.x) * (vector23.y - vector2.y);
		float float3 = (vector24.x - vector2.x) * (vector22.y - vector24.y) - (vector22.x - vector24.x) * (vector24.y - vector2.y);
		return float1 >= 0.0F && float2 >= 0.0F && float3 >= 0.0F || float1 <= 0.0F && float2 <= 0.0F && float3 <= 0.0F;
	}
}
