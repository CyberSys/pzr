package com.jcraft.jorbis;


class Lpc {
	Drft fft = new Drft();
	int ln;
	int m;

	static float FAST_HYPOT(float float1, float float2) {
		return (float)Math.sqrt((double)(float1 * float1 + float2 * float2));
	}

	static float lpc_from_data(float[] floatArray, float[] floatArray2, int int1, int int2) {
		float[] floatArray3 = new float[int2 + 1];
		int int3;
		int int4;
		float float1;
		for (int4 = int2 + 1; int4-- != 0; floatArray3[int4] = float1) {
			float1 = 0.0F;
			for (int3 = int4; int3 < int1; ++int3) {
				float1 += floatArray[int3] * floatArray[int3 - int4];
			}
		}

		float float2 = floatArray3[0];
		for (int3 = 0; int3 < int2; ++int3) {
			float1 = -floatArray3[int3 + 1];
			if (float2 == 0.0F) {
				for (int int5 = 0; int5 < int2; ++int5) {
					floatArray2[int5] = 0.0F;
				}

				return 0.0F;
			}

			for (int4 = 0; int4 < int3; ++int4) {
				float1 -= floatArray2[int4] * floatArray3[int3 - int4];
			}

			float1 /= float2;
			floatArray2[int3] = float1;
			for (int4 = 0; int4 < int3 / 2; ++int4) {
				float float3 = floatArray2[int4];
				floatArray2[int4] += float1 * floatArray2[int3 - 1 - int4];
				floatArray2[int3 - 1 - int4] += float1 * float3;
			}

			if (int3 % 2 != 0) {
				floatArray2[int4] += floatArray2[int4] * float1;
			}

			float2 = (float)((double)float2 * (1.0 - (double)(float1 * float1)));
		}

		return float2;
	}

	void clear() {
		this.fft.clear();
	}

	void init(int int1, int int2) {
		this.ln = int1;
		this.m = int2;
		this.fft.init(int1 * 2);
	}

	float lpc_from_curve(float[] floatArray, float[] floatArray2) {
		int int1 = this.ln;
		float[] floatArray3 = new float[int1 + int1];
		float float1 = (float)(0.5 / (double)int1);
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			floatArray3[int2 * 2] = floatArray[int2] * float1;
			floatArray3[int2 * 2 + 1] = 0.0F;
		}

		floatArray3[int1 * 2 - 1] = floatArray[int1 - 1] * float1;
		int1 *= 2;
		this.fft.backward(floatArray3);
		int2 = 0;
		float float2;
		for (int int3 = int1 / 2; int2 < int1 / 2; floatArray3[int3++] = float2) {
			float2 = floatArray3[int2];
			floatArray3[int2++] = floatArray3[int3];
		}

		return lpc_from_data(floatArray3, floatArray2, int1, this.m);
	}

	void lpc_to_curve(float[] floatArray, float[] floatArray2, float float1) {
		int int1;
		for (int1 = 0; int1 < this.ln * 2; ++int1) {
			floatArray[int1] = 0.0F;
		}

		if (float1 != 0.0F) {
			for (int1 = 0; int1 < this.m; ++int1) {
				floatArray[int1 * 2 + 1] = floatArray2[int1] / (4.0F * float1);
				floatArray[int1 * 2 + 2] = -floatArray2[int1] / (4.0F * float1);
			}

			this.fft.backward(floatArray);
			int1 = this.ln * 2;
			float float2 = (float)(1.0 / (double)float1);
			floatArray[0] = (float)(1.0 / (double)(floatArray[0] * 2.0F + float2));
			for (int int2 = 1; int2 < this.ln; ++int2) {
				float float3 = floatArray[int2] + floatArray[int1 - int2];
				float float4 = floatArray[int2] - floatArray[int1 - int2];
				float float5 = float3 + float2;
				floatArray[int2] = (float)(1.0 / (double)FAST_HYPOT(float5, float4));
			}
		}
	}
}
