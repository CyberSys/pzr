package zombie.util;

import java.util.function.Function;


public class PooledArrayObject extends PooledObject {
	private Object[] m_array = null;

	public Object[] array() {
		return this.m_array;
	}

	public int length() {
		return this.m_array.length;
	}

	public Object get(int int1) {
		return this.m_array[int1];
	}

	public void set(int int1, Object object) {
		this.m_array[int1] = object;
	}

	protected void initCapacity(int int1, Function function) {
		if (this.m_array == null || this.m_array.length != int1) {
			this.m_array = (Object[])function.apply(int1);
		}
	}

	public boolean isEmpty() {
		return this.m_array == null || this.m_array.length == 0;
	}
}
