package zombie.util;


public final class PooledObjectArrayObject extends PooledArrayObject {

	public void onReleased() {
		int int1 = 0;
		for (int int2 = this.length(); int1 < int2; ++int1) {
			((IPooledObject)this.get(int1)).release();
		}
	}
}
