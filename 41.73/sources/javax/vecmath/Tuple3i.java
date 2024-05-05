package javax.vecmath;

import java.io.Serializable;


public abstract class Tuple3i implements Serializable,Cloneable {
	static final long serialVersionUID = -732740491767276200L;
	public int x;
	public int y;
	public int z;

	public Tuple3i(int int1, int int2, int int3) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
	}

	public Tuple3i(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
		this.z = intArray[2];
	}

	public Tuple3i(Tuple3i tuple3i) {
		this.x = tuple3i.x;
		this.y = tuple3i.y;
		this.z = tuple3i.z;
	}

	public Tuple3i() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public final void set(int int1, int int2, int int3) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
	}

	public final void set(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
		this.z = intArray[2];
	}

	public final void set(Tuple3i tuple3i) {
		this.x = tuple3i.x;
		this.y = tuple3i.y;
		this.z = tuple3i.z;
	}

	public final void get(int[] intArray) {
		intArray[0] = this.x;
		intArray[1] = this.y;
		intArray[2] = this.z;
	}

	public final void get(Tuple3i tuple3i) {
		tuple3i.x = this.x;
		tuple3i.y = this.y;
		tuple3i.z = this.z;
	}

	public final void add(Tuple3i tuple3i, Tuple3i tuple3i2) {
		this.x = tuple3i.x + tuple3i2.x;
		this.y = tuple3i.y + tuple3i2.y;
		this.z = tuple3i.z + tuple3i2.z;
	}

	public final void add(Tuple3i tuple3i) {
		this.x += tuple3i.x;
		this.y += tuple3i.y;
		this.z += tuple3i.z;
	}

	public final void sub(Tuple3i tuple3i, Tuple3i tuple3i2) {
		this.x = tuple3i.x - tuple3i2.x;
		this.y = tuple3i.y - tuple3i2.y;
		this.z = tuple3i.z - tuple3i2.z;
	}

	public final void sub(Tuple3i tuple3i) {
		this.x -= tuple3i.x;
		this.y -= tuple3i.y;
		this.z -= tuple3i.z;
	}

	public final void negate(Tuple3i tuple3i) {
		this.x = -tuple3i.x;
		this.y = -tuple3i.y;
		this.z = -tuple3i.z;
	}

	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
	}

	public final void scale(int int1, Tuple3i tuple3i) {
		this.x = int1 * tuple3i.x;
		this.y = int1 * tuple3i.y;
		this.z = int1 * tuple3i.z;
	}

	public final void scale(int int1) {
		this.x *= int1;
		this.y *= int1;
		this.z *= int1;
	}

	public final void scaleAdd(int int1, Tuple3i tuple3i, Tuple3i tuple3i2) {
		this.x = int1 * tuple3i.x + tuple3i2.x;
		this.y = int1 * tuple3i.y + tuple3i2.y;
		this.z = int1 * tuple3i.z + tuple3i2.z;
	}

	public final void scaleAdd(int int1, Tuple3i tuple3i) {
		this.x = int1 * this.x + tuple3i.x;
		this.y = int1 * this.y + tuple3i.y;
		this.z = int1 * this.z + tuple3i.z;
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public boolean equals(Object object) {
		try {
			Tuple3i tuple3i = (Tuple3i)object;
			return this.x == tuple3i.x && this.y == tuple3i.y && this.z == tuple3i.z;
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
		return (int)(long1 ^ long1 >> 32);
	}

	public final void clamp(int int1, int int2, Tuple3i tuple3i) {
		if (tuple3i.x > int2) {
			this.x = int2;
		} else if (tuple3i.x < int1) {
			this.x = int1;
		} else {
			this.x = tuple3i.x;
		}

		if (tuple3i.y > int2) {
			this.y = int2;
		} else if (tuple3i.y < int1) {
			this.y = int1;
		} else {
			this.y = tuple3i.y;
		}

		if (tuple3i.z > int2) {
			this.z = int2;
		} else if (tuple3i.z < int1) {
			this.z = int1;
		} else {
			this.z = tuple3i.z;
		}
	}

	public final void clampMin(int int1, Tuple3i tuple3i) {
		if (tuple3i.x < int1) {
			this.x = int1;
		} else {
			this.x = tuple3i.x;
		}

		if (tuple3i.y < int1) {
			this.y = int1;
		} else {
			this.y = tuple3i.y;
		}

		if (tuple3i.z < int1) {
			this.z = int1;
		} else {
			this.z = tuple3i.z;
		}
	}

	public final void clampMax(int int1, Tuple3i tuple3i) {
		if (tuple3i.x > int1) {
			this.x = int1;
		} else {
			this.x = tuple3i.x;
		}

		if (tuple3i.y > int1) {
			this.y = int1;
		} else {
			this.y = tuple3i.y;
		}

		if (tuple3i.z > int1) {
			this.z = int1;
		} else {
			this.z = tuple3i.z;
		}
	}

	public final void absolute(Tuple3i tuple3i) {
		this.x = Math.abs(tuple3i.x);
		this.y = Math.abs(tuple3i.y);
		this.z = Math.abs(tuple3i.z);
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
	}

	public final void absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
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
}
