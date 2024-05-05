package zombie.util;

import zombie.util.list.PZArrayUtil;


public final class PooledFloatArrayObject extends PooledObject {
	private static final Pool s_pool = new Pool(PooledFloatArrayObject::new);
	private float[] m_array;

	public PooledFloatArrayObject() {
		this.m_array = PZArrayUtil.emptyFloatArray;
	}

	public static PooledFloatArrayObject alloc(int int1) {
		PooledFloatArrayObject pooledFloatArrayObject = (PooledFloatArrayObject)s_pool.alloc();
		pooledFloatArrayObject.initCapacity(int1);
		return pooledFloatArrayObject;
	}

	public static PooledFloatArrayObject toArray(PooledFloatArrayObject pooledFloatArrayObject) {
		if (pooledFloatArrayObject == null) {
			return null;
		} else {
			int int1 = pooledFloatArrayObject.length();
			PooledFloatArrayObject pooledFloatArrayObject2 = alloc(int1);
			if (int1 > 0) {
				System.arraycopy(pooledFloatArrayObject.array(), 0, pooledFloatArrayObject2.array(), 0, int1);
			}

			return pooledFloatArrayObject2;
		}
	}

	private void initCapacity(int int1) {
		if (this.m_array.length != int1) {
			this.m_array = new float[int1];
		}
	}

	public float[] array() {
		return this.m_array;
	}

	public float get(int int1) {
		return this.m_array[int1];
	}

	public void set(int int1, float float1) {
		this.m_array[int1] = float1;
	}

	public int length() {
		return this.m_array.length;
	}
}
