package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Matrix3d implements Externalizable,Matrix3dc {
	private static final long serialVersionUID = 1L;
	public double m00;
	public double m01;
	public double m02;
	public double m10;
	public double m11;
	public double m12;
	public double m20;
	public double m21;
	public double m22;

	public Matrix3d() {
		this.m00 = 1.0;
		this.m11 = 1.0;
		this.m22 = 1.0;
	}

	public Matrix3d(Matrix2dc matrix2dc) {
		this.set(matrix2dc);
	}

	public Matrix3d(Matrix2fc matrix2fc) {
		this.set(matrix2fc);
	}

	public Matrix3d(Matrix3dc matrix3dc) {
		this.set(matrix3dc);
	}

	public Matrix3d(Matrix3fc matrix3fc) {
		this.set(matrix3fc);
	}

	public Matrix3d(Matrix4fc matrix4fc) {
		this.set(matrix4fc);
	}

	public Matrix3d(Matrix4dc matrix4dc) {
		this.set(matrix4dc);
	}

	public Matrix3d(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		this.m00 = double1;
		this.m01 = double2;
		this.m02 = double3;
		this.m10 = double4;
		this.m11 = double5;
		this.m12 = double6;
		this.m20 = double7;
		this.m21 = double8;
		this.m22 = double9;
	}

	public Matrix3d(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
	}

	public Matrix3d(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		this.set(vector3dc, vector3dc2, vector3dc3);
	}

	public double m00() {
		return this.m00;
	}

	public double m01() {
		return this.m01;
	}

	public double m02() {
		return this.m02;
	}

	public double m10() {
		return this.m10;
	}

	public double m11() {
		return this.m11;
	}

	public double m12() {
		return this.m12;
	}

	public double m20() {
		return this.m20;
	}

	public double m21() {
		return this.m21;
	}

	public double m22() {
		return this.m22;
	}

	public Matrix3d m00(double double1) {
		this.m00 = double1;
		return this;
	}

	public Matrix3d m01(double double1) {
		this.m01 = double1;
		return this;
	}

	public Matrix3d m02(double double1) {
		this.m02 = double1;
		return this;
	}

	public Matrix3d m10(double double1) {
		this.m10 = double1;
		return this;
	}

	public Matrix3d m11(double double1) {
		this.m11 = double1;
		return this;
	}

	public Matrix3d m12(double double1) {
		this.m12 = double1;
		return this;
	}

	public Matrix3d m20(double double1) {
		this.m20 = double1;
		return this;
	}

	public Matrix3d m21(double double1) {
		this.m21 = double1;
		return this;
	}

	public Matrix3d m22(double double1) {
		this.m22 = double1;
		return this;
	}

	Matrix3d _m00(double double1) {
		this.m00 = double1;
		return this;
	}

	Matrix3d _m01(double double1) {
		this.m01 = double1;
		return this;
	}

	Matrix3d _m02(double double1) {
		this.m02 = double1;
		return this;
	}

	Matrix3d _m10(double double1) {
		this.m10 = double1;
		return this;
	}

	Matrix3d _m11(double double1) {
		this.m11 = double1;
		return this;
	}

	Matrix3d _m12(double double1) {
		this.m12 = double1;
		return this;
	}

	Matrix3d _m20(double double1) {
		this.m20 = double1;
		return this;
	}

	Matrix3d _m21(double double1) {
		this.m21 = double1;
		return this;
	}

	Matrix3d _m22(double double1) {
		this.m22 = double1;
		return this;
	}

	public Matrix3d set(Matrix3dc matrix3dc) {
		this.m00 = matrix3dc.m00();
		this.m01 = matrix3dc.m01();
		this.m02 = matrix3dc.m02();
		this.m10 = matrix3dc.m10();
		this.m11 = matrix3dc.m11();
		this.m12 = matrix3dc.m12();
		this.m20 = matrix3dc.m20();
		this.m21 = matrix3dc.m21();
		this.m22 = matrix3dc.m22();
		return this;
	}

	public Matrix3d setTransposed(Matrix3dc matrix3dc) {
		double double1 = matrix3dc.m01();
		double double2 = matrix3dc.m21();
		double double3 = matrix3dc.m02();
		double double4 = matrix3dc.m12();
		return this._m00(matrix3dc.m00())._m01(matrix3dc.m10())._m02(matrix3dc.m20())._m10(double1)._m11(matrix3dc.m11())._m12(double2)._m20(double3)._m21(double4)._m22(matrix3dc.m22());
	}

	public Matrix3d set(Matrix3fc matrix3fc) {
		this.m00 = (double)matrix3fc.m00();
		this.m01 = (double)matrix3fc.m01();
		this.m02 = (double)matrix3fc.m02();
		this.m10 = (double)matrix3fc.m10();
		this.m11 = (double)matrix3fc.m11();
		this.m12 = (double)matrix3fc.m12();
		this.m20 = (double)matrix3fc.m20();
		this.m21 = (double)matrix3fc.m21();
		this.m22 = (double)matrix3fc.m22();
		return this;
	}

	public Matrix3d setTransposed(Matrix3fc matrix3fc) {
		float float1 = matrix3fc.m01();
		float float2 = matrix3fc.m21();
		float float3 = matrix3fc.m02();
		float float4 = matrix3fc.m12();
		return this._m00((double)matrix3fc.m00())._m01((double)matrix3fc.m10())._m02((double)matrix3fc.m20())._m10((double)float1)._m11((double)matrix3fc.m11())._m12((double)float2)._m20((double)float3)._m21((double)float4)._m22((double)matrix3fc.m22());
	}

	public Matrix3d set(Matrix4x3dc matrix4x3dc) {
		this.m00 = matrix4x3dc.m00();
		this.m01 = matrix4x3dc.m01();
		this.m02 = matrix4x3dc.m02();
		this.m10 = matrix4x3dc.m10();
		this.m11 = matrix4x3dc.m11();
		this.m12 = matrix4x3dc.m12();
		this.m20 = matrix4x3dc.m20();
		this.m21 = matrix4x3dc.m21();
		this.m22 = matrix4x3dc.m22();
		return this;
	}

	public Matrix3d set(Matrix4fc matrix4fc) {
		this.m00 = (double)matrix4fc.m00();
		this.m01 = (double)matrix4fc.m01();
		this.m02 = (double)matrix4fc.m02();
		this.m10 = (double)matrix4fc.m10();
		this.m11 = (double)matrix4fc.m11();
		this.m12 = (double)matrix4fc.m12();
		this.m20 = (double)matrix4fc.m20();
		this.m21 = (double)matrix4fc.m21();
		this.m22 = (double)matrix4fc.m22();
		return this;
	}

	public Matrix3d set(Matrix4dc matrix4dc) {
		this.m00 = matrix4dc.m00();
		this.m01 = matrix4dc.m01();
		this.m02 = matrix4dc.m02();
		this.m10 = matrix4dc.m10();
		this.m11 = matrix4dc.m11();
		this.m12 = matrix4dc.m12();
		this.m20 = matrix4dc.m20();
		this.m21 = matrix4dc.m21();
		this.m22 = matrix4dc.m22();
		return this;
	}

	public Matrix3d set(Matrix2fc matrix2fc) {
		this.m00 = (double)matrix2fc.m00();
		this.m01 = (double)matrix2fc.m01();
		this.m02 = 0.0;
		this.m10 = (double)matrix2fc.m10();
		this.m11 = (double)matrix2fc.m11();
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
		return this;
	}

	public Matrix3d set(Matrix2dc matrix2dc) {
		this.m00 = matrix2dc.m00();
		this.m01 = matrix2dc.m01();
		this.m02 = 0.0;
		this.m10 = matrix2dc.m10();
		this.m11 = matrix2dc.m11();
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
		return this;
	}

	public Matrix3d set(AxisAngle4f axisAngle4f) {
		double double1 = (double)axisAngle4f.x;
		double double2 = (double)axisAngle4f.y;
		double double3 = (double)axisAngle4f.z;
		double double4 = (double)axisAngle4f.angle;
		double double5 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double1 *= double5;
		double2 *= double5;
		double3 *= double5;
		double double6 = Math.sin(double4);
		double double7 = Math.cosFromSin(double6, double4);
		double double8 = 1.0 - double7;
		this.m00 = double7 + double1 * double1 * double8;
		this.m11 = double7 + double2 * double2 * double8;
		this.m22 = double7 + double3 * double3 * double8;
		double double9 = double1 * double2 * double8;
		double double10 = double3 * double6;
		this.m10 = double9 - double10;
		this.m01 = double9 + double10;
		double9 = double1 * double3 * double8;
		double10 = double2 * double6;
		this.m20 = double9 + double10;
		this.m02 = double9 - double10;
		double9 = double2 * double3 * double8;
		double10 = double1 * double6;
		this.m21 = double9 - double10;
		this.m12 = double9 + double10;
		return this;
	}

	public Matrix3d set(AxisAngle4d axisAngle4d) {
		double double1 = axisAngle4d.x;
		double double2 = axisAngle4d.y;
		double double3 = axisAngle4d.z;
		double double4 = axisAngle4d.angle;
		double double5 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double1 *= double5;
		double2 *= double5;
		double3 *= double5;
		double double6 = Math.sin(double4);
		double double7 = Math.cosFromSin(double6, double4);
		double double8 = 1.0 - double7;
		this.m00 = double7 + double1 * double1 * double8;
		this.m11 = double7 + double2 * double2 * double8;
		this.m22 = double7 + double3 * double3 * double8;
		double double9 = double1 * double2 * double8;
		double double10 = double3 * double6;
		this.m10 = double9 - double10;
		this.m01 = double9 + double10;
		double9 = double1 * double3 * double8;
		double10 = double2 * double6;
		this.m20 = double9 + double10;
		this.m02 = double9 - double10;
		double9 = double2 * double3 * double8;
		double10 = double1 * double6;
		this.m21 = double9 - double10;
		this.m12 = double9 + double10;
		return this;
	}

	public Matrix3d set(Quaternionfc quaternionfc) {
		return this.rotation(quaternionfc);
	}

	public Matrix3d set(Quaterniondc quaterniondc) {
		return this.rotation(quaterniondc);
	}

	public Matrix3d mul(Matrix3dc matrix3dc) {
		return this.mul(matrix3dc, this);
	}

	public Matrix3d mul(Matrix3dc matrix3dc, Matrix3d matrix3d) {
		double double1 = Math.fma(this.m00, matrix3dc.m00(), Math.fma(this.m10, matrix3dc.m01(), this.m20 * matrix3dc.m02()));
		double double2 = Math.fma(this.m01, matrix3dc.m00(), Math.fma(this.m11, matrix3dc.m01(), this.m21 * matrix3dc.m02()));
		double double3 = Math.fma(this.m02, matrix3dc.m00(), Math.fma(this.m12, matrix3dc.m01(), this.m22 * matrix3dc.m02()));
		double double4 = Math.fma(this.m00, matrix3dc.m10(), Math.fma(this.m10, matrix3dc.m11(), this.m20 * matrix3dc.m12()));
		double double5 = Math.fma(this.m01, matrix3dc.m10(), Math.fma(this.m11, matrix3dc.m11(), this.m21 * matrix3dc.m12()));
		double double6 = Math.fma(this.m02, matrix3dc.m10(), Math.fma(this.m12, matrix3dc.m11(), this.m22 * matrix3dc.m12()));
		double double7 = Math.fma(this.m00, matrix3dc.m20(), Math.fma(this.m10, matrix3dc.m21(), this.m20 * matrix3dc.m22()));
		double double8 = Math.fma(this.m01, matrix3dc.m20(), Math.fma(this.m11, matrix3dc.m21(), this.m21 * matrix3dc.m22()));
		double double9 = Math.fma(this.m02, matrix3dc.m20(), Math.fma(this.m12, matrix3dc.m21(), this.m22 * matrix3dc.m22()));
		matrix3d.m00 = double1;
		matrix3d.m01 = double2;
		matrix3d.m02 = double3;
		matrix3d.m10 = double4;
		matrix3d.m11 = double5;
		matrix3d.m12 = double6;
		matrix3d.m20 = double7;
		matrix3d.m21 = double8;
		matrix3d.m22 = double9;
		return matrix3d;
	}

	public Matrix3d mulLocal(Matrix3dc matrix3dc) {
		return this.mulLocal(matrix3dc, this);
	}

	public Matrix3d mulLocal(Matrix3dc matrix3dc, Matrix3d matrix3d) {
		double double1 = matrix3dc.m00() * this.m00 + matrix3dc.m10() * this.m01 + matrix3dc.m20() * this.m02;
		double double2 = matrix3dc.m01() * this.m00 + matrix3dc.m11() * this.m01 + matrix3dc.m21() * this.m02;
		double double3 = matrix3dc.m02() * this.m00 + matrix3dc.m12() * this.m01 + matrix3dc.m22() * this.m02;
		double double4 = matrix3dc.m00() * this.m10 + matrix3dc.m10() * this.m11 + matrix3dc.m20() * this.m12;
		double double5 = matrix3dc.m01() * this.m10 + matrix3dc.m11() * this.m11 + matrix3dc.m21() * this.m12;
		double double6 = matrix3dc.m02() * this.m10 + matrix3dc.m12() * this.m11 + matrix3dc.m22() * this.m12;
		double double7 = matrix3dc.m00() * this.m20 + matrix3dc.m10() * this.m21 + matrix3dc.m20() * this.m22;
		double double8 = matrix3dc.m01() * this.m20 + matrix3dc.m11() * this.m21 + matrix3dc.m21() * this.m22;
		double double9 = matrix3dc.m02() * this.m20 + matrix3dc.m12() * this.m21 + matrix3dc.m22() * this.m22;
		matrix3d.m00 = double1;
		matrix3d.m01 = double2;
		matrix3d.m02 = double3;
		matrix3d.m10 = double4;
		matrix3d.m11 = double5;
		matrix3d.m12 = double6;
		matrix3d.m20 = double7;
		matrix3d.m21 = double8;
		matrix3d.m22 = double9;
		return matrix3d;
	}

	public Matrix3d mul(Matrix3fc matrix3fc) {
		return this.mul(matrix3fc, this);
	}

	public Matrix3d mul(Matrix3fc matrix3fc, Matrix3d matrix3d) {
		double double1 = Math.fma(this.m00, (double)matrix3fc.m00(), Math.fma(this.m10, (double)matrix3fc.m01(), this.m20 * (double)matrix3fc.m02()));
		double double2 = Math.fma(this.m01, (double)matrix3fc.m00(), Math.fma(this.m11, (double)matrix3fc.m01(), this.m21 * (double)matrix3fc.m02()));
		double double3 = Math.fma(this.m02, (double)matrix3fc.m00(), Math.fma(this.m12, (double)matrix3fc.m01(), this.m22 * (double)matrix3fc.m02()));
		double double4 = Math.fma(this.m00, (double)matrix3fc.m10(), Math.fma(this.m10, (double)matrix3fc.m11(), this.m20 * (double)matrix3fc.m12()));
		double double5 = Math.fma(this.m01, (double)matrix3fc.m10(), Math.fma(this.m11, (double)matrix3fc.m11(), this.m21 * (double)matrix3fc.m12()));
		double double6 = Math.fma(this.m02, (double)matrix3fc.m10(), Math.fma(this.m12, (double)matrix3fc.m11(), this.m22 * (double)matrix3fc.m12()));
		double double7 = Math.fma(this.m00, (double)matrix3fc.m20(), Math.fma(this.m10, (double)matrix3fc.m21(), this.m20 * (double)matrix3fc.m22()));
		double double8 = Math.fma(this.m01, (double)matrix3fc.m20(), Math.fma(this.m11, (double)matrix3fc.m21(), this.m21 * (double)matrix3fc.m22()));
		double double9 = Math.fma(this.m02, (double)matrix3fc.m20(), Math.fma(this.m12, (double)matrix3fc.m21(), this.m22 * (double)matrix3fc.m22()));
		matrix3d.m00 = double1;
		matrix3d.m01 = double2;
		matrix3d.m02 = double3;
		matrix3d.m10 = double4;
		matrix3d.m11 = double5;
		matrix3d.m12 = double6;
		matrix3d.m20 = double7;
		matrix3d.m21 = double8;
		matrix3d.m22 = double9;
		return matrix3d;
	}

	public Matrix3d set(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		this.m00 = double1;
		this.m01 = double2;
		this.m02 = double3;
		this.m10 = double4;
		this.m11 = double5;
		this.m12 = double6;
		this.m20 = double7;
		this.m21 = double8;
		this.m22 = double9;
		return this;
	}

	public Matrix3d set(double[] doubleArray) {
		this.m00 = doubleArray[0];
		this.m01 = doubleArray[1];
		this.m02 = doubleArray[2];
		this.m10 = doubleArray[3];
		this.m11 = doubleArray[4];
		this.m12 = doubleArray[5];
		this.m20 = doubleArray[6];
		this.m21 = doubleArray[7];
		this.m22 = doubleArray[8];
		return this;
	}

	public Matrix3d set(float[] floatArray) {
		this.m00 = (double)floatArray[0];
		this.m01 = (double)floatArray[1];
		this.m02 = (double)floatArray[2];
		this.m10 = (double)floatArray[3];
		this.m11 = (double)floatArray[4];
		this.m12 = (double)floatArray[5];
		this.m20 = (double)floatArray[6];
		this.m21 = (double)floatArray[7];
		this.m22 = (double)floatArray[8];
		return this;
	}

	public double determinant() {
		return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
	}

	public Matrix3d invert() {
		return this.invert(this);
	}

	public Matrix3d invert(Matrix3d matrix3d) {
		double double1 = Math.fma(this.m00, this.m11, -this.m01 * this.m10);
		double double2 = Math.fma(this.m02, this.m10, -this.m00 * this.m12);
		double double3 = Math.fma(this.m01, this.m12, -this.m02 * this.m11);
		double double4 = Math.fma(double1, this.m22, Math.fma(double2, this.m21, double3 * this.m20));
		double double5 = 1.0 / double4;
		double double6 = Math.fma(this.m11, this.m22, -this.m21 * this.m12) * double5;
		double double7 = Math.fma(this.m21, this.m02, -this.m01 * this.m22) * double5;
		double double8 = double3 * double5;
		double double9 = Math.fma(this.m20, this.m12, -this.m10 * this.m22) * double5;
		double double10 = Math.fma(this.m00, this.m22, -this.m20 * this.m02) * double5;
		double double11 = double2 * double5;
		double double12 = Math.fma(this.m10, this.m21, -this.m20 * this.m11) * double5;
		double double13 = Math.fma(this.m20, this.m01, -this.m00 * this.m21) * double5;
		double double14 = double1 * double5;
		matrix3d.m00 = double6;
		matrix3d.m01 = double7;
		matrix3d.m02 = double8;
		matrix3d.m10 = double9;
		matrix3d.m11 = double10;
		matrix3d.m12 = double11;
		matrix3d.m20 = double12;
		matrix3d.m21 = double13;
		matrix3d.m22 = double14;
		return matrix3d;
	}

	public Matrix3d transpose() {
		return this.transpose(this);
	}

	public Matrix3d transpose(Matrix3d matrix3d) {
		matrix3d.set(this.m00, this.m10, this.m20, this.m01, this.m11, this.m21, this.m02, this.m12, this.m22);
		return matrix3d;
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
		String string = Runtime.format(this.m00, numberFormat);
		return string + " " + Runtime.format(this.m10, numberFormat) + " " + Runtime.format(this.m20, numberFormat) + "\n" + Runtime.format(this.m01, numberFormat) + " " + Runtime.format(this.m11, numberFormat) + " " + Runtime.format(this.m21, numberFormat) + "\n" + Runtime.format(this.m02, numberFormat) + " " + Runtime.format(this.m12, numberFormat) + " " + Runtime.format(this.m22, numberFormat) + "\n";
	}

	public Matrix3d get(Matrix3d matrix3d) {
		return matrix3d.set((Matrix3dc)this);
	}

	public AxisAngle4f getRotation(AxisAngle4f axisAngle4f) {
		return axisAngle4f.set((Matrix3dc)this);
	}

	public Quaternionf getUnnormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromUnnormalized((Matrix3dc)this);
	}

	public Quaternionf getNormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromNormalized((Matrix3dc)this);
	}

	public Quaterniond getUnnormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromUnnormalized((Matrix3dc)this);
	}

	public Quaterniond getNormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromNormalized((Matrix3dc)this);
	}

	public DoubleBuffer get(DoubleBuffer doubleBuffer) {
		return this.get(doubleBuffer.position(), doubleBuffer);
	}

	public DoubleBuffer get(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public FloatBuffer get(FloatBuffer floatBuffer) {
		return this.get(floatBuffer.position(), floatBuffer);
	}

	public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.putf(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		return this.get(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer getFloats(ByteBuffer byteBuffer) {
		return this.getFloats(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer getFloats(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putf(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Matrix3dc getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public double[] get(double[] doubleArray, int int1) {
		doubleArray[int1 + 0] = this.m00;
		doubleArray[int1 + 1] = this.m01;
		doubleArray[int1 + 2] = this.m02;
		doubleArray[int1 + 3] = this.m10;
		doubleArray[int1 + 4] = this.m11;
		doubleArray[int1 + 5] = this.m12;
		doubleArray[int1 + 6] = this.m20;
		doubleArray[int1 + 7] = this.m21;
		doubleArray[int1 + 8] = this.m22;
		return doubleArray;
	}

	public double[] get(double[] doubleArray) {
		return this.get((double[])doubleArray, 0);
	}

	public float[] get(float[] floatArray, int int1) {
		floatArray[int1 + 0] = (float)this.m00;
		floatArray[int1 + 1] = (float)this.m01;
		floatArray[int1 + 2] = (float)this.m02;
		floatArray[int1 + 3] = (float)this.m10;
		floatArray[int1 + 4] = (float)this.m11;
		floatArray[int1 + 5] = (float)this.m12;
		floatArray[int1 + 6] = (float)this.m20;
		floatArray[int1 + 7] = (float)this.m21;
		floatArray[int1 + 8] = (float)this.m22;
		return floatArray;
	}

	public float[] get(float[] floatArray) {
		return this.get((float[])floatArray, 0);
	}

	public Matrix3d set(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
		return this;
	}

	public Matrix3d set(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.getf(this, floatBuffer.position(), floatBuffer);
		return this;
	}

	public Matrix3d set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Matrix3d setFloats(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.getf(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Matrix3d setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
	}

	public Matrix3d set(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		this.m00 = vector3dc.x();
		this.m01 = vector3dc.y();
		this.m02 = vector3dc.z();
		this.m10 = vector3dc2.x();
		this.m11 = vector3dc2.y();
		this.m12 = vector3dc2.z();
		this.m20 = vector3dc3.x();
		this.m21 = vector3dc3.y();
		this.m22 = vector3dc3.z();
		return this;
	}

	public Matrix3d zero() {
		this.m00 = 0.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 0.0;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 0.0;
		return this;
	}

	public Matrix3d identity() {
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 1.0;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
		return this;
	}

	public Matrix3d scaling(double double1) {
		this.m00 = double1;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = double1;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = double1;
		return this;
	}

	public Matrix3d scaling(double double1, double double2, double double3) {
		this.m00 = double1;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = double2;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = double3;
		return this;
	}

	public Matrix3d scaling(Vector3dc vector3dc) {
		this.m00 = vector3dc.x();
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = vector3dc.y();
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = vector3dc.z();
		return this;
	}

	public Matrix3d scale(Vector3dc vector3dc, Matrix3d matrix3d) {
		return this.scale(vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix3d);
	}

	public Matrix3d scale(Vector3dc vector3dc) {
		return this.scale(vector3dc.x(), vector3dc.y(), vector3dc.z(), this);
	}

	public Matrix3d scale(double double1, double double2, double double3, Matrix3d matrix3d) {
		matrix3d.m00 = this.m00 * double1;
		matrix3d.m01 = this.m01 * double1;
		matrix3d.m02 = this.m02 * double1;
		matrix3d.m10 = this.m10 * double2;
		matrix3d.m11 = this.m11 * double2;
		matrix3d.m12 = this.m12 * double2;
		matrix3d.m20 = this.m20 * double3;
		matrix3d.m21 = this.m21 * double3;
		matrix3d.m22 = this.m22 * double3;
		return matrix3d;
	}

	public Matrix3d scale(double double1, double double2, double double3) {
		return this.scale(double1, double2, double3, this);
	}

	public Matrix3d scale(double double1, Matrix3d matrix3d) {
		return this.scale(double1, double1, double1, matrix3d);
	}

	public Matrix3d scale(double double1) {
		return this.scale(double1, double1, double1);
	}

	public Matrix3d scaleLocal(double double1, double double2, double double3, Matrix3d matrix3d) {
		double double4 = double1 * this.m00;
		double double5 = double2 * this.m01;
		double double6 = double3 * this.m02;
		double double7 = double1 * this.m10;
		double double8 = double2 * this.m11;
		double double9 = double3 * this.m12;
		double double10 = double1 * this.m20;
		double double11 = double2 * this.m21;
		double double12 = double3 * this.m22;
		matrix3d.m00 = double4;
		matrix3d.m01 = double5;
		matrix3d.m02 = double6;
		matrix3d.m10 = double7;
		matrix3d.m11 = double8;
		matrix3d.m12 = double9;
		matrix3d.m20 = double10;
		matrix3d.m21 = double11;
		matrix3d.m22 = double12;
		return matrix3d;
	}

	public Matrix3d scaleLocal(double double1, double double2, double double3) {
		return this.scaleLocal(double1, double2, double3, this);
	}

	public Matrix3d rotation(double double1, Vector3dc vector3dc) {
		return this.rotation(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix3d rotation(double double1, Vector3fc vector3fc) {
		return this.rotation(double1, (double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Matrix3d rotation(AxisAngle4f axisAngle4f) {
		return this.rotation((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z);
	}

	public Matrix3d rotation(AxisAngle4d axisAngle4d) {
		return this.rotation(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z);
	}

	public Matrix3d rotation(double double1, double double2, double double3, double double4) {
		double double5 = Math.sin(double1);
		double double6 = Math.cosFromSin(double5, double1);
		double double7 = 1.0 - double6;
		double double8 = double2 * double3;
		double double9 = double2 * double4;
		double double10 = double3 * double4;
		this.m00 = double6 + double2 * double2 * double7;
		this.m10 = double8 * double7 - double4 * double5;
		this.m20 = double9 * double7 + double3 * double5;
		this.m01 = double8 * double7 + double4 * double5;
		this.m11 = double6 + double3 * double3 * double7;
		this.m21 = double10 * double7 - double2 * double5;
		this.m02 = double9 * double7 - double3 * double5;
		this.m12 = double10 * double7 + double2 * double5;
		this.m22 = double6 + double4 * double4 * double7;
		return this;
	}

	public Matrix3d rotationX(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = double3;
		this.m12 = double2;
		this.m20 = 0.0;
		this.m21 = -double2;
		this.m22 = double3;
		return this;
	}

	public Matrix3d rotationY(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		this.m00 = double3;
		this.m01 = 0.0;
		this.m02 = -double2;
		this.m10 = 0.0;
		this.m11 = 1.0;
		this.m12 = 0.0;
		this.m20 = double2;
		this.m21 = 0.0;
		this.m22 = double3;
		return this;
	}

	public Matrix3d rotationZ(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		this.m00 = double3;
		this.m01 = double2;
		this.m02 = 0.0;
		this.m10 = -double2;
		this.m11 = double3;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
		return this;
	}

	public Matrix3d rotationXYZ(double double1, double double2, double double3) {
		double double4 = Math.sin(double1);
		double double5 = Math.cosFromSin(double4, double1);
		double double6 = Math.sin(double2);
		double double7 = Math.cosFromSin(double6, double2);
		double double8 = Math.sin(double3);
		double double9 = Math.cosFromSin(double8, double3);
		double double10 = -double4;
		double double11 = -double6;
		double double12 = -double8;
		double double13 = double10 * double11;
		double double14 = double5 * double11;
		this.m20 = double6;
		this.m21 = double10 * double7;
		this.m22 = double5 * double7;
		this.m00 = double7 * double9;
		this.m01 = double13 * double9 + double5 * double8;
		this.m02 = double14 * double9 + double4 * double8;
		this.m10 = double7 * double12;
		this.m11 = double13 * double12 + double5 * double9;
		this.m12 = double14 * double12 + double4 * double9;
		return this;
	}

	public Matrix3d rotationZYX(double double1, double double2, double double3) {
		double double4 = Math.sin(double3);
		double double5 = Math.cosFromSin(double4, double3);
		double double6 = Math.sin(double2);
		double double7 = Math.cosFromSin(double6, double2);
		double double8 = Math.sin(double1);
		double double9 = Math.cosFromSin(double8, double1);
		double double10 = -double8;
		double double11 = -double6;
		double double12 = -double4;
		double double13 = double9 * double6;
		double double14 = double8 * double6;
		this.m00 = double9 * double7;
		this.m01 = double8 * double7;
		this.m02 = double11;
		this.m10 = double10 * double5 + double13 * double4;
		this.m11 = double9 * double5 + double14 * double4;
		this.m12 = double7 * double4;
		this.m20 = double10 * double12 + double13 * double5;
		this.m21 = double9 * double12 + double14 * double5;
		this.m22 = double7 * double5;
		return this;
	}

	public Matrix3d rotationYXZ(double double1, double double2, double double3) {
		double double4 = Math.sin(double2);
		double double5 = Math.cosFromSin(double4, double2);
		double double6 = Math.sin(double1);
		double double7 = Math.cosFromSin(double6, double1);
		double double8 = Math.sin(double3);
		double double9 = Math.cosFromSin(double8, double3);
		double double10 = -double6;
		double double11 = -double4;
		double double12 = -double8;
		double double13 = double6 * double4;
		double double14 = double7 * double4;
		this.m20 = double6 * double5;
		this.m21 = double11;
		this.m22 = double7 * double5;
		this.m00 = double7 * double9 + double13 * double8;
		this.m01 = double5 * double8;
		this.m02 = double10 * double9 + double14 * double8;
		this.m10 = double7 * double12 + double13 * double9;
		this.m11 = double5 * double9;
		this.m12 = double10 * double12 + double14 * double9;
		return this;
	}

	public Matrix3d rotation(Quaterniondc quaterniondc) {
		double double1 = quaterniondc.w() * quaterniondc.w();
		double double2 = quaterniondc.x() * quaterniondc.x();
		double double3 = quaterniondc.y() * quaterniondc.y();
		double double4 = quaterniondc.z() * quaterniondc.z();
		double double5 = quaterniondc.z() * quaterniondc.w();
		double double6 = double5 + double5;
		double double7 = quaterniondc.x() * quaterniondc.y();
		double double8 = double7 + double7;
		double double9 = quaterniondc.x() * quaterniondc.z();
		double double10 = double9 + double9;
		double double11 = quaterniondc.y() * quaterniondc.w();
		double double12 = double11 + double11;
		double double13 = quaterniondc.y() * quaterniondc.z();
		double double14 = double13 + double13;
		double double15 = quaterniondc.x() * quaterniondc.w();
		double double16 = double15 + double15;
		this.m00 = double1 + double2 - double4 - double3;
		this.m01 = double8 + double6;
		this.m02 = double10 - double12;
		this.m10 = -double6 + double8;
		this.m11 = double3 - double4 + double1 - double2;
		this.m12 = double14 + double16;
		this.m20 = double12 + double10;
		this.m21 = double14 - double16;
		this.m22 = double4 - double3 - double2 + double1;
		return this;
	}

	public Matrix3d rotation(Quaternionfc quaternionfc) {
		double double1 = (double)(quaternionfc.w() * quaternionfc.w());
		double double2 = (double)(quaternionfc.x() * quaternionfc.x());
		double double3 = (double)(quaternionfc.y() * quaternionfc.y());
		double double4 = (double)(quaternionfc.z() * quaternionfc.z());
		double double5 = (double)(quaternionfc.z() * quaternionfc.w());
		double double6 = double5 + double5;
		double double7 = (double)(quaternionfc.x() * quaternionfc.y());
		double double8 = double7 + double7;
		double double9 = (double)(quaternionfc.x() * quaternionfc.z());
		double double10 = double9 + double9;
		double double11 = (double)(quaternionfc.y() * quaternionfc.w());
		double double12 = double11 + double11;
		double double13 = (double)(quaternionfc.y() * quaternionfc.z());
		double double14 = double13 + double13;
		double double15 = (double)(quaternionfc.x() * quaternionfc.w());
		double double16 = double15 + double15;
		this.m00 = double1 + double2 - double4 - double3;
		this.m01 = double8 + double6;
		this.m02 = double10 - double12;
		this.m10 = -double6 + double8;
		this.m11 = double3 - double4 + double1 - double2;
		this.m12 = double14 + double16;
		this.m20 = double12 + double10;
		this.m21 = double14 - double16;
		this.m22 = double4 - double3 - double2 + double1;
		return this;
	}

	public Vector3d transform(Vector3d vector3d) {
		return vector3d.mul((Matrix3dc)this);
	}

	public Vector3d transform(Vector3dc vector3dc, Vector3d vector3d) {
		vector3dc.mul((Matrix3dc)this, (Vector3d)vector3d);
		return vector3d;
	}

	public Vector3f transform(Vector3f vector3f) {
		return vector3f.mul((Matrix3dc)this);
	}

	public Vector3f transform(Vector3fc vector3fc, Vector3f vector3f) {
		return vector3fc.mul((Matrix3dc)this, vector3f);
	}

	public Vector3d transform(double double1, double double2, double double3, Vector3d vector3d) {
		return vector3d.set(Math.fma(this.m00, double1, Math.fma(this.m10, double2, this.m20 * double3)), Math.fma(this.m01, double1, Math.fma(this.m11, double2, this.m21 * double3)), Math.fma(this.m02, double1, Math.fma(this.m12, double2, this.m22 * double3)));
	}

	public Vector3d transformTranspose(Vector3d vector3d) {
		return vector3d.mulTranspose((Matrix3dc)this);
	}

	public Vector3d transformTranspose(Vector3dc vector3dc, Vector3d vector3d) {
		return vector3dc.mulTranspose((Matrix3dc)this, vector3d);
	}

	public Vector3d transformTranspose(double double1, double double2, double double3, Vector3d vector3d) {
		return vector3d.set(Math.fma(this.m00, double1, Math.fma(this.m01, double2, this.m02 * double3)), Math.fma(this.m10, double1, Math.fma(this.m11, double2, this.m12 * double3)), Math.fma(this.m20, double1, Math.fma(this.m21, double2, this.m22 * double3)));
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeDouble(this.m00);
		objectOutput.writeDouble(this.m01);
		objectOutput.writeDouble(this.m02);
		objectOutput.writeDouble(this.m10);
		objectOutput.writeDouble(this.m11);
		objectOutput.writeDouble(this.m12);
		objectOutput.writeDouble(this.m20);
		objectOutput.writeDouble(this.m21);
		objectOutput.writeDouble(this.m22);
	}

	public void readExternal(ObjectInput objectInput) throws IOException {
		this.m00 = objectInput.readDouble();
		this.m01 = objectInput.readDouble();
		this.m02 = objectInput.readDouble();
		this.m10 = objectInput.readDouble();
		this.m11 = objectInput.readDouble();
		this.m12 = objectInput.readDouble();
		this.m20 = objectInput.readDouble();
		this.m21 = objectInput.readDouble();
		this.m22 = objectInput.readDouble();
	}

	public Matrix3d rotateX(double double1, Matrix3d matrix3d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = -double2;
		double double5 = this.m10 * double3 + this.m20 * double2;
		double double6 = this.m11 * double3 + this.m21 * double2;
		double double7 = this.m12 * double3 + this.m22 * double2;
		matrix3d.m20 = this.m10 * double4 + this.m20 * double3;
		matrix3d.m21 = this.m11 * double4 + this.m21 * double3;
		matrix3d.m22 = this.m12 * double4 + this.m22 * double3;
		matrix3d.m10 = double5;
		matrix3d.m11 = double6;
		matrix3d.m12 = double7;
		matrix3d.m00 = this.m00;
		matrix3d.m01 = this.m01;
		matrix3d.m02 = this.m02;
		return matrix3d;
	}

	public Matrix3d rotateX(double double1) {
		return this.rotateX(double1, this);
	}

	public Matrix3d rotateY(double double1, Matrix3d matrix3d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = -double2;
		double double5 = this.m00 * double3 + this.m20 * double4;
		double double6 = this.m01 * double3 + this.m21 * double4;
		double double7 = this.m02 * double3 + this.m22 * double4;
		matrix3d.m20 = this.m00 * double2 + this.m20 * double3;
		matrix3d.m21 = this.m01 * double2 + this.m21 * double3;
		matrix3d.m22 = this.m02 * double2 + this.m22 * double3;
		matrix3d.m00 = double5;
		matrix3d.m01 = double6;
		matrix3d.m02 = double7;
		matrix3d.m10 = this.m10;
		matrix3d.m11 = this.m11;
		matrix3d.m12 = this.m12;
		return matrix3d;
	}

	public Matrix3d rotateY(double double1) {
		return this.rotateY(double1, this);
	}

	public Matrix3d rotateZ(double double1, Matrix3d matrix3d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = -double2;
		double double5 = this.m00 * double3 + this.m10 * double2;
		double double6 = this.m01 * double3 + this.m11 * double2;
		double double7 = this.m02 * double3 + this.m12 * double2;
		matrix3d.m10 = this.m00 * double4 + this.m10 * double3;
		matrix3d.m11 = this.m01 * double4 + this.m11 * double3;
		matrix3d.m12 = this.m02 * double4 + this.m12 * double3;
		matrix3d.m00 = double5;
		matrix3d.m01 = double6;
		matrix3d.m02 = double7;
		matrix3d.m20 = this.m20;
		matrix3d.m21 = this.m21;
		matrix3d.m22 = this.m22;
		return matrix3d;
	}

	public Matrix3d rotateZ(double double1) {
		return this.rotateZ(double1, this);
	}

	public Matrix3d rotateXYZ(double double1, double double2, double double3) {
		return this.rotateXYZ(double1, double2, double3, this);
	}

	public Matrix3d rotateXYZ(double double1, double double2, double double3, Matrix3d matrix3d) {
		double double4 = Math.sin(double1);
		double double5 = Math.cosFromSin(double4, double1);
		double double6 = Math.sin(double2);
		double double7 = Math.cosFromSin(double6, double2);
		double double8 = Math.sin(double3);
		double double9 = Math.cosFromSin(double8, double3);
		double double10 = -double4;
		double double11 = -double6;
		double double12 = -double8;
		double double13 = this.m10 * double5 + this.m20 * double4;
		double double14 = this.m11 * double5 + this.m21 * double4;
		double double15 = this.m12 * double5 + this.m22 * double4;
		double double16 = this.m10 * double10 + this.m20 * double5;
		double double17 = this.m11 * double10 + this.m21 * double5;
		double double18 = this.m12 * double10 + this.m22 * double5;
		double double19 = this.m00 * double7 + double16 * double11;
		double double20 = this.m01 * double7 + double17 * double11;
		double double21 = this.m02 * double7 + double18 * double11;
		matrix3d.m20 = this.m00 * double6 + double16 * double7;
		matrix3d.m21 = this.m01 * double6 + double17 * double7;
		matrix3d.m22 = this.m02 * double6 + double18 * double7;
		matrix3d.m00 = double19 * double9 + double13 * double8;
		matrix3d.m01 = double20 * double9 + double14 * double8;
		matrix3d.m02 = double21 * double9 + double15 * double8;
		matrix3d.m10 = double19 * double12 + double13 * double9;
		matrix3d.m11 = double20 * double12 + double14 * double9;
		matrix3d.m12 = double21 * double12 + double15 * double9;
		return matrix3d;
	}

	public Matrix3d rotateZYX(double double1, double double2, double double3) {
		return this.rotateZYX(double1, double2, double3, this);
	}

	public Matrix3d rotateZYX(double double1, double double2, double double3, Matrix3d matrix3d) {
		double double4 = Math.sin(double3);
		double double5 = Math.cosFromSin(double4, double3);
		double double6 = Math.sin(double2);
		double double7 = Math.cosFromSin(double6, double2);
		double double8 = Math.sin(double1);
		double double9 = Math.cosFromSin(double8, double1);
		double double10 = -double8;
		double double11 = -double6;
		double double12 = -double4;
		double double13 = this.m00 * double9 + this.m10 * double8;
		double double14 = this.m01 * double9 + this.m11 * double8;
		double double15 = this.m02 * double9 + this.m12 * double8;
		double double16 = this.m00 * double10 + this.m10 * double9;
		double double17 = this.m01 * double10 + this.m11 * double9;
		double double18 = this.m02 * double10 + this.m12 * double9;
		double double19 = double13 * double6 + this.m20 * double7;
		double double20 = double14 * double6 + this.m21 * double7;
		double double21 = double15 * double6 + this.m22 * double7;
		matrix3d.m00 = double13 * double7 + this.m20 * double11;
		matrix3d.m01 = double14 * double7 + this.m21 * double11;
		matrix3d.m02 = double15 * double7 + this.m22 * double11;
		matrix3d.m10 = double16 * double5 + double19 * double4;
		matrix3d.m11 = double17 * double5 + double20 * double4;
		matrix3d.m12 = double18 * double5 + double21 * double4;
		matrix3d.m20 = double16 * double12 + double19 * double5;
		matrix3d.m21 = double17 * double12 + double20 * double5;
		matrix3d.m22 = double18 * double12 + double21 * double5;
		return matrix3d;
	}

	public Matrix3d rotateYXZ(Vector3d vector3d) {
		return this.rotateYXZ(vector3d.y, vector3d.x, vector3d.z);
	}

	public Matrix3d rotateYXZ(double double1, double double2, double double3) {
		return this.rotateYXZ(double1, double2, double3, this);
	}

	public Matrix3d rotateYXZ(double double1, double double2, double double3, Matrix3d matrix3d) {
		double double4 = Math.sin(double2);
		double double5 = Math.cosFromSin(double4, double2);
		double double6 = Math.sin(double1);
		double double7 = Math.cosFromSin(double6, double1);
		double double8 = Math.sin(double3);
		double double9 = Math.cosFromSin(double8, double3);
		double double10 = -double6;
		double double11 = -double4;
		double double12 = -double8;
		double double13 = this.m00 * double6 + this.m20 * double7;
		double double14 = this.m01 * double6 + this.m21 * double7;
		double double15 = this.m02 * double6 + this.m22 * double7;
		double double16 = this.m00 * double7 + this.m20 * double10;
		double double17 = this.m01 * double7 + this.m21 * double10;
		double double18 = this.m02 * double7 + this.m22 * double10;
		double double19 = this.m10 * double5 + double13 * double4;
		double double20 = this.m11 * double5 + double14 * double4;
		double double21 = this.m12 * double5 + double15 * double4;
		matrix3d.m20 = this.m10 * double11 + double13 * double5;
		matrix3d.m21 = this.m11 * double11 + double14 * double5;
		matrix3d.m22 = this.m12 * double11 + double15 * double5;
		matrix3d.m00 = double16 * double9 + double19 * double8;
		matrix3d.m01 = double17 * double9 + double20 * double8;
		matrix3d.m02 = double18 * double9 + double21 * double8;
		matrix3d.m10 = double16 * double12 + double19 * double9;
		matrix3d.m11 = double17 * double12 + double20 * double9;
		matrix3d.m12 = double18 * double12 + double21 * double9;
		return matrix3d;
	}

	public Matrix3d rotate(double double1, double double2, double double3, double double4) {
		return this.rotate(double1, double2, double3, double4, this);
	}

	public Matrix3d rotate(double double1, double double2, double double3, double double4, Matrix3d matrix3d) {
		double double5 = Math.sin(double1);
		double double6 = Math.cosFromSin(double5, double1);
		double double7 = 1.0 - double6;
		double double8 = double2 * double2;
		double double9 = double2 * double3;
		double double10 = double2 * double4;
		double double11 = double3 * double3;
		double double12 = double3 * double4;
		double double13 = double4 * double4;
		double double14 = double8 * double7 + double6;
		double double15 = double9 * double7 + double4 * double5;
		double double16 = double10 * double7 - double3 * double5;
		double double17 = double9 * double7 - double4 * double5;
		double double18 = double11 * double7 + double6;
		double double19 = double12 * double7 + double2 * double5;
		double double20 = double10 * double7 + double3 * double5;
		double double21 = double12 * double7 - double2 * double5;
		double double22 = double13 * double7 + double6;
		double double23 = this.m00 * double14 + this.m10 * double15 + this.m20 * double16;
		double double24 = this.m01 * double14 + this.m11 * double15 + this.m21 * double16;
		double double25 = this.m02 * double14 + this.m12 * double15 + this.m22 * double16;
		double double26 = this.m00 * double17 + this.m10 * double18 + this.m20 * double19;
		double double27 = this.m01 * double17 + this.m11 * double18 + this.m21 * double19;
		double double28 = this.m02 * double17 + this.m12 * double18 + this.m22 * double19;
		matrix3d.m20 = this.m00 * double20 + this.m10 * double21 + this.m20 * double22;
		matrix3d.m21 = this.m01 * double20 + this.m11 * double21 + this.m21 * double22;
		matrix3d.m22 = this.m02 * double20 + this.m12 * double21 + this.m22 * double22;
		matrix3d.m00 = double23;
		matrix3d.m01 = double24;
		matrix3d.m02 = double25;
		matrix3d.m10 = double26;
		matrix3d.m11 = double27;
		matrix3d.m12 = double28;
		return matrix3d;
	}

	public Matrix3d rotateLocal(double double1, double double2, double double3, double double4, Matrix3d matrix3d) {
		double double5 = Math.sin(double1);
		double double6 = Math.cosFromSin(double5, double1);
		double double7 = 1.0 - double6;
		double double8 = double2 * double2;
		double double9 = double2 * double3;
		double double10 = double2 * double4;
		double double11 = double3 * double3;
		double double12 = double3 * double4;
		double double13 = double4 * double4;
		double double14 = double8 * double7 + double6;
		double double15 = double9 * double7 + double4 * double5;
		double double16 = double10 * double7 - double3 * double5;
		double double17 = double9 * double7 - double4 * double5;
		double double18 = double11 * double7 + double6;
		double double19 = double12 * double7 + double2 * double5;
		double double20 = double10 * double7 + double3 * double5;
		double double21 = double12 * double7 - double2 * double5;
		double double22 = double13 * double7 + double6;
		double double23 = double14 * this.m00 + double17 * this.m01 + double20 * this.m02;
		double double24 = double15 * this.m00 + double18 * this.m01 + double21 * this.m02;
		double double25 = double16 * this.m00 + double19 * this.m01 + double22 * this.m02;
		double double26 = double14 * this.m10 + double17 * this.m11 + double20 * this.m12;
		double double27 = double15 * this.m10 + double18 * this.m11 + double21 * this.m12;
		double double28 = double16 * this.m10 + double19 * this.m11 + double22 * this.m12;
		double double29 = double14 * this.m20 + double17 * this.m21 + double20 * this.m22;
		double double30 = double15 * this.m20 + double18 * this.m21 + double21 * this.m22;
		double double31 = double16 * this.m20 + double19 * this.m21 + double22 * this.m22;
		matrix3d.m00 = double23;
		matrix3d.m01 = double24;
		matrix3d.m02 = double25;
		matrix3d.m10 = double26;
		matrix3d.m11 = double27;
		matrix3d.m12 = double28;
		matrix3d.m20 = double29;
		matrix3d.m21 = double30;
		matrix3d.m22 = double31;
		return matrix3d;
	}

	public Matrix3d rotateLocal(double double1, double double2, double double3, double double4) {
		return this.rotateLocal(double1, double2, double3, double4, this);
	}

	public Matrix3d rotateLocalX(double double1, Matrix3d matrix3d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = double3 * this.m01 - double2 * this.m02;
		double double5 = double2 * this.m01 + double3 * this.m02;
		double double6 = double3 * this.m11 - double2 * this.m12;
		double double7 = double2 * this.m11 + double3 * this.m12;
		double double8 = double3 * this.m21 - double2 * this.m22;
		double double9 = double2 * this.m21 + double3 * this.m22;
		matrix3d.m00 = this.m00;
		matrix3d.m01 = double4;
		matrix3d.m02 = double5;
		matrix3d.m10 = this.m10;
		matrix3d.m11 = double6;
		matrix3d.m12 = double7;
		matrix3d.m20 = this.m20;
		matrix3d.m21 = double8;
		matrix3d.m22 = double9;
		return matrix3d;
	}

	public Matrix3d rotateLocalX(double double1) {
		return this.rotateLocalX(double1, this);
	}

	public Matrix3d rotateLocalY(double double1, Matrix3d matrix3d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = double3 * this.m00 + double2 * this.m02;
		double double5 = -double2 * this.m00 + double3 * this.m02;
		double double6 = double3 * this.m10 + double2 * this.m12;
		double double7 = -double2 * this.m10 + double3 * this.m12;
		double double8 = double3 * this.m20 + double2 * this.m22;
		double double9 = -double2 * this.m20 + double3 * this.m22;
		matrix3d.m00 = double4;
		matrix3d.m01 = this.m01;
		matrix3d.m02 = double5;
		matrix3d.m10 = double6;
		matrix3d.m11 = this.m11;
		matrix3d.m12 = double7;
		matrix3d.m20 = double8;
		matrix3d.m21 = this.m21;
		matrix3d.m22 = double9;
		return matrix3d;
	}

	public Matrix3d rotateLocalY(double double1) {
		return this.rotateLocalY(double1, this);
	}

	public Matrix3d rotateLocalZ(double double1, Matrix3d matrix3d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = double3 * this.m00 - double2 * this.m01;
		double double5 = double2 * this.m00 + double3 * this.m01;
		double double6 = double3 * this.m10 - double2 * this.m11;
		double double7 = double2 * this.m10 + double3 * this.m11;
		double double8 = double3 * this.m20 - double2 * this.m21;
		double double9 = double2 * this.m20 + double3 * this.m21;
		matrix3d.m00 = double4;
		matrix3d.m01 = double5;
		matrix3d.m02 = this.m02;
		matrix3d.m10 = double6;
		matrix3d.m11 = double7;
		matrix3d.m12 = this.m12;
		matrix3d.m20 = double8;
		matrix3d.m21 = double9;
		matrix3d.m22 = this.m22;
		return matrix3d;
	}

	public Matrix3d rotateLocalZ(double double1) {
		return this.rotateLocalZ(double1, this);
	}

	public Matrix3d rotateLocal(Quaterniondc quaterniondc, Matrix3d matrix3d) {
		double double1 = quaterniondc.w() * quaterniondc.w();
		double double2 = quaterniondc.x() * quaterniondc.x();
		double double3 = quaterniondc.y() * quaterniondc.y();
		double double4 = quaterniondc.z() * quaterniondc.z();
		double double5 = quaterniondc.z() * quaterniondc.w();
		double double6 = double5 + double5;
		double double7 = quaterniondc.x() * quaterniondc.y();
		double double8 = double7 + double7;
		double double9 = quaterniondc.x() * quaterniondc.z();
		double double10 = double9 + double9;
		double double11 = quaterniondc.y() * quaterniondc.w();
		double double12 = double11 + double11;
		double double13 = quaterniondc.y() * quaterniondc.z();
		double double14 = double13 + double13;
		double double15 = quaterniondc.x() * quaterniondc.w();
		double double16 = double15 + double15;
		double double17 = double1 + double2 - double4 - double3;
		double double18 = double8 + double6;
		double double19 = double10 - double12;
		double double20 = double8 - double6;
		double double21 = double3 - double4 + double1 - double2;
		double double22 = double14 + double16;
		double double23 = double12 + double10;
		double double24 = double14 - double16;
		double double25 = double4 - double3 - double2 + double1;
		double double26 = double17 * this.m00 + double20 * this.m01 + double23 * this.m02;
		double double27 = double18 * this.m00 + double21 * this.m01 + double24 * this.m02;
		double double28 = double19 * this.m00 + double22 * this.m01 + double25 * this.m02;
		double double29 = double17 * this.m10 + double20 * this.m11 + double23 * this.m12;
		double double30 = double18 * this.m10 + double21 * this.m11 + double24 * this.m12;
		double double31 = double19 * this.m10 + double22 * this.m11 + double25 * this.m12;
		double double32 = double17 * this.m20 + double20 * this.m21 + double23 * this.m22;
		double double33 = double18 * this.m20 + double21 * this.m21 + double24 * this.m22;
		double double34 = double19 * this.m20 + double22 * this.m21 + double25 * this.m22;
		matrix3d.m00 = double26;
		matrix3d.m01 = double27;
		matrix3d.m02 = double28;
		matrix3d.m10 = double29;
		matrix3d.m11 = double30;
		matrix3d.m12 = double31;
		matrix3d.m20 = double32;
		matrix3d.m21 = double33;
		matrix3d.m22 = double34;
		return matrix3d;
	}

	public Matrix3d rotateLocal(Quaterniondc quaterniondc) {
		return this.rotateLocal(quaterniondc, this);
	}

	public Matrix3d rotateLocal(Quaternionfc quaternionfc, Matrix3d matrix3d) {
		double double1 = (double)(quaternionfc.w() * quaternionfc.w());
		double double2 = (double)(quaternionfc.x() * quaternionfc.x());
		double double3 = (double)(quaternionfc.y() * quaternionfc.y());
		double double4 = (double)(quaternionfc.z() * quaternionfc.z());
		double double5 = (double)(quaternionfc.z() * quaternionfc.w());
		double double6 = double5 + double5;
		double double7 = (double)(quaternionfc.x() * quaternionfc.y());
		double double8 = double7 + double7;
		double double9 = (double)(quaternionfc.x() * quaternionfc.z());
		double double10 = double9 + double9;
		double double11 = (double)(quaternionfc.y() * quaternionfc.w());
		double double12 = double11 + double11;
		double double13 = (double)(quaternionfc.y() * quaternionfc.z());
		double double14 = double13 + double13;
		double double15 = (double)(quaternionfc.x() * quaternionfc.w());
		double double16 = double15 + double15;
		double double17 = double1 + double2 - double4 - double3;
		double double18 = double8 + double6;
		double double19 = double10 - double12;
		double double20 = double8 - double6;
		double double21 = double3 - double4 + double1 - double2;
		double double22 = double14 + double16;
		double double23 = double12 + double10;
		double double24 = double14 - double16;
		double double25 = double4 - double3 - double2 + double1;
		double double26 = double17 * this.m00 + double20 * this.m01 + double23 * this.m02;
		double double27 = double18 * this.m00 + double21 * this.m01 + double24 * this.m02;
		double double28 = double19 * this.m00 + double22 * this.m01 + double25 * this.m02;
		double double29 = double17 * this.m10 + double20 * this.m11 + double23 * this.m12;
		double double30 = double18 * this.m10 + double21 * this.m11 + double24 * this.m12;
		double double31 = double19 * this.m10 + double22 * this.m11 + double25 * this.m12;
		double double32 = double17 * this.m20 + double20 * this.m21 + double23 * this.m22;
		double double33 = double18 * this.m20 + double21 * this.m21 + double24 * this.m22;
		double double34 = double19 * this.m20 + double22 * this.m21 + double25 * this.m22;
		matrix3d.m00 = double26;
		matrix3d.m01 = double27;
		matrix3d.m02 = double28;
		matrix3d.m10 = double29;
		matrix3d.m11 = double30;
		matrix3d.m12 = double31;
		matrix3d.m20 = double32;
		matrix3d.m21 = double33;
		matrix3d.m22 = double34;
		return matrix3d;
	}

	public Matrix3d rotateLocal(Quaternionfc quaternionfc) {
		return this.rotateLocal(quaternionfc, this);
	}

	public Matrix3d rotate(Quaterniondc quaterniondc) {
		return this.rotate(quaterniondc, this);
	}

	public Matrix3d rotate(Quaterniondc quaterniondc, Matrix3d matrix3d) {
		double double1 = quaterniondc.w() * quaterniondc.w();
		double double2 = quaterniondc.x() * quaterniondc.x();
		double double3 = quaterniondc.y() * quaterniondc.y();
		double double4 = quaterniondc.z() * quaterniondc.z();
		double double5 = quaterniondc.z() * quaterniondc.w();
		double double6 = double5 + double5;
		double double7 = quaterniondc.x() * quaterniondc.y();
		double double8 = double7 + double7;
		double double9 = quaterniondc.x() * quaterniondc.z();
		double double10 = double9 + double9;
		double double11 = quaterniondc.y() * quaterniondc.w();
		double double12 = double11 + double11;
		double double13 = quaterniondc.y() * quaterniondc.z();
		double double14 = double13 + double13;
		double double15 = quaterniondc.x() * quaterniondc.w();
		double double16 = double15 + double15;
		double double17 = double1 + double2 - double4 - double3;
		double double18 = double8 + double6;
		double double19 = double10 - double12;
		double double20 = double8 - double6;
		double double21 = double3 - double4 + double1 - double2;
		double double22 = double14 + double16;
		double double23 = double12 + double10;
		double double24 = double14 - double16;
		double double25 = double4 - double3 - double2 + double1;
		double double26 = this.m00 * double17 + this.m10 * double18 + this.m20 * double19;
		double double27 = this.m01 * double17 + this.m11 * double18 + this.m21 * double19;
		double double28 = this.m02 * double17 + this.m12 * double18 + this.m22 * double19;
		double double29 = this.m00 * double20 + this.m10 * double21 + this.m20 * double22;
		double double30 = this.m01 * double20 + this.m11 * double21 + this.m21 * double22;
		double double31 = this.m02 * double20 + this.m12 * double21 + this.m22 * double22;
		matrix3d.m20 = this.m00 * double23 + this.m10 * double24 + this.m20 * double25;
		matrix3d.m21 = this.m01 * double23 + this.m11 * double24 + this.m21 * double25;
		matrix3d.m22 = this.m02 * double23 + this.m12 * double24 + this.m22 * double25;
		matrix3d.m00 = double26;
		matrix3d.m01 = double27;
		matrix3d.m02 = double28;
		matrix3d.m10 = double29;
		matrix3d.m11 = double30;
		matrix3d.m12 = double31;
		return matrix3d;
	}

	public Matrix3d rotate(Quaternionfc quaternionfc) {
		return this.rotate(quaternionfc, this);
	}

	public Matrix3d rotate(Quaternionfc quaternionfc, Matrix3d matrix3d) {
		double double1 = (double)(quaternionfc.w() * quaternionfc.w());
		double double2 = (double)(quaternionfc.x() * quaternionfc.x());
		double double3 = (double)(quaternionfc.y() * quaternionfc.y());
		double double4 = (double)(quaternionfc.z() * quaternionfc.z());
		double double5 = (double)(quaternionfc.z() * quaternionfc.w());
		double double6 = double5 + double5;
		double double7 = (double)(quaternionfc.x() * quaternionfc.y());
		double double8 = double7 + double7;
		double double9 = (double)(quaternionfc.x() * quaternionfc.z());
		double double10 = double9 + double9;
		double double11 = (double)(quaternionfc.y() * quaternionfc.w());
		double double12 = double11 + double11;
		double double13 = (double)(quaternionfc.y() * quaternionfc.z());
		double double14 = double13 + double13;
		double double15 = (double)(quaternionfc.x() * quaternionfc.w());
		double double16 = double15 + double15;
		double double17 = double1 + double2 - double4 - double3;
		double double18 = double8 + double6;
		double double19 = double10 - double12;
		double double20 = double8 - double6;
		double double21 = double3 - double4 + double1 - double2;
		double double22 = double14 + double16;
		double double23 = double12 + double10;
		double double24 = double14 - double16;
		double double25 = double4 - double3 - double2 + double1;
		double double26 = this.m00 * double17 + this.m10 * double18 + this.m20 * double19;
		double double27 = this.m01 * double17 + this.m11 * double18 + this.m21 * double19;
		double double28 = this.m02 * double17 + this.m12 * double18 + this.m22 * double19;
		double double29 = this.m00 * double20 + this.m10 * double21 + this.m20 * double22;
		double double30 = this.m01 * double20 + this.m11 * double21 + this.m21 * double22;
		double double31 = this.m02 * double20 + this.m12 * double21 + this.m22 * double22;
		matrix3d.m20 = this.m00 * double23 + this.m10 * double24 + this.m20 * double25;
		matrix3d.m21 = this.m01 * double23 + this.m11 * double24 + this.m21 * double25;
		matrix3d.m22 = this.m02 * double23 + this.m12 * double24 + this.m22 * double25;
		matrix3d.m00 = double26;
		matrix3d.m01 = double27;
		matrix3d.m02 = double28;
		matrix3d.m10 = double29;
		matrix3d.m11 = double30;
		matrix3d.m12 = double31;
		return matrix3d;
	}

	public Matrix3d rotate(AxisAngle4f axisAngle4f) {
		return this.rotate((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z);
	}

	public Matrix3d rotate(AxisAngle4f axisAngle4f, Matrix3d matrix3d) {
		return this.rotate((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z, matrix3d);
	}

	public Matrix3d rotate(AxisAngle4d axisAngle4d) {
		return this.rotate(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z);
	}

	public Matrix3d rotate(AxisAngle4d axisAngle4d, Matrix3d matrix3d) {
		return this.rotate(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z, matrix3d);
	}

	public Matrix3d rotate(double double1, Vector3dc vector3dc) {
		return this.rotate(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix3d rotate(double double1, Vector3dc vector3dc, Matrix3d matrix3d) {
		return this.rotate(double1, vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix3d);
	}

	public Matrix3d rotate(double double1, Vector3fc vector3fc) {
		return this.rotate(double1, (double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Matrix3d rotate(double double1, Vector3fc vector3fc, Matrix3d matrix3d) {
		return this.rotate(double1, (double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), matrix3d);
	}

	public Vector3d getRow(int int1, Vector3d vector3d) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			return vector3d.set(this.m00, this.m10, this.m20);
		
		case 1: 
			return vector3d.set(this.m01, this.m11, this.m21);
		
		case 2: 
			return vector3d.set(this.m02, this.m12, this.m22);
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
	}

	public Matrix3d setRow(int int1, Vector3dc vector3dc) throws IndexOutOfBoundsException {
		return this.setRow(int1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix3d setRow(int int1, double double1, double double2, double double3) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			this.m00 = double1;
			this.m10 = double2;
			this.m20 = double3;
			break;
		
		case 1: 
			this.m01 = double1;
			this.m11 = double2;
			this.m21 = double3;
			break;
		
		case 2: 
			this.m02 = double1;
			this.m12 = double2;
			this.m22 = double3;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return this;
	}

	public Vector3d getColumn(int int1, Vector3d vector3d) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			return vector3d.set(this.m00, this.m01, this.m02);
		
		case 1: 
			return vector3d.set(this.m10, this.m11, this.m12);
		
		case 2: 
			return vector3d.set(this.m20, this.m21, this.m22);
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
	}

	public Matrix3d setColumn(int int1, Vector3dc vector3dc) throws IndexOutOfBoundsException {
		return this.setColumn(int1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix3d setColumn(int int1, double double1, double double2, double double3) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			this.m00 = double1;
			this.m01 = double2;
			this.m02 = double3;
			break;
		
		case 1: 
			this.m10 = double1;
			this.m11 = double2;
			this.m12 = double3;
			break;
		
		case 2: 
			this.m20 = double1;
			this.m21 = double2;
			this.m22 = double3;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return this;
	}

	public double get(int int1, int int2) {
		return MemUtil.INSTANCE.get(this, int1, int2);
	}

	public Matrix3d set(int int1, int int2, double double1) {
		return MemUtil.INSTANCE.set(this, int1, int2, double1);
	}

	public double getRowColumn(int int1, int int2) {
		return MemUtil.INSTANCE.get(this, int2, int1);
	}

	public Matrix3d setRowColumn(int int1, int int2, double double1) {
		return MemUtil.INSTANCE.set(this, int2, int1, double1);
	}

	public Matrix3d normal() {
		return this.normal(this);
	}

	public Matrix3d normal(Matrix3d matrix3d) {
		double double1 = this.m00 * this.m11;
		double double2 = this.m01 * this.m10;
		double double3 = this.m02 * this.m10;
		double double4 = this.m00 * this.m12;
		double double5 = this.m01 * this.m12;
		double double6 = this.m02 * this.m11;
		double double7 = (double1 - double2) * this.m22 + (double3 - double4) * this.m21 + (double5 - double6) * this.m20;
		double double8 = 1.0 / double7;
		double double9 = (this.m11 * this.m22 - this.m21 * this.m12) * double8;
		double double10 = (this.m20 * this.m12 - this.m10 * this.m22) * double8;
		double double11 = (this.m10 * this.m21 - this.m20 * this.m11) * double8;
		double double12 = (this.m21 * this.m02 - this.m01 * this.m22) * double8;
		double double13 = (this.m00 * this.m22 - this.m20 * this.m02) * double8;
		double double14 = (this.m20 * this.m01 - this.m00 * this.m21) * double8;
		double double15 = (double5 - double6) * double8;
		double double16 = (double3 - double4) * double8;
		double double17 = (double1 - double2) * double8;
		matrix3d.m00 = double9;
		matrix3d.m01 = double10;
		matrix3d.m02 = double11;
		matrix3d.m10 = double12;
		matrix3d.m11 = double13;
		matrix3d.m12 = double14;
		matrix3d.m20 = double15;
		matrix3d.m21 = double16;
		matrix3d.m22 = double17;
		return matrix3d;
	}

	public Matrix3d cofactor() {
		return this.cofactor(this);
	}

	public Matrix3d cofactor(Matrix3d matrix3d) {
		double double1 = this.m11 * this.m22 - this.m21 * this.m12;
		double double2 = this.m20 * this.m12 - this.m10 * this.m22;
		double double3 = this.m10 * this.m21 - this.m20 * this.m11;
		double double4 = this.m21 * this.m02 - this.m01 * this.m22;
		double double5 = this.m00 * this.m22 - this.m20 * this.m02;
		double double6 = this.m20 * this.m01 - this.m00 * this.m21;
		double double7 = this.m01 * this.m12 - this.m11 * this.m02;
		double double8 = this.m02 * this.m10 - this.m12 * this.m00;
		double double9 = this.m00 * this.m11 - this.m10 * this.m01;
		matrix3d.m00 = double1;
		matrix3d.m01 = double2;
		matrix3d.m02 = double3;
		matrix3d.m10 = double4;
		matrix3d.m11 = double5;
		matrix3d.m12 = double6;
		matrix3d.m20 = double7;
		matrix3d.m21 = double8;
		matrix3d.m22 = double9;
		return matrix3d;
	}

	public Matrix3d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.lookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), this);
	}

	public Matrix3d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix3d matrix3d) {
		return this.lookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), matrix3d);
	}

	public Matrix3d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Matrix3d matrix3d) {
		double double7 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double1 *= -double7;
		double2 *= -double7;
		double3 *= -double7;
		double double8 = double5 * double3 - double6 * double2;
		double double9 = double6 * double1 - double4 * double3;
		double double10 = double4 * double2 - double5 * double1;
		double double11 = Math.invsqrt(double8 * double8 + double9 * double9 + double10 * double10);
		double8 *= double11;
		double9 *= double11;
		double10 *= double11;
		double double12 = double2 * double10 - double3 * double9;
		double double13 = double3 * double8 - double1 * double10;
		double double14 = double1 * double9 - double2 * double8;
		double double15 = this.m00 * double8 + this.m10 * double12 + this.m20 * double1;
		double double16 = this.m01 * double8 + this.m11 * double12 + this.m21 * double1;
		double double17 = this.m02 * double8 + this.m12 * double12 + this.m22 * double1;
		double double18 = this.m00 * double9 + this.m10 * double13 + this.m20 * double2;
		double double19 = this.m01 * double9 + this.m11 * double13 + this.m21 * double2;
		double double20 = this.m02 * double9 + this.m12 * double13 + this.m22 * double2;
		matrix3d.m20 = this.m00 * double10 + this.m10 * double14 + this.m20 * double3;
		matrix3d.m21 = this.m01 * double10 + this.m11 * double14 + this.m21 * double3;
		matrix3d.m22 = this.m02 * double10 + this.m12 * double14 + this.m22 * double3;
		matrix3d.m00 = double15;
		matrix3d.m01 = double16;
		matrix3d.m02 = double17;
		matrix3d.m10 = double18;
		matrix3d.m11 = double19;
		matrix3d.m12 = double20;
		return matrix3d;
	}

	public Matrix3d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.lookAlong(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix3d setLookAlong(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.setLookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix3d setLookAlong(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double1 *= -double7;
		double2 *= -double7;
		double3 *= -double7;
		double double8 = double5 * double3 - double6 * double2;
		double double9 = double6 * double1 - double4 * double3;
		double double10 = double4 * double2 - double5 * double1;
		double double11 = Math.invsqrt(double8 * double8 + double9 * double9 + double10 * double10);
		double8 *= double11;
		double9 *= double11;
		double10 *= double11;
		double double12 = double2 * double10 - double3 * double9;
		double double13 = double3 * double8 - double1 * double10;
		double double14 = double1 * double9 - double2 * double8;
		this.m00 = double8;
		this.m01 = double12;
		this.m02 = double1;
		this.m10 = double9;
		this.m11 = double13;
		this.m12 = double2;
		this.m20 = double10;
		this.m21 = double14;
		this.m22 = double3;
		return this;
	}

	public Vector3d getScale(Vector3d vector3d) {
		vector3d.x = Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
		vector3d.y = Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
		vector3d.z = Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
		return vector3d;
	}

	public Vector3d positiveZ(Vector3d vector3d) {
		vector3d.x = this.m10 * this.m21 - this.m11 * this.m20;
		vector3d.y = this.m20 * this.m01 - this.m21 * this.m00;
		vector3d.z = this.m00 * this.m11 - this.m01 * this.m10;
		return vector3d.normalize(vector3d);
	}

	public Vector3d normalizedPositiveZ(Vector3d vector3d) {
		vector3d.x = this.m02;
		vector3d.y = this.m12;
		vector3d.z = this.m22;
		return vector3d;
	}

	public Vector3d positiveX(Vector3d vector3d) {
		vector3d.x = this.m11 * this.m22 - this.m12 * this.m21;
		vector3d.y = this.m02 * this.m21 - this.m01 * this.m22;
		vector3d.z = this.m01 * this.m12 - this.m02 * this.m11;
		return vector3d.normalize(vector3d);
	}

	public Vector3d normalizedPositiveX(Vector3d vector3d) {
		vector3d.x = this.m00;
		vector3d.y = this.m10;
		vector3d.z = this.m20;
		return vector3d;
	}

	public Vector3d positiveY(Vector3d vector3d) {
		vector3d.x = this.m12 * this.m20 - this.m10 * this.m22;
		vector3d.y = this.m00 * this.m22 - this.m02 * this.m20;
		vector3d.z = this.m02 * this.m10 - this.m00 * this.m12;
		return vector3d.normalize(vector3d);
	}

	public Vector3d normalizedPositiveY(Vector3d vector3d) {
		vector3d.x = this.m01;
		vector3d.y = this.m11;
		vector3d.z = this.m21;
		return vector3d;
	}

	public int hashCode() {
		byte byte1 = 1;
		long long1 = Double.doubleToLongBits(this.m00);
		int int1 = 31 * byte1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m01);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m02);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m10);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m11);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m12);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m20);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m21);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m22);
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
			Matrix3d matrix3d = (Matrix3d)object;
			if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(matrix3d.m00)) {
				return false;
			} else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(matrix3d.m01)) {
				return false;
			} else if (Double.doubleToLongBits(this.m02) != Double.doubleToLongBits(matrix3d.m02)) {
				return false;
			} else if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(matrix3d.m10)) {
				return false;
			} else if (Double.doubleToLongBits(this.m11) != Double.doubleToLongBits(matrix3d.m11)) {
				return false;
			} else if (Double.doubleToLongBits(this.m12) != Double.doubleToLongBits(matrix3d.m12)) {
				return false;
			} else if (Double.doubleToLongBits(this.m20) != Double.doubleToLongBits(matrix3d.m20)) {
				return false;
			} else if (Double.doubleToLongBits(this.m21) != Double.doubleToLongBits(matrix3d.m21)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.m22) == Double.doubleToLongBits(matrix3d.m22);
			}
		}
	}

	public boolean equals(Matrix3dc matrix3dc, double double1) {
		if (this == matrix3dc) {
			return true;
		} else if (matrix3dc == null) {
			return false;
		} else if (!(matrix3dc instanceof Matrix3d)) {
			return false;
		} else if (!Runtime.equals(this.m00, matrix3dc.m00(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m01, matrix3dc.m01(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m02, matrix3dc.m02(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m10, matrix3dc.m10(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m11, matrix3dc.m11(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m12, matrix3dc.m12(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m20, matrix3dc.m20(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m21, matrix3dc.m21(), double1)) {
			return false;
		} else {
			return Runtime.equals(this.m22, matrix3dc.m22(), double1);
		}
	}

	public Matrix3d swap(Matrix3d matrix3d) {
		double double1 = this.m00;
		this.m00 = matrix3d.m00;
		matrix3d.m00 = double1;
		double1 = this.m01;
		this.m01 = matrix3d.m01;
		matrix3d.m01 = double1;
		double1 = this.m02;
		this.m02 = matrix3d.m02;
		matrix3d.m02 = double1;
		double1 = this.m10;
		this.m10 = matrix3d.m10;
		matrix3d.m10 = double1;
		double1 = this.m11;
		this.m11 = matrix3d.m11;
		matrix3d.m11 = double1;
		double1 = this.m12;
		this.m12 = matrix3d.m12;
		matrix3d.m12 = double1;
		double1 = this.m20;
		this.m20 = matrix3d.m20;
		matrix3d.m20 = double1;
		double1 = this.m21;
		this.m21 = matrix3d.m21;
		matrix3d.m21 = double1;
		double1 = this.m22;
		this.m22 = matrix3d.m22;
		matrix3d.m22 = double1;
		return this;
	}

	public Matrix3d add(Matrix3dc matrix3dc) {
		return this.add(matrix3dc, this);
	}

	public Matrix3d add(Matrix3dc matrix3dc, Matrix3d matrix3d) {
		matrix3d.m00 = this.m00 + matrix3dc.m00();
		matrix3d.m01 = this.m01 + matrix3dc.m01();
		matrix3d.m02 = this.m02 + matrix3dc.m02();
		matrix3d.m10 = this.m10 + matrix3dc.m10();
		matrix3d.m11 = this.m11 + matrix3dc.m11();
		matrix3d.m12 = this.m12 + matrix3dc.m12();
		matrix3d.m20 = this.m20 + matrix3dc.m20();
		matrix3d.m21 = this.m21 + matrix3dc.m21();
		matrix3d.m22 = this.m22 + matrix3dc.m22();
		return matrix3d;
	}

	public Matrix3d sub(Matrix3dc matrix3dc) {
		return this.sub(matrix3dc, this);
	}

	public Matrix3d sub(Matrix3dc matrix3dc, Matrix3d matrix3d) {
		matrix3d.m00 = this.m00 - matrix3dc.m00();
		matrix3d.m01 = this.m01 - matrix3dc.m01();
		matrix3d.m02 = this.m02 - matrix3dc.m02();
		matrix3d.m10 = this.m10 - matrix3dc.m10();
		matrix3d.m11 = this.m11 - matrix3dc.m11();
		matrix3d.m12 = this.m12 - matrix3dc.m12();
		matrix3d.m20 = this.m20 - matrix3dc.m20();
		matrix3d.m21 = this.m21 - matrix3dc.m21();
		matrix3d.m22 = this.m22 - matrix3dc.m22();
		return matrix3d;
	}

	public Matrix3d mulComponentWise(Matrix3dc matrix3dc) {
		return this.mulComponentWise(matrix3dc, this);
	}

	public Matrix3d mulComponentWise(Matrix3dc matrix3dc, Matrix3d matrix3d) {
		matrix3d.m00 = this.m00 * matrix3dc.m00();
		matrix3d.m01 = this.m01 * matrix3dc.m01();
		matrix3d.m02 = this.m02 * matrix3dc.m02();
		matrix3d.m10 = this.m10 * matrix3dc.m10();
		matrix3d.m11 = this.m11 * matrix3dc.m11();
		matrix3d.m12 = this.m12 * matrix3dc.m12();
		matrix3d.m20 = this.m20 * matrix3dc.m20();
		matrix3d.m21 = this.m21 * matrix3dc.m21();
		matrix3d.m22 = this.m22 * matrix3dc.m22();
		return matrix3d;
	}

	public Matrix3d setSkewSymmetric(double double1, double double2, double double3) {
		this.m00 = this.m11 = this.m22 = 0.0;
		this.m01 = -double1;
		this.m02 = double2;
		this.m10 = double1;
		this.m12 = -double3;
		this.m20 = -double2;
		this.m21 = double3;
		return this;
	}

	public Matrix3d lerp(Matrix3dc matrix3dc, double double1) {
		return this.lerp(matrix3dc, double1, this);
	}

	public Matrix3d lerp(Matrix3dc matrix3dc, double double1, Matrix3d matrix3d) {
		matrix3d.m00 = Math.fma(matrix3dc.m00() - this.m00, double1, this.m00);
		matrix3d.m01 = Math.fma(matrix3dc.m01() - this.m01, double1, this.m01);
		matrix3d.m02 = Math.fma(matrix3dc.m02() - this.m02, double1, this.m02);
		matrix3d.m10 = Math.fma(matrix3dc.m10() - this.m10, double1, this.m10);
		matrix3d.m11 = Math.fma(matrix3dc.m11() - this.m11, double1, this.m11);
		matrix3d.m12 = Math.fma(matrix3dc.m12() - this.m12, double1, this.m12);
		matrix3d.m20 = Math.fma(matrix3dc.m20() - this.m20, double1, this.m20);
		matrix3d.m21 = Math.fma(matrix3dc.m21() - this.m21, double1, this.m21);
		matrix3d.m22 = Math.fma(matrix3dc.m22() - this.m22, double1, this.m22);
		return matrix3d;
	}

	public Matrix3d rotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix3d matrix3d) {
		return this.rotateTowards(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), matrix3d);
	}

	public Matrix3d rotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.rotateTowards(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), this);
	}

	public Matrix3d rotateTowards(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.rotateTowards(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix3d rotateTowards(double double1, double double2, double double3, double double4, double double5, double double6, Matrix3d matrix3d) {
		double double7 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = double1 * double7;
		double double9 = double2 * double7;
		double double10 = double3 * double7;
		double double11 = double5 * double10 - double6 * double9;
		double double12 = double6 * double8 - double4 * double10;
		double double13 = double4 * double9 - double5 * double8;
		double double14 = Math.invsqrt(double11 * double11 + double12 * double12 + double13 * double13);
		double11 *= double14;
		double12 *= double14;
		double13 *= double14;
		double double15 = double9 * double13 - double10 * double12;
		double double16 = double10 * double11 - double8 * double13;
		double double17 = double8 * double12 - double9 * double11;
		double double18 = this.m00 * double11 + this.m10 * double12 + this.m20 * double13;
		double double19 = this.m01 * double11 + this.m11 * double12 + this.m21 * double13;
		double double20 = this.m02 * double11 + this.m12 * double12 + this.m22 * double13;
		double double21 = this.m00 * double15 + this.m10 * double16 + this.m20 * double17;
		double double22 = this.m01 * double15 + this.m11 * double16 + this.m21 * double17;
		double double23 = this.m02 * double15 + this.m12 * double16 + this.m22 * double17;
		matrix3d.m20 = this.m00 * double8 + this.m10 * double9 + this.m20 * double10;
		matrix3d.m21 = this.m01 * double8 + this.m11 * double9 + this.m21 * double10;
		matrix3d.m22 = this.m02 * double8 + this.m12 * double9 + this.m22 * double10;
		matrix3d.m00 = double18;
		matrix3d.m01 = double19;
		matrix3d.m02 = double20;
		matrix3d.m10 = double21;
		matrix3d.m11 = double22;
		matrix3d.m12 = double23;
		return matrix3d;
	}

	public Matrix3d rotationTowards(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.rotationTowards(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix3d rotationTowards(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = double1 * double7;
		double double9 = double2 * double7;
		double double10 = double3 * double7;
		double double11 = double5 * double10 - double6 * double9;
		double double12 = double6 * double8 - double4 * double10;
		double double13 = double4 * double9 - double5 * double8;
		double double14 = Math.invsqrt(double11 * double11 + double12 * double12 + double13 * double13);
		double11 *= double14;
		double12 *= double14;
		double13 *= double14;
		double double15 = double9 * double13 - double10 * double12;
		double double16 = double10 * double11 - double8 * double13;
		double double17 = double8 * double12 - double9 * double11;
		this.m00 = double11;
		this.m01 = double12;
		this.m02 = double13;
		this.m10 = double15;
		this.m11 = double16;
		this.m12 = double17;
		this.m20 = double8;
		this.m21 = double9;
		this.m22 = double10;
		return this;
	}

	public Vector3d getEulerAnglesZYX(Vector3d vector3d) {
		vector3d.x = (double)((float)Math.atan2(this.m12, this.m22));
		vector3d.y = (double)((float)Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22)));
		vector3d.z = (double)((float)Math.atan2(this.m01, this.m00));
		return vector3d;
	}

	public Matrix3d obliqueZ(double double1, double double2) {
		this.m20 += this.m00 * double1 + this.m10 * double2;
		this.m21 += this.m01 * double1 + this.m11 * double2;
		this.m22 += this.m02 * double1 + this.m12 * double2;
		return this;
	}

	public Matrix3d obliqueZ(double double1, double double2, Matrix3d matrix3d) {
		matrix3d.m00 = this.m00;
		matrix3d.m01 = this.m01;
		matrix3d.m02 = this.m02;
		matrix3d.m10 = this.m10;
		matrix3d.m11 = this.m11;
		matrix3d.m12 = this.m12;
		matrix3d.m20 = this.m00 * double1 + this.m10 * double2 + this.m20;
		matrix3d.m21 = this.m01 * double1 + this.m11 * double2 + this.m21;
		matrix3d.m22 = this.m02 * double1 + this.m12 * double2 + this.m22;
		return matrix3d;
	}

	public Matrix3d reflect(double double1, double double2, double double3, Matrix3d matrix3d) {
		double double4 = double1 + double1;
		double double5 = double2 + double2;
		double double6 = double3 + double3;
		double double7 = 1.0 - double4 * double1;
		double double8 = -double4 * double2;
		double double9 = -double4 * double3;
		double double10 = -double5 * double1;
		double double11 = 1.0 - double5 * double2;
		double double12 = -double5 * double3;
		double double13 = -double6 * double1;
		double double14 = -double6 * double2;
		double double15 = 1.0 - double6 * double3;
		double double16 = this.m00 * double7 + this.m10 * double8 + this.m20 * double9;
		double double17 = this.m01 * double7 + this.m11 * double8 + this.m21 * double9;
		double double18 = this.m02 * double7 + this.m12 * double8 + this.m22 * double9;
		double double19 = this.m00 * double10 + this.m10 * double11 + this.m20 * double12;
		double double20 = this.m01 * double10 + this.m11 * double11 + this.m21 * double12;
		double double21 = this.m02 * double10 + this.m12 * double11 + this.m22 * double12;
		return matrix3d._m20(this.m00 * double13 + this.m10 * double14 + this.m20 * double15)._m21(this.m01 * double13 + this.m11 * double14 + this.m21 * double15)._m22(this.m02 * double13 + this.m12 * double14 + this.m22 * double15)._m00(double16)._m01(double17)._m02(double18)._m10(double19)._m11(double20)._m12(double21);
	}

	public Matrix3d reflect(double double1, double double2, double double3) {
		return this.reflect(double1, double2, double3, this);
	}

	public Matrix3d reflect(Vector3dc vector3dc) {
		return this.reflect(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix3d reflect(Quaterniondc quaterniondc) {
		return this.reflect(quaterniondc, this);
	}

	public Matrix3d reflect(Quaterniondc quaterniondc, Matrix3d matrix3d) {
		double double1 = quaterniondc.x() + quaterniondc.x();
		double double2 = quaterniondc.y() + quaterniondc.y();
		double double3 = quaterniondc.z() + quaterniondc.z();
		double double4 = quaterniondc.x() * double3 + quaterniondc.w() * double2;
		double double5 = quaterniondc.y() * double3 - quaterniondc.w() * double1;
		double double6 = 1.0 - (quaterniondc.x() * double1 + quaterniondc.y() * double2);
		return this.reflect(double4, double5, double6, matrix3d);
	}

	public Matrix3d reflect(Vector3dc vector3dc, Matrix3d matrix3d) {
		return this.reflect(vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix3d);
	}

	public Matrix3d reflection(double double1, double double2, double double3) {
		double double4 = double1 + double1;
		double double5 = double2 + double2;
		double double6 = double3 + double3;
		this._m00(1.0 - double4 * double1);
		this._m01(-double4 * double2);
		this._m02(-double4 * double3);
		this._m10(-double5 * double1);
		this._m11(1.0 - double5 * double2);
		this._m12(-double5 * double3);
		this._m20(-double6 * double1);
		this._m21(-double6 * double2);
		this._m22(1.0 - double6 * double3);
		return this;
	}

	public Matrix3d reflection(Vector3dc vector3dc) {
		return this.reflection(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix3d reflection(Quaterniondc quaterniondc) {
		double double1 = quaterniondc.x() + quaterniondc.x();
		double double2 = quaterniondc.y() + quaterniondc.y();
		double double3 = quaterniondc.z() + quaterniondc.z();
		double double4 = quaterniondc.x() * double3 + quaterniondc.w() * double2;
		double double5 = quaterniondc.y() * double3 - quaterniondc.w() * double1;
		double double6 = 1.0 - (quaterniondc.x() * double1 + quaterniondc.y() * double2);
		return this.reflection(double4, double5, double6);
	}

	public boolean isFinite() {
		return Math.isFinite(this.m00) && Math.isFinite(this.m01) && Math.isFinite(this.m02) && Math.isFinite(this.m10) && Math.isFinite(this.m11) && Math.isFinite(this.m12) && Math.isFinite(this.m20) && Math.isFinite(this.m21) && Math.isFinite(this.m22);
	}

	public double quadraticFormProduct(double double1, double double2, double double3) {
		double double4 = this.m00 * double1 + this.m10 * double2 + this.m20 * double3;
		double double5 = this.m01 * double1 + this.m11 * double2 + this.m21 * double3;
		double double6 = this.m02 * double1 + this.m12 * double2 + this.m22 * double3;
		return double1 * double4 + double2 * double5 + double3 * double6;
	}

	public double quadraticFormProduct(Vector3dc vector3dc) {
		return this.quadraticFormProduct(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public double quadraticFormProduct(Vector3fc vector3fc) {
		return this.quadraticFormProduct((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}
}
