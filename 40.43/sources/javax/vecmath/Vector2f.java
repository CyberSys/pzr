package javax.vecmath;

import java.io.Serializable;


public class Vector2f extends Tuple2f implements Serializable {
	static final long serialVersionUID = -2168194326883512320L;

	public Vector2f(float float1, float float2) {
		super(float1, float2);
	}

	public Vector2f(float[] floatArray) {
		super(floatArray);
	}

	public Vector2f(Vector2f vector2f) {
		super((Tuple2f)vector2f);
	}

	public Vector2f(Vector2d vector2d) {
		super((Tuple2d)vector2d);
	}

	public Vector2f(Tuple2f tuple2f) {
		super(tuple2f);
	}

	public Vector2f(Tuple2d tuple2d) {
		super(tuple2d);
	}

	public Vector2f() {
	}

	public final float dot(Vector2f vector2f) {
		return this.x * vector2f.x + this.y * vector2f.y;
	}

	public final float length() {
		return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y));
	}

	public final float lengthSquared() {
		return this.x * this.x + this.y * this.y;
	}

	public final void normalize(Vector2f vector2f) {
		float float1 = (float)(1.0 / Math.sqrt((double)(vector2f.x * vector2f.x + vector2f.y * vector2f.y)));
		this.x = vector2f.x * float1;
		this.y = vector2f.y * float1;
	}

	public final void normalize() {
		float float1 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y)));
		this.x *= float1;
		this.y *= float1;
	}

	public final float angle(Vector2f vector2f) {
		double double1 = (double)(this.dot(vector2f) / (this.length() * vector2f.length()));
		if (double1 < -1.0) {
			double1 = -1.0;
		}

		if (double1 > 1.0) {
			double1 = 1.0;
		}

		return (float)Math.acos(double1);
	}
}
