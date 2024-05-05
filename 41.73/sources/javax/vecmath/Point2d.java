package javax.vecmath;

import java.io.Serializable;


public class Point2d extends Tuple2d implements Serializable {
	static final long serialVersionUID = 1133748791492571954L;

	public Point2d(double double1, double double2) {
		super(double1, double2);
	}

	public Point2d(double[] doubleArray) {
		super(doubleArray);
	}

	public Point2d(Point2d point2d) {
		super((Tuple2d)point2d);
	}

	public Point2d(Point2f point2f) {
		super((Tuple2f)point2f);
	}

	public Point2d(Tuple2d tuple2d) {
		super(tuple2d);
	}

	public Point2d(Tuple2f tuple2f) {
		super(tuple2f);
	}

	public Point2d() {
	}

	public final double distanceSquared(Point2d point2d) {
		double double1 = this.x - point2d.x;
		double double2 = this.y - point2d.y;
		return double1 * double1 + double2 * double2;
	}

	public final double distance(Point2d point2d) {
		double double1 = this.x - point2d.x;
		double double2 = this.y - point2d.y;
		return Math.sqrt(double1 * double1 + double2 * double2);
	}

	public final double distanceL1(Point2d point2d) {
		return Math.abs(this.x - point2d.x) + Math.abs(this.y - point2d.y);
	}

	public final double distanceLinf(Point2d point2d) {
		return Math.max(Math.abs(this.x - point2d.x), Math.abs(this.y - point2d.y));
	}
}
