package zombie.network;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import zombie.core.Rand;


public class IsoObjectID implements Iterable {
	public static final short incorrect = -1;
	private final ConcurrentHashMap IDToObjectMap = new ConcurrentHashMap();
	private final String objectType;
	private short nextID = (short)Rand.Next(32766);

	public IsoObjectID(Class javaClass) {
		this.objectType = javaClass.getSimpleName();
	}

	public void put(short short1, Object object) {
		if (short1 != -1) {
			this.IDToObjectMap.put(short1, object);
		}
	}

	public void remove(short short1) {
		this.IDToObjectMap.remove(short1);
	}

	public void remove(Object object) {
		this.IDToObjectMap.values().remove(object);
	}

	public Object get(short short1) {
		return this.IDToObjectMap.get(short1);
	}

	public int size() {
		return this.IDToObjectMap.size();
	}

	public void clear() {
		this.IDToObjectMap.clear();
	}

	public short allocateID() {
		++this.nextID;
		if (this.nextID == -1) {
			++this.nextID;
		}

		return this.nextID;
	}

	public Iterator iterator() {
		return this.IDToObjectMap.values().iterator();
	}

	public void getObjects(Collection collection) {
		collection.addAll(this.IDToObjectMap.values());
	}
}
