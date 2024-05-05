package javax.vecmath;

import java.io.Serializable;


public abstract class Tuple3d implements Serializable,Cloneable {
	static final long serialVersionUID = 5542096614926168415L;
	public double x;
	public double y;
	public double z;

	public Tuple3d(double double1, double double2, double double3) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
	}

	public Tuple3d(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
		this.z = doubleArray[2];
	}

	public Tuple3d(Tuple3d tuple3d) {
		this.x = tuple3d.x;
		this.y = tuple3d.y;
		this.z = tuple3d.z;
	}

	public Tuple3d(Tuple3f tuple3f) {
		this.x = (double)tuple3f.x;
		this.y = (double)tuple3f.y;
		this.z = (double)tuple3f.z;
	}

	public Tuple3d() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
	}

	public final void set(double double1, double double2, double double3) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
	}

	public final void set(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
		this.z = doubleArray[2];
	}

	public final void set(Tuple3d tuple3d) {
		this.x = tuple3d.x;
		this.y = tuple3d.y;
		this.z = tuple3d.z;
	}

	public final void set(Tuple3f tuple3f) {
		this.x = (double)tuple3f.x;
		this.y = (double)tuple3f.y;
		this.z = (double)tuple3f.z;
	}

	public final void get(double[] doubleArray) {
		doubleArray[0] = this.x;
		doubleArray[1] = this.y;
		doubleArray[2] = this.z;
	}

	public final void get(Tuple3d tuple3d) {
		tuple3d.x = this.x;
		tuple3d.y = this.y;
		tuple3d.z = this.z;
	}

	public final void add(Tuple3d tuple3d, Tuple3d tuple3d2) {
		this.x = tuple3d.x + tuple3d2.x;
		this.y = tuple3d.y + tuple3d2.y;
		this.z = tuple3d.z + tuple3d2.z;
	}

	public final void add(Tuple3d tuple3d) {
		this.x += tuple3d.x;
		this.y += tuple3d.y;
		this.z += tuple3d.z;
	}

	public final void sub(Tuple3d tuple3d, Tuple3d tuple3d2) {
		this.x = tuple3d.x - tuple3d2.x;
		this.y = tuple3d.y - tuple3d2.y;
		this.z = tuple3d.z - tuple3d2.z;
	}

	public final void sub(Tuple3d tuple3d) {
		this.x -= tuple3d.x;
		this.y -= tuple3d.y;
		this.z -= tuple3d.z;
	}

	public final void negate(Tuple3d tuple3d) {
		this.x = -tuple3d.x;
		this.y = -tuple3d.y;
		this.z = -tuple3d.z;
	}

	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
	}

	public final void scale(double double1, Tuple3d tuple3d) {
		this.x = double1 * tuple3d.x;
		this.y = double1 * tuple3d.y;
		this.z = double1 * tuple3d.z;
	}

	public final void scale(double double1) {
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
	}

	public final void scaleAdd(double double1, Tuple3d tuple3d, Tuple3d tuple3d2) {
		this.x = double1 * tuple3d.x + tuple3d2.x;
		this.y = double1 * tuple3d.y + tuple3d2.y;
		this.z = double1 * tuple3d.z + tuple3d2.z;
	}

	public final void scaleAdd(double double1, Tuple3f tuple3f) {
		this.scaleAdd(double1, (Tuple3d)(new Point3d(tuple3f)));
	}

	public final void scaleAdd(double double1, Tuple3d tuple3d) {
		this.x = double1 * this.x + tuple3d.x;
		this.y = double1 * this.y + tuple3d.y;
		this.z = double1 * this.z + tuple3d.z;
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.x);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.y);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.z);
		return (int)(long1 ^ long1 >> 32);
	}

	public boolean equals(Tuple3d tuple3d) {
		try {
			return this.x == tuple3d.x && this.y == tuple3d.y && this.z == tuple3d.z;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Tuple3d tuple3d = (Tuple3d)object;
			return this.x == tuple3d.x && this.y == tuple3d.y && this.z == tuple3d.z;
		} catch (ClassCastException classCastException) {
			return false;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean epsilonEquals(Tuple3d tuple3d, double double1) {
		double double2 = this.x - tuple3d.x;
		if (Double.isNaN(double2)) {
			return false;
		} else if ((double2 < 0.0 ? -double2 : double2) > double1) {
			return false;
		} else {
			double2 = this.y - tuple3d.y;
			if (Double.isNaN(double2)) {
				return false;
			} else if ((double2 < 0.0 ? -double2 : double2) > double1) {
				return false;
			} else {
				double2 = this.z - tuple3d.z;
				if (Double.isNaN(double2)) {
					return false;
				} else {
					return !((double2 < 0.0 ? -double2 : double2) > double1);
				}
			}
		}
	}

	public final void clamp(float float1, float float2, Tuple3d tuple3d) {
		this.clamp((double)float1, (double)float2, tuple3d);
	}

	public final void clamp(double double1, double double2, Tuple3d tuple3d) {
		if (tuple3d.x > double2) {
			this.x = double2;
		} else if (tuple3d.x < double1) {
			this.x = double1;
		} else {
			this.x = tuple3d.x;
		}

		if (tuple3d.y > double2) {
			this.y = double2;
		} else if (tuple3d.y < double1) {
			this.y = double1;
		} else {
			this.y = tuple3d.y;
		}

		if (tuple3d.z > double2) {
			this.z = double2;
		} else if (tuple3d.z < double1) {
			this.z = double1;
		} else {
			this.z = tuple3d.z;
		}
	}

	public final void clampMin(float float1, Tuple3d tuple3d) {
		this.clampMin((double)float1, tuple3d);
	}

	public final void clampMin(double double1, Tuple3d tuple3d) {
		if (tuple3d.x < double1) {
			this.x = double1;
		} else {
			this.x = tuple3d.x;
		}

		if (tuple3d.y < double1) {
			this.y = double1;
		} else {
			this.y = tuple3d.y;
		}

		if (tuple3d.z < double1) {
			this.z = double1;
		} else {
			this.z = tuple3d.z;
		}
	}

	public final void clampMax(float float1, Tuple3d tuple3d) {
		this.clampMax((double)float1, tuple3d);
	}

	public final void clampMax(double double1, Tuple3d tuple3d) {
		if (tuple3d.x > double1) {
			this.x = double1;
		} else {
			this.x = tuple3d.x;
		}

		if (tuple3d.y > double1) {
			this.y = double1;
		} else {
			this.y = tuple3d.y;
		}

		if (tuple3d.z > double1) {
			this.z = double1;
		} else {
			this.z = tuple3d.z;
		}
	}

	public final void absolute(Tuple3d tuple3d) {
		this.x = Math.abs(tuple3d.x);
		this.y = Math.abs(tuple3d.y);
		this.z = Math.abs(tuple3d.z);
	}

	public final void clamp(float float1, float float2) {
		this.clamp((double)float1, (double)float2);
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

		if (this.z > double2) {
			this.z = double2;
		} else if (this.z < double1) {
			this.z = double1;
		}
	}

	public final void clampMin(float float1) {
		this.clampMin((double)float1);
	}

	public final void clampMin(double double1) {
		if (this.x < double1) {
			this.x = double1;
		}

		if (this.y < double1) {
			this.y = double1;
		}

		if (this.z < double1) {
			this.z = double1;
		}
	}

	public final void clampMax(float float1) {
		this.clampMax((double)float1);
	}

	public final void clampMax(double double1) {
		if (this.x > double1) {
			this.x = double1;
		}

		if (this.y > double1) {
			this.y = double1;
		}

		if (this.z > double1) {
			this.z = double1;
		}
	}

	public final void absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
	}

	public final void interpolate(Tuple3d tuple3d, Tuple3d tuple3d2, float float1) {
		this.interpolate(tuple3d, tuple3d2, (double)float1);
	}

	public final void interpolate(Tuple3d tuple3d, Tuple3d tuple3d2, double double1) {
		this.x = (1.0 - double1) * tuple3d.x + double1 * tuple3d2.x;
		this.y = (1.0 - double1) * tuple3d.y + double1 * tuple3d2.y;
		this.z = (1.0 - double1) * tuple3d.z + double1 * tuple3d2.z;
	}

	public final void interpolate(Tuple3d tuple3d, float float1) {
		this.interpolate(tuple3d, (double)float1);
	}

	public final void interpolate(Tuple3d tuple3d, double double1) {
		this.x = (1.0 - double1) * this.x + double1 * tuple3d.x;
		this.y = (1.0 - double1) * this.y + double1 * tuple3d.y;
		this.z = (1.0 - double1) * this.z + double1 * tuple3d.z;
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

	public final double getZ() {
		return this.z;
	}

	public final void setZ(double double1) {
		this.z = double1;
	}
}
