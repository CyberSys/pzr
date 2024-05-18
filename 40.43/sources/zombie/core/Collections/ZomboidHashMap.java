package zombie.core.Collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;


public class ZomboidHashMap extends ZomboidAbstractMap implements Map,Cloneable,Serializable {
	static final int DEFAULT_INITIAL_CAPACITY = 16;
	static final int MAXIMUM_CAPACITY = 1073741824;
	static final float DEFAULT_LOAD_FACTOR = 0.75F;
	transient ZomboidHashMap.Entry[] table;
	transient int size;
	int threshold;
	final float loadFactor;
	transient volatile int modCount;
	Stack entryStore;
	private transient Set entrySet;
	private static final long serialVersionUID = 362498820763181265L;

	public ZomboidHashMap(int int1, float float1) {
		this.entryStore = new Stack();
		this.entrySet = null;
		if (int1 < 0) {
			throw new IllegalArgumentException("Illegal initial capacity: " + int1);
		} else {
			if (int1 > 1073741824) {
				int1 = 1073741824;
			}

			if (!(float1 <= 0.0F) && !Float.isNaN(float1)) {
				int int2;
				for (int2 = 1; int2 < int1; int2 <<= 1) {
				}

				for (int int3 = 0; int3 < 100; ++int3) {
					this.entryStore.add(new ZomboidHashMap.Entry(0, (Object)null, (Object)null, (ZomboidHashMap.Entry)null));
				}

				this.loadFactor = float1;
				this.threshold = (int)((float)int2 * float1);
				this.table = new ZomboidHashMap.Entry[int2];
				this.init();
			} else {
				throw new IllegalArgumentException("Illegal load factor: " + float1);
			}
		}
	}

	public ZomboidHashMap(int int1) {
		this(int1, 0.75F);
	}

	public ZomboidHashMap() {
		this.entryStore = new Stack();
		this.entrySet = null;
		this.loadFactor = 0.75F;
		this.threshold = 12;
		this.table = new ZomboidHashMap.Entry[16];
		this.init();
	}

	public ZomboidHashMap(Map map) {
		this(Math.max((int)((float)map.size() / 0.75F) + 1, 16), 0.75F);
		this.putAllForCreate(map);
	}

	void init() {
	}

	static int hash(int int1) {
		int1 ^= int1 >>> 20 ^ int1 >>> 12;
		return int1 ^ int1 >>> 7 ^ int1 >>> 4;
	}

	static int indexFor(int int1, int int2) {
		return int1 & int2 - 1;
	}

