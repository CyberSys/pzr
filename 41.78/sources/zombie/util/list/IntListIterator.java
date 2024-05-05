package zombie.util.list;

import zombie.util.IntIterator;


public interface IntListIterator extends IntIterator {

	void add(int int1);

	boolean hasPrevious();

	int nextIndex();

	int previous();

	int previousIndex();

	void set(int int1);
}
