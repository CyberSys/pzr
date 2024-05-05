package org.lwjglx.util.glu.tessellation;


class PriorityQSort extends PriorityQ {
	PriorityQHeap heap;
	Object[] keys;
	int[] order;
	int size;
	int max;
	boolean initialized;
	PriorityQ.Leq leq;

	PriorityQSort(PriorityQ.Leq leq) {
		this.heap = new PriorityQHeap(leq);
		this.keys = new Object[32];
		this.size = 0;
		this.max = 32;
		this.initialized = false;
		this.leq = leq;
	}

	void pqDeletePriorityQ() {
		if (this.heap != null) {
			this.heap.pqDeletePriorityQ();
		}

		this.order = null;
		this.keys = null;
	}

	private static boolean LT(PriorityQ.Leq leq, Object object, Object object2) {
		return !PriorityQHeap.LEQ(leq, object2, object);
	}

	private static boolean GT(PriorityQ.Leq leq, Object object, Object object2) {
		return !PriorityQHeap.LEQ(leq, object, object2);
	}

	private static void Swap(int[] intArray, int int1, int int2) {
		int int3 = intArray[int1];
		intArray[int1] = intArray[int2];
		intArray[int2] = int3;
	}

	boolean pqInit() {
		PriorityQSort.Stack[] stackArray = new PriorityQSort.Stack[50];
		int int1;
		for (int1 = 0; int1 < stackArray.length; ++int1) {
			stackArray[int1] = new PriorityQSort.Stack();
		}

		byte byte1 = 0;
		int int2 = 2016473283;
		this.order = new int[this.size + 1];
		byte byte2 = 0;
		int int3 = this.size - 1;
		int int4 = 0;
		int int5;
		for (int5 = byte2; int5 <= int3; ++int5) {
			this.order[int5] = int4++;
		}

		stackArray[byte1].p = byte2;
		stackArray[byte1].r = int3;
		int1 = byte1 + 1;
		while (true) {
			--int1;
			if (int1 < 0) {
				this.max = this.size;
				this.initialized = true;
				this.heap.pqInit();
				return true;
			}

			int int6 = stackArray[int1].p;
			int3 = stackArray[int1].r;
			int int7;
			while (int3 > int6 + 10) {
				int2 = Math.abs(int2 * 1539415821 + 1);
				int5 = int6 + int2 % (int3 - int6 + 1);
				int4 = this.order[int5];
				this.order[int5] = this.order[int6];
				this.order[int6] = int4;
				int5 = int6 - 1;
				int7 = int3 + 1;
				while (true) {
					++int5;
					if (!GT(this.leq, this.keys[this.order[int5]], this.keys[int4])) {
						do {
							--int7;
						}				 while (LT(this.leq, this.keys[this.order[int7]], this.keys[int4]));

						Swap(this.order, int5, int7);
						if (int5 >= int7) {
							Swap(this.order, int5, int7);
							if (int5 - int6 < int3 - int7) {
								stackArray[int1].p = int7 + 1;
								stackArray[int1].r = int3;
								++int1;
								int3 = int5 - 1;
							} else {
								stackArray[int1].p = int6;
								stackArray[int1].r = int5 - 1;
								++int1;
								int6 = int7 + 1;
							}

							break;
						}
					}
				}
			}

			for (int5 = int6 + 1; int5 <= int3; ++int5) {
				int4 = this.order[int5];
				for (int7 = int5; int7 > int6 && LT(this.leq, this.keys[this.order[int7 - 1]], this.keys[int4]); --int7) {
					this.order[int7] = this.order[int7 - 1];
				}

				this.order[int7] = int4;
			}
		}
	}

	int pqInsert(Object object) {
		if (this.initialized) {
			return this.heap.pqInsert(object);
		} else {
			int int1 = this.size;
			if (++this.size >= this.max) {
				Object[] objectArray = this.keys;
				this.max <<= 1;
				Object[] objectArray2 = new Object[this.max];
				System.arraycopy(this.keys, 0, objectArray2, 0, this.keys.length);
				this.keys = objectArray2;
				if (this.keys == null) {
					this.keys = objectArray;
					return Integer.MAX_VALUE;
				}
			}

			assert int1 != Integer.MAX_VALUE;
			this.keys[int1] = object;
			return -(int1 + 1);
		}
	}

	Object pqExtractMin() {
		if (this.size == 0) {
			return this.heap.pqExtractMin();
		} else {
			Object object = this.keys[this.order[this.size - 1]];
			if (!this.heap.pqIsEmpty()) {
				Object object2 = this.heap.pqMinimum();
				if (LEQ(this.leq, object2, object)) {
					return this.heap.pqExtractMin();
				}
			}

			do {
				--this.size;
			}	 while (this.size > 0 && this.keys[this.order[this.size - 1]] == null);

			return object;
		}
	}

	Object pqMinimum() {
		if (this.size == 0) {
			return this.heap.pqMinimum();
		} else {
			Object object = this.keys[this.order[this.size - 1]];
			if (!this.heap.pqIsEmpty()) {
				Object object2 = this.heap.pqMinimum();
				if (PriorityQHeap.LEQ(this.leq, object2, object)) {
					return object2;
				}
			}

			return object;
		}
	}

	boolean pqIsEmpty() {
		return this.size == 0 && this.heap.pqIsEmpty();
	}

	void pqDelete(int int1) {
		if (int1 >= 0) {
			this.heap.pqDelete(int1);
		} else {
			int1 = -(int1 + 1);
			assert int1 < this.max && this.keys[int1] != null;
			for (this.keys[int1] = null; this.size > 0 && this.keys[this.order[this.size - 1]] == null; --this.size) {
			}
		}
	}

	private static class Stack {
		int p;
		int r;
	}
}
