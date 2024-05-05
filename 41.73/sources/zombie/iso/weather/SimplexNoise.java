package zombie.iso.weather;


public class SimplexNoise {
	private static SimplexNoise.Grad[] grad3 = new SimplexNoise.Grad[]{new SimplexNoise.Grad(1.0, 1.0, 0.0), new SimplexNoise.Grad(-1.0, 1.0, 0.0), new SimplexNoise.Grad(1.0, -1.0, 0.0), new SimplexNoise.Grad(-1.0, -1.0, 0.0), new SimplexNoise.Grad(1.0, 0.0, 1.0), new SimplexNoise.Grad(-1.0, 0.0, 1.0), new SimplexNoise.Grad(1.0, 0.0, -1.0), new SimplexNoise.Grad(-1.0, 0.0, -1.0), new SimplexNoise.Grad(0.0, 1.0, 1.0), new SimplexNoise.Grad(0.0, -1.0, 1.0), new SimplexNoise.Grad(0.0, 1.0, -1.0), new SimplexNoise.Grad(0.0, -1.0, -1.0)};
	private static SimplexNoise.Grad[] grad4 = new SimplexNoise.Grad[]{new SimplexNoise.Grad(0.0, 1.0, 1.0, 1.0), new SimplexNoise.Grad(0.0, 1.0, 1.0, -1.0), new SimplexNoise.Grad(0.0, 1.0, -1.0, 1.0), new SimplexNoise.Grad(0.0, 1.0, -1.0, -1.0), new SimplexNoise.Grad(0.0, -1.0, 1.0, 1.0), new SimplexNoise.Grad(0.0, -1.0, 1.0, -1.0), new SimplexNoise.Grad(0.0, -1.0, -1.0, 1.0), new SimplexNoise.Grad(0.0, -1.0, -1.0, -1.0), new SimplexNoise.Grad(1.0, 0.0, 1.0, 1.0), new SimplexNoise.Grad(1.0, 0.0, 1.0, -1.0), new SimplexNoise.Grad(1.0, 0.0, -1.0, 1.0), new SimplexNoise.Grad(1.0, 0.0, -1.0, -1.0), new SimplexNoise.Grad(-1.0, 0.0, 1.0, 1.0), new SimplexNoise.Grad(-1.0, 0.0, 1.0, -1.0), new SimplexNoise.Grad(-1.0, 0.0, -1.0, 1.0), new SimplexNoise.Grad(-1.0, 0.0, -1.0, -1.0), new SimplexNoise.Grad(1.0, 1.0, 0.0, 1.0), new SimplexNoise.Grad(1.0, 1.0, 0.0, -1.0), new SimplexNoise.Grad(1.0, -1.0, 0.0, 1.0), new SimplexNoise.Grad(1.0, -1.0, 0.0, -1.0), new SimplexNoise.Grad(-1.0, 1.0, 0.0, 1.0), new SimplexNoise.Grad(-1.0, 1.0, 0.0, -1.0), new SimplexNoise.Grad(-1.0, -1.0, 0.0, 1.0), new SimplexNoise.Grad(-1.0, -1.0, 0.0, -1.0), new SimplexNoise.Grad(1.0, 1.0, 1.0, 0.0), new SimplexNoise.Grad(1.0, 1.0, -1.0, 0.0), new SimplexNoise.Grad(1.0, -1.0, 1.0, 0.0), new SimplexNoise.Grad(1.0, -1.0, -1.0, 0.0), new SimplexNoise.Grad(-1.0, 1.0, 1.0, 0.0), new SimplexNoise.Grad(-1.0, 1.0, -1.0, 0.0), new SimplexNoise.Grad(-1.0, -1.0, 1.0, 0.0), new SimplexNoise.Grad(-1.0, -1.0, -1.0, 0.0)};
	private static short[] p = new short[]{151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};
	private static short[] perm = new short[512];
	private static short[] permMod12 = new short[512];
	private static final double F2;
	private static final double G2;
	private static final double F3 = 0.3333333333333333;
	private static final double G3 = 0.16666666666666666;
	private static final double F4;
	private static final double G4;

