package zombie.util.list;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;


public final class PZArrayList extends AbstractList implements List,RandomAccess {
	private Object[] elements;
	private int numElements;

	public PZArrayList(Class javaClass, int int1) {
		this.elements = (Object[])((Object[])Array.newInstance(javaClass, int1));
	}

	public Object get(int int1) {
		if (int1 >= 0 && int1 < this.numElements) {
			return this.elements[int1];
		} else {
			throw new IndexOutOfBoundsException("Index: " + int1 + " Size: " + this.numElements);
		}
	}

	public int size() {
		return this.numElements;
	}

	public int indexOf(Object object) {
		for (int int1 = 0; int1 < this.numElements; ++int1) {
			if (object == null && this.elements[int1] == null || object != null && object.equals(this.elements[int1])) {
				return int1;
			}
		}

		return -1;
	}

	public boolean isEmpty() {
		return this.numElements == 0;
	}

	public boolean contains(Object object) {
		return this.indexOf(object) >= 0;
	}

	public Iterator iterator() {
		throw new UnsupportedOperationException();
	}

	public ListIterator listIterator() {
		throw new UnsupportedOperationException();
	}

	public ListIterator listIterator(int int1) {
		throw new UnsupportedOperationException();
	}

	public boolean add(Object object) {
		if (this.numElements == this.elements.length) {
			int int1 = this.elements.length + (this.elements.length >> 1);
			if (int1 < this.numElements + 1) {
				int1 = this.numElements + 1;
			}

			this.elements = Arrays.copyOf(this.elements, int1);
		}

		this.elements[this.numElements] = object;
		++this.numElements;
		return true;
	}

	public void add(int int1, Object object) {
		if (int1 >= 0 && int1 <= this.numElements) {
			if (this.numElements == this.elements.length) {
				int int2 = this.elements.length + this.elements.length >> 1;
				if (int2 < this.numElements + 1) {
					int2 = this.numElements + 1;
				}

				this.elements = Arrays.copyOf(this.elements, int2);
			}

			System.arraycopy(this.elements, int1, this.elements, int1 + 1, this.numElements - int1);
			this.elements[int1] = object;
			++this.numElements;
		} else {
			throw new IndexOutOfBoundsException("Index: " + int1 + " Size: " + this.numElements);
		}
	}

	public Object remove(int int1) {
		if (int1 >= 0 && int1 < this.numElements) {
			Object object = this.elements[int1];
			int int2 = this.numElements - int1 - 1;
			if (int2 > 0) {
				System.arraycopy(this.elements, int1 + 1, this.elements, int1, int2);
			}

			this.elements[this.numElements - 1] = null;
			--this.numElements;
			return object;
		} else {
			throw new IndexOutOfBoundsException("Index: " + int1 + " Size: " + this.numElements);
		}
	}

	public boolean remove(Object object) {
		for (int int1 = 0; int1 < this.numElements; ++int1) {
			if (object == null && this.elements[int1] == null || object != null && object.equals(this.elements[int1])) {
				int int2 = this.numElements - int1 - 1;
				if (int2 > 0) {
					System.arraycopy(this.elements, int1 + 1, this.elements, int1, int2);
				}

				this.elements[this.numElements - 1] = null;
				--this.numElements;
				return true;
			}
		}

		return false;
	}

	public Object set(int int1, Object object) {
		if (int1 >= 0 && int1 < this.numElements) {
			Object object2 = this.elements[int1];
			this.elements[int1] = object;
			return object2;
		} else {
			throw new IndexOutOfBoundsException("Index: " + int1 + " Size: " + this.numElements);
		}
	}

	public void clear() {
		for (int int1 = 0; int1 < this.numElements; ++int1) {
			this.elements[int1] = null;
		}

		this.numElements = 0;
	}

	public String toString() {
		if (this.isEmpty()) {
			return "[]";
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append('[');
			for (int int1 = 0; int1 < this.numElements; ++int1) {
				Object object = this.elements[int1];
				stringBuilder.append(object == this ? "(self)" : object.toString());
				if (int1 == this.numElements - 1) {
					break;
				}

				stringBuilder.append(',');
				stringBuilder.append(' ');
			}

			return stringBuilder.append(']').toString();
		}
	}

	public Object[] getElements() {
		return this.elements;
	}
}
