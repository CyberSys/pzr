package zombie.core.Collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import sun.misc.Unsafe;


public class NonBlockingHashMapLong extends AbstractMap implements ConcurrentMap,Serializable {
	private static final long serialVersionUID = 1234123412341234124L;
	private static final int REPROBE_LIMIT = 10;
	private static final Unsafe _unsafe = UtilUnsafe.getUnsafe();
	private static final int _Obase;
	private static final int _Oscale;
	private static final int _Lbase;
	private static final int _Lscale;
	private static final long _chm_offset;
	private static final long _val_1_offset;
	private transient NonBlockingHashMapLong.CHM _chm;
	private transient Object _val_1;
	private transient long _last_resize_milli;
	private final boolean _opt_for_space;
	private static final int MIN_SIZE_LOG = 4;
	private static final int MIN_SIZE = 16;
	private static final Object NO_MATCH_OLD;
	private static final Object MATCH_ANY;
	private static final Object TOMBSTONE;
	private static final NonBlockingHashMapLong.Prime TOMBPRIME;
	private static final long NO_KEY = 0L;
	private transient Counter _reprobes;

	private static long rawIndex(Object[] objectArray, int int1) {
		assert int1 >= 0 && int1 < objectArray.length;
		return (long)(_Obase + int1 * _Oscale);
	}

	private static long rawIndex(long[] longArray, int int1) {
		assert int1 >= 0 && int1 < longArray.length;
		return (long)(_Lbase + int1 * _Lscale);
	}

	private final boolean CAS(long long1, Object object, Object object2) {
		return _unsafe.compareAndSwapObject(this, long1, object, object2);
	}

	public final void print() {
		System.out.println("=========");
		print_impl(-99, 0L, this._val_1);
		this._chm.print();
		System.out.println("=========");
	}

	private static final void print_impl(int int1, long long1, Object object) {
		String string = object instanceof NonBlockingHashMapLong.Prime ? "prime_" : "";
		Object object2 = NonBlockingHashMapLong.Prime.unbox(object);
		String string2 = object2 == TOMBSTONE ? "tombstone" : object2.toString();
		System.out.println("[" + int1 + "]=(" + long1 + "," + string + string2 + ")");
	}

	private final void print2() {
		System.out.println("=========");
		print2_impl(-99, 0L, this._val_1);
		this._chm.print();
		System.out.println("=========");
	}

