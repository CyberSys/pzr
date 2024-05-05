package javax.vecmath;

import java.io.Serializable;


public abstract class Tuple4f implements Serializable,Cloneable {
	static final long serialVersionUID = 7068460319248845763L;
	public float x;
	public float y;
	public float z;
	public float w;

	public Tuple4f(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
	}

	public Tuple4f(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
		this.z = floatArray[2];
		this.w = floatArray[3];
	}

	public Tuple4f(Tuple4f tuple4f) {
		this.x = tuple4f.x;
		this.y = tuple4f.y;
		this.z = tuple4f.z;
		this.w = tuple4f.w;
	}

	public Tuple4f(Tuple4d tuple4d) {
		this.x = (float)tuple4d.x;
		this.y = (float)tuple4d.y;
		this.z = (float)tuple4d.z;
		this.w = (float)tuple4d.w;
	}

	public Tuple4f() {
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
		this.w = 0.0F;
	}

	public final void set(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
	}

	public final void set(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
		this.z = floatArray[2];
		this.w = floatArray[3];
	}

	public final void set(Tuple4f tuple4f) {
		this.x = tuple4f.x;
		this.y = tuple4f.y;
		this.z = tuple4f.z;
		this.w = tuple4f.w;
	}

	public final void set(Tuple4d tuple4d) {
		this.x = (float)tuple4d.x;
		this.y = (float)tuple4d.y;
		this.z = (float)tuple4d.z;
		this.w = (float)tuple4d.w;
	}

	public final void get(float[] floatArray) {
		floatArray[0] = this.x;
		floatArray[1] = this.y;
		floatArray[2] = this.z;
		floatArray[3] = this.w;
	}

	public final void get(Tuple4f tuple4f) {
		tuple4f.x = this.x;
		tuple4f.y = this.y;
		tuple4f.z = this.z;
		tuple4f.w = this.w;
	}

	public final void add(Tuple4f tuple4f, Tuple4f tuple4f2) {
		this.x = tuple4f.x + tuple4f2.x;
		this.y = tuple4f.y + tuple4f2.y;
		this.z = tuple4f.z + tuple4f2.z;
		this.w = tuple4f.w + tuple4f2.w;
	}

	public final void add(Tuple4f tuple4f) {
		this.x += tuple4f.x;
		this.y += tuple4f.y;
		this.z += tuple4f.z;
		this.w += tuple4f.w;
	}

	public final void sub(Tuple4f tuple4f, Tuple4f tuple4f2) {
		this.x = tuple4f.x - tuple4f2.x;
		this.y = tuple4f.y - tuple4f2.y;
		this.z = tuple4f.z - tuple4f2.z;
		this.w = tuple4f.w - tuple4f2.w;
	}

	public final void sub(Tuple4f tuple4f) {
		this.x -= tuple4f.x;
		this.y -= tuple4f.y;
		this.z -= tuple4f.z;
		this.w -= tuple4f.w;
	}

