package zombie.util.set;

import zombie.util.AbstractIntCollection;
import zombie.util.IntIterator;
import zombie.util.hash.DefaultIntHashFunction;


public abstract class AbstractIntSet extends AbstractIntCollection implements IntSet {

	protected AbstractIntSet() {
	}

	public boolean equals(Object object) {
		if (!(object instanceof IntSet)) {
			return false;
		} else {
			IntSet intSet = (IntSet)object;
			return intSet.size() != this.size() ? false : this.containsAll(intSet);
		}
	}

	public int hashCode() {
		int int1 = 0;
		for (IntIterator intIterator = this.iterator(); intIterator.hasNext(); int1 += DefaultIntHashFunction.INSTANCE.hash(intIterator.next())) {
		}

		return int1;
	}
}
