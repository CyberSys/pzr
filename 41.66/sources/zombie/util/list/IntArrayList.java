package zombie.util.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import zombie.util.IntCollection;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.util.Exceptions;


public class IntArrayList extends AbstractIntList implements Cloneable,Serializable {
	private static final long serialVersionUID = 1L;
	private static final int GROWTH_POLICY_RELATIVE = 0;
	private static final int GROWTH_POLICY_ABSOLUTE = 1;
	private static final int DEFAULT_GROWTH_POLICY = 0;
	public static final double DEFAULT_GROWTH_FACTOR = 1.0;
	public static final int DEFAULT_GROWTH_CHUNK = 10;
	public static final int DEFAULT_CAPACITY = 10;
	private transient int[] data;
	private int size;
	private int growthPolicy;
	private double growthFactor;
	private int growthChunk;

	private IntArrayList(int int1, int int2, double double1, int int3) {
		if (int1 < 0) {
			Exceptions.negativeArgument("capacity", String.valueOf(int1));
		}

		if (double1 < 0.0) {
			Exceptions.negativeArgument("growthFactor", String.valueOf(double1));
		}

		if (int3 < 0) {
			Exceptions.negativeArgument("growthChunk", String.valueOf(int3));
		}

		this.data = new int[int1];
		this.size = 0;
		this.growthPolicy = int2;
		this.growthFactor = double1;
		this.growthChunk = int3;
	}

	public IntArrayList() {
		this(10);
	}

	public IntArrayList(IntCollection intCollection) {
		this(intCollection.size());
		this.addAll(intCollection);
	}

	public IntArrayList(int[] intArray) {
		this(intArray.length);
		System.arraycopy(intArray, 0, this.data, 0, intArray.length);
		this.size = intArray.length;
	}

	public IntArrayList(int int1) {
		this(int1, 1.0);
	}

	public IntArrayList(int int1, double double1) {
		this(int1, 0, double1, 10);
	}

	public IntArrayList(int int1, int int2) {
		this(int1, 1, 1.0, int2);
	}

	private int computeCapacity(int int1) {
		int int2;
		if (this.growthPolicy == 0) {
			int2 = (int)((double)this.data.length * (1.0 + this.growthFactor));
		} else {
			int2 = this.data.length + this.growthChunk;
		}

		if (int2 < int1) {
			int2 = int1;
		}

		return int2;
	}

	public int ensureCapacity(int int1) {
		if (int1 > this.data.length) {
			int[] intArray = new int[int1 = this.computeCapacity(int1)];
			System.arraycopy(this.data, 0, intArray, 0, this.size);
			this.data = intArray;
		}

		return int1;
	}

	public int capacity() {
		return this.data.length;
	}

	public void add(int int1, int int2) {
		if (int1 < 0 || int1 > this.size) {
			Exceptions.indexOutOfBounds(int1, 0, this.size);
		}

		this.ensureCapacity(this.size + 1);
		int int3 = this.size - int1;
		if (int3 > 0) {
			System.arraycopy(this.data, int1, this.data, int1 + 1, int3);
		}

		this.data[int1] = int2;
		++this.size;
	}

	public int get(int int1) {
		if (int1 < 0 || int1 >= this.size) {
			Exceptions.indexOutOfBounds(int1, 0, this.size - 1);
		}

		return this.data[int1];
	}

	public int set(int int1, int int2) {
		if (int1 < 0 || int1 >= this.size) {
			Exceptions.indexOutOfBounds(int1, 0, this.size - 1);
		}

		int int3 = this.data[int1];
		this.data[int1] = int2;
		return int3;
	}

	public int removeElementAt(int int1) {
		if (int1 < 0 || int1 >= this.size) {
			Exceptions.indexOutOfBounds(int1, 0, this.size - 1);
		}

		int int2 = this.data[int1];
		int int3 = this.size - (int1 + 1);
		if (int3 > 0) {
			System.arraycopy(this.data, int1 + 1, this.data, int1, int3);
		}

		--this.size;
		return int2;
	}

	public void trimToSize() {
		if (this.data.length > this.size) {
			int[] intArray = new int[this.size];
			System.arraycopy(this.data, 0, intArray, 0, this.size);
			this.data = intArray;
		}
	}

	public Object clone() {
		try {
			IntArrayList intArrayList = (IntArrayList)super.clone();
			intArrayList.data = new int[this.data.length];
			System.arraycopy(this.data, 0, intArrayList.data, 0, this.size);
			return intArrayList;
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			Exceptions.cloning();
			return null;
		}
	}

	public int size() {
		return this.size;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public void clear() {
		this.size = 0;
	}

	public boolean contains(int int1) {
		for (int int2 = 0; int2 < this.size; ++int2) {
			if (this.data[int2] == int1) {
				return true;
			}
		}

		return false;
	}

	public int indexOf(int int1) {
		for (int int2 = 0; int2 < this.size; ++int2) {
			if (this.data[int2] == int1) {
				return int2;
			}
		}

		return -1;
	}

	public int indexOf(int int1, int int2) {
		if (int1 < 0 || int1 > this.size) {
			Exceptions.indexOutOfBounds(int1, 0, this.size);
		}

		for (int int3 = int1; int3 < this.size; ++int3) {
			if (this.data[int3] == int2) {
				return int3;
			}
		}

		return -1;
	}

	public int lastIndexOf(int int1) {
		for (int int2 = this.size - 1; int2 >= 0; --int2) {
			if (this.data[int2] == int1) {
				return int2;
			}
		}

		return -1;
	}

	public boolean remove(int int1) {
		int int2 = this.indexOf(int1);
		if (int2 != -1) {
			this.removeElementAt(int2);
			return true;
		} else {
			return false;
		}
	}

	public int[] toArray() {
		int[] intArray = new int[this.size];
		System.arraycopy(this.data, 0, intArray, 0, this.size);
		return intArray;
	}

	public int[] toArray(int[] intArray) {
		if (intArray == null || intArray.length < this.size) {
			intArray = new int[this.size];
		}

		System.arraycopy(this.data, 0, intArray, 0, this.size);
		return intArray;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof IntList)) {
			return false;
		} else {
			int int1 = 0;
			IntListIterator intListIterator = ((IntList)object).listIterator();
			while (int1 < this.size && intListIterator.hasNext()) {
				if (this.data[int1++] != intListIterator.next()) {
					return false;
				}
			}

			return int1 >= this.size && !intListIterator.hasNext();
		}
	}

	public int hashCode() {
		int int1 = 1;
		for (int int2 = 0; int2 < this.size; ++int2) {
			int1 = 31 * int1 + DefaultIntHashFunction.INSTANCE.hash(this.data[int2]);
		}

		return int1;
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		objectOutputStream.defaultWriteObject();
		objectOutputStream.writeInt(this.data.length);
		for (int int1 = 0; int1 < this.size; ++int1) {
			objectOutputStream.writeInt(this.data[int1]);
		}
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		this.data = new int[objectInputStream.readInt()];
		for (int int1 = 0; int1 < this.size; ++int1) {
			this.data[int1] = objectInputStream.readInt();
		}
	}
}
