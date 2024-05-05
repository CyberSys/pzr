package javax.vecmath;

import java.io.Serializable;


public class Vector2d extends Tuple2d implements Serializable {
	static final long serialVersionUID = 8572646365302599857L;

	public Vector2d(double double1, double double2) {
		super(double1, double2);
	}

	public Vector2d(double[] doubleArray) {
		super(doubleArray);
	}

	public Vector2d(Vector2d vector2d) {
		super((Tuple2d)vector2d);
	}

	public Vector2d(Vector2f vector2f) {
		super((Tuple2f)vector2f);
	}

	public Vector2d(Tuple2d tuple2d) {
		super(tuple2d);
	}

	public Vector2d(Tuple2f tuple2f) {
		super(tuple2f);
	}

	public Vector2d() {
	}

	public final double dot(Vector2d vector2d) {
		return this.x * vector2d.x + this.y * vector2d.y;
	}

	public final double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public final double lengthSquared() {
		return this.x * this.x + this.y * this.y;
	}

	public final void normalize(Vector2d vector2d) {
		double double1 = 1.0 / Math.sqrt(vector2d.x * vector2d.x + vector2d.y * vector2d.y);
		this.x = vector2d.x * double1;
		this.y = vector2d.y * double1;
	}

	public final void normalize() {
		double double1 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y);
		this.x *= double1;
		this.y *= double1;
	}

	public final double angle(Vector2d vector2d) {
		double double1 = this.dot(vector2d) / (this.length() * vector2d.length());
		if (double1 < -1.0) {
			double1 = -1.0;
		}

		if (double1 > 1.0) {
			double1 = 1.0;
		}

		return Math.acos(double1);
	}
}
