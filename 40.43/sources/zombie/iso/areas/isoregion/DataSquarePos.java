package zombie.iso.areas.isoregion;

import java.util.ArrayDeque;
import zombie.debug.DebugLog;


public class DataSquarePos {
	private static ArrayDeque pool = new ArrayDeque();
	public int x;
	public int y;
	public int z;

	static DataSquarePos alloc(int int1, int int2, int int3) {
		DataSquarePos dataSquarePos = !pool.isEmpty() ? (DataSquarePos)pool.pop() : new DataSquarePos();
		dataSquarePos.set(int1, int2, int3);
		return dataSquarePos;
	}

	static void release(DataSquarePos dataSquarePos) {
		assert !pool.contains(dataSquarePos);
		if (IsoRegion.PRINT_D && pool.contains(dataSquarePos)) {
			DebugLog.log("Warning: DataSquarePos.release Trying to release a ChunkRegion twice.");
		} else {
			pool.push(dataSquarePos.reset());
		}
	}

	private DataSquarePos() {
	}

	private DataSquarePos reset() {
		return this;
	}

	public void set(int int1, int int2, int int3) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
	}
}
