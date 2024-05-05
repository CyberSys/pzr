package javax.vecmath;

import java.io.Serializable;


public abstract class Tuple4i implements Serializable,Cloneable {
	static final long serialVersionUID = 8064614250942616720L;
	public int x;
	public int y;
	public int z;
	public int w;

	public Tuple4i(int int1, int int2, int int3, int int4) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.w = int4;
	}

	public Tuple4i(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
		this.z = intArray[2];
		this.w = intArray[3];
	}

	public Tuple4i(Tuple4i tuple4i) {
		this.x = tuple4i.x;
		this.y = tuple4i.y;
		this.z = tuple4i.z;
		this.w = tuple4i.w;
	}

	public Tuple4i() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
	}

	public final void set(int int1, int int2, int int3, int int4) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.w = int4;
	}

	public final void set(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
		this.z = intArray[2];
		this.w = intArray[3];
	}

	public final void set(Tuple4i tuple4i) {
		this.x = tuple4i.x;
		this.y = tuple4i.y;
		this.z = tuple4i.z;
		this.w = tuple4i.w;
	}

	public final void get(int[] intArray) {
		intArray[0] = this.x;
		intArray[1] = this.y;
		intArray[2] = this.z;
		intArray[3] = this.w;
	}

	public final void get(Tuple4i tuple4i) {
		tuple4i.x = this.x;
		tuple4i.y = this.y;
		tuple4i.z = this.z;
		tuple4i.w = this.w;
	}

	public final void add(Tuple4i tuple4i, Tuple4i tuple4i2) {
		this.x = tuple4i.x + tuple4i2.x;
		this.y = tuple4i.y + tuple4i2.y;
		this.z = tuple4i.z + tuple4i2.z;
		this.w = tuple4i.w + tuple4i2.w;
	}

	public final void add(Tuple4i tuple4i) {
		this.x += tuple4i.x;
		this.y += tuple4i.y;
		this.z += tuple4i.z;
		this.w += tuple4i.w;
	}

	public final void sub(Tuple4i tuple4i, Tuple4i tuple4i2) {
		this.x = tuple4i.x - tuple4i2.x;
		this.y = tuple4i.y - tuple4i2.y;
		this.z = tuple4i.z - tuple4i2.z;
		this.w = tuple4i.w - tuple4i2.w;
	}

	public final void sub(Tuple4i tuple4i) {
		this.x -= tuple4i.x;
		this.y -= tuple4i.y;
		this.z -= tuple4i.z;
		this.w -= tuple4i.w;
	}

	public final void negate(Tuple4i tuple4i) {
		this.x = -tuple4i.x;
		this.y = -tuple4i.y;
		this.z = -tuple4i.z;
		this.w = -tuple4i.w;
	}

	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
	}

	public final void scale(int int1, Tuple4i tuple4i) {
		this.x = int1 * tuple4i.x;
		this.y = int1 * tuple4i.y;
		this.z = int1 * tuple4i.z;
		this.w = int1 * tuple4i.w;
	}

	public final void scale(int int1) {
		this.x *= int1;
		this.y *= int1;
		this.z *= int1;
		this.w *= int1;
	}

	public final void scaleAdd(int int1, Tuple4i tuple4i, Tuple4i tuple4i2) {
		this.x = int1 * tuple4i.x + tuple4i2.x;
		this.y = int1 * tuple4i.y + tuple4i2.y;
		this.z = int1 * tuple4i.z + tuple4i2.z;
		this.w = int1 * tuple4i.w + tuple4i2.w;
	}

	public final void scaleAdd(int int1, Tuple4i tuple4i) {
		this.x = int1 * this.x + tuple4i.x;
		this.y = int1 * this.y + tuple4i.y;
		this.z = int1 * this.z + tuple4i.z;
		this.w = int1 * this.w + tuple4i.w;
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
	}

	public boolean equals(Object object) {
		try {
			Tuple4i tuple4i = (Tuple4i)object;
			return this.x == tuple4i.x && this.y == tuple4i.y && this.z == tuple4i.z && this.w == tuple4i.w;
		} catch (NullPointerException nullPointerException) {
			return false;
		} catch (ClassCastException classCastException) {
			return false;
		}
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + (long)this.x;
		long1 = 31L * long1 + (long)this.y;
		long1 = 31L * long1 + (long)this.z;
		long1 = 31L * long1 + (long)this.w;
		return (int)(long1 ^ long1 >> 32);
	}

	public final void clamp(int int1, int int2, Tuple4i tuple4i) {
		if (tuple4i.x > int2) {
			this.x = int2;
		} else if (tuple4i.x < int1) {
			this.x = int1;
		} else {
			this.x = tuple4i.x;
		}

		if (tuple4i.y > int2) {
			this.y = int2;
		} else if (tuple4i.y < int1) {
			this.y = int1;
		} else {
			this.y = tuple4i.y;
		}

		if (tuple4i.z > int2) {
			this.z = int2;
		} else if (tuple4i.z < int1) {
			this.z = int1;
		} else {
			this.z = tuple4i.z;
		}

		if (tuple4i.w > int2) {
			this.w = int2;
		} else if (tuple4i.w < int1) {
			this.w = int1;
		} else {
			this.w = tuple4i.w;
		}
	}

	public final void clampMin(int int1, Tuple4i tuple4i) {
		if (tuple4i.x < int1) {
			this.x = int1;
		} else {
			this.x = tuple4i.x;
		}

		if (tuple4i.y < int1) {
			this.y = int1;
		} else {
			this.y = tuple4i.y;
		}

		if (tuple4i.z < int1) {
			this.z = int1;
		} else {
			this.z = tuple4i.z;
		}

		if (tuple4i.w < int1) {
			this.w = int1;
		} else {
			this.w = tuple4i.w;
		}
	}

	public final void clampMax(int int1, Tuple4i tuple4i) {
		if (tuple4i.x > int1) {
			this.x = int1;
		} else {
			this.x = tuple4i.x;
		}

		if (tuple4i.y > int1) {
			this.y = int1;
		} else {
			this.y = tuple4i.y;
		}

		if (tuple4i.z > int1) {
			this.z = int1;
		} else {
			this.z = tuple4i.z;
		}

		if (tuple4i.w > int1) {
			this.w = int1;
		} else {
			this.w = tuple4i.z;
		}
	}

	public final void absolute(Tuple4i tuple4i) {
		this.x = Math.abs(tuple4i.x);
		this.y = Math.abs(tuple4i.y);
		this.z = Math.abs(tuple4i.z);
		this.w = Math.abs(tuple4i.w);
	}

	public final void clamp(int int1, int int2) {
		if (this.x > int2) {
			this.x = int2;
		} else if (this.x < int1) {
			this.x = int1;
		}

		if (this.y > int2) {
			this.y = int2;
		} else if (this.y < int1) {
			this.y = int1;
		}

		if (this.z > int2) {
			this.z = int2;
		} else if (this.z < int1) {
			this.z = int1;
		}

		if (this.w > int2) {
			this.w = int2;
		} else if (this.w < int1) {
			this.w = int1;
		}
	}

	public final void clampMin(int int1) {
		if (this.x < int1) {
			this.x = int1;
		}

		if (this.y < int1) {
			this.y = int1;
		}

		if (this.z < int1) {
			this.z = int1;
		}

		if (this.w < int1) {
			this.w = int1;
		}
	}

	public final void clampMax(int int1) {
		if (this.x > int1) {
			this.x = int1;
		}

		if (this.y > int1) {
			this.y = int1;
		}

		if (this.z > int1) {
			this.z = int1;
		}

		if (this.w > int1) {
			this.w = int1;
		}
	}

	public final void absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
		this.w = Math.abs(this.w);
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}
	}

	public final int getX() {
		return this.x;
	}

	public final void setX(int int1) {
		this.x = int1;
	}

	public final int getY() {
		return this.y;
	}

	public final void setY(int int1) {
		this.y = int1;
	}

	public final int getZ() {
		return this.z;
	}

	public final void setZ(int int1) {
		this.z = int1;
	}

	public final int getW() {
		return this.w;
	}

	public final void setW(int int1) {
		this.w = int1;
	}
}
