package org.joml;


public class Math {
	public static final double PI = 3.141592653589793;
	static final double PI2 = 6.283185307179586;
	static final float PI_f = 3.1415927F;
	static final float PI2_f = 6.2831855F;
	static final double PIHalf = 1.5707963267948966;
	static final float PIHalf_f = 1.5707964F;
	static final double PI_4 = 0.7853981633974483;
	static final double PI_INV = 0.3183098861837907;
	private static final int lookupBits;
	private static final int lookupTableSize;
	private static final int lookupTableSizeMinus1;
	private static final int lookupTableSizeWithMargin;
	private static final float pi2OverLookupSize;
	private static final float lookupSizeOverPi2;
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

	static float sin_theagentd_lookup(float float1) {
		float float2 = float1 * lookupSizeOverPi2;
		int int1 = (int)java.lang.Math.floor((double)float2);
		float float3 = float2 - (float)int1;
		int int2 = int1 & lookupTableSizeMinus1;
		float float4 = sinTable[int2];
		float float5 = sinTable[int2 + 1];
		return float4 + (float5 - float4) * float3;
	}

	public static float sin(float float1) {
		return (float)java.lang.Math.sin((double)float1);
	}

	public static double sin(double double1) {
		if (Options.FASTMATH) {
			return Options.SIN_LOOKUP ? (double)sin_theagentd_lookup((float)double1) : sin_roquen_newk(double1);
		} else {
			return java.lang.Math.sin(double1);
		}
	}

	public static float cos(float float1) {
		return Options.FASTMATH ? sin(float1 + 1.5707964F) : (float)java.lang.Math.cos((double)float1);
	}

	public static double cos(double double1) {
		return Options.FASTMATH ? sin(double1 + 1.5707963267948966) : java.lang.Math.cos(double1);
	}

	public static float cosFromSin(float float1, float float2) {
		return Options.FASTMATH ? sin(float2 + 1.5707964F) : cosFromSinInternal(float1, float2);
	}

	private static float cosFromSinInternal(float float1, float float2) {
		float float3 = sqrt(1.0F - float1 * float1);
		float float4 = float2 + 1.5707964F;
		float float5 = float4 - (float)((int)(float4 / 6.2831855F)) * 6.2831855F;
		if ((double)float5 < 0.0) {
			float5 += 6.2831855F;
		}

		return float5 >= 3.1415927F ? -float3 : float3;
	}

	public static double cosFromSin(double double1, double double2) {
		if (Options.FASTMATH) {
			return sin(double2 + 1.5707963267948966);
		} else {
			double double3 = sqrt(1.0 - double1 * double1);
			double double4 = double2 + 1.5707963267948966;
			double double5 = double4 - (double)((int)(double4 / 6.283185307179586)) * 6.283185307179586;
			if (double5 < 0.0) {
				double5 += 6.283185307179586;
			}

			return double5 >= 3.141592653589793 ? -double3 : double3;
		}
	}

	public static float sqrt(float float1) {
		return (float)java.lang.Math.sqrt((double)float1);
	}

	public static double sqrt(double double1) {
		return java.lang.Math.sqrt(double1);
	}

	public static float invsqrt(float float1) {
		return 1.0F / (float)java.lang.Math.sqrt((double)float1);
	}

	public static double invsqrt(double double1) {
		return 1.0 / java.lang.Math.sqrt(double1);
	}

	public static float tan(float float1) {
		return (float)java.lang.Math.tan((double)float1);
	}

	public static double tan(double double1) {
		return java.lang.Math.tan(double1);
	}

	public static float acos(float float1) {
		return (float)java.lang.Math.acos((double)float1);
	}

	public static double acos(double double1) {
		return java.lang.Math.acos(double1);
	}

	public static float safeAcos(float float1) {
		if (float1 < -1.0F) {
			return 3.1415927F;
		} else {
			return float1 > 1.0F ? 0.0F : acos(float1);
		}
	}

	public static double safeAcos(double double1) {
		if (double1 < -1.0) {
			return 3.141592653589793;
		} else {
			return double1 > 1.0 ? 0.0 : acos(double1);
		}
	}

