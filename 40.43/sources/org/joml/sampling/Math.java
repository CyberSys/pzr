package org.joml.sampling;


class Math {
	static final double PI = 3.141592653589793;
	static final double PI2 = 6.283185307179586;
	static final double PIHalf = 1.5707963267948966;
	static final double PI_INV = 0.3183098861837907;
	private static final double s5 = Double.longBitsToDouble(4523227044276562163L);
	private static final double s4 = Double.longBitsToDouble(-4671934770969572232L);
	private static final double s3 = Double.longBitsToDouble(4575957211482072852L);
	private static final double s2 = Double.longBitsToDouble(-4628199223918090387L);
	private static final double s1 = Double.longBitsToDouble(4607182418589157889L);

	static double sin_roquen_9(double double1) {
		double double2 = java.lang.Math.rint(double1 * 0.3183098861837907);
		double double3 = double1 - double2 * 3.141592653589793;
		double double4 = (double)(1 - 2 * ((int)double2 & 1));
		double double5 = double3 * double3;
		double3 = double4 * double3;
		double double6 = s5;
		double6 = double6 * double5 + s4;
		double6 = double6 * double5 + s3;
		double6 = double6 * double5 + s2;
		double6 = double6 * double5 + s1;
		return double3 * double6;
	}

	static double acos(double double1) {
		return (-0.6981317007977321 * double1 * double1 - 0.8726646259971648) * double1 + 1.5707963267948966;
	}

	static double sqrt(double double1) {
		return java.lang.Math.sqrt(double1);
	}

	static float min(float float1, float float2) {
		return float1 < float2 ? float1 : float2;
	}

	static int min(int int1, int int2) {
		return int1 < int2 ? int1 : int2;
	}

	static int max(int int1, int int2) {
		return int1 > int2 ? int1 : int2;
	}

	static float max(float float1, float float2) {
		return float1 > float2 ? float1 : float2;
	}

	static float abs(float float1) {
		return java.lang.Math.abs(float1);
	}
}
