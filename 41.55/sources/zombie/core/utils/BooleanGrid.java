package zombie.core.utils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class BooleanGrid implements Serializable,Cloneable {
	private static final long serialVersionUID = 1L;
	private final int width;
	private final int height;
	private final int bitWidth;
	private final int[] value;

	public BooleanGrid(int int1, int int2) {
		this.bitWidth = int1;
		this.width = int1 / 32 + (int1 % 32 != 0 ? 1 : 0);
		this.height = int2;
		this.value = new int[this.width * this.height];
	}

	public BooleanGrid clone() throws CloneNotSupportedException {
		BooleanGrid booleanGrid = new BooleanGrid(this.bitWidth, this.height);
		System.arraycopy(this.value, 0, booleanGrid.value, 0, this.value.length);
		return booleanGrid;
	}

	public void copy(BooleanGrid booleanGrid) {
		if (booleanGrid.bitWidth == this.bitWidth && booleanGrid.height == this.height) {
			System.arraycopy(booleanGrid.value, 0, this.value, 0, booleanGrid.value.length);
		} else {
			throw new IllegalArgumentException("src must be same size as this: " + booleanGrid + " cannot be copied into " + this);
		}
	}

	public void clear() {
		Arrays.fill(this.value, 0);
	}

	public void fill() {
		Arrays.fill(this.value, -1);
	}

	private int getIndex(int int1, int int2) {
		return int1 >= 0 && int2 >= 0 && int1 < this.width && int2 < this.height ? int1 + int2 * this.width : -1;
	}

	public boolean getValue(int int1, int int2) {
		if (int1 < this.bitWidth && int1 >= 0 && int2 < this.height && int2 >= 0) {
			int int3 = int1 / 32;
			int int4 = 1 << (int1 & 31);
			int int5 = this.getIndex(int3, int2);
			if (int5 == -1) {
				return false;
			} else {
				int int6 = this.value[int5];
				return (int6 & int4) != 0;
			}
		} else {
			return false;
		}
	}

	public void setValue(int int1, int int2, boolean boolean1) {
		if (int1 < this.bitWidth && int1 >= 0 && int2 < this.height && int2 >= 0) {
			int int3 = int1 / 32;
			int int4 = 1 << (int1 & 31);
			int int5 = this.getIndex(int3, int2);
			if (int5 != -1) {
				int[] intArray;
				if (boolean1) {
					intArray = this.value;
					intArray[int5] |= int4;
				} else {
					intArray = this.value;
					intArray[int5] &= ~int4;
				}
			}
		}
	}

	public final int getWidth() {
		return this.width;
	}

	public final int getHeight() {
		return this.height;
	}

	public String toString() {
		return "BooleanGrid [width=" + this.width + ", height=" + this.height + ", bitWidth=" + this.bitWidth + "]";
	}

	public void LoadFromByteBuffer(ByteBuffer byteBuffer) {
		int int1 = this.width * this.height;
		for (int int2 = 0; int2 < int1; ++int2) {
			this.value[int2] = byteBuffer.getInt();
		}
	}

	public void PutToByteBuffer(ByteBuffer byteBuffer) {
		int int1 = this.width * this.height;
		for (int int2 = 0; int2 < int1; ++int2) {
			byteBuffer.putInt(this.value[int2]);
		}
	}
}
