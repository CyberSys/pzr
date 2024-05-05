package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Matrix4f implements Externalizable,Matrix4fc {
	private static final long serialVersionUID = 1L;
	float m00;
	float m01;
	float m02;
	float m03;
	float m10;
	float m11;
	float m12;
	float m13;
	float m20;
	float m21;
	float m22;
	float m23;
	float m30;
	float m31;
	float m32;
	float m33;
	int properties;

	public Matrix4f() {
		this._m00(1.0F)._m11(1.0F)._m22(1.0F)._m33(1.0F)._properties(30);
	}

	public Matrix4f(Matrix3fc matrix3fc) {
		this.set(matrix3fc);
	}

	public Matrix4f(Matrix4fc matrix4fc) {
		this.set(matrix4fc);
	}

	public Matrix4f(Matrix4x3fc matrix4x3fc) {
		this.set(matrix4x3fc);
	}

	public Matrix4f(Matrix4dc matrix4dc) {
		this.set(matrix4dc);
	}

	public Matrix4f(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		this._m00(float1)._m01(float2)._m02(float3)._m03(float4)._m10(float5)._m11(float6)._m12(float7)._m13(float8)._m20(float9)._m21(float10)._m22(float11)._m23(float12)._m30(float13)._m31(float14)._m32(float15)._m33(float16).determineProperties();
	}

	public Matrix4f(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
		this.determineProperties();
	}

	public Matrix4f(Vector4fc vector4fc, Vector4fc vector4fc2, Vector4fc vector4fc3, Vector4fc vector4fc4) {
		this.set(vector4fc, vector4fc2, vector4fc3, vector4fc4);
	}

	Matrix4f _properties(int int1) {
		this.properties = int1;
		return this;
	}

	public Matrix4f assume(int int1) {
		this._properties(int1);
		return this;
	}

	public Matrix4f determineProperties() {
		int int1 = 0;
		if (this.m03 == 0.0F && this.m13 == 0.0F) {
			if (this.m23 == 0.0F && this.m33 == 1.0F) {
				int1 |= 2;
				if (this.m00 == 1.0F && this.m01 == 0.0F && this.m02 == 0.0F && this.m10 == 0.0F && this.m11 == 1.0F && this.m12 == 0.0F && this.m20 == 0.0F && this.m21 == 0.0F && this.m22 == 1.0F) {
					int1 |= 24;
					if (this.m30 == 0.0F && this.m31 == 0.0F && this.m32 == 0.0F) {
						int1 |= 4;
					}
				}
			} else if (this.m01 == 0.0F && this.m02 == 0.0F && this.m10 == 0.0F && this.m12 == 0.0F && this.m20 == 0.0F && this.m21 == 0.0F && this.m30 == 0.0F && this.m31 == 0.0F && this.m33 == 0.0F) {
				int1 |= 1;
			}
		}

		this.properties = int1;
		return this;
	}

	public int properties() {
		return this.properties;
	}

	public float m00() {
		return this.m00;
	}

	public float m01() {
		return this.m01;
	}

	public float m02() {
		return this.m02;
	}

	public float m03() {
		return this.m03;
	}

	public float m10() {
		return this.m10;
	}

	public float m11() {
		return this.m11;
	}

	public float m12() {
		return this.m12;
	}

	public float m13() {
		return this.m13;
	}

	public float m20() {
		return this.m20;
	}

	public float m21() {
		return this.m21;
	}

	public float m22() {
		return this.m22;
	}

	public float m23() {
		return this.m23;
	}

	public float m30() {
		return this.m30;
	}

	public float m31() {
		return this.m31;
	}

	public float m32() {
		return this.m32;
	}

	public float m33() {
		return this.m33;
	}

	public Matrix4f m00(float float1) {
		this.m00 = float1;
		this.properties &= -17;
		if (float1 != 1.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4f m01(float float1) {
		this.m01 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4f m02(float float1) {
		this.m02 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4f m03(float float1) {
		this.m03 = float1;
		if (float1 != 0.0F) {
			this.properties = 0;
		}

		return this;
	}

	public Matrix4f m10(float float1) {
		this.m10 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4f m11(float float1) {
		this.m11 = float1;
		this.properties &= -17;
		if (float1 != 1.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4f m12(float float1) {
		this.m12 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4f m13(float float1) {
		this.m13 = float1;
		if (float1 != 0.0F) {
			this.properties = 0;
		}

		return this;
	}

	public Matrix4f m20(float float1) {
		this.m20 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4f m21(float float1) {
		this.m21 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4f m22(float float1) {
		this.m22 = float1;
		this.properties &= -17;
		if (float1 != 1.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4f m23(float float1) {
		this.m23 = float1;
		if (float1 != 0.0F) {
			this.properties &= -31;
		}

		return this;
	}

	public Matrix4f m30(float float1) {
		this.m30 = float1;
		if (float1 != 0.0F) {
			this.properties &= -6;
		}

		return this;
	}

	public Matrix4f m31(float float1) {
		this.m31 = float1;
		if (float1 != 0.0F) {
			this.properties &= -6;
		}

		return this;
	}

	public Matrix4f m32(float float1) {
		this.m32 = float1;
		if (float1 != 0.0F) {
			this.properties &= -6;
		}

		return this;
	}

	public Matrix4f m33(float float1) {
		this.m33 = float1;
		if (float1 != 0.0F) {
			this.properties &= -2;
		}

		if (float1 != 1.0F) {
			this.properties &= -31;
		}

		return this;
	}

	Matrix4f _m00(float float1) {
		this.m00 = float1;
		return this;
	}

	Matrix4f _m01(float float1) {
		this.m01 = float1;
		return this;
	}

	Matrix4f _m02(float float1) {
		this.m02 = float1;
		return this;
	}

	Matrix4f _m03(float float1) {
		this.m03 = float1;
		return this;
	}

	Matrix4f _m10(float float1) {
		this.m10 = float1;
		return this;
	}

	Matrix4f _m11(float float1) {
		this.m11 = float1;
		return this;
	}

	Matrix4f _m12(float float1) {
		this.m12 = float1;
		return this;
	}

	Matrix4f _m13(float float1) {
		this.m13 = float1;
		return this;
	}

	Matrix4f _m20(float float1) {
		this.m20 = float1;
		return this;
	}

	Matrix4f _m21(float float1) {
		this.m21 = float1;
		return this;
	}

	Matrix4f _m22(float float1) {
		this.m22 = float1;
		return this;
	}

	Matrix4f _m23(float float1) {
		this.m23 = float1;
		return this;
	}

	Matrix4f _m30(float float1) {
		this.m30 = float1;
		return this;
	}

	Matrix4f _m31(float float1) {
		this.m31 = float1;
		return this;
	}

	Matrix4f _m32(float float1) {
		this.m32 = float1;
		return this;
	}

	Matrix4f _m33(float float1) {
		this.m33 = float1;
		return this;
	}

	public Matrix4f identity() {
		return (this.properties & 4) != 0 ? this : this._m00(1.0F)._m01(0.0F)._m02(0.0F)._m03(0.0F)._m10(0.0F)._m11(1.0F)._m12(0.0F)._m13(0.0F)._m20(0.0F)._m21(0.0F)._m22(1.0F)._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties(30);
	}

	public Matrix4f set(Matrix4fc matrix4fc) {
		return this._m00(matrix4fc.m00())._m01(matrix4fc.m01())._m02(matrix4fc.m02())._m03(matrix4fc.m03())._m10(matrix4fc.m10())._m11(matrix4fc.m11())._m12(matrix4fc.m12())._m13(matrix4fc.m13())._m20(matrix4fc.m20())._m21(matrix4fc.m21())._m22(matrix4fc.m22())._m23(matrix4fc.m23())._m30(matrix4fc.m30())._m31(matrix4fc.m31())._m32(matrix4fc.m32())._m33(matrix4fc.m33())._properties(matrix4fc.properties());
	}

	public Matrix4f setTransposed(Matrix4fc matrix4fc) {
		return (matrix4fc.properties() & 4) != 0 ? this.identity() : this.setTransposedInternal(matrix4fc);
	}

	private Matrix4f setTransposedInternal(Matrix4fc matrix4fc) {
		float float1 = matrix4fc.m01();
		float float2 = matrix4fc.m21();
		float float3 = matrix4fc.m31();
		float float4 = matrix4fc.m02();
		float float5 = matrix4fc.m12();
		float float6 = matrix4fc.m03();
		float float7 = matrix4fc.m13();
		float float8 = matrix4fc.m23();
		return this._m00(matrix4fc.m00())._m01(matrix4fc.m10())._m02(matrix4fc.m20())._m03(matrix4fc.m30())._m10(float1)._m11(matrix4fc.m11())._m12(float2)._m13(float3)._m20(float4)._m21(float5)._m22(matrix4fc.m22())._m23(matrix4fc.m32())._m30(float6)._m31(float7)._m32(float8)._m33(matrix4fc.m33())._properties(matrix4fc.properties() & 4);
	}

	public Matrix4f set(Matrix4x3fc matrix4x3fc) {
		return this._m00(matrix4x3fc.m00())._m01(matrix4x3fc.m01())._m02(matrix4x3fc.m02())._m03(0.0F)._m10(matrix4x3fc.m10())._m11(matrix4x3fc.m11())._m12(matrix4x3fc.m12())._m13(0.0F)._m20(matrix4x3fc.m20())._m21(matrix4x3fc.m21())._m22(matrix4x3fc.m22())._m23(0.0F)._m30(matrix4x3fc.m30())._m31(matrix4x3fc.m31())._m32(matrix4x3fc.m32())._m33(1.0F)._properties(matrix4x3fc.properties() | 2);
	}

	public Matrix4f set(Matrix4dc matrix4dc) {
		return this._m00((float)matrix4dc.m00())._m01((float)matrix4dc.m01())._m02((float)matrix4dc.m02())._m03((float)matrix4dc.m03())._m10((float)matrix4dc.m10())._m11((float)matrix4dc.m11())._m12((float)matrix4dc.m12())._m13((float)matrix4dc.m13())._m20((float)matrix4dc.m20())._m21((float)matrix4dc.m21())._m22((float)matrix4dc.m22())._m23((float)matrix4dc.m23())._m30((float)matrix4dc.m30())._m31((float)matrix4dc.m31())._m32((float)matrix4dc.m32())._m33((float)matrix4dc.m33())._properties(matrix4dc.properties());
	}

	public Matrix4f set(Matrix3fc matrix3fc) {
		return this._m00(matrix3fc.m00())._m01(matrix3fc.m01())._m02(matrix3fc.m02())._m03(0.0F)._m10(matrix3fc.m10())._m11(matrix3fc.m11())._m12(matrix3fc.m12())._m13(0.0F)._m20(matrix3fc.m20())._m21(matrix3fc.m21())._m22(matrix3fc.m22())._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties(2);
	}

	public Matrix4f set(AxisAngle4f axisAngle4f) {
		float float1 = axisAngle4f.x;
		float float2 = axisAngle4f.y;
		float float3 = axisAngle4f.z;
		float float4 = axisAngle4f.angle;
		double double1 = (double)Math.sqrt(float1 * float1 + float2 * float2 + float3 * float3);
		double1 = 1.0 / double1;
		float1 = (float)((double)float1 * double1);
		float2 = (float)((double)float2 * double1);
		float3 = (float)((double)float3 * double1);
		float float5 = Math.sin(float4);
		float float6 = Math.cosFromSin(float5, float4);
		float float7 = 1.0F - float6;
		this._m00(float6 + float1 * float1 * float7)._m11(float6 + float2 * float2 * float7)._m22(float6 + float3 * float3 * float7);
		float float8 = float1 * float2 * float7;
		float float9 = float3 * float5;
		this._m10(float8 - float9)._m01(float8 + float9);
		float8 = float1 * float3 * float7;
		float9 = float2 * float5;
		this._m20(float8 + float9)._m02(float8 - float9);
		float8 = float2 * float3 * float7;
		float9 = float1 * float5;
		return this._m21(float8 - float9)._m12(float8 + float9)._m03(0.0F)._m13(0.0F)._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties(18);
	}

	public Matrix4f set(AxisAngle4d axisAngle4d) {
		double double1 = axisAngle4d.x;
		double double2 = axisAngle4d.y;
		double double3 = axisAngle4d.z;
		double double4 = axisAngle4d.angle;
		double double5 = Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double5 = 1.0 / double5;
		double1 *= double5;
		double2 *= double5;
		double3 *= double5;
		double double6 = Math.sin(double4);
		double double7 = Math.cosFromSin(double6, double4);
		double double8 = 1.0 - double7;
		this._m00((float)(double7 + double1 * double1 * double8))._m11((float)(double7 + double2 * double2 * double8))._m22((float)(double7 + double3 * double3 * double8));
		double double9 = double1 * double2 * double8;
		double double10 = double3 * double6;
		this._m10((float)(double9 - double10))._m01((float)(double9 + double10));
		double9 = double1 * double3 * double8;
		double10 = double2 * double6;
		this._m20((float)(double9 + double10))._m02((float)(double9 - double10));
		double9 = double2 * double3 * double8;
		double10 = double1 * double6;
		return this._m21((float)(double9 - double10))._m12((float)(double9 + double10))._m03(0.0F)._m13(0.0F)._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties(18);
	}

	public Matrix4f set(Quaternionfc quaternionfc) {
		return this.rotation(quaternionfc);
	}

	public Matrix4f set(Quaterniondc quaterniondc) {
		double double1 = quaterniondc.w() * quaterniondc.w();
		double double2 = quaterniondc.x() * quaterniondc.x();
		double double3 = quaterniondc.y() * quaterniondc.y();
		double double4 = quaterniondc.z() * quaterniondc.z();
		double double5 = quaterniondc.z() * quaterniondc.w();
		double double6 = quaterniondc.x() * quaterniondc.y();
		double double7 = quaterniondc.x() * quaterniondc.z();
		double double8 = quaterniondc.y() * quaterniondc.w();
		double double9 = quaterniondc.y() * quaterniondc.z();
		double double10 = quaterniondc.x() * quaterniondc.w();
		return this._m00((float)(double1 + double2 - double4 - double3))._m01((float)(double6 + double5 + double5 + double6))._m02((float)(double7 - double8 + double7 - double8))._m03(0.0F)._m10((float)(-double5 + double6 - double5 + double6))._m11((float)(double3 - double4 + double1 - double2))._m12((float)(double9 + double9 + double10 + double10))._m13(0.0F)._m20((float)(double8 + double7 + double7 + double8))._m21((float)(double9 + double9 - double10 - double10))._m22((float)(double4 - double3 - double2 + double1))._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties(18);
	}

	public Matrix4f set3x3(Matrix4f matrix4f) {
		MemUtil.INSTANCE.copy3x3(matrix4f, this);
		return this._properties(this.properties & matrix4f.properties & -2);
	}

	public Matrix4f set4x3(Matrix4x3fc matrix4x3fc) {
		return this._m00(matrix4x3fc.m00())._m01(matrix4x3fc.m01())._m02(matrix4x3fc.m02())._m10(matrix4x3fc.m10())._m11(matrix4x3fc.m11())._m12(matrix4x3fc.m12())._m20(matrix4x3fc.m20())._m21(matrix4x3fc.m21())._m22(matrix4x3fc.m22())._m30(matrix4x3fc.m30())._m31(matrix4x3fc.m31())._m32(matrix4x3fc.m32())._properties(this.properties & matrix4x3fc.properties() & -2);
	}

	public Matrix4f set4x3(Matrix4f matrix4f) {
		MemUtil.INSTANCE.copy4x3(matrix4f, this);
		return this._properties(this.properties & matrix4f.properties & -2);
	}

	public Matrix4f mul(Matrix4fc matrix4fc) {
		return this.mul(matrix4fc, this);
	}

	public Matrix4f mul(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.set(matrix4fc);
		} else if ((matrix4fc.properties() & 4) != 0) {
			return matrix4f.set((Matrix4fc)this);
		} else if ((this.properties & 8) != 0 && (matrix4fc.properties() & 2) != 0) {
			return this.mulTranslationAffine(matrix4fc, matrix4f);
		} else if ((this.properties & 2) != 0 && (matrix4fc.properties() & 2) != 0) {
			return this.mulAffine(matrix4fc, matrix4f);
		} else if ((this.properties & 1) != 0 && (matrix4fc.properties() & 2) != 0) {
			return this.mulPerspectiveAffine(matrix4fc, matrix4f);
		} else {
			return (matrix4fc.properties() & 2) != 0 ? this.mulAffineR(matrix4fc, matrix4f) : this.mul0(matrix4fc, matrix4f);
		}
	}

	public Matrix4f mul0(Matrix4fc matrix4fc) {
		return this.mul0(matrix4fc, this);
	}

	public Matrix4f mul0(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		float float1 = Math.fma(this.m00, matrix4fc.m00(), Math.fma(this.m10, matrix4fc.m01(), Math.fma(this.m20, matrix4fc.m02(), this.m30 * matrix4fc.m03())));
		float float2 = Math.fma(this.m01, matrix4fc.m00(), Math.fma(this.m11, matrix4fc.m01(), Math.fma(this.m21, matrix4fc.m02(), this.m31 * matrix4fc.m03())));
		float float3 = Math.fma(this.m02, matrix4fc.m00(), Math.fma(this.m12, matrix4fc.m01(), Math.fma(this.m22, matrix4fc.m02(), this.m32 * matrix4fc.m03())));
		float float4 = Math.fma(this.m03, matrix4fc.m00(), Math.fma(this.m13, matrix4fc.m01(), Math.fma(this.m23, matrix4fc.m02(), this.m33 * matrix4fc.m03())));
		float float5 = Math.fma(this.m00, matrix4fc.m10(), Math.fma(this.m10, matrix4fc.m11(), Math.fma(this.m20, matrix4fc.m12(), this.m30 * matrix4fc.m13())));
		float float6 = Math.fma(this.m01, matrix4fc.m10(), Math.fma(this.m11, matrix4fc.m11(), Math.fma(this.m21, matrix4fc.m12(), this.m31 * matrix4fc.m13())));
		float float7 = Math.fma(this.m02, matrix4fc.m10(), Math.fma(this.m12, matrix4fc.m11(), Math.fma(this.m22, matrix4fc.m12(), this.m32 * matrix4fc.m13())));
		float float8 = Math.fma(this.m03, matrix4fc.m10(), Math.fma(this.m13, matrix4fc.m11(), Math.fma(this.m23, matrix4fc.m12(), this.m33 * matrix4fc.m13())));
		float float9 = Math.fma(this.m00, matrix4fc.m20(), Math.fma(this.m10, matrix4fc.m21(), Math.fma(this.m20, matrix4fc.m22(), this.m30 * matrix4fc.m23())));
		float float10 = Math.fma(this.m01, matrix4fc.m20(), Math.fma(this.m11, matrix4fc.m21(), Math.fma(this.m21, matrix4fc.m22(), this.m31 * matrix4fc.m23())));
		float float11 = Math.fma(this.m02, matrix4fc.m20(), Math.fma(this.m12, matrix4fc.m21(), Math.fma(this.m22, matrix4fc.m22(), this.m32 * matrix4fc.m23())));
		float float12 = Math.fma(this.m03, matrix4fc.m20(), Math.fma(this.m13, matrix4fc.m21(), Math.fma(this.m23, matrix4fc.m22(), this.m33 * matrix4fc.m23())));
		float float13 = Math.fma(this.m00, matrix4fc.m30(), Math.fma(this.m10, matrix4fc.m31(), Math.fma(this.m20, matrix4fc.m32(), this.m30 * matrix4fc.m33())));
		float float14 = Math.fma(this.m01, matrix4fc.m30(), Math.fma(this.m11, matrix4fc.m31(), Math.fma(this.m21, matrix4fc.m32(), this.m31 * matrix4fc.m33())));
		float float15 = Math.fma(this.m02, matrix4fc.m30(), Math.fma(this.m12, matrix4fc.m31(), Math.fma(this.m22, matrix4fc.m32(), this.m32 * matrix4fc.m33())));
		float float16 = Math.fma(this.m03, matrix4fc.m30(), Math.fma(this.m13, matrix4fc.m31(), Math.fma(this.m23, matrix4fc.m32(), this.m33 * matrix4fc.m33())));
		return matrix4f._m00(float1)._m01(float2)._m02(float3)._m03(float4)._m10(float5)._m11(float6)._m12(float7)._m13(float8)._m20(float9)._m21(float10)._m22(float11)._m23(float12)._m30(float13)._m31(float14)._m32(float15)._m33(float16)._properties(0);
	}

	public Matrix4f mul(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		return this.mul(float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16, this);
	}

	public Matrix4f mul(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.set(float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16);
		} else {
			return (this.properties & 2) != 0 ? this.mulAffineL(float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16, matrix4f) : this.mulGeneric(float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16, matrix4f);
		}
	}

	private Matrix4f mulAffineL(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, Matrix4f matrix4f) {
		float float17 = Math.fma(this.m00, float1, Math.fma(this.m10, float2, Math.fma(this.m20, float3, this.m30 * float4)));
		float float18 = Math.fma(this.m01, float1, Math.fma(this.m11, float2, Math.fma(this.m21, float3, this.m31 * float4)));
		float float19 = Math.fma(this.m02, float1, Math.fma(this.m12, float2, Math.fma(this.m22, float3, this.m32 * float4)));
		float float20 = Math.fma(this.m00, float5, Math.fma(this.m10, float6, Math.fma(this.m20, float7, this.m30 * float8)));
		float float21 = Math.fma(this.m01, float5, Math.fma(this.m11, float6, Math.fma(this.m21, float7, this.m31 * float8)));
		float float22 = Math.fma(this.m02, float5, Math.fma(this.m12, float6, Math.fma(this.m22, float7, this.m32 * float8)));
		float float23 = Math.fma(this.m00, float9, Math.fma(this.m10, float10, Math.fma(this.m20, float11, this.m30 * float12)));
		float float24 = Math.fma(this.m01, float9, Math.fma(this.m11, float10, Math.fma(this.m21, float11, this.m31 * float12)));
		float float25 = Math.fma(this.m02, float9, Math.fma(this.m12, float10, Math.fma(this.m22, float11, this.m32 * float12)));
		float float26 = Math.fma(this.m00, float13, Math.fma(this.m10, float14, Math.fma(this.m20, float15, this.m30 * float16)));
		float float27 = Math.fma(this.m01, float13, Math.fma(this.m11, float14, Math.fma(this.m21, float15, this.m31 * float16)));
		float float28 = Math.fma(this.m02, float13, Math.fma(this.m12, float14, Math.fma(this.m22, float15, this.m32 * float16)));
		return matrix4f._m00(float17)._m01(float18)._m02(float19)._m03(float4)._m10(float20)._m11(float21)._m12(float22)._m13(float8)._m20(float23)._m21(float24)._m22(float25)._m23(float12)._m30(float26)._m31(float27)._m32(float28)._m33(float16)._properties(2);
	}

	private Matrix4f mulGeneric(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, Matrix4f matrix4f) {
		float float17 = Math.fma(this.m00, float1, Math.fma(this.m10, float2, Math.fma(this.m20, float3, this.m30 * float4)));
		float float18 = Math.fma(this.m01, float1, Math.fma(this.m11, float2, Math.fma(this.m21, float3, this.m31 * float4)));
		float float19 = Math.fma(this.m02, float1, Math.fma(this.m12, float2, Math.fma(this.m22, float3, this.m32 * float4)));
		float float20 = Math.fma(this.m03, float1, Math.fma(this.m13, float2, Math.fma(this.m23, float3, this.m33 * float4)));
		float float21 = Math.fma(this.m00, float5, Math.fma(this.m10, float6, Math.fma(this.m20, float7, this.m30 * float8)));
		float float22 = Math.fma(this.m01, float5, Math.fma(this.m11, float6, Math.fma(this.m21, float7, this.m31 * float8)));
		float float23 = Math.fma(this.m02, float5, Math.fma(this.m12, float6, Math.fma(this.m22, float7, this.m32 * float8)));
		float float24 = Math.fma(this.m03, float5, Math.fma(this.m13, float6, Math.fma(this.m23, float7, this.m33 * float8)));
		float float25 = Math.fma(this.m00, float9, Math.fma(this.m10, float10, Math.fma(this.m20, float11, this.m30 * float12)));
		float float26 = Math.fma(this.m01, float9, Math.fma(this.m11, float10, Math.fma(this.m21, float11, this.m31 * float12)));
		float float27 = Math.fma(this.m02, float9, Math.fma(this.m12, float10, Math.fma(this.m22, float11, this.m32 * float12)));
		float float28 = Math.fma(this.m03, float9, Math.fma(this.m13, float10, Math.fma(this.m23, float11, this.m33 * float12)));
		float float29 = Math.fma(this.m00, float13, Math.fma(this.m10, float14, Math.fma(this.m20, float15, this.m30 * float16)));
		float float30 = Math.fma(this.m01, float13, Math.fma(this.m11, float14, Math.fma(this.m21, float15, this.m31 * float16)));
		float float31 = Math.fma(this.m02, float13, Math.fma(this.m12, float14, Math.fma(this.m22, float15, this.m32 * float16)));
		float float32 = Math.fma(this.m03, float13, Math.fma(this.m13, float14, Math.fma(this.m23, float15, this.m33 * float16)));
		return matrix4f._m00(float17)._m01(float18)._m02(float19)._m03(float20)._m10(float21)._m11(float22)._m12(float23)._m13(float24)._m20(float25)._m21(float26)._m22(float27)._m23(float28)._m30(float29)._m31(float30)._m32(float31)._m33(float32)._properties(0);
	}

	public Matrix4f mul3x3(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		return this.mul3x3(float1, float2, float3, float4, float5, float6, float7, float8, float9, this);
	}

	public Matrix4f mul3x3(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.set(float1, float2, float3, 0.0F, float4, float5, float6, 0.0F, float7, float8, float9, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F) : this.mulGeneric3x3(float1, float2, float3, float4, float5, float6, float7, float8, float9, matrix4f);
	}

	private Matrix4f mulGeneric3x3(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f) {
		float float10 = Math.fma(this.m00, float1, Math.fma(this.m10, float2, this.m20 * float3));
		float float11 = Math.fma(this.m01, float1, Math.fma(this.m11, float2, this.m21 * float3));
		float float12 = Math.fma(this.m02, float1, Math.fma(this.m12, float2, this.m22 * float3));
		float float13 = Math.fma(this.m03, float1, Math.fma(this.m13, float2, this.m23 * float3));
		float float14 = Math.fma(this.m00, float4, Math.fma(this.m10, float5, this.m20 * float6));
		float float15 = Math.fma(this.m01, float4, Math.fma(this.m11, float5, this.m21 * float6));
		float float16 = Math.fma(this.m02, float4, Math.fma(this.m12, float5, this.m22 * float6));
		float float17 = Math.fma(this.m03, float4, Math.fma(this.m13, float5, this.m23 * float6));
		float float18 = Math.fma(this.m00, float7, Math.fma(this.m10, float8, this.m20 * float9));
		float float19 = Math.fma(this.m01, float7, Math.fma(this.m11, float8, this.m21 * float9));
		float float20 = Math.fma(this.m02, float7, Math.fma(this.m12, float8, this.m22 * float9));
		float float21 = Math.fma(this.m03, float7, Math.fma(this.m13, float8, this.m23 * float9));
		return matrix4f._m00(float10)._m01(float11)._m02(float12)._m03(float13)._m10(float14)._m11(float15)._m12(float16)._m13(float17)._m20(float18)._m21(float19)._m22(float20)._m23(float21)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & 2);
	}

	public Matrix4f mulLocal(Matrix4fc matrix4fc) {
		return this.mulLocal(matrix4fc, this);
	}

	public Matrix4f mulLocal(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.set(matrix4fc);
		} else if ((matrix4fc.properties() & 4) != 0) {
			return matrix4f.set((Matrix4fc)this);
		} else {
			return (this.properties & 2) != 0 && (matrix4fc.properties() & 2) != 0 ? this.mulLocalAffine(matrix4fc, matrix4f) : this.mulLocalGeneric(matrix4fc, matrix4f);
		}
	}

	private Matrix4f mulLocalGeneric(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		float float1 = Math.fma(matrix4fc.m00(), this.m00, Math.fma(matrix4fc.m10(), this.m01, Math.fma(matrix4fc.m20(), this.m02, matrix4fc.m30() * this.m03)));
		float float2 = Math.fma(matrix4fc.m01(), this.m00, Math.fma(matrix4fc.m11(), this.m01, Math.fma(matrix4fc.m21(), this.m02, matrix4fc.m31() * this.m03)));
		float float3 = Math.fma(matrix4fc.m02(), this.m00, Math.fma(matrix4fc.m12(), this.m01, Math.fma(matrix4fc.m22(), this.m02, matrix4fc.m32() * this.m03)));
		float float4 = Math.fma(matrix4fc.m03(), this.m00, Math.fma(matrix4fc.m13(), this.m01, Math.fma(matrix4fc.m23(), this.m02, matrix4fc.m33() * this.m03)));
		float float5 = Math.fma(matrix4fc.m00(), this.m10, Math.fma(matrix4fc.m10(), this.m11, Math.fma(matrix4fc.m20(), this.m12, matrix4fc.m30() * this.m13)));
		float float6 = Math.fma(matrix4fc.m01(), this.m10, Math.fma(matrix4fc.m11(), this.m11, Math.fma(matrix4fc.m21(), this.m12, matrix4fc.m31() * this.m13)));
		float float7 = Math.fma(matrix4fc.m02(), this.m10, Math.fma(matrix4fc.m12(), this.m11, Math.fma(matrix4fc.m22(), this.m12, matrix4fc.m32() * this.m13)));
		float float8 = Math.fma(matrix4fc.m03(), this.m10, Math.fma(matrix4fc.m13(), this.m11, Math.fma(matrix4fc.m23(), this.m12, matrix4fc.m33() * this.m13)));
		float float9 = Math.fma(matrix4fc.m00(), this.m20, Math.fma(matrix4fc.m10(), this.m21, Math.fma(matrix4fc.m20(), this.m22, matrix4fc.m30() * this.m23)));
		float float10 = Math.fma(matrix4fc.m01(), this.m20, Math.fma(matrix4fc.m11(), this.m21, Math.fma(matrix4fc.m21(), this.m22, matrix4fc.m31() * this.m23)));
		float float11 = Math.fma(matrix4fc.m02(), this.m20, Math.fma(matrix4fc.m12(), this.m21, Math.fma(matrix4fc.m22(), this.m22, matrix4fc.m32() * this.m23)));
		float float12 = Math.fma(matrix4fc.m03(), this.m20, Math.fma(matrix4fc.m13(), this.m21, Math.fma(matrix4fc.m23(), this.m22, matrix4fc.m33() * this.m23)));
		float float13 = Math.fma(matrix4fc.m00(), this.m30, Math.fma(matrix4fc.m10(), this.m31, Math.fma(matrix4fc.m20(), this.m32, matrix4fc.m30() * this.m33)));
		float float14 = Math.fma(matrix4fc.m01(), this.m30, Math.fma(matrix4fc.m11(), this.m31, Math.fma(matrix4fc.m21(), this.m32, matrix4fc.m31() * this.m33)));
		float float15 = Math.fma(matrix4fc.m02(), this.m30, Math.fma(matrix4fc.m12(), this.m31, Math.fma(matrix4fc.m22(), this.m32, matrix4fc.m32() * this.m33)));
		float float16 = Math.fma(matrix4fc.m03(), this.m30, Math.fma(matrix4fc.m13(), this.m31, Math.fma(matrix4fc.m23(), this.m32, matrix4fc.m33() * this.m33)));
		return matrix4f._m00(float1)._m01(float2)._m02(float3)._m03(float4)._m10(float5)._m11(float6)._m12(float7)._m13(float8)._m20(float9)._m21(float10)._m22(float11)._m23(float12)._m30(float13)._m31(float14)._m32(float15)._m33(float16)._properties(0);
	}

	public Matrix4f mulLocalAffine(Matrix4fc matrix4fc) {
		return this.mulLocalAffine(matrix4fc, this);
	}

	public Matrix4f mulLocalAffine(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		float float1 = matrix4fc.m00() * this.m00 + matrix4fc.m10() * this.m01 + matrix4fc.m20() * this.m02;
		float float2 = matrix4fc.m01() * this.m00 + matrix4fc.m11() * this.m01 + matrix4fc.m21() * this.m02;
		float float3 = matrix4fc.m02() * this.m00 + matrix4fc.m12() * this.m01 + matrix4fc.m22() * this.m02;
		float float4 = matrix4fc.m03();
		float float5 = matrix4fc.m00() * this.m10 + matrix4fc.m10() * this.m11 + matrix4fc.m20() * this.m12;
		float float6 = matrix4fc.m01() * this.m10 + matrix4fc.m11() * this.m11 + matrix4fc.m21() * this.m12;
		float float7 = matrix4fc.m02() * this.m10 + matrix4fc.m12() * this.m11 + matrix4fc.m22() * this.m12;
		float float8 = matrix4fc.m13();
		float float9 = matrix4fc.m00() * this.m20 + matrix4fc.m10() * this.m21 + matrix4fc.m20() * this.m22;
		float float10 = matrix4fc.m01() * this.m20 + matrix4fc.m11() * this.m21 + matrix4fc.m21() * this.m22;
		float float11 = matrix4fc.m02() * this.m20 + matrix4fc.m12() * this.m21 + matrix4fc.m22() * this.m22;
		float float12 = matrix4fc.m23();
		float float13 = matrix4fc.m00() * this.m30 + matrix4fc.m10() * this.m31 + matrix4fc.m20() * this.m32 + matrix4fc.m30();
		float float14 = matrix4fc.m01() * this.m30 + matrix4fc.m11() * this.m31 + matrix4fc.m21() * this.m32 + matrix4fc.m31();
		float float15 = matrix4fc.m02() * this.m30 + matrix4fc.m12() * this.m31 + matrix4fc.m22() * this.m32 + matrix4fc.m32();
		float float16 = matrix4fc.m33();
		return matrix4f._m00(float1)._m01(float2)._m02(float3)._m03(float4)._m10(float5)._m11(float6)._m12(float7)._m13(float8)._m20(float9)._m21(float10)._m22(float11)._m23(float12)._m30(float13)._m31(float14)._m32(float15)._m33(float16)._properties(2 | this.properties() & matrix4fc.properties() & 16);
	}

	public Matrix4f mul(Matrix4x3fc matrix4x3fc) {
		return this.mul(matrix4x3fc, this);
	}

	public Matrix4f mul(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.set(matrix4x3fc);
		} else if ((matrix4x3fc.properties() & 4) != 0) {
			return matrix4f.set((Matrix4fc)this);
		} else if ((this.properties & 8) != 0) {
			return this.mulTranslation(matrix4x3fc, matrix4f);
		} else if ((this.properties & 2) != 0) {
			return this.mulAffine(matrix4x3fc, matrix4f);
		} else {
			return (this.properties & 1) != 0 ? this.mulPerspectiveAffine(matrix4x3fc, matrix4f) : this.mulGeneric(matrix4x3fc, matrix4f);
		}
	}

	private Matrix4f mulTranslation(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f) {
		return matrix4f._m00(matrix4x3fc.m00())._m01(matrix4x3fc.m01())._m02(matrix4x3fc.m02())._m03(this.m03)._m10(matrix4x3fc.m10())._m11(matrix4x3fc.m11())._m12(matrix4x3fc.m12())._m13(this.m13)._m20(matrix4x3fc.m20())._m21(matrix4x3fc.m21())._m22(matrix4x3fc.m22())._m23(this.m23)._m30(matrix4x3fc.m30() + this.m30)._m31(matrix4x3fc.m31() + this.m31)._m32(matrix4x3fc.m32() + this.m32)._m33(this.m33)._properties(2 | matrix4x3fc.properties() & 16);
	}

	private Matrix4f mulAffine(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f) {
		float float1 = this.m00;
		float float2 = this.m01;
		float float3 = this.m02;
		float float4 = this.m10;
		float float5 = this.m11;
		float float6 = this.m12;
		float float7 = this.m20;
		float float8 = this.m21;
		float float9 = this.m22;
		float float10 = matrix4x3fc.m00();
		float float11 = matrix4x3fc.m01();
		float float12 = matrix4x3fc.m02();
		float float13 = matrix4x3fc.m10();
		float float14 = matrix4x3fc.m11();
		float float15 = matrix4x3fc.m12();
		float float16 = matrix4x3fc.m20();
		float float17 = matrix4x3fc.m21();
		float float18 = matrix4x3fc.m22();
		float float19 = matrix4x3fc.m30();
		float float20 = matrix4x3fc.m31();
		float float21 = matrix4x3fc.m32();
		return matrix4f._m00(Math.fma(float1, float10, Math.fma(float4, float11, float7 * float12)))._m01(Math.fma(float2, float10, Math.fma(float5, float11, float8 * float12)))._m02(Math.fma(float3, float10, Math.fma(float6, float11, float9 * float12)))._m03(this.m03)._m10(Math.fma(float1, float13, Math.fma(float4, float14, float7 * float15)))._m11(Math.fma(float2, float13, Math.fma(float5, float14, float8 * float15)))._m12(Math.fma(float3, float13, Math.fma(float6, float14, float9 * float15)))._m13(this.m13)._m20(Math.fma(float1, float16, Math.fma(float4, float17, float7 * float18)))._m21(Math.fma(float2, float16, Math.fma(float5, float17, float8 * float18)))._m22(Math.fma(float3, float16, Math.fma(float6, float17, float9 * float18)))._m23(this.m23)._m30(Math.fma(float1, float19, Math.fma(float4, float20, Math.fma(float7, float21, this.m30))))._m31(Math.fma(float2, float19, Math.fma(float5, float20, Math.fma(float8, float21, this.m31))))._m32(Math.fma(float3, float19, Math.fma(float6, float20, Math.fma(float9, float21, this.m32))))._m33(this.m33)._properties(2 | this.properties & matrix4x3fc.properties() & 16);
	}

	private Matrix4f mulGeneric(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f) {
		float float1 = Math.fma(this.m00, matrix4x3fc.m00(), Math.fma(this.m10, matrix4x3fc.m01(), this.m20 * matrix4x3fc.m02()));
		float float2 = Math.fma(this.m01, matrix4x3fc.m00(), Math.fma(this.m11, matrix4x3fc.m01(), this.m21 * matrix4x3fc.m02()));
		float float3 = Math.fma(this.m02, matrix4x3fc.m00(), Math.fma(this.m12, matrix4x3fc.m01(), this.m22 * matrix4x3fc.m02()));
		float float4 = Math.fma(this.m03, matrix4x3fc.m00(), Math.fma(this.m13, matrix4x3fc.m01(), this.m23 * matrix4x3fc.m02()));
		float float5 = Math.fma(this.m00, matrix4x3fc.m10(), Math.fma(this.m10, matrix4x3fc.m11(), this.m20 * matrix4x3fc.m12()));
		float float6 = Math.fma(this.m01, matrix4x3fc.m10(), Math.fma(this.m11, matrix4x3fc.m11(), this.m21 * matrix4x3fc.m12()));
		float float7 = Math.fma(this.m02, matrix4x3fc.m10(), Math.fma(this.m12, matrix4x3fc.m11(), this.m22 * matrix4x3fc.m12()));
		float float8 = Math.fma(this.m03, matrix4x3fc.m10(), Math.fma(this.m13, matrix4x3fc.m11(), this.m23 * matrix4x3fc.m12()));
		float float9 = Math.fma(this.m00, matrix4x3fc.m20(), Math.fma(this.m10, matrix4x3fc.m21(), this.m20 * matrix4x3fc.m22()));
		float float10 = Math.fma(this.m01, matrix4x3fc.m20(), Math.fma(this.m11, matrix4x3fc.m21(), this.m21 * matrix4x3fc.m22()));
		float float11 = Math.fma(this.m02, matrix4x3fc.m20(), Math.fma(this.m12, matrix4x3fc.m21(), this.m22 * matrix4x3fc.m22()));
		float float12 = Math.fma(this.m03, matrix4x3fc.m20(), Math.fma(this.m13, matrix4x3fc.m21(), this.m23 * matrix4x3fc.m22()));
		float float13 = Math.fma(this.m00, matrix4x3fc.m30(), Math.fma(this.m10, matrix4x3fc.m31(), Math.fma(this.m20, matrix4x3fc.m32(), this.m30)));
		float float14 = Math.fma(this.m01, matrix4x3fc.m30(), Math.fma(this.m11, matrix4x3fc.m31(), Math.fma(this.m21, matrix4x3fc.m32(), this.m31)));
		float float15 = Math.fma(this.m02, matrix4x3fc.m30(), Math.fma(this.m12, matrix4x3fc.m31(), Math.fma(this.m22, matrix4x3fc.m32(), this.m32)));
		float float16 = Math.fma(this.m03, matrix4x3fc.m30(), Math.fma(this.m13, matrix4x3fc.m31(), Math.fma(this.m23, matrix4x3fc.m32(), this.m33)));
		return matrix4f._m00(float1)._m01(float2)._m02(float3)._m03(float4)._m10(float5)._m11(float6)._m12(float7)._m13(float8)._m20(float9)._m21(float10)._m22(float11)._m23(float12)._m30(float13)._m31(float14)._m32(float15)._m33(float16)._properties(this.properties & -30);
	}

	public Matrix4f mul(Matrix3x2fc matrix3x2fc) {
		return this.mul(matrix3x2fc, this);
	}

	public Matrix4f mul(Matrix3x2fc matrix3x2fc, Matrix4f matrix4f) {
		float float1 = this.m00 * matrix3x2fc.m00() + this.m10 * matrix3x2fc.m01();
		float float2 = this.m01 * matrix3x2fc.m00() + this.m11 * matrix3x2fc.m01();
		float float3 = this.m02 * matrix3x2fc.m00() + this.m12 * matrix3x2fc.m01();
		float float4 = this.m03 * matrix3x2fc.m00() + this.m13 * matrix3x2fc.m01();
		float float5 = this.m00 * matrix3x2fc.m10() + this.m10 * matrix3x2fc.m11();
		float float6 = this.m01 * matrix3x2fc.m10() + this.m11 * matrix3x2fc.m11();
		float float7 = this.m02 * matrix3x2fc.m10() + this.m12 * matrix3x2fc.m11();
		float float8 = this.m03 * matrix3x2fc.m10() + this.m13 * matrix3x2fc.m11();
		float float9 = this.m00 * matrix3x2fc.m20() + this.m10 * matrix3x2fc.m21() + this.m30;
		float float10 = this.m01 * matrix3x2fc.m20() + this.m11 * matrix3x2fc.m21() + this.m31;
		float float11 = this.m02 * matrix3x2fc.m20() + this.m12 * matrix3x2fc.m21() + this.m32;
		float float12 = this.m03 * matrix3x2fc.m20() + this.m13 * matrix3x2fc.m21() + this.m33;
		return matrix4f._m00(float1)._m01(float2)._m02(float3)._m03(float4)._m10(float5)._m11(float6)._m12(float7)._m13(float8)._m20(this.m20)._m21(this.m21)._m22(this.m22)._m23(this.m23)._m30(float9)._m31(float10)._m32(float11)._m33(float12)._properties(this.properties & -30);
	}

	public Matrix4f mulPerspectiveAffine(Matrix4fc matrix4fc) {
		return this.mulPerspectiveAffine(matrix4fc, this);
	}

	public Matrix4f mulPerspectiveAffine(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		float float1 = this.m00 * matrix4fc.m00();
		float float2 = this.m11 * matrix4fc.m01();
		float float3 = this.m22 * matrix4fc.m02();
		float float4 = this.m23 * matrix4fc.m02();
		float float5 = this.m00 * matrix4fc.m10();
		float float6 = this.m11 * matrix4fc.m11();
		float float7 = this.m22 * matrix4fc.m12();
		float float8 = this.m23 * matrix4fc.m12();
		float float9 = this.m00 * matrix4fc.m20();
		float float10 = this.m11 * matrix4fc.m21();
		float float11 = this.m22 * matrix4fc.m22();
		float float12 = this.m23 * matrix4fc.m22();
		float float13 = this.m00 * matrix4fc.m30();
		float float14 = this.m11 * matrix4fc.m31();
		float float15 = this.m22 * matrix4fc.m32() + this.m32;
		float float16 = this.m23 * matrix4fc.m32();
		return matrix4f._m00(float1)._m01(float2)._m02(float3)._m03(float4)._m10(float5)._m11(float6)._m12(float7)._m13(float8)._m20(float9)._m21(float10)._m22(float11)._m23(float12)._m30(float13)._m31(float14)._m32(float15)._m33(float16)._properties(0);
	}

	public Matrix4f mulPerspectiveAffine(Matrix4x3fc matrix4x3fc) {
		return this.mulPerspectiveAffine(matrix4x3fc, this);
	}

	public Matrix4f mulPerspectiveAffine(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f) {
		float float1 = this.m00;
		float float2 = this.m11;
		float float3 = this.m22;
		float float4 = this.m23;
		return matrix4f._m00(float1 * matrix4x3fc.m00())._m01(float2 * matrix4x3fc.m01())._m02(float3 * matrix4x3fc.m02())._m03(float4 * matrix4x3fc.m02())._m10(float1 * matrix4x3fc.m10())._m11(float2 * matrix4x3fc.m11())._m12(float3 * matrix4x3fc.m12())._m13(float4 * matrix4x3fc.m12())._m20(float1 * matrix4x3fc.m20())._m21(float2 * matrix4x3fc.m21())._m22(float3 * matrix4x3fc.m22())._m23(float4 * matrix4x3fc.m22())._m30(float1 * matrix4x3fc.m30())._m31(float2 * matrix4x3fc.m31())._m32(float3 * matrix4x3fc.m32() + this.m32)._m33(float4 * matrix4x3fc.m32())._properties(0);
	}

	public Matrix4f mulAffineR(Matrix4fc matrix4fc) {
		return this.mulAffineR(matrix4fc, this);
	}

	public Matrix4f mulAffineR(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		float float1 = Math.fma(this.m00, matrix4fc.m00(), Math.fma(this.m10, matrix4fc.m01(), this.m20 * matrix4fc.m02()));
		float float2 = Math.fma(this.m01, matrix4fc.m00(), Math.fma(this.m11, matrix4fc.m01(), this.m21 * matrix4fc.m02()));
		float float3 = Math.fma(this.m02, matrix4fc.m00(), Math.fma(this.m12, matrix4fc.m01(), this.m22 * matrix4fc.m02()));
		float float4 = Math.fma(this.m03, matrix4fc.m00(), Math.fma(this.m13, matrix4fc.m01(), this.m23 * matrix4fc.m02()));
		float float5 = Math.fma(this.m00, matrix4fc.m10(), Math.fma(this.m10, matrix4fc.m11(), this.m20 * matrix4fc.m12()));
		float float6 = Math.fma(this.m01, matrix4fc.m10(), Math.fma(this.m11, matrix4fc.m11(), this.m21 * matrix4fc.m12()));
		float float7 = Math.fma(this.m02, matrix4fc.m10(), Math.fma(this.m12, matrix4fc.m11(), this.m22 * matrix4fc.m12()));
		float float8 = Math.fma(this.m03, matrix4fc.m10(), Math.fma(this.m13, matrix4fc.m11(), this.m23 * matrix4fc.m12()));
		float float9 = Math.fma(this.m00, matrix4fc.m20(), Math.fma(this.m10, matrix4fc.m21(), this.m20 * matrix4fc.m22()));
		float float10 = Math.fma(this.m01, matrix4fc.m20(), Math.fma(this.m11, matrix4fc.m21(), this.m21 * matrix4fc.m22()));
		float float11 = Math.fma(this.m02, matrix4fc.m20(), Math.fma(this.m12, matrix4fc.m21(), this.m22 * matrix4fc.m22()));
		float float12 = Math.fma(this.m03, matrix4fc.m20(), Math.fma(this.m13, matrix4fc.m21(), this.m23 * matrix4fc.m22()));
		float float13 = Math.fma(this.m00, matrix4fc.m30(), Math.fma(this.m10, matrix4fc.m31(), Math.fma(this.m20, matrix4fc.m32(), this.m30)));
		float float14 = Math.fma(this.m01, matrix4fc.m30(), Math.fma(this.m11, matrix4fc.m31(), Math.fma(this.m21, matrix4fc.m32(), this.m31)));
		float float15 = Math.fma(this.m02, matrix4fc.m30(), Math.fma(this.m12, matrix4fc.m31(), Math.fma(this.m22, matrix4fc.m32(), this.m32)));
		float float16 = Math.fma(this.m03, matrix4fc.m30(), Math.fma(this.m13, matrix4fc.m31(), Math.fma(this.m23, matrix4fc.m32(), this.m33)));
		return matrix4f._m00(float1)._m01(float2)._m02(float3)._m03(float4)._m10(float5)._m11(float6)._m12(float7)._m13(float8)._m20(float9)._m21(float10)._m22(float11)._m23(float12)._m30(float13)._m31(float14)._m32(float15)._m33(float16)._properties(this.properties & -30);
	}

	public Matrix4f mulAffine(Matrix4fc matrix4fc) {
		return this.mulAffine(matrix4fc, this);
	}

	public Matrix4f mulAffine(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		float float1 = this.m00;
		float float2 = this.m01;
		float float3 = this.m02;
		float float4 = this.m10;
		float float5 = this.m11;
		float float6 = this.m12;
		float float7 = this.m20;
		float float8 = this.m21;
		float float9 = this.m22;
		float float10 = matrix4fc.m00();
		float float11 = matrix4fc.m01();
		float float12 = matrix4fc.m02();
		float float13 = matrix4fc.m10();
		float float14 = matrix4fc.m11();
		float float15 = matrix4fc.m12();
		float float16 = matrix4fc.m20();
		float float17 = matrix4fc.m21();
		float float18 = matrix4fc.m22();
		float float19 = matrix4fc.m30();
		float float20 = matrix4fc.m31();
		float float21 = matrix4fc.m32();
		return matrix4f._m00(Math.fma(float1, float10, Math.fma(float4, float11, float7 * float12)))._m01(Math.fma(float2, float10, Math.fma(float5, float11, float8 * float12)))._m02(Math.fma(float3, float10, Math.fma(float6, float11, float9 * float12)))._m03(this.m03)._m10(Math.fma(float1, float13, Math.fma(float4, float14, float7 * float15)))._m11(Math.fma(float2, float13, Math.fma(float5, float14, float8 * float15)))._m12(Math.fma(float3, float13, Math.fma(float6, float14, float9 * float15)))._m13(this.m13)._m20(Math.fma(float1, float16, Math.fma(float4, float17, float7 * float18)))._m21(Math.fma(float2, float16, Math.fma(float5, float17, float8 * float18)))._m22(Math.fma(float3, float16, Math.fma(float6, float17, float9 * float18)))._m23(this.m23)._m30(Math.fma(float1, float19, Math.fma(float4, float20, Math.fma(float7, float21, this.m30))))._m31(Math.fma(float2, float19, Math.fma(float5, float20, Math.fma(float8, float21, this.m31))))._m32(Math.fma(float3, float19, Math.fma(float6, float20, Math.fma(float9, float21, this.m32))))._m33(this.m33)._properties(2 | this.properties & matrix4fc.properties() & 16);
	}

	public Matrix4f mulTranslationAffine(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		return matrix4f._m00(matrix4fc.m00())._m01(matrix4fc.m01())._m02(matrix4fc.m02())._m03(this.m03)._m10(matrix4fc.m10())._m11(matrix4fc.m11())._m12(matrix4fc.m12())._m13(this.m13)._m20(matrix4fc.m20())._m21(matrix4fc.m21())._m22(matrix4fc.m22())._m23(this.m23)._m30(matrix4fc.m30() + this.m30)._m31(matrix4fc.m31() + this.m31)._m32(matrix4fc.m32() + this.m32)._m33(this.m33)._properties(2 | matrix4fc.properties() & 16);
	}

	public Matrix4f mulOrthoAffine(Matrix4fc matrix4fc) {
		return this.mulOrthoAffine(matrix4fc, this);
	}

	public Matrix4f mulOrthoAffine(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		float float1 = this.m00 * matrix4fc.m00();
		float float2 = this.m11 * matrix4fc.m01();
		float float3 = this.m22 * matrix4fc.m02();
		float float4 = this.m00 * matrix4fc.m10();
		float float5 = this.m11 * matrix4fc.m11();
		float float6 = this.m22 * matrix4fc.m12();
		float float7 = this.m00 * matrix4fc.m20();
		float float8 = this.m11 * matrix4fc.m21();
		float float9 = this.m22 * matrix4fc.m22();
		float float10 = this.m00 * matrix4fc.m30() + this.m30;
		float float11 = this.m11 * matrix4fc.m31() + this.m31;
		float float12 = this.m22 * matrix4fc.m32() + this.m32;
		return matrix4f._m00(float1)._m01(float2)._m02(float3)._m03(0.0F)._m10(float4)._m11(float5)._m12(float6)._m13(0.0F)._m20(float7)._m21(float8)._m22(float9)._m23(0.0F)._m30(float10)._m31(float11)._m32(float12)._m33(1.0F)._properties(2);
	}

	public Matrix4f fma4x3(Matrix4fc matrix4fc, float float1) {
		return this.fma4x3(matrix4fc, float1, this);
	}

	public Matrix4f fma4x3(Matrix4fc matrix4fc, float float1, Matrix4f matrix4f) {
		matrix4f._m00(Math.fma(matrix4fc.m00(), float1, this.m00))._m01(Math.fma(matrix4fc.m01(), float1, this.m01))._m02(Math.fma(matrix4fc.m02(), float1, this.m02))._m03(this.m03)._m10(Math.fma(matrix4fc.m10(), float1, this.m10))._m11(Math.fma(matrix4fc.m11(), float1, this.m11))._m12(Math.fma(matrix4fc.m12(), float1, this.m12))._m13(this.m13)._m20(Math.fma(matrix4fc.m20(), float1, this.m20))._m21(Math.fma(matrix4fc.m21(), float1, this.m21))._m22(Math.fma(matrix4fc.m22(), float1, this.m22))._m23(this.m23)._m30(Math.fma(matrix4fc.m30(), float1, this.m30))._m31(Math.fma(matrix4fc.m31(), float1, this.m31))._m32(Math.fma(matrix4fc.m32(), float1, this.m32))._m33(this.m33)._properties(0);
		return matrix4f;
	}

	public Matrix4f add(Matrix4fc matrix4fc) {
		return this.add(matrix4fc, this);
	}

	public Matrix4f add(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		matrix4f._m00(this.m00 + matrix4fc.m00())._m01(this.m01 + matrix4fc.m01())._m02(this.m02 + matrix4fc.m02())._m03(this.m03 + matrix4fc.m03())._m10(this.m10 + matrix4fc.m10())._m11(this.m11 + matrix4fc.m11())._m12(this.m12 + matrix4fc.m12())._m13(this.m13 + matrix4fc.m13())._m20(this.m20 + matrix4fc.m20())._m21(this.m21 + matrix4fc.m21())._m22(this.m22 + matrix4fc.m22())._m23(this.m23 + matrix4fc.m23())._m30(this.m30 + matrix4fc.m30())._m31(this.m31 + matrix4fc.m31())._m32(this.m32 + matrix4fc.m32())._m33(this.m33 + matrix4fc.m33())._properties(0);
		return matrix4f;
	}

	public Matrix4f sub(Matrix4fc matrix4fc) {
		return this.sub(matrix4fc, this);
	}

	public Matrix4f sub(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		matrix4f._m00(this.m00 - matrix4fc.m00())._m01(this.m01 - matrix4fc.m01())._m02(this.m02 - matrix4fc.m02())._m03(this.m03 - matrix4fc.m03())._m10(this.m10 - matrix4fc.m10())._m11(this.m11 - matrix4fc.m11())._m12(this.m12 - matrix4fc.m12())._m13(this.m13 - matrix4fc.m13())._m20(this.m20 - matrix4fc.m20())._m21(this.m21 - matrix4fc.m21())._m22(this.m22 - matrix4fc.m22())._m23(this.m23 - matrix4fc.m23())._m30(this.m30 - matrix4fc.m30())._m31(this.m31 - matrix4fc.m31())._m32(this.m32 - matrix4fc.m32())._m33(this.m33 - matrix4fc.m33())._properties(0);
		return matrix4f;
	}

	public Matrix4f mulComponentWise(Matrix4fc matrix4fc) {
		return this.mulComponentWise(matrix4fc, this);
	}

	public Matrix4f mulComponentWise(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		matrix4f._m00(this.m00 * matrix4fc.m00())._m01(this.m01 * matrix4fc.m01())._m02(this.m02 * matrix4fc.m02())._m03(this.m03 * matrix4fc.m03())._m10(this.m10 * matrix4fc.m10())._m11(this.m11 * matrix4fc.m11())._m12(this.m12 * matrix4fc.m12())._m13(this.m13 * matrix4fc.m13())._m20(this.m20 * matrix4fc.m20())._m21(this.m21 * matrix4fc.m21())._m22(this.m22 * matrix4fc.m22())._m23(this.m23 * matrix4fc.m23())._m30(this.m30 * matrix4fc.m30())._m31(this.m31 * matrix4fc.m31())._m32(this.m32 * matrix4fc.m32())._m33(this.m33 * matrix4fc.m33())._properties(0);
		return matrix4f;
	}

	public Matrix4f add4x3(Matrix4fc matrix4fc) {
		return this.add4x3(matrix4fc, this);
	}

	public Matrix4f add4x3(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		matrix4f._m00(this.m00 + matrix4fc.m00())._m01(this.m01 + matrix4fc.m01())._m02(this.m02 + matrix4fc.m02())._m03(this.m03)._m10(this.m10 + matrix4fc.m10())._m11(this.m11 + matrix4fc.m11())._m12(this.m12 + matrix4fc.m12())._m13(this.m13)._m20(this.m20 + matrix4fc.m20())._m21(this.m21 + matrix4fc.m21())._m22(this.m22 + matrix4fc.m22())._m23(this.m23)._m30(this.m30 + matrix4fc.m30())._m31(this.m31 + matrix4fc.m31())._m32(this.m32 + matrix4fc.m32())._m33(this.m33)._properties(0);
		return matrix4f;
	}

	public Matrix4f sub4x3(Matrix4f matrix4f) {
		return this.sub4x3(matrix4f, this);
	}

	public Matrix4f sub4x3(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		matrix4f._m00(this.m00 - matrix4fc.m00())._m01(this.m01 - matrix4fc.m01())._m02(this.m02 - matrix4fc.m02())._m03(this.m03)._m10(this.m10 - matrix4fc.m10())._m11(this.m11 - matrix4fc.m11())._m12(this.m12 - matrix4fc.m12())._m13(this.m13)._m20(this.m20 - matrix4fc.m20())._m21(this.m21 - matrix4fc.m21())._m22(this.m22 - matrix4fc.m22())._m23(this.m23)._m30(this.m30 - matrix4fc.m30())._m31(this.m31 - matrix4fc.m31())._m32(this.m32 - matrix4fc.m32())._m33(this.m33)._properties(0);
		return matrix4f;
	}

	public Matrix4f mul4x3ComponentWise(Matrix4fc matrix4fc) {
		return this.mul4x3ComponentWise(matrix4fc, this);
	}

	public Matrix4f mul4x3ComponentWise(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		matrix4f._m00(this.m00 * matrix4fc.m00())._m01(this.m01 * matrix4fc.m01())._m02(this.m02 * matrix4fc.m02())._m03(this.m03)._m10(this.m10 * matrix4fc.m10())._m11(this.m11 * matrix4fc.m11())._m12(this.m12 * matrix4fc.m12())._m13(this.m13)._m20(this.m20 * matrix4fc.m20())._m21(this.m21 * matrix4fc.m21())._m22(this.m22 * matrix4fc.m22())._m23(this.m23)._m30(this.m30 * matrix4fc.m30())._m31(this.m31 * matrix4fc.m31())._m32(this.m32 * matrix4fc.m32())._m33(this.m33)._properties(0);
		return matrix4f;
	}

	public Matrix4f set(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		return this._m00(float1)._m10(float5)._m20(float9)._m30(float13)._m01(float2)._m11(float6)._m21(float10)._m31(float14)._m02(float3)._m12(float7)._m22(float11)._m32(float15)._m03(float4)._m13(float8)._m23(float12)._m33(float16).determineProperties();
	}

	public Matrix4f set(float[] floatArray, int int1) {
		MemUtil.INSTANCE.copy(floatArray, int1, this);
		return this.determineProperties();
	}

	public Matrix4f set(float[] floatArray) {
		return this.set(floatArray, 0);
	}

	public Matrix4f setTransposed(float[] floatArray, int int1) {
		MemUtil.INSTANCE.copyTransposed(floatArray, int1, this);
		return this.determineProperties();
	}

	public Matrix4f setTransposed(float[] floatArray) {
		return this.setTransposed(floatArray, 0);
	}

	public Matrix4f set(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
		return this.determineProperties();
	}

	public Matrix4f set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this.determineProperties();
	}

	public Matrix4f setTransposed(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.getTransposed(this, floatBuffer.position(), floatBuffer);
		return this.determineProperties();
	}

	public Matrix4f setTransposed(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.getTransposed(this, byteBuffer.position(), byteBuffer);
		return this.determineProperties();
	}

	public Matrix4f setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this.determineProperties();
		}
	}

	public Matrix4f setTransposedFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.getTransposed(this, long1);
			return this.determineProperties();
		}
	}

	public Matrix4f set(Vector4fc vector4fc, Vector4fc vector4fc2, Vector4fc vector4fc3, Vector4fc vector4fc4) {
		return this._m00(vector4fc.x())._m01(vector4fc.y())._m02(vector4fc.z())._m03(vector4fc.w())._m10(vector4fc2.x())._m11(vector4fc2.y())._m12(vector4fc2.z())._m13(vector4fc2.w())._m20(vector4fc3.x())._m21(vector4fc3.y())._m22(vector4fc3.z())._m23(vector4fc3.w())._m30(vector4fc4.x())._m31(vector4fc4.y())._m32(vector4fc4.z())._m33(vector4fc4.w()).determineProperties();
	}

	public float determinant() {
		return (this.properties & 2) != 0 ? this.determinantAffine() : (this.m00 * this.m11 - this.m01 * this.m10) * (this.m22 * this.m33 - this.m23 * this.m32) + (this.m02 * this.m10 - this.m00 * this.m12) * (this.m21 * this.m33 - this.m23 * this.m31) + (this.m00 * this.m13 - this.m03 * this.m10) * (this.m21 * this.m32 - this.m22 * this.m31) + (this.m01 * this.m12 - this.m02 * this.m11) * (this.m20 * this.m33 - this.m23 * this.m30) + (this.m03 * this.m11 - this.m01 * this.m13) * (this.m20 * this.m32 - this.m22 * this.m30) + (this.m02 * this.m13 - this.m03 * this.m12) * (this.m20 * this.m31 - this.m21 * this.m30);
	}

	public float determinant3x3() {
		return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
	}

	public float determinantAffine() {
		return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
	}

	public Matrix4f invert(Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.identity();
		} else if ((this.properties & 8) != 0) {
			return this.invertTranslation(matrix4f);
		} else if ((this.properties & 16) != 0) {
			return this.invertOrthonormal(matrix4f);
		} else if ((this.properties & 2) != 0) {
			return this.invertAffine(matrix4f);
		} else {
			return (this.properties & 1) != 0 ? this.invertPerspective(matrix4f) : this.invertGeneric(matrix4f);
		}
	}

	private Matrix4f invertTranslation(Matrix4f matrix4f) {
		if (matrix4f != this) {
			matrix4f.set((Matrix4fc)this);
		}

		return matrix4f._m30(-this.m30)._m31(-this.m31)._m32(-this.m32)._properties(26);
	}

	private Matrix4f invertOrthonormal(Matrix4f matrix4f) {
		float float1 = -(this.m00 * this.m30 + this.m01 * this.m31 + this.m02 * this.m32);
		float float2 = -(this.m10 * this.m30 + this.m11 * this.m31 + this.m12 * this.m32);
		float float3 = -(this.m20 * this.m30 + this.m21 * this.m31 + this.m22 * this.m32);
		float float4 = this.m01;
		float float5 = this.m02;
		float float6 = this.m12;
		return matrix4f._m00(this.m00)._m01(this.m10)._m02(this.m20)._m03(0.0F)._m10(float4)._m11(this.m11)._m12(this.m21)._m13(0.0F)._m20(float5)._m21(float6)._m22(this.m22)._m23(0.0F)._m30(float1)._m31(float2)._m32(float3)._m33(1.0F)._properties(18);
	}

	private Matrix4f invertGeneric(Matrix4f matrix4f) {
		return this != matrix4f ? this.invertGenericNonThis(matrix4f) : this.invertGenericThis(matrix4f);
	}

	private Matrix4f invertGenericNonThis(Matrix4f matrix4f) {
		float float1 = this.m00 * this.m11 - this.m01 * this.m10;
		float float2 = this.m00 * this.m12 - this.m02 * this.m10;
		float float3 = this.m00 * this.m13 - this.m03 * this.m10;
		float float4 = this.m01 * this.m12 - this.m02 * this.m11;
		float float5 = this.m01 * this.m13 - this.m03 * this.m11;
		float float6 = this.m02 * this.m13 - this.m03 * this.m12;
		float float7 = this.m20 * this.m31 - this.m21 * this.m30;
		float float8 = this.m20 * this.m32 - this.m22 * this.m30;
		float float9 = this.m20 * this.m33 - this.m23 * this.m30;
		float float10 = this.m21 * this.m32 - this.m22 * this.m31;
		float float11 = this.m21 * this.m33 - this.m23 * this.m31;
		float float12 = this.m22 * this.m33 - this.m23 * this.m32;
		float float13 = float1 * float12 - float2 * float11 + float3 * float10 + float4 * float9 - float5 * float8 + float6 * float7;
		float13 = 1.0F / float13;
		return matrix4f._m00(Math.fma(this.m11, float12, Math.fma(-this.m12, float11, this.m13 * float10)) * float13)._m01(Math.fma(-this.m01, float12, Math.fma(this.m02, float11, -this.m03 * float10)) * float13)._m02(Math.fma(this.m31, float6, Math.fma(-this.m32, float5, this.m33 * float4)) * float13)._m03(Math.fma(-this.m21, float6, Math.fma(this.m22, float5, -this.m23 * float4)) * float13)._m10(Math.fma(-this.m10, float12, Math.fma(this.m12, float9, -this.m13 * float8)) * float13)._m11(Math.fma(this.m00, float12, Math.fma(-this.m02, float9, this.m03 * float8)) * float13)._m12(Math.fma(-this.m30, float6, Math.fma(this.m32, float3, -this.m33 * float2)) * float13)._m13(Math.fma(this.m20, float6, Math.fma(-this.m22, float3, this.m23 * float2)) * float13)._m20(Math.fma(this.m10, float11, Math.fma(-this.m11, float9, this.m13 * float7)) * float13)._m21(Math.fma(-this.m00, float11, Math.fma(this.m01, float9, -this.m03 * float7)) * float13)._m22(Math.fma(this.m30, float5, Math.fma(-this.m31, float3, this.m33 * float1)) * float13)._m23(Math.fma(-this.m20, float5, Math.fma(this.m21, float3, -this.m23 * float1)) * float13)._m30(Math.fma(-this.m10, float10, Math.fma(this.m11, float8, -this.m12 * float7)) * float13)._m31(Math.fma(this.m00, float10, Math.fma(-this.m01, float8, this.m02 * float7)) * float13)._m32(Math.fma(-this.m30, float4, Math.fma(this.m31, float2, -this.m32 * float1)) * float13)._m33(Math.fma(this.m20, float4, Math.fma(-this.m21, float2, this.m22 * float1)) * float13)._properties(0);
	}

	private Matrix4f invertGenericThis(Matrix4f matrix4f) {
		float float1 = this.m00 * this.m11 - this.m01 * this.m10;
		float float2 = this.m00 * this.m12 - this.m02 * this.m10;
		float float3 = this.m00 * this.m13 - this.m03 * this.m10;
		float float4 = this.m01 * this.m12 - this.m02 * this.m11;
		float float5 = this.m01 * this.m13 - this.m03 * this.m11;
		float float6 = this.m02 * this.m13 - this.m03 * this.m12;
		float float7 = this.m20 * this.m31 - this.m21 * this.m30;
		float float8 = this.m20 * this.m32 - this.m22 * this.m30;
		float float9 = this.m20 * this.m33 - this.m23 * this.m30;
		float float10 = this.m21 * this.m32 - this.m22 * this.m31;
		float float11 = this.m21 * this.m33 - this.m23 * this.m31;
		float float12 = this.m22 * this.m33 - this.m23 * this.m32;
		float float13 = float1 * float12 - float2 * float11 + float3 * float10 + float4 * float9 - float5 * float8 + float6 * float7;
		float13 = 1.0F / float13;
		float float14 = Math.fma(this.m11, float12, Math.fma(-this.m12, float11, this.m13 * float10)) * float13;
		float float15 = Math.fma(-this.m01, float12, Math.fma(this.m02, float11, -this.m03 * float10)) * float13;
		float float16 = Math.fma(this.m31, float6, Math.fma(-this.m32, float5, this.m33 * float4)) * float13;
		float float17 = Math.fma(-this.m21, float6, Math.fma(this.m22, float5, -this.m23 * float4)) * float13;
		float float18 = Math.fma(-this.m10, float12, Math.fma(this.m12, float9, -this.m13 * float8)) * float13;
		float float19 = Math.fma(this.m00, float12, Math.fma(-this.m02, float9, this.m03 * float8)) * float13;
		float float20 = Math.fma(-this.m30, float6, Math.fma(this.m32, float3, -this.m33 * float2)) * float13;
		float float21 = Math.fma(this.m20, float6, Math.fma(-this.m22, float3, this.m23 * float2)) * float13;
		float float22 = Math.fma(this.m10, float11, Math.fma(-this.m11, float9, this.m13 * float7)) * float13;
		float float23 = Math.fma(-this.m00, float11, Math.fma(this.m01, float9, -this.m03 * float7)) * float13;
		float float24 = Math.fma(this.m30, float5, Math.fma(-this.m31, float3, this.m33 * float1)) * float13;
		float float25 = Math.fma(-this.m20, float5, Math.fma(this.m21, float3, -this.m23 * float1)) * float13;
		float float26 = Math.fma(-this.m10, float10, Math.fma(this.m11, float8, -this.m12 * float7)) * float13;
		float float27 = Math.fma(this.m00, float10, Math.fma(-this.m01, float8, this.m02 * float7)) * float13;
		float float28 = Math.fma(-this.m30, float4, Math.fma(this.m31, float2, -this.m32 * float1)) * float13;
		float float29 = Math.fma(this.m20, float4, Math.fma(-this.m21, float2, this.m22 * float1)) * float13;
		return matrix4f._m00(float14)._m01(float15)._m02(float16)._m03(float17)._m10(float18)._m11(float19)._m12(float20)._m13(float21)._m20(float22)._m21(float23)._m22(float24)._m23(float25)._m30(float26)._m31(float27)._m32(float28)._m33(float29)._properties(0);
	}

	public Matrix4f invert() {
		return this.invert(this);
	}

	public Matrix4f invertPerspective(Matrix4f matrix4f) {
		float float1 = 1.0F / (this.m00 * this.m11);
		float float2 = -1.0F / (this.m23 * this.m32);
		return matrix4f.set(this.m11 * float1, 0.0F, 0.0F, 0.0F, 0.0F, this.m00 * float1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -this.m23 * float2, 0.0F, 0.0F, -this.m32 * float2, this.m22 * float2)._properties(0);
	}

	public Matrix4f invertPerspective() {
		return this.invertPerspective(this);
	}

	public Matrix4f invertFrustum(Matrix4f matrix4f) {
		float float1 = 1.0F / this.m00;
		float float2 = 1.0F / this.m11;
		float float3 = 1.0F / this.m23;
		float float4 = 1.0F / this.m32;
		return matrix4f.set(float1, 0.0F, 0.0F, 0.0F, 0.0F, float2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, float4, -this.m20 * float1 * float3, -this.m21 * float2 * float3, float3, -this.m22 * float3 * float4);
	}

	public Matrix4f invertFrustum() {
		return this.invertFrustum(this);
	}

	public Matrix4f invertOrtho(Matrix4f matrix4f) {
		float float1 = 1.0F / this.m00;
		float float2 = 1.0F / this.m11;
		float float3 = 1.0F / this.m22;
		return matrix4f.set(float1, 0.0F, 0.0F, 0.0F, 0.0F, float2, 0.0F, 0.0F, 0.0F, 0.0F, float3, 0.0F, -this.m30 * float1, -this.m31 * float2, -this.m32 * float3, 1.0F)._properties(2 | this.properties & 16);
	}

	public Matrix4f invertOrtho() {
		return this.invertOrtho(this);
	}

	public Matrix4f invertPerspectiveView(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		float float1 = 1.0F / (this.m00 * this.m11);
		float float2 = -1.0F / (this.m23 * this.m32);
		float float3 = this.m11 * float1;
		float float4 = this.m00 * float1;
		float float5 = -this.m23 * float2;
		float float6 = -this.m32 * float2;
		float float7 = this.m22 * float2;
		float float8 = -matrix4fc.m00() * matrix4fc.m30() - matrix4fc.m01() * matrix4fc.m31() - matrix4fc.m02() * matrix4fc.m32();
		float float9 = -matrix4fc.m10() * matrix4fc.m30() - matrix4fc.m11() * matrix4fc.m31() - matrix4fc.m12() * matrix4fc.m32();
		float float10 = -matrix4fc.m20() * matrix4fc.m30() - matrix4fc.m21() * matrix4fc.m31() - matrix4fc.m22() * matrix4fc.m32();
		float float11 = matrix4fc.m01() * float4;
		float float12 = matrix4fc.m02() * float6 + float8 * float7;
		float float13 = matrix4fc.m12() * float6 + float9 * float7;
		float float14 = matrix4fc.m22() * float6 + float10 * float7;
		return matrix4f._m00(matrix4fc.m00() * float3)._m01(matrix4fc.m10() * float3)._m02(matrix4fc.m20() * float3)._m03(0.0F)._m10(float11)._m11(matrix4fc.m11() * float4)._m12(matrix4fc.m21() * float4)._m13(0.0F)._m20(float8 * float5)._m21(float9 * float5)._m22(float10 * float5)._m23(float5)._m30(float12)._m31(float13)._m32(float14)._m33(float7)._properties(0);
	}

	public Matrix4f invertPerspectiveView(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f) {
		float float1 = 1.0F / (this.m00 * this.m11);
		float float2 = -1.0F / (this.m23 * this.m32);
		float float3 = this.m11 * float1;
		float float4 = this.m00 * float1;
		float float5 = -this.m23 * float2;
		float float6 = -this.m32 * float2;
		float float7 = this.m22 * float2;
		float float8 = -matrix4x3fc.m00() * matrix4x3fc.m30() - matrix4x3fc.m01() * matrix4x3fc.m31() - matrix4x3fc.m02() * matrix4x3fc.m32();
		float float9 = -matrix4x3fc.m10() * matrix4x3fc.m30() - matrix4x3fc.m11() * matrix4x3fc.m31() - matrix4x3fc.m12() * matrix4x3fc.m32();
		float float10 = -matrix4x3fc.m20() * matrix4x3fc.m30() - matrix4x3fc.m21() * matrix4x3fc.m31() - matrix4x3fc.m22() * matrix4x3fc.m32();
		return matrix4f._m00(matrix4x3fc.m00() * float3)._m01(matrix4x3fc.m10() * float3)._m02(matrix4x3fc.m20() * float3)._m03(0.0F)._m10(matrix4x3fc.m01() * float4)._m11(matrix4x3fc.m11() * float4)._m12(matrix4x3fc.m21() * float4)._m13(0.0F)._m20(float8 * float5)._m21(float9 * float5)._m22(float10 * float5)._m23(float5)._m30(matrix4x3fc.m02() * float6 + float8 * float7)._m31(matrix4x3fc.m12() * float6 + float9 * float7)._m32(matrix4x3fc.m22() * float6 + float10 * float7)._m33(float7)._properties(0);
	}

	public Matrix4f invertAffine(Matrix4f matrix4f) {
		float float1 = this.m00 * this.m11;
		float float2 = this.m01 * this.m10;
		float float3 = this.m02 * this.m10;
		float float4 = this.m00 * this.m12;
		float float5 = this.m01 * this.m12;
		float float6 = this.m02 * this.m11;
		float float7 = (float1 - float2) * this.m22 + (float3 - float4) * this.m21 + (float5 - float6) * this.m20;
		float float8 = 1.0F / float7;
		float float9 = this.m10 * this.m22;
		float float10 = this.m10 * this.m21;
		float float11 = this.m11 * this.m22;
		float float12 = this.m11 * this.m20;
		float float13 = this.m12 * this.m21;
		float float14 = this.m12 * this.m20;
		float float15 = this.m20 * this.m02;
		float float16 = this.m20 * this.m01;
		float float17 = this.m21 * this.m02;
		float float18 = this.m21 * this.m00;
		float float19 = this.m22 * this.m01;
		float float20 = this.m22 * this.m00;
		float float21 = (float15 * this.m31 - float16 * this.m32 + float18 * this.m32 - float17 * this.m30 + float19 * this.m30 - float20 * this.m31) * float8;
		float float22 = (float6 * this.m30 - float5 * this.m30 + float4 * this.m31 - float3 * this.m31 + float2 * this.m32 - float1 * this.m32) * float8;
		return matrix4f._m00((float11 - float13) * float8)._m01((float17 - float19) * float8)._m02((float5 - float6) * float8)._m03(0.0F)._m10((float14 - float9) * float8)._m11((float20 - float15) * float8)._m12((float3 - float4) * float8)._m13(0.0F)._m20((float10 - float12) * float8)._m21((float16 - float18) * float8)._m22((float1 - float2) * float8)._m23(0.0F)._m30((float9 * this.m31 - float10 * this.m32 + float12 * this.m32 - float11 * this.m30 + float13 * this.m30 - float14 * this.m31) * float8)._m31(float21)._m32(float22)._m33(1.0F)._properties(2);
	}

	public Matrix4f invertAffine() {
		return this.invertAffine(this);
	}

	public Matrix4f transpose(Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.identity();
		} else {
			return this != matrix4f ? this.transposeNonThisGeneric(matrix4f) : this.transposeThisGeneric(matrix4f);
		}
	}

	private Matrix4f transposeNonThisGeneric(Matrix4f matrix4f) {
		return matrix4f._m00(this.m00)._m01(this.m10)._m02(this.m20)._m03(this.m30)._m10(this.m01)._m11(this.m11)._m12(this.m21)._m13(this.m31)._m20(this.m02)._m21(this.m12)._m22(this.m22)._m23(this.m32)._m30(this.m03)._m31(this.m13)._m32(this.m23)._m33(this.m33)._properties(0);
	}

	private Matrix4f transposeThisGeneric(Matrix4f matrix4f) {
		float float1 = this.m01;
		float float2 = this.m02;
		float float3 = this.m12;
		float float4 = this.m03;
		float float5 = this.m13;
		float float6 = this.m23;
		return matrix4f._m01(this.m10)._m02(this.m20)._m03(this.m30)._m10(float1)._m12(this.m21)._m13(this.m31)._m20(float2)._m21(float3)._m23(this.m32)._m30(float4)._m31(float5)._m32(float6)._properties(0);
	}

	public Matrix4f transpose3x3() {
		return this.transpose3x3(this);
	}

	public Matrix4f transpose3x3(Matrix4f matrix4f) {
		float float1 = this.m01;
		float float2 = this.m02;
		float float3 = this.m12;
		return matrix4f._m00(this.m00)._m01(this.m10)._m02(this.m20)._m10(float1)._m11(this.m11)._m12(this.m21)._m20(float2)._m21(float3)._m22(this.m22)._properties(this.properties & 30);
	}

	public Matrix3f transpose3x3(Matrix3f matrix3f) {
		return matrix3f._m00(this.m00)._m01(this.m10)._m02(this.m20)._m10(this.m01)._m11(this.m11)._m12(this.m21)._m20(this.m02)._m21(this.m12)._m22(this.m22);
	}

	public Matrix4f transpose() {
		return this.transpose(this);
	}

	public Matrix4f translation(float float1, float float2, float float3) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		return this._m30(float1)._m31(float2)._m32(float3)._properties(26);
	}

	public Matrix4f translation(Vector3fc vector3fc) {
		return this.translation(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4f setTranslation(float float1, float float2, float float3) {
		return this._m30(float1)._m31(float2)._m32(float3)._properties(this.properties & -6);
	}

	public Matrix4f setTranslation(Vector3fc vector3fc) {
		return this.setTranslation(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Vector3f getTranslation(Vector3f vector3f) {
		vector3f.x = this.m30;
		vector3f.y = this.m31;
		vector3f.z = this.m32;
		return vector3f;
	}

	public Vector3f getScale(Vector3f vector3f) {
		vector3f.x = Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
		vector3f.y = Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
		vector3f.z = Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
		return vector3f;
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		String string = this.toString(decimalFormat);
		StringBuffer stringBuffer = new StringBuffer();
		int int1 = Integer.MIN_VALUE;
		for (int int2 = 0; int2 < string.length(); ++int2) {
			char char1 = string.charAt(int2);
			if (char1 == 'E') {
				int1 = int2;
			} else {
				if (char1 == ' ' && int1 == int2 - 1) {
					stringBuffer.append('+');
					continue;
				}

				if (Character.isDigit(char1) && int1 == int2 - 1) {
					stringBuffer.append('+');
				}
			}

			stringBuffer.append(char1);
		}

		return stringBuffer.toString();
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format((double)this.m00, numberFormat);
		return string + " " + Runtime.format((double)this.m10, numberFormat) + " " + Runtime.format((double)this.m20, numberFormat) + " " + Runtime.format((double)this.m30, numberFormat) + "\n" + Runtime.format((double)this.m01, numberFormat) + " " + Runtime.format((double)this.m11, numberFormat) + " " + Runtime.format((double)this.m21, numberFormat) + " " + Runtime.format((double)this.m31, numberFormat) + "\n" + Runtime.format((double)this.m02, numberFormat) + " " + Runtime.format((double)this.m12, numberFormat) + " " + Runtime.format((double)this.m22, numberFormat) + " " + Runtime.format((double)this.m32, numberFormat) + "\n" + Runtime.format((double)this.m03, numberFormat) + " " + Runtime.format((double)this.m13, numberFormat) + " " + Runtime.format((double)this.m23, numberFormat) + " " + Runtime.format((double)this.m33, numberFormat) + "\n";
	}

	public Matrix4f get(Matrix4f matrix4f) {
		return matrix4f.set((Matrix4fc)this);
	}

	public Matrix4x3f get4x3(Matrix4x3f matrix4x3f) {
		return matrix4x3f.set((Matrix4fc)this);
	}

	public Matrix4d get(Matrix4d matrix4d) {
		return matrix4d.set((Matrix4fc)this);
	}

	public Matrix3f get3x3(Matrix3f matrix3f) {
		return matrix3f.set((Matrix4fc)this);
	}

	public Matrix3d get3x3(Matrix3d matrix3d) {
		return matrix3d.set((Matrix4fc)this);
	}

	public AxisAngle4f getRotation(AxisAngle4f axisAngle4f) {
		return axisAngle4f.set((Matrix4fc)this);
	}

	public AxisAngle4d getRotation(AxisAngle4d axisAngle4d) {
		return axisAngle4d.set((Matrix4fc)this);
	}

	public Quaternionf getUnnormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromUnnormalized((Matrix4fc)this);
	}

	public Quaternionf getNormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromNormalized((Matrix4fc)this);
	}

	public Quaterniond getUnnormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromUnnormalized((Matrix4fc)this);
	}

	public Quaterniond getNormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromNormalized((Matrix4fc)this);
	}

	public FloatBuffer get(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer get4x3(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put4x3(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public FloatBuffer get4x3(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put4x3(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get4x3(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x3(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get4x3(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x3(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer get3x4(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put3x4(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public FloatBuffer get3x4(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put3x4(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get3x4(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put3x4(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get3x4(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put3x4(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer getTransposed(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.putTransposed(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public FloatBuffer getTransposed(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer getTransposed(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putTransposed(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer get4x3Transposed(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put4x3Transposed(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public FloatBuffer get4x3Transposed(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put4x3Transposed(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get4x3Transposed(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x3Transposed(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get4x3Transposed(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x3Transposed(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Matrix4fc getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public float[] get(float[] floatArray, int int1) {
		MemUtil.INSTANCE.copy(this, floatArray, int1);
		return floatArray;
	}

	public float[] get(float[] floatArray) {
		MemUtil.INSTANCE.copy((Matrix4f)this, (float[])floatArray, 0);
		return floatArray;
	}

	public Matrix4f zero() {
		MemUtil.INSTANCE.zero(this);
		return this._properties(0);
	}

	public Matrix4f scaling(float float1) {
		return this.scaling(float1, float1, float1);
	}

	public Matrix4f scaling(float float1, float float2, float float3) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		boolean boolean1 = Math.absEqualsOne(float1) && Math.absEqualsOne(float2) && Math.absEqualsOne(float3);
		return this._m00(float1)._m11(float2)._m22(float3)._properties(2 | (boolean1 ? 16 : 0));
	}

	public Matrix4f scaling(Vector3fc vector3fc) {
		return this.scaling(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4f rotation(float float1, Vector3fc vector3fc) {
		return this.rotation(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4f rotation(AxisAngle4f axisAngle4f) {
		return this.rotation(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z);
	}

	public Matrix4f rotation(float float1, float float2, float float3, float float4) {
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return this.rotationX(float2 * float1);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return this.rotationY(float3 * float1);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? this.rotationZ(float4 * float1) : this.rotationInternal(float1, float2, float3, float4);
		}
	}

	private Matrix4f rotationInternal(float float1, float float2, float float3, float float4) {
		float float5 = Math.sin(float1);
		float float6 = Math.cosFromSin(float5, float1);
		float float7 = 1.0F - float6;
		float float8 = float2 * float3;
		float float9 = float2 * float4;
		float float10 = float3 * float4;
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		return this._m00(float6 + float2 * float2 * float7)._m10(float8 * float7 - float4 * float5)._m20(float9 * float7 + float3 * float5)._m01(float8 * float7 + float4 * float5)._m11(float6 + float3 * float3 * float7)._m21(float10 * float7 - float2 * float5)._m02(float9 * float7 - float3 * float5)._m12(float10 * float7 + float2 * float5)._m22(float6 + float4 * float4 * float7)._properties(18);
	}

	public Matrix4f rotationX(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this._m11(float3)._m12(float2)._m21(-float2)._m22(float3)._properties(18);
		return this;
	}

	public Matrix4f rotationY(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this._m00(float3)._m02(-float2)._m20(float2)._m22(float3)._properties(18);
		return this;
	}

	public Matrix4f rotationZ(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		return this._m00(float3)._m01(float2)._m10(-float2)._m11(float3)._properties(18);
	}

	public Matrix4f rotationTowardsXY(float float1, float float2) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		return this._m00(float2)._m01(float1)._m10(-float1)._m11(float2)._properties(18);
	}

	public Matrix4f rotationXYZ(float float1, float float2, float float3) {
		float float4 = Math.sin(float1);
		float float5 = Math.cosFromSin(float4, float1);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		float float10 = -float4 * -float6;
		float float11 = float5 * -float6;
		return this._m20(float6)._m21(-float4 * float7)._m22(float5 * float7)._m00(float7 * float9)._m01(float10 * float9 + float5 * float8)._m02(float11 * float9 + float4 * float8)._m10(float7 * -float8)._m11(float10 * -float8 + float5 * float9)._m12(float11 * -float8 + float4 * float9)._properties(18);
	}

	public Matrix4f rotationZYX(float float1, float float2, float float3) {
		float float4 = Math.sin(float3);
		float float5 = Math.cosFromSin(float4, float3);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float1);
		float float9 = Math.cosFromSin(float8, float1);
		float float10 = float9 * float6;
		float float11 = float8 * float6;
		return this._m00(float9 * float7)._m01(float8 * float7)._m02(-float6)._m03(0.0F)._m10(-float8 * float5 + float10 * float4)._m11(float9 * float5 + float11 * float4)._m12(float7 * float4)._m13(0.0F)._m20(-float8 * -float4 + float10 * float5)._m21(float9 * -float4 + float11 * float5)._m22(float7 * float5)._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties(18);
	}

	public Matrix4f rotationYXZ(float float1, float float2, float float3) {
		float float4 = Math.sin(float2);
		float float5 = Math.cosFromSin(float4, float2);
		float float6 = Math.sin(float1);
		float float7 = Math.cosFromSin(float6, float1);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		float float10 = float6 * float4;
		float float11 = float7 * float4;
		return this._m20(float6 * float5)._m21(-float4)._m22(float7 * float5)._m23(0.0F)._m00(float7 * float9 + float10 * float8)._m01(float5 * float8)._m02(-float6 * float9 + float11 * float8)._m03(0.0F)._m10(float7 * -float8 + float10 * float9)._m11(float5 * float9)._m12(-float6 * -float8 + float11 * float9)._m13(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties(18);
	}

	public Matrix4f setRotationXYZ(float float1, float float2, float float3) {
		float float4 = Math.sin(float1);
		float float5 = Math.cosFromSin(float4, float1);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		float float10 = -float4 * -float6;
		float float11 = float5 * -float6;
		return this._m20(float6)._m21(-float4 * float7)._m22(float5 * float7)._m00(float7 * float9)._m01(float10 * float9 + float5 * float8)._m02(float11 * float9 + float4 * float8)._m10(float7 * -float8)._m11(float10 * -float8 + float5 * float9)._m12(float11 * -float8 + float4 * float9)._properties(this.properties & -14);
	}

	public Matrix4f setRotationZYX(float float1, float float2, float float3) {
		float float4 = Math.sin(float3);
		float float5 = Math.cosFromSin(float4, float3);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float1);
		float float9 = Math.cosFromSin(float8, float1);
		float float10 = float9 * float6;
		float float11 = float8 * float6;
		return this._m00(float9 * float7)._m01(float8 * float7)._m02(-float6)._m10(-float8 * float5 + float10 * float4)._m11(float9 * float5 + float11 * float4)._m12(float7 * float4)._m20(-float8 * -float4 + float10 * float5)._m21(float9 * -float4 + float11 * float5)._m22(float7 * float5)._properties(this.properties & -14);
	}

	public Matrix4f setRotationYXZ(float float1, float float2, float float3) {
		float float4 = Math.sin(float2);
		float float5 = Math.cosFromSin(float4, float2);
		float float6 = Math.sin(float1);
		float float7 = Math.cosFromSin(float6, float1);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		float float10 = float6 * float4;
		float float11 = float7 * float4;
		return this._m20(float6 * float5)._m21(-float4)._m22(float7 * float5)._m00(float7 * float9 + float10 * float8)._m01(float5 * float8)._m02(-float6 * float9 + float11 * float8)._m10(float7 * -float8 + float10 * float9)._m11(float5 * float9)._m12(-float6 * -float8 + float11 * float9)._properties(this.properties & -14);
	}

	public Matrix4f rotation(Quaternionfc quaternionfc) {
		float float1 = quaternionfc.w() * quaternionfc.w();
		float float2 = quaternionfc.x() * quaternionfc.x();
		float float3 = quaternionfc.y() * quaternionfc.y();
		float float4 = quaternionfc.z() * quaternionfc.z();
		float float5 = quaternionfc.z() * quaternionfc.w();
		float float6 = float5 + float5;
		float float7 = quaternionfc.x() * quaternionfc.y();
		float float8 = float7 + float7;
		float float9 = quaternionfc.x() * quaternionfc.z();
		float float10 = float9 + float9;
		float float11 = quaternionfc.y() * quaternionfc.w();
		float float12 = float11 + float11;
		float float13 = quaternionfc.y() * quaternionfc.z();
		float float14 = float13 + float13;
		float float15 = quaternionfc.x() * quaternionfc.w();
		float float16 = float15 + float15;
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		return this._m00(float1 + float2 - float4 - float3)._m01(float8 + float6)._m02(float10 - float12)._m10(-float6 + float8)._m11(float3 - float4 + float1 - float2)._m12(float14 + float16)._m20(float12 + float10)._m21(float14 - float16)._m22(float4 - float3 - float2 + float1)._properties(18);
	}

	public Matrix4f translationRotateScale(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
		float float11 = float4 + float4;
		float float12 = float5 + float5;
		float float13 = float6 + float6;
		float float14 = float11 * float4;
		float float15 = float12 * float5;
		float float16 = float13 * float6;
		float float17 = float11 * float5;
		float float18 = float11 * float6;
		float float19 = float11 * float7;
		float float20 = float12 * float6;
		float float21 = float12 * float7;
		float float22 = float13 * float7;
		boolean boolean1 = Math.absEqualsOne(float8) && Math.absEqualsOne(float9) && Math.absEqualsOne(float10);
		return this._m00(float8 - (float15 + float16) * float8)._m01((float17 + float22) * float8)._m02((float18 - float21) * float8)._m03(0.0F)._m10((float17 - float22) * float9)._m11(float9 - (float16 + float14) * float9)._m12((float20 + float19) * float9)._m13(0.0F)._m20((float18 + float21) * float10)._m21((float20 - float19) * float10)._m22(float10 - (float15 + float14) * float10)._m23(0.0F)._m30(float1)._m31(float2)._m32(float3)._m33(1.0F)._properties(2 | (boolean1 ? 16 : 0));
	}

	public Matrix4f translationRotateScale(Vector3fc vector3fc, Quaternionfc quaternionfc, Vector3fc vector3fc2) {
		return this.translationRotateScale(vector3fc.x(), vector3fc.y(), vector3fc.z(), quaternionfc.x(), quaternionfc.y(), quaternionfc.z(), quaternionfc.w(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix4f translationRotateScale(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		return this.translationRotateScale(float1, float2, float3, float4, float5, float6, float7, float8, float8, float8);
	}

	public Matrix4f translationRotateScale(Vector3fc vector3fc, Quaternionfc quaternionfc, float float1) {
		return this.translationRotateScale(vector3fc.x(), vector3fc.y(), vector3fc.z(), quaternionfc.x(), quaternionfc.y(), quaternionfc.z(), quaternionfc.w(), float1, float1, float1);
	}

	public Matrix4f translationRotateScaleInvert(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
		boolean boolean1 = Math.absEqualsOne(float8) && Math.absEqualsOne(float9) && Math.absEqualsOne(float10);
		if (boolean1) {
			return this.translationRotateScale(float1, float2, float3, float4, float5, float6, float7, float8, float9, float10).invertOrthonormal(this);
		} else {
			float float11 = -float4;
			float float12 = -float5;
			float float13 = -float6;
			float float14 = float11 + float11;
			float float15 = float12 + float12;
			float float16 = float13 + float13;
			float float17 = float14 * float11;
			float float18 = float15 * float12;
			float float19 = float16 * float13;
			float float20 = float14 * float12;
			float float21 = float14 * float13;
			float float22 = float14 * float7;
			float float23 = float15 * float13;
			float float24 = float15 * float7;
			float float25 = float16 * float7;
			float float26 = 1.0F / float8;
			float float27 = 1.0F / float9;
			float float28 = 1.0F / float10;
			return this._m00(float26 * (1.0F - float18 - float19))._m01(float27 * (float20 + float25))._m02(float28 * (float21 - float24))._m03(0.0F)._m10(float26 * (float20 - float25))._m11(float27 * (1.0F - float19 - float17))._m12(float28 * (float23 + float22))._m13(0.0F)._m20(float26 * (float21 + float24))._m21(float27 * (float23 - float22))._m22(float28 * (1.0F - float18 - float17))._m23(0.0F)._m30(-this.m00 * float1 - this.m10 * float2 - this.m20 * float3)._m31(-this.m01 * float1 - this.m11 * float2 - this.m21 * float3)._m32(-this.m02 * float1 - this.m12 * float2 - this.m22 * float3)._m33(1.0F)._properties(2);
		}
	}

	public Matrix4f translationRotateScaleInvert(Vector3fc vector3fc, Quaternionfc quaternionfc, Vector3fc vector3fc2) {
		return this.translationRotateScaleInvert(vector3fc.x(), vector3fc.y(), vector3fc.z(), quaternionfc.x(), quaternionfc.y(), quaternionfc.z(), quaternionfc.w(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix4f translationRotateScaleInvert(Vector3fc vector3fc, Quaternionfc quaternionfc, float float1) {
		return this.translationRotateScaleInvert(vector3fc.x(), vector3fc.y(), vector3fc.z(), quaternionfc.x(), quaternionfc.y(), quaternionfc.z(), quaternionfc.w(), float1, float1, float1);
	}

	public Matrix4f translationRotateScaleMulAffine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, Matrix4f matrix4f) {
		float float11 = float7 * float7;
		float float12 = float4 * float4;
		float float13 = float5 * float5;
		float float14 = float6 * float6;
		float float15 = float6 * float7;
		float float16 = float4 * float5;
		float float17 = float4 * float6;
		float float18 = float5 * float7;
		float float19 = float5 * float6;
		float float20 = float4 * float7;
		float float21 = float11 + float12 - float14 - float13;
		float float22 = float16 + float15 + float15 + float16;
		float float23 = float17 - float18 + float17 - float18;
		float float24 = -float15 + float16 - float15 + float16;
		float float25 = float13 - float14 + float11 - float12;
		float float26 = float19 + float19 + float20 + float20;
		float float27 = float18 + float17 + float17 + float18;
		float float28 = float19 + float19 - float20 - float20;
		float float29 = float14 - float13 - float12 + float11;
		float float30 = float21 * matrix4f.m00 + float24 * matrix4f.m01 + float27 * matrix4f.m02;
		float float31 = float22 * matrix4f.m00 + float25 * matrix4f.m01 + float28 * matrix4f.m02;
		this._m02(float23 * matrix4f.m00 + float26 * matrix4f.m01 + float29 * matrix4f.m02)._m00(float30)._m01(float31)._m03(0.0F);
		float float32 = float21 * matrix4f.m10 + float24 * matrix4f.m11 + float27 * matrix4f.m12;
		float float33 = float22 * matrix4f.m10 + float25 * matrix4f.m11 + float28 * matrix4f.m12;
		this._m12(float23 * matrix4f.m10 + float26 * matrix4f.m11 + float29 * matrix4f.m12)._m10(float32)._m11(float33)._m13(0.0F);
		float float34 = float21 * matrix4f.m20 + float24 * matrix4f.m21 + float27 * matrix4f.m22;
		float float35 = float22 * matrix4f.m20 + float25 * matrix4f.m21 + float28 * matrix4f.m22;
		this._m22(float23 * matrix4f.m20 + float26 * matrix4f.m21 + float29 * matrix4f.m22)._m20(float34)._m21(float35)._m23(0.0F);
		float float36 = float21 * matrix4f.m30 + float24 * matrix4f.m31 + float27 * matrix4f.m32 + float1;
		float float37 = float22 * matrix4f.m30 + float25 * matrix4f.m31 + float28 * matrix4f.m32 + float2;
		this._m32(float23 * matrix4f.m30 + float26 * matrix4f.m31 + float29 * matrix4f.m32 + float3)._m30(float36)._m31(float37)._m33(1.0F);
		boolean boolean1 = Math.absEqualsOne(float8) && Math.absEqualsOne(float9) && Math.absEqualsOne(float10);
		return this._properties(2 | (boolean1 && (matrix4f.properties & 16) != 0 ? 16 : 0));
	}

	public Matrix4f translationRotateScaleMulAffine(Vector3fc vector3fc, Quaternionfc quaternionfc, Vector3fc vector3fc2, Matrix4f matrix4f) {
		return this.translationRotateScaleMulAffine(vector3fc.x(), vector3fc.y(), vector3fc.z(), quaternionfc.x(), quaternionfc.y(), quaternionfc.z(), quaternionfc.w(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), matrix4f);
	}

	public Matrix4f translationRotate(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		float float8 = float7 * float7;
		float float9 = float4 * float4;
		float float10 = float5 * float5;
		float float11 = float6 * float6;
		float float12 = float6 * float7;
		float float13 = float4 * float5;
		float float14 = float4 * float6;
		float float15 = float5 * float7;
		float float16 = float5 * float6;
		float float17 = float4 * float7;
		return this._m00(float8 + float9 - float11 - float10)._m01(float13 + float12 + float12 + float13)._m02(float14 - float15 + float14 - float15)._m10(-float12 + float13 - float12 + float13)._m11(float10 - float11 + float8 - float9)._m12(float16 + float16 + float17 + float17)._m20(float15 + float14 + float14 + float15)._m21(float16 + float16 - float17 - float17)._m22(float11 - float10 - float9 + float8)._m30(float1)._m31(float2)._m32(float3)._m33(1.0F)._properties(18);
	}

	public Matrix4f translationRotate(float float1, float float2, float float3, Quaternionfc quaternionfc) {
		return this.translationRotate(float1, float2, float3, quaternionfc.x(), quaternionfc.y(), quaternionfc.z(), quaternionfc.w());
	}

	public Matrix4f set3x3(Matrix3fc matrix3fc) {
		return this.set3x3Matrix3fc(matrix3fc)._properties(this.properties & -30);
	}

	private Matrix4f set3x3Matrix3fc(Matrix3fc matrix3fc) {
		return this._m00(matrix3fc.m00())._m01(matrix3fc.m01())._m02(matrix3fc.m02())._m10(matrix3fc.m10())._m11(matrix3fc.m11())._m12(matrix3fc.m12())._m20(matrix3fc.m20())._m21(matrix3fc.m21())._m22(matrix3fc.m22());
	}

	public Vector4f transform(Vector4f vector4f) {
		return vector4f.mul((Matrix4fc)this);
	}

	public Vector4f transform(Vector4fc vector4fc, Vector4f vector4f) {
		return vector4fc.mul((Matrix4fc)this, vector4f);
	}

	public Vector4f transform(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		return vector4f.set(float1, float2, float3, float4).mul((Matrix4fc)this);
	}

	public Vector4f transformTranspose(Vector4f vector4f) {
		return vector4f.mulTranspose(this);
	}

	public Vector4f transformTranspose(Vector4fc vector4fc, Vector4f vector4f) {
		return vector4fc.mulTranspose(this, vector4f);
	}

	public Vector4f transformTranspose(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		return vector4f.set(float1, float2, float3, float4).mulTranspose(this);
	}

	public Vector4f transformProject(Vector4f vector4f) {
		return vector4f.mulProject(this);
	}

	public Vector4f transformProject(Vector4fc vector4fc, Vector4f vector4f) {
		return vector4fc.mulProject(this, (Vector4f)vector4f);
	}

	public Vector4f transformProject(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		return vector4f.set(float1, float2, float3, float4).mulProject(this);
	}

	public Vector3f transformProject(Vector4fc vector4fc, Vector3f vector3f) {
		return vector4fc.mulProject(this, (Vector3f)vector3f);
	}

	public Vector3f transformProject(float float1, float float2, float float3, float float4, Vector3f vector3f) {
		return vector3f.set(float1, float2, float3).mulProject(this, float4, vector3f);
	}

	public Vector3f transformProject(Vector3f vector3f) {
		return vector3f.mulProject(this);
	}

	public Vector3f transformProject(Vector3fc vector3fc, Vector3f vector3f) {
		return vector3fc.mulProject(this, vector3f);
	}

	public Vector3f transformProject(float float1, float float2, float float3, Vector3f vector3f) {
		return vector3f.set(float1, float2, float3).mulProject(this);
	}

	public Vector3f transformPosition(Vector3f vector3f) {
		return vector3f.mulPosition((Matrix4fc)this);
	}

	public Vector3f transformPosition(Vector3fc vector3fc, Vector3f vector3f) {
		return this.transformPosition(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3f);
	}

	public Vector3f transformPosition(float float1, float float2, float float3, Vector3f vector3f) {
		return vector3f.set(float1, float2, float3).mulPosition((Matrix4fc)this);
	}

	public Vector3f transformDirection(Vector3f vector3f) {
		return this.transformDirection(vector3f.x, vector3f.y, vector3f.z, vector3f);
	}

	public Vector3f transformDirection(Vector3fc vector3fc, Vector3f vector3f) {
		return this.transformDirection(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3f);
	}

	public Vector3f transformDirection(float float1, float float2, float float3, Vector3f vector3f) {
		return vector3f.set(float1, float2, float3).mulDirection((Matrix4fc)this);
	}

	public Vector4f transformAffine(Vector4f vector4f) {
		return vector4f.mulAffine(this, vector4f);
	}

	public Vector4f transformAffine(Vector4fc vector4fc, Vector4f vector4f) {
		return this.transformAffine(vector4fc.x(), vector4fc.y(), vector4fc.z(), vector4fc.w(), vector4f);
	}

	public Vector4f transformAffine(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		return vector4f.set(float1, float2, float3, float4).mulAffine(this, vector4f);
	}

	public Matrix4f scale(Vector3fc vector3fc, Matrix4f matrix4f) {
		return this.scale(vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix4f);
	}

	public Matrix4f scale(Vector3fc vector3fc) {
		return this.scale(vector3fc.x(), vector3fc.y(), vector3fc.z(), this);
	}

	public Matrix4f scale(float float1, Matrix4f matrix4f) {
		return this.scale(float1, float1, float1, matrix4f);
	}

	public Matrix4f scale(float float1) {
		return this.scale(float1, float1, float1);
	}

	public Matrix4f scaleXY(float float1, float float2, Matrix4f matrix4f) {
		return this.scale(float1, float2, 1.0F, matrix4f);
	}

	public Matrix4f scaleXY(float float1, float float2) {
		return this.scale(float1, float2, 1.0F);
	}

	public Matrix4f scale(float float1, float float2, float float3, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.scaling(float1, float2, float3) : this.scaleGeneric(float1, float2, float3, matrix4f);
	}

	private Matrix4f scaleGeneric(float float1, float float2, float float3, Matrix4f matrix4f) {
		boolean boolean1 = Math.absEqualsOne(float1) && Math.absEqualsOne(float2) && Math.absEqualsOne(float3);
		return matrix4f._m00(this.m00 * float1)._m01(this.m01 * float1)._m02(this.m02 * float1)._m03(this.m03 * float1)._m10(this.m10 * float2)._m11(this.m11 * float2)._m12(this.m12 * float2)._m13(this.m13 * float2)._m20(this.m20 * float3)._m21(this.m21 * float3)._m22(this.m22 * float3)._m23(this.m23 * float3)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & ~(13 | (boolean1 ? 0 : 16)));
	}

	public Matrix4f scale(float float1, float float2, float float3) {
		return this.scale(float1, float2, float3, this);
	}

	public Matrix4f scaleAround(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		float float7 = this.m00 * float4 + this.m10 * float5 + this.m20 * float6 + this.m30;
		float float8 = this.m01 * float4 + this.m11 * float5 + this.m21 * float6 + this.m31;
		float float9 = this.m02 * float4 + this.m12 * float5 + this.m22 * float6 + this.m32;
		float float10 = this.m03 * float4 + this.m13 * float5 + this.m23 * float6 + this.m33;
		boolean boolean1 = Math.absEqualsOne(float1) && Math.absEqualsOne(float2) && Math.absEqualsOne(float3);
		return matrix4f._m00(this.m00 * float1)._m01(this.m01 * float1)._m02(this.m02 * float1)._m03(this.m03 * float1)._m10(this.m10 * float2)._m11(this.m11 * float2)._m12(this.m12 * float2)._m13(this.m13 * float2)._m20(this.m20 * float3)._m21(this.m21 * float3)._m22(this.m22 * float3)._m23(this.m23 * float3)._m30(-this.m00 * float4 - this.m10 * float5 - this.m20 * float6 + float7)._m31(-this.m01 * float4 - this.m11 * float5 - this.m21 * float6 + float8)._m32(-this.m02 * float4 - this.m12 * float5 - this.m22 * float6 + float9)._m33(-this.m03 * float4 - this.m13 * float5 - this.m23 * float6 + float10)._properties(this.properties & ~(13 | (boolean1 ? 0 : 16)));
	}

	public Matrix4f scaleAround(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.scaleAround(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4f scaleAround(float float1, float float2, float float3, float float4) {
		return this.scaleAround(float1, float1, float1, float2, float3, float4, this);
	}

	public Matrix4f scaleAround(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return this.scaleAround(float1, float1, float1, float2, float3, float4, matrix4f);
	}

	public Matrix4f scaleLocal(float float1, float float2, float float3, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.scaling(float1, float2, float3) : this.scaleLocalGeneric(float1, float2, float3, matrix4f);
	}

	private Matrix4f scaleLocalGeneric(float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = float1 * this.m00;
		float float5 = float2 * this.m01;
		float float6 = float3 * this.m02;
		float float7 = float1 * this.m10;
		float float8 = float2 * this.m11;
		float float9 = float3 * this.m12;
		float float10 = float1 * this.m20;
		float float11 = float2 * this.m21;
		float float12 = float3 * this.m22;
		float float13 = float1 * this.m30;
		float float14 = float2 * this.m31;
		float float15 = float3 * this.m32;
		boolean boolean1 = Math.absEqualsOne(float1) && Math.absEqualsOne(float2) && Math.absEqualsOne(float3);
		return matrix4f._m00(float4)._m01(float5)._m02(float6)._m03(this.m03)._m10(float7)._m11(float8)._m12(float9)._m13(this.m13)._m20(float10)._m21(float11)._m22(float12)._m23(this.m23)._m30(float13)._m31(float14)._m32(float15)._m33(this.m33)._properties(this.properties & ~(13 | (boolean1 ? 0 : 16)));
	}

	public Matrix4f scaleLocal(float float1, Matrix4f matrix4f) {
		return this.scaleLocal(float1, float1, float1, matrix4f);
	}

	public Matrix4f scaleLocal(float float1) {
		return this.scaleLocal(float1, this);
	}

	public Matrix4f scaleLocal(float float1, float float2, float float3) {
		return this.scaleLocal(float1, float2, float3, this);
	}

	public Matrix4f scaleAroundLocal(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		boolean boolean1 = Math.absEqualsOne(float1) && Math.absEqualsOne(float2) && Math.absEqualsOne(float3);
		return matrix4f._m00(float1 * (this.m00 - float4 * this.m03) + float4 * this.m03)._m01(float2 * (this.m01 - float5 * this.m03) + float5 * this.m03)._m02(float3 * (this.m02 - float6 * this.m03) + float6 * this.m03)._m03(this.m03)._m10(float1 * (this.m10 - float4 * this.m13) + float4 * this.m13)._m11(float2 * (this.m11 - float5 * this.m13) + float5 * this.m13)._m12(float3 * (this.m12 - float6 * this.m13) + float6 * this.m13)._m13(this.m13)._m20(float1 * (this.m20 - float4 * this.m23) + float4 * this.m23)._m21(float2 * (this.m21 - float5 * this.m23) + float5 * this.m23)._m22(float3 * (this.m22 - float6 * this.m23) + float6 * this.m23)._m23(this.m23)._m30(float1 * (this.m30 - float4 * this.m33) + float4 * this.m33)._m31(float2 * (this.m31 - float5 * this.m33) + float5 * this.m33)._m32(float3 * (this.m32 - float6 * this.m33) + float6 * this.m33)._m33(this.m33)._properties(this.properties & ~(13 | (boolean1 ? 0 : 16)));
	}

	public Matrix4f scaleAroundLocal(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.scaleAroundLocal(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4f scaleAroundLocal(float float1, float float2, float float3, float float4) {
		return this.scaleAroundLocal(float1, float1, float1, float2, float3, float4, this);
	}

	public Matrix4f scaleAroundLocal(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return this.scaleAroundLocal(float1, float1, float1, float2, float3, float4, matrix4f);
	}

	public Matrix4f rotateX(float float1, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.rotationX(float1);
		} else if ((this.properties & 8) != 0) {
			float float2 = this.m30;
			float float3 = this.m31;
			float float4 = this.m32;
			return matrix4f.rotationX(float1).setTranslation(float2, float3, float4);
		} else {
			return this.rotateXInternal(float1, matrix4f);
		}
	}

	private Matrix4f rotateXInternal(float float1, Matrix4f matrix4f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.m10;
		float float5 = this.m11;
		float float6 = this.m12;
		float float7 = this.m13;
		float float8 = this.m20;
		float float9 = this.m21;
		float float10 = this.m22;
		float float11 = this.m23;
		return matrix4f._m20(float4 * -float2 + float8 * float3)._m21(float5 * -float2 + float9 * float3)._m22(float6 * -float2 + float10 * float3)._m23(float7 * -float2 + float11 * float3)._m10(float4 * float3 + float8 * float2)._m11(float5 * float3 + float9 * float2)._m12(float6 * float3 + float10 * float2)._m13(float7 * float3 + float11 * float2)._m00(this.m00)._m01(this.m01)._m02(this.m02)._m03(this.m03)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateX(float float1) {
		return this.rotateX(float1, this);
	}

	public Matrix4f rotateY(float float1, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.rotationY(float1);
		} else if ((this.properties & 8) != 0) {
			float float2 = this.m30;
			float float3 = this.m31;
			float float4 = this.m32;
			return matrix4f.rotationY(float1).setTranslation(float2, float3, float4);
		} else {
			return this.rotateYInternal(float1, matrix4f);
		}
	}

	private Matrix4f rotateYInternal(float float1, Matrix4f matrix4f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.m00 * float3 + this.m20 * -float2;
		float float5 = this.m01 * float3 + this.m21 * -float2;
		float float6 = this.m02 * float3 + this.m22 * -float2;
		float float7 = this.m03 * float3 + this.m23 * -float2;
		return matrix4f._m20(this.m00 * float2 + this.m20 * float3)._m21(this.m01 * float2 + this.m21 * float3)._m22(this.m02 * float2 + this.m22 * float3)._m23(this.m03 * float2 + this.m23 * float3)._m00(float4)._m01(float5)._m02(float6)._m03(float7)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m13(this.m13)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateY(float float1) {
		return this.rotateY(float1, this);
	}

	public Matrix4f rotateZ(float float1, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.rotationZ(float1);
		} else if ((this.properties & 8) != 0) {
			float float2 = this.m30;
			float float3 = this.m31;
			float float4 = this.m32;
			return matrix4f.rotationZ(float1).setTranslation(float2, float3, float4);
		} else {
			return this.rotateZInternal(float1, matrix4f);
		}
	}

	private Matrix4f rotateZInternal(float float1, Matrix4f matrix4f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		return this.rotateTowardsXY(float2, float3, matrix4f);
	}

	public Matrix4f rotateZ(float float1) {
		return this.rotateZ(float1, this);
	}

	public Matrix4f rotateTowardsXY(float float1, float float2) {
		return this.rotateTowardsXY(float1, float2, this);
	}

	public Matrix4f rotateTowardsXY(float float1, float float2, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.rotationTowardsXY(float1, float2);
		} else {
			float float3 = this.m00 * float2 + this.m10 * float1;
			float float4 = this.m01 * float2 + this.m11 * float1;
			float float5 = this.m02 * float2 + this.m12 * float1;
			float float6 = this.m03 * float2 + this.m13 * float1;
			return matrix4f._m10(this.m00 * -float1 + this.m10 * float2)._m11(this.m01 * -float1 + this.m11 * float2)._m12(this.m02 * -float1 + this.m12 * float2)._m13(this.m03 * -float1 + this.m13 * float2)._m00(float3)._m01(float4)._m02(float5)._m03(float6)._m20(this.m20)._m21(this.m21)._m22(this.m22)._m23(this.m23)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		}
	}

	public Matrix4f rotateXYZ(Vector3fc vector3fc) {
		return this.rotateXYZ(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4f rotateXYZ(float float1, float float2, float float3) {
		return this.rotateXYZ(float1, float2, float3, this);
	}

	public Matrix4f rotateXYZ(float float1, float float2, float float3, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.rotationXYZ(float1, float2, float3);
		} else if ((this.properties & 8) != 0) {
			float float4 = this.m30;
			float float5 = this.m31;
			float float6 = this.m32;
			return matrix4f.rotationXYZ(float1, float2, float3).setTranslation(float4, float5, float6);
		} else {
			return (this.properties & 2) != 0 ? matrix4f.rotateAffineXYZ(float1, float2, float3) : this.rotateXYZInternal(float1, float2, float3, matrix4f);
		}
	}

	private Matrix4f rotateXYZInternal(float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = Math.sin(float1);
		float float5 = Math.cosFromSin(float4, float1);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		float float10 = -float4;
		float float11 = -float6;
		float float12 = -float8;
		float float13 = this.m10 * float5 + this.m20 * float4;
		float float14 = this.m11 * float5 + this.m21 * float4;
		float float15 = this.m12 * float5 + this.m22 * float4;
		float float16 = this.m13 * float5 + this.m23 * float4;
		float float17 = this.m10 * float10 + this.m20 * float5;
		float float18 = this.m11 * float10 + this.m21 * float5;
		float float19 = this.m12 * float10 + this.m22 * float5;
		float float20 = this.m13 * float10 + this.m23 * float5;
		float float21 = this.m00 * float7 + float17 * float11;
		float float22 = this.m01 * float7 + float18 * float11;
		float float23 = this.m02 * float7 + float19 * float11;
		float float24 = this.m03 * float7 + float20 * float11;
		return matrix4f._m20(this.m00 * float6 + float17 * float7)._m21(this.m01 * float6 + float18 * float7)._m22(this.m02 * float6 + float19 * float7)._m23(this.m03 * float6 + float20 * float7)._m00(float21 * float9 + float13 * float8)._m01(float22 * float9 + float14 * float8)._m02(float23 * float9 + float15 * float8)._m03(float24 * float9 + float16 * float8)._m10(float21 * float12 + float13 * float9)._m11(float22 * float12 + float14 * float9)._m12(float23 * float12 + float15 * float9)._m13(float24 * float12 + float16 * float9)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateAffineXYZ(float float1, float float2, float float3) {
		return this.rotateAffineXYZ(float1, float2, float3, this);
	}

	public Matrix4f rotateAffineXYZ(float float1, float float2, float float3, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.rotationXYZ(float1, float2, float3);
		} else if ((this.properties & 8) != 0) {
			float float4 = this.m30;
			float float5 = this.m31;
			float float6 = this.m32;
			return matrix4f.rotationXYZ(float1, float2, float3).setTranslation(float4, float5, float6);
		} else {
			return this.rotateAffineXYZInternal(float1, float2, float3, matrix4f);
		}
	}

	private Matrix4f rotateAffineXYZInternal(float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = Math.sin(float1);
		float float5 = Math.cosFromSin(float4, float1);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		float float10 = -float4;
		float float11 = -float6;
		float float12 = -float8;
		float float13 = this.m10 * float5 + this.m20 * float4;
		float float14 = this.m11 * float5 + this.m21 * float4;
		float float15 = this.m12 * float5 + this.m22 * float4;
		float float16 = this.m10 * float10 + this.m20 * float5;
		float float17 = this.m11 * float10 + this.m21 * float5;
		float float18 = this.m12 * float10 + this.m22 * float5;
		float float19 = this.m00 * float7 + float16 * float11;
		float float20 = this.m01 * float7 + float17 * float11;
		float float21 = this.m02 * float7 + float18 * float11;
		return matrix4f._m20(this.m00 * float6 + float16 * float7)._m21(this.m01 * float6 + float17 * float7)._m22(this.m02 * float6 + float18 * float7)._m23(0.0F)._m00(float19 * float9 + float13 * float8)._m01(float20 * float9 + float14 * float8)._m02(float21 * float9 + float15 * float8)._m03(0.0F)._m10(float19 * float12 + float13 * float9)._m11(float20 * float12 + float14 * float9)._m12(float21 * float12 + float15 * float9)._m13(0.0F)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateZYX(Vector3f vector3f) {
		return this.rotateZYX(vector3f.z, vector3f.y, vector3f.x);
	}

	public Matrix4f rotateZYX(float float1, float float2, float float3) {
		return this.rotateZYX(float1, float2, float3, this);
	}

	public Matrix4f rotateZYX(float float1, float float2, float float3, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.rotationZYX(float1, float2, float3);
		} else if ((this.properties & 8) != 0) {
			float float4 = this.m30;
			float float5 = this.m31;
			float float6 = this.m32;
			return matrix4f.rotationZYX(float1, float2, float3).setTranslation(float4, float5, float6);
		} else {
			return (this.properties & 2) != 0 ? matrix4f.rotateAffineZYX(float1, float2, float3) : this.rotateZYXInternal(float1, float2, float3, matrix4f);
		}
	}

	private Matrix4f rotateZYXInternal(float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = Math.sin(float3);
		float float5 = Math.cosFromSin(float4, float3);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float1);
		float float9 = Math.cosFromSin(float8, float1);
		float float10 = -float8;
		float float11 = -float6;
		float float12 = -float4;
		float float13 = this.m00 * float9 + this.m10 * float8;
		float float14 = this.m01 * float9 + this.m11 * float8;
		float float15 = this.m02 * float9 + this.m12 * float8;
		float float16 = this.m03 * float9 + this.m13 * float8;
		float float17 = this.m00 * float10 + this.m10 * float9;
		float float18 = this.m01 * float10 + this.m11 * float9;
		float float19 = this.m02 * float10 + this.m12 * float9;
		float float20 = this.m03 * float10 + this.m13 * float9;
		float float21 = float13 * float6 + this.m20 * float7;
		float float22 = float14 * float6 + this.m21 * float7;
		float float23 = float15 * float6 + this.m22 * float7;
		float float24 = float16 * float6 + this.m23 * float7;
		return matrix4f._m00(float13 * float7 + this.m20 * float11)._m01(float14 * float7 + this.m21 * float11)._m02(float15 * float7 + this.m22 * float11)._m03(float16 * float7 + this.m23 * float11)._m10(float17 * float5 + float21 * float4)._m11(float18 * float5 + float22 * float4)._m12(float19 * float5 + float23 * float4)._m13(float20 * float5 + float24 * float4)._m20(float17 * float12 + float21 * float5)._m21(float18 * float12 + float22 * float5)._m22(float19 * float12 + float23 * float5)._m23(float20 * float12 + float24 * float5)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateAffineZYX(float float1, float float2, float float3) {
		return this.rotateAffineZYX(float1, float2, float3, this);
	}

	public Matrix4f rotateAffineZYX(float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = Math.sin(float3);
		float float5 = Math.cosFromSin(float4, float3);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float1);
		float float9 = Math.cosFromSin(float8, float1);
		float float10 = -float8;
		float float11 = -float6;
		float float12 = -float4;
		float float13 = this.m00 * float9 + this.m10 * float8;
		float float14 = this.m01 * float9 + this.m11 * float8;
		float float15 = this.m02 * float9 + this.m12 * float8;
		float float16 = this.m00 * float10 + this.m10 * float9;
		float float17 = this.m01 * float10 + this.m11 * float9;
		float float18 = this.m02 * float10 + this.m12 * float9;
		float float19 = float13 * float6 + this.m20 * float7;
		float float20 = float14 * float6 + this.m21 * float7;
		float float21 = float15 * float6 + this.m22 * float7;
		return matrix4f._m00(float13 * float7 + this.m20 * float11)._m01(float14 * float7 + this.m21 * float11)._m02(float15 * float7 + this.m22 * float11)._m03(0.0F)._m10(float16 * float5 + float19 * float4)._m11(float17 * float5 + float20 * float4)._m12(float18 * float5 + float21 * float4)._m13(0.0F)._m20(float16 * float12 + float19 * float5)._m21(float17 * float12 + float20 * float5)._m22(float18 * float12 + float21 * float5)._m23(0.0F)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateYXZ(Vector3f vector3f) {
		return this.rotateYXZ(vector3f.y, vector3f.x, vector3f.z);
	}

	public Matrix4f rotateYXZ(float float1, float float2, float float3) {
		return this.rotateYXZ(float1, float2, float3, this);
	}

	public Matrix4f rotateYXZ(float float1, float float2, float float3, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.rotationYXZ(float1, float2, float3);
		} else if ((this.properties & 8) != 0) {
			float float4 = this.m30;
			float float5 = this.m31;
			float float6 = this.m32;
			return matrix4f.rotationYXZ(float1, float2, float3).setTranslation(float4, float5, float6);
		} else {
			return (this.properties & 2) != 0 ? matrix4f.rotateAffineYXZ(float1, float2, float3) : this.rotateYXZInternal(float1, float2, float3, matrix4f);
		}
	}

	private Matrix4f rotateYXZInternal(float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = Math.sin(float2);
		float float5 = Math.cosFromSin(float4, float2);
		float float6 = Math.sin(float1);
		float float7 = Math.cosFromSin(float6, float1);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		float float10 = -float6;
		float float11 = -float4;
		float float12 = -float8;
		float float13 = this.m00 * float6 + this.m20 * float7;
		float float14 = this.m01 * float6 + this.m21 * float7;
		float float15 = this.m02 * float6 + this.m22 * float7;
		float float16 = this.m03 * float6 + this.m23 * float7;
		float float17 = this.m00 * float7 + this.m20 * float10;
		float float18 = this.m01 * float7 + this.m21 * float10;
		float float19 = this.m02 * float7 + this.m22 * float10;
		float float20 = this.m03 * float7 + this.m23 * float10;
		float float21 = this.m10 * float5 + float13 * float4;
		float float22 = this.m11 * float5 + float14 * float4;
		float float23 = this.m12 * float5 + float15 * float4;
		float float24 = this.m13 * float5 + float16 * float4;
		return matrix4f._m20(this.m10 * float11 + float13 * float5)._m21(this.m11 * float11 + float14 * float5)._m22(this.m12 * float11 + float15 * float5)._m23(this.m13 * float11 + float16 * float5)._m00(float17 * float9 + float21 * float8)._m01(float18 * float9 + float22 * float8)._m02(float19 * float9 + float23 * float8)._m03(float20 * float9 + float24 * float8)._m10(float17 * float12 + float21 * float9)._m11(float18 * float12 + float22 * float9)._m12(float19 * float12 + float23 * float9)._m13(float20 * float12 + float24 * float9)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateAffineYXZ(float float1, float float2, float float3) {
		return this.rotateAffineYXZ(float1, float2, float3, this);
	}

	public Matrix4f rotateAffineYXZ(float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = Math.sin(float2);
		float float5 = Math.cosFromSin(float4, float2);
		float float6 = Math.sin(float1);
		float float7 = Math.cosFromSin(float6, float1);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		float float10 = -float6;
		float float11 = -float4;
		float float12 = -float8;
		float float13 = this.m00 * float6 + this.m20 * float7;
		float float14 = this.m01 * float6 + this.m21 * float7;
		float float15 = this.m02 * float6 + this.m22 * float7;
		float float16 = this.m00 * float7 + this.m20 * float10;
		float float17 = this.m01 * float7 + this.m21 * float10;
		float float18 = this.m02 * float7 + this.m22 * float10;
		float float19 = this.m10 * float5 + float13 * float4;
		float float20 = this.m11 * float5 + float14 * float4;
		float float21 = this.m12 * float5 + float15 * float4;
		return matrix4f._m20(this.m10 * float11 + float13 * float5)._m21(this.m11 * float11 + float14 * float5)._m22(this.m12 * float11 + float15 * float5)._m23(0.0F)._m00(float16 * float9 + float19 * float8)._m01(float17 * float9 + float20 * float8)._m02(float18 * float9 + float21 * float8)._m03(0.0F)._m10(float16 * float12 + float19 * float9)._m11(float17 * float12 + float20 * float9)._m12(float18 * float12 + float21 * float9)._m13(0.0F)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotate(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.rotation(float1, float2, float3, float4);
		} else if ((this.properties & 8) != 0) {
			return this.rotateTranslation(float1, float2, float3, float4, matrix4f);
		} else {
			return (this.properties & 2) != 0 ? this.rotateAffine(float1, float2, float3, float4, matrix4f) : this.rotateGeneric(float1, float2, float3, float4, matrix4f);
		}
	}

	private Matrix4f rotateGeneric(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return this.rotateX(float2 * float1, matrix4f);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return this.rotateY(float3 * float1, matrix4f);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? this.rotateZ(float4 * float1, matrix4f) : this.rotateGenericInternal(float1, float2, float3, float4, matrix4f);
		}
	}

	private Matrix4f rotateGenericInternal(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		float float5 = Math.sin(float1);
		float float6 = Math.cosFromSin(float5, float1);
		float float7 = 1.0F - float6;
		float float8 = float2 * float2;
		float float9 = float2 * float3;
		float float10 = float2 * float4;
		float float11 = float3 * float3;
		float float12 = float3 * float4;
		float float13 = float4 * float4;
		float float14 = float8 * float7 + float6;
		float float15 = float9 * float7 + float4 * float5;
		float float16 = float10 * float7 - float3 * float5;
		float float17 = float9 * float7 - float4 * float5;
		float float18 = float11 * float7 + float6;
		float float19 = float12 * float7 + float2 * float5;
		float float20 = float10 * float7 + float3 * float5;
		float float21 = float12 * float7 - float2 * float5;
		float float22 = float13 * float7 + float6;
		float float23 = this.m00 * float14 + this.m10 * float15 + this.m20 * float16;
		float float24 = this.m01 * float14 + this.m11 * float15 + this.m21 * float16;
		float float25 = this.m02 * float14 + this.m12 * float15 + this.m22 * float16;
		float float26 = this.m03 * float14 + this.m13 * float15 + this.m23 * float16;
		float float27 = this.m00 * float17 + this.m10 * float18 + this.m20 * float19;
		float float28 = this.m01 * float17 + this.m11 * float18 + this.m21 * float19;
		float float29 = this.m02 * float17 + this.m12 * float18 + this.m22 * float19;
		float float30 = this.m03 * float17 + this.m13 * float18 + this.m23 * float19;
		return matrix4f._m20(this.m00 * float20 + this.m10 * float21 + this.m20 * float22)._m21(this.m01 * float20 + this.m11 * float21 + this.m21 * float22)._m22(this.m02 * float20 + this.m12 * float21 + this.m22 * float22)._m23(this.m03 * float20 + this.m13 * float21 + this.m23 * float22)._m00(float23)._m01(float24)._m02(float25)._m03(float26)._m10(float27)._m11(float28)._m12(float29)._m13(float30)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotate(float float1, float float2, float float3, float float4) {
		return this.rotate(float1, float2, float3, float4, this);
	}

	public Matrix4f rotateTranslation(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		float float5 = this.m30;
		float float6 = this.m31;
		float float7 = this.m32;
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return matrix4f.rotationX(float2 * float1).setTranslation(float5, float6, float7);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return matrix4f.rotationY(float3 * float1).setTranslation(float5, float6, float7);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? matrix4f.rotationZ(float4 * float1).setTranslation(float5, float6, float7) : this.rotateTranslationInternal(float1, float2, float3, float4, matrix4f);
		}
	}

	private Matrix4f rotateTranslationInternal(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		float float5 = Math.sin(float1);
		float float6 = Math.cosFromSin(float5, float1);
		float float7 = 1.0F - float6;
		float float8 = float2 * float2;
		float float9 = float2 * float3;
		float float10 = float2 * float4;
		float float11 = float3 * float3;
		float float12 = float3 * float4;
		float float13 = float4 * float4;
		float float14 = float8 * float7 + float6;
		float float15 = float9 * float7 + float4 * float5;
		float float16 = float10 * float7 - float3 * float5;
		float float17 = float9 * float7 - float4 * float5;
		float float18 = float11 * float7 + float6;
		float float19 = float12 * float7 + float2 * float5;
		float float20 = float10 * float7 + float3 * float5;
		float float21 = float12 * float7 - float2 * float5;
		float float22 = float13 * float7 + float6;
		return matrix4f._m20(float20)._m21(float21)._m22(float22)._m00(float14)._m01(float15)._m02(float16)._m03(0.0F)._m10(float17)._m11(float18)._m12(float19)._m13(0.0F)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(1.0F)._properties(this.properties & -14);
	}

	public Matrix4f rotateAffine(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return this.rotateX(float2 * float1, matrix4f);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return this.rotateY(float3 * float1, matrix4f);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? this.rotateZ(float4 * float1, matrix4f) : this.rotateAffineInternal(float1, float2, float3, float4, matrix4f);
		}
	}

	private Matrix4f rotateAffineInternal(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		float float5 = Math.sin(float1);
		float float6 = Math.cosFromSin(float5, float1);
		float float7 = 1.0F - float6;
		float float8 = float2 * float2;
		float float9 = float2 * float3;
		float float10 = float2 * float4;
		float float11 = float3 * float3;
		float float12 = float3 * float4;
		float float13 = float4 * float4;
		float float14 = float8 * float7 + float6;
		float float15 = float9 * float7 + float4 * float5;
		float float16 = float10 * float7 - float3 * float5;
		float float17 = float9 * float7 - float4 * float5;
		float float18 = float11 * float7 + float6;
		float float19 = float12 * float7 + float2 * float5;
		float float20 = float10 * float7 + float3 * float5;
		float float21 = float12 * float7 - float2 * float5;
		float float22 = float13 * float7 + float6;
		float float23 = this.m00 * float14 + this.m10 * float15 + this.m20 * float16;
		float float24 = this.m01 * float14 + this.m11 * float15 + this.m21 * float16;
		float float25 = this.m02 * float14 + this.m12 * float15 + this.m22 * float16;
		float float26 = this.m00 * float17 + this.m10 * float18 + this.m20 * float19;
		float float27 = this.m01 * float17 + this.m11 * float18 + this.m21 * float19;
		float float28 = this.m02 * float17 + this.m12 * float18 + this.m22 * float19;
		return matrix4f._m20(this.m00 * float20 + this.m10 * float21 + this.m20 * float22)._m21(this.m01 * float20 + this.m11 * float21 + this.m21 * float22)._m22(this.m02 * float20 + this.m12 * float21 + this.m22 * float22)._m23(0.0F)._m00(float23)._m01(float24)._m02(float25)._m03(0.0F)._m10(float26)._m11(float27)._m12(float28)._m13(0.0F)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(1.0F)._properties(this.properties & -14);
	}

	public Matrix4f rotateAffine(float float1, float float2, float float3, float float4) {
		return this.rotateAffine(float1, float2, float3, float4, this);
	}

	public Matrix4f rotateLocal(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.rotation(float1, float2, float3, float4) : this.rotateLocalGeneric(float1, float2, float3, float4, matrix4f);
	}

	private Matrix4f rotateLocalGeneric(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return this.rotateLocalX(float2 * float1, matrix4f);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return this.rotateLocalY(float3 * float1, matrix4f);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? this.rotateLocalZ(float4 * float1, matrix4f) : this.rotateLocalGenericInternal(float1, float2, float3, float4, matrix4f);
		}
	}

	private Matrix4f rotateLocalGenericInternal(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		float float5 = Math.sin(float1);
		float float6 = Math.cosFromSin(float5, float1);
		float float7 = 1.0F - float6;
		float float8 = float2 * float2;
		float float9 = float2 * float3;
		float float10 = float2 * float4;
		float float11 = float3 * float3;
		float float12 = float3 * float4;
		float float13 = float4 * float4;
		float float14 = float8 * float7 + float6;
		float float15 = float9 * float7 + float4 * float5;
		float float16 = float10 * float7 - float3 * float5;
		float float17 = float9 * float7 - float4 * float5;
		float float18 = float11 * float7 + float6;
		float float19 = float12 * float7 + float2 * float5;
		float float20 = float10 * float7 + float3 * float5;
		float float21 = float12 * float7 - float2 * float5;
		float float22 = float13 * float7 + float6;
		float float23 = float14 * this.m00 + float17 * this.m01 + float20 * this.m02;
		float float24 = float15 * this.m00 + float18 * this.m01 + float21 * this.m02;
		float float25 = float16 * this.m00 + float19 * this.m01 + float22 * this.m02;
		float float26 = float14 * this.m10 + float17 * this.m11 + float20 * this.m12;
		float float27 = float15 * this.m10 + float18 * this.m11 + float21 * this.m12;
		float float28 = float16 * this.m10 + float19 * this.m11 + float22 * this.m12;
		float float29 = float14 * this.m20 + float17 * this.m21 + float20 * this.m22;
		float float30 = float15 * this.m20 + float18 * this.m21 + float21 * this.m22;
		float float31 = float16 * this.m20 + float19 * this.m21 + float22 * this.m22;
		float float32 = float14 * this.m30 + float17 * this.m31 + float20 * this.m32;
		float float33 = float15 * this.m30 + float18 * this.m31 + float21 * this.m32;
		float float34 = float16 * this.m30 + float19 * this.m31 + float22 * this.m32;
		return matrix4f._m00(float23)._m01(float24)._m02(float25)._m03(this.m03)._m10(float26)._m11(float27)._m12(float28)._m13(this.m13)._m20(float29)._m21(float30)._m22(float31)._m23(this.m23)._m30(float32)._m31(float33)._m32(float34)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateLocal(float float1, float float2, float float3, float float4) {
		return this.rotateLocal(float1, float2, float3, float4, this);
	}

	public Matrix4f rotateLocalX(float float1, Matrix4f matrix4f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = float2 * this.m01 + float3 * this.m02;
		float float5 = float2 * this.m11 + float3 * this.m12;
		float float6 = float2 * this.m21 + float3 * this.m22;
		float float7 = float2 * this.m31 + float3 * this.m32;
		return matrix4f._m00(this.m00)._m01(float3 * this.m01 - float2 * this.m02)._m02(float4)._m03(this.m03)._m10(this.m10)._m11(float3 * this.m11 - float2 * this.m12)._m12(float5)._m13(this.m13)._m20(this.m20)._m21(float3 * this.m21 - float2 * this.m22)._m22(float6)._m23(this.m23)._m30(this.m30)._m31(float3 * this.m31 - float2 * this.m32)._m32(float7)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateLocalX(float float1) {
		return this.rotateLocalX(float1, this);
	}

	public Matrix4f rotateLocalY(float float1, Matrix4f matrix4f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = -float2 * this.m00 + float3 * this.m02;
		float float5 = -float2 * this.m10 + float3 * this.m12;
		float float6 = -float2 * this.m20 + float3 * this.m22;
		float float7 = -float2 * this.m30 + float3 * this.m32;
		return matrix4f._m00(float3 * this.m00 + float2 * this.m02)._m01(this.m01)._m02(float4)._m03(this.m03)._m10(float3 * this.m10 + float2 * this.m12)._m11(this.m11)._m12(float5)._m13(this.m13)._m20(float3 * this.m20 + float2 * this.m22)._m21(this.m21)._m22(float6)._m23(this.m23)._m30(float3 * this.m30 + float2 * this.m32)._m31(this.m31)._m32(float7)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateLocalY(float float1) {
		return this.rotateLocalY(float1, this);
	}

	public Matrix4f rotateLocalZ(float float1, Matrix4f matrix4f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = float2 * this.m00 + float3 * this.m01;
		float float5 = float2 * this.m10 + float3 * this.m11;
		float float6 = float2 * this.m20 + float3 * this.m21;
		float float7 = float2 * this.m30 + float3 * this.m31;
		return matrix4f._m00(float3 * this.m00 - float2 * this.m01)._m01(float4)._m02(this.m02)._m03(this.m03)._m10(float3 * this.m10 - float2 * this.m11)._m11(float5)._m12(this.m12)._m13(this.m13)._m20(float3 * this.m20 - float2 * this.m21)._m21(float6)._m22(this.m22)._m23(this.m23)._m30(float3 * this.m30 - float2 * this.m31)._m31(float7)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateLocalZ(float float1) {
		return this.rotateLocalZ(float1, this);
	}

	public Matrix4f translate(Vector3fc vector3fc) {
		return this.translate(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4f translate(Vector3fc vector3fc, Matrix4f matrix4f) {
		return this.translate(vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix4f);
	}

	public Matrix4f translate(float float1, float float2, float float3, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.translation(float1, float2, float3) : this.translateGeneric(float1, float2, float3, matrix4f);
	}

	private Matrix4f translateGeneric(float float1, float float2, float float3, Matrix4f matrix4f) {
		MemUtil.INSTANCE.copy(this, matrix4f);
		return matrix4f._m30(Math.fma(this.m00, float1, Math.fma(this.m10, float2, Math.fma(this.m20, float3, this.m30))))._m31(Math.fma(this.m01, float1, Math.fma(this.m11, float2, Math.fma(this.m21, float3, this.m31))))._m32(Math.fma(this.m02, float1, Math.fma(this.m12, float2, Math.fma(this.m22, float3, this.m32))))._m33(Math.fma(this.m03, float1, Math.fma(this.m13, float2, Math.fma(this.m23, float3, this.m33))))._properties(this.properties & -6);
	}

	public Matrix4f translate(float float1, float float2, float float3) {
		return (this.properties & 4) != 0 ? this.translation(float1, float2, float3) : this.translateGeneric(float1, float2, float3);
	}

	private Matrix4f translateGeneric(float float1, float float2, float float3) {
		return this._m30(Math.fma(this.m00, float1, Math.fma(this.m10, float2, Math.fma(this.m20, float3, this.m30))))._m31(Math.fma(this.m01, float1, Math.fma(this.m11, float2, Math.fma(this.m21, float3, this.m31))))._m32(Math.fma(this.m02, float1, Math.fma(this.m12, float2, Math.fma(this.m22, float3, this.m32))))._m33(Math.fma(this.m03, float1, Math.fma(this.m13, float2, Math.fma(this.m23, float3, this.m33))))._properties(this.properties & -6);
	}

	public Matrix4f translateLocal(Vector3fc vector3fc) {
		return this.translateLocal(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4f translateLocal(Vector3fc vector3fc, Matrix4f matrix4f) {
		return this.translateLocal(vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix4f);
	}

	public Matrix4f translateLocal(float float1, float float2, float float3, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.translation(float1, float2, float3) : this.translateLocalGeneric(float1, float2, float3, matrix4f);
	}

	private Matrix4f translateLocalGeneric(float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = this.m00 + float1 * this.m03;
		float float5 = this.m01 + float2 * this.m03;
		float float6 = this.m02 + float3 * this.m03;
		float float7 = this.m10 + float1 * this.m13;
		float float8 = this.m11 + float2 * this.m13;
		float float9 = this.m12 + float3 * this.m13;
		float float10 = this.m20 + float1 * this.m23;
		float float11 = this.m21 + float2 * this.m23;
		float float12 = this.m22 + float3 * this.m23;
		float float13 = this.m30 + float1 * this.m33;
		float float14 = this.m31 + float2 * this.m33;
		float float15 = this.m32 + float3 * this.m33;
		return matrix4f._m00(float4)._m01(float5)._m02(float6)._m03(this.m03)._m10(float7)._m11(float8)._m12(float9)._m13(this.m13)._m20(float10)._m21(float11)._m22(float12)._m23(this.m23)._m30(float13)._m31(float14)._m32(float15)._m33(this.m33)._properties(this.properties & -6);
	}

	public Matrix4f translateLocal(float float1, float float2, float float3) {
		return this.translateLocal(float1, float2, float3, this);
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeFloat(this.m00);
		objectOutput.writeFloat(this.m01);
		objectOutput.writeFloat(this.m02);
		objectOutput.writeFloat(this.m03);
		objectOutput.writeFloat(this.m10);
		objectOutput.writeFloat(this.m11);
		objectOutput.writeFloat(this.m12);
		objectOutput.writeFloat(this.m13);
		objectOutput.writeFloat(this.m20);
		objectOutput.writeFloat(this.m21);
		objectOutput.writeFloat(this.m22);
		objectOutput.writeFloat(this.m23);
		objectOutput.writeFloat(this.m30);
		objectOutput.writeFloat(this.m31);
		objectOutput.writeFloat(this.m32);
		objectOutput.writeFloat(this.m33);
	}

	public void readExternal(ObjectInput objectInput) throws IOException {
		this._m00(objectInput.readFloat())._m01(objectInput.readFloat())._m02(objectInput.readFloat())._m03(objectInput.readFloat())._m10(objectInput.readFloat())._m11(objectInput.readFloat())._m12(objectInput.readFloat())._m13(objectInput.readFloat())._m20(objectInput.readFloat())._m21(objectInput.readFloat())._m22(objectInput.readFloat())._m23(objectInput.readFloat())._m30(objectInput.readFloat())._m31(objectInput.readFloat())._m32(objectInput.readFloat())._m33(objectInput.readFloat()).determineProperties();
	}

	public Matrix4f ortho(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setOrtho(float1, float2, float3, float4, float5, float6, boolean1) : this.orthoGeneric(float1, float2, float3, float4, float5, float6, boolean1, matrix4f);
	}

	private Matrix4f orthoGeneric(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f) {
		float float7 = 2.0F / (float2 - float1);
		float float8 = 2.0F / (float4 - float3);
		float float9 = (boolean1 ? 1.0F : 2.0F) / (float5 - float6);
		float float10 = (float1 + float2) / (float1 - float2);
		float float11 = (float4 + float3) / (float3 - float4);
		float float12 = (boolean1 ? float5 : float6 + float5) / (float5 - float6);
		matrix4f._m30(this.m00 * float10 + this.m10 * float11 + this.m20 * float12 + this.m30)._m31(this.m01 * float10 + this.m11 * float11 + this.m21 * float12 + this.m31)._m32(this.m02 * float10 + this.m12 * float11 + this.m22 * float12 + this.m32)._m33(this.m03 * float10 + this.m13 * float11 + this.m23 * float12 + this.m33)._m00(this.m00 * float7)._m01(this.m01 * float7)._m02(this.m02 * float7)._m03(this.m03 * float7)._m10(this.m10 * float8)._m11(this.m11 * float8)._m12(this.m12 * float8)._m13(this.m13 * float8)._m20(this.m20 * float9)._m21(this.m21 * float9)._m22(this.m22 * float9)._m23(this.m23 * float9)._properties(this.properties & -30);
		return matrix4f;
	}

	public Matrix4f ortho(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		return this.ortho(float1, float2, float3, float4, float5, float6, false, matrix4f);
	}

	public Matrix4f ortho(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		return this.ortho(float1, float2, float3, float4, float5, float6, boolean1, this);
	}

	public Matrix4f ortho(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.ortho(float1, float2, float3, float4, float5, float6, false);
	}

	public Matrix4f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setOrthoLH(float1, float2, float3, float4, float5, float6, boolean1) : this.orthoLHGeneric(float1, float2, float3, float4, float5, float6, boolean1, matrix4f);
	}

	private Matrix4f orthoLHGeneric(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f) {
		float float7 = 2.0F / (float2 - float1);
		float float8 = 2.0F / (float4 - float3);
		float float9 = (boolean1 ? 1.0F : 2.0F) / (float6 - float5);
		float float10 = (float1 + float2) / (float1 - float2);
		float float11 = (float4 + float3) / (float3 - float4);
		float float12 = (boolean1 ? float5 : float6 + float5) / (float5 - float6);
		matrix4f._m30(this.m00 * float10 + this.m10 * float11 + this.m20 * float12 + this.m30)._m31(this.m01 * float10 + this.m11 * float11 + this.m21 * float12 + this.m31)._m32(this.m02 * float10 + this.m12 * float11 + this.m22 * float12 + this.m32)._m33(this.m03 * float10 + this.m13 * float11 + this.m23 * float12 + this.m33)._m00(this.m00 * float7)._m01(this.m01 * float7)._m02(this.m02 * float7)._m03(this.m03 * float7)._m10(this.m10 * float8)._m11(this.m11 * float8)._m12(this.m12 * float8)._m13(this.m13 * float8)._m20(this.m20 * float9)._m21(this.m21 * float9)._m22(this.m22 * float9)._m23(this.m23 * float9)._properties(this.properties & -30);
		return matrix4f;
	}

	public Matrix4f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		return this.orthoLH(float1, float2, float3, float4, float5, float6, false, matrix4f);
	}

	public Matrix4f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		return this.orthoLH(float1, float2, float3, float4, float5, float6, boolean1, this);
	}

	public Matrix4f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.orthoLH(float1, float2, float3, float4, float5, float6, false);
	}

	public Matrix4f setOrtho(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this._m00(2.0F / (float2 - float1))._m11(2.0F / (float4 - float3))._m22((boolean1 ? 1.0F : 2.0F) / (float5 - float6))._m30((float2 + float1) / (float1 - float2))._m31((float4 + float3) / (float3 - float4))._m32((boolean1 ? float5 : float6 + float5) / (float5 - float6))._properties(2);
		return this;
	}

	public Matrix4f setOrtho(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.setOrtho(float1, float2, float3, float4, float5, float6, false);
	}

	public Matrix4f setOrthoLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this._m00(2.0F / (float2 - float1))._m11(2.0F / (float4 - float3))._m22((boolean1 ? 1.0F : 2.0F) / (float6 - float5))._m30((float2 + float1) / (float1 - float2))._m31((float4 + float3) / (float3 - float4))._m32((boolean1 ? float5 : float6 + float5) / (float5 - float6))._properties(2);
		return this;
	}

	public Matrix4f setOrthoLH(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.setOrthoLH(float1, float2, float3, float4, float5, float6, false);
	}

	public Matrix4f orthoSymmetric(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setOrthoSymmetric(float1, float2, float3, float4, boolean1) : this.orthoSymmetricGeneric(float1, float2, float3, float4, boolean1, matrix4f);
	}

	private Matrix4f orthoSymmetricGeneric(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f) {
		float float5 = 2.0F / float1;
		float float6 = 2.0F / float2;
		float float7 = (boolean1 ? 1.0F : 2.0F) / (float3 - float4);
		float float8 = (boolean1 ? float3 : float4 + float3) / (float3 - float4);
		matrix4f._m30(this.m20 * float8 + this.m30)._m31(this.m21 * float8 + this.m31)._m32(this.m22 * float8 + this.m32)._m33(this.m23 * float8 + this.m33)._m00(this.m00 * float5)._m01(this.m01 * float5)._m02(this.m02 * float5)._m03(this.m03 * float5)._m10(this.m10 * float6)._m11(this.m11 * float6)._m12(this.m12 * float6)._m13(this.m13 * float6)._m20(this.m20 * float7)._m21(this.m21 * float7)._m22(this.m22 * float7)._m23(this.m23 * float7)._properties(this.properties & -30);
		return matrix4f;
	}

	public Matrix4f orthoSymmetric(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return this.orthoSymmetric(float1, float2, float3, float4, false, matrix4f);
	}

	public Matrix4f orthoSymmetric(float float1, float float2, float float3, float float4, boolean boolean1) {
		return this.orthoSymmetric(float1, float2, float3, float4, boolean1, this);
	}

	public Matrix4f orthoSymmetric(float float1, float float2, float float3, float float4) {
		return this.orthoSymmetric(float1, float2, float3, float4, false, this);
	}

	public Matrix4f orthoSymmetricLH(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setOrthoSymmetricLH(float1, float2, float3, float4, boolean1) : this.orthoSymmetricLHGeneric(float1, float2, float3, float4, boolean1, matrix4f);
	}

	private Matrix4f orthoSymmetricLHGeneric(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f) {
		float float5 = 2.0F / float1;
		float float6 = 2.0F / float2;
		float float7 = (boolean1 ? 1.0F : 2.0F) / (float4 - float3);
		float float8 = (boolean1 ? float3 : float4 + float3) / (float3 - float4);
		matrix4f._m30(this.m20 * float8 + this.m30)._m31(this.m21 * float8 + this.m31)._m32(this.m22 * float8 + this.m32)._m33(this.m23 * float8 + this.m33)._m00(this.m00 * float5)._m01(this.m01 * float5)._m02(this.m02 * float5)._m03(this.m03 * float5)._m10(this.m10 * float6)._m11(this.m11 * float6)._m12(this.m12 * float6)._m13(this.m13 * float6)._m20(this.m20 * float7)._m21(this.m21 * float7)._m22(this.m22 * float7)._m23(this.m23 * float7)._properties(this.properties & -30);
		return matrix4f;
	}

	public Matrix4f orthoSymmetricLH(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return this.orthoSymmetricLH(float1, float2, float3, float4, false, matrix4f);
	}

	public Matrix4f orthoSymmetricLH(float float1, float float2, float float3, float float4, boolean boolean1) {
		return this.orthoSymmetricLH(float1, float2, float3, float4, boolean1, this);
	}

	public Matrix4f orthoSymmetricLH(float float1, float float2, float float3, float float4) {
		return this.orthoSymmetricLH(float1, float2, float3, float4, false, this);
	}

	public Matrix4f setOrthoSymmetric(float float1, float float2, float float3, float float4, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this._m00(2.0F / float1)._m11(2.0F / float2)._m22((boolean1 ? 1.0F : 2.0F) / (float3 - float4))._m32((boolean1 ? float3 : float4 + float3) / (float3 - float4))._properties(2);
		return this;
	}

	public Matrix4f setOrthoSymmetric(float float1, float float2, float float3, float float4) {
		return this.setOrthoSymmetric(float1, float2, float3, float4, false);
	}

	public Matrix4f setOrthoSymmetricLH(float float1, float float2, float float3, float float4, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this._m00(2.0F / float1)._m11(2.0F / float2)._m22((boolean1 ? 1.0F : 2.0F) / (float4 - float3))._m32((boolean1 ? float3 : float4 + float3) / (float3 - float4))._properties(2);
		return this;
	}

	public Matrix4f setOrthoSymmetricLH(float float1, float float2, float float3, float float4) {
		return this.setOrthoSymmetricLH(float1, float2, float3, float4, false);
	}

	public Matrix4f ortho2D(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setOrtho2D(float1, float2, float3, float4) : this.ortho2DGeneric(float1, float2, float3, float4, matrix4f);
	}

	private Matrix4f ortho2DGeneric(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		float float5 = 2.0F / (float2 - float1);
		float float6 = 2.0F / (float4 - float3);
		float float7 = (float2 + float1) / (float1 - float2);
		float float8 = (float4 + float3) / (float3 - float4);
		matrix4f._m30(this.m00 * float7 + this.m10 * float8 + this.m30)._m31(this.m01 * float7 + this.m11 * float8 + this.m31)._m32(this.m02 * float7 + this.m12 * float8 + this.m32)._m33(this.m03 * float7 + this.m13 * float8 + this.m33)._m00(this.m00 * float5)._m01(this.m01 * float5)._m02(this.m02 * float5)._m03(this.m03 * float5)._m10(this.m10 * float6)._m11(this.m11 * float6)._m12(this.m12 * float6)._m13(this.m13 * float6)._m20(-this.m20)._m21(-this.m21)._m22(-this.m22)._m23(-this.m23)._properties(this.properties & -30);
		return matrix4f;
	}

	public Matrix4f ortho2D(float float1, float float2, float float3, float float4) {
		return this.ortho2D(float1, float2, float3, float4, this);
	}

	public Matrix4f ortho2DLH(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setOrtho2DLH(float1, float2, float3, float4) : this.ortho2DLHGeneric(float1, float2, float3, float4, matrix4f);
	}

	private Matrix4f ortho2DLHGeneric(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		float float5 = 2.0F / (float2 - float1);
		float float6 = 2.0F / (float4 - float3);
		float float7 = (float2 + float1) / (float1 - float2);
		float float8 = (float4 + float3) / (float3 - float4);
		matrix4f._m30(this.m00 * float7 + this.m10 * float8 + this.m30)._m31(this.m01 * float7 + this.m11 * float8 + this.m31)._m32(this.m02 * float7 + this.m12 * float8 + this.m32)._m33(this.m03 * float7 + this.m13 * float8 + this.m33)._m00(this.m00 * float5)._m01(this.m01 * float5)._m02(this.m02 * float5)._m03(this.m03 * float5)._m10(this.m10 * float6)._m11(this.m11 * float6)._m12(this.m12 * float6)._m13(this.m13 * float6)._m20(this.m20)._m21(this.m21)._m22(this.m22)._m23(this.m23)._properties(this.properties & -30);
		return matrix4f;
	}

	public Matrix4f ortho2DLH(float float1, float float2, float float3, float float4) {
		return this.ortho2DLH(float1, float2, float3, float4, this);
	}

	public Matrix4f setOrtho2D(float float1, float float2, float float3, float float4) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this._m00(2.0F / (float2 - float1))._m11(2.0F / (float4 - float3))._m22(-1.0F)._m30((float2 + float1) / (float1 - float2))._m31((float4 + float3) / (float3 - float4))._properties(2);
		return this;
	}

	public Matrix4f setOrtho2DLH(float float1, float float2, float float3, float float4) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this._m00(2.0F / (float2 - float1))._m11(2.0F / (float4 - float3))._m30((float2 + float1) / (float1 - float2))._m31((float4 + float3) / (float3 - float4))._properties(2);
		return this;
	}

	public Matrix4f lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.lookAlong(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), this);
	}

	public Matrix4f lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4f matrix4f) {
		return this.lookAlong(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), matrix4f);
	}

	public Matrix4f lookAlong(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setLookAlong(float1, float2, float3, float4, float5, float6) : this.lookAlongGeneric(float1, float2, float3, float4, float5, float6, matrix4f);
	}

	private Matrix4f lookAlongGeneric(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		float float7 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float1 *= -float7;
		float2 *= -float7;
		float3 *= -float7;
		float float8 = float5 * float3 - float6 * float2;
		float float9 = float6 * float1 - float4 * float3;
		float float10 = float4 * float2 - float5 * float1;
		float float11 = Math.invsqrt(float8 * float8 + float9 * float9 + float10 * float10);
		float8 *= float11;
		float9 *= float11;
		float10 *= float11;
		float float12 = float2 * float10 - float3 * float9;
		float float13 = float3 * float8 - float1 * float10;
		float float14 = float1 * float9 - float2 * float8;
		float float15 = this.m00 * float8 + this.m10 * float12 + this.m20 * float1;
		float float16 = this.m01 * float8 + this.m11 * float12 + this.m21 * float1;
		float float17 = this.m02 * float8 + this.m12 * float12 + this.m22 * float1;
		float float18 = this.m03 * float8 + this.m13 * float12 + this.m23 * float1;
		float float19 = this.m00 * float9 + this.m10 * float13 + this.m20 * float2;
		float float20 = this.m01 * float9 + this.m11 * float13 + this.m21 * float2;
		float float21 = this.m02 * float9 + this.m12 * float13 + this.m22 * float2;
		float float22 = this.m03 * float9 + this.m13 * float13 + this.m23 * float2;
		return matrix4f._m20(this.m00 * float10 + this.m10 * float14 + this.m20 * float3)._m21(this.m01 * float10 + this.m11 * float14 + this.m21 * float3)._m22(this.m02 * float10 + this.m12 * float14 + this.m22 * float3)._m23(this.m03 * float10 + this.m13 * float14 + this.m23 * float3)._m00(float15)._m01(float16)._m02(float17)._m03(float18)._m10(float19)._m11(float20)._m12(float21)._m13(float22)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f lookAlong(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.lookAlong(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4f setLookAlong(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.setLookAlong(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix4f setLookAlong(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float1 *= -float7;
		float2 *= -float7;
		float3 *= -float7;
		float float8 = float5 * float3 - float6 * float2;
		float float9 = float6 * float1 - float4 * float3;
		float float10 = float4 * float2 - float5 * float1;
		float float11 = Math.invsqrt(float8 * float8 + float9 * float9 + float10 * float10);
		float8 *= float11;
		float9 *= float11;
		float10 *= float11;
		this._m00(float8)._m01(float2 * float10 - float3 * float9)._m02(float1)._m03(0.0F)._m10(float9)._m11(float3 * float8 - float1 * float10)._m12(float2)._m13(0.0F)._m20(float10)._m21(float1 * float9 - float2 * float8)._m22(float3)._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties(18);
		return this;
	}

	public Matrix4f setLookAt(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		return this.setLookAt(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z());
	}

	public Matrix4f setLookAt(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		float float10 = float1 - float4;
		float float11 = float2 - float5;
		float float12 = float3 - float6;
		float float13 = Math.invsqrt(float10 * float10 + float11 * float11 + float12 * float12);
		float10 *= float13;
		float11 *= float13;
		float12 *= float13;
		float float14 = float8 * float12 - float9 * float11;
		float float15 = float9 * float10 - float7 * float12;
		float float16 = float7 * float11 - float8 * float10;
		float float17 = Math.invsqrt(float14 * float14 + float15 * float15 + float16 * float16);
		float14 *= float17;
		float15 *= float17;
		float16 *= float17;
		float float18 = float11 * float16 - float12 * float15;
		float float19 = float12 * float14 - float10 * float16;
		float float20 = float10 * float15 - float11 * float14;
		return this._m00(float14)._m01(float18)._m02(float10)._m03(0.0F)._m10(float15)._m11(float19)._m12(float11)._m13(0.0F)._m20(float16)._m21(float20)._m22(float12)._m23(0.0F)._m30(-(float14 * float1 + float15 * float2 + float16 * float3))._m31(-(float18 * float1 + float19 * float2 + float20 * float3))._m32(-(float10 * float1 + float11 * float2 + float12 * float3))._m33(1.0F)._properties(18);
	}

	public Matrix4f lookAt(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Matrix4f matrix4f) {
		return this.lookAt(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), matrix4f);
	}

	public Matrix4f lookAt(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		return this.lookAt(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), this);
	}

	public Matrix4f lookAt(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.setLookAt(float1, float2, float3, float4, float5, float6, float7, float8, float9);
		} else {
			return (this.properties & 1) != 0 ? this.lookAtPerspective(float1, float2, float3, float4, float5, float6, float7, float8, float9, matrix4f) : this.lookAtGeneric(float1, float2, float3, float4, float5, float6, float7, float8, float9, matrix4f);
		}
	}

	private Matrix4f lookAtGeneric(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f) {
		float float10 = float1 - float4;
		float float11 = float2 - float5;
		float float12 = float3 - float6;
		float float13 = Math.invsqrt(float10 * float10 + float11 * float11 + float12 * float12);
		float10 *= float13;
		float11 *= float13;
		float12 *= float13;
		float float14 = float8 * float12 - float9 * float11;
		float float15 = float9 * float10 - float7 * float12;
		float float16 = float7 * float11 - float8 * float10;
		float float17 = Math.invsqrt(float14 * float14 + float15 * float15 + float16 * float16);
		float14 *= float17;
		float15 *= float17;
		float16 *= float17;
		float float18 = float11 * float16 - float12 * float15;
		float float19 = float12 * float14 - float10 * float16;
		float float20 = float10 * float15 - float11 * float14;
		float float21 = -(float14 * float1 + float15 * float2 + float16 * float3);
		float float22 = -(float18 * float1 + float19 * float2 + float20 * float3);
		float float23 = -(float10 * float1 + float11 * float2 + float12 * float3);
		float float24 = this.m00 * float14 + this.m10 * float18 + this.m20 * float10;
		float float25 = this.m01 * float14 + this.m11 * float18 + this.m21 * float10;
		float float26 = this.m02 * float14 + this.m12 * float18 + this.m22 * float10;
		float float27 = this.m03 * float14 + this.m13 * float18 + this.m23 * float10;
		float float28 = this.m00 * float15 + this.m10 * float19 + this.m20 * float11;
		float float29 = this.m01 * float15 + this.m11 * float19 + this.m21 * float11;
		float float30 = this.m02 * float15 + this.m12 * float19 + this.m22 * float11;
		float float31 = this.m03 * float15 + this.m13 * float19 + this.m23 * float11;
		return matrix4f._m30(this.m00 * float21 + this.m10 * float22 + this.m20 * float23 + this.m30)._m31(this.m01 * float21 + this.m11 * float22 + this.m21 * float23 + this.m31)._m32(this.m02 * float21 + this.m12 * float22 + this.m22 * float23 + this.m32)._m33(this.m03 * float21 + this.m13 * float22 + this.m23 * float23 + this.m33)._m20(this.m00 * float16 + this.m10 * float20 + this.m20 * float12)._m21(this.m01 * float16 + this.m11 * float20 + this.m21 * float12)._m22(this.m02 * float16 + this.m12 * float20 + this.m22 * float12)._m23(this.m03 * float16 + this.m13 * float20 + this.m23 * float12)._m00(float24)._m01(float25)._m02(float26)._m03(float27)._m10(float28)._m11(float29)._m12(float30)._m13(float31)._properties(this.properties & -14);
	}

	public Matrix4f lookAtPerspective(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f) {
		float float10 = float1 - float4;
		float float11 = float2 - float5;
		float float12 = float3 - float6;
		float float13 = Math.invsqrt(float10 * float10 + float11 * float11 + float12 * float12);
		float10 *= float13;
		float11 *= float13;
		float12 *= float13;
		float float14 = float8 * float12 - float9 * float11;
		float float15 = float9 * float10 - float7 * float12;
		float float16 = float7 * float11 - float8 * float10;
		float float17 = Math.invsqrt(float14 * float14 + float15 * float15 + float16 * float16);
		float14 *= float17;
		float15 *= float17;
		float16 *= float17;
		float float18 = float11 * float16 - float12 * float15;
		float float19 = float12 * float14 - float10 * float16;
		float float20 = float10 * float15 - float11 * float14;
		float float21 = -(float14 * float1 + float15 * float2 + float16 * float3);
		float float22 = -(float18 * float1 + float19 * float2 + float20 * float3);
		float float23 = -(float10 * float1 + float11 * float2 + float12 * float3);
		float float24 = this.m00 * float15;
		float float25 = this.m00 * float16;
		float float26 = this.m11 * float20;
		float float27 = this.m00 * float21;
		float float28 = this.m11 * float22;
		float float29 = this.m22 * float23 + this.m32;
		float float30 = this.m23 * float23;
		return matrix4f._m00(this.m00 * float14)._m01(this.m11 * float18)._m02(this.m22 * float10)._m03(this.m23 * float10)._m10(float24)._m11(this.m11 * float19)._m12(this.m22 * float11)._m13(this.m23 * float11)._m20(float25)._m21(float26)._m22(this.m22 * float12)._m23(this.m23 * float12)._m30(float27)._m31(float28)._m32(float29)._m33(float30)._properties(0);
	}

	public Matrix4f lookAt(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		return this.lookAt(float1, float2, float3, float4, float5, float6, float7, float8, float9, this);
	}

	public Matrix4f setLookAtLH(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		return this.setLookAtLH(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z());
	}

	public Matrix4f setLookAtLH(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		float float10 = float4 - float1;
		float float11 = float5 - float2;
		float float12 = float6 - float3;
		float float13 = Math.invsqrt(float10 * float10 + float11 * float11 + float12 * float12);
		float10 *= float13;
		float11 *= float13;
		float12 *= float13;
		float float14 = float8 * float12 - float9 * float11;
		float float15 = float9 * float10 - float7 * float12;
		float float16 = float7 * float11 - float8 * float10;
		float float17 = Math.invsqrt(float14 * float14 + float15 * float15 + float16 * float16);
		float14 *= float17;
		float15 *= float17;
		float16 *= float17;
		float float18 = float11 * float16 - float12 * float15;
		float float19 = float12 * float14 - float10 * float16;
		float float20 = float10 * float15 - float11 * float14;
		this._m00(float14)._m01(float18)._m02(float10)._m03(0.0F)._m10(float15)._m11(float19)._m12(float11)._m13(0.0F)._m20(float16)._m21(float20)._m22(float12)._m23(0.0F)._m30(-(float14 * float1 + float15 * float2 + float16 * float3))._m31(-(float18 * float1 + float19 * float2 + float20 * float3))._m32(-(float10 * float1 + float11 * float2 + float12 * float3))._m33(1.0F)._properties(18);
		return this;
	}

	public Matrix4f lookAtLH(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Matrix4f matrix4f) {
		return this.lookAtLH(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), matrix4f);
	}

	public Matrix4f lookAtLH(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		return this.lookAtLH(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), this);
	}

	public Matrix4f lookAtLH(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.setLookAtLH(float1, float2, float3, float4, float5, float6, float7, float8, float9);
		} else {
			return (this.properties & 1) != 0 ? this.lookAtPerspectiveLH(float1, float2, float3, float4, float5, float6, float7, float8, float9, matrix4f) : this.lookAtLHGeneric(float1, float2, float3, float4, float5, float6, float7, float8, float9, matrix4f);
		}
	}

	private Matrix4f lookAtLHGeneric(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f) {
		float float10 = float4 - float1;
		float float11 = float5 - float2;
		float float12 = float6 - float3;
		float float13 = Math.invsqrt(float10 * float10 + float11 * float11 + float12 * float12);
		float10 *= float13;
		float11 *= float13;
		float12 *= float13;
		float float14 = float8 * float12 - float9 * float11;
		float float15 = float9 * float10 - float7 * float12;
		float float16 = float7 * float11 - float8 * float10;
		float float17 = Math.invsqrt(float14 * float14 + float15 * float15 + float16 * float16);
		float14 *= float17;
		float15 *= float17;
		float16 *= float17;
		float float18 = float11 * float16 - float12 * float15;
		float float19 = float12 * float14 - float10 * float16;
		float float20 = float10 * float15 - float11 * float14;
		float float21 = -(float14 * float1 + float15 * float2 + float16 * float3);
		float float22 = -(float18 * float1 + float19 * float2 + float20 * float3);
		float float23 = -(float10 * float1 + float11 * float2 + float12 * float3);
		float float24 = this.m00 * float14 + this.m10 * float18 + this.m20 * float10;
		float float25 = this.m01 * float14 + this.m11 * float18 + this.m21 * float10;
		float float26 = this.m02 * float14 + this.m12 * float18 + this.m22 * float10;
		float float27 = this.m03 * float14 + this.m13 * float18 + this.m23 * float10;
		float float28 = this.m00 * float15 + this.m10 * float19 + this.m20 * float11;
		float float29 = this.m01 * float15 + this.m11 * float19 + this.m21 * float11;
		float float30 = this.m02 * float15 + this.m12 * float19 + this.m22 * float11;
		float float31 = this.m03 * float15 + this.m13 * float19 + this.m23 * float11;
		return matrix4f._m30(this.m00 * float21 + this.m10 * float22 + this.m20 * float23 + this.m30)._m31(this.m01 * float21 + this.m11 * float22 + this.m21 * float23 + this.m31)._m32(this.m02 * float21 + this.m12 * float22 + this.m22 * float23 + this.m32)._m33(this.m03 * float21 + this.m13 * float22 + this.m23 * float23 + this.m33)._m20(this.m00 * float16 + this.m10 * float20 + this.m20 * float12)._m21(this.m01 * float16 + this.m11 * float20 + this.m21 * float12)._m22(this.m02 * float16 + this.m12 * float20 + this.m22 * float12)._m23(this.m03 * float16 + this.m13 * float20 + this.m23 * float12)._m00(float24)._m01(float25)._m02(float26)._m03(float27)._m10(float28)._m11(float29)._m12(float30)._m13(float31)._properties(this.properties & -14);
	}

	public Matrix4f lookAtLH(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		return this.lookAtLH(float1, float2, float3, float4, float5, float6, float7, float8, float9, this);
	}

	public Matrix4f lookAtPerspectiveLH(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f) {
		float float10 = float4 - float1;
		float float11 = float5 - float2;
		float float12 = float6 - float3;
		float float13 = Math.invsqrt(float10 * float10 + float11 * float11 + float12 * float12);
		float10 *= float13;
		float11 *= float13;
		float12 *= float13;
		float float14 = float8 * float12 - float9 * float11;
		float float15 = float9 * float10 - float7 * float12;
		float float16 = float7 * float11 - float8 * float10;
		float float17 = Math.invsqrt(float14 * float14 + float15 * float15 + float16 * float16);
		float14 *= float17;
		float15 *= float17;
		float16 *= float17;
		float float18 = float11 * float16 - float12 * float15;
		float float19 = float12 * float14 - float10 * float16;
		float float20 = float10 * float15 - float11 * float14;
		float float21 = -(float14 * float1 + float15 * float2 + float16 * float3);
		float float22 = -(float18 * float1 + float19 * float2 + float20 * float3);
		float float23 = -(float10 * float1 + float11 * float2 + float12 * float3);
		float float24 = this.m00 * float14;
		float float25 = this.m11 * float18;
		float float26 = this.m22 * float10;
		float float27 = this.m23 * float10;
		float float28 = this.m00 * float15;
		float float29 = this.m11 * float19;
		float float30 = this.m22 * float11;
		float float31 = this.m23 * float11;
		float float32 = this.m00 * float16;
		float float33 = this.m11 * float20;
		float float34 = this.m22 * float12;
		float float35 = this.m23 * float12;
		float float36 = this.m00 * float21;
		float float37 = this.m11 * float22;
		float float38 = this.m22 * float23 + this.m32;
		float float39 = this.m23 * float23;
		return matrix4f._m00(float24)._m01(float25)._m02(float26)._m03(float27)._m10(float28)._m11(float29)._m12(float30)._m13(float31)._m20(float32)._m21(float33)._m22(float34)._m23(float35)._m30(float36)._m31(float37)._m32(float38)._m33(float39)._properties(0);
	}

	public Matrix4f perspective(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setPerspective(float1, float2, float3, float4, boolean1) : this.perspectiveGeneric(float1, float2, float3, float4, boolean1, matrix4f);
	}

	private Matrix4f perspectiveGeneric(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f) {
		float float5 = Math.tan(float1 * 0.5F);
		float float6 = 1.0F / (float5 * float2);
		float float7 = 1.0F / float5;
		boolean boolean2 = float4 > 0.0F && Float.isInfinite(float4);
		boolean boolean3 = float3 > 0.0F && Float.isInfinite(float3);
		float float8;
		float float9;
		float float10;
		if (boolean2) {
			float10 = 1.0E-6F;
			float8 = float10 - 1.0F;
			float9 = (float10 - (boolean1 ? 1.0F : 2.0F)) * float3;
		} else if (boolean3) {
			float10 = 1.0E-6F;
			float8 = (boolean1 ? 0.0F : 1.0F) - float10;
			float9 = ((boolean1 ? 1.0F : 2.0F) - float10) * float4;
		} else {
			float8 = (boolean1 ? float4 : float4 + float3) / (float3 - float4);
			float9 = (boolean1 ? float4 : float4 + float4) * float3 / (float3 - float4);
		}

		float10 = this.m20 * float8 - this.m30;
		float float11 = this.m21 * float8 - this.m31;
		float float12 = this.m22 * float8 - this.m32;
		float float13 = this.m23 * float8 - this.m33;
		matrix4f._m00(this.m00 * float6)._m01(this.m01 * float6)._m02(this.m02 * float6)._m03(this.m03 * float6)._m10(this.m10 * float7)._m11(this.m11 * float7)._m12(this.m12 * float7)._m13(this.m13 * float7)._m30(this.m20 * float9)._m31(this.m21 * float9)._m32(this.m22 * float9)._m33(this.m23 * float9)._m20(float10)._m21(float11)._m22(float12)._m23(float13)._properties(this.properties & -31);
		return matrix4f;
	}

	public Matrix4f perspective(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return this.perspective(float1, float2, float3, float4, false, matrix4f);
	}

	public Matrix4f perspective(float float1, float float2, float float3, float float4, boolean boolean1) {
		return this.perspective(float1, float2, float3, float4, boolean1, this);
	}

	public Matrix4f perspective(float float1, float float2, float float3, float float4) {
		return this.perspective(float1, float2, float3, float4, this);
	}

	public Matrix4f perspectiveRect(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setPerspectiveRect(float1, float2, float3, float4, boolean1) : this.perspectiveRectGeneric(float1, float2, float3, float4, boolean1, matrix4f);
	}

	private Matrix4f perspectiveRectGeneric(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f) {
		float float5 = (float3 + float3) / float1;
		float float6 = (float3 + float3) / float2;
		boolean boolean2 = float4 > 0.0F && Float.isInfinite(float4);
		boolean boolean3 = float3 > 0.0F && Float.isInfinite(float3);
		float float7;
		float float8;
		float float9;
		if (boolean2) {
			float9 = 1.0E-6F;
			float7 = float9 - 1.0F;
			float8 = (float9 - (boolean1 ? 1.0F : 2.0F)) * float3;
		} else if (boolean3) {
			float9 = 1.0E-6F;
			float7 = (boolean1 ? 0.0F : 1.0F) - float9;
			float8 = ((boolean1 ? 1.0F : 2.0F) - float9) * float4;
		} else {
			float7 = (boolean1 ? float4 : float4 + float3) / (float3 - float4);
			float8 = (boolean1 ? float4 : float4 + float4) * float3 / (float3 - float4);
		}

		float9 = this.m20 * float7 - this.m30;
		float float10 = this.m21 * float7 - this.m31;
		float float11 = this.m22 * float7 - this.m32;
		float float12 = this.m23 * float7 - this.m33;
		matrix4f._m00(this.m00 * float5)._m01(this.m01 * float5)._m02(this.m02 * float5)._m03(this.m03 * float5)._m10(this.m10 * float6)._m11(this.m11 * float6)._m12(this.m12 * float6)._m13(this.m13 * float6)._m30(this.m20 * float8)._m31(this.m21 * float8)._m32(this.m22 * float8)._m33(this.m23 * float8)._m20(float9)._m21(float10)._m22(float11)._m23(float12)._properties(this.properties & -31);
		return matrix4f;
	}

	public Matrix4f perspectiveRect(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return this.perspectiveRect(float1, float2, float3, float4, false, matrix4f);
	}

	public Matrix4f perspectiveRect(float float1, float float2, float float3, float float4, boolean boolean1) {
		return this.perspectiveRect(float1, float2, float3, float4, boolean1, this);
	}

	public Matrix4f perspectiveRect(float float1, float float2, float float3, float float4) {
		return this.perspectiveRect(float1, float2, float3, float4, this);
	}

	public Matrix4f perspectiveOffCenter(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setPerspectiveOffCenter(float1, float2, float3, float4, float5, float6, boolean1) : this.perspectiveOffCenterGeneric(float1, float2, float3, float4, float5, float6, boolean1, matrix4f);
	}

	private Matrix4f perspectiveOffCenterGeneric(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f) {
		float float7 = Math.tan(float1 * 0.5F);
		float float8 = 1.0F / (float7 * float4);
		float float9 = 1.0F / float7;
		float float10 = Math.tan(float2);
		float float11 = Math.tan(float3);
		float float12 = float10 * float8;
		float float13 = float11 * float9;
		boolean boolean2 = float6 > 0.0F && Float.isInfinite(float6);
		boolean boolean3 = float5 > 0.0F && Float.isInfinite(float5);
		float float14;
		float float15;
		float float16;
		if (boolean2) {
			float16 = 1.0E-6F;
			float14 = float16 - 1.0F;
			float15 = (float16 - (boolean1 ? 1.0F : 2.0F)) * float5;
		} else if (boolean3) {
			float16 = 1.0E-6F;
			float14 = (boolean1 ? 0.0F : 1.0F) - float16;
			float15 = ((boolean1 ? 1.0F : 2.0F) - float16) * float6;
		} else {
			float14 = (boolean1 ? float6 : float6 + float5) / (float5 - float6);
			float15 = (boolean1 ? float6 : float6 + float6) * float5 / (float5 - float6);
		}

		float16 = this.m00 * float12 + this.m10 * float13 + this.m20 * float14 - this.m30;
		float float17 = this.m01 * float12 + this.m11 * float13 + this.m21 * float14 - this.m31;
		float float18 = this.m02 * float12 + this.m12 * float13 + this.m22 * float14 - this.m32;
		float float19 = this.m03 * float12 + this.m13 * float13 + this.m23 * float14 - this.m33;
		matrix4f._m00(this.m00 * float8)._m01(this.m01 * float8)._m02(this.m02 * float8)._m03(this.m03 * float8)._m10(this.m10 * float9)._m11(this.m11 * float9)._m12(this.m12 * float9)._m13(this.m13 * float9)._m30(this.m20 * float15)._m31(this.m21 * float15)._m32(this.m22 * float15)._m33(this.m23 * float15)._m20(float16)._m21(float17)._m22(float18)._m23(float19)._properties(this.properties & ~(30 | (float12 == 0.0F && float13 == 0.0F ? 0 : 1)));
		return matrix4f;
	}

	public Matrix4f perspectiveOffCenter(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		return this.perspectiveOffCenter(float1, float2, float3, float4, float5, float6, false, matrix4f);
	}

	public Matrix4f perspectiveOffCenter(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		return this.perspectiveOffCenter(float1, float2, float3, float4, float5, float6, boolean1, this);
	}

	public Matrix4f perspectiveOffCenter(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.perspectiveOffCenter(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4f setPerspective(float float1, float float2, float float3, float float4, boolean boolean1) {
		MemUtil.INSTANCE.zero(this);
		float float5 = Math.tan(float1 * 0.5F);
		this._m00(1.0F / (float5 * float2))._m11(1.0F / float5);
		boolean boolean2 = float4 > 0.0F && Float.isInfinite(float4);
		boolean boolean3 = float3 > 0.0F && Float.isInfinite(float3);
		float float6;
		if (boolean2) {
			float6 = 1.0E-6F;
			this._m22(float6 - 1.0F)._m32((float6 - (boolean1 ? 1.0F : 2.0F)) * float3);
		} else if (boolean3) {
			float6 = 1.0E-6F;
			this._m22((boolean1 ? 0.0F : 1.0F) - float6)._m32(((boolean1 ? 1.0F : 2.0F) - float6) * float4);
		} else {
			this._m22((boolean1 ? float4 : float4 + float3) / (float3 - float4))._m32((boolean1 ? float4 : float4 + float4) * float3 / (float3 - float4));
		}

		return this._m23(-1.0F)._properties(1);
	}

	public Matrix4f setPerspective(float float1, float float2, float float3, float float4) {
		return this.setPerspective(float1, float2, float3, float4, false);
	}

	public Matrix4f setPerspectiveRect(float float1, float float2, float float3, float float4, boolean boolean1) {
		MemUtil.INSTANCE.zero(this);
		this._m00((float3 + float3) / float1)._m11((float3 + float3) / float2);
		boolean boolean2 = float4 > 0.0F && Float.isInfinite(float4);
		boolean boolean3 = float3 > 0.0F && Float.isInfinite(float3);
		float float5;
		if (boolean2) {
			float5 = 1.0E-6F;
			this._m22(float5 - 1.0F)._m32((float5 - (boolean1 ? 1.0F : 2.0F)) * float3);
		} else if (boolean3) {
			float5 = 1.0E-6F;
			this._m22((boolean1 ? 0.0F : 1.0F) - float5)._m32(((boolean1 ? 1.0F : 2.0F) - float5) * float4);
		} else {
			this._m22((boolean1 ? float4 : float4 + float3) / (float3 - float4))._m32((boolean1 ? float4 : float4 + float4) * float3 / (float3 - float4));
		}

		this._m23(-1.0F)._properties(1);
		return this;
	}

	public Matrix4f setPerspectiveRect(float float1, float float2, float float3, float float4) {
		return this.setPerspectiveRect(float1, float2, float3, float4, false);
	}

	public Matrix4f setPerspectiveOffCenter(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.setPerspectiveOffCenter(float1, float2, float3, float4, float5, float6, false);
	}

	public Matrix4f setPerspectiveOffCenter(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		MemUtil.INSTANCE.zero(this);
		float float7 = Math.tan(float1 * 0.5F);
		float float8 = 1.0F / (float7 * float4);
		float float9 = 1.0F / float7;
		float float10 = Math.tan(float2);
		float float11 = Math.tan(float3);
		this._m00(float8)._m11(float9)._m20(float10 * float8)._m21(float11 * float9);
		boolean boolean2 = float6 > 0.0F && Float.isInfinite(float6);
		boolean boolean3 = float5 > 0.0F && Float.isInfinite(float5);
		float float12;
		if (boolean2) {
			float12 = 1.0E-6F;
			this._m22(float12 - 1.0F)._m32((float12 - (boolean1 ? 1.0F : 2.0F)) * float5);
		} else if (boolean3) {
			float12 = 1.0E-6F;
			this._m22((boolean1 ? 0.0F : 1.0F) - float12)._m32(((boolean1 ? 1.0F : 2.0F) - float12) * float6);
		} else {
			this._m22((boolean1 ? float6 : float6 + float5) / (float5 - float6))._m32((boolean1 ? float6 : float6 + float6) * float5 / (float5 - float6));
		}

		this._m23(-1.0F)._properties(float2 == 0.0F && float3 == 0.0F ? 1 : 0);
		return this;
	}

	public Matrix4f perspectiveLH(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setPerspectiveLH(float1, float2, float3, float4, boolean1) : this.perspectiveLHGeneric(float1, float2, float3, float4, boolean1, matrix4f);
	}

	private Matrix4f perspectiveLHGeneric(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f) {
		float float5 = Math.tan(float1 * 0.5F);
		float float6 = 1.0F / (float5 * float2);
		float float7 = 1.0F / float5;
		boolean boolean2 = float4 > 0.0F && Float.isInfinite(float4);
		boolean boolean3 = float3 > 0.0F && Float.isInfinite(float3);
		float float8;
		float float9;
		float float10;
		if (boolean2) {
			float10 = 1.0E-6F;
			float8 = 1.0F - float10;
			float9 = (float10 - (boolean1 ? 1.0F : 2.0F)) * float3;
		} else if (boolean3) {
			float10 = 1.0E-6F;
			float8 = (boolean1 ? 0.0F : 1.0F) - float10;
			float9 = ((boolean1 ? 1.0F : 2.0F) - float10) * float4;
		} else {
			float8 = (boolean1 ? float4 : float4 + float3) / (float4 - float3);
			float9 = (boolean1 ? float4 : float4 + float4) * float3 / (float3 - float4);
		}

		float10 = this.m20 * float8 + this.m30;
		float float11 = this.m21 * float8 + this.m31;
		float float12 = this.m22 * float8 + this.m32;
		float float13 = this.m23 * float8 + this.m33;
		matrix4f._m00(this.m00 * float6)._m01(this.m01 * float6)._m02(this.m02 * float6)._m03(this.m03 * float6)._m10(this.m10 * float7)._m11(this.m11 * float7)._m12(this.m12 * float7)._m13(this.m13 * float7)._m30(this.m20 * float9)._m31(this.m21 * float9)._m32(this.m22 * float9)._m33(this.m23 * float9)._m20(float10)._m21(float11)._m22(float12)._m23(float13)._properties(this.properties & -31);
		return matrix4f;
	}

	public Matrix4f perspectiveLH(float float1, float float2, float float3, float float4, boolean boolean1) {
		return this.perspectiveLH(float1, float2, float3, float4, boolean1, this);
	}

	public Matrix4f perspectiveLH(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return this.perspectiveLH(float1, float2, float3, float4, false, matrix4f);
	}

	public Matrix4f perspectiveLH(float float1, float float2, float float3, float float4) {
		return this.perspectiveLH(float1, float2, float3, float4, this);
	}

	public Matrix4f setPerspectiveLH(float float1, float float2, float float3, float float4, boolean boolean1) {
		MemUtil.INSTANCE.zero(this);
		float float5 = Math.tan(float1 * 0.5F);
		this._m00(1.0F / (float5 * float2))._m11(1.0F / float5);
		boolean boolean2 = float4 > 0.0F && Float.isInfinite(float4);
		boolean boolean3 = float3 > 0.0F && Float.isInfinite(float3);
		float float6;
		if (boolean2) {
			float6 = 1.0E-6F;
			this._m22(1.0F - float6)._m32((float6 - (boolean1 ? 1.0F : 2.0F)) * float3);
		} else if (boolean3) {
			float6 = 1.0E-6F;
			this._m22((boolean1 ? 0.0F : 1.0F) - float6)._m32(((boolean1 ? 1.0F : 2.0F) - float6) * float4);
		} else {
			this._m22((boolean1 ? float4 : float4 + float3) / (float4 - float3))._m32((boolean1 ? float4 : float4 + float4) * float3 / (float3 - float4));
		}

		this._m23(1.0F)._properties(1);
		return this;
	}

	public Matrix4f setPerspectiveLH(float float1, float float2, float float3, float float4) {
		return this.setPerspectiveLH(float1, float2, float3, float4, false);
	}

	public Matrix4f frustum(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setFrustum(float1, float2, float3, float4, float5, float6, boolean1) : this.frustumGeneric(float1, float2, float3, float4, float5, float6, boolean1, matrix4f);
	}

	private Matrix4f frustumGeneric(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f) {
		float float7 = (float5 + float5) / (float2 - float1);
		float float8 = (float5 + float5) / (float4 - float3);
		float float9 = (float2 + float1) / (float2 - float1);
		float float10 = (float4 + float3) / (float4 - float3);
		boolean boolean2 = float6 > 0.0F && Float.isInfinite(float6);
		boolean boolean3 = float5 > 0.0F && Float.isInfinite(float5);
		float float11;
		float float12;
		float float13;
		if (boolean2) {
			float13 = 1.0E-6F;
			float11 = float13 - 1.0F;
			float12 = (float13 - (boolean1 ? 1.0F : 2.0F)) * float5;
		} else if (boolean3) {
			float13 = 1.0E-6F;
			float11 = (boolean1 ? 0.0F : 1.0F) - float13;
			float12 = ((boolean1 ? 1.0F : 2.0F) - float13) * float6;
		} else {
			float11 = (boolean1 ? float6 : float6 + float5) / (float5 - float6);
			float12 = (boolean1 ? float6 : float6 + float6) * float5 / (float5 - float6);
		}

		float13 = this.m00 * float9 + this.m10 * float10 + this.m20 * float11 - this.m30;
		float float14 = this.m01 * float9 + this.m11 * float10 + this.m21 * float11 - this.m31;
		float float15 = this.m02 * float9 + this.m12 * float10 + this.m22 * float11 - this.m32;
		float float16 = this.m03 * float9 + this.m13 * float10 + this.m23 * float11 - this.m33;
		matrix4f._m00(this.m00 * float7)._m01(this.m01 * float7)._m02(this.m02 * float7)._m03(this.m03 * float7)._m10(this.m10 * float8)._m11(this.m11 * float8)._m12(this.m12 * float8)._m13(this.m13 * float8)._m30(this.m20 * float12)._m31(this.m21 * float12)._m32(this.m22 * float12)._m33(this.m23 * float12)._m20(float13)._m21(float14)._m22(float15)._m23(float16)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(0);
		return matrix4f;
	}

	public Matrix4f frustum(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		return this.frustum(float1, float2, float3, float4, float5, float6, false, matrix4f);
	}

	public Matrix4f frustum(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		return this.frustum(float1, float2, float3, float4, float5, float6, boolean1, this);
	}

	public Matrix4f frustum(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.frustum(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4f setFrustum(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this._m00((float5 + float5) / (float2 - float1))._m11((float5 + float5) / (float4 - float3))._m20((float2 + float1) / (float2 - float1))._m21((float4 + float3) / (float4 - float3));
		boolean boolean2 = float6 > 0.0F && Float.isInfinite(float6);
		boolean boolean3 = float5 > 0.0F && Float.isInfinite(float5);
		float float7;
		if (boolean2) {
			float7 = 1.0E-6F;
			this._m22(float7 - 1.0F)._m32((float7 - (boolean1 ? 1.0F : 2.0F)) * float5);
		} else if (boolean3) {
			float7 = 1.0E-6F;
			this._m22((boolean1 ? 0.0F : 1.0F) - float7)._m32(((boolean1 ? 1.0F : 2.0F) - float7) * float6);
		} else {
			this._m22((boolean1 ? float6 : float6 + float5) / (float5 - float6))._m32((boolean1 ? float6 : float6 + float6) * float5 / (float5 - float6));
		}

		this._m23(-1.0F)._m33(0.0F)._properties(this.m20 == 0.0F && this.m21 == 0.0F ? 1 : 0);
		return this;
	}

	public Matrix4f setFrustum(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.setFrustum(float1, float2, float3, float4, float5, float6, false);
	}

	public Matrix4f frustumLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f) {
		return (this.properties & 4) != 0 ? matrix4f.setFrustumLH(float1, float2, float3, float4, float5, float6, boolean1) : this.frustumLHGeneric(float1, float2, float3, float4, float5, float6, boolean1, matrix4f);
	}

	private Matrix4f frustumLHGeneric(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f) {
		float float7 = (float5 + float5) / (float2 - float1);
		float float8 = (float5 + float5) / (float4 - float3);
		float float9 = (float2 + float1) / (float2 - float1);
		float float10 = (float4 + float3) / (float4 - float3);
		boolean boolean2 = float6 > 0.0F && Float.isInfinite(float6);
		boolean boolean3 = float5 > 0.0F && Float.isInfinite(float5);
		float float11;
		float float12;
		float float13;
		if (boolean2) {
			float13 = 1.0E-6F;
			float11 = 1.0F - float13;
			float12 = (float13 - (boolean1 ? 1.0F : 2.0F)) * float5;
		} else if (boolean3) {
			float13 = 1.0E-6F;
			float11 = (boolean1 ? 0.0F : 1.0F) - float13;
			float12 = ((boolean1 ? 1.0F : 2.0F) - float13) * float6;
		} else {
			float11 = (boolean1 ? float6 : float6 + float5) / (float6 - float5);
			float12 = (boolean1 ? float6 : float6 + float6) * float5 / (float5 - float6);
		}

		float13 = this.m00 * float9 + this.m10 * float10 + this.m20 * float11 + this.m30;
		float float14 = this.m01 * float9 + this.m11 * float10 + this.m21 * float11 + this.m31;
		float float15 = this.m02 * float9 + this.m12 * float10 + this.m22 * float11 + this.m32;
		float float16 = this.m03 * float9 + this.m13 * float10 + this.m23 * float11 + this.m33;
		matrix4f._m00(this.m00 * float7)._m01(this.m01 * float7)._m02(this.m02 * float7)._m03(this.m03 * float7)._m10(this.m10 * float8)._m11(this.m11 * float8)._m12(this.m12 * float8)._m13(this.m13 * float8)._m30(this.m20 * float12)._m31(this.m21 * float12)._m32(this.m22 * float12)._m33(this.m23 * float12)._m20(float13)._m21(float14)._m22(float15)._m23(float16)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(0);
		return matrix4f;
	}

	public Matrix4f frustumLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		return this.frustumLH(float1, float2, float3, float4, float5, float6, boolean1, this);
	}

	public Matrix4f frustumLH(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		return this.frustumLH(float1, float2, float3, float4, float5, float6, false, matrix4f);
	}

	public Matrix4f frustumLH(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.frustumLH(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4f setFrustumLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this._m00((float5 + float5) / (float2 - float1))._m11((float5 + float5) / (float4 - float3))._m20((float2 + float1) / (float2 - float1))._m21((float4 + float3) / (float4 - float3));
		boolean boolean2 = float6 > 0.0F && Float.isInfinite(float6);
		boolean boolean3 = float5 > 0.0F && Float.isInfinite(float5);
		float float7;
		if (boolean2) {
			float7 = 1.0E-6F;
			this._m22(1.0F - float7)._m32((float7 - (boolean1 ? 1.0F : 2.0F)) * float5);
		} else if (boolean3) {
			float7 = 1.0E-6F;
			this._m22((boolean1 ? 0.0F : 1.0F) - float7)._m32(((boolean1 ? 1.0F : 2.0F) - float7) * float6);
		} else {
			this._m22((boolean1 ? float6 : float6 + float5) / (float6 - float5))._m32((boolean1 ? float6 : float6 + float6) * float5 / (float5 - float6));
		}

		return this._m23(1.0F)._m33(0.0F)._properties(0);
	}

	public Matrix4f setFrustumLH(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.setFrustumLH(float1, float2, float3, float4, float5, float6, false);
	}

	public Matrix4f setFromIntrinsic(float float1, float float2, float float3, float float4, float float5, int int1, int int2, float float6, float float7) {
		float float8 = 2.0F / (float)int1;
		float float9 = 2.0F / (float)int2;
		float float10 = 2.0F / (float6 - float7);
		this.m00 = float8 * float1;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = float8 * float3;
		this.m11 = float9 * float2;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = float8 * float4 - 1.0F;
		this.m21 = float9 * float5 - 1.0F;
		this.m22 = float10 * -(float6 + float7) + (float7 + float6) / (float6 - float7);
		this.m23 = -1.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = float10 * -float6 * float7;
		this.m33 = 0.0F;
		this.properties = 1;
		return this;
	}

	public Matrix4f rotate(Quaternionfc quaternionfc, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.rotation(quaternionfc);
		} else if ((this.properties & 8) != 0) {
			return this.rotateTranslation(quaternionfc, matrix4f);
		} else {
			return (this.properties & 2) != 0 ? this.rotateAffine(quaternionfc, matrix4f) : this.rotateGeneric(quaternionfc, matrix4f);
		}
	}

	private Matrix4f rotateGeneric(Quaternionfc quaternionfc, Matrix4f matrix4f) {
		float float1 = quaternionfc.w() * quaternionfc.w();
		float float2 = quaternionfc.x() * quaternionfc.x();
		float float3 = quaternionfc.y() * quaternionfc.y();
		float float4 = quaternionfc.z() * quaternionfc.z();
		float float5 = quaternionfc.z() * quaternionfc.w();
		float float6 = float5 + float5;
		float float7 = quaternionfc.x() * quaternionfc.y();
		float float8 = float7 + float7;
		float float9 = quaternionfc.x() * quaternionfc.z();
		float float10 = float9 + float9;
		float float11 = quaternionfc.y() * quaternionfc.w();
		float float12 = float11 + float11;
		float float13 = quaternionfc.y() * quaternionfc.z();
		float float14 = float13 + float13;
		float float15 = quaternionfc.x() * quaternionfc.w();
		float float16 = float15 + float15;
		float float17 = float1 + float2 - float4 - float3;
		float float18 = float8 + float6;
		float float19 = float10 - float12;
		float float20 = -float6 + float8;
		float float21 = float3 - float4 + float1 - float2;
		float float22 = float14 + float16;
		float float23 = float12 + float10;
		float float24 = float14 - float16;
		float float25 = float4 - float3 - float2 + float1;
		float float26 = this.m00 * float17 + this.m10 * float18 + this.m20 * float19;
		float float27 = this.m01 * float17 + this.m11 * float18 + this.m21 * float19;
		float float28 = this.m02 * float17 + this.m12 * float18 + this.m22 * float19;
		float float29 = this.m03 * float17 + this.m13 * float18 + this.m23 * float19;
		float float30 = this.m00 * float20 + this.m10 * float21 + this.m20 * float22;
		float float31 = this.m01 * float20 + this.m11 * float21 + this.m21 * float22;
		float float32 = this.m02 * float20 + this.m12 * float21 + this.m22 * float22;
		float float33 = this.m03 * float20 + this.m13 * float21 + this.m23 * float22;
		return matrix4f._m20(this.m00 * float23 + this.m10 * float24 + this.m20 * float25)._m21(this.m01 * float23 + this.m11 * float24 + this.m21 * float25)._m22(this.m02 * float23 + this.m12 * float24 + this.m22 * float25)._m23(this.m03 * float23 + this.m13 * float24 + this.m23 * float25)._m00(float26)._m01(float27)._m02(float28)._m03(float29)._m10(float30)._m11(float31)._m12(float32)._m13(float33)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotate(Quaternionfc quaternionfc) {
		return this.rotate(quaternionfc, this);
	}

	public Matrix4f rotateAffine(Quaternionfc quaternionfc, Matrix4f matrix4f) {
		float float1 = quaternionfc.w() * quaternionfc.w();
		float float2 = quaternionfc.x() * quaternionfc.x();
		float float3 = quaternionfc.y() * quaternionfc.y();
		float float4 = quaternionfc.z() * quaternionfc.z();
		float float5 = quaternionfc.z() * quaternionfc.w();
		float float6 = float5 + float5;
		float float7 = quaternionfc.x() * quaternionfc.y();
		float float8 = float7 + float7;
		float float9 = quaternionfc.x() * quaternionfc.z();
		float float10 = float9 + float9;
		float float11 = quaternionfc.y() * quaternionfc.w();
		float float12 = float11 + float11;
		float float13 = quaternionfc.y() * quaternionfc.z();
		float float14 = float13 + float13;
		float float15 = quaternionfc.x() * quaternionfc.w();
		float float16 = float15 + float15;
		float float17 = float1 + float2 - float4 - float3;
		float float18 = float8 + float6;
		float float19 = float10 - float12;
		float float20 = -float6 + float8;
		float float21 = float3 - float4 + float1 - float2;
		float float22 = float14 + float16;
		float float23 = float12 + float10;
		float float24 = float14 - float16;
		float float25 = float4 - float3 - float2 + float1;
		float float26 = this.m00 * float17 + this.m10 * float18 + this.m20 * float19;
		float float27 = this.m01 * float17 + this.m11 * float18 + this.m21 * float19;
		float float28 = this.m02 * float17 + this.m12 * float18 + this.m22 * float19;
		float float29 = this.m00 * float20 + this.m10 * float21 + this.m20 * float22;
		float float30 = this.m01 * float20 + this.m11 * float21 + this.m21 * float22;
		float float31 = this.m02 * float20 + this.m12 * float21 + this.m22 * float22;
		return matrix4f._m20(this.m00 * float23 + this.m10 * float24 + this.m20 * float25)._m21(this.m01 * float23 + this.m11 * float24 + this.m21 * float25)._m22(this.m02 * float23 + this.m12 * float24 + this.m22 * float25)._m23(0.0F)._m00(float26)._m01(float27)._m02(float28)._m03(0.0F)._m10(float29)._m11(float30)._m12(float31)._m13(0.0F)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateAffine(Quaternionfc quaternionfc) {
		return this.rotateAffine(quaternionfc, this);
	}

	public Matrix4f rotateTranslation(Quaternionfc quaternionfc, Matrix4f matrix4f) {
		float float1 = quaternionfc.w() * quaternionfc.w();
		float float2 = quaternionfc.x() * quaternionfc.x();
		float float3 = quaternionfc.y() * quaternionfc.y();
		float float4 = quaternionfc.z() * quaternionfc.z();
		float float5 = quaternionfc.z() * quaternionfc.w();
		float float6 = float5 + float5;
		float float7 = quaternionfc.x() * quaternionfc.y();
		float float8 = float7 + float7;
		float float9 = quaternionfc.x() * quaternionfc.z();
		float float10 = float9 + float9;
		float float11 = quaternionfc.y() * quaternionfc.w();
		float float12 = float11 + float11;
		float float13 = quaternionfc.y() * quaternionfc.z();
		float float14 = float13 + float13;
		float float15 = quaternionfc.x() * quaternionfc.w();
		float float16 = float15 + float15;
		float float17 = float1 + float2 - float4 - float3;
		float float18 = float8 + float6;
		float float19 = float10 - float12;
		float float20 = -float6 + float8;
		float float21 = float3 - float4 + float1 - float2;
		float float22 = float14 + float16;
		float float23 = float12 + float10;
		float float24 = float14 - float16;
		float float25 = float4 - float3 - float2 + float1;
		return matrix4f._m20(float23)._m21(float24)._m22(float25)._m23(0.0F)._m00(float17)._m01(float18)._m02(float19)._m03(0.0F)._m10(float20)._m11(float21)._m12(float22)._m13(0.0F)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateAround(Quaternionfc quaternionfc, float float1, float float2, float float3) {
		return this.rotateAround(quaternionfc, float1, float2, float3, this);
	}

	public Matrix4f rotateAroundAffine(Quaternionfc quaternionfc, float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = quaternionfc.w() * quaternionfc.w();
		float float5 = quaternionfc.x() * quaternionfc.x();
		float float6 = quaternionfc.y() * quaternionfc.y();
		float float7 = quaternionfc.z() * quaternionfc.z();
		float float8 = quaternionfc.z() * quaternionfc.w();
		float float9 = float8 + float8;
		float float10 = quaternionfc.x() * quaternionfc.y();
		float float11 = float10 + float10;
		float float12 = quaternionfc.x() * quaternionfc.z();
		float float13 = float12 + float12;
		float float14 = quaternionfc.y() * quaternionfc.w();
		float float15 = float14 + float14;
		float float16 = quaternionfc.y() * quaternionfc.z();
		float float17 = float16 + float16;
		float float18 = quaternionfc.x() * quaternionfc.w();
		float float19 = float18 + float18;
		float float20 = float4 + float5 - float7 - float6;
		float float21 = float11 + float9;
		float float22 = float13 - float15;
		float float23 = -float9 + float11;
		float float24 = float6 - float7 + float4 - float5;
		float float25 = float17 + float19;
		float float26 = float15 + float13;
		float float27 = float17 - float19;
		float float28 = float7 - float6 - float5 + float4;
		float float29 = this.m00 * float1 + this.m10 * float2 + this.m20 * float3 + this.m30;
		float float30 = this.m01 * float1 + this.m11 * float2 + this.m21 * float3 + this.m31;
		float float31 = this.m02 * float1 + this.m12 * float2 + this.m22 * float3 + this.m32;
		float float32 = this.m00 * float20 + this.m10 * float21 + this.m20 * float22;
		float float33 = this.m01 * float20 + this.m11 * float21 + this.m21 * float22;
		float float34 = this.m02 * float20 + this.m12 * float21 + this.m22 * float22;
		float float35 = this.m00 * float23 + this.m10 * float24 + this.m20 * float25;
		float float36 = this.m01 * float23 + this.m11 * float24 + this.m21 * float25;
		float float37 = this.m02 * float23 + this.m12 * float24 + this.m22 * float25;
		matrix4f._m20(this.m00 * float26 + this.m10 * float27 + this.m20 * float28)._m21(this.m01 * float26 + this.m11 * float27 + this.m21 * float28)._m22(this.m02 * float26 + this.m12 * float27 + this.m22 * float28)._m23(0.0F)._m00(float32)._m01(float33)._m02(float34)._m03(0.0F)._m10(float35)._m11(float36)._m12(float37)._m13(0.0F)._m30(-float32 * float1 - float35 * float2 - this.m20 * float3 + float29)._m31(-float33 * float1 - float36 * float2 - this.m21 * float3 + float30)._m32(-float34 * float1 - float37 * float2 - this.m22 * float3 + float31)._m33(1.0F)._properties(this.properties & -14);
		return matrix4f;
	}

	public Matrix4f rotateAround(Quaternionfc quaternionfc, float float1, float float2, float float3, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return this.rotationAround(quaternionfc, float1, float2, float3);
		} else {
			return (this.properties & 2) != 0 ? this.rotateAroundAffine(quaternionfc, float1, float2, float3, matrix4f) : this.rotateAroundGeneric(quaternionfc, float1, float2, float3, matrix4f);
		}
	}

	private Matrix4f rotateAroundGeneric(Quaternionfc quaternionfc, float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = quaternionfc.w() * quaternionfc.w();
		float float5 = quaternionfc.x() * quaternionfc.x();
		float float6 = quaternionfc.y() * quaternionfc.y();
		float float7 = quaternionfc.z() * quaternionfc.z();
		float float8 = quaternionfc.z() * quaternionfc.w();
		float float9 = float8 + float8;
		float float10 = quaternionfc.x() * quaternionfc.y();
		float float11 = float10 + float10;
		float float12 = quaternionfc.x() * quaternionfc.z();
		float float13 = float12 + float12;
		float float14 = quaternionfc.y() * quaternionfc.w();
		float float15 = float14 + float14;
		float float16 = quaternionfc.y() * quaternionfc.z();
		float float17 = float16 + float16;
		float float18 = quaternionfc.x() * quaternionfc.w();
		float float19 = float18 + float18;
		float float20 = float4 + float5 - float7 - float6;
		float float21 = float11 + float9;
		float float22 = float13 - float15;
		float float23 = -float9 + float11;
		float float24 = float6 - float7 + float4 - float5;
		float float25 = float17 + float19;
		float float26 = float15 + float13;
		float float27 = float17 - float19;
		float float28 = float7 - float6 - float5 + float4;
		float float29 = this.m00 * float1 + this.m10 * float2 + this.m20 * float3 + this.m30;
		float float30 = this.m01 * float1 + this.m11 * float2 + this.m21 * float3 + this.m31;
		float float31 = this.m02 * float1 + this.m12 * float2 + this.m22 * float3 + this.m32;
		float float32 = this.m00 * float20 + this.m10 * float21 + this.m20 * float22;
		float float33 = this.m01 * float20 + this.m11 * float21 + this.m21 * float22;
		float float34 = this.m02 * float20 + this.m12 * float21 + this.m22 * float22;
		float float35 = this.m03 * float20 + this.m13 * float21 + this.m23 * float22;
		float float36 = this.m00 * float23 + this.m10 * float24 + this.m20 * float25;
		float float37 = this.m01 * float23 + this.m11 * float24 + this.m21 * float25;
		float float38 = this.m02 * float23 + this.m12 * float24 + this.m22 * float25;
		float float39 = this.m03 * float23 + this.m13 * float24 + this.m23 * float25;
		matrix4f._m20(this.m00 * float26 + this.m10 * float27 + this.m20 * float28)._m21(this.m01 * float26 + this.m11 * float27 + this.m21 * float28)._m22(this.m02 * float26 + this.m12 * float27 + this.m22 * float28)._m23(this.m03 * float26 + this.m13 * float27 + this.m23 * float28)._m00(float32)._m01(float33)._m02(float34)._m03(float35)._m10(float36)._m11(float37)._m12(float38)._m13(float39)._m30(-float32 * float1 - float36 * float2 - this.m20 * float3 + float29)._m31(-float33 * float1 - float37 * float2 - this.m21 * float3 + float30)._m32(-float34 * float1 - float38 * float2 - this.m22 * float3 + float31)._m33(this.m33)._properties(this.properties & -14);
		return matrix4f;
	}

	public Matrix4f rotationAround(Quaternionfc quaternionfc, float float1, float float2, float float3) {
		float float4 = quaternionfc.w() * quaternionfc.w();
		float float5 = quaternionfc.x() * quaternionfc.x();
		float float6 = quaternionfc.y() * quaternionfc.y();
		float float7 = quaternionfc.z() * quaternionfc.z();
		float float8 = quaternionfc.z() * quaternionfc.w();
		float float9 = float8 + float8;
		float float10 = quaternionfc.x() * quaternionfc.y();
		float float11 = float10 + float10;
		float float12 = quaternionfc.x() * quaternionfc.z();
		float float13 = float12 + float12;
		float float14 = quaternionfc.y() * quaternionfc.w();
		float float15 = float14 + float14;
		float float16 = quaternionfc.y() * quaternionfc.z();
		float float17 = float16 + float16;
		float float18 = quaternionfc.x() * quaternionfc.w();
		float float19 = float18 + float18;
		this._m20(float15 + float13)._m21(float17 - float19)._m22(float7 - float6 - float5 + float4)._m23(0.0F)._m00(float4 + float5 - float7 - float6)._m01(float11 + float9)._m02(float13 - float15)._m03(0.0F)._m10(-float9 + float11)._m11(float6 - float7 + float4 - float5)._m12(float17 + float19)._m13(0.0F)._m30(-this.m00 * float1 - this.m10 * float2 - this.m20 * float3 + float1)._m31(-this.m01 * float1 - this.m11 * float2 - this.m21 * float3 + float2)._m32(-this.m02 * float1 - this.m12 * float2 - this.m22 * float3 + float3)._m33(1.0F)._properties(18);
		return this;
	}

	public Matrix4f rotateLocal(Quaternionfc quaternionfc, Matrix4f matrix4f) {
		float float1 = quaternionfc.w() * quaternionfc.w();
		float float2 = quaternionfc.x() * quaternionfc.x();
		float float3 = quaternionfc.y() * quaternionfc.y();
		float float4 = quaternionfc.z() * quaternionfc.z();
		float float5 = quaternionfc.z() * quaternionfc.w();
		float float6 = float5 + float5;
		float float7 = quaternionfc.x() * quaternionfc.y();
		float float8 = float7 + float7;
		float float9 = quaternionfc.x() * quaternionfc.z();
		float float10 = float9 + float9;
		float float11 = quaternionfc.y() * quaternionfc.w();
		float float12 = float11 + float11;
		float float13 = quaternionfc.y() * quaternionfc.z();
		float float14 = float13 + float13;
		float float15 = quaternionfc.x() * quaternionfc.w();
		float float16 = float15 + float15;
		float float17 = float1 + float2 - float4 - float3;
		float float18 = float8 + float6;
		float float19 = float10 - float12;
		float float20 = -float6 + float8;
		float float21 = float3 - float4 + float1 - float2;
		float float22 = float14 + float16;
		float float23 = float12 + float10;
		float float24 = float14 - float16;
		float float25 = float4 - float3 - float2 + float1;
		float float26 = float17 * this.m00 + float20 * this.m01 + float23 * this.m02;
		float float27 = float18 * this.m00 + float21 * this.m01 + float24 * this.m02;
		float float28 = float19 * this.m00 + float22 * this.m01 + float25 * this.m02;
		float float29 = float17 * this.m10 + float20 * this.m11 + float23 * this.m12;
		float float30 = float18 * this.m10 + float21 * this.m11 + float24 * this.m12;
		float float31 = float19 * this.m10 + float22 * this.m11 + float25 * this.m12;
		float float32 = float17 * this.m20 + float20 * this.m21 + float23 * this.m22;
		float float33 = float18 * this.m20 + float21 * this.m21 + float24 * this.m22;
		float float34 = float19 * this.m20 + float22 * this.m21 + float25 * this.m22;
		float float35 = float17 * this.m30 + float20 * this.m31 + float23 * this.m32;
		float float36 = float18 * this.m30 + float21 * this.m31 + float24 * this.m32;
		float float37 = float19 * this.m30 + float22 * this.m31 + float25 * this.m32;
		return matrix4f._m00(float26)._m01(float27)._m02(float28)._m03(this.m03)._m10(float29)._m11(float30)._m12(float31)._m13(this.m13)._m20(float32)._m21(float33)._m22(float34)._m23(this.m23)._m30(float35)._m31(float36)._m32(float37)._m33(this.m33)._properties(this.properties & -14);
	}

	public Matrix4f rotateLocal(Quaternionfc quaternionfc) {
		return this.rotateLocal(quaternionfc, this);
	}

	public Matrix4f rotateAroundLocal(Quaternionfc quaternionfc, float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = quaternionfc.w() * quaternionfc.w();
		float float5 = quaternionfc.x() * quaternionfc.x();
		float float6 = quaternionfc.y() * quaternionfc.y();
		float float7 = quaternionfc.z() * quaternionfc.z();
		float float8 = quaternionfc.z() * quaternionfc.w();
		float float9 = quaternionfc.x() * quaternionfc.y();
		float float10 = quaternionfc.x() * quaternionfc.z();
		float float11 = quaternionfc.y() * quaternionfc.w();
		float float12 = quaternionfc.y() * quaternionfc.z();
		float float13 = quaternionfc.x() * quaternionfc.w();
		float float14 = float4 + float5 - float7 - float6;
		float float15 = float9 + float8 + float8 + float9;
		float float16 = float10 - float11 + float10 - float11;
		float float17 = -float8 + float9 - float8 + float9;
		float float18 = float6 - float7 + float4 - float5;
		float float19 = float12 + float12 + float13 + float13;
		float float20 = float11 + float10 + float10 + float11;
		float float21 = float12 + float12 - float13 - float13;
		float float22 = float7 - float6 - float5 + float4;
		float float23 = this.m00 - float1 * this.m03;
		float float24 = this.m01 - float2 * this.m03;
		float float25 = this.m02 - float3 * this.m03;
		float float26 = this.m10 - float1 * this.m13;
		float float27 = this.m11 - float2 * this.m13;
		float float28 = this.m12 - float3 * this.m13;
		float float29 = this.m20 - float1 * this.m23;
		float float30 = this.m21 - float2 * this.m23;
		float float31 = this.m22 - float3 * this.m23;
		float float32 = this.m30 - float1 * this.m33;
		float float33 = this.m31 - float2 * this.m33;
		float float34 = this.m32 - float3 * this.m33;
		matrix4f._m00(float14 * float23 + float17 * float24 + float20 * float25 + float1 * this.m03)._m01(float15 * float23 + float18 * float24 + float21 * float25 + float2 * this.m03)._m02(float16 * float23 + float19 * float24 + float22 * float25 + float3 * this.m03)._m03(this.m03)._m10(float14 * float26 + float17 * float27 + float20 * float28 + float1 * this.m13)._m11(float15 * float26 + float18 * float27 + float21 * float28 + float2 * this.m13)._m12(float16 * float26 + float19 * float27 + float22 * float28 + float3 * this.m13)._m13(this.m13)._m20(float14 * float29 + float17 * float30 + float20 * float31 + float1 * this.m23)._m21(float15 * float29 + float18 * float30 + float21 * float31 + float2 * this.m23)._m22(float16 * float29 + float19 * float30 + float22 * float31 + float3 * this.m23)._m23(this.m23)._m30(float14 * float32 + float17 * float33 + float20 * float34 + float1 * this.m33)._m31(float15 * float32 + float18 * float33 + float21 * float34 + float2 * this.m33)._m32(float16 * float32 + float19 * float33 + float22 * float34 + float3 * this.m33)._m33(this.m33)._properties(this.properties & -14);
		return matrix4f;
	}

	public Matrix4f rotateAroundLocal(Quaternionfc quaternionfc, float float1, float float2, float float3) {
		return this.rotateAroundLocal(quaternionfc, float1, float2, float3, this);
	}

	public Matrix4f rotate(AxisAngle4f axisAngle4f) {
		return this.rotate(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z);
	}

	public Matrix4f rotate(AxisAngle4f axisAngle4f, Matrix4f matrix4f) {
		return this.rotate(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z, matrix4f);
	}

	public Matrix4f rotate(float float1, Vector3fc vector3fc) {
		return this.rotate(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4f rotate(float float1, Vector3fc vector3fc, Matrix4f matrix4f) {
		return this.rotate(float1, vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix4f);
	}

	public Vector4f unproject(float float1, float float2, float float3, int[] intArray, Vector4f vector4f) {
		float float4 = this.m00 * this.m11 - this.m01 * this.m10;
		float float5 = this.m00 * this.m12 - this.m02 * this.m10;
		float float6 = this.m00 * this.m13 - this.m03 * this.m10;
		float float7 = this.m01 * this.m12 - this.m02 * this.m11;
		float float8 = this.m01 * this.m13 - this.m03 * this.m11;
		float float9 = this.m02 * this.m13 - this.m03 * this.m12;
		float float10 = this.m20 * this.m31 - this.m21 * this.m30;
		float float11 = this.m20 * this.m32 - this.m22 * this.m30;
		float float12 = this.m20 * this.m33 - this.m23 * this.m30;
		float float13 = this.m21 * this.m32 - this.m22 * this.m31;
		float float14 = this.m21 * this.m33 - this.m23 * this.m31;
		float float15 = this.m22 * this.m33 - this.m23 * this.m32;
		float float16 = float4 * float15 - float5 * float14 + float6 * float13 + float7 * float12 - float8 * float11 + float9 * float10;
		float16 = 1.0F / float16;
		float float17 = (this.m11 * float15 - this.m12 * float14 + this.m13 * float13) * float16;
		float float18 = (-this.m01 * float15 + this.m02 * float14 - this.m03 * float13) * float16;
		float float19 = (this.m31 * float9 - this.m32 * float8 + this.m33 * float7) * float16;
		float float20 = (-this.m21 * float9 + this.m22 * float8 - this.m23 * float7) * float16;
		float float21 = (-this.m10 * float15 + this.m12 * float12 - this.m13 * float11) * float16;
		float float22 = (this.m00 * float15 - this.m02 * float12 + this.m03 * float11) * float16;
		float float23 = (-this.m30 * float9 + this.m32 * float6 - this.m33 * float5) * float16;
		float float24 = (this.m20 * float9 - this.m22 * float6 + this.m23 * float5) * float16;
		float float25 = (this.m10 * float14 - this.m11 * float12 + this.m13 * float10) * float16;
		float float26 = (-this.m00 * float14 + this.m01 * float12 - this.m03 * float10) * float16;
		float float27 = (this.m30 * float8 - this.m31 * float6 + this.m33 * float4) * float16;
		float float28 = (-this.m20 * float8 + this.m21 * float6 - this.m23 * float4) * float16;
		float float29 = (-this.m10 * float13 + this.m11 * float11 - this.m12 * float10) * float16;
		float float30 = (this.m00 * float13 - this.m01 * float11 + this.m02 * float10) * float16;
		float float31 = (-this.m30 * float7 + this.m31 * float5 - this.m32 * float4) * float16;
		float float32 = (this.m20 * float7 - this.m21 * float5 + this.m22 * float4) * float16;
		float float33 = (float1 - (float)intArray[0]) / (float)intArray[2] * 2.0F - 1.0F;
		float float34 = (float2 - (float)intArray[1]) / (float)intArray[3] * 2.0F - 1.0F;
		float float35 = float3 + float3 - 1.0F;
		float float36 = 1.0F / (float20 * float33 + float24 * float34 + float28 * float35 + float32);
		return vector4f.set((float17 * float33 + float21 * float34 + float25 * float35 + float29) * float36, (float18 * float33 + float22 * float34 + float26 * float35 + float30) * float36, (float19 * float33 + float23 * float34 + float27 * float35 + float31) * float36, 1.0F);
	}

	public Vector3f unproject(float float1, float float2, float float3, int[] intArray, Vector3f vector3f) {
		float float4 = this.m00 * this.m11 - this.m01 * this.m10;
		float float5 = this.m00 * this.m12 - this.m02 * this.m10;
		float float6 = this.m00 * this.m13 - this.m03 * this.m10;
		float float7 = this.m01 * this.m12 - this.m02 * this.m11;
		float float8 = this.m01 * this.m13 - this.m03 * this.m11;
		float float9 = this.m02 * this.m13 - this.m03 * this.m12;
		float float10 = this.m20 * this.m31 - this.m21 * this.m30;
		float float11 = this.m20 * this.m32 - this.m22 * this.m30;
		float float12 = this.m20 * this.m33 - this.m23 * this.m30;
		float float13 = this.m21 * this.m32 - this.m22 * this.m31;
		float float14 = this.m21 * this.m33 - this.m23 * this.m31;
		float float15 = this.m22 * this.m33 - this.m23 * this.m32;
		float float16 = float4 * float15 - float5 * float14 + float6 * float13 + float7 * float12 - float8 * float11 + float9 * float10;
		float16 = 1.0F / float16;
		float float17 = (this.m11 * float15 - this.m12 * float14 + this.m13 * float13) * float16;
		float float18 = (-this.m01 * float15 + this.m02 * float14 - this.m03 * float13) * float16;
		float float19 = (this.m31 * float9 - this.m32 * float8 + this.m33 * float7) * float16;
		float float20 = (-this.m21 * float9 + this.m22 * float8 - this.m23 * float7) * float16;
		float float21 = (-this.m10 * float15 + this.m12 * float12 - this.m13 * float11) * float16;
		float float22 = (this.m00 * float15 - this.m02 * float12 + this.m03 * float11) * float16;
		float float23 = (-this.m30 * float9 + this.m32 * float6 - this.m33 * float5) * float16;
		float float24 = (this.m20 * float9 - this.m22 * float6 + this.m23 * float5) * float16;
		float float25 = (this.m10 * float14 - this.m11 * float12 + this.m13 * float10) * float16;
		float float26 = (-this.m00 * float14 + this.m01 * float12 - this.m03 * float10) * float16;
		float float27 = (this.m30 * float8 - this.m31 * float6 + this.m33 * float4) * float16;
		float float28 = (-this.m20 * float8 + this.m21 * float6 - this.m23 * float4) * float16;
		float float29 = (-this.m10 * float13 + this.m11 * float11 - this.m12 * float10) * float16;
		float float30 = (this.m00 * float13 - this.m01 * float11 + this.m02 * float10) * float16;
		float float31 = (-this.m30 * float7 + this.m31 * float5 - this.m32 * float4) * float16;
		float float32 = (this.m20 * float7 - this.m21 * float5 + this.m22 * float4) * float16;
		float float33 = (float1 - (float)intArray[0]) / (float)intArray[2] * 2.0F - 1.0F;
		float float34 = (float2 - (float)intArray[1]) / (float)intArray[3] * 2.0F - 1.0F;
		float float35 = float3 + float3 - 1.0F;
		float float36 = 1.0F / (float20 * float33 + float24 * float34 + float28 * float35 + float32);
		return vector3f.set((float17 * float33 + float21 * float34 + float25 * float35 + float29) * float36, (float18 * float33 + float22 * float34 + float26 * float35 + float30) * float36, (float19 * float33 + float23 * float34 + float27 * float35 + float31) * float36);
	}

	public Vector4f unproject(Vector3fc vector3fc, int[] intArray, Vector4f vector4f) {
		return this.unproject(vector3fc.x(), vector3fc.y(), vector3fc.z(), intArray, vector4f);
	}

	public Vector3f unproject(Vector3fc vector3fc, int[] intArray, Vector3f vector3f) {
		return this.unproject(vector3fc.x(), vector3fc.y(), vector3fc.z(), intArray, vector3f);
	}

	public Matrix4f unprojectRay(float float1, float float2, int[] intArray, Vector3f vector3f, Vector3f vector3f2) {
		float float3 = this.m00 * this.m11 - this.m01 * this.m10;
		float float4 = this.m00 * this.m12 - this.m02 * this.m10;
		float float5 = this.m00 * this.m13 - this.m03 * this.m10;
		float float6 = this.m01 * this.m12 - this.m02 * this.m11;
		float float7 = this.m01 * this.m13 - this.m03 * this.m11;
		float float8 = this.m02 * this.m13 - this.m03 * this.m12;
		float float9 = this.m20 * this.m31 - this.m21 * this.m30;
		float float10 = this.m20 * this.m32 - this.m22 * this.m30;
		float float11 = this.m20 * this.m33 - this.m23 * this.m30;
		float float12 = this.m21 * this.m32 - this.m22 * this.m31;
		float float13 = this.m21 * this.m33 - this.m23 * this.m31;
		float float14 = this.m22 * this.m33 - this.m23 * this.m32;
		float float15 = float3 * float14 - float4 * float13 + float5 * float12 + float6 * float11 - float7 * float10 + float8 * float9;
		float15 = 1.0F / float15;
		float float16 = (this.m11 * float14 - this.m12 * float13 + this.m13 * float12) * float15;
		float float17 = (-this.m01 * float14 + this.m02 * float13 - this.m03 * float12) * float15;
		float float18 = (this.m31 * float8 - this.m32 * float7 + this.m33 * float6) * float15;
		float float19 = (-this.m21 * float8 + this.m22 * float7 - this.m23 * float6) * float15;
		float float20 = (-this.m10 * float14 + this.m12 * float11 - this.m13 * float10) * float15;
		float float21 = (this.m00 * float14 - this.m02 * float11 + this.m03 * float10) * float15;
		float float22 = (-this.m30 * float8 + this.m32 * float5 - this.m33 * float4) * float15;
		float float23 = (this.m20 * float8 - this.m22 * float5 + this.m23 * float4) * float15;
		float float24 = (this.m10 * float13 - this.m11 * float11 + this.m13 * float9) * float15;
		float float25 = (-this.m00 * float13 + this.m01 * float11 - this.m03 * float9) * float15;
		float float26 = (this.m30 * float7 - this.m31 * float5 + this.m33 * float3) * float15;
		float float27 = (-this.m20 * float7 + this.m21 * float5 - this.m23 * float3) * float15;
		float float28 = (-this.m10 * float12 + this.m11 * float10 - this.m12 * float9) * float15;
		float float29 = (this.m00 * float12 - this.m01 * float10 + this.m02 * float9) * float15;
		float float30 = (-this.m30 * float6 + this.m31 * float4 - this.m32 * float3) * float15;
		float float31 = (this.m20 * float6 - this.m21 * float4 + this.m22 * float3) * float15;
		float float32 = (float1 - (float)intArray[0]) / (float)intArray[2] * 2.0F - 1.0F;
		float float33 = (float2 - (float)intArray[1]) / (float)intArray[3] * 2.0F - 1.0F;
		float float34 = float16 * float32 + float20 * float33 + float28;
		float float35 = float17 * float32 + float21 * float33 + float29;
		float float36 = float18 * float32 + float22 * float33 + float30;
		float float37 = 1.0F / (float19 * float32 + float23 * float33 - float27 + float31);
		float float38 = (float34 - float24) * float37;
		float float39 = (float35 - float25) * float37;
		float float40 = (float36 - float26) * float37;
		float float41 = 1.0F / (float19 * float32 + float23 * float33 + float31);
		float float42 = float34 * float41;
		float float43 = float35 * float41;
		float float44 = float36 * float41;
		vector3f.x = float38;
		vector3f.y = float39;
		vector3f.z = float40;
		vector3f2.x = float42 - float38;
		vector3f2.y = float43 - float39;
		vector3f2.z = float44 - float40;
		return this;
	}

	public Matrix4f unprojectRay(Vector2fc vector2fc, int[] intArray, Vector3f vector3f, Vector3f vector3f2) {
		return this.unprojectRay(vector2fc.x(), vector2fc.y(), intArray, vector3f, vector3f2);
	}

	public Vector4f unprojectInv(Vector3fc vector3fc, int[] intArray, Vector4f vector4f) {
		return this.unprojectInv(vector3fc.x(), vector3fc.y(), vector3fc.z(), intArray, vector4f);
	}

	public Vector4f unprojectInv(float float1, float float2, float float3, int[] intArray, Vector4f vector4f) {
		float float4 = (float1 - (float)intArray[0]) / (float)intArray[2] * 2.0F - 1.0F;
		float float5 = (float2 - (float)intArray[1]) / (float)intArray[3] * 2.0F - 1.0F;
		float float6 = float3 + float3 - 1.0F;
		float float7 = 1.0F / (this.m03 * float4 + this.m13 * float5 + this.m23 * float6 + this.m33);
		return vector4f.set((this.m00 * float4 + this.m10 * float5 + this.m20 * float6 + this.m30) * float7, (this.m01 * float4 + this.m11 * float5 + this.m21 * float6 + this.m31) * float7, (this.m02 * float4 + this.m12 * float5 + this.m22 * float6 + this.m32) * float7, 1.0F);
	}

	public Matrix4f unprojectInvRay(Vector2fc vector2fc, int[] intArray, Vector3f vector3f, Vector3f vector3f2) {
		return this.unprojectInvRay(vector2fc.x(), vector2fc.y(), intArray, vector3f, vector3f2);
	}

	public Matrix4f unprojectInvRay(float float1, float float2, int[] intArray, Vector3f vector3f, Vector3f vector3f2) {
		float float3 = (float1 - (float)intArray[0]) / (float)intArray[2] * 2.0F - 1.0F;
		float float4 = (float2 - (float)intArray[1]) / (float)intArray[3] * 2.0F - 1.0F;
		float float5 = this.m00 * float3 + this.m10 * float4 + this.m30;
		float float6 = this.m01 * float3 + this.m11 * float4 + this.m31;
		float float7 = this.m02 * float3 + this.m12 * float4 + this.m32;
		float float8 = 1.0F / (this.m03 * float3 + this.m13 * float4 - this.m23 + this.m33);
		float float9 = (float5 - this.m20) * float8;
		float float10 = (float6 - this.m21) * float8;
		float float11 = (float7 - this.m22) * float8;
		float float12 = 1.0F / (this.m03 * float3 + this.m13 * float4 + this.m33);
		float float13 = float5 * float12;
		float float14 = float6 * float12;
		float float15 = float7 * float12;
		vector3f.x = float9;
		vector3f.y = float10;
		vector3f.z = float11;
		vector3f2.x = float13 - float9;
		vector3f2.y = float14 - float10;
		vector3f2.z = float15 - float11;
		return this;
	}

	public Vector3f unprojectInv(Vector3fc vector3fc, int[] intArray, Vector3f vector3f) {
		return this.unprojectInv(vector3fc.x(), vector3fc.y(), vector3fc.z(), intArray, vector3f);
	}

	public Vector3f unprojectInv(float float1, float float2, float float3, int[] intArray, Vector3f vector3f) {
		float float4 = (float1 - (float)intArray[0]) / (float)intArray[2] * 2.0F - 1.0F;
		float float5 = (float2 - (float)intArray[1]) / (float)intArray[3] * 2.0F - 1.0F;
		float float6 = float3 + float3 - 1.0F;
		float float7 = 1.0F / (this.m03 * float4 + this.m13 * float5 + this.m23 * float6 + this.m33);
		return vector3f.set((this.m00 * float4 + this.m10 * float5 + this.m20 * float6 + this.m30) * float7, (this.m01 * float4 + this.m11 * float5 + this.m21 * float6 + this.m31) * float7, (this.m02 * float4 + this.m12 * float5 + this.m22 * float6 + this.m32) * float7);
	}

	public Vector4f project(float float1, float float2, float float3, int[] intArray, Vector4f vector4f) {
		float float4 = 1.0F / (this.m03 * float1 + this.m13 * float2 + this.m23 * float3 + this.m33);
		float float5 = (this.m00 * float1 + this.m10 * float2 + this.m20 * float3 + this.m30) * float4;
		float float6 = (this.m01 * float1 + this.m11 * float2 + this.m21 * float3 + this.m31) * float4;
		float float7 = (this.m02 * float1 + this.m12 * float2 + this.m22 * float3 + this.m32) * float4;
		return vector4f.set((float5 * 0.5F + 0.5F) * (float)intArray[2] + (float)intArray[0], (float6 * 0.5F + 0.5F) * (float)intArray[3] + (float)intArray[1], (1.0F + float7) * 0.5F, 1.0F);
	}

	public Vector3f project(float float1, float float2, float float3, int[] intArray, Vector3f vector3f) {
		float float4 = 1.0F / (this.m03 * float1 + this.m13 * float2 + this.m23 * float3 + this.m33);
		float float5 = (this.m00 * float1 + this.m10 * float2 + this.m20 * float3 + this.m30) * float4;
		float float6 = (this.m01 * float1 + this.m11 * float2 + this.m21 * float3 + this.m31) * float4;
		float float7 = (this.m02 * float1 + this.m12 * float2 + this.m22 * float3 + this.m32) * float4;
		vector3f.x = (float5 * 0.5F + 0.5F) * (float)intArray[2] + (float)intArray[0];
		vector3f.y = (float6 * 0.5F + 0.5F) * (float)intArray[3] + (float)intArray[1];
		vector3f.z = (1.0F + float7) * 0.5F;
		return vector3f;
	}

	public Vector4f project(Vector3fc vector3fc, int[] intArray, Vector4f vector4f) {
		return this.project(vector3fc.x(), vector3fc.y(), vector3fc.z(), intArray, vector4f);
	}

	public Vector3f project(Vector3fc vector3fc, int[] intArray, Vector3f vector3f) {
		return this.project(vector3fc.x(), vector3fc.y(), vector3fc.z(), intArray, vector3f);
	}

	public Matrix4f reflect(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.reflection(float1, float2, float3, float4);
		} else {
			return (this.properties & 2) != 0 ? this.reflectAffine(float1, float2, float3, float4, matrix4f) : this.reflectGeneric(float1, float2, float3, float4, matrix4f);
		}
	}

	private Matrix4f reflectAffine(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		float float5 = float1 + float1;
		float float6 = float2 + float2;
		float float7 = float3 + float3;
		float float8 = float4 + float4;
		float float9 = 1.0F - float5 * float1;
		float float10 = -float5 * float2;
		float float11 = -float5 * float3;
		float float12 = -float6 * float1;
		float float13 = 1.0F - float6 * float2;
		float float14 = -float6 * float3;
		float float15 = -float7 * float1;
		float float16 = -float7 * float2;
		float float17 = 1.0F - float7 * float3;
		float float18 = -float8 * float1;
		float float19 = -float8 * float2;
		float float20 = -float8 * float3;
		matrix4f._m30(this.m00 * float18 + this.m10 * float19 + this.m20 * float20 + this.m30)._m31(this.m01 * float18 + this.m11 * float19 + this.m21 * float20 + this.m31)._m32(this.m02 * float18 + this.m12 * float19 + this.m22 * float20 + this.m32)._m33(this.m33);
		float float21 = this.m00 * float9 + this.m10 * float10 + this.m20 * float11;
		float float22 = this.m01 * float9 + this.m11 * float10 + this.m21 * float11;
		float float23 = this.m02 * float9 + this.m12 * float10 + this.m22 * float11;
		float float24 = this.m00 * float12 + this.m10 * float13 + this.m20 * float14;
		float float25 = this.m01 * float12 + this.m11 * float13 + this.m21 * float14;
		float float26 = this.m02 * float12 + this.m12 * float13 + this.m22 * float14;
		matrix4f._m20(this.m00 * float15 + this.m10 * float16 + this.m20 * float17)._m21(this.m01 * float15 + this.m11 * float16 + this.m21 * float17)._m22(this.m02 * float15 + this.m12 * float16 + this.m22 * float17)._m23(0.0F)._m00(float21)._m01(float22)._m02(float23)._m03(0.0F)._m10(float24)._m11(float25)._m12(float26)._m13(0.0F)._properties(this.properties & -14);
		return matrix4f;
	}

	private Matrix4f reflectGeneric(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		float float5 = float1 + float1;
		float float6 = float2 + float2;
		float float7 = float3 + float3;
		float float8 = float4 + float4;
		float float9 = 1.0F - float5 * float1;
		float float10 = -float5 * float2;
		float float11 = -float5 * float3;
		float float12 = -float6 * float1;
		float float13 = 1.0F - float6 * float2;
		float float14 = -float6 * float3;
		float float15 = -float7 * float1;
		float float16 = -float7 * float2;
		float float17 = 1.0F - float7 * float3;
		float float18 = -float8 * float1;
		float float19 = -float8 * float2;
		float float20 = -float8 * float3;
		matrix4f._m30(this.m00 * float18 + this.m10 * float19 + this.m20 * float20 + this.m30)._m31(this.m01 * float18 + this.m11 * float19 + this.m21 * float20 + this.m31)._m32(this.m02 * float18 + this.m12 * float19 + this.m22 * float20 + this.m32)._m33(this.m03 * float18 + this.m13 * float19 + this.m23 * float20 + this.m33);
		float float21 = this.m00 * float9 + this.m10 * float10 + this.m20 * float11;
		float float22 = this.m01 * float9 + this.m11 * float10 + this.m21 * float11;
		float float23 = this.m02 * float9 + this.m12 * float10 + this.m22 * float11;
		float float24 = this.m03 * float9 + this.m13 * float10 + this.m23 * float11;
		float float25 = this.m00 * float12 + this.m10 * float13 + this.m20 * float14;
		float float26 = this.m01 * float12 + this.m11 * float13 + this.m21 * float14;
		float float27 = this.m02 * float12 + this.m12 * float13 + this.m22 * float14;
		float float28 = this.m03 * float12 + this.m13 * float13 + this.m23 * float14;
		matrix4f._m20(this.m00 * float15 + this.m10 * float16 + this.m20 * float17)._m21(this.m01 * float15 + this.m11 * float16 + this.m21 * float17)._m22(this.m02 * float15 + this.m12 * float16 + this.m22 * float17)._m23(this.m03 * float15 + this.m13 * float16 + this.m23 * float17)._m00(float21)._m01(float22)._m02(float23)._m03(float24)._m10(float25)._m11(float26)._m12(float27)._m13(float28)._properties(this.properties & -14);
		return matrix4f;
	}

	public Matrix4f reflect(float float1, float float2, float float3, float float4) {
		return this.reflect(float1, float2, float3, float4, this);
	}

	public Matrix4f reflect(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.reflect(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4f reflect(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		float float7 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float float8 = float1 * float7;
		float float9 = float2 * float7;
		float float10 = float3 * float7;
		return this.reflect(float8, float9, float10, -float8 * float4 - float9 * float5 - float10 * float6, matrix4f);
	}

	public Matrix4f reflect(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.reflect(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix4f reflect(Quaternionfc quaternionfc, Vector3fc vector3fc) {
		return this.reflect(quaternionfc, vector3fc, this);
	}

	public Matrix4f reflect(Quaternionfc quaternionfc, Vector3fc vector3fc, Matrix4f matrix4f) {
		double double1 = (double)(quaternionfc.x() + quaternionfc.x());
		double double2 = (double)(quaternionfc.y() + quaternionfc.y());
		double double3 = (double)(quaternionfc.z() + quaternionfc.z());
		float float1 = (float)((double)quaternionfc.x() * double3 + (double)quaternionfc.w() * double2);
		float float2 = (float)((double)quaternionfc.y() * double3 - (double)quaternionfc.w() * double1);
		float float3 = (float)(1.0 - ((double)quaternionfc.x() * double1 + (double)quaternionfc.y() * double2));
		return this.reflect(float1, float2, float3, vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix4f);
	}

	public Matrix4f reflect(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4f matrix4f) {
		return this.reflect(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), matrix4f);
	}

	public Matrix4f reflection(float float1, float float2, float float3, float float4) {
		float float5 = float1 + float1;
		float float6 = float2 + float2;
		float float7 = float3 + float3;
		float float8 = float4 + float4;
		this._m00(1.0F - float5 * float1)._m01(-float5 * float2)._m02(-float5 * float3)._m03(0.0F)._m10(-float6 * float1)._m11(1.0F - float6 * float2)._m12(-float6 * float3)._m13(0.0F)._m20(-float7 * float1)._m21(-float7 * float2)._m22(1.0F - float7 * float3)._m23(0.0F)._m30(-float8 * float1)._m31(-float8 * float2)._m32(-float8 * float3)._m33(1.0F)._properties(18);
		return this;
	}

	public Matrix4f reflection(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float float8 = float1 * float7;
		float float9 = float2 * float7;
		float float10 = float3 * float7;
		return this.reflection(float8, float9, float10, -float8 * float4 - float9 * float5 - float10 * float6);
	}

	public Matrix4f reflection(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.reflection(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix4f reflection(Quaternionfc quaternionfc, Vector3fc vector3fc) {
		double double1 = (double)(quaternionfc.x() + quaternionfc.x());
		double double2 = (double)(quaternionfc.y() + quaternionfc.y());
		double double3 = (double)(quaternionfc.z() + quaternionfc.z());
		float float1 = (float)((double)quaternionfc.x() * double3 + (double)quaternionfc.w() * double2);
		float float2 = (float)((double)quaternionfc.y() * double3 - (double)quaternionfc.w() * double1);
		float float3 = (float)(1.0 - ((double)quaternionfc.x() * double1 + (double)quaternionfc.y() * double2));
		return this.reflection(float1, float2, float3, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Vector4f getRow(int int1, Vector4f vector4f) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			return vector4f.set(this.m00, this.m10, this.m20, this.m30);
		
		case 1: 
			return vector4f.set(this.m01, this.m11, this.m21, this.m31);
		
		case 2: 
			return vector4f.set(this.m02, this.m12, this.m22, this.m32);
		
		case 3: 
			return vector4f.set(this.m03, this.m13, this.m23, this.m33);
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
	}

	public Vector3f getRow(int int1, Vector3f vector3f) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			return vector3f.set(this.m00, this.m10, this.m20);
		
		case 1: 
			return vector3f.set(this.m01, this.m11, this.m21);
		
		case 2: 
			return vector3f.set(this.m02, this.m12, this.m22);
		
		case 3: 
			return vector3f.set(this.m03, this.m13, this.m23);
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
	}

	public Matrix4f setRow(int int1, Vector4fc vector4fc) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			return this._m00(vector4fc.x())._m10(vector4fc.y())._m20(vector4fc.z())._m30(vector4fc.w())._properties(0);
		
		case 1: 
			return this._m01(vector4fc.x())._m11(vector4fc.y())._m21(vector4fc.z())._m31(vector4fc.w())._properties(0);
		
		case 2: 
			return this._m02(vector4fc.x())._m12(vector4fc.y())._m22(vector4fc.z())._m32(vector4fc.w())._properties(0);
		
		case 3: 
			return this._m03(vector4fc.x())._m13(vector4fc.y())._m23(vector4fc.z())._m33(vector4fc.w())._properties(0);
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
	}

	public Vector4f getColumn(int int1, Vector4f vector4f) throws IndexOutOfBoundsException {
		return MemUtil.INSTANCE.getColumn(this, int1, vector4f);
	}

	public Vector3f getColumn(int int1, Vector3f vector3f) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			return vector3f.set(this.m00, this.m01, this.m02);
		
		case 1: 
			return vector3f.set(this.m10, this.m11, this.m12);
		
		case 2: 
			return vector3f.set(this.m20, this.m21, this.m22);
		
		case 3: 
			return vector3f.set(this.m30, this.m31, this.m32);
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
	}

	public Matrix4f setColumn(int int1, Vector4fc vector4fc) throws IndexOutOfBoundsException {
		return vector4fc instanceof Vector4f ? MemUtil.INSTANCE.setColumn((Vector4f)vector4fc, int1, this)._properties(0) : MemUtil.INSTANCE.setColumn(vector4fc, int1, this)._properties(0);
	}

	public float get(int int1, int int2) {
		return MemUtil.INSTANCE.get(this, int1, int2);
	}

	public Matrix4f set(int int1, int int2, float float1) {
		return MemUtil.INSTANCE.set(this, int1, int2, float1);
	}

	public float getRowColumn(int int1, int int2) {
		return MemUtil.INSTANCE.get(this, int2, int1);
	}

	public Matrix4f setRowColumn(int int1, int int2, float float1) {
		return MemUtil.INSTANCE.set(this, int2, int1, float1);
	}

	public Matrix4f normal() {
		return this.normal(this);
	}

	public Matrix4f normal(Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.identity();
		} else {
			return (this.properties & 16) != 0 ? this.normalOrthonormal(matrix4f) : this.normalGeneric(matrix4f);
		}
	}

	private Matrix4f normalOrthonormal(Matrix4f matrix4f) {
		if (matrix4f != this) {
			matrix4f.set((Matrix4fc)this);
		}

		return matrix4f._properties(18);
	}

	private Matrix4f normalGeneric(Matrix4f matrix4f) {
		float float1 = this.m00 * this.m11;
		float float2 = this.m01 * this.m10;
		float float3 = this.m02 * this.m10;
		float float4 = this.m00 * this.m12;
		float float5 = this.m01 * this.m12;
		float float6 = this.m02 * this.m11;
		float float7 = (float1 - float2) * this.m22 + (float3 - float4) * this.m21 + (float5 - float6) * this.m20;
		float float8 = 1.0F / float7;
		float float9 = (this.m11 * this.m22 - this.m21 * this.m12) * float8;
		float float10 = (this.m20 * this.m12 - this.m10 * this.m22) * float8;
		float float11 = (this.m10 * this.m21 - this.m20 * this.m11) * float8;
		float float12 = (this.m21 * this.m02 - this.m01 * this.m22) * float8;
		float float13 = (this.m00 * this.m22 - this.m20 * this.m02) * float8;
		float float14 = (this.m20 * this.m01 - this.m00 * this.m21) * float8;
		float float15 = (float5 - float6) * float8;
		float float16 = (float3 - float4) * float8;
		float float17 = (float1 - float2) * float8;
		return matrix4f._m00(float9)._m01(float10)._m02(float11)._m03(0.0F)._m10(float12)._m11(float13)._m12(float14)._m13(0.0F)._m20(float15)._m21(float16)._m22(float17)._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties((this.properties | 2) & -10);
	}

	public Matrix3f normal(Matrix3f matrix3f) {
		return (this.properties & 16) != 0 ? this.normalOrthonormal(matrix3f) : this.normalGeneric(matrix3f);
	}

	private Matrix3f normalOrthonormal(Matrix3f matrix3f) {
		matrix3f.set((Matrix4fc)this);
		return matrix3f;
	}

	private Matrix3f normalGeneric(Matrix3f matrix3f) {
		float float1 = (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
		float float2 = 1.0F / float1;
		return matrix3f._m00((this.m11 * this.m22 - this.m21 * this.m12) * float2)._m01((this.m20 * this.m12 - this.m10 * this.m22) * float2)._m02((this.m10 * this.m21 - this.m20 * this.m11) * float2)._m10((this.m21 * this.m02 - this.m01 * this.m22) * float2)._m11((this.m00 * this.m22 - this.m20 * this.m02) * float2)._m12((this.m20 * this.m01 - this.m00 * this.m21) * float2)._m20((this.m01 * this.m12 - this.m02 * this.m11) * float2)._m21((this.m02 * this.m10 - this.m00 * this.m12) * float2)._m22((this.m00 * this.m11 - this.m01 * this.m10) * float2);
	}

	public Matrix4f cofactor3x3() {
		return this.cofactor3x3(this);
	}

	public Matrix3f cofactor3x3(Matrix3f matrix3f) {
		return matrix3f._m00(this.m11 * this.m22 - this.m21 * this.m12)._m01(this.m20 * this.m12 - this.m10 * this.m22)._m02(this.m10 * this.m21 - this.m20 * this.m11)._m10(this.m21 * this.m02 - this.m01 * this.m22)._m11(this.m00 * this.m22 - this.m20 * this.m02)._m12(this.m20 * this.m01 - this.m00 * this.m21)._m20(this.m01 * this.m12 - this.m02 * this.m11)._m21(this.m02 * this.m10 - this.m00 * this.m12)._m22(this.m00 * this.m11 - this.m01 * this.m10);
	}

	public Matrix4f cofactor3x3(Matrix4f matrix4f) {
		float float1 = this.m21 * this.m02 - this.m01 * this.m22;
		float float2 = this.m00 * this.m22 - this.m20 * this.m02;
		float float3 = this.m20 * this.m01 - this.m00 * this.m21;
		float float4 = this.m01 * this.m12 - this.m11 * this.m02;
		float float5 = this.m02 * this.m10 - this.m12 * this.m00;
		float float6 = this.m00 * this.m11 - this.m10 * this.m01;
		return matrix4f._m00(this.m11 * this.m22 - this.m21 * this.m12)._m01(this.m20 * this.m12 - this.m10 * this.m22)._m02(this.m10 * this.m21 - this.m20 * this.m11)._m03(0.0F)._m10(float1)._m11(float2)._m12(float3)._m13(0.0F)._m20(float4)._m21(float5)._m22(float6)._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties((this.properties | 2) & -10);
	}

	public Matrix4f normalize3x3() {
		return this.normalize3x3(this);
	}

	public Matrix4f normalize3x3(Matrix4f matrix4f) {
		float float1 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
		float float2 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
		float float3 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
		return matrix4f._m00(this.m00 * float1)._m01(this.m01 * float1)._m02(this.m02 * float1)._m10(this.m10 * float2)._m11(this.m11 * float2)._m12(this.m12 * float2)._m20(this.m20 * float3)._m21(this.m21 * float3)._m22(this.m22 * float3)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties);
	}

	public Matrix3f normalize3x3(Matrix3f matrix3f) {
		float float1 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
		float float2 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
		float float3 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
		return matrix3f._m00(this.m00 * float1)._m01(this.m01 * float1)._m02(this.m02 * float1)._m10(this.m10 * float2)._m11(this.m11 * float2)._m12(this.m12 * float2)._m20(this.m20 * float3)._m21(this.m21 * float3)._m22(this.m22 * float3);
	}

	public Vector4f frustumPlane(int int1, Vector4f vector4f) {
		switch (int1) {
		case 0: 
			vector4f.set(this.m03 + this.m00, this.m13 + this.m10, this.m23 + this.m20, this.m33 + this.m30).normalize3();
			break;
		
		case 1: 
			vector4f.set(this.m03 - this.m00, this.m13 - this.m10, this.m23 - this.m20, this.m33 - this.m30).normalize3();
			break;
		
		case 2: 
			vector4f.set(this.m03 + this.m01, this.m13 + this.m11, this.m23 + this.m21, this.m33 + this.m31).normalize3();
			break;
		
		case 3: 
			vector4f.set(this.m03 - this.m01, this.m13 - this.m11, this.m23 - this.m21, this.m33 - this.m31).normalize3();
			break;
		
		case 4: 
			vector4f.set(this.m03 + this.m02, this.m13 + this.m12, this.m23 + this.m22, this.m33 + this.m32).normalize3();
			break;
		
		case 5: 
			vector4f.set(this.m03 - this.m02, this.m13 - this.m12, this.m23 - this.m22, this.m33 - this.m32).normalize3();
			break;
		
		default: 
			throw new IllegalArgumentException("dest");
		
		}
		return vector4f;
	}

	public Vector3f frustumCorner(int int1, Vector3f vector3f) {
		float float1;
		float float2;
		float float3;
		float float4;
		float float5;
		float float6;
		float float7;
		float float8;
		float float9;
		float float10;
		float float11;
		float float12;
		switch (int1) {
		case 0: 
			float4 = this.m03 + this.m00;
			float5 = this.m13 + this.m10;
			float6 = this.m23 + this.m20;
			float1 = this.m33 + this.m30;
			float7 = this.m03 + this.m01;
			float8 = this.m13 + this.m11;
			float9 = this.m23 + this.m21;
			float2 = this.m33 + this.m31;
			float10 = this.m03 + this.m02;
			float11 = this.m13 + this.m12;
			float12 = this.m23 + this.m22;
			float3 = this.m33 + this.m32;
			break;
		
		case 1: 
			float4 = this.m03 - this.m00;
			float5 = this.m13 - this.m10;
			float6 = this.m23 - this.m20;
			float1 = this.m33 - this.m30;
			float7 = this.m03 + this.m01;
			float8 = this.m13 + this.m11;
			float9 = this.m23 + this.m21;
			float2 = this.m33 + this.m31;
			float10 = this.m03 + this.m02;
			float11 = this.m13 + this.m12;
			float12 = this.m23 + this.m22;
			float3 = this.m33 + this.m32;
			break;
		
		case 2: 
			float4 = this.m03 - this.m00;
			float5 = this.m13 - this.m10;
			float6 = this.m23 - this.m20;
			float1 = this.m33 - this.m30;
			float7 = this.m03 - this.m01;
			float8 = this.m13 - this.m11;
			float9 = this.m23 - this.m21;
			float2 = this.m33 - this.m31;
			float10 = this.m03 + this.m02;
			float11 = this.m13 + this.m12;
			float12 = this.m23 + this.m22;
			float3 = this.m33 + this.m32;
			break;
		
		case 3: 
			float4 = this.m03 + this.m00;
			float5 = this.m13 + this.m10;
			float6 = this.m23 + this.m20;
			float1 = this.m33 + this.m30;
			float7 = this.m03 - this.m01;
			float8 = this.m13 - this.m11;
			float9 = this.m23 - this.m21;
			float2 = this.m33 - this.m31;
			float10 = this.m03 + this.m02;
			float11 = this.m13 + this.m12;
			float12 = this.m23 + this.m22;
			float3 = this.m33 + this.m32;
			break;
		
		case 4: 
			float4 = this.m03 - this.m00;
			float5 = this.m13 - this.m10;
			float6 = this.m23 - this.m20;
			float1 = this.m33 - this.m30;
			float7 = this.m03 + this.m01;
			float8 = this.m13 + this.m11;
			float9 = this.m23 + this.m21;
			float2 = this.m33 + this.m31;
			float10 = this.m03 - this.m02;
			float11 = this.m13 - this.m12;
			float12 = this.m23 - this.m22;
			float3 = this.m33 - this.m32;
			break;
		
		case 5: 
			float4 = this.m03 + this.m00;
			float5 = this.m13 + this.m10;
			float6 = this.m23 + this.m20;
			float1 = this.m33 + this.m30;
			float7 = this.m03 + this.m01;
			float8 = this.m13 + this.m11;
			float9 = this.m23 + this.m21;
			float2 = this.m33 + this.m31;
			float10 = this.m03 - this.m02;
			float11 = this.m13 - this.m12;
			float12 = this.m23 - this.m22;
			float3 = this.m33 - this.m32;
			break;
		
		case 6: 
			float4 = this.m03 + this.m00;
			float5 = this.m13 + this.m10;
			float6 = this.m23 + this.m20;
			float1 = this.m33 + this.m30;
			float7 = this.m03 - this.m01;
			float8 = this.m13 - this.m11;
			float9 = this.m23 - this.m21;
			float2 = this.m33 - this.m31;
			float10 = this.m03 - this.m02;
			float11 = this.m13 - this.m12;
			float12 = this.m23 - this.m22;
			float3 = this.m33 - this.m32;
			break;
		
		case 7: 
			float4 = this.m03 - this.m00;
			float5 = this.m13 - this.m10;
			float6 = this.m23 - this.m20;
			float1 = this.m33 - this.m30;
			float7 = this.m03 - this.m01;
			float8 = this.m13 - this.m11;
			float9 = this.m23 - this.m21;
			float2 = this.m33 - this.m31;
			float10 = this.m03 - this.m02;
			float11 = this.m13 - this.m12;
			float12 = this.m23 - this.m22;
			float3 = this.m33 - this.m32;
			break;
		
		default: 
			throw new IllegalArgumentException("corner");
		
		}
		float float13 = float8 * float12 - float9 * float11;
		float float14 = float9 * float10 - float7 * float12;
		float float15 = float7 * float11 - float8 * float10;
		float float16 = float11 * float6 - float12 * float5;
		float float17 = float12 * float4 - float10 * float6;
		float float18 = float10 * float5 - float11 * float4;
		float float19 = float5 * float9 - float6 * float8;
		float float20 = float6 * float7 - float4 * float9;
		float float21 = float4 * float8 - float5 * float7;
		float float22 = 1.0F / (float4 * float13 + float5 * float14 + float6 * float15);
		vector3f.x = (-float13 * float1 - float16 * float2 - float19 * float3) * float22;
		vector3f.y = (-float14 * float1 - float17 * float2 - float20 * float3) * float22;
		vector3f.z = (-float15 * float1 - float18 * float2 - float21 * float3) * float22;
		return vector3f;
	}

	public Vector3f perspectiveOrigin(Vector3f vector3f) {
		float float1 = this.m03 + this.m00;
		float float2 = this.m13 + this.m10;
		float float3 = this.m23 + this.m20;
		float float4 = this.m33 + this.m30;
		float float5 = this.m03 - this.m00;
		float float6 = this.m13 - this.m10;
		float float7 = this.m23 - this.m20;
		float float8 = this.m33 - this.m30;
		float float9 = this.m03 - this.m01;
		float float10 = this.m13 - this.m11;
		float float11 = this.m23 - this.m21;
		float float12 = this.m33 - this.m31;
		float float13 = float6 * float11 - float7 * float10;
		float float14 = float7 * float9 - float5 * float11;
		float float15 = float5 * float10 - float6 * float9;
		float float16 = float10 * float3 - float11 * float2;
		float float17 = float11 * float1 - float9 * float3;
		float float18 = float9 * float2 - float10 * float1;
		float float19 = float2 * float7 - float3 * float6;
		float float20 = float3 * float5 - float1 * float7;
		float float21 = float1 * float6 - float2 * float5;
		float float22 = 1.0F / (float1 * float13 + float2 * float14 + float3 * float15);
		vector3f.x = (-float13 * float4 - float16 * float8 - float19 * float12) * float22;
		vector3f.y = (-float14 * float4 - float17 * float8 - float20 * float12) * float22;
		vector3f.z = (-float15 * float4 - float18 * float8 - float21 * float12) * float22;
		return vector3f;
	}

	public Vector3f perspectiveInvOrigin(Vector3f vector3f) {
		float float1 = 1.0F / this.m23;
		vector3f.x = this.m20 * float1;
		vector3f.y = this.m21 * float1;
		vector3f.z = this.m22 * float1;
		return vector3f;
	}

	public float perspectiveFov() {
		float float1 = this.m03 + this.m01;
		float float2 = this.m13 + this.m11;
		float float3 = this.m23 + this.m21;
		float float4 = this.m01 - this.m03;
		float float5 = this.m11 - this.m13;
		float float6 = this.m21 - this.m23;
		float float7 = Math.sqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float float8 = Math.sqrt(float4 * float4 + float5 * float5 + float6 * float6);
		return Math.acos((float1 * float4 + float2 * float5 + float3 * float6) / (float7 * float8));
	}

	public float perspectiveNear() {
		return this.m32 / (this.m23 + this.m22);
	}

	public float perspectiveFar() {
		return this.m32 / (this.m22 - this.m23);
	}

	public Vector3f frustumRayDir(float float1, float float2, Vector3f vector3f) {
		float float3 = this.m10 * this.m23;
		float float4 = this.m13 * this.m21;
		float float5 = this.m10 * this.m21;
		float float6 = this.m11 * this.m23;
		float float7 = this.m13 * this.m20;
		float float8 = this.m11 * this.m20;
		float float9 = this.m03 * this.m20;
		float float10 = this.m01 * this.m23;
		float float11 = this.m01 * this.m20;
		float float12 = this.m03 * this.m21;
		float float13 = this.m00 * this.m23;
		float float14 = this.m00 * this.m21;
		float float15 = this.m00 * this.m13;
		float float16 = this.m03 * this.m11;
		float float17 = this.m00 * this.m11;
		float float18 = this.m01 * this.m13;
		float float19 = this.m03 * this.m10;
		float float20 = this.m01 * this.m10;
		float float21 = (float6 + float7 + float8 - float3 - float4 - float5) * (1.0F - float2) + (float3 - float4 - float5 + float6 - float7 + float8) * float2;
		float float22 = (float12 + float13 + float14 - float9 - float10 - float11) * (1.0F - float2) + (float9 - float10 - float11 + float12 - float13 + float14) * float2;
		float float23 = (float18 + float19 + float20 - float15 - float16 - float17) * (1.0F - float2) + (float15 - float16 - float17 + float18 - float19 + float20) * float2;
		float float24 = (float4 - float5 - float6 + float7 + float8 - float3) * (1.0F - float2) + (float3 + float4 - float5 - float6 - float7 + float8) * float2;
		float float25 = (float10 - float11 - float12 + float13 + float14 - float9) * (1.0F - float2) + (float9 + float10 - float11 - float12 - float13 + float14) * float2;
		float float26 = (float16 - float17 - float18 + float19 + float20 - float15) * (1.0F - float2) + (float15 + float16 - float17 - float18 - float19 + float20) * float2;
		vector3f.x = float21 + (float24 - float21) * float1;
		vector3f.y = float22 + (float25 - float22) * float1;
		vector3f.z = float23 + (float26 - float23) * float1;
		return vector3f.normalize(vector3f);
	}

	public Vector3f positiveZ(Vector3f vector3f) {
		return (this.properties & 16) != 0 ? this.normalizedPositiveZ(vector3f) : this.positiveZGeneric(vector3f);
	}

	private Vector3f positiveZGeneric(Vector3f vector3f) {
		return vector3f.set(this.m10 * this.m21 - this.m11 * this.m20, this.m20 * this.m01 - this.m21 * this.m00, this.m00 * this.m11 - this.m01 * this.m10).normalize();
	}

	public Vector3f normalizedPositiveZ(Vector3f vector3f) {
		return vector3f.set(this.m02, this.m12, this.m22);
	}

	public Vector3f positiveX(Vector3f vector3f) {
		return (this.properties & 16) != 0 ? this.normalizedPositiveX(vector3f) : this.positiveXGeneric(vector3f);
	}

	private Vector3f positiveXGeneric(Vector3f vector3f) {
		return vector3f.set(this.m11 * this.m22 - this.m12 * this.m21, this.m02 * this.m21 - this.m01 * this.m22, this.m01 * this.m12 - this.m02 * this.m11).normalize();
	}

	public Vector3f normalizedPositiveX(Vector3f vector3f) {
		return vector3f.set(this.m00, this.m10, this.m20);
	}

	public Vector3f positiveY(Vector3f vector3f) {
		return (this.properties & 16) != 0 ? this.normalizedPositiveY(vector3f) : this.positiveYGeneric(vector3f);
	}

	private Vector3f positiveYGeneric(Vector3f vector3f) {
		return vector3f.set(this.m12 * this.m20 - this.m10 * this.m22, this.m00 * this.m22 - this.m02 * this.m20, this.m02 * this.m10 - this.m00 * this.m12).normalize();
	}

	public Vector3f normalizedPositiveY(Vector3f vector3f) {
		return vector3f.set(this.m01, this.m11, this.m21);
	}

	public Vector3f originAffine(Vector3f vector3f) {
		float float1 = this.m00 * this.m11 - this.m01 * this.m10;
		float float2 = this.m00 * this.m12 - this.m02 * this.m10;
		float float3 = this.m01 * this.m12 - this.m02 * this.m11;
		float float4 = this.m20 * this.m31 - this.m21 * this.m30;
		float float5 = this.m20 * this.m32 - this.m22 * this.m30;
		float float6 = this.m21 * this.m32 - this.m22 * this.m31;
		return vector3f.set(-this.m10 * float6 + this.m11 * float5 - this.m12 * float4, this.m00 * float6 - this.m01 * float5 + this.m02 * float4, -this.m30 * float3 + this.m31 * float2 - this.m32 * float1);
	}

	public Vector3f origin(Vector3f vector3f) {
		return (this.properties & 2) != 0 ? this.originAffine(vector3f) : this.originGeneric(vector3f);
	}

	private Vector3f originGeneric(Vector3f vector3f) {
		float float1 = this.m00 * this.m11 - this.m01 * this.m10;
		float float2 = this.m00 * this.m12 - this.m02 * this.m10;
		float float3 = this.m00 * this.m13 - this.m03 * this.m10;
		float float4 = this.m01 * this.m12 - this.m02 * this.m11;
		float float5 = this.m01 * this.m13 - this.m03 * this.m11;
		float float6 = this.m02 * this.m13 - this.m03 * this.m12;
		float float7 = this.m20 * this.m31 - this.m21 * this.m30;
		float float8 = this.m20 * this.m32 - this.m22 * this.m30;
		float float9 = this.m20 * this.m33 - this.m23 * this.m30;
		float float10 = this.m21 * this.m32 - this.m22 * this.m31;
		float float11 = this.m21 * this.m33 - this.m23 * this.m31;
		float float12 = this.m22 * this.m33 - this.m23 * this.m32;
		float float13 = float1 * float12 - float2 * float11 + float3 * float10 + float4 * float9 - float5 * float8 + float6 * float7;
		float float14 = 1.0F / float13;
		float float15 = (-this.m10 * float10 + this.m11 * float8 - this.m12 * float7) * float14;
		float float16 = (this.m00 * float10 - this.m01 * float8 + this.m02 * float7) * float14;
		float float17 = (-this.m30 * float4 + this.m31 * float2 - this.m32 * float1) * float14;
		float float18 = float13 / (this.m20 * float4 - this.m21 * float2 + this.m22 * float1);
		return vector3f.set(float15 * float18, float16 * float18, float17 * float18);
	}

	public Matrix4f shadow(Vector4f vector4f, float float1, float float2, float float3, float float4) {
		return this.shadow(vector4f.x, vector4f.y, vector4f.z, vector4f.w, float1, float2, float3, float4, this);
	}

	public Matrix4f shadow(Vector4f vector4f, float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return this.shadow(vector4f.x, vector4f.y, vector4f.z, vector4f.w, float1, float2, float3, float4, matrix4f);
	}

	public Matrix4f shadow(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		return this.shadow(float1, float2, float3, float4, float5, float6, float7, float8, this);
	}

	public Matrix4f shadow(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Matrix4f matrix4f) {
		float float9 = Math.invsqrt(float5 * float5 + float6 * float6 + float7 * float7);
		float float10 = float5 * float9;
		float float11 = float6 * float9;
		float float12 = float7 * float9;
		float float13 = float8 * float9;
		float float14 = float10 * float1 + float11 * float2 + float12 * float3 + float13 * float4;
		float float15 = float14 - float10 * float1;
		float float16 = -float10 * float2;
		float float17 = -float10 * float3;
		float float18 = -float10 * float4;
		float float19 = -float11 * float1;
		float float20 = float14 - float11 * float2;
		float float21 = -float11 * float3;
		float float22 = -float11 * float4;
		float float23 = -float12 * float1;
		float float24 = -float12 * float2;
		float float25 = float14 - float12 * float3;
		float float26 = -float12 * float4;
		float float27 = -float13 * float1;
		float float28 = -float13 * float2;
		float float29 = -float13 * float3;
		float float30 = float14 - float13 * float4;
		float float31 = this.m00 * float15 + this.m10 * float16 + this.m20 * float17 + this.m30 * float18;
		float float32 = this.m01 * float15 + this.m11 * float16 + this.m21 * float17 + this.m31 * float18;
		float float33 = this.m02 * float15 + this.m12 * float16 + this.m22 * float17 + this.m32 * float18;
		float float34 = this.m03 * float15 + this.m13 * float16 + this.m23 * float17 + this.m33 * float18;
		float float35 = this.m00 * float19 + this.m10 * float20 + this.m20 * float21 + this.m30 * float22;
		float float36 = this.m01 * float19 + this.m11 * float20 + this.m21 * float21 + this.m31 * float22;
		float float37 = this.m02 * float19 + this.m12 * float20 + this.m22 * float21 + this.m32 * float22;
		float float38 = this.m03 * float19 + this.m13 * float20 + this.m23 * float21 + this.m33 * float22;
		float float39 = this.m00 * float23 + this.m10 * float24 + this.m20 * float25 + this.m30 * float26;
		float float40 = this.m01 * float23 + this.m11 * float24 + this.m21 * float25 + this.m31 * float26;
		float float41 = this.m02 * float23 + this.m12 * float24 + this.m22 * float25 + this.m32 * float26;
		float float42 = this.m03 * float23 + this.m13 * float24 + this.m23 * float25 + this.m33 * float26;
		matrix4f._m30(this.m00 * float27 + this.m10 * float28 + this.m20 * float29 + this.m30 * float30)._m31(this.m01 * float27 + this.m11 * float28 + this.m21 * float29 + this.m31 * float30)._m32(this.m02 * float27 + this.m12 * float28 + this.m22 * float29 + this.m32 * float30)._m33(this.m03 * float27 + this.m13 * float28 + this.m23 * float29 + this.m33 * float30)._m00(float31)._m01(float32)._m02(float33)._m03(float34)._m10(float35)._m11(float36)._m12(float37)._m13(float38)._m20(float39)._m21(float40)._m22(float41)._m23(float42)._properties(this.properties & -30);
		return matrix4f;
	}

	public Matrix4f shadow(Vector4f vector4f, Matrix4fc matrix4fc, Matrix4f matrix4f) {
		float float1 = matrix4fc.m10();
		float float2 = matrix4fc.m11();
		float float3 = matrix4fc.m12();
		float float4 = -float1 * matrix4fc.m30() - float2 * matrix4fc.m31() - float3 * matrix4fc.m32();
		return this.shadow(vector4f.x, vector4f.y, vector4f.z, vector4f.w, float1, float2, float3, float4, matrix4f);
	}

	public Matrix4f shadow(Vector4f vector4f, Matrix4f matrix4f) {
		return this.shadow(vector4f, matrix4f, this);
	}

	public Matrix4f shadow(float float1, float float2, float float3, float float4, Matrix4fc matrix4fc, Matrix4f matrix4f) {
		float float5 = matrix4fc.m10();
		float float6 = matrix4fc.m11();
		float float7 = matrix4fc.m12();
		float float8 = -float5 * matrix4fc.m30() - float6 * matrix4fc.m31() - float7 * matrix4fc.m32();
		return this.shadow(float1, float2, float3, float4, float5, float6, float7, float8, matrix4f);
	}

	public Matrix4f shadow(float float1, float float2, float float3, float float4, Matrix4f matrix4f) {
		return this.shadow(float1, float2, float3, float4, matrix4f, this);
	}

	public Matrix4f billboardCylindrical(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		float float1 = vector3fc2.x() - vector3fc.x();
		float float2 = vector3fc2.y() - vector3fc.y();
		float float3 = vector3fc2.z() - vector3fc.z();
		float float4 = vector3fc3.y() * float3 - vector3fc3.z() * float2;
		float float5 = vector3fc3.z() * float1 - vector3fc3.x() * float3;
		float float6 = vector3fc3.x() * float2 - vector3fc3.y() * float1;
		float float7 = Math.invsqrt(float4 * float4 + float5 * float5 + float6 * float6);
		float4 *= float7;
		float5 *= float7;
		float6 *= float7;
		float1 = float5 * vector3fc3.z() - float6 * vector3fc3.y();
		float2 = float6 * vector3fc3.x() - float4 * vector3fc3.z();
		float3 = float4 * vector3fc3.y() - float5 * vector3fc3.x();
		float float8 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float1 *= float8;
		float2 *= float8;
		float3 *= float8;
		this._m00(float4)._m01(float5)._m02(float6)._m03(0.0F)._m10(vector3fc3.x())._m11(vector3fc3.y())._m12(vector3fc3.z())._m13(0.0F)._m20(float1)._m21(float2)._m22(float3)._m23(0.0F)._m30(vector3fc.x())._m31(vector3fc.y())._m32(vector3fc.z())._m33(1.0F)._properties(18);
		return this;
	}

	public Matrix4f billboardSpherical(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		float float1 = vector3fc2.x() - vector3fc.x();
		float float2 = vector3fc2.y() - vector3fc.y();
		float float3 = vector3fc2.z() - vector3fc.z();
		float float4 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float1 *= float4;
		float2 *= float4;
		float3 *= float4;
		float float5 = vector3fc3.y() * float3 - vector3fc3.z() * float2;
		float float6 = vector3fc3.z() * float1 - vector3fc3.x() * float3;
		float float7 = vector3fc3.x() * float2 - vector3fc3.y() * float1;
		float float8 = Math.invsqrt(float5 * float5 + float6 * float6 + float7 * float7);
		float5 *= float8;
		float6 *= float8;
		float7 *= float8;
		float float9 = float2 * float7 - float3 * float6;
		float float10 = float3 * float5 - float1 * float7;
		float float11 = float1 * float6 - float2 * float5;
		this._m00(float5)._m01(float6)._m02(float7)._m03(0.0F)._m10(float9)._m11(float10)._m12(float11)._m13(0.0F)._m20(float1)._m21(float2)._m22(float3)._m23(0.0F)._m30(vector3fc.x())._m31(vector3fc.y())._m32(vector3fc.z())._m33(1.0F)._properties(18);
		return this;
	}

	public Matrix4f billboardSpherical(Vector3fc vector3fc, Vector3fc vector3fc2) {
		float float1 = vector3fc2.x() - vector3fc.x();
		float float2 = vector3fc2.y() - vector3fc.y();
		float float3 = vector3fc2.z() - vector3fc.z();
		float float4 = -float2;
		float float5 = Math.sqrt(float1 * float1 + float2 * float2 + float3 * float3) + float3;
		float float6 = Math.invsqrt(float4 * float4 + float1 * float1 + float5 * float5);
		float4 *= float6;
		float float7 = float1 * float6;
		float5 *= float6;
		float float8 = (float4 + float4) * float4;
		float float9 = (float7 + float7) * float7;
		float float10 = (float4 + float4) * float7;
		float float11 = (float4 + float4) * float5;
		float float12 = (float7 + float7) * float5;
		this._m00(1.0F - float9)._m01(float10)._m02(-float12)._m03(0.0F)._m10(float10)._m11(1.0F - float8)._m12(float11)._m13(0.0F)._m20(float12)._m21(-float11)._m22(1.0F - float9 - float8)._m23(0.0F)._m30(vector3fc.x())._m31(vector3fc.y())._m32(vector3fc.z())._m33(1.0F)._properties(18);
		return this;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + Float.floatToIntBits(this.m00);
		int1 = 31 * int1 + Float.floatToIntBits(this.m01);
		int1 = 31 * int1 + Float.floatToIntBits(this.m02);
		int1 = 31 * int1 + Float.floatToIntBits(this.m03);
		int1 = 31 * int1 + Float.floatToIntBits(this.m10);
		int1 = 31 * int1 + Float.floatToIntBits(this.m11);
		int1 = 31 * int1 + Float.floatToIntBits(this.m12);
		int1 = 31 * int1 + Float.floatToIntBits(this.m13);
		int1 = 31 * int1 + Float.floatToIntBits(this.m20);
		int1 = 31 * int1 + Float.floatToIntBits(this.m21);
		int1 = 31 * int1 + Float.floatToIntBits(this.m22);
		int1 = 31 * int1 + Float.floatToIntBits(this.m23);
		int1 = 31 * int1 + Float.floatToIntBits(this.m30);
		int1 = 31 * int1 + Float.floatToIntBits(this.m31);
		int1 = 31 * int1 + Float.floatToIntBits(this.m32);
		int1 = 31 * int1 + Float.floatToIntBits(this.m33);
		return int1;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object == null) {
			return false;
		} else if (!(object instanceof Matrix4f)) {
			return false;
		} else {
			Matrix4fc matrix4fc = (Matrix4fc)object;
			if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(matrix4fc.m00())) {
				return false;
			} else if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(matrix4fc.m01())) {
				return false;
			} else if (Float.floatToIntBits(this.m02) != Float.floatToIntBits(matrix4fc.m02())) {
				return false;
			} else if (Float.floatToIntBits(this.m03) != Float.floatToIntBits(matrix4fc.m03())) {
				return false;
			} else if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(matrix4fc.m10())) {
				return false;
			} else if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(matrix4fc.m11())) {
				return false;
			} else if (Float.floatToIntBits(this.m12) != Float.floatToIntBits(matrix4fc.m12())) {
				return false;
			} else if (Float.floatToIntBits(this.m13) != Float.floatToIntBits(matrix4fc.m13())) {
				return false;
			} else if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(matrix4fc.m20())) {
				return false;
			} else if (Float.floatToIntBits(this.m21) != Float.floatToIntBits(matrix4fc.m21())) {
				return false;
			} else if (Float.floatToIntBits(this.m22) != Float.floatToIntBits(matrix4fc.m22())) {
				return false;
			} else if (Float.floatToIntBits(this.m23) != Float.floatToIntBits(matrix4fc.m23())) {
				return false;
			} else if (Float.floatToIntBits(this.m30) != Float.floatToIntBits(matrix4fc.m30())) {
				return false;
			} else if (Float.floatToIntBits(this.m31) != Float.floatToIntBits(matrix4fc.m31())) {
				return false;
			} else if (Float.floatToIntBits(this.m32) != Float.floatToIntBits(matrix4fc.m32())) {
				return false;
			} else {
				return Float.floatToIntBits(this.m33) == Float.floatToIntBits(matrix4fc.m33());
			}
		}
	}

	public boolean equals(Matrix4fc matrix4fc, float float1) {
		if (this == matrix4fc) {
			return true;
		} else if (matrix4fc == null) {
			return false;
		} else if (!(matrix4fc instanceof Matrix4f)) {
			return false;
		} else if (!Runtime.equals(this.m00, matrix4fc.m00(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m01, matrix4fc.m01(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m02, matrix4fc.m02(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m03, matrix4fc.m03(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m10, matrix4fc.m10(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m11, matrix4fc.m11(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m12, matrix4fc.m12(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m13, matrix4fc.m13(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m20, matrix4fc.m20(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m21, matrix4fc.m21(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m22, matrix4fc.m22(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m23, matrix4fc.m23(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m30, matrix4fc.m30(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m31, matrix4fc.m31(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m32, matrix4fc.m32(), float1)) {
			return false;
		} else {
			return Runtime.equals(this.m33, matrix4fc.m33(), float1);
		}
	}

	public Matrix4f pick(float float1, float float2, float float3, float float4, int[] intArray, Matrix4f matrix4f) {
		float float5 = (float)intArray[2] / float3;
		float float6 = (float)intArray[3] / float4;
		float float7 = ((float)intArray[2] + 2.0F * ((float)intArray[0] - float1)) / float3;
		float float8 = ((float)intArray[3] + 2.0F * ((float)intArray[1] - float2)) / float4;
		matrix4f._m30(this.m00 * float7 + this.m10 * float8 + this.m30)._m31(this.m01 * float7 + this.m11 * float8 + this.m31)._m32(this.m02 * float7 + this.m12 * float8 + this.m32)._m33(this.m03 * float7 + this.m13 * float8 + this.m33)._m00(this.m00 * float5)._m01(this.m01 * float5)._m02(this.m02 * float5)._m03(this.m03 * float5)._m10(this.m10 * float6)._m11(this.m11 * float6)._m12(this.m12 * float6)._m13(this.m13 * float6)._properties(0);
		return matrix4f;
	}

	public Matrix4f pick(float float1, float float2, float float3, float float4, int[] intArray) {
		return this.pick(float1, float2, float3, float4, intArray, this);
	}

	public boolean isAffine() {
		return this.m03 == 0.0F && this.m13 == 0.0F && this.m23 == 0.0F && this.m33 == 1.0F;
	}

	public Matrix4f swap(Matrix4f matrix4f) {
		MemUtil.INSTANCE.swap(this, matrix4f);
		int int1 = this.properties;
		this.properties = matrix4f.properties();
		matrix4f.properties = int1;
		return this;
	}

	public Matrix4f arcball(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		float float7 = this.m20 * -float1 + this.m30;
		float float8 = this.m21 * -float1 + this.m31;
		float float9 = this.m22 * -float1 + this.m32;
		float float10 = this.m23 * -float1 + this.m33;
		float float11 = Math.sin(float5);
		float float12 = Math.cosFromSin(float11, float5);
		float float13 = this.m10 * float12 + this.m20 * float11;
		float float14 = this.m11 * float12 + this.m21 * float11;
		float float15 = this.m12 * float12 + this.m22 * float11;
		float float16 = this.m13 * float12 + this.m23 * float11;
		float float17 = this.m20 * float12 - this.m10 * float11;
		float float18 = this.m21 * float12 - this.m11 * float11;
		float float19 = this.m22 * float12 - this.m12 * float11;
		float float20 = this.m23 * float12 - this.m13 * float11;
		float11 = Math.sin(float6);
		float12 = Math.cosFromSin(float11, float6);
		float float21 = this.m00 * float12 - float17 * float11;
		float float22 = this.m01 * float12 - float18 * float11;
		float float23 = this.m02 * float12 - float19 * float11;
		float float24 = this.m03 * float12 - float20 * float11;
		float float25 = this.m00 * float11 + float17 * float12;
		float float26 = this.m01 * float11 + float18 * float12;
		float float27 = this.m02 * float11 + float19 * float12;
		float float28 = this.m03 * float11 + float20 * float12;
		matrix4f._m30(-float21 * float2 - float13 * float3 - float25 * float4 + float7)._m31(-float22 * float2 - float14 * float3 - float26 * float4 + float8)._m32(-float23 * float2 - float15 * float3 - float27 * float4 + float9)._m33(-float24 * float2 - float16 * float3 - float28 * float4 + float10)._m20(float25)._m21(float26)._m22(float27)._m23(float28)._m10(float13)._m11(float14)._m12(float15)._m13(float16)._m00(float21)._m01(float22)._m02(float23)._m03(float24)._properties(this.properties & -14);
		return matrix4f;
	}

	public Matrix4f arcball(float float1, Vector3fc vector3fc, float float2, float float3, Matrix4f matrix4f) {
		return this.arcball(float1, vector3fc.x(), vector3fc.y(), vector3fc.z(), float2, float3, matrix4f);
	}

	public Matrix4f arcball(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.arcball(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4f arcball(float float1, Vector3fc vector3fc, float float2, float float3) {
		return this.arcball(float1, vector3fc.x(), vector3fc.y(), vector3fc.z(), float2, float3, this);
	}

	public Matrix4f frustumAabb(Vector3f vector3f, Vector3f vector3f2) {
		float float1 = Float.POSITIVE_INFINITY;
		float float2 = Float.POSITIVE_INFINITY;
		float float3 = Float.POSITIVE_INFINITY;
		float float4 = Float.NEGATIVE_INFINITY;
		float float5 = Float.NEGATIVE_INFINITY;
		float float6 = Float.NEGATIVE_INFINITY;
		for (int int1 = 0; int1 < 8; ++int1) {
			float float7 = (float)((int1 & 1) << 1) - 1.0F;
			float float8 = (float)((int1 >>> 1 & 1) << 1) - 1.0F;
			float float9 = (float)((int1 >>> 2 & 1) << 1) - 1.0F;
			float float10 = 1.0F / (this.m03 * float7 + this.m13 * float8 + this.m23 * float9 + this.m33);
			float float11 = (this.m00 * float7 + this.m10 * float8 + this.m20 * float9 + this.m30) * float10;
			float float12 = (this.m01 * float7 + this.m11 * float8 + this.m21 * float9 + this.m31) * float10;
			float float13 = (this.m02 * float7 + this.m12 * float8 + this.m22 * float9 + this.m32) * float10;
			float1 = float1 < float11 ? float1 : float11;
			float2 = float2 < float12 ? float2 : float12;
			float3 = float3 < float13 ? float3 : float13;
			float4 = float4 > float11 ? float4 : float11;
			float5 = float5 > float12 ? float5 : float12;
			float6 = float6 > float13 ? float6 : float13;
		}

		vector3f.x = float1;
		vector3f.y = float2;
		vector3f.z = float3;
		vector3f2.x = float4;
		vector3f2.y = float5;
		vector3f2.z = float6;
		return this;
	}

	public Matrix4f projectedGridRange(Matrix4fc matrix4fc, float float1, float float2, Matrix4f matrix4f) {
		float float3 = Float.POSITIVE_INFINITY;
		float float4 = Float.POSITIVE_INFINITY;
		float float5 = Float.NEGATIVE_INFINITY;
		float float6 = Float.NEGATIVE_INFINITY;
		boolean boolean1 = false;
		for (int int1 = 0; int1 < 12; ++int1) {
			float float7;
			float float8;
			float float9;
			float float10;
			float float11;
			float float12;
			if (int1 < 4) {
				float7 = -1.0F;
				float10 = 1.0F;
				float8 = float11 = (float)((int1 & 1) << 1) - 1.0F;
				float9 = float12 = (float)((int1 >>> 1 & 1) << 1) - 1.0F;
			} else if (int1 < 8) {
				float8 = -1.0F;
				float11 = 1.0F;
				float7 = float10 = (float)((int1 & 1) << 1) - 1.0F;
				float9 = float12 = (float)((int1 >>> 1 & 1) << 1) - 1.0F;
			} else {
				float9 = -1.0F;
				float12 = 1.0F;
				float7 = float10 = (float)((int1 & 1) << 1) - 1.0F;
				float8 = float11 = (float)((int1 >>> 1 & 1) << 1) - 1.0F;
			}

			float float13 = 1.0F / (this.m03 * float7 + this.m13 * float8 + this.m23 * float9 + this.m33);
			float float14 = (this.m00 * float7 + this.m10 * float8 + this.m20 * float9 + this.m30) * float13;
			float float15 = (this.m01 * float7 + this.m11 * float8 + this.m21 * float9 + this.m31) * float13;
			float float16 = (this.m02 * float7 + this.m12 * float8 + this.m22 * float9 + this.m32) * float13;
			float13 = 1.0F / (this.m03 * float10 + this.m13 * float11 + this.m23 * float12 + this.m33);
			float float17 = (this.m00 * float10 + this.m10 * float11 + this.m20 * float12 + this.m30) * float13;
			float float18 = (this.m01 * float10 + this.m11 * float11 + this.m21 * float12 + this.m31) * float13;
			float float19 = (this.m02 * float10 + this.m12 * float11 + this.m22 * float12 + this.m32) * float13;
			float float20 = float17 - float14;
			float float21 = float18 - float15;
			float float22 = float19 - float16;
			float float23 = 1.0F / float21;
			for (int int2 = 0; int2 < 2; ++int2) {
				float float24 = -(float15 + (int2 == 0 ? float1 : float2)) * float23;
				if (float24 >= 0.0F && float24 <= 1.0F) {
					boolean1 = true;
					float float25 = float14 + float24 * float20;
					float float26 = float16 + float24 * float22;
					float13 = 1.0F / (matrix4fc.m03() * float25 + matrix4fc.m23() * float26 + matrix4fc.m33());
					float float27 = (matrix4fc.m00() * float25 + matrix4fc.m20() * float26 + matrix4fc.m30()) * float13;
					float float28 = (matrix4fc.m01() * float25 + matrix4fc.m21() * float26 + matrix4fc.m31()) * float13;
					float3 = float3 < float27 ? float3 : float27;
					float4 = float4 < float28 ? float4 : float28;
					float5 = float5 > float27 ? float5 : float27;
					float6 = float6 > float28 ? float6 : float28;
				}
			}
		}

		if (!boolean1) {
			return null;
		} else {
			matrix4f.set(float5 - float3, 0.0F, 0.0F, 0.0F, 0.0F, float6 - float4, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, float3, float4, 0.0F, 1.0F);
			matrix4f._properties(2);
			return matrix4f;
		}
	}

	public Matrix4f perspectiveFrustumSlice(float float1, float float2, Matrix4f matrix4f) {
		float float3 = (this.m23 + this.m22) / this.m32;
		float float4 = 1.0F / (float1 - float2);
		matrix4f._m00(this.m00 * float3 * float1)._m01(this.m01)._m02(this.m02)._m03(this.m03)._m10(this.m10)._m11(this.m11 * float3 * float1)._m12(this.m12)._m13(this.m13)._m20(this.m20)._m21(this.m21)._m22((float2 + float1) * float4)._m23(this.m23)._m30(this.m30)._m31(this.m31)._m32((float2 + float2) * float1 * float4)._m33(this.m33)._properties(this.properties & -29);
		return matrix4f;
	}

	public Matrix4f orthoCrop(Matrix4fc matrix4fc, Matrix4f matrix4f) {
		float float1 = Float.POSITIVE_INFINITY;
		float float2 = Float.NEGATIVE_INFINITY;
		float float3 = Float.POSITIVE_INFINITY;
		float float4 = Float.NEGATIVE_INFINITY;
		float float5 = Float.POSITIVE_INFINITY;
		float float6 = Float.NEGATIVE_INFINITY;
		for (int int1 = 0; int1 < 8; ++int1) {
			float float7 = (float)((int1 & 1) << 1) - 1.0F;
			float float8 = (float)((int1 >>> 1 & 1) << 1) - 1.0F;
			float float9 = (float)((int1 >>> 2 & 1) << 1) - 1.0F;
			float float10 = 1.0F / (this.m03 * float7 + this.m13 * float8 + this.m23 * float9 + this.m33);
			float float11 = (this.m00 * float7 + this.m10 * float8 + this.m20 * float9 + this.m30) * float10;
			float float12 = (this.m01 * float7 + this.m11 * float8 + this.m21 * float9 + this.m31) * float10;
			float float13 = (this.m02 * float7 + this.m12 * float8 + this.m22 * float9 + this.m32) * float10;
			float10 = 1.0F / (matrix4fc.m03() * float11 + matrix4fc.m13() * float12 + matrix4fc.m23() * float13 + matrix4fc.m33());
			float float14 = matrix4fc.m00() * float11 + matrix4fc.m10() * float12 + matrix4fc.m20() * float13 + matrix4fc.m30();
			float float15 = matrix4fc.m01() * float11 + matrix4fc.m11() * float12 + matrix4fc.m21() * float13 + matrix4fc.m31();
			float float16 = (matrix4fc.m02() * float11 + matrix4fc.m12() * float12 + matrix4fc.m22() * float13 + matrix4fc.m32()) * float10;
			float1 = float1 < float14 ? float1 : float14;
			float2 = float2 > float14 ? float2 : float14;
			float3 = float3 < float15 ? float3 : float15;
			float4 = float4 > float15 ? float4 : float15;
			float5 = float5 < float16 ? float5 : float16;
			float6 = float6 > float16 ? float6 : float16;
		}

		return matrix4f.setOrtho(float1, float2, float3, float4, -float6, -float5);
	}

	public Matrix4f trapezoidCrop(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = float4 - float2;
		float float10 = float1 - float3;
		float float11 = -float9;
		float float12 = float9 * float2 - float10 * float1;
		float float13 = -(float9 * float1 + float10 * float2);
		float float14 = float10 * float7 + float11 * float8 + float12;
		float float15 = float9 * float7 + float10 * float8 + float13;
		float float16 = -float14 / float15;
		float float17 = float10 + float16 * float9;
		float11 += float16 * float10;
		float12 += float16 * float13;
		float float18 = float17 * float3 + float11 * float4 + float12;
		float float19 = float17 * float5 + float11 * float6 + float12;
		float float20 = float18 * float15 / (float19 - float18);
		float13 += float20;
		float float21 = 2.0F / float19;
		float float22 = 1.0F / (float15 + float20);
		float float23 = (float22 + float22) * float20 / (1.0F - float22 * float20);
		float float24 = float9 * float22;
		float float25 = float10 * float22;
		float float26 = float13 * float22;
		float float27 = (float23 + 1.0F) * float24;
		float float28 = (float23 + 1.0F) * float25;
		float13 = (float23 + 1.0F) * float26 - float23;
		float17 = float21 * float17 - float24;
		float11 = float21 * float11 - float25;
		float12 = float21 * float12 - float26;
		this.set(float17, float27, 0.0F, float24, float11, float28, 0.0F, float25, 0.0F, 0.0F, 1.0F, 0.0F, float12, float13, 0.0F, float26);
		this._properties(0);
		return this;
	}

	public Matrix4f transformAab(float float1, float float2, float float3, float float4, float float5, float float6, Vector3f vector3f, Vector3f vector3f2) {
		float float7 = this.m00 * float1;
		float float8 = this.m01 * float1;
		float float9 = this.m02 * float1;
		float float10 = this.m00 * float4;
		float float11 = this.m01 * float4;
		float float12 = this.m02 * float4;
		float float13 = this.m10 * float2;
		float float14 = this.m11 * float2;
		float float15 = this.m12 * float2;
		float float16 = this.m10 * float5;
		float float17 = this.m11 * float5;
		float float18 = this.m12 * float5;
		float float19 = this.m20 * float3;
		float float20 = this.m21 * float3;
		float float21 = this.m22 * float3;
		float float22 = this.m20 * float6;
		float float23 = this.m21 * float6;
		float float24 = this.m22 * float6;
		float float25;
		float float26;
		if (float7 < float10) {
			float25 = float7;
			float26 = float10;
		} else {
			float25 = float10;
			float26 = float7;
		}

		float float27;
		float float28;
		if (float8 < float11) {
			float27 = float8;
			float28 = float11;
		} else {
			float27 = float11;
			float28 = float8;
		}

		float float29;
		float float30;
		if (float9 < float12) {
			float29 = float9;
			float30 = float12;
		} else {
			float29 = float12;
			float30 = float9;
		}

		float float31;
		float float32;
		if (float13 < float16) {
			float31 = float13;
			float32 = float16;
		} else {
			float31 = float16;
			float32 = float13;
		}

		float float33;
		float float34;
		if (float14 < float17) {
			float33 = float14;
			float34 = float17;
		} else {
			float33 = float17;
			float34 = float14;
		}

		float float35;
		float float36;
		if (float15 < float18) {
			float35 = float15;
			float36 = float18;
		} else {
			float35 = float18;
			float36 = float15;
		}

		float float37;
		float float38;
		if (float19 < float22) {
			float37 = float19;
			float38 = float22;
		} else {
			float37 = float22;
			float38 = float19;
		}

		float float39;
		float float40;
		if (float20 < float23) {
			float39 = float20;
			float40 = float23;
		} else {
			float39 = float23;
			float40 = float20;
		}

		float float41;
		float float42;
		if (float21 < float24) {
			float41 = float21;
			float42 = float24;
		} else {
			float41 = float24;
			float42 = float21;
		}

		vector3f.x = float25 + float31 + float37 + this.m30;
		vector3f.y = float27 + float33 + float39 + this.m31;
		vector3f.z = float29 + float35 + float41 + this.m32;
		vector3f2.x = float26 + float32 + float38 + this.m30;
		vector3f2.y = float28 + float34 + float40 + this.m31;
		vector3f2.z = float30 + float36 + float42 + this.m32;
		return this;
	}

	public Matrix4f transformAab(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3f vector3f, Vector3f vector3f2) {
		return this.transformAab(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3f, vector3f2);
	}

	public Matrix4f lerp(Matrix4fc matrix4fc, float float1) {
		return this.lerp(matrix4fc, float1, this);
	}

	public Matrix4f lerp(Matrix4fc matrix4fc, float float1, Matrix4f matrix4f) {
		matrix4f._m00(Math.fma(matrix4fc.m00() - this.m00, float1, this.m00))._m01(Math.fma(matrix4fc.m01() - this.m01, float1, this.m01))._m02(Math.fma(matrix4fc.m02() - this.m02, float1, this.m02))._m03(Math.fma(matrix4fc.m03() - this.m03, float1, this.m03))._m10(Math.fma(matrix4fc.m10() - this.m10, float1, this.m10))._m11(Math.fma(matrix4fc.m11() - this.m11, float1, this.m11))._m12(Math.fma(matrix4fc.m12() - this.m12, float1, this.m12))._m13(Math.fma(matrix4fc.m13() - this.m13, float1, this.m13))._m20(Math.fma(matrix4fc.m20() - this.m20, float1, this.m20))._m21(Math.fma(matrix4fc.m21() - this.m21, float1, this.m21))._m22(Math.fma(matrix4fc.m22() - this.m22, float1, this.m22))._m23(Math.fma(matrix4fc.m23() - this.m23, float1, this.m23))._m30(Math.fma(matrix4fc.m30() - this.m30, float1, this.m30))._m31(Math.fma(matrix4fc.m31() - this.m31, float1, this.m31))._m32(Math.fma(matrix4fc.m32() - this.m32, float1, this.m32))._m33(Math.fma(matrix4fc.m33() - this.m33, float1, this.m33))._properties(this.properties & matrix4fc.properties());
		return matrix4f;
	}

	public Matrix4f rotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4f matrix4f) {
		return this.rotateTowards(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), matrix4f);
	}

	public Matrix4f rotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.rotateTowards(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), this);
	}

	public Matrix4f rotateTowards(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.rotateTowards(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4f rotateTowards(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f) {
		float float7 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float float8 = float1 * float7;
		float float9 = float2 * float7;
		float float10 = float3 * float7;
		float float11 = float5 * float10 - float6 * float9;
		float float12 = float6 * float8 - float4 * float10;
		float float13 = float4 * float9 - float5 * float8;
		float float14 = Math.invsqrt(float11 * float11 + float12 * float12 + float13 * float13);
		float11 *= float14;
		float12 *= float14;
		float13 *= float14;
		float float15 = float9 * float13 - float10 * float12;
		float float16 = float10 * float11 - float8 * float13;
		float float17 = float8 * float12 - float9 * float11;
		matrix4f._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33);
		float float18 = this.m00 * float11 + this.m10 * float12 + this.m20 * float13;
		float float19 = this.m01 * float11 + this.m11 * float12 + this.m21 * float13;
		float float20 = this.m02 * float11 + this.m12 * float12 + this.m22 * float13;
		float float21 = this.m03 * float11 + this.m13 * float12 + this.m23 * float13;
		float float22 = this.m00 * float15 + this.m10 * float16 + this.m20 * float17;
		float float23 = this.m01 * float15 + this.m11 * float16 + this.m21 * float17;
		float float24 = this.m02 * float15 + this.m12 * float16 + this.m22 * float17;
		float float25 = this.m03 * float15 + this.m13 * float16 + this.m23 * float17;
		matrix4f._m20(this.m00 * float8 + this.m10 * float9 + this.m20 * float10)._m21(this.m01 * float8 + this.m11 * float9 + this.m21 * float10)._m22(this.m02 * float8 + this.m12 * float9 + this.m22 * float10)._m23(this.m03 * float8 + this.m13 * float9 + this.m23 * float10)._m00(float18)._m01(float19)._m02(float20)._m03(float21)._m10(float22)._m11(float23)._m12(float24)._m13(float25)._properties(this.properties & -14);
		return matrix4f;
	}

	public Matrix4f rotationTowards(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.rotationTowards(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix4f rotationTowards(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float float8 = float1 * float7;
		float float9 = float2 * float7;
		float float10 = float3 * float7;
		float float11 = float5 * float10 - float6 * float9;
		float float12 = float6 * float8 - float4 * float10;
		float float13 = float4 * float9 - float5 * float8;
		float float14 = Math.invsqrt(float11 * float11 + float12 * float12 + float13 * float13);
		float11 *= float14;
		float12 *= float14;
		float13 *= float14;
		float float15 = float9 * float13 - float10 * float12;
		float float16 = float10 * float11 - float8 * float13;
		float float17 = float8 * float12 - float9 * float11;
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this._m00(float11)._m01(float12)._m02(float13)._m10(float15)._m11(float16)._m12(float17)._m20(float8)._m21(float9)._m22(float10)._properties(18);
		return this;
	}

	public Matrix4f translationRotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		return this.translationRotateTowards(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z());
	}

	public Matrix4f translationRotateTowards(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		float float10 = Math.invsqrt(float4 * float4 + float5 * float5 + float6 * float6);
		float float11 = float4 * float10;
		float float12 = float5 * float10;
		float float13 = float6 * float10;
		float float14 = float8 * float13 - float9 * float12;
		float float15 = float9 * float11 - float7 * float13;
		float float16 = float7 * float12 - float8 * float11;
		float float17 = Math.invsqrt(float14 * float14 + float15 * float15 + float16 * float16);
		float14 *= float17;
		float15 *= float17;
		float16 *= float17;
		float float18 = float12 * float16 - float13 * float15;
		float float19 = float13 * float14 - float11 * float16;
		float float20 = float11 * float15 - float12 * float14;
		this._m00(float14)._m01(float15)._m02(float16)._m03(0.0F)._m10(float18)._m11(float19)._m12(float20)._m13(0.0F)._m20(float11)._m21(float12)._m22(float13)._m23(0.0F)._m30(float1)._m31(float2)._m32(float3)._m33(1.0F)._properties(18);
		return this;
	}

	public Vector3f getEulerAnglesZYX(Vector3f vector3f) {
		vector3f.x = Math.atan2(this.m12, this.m22);
		vector3f.y = Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22));
		vector3f.z = Math.atan2(this.m01, this.m00);
		return vector3f;
	}

	public Matrix4f affineSpan(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3, Vector3f vector3f4) {
		float float1 = this.m10 * this.m22;
		float float2 = this.m10 * this.m21;
		float float3 = this.m10 * this.m02;
		float float4 = this.m10 * this.m01;
		float float5 = this.m11 * this.m22;
		float float6 = this.m11 * this.m20;
		float float7 = this.m11 * this.m02;
		float float8 = this.m11 * this.m00;
		float float9 = this.m12 * this.m21;
		float float10 = this.m12 * this.m20;
		float float11 = this.m12 * this.m01;
		float float12 = this.m12 * this.m00;
		float float13 = this.m20 * this.m02;
		float float14 = this.m20 * this.m01;
		float float15 = this.m21 * this.m02;
		float float16 = this.m21 * this.m00;
		float float17 = this.m22 * this.m01;
		float float18 = this.m22 * this.m00;
		float float19 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
		float float20 = (float5 - float9) * float19;
		float float21 = (float15 - float17) * float19;
		float float22 = (float11 - float7) * float19;
		float float23 = (float10 - float1) * float19;
		float float24 = (float18 - float13) * float19;
		float float25 = (float3 - float12) * float19;
		float float26 = (float2 - float6) * float19;
		float float27 = (float14 - float16) * float19;
		float float28 = (float8 - float4) * float19;
		vector3f.x = -float20 - float23 - float26 + (float1 * this.m31 - float2 * this.m32 + float6 * this.m32 - float5 * this.m30 + float9 * this.m30 - float10 * this.m31) * float19;
		vector3f.y = -float21 - float24 - float27 + (float13 * this.m31 - float14 * this.m32 + float16 * this.m32 - float15 * this.m30 + float17 * this.m30 - float18 * this.m31) * float19;
		vector3f.z = -float22 - float25 - float28 + (float7 * this.m30 - float11 * this.m30 + float12 * this.m31 - float3 * this.m31 + float4 * this.m32 - float8 * this.m32) * float19;
		vector3f2.x = 2.0F * float20;
		vector3f2.y = 2.0F * float21;
		vector3f2.z = 2.0F * float22;
		vector3f3.x = 2.0F * float23;
		vector3f3.y = 2.0F * float24;
		vector3f3.z = 2.0F * float25;
		vector3f4.x = 2.0F * float26;
		vector3f4.y = 2.0F * float27;
		vector3f4.z = 2.0F * float28;
		return this;
	}

	public boolean testPoint(float float1, float float2, float float3) {
		float float4 = this.m03 + this.m00;
		float float5 = this.m13 + this.m10;
		float float6 = this.m23 + this.m20;
		float float7 = this.m33 + this.m30;
		float float8 = this.m03 - this.m00;
		float float9 = this.m13 - this.m10;
		float float10 = this.m23 - this.m20;
		float float11 = this.m33 - this.m30;
		float float12 = this.m03 + this.m01;
		float float13 = this.m13 + this.m11;
		float float14 = this.m23 + this.m21;
		float float15 = this.m33 + this.m31;
		float float16 = this.m03 - this.m01;
		float float17 = this.m13 - this.m11;
		float float18 = this.m23 - this.m21;
		float float19 = this.m33 - this.m31;
		float float20 = this.m03 + this.m02;
		float float21 = this.m13 + this.m12;
		float float22 = this.m23 + this.m22;
		float float23 = this.m33 + this.m32;
		float float24 = this.m03 - this.m02;
		float float25 = this.m13 - this.m12;
		float float26 = this.m23 - this.m22;
		float float27 = this.m33 - this.m32;
		return float4 * float1 + float5 * float2 + float6 * float3 + float7 >= 0.0F && float8 * float1 + float9 * float2 + float10 * float3 + float11 >= 0.0F && float12 * float1 + float13 * float2 + float14 * float3 + float15 >= 0.0F && float16 * float1 + float17 * float2 + float18 * float3 + float19 >= 0.0F && float20 * float1 + float21 * float2 + float22 * float3 + float23 >= 0.0F && float24 * float1 + float25 * float2 + float26 * float3 + float27 >= 0.0F;
	}

	public boolean testSphere(float float1, float float2, float float3, float float4) {
		float float5 = this.m03 + this.m00;
		float float6 = this.m13 + this.m10;
		float float7 = this.m23 + this.m20;
		float float8 = this.m33 + this.m30;
		float float9 = Math.invsqrt(float5 * float5 + float6 * float6 + float7 * float7);
		float5 *= float9;
		float6 *= float9;
		float7 *= float9;
		float8 *= float9;
		float float10 = this.m03 - this.m00;
		float float11 = this.m13 - this.m10;
		float float12 = this.m23 - this.m20;
		float float13 = this.m33 - this.m30;
		float9 = Math.invsqrt(float10 * float10 + float11 * float11 + float12 * float12);
		float10 *= float9;
		float11 *= float9;
		float12 *= float9;
		float13 *= float9;
		float float14 = this.m03 + this.m01;
		float float15 = this.m13 + this.m11;
		float float16 = this.m23 + this.m21;
		float float17 = this.m33 + this.m31;
		float9 = Math.invsqrt(float14 * float14 + float15 * float15 + float16 * float16);
		float14 *= float9;
		float15 *= float9;
		float16 *= float9;
		float17 *= float9;
		float float18 = this.m03 - this.m01;
		float float19 = this.m13 - this.m11;
		float float20 = this.m23 - this.m21;
		float float21 = this.m33 - this.m31;
		float9 = Math.invsqrt(float18 * float18 + float19 * float19 + float20 * float20);
		float18 *= float9;
		float19 *= float9;
		float20 *= float9;
		float21 *= float9;
		float float22 = this.m03 + this.m02;
		float float23 = this.m13 + this.m12;
		float float24 = this.m23 + this.m22;
		float float25 = this.m33 + this.m32;
		float9 = Math.invsqrt(float22 * float22 + float23 * float23 + float24 * float24);
		float22 *= float9;
		float23 *= float9;
		float24 *= float9;
		float25 *= float9;
		float float26 = this.m03 - this.m02;
		float float27 = this.m13 - this.m12;
		float float28 = this.m23 - this.m22;
		float float29 = this.m33 - this.m32;
		float9 = Math.invsqrt(float26 * float26 + float27 * float27 + float28 * float28);
		float26 *= float9;
		float27 *= float9;
		float28 *= float9;
		float29 *= float9;
		return float5 * float1 + float6 * float2 + float7 * float3 + float8 >= -float4 && float10 * float1 + float11 * float2 + float12 * float3 + float13 >= -float4 && float14 * float1 + float15 * float2 + float16 * float3 + float17 >= -float4 && float18 * float1 + float19 * float2 + float20 * float3 + float21 >= -float4 && float22 * float1 + float23 * float2 + float24 * float3 + float25 >= -float4 && float26 * float1 + float27 * float2 + float28 * float3 + float29 >= -float4;
	}

	public boolean testAab(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = this.m03 + this.m00;
		float float8 = this.m13 + this.m10;
		float float9 = this.m23 + this.m20;
		float float10 = this.m33 + this.m30;
		float float11 = this.m03 - this.m00;
		float float12 = this.m13 - this.m10;
		float float13 = this.m23 - this.m20;
		float float14 = this.m33 - this.m30;
		float float15 = this.m03 + this.m01;
		float float16 = this.m13 + this.m11;
		float float17 = this.m23 + this.m21;
		float float18 = this.m33 + this.m31;
		float float19 = this.m03 - this.m01;
		float float20 = this.m13 - this.m11;
		float float21 = this.m23 - this.m21;
		float float22 = this.m33 - this.m31;
		float float23 = this.m03 + this.m02;
		float float24 = this.m13 + this.m12;
		float float25 = this.m23 + this.m22;
		float float26 = this.m33 + this.m32;
		float float27 = this.m03 - this.m02;
		float float28 = this.m13 - this.m12;
		float float29 = this.m23 - this.m22;
		float float30 = this.m33 - this.m32;
		return float7 * (float7 < 0.0F ? float1 : float4) + float8 * (float8 < 0.0F ? float2 : float5) + float9 * (float9 < 0.0F ? float3 : float6) >= -float10 && float11 * (float11 < 0.0F ? float1 : float4) + float12 * (float12 < 0.0F ? float2 : float5) + float13 * (float13 < 0.0F ? float3 : float6) >= -float14 && float15 * (float15 < 0.0F ? float1 : float4) + float16 * (float16 < 0.0F ? float2 : float5) + float17 * (float17 < 0.0F ? float3 : float6) >= -float18 && float19 * (float19 < 0.0F ? float1 : float4) + float20 * (float20 < 0.0F ? float2 : float5) + float21 * (float21 < 0.0F ? float3 : float6) >= -float22 && float23 * (float23 < 0.0F ? float1 : float4) + float24 * (float24 < 0.0F ? float2 : float5) + float25 * (float25 < 0.0F ? float3 : float6) >= -float26 && float27 * (float27 < 0.0F ? float1 : float4) + float28 * (float28 < 0.0F ? float2 : float5) + float29 * (float29 < 0.0F ? float3 : float6) >= -float30;
	}

	public Matrix4f obliqueZ(float float1, float float2) {
		this.m20 += this.m00 * float1 + this.m10 * float2;
		this.m21 += this.m01 * float1 + this.m11 * float2;
		this.m22 += this.m02 * float1 + this.m12 * float2;
		this._properties(this.properties & 2);
		return this;
	}

	public Matrix4f obliqueZ(float float1, float float2, Matrix4f matrix4f) {
		matrix4f._m00(this.m00)._m01(this.m01)._m02(this.m02)._m03(this.m03)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m13(this.m13)._m20(this.m00 * float1 + this.m10 * float2 + this.m20)._m21(this.m01 * float1 + this.m11 * float2 + this.m21)._m22(this.m02 * float1 + this.m12 * float2 + this.m22)._m23(this.m23)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & 2);
		return matrix4f;
	}

	public static void projViewFromRectangle(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3, Vector3f vector3f4, float float1, boolean boolean1, Matrix4f matrix4f, Matrix4f matrix4f2) {
		float float2 = vector3f4.y * vector3f3.z - vector3f4.z * vector3f3.y;
		float float3 = vector3f4.z * vector3f3.x - vector3f4.x * vector3f3.z;
		float float4 = vector3f4.x * vector3f3.y - vector3f4.y * vector3f3.x;
		float float5 = float2 * (vector3f2.x - vector3f.x) + float3 * (vector3f2.y - vector3f.y) + float4 * (vector3f2.z - vector3f.z);
		float float6 = float5 >= 0.0F ? 1.0F : -1.0F;
		float2 *= float6;
		float3 *= float6;
		float4 *= float6;
		float5 *= float6;
		matrix4f2.setLookAt(vector3f.x, vector3f.y, vector3f.z, vector3f.x + float2, vector3f.y + float3, vector3f.z + float4, vector3f4.x, vector3f4.y, vector3f4.z);
		float float7 = matrix4f2.m00 * vector3f2.x + matrix4f2.m10 * vector3f2.y + matrix4f2.m20 * vector3f2.z + matrix4f2.m30;
		float float8 = matrix4f2.m01 * vector3f2.x + matrix4f2.m11 * vector3f2.y + matrix4f2.m21 * vector3f2.z + matrix4f2.m31;
		float float9 = matrix4f2.m00 * vector3f3.x + matrix4f2.m10 * vector3f3.y + matrix4f2.m20 * vector3f3.z;
		float float10 = matrix4f2.m01 * vector3f4.x + matrix4f2.m11 * vector3f4.y + matrix4f2.m21 * vector3f4.z;
		float float11 = Math.sqrt(float2 * float2 + float3 * float3 + float4 * float4);
		float float12 = float5 / float11;
		float float13;
		if (Float.isInfinite(float1) && float1 < 0.0F) {
			float13 = float12;
			float12 = Float.POSITIVE_INFINITY;
		} else if (Float.isInfinite(float1) && float1 > 0.0F) {
			float13 = Float.POSITIVE_INFINITY;
		} else if (float1 < 0.0F) {
			float13 = float12;
			float12 += float1;
		} else {
			float13 = float12 + float1;
		}

		matrix4f.setFrustum(float7, float7 + float9, float8, float8 + float10, float12, float13, boolean1);
	}

	public Matrix4f withLookAtUp(Vector3fc vector3fc) {
		return this.withLookAtUp(vector3fc.x(), vector3fc.y(), vector3fc.z(), this);
	}

	public Matrix4f withLookAtUp(Vector3fc vector3fc, Matrix4f matrix4f) {
		return this.withLookAtUp(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4f withLookAtUp(float float1, float float2, float float3) {
		return this.withLookAtUp(float1, float2, float3, this);
	}

	public Matrix4f withLookAtUp(float float1, float float2, float float3, Matrix4f matrix4f) {
		float float4 = (float2 * this.m21 - float3 * this.m11) * this.m02 + (float3 * this.m01 - float1 * this.m21) * this.m12 + (float1 * this.m11 - float2 * this.m01) * this.m22;
		float float5 = float1 * this.m01 + float2 * this.m11 + float3 * this.m21;
		if ((this.properties & 16) == 0) {
			float5 *= Math.sqrt(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21);
		}

		float float6 = Math.invsqrt(float4 * float4 + float5 * float5);
		float float7 = float5 * float6;
		float float8 = float4 * float6;
		float float9 = float7 * this.m00 - float8 * this.m01;
		float float10 = float7 * this.m10 - float8 * this.m11;
		float float11 = float7 * this.m20 - float8 * this.m21;
		float float12 = float8 * this.m30 + float7 * this.m31;
		float float13 = float8 * this.m00 + float7 * this.m01;
		float float14 = float8 * this.m10 + float7 * this.m11;
		float float15 = float8 * this.m20 + float7 * this.m21;
		float float16 = float7 * this.m30 - float8 * this.m31;
		matrix4f._m00(float9)._m10(float10)._m20(float11)._m30(float16)._m01(float13)._m11(float14)._m21(float15)._m31(float12);
		if (matrix4f != this) {
			matrix4f._m02(this.m02)._m12(this.m12)._m22(this.m22)._m32(this.m32)._m03(this.m03)._m13(this.m13)._m23(this.m23)._m33(this.m33);
		}

		matrix4f._properties(this.properties & -14);
		return matrix4f;
	}

	public boolean isFinite() {
		return Math.isFinite(this.m00) && Math.isFinite(this.m01) && Math.isFinite(this.m02) && Math.isFinite(this.m03) && Math.isFinite(this.m10) && Math.isFinite(this.m11) && Math.isFinite(this.m12) && Math.isFinite(this.m13) && Math.isFinite(this.m20) && Math.isFinite(this.m21) && Math.isFinite(this.m22) && Math.isFinite(this.m23) && Math.isFinite(this.m30) && Math.isFinite(this.m31) && Math.isFinite(this.m32) && Math.isFinite(this.m33);
	}
}
