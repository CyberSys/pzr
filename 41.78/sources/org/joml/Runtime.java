package org.joml;

import java.text.NumberFormat;


public final class Runtime {
	public static final boolean HAS_floatToRawIntBits = hasFloatToRawIntBits();
	public static final boolean HAS_doubleToRawLongBits = hasDoubleToRawLongBits();
	public static final boolean HAS_Long_rotateLeft = hasLongRotateLeft();
	public static final boolean HAS_Math_fma;

	private static boolean hasMathFma() {
		try {
			java.lang.Math.class.getDeclaredMethod("fma", Float.TYPE, Float.TYPE, Float.TYPE);
			return true;
		} catch (NoSuchMethodException noSuchMethodException) {
			return false;
		}
	}

	private Runtime() {
	}

	private static boolean hasFloatToRawIntBits() {
		try {
			Float.class.getDeclaredMethod("floatToRawIntBits", Float.TYPE);
			return true;
		} catch (NoSuchMethodException noSuchMethodException) {
			return false;
		}
	}

	private static boolean hasDoubleToRawLongBits() {
		try {
			Double.class.getDeclaredMethod("doubleToRawLongBits", Double.TYPE);
			return true;
		} catch (NoSuchMethodException noSuchMethodException) {
			return false;
		}
	}

	private static boolean hasLongRotateLeft() {
		try {
			Long.class.getDeclaredMethod("rotateLeft", Long.TYPE, Integer.TYPE);
			return true;
		} catch (NoSuchMethodException noSuchMethodException) {
			return false;
		}
	}

	public static int floatToIntBits(float float1) {
		return HAS_floatToRawIntBits ? floatToIntBits1_3(float1) : floatToIntBits1_2(float1);
	}

	private static int floatToIntBits1_3(float float1) {
		return Float.floatToRawIntBits(float1);
	}

	private static int floatToIntBits1_2(float float1) {
		return Float.floatToIntBits(float1);
	}

	public static long doubleToLongBits(double double1) {
		return HAS_doubleToRawLongBits ? doubleToLongBits1_3(double1) : doubleToLongBits1_2(double1);
	}

	private static long doubleToLongBits1_3(double double1) {
		return Double.doubleToRawLongBits(double1);
	}

	private static long doubleToLongBits1_2(double double1) {
		return Double.doubleToLongBits(double1);
	}

	public static String formatNumbers(String string) {
		StringBuffer stringBuffer = new StringBuffer();
		int int1 = Integer.MIN_VALUE;
		for (int int2 = 0; int2 < string.length(); ++int2) {
			char char1 = string.charAt(int2);
			if (char1 == 'E') {
				int1 = int2;
			} else {
				if (char1 == ' ' && int1 == int2 - 1) {
					stringBuffer.append('+');
					continue;
				}

				if (Character.isDigit(char1) && int1 == int2 - 1) {
					stringBuffer.append('+');
				}
			}

			stringBuffer.append(char1);
		}

		return stringBuffer.toString();
	}

	public static String format(double double1, NumberFormat numberFormat) {
		if (Double.isNaN(double1)) {
			return padLeft(numberFormat, " NaN");
		} else {
			return Double.isInfinite(double1) ? padLeft(numberFormat, double1 > 0.0 ? " +Inf" : " -Inf") : numberFormat.format(double1);
		}
	}

	private static String padLeft(NumberFormat numberFormat, String string) {
		int int1 = numberFormat.format(0.0).length();
		StringBuffer stringBuffer = new StringBuffer();
		for (int int2 = 0; int2 < int1 - string.length() + 1; ++int2) {
			stringBuffer.append(" ");
		}

		return stringBuffer.append(string).toString();
	}

	public static boolean equals(float float1, float float2, float float3) {
		return Float.floatToIntBits(float1) == Float.floatToIntBits(float2) || Math.abs(float1 - float2) <= float3;
	}

	public static boolean equals(double double1, double double2, double double3) {
		return Double.doubleToLongBits(double1) == Double.doubleToLongBits(double2) || Math.abs(double1 - double2) <= double3;
	}

	static  {
		HAS_Math_fma = Options.USE_MATH_FMA && hasMathFma();
	}
}
