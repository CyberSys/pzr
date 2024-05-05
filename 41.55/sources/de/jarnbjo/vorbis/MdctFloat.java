package de.jarnbjo.vorbis;


class MdctFloat {
	private static final float cPI3_8 = 0.38268343F;
	private static final float cPI2_8 = 0.70710677F;
	private static final float cPI1_8 = 0.9238795F;
	private int n;
	private int log2n;
	private float[] trig;
	private int[] bitrev;
	private float[] equalizer;
	private float scale;
	private int itmp1;
	private int itmp2;
	private int itmp3;
	private int itmp4;
	private int itmp5;
	private int itmp6;
	private int itmp7;
	private int itmp8;
	private int itmp9;
	private float dtmp1;
	private float dtmp2;
	private float dtmp3;
	private float dtmp4;
	private float dtmp5;
	private float dtmp6;
	private float dtmp7;
	private float dtmp8;
	private float dtmp9;
	private float[] _x = new float[1024];
	private float[] _w = new float[1024];

	protected MdctFloat(int int1) {
		this.bitrev = new int[int1 / 4];
		this.trig = new float[int1 + int1 / 4];
		int int2 = int1 >>> 1;
		this.log2n = (int)Math.rint(Math.log((double)int1) / Math.log(2.0));
		this.n = int1;
		byte byte1 = 0;
		byte byte2 = 1;
		int int3 = byte1 + int1 / 2;
		int int4 = int3 + 1;
		int int5 = int3 + int1 / 2;
		int int6 = int5 + 1;
		int int7;
		for (int7 = 0; int7 < int1 / 4; ++int7) {
			this.trig[byte1 + int7 * 2] = (float)Math.cos(3.141592653589793 / (double)int1 * (double)(4 * int7));
			this.trig[byte2 + int7 * 2] = (float)(-Math.sin(3.141592653589793 / (double)int1 * (double)(4 * int7)));
			this.trig[int3 + int7 * 2] = (float)Math.cos(3.141592653589793 / (double)(2 * int1) * (double)(2 * int7 + 1));
			this.trig[int4 + int7 * 2] = (float)Math.sin(3.141592653589793 / (double)(2 * int1) * (double)(2 * int7 + 1));
		}

		for (int7 = 0; int7 < int1 / 8; ++int7) {
			this.trig[int5 + int7 * 2] = (float)Math.cos(3.141592653589793 / (double)int1 * (double)(4 * int7 + 2));
			this.trig[int6 + int7 * 2] = (float)(-Math.sin(3.141592653589793 / (double)int1 * (double)(4 * int7 + 2)));
		}

		int7 = (1 << this.log2n - 1) - 1;
		int int8 = 1 << this.log2n - 2;
		for (int int9 = 0; int9 < int1 / 8; ++int9) {
			int int10 = 0;
			for (int int11 = 0; int8 >>> int11 != 0; ++int11) {
				if ((int8 >>> int11 & int9) != 0) {
					int10 |= 1 << int11;
				}
			}

			this.bitrev[int9 * 2] = ~int10 & int7;
			this.bitrev[int9 * 2 + 1] = int10;
		}

		this.scale = 4.0F / (float)int1;
	}

	protected void setEqualizer(float[] floatArray) {
		this.equalizer = floatArray;
	}

	protected float[] getEqualizer() {
		return this.equalizer;
	}

	protected synchronized void imdct(float[] floatArray, float[] floatArray2, int[] intArray) {
		float[] floatArray3 = floatArray;
		if (this._x.length < this.n / 2) {
			this._x = new float[this.n / 2];
		}

		if (this._w.length < this.n / 2) {
			this._w = new float[this.n / 2];
		}

		float[] floatArray4 = this._x;
		float[] floatArray5 = this._w;
		int int1 = this.n >> 1;
		int int2 = this.n >> 2;
		int int3 = this.n >> 3;
		int int4;
		if (this.equalizer != null) {
			for (int4 = 0; int4 < this.n; ++int4) {
				floatArray[int4] *= this.equalizer[int4];
			}
		}

		int4 = -1;
		int int5 = 0;
		int int6 = int1;
		int int7;
		for (int7 = 0; int7 < int3; ++int7) {
			int4 += 2;
			this.dtmp1 = floatArray3[int4];
			int4 += 2;
			this.dtmp2 = floatArray3[int4];
			--int6;
			this.dtmp3 = this.trig[int6];
			--int6;
			this.dtmp4 = this.trig[int6];
			floatArray4[int5++] = -this.dtmp2 * this.dtmp3 - this.dtmp1 * this.dtmp4;
			floatArray4[int5++] = this.dtmp1 * this.dtmp3 - this.dtmp2 * this.dtmp4;
		}

		int4 = int1;
		for (int7 = 0; int7 < int3; ++int7) {
			int4 -= 2;
			this.dtmp1 = floatArray3[int4];
			int4 -= 2;
			this.dtmp2 = floatArray3[int4];
			--int6;
			this.dtmp3 = this.trig[int6];
			--int6;
			this.dtmp4 = this.trig[int6];
			floatArray4[int5++] = this.dtmp2 * this.dtmp3 + this.dtmp1 * this.dtmp4;
			floatArray4[int5++] = this.dtmp2 * this.dtmp4 - this.dtmp1 * this.dtmp3;
		}

		float[] floatArray6 = this.kernel(floatArray4, floatArray5, this.n, int1, int2, int3);
		int5 = 0;
		int6 = int1;
		int7 = int2;
		int int8 = int2 - 1;
		int int9 = int2 + int1;
		int int10 = int9 - 1;
		for (int int11 = 0; int11 < int2; ++int11) {
			this.dtmp1 = floatArray6[int5++];
			this.dtmp2 = floatArray6[int5++];
			this.dtmp3 = this.trig[int6++];
			this.dtmp4 = this.trig[int6++];
			float float1 = this.dtmp1 * this.dtmp4 - this.dtmp2 * this.dtmp3;
			float float2 = -(this.dtmp1 * this.dtmp3 + this.dtmp2 * this.dtmp4);
			intArray[int7] = (int)(-float1 * floatArray2[int7]);
			intArray[int8] = (int)(float1 * floatArray2[int8]);
			intArray[int9] = (int)(float2 * floatArray2[int9]);
			intArray[int10] = (int)(float2 * floatArray2[int10]);
			++int7;
			--int8;
			++int9;
			--int10;
		}
	}

	private float[] kernel(float[] floatArray, float[] floatArray2, int int1, int int2, int int3, int int4) {
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
					this.dtmp1 = floatArray2[int12];
					this.dtmp2 = floatArray2[int7];
					float6 = this.dtmp1 - this.dtmp2;
					floatArray[int12] = this.dtmp1 + this.dtmp2;
					++int12;
					this.dtmp1 = floatArray2[int12];
					++int7;
					this.dtmp2 = floatArray2[int7];
					float4 = this.dtmp1 - this.dtmp2;
					floatArray[int12] = this.dtmp1 + this.dtmp2;
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
			float float7 = float3 * this.trig[int9];
			float float8 = float4 * this.trig[int9++];
			float float9 = float3 * this.trig[int9];
			float float10 = float4 * this.trig[int9++];
			floatArray[int14++] = (float5 + float9 + float8) * 16383.0F;
			floatArray[int10--] = (-float6 + float10 - float7) * 16383.0F;
			floatArray[int14++] = (float6 + float10 - float7) * 16383.0F;
			floatArray[int10--] = (float5 - float9 - float8) * 16383.0F;
		}

		return floatArray;
	}
}
