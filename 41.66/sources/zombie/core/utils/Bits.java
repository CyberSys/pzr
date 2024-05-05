package zombie.core.utils;

import zombie.core.Core;
import zombie.core.math.PZMath;


public class Bits {
	public static final boolean ENABLED = true;
	public static final int BIT_0 = 0;
	public static final int BIT_1 = 1;
	public static final int BIT_2 = 2;
	public static final int BIT_3 = 4;
	public static final int BIT_4 = 8;
	public static final int BIT_5 = 16;
	public static final int BIT_6 = 32;
	public static final int BIT_7 = 64;
	public static final int BIT_BYTE_MAX = 64;
	public static final int BIT_8 = 128;
	public static final int BIT_9 = 256;
	public static final int BIT_10 = 512;
	public static final int BIT_11 = 1024;
	public static final int BIT_12 = 2048;
	public static final int BIT_13 = 4096;
	public static final int BIT_14 = 8192;
	public static final int BIT_15 = 16384;
	public static final int BIT_SHORT_MAX = 16384;
	public static final int BIT_16 = 32768;
	public static final int BIT_17 = 65536;
	public static final int BIT_18 = 131072;
	public static final int BIT_19 = 262144;
	public static final int BIT_20 = 524288;
	public static final int BIT_21 = 1048576;
	public static final int BIT_22 = 2097152;
	public static final int BIT_23 = 4194304;
	public static final int BIT_24 = 8388608;
	public static final int BIT_25 = 16777216;
	public static final int BIT_26 = 33554432;
	public static final int BIT_27 = 67108864;
	public static final int BIT_28 = 134217728;
	public static final int BIT_29 = 268435456;
	public static final int BIT_30 = 536870912;
	public static final int BIT_31 = 1073741824;
	public static final int BIT_INT_MAX = 1073741824;
	public static final long BIT_32 = 2147483648L;
	public static final long BIT_33 = 4294967296L;
	public static final long BIT_34 = 8589934592L;
	public static final long BIT_35 = 17179869184L;
	public static final long BIT_36 = 34359738368L;
	public static final long BIT_37 = 68719476736L;
	public static final long BIT_38 = 137438953472L;
	public static final long BIT_39 = 274877906944L;
	public static final long BIT_40 = 549755813888L;
	public static final long BIT_41 = 1099511627776L;
	public static final long BIT_42 = 2199023255552L;
	public static final long BIT_43 = 4398046511104L;
	public static final long BIT_44 = 8796093022208L;
	public static final long BIT_45 = 17592186044416L;
	public static final long BIT_46 = 35184372088832L;
	public static final long BIT_47 = 70368744177664L;
	public static final long BIT_48 = 140737488355328L;
	public static final long BIT_49 = 281474976710656L;
	public static final long BIT_50 = 562949953421312L;
	public static final long BIT_51 = 1125899906842624L;
	public static final long BIT_52 = 2251799813685248L;
	public static final long BIT_53 = 4503599627370496L;
	public static final long BIT_54 = 9007199254740992L;
	public static final long BIT_55 = 18014398509481984L;
	public static final long BIT_56 = 36028797018963968L;
	public static final long BIT_57 = 72057594037927936L;
	public static final long BIT_58 = 144115188075855872L;
	public static final long BIT_59 = 288230376151711744L;
	public static final long BIT_60 = 576460752303423488L;
	public static final long BIT_61 = 1152921504606846976L;
	public static final long BIT_62 = 2305843009213693952L;
	public static final long BIT_63 = 4611686018427387904L;
	public static final long BIT_LONG_MAX = 4611686018427387904L;
	private static StringBuilder sb = new StringBuilder();

	public static byte packFloatUnitToByte(float float1) {
		if (float1 < 0.0F || float1 > 1.0F) {
			if (Core.bDebug) {
				throw new RuntimeException("UtilsIO Cannot pack float units out of the range 0.0 to 1.0");
			}

			float1 = PZMath.clamp(float1, 0.0F, 1.0F);
		}

		return (byte)((int)(float1 * 255.0F + -128.0F));
	}

	public static float unpackByteToFloatUnit(byte byte1) {
		return (float)(byte1 - -128) / 255.0F;
	}

	public static byte addFlags(byte byte1, int int1) {
		if (int1 >= 0 && int1 <= 64) {
			return (byte)(byte1 | int1);
		} else {
			throw new RuntimeException("Cannot add flags, exceeding byte bounds or negative number flags. (" + int1 + ")");
		}
	}

