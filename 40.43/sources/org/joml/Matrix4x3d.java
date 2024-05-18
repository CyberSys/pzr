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


public class Matrix4x3d implements Externalizable,Matrix4x3dc {
	private static final long serialVersionUID = 1L;
	double m00;
	double m01;
	double m02;
	double m10;
	double m11;
	double m12;
	double m20;
	double m21;
	double m22;
	double m30;
	double m31;
	double m32;
	byte properties;

	public Matrix4x3d() {
		this.m00 = 1.0;
		this.m11 = 1.0;
		this.m22 = 1.0;
		this.properties = 12;
	}

	public Matrix4x3d(Matrix4x3dc matrix4x3dc) {
		this.m00 = matrix4x3dc.m00();
		this.m01 = matrix4x3dc.m01();
		this.m02 = matrix4x3dc.m02();
		this.m10 = matrix4x3dc.m10();
		this.m11 = matrix4x3dc.m11();
		this.m12 = matrix4x3dc.m12();
		this.m20 = matrix4x3dc.m20();
		this.m21 = matrix4x3dc.m21();
		this.m22 = matrix4x3dc.m22();
		this.m30 = matrix4x3dc.m30();
		this.m31 = matrix4x3dc.m31();
		this.m32 = matrix4x3dc.m32();
		this.properties = matrix4x3dc.properties();
	}

	public Matrix4x3d(Matrix4x3fc matrix4x3fc) {
		this.m00 = (double)matrix4x3fc.m00();
		this.m01 = (double)matrix4x3fc.m01();
		this.m02 = (double)matrix4x3fc.m02();
		this.m10 = (double)matrix4x3fc.m10();
		this.m11 = (double)matrix4x3fc.m11();
		this.m12 = (double)matrix4x3fc.m12();
		this.m20 = (double)matrix4x3fc.m20();
		this.m21 = (double)matrix4x3fc.m21();
		this.m22 = (double)matrix4x3fc.m22();
		this.m30 = (double)matrix4x3fc.m30();
		this.m31 = (double)matrix4x3fc.m31();
		this.m32 = (double)matrix4x3fc.m32();
		this.properties = matrix4x3fc.properties();
	}

	public Matrix4x3d(Matrix3dc matrix3dc) {
		this.m00 = matrix3dc.m00();
		this.m01 = matrix3dc.m01();
		this.m02 = matrix3dc.m02();
		this.m10 = matrix3dc.m10();
		this.m11 = matrix3dc.m11();
		this.m12 = matrix3dc.m12();
		this.m20 = matrix3dc.m20();
		this.m21 = matrix3dc.m21();
		this.m22 = matrix3dc.m22();
		this.properties = 0;
	}

	public Matrix4x3d(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12) {
		this.m00 = double1;
		this.m01 = double2;
		this.m02 = double3;
		this.m10 = double4;
		this.m11 = double5;
		this.m12 = double6;
		this.m20 = double7;
		this.m21 = double8;
		this.m22 = double9;
		this.m30 = double10;
		this.m31 = double11;
		this.m32 = double12;
		this.properties = 0;
	}

	public Matrix4x3d(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
	}

	public Matrix4x3d assumeNothing() {
		this.properties = 0;
		return this;
	}

