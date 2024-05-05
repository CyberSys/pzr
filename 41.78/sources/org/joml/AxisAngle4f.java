package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;


public class AxisAngle4f implements Externalizable {
	private static final long serialVersionUID = 1L;
	public float angle;
	public float x;
	public float y;
	public float z;

	public AxisAngle4f() {
		this.z = 1.0F;
	}

	public AxisAngle4f(AxisAngle4f axisAngle4f) {
		this.x = axisAngle4f.x;
		this.y = axisAngle4f.y;
		this.z = axisAngle4f.z;
		this.angle = (float)(((double)axisAngle4f.angle < 0.0 ? 6.283185307179586 + (double)axisAngle4f.angle % 6.283185307179586 : (double)axisAngle4f.angle) % 6.283185307179586);
	}

	public AxisAngle4f(Quaternionfc quaternionfc) {
		float float1 = Math.safeAcos(quaternionfc.w());
		float float2 = Math.invsqrt(1.0F - quaternionfc.w() * quaternionfc.w());
		if (Float.isInfinite(float2)) {
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 1.0F;
		} else {
			this.x = quaternionfc.x() * float2;
			this.y = quaternionfc.y() * float2;
			this.z = quaternionfc.z() * float2;
		}

		this.angle = float1 + float1;
	}

	public AxisAngle4f(float float1, float float2, float float3, float float4) {
		this.x = float2;
		this.y = float3;
		this.z = float4;
		this.angle = (float)(((double)float1 < 0.0 ? 6.283185307179586 + (double)float1 % 6.283185307179586 : (double)float1) % 6.283185307179586);
	}

