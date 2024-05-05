package javax.vecmath;

import java.io.Serializable;


public class Point4i extends Tuple4i implements Serializable {
	static final long serialVersionUID = 620124780244617983L;

	public Point4i(int int1, int int2, int int3, int int4) {
		super(int1, int2, int3, int4);
	}

	public Point4i(int[] intArray) {
		super(intArray);
	}

	public Point4i(Tuple4i tuple4i) {
		super(tuple4i);
	}

	public Point4i() {
	}
}
