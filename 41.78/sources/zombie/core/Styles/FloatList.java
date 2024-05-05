package zombie.core.Styles;

import java.io.Serializable;


public class FloatList implements Serializable {
	private static final long serialVersionUID = 1L;
	private float[] value;
	private int count;
	private final boolean fastExpand;

	public FloatList() {
		this(0);
	}

	public FloatList(int int1) {
		this(true, int1);
	}

	public FloatList(boolean boolean1, int int1) {
		this.count = 0;
		this.fastExpand = boolean1;
		this.value = new float[int1];
	}

	public float add(float float1) {
		if (this.count == this.value.length) {
			float[] floatArray = this.value;
			if (this.fastExpand) {
				this.value = new float[(floatArray.length << 1) + 1];
			} else {
				this.value = new float[floatArray.length + 1];
			}

			System.arraycopy(floatArray, 0, this.value, 0, floatArray.length);
		}

		this.value[this.count] = float1;
		return (float)(this.count++);
	}

	public float remove(int int1) {
		if (int1 < this.count && int1 >= 0) {
			float float1 = this.value[int1];
			if (int1 < this.count - 1) {
				System.arraycopy(this.value, int1 + 1, this.value, int1, this.count - int1 - 1);
			}

			--this.count;
			return float1;
		} else {
			throw new IndexOutOfBoundsException("Referenced " + int1 + ", size=" + this.count);
		}
	}

	public void addAll(float[] floatArray) {
		this.ensureCapacity(this.count + floatArray.length);
		System.arraycopy(floatArray, 0, this.value, this.count, floatArray.length);
		this.count += floatArray.length;
	}

	public void addAll(FloatList floatList) {
		this.ensureCapacity(this.count + floatList.count);
		System.arraycopy(floatList.value, 0, this.value, this.count, floatList.count);
		this.count += floatList.count;
	}

	public float[] array() {
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
			float[] floatArray = this.value;
			this.value = new float[int1];
			System.arraycopy(floatArray, 0, this.value, 0, floatArray.length);
		}
	}

	public float get(int int1) {
		return this.value[int1];
	}

	public boolean isEmpty() {
		return this.count == 0;
	}

	public int size() {
		return this.count;
	}

	public void toArray(Object[] objectArray) {
		System.arraycopy(this.value, 0, objectArray, 0, this.count);
	}

	public void trimToSize() {
		if (this.count != this.value.length) {
			float[] floatArray = this.value;
			this.value = new float[this.count];
			System.arraycopy(floatArray, 0, this.value, 0, this.count);
		}
	}
}
