package se.krka.kahlua.j2se;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;


public class MathLib implements JavaFunction {
	private static final int ABS = 0;
	private static final int ACOS = 1;
	private static final int ASIN = 2;
	private static final int ATAN = 3;
	private static final int ATAN2 = 4;
	private static final int CEIL = 5;
	private static final int COS = 6;
	private static final int COSH = 7;
	private static final int DEG = 8;
	private static final int EXP = 9;
	private static final int FLOOR = 10;
	private static final int FMOD = 11;
	private static final int FREXP = 12;
	private static final int LDEXP = 13;
	private static final int LOG = 14;
	private static final int LOG10 = 15;
	private static final int MODF = 16;
	private static final int POW = 17;
	private static final int RAD = 18;
	private static final int SIN = 19;
	private static final int SINH = 20;
	private static final int SQRT = 21;
	private static final int TAN = 22;
	private static final int TANH = 23;
	private static final int NUM_FUNCTIONS = 24;
	private static final String[] names = new String[24];
	private static final MathLib[] functions;
	private final int index;
	private static final double LN2_INV;

	public MathLib(int int1) {
		this.index = int1;
	}

	public static void register(Platform platform, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		kahluaTable.rawset("math", kahluaTable2);
		kahluaTable2.rawset("pi", KahluaUtil.toDouble(3.141592653589793));
		kahluaTable2.rawset("huge", KahluaUtil.toDouble(Double.POSITIVE_INFINITY));
		for (int int1 = 0; int1 < 24; ++int1) {
			kahluaTable2.rawset(names[int1], functions[int1]);
		}
	}

	public String toString() {
		return "math." + names[this.index];
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		switch (this.index) {
		case 0: 
			return abs(luaCallFrame, int1);
		
		case 1: 
			return acos(luaCallFrame, int1);
		
		case 2: 
			return asin(luaCallFrame, int1);
		
		case 3: 
			return atan(luaCallFrame, int1);
		
		case 4: 
			return atan2(luaCallFrame, int1);
		
		case 5: 
			return ceil(luaCallFrame, int1);
		
		case 6: 
			return cos(luaCallFrame, int1);
		
		case 7: 
			return cosh(luaCallFrame, int1);
		
		case 8: 
			return deg(luaCallFrame, int1);
		
		case 9: 
			return exp(luaCallFrame, int1);
		
		case 10: 
			return floor(luaCallFrame, int1);
		
		case 11: 
			return fmod(luaCallFrame, int1);
		
		case 12: 
			return frexp(luaCallFrame, int1);
		
		case 13: 
			return ldexp(luaCallFrame, int1);
		
		case 14: 
			return log(luaCallFrame, int1);
		
		case 15: 
			return log10(luaCallFrame, int1);
		
		case 16: 
			return modf(luaCallFrame, int1);
		
		case 17: 
			return pow(luaCallFrame, int1);
		
		case 18: 
			return rad(luaCallFrame, int1);
		
		case 19: 
			return sin(luaCallFrame, int1);
		
		case 20: 
			return sinh(luaCallFrame, int1);
		
		case 21: 
			return sqrt(luaCallFrame, int1);
		
		case 22: 
			return tan(luaCallFrame, int1);
		
		case 23: 
			return tanh(luaCallFrame, int1);
		
		default: 
			return 0;
		
		}
	}