	public final void negate(Tuple4f tuple4f) {
		this.x = -tuple4f.x;
		this.y = -tuple4f.y;
		this.z = -tuple4f.z;
		this.w = -tuple4f.w;
	}

	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
	}

	public final void scale(float float1, Tuple4f tuple4f) {
		this.x = float1 * tuple4f.x;
		this.y = float1 * tuple4f.y;
		this.z = float1 * tuple4f.z;
		this.w = float1 * tuple4f.w;
	}

	public final void scale(float float1) {
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		this.w *= float1;
	}

	public final void scaleAdd(float float1, Tuple4f tuple4f, Tuple4f tuple4f2) {
		this.x = float1 * tuple4f.x + tuple4f2.x;
		this.y = float1 * tuple4f.y + tuple4f2.y;
		this.z = float1 * tuple4f.z + tuple4f2.z;
		this.w = float1 * tuple4f.w + tuple4f2.w;
	}

	public final void scaleAdd(float float1, Tuple4f tuple4f) {
		this.x = float1 * this.x + tuple4f.x;
		this.y = float1 * this.y + tuple4f.y;
		this.z = float1 * this.z + tuple4f.z;
		this.w = float1 * this.w + tuple4f.w;
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
	}

	public boolean equals(Tuple4f tuple4f) {
		try {
			return this.x == tuple4f.x && this.y == tuple4f.y && this.z == tuple4f.z && this.w == tuple4f.w;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Tuple4f tuple4f = (Tuple4f)object;
			return this.x == tuple4f.x && this.y == tuple4f.y && this.z == tuple4f.z && this.w == tuple4f.w;
		} catch (NullPointerException nullPointerException) {
			return false;
		} catch (ClassCastException classCastException) {
			return false;
		}
	}

	public boolean epsilonEquals(Tuple4f tuple4f, float float1) {
		float float2 = this.x - tuple4f.x;
		if (Float.isNaN(float2)) {
			return false;
		} else if ((float2 < 0.0F ? -float2 : float2) > float1) {
			return false;
		} else {
			float2 = this.y - tuple4f.y;
			if (Float.isNaN(float2)) {
				return false;
			} else if ((float2 < 0.0F ? -float2 : float2) > float1) {
				return false;
			} else {
				float2 = this.z - tuple4f.z;
				if (Float.isNaN(float2)) {
					return false;
				} else if ((float2 < 0.0F ? -float2 : float2) > float1) {
					return false;
				} else {
					float2 = this.w - tuple4f.w;
					if (Float.isNaN(float2)) {
						return false;
					} else {
						return !((float2 < 0.0F ? -float2 : float2) > float1);
					}
				}
			}
		}
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.x);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.y);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.z);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.w);
		return (int)(long1 ^ long1 >> 32);
	}

	public final void clamp(float float1, float float2, Tuple4f tuple4f) {
		if (tuple4f.x > float2) {
			this.x = float2;
		} else if (tuple4f.x < float1) {
			this.x = float1;
		} else {
			this.x = tuple4f.x;
		}

		if (tuple4f.y > float2) {
			this.y = float2;
		} else if (tuple4f.y < float1) {
			this.y = float1;
		} else {
			this.y = tuple4f.y;
		}

		if (tuple4f.z > float2) {
			this.z = float2;
		} else if (tuple4f.z < float1) {
			this.z = float1;
		} else {
			this.z = tuple4f.z;
		}

		if (tuple4f.w > float2) {
			this.w = float2;
		} else if (tuple4f.w < float1) {
			this.w = float1;
		} else {
			this.w = tuple4f.w;
		}
	}

	public final void clampMin(float float1, Tuple4f tuple4f) {
		if (tuple4f.x < float1) {
			this.x = float1;
		} else {
			this.x = tuple4f.x;
		}

		if (tuple4f.y < float1) {
			this.y = float1;
		} else {
			this.y = tuple4f.y;
		}

		if (tuple4f.z < float1) {
			this.z = float1;
		} else {
			this.z = tuple4f.z;
		}

		if (tuple4f.w < float1) {
			this.w = float1;
		} else {
			this.w = tuple4f.w;
		}
	}

	public final void clampMax(float float1, Tuple4f tuple4f) {
		if (tuple4f.x > float1) {
			this.x = float1;
		} else {
			this.x = tuple4f.x;
		}

		if (tuple4f.y > float1) {
			this.y = float1;
		} else {
			this.y = tuple4f.y;
		}

		if (tuple4f.z > float1) {
			this.z = float1;
		} else {
			this.z = tuple4f.z;
		}

		if (tuple4f.w > float1) {
			this.w = float1;
		} else {
			this.w = tuple4f.z;
		}
	}

	public final void absolute(Tuple4f tuple4f) {
		this.x = Math.abs(tuple4f.x);
		this.y = Math.abs(tuple4f.y);
		this.z = Math.abs(tuple4f.z);
		this.w = Math.abs(tuple4f.w);
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

		if (this.w > float2) {
			this.w = float2;
		} else if (this.w < float1) {
			this.w = float1;
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

		if (this.w < float1) {
			this.w = float1;
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

		if (this.w > float1) {
			this.w = float1;
		}
	}

	public final void absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
		this.w = Math.abs(this.w);
	}

	public void interpolate(Tuple4f tuple4f, Tuple4f tuple4f2, float float1) {
		this.x = (1.0F - float1) * tuple4f.x + float1 * tuple4f2.x;
		this.y = (1.0F - float1) * tuple4f.y + float1 * tuple4f2.y;
		this.z = (1.0F - float1) * tuple4f.z + float1 * tuple4f2.z;
		this.w = (1.0F - float1) * tuple4f.w + float1 * tuple4f2.w;
	}

	public void interpolate(Tuple4f tuple4f, float float1) {
		this.x = (1.0F - float1) * this.x + float1 * tuple4f.x;
		this.y = (1.0F - float1) * this.y + float1 * tuple4f.y;
		this.z = (1.0F - float1) * this.z + float1 * tuple4f.z;
		this.w = (1.0F - float1) * this.w + float1 * tuple4f.w;
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

	public final float getW() {
		return this.w;
	}

	public final void setW(float float1) {
		this.w = float1;
	}
}
