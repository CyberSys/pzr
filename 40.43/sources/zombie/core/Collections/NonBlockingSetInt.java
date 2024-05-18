package zombie.core.Collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import sun.misc.Unsafe;


public class NonBlockingSetInt extends AbstractSet implements Serializable {
	private static final long serialVersionUID = 1234123412341234123L;
	private static final Unsafe _unsafe = UtilUnsafe.getUnsafe();
	private static final long _nbsi_offset;
	private transient NonBlockingSetInt.NBSI _nbsi;

	private final boolean CAS_nbsi(NonBlockingSetInt.NBSI nBSI, NonBlockingSetInt.NBSI nBSI2) {
		return _unsafe.compareAndSwapObject(this, _nbsi_offset, nBSI, nBSI2);
	}

	public NonBlockingSetInt() {
		this._nbsi = new NonBlockingSetInt.NBSI(63, new Counter(), this);
	}

	private NonBlockingSetInt(NonBlockingSetInt nonBlockingSetInt, NonBlockingSetInt nonBlockingSetInt2) {
		this._nbsi = new NonBlockingSetInt.NBSI(nonBlockingSetInt._nbsi, nonBlockingSetInt2._nbsi, new Counter(), this);
	}

	public boolean add(Integer integer) {
		return this.add(integer);
	}

	public boolean contains(Object object) {
		return object instanceof Integer ? this.contains((Integer)object) : false;
	}

	public boolean remove(Object object) {
		return object instanceof Integer ? this.remove((Integer)object) : false;
	}

	public boolean add(int int1) {
		if (int1 < 0) {
			throw new IllegalArgumentException("" + int1);
		} else {
			return this._nbsi.add(int1);
		}
	}

	public boolean contains(int int1) {
		return int1 < 0 ? false : this._nbsi.contains(int1);
	}

	public boolean remove(int int1) {
		return int1 < 0 ? false : this._nbsi.remove(int1);
	}

	public int size() {
		return this._nbsi.size();
	}

	public void clear() {
		NonBlockingSetInt.NBSI nBSI = new NonBlockingSetInt.NBSI(63, new Counter(), this);
		while (!this.CAS_nbsi(this._nbsi, nBSI)) {
		}
	}

	public int sizeInBytes() {
		return this._nbsi.sizeInBytes();
	}

	public NonBlockingSetInt intersect(NonBlockingSetInt nonBlockingSetInt) {
		NonBlockingSetInt nonBlockingSetInt2 = new NonBlockingSetInt(this, nonBlockingSetInt);
		nonBlockingSetInt2._nbsi.intersect(nonBlockingSetInt2._nbsi, this._nbsi, nonBlockingSetInt._nbsi);
		return nonBlockingSetInt2;
	}

	public NonBlockingSetInt union(NonBlockingSetInt nonBlockingSetInt) {
		NonBlockingSetInt nonBlockingSetInt2 = new NonBlockingSetInt(this, nonBlockingSetInt);
		nonBlockingSetInt2._nbsi.union(nonBlockingSetInt2._nbsi, this._nbsi, nonBlockingSetInt._nbsi);
		return nonBlockingSetInt2;
	}

	public void print() {
		this._nbsi.print(0);
	}

	public Iterator iterator() {
		return new NonBlockingSetInt.iter();
	}

	public IntIterator intIterator() {
		return new NonBlockingSetInt.NBSIIntIterator();
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		objectOutputStream.defaultWriteObject();
		NonBlockingSetInt.NBSI nBSI = this._nbsi;
		int int1 = this._nbsi._bits.length << 6;
		objectOutputStream.writeInt(int1);
		for (int int2 = 0; int2 < int1; ++int2) {
			objectOutputStream.writeBoolean(this._nbsi.contains(int2));
		}
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		int int1 = objectInputStream.readInt();
		this._nbsi = new NonBlockingSetInt.NBSI(int1, new Counter(), this);
		for (int int2 = 0; int2 < int1; ++int2) {
			if (objectInputStream.readBoolean()) {
				this._nbsi.add(int2);
			}
		}
	}

	static  {
	Field var0 = null;
	try {
		var0 = NonBlockingSetInt.class.getDeclaredField("_nbsi");
	} catch (NoSuchFieldException var2) {
	}

		_nbsi_offset = _unsafe.objectFieldOffset(var0);
	}

