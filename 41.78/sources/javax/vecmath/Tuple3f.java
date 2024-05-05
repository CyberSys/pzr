package javax.vecmath;

import java.io.Serializable;


public abstract class Tuple3f implements Serializable,Cloneable {
	static final long serialVersionUID = 5019834619484343712L;
	public float x;
	public float y;
	public float z;

	public Tuple3f(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
	}

	public Tuple3f(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
		this.z = floatArray[2];
	}

	public Tuple3f(Tuple3f tuple3f) {
		this.x = tuple3f.x;
		this.y = tuple3f.y;
		this.z = tuple3f.z;
	}

	public Tuple3f(Tuple3d tuple3d) {
		this.x = (float)tuple3d.x;
		this.y = (float)tuple3d.y;
		this.z = (float)tuple3d.z;
	}

	public Tuple3f() {
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public final void set(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
	}

	public final void set(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
		this.z = floatArray[2];
	}

	public final void set(Tuple3f tuple3f) {
		this.x = tuple3f.x;
		this.y = tuple3f.y;
		this.z = tuple3f.z;
	}

	public final void set(Tuple3d tuple3d) {
		this.x = (float)tuple3d.x;
		this.y = (float)tuple3d.y;
		this.z = (float)tuple3d.z;
	}

	public final void get(float[] floatArray) {
		floatArray[0] = this.x;
		floatArray[1] = this.y;
		floatArray[2] = this.z;
	}

	public final void get(Tuple3f tuple3f) {
		tuple3f.x = this.x;
		tuple3f.y = this.y;
		tuple3f.z = this.z;
	}

	public final void add(Tuple3f tuple3f, Tuple3f tuple3f2) {
		this.x = tuple3f.x + tuple3f2.x;
		this.y = tuple3f.y + tuple3f2.y;
		this.z = tuple3f.z + tuple3f2.z;
	}

	public final void add(Tuple3f tuple3f) {
		this.x += tuple3f.x;
		this.y += tuple3f.y;
		this.z += tuple3f.z;
	}

	public final void sub(Tuple3f tuple3f, Tuple3f tuple3f2) {
		this.x = tuple3f.x - tuple3f2.x;
		this.y = tuple3f.y - tuple3f2.y;
		this.z = tuple3f.z - tuple3f2.z;
	}

	public final void sub(Tuple3f tuple3f) {
		this.x -= tuple3f.x;
		this.y -= tuple3f.y;
		this.z -= tuple3f.z;
	}

	public final void negate(Tuple3f tuple3f) {
		this.x = -tuple3f.x;
		this.y = -tuple3f.y;
		this.z = -tuple3f.z;
	}

	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
	}

	public final void scale(float float1, Tuple3f tuple3f) {
		this.x = float1 * tuple3f.x;
		this.y = float1 * tuple3f.y;
		this.z = float1 * tuple3f.z;
	}

	public final void scale(float float1) {
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
	}

	public final void scaleAdd(float float1, Tuple3f tuple3f, Tuple3f tuple3f2) {
		this.x = float1 * tuple3f.x + tuple3f2.x;
		this.y = float1 * tuple3f.y + tuple3f2.y;
		this.z = float1 * tuple3f.z + tuple3f2.z;
	}

	public final void scaleAdd(float float1, Tuple3f tuple3f) {
		this.x = float1 * this.x + tuple3f.x;
		this.y = float1 * this.y + tuple3f.y;
		this.z = float1 * this.z + tuple3f.z;
	}

	public boolean equals(Tuple3f tuple3f) {
		try {
			return this.x == tuple3f.x && this.y == tuple3f.y && this.z == tuple3f.z;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Tuple3f tuple3f = (Tuple3f)object;
			return this.x == tuple3f.x && this.y == tuple3f.y && this.z == tuple3f.z;
		} catch (NullPointerException nullPointerException) {
			return false;
		} catch (ClassCastException classCastException) {
			return false;
		}
	}

