package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Quaternionf implements Externalizable,Quaternionfc {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;
	public float z;
	public float w;

	public Quaternionf() {
		MemUtil.INSTANCE.identity(this);
	}

	public Quaternionf(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
	}

	public Quaternionf(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = 1.0F;
	}

	public Quaternionf(Quaternionf quaternionf) {
		MemUtil.INSTANCE.copy(quaternionf, this);
	}

	public Quaternionf(AxisAngle4f axisAngle4f) {
		float float1 = (float)Math.sin((double)axisAngle4f.angle / 2.0);
		float float2 = (float)Math.cos((double)axisAngle4f.angle / 2.0);
		this.x = axisAngle4f.x * float1;
		this.y = axisAngle4f.y * float1;
		this.z = axisAngle4f.z * float1;
		this.w = float2;
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
		float float1 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w)));
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		this.w *= float1;
		return this;
	}

	public Quaternionf normalize(Quaternionf quaternionf) {
		float float1 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w)));
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
		this.x += quaternionfc.x();
		this.y += quaternionfc.y();
		this.z += quaternionfc.z();
		this.w += quaternionfc.w();
		return this;
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
		float float1 = (float)(2.0 * Math.acos((double)this.w));
		return (float)((double)float1 <= 3.141592653589793 ? (double)float1 : 6.283185307179586 - (double)float1);
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
			float5 = (float)(1.0 / Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3 + float4 * float4)));
			float1 *= float5;
			float2 *= float5;
			float3 *= float5;
			float4 *= float5;
		}

		axisAngle4f.angle = (float)(2.0 * Math.acos((double)float4));
		float5 = (float)Math.sqrt(1.0 - (double)(float4 * float4));
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

	public Quaternionf set(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		return this;
	}

	public Quaternionf set(Quaternionfc quaternionfc) {
		if (quaternionfc instanceof Quaternionf) {
			MemUtil.INSTANCE.copy((Quaternionf)quaternionfc, this);
		} else {
			this.x = quaternionfc.x();
			this.y = quaternionfc.y();
			this.z = quaternionfc.z();
			this.w = quaternionfc.w();
		}

		return this;
	}

	public Quaternionf set(AxisAngle4f axisAngle4f) {
		return this.setAngleAxis(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z);
	}

	public Quaternionf set(AxisAngle4d axisAngle4d) {
		return this.setAngleAxis(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z);
	}

	public Quaternionf setAngleAxis(float float1, float float2, float float3, float float4) {
		float float5 = (float)Math.sin((double)float1 * 0.5);
		this.x = float2 * float5;
		this.y = float3 * float5;
		this.z = float4 * float5;
		this.w = (float)Math.cos((double)float1 * 0.5);
		return this;
	}

	public Quaternionf setAngleAxis(double double1, double double2, double double3, double double4) {
		double double5 = Math.sin(double1 * 0.5);
		this.x = (float)(double2 * double5);
		this.y = (float)(double3 * double5);
		this.z = (float)(double4 * double5);
		this.w = (float)Math.cos(double1 * 0.5);
		return this;
	}

	public Quaternionf rotationAxis(AxisAngle4f axisAngle4f) {
		return this.rotationAxis(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z);
	}

	public Quaternionf rotationAxis(float float1, float float2, float float3, float float4) {
		float float5 = float1 / 2.0F;
		float float6 = (float)Math.sin((double)float5);
		float float7 = (float)(1.0 / Math.sqrt((double)(float2 * float2 + float3 * float3 + float4 * float4)));
		this.x = float2 * float7 * float6;
		this.y = float3 * float7 * float6;
		this.z = float4 * float7 * float6;
		this.w = (float)Math.cos((double)float5);
		return this;
	}

	public Quaternionf rotationAxis(float float1, Vector3fc vector3fc) {
		return this.rotationAxis(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Quaternionf rotation(float float1, float float2, float float3) {
		double double1 = (double)float1 * 0.5;
		double double2 = (double)float2 * 0.5;
		double double3 = (double)float3 * 0.5;
		double double4 = double1 * double1 + double2 * double2 + double3 * double3;
		double double5;
		if (double4 * double4 / 24.0 < 9.99999993922529E-9) {
			this.w = (float)(1.0 - double4 / 2.0);
			double5 = 1.0 - double4 / 6.0;
		} else {
			double double6 = Math.sqrt(double4);
			this.w = (float)Math.cos(double6);
			double5 = Math.sin(double6) / double6;
		}

		this.x = (float)(double1 * double5);
		this.y = (float)(double2 * double5);
		this.z = (float)(double3 * double5);
		return this;
	}

	public Quaternionf rotationX(float float1) {
		float float2 = (float)Math.cos((double)float1 * 0.5);
		float float3 = (float)Math.sin((double)float1 * 0.5);
		this.w = float2;
		this.x = float3;
		this.y = 0.0F;
		this.z = 0.0F;
		return this;
	}

	public Quaternionf rotationY(float float1) {
		float float2 = (float)Math.cos((double)float1 * 0.5);
		float float3 = (float)Math.sin((double)float1 * 0.5);
		this.w = float2;
		this.x = 0.0F;
		this.y = float3;
		this.z = 0.0F;
		return this;
	}

	public Quaternionf rotationZ(float float1) {
		float float2 = (float)Math.cos((double)float1 * 0.5);
		float float3 = (float)Math.sin((double)float1 * 0.5);
		this.w = float2;
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = float3;
		return this;
	}

	private void setFromUnnormalized(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		float float10 = (float)(1.0 / Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3)));
		float float11 = (float)(1.0 / Math.sqrt((double)(float4 * float4 + float5 * float5 + float6 * float6)));
		float float12 = (float)(1.0 / Math.sqrt((double)(float7 * float7 + float8 * float8 + float9 * float9)));
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
			float11 = (float)Math.sqrt((double)(float10 + 1.0F));
			this.w = float11 * 0.5F;
			float11 = 0.5F / float11;
			this.x = (float6 - float8) * float11;
			this.y = (float7 - float3) * float11;
			this.z = (float2 - float4) * float11;
		} else if (float1 >= float5 && float1 >= float9) {
			float11 = (float)Math.sqrt((double)(float1 - (float5 + float9)) + 1.0);
			this.x = float11 * 0.5F;
			float11 = 0.5F / float11;
			this.y = (float4 + float2) * float11;
			this.z = (float3 + float7) * float11;
			this.w = (float6 - float8) * float11;
		} else if (float5 > float9) {
			float11 = (float)Math.sqrt((double)(float5 - (float9 + float1)) + 1.0);
			this.y = float11 * 0.5F;
			float11 = 0.5F / float11;
			this.z = (float8 + float6) * float11;
			this.x = (float4 + float2) * float11;
			this.w = (float7 - float3) * float11;
		} else {
			float11 = (float)Math.sqrt((double)(float9 - (float1 + float5)) + 1.0);
			this.z = float11 * 0.5F;
			float11 = 0.5F / float11;
			this.x = (float3 + float7) * float11;
			this.y = (float8 + float6) * float11;
			this.w = (float2 - float4) * float11;
		}
	}

	private void setFromUnnormalized(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double11 = 1.0 / Math.sqrt(double4 * double4 + double5 * double5 + double6 * double6);
		double double12 = 1.0 / Math.sqrt(double7 * double7 + double8 * double8 + double9 * double9);
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
			double11 = (double)((float)Math.sqrt(double5 - (double9 + double1) + 1.0));
			this.y = (float)(double11 * 0.5);
			double11 = 0.5 / double11;
			this.z = (float)((double8 + double6) * double11);
			this.x = (float)((double4 + double2) * double11);
			this.w = (float)((double7 - double3) * double11);
		} else {
			double11 = (double)((float)Math.sqrt(double9 - (double1 + double5) + 1.0));
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
		float float6 = (float)Math.sin((double)float5);
		float float7 = (float)Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3));
		this.x = float1 / float7 * float6;
		this.y = float2 / float7 * float6;
		this.z = float3 / float7 * float6;
		this.w = (float)Math.cos((double)float5);
		return this;
	}

	public Quaternionf fromAxisAngleDeg(Vector3fc vector3fc, float float1) {
		return this.fromAxisAngleRad(vector3fc.x(), vector3fc.y(), vector3fc.z(), (float)Math.toRadians((double)float1));
	}

	public Quaternionf fromAxisAngleDeg(float float1, float float2, float float3, float float4) {
		return this.fromAxisAngleRad(float1, float2, float3, (float)Math.toRadians((double)float4));
	}

	public Quaternionf mul(Quaternionfc quaternionfc) {
		return this.mul(quaternionfc, this);
	}

	public Quaternionf mul(Quaternionfc quaternionfc, Quaternionf quaternionf) {
		quaternionf.set(this.w * quaternionfc.x() + this.x * quaternionfc.w() + this.y * quaternionfc.z() - this.z * quaternionfc.y(), this.w * quaternionfc.y() - this.x * quaternionfc.z() + this.y * quaternionfc.w() + this.z * quaternionfc.x(), this.w * quaternionfc.z() + this.x * quaternionfc.y() - this.y * quaternionfc.x() + this.z * quaternionfc.w(), this.w * quaternionfc.w() - this.x * quaternionfc.x() - this.y * quaternionfc.y() - this.z * quaternionfc.z());
		return quaternionf;
	}

	public Quaternionf mul(float float1, float float2, float float3, float float4) {
		this.set(this.w * float1 + this.x * float4 + this.y * float3 - this.z * float2, this.w * float2 - this.x * float3 + this.y * float4 + this.z * float1, this.w * float3 + this.x * float2 - this.y * float1 + this.z * float4, this.w * float4 - this.x * float1 - this.y * float2 - this.z * float3);
		return this;
	}

	public Quaternionf mul(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
		quaternionf.set(this.w * float1 + this.x * float4 + this.y * float3 - this.z * float2, this.w * float2 - this.x * float3 + this.y * float4 + this.z * float1, this.w * float3 + this.x * float2 - this.y * float1 + this.z * float4, this.w * float4 - this.x * float1 - this.y * float2 - this.z * float3);
		return quaternionf;
	}

	public Quaternionf premul(Quaternionfc quaternionfc) {
		return this.premul(quaternionfc, this);
	}

	public Quaternionf premul(Quaternionfc quaternionfc, Quaternionf quaternionf) {
		quaternionf.set(quaternionfc.w() * this.x + quaternionfc.x() * this.w + quaternionfc.y() * this.z - quaternionfc.z() * this.y, quaternionfc.w() * this.y - quaternionfc.x() * this.z + quaternionfc.y() * this.w + quaternionfc.z() * this.x, quaternionfc.w() * this.z + quaternionfc.x() * this.y - quaternionfc.y() * this.x + quaternionfc.z() * this.w, quaternionfc.w() * this.w - quaternionfc.x() * this.x - quaternionfc.y() * this.y - quaternionfc.z() * this.z);
		return quaternionf;
	}

	public Quaternionf premul(float float1, float float2, float float3, float float4) {
		return this.premul(float1, float2, float3, float4, this);
	}

	public Quaternionf premul(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
		quaternionf.set(float4 * this.x + float1 * this.w + float2 * this.z - float3 * this.y, float4 * this.y - float1 * this.z + float2 * this.w + float3 * this.x, float4 * this.z + float1 * this.y - float2 * this.x + float3 * this.w, float4 * this.w - float1 * this.x - float2 * this.y - float3 * this.z);
		return quaternionf;
	}

	public Vector3f transform(Vector3f vector3f) {
		return this.transform(vector3f.x, vector3f.y, vector3f.z, vector3f);
	}

	public Vector4f transform(Vector4f vector4f) {
		return this.transform((Vector4fc)vector4f, (Vector4f)vector4f);
	}

	public Vector3f transform(Vector3fc vector3fc, Vector3f vector3f) {
		return this.transform(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3f);
	}

	public Vector3f transform(float float1, float float2, float float3, Vector3f vector3f) {
		float float4 = this.w * this.w;
		float float5 = this.x * this.x;
		float float6 = this.y * this.y;
		float float7 = this.z * this.z;
		float float8 = this.z * this.w;
		float float9 = this.x * this.y;
		float float10 = this.x * this.z;
		float float11 = this.y * this.w;
		float float12 = this.y * this.z;
		float float13 = this.x * this.w;
		float float14 = float4 + float5 - float7 - float6;
		float float15 = float9 + float8 + float8 + float9;
		float float16 = float10 - float11 + float10 - float11;
		float float17 = -float8 + float9 - float8 + float9;
		float float18 = float6 - float7 + float4 - float5;
		float float19 = float12 + float12 + float13 + float13;
		float float20 = float11 + float10 + float10 + float11;
		float float21 = float12 + float12 - float13 - float13;
		float float22 = float7 - float6 - float5 + float4;
		vector3f.x = float14 * float1 + float17 * float2 + float20 * float3;
		vector3f.y = float15 * float1 + float18 * float2 + float21 * float3;
		vector3f.z = float16 * float1 + float19 * float2 + float22 * float3;
		return vector3f;
	}

	public Vector4f transform(Vector4fc vector4fc, Vector4f vector4f) {
		return this.transform(vector4fc.x(), vector4fc.y(), vector4fc.z(), vector4f);
	}

	public Vector4f transform(float float1, float float2, float float3, Vector4f vector4f) {
		float float4 = this.w * this.w;
		float float5 = this.x * this.x;
		float float6 = this.y * this.y;
		float float7 = this.z * this.z;
		float float8 = this.z * this.w;
		float float9 = this.x * this.y;
		float float10 = this.x * this.z;
		float float11 = this.y * this.w;
		float float12 = this.y * this.z;
		float float13 = this.x * this.w;
		float float14 = float4 + float5 - float7 - float6;
		float float15 = float9 + float8 + float8 + float9;
		float float16 = float10 - float11 + float10 - float11;
		float float17 = -float8 + float9 - float8 + float9;
		float float18 = float6 - float7 + float4 - float5;
		float float19 = float12 + float12 + float13 + float13;
		float float20 = float11 + float10 + float10 + float11;
		float float21 = float12 + float12 - float13 - float13;
		float float22 = float7 - float6 - float5 + float4;
		vector4f.x = float14 * float1 + float17 * float2 + float20 * float3;
		vector4f.y = float15 * float1 + float18 * float2 + float21 * float3;
		vector4f.z = float16 * float1 + float19 * float2 + float22 * float3;
		return vector4f;
	}

	public Quaternionf invert(Quaternionf quaternionf) {
		float float1 = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
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
		float float1 = 1.0F / (quaternionfc.x() * quaternionfc.x() + quaternionfc.y() * quaternionfc.y() + quaternionfc.z() * quaternionfc.z() + quaternionfc.w() * quaternionfc.w());
		float float2 = -quaternionfc.x() * float1;
		float float3 = -quaternionfc.y() * float1;
		float float4 = -quaternionfc.z() * float1;
		float float5 = quaternionfc.w() * float1;
		quaternionf.set(this.w * float2 + this.x * float5 + this.y * float4 - this.z * float3, this.w * float3 - this.x * float4 + this.y * float5 + this.z * float2, this.w * float4 + this.x * float3 - this.y * float2 + this.z * float5, this.w * float5 - this.x * float2 - this.y * float3 - this.z * float4);
		return quaternionf;
	}

	public Quaternionf div(Quaternionfc quaternionfc) {
		return this.div(quaternionfc, this);
	}

	public Quaternionf conjugate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	public Quaternionf conjugate(Quaternionf quaternionf) {
		quaternionf.x = -this.x;
		quaternionf.y = -this.y;
		quaternionf.z = -this.z;
		quaternionf.w = this.w;
		return quaternionf;
	}

	public Quaternionf identity() {
		MemUtil.INSTANCE.identity(this);
		return this;
	}

	public Quaternionf rotateXYZ(float float1, float float2, float float3) {
		return this.rotateXYZ(float1, float2, float3, this);
	}

	public Quaternionf rotateXYZ(float float1, float float2, float float3, Quaternionf quaternionf) {
		float float4 = (float)Math.sin((double)float1 * 0.5);
		float float5 = (float)Math.cos((double)float1 * 0.5);
		float float6 = (float)Math.sin((double)float2 * 0.5);
		float float7 = (float)Math.cos((double)float2 * 0.5);
		float float8 = (float)Math.sin((double)float3 * 0.5);
		float float9 = (float)Math.cos((double)float3 * 0.5);
		float float10 = float7 * float9;
		float float11 = float6 * float8;
		float float12 = float6 * float9;
		float float13 = float7 * float8;
		float float14 = float5 * float10 - float4 * float11;
		float float15 = float4 * float10 + float5 * float11;
		float float16 = float5 * float12 - float4 * float13;
		float float17 = float5 * float13 + float4 * float12;
		quaternionf.set(this.w * float15 + this.x * float14 + this.y * float17 - this.z * float16, this.w * float16 - this.x * float17 + this.y * float14 + this.z * float15, this.w * float17 + this.x * float16 - this.y * float15 + this.z * float14, this.w * float14 - this.x * float15 - this.y * float16 - this.z * float17);
		return quaternionf;
	}

	public Quaternionf rotateZYX(float float1, float float2, float float3) {
		return this.rotateZYX(float1, float2, float3, this);
	}

	public Quaternionf rotateZYX(float float1, float float2, float float3, Quaternionf quaternionf) {
		float float4 = (float)Math.sin((double)float3 * 0.5);
		float float5 = (float)Math.cos((double)float3 * 0.5);
		float float6 = (float)Math.sin((double)float2 * 0.5);
		float float7 = (float)Math.cos((double)float2 * 0.5);
		float float8 = (float)Math.sin((double)float1 * 0.5);
		float float9 = (float)Math.cos((double)float1 * 0.5);
		float float10 = float7 * float9;
		float float11 = float6 * float8;
		float float12 = float6 * float9;
		float float13 = float7 * float8;
		float float14 = float5 * float10 + float4 * float11;
		float float15 = float4 * float10 - float5 * float11;
		float float16 = float5 * float12 + float4 * float13;
		float float17 = float5 * float13 - float4 * float12;
		quaternionf.set(this.w * float15 + this.x * float14 + this.y * float17 - this.z * float16, this.w * float16 - this.x * float17 + this.y * float14 + this.z * float15, this.w * float17 + this.x * float16 - this.y * float15 + this.z * float14, this.w * float14 - this.x * float15 - this.y * float16 - this.z * float17);
		return quaternionf;
	}

	public Quaternionf rotateYXZ(float float1, float float2, float float3) {
		return this.rotateYXZ(float1, float2, float3, this);
	}

	public Quaternionf rotateYXZ(float float1, float float2, float float3, Quaternionf quaternionf) {
		float float4 = (float)Math.sin((double)float2 * 0.5);
		float float5 = (float)Math.cos((double)float2 * 0.5);
		float float6 = (float)Math.sin((double)float1 * 0.5);
		float float7 = (float)Math.cos((double)float1 * 0.5);
		float float8 = (float)Math.sin((double)float3 * 0.5);
		float float9 = (float)Math.cos((double)float3 * 0.5);
		float float10 = float7 * float4;
		float float11 = float6 * float5;
		float float12 = float6 * float4;
		float float13 = float7 * float5;
		float float14 = float10 * float9 + float11 * float8;
		float float15 = float11 * float9 - float10 * float8;
		float float16 = float13 * float8 - float12 * float9;
		float float17 = float13 * float9 + float12 * float8;
		quaternionf.set(this.w * float14 + this.x * float17 + this.y * float16 - this.z * float15, this.w * float15 - this.x * float16 + this.y * float17 + this.z * float14, this.w * float16 + this.x * float15 - this.y * float14 + this.z * float17, this.w * float17 - this.x * float14 - this.y * float15 - this.z * float16);
		return quaternionf;
	}

	public Vector3f getEulerAnglesXYZ(Vector3f vector3f) {
		vector3f.x = (float)Math.atan2(2.0 * (double)(this.x * this.w - this.y * this.z), 1.0 - 2.0 * (double)(this.x * this.x + this.y * this.y));
		vector3f.y = (float)Math.asin(2.0 * (double)(this.x * this.z + this.y * this.w));
		vector3f.z = (float)Math.atan2(2.0 * (double)(this.z * this.w - this.x * this.y), 1.0 - 2.0 * (double)(this.y * this.y + this.z * this.z));
		return vector3f;
	}

	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}

	public Quaternionf rotationXYZ(float float1, float float2, float float3) {
		float float4 = (float)Math.sin((double)float1 * 0.5);
		float float5 = (float)Math.cos((double)float1 * 0.5);
		float float6 = (float)Math.sin((double)float2 * 0.5);
		float float7 = (float)Math.cos((double)float2 * 0.5);
		float float8 = (float)Math.sin((double)float3 * 0.5);
		float float9 = (float)Math.cos((double)float3 * 0.5);
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
		float float4 = (float)Math.sin((double)float3 * 0.5);
		float float5 = (float)Math.cos((double)float3 * 0.5);
		float float6 = (float)Math.sin((double)float2 * 0.5);
		float float7 = (float)Math.cos((double)float2 * 0.5);
		float float8 = (float)Math.sin((double)float1 * 0.5);
		float float9 = (float)Math.cos((double)float1 * 0.5);
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
		float float4 = (float)Math.sin((double)float2 * 0.5);
		float float5 = (float)Math.cos((double)float2 * 0.5);
		float float6 = (float)Math.sin((double)float1 * 0.5);
		float float7 = (float)Math.cos((double)float1 * 0.5);
		float float8 = (float)Math.sin((double)float3 * 0.5);
		float float9 = (float)Math.cos((double)float3 * 0.5);
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
		float float2 = this.x * quaternionfc.x() + this.y * quaternionfc.y() + this.z * quaternionfc.z() + this.w * quaternionfc.w();
		float float3 = Math.abs(float2);
		float float4;
		float float5;
		if (1.0F - float3 > 1.0E-6F) {
			float float6 = 1.0F - float3 * float3;
			float float7 = (float)(1.0 / Math.sqrt((double)float6));
			float float8 = (float)Math.atan2((double)(float6 * float7), (double)float3);
			float4 = (float)(Math.sin((1.0 - (double)float1) * (double)float8) * (double)float7);
			float5 = (float)(Math.sin((double)(float1 * float8)) * (double)float7);
		} else {
			float4 = 1.0F - float1;
			float5 = float1;
		}

		float5 = float2 >= 0.0F ? float5 : -float5;
		quaternionf.x = float4 * this.x + float5 * quaternionfc.x();
		quaternionf.y = float4 * this.y + float5 * quaternionfc.y();
		quaternionf.z = float4 * this.z + float5 * quaternionfc.z();
		quaternionf.w = float4 * this.w + float5 * quaternionfc.w();
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
		float float2 = (float)Math.sqrt((double)float1);
		quaternionf.x = float2 * this.x;
		quaternionf.y = float2 * this.y;
		quaternionf.z = float2 * this.z;
		quaternionf.w = float2 * this.w;
		return this;
	}

	public Quaternionf scaling(float float1) {
		float float2 = (float)Math.sqrt((double)float1);
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
		return this.rotateLocal(float1 * float2, float1 * float3, float1 * float4, quaternionf);
	}

	public Quaternionf nlerp(Quaternionfc quaternionfc, float float1) {
		return this.nlerp(quaternionfc, float1, this);
	}

	public Quaternionf nlerp(Quaternionfc quaternionfc, float float1, Quaternionf quaternionf) {
		float float2 = this.x * quaternionfc.x() + this.y * quaternionfc.y() + this.z * quaternionfc.z() + this.w * quaternionfc.w();
		float float3 = 1.0F - float1;
		float float4 = float2 >= 0.0F ? float1 : -float1;
		quaternionf.x = float3 * this.x + float4 * quaternionfc.x();
		quaternionf.y = float3 * this.y + float4 * quaternionfc.y();
		quaternionf.z = float3 * this.z + float4 * quaternionfc.z();
		quaternionf.w = float3 * this.w + float4 * quaternionfc.w();
		float float5 = (float)(1.0 / Math.sqrt((double)(quaternionf.x * quaternionf.x + quaternionf.y * quaternionf.y + quaternionf.z * quaternionf.z + quaternionf.w * quaternionf.w)));
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
		float float11 = float3 * float7 + float4 * float8 + float5 * float9 + float6 * float10;
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
					float7 = float14 * float7 + float15 * float3;
					float8 = float14 * float8 + float15 * float4;
					float9 = float14 * float9 + float15 * float5;
					float10 = float14 * float10 + float15 * float6;
					float16 = (float)(1.0 / Math.sqrt((double)(float7 * float7 + float8 * float8 + float9 * float9 + float10 * float10)));
					float7 *= float16;
					float8 *= float16;
					float9 *= float16;
					float10 *= float16;
					float13 += float13;
				} else {
					float3 = float14 * float3 + float15 * float7;
					float4 = float14 * float4 + float15 * float8;
					float5 = float14 * float5 + float15 * float9;
					float6 = float14 * float6 + float15 * float10;
					float16 = (float)(1.0 / Math.sqrt((double)(float3 * float3 + float4 * float4 + float5 * float5 + float6 * float6)));
					float3 *= float16;
					float4 *= float16;
					float5 *= float16;
					float6 *= float16;
					float13 = float13 + float13 - 1.0F;
				}

				float11 = float3 * float7 + float4 * float8 + float5 * float9 + float6 * float10;
			}

			float14 = 1.0F - float13;
			float15 = float11 >= 0.0F ? float13 : -float13;
			float16 = float14 * float3 + float15 * float7;
			float float17 = float14 * float4 + float15 * float8;
			float float18 = float14 * float5 + float15 * float9;
			float float19 = float14 * float6 + float15 * float10;
			float float20 = (float)(1.0 / Math.sqrt((double)(float16 * float16 + float17 * float17 + float18 * float18 + float19 * float19)));
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
		float float7 = (float)(1.0 / Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3)));
		float float8 = float1 * float7;
		float float9 = float2 * float7;
		float float10 = float3 * float7;
		float float11 = float5 * float10 - float6 * float9;
		float float12 = float6 * float8 - float4 * float10;
		float float13 = float4 * float9 - float5 * float8;
		float float14 = (float)(1.0 / Math.sqrt((double)(float11 * float11 + float12 * float12 + float13 * float13)));
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

		quaternionf.set(this.w * float18 + this.x * float21 + this.y * float20 - this.z * float19, this.w * float19 - this.x * float20 + this.y * float21 + this.z * float18, this.w * float20 + this.x * float19 - this.y * float18 + this.z * float21, this.w * float21 - this.x * float18 - this.y * float19 - this.z * float20);
		return quaternionf;
	}

	public Quaternionf rotationTo(float float1, float float2, float float3, float float4, float float5, float float6) {
		this.x = float2 * float6 - float3 * float5;
		this.y = float3 * float4 - float1 * float6;
		this.z = float1 * float5 - float2 * float4;
		this.w = (float)Math.sqrt((double)((float1 * float1 + float2 * float2 + float3 * float3) * (float4 * float4 + float5 * float5 + float6 * float6))) + float1 * float4 + float2 * float5 + float3 * float6;
		float float7 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w)));
		if (Float.isInfinite(float7)) {
			this.x = float5;
			this.y = -float4;
			this.z = 0.0F;
			this.w = 0.0F;
			float7 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y)));
			if (Float.isInfinite(float7)) {
				this.x = 0.0F;
				this.y = float6;
				this.z = -float5;
				this.w = 0.0F;
				float7 = (float)(1.0 / Math.sqrt((double)(this.y * this.y + this.z * this.z)));
			}
		}

		this.x *= float7;
		this.y *= float7;
		this.z *= float7;
		this.w *= float7;
		return this;
	}

	public Quaternionf rotationTo(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.rotationTo(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Quaternionf rotateTo(float float1, float float2, float float3, float float4, float float5, float float6, Quaternionf quaternionf) {
		float float7 = float2 * float6 - float3 * float5;
		float float8 = float3 * float4 - float1 * float6;
		float float9 = float1 * float5 - float2 * float4;
		float float10 = (float)Math.sqrt((double)((float1 * float1 + float2 * float2 + float3 * float3) * (float4 * float4 + float5 * float5 + float6 * float6))) + float1 * float4 + float2 * float5 + float3 * float6;
		float float11 = (float)(1.0 / Math.sqrt((double)(float7 * float7 + float8 * float8 + float9 * float9 + float10 * float10)));
		if (Float.isInfinite(float11)) {
			float7 = float5;
			float8 = -float4;
			float9 = 0.0F;
			float10 = 0.0F;
			float11 = (float)(1.0 / Math.sqrt((double)(float5 * float5 + float8 * float8)));
			if (Float.isInfinite(float11)) {
				float7 = 0.0F;
				float8 = float6;
				float9 = -float5;
				float10 = 0.0F;
				float11 = (float)(1.0 / Math.sqrt((double)(float6 * float6 + float9 * float9)));
			}
		}

		float7 *= float11;
		float8 *= float11;
		float9 *= float11;
		float10 *= float11;
		quaternionf.set(this.w * float7 + this.x * float10 + this.y * float9 - this.z * float8, this.w * float8 - this.x * float9 + this.y * float10 + this.z * float7, this.w * float9 + this.x * float8 - this.y * float7 + this.z * float10, this.w * float10 - this.x * float7 - this.y * float8 - this.z * float9);
		return quaternionf;
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

	public Quaternionf rotate(float float1, float float2, float float3) {
		return this.rotate(float1, float2, float3, this);
	}

	public Quaternionf rotate(float float1, float float2, float float3, Quaternionf quaternionf) {
		double double1 = (double)float1 * 0.5;
		double double2 = (double)float2 * 0.5;
		double double3 = (double)float3 * 0.5;
		double double4 = double1 * double1 + double2 * double2 + double3 * double3;
		double double5;
		double double6;
		if (double4 * double4 / 24.0 < 9.99999993922529E-9) {
			double6 = 1.0 - double4 / 2.0;
			double5 = 1.0 - double4 / 6.0;
		} else {
			double double7 = Math.sqrt(double4);
			double6 = Math.cos(double7);
			double5 = Math.sin(double7) / double7;
		}

		double double8 = double1 * double5;
		double double9 = double2 * double5;
		double double10 = double3 * double5;
		quaternionf.set((float)((double)this.w * double8 + (double)this.x * double6 + (double)this.y * double10 - (double)this.z * double9), (float)((double)this.w * double9 - (double)this.x * double10 + (double)this.y * double6 + (double)this.z * double8), (float)((double)this.w * double10 + (double)this.x * double9 - (double)this.y * double8 + (double)this.z * double6), (float)((double)this.w * double6 - (double)this.x * double8 - (double)this.y * double9 - (double)this.z * double10));
		return quaternionf;
	}

	public Quaternionf rotateLocal(float float1, float float2, float float3) {
		return this.rotateLocal(float1, float2, float3, this);
	}

	public Quaternionf rotateLocal(float float1, float float2, float float3, Quaternionf quaternionf) {
		float float4 = float1 * 0.5F;
		float float5 = float2 * 0.5F;
		float float6 = float3 * 0.5F;
		float float7 = float4 * float4 + float5 * float5 + float6 * float6;
		float float8;
		float float9;
		if (float7 * float7 / 24.0F < 1.0E-8F) {
			float9 = 1.0F - float7 * 0.5F;
			float8 = 1.0F - float7 / 6.0F;
		} else {
			float float10 = (float)Math.sqrt((double)float7);
			float9 = (float)Math.cos((double)float10);
			float8 = (float)(Math.sin((double)float10) / (double)float10);
		}

		float float11 = float4 * float8;
		float float12 = float5 * float8;
		float float13 = float6 * float8;
		quaternionf.set(float9 * this.x + float11 * this.w + float12 * this.z - float13 * this.y, float9 * this.y - float11 * this.z + float12 * this.w + float13 * this.x, float9 * this.z + float11 * this.y - float12 * this.x + float13 * this.w, float9 * this.w - float11 * this.x - float12 * this.y - float13 * this.z);
		return quaternionf;
	}

	public Quaternionf rotateX(float float1) {
		return this.rotateX(float1, this);
	}

	public Quaternionf rotateX(float float1, Quaternionf quaternionf) {
		float float2 = (float)Math.cos((double)float1 * 0.5);
		float float3 = (float)Math.sin((double)float1 * 0.5);
		quaternionf.set(this.w * float3 + this.x * float2, this.y * float2 + this.z * float3, this.z * float2 - this.y * float3, this.w * float2 - this.x * float3);
		return quaternionf;
	}

	public Quaternionf rotateY(float float1) {
		return this.rotateY(float1, this);
	}

	public Quaternionf rotateY(float float1, Quaternionf quaternionf) {
		float float2 = (float)Math.cos((double)float1 * 0.5);
		float float3 = (float)Math.sin((double)float1 * 0.5);
		quaternionf.set(this.x * float2 - this.z * float3, this.w * float3 + this.y * float2, this.x * float3 + this.z * float2, this.w * float2 - this.y * float3);
		return quaternionf;
	}

	public Quaternionf rotateZ(float float1) {
		return this.rotateZ(float1, this);
	}

	public Quaternionf rotateZ(float float1, Quaternionf quaternionf) {
		float float2 = (float)Math.cos((double)float1 * 0.5);
		float float3 = (float)Math.sin((double)float1 * 0.5);
		quaternionf.set(this.x * float2 + this.y * float3, this.y * float2 - this.x * float3, this.w * float3 + this.z * float2, this.w * float2 - this.z * float3);
		return quaternionf;
	}

	public Quaternionf rotateLocalX(float float1) {
		return this.rotateLocalX(float1, this);
	}

	public Quaternionf rotateLocalX(float float1, Quaternionf quaternionf) {
		float float2 = float1 * 0.5F;
		float float3 = (float)Math.sin((double)float2);
		float float4 = (float)Math.cos((double)float2);
		quaternionf.set(float4 * this.x + float3 * this.w, float4 * this.y - float3 * this.z, float4 * this.z + float3 * this.y, float4 * this.w - float3 * this.x);
		return quaternionf;
	}

	public Quaternionf rotateLocalY(float float1) {
		return this.rotateLocalY(float1, this);
	}

	public Quaternionf rotateLocalY(float float1, Quaternionf quaternionf) {
		float float2 = float1 * 0.5F;
		float float3 = (float)Math.sin((double)float2);
		float float4 = (float)Math.cos((double)float2);
		quaternionf.set(float4 * this.x + float3 * this.z, float4 * this.y + float3 * this.w, float4 * this.z - float3 * this.x, float4 * this.w - float3 * this.y);
		return quaternionf;
	}

	public Quaternionf rotateLocalZ(float float1) {
		return this.rotateLocalZ(float1, this);
	}

	public Quaternionf rotateLocalZ(float float1, Quaternionf quaternionf) {
		float float2 = float1 * 0.5F;
		float float3 = (float)Math.sin((double)float2);
		float float4 = (float)Math.cos((double)float2);
		quaternionf.set(float4 * this.x - float3 * this.y, float4 * this.y + float3 * this.x, float4 * this.z + float3 * this.w, float4 * this.w - float3 * this.z);
		return quaternionf;
	}

	public Quaternionf rotateAxis(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
		double double1 = (double)float1 / 2.0;
		double double2 = Math.sin(double1);
		double double3 = 1.0 / Math.sqrt((double)(float2 * float2 + float3 * float3 + float4 * float4));
		double double4 = (double)float2 * double3 * double2;
		double double5 = (double)float3 * double3 * double2;
		double double6 = (double)float4 * double3 * double2;
		double double7 = Math.cos(double1);
		quaternionf.set((float)((double)this.w * double4 + (double)this.x * double7 + (double)this.y * double6 - (double)this.z * double5), (float)((double)this.w * double5 - (double)this.x * double6 + (double)this.y * double7 + (double)this.z * double4), (float)((double)this.w * double6 + (double)this.x * double5 - (double)this.y * double4 + (double)this.z * double7), (float)((double)this.w * double7 - (double)this.x * double4 - (double)this.y * double5 - (double)this.z * double6));
		return quaternionf;
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
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format((double)this.x) + " " + numberFormat.format((double)this.y) + " " + numberFormat.format((double)this.z) + " " + numberFormat.format((double)this.w) + ")";
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

	public Quaternionf difference(Quaternionf quaternionf, Quaternionf quaternionf2) {
		float float1 = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
		float float2 = -this.x * float1;
		float float3 = -this.y * float1;
		float float4 = -this.z * float1;
		float float5 = this.w * float1;
		quaternionf2.set(float5 * quaternionf.x + float2 * quaternionf.w + float3 * quaternionf.z - float4 * quaternionf.y, float5 * quaternionf.y - float2 * quaternionf.z + float3 * quaternionf.w + float4 * quaternionf.x, float5 * quaternionf.z + float2 * quaternionf.y - float3 * quaternionf.x + float4 * quaternionf.w, float5 * quaternionf.w - float2 * quaternionf.x - float3 * quaternionf.y - float4 * quaternionf.z);
		return quaternionf2;
	}

	public Vector3f positiveX(Vector3f vector3f) {
		float float1 = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
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
		float float1 = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
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
		float float1 = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
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

	public Quaternionfc toImmutable() {
		return (Quaternionfc)(!Options.DEBUG ? this : new Quaternionf.Proxy(this));
	}

	private final class Proxy implements Quaternionfc {
		private final Quaternionfc delegate;

		Proxy(Quaternionfc quaternionfc) {
			this.delegate = quaternionfc;
		}

		public float x() {
			return this.delegate.x();
		}

		public float y() {
			return this.delegate.y();
		}

		public float z() {
			return this.delegate.z();
		}

		public float w() {
			return this.delegate.w();
		}

		public Quaternionf normalize(Quaternionf quaternionf) {
			return this.delegate.normalize(quaternionf);
		}

		public Quaternionf add(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
			return this.delegate.add(float1, float2, float3, float4, quaternionf);
		}

		public Quaternionf add(Quaternionfc quaternionfc, Quaternionf quaternionf) {
			return this.delegate.add(quaternionfc, quaternionf);
		}

		public float angle() {
			return this.delegate.angle();
		}

		public Matrix3f get(Matrix3f matrix3f) {
			return this.delegate.get(matrix3f);
		}

		public Matrix3d get(Matrix3d matrix3d) {
			return this.delegate.get(matrix3d);
		}

		public Matrix4f get(Matrix4f matrix4f) {
			return this.delegate.get(matrix4f);
		}

		public Matrix4d get(Matrix4d matrix4d) {
			return this.delegate.get(matrix4d);
		}

		public Matrix4x3f get(Matrix4x3f matrix4x3f) {
			return this.delegate.get(matrix4x3f);
		}

		public Matrix4x3d get(Matrix4x3d matrix4x3d) {
			return this.delegate.get(matrix4x3d);
		}

		public AxisAngle4f get(AxisAngle4f axisAngle4f) {
			return this.delegate.get(axisAngle4f);
		}

		public Quaterniond get(Quaterniond quaterniond) {
			return this.delegate.get(quaterniond);
		}

		public Quaternionf get(Quaternionf quaternionf) {
			return this.delegate.get(quaternionf);
		}

		public ByteBuffer getAsMatrix3f(ByteBuffer byteBuffer) {
			return this.delegate.getAsMatrix3f(byteBuffer);
		}

		public FloatBuffer getAsMatrix3f(FloatBuffer floatBuffer) {
			return this.delegate.getAsMatrix3f(floatBuffer);
		}

		public ByteBuffer getAsMatrix4f(ByteBuffer byteBuffer) {
			return this.delegate.getAsMatrix4f(byteBuffer);
		}

		public FloatBuffer getAsMatrix4f(FloatBuffer floatBuffer) {
			return this.delegate.getAsMatrix4f(floatBuffer);
		}

		public ByteBuffer getAsMatrix4x3f(ByteBuffer byteBuffer) {
			return this.delegate.getAsMatrix4x3f(byteBuffer);
		}

		public FloatBuffer getAsMatrix4x3f(FloatBuffer floatBuffer) {
			return this.delegate.getAsMatrix4x3f(floatBuffer);
		}

		public Quaternionf mul(Quaternionfc quaternionfc, Quaternionf quaternionf) {
			return this.delegate.mul(quaternionfc, quaternionf);
		}

		public Quaternionf mul(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
			return this.delegate.mul(float1, float2, float3, float4, quaternionf);
		}

		public Quaternionf premul(Quaternionfc quaternionfc, Quaternionf quaternionf) {
			return this.delegate.premul(quaternionfc, quaternionf);
		}

		public Quaternionf premul(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
			return this.delegate.premul(float1, float2, float3, float4, quaternionf);
		}

		public Vector3f transform(Vector3f vector3f) {
			return this.delegate.transform(vector3f);
		}

		public Vector4f transform(Vector4f vector4f) {
			return this.delegate.transform(vector4f);
		}

		public Vector3f transform(Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.transform(vector3fc, vector3f);
		}

		public Vector3f transform(float float1, float float2, float float3, Vector3f vector3f) {
			return this.delegate.transform(float1, float2, float3, vector3f);
		}

		public Vector4f transform(Vector4fc vector4fc, Vector4f vector4f) {
			return this.delegate.transform(vector4fc, vector4f);
		}

		public Vector4f transform(float float1, float float2, float float3, Vector4f vector4f) {
			return this.delegate.transform(float1, float2, float3, vector4f);
		}

		public Quaternionf invert(Quaternionf quaternionf) {
			return this.delegate.invert(quaternionf);
		}

		public Quaternionf div(Quaternionfc quaternionfc, Quaternionf quaternionf) {
			return this.delegate.div(quaternionfc, quaternionf);
		}

		public Quaternionf conjugate(Quaternionf quaternionf) {
			return this.delegate.conjugate(quaternionf);
		}

		public Quaternionf rotateXYZ(float float1, float float2, float float3, Quaternionf quaternionf) {
			return this.delegate.rotateXYZ(float1, float2, float3, quaternionf);
		}

		public Quaternionf rotateZYX(float float1, float float2, float float3, Quaternionf quaternionf) {
			return this.delegate.rotateZYX(float1, float2, float3, quaternionf);
		}

		public Quaternionf rotateYXZ(float float1, float float2, float float3, Quaternionf quaternionf) {
			return this.delegate.rotateYXZ(float1, float2, float3, quaternionf);
		}

		public Vector3f getEulerAnglesXYZ(Vector3f vector3f) {
			return this.delegate.getEulerAnglesXYZ(vector3f);
		}

		public float lengthSquared() {
			return this.delegate.lengthSquared();
		}

		public Quaternionf slerp(Quaternionfc quaternionfc, float float1, Quaternionf quaternionf) {
			return this.delegate.slerp(quaternionfc, float1, quaternionf);
		}

		public Quaternionf scale(float float1, Quaternionf quaternionf) {
			return this.delegate.scale(float1, quaternionf);
		}

		public Quaternionf integrate(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
			return this.delegate.integrate(float1, float2, float3, float4, quaternionf);
		}

		public Quaternionf nlerp(Quaternionfc quaternionfc, float float1, Quaternionf quaternionf) {
			return this.delegate.nlerp(quaternionfc, float1, quaternionf);
		}

		public Quaternionf nlerpIterative(Quaternionfc quaternionfc, float float1, float float2, Quaternionf quaternionf) {
			return this.delegate.nlerpIterative(quaternionfc, float1, float2, quaternionf);
		}

		public Quaternionf lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2, Quaternionf quaternionf) {
			return this.delegate.lookAlong(vector3fc, vector3fc2, quaternionf);
		}

		public Quaternionf lookAlong(float float1, float float2, float float3, float float4, float float5, float float6, Quaternionf quaternionf) {
			return this.delegate.lookAlong(float1, float2, float3, float4, float5, float6, quaternionf);
		}

		public Quaternionf rotateTo(float float1, float float2, float float3, float float4, float float5, float float6, Quaternionf quaternionf) {
			return this.delegate.rotateTo(float1, float2, float3, float4, float5, float6, quaternionf);
		}

		public Quaternionf rotateTo(Vector3fc vector3fc, Vector3fc vector3fc2, Quaternionf quaternionf) {
			return this.delegate.rotateTo(vector3fc, vector3fc2, quaternionf);
		}

		public Quaternionf rotate(float float1, float float2, float float3, Quaternionf quaternionf) {
			return this.delegate.rotate(float1, float2, float3, quaternionf);
		}

		public Quaternionf rotateLocal(float float1, float float2, float float3, Quaternionf quaternionf) {
			return this.delegate.rotateLocal(float1, float2, float3, quaternionf);
		}

		public Quaternionf rotateX(float float1, Quaternionf quaternionf) {
			return this.delegate.rotateX(float1, quaternionf);
		}

		public Quaternionf rotateY(float float1, Quaternionf quaternionf) {
			return this.delegate.rotateY(float1, quaternionf);
		}

		public Quaternionf rotateZ(float float1, Quaternionf quaternionf) {
			return this.delegate.rotateZ(float1, quaternionf);
		}

		public Quaternionf rotateLocalX(float float1, Quaternionf quaternionf) {
			return this.delegate.rotateLocalX(float1, quaternionf);
		}

		public Quaternionf rotateLocalY(float float1, Quaternionf quaternionf) {
			return this.delegate.rotateLocalY(float1, quaternionf);
		}

		public Quaternionf rotateLocalZ(float float1, Quaternionf quaternionf) {
			return this.delegate.rotateLocalZ(float1, quaternionf);
		}

		public Quaternionf rotateAxis(float float1, float float2, float float3, float float4, Quaternionf quaternionf) {
			return this.delegate.rotateAxis(float1, float2, float3, float4, quaternionf);
		}

		public Quaternionf rotateAxis(float float1, Vector3fc vector3fc, Quaternionf quaternionf) {
			return this.delegate.rotateAxis(float1, vector3fc, quaternionf);
		}

		public Quaternionf difference(Quaternionf quaternionf, Quaternionf quaternionf2) {
			return this.delegate.difference(quaternionf, quaternionf2);
		}

		public Vector3f positiveX(Vector3f vector3f) {
			return this.delegate.positiveX(vector3f);
		}

		public Vector3f normalizedPositiveX(Vector3f vector3f) {
			return this.delegate.normalizedPositiveX(vector3f);
		}

		public Vector3f positiveY(Vector3f vector3f) {
			return this.delegate.positiveY(vector3f);
		}

		public Vector3f normalizedPositiveY(Vector3f vector3f) {
			return this.delegate.normalizedPositiveY(vector3f);
		}

		public Vector3f positiveZ(Vector3f vector3f) {
			return this.delegate.positiveZ(vector3f);
		}

		public Vector3f normalizedPositiveZ(Vector3f vector3f) {
			return this.delegate.normalizedPositiveZ(vector3f);
		}
	}
}
