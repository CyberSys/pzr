package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;


public class AxisAngle4d implements Externalizable {
	private static final long serialVersionUID = 1L;
	public double angle;
	public double x;
	public double y;
	public double z;

	public AxisAngle4d() {
		this.z = 1.0;
	}

	public AxisAngle4d(AxisAngle4d axisAngle4d) {
		this.x = axisAngle4d.x;
		this.y = axisAngle4d.y;
		this.z = axisAngle4d.z;
		this.angle = (axisAngle4d.angle < 0.0 ? 6.283185307179586 + axisAngle4d.angle % 6.283185307179586 : axisAngle4d.angle) % 6.283185307179586;
	}

	public AxisAngle4d(AxisAngle4f axisAngle4f) {
		this.x = (double)axisAngle4f.x;
		this.y = (double)axisAngle4f.y;
		this.z = (double)axisAngle4f.z;
		this.angle = ((double)axisAngle4f.angle < 0.0 ? 6.283185307179586 + (double)axisAngle4f.angle % 6.283185307179586 : (double)axisAngle4f.angle) % 6.283185307179586;
	}

	public AxisAngle4d(Quaternionfc quaternionfc) {
		float float1 = Math.safeAcos(quaternionfc.w());
		float float2 = Math.invsqrt(1.0F - quaternionfc.w() * quaternionfc.w());
		if (Float.isInfinite(float2)) {
			this.x = 0.0;
			this.y = 0.0;
			this.z = 1.0;
		} else {
			this.x = (double)(quaternionfc.x() * float2);
			this.y = (double)(quaternionfc.y() * float2);
			this.z = (double)(quaternionfc.z() * float2);
		}

		this.angle = (double)(float1 + float1);
	}

	public AxisAngle4d(Quaterniondc quaterniondc) {
		double double1 = Math.safeAcos(quaterniondc.w());
		double double2 = Math.invsqrt(1.0 - quaterniondc.w() * quaterniondc.w());
		if (Double.isInfinite(double2)) {
			this.x = 0.0;
			this.y = 0.0;
			this.z = 1.0;
		} else {
			this.x = quaterniondc.x() * double2;
			this.y = quaterniondc.y() * double2;
			this.z = quaterniondc.z() * double2;
		}

		this.angle = double1 + double1;
	}

	public AxisAngle4d(double double1, double double2, double double3, double double4) {
		this.x = double2;
		this.y = double3;
		this.z = double4;
		this.angle = (double1 < 0.0 ? 6.283185307179586 + double1 % 6.283185307179586 : double1) % 6.283185307179586;
	}

