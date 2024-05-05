package zombie.core.Collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import sun.misc.Unsafe;


public class NonBlockingHashtable extends Dictionary implements ConcurrentMap,Cloneable,Serializable {
	private static final long serialVersionUID = 1234123412341234123L;
	private static final int REPROBE_LIMIT = 10;
	private static final Unsafe _unsafe = UtilUnsafe.getUnsafe();
	private static final int _Obase;
	private static final int _Oscale;
	private static final long _kvs_offset;
	private transient Object[] _kvs;
	private transient long _last_resize_milli;
	private static final int MIN_SIZE_LOG = 3;
	private static final int MIN_SIZE = 8;
	private static final Object NO_MATCH_OLD;
	private static final Object MATCH_ANY;
	private static final Object TOMBSTONE;
	private static final NonBlockingHashtable.Prime TOMBPRIME;
	private transient Counter _reprobes;

	private static long rawIndex(Object[] objectArray, int int1) {
		assert int1 >= 0 && int1 < objectArray.length;
		return (long)(_Obase + int1 * _Oscale);
	}

	private final boolean CAS_kvs(Object[] objectArray, Object[] objectArray2) {
		return _unsafe.compareAndSwapObject(this, _kvs_offset, objectArray, objectArray2);
	}

	private static final int hash(Object object) {
		int int1 = object.hashCode();
		int1 += int1 << 15 ^ -12931;
		int1 ^= int1 >>> 10;
		int1 += int1 << 3;
		int1 ^= int1 >>> 6;
		int1 += (int1 << 2) + (int1 << 14);
		return int1 ^ int1 >>> 16;
	}

	private static final NonBlockingHashtable.CHM chm(Object[] objectArray) {
		return (NonBlockingHashtable.CHM)objectArray[0];
	}

	private static final int[] hashes(Object[] objectArray) {
		return (int[])objectArray[1];
	}

	private static final int len(Object[] objectArray) {
		return objectArray.length - 2 >> 1;
	}

	private static final Object key(Object[] objectArray, int int1) {
		return objectArray[(int1 << 1) + 2];
	}

	private static final Object val(Object[] objectArray, int int1) {
		return objectArray[(int1 << 1) + 3];
	}

	private static final boolean CAS_key(Object[] objectArray, int int1, Object object, Object object2) {
		return _unsafe.compareAndSwapObject(objectArray, rawIndex(objectArray, (int1 << 1) + 2), object, object2);
	}

	private static final boolean CAS_val(Object[] objectArray, int int1, Object object, Object object2) {
		return _unsafe.compareAndSwapObject(objectArray, rawIndex(objectArray, (int1 << 1) + 3), object, object2);
	}

	public final void print() {
		System.out.println("=========");
		this.print2(this._kvs);
		System.out.println("=========");
	}

	private final void print(Object[] objectArray) {
		for (int int1 = 0; int1 < len(objectArray); ++int1) {
			Object object = key(objectArray, int1);
			if (object != null) {
				String string = object == TOMBSTONE ? "XXX" : object.toString();
				Object object2 = val(objectArray, int1);
				Object object3 = NonBlockingHashtable.Prime.unbox(object2);
				String string2 = object2 == object3 ? "" : "prime_";
				String string3 = object3 == TOMBSTONE ? "tombstone" : object3.toString();
				System.out.println(int1 + " (" + string + "," + string2 + string3 + ")");
			}
		}

		Object[] objectArray2 = chm(objectArray)._newkvs;
		if (objectArray2 != null) {
			System.out.println("----");
			this.print(objectArray2);
		}
	}

	private final void print2(Object[] objectArray) {
		for (int int1 = 0; int1 < len(objectArray); ++int1) {
			Object object = key(objectArray, int1);
			Object object2 = val(objectArray, int1);
			Object object3 = NonBlockingHashtable.Prime.unbox(object2);
			if (object != null && object != TOMBSTONE && object2 != null && object3 != TOMBSTONE) {
				String string = object2 == object3 ? "" : "prime_";
				System.out.println(int1 + " (" + object + "," + string + object2 + ")");
			}
		}

		Object[] objectArray2 = chm(objectArray)._newkvs;
		if (objectArray2 != null) {
			System.out.println("----");
			this.print2(objectArray2);
		}
	}

