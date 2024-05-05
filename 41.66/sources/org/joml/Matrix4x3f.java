package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Matrix4x3f implements Externalizable,Matrix4x3fc {
	private static final long serialVersionUID = 1L;
	float m00;
	float m01;
	float m02;
	float m10;
	float m11;
	float m12;
	float m20;
	float m21;
	float m22;
	float m30;
	float m31;
	float m32;
	int properties;

	public Matrix4x3f() {
		this.m00 = 1.0F;
		this.m11 = 1.0F;
		this.m22 = 1.0F;
		this.properties = 28;
	}

	public Matrix4x3f(Matrix3fc matrix3fc) {
		this.set(matrix3fc);
	}

	public Matrix4x3f(Matrix4x3fc matrix4x3fc) {
		this.set(matrix4x3fc);
	}

	public Matrix4x3f(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		this.m00 = float1;
		this.m01 = float2;
		this.m02 = float3;
		this.m10 = float4;
		this.m11 = float5;
		this.m12 = float6;
		this.m20 = float7;
		this.m21 = float8;
		this.m22 = float9;
		this.m30 = float10;
		this.m31 = float11;
		this.m32 = float12;
		this.determineProperties();
	}

	public Matrix4x3f(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
		this.determineProperties();
	}

	public Matrix4x3f(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4) {
		this.set(vector3fc, vector3fc2, vector3fc3, vector3fc4).determineProperties();
	}

	public Matrix4x3f assume(int int1) {
		this.properties = int1;
		return this;
	}

	public Matrix4x3f determineProperties() {
		int int1 = 0;
		if (this.m00 == 1.0F && this.m01 == 0.0F && this.m02 == 0.0F && this.m10 == 0.0F && this.m11 == 1.0F && this.m12 == 0.0F && this.m20 == 0.0F && this.m21 == 0.0F && this.m22 == 1.0F) {
			int1 |= 24;
			if (this.m30 == 0.0F && this.m31 == 0.0F && this.m32 == 0.0F) {
				int1 |= 4;
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

	public float m10() {
		return this.m10;
	}

	public float m11() {
		return this.m11;
	}

	public float m12() {
		return this.m12;
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

	public float m30() {
		return this.m30;
	}

	public float m31() {
		return this.m31;
	}

	public float m32() {
		return this.m32;
	}

	public Matrix4x3f m00(float float1) {
		this.m00 = float1;
		this.properties &= -17;
		if (float1 != 1.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4x3f m01(float float1) {
		this.m01 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4x3f m02(float float1) {
		this.m02 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4x3f m10(float float1) {
		this.m10 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4x3f m11(float float1) {
		this.m11 = float1;
		this.properties &= -17;
		if (float1 != 1.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4x3f m12(float float1) {
		this.m12 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4x3f m20(float float1) {
		this.m20 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4x3f m21(float float1) {
		this.m21 = float1;
		this.properties &= -17;
		if (float1 != 0.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4x3f m22(float float1) {
		this.m22 = float1;
		this.properties &= -17;
		if (float1 != 1.0F) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4x3f m30(float float1) {
		this.m30 = float1;
		if (float1 != 0.0F) {
			this.properties &= -5;
		}

		return this;
	}

	public Matrix4x3f m31(float float1) {
		this.m31 = float1;
		if (float1 != 0.0F) {
			this.properties &= -5;
		}

		return this;
	}

	public Matrix4x3f m32(float float1) {
		this.m32 = float1;
		if (float1 != 0.0F) {
			this.properties &= -5;
		}

		return this;
	}

	Matrix4x3f _properties(int int1) {
		this.properties = int1;
		return this;
	}

	Matrix4x3f _m00(float float1) {
		this.m00 = float1;
		return this;
	}

	Matrix4x3f _m01(float float1) {
		this.m01 = float1;
		return this;
	}

	Matrix4x3f _m02(float float1) {
		this.m02 = float1;
		return this;
	}

	Matrix4x3f _m10(float float1) {
		this.m10 = float1;
		return this;
	}

	Matrix4x3f _m11(float float1) {
		this.m11 = float1;
		return this;
	}

	Matrix4x3f _m12(float float1) {
		this.m12 = float1;
		return this;
	}

	Matrix4x3f _m20(float float1) {
		this.m20 = float1;
		return this;
	}

	Matrix4x3f _m21(float float1) {
		this.m21 = float1;
		return this;
	}

	Matrix4x3f _m22(float float1) {
		this.m22 = float1;
		return this;
	}

	Matrix4x3f _m30(float float1) {
		this.m30 = float1;
		return this;
	}

	Matrix4x3f _m31(float float1) {
		this.m31 = float1;
		return this;
	}

	Matrix4x3f _m32(float float1) {
		this.m32 = float1;
		return this;
	}

	public Matrix4x3f identity() {
		if ((this.properties & 4) != 0) {
			return this;
		} else {
			MemUtil.INSTANCE.identity(this);
			this.properties = 28;
			return this;
		}
	}

	public Matrix4x3f set(Matrix4x3fc matrix4x3fc) {
		this.m00 = matrix4x3fc.m00();
		this.m01 = matrix4x3fc.m01();
		this.m02 = matrix4x3fc.m02();
		this.m10 = matrix4x3fc.m10();
		this.m11 = matrix4x3fc.m11();
		this.m12 = matrix4x3fc.m12();
		this.m20 = matrix4x3fc.m20();
		this.m21 = matrix4x3fc.m21();
		this.m22 = matrix4x3fc.m22();
		this.m30 = matrix4x3fc.m30();
		this.m31 = matrix4x3fc.m31();
		this.m32 = matrix4x3fc.m32();
		this.properties = matrix4x3fc.properties();
		return this;
	}

	public Matrix4x3f set(Matrix4fc matrix4fc) {
		this.m00 = matrix4fc.m00();
		this.m01 = matrix4fc.m01();
		this.m02 = matrix4fc.m02();
		this.m10 = matrix4fc.m10();
		this.m11 = matrix4fc.m11();
		this.m12 = matrix4fc.m12();
		this.m20 = matrix4fc.m20();
		this.m21 = matrix4fc.m21();
		this.m22 = matrix4fc.m22();
		this.m30 = matrix4fc.m30();
		this.m31 = matrix4fc.m31();
		this.m32 = matrix4fc.m32();
		this.properties = matrix4fc.properties() & 28;
		return this;
	}

	public Matrix4f get(Matrix4f matrix4f) {
		return matrix4f.set4x3((Matrix4x3fc)this);
	}

	public Matrix4d get(Matrix4d matrix4d) {
		return matrix4d.set4x3((Matrix4x3fc)this);
	}

	public Matrix4x3f set(Matrix3fc matrix3fc) {
		this.m00 = matrix3fc.m00();
		this.m01 = matrix3fc.m01();
		this.m02 = matrix3fc.m02();
		this.m10 = matrix3fc.m10();
		this.m11 = matrix3fc.m11();
		this.m12 = matrix3fc.m12();
		this.m20 = matrix3fc.m20();
		this.m21 = matrix3fc.m21();
		this.m22 = matrix3fc.m22();
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		return this.determineProperties();
	}

	public Matrix4x3f set(AxisAngle4f axisAngle4f) {
		float float1 = axisAngle4f.x;
		float float2 = axisAngle4f.y;
		float float3 = axisAngle4f.z;
		float float4 = axisAngle4f.angle;
		float float5 = Math.sqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float5 = 1.0F / float5;
		float1 *= float5;
		float2 *= float5;
		float3 *= float5;
		float float6 = Math.sin(float4);
		float float7 = Math.cosFromSin(float6, float4);
		float float8 = 1.0F - float7;
		this.m00 = float7 + float1 * float1 * float8;
		this.m11 = float7 + float2 * float2 * float8;
		this.m22 = float7 + float3 * float3 * float8;
		float float9 = float1 * float2 * float8;
		float float10 = float3 * float6;
		this.m10 = float9 - float10;
		this.m01 = float9 + float10;
		float9 = float1 * float3 * float8;
		float10 = float2 * float6;
		this.m20 = float9 + float10;
		this.m02 = float9 - float10;
		float9 = float2 * float3 * float8;
		float10 = float1 * float6;
		this.m21 = float9 - float10;
		this.m12 = float9 + float10;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f set(AxisAngle4d axisAngle4d) {
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
		this.m00 = (float)(double7 + double1 * double1 * double8);
		this.m11 = (float)(double7 + double2 * double2 * double8);
		this.m22 = (float)(double7 + double3 * double3 * double8);
		double double9 = double1 * double2 * double8;
		double double10 = double3 * double6;
		this.m10 = (float)(double9 - double10);
		this.m01 = (float)(double9 + double10);
		double9 = double1 * double3 * double8;
		double10 = double2 * double6;
		this.m20 = (float)(double9 + double10);
		this.m02 = (float)(double9 - double10);
		double9 = double2 * double3 * double8;
		double10 = double1 * double6;
		this.m21 = (float)(double9 - double10);
		this.m12 = (float)(double9 + double10);
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f set(Quaternionfc quaternionfc) {
		return this.rotation(quaternionfc);
	}

	public Matrix4x3f set(Quaterniondc quaterniondc) {
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
		this.m00 = (float)(double1 + double2 - double4 - double3);
		this.m01 = (float)(double6 + double5 + double5 + double6);
		this.m02 = (float)(double7 - double8 + double7 - double8);
		this.m10 = (float)(-double5 + double6 - double5 + double6);
		this.m11 = (float)(double3 - double4 + double1 - double2);
		this.m12 = (float)(double9 + double9 + double10 + double10);
		this.m20 = (float)(double8 + double7 + double7 + double8);
		this.m21 = (float)(double9 + double9 - double10 - double10);
		this.m22 = (float)(double4 - double3 - double2 + double1);
		this.properties = 16;
		return this;
	}

	public Matrix4x3f set(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4) {
		this.m00 = vector3fc.x();
		this.m01 = vector3fc.y();
		this.m02 = vector3fc.z();
		this.m10 = vector3fc2.x();
		this.m11 = vector3fc2.y();
		this.m12 = vector3fc2.z();
		this.m20 = vector3fc3.x();
		this.m21 = vector3fc3.y();
		this.m22 = vector3fc3.z();
		this.m30 = vector3fc4.x();
		this.m31 = vector3fc4.y();
		this.m32 = vector3fc4.z();
		return this.determineProperties();
	}

	public Matrix4x3f set3x3(Matrix4x3fc matrix4x3fc) {
		this.m00 = matrix4x3fc.m00();
		this.m01 = matrix4x3fc.m01();
		this.m02 = matrix4x3fc.m02();
		this.m10 = matrix4x3fc.m10();
		this.m11 = matrix4x3fc.m11();
		this.m12 = matrix4x3fc.m12();
		this.m20 = matrix4x3fc.m20();
		this.m21 = matrix4x3fc.m21();
		this.m22 = matrix4x3fc.m22();
		this.properties &= matrix4x3fc.properties();
		return this;
	}

	public Matrix4x3f mul(Matrix4x3fc matrix4x3fc) {
		return this.mul(matrix4x3fc, this);
	}

	public Matrix4x3f mul(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.set(matrix4x3fc);
		} else if ((matrix4x3fc.properties() & 4) != 0) {
			return matrix4x3f.set((Matrix4x3fc)this);
		} else {
			return (this.properties & 8) != 0 ? this.mulTranslation(matrix4x3fc, matrix4x3f) : this.mulGeneric(matrix4x3fc, matrix4x3f);
		}
	}

	private Matrix4x3f mulGeneric(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f) {
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
		return matrix4x3f._m00(Math.fma(float1, float10, Math.fma(float4, float11, float7 * float12)))._m01(Math.fma(float2, float10, Math.fma(float5, float11, float8 * float12)))._m02(Math.fma(float3, float10, Math.fma(float6, float11, float9 * float12)))._m10(Math.fma(float1, float13, Math.fma(float4, float14, float7 * float15)))._m11(Math.fma(float2, float13, Math.fma(float5, float14, float8 * float15)))._m12(Math.fma(float3, float13, Math.fma(float6, float14, float9 * float15)))._m20(Math.fma(float1, float16, Math.fma(float4, float17, float7 * float18)))._m21(Math.fma(float2, float16, Math.fma(float5, float17, float8 * float18)))._m22(Math.fma(float3, float16, Math.fma(float6, float17, float9 * float18)))._m30(Math.fma(float1, float19, Math.fma(float4, float20, Math.fma(float7, float21, this.m30))))._m31(Math.fma(float2, float19, Math.fma(float5, float20, Math.fma(float8, float21, this.m31))))._m32(Math.fma(float3, float19, Math.fma(float6, float20, Math.fma(float9, float21, this.m32))))._properties(this.properties & matrix4x3fc.properties() & 16);
	}

	public Matrix4x3f mulTranslation(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f) {
		return matrix4x3f._m00(matrix4x3fc.m00())._m01(matrix4x3fc.m01())._m02(matrix4x3fc.m02())._m10(matrix4x3fc.m10())._m11(matrix4x3fc.m11())._m12(matrix4x3fc.m12())._m20(matrix4x3fc.m20())._m21(matrix4x3fc.m21())._m22(matrix4x3fc.m22())._m30(matrix4x3fc.m30() + this.m30)._m31(matrix4x3fc.m31() + this.m31)._m32(matrix4x3fc.m32() + this.m32)._properties(matrix4x3fc.properties() & 16);
	}

	public Matrix4x3f mulOrtho(Matrix4x3fc matrix4x3fc) {
		return this.mulOrtho(matrix4x3fc, this);
	}

	public Matrix4x3f mulOrtho(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f) {
		float float1 = this.m00 * matrix4x3fc.m00();
		float float2 = this.m11 * matrix4x3fc.m01();
		float float3 = this.m22 * matrix4x3fc.m02();
		float float4 = this.m00 * matrix4x3fc.m10();
		float float5 = this.m11 * matrix4x3fc.m11();
		float float6 = this.m22 * matrix4x3fc.m12();
		float float7 = this.m00 * matrix4x3fc.m20();
		float float8 = this.m11 * matrix4x3fc.m21();
		float float9 = this.m22 * matrix4x3fc.m22();
		float float10 = this.m00 * matrix4x3fc.m30() + this.m30;
		float float11 = this.m11 * matrix4x3fc.m31() + this.m31;
		float float12 = this.m22 * matrix4x3fc.m32() + this.m32;
		matrix4x3f.m00 = float1;
		matrix4x3f.m01 = float2;
		matrix4x3f.m02 = float3;
		matrix4x3f.m10 = float4;
		matrix4x3f.m11 = float5;
		matrix4x3f.m12 = float6;
		matrix4x3f.m20 = float7;
		matrix4x3f.m21 = float8;
		matrix4x3f.m22 = float9;
		matrix4x3f.m30 = float10;
		matrix4x3f.m31 = float11;
		matrix4x3f.m32 = float12;
		matrix4x3f.properties = this.properties & matrix4x3fc.properties() & 16;
		return matrix4x3f;
	}

	public Matrix4x3f fma(Matrix4x3fc matrix4x3fc, float float1) {
		return this.fma(matrix4x3fc, float1, this);
	}

	public Matrix4x3f fma(Matrix4x3fc matrix4x3fc, float float1, Matrix4x3f matrix4x3f) {
		matrix4x3f._m00(Math.fma(matrix4x3fc.m00(), float1, this.m00))._m01(Math.fma(matrix4x3fc.m01(), float1, this.m01))._m02(Math.fma(matrix4x3fc.m02(), float1, this.m02))._m10(Math.fma(matrix4x3fc.m10(), float1, this.m10))._m11(Math.fma(matrix4x3fc.m11(), float1, this.m11))._m12(Math.fma(matrix4x3fc.m12(), float1, this.m12))._m20(Math.fma(matrix4x3fc.m20(), float1, this.m20))._m21(Math.fma(matrix4x3fc.m21(), float1, this.m21))._m22(Math.fma(matrix4x3fc.m22(), float1, this.m22))._m30(Math.fma(matrix4x3fc.m30(), float1, this.m30))._m31(Math.fma(matrix4x3fc.m31(), float1, this.m31))._m32(Math.fma(matrix4x3fc.m32(), float1, this.m32))._properties(0);
		return matrix4x3f;
	}

	public Matrix4x3f add(Matrix4x3fc matrix4x3fc) {
		return this.add(matrix4x3fc, this);
	}

	public Matrix4x3f add(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f) {
		matrix4x3f.m00 = this.m00 + matrix4x3fc.m00();
		matrix4x3f.m01 = this.m01 + matrix4x3fc.m01();
		matrix4x3f.m02 = this.m02 + matrix4x3fc.m02();
		matrix4x3f.m10 = this.m10 + matrix4x3fc.m10();
		matrix4x3f.m11 = this.m11 + matrix4x3fc.m11();
		matrix4x3f.m12 = this.m12 + matrix4x3fc.m12();
		matrix4x3f.m20 = this.m20 + matrix4x3fc.m20();
		matrix4x3f.m21 = this.m21 + matrix4x3fc.m21();
		matrix4x3f.m22 = this.m22 + matrix4x3fc.m22();
		matrix4x3f.m30 = this.m30 + matrix4x3fc.m30();
		matrix4x3f.m31 = this.m31 + matrix4x3fc.m31();
		matrix4x3f.m32 = this.m32 + matrix4x3fc.m32();
		matrix4x3f.properties = 0;
		return matrix4x3f;
	}

	public Matrix4x3f sub(Matrix4x3fc matrix4x3fc) {
		return this.sub(matrix4x3fc, this);
	}

	public Matrix4x3f sub(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f) {
		matrix4x3f.m00 = this.m00 - matrix4x3fc.m00();
		matrix4x3f.m01 = this.m01 - matrix4x3fc.m01();
		matrix4x3f.m02 = this.m02 - matrix4x3fc.m02();
		matrix4x3f.m10 = this.m10 - matrix4x3fc.m10();
		matrix4x3f.m11 = this.m11 - matrix4x3fc.m11();
		matrix4x3f.m12 = this.m12 - matrix4x3fc.m12();
		matrix4x3f.m20 = this.m20 - matrix4x3fc.m20();
		matrix4x3f.m21 = this.m21 - matrix4x3fc.m21();
		matrix4x3f.m22 = this.m22 - matrix4x3fc.m22();
		matrix4x3f.m30 = this.m30 - matrix4x3fc.m30();
		matrix4x3f.m31 = this.m31 - matrix4x3fc.m31();
		matrix4x3f.m32 = this.m32 - matrix4x3fc.m32();
		matrix4x3f.properties = 0;
		return matrix4x3f;
	}

	public Matrix4x3f mulComponentWise(Matrix4x3fc matrix4x3fc) {
		return this.mulComponentWise(matrix4x3fc, this);
	}

	public Matrix4x3f mulComponentWise(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f) {
		matrix4x3f.m00 = this.m00 * matrix4x3fc.m00();
		matrix4x3f.m01 = this.m01 * matrix4x3fc.m01();
		matrix4x3f.m02 = this.m02 * matrix4x3fc.m02();
		matrix4x3f.m10 = this.m10 * matrix4x3fc.m10();
		matrix4x3f.m11 = this.m11 * matrix4x3fc.m11();
		matrix4x3f.m12 = this.m12 * matrix4x3fc.m12();
		matrix4x3f.m20 = this.m20 * matrix4x3fc.m20();
		matrix4x3f.m21 = this.m21 * matrix4x3fc.m21();
		matrix4x3f.m22 = this.m22 * matrix4x3fc.m22();
		matrix4x3f.m30 = this.m30 * matrix4x3fc.m30();
		matrix4x3f.m31 = this.m31 * matrix4x3fc.m31();
		matrix4x3f.m32 = this.m32 * matrix4x3fc.m32();
		matrix4x3f.properties = 0;
		return matrix4x3f;
	}

	public Matrix4x3f set(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		this.m00 = float1;
		this.m01 = float2;
		this.m02 = float3;
		this.m10 = float4;
		this.m11 = float5;
		this.m12 = float6;
		this.m20 = float7;
		this.m21 = float8;
		this.m22 = float9;
		this.m30 = float10;
		this.m31 = float11;
		this.m32 = float12;
		return this.determineProperties();
	}

	public Matrix4x3f set(float[] floatArray, int int1) {
		MemUtil.INSTANCE.copy(floatArray, int1, this);
		return this.determineProperties();
	}

	public Matrix4x3f set(float[] floatArray) {
		return this.set(floatArray, 0);
	}

	public Matrix4x3f set(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
		return this.determineProperties();
	}

	public Matrix4x3f set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this.determineProperties();
	}

	public Matrix4x3f setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this.determineProperties();
		}
	}

	public float determinant() {
		return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
	}

	public Matrix4x3f invert(Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.identity();
		} else {
			return (this.properties & 16) != 0 ? this.invertOrthonormal(matrix4x3f) : this.invertGeneric(matrix4x3f);
		}
	}

	private Matrix4x3f invertGeneric(Matrix4x3f matrix4x3f) {
		float float1 = this.m00 * this.m11;
		float float2 = this.m01 * this.m10;
		float float3 = this.m02 * this.m10;
		float float4 = this.m00 * this.m12;
		float float5 = this.m01 * this.m12;
		float float6 = this.m02 * this.m11;
		float float7 = 1.0F / ((float1 - float2) * this.m22 + (float3 - float4) * this.m21 + (float5 - float6) * this.m20);
		float float8 = this.m10 * this.m22;
		float float9 = this.m10 * this.m21;
		float float10 = this.m11 * this.m22;
		float float11 = this.m11 * this.m20;
		float float12 = this.m12 * this.m21;
		float float13 = this.m12 * this.m20;
		float float14 = this.m20 * this.m02;
		float float15 = this.m20 * this.m01;
		float float16 = this.m21 * this.m02;
		float float17 = this.m21 * this.m00;
		float float18 = this.m22 * this.m01;
		float float19 = this.m22 * this.m00;
		float float20 = (float10 - float12) * float7;
		float float21 = (float16 - float18) * float7;
		float float22 = (float5 - float6) * float7;
		float float23 = (float13 - float8) * float7;
		float float24 = (float19 - float14) * float7;
		float float25 = (float3 - float4) * float7;
		float float26 = (float9 - float11) * float7;
		float float27 = (float15 - float17) * float7;
		float float28 = (float1 - float2) * float7;
		float float29 = (float8 * this.m31 - float9 * this.m32 + float11 * this.m32 - float10 * this.m30 + float12 * this.m30 - float13 * this.m31) * float7;
		float float30 = (float14 * this.m31 - float15 * this.m32 + float17 * this.m32 - float16 * this.m30 + float18 * this.m30 - float19 * this.m31) * float7;
		float float31 = (float6 * this.m30 - float5 * this.m30 + float4 * this.m31 - float3 * this.m31 + float2 * this.m32 - float1 * this.m32) * float7;
		matrix4x3f.m00 = float20;
		matrix4x3f.m01 = float21;
		matrix4x3f.m02 = float22;
		matrix4x3f.m10 = float23;
		matrix4x3f.m11 = float24;
		matrix4x3f.m12 = float25;
		matrix4x3f.m20 = float26;
		matrix4x3f.m21 = float27;
		matrix4x3f.m22 = float28;
		matrix4x3f.m30 = float29;
		matrix4x3f.m31 = float30;
		matrix4x3f.m32 = float31;
		matrix4x3f.properties = 0;
		return matrix4x3f;
	}

	private Matrix4x3f invertOrthonormal(Matrix4x3f matrix4x3f) {
		float float1 = -(this.m00 * this.m30 + this.m01 * this.m31 + this.m02 * this.m32);
		float float2 = -(this.m10 * this.m30 + this.m11 * this.m31 + this.m12 * this.m32);
		float float3 = -(this.m20 * this.m30 + this.m21 * this.m31 + this.m22 * this.m32);
		float float4 = this.m01;
		float float5 = this.m02;
		float float6 = this.m12;
		matrix4x3f.m00 = this.m00;
		matrix4x3f.m01 = this.m10;
		matrix4x3f.m02 = this.m20;
		matrix4x3f.m10 = float4;
		matrix4x3f.m11 = this.m11;
		matrix4x3f.m12 = this.m21;
		matrix4x3f.m20 = float5;
		matrix4x3f.m21 = float6;
		matrix4x3f.m22 = this.m22;
		matrix4x3f.m30 = float1;
		matrix4x3f.m31 = float2;
		matrix4x3f.m32 = float3;
		matrix4x3f.properties = 16;
		return matrix4x3f;
	}

	public Matrix4f invert(Matrix4f matrix4f) {
		if ((this.properties & 4) != 0) {
			return matrix4f.identity();
		} else {
			return (this.properties & 16) != 0 ? this.invertOrthonormal(matrix4f) : this.invertGeneric(matrix4f);
		}
	}

	private Matrix4f invertGeneric(Matrix4f matrix4f) {
		float float1 = this.m00 * this.m11;
		float float2 = this.m01 * this.m10;
		float float3 = this.m02 * this.m10;
		float float4 = this.m00 * this.m12;
		float float5 = this.m01 * this.m12;
		float float6 = this.m02 * this.m11;
		float float7 = 1.0F / ((float1 - float2) * this.m22 + (float3 - float4) * this.m21 + (float5 - float6) * this.m20);
		float float8 = this.m10 * this.m22;
		float float9 = this.m10 * this.m21;
		float float10 = this.m11 * this.m22;
		float float11 = this.m11 * this.m20;
		float float12 = this.m12 * this.m21;
		float float13 = this.m12 * this.m20;
		float float14 = this.m20 * this.m02;
		float float15 = this.m20 * this.m01;
		float float16 = this.m21 * this.m02;
		float float17 = this.m21 * this.m00;
		float float18 = this.m22 * this.m01;
		float float19 = this.m22 * this.m00;
		float float20 = (float10 - float12) * float7;
		float float21 = (float16 - float18) * float7;
		float float22 = (float5 - float6) * float7;
		float float23 = (float13 - float8) * float7;
		float float24 = (float19 - float14) * float7;
		float float25 = (float3 - float4) * float7;
		float float26 = (float9 - float11) * float7;
		float float27 = (float15 - float17) * float7;
		float float28 = (float1 - float2) * float7;
		float float29 = (float8 * this.m31 - float9 * this.m32 + float11 * this.m32 - float10 * this.m30 + float12 * this.m30 - float13 * this.m31) * float7;
		float float30 = (float14 * this.m31 - float15 * this.m32 + float17 * this.m32 - float16 * this.m30 + float18 * this.m30 - float19 * this.m31) * float7;
		float float31 = (float6 * this.m30 - float5 * this.m30 + float4 * this.m31 - float3 * this.m31 + float2 * this.m32 - float1 * this.m32) * float7;
		matrix4f.m00 = float20;
		matrix4f.m01 = float21;
		matrix4f.m02 = float22;
		matrix4f.m03 = 0.0F;
		matrix4f.m10 = float23;
		matrix4f.m11 = float24;
		matrix4f.m12 = float25;
		matrix4f.m13 = 0.0F;
		matrix4f.m20 = float26;
		matrix4f.m21 = float27;
		matrix4f.m22 = float28;
		matrix4f.m23 = 0.0F;
		matrix4f.m30 = float29;
		matrix4f.m31 = float30;
		matrix4f.m32 = float31;
		matrix4f.m33 = 0.0F;
		matrix4f.properties = 0;
		return matrix4f;
	}

	private Matrix4f invertOrthonormal(Matrix4f matrix4f) {
		float float1 = -(this.m00 * this.m30 + this.m01 * this.m31 + this.m02 * this.m32);
		float float2 = -(this.m10 * this.m30 + this.m11 * this.m31 + this.m12 * this.m32);
		float float3 = -(this.m20 * this.m30 + this.m21 * this.m31 + this.m22 * this.m32);
		float float4 = this.m01;
		float float5 = this.m02;
		float float6 = this.m12;
		matrix4f.m00 = this.m00;
		matrix4f.m01 = this.m10;
		matrix4f.m02 = this.m20;
		matrix4f.m03 = 0.0F;
		matrix4f.m10 = float4;
		matrix4f.m11 = this.m11;
		matrix4f.m12 = this.m21;
		matrix4f.m13 = 0.0F;
		matrix4f.m20 = float5;
		matrix4f.m21 = float6;
		matrix4f.m22 = this.m22;
		matrix4f.m23 = 0.0F;
		matrix4f.m30 = float1;
		matrix4f.m31 = float2;
		matrix4f.m32 = float3;
		matrix4f.m33 = 0.0F;
		matrix4f.properties = 16;
		return matrix4f;
	}

	public Matrix4x3f invert() {
		return this.invert(this);
	}

	public Matrix4x3f invertOrtho(Matrix4x3f matrix4x3f) {
		float float1 = 1.0F / this.m00;
		float float2 = 1.0F / this.m11;
		float float3 = 1.0F / this.m22;
		matrix4x3f.set(float1, 0.0F, 0.0F, 0.0F, float2, 0.0F, 0.0F, 0.0F, float3, -this.m30 * float1, -this.m31 * float2, -this.m32 * float3);
		matrix4x3f.properties = 0;
		return matrix4x3f;
	}

	public Matrix4x3f invertOrtho() {
		return this.invertOrtho(this);
	}

	public Matrix4x3f transpose3x3() {
		return this.transpose3x3(this);
	}

	public Matrix4x3f transpose3x3(Matrix4x3f matrix4x3f) {
		float float1 = this.m00;
		float float2 = this.m10;
		float float3 = this.m20;
		float float4 = this.m01;
		float float5 = this.m11;
		float float6 = this.m21;
		float float7 = this.m02;
		float float8 = this.m12;
		float float9 = this.m22;
		matrix4x3f.m00 = float1;
		matrix4x3f.m01 = float2;
		matrix4x3f.m02 = float3;
		matrix4x3f.m10 = float4;
		matrix4x3f.m11 = float5;
		matrix4x3f.m12 = float6;
		matrix4x3f.m20 = float7;
		matrix4x3f.m21 = float8;
		matrix4x3f.m22 = float9;
		matrix4x3f.properties = this.properties;
		return matrix4x3f;
	}

	public Matrix3f transpose3x3(Matrix3f matrix3f) {
		matrix3f.m00(this.m00);
		matrix3f.m01(this.m10);
		matrix3f.m02(this.m20);
		matrix3f.m10(this.m01);
		matrix3f.m11(this.m11);
		matrix3f.m12(this.m21);
		matrix3f.m20(this.m02);
		matrix3f.m21(this.m12);
		matrix3f.m22(this.m22);
		return matrix3f;
	}

	public Matrix4x3f translation(float float1, float float2, float float3) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this.m30 = float1;
		this.m31 = float2;
		this.m32 = float3;
		this.properties = 24;
		return this;
	}

	public Matrix4x3f translation(Vector3fc vector3fc) {
		return this.translation(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4x3f setTranslation(float float1, float float2, float float3) {
		this.m30 = float1;
		this.m31 = float2;
		this.m32 = float3;
		this.properties &= -5;
		return this;
	}

	public Matrix4x3f setTranslation(Vector3fc vector3fc) {
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
		return string + " " + Runtime.format((double)this.m10, numberFormat) + " " + Runtime.format((double)this.m20, numberFormat) + " " + Runtime.format((double)this.m30, numberFormat) + "\n" + Runtime.format((double)this.m01, numberFormat) + " " + Runtime.format((double)this.m11, numberFormat) + " " + Runtime.format((double)this.m21, numberFormat) + " " + Runtime.format((double)this.m31, numberFormat) + "\n" + Runtime.format((double)this.m02, numberFormat) + " " + Runtime.format((double)this.m12, numberFormat) + " " + Runtime.format((double)this.m22, numberFormat) + " " + Runtime.format((double)this.m32, numberFormat) + "\n";
	}

	public Matrix4x3f get(Matrix4x3f matrix4x3f) {
		return matrix4x3f.set((Matrix4x3fc)this);
	}

	public Matrix4x3d get(Matrix4x3d matrix4x3d) {
		return matrix4x3d.set((Matrix4x3fc)this);
	}

	public AxisAngle4f getRotation(AxisAngle4f axisAngle4f) {
		return axisAngle4f.set((Matrix4x3fc)this);
	}

	public AxisAngle4d getRotation(AxisAngle4d axisAngle4d) {
		return axisAngle4d.set((Matrix4x3fc)this);
	}

	public Quaternionf getUnnormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromUnnormalized((Matrix4x3fc)this);
	}

	public Quaternionf getNormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromNormalized((Matrix4x3fc)this);
	}

	public Quaterniond getUnnormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromUnnormalized((Matrix4x3fc)this);
	}

	public Quaterniond getNormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromNormalized((Matrix4x3fc)this);
	}

	public FloatBuffer get(FloatBuffer floatBuffer) {
		return this.get(floatBuffer.position(), floatBuffer);
	}

	public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		return this.get(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Matrix4x3fc getToAddress(long long1) {
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
		return this.get(floatArray, 0);
	}

	public float[] get4x4(float[] floatArray, int int1) {
		MemUtil.INSTANCE.copy4x4(this, floatArray, int1);
		return floatArray;
	}

	public float[] get4x4(float[] floatArray) {
		return this.get4x4(floatArray, 0);
	}

	public FloatBuffer get4x4(FloatBuffer floatBuffer) {
		return this.get4x4(floatBuffer.position(), floatBuffer);
	}

	public FloatBuffer get4x4(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put4x4(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get4x4(ByteBuffer byteBuffer) {
		return this.get4x4(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get4x4(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x4(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer get3x4(FloatBuffer floatBuffer) {
		return this.get3x4(floatBuffer.position(), floatBuffer);
	}

	public FloatBuffer get3x4(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put3x4(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get3x4(ByteBuffer byteBuffer) {
		return this.get3x4(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get3x4(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put3x4(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer getTransposed(FloatBuffer floatBuffer) {
		return this.getTransposed(floatBuffer.position(), floatBuffer);
	}

	public FloatBuffer getTransposed(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer getTransposed(ByteBuffer byteBuffer) {
		return this.getTransposed(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, byteBuffer);
		return byteBuffer;
	}

	public float[] getTransposed(float[] floatArray, int int1) {
		floatArray[int1 + 0] = this.m00;
		floatArray[int1 + 1] = this.m10;
		floatArray[int1 + 2] = this.m20;
		floatArray[int1 + 3] = this.m30;
		floatArray[int1 + 4] = this.m01;
		floatArray[int1 + 5] = this.m11;
		floatArray[int1 + 6] = this.m21;
		floatArray[int1 + 7] = this.m31;
		floatArray[int1 + 8] = this.m02;
		floatArray[int1 + 9] = this.m12;
		floatArray[int1 + 10] = this.m22;
		floatArray[int1 + 11] = this.m32;
		return floatArray;
	}

	public float[] getTransposed(float[] floatArray) {
		return this.getTransposed(floatArray, 0);
	}

	public Matrix4x3f zero() {
		MemUtil.INSTANCE.zero(this);
		this.properties = 0;
		return this;
	}

	public Matrix4x3f scaling(float float1) {
		return this.scaling(float1, float1, float1);
	}

	public Matrix4x3f scaling(float float1, float float2, float float3) {
		if ((this.properties & 4) == 0) {
			MemUtil.INSTANCE.identity(this);
		}

		this.m00 = float1;
		this.m11 = float2;
		this.m22 = float3;
		boolean boolean1 = Math.absEqualsOne(float1) && Math.absEqualsOne(float2) && Math.absEqualsOne(float3);
		this.properties = boolean1 ? 16 : 0;
		return this;
	}

	public Matrix4x3f scaling(Vector3fc vector3fc) {
		return this.scaling(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4x3f rotation(float float1, Vector3fc vector3fc) {
		return this.rotation(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4x3f rotation(AxisAngle4f axisAngle4f) {
		return this.rotation(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z);
	}

	public Matrix4x3f rotation(float float1, float float2, float float3, float float4) {
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return this.rotationX(float2 * float1);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return this.rotationY(float3 * float1);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? this.rotationZ(float4 * float1) : this.rotationInternal(float1, float2, float3, float4);
		}
	}

	private Matrix4x3f rotationInternal(float float1, float float2, float float3, float float4) {
		float float5 = Math.sin(float1);
		float float6 = Math.cosFromSin(float5, float1);
		float float7 = 1.0F - float6;
		float float8 = float2 * float3;
		float float9 = float2 * float4;
		float float10 = float3 * float4;
		this.m00 = float6 + float2 * float2 * float7;
		this.m01 = float8 * float7 + float4 * float5;
		this.m02 = float9 * float7 - float3 * float5;
		this.m10 = float8 * float7 - float4 * float5;
		this.m11 = float6 + float3 * float3 * float7;
		this.m12 = float10 * float7 + float2 * float5;
		this.m20 = float9 * float7 + float3 * float5;
		this.m21 = float10 * float7 - float2 * float5;
		this.m22 = float6 + float4 * float4 * float7;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f rotationX(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = float3;
		this.m12 = float2;
		this.m20 = 0.0F;
		this.m21 = -float2;
		this.m22 = float3;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f rotationY(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		this.m00 = float3;
		this.m01 = 0.0F;
		this.m02 = -float2;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m20 = float2;
		this.m21 = 0.0F;
		this.m22 = float3;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f rotationZ(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		this.m00 = float3;
		this.m01 = float2;
		this.m02 = 0.0F;
		this.m10 = -float2;
		this.m11 = float3;
		this.m12 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f rotationXYZ(float float1, float float2, float float3) {
		float float4 = Math.sin(float1);
		float float5 = Math.cosFromSin(float4, float1);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		float float10 = -float4;
		float float11 = -float6;
		float float12 = -float8;
		float float13 = float10 * float11;
		float float14 = float5 * float11;
		this.m20 = float6;
		this.m21 = float10 * float7;
		this.m22 = float5 * float7;
		this.m00 = float7 * float9;
		this.m01 = float13 * float9 + float5 * float8;
		this.m02 = float14 * float9 + float4 * float8;
		this.m10 = float7 * float12;
		this.m11 = float13 * float12 + float5 * float9;
		this.m12 = float14 * float12 + float4 * float9;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f rotationZYX(float float1, float float2, float float3) {
		float float4 = Math.sin(float3);
		float float5 = Math.cosFromSin(float4, float3);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float1);
		float float9 = Math.cosFromSin(float8, float1);
		float float10 = -float8;
		float float11 = -float6;
		float float12 = -float4;
		float float13 = float9 * float6;
		float float14 = float8 * float6;
		this.m00 = float9 * float7;
		this.m01 = float8 * float7;
		this.m02 = float11;
		this.m10 = float10 * float5 + float13 * float4;
		this.m11 = float9 * float5 + float14 * float4;
		this.m12 = float7 * float4;
		this.m20 = float10 * float12 + float13 * float5;
		this.m21 = float9 * float12 + float14 * float5;
		this.m22 = float7 * float5;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f rotationYXZ(float float1, float float2, float float3) {
		float float4 = Math.sin(float2);
		float float5 = Math.cosFromSin(float4, float2);
		float float6 = Math.sin(float1);
		float float7 = Math.cosFromSin(float6, float1);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		float float10 = -float6;
		float float11 = -float4;
		float float12 = -float8;
		float float13 = float6 * float4;
		float float14 = float7 * float4;
		this.m20 = float6 * float5;
		this.m21 = float11;
		this.m22 = float7 * float5;
		this.m00 = float7 * float9 + float13 * float8;
		this.m01 = float5 * float8;
		this.m02 = float10 * float9 + float14 * float8;
		this.m10 = float7 * float12 + float13 * float9;
		this.m11 = float5 * float9;
		this.m12 = float10 * float12 + float14 * float9;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f setRotationXYZ(float float1, float float2, float float3) {
		float float4 = Math.sin(float1);
		float float5 = Math.cosFromSin(float4, float1);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		float float10 = -float4;
		float float11 = -float6;
		float float12 = -float8;
		float float13 = float10 * float11;
		float float14 = float5 * float11;
		this.m20 = float6;
		this.m21 = float10 * float7;
		this.m22 = float5 * float7;
		this.m00 = float7 * float9;
		this.m01 = float13 * float9 + float5 * float8;
		this.m02 = float14 * float9 + float4 * float8;
		this.m10 = float7 * float12;
		this.m11 = float13 * float12 + float5 * float9;
		this.m12 = float14 * float12 + float4 * float9;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3f setRotationZYX(float float1, float float2, float float3) {
		float float4 = Math.sin(float3);
		float float5 = Math.cosFromSin(float4, float3);
		float float6 = Math.sin(float2);
		float float7 = Math.cosFromSin(float6, float2);
		float float8 = Math.sin(float1);
		float float9 = Math.cosFromSin(float8, float1);
		float float10 = -float8;
		float float11 = -float6;
		float float12 = -float4;
		float float13 = float9 * float6;
		float float14 = float8 * float6;
		this.m00 = float9 * float7;
		this.m01 = float8 * float7;
		this.m02 = float11;
		this.m10 = float10 * float5 + float13 * float4;
		this.m11 = float9 * float5 + float14 * float4;
		this.m12 = float7 * float4;
		this.m20 = float10 * float12 + float13 * float5;
		this.m21 = float9 * float12 + float14 * float5;
		this.m22 = float7 * float5;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3f setRotationYXZ(float float1, float float2, float float3) {
		float float4 = Math.sin(float2);
		float float5 = Math.cosFromSin(float4, float2);
		float float6 = Math.sin(float1);
		float float7 = Math.cosFromSin(float6, float1);
		float float8 = Math.sin(float3);
		float float9 = Math.cosFromSin(float8, float3);
		float float10 = -float6;
		float float11 = -float4;
		float float12 = -float8;
		float float13 = float6 * float4;
		float float14 = float7 * float4;
		this.m20 = float6 * float5;
		this.m21 = float11;
		this.m22 = float7 * float5;
		this.m00 = float7 * float9 + float13 * float8;
		this.m01 = float5 * float8;
		this.m02 = float10 * float9 + float14 * float8;
		this.m10 = float7 * float12 + float13 * float9;
		this.m11 = float5 * float9;
		this.m12 = float10 * float12 + float14 * float9;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3f rotation(Quaternionfc quaternionfc) {
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
		this._m00(float1 + float2 - float4 - float3);
		this._m01(float8 + float6);
		this._m02(float10 - float12);
		this._m10(float8 - float6);
		this._m11(float3 - float4 + float1 - float2);
		this._m12(float14 + float16);
		this._m20(float12 + float10);
		this._m21(float14 - float16);
		this._m22(float4 - float3 - float2 + float1);
		this._m30(0.0F);
		this._m31(0.0F);
		this._m32(0.0F);
		this.properties = 16;
		return this;
	}

	public Matrix4x3f translationRotateScale(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
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
		this.m00 = float8 - (float15 + float16) * float8;
		this.m01 = (float17 + float22) * float8;
		this.m02 = (float18 - float21) * float8;
		this.m10 = (float17 - float22) * float9;
		this.m11 = float9 - (float16 + float14) * float9;
		this.m12 = (float20 + float19) * float9;
		this.m20 = (float18 + float21) * float10;
		this.m21 = (float20 - float19) * float10;
		this.m22 = float10 - (float15 + float14) * float10;
		this.m30 = float1;
		this.m31 = float2;
		this.m32 = float3;
		this.properties = 0;
		return this;
	}

	public Matrix4x3f translationRotateScale(Vector3fc vector3fc, Quaternionfc quaternionfc, Vector3fc vector3fc2) {
		return this.translationRotateScale(vector3fc.x(), vector3fc.y(), vector3fc.z(), quaternionfc.x(), quaternionfc.y(), quaternionfc.z(), quaternionfc.w(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix4x3f translationRotateScaleMul(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, Matrix4x3f matrix4x3f) {
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
		float float23 = float8 - (float15 + float16) * float8;
		float float24 = (float17 + float22) * float8;
		float float25 = (float18 - float21) * float8;
		float float26 = (float17 - float22) * float9;
		float float27 = float9 - (float16 + float14) * float9;
		float float28 = (float20 + float19) * float9;
		float float29 = (float18 + float21) * float10;
		float float30 = (float20 - float19) * float10;
		float float31 = float10 - (float15 + float14) * float10;
		float float32 = float23 * matrix4x3f.m00 + float26 * matrix4x3f.m01 + float29 * matrix4x3f.m02;
		float float33 = float24 * matrix4x3f.m00 + float27 * matrix4x3f.m01 + float30 * matrix4x3f.m02;
		this.m02 = float25 * matrix4x3f.m00 + float28 * matrix4x3f.m01 + float31 * matrix4x3f.m02;
		this.m00 = float32;
		this.m01 = float33;
		float float34 = float23 * matrix4x3f.m10 + float26 * matrix4x3f.m11 + float29 * matrix4x3f.m12;
		float float35 = float24 * matrix4x3f.m10 + float27 * matrix4x3f.m11 + float30 * matrix4x3f.m12;
		this.m12 = float25 * matrix4x3f.m10 + float28 * matrix4x3f.m11 + float31 * matrix4x3f.m12;
		this.m10 = float34;
		this.m11 = float35;
		float float36 = float23 * matrix4x3f.m20 + float26 * matrix4x3f.m21 + float29 * matrix4x3f.m22;
		float float37 = float24 * matrix4x3f.m20 + float27 * matrix4x3f.m21 + float30 * matrix4x3f.m22;
		this.m22 = float25 * matrix4x3f.m20 + float28 * matrix4x3f.m21 + float31 * matrix4x3f.m22;
		this.m20 = float36;
		this.m21 = float37;
		float float38 = float23 * matrix4x3f.m30 + float26 * matrix4x3f.m31 + float29 * matrix4x3f.m32 + float1;
		float float39 = float24 * matrix4x3f.m30 + float27 * matrix4x3f.m31 + float30 * matrix4x3f.m32 + float2;
		this.m32 = float25 * matrix4x3f.m30 + float28 * matrix4x3f.m31 + float31 * matrix4x3f.m32 + float3;
		this.m30 = float38;
		this.m31 = float39;
		this.properties = 0;
		return this;
	}

	public Matrix4x3f translationRotateScaleMul(Vector3fc vector3fc, Quaternionfc quaternionfc, Vector3fc vector3fc2, Matrix4x3f matrix4x3f) {
		return this.translationRotateScaleMul(vector3fc.x(), vector3fc.y(), vector3fc.z(), quaternionfc.x(), quaternionfc.y(), quaternionfc.z(), quaternionfc.w(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), matrix4x3f);
	}

	public Matrix4x3f translationRotate(float float1, float float2, float float3, Quaternionfc quaternionfc) {
		float float4 = quaternionfc.x() + quaternionfc.x();
		float float5 = quaternionfc.y() + quaternionfc.y();
		float float6 = quaternionfc.z() + quaternionfc.z();
		float float7 = float4 * quaternionfc.x();
		float float8 = float5 * quaternionfc.y();
		float float9 = float6 * quaternionfc.z();
		float float10 = float4 * quaternionfc.y();
		float float11 = float4 * quaternionfc.z();
		float float12 = float4 * quaternionfc.w();
		float float13 = float5 * quaternionfc.z();
		float float14 = float5 * quaternionfc.w();
		float float15 = float6 * quaternionfc.w();
		this.m00 = 1.0F - (float8 + float9);
		this.m01 = float10 + float15;
		this.m02 = float11 - float14;
		this.m10 = float10 - float15;
		this.m11 = 1.0F - (float9 + float7);
		this.m12 = float13 + float12;
		this.m20 = float11 + float14;
		this.m21 = float13 - float12;
		this.m22 = 1.0F - (float8 + float7);
		this.m30 = float1;
		this.m31 = float2;
		this.m32 = float3;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f translationRotateMul(float float1, float float2, float float3, Quaternionfc quaternionfc, Matrix4x3fc matrix4x3fc) {
		return this.translationRotateMul(float1, float2, float3, quaternionfc.x(), quaternionfc.y(), quaternionfc.z(), quaternionfc.w(), matrix4x3fc);
	}

	public Matrix4x3f translationRotateMul(float float1, float float2, float float3, float float4, float float5, float float6, float float7, Matrix4x3fc matrix4x3fc) {
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
		float float18 = float8 + float9 - float11 - float10;
		float float19 = float13 + float12 + float12 + float13;
		float float20 = float14 - float15 + float14 - float15;
		float float21 = -float12 + float13 - float12 + float13;
		float float22 = float10 - float11 + float8 - float9;
		float float23 = float16 + float16 + float17 + float17;
		float float24 = float15 + float14 + float14 + float15;
		float float25 = float16 + float16 - float17 - float17;
		float float26 = float11 - float10 - float9 + float8;
		this.m00 = float18 * matrix4x3fc.m00() + float21 * matrix4x3fc.m01() + float24 * matrix4x3fc.m02();
		this.m01 = float19 * matrix4x3fc.m00() + float22 * matrix4x3fc.m01() + float25 * matrix4x3fc.m02();
		this.m02 = float20 * matrix4x3fc.m00() + float23 * matrix4x3fc.m01() + float26 * matrix4x3fc.m02();
		this.m10 = float18 * matrix4x3fc.m10() + float21 * matrix4x3fc.m11() + float24 * matrix4x3fc.m12();
		this.m11 = float19 * matrix4x3fc.m10() + float22 * matrix4x3fc.m11() + float25 * matrix4x3fc.m12();
		this.m12 = float20 * matrix4x3fc.m10() + float23 * matrix4x3fc.m11() + float26 * matrix4x3fc.m12();
		this.m20 = float18 * matrix4x3fc.m20() + float21 * matrix4x3fc.m21() + float24 * matrix4x3fc.m22();
		this.m21 = float19 * matrix4x3fc.m20() + float22 * matrix4x3fc.m21() + float25 * matrix4x3fc.m22();
		this.m22 = float20 * matrix4x3fc.m20() + float23 * matrix4x3fc.m21() + float26 * matrix4x3fc.m22();
		this.m30 = float18 * matrix4x3fc.m30() + float21 * matrix4x3fc.m31() + float24 * matrix4x3fc.m32() + float1;
		this.m31 = float19 * matrix4x3fc.m30() + float22 * matrix4x3fc.m31() + float25 * matrix4x3fc.m32() + float2;
		this.m32 = float20 * matrix4x3fc.m30() + float23 * matrix4x3fc.m31() + float26 * matrix4x3fc.m32() + float3;
		this.properties = 0;
		return this;
	}

	public Matrix4x3f set3x3(Matrix3fc matrix3fc) {
		if (matrix3fc instanceof Matrix3f) {
			MemUtil.INSTANCE.copy3x3((Matrix3f)matrix3fc, this);
		} else {
			this.set3x3Matrix3fc(matrix3fc);
		}

		this.properties = 0;
		return this;
	}

	private void set3x3Matrix3fc(Matrix3fc matrix3fc) {
		this.m00 = matrix3fc.m00();
		this.m01 = matrix3fc.m01();
		this.m02 = matrix3fc.m02();
		this.m10 = matrix3fc.m10();
		this.m11 = matrix3fc.m11();
		this.m12 = matrix3fc.m12();
		this.m20 = matrix3fc.m20();
		this.m21 = matrix3fc.m21();
		this.m22 = matrix3fc.m22();
	}

	public Vector4f transform(Vector4f vector4f) {
		return vector4f.mul((Matrix4x3fc)this);
	}

	public Vector4f transform(Vector4fc vector4fc, Vector4f vector4f) {
		return vector4fc.mul((Matrix4x3fc)this, vector4f);
	}

	public Vector3f transformPosition(Vector3f vector3f) {
		vector3f.set(this.m00 * vector3f.x + this.m10 * vector3f.y + this.m20 * vector3f.z + this.m30, this.m01 * vector3f.x + this.m11 * vector3f.y + this.m21 * vector3f.z + this.m31, this.m02 * vector3f.x + this.m12 * vector3f.y + this.m22 * vector3f.z + this.m32);
		return vector3f;
	}

	public Vector3f transformPosition(Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.set(this.m00 * vector3fc.x() + this.m10 * vector3fc.y() + this.m20 * vector3fc.z() + this.m30, this.m01 * vector3fc.x() + this.m11 * vector3fc.y() + this.m21 * vector3fc.z() + this.m31, this.m02 * vector3fc.x() + this.m12 * vector3fc.y() + this.m22 * vector3fc.z() + this.m32);
		return vector3f;
	}

	public Vector3f transformDirection(Vector3f vector3f) {
		vector3f.set(this.m00 * vector3f.x + this.m10 * vector3f.y + this.m20 * vector3f.z, this.m01 * vector3f.x + this.m11 * vector3f.y + this.m21 * vector3f.z, this.m02 * vector3f.x + this.m12 * vector3f.y + this.m22 * vector3f.z);
		return vector3f;
	}

	public Vector3f transformDirection(Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.set(this.m00 * vector3fc.x() + this.m10 * vector3fc.y() + this.m20 * vector3fc.z(), this.m01 * vector3fc.x() + this.m11 * vector3fc.y() + this.m21 * vector3fc.z(), this.m02 * vector3fc.x() + this.m12 * vector3fc.y() + this.m22 * vector3fc.z());
		return vector3f;
	}

	public Matrix4x3f scale(Vector3fc vector3fc, Matrix4x3f matrix4x3f) {
		return this.scale(vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix4x3f);
	}

	public Matrix4x3f scale(Vector3fc vector3fc) {
		return this.scale(vector3fc.x(), vector3fc.y(), vector3fc.z(), this);
	}

	public Matrix4x3f scale(float float1, Matrix4x3f matrix4x3f) {
		return this.scale(float1, float1, float1, matrix4x3f);
	}

	public Matrix4x3f scale(float float1) {
		return this.scale(float1, float1, float1);
	}

	public Matrix4x3f scaleXY(float float1, float float2, Matrix4x3f matrix4x3f) {
		return this.scale(float1, float2, 1.0F, matrix4x3f);
	}

	public Matrix4x3f scaleXY(float float1, float float2) {
		return this.scale(float1, float2, 1.0F);
	}

	public Matrix4x3f scale(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
		return (this.properties & 4) != 0 ? matrix4x3f.scaling(float1, float2, float3) : this.scaleGeneric(float1, float2, float3, matrix4x3f);
	}

	private Matrix4x3f scaleGeneric(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
		matrix4x3f.m00 = this.m00 * float1;
		matrix4x3f.m01 = this.m01 * float1;
		matrix4x3f.m02 = this.m02 * float1;
		matrix4x3f.m10 = this.m10 * float2;
		matrix4x3f.m11 = this.m11 * float2;
		matrix4x3f.m12 = this.m12 * float2;
		matrix4x3f.m20 = this.m20 * float3;
		matrix4x3f.m21 = this.m21 * float3;
		matrix4x3f.m22 = this.m22 * float3;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = this.properties & -29;
		return matrix4x3f;
	}

	public Matrix4x3f scale(float float1, float float2, float float3) {
		return this.scale(float1, float2, float3, this);
	}

	public Matrix4x3f scaleLocal(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.scaling(float1, float2, float3);
		} else {
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
			matrix4x3f.m00 = float4;
			matrix4x3f.m01 = float5;
			matrix4x3f.m02 = float6;
			matrix4x3f.m10 = float7;
			matrix4x3f.m11 = float8;
			matrix4x3f.m12 = float9;
			matrix4x3f.m20 = float10;
			matrix4x3f.m21 = float11;
			matrix4x3f.m22 = float12;
			matrix4x3f.m30 = float13;
			matrix4x3f.m31 = float14;
			matrix4x3f.m32 = float15;
			matrix4x3f.properties = this.properties & -29;
			return matrix4x3f;
		}
	}

	public Matrix4x3f scaleLocal(float float1, float float2, float float3) {
		return this.scaleLocal(float1, float2, float3, this);
	}

	public Matrix4x3f rotateX(float float1, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.rotationX(float1);
		} else if ((this.properties & 8) != 0) {
			float float2 = this.m30;
			float float3 = this.m31;
			float float4 = this.m32;
			return matrix4x3f.rotationX(float1).setTranslation(float2, float3, float4);
		} else {
			return this.rotateXInternal(float1, matrix4x3f);
		}
	}

	private Matrix4x3f rotateXInternal(float float1, Matrix4x3f matrix4x3f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = -float2;
		float float5 = this.m10 * float3 + this.m20 * float2;
		float float6 = this.m11 * float3 + this.m21 * float2;
		float float7 = this.m12 * float3 + this.m22 * float2;
		matrix4x3f.m20 = this.m10 * float4 + this.m20 * float3;
		matrix4x3f.m21 = this.m11 * float4 + this.m21 * float3;
		matrix4x3f.m22 = this.m12 * float4 + this.m22 * float3;
		matrix4x3f.m10 = float5;
		matrix4x3f.m11 = float6;
		matrix4x3f.m12 = float7;
		matrix4x3f.m00 = this.m00;
		matrix4x3f.m01 = this.m01;
		matrix4x3f.m02 = this.m02;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotateX(float float1) {
		return this.rotateX(float1, this);
	}

	public Matrix4x3f rotateY(float float1, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.rotationY(float1);
		} else if ((this.properties & 8) != 0) {
			float float2 = this.m30;
			float float3 = this.m31;
			float float4 = this.m32;
			return matrix4x3f.rotationY(float1).setTranslation(float2, float3, float4);
		} else {
			return this.rotateYInternal(float1, matrix4x3f);
		}
	}

	private Matrix4x3f rotateYInternal(float float1, Matrix4x3f matrix4x3f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = -float2;
		float float5 = this.m00 * float3 + this.m20 * float4;
		float float6 = this.m01 * float3 + this.m21 * float4;
		float float7 = this.m02 * float3 + this.m22 * float4;
		matrix4x3f.m20 = this.m00 * float2 + this.m20 * float3;
		matrix4x3f.m21 = this.m01 * float2 + this.m21 * float3;
		matrix4x3f.m22 = this.m02 * float2 + this.m22 * float3;
		matrix4x3f.m00 = float5;
		matrix4x3f.m01 = float6;
		matrix4x3f.m02 = float7;
		matrix4x3f.m10 = this.m10;
		matrix4x3f.m11 = this.m11;
		matrix4x3f.m12 = this.m12;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotateY(float float1) {
		return this.rotateY(float1, this);
	}

	public Matrix4x3f rotateZ(float float1, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.rotationZ(float1);
		} else if ((this.properties & 8) != 0) {
			float float2 = this.m30;
			float float3 = this.m31;
			float float4 = this.m32;
			return matrix4x3f.rotationZ(float1).setTranslation(float2, float3, float4);
		} else {
			return this.rotateZInternal(float1, matrix4x3f);
		}
	}

	private Matrix4x3f rotateZInternal(float float1, Matrix4x3f matrix4x3f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = -float2;
		float float5 = this.m00 * float3 + this.m10 * float2;
		float float6 = this.m01 * float3 + this.m11 * float2;
		float float7 = this.m02 * float3 + this.m12 * float2;
		matrix4x3f.m10 = this.m00 * float4 + this.m10 * float3;
		matrix4x3f.m11 = this.m01 * float4 + this.m11 * float3;
		matrix4x3f.m12 = this.m02 * float4 + this.m12 * float3;
		matrix4x3f.m00 = float5;
		matrix4x3f.m01 = float6;
		matrix4x3f.m02 = float7;
		matrix4x3f.m20 = this.m20;
		matrix4x3f.m21 = this.m21;
		matrix4x3f.m22 = this.m22;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotateZ(float float1) {
		return this.rotateZ(float1, this);
	}

	public Matrix4x3f rotateXYZ(Vector3f vector3f) {
		return this.rotateXYZ(vector3f.x, vector3f.y, vector3f.z);
	}

	public Matrix4x3f rotateXYZ(float float1, float float2, float float3) {
		return this.rotateXYZ(float1, float2, float3, this);
	}

	public Matrix4x3f rotateXYZ(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.rotationXYZ(float1, float2, float3);
		} else if ((this.properties & 8) != 0) {
			float float4 = this.m30;
			float float5 = this.m31;
			float float6 = this.m32;
			return matrix4x3f.rotationXYZ(float1, float2, float3).setTranslation(float4, float5, float6);
		} else {
			return this.rotateXYZInternal(float1, float2, float3, matrix4x3f);
		}
	}

	private Matrix4x3f rotateXYZInternal(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
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
		matrix4x3f.m20 = this.m00 * float6 + float16 * float7;
		matrix4x3f.m21 = this.m01 * float6 + float17 * float7;
		matrix4x3f.m22 = this.m02 * float6 + float18 * float7;
		matrix4x3f.m00 = float19 * float9 + float13 * float8;
		matrix4x3f.m01 = float20 * float9 + float14 * float8;
		matrix4x3f.m02 = float21 * float9 + float15 * float8;
		matrix4x3f.m10 = float19 * float12 + float13 * float9;
		matrix4x3f.m11 = float20 * float12 + float14 * float9;
		matrix4x3f.m12 = float21 * float12 + float15 * float9;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotateZYX(Vector3f vector3f) {
		return this.rotateZYX(vector3f.z, vector3f.y, vector3f.x);
	}

	public Matrix4x3f rotateZYX(float float1, float float2, float float3) {
		return this.rotateZYX(float1, float2, float3, this);
	}

	public Matrix4x3f rotateZYX(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.rotationZYX(float1, float2, float3);
		} else if ((this.properties & 8) != 0) {
			float float4 = this.m30;
			float float5 = this.m31;
			float float6 = this.m32;
			return matrix4x3f.rotationZYX(float1, float2, float3).setTranslation(float4, float5, float6);
		} else {
			return this.rotateZYXInternal(float1, float2, float3, matrix4x3f);
		}
	}

	private Matrix4x3f rotateZYXInternal(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
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
		matrix4x3f.m00 = float13 * float7 + this.m20 * float11;
		matrix4x3f.m01 = float14 * float7 + this.m21 * float11;
		matrix4x3f.m02 = float15 * float7 + this.m22 * float11;
		matrix4x3f.m10 = float16 * float5 + float19 * float4;
		matrix4x3f.m11 = float17 * float5 + float20 * float4;
		matrix4x3f.m12 = float18 * float5 + float21 * float4;
		matrix4x3f.m20 = float16 * float12 + float19 * float5;
		matrix4x3f.m21 = float17 * float12 + float20 * float5;
		matrix4x3f.m22 = float18 * float12 + float21 * float5;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotateYXZ(Vector3f vector3f) {
		return this.rotateYXZ(vector3f.y, vector3f.x, vector3f.z);
	}

	public Matrix4x3f rotateYXZ(float float1, float float2, float float3) {
		return this.rotateYXZ(float1, float2, float3, this);
	}

	public Matrix4x3f rotateYXZ(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.rotationYXZ(float1, float2, float3);
		} else if ((this.properties & 8) != 0) {
			float float4 = this.m30;
			float float5 = this.m31;
			float float6 = this.m32;
			return matrix4x3f.rotationYXZ(float1, float2, float3).setTranslation(float4, float5, float6);
		} else {
			return this.rotateYXZInternal(float1, float2, float3, matrix4x3f);
		}
	}

	private Matrix4x3f rotateYXZInternal(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
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
		matrix4x3f.m20 = this.m10 * float11 + float13 * float5;
		matrix4x3f.m21 = this.m11 * float11 + float14 * float5;
		matrix4x3f.m22 = this.m12 * float11 + float15 * float5;
		matrix4x3f.m00 = float16 * float9 + float19 * float8;
		matrix4x3f.m01 = float17 * float9 + float20 * float8;
		matrix4x3f.m02 = float18 * float9 + float21 * float8;
		matrix4x3f.m10 = float16 * float12 + float19 * float9;
		matrix4x3f.m11 = float17 * float12 + float20 * float9;
		matrix4x3f.m12 = float18 * float12 + float21 * float9;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotate(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.rotation(float1, float2, float3, float4);
		} else {
			return (this.properties & 8) != 0 ? this.rotateTranslation(float1, float2, float3, float4, matrix4x3f) : this.rotateGeneric(float1, float2, float3, float4, matrix4x3f);
		}
	}

	private Matrix4x3f rotateGeneric(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return this.rotateX(float2 * float1, matrix4x3f);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return this.rotateY(float3 * float1, matrix4x3f);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? this.rotateZ(float4 * float1, matrix4x3f) : this.rotateGenericInternal(float1, float2, float3, float4, matrix4x3f);
		}
	}

	private Matrix4x3f rotateGenericInternal(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
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
		matrix4x3f.m20 = this.m00 * float20 + this.m10 * float21 + this.m20 * float22;
		matrix4x3f.m21 = this.m01 * float20 + this.m11 * float21 + this.m21 * float22;
		matrix4x3f.m22 = this.m02 * float20 + this.m12 * float21 + this.m22 * float22;
		matrix4x3f.m00 = float23;
		matrix4x3f.m01 = float24;
		matrix4x3f.m02 = float25;
		matrix4x3f.m10 = float26;
		matrix4x3f.m11 = float27;
		matrix4x3f.m12 = float28;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotate(float float1, float float2, float float3, float float4) {
		return this.rotate(float1, float2, float3, float4, this);
	}

	public Matrix4x3f rotateTranslation(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
		float float5 = this.m30;
		float float6 = this.m31;
		float float7 = this.m32;
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return matrix4x3f.rotationX(float2 * float1).setTranslation(float5, float6, float7);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return matrix4x3f.rotationY(float3 * float1).setTranslation(float5, float6, float7);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? matrix4x3f.rotationZ(float4 * float1).setTranslation(float5, float6, float7) : this.rotateTranslationInternal(float1, float2, float3, float4, matrix4x3f);
		}
	}

	private Matrix4x3f rotateTranslationInternal(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
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
		matrix4x3f.m20 = float20;
		matrix4x3f.m21 = float21;
		matrix4x3f.m22 = float22;
		matrix4x3f.m00 = float14;
		matrix4x3f.m01 = float15;
		matrix4x3f.m02 = float16;
		matrix4x3f.m10 = float17;
		matrix4x3f.m11 = float18;
		matrix4x3f.m12 = float19;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotateAround(Quaternionfc quaternionfc, float float1, float float2, float float3) {
		return this.rotateAround(quaternionfc, float1, float2, float3, this);
	}

	private Matrix4x3f rotateAroundAffine(Quaternionfc quaternionfc, float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
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
		float float23 = float11 - float9;
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
		matrix4x3f._m20(this.m00 * float26 + this.m10 * float27 + this.m20 * float28)._m21(this.m01 * float26 + this.m11 * float27 + this.m21 * float28)._m22(this.m02 * float26 + this.m12 * float27 + this.m22 * float28)._m00(float32)._m01(float33)._m02(float34)._m10(float35)._m11(float36)._m12(float37)._m30(-float32 * float1 - float35 * float2 - this.m20 * float3 + float29)._m31(-float33 * float1 - float36 * float2 - this.m21 * float3 + float30)._m32(-float34 * float1 - float37 * float2 - this.m22 * float3 + float31)._properties(this.properties & -13);
		return matrix4x3f;
	}

	public Matrix4x3f rotateAround(Quaternionfc quaternionfc, float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
		return (this.properties & 4) != 0 ? this.rotationAround(quaternionfc, float1, float2, float3) : this.rotateAroundAffine(quaternionfc, float1, float2, float3, matrix4x3f);
	}

	public Matrix4x3f rotationAround(Quaternionfc quaternionfc, float float1, float float2, float float3) {
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
		this._m20(float15 + float13);
		this._m21(float17 - float19);
		this._m22(float7 - float6 - float5 + float4);
		this._m00(float4 + float5 - float7 - float6);
		this._m01(float11 + float9);
		this._m02(float13 - float15);
		this._m10(float11 - float9);
		this._m11(float6 - float7 + float4 - float5);
		this._m12(float17 + float19);
		this._m30(-this.m00 * float1 - this.m10 * float2 - this.m20 * float3 + float1);
		this._m31(-this.m01 * float1 - this.m11 * float2 - this.m21 * float3 + float2);
		this._m32(-this.m02 * float1 - this.m12 * float2 - this.m22 * float3 + float3);
		this.properties = 16;
		return this;
	}

	public Matrix4x3f rotateLocal(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return this.rotateLocalX(float2 * float1, matrix4x3f);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return this.rotateLocalY(float3 * float1, matrix4x3f);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? this.rotateLocalZ(float4 * float1, matrix4x3f) : this.rotateLocalInternal(float1, float2, float3, float4, matrix4x3f);
		}
	}

	private Matrix4x3f rotateLocalInternal(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
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
		matrix4x3f.m00 = float23;
		matrix4x3f.m01 = float24;
		matrix4x3f.m02 = float25;
		matrix4x3f.m10 = float26;
		matrix4x3f.m11 = float27;
		matrix4x3f.m12 = float28;
		matrix4x3f.m20 = float29;
		matrix4x3f.m21 = float30;
		matrix4x3f.m22 = float31;
		matrix4x3f.m30 = float32;
		matrix4x3f.m31 = float33;
		matrix4x3f.m32 = float34;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotateLocal(float float1, float float2, float float3, float float4) {
		return this.rotateLocal(float1, float2, float3, float4, this);
	}

	public Matrix4x3f rotateLocalX(float float1, Matrix4x3f matrix4x3f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = float3 * this.m01 - float2 * this.m02;
		float float5 = float2 * this.m01 + float3 * this.m02;
		float float6 = float3 * this.m11 - float2 * this.m12;
		float float7 = float2 * this.m11 + float3 * this.m12;
		float float8 = float3 * this.m21 - float2 * this.m22;
		float float9 = float2 * this.m21 + float3 * this.m22;
		float float10 = float3 * this.m31 - float2 * this.m32;
		float float11 = float2 * this.m31 + float3 * this.m32;
		matrix4x3f._m00(this.m00)._m01(float4)._m02(float5)._m10(this.m10)._m11(float6)._m12(float7)._m20(this.m20)._m21(float8)._m22(float9)._m30(this.m30)._m31(float10)._m32(float11)._properties(this.properties & -13);
		return matrix4x3f;
	}

	public Matrix4x3f rotateLocalX(float float1) {
		return this.rotateLocalX(float1, this);
	}

	public Matrix4x3f rotateLocalY(float float1, Matrix4x3f matrix4x3f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = float3 * this.m00 + float2 * this.m02;
		float float5 = -float2 * this.m00 + float3 * this.m02;
		float float6 = float3 * this.m10 + float2 * this.m12;
		float float7 = -float2 * this.m10 + float3 * this.m12;
		float float8 = float3 * this.m20 + float2 * this.m22;
		float float9 = -float2 * this.m20 + float3 * this.m22;
		float float10 = float3 * this.m30 + float2 * this.m32;
		float float11 = -float2 * this.m30 + float3 * this.m32;
		matrix4x3f._m00(float4)._m01(this.m01)._m02(float5)._m10(float6)._m11(this.m11)._m12(float7)._m20(float8)._m21(this.m21)._m22(float9)._m30(float10)._m31(this.m31)._m32(float11)._properties(this.properties & -13);
		return matrix4x3f;
	}

	public Matrix4x3f rotateLocalY(float float1) {
		return this.rotateLocalY(float1, this);
	}

	public Matrix4x3f rotateLocalZ(float float1, Matrix4x3f matrix4x3f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = float3 * this.m00 - float2 * this.m01;
		float float5 = float2 * this.m00 + float3 * this.m01;
		float float6 = float3 * this.m10 - float2 * this.m11;
		float float7 = float2 * this.m10 + float3 * this.m11;
		float float8 = float3 * this.m20 - float2 * this.m21;
		float float9 = float2 * this.m20 + float3 * this.m21;
		float float10 = float3 * this.m30 - float2 * this.m31;
		float float11 = float2 * this.m30 + float3 * this.m31;
		matrix4x3f._m00(float4)._m01(float5)._m02(this.m02)._m10(float6)._m11(float7)._m12(this.m12)._m20(float8)._m21(float9)._m22(this.m22)._m30(float10)._m31(float11)._m32(this.m32)._properties(this.properties & -13);
		return matrix4x3f;
	}

	public Matrix4x3f rotateLocalZ(float float1) {
		return this.rotateLocalZ(float1, this);
	}

	public Matrix4x3f translate(Vector3fc vector3fc) {
		return this.translate(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4x3f translate(Vector3fc vector3fc, Matrix4x3f matrix4x3f) {
		return this.translate(vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix4x3f);
	}

	public Matrix4x3f translate(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
		return (this.properties & 4) != 0 ? matrix4x3f.translation(float1, float2, float3) : this.translateGeneric(float1, float2, float3, matrix4x3f);
	}

	private Matrix4x3f translateGeneric(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
		MemUtil.INSTANCE.copy(this, matrix4x3f);
		matrix4x3f.m30 = this.m00 * float1 + this.m10 * float2 + this.m20 * float3 + this.m30;
		matrix4x3f.m31 = this.m01 * float1 + this.m11 * float2 + this.m21 * float3 + this.m31;
		matrix4x3f.m32 = this.m02 * float1 + this.m12 * float2 + this.m22 * float3 + this.m32;
		matrix4x3f.properties = this.properties & -5;
		return matrix4x3f;
	}

	public Matrix4x3f translate(float float1, float float2, float float3) {
		if ((this.properties & 4) != 0) {
			return this.translation(float1, float2, float3);
		} else {
			this.m30 += this.m00 * float1 + this.m10 * float2 + this.m20 * float3;
			this.m31 += this.m01 * float1 + this.m11 * float2 + this.m21 * float3;
			this.m32 += this.m02 * float1 + this.m12 * float2 + this.m22 * float3;
			this.properties &= -5;
			return this;
		}
	}

	public Matrix4x3f translateLocal(Vector3fc vector3fc) {
		return this.translateLocal(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4x3f translateLocal(Vector3fc vector3fc, Matrix4x3f matrix4x3f) {
		return this.translateLocal(vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix4x3f);
	}

	public Matrix4x3f translateLocal(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
		matrix4x3f.m00 = this.m00;
		matrix4x3f.m01 = this.m01;
		matrix4x3f.m02 = this.m02;
		matrix4x3f.m10 = this.m10;
		matrix4x3f.m11 = this.m11;
		matrix4x3f.m12 = this.m12;
		matrix4x3f.m20 = this.m20;
		matrix4x3f.m21 = this.m21;
		matrix4x3f.m22 = this.m22;
		matrix4x3f.m30 = this.m30 + float1;
		matrix4x3f.m31 = this.m31 + float2;
		matrix4x3f.m32 = this.m32 + float3;
		matrix4x3f.properties = this.properties & -5;
		return matrix4x3f;
	}

	public Matrix4x3f translateLocal(float float1, float float2, float float3) {
		return this.translateLocal(float1, float2, float3, this);
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeFloat(this.m00);
		objectOutput.writeFloat(this.m01);
		objectOutput.writeFloat(this.m02);
		objectOutput.writeFloat(this.m10);
		objectOutput.writeFloat(this.m11);
		objectOutput.writeFloat(this.m12);
		objectOutput.writeFloat(this.m20);
		objectOutput.writeFloat(this.m21);
		objectOutput.writeFloat(this.m22);
		objectOutput.writeFloat(this.m30);
		objectOutput.writeFloat(this.m31);
		objectOutput.writeFloat(this.m32);
	}

	public void readExternal(ObjectInput objectInput) throws IOException {
		this.m00 = objectInput.readFloat();
		this.m01 = objectInput.readFloat();
		this.m02 = objectInput.readFloat();
		this.m10 = objectInput.readFloat();
		this.m11 = objectInput.readFloat();
		this.m12 = objectInput.readFloat();
		this.m20 = objectInput.readFloat();
		this.m21 = objectInput.readFloat();
		this.m22 = objectInput.readFloat();
		this.m30 = objectInput.readFloat();
		this.m31 = objectInput.readFloat();
		this.m32 = objectInput.readFloat();
		this.determineProperties();
	}

	public Matrix4x3f ortho(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4x3f matrix4x3f) {
		float float7 = 2.0F / (float2 - float1);
		float float8 = 2.0F / (float4 - float3);
		float float9 = (boolean1 ? 1.0F : 2.0F) / (float5 - float6);
		float float10 = (float1 + float2) / (float1 - float2);
		float float11 = (float4 + float3) / (float3 - float4);
		float float12 = (boolean1 ? float5 : float6 + float5) / (float5 - float6);
		matrix4x3f.m30 = this.m00 * float10 + this.m10 * float11 + this.m20 * float12 + this.m30;
		matrix4x3f.m31 = this.m01 * float10 + this.m11 * float11 + this.m21 * float12 + this.m31;
		matrix4x3f.m32 = this.m02 * float10 + this.m12 * float11 + this.m22 * float12 + this.m32;
		matrix4x3f.m00 = this.m00 * float7;
		matrix4x3f.m01 = this.m01 * float7;
		matrix4x3f.m02 = this.m02 * float7;
		matrix4x3f.m10 = this.m10 * float8;
		matrix4x3f.m11 = this.m11 * float8;
		matrix4x3f.m12 = this.m12 * float8;
		matrix4x3f.m20 = this.m20 * float9;
		matrix4x3f.m21 = this.m21 * float9;
		matrix4x3f.m22 = this.m22 * float9;
		matrix4x3f.properties = this.properties & -29;
		return matrix4x3f;
	}

	public Matrix4x3f ortho(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f) {
		return this.ortho(float1, float2, float3, float4, float5, float6, false, matrix4x3f);
	}

	public Matrix4x3f ortho(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		return this.ortho(float1, float2, float3, float4, float5, float6, boolean1, this);
	}

	public Matrix4x3f ortho(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.ortho(float1, float2, float3, float4, float5, float6, false);
	}

	public Matrix4x3f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4x3f matrix4x3f) {
		float float7 = 2.0F / (float2 - float1);
		float float8 = 2.0F / (float4 - float3);
		float float9 = (boolean1 ? 1.0F : 2.0F) / (float6 - float5);
		float float10 = (float1 + float2) / (float1 - float2);
		float float11 = (float4 + float3) / (float3 - float4);
		float float12 = (boolean1 ? float5 : float6 + float5) / (float5 - float6);
		matrix4x3f.m30 = this.m00 * float10 + this.m10 * float11 + this.m20 * float12 + this.m30;
		matrix4x3f.m31 = this.m01 * float10 + this.m11 * float11 + this.m21 * float12 + this.m31;
		matrix4x3f.m32 = this.m02 * float10 + this.m12 * float11 + this.m22 * float12 + this.m32;
		matrix4x3f.m00 = this.m00 * float7;
		matrix4x3f.m01 = this.m01 * float7;
		matrix4x3f.m02 = this.m02 * float7;
		matrix4x3f.m10 = this.m10 * float8;
		matrix4x3f.m11 = this.m11 * float8;
		matrix4x3f.m12 = this.m12 * float8;
		matrix4x3f.m20 = this.m20 * float9;
		matrix4x3f.m21 = this.m21 * float9;
		matrix4x3f.m22 = this.m22 * float9;
		matrix4x3f.properties = this.properties & -29;
		return matrix4x3f;
	}

	public Matrix4x3f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f) {
		return this.orthoLH(float1, float2, float3, float4, float5, float6, false, matrix4x3f);
	}

	public Matrix4x3f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		return this.orthoLH(float1, float2, float3, float4, float5, float6, boolean1, this);
	}

	public Matrix4x3f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.orthoLH(float1, float2, float3, float4, float5, float6, false);
	}

	public Matrix4x3f setOrtho(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		MemUtil.INSTANCE.identity(this);
		this.m00 = 2.0F / (float2 - float1);
		this.m11 = 2.0F / (float4 - float3);
		this.m22 = (boolean1 ? 1.0F : 2.0F) / (float5 - float6);
		this.m30 = (float2 + float1) / (float1 - float2);
		this.m31 = (float4 + float3) / (float3 - float4);
		this.m32 = (boolean1 ? float5 : float6 + float5) / (float5 - float6);
		this.properties = 0;
		return this;
	}

	public Matrix4x3f setOrtho(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.setOrtho(float1, float2, float3, float4, float5, float6, false);
	}

	public Matrix4x3f setOrthoLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1) {
		MemUtil.INSTANCE.identity(this);
		this.m00 = 2.0F / (float2 - float1);
		this.m11 = 2.0F / (float4 - float3);
		this.m22 = (boolean1 ? 1.0F : 2.0F) / (float6 - float5);
		this.m30 = (float2 + float1) / (float1 - float2);
		this.m31 = (float4 + float3) / (float3 - float4);
		this.m32 = (boolean1 ? float5 : float6 + float5) / (float5 - float6);
		this.properties = 0;
		return this;
	}

	public Matrix4x3f setOrthoLH(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.setOrthoLH(float1, float2, float3, float4, float5, float6, false);
	}

	public Matrix4x3f orthoSymmetric(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4x3f matrix4x3f) {
		float float5 = 2.0F / float1;
		float float6 = 2.0F / float2;
		float float7 = (boolean1 ? 1.0F : 2.0F) / (float3 - float4);
		float float8 = (boolean1 ? float3 : float4 + float3) / (float3 - float4);
		matrix4x3f.m30 = this.m20 * float8 + this.m30;
		matrix4x3f.m31 = this.m21 * float8 + this.m31;
		matrix4x3f.m32 = this.m22 * float8 + this.m32;
		matrix4x3f.m00 = this.m00 * float5;
		matrix4x3f.m01 = this.m01 * float5;
		matrix4x3f.m02 = this.m02 * float5;
		matrix4x3f.m10 = this.m10 * float6;
		matrix4x3f.m11 = this.m11 * float6;
		matrix4x3f.m12 = this.m12 * float6;
		matrix4x3f.m20 = this.m20 * float7;
		matrix4x3f.m21 = this.m21 * float7;
		matrix4x3f.m22 = this.m22 * float7;
		matrix4x3f.properties = this.properties & -29;
		return matrix4x3f;
	}

	public Matrix4x3f orthoSymmetric(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
		return this.orthoSymmetric(float1, float2, float3, float4, false, matrix4x3f);
	}

	public Matrix4x3f orthoSymmetric(float float1, float float2, float float3, float float4, boolean boolean1) {
		return this.orthoSymmetric(float1, float2, float3, float4, boolean1, this);
	}

	public Matrix4x3f orthoSymmetric(float float1, float float2, float float3, float float4) {
		return this.orthoSymmetric(float1, float2, float3, float4, false, this);
	}

	public Matrix4x3f orthoSymmetricLH(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4x3f matrix4x3f) {
		float float5 = 2.0F / float1;
		float float6 = 2.0F / float2;
		float float7 = (boolean1 ? 1.0F : 2.0F) / (float4 - float3);
		float float8 = (boolean1 ? float3 : float4 + float3) / (float3 - float4);
		matrix4x3f.m30 = this.m20 * float8 + this.m30;
		matrix4x3f.m31 = this.m21 * float8 + this.m31;
		matrix4x3f.m32 = this.m22 * float8 + this.m32;
		matrix4x3f.m00 = this.m00 * float5;
		matrix4x3f.m01 = this.m01 * float5;
		matrix4x3f.m02 = this.m02 * float5;
		matrix4x3f.m10 = this.m10 * float6;
		matrix4x3f.m11 = this.m11 * float6;
		matrix4x3f.m12 = this.m12 * float6;
		matrix4x3f.m20 = this.m20 * float7;
		matrix4x3f.m21 = this.m21 * float7;
		matrix4x3f.m22 = this.m22 * float7;
		matrix4x3f.properties = this.properties & -29;
		return matrix4x3f;
	}

	public Matrix4x3f orthoSymmetricLH(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
		return this.orthoSymmetricLH(float1, float2, float3, float4, false, matrix4x3f);
	}

	public Matrix4x3f orthoSymmetricLH(float float1, float float2, float float3, float float4, boolean boolean1) {
		return this.orthoSymmetricLH(float1, float2, float3, float4, boolean1, this);
	}

	public Matrix4x3f orthoSymmetricLH(float float1, float float2, float float3, float float4) {
		return this.orthoSymmetricLH(float1, float2, float3, float4, false, this);
	}

	public Matrix4x3f setOrthoSymmetric(float float1, float float2, float float3, float float4, boolean boolean1) {
		MemUtil.INSTANCE.identity(this);
		this.m00 = 2.0F / float1;
		this.m11 = 2.0F / float2;
		this.m22 = (boolean1 ? 1.0F : 2.0F) / (float3 - float4);
		this.m32 = (boolean1 ? float3 : float4 + float3) / (float3 - float4);
		this.properties = 0;
		return this;
	}

	public Matrix4x3f setOrthoSymmetric(float float1, float float2, float float3, float float4) {
		return this.setOrthoSymmetric(float1, float2, float3, float4, false);
	}

	public Matrix4x3f setOrthoSymmetricLH(float float1, float float2, float float3, float float4, boolean boolean1) {
		MemUtil.INSTANCE.identity(this);
		this.m00 = 2.0F / float1;
		this.m11 = 2.0F / float2;
		this.m22 = (boolean1 ? 1.0F : 2.0F) / (float4 - float3);
		this.m32 = (boolean1 ? float3 : float4 + float3) / (float3 - float4);
		this.properties = 0;
		return this;
	}

	public Matrix4x3f setOrthoSymmetricLH(float float1, float float2, float float3, float float4) {
		return this.setOrthoSymmetricLH(float1, float2, float3, float4, false);
	}

	public Matrix4x3f ortho2D(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
		float float5 = 2.0F / (float2 - float1);
		float float6 = 2.0F / (float4 - float3);
		float float7 = -(float2 + float1) / (float2 - float1);
		float float8 = -(float4 + float3) / (float4 - float3);
		matrix4x3f.m30 = this.m00 * float7 + this.m10 * float8 + this.m30;
		matrix4x3f.m31 = this.m01 * float7 + this.m11 * float8 + this.m31;
		matrix4x3f.m32 = this.m02 * float7 + this.m12 * float8 + this.m32;
		matrix4x3f.m00 = this.m00 * float5;
		matrix4x3f.m01 = this.m01 * float5;
		matrix4x3f.m02 = this.m02 * float5;
		matrix4x3f.m10 = this.m10 * float6;
		matrix4x3f.m11 = this.m11 * float6;
		matrix4x3f.m12 = this.m12 * float6;
		matrix4x3f.m20 = -this.m20;
		matrix4x3f.m21 = -this.m21;
		matrix4x3f.m22 = -this.m22;
		matrix4x3f.properties = this.properties & -29;
		return matrix4x3f;
	}

	public Matrix4x3f ortho2D(float float1, float float2, float float3, float float4) {
		return this.ortho2D(float1, float2, float3, float4, this);
	}

	public Matrix4x3f ortho2DLH(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
		float float5 = 2.0F / (float2 - float1);
		float float6 = 2.0F / (float4 - float3);
		float float7 = -(float2 + float1) / (float2 - float1);
		float float8 = -(float4 + float3) / (float4 - float3);
		matrix4x3f.m30 = this.m00 * float7 + this.m10 * float8 + this.m30;
		matrix4x3f.m31 = this.m01 * float7 + this.m11 * float8 + this.m31;
		matrix4x3f.m32 = this.m02 * float7 + this.m12 * float8 + this.m32;
		matrix4x3f.m00 = this.m00 * float5;
		matrix4x3f.m01 = this.m01 * float5;
		matrix4x3f.m02 = this.m02 * float5;
		matrix4x3f.m10 = this.m10 * float6;
		matrix4x3f.m11 = this.m11 * float6;
		matrix4x3f.m12 = this.m12 * float6;
		matrix4x3f.m20 = this.m20;
		matrix4x3f.m21 = this.m21;
		matrix4x3f.m22 = this.m22;
		matrix4x3f.properties = this.properties & -29;
		return matrix4x3f;
	}

	public Matrix4x3f ortho2DLH(float float1, float float2, float float3, float float4) {
		return this.ortho2DLH(float1, float2, float3, float4, this);
	}

	public Matrix4x3f setOrtho2D(float float1, float float2, float float3, float float4) {
		MemUtil.INSTANCE.identity(this);
		this.m00 = 2.0F / (float2 - float1);
		this.m11 = 2.0F / (float4 - float3);
		this.m22 = -1.0F;
		this.m30 = -(float2 + float1) / (float2 - float1);
		this.m31 = -(float4 + float3) / (float4 - float3);
		this.properties = 0;
		return this;
	}

	public Matrix4x3f setOrtho2DLH(float float1, float float2, float float3, float float4) {
		MemUtil.INSTANCE.identity(this);
		this.m00 = 2.0F / (float2 - float1);
		this.m11 = 2.0F / (float4 - float3);
		this.m22 = 1.0F;
		this.m30 = -(float2 + float1) / (float2 - float1);
		this.m31 = -(float4 + float3) / (float4 - float3);
		this.properties = 0;
		return this;
	}

	public Matrix4x3f lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.lookAlong(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), this);
	}

	public Matrix4x3f lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4x3f matrix4x3f) {
		return this.lookAlong(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), matrix4x3f);
	}

	public Matrix4x3f lookAlong(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return this.setLookAlong(float1, float2, float3, float4, float5, float6);
		} else {
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
			float float18 = this.m00 * float9 + this.m10 * float13 + this.m20 * float2;
			float float19 = this.m01 * float9 + this.m11 * float13 + this.m21 * float2;
			float float20 = this.m02 * float9 + this.m12 * float13 + this.m22 * float2;
			matrix4x3f.m20 = this.m00 * float10 + this.m10 * float14 + this.m20 * float3;
			matrix4x3f.m21 = this.m01 * float10 + this.m11 * float14 + this.m21 * float3;
			matrix4x3f.m22 = this.m02 * float10 + this.m12 * float14 + this.m22 * float3;
			matrix4x3f.m00 = float15;
			matrix4x3f.m01 = float16;
			matrix4x3f.m02 = float17;
			matrix4x3f.m10 = float18;
			matrix4x3f.m11 = float19;
			matrix4x3f.m12 = float20;
			matrix4x3f.m30 = this.m30;
			matrix4x3f.m31 = this.m31;
			matrix4x3f.m32 = this.m32;
			matrix4x3f.properties = this.properties & -13;
			return matrix4x3f;
		}
	}

	public Matrix4x3f lookAlong(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.lookAlong(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4x3f setLookAlong(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.setLookAlong(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix4x3f setLookAlong(float float1, float float2, float float3, float float4, float float5, float float6) {
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
		this.m00 = float8;
		this.m01 = float12;
		this.m02 = float1;
		this.m10 = float9;
		this.m11 = float13;
		this.m12 = float2;
		this.m20 = float10;
		this.m21 = float14;
		this.m22 = float3;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f setLookAt(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		return this.setLookAt(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z());
	}

	public Matrix4x3f setLookAt(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
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
		this.m00 = float14;
		this.m01 = float18;
		this.m02 = float10;
		this.m10 = float15;
		this.m11 = float19;
		this.m12 = float11;
		this.m20 = float16;
		this.m21 = float20;
		this.m22 = float12;
		this.m30 = -(float14 * float1 + float15 * float2 + float16 * float3);
		this.m31 = -(float18 * float1 + float19 * float2 + float20 * float3);
		this.m32 = -(float10 * float1 + float11 * float2 + float12 * float3);
		this.properties = 16;
		return this;
	}

	public Matrix4x3f lookAt(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Matrix4x3f matrix4x3f) {
		return this.lookAt(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), matrix4x3f);
	}

	public Matrix4x3f lookAt(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		return this.lookAt(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), this);
	}

	public Matrix4x3f lookAt(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4x3f matrix4x3f) {
		return (this.properties & 4) != 0 ? matrix4x3f.setLookAt(float1, float2, float3, float4, float5, float6, float7, float8, float9) : this.lookAtGeneric(float1, float2, float3, float4, float5, float6, float7, float8, float9, matrix4x3f);
	}

	private Matrix4x3f lookAtGeneric(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4x3f matrix4x3f) {
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
		matrix4x3f.m30 = this.m00 * float21 + this.m10 * float22 + this.m20 * float23 + this.m30;
		matrix4x3f.m31 = this.m01 * float21 + this.m11 * float22 + this.m21 * float23 + this.m31;
		matrix4x3f.m32 = this.m02 * float21 + this.m12 * float22 + this.m22 * float23 + this.m32;
		float float24 = this.m00 * float14 + this.m10 * float18 + this.m20 * float10;
		float float25 = this.m01 * float14 + this.m11 * float18 + this.m21 * float10;
		float float26 = this.m02 * float14 + this.m12 * float18 + this.m22 * float10;
		float float27 = this.m00 * float15 + this.m10 * float19 + this.m20 * float11;
		float float28 = this.m01 * float15 + this.m11 * float19 + this.m21 * float11;
		float float29 = this.m02 * float15 + this.m12 * float19 + this.m22 * float11;
		matrix4x3f.m20 = this.m00 * float16 + this.m10 * float20 + this.m20 * float12;
		matrix4x3f.m21 = this.m01 * float16 + this.m11 * float20 + this.m21 * float12;
		matrix4x3f.m22 = this.m02 * float16 + this.m12 * float20 + this.m22 * float12;
		matrix4x3f.m00 = float24;
		matrix4x3f.m01 = float25;
		matrix4x3f.m02 = float26;
		matrix4x3f.m10 = float27;
		matrix4x3f.m11 = float28;
		matrix4x3f.m12 = float29;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f lookAt(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		return this.lookAt(float1, float2, float3, float4, float5, float6, float7, float8, float9, this);
	}

	public Matrix4x3f setLookAtLH(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		return this.setLookAtLH(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z());
	}

	public Matrix4x3f setLookAtLH(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
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
		this.m00 = float14;
		this.m01 = float18;
		this.m02 = float10;
		this.m10 = float15;
		this.m11 = float19;
		this.m12 = float11;
		this.m20 = float16;
		this.m21 = float20;
		this.m22 = float12;
		this.m30 = -(float14 * float1 + float15 * float2 + float16 * float3);
		this.m31 = -(float18 * float1 + float19 * float2 + float20 * float3);
		this.m32 = -(float10 * float1 + float11 * float2 + float12 * float3);
		this.properties = 16;
		return this;
	}

	public Matrix4x3f lookAtLH(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Matrix4x3f matrix4x3f) {
		return this.lookAtLH(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), matrix4x3f);
	}

	public Matrix4x3f lookAtLH(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		return this.lookAtLH(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), this);
	}

	public Matrix4x3f lookAtLH(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4x3f matrix4x3f) {
		return (this.properties & 4) != 0 ? matrix4x3f.setLookAtLH(float1, float2, float3, float4, float5, float6, float7, float8, float9) : this.lookAtLHGeneric(float1, float2, float3, float4, float5, float6, float7, float8, float9, matrix4x3f);
	}

	private Matrix4x3f lookAtLHGeneric(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4x3f matrix4x3f) {
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
		matrix4x3f.m30 = this.m00 * float21 + this.m10 * float22 + this.m20 * float23 + this.m30;
		matrix4x3f.m31 = this.m01 * float21 + this.m11 * float22 + this.m21 * float23 + this.m31;
		matrix4x3f.m32 = this.m02 * float21 + this.m12 * float22 + this.m22 * float23 + this.m32;
		float float24 = this.m00 * float14 + this.m10 * float18 + this.m20 * float10;
		float float25 = this.m01 * float14 + this.m11 * float18 + this.m21 * float10;
		float float26 = this.m02 * float14 + this.m12 * float18 + this.m22 * float10;
		float float27 = this.m00 * float15 + this.m10 * float19 + this.m20 * float11;
		float float28 = this.m01 * float15 + this.m11 * float19 + this.m21 * float11;
		float float29 = this.m02 * float15 + this.m12 * float19 + this.m22 * float11;
		matrix4x3f.m20 = this.m00 * float16 + this.m10 * float20 + this.m20 * float12;
		matrix4x3f.m21 = this.m01 * float16 + this.m11 * float20 + this.m21 * float12;
		matrix4x3f.m22 = this.m02 * float16 + this.m12 * float20 + this.m22 * float12;
		matrix4x3f.m00 = float24;
		matrix4x3f.m01 = float25;
		matrix4x3f.m02 = float26;
		matrix4x3f.m10 = float27;
		matrix4x3f.m11 = float28;
		matrix4x3f.m12 = float29;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f lookAtLH(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		return this.lookAtLH(float1, float2, float3, float4, float5, float6, float7, float8, float9, this);
	}

	public Matrix4x3f rotate(Quaternionfc quaternionfc, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.rotation(quaternionfc);
		} else {
			return (this.properties & 8) != 0 ? this.rotateTranslation(quaternionfc, matrix4x3f) : this.rotateGeneric(quaternionfc, matrix4x3f);
		}
	}

	private Matrix4x3f rotateGeneric(Quaternionfc quaternionfc, Matrix4x3f matrix4x3f) {
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
		float float20 = float8 - float6;
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
		matrix4x3f.m20 = this.m00 * float23 + this.m10 * float24 + this.m20 * float25;
		matrix4x3f.m21 = this.m01 * float23 + this.m11 * float24 + this.m21 * float25;
		matrix4x3f.m22 = this.m02 * float23 + this.m12 * float24 + this.m22 * float25;
		matrix4x3f.m00 = float26;
		matrix4x3f.m01 = float27;
		matrix4x3f.m02 = float28;
		matrix4x3f.m10 = float29;
		matrix4x3f.m11 = float30;
		matrix4x3f.m12 = float31;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotate(Quaternionfc quaternionfc) {
		return this.rotate(quaternionfc, this);
	}

	public Matrix4x3f rotateTranslation(Quaternionfc quaternionfc, Matrix4x3f matrix4x3f) {
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
		float float20 = float8 - float6;
		float float21 = float3 - float4 + float1 - float2;
		float float22 = float14 + float16;
		float float23 = float12 + float10;
		float float24 = float14 - float16;
		float float25 = float4 - float3 - float2 + float1;
		matrix4x3f.m20 = float23;
		matrix4x3f.m21 = float24;
		matrix4x3f.m22 = float25;
		matrix4x3f.m00 = float17;
		matrix4x3f.m01 = float18;
		matrix4x3f.m02 = float19;
		matrix4x3f.m10 = float20;
		matrix4x3f.m11 = float21;
		matrix4x3f.m12 = float22;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotateLocal(Quaternionfc quaternionfc, Matrix4x3f matrix4x3f) {
		float float1 = quaternionfc.w() * quaternionfc.w();
		float float2 = quaternionfc.x() * quaternionfc.x();
		float float3 = quaternionfc.y() * quaternionfc.y();
		float float4 = quaternionfc.z() * quaternionfc.z();
		float float5 = quaternionfc.z() * quaternionfc.w();
		float float6 = quaternionfc.x() * quaternionfc.y();
		float float7 = quaternionfc.x() * quaternionfc.z();
		float float8 = quaternionfc.y() * quaternionfc.w();
		float float9 = quaternionfc.y() * quaternionfc.z();
		float float10 = quaternionfc.x() * quaternionfc.w();
		float float11 = float1 + float2 - float4 - float3;
		float float12 = float6 + float5 + float5 + float6;
		float float13 = float7 - float8 + float7 - float8;
		float float14 = -float5 + float6 - float5 + float6;
		float float15 = float3 - float4 + float1 - float2;
		float float16 = float9 + float9 + float10 + float10;
		float float17 = float8 + float7 + float7 + float8;
		float float18 = float9 + float9 - float10 - float10;
		float float19 = float4 - float3 - float2 + float1;
		float float20 = float11 * this.m00 + float14 * this.m01 + float17 * this.m02;
		float float21 = float12 * this.m00 + float15 * this.m01 + float18 * this.m02;
		float float22 = float13 * this.m00 + float16 * this.m01 + float19 * this.m02;
		float float23 = float11 * this.m10 + float14 * this.m11 + float17 * this.m12;
		float float24 = float12 * this.m10 + float15 * this.m11 + float18 * this.m12;
		float float25 = float13 * this.m10 + float16 * this.m11 + float19 * this.m12;
		float float26 = float11 * this.m20 + float14 * this.m21 + float17 * this.m22;
		float float27 = float12 * this.m20 + float15 * this.m21 + float18 * this.m22;
		float float28 = float13 * this.m20 + float16 * this.m21 + float19 * this.m22;
		float float29 = float11 * this.m30 + float14 * this.m31 + float17 * this.m32;
		float float30 = float12 * this.m30 + float15 * this.m31 + float18 * this.m32;
		float float31 = float13 * this.m30 + float16 * this.m31 + float19 * this.m32;
		matrix4x3f.m00 = float20;
		matrix4x3f.m01 = float21;
		matrix4x3f.m02 = float22;
		matrix4x3f.m10 = float23;
		matrix4x3f.m11 = float24;
		matrix4x3f.m12 = float25;
		matrix4x3f.m20 = float26;
		matrix4x3f.m21 = float27;
		matrix4x3f.m22 = float28;
		matrix4x3f.m30 = float29;
		matrix4x3f.m31 = float30;
		matrix4x3f.m32 = float31;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotateLocal(Quaternionfc quaternionfc) {
		return this.rotateLocal(quaternionfc, this);
	}

	public Matrix4x3f rotate(AxisAngle4f axisAngle4f) {
		return this.rotate(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z);
	}

	public Matrix4x3f rotate(AxisAngle4f axisAngle4f, Matrix4x3f matrix4x3f) {
		return this.rotate(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z, matrix4x3f);
	}

	public Matrix4x3f rotate(float float1, Vector3fc vector3fc) {
		return this.rotate(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4x3f rotate(float float1, Vector3fc vector3fc, Matrix4x3f matrix4x3f) {
		return this.rotate(float1, vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix4x3f);
	}

	public Matrix4x3f reflect(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.reflection(float1, float2, float3, float4);
		} else {
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
			matrix4x3f.m30 = this.m00 * float18 + this.m10 * float19 + this.m20 * float20 + this.m30;
			matrix4x3f.m31 = this.m01 * float18 + this.m11 * float19 + this.m21 * float20 + this.m31;
			matrix4x3f.m32 = this.m02 * float18 + this.m12 * float19 + this.m22 * float20 + this.m32;
			float float21 = this.m00 * float9 + this.m10 * float10 + this.m20 * float11;
			float float22 = this.m01 * float9 + this.m11 * float10 + this.m21 * float11;
			float float23 = this.m02 * float9 + this.m12 * float10 + this.m22 * float11;
			float float24 = this.m00 * float12 + this.m10 * float13 + this.m20 * float14;
			float float25 = this.m01 * float12 + this.m11 * float13 + this.m21 * float14;
			float float26 = this.m02 * float12 + this.m12 * float13 + this.m22 * float14;
			matrix4x3f.m20 = this.m00 * float15 + this.m10 * float16 + this.m20 * float17;
			matrix4x3f.m21 = this.m01 * float15 + this.m11 * float16 + this.m21 * float17;
			matrix4x3f.m22 = this.m02 * float15 + this.m12 * float16 + this.m22 * float17;
			matrix4x3f.m00 = float21;
			matrix4x3f.m01 = float22;
			matrix4x3f.m02 = float23;
			matrix4x3f.m10 = float24;
			matrix4x3f.m11 = float25;
			matrix4x3f.m12 = float26;
			matrix4x3f.properties = this.properties & -13;
			return matrix4x3f;
		}
	}

	public Matrix4x3f reflect(float float1, float float2, float float3, float float4) {
		return this.reflect(float1, float2, float3, float4, this);
	}

	public Matrix4x3f reflect(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.reflect(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4x3f reflect(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f) {
		float float7 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float float8 = float1 * float7;
		float float9 = float2 * float7;
		float float10 = float3 * float7;
		return this.reflect(float8, float9, float10, -float8 * float4 - float9 * float5 - float10 * float6, matrix4x3f);
	}

	public Matrix4x3f reflect(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.reflect(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix4x3f reflect(Quaternionfc quaternionfc, Vector3fc vector3fc) {
		return this.reflect(quaternionfc, vector3fc, this);
	}

	public Matrix4x3f reflect(Quaternionfc quaternionfc, Vector3fc vector3fc, Matrix4x3f matrix4x3f) {
		double double1 = (double)(quaternionfc.x() + quaternionfc.x());
		double double2 = (double)(quaternionfc.y() + quaternionfc.y());
		double double3 = (double)(quaternionfc.z() + quaternionfc.z());
		float float1 = (float)((double)quaternionfc.x() * double3 + (double)quaternionfc.w() * double2);
		float float2 = (float)((double)quaternionfc.y() * double3 - (double)quaternionfc.w() * double1);
		float float3 = (float)(1.0 - ((double)quaternionfc.x() * double1 + (double)quaternionfc.y() * double2));
		return this.reflect(float1, float2, float3, vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix4x3f);
	}

	public Matrix4x3f reflect(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4x3f matrix4x3f) {
		return this.reflect(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), matrix4x3f);
	}

	public Matrix4x3f reflection(float float1, float float2, float float3, float float4) {
		float float5 = float1 + float1;
		float float6 = float2 + float2;
		float float7 = float3 + float3;
		float float8 = float4 + float4;
		this.m00 = 1.0F - float5 * float1;
		this.m01 = -float5 * float2;
		this.m02 = -float5 * float3;
		this.m10 = -float6 * float1;
		this.m11 = 1.0F - float6 * float2;
		this.m12 = -float6 * float3;
		this.m20 = -float7 * float1;
		this.m21 = -float7 * float2;
		this.m22 = 1.0F - float7 * float3;
		this.m30 = -float8 * float1;
		this.m31 = -float8 * float2;
		this.m32 = -float8 * float3;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f reflection(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float float8 = float1 * float7;
		float float9 = float2 * float7;
		float float10 = float3 * float7;
		return this.reflection(float8, float9, float10, -float8 * float4 - float9 * float5 - float10 * float6);
	}

	public Matrix4x3f reflection(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.reflection(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix4x3f reflection(Quaternionfc quaternionfc, Vector3fc vector3fc) {
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
			vector4f.x = this.m00;
			vector4f.y = this.m10;
			vector4f.z = this.m20;
			vector4f.w = this.m30;
			break;
		
		case 1: 
			vector4f.x = this.m01;
			vector4f.y = this.m11;
			vector4f.z = this.m21;
			vector4f.w = this.m31;
			break;
		
		case 2: 
			vector4f.x = this.m02;
			vector4f.y = this.m12;
			vector4f.z = this.m22;
			vector4f.w = this.m32;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector4f;
	}

	public Matrix4x3f setRow(int int1, Vector4fc vector4fc) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			this.m00 = vector4fc.x();
			this.m10 = vector4fc.y();
			this.m20 = vector4fc.z();
			this.m30 = vector4fc.w();
			break;
		
		case 1: 
			this.m01 = vector4fc.x();
			this.m11 = vector4fc.y();
			this.m21 = vector4fc.z();
			this.m31 = vector4fc.w();
			break;
		
		case 2: 
			this.m02 = vector4fc.x();
			this.m12 = vector4fc.y();
			this.m22 = vector4fc.z();
			this.m32 = vector4fc.w();
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		this.properties = 0;
		return this;
	}

	public Vector3f getColumn(int int1, Vector3f vector3f) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			vector3f.x = this.m00;
			vector3f.y = this.m01;
			vector3f.z = this.m02;
			break;
		
		case 1: 
			vector3f.x = this.m10;
			vector3f.y = this.m11;
			vector3f.z = this.m12;
			break;
		
		case 2: 
			vector3f.x = this.m20;
			vector3f.y = this.m21;
			vector3f.z = this.m22;
			break;
		
		case 3: 
			vector3f.x = this.m30;
			vector3f.y = this.m31;
			vector3f.z = this.m32;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector3f;
	}

	public Matrix4x3f setColumn(int int1, Vector3fc vector3fc) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			this.m00 = vector3fc.x();
			this.m01 = vector3fc.y();
			this.m02 = vector3fc.z();
			break;
		
		case 1: 
			this.m10 = vector3fc.x();
			this.m11 = vector3fc.y();
			this.m12 = vector3fc.z();
			break;
		
		case 2: 
			this.m20 = vector3fc.x();
			this.m21 = vector3fc.y();
			this.m22 = vector3fc.z();
			break;
		
		case 3: 
			this.m30 = vector3fc.x();
			this.m31 = vector3fc.y();
			this.m32 = vector3fc.z();
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		this.properties = 0;
		return this;
	}

	public Matrix4x3f normal() {
		return this.normal(this);
	}

	public Matrix4x3f normal(Matrix4x3f matrix4x3f) {
		if ((this.properties & 4) != 0) {
			return matrix4x3f.identity();
		} else {
			return (this.properties & 16) != 0 ? this.normalOrthonormal(matrix4x3f) : this.normalGeneric(matrix4x3f);
		}
	}

	private Matrix4x3f normalOrthonormal(Matrix4x3f matrix4x3f) {
		if (matrix4x3f != this) {
			matrix4x3f.set((Matrix4x3fc)this);
		}

		return matrix4x3f._properties(16);
	}

	private Matrix4x3f normalGeneric(Matrix4x3f matrix4x3f) {
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
		matrix4x3f.m00 = float9;
		matrix4x3f.m01 = float10;
		matrix4x3f.m02 = float11;
		matrix4x3f.m10 = float12;
		matrix4x3f.m11 = float13;
		matrix4x3f.m12 = float14;
		matrix4x3f.m20 = float15;
		matrix4x3f.m21 = float16;
		matrix4x3f.m22 = float17;
		matrix4x3f.m30 = 0.0F;
		matrix4x3f.m31 = 0.0F;
		matrix4x3f.m32 = 0.0F;
		matrix4x3f.properties = this.properties & -9;
		return matrix4x3f;
	}

	public Matrix3f normal(Matrix3f matrix3f) {
		return (this.properties & 16) != 0 ? this.normalOrthonormal(matrix3f) : this.normalGeneric(matrix3f);
	}

	private Matrix3f normalOrthonormal(Matrix3f matrix3f) {
		return matrix3f.set((Matrix4x3fc)this);
	}

	private Matrix3f normalGeneric(Matrix3f matrix3f) {
		float float1 = this.m00 * this.m11;
		float float2 = this.m01 * this.m10;
		float float3 = this.m02 * this.m10;
		float float4 = this.m00 * this.m12;
		float float5 = this.m01 * this.m12;
		float float6 = this.m02 * this.m11;
		float float7 = (float1 - float2) * this.m22 + (float3 - float4) * this.m21 + (float5 - float6) * this.m20;
		float float8 = 1.0F / float7;
		matrix3f.m00((this.m11 * this.m22 - this.m21 * this.m12) * float8);
		matrix3f.m01((this.m20 * this.m12 - this.m10 * this.m22) * float8);
		matrix3f.m02((this.m10 * this.m21 - this.m20 * this.m11) * float8);
		matrix3f.m10((this.m21 * this.m02 - this.m01 * this.m22) * float8);
		matrix3f.m11((this.m00 * this.m22 - this.m20 * this.m02) * float8);
		matrix3f.m12((this.m20 * this.m01 - this.m00 * this.m21) * float8);
		matrix3f.m20((float5 - float6) * float8);
		matrix3f.m21((float3 - float4) * float8);
		matrix3f.m22((float1 - float2) * float8);
		return matrix3f;
	}

	public Matrix4x3f cofactor3x3() {
		return this.cofactor3x3(this);
	}

	public Matrix3f cofactor3x3(Matrix3f matrix3f) {
		matrix3f.m00 = this.m11 * this.m22 - this.m21 * this.m12;
		matrix3f.m01 = this.m20 * this.m12 - this.m10 * this.m22;
		matrix3f.m02 = this.m10 * this.m21 - this.m20 * this.m11;
		matrix3f.m10 = this.m21 * this.m02 - this.m01 * this.m22;
		matrix3f.m11 = this.m00 * this.m22 - this.m20 * this.m02;
		matrix3f.m12 = this.m20 * this.m01 - this.m00 * this.m21;
		matrix3f.m20 = this.m01 * this.m12 - this.m02 * this.m11;
		matrix3f.m21 = this.m02 * this.m10 - this.m00 * this.m12;
		matrix3f.m22 = this.m00 * this.m11 - this.m01 * this.m10;
		return matrix3f;
	}

	public Matrix4x3f cofactor3x3(Matrix4x3f matrix4x3f) {
		float float1 = this.m11 * this.m22 - this.m21 * this.m12;
		float float2 = this.m20 * this.m12 - this.m10 * this.m22;
		float float3 = this.m10 * this.m21 - this.m20 * this.m11;
		float float4 = this.m21 * this.m02 - this.m01 * this.m22;
		float float5 = this.m00 * this.m22 - this.m20 * this.m02;
		float float6 = this.m20 * this.m01 - this.m00 * this.m21;
		float float7 = this.m01 * this.m12 - this.m11 * this.m02;
		float float8 = this.m02 * this.m10 - this.m12 * this.m00;
		float float9 = this.m00 * this.m11 - this.m10 * this.m01;
		matrix4x3f.m00 = float1;
		matrix4x3f.m01 = float2;
		matrix4x3f.m02 = float3;
		matrix4x3f.m10 = float4;
		matrix4x3f.m11 = float5;
		matrix4x3f.m12 = float6;
		matrix4x3f.m20 = float7;
		matrix4x3f.m21 = float8;
		matrix4x3f.m22 = float9;
		matrix4x3f.m30 = 0.0F;
		matrix4x3f.m31 = 0.0F;
		matrix4x3f.m32 = 0.0F;
		matrix4x3f.properties = this.properties & -9;
		return matrix4x3f;
	}

	public Matrix4x3f normalize3x3() {
		return this.normalize3x3(this);
	}

	public Matrix4x3f normalize3x3(Matrix4x3f matrix4x3f) {
		float float1 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
		float float2 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
		float float3 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
		matrix4x3f.m00 = this.m00 * float1;
		matrix4x3f.m01 = this.m01 * float1;
		matrix4x3f.m02 = this.m02 * float1;
		matrix4x3f.m10 = this.m10 * float2;
		matrix4x3f.m11 = this.m11 * float2;
		matrix4x3f.m12 = this.m12 * float2;
		matrix4x3f.m20 = this.m20 * float3;
		matrix4x3f.m21 = this.m21 * float3;
		matrix4x3f.m22 = this.m22 * float3;
		matrix4x3f.properties = this.properties;
		return matrix4x3f;
	}

	public Matrix3f normalize3x3(Matrix3f matrix3f) {
		float float1 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
		float float2 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
		float float3 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
		matrix3f.m00(this.m00 * float1);
		matrix3f.m01(this.m01 * float1);
		matrix3f.m02(this.m02 * float1);
		matrix3f.m10(this.m10 * float2);
		matrix3f.m11(this.m11 * float2);
		matrix3f.m12(this.m12 * float2);
		matrix3f.m20(this.m20 * float3);
		matrix3f.m21(this.m21 * float3);
		matrix3f.m22(this.m22 * float3);
		return matrix3f;
	}

	public Vector4f frustumPlane(int int1, Vector4f vector4f) {
		switch (int1) {
		case 0: 
			vector4f.set(this.m00, this.m10, this.m20, 1.0F + this.m30).normalize();
			break;
		
		case 1: 
			vector4f.set(-this.m00, -this.m10, -this.m20, 1.0F - this.m30).normalize();
			break;
		
		case 2: 
			vector4f.set(this.m01, this.m11, this.m21, 1.0F + this.m31).normalize();
			break;
		
		case 3: 
			vector4f.set(-this.m01, -this.m11, -this.m21, 1.0F - this.m31).normalize();
			break;
		
		case 4: 
			vector4f.set(this.m02, this.m12, this.m22, 1.0F + this.m32).normalize();
			break;
		
		case 5: 
			vector4f.set(-this.m02, -this.m12, -this.m22, 1.0F - this.m32).normalize();
			break;
		
		default: 
			throw new IllegalArgumentException("which");
		
		}
		return vector4f;
	}

	public Vector3f positiveZ(Vector3f vector3f) {
		vector3f.x = this.m10 * this.m21 - this.m11 * this.m20;
		vector3f.y = this.m20 * this.m01 - this.m21 * this.m00;
		vector3f.z = this.m00 * this.m11 - this.m01 * this.m10;
		return vector3f.normalize(vector3f);
	}

	public Vector3f normalizedPositiveZ(Vector3f vector3f) {
		vector3f.x = this.m02;
		vector3f.y = this.m12;
		vector3f.z = this.m22;
		return vector3f;
	}

	public Vector3f positiveX(Vector3f vector3f) {
		vector3f.x = this.m11 * this.m22 - this.m12 * this.m21;
		vector3f.y = this.m02 * this.m21 - this.m01 * this.m22;
		vector3f.z = this.m01 * this.m12 - this.m02 * this.m11;
		return vector3f.normalize(vector3f);
	}

	public Vector3f normalizedPositiveX(Vector3f vector3f) {
		vector3f.x = this.m00;
		vector3f.y = this.m10;
		vector3f.z = this.m20;
		return vector3f;
	}

	public Vector3f positiveY(Vector3f vector3f) {
		vector3f.x = this.m12 * this.m20 - this.m10 * this.m22;
		vector3f.y = this.m00 * this.m22 - this.m02 * this.m20;
		vector3f.z = this.m02 * this.m10 - this.m00 * this.m12;
		return vector3f.normalize(vector3f);
	}

	public Vector3f normalizedPositiveY(Vector3f vector3f) {
		vector3f.x = this.m01;
		vector3f.y = this.m11;
		vector3f.z = this.m21;
		return vector3f;
	}

	public Vector3f origin(Vector3f vector3f) {
		float float1 = this.m00 * this.m11 - this.m01 * this.m10;
		float float2 = this.m00 * this.m12 - this.m02 * this.m10;
		float float3 = this.m01 * this.m12 - this.m02 * this.m11;
		float float4 = this.m20 * this.m31 - this.m21 * this.m30;
		float float5 = this.m20 * this.m32 - this.m22 * this.m30;
		float float6 = this.m21 * this.m32 - this.m22 * this.m31;
		vector3f.x = -this.m10 * float6 + this.m11 * float5 - this.m12 * float4;
		vector3f.y = this.m00 * float6 - this.m01 * float5 + this.m02 * float4;
		vector3f.z = -this.m30 * float3 + this.m31 * float2 - this.m32 * float1;
		return vector3f;
	}

	public Matrix4x3f shadow(Vector4fc vector4fc, float float1, float float2, float float3, float float4) {
		return this.shadow(vector4fc.x(), vector4fc.y(), vector4fc.z(), vector4fc.w(), float1, float2, float3, float4, this);
	}

	public Matrix4x3f shadow(Vector4fc vector4fc, float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
		return this.shadow(vector4fc.x(), vector4fc.y(), vector4fc.z(), vector4fc.w(), float1, float2, float3, float4, matrix4x3f);
	}

	public Matrix4x3f shadow(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		return this.shadow(float1, float2, float3, float4, float5, float6, float7, float8, this);
	}

	public Matrix4x3f shadow(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Matrix4x3f matrix4x3f) {
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
		float float34 = this.m00 * float19 + this.m10 * float20 + this.m20 * float21 + this.m30 * float22;
		float float35 = this.m01 * float19 + this.m11 * float20 + this.m21 * float21 + this.m31 * float22;
		float float36 = this.m02 * float19 + this.m12 * float20 + this.m22 * float21 + this.m32 * float22;
		float float37 = this.m00 * float23 + this.m10 * float24 + this.m20 * float25 + this.m30 * float26;
		float float38 = this.m01 * float23 + this.m11 * float24 + this.m21 * float25 + this.m31 * float26;
		float float39 = this.m02 * float23 + this.m12 * float24 + this.m22 * float25 + this.m32 * float26;
		matrix4x3f.m30 = this.m00 * float27 + this.m10 * float28 + this.m20 * float29 + this.m30 * float30;
		matrix4x3f.m31 = this.m01 * float27 + this.m11 * float28 + this.m21 * float29 + this.m31 * float30;
		matrix4x3f.m32 = this.m02 * float27 + this.m12 * float28 + this.m22 * float29 + this.m32 * float30;
		matrix4x3f.m00 = float31;
		matrix4x3f.m01 = float32;
		matrix4x3f.m02 = float33;
		matrix4x3f.m10 = float34;
		matrix4x3f.m11 = float35;
		matrix4x3f.m12 = float36;
		matrix4x3f.m20 = float37;
		matrix4x3f.m21 = float38;
		matrix4x3f.m22 = float39;
		matrix4x3f.properties = this.properties & -29;
		return matrix4x3f;
	}

	public Matrix4x3f shadow(Vector4fc vector4fc, Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f) {
		float float1 = matrix4x3fc.m10();
		float float2 = matrix4x3fc.m11();
		float float3 = matrix4x3fc.m12();
		float float4 = -float1 * matrix4x3fc.m30() - float2 * matrix4x3fc.m31() - float3 * matrix4x3fc.m32();
		return this.shadow(vector4fc.x(), vector4fc.y(), vector4fc.z(), vector4fc.w(), float1, float2, float3, float4, matrix4x3f);
	}

	public Matrix4x3f shadow(Vector4fc vector4fc, Matrix4x3fc matrix4x3fc) {
		return this.shadow(vector4fc, matrix4x3fc, this);
	}

	public Matrix4x3f shadow(float float1, float float2, float float3, float float4, Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f) {
		float float5 = matrix4x3fc.m10();
		float float6 = matrix4x3fc.m11();
		float float7 = matrix4x3fc.m12();
		float float8 = -float5 * matrix4x3fc.m30() - float6 * matrix4x3fc.m31() - float7 * matrix4x3fc.m32();
		return this.shadow(float1, float2, float3, float4, float5, float6, float7, float8, matrix4x3f);
	}

	public Matrix4x3f shadow(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f) {
		return this.shadow(float1, float2, float3, float4, matrix4x3f, this);
	}

	public Matrix4x3f billboardCylindrical(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
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
		this.m00 = float4;
		this.m01 = float5;
		this.m02 = float6;
		this.m10 = vector3fc3.x();
		this.m11 = vector3fc3.y();
		this.m12 = vector3fc3.z();
		this.m20 = float1;
		this.m21 = float2;
		this.m22 = float3;
		this.m30 = vector3fc.x();
		this.m31 = vector3fc.y();
		this.m32 = vector3fc.z();
		this.properties = 16;
		return this;
	}

	public Matrix4x3f billboardSpherical(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
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
		this.m00 = float5;
		this.m01 = float6;
		this.m02 = float7;
		this.m10 = float9;
		this.m11 = float10;
		this.m12 = float11;
		this.m20 = float1;
		this.m21 = float2;
		this.m22 = float3;
		this.m30 = vector3fc.x();
		this.m31 = vector3fc.y();
		this.m32 = vector3fc.z();
		this.properties = 16;
		return this;
	}

	public Matrix4x3f billboardSpherical(Vector3fc vector3fc, Vector3fc vector3fc2) {
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
		this.m00 = 1.0F - float9;
		this.m01 = float10;
		this.m02 = -float12;
		this.m10 = float10;
		this.m11 = 1.0F - float8;
		this.m12 = float11;
		this.m20 = float12;
		this.m21 = -float11;
		this.m22 = 1.0F - float9 - float8;
		this.m30 = vector3fc.x();
		this.m31 = vector3fc.y();
		this.m32 = vector3fc.z();
		this.properties = 16;
		return this;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + Float.floatToIntBits(this.m00);
		int1 = 31 * int1 + Float.floatToIntBits(this.m01);
		int1 = 31 * int1 + Float.floatToIntBits(this.m02);
		int1 = 31 * int1 + Float.floatToIntBits(this.m10);
		int1 = 31 * int1 + Float.floatToIntBits(this.m11);
		int1 = 31 * int1 + Float.floatToIntBits(this.m12);
		int1 = 31 * int1 + Float.floatToIntBits(this.m20);
		int1 = 31 * int1 + Float.floatToIntBits(this.m21);
		int1 = 31 * int1 + Float.floatToIntBits(this.m22);
		int1 = 31 * int1 + Float.floatToIntBits(this.m30);
		int1 = 31 * int1 + Float.floatToIntBits(this.m31);
		int1 = 31 * int1 + Float.floatToIntBits(this.m32);
		return int1;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object == null) {
			return false;
		} else if (!(object instanceof Matrix4x3f)) {
			return false;
		} else {
			Matrix4x3f matrix4x3f = (Matrix4x3f)object;
			if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(matrix4x3f.m00)) {
				return false;
			} else if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(matrix4x3f.m01)) {
				return false;
			} else if (Float.floatToIntBits(this.m02) != Float.floatToIntBits(matrix4x3f.m02)) {
				return false;
			} else if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(matrix4x3f.m10)) {
				return false;
			} else if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(matrix4x3f.m11)) {
				return false;
			} else if (Float.floatToIntBits(this.m12) != Float.floatToIntBits(matrix4x3f.m12)) {
				return false;
			} else if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(matrix4x3f.m20)) {
				return false;
			} else if (Float.floatToIntBits(this.m21) != Float.floatToIntBits(matrix4x3f.m21)) {
				return false;
			} else if (Float.floatToIntBits(this.m22) != Float.floatToIntBits(matrix4x3f.m22)) {
				return false;
			} else if (Float.floatToIntBits(this.m30) != Float.floatToIntBits(matrix4x3f.m30)) {
				return false;
			} else if (Float.floatToIntBits(this.m31) != Float.floatToIntBits(matrix4x3f.m31)) {
				return false;
			} else {
				return Float.floatToIntBits(this.m32) == Float.floatToIntBits(matrix4x3f.m32);
			}
		}
	}

	public boolean equals(Matrix4x3fc matrix4x3fc, float float1) {
		if (this == matrix4x3fc) {
			return true;
		} else if (matrix4x3fc == null) {
			return false;
		} else if (!(matrix4x3fc instanceof Matrix4x3f)) {
			return false;
		} else if (!Runtime.equals(this.m00, matrix4x3fc.m00(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m01, matrix4x3fc.m01(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m02, matrix4x3fc.m02(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m10, matrix4x3fc.m10(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m11, matrix4x3fc.m11(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m12, matrix4x3fc.m12(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m20, matrix4x3fc.m20(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m21, matrix4x3fc.m21(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m22, matrix4x3fc.m22(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m30, matrix4x3fc.m30(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m31, matrix4x3fc.m31(), float1)) {
			return false;
		} else {
			return Runtime.equals(this.m32, matrix4x3fc.m32(), float1);
		}
	}

	public Matrix4x3f pick(float float1, float float2, float float3, float float4, int[] intArray, Matrix4x3f matrix4x3f) {
		float float5 = (float)intArray[2] / float3;
		float float6 = (float)intArray[3] / float4;
		float float7 = ((float)intArray[2] + 2.0F * ((float)intArray[0] - float1)) / float3;
		float float8 = ((float)intArray[3] + 2.0F * ((float)intArray[1] - float2)) / float4;
		matrix4x3f.m30 = this.m00 * float7 + this.m10 * float8 + this.m30;
		matrix4x3f.m31 = this.m01 * float7 + this.m11 * float8 + this.m31;
		matrix4x3f.m32 = this.m02 * float7 + this.m12 * float8 + this.m32;
		matrix4x3f.m00 = this.m00 * float5;
		matrix4x3f.m01 = this.m01 * float5;
		matrix4x3f.m02 = this.m02 * float5;
		matrix4x3f.m10 = this.m10 * float6;
		matrix4x3f.m11 = this.m11 * float6;
		matrix4x3f.m12 = this.m12 * float6;
		matrix4x3f.properties = 0;
		return matrix4x3f;
	}

	public Matrix4x3f pick(float float1, float float2, float float3, float float4, int[] intArray) {
		return this.pick(float1, float2, float3, float4, intArray, this);
	}

	public Matrix4x3f swap(Matrix4x3f matrix4x3f) {
		MemUtil.INSTANCE.swap(this, matrix4x3f);
		int int1 = this.properties;
		this.properties = matrix4x3f.properties;
		matrix4x3f.properties = int1;
		return this;
	}

	public Matrix4x3f arcball(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f) {
		float float7 = this.m20 * -float1 + this.m30;
		float float8 = this.m21 * -float1 + this.m31;
		float float9 = this.m22 * -float1 + this.m32;
		float float10 = Math.sin(float5);
		float float11 = Math.cosFromSin(float10, float5);
		float float12 = this.m10 * float11 + this.m20 * float10;
		float float13 = this.m11 * float11 + this.m21 * float10;
		float float14 = this.m12 * float11 + this.m22 * float10;
		float float15 = this.m20 * float11 - this.m10 * float10;
		float float16 = this.m21 * float11 - this.m11 * float10;
		float float17 = this.m22 * float11 - this.m12 * float10;
		float10 = Math.sin(float6);
		float11 = Math.cosFromSin(float10, float6);
		float float18 = this.m00 * float11 - float15 * float10;
		float float19 = this.m01 * float11 - float16 * float10;
		float float20 = this.m02 * float11 - float17 * float10;
		float float21 = this.m00 * float10 + float15 * float11;
		float float22 = this.m01 * float10 + float16 * float11;
		float float23 = this.m02 * float10 + float17 * float11;
		matrix4x3f.m30 = -float18 * float2 - float12 * float3 - float21 * float4 + float7;
		matrix4x3f.m31 = -float19 * float2 - float13 * float3 - float22 * float4 + float8;
		matrix4x3f.m32 = -float20 * float2 - float14 * float3 - float23 * float4 + float9;
		matrix4x3f.m20 = float21;
		matrix4x3f.m21 = float22;
		matrix4x3f.m22 = float23;
		matrix4x3f.m10 = float12;
		matrix4x3f.m11 = float13;
		matrix4x3f.m12 = float14;
		matrix4x3f.m00 = float18;
		matrix4x3f.m01 = float19;
		matrix4x3f.m02 = float20;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f arcball(float float1, Vector3fc vector3fc, float float2, float float3, Matrix4x3f matrix4x3f) {
		return this.arcball(float1, vector3fc.x(), vector3fc.y(), vector3fc.z(), float2, float3, matrix4x3f);
	}

	public Matrix4x3f arcball(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.arcball(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4x3f arcball(float float1, Vector3fc vector3fc, float float2, float float3) {
		return this.arcball(float1, vector3fc.x(), vector3fc.y(), vector3fc.z(), float2, float3, this);
	}

	public Matrix4x3f transformAab(float float1, float float2, float float3, float float4, float float5, float float6, Vector3f vector3f, Vector3f vector3f2) {
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

	public Matrix4x3f transformAab(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3f vector3f, Vector3f vector3f2) {
		return this.transformAab(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3f, vector3f2);
	}

	public Matrix4x3f lerp(Matrix4x3fc matrix4x3fc, float float1) {
		return this.lerp(matrix4x3fc, float1, this);
	}

	public Matrix4x3f lerp(Matrix4x3fc matrix4x3fc, float float1, Matrix4x3f matrix4x3f) {
		matrix4x3f.m00 = Math.fma(matrix4x3fc.m00() - this.m00, float1, this.m00);
		matrix4x3f.m01 = Math.fma(matrix4x3fc.m01() - this.m01, float1, this.m01);
		matrix4x3f.m02 = Math.fma(matrix4x3fc.m02() - this.m02, float1, this.m02);
		matrix4x3f.m10 = Math.fma(matrix4x3fc.m10() - this.m10, float1, this.m10);
		matrix4x3f.m11 = Math.fma(matrix4x3fc.m11() - this.m11, float1, this.m11);
		matrix4x3f.m12 = Math.fma(matrix4x3fc.m12() - this.m12, float1, this.m12);
		matrix4x3f.m20 = Math.fma(matrix4x3fc.m20() - this.m20, float1, this.m20);
		matrix4x3f.m21 = Math.fma(matrix4x3fc.m21() - this.m21, float1, this.m21);
		matrix4x3f.m22 = Math.fma(matrix4x3fc.m22() - this.m22, float1, this.m22);
		matrix4x3f.m30 = Math.fma(matrix4x3fc.m30() - this.m30, float1, this.m30);
		matrix4x3f.m31 = Math.fma(matrix4x3fc.m31() - this.m31, float1, this.m31);
		matrix4x3f.m32 = Math.fma(matrix4x3fc.m32() - this.m32, float1, this.m32);
		matrix4x3f.properties = this.properties & matrix4x3fc.properties();
		return matrix4x3f;
	}

	public Matrix4x3f rotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4x3f matrix4x3f) {
		return this.rotateTowards(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), matrix4x3f);
	}

	public Matrix4x3f rotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.rotateTowards(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), this);
	}

	public Matrix4x3f rotateTowards(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.rotateTowards(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix4x3f rotateTowards(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f) {
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
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		float float18 = this.m00 * float11 + this.m10 * float12 + this.m20 * float13;
		float float19 = this.m01 * float11 + this.m11 * float12 + this.m21 * float13;
		float float20 = this.m02 * float11 + this.m12 * float12 + this.m22 * float13;
		float float21 = this.m00 * float15 + this.m10 * float16 + this.m20 * float17;
		float float22 = this.m01 * float15 + this.m11 * float16 + this.m21 * float17;
		float float23 = this.m02 * float15 + this.m12 * float16 + this.m22 * float17;
		matrix4x3f.m20 = this.m00 * float8 + this.m10 * float9 + this.m20 * float10;
		matrix4x3f.m21 = this.m01 * float8 + this.m11 * float9 + this.m21 * float10;
		matrix4x3f.m22 = this.m02 * float8 + this.m12 * float9 + this.m22 * float10;
		matrix4x3f.m00 = float18;
		matrix4x3f.m01 = float19;
		matrix4x3f.m02 = float20;
		matrix4x3f.m10 = float21;
		matrix4x3f.m11 = float22;
		matrix4x3f.m12 = float23;
		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public Matrix4x3f rotationTowards(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.rotationTowards(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix4x3f rotationTowards(float float1, float float2, float float3, float float4, float float5, float float6) {
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
		this.m00 = float11;
		this.m01 = float12;
		this.m02 = float13;
		this.m10 = float15;
		this.m11 = float16;
		this.m12 = float17;
		this.m20 = float8;
		this.m21 = float9;
		this.m22 = float10;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.properties = 16;
		return this;
	}

	public Matrix4x3f translationRotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		return this.translationRotateTowards(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z());
	}

	public Matrix4x3f translationRotateTowards(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
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
		this.m00 = float14;
		this.m01 = float15;
		this.m02 = float16;
		this.m10 = float18;
		this.m11 = float19;
		this.m12 = float20;
		this.m20 = float11;
		this.m21 = float12;
		this.m22 = float13;
		this.m30 = float1;
		this.m31 = float2;
		this.m32 = float3;
		this.properties = 16;
		return this;
	}

	public Vector3f getEulerAnglesZYX(Vector3f vector3f) {
		vector3f.x = Math.atan2(this.m12, this.m22);
		vector3f.y = Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22));
		vector3f.z = Math.atan2(this.m01, this.m00);
		return vector3f;
	}

	public Matrix4x3f obliqueZ(float float1, float float2) {
		this.m20 += this.m00 * float1 + this.m10 * float2;
		this.m21 += this.m01 * float1 + this.m11 * float2;
		this.m22 += this.m02 * float1 + this.m12 * float2;
		this.properties = 0;
		return this;
	}

	public Matrix4x3f obliqueZ(float float1, float float2, Matrix4x3f matrix4x3f) {
		matrix4x3f.m00 = this.m00;
		matrix4x3f.m01 = this.m01;
		matrix4x3f.m02 = this.m02;
		matrix4x3f.m10 = this.m10;
		matrix4x3f.m11 = this.m11;
		matrix4x3f.m12 = this.m12;
		matrix4x3f.m20 = this.m00 * float1 + this.m10 * float2 + this.m20;
		matrix4x3f.m21 = this.m01 * float1 + this.m11 * float2 + this.m21;
		matrix4x3f.m22 = this.m02 * float1 + this.m12 * float2 + this.m22;
		matrix4x3f.m30 = this.m30;
		matrix4x3f.m31 = this.m31;
		matrix4x3f.m32 = this.m32;
		matrix4x3f.properties = 0;
		return matrix4x3f;
	}

	public Matrix4x3f withLookAtUp(Vector3fc vector3fc) {
		return this.withLookAtUp(vector3fc.x(), vector3fc.y(), vector3fc.z(), this);
	}

	public Matrix4x3f withLookAtUp(Vector3fc vector3fc, Matrix4x3f matrix4x3f) {
		return this.withLookAtUp(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix4x3f withLookAtUp(float float1, float float2, float float3) {
		return this.withLookAtUp(float1, float2, float3, this);
	}

	public Matrix4x3f withLookAtUp(float float1, float float2, float float3, Matrix4x3f matrix4x3f) {
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
		matrix4x3f._m00(float9)._m10(float10)._m20(float11)._m30(float16)._m01(float13)._m11(float14)._m21(float15)._m31(float12);
		if (matrix4x3f != this) {
			matrix4x3f._m02(this.m02)._m12(this.m12)._m22(this.m22)._m32(this.m32);
		}

		matrix4x3f.properties = this.properties & -13;
		return matrix4x3f;
	}

	public boolean isFinite() {
		return Math.isFinite(this.m00) && Math.isFinite(this.m01) && Math.isFinite(this.m02) && Math.isFinite(this.m10) && Math.isFinite(this.m11) && Math.isFinite(this.m12) && Math.isFinite(this.m20) && Math.isFinite(this.m21) && Math.isFinite(this.m22) && Math.isFinite(this.m30) && Math.isFinite(this.m31) && Math.isFinite(this.m32);
	}
}