	public AxisAngle4f(float float1, Vector3fc vector3fc) {
		this(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public AxisAngle4f set(AxisAngle4f axisAngle4f) {
		this.x = axisAngle4f.x;
		this.y = axisAngle4f.y;
		this.z = axisAngle4f.z;
		this.angle = axisAngle4f.angle;
		this.angle = (float)(((double)this.angle < 0.0 ? 6.283185307179586 + (double)this.angle % 6.283185307179586 : (double)this.angle) % 6.283185307179586);
		return this;
	}

	public AxisAngle4f set(AxisAngle4d axisAngle4d) {
		this.x = (float)axisAngle4d.x;
		this.y = (float)axisAngle4d.y;
		this.z = (float)axisAngle4d.z;
		this.angle = (float)axisAngle4d.angle;
		this.angle = (float)(((double)this.angle < 0.0 ? 6.283185307179586 + (double)this.angle % 6.283185307179586 : (double)this.angle) % 6.283185307179586);
		return this;
	}

	public AxisAngle4f set(float float1, float float2, float float3, float float4) {
		this.x = float2;
		this.y = float3;
		this.z = float4;
		this.angle = (float)(((double)float1 < 0.0 ? 6.283185307179586 + (double)float1 % 6.283185307179586 : (double)float1) % 6.283185307179586);
		return this;
	}

	public AxisAngle4f set(float float1, Vector3fc vector3fc) {
		return this.set(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public AxisAngle4f set(Quaternionfc quaternionfc) {
		float float1 = Math.safeAcos(quaternionfc.w());
		float float2 = Math.invsqrt(1.0F - quaternionfc.w() * quaternionfc.w());
		if (Float.isInfinite(float2)) {
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 1.0F;
		} else {
			this.x = quaternionfc.x() * float2;
			this.y = quaternionfc.y() * float2;
			this.z = quaternionfc.z() * float2;
		}

		this.angle = float1 + float1;
		return this;
	}

	public AxisAngle4f set(Quaterniondc quaterniondc) {
		double double1 = Math.safeAcos(quaterniondc.w());
		double double2 = Math.invsqrt(1.0 - quaterniondc.w() * quaterniondc.w());
		if (Double.isInfinite(double2)) {
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 1.0F;
		} else {
			this.x = (float)(quaterniondc.x() * double2);
			this.y = (float)(quaterniondc.y() * double2);
			this.z = (float)(quaterniondc.z() * double2);
		}

		this.angle = (float)(double1 + double1);
		return this;
	}

	public AxisAngle4f set(Matrix3fc matrix3fc) {
		float float1 = matrix3fc.m00();
		float float2 = matrix3fc.m01();
		float float3 = matrix3fc.m02();
		float float4 = matrix3fc.m10();
		float float5 = matrix3fc.m11();
		float float6 = matrix3fc.m12();
		float float7 = matrix3fc.m20();
		float float8 = matrix3fc.m21();
		float float9 = matrix3fc.m22();
		float float10 = Math.invsqrt(matrix3fc.m00() * matrix3fc.m00() + matrix3fc.m01() * matrix3fc.m01() + matrix3fc.m02() * matrix3fc.m02());
		float float11 = Math.invsqrt(matrix3fc.m10() * matrix3fc.m10() + matrix3fc.m11() * matrix3fc.m11() + matrix3fc.m12() * matrix3fc.m12());
		float float12 = Math.invsqrt(matrix3fc.m20() * matrix3fc.m20() + matrix3fc.m21() * matrix3fc.m21() + matrix3fc.m22() * matrix3fc.m22());
		float1 *= float10;
		float2 *= float10;
		float3 *= float10;
		float4 *= float11;
		float5 *= float11;
		float6 *= float11;
		float7 *= float12;
		float8 *= float12;
		float9 *= float12;
		float float13 = 1.0E-4F;
		float float14 = 0.001F;
		float float15;
		if (Math.abs(float4 - float2) < float13 && Math.abs(float7 - float3) < float13 && Math.abs(float8 - float6) < float13) {
			if (Math.abs(float4 + float2) < float14 && Math.abs(float7 + float3) < float14 && Math.abs(float8 + float6) < float14 && Math.abs(float1 + float5 + float9 - 3.0F) < float14) {
				this.x = 0.0F;
				this.y = 0.0F;
				this.z = 1.0F;
				this.angle = 0.0F;
				return this;
			} else {
				this.angle = 3.1415927F;
				float15 = (float1 + 1.0F) / 2.0F;
				float float16 = (float5 + 1.0F) / 2.0F;
				float float17 = (float9 + 1.0F) / 2.0F;
				float float18 = (float4 + float2) / 4.0F;
				float float19 = (float7 + float3) / 4.0F;
				float float20 = (float8 + float6) / 4.0F;
				if (float15 > float16 && float15 > float17) {
					this.x = Math.sqrt(float15);
					this.y = float18 / this.x;
					this.z = float19 / this.x;
				} else if (float16 > float17) {
					this.y = Math.sqrt(float16);
					this.x = float18 / this.y;
					this.z = float20 / this.y;
				} else {
					this.z = Math.sqrt(float17);
					this.x = float19 / this.z;
					this.y = float20 / this.z;
				}

				return this;
			}
		} else {
			float15 = Math.sqrt((float6 - float8) * (float6 - float8) + (float7 - float3) * (float7 - float3) + (float2 - float4) * (float2 - float4));
			this.angle = Math.safeAcos((float1 + float5 + float9 - 1.0F) / 2.0F);
			this.x = (float6 - float8) / float15;
			this.y = (float7 - float3) / float15;
			this.z = (float2 - float4) / float15;
			return this;
		}
	}

	public AxisAngle4f set(Matrix3dc matrix3dc) {
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
				this.x = 0.0F;
				this.y = 0.0F;
				this.z = 1.0F;
				this.angle = 0.0F;
				return this;
			} else {
				this.angle = 3.1415927F;
				double15 = (double1 + 1.0) / 2.0;
				double double16 = (double5 + 1.0) / 2.0;
				double double17 = (double9 + 1.0) / 2.0;
				double double18 = (double4 + double2) / 4.0;
				double double19 = (double7 + double3) / 4.0;
				double double20 = (double8 + double6) / 4.0;
				if (double15 > double16 && double15 > double17) {
					this.x = (float)Math.sqrt(double15);
					this.y = (float)(double18 / (double)this.x);
					this.z = (float)(double19 / (double)this.x);
				} else if (double16 > double17) {
					this.y = (float)Math.sqrt(double16);
					this.x = (float)(double18 / (double)this.y);
					this.z = (float)(double20 / (double)this.y);
				} else {
					this.z = (float)Math.sqrt(double17);
					this.x = (float)(double19 / (double)this.z);
					this.y = (float)(double20 / (double)this.z);
				}

				return this;
			}
		} else {
			double15 = Math.sqrt((double6 - double8) * (double6 - double8) + (double7 - double3) * (double7 - double3) + (double2 - double4) * (double2 - double4));
			this.angle = (float)Math.safeAcos((double1 + double5 + double9 - 1.0) / 2.0);
			this.x = (float)((double6 - double8) / double15);
			this.y = (float)((double7 - double3) / double15);
			this.z = (float)((double2 - double4) / double15);
			return this;
		}
	}

	public AxisAngle4f set(Matrix4fc matrix4fc) {
		float float1 = matrix4fc.m00();
		float float2 = matrix4fc.m01();
		float float3 = matrix4fc.m02();
		float float4 = matrix4fc.m10();
		float float5 = matrix4fc.m11();
		float float6 = matrix4fc.m12();
		float float7 = matrix4fc.m20();
		float float8 = matrix4fc.m21();
		float float9 = matrix4fc.m22();
		float float10 = Math.invsqrt(matrix4fc.m00() * matrix4fc.m00() + matrix4fc.m01() * matrix4fc.m01() + matrix4fc.m02() * matrix4fc.m02());
		float float11 = Math.invsqrt(matrix4fc.m10() * matrix4fc.m10() + matrix4fc.m11() * matrix4fc.m11() + matrix4fc.m12() * matrix4fc.m12());
		float float12 = Math.invsqrt(matrix4fc.m20() * matrix4fc.m20() + matrix4fc.m21() * matrix4fc.m21() + matrix4fc.m22() * matrix4fc.m22());
		float1 *= float10;
		float2 *= float10;
		float3 *= float10;
		float4 *= float11;
		float5 *= float11;
		float6 *= float11;
		float7 *= float12;
		float8 *= float12;
		float9 *= float12;
		float float13 = 1.0E-4F;
		float float14 = 0.001F;
		float float15;
		if (Math.abs(float4 - float2) < float13 && Math.abs(float7 - float3) < float13 && Math.abs(float8 - float6) < float13) {
			if (Math.abs(float4 + float2) < float14 && Math.abs(float7 + float3) < float14 && Math.abs(float8 + float6) < float14 && Math.abs(float1 + float5 + float9 - 3.0F) < float14) {
				this.x = 0.0F;
				this.y = 0.0F;
				this.z = 1.0F;
				this.angle = 0.0F;
				return this;
			} else {
				this.angle = 3.1415927F;
				float15 = (float1 + 1.0F) / 2.0F;
				float float16 = (float5 + 1.0F) / 2.0F;
				float float17 = (float9 + 1.0F) / 2.0F;
				float float18 = (float4 + float2) / 4.0F;
				float float19 = (float7 + float3) / 4.0F;
				float float20 = (float8 + float6) / 4.0F;
				if (float15 > float16 && float15 > float17) {
					this.x = Math.sqrt(float15);
					this.y = float18 / this.x;
					this.z = float19 / this.x;
				} else if (float16 > float17) {
					this.y = Math.sqrt(float16);
					this.x = float18 / this.y;
					this.z = float20 / this.y;
				} else {
					this.z = Math.sqrt(float17);
					this.x = float19 / this.z;
					this.y = float20 / this.z;
				}

				return this;
			}
		} else {
			float15 = Math.sqrt((float6 - float8) * (float6 - float8) + (float7 - float3) * (float7 - float3) + (float2 - float4) * (float2 - float4));
			this.angle = Math.safeAcos((float1 + float5 + float9 - 1.0F) / 2.0F);
			this.x = (float6 - float8) / float15;
			this.y = (float7 - float3) / float15;
			this.z = (float2 - float4) / float15;
			return this;
		}
	}

	public AxisAngle4f set(Matrix4x3fc matrix4x3fc) {
		float float1 = matrix4x3fc.m00();
		float float2 = matrix4x3fc.m01();
		float float3 = matrix4x3fc.m02();
		float float4 = matrix4x3fc.m10();
		float float5 = matrix4x3fc.m11();
		float float6 = matrix4x3fc.m12();
		float float7 = matrix4x3fc.m20();
		float float8 = matrix4x3fc.m21();
		float float9 = matrix4x3fc.m22();
		float float10 = Math.invsqrt(matrix4x3fc.m00() * matrix4x3fc.m00() + matrix4x3fc.m01() * matrix4x3fc.m01() + matrix4x3fc.m02() * matrix4x3fc.m02());
		float float11 = Math.invsqrt(matrix4x3fc.m10() * matrix4x3fc.m10() + matrix4x3fc.m11() * matrix4x3fc.m11() + matrix4x3fc.m12() * matrix4x3fc.m12());
		float float12 = Math.invsqrt(matrix4x3fc.m20() * matrix4x3fc.m20() + matrix4x3fc.m21() * matrix4x3fc.m21() + matrix4x3fc.m22() * matrix4x3fc.m22());
		float1 *= float10;
		float2 *= float10;
		float3 *= float10;
		float4 *= float11;
		float5 *= float11;
		float6 *= float11;
		float7 *= float12;
		float8 *= float12;
		float9 *= float12;
		float float13 = 1.0E-4F;
		float float14 = 0.001F;
		float float15;
		if (Math.abs(float4 - float2) < float13 && Math.abs(float7 - float3) < float13 && Math.abs(float8 - float6) < float13) {
			if (Math.abs(float4 + float2) < float14 && Math.abs(float7 + float3) < float14 && Math.abs(float8 + float6) < float14 && Math.abs(float1 + float5 + float9 - 3.0F) < float14) {
				this.x = 0.0F;
				this.y = 0.0F;
				this.z = 1.0F;
				this.angle = 0.0F;
				return this;
			} else {
				this.angle = 3.1415927F;
				float15 = (float1 + 1.0F) / 2.0F;
				float float16 = (float5 + 1.0F) / 2.0F;
				float float17 = (float9 + 1.0F) / 2.0F;
				float float18 = (float4 + float2) / 4.0F;
				float float19 = (float7 + float3) / 4.0F;
				float float20 = (float8 + float6) / 4.0F;
				if (float15 > float16 && float15 > float17) {
					this.x = Math.sqrt(float15);
					this.y = float18 / this.x;
					this.z = float19 / this.x;
				} else if (float16 > float17) {
					this.y = Math.sqrt(float16);
					this.x = float18 / this.y;
					this.z = float20 / this.y;
				} else {
					this.z = Math.sqrt(float17);
					this.x = float19 / this.z;
					this.y = float20 / this.z;
				}

				return this;
			}
		} else {
			float15 = Math.sqrt((float6 - float8) * (float6 - float8) + (float7 - float3) * (float7 - float3) + (float2 - float4) * (float2 - float4));
			this.angle = Math.safeAcos((float1 + float5 + float9 - 1.0F) / 2.0F);
			this.x = (float6 - float8) / float15;
			this.y = (float7 - float3) / float15;
			this.z = (float2 - float4) / float15;
			return this;
		}
	}

	public AxisAngle4f set(Matrix4dc matrix4dc) {
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
				this.x = 0.0F;
				this.y = 0.0F;
				this.z = 1.0F;
				this.angle = 0.0F;
				return this;
			} else {
				this.angle = 3.1415927F;
				double15 = (double1 + 1.0) / 2.0;
				double double16 = (double5 + 1.0) / 2.0;
				double double17 = (double9 + 1.0) / 2.0;
				double double18 = (double4 + double2) / 4.0;
				double double19 = (double7 + double3) / 4.0;
				double double20 = (double8 + double6) / 4.0;
				if (double15 > double16 && double15 > double17) {
					this.x = (float)Math.sqrt(double15);
					this.y = (float)(double18 / (double)this.x);
					this.z = (float)(double19 / (double)this.x);
				} else if (double16 > double17) {
					this.y = (float)Math.sqrt(double16);
					this.x = (float)(double18 / (double)this.y);
					this.z = (float)(double20 / (double)this.y);
				} else {
					this.z = (float)Math.sqrt(double17);
					this.x = (float)(double19 / (double)this.z);
					this.y = (float)(double20 / (double)this.z);
				}

				return this;
			}
		} else {
			double15 = Math.sqrt((double6 - double8) * (double6 - double8) + (double7 - double3) * (double7 - double3) + (double2 - double4) * (double2 - double4));
			this.angle = (float)Math.safeAcos((double1 + double5 + double9 - 1.0) / 2.0);
			this.x = (float)((double6 - double8) / double15);
			this.y = (float)((double7 - double3) / double15);
			this.z = (float)((double2 - double4) / double15);
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
		objectOutput.writeFloat(this.angle);
		objectOutput.writeFloat(this.x);
		objectOutput.writeFloat(this.y);
		objectOutput.writeFloat(this.z);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.angle = objectInput.readFloat();
		this.x = objectInput.readFloat();
		this.y = objectInput.readFloat();
		this.z = objectInput.readFloat();
	}

	public AxisAngle4f normalize() {
		float float1 = Math.invsqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		return this;
	}

	public AxisAngle4f rotate(float float1) {
		this.angle += float1;
		this.angle = (float)(((double)this.angle < 0.0 ? 6.283185307179586 + (double)this.angle % 6.283185307179586 : (double)this.angle) % 6.283185307179586);
		return this;
	}

	public Vector3f transform(Vector3f vector3f) {
		return this.transform((Vector3fc)vector3f, (Vector3f)vector3f);
	}

	public Vector3f transform(Vector3fc vector3fc, Vector3f vector3f) {
		double double1 = (double)Math.sin(this.angle);
		double double2 = Math.cosFromSin(double1, (double)this.angle);
		float float1 = this.x * vector3fc.x() + this.y * vector3fc.y() + this.z * vector3fc.z();
		vector3f.set((float)((double)vector3fc.x() * double2 + double1 * (double)(this.y * vector3fc.z() - this.z * vector3fc.y()) + (1.0 - double2) * (double)float1 * (double)this.x), (float)((double)vector3fc.y() * double2 + double1 * (double)(this.z * vector3fc.x() - this.x * vector3fc.z()) + (1.0 - double2) * (double)float1 * (double)this.y), (float)((double)vector3fc.z() * double2 + double1 * (double)(this.x * vector3fc.y() - this.y * vector3fc.x()) + (1.0 - double2) * (double)float1 * (double)this.z));
		return vector3f;
	}

	public Vector4f transform(Vector4f vector4f) {
		return this.transform((Vector4fc)vector4f, (Vector4f)vector4f);
	}

	public Vector4f transform(Vector4fc vector4fc, Vector4f vector4f) {
		double double1 = (double)Math.sin(this.angle);
		double double2 = Math.cosFromSin(double1, (double)this.angle);
		float float1 = this.x * vector4fc.x() + this.y * vector4fc.y() + this.z * vector4fc.z();
		vector4f.set((float)((double)vector4fc.x() * double2 + double1 * (double)(this.y * vector4fc.z() - this.z * vector4fc.y()) + (1.0 - double2) * (double)float1 * (double)this.x), (float)((double)vector4fc.y() * double2 + double1 * (double)(this.z * vector4fc.x() - this.x * vector4fc.z()) + (1.0 - double2) * (double)float1 * (double)this.y), (float)((double)vector4fc.z() * double2 + double1 * (double)(this.x * vector4fc.y() - this.y * vector4fc.x()) + (1.0 - double2) * (double)float1 * (double)this.z), vector4f.w);
		return vector4f;
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format((double)this.x, numberFormat);
		return "(" + string + " " + Runtime.format((double)this.y, numberFormat) + " " + Runtime.format((double)this.z, numberFormat) + " <| " + Runtime.format((double)this.angle, numberFormat) + ")";
	}

	public int hashCode() {
		byte byte1 = 1;
		float float1 = (float)(((double)this.angle < 0.0 ? 6.283185307179586 + (double)this.angle % 6.283185307179586 : (double)this.angle) % 6.283185307179586);
		int int1 = 31 * byte1 + Float.floatToIntBits(float1);
		int1 = 31 * int1 + Float.floatToIntBits(this.x);
		int1 = 31 * int1 + Float.floatToIntBits(this.y);
		int1 = 31 * int1 + Float.floatToIntBits(this.z);
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
			AxisAngle4f axisAngle4f = (AxisAngle4f)object;
			float float1 = (float)(((double)this.angle < 0.0 ? 6.283185307179586 + (double)this.angle % 6.283185307179586 : (double)this.angle) % 6.283185307179586);
			float float2 = (float)(((double)axisAngle4f.angle < 0.0 ? 6.283185307179586 + (double)axisAngle4f.angle % 6.283185307179586 : (double)axisAngle4f.angle) % 6.283185307179586);
			if (Float.floatToIntBits(float1) != Float.floatToIntBits(float2)) {
				return false;
			} else if (Float.floatToIntBits(this.x) != Float.floatToIntBits(axisAngle4f.x)) {
				return false;
			} else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(axisAngle4f.y)) {
				return false;
			} else {
				return Float.floatToIntBits(this.z) == Float.floatToIntBits(axisAngle4f.z);
			}
		}
	}
}
