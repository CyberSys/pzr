package javax.vecmath;

import java.io.Serializable;


public class Point2i extends Tuple2i implements Serializable {
	static final long serialVersionUID = 9208072376494084954L;

	public Point2i(int int1, int int2) {
		super(int1, int2);
	}

	public Point2i(int[] intArray) {
		super(intArray);
	}

	public Point2i(Tuple2i tuple2i) {
		super(tuple2i);
	}

	public Point2i() {
	}
}