	private static int fastfloor(double double1) {
		int int1 = (int)double1;
		return double1 < (double)int1 ? int1 - 1 : int1;
	}

	private static double dot(SimplexNoise.Grad grad, double double1, double double2) {
		return grad.x * double1 + grad.y * double2;
	}

	private static double dot(SimplexNoise.Grad grad, double double1, double double2, double double3) {
		return grad.x * double1 + grad.y * double2 + grad.z * double3;
	}

	private static double dot(SimplexNoise.Grad grad, double double1, double double2, double double3, double double4) {
		return grad.x * double1 + grad.y * double2 + grad.z * double3 + grad.w * double4;
	}

	public static double noise(double double1, double double2) {
		double double3 = (double1 + double2) * F2;
		int int1 = fastfloor(double1 + double3);
		int int2 = fastfloor(double2 + double3);
		double double4 = (double)(int1 + int2) * G2;
		double double5 = (double)int1 - double4;
		double double6 = (double)int2 - double4;
		double double7 = double1 - double5;
		double double8 = double2 - double6;
		byte byte1;
		byte byte2;
		if (double7 > double8) {
			byte1 = 1;
			byte2 = 0;
		} else {
			byte1 = 0;
			byte2 = 1;
		}

		double double9 = double7 - (double)byte1 + G2;
		double double10 = double8 - (double)byte2 + G2;
		double double11 = double7 - 1.0 + 2.0 * G2;
		double double12 = double8 - 1.0 + 2.0 * G2;
		int int3 = int1 & 255;
		int int4 = int2 & 255;
		short short1 = permMod12[int3 + perm[int4]];
		short short2 = permMod12[int3 + byte1 + perm[int4 + byte2]];
		short short3 = permMod12[int3 + 1 + perm[int4 + 1]];
		double double13 = 0.5 - double7 * double7 - double8 * double8;
		double double14;
		if (double13 < 0.0) {
			double14 = 0.0;
		} else {
			double13 *= double13;
			double14 = double13 * double13 * dot(grad3[short1], double7, double8);
		}

		double double15 = 0.5 - double9 * double9 - double10 * double10;
		double double16;
		if (double15 < 0.0) {
			double16 = 0.0;
		} else {
			double15 *= double15;
			double16 = double15 * double15 * dot(grad3[short2], double9, double10);
		}

		double double17 = 0.5 - double11 * double11 - double12 * double12;
		double double18;
		if (double17 < 0.0) {
			double18 = 0.0;
		} else {
			double17 *= double17;
			double18 = double17 * double17 * dot(grad3[short3], double11, double12);
		}

		return 70.0 * (double14 + double16 + double18);
	}