	public static byte addFlags(byte byte1, long long1) {
		if (long1 >= 0L && long1 <= 64L) {
			return (byte)((int)((long)byte1 | long1));
		} else {
			throw new RuntimeException("Cannot add flags, exceeding byte bounds or negative number flags. (" + long1 + ")");
		}
	}

	public static short addFlags(short short1, int int1) {
		if (int1 >= 0 && int1 <= 16384) {
			return (short)(short1 | int1);
		} else {
			throw new RuntimeException("Cannot add flags, exceeding short bounds or negative number flags. (" + int1 + ")");
		}
	}

	public static short addFlags(short short1, long long1) {
		if (long1 >= 0L && long1 <= 16384L) {
			return (short)((int)((long)short1 | long1));
		} else {
			throw new RuntimeException("Cannot add flags, exceeding short bounds or negative number flags. (" + long1 + ")");
		}
	}

	public static int addFlags(int int1, int int2) {
		if (int2 >= 0 && int2 <= 1073741824) {
			return int1 | int2;
		} else {
			throw new RuntimeException("Cannot add flags, exceeding short bounds or negative number flags. (" + int2 + ")");
		}
	}

	public static int addFlags(int int1, long long1) {
		if (long1 >= 0L && long1 <= 1073741824L) {
			return (int)((long)int1 | long1);
		} else {
			throw new RuntimeException("Cannot add flags, exceeding integer bounds or negative number flags. (" + long1 + ")");
		}
	}

	public static long addFlags(long long1, int int1) {
		if (int1 >= 0 && (long)int1 <= 4611686018427387904L) {
			return long1 | (long)int1;
		} else {
			throw new RuntimeException("Cannot add flags, exceeding long bounds or negative number flags. (" + int1 + ")");
		}
	}

	public static long addFlags(long long1, long long2) {
		if (long2 >= 0L && long2 <= 4611686018427387904L) {
			return long1 | long2;
		} else {
			throw new RuntimeException("Cannot add flags, exceeding long bounds or negative number flags. (" + long2 + ")");
		}
	}

	public static boolean hasFlags(byte byte1, int int1) {
		return checkFlags(byte1, int1, 64, Bits.CompareOption.ContainsAll);
	}

	public static boolean hasFlags(byte byte1, long long1) {
		return checkFlags((long)byte1, long1, 64L, Bits.CompareOption.ContainsAll);
	}

	public static boolean hasEitherFlags(byte byte1, int int1) {
		return checkFlags(byte1, int1, 64, Bits.CompareOption.HasEither);
	}

	public static boolean hasEitherFlags(byte byte1, long long1) {
		return checkFlags((long)byte1, long1, 64L, Bits.CompareOption.HasEither);
	}

	public static boolean notHasFlags(byte byte1, int int1) {
		return checkFlags(byte1, int1, 64, Bits.CompareOption.NotHas);
	}

	public static boolean notHasFlags(byte byte1, long long1) {
		return checkFlags((long)byte1, long1, 64L, Bits.CompareOption.NotHas);
	}

	public static boolean hasFlags(short short1, int int1) {
		return checkFlags(short1, int1, 16384, Bits.CompareOption.ContainsAll);
	}

	public static boolean hasFlags(short short1, long long1) {
		return checkFlags((long)short1, long1, 16384L, Bits.CompareOption.ContainsAll);
	}

	public static boolean hasEitherFlags(short short1, int int1) {
		return checkFlags(short1, int1, 16384, Bits.CompareOption.HasEither);
	}

	public static boolean hasEitherFlags(short short1, long long1) {
		return checkFlags((long)short1, long1, 16384L, Bits.CompareOption.HasEither);
	}

	public static boolean notHasFlags(short short1, int int1) {
		return checkFlags(short1, int1, 16384, Bits.CompareOption.NotHas);
	}

	public static boolean notHasFlags(short short1, long long1) {
		return checkFlags((long)short1, long1, 16384L, Bits.CompareOption.NotHas);
	}

	public static boolean hasFlags(int int1, int int2) {
		return checkFlags(int1, int2, 1073741824, Bits.CompareOption.ContainsAll);
	}

	public static boolean hasFlags(int int1, long long1) {
		return checkFlags((long)int1, long1, 1073741824L, Bits.CompareOption.ContainsAll);
	}

	public static boolean hasEitherFlags(int int1, int int2) {
		return checkFlags(int1, int2, 1073741824, Bits.CompareOption.HasEither);
	}

