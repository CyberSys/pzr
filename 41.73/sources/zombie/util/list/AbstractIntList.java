package zombie.util.list;

import zombie.util.AbstractIntCollection;
import zombie.util.IntCollection;
import zombie.util.IntIterator;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.util.Exceptions;


public abstract class AbstractIntList extends AbstractIntCollection implements IntList {

	protected AbstractIntList() {
	}

	public boolean add(int int1) {
		this.add(this.size(), int1);
		return true;
	}

	public void add(int int1, int int2) {
		Exceptions.unsupported("add");
	}

	public boolean addAll(int int1, IntCollection intCollection) {
		if (int1 < 0 || int1 > this.size()) {
			Exceptions.indexOutOfBounds(int1, 0, this.size());
		}

		IntIterator intIterator = intCollection.iterator();
		boolean boolean1;
		for (boolean1 = intIterator.hasNext(); intIterator.hasNext(); ++int1) {
			this.add(int1, intIterator.next());
		}

		return boolean1;
	}

	public int indexOf(int int1) {
		return this.indexOf(0, int1);
	}

	public int indexOf(int int1, int int2) {
		IntListIterator intListIterator = this.listIterator(int1);
		do {
			if (!intListIterator.hasNext()) {
				return -1;
			}
		} while (intListIterator.next() != int2);

		return intListIterator.previousIndex();
	}

	public IntIterator iterator() {
		return this.listIterator();
	}

	public int lastIndexOf(int int1) {
		IntListIterator intListIterator = this.listIterator(this.size());
		do {
			if (!intListIterator.hasPrevious()) {
				return -1;
			}
		} while (intListIterator.previous() != int1);

		return intListIterator.nextIndex();
	}

	public int lastIndexOf(int int1, int int2) {
		IntListIterator intListIterator = this.listIterator(int1);
		do {
			if (!intListIterator.hasPrevious()) {
				return -1;
			}
		} while (intListIterator.previous() != int2);

		return intListIterator.nextIndex();
	}

	public IntListIterator listIterator() {
		return this.listIterator(0);
	}

	public IntListIterator listIterator(int int1) {
		if (int1 < 0 || int1 > this.size()) {
			Exceptions.indexOutOfBounds(int1, 0, this.size());
		}

		return new IntListIterator(){
			private int ptr = int1;
			private int lptr = -1;
			
			public boolean hasNext() {
				return this.ptr < AbstractIntList.this.size();
			}

			
			public int next() {
				if (this.ptr == AbstractIntList.this.size()) {
					Exceptions.endOfIterator();
				}

				this.lptr = this.ptr++;
				return AbstractIntList.this.get(this.lptr);
			}

			
			public void remove() {
				if (this.lptr == -1) {
					Exceptions.noElementToRemove();
				}

				AbstractIntList.this.removeElementAt(this.lptr);
				if (this.lptr < this.ptr) {
					--this.ptr;
				}

				this.lptr = -1;
			}

			
			public void add(int int1x) {
				AbstractIntList.this.add(this.ptr++, int1x);
				this.lptr = -1;
			}

			
			public boolean hasPrevious() {
				return this.ptr > 0;
			}

			
			public int nextIndex() {
				return this.ptr;
			}

			
			public int previous() {
				if (this.ptr == 0) {
					Exceptions.startOfIterator();
				}

				--this.ptr;
				this.lptr = this.ptr;
				return AbstractIntList.this.get(this.ptr);
			}

			
			public int previousIndex() {
				return this.ptr - 1;
			}

			
			public void set(int int1x) {
				if (this.lptr == -1) {
					Exceptions.noElementToSet();
				}

				AbstractIntList.this.set(this.lptr, int1x);
			}
		};
	}

	public int removeElementAt(int int1) {
		Exceptions.unsupported("removeElementAt");
		throw new RuntimeException();
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof IntList)) {
			return false;
		} else {
			IntListIterator intListIterator = this.listIterator();
			IntListIterator intListIterator2 = ((IntList)object).listIterator();
			while (intListIterator.hasNext() && intListIterator2.hasNext()) {
				if (intListIterator.next() != intListIterator2.next()) {
					return false;
				}
			}

			return !intListIterator.hasNext() && !intListIterator2.hasNext();
		}
	}

	public int hashCode() {
		int int1 = 1;
		for (IntIterator intIterator = this.iterator(); intIterator.hasNext(); int1 = 31 * int1 + DefaultIntHashFunction.INSTANCE.hash(intIterator.next())) {
		}

		return int1;
	}
}
