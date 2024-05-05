package org.joml;


public class SimplexNoise {
	private static final SimplexNoise.Vector3b[] grad3 = new SimplexNoise.Vector3b[]{new SimplexNoise.Vector3b(1, 1, 0), new SimplexNoise.Vector3b(-1, 1, 0), new SimplexNoise.Vector3b(1, -1, 0), new SimplexNoise.Vector3b(-1, -1, 0), new SimplexNoise.Vector3b(1, 0, 1), new SimplexNoise.Vector3b(-1, 0, 1), new SimplexNoise.Vector3b(1, 0, -1), new SimplexNoise.Vector3b(-1, 0, -1), new SimplexNoise.Vector3b(0, 1, 1), new SimplexNoise.Vector3b(0, -1, 1), new SimplexNoise.Vector3b(0, 1, -1), new SimplexNoise.Vector3b(0, -1, -1)};
	private static final SimplexNoise.Vector4b[] grad4 = new SimplexNoise.Vector4b[]{new SimplexNoise.Vector4b(0, 1, 1, 1), new SimplexNoise.Vector4b(0, 1, 1, -1), new SimplexNoise.Vector4b(0, 1, -1, 1), new SimplexNoise.Vector4b(0, 1, -1, -1), new SimplexNoise.Vector4b(0, -1, 1, 1), new SimplexNoise.Vector4b(0, -1, 1, -1), new SimplexNoise.Vector4b(0, -1, -1, 1), new SimplexNoise.Vector4b(0, -1, -1, -1), new SimplexNoise.Vector4b(1, 0, 1, 1), new SimplexNoise.Vector4b(1, 0, 1, -1), new SimplexNoise.Vector4b(1, 0, -1, 1), new SimplexNoise.Vector4b(1, 0, -1, -1), new SimplexNoise.Vector4b(-1, 0, 1, 1), new SimplexNoise.Vector4b(-1, 0, 1, -1), new SimplexNoise.Vector4b(-1, 0, -1, 1), new SimplexNoise.Vector4b(-1, 0, -1, -1), new SimplexNoise.Vector4b(1, 1, 0, 1), new SimplexNoise.Vector4b(1, 1, 0, -1), new SimplexNoise.Vector4b(1, -1, 0, 1), new SimplexNoise.Vector4b(1, -1, 0, -1), new SimplexNoise.Vector4b(-1, 1, 0, 1), new SimplexNoise.Vector4b(-1, 1, 0, -1), new SimplexNoise.Vector4b(-1, -1, 0, 1), new SimplexNoise.Vector4b(-1, -1, 0, -1), new SimplexNoise.Vector4b(1, 1, 1, 0), new SimplexNoise.Vector4b(1, 1, -1, 0), new SimplexNoise.Vector4b(1, -1, 1, 0), new SimplexNoise.Vector4b(1, -1, -1, 0), new SimplexNoise.Vector4b(-1, 1, 1, 0), new SimplexNoise.Vector4b(-1, 1, -1, 0), new SimplexNoise.Vector4b(-1, -1, 1, 0), new SimplexNoise.Vector4b(-1, -1, -1, 0)};
	private static final byte[] p = new byte[]{-105, -96, -119, 91, 90, 15, -125, 13, -55, 95, 96, 53, -62, -23, 7, -31, -116, 36, 103, 30, 69, -114, 8, 99, 37, -16, 21, 10, 23, -66, 6, -108, -9, 120, -22, 75, 0, 26, -59, 62, 94, -4, -37, -53, 117, 35, 11, 32, 57, -79, 33, 88, -19, -107, 56, 87, -82, 20, 125, -120, -85, -88, 68, -81, 74, -91, 71, -122, -117, 48, 27, -90, 77, -110, -98, -25, 83, 111, -27, 122, 60, -45, -123, -26, -36, 105, 92, 41, 55, 46, -11, 40, -12, 102, -113, 54, 65, 25, 63, -95, 1, -40, 80, 73, -47, 76, -124, -69, -48, 89, 18, -87, -56, -60, -121, -126, 116, -68, -97, 86, -92, 100, 109, -58, -83, -70, 3, 64, 52, -39, -30, -6, 124, 123, 5, -54, 38, -109, 118, 126, -1, 82, 85, -44, -49, -50, 59, -29, 47, 16, 58, 17, -74, -67, 28, 42, -33, -73, -86, -43, 119, -8, -104, 2, 44, -102, -93, 70, -35, -103, 101, -101, -89, 43, -84, 9, -127, 22, 39, -3, 19, 98, 108, 110, 79, 113, -32, -24, -78, -71, 112, 104, -38, -10, 97, -28, -5, 34, -14, -63, -18, -46, -112, 12, -65, -77, -94, -15, 81, 51, -111, -21, -7, 14, -17, 107, 49, -64, -42, 31, -75, -57, 106, -99, -72, 84, -52, -80, 115, 121, 50, 45, 127, 4, -106, -2, -118, -20, -51, 93, -34, 114, 67, 29, 24, 72, -13, -115, -128, -61, 78, 66, -41, 61, -100, -76};
	private static final byte[] perm = new byte[512];
	private static final byte[] permMod12 = new byte[512];
	private static final float F2 = 0.36602542F;
	private static final float G2 = 0.21132487F;
	private static final float F3 = 0.33333334F;
	private static final float G3 = 0.16666667F;
	private static final float F4 = 0.309017F;
	private static final float G4 = 0.1381966F;