	private static double fastAtan2(double double1, double double2) {
		double double3 = double2 >= 0.0 ? double2 : -double2;
		double double4 = double1 >= 0.0 ? double1 : -double1;
		double double5 = min(double3, double4) / max(double3, double4);
		double double6 = double5 * double5;
		double double7 = ((-0.0464964749 * double6 + 0.15931422) * double6 - 0.327622764) * double6 * double5 + double5;
		if (double4 > double3) {
			double7 = 1.57079637 - double7;
		}

		if (double2 < 0.0) {
			double7 = 3.14159274 - double7;
		}

		return double1 >= 0.0 ? double7 : -double7;
	}

	public static float atan2(float float1, float float2) {
		return (float)java.lang.Math.atan2((double)float1, (double)float2);
	}

	public static double atan2(double double1, double double2) {
		return Options.FASTMATH ? fastAtan2(double1, double2) : java.lang.Math.atan2(double1, double2);
	}

	public static float asin(float float1) {
		return (float)java.lang.Math.asin((double)float1);
	}

	public static double asin(double double1) {
		return java.lang.Math.asin(double1);
	}

	public static float safeAsin(float float1) {
		return float1 <= -1.0F ? -1.5707964F : (float1 >= 1.0F ? 1.5707964F : asin(float1));
	}

	public static double safeAsin(double double1) {
		return double1 <= -1.0 ? -1.5707963267948966 : (double1 >= 1.0 ? 1.5707963267948966 : asin(double1));
	}

	public static float abs(float float1) {
		return java.lang.Math.abs(float1);
	}

	public static double abs(double double1) {
		return java.lang.Math.abs(double1);
	}

	static boolean absEqualsOne(float float1) {
		return (Float.floatToRawIntBits(float1) & Integer.MAX_VALUE) == 1065353216;
	}

	static boolean absEqualsOne(double double1) {
		return (Double.doubleToRawLongBits(double1) & Long.MAX_VALUE) == 4607182418800017408L;
	}

	public static int abs(int int1) {
		return java.lang.Math.abs(int1);
	}

	public static int max(int int1, int int2) {
		return java.lang.Math.max(int1, int2);
	}

	public static int min(int int1, int int2) {
		return java.lang.Math.min(int1, int2);
	}

	public static double min(double double1, double double2) {
		return double1 < double2 ? double1 : double2;
	}

	public static float min(float float1, float float2) {
		return float1 < float2 ? float1 : float2;
	}

	public static float max(float float1, float float2) {
		return float1 > float2 ? float1 : float2;
	}

	public static double max(double double1, double double2) {
		return double1 > double2 ? double1 : double2;
	}

	public static float clamp(float float1, float float2, float float3) {
		return max(float1, min(float2, float3));
	}

	public static double clamp(double double1, double double2, double double3) {
		return max(double1, min(double2, double3));
	}

	public static int clamp(int int1, int int2, int int3) {
		return max(int1, min(int2, int3));
	}

