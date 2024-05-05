package de.jarnbjo.vorbis;


public final class Util {

	public static final int ilog(int int1) {
		int int2;
		for (int2 = 0; int1 > 0; ++int2) {
			int1 >>= 1;
		}

		return int2;
	}

	public static final float float32unpack(int int1) {
		float float1 = (float)(int1 & 2097151);
		float float2 = (float)((int1 & 2145386496) >> 21);
		if ((int1 & Integer.MIN_VALUE) != 0) {
			float1 = -float1;
		}

		return float1 * (float)Math.pow(2.0, (double)float2 - 788.0);
	}

	public static final int lookup1Values(int int1, int int2) {
		int int3 = (int)Math.pow(2.718281828459045, Math.log((double)int1) / (double)int2);
		return intPow(int3 + 1, int2) <= int1 ? int3 + 1 : int3;
	}

	public static final int intPow(int int1, int int2) {
		int int3;
		for (int3 = 1; int2 > 0; int3 *= int1) {
			--int2;
		}

		return int3;
	}

	public static final boolean isBitSet(int int1, int int2) {
		return (int1 & 1 << int2) != 0;
	}

	public static final int icount(int int1) {
		int int2;
		for (int2 = 0; int1 > 0; int1 >>= 1) {
			int2 += int1 & 1;
		}

		return int2;
	}

	public static final int lowNeighbour(int[] intArray, int int1) {
		int int2 = -1;
		int int3 = 0;
		for (int int4 = 0; int4 < intArray.length && int4 < int1; ++int4) {
			if (intArray[int4] > int2 && intArray[int4] < intArray[int1]) {
				int2 = intArray[int4];
				int3 = int4;
			}
		}

		return int3;
	}

	public static final int highNeighbour(int[] intArray, int int1) {
		int int2 = Integer.MAX_VALUE;
		int int3 = 0;
		for (int int4 = 0; int4 < intArray.length && int4 < int1; ++int4) {
			if (intArray[int4] < int2 && intArray[int4] > intArray[int1]) {
				int2 = intArray[int4];
				int3 = int4;
			}
		}

		return int3;
	}

	public static final int renderPoint(int int1, int int2, int int3, int int4, int int5) {
		int int6 = int4 - int3;
		int int7 = int6 < 0 ? -int6 : int6;
		int int8 = int7 * (int5 - int1) / (int2 - int1);
		return int6 < 0 ? int3 - int8 : int3 + int8;
	}

	public static final void renderLine(int int1, int int2, int int3, int int4, float[] floatArray) {
		int int5 = int4 - int2;
		int int6 = int3 - int1;
		int int7 = int5 / int6;
		int int8 = int5 < 0 ? int7 - 1 : int7 + 1;
		int int9 = int2;
		int int10 = 0;
		int int11 = (int5 < 0 ? -int5 : int5) - (int7 > 0 ? int7 * int6 : -int7 * int6);
		floatArray[int1] *= Floor.DB_STATIC_TABLE[int2];
		for (int int12 = int1 + 1; int12 < int3; ++int12) {
			int10 += int11;
			if (int10 >= int6) {
				int10 -= int6;
				floatArray[int12] *= Floor.DB_STATIC_TABLE[int9 += int8];
			} else {
				floatArray[int12] *= Floor.DB_STATIC_TABLE[int9 += int7];
			}
		}
	}
}
