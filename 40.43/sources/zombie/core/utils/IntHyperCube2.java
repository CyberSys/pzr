package zombie.core.utils;

import java.util.Arrays;


public class IntHyperCube2 {
	private static final long serialVersionUID = 1L;
	private final int width;
	private final int height;
	private final int depth;
	private final int quanta;
	private final int wxh;
	private final int wxhxd;
	private final int[][][][] value;

	public IntHyperCube2(int int1, int int2, int int3, int int4) {
		this.width = int1;
		this.height = int2;
		this.depth = int3;
		this.quanta = int4;
		this.wxh = int1 * int2;
		this.wxhxd = this.wxh * int3;
		this.value = new int[int1][int2][int3][int4];
	}

	public void clear() {
		Arrays.fill(this.value, 0);
	}

	public void fill(int int1) {
		Arrays.fill(this.value, int1);
	}

	private int getIndex(int int1, int int2, int int3, int int4) {
		return int1 >= 0 && int2 >= 0 && int3 >= 0 && int4 >= 0 && int1 < this.width && int2 < this.height && int3 < this.depth && int4 < this.quanta ? int1 + int2 * this.width + int3 * this.wxh + int4 * this.wxhxd : -1;
	}

	public int getValue(int int1, int int2, int int3, int int4) {
		return int1 >= 0 && int2 >= 0 && int3 >= 0 && int4 >= 0 && int1 < this.width && int2 < this.height && int3 < this.depth && int4 < this.quanta ? this.value[int1][int2][int3][int4] : 0;
	}

	public void setValue(int int1, int int2, int int3, int int4, int int5) {
		this.value[int1][int2][int3][int4] = int5;
	}

	public final int getWidth() {
		return this.width;
	}

	public final int getHeight() {
		return this.height;
	}

	public final int getDepth() {
		return this.depth;
	}

	public final int getQuanta() {
		return this.quanta;
	}
}