	public byte properties() {
		return this.properties;
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

	public double m30() {
		return this.m30;
	}

	public double m31() {
		return this.m31;
	}

	public double m32() {
		return this.m32;
	}

	public Matrix4x3d m00(double double1) {
		this.m00 = double1;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d m01(double double1) {
		this.m01 = double1;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d m02(double double1) {
		this.m02 = double1;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d m10(double double1) {
		this.m10 = double1;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d m11(double double1) {
		this.m11 = double1;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d m12(double double1) {
		this.m12 = double1;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d m20(double double1) {
		this.m20 = double1;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d m21(double double1) {
		this.m21 = double1;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d m22(double double1) {
		this.m22 = double1;
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d m30(double double1) {
		this.m30 = double1;
		this.properties &= -5;
		return this;
	}

	public Matrix4x3d m31(double double1) {
		this.m31 = double1;
		this.properties &= -5;
		return this;
	}

	public Matrix4x3d m32(double double1) {
		this.m32 = double1;
		this.properties &= -5;
		return this;
	}

	public Matrix4x3d identity() {
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 1.0;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 12;
		return this;
	}

	public Matrix4x3d set(Matrix4x3dc matrix4x3dc) {
		this.m00 = matrix4x3dc.m00();
		this.m01 = matrix4x3dc.m01();
		this.m02 = matrix4x3dc.m02();
		this.m10 = matrix4x3dc.m10();
		this.m11 = matrix4x3dc.m11();
		this.m12 = matrix4x3dc.m12();
		this.m20 = matrix4x3dc.m20();
		this.m21 = matrix4x3dc.m21();
		this.m22 = matrix4x3dc.m22();
		this.m30 = matrix4x3dc.m30();
		this.m31 = matrix4x3dc.m31();
		this.m32 = matrix4x3dc.m32();
		this.properties = matrix4x3dc.properties();
		return this;
	}

	public Matrix4x3d set(Matrix4x3fc matrix4x3fc) {
		this.m00 = (double)matrix4x3fc.m00();
		this.m01 = (double)matrix4x3fc.m01();
		this.m02 = (double)matrix4x3fc.m02();
		this.m10 = (double)matrix4x3fc.m10();
		this.m11 = (double)matrix4x3fc.m11();
		this.m12 = (double)matrix4x3fc.m12();
		this.m20 = (double)matrix4x3fc.m20();
		this.m21 = (double)matrix4x3fc.m21();
		this.m22 = (double)matrix4x3fc.m22();
		this.m30 = (double)matrix4x3fc.m30();
		this.m31 = (double)matrix4x3fc.m31();
		this.m32 = (double)matrix4x3fc.m32();
		this.properties = matrix4x3fc.properties();
		return this;
	}

	public Matrix4x3d set(Matrix4dc matrix4dc) {
		this.m00 = matrix4dc.m00();
		this.m01 = matrix4dc.m01();
		this.m02 = matrix4dc.m02();
		this.m10 = matrix4dc.m10();
		this.m11 = matrix4dc.m11();
		this.m12 = matrix4dc.m12();
		this.m20 = matrix4dc.m20();
		this.m21 = matrix4dc.m21();
		this.m22 = matrix4dc.m22();
		this.m30 = matrix4dc.m30();
		this.m31 = matrix4dc.m31();
		this.m32 = matrix4dc.m32();
		this.properties = (byte)(matrix4dc.properties() & 12);
		return this;
	}

	public Matrix4d get(Matrix4d matrix4d) {
		return matrix4d.set4x3((Matrix4x3dc)this);
	}

	public Matrix4x3d set(Matrix3dc matrix3dc) {
		this.m00 = matrix3dc.m00();
		this.m01 = matrix3dc.m01();
		this.m02 = matrix3dc.m02();
		this.m10 = matrix3dc.m10();
		this.m11 = matrix3dc.m11();
		this.m12 = matrix3dc.m12();
		this.m20 = matrix3dc.m20();
		this.m21 = matrix3dc.m21();
		this.m22 = matrix3dc.m22();
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d set(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4) {
		this.m00 = vector3dc.x();
		this.m01 = vector3dc.y();
		this.m02 = vector3dc.z();
		this.m10 = vector3dc2.x();
		this.m11 = vector3dc2.y();
		this.m12 = vector3dc2.z();
		this.m20 = vector3dc3.x();
		this.m21 = vector3dc3.y();
		this.m22 = vector3dc3.z();
		this.m30 = vector3dc4.x();
		this.m31 = vector3dc4.y();
		this.m32 = vector3dc4.z();
		this.properties = 0;
		return this;
	}

	public Matrix4x3d set3x3(Matrix4x3dc matrix4x3dc) {
		this.m00 = matrix4x3dc.m00();
		this.m01 = matrix4x3dc.m01();
		this.m02 = matrix4x3dc.m02();
		this.m10 = matrix4x3dc.m10();
		this.m11 = matrix4x3dc.m11();
		this.m12 = matrix4x3dc.m12();
		this.m20 = matrix4x3dc.m20();
		this.m21 = matrix4x3dc.m21();
		this.m22 = matrix4x3dc.m22();
		this.properties &= matrix4x3dc.properties();
		return this;
	}

	public Matrix4x3d set(AxisAngle4f axisAngle4f) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d set(AxisAngle4d axisAngle4d) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d set(Quaternionfc quaternionfc) {
		return this.rotation(quaternionfc);
	}

	public Matrix4x3d set(Quaterniondc quaterniondc) {
		return this.rotation(quaterniondc);
	}

	public Matrix4x3d mul(Matrix4x3dc matrix4x3dc) {
		return this.mul(matrix4x3dc, this);
	}

	public Matrix4x3d mul(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.set(matrix4x3dc);
		} else if ((matrix4x3dc.properties() & 4) != 0) {
			return matrix4x3d.set((Matrix4x3dc)this);
		} else {
			return (this.properties & 8) != 0 ? this.mulTranslation(matrix4x3dc, matrix4x3d) : this.mulGeneric(matrix4x3dc, matrix4x3d);
		}
	}

	private Matrix4x3d mulGeneric(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
		double double1 = this.m00 * matrix4x3dc.m00() + this.m10 * matrix4x3dc.m01() + this.m20 * matrix4x3dc.m02();
		double double2 = this.m01 * matrix4x3dc.m00() + this.m11 * matrix4x3dc.m01() + this.m21 * matrix4x3dc.m02();
		double double3 = this.m02 * matrix4x3dc.m00() + this.m12 * matrix4x3dc.m01() + this.m22 * matrix4x3dc.m02();
		double double4 = this.m00 * matrix4x3dc.m10() + this.m10 * matrix4x3dc.m11() + this.m20 * matrix4x3dc.m12();
		double double5 = this.m01 * matrix4x3dc.m10() + this.m11 * matrix4x3dc.m11() + this.m21 * matrix4x3dc.m12();
		double double6 = this.m02 * matrix4x3dc.m10() + this.m12 * matrix4x3dc.m11() + this.m22 * matrix4x3dc.m12();
		double double7 = this.m00 * matrix4x3dc.m20() + this.m10 * matrix4x3dc.m21() + this.m20 * matrix4x3dc.m22();
		double double8 = this.m01 * matrix4x3dc.m20() + this.m11 * matrix4x3dc.m21() + this.m21 * matrix4x3dc.m22();
		double double9 = this.m02 * matrix4x3dc.m20() + this.m12 * matrix4x3dc.m21() + this.m22 * matrix4x3dc.m22();
		double double10 = this.m00 * matrix4x3dc.m30() + this.m10 * matrix4x3dc.m31() + this.m20 * matrix4x3dc.m32() + this.m30;
		double double11 = this.m01 * matrix4x3dc.m30() + this.m11 * matrix4x3dc.m31() + this.m21 * matrix4x3dc.m32() + this.m31;
		double double12 = this.m02 * matrix4x3dc.m30() + this.m12 * matrix4x3dc.m31() + this.m22 * matrix4x3dc.m32() + this.m32;
		matrix4x3d.m00 = double1;
		matrix4x3d.m01 = double2;
		matrix4x3d.m02 = double3;
		matrix4x3d.m10 = double4;
		matrix4x3d.m11 = double5;
		matrix4x3d.m12 = double6;
		matrix4x3d.m20 = double7;
		matrix4x3d.m21 = double8;
		matrix4x3d.m22 = double9;
		matrix4x3d.m30 = double10;
		matrix4x3d.m31 = double11;
		matrix4x3d.m32 = double12;
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d mul(Matrix4x3fc matrix4x3fc) {
		return this.mul(matrix4x3fc, this);
	}

	public Matrix4x3d mul(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.set(matrix4x3fc);
		} else if ((matrix4x3fc.properties() & 4) != 0) {
			return matrix4x3d.set((Matrix4x3dc)this);
		} else {
			return (this.properties & 8) != 0 ? this.mulTranslation(matrix4x3fc, matrix4x3d) : this.mulGeneric(matrix4x3fc, matrix4x3d);
		}
	}

	private Matrix4x3d mulGeneric(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d) {
		double double1 = this.m00 * (double)matrix4x3fc.m00() + this.m10 * (double)matrix4x3fc.m01() + this.m20 * (double)matrix4x3fc.m02();
		double double2 = this.m01 * (double)matrix4x3fc.m00() + this.m11 * (double)matrix4x3fc.m01() + this.m21 * (double)matrix4x3fc.m02();
		double double3 = this.m02 * (double)matrix4x3fc.m00() + this.m12 * (double)matrix4x3fc.m01() + this.m22 * (double)matrix4x3fc.m02();
		double double4 = this.m00 * (double)matrix4x3fc.m10() + this.m10 * (double)matrix4x3fc.m11() + this.m20 * (double)matrix4x3fc.m12();
		double double5 = this.m01 * (double)matrix4x3fc.m10() + this.m11 * (double)matrix4x3fc.m11() + this.m21 * (double)matrix4x3fc.m12();
		double double6 = this.m02 * (double)matrix4x3fc.m10() + this.m12 * (double)matrix4x3fc.m11() + this.m22 * (double)matrix4x3fc.m12();
		double double7 = this.m00 * (double)matrix4x3fc.m20() + this.m10 * (double)matrix4x3fc.m21() + this.m20 * (double)matrix4x3fc.m22();
		double double8 = this.m01 * (double)matrix4x3fc.m20() + this.m11 * (double)matrix4x3fc.m21() + this.m21 * (double)matrix4x3fc.m22();
		double double9 = this.m02 * (double)matrix4x3fc.m20() + this.m12 * (double)matrix4x3fc.m21() + this.m22 * (double)matrix4x3fc.m22();
		double double10 = this.m00 * (double)matrix4x3fc.m30() + this.m10 * (double)matrix4x3fc.m31() + this.m20 * (double)matrix4x3fc.m32() + this.m30;
		double double11 = this.m01 * (double)matrix4x3fc.m30() + this.m11 * (double)matrix4x3fc.m31() + this.m21 * (double)matrix4x3fc.m32() + this.m31;
		double double12 = this.m02 * (double)matrix4x3fc.m30() + this.m12 * (double)matrix4x3fc.m31() + this.m22 * (double)matrix4x3fc.m32() + this.m32;
		matrix4x3d.m00 = double1;
		matrix4x3d.m01 = double2;
		matrix4x3d.m02 = double3;
		matrix4x3d.m10 = double4;
		matrix4x3d.m11 = double5;
		matrix4x3d.m12 = double6;
		matrix4x3d.m20 = double7;
		matrix4x3d.m21 = double8;
		matrix4x3d.m22 = double9;
		matrix4x3d.m30 = double10;
		matrix4x3d.m31 = double11;
		matrix4x3d.m32 = double12;
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d mulTranslation(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
		double double1 = matrix4x3dc.m00();
		double double2 = matrix4x3dc.m01();
		double double3 = matrix4x3dc.m02();
		double double4 = matrix4x3dc.m10();
		double double5 = matrix4x3dc.m11();
		double double6 = matrix4x3dc.m12();
		double double7 = matrix4x3dc.m20();
		double double8 = matrix4x3dc.m21();
		double double9 = matrix4x3dc.m22();
		double double10 = matrix4x3dc.m30() + this.m30;
		double double11 = matrix4x3dc.m31() + this.m31;
		double double12 = matrix4x3dc.m32() + this.m32;
		matrix4x3d.m00 = double1;
		matrix4x3d.m01 = double2;
		matrix4x3d.m02 = double3;
		matrix4x3d.m10 = double4;
		matrix4x3d.m11 = double5;
		matrix4x3d.m12 = double6;
		matrix4x3d.m20 = double7;
		matrix4x3d.m21 = double8;
		matrix4x3d.m22 = double9;
		matrix4x3d.m30 = double10;
		matrix4x3d.m31 = double11;
		matrix4x3d.m32 = double12;
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d mulTranslation(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d) {
		double double1 = (double)matrix4x3fc.m00();
		double double2 = (double)matrix4x3fc.m01();
		double double3 = (double)matrix4x3fc.m02();
		double double4 = (double)matrix4x3fc.m10();
		double double5 = (double)matrix4x3fc.m11();
		double double6 = (double)matrix4x3fc.m12();
		double double7 = (double)matrix4x3fc.m20();
		double double8 = (double)matrix4x3fc.m21();
		double double9 = (double)matrix4x3fc.m22();
		double double10 = (double)matrix4x3fc.m30() + this.m30;
		double double11 = (double)matrix4x3fc.m31() + this.m31;
		double double12 = (double)matrix4x3fc.m32() + this.m32;
		matrix4x3d.m00 = double1;
		matrix4x3d.m01 = double2;
		matrix4x3d.m02 = double3;
		matrix4x3d.m10 = double4;
		matrix4x3d.m11 = double5;
		matrix4x3d.m12 = double6;
		matrix4x3d.m20 = double7;
		matrix4x3d.m21 = double8;
		matrix4x3d.m22 = double9;
		matrix4x3d.m30 = double10;
		matrix4x3d.m31 = double11;
		matrix4x3d.m32 = double12;
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d mulOrtho(Matrix4x3dc matrix4x3dc) {
		return this.mulOrtho(matrix4x3dc, this);
	}

	public Matrix4x3d mulOrtho(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
		double double1 = this.m00 * matrix4x3dc.m00();
		double double2 = this.m11 * matrix4x3dc.m01();
		double double3 = this.m22 * matrix4x3dc.m02();
		double double4 = this.m00 * matrix4x3dc.m10();
		double double5 = this.m11 * matrix4x3dc.m11();
		double double6 = this.m22 * matrix4x3dc.m12();
		double double7 = this.m00 * matrix4x3dc.m20();
		double double8 = this.m11 * matrix4x3dc.m21();
		double double9 = this.m22 * matrix4x3dc.m22();
		double double10 = this.m00 * matrix4x3dc.m30() + this.m30;
		double double11 = this.m11 * matrix4x3dc.m31() + this.m31;
		double double12 = this.m22 * matrix4x3dc.m32() + this.m32;
		matrix4x3d.m00 = double1;
		matrix4x3d.m01 = double2;
		matrix4x3d.m02 = double3;
		matrix4x3d.m10 = double4;
		matrix4x3d.m11 = double5;
		matrix4x3d.m12 = double6;
		matrix4x3d.m20 = double7;
		matrix4x3d.m21 = double8;
		matrix4x3d.m22 = double9;
		matrix4x3d.m30 = double10;
		matrix4x3d.m31 = double11;
		matrix4x3d.m32 = double12;
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d fma(Matrix4x3dc matrix4x3dc, double double1) {
		return this.fma(matrix4x3dc, double1, this);
	}

	public Matrix4x3d fma(Matrix4x3dc matrix4x3dc, double double1, Matrix4x3d matrix4x3d) {
		matrix4x3d.m00 = this.m00 + matrix4x3dc.m00() * double1;
		matrix4x3d.m01 = this.m01 + matrix4x3dc.m01() * double1;
		matrix4x3d.m02 = this.m02 + matrix4x3dc.m02() * double1;
		matrix4x3d.m10 = this.m10 + matrix4x3dc.m10() * double1;
		matrix4x3d.m11 = this.m11 + matrix4x3dc.m11() * double1;
		matrix4x3d.m12 = this.m12 + matrix4x3dc.m12() * double1;
		matrix4x3d.m20 = this.m20 + matrix4x3dc.m20() * double1;
		matrix4x3d.m21 = this.m21 + matrix4x3dc.m21() * double1;
		matrix4x3d.m22 = this.m22 + matrix4x3dc.m22() * double1;
		matrix4x3d.m30 = this.m30 + matrix4x3dc.m30() * double1;
		matrix4x3d.m31 = this.m31 + matrix4x3dc.m31() * double1;
		matrix4x3d.m32 = this.m32 + matrix4x3dc.m32() * double1;
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d fma(Matrix4x3fc matrix4x3fc, double double1) {
		return this.fma(matrix4x3fc, double1, this);
	}

	public Matrix4x3d fma(Matrix4x3fc matrix4x3fc, double double1, Matrix4x3d matrix4x3d) {
		matrix4x3d.m00 = this.m00 + (double)matrix4x3fc.m00() * double1;
		matrix4x3d.m01 = this.m01 + (double)matrix4x3fc.m01() * double1;
		matrix4x3d.m02 = this.m02 + (double)matrix4x3fc.m02() * double1;
		matrix4x3d.m10 = this.m10 + (double)matrix4x3fc.m10() * double1;
		matrix4x3d.m11 = this.m11 + (double)matrix4x3fc.m11() * double1;
		matrix4x3d.m12 = this.m12 + (double)matrix4x3fc.m12() * double1;
		matrix4x3d.m20 = this.m20 + (double)matrix4x3fc.m20() * double1;
		matrix4x3d.m21 = this.m21 + (double)matrix4x3fc.m21() * double1;
		matrix4x3d.m22 = this.m22 + (double)matrix4x3fc.m22() * double1;
		matrix4x3d.m30 = this.m30 + (double)matrix4x3fc.m30() * double1;
		matrix4x3d.m31 = this.m31 + (double)matrix4x3fc.m31() * double1;
		matrix4x3d.m32 = this.m32 + (double)matrix4x3fc.m32() * double1;
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d add(Matrix4x3dc matrix4x3dc) {
		return this.add(matrix4x3dc, this);
	}

	public Matrix4x3d add(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
		matrix4x3d.m00 = this.m00 + matrix4x3dc.m00();
		matrix4x3d.m01 = this.m01 + matrix4x3dc.m01();
		matrix4x3d.m02 = this.m02 + matrix4x3dc.m02();
		matrix4x3d.m10 = this.m10 + matrix4x3dc.m10();
		matrix4x3d.m11 = this.m11 + matrix4x3dc.m11();
		matrix4x3d.m12 = this.m12 + matrix4x3dc.m12();
		matrix4x3d.m20 = this.m20 + matrix4x3dc.m20();
		matrix4x3d.m21 = this.m21 + matrix4x3dc.m21();
		matrix4x3d.m22 = this.m22 + matrix4x3dc.m22();
		matrix4x3d.m30 = this.m30 + matrix4x3dc.m30();
		matrix4x3d.m31 = this.m31 + matrix4x3dc.m31();
		matrix4x3d.m32 = this.m32 + matrix4x3dc.m32();
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d add(Matrix4x3fc matrix4x3fc) {
		return this.add(matrix4x3fc, this);
	}

	public Matrix4x3d add(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d) {
		matrix4x3d.m00 = this.m00 + (double)matrix4x3fc.m00();
		matrix4x3d.m01 = this.m01 + (double)matrix4x3fc.m01();
		matrix4x3d.m02 = this.m02 + (double)matrix4x3fc.m02();
		matrix4x3d.m10 = this.m10 + (double)matrix4x3fc.m10();
		matrix4x3d.m11 = this.m11 + (double)matrix4x3fc.m11();
		matrix4x3d.m12 = this.m12 + (double)matrix4x3fc.m12();
		matrix4x3d.m20 = this.m20 + (double)matrix4x3fc.m20();
		matrix4x3d.m21 = this.m21 + (double)matrix4x3fc.m21();
		matrix4x3d.m22 = this.m22 + (double)matrix4x3fc.m22();
		matrix4x3d.m30 = this.m30 + (double)matrix4x3fc.m30();
		matrix4x3d.m31 = this.m31 + (double)matrix4x3fc.m31();
		matrix4x3d.m32 = this.m32 + (double)matrix4x3fc.m32();
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d sub(Matrix4x3dc matrix4x3dc) {
		return this.sub(matrix4x3dc, this);
	}

	public Matrix4x3d sub(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
		matrix4x3d.m00 = this.m00 - matrix4x3dc.m00();
		matrix4x3d.m01 = this.m01 - matrix4x3dc.m01();
		matrix4x3d.m02 = this.m02 - matrix4x3dc.m02();
		matrix4x3d.m10 = this.m10 - matrix4x3dc.m10();
		matrix4x3d.m11 = this.m11 - matrix4x3dc.m11();
		matrix4x3d.m12 = this.m12 - matrix4x3dc.m12();
		matrix4x3d.m20 = this.m20 - matrix4x3dc.m20();
		matrix4x3d.m21 = this.m21 - matrix4x3dc.m21();
		matrix4x3d.m22 = this.m22 - matrix4x3dc.m22();
		matrix4x3d.m30 = this.m30 - matrix4x3dc.m30();
		matrix4x3d.m31 = this.m31 - matrix4x3dc.m31();
		matrix4x3d.m32 = this.m32 - matrix4x3dc.m32();
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d sub(Matrix4x3fc matrix4x3fc) {
		return this.sub(matrix4x3fc, this);
	}

	public Matrix4x3d sub(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d) {
		matrix4x3d.m00 = this.m00 - (double)matrix4x3fc.m00();
		matrix4x3d.m01 = this.m01 - (double)matrix4x3fc.m01();
		matrix4x3d.m02 = this.m02 - (double)matrix4x3fc.m02();
		matrix4x3d.m10 = this.m10 - (double)matrix4x3fc.m10();
		matrix4x3d.m11 = this.m11 - (double)matrix4x3fc.m11();
		matrix4x3d.m12 = this.m12 - (double)matrix4x3fc.m12();
		matrix4x3d.m20 = this.m20 - (double)matrix4x3fc.m20();
		matrix4x3d.m21 = this.m21 - (double)matrix4x3fc.m21();
		matrix4x3d.m22 = this.m22 - (double)matrix4x3fc.m22();
		matrix4x3d.m30 = this.m30 - (double)matrix4x3fc.m30();
		matrix4x3d.m31 = this.m31 - (double)matrix4x3fc.m31();
		matrix4x3d.m32 = this.m32 - (double)matrix4x3fc.m32();
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d mulComponentWise(Matrix4x3dc matrix4x3dc) {
		return this.mulComponentWise(matrix4x3dc, this);
	}

	public Matrix4x3d mulComponentWise(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
		matrix4x3d.m00 = this.m00 * matrix4x3dc.m00();
		matrix4x3d.m01 = this.m01 * matrix4x3dc.m01();
		matrix4x3d.m02 = this.m02 * matrix4x3dc.m02();
		matrix4x3d.m10 = this.m10 * matrix4x3dc.m10();
		matrix4x3d.m11 = this.m11 * matrix4x3dc.m11();
		matrix4x3d.m12 = this.m12 * matrix4x3dc.m12();
		matrix4x3d.m20 = this.m20 * matrix4x3dc.m20();
		matrix4x3d.m21 = this.m21 * matrix4x3dc.m21();
		matrix4x3d.m22 = this.m22 * matrix4x3dc.m22();
		matrix4x3d.m30 = this.m30 * matrix4x3dc.m30();
		matrix4x3d.m31 = this.m31 * matrix4x3dc.m31();
		matrix4x3d.m32 = this.m32 * matrix4x3dc.m32();
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d set(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12) {
		this.m00 = double1;
		this.m10 = double4;
		this.m20 = double7;
		this.m30 = double10;
		this.m01 = double2;
		this.m11 = double5;
		this.m21 = double8;
		this.m31 = double11;
		this.m02 = double3;
		this.m12 = double6;
		this.m22 = double9;
		this.m32 = double12;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d set(double[] doubleArray, int int1) {
		this.m00 = doubleArray[int1 + 0];
		this.m01 = doubleArray[int1 + 1];
		this.m02 = doubleArray[int1 + 2];
		this.m10 = doubleArray[int1 + 3];
		this.m11 = doubleArray[int1 + 4];
		this.m12 = doubleArray[int1 + 5];
		this.m20 = doubleArray[int1 + 6];
		this.m21 = doubleArray[int1 + 7];
		this.m22 = doubleArray[int1 + 8];
		this.m30 = doubleArray[int1 + 9];
		this.m31 = doubleArray[int1 + 10];
		this.m32 = doubleArray[int1 + 11];
		this.properties = 0;
		return this;
	}

	public Matrix4x3d set(double[] doubleArray) {
		return this.set((double[])doubleArray, 0);
	}

	public Matrix4x3d set(float[] floatArray, int int1) {
		this.m00 = (double)floatArray[int1 + 0];
		this.m01 = (double)floatArray[int1 + 1];
		this.m02 = (double)floatArray[int1 + 2];
		this.m10 = (double)floatArray[int1 + 3];
		this.m11 = (double)floatArray[int1 + 4];
		this.m12 = (double)floatArray[int1 + 5];
		this.m20 = (double)floatArray[int1 + 6];
		this.m21 = (double)floatArray[int1 + 7];
		this.m22 = (double)floatArray[int1 + 8];
		this.m30 = (double)floatArray[int1 + 9];
		this.m31 = (double)floatArray[int1 + 10];
		this.m32 = (double)floatArray[int1 + 11];
		this.properties = 0;
		return this;
	}

	public Matrix4x3d set(float[] floatArray) {
		return this.set((float[])floatArray, 0);
	}

	public Matrix4x3d set(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
		this.properties = 0;
		return this;
	}

	public Matrix4x3d set(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.getf(this, floatBuffer.position(), floatBuffer);
		this.properties = 0;
		return this;
	}

	public Matrix4x3d set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		this.properties = 0;
		return this;
	}

	public Matrix4x3d setFloats(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.getf(this, byteBuffer.position(), byteBuffer);
		this.properties = 0;
		return this;
	}

	public double determinant() {
		return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
	}

	public Matrix4x3d invert() {
		return this.invert(this);
	}

	public Matrix4x3d invert(Matrix4x3d matrix4x3d) {
		return (this.properties & 4) != 0 ? matrix4x3d.identity() : this.invertGeneric(matrix4x3d);
	}

	private Matrix4x3d invertGeneric(Matrix4x3d matrix4x3d) {
		double double1 = this.determinant();
		double1 = 1.0 / double1;
		double double2 = this.m10 * this.m22;
		double double3 = this.m10 * this.m21;
		double double4 = this.m10 * this.m02;
		double double5 = this.m10 * this.m01;
		double double6 = this.m11 * this.m22;
		double double7 = this.m11 * this.m20;
		double double8 = this.m11 * this.m02;
		double double9 = this.m11 * this.m00;
		double double10 = this.m12 * this.m21;
		double double11 = this.m12 * this.m20;
		double double12 = this.m12 * this.m01;
		double double13 = this.m12 * this.m00;
		double double14 = this.m20 * this.m02;
		double double15 = this.m20 * this.m01;
		double double16 = this.m21 * this.m02;
		double double17 = this.m21 * this.m00;
		double double18 = this.m22 * this.m01;
		double double19 = this.m22 * this.m00;
		double double20 = (double6 - double10) * double1;
		double double21 = (double16 - double18) * double1;
		double double22 = (double12 - double8) * double1;
		double double23 = (double11 - double2) * double1;
		double double24 = (double19 - double14) * double1;
		double double25 = (double4 - double13) * double1;
		double double26 = (double3 - double7) * double1;
		double double27 = (double15 - double17) * double1;
		double double28 = (double9 - double5) * double1;
		double double29 = (double2 * this.m31 - double3 * this.m32 + double7 * this.m32 - double6 * this.m30 + double10 * this.m30 - double11 * this.m31) * double1;
		double double30 = (double14 * this.m31 - double15 * this.m32 + double17 * this.m32 - double16 * this.m30 + double18 * this.m30 - double19 * this.m31) * double1;
		double double31 = (double8 * this.m30 - double12 * this.m30 + double13 * this.m31 - double4 * this.m31 + double5 * this.m32 - double9 * this.m32) * double1;
		matrix4x3d.m00 = double20;
		matrix4x3d.m01 = double21;
		matrix4x3d.m02 = double22;
		matrix4x3d.m10 = double23;
		matrix4x3d.m11 = double24;
		matrix4x3d.m12 = double25;
		matrix4x3d.m20 = double26;
		matrix4x3d.m21 = double27;
		matrix4x3d.m22 = double28;
		matrix4x3d.m30 = double29;
		matrix4x3d.m31 = double30;
		matrix4x3d.m32 = double31;
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d invertOrtho(Matrix4x3d matrix4x3d) {
		double double1 = 1.0 / this.m00;
		double double2 = 1.0 / this.m11;
		double double3 = 1.0 / this.m22;
		matrix4x3d.set(double1, 0.0, 0.0, 0.0, double2, 0.0, 0.0, 0.0, double3, -this.m30 * double1, -this.m31 * double2, -this.m32 * double3);
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d invertOrtho() {
		return this.invertOrtho(this);
	}

	public Matrix4x3d invertUnitScale(Matrix4x3d matrix4x3d) {
		matrix4x3d.set(this.m00, this.m10, this.m20, this.m01, this.m11, this.m21, this.m02, this.m12, this.m22, -this.m00 * this.m30 - this.m01 * this.m31 - this.m02 * this.m32, -this.m10 * this.m30 - this.m11 * this.m31 - this.m12 * this.m32, -this.m20 * this.m30 - this.m21 * this.m31 - this.m22 * this.m32);
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d invertUnitScale() {
		return this.invertUnitScale(this);
	}

	public Matrix4x3d transpose3x3() {
		return this.transpose3x3(this);
	}

	public Matrix4x3d transpose3x3(Matrix4x3d matrix4x3d) {
		double double1 = this.m00;
		double double2 = this.m10;
		double double3 = this.m20;
		double double4 = this.m01;
		double double5 = this.m11;
		double double6 = this.m21;
		double double7 = this.m02;
		double double8 = this.m12;
		double double9 = this.m22;
		matrix4x3d.m00 = double1;
		matrix4x3d.m01 = double2;
		matrix4x3d.m02 = double3;
		matrix4x3d.m10 = double4;
		matrix4x3d.m11 = double5;
		matrix4x3d.m12 = double6;
		matrix4x3d.m20 = double7;
		matrix4x3d.m21 = double8;
		matrix4x3d.m22 = double9;
		matrix4x3d.properties = this.properties;
		return matrix4x3d;
	}

	public Matrix3d transpose3x3(Matrix3d matrix3d) {
		matrix3d.m00 = this.m00;
		matrix3d.m01 = this.m10;
		matrix3d.m02 = this.m20;
		matrix3d.m10 = this.m01;
		matrix3d.m11 = this.m11;
		matrix3d.m12 = this.m21;
		matrix3d.m20 = this.m02;
		matrix3d.m21 = this.m12;
		matrix3d.m22 = this.m22;
		return matrix3d;
	}

	public Matrix4x3d translation(double double1, double double2, double double3) {
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 1.0;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
		this.m30 = double1;
		this.m31 = double2;
		this.m32 = double3;
		this.properties = 8;
		return this;
	}

	public Matrix4x3d translation(Vector3fc vector3fc) {
		return this.translation((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Matrix4x3d translation(Vector3dc vector3dc) {
		return this.translation(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4x3d setTranslation(double double1, double double2, double double3) {
		this.m30 = double1;
		this.m31 = double2;
		this.m32 = double3;
		return this;
	}

	public Matrix4x3d setTranslation(Vector3dc vector3dc) {
		return this.setTranslation(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Vector3d getTranslation(Vector3d vector3d) {
		vector3d.x = this.m30;
		vector3d.y = this.m31;
		vector3d.z = this.m32;
		return vector3d;
	}

	public Vector3d getScale(Vector3d vector3d) {
		vector3d.x = Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
		vector3d.y = Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
		vector3d.z = Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
		return vector3d;
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat("  0.000E0; -");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return numberFormat.format(this.m00) + numberFormat.format(this.m10) + numberFormat.format(this.m20) + numberFormat.format(this.m30) + "\n" + numberFormat.format(this.m01) + numberFormat.format(this.m11) + numberFormat.format(this.m21) + numberFormat.format(this.m31) + "\n" + numberFormat.format(this.m02) + numberFormat.format(this.m12) + numberFormat.format(this.m22) + numberFormat.format(this.m32) + "\n";
	}

	public Matrix4x3d get(Matrix4x3d matrix4x3d) {
		return matrix4x3d.set((Matrix4x3dc)this);
	}

	public Quaternionf getUnnormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromUnnormalized((Matrix4x3dc)this);
	}

	public Quaternionf getNormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromNormalized((Matrix4x3dc)this);
	}

	public Quaterniond getUnnormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromUnnormalized((Matrix4x3dc)this);
	}

	public Quaterniond getNormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromNormalized((Matrix4x3dc)this);
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
		doubleArray[int1 + 9] = this.m30;
		doubleArray[int1 + 10] = this.m31;
		doubleArray[int1 + 11] = this.m32;
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
		floatArray[int1 + 9] = (float)this.m30;
		floatArray[int1 + 10] = (float)this.m31;
		floatArray[int1 + 11] = (float)this.m32;
		return floatArray;
	}

	public float[] get(float[] floatArray) {
		return this.get((float[])floatArray, 0);
	}

	public DoubleBuffer get4x4(DoubleBuffer doubleBuffer) {
		return this.get4x4(doubleBuffer.position(), doubleBuffer);
	}

	public DoubleBuffer get4x4(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put4x4(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public ByteBuffer get4x4(ByteBuffer byteBuffer) {
		return this.get4x4(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get4x4(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x4(this, int1, byteBuffer);
		return byteBuffer;
	}

	public DoubleBuffer getTransposed(DoubleBuffer doubleBuffer) {
		return this.getTransposed(doubleBuffer.position(), doubleBuffer);
	}

	public DoubleBuffer getTransposed(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public ByteBuffer getTransposed(ByteBuffer byteBuffer) {
		return this.getTransposed(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer getTransposed(FloatBuffer floatBuffer) {
		return this.getTransposed(floatBuffer.position(), floatBuffer);
	}

	public FloatBuffer getTransposed(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.putfTransposed(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer getTransposedFloats(ByteBuffer byteBuffer) {
		return this.getTransposed(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer getTransposedFloats(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putfTransposed(this, int1, byteBuffer);
		return byteBuffer;
	}

	public double[] getTransposed(double[] doubleArray, int int1) {
		doubleArray[int1 + 0] = this.m00;
		doubleArray[int1 + 1] = this.m10;
		doubleArray[int1 + 2] = this.m20;
		doubleArray[int1 + 3] = this.m30;
		doubleArray[int1 + 4] = this.m01;
		doubleArray[int1 + 5] = this.m11;
		doubleArray[int1 + 6] = this.m21;
		doubleArray[int1 + 7] = this.m31;
		doubleArray[int1 + 8] = this.m02;
		doubleArray[int1 + 9] = this.m12;
		doubleArray[int1 + 10] = this.m22;
		doubleArray[int1 + 11] = this.m32;
		return doubleArray;
	}

	public double[] getTransposed(double[] doubleArray) {
		return this.getTransposed(doubleArray, 0);
	}

	public Matrix4x3d zero() {
		this.m00 = 0.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 0.0;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d scaling(double double1) {
		return this.scaling(double1, double1, double1);
	}

	public Matrix4x3d scaling(double double1, double double2, double double3) {
		this.m00 = double1;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = double2;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = double3;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d scaling(Vector3dc vector3dc) {
		return this.scaling(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4x3d rotation(double double1, double double2, double double3, double double4) {
		double double5 = Math.cos(double1);
		double double6 = Math.sin(double1);
		double double7 = 1.0 - double5;
		double double8 = double2 * double3;
		double double9 = double2 * double4;
		double double10 = double3 * double4;
		this.m00 = double5 + double2 * double2 * double7;
		this.m10 = double8 * double7 - double4 * double6;
		this.m20 = double9 * double7 + double3 * double6;
		this.m30 = 0.0;
		this.m01 = double8 * double7 + double4 * double6;
		this.m11 = double5 + double3 * double3 * double7;
		this.m21 = double10 * double7 - double2 * double6;
		this.m31 = 0.0;
		this.m02 = double9 * double7 - double3 * double6;
		this.m12 = double10 * double7 + double2 * double6;
		this.m22 = double5 + double4 * double4 * double7;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d rotationX(double double1) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d rotationY(double double1) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d rotationZ(double double1) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d rotationXYZ(double double1, double double2, double double3) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d rotationZYX(double double1, double double2, double double3) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d rotationYXZ(double double1, double double2, double double3) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d setRotationXYZ(double double1, double double2, double double3) {
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
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d setRotationZYX(double double1, double double2, double double3) {
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
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d setRotationYXZ(double double1, double double2, double double3) {
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
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d rotation(double double1, Vector3dc vector3dc) {
		return this.rotation(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4x3d rotation(double double1, Vector3fc vector3fc) {
		return this.rotation(double1, (double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Vector4d transform(Vector4d vector4d) {
		return vector4d.mul((Matrix4x3dc)this);
	}

	public Vector4d transform(Vector4dc vector4dc, Vector4d vector4d) {
		return vector4dc.mul((Matrix4x3dc)this, vector4d);
	}

	public Vector3d transformPosition(Vector3d vector3d) {
		vector3d.set(this.m00 * vector3d.x + this.m10 * vector3d.y + this.m20 * vector3d.z + this.m30, this.m01 * vector3d.x + this.m11 * vector3d.y + this.m21 * vector3d.z + this.m31, this.m02 * vector3d.x + this.m12 * vector3d.y + this.m22 * vector3d.z + this.m32);
		return vector3d;
	}

	public Vector3d transformPosition(Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.set(this.m00 * vector3dc.x() + this.m10 * vector3dc.y() + this.m20 * vector3dc.z() + this.m30, this.m01 * vector3dc.x() + this.m11 * vector3dc.y() + this.m21 * vector3dc.z() + this.m31, this.m02 * vector3dc.x() + this.m12 * vector3dc.y() + this.m22 * vector3dc.z() + this.m32);
		return vector3d;
	}

	public Vector3d transformDirection(Vector3d vector3d) {
		vector3d.set(this.m00 * vector3d.x + this.m10 * vector3d.y + this.m20 * vector3d.z, this.m01 * vector3d.x + this.m11 * vector3d.y + this.m21 * vector3d.z, this.m02 * vector3d.x + this.m12 * vector3d.y + this.m22 * vector3d.z);
		return vector3d;
	}

	public Vector3d transformDirection(Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.set(this.m00 * vector3dc.x() + this.m10 * vector3dc.y() + this.m20 * vector3dc.z(), this.m01 * vector3dc.x() + this.m11 * vector3dc.y() + this.m21 * vector3dc.z(), this.m02 * vector3dc.x() + this.m12 * vector3dc.y() + this.m22 * vector3dc.z());
		return vector3d;
	}

	public Matrix4x3d set3x3(Matrix3dc matrix3dc) {
		this.m00 = matrix3dc.m00();
		this.m01 = matrix3dc.m01();
		this.m02 = matrix3dc.m02();
		this.m10 = matrix3dc.m10();
		this.m11 = matrix3dc.m11();
		this.m12 = matrix3dc.m12();
		this.m20 = matrix3dc.m20();
		this.m21 = matrix3dc.m21();
		this.m22 = matrix3dc.m22();
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d set3x3(Matrix3fc matrix3fc) {
		this.m00 = (double)matrix3fc.m00();
		this.m01 = (double)matrix3fc.m01();
		this.m02 = (double)matrix3fc.m02();
		this.m10 = (double)matrix3fc.m10();
		this.m11 = (double)matrix3fc.m11();
		this.m12 = (double)matrix3fc.m12();
		this.m20 = (double)matrix3fc.m20();
		this.m21 = (double)matrix3fc.m21();
		this.m22 = (double)matrix3fc.m22();
		this.properties &= -13;
		return this;
	}

	public Matrix4x3d scale(Vector3dc vector3dc, Matrix4x3d matrix4x3d) {
		return this.scale(vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix4x3d);
	}

	public Matrix4x3d scale(Vector3dc vector3dc) {
		return this.scale(vector3dc.x(), vector3dc.y(), vector3dc.z(), this);
	}

	public Matrix4x3d scale(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
		return (this.properties & 4) != 0 ? matrix4x3d.scaling(double1, double2, double3) : this.scaleGeneric(double1, double2, double3, matrix4x3d);
	}

	private Matrix4x3d scaleGeneric(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
		matrix4x3d.m00 = this.m00 * double1;
		matrix4x3d.m01 = this.m01 * double1;
		matrix4x3d.m02 = this.m02 * double1;
		matrix4x3d.m10 = this.m10 * double2;
		matrix4x3d.m11 = this.m11 * double2;
		matrix4x3d.m12 = this.m12 * double2;
		matrix4x3d.m20 = this.m20 * double3;
		matrix4x3d.m21 = this.m21 * double3;
		matrix4x3d.m22 = this.m22 * double3;
		matrix4x3d.m30 = this.m30;
		matrix4x3d.m31 = this.m31;
		matrix4x3d.m32 = this.m32;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d scale(double double1, double double2, double double3) {
		return this.scale(double1, double2, double3, this);
	}

	public Matrix4x3d scale(double double1, Matrix4x3d matrix4x3d) {
		return this.scale(double1, double1, double1, matrix4x3d);
	}

	public Matrix4x3d scale(double double1) {
		return this.scale(double1, double1, double1);
	}

	public Matrix4x3d scaleLocal(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.scaling(double1, double2, double3);
		} else {
			double double4 = double1 * this.m00;
			double double5 = double2 * this.m01;
			double double6 = double3 * this.m02;
			double double7 = double1 * this.m10;
			double double8 = double2 * this.m11;
			double double9 = double3 * this.m12;
			double double10 = double1 * this.m20;
			double double11 = double2 * this.m21;
			double double12 = double3 * this.m22;
			double double13 = double1 * this.m30;
			double double14 = double2 * this.m31;
			double double15 = double3 * this.m32;
			matrix4x3d.m00 = double4;
			matrix4x3d.m01 = double5;
			matrix4x3d.m02 = double6;
			matrix4x3d.m10 = double7;
			matrix4x3d.m11 = double8;
			matrix4x3d.m12 = double9;
			matrix4x3d.m20 = double10;
			matrix4x3d.m21 = double11;
			matrix4x3d.m22 = double12;
			matrix4x3d.m30 = double13;
			matrix4x3d.m31 = double14;
			matrix4x3d.m32 = double15;
			matrix4x3d.properties = (byte)(this.properties & -13);
			return matrix4x3d;
		}
	}

	public Matrix4x3d scaleLocal(double double1, double double2, double double3) {
		return this.scaleLocal(double1, double2, double3, this);
	}

	public Matrix4x3d rotate(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.rotation(double1, double2, double3, double4);
		} else {
			return (this.properties & 8) != 0 ? this.rotateTranslation(double1, double2, double3, double4, matrix4x3d) : this.rotateGeneric(double1, double2, double3, double4, matrix4x3d);
		}
	}

	private Matrix4x3d rotateGeneric(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
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
		matrix4x3d.m20 = this.m00 * double20 + this.m10 * double21 + this.m20 * double22;
		matrix4x3d.m21 = this.m01 * double20 + this.m11 * double21 + this.m21 * double22;
		matrix4x3d.m22 = this.m02 * double20 + this.m12 * double21 + this.m22 * double22;
		matrix4x3d.m00 = double23;
		matrix4x3d.m01 = double24;
		matrix4x3d.m02 = double25;
		matrix4x3d.m10 = double26;
		matrix4x3d.m11 = double27;
		matrix4x3d.m12 = double28;
		matrix4x3d.m30 = this.m30;
		matrix4x3d.m31 = this.m31;
		matrix4x3d.m32 = this.m32;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d rotate(double double1, double double2, double double3, double double4) {
		return this.rotate(double1, double2, double3, double4, this);
	}

	public Matrix4x3d rotateTranslation(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
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
		matrix4x3d.m20 = double20;
		matrix4x3d.m21 = double21;
		matrix4x3d.m22 = double22;
		matrix4x3d.m00 = double14;
		matrix4x3d.m01 = double15;
		matrix4x3d.m02 = double16;
		matrix4x3d.m10 = double17;
		matrix4x3d.m11 = double18;
		matrix4x3d.m12 = double19;
		matrix4x3d.m30 = this.m30;
		matrix4x3d.m31 = this.m31;
		matrix4x3d.m32 = this.m32;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d rotateLocal(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
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
		double double32 = double14 * this.m30 + double17 * this.m31 + double20 * this.m32;
		double double33 = double15 * this.m30 + double18 * this.m31 + double21 * this.m32;
		double double34 = double16 * this.m30 + double19 * this.m31 + double22 * this.m32;
		matrix4x3d.m00 = double23;
		matrix4x3d.m01 = double24;
		matrix4x3d.m02 = double25;
		matrix4x3d.m10 = double26;
		matrix4x3d.m11 = double27;
		matrix4x3d.m12 = double28;
		matrix4x3d.m20 = double29;
		matrix4x3d.m21 = double30;
		matrix4x3d.m22 = double31;
		matrix4x3d.m30 = double32;
		matrix4x3d.m31 = double33;
		matrix4x3d.m32 = double34;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d rotateLocal(double double1, double double2, double double3, double double4) {
		return this.rotateLocal(double1, double2, double3, double4, this);
	}

	public Matrix4x3d translate(Vector3dc vector3dc) {
		return this.translate(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4x3d translate(Vector3dc vector3dc, Matrix4x3d matrix4x3d) {
		return this.translate(vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix4x3d);
	}

	public Matrix4x3d translate(Vector3fc vector3fc) {
		return this.translate((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Matrix4x3d translate(Vector3fc vector3fc, Matrix4x3d matrix4x3d) {
		return this.translate((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), matrix4x3d);
	}

	public Matrix4x3d translate(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
		return (this.properties & 4) != 0 ? matrix4x3d.translation(double1, double2, double3) : this.translateGeneric(double1, double2, double3, matrix4x3d);
	}

	private Matrix4x3d translateGeneric(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
		matrix4x3d.m00 = this.m00;
		matrix4x3d.m01 = this.m01;
		matrix4x3d.m02 = this.m02;
		matrix4x3d.m10 = this.m10;
		matrix4x3d.m11 = this.m11;
		matrix4x3d.m12 = this.m12;
		matrix4x3d.m20 = this.m20;
		matrix4x3d.m21 = this.m21;
		matrix4x3d.m22 = this.m22;
		matrix4x3d.m30 = this.m00 * double1 + this.m10 * double2 + this.m20 * double3 + this.m30;
		matrix4x3d.m31 = this.m01 * double1 + this.m11 * double2 + this.m21 * double3 + this.m31;
		matrix4x3d.m32 = this.m02 * double1 + this.m12 * double2 + this.m22 * double3 + this.m32;
		matrix4x3d.properties = (byte)(this.properties & -5);
		return matrix4x3d;
	}

	public Matrix4x3d translate(double double1, double double2, double double3) {
		if ((this.properties & 4) != 0) {
			return this.translation(double1, double2, double3);
		} else {
			this.m30 += this.m00 * double1 + this.m10 * double2 + this.m20 * double3;
			this.m31 += this.m01 * double1 + this.m11 * double2 + this.m21 * double3;
			this.m32 += this.m02 * double1 + this.m12 * double2 + this.m22 * double3;
			this.properties &= -5;
			return this;
		}
	}

	public Matrix4x3d translateLocal(Vector3fc vector3fc) {
		return this.translateLocal((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Matrix4x3d translateLocal(Vector3fc vector3fc, Matrix4x3d matrix4x3d) {
		return this.translateLocal((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), matrix4x3d);
	}

	public Matrix4x3d translateLocal(Vector3dc vector3dc) {
		return this.translateLocal(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4x3d translateLocal(Vector3dc vector3dc, Matrix4x3d matrix4x3d) {
		return this.translateLocal(vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix4x3d);
	}

	public Matrix4x3d translateLocal(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
		matrix4x3d.m00 = this.m00;
		matrix4x3d.m01 = this.m01;
		matrix4x3d.m02 = this.m02;
		matrix4x3d.m10 = this.m10;
		matrix4x3d.m11 = this.m11;
		matrix4x3d.m12 = this.m12;
		matrix4x3d.m20 = this.m20;
		matrix4x3d.m21 = this.m21;
		matrix4x3d.m22 = this.m22;
		matrix4x3d.m30 = this.m30 + double1;
		matrix4x3d.m31 = this.m31 + double2;
		matrix4x3d.m32 = this.m32 + double3;
		matrix4x3d.properties = (byte)(this.properties & -5);
		return matrix4x3d;
	}

	public Matrix4x3d translateLocal(double double1, double double2, double double3) {
		return this.translateLocal(double1, double2, double3, this);
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
		objectOutput.writeDouble(this.m30);
		objectOutput.writeDouble(this.m31);
		objectOutput.writeDouble(this.m32);
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
		this.m30 = objectInput.readDouble();
		this.m31 = objectInput.readDouble();
		this.m32 = objectInput.readDouble();
	}

	public Matrix4x3d rotateX(double double1, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.rotationX(double1);
		} else {
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
			matrix4x3d.m20 = this.m10 * double4 + this.m20 * double3;
			matrix4x3d.m21 = this.m11 * double4 + this.m21 * double3;
			matrix4x3d.m22 = this.m12 * double4 + this.m22 * double3;
			matrix4x3d.m10 = double5;
			matrix4x3d.m11 = double6;
			matrix4x3d.m12 = double7;
			matrix4x3d.m00 = this.m00;
			matrix4x3d.m01 = this.m01;
			matrix4x3d.m02 = this.m02;
			matrix4x3d.m30 = this.m30;
			matrix4x3d.m31 = this.m31;
			matrix4x3d.m32 = this.m32;
			matrix4x3d.properties = (byte)(this.properties & -13);
			return matrix4x3d;
		}
	}

	public Matrix4x3d rotateX(double double1) {
		return this.rotateX(double1, this);
	}

	public Matrix4x3d rotateY(double double1, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.rotationY(double1);
		} else {
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
			matrix4x3d.m20 = this.m00 * double2 + this.m20 * double3;
			matrix4x3d.m21 = this.m01 * double2 + this.m21 * double3;
			matrix4x3d.m22 = this.m02 * double2 + this.m22 * double3;
			matrix4x3d.m00 = double5;
			matrix4x3d.m01 = double6;
			matrix4x3d.m02 = double7;
			matrix4x3d.m10 = this.m10;
			matrix4x3d.m11 = this.m11;
			matrix4x3d.m12 = this.m12;
			matrix4x3d.m30 = this.m30;
			matrix4x3d.m31 = this.m31;
			matrix4x3d.m32 = this.m32;
			matrix4x3d.properties = (byte)(this.properties & -13);
			return matrix4x3d;
		}
	}

	public Matrix4x3d rotateY(double double1) {
		return this.rotateY(double1, this);
	}

	public Matrix4x3d rotateZ(double double1, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.rotationZ(double1);
		} else {
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
			matrix4x3d.m10 = this.m00 * double4 + this.m10 * double3;
			matrix4x3d.m11 = this.m01 * double4 + this.m11 * double3;
			matrix4x3d.m12 = this.m02 * double4 + this.m12 * double3;
			matrix4x3d.m00 = double5;
			matrix4x3d.m01 = double6;
			matrix4x3d.m02 = double7;
			matrix4x3d.m20 = this.m20;
			matrix4x3d.m21 = this.m21;
			matrix4x3d.m22 = this.m22;
			matrix4x3d.m30 = this.m30;
			matrix4x3d.m31 = this.m31;
			matrix4x3d.m32 = this.m32;
			matrix4x3d.properties = (byte)(this.properties & -13);
			return matrix4x3d;
		}
	}

	public Matrix4x3d rotateZ(double double1) {
		return this.rotateZ(double1, this);
	}

	public Matrix4x3d rotateXYZ(Vector3d vector3d) {
		return this.rotateXYZ(vector3d.x, vector3d.y, vector3d.z);
	}

	public Matrix4x3d rotateXYZ(double double1, double double2, double double3) {
		return this.rotateXYZ(double1, double2, double3, this);
	}

	public Matrix4x3d rotateXYZ(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.rotationXYZ(double1, double2, double3);
		} else {
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
			matrix4x3d.m20 = this.m00 * double7 + double16 * double6;
			matrix4x3d.m21 = this.m01 * double7 + double17 * double6;
			matrix4x3d.m22 = this.m02 * double7 + double18 * double6;
			matrix4x3d.m00 = double19 * double8 + double13 * double9;
			matrix4x3d.m01 = double20 * double8 + double14 * double9;
			matrix4x3d.m02 = double21 * double8 + double15 * double9;
			matrix4x3d.m10 = double19 * double12 + double13 * double8;
			matrix4x3d.m11 = double20 * double12 + double14 * double8;
			matrix4x3d.m12 = double21 * double12 + double15 * double8;
			matrix4x3d.m30 = this.m30;
			matrix4x3d.m31 = this.m31;
			matrix4x3d.m32 = this.m32;
			matrix4x3d.properties = (byte)(this.properties & -13);
			return matrix4x3d;
		}
	}

	public Matrix4x3d rotateZYX(Vector3d vector3d) {
		return this.rotateZYX(vector3d.z, vector3d.y, vector3d.x);
	}

	public Matrix4x3d rotateZYX(double double1, double double2, double double3) {
		return this.rotateZYX(double1, double2, double3, this);
	}

	public Matrix4x3d rotateZYX(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.rotationZYX(double1, double2, double3);
		} else {
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
			matrix4x3d.m00 = double13 * double6 + this.m20 * double11;
			matrix4x3d.m01 = double14 * double6 + this.m21 * double11;
			matrix4x3d.m02 = double15 * double6 + this.m22 * double11;
			matrix4x3d.m10 = double16 * double8 + double19 * double9;
			matrix4x3d.m11 = double17 * double8 + double20 * double9;
			matrix4x3d.m12 = double18 * double8 + double21 * double9;
			matrix4x3d.m20 = double16 * double12 + double19 * double8;
			matrix4x3d.m21 = double17 * double12 + double20 * double8;
			matrix4x3d.m22 = double18 * double12 + double21 * double8;
			matrix4x3d.m30 = this.m30;
			matrix4x3d.m31 = this.m31;
			matrix4x3d.m32 = this.m32;
			matrix4x3d.properties = (byte)(this.properties & -13);
			return matrix4x3d;
		}
	}

	public Matrix4x3d rotateYXZ(Vector3d vector3d) {
		return this.rotateYXZ(vector3d.y, vector3d.x, vector3d.z);
	}

	public Matrix4x3d rotateYXZ(double double1, double double2, double double3) {
		return this.rotateYXZ(double1, double2, double3, this);
	}

	public Matrix4x3d rotateYXZ(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.rotationYXZ(double1, double2, double3);
		} else {
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
			matrix4x3d.m20 = this.m10 * double11 + double13 * double6;
			matrix4x3d.m21 = this.m11 * double11 + double14 * double6;
			matrix4x3d.m22 = this.m12 * double11 + double15 * double6;
			matrix4x3d.m00 = double16 * double8 + double19 * double9;
			matrix4x3d.m01 = double17 * double8 + double20 * double9;
			matrix4x3d.m02 = double18 * double8 + double21 * double9;
			matrix4x3d.m10 = double16 * double12 + double19 * double8;
			matrix4x3d.m11 = double17 * double12 + double20 * double8;
			matrix4x3d.m12 = double18 * double12 + double21 * double8;
			matrix4x3d.m30 = this.m30;
			matrix4x3d.m31 = this.m31;
			matrix4x3d.m32 = this.m32;
			matrix4x3d.properties = (byte)(this.properties & -13);
			return matrix4x3d;
		}
	}

	public Matrix4x3d rotation(AxisAngle4f axisAngle4f) {
		return this.rotation((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z);
	}

	public Matrix4x3d rotation(AxisAngle4d axisAngle4d) {
		return this.rotation(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z);
	}

	public Matrix4x3d rotation(Quaterniondc quaterniondc) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d rotation(Quaternionfc quaternionfc) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d translationRotateScale(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10) {
		double double11 = double4 + double4;
		double double12 = double5 + double5;
		double double13 = double6 + double6;
		double double14 = double11 * double4;
		double double15 = double12 * double5;
		double double16 = double13 * double6;
		double double17 = double11 * double5;
		double double18 = double11 * double6;
		double double19 = double11 * double7;
		double double20 = double12 * double6;
		double double21 = double12 * double7;
		double double22 = double13 * double7;
		this.m00 = double8 - (double15 + double16) * double8;
		this.m01 = (double17 + double22) * double8;
		this.m02 = (double18 - double21) * double8;
		this.m10 = (double17 - double22) * double9;
		this.m11 = double9 - (double16 + double14) * double9;
		this.m12 = (double20 + double19) * double9;
		this.m20 = (double18 + double21) * double10;
		this.m21 = (double20 - double19) * double10;
		this.m22 = double10 - (double15 + double14) * double10;
		this.m30 = double1;
		this.m31 = double2;
		this.m32 = double3;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d translationRotateScale(Vector3fc vector3fc, Quaternionfc quaternionfc, Vector3fc vector3fc2) {
		return this.translationRotateScale((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), (double)quaternionfc.x(), (double)quaternionfc.y(), (double)quaternionfc.z(), (double)quaternionfc.w(), (double)vector3fc2.x(), (double)vector3fc2.y(), (double)vector3fc2.z());
	}

	public Matrix4x3d translationRotateScale(Vector3dc vector3dc, Quaterniondc quaterniondc, Vector3dc vector3dc2) {
		return this.translationRotateScale(vector3dc.x(), vector3dc.y(), vector3dc.z(), quaterniondc.x(), quaterniondc.y(), quaterniondc.z(), quaterniondc.w(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix4x3d translationRotateScaleMul(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, Matrix4x3dc matrix4x3dc) {
		double double11 = double4 + double4;
		double double12 = double5 + double5;
		double double13 = double6 + double6;
		double double14 = double11 * double4;
		double double15 = double12 * double5;
		double double16 = double13 * double6;
		double double17 = double11 * double5;
		double double18 = double11 * double6;
		double double19 = double11 * double7;
		double double20 = double12 * double6;
		double double21 = double12 * double7;
		double double22 = double13 * double7;
		double double23 = double8 - (double15 + double16) * double8;
		double double24 = (double17 + double22) * double8;
		double double25 = (double18 - double21) * double8;
		double double26 = (double17 - double22) * double9;
		double double27 = double9 - (double16 + double14) * double9;
		double double28 = (double20 + double19) * double9;
		double double29 = (double18 + double21) * double10;
		double double30 = (double20 - double19) * double10;
		double double31 = double10 - (double15 + double14) * double10;
		double double32 = double23 * matrix4x3dc.m00() + double26 * matrix4x3dc.m01() + double29 * matrix4x3dc.m02();
		double double33 = double24 * matrix4x3dc.m00() + double27 * matrix4x3dc.m01() + double30 * matrix4x3dc.m02();
		this.m02 = double25 * matrix4x3dc.m00() + double28 * matrix4x3dc.m01() + double31 * matrix4x3dc.m02();
		this.m00 = double32;
		this.m01 = double33;
		double double34 = double23 * matrix4x3dc.m10() + double26 * matrix4x3dc.m11() + double29 * matrix4x3dc.m12();
		double double35 = double24 * matrix4x3dc.m10() + double27 * matrix4x3dc.m11() + double30 * matrix4x3dc.m12();
		this.m12 = double25 * matrix4x3dc.m10() + double28 * matrix4x3dc.m11() + double31 * matrix4x3dc.m12();
		this.m10 = double34;
		this.m11 = double35;
		double double36 = double23 * matrix4x3dc.m20() + double26 * matrix4x3dc.m21() + double29 * matrix4x3dc.m22();
		double double37 = double24 * matrix4x3dc.m20() + double27 * matrix4x3dc.m21() + double30 * matrix4x3dc.m22();
		this.m22 = double25 * matrix4x3dc.m20() + double28 * matrix4x3dc.m21() + double31 * matrix4x3dc.m22();
		this.m20 = double36;
		this.m21 = double37;
		double double38 = double23 * matrix4x3dc.m30() + double26 * matrix4x3dc.m31() + double29 * matrix4x3dc.m32() + double1;
		double double39 = double24 * matrix4x3dc.m30() + double27 * matrix4x3dc.m31() + double30 * matrix4x3dc.m32() + double2;
		this.m32 = double25 * matrix4x3dc.m30() + double28 * matrix4x3dc.m31() + double31 * matrix4x3dc.m32() + double3;
		this.m30 = double38;
		this.m31 = double39;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d translationRotateScaleMul(Vector3dc vector3dc, Quaterniondc quaterniondc, Vector3dc vector3dc2, Matrix4x3dc matrix4x3dc) {
		return this.translationRotateScaleMul(vector3dc.x(), vector3dc.y(), vector3dc.z(), quaterniondc.x(), quaterniondc.y(), quaterniondc.z(), quaterniondc.w(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), matrix4x3dc);
	}

	public Matrix4x3d translationRotate(double double1, double double2, double double3, Quaterniondc quaterniondc) {
		double double4 = quaterniondc.x() + quaterniondc.x();
		double double5 = quaterniondc.y() + quaterniondc.y();
		double double6 = quaterniondc.z() + quaterniondc.z();
		double double7 = double4 * quaterniondc.x();
		double double8 = double5 * quaterniondc.y();
		double double9 = double6 * quaterniondc.z();
		double double10 = double4 * quaterniondc.y();
		double double11 = double4 * quaterniondc.z();
		double double12 = double4 * quaterniondc.w();
		double double13 = double5 * quaterniondc.z();
		double double14 = double5 * quaterniondc.w();
		double double15 = double6 * quaterniondc.w();
		this.m00 = 1.0 - (double8 + double9);
		this.m01 = double10 + double15;
		this.m02 = double11 - double14;
		this.m10 = double10 - double15;
		this.m11 = 1.0 - (double9 + double7);
		this.m12 = double13 + double12;
		this.m20 = double11 + double14;
		this.m21 = double13 - double12;
		this.m22 = 1.0 - (double8 + double7);
		this.m30 = double1;
		this.m31 = double2;
		this.m32 = double3;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d translationRotateMul(double double1, double double2, double double3, Quaternionfc quaternionfc, Matrix4x3dc matrix4x3dc) {
		return this.translationRotateMul(double1, double2, double3, (double)quaternionfc.x(), (double)quaternionfc.y(), (double)quaternionfc.z(), (double)quaternionfc.w(), matrix4x3dc);
	}

	public Matrix4x3d translationRotateMul(double double1, double double2, double double3, double double4, double double5, double double6, double double7, Matrix4x3dc matrix4x3dc) {
		double double8 = double7 * double7;
		double double9 = double4 * double4;
		double double10 = double5 * double5;
		double double11 = double6 * double6;
		double double12 = double6 * double7;
		double double13 = double4 * double5;
		double double14 = double4 * double6;
		double double15 = double5 * double7;
		double double16 = double5 * double6;
		double double17 = double4 * double7;
		double double18 = double8 + double9 - double11 - double10;
		double double19 = double13 + double12 + double12 + double13;
		double double20 = double14 - double15 + double14 - double15;
		double double21 = -double12 + double13 - double12 + double13;
		double double22 = double10 - double11 + double8 - double9;
		double double23 = double16 + double16 + double17 + double17;
		double double24 = double15 + double14 + double14 + double15;
		double double25 = double16 + double16 - double17 - double17;
		double double26 = double11 - double10 - double9 + double8;
		this.m00 = double18 * matrix4x3dc.m00() + double21 * matrix4x3dc.m01() + double24 * matrix4x3dc.m02();
		this.m01 = double19 * matrix4x3dc.m00() + double22 * matrix4x3dc.m01() + double25 * matrix4x3dc.m02();
		this.m02 = double20 * matrix4x3dc.m00() + double23 * matrix4x3dc.m01() + double26 * matrix4x3dc.m02();
		this.m10 = double18 * matrix4x3dc.m10() + double21 * matrix4x3dc.m11() + double24 * matrix4x3dc.m12();
		this.m11 = double19 * matrix4x3dc.m10() + double22 * matrix4x3dc.m11() + double25 * matrix4x3dc.m12();
		this.m12 = double20 * matrix4x3dc.m10() + double23 * matrix4x3dc.m11() + double26 * matrix4x3dc.m12();
		this.m20 = double18 * matrix4x3dc.m20() + double21 * matrix4x3dc.m21() + double24 * matrix4x3dc.m22();
		this.m21 = double19 * matrix4x3dc.m20() + double22 * matrix4x3dc.m21() + double25 * matrix4x3dc.m22();
		this.m22 = double20 * matrix4x3dc.m20() + double23 * matrix4x3dc.m21() + double26 * matrix4x3dc.m22();
		this.m30 = double18 * matrix4x3dc.m30() + double21 * matrix4x3dc.m31() + double24 * matrix4x3dc.m32() + double1;
		this.m31 = double19 * matrix4x3dc.m30() + double22 * matrix4x3dc.m31() + double25 * matrix4x3dc.m32() + double2;
		this.m32 = double20 * matrix4x3dc.m30() + double23 * matrix4x3dc.m31() + double26 * matrix4x3dc.m32() + double3;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d rotate(Quaterniondc quaterniondc, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.rotation(quaterniondc);
		} else {
			return (this.properties & 8) != 0 ? this.rotateTranslation(quaterniondc, matrix4x3d) : this.rotateGeneric(quaterniondc, matrix4x3d);
		}
	}

	private Matrix4x3d rotateGeneric(Quaterniondc quaterniondc, Matrix4x3d matrix4x3d) {
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
		matrix4x3d.m20 = this.m00 * double17 + this.m10 * double18 + this.m20 * double19;
		matrix4x3d.m21 = this.m01 * double17 + this.m11 * double18 + this.m21 * double19;
		matrix4x3d.m22 = this.m02 * double17 + this.m12 * double18 + this.m22 * double19;
		matrix4x3d.m00 = double20;
		matrix4x3d.m01 = double21;
		matrix4x3d.m02 = double22;
		matrix4x3d.m10 = double23;
		matrix4x3d.m11 = double24;
		matrix4x3d.m12 = double25;
		matrix4x3d.m30 = this.m30;
		matrix4x3d.m31 = this.m31;
		matrix4x3d.m32 = this.m32;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d rotate(Quaternionfc quaternionfc, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.rotation(quaternionfc);
		} else {
			return (this.properties & 8) != 0 ? this.rotateTranslation(quaternionfc, matrix4x3d) : this.rotateGeneric(quaternionfc, matrix4x3d);
		}
	}

	private Matrix4x3d rotateGeneric(Quaternionfc quaternionfc, Matrix4x3d matrix4x3d) {
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
		matrix4x3d.m20 = this.m00 * double17 + this.m10 * double18 + this.m20 * double19;
		matrix4x3d.m21 = this.m01 * double17 + this.m11 * double18 + this.m21 * double19;
		matrix4x3d.m22 = this.m02 * double17 + this.m12 * double18 + this.m22 * double19;
		matrix4x3d.m00 = double20;
		matrix4x3d.m01 = double21;
		matrix4x3d.m02 = double22;
		matrix4x3d.m10 = double23;
		matrix4x3d.m11 = double24;
		matrix4x3d.m12 = double25;
		matrix4x3d.m30 = this.m30;
		matrix4x3d.m31 = this.m31;
		matrix4x3d.m32 = this.m32;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d rotate(Quaterniondc quaterniondc) {
		return this.rotate(quaterniondc, this);
	}

	public Matrix4x3d rotate(Quaternionfc quaternionfc) {
		return this.rotate(quaternionfc, this);
	}

	public Matrix4x3d rotateTranslation(Quaterniondc quaterniondc, Matrix4x3d matrix4x3d) {
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
		matrix4x3d.m20 = double17;
		matrix4x3d.m21 = double18;
		matrix4x3d.m22 = double19;
		matrix4x3d.m00 = double11;
		matrix4x3d.m01 = double12;
		matrix4x3d.m02 = double13;
		matrix4x3d.m10 = double14;
		matrix4x3d.m11 = double15;
		matrix4x3d.m12 = double16;
		matrix4x3d.m30 = this.m30;
		matrix4x3d.m31 = this.m31;
		matrix4x3d.m32 = this.m32;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d rotateTranslation(Quaternionfc quaternionfc, Matrix4x3d matrix4x3d) {
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
		matrix4x3d.m20 = double17;
		matrix4x3d.m21 = double18;
		matrix4x3d.m22 = double19;
		matrix4x3d.m00 = double11;
		matrix4x3d.m01 = double12;
		matrix4x3d.m02 = double13;
		matrix4x3d.m10 = double14;
		matrix4x3d.m11 = double15;
		matrix4x3d.m12 = double16;
		matrix4x3d.m30 = this.m30;
		matrix4x3d.m31 = this.m31;
		matrix4x3d.m32 = this.m32;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d rotateLocal(Quaterniondc quaterniondc, Matrix4x3d matrix4x3d) {
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
		double double29 = double11 * this.m30 + double14 * this.m31 + double17 * this.m32;
		double double30 = double12 * this.m30 + double15 * this.m31 + double18 * this.m32;
		double double31 = double13 * this.m30 + double16 * this.m31 + double19 * this.m32;
		matrix4x3d.m00 = double20;
		matrix4x3d.m01 = double21;
		matrix4x3d.m02 = double22;
		matrix4x3d.m10 = double23;
		matrix4x3d.m11 = double24;
		matrix4x3d.m12 = double25;
		matrix4x3d.m20 = double26;
		matrix4x3d.m21 = double27;
		matrix4x3d.m22 = double28;
		matrix4x3d.m30 = double29;
		matrix4x3d.m31 = double30;
		matrix4x3d.m32 = double31;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d rotateLocal(Quaterniondc quaterniondc) {
		return this.rotateLocal(quaterniondc, this);
	}

	public Matrix4x3d rotateLocal(Quaternionfc quaternionfc, Matrix4x3d matrix4x3d) {
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
		double double29 = double11 * this.m30 + double14 * this.m31 + double17 * this.m32;
		double double30 = double12 * this.m30 + double15 * this.m31 + double18 * this.m32;
		double double31 = double13 * this.m30 + double16 * this.m31 + double19 * this.m32;
		matrix4x3d.m00 = double20;
		matrix4x3d.m01 = double21;
		matrix4x3d.m02 = double22;
		matrix4x3d.m10 = double23;
		matrix4x3d.m11 = double24;
		matrix4x3d.m12 = double25;
		matrix4x3d.m20 = double26;
		matrix4x3d.m21 = double27;
		matrix4x3d.m22 = double28;
		matrix4x3d.m30 = double29;
		matrix4x3d.m31 = double30;
		matrix4x3d.m32 = double31;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d rotateLocal(Quaternionfc quaternionfc) {
		return this.rotateLocal(quaternionfc, this);
	}

	public Matrix4x3d rotate(AxisAngle4f axisAngle4f) {
		return this.rotate((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z);
	}

	public Matrix4x3d rotate(AxisAngle4f axisAngle4f, Matrix4x3d matrix4x3d) {
		return this.rotate((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z, matrix4x3d);
	}

	public Matrix4x3d rotate(AxisAngle4d axisAngle4d) {
		return this.rotate(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z);
	}

	public Matrix4x3d rotate(AxisAngle4d axisAngle4d, Matrix4x3d matrix4x3d) {
		return this.rotate(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z, matrix4x3d);
	}

	public Matrix4x3d rotate(double double1, Vector3dc vector3dc) {
		return this.rotate(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4x3d rotate(double double1, Vector3dc vector3dc, Matrix4x3d matrix4x3d) {
		return this.rotate(double1, vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix4x3d);
	}

	public Matrix4x3d rotate(double double1, Vector3fc vector3fc) {
		return this.rotate(double1, (double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Matrix4x3d rotate(double double1, Vector3fc vector3fc, Matrix4x3d matrix4x3d) {
		return this.rotate(double1, (double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), matrix4x3d);
	}

	public Vector4d getRow(int int1, Vector4d vector4d) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			vector4d.x = this.m00;
			vector4d.y = this.m10;
			vector4d.z = this.m20;
			vector4d.w = this.m30;
			break;
		
		case 1: 
			vector4d.x = this.m01;
			vector4d.y = this.m11;
			vector4d.z = this.m21;
			vector4d.w = this.m31;
			break;
		
		case 2: 
			vector4d.x = this.m02;
			vector4d.y = this.m12;
			vector4d.z = this.m22;
			vector4d.w = this.m32;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector4d;
	}

	public Matrix4x3d setRow(int int1, Vector4dc vector4dc) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			this.m00 = vector4dc.x();
			this.m10 = vector4dc.y();
			this.m20 = vector4dc.z();
			this.m30 = vector4dc.w();
			break;
		
		case 1: 
			this.m01 = vector4dc.x();
			this.m11 = vector4dc.y();
			this.m21 = vector4dc.z();
			this.m31 = vector4dc.w();
			break;
		
		case 2: 
			this.m02 = vector4dc.x();
			this.m12 = vector4dc.y();
			this.m22 = vector4dc.z();
			this.m32 = vector4dc.w();
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
		
		case 3: 
			vector3d.x = this.m30;
			vector3d.y = this.m31;
			vector3d.z = this.m32;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector3d;
	}

	public Matrix4x3d setColumn(int int1, Vector3dc vector3dc) throws IndexOutOfBoundsException {
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
		
		case 3: 
			this.m30 = vector3dc.x();
			this.m31 = vector3dc.y();
			this.m32 = vector3dc.z();
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return this;
	}

	public Matrix4x3d normal() {
		return this.normal(this);
	}

	public Matrix4x3d normal(Matrix4x3d matrix4x3d) {
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
		matrix4x3d.m00 = double9;
		matrix4x3d.m01 = double10;
		matrix4x3d.m02 = double11;
		matrix4x3d.m10 = double12;
		matrix4x3d.m11 = double13;
		matrix4x3d.m12 = double14;
		matrix4x3d.m20 = double15;
		matrix4x3d.m21 = double16;
		matrix4x3d.m22 = double17;
		matrix4x3d.m30 = 0.0;
		matrix4x3d.m31 = 0.0;
		matrix4x3d.m32 = 0.0;
		matrix4x3d.properties = 0;
		return matrix4x3d;
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
		matrix3d.m00 = (this.m11 * this.m22 - this.m21 * this.m12) * double8;
		matrix3d.m01 = (this.m20 * this.m12 - this.m10 * this.m22) * double8;
		matrix3d.m02 = (this.m10 * this.m21 - this.m20 * this.m11) * double8;
		matrix3d.m10 = (this.m21 * this.m02 - this.m01 * this.m22) * double8;
		matrix3d.m11 = (this.m00 * this.m22 - this.m20 * this.m02) * double8;
		matrix3d.m12 = (this.m20 * this.m01 - this.m00 * this.m21) * double8;
		matrix3d.m20 = (double5 - double6) * double8;
		matrix3d.m21 = (double3 - double4) * double8;
		matrix3d.m22 = (double1 - double2) * double8;
		return matrix3d;
	}

	public Matrix4x3d normalize3x3() {
		return this.normalize3x3(this);
	}

	public Matrix4x3d normalize3x3(Matrix4x3d matrix4x3d) {
		double double1 = 1.0 / Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
		double double2 = 1.0 / Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
		double double3 = 1.0 / Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
		matrix4x3d.m00 = this.m00 * double1;
		matrix4x3d.m01 = this.m01 * double1;
		matrix4x3d.m02 = this.m02 * double1;
		matrix4x3d.m10 = this.m10 * double2;
		matrix4x3d.m11 = this.m11 * double2;
		matrix4x3d.m12 = this.m12 * double2;
		matrix4x3d.m20 = this.m20 * double3;
		matrix4x3d.m21 = this.m21 * double3;
		matrix4x3d.m22 = this.m22 * double3;
		return matrix4x3d;
	}

	public Matrix3d normalize3x3(Matrix3d matrix3d) {
		double double1 = 1.0 / Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
		double double2 = 1.0 / Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
		double double3 = 1.0 / Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
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

	public Matrix4x3d reflect(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return matrix4x3d.reflection(double1, double2, double3, double4);
		} else {
			double double5 = double1 + double1;
			double double6 = double2 + double2;
			double double7 = double3 + double3;
			double double8 = double4 + double4;
			double double9 = 1.0 - double5 * double1;
			double double10 = -double5 * double2;
			double double11 = -double5 * double3;
			double double12 = -double6 * double1;
			double double13 = 1.0 - double6 * double2;
			double double14 = -double6 * double3;
			double double15 = -double7 * double1;
			double double16 = -double7 * double2;
			double double17 = 1.0 - double7 * double3;
			double double18 = -double8 * double1;
			double double19 = -double8 * double2;
			double double20 = -double8 * double3;
			matrix4x3d.m30 = this.m00 * double18 + this.m10 * double19 + this.m20 * double20 + this.m30;
			matrix4x3d.m31 = this.m01 * double18 + this.m11 * double19 + this.m21 * double20 + this.m31;
			matrix4x3d.m32 = this.m02 * double18 + this.m12 * double19 + this.m22 * double20 + this.m32;
			double double21 = this.m00 * double9 + this.m10 * double10 + this.m20 * double11;
			double double22 = this.m01 * double9 + this.m11 * double10 + this.m21 * double11;
			double double23 = this.m02 * double9 + this.m12 * double10 + this.m22 * double11;
			double double24 = this.m00 * double12 + this.m10 * double13 + this.m20 * double14;
			double double25 = this.m01 * double12 + this.m11 * double13 + this.m21 * double14;
			double double26 = this.m02 * double12 + this.m12 * double13 + this.m22 * double14;
			matrix4x3d.m20 = this.m00 * double15 + this.m10 * double16 + this.m20 * double17;
			matrix4x3d.m21 = this.m01 * double15 + this.m11 * double16 + this.m21 * double17;
			matrix4x3d.m22 = this.m02 * double15 + this.m12 * double16 + this.m22 * double17;
			matrix4x3d.m00 = double21;
			matrix4x3d.m01 = double22;
			matrix4x3d.m02 = double23;
			matrix4x3d.m10 = double24;
			matrix4x3d.m11 = double25;
			matrix4x3d.m12 = double26;
			matrix4x3d.properties = (byte)(this.properties & -13);
			return matrix4x3d;
		}
	}

	public Matrix4x3d reflect(double double1, double double2, double double3, double double4) {
		return this.reflect(double1, double2, double3, double4, this);
	}

	public Matrix4x3d reflect(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.reflect(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4x3d reflect(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
		double double7 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = double1 * double7;
		double double9 = double2 * double7;
		double double10 = double3 * double7;
		return this.reflect(double8, double9, double10, -double8 * double4 - double9 * double5 - double10 * double6, matrix4x3d);
	}

	public Matrix4x3d reflect(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.reflect(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix4x3d reflect(Quaterniondc quaterniondc, Vector3dc vector3dc) {
		return this.reflect(quaterniondc, vector3dc, this);
	}

	public Matrix4x3d reflect(Quaterniondc quaterniondc, Vector3dc vector3dc, Matrix4x3d matrix4x3d) {
		double double1 = quaterniondc.x() + quaterniondc.x();
		double double2 = quaterniondc.y() + quaterniondc.y();
		double double3 = quaterniondc.z() + quaterniondc.z();
		double double4 = quaterniondc.x() * double3 + quaterniondc.w() * double2;
		double double5 = quaterniondc.y() * double3 - quaterniondc.w() * double1;
		double double6 = 1.0 - (quaterniondc.x() * double1 + quaterniondc.y() * double2);
		return this.reflect(double4, double5, double6, vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix4x3d);
	}

	public Matrix4x3d reflect(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4x3d matrix4x3d) {
		return this.reflect(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), matrix4x3d);
	}

	public Matrix4x3d reflection(double double1, double double2, double double3, double double4) {
		double double5 = double1 + double1;
		double double6 = double2 + double2;
		double double7 = double3 + double3;
		double double8 = double4 + double4;
		this.m00 = 1.0 - double5 * double1;
		this.m01 = -double5 * double2;
		this.m02 = -double5 * double3;
		this.m10 = -double6 * double1;
		this.m11 = 1.0 - double6 * double2;
		this.m12 = -double6 * double3;
		this.m20 = -double7 * double1;
		this.m21 = -double7 * double2;
		this.m22 = 1.0 - double7 * double3;
		this.m30 = -double8 * double1;
		this.m31 = -double8 * double2;
		this.m32 = -double8 * double3;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d reflection(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = double1 * double7;
		double double9 = double2 * double7;
		double double10 = double3 * double7;
		return this.reflection(double8, double9, double10, -double8 * double4 - double9 * double5 - double10 * double6);
	}

	public Matrix4x3d reflection(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.reflection(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix4x3d reflection(Quaterniondc quaterniondc, Vector3dc vector3dc) {
		double double1 = quaterniondc.x() + quaterniondc.x();
		double double2 = quaterniondc.y() + quaterniondc.y();
		double double3 = quaterniondc.z() + quaterniondc.z();
		double double4 = quaterniondc.x() * double3 + quaterniondc.w() * double2;
		double double5 = quaterniondc.y() * double3 - quaterniondc.w() * double1;
		double double6 = 1.0 - (quaterniondc.x() * double1 + quaterniondc.y() * double2);
		return this.reflection(double4, double5, double6, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4x3d ortho(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4x3d matrix4x3d) {
		double double7 = 2.0 / (double2 - double1);
		double double8 = 2.0 / (double4 - double3);
		double double9 = (boolean1 ? 1.0 : 2.0) / (double5 - double6);
		double double10 = (double1 + double2) / (double1 - double2);
		double double11 = (double4 + double3) / (double3 - double4);
		double double12 = (boolean1 ? double5 : double6 + double5) / (double5 - double6);
		matrix4x3d.m30 = this.m00 * double10 + this.m10 * double11 + this.m20 * double12 + this.m30;
		matrix4x3d.m31 = this.m01 * double10 + this.m11 * double11 + this.m21 * double12 + this.m31;
		matrix4x3d.m32 = this.m02 * double10 + this.m12 * double11 + this.m22 * double12 + this.m32;
		matrix4x3d.m00 = this.m00 * double7;
		matrix4x3d.m01 = this.m01 * double7;
		matrix4x3d.m02 = this.m02 * double7;
		matrix4x3d.m10 = this.m10 * double8;
		matrix4x3d.m11 = this.m11 * double8;
		matrix4x3d.m12 = this.m12 * double8;
		matrix4x3d.m20 = this.m20 * double9;
		matrix4x3d.m21 = this.m21 * double9;
		matrix4x3d.m22 = this.m22 * double9;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d ortho(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
		return this.ortho(double1, double2, double3, double4, double5, double6, false, matrix4x3d);
	}

	public Matrix4x3d ortho(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		return this.ortho(double1, double2, double3, double4, double5, double6, boolean1, this);
	}

	public Matrix4x3d ortho(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.ortho(double1, double2, double3, double4, double5, double6, false);
	}

	public Matrix4x3d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4x3d matrix4x3d) {
		double double7 = 2.0 / (double2 - double1);
		double double8 = 2.0 / (double4 - double3);
		double double9 = (boolean1 ? 1.0 : 2.0) / (double6 - double5);
		double double10 = (double1 + double2) / (double1 - double2);
		double double11 = (double4 + double3) / (double3 - double4);
		double double12 = (boolean1 ? double5 : double6 + double5) / (double5 - double6);
		matrix4x3d.m30 = this.m00 * double10 + this.m10 * double11 + this.m20 * double12 + this.m30;
		matrix4x3d.m31 = this.m01 * double10 + this.m11 * double11 + this.m21 * double12 + this.m31;
		matrix4x3d.m32 = this.m02 * double10 + this.m12 * double11 + this.m22 * double12 + this.m32;
		matrix4x3d.m00 = this.m00 * double7;
		matrix4x3d.m01 = this.m01 * double7;
		matrix4x3d.m02 = this.m02 * double7;
		matrix4x3d.m10 = this.m10 * double8;
		matrix4x3d.m11 = this.m11 * double8;
		matrix4x3d.m12 = this.m12 * double8;
		matrix4x3d.m20 = this.m20 * double9;
		matrix4x3d.m21 = this.m21 * double9;
		matrix4x3d.m22 = this.m22 * double9;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
		return this.orthoLH(double1, double2, double3, double4, double5, double6, false, matrix4x3d);
	}

	public Matrix4x3d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		return this.orthoLH(double1, double2, double3, double4, double5, double6, boolean1, this);
	}

	public Matrix4x3d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.orthoLH(double1, double2, double3, double4, double5, double6, false);
	}

	public Matrix4x3d setOrtho(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		this.m00 = 2.0 / (double2 - double1);
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 2.0 / (double4 - double3);
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = (boolean1 ? 1.0 : 2.0) / (double5 - double6);
		this.m30 = (double2 + double1) / (double1 - double2);
		this.m31 = (double4 + double3) / (double3 - double4);
		this.m32 = (boolean1 ? double5 : double6 + double5) / (double5 - double6);
		this.properties = 0;
		return this;
	}

	public Matrix4x3d setOrtho(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.setOrtho(double1, double2, double3, double4, double5, double6, false);
	}

	public Matrix4x3d setOrthoLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		this.m00 = 2.0 / (double2 - double1);
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 2.0 / (double4 - double3);
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = (boolean1 ? 1.0 : 2.0) / (double6 - double5);
		this.m30 = (double2 + double1) / (double1 - double2);
		this.m31 = (double4 + double3) / (double3 - double4);
		this.m32 = (boolean1 ? double5 : double6 + double5) / (double5 - double6);
		this.properties = 0;
		return this;
	}

	public Matrix4x3d setOrthoLH(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.setOrthoLH(double1, double2, double3, double4, double5, double6, false);
	}

	public Matrix4x3d orthoSymmetric(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4x3d matrix4x3d) {
		double double5 = 2.0 / double1;
		double double6 = 2.0 / double2;
		double double7 = (boolean1 ? 1.0 : 2.0) / (double3 - double4);
		double double8 = (boolean1 ? double3 : double4 + double3) / (double3 - double4);
		matrix4x3d.m30 = this.m20 * double8 + this.m30;
		matrix4x3d.m31 = this.m21 * double8 + this.m31;
		matrix4x3d.m32 = this.m22 * double8 + this.m32;
		matrix4x3d.m00 = this.m00 * double5;
		matrix4x3d.m01 = this.m01 * double5;
		matrix4x3d.m02 = this.m02 * double5;
		matrix4x3d.m10 = this.m10 * double6;
		matrix4x3d.m11 = this.m11 * double6;
		matrix4x3d.m12 = this.m12 * double6;
		matrix4x3d.m20 = this.m20 * double7;
		matrix4x3d.m21 = this.m21 * double7;
		matrix4x3d.m22 = this.m22 * double7;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d orthoSymmetric(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
		return this.orthoSymmetric(double1, double2, double3, double4, false, matrix4x3d);
	}

	public Matrix4x3d orthoSymmetric(double double1, double double2, double double3, double double4, boolean boolean1) {
		return this.orthoSymmetric(double1, double2, double3, double4, boolean1, this);
	}

	public Matrix4x3d orthoSymmetric(double double1, double double2, double double3, double double4) {
		return this.orthoSymmetric(double1, double2, double3, double4, false, this);
	}

	public Matrix4x3d orthoSymmetricLH(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4x3d matrix4x3d) {
		double double5 = 2.0 / double1;
		double double6 = 2.0 / double2;
		double double7 = (double)(boolean1 ? 1.0F : 2.0F) / (double4 - double3);
		double double8 = (boolean1 ? double3 : double4 + double3) / (double3 - double4);
		matrix4x3d.m30 = this.m20 * double8 + this.m30;
		matrix4x3d.m31 = this.m21 * double8 + this.m31;
		matrix4x3d.m32 = this.m22 * double8 + this.m32;
		matrix4x3d.m00 = this.m00 * double5;
		matrix4x3d.m01 = this.m01 * double5;
		matrix4x3d.m02 = this.m02 * double5;
		matrix4x3d.m10 = this.m10 * double6;
		matrix4x3d.m11 = this.m11 * double6;
		matrix4x3d.m12 = this.m12 * double6;
		matrix4x3d.m20 = this.m20 * double7;
		matrix4x3d.m21 = this.m21 * double7;
		matrix4x3d.m22 = this.m22 * double7;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d orthoSymmetricLH(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
		return this.orthoSymmetricLH(double1, double2, double3, double4, false, matrix4x3d);
	}

	public Matrix4x3d orthoSymmetricLH(double double1, double double2, double double3, double double4, boolean boolean1) {
		return this.orthoSymmetricLH(double1, double2, double3, double4, boolean1, this);
	}

	public Matrix4x3d orthoSymmetricLH(double double1, double double2, double double3, double double4) {
		return this.orthoSymmetricLH(double1, double2, double3, double4, false, this);
	}

	public Matrix4x3d setOrthoSymmetric(double double1, double double2, double double3, double double4, boolean boolean1) {
		this.m00 = 2.0 / double1;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 2.0 / double2;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = (boolean1 ? 1.0 : 2.0) / (double3 - double4);
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = (boolean1 ? double3 : double4 + double3) / (double3 - double4);
		this.properties = 0;
		return this;
	}

	public Matrix4x3d setOrthoSymmetric(double double1, double double2, double double3, double double4) {
		return this.setOrthoSymmetric(double1, double2, double3, double4, false);
	}

	public Matrix4x3d setOrthoSymmetricLH(double double1, double double2, double double3, double double4, boolean boolean1) {
		this.m00 = 2.0 / double1;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 2.0 / double2;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = (boolean1 ? 1.0 : 2.0) / (double4 - double3);
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = (boolean1 ? double3 : double4 + double3) / (double3 - double4);
		this.properties = 0;
		return this;
	}

	public Matrix4x3d setOrthoSymmetricLH(double double1, double double2, double double3, double double4) {
		return this.setOrthoSymmetricLH(double1, double2, double3, double4, false);
	}

	public Matrix4x3d ortho2D(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
		double double5 = 2.0 / (double2 - double1);
		double double6 = 2.0 / (double4 - double3);
		double double7 = -(double2 + double1) / (double2 - double1);
		double double8 = -(double4 + double3) / (double4 - double3);
		matrix4x3d.m30 = this.m00 * double7 + this.m10 * double8 + this.m30;
		matrix4x3d.m31 = this.m01 * double7 + this.m11 * double8 + this.m31;
		matrix4x3d.m32 = this.m02 * double7 + this.m12 * double8 + this.m32;
		matrix4x3d.m00 = this.m00 * double5;
		matrix4x3d.m01 = this.m01 * double5;
		matrix4x3d.m02 = this.m02 * double5;
		matrix4x3d.m10 = this.m10 * double6;
		matrix4x3d.m11 = this.m11 * double6;
		matrix4x3d.m12 = this.m12 * double6;
		matrix4x3d.m20 = -this.m20;
		matrix4x3d.m21 = -this.m21;
		matrix4x3d.m22 = -this.m22;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d ortho2D(double double1, double double2, double double3, double double4) {
		return this.ortho2D(double1, double2, double3, double4, this);
	}

	public Matrix4x3d ortho2DLH(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
		double double5 = 2.0 / (double2 - double1);
		double double6 = 2.0 / (double4 - double3);
		double double7 = -(double2 + double1) / (double2 - double1);
		double double8 = -(double4 + double3) / (double4 - double3);
		matrix4x3d.m30 = this.m00 * double7 + this.m10 * double8 + this.m30;
		matrix4x3d.m31 = this.m01 * double7 + this.m11 * double8 + this.m31;
		matrix4x3d.m32 = this.m02 * double7 + this.m12 * double8 + this.m32;
		matrix4x3d.m00 = this.m00 * double5;
		matrix4x3d.m01 = this.m01 * double5;
		matrix4x3d.m02 = this.m02 * double5;
		matrix4x3d.m10 = this.m10 * double6;
		matrix4x3d.m11 = this.m11 * double6;
		matrix4x3d.m12 = this.m12 * double6;
		matrix4x3d.m20 = this.m20;
		matrix4x3d.m21 = this.m21;
		matrix4x3d.m22 = this.m22;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d ortho2DLH(double double1, double double2, double double3, double double4) {
		return this.ortho2DLH(double1, double2, double3, double4, this);
	}

	public Matrix4x3d setOrtho2D(double double1, double double2, double double3, double double4) {
		this.m00 = 2.0 / (double2 - double1);
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 2.0 / (double4 - double3);
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = -1.0;
		this.m30 = -(double2 + double1) / (double2 - double1);
		this.m31 = -(double4 + double3) / (double4 - double3);
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d setOrtho2DLH(double double1, double double2, double double3, double double4) {
		this.m00 = 2.0 / (double2 - double1);
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 2.0 / (double4 - double3);
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
		this.m30 = -(double2 + double1) / (double2 - double1);
		this.m31 = -(double4 + double3) / (double4 - double3);
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.lookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), this);
	}

	public Matrix4x3d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4x3d matrix4x3d) {
		return this.lookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), matrix4x3d);
	}

	public Matrix4x3d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
		if ((this.properties & 4) != 0) {
			return this.setLookAlong(double1, double2, double3, double4, double5, double6);
		} else {
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
			matrix4x3d.m20 = this.m00 * double13 + this.m10 * double17 + this.m20 * double20;
			matrix4x3d.m21 = this.m01 * double13 + this.m11 * double17 + this.m21 * double20;
			matrix4x3d.m22 = this.m02 * double13 + this.m12 * double17 + this.m22 * double20;
			matrix4x3d.m00 = double21;
			matrix4x3d.m01 = double22;
			matrix4x3d.m02 = double23;
			matrix4x3d.m10 = double24;
			matrix4x3d.m11 = double25;
			matrix4x3d.m12 = double26;
			matrix4x3d.m30 = this.m30;
			matrix4x3d.m31 = this.m31;
			matrix4x3d.m32 = this.m32;
			matrix4x3d.properties = (byte)(this.properties & -13);
			return matrix4x3d;
		}
	}

	public Matrix4x3d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.lookAlong(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4x3d setLookAlong(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.setLookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix4x3d setLookAlong(double double1, double double2, double double3, double double4, double double5, double double6) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d setLookAt(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		return this.setLookAt(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z());
	}

	public Matrix4x3d setLookAt(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = double1 - double4;
		double double11 = double2 - double5;
		double double12 = double3 - double6;
		double double13 = 1.0 / Math.sqrt(double10 * double10 + double11 * double11 + double12 * double12);
		double10 *= double13;
		double11 *= double13;
		double12 *= double13;
		double double14 = double8 * double12 - double9 * double11;
		double double15 = double9 * double10 - double7 * double12;
		double double16 = double7 * double11 - double8 * double10;
		double double17 = 1.0 / Math.sqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double11 * double16 - double12 * double15;
		double double19 = double12 * double14 - double10 * double16;
		double double20 = double10 * double15 - double11 * double14;
		this.m00 = double14;
		this.m01 = double18;
		this.m02 = double10;
		this.m10 = double15;
		this.m11 = double19;
		this.m12 = double11;
		this.m20 = double16;
		this.m21 = double20;
		this.m22 = double12;
		this.m30 = -(double14 * double1 + double15 * double2 + double16 * double3);
		this.m31 = -(double18 * double1 + double19 * double2 + double20 * double3);
		this.m32 = -(double10 * double1 + double11 * double2 + double12 * double3);
		this.properties = 0;
		return this;
	}

	public Matrix4x3d lookAt(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Matrix4x3d matrix4x3d) {
		return this.lookAt(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), matrix4x3d);
	}

	public Matrix4x3d lookAt(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		return this.lookAt(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), this);
	}

	public Matrix4x3d lookAt(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4x3d matrix4x3d) {
		return (this.properties & 4) != 0 ? matrix4x3d.setLookAt(double1, double2, double3, double4, double5, double6, double7, double8, double9) : this.lookAtGeneric(double1, double2, double3, double4, double5, double6, double7, double8, double9, matrix4x3d);
	}

	private Matrix4x3d lookAtGeneric(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4x3d matrix4x3d) {
		double double10 = double1 - double4;
		double double11 = double2 - double5;
		double double12 = double3 - double6;
		double double13 = 1.0 / Math.sqrt(double10 * double10 + double11 * double11 + double12 * double12);
		double10 *= double13;
		double11 *= double13;
		double12 *= double13;
		double double14 = double8 * double12 - double9 * double11;
		double double15 = double9 * double10 - double7 * double12;
		double double16 = double7 * double11 - double8 * double10;
		double double17 = 1.0 / Math.sqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double11 * double16 - double12 * double15;
		double double19 = double12 * double14 - double10 * double16;
		double double20 = double10 * double15 - double11 * double14;
		double double21 = -(double14 * double1 + double15 * double2 + double16 * double3);
		double double22 = -(double18 * double1 + double19 * double2 + double20 * double3);
		double double23 = -(double10 * double1 + double11 * double2 + double12 * double3);
		matrix4x3d.m30 = this.m00 * double21 + this.m10 * double22 + this.m20 * double23 + this.m30;
		matrix4x3d.m31 = this.m01 * double21 + this.m11 * double22 + this.m21 * double23 + this.m31;
		matrix4x3d.m32 = this.m02 * double21 + this.m12 * double22 + this.m22 * double23 + this.m32;
		double double24 = this.m00 * double14 + this.m10 * double18 + this.m20 * double10;
		double double25 = this.m01 * double14 + this.m11 * double18 + this.m21 * double10;
		double double26 = this.m02 * double14 + this.m12 * double18 + this.m22 * double10;
		double double27 = this.m00 * double15 + this.m10 * double19 + this.m20 * double11;
		double double28 = this.m01 * double15 + this.m11 * double19 + this.m21 * double11;
		double double29 = this.m02 * double15 + this.m12 * double19 + this.m22 * double11;
		matrix4x3d.m20 = this.m00 * double16 + this.m10 * double20 + this.m20 * double12;
		matrix4x3d.m21 = this.m01 * double16 + this.m11 * double20 + this.m21 * double12;
		matrix4x3d.m22 = this.m02 * double16 + this.m12 * double20 + this.m22 * double12;
		matrix4x3d.m00 = double24;
		matrix4x3d.m01 = double25;
		matrix4x3d.m02 = double26;
		matrix4x3d.m10 = double27;
		matrix4x3d.m11 = double28;
		matrix4x3d.m12 = double29;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d lookAt(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		return this.lookAt(double1, double2, double3, double4, double5, double6, double7, double8, double9, this);
	}

	public Matrix4x3d setLookAtLH(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		return this.setLookAtLH(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z());
	}

	public Matrix4x3d setLookAtLH(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = double4 - double1;
		double double11 = double5 - double2;
		double double12 = double6 - double3;
		double double13 = 1.0 / Math.sqrt(double10 * double10 + double11 * double11 + double12 * double12);
		double10 *= double13;
		double11 *= double13;
		double12 *= double13;
		double double14 = double8 * double12 - double9 * double11;
		double double15 = double9 * double10 - double7 * double12;
		double double16 = double7 * double11 - double8 * double10;
		double double17 = 1.0 / Math.sqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double11 * double16 - double12 * double15;
		double double19 = double12 * double14 - double10 * double16;
		double double20 = double10 * double15 - double11 * double14;
		this.m00 = double14;
		this.m01 = double18;
		this.m02 = double10;
		this.m10 = double15;
		this.m11 = double19;
		this.m12 = double11;
		this.m20 = double16;
		this.m21 = double20;
		this.m22 = double12;
		this.m30 = -(double14 * double1 + double15 * double2 + double16 * double3);
		this.m31 = -(double18 * double1 + double19 * double2 + double20 * double3);
		this.m32 = -(double10 * double1 + double11 * double2 + double12 * double3);
		this.properties = 0;
		return this;
	}

	public Matrix4x3d lookAtLH(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Matrix4x3d matrix4x3d) {
		return this.lookAtLH(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), matrix4x3d);
	}

	public Matrix4x3d lookAtLH(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		return this.lookAtLH(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), this);
	}

	public Matrix4x3d lookAtLH(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4x3d matrix4x3d) {
		return (this.properties & 4) != 0 ? matrix4x3d.setLookAtLH(double1, double2, double3, double4, double5, double6, double7, double8, double9) : this.lookAtLHGeneric(double1, double2, double3, double4, double5, double6, double7, double8, double9, matrix4x3d);
	}

	private Matrix4x3d lookAtLHGeneric(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4x3d matrix4x3d) {
		double double10 = double4 - double1;
		double double11 = double5 - double2;
		double double12 = double6 - double3;
		double double13 = 1.0 / Math.sqrt(double10 * double10 + double11 * double11 + double12 * double12);
		double10 *= double13;
		double11 *= double13;
		double12 *= double13;
		double double14 = double8 * double12 - double9 * double11;
		double double15 = double9 * double10 - double7 * double12;
		double double16 = double7 * double11 - double8 * double10;
		double double17 = 1.0 / Math.sqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double11 * double16 - double12 * double15;
		double double19 = double12 * double14 - double10 * double16;
		double double20 = double10 * double15 - double11 * double14;
		double double21 = -(double14 * double1 + double15 * double2 + double16 * double3);
		double double22 = -(double18 * double1 + double19 * double2 + double20 * double3);
		double double23 = -(double10 * double1 + double11 * double2 + double12 * double3);
		matrix4x3d.m30 = this.m00 * double21 + this.m10 * double22 + this.m20 * double23 + this.m30;
		matrix4x3d.m31 = this.m01 * double21 + this.m11 * double22 + this.m21 * double23 + this.m31;
		matrix4x3d.m32 = this.m02 * double21 + this.m12 * double22 + this.m22 * double23 + this.m32;
		double double24 = this.m00 * double14 + this.m10 * double18 + this.m20 * double10;
		double double25 = this.m01 * double14 + this.m11 * double18 + this.m21 * double10;
		double double26 = this.m02 * double14 + this.m12 * double18 + this.m22 * double10;
		double double27 = this.m00 * double15 + this.m10 * double19 + this.m20 * double11;
		double double28 = this.m01 * double15 + this.m11 * double19 + this.m21 * double11;
		double double29 = this.m02 * double15 + this.m12 * double19 + this.m22 * double11;
		matrix4x3d.m20 = this.m00 * double16 + this.m10 * double20 + this.m20 * double12;
		matrix4x3d.m21 = this.m01 * double16 + this.m11 * double20 + this.m21 * double12;
		matrix4x3d.m22 = this.m02 * double16 + this.m12 * double20 + this.m22 * double12;
		matrix4x3d.m00 = double24;
		matrix4x3d.m01 = double25;
		matrix4x3d.m02 = double26;
		matrix4x3d.m10 = double27;
		matrix4x3d.m11 = double28;
		matrix4x3d.m12 = double29;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d lookAtLH(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		return this.lookAtLH(double1, double2, double3, double4, double5, double6, double7, double8, double9, this);
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

	public Vector3d origin(Vector3d vector3d) {
		double double1 = this.m00 * this.m11 - this.m01 * this.m10;
		double double2 = this.m00 * this.m12 - this.m02 * this.m10;
		double double3 = this.m01 * this.m12 - this.m02 * this.m11;
		double double4 = this.m20 * this.m31 - this.m21 * this.m30;
		double double5 = this.m20 * this.m32 - this.m22 * this.m30;
		double double6 = this.m21 * this.m32 - this.m22 * this.m31;
		vector3d.x = -this.m10 * double6 + this.m11 * double5 - this.m12 * double4;
		vector3d.y = this.m00 * double6 - this.m01 * double5 + this.m02 * double4;
		vector3d.z = -this.m30 * double3 + this.m31 * double2 - this.m32 * double1;
		return vector3d;
	}

	public Matrix4x3d shadow(Vector4dc vector4dc, double double1, double double2, double double3, double double4) {
		return this.shadow(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4dc.w(), double1, double2, double3, double4, this);
	}

	public Matrix4x3d shadow(Vector4dc vector4dc, double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
		return this.shadow(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4dc.w(), double1, double2, double3, double4, matrix4x3d);
	}

	public Matrix4x3d shadow(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		return this.shadow(double1, double2, double3, double4, double5, double6, double7, double8, this);
	}

	public Matrix4x3d shadow(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Matrix4x3d matrix4x3d) {
		double double9 = 1.0 / Math.sqrt(double5 * double5 + double6 * double6 + double7 * double7);
		double double10 = double5 * double9;
		double double11 = double6 * double9;
		double double12 = double7 * double9;
		double double13 = double8 * double9;
		double double14 = double10 * double1 + double11 * double2 + double12 * double3 + double13 * double4;
		double double15 = double14 - double10 * double1;
		double double16 = -double10 * double2;
		double double17 = -double10 * double3;
		double double18 = -double10 * double4;
		double double19 = -double11 * double1;
		double double20 = double14 - double11 * double2;
		double double21 = -double11 * double3;
		double double22 = -double11 * double4;
		double double23 = -double12 * double1;
		double double24 = -double12 * double2;
		double double25 = double14 - double12 * double3;
		double double26 = -double12 * double4;
		double double27 = -double13 * double1;
		double double28 = -double13 * double2;
		double double29 = -double13 * double3;
		double double30 = double14 - double13 * double4;
		double double31 = this.m00 * double15 + this.m10 * double16 + this.m20 * double17 + this.m30 * double18;
		double double32 = this.m01 * double15 + this.m11 * double16 + this.m21 * double17 + this.m31 * double18;
		double double33 = this.m02 * double15 + this.m12 * double16 + this.m22 * double17 + this.m32 * double18;
		double double34 = this.m00 * double19 + this.m10 * double20 + this.m20 * double21 + this.m30 * double22;
		double double35 = this.m01 * double19 + this.m11 * double20 + this.m21 * double21 + this.m31 * double22;
		double double36 = this.m02 * double19 + this.m12 * double20 + this.m22 * double21 + this.m32 * double22;
		double double37 = this.m00 * double23 + this.m10 * double24 + this.m20 * double25 + this.m30 * double26;
		double double38 = this.m01 * double23 + this.m11 * double24 + this.m21 * double25 + this.m31 * double26;
		double double39 = this.m02 * double23 + this.m12 * double24 + this.m22 * double25 + this.m32 * double26;
		matrix4x3d.m30 = this.m00 * double27 + this.m10 * double28 + this.m20 * double29 + this.m30 * double30;
		matrix4x3d.m31 = this.m01 * double27 + this.m11 * double28 + this.m21 * double29 + this.m31 * double30;
		matrix4x3d.m32 = this.m02 * double27 + this.m12 * double28 + this.m22 * double29 + this.m32 * double30;
		matrix4x3d.m00 = double31;
		matrix4x3d.m01 = double32;
		matrix4x3d.m02 = double33;
		matrix4x3d.m10 = double34;
		matrix4x3d.m11 = double35;
		matrix4x3d.m12 = double36;
		matrix4x3d.m20 = double37;
		matrix4x3d.m21 = double38;
		matrix4x3d.m22 = double39;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d shadow(Vector4dc vector4dc, Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
		double double1 = matrix4x3dc.m10();
		double double2 = matrix4x3dc.m11();
		double double3 = matrix4x3dc.m12();
		double double4 = -double1 * matrix4x3dc.m30() - double2 * matrix4x3dc.m31() - double3 * matrix4x3dc.m32();
		return this.shadow(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4dc.w(), double1, double2, double3, double4, matrix4x3d);
	}

	public Matrix4x3d shadow(Vector4dc vector4dc, Matrix4x3dc matrix4x3dc) {
		return this.shadow(vector4dc, matrix4x3dc, this);
	}

	public Matrix4x3d shadow(double double1, double double2, double double3, double double4, Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
		double double5 = matrix4x3dc.m10();
		double double6 = matrix4x3dc.m11();
		double double7 = matrix4x3dc.m12();
		double double8 = -double5 * matrix4x3dc.m30() - double6 * matrix4x3dc.m31() - double7 * matrix4x3dc.m32();
		return this.shadow(double1, double2, double3, double4, double5, double6, double7, double8, matrix4x3d);
	}

	public Matrix4x3d shadow(double double1, double double2, double double3, double double4, Matrix4x3dc matrix4x3dc) {
		return this.shadow(double1, double2, double3, double4, matrix4x3dc, this);
	}

	public Matrix4x3d billboardCylindrical(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		double double1 = vector3dc2.x() - vector3dc.x();
		double double2 = vector3dc2.y() - vector3dc.y();
		double double3 = vector3dc2.z() - vector3dc.z();
		double double4 = vector3dc3.y() * double3 - vector3dc3.z() * double2;
		double double5 = vector3dc3.z() * double1 - vector3dc3.x() * double3;
		double double6 = vector3dc3.x() * double2 - vector3dc3.y() * double1;
		double double7 = 1.0 / Math.sqrt(double4 * double4 + double5 * double5 + double6 * double6);
		double4 *= double7;
		double5 *= double7;
		double6 *= double7;
		double1 = double5 * vector3dc3.z() - double6 * vector3dc3.y();
		double2 = double6 * vector3dc3.x() - double4 * vector3dc3.z();
		double3 = double4 * vector3dc3.y() - double5 * vector3dc3.x();
		double double8 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double1 *= double8;
		double2 *= double8;
		double3 *= double8;
		this.m00 = double4;
		this.m01 = double5;
		this.m02 = double6;
		this.m10 = vector3dc3.x();
		this.m11 = vector3dc3.y();
		this.m12 = vector3dc3.z();
		this.m20 = double1;
		this.m21 = double2;
		this.m22 = double3;
		this.m30 = vector3dc.x();
		this.m31 = vector3dc.y();
		this.m32 = vector3dc.z();
		this.properties = 0;
		return this;
	}

	public Matrix4x3d billboardSpherical(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		double double1 = vector3dc2.x() - vector3dc.x();
		double double2 = vector3dc2.y() - vector3dc.y();
		double double3 = vector3dc2.z() - vector3dc.z();
		double double4 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double1 *= double4;
		double2 *= double4;
		double3 *= double4;
		double double5 = vector3dc3.y() * double3 - vector3dc3.z() * double2;
		double double6 = vector3dc3.z() * double1 - vector3dc3.x() * double3;
		double double7 = vector3dc3.x() * double2 - vector3dc3.y() * double1;
		double double8 = 1.0 / Math.sqrt(double5 * double5 + double6 * double6 + double7 * double7);
		double5 *= double8;
		double6 *= double8;
		double7 *= double8;
		double double9 = double2 * double7 - double3 * double6;
		double double10 = double3 * double5 - double1 * double7;
		double double11 = double1 * double6 - double2 * double5;
		this.m00 = double5;
		this.m01 = double6;
		this.m02 = double7;
		this.m10 = double9;
		this.m11 = double10;
		this.m12 = double11;
		this.m20 = double1;
		this.m21 = double2;
		this.m22 = double3;
		this.m30 = vector3dc.x();
		this.m31 = vector3dc.y();
		this.m32 = vector3dc.z();
		this.properties = 0;
		return this;
	}

	public Matrix4x3d billboardSpherical(Vector3dc vector3dc, Vector3dc vector3dc2) {
		double double1 = vector3dc2.x() - vector3dc.x();
		double double2 = vector3dc2.y() - vector3dc.y();
		double double3 = vector3dc2.z() - vector3dc.z();
		double double4 = -double2;
		double double5 = Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3) + double3;
		double double6 = 1.0 / Math.sqrt(double4 * double4 + double1 * double1 + double5 * double5);
		double4 *= double6;
		double double7 = double1 * double6;
		double5 *= double6;
		double double8 = (double4 + double4) * double4;
		double double9 = (double7 + double7) * double7;
		double double10 = (double4 + double4) * double7;
		double double11 = (double4 + double4) * double5;
		double double12 = (double7 + double7) * double5;
		this.m00 = 1.0 - double9;
		this.m01 = double10;
		this.m02 = -double12;
		this.m10 = double10;
		this.m11 = 1.0 - double8;
		this.m12 = double11;
		this.m20 = double12;
		this.m21 = -double11;
		this.m22 = 1.0 - double9 - double8;
		this.m30 = vector3dc.x();
		this.m31 = vector3dc.y();
		this.m32 = vector3dc.z();
		this.properties = 0;
		return this;
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
		long1 = Double.doubleToLongBits(this.m30);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m31);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m32);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		return int1;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object == null) {
			return false;
		} else if (!(object instanceof Matrix4x3d)) {
			return false;
		} else {
			Matrix4x3d matrix4x3d = (Matrix4x3d)object;
			if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(matrix4x3d.m00)) {
				return false;
			} else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(matrix4x3d.m01)) {
				return false;
			} else if (Double.doubleToLongBits(this.m02) != Double.doubleToLongBits(matrix4x3d.m02)) {
				return false;
			} else if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(matrix4x3d.m10)) {
				return false;
			} else if (Double.doubleToLongBits(this.m11) != Double.doubleToLongBits(matrix4x3d.m11)) {
				return false;
			} else if (Double.doubleToLongBits(this.m12) != Double.doubleToLongBits(matrix4x3d.m12)) {
				return false;
			} else if (Double.doubleToLongBits(this.m20) != Double.doubleToLongBits(matrix4x3d.m20)) {
				return false;
			} else if (Double.doubleToLongBits(this.m21) != Double.doubleToLongBits(matrix4x3d.m21)) {
				return false;
			} else if (Double.doubleToLongBits(this.m22) != Double.doubleToLongBits(matrix4x3d.m22)) {
				return false;
			} else if (Double.doubleToLongBits(this.m30) != Double.doubleToLongBits(matrix4x3d.m30)) {
				return false;
			} else if (Double.doubleToLongBits(this.m31) != Double.doubleToLongBits(matrix4x3d.m31)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.m32) == Double.doubleToLongBits(matrix4x3d.m32);
			}
		}
	}

	public Matrix4x3d pick(double double1, double double2, double double3, double double4, int[] intArray, Matrix4x3d matrix4x3d) {
		double double5 = (double)intArray[2] / double3;
		double double6 = (double)intArray[3] / double4;
		double double7 = ((double)intArray[2] + 2.0 * ((double)intArray[0] - double1)) / double3;
		double double8 = ((double)intArray[3] + 2.0 * ((double)intArray[1] - double2)) / double4;
		matrix4x3d.m30 = this.m00 * double7 + this.m10 * double8 + this.m30;
		matrix4x3d.m31 = this.m01 * double7 + this.m11 * double8 + this.m31;
		matrix4x3d.m32 = this.m02 * double7 + this.m12 * double8 + this.m32;
		matrix4x3d.m00 = this.m00 * double5;
		matrix4x3d.m01 = this.m01 * double5;
		matrix4x3d.m02 = this.m02 * double5;
		matrix4x3d.m10 = this.m10 * double6;
		matrix4x3d.m11 = this.m11 * double6;
		matrix4x3d.m12 = this.m12 * double6;
		matrix4x3d.properties = 0;
		return matrix4x3d;
	}

	public Matrix4x3d pick(double double1, double double2, double double3, double double4, int[] intArray) {
		return this.pick(double1, double2, double3, double4, intArray, this);
	}

	public Matrix4x3d swap(Matrix4x3d matrix4x3d) {
		double double1 = this.m00;
		this.m00 = matrix4x3d.m00;
		matrix4x3d.m00 = double1;
		double1 = this.m01;
		this.m01 = matrix4x3d.m01;
		matrix4x3d.m01 = double1;
		double1 = this.m02;
		this.m02 = matrix4x3d.m02;
		matrix4x3d.m02 = double1;
		double1 = this.m10;
		this.m10 = matrix4x3d.m10;
		matrix4x3d.m10 = double1;
		double1 = this.m11;
		this.m11 = matrix4x3d.m11;
		matrix4x3d.m11 = double1;
		double1 = this.m12;
		this.m12 = matrix4x3d.m12;
		matrix4x3d.m12 = double1;
		double1 = this.m20;
		this.m20 = matrix4x3d.m20;
		matrix4x3d.m20 = double1;
		double1 = this.m21;
		this.m21 = matrix4x3d.m21;
		matrix4x3d.m21 = double1;
		double1 = this.m22;
		this.m22 = matrix4x3d.m22;
		matrix4x3d.m22 = double1;
		double1 = this.m30;
		this.m30 = matrix4x3d.m30;
		matrix4x3d.m30 = double1;
		double1 = this.m31;
		this.m31 = matrix4x3d.m31;
		matrix4x3d.m31 = double1;
		double1 = this.m32;
		this.m32 = matrix4x3d.m32;
		matrix4x3d.m32 = double1;
		byte byte1 = this.properties;
		this.properties = matrix4x3d.properties;
		matrix4x3d.properties = byte1;
		return this;
	}

	public Matrix4x3d arcball(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
		double double7 = this.m20 * -double1 + this.m30;
		double double8 = this.m21 * -double1 + this.m31;
		double double9 = this.m22 * -double1 + this.m32;
		double double10 = Math.cos(double5);
		double double11 = Math.sin(double5);
		double double12 = this.m10 * double10 + this.m20 * double11;
		double double13 = this.m11 * double10 + this.m21 * double11;
		double double14 = this.m12 * double10 + this.m22 * double11;
		double double15 = this.m20 * double10 - this.m10 * double11;
		double double16 = this.m21 * double10 - this.m11 * double11;
		double double17 = this.m22 * double10 - this.m12 * double11;
		double10 = Math.cos(double6);
		double11 = Math.sin(double6);
		double double18 = this.m00 * double10 - double15 * double11;
		double double19 = this.m01 * double10 - double16 * double11;
		double double20 = this.m02 * double10 - double17 * double11;
		double double21 = this.m00 * double11 + double15 * double10;
		double double22 = this.m01 * double11 + double16 * double10;
		double double23 = this.m02 * double11 + double17 * double10;
		matrix4x3d.m30 = -double18 * double2 - double12 * double3 - double21 * double4 + double7;
		matrix4x3d.m31 = -double19 * double2 - double13 * double3 - double22 * double4 + double8;
		matrix4x3d.m32 = -double20 * double2 - double14 * double3 - double23 * double4 + double9;
		matrix4x3d.m20 = double21;
		matrix4x3d.m21 = double22;
		matrix4x3d.m22 = double23;
		matrix4x3d.m10 = double12;
		matrix4x3d.m11 = double13;
		matrix4x3d.m12 = double14;
		matrix4x3d.m00 = double18;
		matrix4x3d.m01 = double19;
		matrix4x3d.m02 = double20;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d arcball(double double1, Vector3dc vector3dc, double double2, double double3, Matrix4x3d matrix4x3d) {
		return this.arcball(double1, vector3dc.x(), vector3dc.y(), vector3dc.z(), double2, double3, matrix4x3d);
	}

	public Matrix4x3d arcball(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.arcball(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4x3d arcball(double double1, Vector3dc vector3dc, double double2, double double3) {
		return this.arcball(double1, vector3dc.x(), vector3dc.y(), vector3dc.z(), double2, double3, this);
	}

	public Matrix4x3d transformAab(double double1, double double2, double double3, double double4, double double5, double double6, Vector3d vector3d, Vector3d vector3d2) {
		double double7 = this.m00 * double1;
		double double8 = this.m01 * double1;
		double double9 = this.m02 * double1;
		double double10 = this.m00 * double4;
		double double11 = this.m01 * double4;
		double double12 = this.m02 * double4;
		double double13 = this.m10 * double2;
		double double14 = this.m11 * double2;
		double double15 = this.m12 * double2;
		double double16 = this.m10 * double5;
		double double17 = this.m11 * double5;
		double double18 = this.m12 * double5;
		double double19 = this.m20 * double3;
		double double20 = this.m21 * double3;
		double double21 = this.m22 * double3;
		double double22 = this.m20 * double6;
		double double23 = this.m21 * double6;
		double double24 = this.m22 * double6;
		double double25;
		double double26;
		if (double7 < double10) {
			double26 = double7;
			double25 = double10;
		} else {
			double26 = double10;
			double25 = double7;
		}

		double double27;
		double double28;
		if (double8 < double11) {
			double28 = double8;
			double27 = double11;
		} else {
			double28 = double11;
			double27 = double8;
		}

		double double29;
		double double30;
		if (double9 < double12) {
			double30 = double9;
			double29 = double12;
		} else {
			double30 = double12;
			double29 = double9;
		}

		double double31;
		double double32;
		if (double13 < double16) {
			double32 = double13;
			double31 = double16;
		} else {
			double32 = double16;
			double31 = double13;
		}

		double double33;
		double double34;
		if (double14 < double17) {
			double34 = double14;
			double33 = double17;
		} else {
			double34 = double17;
			double33 = double14;
		}

		double double35;
		double double36;
		if (double15 < double18) {
			double36 = double15;
			double35 = double18;
		} else {
			double36 = double18;
			double35 = double15;
		}

		double double37;
		double double38;
		if (double19 < double22) {
			double38 = double19;
			double37 = double22;
		} else {
			double38 = double22;
			double37 = double19;
		}

		double double39;
		double double40;
		if (double20 < double23) {
			double39 = double20;
			double40 = double23;
		} else {
			double39 = double23;
			double40 = double20;
		}

		double double41;
		double double42;
		if (double21 < double24) {
			double41 = double21;
			double42 = double24;
		} else {
			double41 = double24;
			double42 = double21;
		}

		vector3d.x = double26 + double32 + double38 + this.m30;
		vector3d.y = double28 + double34 + double39 + this.m31;
		vector3d.z = double30 + double36 + double41 + this.m32;
		vector3d2.x = double25 + double31 + double37 + this.m30;
		vector3d2.y = double27 + double33 + double40 + this.m31;
		vector3d2.z = double29 + double35 + double42 + this.m32;
		return this;
	}

	public Matrix4x3d transformAab(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3d vector3d, Vector3d vector3d2) {
		return this.transformAab(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3d, vector3d2);
	}

	public Matrix4x3d lerp(Matrix4x3dc matrix4x3dc, double double1) {
		return this.lerp(matrix4x3dc, double1, this);
	}

	public Matrix4x3d lerp(Matrix4x3dc matrix4x3dc, double double1, Matrix4x3d matrix4x3d) {
		matrix4x3d.m00 = this.m00 + (matrix4x3dc.m00() - this.m00) * double1;
		matrix4x3d.m01 = this.m01 + (matrix4x3dc.m01() - this.m01) * double1;
		matrix4x3d.m02 = this.m02 + (matrix4x3dc.m02() - this.m02) * double1;
		matrix4x3d.m10 = this.m10 + (matrix4x3dc.m10() - this.m10) * double1;
		matrix4x3d.m11 = this.m11 + (matrix4x3dc.m11() - this.m11) * double1;
		matrix4x3d.m12 = this.m12 + (matrix4x3dc.m12() - this.m12) * double1;
		matrix4x3d.m20 = this.m20 + (matrix4x3dc.m20() - this.m20) * double1;
		matrix4x3d.m21 = this.m21 + (matrix4x3dc.m21() - this.m21) * double1;
		matrix4x3d.m22 = this.m22 + (matrix4x3dc.m22() - this.m22) * double1;
		matrix4x3d.m30 = this.m30 + (matrix4x3dc.m30() - this.m30) * double1;
		matrix4x3d.m31 = this.m31 + (matrix4x3dc.m31() - this.m31) * double1;
		matrix4x3d.m32 = this.m32 + (matrix4x3dc.m32() - this.m32) * double1;
		return matrix4x3d;
	}

	public Matrix4x3d rotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4x3d matrix4x3d) {
		return this.rotateTowards(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), matrix4x3d);
	}

	public Matrix4x3d rotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.rotateTowards(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), this);
	}

	public Matrix4x3d rotateTowards(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.rotateTowards(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4x3d rotateTowards(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
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
		matrix4x3d.m30 = this.m30;
		matrix4x3d.m31 = this.m31;
		matrix4x3d.m32 = this.m32;
		double double18 = this.m00 * double11 + this.m10 * double12 + this.m20 * double13;
		double double19 = this.m01 * double11 + this.m11 * double12 + this.m21 * double13;
		double double20 = this.m02 * double11 + this.m12 * double12 + this.m22 * double13;
		double double21 = this.m00 * double15 + this.m10 * double16 + this.m20 * double17;
		double double22 = this.m01 * double15 + this.m11 * double16 + this.m21 * double17;
		double double23 = this.m02 * double15 + this.m12 * double16 + this.m22 * double17;
		matrix4x3d.m20 = this.m00 * double8 + this.m10 * double9 + this.m20 * double10;
		matrix4x3d.m21 = this.m01 * double8 + this.m11 * double9 + this.m21 * double10;
		matrix4x3d.m22 = this.m02 * double8 + this.m12 * double9 + this.m22 * double10;
		matrix4x3d.m00 = double18;
		matrix4x3d.m01 = double19;
		matrix4x3d.m02 = double20;
		matrix4x3d.m10 = double21;
		matrix4x3d.m11 = double22;
		matrix4x3d.m12 = double23;
		matrix4x3d.properties = (byte)(this.properties & -13);
		return matrix4x3d;
	}

	public Matrix4x3d rotationTowards(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.rotationTowards(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix4x3d rotationTowards(double double1, double double2, double double3, double double4, double double5, double double6) {
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
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.properties = 0;
		return this;
	}

	public Matrix4x3d translationRotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		return this.translationRotateTowards(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z());
	}

	public Matrix4x3d translationRotateTowards(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = 1.0 / Math.sqrt(double4 * double4 + double5 * double5 + double6 * double6);
		double double11 = double4 * double10;
		double double12 = double5 * double10;
		double double13 = double6 * double10;
		double double14 = double8 * double13 - double9 * double12;
		double double15 = double9 * double11 - double7 * double13;
		double double16 = double7 * double12 - double8 * double11;
		double double17 = 1.0 / Math.sqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double12 * double16 - double13 * double15;
		double double19 = double13 * double14 - double11 * double16;
		double double20 = double11 * double15 - double12 * double14;
		this.m00 = double14;
		this.m01 = double15;
		this.m02 = double16;
		this.m10 = double18;
		this.m11 = double19;
		this.m12 = double20;
		this.m20 = double11;
		this.m21 = double12;
		this.m22 = double13;
		this.m30 = double1;
		this.m31 = double2;
		this.m32 = double3;
		this.properties = 0;
		return this;
	}

	public Vector3d getEulerAnglesZYX(Vector3d vector3d) {
		vector3d.x = Math.atan2(this.m12, this.m22);
		vector3d.y = Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22));
		vector3d.z = Math.atan2(this.m01, this.m00);
		return vector3d;
	}

	public Matrix4x3dc toImmutable() {
		return (Matrix4x3dc)(!Options.DEBUG ? this : new Matrix4x3d.Proxy(this));
	}

	private final class Proxy implements Matrix4x3dc {
		private final Matrix4x3dc delegate;

		Proxy(Matrix4x3dc matrix4x3dc) {
			this.delegate = matrix4x3dc;
		}

		public byte properties() {
			return this.delegate.properties();
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

		public double m30() {
			return this.delegate.m30();
		}

		public double m31() {
			return this.delegate.m31();
		}

		public double m32() {
			return this.delegate.m32();
		}

		public Matrix4d get(Matrix4d matrix4d) {
			return this.delegate.get(matrix4d);
		}

		public Matrix4x3d mul(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.mul(matrix4x3dc, matrix4x3d);
		}

		public Matrix4x3d mul(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d) {
			return this.delegate.mul(matrix4x3fc, matrix4x3d);
		}

		public Matrix4x3d mulTranslation(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.mulTranslation(matrix4x3dc, matrix4x3d);
		}

		public Matrix4x3d mulTranslation(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d) {
			return this.delegate.mulTranslation(matrix4x3fc, matrix4x3d);
		}

		public Matrix4x3d mulOrtho(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.mulOrtho(matrix4x3dc, matrix4x3d);
		}

		public Matrix4x3d fma(Matrix4x3dc matrix4x3dc, double double1, Matrix4x3d matrix4x3d) {
			return this.delegate.fma(matrix4x3dc, double1, matrix4x3d);
		}

		public Matrix4x3d fma(Matrix4x3fc matrix4x3fc, double double1, Matrix4x3d matrix4x3d) {
			return this.delegate.fma(matrix4x3fc, double1, matrix4x3d);
		}

		public Matrix4x3d add(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.add(matrix4x3dc, matrix4x3d);
		}

		public Matrix4x3d add(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d) {
			return this.delegate.add(matrix4x3fc, matrix4x3d);
		}

		public Matrix4x3d sub(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.sub(matrix4x3dc, matrix4x3d);
		}

		public Matrix4x3d sub(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d) {
			return this.delegate.sub(matrix4x3fc, matrix4x3d);
		}

		public Matrix4x3d mulComponentWise(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.mulComponentWise(matrix4x3dc, matrix4x3d);
		}

		public double determinant() {
			return this.delegate.determinant();
		}

		public Matrix4x3d invert(Matrix4x3d matrix4x3d) {
			return this.delegate.invert(matrix4x3d);
		}

		public Matrix4x3d invertOrtho(Matrix4x3d matrix4x3d) {
			return this.delegate.invertOrtho(matrix4x3d);
		}

		public Matrix4x3d invertUnitScale(Matrix4x3d matrix4x3d) {
			return this.delegate.invertUnitScale(matrix4x3d);
		}

		public Matrix4x3d transpose3x3(Matrix4x3d matrix4x3d) {
			return this.delegate.transpose3x3(matrix4x3d);
		}

		public Matrix3d transpose3x3(Matrix3d matrix3d) {
			return this.delegate.transpose3x3(matrix3d);
		}

		public Vector3d getTranslation(Vector3d vector3d) {
			return this.delegate.getTranslation(vector3d);
		}

		public Vector3d getScale(Vector3d vector3d) {
			return this.delegate.getScale(vector3d);
		}

		public Matrix4x3d get(Matrix4x3d matrix4x3d) {
			return this.delegate.get(matrix4x3d);
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

		public DoubleBuffer get4x4(DoubleBuffer doubleBuffer) {
			return this.delegate.get4x4(doubleBuffer);
		}

		public DoubleBuffer get4x4(int int1, DoubleBuffer doubleBuffer) {
			return this.delegate.get4x4(int1, doubleBuffer);
		}

		public ByteBuffer get4x4(ByteBuffer byteBuffer) {
			return this.delegate.get4x4(byteBuffer);
		}

		public ByteBuffer get4x4(int int1, ByteBuffer byteBuffer) {
			return this.delegate.get4x4(int1, byteBuffer);
		}

		public DoubleBuffer getTransposed(DoubleBuffer doubleBuffer) {
			return this.delegate.getTransposed(doubleBuffer);
		}

		public DoubleBuffer getTransposed(int int1, DoubleBuffer doubleBuffer) {
			return this.delegate.getTransposed(int1, doubleBuffer);
		}

		public ByteBuffer getTransposed(ByteBuffer byteBuffer) {
			return this.delegate.getTransposed(byteBuffer);
		}

		public ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer) {
			return this.delegate.getTransposed(int1, byteBuffer);
		}

		public FloatBuffer getTransposed(FloatBuffer floatBuffer) {
			return this.delegate.getTransposed(floatBuffer);
		}

		public FloatBuffer getTransposed(int int1, FloatBuffer floatBuffer) {
			return this.delegate.getTransposed(int1, floatBuffer);
		}

		public ByteBuffer getTransposedFloats(ByteBuffer byteBuffer) {
			return this.delegate.getTransposedFloats(byteBuffer);
		}

		public ByteBuffer getTransposedFloats(int int1, ByteBuffer byteBuffer) {
			return this.delegate.getTransposedFloats(int1, byteBuffer);
		}

		public double[] getTransposed(double[] doubleArray, int int1) {
			return this.delegate.getTransposed(doubleArray, int1);
		}

		public double[] getTransposed(double[] doubleArray) {
			return this.delegate.getTransposed(doubleArray);
		}

		public Vector4d transform(Vector4d vector4d) {
			return this.delegate.transform(vector4d);
		}

		public Vector4d transform(Vector4dc vector4dc, Vector4d vector4d) {
			return this.delegate.transform(vector4dc, vector4d);
		}

		public Vector3d transformPosition(Vector3d vector3d) {
			return this.delegate.transformPosition(vector3d);
		}

		public Vector3d transformPosition(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.transformPosition(vector3dc, vector3d);
		}

		public Vector3d transformDirection(Vector3d vector3d) {
			return this.delegate.transformDirection(vector3d);
		}

		public Vector3d transformDirection(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.transformDirection(vector3dc, vector3d);
		}

		public Matrix4x3d scale(Vector3dc vector3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.scale(vector3dc, matrix4x3d);
		}

		public Matrix4x3d scale(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
			return this.delegate.scale(double1, double2, double3, matrix4x3d);
		}

		public Matrix4x3d scale(double double1, Matrix4x3d matrix4x3d) {
			return this.delegate.scale(double1, matrix4x3d);
		}

		public Matrix4x3d scaleLocal(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
			return this.delegate.scaleLocal(double1, double2, double3, matrix4x3d);
		}

		public Matrix4x3d rotate(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
			return this.delegate.rotate(double1, double2, double3, double4, matrix4x3d);
		}

		public Matrix4x3d rotateTranslation(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateTranslation(double1, double2, double3, double4, matrix4x3d);
		}

		public Matrix4x3d rotateLocal(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateLocal(double1, double2, double3, double4, matrix4x3d);
		}

		public Matrix4x3d translate(Vector3dc vector3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.translate(vector3dc, matrix4x3d);
		}

		public Matrix4x3d translate(Vector3fc vector3fc, Matrix4x3d matrix4x3d) {
			return this.delegate.translate(vector3fc, matrix4x3d);
		}

		public Matrix4x3d translate(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
			return this.delegate.translate(double1, double2, double3, matrix4x3d);
		}

		public Matrix4x3d translateLocal(Vector3fc vector3fc, Matrix4x3d matrix4x3d) {
			return this.delegate.translateLocal(vector3fc, matrix4x3d);
		}

		public Matrix4x3d translateLocal(Vector3dc vector3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.translateLocal(vector3dc, matrix4x3d);
		}

		public Matrix4x3d translateLocal(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
			return this.delegate.translateLocal(double1, double2, double3, matrix4x3d);
		}

		public Matrix4x3d rotateX(double double1, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateX(double1, matrix4x3d);
		}

		public Matrix4x3d rotateY(double double1, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateY(double1, matrix4x3d);
		}

		public Matrix4x3d rotateZ(double double1, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateZ(double1, matrix4x3d);
		}

		public Matrix4x3d rotateXYZ(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateXYZ(double1, double2, double3, matrix4x3d);
		}

		public Matrix4x3d rotateZYX(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateZYX(double1, double2, double3, matrix4x3d);
		}

		public Matrix4x3d rotateYXZ(double double1, double double2, double double3, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateYXZ(double1, double2, double3, matrix4x3d);
		}

		public Matrix4x3d rotate(Quaterniondc quaterniondc, Matrix4x3d matrix4x3d) {
			return this.delegate.rotate(quaterniondc, matrix4x3d);
		}

		public Matrix4x3d rotate(Quaternionfc quaternionfc, Matrix4x3d matrix4x3d) {
			return this.delegate.rotate(quaternionfc, matrix4x3d);
		}

		public Matrix4x3d rotateTranslation(Quaterniondc quaterniondc, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateTranslation(quaterniondc, matrix4x3d);
		}

		public Matrix4x3d rotateTranslation(Quaternionfc quaternionfc, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateTranslation(quaternionfc, matrix4x3d);
		}

		public Matrix4x3d rotateLocal(Quaterniondc quaterniondc, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateLocal(quaterniondc, matrix4x3d);
		}

		public Matrix4x3d rotateLocal(Quaternionfc quaternionfc, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateLocal(quaternionfc, matrix4x3d);
		}

		public Matrix4x3d rotate(AxisAngle4f axisAngle4f, Matrix4x3d matrix4x3d) {
			return this.delegate.rotate(axisAngle4f, matrix4x3d);
		}

		public Matrix4x3d rotate(AxisAngle4d axisAngle4d, Matrix4x3d matrix4x3d) {
			return this.delegate.rotate(axisAngle4d, matrix4x3d);
		}

		public Matrix4x3d rotate(double double1, Vector3dc vector3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.rotate(double1, vector3dc, matrix4x3d);
		}

		public Matrix4x3d rotate(double double1, Vector3fc vector3fc, Matrix4x3d matrix4x3d) {
			return this.delegate.rotate(double1, vector3fc, matrix4x3d);
		}

		public Vector4d getRow(int int1, Vector4d vector4d) throws IndexOutOfBoundsException {
			return this.delegate.getRow(int1, vector4d);
		}

		public Vector3d getColumn(int int1, Vector3d vector3d) throws IndexOutOfBoundsException {
			return this.delegate.getColumn(int1, vector3d);
		}

		public Matrix4x3d normal(Matrix4x3d matrix4x3d) {
			return this.delegate.normal(matrix4x3d);
		}

		public Matrix3d normal(Matrix3d matrix3d) {
			return this.delegate.normal(matrix3d);
		}

		public Matrix4x3d normalize3x3(Matrix4x3d matrix4x3d) {
			return this.delegate.normalize3x3(matrix4x3d);
		}

		public Matrix3d normalize3x3(Matrix3d matrix3d) {
			return this.delegate.normalize3x3(matrix3d);
		}

		public Matrix4x3d reflect(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
			return this.delegate.reflect(double1, double2, double3, double4, matrix4x3d);
		}

		public Matrix4x3d reflect(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
			return this.delegate.reflect(double1, double2, double3, double4, double5, double6, matrix4x3d);
		}

		public Matrix4x3d reflect(Quaterniondc quaterniondc, Vector3dc vector3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.reflect(quaterniondc, vector3dc, matrix4x3d);
		}

		public Matrix4x3d reflect(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4x3d matrix4x3d) {
			return this.delegate.reflect(vector3dc, vector3dc2, matrix4x3d);
		}

		public Matrix4x3d ortho(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4x3d matrix4x3d) {
			return this.delegate.ortho(double1, double2, double3, double4, double5, double6, boolean1, matrix4x3d);
		}

		public Matrix4x3d ortho(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
			return this.delegate.ortho(double1, double2, double3, double4, double5, double6, matrix4x3d);
		}

		public Matrix4x3d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4x3d matrix4x3d) {
			return this.delegate.orthoLH(double1, double2, double3, double4, double5, double6, boolean1, matrix4x3d);
		}

		public Matrix4x3d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
			return this.delegate.orthoLH(double1, double2, double3, double4, double5, double6, matrix4x3d);
		}

		public Matrix4x3d orthoSymmetric(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4x3d matrix4x3d) {
			return this.delegate.orthoSymmetric(double1, double2, double3, double4, boolean1, matrix4x3d);
		}

		public Matrix4x3d orthoSymmetric(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
			return this.delegate.orthoSymmetric(double1, double2, double3, double4, matrix4x3d);
		}

		public Matrix4x3d orthoSymmetricLH(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4x3d matrix4x3d) {
			return this.delegate.orthoSymmetricLH(double1, double2, double3, double4, boolean1, matrix4x3d);
		}

		public Matrix4x3d orthoSymmetricLH(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
			return this.delegate.orthoSymmetricLH(double1, double2, double3, double4, matrix4x3d);
		}

		public Matrix4x3d ortho2D(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
			return this.delegate.ortho2D(double1, double2, double3, double4, matrix4x3d);
		}

		public Matrix4x3d ortho2DLH(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
			return this.delegate.ortho2DLH(double1, double2, double3, double4, matrix4x3d);
		}

		public Matrix4x3d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4x3d matrix4x3d) {
			return this.delegate.lookAlong(vector3dc, vector3dc2, matrix4x3d);
		}

		public Matrix4x3d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
			return this.delegate.lookAlong(double1, double2, double3, double4, double5, double6, matrix4x3d);
		}

		public Matrix4x3d lookAt(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Matrix4x3d matrix4x3d) {
			return this.delegate.lookAt(vector3dc, vector3dc2, vector3dc3, matrix4x3d);
		}

		public Matrix4x3d lookAt(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4x3d matrix4x3d) {
			return this.delegate.lookAt(double1, double2, double3, double4, double5, double6, double7, double8, double9, matrix4x3d);
		}

		public Matrix4x3d lookAtLH(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Matrix4x3d matrix4x3d) {
			return this.delegate.lookAtLH(vector3dc, vector3dc2, vector3dc3, matrix4x3d);
		}

		public Matrix4x3d lookAtLH(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4x3d matrix4x3d) {
			return this.delegate.lookAtLH(double1, double2, double3, double4, double5, double6, double7, double8, double9, matrix4x3d);
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

		public Vector3d origin(Vector3d vector3d) {
			return this.delegate.origin(vector3d);
		}

		public Matrix4x3d shadow(Vector4dc vector4dc, double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d) {
			return this.delegate.shadow(vector4dc, double1, double2, double3, double4, matrix4x3d);
		}

		public Matrix4x3d shadow(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Matrix4x3d matrix4x3d) {
			return this.delegate.shadow(double1, double2, double3, double4, double5, double6, double7, double8, matrix4x3d);
		}

		public Matrix4x3d shadow(Vector4dc vector4dc, Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.shadow(vector4dc, matrix4x3dc, matrix4x3d);
		}

		public Matrix4x3d shadow(double double1, double double2, double double3, double double4, Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d) {
			return this.delegate.shadow(double1, double2, double3, double4, matrix4x3dc, matrix4x3d);
		}

		public Matrix4x3d pick(double double1, double double2, double double3, double double4, int[] intArray, Matrix4x3d matrix4x3d) {
			return this.delegate.pick(double1, double2, double3, double4, intArray, matrix4x3d);
		}

		public Matrix4x3d arcball(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
			return this.delegate.arcball(double1, double2, double3, double4, double5, double6, matrix4x3d);
		}

		public Matrix4x3d arcball(double double1, Vector3dc vector3dc, double double2, double double3, Matrix4x3d matrix4x3d) {
			return this.delegate.arcball(double1, vector3dc, double2, double3, matrix4x3d);
		}

		public Matrix4x3d transformAab(double double1, double double2, double double3, double double4, double double5, double double6, Vector3d vector3d, Vector3d vector3d2) {
			return this.delegate.transformAab(double1, double2, double3, double4, double5, double6, vector3d, vector3d2);
		}

		public Matrix4x3d transformAab(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3d vector3d, Vector3d vector3d2) {
			return this.delegate.transformAab(vector3dc, vector3dc2, vector3d, vector3d2);
		}

		public Matrix4x3d lerp(Matrix4x3dc matrix4x3dc, double double1, Matrix4x3d matrix4x3d) {
			return this.delegate.lerp(matrix4x3dc, double1, matrix4x3d);
		}

		public Matrix4x3d rotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateTowards(vector3dc, vector3dc2, matrix4x3d);
		}

		public Matrix4x3d rotateTowards(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d) {
			return this.delegate.rotateTowards(double1, double2, double3, double4, double5, double6, matrix4x3d);
		}

		public Vector3d getEulerAnglesZYX(Vector3d vector3d) {
			return this.delegate.getEulerAnglesZYX(vector3d);
		}
	}
}
