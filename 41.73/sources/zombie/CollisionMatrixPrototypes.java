package zombie;

import java.util.HashMap;


public class CollisionMatrixPrototypes {
	public static CollisionMatrixPrototypes instance = new CollisionMatrixPrototypes();
	public HashMap Map = new HashMap();

	public int ToBitMatrix(boolean[][][] booleanArrayArrayArray) {
		int int1 = 0;
		for (int int2 = 0; int2 < 3; ++int2) {
			for (int int3 = 0; int3 < 3; ++int3) {
				for (int int4 = 0; int4 < 3; ++int4) {
					if (booleanArrayArrayArray[int2][int3][int4]) {
						int1 = BitMatrix.Set(int1, int2 - 1, int3 - 1, int4 - 1, true);
					}
				}
			}
		}

		return int1;
	}

	public boolean[][][] Add(int int1) {
		if (this.Map.containsKey(int1)) {
			return (boolean[][][])this.Map.get(int1);
		} else {
			boolean[][][] booleanArrayArrayArray = new boolean[3][3][3];
			for (int int2 = 0; int2 < 3; ++int2) {
				for (int int3 = 0; int3 < 3; ++int3) {
					for (int int4 = 0; int4 < 3; ++int4) {
						booleanArrayArrayArray[int2][int3][int4] = BitMatrix.Is(int1, int2 - 1, int3 - 1, int4 - 1);
					}
				}
			}

			this.Map.put(int1, booleanArrayArrayArray);
			return booleanArrayArrayArray;
		}
	}
}
