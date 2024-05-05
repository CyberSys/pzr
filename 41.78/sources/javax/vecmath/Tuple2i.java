package javax.vecmath;

import java.io.Serializable;


public abstract class Tuple2i implements Serializable,Cloneable {
	static final long serialVersionUID = -3555701650170169638L;
	public int x;
	public int y;

	public Tuple2i(int int1, int int2) {
		this.x = int1;
		this.y = int2;
	}

	public Tuple2i(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
	}

	public Tuple2i(Tuple2i tuple2i) {
		this.x = tuple2i.x;
		this.y = tuple2i.y;
	}

	public Tuple2i() {
		this.x = 0;
		this.y = 0;
	}

	public final void set(int int1, int int2) {
		this.x = int1;
		this.y = int2;
	}

	public final void set(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
	}

	public final void set(Tuple2i tuple2i) {
		this.x = tuple2i.x;
		this.y = tuple2i.y;
	}

	public final void get(int[] intArray) {
		intArray[0] = this.x;
		intArray[1] = this.y;
	}

	public final void get(Tuple2i tuple2i) {
		tuple2i.x = this.x;
		tuple2i.y = this.y;
	}

	public final void add(Tuple2i tuple2i, Tuple2i tuple2i2) {
		this.x = tuple2i.x + tuple2i2.x;
		this.y = tuple2i.y + tuple2i2.y;
	}

	public final void add(Tuple2i tuple2i) {
		this.x += tuple2i.x;
		this.y += tuple2i.y;
	}

	public final void sub(Tuple2i tuple2i, Tuple2i tuple2i2) {
		this.x = tuple2i.x - tuple2i2.x;
		this.y = tuple2i.y - tuple2i2.y;
	}

	public final void sub(Tuple2i tuple2i) {
		this.x -= tuple2i.x;
		this.y -= tuple2i.y;
	}

	public final void negate(Tuple2i tuple2i) {
		this.x = -tuple2i.x;
		this.y = -tuple2i.y;
	}

	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
	}

	public final void scale(int int1, Tuple2i tuple2i) {
		this.x = int1 * tuple2i.x;
		this.y = int1 * tuple2i.y;
	}

	public final void scale(int int1) {
		this.x *= int1;
		this.y *= int1;
	}

	public final void scaleAdd(int int1, Tuple2i tuple2i, Tuple2i tuple2i2) {
		this.x = int1 * tuple2i.x + tuple2i2.x;
		this.y = int1 * tuple2i.y + tuple2i2.y;
	}

	public final void scaleAdd(int int1, Tuple2i tuple2i) {
		this.x = int1 * this.x + tuple2i.x;
		this.y = int1 * this.y + tuple2i.y;
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}

	public boolean equals(Object object) {
		try {
			Tuple2i tuple2i = (Tuple2i)object;
			return this.x == tuple2i.x && this.y == tuple2i.y;
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
		return (int)(long1 ^ long1 >> 32);
	}

	public final void clamp(int int1, int int2, Tuple2i tuple2i) {
		if (tuple2i.x > int2) {
			this.x = int2;
		} else if (tuple2i.x < int1) {
			this.x = int1;
		} else {
			this.x = tuple2i.x;
		}

		if (tuple2i.y > int2) {
			this.y = int2;
		} else if (tuple2i.y < int1) {
			this.y = int1;
		} else {
			this.y = tuple2i.y;
		}
	}

	public final void clampMin(int int1, Tuple2i tuple2i) {
		if (tuple2i.x < int1) {
			this.x = int1;
		} else {
			this.x = tuple2i.x;
		}

		if (tuple2i.y < int1) {
			this.y = int1;
		} else {
			this.y = tuple2i.y;
		}
	}

	public final void clampMax(int int1, Tuple2i tuple2i) {
		if (tuple2i.x > int1) {
			this.x = int1;
		} else {
			this.x = tuple2i.x;
		}

		if (tuple2i.y > int1) {
			this.y = int1;
		} else {
			this.y = tuple2i.y;
		}
	}

	public final void absolute(Tuple2i tuple2i) {
		this.x = Math.abs(tuple2i.x);
		this.y = Math.abs(tuple2i.y);
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
	}

	public final void clampMin(int int1) {
		if (this.x < int1) {
			this.x = int1;
		}

		if (this.y < int1) {
			this.y = int1;
		}
	}

	public final void clampMax(int int1) {
		if (this.x > int1) {
			this.x = int1;
		}

		if (this.y > int1) {
			this.y = int1;
		}
	}

	public final void absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
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
}
