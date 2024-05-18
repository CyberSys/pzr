package javax.vecmath;

import java.io.Serializable;


public abstract class Tuple2f implements Serializable,Cloneable {
	static final long serialVersionUID = 9011180388985266884L;
	public float x;
	public float y;

	public Tuple2f(float float1, float float2) {
		this.x = float1;
		this.y = float2;
	}

	public Tuple2f(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
	}

	public Tuple2f(Tuple2f tuple2f) {
		this.x = tuple2f.x;
		this.y = tuple2f.y;
	}

	public Tuple2f(Tuple2d tuple2d) {
		this.x = (float)tuple2d.x;
		this.y = (float)tuple2d.y;
	}

	public Tuple2f() {
		this.x = 0.0F;
		this.y = 0.0F;
	}

	public final void set(float float1, float float2) {
		this.x = float1;
		this.y = float2;
	}

	public final void set(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
	}

	public final void set(Tuple2f tuple2f) {
		this.x = tuple2f.x;
		this.y = tuple2f.y;
	}

	public final void set(Tuple2d tuple2d) {
		this.x = (float)tuple2d.x;
		this.y = (float)tuple2d.y;
	}

	public final void get(float[] floatArray) {
		floatArray[0] = this.x;
		floatArray[1] = this.y;
	}

	public final void add(Tuple2f tuple2f, Tuple2f tuple2f2) {
		this.x = tuple2f.x + tuple2f2.x;
		this.y = tuple2f.y + tuple2f2.y;
	}

	public final void add(Tuple2f tuple2f) {
		this.x += tuple2f.x;
		this.y += tuple2f.y;
	}

	public final void sub(Tuple2f tuple2f, Tuple2f tuple2f2) {
		this.x = tuple2f.x - tuple2f2.x;
		this.y = tuple2f.y - tuple2f2.y;
	}

	public final void sub(Tuple2f tuple2f) {
		this.x -= tuple2f.x;
		this.y -= tuple2f.y;
	}

	public final void negate(Tuple2f tuple2f) {
		this.x = -tuple2f.x;
		this.y = -tuple2f.y;
	}

	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
	}

	public final void scale(float float1, Tuple2f tuple2f) {
		this.x = float1 * tuple2f.x;
		this.y = float1 * tuple2f.y;
	}

	public final void scale(float float1) {
		this.x *= float1;
		this.y *= float1;
	}

	public final void scaleAdd(float float1, Tuple2f tuple2f, Tuple2f tuple2f2) {
		this.x = float1 * tuple2f.x + tuple2f2.x;
		this.y = float1 * tuple2f.y + tuple2f2.y;
	}

	public final void scaleAdd(float float1, Tuple2f tuple2f) {
		this.x = float1 * this.x + tuple2f.x;
		this.y = float1 * this.y + tuple2f.y;
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.x);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.y);
		return (int)(long1 ^ long1 >> 32);
	}

	public boolean equals(Tuple2f tuple2f) {
		try {
			return this.x == tuple2f.x && this.y == tuple2f.y;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Tuple2f tuple2f = (Tuple2f)object;
			return this.x == tuple2f.x && this.y == tuple2f.y;
		} catch (NullPointerException nullPointerException) {
			return false;
		} catch (ClassCastException classCastException) {
			return false;
		}
	}

	public boolean epsilonEquals(Tuple2f tuple2f, float float1) {
		float float2 = this.x - tuple2f.x;
		if (Float.isNaN(float2)) {
			return false;
		} else if ((float2 < 0.0F ? -float2 : float2) > float1) {
			return false;
		} else {
			float2 = this.y - tuple2f.y;
			if (Float.isNaN(float2)) {
				return false;
			} else {
				return !((float2 < 0.0F ? -float2 : float2) > float1);
			}
		}
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}

	public final void clamp(float float1, float float2, Tuple2f tuple2f) {
		if (tuple2f.x > float2) {
			this.x = float2;
		} else if (tuple2f.x < float1) {
			this.x = float1;
		} else {
			this.x = tuple2f.x;
		}

		if (tuple2f.y > float2) {
			this.y = float2;
		} else if (tuple2f.y < float1) {
			this.y = float1;
		} else {
			this.y = tuple2f.y;
		}
	}

	public final void clampMin(float float1, Tuple2f tuple2f) {
		if (tuple2f.x < float1) {
			this.x = float1;
		} else {
			this.x = tuple2f.x;
		}

		if (tuple2f.y < float1) {
			this.y = float1;
		} else {
			this.y = tuple2f.y;
		}
	}

	public final void clampMax(float float1, Tuple2f tuple2f) {
		if (tuple2f.x > float1) {
			this.x = float1;
		} else {
			this.x = tuple2f.x;
		}

		if (tuple2f.y > float1) {
			this.y = float1;
		} else {
			this.y = tuple2f.y;
		}
	}

	public final void absolute(Tuple2f tuple2f) {
		this.x = Math.abs(tuple2f.x);
		this.y = Math.abs(tuple2f.y);
	}

	public final void clamp(float float1, float float2) {
		if (this.x > float2) {
			this.x = float2;
		} else if (this.x < float1) {
			this.x = float1;
		}

		if (this.y > float2) {
			this.y = float2;
		} else if (this.y < float1) {
			this.y = float1;
		}
	}

	public final void clampMin(float float1) {
		if (this.x < float1) {
			this.x = float1;
		}

		if (this.y < float1) {
			this.y = float1;
		}
	}

	public final void clampMax(float float1) {
		if (this.x > float1) {
			this.x = float1;
		}

		if (this.y > float1) {
			this.y = float1;
		}
	}

	public final void absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
	}

	public final void interpolate(Tuple2f tuple2f, Tuple2f tuple2f2, float float1) {
		this.x = (1.0F - float1) * tuple2f.x + float1 * tuple2f2.x;
		this.y = (1.0F - float1) * tuple2f.y + float1 * tuple2f2.y;
	}

	public final void interpolate(Tuple2f tuple2f, float float1) {
		this.x = (1.0F - float1) * this.x + float1 * tuple2f.x;
		this.y = (1.0F - float1) * this.y + float1 * tuple2f.y;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}
	}

	public final float getX() {
		return this.x;
	}

	public final void setX(float float1) {
		this.x = float1;
	}

	public final float getY() {
		return this.y;
	}

	public final void setY(float float1) {
		this.y = float1;
	}
}
