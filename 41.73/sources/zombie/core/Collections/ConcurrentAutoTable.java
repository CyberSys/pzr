package zombie.core.Collections;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import sun.misc.Unsafe;


public class ConcurrentAutoTable implements Serializable {
	private volatile ConcurrentAutoTable.CAT _cat = new ConcurrentAutoTable.CAT((ConcurrentAutoTable.CAT)null, 4, 0L);
	private static final AtomicReferenceFieldUpdater _catUpdater = AtomicReferenceFieldUpdater.newUpdater(ConcurrentAutoTable.class, ConcurrentAutoTable.CAT.class, "_cat");

	public void add(long long1) {
		this.add_if_mask(long1, 0L);
	}

	public void decrement() {
		this.add_if_mask(-1L, 0L);
	}

	public void increment() {
		this.add_if_mask(1L, 0L);
	}

	public void set(long long1) {
		ConcurrentAutoTable.CAT cAT = new ConcurrentAutoTable.CAT((ConcurrentAutoTable.CAT)null, 4, long1);
		while (!this.CAS_cat(this._cat, cAT)) {
		}
	}

	public long get() {
		return this._cat.sum(0L);
	}

	public int intValue() {
		return (int)this._cat.sum(0L);
	}

	public long longValue() {
		return this._cat.sum(0L);
	}

	public long estimate_get() {
		return this._cat.estimate_sum(0L);
	}

	public String toString() {
		return this._cat.toString(0L);
	}

	public void print() {
		this._cat.print();
	}

	public int internal_size() {
		return this._cat._t.length;
	}

	private long add_if_mask(long long1, long long2) {
		return this._cat.add_if_mask(long1, long2, hash(), this);
	}

	private boolean CAS_cat(ConcurrentAutoTable.CAT cAT, ConcurrentAutoTable.CAT cAT2) {
		return _catUpdater.compareAndSet(this, cAT, cAT2);
	}

	private static final int hash() {
		int int1 = System.identityHashCode(Thread.currentThread());
		int1 ^= int1 >>> 20 ^ int1 >>> 12;
		int1 ^= int1 >>> 7 ^ int1 >>> 4;
		return int1 << 2;
	}

	private static class CAT implements Serializable {
		private static final Unsafe _unsafe = UtilUnsafe.getUnsafe();
		private static final int _Lbase;
		private static final int _Lscale;
		volatile long _resizers;
		private static final AtomicLongFieldUpdater _resizerUpdater;
		private final ConcurrentAutoTable.CAT _next;
		private volatile long _sum_cache;
		private volatile long _fuzzy_sum_cache;
		private volatile long _fuzzy_time;
		private static final int MAX_SPIN = 2;
		private long[] _t;

		private static long rawIndex(long[] longArray, int int1) {
			assert int1 >= 0 && int1 < longArray.length;
			return (long)(_Lbase + int1 * _Lscale);
		}

		private static final boolean CAS(long[] longArray, int int1, long long1, long long2) {
			return _unsafe.compareAndSwapLong(longArray, rawIndex(longArray, int1), long1, long2);
		}

		CAT(ConcurrentAutoTable.CAT cAT, int int1, long long1) {
			this._next = cAT;
			this._sum_cache = Long.MIN_VALUE;
			this._t = new long[int1];
			this._t[0] = long1;
		}