	public int size() {
		return this.size;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public Object get(Object object) {
		if (object == null) {
			return this.getForNullKey();
		} else {
			int int1 = hash(object.hashCode());
			for (ZomboidHashMap.Entry entry = this.table[indexFor(int1, this.table.length)]; entry != null; entry = entry.next) {
				Object object2;
				if (entry.hash == int1 && ((object2 = entry.key) == object || object.equals(object2))) {
					return entry.value;
				}
			}

			return null;
		}
	}

	private Object getForNullKey() {
		for (ZomboidHashMap.Entry entry = this.table[0]; entry != null; entry = entry.next) {
			if (entry.key == null) {
				return entry.value;
			}
		}

		return null;
	}

	public boolean containsKey(Object object) {
		return this.getEntry(object) != null;
	}

	final ZomboidHashMap.Entry getEntry(Object object) {
		int int1 = object == null ? 0 : hash(object.hashCode());
		for (ZomboidHashMap.Entry entry = this.table[indexFor(int1, this.table.length)]; entry != null; entry = entry.next) {
			Object object2;
			if (entry.hash == int1 && ((object2 = entry.key) == object || object != null && object.equals(object2))) {
				return entry;
			}
		}

		return null;
	}

	public Object put(Object object, Object object2) {
		if (object == null) {
			return this.putForNullKey(object2);
		} else {
			int int1 = hash(object.hashCode());
			int int2 = indexFor(int1, this.table.length);
			for (ZomboidHashMap.Entry entry = this.table[int2]; entry != null; entry = entry.next) {
				Object object3;
				if (entry.hash == int1 && ((object3 = entry.key) == object || object.equals(object3))) {
					Object object4 = entry.value;
					entry.value = object2;
					entry.recordAccess(this);
					return object4;
				}
			}

			++this.modCount;
			this.addEntry(int1, object, object2, int2);
			return null;
		}
	}

	private Object putForNullKey(Object object) {
		for (ZomboidHashMap.Entry entry = this.table[0]; entry != null; entry = entry.next) {
			if (entry.key == null) {
				Object object2 = entry.value;
				entry.value = object;
				entry.recordAccess(this);
				return object2;
			}
		}

		++this.modCount;
		this.addEntry(0, (Object)null, object, 0);
		return null;
	}

	private void putForCreate(Object object, Object object2) {
		int int1 = object == null ? 0 : hash(object.hashCode());
		int int2 = indexFor(int1, this.table.length);
		for (ZomboidHashMap.Entry entry = this.table[int2]; entry != null; entry = entry.next) {
			Object object3;
			if (entry.hash == int1 && ((object3 = entry.key) == object || object != null && object.equals(object3))) {
				entry.value = object2;
				return;
			}
		}

		this.createEntry(int1, object, object2, int2);
	}

	private void putAllForCreate(Map map) {
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
			this.putForCreate(entry.getKey(), entry.getValue());
		}
	}

	void resize(int int1) {
		ZomboidHashMap.Entry[] entryArray = this.table;
		int int2 = entryArray.length;
		if (int2 == 1073741824) {
			this.threshold = Integer.MAX_VALUE;
		} else {
			ZomboidHashMap.Entry[] entryArray2 = new ZomboidHashMap.Entry[int1];
			this.transfer(entryArray2);
			this.table = entryArray2;
			this.threshold = (int)((float)int1 * this.loadFactor);
		}
	}

	void transfer(ZomboidHashMap.Entry[] entryArray) {
		ZomboidHashMap.Entry[] entryArray2 = this.table;
		int int1 = entryArray.length;
		for (int int2 = 0; int2 < entryArray2.length; ++int2) {
			ZomboidHashMap.Entry entry = entryArray2[int2];
			if (entry != null) {
				entryArray2[int2] = null;
				ZomboidHashMap.Entry entry2;
				do {
					entry2 = entry.next;
					int int3 = indexFor(entry.hash, int1);
					entry.next = entryArray[int3];
					entryArray[int3] = entry;
					entry = entry2;
				}		 while (entry2 != null);
			}
		}
	}

