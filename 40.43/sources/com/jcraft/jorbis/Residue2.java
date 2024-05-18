package com.jcraft.jorbis;


class Residue2 extends Residue0 {

	int inverse(Block block, Object object, float[][] floatArrayArray, int[] intArray, int int1) {
		boolean boolean1 = false;
		int int2;
		for (int2 = 0; int2 < int1 && intArray[int2] == 0; ++int2) {
		}

		return int2 == int1 ? 0 : _2inverse(block, object, floatArrayArray, int1);
	}
}
