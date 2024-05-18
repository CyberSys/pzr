package com.jcraft.jorbis;


class Lsp {
	static final float M_PI = 3.1415927F;

	static void lsp_to_curve(float[] floatArray, int[] intArray, int int1, int int2, float[] floatArray2, int int3, float float1, float float2) {
		float float3 = 3.1415927F / (float)int2;
		int int4;
		for (int4 = 0; int4 < int3; ++int4) {
			floatArray2[int4] = Lookup.coslook(floatArray2[int4]);
		}

		int int5 = int3 / 2 * 2;
		int4 = 0;
		while (int4 < int1) {
			int int6 = intArray[int4];
			float float4 = 0.70710677F;
			float float5 = 0.70710677F;
			float float6 = Lookup.coslook(float3 * (float)int6);
			int int7;
			for (int7 = 0; int7 < int5; int7 += 2) {
				float5 *= floatArray2[int7] - float6;
				float4 *= floatArray2[int7 + 1] - float6;
			}

			if ((int3 & 1) != 0) {
				float5 *= floatArray2[int3 - 1] - float6;
				float5 *= float5;
				float4 *= float4 * (1.0F - float6 * float6);
			} else {
				float5 *= float5 * (1.0F + float6);
				float4 *= float4 * (1.0F - float6);
			}

			float5 += float4;
			int7 = Float.floatToIntBits(float5);
			int int8 = Integer.MAX_VALUE & int7;
			int int9 = 0;
			if (int8 < 2139095040 && int8 != 0) {
				if (int8 < 8388608) {
					float5 = (float)((double)float5 * 3.3554432E7);
					int7 = Float.floatToIntBits(float5);
					int8 = Integer.MAX_VALUE & int7;
					int9 = -25;
				}

				int9 += (int8 >>> 23) - 126;
				int7 = int7 & -2139095041 | 1056964608;
				float5 = Float.intBitsToFloat(int7);
			}

			float5 = Lookup.fromdBlook(float1 * Lookup.invsqlook(float5) * Lookup.invsq2explook(int9 + int3) - float2);
			while (true) {
				int int10 = int4++;
				floatArray[int10] *= float5;
				if (int4 >= int1 || intArray[int4] != int6) {
					break;
				}
			}
		}
	}
}
