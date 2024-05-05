package zombie;


public class BitMatrix {

	public static boolean Is(int int1, int int2, int int3, int int4) {
		return (1 << (int2 + 1) * 9 + (int3 + 1) * 3 + int4 + 1 & int1) == 1 << (int2 + 1) * 9 + (int3 + 1) * 3 + int4 + 1;
	}

	public static int Set(int int1, int int2, int int3, int int4, boolean boolean1) {
		if (boolean1) {
			int1 |= 1 << (int2 + 1) * 9 + (int3 + 1) * 3 + int4 + 1;
		} else {
			int1 &= ~(1 << (int2 + 1) * 9 + (int3 + 1) * 3 + int4 + 1);
		}

		return int1;
	}
}