	private static int fastfloor(float float1) {
		int int1 = (int)float1;
		return float1 < (float)int1 ? int1 - 1 : int1;
	}

	private static float dot(SimplexNoise.Vector3b vector3b, float float1, float float2) {
		return (float)vector3b.x * float1 + (float)vector3b.y * float2;
	}

	private static float dot(SimplexNoise.Vector3b vector3b, float float1, float float2, float float3) {
		return (float)vector3b.x * float1 + (float)vector3b.y * float2 + (float)vector3b.z * float3;
	}

	private static float dot(SimplexNoise.Vector4b vector4b, float float1, float float2, float float3, float float4) {
		return (float)vector4b.x * float1 + (float)vector4b.y * float2 + (float)vector4b.z * float3 + (float)vector4b.w * float4;
	}

	public static float noise(float float1, float float2) {
		float float3 = (float1 + float2) * 0.36602542F;
		int int1 = fastfloor(float1 + float3);
		int int2 = fastfloor(float2 + float3);
		float float4 = (float)(int1 + int2) * 0.21132487F;
		float float5 = (float)int1 - float4;
		float float6 = (float)int2 - float4;
		float float7 = float1 - float5;
		float float8 = float2 - float6;
		byte byte1;
		byte byte2;
		if (float7 > float8) {
			byte1 = 1;
			byte2 = 0;
		} else {
			byte1 = 0;
			byte2 = 1;
		}

		float float9 = float7 - (float)byte1 + 0.21132487F;
		float float10 = float8 - (float)byte2 + 0.21132487F;
		float float11 = float7 - 1.0F + 0.42264974F;
		float float12 = float8 - 1.0F + 0.42264974F;
		int int3 = int1 & 255;
		int int4 = int2 & 255;
		int int5 = permMod12[int3 + perm[int4] & 255] & 255;
		int int6 = permMod12[int3 + byte1 + perm[int4 + byte2] & 255] & 255;
		int int7 = permMod12[int3 + 1 + perm[int4 + 1] & 255] & 255;
		float float13 = 0.5F - float7 * float7 - float8 * float8;
		float float14;
		if (float13 < 0.0F) {
			float14 = 0.0F;
		} else {
			float13 *= float13;
			float14 = float13 * float13 * dot(grad3[int5], float7, float8);
		}

		float float15 = 0.5F - float9 * float9 - float10 * float10;
		float float16;
		if (float15 < 0.0F) {
			float16 = 0.0F;
		} else {
			float15 *= float15;
			float16 = float15 * float15 * dot(grad3[int6], float9, float10);
		}

		float float17 = 0.5F - float11 * float11 - float12 * float12;
		float float18;
		if (float17 < 0.0F) {
			float18 = 0.0F;
		} else {
			float17 *= float17;
			float18 = float17 * float17 * dot(grad3[int7], float11, float12);
		}

		return 70.0F * (float14 + float16 + float18);
	}