	public static boolean hasEitherFlags(int int1, long long1) {
		return checkFlags((long)int1, long1, 1073741824L, Bits.CompareOption.HasEither);
	}

	public static boolean notHasFlags(int int1, int int2) {
		return checkFlags(int1, int2, 1073741824, Bits.CompareOption.NotHas);
	}

	public static boolean notHasFlags(int int1, long long1) {
		return checkFlags((long)int1, long1, 1073741824L, Bits.CompareOption.NotHas);
	}

	public static boolean hasFlags(long long1, int int1) {
		return checkFlags(long1, (long)int1, 4611686018427387904L, Bits.CompareOption.ContainsAll);
	}

	public static boolean hasFlags(long long1, long long2) {
		return checkFlags(long1, long2, 4611686018427387904L, Bits.CompareOption.ContainsAll);
	}

	public static boolean hasEitherFlags(long long1, int int1) {
		return checkFlags(long1, (long)int1, 4611686018427387904L, Bits.CompareOption.HasEither);
	}

	public static boolean hasEitherFlags(long long1, long long2) {
		return checkFlags(long1, long2, 4611686018427387904L, Bits.CompareOption.HasEither);
	}

	public static boolean notHasFlags(long long1, int int1) {
		return checkFlags(long1, (long)int1, 4611686018427387904L, Bits.CompareOption.NotHas);
	}

	public static boolean notHasFlags(long long1, long long2) {
		return checkFlags(long1, long2, 4611686018427387904L, Bits.CompareOption.NotHas);
	}

	public static boolean checkFlags(int int1, int int2, int int3, Bits.CompareOption compareOption) {
		if (int2 >= 0 && int2 <= int3) {
			if (compareOption == Bits.CompareOption.ContainsAll) {
				return (int1 & int2) == int2;
			} else if (compareOption == Bits.CompareOption.HasEither) {
				return (int1 & int2) != 0;
			} else if (compareOption == Bits.CompareOption.NotHas) {
				return (int1 & int2) == 0;
			} else {
				throw new RuntimeException("No valid compare option.");
			}
		} else {
			throw new RuntimeException("Cannot check for flags, exceeding byte bounds or negative number flags. (" + int2 + ")");
		}
	}

	public static boolean checkFlags(long long1, long long2, long long3, Bits.CompareOption compareOption) {
		if (long2 >= 0L && long2 <= long3) {
			if (compareOption == Bits.CompareOption.ContainsAll) {
				return (long1 & long2) == long2;
			} else if (compareOption == Bits.CompareOption.HasEither) {
				return (long1 & long2) != 0L;
			} else if (compareOption == Bits.CompareOption.NotHas) {
				return (long1 & long2) == 0L;
			} else {
				throw new RuntimeException("No valid compare option.");
			}
		} else {
			throw new RuntimeException("Cannot check for flags, exceeding byte bounds or negative number flags. (" + long2 + ")");
		}
	}

	public static int getLen(byte byte1) {
		return 1;
	}

	public static int getLen(short short1) {
		return 2;
	}

	public static int getLen(int int1) {
		return 4;
	}

	public static int getLen(long long1) {
		return 8;
	}

	private static void clearStringBuilder() {
		if (sb.length() > 0) {
			sb.delete(0, sb.length());
		}
	}

	public static String getBitsString(byte byte1) {
		return getBitsString((long)byte1, 8);
	}

	public static String getBitsString(short short1) {
		return getBitsString((long)short1, 16);
	}

	public static String getBitsString(int int1) {
		return getBitsString((long)int1, 32);
	}

	public static String getBitsString(long long1) {
		return getBitsString(long1, 64);
	}

	private static String getBitsString(long long1, int int1) {
		clearStringBuilder();
		if (long1 != 0L) {
			sb.append("Bits(" + (int1 - 1) + "): ");
			long long2 = 1L;
			for (int int2 = 1; int2 < int1; ++int2) {
				sb.append("[" + int2 + "]");
				if ((long1 & long2) == long2) {
					sb.append("1");
				} else {
					sb.append("0");
				}

				if (int2 < int1 - 1) {
					sb.append(" ");
				}

				long2 *= 2L;
			}
		} else {
			sb.append("No bits saved, 0x0.");
		}

		return sb.toString();
	}

	public static enum CompareOption {

		ContainsAll,
		HasEither,
		NotHas;

		private static Bits.CompareOption[] $values() {
			return new Bits.CompareOption[]{ContainsAll, HasEither, NotHas};
		}
	}
}
