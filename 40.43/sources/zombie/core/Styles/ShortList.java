package zombie.core.Styles;

import java.io.Serializable;


public class ShortList implements Serializable {
	private static final long serialVersionUID = 1L;
	private short[] value;
	private short count;
	private final boolean fastExpand;

	public ShortList() {
		this(0);
	}

	public ShortList(int int1) {
		this(true, int1);
	}

	public ShortList(boolean boolean1, int int1) {
		this.count = 0;
		this.fastExpand = boolean1;
		this.value = new short[int1];
	}

	public short add(short short1) {
		if (this.count == this.value.length) {
			short[] shortArray = this.value;
			if (this.fastExpand) {
				this.value = new short[(shortArray.length << 1) + 1];
			} else {
				this.value = new short[shortArray.length + 1];
			}

			System.arraycopy(shortArray, 0, this.value, 0, shortArray.length);
		}

		this.value[this.count] = short1;
		short short2 = this.count;
		this.count = (short)(short2 + 1);
		return short2;
	}

	public short remove(int int1) {
		if (int1 < this.count && int1 >= 0) {
			short short1 = this.value[int1];
			if (int1 < this.count - 1) {
				System.arraycopy(this.value, int1 + 1, this.value, int1, this.count - int1 - 1);
			}

			--this.count;
			return short1;
		} else {
			throw new IndexOutOfBoundsException("Referenced " + int1 + ", size=" + this.count);
		}
	}

	public void addAll(short[] shortArray) {
		this.ensureCapacity(this.count + shortArray.length);
		System.arraycopy(shortArray, 0, this.value, this.count, shortArray.length);
		this.count = (short)(this.count + shortArray.length);
	}

	public void addAll(ShortList shortList) {
		this.ensureCapacity(this.count + shortList.count);
		System.arraycopy(shortList.value, 0, this.value, this.count, shortList.count);
		this.count += shortList.count;
	}

	public short[] array() {
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
			short[] shortArray = this.value;
			this.value = new short[int1];
			System.arraycopy(shortArray, 0, this.value, 0, shortArray.length);
		}
	}

	public short get(int int1) {
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
			short[] shortArray = this.value;
			this.value = new short[this.count];
			System.arraycopy(shortArray, 0, this.value, 0, this.count);
		}
	}
}
