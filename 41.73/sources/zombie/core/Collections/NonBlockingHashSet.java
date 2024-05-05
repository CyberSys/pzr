package zombie.core.Collections;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;


public class NonBlockingHashSet extends AbstractSet implements Serializable {
	private static final Object V = "";
	private final NonBlockingHashMap _map = new NonBlockingHashMap();

	public boolean add(Object object) {
		return this._map.putIfAbsent(object, V) != V;
	}

	public boolean contains(Object object) {
		return this._map.containsKey(object);
	}

	public boolean remove(Object object) {
		return this._map.remove(object) == V;
	}

	public int size() {
		return this._map.size();
	}

	public void clear() {
		this._map.clear();
	}

	public Iterator iterator() {
		return this._map.keySet().iterator();
	}

	public void readOnly() {
		throw new RuntimeException("Unimplemented");
	}
}
