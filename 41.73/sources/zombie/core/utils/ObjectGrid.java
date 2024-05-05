package zombie.core.utils;

import java.io.Serializable;
import java.util.Arrays;


public class ObjectGrid implements Serializable,Cloneable {
	private static final long serialVersionUID = 1L;
	private final int width;
	private final int height;
	private final Object[] value;

	public ObjectGrid(int int1, int int2) {
		this.width = int1;
		this.height = int2;
		this.value = new Object[int1 * int2];
	}

	public ObjectGrid clone() throws CloneNotSupportedException {
		ObjectGrid objectGrid = new ObjectGrid(this.width, this.height);
		System.arraycopy(this.value, 0, objectGrid.value, 0, this.value.length);
		return objectGrid;
	}

	public void clear() {
		Arrays.fill(this.value, 0);
	}

	public void fill(Object object) {
		Arrays.fill(this.value, object);
	}

	private int getIndex(int int1, int int2) {
		return int1 >= 0 && int2 >= 0 && int1 < this.width && int2 < this.height ? int1 + int2 * this.width : -1;
	}

	public Object getValue(int int1, int int2) {
		int int3 = this.getIndex(int1, int2);
		return int3 == -1 ? null : this.value[int3];
	}

	public void setValue(int int1, int int2, Object object) {
		int int3 = this.getIndex(int1, int2);
		if (int3 != -1) {
			this.value[int3] = object;
		}
	}

	public final int getWidth() {
		return this.width;
	}

	public final int getHeight() {
		return this.height;
	}
}
