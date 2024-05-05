package org.joml.sampling;


class Math extends org.joml.Math {
	static final double PI = 3.141592653589793;
	static final double PI2 = 6.283185307179586;
	static final double PIHalf = 1.5707963267948966;
	private static final double ONE_OVER_PI = 0.3183098861837907;
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
}
