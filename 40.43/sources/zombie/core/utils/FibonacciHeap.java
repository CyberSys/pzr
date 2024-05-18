package zombie.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import zombie.iso.IsoGridSquare;


public final class FibonacciHeap {
	private FibonacciHeap.Entry mMin = null;
	private int mSize = 0;
	List treeTable = new ArrayList(300);
	List toVisit = new ArrayList(300);

	public void empty() {
		this.mMin = null;
		this.mSize = 0;
	}

	public FibonacciHeap.Entry enqueue(Object object, double double1) {
		this.checkPriority(double1);
		FibonacciHeap.Entry entry = new FibonacciHeap.Entry(object, double1);
		this.mMin = mergeLists(this.mMin, entry);
		++this.mSize;
		return entry;
	}

	public FibonacciHeap.Entry min() {
		if (this.isEmpty()) {
			throw new NoSuchElementException("Heap is empty.");
		} else {
			return this.mMin;
		}
	}

	public boolean isEmpty() {
		return this.mMin == null;
	}

	public int size() {
		return this.mSize;
	}

	public static FibonacciHeap merge(FibonacciHeap fibonacciHeap, FibonacciHeap fibonacciHeap2) {
		FibonacciHeap fibonacciHeap3 = new FibonacciHeap();
		fibonacciHeap3.mMin = mergeLists(fibonacciHeap.mMin, fibonacciHeap2.mMin);
		fibonacciHeap3.mSize = fibonacciHeap.mSize + fibonacciHeap2.mSize;
		fibonacciHeap.mSize = fibonacciHeap2.mSize = 0;
		fibonacciHeap.mMin = null;
		fibonacciHeap2.mMin = null;
		return fibonacciHeap3;
	}

	public FibonacciHeap.Entry dequeueMin() {
		if (this.isEmpty()) {
			throw new NoSuchElementException("Heap is empty.");
		} else {
			--this.mSize;
			FibonacciHeap.Entry entry = this.mMin;
			if (this.mMin.mNext == this.mMin) {
				this.mMin = null;
			} else {
				this.mMin.mPrev.mNext = this.mMin.mNext;
				this.mMin.mNext.mPrev = this.mMin.mPrev;
				this.mMin = this.mMin.mNext;
			}

			FibonacciHeap.Entry entry2;
			if (entry.mChild != null) {
				entry2 = entry.mChild;
				do {
					entry2.mParent = null;
					entry2 = entry2.mNext;
				}		 while (entry2 != entry.mChild);
			}

			this.mMin = mergeLists(this.mMin, entry.mChild);
			if (this.mMin == null) {
				return entry;
			} else {
				this.treeTable.clear();
				this.toVisit.clear();
				for (entry2 = this.mMin; this.toVisit.isEmpty() || this.toVisit.get(0) != entry2; entry2 = entry2.mNext) {
					this.toVisit.add(entry2);
				}

				Iterator iterator = this.toVisit.iterator();
				label57: while (iterator.hasNext()) {
					FibonacciHeap.Entry entry3 = (FibonacciHeap.Entry)iterator.next();
					while (true) {
						while (entry3.mDegree < this.treeTable.size()) {
							if (this.treeTable.get(entry3.mDegree) == null) {
								this.treeTable.set(entry3.mDegree, entry3);
								if (entry3.mPriority <= this.mMin.mPriority) {
									this.mMin = entry3;
								}

								continue label57;
							}

							FibonacciHeap.Entry entry4 = (FibonacciHeap.Entry)this.treeTable.get(entry3.mDegree);
							this.treeTable.set(entry3.mDegree, (Object)null);
							FibonacciHeap.Entry entry5 = entry4.mPriority < entry3.mPriority ? entry4 : entry3;
							FibonacciHeap.Entry entry6 = entry4.mPriority < entry3.mPriority ? entry3 : entry4;
							entry6.mNext.mPrev = entry6.mPrev;
							entry6.mPrev.mNext = entry6.mNext;
							entry6.mNext = entry6.mPrev = entry6;
							entry5.mChild = mergeLists(entry5.mChild, entry6);
							entry6.mParent = entry5;
							entry6.mIsMarked = false;
							++entry5.mDegree;
							entry3 = entry5;
						}

						this.treeTable.add((Object)null);
					}
				}

				return entry;
			}
		}
	}

