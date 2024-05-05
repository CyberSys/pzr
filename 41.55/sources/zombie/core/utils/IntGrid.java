package zombie.core.utils;

import java.io.Serializable;
import java.util.Arrays;


public class IntGrid implements Serializable,Cloneable {
	private static final long serialVersionUID = 1L;
	private final int width;
	private final int height;
	private final int[] value;

	public IntGrid(int int1, int int2) {
		this.width = int1;
		this.height = int2;
		this.value = new int[int1 * int2];
	}

	public IntGrid clone() throws CloneNotSupportedException {
		IntGrid intGrid = new IntGrid(this.width, this.height);
		System.arraycopy(this.value, 0, intGrid.value, 0, this.value.length);
		return intGrid;
	}

	public void clear() {
		Arrays.fill(this.value, 0);
	}

	public void fill(int int1) {
		Arrays.fill(this.value, int1);
	}

	private int getIndex(int int1, int int2) {
		return int1 >= 0 && int2 >= 0 && int1 < this.width && int2 < this.height ? int1 + int2 * this.width : -1;
	}

	public int getValue(int int1, int int2) {
		int int3 = this.getIndex(int1, int2);
		return int3 == -1 ? 0 : this.value[int3];
	}

	public void setValue(int int1, int int2, int int3) {
		int int4 = this.getIndex(int1, int2);
		if (int4 != -1) {
			this.value[int4] = int3;
		}
	}

	public final int getWidth() {
		return this.width;
	}

	public final int getHeight() {
		return this.height;
	}
}