	private static final class NBSI {
		private final transient NonBlockingSetInt _non_blocking_set_int;
		private final transient Counter _size;
		private final long[] _bits;
		private static final int _Lbase;
		private static final int _Lscale;
		private NonBlockingSetInt.NBSI _new;
		private static final long _new_offset;
		private final transient AtomicInteger _copyIdx;
		private final transient AtomicInteger _copyDone;
		private final transient int _sum_bits_length;
		private final NonBlockingSetInt.NBSI _nbsi64;

		private static long rawIndex(long[] longArray, int int1) {
			assert int1 >= 0 && int1 < longArray.length;
			return (long)(_Lbase + int1 * _Lscale);
		}

		private final boolean CAS(int int1, long long1, long long2) {
			return NonBlockingSetInt._unsafe.compareAndSwapLong(this._bits, rawIndex(this._bits, int1), long1, long2);
		}

		private final boolean CAS_new(NonBlockingSetInt.NBSI nBSI) {
			return NonBlockingSetInt._unsafe.compareAndSwapObject(this, _new_offset, (Object)null, nBSI);
		}

		private static final long mask(int int1) {
			return 1L << (int1 & 63);
		}

		private NBSI(int int1, Counter counter, NonBlockingSetInt nonBlockingSetInt) {
			this._non_blocking_set_int = nonBlockingSetInt;
			this._size = counter;
			this._copyIdx = counter == null ? null : new AtomicInteger();
			this._copyDone = counter == null ? null : new AtomicInteger();
			this._bits = new long[(int)((long)int1 + 63L >>> 6)];
			this._nbsi64 = int1 + 1 >>> 6 == 0 ? null : new NonBlockingSetInt.NBSI(int1 + 1 >>> 6, (Counter)null, (NonBlockingSetInt)null);
			this._sum_bits_length = this._bits.length + (this._nbsi64 == null ? 0 : this._nbsi64._sum_bits_length);
		}

		private NBSI(NonBlockingSetInt.NBSI nBSI, NonBlockingSetInt.NBSI nBSI2, Counter counter, NonBlockingSetInt nonBlockingSetInt) {
			this._non_blocking_set_int = nonBlockingSetInt;
			this._size = counter;
			this._copyIdx = counter == null ? null : new AtomicInteger();
			this._copyDone = counter == null ? null : new AtomicInteger();
			if (!has_bits(nBSI) && !has_bits(nBSI2)) {
				this._bits = null;
				this._nbsi64 = null;
				this._sum_bits_length = 0;
			} else {
				if (!has_bits(nBSI)) {
					this._bits = new long[nBSI2._bits.length];
					this._nbsi64 = new NonBlockingSetInt.NBSI((NonBlockingSetInt.NBSI)null, nBSI2._nbsi64, (Counter)null, (NonBlockingSetInt)null);
				} else if (!has_bits(nBSI2)) {
					this._bits = new long[nBSI._bits.length];
					this._nbsi64 = new NonBlockingSetInt.NBSI((NonBlockingSetInt.NBSI)null, nBSI._nbsi64, (Counter)null, (NonBlockingSetInt)null);
				} else {
					int int1 = nBSI._bits.length > nBSI2._bits.length ? nBSI._bits.length : nBSI2._bits.length;
					this._bits = new long[int1];
					this._nbsi64 = new NonBlockingSetInt.NBSI(nBSI._nbsi64, nBSI2._nbsi64, (Counter)null, (NonBlockingSetInt)null);
				}

				this._sum_bits_length = this._bits.length + this._nbsi64._sum_bits_length;
			}
		}

		private static boolean has_bits(NonBlockingSetInt.NBSI nBSI) {
			return nBSI != null && nBSI._bits != null;
		}

		public boolean add(int int1) {
			if (int1 >> 6 >= this._bits.length) {
				return this.install_larger_new_bits(int1).help_copy().add(int1);
			} else {
				NonBlockingSetInt.NBSI nBSI = this;
				int int2;
				for (int2 = int1; (int2 & 63) == 63; int2 >>= 6) {
					nBSI = nBSI._nbsi64;
				}

				long long1 = mask(int2);
				long long2;
				do {
					long2 = nBSI._bits[int2 >> 6];
					if (long2 < 0L) {
						return this.help_copy_impl(int1).help_copy().add(int1);
					}

					if ((long2 & long1) != 0L) {
						return false;
					}
				}	 while (!nBSI.CAS(int2 >> 6, long2, long2 | long1));

				this._size.add(1L);
				return true;
			}
		}

