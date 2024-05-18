package zombie.iso;


public class CellStreamer {
	public static int WidthTiles = 50;
	public static int HeightTiles = 50;

	public static void LoadInitialStream(int int1, int int2, int int3, int int4) {
		int int5 = int3 / WidthTiles;
		int int6 = int4 / HeightTiles;
		int int7 = int1;
		int int8 = int2;
		--int5;
		--int6;
		int int9 = int5 + 2;
		int int10 = int6 + 2;
		if (int5 < 0) {
			int5 += 3;
			int7 = int1 - 1;
		}

		if (int6 < 0) {
			int6 += 3;
			int8 = int2 - 1;
		}

		if (int5 > 2) {
			int5 -= 3;
			++int7;
		}

		if (int6 > 2) {
			int6 -= 3;
			++int8;
		}
	}
}
