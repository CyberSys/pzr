package zombie.util.list;

import zombie.util.IntCollection;


public interface IntList extends IntCollection {

	void add(int int1, int int2);

	boolean addAll(int int1, IntCollection intCollection);

	int get(int int1);

	int indexOf(int int1);

	int indexOf(int int1, int int2);

	int lastIndexOf(int int1);

	int lastIndexOf(int int1, int int2);

	IntListIterator listIterator();

	IntListIterator listIterator(int int1);

	int removeElementAt(int int1);

	int set(int int1, int int2);
}