		public long add_if_mask(long long1, long long2, int int1, ConcurrentAutoTable concurrentAutoTable) {
			long[] longArray = this._t;
			int int2 = int1 & longArray.length - 1;
			long long3 = longArray[int2];
			boolean boolean1 = CAS(longArray, int2, long3 & ~long2, long3 + long1);
			if (this._sum_cache != Long.MIN_VALUE) {
				this._sum_cache = Long.MIN_VALUE;
			}

			if (boolean1) {
				return long3;
			} else if ((long3 & long2) != 0L) {
				return long3;
			} else {
				int int3 = 0;
				while (true) {
					long3 = longArray[int2];
					if ((long3 & long2) != 0L) {
						return long3;
					}

					if (CAS(longArray, int2, long3, long3 + long1)) {
						if (int3 < 2) {
							return long3;
						}

						if (longArray.length >= 1048576) {
							return long3;
						}

						long long4 = this._resizers;
						int int4;
						for (int4 = longArray.length << 1 << 3; !_resizerUpdater.compareAndSet(this, long4, long4 + (long)int4); long4 = this._resizers) {
						}

						long4 += (long)int4;
						if (concurrentAutoTable._cat != this) {
							return long3;
						}

						if (long4 >> 17 != 0L) {
							try {
								Thread.sleep(long4 >> 17);
							} catch (InterruptedException interruptedException) {
							}

							if (concurrentAutoTable._cat != this) {
								return long3;
							}
						}

						ConcurrentAutoTable.CAT cAT = new ConcurrentAutoTable.CAT(this, longArray.length * 2, 0L);
						concurrentAutoTable.CAS_cat(this, cAT);
						return long3;
					}

					++int3;
				}
			}
		}

		public long sum(long long1) {
			long long2 = this._sum_cache;
			if (long2 != Long.MIN_VALUE) {
				return long2;
			} else {
				long2 = this._next == null ? 0L : this._next.sum(long1);
				long[] longArray = this._t;
				for (int int1 = 0; int1 < longArray.length; ++int1) {
					long2 += longArray[int1] & ~long1;
				}

				this._sum_cache = long2;
				return long2;
			}
		}

		public long estimate_sum(long long1) {
			if (this._t.length <= 64) {
				return this.sum(long1);
			} else {
				long long2 = System.currentTimeMillis();
				if (this._fuzzy_time != long2) {
					this._fuzzy_sum_cache = this.sum(long1);
					this._fuzzy_time = long2;
				}

				return this._fuzzy_sum_cache;
			}
		}

		public void all_or(long long1) {
			long[] longArray = this._t;
			long long2;
			for (int int1 = 0; int1 < longArray.length; ++int1) {
				for (boolean boolean1 = false; !boolean1; boolean1 = CAS(longArray, int1, long2, long2 | long1)) {
					long2 = longArray[int1];
				}
			}

			if (this._next != null) {
				this._next.all_or(long1);
			}

			if (this._sum_cache != Long.MIN_VALUE) {
				this._sum_cache = Long.MIN_VALUE;
			}
		}

		public void all_and(long long1) {
			long[] longArray = this._t;
			long long2;
			for (int int1 = 0; int1 < longArray.length; ++int1) {
				for (boolean boolean1 = false; !boolean1; boolean1 = CAS(longArray, int1, long2, long2 & long1)) {
					long2 = longArray[int1];
				}
			}

			if (this._next != null) {
				this._next.all_and(long1);
			}

			if (this._sum_cache != Long.MIN_VALUE) {
				this._sum_cache = Long.MIN_VALUE;
			}
		}

		public void all_set(long long1) {
			long[] longArray = this._t;
			for (int int1 = 0; int1 < longArray.length; ++int1) {
				longArray[int1] = long1;
			}

			if (this._next != null) {
				this._next.all_set(long1);
			}

			if (this._sum_cache != Long.MIN_VALUE) {
				this._sum_cache = Long.MIN_VALUE;
			}
		}

		String toString(long long1) {
			return Long.toString(this.sum(long1));
		}

		public void print() {
			long[] longArray = this._t;
			System.out.print("[sum=" + this._sum_cache + "," + longArray[0]);
			for (int int1 = 1; int1 < longArray.length; ++int1) {
				System.out.print("," + longArray[int1]);
			}

			System.out.print("]");
			if (this._next != null) {
				this._next.print();
			}
		}

		static  {
			_Lbase = _unsafe.arrayBaseOffset(long[].class);
			_Lscale = _unsafe.arrayIndexScale(long[].class);
			_resizerUpdater = AtomicLongFieldUpdater.newUpdater(ConcurrentAutoTable.CAT.class, "_resizers");
		}
	}
}
