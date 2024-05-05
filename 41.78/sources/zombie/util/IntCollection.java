package zombie.util;


public interface IntCollection {

	boolean add(int int1);

	boolean addAll(IntCollection intCollection);

	void clear();

	boolean contains(int int1);

	boolean containsAll(IntCollection intCollection);

	boolean equals(Object object);

	int hashCode();

	boolean isEmpty();

	IntIterator iterator();

	boolean remove(int int1);

	boolean removeAll(IntCollection intCollection);

	boolean retainAll(IntCollection intCollection);

	int size();

	int[] toArray();

	int[] toArray(int[] intArray);

	void trimToSize();
}