	public static double noise(double double1, double double2, double double3) {
		double double4 = (double1 + double2 + double3) * 0.3333333333333333;
		int int1 = fastfloor(double1 + double4);
		int int2 = fastfloor(double2 + double4);
		int int3 = fastfloor(double3 + double4);
		double double5 = (double)(int1 + int2 + int3) * 0.16666666666666666;
		double double6 = (double)int1 - double5;
		double double7 = (double)int2 - double5;
		double double8 = (double)int3 - double5;
		double double9 = double1 - double6;
		double double10 = double2 - double7;
		double double11 = double3 - double8;
		byte byte1;
		byte byte2;
		byte byte3;
		byte byte4;
		byte byte5;
		byte byte6;
		if (double9 >= double10) {
			if (double10 >= double11) {
				byte1 = 1;
				byte2 = 0;
				byte3 = 0;
				byte4 = 1;
				byte5 = 1;
				byte6 = 0;
			} else if (double9 >= double11) {
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
		} else if (double10 < double11) {
			byte1 = 0;
			byte2 = 0;
			byte3 = 1;
			byte4 = 0;
			byte5 = 1;
			byte6 = 1;
		} else if (double9 < double11) {
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

		double double12 = double9 - (double)byte1 + 0.16666666666666666;
		double double13 = double10 - (double)byte2 + 0.16666666666666666;
		double double14 = double11 - (double)byte3 + 0.16666666666666666;
		double double15 = double9 - (double)byte4 + 0.3333333333333333;
		double double16 = double10 - (double)byte5 + 0.3333333333333333;
		double double17 = double11 - (double)byte6 + 0.3333333333333333;
		double double18 = double9 - 1.0 + 0.5;
		double double19 = double10 - 1.0 + 0.5;
		double double20 = double11 - 1.0 + 0.5;
		int int4 = int1 & 255;
		int int5 = int2 & 255;
		int int6 = int3 & 255;
		short short1 = permMod12[int4 + perm[int5 + perm[int6]]];
		short short2 = permMod12[int4 + byte1 + perm[int5 + byte2 + perm[int6 + byte3]]];
		short short3 = permMod12[int4 + byte4 + perm[int5 + byte5 + perm[int6 + byte6]]];
		short short4 = permMod12[int4 + 1 + perm[int5 + 1 + perm[int6 + 1]]];
		double double21 = 0.6 - double9 * double9 - double10 * double10 - double11 * double11;
		double double22;
		if (double21 < 0.0) {
			double22 = 0.0;
		} else {
			double21 *= double21;
			double22 = double21 * double21 * dot(grad3[short1], double9, double10, double11);
		}

		double double23 = 0.6 - double12 * double12 - double13 * double13 - double14 * double14;
		double double24;
		if (double23 < 0.0) {
			double24 = 0.0;
		} else {
			double23 *= double23;
			double24 = double23 * double23 * dot(grad3[short2], double12, double13, double14);
		}

		double double25 = 0.6 - double15 * double15 - double16 * double16 - double17 * double17;
		double double26;
		if (double25 < 0.0) {
			double26 = 0.0;
		} else {
			double25 *= double25;
			double26 = double25 * double25 * dot(grad3[short3], double15, double16, double17);
		}

		double double27 = 0.6 - double18 * double18 - double19 * double19 - double20 * double20;
		double double28;
		if (double27 < 0.0) {
			double28 = 0.0;
		} else {
			double27 *= double27;
			double28 = double27 * double27 * dot(grad3[short4], double18, double19, double20);
		}

		return 32.0 * (double22 + double24 + double26 + double28);
	}

	public static double noise(double double1, double double2, double double3, double double4) {
		double double5 = (double1 + double2 + double3 + double4) * F4;
		int int1 = fastfloor(double1 + double5);
		int int2 = fastfloor(double2 + double5);
		int int3 = fastfloor(double3 + double5);
		int int4 = fastfloor(double4 + double5);
		double double6 = (double)(int1 + int2 + int3 + int4) * G4;
		double double7 = (double)int1 - double6;
		double double8 = (double)int2 - double6;
		double double9 = (double)int3 - double6;
		double double10 = (double)int4 - double6;
		double double11 = double1 - double7;
		double double12 = double2 - double8;
		double double13 = double3 - double9;
		double double14 = double4 - double10;
		int int5 = 0;
		int int6 = 0;
		int int7 = 0;
		int int8 = 0;
		if (double11 > double12) {
			++int5;
		} else {
			++int6;
		}

		if (double11 > double13) {
			++int5;
		} else {
			++int7;
		}

		if (double11 > double14) {
			++int5;
		} else {
			++int8;
		}

		if (double12 > double13) {
			++int6;
		} else {
			++int7;
		}

		if (double12 > double14) {
			++int6;
		} else {
			++int8;
		}

		if (double13 > double14) {
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
		double double15 = double11 - (double)int9 + G4;
		double double16 = double12 - (double)int10 + G4;
		double double17 = double13 - (double)int11 + G4;
		double double18 = double14 - (double)int12 + G4;
		double double19 = double11 - (double)int13 + 2.0 * G4;
		double double20 = double12 - (double)int14 + 2.0 * G4;
		double double21 = double13 - (double)int15 + 2.0 * G4;
		double double22 = double14 - (double)int16 + 2.0 * G4;
		double double23 = double11 - (double)int17 + 3.0 * G4;
		double double24 = double12 - (double)int18 + 3.0 * G4;
		double double25 = double13 - (double)int19 + 3.0 * G4;
		double double26 = double14 - (double)int20 + 3.0 * G4;
		double double27 = double11 - 1.0 + 4.0 * G4;
		double double28 = double12 - 1.0 + 4.0 * G4;
		double double29 = double13 - 1.0 + 4.0 * G4;
		double double30 = double14 - 1.0 + 4.0 * G4;
		int int21 = int1 & 255;
		int int22 = int2 & 255;
		int int23 = int3 & 255;
		int int24 = int4 & 255;
		int int25 = perm[int21 + perm[int22 + perm[int23 + perm[int24]]]] % 32;
		int int26 = perm[int21 + int9 + perm[int22 + int10 + perm[int23 + int11 + perm[int24 + int12]]]] % 32;
		int int27 = perm[int21 + int13 + perm[int22 + int14 + perm[int23 + int15 + perm[int24 + int16]]]] % 32;
		int int28 = perm[int21 + int17 + perm[int22 + int18 + perm[int23 + int19 + perm[int24 + int20]]]] % 32;
		int int29 = perm[int21 + 1 + perm[int22 + 1 + perm[int23 + 1 + perm[int24 + 1]]]] % 32;
		double double31 = 0.6 - double11 * double11 - double12 * double12 - double13 * double13 - double14 * double14;
		double double32;
		if (double31 < 0.0) {
			double32 = 0.0;
		} else {
			double31 *= double31;
			double32 = double31 * double31 * dot(grad4[int25], double11, double12, double13, double14);
		}

		double double33 = 0.6 - double15 * double15 - double16 * double16 - double17 * double17 - double18 * double18;
		double double34;
		if (double33 < 0.0) {
			double34 = 0.0;
		} else {
			double33 *= double33;
			double34 = double33 * double33 * dot(grad4[int26], double15, double16, double17, double18);
		}

		double double35 = 0.6 - double19 * double19 - double20 * double20 - double21 * double21 - double22 * double22;
		double double36;
		if (double35 < 0.0) {
			double36 = 0.0;
		} else {
			double35 *= double35;
			double36 = double35 * double35 * dot(grad4[int27], double19, double20, double21, double22);
		}

		double double37 = 0.6 - double23 * double23 - double24 * double24 - double25 * double25 - double26 * double26;
		double double38;
		if (double37 < 0.0) {
			double38 = 0.0;
		} else {
			double37 *= double37;
			double38 = double37 * double37 * dot(grad4[int28], double23, double24, double25, double26);
		}

		double double39 = 0.6 - double27 * double27 - double28 * double28 - double29 * double29 - double30 * double30;
		double double40;
		if (double39 < 0.0) {
			double40 = 0.0;
		} else {
			double39 *= double39;
			double40 = double39 * double39 * dot(grad4[int29], double27, double28, double29, double30);
		}

		return 27.0 * (double32 + double34 + double36 + double38 + double40);
	}

	static  {
	for (int var0 = 0; var0 < 512; ++var0) {
		perm[var0] = p[var0 & 255];
		permMod12[var0] = (short)(perm[var0] % 12);
	}

		F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
		G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
		F4 = (Math.sqrt(5.0) - 1.0) / 4.0;
		G4 = (5.0 - Math.sqrt(5.0)) / 20.0;
	}

	private static class Grad {
		double x;
		double y;
		double z;
		double w;

		Grad(double double1, double double2, double double3) {
			this.x = double1;
			this.y = double2;
			this.z = double3;
		}

		Grad(double double1, double double2, double double3, double double4) {
			this.x = double1;
			this.y = double2;
			this.z = double3;
			this.w = double4;
		}
	}
}
