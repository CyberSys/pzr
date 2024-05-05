package com.jcraft.jorbis;


class Residue1 extends Residue0 {

	int inverse(Block block, Object object, float[][] floatArrayArray, int[] intArray, int int1) {
		int int2 = 0;
		for (int int3 = 0; int3 < int1; ++int3) {
			if (intArray[int3] != 0) {
				floatArrayArray[int2++] = floatArrayArray[int3];
			}
		}

		if (int2 != 0) {
			return _01inverse(block, object, floatArrayArray, int2, 1);
		} else {
			return 0;
		}
	}
}
