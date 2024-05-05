package javax.vecmath;

import java.io.Serializable;


public class Vector3d extends Tuple3d implements Serializable {
	static final long serialVersionUID = 3761969948420550442L;

	public Vector3d(double double1, double double2, double double3) {
		super(double1, double2, double3);
	}

	public Vector3d(double[] doubleArray) {
		super(doubleArray);
	}

	public Vector3d(Vector3d vector3d) {
		super((Tuple3d)vector3d);
	}

	public Vector3d(Vector3f vector3f) {
		super((Tuple3f)vector3f);
	}

	public Vector3d(Tuple3f tuple3f) {
		super(tuple3f);
	}

	public Vector3d(Tuple3d tuple3d) {
		super(tuple3d);
	}

	public Vector3d() {
	}

	public final void cross(Vector3d vector3d, Vector3d vector3d2) {
		double double1 = vector3d.y * vector3d2.z - vector3d.z * vector3d2.y;
		double double2 = vector3d2.x * vector3d.z - vector3d2.z * vector3d.x;
		this.z = vector3d.x * vector3d2.y - vector3d.y * vector3d2.x;
		this.x = double1;
		this.y = double2;
	}

	public final void normalize(Vector3d vector3d) {
		double double1 = 1.0 / Math.sqrt(vector3d.x * vector3d.x + vector3d.y * vector3d.y + vector3d.z * vector3d.z);
		this.x = vector3d.x * double1;
		this.y = vector3d.y * double1;
		this.z = vector3d.z * double1;
	}

	public final void normalize() {
		double double1 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
	}

	public final double dot(Vector3d vector3d) {
		return this.x * vector3d.x + this.y * vector3d.y + this.z * vector3d.z;
	}

	public final double lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	public final double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	public final double angle(Vector3d vector3d) {
		double double1 = this.dot(vector3d) / (this.length() * vector3d.length());
		if (double1 < -1.0) {
			double1 = -1.0;
		}

		if (double1 > 1.0) {
			double1 = 1.0;
		}

		return Math.acos(double1);
	}
}
