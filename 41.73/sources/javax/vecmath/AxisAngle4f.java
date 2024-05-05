package javax.vecmath;

import java.io.Serializable;


public class AxisAngle4f implements Serializable,Cloneable {
	static final long serialVersionUID = -163246355858070601L;
	public float x;
	public float y;
	public float z;
	public float angle;
	static final double EPS = 1.0E-6;

	public AxisAngle4f(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.angle = float4;
	}

	public AxisAngle4f(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
		this.z = floatArray[2];
		this.angle = floatArray[3];
	}

	public AxisAngle4f(AxisAngle4f axisAngle4f) {
		this.x = axisAngle4f.x;
		this.y = axisAngle4f.y;
		this.z = axisAngle4f.z;
		this.angle = axisAngle4f.angle;
	}

	public AxisAngle4f(AxisAngle4d axisAngle4d) {
		this.x = (float)axisAngle4d.x;
		this.y = (float)axisAngle4d.y;
		this.z = (float)axisAngle4d.z;
		this.angle = (float)axisAngle4d.angle;
	}

	public AxisAngle4f(Vector3f vector3f, float float1) {
		this.x = vector3f.x;
		this.y = vector3f.y;
		this.z = vector3f.z;
		this.angle = float1;
	}

	public AxisAngle4f() {
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 1.0F;
		this.angle = 0.0F;
	}

	public final void set(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.angle = float4;
	}

	public final void set(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
		this.z = floatArray[2];
		this.angle = floatArray[3];
	}

	public final void set(AxisAngle4f axisAngle4f) {
		this.x = axisAngle4f.x;
		this.y = axisAngle4f.y;
		this.z = axisAngle4f.z;
		this.angle = axisAngle4f.angle;
	}

	public final void set(AxisAngle4d axisAngle4d) {
		this.x = (float)axisAngle4d.x;
		this.y = (float)axisAngle4d.y;
		this.z = (float)axisAngle4d.z;
		this.angle = (float)axisAngle4d.angle;
	}

	public final void set(Vector3f vector3f, float float1) {
		this.x = vector3f.x;
		this.y = vector3f.y;
		this.z = vector3f.z;
		this.angle = float1;
	}

	public final void get(float[] floatArray) {
		floatArray[0] = this.x;
		floatArray[1] = this.y;
		floatArray[2] = this.z;
		floatArray[3] = this.angle;
	}

	public final void set(Quat4f quat4f) {
		double double1 = (double)(quat4f.x * quat4f.x + quat4f.y * quat4f.y + quat4f.z * quat4f.z);
		if (double1 > 1.0E-6) {
			double1 = Math.sqrt(double1);
			double double2 = 1.0 / double1;
			this.x = (float)((double)quat4f.x * double2);
			this.y = (float)((double)quat4f.y * double2);
			this.z = (float)((double)quat4f.z * double2);
			this.angle = (float)(2.0 * Math.atan2(double1, (double)quat4f.w));
		} else {
			this.x = 0.0F;
			this.y = 1.0F;
			this.z = 0.0F;
			this.angle = 0.0F;
		}
	}

	public final void set(Quat4d quat4d) {
		double double1 = quat4d.x * quat4d.x + quat4d.y * quat4d.y + quat4d.z * quat4d.z;
		if (double1 > 1.0E-6) {
			double1 = Math.sqrt(double1);
			double double2 = 1.0 / double1;
			this.x = (float)(quat4d.x * double2);
			this.y = (float)(quat4d.y * double2);
			this.z = (float)(quat4d.z * double2);
			this.angle = (float)(2.0 * Math.atan2(double1, quat4d.w));
		} else {
			this.x = 0.0F;
			this.y = 1.0F;
			this.z = 0.0F;
			this.angle = 0.0F;
		}
	}

	public final void set(Matrix4f matrix4f) {
		Matrix3f matrix3f = new Matrix3f();
		matrix4f.get(matrix3f);
		this.x = matrix3f.m21 - matrix3f.m12;
		this.y = matrix3f.m02 - matrix3f.m20;
		this.z = matrix3f.m10 - matrix3f.m01;
		double double1 = (double)(this.x * this.x + this.y * this.y + this.z * this.z);
		if (double1 > 1.0E-6) {
			double1 = Math.sqrt(double1);
			double double2 = 0.5 * double1;
			double double3 = 0.5 * ((double)(matrix3f.m00 + matrix3f.m11 + matrix3f.m22) - 1.0);
			this.angle = (float)Math.atan2(double2, double3);
			double double4 = 1.0 / double1;
			this.x = (float)((double)this.x * double4);
			this.y = (float)((double)this.y * double4);
			this.z = (float)((double)this.z * double4);
		} else {
			this.x = 0.0F;
			this.y = 1.0F;
			this.z = 0.0F;
			this.angle = 0.0F;
		}
	}

