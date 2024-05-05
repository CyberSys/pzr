package org.lwjglx.util.glu.tessellation;


abstract class PriorityQ {
	public static final int INIT_SIZE = 32;

	public static boolean LEQ(PriorityQ.Leq leq, Object object, Object object2) {
		return Geom.VertLeq((GLUvertex)object, (GLUvertex)object2);
	}

	static PriorityQ pqNewPriorityQ(PriorityQ.Leq leq) {
		return new PriorityQSort(leq);
	}

	abstract void pqDeletePriorityQ();

	abstract boolean pqInit();

	abstract int pqInsert(Object object);

	abstract Object pqExtractMin();

	abstract void pqDelete(int int1);

	abstract Object pqMinimum();

	abstract boolean pqIsEmpty();

	public interface Leq {

		boolean leq(Object object, Object object2);
	}

	public static class PQhandleElem {
		Object key;
		int node;
	}

	public static class PQnode {
		int handle;
	}
}