	public static float noise(float float1, float float2, float float3) {
		float float4 = (float1 + float2 + float3) * 0.33333334F;
		int int1 = fastfloor(float1 + float4);
		int int2 = fastfloor(float2 + float4);
		int int3 = fastfloor(float3 + float4);
		float float5 = (float)(int1 + int2 + int3) * 0.16666667F;
		float float6 = (float)int1 - float5;
		float float7 = (float)int2 - float5;
		float float8 = (float)int3 - float5;
		float float9 = float1 - float6;
		float float10 = float2 - float7;
		float float11 = float3 - float8;
		byte byte1;
		byte byte2;
		byte byte3;
		byte byte4;
		byte byte5;
		byte byte6;
		if (float9 >= float10) {
			if (float10 >= float11) {
				byte1 = 1;
				byte2 = 0;
				byte3 = 0;
				byte4 = 1;
				byte5 = 1;
				byte6 = 0;
			} else if (float9 >= float11) {
				byte1 = 1;
				byte2 = 0;
				byte3 = 0;
				byte4 = 1;
				byte5 = 0;
				byte6 = 1;
			} else {
				byte1 = 0;
				byte2 = 0;
				byte3 = 1;
				byte4 = 1;
				byte5 = 0;
				byte6 = 1;
			}
		} else if (float10 < float11) {
			byte1 = 0;
			byte2 = 0;
			byte3 = 1;
			byte4 = 0;
			byte5 = 1;
			byte6 = 1;
		} else if (float9 < float11) {
			byte1 = 0;
			byte2 = 1;
			byte3 = 0;
			byte4 = 0;
			byte5 = 1;
			byte6 = 1;
		} else {
			byte1 = 0;
			byte2 = 1;
			byte3 = 0;
			byte4 = 1;
			byte5 = 1;
			byte6 = 0;
		}

		float float12 = float9 - (float)byte1 + 0.16666667F;
		float float13 = float10 - (float)byte2 + 0.16666667F;
		float float14 = float11 - (float)byte3 + 0.16666667F;
		float float15 = float9 - (float)byte4 + 0.33333334F;
		float float16 = float10 - (float)byte5 + 0.33333334F;
		float float17 = float11 - (float)byte6 + 0.33333334F;
		float float18 = float9 - 1.0F + 0.5F;
		float float19 = float10 - 1.0F + 0.5F;
		float float20 = float11 - 1.0F + 0.5F;
		int int4 = int1 & 255;
		int int5 = int2 & 255;
		int int6 = int3 & 255;
		int int7 = permMod12[int4 + perm[int5 + perm[int6] & 255] & 255] & 255;
		int int8 = permMod12[int4 + byte1 + perm[int5 + byte2 + perm[int6 + byte3] & 255] & 255] & 255;
		int int9 = permMod12[int4 + byte4 + perm[int5 + byte5 + perm[int6 + byte6] & 255] & 255] & 255;
		int int10 = permMod12[int4 + 1 + perm[int5 + 1 + perm[int6 + 1] & 255] & 255] & 255;
		float float21 = 0.6F - float9 * float9 - float10 * float10 - float11 * float11;
		float float22;
		if (float21 < 0.0F) {
			float22 = 0.0F;
		} else {
			float21 *= float21;
			float22 = float21 * float21 * dot(grad3[int7], float9, float10, float11);
		}

		float float23 = 0.6F - float12 * float12 - float13 * float13 - float14 * float14;
		float float24;
		if (float23 < 0.0F) {
			float24 = 0.0F;
		} else {
			float23 *= float23;
			float24 = float23 * float23 * dot(grad3[int8], float12, float13, float14);
		}

		float float25 = 0.6F - float15 * float15 - float16 * float16 - float17 * float17;
		float float26;
		if (float25 < 0.0F) {
			float26 = 0.0F;
		} else {
			float25 *= float25;
			float26 = float25 * float25 * dot(grad3[int9], float15, float16, float17);
		}

		float float27 = 0.6F - float18 * float18 - float19 * float19 - float20 * float20;
		float float28;
		if (float27 < 0.0F) {
			float28 = 0.0F;
		} else {
			float27 *= float27;
			float28 = float27 * float27 * dot(grad3[int10], float18, float19, float20);
		}

		return 32.0F * (float22 + float24 + float26 + float28);
	}

