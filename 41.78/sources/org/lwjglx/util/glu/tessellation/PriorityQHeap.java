package org.lwjglx.util.glu.tessellation;


class PriorityQHeap extends PriorityQ {
	PriorityQ.PQnode[] nodes = new PriorityQ.PQnode[33];
	PriorityQ.PQhandleElem[] handles;
	int size = 0;
	int max = 32;
	int freeList;
	boolean initialized;
	PriorityQ.Leq leq;

	PriorityQHeap(PriorityQ.Leq leq) {
		int int1;
		for (int1 = 0; int1 < this.nodes.length; ++int1) {
			this.nodes[int1] = new PriorityQ.PQnode();
		}

		this.handles = new PriorityQ.PQhandleElem[33];
		for (int1 = 0; int1 < this.handles.length; ++int1) {
			this.handles[int1] = new PriorityQ.PQhandleElem();
		}

		this.initialized = false;
		this.freeList = 0;
		this.leq = leq;
		this.nodes[1].handle = 1;
		this.handles[1].key = null;
	}

	void pqDeletePriorityQ() {
		this.handles = null;
		this.nodes = null;
	}

	void FloatDown(int int1) {
		PriorityQ.PQnode[] pQnodeArray = this.nodes;
		PriorityQ.PQhandleElem[] pQhandleElemArray = this.handles;
		int int2 = pQnodeArray[int1].handle;
		while (true) {
			int int3 = int1 << 1;
			if (int3 < this.size && LEQ(this.leq, pQhandleElemArray[pQnodeArray[int3 + 1].handle].key, pQhandleElemArray[pQnodeArray[int3].handle].key)) {
				++int3;
			}

			assert int3 <= this.max;
			int int4 = pQnodeArray[int3].handle;
			if (int3 > this.size || LEQ(this.leq, pQhandleElemArray[int2].key, pQhandleElemArray[int4].key)) {
				pQnodeArray[int1].handle = int2;
				pQhandleElemArray[int2].node = int1;
				return;
			}

			pQnodeArray[int1].handle = int4;
			pQhandleElemArray[int4].node = int1;
			int1 = int3;
		}
	}

	void FloatUp(int int1) {
		PriorityQ.PQnode[] pQnodeArray = this.nodes;
		PriorityQ.PQhandleElem[] pQhandleElemArray = this.handles;
		int int2 = pQnodeArray[int1].handle;
		while (true) {
			int int3 = int1 >> 1;
			int int4 = pQnodeArray[int3].handle;
			if (int3 == 0 || LEQ(this.leq, pQhandleElemArray[int4].key, pQhandleElemArray[int2].key)) {
				pQnodeArray[int1].handle = int2;
				pQhandleElemArray[int2].node = int1;
				return;
			}

			pQnodeArray[int1].handle = int4;
			pQhandleElemArray[int4].node = int1;
			int1 = int3;
		}
	}

	boolean pqInit() {
		for (int int1 = this.size; int1 >= 1; --int1) {
			this.FloatDown(int1);
		}

		this.initialized = true;
		return true;
	}

	int pqInsert(Object object) {
		int int1 = ++this.size;
		if (int1 * 2 > this.max) {
			PriorityQ.PQnode[] pQnodeArray = this.nodes;
			PriorityQ.PQhandleElem[] pQhandleElemArray = this.handles;
			this.max <<= 1;
			PriorityQ.PQnode[] pQnodeArray2 = new PriorityQ.PQnode[this.max + 1];
			System.arraycopy(this.nodes, 0, pQnodeArray2, 0, this.nodes.length);
			for (int int2 = this.nodes.length; int2 < pQnodeArray2.length; ++int2) {
				pQnodeArray2[int2] = new PriorityQ.PQnode();
			}

			this.nodes = pQnodeArray2;
			if (this.nodes == null) {
				this.nodes = pQnodeArray;
				return Integer.MAX_VALUE;
			}

			PriorityQ.PQhandleElem[] pQhandleElemArray2 = new PriorityQ.PQhandleElem[this.max + 1];
			System.arraycopy(this.handles, 0, pQhandleElemArray2, 0, this.handles.length);
			for (int int3 = this.handles.length; int3 < pQhandleElemArray2.length; ++int3) {
				pQhandleElemArray2[int3] = new PriorityQ.PQhandleElem();
			}

			this.handles = pQhandleElemArray2;
			if (this.handles == null) {
				this.handles = pQhandleElemArray;
				return Integer.MAX_VALUE;
			}
		}

		int int4;
		if (this.freeList == 0) {
			int4 = int1;
		} else {
			int4 = this.freeList;
			this.freeList = this.handles[int4].node;
		}

		this.nodes[int1].handle = int4;
		this.handles[int4].node = int1;
		this.handles[int4].key = object;
		if (this.initialized) {
			this.FloatUp(int1);
		}

		assert int4 != Integer.MAX_VALUE;
		return int4;
	}

	Object pqExtractMin() {
		PriorityQ.PQnode[] pQnodeArray = this.nodes;
		PriorityQ.PQhandleElem[] pQhandleElemArray = this.handles;
		int int1 = pQnodeArray[1].handle;
		Object object = pQhandleElemArray[int1].key;
		if (this.size > 0) {
			pQnodeArray[1].handle = pQnodeArray[this.size].handle;
			pQhandleElemArray[pQnodeArray[1].handle].node = 1;
			pQhandleElemArray[int1].key = null;
			pQhandleElemArray[int1].node = this.freeList;
			this.freeList = int1;
			if (--this.size > 0) {
				this.FloatDown(1);
			}
		}

		return object;
	}

	void pqDelete(int int1) {
		PriorityQ.PQnode[] pQnodeArray = this.nodes;
		PriorityQ.PQhandleElem[] pQhandleElemArray = this.handles;
		assert int1 >= 1 && int1 <= this.max && pQhandleElemArray[int1].key != null;
		int int2 = pQhandleElemArray[int1].node;
		pQnodeArray[int2].handle = pQnodeArray[this.size].handle;
		pQhandleElemArray[pQnodeArray[int2].handle].node = int2;
		if (int2 <= --this.size) {
			if (int2 > 1 && !LEQ(this.leq, pQhandleElemArray[pQnodeArray[int2 >> 1].handle].key, pQhandleElemArray[pQnodeArray[int2].handle].key)) {
				this.FloatUp(int2);
			} else {
				this.FloatDown(int2);
			}
		}

		pQhandleElemArray[int1].key = null;
		pQhandleElemArray[int1].node = this.freeList;
		this.freeList = int1;
	}

	Object pqMinimum() {
		return this.handles[this.nodes[1].handle].key;
	}

	boolean pqIsEmpty() {
		return this.size == 0;
	}
}
