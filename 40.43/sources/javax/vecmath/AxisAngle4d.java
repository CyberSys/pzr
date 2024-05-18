package javax.vecmath;

import java.io.Serializable;


public class AxisAngle4d implements Serializable,Cloneable {
	static final long serialVersionUID = 3644296204459140589L;
	public double x;
	public double y;
	public double z;
	public double angle;
	static final double EPS = 1.0E-12;

	public AxisAngle4d(double double1, double double2, double double3, double double4) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		this.angle = double4;
	}

	public AxisAngle4d(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
		this.z = doubleArray[2];
		this.angle = doubleArray[3];
	}

	public AxisAngle4d(AxisAngle4d axisAngle4d) {
		this.x = axisAngle4d.x;
		this.y = axisAngle4d.y;
		this.z = axisAngle4d.z;
		this.angle = axisAngle4d.angle;
	}

	public AxisAngle4d(AxisAngle4f axisAngle4f) {
		this.x = (double)axisAngle4f.x;
		this.y = (double)axisAngle4f.y;
		this.z = (double)axisAngle4f.z;
		this.angle = (double)axisAngle4f.angle;
	}

	public AxisAngle4d(Vector3d vector3d, double double1) {
		this.x = vector3d.x;
		this.y = vector3d.y;
		this.z = vector3d.z;
		this.angle = double1;
	}

	public AxisAngle4d() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 1.0;
		this.angle = 0.0;
	}

	public final void set(double double1, double double2, double double3, double double4) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		this.angle = double4;
	}

	public final void set(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
		this.z = doubleArray[2];
		this.angle = doubleArray[3];
	}

	public final void set(AxisAngle4d axisAngle4d) {
		this.x = axisAngle4d.x;
		this.y = axisAngle4d.y;
		this.z = axisAngle4d.z;
		this.angle = axisAngle4d.angle;
	}

	public final void set(AxisAngle4f axisAngle4f) {
		this.x = (double)axisAngle4f.x;
		this.y = (double)axisAngle4f.y;
		this.z = (double)axisAngle4f.z;
		this.angle = (double)axisAngle4f.angle;
	}

	public final void set(Vector3d vector3d, double double1) {
		this.x = vector3d.x;
		this.y = vector3d.y;
		this.z = vector3d.z;
		this.angle = double1;
	}

	public final void get(double[] doubleArray) {
		doubleArray[0] = this.x;
		doubleArray[1] = this.y;
		doubleArray[2] = this.z;
		doubleArray[3] = this.angle;
	}

	public final void set(Matrix4f matrix4f) {
		Matrix3d matrix3d = new Matrix3d();
		matrix4f.get(matrix3d);
		this.x = (double)((float)(matrix3d.m21 - matrix3d.m12));
		this.y = (double)((float)(matrix3d.m02 - matrix3d.m20));
		this.z = (double)((float)(matrix3d.m10 - matrix3d.m01));
		double double1 = this.x * this.x + this.y * this.y + this.z * this.z;
		if (double1 > 1.0E-12) {
			double1 = Math.sqrt(double1);
			double double2 = 0.5 * double1;
			double double3 = 0.5 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 - 1.0);
			this.angle = (double)((float)Math.atan2(double2, double3));
			double double4 = 1.0 / double1;
			this.x *= double4;
			this.y *= double4;
			this.z *= double4;
		} else {
			this.x = 0.0;
			this.y = 1.0;
			this.z = 0.0;
			this.angle = 0.0;
		}
	}

	public final void set(Matrix4d matrix4d) {
		Matrix3d matrix3d = new Matrix3d();
		matrix4d.get(matrix3d);
		this.x = (double)((float)(matrix3d.m21 - matrix3d.m12));
		this.y = (double)((float)(matrix3d.m02 - matrix3d.m20));
		this.z = (double)((float)(matrix3d.m10 - matrix3d.m01));
		double double1 = this.x * this.x + this.y * this.y + this.z * this.z;
		if (double1 > 1.0E-12) {
			double1 = Math.sqrt(double1);
			double double2 = 0.5 * double1;
			double double3 = 0.5 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 - 1.0);
			this.angle = (double)((float)Math.atan2(double2, double3));
			double double4 = 1.0 / double1;
			this.x *= double4;
			this.y *= double4;
			this.z *= double4;
		} else {
			this.x = 0.0;
			this.y = 1.0;
			this.z = 0.0;
			this.angle = 0.0;
		}
	}

	public final void set(Matrix3f matrix3f) {
		this.x = (double)(matrix3f.m21 - matrix3f.m12);
		this.y = (double)(matrix3f.m02 - matrix3f.m20);
		this.z = (double)(matrix3f.m10 - matrix3f.m01);
		double double1 = this.x * this.x + this.y * this.y + this.z * this.z;
		if (double1 > 1.0E-12) {
			double1 = Math.sqrt(double1);
			double double2 = 0.5 * double1;
			double double3 = 0.5 * ((double)(matrix3f.m00 + matrix3f.m11 + matrix3f.m22) - 1.0);
			this.angle = (double)((float)Math.atan2(double2, double3));
			double double4 = 1.0 / double1;
			this.x *= double4;
			this.y *= double4;
			this.z *= double4;
		} else {
			this.x = 0.0;
			this.y = 1.0;
			this.z = 0.0;
			this.angle = 0.0;
		}
	}

	public final void set(Matrix3d matrix3d) {
		this.x = (double)((float)(matrix3d.m21 - matrix3d.m12));
		this.y = (double)((float)(matrix3d.m02 - matrix3d.m20));
		this.z = (double)((float)(matrix3d.m10 - matrix3d.m01));
		double double1 = this.x * this.x + this.y * this.y + this.z * this.z;
		if (double1 > 1.0E-12) {
			double1 = Math.sqrt(double1);
			double double2 = 0.5 * double1;
			double double3 = 0.5 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 - 1.0);
			this.angle = (double)((float)Math.atan2(double2, double3));
			double double4 = 1.0 / double1;
			this.x *= double4;
			this.y *= double4;
			this.z *= double4;
		} else {
			this.x = 0.0;
			this.y = 1.0;
			this.z = 0.0;
			this.angle = 0.0;
		}
	}

	public final void set(Quat4f quat4f) {
		double double1 = (double)(quat4f.x * quat4f.x + quat4f.y * quat4f.y + quat4f.z * quat4f.z);
		if (double1 > 1.0E-12) {
			double1 = Math.sqrt(double1);
			double double2 = 1.0 / double1;
			this.x = (double)quat4f.x * double2;
			this.y = (double)quat4f.y * double2;
			this.z = (double)quat4f.z * double2;
			this.angle = 2.0 * Math.atan2(double1, (double)quat4f.w);
		} else {
			this.x = 0.0;
			this.y = 1.0;
			this.z = 0.0;
			this.angle = 0.0;
		}
	}

	public final void set(Quat4d quat4d) {
		double double1 = quat4d.x * quat4d.x + quat4d.y * quat4d.y + quat4d.z * quat4d.z;
		if (double1 > 1.0E-12) {
			double1 = Math.sqrt(double1);
			double double2 = 1.0 / double1;
			this.x = quat4d.x * double2;
			this.y = quat4d.y * double2;
			this.z = quat4d.z * double2;
			this.angle = 2.0 * Math.atan2(double1, quat4d.w);
		} else {
			this.x = 0.0;
			this.y = 1.0;
			this.z = 0.0;
			this.angle = 0.0;
		}
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.angle + ")";
	}

	public boolean equals(AxisAngle4d axisAngle4d) {
		try {
			return this.x == axisAngle4d.x && this.y == axisAngle4d.y && this.z == axisAngle4d.z && this.angle == axisAngle4d.angle;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			AxisAngle4d axisAngle4d = (AxisAngle4d)object;
			return this.x == axisAngle4d.x && this.y == axisAngle4d.y && this.z == axisAngle4d.z && this.angle == axisAngle4d.angle;
		} catch (NullPointerException nullPointerException) {
			return false;
		} catch (ClassCastException classCastException) {
			return false;
		}
	}

	public boolean epsilonEquals(AxisAngle4d axisAngle4d, double double1) {
		double double2 = this.x - axisAngle4d.x;
		if ((double2 < 0.0 ? -double2 : double2) > double1) {
			return false;
		} else {
			double2 = this.y - axisAngle4d.y;
			if ((double2 < 0.0 ? -double2 : double2) > double1) {
				return false;
			} else {
				double2 = this.z - axisAngle4d.z;
				if ((double2 < 0.0 ? -double2 : double2) > double1) {
					return false;
				} else {
					double2 = this.angle - axisAngle4d.angle;
					return !((double2 < 0.0 ? -double2 : double2) > double1);
				}
			}
		}
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.x);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.y);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.z);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.angle);
		return (int)(long1 ^ long1 >> 32);
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}
	}

	public final double getAngle() {
		return this.angle;
	}

	public final void setAngle(double double1) {
		this.angle = double1;
	}

	public double getX() {
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

	public double getZ() {
		return this.z;
	}

	public final void setZ(double double1) {
		this.z = double1;
	}
}
