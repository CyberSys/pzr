package zombie.util;

import java.util.List;


public interface IPooledObject {

	Pool getPool();

	void setPool(Pool pool);

	void release();

	boolean isFree();

	void setFree(boolean boolean1);

	default void onReleased() {
	}

	static void release(IPooledObject[] iPooledObjectArray) {
		int int1 = 0;
		for (int int2 = iPooledObjectArray.length; int1 < int2; ++int1) {
			Pool.tryRelease(iPooledObjectArray[int1]);
		}
	}

	static void tryReleaseAndBlank(IPooledObject[] iPooledObjectArray) {
		if (iPooledObjectArray != null) {
			releaseAndBlank(iPooledObjectArray);
		}
	}

	static void releaseAndBlank(IPooledObject[] iPooledObjectArray) {
		int int1 = 0;
		for (int int2 = iPooledObjectArray.length; int1 < int2; ++int1) {
			iPooledObjectArray[int1] = Pool.tryRelease(iPooledObjectArray[int1]);
		}
	}

	static void release(List list) {
		int int1 = 0;
		for (int int2 = list.size(); int1 < int2; ++int1) {
			Pool.tryRelease((IPooledObject)list.get(int1));
		}

		list.clear();
	}
}