	public long reprobes() {
		long long1 = this._reprobes.get();
		this._reprobes = new Counter();
		return long1;
	}

	private static final int reprobe_limit(int int1) {
		return 10 + (int1 >> 2);
	}

	public NonBlockingHashtable() {
		this(8);
	}

	public NonBlockingHashtable(int int1) {
		this._reprobes = new Counter();
		this.initialize(int1);
	}

	private final void initialize(int int1) {
		if (int1 < 0) {
			throw new IllegalArgumentException();
		} else {
			if (int1 > 1048576) {
				int1 = 1048576;
			}

			int int2;
			for (int2 = 3; 1 << int2 < int1 << 2; ++int2) {
			}

			this._kvs = new Object[(1 << int2 << 1) + 2];
			this._kvs[0] = new NonBlockingHashtable.CHM(new Counter());
			this._kvs[1] = new int[1 << int2];
			this._last_resize_milli = System.currentTimeMillis();
		}
	}

	protected final void initialize() {
		this.initialize(8);
	}

	public int size() {
		return chm(this._kvs).size();
	}

	public boolean isEmpty() {
		return this.size() == 0;
	}

	public boolean containsKey(Object object) {
		return this.get(object) != null;
	}

	public boolean contains(Object object) {
		return this.containsValue(object);
	}

	public Object put(Object object, Object object2) {
		return this.putIfMatch(object, object2, NO_MATCH_OLD);
	}

	public Object putIfAbsent(Object object, Object object2) {
		return this.putIfMatch(object, object2, TOMBSTONE);
	}

	public Object remove(Object object) {
		return this.putIfMatch(object, TOMBSTONE, NO_MATCH_OLD);
	}

	public boolean remove(Object object, Object object2) {
		return this.putIfMatch(object, TOMBSTONE, object2) == object2;
	}

	public Object replace(Object object, Object object2) {
		return this.putIfMatch(object, object2, MATCH_ANY);
	}

	public boolean replace(Object object, Object object2, Object object3) {
		return this.putIfMatch(object, object3, object2) == object2;
	}

	private final Object putIfMatch(Object object, Object object2, Object object3) {
		if (object3 != null && object2 != null) {
			Object object4 = putIfMatch(this, this._kvs, object, object2, object3);
			assert !(object4 instanceof NonBlockingHashtable.Prime);
			assert object4 != null;
			return object4 == TOMBSTONE ? null : object4;
		} else {
			throw new NullPointerException();
		}
	}

