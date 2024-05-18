package javax.vecmath;

import java.io.Serializable;


public abstract class Tuple2d implements Serializable,Cloneable {
	static final long serialVersionUID = 6205762482756093838L;
	public double x;
	public double y;

	public Tuple2d(double double1, double double2) {
		this.x = double1;
		this.y = double2;
	}

	public Tuple2d(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
	}

	public Tuple2d(Tuple2d tuple2d) {
		this.x = tuple2d.x;
		this.y = tuple2d.y;
	}

	public Tuple2d(Tuple2f tuple2f) {
		this.x = (double)tuple2f.x;
		this.y = (double)tuple2f.y;
	}

	public Tuple2d() {
		this.x = 0.0;
		this.y = 0.0;
	}

	public final void set(double double1, double double2) {
		this.x = double1;
		this.y = double2;
	}

	public final void set(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
	}

	public final void set(Tuple2d tuple2d) {
		this.x = tuple2d.x;
		this.y = tuple2d.y;
	}

	public final void set(Tuple2f tuple2f) {
		this.x = (double)tuple2f.x;
		this.y = (double)tuple2f.y;
	}

	public final void get(double[] doubleArray) {
		doubleArray[0] = this.x;
		doubleArray[1] = this.y;
	}

	public final void add(Tuple2d tuple2d, Tuple2d tuple2d2) {
		this.x = tuple2d.x + tuple2d2.x;
		this.y = tuple2d.y + tuple2d2.y;
	}

	public final void add(Tuple2d tuple2d) {
		this.x += tuple2d.x;
		this.y += tuple2d.y;
	}

	public final void sub(Tuple2d tuple2d, Tuple2d tuple2d2) {
		this.x = tuple2d.x - tuple2d2.x;
		this.y = tuple2d.y - tuple2d2.y;
	}

	public final void sub(Tuple2d tuple2d) {
		this.x -= tuple2d.x;
		this.y -= tuple2d.y;
	}

	public final void negate(Tuple2d tuple2d) {
		this.x = -tuple2d.x;
		this.y = -tuple2d.y;
	}

	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
	}

	public final void scale(double double1, Tuple2d tuple2d) {
		this.x = double1 * tuple2d.x;
		this.y = double1 * tuple2d.y;
	}

	public final void scale(double double1) {
		this.x *= double1;
		this.y *= double1;
	}

	public final void scaleAdd(double double1, Tuple2d tuple2d, Tuple2d tuple2d2) {
		this.x = double1 * tuple2d.x + tuple2d2.x;
		this.y = double1 * tuple2d.y + tuple2d2.y;
	}

	public final void scaleAdd(double double1, Tuple2d tuple2d) {
		this.x = double1 * this.x + tuple2d.x;
		this.y = double1 * this.y + tuple2d.y;
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.x);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.y);
		return (int)(long1 ^ long1 >> 32);
	}

	public boolean equals(Tuple2d tuple2d) {
		try {
			return this.x == tuple2d.x && this.y == tuple2d.y;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Tuple2d tuple2d = (Tuple2d)object;
			return this.x == tuple2d.x && this.y == tuple2d.y;
		} catch (NullPointerException nullPointerException) {
			return false;
		} catch (ClassCastException classCastException) {
			return false;
		}
	}

	public boolean epsilonEquals(Tuple2d tuple2d, double double1) {
		double double2 = this.x - tuple2d.x;
		if (Double.isNaN(double2)) {
			return false;
		} else if ((double2 < 0.0 ? -double2 : double2) > double1) {
			return false;
		} else {
			double2 = this.y - tuple2d.y;
			if (Double.isNaN(double2)) {
				return false;
			} else {
				return !((double2 < 0.0 ? -double2 : double2) > double1);
			}
		}
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}

	public final void clamp(double double1, double double2, Tuple2d tuple2d) {
		if (tuple2d.x > double2) {
			this.x = double2;
		} else if (tuple2d.x < double1) {
			this.x = double1;
		} else {
			this.x = tuple2d.x;
		}

		if (tuple2d.y > double2) {
			this.y = double2;
		} else if (tuple2d.y < double1) {
			this.y = double1;
		} else {
			this.y = tuple2d.y;
		}
	}

	public final void clampMin(double double1, Tuple2d tuple2d) {
		if (tuple2d.x < double1) {
			this.x = double1;
		} else {
			this.x = tuple2d.x;
		}

		if (tuple2d.y < double1) {
			this.y = double1;
		} else {
			this.y = tuple2d.y;
		}
	}

	public final void clampMax(double double1, Tuple2d tuple2d) {
		if (tuple2d.x > double1) {
			this.x = double1;
		} else {
			this.x = tuple2d.x;
		}

		if (tuple2d.y > double1) {
			this.y = double1;
		} else {
			this.y = tuple2d.y;
		}
	}

	public final void absolute(Tuple2d tuple2d) {
		this.x = Math.abs(tuple2d.x);
		this.y = Math.abs(tuple2d.y);
	}

	public final void clamp(double double1, double double2) {
		if (this.x > double2) {
			this.x = double2;
		} else if (this.x < double1) {
			this.x = double1;
		}

		if (this.y > double2) {
			this.y = double2;
		} else if (this.y < double1) {
			this.y = double1;
		}
	}

	public final void clampMin(double double1) {
		if (this.x < double1) {
			this.x = double1;
		}

		if (this.y < double1) {
			this.y = double1;
		}
	}

	public final void clampMax(double double1) {
		if (this.x > double1) {
			this.x = double1;
		}

		if (this.y > double1) {
			this.y = double1;
		}
	}

	public final void absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
	}

	public final void interpolate(Tuple2d tuple2d, Tuple2d tuple2d2, double double1) {
		this.x = (1.0 - double1) * tuple2d.x + double1 * tuple2d2.x;
		this.y = (1.0 - double1) * tuple2d.y + double1 * tuple2d2.y;
	}

	public final void interpolate(Tuple2d tuple2d, double double1) {
		this.x = (1.0 - double1) * this.x + double1 * tuple2d.x;
		this.y = (1.0 - double1) * this.y + double1 * tuple2d.y;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}
	}

	public final double getX() {
		return this.x;
	}

	public final void setX(double double1) {
		this.x = double1;
	}

	public final double getY() {
		return this.y;
	}

	public final void setY(double double1) {
		this.y = double1;
	}
}