	public void putAll(Map map) {
		int int1 = map.size();
		if (int1 != 0) {
			if (int1 > this.threshold) {
				int int2 = (int)((float)int1 / this.loadFactor + 1.0F);
				if (int2 > 1073741824) {
					int2 = 1073741824;
				}

				int int3;
				for (int3 = this.table.length; int3 < int2; int3 <<= 1) {
				}

				if (int3 > this.table.length) {
					this.resize(int3);
				}
			}

			Iterator iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
				this.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public Object remove(Object object) {
		ZomboidHashMap.Entry entry = this.removeEntryForKey(object);
		return entry == null ? null : entry.value;
	}

	final ZomboidHashMap.Entry removeEntryForKey(Object object) {
		int int1 = object == null ? 0 : hash(object.hashCode());
		int int2 = indexFor(int1, this.table.length);
		ZomboidHashMap.Entry entry = this.table[int2];
		ZomboidHashMap.Entry entry2;
		ZomboidHashMap.Entry entry3;
		for (entry2 = entry; entry2 != null; entry2 = entry3) {
			entry3 = entry2.next;
			Object object2;
			if (entry2.hash == int1 && ((object2 = entry2.key) == object || object != null && object.equals(object2))) {
				++this.modCount;
				--this.size;
				if (entry == entry2) {
					this.table[int2] = entry3;
				} else {
					entry.next = entry3;
				}

				entry2.recordRemoval(this);
				entry2.value = null;
				entry2.next = null;
				this.entryStore.push(entry2);
				return entry2;
			}

			entry = entry2;
		}

		return entry2;
	}

	final ZomboidHashMap.Entry removeMapping(Object object) {
		if (!(object instanceof java.util.Map.Entry)) {
			return null;
		} else {
			java.util.Map.Entry entry = (java.util.Map.Entry)object;
			Object object2 = entry.getKey();
			int int1 = object2 == null ? 0 : hash(object2.hashCode());
			int int2 = indexFor(int1, this.table.length);
			ZomboidHashMap.Entry entry2 = this.table[int2];
			ZomboidHashMap.Entry entry3;
			ZomboidHashMap.Entry entry4;
			for (entry3 = entry2; entry3 != null; entry3 = entry4) {
				entry4 = entry3.next;
				if (entry3.hash == int1 && entry3.equals(entry)) {
					++this.modCount;
					--this.size;
					if (entry2 == entry3) {
						this.table[int2] = entry4;
					} else {
						entry2.next = entry4;
					}

					entry3.recordRemoval(this);
					entry3.value = null;
					entry3.next = null;
					this.entryStore.push(entry3);
					return entry3;
				}

				entry2 = entry3;
			}

			return entry3;
		}
	}

	public void clear() {
		++this.modCount;
		ZomboidHashMap.Entry[] entryArray = this.table;
		for (int int1 = 0; int1 < entryArray.length; ++int1) {
			if (entryArray[int1] != null) {
				entryArray[int1].value = null;
				entryArray[int1].next = null;
				this.entryStore.push(entryArray[int1]);
			}

			entryArray[int1] = null;
		}

		this.size = 0;
	}

	public boolean containsValue(Object object) {
		if (object == null) {
			return this.containsNullValue();
		} else {
			ZomboidHashMap.Entry[] entryArray = this.table;
			for (int int1 = 0; int1 < entryArray.length; ++int1) {
				for (ZomboidHashMap.Entry entry = entryArray[int1]; entry != null; entry = entry.next) {
					if (object.equals(entry.value)) {
						return true;
					}
				}
			}

			return false;
		}
	}

	private boolean containsNullValue() {
		ZomboidHashMap.Entry[] entryArray = this.table;
		for (int int1 = 0; int1 < entryArray.length; ++int1) {
			for (ZomboidHashMap.Entry entry = entryArray[int1]; entry != null; entry = entry.next) {
				if (entry.value == null) {
					return true;
				}
			}
		}

		return false;
	}

	public Object clone() {
		ZomboidHashMap zomboidHashMap = null;
		try {
			zomboidHashMap = (ZomboidHashMap)super.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
		}

		zomboidHashMap.table = new ZomboidHashMap.Entry[this.table.length];
		zomboidHashMap.entrySet = null;
		zomboidHashMap.modCount = 0;
		zomboidHashMap.size = 0;
		zomboidHashMap.init();
		zomboidHashMap.putAllForCreate(this);
		return zomboidHashMap;
	}

	void addEntry(int int1, Object object, Object object2, int int2) {
		ZomboidHashMap.Entry entry = this.table[int2];
		if (this.entryStore.isEmpty()) {
			for (int int3 = 0; int3 < 100; ++int3) {
				this.entryStore.add(new ZomboidHashMap.Entry(0, (Object)null, (Object)null, (ZomboidHashMap.Entry)null));
			}
		}

		ZomboidHashMap.Entry entry2 = (ZomboidHashMap.Entry)this.entryStore.pop();
		entry2.hash = int1;
		entry2.key = object;
		entry2.value = object2;
		entry2.next = entry;
		this.table[int2] = entry2;
		if (this.size++ >= this.threshold) {
			this.resize(2 * this.table.length);
		}
	}

	void createEntry(int int1, Object object, Object object2, int int2) {
		ZomboidHashMap.Entry entry = this.table[int2];
		if (this.entryStore.isEmpty()) {
			for (int int3 = 0; int3 < 100; ++int3) {
				this.entryStore.add(new ZomboidHashMap.Entry(0, (Object)null, (Object)null, (ZomboidHashMap.Entry)null));
			}
		}

		ZomboidHashMap.Entry entry2 = (ZomboidHashMap.Entry)this.entryStore.pop();
		entry2.hash = int1;
		entry2.key = object;
		entry2.value = object2;
		entry2.next = entry;
		this.table[int2] = entry2;
		++this.size;
	}

	Iterator newKeyIterator() {
		return new ZomboidHashMap.KeyIterator();
	}

	Iterator newValueIterator() {
		return new ZomboidHashMap.ValueIterator();
	}

	Iterator newEntryIterator() {
		return new ZomboidHashMap.EntryIterator();
	}

	public Set keySet() {
		Set set = this.keySet;
		return set != null ? set : (this.keySet = new ZomboidHashMap.KeySet());
	}

	public Collection values() {
		Collection collection = this.values;
		return collection != null ? collection : (this.values = new ZomboidHashMap.Values());
	}

	public Set entrySet() {
		return this.entrySet0();
	}

	private Set entrySet0() {
		Set set = this.entrySet;
		return set != null ? set : (this.entrySet = new ZomboidHashMap.EntrySet());
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		Iterator iterator = this.size > 0 ? this.entrySet0().iterator() : null;
		objectOutputStream.defaultWriteObject();
		objectOutputStream.writeInt(this.table.length);
		objectOutputStream.writeInt(this.size);
		if (iterator != null) {
			while (iterator.hasNext()) {
				java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
				objectOutputStream.writeObject(entry.getKey());
				objectOutputStream.writeObject(entry.getValue());
			}
		}
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		int int1 = objectInputStream.readInt();
		this.table = new ZomboidHashMap.Entry[int1];
		this.init();
		int int2 = objectInputStream.readInt();
		for (int int3 = 0; int3 < int2; ++int3) {
			Object object = objectInputStream.readObject();
			Object object2 = objectInputStream.readObject();
			this.putForCreate(object, object2);
		}
	}

	int capacity() {
		return this.table.length;
	}

	float loadFactor() {
		return this.loadFactor;
	}

	private final class EntrySet extends AbstractSet {

		private EntrySet() {
		}

		public Iterator iterator() {
			return ZomboidHashMap.this.newEntryIterator();
		}

		public boolean contains(Object object) {
			if (!(object instanceof java.util.Map.Entry)) {
				return false;
			} else {
				java.util.Map.Entry entry = (java.util.Map.Entry)object;
				ZomboidHashMap.Entry entry2 = ZomboidHashMap.this.getEntry(entry.getKey());
				return entry2 != null && entry2.equals(entry);
			}
		}

		public boolean remove(Object object) {
			return ZomboidHashMap.this.removeMapping(object) != null;
		}

		public int size() {
			return ZomboidHashMap.this.size;
		}

		public void clear() {
			ZomboidHashMap.this.clear();
		}

		EntrySet(Object object) {
			this();
		}
	}

	private final class Values extends AbstractCollection {

		private Values() {
		}

		public Iterator iterator() {
			return ZomboidHashMap.this.newValueIterator();
		}

		public int size() {
			return ZomboidHashMap.this.size;
		}

		public boolean contains(Object object) {
			return ZomboidHashMap.this.containsValue(object);
		}

		public void clear() {
			ZomboidHashMap.this.clear();
		}

		Values(Object object) {
			this();
		}
	}

	private final class KeySet extends AbstractSet {

		private KeySet() {
		}

		public Iterator iterator() {
			return ZomboidHashMap.this.newKeyIterator();
		}

		public int size() {
			return ZomboidHashMap.this.size;
		}

		public boolean contains(Object object) {
			return ZomboidHashMap.this.containsKey(object);
		}

		public boolean remove(Object object) {
			return ZomboidHashMap.this.removeEntryForKey(object) != null;
		}

		public void clear() {
			ZomboidHashMap.this.clear();
		}

		KeySet(Object object) {
			this();
		}
	}

	private final class EntryIterator extends ZomboidHashMap.HashIterator {

		private EntryIterator() {
			super();
		}

		public java.util.Map.Entry next() {
			return this.nextEntry();
		}

		EntryIterator(Object object) {
			this();
		}
	}

	private final class KeyIterator extends ZomboidHashMap.HashIterator {

		private KeyIterator() {
			super();
		}

		public Object next() {
			return this.nextEntry().getKey();
		}

		KeyIterator(Object object) {
			this();
		}
	}

	private final class ValueIterator extends ZomboidHashMap.HashIterator {

		private ValueIterator() {
			super();
		}

		public Object next() {
			return this.nextEntry().value;
		}

		ValueIterator(Object object) {
			this();
		}
	}

	private abstract class HashIterator implements Iterator {
		ZomboidHashMap.Entry next;
		int expectedModCount;
		int index;
		ZomboidHashMap.Entry current;

		HashIterator() {
			this.expectedModCount = ZomboidHashMap.this.modCount;
			if (ZomboidHashMap.this.size > 0) {
				ZomboidHashMap.Entry[] entryArray = ZomboidHashMap.this.table;
				while (this.index < entryArray.length && (this.next = entryArray[this.index++]) == null) {
				}
			}
		}

		public final boolean hasNext() {
			return this.next != null;
		}

		final ZomboidHashMap.Entry nextEntry() {
			if (ZomboidHashMap.this.modCount != this.expectedModCount) {
				throw new ConcurrentModificationException();
			} else {
				ZomboidHashMap.Entry entry = this.next;
				if (entry == null) {
					throw new NoSuchElementException();
				} else {
					if ((this.next = entry.next) == null) {
						ZomboidHashMap.Entry[] entryArray = ZomboidHashMap.this.table;
						while (this.index < entryArray.length && (this.next = entryArray[this.index++]) == null) {
						}
					}

					this.current = entry;
					return entry;
				}
			}
		}

		public void remove() {
			if (this.current == null) {
				throw new IllegalStateException();
			} else if (ZomboidHashMap.this.modCount != this.expectedModCount) {
				throw new ConcurrentModificationException();
			} else {
				Object object = this.current.key;
				this.current = null;
				ZomboidHashMap.this.removeEntryForKey(object);
				this.expectedModCount = ZomboidHashMap.this.modCount;
			}
		}
	}

	static class Entry implements java.util.Map.Entry {
		Object key;
		Object value;
		ZomboidHashMap.Entry next;
		int hash;

		Entry(int int1, Object object, Object object2, ZomboidHashMap.Entry entry) {
			this.value = object2;
			this.next = entry;
			this.key = object;
			this.hash = int1;
		}

		public final Object getKey() {
			return this.key;
		}

		public final Object getValue() {
			return this.value;
		}

		public final Object setValue(Object object) {
			Object object2 = this.value;
			this.value = object;
			return object2;
		}

		public final boolean equals(Object object) {
			if (!(object instanceof java.util.Map.Entry)) {
				return false;
			} else {
				java.util.Map.Entry entry = (java.util.Map.Entry)object;
				Object object2 = this.getKey();
				Object object3 = entry.getKey();
				if (object2 == object3 || object2 != null && object2.equals(object3)) {
					Object object4 = this.getValue();
					Object object5 = entry.getValue();
					if (object4 == object5 || object4 != null && object4.equals(object5)) {
						return true;
					}
				}

				return false;
			}
		}

		public final int hashCode() {
			return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
		}

		public final String toString() {
			return this.getKey() + "=" + this.getValue();
		}

		void recordAccess(ZomboidHashMap zomboidHashMap) {
		}

		void recordRemoval(ZomboidHashMap zomboidHashMap) {
		}
	}
}
