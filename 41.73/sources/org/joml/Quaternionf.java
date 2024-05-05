package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;


public class Quaternionf implements Externalizable,Quaternionfc {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;
	public float z;
	public float w;

	public Quaternionf() {
		this.w = 1.0F;
	}

	public Quaternionf(double double1, double double2, double double3, double double4) {
		this.x = (float)double1;
		this.y = (float)double2;
		this.z = (float)double3;
		this.w = (float)double4;
	}

	public Quaternionf(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
	}

	public Quaternionf(Quaternionfc quaternionfc) {
		this.set(quaternionfc);
	}

	public Quaternionf(Quaterniondc quaterniondc) {
		this.set(quaterniondc);
	}

	public Quaternionf(AxisAngle4f axisAngle4f) {
		float float1 = Math.sin(axisAngle4f.angle * 0.5F);
		float float2 = Math.cosFromSin(float1, axisAngle4f.angle * 0.5F);
		this.x = axisAngle4f.x * float1;
		this.y = axisAngle4f.y * float1;
		this.z = axisAngle4f.z * float1;
		this.w = float2;
	}

	public Quaternionf(AxisAngle4d axisAngle4d) {
		double double1 = Math.sin(axisAngle4d.angle * 0.5);
		double double2 = Math.cosFromSin(double1, axisAngle4d.angle * 0.5);
		this.x = (float)(axisAngle4d.x * double1);
		this.y = (float)(axisAngle4d.y * double1);
		this.z = (float)(axisAngle4d.z * double1);
		this.w = (float)double2;
	}

	public float x() {
		return this.x;
	}

	public float y() {
		return this.y;
	}

	public float z() {
		return this.z;
	}

	public float w() {
		return this.w;
	}

	public Quaternionf normalize() {
		return this.normalize(this);
	}

