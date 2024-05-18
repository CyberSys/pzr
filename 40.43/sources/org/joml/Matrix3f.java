package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Matrix3f implements Externalizable,Matrix3fc {
	private static final long serialVersionUID = 1L;
	public float m00;
	public float m01;
	public float m02;
	public float m10;
	public float m11;
	public float m12;
	public float m20;
	public float m21;
	public float m22;

	public Matrix3f() {
		this.m00 = 1.0F;
		this.m11 = 1.0F;
		this.m22 = 1.0F;
	}

	public Matrix3f(Matrix3fc matrix3fc) {
		if (matrix3fc instanceof Matrix3f) {
			MemUtil.INSTANCE.copy((Matrix3f)matrix3fc, this);
		} else {
			this.setMatrix3fc(matrix3fc);
		}
	}

	public Matrix3f(Matrix4fc matrix4fc) {
		if (matrix4fc instanceof Matrix4f) {
			MemUtil.INSTANCE.copy((Matrix4f)matrix4fc, this);
		} else {
			this.setMatrix4fc(matrix4fc);
		}
	}

	public Matrix3f(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		this.m00 = float1;
		this.m01 = float2;
		this.m02 = float3;
		this.m10 = float4;
		this.m11 = float5;
		this.m12 = float6;
		this.m20 = float7;
		this.m21 = float8;
		this.m22 = float9;
	}

	public Matrix3f(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
	}

	public Matrix3f(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		if (vector3fc instanceof Vector3f && vector3fc2 instanceof Vector3f && vector3fc3 instanceof Vector3f) {
			MemUtil.INSTANCE.set(this, (Vector3f)vector3fc, (Vector3f)vector3fc2, (Vector3f)vector3fc3);
		} else {
			this.setVector3fc(vector3fc, vector3fc2, vector3fc3);
		}
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

	public Matrix3f m00(float float1) {
		this.m00 = float1;
		return this;
	}

	public Matrix3f m01(float float1) {
		this.m01 = float1;
		return this;
	}

	public Matrix3f m02(float float1) {
		this.m02 = float1;
		return this;
	}

	public Matrix3f m10(float float1) {
		this.m10 = float1;
		return this;
	}

	public Matrix3f m11(float float1) {
		this.m11 = float1;
		return this;
	}

	public Matrix3f m12(float float1) {
		this.m12 = float1;
		return this;
	}

	public Matrix3f m20(float float1) {
		this.m20 = float1;
		return this;
	}

	public Matrix3f m21(float float1) {
		this.m21 = float1;
		return this;
	}

	public Matrix3f m22(float float1) {
		this.m22 = float1;
		return this;
	}

	public Matrix3f set(Matrix3fc matrix3fc) {
		if (matrix3fc instanceof Matrix3f) {
			MemUtil.INSTANCE.copy((Matrix3f)matrix3fc, this);
		} else {
			this.setMatrix3fc(matrix3fc);
		}

		return this;
	}

	private void setMatrix3fc(Matrix3fc matrix3fc) {
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

	public Matrix3f set(Matrix4fc matrix4fc) {
		if (matrix4fc instanceof Matrix4f) {
			MemUtil.INSTANCE.copy((Matrix4f)matrix4fc, this);
		} else {
			this.setMatrix4fc(matrix4fc);
		}

		return this;
	}

	private void setMatrix4fc(Matrix4fc matrix4fc) {
		this.m00 = matrix4fc.m00();
		this.m01 = matrix4fc.m01();
		this.m02 = matrix4fc.m02();
		this.m10 = matrix4fc.m10();
		this.m11 = matrix4fc.m11();
		this.m12 = matrix4fc.m12();
		this.m20 = matrix4fc.m20();
		this.m21 = matrix4fc.m21();
		this.m22 = matrix4fc.m22();
	}

	public Matrix3f set(AxisAngle4f axisAngle4f) {
		float float1 = axisAngle4f.x;
		float float2 = axisAngle4f.y;
		float float3 = axisAngle4f.z;
		float float4 = axisAngle4f.angle;
		float float5 = (float)(1.0 / Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3)));
		float1 *= float5;
		float2 *= float5;
		float3 *= float5;
		float float6 = (float)Math.cos((double)float4);
		float float7 = (float)Math.sin((double)float4);
		float float8 = 1.0F - float6;
		this.m00 = float6 + float1 * float1 * float8;
		this.m11 = float6 + float2 * float2 * float8;
		this.m22 = float6 + float3 * float3 * float8;
		float float9 = float1 * float2 * float8;
		float float10 = float3 * float7;
		this.m10 = float9 - float10;
		this.m01 = float9 + float10;
		float9 = float1 * float3 * float8;
		float10 = float2 * float7;
		this.m20 = float9 + float10;
		this.m02 = float9 - float10;
		float9 = float2 * float3 * float8;
		float10 = float1 * float7;
		this.m21 = float9 - float10;
		this.m12 = float9 + float10;
		return this;
	}

	public Matrix3f set(AxisAngle4d axisAngle4d) {
		double double1 = axisAngle4d.x;
		double double2 = axisAngle4d.y;
		double double3 = axisAngle4d.z;
		double double4 = axisAngle4d.angle;
		double double5 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double1 *= double5;
		double2 *= double5;
		double3 *= double5;
		double double6 = Math.cos(double4);
		double double7 = Math.sin(double4);
		double double8 = 1.0 - double6;
		this.m00 = (float)(double6 + double1 * double1 * double8);
		this.m11 = (float)(double6 + double2 * double2 * double8);
		this.m22 = (float)(double6 + double3 * double3 * double8);
		double double9 = double1 * double2 * double8;
		double double10 = double3 * double7;
		this.m10 = (float)(double9 - double10);
		this.m01 = (float)(double9 + double10);
		double9 = double1 * double3 * double8;
		double10 = double2 * double7;
		this.m20 = (float)(double9 + double10);
		this.m02 = (float)(double9 - double10);
		double9 = double2 * double3 * double8;
		double10 = double1 * double7;
		this.m21 = (float)(double9 - double10);
		this.m12 = (float)(double9 + double10);
		return this;
	}

	public Matrix3f set(Quaternionfc quaternionfc) {
		return this.rotation(quaternionfc);
	}

	public Matrix3f set(Quaterniondc quaterniondc) {
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
		return this;
	}

	public Matrix3f mul(Matrix3fc matrix3fc) {
		return this.mul(matrix3fc, this);
	}

	public Matrix3f mul(Matrix3fc matrix3fc, Matrix3f matrix3f) {
		float float1 = this.m00 * matrix3fc.m00() + this.m10 * matrix3fc.m01() + this.m20 * matrix3fc.m02();
		float float2 = this.m01 * matrix3fc.m00() + this.m11 * matrix3fc.m01() + this.m21 * matrix3fc.m02();
		float float3 = this.m02 * matrix3fc.m00() + this.m12 * matrix3fc.m01() + this.m22 * matrix3fc.m02();
		float float4 = this.m00 * matrix3fc.m10() + this.m10 * matrix3fc.m11() + this.m20 * matrix3fc.m12();
		float float5 = this.m01 * matrix3fc.m10() + this.m11 * matrix3fc.m11() + this.m21 * matrix3fc.m12();
		float float6 = this.m02 * matrix3fc.m10() + this.m12 * matrix3fc.m11() + this.m22 * matrix3fc.m12();
		float float7 = this.m00 * matrix3fc.m20() + this.m10 * matrix3fc.m21() + this.m20 * matrix3fc.m22();
		float float8 = this.m01 * matrix3fc.m20() + this.m11 * matrix3fc.m21() + this.m21 * matrix3fc.m22();
		float float9 = this.m02 * matrix3fc.m20() + this.m12 * matrix3fc.m21() + this.m22 * matrix3fc.m22();
		matrix3f.m00 = float1;
		matrix3f.m01 = float2;
		matrix3f.m02 = float3;
		matrix3f.m10 = float4;
		matrix3f.m11 = float5;
		matrix3f.m12 = float6;
		matrix3f.m20 = float7;
		matrix3f.m21 = float8;
		matrix3f.m22 = float9;
		return matrix3f;
	}

	public Matrix3f set(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		this.m00 = float1;
		this.m01 = float2;
		this.m02 = float3;
		this.m10 = float4;
		this.m11 = float5;
		this.m12 = float6;
		this.m20 = float7;
		this.m21 = float8;
		this.m22 = float9;
		return this;
	}

	public Matrix3f set(float[] floatArray) {
		MemUtil.INSTANCE.copy(floatArray, 0, (Matrix3f)this);
		return this;
	}

	public Matrix3f set(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		if (vector3fc instanceof Vector3f && vector3fc2 instanceof Vector3f && vector3fc3 instanceof Vector3f) {
			MemUtil.INSTANCE.set(this, (Vector3f)vector3fc, (Vector3f)vector3fc2, (Vector3f)vector3fc3);
		} else {
			this.setVector3fc(vector3fc, vector3fc2, vector3fc3);
		}

		return this;
	}

	private void setVector3fc(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3) {
		this.m00 = vector3fc.x();
		this.m01 = vector3fc.y();
		this.m02 = vector3fc.z();
		this.m10 = vector3fc2.x();
		this.m11 = vector3fc2.y();
		this.m12 = vector3fc2.z();
		this.m20 = vector3fc3.x();
		this.m21 = vector3fc3.y();
		this.m22 = vector3fc3.z();
	}

	public float determinant() {
		return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
	}

	public Matrix3f invert() {
		return this.invert(this);
	}

	public Matrix3f invert(Matrix3f matrix3f) {
		float float1 = this.determinant();
		float1 = 1.0F / float1;
		float float2 = (this.m11 * this.m22 - this.m21 * this.m12) * float1;
		float float3 = (this.m21 * this.m02 - this.m01 * this.m22) * float1;
		float float4 = (this.m01 * this.m12 - this.m11 * this.m02) * float1;
		float float5 = (this.m20 * this.m12 - this.m10 * this.m22) * float1;
		float float6 = (this.m00 * this.m22 - this.m20 * this.m02) * float1;
		float float7 = (this.m10 * this.m02 - this.m00 * this.m12) * float1;
		float float8 = (this.m10 * this.m21 - this.m20 * this.m11) * float1;
		float float9 = (this.m20 * this.m01 - this.m00 * this.m21) * float1;
		float float10 = (this.m00 * this.m11 - this.m10 * this.m01) * float1;
		matrix3f.m00 = float2;
		matrix3f.m01 = float3;
		matrix3f.m02 = float4;
		matrix3f.m10 = float5;
		matrix3f.m11 = float6;
		matrix3f.m12 = float7;
		matrix3f.m20 = float8;
		matrix3f.m21 = float9;
		matrix3f.m22 = float10;
		return matrix3f;
	}

	public Matrix3f transpose() {
		return this.transpose(this);
	}

	public Matrix3f transpose(Matrix3f matrix3f) {
		matrix3f.set(this.m00, this.m10, this.m20, this.m01, this.m11, this.m21, this.m02, this.m12, this.m22);
		return matrix3f;
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat("  0.000E0; -");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return numberFormat.format((double)this.m00) + numberFormat.format((double)this.m10) + numberFormat.format((double)this.m20) + "\n" + numberFormat.format((double)this.m01) + numberFormat.format((double)this.m11) + numberFormat.format((double)this.m21) + "\n" + numberFormat.format((double)this.m02) + numberFormat.format((double)this.m12) + numberFormat.format((double)this.m22) + "\n";
	}

	public Matrix3f get(Matrix3f matrix3f) {
		return matrix3f.set((Matrix3fc)this);
	}

	public Matrix4f get(Matrix4f matrix4f) {
		return matrix4f.set((Matrix3fc)this);
	}

	public AxisAngle4f getRotation(AxisAngle4f axisAngle4f) {
		return axisAngle4f.set((Matrix3fc)this);
	}

	public Quaternionf getUnnormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromUnnormalized((Matrix3fc)this);
	}

	public Quaternionf getNormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromNormalized((Matrix3fc)this);
	}

	public Quaterniond getUnnormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromUnnormalized((Matrix3fc)this);
	}

	public Quaterniond getNormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromNormalized((Matrix3fc)this);
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

	public float[] get(float[] floatArray, int int1) {
		MemUtil.INSTANCE.copy(this, floatArray, int1);
		return floatArray;
	}

	public float[] get(float[] floatArray) {
		return this.get(floatArray, 0);
	}

	public Matrix3f set(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
		return this;
	}

	public Matrix3f set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Matrix3f zero() {
		MemUtil.INSTANCE.zero(this);
		return this;
	}

	public Matrix3f identity() {
		MemUtil.INSTANCE.identity(this);
		return this;
	}

	public Matrix3f scale(Vector3fc vector3fc, Matrix3f matrix3f) {
		return this.scale(vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix3f);
	}

	public Matrix3f scale(Vector3fc vector3fc) {
		return this.scale(vector3fc.x(), vector3fc.y(), vector3fc.z(), this);
	}

	public Matrix3f scale(float float1, float float2, float float3, Matrix3f matrix3f) {
		matrix3f.m00 = this.m00 * float1;
		matrix3f.m01 = this.m01 * float1;
		matrix3f.m02 = this.m02 * float1;
		matrix3f.m10 = this.m10 * float2;
		matrix3f.m11 = this.m11 * float2;
		matrix3f.m12 = this.m12 * float2;
		matrix3f.m20 = this.m20 * float3;
		matrix3f.m21 = this.m21 * float3;
		matrix3f.m22 = this.m22 * float3;
		return matrix3f;
	}

	public Matrix3f scale(float float1, float float2, float float3) {
		return this.scale(float1, float2, float3, this);
	}

	public Matrix3f scale(float float1, Matrix3f matrix3f) {
		return this.scale(float1, float1, float1, matrix3f);
	}

	public Matrix3f scale(float float1) {
		return this.scale(float1, float1, float1);
	}

	public Matrix3f scaleLocal(float float1, float float2, float float3, Matrix3f matrix3f) {
		float float4 = float1 * this.m00;
		float float5 = float2 * this.m01;
		float float6 = float3 * this.m02;
		float float7 = float1 * this.m10;
		float float8 = float2 * this.m11;
		float float9 = float3 * this.m12;
		float float10 = float1 * this.m20;
		float float11 = float2 * this.m21;
		float float12 = float3 * this.m22;
		matrix3f.m00 = float4;
		matrix3f.m01 = float5;
		matrix3f.m02 = float6;
		matrix3f.m10 = float7;
		matrix3f.m11 = float8;
		matrix3f.m12 = float9;
		matrix3f.m20 = float10;
		matrix3f.m21 = float11;
		matrix3f.m22 = float12;
		return matrix3f;
	}

	public Matrix3f scaleLocal(float float1, float float2, float float3) {
		return this.scaleLocal(float1, float2, float3, this);
	}

	public Matrix3f scaling(float float1) {
		MemUtil.INSTANCE.zero(this);
		this.m00 = float1;
		this.m11 = float1;
		this.m22 = float1;
		return this;
	}

	public Matrix3f scaling(float float1, float float2, float float3) {
		MemUtil.INSTANCE.zero(this);
		this.m00 = float1;
		this.m11 = float2;
		this.m22 = float3;
		return this;
	}

	public Matrix3f scaling(Vector3fc vector3fc) {
		return this.scaling(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix3f rotation(float float1, Vector3fc vector3fc) {
		return this.rotation(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix3f rotation(AxisAngle4f axisAngle4f) {
		return this.rotation(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z);
	}

	public Matrix3f rotation(float float1, float float2, float float3, float float4) {
		float float5 = (float)Math.cos((double)float1);
		float float6 = (float)Math.sin((double)float1);
		float float7 = 1.0F - float5;
		float float8 = float2 * float3;
		float float9 = float2 * float4;
		float float10 = float3 * float4;
		this.m00 = float5 + float2 * float2 * float7;
		this.m10 = float8 * float7 - float4 * float6;
		this.m20 = float9 * float7 + float3 * float6;
		this.m01 = float8 * float7 + float4 * float6;
		this.m11 = float5 + float3 * float3 * float7;
		this.m21 = float10 * float7 - float2 * float6;
		this.m02 = float9 * float7 - float3 * float6;
		this.m12 = float10 * float7 + float2 * float6;
		this.m22 = float5 + float4 * float4 * float7;
		return this;
	}

	public Matrix3f rotationX(float float1) {
		float float2;
		float float3;
		if (float1 != 3.1415927F && float1 != -3.1415927F) {
			if (float1 != 1.5707964F && float1 != -4.712389F) {
				if (float1 != -1.5707964F && float1 != 4.712389F) {
					float3 = (float)Math.cos((double)float1);
					float2 = (float)Math.sin((double)float1);
				} else {
					float3 = 0.0F;
					float2 = -1.0F;
				}
			} else {
				float3 = 0.0F;
				float2 = 1.0F;
			}
		} else {
			float3 = -1.0F;
			float2 = 0.0F;
		}

		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = float3;
		this.m12 = float2;
		this.m20 = 0.0F;
		this.m21 = -float2;
		this.m22 = float3;
		return this;
	}

	public Matrix3f rotationY(float float1) {
		float float2;
		float float3;
		if (float1 != 3.1415927F && float1 != -3.1415927F) {
			if (float1 != 1.5707964F && float1 != -4.712389F) {
				if (float1 != -1.5707964F && float1 != 4.712389F) {
					float3 = (float)Math.cos((double)float1);
					float2 = (float)Math.sin((double)float1);
				} else {
					float3 = 0.0F;
					float2 = -1.0F;
				}
			} else {
				float3 = 0.0F;
				float2 = 1.0F;
			}
		} else {
			float3 = -1.0F;
			float2 = 0.0F;
		}

		this.m00 = float3;
		this.m01 = 0.0F;
		this.m02 = -float2;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m20 = float2;
		this.m21 = 0.0F;
		this.m22 = float3;
		return this;
	}

	public Matrix3f rotationZ(float float1) {
		float float2;
		float float3;
		if (float1 != 3.1415927F && float1 != -3.1415927F) {
			if (float1 != 1.5707964F && float1 != -4.712389F) {
				if (float1 != -1.5707964F && float1 != 4.712389F) {
					float3 = (float)Math.cos((double)float1);
					float2 = (float)Math.sin((double)float1);
				} else {
					float3 = 0.0F;
					float2 = -1.0F;
				}
			} else {
				float3 = 0.0F;
				float2 = 1.0F;
			}
		} else {
			float3 = -1.0F;
			float2 = 0.0F;
		}

		this.m00 = float3;
		this.m01 = float2;
		this.m02 = 0.0F;
		this.m10 = -float2;
		this.m11 = float3;
		this.m12 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
		return this;
	}

	public Matrix3f rotationXYZ(float float1, float float2, float float3) {
		float float4 = (float)Math.cos((double)float1);
		float float5 = (float)Math.sin((double)float1);
		float float6 = (float)Math.cos((double)float2);
		float float7 = (float)Math.sin((double)float2);
		float float8 = (float)Math.cos((double)float3);
		float float9 = (float)Math.sin((double)float3);
		float float10 = -float5;
		float float11 = -float7;
		float float12 = -float9;
		float float13 = float10 * float11;
		float float14 = float4 * float11;
		this.m20 = float7;
		this.m21 = float10 * float6;
		this.m22 = float4 * float6;
		this.m00 = float6 * float8;
		this.m01 = float13 * float8 + float4 * float9;
		this.m02 = float14 * float8 + float5 * float9;
		this.m10 = float6 * float12;
		this.m11 = float13 * float12 + float4 * float8;
		this.m12 = float14 * float12 + float5 * float8;
		return this;
	}

	public Matrix3f rotationZYX(float float1, float float2, float float3) {
		float float4 = (float)Math.cos((double)float1);
		float float5 = (float)Math.sin((double)float1);
		float float6 = (float)Math.cos((double)float2);
		float float7 = (float)Math.sin((double)float2);
		float float8 = (float)Math.cos((double)float3);
		float float9 = (float)Math.sin((double)float3);
		float float10 = -float5;
		float float11 = -float7;
		float float12 = -float9;
		float float13 = float4 * float7;
		float float14 = float5 * float7;
		this.m00 = float4 * float6;
		this.m01 = float5 * float6;
		this.m02 = float11;
		this.m10 = float10 * float8 + float13 * float9;
		this.m11 = float4 * float8 + float14 * float9;
		this.m12 = float6 * float9;
		this.m20 = float10 * float12 + float13 * float8;
		this.m21 = float4 * float12 + float14 * float8;
		this.m22 = float6 * float8;
		return this;
	}

	public Matrix3f rotationYXZ(float float1, float float2, float float3) {
		float float4 = (float)Math.cos((double)float1);
		float float5 = (float)Math.sin((double)float1);
		float float6 = (float)Math.cos((double)float2);
		float float7 = (float)Math.sin((double)float2);
		float float8 = (float)Math.cos((double)float3);
		float float9 = (float)Math.sin((double)float3);
		float float10 = -float5;
		float float11 = -float7;
		float float12 = -float9;
		float float13 = float5 * float7;
		float float14 = float4 * float7;
		this.m20 = float5 * float6;
		this.m21 = float11;
		this.m22 = float4 * float6;
		this.m00 = float4 * float8 + float13 * float9;
		this.m01 = float6 * float9;
		this.m02 = float10 * float8 + float14 * float9;
		this.m10 = float4 * float12 + float13 * float8;
		this.m11 = float6 * float8;
		this.m12 = float10 * float12 + float14 * float8;
		return this;
	}

	public Matrix3f rotation(Quaternionfc quaternionfc) {
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
		this.m00 = float1 + float2 - float4 - float3;
		this.m01 = float6 + float5 + float5 + float6;
		this.m02 = float7 - float8 + float7 - float8;
		this.m10 = -float5 + float6 - float5 + float6;
		this.m11 = float3 - float4 + float1 - float2;
		this.m12 = float9 + float9 + float10 + float10;
		this.m20 = float8 + float7 + float7 + float8;
		this.m21 = float9 + float9 - float10 - float10;
		this.m22 = float4 - float3 - float2 + float1;
		return this;
	}

	public Vector3f transform(Vector3f vector3f) {
		return vector3f.mul((Matrix3fc)this);
	}

	public Vector3f transform(Vector3fc vector3fc, Vector3f vector3f) {
		vector3fc.mul((Matrix3fc)this, vector3f);
		return vector3f;
	}

	public Vector3f transform(float float1, float float2, float float3, Vector3f vector3f) {
		vector3f.set(this.m00 * float1 + this.m10 * float2 + this.m20 * float3, this.m01 * float1 + this.m11 * float2 + this.m21 * float3, this.m02 * float1 + this.m12 * float2 + this.m22 * float3);
		return vector3f;
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
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.m00 = objectInput.readFloat();
		this.m01 = objectInput.readFloat();
		this.m02 = objectInput.readFloat();
		this.m10 = objectInput.readFloat();
		this.m11 = objectInput.readFloat();
		this.m12 = objectInput.readFloat();
		this.m20 = objectInput.readFloat();
		this.m21 = objectInput.readFloat();
		this.m22 = objectInput.readFloat();
	}

	public Matrix3f rotateX(float float1, Matrix3f matrix3f) {
		float float2;
		float float3;
		if (float1 != 3.1415927F && float1 != -3.1415927F) {
			if (float1 != 1.5707964F && float1 != -4.712389F) {
				if (float1 != -1.5707964F && float1 != 4.712389F) {
					float3 = (float)Math.cos((double)float1);
					float2 = (float)Math.sin((double)float1);
				} else {
					float3 = 0.0F;
					float2 = -1.0F;
				}
			} else {
				float3 = 0.0F;
				float2 = 1.0F;
			}
		} else {
			float3 = -1.0F;
			float2 = 0.0F;
		}

		float float4 = -float2;
		float float5 = this.m10 * float3 + this.m20 * float2;
		float float6 = this.m11 * float3 + this.m21 * float2;
		float float7 = this.m12 * float3 + this.m22 * float2;
		matrix3f.m20 = this.m10 * float4 + this.m20 * float3;
		matrix3f.m21 = this.m11 * float4 + this.m21 * float3;
		matrix3f.m22 = this.m12 * float4 + this.m22 * float3;
		matrix3f.m10 = float5;
		matrix3f.m11 = float6;
		matrix3f.m12 = float7;
		matrix3f.m00 = this.m00;
		matrix3f.m01 = this.m01;
		matrix3f.m02 = this.m02;
		return matrix3f;
	}

	public Matrix3f rotateX(float float1) {
		return this.rotateX(float1, this);
	}

	public Matrix3f rotateY(float float1, Matrix3f matrix3f) {
		float float2;
		float float3;
		if (float1 != 3.1415927F && float1 != -3.1415927F) {
			if (float1 != 1.5707964F && float1 != -4.712389F) {
				if (float1 != -1.5707964F && float1 != 4.712389F) {
					float3 = (float)Math.cos((double)float1);
					float2 = (float)Math.sin((double)float1);
				} else {
					float3 = 0.0F;
					float2 = -1.0F;
				}
			} else {
				float3 = 0.0F;
				float2 = 1.0F;
			}
		} else {
			float3 = -1.0F;
			float2 = 0.0F;
		}

		float float4 = -float2;
		float float5 = this.m00 * float3 + this.m20 * float4;
		float float6 = this.m01 * float3 + this.m21 * float4;
		float float7 = this.m02 * float3 + this.m22 * float4;
		matrix3f.m20 = this.m00 * float2 + this.m20 * float3;
		matrix3f.m21 = this.m01 * float2 + this.m21 * float3;
		matrix3f.m22 = this.m02 * float2 + this.m22 * float3;
		matrix3f.m00 = float5;
		matrix3f.m01 = float6;
		matrix3f.m02 = float7;
		matrix3f.m10 = this.m10;
		matrix3f.m11 = this.m11;
		matrix3f.m12 = this.m12;
		return matrix3f;
	}

	public Matrix3f rotateY(float float1) {
		return this.rotateY(float1, this);
	}

	public Matrix3f rotateZ(float float1, Matrix3f matrix3f) {
		float float2;
		float float3;
		if (float1 != 3.1415927F && float1 != -3.1415927F) {
			if (float1 != 1.5707964F && float1 != -4.712389F) {
				if (float1 != -1.5707964F && float1 != 4.712389F) {
					float3 = (float)Math.cos((double)float1);
					float2 = (float)Math.sin((double)float1);
				} else {
					float3 = 0.0F;
					float2 = -1.0F;
				}
			} else {
				float3 = 0.0F;
				float2 = 1.0F;
			}
		} else {
			float3 = -1.0F;
			float2 = 0.0F;
		}

		float float4 = -float2;
		float float5 = this.m00 * float3 + this.m10 * float2;
		float float6 = this.m01 * float3 + this.m11 * float2;
		float float7 = this.m02 * float3 + this.m12 * float2;
		matrix3f.m10 = this.m00 * float4 + this.m10 * float3;
		matrix3f.m11 = this.m01 * float4 + this.m11 * float3;
		matrix3f.m12 = this.m02 * float4 + this.m12 * float3;
		matrix3f.m00 = float5;
		matrix3f.m01 = float6;
		matrix3f.m02 = float7;
		matrix3f.m20 = this.m20;
		matrix3f.m21 = this.m21;
		matrix3f.m22 = this.m22;
		return matrix3f;
	}

	public Matrix3f rotateZ(float float1) {
		return this.rotateZ(float1, this);
	}

	public Matrix3f rotateXYZ(Vector3f vector3f) {
		return this.rotateXYZ(vector3f.x, vector3f.y, vector3f.z);
	}

	public Matrix3f rotateXYZ(float float1, float float2, float float3) {
		return this.rotateXYZ(float1, float2, float3, this);
	}

	public Matrix3f rotateXYZ(float float1, float float2, float float3, Matrix3f matrix3f) {
		float float4 = (float)Math.cos((double)float1);
		float float5 = (float)Math.sin((double)float1);
		float float6 = (float)Math.cos((double)float2);
		float float7 = (float)Math.sin((double)float2);
		float float8 = (float)Math.cos((double)float3);
		float float9 = (float)Math.sin((double)float3);
		float float10 = -float5;
		float float11 = -float7;
		float float12 = -float9;
		float float13 = this.m10 * float4 + this.m20 * float5;
		float float14 = this.m11 * float4 + this.m21 * float5;
		float float15 = this.m12 * float4 + this.m22 * float5;
		float float16 = this.m10 * float10 + this.m20 * float4;
		float float17 = this.m11 * float10 + this.m21 * float4;
		float float18 = this.m12 * float10 + this.m22 * float4;
		float float19 = this.m00 * float6 + float16 * float11;
		float float20 = this.m01 * float6 + float17 * float11;
		float float21 = this.m02 * float6 + float18 * float11;
		matrix3f.m20 = this.m00 * float7 + float16 * float6;
		matrix3f.m21 = this.m01 * float7 + float17 * float6;
		matrix3f.m22 = this.m02 * float7 + float18 * float6;
		matrix3f.m00 = float19 * float8 + float13 * float9;
		matrix3f.m01 = float20 * float8 + float14 * float9;
		matrix3f.m02 = float21 * float8 + float15 * float9;
		matrix3f.m10 = float19 * float12 + float13 * float8;
		matrix3f.m11 = float20 * float12 + float14 * float8;
		matrix3f.m12 = float21 * float12 + float15 * float8;
		return matrix3f;
	}

	public Matrix3f rotateZYX(Vector3f vector3f) {
		return this.rotateZYX(vector3f.z, vector3f.y, vector3f.x);
	}

	public Matrix3f rotateZYX(float float1, float float2, float float3) {
		return this.rotateZYX(float1, float2, float3, this);
	}

	public Matrix3f rotateZYX(float float1, float float2, float float3, Matrix3f matrix3f) {
		float float4 = (float)Math.cos((double)float1);
		float float5 = (float)Math.sin((double)float1);
		float float6 = (float)Math.cos((double)float2);
		float float7 = (float)Math.sin((double)float2);
		float float8 = (float)Math.cos((double)float3);
		float float9 = (float)Math.sin((double)float3);
		float float10 = -float5;
		float float11 = -float7;
		float float12 = -float9;
		float float13 = this.m00 * float4 + this.m10 * float5;
		float float14 = this.m01 * float4 + this.m11 * float5;
		float float15 = this.m02 * float4 + this.m12 * float5;
		float float16 = this.m00 * float10 + this.m10 * float4;
		float float17 = this.m01 * float10 + this.m11 * float4;
		float float18 = this.m02 * float10 + this.m12 * float4;
		float float19 = float13 * float7 + this.m20 * float6;
		float float20 = float14 * float7 + this.m21 * float6;
		float float21 = float15 * float7 + this.m22 * float6;
		matrix3f.m00 = float13 * float6 + this.m20 * float11;
		matrix3f.m01 = float14 * float6 + this.m21 * float11;
		matrix3f.m02 = float15 * float6 + this.m22 * float11;
		matrix3f.m10 = float16 * float8 + float19 * float9;
		matrix3f.m11 = float17 * float8 + float20 * float9;
		matrix3f.m12 = float18 * float8 + float21 * float9;
		matrix3f.m20 = float16 * float12 + float19 * float8;
		matrix3f.m21 = float17 * float12 + float20 * float8;
		matrix3f.m22 = float18 * float12 + float21 * float8;
		return matrix3f;
	}

	public Matrix3f rotateYXZ(Vector3f vector3f) {
		return this.rotateYXZ(vector3f.y, vector3f.x, vector3f.z);
	}

	public Matrix3f rotateYXZ(float float1, float float2, float float3) {
		return this.rotateYXZ(float1, float2, float3, this);
	}

	public Matrix3f rotateYXZ(float float1, float float2, float float3, Matrix3f matrix3f) {
		float float4 = (float)Math.cos((double)float1);
		float float5 = (float)Math.sin((double)float1);
		float float6 = (float)Math.cos((double)float2);
		float float7 = (float)Math.sin((double)float2);
		float float8 = (float)Math.cos((double)float3);
		float float9 = (float)Math.sin((double)float3);
		float float10 = -float5;
		float float11 = -float7;
		float float12 = -float9;
		float float13 = this.m00 * float5 + this.m20 * float4;
		float float14 = this.m01 * float5 + this.m21 * float4;
		float float15 = this.m02 * float5 + this.m22 * float4;
		float float16 = this.m00 * float4 + this.m20 * float10;
		float float17 = this.m01 * float4 + this.m21 * float10;
		float float18 = this.m02 * float4 + this.m22 * float10;
		float float19 = this.m10 * float6 + float13 * float7;
		float float20 = this.m11 * float6 + float14 * float7;
		float float21 = this.m12 * float6 + float15 * float7;
		matrix3f.m20 = this.m10 * float11 + float13 * float6;
		matrix3f.m21 = this.m11 * float11 + float14 * float6;
		matrix3f.m22 = this.m12 * float11 + float15 * float6;
		matrix3f.m00 = float16 * float8 + float19 * float9;
		matrix3f.m01 = float17 * float8 + float20 * float9;
		matrix3f.m02 = float18 * float8 + float21 * float9;
		matrix3f.m10 = float16 * float12 + float19 * float8;
		matrix3f.m11 = float17 * float12 + float20 * float8;
		matrix3f.m12 = float18 * float12 + float21 * float8;
		return matrix3f;
	}

	public Matrix3f rotate(float float1, float float2, float float3, float float4) {
		return this.rotate(float1, float2, float3, float4, this);
	}

	public Matrix3f rotate(float float1, float float2, float float3, float float4, Matrix3f matrix3f) {
		float float5 = (float)Math.sin((double)float1);
		float float6 = (float)Math.cos((double)float1);
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
		matrix3f.m20 = this.m00 * float20 + this.m10 * float21 + this.m20 * float22;
		matrix3f.m21 = this.m01 * float20 + this.m11 * float21 + this.m21 * float22;
		matrix3f.m22 = this.m02 * float20 + this.m12 * float21 + this.m22 * float22;
		matrix3f.m00 = float23;
		matrix3f.m01 = float24;
		matrix3f.m02 = float25;
		matrix3f.m10 = float26;
		matrix3f.m11 = float27;
		matrix3f.m12 = float28;
		return matrix3f;
	}

	public Matrix3f rotateLocal(float float1, float float2, float float3, float float4, Matrix3f matrix3f) {
		float float5 = (float)Math.sin((double)float1);
		float float6 = (float)Math.cos((double)float1);
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
		matrix3f.m00 = float23;
		matrix3f.m01 = float24;
		matrix3f.m02 = float25;
		matrix3f.m10 = float26;
		matrix3f.m11 = float27;
		matrix3f.m12 = float28;
		matrix3f.m20 = float29;
		matrix3f.m21 = float30;
		matrix3f.m22 = float31;
		return matrix3f;
	}

	public Matrix3f rotateLocal(float float1, float float2, float float3, float float4) {
		return this.rotateLocal(float1, float2, float3, float4, this);
	}

	public Matrix3f rotate(Quaternionfc quaternionfc) {
		return this.rotate(quaternionfc, this);
	}

	public Matrix3f rotate(Quaternionfc quaternionfc, Matrix3f matrix3f) {
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
		float float20 = this.m00 * float11 + this.m10 * float12 + this.m20 * float13;
		float float21 = this.m01 * float11 + this.m11 * float12 + this.m21 * float13;
		float float22 = this.m02 * float11 + this.m12 * float12 + this.m22 * float13;
		float float23 = this.m00 * float14 + this.m10 * float15 + this.m20 * float16;
		float float24 = this.m01 * float14 + this.m11 * float15 + this.m21 * float16;
		float float25 = this.m02 * float14 + this.m12 * float15 + this.m22 * float16;
		matrix3f.m20 = this.m00 * float17 + this.m10 * float18 + this.m20 * float19;
		matrix3f.m21 = this.m01 * float17 + this.m11 * float18 + this.m21 * float19;
		matrix3f.m22 = this.m02 * float17 + this.m12 * float18 + this.m22 * float19;
		matrix3f.m00 = float20;
		matrix3f.m01 = float21;
		matrix3f.m02 = float22;
		matrix3f.m10 = float23;
		matrix3f.m11 = float24;
		matrix3f.m12 = float25;
		return matrix3f;
	}

	public Matrix3f rotateLocal(Quaternionfc quaternionfc, Matrix3f matrix3f) {
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
		matrix3f.m00 = float20;
		matrix3f.m01 = float21;
		matrix3f.m02 = float22;
		matrix3f.m10 = float23;
		matrix3f.m11 = float24;
		matrix3f.m12 = float25;
		matrix3f.m20 = float26;
		matrix3f.m21 = float27;
		matrix3f.m22 = float28;
		return matrix3f;
	}

	public Matrix3f rotateLocal(Quaternionfc quaternionfc) {
		return this.rotateLocal(quaternionfc, this);
	}

	public Matrix3f rotate(AxisAngle4f axisAngle4f) {
		return this.rotate(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z);
	}

	public Matrix3f rotate(AxisAngle4f axisAngle4f, Matrix3f matrix3f) {
		return this.rotate(axisAngle4f.angle, axisAngle4f.x, axisAngle4f.y, axisAngle4f.z, matrix3f);
	}

	public Matrix3f rotate(float float1, Vector3fc vector3fc) {
		return this.rotate(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public Matrix3f rotate(float float1, Vector3fc vector3fc, Matrix3f matrix3f) {
		return this.rotate(float1, vector3fc.x(), vector3fc.y(), vector3fc.z(), matrix3f);
	}

	public Matrix3f lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.lookAlong(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), this);
	}

	public Matrix3f lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix3f matrix3f) {
		return this.lookAlong(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), matrix3f);
	}

	public Matrix3f lookAlong(float float1, float float2, float float3, float float4, float float5, float float6, Matrix3f matrix3f) {
		float float7 = (float)(1.0 / Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3)));
		float float8 = float1 * float7;
		float float9 = float2 * float7;
		float float10 = float3 * float7;
		float float11 = float9 * float6 - float10 * float5;
		float float12 = float10 * float4 - float8 * float6;
		float float13 = float8 * float5 - float9 * float4;
		float float14 = (float)(1.0 / Math.sqrt((double)(float11 * float11 + float12 * float12 + float13 * float13)));
		float11 *= float14;
		float12 *= float14;
		float13 *= float14;
		float float15 = float12 * float10 - float13 * float9;
		float float16 = float13 * float8 - float11 * float10;
		float float17 = float11 * float9 - float12 * float8;
		float float18 = -float8;
		float float19 = -float9;
		float float20 = -float10;
		float float21 = this.m00 * float11 + this.m10 * float15 + this.m20 * float18;
		float float22 = this.m01 * float11 + this.m11 * float15 + this.m21 * float18;
		float float23 = this.m02 * float11 + this.m12 * float15 + this.m22 * float18;
		float float24 = this.m00 * float12 + this.m10 * float16 + this.m20 * float19;
		float float25 = this.m01 * float12 + this.m11 * float16 + this.m21 * float19;
		float float26 = this.m02 * float12 + this.m12 * float16 + this.m22 * float19;
		matrix3f.m20 = this.m00 * float13 + this.m10 * float17 + this.m20 * float20;
		matrix3f.m21 = this.m01 * float13 + this.m11 * float17 + this.m21 * float20;
		matrix3f.m22 = this.m02 * float13 + this.m12 * float17 + this.m22 * float20;
		matrix3f.m00 = float21;
		matrix3f.m01 = float22;
		matrix3f.m02 = float23;
		matrix3f.m10 = float24;
		matrix3f.m11 = float25;
		matrix3f.m12 = float26;
		return matrix3f;
	}

	public Matrix3f lookAlong(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.lookAlong(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix3f setLookAlong(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.setLookAlong(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix3f setLookAlong(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = (float)(1.0 / Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3)));
		float float8 = float1 * float7;
		float float9 = float2 * float7;
		float float10 = float3 * float7;
		float float11 = float9 * float6 - float10 * float5;
		float float12 = float10 * float4 - float8 * float6;
		float float13 = float8 * float5 - float9 * float4;
		float float14 = (float)(1.0 / Math.sqrt((double)(float11 * float11 + float12 * float12 + float13 * float13)));
		float11 *= float14;
		float12 *= float14;
		float13 *= float14;
		float float15 = float12 * float10 - float13 * float9;
		float float16 = float13 * float8 - float11 * float10;
		float float17 = float11 * float9 - float12 * float8;
		this.m00 = float11;
		this.m01 = float15;
		this.m02 = -float8;
		this.m10 = float12;
		this.m11 = float16;
		this.m12 = -float9;
		this.m20 = float13;
		this.m21 = float17;
		this.m22 = -float10;
		return this;
	}

	public Vector3f getRow(int int1, Vector3f vector3f) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			vector3f.x = this.m00;
			vector3f.y = this.m10;
			vector3f.z = this.m20;
			break;
		
		case 1: 
			vector3f.x = this.m01;
			vector3f.y = this.m11;
			vector3f.z = this.m21;
			break;
		
		case 2: 
			vector3f.x = this.m02;
			vector3f.y = this.m12;
			vector3f.z = this.m22;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector3f;
	}

	public Matrix3f setRow(int int1, Vector3fc vector3fc) throws IndexOutOfBoundsException {
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
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
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
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector3f;
	}

	public Matrix3f setColumn(int int1, Vector3fc vector3fc) throws IndexOutOfBoundsException {
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
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return this;
	}

	public Matrix3f normal() {
		return this.normal(this);
	}

	public Matrix3f normal(Matrix3f matrix3f) {
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
		matrix3f.m00 = float9;
		matrix3f.m01 = float10;
		matrix3f.m02 = float11;
		matrix3f.m10 = float12;
		matrix3f.m11 = float13;
		matrix3f.m12 = float14;
		matrix3f.m20 = float15;
		matrix3f.m21 = float16;
		matrix3f.m22 = float17;
		return matrix3f;
	}

	public Vector3f getScale(Vector3f vector3f) {
		vector3f.x = (float)Math.sqrt((double)(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02));
		vector3f.y = (float)Math.sqrt((double)(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12));
		vector3f.z = (float)Math.sqrt((double)(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22));
		return vector3f;
	}

	public Vector3f positiveZ(Vector3f vector3f) {
		vector3f.x = this.m10 * this.m21 - this.m11 * this.m20;
		vector3f.y = this.m20 * this.m01 - this.m21 * this.m00;
		vector3f.z = this.m00 * this.m11 - this.m01 * this.m10;
		vector3f.normalize();
		return vector3f;
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
		vector3f.normalize();
		return vector3f;
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
		vector3f.normalize();
		return vector3f;
	}

	public Vector3f normalizedPositiveY(Vector3f vector3f) {
		vector3f.x = this.m01;
		vector3f.y = this.m11;
		vector3f.z = this.m21;
		return vector3f;
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
			Matrix3f matrix3f = (Matrix3f)object;
			if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(matrix3f.m00)) {
				return false;
			} else if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(matrix3f.m01)) {
				return false;
			} else if (Float.floatToIntBits(this.m02) != Float.floatToIntBits(matrix3f.m02)) {
				return false;
			} else if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(matrix3f.m10)) {
				return false;
			} else if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(matrix3f.m11)) {
				return false;
			} else if (Float.floatToIntBits(this.m12) != Float.floatToIntBits(matrix3f.m12)) {
				return false;
			} else if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(matrix3f.m20)) {
				return false;
			} else if (Float.floatToIntBits(this.m21) != Float.floatToIntBits(matrix3f.m21)) {
				return false;
			} else {
				return Float.floatToIntBits(this.m22) == Float.floatToIntBits(matrix3f.m22);
			}
		}
	}

	public Matrix3f swap(Matrix3f matrix3f) {
		MemUtil.INSTANCE.swap(this, matrix3f);
		return this;
	}

	public Matrix3f add(Matrix3fc matrix3fc) {
		return this.add(matrix3fc, this);
	}

	public Matrix3f add(Matrix3fc matrix3fc, Matrix3f matrix3f) {
		matrix3f.m00 = this.m00 + matrix3fc.m00();
		matrix3f.m01 = this.m01 + matrix3fc.m01();
		matrix3f.m02 = this.m02 + matrix3fc.m02();
		matrix3f.m10 = this.m10 + matrix3fc.m10();
		matrix3f.m11 = this.m11 + matrix3fc.m11();
		matrix3f.m12 = this.m12 + matrix3fc.m12();
		matrix3f.m20 = this.m20 + matrix3fc.m20();
		matrix3f.m21 = this.m21 + matrix3fc.m21();
		matrix3f.m22 = this.m22 + matrix3fc.m22();
		return matrix3f;
	}

	public Matrix3f sub(Matrix3fc matrix3fc) {
		return this.sub(matrix3fc, this);
	}

	public Matrix3f sub(Matrix3fc matrix3fc, Matrix3f matrix3f) {
		matrix3f.m00 = this.m00 - matrix3fc.m00();
		matrix3f.m01 = this.m01 - matrix3fc.m01();
		matrix3f.m02 = this.m02 - matrix3fc.m02();
		matrix3f.m10 = this.m10 - matrix3fc.m10();
		matrix3f.m11 = this.m11 - matrix3fc.m11();
		matrix3f.m12 = this.m12 - matrix3fc.m12();
		matrix3f.m20 = this.m20 - matrix3fc.m20();
		matrix3f.m21 = this.m21 - matrix3fc.m21();
		matrix3f.m22 = this.m22 - matrix3fc.m22();
		return matrix3f;
	}

	public Matrix3f mulComponentWise(Matrix3fc matrix3fc) {
		return this.mulComponentWise(matrix3fc, this);
	}

	public Matrix3f mulComponentWise(Matrix3fc matrix3fc, Matrix3f matrix3f) {
		matrix3f.m00 = this.m00 * matrix3fc.m00();
		matrix3f.m01 = this.m01 * matrix3fc.m01();
		matrix3f.m02 = this.m02 * matrix3fc.m02();
		matrix3f.m10 = this.m10 * matrix3fc.m10();
		matrix3f.m11 = this.m11 * matrix3fc.m11();
		matrix3f.m12 = this.m12 * matrix3fc.m12();
		matrix3f.m20 = this.m20 * matrix3fc.m20();
		matrix3f.m21 = this.m21 * matrix3fc.m21();
		matrix3f.m22 = this.m22 * matrix3fc.m22();
		return matrix3f;
	}

	public Matrix3f setSkewSymmetric(float float1, float float2, float float3) {
		this.m00 = this.m11 = this.m22 = 0.0F;
		this.m01 = -float1;
		this.m02 = float2;
		this.m10 = float1;
		this.m12 = -float3;
		this.m20 = -float2;
		this.m21 = float3;
		return this;
	}

	public Matrix3f lerp(Matrix3fc matrix3fc, float float1) {
		return this.lerp(matrix3fc, float1, this);
	}

	public Matrix3f lerp(Matrix3fc matrix3fc, float float1, Matrix3f matrix3f) {
		matrix3f.m00 = this.m00 + (matrix3fc.m00() - this.m00) * float1;
		matrix3f.m01 = this.m01 + (matrix3fc.m01() - this.m01) * float1;
		matrix3f.m02 = this.m02 + (matrix3fc.m02() - this.m02) * float1;
		matrix3f.m10 = this.m10 + (matrix3fc.m10() - this.m10) * float1;
		matrix3f.m11 = this.m11 + (matrix3fc.m11() - this.m11) * float1;
		matrix3f.m12 = this.m12 + (matrix3fc.m12() - this.m12) * float1;
		matrix3f.m20 = this.m20 + (matrix3fc.m20() - this.m20) * float1;
		matrix3f.m21 = this.m21 + (matrix3fc.m21() - this.m21) * float1;
		matrix3f.m22 = this.m22 + (matrix3fc.m22() - this.m22) * float1;
		return matrix3f;
	}

	public Matrix3f rotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix3f matrix3f) {
		return this.rotateTowards(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), matrix3f);
	}

	public Matrix3f rotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.rotateTowards(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), this);
	}

	public Matrix3f rotateTowards(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.rotateTowards(float1, float2, float3, float4, float5, float6, this);
	}

	public Matrix3f rotateTowards(float float1, float float2, float float3, float float4, float float5, float float6, Matrix3f matrix3f) {
		float float7 = 1.0F / (float)Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3));
		float float8 = float1 * float7;
		float float9 = float2 * float7;
		float float10 = float3 * float7;
		float float11 = float5 * float10 - float6 * float9;
		float float12 = float6 * float8 - float4 * float10;
		float float13 = float4 * float9 - float5 * float8;
		float float14 = 1.0F / (float)Math.sqrt((double)(float11 * float11 + float12 * float12 + float13 * float13));
		float11 *= float14;
		float12 *= float14;
		float13 *= float14;
		float float15 = float9 * float13 - float10 * float12;
		float float16 = float10 * float11 - float8 * float13;
		float float17 = float8 * float12 - float9 * float11;
		float float18 = this.m00 * float11 + this.m10 * float12 + this.m20 * float13;
		float float19 = this.m01 * float11 + this.m11 * float12 + this.m21 * float13;
		float float20 = this.m02 * float11 + this.m12 * float12 + this.m22 * float13;
		float float21 = this.m00 * float15 + this.m10 * float16 + this.m20 * float17;
		float float22 = this.m01 * float15 + this.m11 * float16 + this.m21 * float17;
		float float23 = this.m02 * float15 + this.m12 * float16 + this.m22 * float17;
		matrix3f.m20 = this.m00 * float8 + this.m10 * float9 + this.m20 * float10;
		matrix3f.m21 = this.m01 * float8 + this.m11 * float9 + this.m21 * float10;
		matrix3f.m22 = this.m02 * float8 + this.m12 * float9 + this.m22 * float10;
		matrix3f.m00 = float18;
		matrix3f.m01 = float19;
		matrix3f.m02 = float20;
		matrix3f.m10 = float21;
		matrix3f.m11 = float22;
		matrix3f.m12 = float23;
		return matrix3f;
	}

	public Matrix3f rotationTowards(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.rotationTowards(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public Matrix3f rotationTowards(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = 1.0F / (float)Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3));
		float float8 = float1 * float7;
		float float9 = float2 * float7;
		float float10 = float3 * float7;
		float float11 = float5 * float10 - float6 * float9;
		float float12 = float6 * float8 - float4 * float10;
		float float13 = float4 * float9 - float5 * float8;
		float float14 = 1.0F / (float)Math.sqrt((double)(float11 * float11 + float12 * float12 + float13 * float13));
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
		return this;
	}

	public Vector3f getEulerAnglesZYX(Vector3f vector3f) {
		vector3f.x = (float)Math.atan2((double)this.m12, (double)this.m22);
		vector3f.y = (float)Math.atan2((double)(-this.m02), (double)((float)Math.sqrt((double)(this.m12 * this.m12 + this.m22 * this.m22))));
		vector3f.z = (float)Math.atan2((double)this.m01, (double)this.m00);
		return vector3f;
	}

	public Matrix3fc toImmutable() {
		return (Matrix3fc)(!Options.DEBUG ? this : new Matrix3f.Proxy(this));
	}

	private final class Proxy implements Matrix3fc {
		private final Matrix3fc delegate;

		Proxy(Matrix3fc matrix3fc) {
			this.delegate = matrix3fc;
		}

		public float m00() {
			return this.delegate.m00();
		}

		public float m01() {
			return this.delegate.m01();
		}

		public float m02() {
			return this.delegate.m02();
		}

		public float m10() {
			return this.delegate.m10();
		}

		public float m11() {
			return this.delegate.m11();
		}

		public float m12() {
			return this.delegate.m12();
		}

		public float m20() {
			return this.delegate.m20();
		}

		public float m21() {
			return this.delegate.m21();
		}

		public float m22() {
			return this.delegate.m22();
		}

		public Matrix3f mul(Matrix3fc matrix3fc, Matrix3f matrix3f) {
			return this.delegate.mul(matrix3fc, matrix3f);
		}

		public float determinant() {
			return this.delegate.determinant();
		}

		public Matrix3f invert(Matrix3f matrix3f) {
			return this.delegate.invert(matrix3f);
		}

		public Matrix3f transpose(Matrix3f matrix3f) {
			return this.delegate.transpose(matrix3f);
		}

		public Matrix3f get(Matrix3f matrix3f) {
			return this.delegate.get(matrix3f);
		}

		public Matrix4f get(Matrix4f matrix4f) {
			return this.delegate.get(matrix4f);
		}

		public AxisAngle4f getRotation(AxisAngle4f axisAngle4f) {
			return this.delegate.getRotation(axisAngle4f);
		}

		public Quaternionf getUnnormalizedRotation(Quaternionf quaternionf) {
			return this.delegate.getUnnormalizedRotation(quaternionf);
		}

		public Quaternionf getNormalizedRotation(Quaternionf quaternionf) {
			return this.delegate.getNormalizedRotation(quaternionf);
		}

		public Quaterniond getUnnormalizedRotation(Quaterniond quaterniond) {
			return this.delegate.getUnnormalizedRotation(quaterniond);
		}

		public Quaterniond getNormalizedRotation(Quaterniond quaterniond) {
			return this.delegate.getNormalizedRotation(quaterniond);
		}

		public FloatBuffer get(FloatBuffer floatBuffer) {
			return this.delegate.get(floatBuffer);
		}

		public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
			return this.delegate.get(int1, floatBuffer);
		}

		public ByteBuffer get(ByteBuffer byteBuffer) {
			return this.delegate.get(byteBuffer);
		}

		public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
			return this.delegate.get(int1, byteBuffer);
		}

		public FloatBuffer getTransposed(FloatBuffer floatBuffer) {
			return this.delegate.getTransposed(floatBuffer);
		}

		public FloatBuffer getTransposed(int int1, FloatBuffer floatBuffer) {
			return this.delegate.getTransposed(int1, floatBuffer);
		}

		public ByteBuffer getTransposed(ByteBuffer byteBuffer) {
			return this.delegate.getTransposed(byteBuffer);
		}

		public ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer) {
			return this.delegate.getTransposed(int1, byteBuffer);
		}

		public float[] get(float[] floatArray, int int1) {
			return this.delegate.get(floatArray, int1);
		}

		public float[] get(float[] floatArray) {
			return this.delegate.get(floatArray);
		}

		public Matrix3f scale(Vector3fc vector3fc, Matrix3f matrix3f) {
			return this.delegate.scale(vector3fc, matrix3f);
		}

		public Matrix3f scale(float float1, float float2, float float3, Matrix3f matrix3f) {
			return this.delegate.scale(float1, float2, float3, matrix3f);
		}

		public Matrix3f scale(float float1, Matrix3f matrix3f) {
			return this.delegate.scale(float1, matrix3f);
		}

		public Matrix3f scaleLocal(float float1, float float2, float float3, Matrix3f matrix3f) {
			return this.delegate.scaleLocal(float1, float2, float3, matrix3f);
		}

		public Vector3f transform(Vector3f vector3f) {
			return this.delegate.transform(vector3f);
		}

		public Vector3f transform(Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.transform(vector3fc, vector3f);
		}

		public Vector3f transform(float float1, float float2, float float3, Vector3f vector3f) {
			return this.delegate.transform(float1, float2, float3, vector3f);
		}

		public Matrix3f rotateX(float float1, Matrix3f matrix3f) {
			return this.delegate.rotateX(float1, matrix3f);
		}

		public Matrix3f rotateY(float float1, Matrix3f matrix3f) {
			return this.delegate.rotateY(float1, matrix3f);
		}

		public Matrix3f rotateZ(float float1, Matrix3f matrix3f) {
			return this.delegate.rotateZ(float1, matrix3f);
		}

		public Matrix3f rotateXYZ(float float1, float float2, float float3, Matrix3f matrix3f) {
			return this.delegate.rotateXYZ(float1, float2, float3, matrix3f);
		}

		public Matrix3f rotateZYX(float float1, float float2, float float3, Matrix3f matrix3f) {
			return this.delegate.rotateZYX(float1, float2, float3, matrix3f);
		}

		public Matrix3f rotateYXZ(float float1, float float2, float float3, Matrix3f matrix3f) {
			return this.delegate.rotateYXZ(float1, float2, float3, matrix3f);
		}

		public Matrix3f rotate(float float1, float float2, float float3, float float4, Matrix3f matrix3f) {
			return this.delegate.rotate(float1, float2, float3, float4, matrix3f);
		}

		public Matrix3f rotateLocal(float float1, float float2, float float3, float float4, Matrix3f matrix3f) {
			return this.delegate.rotateLocal(float1, float2, float3, float4, matrix3f);
		}

		public Matrix3f rotate(Quaternionfc quaternionfc, Matrix3f matrix3f) {
			return this.delegate.rotate(quaternionfc, matrix3f);
		}

		public Matrix3f rotateLocal(Quaternionfc quaternionfc, Matrix3f matrix3f) {
			return this.delegate.rotateLocal(quaternionfc, matrix3f);
		}

		public Matrix3f rotate(AxisAngle4f axisAngle4f, Matrix3f matrix3f) {
			return this.delegate.rotate(axisAngle4f, matrix3f);
		}

		public Matrix3f rotate(float float1, Vector3fc vector3fc, Matrix3f matrix3f) {
			return this.delegate.rotate(float1, vector3fc, matrix3f);
		}

		public Matrix3f lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix3f matrix3f) {
			return this.delegate.lookAlong(vector3fc, vector3fc2, matrix3f);
		}

		public Matrix3f lookAlong(float float1, float float2, float float3, float float4, float float5, float float6, Matrix3f matrix3f) {
			return this.delegate.lookAlong(float1, float2, float3, float4, float5, float6, matrix3f);
		}

		public Vector3f getRow(int int1, Vector3f vector3f) throws IndexOutOfBoundsException {
			return this.delegate.getRow(int1, vector3f);
		}

		public Vector3f getColumn(int int1, Vector3f vector3f) throws IndexOutOfBoundsException {
			return this.delegate.getColumn(int1, vector3f);
		}

		public Matrix3f normal(Matrix3f matrix3f) {
			return this.delegate.normal(matrix3f);
		}

		public Vector3f getScale(Vector3f vector3f) {
			return this.delegate.getScale(vector3f);
		}

		public Vector3f positiveZ(Vector3f vector3f) {
			return this.delegate.positiveZ(vector3f);
		}

		public Vector3f normalizedPositiveZ(Vector3f vector3f) {
			return this.delegate.normalizedPositiveZ(vector3f);
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

		public Matrix3f add(Matrix3fc matrix3fc, Matrix3f matrix3f) {
			return this.delegate.add(matrix3fc, matrix3f);
		}

		public Matrix3f sub(Matrix3fc matrix3fc, Matrix3f matrix3f) {
			return this.delegate.sub(matrix3fc, matrix3f);
		}

		public Matrix3f mulComponentWise(Matrix3fc matrix3fc, Matrix3f matrix3f) {
			return this.delegate.mulComponentWise(matrix3fc, matrix3f);
		}

		public Matrix3f lerp(Matrix3fc matrix3fc, float float1, Matrix3f matrix3f) {
			return this.delegate.lerp(matrix3fc, float1, matrix3f);
		}

		public Matrix3f rotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix3f matrix3f) {
			return this.delegate.rotateTowards(vector3fc, vector3fc2, matrix3f);
		}

		public Matrix3f rotateTowards(float float1, float float2, float float3, float float4, float float5, float float6, Matrix3f matrix3f) {
			return this.delegate.rotateTowards(float1, float2, float3, float4, float5, float6, matrix3f);
		}

		public Vector3f getEulerAnglesZYX(Vector3f vector3f) {
			return this.delegate.getEulerAnglesZYX(vector3f);
		}
	}
}
