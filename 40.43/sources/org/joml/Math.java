package org.joml;


public class Math {
	public static final double PI = 3.141592653589793;
	static final double PI2 = 6.283185307179586;
	static final double PIHalf = 1.5707963267948966;
	static final double PI_4 = 0.7853981633974483;
	static final double PI_INV = 0.3183098861837907;
	private static final int lookupBits;
	private static final int lookupTableSize;
	private static final int lookupTableSizeMinus1;
	private static final int lookupTableSizeWithMargin;
	private static final double pi2OverLookupSize;
	private static final double lookupSizeOverPi2;
	private static final float[] sinTable;
	private static final double c1;
	private static final double c2;
	private static final double c3;
	private static final double c4;
	private static final double c5;
	private static final double c6;
	private static final double c7;
	private static final double s5;
	private static final double s4;
	private static final double s3;
	private static final double s2;
	private static final double s1;
	private static final double k1;
	private static final double k2;
	private static final double k3;
	private static final double k4;
	private static final double k5;
	private static final double k6;
	private static final double k7;

	static double sin_theagentd_arith(double double1) {
		double double2 = floor((double1 + 0.7853981633974483) * 0.3183098861837907);
		double double3 = double1 - double2 * 3.141592653589793;
		double double4 = (double)(((int)double2 & 1) * -2 + 1);
		double double5 = double3 * double3;
		double double6 = double3 * double5;
		double double7 = double3 + double6 * c1;
		double6 *= double5;
		double7 += double6 * c2;
		double6 *= double5;
		double7 += double6 * c3;
		double6 *= double5;
		double7 += double6 * c4;
		double6 *= double5;
		double7 += double6 * c5;
		double6 *= double5;
		double7 += double6 * c6;
		double6 *= double5;
		double7 += double6 * c7;
		return double4 * double7;
	}

	static double sin_roquen_arith(double double1) {
		double double2 = floor((double1 + 0.7853981633974483) * 0.3183098861837907);
		double double3 = double1 - double2 * 3.141592653589793;
		double double4 = (double)(((int)double2 & 1) * -2 + 1);
		double double5 = double3 * double3;
		double3 = double4 * double3;
		double double6 = c7;
		double6 = double6 * double5 + c6;
		double6 = double6 * double5 + c5;
		double6 = double6 * double5 + c4;
		double6 = double6 * double5 + c3;
		double6 = double6 * double5 + c2;
		double6 = double6 * double5 + c1;
		return double3 + double3 * double5 * double6;
	}

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

	static double sin_roquen_newk(double double1) {
		double double2 = java.lang.Math.rint(double1 * 0.3183098861837907);
		double double3 = double1 - double2 * 3.141592653589793;
		double double4 = (double)(1 - 2 * ((int)double2 & 1));
		double double5 = double3 * double3;
		double3 = double4 * double3;
		double double6 = k7;
		double6 = double6 * double5 + k6;
		double6 = double6 * double5 + k5;
		double6 = double6 * double5 + k4;
		double6 = double6 * double5 + k3;
		double6 = double6 * double5 + k2;
		double6 = double6 * double5 + k1;
		return double3 + double3 * double5 * double6;
	}

	static double sin_theagentd_lookup(double double1) {
		float float1 = (float)(double1 * lookupSizeOverPi2);
		int int1 = (int)java.lang.Math.floor((double)float1);
		float float2 = float1 - (float)int1;
		int int2 = int1 & lookupTableSizeMinus1;
		float float3 = sinTable[int2];
		float float4 = sinTable[int2 + 1];
		return (double)(float3 + (float4 - float3) * float2);
	}

	public static double sin(double double1) {
		if (Options.FASTMATH) {
			return Options.SIN_LOOKUP ? sin_theagentd_lookup(double1) : sin_roquen_newk(double1);
		} else {
			return java.lang.Math.sin(double1);
		}
	}