	private static final void print2_impl(int int1, long long1, Object object) {
		if (object != null && NonBlockingHashMapLong.Prime.unbox(object) != TOMBSTONE) {
			print_impl(int1, long1, object);
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

	public NonBlockingHashMapLong() {
		this(16, true);
	}

	public NonBlockingHashMapLong(int int1) {
		this(int1, true);
	}

	public NonBlockingHashMapLong(boolean boolean1) {
		this(1, boolean1);
	}

	public NonBlockingHashMapLong(int int1, boolean boolean1) {
		this._reprobes = new Counter();
		this._opt_for_space = boolean1;
		this.initialize(int1);
	}

	private final void initialize(int int1) {
		if (int1 < 0) {
			throw new IllegalArgumentException();
		} else {
			int int2;
			for (int2 = 4; 1 << int2 < int1; ++int2) {
			}

			this._chm = new NonBlockingHashMapLong.CHM(this, new Counter(), int2);
			this._val_1 = TOMBSTONE;
			this._last_resize_milli = System.currentTimeMillis();
		}
	}

	public int size() {
		return (this._val_1 == TOMBSTONE ? 0 : 1) + this._chm.size();
	}

	public boolean containsKey(long long1) {
		return this.get(long1) != null;
	}

	public boolean contains(Object object) {
		return this.containsValue(object);
	}

	public Object put(long long1, Object object) {
		return this.putIfMatch(long1, object, NO_MATCH_OLD);
	}

	public Object putIfAbsent(long long1, Object object) {
		return this.putIfMatch(long1, object, TOMBSTONE);
	}

	public Object remove(long long1) {
		return this.putIfMatch(long1, TOMBSTONE, NO_MATCH_OLD);
	}

	public boolean remove(long long1, Object object) {
		return this.putIfMatch(long1, TOMBSTONE, object) == object;
	}

	public Object replace(long long1, Object object) {
		return this.putIfMatch(long1, object, MATCH_ANY);
	}

	public boolean replace(long long1, Object object, Object object2) {
		return this.putIfMatch(long1, object2, object) == object;
	}

	private final Object putIfMatch(long long1, Object object, Object object2) {
		if (object2 != null && object != null) {
			Object object3;
			if (long1 != 0L) {
				object3 = this._chm.putIfMatch(long1, object, object2);
				assert !(object3 instanceof NonBlockingHashMapLong.Prime);
				assert object3 != null;
				return object3 == TOMBSTONE ? null : object3;
			} else {
				object3 = this._val_1;
				if (object2 == NO_MATCH_OLD || object3 == object2 || object2 == MATCH_ANY && object3 != TOMBSTONE || object2.equals(object3)) {
					this.CAS(_val_1_offset, object3, object);
				}

				return object3 == TOMBSTONE ? null : object3;
			}
		} else {
			throw new NullPointerException();
		}
	}

	public void clear() {
		NonBlockingHashMapLong.CHM cHM = new NonBlockingHashMapLong.CHM(this, new Counter(), 4);
		while (!this.CAS(_chm_offset, this._chm, cHM)) {
		}

		this.CAS(_val_1_offset, this._val_1, TOMBSTONE);
	}

	public boolean containsValue(Object object) {
		if (object == null) {
			return false;
		} else if (object == this._val_1) {
			return true;
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

	public final Object get(long long1) {
		Object object;
		if (long1 == 0L) {
			object = this._val_1;
			return object == TOMBSTONE ? null : object;
		} else {
			object = this._chm.get_impl(long1);
			assert !(object instanceof NonBlockingHashMapLong.Prime);
			assert object != TOMBSTONE;
			return object;
		}
	}

	public Object get(Object object) {
		return object instanceof Long ? this.get((Long)object) : null;
	}

	public Object remove(Object object) {
		return object instanceof Long ? this.remove((Long)object) : null;
	}

	public boolean remove(Object object, Object object2) {
		return object instanceof Long ? this.remove((Long)object, object2) : false;
	}

	public boolean containsKey(Object object) {
		return object instanceof Long ? this.containsKey((Long)object) : false;
	}

	public Object putIfAbsent(Long Long1, Object object) {
		return this.putIfAbsent(Long1, object);
	}

	public Object replace(Long Long1, Object object) {
		return this.replace(Long1, object);
	}

	public Object put(Long Long1, Object object) {
		return this.put(Long1, object);
	}

	public boolean replace(Long Long1, Object object, Object object2) {
		return this.replace(Long1, object, object2);
	}

	private final void help_copy() {
		NonBlockingHashMapLong.CHM cHM = this._chm;
		if (cHM._newchm != null) {
			cHM.help_copy_impl(false);
		}
	}

	public Enumeration elements() {
		return new NonBlockingHashMapLong.SnapshotV();
	}

	public Collection values() {
		return new AbstractCollection(){
			
			public void clear() {
				NonBlockingHashMapLong.this.clear();
			}

			
			public int size() {
				return NonBlockingHashMapLong.this.size();
			}

			
			public boolean contains(Object var1) {
				return NonBlockingHashMapLong.this.containsValue(var1);
			}

			
			public Iterator iterator() {
				return NonBlockingHashMapLong.this.new SnapshotV();
			}
		};
	}

	public Enumeration keys() {
		return new NonBlockingHashMapLong.IteratorLong();
	}

	public Set keySet() {
		return new AbstractSet(){
			
			public void clear() {
				NonBlockingHashMapLong.this.clear();
			}

			
			public int size() {
				return NonBlockingHashMapLong.this.size();
			}

			
			public boolean contains(Object var1) {
				return NonBlockingHashMapLong.this.containsKey(var1);
			}

			
			public boolean remove(Object var1) {
				return NonBlockingHashMapLong.this.remove(var1) != null;
			}

			
			public NonBlockingHashMapLong.IteratorLong iterator() {
				return NonBlockingHashMapLong.this.new IteratorLong();
			}
		};
	}

	public Set entrySet() {
		return new AbstractSet(){
			
			public void clear() {
				NonBlockingHashMapLong.this.clear();
			}

			
			public int size() {
				return NonBlockingHashMapLong.this.size();
			}

			
			public boolean remove(Object var1) {
				if (!(var1 instanceof Entry)) {
					return false;
				} else {
					Entry var2 = (Entry)var1;
					return NonBlockingHashMapLong.this.remove(var2.getKey(), var2.getValue());
				}
			}

			
			public boolean contains(Object var1) {
				if (!(var1 instanceof Entry)) {
					return false;
				} else {
					Entry var2 = (Entry)var1;
					Object var3 = NonBlockingHashMapLong.this.get(var2.getKey());
					return var3.equals(var2.getValue());
				}
			}

			
			public Iterator iterator() {
				return NonBlockingHashMapLong.this.new SnapshotE();
			}
		};
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		objectOutputStream.defaultWriteObject();
		Iterator iterator = this.keySet().iterator();
		while (iterator.hasNext()) {
			long long1 = (Long)iterator.next();
			Object object = this.get(long1);
			objectOutputStream.writeLong(long1);
			objectOutputStream.writeObject(object);
		}

		objectOutputStream.writeLong(0L);
		objectOutputStream.writeObject((Object)null);
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		this.initialize(16);
		while (true) {
			long long1 = objectInputStream.readLong();
			Object object = objectInputStream.readObject();
			if (long1 == 0L && object == null) {
				return;
			}

			this.put(long1, object);
		}
	}

	static  {
		_Obase = _unsafe.arrayBaseOffset(Object[].class);
		_Oscale = _unsafe.arrayIndexScale(Object[].class);
		_Lbase = _unsafe.arrayBaseOffset(long[].class);
		_Lscale = _unsafe.arrayIndexScale(long[].class);
	Field var0 = null;
	try {
		var0 = NonBlockingHashMapLong.class.getDeclaredField("_chm");
	} catch (NoSuchFieldException var3) {
		throw new RuntimeException(var3);
	}

		_chm_offset = _unsafe.objectFieldOffset(var0);
	try {
		var0 = NonBlockingHashMapLong.class.getDeclaredField("_val_1");
	} catch (NoSuchFieldException var2) {
		throw new RuntimeException(var2);
	}

		_val_1_offset = _unsafe.objectFieldOffset(var0);
		NO_MATCH_OLD = new Object();
		MATCH_ANY = new Object();
		TOMBSTONE = new Object();
		TOMBPRIME = new NonBlockingHashMapLong.Prime(TOMBSTONE);
	}

	private class SnapshotE implements Iterator {
		final NonBlockingHashMapLong.SnapshotV _ss = NonBlockingHashMapLong.this.new SnapshotV();

		public SnapshotE() {
		}

		public void remove() {
			this._ss.remove();
		}

		public Entry next() {
			this._ss.next();
			return NonBlockingHashMapLong.this.new NBHMLEntry(this._ss._prevK, this._ss._prevV);
		}

		public boolean hasNext() {
			return this._ss.hasNext();
		}
	}

	private class NBHMLEntry extends AbstractEntry {

		NBHMLEntry(Long Long1, Object object) {
			super(Long1, object);
		}

		public Object setValue(Object object) {
			if (object == null) {
				throw new NullPointerException();
			} else {
				this._val = object;
				return NonBlockingHashMapLong.this.put((Long)this._key, object);
			}
		}
	}

	public class IteratorLong implements Iterator,Enumeration {
		private final NonBlockingHashMapLong.SnapshotV _ss = NonBlockingHashMapLong.this.new SnapshotV();

		public void remove() {
			this._ss.remove();
		}

		public Long next() {
			this._ss.next();
			return this._ss._prevK;
		}

		public long nextLong() {
			this._ss.next();
			return this._ss._prevK;
		}

		public boolean hasNext() {
			return this._ss.hasNext();
		}

		public Long nextElement() {
			return this.next();
		}

		public boolean hasMoreElements() {
			return this.hasNext();
		}
	}

	private class SnapshotV implements Iterator,Enumeration {
		final NonBlockingHashMapLong.CHM _sschm;
		private int _idx;
		private long _nextK;
		private long _prevK;
		private Object _nextV;
		private Object _prevV;

		public SnapshotV() {
			while (true) {
				NonBlockingHashMapLong.CHM cHM = NonBlockingHashMapLong.this._chm;
				if (cHM._newchm == null) {
					this._sschm = cHM;
					this._idx = -1;
					this.next();
					return;
				}

				cHM.help_copy_impl(true);
			}
		}

		int length() {
			return this._sschm._keys.length;
		}

		long key(int int1) {
			return this._sschm._keys[int1];
		}

		public boolean hasNext() {
			return this._nextV != null;
		}

		public Object next() {
			if (this._idx != -1 && this._nextV == null) {
				throw new NoSuchElementException();
			} else {
				this._prevK = this._nextK;
				this._prevV = this._nextV;
				this._nextV = null;
				if (this._idx == -1) {
					this._idx = 0;
					this._nextK = 0L;
					if ((this._nextV = NonBlockingHashMapLong.this.get(this._nextK)) != null) {
						return this._prevV;
					}
				}

				while (this._idx < this.length()) {
					this._nextK = this.key(this._idx++);
					if (this._nextK != 0L && (this._nextV = NonBlockingHashMapLong.this.get(this._nextK)) != null) {
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
				this._sschm.putIfMatch(this._prevK, NonBlockingHashMapLong.TOMBSTONE, this._prevV);
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

	private static final class CHM implements Serializable {
		final NonBlockingHashMapLong _nbhml;
		private final Counter _size;
		private final Counter _slots;
		volatile NonBlockingHashMapLong.CHM _newchm;
		private static final AtomicReferenceFieldUpdater _newchmUpdater = AtomicReferenceFieldUpdater.newUpdater(NonBlockingHashMapLong.CHM.class, NonBlockingHashMapLong.CHM.class, "_newchm");
		volatile long _resizers;
		private static final AtomicLongFieldUpdater _resizerUpdater = AtomicLongFieldUpdater.newUpdater(NonBlockingHashMapLong.CHM.class, "_resizers");
		final long[] _keys;
		final Object[] _vals;
		volatile long _copyIdx = 0L;
		private static final AtomicLongFieldUpdater _copyIdxUpdater = AtomicLongFieldUpdater.newUpdater(NonBlockingHashMapLong.CHM.class, "_copyIdx");
		volatile long _copyDone = 0L;
		private static final AtomicLongFieldUpdater _copyDoneUpdater = AtomicLongFieldUpdater.newUpdater(NonBlockingHashMapLong.CHM.class, "_copyDone");

		public int size() {
			return (int)this._size.get();
		}

		public int slots() {
			return (int)this._slots.get();
		}

		boolean CAS_newchm(NonBlockingHashMapLong.CHM cHM) {
			return _newchmUpdater.compareAndSet(this, (Object)null, cHM);
		}

		private final boolean CAS_key(int int1, long long1, long long2) {
			return NonBlockingHashMapLong._unsafe.compareAndSwapLong(this._keys, NonBlockingHashMapLong.rawIndex(this._keys, int1), long1, long2);
		}

		private final boolean CAS_val(int int1, Object object, Object object2) {
			return NonBlockingHashMapLong._unsafe.compareAndSwapObject(this._vals, NonBlockingHashMapLong.rawIndex(this._vals, int1), object, object2);
		}

		CHM(NonBlockingHashMapLong nonBlockingHashMapLong, Counter counter, int int1) {
			this._nbhml = nonBlockingHashMapLong;
			this._size = counter;
			this._slots = new Counter();
			this._keys = new long[1 << int1];
			this._vals = new Object[1 << int1];
		}

		private final void print() {
			for (int int1 = 0; int1 < this._keys.length; ++int1) {
				long long1 = this._keys[int1];
				if (long1 != 0L) {
					NonBlockingHashMapLong.print_impl(int1, long1, this._vals[int1]);
				}
			}

			NonBlockingHashMapLong.CHM cHM = this._newchm;
			if (cHM != null) {
				System.out.println("----");
				cHM.print();
			}
		}

		private final void print2() {
			for (int int1 = 0; int1 < this._keys.length; ++int1) {
				long long1 = this._keys[int1];
				if (long1 != 0L) {
					NonBlockingHashMapLong.print2_impl(int1, long1, this._vals[int1]);
				}
			}

			NonBlockingHashMapLong.CHM cHM = this._newchm;
			if (cHM != null) {
				System.out.println("----");
				cHM.print2();
			}
		}

		private final Object get_impl(long long1) {
			int int1 = this._keys.length;
			int int2 = (int)(long1 & (long)(int1 - 1));
			int int3 = 0;
			while (true) {
				long long2 = this._keys[int2];
				Object object = this._vals[int2];
				if (long2 == 0L) {
					return null;
				}

				if (long1 == long2) {
					if (!(object instanceof NonBlockingHashMapLong.Prime)) {
						if (object == NonBlockingHashMapLong.TOMBSTONE) {
							return null;
						}

						NonBlockingHashMapLong.CHM cHM = this._newchm;
						return object;
					}

					return this.copy_slot_and_check(int2, long1).get_impl(long1);
				}

				++int3;
				if (int3 >= NonBlockingHashMapLong.reprobe_limit(int1)) {
					return this._newchm == null ? null : this.copy_slot_and_check(int2, long1).get_impl(long1);
				}

				int2 = int2 + 1 & int1 - 1;
			}
		}

		private final Object putIfMatch(long long1, Object object, Object object2) {
			assert object != null;
			assert !(object instanceof NonBlockingHashMapLong.Prime);
			assert !(object2 instanceof NonBlockingHashMapLong.Prime);
			int int1 = this._keys.length;
			int int2 = (int)(long1 & (long)(int1 - 1));
			int int3 = 0;
			long long2 = 0L;
			Object object3 = null;
			while (true) {
				object3 = this._vals[int2];
				long2 = this._keys[int2];
				if (long2 == 0L) {
					if (object == NonBlockingHashMapLong.TOMBSTONE) {
						return object;
					}

					if (this.CAS_key(int2, 0L, long1)) {
						this._slots.add(1L);
						break;
					}

					long2 = this._keys[int2];
					assert long2 != 0L;
				}

				if (long2 == long1) {
					break;
				}

				++int3;
				if (int3 >= NonBlockingHashMapLong.reprobe_limit(int1)) {
					NonBlockingHashMapLong.CHM cHM = this.resize();
					if (object2 != null) {
						this._nbhml.help_copy();
					}

					return cHM.putIfMatch(long1, object, object2);
				}

				int2 = int2 + 1 & int1 - 1;
			}

			if (object == object3) {
				return object3;
			} else if ((object3 != null || !this.tableFull(int3, int1)) && !(object3 instanceof NonBlockingHashMapLong.Prime)) {
				do {
					assert !(object3 instanceof NonBlockingHashMapLong.Prime);
					if (object2 != NonBlockingHashMapLong.NO_MATCH_OLD && object3 != object2 && (object2 != NonBlockingHashMapLong.MATCH_ANY || object3 == NonBlockingHashMapLong.TOMBSTONE || object3 == null) && (object3 != null || object2 != NonBlockingHashMapLong.TOMBSTONE) && (object2 == null || !object2.equals(object3))) {
						return object3;
					}

					if (this.CAS_val(int2, object3, object)) {
						if (object2 != null) {
							if ((object3 == null || object3 == NonBlockingHashMapLong.TOMBSTONE) && object != NonBlockingHashMapLong.TOMBSTONE) {
								this._size.add(1L);
							}

							if (object3 != null && object3 != NonBlockingHashMapLong.TOMBSTONE && object == NonBlockingHashMapLong.TOMBSTONE) {
								this._size.add(-1L);
							}
						}

						return object3 == null && object2 != null ? NonBlockingHashMapLong.TOMBSTONE : object3;
					}

					object3 = this._vals[int2];
				}	 while (!(object3 instanceof NonBlockingHashMapLong.Prime));

				return this.copy_slot_and_check(int2, object2).putIfMatch(long1, object, object2);
			} else {
				this.resize();
				return this.copy_slot_and_check(int2, object2).putIfMatch(long1, object, object2);
			}
		}

		private final boolean tableFull(int int1, int int2) {
			return int1 >= 10 && this._slots.estimate_get() >= (long)NonBlockingHashMapLong.reprobe_limit(int2);
		}

		private final NonBlockingHashMapLong.CHM resize() {
			NonBlockingHashMapLong.CHM cHM = this._newchm;
			if (cHM != null) {
				return cHM;
			} else {
				int int1 = this._keys.length;
				int int2 = this.size();
				int int3 = int2;
				if (this._nbhml._opt_for_space) {
					if (int2 >= int1 >> 1) {
						int3 = int1 << 1;
					}
				} else if (int2 >= int1 >> 2) {
					int3 = int1 << 1;
					if (int2 >= int1 >> 1) {
						int3 = int1 << 2;
					}
				}

				long long1 = System.currentTimeMillis();
				long long2 = 0L;
				if (int3 <= int1 && long1 <= this._nbhml._last_resize_milli + 10000L) {
					int3 = int1 << 1;
				}

				if (int3 < int1) {
					int3 = int1;
				}

				int int4;
				for (int4 = 4; 1 << int4 < int3; ++int4) {
				}

				long long3;
				for (long3 = this._resizers; !_resizerUpdater.compareAndSet(this, long3, long3 + 1L); long3 = this._resizers) {
				}

				int int5 = (1 << int4 << 1) + 4 << 3 >> 20;
				if (long3 >= 2L && int5 > 0) {
					cHM = this._newchm;
					if (cHM != null) {
						return cHM;
					}

					try {
						Thread.sleep((long)(8 * int5));
					} catch (Exception exception) {
					}
				}

				cHM = this._newchm;
				if (cHM != null) {
					return cHM;
				} else {
					cHM = new NonBlockingHashMapLong.CHM(this._nbhml, this._size, int4);
					if (this._newchm != null) {
						return this._newchm;
					} else {
						if (!this.CAS_newchm(cHM)) {
							cHM = this._newchm;
						}

						return cHM;
					}
				}
			}
		}

		private final void help_copy_impl(boolean boolean1) {
			NonBlockingHashMapLong.CHM cHM = this._newchm;
			assert cHM != null;
			int int1 = this._keys.length;
			int int2 = Math.min(int1, 1024);
			int int3 = -1;
			int int4 = -9999;
			do {
				if (this._copyDone >= (long)int1) {
					this.copy_check_and_promote(0);
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
					if (this.copy_slot(int4 + int6 & int1 - 1)) {
						++int5;
					}
				}

				if (int5 > 0) {
					this.copy_check_and_promote(int5);
				}

				int4 += int2;
			} while (boolean1 || int3 != -1);
		}

		private final NonBlockingHashMapLong.CHM copy_slot_and_check(int int1, Object object) {
			assert this._newchm != null;
			if (this.copy_slot(int1)) {
				this.copy_check_and_promote(1);
			}

			if (object != null) {
				this._nbhml.help_copy();
			}

			return this._newchm;
		}

		private final void copy_check_and_promote(int int1) {
			int int2 = this._keys.length;
			long long1 = this._copyDone;
			long long2 = long1 + (long)int1;
			assert long2 <= (long)int2;
			if (int1 > 0) {
				while (!_copyDoneUpdater.compareAndSet(this, long1, long2)) {
					long1 = this._copyDone;
					long2 = long1 + (long)int1;
					assert long2 <= (long)int2;
				}
			}

			if (long2 == (long)int2 && this._nbhml._chm == this && this._nbhml.CAS(NonBlockingHashMapLong._chm_offset, this, this._newchm)) {
				this._nbhml._last_resize_milli = System.currentTimeMillis();
			}
		}

		private boolean copy_slot(int int1) {
			long long1;
			while ((long1 = this._keys[int1]) == 0L) {
				this.CAS_key(int1, 0L, (long)(int1 + this._keys.length));
			}

			Object object;
			for (object = this._vals[int1]; !(object instanceof NonBlockingHashMapLong.Prime); object = this._vals[int1]) {
				NonBlockingHashMapLong.Prime prime = object != null && object != NonBlockingHashMapLong.TOMBSTONE ? new NonBlockingHashMapLong.Prime(object) : NonBlockingHashMapLong.TOMBPRIME;
				if (this.CAS_val(int1, object, prime)) {
					if (prime == NonBlockingHashMapLong.TOMBPRIME) {
						return true;
					}

					object = prime;
					break;
				}
			}

			if (object == NonBlockingHashMapLong.TOMBPRIME) {
				return false;
			} else {
				Object object2 = ((NonBlockingHashMapLong.Prime)object)._V;
				assert object2 != NonBlockingHashMapLong.TOMBSTONE;
				boolean boolean1;
				for (boolean1 = this._newchm.putIfMatch(long1, object2, (Object)null) == null; !this.CAS_val(int1, object, NonBlockingHashMapLong.TOMBPRIME); object = this._vals[int1]) {
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
			return object instanceof NonBlockingHashMapLong.Prime ? ((NonBlockingHashMapLong.Prime)object)._V : object;
		}
	}
}
