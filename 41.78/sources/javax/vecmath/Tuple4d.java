package javax.vecmath;

import java.io.Serializable;


public abstract class Tuple4d implements Serializable,Cloneable {
	static final long serialVersionUID = -4748953690425311052L;
	public double x;
	public double y;
	public double z;
	public double w;

	public Tuple4d(double double1, double double2, double double3, double double4) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		this.w = double4;
	}

	public Tuple4d(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
		this.z = doubleArray[2];
		this.w = doubleArray[3];
	}

	public Tuple4d(Tuple4d tuple4d) {
		this.x = tuple4d.x;
		this.y = tuple4d.y;
		this.z = tuple4d.z;
		this.w = tuple4d.w;
	}

	public Tuple4d(Tuple4f tuple4f) {
		this.x = (double)tuple4f.x;
		this.y = (double)tuple4f.y;
		this.z = (double)tuple4f.z;
		this.w = (double)tuple4f.w;
	}

	public Tuple4d() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
		this.w = 0.0;
	}

	public final void set(double double1, double double2, double double3, double double4) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		this.w = double4;
	}

	public final void set(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
		this.z = doubleArray[2];
		this.w = doubleArray[3];
	}

	public final void set(Tuple4d tuple4d) {
		this.x = tuple4d.x;
		this.y = tuple4d.y;
		this.z = tuple4d.z;
		this.w = tuple4d.w;
	}

	public final void set(Tuple4f tuple4f) {
		this.x = (double)tuple4f.x;
		this.y = (double)tuple4f.y;
		this.z = (double)tuple4f.z;
		this.w = (double)tuple4f.w;
	}

	public final void get(double[] doubleArray) {
		doubleArray[0] = this.x;
		doubleArray[1] = this.y;
		doubleArray[2] = this.z;
		doubleArray[3] = this.w;
	}

	public final void get(Tuple4d tuple4d) {
		tuple4d.x = this.x;
		tuple4d.y = this.y;
		tuple4d.z = this.z;
		tuple4d.w = this.w;
	}

	public final void add(Tuple4d tuple4d, Tuple4d tuple4d2) {
		this.x = tuple4d.x + tuple4d2.x;
		this.y = tuple4d.y + tuple4d2.y;
		this.z = tuple4d.z + tuple4d2.z;
		this.w = tuple4d.w + tuple4d2.w;
	}

	public final void add(Tuple4d tuple4d) {
		this.x += tuple4d.x;
		this.y += tuple4d.y;
		this.z += tuple4d.z;
		this.w += tuple4d.w;
	}

	public final void sub(Tuple4d tuple4d, Tuple4d tuple4d2) {
		this.x = tuple4d.x - tuple4d2.x;
		this.y = tuple4d.y - tuple4d2.y;
		this.z = tuple4d.z - tuple4d2.z;
		this.w = tuple4d.w - tuple4d2.w;
	}

	public final void sub(Tuple4d tuple4d) {
		this.x -= tuple4d.x;
		this.y -= tuple4d.y;
		this.z -= tuple4d.z;
		this.w -= tuple4d.w;
	}

	public final void negate(Tuple4d tuple4d) {
		this.x = -tuple4d.x;
		this.y = -tuple4d.y;
		this.z = -tuple4d.z;
		this.w = -tuple4d.w;
	}

	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
	}

	public final void scale(double double1, Tuple4d tuple4d) {
		this.x = double1 * tuple4d.x;
		this.y = double1 * tuple4d.y;
		this.z = double1 * tuple4d.z;
		this.w = double1 * tuple4d.w;
	}

	public final void scale(double double1) {
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		this.w *= double1;
	}

	public final void scaleAdd(double double1, Tuple4d tuple4d, Tuple4d tuple4d2) {
		this.x = double1 * tuple4d.x + tuple4d2.x;
		this.y = double1 * tuple4d.y + tuple4d2.y;
		this.z = double1 * tuple4d.z + tuple4d2.z;
		this.w = double1 * tuple4d.w + tuple4d2.w;
	}

	public final void scaleAdd(float float1, Tuple4d tuple4d) {
		this.scaleAdd((double)float1, tuple4d);
	}

	public final void scaleAdd(double double1, Tuple4d tuple4d) {
		this.x = double1 * this.x + tuple4d.x;
		this.y = double1 * this.y + tuple4d.y;
		this.z = double1 * this.z + tuple4d.z;
		this.w = double1 * this.w + tuple4d.w;
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
	}

	public boolean equals(Tuple4d tuple4d) {
		try {
			return this.x == tuple4d.x && this.y == tuple4d.y && this.z == tuple4d.z && this.w == tuple4d.w;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Tuple4d tuple4d = (Tuple4d)object;
			return this.x == tuple4d.x && this.y == tuple4d.y && this.z == tuple4d.z && this.w == tuple4d.w;
		} catch (NullPointerException nullPointerException) {
			return false;
		} catch (ClassCastException classCastException) {
			return false;
		}
	}

	public boolean epsilonEquals(Tuple4d tuple4d, double double1) {
		double double2 = this.x - tuple4d.x;
		if (Double.isNaN(double2)) {
			return false;
		} else if ((double2 < 0.0 ? -double2 : double2) > double1) {
			return false;
		} else {
			double2 = this.y - tuple4d.y;
			if (Double.isNaN(double2)) {
				return false;
			} else if ((double2 < 0.0 ? -double2 : double2) > double1) {
				return false;
			} else {
				double2 = this.z - tuple4d.z;
				if (Double.isNaN(double2)) {
					return false;
				} else if ((double2 < 0.0 ? -double2 : double2) > double1) {
					return false;
				} else {
					double2 = this.w - tuple4d.w;
					if (Double.isNaN(double2)) {
						return false;
					} else {
						return !((double2 < 0.0 ? -double2 : double2) > double1);
					}
				}
			}
		}
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.x);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.y);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.z);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.w);
		return (int)(long1 ^ long1 >> 32);
	}

	public final void clamp(float float1, float float2, Tuple4d tuple4d) {
		this.clamp((double)float1, (double)float2, tuple4d);
	}

	public final void clamp(double double1, double double2, Tuple4d tuple4d) {
		if (tuple4d.x > double2) {
			this.x = double2;
		} else if (tuple4d.x < double1) {
			this.x = double1;
		} else {
			this.x = tuple4d.x;
		}

		if (tuple4d.y > double2) {
			this.y = double2;
		} else if (tuple4d.y < double1) {
			this.y = double1;
		} else {
			this.y = tuple4d.y;
		}

		if (tuple4d.z > double2) {
			this.z = double2;
		} else if (tuple4d.z < double1) {
			this.z = double1;
		} else {
			this.z = tuple4d.z;
		}

		if (tuple4d.w > double2) {
			this.w = double2;
		} else if (tuple4d.w < double1) {
			this.w = double1;
		} else {
			this.w = tuple4d.w;
		}
	}

	public final void clampMin(float float1, Tuple4d tuple4d) {
		this.clampMin((double)float1, tuple4d);
	}

	public final void clampMin(double double1, Tuple4d tuple4d) {
		if (tuple4d.x < double1) {
			this.x = double1;
		} else {
			this.x = tuple4d.x;
		}

		if (tuple4d.y < double1) {
			this.y = double1;
		} else {
			this.y = tuple4d.y;
		}

		if (tuple4d.z < double1) {
			this.z = double1;
		} else {
			this.z = tuple4d.z;
		}

		if (tuple4d.w < double1) {
			this.w = double1;
		} else {
			this.w = tuple4d.w;
		}
	}

	public final void clampMax(float float1, Tuple4d tuple4d) {
		this.clampMax((double)float1, tuple4d);
	}

	public final void clampMax(double double1, Tuple4d tuple4d) {
		if (tuple4d.x > double1) {
			this.x = double1;
		} else {
			this.x = tuple4d.x;
		}

		if (tuple4d.y > double1) {
			this.y = double1;
		} else {
			this.y = tuple4d.y;
		}

		if (tuple4d.z > double1) {
			this.z = double1;
		} else {
			this.z = tuple4d.z;
		}

		if (tuple4d.w > double1) {
			this.w = double1;
		} else {
			this.w = tuple4d.z;
		}
	}

	public final void absolute(Tuple4d tuple4d) {
		this.x = Math.abs(tuple4d.x);
		this.y = Math.abs(tuple4d.y);
		this.z = Math.abs(tuple4d.z);
		this.w = Math.abs(tuple4d.w);
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

		if (this.w > double2) {
			this.w = double2;
		} else if (this.w < double1) {
			this.w = double1;
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

		if (this.w < double1) {
			this.w = double1;
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

		if (this.w > double1) {
			this.w = double1;
		}
	}

	public final void absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
		this.w = Math.abs(this.w);
	}

	public void interpolate(Tuple4d tuple4d, Tuple4d tuple4d2, float float1) {
		this.interpolate(tuple4d, tuple4d2, (double)float1);
	}

	public void interpolate(Tuple4d tuple4d, Tuple4d tuple4d2, double double1) {
		this.x = (1.0 - double1) * tuple4d.x + double1 * tuple4d2.x;
		this.y = (1.0 - double1) * tuple4d.y + double1 * tuple4d2.y;
		this.z = (1.0 - double1) * tuple4d.z + double1 * tuple4d2.z;
		this.w = (1.0 - double1) * tuple4d.w + double1 * tuple4d2.w;
	}

	public void interpolate(Tuple4d tuple4d, float float1) {
		this.interpolate(tuple4d, (double)float1);
	}

	public void interpolate(Tuple4d tuple4d, double double1) {
		this.x = (1.0 - double1) * this.x + double1 * tuple4d.x;
		this.y = (1.0 - double1) * this.y + double1 * tuple4d.y;
		this.z = (1.0 - double1) * this.z + double1 * tuple4d.z;
		this.w = (1.0 - double1) * this.w + double1 * tuple4d.w;
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

	public final double getW() {
		return this.w;
	}

	public final void setW(double double1) {
		this.w = double1;
	}
}