	public void putAll(Map map) {
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			this.put(entry.getKey(), entry.getValue());
		}
	}

	public void clear() {
		Object[] objectArray = (new NonBlockingHashtable(8))._kvs;
		while (!this.CAS_kvs(this._kvs, objectArray)) {
		}
	}

	public boolean containsValue(Object object) {
		if (object == null) {
			throw new NullPointerException();
		} else {
			Iterator iterator = this.values().iterator();
			Object object2;
			do {
				if (!iterator.hasNext()) {
					return false;
				}

				object2 = iterator.next();
			}	 while (object2 != object && !object2.equals(object));

			return true;
		}
	}

	protected void rehash() {
	}

	public Object clone() {
		try {
			NonBlockingHashtable nonBlockingHashtable = (NonBlockingHashtable)super.clone();
			nonBlockingHashtable.clear();
			Iterator iterator = this.keySet().iterator();
			while (iterator.hasNext()) {
				Object object = iterator.next();
				Object object2 = this.get(object);
				nonBlockingHashtable.put(object, object2);
			}

			return nonBlockingHashtable;
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}
	}

	public String toString() {
		Iterator iterator = this.entrySet().iterator();
		if (!iterator.hasNext()) {
			return "{}";
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append('{');
			while (true) {
				Entry entry = (Entry)iterator.next();
				Object object = entry.getKey();
				Object object2 = entry.getValue();
				stringBuilder.append(object == this ? "(this Map)" : object);
				stringBuilder.append('=');
				stringBuilder.append(object2 == this ? "(this Map)" : object2);
				if (!iterator.hasNext()) {
					return stringBuilder.append('}').toString();
				}

				stringBuilder.append(", ");
			}
		}
	}

	private static boolean keyeq(Object object, Object object2, int[] intArray, int int1, int int2) {
		return object == object2 || (intArray[int1] == 0 || intArray[int1] == int2) && object != TOMBSTONE && object2.equals(object);
	}

	public Object get(Object object) {
		int int1 = hash(object);
		Object object2 = get_impl(this, this._kvs, object, int1);
		assert !(object2 instanceof NonBlockingHashtable.Prime);
		return object2;
	}

	private static final Object get_impl(NonBlockingHashtable nonBlockingHashtable, Object[] objectArray, Object object, int int1) {
		int int2 = len(objectArray);
		NonBlockingHashtable.CHM cHM = chm(objectArray);
		int[] intArray = hashes(objectArray);
		int int3 = int1 & int2 - 1;
		int int4 = 0;
		while (true) {
			Object object2 = key(objectArray, int3);
			Object object3 = val(objectArray, int3);
			if (object2 == null) {
				return null;
			}

			Object[] objectArray2 = cHM._newkvs;
			if (keyeq(object2, object, intArray, int3, int1)) {
				if (!(object3 instanceof NonBlockingHashtable.Prime)) {
					return object3 == TOMBSTONE ? null : object3;
				}

				return get_impl(nonBlockingHashtable, cHM.copy_slot_and_check(nonBlockingHashtable, objectArray, int3, object), object, int1);
			}

			++int4;
			if (int4 >= reprobe_limit(int2) || object == TOMBSTONE) {
				return objectArray2 == null ? null : get_impl(nonBlockingHashtable, nonBlockingHashtable.help_copy(objectArray2), object, int1);
			}

			int3 = int3 + 1 & int2 - 1;
		}
	}

	private static final Object putIfMatch(NonBlockingHashtable nonBlockingHashtable, Object[] objectArray, Object object, Object object2, Object object3) {
		assert object2 != null;
		assert !(object2 instanceof NonBlockingHashtable.Prime);
		assert !(object3 instanceof NonBlockingHashtable.Prime);
		int int1 = hash(object);
		int int2 = len(objectArray);
		NonBlockingHashtable.CHM cHM = chm(objectArray);
		int[] intArray = hashes(objectArray);
		int int3 = int1 & int2 - 1;
		int int4 = 0;
		Object object4 = null;
		Object object5 = null;
		Object[] objectArray2 = null;
		while (true) {
			object5 = val(objectArray, int3);
			object4 = key(objectArray, int3);
			if (object4 == null) {
				if (object2 == TOMBSTONE) {
					return object2;
				}

				if (CAS_key(objectArray, int3, (Object)null, object)) {
					cHM._slots.add(1L);
					intArray[int3] = int1;
					break;
				}

				object4 = key(objectArray, int3);
				assert object4 != null;
			}

			objectArray2 = cHM._newkvs;
			if (!keyeq(object4, object, intArray, int3, int1)) {
				++int4;
				if (int4 < reprobe_limit(int2) && object != TOMBSTONE) {
					int3 = int3 + 1 & int2 - 1;
					continue;
				}

				objectArray2 = cHM.resize(nonBlockingHashtable, objectArray);
				if (object3 != null) {
					nonBlockingHashtable.help_copy(objectArray2);
				}

				return putIfMatch(nonBlockingHashtable, objectArray2, object, object2, object3);
			}

			break;
		}

		if (object2 == object5) {
			return object5;
		} else {
			if (objectArray2 == null && (object5 == null && cHM.tableFull(int4, int2) || object5 instanceof NonBlockingHashtable.Prime)) {
				objectArray2 = cHM.resize(nonBlockingHashtable, objectArray);
			}

			if (objectArray2 != null) {
				return putIfMatch(nonBlockingHashtable, cHM.copy_slot_and_check(nonBlockingHashtable, objectArray, int3, object3), object, object2, object3);
			} else {
				do {
					assert !(object5 instanceof NonBlockingHashtable.Prime);
					if (object3 != NO_MATCH_OLD && object5 != object3 && (object3 != MATCH_ANY || object5 == TOMBSTONE || object5 == null) && (object5 != null || object3 != TOMBSTONE) && (object3 == null || !object3.equals(object5))) {
						return object5;
					}

					if (CAS_val(objectArray, int3, object5, object2)) {
						if (object3 != null) {
							if ((object5 == null || object5 == TOMBSTONE) && object2 != TOMBSTONE) {
								cHM._size.add(1L);
							}

							if (object5 != null && object5 != TOMBSTONE && object2 == TOMBSTONE) {
								cHM._size.add(-1L);
							}
						}

						return object5 == null && object3 != null ? TOMBSTONE : object5;
					}

					object5 = val(objectArray, int3);
				}		 while (!(object5 instanceof NonBlockingHashtable.Prime));

				return putIfMatch(nonBlockingHashtable, cHM.copy_slot_and_check(nonBlockingHashtable, objectArray, int3, object3), object, object2, object3);
			}
		}
	}

	private final Object[] help_copy(Object[] objectArray) {
		Object[] objectArray2 = this._kvs;
		NonBlockingHashtable.CHM cHM = chm(objectArray2);
		if (cHM._newkvs == null) {
			return objectArray;
		} else {
			cHM.help_copy_impl(this, objectArray2, false);
			return objectArray;
		}
	}

	public Enumeration elements() {
		return new NonBlockingHashtable.SnapshotV();
	}

	public Collection values() {
		return new AbstractCollection(){
			
			public void clear() {
				NonBlockingHashtable.this.clear();
			}

			
			public int size() {
				return NonBlockingHashtable.this.size();
			}

			
			public boolean contains(Object var1) {
				return NonBlockingHashtable.this.containsValue(var1);
			}

			
			public Iterator iterator() {
				return NonBlockingHashtable.this.new SnapshotV();
			}
		};
	}

	public Enumeration keys() {
		return new NonBlockingHashtable.SnapshotK();
	}

	public Set keySet() {
		return new AbstractSet(){
			
			public void clear() {
				NonBlockingHashtable.this.clear();
			}

			
			public int size() {
				return NonBlockingHashtable.this.size();
			}

			
			public boolean contains(Object var1) {
				return NonBlockingHashtable.this.containsKey(var1);
			}

			
			public boolean remove(Object var1) {
				return NonBlockingHashtable.this.remove(var1) != null;
			}

			
			public Iterator iterator() {
				return NonBlockingHashtable.this.new SnapshotK();
			}
		};
	}

	public Set entrySet() {
		return new AbstractSet(){
			
			public void clear() {
				NonBlockingHashtable.this.clear();
			}

			
			public int size() {
				return NonBlockingHashtable.this.size();
			}

			
			public boolean remove(Object var1) {
				if (!(var1 instanceof Entry)) {
					return false;
				} else {
					Entry var2 = (Entry)var1;
					return NonBlockingHashtable.this.remove(var2.getKey(), var2.getValue());
				}
			}

			
			public boolean contains(Object var1) {
				if (!(var1 instanceof Entry)) {
					return false;
				} else {
					Entry var2 = (Entry)var1;
					Object var3 = NonBlockingHashtable.this.get(var2.getKey());
					return var3.equals(var2.getValue());
				}
			}

			
			public Iterator iterator() {
				return NonBlockingHashtable.this.new SnapshotE();
			}
		};
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		objectOutputStream.defaultWriteObject();
		Iterator iterator = this.keySet().iterator();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			Object object2 = this.get(object);
			objectOutputStream.writeObject(object);
			objectOutputStream.writeObject(object2);
		}

		objectOutputStream.writeObject((Object)null);
		objectOutputStream.writeObject((Object)null);
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		this.initialize(8);
		while (true) {
			Object object = objectInputStream.readObject();
			Object object2 = objectInputStream.readObject();
			if (object == null) {
				return;
			}

			this.put(object, object2);
		}
	}

	static  {
		_Obase = _unsafe.arrayBaseOffset(Object[].class);
		_Oscale = _unsafe.arrayIndexScale(Object[].class);
	Field var0 = null;
	try {
		var0 = NonBlockingHashtable.class.getDeclaredField("_kvs");
	} catch (NoSuchFieldException var2) {
		throw new RuntimeException(var2);
	}

		_kvs_offset = _unsafe.objectFieldOffset(var0);
		NO_MATCH_OLD = new Object();
		MATCH_ANY = new Object();
		TOMBSTONE = new Object();
		TOMBPRIME = new NonBlockingHashtable.Prime(TOMBSTONE);
	}

	private static final class CHM {
		private final Counter _size;
		private final Counter _slots;
		volatile Object[] _newkvs;
		private final AtomicReferenceFieldUpdater _newkvsUpdater = AtomicReferenceFieldUpdater.newUpdater(NonBlockingHashtable.CHM.class, Object[].class, "_newkvs");
		volatile long _resizers;
		private static final AtomicLongFieldUpdater _resizerUpdater = AtomicLongFieldUpdater.newUpdater(NonBlockingHashtable.CHM.class, "_resizers");
		volatile long _copyIdx = 0L;
		private static final AtomicLongFieldUpdater _copyIdxUpdater = AtomicLongFieldUpdater.newUpdater(NonBlockingHashtable.CHM.class, "_copyIdx");
		volatile long _copyDone = 0L;
		private static final AtomicLongFieldUpdater _copyDoneUpdater = AtomicLongFieldUpdater.newUpdater(NonBlockingHashtable.CHM.class, "_copyDone");

		public int size() {
			return (int)this._size.get();
		}

		public int slots() {
			return (int)this._slots.get();
		}

		boolean CAS_newkvs(Object[] objectArray) {
			while (true) {
				if (this._newkvs == null) {
					if (!this._newkvsUpdater.compareAndSet(this, (Object)null, objectArray)) {
						continue;
					}

					return true;
				}

				return false;
			}
		}

		CHM(Counter counter) {
			this._size = counter;
			this._slots = new Counter();
		}

		private final boolean tableFull(int int1, int int2) {
			return int1 >= 10 && this._slots.estimate_get() >= (long)NonBlockingHashtable.reprobe_limit(int2);
		}

		private final Object[] resize(NonBlockingHashtable nonBlockingHashtable, Object[] objectArray) {
			assert NonBlockingHashtable.chm(objectArray) == this;
			Object[] objectArray2 = this._newkvs;
			if (objectArray2 != null) {
				return objectArray2;
			} else {
				int int1 = NonBlockingHashtable.len(objectArray);
				int int2 = this.size();
				int int3 = int2;
				if (int2 >= int1 >> 2) {
					int3 = int1 << 1;
					if (int2 >= int1 >> 1) {
						int3 = int1 << 2;
					}
				}

				long long1 = System.currentTimeMillis();
				long long2 = 0L;
				if (int3 <= int1 && long1 <= nonBlockingHashtable._last_resize_milli + 10000L && this._slots.estimate_get() >= (long)(int2 << 1)) {
					int3 = int1 << 1;
				}

				if (int3 < int1) {
					int3 = int1;
				}

				int int4;
				for (int4 = 3; 1 << int4 < int3; ++int4) {
				}

				long long3;
				for (long3 = this._resizers; !_resizerUpdater.compareAndSet(this, long3, long3 + 1L); long3 = this._resizers) {
				}

				int int5 = (1 << int4 << 1) + 4 << 3 >> 20;
				if (long3 >= 2L && int5 > 0) {
					objectArray2 = this._newkvs;
					if (objectArray2 != null) {
						return objectArray2;
					}

					try {
						Thread.sleep((long)(8 * int5));
					} catch (Exception exception) {
					}
				}

				objectArray2 = this._newkvs;
				if (objectArray2 != null) {
					return objectArray2;
				} else {
					objectArray2 = new Object[(1 << int4 << 1) + 2];
					objectArray2[0] = new NonBlockingHashtable.CHM(this._size);
					objectArray2[1] = new int[1 << int4];
					if (this._newkvs != null) {
						return this._newkvs;
					} else {
						if (this.CAS_newkvs(objectArray2)) {
							nonBlockingHashtable.rehash();
						} else {
							objectArray2 = this._newkvs;
						}

						return objectArray2;
					}
				}
			}
		}

		private final void help_copy_impl(NonBlockingHashtable nonBlockingHashtable, Object[] objectArray, boolean boolean1) {
			assert NonBlockingHashtable.chm(objectArray) == this;
			Object[] objectArray2 = this._newkvs;
			assert objectArray2 != null;
			int int1 = NonBlockingHashtable.len(objectArray);
			int int2 = Math.min(int1, 1024);
			int int3 = -1;
			int int4 = -9999;
			do {
				if (this._copyDone >= (long)int1) {
					this.copy_check_and_promote(nonBlockingHashtable, objectArray, 0);
					return;
				}

				if (int3 == -1) {
					for (int4 = (int)this._copyIdx; int4 < int1 << 1 && !_copyIdxUpdater.compareAndSet(this, (long)int4, (long)(int4 + int2)); int4 = (int)this._copyIdx) {
					}

					if (int4 >= int1 << 1) {
						int3 = int4;
					}
				}

				int int5 = 0;
				for (int int6 = 0; int6 < int2; ++int6) {
					if (this.copy_slot(nonBlockingHashtable, int4 + int6 & int1 - 1, objectArray, objectArray2)) {
						++int5;
					}
				}

				if (int5 > 0) {
					this.copy_check_and_promote(nonBlockingHashtable, objectArray, int5);
				}

				int4 += int2;
			} while (boolean1 || int3 != -1);
		}

		private final Object[] copy_slot_and_check(NonBlockingHashtable nonBlockingHashtable, Object[] objectArray, int int1, Object object) {
			assert NonBlockingHashtable.chm(objectArray) == this;
			Object[] objectArray2 = this._newkvs;
			assert objectArray2 != null;
			if (this.copy_slot(nonBlockingHashtable, int1, objectArray, this._newkvs)) {
				this.copy_check_and_promote(nonBlockingHashtable, objectArray, 1);
			}

			return object == null ? objectArray2 : nonBlockingHashtable.help_copy(objectArray2);
		}

		private final void copy_check_and_promote(NonBlockingHashtable nonBlockingHashtable, Object[] objectArray, int int1) {
			assert NonBlockingHashtable.chm(objectArray) == this;
			int int2 = NonBlockingHashtable.len(objectArray);
			long long1 = this._copyDone;
			assert long1 + (long)int1 <= (long)int2;
			if (int1 > 0) {
				while (!_copyDoneUpdater.compareAndSet(this, long1, long1 + (long)int1)) {
					long1 = this._copyDone;
					assert long1 + (long)int1 <= (long)int2;
				}
			}

			if (long1 + (long)int1 == (long)int2 && nonBlockingHashtable._kvs == objectArray && nonBlockingHashtable.CAS_kvs(objectArray, this._newkvs)) {
				nonBlockingHashtable._last_resize_milli = System.currentTimeMillis();
			}
		}

		private boolean copy_slot(NonBlockingHashtable nonBlockingHashtable, int int1, Object[] objectArray, Object[] objectArray2) {
			Object object;
			while ((object = NonBlockingHashtable.key(objectArray, int1)) == null) {
				NonBlockingHashtable.CAS_key(objectArray, int1, (Object)null, NonBlockingHashtable.TOMBSTONE);
			}

			Object object2;
			for (object2 = NonBlockingHashtable.val(objectArray, int1); !(object2 instanceof NonBlockingHashtable.Prime); object2 = NonBlockingHashtable.val(objectArray, int1)) {
				NonBlockingHashtable.Prime prime = object2 != null && object2 != NonBlockingHashtable.TOMBSTONE ? new NonBlockingHashtable.Prime(object2) : NonBlockingHashtable.TOMBPRIME;
				if (NonBlockingHashtable.CAS_val(objectArray, int1, object2, prime)) {
					if (prime == NonBlockingHashtable.TOMBPRIME) {
						return true;
					}

					object2 = prime;
					break;
				}
			}

			if (object2 == NonBlockingHashtable.TOMBPRIME) {
				return false;
			} else {
				Object object3 = ((NonBlockingHashtable.Prime)object2)._V;
				assert object3 != NonBlockingHashtable.TOMBSTONE;
				boolean boolean1;
				for (boolean1 = NonBlockingHashtable.putIfMatch(nonBlockingHashtable, objectArray2, object, object3, (Object)null) == null; !NonBlockingHashtable.CAS_val(objectArray, int1, object2, NonBlockingHashtable.TOMBPRIME); object2 = NonBlockingHashtable.val(objectArray, int1)) {
				}

				return boolean1;
			}
		}
	}

	private static final class Prime {
		final Object _V;

		Prime(Object object) {
			this._V = object;
		}

		static Object unbox(Object object) {
			return object instanceof NonBlockingHashtable.Prime ? ((NonBlockingHashtable.Prime)object)._V : object;
		}
	}

	private class SnapshotV implements Iterator,Enumeration {
		final Object[] _sskvs;
		private int _idx;
		private Object _nextK;
		private Object _prevK;
		private Object _nextV;
		private Object _prevV;

		public SnapshotV() {
			while (true) {
				Object[] objectArray = NonBlockingHashtable.this._kvs;
				NonBlockingHashtable.CHM cHM = NonBlockingHashtable.chm(objectArray);
				if (cHM._newkvs == null) {
					this._sskvs = objectArray;
					this.next();
					return;
				}

				cHM.help_copy_impl(NonBlockingHashtable.this, objectArray, true);
			}
		}

		int length() {
			return NonBlockingHashtable.len(this._sskvs);
		}

		Object key(int int1) {
			return NonBlockingHashtable.key(this._sskvs, int1);
		}

		public boolean hasNext() {
			return this._nextV != null;
		}

		public Object next() {
			if (this._idx != 0 && this._nextV == null) {
				throw new NoSuchElementException();
			} else {
				this._prevK = this._nextK;
				this._prevV = this._nextV;
				this._nextV = null;
				while (this._idx < this.length()) {
					this._nextK = this.key(this._idx++);
					if (this._nextK != null && this._nextK != NonBlockingHashtable.TOMBSTONE && (this._nextV = NonBlockingHashtable.this.get(this._nextK)) != null) {
						break;
					}
				}

				return this._prevV;
			}
		}

		public void remove() {
			if (this._prevV == null) {
				throw new IllegalStateException();
			} else {
				NonBlockingHashtable.putIfMatch(NonBlockingHashtable.this, this._sskvs, this._prevK, NonBlockingHashtable.TOMBSTONE, this._prevV);
				this._prevV = null;
			}
		}

		public Object nextElement() {
			return this.next();
		}

		public boolean hasMoreElements() {
			return this.hasNext();
		}
	}

	private class SnapshotK implements Iterator,Enumeration {
		final NonBlockingHashtable.SnapshotV _ss = NonBlockingHashtable.this.new SnapshotV();

		public SnapshotK() {
		}

		public void remove() {
			this._ss.remove();
		}

		public Object next() {
			this._ss.next();
			return this._ss._prevK;
		}

		public boolean hasNext() {
			return this._ss.hasNext();
		}

		public Object nextElement() {
			return this.next();
		}

		public boolean hasMoreElements() {
			return this.hasNext();
		}
	}

	private class SnapshotE implements Iterator {
		final NonBlockingHashtable.SnapshotV _ss = NonBlockingHashtable.this.new SnapshotV();

		public SnapshotE() {
		}

		public void remove() {
			this._ss.remove();
		}

		public Entry next() {
			this._ss.next();
			return NonBlockingHashtable.this.new NBHMEntry(this._ss._prevK, this._ss._prevV);
		}

		public boolean hasNext() {
			return this._ss.hasNext();
		}
	}

	private class NBHMEntry extends AbstractEntry {

		NBHMEntry(Object object, Object object2) {
			super(object, object2);
		}

		public Object setValue(Object object) {
			if (object == null) {
				throw new NullPointerException();
			} else {
				this._val = object;
				return NonBlockingHashtable.this.put(this._key, object);
			}
		}
	}
}
