package javax.vecmath;

import java.io.Serializable;


public class Vector4d extends Tuple4d implements Serializable {
	static final long serialVersionUID = 3938123424117448700L;

	public Vector4d(double double1, double double2, double double3, double double4) {
		super(double1, double2, double3, double4);
	}

	public Vector4d(double[] doubleArray) {
		super(doubleArray);
	}

	public Vector4d(Vector4d vector4d) {
		super((Tuple4d)vector4d);
	}

	public Vector4d(Vector4f vector4f) {
		super((Tuple4f)vector4f);
	}

	public Vector4d(Tuple4f tuple4f) {
		super(tuple4f);
	}

	public Vector4d(Tuple4d tuple4d) {
		super(tuple4d);
	}

	public Vector4d(Tuple3d tuple3d) {
		super(tuple3d.x, tuple3d.y, tuple3d.z, 0.0);
	}

	public Vector4d() {
	}

	public final void set(Tuple3d tuple3d) {
		this.x = tuple3d.x;
		this.y = tuple3d.y;
		this.z = tuple3d.z;
		this.w = 0.0;
	}

	public final double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
	}

	public final double lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}

	public final double dot(Vector4d vector4d) {
		return this.x * vector4d.x + this.y * vector4d.y + this.z * vector4d.z + this.w * vector4d.w;
	}

	public final void normalize(Vector4d vector4d) {
		double double1 = 1.0 / Math.sqrt(vector4d.x * vector4d.x + vector4d.y * vector4d.y + vector4d.z * vector4d.z + vector4d.w * vector4d.w);
		this.x = vector4d.x * double1;
		this.y = vector4d.y * double1;
		this.z = vector4d.z * double1;
		this.w = vector4d.w * double1;
	}

	public final void normalize() {
		double double1 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		this.w *= double1;
	}

	public final double angle(Vector4d vector4d) {
		double double1 = this.dot(vector4d) / (this.length() * vector4d.length());
		if (double1 < -1.0) {
			double1 = -1.0;
		}

		if (double1 > 1.0) {
			double1 = 1.0;
		}

		return Math.acos(double1);
	}
}