		public boolean remove(int int1) {
			if (int1 >> 6 >= this._bits.length) {
				return this._new == null ? false : this.help_copy().remove(int1);
			} else {
				NonBlockingSetInt.NBSI nBSI = this;
				int int2;
				for (int2 = int1; (int2 & 63) == 63; int2 >>= 6) {
					nBSI = nBSI._nbsi64;
				}

				long long1 = mask(int2);
				long long2;
				do {
					long2 = nBSI._bits[int2 >> 6];
					if (long2 < 0L) {
						return this.help_copy_impl(int1).help_copy().remove(int1);
					}

					if ((long2 & long1) == 0L) {
						return false;
					}
				}	 while (!nBSI.CAS(int2 >> 6, long2, long2 & ~long1));

				this._size.add(-1L);
				return true;
			}
		}

		public boolean contains(int int1) {
			if (int1 >> 6 >= this._bits.length) {
				return this._new == null ? false : this.help_copy().contains(int1);
			} else {
				NonBlockingSetInt.NBSI nBSI = this;
				int int2;
				for (int2 = int1; (int2 & 63) == 63; int2 >>= 6) {
					nBSI = nBSI._nbsi64;
				}

				long long1 = mask(int2);
				long long2 = nBSI._bits[int2 >> 6];
				if (long2 < 0L) {
					return this.help_copy_impl(int1).help_copy().contains(int1);
				} else {
					return (long2 & long1) != 0L;
				}
			}
		}

		public boolean intersect(NonBlockingSetInt.NBSI nBSI, NonBlockingSetInt.NBSI nBSI2, NonBlockingSetInt.NBSI nBSI3) {
			if (has_bits(nBSI2) && has_bits(nBSI3)) {
				for (int int1 = 0; int1 < nBSI._bits.length; ++int1) {
					long long1 = nBSI2.safe_read_word(int1, 0L);
					long long2 = nBSI3.safe_read_word(int1, 0L);
					nBSI._bits[int1] = long1 & long2 & Long.MAX_VALUE;
				}

				return this.intersect(nBSI._nbsi64, nBSI2._nbsi64, nBSI3._nbsi64);
			} else {
				return true;
			}
		}

		public boolean union(NonBlockingSetInt.NBSI nBSI, NonBlockingSetInt.NBSI nBSI2, NonBlockingSetInt.NBSI nBSI3) {
			if (!has_bits(nBSI2) && !has_bits(nBSI3)) {
				return true;
			} else {
				if (has_bits(nBSI2) || has_bits(nBSI3)) {
					for (int int1 = 0; int1 < nBSI._bits.length; ++int1) {
						long long1 = nBSI2.safe_read_word(int1, 0L);
						long long2 = nBSI3.safe_read_word(int1, 0L);
						nBSI._bits[int1] = (long1 | long2) & Long.MAX_VALUE;
					}
				}

				return this.union(nBSI._nbsi64, nBSI2._nbsi64, nBSI3._nbsi64);
			}
		}

		private long safe_read_word(int int1, long long1) {
			if (int1 >= this._bits.length) {
				return long1;
			} else {
				long long2 = this._bits[int1];
				if (long2 < 0L) {
					long2 = this.help_copy_impl(int1).help_copy()._bits[int1];
				}

				return long2;
			}
		}

		public int sizeInBytes() {
			return this._bits.length;
		}

		public int size() {
			return (int)this._size.get();
		}

		private NonBlockingSetInt.NBSI install_larger_new_bits(int int1) {
			if (this._new == null) {
				int int2 = this._bits.length << 6 << 1;
				this.CAS_new(new NonBlockingSetInt.NBSI(int2, this._size, this._non_blocking_set_int));
			}

			return this;
		}

		private NonBlockingSetInt.NBSI help_copy() {
			NonBlockingSetInt.NBSI nBSI = this._non_blocking_set_int._nbsi;
			int int1 = nBSI._copyIdx.getAndAdd(512);
			for (int int2 = 0; int2 < 8; ++int2) {
				int int3 = int1 + int2 * 64;
				int3 %= nBSI._bits.length << 6;
				nBSI.help_copy_impl(int3);
				nBSI.help_copy_impl(int3 + 63);
			}

			if (nBSI._copyDone.get() == nBSI._sum_bits_length && this._non_blocking_set_int.CAS_nbsi(nBSI, nBSI._new)) {
			}

			return this._new;
		}

