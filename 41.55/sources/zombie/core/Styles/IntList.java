package zombie.core.Styles;

import java.io.Serializable;


public class IntList implements Serializable {
	private static final long serialVersionUID = 1L;
	private int[] value;
	private int count;
	private final boolean fastExpand;

	public IntList() {
		this(0);
	}

	public IntList(int int1) {
		this(true, int1);
	}

	public IntList(boolean boolean1, int int1) {
		this.count = 0;
		this.fastExpand = boolean1;
		this.value = new int[int1];
	}

	public int add(short short1) {
		if (this.count == this.value.length) {
			int[] intArray = this.value;
			if (this.fastExpand) {
				this.value = new int[(intArray.length << 1) + 1];
			} else {
				this.value = new int[intArray.length + 1];
			}

			System.arraycopy(intArray, 0, this.value, 0, intArray.length);
		}

		this.value[this.count] = short1;
		return this.count++;
	}

	public int remove(int int1) {
		if (int1 < this.count && int1 >= 0) {
			int int2 = this.value[int1];
			if (int1 < this.count - 1) {
				System.arraycopy(this.value, int1 + 1, this.value, int1, this.count - int1 - 1);
			}

			--this.count;
			return int2;
		} else {
			throw new IndexOutOfBoundsException("Referenced " + int1 + ", size=" + this.count);
		}
	}

	public void addAll(short[] shortArray) {
		this.ensureCapacity(this.count + shortArray.length);
		System.arraycopy(shortArray, 0, this.value, this.count, shortArray.length);
		this.count += shortArray.length;
	}

	public void addAll(IntList intList) {
		this.ensureCapacity(this.count + intList.count);
		System.arraycopy(intList.value, 0, this.value, this.count, intList.count);
		this.count += intList.count;
	}

	public int[] array() {
		return this.value;
	}

	public int capacity() {
		return this.value.length;
	}

	public void clear() {
		this.count = 0;
	}

	public void ensureCapacity(int int1) {
		if (this.value.length < int1) {
			int[] intArray = this.value;
			this.value = new int[int1];
			System.arraycopy(intArray, 0, this.value, 0, intArray.length);
		}
	}

	public int get(int int1) {
		return this.value[int1];
	}

	public boolean isEmpty() {
		return this.count == 0;
	}

	public int size() {
		return this.count;
	}

	public short[] toArray(short[] shortArray) {
		if (shortArray == null) {
			shortArray = new short[this.count];
		}

		System.arraycopy(this.value, 0, shortArray, 0, this.count);
		return shortArray;
	}

	public void trimToSize() {
		if (this.count != this.value.length) {
			int[] intArray = this.value;
			this.value = new int[this.count];
			System.arraycopy(intArray, 0, this.value, 0, this.count);
		}
	}
}