	private static int abs(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[0]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.abs(double1)));
		return 1;
	}

	private static int ceil(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[5]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.ceil(double1)));
		return 1;
	}

	private static int floor(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[10]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.floor(double1)));
		return 1;
	}

	public static boolean isNegative(double double1) {
		return Double.doubleToLongBits(double1) < 0L;
	}

	public static double round(double double1) {
		if (double1 < 0.0) {
			return -round(-double1);
		} else {
			double1 += 0.5;
			double double2 = Math.floor(double1);
			return double2 == double1 ? double2 - (double)((long)double2 & 1L) : double2;
		}
	}

	private static int modf(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[16]);
		boolean boolean1 = false;
		if (isNegative(double1)) {
			boolean1 = true;
			double1 = -double1;
		}

		double double2 = Math.floor(double1);
		double double3;
		if (Double.isInfinite(double2)) {
			double3 = 0.0;
		} else {
			double3 = double1 - double2;
		}

		if (boolean1) {
			double2 = -double2;
			double3 = -double3;
		}

		luaCallFrame.push(KahluaUtil.toDouble(double2), KahluaUtil.toDouble(double3));
		return 2;
	}

	private static int fmod(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[11]);
		double double2 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[11]);
		double double3;
		if (!Double.isInfinite(double1) && !Double.isNaN(double1)) {
			if (Double.isInfinite(double2)) {
				double3 = double1;
			} else {
				double2 = Math.abs(double2);
				boolean boolean1 = false;
				if (isNegative(double1)) {
					boolean1 = true;
					double1 = -double1;
				}

				double3 = double1 - Math.floor(double1 / double2) * double2;
				if (boolean1) {
					double3 = -double3;
				}
			}
		} else {
			double3 = Double.NaN;
		}

		luaCallFrame.push(KahluaUtil.toDouble(double3));
		return 1;
	}

	private static int cosh(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[7]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.cosh(double1)));
		return 1;
	}

	private static int sinh(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[20]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.sinh(double1)));
		return 1;
	}

	private static int tanh(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[23]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.tanh(double1)));
		return 1;
	}

	private static int deg(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[8]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.toDegrees(double1)));
		return 1;
	}

	private static int rad(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[18]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.toRadians(double1)));
		return 1;
	}

	private static int acos(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[1]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.acos(double1)));
		return 1;
	}

	private static int asin(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[2]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.asin(double1)));
		return 1;
	}

	private static int atan(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[3]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.atan(double1)));
		return 1;
	}

	private static int atan2(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[4]);
		double double2 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[4]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.atan2(double1, double2)));
		return 1;
	}

	private static int cos(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[6]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.cos(double1)));
		return 1;
	}

	private static int sin(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[19]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.sin(double1)));
		return 1;
	}

	private static int tan(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[22]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.tan(double1)));
		return 1;
	}

	private static int sqrt(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[21]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.sqrt(double1)));
		return 1;
	}

	private static int exp(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[9]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.exp(double1)));
		return 1;
	}

	private static int pow(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[17]);
		double double2 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[17]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.pow(double1, double2)));
		return 1;
	}

	private static int log(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[14]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.log(double1)));
		return 1;
	}

	private static int log10(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[15]);
		luaCallFrame.push(KahluaUtil.toDouble(Math.log10(double1)));
		return 1;
	}

	private static int frexp(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[12]);
		double double2;
		double double3;
		if (!Double.isInfinite(double1) && !Double.isNaN(double1)) {
			double2 = Math.ceil(Math.log(double1) * LN2_INV);
			int int2 = 1 << (int)double2;
			double3 = double1 / (double)int2;
		} else {
			double2 = 0.0;
			double3 = double1;
		}

		luaCallFrame.push(KahluaUtil.toDouble(double3), KahluaUtil.toDouble(double2));
		return 2;
	}

	private static int ldexp(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "Not enough arguments");
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[13]);
		double double2 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[13]);
		double double3 = double1 + double2;
		double double4;
		if (!Double.isInfinite(double3) && !Double.isNaN(double3)) {
			int int2 = (int)double2;
			double4 = double1 * (double)(1 << int2);
		} else {
			double4 = double1;
		}

		luaCallFrame.push(KahluaUtil.toDouble(double4));
		return 1;
	}

	static  {
		names[0] = "abs";
		names[1] = "acos";
		names[2] = "asin";
		names[3] = "atan";
		names[4] = "atan2";
		names[5] = "ceil";
		names[6] = "cos";
		names[7] = "cosh";
		names[8] = "deg";
		names[9] = "exp";
		names[10] = "floor";
		names[11] = "fmod";
		names[12] = "frexp";
		names[13] = "ldexp";
		names[14] = "log";
		names[15] = "log10";
		names[16] = "modf";
		names[17] = "pow";
		names[18] = "rad";
		names[19] = "sin";
		names[20] = "sinh";
		names[21] = "sqrt";
		names[22] = "tan";
		names[23] = "tanh";
		functions = new MathLib[24];
	for (int var0 = 0; var0 < 24; ++var0) {
		functions[var0] = new MathLib(var0);
	}

		LN2_INV = 1.0 / Math.log(2.0);
	}
}
