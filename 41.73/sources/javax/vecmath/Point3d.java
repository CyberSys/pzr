package javax.vecmath;

import java.io.Serializable;


public class Point3d extends Tuple3d implements Serializable {
	static final long serialVersionUID = 5718062286069042927L;

	public Point3d(double double1, double double2, double double3) {
		super(double1, double2, double3);
	}

	public Point3d(double[] doubleArray) {
		super(doubleArray);
	}

	public Point3d(Point3d point3d) {
		super((Tuple3d)point3d);
	}

	public Point3d(Point3f point3f) {
		super((Tuple3f)point3f);
	}

	public Point3d(Tuple3f tuple3f) {
		super(tuple3f);
	}

	public Point3d(Tuple3d tuple3d) {
		super(tuple3d);
	}

	public Point3d() {
	}

	public final double distanceSquared(Point3d point3d) {
		double double1 = this.x - point3d.x;
		double double2 = this.y - point3d.y;
		double double3 = this.z - point3d.z;
		return double1 * double1 + double2 * double2 + double3 * double3;
	}

	public final double distance(Point3d point3d) {
		double double1 = this.x - point3d.x;
		double double2 = this.y - point3d.y;
		double double3 = this.z - point3d.z;
		return Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
	}

	public final double distanceL1(Point3d point3d) {
		return Math.abs(this.x - point3d.x) + Math.abs(this.y - point3d.y) + Math.abs(this.z - point3d.z);
	}

	public final double distanceLinf(Point3d point3d) {
		double double1 = Math.max(Math.abs(this.x - point3d.x), Math.abs(this.y - point3d.y));
		return Math.max(double1, Math.abs(this.z - point3d.z));
	}

	public final void project(Point4d point4d) {
		double double1 = 1.0 / point4d.w;
		this.x = point4d.x * double1;
		this.y = point4d.y * double1;
		this.z = point4d.z * double1;
	}
}