	public boolean epsilonEquals(Tuple3f tuple3f, float float1) {
		float float2 = this.x - tuple3f.x;
		if (Float.isNaN(float2)) {
			return false;
		} else if ((float2 < 0.0F ? -float2 : float2) > float1) {
			return false;
		} else {
			float2 = this.y - tuple3f.y;
			if (Float.isNaN(float2)) {
				return false;
			} else if ((float2 < 0.0F ? -float2 : float2) > float1) {
				return false;
			} else {
				float2 = this.z - tuple3f.z;
				if (Float.isNaN(float2)) {
					return false;
				} else {
					return !((float2 < 0.0F ? -float2 : float2) > float1);
				}
			}
		}
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.x);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.y);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.z);
		return (int)(long1 ^ long1 >> 32);
	}

	public final void clamp(float float1, float float2, Tuple3f tuple3f) {
		if (tuple3f.x > float2) {
			this.x = float2;
		} else if (tuple3f.x < float1) {
			this.x = float1;
		} else {
			this.x = tuple3f.x;
		}

		if (tuple3f.y > float2) {
			this.y = float2;
		} else if (tuple3f.y < float1) {
			this.y = float1;
		} else {
			this.y = tuple3f.y;
		}

		if (tuple3f.z > float2) {
			this.z = float2;
		} else if (tuple3f.z < float1) {
			this.z = float1;
		} else {
			this.z = tuple3f.z;
		}
	}

	public final void clampMin(float float1, Tuple3f tuple3f) {
		if (tuple3f.x < float1) {
			this.x = float1;
		} else {
			this.x = tuple3f.x;
		}

		if (tuple3f.y < float1) {
			this.y = float1;
		} else {
			this.y = tuple3f.y;
		}

		if (tuple3f.z < float1) {
			this.z = float1;
		} else {
			this.z = tuple3f.z;
		}
	}

	public final void clampMax(float float1, Tuple3f tuple3f) {
		if (tuple3f.x > float1) {
			this.x = float1;
		} else {
			this.x = tuple3f.x;
		}

		if (tuple3f.y > float1) {
			this.y = float1;
		} else {
			this.y = tuple3f.y;
		}

		if (tuple3f.z > float1) {
			this.z = float1;
		} else {
			this.z = tuple3f.z;
		}
	}

	public final void absolute(Tuple3f tuple3f) {
		this.x = Math.abs(tuple3f.x);
		this.y = Math.abs(tuple3f.y);
		this.z = Math.abs(tuple3f.z);
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

		if (this.z > float2) {
			this.z = float2;
		} else if (this.z < float1) {
			this.z = float1;
		}
	}

	public final void clampMin(float float1) {
		if (this.x < float1) {
			this.x = float1;
		}

		if (this.y < float1) {
			this.y = float1;
		}

		if (this.z < float1) {
			this.z = float1;
		}
	}

	public final void clampMax(float float1) {
		if (this.x > float1) {
			this.x = float1;
		}

		if (this.y > float1) {
			this.y = float1;
		}

		if (this.z > float1) {
			this.z = float1;
		}
	}

	public final void absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
	}

	public final void interpolate(Tuple3f tuple3f, Tuple3f tuple3f2, float float1) {
		this.x = (1.0F - float1) * tuple3f.x + float1 * tuple3f2.x;
		this.y = (1.0F - float1) * tuple3f.y + float1 * tuple3f2.y;
		this.z = (1.0F - float1) * tuple3f.z + float1 * tuple3f2.z;
	}

	public final void interpolate(Tuple3f tuple3f, float float1) {
		this.x = (1.0F - float1) * this.x + float1 * tuple3f.x;
		this.y = (1.0F - float1) * this.y + float1 * tuple3f.y;
		this.z = (1.0F - float1) * this.z + float1 * tuple3f.z;
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

	public final float getZ() {
		return this.z;
	}

	public final void setZ(float float1) {
		this.z = float1;
	}
}
