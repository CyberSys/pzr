package zombie.util;

import zombie.util.util.Display;
import zombie.util.util.Exceptions;


public abstract class AbstractIntCollection implements IntCollection {

	protected AbstractIntCollection() {
	}

	public boolean add(int int1) {
		Exceptions.unsupported("add");
		return false;
	}

	public boolean addAll(IntCollection intCollection) {
		IntIterator intIterator = intCollection.iterator();
		boolean boolean1;
		for (boolean1 = false; intIterator.hasNext(); boolean1 |= this.add(intIterator.next())) {
		}

		return boolean1;
	}

	public void clear() {
		IntIterator intIterator = this.iterator();
		while (intIterator.hasNext()) {
			intIterator.next();
			intIterator.remove();
		}
	}

	public boolean contains(int int1) {
		IntIterator intIterator = this.iterator();
		do {
			if (!intIterator.hasNext()) {
				return false;
			}
		} while (intIterator.next() != int1);

		return true;
	}

	public boolean containsAll(IntCollection intCollection) {
		IntIterator intIterator = intCollection.iterator();
		do {
			if (!intIterator.hasNext()) {
				return true;
			}
		} while (this.contains(intIterator.next()));

		return false;
	}

	public boolean isEmpty() {
		return this.size() == 0;
	}

	public boolean remove(int int1) {
		IntIterator intIterator = this.iterator();
		boolean boolean1 = false;
		while (intIterator.hasNext()) {
			if (intIterator.next() == int1) {
				intIterator.remove();
				boolean1 = true;
				break;
			}
		}

		return boolean1;
	}

	public boolean removeAll(IntCollection intCollection) {
		if (intCollection == null) {
			Exceptions.nullArgument("collection");
		}

		IntIterator intIterator = this.iterator();
		boolean boolean1 = false;
		while (intIterator.hasNext()) {
			if (intCollection.contains(intIterator.next())) {
				intIterator.remove();
				boolean1 = true;
			}
		}

		return boolean1;
	}

	public boolean retainAll(IntCollection intCollection) {
		if (intCollection == null) {
			Exceptions.nullArgument("collection");
		}

		IntIterator intIterator = this.iterator();
		boolean boolean1 = false;
		while (intIterator.hasNext()) {
			if (!intCollection.contains(intIterator.next())) {
				intIterator.remove();
				boolean1 = true;
			}
		}

		return boolean1;
	}

	public int size() {
		IntIterator intIterator = this.iterator();
		int int1;
		for (int1 = 0; intIterator.hasNext(); ++int1) {
			intIterator.next();
		}

		return int1;
	}

	public int[] toArray() {
		return this.toArray((int[])null);
	}

	public int[] toArray(int[] intArray) {
		int int1 = this.size();
		if (intArray == null || intArray.length < int1) {
			intArray = new int[int1];
		}

		IntIterator intIterator = this.iterator();
		for (int int2 = 0; intIterator.hasNext(); ++int2) {
			intArray[int2] = intIterator.next();
		}

		return intArray;
	}

	public void trimToSize() {
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append('[');
		for (IntIterator intIterator = this.iterator(); intIterator.hasNext(); stringBuilder.append(Display.display(intIterator.next()))) {
			if (stringBuilder.length() > 1) {
				stringBuilder.append(',');
			}
		}

		stringBuilder.append(']');
		return stringBuilder.toString();
	}
}