	public static float noise(float float1, float float2, float float3, float float4) {
		float float5 = (float1 + float2 + float3 + float4) * 0.309017F;
		int int1 = fastfloor(float1 + float5);
		int int2 = fastfloor(float2 + float5);
		int int3 = fastfloor(float3 + float5);
		int int4 = fastfloor(float4 + float5);
		float float6 = (float)(int1 + int2 + int3 + int4) * 0.1381966F;
		float float7 = (float)int1 - float6;
		float float8 = (float)int2 - float6;
		float float9 = (float)int3 - float6;
		float float10 = (float)int4 - float6;
		float float11 = float1 - float7;
		float float12 = float2 - float8;
		float float13 = float3 - float9;
		float float14 = float4 - float10;
		int int5 = 0;
		int int6 = 0;
		int int7 = 0;
		int int8 = 0;
		if (float11 > float12) {
			++int5;
		} else {
			++int6;
		}

		if (float11 > float13) {
			++int5;
		} else {
			++int7;
		}

		if (float11 > float14) {
			++int5;
		} else {
			++int8;
		}

		if (float12 > float13) {
			++int6;
		} else {
			++int7;
		}

		if (float12 > float14) {
			++int6;
		} else {
			++int8;
		}

		if (float13 > float14) {
			++int7;
		} else {
			++int8;
		}

		int int9 = int5 >= 3 ? 1 : 0;
		int int10 = int6 >= 3 ? 1 : 0;
		int int11 = int7 >= 3 ? 1 : 0;
		int int12 = int8 >= 3 ? 1 : 0;
		int int13 = int5 >= 2 ? 1 : 0;
		int int14 = int6 >= 2 ? 1 : 0;
		int int15 = int7 >= 2 ? 1 : 0;
		int int16 = int8 >= 2 ? 1 : 0;
		int int17 = int5 >= 1 ? 1 : 0;
		int int18 = int6 >= 1 ? 1 : 0;
		int int19 = int7 >= 1 ? 1 : 0;
		int int20 = int8 >= 1 ? 1 : 0;
		float float15 = float11 - (float)int9 + 0.1381966F;
		float float16 = float12 - (float)int10 + 0.1381966F;
		float float17 = float13 - (float)int11 + 0.1381966F;
		float float18 = float14 - (float)int12 + 0.1381966F;
		float float19 = float11 - (float)int13 + 0.2763932F;
		float float20 = float12 - (float)int14 + 0.2763932F;
		float float21 = float13 - (float)int15 + 0.2763932F;
		float float22 = float14 - (float)int16 + 0.2763932F;
		float float23 = float11 - (float)int17 + 0.41458982F;
		float float24 = float12 - (float)int18 + 0.41458982F;
		float float25 = float13 - (float)int19 + 0.41458982F;
		float float26 = float14 - (float)int20 + 0.41458982F;
		float float27 = float11 - 1.0F + 0.5527864F;
		float float28 = float12 - 1.0F + 0.5527864F;
		float float29 = float13 - 1.0F + 0.5527864F;
		float float30 = float14 - 1.0F + 0.5527864F;
		int int21 = int1 & 255;
		int int22 = int2 & 255;
		int int23 = int3 & 255;
		int int24 = int4 & 255;
		int int25 = (perm[int21 + perm[int22 + perm[int23 + perm[int24] & 255] & 255] & 255] & 255) % 32;
		int int26 = (perm[int21 + int9 + perm[int22 + int10 + perm[int23 + int11 + perm[int24 + int12] & 255] & 255] & 255] & 255) % 32;
		int int27 = (perm[int21 + int13 + perm[int22 + int14 + perm[int23 + int15 + perm[int24 + int16] & 255] & 255] & 255] & 255) % 32;
		int int28 = (perm[int21 + int17 + perm[int22 + int18 + perm[int23 + int19 + perm[int24 + int20] & 255] & 255] & 255] & 255) % 32;
		int int29 = (perm[int21 + 1 + perm[int22 + 1 + perm[int23 + 1 + perm[int24 + 1] & 255] & 255] & 255] & 255) % 32;
		float float31 = 0.6F - float11 * float11 - float12 * float12 - float13 * float13 - float14 * float14;
		float float32;
		if (float31 < 0.0F) {
			float32 = 0.0F;
		} else {
			float31 *= float31;
			float32 = float31 * float31 * dot(grad4[int25], float11, float12, float13, float14);
		}

		float float33 = 0.6F - float15 * float15 - float16 * float16 - float17 * float17 - float18 * float18;
		float float34;
		if (float33 < 0.0F) {
			float34 = 0.0F;
		} else {
			float33 *= float33;
			float34 = float33 * float33 * dot(grad4[int26], float15, float16, float17, float18);
		}

		float float35 = 0.6F - float19 * float19 - float20 * float20 - float21 * float21 - float22 * float22;
		float float36;
		if (float35 < 0.0F) {
			float36 = 0.0F;
		} else {
			float35 *= float35;
			float36 = float35 * float35 * dot(grad4[int27], float19, float20, float21, float22);
		}

		float float37 = 0.6F - float23 * float23 - float24 * float24 - float25 * float25 - float26 * float26;
		float float38;
		if (float37 < 0.0F) {
			float38 = 0.0F;
		} else {
			float37 *= float37;
			float38 = float37 * float37 * dot(grad4[int28], float23, float24, float25, float26);
		}

		float float39 = 0.6F - float27 * float27 - float28 * float28 - float29 * float29 - float30 * float30;
		float float40;
		if (float39 < 0.0F) {
			float40 = 0.0F;
		} else {
			float39 *= float39;
			float40 = float39 * float39 * dot(grad4[int29], float27, float28, float29, float30);
		}

		return 27.0F * (float32 + float34 + float36 + float38 + float40);
	}

	static  {
	for (int var0 = 0; var0 < 512; ++var0) {
		perm[var0] = p[var0 & 255];
		permMod12[var0] = (byte)((perm[var0] & 255) % 12);
	}
	}

	private static class Vector3b {
		byte x;
		byte y;
		byte z;

		Vector3b(int int1, int int2, int int3) {
			this.x = (byte)int1;
			this.y = (byte)int2;
			this.z = (byte)int3;
		}
	}

	private static class Vector4b {
		byte x;
		byte y;
		byte z;
		byte w;

		Vector4b(int int1, int int2, int int3, int int4) {
			this.x = (byte)int1;
			this.y = (byte)int2;
			this.z = (byte)int3;
			this.w = (byte)int4;
		}
	}
}
