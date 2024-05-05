package org.joml;


public class Interpolationf {

	public static float interpolateTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11) {
		float float12 = float5 - float8;
		float float13 = float7 - float4;
		float float14 = float1 - float7;
		float float15 = float11 - float8;
		float float16 = float10 - float7;
		float float17 = float2 - float8;
		float float18 = 1.0F / (float12 * float14 + float13 * float17);
		float float19 = (float12 * float16 + float13 * float15) * float18;
		float float20 = (float14 * float15 - float17 * float16) * float18;
		return float19 * float3 + float20 * float6 + (1.0F - float19 - float20) * float9;
	}

	public static Vector2f interpolateTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, Vector2f vector2f) {
		float float15 = float6 - float10;
		float float16 = float9 - float5;
		float float17 = float1 - float9;
		float float18 = float14 - float10;
		float float19 = float13 - float9;
		float float20 = float2 - float10;
		float float21 = 1.0F / (float15 * float17 + float16 * float20);
		float float22 = (float15 * float19 + float16 * float18) * float21;
		float float23 = (float17 * float18 - float20 * float19) * float21;
		float float24 = 1.0F - float22 - float23;
		vector2f.x = float22 * float3 + float23 * float7 + float24 * float11;
		vector2f.y = float22 * float4 + float23 * float8 + float24 * float12;
		return vector2f;
	}

	public static Vector2f dFdxLinear(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, Vector2f vector2f) {
		float float13 = float6 - float10;
		float float14 = float2 - float10;
		float float15 = float13 * (float1 - float9) + (float9 - float5) * float14;
		float float16 = float15 - float13 + float14;
		float float17 = 1.0F / float15;
		vector2f.x = float17 * (float13 * float3 - float14 * float7 + float16 * float11) - float11;
		vector2f.y = float17 * (float13 * float4 - float14 * float8 + float16 * float12) - float12;
		return vector2f;
	}

	public static Vector2f dFdyLinear(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, Vector2f vector2f) {
		float float13 = float9 - float5;
		float float14 = float1 - float9;
		float float15 = (float6 - float10) * float14 + float13 * (float2 - float10);
		float float16 = float15 - float13 - float14;
		float float17 = 1.0F / float15;
		vector2f.x = float17 * (float13 * float3 + float14 * float7 + float16 * float11) - float11;
		vector2f.y = float17 * (float13 * float4 + float14 * float8 + float16 * float12) - float12;
		return vector2f;
	}

	public static Vector3f interpolateTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, float float17, Vector3f vector3f) {
		interpolationFactorsTriangle(float1, float2, float6, float7, float11, float12, float16, float17, vector3f);
		return vector3f.set(vector3f.x * float3 + vector3f.y * float8 + vector3f.z * float13, vector3f.x * float4 + vector3f.y * float9 + vector3f.z * float14, vector3f.x * float5 + vector3f.y * float10 + vector3f.z * float15);
	}

	public static Vector3f interpolationFactorsTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Vector3f vector3f) {
		float float9 = float4 - float6;
		float float10 = float5 - float3;
		float float11 = float1 - float5;
		float float12 = float8 - float6;
		float float13 = float7 - float5;
		float float14 = float2 - float6;
		float float15 = 1.0F / (float9 * float11 + float10 * float14);
		vector3f.x = (float9 * float13 + float10 * float12) * float15;
		vector3f.y = (float11 * float12 - float14 * float13) * float15;
		vector3f.z = 1.0F - vector3f.x - vector3f.y;
		return vector3f;
	}
}
