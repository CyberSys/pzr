package zombie.util.map;

import java.util.Collection;
import zombie.util.set.IntSet;


public interface IntKeyMap {

	void clear();

	boolean containsKey(int int1);

	boolean containsValue(Object object);

	IntKeyMapIterator entries();

	boolean equals(Object object);

	Object get(int int1);

	int hashCode();

	boolean isEmpty();

	IntSet keySet();

	Object put(int int1, Object object);

	void putAll(IntKeyMap intKeyMap);

	Object remove(int int1);

	int size();

	Collection values();
}
