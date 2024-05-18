package zombie.core.physics;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.RandomAccess;


public final class ObjectArrayList extends AbstractList implements RandomAccess,Externalizable {
	private Object[] array;
	private int size;

	public ObjectArrayList() {
		this(16);
	}

	public ObjectArrayList(int int1) {
		this.array = (Object[])(new Object[int1]);
	}

	public boolean add(Object object) {
		if (this.size == this.array.length) {
			this.expand();
		}

		this.array[this.size++] = object;
		return true;
	}

	public void add(int int1, Object object) {
		if (this.size == this.array.length) {
			this.expand();
		}

		int int2 = this.size - int1;
		if (int2 > 0) {
			System.arraycopy(this.array, int1, this.array, int1 + 1, int2);
		}

		this.array[int1] = object;
		++this.size;
	}

	public Object remove(int int1) {
		if (int1 >= 0 && int1 < this.size) {
			Object object = this.array[int1];
			System.arraycopy(this.array, int1 + 1, this.array, int1, this.size - int1 - 1);
			this.array[this.size - 1] = null;
			--this.size;
			return object;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	private void expand() {
		Object[] objectArray = (Object[])(new Object[this.array.length << 1]);
		System.arraycopy(this.array, 0, objectArray, 0, this.array.length);
		this.array = objectArray;
	}

	public void removeQuick(int int1) {
		System.arraycopy(this.array, int1 + 1, this.array, int1, this.size - int1 - 1);
		this.array[this.size - 1] = null;
		--this.size;
	}

	public Object get(int int1) {
		if (int1 >= this.size) {
			throw new IndexOutOfBoundsException();
		} else {
			return this.array[int1];
		}
	}

	public Object getQuick(int int1) {
		return this.array[int1];
	}

	public Object set(int int1, Object object) {
		if (int1 >= this.size) {
			throw new IndexOutOfBoundsException();
		} else {
			Object object2 = this.array[int1];
			this.array[int1] = object;
			return object2;
		}
	}

	public void setQuick(int int1, Object object) {
		this.array[int1] = object;
	}

	public int size() {
		return this.size;
	}

	public int capacity() {
		return this.array.length;
	}

	public void clear() {
		this.size = 0;
	}

	public int indexOf(Object object) {
		int int1 = this.size;
		Object[] objectArray = this.array;
		int int2 = 0;
		while (true) {
			if (int2 >= int1) {
				return -1;
			}

			if (object == null) {
				if (objectArray[int2] == null) {
					break;
				}
			} else if (object.equals(objectArray[int2])) {
				break;
			}

			++int2;
		}

		return int2;
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeInt(this.size);
		for (int int1 = 0; int1 < this.size; ++int1) {
			objectOutput.writeObject(this.array[int1]);
		}
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.size = objectInput.readInt();
		int int1;
		for (int1 = 16; int1 < this.size; int1 <<= 1) {
		}

		this.array = (Object[])(new Object[int1]);
		for (int int2 = 0; int2 < this.size; ++int2) {
			this.array[int2] = objectInput.readObject();
		}
	}
}
