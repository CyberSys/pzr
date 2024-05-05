package javax.vecmath;

import java.io.Serializable;


public class Point3i extends Tuple3i implements Serializable {
	static final long serialVersionUID = 6149289077348153921L;

	public Point3i(int int1, int int2, int int3) {
		super(int1, int2, int3);
	}

	public Point3i(int[] intArray) {
		super(intArray);
	}

	public Point3i(Tuple3i tuple3i) {
		super(tuple3i);
	}

	public Point3i() {
	}
}