	public AxisAngle4d(double double1, Vector3dc vector3dc) {
		this(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public AxisAngle4d(double double1, Vector3f vector3f) {
		this(double1, (double)vector3f.x, (double)vector3f.y, (double)vector3f.z);
	}

	public AxisAngle4d set(AxisAngle4d axisAngle4d) {
		this.x = axisAngle4d.x;
		this.y = axisAngle4d.y;
		this.z = axisAngle4d.z;
		this.angle = (axisAngle4d.angle < 0.0 ? 6.283185307179586 + axisAngle4d.angle % 6.283185307179586 : axisAngle4d.angle) % 6.283185307179586;
		return this;
	}

	public AxisAngle4d set(AxisAngle4f axisAngle4f) {
		this.x = (double)axisAngle4f.x;
		this.y = (double)axisAngle4f.y;
		this.z = (double)axisAngle4f.z;
		this.angle = ((double)axisAngle4f.angle < 0.0 ? 6.283185307179586 + (double)axisAngle4f.angle % 6.283185307179586 : (double)axisAngle4f.angle) % 6.283185307179586;
		return this;
	}

	public AxisAngle4d set(double double1, double double2, double double3, double double4) {
		this.x = double2;
		this.y = double3;
		this.z = double4;
		this.angle = (double1 < 0.0 ? 6.283185307179586 + double1 % 6.283185307179586 : double1) % 6.283185307179586;
		return this;
	}

	public AxisAngle4d set(double double1, Vector3dc vector3dc) {
		return this.set(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public AxisAngle4d set(double double1, Vector3f vector3f) {
		return this.set(double1, (double)vector3f.x, (double)vector3f.y, (double)vector3f.z);
	}

	public AxisAngle4d set(Quaternionfc quaternionfc) {
		float float1 = Math.safeAcos(quaternionfc.w());
		float float2 = Math.invsqrt(1.0F - quaternionfc.w() * quaternionfc.w());
		if (Float.isInfinite(float2)) {
			this.x = 0.0;
			this.y = 0.0;
			this.z = 1.0;
		} else {
			this.x = (double)(quaternionfc.x() * float2);
			this.y = (double)(quaternionfc.y() * float2);
			this.z = (double)(quaternionfc.z() * float2);
		}

		this.angle = (double)(float1 + float1);
		return this;
	}

	public AxisAngle4d set(Quaterniondc quaterniondc) {
		double double1 = Math.safeAcos(quaterniondc.w());
		double double2 = Math.invsqrt(1.0 - quaterniondc.w() * quaterniondc.w());
		if (Double.isInfinite(double2)) {
			this.x = 0.0;
			this.y = 0.0;
			this.z = 1.0;
		} else {
			this.x = quaterniondc.x() * double2;
			this.y = quaterniondc.y() * double2;
			this.z = quaterniondc.z() * double2;
		}

		this.angle = double1 + double1;
		return this;
	}

	public AxisAngle4d set(Matrix3fc matrix3fc) {
		double double1 = (double)matrix3fc.m00();
		double double2 = (double)matrix3fc.m01();
		double double3 = (double)matrix3fc.m02();
		double double4 = (double)matrix3fc.m10();
		double double5 = (double)matrix3fc.m11();
		double double6 = (double)matrix3fc.m12();
		double double7 = (double)matrix3fc.m20();
		double double8 = (double)matrix3fc.m21();
		double double9 = (double)matrix3fc.m22();
		double double10 = (double)Math.invsqrt(matrix3fc.m00() * matrix3fc.m00() + matrix3fc.m01() * matrix3fc.m01() + matrix3fc.m02() * matrix3fc.m02());
		double double11 = (double)Math.invsqrt(matrix3fc.m10() * matrix3fc.m10() + matrix3fc.m11() * matrix3fc.m11() + matrix3fc.m12() * matrix3fc.m12());
		double double12 = (double)Math.invsqrt(matrix3fc.m20() * matrix3fc.m20() + matrix3fc.m21() * matrix3fc.m21() + matrix3fc.m22() * matrix3fc.m22());
		double1 *= double10;
		double2 *= double10;
		double3 *= double10;
		double4 *= double11;
		double5 *= double11;
		double6 *= double11;
		double7 *= double12;
		double8 *= double12;
		double9 *= double12;
		double double13 = 1.0E-4;
		double double14 = 0.001;
		double double15;
		if (Math.abs(double4 - double2) < double13 && Math.abs(double7 - double3) < double13 && Math.abs(double8 - double6) < double13) {
			if (Math.abs(double4 + double2) < double14 && Math.abs(double7 + double3) < double14 && Math.abs(double8 + double6) < double14 && Math.abs(double1 + double5 + double9 - 3.0) < double14) {
				this.x = 0.0;
				this.y = 0.0;
				this.z = 1.0;
				this.angle = 0.0;
				return this;
			} else {
				this.angle = 3.141592653589793;
				double15 = (double1 + 1.0) / 2.0;
				double double16 = (double5 + 1.0) / 2.0;
				double double17 = (double9 + 1.0) / 2.0;
				double double18 = (double4 + double2) / 4.0;
				double double19 = (double7 + double3) / 4.0;
				double double20 = (double8 + double6) / 4.0;
				if (double15 > double16 && double15 > double17) {
					this.x = Math.sqrt(double15);
					this.y = double18 / this.x;
					this.z = double19 / this.x;
				} else if (double16 > double17) {
					this.y = Math.sqrt(double16);
					this.x = double18 / this.y;
					this.z = double20 / this.y;
				} else {
					this.z = Math.sqrt(double17);
					this.x = double19 / this.z;
					this.y = double20 / this.z;
				}

				return this;
			}
		} else {
			double15 = Math.sqrt((double6 - double8) * (double6 - double8) + (double7 - double3) * (double7 - double3) + (double2 - double4) * (double2 - double4));
			this.angle = Math.safeAcos((double1 + double5 + double9 - 1.0) / 2.0);
			this.x = (double6 - double8) / double15;
			this.y = (double7 - double3) / double15;
			this.z = (double2 - double4) / double15;
			return this;
		}
	}

	public AxisAngle4d set(Matrix3dc matrix3dc) {
		double double1 = matrix3dc.m00();
		double double2 = matrix3dc.m01();
		double double3 = matrix3dc.m02();
		double double4 = matrix3dc.m10();
		double double5 = matrix3dc.m11();
		double double6 = matrix3dc.m12();
		double double7 = matrix3dc.m20();
		double double8 = matrix3dc.m21();
		double double9 = matrix3dc.m22();
		double double10 = Math.invsqrt(matrix3dc.m00() * matrix3dc.m00() + matrix3dc.m01() * matrix3dc.m01() + matrix3dc.m02() * matrix3dc.m02());
		double double11 = Math.invsqrt(matrix3dc.m10() * matrix3dc.m10() + matrix3dc.m11() * matrix3dc.m11() + matrix3dc.m12() * matrix3dc.m12());
		double double12 = Math.invsqrt(matrix3dc.m20() * matrix3dc.m20() + matrix3dc.m21() * matrix3dc.m21() + matrix3dc.m22() * matrix3dc.m22());
		double1 *= double10;
		double2 *= double10;
		double3 *= double10;
		double4 *= double11;
		double5 *= double11;
		double6 *= double11;
		double7 *= double12;
		double8 *= double12;
		double9 *= double12;
		double double13 = 1.0E-4;
		double double14 = 0.001;
		double double15;
		if (Math.abs(double4 - double2) < double13 && Math.abs(double7 - double3) < double13 && Math.abs(double8 - double6) < double13) {
			if (Math.abs(double4 + double2) < double14 && Math.abs(double7 + double3) < double14 && Math.abs(double8 + double6) < double14 && Math.abs(double1 + double5 + double9 - 3.0) < double14) {
				this.x = 0.0;
				this.y = 0.0;
				this.z = 1.0;
				this.angle = 0.0;
				return this;
			} else {
				this.angle = 3.141592653589793;
				double15 = (double1 + 1.0) / 2.0;
				double double16 = (double5 + 1.0) / 2.0;
				double double17 = (double9 + 1.0) / 2.0;
				double double18 = (double4 + double2) / 4.0;
				double double19 = (double7 + double3) / 4.0;
				double double20 = (double8 + double6) / 4.0;
				if (double15 > double16 && double15 > double17) {
					this.x = Math.sqrt(double15);
					this.y = double18 / this.x;
					this.z = double19 / this.x;
				} else if (double16 > double17) {
					this.y = Math.sqrt(double16);
					this.x = double18 / this.y;
					this.z = double20 / this.y;
				} else {
					this.z = Math.sqrt(double17);
					this.x = double19 / this.z;
					this.y = double20 / this.z;
				}

				return this;
			}
		} else {
			double15 = Math.sqrt((double6 - double8) * (double6 - double8) + (double7 - double3) * (double7 - double3) + (double2 - double4) * (double2 - double4));
			this.angle = Math.safeAcos((double1 + double5 + double9 - 1.0) / 2.0);
			this.x = (double6 - double8) / double15;
			this.y = (double7 - double3) / double15;
			this.z = (double2 - double4) / double15;
			return this;
		}
	}

	public AxisAngle4d set(Matrix4fc matrix4fc) {
		double double1 = (double)matrix4fc.m00();
		double double2 = (double)matrix4fc.m01();
		double double3 = (double)matrix4fc.m02();
		double double4 = (double)matrix4fc.m10();
		double double5 = (double)matrix4fc.m11();
		double double6 = (double)matrix4fc.m12();
		double double7 = (double)matrix4fc.m20();
		double double8 = (double)matrix4fc.m21();
		double double9 = (double)matrix4fc.m22();
		double double10 = (double)Math.invsqrt(matrix4fc.m00() * matrix4fc.m00() + matrix4fc.m01() * matrix4fc.m01() + matrix4fc.m02() * matrix4fc.m02());
		double double11 = (double)Math.invsqrt(matrix4fc.m10() * matrix4fc.m10() + matrix4fc.m11() * matrix4fc.m11() + matrix4fc.m12() * matrix4fc.m12());
		double double12 = (double)Math.invsqrt(matrix4fc.m20() * matrix4fc.m20() + matrix4fc.m21() * matrix4fc.m21() + matrix4fc.m22() * matrix4fc.m22());
		double1 *= double10;
		double2 *= double10;
		double3 *= double10;
		double4 *= double11;
		double5 *= double11;
		double6 *= double11;
		double7 *= double12;
		double8 *= double12;
		double9 *= double12;
		double double13 = 1.0E-4;
		double double14 = 0.001;
		double double15;
		if (Math.abs(double4 - double2) < double13 && Math.abs(double7 - double3) < double13 && Math.abs(double8 - double6) < double13) {
			if (Math.abs(double4 + double2) < double14 && Math.abs(double7 + double3) < double14 && Math.abs(double8 + double6) < double14 && Math.abs(double1 + double5 + double9 - 3.0) < double14) {
				this.x = 0.0;
				this.y = 0.0;
				this.z = 1.0;
				this.angle = 0.0;
				return this;
			} else {
				this.angle = 3.141592653589793;
				double15 = (double1 + 1.0) / 2.0;
				double double16 = (double5 + 1.0) / 2.0;
				double double17 = (double9 + 1.0) / 2.0;
				double double18 = (double4 + double2) / 4.0;
				double double19 = (double7 + double3) / 4.0;
				double double20 = (double8 + double6) / 4.0;
				if (double15 > double16 && double15 > double17) {
					this.x = Math.sqrt(double15);
					this.y = double18 / this.x;
					this.z = double19 / this.x;
				} else if (double16 > double17) {
					this.y = Math.sqrt(double16);
					this.x = double18 / this.y;
					this.z = double20 / this.y;
				} else {
					this.z = Math.sqrt(double17);
					this.x = double19 / this.z;
					this.y = double20 / this.z;
				}

				return this;
			}
		} else {
			double15 = Math.sqrt((double6 - double8) * (double6 - double8) + (double7 - double3) * (double7 - double3) + (double2 - double4) * (double2 - double4));
			this.angle = Math.safeAcos((double1 + double5 + double9 - 1.0) / 2.0);
			this.x = (double6 - double8) / double15;
			this.y = (double7 - double3) / double15;
			this.z = (double2 - double4) / double15;
			return this;
		}
	}

	public AxisAngle4d set(Matrix4x3fc matrix4x3fc) {
		double double1 = (double)matrix4x3fc.m00();
		double double2 = (double)matrix4x3fc.m01();
		double double3 = (double)matrix4x3fc.m02();
		double double4 = (double)matrix4x3fc.m10();
		double double5 = (double)matrix4x3fc.m11();
		double double6 = (double)matrix4x3fc.m12();
		double double7 = (double)matrix4x3fc.m20();
		double double8 = (double)matrix4x3fc.m21();
		double double9 = (double)matrix4x3fc.m22();
		double double10 = (double)Math.invsqrt(matrix4x3fc.m00() * matrix4x3fc.m00() + matrix4x3fc.m01() * matrix4x3fc.m01() + matrix4x3fc.m02() * matrix4x3fc.m02());
		double double11 = (double)Math.invsqrt(matrix4x3fc.m10() * matrix4x3fc.m10() + matrix4x3fc.m11() * matrix4x3fc.m11() + matrix4x3fc.m12() * matrix4x3fc.m12());
		double double12 = (double)Math.invsqrt(matrix4x3fc.m20() * matrix4x3fc.m20() + matrix4x3fc.m21() * matrix4x3fc.m21() + matrix4x3fc.m22() * matrix4x3fc.m22());
		double1 *= double10;
		double2 *= double10;
		double3 *= double10;
		double4 *= double11;
		double5 *= double11;
		double6 *= double11;
		double7 *= double12;
		double8 *= double12;
		double9 *= double12;
		double double13 = 1.0E-4;
		double double14 = 0.001;
		double double15;
		if (Math.abs(double4 - double2) < double13 && Math.abs(double7 - double3) < double13 && Math.abs(double8 - double6) < double13) {
			if (Math.abs(double4 + double2) < double14 && Math.abs(double7 + double3) < double14 && Math.abs(double8 + double6) < double14 && Math.abs(double1 + double5 + double9 - 3.0) < double14) {
				this.x = 0.0;
				this.y = 0.0;
				this.z = 1.0;
				this.angle = 0.0;
				return this;
			} else {
				this.angle = 3.141592653589793;
				double15 = (double1 + 1.0) / 2.0;
				double double16 = (double5 + 1.0) / 2.0;
				double double17 = (double9 + 1.0) / 2.0;
				double double18 = (double4 + double2) / 4.0;
				double double19 = (double7 + double3) / 4.0;
				double double20 = (double8 + double6) / 4.0;
				if (double15 > double16 && double15 > double17) {
					this.x = Math.sqrt(double15);
					this.y = double18 / this.x;
					this.z = double19 / this.x;
				} else if (double16 > double17) {
					this.y = Math.sqrt(double16);
					this.x = double18 / this.y;
					this.z = double20 / this.y;
				} else {
					this.z = Math.sqrt(double17);
					this.x = double19 / this.z;
					this.y = double20 / this.z;
				}

				return this;
			}
		} else {
			double15 = Math.sqrt((double6 - double8) * (double6 - double8) + (double7 - double3) * (double7 - double3) + (double2 - double4) * (double2 - double4));
			this.angle = Math.safeAcos((double1 + double5 + double9 - 1.0) / 2.0);
			this.x = (double6 - double8) / double15;
			this.y = (double7 - double3) / double15;
			this.z = (double2 - double4) / double15;
			return this;
		}
	}

	public AxisAngle4d set(Matrix4dc matrix4dc) {
		double double1 = matrix4dc.m00();
		double double2 = matrix4dc.m01();
		double double3 = matrix4dc.m02();
		double double4 = matrix4dc.m10();
		double double5 = matrix4dc.m11();
		double double6 = matrix4dc.m12();
		double double7 = matrix4dc.m20();
		double double8 = matrix4dc.m21();
		double double9 = matrix4dc.m22();
		double double10 = Math.invsqrt(matrix4dc.m00() * matrix4dc.m00() + matrix4dc.m01() * matrix4dc.m01() + matrix4dc.m02() * matrix4dc.m02());
		double double11 = Math.invsqrt(matrix4dc.m10() * matrix4dc.m10() + matrix4dc.m11() * matrix4dc.m11() + matrix4dc.m12() * matrix4dc.m12());
		double double12 = Math.invsqrt(matrix4dc.m20() * matrix4dc.m20() + matrix4dc.m21() * matrix4dc.m21() + matrix4dc.m22() * matrix4dc.m22());
		double1 *= double10;
		double2 *= double10;
		double3 *= double10;
		double4 *= double11;
		double5 *= double11;
		double6 *= double11;
		double7 *= double12;
		double8 *= double12;
		double9 *= double12;
		double double13 = 1.0E-4;
		double double14 = 0.001;
		double double15;
		if (Math.abs(double4 - double2) < double13 && Math.abs(double7 - double3) < double13 && Math.abs(double8 - double6) < double13) {
			if (Math.abs(double4 + double2) < double14 && Math.abs(double7 + double3) < double14 && Math.abs(double8 + double6) < double14 && Math.abs(double1 + double5 + double9 - 3.0) < double14) {
				this.x = 0.0;
				this.y = 0.0;
				this.z = 1.0;
				this.angle = 0.0;
				return this;
			} else {
				this.angle = 3.141592653589793;
				double15 = (double1 + 1.0) / 2.0;
				double double16 = (double5 + 1.0) / 2.0;
				double double17 = (double9 + 1.0) / 2.0;
				double double18 = (double4 + double2) / 4.0;
				double double19 = (double7 + double3) / 4.0;
				double double20 = (double8 + double6) / 4.0;
				if (double15 > double16 && double15 > double17) {
					this.x = Math.sqrt(double15);
					this.y = double18 / this.x;
					this.z = double19 / this.x;
				} else if (double16 > double17) {
					this.y = Math.sqrt(double16);
					this.x = double18 / this.y;
					this.z = double20 / this.y;
				} else {
					this.z = Math.sqrt(double17);
					this.x = double19 / this.z;
					this.y = double20 / this.z;
				}

				return this;
			}
		} else {
			double15 = Math.sqrt((double6 - double8) * (double6 - double8) + (double7 - double3) * (double7 - double3) + (double2 - double4) * (double2 - double4));
			this.angle = Math.safeAcos((double1 + double5 + double9 - 1.0) / 2.0);
			this.x = (double6 - double8) / double15;
			this.y = (double7 - double3) / double15;
			this.z = (double2 - double4) / double15;
			return this;
		}
	}

	public Quaternionf get(Quaternionf quaternionf) {
		return quaternionf.set(this);
	}

	public Quaterniond get(Quaterniond quaterniond) {
		return quaterniond.set(this);
	}

	public Matrix4f get(Matrix4f matrix4f) {
		return matrix4f.set(this);
	}

	public Matrix3f get(Matrix3f matrix3f) {
		return matrix3f.set(this);
	}

	public Matrix4d get(Matrix4d matrix4d) {
		return matrix4d.set(this);
	}

	public Matrix3d get(Matrix3d matrix3d) {
		return matrix3d.set(this);
	}

	public AxisAngle4d get(AxisAngle4d axisAngle4d) {
		return axisAngle4d.set(this);
	}

	public AxisAngle4f get(AxisAngle4f axisAngle4f) {
		return axisAngle4f.set(this);
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeDouble(this.angle);
		objectOutput.writeDouble(this.x);
		objectOutput.writeDouble(this.y);
		objectOutput.writeDouble(this.z);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.angle = objectInput.readDouble();
		this.x = objectInput.readDouble();
		this.y = objectInput.readDouble();
		this.z = objectInput.readDouble();
	}

	public AxisAngle4d normalize() {
		double double1 = Math.invsqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		return this;
	}

	public AxisAngle4d rotate(double double1) {
		this.angle += double1;
		this.angle = (this.angle < 0.0 ? 6.283185307179586 + this.angle % 6.283185307179586 : this.angle) % 6.283185307179586;
		return this;
	}

	public Vector3d transform(Vector3d vector3d) {
		return this.transform((Vector3dc)vector3d, (Vector3d)vector3d);
	}

	public Vector3d transform(Vector3dc vector3dc, Vector3d vector3d) {
		double double1 = Math.sin(this.angle);
		double double2 = Math.cosFromSin(double1, this.angle);
		double double3 = this.x * vector3dc.x() + this.y * vector3dc.y() + this.z * vector3dc.z();
		vector3d.set(vector3dc.x() * double2 + double1 * (this.y * vector3dc.z() - this.z * vector3dc.y()) + (1.0 - double2) * double3 * this.x, vector3dc.y() * double2 + double1 * (this.z * vector3dc.x() - this.x * vector3dc.z()) + (1.0 - double2) * double3 * this.y, vector3dc.z() * double2 + double1 * (this.x * vector3dc.y() - this.y * vector3dc.x()) + (1.0 - double2) * double3 * this.z);
		return vector3d;
	}

	public Vector3f transform(Vector3f vector3f) {
		return this.transform((Vector3fc)vector3f, (Vector3f)vector3f);
	}

	public Vector3f transform(Vector3fc vector3fc, Vector3f vector3f) {
		double double1 = Math.sin(this.angle);
		double double2 = Math.cosFromSin(double1, this.angle);
		double double3 = this.x * (double)vector3fc.x() + this.y * (double)vector3fc.y() + this.z * (double)vector3fc.z();
		vector3f.set((float)((double)vector3fc.x() * double2 + double1 * (this.y * (double)vector3fc.z() - this.z * (double)vector3fc.y()) + (1.0 - double2) * double3 * this.x), (float)((double)vector3fc.y() * double2 + double1 * (this.z * (double)vector3fc.x() - this.x * (double)vector3fc.z()) + (1.0 - double2) * double3 * this.y), (float)((double)vector3fc.z() * double2 + double1 * (this.x * (double)vector3fc.y() - this.y * (double)vector3fc.x()) + (1.0 - double2) * double3 * this.z));
		return vector3f;
	}

	public Vector4d transform(Vector4d vector4d) {
		return this.transform((Vector4dc)vector4d, (Vector4d)vector4d);
	}

	public Vector4d transform(Vector4dc vector4dc, Vector4d vector4d) {
		double double1 = Math.sin(this.angle);
		double double2 = Math.cosFromSin(double1, this.angle);
		double double3 = this.x * vector4dc.x() + this.y * vector4dc.y() + this.z * vector4dc.z();
		vector4d.set(vector4dc.x() * double2 + double1 * (this.y * vector4dc.z() - this.z * vector4dc.y()) + (1.0 - double2) * double3 * this.x, vector4dc.y() * double2 + double1 * (this.z * vector4dc.x() - this.x * vector4dc.z()) + (1.0 - double2) * double3 * this.y, vector4dc.z() * double2 + double1 * (this.x * vector4dc.y() - this.y * vector4dc.x()) + (1.0 - double2) * double3 * this.z, vector4d.w);
		return vector4d;
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format(this.x, numberFormat);
		return "(" + string + " " + Runtime.format(this.y, numberFormat) + " " + Runtime.format(this.z, numberFormat) + " <| " + Runtime.format(this.angle, numberFormat) + ")";
	}

	public int hashCode() {
		byte byte1 = 1;
		long long1 = Double.doubleToLongBits((this.angle < 0.0 ? 6.283185307179586 + this.angle % 6.283185307179586 : this.angle) % 6.283185307179586);
		int int1 = 31 * byte1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.x);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.y);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.z);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		return int1;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object == null) {
			return false;
		} else if (this.getClass() != object.getClass()) {
			return false;
		} else {
			AxisAngle4d axisAngle4d = (AxisAngle4d)object;
			if (Double.doubleToLongBits((this.angle < 0.0 ? 6.283185307179586 + this.angle % 6.283185307179586 : this.angle) % 6.283185307179586) != Double.doubleToLongBits((axisAngle4d.angle < 0.0 ? 6.283185307179586 + axisAngle4d.angle % 6.283185307179586 : axisAngle4d.angle) % 6.283185307179586)) {
				return false;
			} else if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(axisAngle4d.x)) {
				return false;
			} else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(axisAngle4d.y)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(axisAngle4d.z);
			}
		}
	}
}