	public final void set(Matrix4d matrix4d) {
		Matrix3d matrix3d = new Matrix3d();
		matrix4d.get(matrix3d);
		this.x = (float)(matrix3d.m21 - matrix3d.m12);
		this.y = (float)(matrix3d.m02 - matrix3d.m20);
		this.z = (float)(matrix3d.m10 - matrix3d.m01);
		double double1 = (double)(this.x * this.x + this.y * this.y + this.z * this.z);
		if (double1 > 1.0E-6) {
			double1 = Math.sqrt(double1);
			double double2 = 0.5 * double1;
			double double3 = 0.5 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 - 1.0);
			this.angle = (float)Math.atan2(double2, double3);
			double double4 = 1.0 / double1;
			this.x = (float)((double)this.x * double4);
			this.y = (float)((double)this.y * double4);
			this.z = (float)((double)this.z * double4);
		} else {
			this.x = 0.0F;
			this.y = 1.0F;
			this.z = 0.0F;
			this.angle = 0.0F;
		}
	}

	public final void set(Matrix3f matrix3f) {
		this.x = matrix3f.m21 - matrix3f.m12;
		this.y = matrix3f.m02 - matrix3f.m20;
		this.z = matrix3f.m10 - matrix3f.m01;
		double double1 = (double)(this.x * this.x + this.y * this.y + this.z * this.z);
		if (double1 > 1.0E-6) {
			double1 = Math.sqrt(double1);
			double double2 = 0.5 * double1;
			double double3 = 0.5 * ((double)(matrix3f.m00 + matrix3f.m11 + matrix3f.m22) - 1.0);
			this.angle = (float)Math.atan2(double2, double3);
			double double4 = 1.0 / double1;
			this.x = (float)((double)this.x * double4);
			this.y = (float)((double)this.y * double4);
			this.z = (float)((double)this.z * double4);
		} else {
			this.x = 0.0F;
			this.y = 1.0F;
			this.z = 0.0F;
			this.angle = 0.0F;
		}
	}

	public final void set(Matrix3d matrix3d) {
		this.x = (float)(matrix3d.m21 - matrix3d.m12);
		this.y = (float)(matrix3d.m02 - matrix3d.m20);
		this.z = (float)(matrix3d.m10 - matrix3d.m01);
		double double1 = (double)(this.x * this.x + this.y * this.y + this.z * this.z);
		if (double1 > 1.0E-6) {
			double1 = Math.sqrt(double1);
			double double2 = 0.5 * double1;
			double double3 = 0.5 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 - 1.0);
			this.angle = (float)Math.atan2(double2, double3);
			double double4 = 1.0 / double1;
			this.x = (float)((double)this.x * double4);
			this.y = (float)((double)this.y * double4);
			this.z = (float)((double)this.z * double4);
		} else {
			this.x = 0.0F;
			this.y = 1.0F;
			this.z = 0.0F;
			this.angle = 0.0F;
		}
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.angle + ")";
	}

	public boolean equals(AxisAngle4f axisAngle4f) {
		try {
			return this.x == axisAngle4f.x && this.y == axisAngle4f.y && this.z == axisAngle4f.z && this.angle == axisAngle4f.angle;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			AxisAngle4f axisAngle4f = (AxisAngle4f)object;
			return this.x == axisAngle4f.x && this.y == axisAngle4f.y && this.z == axisAngle4f.z && this.angle == axisAngle4f.angle;
		} catch (NullPointerException nullPointerException) {
			return false;
		} catch (ClassCastException classCastException) {
			return false;
		}
	}

	public boolean epsilonEquals(AxisAngle4f axisAngle4f, float float1) {
		float float2 = this.x - axisAngle4f.x;
		if ((float2 < 0.0F ? -float2 : float2) > float1) {
			return false;
		} else {
			float2 = this.y - axisAngle4f.y;
			if ((float2 < 0.0F ? -float2 : float2) > float1) {
				return false;
			} else {
				float2 = this.z - axisAngle4f.z;
				if ((float2 < 0.0F ? -float2 : float2) > float1) {
					return false;
				} else {
					float2 = this.angle - axisAngle4f.angle;
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
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.angle);
		return (int)(long1 ^ long1 >> 32);
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}
	}

	public final float getAngle() {
		return this.angle;
	}

	public final void setAngle(float float1) {
		this.angle = float1;
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
