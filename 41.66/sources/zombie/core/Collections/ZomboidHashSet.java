package zombie.core.Collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


public class ZomboidHashSet extends AbstractSet implements Set,Cloneable,Serializable {
	static final long serialVersionUID = -5024744406713321676L;
	private transient ZomboidHashMap map;
	private static final Object PRESENT = new Object();

	public ZomboidHashSet() {
		this.map = new ZomboidHashMap();
	}

	public ZomboidHashSet(Collection collection) {
		this.map = new ZomboidHashMap(Math.max((int)((float)collection.size() / 0.75F) + 1, 16));
		this.addAll(collection);
	}

	public ZomboidHashSet(int int1, float float1) {
		this.map = new ZomboidHashMap(int1);
	}

	public ZomboidHashSet(int int1) {
		this.map = new ZomboidHashMap(int1);
	}

	public Iterator iterator() {
		return this.map.keySet().iterator();
	}

	public int size() {
		return this.map.size();
	}

	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	public boolean contains(Object object) {
		return this.map.containsKey(object);
	}

	public boolean add(Object object) {
		return this.map.put(object, PRESENT) == null;
	}

	public boolean remove(Object object) {
		return this.map.remove(object) == PRESENT;
	}

	public void clear() {
		this.map.clear();
	}

	public Object clone() {
		try {
			ZomboidHashSet zomboidHashSet = (ZomboidHashSet)super.clone();
			zomboidHashSet.map = (ZomboidHashMap)this.map.clone();
			return zomboidHashSet;
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		objectOutputStream.defaultWriteObject();
		objectOutputStream.writeInt(this.map.size());
		Iterator iterator = this.map.keySet().iterator();
		while (iterator.hasNext()) {
			objectOutputStream.writeObject(iterator.next());
		}
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
	}
}
