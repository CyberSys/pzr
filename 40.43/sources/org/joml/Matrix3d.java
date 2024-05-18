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

	public Matrix3d(Matrix3dc matrix3dc) {
		this.m00 = matrix3dc.m00();
		this.m01 = matrix3dc.m01();
		this.m02 = matrix3dc.m02();
		this.m10 = matrix3dc.m10();
		this.m11 = matrix3dc.m11();
		this.m12 = matrix3dc.m12();
		this.m20 = matrix3dc.m20();
		this.m21 = matrix3dc.m21();
		this.m22 = matrix3dc.m22();
	}

	public Matrix3d(Matrix3fc matrix3fc) {
		this.m00 = (double)matrix3fc.m00();
		this.m01 = (double)matrix3fc.m01();
		this.m02 = (double)matrix3fc.m02();
		this.m10 = (double)matrix3fc.m10();
		this.m11 = (double)matrix3fc.m11();
		this.m12 = (double)matrix3fc.m12();
		this.m20 = (double)matrix3fc.m20();
		this.m21 = (double)matrix3fc.m21();
		this.m22 = (double)matrix3fc.m22();
	}

	public Matrix3d(Matrix4fc matrix4fc) {
		this.m00 = (double)matrix4fc.m00();
		this.m01 = (double)matrix4fc.m01();
		this.m02 = (double)matrix4fc.m02();
		this.m10 = (double)matrix4fc.m10();
		this.m11 = (double)matrix4fc.m11();
		this.m12 = (double)matrix4fc.m12();
		this.m20 = (double)matrix4fc.m20();
		this.m21 = (double)matrix4fc.m21();
		this.m22 = (double)matrix4fc.m22();
	}

	public Matrix3d(Matrix4dc matrix4dc) {
		this.m00 = matrix4dc.m00();
		this.m01 = matrix4dc.m01();
		this.m02 = matrix4dc.m02();
		this.m10 = matrix4dc.m10();
		this.m11 = matrix4dc.m11();
		this.m12 = matrix4dc.m12();
		this.m20 = matrix4dc.m20();
		this.m21 = matrix4dc.m21();
		this.m22 = matrix4dc.m22();
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
		this.m00 = vector3dc.x();
		this.m01 = vector3dc.y();
		this.m02 = vector3dc.z();
		this.m10 = vector3dc2.x();
		this.m11 = vector3dc2.y();
		this.m12 = vector3dc2.z();
		this.m20 = vector3dc3.x();
		this.m21 = vector3dc3.y();
		this.m22 = vector3dc3.z();
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

	public Matrix3d set(AxisAngle4f axisAngle4f) {
		double double1 = (double)axisAngle4f.x;
		double double2 = (double)axisAngle4f.y;
		double double3 = (double)axisAngle4f.z;
		double double4 = (double)axisAngle4f.angle;
		double double5 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double1 *= double5;
		double2 *= double5;
		double3 *= double5;
		double double6 = Math.cos(double4);
		double double7 = Math.sin(double4);
		double double8 = 1.0 - double6;
		this.m00 = double6 + double1 * double1 * double8;
		this.m11 = double6 + double2 * double2 * double8;
		this.m22 = double6 + double3 * double3 * double8;
		double double9 = double1 * double2 * double8;
		double double10 = double3 * double7;
		this.m10 = double9 - double10;
		this.m01 = double9 + double10;
		double9 = double1 * double3 * double8;
		double10 = double2 * double7;
		this.m20 = double9 + double10;
		this.m02 = double9 - double10;
		double9 = double2 * double3 * double8;
		double10 = double1 * double7;
		this.m21 = double9 - double10;
		this.m12 = double9 + double10;
		return this;
	}

	public Matrix3d set(AxisAngle4d axisAngle4d) {
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
		this.m00 = double6 + double1 * double1 * double8;
		this.m11 = double6 + double2 * double2 * double8;
		this.m22 = double6 + double3 * double3 * double8;
		double double9 = double1 * double2 * double8;
		double double10 = double3 * double7;
		this.m10 = double9 - double10;
		this.m01 = double9 + double10;
		double9 = double1 * double3 * double8;
		double10 = double2 * double7;
		this.m20 = double9 + double10;
		this.m02 = double9 - double10;
		double9 = double2 * double3 * double8;
		double10 = double1 * double7;
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
		double double1 = this.m00 * matrix3dc.m00() + this.m10 * matrix3dc.m01() + this.m20 * matrix3dc.m02();
		double double2 = this.m01 * matrix3dc.m00() + this.m11 * matrix3dc.m01() + this.m21 * matrix3dc.m02();
		double double3 = this.m02 * matrix3dc.m00() + this.m12 * matrix3dc.m01() + this.m22 * matrix3dc.m02();
		double double4 = this.m00 * matrix3dc.m10() + this.m10 * matrix3dc.m11() + this.m20 * matrix3dc.m12();
		double double5 = this.m01 * matrix3dc.m10() + this.m11 * matrix3dc.m11() + this.m21 * matrix3dc.m12();
		double double6 = this.m02 * matrix3dc.m10() + this.m12 * matrix3dc.m11() + this.m22 * matrix3dc.m12();
		double double7 = this.m00 * matrix3dc.m20() + this.m10 * matrix3dc.m21() + this.m20 * matrix3dc.m22();
		double double8 = this.m01 * matrix3dc.m20() + this.m11 * matrix3dc.m21() + this.m21 * matrix3dc.m22();
		double double9 = this.m02 * matrix3dc.m20() + this.m12 * matrix3dc.m21() + this.m22 * matrix3dc.m22();
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
		double double1 = this.m00 * (double)matrix3fc.m00() + this.m10 * (double)matrix3fc.m01() + this.m20 * (double)matrix3fc.m02();
		double double2 = this.m01 * (double)matrix3fc.m00() + this.m11 * (double)matrix3fc.m01() + this.m21 * (double)matrix3fc.m02();
		double double3 = this.m02 * (double)matrix3fc.m00() + this.m12 * (double)matrix3fc.m01() + this.m22 * (double)matrix3fc.m02();
		double double4 = this.m00 * (double)matrix3fc.m10() + this.m10 * (double)matrix3fc.m11() + this.m20 * (double)matrix3fc.m12();
		double double5 = this.m01 * (double)matrix3fc.m10() + this.m11 * (double)matrix3fc.m11() + this.m21 * (double)matrix3fc.m12();
		double double6 = this.m02 * (double)matrix3fc.m10() + this.m12 * (double)matrix3fc.m11() + this.m22 * (double)matrix3fc.m12();
		double double7 = this.m00 * (double)matrix3fc.m20() + this.m10 * (double)matrix3fc.m21() + this.m20 * (double)matrix3fc.m22();
		double double8 = this.m01 * (double)matrix3fc.m20() + this.m11 * (double)matrix3fc.m21() + this.m21 * (double)matrix3fc.m22();
		double double9 = this.m02 * (double)matrix3fc.m20() + this.m12 * (double)matrix3fc.m21() + this.m22 * (double)matrix3fc.m22();
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
		double double1 = this.determinant();
		double1 = 1.0 / double1;
		double double2 = (this.m11 * this.m22 - this.m21 * this.m12) * double1;
		double double3 = (this.m21 * this.m02 - this.m01 * this.m22) * double1;
		double double4 = (this.m01 * this.m12 - this.m11 * this.m02) * double1;
		double double5 = (this.m20 * this.m12 - this.m10 * this.m22) * double1;
		double double6 = (this.m00 * this.m22 - this.m20 * this.m02) * double1;
		double double7 = (this.m10 * this.m02 - this.m00 * this.m12) * double1;
		double double8 = (this.m10 * this.m21 - this.m20 * this.m11) * double1;
		double double9 = (this.m20 * this.m01 - this.m00 * this.m21) * double1;
		double double10 = (this.m00 * this.m11 - this.m10 * this.m01) * double1;
		matrix3d.m00 = double2;
		matrix3d.m01 = double3;
		matrix3d.m02 = double4;
		matrix3d.m10 = double5;
		matrix3d.m11 = double6;
		matrix3d.m12 = double7;
		matrix3d.m20 = double8;
		matrix3d.m21 = double9;
		matrix3d.m22 = double10;
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
		DecimalFormat decimalFormat = new DecimalFormat("  0.000E0; -");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return numberFormat.format(this.m00) + numberFormat.format(this.m10) + numberFormat.format(this.m20) + "\n" + numberFormat.format(this.m01) + numberFormat.format(this.m11) + numberFormat.format(this.m21) + "\n" + numberFormat.format(this.m02) + numberFormat.format(this.m12) + numberFormat.format(this.m22) + "\n";
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
		double double5 = Math.cos(double1);
		double double6 = Math.sin(double1);
		double double7 = 1.0 - double5;
		double double8 = double2 * double3;
		double double9 = double2 * double4;
		double double10 = double3 * double4;
		this.m00 = double5 + double2 * double2 * double7;
		this.m10 = double8 * double7 - double4 * double6;
		this.m20 = double9 * double7 + double3 * double6;
		this.m01 = double8 * double7 + double4 * double6;
		this.m11 = double5 + double3 * double3 * double7;
		this.m21 = double10 * double7 - double2 * double6;
		this.m02 = double9 * double7 - double3 * double6;
		this.m12 = double10 * double7 + double2 * double6;
		this.m22 = double5 + double4 * double4 * double7;
		return this;
	}

	public Matrix3d rotationX(double double1) {
		double double2;
		double double3;
		if (double1 != 3.141592653589793 && double1 != -3.141592653589793) {
			if (double1 != 1.5707963267948966 && double1 != -4.71238898038469) {
				if (double1 != -1.5707963267948966 && double1 != 4.71238898038469) {
					double3 = Math.cos(double1);
					double2 = Math.sin(double1);
				} else {
					double3 = 0.0;
					double2 = -1.0;
				}
			} else {
				double3 = 0.0;
				double2 = 1.0;
			}
		} else {
			double3 = -1.0;
			double2 = 0.0;
		}

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
		double double2;
		double double3;
		if (double1 != 3.141592653589793 && double1 != -3.141592653589793) {
			if (double1 != 1.5707963267948966 && double1 != -4.71238898038469) {
				if (double1 != -1.5707963267948966 && double1 != 4.71238898038469) {
					double3 = Math.cos(double1);
					double2 = Math.sin(double1);
				} else {
					double3 = 0.0;
					double2 = -1.0;
				}
			} else {
				double3 = 0.0;
				double2 = 1.0;
			}
		} else {
			double3 = -1.0;
			double2 = 0.0;
		}

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
		double double2;
		double double3;
		if (double1 != 3.141592653589793 && double1 != -3.141592653589793) {
			if (double1 != 1.5707963267948966 && double1 != -4.71238898038469) {
				if (double1 != -1.5707963267948966 && double1 != 4.71238898038469) {
					double3 = Math.cos(double1);
					double2 = Math.sin(double1);
				} else {
					double3 = 0.0;
					double2 = -1.0;
				}
			} else {
				double3 = 0.0;
				double2 = 1.0;
			}
		} else {
			double3 = -1.0;
			double2 = 0.0;
		}

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
		double double4 = Math.cos(double1);
		double double5 = Math.sin(double1);
		double double6 = Math.cos(double2);
		double double7 = Math.sin(double2);
		double double8 = Math.cos(double3);
		double double9 = Math.sin(double3);
		double double10 = -double5;
		double double11 = -double7;
		double double12 = -double9;
		double double13 = double10 * double11;
		double double14 = double4 * double11;
		this.m20 = double7;
		this.m21 = double10 * double6;
		this.m22 = double4 * double6;
		this.m00 = double6 * double8;
		this.m01 = double13 * double8 + double4 * double9;
		this.m02 = double14 * double8 + double5 * double9;
		this.m10 = double6 * double12;
		this.m11 = double13 * double12 + double4 * double8;
		this.m12 = double14 * double12 + double5 * double8;
		return this;
	}

	public Matrix3d rotationZYX(double double1, double double2, double double3) {
		double double4 = Math.cos(double1);
		double double5 = Math.sin(double1);
		double double6 = Math.cos(double2);
		double double7 = Math.sin(double2);
		double double8 = Math.cos(double3);
		double double9 = Math.sin(double3);
		double double10 = -double5;
		double double11 = -double7;
		double double12 = -double9;
		double double13 = double4 * double7;
		double double14 = double5 * double7;
		this.m00 = double4 * double6;
		this.m01 = double5 * double6;
		this.m02 = double11;
		this.m10 = double10 * double8 + double13 * double9;
		this.m11 = double4 * double8 + double14 * double9;
		this.m12 = double6 * double9;
		this.m20 = double10 * double12 + double13 * double8;
		this.m21 = double4 * double12 + double14 * double8;
		this.m22 = double6 * double8;
		return this;
	}

	public Matrix3d rotationYXZ(double double1, double double2, double double3) {
		double double4 = Math.cos(double1);
		double double5 = Math.sin(double1);
		double double6 = Math.cos(double2);
		double double7 = Math.sin(double2);
		double double8 = Math.cos(double3);
		double double9 = Math.sin(double3);
		double double10 = -double5;
		double double11 = -double7;
		double double12 = -double9;
		double double13 = double5 * double7;
		double double14 = double4 * double7;
		this.m20 = double5 * double6;
		this.m21 = double11;
		this.m22 = double4 * double6;
		this.m00 = double4 * double8 + double13 * double9;
		this.m01 = double6 * double9;
		this.m02 = double10 * double8 + double14 * double9;
		this.m10 = double4 * double12 + double13 * double8;
		this.m11 = double6 * double8;
		this.m12 = double10 * double12 + double14 * double8;
		return this;
	}

	public Matrix3d rotation(Quaterniondc quaterniondc) {
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
		this.m00 = double1 + double2 - double4 - double3;
		this.m01 = double6 + double5 + double5 + double6;
		this.m02 = double7 - double8 + double7 - double8;
		this.m10 = -double5 + double6 - double5 + double6;
		this.m11 = double3 - double4 + double1 - double2;
		this.m12 = double9 + double9 + double10 + double10;
		this.m20 = double8 + double7 + double7 + double8;
		this.m21 = double9 + double9 - double10 - double10;
		this.m22 = double4 - double3 - double2 + double1;
		return this;
	}

	public Matrix3d rotation(Quaternionfc quaternionfc) {
		double double1 = (double)(quaternionfc.w() * quaternionfc.w());
		double double2 = (double)(quaternionfc.x() * quaternionfc.x());
		double double3 = (double)(quaternionfc.y() * quaternionfc.y());
		double double4 = (double)(quaternionfc.z() * quaternionfc.z());
		double double5 = (double)(quaternionfc.z() * quaternionfc.w());
		double double6 = (double)(quaternionfc.x() * quaternionfc.y());
		double double7 = (double)(quaternionfc.x() * quaternionfc.z());
		double double8 = (double)(quaternionfc.y() * quaternionfc.w());
		double double9 = (double)(quaternionfc.y() * quaternionfc.z());
		double double10 = (double)(quaternionfc.x() * quaternionfc.w());
		this.m00 = double1 + double2 - double4 - double3;
		this.m01 = double6 + double5 + double5 + double6;
		this.m02 = double7 - double8 + double7 - double8;
		this.m10 = -double5 + double6 - double5 + double6;
		this.m11 = double3 - double4 + double1 - double2;
		this.m12 = double9 + double9 + double10 + double10;
		this.m20 = double8 + double7 + double7 + double8;
		this.m21 = double9 + double9 - double10 - double10;
		this.m22 = double4 - double3 - double2 + double1;
		return this;
	}

	public Vector3d transform(Vector3d vector3d) {
		return vector3d.mul((Matrix3dc)this);
	}

	public Vector3d transform(Vector3dc vector3dc, Vector3d vector3d) {
		vector3dc.mul((Matrix3dc)this, vector3d);
		return vector3d;
	}

	public Vector3d transform(double double1, double double2, double double3, Vector3d vector3d) {
		vector3d.set(this.m00 * double1 + this.m10 * double2 + this.m20 * double3, this.m01 * double1 + this.m11 * double2 + this.m21 * double3, this.m02 * double1 + this.m12 * double2 + this.m22 * double3);
		return vector3d;
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

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
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
		double double2;
		double double3;
		if (double1 != 3.141592653589793 && double1 != -3.141592653589793) {
			if (double1 != 1.5707963267948966 && double1 != -4.71238898038469) {
				if (double1 != -1.5707963267948966 && double1 != 4.71238898038469) {
					double3 = Math.cos(double1);
					double2 = Math.sin(double1);
				} else {
					double3 = 0.0;
					double2 = -1.0;
				}
			} else {
				double3 = 0.0;
				double2 = 1.0;
			}
		} else {
			double3 = -1.0;
			double2 = 0.0;
		}

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
		double double2;
		double double3;
		if (double1 != 3.141592653589793 && double1 != -3.141592653589793) {
			if (double1 != 1.5707963267948966 && double1 != -4.71238898038469) {
				if (double1 != -1.5707963267948966 && double1 != 4.71238898038469) {
					double3 = Math.cos(double1);
					double2 = Math.sin(double1);
				} else {
					double3 = 0.0;
					double2 = -1.0;
				}
			} else {
				double3 = 0.0;
				double2 = 1.0;
			}
		} else {
			double3 = -1.0;
			double2 = 0.0;
		}

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
		double double2;
		double double3;
		if (double1 != 3.141592653589793 && double1 != -3.141592653589793) {
			if (double1 != 1.5707963267948966 && double1 != -4.71238898038469) {
				if (double1 != -1.5707963267948966 && double1 != 4.71238898038469) {
					double3 = Math.cos(double1);
					double2 = Math.sin(double1);
				} else {
					double3 = 0.0;
					double2 = -1.0;
				}
			} else {
				double3 = 0.0;
				double2 = 1.0;
			}
		} else {
			double3 = -1.0;
			double2 = 0.0;
		}

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
		double double4 = Math.cos(double1);
		double double5 = Math.sin(double1);
		double double6 = Math.cos(double2);
		double double7 = Math.sin(double2);
		double double8 = Math.cos(double3);
		double double9 = Math.sin(double3);
		double double10 = -double5;
		double double11 = -double7;
		double double12 = -double9;
		double double13 = this.m10 * double4 + this.m20 * double5;
		double double14 = this.m11 * double4 + this.m21 * double5;
		double double15 = this.m12 * double4 + this.m22 * double5;
		double double16 = this.m10 * double10 + this.m20 * double4;
		double double17 = this.m11 * double10 + this.m21 * double4;
		double double18 = this.m12 * double10 + this.m22 * double4;
		double double19 = this.m00 * double6 + double16 * double11;
		double double20 = this.m01 * double6 + double17 * double11;
		double double21 = this.m02 * double6 + double18 * double11;
		matrix3d.m20 = this.m00 * double7 + double16 * double6;
		matrix3d.m21 = this.m01 * double7 + double17 * double6;
		matrix3d.m22 = this.m02 * double7 + double18 * double6;
		matrix3d.m00 = double19 * double8 + double13 * double9;
		matrix3d.m01 = double20 * double8 + double14 * double9;
		matrix3d.m02 = double21 * double8 + double15 * double9;
		matrix3d.m10 = double19 * double12 + double13 * double8;
		matrix3d.m11 = double20 * double12 + double14 * double8;
		matrix3d.m12 = double21 * double12 + double15 * double8;
		return matrix3d;
	}

	public Matrix3d rotateZYX(double double1, double double2, double double3) {
		return this.rotateZYX(double1, double2, double3, this);
	}

	public Matrix3d rotateZYX(double double1, double double2, double double3, Matrix3d matrix3d) {
		double double4 = Math.cos(double1);
		double double5 = Math.sin(double1);
		double double6 = Math.cos(double2);
		double double7 = Math.sin(double2);
		double double8 = Math.cos(double3);
		double double9 = Math.sin(double3);
		double double10 = -double5;
		double double11 = -double7;
		double double12 = -double9;
		double double13 = this.m00 * double4 + this.m10 * double5;
		double double14 = this.m01 * double4 + this.m11 * double5;
		double double15 = this.m02 * double4 + this.m12 * double5;
		double double16 = this.m00 * double10 + this.m10 * double4;
		double double17 = this.m01 * double10 + this.m11 * double4;
		double double18 = this.m02 * double10 + this.m12 * double4;
		double double19 = double13 * double7 + this.m20 * double6;
		double double20 = double14 * double7 + this.m21 * double6;
		double double21 = double15 * double7 + this.m22 * double6;
		matrix3d.m00 = double13 * double6 + this.m20 * double11;
		matrix3d.m01 = double14 * double6 + this.m21 * double11;
		matrix3d.m02 = double15 * double6 + this.m22 * double11;
		matrix3d.m10 = double16 * double8 + double19 * double9;
		matrix3d.m11 = double17 * double8 + double20 * double9;
		matrix3d.m12 = double18 * double8 + double21 * double9;
		matrix3d.m20 = double16 * double12 + double19 * double8;
		matrix3d.m21 = double17 * double12 + double20 * double8;
		matrix3d.m22 = double18 * double12 + double21 * double8;
		return matrix3d;
	}

	public Matrix3d rotateYXZ(Vector3d vector3d) {
		return this.rotateYXZ(vector3d.y, vector3d.x, vector3d.z);
	}

	public Matrix3d rotateYXZ(double double1, double double2, double double3) {
		return this.rotateYXZ(double1, double2, double3, this);
	}

	public Matrix3d rotateYXZ(double double1, double double2, double double3, Matrix3d matrix3d) {
		double double4 = Math.cos(double1);
		double double5 = Math.sin(double1);
		double double6 = Math.cos(double2);
		double double7 = Math.sin(double2);
		double double8 = Math.cos(double3);
		double double9 = Math.sin(double3);
		double double10 = -double5;
		double double11 = -double7;
		double double12 = -double9;
		double double13 = this.m00 * double5 + this.m20 * double4;
		double double14 = this.m01 * double5 + this.m21 * double4;
		double double15 = this.m02 * double5 + this.m22 * double4;
		double double16 = this.m00 * double4 + this.m20 * double10;
		double double17 = this.m01 * double4 + this.m21 * double10;
		double double18 = this.m02 * double4 + this.m22 * double10;
		double double19 = this.m10 * double6 + double13 * double7;
		double double20 = this.m11 * double6 + double14 * double7;
		double double21 = this.m12 * double6 + double15 * double7;
		matrix3d.m20 = this.m10 * double11 + double13 * double6;
		matrix3d.m21 = this.m11 * double11 + double14 * double6;
		matrix3d.m22 = this.m12 * double11 + double15 * double6;
		matrix3d.m00 = double16 * double8 + double19 * double9;
		matrix3d.m01 = double17 * double8 + double20 * double9;
		matrix3d.m02 = double18 * double8 + double21 * double9;
		matrix3d.m10 = double16 * double12 + double19 * double8;
		matrix3d.m11 = double17 * double12 + double20 * double8;
		matrix3d.m12 = double18 * double12 + double21 * double8;
		return matrix3d;
	}

	public Matrix3d rotate(double double1, double double2, double double3, double double4) {
		return this.rotate(double1, double2, double3, double4, this);
	}

	public Matrix3d rotate(double double1, double double2, double double3, double double4, Matrix3d matrix3d) {
		double double5 = Math.sin(double1);
		double double6 = Math.cos(double1);
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
		double double6 = Math.cos(double1);
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

	public Matrix3d rotateLocal(Quaterniondc quaterniondc, Matrix3d matrix3d) {
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
		double double11 = double1 + double2 - double4 - double3;
		double double12 = double6 + double5 + double5 + double6;
		double double13 = double7 - double8 + double7 - double8;
		double double14 = -double5 + double6 - double5 + double6;
		double double15 = double3 - double4 + double1 - double2;
		double double16 = double9 + double9 + double10 + double10;
		double double17 = double8 + double7 + double7 + double8;
		double double18 = double9 + double9 - double10 - double10;
		double double19 = double4 - double3 - double2 + double1;
		double double20 = double11 * this.m00 + double14 * this.m01 + double17 * this.m02;
		double double21 = double12 * this.m00 + double15 * this.m01 + double18 * this.m02;
		double double22 = double13 * this.m00 + double16 * this.m01 + double19 * this.m02;
		double double23 = double11 * this.m10 + double14 * this.m11 + double17 * this.m12;
		double double24 = double12 * this.m10 + double15 * this.m11 + double18 * this.m12;
		double double25 = double13 * this.m10 + double16 * this.m11 + double19 * this.m12;
		double double26 = double11 * this.m20 + double14 * this.m21 + double17 * this.m22;
		double double27 = double12 * this.m20 + double15 * this.m21 + double18 * this.m22;
		double double28 = double13 * this.m20 + double16 * this.m21 + double19 * this.m22;
		matrix3d.m00 = double20;
		matrix3d.m01 = double21;
		matrix3d.m02 = double22;
		matrix3d.m10 = double23;
		matrix3d.m11 = double24;
		matrix3d.m12 = double25;
		matrix3d.m20 = double26;
		matrix3d.m21 = double27;
		matrix3d.m22 = double28;
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
		double double6 = (double)(quaternionfc.x() * quaternionfc.y());
		double double7 = (double)(quaternionfc.x() * quaternionfc.z());
		double double8 = (double)(quaternionfc.y() * quaternionfc.w());
		double double9 = (double)(quaternionfc.y() * quaternionfc.z());
		double double10 = (double)(quaternionfc.x() * quaternionfc.w());
		double double11 = double1 + double2 - double4 - double3;
		double double12 = double6 + double5 + double5 + double6;
		double double13 = double7 - double8 + double7 - double8;
		double double14 = -double5 + double6 - double5 + double6;
		double double15 = double3 - double4 + double1 - double2;
		double double16 = double9 + double9 + double10 + double10;
		double double17 = double8 + double7 + double7 + double8;
		double double18 = double9 + double9 - double10 - double10;
		double double19 = double4 - double3 - double2 + double1;
		double double20 = double11 * this.m00 + double14 * this.m01 + double17 * this.m02;
		double double21 = double12 * this.m00 + double15 * this.m01 + double18 * this.m02;
		double double22 = double13 * this.m00 + double16 * this.m01 + double19 * this.m02;
		double double23 = double11 * this.m10 + double14 * this.m11 + double17 * this.m12;
		double double24 = double12 * this.m10 + double15 * this.m11 + double18 * this.m12;
		double double25 = double13 * this.m10 + double16 * this.m11 + double19 * this.m12;
		double double26 = double11 * this.m20 + double14 * this.m21 + double17 * this.m22;
		double double27 = double12 * this.m20 + double15 * this.m21 + double18 * this.m22;
		double double28 = double13 * this.m20 + double16 * this.m21 + double19 * this.m22;
		matrix3d.m00 = double20;
		matrix3d.m01 = double21;
		matrix3d.m02 = double22;
		matrix3d.m10 = double23;
		matrix3d.m11 = double24;
		matrix3d.m12 = double25;
		matrix3d.m20 = double26;
		matrix3d.m21 = double27;
		matrix3d.m22 = double28;
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
		double double6 = quaterniondc.x() * quaterniondc.y();
		double double7 = quaterniondc.x() * quaterniondc.z();
		double double8 = quaterniondc.y() * quaterniondc.w();
		double double9 = quaterniondc.y() * quaterniondc.z();
		double double10 = quaterniondc.x() * quaterniondc.w();
		double double11 = double1 + double2 - double4 - double3;
		double double12 = double6 + double5 + double5 + double6;
		double double13 = double7 - double8 + double7 - double8;
		double double14 = -double5 + double6 - double5 + double6;
		double double15 = double3 - double4 + double1 - double2;
		double double16 = double9 + double9 + double10 + double10;
		double double17 = double8 + double7 + double7 + double8;
		double double18 = double9 + double9 - double10 - double10;
		double double19 = double4 - double3 - double2 + double1;
		double double20 = this.m00 * double11 + this.m10 * double12 + this.m20 * double13;
		double double21 = this.m01 * double11 + this.m11 * double12 + this.m21 * double13;
		double double22 = this.m02 * double11 + this.m12 * double12 + this.m22 * double13;
		double double23 = this.m00 * double14 + this.m10 * double15 + this.m20 * double16;
		double double24 = this.m01 * double14 + this.m11 * double15 + this.m21 * double16;
		double double25 = this.m02 * double14 + this.m12 * double15 + this.m22 * double16;
		matrix3d.m20 = this.m00 * double17 + this.m10 * double18 + this.m20 * double19;
		matrix3d.m21 = this.m01 * double17 + this.m11 * double18 + this.m21 * double19;
		matrix3d.m22 = this.m02 * double17 + this.m12 * double18 + this.m22 * double19;
		matrix3d.m00 = double20;
		matrix3d.m01 = double21;
		matrix3d.m02 = double22;
		matrix3d.m10 = double23;
		matrix3d.m11 = double24;
		matrix3d.m12 = double25;
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
		double double6 = (double)(quaternionfc.x() * quaternionfc.y());
		double double7 = (double)(quaternionfc.x() * quaternionfc.z());
		double double8 = (double)(quaternionfc.y() * quaternionfc.w());
		double double9 = (double)(quaternionfc.y() * quaternionfc.z());
		double double10 = (double)(quaternionfc.x() * quaternionfc.w());
		double double11 = double1 + double2 - double4 - double3;
		double double12 = double6 + double5 + double5 + double6;
		double double13 = double7 - double8 + double7 - double8;
		double double14 = -double5 + double6 - double5 + double6;
		double double15 = double3 - double4 + double1 - double2;
		double double16 = double9 + double9 + double10 + double10;
		double double17 = double8 + double7 + double7 + double8;
		double double18 = double9 + double9 - double10 - double10;
		double double19 = double4 - double3 - double2 + double1;
		double double20 = this.m00 * double11 + this.m10 * double12 + this.m20 * double13;
		double double21 = this.m01 * double11 + this.m11 * double12 + this.m21 * double13;
		double double22 = this.m02 * double11 + this.m12 * double12 + this.m22 * double13;
		double double23 = this.m00 * double14 + this.m10 * double15 + this.m20 * double16;
		double double24 = this.m01 * double14 + this.m11 * double15 + this.m21 * double16;
		double double25 = this.m02 * double14 + this.m12 * double15 + this.m22 * double16;
		matrix3d.m20 = this.m00 * double17 + this.m10 * double18 + this.m20 * double19;
		matrix3d.m21 = this.m01 * double17 + this.m11 * double18 + this.m21 * double19;
		matrix3d.m22 = this.m02 * double17 + this.m12 * double18 + this.m22 * double19;
		matrix3d.m00 = double20;
		matrix3d.m01 = double21;
		matrix3d.m02 = double22;
		matrix3d.m10 = double23;
		matrix3d.m11 = double24;
		matrix3d.m12 = double25;
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
			vector3d.x = this.m00;
			vector3d.y = this.m10;
			vector3d.z = this.m20;
			break;
		
		case 1: 
			vector3d.x = this.m01;
			vector3d.y = this.m11;
			vector3d.z = this.m21;
			break;
		
		case 2: 
			vector3d.x = this.m02;
			vector3d.y = this.m12;
			vector3d.z = this.m22;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector3d;
	}

	public Matrix3d setRow(int int1, Vector3dc vector3dc) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			this.m00 = vector3dc.x();
			this.m01 = vector3dc.y();
			this.m02 = vector3dc.z();
			break;
		
		case 1: 
			this.m10 = vector3dc.x();
			this.m11 = vector3dc.y();
			this.m12 = vector3dc.z();
			break;
		
		case 2: 
			this.m20 = vector3dc.x();
			this.m21 = vector3dc.y();
			this.m22 = vector3dc.z();
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return this;
	}

	public Vector3d getColumn(int int1, Vector3d vector3d) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			vector3d.x = this.m00;
			vector3d.y = this.m01;
			vector3d.z = this.m02;
			break;
		
		case 1: 
			vector3d.x = this.m10;
			vector3d.y = this.m11;
			vector3d.z = this.m12;
			break;
		
		case 2: 
			vector3d.x = this.m20;
			vector3d.y = this.m21;
			vector3d.z = this.m22;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector3d;
	}

	public Matrix3d setColumn(int int1, Vector3dc vector3dc) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			this.m00 = vector3dc.x();
			this.m01 = vector3dc.y();
			this.m02 = vector3dc.z();
			break;
		
		case 1: 
			this.m10 = vector3dc.x();
			this.m11 = vector3dc.y();
			this.m12 = vector3dc.z();
			break;
		
		case 2: 
			this.m20 = vector3dc.x();
			this.m21 = vector3dc.y();
			this.m22 = vector3dc.z();
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return this;
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

	public Matrix3d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.lookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), this);
	}

	public Matrix3d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix3d matrix3d) {
		return this.lookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), matrix3d);
	}

	public Matrix3d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Matrix3d matrix3d) {
		double double7 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = double1 * double7;
		double double9 = double2 * double7;
		double double10 = double3 * double7;
		double double11 = double9 * double6 - double10 * double5;
		double double12 = double10 * double4 - double8 * double6;
		double double13 = double8 * double5 - double9 * double4;
		double double14 = 1.0 / Math.sqrt(double11 * double11 + double12 * double12 + double13 * double13);
		double11 *= double14;
		double12 *= double14;
		double13 *= double14;
		double double15 = double12 * double10 - double13 * double9;
		double double16 = double13 * double8 - double11 * double10;
		double double17 = double11 * double9 - double12 * double8;
		double double18 = -double8;
		double double19 = -double9;
		double double20 = -double10;
		double double21 = this.m00 * double11 + this.m10 * double15 + this.m20 * double18;
		double double22 = this.m01 * double11 + this.m11 * double15 + this.m21 * double18;
		double double23 = this.m02 * double11 + this.m12 * double15 + this.m22 * double18;
		double double24 = this.m00 * double12 + this.m10 * double16 + this.m20 * double19;
		double double25 = this.m01 * double12 + this.m11 * double16 + this.m21 * double19;
		double double26 = this.m02 * double12 + this.m12 * double16 + this.m22 * double19;
		matrix3d.m20 = this.m00 * double13 + this.m10 * double17 + this.m20 * double20;
		matrix3d.m21 = this.m01 * double13 + this.m11 * double17 + this.m21 * double20;
		matrix3d.m22 = this.m02 * double13 + this.m12 * double17 + this.m22 * double20;
		matrix3d.m00 = double21;
		matrix3d.m01 = double22;
		matrix3d.m02 = double23;
		matrix3d.m10 = double24;
		matrix3d.m11 = double25;
		matrix3d.m12 = double26;
		return matrix3d;
	}

	public Matrix3d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.lookAlong(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix3d setLookAlong(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.setLookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix3d setLookAlong(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = double1 * double7;
		double double9 = double2 * double7;
		double double10 = double3 * double7;
		double double11 = double9 * double6 - double10 * double5;
		double double12 = double10 * double4 - double8 * double6;
		double double13 = double8 * double5 - double9 * double4;
		double double14 = 1.0 / Math.sqrt(double11 * double11 + double12 * double12 + double13 * double13);
		double11 *= double14;
		double12 *= double14;
		double13 *= double14;
		double double15 = double12 * double10 - double13 * double9;
		double double16 = double13 * double8 - double11 * double10;
		double double17 = double11 * double9 - double12 * double8;
		this.m00 = double11;
		this.m01 = double15;
		this.m02 = -double8;
		this.m10 = double12;
		this.m11 = double16;
		this.m12 = -double9;
		this.m20 = double13;
		this.m21 = double17;
		this.m22 = -double10;
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
		vector3d.normalize();
		return vector3d;
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
		vector3d.normalize();
		return vector3d;
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
		vector3d.normalize();
		return vector3d;
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
		matrix3d.m00 = this.m00 + (matrix3dc.m00() - this.m00) * double1;
		matrix3d.m01 = this.m01 + (matrix3dc.m01() - this.m01) * double1;
		matrix3d.m02 = this.m02 + (matrix3dc.m02() - this.m02) * double1;
		matrix3d.m10 = this.m10 + (matrix3dc.m10() - this.m10) * double1;
		matrix3d.m11 = this.m11 + (matrix3dc.m11() - this.m11) * double1;
		matrix3d.m12 = this.m12 + (matrix3dc.m12() - this.m12) * double1;
		matrix3d.m20 = this.m20 + (matrix3dc.m20() - this.m20) * double1;
		matrix3d.m21 = this.m21 + (matrix3dc.m21() - this.m21) * double1;
		matrix3d.m22 = this.m22 + (matrix3dc.m22() - this.m22) * double1;
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
		double double7 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = double1 * double7;
		double double9 = double2 * double7;
		double double10 = double3 * double7;
		double double11 = double5 * double10 - double6 * double9;
		double double12 = double6 * double8 - double4 * double10;
		double double13 = double4 * double9 - double5 * double8;
		double double14 = 1.0 / Math.sqrt(double11 * double11 + double12 * double12 + double13 * double13);
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
		double double7 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = double1 * double7;
		double double9 = double2 * double7;
		double double10 = double3 * double7;
		double double11 = double5 * double10 - double6 * double9;
		double double12 = double6 * double8 - double4 * double10;
		double double13 = double4 * double9 - double5 * double8;
		double double14 = 1.0 / Math.sqrt(double11 * double11 + double12 * double12 + double13 * double13);
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

	public Matrix3dc toImmutable() {
		return (Matrix3dc)(!Options.DEBUG ? this : new Matrix3d.Proxy(this));
	}

	private final class Proxy implements Matrix3dc {
		private final Matrix3dc delegate;

		Proxy(Matrix3dc matrix3dc) {
			this.delegate = matrix3dc;
		}

		public double m00() {
			return this.delegate.m00();
		}

		public double m01() {
			return this.delegate.m01();
		}

		public double m02() {
			return this.delegate.m02();
		}

		public double m10() {
			return this.delegate.m10();
		}

		public double m11() {
			return this.delegate.m11();
		}

		public double m12() {
			return this.delegate.m12();
		}

		public double m20() {
			return this.delegate.m20();
		}

		public double m21() {
			return this.delegate.m21();
		}

		public double m22() {
			return this.delegate.m22();
		}

		public Matrix3d mul(Matrix3dc matrix3dc, Matrix3d matrix3d) {
			return this.delegate.mul(matrix3dc, matrix3d);
		}

		public Matrix3d mul(Matrix3fc matrix3fc, Matrix3d matrix3d) {
			return this.delegate.mul(matrix3fc, matrix3d);
		}

		public double determinant() {
			return this.delegate.determinant();
		}

		public Matrix3d invert(Matrix3d matrix3d) {
			return this.delegate.invert(matrix3d);
		}

		public Matrix3d transpose(Matrix3d matrix3d) {
			return this.delegate.transpose(matrix3d);
		}

		public Matrix3d get(Matrix3d matrix3d) {
			return this.delegate.get(matrix3d);
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

		public DoubleBuffer get(DoubleBuffer doubleBuffer) {
			return this.delegate.get(doubleBuffer);
		}

		public DoubleBuffer get(int int1, DoubleBuffer doubleBuffer) {
			return this.delegate.get(int1, doubleBuffer);
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

		public ByteBuffer getFloats(ByteBuffer byteBuffer) {
			return this.delegate.getFloats(byteBuffer);
		}

		public ByteBuffer getFloats(int int1, ByteBuffer byteBuffer) {
			return this.delegate.getFloats(int1, byteBuffer);
		}

		public double[] get(double[] doubleArray, int int1) {
			return this.delegate.get(doubleArray, int1);
		}

		public double[] get(double[] doubleArray) {
			return this.delegate.get(doubleArray);
		}

		public float[] get(float[] floatArray, int int1) {
			return this.delegate.get(floatArray, int1);
		}

		public float[] get(float[] floatArray) {
			return this.delegate.get(floatArray);
		}

		public Matrix3d scale(Vector3dc vector3dc, Matrix3d matrix3d) {
			return this.delegate.scale(vector3dc, matrix3d);
		}

		public Matrix3d scale(double double1, double double2, double double3, Matrix3d matrix3d) {
			return this.delegate.scale(double1, double2, double3, matrix3d);
		}

		public Matrix3d scale(double double1, Matrix3d matrix3d) {
			return this.delegate.scale(double1, matrix3d);
		}

		public Matrix3d scaleLocal(double double1, double double2, double double3, Matrix3d matrix3d) {
			return this.delegate.scaleLocal(double1, double2, double3, matrix3d);
		}

		public Vector3d transform(Vector3d vector3d) {
			return this.delegate.transform(vector3d);
		}

		public Vector3d transform(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.transform(vector3dc, vector3d);
		}

		public Vector3d transform(double double1, double double2, double double3, Vector3d vector3d) {
			return this.delegate.transform(double1, double2, double3, vector3d);
		}

		public Matrix3d rotateX(double double1, Matrix3d matrix3d) {
			return this.delegate.rotateX(double1, matrix3d);
		}

		public Matrix3d rotateY(double double1, Matrix3d matrix3d) {
			return this.delegate.rotateY(double1, matrix3d);
		}

		public Matrix3d rotateZ(double double1, Matrix3d matrix3d) {
			return this.delegate.rotateZ(double1, matrix3d);
		}

		public Matrix3d rotateXYZ(double double1, double double2, double double3, Matrix3d matrix3d) {
			return this.delegate.rotateXYZ(double1, double2, double3, matrix3d);
		}

		public Matrix3d rotateZYX(double double1, double double2, double double3, Matrix3d matrix3d) {
			return this.delegate.rotateZYX(double1, double2, double3, matrix3d);
		}

		public Matrix3d rotateYXZ(double double1, double double2, double double3, Matrix3d matrix3d) {
			return this.delegate.rotateYXZ(double1, double2, double3, matrix3d);
		}

		public Matrix3d rotate(double double1, double double2, double double3, double double4, Matrix3d matrix3d) {
			return this.delegate.rotate(double1, double2, double3, double4, matrix3d);
		}

		public Matrix3d rotateLocal(double double1, double double2, double double3, double double4, Matrix3d matrix3d) {
			return this.delegate.rotateLocal(double1, double2, double3, double4, matrix3d);
		}

		public Matrix3d rotateLocal(Quaterniondc quaterniondc, Matrix3d matrix3d) {
			return this.delegate.rotateLocal(quaterniondc, matrix3d);
		}

		public Matrix3d rotateLocal(Quaternionfc quaternionfc, Matrix3d matrix3d) {
			return this.delegate.rotateLocal(quaternionfc, matrix3d);
		}

		public Matrix3d rotate(Quaterniondc quaterniondc, Matrix3d matrix3d) {
			return this.delegate.rotate(quaterniondc, matrix3d);
		}

		public Matrix3d rotate(Quaternionfc quaternionfc, Matrix3d matrix3d) {
			return this.delegate.rotate(quaternionfc, matrix3d);
		}

		public Matrix3d rotate(AxisAngle4f axisAngle4f, Matrix3d matrix3d) {
			return this.delegate.rotate(axisAngle4f, matrix3d);
		}

		public Matrix3d rotate(AxisAngle4d axisAngle4d, Matrix3d matrix3d) {
			return this.delegate.rotate(axisAngle4d, matrix3d);
		}

		public Matrix3d rotate(double double1, Vector3dc vector3dc, Matrix3d matrix3d) {
			return this.delegate.rotate(double1, vector3dc, matrix3d);
		}

		public Matrix3d rotate(double double1, Vector3fc vector3fc, Matrix3d matrix3d) {
			return this.delegate.rotate(double1, vector3fc, matrix3d);
		}

		public Vector3d getRow(int int1, Vector3d vector3d) throws IndexOutOfBoundsException {
			return this.delegate.getRow(int1, vector3d);
		}

		public Vector3d getColumn(int int1, Vector3d vector3d) throws IndexOutOfBoundsException {
			return this.delegate.getColumn(int1, vector3d);
		}

		public Matrix3d normal(Matrix3d matrix3d) {
			return this.delegate.normal(matrix3d);
		}

		public Matrix3d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix3d matrix3d) {
			return this.delegate.lookAlong(vector3dc, vector3dc2, matrix3d);
		}

		public Matrix3d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Matrix3d matrix3d) {
			return this.delegate.lookAlong(double1, double2, double3, double4, double5, double6, matrix3d);
		}

		public Vector3d getScale(Vector3d vector3d) {
			return this.delegate.getScale(vector3d);
		}

		public Vector3d positiveZ(Vector3d vector3d) {
			return this.delegate.positiveZ(vector3d);
		}

		public Vector3d normalizedPositiveZ(Vector3d vector3d) {
			return this.delegate.normalizedPositiveZ(vector3d);
		}

		public Vector3d positiveX(Vector3d vector3d) {
			return this.delegate.positiveX(vector3d);
		}

		public Vector3d normalizedPositiveX(Vector3d vector3d) {
			return this.delegate.normalizedPositiveX(vector3d);
		}

		public Vector3d positiveY(Vector3d vector3d) {
			return this.delegate.positiveY(vector3d);
		}

		public Vector3d normalizedPositiveY(Vector3d vector3d) {
			return this.delegate.normalizedPositiveY(vector3d);
		}

		public Matrix3d add(Matrix3dc matrix3dc, Matrix3d matrix3d) {
			return this.delegate.add(matrix3dc, matrix3d);
		}

		public Matrix3d sub(Matrix3dc matrix3dc, Matrix3d matrix3d) {
			return this.delegate.sub(matrix3dc, matrix3d);
		}

		public Matrix3d mulComponentWise(Matrix3dc matrix3dc, Matrix3d matrix3d) {
			return this.delegate.mulComponentWise(matrix3dc, matrix3d);
		}

		public Matrix3d lerp(Matrix3dc matrix3dc, double double1, Matrix3d matrix3d) {
			return this.delegate.lerp(matrix3dc, double1, matrix3d);
		}

		public Matrix3d rotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix3d matrix3d) {
			return this.delegate.rotateTowards(vector3dc, vector3dc2, matrix3d);
		}

		public Matrix3d rotateTowards(double double1, double double2, double double3, double double4, double double5, double double6, Matrix3d matrix3d) {
			return this.delegate.rotateTowards(double1, double2, double3, double4, double5, double6, matrix3d);
		}

		public Vector3d getEulerAnglesZYX(Vector3d vector3d) {
			return this.delegate.getEulerAnglesZYX(vector3d);
		}
	}
}