	public void decreaseKey(FibonacciHeap.Entry entry, double double1) {
		this.checkPriority(double1);
		if (double1 > entry.mPriority) {
			throw new IllegalArgumentException("New priority exceeds old.");
		} else {
			this.decreaseKeyUnchecked(entry, double1);
		}
	}

	public void delete(FibonacciHeap.Entry entry) {
		this.decreaseKeyUnchecked(entry, Double.NEGATIVE_INFINITY);
		this.dequeueMin();
	}

	public void delete(int int1, IsoGridSquare square) {
	}

	private void checkPriority(double double1) {
		if (Double.isNaN(double1)) {
			throw new IllegalArgumentException(double1 + " is invalid.");
		}
	}

	private static FibonacciHeap.Entry mergeLists(FibonacciHeap.Entry entry, FibonacciHeap.Entry entry2) {
		if (entry == null && entry2 == null) {
			return null;
		} else if (entry != null && entry2 == null) {
			return entry;
		} else if (entry == null && entry2 != null) {
			return entry2;
		} else {
			FibonacciHeap.Entry entry3 = entry.mNext;
			entry.mNext = entry2.mNext;
			entry.mNext.mPrev = entry;
			entry2.mNext = entry3;
			entry2.mNext.mPrev = entry2;
			return entry.mPriority < entry2.mPriority ? entry : entry2;
		}
	}

	private void decreaseKeyUnchecked(FibonacciHeap.Entry entry, double double1) {
		entry.mPriority = double1;
		if (entry.mParent != null && entry.mPriority <= entry.mParent.mPriority) {
			this.cutNode(entry);
		}

		if (entry.mPriority <= this.mMin.mPriority) {
			this.mMin = entry;
		}
	}

	private void decreaseKeyUncheckedNode(FibonacciHeap.Entry entry, double double1) {
		entry.mPriority = double1;
		if (entry.mParent != null && entry.mPriority <= entry.mParent.mPriority) {
			this.cutNodeNode(entry);
		}

		if (entry.mPriority <= this.mMin.mPriority) {
			this.mMin = entry;
		}
	}

	private void cutNode(FibonacciHeap.Entry entry) {
		entry.mIsMarked = false;
		if (entry.mParent != null) {
			if (entry.mNext != entry) {
				entry.mNext.mPrev = entry.mPrev;
				entry.mPrev.mNext = entry.mNext;
			}

			if (entry.mParent.mChild == entry) {
				if (entry.mNext != entry) {
					entry.mParent.mChild = entry.mNext;
				} else {
					entry.mParent.mChild = null;
				}
			}

			--entry.mParent.mDegree;
			entry.mPrev = entry.mNext = entry;
			this.mMin = mergeLists(this.mMin, entry);
			if (entry.mParent.mIsMarked) {
				this.cutNode(entry.mParent);
			} else {
				entry.mParent.mIsMarked = true;
			}

			entry.mParent = null;
		}
	}

	private void cutNodeNode(FibonacciHeap.Entry entry) {
		entry.mIsMarked = false;
		if (entry.mParent != null) {
			if (entry.mNext != entry) {
				entry.mNext.mPrev = entry.mPrev;
				entry.mPrev.mNext = entry.mNext;
			}

			if (entry.mParent.mChild == entry) {
				if (entry.mNext != entry) {
					entry.mParent.mChild = entry.mNext;
				} else {
					entry.mParent.mChild = null;
				}
			}

			--entry.mParent.mDegree;
			entry.mPrev = entry.mNext = entry;
			this.mMin = mergeLists(this.mMin, entry);
			if (entry.mParent.mIsMarked) {
				this.cutNode(entry.mParent);
			} else {
				entry.mParent.mIsMarked = true;
			}

			entry.mParent = null;
		}
	}

	public static final class Entry {
		private int mDegree;
		private boolean mIsMarked;
		private FibonacciHeap.Entry mNext;
		private FibonacciHeap.Entry mPrev;
		private FibonacciHeap.Entry mParent;
		private FibonacciHeap.Entry mChild;
		private Object mElem;
		private double mPriority;

		public Object getValue() {
			return this.mElem;
		}

		public void setValue(Object object) {
			this.mElem = object;
		}

		public double getPriority() {
			return this.mPriority;
		}

		private Entry(Object object, double double1) {
			this.mDegree = 0;
			this.mIsMarked = false;
			this.mNext = this.mPrev = this;
			this.mElem = object;
			this.mPriority = double1;
		}

		Entry(Object object, double double1, Object object2) {
			this(object, double1);
		}
	}
}