	public static double cos(double double1) {
		return Options.FASTMATH ? sin(double1 + 1.5707963267948966) : java.lang.Math.cos(double1);
	}

	public static double sqrt(double double1) {
		return java.lang.Math.sqrt(double1);
	}

	public static double tan(double double1) {
		return java.lang.Math.tan(double1);
	}

	public static double acos(double double1) {
		return java.lang.Math.acos(double1);
	}

	public static double atan2(double double1, double double2) {
		return java.lang.Math.atan2(double1, double2);
	}

	public static double asin(double double1) {
		return java.lang.Math.asin(double1);
	}

	public static double abs(double double1) {
		return java.lang.Math.abs(double1);
	}

	public static float abs(float float1) {
		return java.lang.Math.abs(float1);
	}

	public static int max(int int1, int int2) {
		return java.lang.Math.max(int1, int2);
	}

	public static int min(int int1, int int2) {
		return java.lang.Math.min(int1, int2);
	}

	public static float min(float float1, float float2) {
		return float1 < float2 ? float1 : float2;
	}

	public static float max(float float1, float float2) {
		return float1 > float2 ? float1 : float2;
	}

	public static double min(double double1, double double2) {
		return double1 < double2 ? double1 : double2;
	}

	public static double max(double double1, double double2) {
		return double1 > double2 ? double1 : double2;
	}

	public static double toRadians(double double1) {
		return java.lang.Math.toRadians(double1);
	}

	public static double toDegrees(double double1) {
		return java.lang.Math.toDegrees(double1);
	}

	public static double floor(double double1) {
		return java.lang.Math.floor(double1);
	}

	public static double exp(double double1) {
		return java.lang.Math.exp(double1);
	}

	static  {
		lookupBits = Options.SIN_LOOKUP_BITS;
		lookupTableSize = 1 << lookupBits;
		lookupTableSizeMinus1 = lookupTableSize - 1;
		lookupTableSizeWithMargin = lookupTableSize + 1;
		pi2OverLookupSize = 6.283185307179586 / (double)lookupTableSize;
		lookupSizeOverPi2 = (double)lookupTableSize / 6.283185307179586;
	if (Options.FASTMATH && Options.SIN_LOOKUP) {
		sinTable = new float[lookupTableSizeWithMargin];
		for (int var0 = 0; var0 < lookupTableSizeWithMargin; ++var0) {
			double var1 = (double)var0 * pi2OverLookupSize;
			sinTable[var0] = (float)java.lang.Math.sin(var1);
		}
	} else {
		sinTable = null;
	}

		c1 = Double.longBitsToDouble(-4628199217061079772L);
		c2 = Double.longBitsToDouble(4575957461383582011L);
		c3 = Double.longBitsToDouble(-4671919876300759001L);
		c4 = Double.longBitsToDouble(4523617214285661942L);
		c5 = Double.longBitsToDouble(-4730215272828025532L);
		c6 = Double.longBitsToDouble(4460272573143870633L);
		c7 = Double.longBitsToDouble(-4797767418267846529L);
		s5 = Double.longBitsToDouble(4523227044276562163L);
		s4 = Double.longBitsToDouble(-4671934770969572232L);
		s3 = Double.longBitsToDouble(4575957211482072852L);
		s2 = Double.longBitsToDouble(-4628199223918090387L);
		s1 = Double.longBitsToDouble(4607182418589157889L);
		k1 = Double.longBitsToDouble(-4628199217061079959L);
		k2 = Double.longBitsToDouble(4575957461383549981L);
		k3 = Double.longBitsToDouble(-4671919876307284301L);
		k4 = Double.longBitsToDouble(4523617213632129738L);
		k5 = Double.longBitsToDouble(-4730215344060517252L);
		k6 = Double.longBitsToDouble(4460268259291226124L);
		k7 = Double.longBitsToDouble(-4798040743777455072L);
	}
}
