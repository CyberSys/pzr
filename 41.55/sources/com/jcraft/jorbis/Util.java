package com.jcraft.jorbis;


class Util {

	static int icount(int int1) {
		int int2;
		for (int2 = 0; int1 != 0; int1 >>>= 1) {
			int2 += int1 & 1;
		}

		return int2;
	}

	static int ilog(int int1) {
		int int2;
		for (int2 = 0; int1 != 0; int1 >>>= 1) {
			++int2;
		}

		return int2;
	}

	static int ilog2(int int1) {
		int int2;
		for (int2 = 0; int1 > 1; int1 >>>= 1) {
			++int2;
		}

		return int2;
	}
}