	public static float toRadians(float float1) {
		return (float)java.lang.Math.toRadians((double)float1);
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

	public static float floor(float float1) {
		return (float)java.lang.Math.floor((double)float1);
	}

	public static double ceil(double double1) {
		return java.lang.Math.ceil(double1);
	}

	public static float ceil(float float1) {
		return (float)java.lang.Math.ceil((double)float1);
	}

	public static long round(double double1) {
		return java.lang.Math.round(double1);
	}

	public static int round(float float1) {
		return java.lang.Math.round(float1);
	}

	public static double exp(double double1) {
		return java.lang.Math.exp(double1);
	}

	public static boolean isFinite(double double1) {
		return abs(double1) <= Double.MAX_VALUE;
	}

	public static boolean isFinite(float float1) {
		return abs(float1) <= Float.MAX_VALUE;
	}

	public static float fma(float float1, float float2, float float3) {
		return Runtime.HAS_Math_fma ? java.lang.Math.fma(float1, float2, float3) : float1 * float2 + float3;
	}

	public static double fma(double double1, double double2, double double3) {
		return Runtime.HAS_Math_fma ? java.lang.Math.fma(double1, double2, double3) : double1 * double2 + double3;
	}

	public static int roundUsing(float float1, int int1) {
		switch (int1) {
		case 0: 
			return (int)float1;
		
		case 1: 
			return (int)java.lang.Math.ceil((double)float1);
		
		case 2: 
			return (int)java.lang.Math.floor((double)float1);
		
		case 3: 
			return roundHalfEven(float1);
		
		case 4: 
			return roundHalfDown(float1);
		
		case 5: 
			return roundHalfUp(float1);
		
		default: 
			throw new UnsupportedOperationException();
		
		}
	}

	public static int roundUsing(double double1, int int1) {
		switch (int1) {
		case 0: 
			return (int)double1;
		
		case 1: 
			return (int)java.lang.Math.ceil(double1);
		
		case 2: 
			return (int)java.lang.Math.floor(double1);
		
		case 3: 
			return roundHalfEven(double1);
		
		case 4: 
			return roundHalfDown(double1);
		
		case 5: 
			return roundHalfUp(double1);
		
		default: 
			throw new UnsupportedOperationException();
		
		}
	}

	public static float lerp(float float1, float float2, float float3) {
		return fma(float2 - float1, float3, float1);
	}

	public static double lerp(double double1, double double2, double double3) {
		return fma(double2 - double1, double3, double1);
	}

	public static float biLerp(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = lerp(float1, float2, float5);
		float float8 = lerp(float3, float4, float5);
		return lerp(float7, float8, float6);
	}

	public static double biLerp(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = lerp(double1, double2, double5);
		double double8 = lerp(double3, double4, double5);
		return lerp(double7, double8, double6);
	}

	public static float triLerp(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11) {
		float float12 = lerp(float1, float2, float9);
		float float13 = lerp(float3, float4, float9);
		float float14 = lerp(float5, float6, float9);
		float float15 = lerp(float7, float8, float9);
		float float16 = lerp(float12, float13, float10);
		float float17 = lerp(float14, float15, float10);
		return lerp(float16, float17, float11);
	}

	public static double triLerp(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11) {
		double double12 = lerp(double1, double2, double9);
		double double13 = lerp(double3, double4, double9);
		double double14 = lerp(double5, double6, double9);
		double double15 = lerp(double7, double8, double9);
		double double16 = lerp(double12, double13, double10);
		double double17 = lerp(double14, double15, double10);
		return lerp(double16, double17, double11);
	}

	public static int roundHalfEven(float float1) {
		return (int)java.lang.Math.rint((double)float1);
	}

	public static int roundHalfDown(float float1) {
		return float1 > 0.0F ? (int)java.lang.Math.ceil((double)float1 - 0.5) : (int)java.lang.Math.floor((double)float1 + 0.5);
	}

	public static int roundHalfUp(float float1) {
		return float1 > 0.0F ? (int)java.lang.Math.floor((double)float1 + 0.5) : (int)java.lang.Math.ceil((double)float1 - 0.5);
	}

	public static int roundHalfEven(double double1) {
		return (int)java.lang.Math.rint(double1);
	}

	public static int roundHalfDown(double double1) {
		return double1 > 0.0 ? (int)java.lang.Math.ceil(double1 - 0.5) : (int)java.lang.Math.floor(double1 + 0.5);
	}

	public static int roundHalfUp(double double1) {
		return double1 > 0.0 ? (int)java.lang.Math.floor(double1 + 0.5) : (int)java.lang.Math.ceil(double1 - 0.5);
	}

	public static double random() {
		return java.lang.Math.random();
	}

	public static double signum(double double1) {
		return java.lang.Math.signum(double1);
	}

	public static float signum(float float1) {
		return java.lang.Math.signum(float1);
	}

	public static int signum(int int1) {
		int int2 = Integer.signum(int1);
		return int2;
	}

	public static int signum(long long1) {
		int int1 = Long.signum(long1);
		return int1;
	}

	static  {
		lookupBits = Options.SIN_LOOKUP_BITS;
		lookupTableSize = 1 << lookupBits;
		lookupTableSizeMinus1 = lookupTableSize - 1;
		lookupTableSizeWithMargin = lookupTableSize + 1;
		pi2OverLookupSize = 6.2831855F / (float)lookupTableSize;
		lookupSizeOverPi2 = (float)lookupTableSize / 6.2831855F;
	if (Options.FASTMATH && Options.SIN_LOOKUP) {
		sinTable = new float[lookupTableSizeWithMargin];
		for (int var0 = 0; var0 < lookupTableSizeWithMargin; ++var0) {
			double var1 = (double)((float)var0 * pi2OverLookupSize);
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
