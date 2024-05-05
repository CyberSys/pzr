package javax.vecmath;

import java.io.Serializable;


public class Point4d extends Tuple4d implements Serializable {
	static final long serialVersionUID = 1733471895962736949L;

	public Point4d(double double1, double double2, double double3, double double4) {
		super(double1, double2, double3, double4);
	}

	public Point4d(double[] doubleArray) {
		super(doubleArray);
	}

	public Point4d(Point4d point4d) {
		super((Tuple4d)point4d);
	}

	public Point4d(Point4f point4f) {
		super((Tuple4f)point4f);
	}

	public Point4d(Tuple4f tuple4f) {
		super(tuple4f);
	}

	public Point4d(Tuple4d tuple4d) {
		super(tuple4d);
	}

	public Point4d(Tuple3d tuple3d) {
		super(tuple3d.x, tuple3d.y, tuple3d.z, 1.0);
	}

	public Point4d() {
	}

	public final void set(Tuple3d tuple3d) {
		this.x = tuple3d.x;
		this.y = tuple3d.y;
		this.z = tuple3d.z;
		this.w = 1.0;
	}

	public final double distanceSquared(Point4d point4d) {
		double double1 = this.x - point4d.x;
		double double2 = this.y - point4d.y;
		double double3 = this.z - point4d.z;
		double double4 = this.w - point4d.w;
		return double1 * double1 + double2 * double2 + double3 * double3 + double4 * double4;
	}

	public final double distance(Point4d point4d) {
		double double1 = this.x - point4d.x;
		double double2 = this.y - point4d.y;
		double double3 = this.z - point4d.z;
		double double4 = this.w - point4d.w;
		return Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3 + double4 * double4);
	}

	public final double distanceL1(Point4d point4d) {
		return Math.abs(this.x - point4d.x) + Math.abs(this.y - point4d.y) + Math.abs(this.z - point4d.z) + Math.abs(this.w - point4d.w);
	}

	public final double distanceLinf(Point4d point4d) {
		double double1 = Math.max(Math.abs(this.x - point4d.x), Math.abs(this.y - point4d.y));
		double double2 = Math.max(Math.abs(this.z - point4d.z), Math.abs(this.w - point4d.w));
		return Math.max(double1, double2);
	}

	public final void project(Point4d point4d) {
		double double1 = 1.0 / point4d.w;
		this.x = point4d.x * double1;
		this.y = point4d.y * double1;
		this.z = point4d.z * double1;
		this.w = 1.0;
	}
}
