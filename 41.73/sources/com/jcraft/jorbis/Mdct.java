package com.jcraft.jorbis;


class Mdct {
	float[] _w = new float[1024];
	float[] _x = new float[1024];
	int[] bitrev;
	int log2n;
	int n;
	float scale;
	float[] trig;

	synchronized void backward(float[] floatArray, float[] floatArray2) {
		if (this._x.length < this.n / 2) {
			this._x = new float[this.n / 2];
		}

		if (this._w.length < this.n / 2) {
			this._w = new float[this.n / 2];
		}

		float[] floatArray3 = this._x;
		float[] floatArray4 = this._w;
		int int1 = this.n >>> 1;
		int int2 = this.n >>> 2;
		int int3 = this.n >>> 3;
		int int4 = 1;
		int int5 = 0;
		int int6 = int1;
		int int7;
		for (int7 = 0; int7 < int3; ++int7) {
			int6 -= 2;
			floatArray3[int5++] = -floatArray[int4 + 2] * this.trig[int6 + 1] - floatArray[int4] * this.trig[int6];
			floatArray3[int5++] = floatArray[int4] * this.trig[int6 + 1] - floatArray[int4 + 2] * this.trig[int6];
			int4 += 4;
		}

		int4 = int1 - 4;
		for (int7 = 0; int7 < int3; ++int7) {
			int6 -= 2;
			floatArray3[int5++] = floatArray[int4] * this.trig[int6 + 1] + floatArray[int4 + 2] * this.trig[int6];
			floatArray3[int5++] = floatArray[int4] * this.trig[int6] - floatArray[int4 + 2] * this.trig[int6 + 1];
			int4 -= 4;
		}

		float[] floatArray5 = this.mdct_kernel(floatArray3, floatArray4, this.n, int1, int2, int3);
		int5 = 0;
		int6 = int1;
		int7 = int2;
		int int8 = int2 - 1;
		int int9 = int2 + int1;
		int int10 = int9 - 1;
		for (int int11 = 0; int11 < int2; ++int11) {
			float float1 = floatArray5[int5] * this.trig[int6 + 1] - floatArray5[int5 + 1] * this.trig[int6];
			float float2 = -(floatArray5[int5] * this.trig[int6] + floatArray5[int5 + 1] * this.trig[int6 + 1]);
			floatArray2[int7] = -float1;
			floatArray2[int8] = float1;
			floatArray2[int9] = float2;
			floatArray2[int10] = float2;
			++int7;
			--int8;
			++int9;
			--int10;
			int5 += 2;
			int6 += 2;
		}
	}

	void clear() {
	}

	void forward(float[] floatArray, float[] floatArray2) {
	}

	void init(int int1) {
		this.bitrev = new int[int1 / 4];
		this.trig = new float[int1 + int1 / 4];
		this.log2n = (int)Math.rint(Math.log((double)int1) / Math.log(2.0));
		this.n = int1;
		byte byte1 = 0;
		byte byte2 = 1;
		int int2 = byte1 + int1 / 2;
		int int3 = int2 + 1;
		int int4 = int2 + int1 / 2;
		int int5 = int4 + 1;
		int int6;
		for (int6 = 0; int6 < int1 / 4; ++int6) {
			this.trig[byte1 + int6 * 2] = (float)Math.cos(3.141592653589793 / (double)int1 * (double)(4 * int6));
			this.trig[byte2 + int6 * 2] = (float)(-Math.sin(3.141592653589793 / (double)int1 * (double)(4 * int6)));
			this.trig[int2 + int6 * 2] = (float)Math.cos(3.141592653589793 / (double)(2 * int1) * (double)(2 * int6 + 1));
			this.trig[int3 + int6 * 2] = (float)Math.sin(3.141592653589793 / (double)(2 * int1) * (double)(2 * int6 + 1));
		}

		for (int6 = 0; int6 < int1 / 8; ++int6) {
			this.trig[int4 + int6 * 2] = (float)Math.cos(3.141592653589793 / (double)int1 * (double)(4 * int6 + 2));
			this.trig[int5 + int6 * 2] = (float)(-Math.sin(3.141592653589793 / (double)int1 * (double)(4 * int6 + 2)));
		}

		int6 = (1 << this.log2n - 1) - 1;
		int int7 = 1 << this.log2n - 2;
		for (int int8 = 0; int8 < int1 / 8; ++int8) {
			int int9 = 0;
			for (int int10 = 0; int7 >>> int10 != 0; ++int10) {
				if ((int7 >>> int10 & int8) != 0) {
					int9 |= 1 << int10;
				}
			}

			this.bitrev[int8 * 2] = ~int9 & int6;
			this.bitrev[int8 * 2 + 1] = int9;
		}

		this.scale = 4.0F / (float)int1;
	}