	public Quaternionf normalize(Quaternionf quaternionf) {
		float float1 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w))));
		quaternionf.x = this.x * float1;
		quaternionf.y = this.y * float1;
		quaternionf.z = this.z * float1;
		quaternionf.w = this.w * float1;
		return quaternionf;
	}

	public Quaternionf add(float float1, float float2, float float3, float float4) {
		return this.add(float1, float2, float3, float4, this);
	}

	public Quaternionf add(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
		quaternionf.x = this.x + float1;
		quaternionf.y = this.y + float2;
		quaternionf.z = this.z + float3;
		quaternionf.w = this.w + float4;
		return quaternionf;
	}

	public Quaternionf add(Quaternionfc quaternionfc) {
		return this.add(quaternionfc, this);
	}

	public Quaternionf add(Quaternionfc quaternionfc, Quaternionf quaternionf) {
		quaternionf.x = this.x + quaternionfc.x();
		quaternionf.y = this.y + quaternionfc.y();
		quaternionf.z = this.z + quaternionfc.z();
		quaternionf.w = this.w + quaternionfc.w();
		return quaternionf;
	}

	public float dot(Quaternionf quaternionf) {
		return this.x * quaternionf.x + this.y * quaternionf.y + this.z * quaternionf.z + this.w * quaternionf.w;
	}

	public float angle() {
		return (float)(2.0 * (double)Math.safeAcos(this.w));
	}

	public Matrix3f get(Matrix3f matrix3f) {
		return matrix3f.set((Quaternionfc)this);
	}

	public Matrix3d get(Matrix3d matrix3d) {
		return matrix3d.set((Quaternionfc)this);
	}

	public Matrix4f get(Matrix4f matrix4f) {
		return matrix4f.set((Quaternionfc)this);
	}

	public Matrix4d get(Matrix4d matrix4d) {
		return matrix4d.set((Quaternionfc)this);
	}

	public Matrix4x3f get(Matrix4x3f matrix4x3f) {
		return matrix4x3f.set((Quaternionfc)this);
	}

	public Matrix4x3d get(Matrix4x3d matrix4x3d) {
		return matrix4x3d.set((Quaternionfc)this);
	}

	public AxisAngle4f get(AxisAngle4f axisAngle4f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		float float5;
		if (float4 > 1.0F) {
			float5 = Math.invsqrt(Math.fma(float1, float1, Math.fma(float2, float2, Math.fma(float3, float3, float4 * float4))));
			float1 *= float5;
			float2 *= float5;
			float3 *= float5;
			float4 *= float5;
		}

		axisAngle4f.angle = 2.0F * Math.acos(float4);
		float5 = Math.sqrt(1.0F - float4 * float4);
		if (float5 < 0.001F) {
			axisAngle4f.x = float1;
			axisAngle4f.y = float2;
			axisAngle4f.z = float3;
		} else {
			float5 = 1.0F / float5;
			axisAngle4f.x = float1 * float5;
			axisAngle4f.y = float2 * float5;
			axisAngle4f.z = float3 * float5;
		}

		return axisAngle4f;
	}

	public AxisAngle4d get(AxisAngle4d axisAngle4d) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		float float5;
		if (float4 > 1.0F) {
			float5 = Math.invsqrt(Math.fma(float1, float1, Math.fma(float2, float2, Math.fma(float3, float3, float4 * float4))));
			float1 *= float5;
			float2 *= float5;
			float3 *= float5;
			float4 *= float5;
		}

		axisAngle4d.angle = (double)(2.0F * Math.acos(float4));
		float5 = Math.sqrt(1.0F - float4 * float4);
		if (float5 < 0.001F) {
			axisAngle4d.x = (double)float1;
			axisAngle4d.y = (double)float2;
			axisAngle4d.z = (double)float3;
		} else {
			float5 = 1.0F / float5;
			axisAngle4d.x = (double)(float1 * float5);
			axisAngle4d.y = (double)(float2 * float5);
			axisAngle4d.z = (double)(float3 * float5);
		}

		return axisAngle4d;
	}

	public Quaterniond get(Quaterniond quaterniond) {
		return quaterniond.set((Quaternionfc)this);
	}

	public Quaternionf get(Quaternionf quaternionf) {
		return quaternionf.set((Quaternionfc)this);
	}

	public ByteBuffer getAsMatrix3f(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putMatrix3f(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer getAsMatrix3f(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.putMatrix3f(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer getAsMatrix4f(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putMatrix4f(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer getAsMatrix4f(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.putMatrix4f(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer getAsMatrix4x3f(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putMatrix4x3f(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer getAsMatrix4x3f(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.putMatrix4x3f(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public Quaternionf set(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
		return this;
	}

	public Quaternionf set(Quaternionfc quaternionfc) {
		this.x = quaternionfc.x();
		this.y = quaternionfc.y();
		this.z = quaternionfc.z();
		this.w = quaternionfc.w();
		return this;
	}

	public Quaternionf set(Quaterniondc quaterniondc) {
		this.x = (float)quaterniondc.x();
		this.y = (float)quaterniondc.y();
		this.z = (float)quaterniondc.z();
		this.w = (float)quaterniondc.w();
		return this;
	}

	public Quaternionf set(AxisAngle4f axisAngle4f) {
		return this.setAngleAxis(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z);
	}

	public Quaternionf set(AxisAngle4d axisAngle4d) {
		return this.setAngleAxis(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z);
	}

	public Quaternionf setAngleAxis(float float1, float float2, float float3, float float4) {
		float float5 = Math.sin(float1 * 0.5F);
		this.x = float2 * float5;
		this.y = float3 * float5;
		this.z = float4 * float5;
		this.w = Math.cosFromSin(float5, float1 * 0.5F);
		return this;
	}

	public Quaternionf setAngleAxis(double double1, double double2, double double3, double double4) {
		double double5 = Math.sin(double1 * 0.5);
		this.x = (float)(double2 * double5);
		this.y = (float)(double3 * double5);
		this.z = (float)(double4 * double5);
		this.w = (float)Math.cosFromSin(double5, double1 * 0.5);
		return this;
	}

	public Quaternionf rotationAxis(AxisAngle4f axisAngle4f) {
		return this.rotationAxis(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z);
	}

	public Quaternionf rotationAxis(float float1, float float2, float float3, float float4) {
		float float5 = float1 / 2.0F;
		float float6 = Math.sin(float5);
		float float7 = Math.invsqrt(float2 * float2 + float3 * float3 + float4 * float4);
		return this.set(float2 * float7 * float6, float3 * float7 * float6, float4 * float7 * float6, Math.cosFromSin(float6, float5));
	}

	public Quaternionf rotationAxis(float float1, Vector3fc vector3fc) {
		return this.rotationAxis(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Quaternionf rotationX(float float1) {
		float float2 = Math.sin(float1 * 0.5F);
		float float3 = Math.cosFromSin(float2, float1 * 0.5F);
		return this.set(float2, 0.0F, 0.0F, float3);
	}

	public Quaternionf rotationY(float float1) {
		float float2 = Math.sin(float1 * 0.5F);
		float float3 = Math.cosFromSin(float2, float1 * 0.5F);
		return this.set(0.0F, float2, 0.0F, float3);
	}

	public Quaternionf rotationZ(float float1) {
		float float2 = Math.sin(float1 * 0.5F);
		float float3 = Math.cosFromSin(float2, float1 * 0.5F);
		return this.set(0.0F, 0.0F, float2, float3);
	}

	private void setFromUnnormalized(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		float float10 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float float11 = Math.invsqrt(float4 * float4 + float5 * float5 + float6 * float6);
		float float12 = Math.invsqrt(float7 * float7 + float8 * float8 + float9 * float9);
		float float13 = float1 * float10;
		float float14 = float2 * float10;
		float float15 = float3 * float10;
		float float16 = float4 * float11;
		float float17 = float5 * float11;
		float float18 = float6 * float11;
		float float19 = float7 * float12;
		float float20 = float8 * float12;
		float float21 = float9 * float12;
		this.setFromNormalized(float13, float14, float15, float16, float17, float18, float19, float20, float21);
	}

	private void setFromNormalized(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		float float10 = float1 + float5 + float9;
		float float11;
		if (float10 >= 0.0F) {
			float11 = Math.sqrt(float10 + 1.0F);
			this.w = float11 * 0.5F;
			float11 = 0.5F / float11;
			this.x = (float6 - float8) * float11;
			this.y = (float7 - float3) * float11;
			this.z = (float2 - float4) * float11;
		} else if (float1 >= float5 && float1 >= float9) {
			float11 = Math.sqrt(float1 - (float5 + float9) + 1.0F);
			this.x = float11 * 0.5F;
			float11 = 0.5F / float11;
			this.y = (float4 + float2) * float11;
			this.z = (float3 + float7) * float11;
			this.w = (float6 - float8) * float11;
		} else if (float5 > float9) {
			float11 = Math.sqrt(float5 - (float9 + float1) + 1.0F);
			this.y = float11 * 0.5F;
			float11 = 0.5F / float11;
			this.z = (float8 + float6) * float11;
			this.x = (float4 + float2) * float11;
			this.w = (float7 - float3) * float11;
		} else {
			float11 = Math.sqrt(float9 - (float1 + float5) + 1.0F);
			this.z = float11 * 0.5F;
			float11 = 0.5F / float11;
			this.x = (float3 + float7) * float11;
			this.y = (float8 + float6) * float11;
			this.w = (float2 - float4) * float11;
		}
	}

	private void setFromUnnormalized(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double11 = Math.invsqrt(double4 * double4 + double5 * double5 + double6 * double6);
		double double12 = Math.invsqrt(double7 * double7 + double8 * double8 + double9 * double9);
		double double13 = double1 * double10;
		double double14 = double2 * double10;
		double double15 = double3 * double10;
		double double16 = double4 * double11;
		double double17 = double5 * double11;
		double double18 = double6 * double11;
		double double19 = double7 * double12;
		double double20 = double8 * double12;
		double double21 = double9 * double12;
		this.setFromNormalized(double13, double14, double15, double16, double17, double18, double19, double20, double21);
	}

	private void setFromNormalized(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = double1 + double5 + double9;
		double double11;
		if (double10 >= 0.0) {
			double11 = Math.sqrt(double10 + 1.0);
			this.w = (float)(double11 * 0.5);
			double11 = 0.5 / double11;
			this.x = (float)((double6 - double8) * double11);
			this.y = (float)((double7 - double3) * double11);
			this.z = (float)((double2 - double4) * double11);
		} else if (double1 >= double5 && double1 >= double9) {
			double11 = Math.sqrt(double1 - (double5 + double9) + 1.0);
			this.x = (float)(double11 * 0.5);
			double11 = 0.5 / double11;
			this.y = (float)((double4 + double2) * double11);
			this.z = (float)((double3 + double7) * double11);
			this.w = (float)((double6 - double8) * double11);
		} else if (double5 > double9) {
			double11 = Math.sqrt(double5 - (double9 + double1) + 1.0);
			this.y = (float)(double11 * 0.5);
			double11 = 0.5 / double11;
			this.z = (float)((double8 + double6) * double11);
			this.x = (float)((double4 + double2) * double11);
			this.w = (float)((double7 - double3) * double11);
		} else {
			double11 = Math.sqrt(double9 - (double1 + double5) + 1.0);
			this.z = (float)(double11 * 0.5);
			double11 = 0.5 / double11;
			this.x = (float)((double3 + double7) * double11);
			this.y = (float)((double8 + double6) * double11);
			this.w = (float)((double2 - double4) * double11);
		}
	}

	public Quaternionf setFromUnnormalized(Matrix4fc matrix4fc) {
		this.setFromUnnormalized(matrix4fc.m00(), matrix4fc.m01(), matrix4fc.m02(), matrix4fc.m10(), matrix4fc.m11(), matrix4fc.m12(), matrix4fc.m20(), matrix4fc.m21(), matrix4fc.m22());
		return this;
	}

	public Quaternionf setFromUnnormalized(Matrix4x3fc matrix4x3fc) {
		this.setFromUnnormalized(matrix4x3fc.m00(), matrix4x3fc.m01(), matrix4x3fc.m02(), matrix4x3fc.m10(), matrix4x3fc.m11(), matrix4x3fc.m12(), matrix4x3fc.m20(), matrix4x3fc.m21(), matrix4x3fc.m22());
		return this;
	}

	public Quaternionf setFromUnnormalized(Matrix4x3dc matrix4x3dc) {
		this.setFromUnnormalized(matrix4x3dc.m00(), matrix4x3dc.m01(), matrix4x3dc.m02(), matrix4x3dc.m10(), matrix4x3dc.m11(), matrix4x3dc.m12(), matrix4x3dc.m20(), matrix4x3dc.m21(), matrix4x3dc.m22());
		return this;
	}

	public Quaternionf setFromNormalized(Matrix4fc matrix4fc) {
		this.setFromNormalized(matrix4fc.m00(), matrix4fc.m01(), matrix4fc.m02(), matrix4fc.m10(), matrix4fc.m11(), matrix4fc.m12(), matrix4fc.m20(), matrix4fc.m21(), matrix4fc.m22());
		return this;
	}

	public Quaternionf setFromNormalized(Matrix4x3fc matrix4x3fc) {
		this.setFromNormalized(matrix4x3fc.m00(), matrix4x3fc.m01(), matrix4x3fc.m02(), matrix4x3fc.m10(), matrix4x3fc.m11(), matrix4x3fc.m12(), matrix4x3fc.m20(), matrix4x3fc.m21(), matrix4x3fc.m22());
		return this;
	}

	public Quaternionf setFromNormalized(Matrix4x3dc matrix4x3dc) {
		this.setFromNormalized(matrix4x3dc.m00(), matrix4x3dc.m01(), matrix4x3dc.m02(), matrix4x3dc.m10(), matrix4x3dc.m11(), matrix4x3dc.m12(), matrix4x3dc.m20(), matrix4x3dc.m21(), matrix4x3dc.m22());
		return this;
	}

	public Quaternionf setFromUnnormalized(Matrix4dc matrix4dc) {
		this.setFromUnnormalized(matrix4dc.m00(), matrix4dc.m01(), matrix4dc.m02(), matrix4dc.m10(), matrix4dc.m11(), matrix4dc.m12(), matrix4dc.m20(), matrix4dc.m21(), matrix4dc.m22());
		return this;
	}

	public Quaternionf setFromNormalized(Matrix4dc matrix4dc) {
		this.setFromNormalized(matrix4dc.m00(), matrix4dc.m01(), matrix4dc.m02(), matrix4dc.m10(), matrix4dc.m11(), matrix4dc.m12(), matrix4dc.m20(), matrix4dc.m21(), matrix4dc.m22());
		return this;
	}

	public Quaternionf setFromUnnormalized(Matrix3fc matrix3fc) {
		this.setFromUnnormalized(matrix3fc.m00(), matrix3fc.m01(), matrix3fc.m02(), matrix3fc.m10(), matrix3fc.m11(), matrix3fc.m12(), matrix3fc.m20(), matrix3fc.m21(), matrix3fc.m22());
		return this;
	}

	public Quaternionf setFromNormalized(Matrix3fc matrix3fc) {
		this.setFromNormalized(matrix3fc.m00(), matrix3fc.m01(), matrix3fc.m02(), matrix3fc.m10(), matrix3fc.m11(), matrix3fc.m12(), matrix3fc.m20(), matrix3fc.m21(), matrix3fc.m22());
		return this;
	}

	public Quaternionf setFromUnnormalized(Matrix3dc matrix3dc) {
		this.setFromUnnormalized(matrix3dc.m00(), matrix3dc.m01(), matrix3dc.m02(), matrix3dc.m10(), matrix3dc.m11(), matrix3dc.m12(), matrix3dc.m20(), matrix3dc.m21(), matrix3dc.m22());
		return this;
	}

	public Quaternionf setFromNormalized(Matrix3dc matrix3dc) {
		this.setFromNormalized(matrix3dc.m00(), matrix3dc.m01(), matrix3dc.m02(), matrix3dc.m10(), matrix3dc.m11(), matrix3dc.m12(), matrix3dc.m20(), matrix3dc.m21(), matrix3dc.m22());
		return this;
	}

	public Quaternionf fromAxisAngleRad(Vector3fc vector3fc, float float1) {
		return this.fromAxisAngleRad(vector3fc.x(), vector3fc.y(), vector3fc.z(), float1);
	}

	public Quaternionf fromAxisAngleRad(float float1, float float2, float float3, float float4) {
		float float5 = float4 / 2.0F;
		float float6 = Math.sin(float5);
		float float7 = Math.sqrt(float1 * float1 + float2 * float2 + float3 * float3);
		this.x = float1 / float7 * float6;
		this.y = float2 / float7 * float6;
		this.z = float3 / float7 * float6;
		this.w = Math.cosFromSin(float6, float5);
		return this;
	}

	public Quaternionf fromAxisAngleDeg(Vector3fc vector3fc, float float1) {
		return this.fromAxisAngleRad(vector3fc.x(), vector3fc.y(), vector3fc.z(), Math.toRadians(float1));
	}

	public Quaternionf fromAxisAngleDeg(float float1, float float2, float float3, float float4) {
		return this.fromAxisAngleRad(float1, float2, float3, Math.toRadians(float4));
	}

	public Quaternionf mul(Quaternionfc quaternionfc) {
		return this.mul(quaternionfc, this);
	}

	public Quaternionf mul(Quaternionfc quaternionfc, Quaternionf quaternionf) {
		return quaternionf.set(Math.fma(this.w, quaternionfc.x(), Math.fma(this.x, quaternionfc.w(), Math.fma(this.y, quaternionfc.z(), -this.z * quaternionfc.y()))), Math.fma(this.w, quaternionfc.y(), Math.fma(-this.x, quaternionfc.z(), Math.fma(this.y, quaternionfc.w(), this.z * quaternionfc.x()))), Math.fma(this.w, quaternionfc.z(), Math.fma(this.x, quaternionfc.y(), Math.fma(-this.y, quaternionfc.x(), this.z * quaternionfc.w()))), Math.fma(this.w, quaternionfc.w(), Math.fma(-this.x, quaternionfc.x(), Math.fma(-this.y, quaternionfc.y(), -this.z * quaternionfc.z()))));
	}

	public Quaternionf mul(float float1, float float2, float float3, float float4) {
		return this.mul(float1, float2, float3, float4, this);
	}

	public Quaternionf mul(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
		return quaternionf.set(Math.fma(this.w, float1, Math.fma(this.x, float4, Math.fma(this.y, float3, -this.z * float2))), Math.fma(this.w, float2, Math.fma(-this.x, float3, Math.fma(this.y, float4, this.z * float1))), Math.fma(this.w, float3, Math.fma(this.x, float2, Math.fma(-this.y, float1, this.z * float4))), Math.fma(this.w, float4, Math.fma(-this.x, float1, Math.fma(-this.y, float2, -this.z * float3))));
	}

	public Quaternionf premul(Quaternionfc quaternionfc) {
		return this.premul(quaternionfc, this);
	}

	public Quaternionf premul(Quaternionfc quaternionfc, Quaternionf quaternionf) {
		return quaternionf.set(Math.fma(quaternionfc.w(), this.x, Math.fma(quaternionfc.x(), this.w, Math.fma(quaternionfc.y(), this.z, -quaternionfc.z() * this.y))), Math.fma(quaternionfc.w(), this.y, Math.fma(-quaternionfc.x(), this.z, Math.fma(quaternionfc.y(), this.w, quaternionfc.z() * this.x))), Math.fma(quaternionfc.w(), this.z, Math.fma(quaternionfc.x(), this.y, Math.fma(-quaternionfc.y(), this.x, quaternionfc.z() * this.w))), Math.fma(quaternionfc.w(), this.w, Math.fma(-quaternionfc.x(), this.x, Math.fma(-quaternionfc.y(), this.y, -quaternionfc.z() * this.z))));
	}

	public Quaternionf premul(float float1, float float2, float float3, float float4) {
		return this.premul(float1, float2, float3, float4, this);
	}

	public Quaternionf premul(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
		return quaternionf.set(Math.fma(float4, this.x, Math.fma(float1, this.w, Math.fma(float2, this.z, -float3 * this.y))), Math.fma(float4, this.y, Math.fma(-float1, this.z, Math.fma(float2, this.w, float3 * this.x))), Math.fma(float4, this.z, Math.fma(float1, this.y, Math.fma(-float2, this.x, float3 * this.w))), Math.fma(float4, this.w, Math.fma(-float1, this.x, Math.fma(-float2, this.y, -float3 * this.z))));
	}

	public Vector3f transform(Vector3f vector3f) {
		return this.transform(vector3f.x, vector3f.y, vector3f.z, vector3f);
	}

	public Vector3f transformInverse(Vector3f vector3f) {
		return this.transformInverse(vector3f.x, vector3f.y, vector3f.z, vector3f);
	}

	public Vector3f transformPositiveX(Vector3f vector3f) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.z * this.w;
		float float6 = this.x * this.y;
		float float7 = this.x * this.z;
		float float8 = this.y * this.w;
		vector3f.x = float1 + float2 - float4 - float3;
		vector3f.y = float6 + float5 + float5 + float6;
		vector3f.z = float7 - float8 + float7 - float8;
		return vector3f;
	}

	public Vector4f transformPositiveX(Vector4f vector4f) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.z * this.w;
		float float6 = this.x * this.y;
		float float7 = this.x * this.z;
		float float8 = this.y * this.w;
		vector4f.x = float1 + float2 - float4 - float3;
		vector4f.y = float6 + float5 + float5 + float6;
		vector4f.z = float7 - float8 + float7 - float8;
		return vector4f;
	}

	public Vector3f transformUnitPositiveX(Vector3f vector3f) {
		float float1 = this.x * this.y;
		float float2 = this.x * this.z;
		float float3 = this.y * this.y;
		float float4 = this.y * this.w;
		float float5 = this.z * this.z;
		float float6 = this.z * this.w;
		vector3f.x = 1.0F - float3 - float5 - float3 - float5;
		vector3f.y = float1 + float6 + float1 + float6;
		vector3f.z = float2 - float4 + float2 - float4;
		return vector3f;
	}

	public Vector4f transformUnitPositiveX(Vector4f vector4f) {
		float float1 = this.y * this.y;
		float float2 = this.z * this.z;
		float float3 = this.x * this.y;
		float float4 = this.x * this.z;
		float float5 = this.y * this.w;
		float float6 = this.z * this.w;
		vector4f.x = 1.0F - float1 - float1 - float2 - float2;
		vector4f.y = float3 + float6 + float3 + float6;
		vector4f.z = float4 - float5 + float4 - float5;
		return vector4f;
	}

	public Vector3f transformPositiveY(Vector3f vector3f) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.z * this.w;
		float float6 = this.x * this.y;
		float float7 = this.y * this.z;
		float float8 = this.x * this.w;
		vector3f.x = -float5 + float6 - float5 + float6;
		vector3f.y = float3 - float4 + float1 - float2;
		vector3f.z = float7 + float7 + float8 + float8;
		return vector3f;
	}

	public Vector4f transformPositiveY(Vector4f vector4f) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.z * this.w;
		float float6 = this.x * this.y;
		float float7 = this.y * this.z;
		float float8 = this.x * this.w;
		vector4f.x = -float5 + float6 - float5 + float6;
		vector4f.y = float3 - float4 + float1 - float2;
		vector4f.z = float7 + float7 + float8 + float8;
		return vector4f;
	}

	public Vector4f transformUnitPositiveY(Vector4f vector4f) {
		float float1 = this.x * this.x;
		float float2 = this.z * this.z;
		float float3 = this.x * this.y;
		float float4 = this.y * this.z;
		float float5 = this.x * this.w;
		float float6 = this.z * this.w;
		vector4f.x = float3 - float6 + float3 - float6;
		vector4f.y = 1.0F - float1 - float1 - float2 - float2;
		vector4f.z = float4 + float4 + float5 + float5;
		return vector4f;
	}

	public Vector3f transformUnitPositiveY(Vector3f vector3f) {
		float float1 = this.x * this.x;
		float float2 = this.z * this.z;
		float float3 = this.x * this.y;
		float float4 = this.y * this.z;
		float float5 = this.x * this.w;
		float float6 = this.z * this.w;
		vector3f.x = float3 - float6 + float3 - float6;
		vector3f.y = 1.0F - float1 - float1 - float2 - float2;
		vector3f.z = float4 + float4 + float5 + float5;
		return vector3f;
	}

	public Vector3f transformPositiveZ(Vector3f vector3f) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.x * this.z;
		float float6 = this.y * this.w;
		float float7 = this.y * this.z;
		float float8 = this.x * this.w;
		vector3f.x = float6 + float5 + float5 + float6;
		vector3f.y = float7 + float7 - float8 - float8;
		vector3f.z = float4 - float3 - float2 + float1;
		return vector3f;
	}

	public Vector4f transformPositiveZ(Vector4f vector4f) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.x * this.z;
		float float6 = this.y * this.w;
		float float7 = this.y * this.z;
		float float8 = this.x * this.w;
		vector4f.x = float6 + float5 + float5 + float6;
		vector4f.y = float7 + float7 - float8 - float8;
		vector4f.z = float4 - float3 - float2 + float1;
		return vector4f;
	}

	public Vector4f transformUnitPositiveZ(Vector4f vector4f) {
		float float1 = this.x * this.x;
		float float2 = this.y * this.y;
		float float3 = this.x * this.z;
		float float4 = this.y * this.z;
		float float5 = this.x * this.w;
		float float6 = this.y * this.w;
		vector4f.x = float3 + float6 + float3 + float6;
		vector4f.y = float4 + float4 - float5 - float5;
		vector4f.z = 1.0F - float1 - float1 - float2 - float2;
		return vector4f;
	}

	public Vector3f transformUnitPositiveZ(Vector3f vector3f) {
		float float1 = this.x * this.x;
		float float2 = this.y * this.y;
		float float3 = this.x * this.z;
		float float4 = this.y * this.z;
		float float5 = this.x * this.w;
		float float6 = this.y * this.w;
		vector3f.x = float3 + float6 + float3 + float6;
		vector3f.y = float4 + float4 - float5 - float5;
		vector3f.z = 1.0F - float1 - float1 - float2 - float2;
		return vector3f;
	}

	public Vector4f transform(Vector4f vector4f) {
		return this.transform((Vector4fc)vector4f, (Vector4f)vector4f);
	}

	public Vector4f transformInverse(Vector4f vector4f) {
		return this.transformInverse((Vector4fc)vector4f, (Vector4f)vector4f);
	}

	public Vector3f transform(Vector3fc vector3fc, Vector3f vector3f) {
		return this.transform(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3f);
	}

	public Vector3f transformInverse(Vector3fc vector3fc, Vector3f vector3f) {
		return this.transformInverse(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3f);
	}

	public Vector3f transform(float float1, float float2, float float3, Vector3f vector3f) {
		float float4 = this.x * this.x;
		float float5 = this.y * this.y;
		float float6 = this.z * this.z;
		float float7 = this.w * this.w;
		float float8 = this.x * this.y;
		float float9 = this.x * this.z;
		float float10 = this.y * this.z;
		float float11 = this.x * this.w;
		float float12 = this.z * this.w;
		float float13 = this.y * this.w;
		float float14 = 1.0F / (float4 + float5 + float6 + float7);
		return vector3f.set(Math.fma((float4 - float5 - float6 + float7) * float14, float1, Math.fma(2.0F * (float8 - float12) * float14, float2, 2.0F * (float9 + float13) * float14 * float3)), Math.fma(2.0F * (float8 + float12) * float14, float1, Math.fma((float5 - float4 - float6 + float7) * float14, float2, 2.0F * (float10 - float11) * float14 * float3)), Math.fma(2.0F * (float9 - float13) * float14, float1, Math.fma(2.0F * (float10 + float11) * float14, float2, (float6 - float4 - float5 + float7) * float14 * float3)));
	}

	public Vector3f transformInverse(float float1, float float2, float float3, Vector3f vector3f) {
		float float4 = 1.0F / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
		float float5 = this.x * float4;
		float float6 = this.y * float4;
		float float7 = this.z * float4;
		float float8 = this.w * float4;
		float float9 = float5 * float5;
		float float10 = float6 * float6;
		float float11 = float7 * float7;
		float float12 = float8 * float8;
		float float13 = float5 * float6;
		float float14 = float5 * float7;
		float float15 = float6 * float7;
		float float16 = float5 * float8;
		float float17 = float7 * float8;
		float float18 = float6 * float8;
		float float19 = 1.0F / (float9 + float10 + float11 + float12);
		return vector3f.set(Math.fma((float9 - float10 - float11 + float12) * float19, float1, Math.fma(2.0F * (float13 + float17) * float19, float2, 2.0F * (float14 - float18) * float19 * float3)), Math.fma(2.0F * (float13 - float17) * float19, float1, Math.fma((float10 - float9 - float11 + float12) * float19, float2, 2.0F * (float15 + float16) * float19 * float3)), Math.fma(2.0F * (float14 + float18) * float19, float1, Math.fma(2.0F * (float15 - float16) * float19, float2, (float11 - float9 - float10 + float12) * float19 * float3)));
	}

	public Vector3f transformUnit(Vector3f vector3f) {
		return this.transformUnit(vector3f.x, vector3f.y, vector3f.z, vector3f);
	}

	public Vector3f transformInverseUnit(Vector3f vector3f) {
		return this.transformInverseUnit(vector3f.x, vector3f.y, vector3f.z, vector3f);
	}

	public Vector3f transformUnit(Vector3fc vector3fc, Vector3f vector3f) {
		return this.transformUnit(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3f);
	}

	public Vector3f transformInverseUnit(Vector3fc vector3fc, Vector3f vector3f) {
		return this.transformInverseUnit(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3f);
	}

	public Vector3f transformUnit(float float1, float float2, float float3, Vector3f vector3f) {
		float float4 = this.x * this.x;
		float float5 = this.x * this.y;
		float float6 = this.x * this.z;
		float float7 = this.x * this.w;
		float float8 = this.y * this.y;
		float float9 = this.y * this.z;
		float float10 = this.y * this.w;
		float float11 = this.z * this.z;
		float float12 = this.z * this.w;
		return vector3f.set(Math.fma(Math.fma(-2.0F, float8 + float11, 1.0F), float1, Math.fma(2.0F * (float5 - float12), float2, 2.0F * (float6 + float10) * float3)), Math.fma(2.0F * (float5 + float12), float1, Math.fma(Math.fma(-2.0F, float4 + float11, 1.0F), float2, 2.0F * (float9 - float7) * float3)), Math.fma(2.0F * (float6 - float10), float1, Math.fma(2.0F * (float9 + float7), float2, Math.fma(-2.0F, float4 + float8, 1.0F) * float3)));
	}

	public Vector3f transformInverseUnit(float float1, float float2, float float3, Vector3f vector3f) {
		float float4 = this.x * this.x;
		float float5 = this.x * this.y;
		float float6 = this.x * this.z;
		float float7 = this.x * this.w;
		float float8 = this.y * this.y;
		float float9 = this.y * this.z;
		float float10 = this.y * this.w;
		float float11 = this.z * this.z;
		float float12 = this.z * this.w;
		return vector3f.set(Math.fma(Math.fma(-2.0F, float8 + float11, 1.0F), float1, Math.fma(2.0F * (float5 + float12), float2, 2.0F * (float6 - float10) * float3)), Math.fma(2.0F * (float5 - float12), float1, Math.fma(Math.fma(-2.0F, float4 + float11, 1.0F), float2, 2.0F * (float9 + float7) * float3)), Math.fma(2.0F * (float6 + float10), float1, Math.fma(2.0F * (float9 - float7), float2, Math.fma(-2.0F, float4 + float8, 1.0F) * float3)));
	}

	public Vector4f transform(Vector4fc vector4fc, Vector4f vector4f) {
		return this.transform(vector4fc.x(), vector4fc.y(), vector4fc.z(), vector4f);
	}

	public Vector4f transformInverse(Vector4fc vector4fc, Vector4f vector4f) {
		return this.transformInverse(vector4fc.x(), vector4fc.y(), vector4fc.z(), vector4f);
	}

	public Vector4f transform(float float1, float float2, float float3, Vector4f vector4f) {
		float float4 = this.x * this.x;
		float float5 = this.y * this.y;
		float float6 = this.z * this.z;
		float float7 = this.w * this.w;
		float float8 = this.x * this.y;
		float float9 = this.x * this.z;
		float float10 = this.y * this.z;
		float float11 = this.x * this.w;
		float float12 = this.z * this.w;
		float float13 = this.y * this.w;
		float float14 = 1.0F / (float4 + float5 + float6 + float7);
		return vector4f.set(Math.fma((float4 - float5 - float6 + float7) * float14, float1, Math.fma(2.0F * (float8 - float12) * float14, float2, 2.0F * (float9 + float13) * float14 * float3)), Math.fma(2.0F * (float8 + float12) * float14, float1, Math.fma((float5 - float4 - float6 + float7) * float14, float2, 2.0F * (float10 - float11) * float14 * float3)), Math.fma(2.0F * (float9 - float13) * float14, float1, Math.fma(2.0F * (float10 + float11) * float14, float2, (float6 - float4 - float5 + float7) * float14 * float3)));
	}

	public Vector4f transformInverse(float float1, float float2, float float3, Vector4f vector4f) {
		float float4 = 1.0F / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
		float float5 = this.x * float4;
		float float6 = this.y * float4;
		float float7 = this.z * float4;
		float float8 = this.w * float4;
		float float9 = float5 * float5;
		float float10 = float6 * float6;
		float float11 = float7 * float7;
		float float12 = float8 * float8;
		float float13 = float5 * float6;
		float float14 = float5 * float7;
		float float15 = float6 * float7;
		float float16 = float5 * float8;
		float float17 = float7 * float8;
		float float18 = float6 * float8;
		float float19 = 1.0F / (float9 + float10 + float11 + float12);
		return vector4f.set(Math.fma((float9 - float10 - float11 + float12) * float19, float1, Math.fma(2.0F * (float13 + float17) * float19, float2, 2.0F * (float14 - float18) * float19 * float3)), Math.fma(2.0F * (float13 - float17) * float19, float1, Math.fma((float10 - float9 - float11 + float12) * float19, float2, 2.0F * (float15 + float16) * float19 * float3)), Math.fma(2.0F * (float14 + float18) * float19, float1, Math.fma(2.0F * (float15 - float16) * float19, float2, (float11 - float9 - float10 + float12) * float19 * float3)));
	}

	public Vector3d transform(Vector3d vector3d) {
		return this.transform(vector3d.x, vector3d.y, vector3d.z, vector3d);
	}

	public Vector3d transformInverse(Vector3d vector3d) {
		return this.transformInverse(vector3d.x, vector3d.y, vector3d.z, vector3d);
	}

	public Vector4f transformUnit(Vector4f vector4f) {
		return this.transformUnit(vector4f.x, vector4f.y, vector4f.z, vector4f);
	}

	public Vector4f transformInverseUnit(Vector4f vector4f) {
		return this.transformInverseUnit(vector4f.x, vector4f.y, vector4f.z, vector4f);
	}

	public Vector4f transformUnit(Vector4fc vector4fc, Vector4f vector4f) {
		return this.transformUnit(vector4fc.x(), vector4fc.y(), vector4fc.z(), vector4f);
	}

	public Vector4f transformInverseUnit(Vector4fc vector4fc, Vector4f vector4f) {
		return this.transformInverseUnit(vector4fc.x(), vector4fc.y(), vector4fc.z(), vector4f);
	}

	public Vector4f transformUnit(float float1, float float2, float float3, Vector4f vector4f) {
		float float4 = this.x * this.x;
		float float5 = this.x * this.y;
		float float6 = this.x * this.z;
		float float7 = this.x * this.w;
		float float8 = this.y * this.y;
		float float9 = this.y * this.z;
		float float10 = this.y * this.w;
		float float11 = this.z * this.z;
		float float12 = this.z * this.w;
		return vector4f.set(Math.fma(Math.fma(-2.0F, float8 + float11, 1.0F), float1, Math.fma(2.0F * (float5 - float12), float2, 2.0F * (float6 + float10) * float3)), Math.fma(2.0F * (float5 + float12), float1, Math.fma(Math.fma(-2.0F, float4 + float11, 1.0F), float2, 2.0F * (float9 - float7) * float3)), Math.fma(2.0F * (float6 - float10), float1, Math.fma(2.0F * (float9 + float7), float2, Math.fma(-2.0F, float4 + float8, 1.0F) * float3)));
	}

	public Vector4f transformInverseUnit(float float1, float float2, float float3, Vector4f vector4f) {
		float float4 = this.x * this.x;
		float float5 = this.x * this.y;
		float float6 = this.x * this.z;
		float float7 = this.x * this.w;
		float float8 = this.y * this.y;
		float float9 = this.y * this.z;
		float float10 = this.y * this.w;
		float float11 = this.z * this.z;
		float float12 = this.z * this.w;
		return vector4f.set(Math.fma(Math.fma(-2.0F, float8 + float11, 1.0F), float1, Math.fma(2.0F * (float5 + float12), float2, 2.0F * (float6 - float10) * float3)), Math.fma(2.0F * (float5 - float12), float1, Math.fma(Math.fma(-2.0F, float4 + float11, 1.0F), float2, 2.0F * (float9 + float7) * float3)), Math.fma(2.0F * (float6 + float10), float1, Math.fma(2.0F * (float9 - float7), float2, Math.fma(-2.0F, float4 + float8, 1.0F) * float3)));
	}

	public Vector3d transformPositiveX(Vector3d vector3d) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.z * this.w;
		float float6 = this.x * this.y;
		float float7 = this.x * this.z;
		float float8 = this.y * this.w;
		vector3d.x = (double)(float1 + float2 - float4 - float3);
		vector3d.y = (double)(float6 + float5 + float5 + float6);
		vector3d.z = (double)(float7 - float8 + float7 - float8);
		return vector3d;
	}

	public Vector4d transformPositiveX(Vector4d vector4d) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.z * this.w;
		float float6 = this.x * this.y;
		float float7 = this.x * this.z;
		float float8 = this.y * this.w;
		vector4d.x = (double)(float1 + float2 - float4 - float3);
		vector4d.y = (double)(float6 + float5 + float5 + float6);
		vector4d.z = (double)(float7 - float8 + float7 - float8);
		return vector4d;
	}

	public Vector3d transformUnitPositiveX(Vector3d vector3d) {
		float float1 = this.y * this.y;
		float float2 = this.z * this.z;
		float float3 = this.x * this.y;
		float float4 = this.x * this.z;
		float float5 = this.y * this.w;
		float float6 = this.z * this.w;
		vector3d.x = (double)(1.0F - float1 - float1 - float2 - float2);
		vector3d.y = (double)(float3 + float6 + float3 + float6);
		vector3d.z = (double)(float4 - float5 + float4 - float5);
		return vector3d;
	}

	public Vector4d transformUnitPositiveX(Vector4d vector4d) {
		float float1 = this.y * this.y;
		float float2 = this.z * this.z;
		float float3 = this.x * this.y;
		float float4 = this.x * this.z;
		float float5 = this.y * this.w;
		float float6 = this.z * this.w;
		vector4d.x = (double)(1.0F - float1 - float1 - float2 - float2);
		vector4d.y = (double)(float3 + float6 + float3 + float6);
		vector4d.z = (double)(float4 - float5 + float4 - float5);
		return vector4d;
	}

	public Vector3d transformPositiveY(Vector3d vector3d) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.z * this.w;
		float float6 = this.x * this.y;
		float float7 = this.y * this.z;
		float float8 = this.x * this.w;
		vector3d.x = (double)(-float5 + float6 - float5 + float6);
		vector3d.y = (double)(float3 - float4 + float1 - float2);
		vector3d.z = (double)(float7 + float7 + float8 + float8);
		return vector3d;
	}

	public Vector4d transformPositiveY(Vector4d vector4d) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.z * this.w;
		float float6 = this.x * this.y;
		float float7 = this.y * this.z;
		float float8 = this.x * this.w;
		vector4d.x = (double)(-float5 + float6 - float5 + float6);
		vector4d.y = (double)(float3 - float4 + float1 - float2);
		vector4d.z = (double)(float7 + float7 + float8 + float8);
		return vector4d;
	}

	public Vector4d transformUnitPositiveY(Vector4d vector4d) {
		float float1 = this.x * this.x;
		float float2 = this.z * this.z;
		float float3 = this.x * this.y;
		float float4 = this.y * this.z;
		float float5 = this.x * this.w;
		float float6 = this.z * this.w;
		vector4d.x = (double)(float3 - float6 + float3 - float6);
		vector4d.y = (double)(1.0F - float1 - float1 - float2 - float2);
		vector4d.z = (double)(float4 + float4 + float5 + float5);
		return vector4d;
	}

	public Vector3d transformUnitPositiveY(Vector3d vector3d) {
		float float1 = this.x * this.x;
		float float2 = this.z * this.z;
		float float3 = this.x * this.y;
		float float4 = this.y * this.z;
		float float5 = this.x * this.w;
		float float6 = this.z * this.w;
		vector3d.x = (double)(float3 - float6 + float3 - float6);
		vector3d.y = (double)(1.0F - float1 - float1 - float2 - float2);
		vector3d.z = (double)(float4 + float4 + float5 + float5);
		return vector3d;
	}

	public Vector3d transformPositiveZ(Vector3d vector3d) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.x * this.z;
		float float6 = this.y * this.w;
		float float7 = this.y * this.z;
		float float8 = this.x * this.w;
		vector3d.x = (double)(float6 + float5 + float5 + float6);
		vector3d.y = (double)(float7 + float7 - float8 - float8);
		vector3d.z = (double)(float4 - float3 - float2 + float1);
		return vector3d;
	}

	public Vector4d transformPositiveZ(Vector4d vector4d) {
		float float1 = this.w * this.w;
		float float2 = this.x * this.x;
		float float3 = this.y * this.y;
		float float4 = this.z * this.z;
		float float5 = this.x * this.z;
		float float6 = this.y * this.w;
		float float7 = this.y * this.z;
		float float8 = this.x * this.w;
		vector4d.x = (double)(float6 + float5 + float5 + float6);
		vector4d.y = (double)(float7 + float7 - float8 - float8);
		vector4d.z = (double)(float4 - float3 - float2 + float1);
		return vector4d;
	}

	public Vector4d transformUnitPositiveZ(Vector4d vector4d) {
		float float1 = this.x * this.x;
		float float2 = this.y * this.y;
		float float3 = this.x * this.z;
		float float4 = this.y * this.z;
		float float5 = this.x * this.w;
		float float6 = this.y * this.w;
		vector4d.x = (double)(float3 + float6 + float3 + float6);
		vector4d.y = (double)(float4 + float4 - float5 - float5);
		vector4d.z = (double)(1.0F - float1 - float1 - float2 - float2);
		return vector4d;
	}

	public Vector3d transformUnitPositiveZ(Vector3d vector3d) {
		float float1 = this.x * this.x;
		float float2 = this.y * this.y;
		float float3 = this.x * this.z;
		float float4 = this.y * this.z;
		float float5 = this.x * this.w;
		float float6 = this.y * this.w;
		vector3d.x = (double)(float3 + float6 + float3 + float6);
		vector3d.y = (double)(float4 + float4 - float5 - float5);
		vector3d.z = (double)(1.0F - float1 - float1 - float2 - float2);
		return vector3d;
	}

	public Vector4d transform(Vector4d vector4d) {
		return this.transform((Vector4dc)vector4d, (Vector4d)vector4d);
	}

	public Vector4d transformInverse(Vector4d vector4d) {
		return this.transformInverse((Vector4dc)vector4d, (Vector4d)vector4d);
	}

	public Vector3d transform(Vector3dc vector3dc, Vector3d vector3d) {
		return this.transform(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3d);
	}

	public Vector3d transformInverse(Vector3dc vector3dc, Vector3d vector3d) {
		return this.transformInverse(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3d);
	}

	public Vector3d transform(float float1, float float2, float float3, Vector3d vector3d) {
		return this.transform((double)float1, (double)float2, (double)float3, vector3d);
	}

	public Vector3d transformInverse(float float1, float float2, float float3, Vector3d vector3d) {
		return this.transformInverse((double)float1, (double)float2, (double)float3, vector3d);
	}

	public Vector3d transform(double double1, double double2, double double3, Vector3d vector3d) {
		float float1 = this.x * this.x;
		float float2 = this.y * this.y;
		float float3 = this.z * this.z;
		float float4 = this.w * this.w;
		float float5 = this.x * this.y;
		float float6 = this.x * this.z;
		float float7 = this.y * this.z;
		float float8 = this.x * this.w;
		float float9 = this.z * this.w;
		float float10 = this.y * this.w;
		float float11 = 1.0F / (float1 + float2 + float3 + float4);
		return vector3d.set(Math.fma((double)((float1 - float2 - float3 + float4) * float11), double1, Math.fma((double)(2.0F * (float5 - float9) * float11), double2, (double)(2.0F * (float6 + float10) * float11) * double3)), Math.fma((double)(2.0F * (float5 + float9) * float11), double1, Math.fma((double)((float2 - float1 - float3 + float4) * float11), double2, (double)(2.0F * (float7 - float8) * float11) * double3)), Math.fma((double)(2.0F * (float6 - float10) * float11), double1, Math.fma((double)(2.0F * (float7 + float8) * float11), double2, (double)((float3 - float1 - float2 + float4) * float11) * double3)));
	}

	public Vector3d transformInverse(double double1, double double2, double double3, Vector3d vector3d) {
		float float1 = 1.0F / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
		float float2 = this.x * float1;
		float float3 = this.y * float1;
		float float4 = this.z * float1;
		float float5 = this.w * float1;
		float float6 = float2 * float2;
		float float7 = float3 * float3;
		float float8 = float4 * float4;
		float float9 = float5 * float5;
		float float10 = float2 * float3;
		float float11 = float2 * float4;
		float float12 = float3 * float4;
		float float13 = float2 * float5;
		float float14 = float4 * float5;
		float float15 = float3 * float5;
		float float16 = 1.0F / (float6 + float7 + float8 + float9);
		return vector3d.set(Math.fma((double)((float6 - float7 - float8 + float9) * float16), double1, Math.fma((double)(2.0F * (float10 + float14) * float16), double2, (double)(2.0F * (float11 - float15) * float16) * double3)), Math.fma((double)(2.0F * (float10 - float14) * float16), double1, Math.fma((double)((float7 - float6 - float8 + float9) * float16), double2, (double)(2.0F * (float12 + float13) * float16) * double3)), Math.fma((double)(2.0F * (float11 + float15) * float16), double1, Math.fma((double)(2.0F * (float12 - float13) * float16), double2, (double)((float8 - float6 - float7 + float9) * float16) * double3)));
	}

	public Vector4d transform(Vector4dc vector4dc, Vector4d vector4d) {
		return this.transform(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4d);
	}

	public Vector4d transformInverse(Vector4dc vector4dc, Vector4d vector4d) {
		return this.transformInverse(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4d);
	}

	public Vector4d transform(double double1, double double2, double double3, Vector4d vector4d) {
		float float1 = this.x * this.x;
		float float2 = this.y * this.y;
		float float3 = this.z * this.z;
		float float4 = this.w * this.w;
		float float5 = this.x * this.y;
		float float6 = this.x * this.z;
		float float7 = this.y * this.z;
		float float8 = this.x * this.w;
		float float9 = this.z * this.w;
		float float10 = this.y * this.w;
		float float11 = 1.0F / (float1 + float2 + float3 + float4);
		return vector4d.set(Math.fma((double)((float1 - float2 - float3 + float4) * float11), double1, Math.fma((double)(2.0F * (float5 - float9) * float11), double2, (double)(2.0F * (float6 + float10) * float11) * double3)), Math.fma((double)(2.0F * (float5 + float9) * float11), double1, Math.fma((double)((float2 - float1 - float3 + float4) * float11), double2, (double)(2.0F * (float7 - float8) * float11) * double3)), Math.fma((double)(2.0F * (float6 - float10) * float11), double1, Math.fma((double)(2.0F * (float7 + float8) * float11), double2, (double)((float3 - float1 - float2 + float4) * float11) * double3)));
	}

	public Vector4d transformInverse(double double1, double double2, double double3, Vector4d vector4d) {
		float float1 = 1.0F / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
		float float2 = this.x * float1;
		float float3 = this.y * float1;
		float float4 = this.z * float1;
		float float5 = this.w * float1;
		float float6 = float2 * float2;
		float float7 = float3 * float3;
		float float8 = float4 * float4;
		float float9 = float5 * float5;
		float float10 = float2 * float3;
		float float11 = float2 * float4;
		float float12 = float3 * float4;
		float float13 = float2 * float5;
		float float14 = float4 * float5;
		float float15 = float3 * float5;
		float float16 = 1.0F / (float6 + float7 + float8 + float9);
		return vector4d.set(Math.fma((double)((float6 - float7 - float8 + float9) * float16), double1, Math.fma((double)(2.0F * (float10 + float14) * float16), double2, (double)(2.0F * (float11 - float15) * float16) * double3)), Math.fma((double)(2.0F * (float10 - float14) * float16), double1, Math.fma((double)((float7 - float6 - float8 + float9) * float16), double2, (double)(2.0F * (float12 + float13) * float16) * double3)), Math.fma((double)(2.0F * (float11 + float15) * float16), double1, Math.fma((double)(2.0F * (float12 - float13) * float16), double2, (double)((float8 - float6 - float7 + float9) * float16) * double3)));
	}

	public Vector4d transformUnit(Vector4d vector4d) {
		return this.transformUnit((Vector4dc)vector4d, (Vector4d)vector4d);
	}

	public Vector4d transformInverseUnit(Vector4d vector4d) {
		return this.transformInverseUnit((Vector4dc)vector4d, (Vector4d)vector4d);
	}

	public Vector3d transformUnit(Vector3dc vector3dc, Vector3d vector3d) {
		return this.transformUnit(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3d);
	}

	public Vector3d transformInverseUnit(Vector3dc vector3dc, Vector3d vector3d) {
		return this.transformInverseUnit(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3d);
	}

	public Vector3d transformUnit(float float1, float float2, float float3, Vector3d vector3d) {
		return this.transformUnit((double)float1, (double)float2, (double)float3, vector3d);
	}

	public Vector3d transformInverseUnit(float float1, float float2, float float3, Vector3d vector3d) {
		return this.transformInverseUnit((double)float1, (double)float2, (double)float3, vector3d);
	}

	public Vector3d transformUnit(double double1, double double2, double double3, Vector3d vector3d) {
		float float1 = this.x * this.x;
		float float2 = this.x * this.y;
		float float3 = this.x * this.z;
		float float4 = this.x * this.w;
		float float5 = this.y * this.y;
		float float6 = this.y * this.z;
		float float7 = this.y * this.w;
		float float8 = this.z * this.z;
		float float9 = this.z * this.w;
		return vector3d.set(Math.fma((double)Math.fma(-2.0F, float5 + float8, 1.0F), double1, Math.fma((double)(2.0F * (float2 - float9)), double2, (double)(2.0F * (float3 + float7)) * double3)), Math.fma((double)(2.0F * (float2 + float9)), double1, Math.fma((double)Math.fma(-2.0F, float1 + float8, 1.0F), double2, (double)(2.0F * (float6 - float4)) * double3)), Math.fma((double)(2.0F * (float3 - float7)), double1, Math.fma((double)(2.0F * (float6 + float4)), double2, (double)Math.fma(-2.0F, float1 + float5, 1.0F) * double3)));
	}

	public Vector3d transformInverseUnit(double double1, double double2, double double3, Vector3d vector3d) {
		float float1 = this.x * this.x;
		float float2 = this.x * this.y;
		float float3 = this.x * this.z;
		float float4 = this.x * this.w;
		float float5 = this.y * this.y;
		float float6 = this.y * this.z;
		float float7 = this.y * this.w;
		float float8 = this.z * this.z;
		float float9 = this.z * this.w;
		return vector3d.set(Math.fma((double)Math.fma(-2.0F, float5 + float8, 1.0F), double1, Math.fma((double)(2.0F * (float2 + float9)), double2, (double)(2.0F * (float3 - float7)) * double3)), Math.fma((double)(2.0F * (float2 - float9)), double1, Math.fma((double)Math.fma(-2.0F, float1 + float8, 1.0F), double2, (double)(2.0F * (float6 + float4)) * double3)), Math.fma((double)(2.0F * (float3 + float7)), double1, Math.fma((double)(2.0F * (float6 - float4)), double2, (double)Math.fma(-2.0F, float1 + float5, 1.0F) * double3)));
	}

	public Vector4d transformUnit(Vector4dc vector4dc, Vector4d vector4d) {
		return this.transformUnit(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4d);
	}

	public Vector4d transformInverseUnit(Vector4dc vector4dc, Vector4d vector4d) {
		return this.transformInverseUnit(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4d);
	}

	public Vector4d transformUnit(double double1, double double2, double double3, Vector4d vector4d) {
		float float1 = this.x * this.x;
		float float2 = this.x * this.y;
		float float3 = this.x * this.z;
		float float4 = this.x * this.w;
		float float5 = this.y * this.y;
		float float6 = this.y * this.z;
		float float7 = this.y * this.w;
		float float8 = this.z * this.z;
		float float9 = this.z * this.w;
		return vector4d.set(Math.fma((double)Math.fma(-2.0F, float5 + float8, 1.0F), double1, Math.fma((double)(2.0F * (float2 - float9)), double2, (double)(2.0F * (float3 + float7)) * double3)), Math.fma((double)(2.0F * (float2 + float9)), double1, Math.fma((double)Math.fma(-2.0F, float1 + float8, 1.0F), double2, (double)(2.0F * (float6 - float4)) * double3)), Math.fma((double)(2.0F * (float3 - float7)), double1, Math.fma((double)(2.0F * (float6 + float4)), double2, (double)Math.fma(-2.0F, float1 + float5, 1.0F) * double3)));
	}

	public Vector4d transformInverseUnit(double double1, double double2, double double3, Vector4d vector4d) {
		float float1 = this.x * this.x;
		float float2 = this.x * this.y;
		float float3 = this.x * this.z;
		float float4 = this.x * this.w;
		float float5 = this.y * this.y;
		float float6 = this.y * this.z;
		float float7 = this.y * this.w;
		float float8 = this.z * this.z;
		float float9 = this.z * this.w;
		return vector4d.set(Math.fma((double)Math.fma(-2.0F, float5 + float8, 1.0F), double1, Math.fma((double)(2.0F * (float2 + float9)), double2, (double)(2.0F * (float3 - float7)) * double3)), Math.fma((double)(2.0F * (float2 - float9)), double1, Math.fma((double)Math.fma(-2.0F, float1 + float8, 1.0F), double2, (double)(2.0F * (float6 + float4)) * double3)), Math.fma((double)(2.0F * (float3 + float7)), double1, Math.fma((double)(2.0F * (float6 - float4)), double2, (double)Math.fma(-2.0F, float1 + float5, 1.0F) * double3)));
	}

	public Quaternionf invert(Quaternionf quaternionf) {
		float float1 = 1.0F / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
		quaternionf.x = -this.x * float1;
		quaternionf.y = -this.y * float1;
		quaternionf.z = -this.z * float1;
		quaternionf.w = this.w * float1;
		return quaternionf;
	}

	public Quaternionf invert() {
		return this.invert(this);
	}

	public Quaternionf div(Quaternionfc quaternionfc, Quaternionf quaternionf) {
		float float1 = 1.0F / Math.fma(quaternionfc.x(), quaternionfc.x(), Math.fma(quaternionfc.y(), quaternionfc.y(), Math.fma(quaternionfc.z(), quaternionfc.z(), quaternionfc.w() * quaternionfc.w())));
		float float2 = -quaternionfc.x() * float1;
		float float3 = -quaternionfc.y() * float1;
		float float4 = -quaternionfc.z() * float1;
		float float5 = quaternionfc.w() * float1;
		return quaternionf.set(Math.fma(this.w, float2, Math.fma(this.x, float5, Math.fma(this.y, float4, -this.z * float3))), Math.fma(this.w, float3, Math.fma(-this.x, float4, Math.fma(this.y, float5, this.z * float2))), Math.fma(this.w, float4, Math.fma(this.x, float3, Math.fma(-this.y, float2, this.z * float5))), Math.fma(this.w, float5, Math.fma(-this.x, float2, Math.fma(-this.y, float3, -this.z * float4))));
	}

	public Quaternionf div(Quaternionfc quaternionfc) {
		return this.div(quaternionfc, this);
	}

	public Quaternionf conjugate() {
		return this.conjugate(this);
	}

	public Quaternionf conjugate(Quaternionf quaternionf) {
		quaternionf.x = -this.x;
		quaternionf.y = -this.y;
		quaternionf.z = -this.z;
		quaternionf.w = this.w;
		return quaternionf;
	}

	public Quaternionf identity() {
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
		this.w = 1.0F;
		return this;
	}

	public Quaternionf rotateXYZ(float float1, float float2, float float3) {
		return this.rotateXYZ(float1, float2, float3, this);
	}

	public Quaternionf rotateXYZ(float float1, float float2, float float3, Quaternionf quaternionf) {
		float float4 = Math.sin(float1 * 0.5F);
		float float5 = Math.cosFromSin(float4, float1 * 0.5F);
		float float6 = Math.sin(float2 * 0.5F);
		float float7 = Math.cosFromSin(float6, float2 * 0.5F);
		float float8 = Math.sin(float3 * 0.5F);
		float float9 = Math.cosFromSin(float8, float3 * 0.5F);
		float float10 = float7 * float9;
		float float11 = float6 * float8;
		float float12 = float6 * float9;
		float float13 = float7 * float8;
		float float14 = float5 * float10 - float4 * float11;
		float float15 = float4 * float10 + float5 * float11;
		float float16 = float5 * float12 - float4 * float13;
		float float17 = float5 * float13 + float4 * float12;
		return quaternionf.set(Math.fma(this.w, float15, Math.fma(this.x, float14, Math.fma(this.y, float17, -this.z * float16))), Math.fma(this.w, float16, Math.fma(-this.x, float17, Math.fma(this.y, float14, this.z * float15))), Math.fma(this.w, float17, Math.fma(this.x, float16, Math.fma(-this.y, float15, this.z * float14))), Math.fma(this.w, float14, Math.fma(-this.x, float15, Math.fma(-this.y, float16, -this.z * float17))));
	}

	public Quaternionf rotateZYX(float float1, float float2, float float3) {
		return this.rotateZYX(float1, float2, float3, this);
	}

	public Quaternionf rotateZYX(float float1, float float2, float float3, Quaternionf quaternionf) {
		float float4 = Math.sin(float3 * 0.5F);
		float float5 = Math.cosFromSin(float4, float3 * 0.5F);
		float float6 = Math.sin(float2 * 0.5F);
		float float7 = Math.cosFromSin(float6, float2 * 0.5F);
		float float8 = Math.sin(float1 * 0.5F);
		float float9 = Math.cosFromSin(float8, float1 * 0.5F);
		float float10 = float7 * float9;
		float float11 = float6 * float8;
		float float12 = float6 * float9;
		float float13 = float7 * float8;
		float float14 = float5 * float10 + float4 * float11;
		float float15 = float4 * float10 - float5 * float11;
		float float16 = float5 * float12 + float4 * float13;
		float float17 = float5 * float13 - float4 * float12;
		return quaternionf.set(Math.fma(this.w, float15, Math.fma(this.x, float14, Math.fma(this.y, float17, -this.z * float16))), Math.fma(this.w, float16, Math.fma(-this.x, float17, Math.fma(this.y, float14, this.z * float15))), Math.fma(this.w, float17, Math.fma(this.x, float16, Math.fma(-this.y, float15, this.z * float14))), Math.fma(this.w, float14, Math.fma(-this.x, float15, Math.fma(-this.y, float16, -this.z * float17))));
	}

	public Quaternionf rotateYXZ(float float1, float float2, float float3) {
		return this.rotateYXZ(float1, float2, float3, this);
	}

	public Quaternionf rotateYXZ(float float1, float float2, float float3, Quaternionf quaternionf) {
		float float4 = Math.sin(float2 * 0.5F);
		float float5 = Math.cosFromSin(float4, float2 * 0.5F);
		float float6 = Math.sin(float1 * 0.5F);
		float float7 = Math.cosFromSin(float6, float1 * 0.5F);
		float float8 = Math.sin(float3 * 0.5F);
		float float9 = Math.cosFromSin(float8, float3 * 0.5F);
		float float10 = float7 * float4;
		float float11 = float6 * float5;
		float float12 = float6 * float4;
		float float13 = float7 * float5;
		float float14 = float10 * float9 + float11 * float8;
		float float15 = float11 * float9 - float10 * float8;
		float float16 = float13 * float8 - float12 * float9;
		float float17 = float13 * float9 + float12 * float8;
		return quaternionf.set(Math.fma(this.w, float14, Math.fma(this.x, float17, Math.fma(this.y, float16, -this.z * float15))), Math.fma(this.w, float15, Math.fma(-this.x, float16, Math.fma(this.y, float17, this.z * float14))), Math.fma(this.w, float16, Math.fma(this.x, float15, Math.fma(-this.y, float14, this.z * float17))), Math.fma(this.w, float17, Math.fma(-this.x, float14, Math.fma(-this.y, float15, -this.z * float16))));
	}

	public Vector3f getEulerAnglesXYZ(Vector3f vector3f) {
		vector3f.x = Math.atan2(2.0F * (this.x * this.w - this.y * this.z), 1.0F - 2.0F * (this.x * this.x + this.y * this.y));
		vector3f.y = Math.safeAsin(2.0F * (this.x * this.z + this.y * this.w));
		vector3f.z = Math.atan2(2.0F * (this.z * this.w - this.x * this.y), 1.0F - 2.0F * (this.y * this.y + this.z * this.z));
		return vector3f;
	}

	public float lengthSquared() {
		return Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
	}

	public Quaternionf rotationXYZ(float float1, float float2, float float3) {
		float float4 = Math.sin(float1 * 0.5F);
		float float5 = Math.cosFromSin(float4, float1 * 0.5F);
		float float6 = Math.sin(float2 * 0.5F);
		float float7 = Math.cosFromSin(float6, float2 * 0.5F);
		float float8 = Math.sin(float3 * 0.5F);
		float float9 = Math.cosFromSin(float8, float3 * 0.5F);
		float float10 = float7 * float9;
		float float11 = float6 * float8;
		float float12 = float6 * float9;
		float float13 = float7 * float8;
		this.w = float5 * float10 - float4 * float11;
		this.x = float4 * float10 + float5 * float11;
		this.y = float5 * float12 - float4 * float13;
		this.z = float5 * float13 + float4 * float12;
		return this;
	}

	public Quaternionf rotationZYX(float float1, float float2, float float3) {
		float float4 = Math.sin(float3 * 0.5F);
		float float5 = Math.cosFromSin(float4, float3 * 0.5F);
		float float6 = Math.sin(float2 * 0.5F);
		float float7 = Math.cosFromSin(float6, float2 * 0.5F);
		float float8 = Math.sin(float1 * 0.5F);
		float float9 = Math.cosFromSin(float8, float1 * 0.5F);
		float float10 = float7 * float9;
		float float11 = float6 * float8;
		float float12 = float6 * float9;
		float float13 = float7 * float8;
		this.w = float5 * float10 + float4 * float11;
		this.x = float4 * float10 - float5 * float11;
		this.y = float5 * float12 + float4 * float13;
		this.z = float5 * float13 - float4 * float12;
		return this;
	}

	public Quaternionf rotationYXZ(float float1, float float2, float float3) {
		float float4 = Math.sin(float2 * 0.5F);
		float float5 = Math.cosFromSin(float4, float2 * 0.5F);
		float float6 = Math.sin(float1 * 0.5F);
		float float7 = Math.cosFromSin(float6, float1 * 0.5F);
		float float8 = Math.sin(float3 * 0.5F);
		float float9 = Math.cosFromSin(float8, float3 * 0.5F);
		float float10 = float7 * float4;
		float float11 = float6 * float5;
		float float12 = float6 * float4;
		float float13 = float7 * float5;
		this.x = float10 * float9 + float11 * float8;
		this.y = float11 * float9 - float10 * float8;
		this.z = float13 * float8 - float12 * float9;
		this.w = float13 * float9 + float12 * float8;
		return this;
	}

	public Quaternionf slerp(Quaternionfc quaternionfc, float float1) {
		return this.slerp(quaternionfc, float1, this);
	}

	public Quaternionf slerp(Quaternionfc quaternionfc, float float1, Quaternionf quaternionf) {
		float float2 = Math.fma(this.x, quaternionfc.x(), Math.fma(this.y, quaternionfc.y(), Math.fma(this.z, quaternionfc.z(), this.w * quaternionfc.w())));
		float float3 = Math.abs(float2);
		float float4;
		float float5;
		if (1.0F - float3 > 1.0E-6F) {
			float float6 = 1.0F - float3 * float3;
			float float7 = Math.invsqrt(float6);
			float float8 = Math.atan2(float6 * float7, float3);
			float4 = (float)(Math.sin((1.0 - (double)float1) * (double)float8) * (double)float7);
			float5 = Math.sin(float1 * float8) * float7;
		} else {
			float4 = 1.0F - float1;
			float5 = float1;
		}

		float5 = float2 >= 0.0F ? float5 : -float5;
		quaternionf.x = Math.fma(float4, this.x, float5 * quaternionfc.x());
		quaternionf.y = Math.fma(float4, this.y, float5 * quaternionfc.y());
		quaternionf.z = Math.fma(float4, this.z, float5 * quaternionfc.z());
		quaternionf.w = Math.fma(float4, this.w, float5 * quaternionfc.w());
		return quaternionf;
	}

	public static Quaternionfc slerp(Quaternionf[] quaternionfArray, float[] floatArray, Quaternionf quaternionf) {
		quaternionf.set((Quaternionfc)quaternionfArray[0]);
		float float1 = floatArray[0];
		for (int int1 = 1; int1 < quaternionfArray.length; ++int1) {
			float float2 = floatArray[int1];
			float float3 = float2 / (float1 + float2);
			float1 += float2;
			quaternionf.slerp(quaternionfArray[int1], float3);
		}

		return quaternionf;
	}

	public Quaternionf scale(float float1) {
		return this.scale(float1, this);
	}

	public Quaternionf scale(float float1, Quaternionf quaternionf) {
		float float2 = Math.sqrt(float1);
		quaternionf.x = float2 * this.x;
		quaternionf.y = float2 * this.y;
		quaternionf.z = float2 * this.z;
		quaternionf.w = float2 * this.w;
		return quaternionf;
	}

	public Quaternionf scaling(float float1) {
		float float2 = Math.sqrt(float1);
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
		this.w = float2;
		return this;
	}

	public Quaternionf integrate(float float1, float float2, float float3, float float4) {
		return this.integrate(float1, float2, float3, float4, this);
	}

	public Quaternionf integrate(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
		float float5 = float1 * float2 * 0.5F;
		float float6 = float1 * float3 * 0.5F;
		float float7 = float1 * float4 * 0.5F;
		float float8 = float5 * float5 + float6 * float6 + float7 * float7;
		float float9;
		float float10;
		if (float8 * float8 / 24.0F < 1.0E-8F) {
			float10 = 1.0F - float8 * 0.5F;
			float9 = 1.0F - float8 / 6.0F;
		} else {
			float float11 = Math.sqrt(float8);
			float float12 = Math.sin(float11);
			float9 = float12 / float11;
			float10 = Math.cosFromSin(float12, float11);
		}

		float float13 = float5 * float9;
		float float14 = float6 * float9;
		float float15 = float7 * float9;
		return quaternionf.set(Math.fma(float10, this.x, Math.fma(float13, this.w, Math.fma(float14, this.z, -float15 * this.y))), Math.fma(float10, this.y, Math.fma(-float13, this.z, Math.fma(float14, this.w, float15 * this.x))), Math.fma(float10, this.z, Math.fma(float13, this.y, Math.fma(-float14, this.x, float15 * this.w))), Math.fma(float10, this.w, Math.fma(-float13, this.x, Math.fma(-float14, this.y, -float15 * this.z))));
	}

	public Quaternionf nlerp(Quaternionfc quaternionfc, float float1) {
		return this.nlerp(quaternionfc, float1, this);
	}

	public Quaternionf nlerp(Quaternionfc quaternionfc, float float1, Quaternionf quaternionf) {
		float float2 = Math.fma(this.x, quaternionfc.x(), Math.fma(this.y, quaternionfc.y(), Math.fma(this.z, quaternionfc.z(), this.w * quaternionfc.w())));
		float float3 = 1.0F - float1;
		float float4 = float2 >= 0.0F ? float1 : -float1;
		quaternionf.x = Math.fma(float3, this.x, float4 * quaternionfc.x());
		quaternionf.y = Math.fma(float3, this.y, float4 * quaternionfc.y());
		quaternionf.z = Math.fma(float3, this.z, float4 * quaternionfc.z());
		quaternionf.w = Math.fma(float3, this.w, float4 * quaternionfc.w());
		float float5 = Math.invsqrt(Math.fma(quaternionf.x, quaternionf.x, Math.fma(quaternionf.y, quaternionf.y, Math.fma(quaternionf.z, quaternionf.z, quaternionf.w * quaternionf.w))));
		quaternionf.x *= float5;
		quaternionf.y *= float5;
		quaternionf.z *= float5;
		quaternionf.w *= float5;
		return quaternionf;
	}

	public static Quaternionfc nlerp(Quaternionfc[] quaternionfcArray, float[] floatArray, Quaternionf quaternionf) {
		quaternionf.set(quaternionfcArray[0]);
		float float1 = floatArray[0];
		for (int int1 = 1; int1 < quaternionfcArray.length; ++int1) {
			float float2 = floatArray[int1];
			float float3 = float2 / (float1 + float2);
			float1 += float2;
			quaternionf.nlerp(quaternionfcArray[int1], float3);
		}

		return quaternionf;
	}

	public Quaternionf nlerpIterative(Quaternionfc quaternionfc, float float1, float float2, Quaternionf quaternionf) {
		float float3 = this.x;
		float float4 = this.y;
		float float5 = this.z;
		float float6 = this.w;
		float float7 = quaternionfc.x();
		float float8 = quaternionfc.y();
		float float9 = quaternionfc.z();
		float float10 = quaternionfc.w();
		float float11 = Math.fma(float3, float7, Math.fma(float4, float8, Math.fma(float5, float9, float6 * float10)));
		float float12 = Math.abs(float11);
		if (0.999999F < float12) {
			return quaternionf.set((Quaternionfc)this);
		} else {
			float float13;
			float float14;
			float float15;
			float float16;
			for (float13 = float1; float12 < float2; float12 = Math.abs(float11)) {
				float14 = 0.5F;
				float15 = float11 >= 0.0F ? 0.5F : -0.5F;
				if (float13 < 0.5F) {
					float7 = Math.fma(float14, float7, float15 * float3);
					float8 = Math.fma(float14, float8, float15 * float4);
					float9 = Math.fma(float14, float9, float15 * float5);
					float10 = Math.fma(float14, float10, float15 * float6);
					float16 = Math.invsqrt(Math.fma(float7, float7, Math.fma(float8, float8, Math.fma(float9, float9, float10 * float10))));
					float7 *= float16;
					float8 *= float16;
					float9 *= float16;
					float10 *= float16;
					float13 += float13;
				} else {
					float3 = Math.fma(float14, float3, float15 * float7);
					float4 = Math.fma(float14, float4, float15 * float8);
					float5 = Math.fma(float14, float5, float15 * float9);
					float6 = Math.fma(float14, float6, float15 * float10);
					float16 = Math.invsqrt(Math.fma(float3, float3, Math.fma(float4, float4, Math.fma(float5, float5, float6 * float6))));
					float3 *= float16;
					float4 *= float16;
					float5 *= float16;
					float6 *= float16;
					float13 = float13 + float13 - 1.0F;
				}

				float11 = Math.fma(float3, float7, Math.fma(float4, float8, Math.fma(float5, float9, float6 * float10)));
			}

			float14 = 1.0F - float13;
			float15 = float11 >= 0.0F ? float13 : -float13;
			float16 = Math.fma(float14, float3, float15 * float7);
			float float17 = Math.fma(float14, float4, float15 * float8);
			float float18 = Math.fma(float14, float5, float15 * float9);
			float float19 = Math.fma(float14, float6, float15 * float10);
			float float20 = Math.invsqrt(Math.fma(float16, float16, Math.fma(float17, float17, Math.fma(float18, float18, float19 * float19))));
			quaternionf.x = float16 * float20;
			quaternionf.y = float17 * float20;
			quaternionf.z = float18 * float20;
			quaternionf.w = float19 * float20;
			return quaternionf;
		}
	}

	public Quaternionf nlerpIterative(Quaternionfc quaternionfc, float float1, float float2) {
		return this.nlerpIterative(quaternionfc, float1, float2, this);
	}

	public static Quaternionfc nlerpIterative(Quaternionf[] quaternionfArray, float[] floatArray, float float1, Quaternionf quaternionf) {
		quaternionf.set((Quaternionfc)quaternionfArray[0]);
		float float2 = floatArray[0];
		for (int int1 = 1; int1 < quaternionfArray.length; ++int1) {
			float float3 = floatArray[int1];
			float float4 = float3 / (float2 + float3);
			float2 += float3;
			quaternionf.nlerpIterative(quaternionfArray[int1], float4, float1);
		}

		return quaternionf;
	}

	public Quaternionf lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.lookAlong(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), this);
	}

	public Quaternionf lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2, Quaternionf quaternionf) {
		return this.lookAlong(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), quaternionf);
	}

	public Quaternionf lookAlong(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.lookAlong(float1, float2, float3, float4, float5, float6, this);
	}

	public Quaternionf lookAlong(float float1, float float2, float float3, float float4, float float5, float float6, Quaternionf quaternionf) {
		float float7 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		float float8 = -float1 * float7;
		float float9 = -float2 * float7;
		float float10 = -float3 * float7;
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
		double double1 = (double)(float11 + float16 + float10);
		float float18;
		float float19;
		float float20;
		float float21;
		double double2;
		if (double1 >= 0.0) {
			double2 = Math.sqrt(double1 + 1.0);
			float21 = (float)(double2 * 0.5);
			double2 = 0.5 / double2;
			float18 = (float)((double)(float9 - float17) * double2);
			float19 = (float)((double)(float13 - float8) * double2);
			float20 = (float)((double)(float15 - float12) * double2);
		} else if (float11 > float16 && float11 > float10) {
			double2 = Math.sqrt(1.0 + (double)float11 - (double)float16 - (double)float10);
			float18 = (float)(double2 * 0.5);
			double2 = 0.5 / double2;
			float19 = (float)((double)(float12 + float15) * double2);
			float20 = (float)((double)(float8 + float13) * double2);
			float21 = (float)((double)(float9 - float17) * double2);
		} else if (float16 > float10) {
			double2 = Math.sqrt(1.0 + (double)float16 - (double)float11 - (double)float10);
			float19 = (float)(double2 * 0.5);
			double2 = 0.5 / double2;
			float18 = (float)((double)(float12 + float15) * double2);
			float20 = (float)((double)(float17 + float9) * double2);
			float21 = (float)((double)(float13 - float8) * double2);
		} else {
			double2 = Math.sqrt(1.0 + (double)float10 - (double)float11 - (double)float16);
			float20 = (float)(double2 * 0.5);
			double2 = 0.5 / double2;
			float18 = (float)((double)(float8 + float13) * double2);
			float19 = (float)((double)(float17 + float9) * double2);
			float21 = (float)((double)(float15 - float12) * double2);
		}

		return quaternionf.set(Math.fma(this.w, float18, Math.fma(this.x, float21, Math.fma(this.y, float20, -this.z * float19))), Math.fma(this.w, float19, Math.fma(-this.x, float20, Math.fma(this.y, float21, this.z * float18))), Math.fma(this.w, float20, Math.fma(this.x, float19, Math.fma(-this.y, float18, this.z * float21))), Math.fma(this.w, float21, Math.fma(-this.x, float18, Math.fma(-this.y, float19, -this.z * float20))));
	}

	public Quaternionf rotationTo(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = Math.invsqrt(Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3)));
		float float8 = Math.invsqrt(Math.fma(float4, float4, Math.fma(float5, float5, float6 * float6)));
		float float9 = float1 * float7;
		float float10 = float2 * float7;
		float float11 = float3 * float7;
		float float12 = float4 * float8;
		float float13 = float5 * float8;
		float float14 = float6 * float8;
		float float15 = float9 * float12 + float10 * float13 + float11 * float14;
		float float16;
		float float17;
		float float18;
		float float19;
		if (float15 < -0.999999F) {
			float16 = float10;
			float17 = -float9;
			float18 = 0.0F;
			float19 = 0.0F;
			if (float10 * float10 + float17 * float17 == 0.0F) {
				float16 = 0.0F;
				float17 = float11;
				float18 = -float10;
				float19 = 0.0F;
			}

			this.x = float16;
			this.y = float17;
			this.z = float18;
			this.w = 0.0F;
		} else {
			float float20 = Math.sqrt((1.0F + float15) * 2.0F);
			float float21 = 1.0F / float20;
			float float22 = float10 * float14 - float11 * float13;
			float float23 = float11 * float12 - float9 * float14;
			float float24 = float9 * float13 - float10 * float12;
			float16 = float22 * float21;
			float17 = float23 * float21;
			float18 = float24 * float21;
			float19 = float20 * 0.5F;
			float float25 = Math.invsqrt(Math.fma(float16, float16, Math.fma(float17, float17, Math.fma(float18, float18, float19 * float19))));
			this.x = float16 * float25;
			this.y = float17 * float25;
			this.z = float18 * float25;
			this.w = float19 * float25;
		}

		return this;
	}

	public Quaternionf rotationTo(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.rotationTo(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Quaternionf rotateTo(float float1, float float2, float float3, float float4, float float5, float float6, Quaternionf quaternionf) {
		float float7 = Math.invsqrt(Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3)));
		float float8 = Math.invsqrt(Math.fma(float4, float4, Math.fma(float5, float5, float6 * float6)));
		float float9 = float1 * float7;
		float float10 = float2 * float7;
		float float11 = float3 * float7;
		float float12 = float4 * float8;
		float float13 = float5 * float8;
		float float14 = float6 * float8;
		float float15 = float9 * float12 + float10 * float13 + float11 * float14;
		float float16;
		float float17;
		float float18;
		float float19;
		if (float15 < -0.999999F) {
			float16 = float10;
			float17 = -float9;
			float18 = 0.0F;
			float19 = 0.0F;
			if (float10 * float10 + float17 * float17 == 0.0F) {
				float16 = 0.0F;
				float17 = float11;
				float18 = -float10;
				float19 = 0.0F;
			}
		} else {
			float float20 = Math.sqrt((1.0F + float15) * 2.0F);
			float float21 = 1.0F / float20;
			float float22 = float10 * float14 - float11 * float13;
			float float23 = float11 * float12 - float9 * float14;
			float float24 = float9 * float13 - float10 * float12;
			float16 = float22 * float21;
			float17 = float23 * float21;
			float18 = float24 * float21;
			float19 = float20 * 0.5F;
			float float25 = Math.invsqrt(Math.fma(float16, float16, Math.fma(float17, float17, Math.fma(float18, float18, float19 * float19))));
			float16 *= float25;
			float17 *= float25;
			float18 *= float25;
			float19 *= float25;
		}

		return quaternionf.set(Math.fma(this.w, float16, Math.fma(this.x, float19, Math.fma(this.y, float18, -this.z * float17))), Math.fma(this.w, float17, Math.fma(-this.x, float18, Math.fma(this.y, float19, this.z * float16))), Math.fma(this.w, float18, Math.fma(this.x, float17, Math.fma(-this.y, float16, this.z * float19))), Math.fma(this.w, float19, Math.fma(-this.x, float16, Math.fma(-this.y, float17, -this.z * float18))));
	}

	public Quaternionf rotateTo(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.rotateTo(float1, float2, float3, float4, float5, float6, this);
	}

	public Quaternionf rotateTo(Vector3fc vector3fc, Vector3fc vector3fc2, Quaternionf quaternionf) {
		return this.rotateTo(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), quaternionf);
	}

	public Quaternionf rotateTo(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.rotateTo(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), this);
	}

	public Quaternionf rotateX(float float1) {
		return this.rotateX(float1, this);
	}

	public Quaternionf rotateX(float float1, Quaternionf quaternionf) {
		float float2 = Math.sin(float1 * 0.5F);
		float float3 = Math.cosFromSin(float2, float1 * 0.5F);
		return quaternionf.set(this.w * float2 + this.x * float3, this.y * float3 + this.z * float2, this.z * float3 - this.y * float2, this.w * float3 - this.x * float2);
	}

	public Quaternionf rotateY(float float1) {
		return this.rotateY(float1, this);
	}

	public Quaternionf rotateY(float float1, Quaternionf quaternionf) {
		float float2 = Math.sin(float1 * 0.5F);
		float float3 = Math.cosFromSin(float2, float1 * 0.5F);
		return quaternionf.set(this.x * float3 - this.z * float2, this.w * float2 + this.y * float3, this.x * float2 + this.z * float3, this.w * float3 - this.y * float2);
	}

	public Quaternionf rotateZ(float float1) {
		return this.rotateZ(float1, this);
	}

	public Quaternionf rotateZ(float float1, Quaternionf quaternionf) {
		float float2 = Math.sin(float1 * 0.5F);
		float float3 = Math.cosFromSin(float2, float1 * 0.5F);
		return quaternionf.set(this.x * float3 + this.y * float2, this.y * float3 - this.x * float2, this.w * float2 + this.z * float3, this.w * float3 - this.z * float2);
	}

	public Quaternionf rotateLocalX(float float1) {
		return this.rotateLocalX(float1, this);
	}

	public Quaternionf rotateLocalX(float float1, Quaternionf quaternionf) {
		float float2 = float1 * 0.5F;
		float float3 = Math.sin(float2);
		float float4 = Math.cosFromSin(float3, float2);
		quaternionf.set(float4 * this.x + float3 * this.w, float4 * this.y - float3 * this.z, float4 * this.z + float3 * this.y, float4 * this.w - float3 * this.x);
		return quaternionf;
	}

	public Quaternionf rotateLocalY(float float1) {
		return this.rotateLocalY(float1, this);
	}

	public Quaternionf rotateLocalY(float float1, Quaternionf quaternionf) {
		float float2 = float1 * 0.5F;
		float float3 = Math.sin(float2);
		float float4 = Math.cosFromSin(float3, float2);
		quaternionf.set(float4 * this.x + float3 * this.z, float4 * this.y + float3 * this.w, float4 * this.z - float3 * this.x, float4 * this.w - float3 * this.y);
		return quaternionf;
	}

	public Quaternionf rotateLocalZ(float float1) {
		return this.rotateLocalZ(float1, this);
	}

	public Quaternionf rotateLocalZ(float float1, Quaternionf quaternionf) {
		float float2 = float1 * 0.5F;
		float float3 = Math.sin(float2);
		float float4 = Math.cosFromSin(float3, float2);
		quaternionf.set(float4 * this.x - float3 * this.y, float4 * this.y + float3 * this.x, float4 * this.z + float3 * this.w, float4 * this.w - float3 * this.z);
		return quaternionf;
	}

	public Quaternionf rotateAxis(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
		float float5 = float1 / 2.0F;
		float float6 = Math.sin(float5);
		float float7 = Math.invsqrt(Math.fma(float2, float2, Math.fma(float3, float3, float4 * float4)));
		float float8 = float2 * float7 * float6;
		float float9 = float3 * float7 * float6;
		float float10 = float4 * float7 * float6;
		float float11 = Math.cosFromSin(float6, float5);
		return quaternionf.set(Math.fma(this.w, float8, Math.fma(this.x, float11, Math.fma(this.y, float10, -this.z * float9))), Math.fma(this.w, float9, Math.fma(-this.x, float10, Math.fma(this.y, float11, this.z * float8))), Math.fma(this.w, float10, Math.fma(this.x, float9, Math.fma(-this.y, float8, this.z * float11))), Math.fma(this.w, float11, Math.fma(-this.x, float8, Math.fma(-this.y, float9, -this.z * float10))));
	}

	public Quaternionf rotateAxis(float float1, Vector3fc vector3fc, Quaternionf quaternionf) {
		return this.rotateAxis(float1, vector3fc.x(), vector3fc.y(), vector3fc.z(), quaternionf);
	}

	public Quaternionf rotateAxis(float float1, Vector3fc vector3fc) {
		return this.rotateAxis(float1, vector3fc.x(), vector3fc.y(), vector3fc.z(), this);
	}

	public Quaternionf rotateAxis(float float1, float float2, float float3, float float4) {
		return this.rotateAxis(float1, float2, float3, float4, this);
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format((double)this.x, numberFormat);
		return "(" + string + " " + Runtime.format((double)this.y, numberFormat) + " " + Runtime.format((double)this.z, numberFormat) + " " + Runtime.format((double)this.w, numberFormat) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeFloat(this.x);
		objectOutput.writeFloat(this.y);
		objectOutput.writeFloat(this.z);
		objectOutput.writeFloat(this.w);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readFloat();
		this.y = objectInput.readFloat();
		this.z = objectInput.readFloat();
		this.w = objectInput.readFloat();
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + Float.floatToIntBits(this.w);
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
			Quaternionf quaternionf = (Quaternionf)object;
			if (Float.floatToIntBits(this.w) != Float.floatToIntBits(quaternionf.w)) {
				return false;
			} else if (Float.floatToIntBits(this.x) != Float.floatToIntBits(quaternionf.x)) {
				return false;
			} else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(quaternionf.y)) {
				return false;
			} else {
				return Float.floatToIntBits(this.z) == Float.floatToIntBits(quaternionf.z);
			}
		}
	}

	public Quaternionf difference(Quaternionf quaternionf) {
		return this.difference(quaternionf, this);
	}

	public Quaternionf difference(Quaternionfc quaternionfc, Quaternionf quaternionf) {
		float float1 = 1.0F / this.lengthSquared();
		float float2 = -this.x * float1;
		float float3 = -this.y * float1;
		float float4 = -this.z * float1;
		float float5 = this.w * float1;
		quaternionf.set(Math.fma(float5, quaternionfc.x(), Math.fma(float2, quaternionfc.w(), Math.fma(float3, quaternionfc.z(), -float4 * quaternionfc.y()))), Math.fma(float5, quaternionfc.y(), Math.fma(-float2, quaternionfc.z(), Math.fma(float3, quaternionfc.w(), float4 * quaternionfc.x()))), Math.fma(float5, quaternionfc.z(), Math.fma(float2, quaternionfc.y(), Math.fma(-float3, quaternionfc.x(), float4 * quaternionfc.w()))), Math.fma(float5, quaternionfc.w(), Math.fma(-float2, quaternionfc.x(), Math.fma(-float3, quaternionfc.y(), -float4 * quaternionfc.z()))));
		return quaternionf;
	}

	public Vector3f positiveX(Vector3f vector3f) {
		float float1 = 1.0F / this.lengthSquared();
		float float2 = -this.x * float1;
		float float3 = -this.y * float1;
		float float4 = -this.z * float1;
		float float5 = this.w * float1;
		float float6 = float3 + float3;
		float float7 = float4 + float4;
		vector3f.x = -float3 * float6 - float4 * float7 + 1.0F;
		vector3f.y = float2 * float6 + float5 * float7;
		vector3f.z = float2 * float7 - float5 * float6;
		return vector3f;
	}

	public Vector3f normalizedPositiveX(Vector3f vector3f) {
		float float1 = this.y + this.y;
		float float2 = this.z + this.z;
		vector3f.x = -this.y * float1 - this.z * float2 + 1.0F;
		vector3f.y = this.x * float1 - this.w * float2;
		vector3f.z = this.x * float2 + this.w * float1;
		return vector3f;
	}

	public Vector3f positiveY(Vector3f vector3f) {
		float float1 = 1.0F / this.lengthSquared();
		float float2 = -this.x * float1;
		float float3 = -this.y * float1;
		float float4 = -this.z * float1;
		float float5 = this.w * float1;
		float float6 = float2 + float2;
		float float7 = float3 + float3;
		float float8 = float4 + float4;
		vector3f.x = float2 * float7 - float5 * float8;
		vector3f.y = -float2 * float6 - float4 * float8 + 1.0F;
		vector3f.z = float3 * float8 + float5 * float6;
		return vector3f;
	}

	public Vector3f normalizedPositiveY(Vector3f vector3f) {
		float float1 = this.x + this.x;
		float float2 = this.y + this.y;
		float float3 = this.z + this.z;
		vector3f.x = this.x * float2 + this.w * float3;
		vector3f.y = -this.x * float1 - this.z * float3 + 1.0F;
		vector3f.z = this.y * float3 - this.w * float1;
		return vector3f;
	}

	public Vector3f positiveZ(Vector3f vector3f) {
		float float1 = 1.0F / this.lengthSquared();
		float float2 = -this.x * float1;
		float float3 = -this.y * float1;
		float float4 = -this.z * float1;
		float float5 = this.w * float1;
		float float6 = float2 + float2;
		float float7 = float3 + float3;
		float float8 = float4 + float4;
		vector3f.x = float2 * float8 + float5 * float7;
		vector3f.y = float3 * float8 - float5 * float6;
		vector3f.z = -float2 * float6 - float3 * float7 + 1.0F;
		return vector3f;
	}

	public Vector3f normalizedPositiveZ(Vector3f vector3f) {
		float float1 = this.x + this.x;
		float float2 = this.y + this.y;
		float float3 = this.z + this.z;
		vector3f.x = this.x * float3 - this.w * float2;
		vector3f.y = this.y * float3 + this.w * float1;
		vector3f.z = -this.x * float1 - this.y * float2 + 1.0F;
		return vector3f;
	}

	public Quaternionf conjugateBy(Quaternionfc quaternionfc) {
		return this.conjugateBy(quaternionfc, this);
	}

	public Quaternionf conjugateBy(Quaternionfc quaternionfc, Quaternionf quaternionf) {
		float float1 = 1.0F / quaternionfc.lengthSquared();
		float float2 = -quaternionfc.x() * float1;
		float float3 = -quaternionfc.y() * float1;
		float float4 = -quaternionfc.z() * float1;
		float float5 = quaternionfc.w() * float1;
		float float6 = Math.fma(quaternionfc.w(), this.x, Math.fma(quaternionfc.x(), this.w, Math.fma(quaternionfc.y(), this.z, -quaternionfc.z() * this.y)));
		float float7 = Math.fma(quaternionfc.w(), this.y, Math.fma(-quaternionfc.x(), this.z, Math.fma(quaternionfc.y(), this.w, quaternionfc.z() * this.x)));
		float float8 = Math.fma(quaternionfc.w(), this.z, Math.fma(quaternionfc.x(), this.y, Math.fma(-quaternionfc.y(), this.x, quaternionfc.z() * this.w)));
		float float9 = Math.fma(quaternionfc.w(), this.w, Math.fma(-quaternionfc.x(), this.x, Math.fma(-quaternionfc.y(), this.y, -quaternionfc.z() * this.z)));
		return quaternionf.set(Math.fma(float9, float2, Math.fma(float6, float5, Math.fma(float7, float4, -float8 * float3))), Math.fma(float9, float3, Math.fma(-float6, float4, Math.fma(float7, float5, float8 * float2))), Math.fma(float9, float4, Math.fma(float6, float3, Math.fma(-float7, float2, float8 * float5))), Math.fma(float9, float5, Math.fma(-float6, float2, Math.fma(-float7, float3, -float8 * float4))));
	}

	public boolean isFinite() {
		return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z) && Math.isFinite(this.w);
	}

	public boolean equals(Quaternionfc quaternionfc, float float1) {
		if (this == quaternionfc) {
			return true;
		} else if (quaternionfc == null) {
			return false;
		} else if (!(quaternionfc instanceof Quaternionfc)) {
			return false;
		} else if (!Runtime.equals(this.x, quaternionfc.x(), float1)) {
			return false;
		} else if (!Runtime.equals(this.y, quaternionfc.y(), float1)) {
			return false;
		} else if (!Runtime.equals(this.z, quaternionfc.z(), float1)) {
			return false;
		} else {
			return Runtime.equals(this.w, quaternionfc.w(), float1);
		}
	}

	public boolean equals(float float1, float float2, float float3, float float4) {
		if (Float.floatToIntBits(this.x) != Float.floatToIntBits(float1)) {
			return false;
		} else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(float2)) {
			return false;
		} else if (Float.floatToIntBits(this.z) != Float.floatToIntBits(float3)) {
			return false;
		} else {
			return Float.floatToIntBits(this.w) == Float.floatToIntBits(float4);
		}
	}
}