		private NonBlockingSetInt.NBSI help_copy_impl(int int1) {
			NonBlockingSetInt.NBSI nBSI = this;
			NonBlockingSetInt.NBSI nBSI2 = this._new;
			if (nBSI2 == null) {
				return this;
			} else {
				int int2;
				for (int2 = int1; (int2 & 63) == 63; int2 >>= 6) {
					nBSI = nBSI._nbsi64;
					nBSI2 = nBSI2._nbsi64;
				}

				long long1;
				long long2;
				for (long1 = nBSI._bits[int2 >> 6]; long1 >= 0L; long1 = nBSI._bits[int2 >> 6]) {
					long2 = long1;
					long1 |= mask(63);
					if (nBSI.CAS(int2 >> 6, long2, long1)) {
						if (long2 == 0L) {
							this._copyDone.addAndGet(1);
						}

						break;
					}
				}

				if (long1 != mask(63)) {
					long2 = nBSI2._bits[int2 >> 6];
					if (long2 == 0L) {
						long2 = long1 & ~mask(63);
						if (!nBSI2.CAS(int2 >> 6, 0L, long2)) {
							long2 = nBSI2._bits[int2 >> 6];
						}

						assert long2 != 0L;
					}

					if (nBSI.CAS(int2 >> 6, long1, mask(63))) {
						this._copyDone.addAndGet(1);
					}
				}

				return this;
			}
		}

		private void print(int int1, String string) {
			for (int int2 = 0; int2 < int1; ++int2) {
				System.out.print("  ");
			}

			System.out.println(string);
		}

		private void print(int int1) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("NBSI - _bits.len=");
			NonBlockingSetInt.NBSI nBSI;
			for (nBSI = this; nBSI != null; nBSI = nBSI._nbsi64) {
				stringBuffer.append(" " + nBSI._bits.length);
			}

			this.print(int1, stringBuffer.toString());
			nBSI = this;
			while (nBSI != null) {
				for (int int2 = 0; int2 < nBSI._bits.length; ++int2) {
					System.out.print(Long.toHexString(nBSI._bits[int2]) + " ");
				}

				nBSI = nBSI._nbsi64;
				System.out.println();
			}

			if (this._copyIdx.get() != 0 || this._copyDone.get() != 0) {
				this.print(int1, "_copyIdx=" + this._copyIdx.get() + " _copyDone=" + this._copyDone.get() + " _words_to_cpy=" + this._sum_bits_length);
			}

			if (this._new != null) {
				this.print(int1, "__has_new - ");
				this._new.print(int1 + 1);
			}
		}

		NBSI(int int1, Counter counter, NonBlockingSetInt nonBlockingSetInt, Object object) {
			this(int1, counter, nonBlockingSetInt);
		}

		NBSI(NonBlockingSetInt.NBSI nBSI, NonBlockingSetInt.NBSI nBSI2, Counter counter, NonBlockingSetInt nonBlockingSetInt, Object object) {
			this(nBSI, nBSI2, counter, nonBlockingSetInt);
		}

		static  {
			_Lbase = NonBlockingSetInt._unsafe.arrayBaseOffset(long[].class);
			_Lscale = NonBlockingSetInt._unsafe.arrayIndexScale(long[].class);
		Field var0 = null;
		try {
			var0 = NonBlockingSetInt.NBSI.class.getDeclaredField("_new");
		} catch (NoSuchFieldException var2) {
		}

			_new_offset = NonBlockingSetInt._unsafe.objectFieldOffset(var0);
		}
	}

	private class iter implements Iterator {
		NonBlockingSetInt.NBSIIntIterator intIterator = NonBlockingSetInt.this.new NBSIIntIterator();

		iter() {
		}

		public boolean hasNext() {
			return this.intIterator.hasNext();
		}

		public Integer next() {
			return this.intIterator.next();
		}

		public void remove() {
			this.intIterator.remove();
		}
	}

	private class NBSIIntIterator implements IntIterator {
		NonBlockingSetInt.NBSI nbsi;
		int index = -1;
		int prev = -1;

		NBSIIntIterator() {
			this.nbsi = NonBlockingSetInt.this._nbsi;
			this.advance();
		}

		private void advance() {
			do {
				++this.index;
				while (this.index >> 6 >= this.nbsi._bits.length) {
					if (this.nbsi._new == null) {
						this.index = -2;
						return;
					}

					this.nbsi = this.nbsi._new;
				}
			} while (!this.nbsi.contains(this.index));
		}

		public int next() {
			if (this.index == -1) {
				throw new NoSuchElementException();
			} else {
				this.prev = this.index;
				this.advance();
				return this.prev;
			}
		}

		public boolean hasNext() {
			return this.index != -2;
		}

		public void remove() {
			if (this.prev == -1) {
				throw new IllegalStateException();
			} else {
				this.nbsi.remove(this.prev);
				this.prev = -1;
			}
		}
	}
}