	private float[] mdct_kernel(float[] floatArray, float[] floatArray2, int int1, int int2, int int3, int int4) {
		int int5 = int3;
		int int6 = 0;
		int int7 = int3;
		int int8 = int2;
		int int9;
		for (int9 = 0; int9 < int3; ++int9) {
			float float1 = floatArray[int5] - floatArray[int6];
			floatArray2[int7 + int9] = floatArray[int5++] + floatArray[int6++];
			float float2 = floatArray[int5] - floatArray[int6];
			int8 -= 4;
			floatArray2[int9++] = float1 * this.trig[int8] + float2 * this.trig[int8 + 1];
			floatArray2[int9] = float2 * this.trig[int8] - float1 * this.trig[int8 + 1];
			floatArray2[int7 + int9] = floatArray[int5++] + floatArray[int6++];
		}

		int int10;
		int int11;
		int int12;
		float float3;
		float float4;
		float float5;
		float float6;
		int int13;
		int int14;
		for (int9 = 0; int9 < this.log2n - 3; ++int9) {
			int13 = int1 >>> int9 + 2;
			int14 = 1 << int9 + 3;
			int10 = int2 - 2;
			int8 = 0;
			for (int11 = 0; int11 < int13 >>> 2; ++int11) {
				int12 = int10;
				int7 = int10 - (int13 >> 1);
				float3 = this.trig[int8];
				float5 = this.trig[int8 + 1];
				int10 -= 2;
				++int13;
				for (int int15 = 0; int15 < 2 << int9; ++int15) {
					float6 = floatArray2[int12] - floatArray2[int7];
					floatArray[int12] = floatArray2[int12] + floatArray2[int7];
					++int12;
					float float7 = floatArray2[int12];
					++int7;
					float4 = float7 - floatArray2[int7];
					floatArray[int12] = floatArray2[int12] + floatArray2[int7];
					floatArray[int7] = float4 * float3 - float6 * float5;
					floatArray[int7 - 1] = float6 * float3 + float4 * float5;
					int12 -= int13;
					int7 -= int13;
				}

				--int13;
				int8 += int14;
			}

			float[] floatArray3 = floatArray2;
			floatArray2 = floatArray;
			floatArray = floatArray3;
		}

		int9 = int1;
		int13 = 0;
		int14 = 0;
		int10 = int2 - 1;
		for (int int16 = 0; int16 < int4; ++int16) {
			int11 = this.bitrev[int13++];
			int12 = this.bitrev[int13++];
			float3 = floatArray2[int11] - floatArray2[int12 + 1];
			float4 = floatArray2[int11 - 1] + floatArray2[int12];
			float5 = floatArray2[int11] + floatArray2[int12 + 1];
			float6 = floatArray2[int11 - 1] - floatArray2[int12];
			float float8 = float3 * this.trig[int9];
			float float9 = float4 * this.trig[int9++];
			float float10 = float3 * this.trig[int9];
			float float11 = float4 * this.trig[int9++];
			floatArray[int14++] = (float5 + float10 + float9) * 0.5F;
			floatArray[int10--] = (-float6 + float11 - float8) * 0.5F;
			floatArray[int14++] = (float6 + float11 - float8) * 0.5F;
			floatArray[int10--] = (float5 - float10 - float9) * 0.5F;
		}

		return floatArray;
	}
}
