package zombie.core.utils;

import java.io.Serializable;
import java.util.Arrays;


public class ExpandableBooleanList implements Serializable,Cloneable {
	private static final long serialVersionUID = 1L;
	private int width;
	private int bitWidth;
	private int[] value;

	public ExpandableBooleanList(int int1) {
		this.bitWidth = int1;
		this.width = int1 / 32 + (int1 % 32 != 0 ? 1 : 0);
		this.value = new int[this.width];
	}

	public ExpandableBooleanList clone() throws CloneNotSupportedException {
		ExpandableBooleanList expandableBooleanList = new ExpandableBooleanList(this.bitWidth);
		System.arraycopy(this.value, 0, expandableBooleanList.value, 0, this.value.length);
		return expandableBooleanList;
	}

	public void clear() {
		Arrays.fill(this.value, 0);
	}

	public void fill() {
		Arrays.fill(this.value, -1);
	}

	public boolean getValue(int int1) {
		if (int1 >= 0 && int1 < this.bitWidth) {
			int int2 = int1 >> 5;
			int int3 = 1 << (int1 & 31);
			int int4 = this.value[int2];
			return (int4 & int3) != 0;
		} else {
			return false;
		}
	}

	public void setValue(int int1, boolean boolean1) {
		if (int1 >= 0) {
			if (int1 >= this.bitWidth) {
				int[] intArray = this.value;
				this.bitWidth = Math.max(this.bitWidth * 2, int1 + 1);
				this.width = this.bitWidth / 32 + (this.width % 32 != 0 ? 1 : 0);
				this.value = new int[this.width];
				System.arraycopy(intArray, 0, this.value, 0, intArray.length);
			}

			int int2 = int1 >> 5;
			int int3 = 1 << (int1 & 31);
			int[] intArray2;
			if (boolean1) {
				intArray2 = this.value;
				intArray2[int2] |= int3;
			} else {
				intArray2 = this.value;
				intArray2[int2] &= ~int3;
			}
		}
	}

	public final int getWidth() {
		return this.width;
	}
}
