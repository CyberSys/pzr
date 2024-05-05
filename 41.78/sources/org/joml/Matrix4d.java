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


public class Matrix4d implements Externalizable,Matrix4dc {
	private static final long serialVersionUID = 1L;
	double m00;
	double m01;
	double m02;
	double m03;
	double m10;
	double m11;
	double m12;
	double m13;
	double m20;
	double m21;
	double m22;
	double m23;
	double m30;
	double m31;
	double m32;
	double m33;
	int properties;

	public Matrix4d() {
		this._m00(1.0)._m11(1.0)._m22(1.0)._m33(1.0).properties = 30;
	}

	public Matrix4d(Matrix4dc matrix4dc) {
		this.set(matrix4dc);
	}

	public Matrix4d(Matrix4fc matrix4fc) {
		this.set(matrix4fc);
	}

	public Matrix4d(Matrix4x3dc matrix4x3dc) {
		this.set(matrix4x3dc);
	}

	public Matrix4d(Matrix4x3fc matrix4x3fc) {
		this.set(matrix4x3fc);
	}

	public Matrix4d(Matrix3dc matrix3dc) {
		this.set(matrix3dc);
	}

	public Matrix4d(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16) {
		this.m00 = double1;
		this.m01 = double2;
		this.m02 = double3;
		this.m03 = double4;
		this.m10 = double5;
		this.m11 = double6;
		this.m12 = double7;
		this.m13 = double8;
		this.m20 = double9;
		this.m21 = double10;
		this.m22 = double11;
		this.m23 = double12;
		this.m30 = double13;
		this.m31 = double14;
		this.m32 = double15;
		this.m33 = double16;
		this.determineProperties();
	}

	public Matrix4d(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
		this.determineProperties();
	}

	public Matrix4d(Vector4d vector4d, Vector4d vector4d2, Vector4d vector4d3, Vector4d vector4d4) {
		this.set(vector4d, vector4d2, vector4d3, vector4d4);
	}

	public Matrix4d assume(int int1) {
		this.properties = (byte)int1;
		return this;
	}

	public Matrix4d determineProperties() {
		int int1 = 0;
		if (this.m03 == 0.0 && this.m13 == 0.0) {
			if (this.m23 == 0.0 && this.m33 == 1.0) {
				int1 |= 2;
				if (this.m00 == 1.0 && this.m01 == 0.0 && this.m02 == 0.0 && this.m10 == 0.0 && this.m11 == 1.0 && this.m12 == 0.0 && this.m20 == 0.0 && this.m21 == 0.0 && this.m22 == 1.0) {
					int1 |= 24;
					if (this.m30 == 0.0 && this.m31 == 0.0 && this.m32 == 0.0) {
						int1 |= 4;
					}
				}
			} else if (this.m01 == 0.0 && this.m02 == 0.0 && this.m10 == 0.0 && this.m12 == 0.0 && this.m20 == 0.0 && this.m21 == 0.0 && this.m30 == 0.0 && this.m31 == 0.0 && this.m33 == 0.0) {
				int1 |= 1;
			}
		}

		this.properties = int1;
		return this;
	}

	public int properties() {
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

	public double m03() {
		return this.m03;
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

	public double m13() {
		return this.m13;
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

	public double m23() {
		return this.m23;
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

	public double m33() {
		return this.m33;
	}

	public Matrix4d m00(double double1) {
		this.m00 = double1;
		this.properties &= -17;
		if (double1 != 1.0) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4d m01(double double1) {
		this.m01 = double1;
		this.properties &= -17;
		if (double1 != 0.0) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4d m02(double double1) {
		this.m02 = double1;
		this.properties &= -17;
		if (double1 != 0.0) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4d m03(double double1) {
		this.m03 = double1;
		if (double1 != 0.0) {
			this.properties = 0;
		}

		return this;
	}

	public Matrix4d m10(double double1) {
		this.m10 = double1;
		this.properties &= -17;
		if (double1 != 0.0) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4d m11(double double1) {
		this.m11 = double1;
		this.properties &= -17;
		if (double1 != 1.0) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4d m12(double double1) {
		this.m12 = double1;
		this.properties &= -17;
		if (double1 != 0.0) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4d m13(double double1) {
		this.m13 = double1;
		if (this.m03 != 0.0) {
			this.properties = 0;
		}

		return this;
	}

	public Matrix4d m20(double double1) {
		this.m20 = double1;
		this.properties &= -17;
		if (double1 != 0.0) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4d m21(double double1) {
		this.m21 = double1;
		this.properties &= -17;
		if (double1 != 0.0) {
			this.properties &= -14;
		}

		return this;
	}

	public Matrix4d m22(double double1) {
		this.m22 = double1;
		this.properties &= -17;
		if (double1 != 1.0) {
			this.properties &= -13;
		}

		return this;
	}

	public Matrix4d m23(double double1) {
		this.m23 = double1;
		if (double1 != 0.0) {
			this.properties &= -31;
		}

		return this;
	}

	public Matrix4d m30(double double1) {
		this.m30 = double1;
		if (double1 != 0.0) {
			this.properties &= -6;
		}

		return this;
	}

	public Matrix4d m31(double double1) {
		this.m31 = double1;
		if (double1 != 0.0) {
			this.properties &= -6;
		}

		return this;
	}

	public Matrix4d m32(double double1) {
		this.m32 = double1;
		if (double1 != 0.0) {
			this.properties &= -6;
		}

		return this;
	}

	public Matrix4d m33(double double1) {
		this.m33 = double1;
		if (double1 != 0.0) {
			this.properties &= -2;
		}

		if (double1 != 1.0) {
			this.properties &= -31;
		}

		return this;
	}

	Matrix4d _properties(int int1) {
		this.properties = int1;
		return this;
	}

	Matrix4d _m00(double double1) {
		this.m00 = double1;
		return this;
	}

	Matrix4d _m01(double double1) {
		this.m01 = double1;
		return this;
	}

	Matrix4d _m02(double double1) {
		this.m02 = double1;
		return this;
	}

	Matrix4d _m03(double double1) {
		this.m03 = double1;
		return this;
	}

	Matrix4d _m10(double double1) {
		this.m10 = double1;
		return this;
	}

	Matrix4d _m11(double double1) {
		this.m11 = double1;
		return this;
	}

	Matrix4d _m12(double double1) {
		this.m12 = double1;
		return this;
	}

	Matrix4d _m13(double double1) {
		this.m13 = double1;
		return this;
	}

	Matrix4d _m20(double double1) {
		this.m20 = double1;
		return this;
	}

	Matrix4d _m21(double double1) {
		this.m21 = double1;
		return this;
	}

	Matrix4d _m22(double double1) {
		this.m22 = double1;
		return this;
	}

	Matrix4d _m23(double double1) {
		this.m23 = double1;
		return this;
	}

	Matrix4d _m30(double double1) {
		this.m30 = double1;
		return this;
	}

	Matrix4d _m31(double double1) {
		this.m31 = double1;
		return this;
	}

	Matrix4d _m32(double double1) {
		this.m32 = double1;
		return this;
	}

	Matrix4d _m33(double double1) {
		this.m33 = double1;
		return this;
	}

	public Matrix4d identity() {
		if ((this.properties & 4) != 0) {
			return this;
		} else {
			this._identity();
			this.properties = 30;
			return this;
		}
	}

	private void _identity() {
		this._m00(1.0)._m10(0.0)._m20(0.0)._m30(0.0)._m01(0.0)._m11(1.0)._m21(0.0)._m31(0.0)._m02(0.0)._m12(0.0)._m22(1.0)._m32(0.0)._m03(0.0)._m13(0.0)._m23(0.0)._m33(1.0);
	}

	public Matrix4d set(Matrix4dc matrix4dc) {
		return this._m00(matrix4dc.m00())._m01(matrix4dc.m01())._m02(matrix4dc.m02())._m03(matrix4dc.m03())._m10(matrix4dc.m10())._m11(matrix4dc.m11())._m12(matrix4dc.m12())._m13(matrix4dc.m13())._m20(matrix4dc.m20())._m21(matrix4dc.m21())._m22(matrix4dc.m22())._m23(matrix4dc.m23())._m30(matrix4dc.m30())._m31(matrix4dc.m31())._m32(matrix4dc.m32())._m33(matrix4dc.m33())._properties(matrix4dc.properties());
	}

	public Matrix4d set(Matrix4fc matrix4fc) {
		return this._m00((double)matrix4fc.m00())._m01((double)matrix4fc.m01())._m02((double)matrix4fc.m02())._m03((double)matrix4fc.m03())._m10((double)matrix4fc.m10())._m11((double)matrix4fc.m11())._m12((double)matrix4fc.m12())._m13((double)matrix4fc.m13())._m20((double)matrix4fc.m20())._m21((double)matrix4fc.m21())._m22((double)matrix4fc.m22())._m23((double)matrix4fc.m23())._m30((double)matrix4fc.m30())._m31((double)matrix4fc.m31())._m32((double)matrix4fc.m32())._m33((double)matrix4fc.m33())._properties(matrix4fc.properties());
	}

	public Matrix4d setTransposed(Matrix4dc matrix4dc) {
		return (matrix4dc.properties() & 4) != 0 ? this.identity() : this.setTransposedInternal(matrix4dc);
	}

	private Matrix4d setTransposedInternal(Matrix4dc matrix4dc) {
		double double1 = matrix4dc.m01();
		double double2 = matrix4dc.m21();
		double double3 = matrix4dc.m31();
		double double4 = matrix4dc.m02();
		double double5 = matrix4dc.m12();
		double double6 = matrix4dc.m03();
		double double7 = matrix4dc.m13();
		double double8 = matrix4dc.m23();
		return this._m00(matrix4dc.m00())._m01(matrix4dc.m10())._m02(matrix4dc.m20())._m03(matrix4dc.m30())._m10(double1)._m11(matrix4dc.m11())._m12(double2)._m13(double3)._m20(double4)._m21(double5)._m22(matrix4dc.m22())._m23(matrix4dc.m32())._m30(double6)._m31(double7)._m32(double8)._m33(matrix4dc.m33())._properties(matrix4dc.properties() & 4);
	}

	public Matrix4d set(Matrix4x3dc matrix4x3dc) {
		return this._m00(matrix4x3dc.m00())._m01(matrix4x3dc.m01())._m02(matrix4x3dc.m02())._m03(0.0)._m10(matrix4x3dc.m10())._m11(matrix4x3dc.m11())._m12(matrix4x3dc.m12())._m13(0.0)._m20(matrix4x3dc.m20())._m21(matrix4x3dc.m21())._m22(matrix4x3dc.m22())._m23(0.0)._m30(matrix4x3dc.m30())._m31(matrix4x3dc.m31())._m32(matrix4x3dc.m32())._m33(1.0)._properties(matrix4x3dc.properties() | 2);
	}

	public Matrix4d set(Matrix4x3fc matrix4x3fc) {
		return this._m00((double)matrix4x3fc.m00())._m01((double)matrix4x3fc.m01())._m02((double)matrix4x3fc.m02())._m03(0.0)._m10((double)matrix4x3fc.m10())._m11((double)matrix4x3fc.m11())._m12((double)matrix4x3fc.m12())._m13(0.0)._m20((double)matrix4x3fc.m20())._m21((double)matrix4x3fc.m21())._m22((double)matrix4x3fc.m22())._m23(0.0)._m30((double)matrix4x3fc.m30())._m31((double)matrix4x3fc.m31())._m32((double)matrix4x3fc.m32())._m33(1.0)._properties(matrix4x3fc.properties() | 2);
	}

	public Matrix4d set(Matrix3dc matrix3dc) {
		return this._m00(matrix3dc.m00())._m01(matrix3dc.m01())._m02(matrix3dc.m02())._m03(0.0)._m10(matrix3dc.m10())._m11(matrix3dc.m11())._m12(matrix3dc.m12())._m13(0.0)._m20(matrix3dc.m20())._m21(matrix3dc.m21())._m22(matrix3dc.m22())._m23(0.0)._m30(0.0)._m31(0.0)._m32(0.0)._m33(1.0)._properties(2);
	}

	public Matrix4d set3x3(Matrix4dc matrix4dc) {
		return this._m00(matrix4dc.m00())._m01(matrix4dc.m01())._m02(matrix4dc.m02())._m10(matrix4dc.m10())._m11(matrix4dc.m11())._m12(matrix4dc.m12())._m20(matrix4dc.m20())._m21(matrix4dc.m21())._m22(matrix4dc.m22())._properties(this.properties & matrix4dc.properties() & -2);
	}

	public Matrix4d set4x3(Matrix4x3dc matrix4x3dc) {
		return this._m00(matrix4x3dc.m00())._m01(matrix4x3dc.m01())._m02(matrix4x3dc.m02())._m10(matrix4x3dc.m10())._m11(matrix4x3dc.m11())._m12(matrix4x3dc.m12())._m20(matrix4x3dc.m20())._m21(matrix4x3dc.m21())._m22(matrix4x3dc.m22())._m30(matrix4x3dc.m30())._m31(matrix4x3dc.m31())._m32(matrix4x3dc.m32())._properties(this.properties & matrix4x3dc.properties() & -2);
	}

	public Matrix4d set4x3(Matrix4x3fc matrix4x3fc) {
		return this._m00((double)matrix4x3fc.m00())._m01((double)matrix4x3fc.m01())._m02((double)matrix4x3fc.m02())._m10((double)matrix4x3fc.m10())._m11((double)matrix4x3fc.m11())._m12((double)matrix4x3fc.m12())._m20((double)matrix4x3fc.m20())._m21((double)matrix4x3fc.m21())._m22((double)matrix4x3fc.m22())._m30((double)matrix4x3fc.m30())._m31((double)matrix4x3fc.m31())._m32((double)matrix4x3fc.m32())._properties(this.properties & matrix4x3fc.properties() & -2);
	}

	public Matrix4d set4x3(Matrix4dc matrix4dc) {
		return this._m00(matrix4dc.m00())._m01(matrix4dc.m01())._m02(matrix4dc.m02())._m10(matrix4dc.m10())._m11(matrix4dc.m11())._m12(matrix4dc.m12())._m20(matrix4dc.m20())._m21(matrix4dc.m21())._m22(matrix4dc.m22())._m30(matrix4dc.m30())._m31(matrix4dc.m31())._m32(matrix4dc.m32())._properties(this.properties & matrix4dc.properties() & -2);
	}

	public Matrix4d set(AxisAngle4f axisAngle4f) {
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
		this._m00(double7 + double1 * double1 * double8)._m11(double7 + double2 * double2 * double8)._m22(double7 + double3 * double3 * double8);
		double double9 = double1 * double2 * double8;
		double double10 = double3 * double6;
		this._m10(double9 - double10)._m01(double9 + double10);
		double9 = double1 * double3 * double8;
		double10 = double2 * double6;
		this._m20(double9 + double10)._m02(double9 - double10);
		double9 = double2 * double3 * double8;
		double10 = double1 * double6;
		this._m21(double9 - double10)._m12(double9 + double10)._m03(0.0)._m13(0.0)._m23(0.0)._m30(0.0)._m31(0.0)._m32(0.0)._m33(1.0).properties = 18;
		return this;
	}

	public Matrix4d set(AxisAngle4d axisAngle4d) {
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
		this._m00(double7 + double1 * double1 * double8)._m11(double7 + double2 * double2 * double8)._m22(double7 + double3 * double3 * double8);
		double double9 = double1 * double2 * double8;
		double double10 = double3 * double6;
		this._m10(double9 - double10)._m01(double9 + double10);
		double9 = double1 * double3 * double8;
		double10 = double2 * double6;
		this._m20(double9 + double10)._m02(double9 - double10);
		double9 = double2 * double3 * double8;
		double10 = double1 * double6;
		this._m21(double9 - double10)._m12(double9 + double10)._m03(0.0)._m13(0.0)._m23(0.0)._m30(0.0)._m31(0.0)._m32(0.0)._m33(1.0).properties = 18;
		return this;
	}

	public Matrix4d set(Quaternionfc quaternionfc) {
		return this.rotation(quaternionfc);
	}

	public Matrix4d set(Quaterniondc quaterniondc) {
		return this.rotation(quaterniondc);
	}

	public Matrix4d mul(Matrix4dc matrix4dc) {
		return this.mul(matrix4dc, this);
	}

	public Matrix4d mul(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.set(matrix4dc);
		} else if ((matrix4dc.properties() & 4) != 0) {
			return matrix4d.set((Matrix4dc)this);
		} else if ((this.properties & 8) != 0 && (matrix4dc.properties() & 2) != 0) {
			return this.mulTranslationAffine(matrix4dc, matrix4d);
		} else if ((this.properties & 2) != 0 && (matrix4dc.properties() & 2) != 0) {
			return this.mulAffine(matrix4dc, matrix4d);
		} else if ((this.properties & 1) != 0 && (matrix4dc.properties() & 2) != 0) {
			return this.mulPerspectiveAffine(matrix4dc, matrix4d);
		} else {
			return (matrix4dc.properties() & 2) != 0 ? this.mulAffineR(matrix4dc, matrix4d) : this.mul0(matrix4dc, matrix4d);
		}
	}

	public Matrix4d mul0(Matrix4dc matrix4dc) {
		return this.mul0(matrix4dc, this);
	}

	public Matrix4d mul0(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		double double1 = Math.fma(this.m00, matrix4dc.m00(), Math.fma(this.m10, matrix4dc.m01(), Math.fma(this.m20, matrix4dc.m02(), this.m30 * matrix4dc.m03())));
		double double2 = Math.fma(this.m01, matrix4dc.m00(), Math.fma(this.m11, matrix4dc.m01(), Math.fma(this.m21, matrix4dc.m02(), this.m31 * matrix4dc.m03())));
		double double3 = Math.fma(this.m02, matrix4dc.m00(), Math.fma(this.m12, matrix4dc.m01(), Math.fma(this.m22, matrix4dc.m02(), this.m32 * matrix4dc.m03())));
		double double4 = Math.fma(this.m03, matrix4dc.m00(), Math.fma(this.m13, matrix4dc.m01(), Math.fma(this.m23, matrix4dc.m02(), this.m33 * matrix4dc.m03())));
		double double5 = Math.fma(this.m00, matrix4dc.m10(), Math.fma(this.m10, matrix4dc.m11(), Math.fma(this.m20, matrix4dc.m12(), this.m30 * matrix4dc.m13())));
		double double6 = Math.fma(this.m01, matrix4dc.m10(), Math.fma(this.m11, matrix4dc.m11(), Math.fma(this.m21, matrix4dc.m12(), this.m31 * matrix4dc.m13())));
		double double7 = Math.fma(this.m02, matrix4dc.m10(), Math.fma(this.m12, matrix4dc.m11(), Math.fma(this.m22, matrix4dc.m12(), this.m32 * matrix4dc.m13())));
		double double8 = Math.fma(this.m03, matrix4dc.m10(), Math.fma(this.m13, matrix4dc.m11(), Math.fma(this.m23, matrix4dc.m12(), this.m33 * matrix4dc.m13())));
		double double9 = Math.fma(this.m00, matrix4dc.m20(), Math.fma(this.m10, matrix4dc.m21(), Math.fma(this.m20, matrix4dc.m22(), this.m30 * matrix4dc.m23())));
		double double10 = Math.fma(this.m01, matrix4dc.m20(), Math.fma(this.m11, matrix4dc.m21(), Math.fma(this.m21, matrix4dc.m22(), this.m31 * matrix4dc.m23())));
		double double11 = Math.fma(this.m02, matrix4dc.m20(), Math.fma(this.m12, matrix4dc.m21(), Math.fma(this.m22, matrix4dc.m22(), this.m32 * matrix4dc.m23())));
		double double12 = Math.fma(this.m03, matrix4dc.m20(), Math.fma(this.m13, matrix4dc.m21(), Math.fma(this.m23, matrix4dc.m22(), this.m33 * matrix4dc.m23())));
		double double13 = Math.fma(this.m00, matrix4dc.m30(), Math.fma(this.m10, matrix4dc.m31(), Math.fma(this.m20, matrix4dc.m32(), this.m30 * matrix4dc.m33())));
		double double14 = Math.fma(this.m01, matrix4dc.m30(), Math.fma(this.m11, matrix4dc.m31(), Math.fma(this.m21, matrix4dc.m32(), this.m31 * matrix4dc.m33())));
		double double15 = Math.fma(this.m02, matrix4dc.m30(), Math.fma(this.m12, matrix4dc.m31(), Math.fma(this.m22, matrix4dc.m32(), this.m32 * matrix4dc.m33())));
		double double16 = Math.fma(this.m03, matrix4dc.m30(), Math.fma(this.m13, matrix4dc.m31(), Math.fma(this.m23, matrix4dc.m32(), this.m33 * matrix4dc.m33())));
		return matrix4d._m00(double1)._m01(double2)._m02(double3)._m03(double4)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m20(double9)._m21(double10)._m22(double11)._m23(double12)._m30(double13)._m31(double14)._m32(double15)._m33(double16)._properties(0);
	}

	public Matrix4d mul(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16) {
		return this.mul(double1, double2, double3, double4, double5, double6, double7, double8, double9, double10, double11, double12, double13, double14, double15, double16, this);
	}

	public Matrix4d mul(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.set(double1, double2, double3, double4, double5, double6, double7, double8, double9, double10, double11, double12, double13, double14, double15, double16);
		} else {
			return (this.properties & 2) != 0 ? this.mulAffineL(double1, double2, double3, double4, double5, double6, double7, double8, double9, double10, double11, double12, double13, double14, double15, double16, matrix4d) : this.mulGeneric(double1, double2, double3, double4, double5, double6, double7, double8, double9, double10, double11, double12, double13, double14, double15, double16, matrix4d);
		}
	}

	private Matrix4d mulAffineL(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16, Matrix4d matrix4d) {
		double double17 = Math.fma(this.m00, double1, Math.fma(this.m10, double2, Math.fma(this.m20, double3, this.m30 * double4)));
		double double18 = Math.fma(this.m01, double1, Math.fma(this.m11, double2, Math.fma(this.m21, double3, this.m31 * double4)));
		double double19 = Math.fma(this.m02, double1, Math.fma(this.m12, double2, Math.fma(this.m22, double3, this.m32 * double4)));
		double double20 = Math.fma(this.m00, double5, Math.fma(this.m10, double6, Math.fma(this.m20, double7, this.m30 * double8)));
		double double21 = Math.fma(this.m01, double5, Math.fma(this.m11, double6, Math.fma(this.m21, double7, this.m31 * double8)));
		double double22 = Math.fma(this.m02, double5, Math.fma(this.m12, double6, Math.fma(this.m22, double7, this.m32 * double8)));
		double double23 = Math.fma(this.m00, double9, Math.fma(this.m10, double10, Math.fma(this.m20, double11, this.m30 * double12)));
		double double24 = Math.fma(this.m01, double9, Math.fma(this.m11, double10, Math.fma(this.m21, double11, this.m31 * double12)));
		double double25 = Math.fma(this.m02, double9, Math.fma(this.m12, double10, Math.fma(this.m22, double11, this.m32 * double12)));
		double double26 = Math.fma(this.m00, double13, Math.fma(this.m10, double14, Math.fma(this.m20, double15, this.m30 * double16)));
		double double27 = Math.fma(this.m01, double13, Math.fma(this.m11, double14, Math.fma(this.m21, double15, this.m31 * double16)));
		double double28 = Math.fma(this.m02, double13, Math.fma(this.m12, double14, Math.fma(this.m22, double15, this.m32 * double16)));
		return matrix4d._m00(double17)._m01(double18)._m02(double19)._m03(double4)._m10(double20)._m11(double21)._m12(double22)._m13(double8)._m20(double23)._m21(double24)._m22(double25)._m23(double12)._m30(double26)._m31(double27)._m32(double28)._m33(double16)._properties(2);
	}

	private Matrix4d mulGeneric(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16, Matrix4d matrix4d) {
		double double17 = Math.fma(this.m00, double1, Math.fma(this.m10, double2, Math.fma(this.m20, double3, this.m30 * double4)));
		double double18 = Math.fma(this.m01, double1, Math.fma(this.m11, double2, Math.fma(this.m21, double3, this.m31 * double4)));
		double double19 = Math.fma(this.m02, double1, Math.fma(this.m12, double2, Math.fma(this.m22, double3, this.m32 * double4)));
		double double20 = Math.fma(this.m03, double1, Math.fma(this.m13, double2, Math.fma(this.m23, double3, this.m33 * double4)));
		double double21 = Math.fma(this.m00, double5, Math.fma(this.m10, double6, Math.fma(this.m20, double7, this.m30 * double8)));
		double double22 = Math.fma(this.m01, double5, Math.fma(this.m11, double6, Math.fma(this.m21, double7, this.m31 * double8)));
		double double23 = Math.fma(this.m02, double5, Math.fma(this.m12, double6, Math.fma(this.m22, double7, this.m32 * double8)));
		double double24 = Math.fma(this.m03, double5, Math.fma(this.m13, double6, Math.fma(this.m23, double7, this.m33 * double8)));
		double double25 = Math.fma(this.m00, double9, Math.fma(this.m10, double10, Math.fma(this.m20, double11, this.m30 * double12)));
		double double26 = Math.fma(this.m01, double9, Math.fma(this.m11, double10, Math.fma(this.m21, double11, this.m31 * double12)));
		double double27 = Math.fma(this.m02, double9, Math.fma(this.m12, double10, Math.fma(this.m22, double11, this.m32 * double12)));
		double double28 = Math.fma(this.m03, double9, Math.fma(this.m13, double10, Math.fma(this.m23, double11, this.m33 * double12)));
		double double29 = Math.fma(this.m00, double13, Math.fma(this.m10, double14, Math.fma(this.m20, double15, this.m30 * double16)));
		double double30 = Math.fma(this.m01, double13, Math.fma(this.m11, double14, Math.fma(this.m21, double15, this.m31 * double16)));
		double double31 = Math.fma(this.m02, double13, Math.fma(this.m12, double14, Math.fma(this.m22, double15, this.m32 * double16)));
		double double32 = Math.fma(this.m03, double13, Math.fma(this.m13, double14, Math.fma(this.m23, double15, this.m33 * double16)));
		return matrix4d._m00(double17)._m01(double18)._m02(double19)._m03(double20)._m10(double21)._m11(double22)._m12(double23)._m13(double24)._m20(double25)._m21(double26)._m22(double27)._m23(double28)._m30(double29)._m31(double30)._m32(double31)._m33(double32)._properties(0);
	}

	public Matrix4d mul3x3(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		return this.mul3x3(double1, double2, double3, double4, double5, double6, double7, double8, double9, this);
	}

	public Matrix4d mul3x3(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.set(double1, double2, double3, 0.0, double4, double5, double6, 0.0, double7, double8, double9, 0.0, 0.0, 0.0, 0.0, 1.0) : this.mulGeneric3x3(double1, double2, double3, double4, double5, double6, double7, double8, double9, matrix4d);
	}

	private Matrix4d mulGeneric3x3(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d) {
		double double10 = Math.fma(this.m00, double1, Math.fma(this.m10, double2, this.m20 * double3));
		double double11 = Math.fma(this.m01, double1, Math.fma(this.m11, double2, this.m21 * double3));
		double double12 = Math.fma(this.m02, double1, Math.fma(this.m12, double2, this.m22 * double3));
		double double13 = Math.fma(this.m03, double1, Math.fma(this.m13, double2, this.m23 * double3));
		double double14 = Math.fma(this.m00, double4, Math.fma(this.m10, double5, this.m20 * double6));
		double double15 = Math.fma(this.m01, double4, Math.fma(this.m11, double5, this.m21 * double6));
		double double16 = Math.fma(this.m02, double4, Math.fma(this.m12, double5, this.m22 * double6));
		double double17 = Math.fma(this.m03, double4, Math.fma(this.m13, double5, this.m23 * double6));
		double double18 = Math.fma(this.m00, double7, Math.fma(this.m10, double8, this.m20 * double9));
		double double19 = Math.fma(this.m01, double7, Math.fma(this.m11, double8, this.m21 * double9));
		double double20 = Math.fma(this.m02, double7, Math.fma(this.m12, double8, this.m22 * double9));
		double double21 = Math.fma(this.m03, double7, Math.fma(this.m13, double8, this.m23 * double9));
		return matrix4d._m00(double10)._m01(double11)._m02(double12)._m03(double13)._m10(double14)._m11(double15)._m12(double16)._m13(double17)._m20(double18)._m21(double19)._m22(double20)._m23(double21)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & 2);
	}

	public Matrix4d mulLocal(Matrix4dc matrix4dc) {
		return this.mulLocal(matrix4dc, this);
	}

	public Matrix4d mulLocal(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.set(matrix4dc);
		} else if ((matrix4dc.properties() & 4) != 0) {
			return matrix4d.set((Matrix4dc)this);
		} else {
			return (this.properties & 2) != 0 && (matrix4dc.properties() & 2) != 0 ? this.mulLocalAffine(matrix4dc, matrix4d) : this.mulLocalGeneric(matrix4dc, matrix4d);
		}
	}

	private Matrix4d mulLocalGeneric(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		double double1 = Math.fma(matrix4dc.m00(), this.m00, Math.fma(matrix4dc.m10(), this.m01, Math.fma(matrix4dc.m20(), this.m02, matrix4dc.m30() * this.m03)));
		double double2 = Math.fma(matrix4dc.m01(), this.m00, Math.fma(matrix4dc.m11(), this.m01, Math.fma(matrix4dc.m21(), this.m02, matrix4dc.m31() * this.m03)));
		double double3 = Math.fma(matrix4dc.m02(), this.m00, Math.fma(matrix4dc.m12(), this.m01, Math.fma(matrix4dc.m22(), this.m02, matrix4dc.m32() * this.m03)));
		double double4 = Math.fma(matrix4dc.m03(), this.m00, Math.fma(matrix4dc.m13(), this.m01, Math.fma(matrix4dc.m23(), this.m02, matrix4dc.m33() * this.m03)));
		double double5 = Math.fma(matrix4dc.m00(), this.m10, Math.fma(matrix4dc.m10(), this.m11, Math.fma(matrix4dc.m20(), this.m12, matrix4dc.m30() * this.m13)));
		double double6 = Math.fma(matrix4dc.m01(), this.m10, Math.fma(matrix4dc.m11(), this.m11, Math.fma(matrix4dc.m21(), this.m12, matrix4dc.m31() * this.m13)));
		double double7 = Math.fma(matrix4dc.m02(), this.m10, Math.fma(matrix4dc.m12(), this.m11, Math.fma(matrix4dc.m22(), this.m12, matrix4dc.m32() * this.m13)));
		double double8 = Math.fma(matrix4dc.m03(), this.m10, Math.fma(matrix4dc.m13(), this.m11, Math.fma(matrix4dc.m23(), this.m12, matrix4dc.m33() * this.m13)));
		double double9 = Math.fma(matrix4dc.m00(), this.m20, Math.fma(matrix4dc.m10(), this.m21, Math.fma(matrix4dc.m20(), this.m22, matrix4dc.m30() * this.m23)));
		double double10 = Math.fma(matrix4dc.m01(), this.m20, Math.fma(matrix4dc.m11(), this.m21, Math.fma(matrix4dc.m21(), this.m22, matrix4dc.m31() * this.m23)));
		double double11 = Math.fma(matrix4dc.m02(), this.m20, Math.fma(matrix4dc.m12(), this.m21, Math.fma(matrix4dc.m22(), this.m22, matrix4dc.m32() * this.m23)));
		double double12 = Math.fma(matrix4dc.m03(), this.m20, Math.fma(matrix4dc.m13(), this.m21, Math.fma(matrix4dc.m23(), this.m22, matrix4dc.m33() * this.m23)));
		double double13 = Math.fma(matrix4dc.m00(), this.m30, Math.fma(matrix4dc.m10(), this.m31, Math.fma(matrix4dc.m20(), this.m32, matrix4dc.m30() * this.m33)));
		double double14 = Math.fma(matrix4dc.m01(), this.m30, Math.fma(matrix4dc.m11(), this.m31, Math.fma(matrix4dc.m21(), this.m32, matrix4dc.m31() * this.m33)));
		double double15 = Math.fma(matrix4dc.m02(), this.m30, Math.fma(matrix4dc.m12(), this.m31, Math.fma(matrix4dc.m22(), this.m32, matrix4dc.m32() * this.m33)));
		double double16 = Math.fma(matrix4dc.m03(), this.m30, Math.fma(matrix4dc.m13(), this.m31, Math.fma(matrix4dc.m23(), this.m32, matrix4dc.m33() * this.m33)));
		return matrix4d._m00(double1)._m01(double2)._m02(double3)._m03(double4)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m20(double9)._m21(double10)._m22(double11)._m23(double12)._m30(double13)._m31(double14)._m32(double15)._m33(double16)._properties(0);
	}

	public Matrix4d mulLocalAffine(Matrix4dc matrix4dc) {
		return this.mulLocalAffine(matrix4dc, this);
	}

	public Matrix4d mulLocalAffine(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		double double1 = matrix4dc.m00() * this.m00 + matrix4dc.m10() * this.m01 + matrix4dc.m20() * this.m02;
		double double2 = matrix4dc.m01() * this.m00 + matrix4dc.m11() * this.m01 + matrix4dc.m21() * this.m02;
		double double3 = matrix4dc.m02() * this.m00 + matrix4dc.m12() * this.m01 + matrix4dc.m22() * this.m02;
		double double4 = matrix4dc.m03();
		double double5 = matrix4dc.m00() * this.m10 + matrix4dc.m10() * this.m11 + matrix4dc.m20() * this.m12;
		double double6 = matrix4dc.m01() * this.m10 + matrix4dc.m11() * this.m11 + matrix4dc.m21() * this.m12;
		double double7 = matrix4dc.m02() * this.m10 + matrix4dc.m12() * this.m11 + matrix4dc.m22() * this.m12;
		double double8 = matrix4dc.m13();
		double double9 = matrix4dc.m00() * this.m20 + matrix4dc.m10() * this.m21 + matrix4dc.m20() * this.m22;
		double double10 = matrix4dc.m01() * this.m20 + matrix4dc.m11() * this.m21 + matrix4dc.m21() * this.m22;
		double double11 = matrix4dc.m02() * this.m20 + matrix4dc.m12() * this.m21 + matrix4dc.m22() * this.m22;
		double double12 = matrix4dc.m23();
		double double13 = matrix4dc.m00() * this.m30 + matrix4dc.m10() * this.m31 + matrix4dc.m20() * this.m32 + matrix4dc.m30();
		double double14 = matrix4dc.m01() * this.m30 + matrix4dc.m11() * this.m31 + matrix4dc.m21() * this.m32 + matrix4dc.m31();
		double double15 = matrix4dc.m02() * this.m30 + matrix4dc.m12() * this.m31 + matrix4dc.m22() * this.m32 + matrix4dc.m32();
		double double16 = matrix4dc.m33();
		matrix4d._m00(double1)._m01(double2)._m02(double3)._m03(double4)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m20(double9)._m21(double10)._m22(double11)._m23(double12)._m30(double13)._m31(double14)._m32(double15)._m33(double16)._properties(2);
		return matrix4d;
	}

	public Matrix4d mul(Matrix4x3dc matrix4x3dc) {
		return this.mul(matrix4x3dc, this);
	}

	public Matrix4d mul(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.set(matrix4x3dc);
		} else if ((matrix4x3dc.properties() & 4) != 0) {
			return matrix4d.set((Matrix4dc)this);
		} else if ((this.properties & 8) != 0) {
			return this.mulTranslation(matrix4x3dc, matrix4d);
		} else if ((this.properties & 2) != 0) {
			return this.mulAffine(matrix4x3dc, matrix4d);
		} else {
			return (this.properties & 1) != 0 ? this.mulPerspectiveAffine(matrix4x3dc, matrix4d) : this.mulGeneric(matrix4x3dc, matrix4d);
		}
	}

	private Matrix4d mulTranslation(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d) {
		return matrix4d._m00(matrix4x3dc.m00())._m01(matrix4x3dc.m01())._m02(matrix4x3dc.m02())._m03(this.m03)._m10(matrix4x3dc.m10())._m11(matrix4x3dc.m11())._m12(matrix4x3dc.m12())._m13(this.m13)._m20(matrix4x3dc.m20())._m21(matrix4x3dc.m21())._m22(matrix4x3dc.m22())._m23(this.m23)._m30(matrix4x3dc.m30() + this.m30)._m31(matrix4x3dc.m31() + this.m31)._m32(matrix4x3dc.m32() + this.m32)._m33(this.m33)._properties(2 | matrix4x3dc.properties() & 16);
	}

	private Matrix4d mulAffine(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d) {
		double double1 = this.m00;
		double double2 = this.m01;
		double double3 = this.m02;
		double double4 = this.m10;
		double double5 = this.m11;
		double double6 = this.m12;
		double double7 = this.m20;
		double double8 = this.m21;
		double double9 = this.m22;
		double double10 = matrix4x3dc.m00();
		double double11 = matrix4x3dc.m01();
		double double12 = matrix4x3dc.m02();
		double double13 = matrix4x3dc.m10();
		double double14 = matrix4x3dc.m11();
		double double15 = matrix4x3dc.m12();
		double double16 = matrix4x3dc.m20();
		double double17 = matrix4x3dc.m21();
		double double18 = matrix4x3dc.m22();
		double double19 = matrix4x3dc.m30();
		double double20 = matrix4x3dc.m31();
		double double21 = matrix4x3dc.m32();
		return matrix4d._m00(Math.fma(double1, double10, Math.fma(double4, double11, double7 * double12)))._m01(Math.fma(double2, double10, Math.fma(double5, double11, double8 * double12)))._m02(Math.fma(double3, double10, Math.fma(double6, double11, double9 * double12)))._m03(this.m03)._m10(Math.fma(double1, double13, Math.fma(double4, double14, double7 * double15)))._m11(Math.fma(double2, double13, Math.fma(double5, double14, double8 * double15)))._m12(Math.fma(double3, double13, Math.fma(double6, double14, double9 * double15)))._m13(this.m13)._m20(Math.fma(double1, double16, Math.fma(double4, double17, double7 * double18)))._m21(Math.fma(double2, double16, Math.fma(double5, double17, double8 * double18)))._m22(Math.fma(double3, double16, Math.fma(double6, double17, double9 * double18)))._m23(this.m23)._m30(Math.fma(double1, double19, Math.fma(double4, double20, Math.fma(double7, double21, this.m30))))._m31(Math.fma(double2, double19, Math.fma(double5, double20, Math.fma(double8, double21, this.m31))))._m32(Math.fma(double3, double19, Math.fma(double6, double20, Math.fma(double9, double21, this.m32))))._m33(this.m33)._properties(2 | this.properties & matrix4x3dc.properties() & 16);
	}

	private Matrix4d mulGeneric(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d) {
		double double1 = Math.fma(this.m00, matrix4x3dc.m00(), Math.fma(this.m10, matrix4x3dc.m01(), this.m20 * matrix4x3dc.m02()));
		double double2 = Math.fma(this.m01, matrix4x3dc.m00(), Math.fma(this.m11, matrix4x3dc.m01(), this.m21 * matrix4x3dc.m02()));
		double double3 = Math.fma(this.m02, matrix4x3dc.m00(), Math.fma(this.m12, matrix4x3dc.m01(), this.m22 * matrix4x3dc.m02()));
		double double4 = Math.fma(this.m03, matrix4x3dc.m00(), Math.fma(this.m13, matrix4x3dc.m01(), this.m23 * matrix4x3dc.m02()));
		double double5 = Math.fma(this.m00, matrix4x3dc.m10(), Math.fma(this.m10, matrix4x3dc.m11(), this.m20 * matrix4x3dc.m12()));
		double double6 = Math.fma(this.m01, matrix4x3dc.m10(), Math.fma(this.m11, matrix4x3dc.m11(), this.m21 * matrix4x3dc.m12()));
		double double7 = Math.fma(this.m02, matrix4x3dc.m10(), Math.fma(this.m12, matrix4x3dc.m11(), this.m22 * matrix4x3dc.m12()));
		double double8 = Math.fma(this.m03, matrix4x3dc.m10(), Math.fma(this.m13, matrix4x3dc.m11(), this.m23 * matrix4x3dc.m12()));
		double double9 = Math.fma(this.m00, matrix4x3dc.m20(), Math.fma(this.m10, matrix4x3dc.m21(), this.m20 * matrix4x3dc.m22()));
		double double10 = Math.fma(this.m01, matrix4x3dc.m20(), Math.fma(this.m11, matrix4x3dc.m21(), this.m21 * matrix4x3dc.m22()));
		double double11 = Math.fma(this.m02, matrix4x3dc.m20(), Math.fma(this.m12, matrix4x3dc.m21(), this.m22 * matrix4x3dc.m22()));
		double double12 = Math.fma(this.m03, matrix4x3dc.m20(), Math.fma(this.m13, matrix4x3dc.m21(), this.m23 * matrix4x3dc.m22()));
		double double13 = Math.fma(this.m00, matrix4x3dc.m30(), Math.fma(this.m10, matrix4x3dc.m31(), Math.fma(this.m20, matrix4x3dc.m32(), this.m30)));
		double double14 = Math.fma(this.m01, matrix4x3dc.m30(), Math.fma(this.m11, matrix4x3dc.m31(), Math.fma(this.m21, matrix4x3dc.m32(), this.m31)));
		double double15 = Math.fma(this.m02, matrix4x3dc.m30(), Math.fma(this.m12, matrix4x3dc.m31(), Math.fma(this.m22, matrix4x3dc.m32(), this.m32)));
		double double16 = Math.fma(this.m03, matrix4x3dc.m30(), Math.fma(this.m13, matrix4x3dc.m31(), Math.fma(this.m23, matrix4x3dc.m32(), this.m33)));
		matrix4d._m00(double1)._m01(double2)._m02(double3)._m03(double4)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m20(double9)._m21(double10)._m22(double11)._m23(double12)._m30(double13)._m31(double14)._m32(double15)._m33(double16)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d mulPerspectiveAffine(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d) {
		double double1 = this.m00;
		double double2 = this.m11;
		double double3 = this.m22;
		double double4 = this.m23;
		matrix4d._m00(double1 * matrix4x3dc.m00())._m01(double2 * matrix4x3dc.m01())._m02(double3 * matrix4x3dc.m02())._m03(double4 * matrix4x3dc.m02())._m10(double1 * matrix4x3dc.m10())._m11(double2 * matrix4x3dc.m11())._m12(double3 * matrix4x3dc.m12())._m13(double4 * matrix4x3dc.m12())._m20(double1 * matrix4x3dc.m20())._m21(double2 * matrix4x3dc.m21())._m22(double3 * matrix4x3dc.m22())._m23(double4 * matrix4x3dc.m22())._m30(double1 * matrix4x3dc.m30())._m31(double2 * matrix4x3dc.m31())._m32(double3 * matrix4x3dc.m32() + this.m32)._m33(double4 * matrix4x3dc.m32())._properties(0);
		return matrix4d;
	}

	public Matrix4d mul(Matrix4x3fc matrix4x3fc, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.set(matrix4x3fc);
		} else {
			return (matrix4x3fc.properties() & 4) != 0 ? matrix4d.set((Matrix4dc)this) : this.mulGeneric(matrix4x3fc, matrix4d);
		}
	}

	private Matrix4d mulGeneric(Matrix4x3fc matrix4x3fc, Matrix4d matrix4d) {
		double double1 = Math.fma(this.m00, (double)matrix4x3fc.m00(), Math.fma(this.m10, (double)matrix4x3fc.m01(), this.m20 * (double)matrix4x3fc.m02()));
		double double2 = Math.fma(this.m01, (double)matrix4x3fc.m00(), Math.fma(this.m11, (double)matrix4x3fc.m01(), this.m21 * (double)matrix4x3fc.m02()));
		double double3 = Math.fma(this.m02, (double)matrix4x3fc.m00(), Math.fma(this.m12, (double)matrix4x3fc.m01(), this.m22 * (double)matrix4x3fc.m02()));
		double double4 = Math.fma(this.m03, (double)matrix4x3fc.m00(), Math.fma(this.m13, (double)matrix4x3fc.m01(), this.m23 * (double)matrix4x3fc.m02()));
		double double5 = Math.fma(this.m00, (double)matrix4x3fc.m10(), Math.fma(this.m10, (double)matrix4x3fc.m11(), this.m20 * (double)matrix4x3fc.m12()));
		double double6 = Math.fma(this.m01, (double)matrix4x3fc.m10(), Math.fma(this.m11, (double)matrix4x3fc.m11(), this.m21 * (double)matrix4x3fc.m12()));
		double double7 = Math.fma(this.m02, (double)matrix4x3fc.m10(), Math.fma(this.m12, (double)matrix4x3fc.m11(), this.m22 * (double)matrix4x3fc.m12()));
		double double8 = Math.fma(this.m03, (double)matrix4x3fc.m10(), Math.fma(this.m13, (double)matrix4x3fc.m11(), this.m23 * (double)matrix4x3fc.m12()));
		double double9 = Math.fma(this.m00, (double)matrix4x3fc.m20(), Math.fma(this.m10, (double)matrix4x3fc.m21(), this.m20 * (double)matrix4x3fc.m22()));
		double double10 = Math.fma(this.m01, (double)matrix4x3fc.m20(), Math.fma(this.m11, (double)matrix4x3fc.m21(), this.m21 * (double)matrix4x3fc.m22()));
		double double11 = Math.fma(this.m02, (double)matrix4x3fc.m20(), Math.fma(this.m12, (double)matrix4x3fc.m21(), this.m22 * (double)matrix4x3fc.m22()));
		double double12 = Math.fma(this.m03, (double)matrix4x3fc.m20(), Math.fma(this.m13, (double)matrix4x3fc.m21(), this.m23 * (double)matrix4x3fc.m22()));
		double double13 = Math.fma(this.m00, (double)matrix4x3fc.m30(), Math.fma(this.m10, (double)matrix4x3fc.m31(), Math.fma(this.m20, (double)matrix4x3fc.m32(), this.m30)));
		double double14 = Math.fma(this.m01, (double)matrix4x3fc.m30(), Math.fma(this.m11, (double)matrix4x3fc.m31(), Math.fma(this.m21, (double)matrix4x3fc.m32(), this.m31)));
		double double15 = Math.fma(this.m02, (double)matrix4x3fc.m30(), Math.fma(this.m12, (double)matrix4x3fc.m31(), Math.fma(this.m22, (double)matrix4x3fc.m32(), this.m32)));
		double double16 = Math.fma(this.m03, (double)matrix4x3fc.m30(), Math.fma(this.m13, (double)matrix4x3fc.m31(), Math.fma(this.m23, (double)matrix4x3fc.m32(), this.m33)));
		matrix4d._m00(double1)._m01(double2)._m02(double3)._m03(double4)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m20(double9)._m21(double10)._m22(double11)._m23(double12)._m30(double13)._m31(double14)._m32(double15)._m33(double16)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d mul(Matrix3x2dc matrix3x2dc) {
		return this.mul(matrix3x2dc, this);
	}

	public Matrix4d mul(Matrix3x2dc matrix3x2dc, Matrix4d matrix4d) {
		double double1 = this.m00 * matrix3x2dc.m00() + this.m10 * matrix3x2dc.m01();
		double double2 = this.m01 * matrix3x2dc.m00() + this.m11 * matrix3x2dc.m01();
		double double3 = this.m02 * matrix3x2dc.m00() + this.m12 * matrix3x2dc.m01();
		double double4 = this.m03 * matrix3x2dc.m00() + this.m13 * matrix3x2dc.m01();
		double double5 = this.m00 * matrix3x2dc.m10() + this.m10 * matrix3x2dc.m11();
		double double6 = this.m01 * matrix3x2dc.m10() + this.m11 * matrix3x2dc.m11();
		double double7 = this.m02 * matrix3x2dc.m10() + this.m12 * matrix3x2dc.m11();
		double double8 = this.m03 * matrix3x2dc.m10() + this.m13 * matrix3x2dc.m11();
		double double9 = this.m00 * matrix3x2dc.m20() + this.m10 * matrix3x2dc.m21() + this.m30;
		double double10 = this.m01 * matrix3x2dc.m20() + this.m11 * matrix3x2dc.m21() + this.m31;
		double double11 = this.m02 * matrix3x2dc.m20() + this.m12 * matrix3x2dc.m21() + this.m32;
		double double12 = this.m03 * matrix3x2dc.m20() + this.m13 * matrix3x2dc.m21() + this.m33;
		matrix4d._m00(double1)._m01(double2)._m02(double3)._m03(double4)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m20(this.m20)._m21(this.m21)._m22(this.m22)._m23(this.m23)._m30(double9)._m31(double10)._m32(double11)._m33(double12)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d mul(Matrix3x2fc matrix3x2fc) {
		return this.mul(matrix3x2fc, this);
	}

	public Matrix4d mul(Matrix3x2fc matrix3x2fc, Matrix4d matrix4d) {
		double double1 = this.m00 * (double)matrix3x2fc.m00() + this.m10 * (double)matrix3x2fc.m01();
		double double2 = this.m01 * (double)matrix3x2fc.m00() + this.m11 * (double)matrix3x2fc.m01();
		double double3 = this.m02 * (double)matrix3x2fc.m00() + this.m12 * (double)matrix3x2fc.m01();
		double double4 = this.m03 * (double)matrix3x2fc.m00() + this.m13 * (double)matrix3x2fc.m01();
		double double5 = this.m00 * (double)matrix3x2fc.m10() + this.m10 * (double)matrix3x2fc.m11();
		double double6 = this.m01 * (double)matrix3x2fc.m10() + this.m11 * (double)matrix3x2fc.m11();
		double double7 = this.m02 * (double)matrix3x2fc.m10() + this.m12 * (double)matrix3x2fc.m11();
		double double8 = this.m03 * (double)matrix3x2fc.m10() + this.m13 * (double)matrix3x2fc.m11();
		double double9 = this.m00 * (double)matrix3x2fc.m20() + this.m10 * (double)matrix3x2fc.m21() + this.m30;
		double double10 = this.m01 * (double)matrix3x2fc.m20() + this.m11 * (double)matrix3x2fc.m21() + this.m31;
		double double11 = this.m02 * (double)matrix3x2fc.m20() + this.m12 * (double)matrix3x2fc.m21() + this.m32;
		double double12 = this.m03 * (double)matrix3x2fc.m20() + this.m13 * (double)matrix3x2fc.m21() + this.m33;
		matrix4d._m00(double1)._m01(double2)._m02(double3)._m03(double4)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m20(this.m20)._m21(this.m21)._m22(this.m22)._m23(this.m23)._m30(double9)._m31(double10)._m32(double11)._m33(double12)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d mul(Matrix4f matrix4f) {
		return this.mul((Matrix4fc)matrix4f, this);
	}

	public Matrix4d mul(Matrix4fc matrix4fc, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.set(matrix4fc);
		} else {
			return (matrix4fc.properties() & 4) != 0 ? matrix4d.set((Matrix4dc)this) : this.mulGeneric(matrix4fc, matrix4d);
		}
	}

	private Matrix4d mulGeneric(Matrix4fc matrix4fc, Matrix4d matrix4d) {
		double double1 = this.m00 * (double)matrix4fc.m00() + this.m10 * (double)matrix4fc.m01() + this.m20 * (double)matrix4fc.m02() + this.m30 * (double)matrix4fc.m03();
		double double2 = this.m01 * (double)matrix4fc.m00() + this.m11 * (double)matrix4fc.m01() + this.m21 * (double)matrix4fc.m02() + this.m31 * (double)matrix4fc.m03();
		double double3 = this.m02 * (double)matrix4fc.m00() + this.m12 * (double)matrix4fc.m01() + this.m22 * (double)matrix4fc.m02() + this.m32 * (double)matrix4fc.m03();
		double double4 = this.m03 * (double)matrix4fc.m00() + this.m13 * (double)matrix4fc.m01() + this.m23 * (double)matrix4fc.m02() + this.m33 * (double)matrix4fc.m03();
		double double5 = this.m00 * (double)matrix4fc.m10() + this.m10 * (double)matrix4fc.m11() + this.m20 * (double)matrix4fc.m12() + this.m30 * (double)matrix4fc.m13();
		double double6 = this.m01 * (double)matrix4fc.m10() + this.m11 * (double)matrix4fc.m11() + this.m21 * (double)matrix4fc.m12() + this.m31 * (double)matrix4fc.m13();
		double double7 = this.m02 * (double)matrix4fc.m10() + this.m12 * (double)matrix4fc.m11() + this.m22 * (double)matrix4fc.m12() + this.m32 * (double)matrix4fc.m13();
		double double8 = this.m03 * (double)matrix4fc.m10() + this.m13 * (double)matrix4fc.m11() + this.m23 * (double)matrix4fc.m12() + this.m33 * (double)matrix4fc.m13();
		double double9 = this.m00 * (double)matrix4fc.m20() + this.m10 * (double)matrix4fc.m21() + this.m20 * (double)matrix4fc.m22() + this.m30 * (double)matrix4fc.m23();
		double double10 = this.m01 * (double)matrix4fc.m20() + this.m11 * (double)matrix4fc.m21() + this.m21 * (double)matrix4fc.m22() + this.m31 * (double)matrix4fc.m23();
		double double11 = this.m02 * (double)matrix4fc.m20() + this.m12 * (double)matrix4fc.m21() + this.m22 * (double)matrix4fc.m22() + this.m32 * (double)matrix4fc.m23();
		double double12 = this.m03 * (double)matrix4fc.m20() + this.m13 * (double)matrix4fc.m21() + this.m23 * (double)matrix4fc.m22() + this.m33 * (double)matrix4fc.m23();
		double double13 = this.m00 * (double)matrix4fc.m30() + this.m10 * (double)matrix4fc.m31() + this.m20 * (double)matrix4fc.m32() + this.m30 * (double)matrix4fc.m33();
		double double14 = this.m01 * (double)matrix4fc.m30() + this.m11 * (double)matrix4fc.m31() + this.m21 * (double)matrix4fc.m32() + this.m31 * (double)matrix4fc.m33();
		double double15 = this.m02 * (double)matrix4fc.m30() + this.m12 * (double)matrix4fc.m31() + this.m22 * (double)matrix4fc.m32() + this.m32 * (double)matrix4fc.m33();
		double double16 = this.m03 * (double)matrix4fc.m30() + this.m13 * (double)matrix4fc.m31() + this.m23 * (double)matrix4fc.m32() + this.m33 * (double)matrix4fc.m33();
		matrix4d._m00(double1)._m01(double2)._m02(double3)._m03(double4)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m20(double9)._m21(double10)._m22(double11)._m23(double12)._m30(double13)._m31(double14)._m32(double15)._m33(double16)._properties(0);
		return matrix4d;
	}

	public Matrix4d mulPerspectiveAffine(Matrix4dc matrix4dc) {
		return this.mulPerspectiveAffine(matrix4dc, this);
	}

	public Matrix4d mulPerspectiveAffine(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		double double1 = this.m00 * matrix4dc.m00();
		double double2 = this.m11 * matrix4dc.m01();
		double double3 = this.m22 * matrix4dc.m02();
		double double4 = this.m23 * matrix4dc.m02();
		double double5 = this.m00 * matrix4dc.m10();
		double double6 = this.m11 * matrix4dc.m11();
		double double7 = this.m22 * matrix4dc.m12();
		double double8 = this.m23 * matrix4dc.m12();
		double double9 = this.m00 * matrix4dc.m20();
		double double10 = this.m11 * matrix4dc.m21();
		double double11 = this.m22 * matrix4dc.m22();
		double double12 = this.m23 * matrix4dc.m22();
		double double13 = this.m00 * matrix4dc.m30();
		double double14 = this.m11 * matrix4dc.m31();
		double double15 = this.m22 * matrix4dc.m32() + this.m32;
		double double16 = this.m23 * matrix4dc.m32();
		return matrix4d._m00(double1)._m01(double2)._m02(double3)._m03(double4)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m20(double9)._m21(double10)._m22(double11)._m23(double12)._m30(double13)._m31(double14)._m32(double15)._m33(double16)._properties(0);
	}

	public Matrix4d mulAffineR(Matrix4dc matrix4dc) {
		return this.mulAffineR(matrix4dc, this);
	}

	public Matrix4d mulAffineR(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		double double1 = Math.fma(this.m00, matrix4dc.m00(), Math.fma(this.m10, matrix4dc.m01(), this.m20 * matrix4dc.m02()));
		double double2 = Math.fma(this.m01, matrix4dc.m00(), Math.fma(this.m11, matrix4dc.m01(), this.m21 * matrix4dc.m02()));
		double double3 = Math.fma(this.m02, matrix4dc.m00(), Math.fma(this.m12, matrix4dc.m01(), this.m22 * matrix4dc.m02()));
		double double4 = Math.fma(this.m03, matrix4dc.m00(), Math.fma(this.m13, matrix4dc.m01(), this.m23 * matrix4dc.m02()));
		double double5 = Math.fma(this.m00, matrix4dc.m10(), Math.fma(this.m10, matrix4dc.m11(), this.m20 * matrix4dc.m12()));
		double double6 = Math.fma(this.m01, matrix4dc.m10(), Math.fma(this.m11, matrix4dc.m11(), this.m21 * matrix4dc.m12()));
		double double7 = Math.fma(this.m02, matrix4dc.m10(), Math.fma(this.m12, matrix4dc.m11(), this.m22 * matrix4dc.m12()));
		double double8 = Math.fma(this.m03, matrix4dc.m10(), Math.fma(this.m13, matrix4dc.m11(), this.m23 * matrix4dc.m12()));
		double double9 = Math.fma(this.m00, matrix4dc.m20(), Math.fma(this.m10, matrix4dc.m21(), this.m20 * matrix4dc.m22()));
		double double10 = Math.fma(this.m01, matrix4dc.m20(), Math.fma(this.m11, matrix4dc.m21(), this.m21 * matrix4dc.m22()));
		double double11 = Math.fma(this.m02, matrix4dc.m20(), Math.fma(this.m12, matrix4dc.m21(), this.m22 * matrix4dc.m22()));
		double double12 = Math.fma(this.m03, matrix4dc.m20(), Math.fma(this.m13, matrix4dc.m21(), this.m23 * matrix4dc.m22()));
		double double13 = Math.fma(this.m00, matrix4dc.m30(), Math.fma(this.m10, matrix4dc.m31(), Math.fma(this.m20, matrix4dc.m32(), this.m30)));
		double double14 = Math.fma(this.m01, matrix4dc.m30(), Math.fma(this.m11, matrix4dc.m31(), Math.fma(this.m21, matrix4dc.m32(), this.m31)));
		double double15 = Math.fma(this.m02, matrix4dc.m30(), Math.fma(this.m12, matrix4dc.m31(), Math.fma(this.m22, matrix4dc.m32(), this.m32)));
		double double16 = Math.fma(this.m03, matrix4dc.m30(), Math.fma(this.m13, matrix4dc.m31(), Math.fma(this.m23, matrix4dc.m32(), this.m33)));
		matrix4d._m00(double1)._m01(double2)._m02(double3)._m03(double4)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m20(double9)._m21(double10)._m22(double11)._m23(double12)._m30(double13)._m31(double14)._m32(double15)._m33(double16)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d mulAffine(Matrix4dc matrix4dc) {
		return this.mulAffine(matrix4dc, this);
	}

	public Matrix4d mulAffine(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		double double1 = this.m00;
		double double2 = this.m01;
		double double3 = this.m02;
		double double4 = this.m10;
		double double5 = this.m11;
		double double6 = this.m12;
		double double7 = this.m20;
		double double8 = this.m21;
		double double9 = this.m22;
		double double10 = matrix4dc.m00();
		double double11 = matrix4dc.m01();
		double double12 = matrix4dc.m02();
		double double13 = matrix4dc.m10();
		double double14 = matrix4dc.m11();
		double double15 = matrix4dc.m12();
		double double16 = matrix4dc.m20();
		double double17 = matrix4dc.m21();
		double double18 = matrix4dc.m22();
		double double19 = matrix4dc.m30();
		double double20 = matrix4dc.m31();
		double double21 = matrix4dc.m32();
		return matrix4d._m00(Math.fma(double1, double10, Math.fma(double4, double11, double7 * double12)))._m01(Math.fma(double2, double10, Math.fma(double5, double11, double8 * double12)))._m02(Math.fma(double3, double10, Math.fma(double6, double11, double9 * double12)))._m03(this.m03)._m10(Math.fma(double1, double13, Math.fma(double4, double14, double7 * double15)))._m11(Math.fma(double2, double13, Math.fma(double5, double14, double8 * double15)))._m12(Math.fma(double3, double13, Math.fma(double6, double14, double9 * double15)))._m13(this.m13)._m20(Math.fma(double1, double16, Math.fma(double4, double17, double7 * double18)))._m21(Math.fma(double2, double16, Math.fma(double5, double17, double8 * double18)))._m22(Math.fma(double3, double16, Math.fma(double6, double17, double9 * double18)))._m23(this.m23)._m30(Math.fma(double1, double19, Math.fma(double4, double20, Math.fma(double7, double21, this.m30))))._m31(Math.fma(double2, double19, Math.fma(double5, double20, Math.fma(double8, double21, this.m31))))._m32(Math.fma(double3, double19, Math.fma(double6, double20, Math.fma(double9, double21, this.m32))))._m33(this.m33)._properties(2 | this.properties & matrix4dc.properties() & 16);
	}

	public Matrix4d mulTranslationAffine(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		return matrix4d._m00(matrix4dc.m00())._m01(matrix4dc.m01())._m02(matrix4dc.m02())._m03(this.m03)._m10(matrix4dc.m10())._m11(matrix4dc.m11())._m12(matrix4dc.m12())._m13(this.m13)._m20(matrix4dc.m20())._m21(matrix4dc.m21())._m22(matrix4dc.m22())._m23(this.m23)._m30(matrix4dc.m30() + this.m30)._m31(matrix4dc.m31() + this.m31)._m32(matrix4dc.m32() + this.m32)._m33(this.m33)._properties(2 | matrix4dc.properties() & 16);
	}

	public Matrix4d mulOrthoAffine(Matrix4dc matrix4dc) {
		return this.mulOrthoAffine(matrix4dc, this);
	}

	public Matrix4d mulOrthoAffine(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		double double1 = this.m00 * matrix4dc.m00();
		double double2 = this.m11 * matrix4dc.m01();
		double double3 = this.m22 * matrix4dc.m02();
		double double4 = 0.0;
		double double5 = this.m00 * matrix4dc.m10();
		double double6 = this.m11 * matrix4dc.m11();
		double double7 = this.m22 * matrix4dc.m12();
		double double8 = 0.0;
		double double9 = this.m00 * matrix4dc.m20();
		double double10 = this.m11 * matrix4dc.m21();
		double double11 = this.m22 * matrix4dc.m22();
		double double12 = 0.0;
		double double13 = this.m00 * matrix4dc.m30() + this.m30;
		double double14 = this.m11 * matrix4dc.m31() + this.m31;
		double double15 = this.m22 * matrix4dc.m32() + this.m32;
		double double16 = 1.0;
		matrix4d._m00(double1)._m01(double2)._m02(double3)._m03(double4)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m20(double9)._m21(double10)._m22(double11)._m23(double12)._m30(double13)._m31(double14)._m32(double15)._m33(double16)._properties(2);
		return matrix4d;
	}

	public Matrix4d fma4x3(Matrix4dc matrix4dc, double double1) {
		return this.fma4x3(matrix4dc, double1, this);
	}

	public Matrix4d fma4x3(Matrix4dc matrix4dc, double double1, Matrix4d matrix4d) {
		matrix4d._m00(Math.fma(matrix4dc.m00(), double1, this.m00))._m01(Math.fma(matrix4dc.m01(), double1, this.m01))._m02(Math.fma(matrix4dc.m02(), double1, this.m02))._m03(this.m03)._m10(Math.fma(matrix4dc.m10(), double1, this.m10))._m11(Math.fma(matrix4dc.m11(), double1, this.m11))._m12(Math.fma(matrix4dc.m12(), double1, this.m12))._m13(this.m13)._m20(Math.fma(matrix4dc.m20(), double1, this.m20))._m21(Math.fma(matrix4dc.m21(), double1, this.m21))._m22(Math.fma(matrix4dc.m22(), double1, this.m22))._m23(this.m23)._m30(Math.fma(matrix4dc.m30(), double1, this.m30))._m31(Math.fma(matrix4dc.m31(), double1, this.m31))._m32(Math.fma(matrix4dc.m32(), double1, this.m32))._m33(this.m33)._properties(0);
		return matrix4d;
	}

	public Matrix4d add(Matrix4dc matrix4dc) {
		return this.add(matrix4dc, this);
	}

	public Matrix4d add(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		matrix4d._m00(this.m00 + matrix4dc.m00())._m01(this.m01 + matrix4dc.m01())._m02(this.m02 + matrix4dc.m02())._m03(this.m03 + matrix4dc.m03())._m10(this.m10 + matrix4dc.m10())._m11(this.m11 + matrix4dc.m11())._m12(this.m12 + matrix4dc.m12())._m13(this.m13 + matrix4dc.m13())._m20(this.m20 + matrix4dc.m20())._m21(this.m21 + matrix4dc.m21())._m22(this.m22 + matrix4dc.m22())._m23(this.m23 + matrix4dc.m23())._m30(this.m30 + matrix4dc.m30())._m31(this.m31 + matrix4dc.m31())._m32(this.m32 + matrix4dc.m32())._m33(this.m33 + matrix4dc.m33())._properties(0);
		return matrix4d;
	}

	public Matrix4d sub(Matrix4dc matrix4dc) {
		return this.sub(matrix4dc, this);
	}

	public Matrix4d sub(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		matrix4d._m00(this.m00 - matrix4dc.m00())._m01(this.m01 - matrix4dc.m01())._m02(this.m02 - matrix4dc.m02())._m03(this.m03 - matrix4dc.m03())._m10(this.m10 - matrix4dc.m10())._m11(this.m11 - matrix4dc.m11())._m12(this.m12 - matrix4dc.m12())._m13(this.m13 - matrix4dc.m13())._m20(this.m20 - matrix4dc.m20())._m21(this.m21 - matrix4dc.m21())._m22(this.m22 - matrix4dc.m22())._m23(this.m23 - matrix4dc.m23())._m30(this.m30 - matrix4dc.m30())._m31(this.m31 - matrix4dc.m31())._m32(this.m32 - matrix4dc.m32())._m33(this.m33 - matrix4dc.m33())._properties(0);
		return matrix4d;
	}

	public Matrix4d mulComponentWise(Matrix4dc matrix4dc) {
		return this.mulComponentWise(matrix4dc, this);
	}

	public Matrix4d mulComponentWise(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		matrix4d._m00(this.m00 * matrix4dc.m00())._m01(this.m01 * matrix4dc.m01())._m02(this.m02 * matrix4dc.m02())._m03(this.m03 * matrix4dc.m03())._m10(this.m10 * matrix4dc.m10())._m11(this.m11 * matrix4dc.m11())._m12(this.m12 * matrix4dc.m12())._m13(this.m13 * matrix4dc.m13())._m20(this.m20 * matrix4dc.m20())._m21(this.m21 * matrix4dc.m21())._m22(this.m22 * matrix4dc.m22())._m23(this.m23 * matrix4dc.m23())._m30(this.m30 * matrix4dc.m30())._m31(this.m31 * matrix4dc.m31())._m32(this.m32 * matrix4dc.m32())._m33(this.m33 * matrix4dc.m33())._properties(0);
		return matrix4d;
	}

	public Matrix4d add4x3(Matrix4dc matrix4dc) {
		return this.add4x3(matrix4dc, this);
	}

	public Matrix4d add4x3(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		matrix4d._m00(this.m00 + matrix4dc.m00())._m01(this.m01 + matrix4dc.m01())._m02(this.m02 + matrix4dc.m02())._m03(this.m03)._m10(this.m10 + matrix4dc.m10())._m11(this.m11 + matrix4dc.m11())._m12(this.m12 + matrix4dc.m12())._m13(this.m13)._m20(this.m20 + matrix4dc.m20())._m21(this.m21 + matrix4dc.m21())._m22(this.m22 + matrix4dc.m22())._m23(this.m23)._m30(this.m30 + matrix4dc.m30())._m31(this.m31 + matrix4dc.m31())._m32(this.m32 + matrix4dc.m32())._m33(this.m33)._properties(0);
		return matrix4d;
	}

	public Matrix4d add4x3(Matrix4fc matrix4fc) {
		return this.add4x3(matrix4fc, this);
	}

	public Matrix4d add4x3(Matrix4fc matrix4fc, Matrix4d matrix4d) {
		matrix4d._m00(this.m00 + (double)matrix4fc.m00())._m01(this.m01 + (double)matrix4fc.m01())._m02(this.m02 + (double)matrix4fc.m02())._m03(this.m03)._m10(this.m10 + (double)matrix4fc.m10())._m11(this.m11 + (double)matrix4fc.m11())._m12(this.m12 + (double)matrix4fc.m12())._m13(this.m13)._m20(this.m20 + (double)matrix4fc.m20())._m21(this.m21 + (double)matrix4fc.m21())._m22(this.m22 + (double)matrix4fc.m22())._m23(this.m23)._m30(this.m30 + (double)matrix4fc.m30())._m31(this.m31 + (double)matrix4fc.m31())._m32(this.m32 + (double)matrix4fc.m32())._m33(this.m33)._properties(0);
		return matrix4d;
	}

	public Matrix4d sub4x3(Matrix4dc matrix4dc) {
		return this.sub4x3(matrix4dc, this);
	}

	public Matrix4d sub4x3(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		matrix4d._m00(this.m00 - matrix4dc.m00())._m01(this.m01 - matrix4dc.m01())._m02(this.m02 - matrix4dc.m02())._m03(this.m03)._m10(this.m10 - matrix4dc.m10())._m11(this.m11 - matrix4dc.m11())._m12(this.m12 - matrix4dc.m12())._m13(this.m13)._m20(this.m20 - matrix4dc.m20())._m21(this.m21 - matrix4dc.m21())._m22(this.m22 - matrix4dc.m22())._m23(this.m23)._m30(this.m30 - matrix4dc.m30())._m31(this.m31 - matrix4dc.m31())._m32(this.m32 - matrix4dc.m32())._m33(this.m33)._properties(0);
		return matrix4d;
	}

	public Matrix4d mul4x3ComponentWise(Matrix4dc matrix4dc) {
		return this.mul4x3ComponentWise(matrix4dc, this);
	}

	public Matrix4d mul4x3ComponentWise(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		matrix4d._m00(this.m00 * matrix4dc.m00())._m01(this.m01 * matrix4dc.m01())._m02(this.m02 * matrix4dc.m02())._m03(this.m03)._m10(this.m10 * matrix4dc.m10())._m11(this.m11 * matrix4dc.m11())._m12(this.m12 * matrix4dc.m12())._m13(this.m13)._m20(this.m20 * matrix4dc.m20())._m21(this.m21 * matrix4dc.m21())._m22(this.m22 * matrix4dc.m22())._m23(this.m23)._m30(this.m30 * matrix4dc.m30())._m31(this.m31 * matrix4dc.m31())._m32(this.m32 * matrix4dc.m32())._m33(this.m33)._properties(0);
		return matrix4d;
	}

	public Matrix4d set(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16) {
		this.m00 = double1;
		this.m10 = double5;
		this.m20 = double9;
		this.m30 = double13;
		this.m01 = double2;
		this.m11 = double6;
		this.m21 = double10;
		this.m31 = double14;
		this.m02 = double3;
		this.m12 = double7;
		this.m22 = double11;
		this.m32 = double15;
		this.m03 = double4;
		this.m13 = double8;
		this.m23 = double12;
		this.m33 = double16;
		return this.determineProperties();
	}

	public Matrix4d set(double[] doubleArray, int int1) {
		return this._m00(doubleArray[int1 + 0])._m01(doubleArray[int1 + 1])._m02(doubleArray[int1 + 2])._m03(doubleArray[int1 + 3])._m10(doubleArray[int1 + 4])._m11(doubleArray[int1 + 5])._m12(doubleArray[int1 + 6])._m13(doubleArray[int1 + 7])._m20(doubleArray[int1 + 8])._m21(doubleArray[int1 + 9])._m22(doubleArray[int1 + 10])._m23(doubleArray[int1 + 11])._m30(doubleArray[int1 + 12])._m31(doubleArray[int1 + 13])._m32(doubleArray[int1 + 14])._m33(doubleArray[int1 + 15]).determineProperties();
	}

	public Matrix4d set(double[] doubleArray) {
		return this.set((double[])doubleArray, 0);
	}

	public Matrix4d set(float[] floatArray, int int1) {
		return this._m00((double)floatArray[int1 + 0])._m01((double)floatArray[int1 + 1])._m02((double)floatArray[int1 + 2])._m03((double)floatArray[int1 + 3])._m10((double)floatArray[int1 + 4])._m11((double)floatArray[int1 + 5])._m12((double)floatArray[int1 + 6])._m13((double)floatArray[int1 + 7])._m20((double)floatArray[int1 + 8])._m21((double)floatArray[int1 + 9])._m22((double)floatArray[int1 + 10])._m23((double)floatArray[int1 + 11])._m30((double)floatArray[int1 + 12])._m31((double)floatArray[int1 + 13])._m32((double)floatArray[int1 + 14])._m33((double)floatArray[int1 + 15]).determineProperties();
	}

	public Matrix4d set(float[] floatArray) {
		return this.set((float[])floatArray, 0);
	}

	public Matrix4d set(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
		return this.determineProperties();
	}

	public Matrix4d set(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.getf(this, floatBuffer.position(), floatBuffer);
		return this.determineProperties();
	}

	public Matrix4d set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this.determineProperties();
	}

	public Matrix4d setFloats(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.getf(this, byteBuffer.position(), byteBuffer);
		return this.determineProperties();
	}

	public Matrix4d setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this.determineProperties();
		}
	}

	public Matrix4d set(Vector4d vector4d, Vector4d vector4d2, Vector4d vector4d3, Vector4d vector4d4) {
		return this._m00(vector4d.x())._m01(vector4d.y())._m02(vector4d.z())._m03(vector4d.w())._m10(vector4d2.x())._m11(vector4d2.y())._m12(vector4d2.z())._m13(vector4d2.w())._m20(vector4d3.x())._m21(vector4d3.y())._m22(vector4d3.z())._m23(vector4d3.w())._m30(vector4d4.x())._m31(vector4d4.y())._m32(vector4d4.z())._m33(vector4d4.w()).determineProperties();
	}

	public double determinant() {
		return (this.properties & 2) != 0 ? this.determinantAffine() : (this.m00 * this.m11 - this.m01 * this.m10) * (this.m22 * this.m33 - this.m23 * this.m32) + (this.m02 * this.m10 - this.m00 * this.m12) * (this.m21 * this.m33 - this.m23 * this.m31) + (this.m00 * this.m13 - this.m03 * this.m10) * (this.m21 * this.m32 - this.m22 * this.m31) + (this.m01 * this.m12 - this.m02 * this.m11) * (this.m20 * this.m33 - this.m23 * this.m30) + (this.m03 * this.m11 - this.m01 * this.m13) * (this.m20 * this.m32 - this.m22 * this.m30) + (this.m02 * this.m13 - this.m03 * this.m12) * (this.m20 * this.m31 - this.m21 * this.m30);
	}

	public double determinant3x3() {
		return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
	}

	public double determinantAffine() {
		return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
	}

	public Matrix4d invert() {
		return this.invert(this);
	}

	public Matrix4d invert(Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.identity();
		} else if ((this.properties & 8) != 0) {
			return this.invertTranslation(matrix4d);
		} else if ((this.properties & 16) != 0) {
			return this.invertOrthonormal(matrix4d);
		} else if ((this.properties & 2) != 0) {
			return this.invertAffine(matrix4d);
		} else {
			return (this.properties & 1) != 0 ? this.invertPerspective(matrix4d) : this.invertGeneric(matrix4d);
		}
	}

	private Matrix4d invertTranslation(Matrix4d matrix4d) {
		if (matrix4d != this) {
			matrix4d.set((Matrix4dc)this);
		}

		matrix4d._m30(-this.m30)._m31(-this.m31)._m32(-this.m32)._properties(26);
		return matrix4d;
	}

	private Matrix4d invertOrthonormal(Matrix4d matrix4d) {
		double double1 = -(this.m00 * this.m30 + this.m01 * this.m31 + this.m02 * this.m32);
		double double2 = -(this.m10 * this.m30 + this.m11 * this.m31 + this.m12 * this.m32);
		double double3 = -(this.m20 * this.m30 + this.m21 * this.m31 + this.m22 * this.m32);
		double double4 = this.m01;
		double double5 = this.m02;
		double double6 = this.m12;
		matrix4d._m00(this.m00)._m01(this.m10)._m02(this.m20)._m03(0.0)._m10(double4)._m11(this.m11)._m12(this.m21)._m13(0.0)._m20(double5)._m21(double6)._m22(this.m22)._m23(0.0)._m30(double1)._m31(double2)._m32(double3)._m33(1.0)._properties(18);
		return matrix4d;
	}

	private Matrix4d invertGeneric(Matrix4d matrix4d) {
		return this != matrix4d ? this.invertGenericNonThis(matrix4d) : this.invertGenericThis(matrix4d);
	}

	private Matrix4d invertGenericNonThis(Matrix4d matrix4d) {
		double double1 = this.m00 * this.m11 - this.m01 * this.m10;
		double double2 = this.m00 * this.m12 - this.m02 * this.m10;
		double double3 = this.m00 * this.m13 - this.m03 * this.m10;
		double double4 = this.m01 * this.m12 - this.m02 * this.m11;
		double double5 = this.m01 * this.m13 - this.m03 * this.m11;
		double double6 = this.m02 * this.m13 - this.m03 * this.m12;
		double double7 = this.m20 * this.m31 - this.m21 * this.m30;
		double double8 = this.m20 * this.m32 - this.m22 * this.m30;
		double double9 = this.m20 * this.m33 - this.m23 * this.m30;
		double double10 = this.m21 * this.m32 - this.m22 * this.m31;
		double double11 = this.m21 * this.m33 - this.m23 * this.m31;
		double double12 = this.m22 * this.m33 - this.m23 * this.m32;
		double double13 = double1 * double12 - double2 * double11 + double3 * double10 + double4 * double9 - double5 * double8 + double6 * double7;
		double13 = 1.0 / double13;
		return matrix4d._m00(Math.fma(this.m11, double12, Math.fma(-this.m12, double11, this.m13 * double10)) * double13)._m01(Math.fma(-this.m01, double12, Math.fma(this.m02, double11, -this.m03 * double10)) * double13)._m02(Math.fma(this.m31, double6, Math.fma(-this.m32, double5, this.m33 * double4)) * double13)._m03(Math.fma(-this.m21, double6, Math.fma(this.m22, double5, -this.m23 * double4)) * double13)._m10(Math.fma(-this.m10, double12, Math.fma(this.m12, double9, -this.m13 * double8)) * double13)._m11(Math.fma(this.m00, double12, Math.fma(-this.m02, double9, this.m03 * double8)) * double13)._m12(Math.fma(-this.m30, double6, Math.fma(this.m32, double3, -this.m33 * double2)) * double13)._m13(Math.fma(this.m20, double6, Math.fma(-this.m22, double3, this.m23 * double2)) * double13)._m20(Math.fma(this.m10, double11, Math.fma(-this.m11, double9, this.m13 * double7)) * double13)._m21(Math.fma(-this.m00, double11, Math.fma(this.m01, double9, -this.m03 * double7)) * double13)._m22(Math.fma(this.m30, double5, Math.fma(-this.m31, double3, this.m33 * double1)) * double13)._m23(Math.fma(-this.m20, double5, Math.fma(this.m21, double3, -this.m23 * double1)) * double13)._m30(Math.fma(-this.m10, double10, Math.fma(this.m11, double8, -this.m12 * double7)) * double13)._m31(Math.fma(this.m00, double10, Math.fma(-this.m01, double8, this.m02 * double7)) * double13)._m32(Math.fma(-this.m30, double4, Math.fma(this.m31, double2, -this.m32 * double1)) * double13)._m33(Math.fma(this.m20, double4, Math.fma(-this.m21, double2, this.m22 * double1)) * double13)._properties(0);
	}

	private Matrix4d invertGenericThis(Matrix4d matrix4d) {
		double double1 = this.m00 * this.m11 - this.m01 * this.m10;
		double double2 = this.m00 * this.m12 - this.m02 * this.m10;
		double double3 = this.m00 * this.m13 - this.m03 * this.m10;
		double double4 = this.m01 * this.m12 - this.m02 * this.m11;
		double double5 = this.m01 * this.m13 - this.m03 * this.m11;
		double double6 = this.m02 * this.m13 - this.m03 * this.m12;
		double double7 = this.m20 * this.m31 - this.m21 * this.m30;
		double double8 = this.m20 * this.m32 - this.m22 * this.m30;
		double double9 = this.m20 * this.m33 - this.m23 * this.m30;
		double double10 = this.m21 * this.m32 - this.m22 * this.m31;
		double double11 = this.m21 * this.m33 - this.m23 * this.m31;
		double double12 = this.m22 * this.m33 - this.m23 * this.m32;
		double double13 = double1 * double12 - double2 * double11 + double3 * double10 + double4 * double9 - double5 * double8 + double6 * double7;
		double13 = 1.0 / double13;
		double double14 = Math.fma(this.m11, double12, Math.fma(-this.m12, double11, this.m13 * double10)) * double13;
		double double15 = Math.fma(-this.m01, double12, Math.fma(this.m02, double11, -this.m03 * double10)) * double13;
		double double16 = Math.fma(this.m31, double6, Math.fma(-this.m32, double5, this.m33 * double4)) * double13;
		double double17 = Math.fma(-this.m21, double6, Math.fma(this.m22, double5, -this.m23 * double4)) * double13;
		double double18 = Math.fma(-this.m10, double12, Math.fma(this.m12, double9, -this.m13 * double8)) * double13;
		double double19 = Math.fma(this.m00, double12, Math.fma(-this.m02, double9, this.m03 * double8)) * double13;
		double double20 = Math.fma(-this.m30, double6, Math.fma(this.m32, double3, -this.m33 * double2)) * double13;
		double double21 = Math.fma(this.m20, double6, Math.fma(-this.m22, double3, this.m23 * double2)) * double13;
		double double22 = Math.fma(this.m10, double11, Math.fma(-this.m11, double9, this.m13 * double7)) * double13;
		double double23 = Math.fma(-this.m00, double11, Math.fma(this.m01, double9, -this.m03 * double7)) * double13;
		double double24 = Math.fma(this.m30, double5, Math.fma(-this.m31, double3, this.m33 * double1)) * double13;
		double double25 = Math.fma(-this.m20, double5, Math.fma(this.m21, double3, -this.m23 * double1)) * double13;
		double double26 = Math.fma(-this.m10, double10, Math.fma(this.m11, double8, -this.m12 * double7)) * double13;
		double double27 = Math.fma(this.m00, double10, Math.fma(-this.m01, double8, this.m02 * double7)) * double13;
		double double28 = Math.fma(-this.m30, double4, Math.fma(this.m31, double2, -this.m32 * double1)) * double13;
		double double29 = Math.fma(this.m20, double4, Math.fma(-this.m21, double2, this.m22 * double1)) * double13;
		return matrix4d._m00(double14)._m01(double15)._m02(double16)._m03(double17)._m10(double18)._m11(double19)._m12(double20)._m13(double21)._m20(double22)._m21(double23)._m22(double24)._m23(double25)._m30(double26)._m31(double27)._m32(double28)._m33(double29)._properties(0);
	}

	public Matrix4d invertPerspective(Matrix4d matrix4d) {
		double double1 = 1.0 / (this.m00 * this.m11);
		double double2 = -1.0 / (this.m23 * this.m32);
		matrix4d.set(this.m11 * double1, 0.0, 0.0, 0.0, 0.0, this.m00 * double1, 0.0, 0.0, 0.0, 0.0, 0.0, -this.m23 * double2, 0.0, 0.0, -this.m32 * double2, this.m22 * double2);
		return matrix4d;
	}

	public Matrix4d invertPerspective() {
		return this.invertPerspective(this);
	}

	public Matrix4d invertFrustum(Matrix4d matrix4d) {
		double double1 = 1.0 / this.m00;
		double double2 = 1.0 / this.m11;
		double double3 = 1.0 / this.m23;
		double double4 = 1.0 / this.m32;
		matrix4d.set(double1, 0.0, 0.0, 0.0, 0.0, double2, 0.0, 0.0, 0.0, 0.0, 0.0, double4, -this.m20 * double1 * double3, -this.m21 * double2 * double3, double3, -this.m22 * double3 * double4);
		return matrix4d;
	}

	public Matrix4d invertFrustum() {
		return this.invertFrustum(this);
	}

	public Matrix4d invertOrtho(Matrix4d matrix4d) {
		double double1 = 1.0 / this.m00;
		double double2 = 1.0 / this.m11;
		double double3 = 1.0 / this.m22;
		matrix4d.set(double1, 0.0, 0.0, 0.0, 0.0, double2, 0.0, 0.0, 0.0, 0.0, double3, 0.0, -this.m30 * double1, -this.m31 * double2, -this.m32 * double3, 1.0)._properties(2 | this.properties & 16);
		return matrix4d;
	}

	public Matrix4d invertOrtho() {
		return this.invertOrtho(this);
	}

	public Matrix4d invertPerspectiveView(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		double double1 = 1.0 / (this.m00 * this.m11);
		double double2 = -1.0 / (this.m23 * this.m32);
		double double3 = this.m11 * double1;
		double double4 = this.m00 * double1;
		double double5 = -this.m23 * double2;
		double double6 = -this.m32 * double2;
		double double7 = this.m22 * double2;
		double double8 = -matrix4dc.m00() * matrix4dc.m30() - matrix4dc.m01() * matrix4dc.m31() - matrix4dc.m02() * matrix4dc.m32();
		double double9 = -matrix4dc.m10() * matrix4dc.m30() - matrix4dc.m11() * matrix4dc.m31() - matrix4dc.m12() * matrix4dc.m32();
		double double10 = -matrix4dc.m20() * matrix4dc.m30() - matrix4dc.m21() * matrix4dc.m31() - matrix4dc.m22() * matrix4dc.m32();
		double double11 = matrix4dc.m01() * double4;
		double double12 = matrix4dc.m02() * double6 + double8 * double7;
		double double13 = matrix4dc.m12() * double6 + double9 * double7;
		double double14 = matrix4dc.m22() * double6 + double10 * double7;
		return matrix4d._m00(matrix4dc.m00() * double3)._m01(matrix4dc.m10() * double3)._m02(matrix4dc.m20() * double3)._m03(0.0)._m10(double11)._m11(matrix4dc.m11() * double4)._m12(matrix4dc.m21() * double4)._m13(0.0)._m20(double8 * double5)._m21(double9 * double5)._m22(double10 * double5)._m23(double5)._m30(double12)._m31(double13)._m32(double14)._m33(double7)._properties(0);
	}

	public Matrix4d invertPerspectiveView(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d) {
		double double1 = 1.0 / (this.m00 * this.m11);
		double double2 = -1.0 / (this.m23 * this.m32);
		double double3 = this.m11 * double1;
		double double4 = this.m00 * double1;
		double double5 = -this.m23 * double2;
		double double6 = -this.m32 * double2;
		double double7 = this.m22 * double2;
		double double8 = -matrix4x3dc.m00() * matrix4x3dc.m30() - matrix4x3dc.m01() * matrix4x3dc.m31() - matrix4x3dc.m02() * matrix4x3dc.m32();
		double double9 = -matrix4x3dc.m10() * matrix4x3dc.m30() - matrix4x3dc.m11() * matrix4x3dc.m31() - matrix4x3dc.m12() * matrix4x3dc.m32();
		double double10 = -matrix4x3dc.m20() * matrix4x3dc.m30() - matrix4x3dc.m21() * matrix4x3dc.m31() - matrix4x3dc.m22() * matrix4x3dc.m32();
		return matrix4d._m00(matrix4x3dc.m00() * double3)._m01(matrix4x3dc.m10() * double3)._m02(matrix4x3dc.m20() * double3)._m03(0.0)._m10(matrix4x3dc.m01() * double4)._m11(matrix4x3dc.m11() * double4)._m12(matrix4x3dc.m21() * double4)._m13(0.0)._m20(double8 * double5)._m21(double9 * double5)._m22(double10 * double5)._m23(double5)._m30(matrix4x3dc.m02() * double6 + double8 * double7)._m31(matrix4x3dc.m12() * double6 + double9 * double7)._m32(matrix4x3dc.m22() * double6 + double10 * double7)._m33(double7)._properties(0);
	}

	public Matrix4d invertAffine(Matrix4d matrix4d) {
		double double1 = this.m00 * this.m11;
		double double2 = this.m01 * this.m10;
		double double3 = this.m02 * this.m10;
		double double4 = this.m00 * this.m12;
		double double5 = this.m01 * this.m12;
		double double6 = this.m02 * this.m11;
		double double7 = 1.0 / ((double1 - double2) * this.m22 + (double3 - double4) * this.m21 + (double5 - double6) * this.m20);
		double double8 = this.m10 * this.m22;
		double double9 = this.m10 * this.m21;
		double double10 = this.m11 * this.m22;
		double double11 = this.m11 * this.m20;
		double double12 = this.m12 * this.m21;
		double double13 = this.m12 * this.m20;
		double double14 = this.m20 * this.m02;
		double double15 = this.m20 * this.m01;
		double double16 = this.m21 * this.m02;
		double double17 = this.m21 * this.m00;
		double double18 = this.m22 * this.m01;
		double double19 = this.m22 * this.m00;
		double double20 = (double10 - double12) * double7;
		double double21 = (double16 - double18) * double7;
		double double22 = (double5 - double6) * double7;
		double double23 = (double13 - double8) * double7;
		double double24 = (double19 - double14) * double7;
		double double25 = (double3 - double4) * double7;
		double double26 = (double9 - double11) * double7;
		double double27 = (double15 - double17) * double7;
		double double28 = (double1 - double2) * double7;
		double double29 = (double8 * this.m31 - double9 * this.m32 + double11 * this.m32 - double10 * this.m30 + double12 * this.m30 - double13 * this.m31) * double7;
		double double30 = (double14 * this.m31 - double15 * this.m32 + double17 * this.m32 - double16 * this.m30 + double18 * this.m30 - double19 * this.m31) * double7;
		double double31 = (double6 * this.m30 - double5 * this.m30 + double4 * this.m31 - double3 * this.m31 + double2 * this.m32 - double1 * this.m32) * double7;
		matrix4d._m00(double20)._m01(double21)._m02(double22)._m03(0.0)._m10(double23)._m11(double24)._m12(double25)._m13(0.0)._m20(double26)._m21(double27)._m22(double28)._m23(0.0)._m30(double29)._m31(double30)._m32(double31)._m33(1.0)._properties(2);
		return matrix4d;
	}

	public Matrix4d invertAffine() {
		return this.invertAffine(this);
	}

	public Matrix4d transpose() {
		return this.transpose(this);
	}

	public Matrix4d transpose(Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.identity();
		} else {
			return this != matrix4d ? this.transposeNonThisGeneric(matrix4d) : this.transposeThisGeneric(matrix4d);
		}
	}

	private Matrix4d transposeNonThisGeneric(Matrix4d matrix4d) {
		return matrix4d._m00(this.m00)._m01(this.m10)._m02(this.m20)._m03(this.m30)._m10(this.m01)._m11(this.m11)._m12(this.m21)._m13(this.m31)._m20(this.m02)._m21(this.m12)._m22(this.m22)._m23(this.m32)._m30(this.m03)._m31(this.m13)._m32(this.m23)._m33(this.m33)._properties(0);
	}

	private Matrix4d transposeThisGeneric(Matrix4d matrix4d) {
		double double1 = this.m01;
		double double2 = this.m02;
		double double3 = this.m12;
		double double4 = this.m03;
		double double5 = this.m13;
		double double6 = this.m23;
		return matrix4d._m01(this.m10)._m02(this.m20)._m03(this.m30)._m10(double1)._m12(this.m21)._m13(this.m31)._m20(double2)._m21(double3)._m23(this.m32)._m30(double4)._m31(double5)._m32(double6)._properties(0);
	}

	public Matrix4d transpose3x3() {
		return this.transpose3x3(this);
	}

	public Matrix4d transpose3x3(Matrix4d matrix4d) {
		double double1 = this.m01;
		double double2 = this.m02;
		double double3 = this.m12;
		return matrix4d._m00(this.m00)._m01(this.m10)._m02(this.m20)._m10(double1)._m11(this.m11)._m12(this.m21)._m20(double2)._m21(double3)._m22(this.m22)._properties(this.properties & 30);
	}

	public Matrix3d transpose3x3(Matrix3d matrix3d) {
		return matrix3d._m00(this.m00)._m01(this.m10)._m02(this.m20)._m10(this.m01)._m11(this.m11)._m12(this.m21)._m20(this.m02)._m21(this.m12)._m22(this.m22);
	}

	public Matrix4d translation(double double1, double double2, double double3) {
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		return this._m30(double1)._m31(double2)._m32(double3)._m33(1.0)._properties(26);
	}

	public Matrix4d translation(Vector3fc vector3fc) {
		return this.translation((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Matrix4d translation(Vector3dc vector3dc) {
		return this.translation(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4d setTranslation(double double1, double double2, double double3) {
		Matrix4d matrix4d = this._m30(double1)._m31(double2)._m32(double3);
		matrix4d.properties &= -6;
		return this;
	}

	public Matrix4d setTranslation(Vector3dc vector3dc) {
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
		return string + " " + Runtime.format(this.m10, numberFormat) + " " + Runtime.format(this.m20, numberFormat) + " " + Runtime.format(this.m30, numberFormat) + "\n" + Runtime.format(this.m01, numberFormat) + " " + Runtime.format(this.m11, numberFormat) + " " + Runtime.format(this.m21, numberFormat) + " " + Runtime.format(this.m31, numberFormat) + "\n" + Runtime.format(this.m02, numberFormat) + " " + Runtime.format(this.m12, numberFormat) + " " + Runtime.format(this.m22, numberFormat) + " " + Runtime.format(this.m32, numberFormat) + "\n" + Runtime.format(this.m03, numberFormat) + " " + Runtime.format(this.m13, numberFormat) + " " + Runtime.format(this.m23, numberFormat) + " " + Runtime.format(this.m33, numberFormat) + "\n";
	}

	public Matrix4d get(Matrix4d matrix4d) {
		return matrix4d.set((Matrix4dc)this);
	}

	public Matrix4x3d get4x3(Matrix4x3d matrix4x3d) {
		return matrix4x3d.set((Matrix4dc)this);
	}

	public Matrix3d get3x3(Matrix3d matrix3d) {
		return matrix3d.set((Matrix4dc)this);
	}

	public Quaternionf getUnnormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromUnnormalized((Matrix4dc)this);
	}

	public Quaternionf getNormalizedRotation(Quaternionf quaternionf) {
		return quaternionf.setFromNormalized((Matrix4dc)this);
	}

	public Quaterniond getUnnormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromUnnormalized((Matrix4dc)this);
	}

	public Quaterniond getNormalizedRotation(Quaterniond quaterniond) {
		return quaterniond.setFromNormalized((Matrix4dc)this);
	}

	public DoubleBuffer get(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put(this, doubleBuffer.position(), doubleBuffer);
		return doubleBuffer;
	}

	public DoubleBuffer get(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public FloatBuffer get(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.putf(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.putf(this, int1, floatBuffer);
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

	public ByteBuffer getFloats(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putf(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer getFloats(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putf(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Matrix4dc getToAddress(long long1) {
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
		doubleArray[int1 + 3] = this.m03;
		doubleArray[int1 + 4] = this.m10;
		doubleArray[int1 + 5] = this.m11;
		doubleArray[int1 + 6] = this.m12;
		doubleArray[int1 + 7] = this.m13;
		doubleArray[int1 + 8] = this.m20;
		doubleArray[int1 + 9] = this.m21;
		doubleArray[int1 + 10] = this.m22;
		doubleArray[int1 + 11] = this.m23;
		doubleArray[int1 + 12] = this.m30;
		doubleArray[int1 + 13] = this.m31;
		doubleArray[int1 + 14] = this.m32;
		doubleArray[int1 + 15] = this.m33;
		return doubleArray;
	}

	public double[] get(double[] doubleArray) {
		return this.get((double[])doubleArray, 0);
	}

	public float[] get(float[] floatArray, int int1) {
		floatArray[int1 + 0] = (float)this.m00;
		floatArray[int1 + 1] = (float)this.m01;
		floatArray[int1 + 2] = (float)this.m02;
		floatArray[int1 + 3] = (float)this.m03;
		floatArray[int1 + 4] = (float)this.m10;
		floatArray[int1 + 5] = (float)this.m11;
		floatArray[int1 + 6] = (float)this.m12;
		floatArray[int1 + 7] = (float)this.m13;
		floatArray[int1 + 8] = (float)this.m20;
		floatArray[int1 + 9] = (float)this.m21;
		floatArray[int1 + 10] = (float)this.m22;
		floatArray[int1 + 11] = (float)this.m23;
		floatArray[int1 + 12] = (float)this.m30;
		floatArray[int1 + 13] = (float)this.m31;
		floatArray[int1 + 14] = (float)this.m32;
		floatArray[int1 + 15] = (float)this.m33;
		return floatArray;
	}

	public float[] get(float[] floatArray) {
		return this.get((float[])floatArray, 0);
	}

	public DoubleBuffer getTransposed(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.putTransposed(this, doubleBuffer.position(), doubleBuffer);
		return doubleBuffer;
	}

	public DoubleBuffer getTransposed(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public ByteBuffer getTransposed(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putTransposed(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, byteBuffer);
		return byteBuffer;
	}

	public DoubleBuffer get4x3Transposed(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put4x3Transposed(this, doubleBuffer.position(), doubleBuffer);
		return doubleBuffer;
	}

	public DoubleBuffer get4x3Transposed(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put4x3Transposed(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public ByteBuffer get4x3Transposed(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x3Transposed(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get4x3Transposed(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x3Transposed(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Matrix4d zero() {
		return this._m00(0.0)._m01(0.0)._m02(0.0)._m03(0.0)._m10(0.0)._m11(0.0)._m12(0.0)._m13(0.0)._m20(0.0)._m21(0.0)._m22(0.0)._m23(0.0)._m30(0.0)._m31(0.0)._m32(0.0)._m33(0.0)._properties(0);
	}

	public Matrix4d scaling(double double1) {
		return this.scaling(double1, double1, double1);
	}

	public Matrix4d scaling(double double1, double double2, double double3) {
		if ((this.properties & 4) == 0) {
			this.identity();
		}

		boolean boolean1 = Math.absEqualsOne(double1) && Math.absEqualsOne(double2) && Math.absEqualsOne(double3);
		this._m00(double1)._m11(double2)._m22(double3).properties = 2 | (boolean1 ? 16 : 0);
		return this;
	}

	public Matrix4d scaling(Vector3dc vector3dc) {
		return this.scaling(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4d rotation(double double1, double double2, double double3, double double4) {
		if (double3 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double2)) {
			return this.rotationX(double2 * double1);
		} else if (double2 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double3)) {
			return this.rotationY(double3 * double1);
		} else {
			return double2 == 0.0 && double3 == 0.0 && Math.absEqualsOne(double4) ? this.rotationZ(double4 * double1) : this.rotationInternal(double1, double2, double3, double4);
		}
	}

	private Matrix4d rotationInternal(double double1, double double2, double double3, double double4) {
		double double5 = Math.sin(double1);
		double double6 = Math.cosFromSin(double5, double1);
		double double7 = 1.0 - double6;
		double double8 = double2 * double3;
		double double9 = double2 * double4;
		double double10 = double3 * double4;
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00(double6 + double2 * double2 * double7)._m10(double8 * double7 - double4 * double5)._m20(double9 * double7 + double3 * double5)._m01(double8 * double7 + double4 * double5)._m11(double6 + double3 * double3 * double7)._m21(double10 * double7 - double2 * double5)._m02(double9 * double7 - double3 * double5)._m12(double10 * double7 + double2 * double5)._m22(double6 + double4 * double4 * double7).properties = 18;
		return this;
	}

	public Matrix4d rotationX(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m11(double3)._m12(double2)._m21(-double2)._m22(double3).properties = 18;
		return this;
	}

	public Matrix4d rotationY(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00(double3)._m02(-double2)._m20(double2)._m22(double3).properties = 18;
		return this;
	}

	public Matrix4d rotationZ(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00(double3)._m01(double2)._m10(-double2)._m11(double3).properties = 18;
		return this;
	}

	public Matrix4d rotationTowardsXY(double double1, double double2) {
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this.m00 = double2;
		this.m01 = double1;
		this.m10 = -double1;
		this.m11 = double2;
		this.properties = 18;
		return this;
	}

	public Matrix4d rotationXYZ(double double1, double double2, double double3) {
		double double4 = Math.sin(double1);
		double double5 = Math.cosFromSin(double4, double1);
		double double6 = Math.sin(double2);
		double double7 = Math.cosFromSin(double6, double2);
		double double8 = Math.sin(double3);
		double double9 = Math.cosFromSin(double8, double3);
		double double10 = -double4;
		double double11 = -double6;
		double double12 = -double8;
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		double double13 = double10 * double11;
		double double14 = double5 * double11;
		this._m20(double6)._m21(double10 * double7)._m22(double5 * double7)._m00(double7 * double9)._m01(double13 * double9 + double5 * double8)._m02(double14 * double9 + double4 * double8)._m10(double7 * double12)._m11(double13 * double12 + double5 * double9)._m12(double14 * double12 + double4 * double9).properties = 18;
		return this;
	}

	public Matrix4d rotationZYX(double double1, double double2, double double3) {
		double double4 = Math.sin(double3);
		double double5 = Math.cosFromSin(double4, double3);
		double double6 = Math.sin(double2);
		double double7 = Math.cosFromSin(double6, double2);
		double double8 = Math.sin(double1);
		double double9 = Math.cosFromSin(double8, double1);
		double double10 = -double8;
		double double11 = -double6;
		double double12 = -double4;
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		double double13 = double9 * double6;
		double double14 = double8 * double6;
		this._m00(double9 * double7)._m01(double8 * double7)._m02(double11)._m10(double10 * double5 + double13 * double4)._m11(double9 * double5 + double14 * double4)._m12(double7 * double4)._m20(double10 * double12 + double13 * double5)._m21(double9 * double12 + double14 * double5)._m22(double7 * double5).properties = 18;
		return this;
	}

	public Matrix4d rotationYXZ(double double1, double double2, double double3) {
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
		this._m20(double6 * double5)._m21(double11)._m22(double7 * double5)._m23(0.0)._m00(double7 * double9 + double13 * double8)._m01(double5 * double8)._m02(double10 * double9 + double14 * double8)._m03(0.0)._m10(double7 * double12 + double13 * double9)._m11(double5 * double9)._m12(double10 * double12 + double14 * double9)._m13(0.0)._m30(0.0)._m31(0.0)._m32(0.0)._m33(1.0).properties = 18;
		return this;
	}

	public Matrix4d setRotationXYZ(double double1, double double2, double double3) {
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
		Matrix4d matrix4d = this._m20(double6)._m21(double10 * double7)._m22(double5 * double7)._m00(double7 * double9)._m01(double13 * double9 + double5 * double8)._m02(double14 * double9 + double4 * double8)._m10(double7 * double12)._m11(double13 * double12 + double5 * double9)._m12(double14 * double12 + double4 * double9);
		matrix4d.properties &= -14;
		return this;
	}

	public Matrix4d setRotationZYX(double double1, double double2, double double3) {
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
		Matrix4d matrix4d = this._m00(double9 * double7)._m01(double8 * double7)._m02(double11)._m10(double10 * double5 + double13 * double4)._m11(double9 * double5 + double14 * double4)._m12(double7 * double4)._m20(double10 * double12 + double13 * double5)._m21(double9 * double12 + double14 * double5)._m22(double7 * double5);
		matrix4d.properties &= -14;
		return this;
	}

	public Matrix4d setRotationYXZ(double double1, double double2, double double3) {
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
		Matrix4d matrix4d = this._m20(double6 * double5)._m21(double11)._m22(double7 * double5)._m00(double7 * double9 + double13 * double8)._m01(double5 * double8)._m02(double10 * double9 + double14 * double8)._m10(double7 * double12 + double13 * double9)._m11(double5 * double9)._m12(double10 * double12 + double14 * double9);
		matrix4d.properties &= -14;
		return this;
	}

	public Matrix4d rotation(double double1, Vector3dc vector3dc) {
		return this.rotation(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4d rotation(double double1, Vector3fc vector3fc) {
		return this.rotation(double1, (double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Vector4d transform(Vector4d vector4d) {
		return vector4d.mul((Matrix4dc)this);
	}

	public Vector4d transform(Vector4dc vector4dc, Vector4d vector4d) {
		return vector4dc.mul((Matrix4dc)this, vector4d);
	}

	public Vector4d transform(double double1, double double2, double double3, double double4, Vector4d vector4d) {
		return vector4d.set(this.m00 * double1 + this.m10 * double2 + this.m20 * double3 + this.m30 * double4, this.m01 * double1 + this.m11 * double2 + this.m21 * double3 + this.m31 * double4, this.m02 * double1 + this.m12 * double2 + this.m22 * double3 + this.m32 * double4, this.m03 * double1 + this.m13 * double2 + this.m23 * double3 + this.m33 * double4);
	}

	public Vector4d transformTranspose(Vector4d vector4d) {
		return vector4d.mulTranspose(this);
	}

	public Vector4d transformTranspose(Vector4dc vector4dc, Vector4d vector4d) {
		return vector4dc.mulTranspose(this, vector4d);
	}

	public Vector4d transformTranspose(double double1, double double2, double double3, double double4, Vector4d vector4d) {
		return vector4d.set(double1, double2, double3, double4).mulTranspose(this);
	}

	public Vector4d transformProject(Vector4d vector4d) {
		return vector4d.mulProject(this);
	}

	public Vector4d transformProject(Vector4dc vector4dc, Vector4d vector4d) {
		return vector4dc.mulProject(this, (Vector4d)vector4d);
	}

	public Vector4d transformProject(double double1, double double2, double double3, double double4, Vector4d vector4d) {
		double double5 = 1.0 / (this.m03 * double1 + this.m13 * double2 + this.m23 * double3 + this.m33 * double4);
		return vector4d.set((this.m00 * double1 + this.m10 * double2 + this.m20 * double3 + this.m30 * double4) * double5, (this.m01 * double1 + this.m11 * double2 + this.m21 * double3 + this.m31 * double4) * double5, (this.m02 * double1 + this.m12 * double2 + this.m22 * double3 + this.m32 * double4) * double5, 1.0);
	}

	public Vector3d transformProject(Vector3d vector3d) {
		return vector3d.mulProject((Matrix4dc)this);
	}

	public Vector3d transformProject(Vector3dc vector3dc, Vector3d vector3d) {
		return vector3dc.mulProject((Matrix4dc)this, vector3d);
	}

	public Vector3d transformProject(double double1, double double2, double double3, Vector3d vector3d) {
		double double4 = 1.0 / (this.m03 * double1 + this.m13 * double2 + this.m23 * double3 + this.m33);
		return vector3d.set((this.m00 * double1 + this.m10 * double2 + this.m20 * double3 + this.m30) * double4, (this.m01 * double1 + this.m11 * double2 + this.m21 * double3 + this.m31) * double4, (this.m02 * double1 + this.m12 * double2 + this.m22 * double3 + this.m32) * double4);
	}

	public Vector3d transformProject(Vector4dc vector4dc, Vector3d vector3d) {
		return vector4dc.mulProject(this, (Vector3d)vector3d);
	}

	public Vector3d transformProject(double double1, double double2, double double3, double double4, Vector3d vector3d) {
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d.mulProject(this, double4, vector3d);
	}

	public Vector3d transformPosition(Vector3d vector3d) {
		return vector3d.set(this.m00 * vector3d.x + this.m10 * vector3d.y + this.m20 * vector3d.z + this.m30, this.m01 * vector3d.x + this.m11 * vector3d.y + this.m21 * vector3d.z + this.m31, this.m02 * vector3d.x + this.m12 * vector3d.y + this.m22 * vector3d.z + this.m32);
	}

	public Vector3d transformPosition(Vector3dc vector3dc, Vector3d vector3d) {
		return this.transformPosition(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3d);
	}

	public Vector3d transformPosition(double double1, double double2, double double3, Vector3d vector3d) {
		return vector3d.set(this.m00 * double1 + this.m10 * double2 + this.m20 * double3 + this.m30, this.m01 * double1 + this.m11 * double2 + this.m21 * double3 + this.m31, this.m02 * double1 + this.m12 * double2 + this.m22 * double3 + this.m32);
	}

	public Vector3d transformDirection(Vector3d vector3d) {
		return vector3d.set(this.m00 * vector3d.x + this.m10 * vector3d.y + this.m20 * vector3d.z, this.m01 * vector3d.x + this.m11 * vector3d.y + this.m21 * vector3d.z, this.m02 * vector3d.x + this.m12 * vector3d.y + this.m22 * vector3d.z);
	}

	public Vector3d transformDirection(Vector3dc vector3dc, Vector3d vector3d) {
		return vector3d.set(this.m00 * vector3dc.x() + this.m10 * vector3dc.y() + this.m20 * vector3dc.z(), this.m01 * vector3dc.x() + this.m11 * vector3dc.y() + this.m21 * vector3dc.z(), this.m02 * vector3dc.x() + this.m12 * vector3dc.y() + this.m22 * vector3dc.z());
	}

	public Vector3d transformDirection(double double1, double double2, double double3, Vector3d vector3d) {
		return vector3d.set(this.m00 * double1 + this.m10 * double2 + this.m20 * double3, this.m01 * double1 + this.m11 * double2 + this.m21 * double3, this.m02 * double1 + this.m12 * double2 + this.m22 * double3);
	}

	public Vector3f transformDirection(Vector3f vector3f) {
		return vector3f.mulDirection((Matrix4dc)this);
	}

	public Vector3f transformDirection(Vector3fc vector3fc, Vector3f vector3f) {
		return vector3fc.mulDirection((Matrix4dc)this, vector3f);
	}

	public Vector3f transformDirection(double double1, double double2, double double3, Vector3f vector3f) {
		float float1 = (float)(this.m00 * double1 + this.m10 * double2 + this.m20 * double3);
		float float2 = (float)(this.m01 * double1 + this.m11 * double2 + this.m21 * double3);
		float float3 = (float)(this.m02 * double1 + this.m12 * double2 + this.m22 * double3);
		vector3f.x = float1;
		vector3f.y = float2;
		vector3f.z = float3;
		return vector3f;
	}

	public Vector4d transformAffine(Vector4d vector4d) {
		return vector4d.mulAffine((Matrix4dc)this, vector4d);
	}

	public Vector4d transformAffine(Vector4dc vector4dc, Vector4d vector4d) {
		return this.transformAffine(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4dc.w(), vector4d);
	}

	public Vector4d transformAffine(double double1, double double2, double double3, double double4, Vector4d vector4d) {
		double double5 = this.m00 * double1 + this.m10 * double2 + this.m20 * double3 + this.m30 * double4;
		double double6 = this.m01 * double1 + this.m11 * double2 + this.m21 * double3 + this.m31 * double4;
		double double7 = this.m02 * double1 + this.m12 * double2 + this.m22 * double3 + this.m32 * double4;
		vector4d.x = double5;
		vector4d.y = double6;
		vector4d.z = double7;
		vector4d.w = double4;
		return vector4d;
	}

	public Matrix4d set3x3(Matrix3dc matrix3dc) {
		return this._m00(matrix3dc.m00())._m01(matrix3dc.m01())._m02(matrix3dc.m02())._m10(matrix3dc.m10())._m11(matrix3dc.m11())._m12(matrix3dc.m12())._m20(matrix3dc.m20())._m21(matrix3dc.m21())._m22(matrix3dc.m22())._properties(this.properties & -30);
	}

	public Matrix4d scale(Vector3dc vector3dc, Matrix4d matrix4d) {
		return this.scale(vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix4d);
	}

	public Matrix4d scale(Vector3dc vector3dc) {
		return this.scale(vector3dc.x(), vector3dc.y(), vector3dc.z(), this);
	}

	public Matrix4d scale(double double1, double double2, double double3, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.scaling(double1, double2, double3) : this.scaleGeneric(double1, double2, double3, matrix4d);
	}

	private Matrix4d scaleGeneric(double double1, double double2, double double3, Matrix4d matrix4d) {
		boolean boolean1 = Math.absEqualsOne(double1) && Math.absEqualsOne(double2) && Math.absEqualsOne(double3);
		matrix4d._m00(this.m00 * double1)._m01(this.m01 * double1)._m02(this.m02 * double1)._m03(this.m03 * double1)._m10(this.m10 * double2)._m11(this.m11 * double2)._m12(this.m12 * double2)._m13(this.m13 * double2)._m20(this.m20 * double3)._m21(this.m21 * double3)._m22(this.m22 * double3)._m23(this.m23 * double3)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & ~(13 | (boolean1 ? 0 : 16)));
		return matrix4d;
	}

	public Matrix4d scale(double double1, double double2, double double3) {
		return this.scale(double1, double2, double3, this);
	}

	public Matrix4d scale(double double1, Matrix4d matrix4d) {
		return this.scale(double1, double1, double1, matrix4d);
	}

	public Matrix4d scale(double double1) {
		return this.scale(double1, double1, double1);
	}

	public Matrix4d scaleXY(double double1, double double2, Matrix4d matrix4d) {
		return this.scale(double1, double2, 1.0, matrix4d);
	}

	public Matrix4d scaleXY(double double1, double double2) {
		return this.scale(double1, double2, 1.0);
	}

	public Matrix4d scaleAround(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
		double double7 = this.m00 * double4 + this.m10 * double5 + this.m20 * double6 + this.m30;
		double double8 = this.m01 * double4 + this.m11 * double5 + this.m21 * double6 + this.m31;
		double double9 = this.m02 * double4 + this.m12 * double5 + this.m22 * double6 + this.m32;
		double double10 = this.m03 * double4 + this.m13 * double5 + this.m23 * double6 + this.m33;
		boolean boolean1 = Math.absEqualsOne(double1) && Math.absEqualsOne(double2) && Math.absEqualsOne(double3);
		matrix4d._m00(this.m00 * double1)._m01(this.m01 * double1)._m02(this.m02 * double1)._m03(this.m03 * double1)._m10(this.m10 * double2)._m11(this.m11 * double2)._m12(this.m12 * double2)._m13(this.m13 * double2)._m20(this.m20 * double3)._m21(this.m21 * double3)._m22(this.m22 * double3)._m23(this.m23 * double3)._m30(-this.m00 * double4 - this.m10 * double5 - this.m20 * double6 + double7)._m31(-this.m01 * double4 - this.m11 * double5 - this.m21 * double6 + double8)._m32(-this.m02 * double4 - this.m12 * double5 - this.m22 * double6 + double9)._m33(-this.m03 * double4 - this.m13 * double5 - this.m23 * double6 + double10)._properties(this.properties & ~(13 | (boolean1 ? 0 : 16)));
		return matrix4d;
	}

	public Matrix4d scaleAround(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.scaleAround(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4d scaleAround(double double1, double double2, double double3, double double4) {
		return this.scaleAround(double1, double1, double1, double2, double3, double4, this);
	}

	public Matrix4d scaleAround(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		return this.scaleAround(double1, double1, double1, double2, double3, double4, matrix4d);
	}

	public Matrix4d scaleLocal(double double1, double double2, double double3, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.scaling(double1, double2, double3) : this.scaleLocalGeneric(double1, double2, double3, matrix4d);
	}

	private Matrix4d scaleLocalGeneric(double double1, double double2, double double3, Matrix4d matrix4d) {
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
		boolean boolean1 = Math.absEqualsOne(double1) && Math.absEqualsOne(double2) && Math.absEqualsOne(double3);
		matrix4d._m00(double4)._m01(double5)._m02(double6)._m03(this.m03)._m10(double7)._m11(double8)._m12(double9)._m13(this.m13)._m20(double10)._m21(double11)._m22(double12)._m23(this.m23)._m30(double13)._m31(double14)._m32(double15)._m33(this.m33)._properties(this.properties & ~(13 | (boolean1 ? 0 : 16)));
		return matrix4d;
	}

	public Matrix4d scaleLocal(double double1, Matrix4d matrix4d) {
		return this.scaleLocal(double1, double1, double1, matrix4d);
	}

	public Matrix4d scaleLocal(double double1) {
		return this.scaleLocal(double1, this);
	}

	public Matrix4d scaleLocal(double double1, double double2, double double3) {
		return this.scaleLocal(double1, double2, double3, this);
	}

	public Matrix4d scaleAroundLocal(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
		boolean boolean1 = Math.absEqualsOne(double1) && Math.absEqualsOne(double2) && Math.absEqualsOne(double3);
		matrix4d._m00(double1 * (this.m00 - double4 * this.m03) + double4 * this.m03)._m01(double2 * (this.m01 - double5 * this.m03) + double5 * this.m03)._m02(double3 * (this.m02 - double6 * this.m03) + double6 * this.m03)._m03(this.m03)._m10(double1 * (this.m10 - double4 * this.m13) + double4 * this.m13)._m11(double2 * (this.m11 - double5 * this.m13) + double5 * this.m13)._m12(double3 * (this.m12 - double6 * this.m13) + double6 * this.m13)._m13(this.m13)._m20(double1 * (this.m20 - double4 * this.m23) + double4 * this.m23)._m21(double2 * (this.m21 - double5 * this.m23) + double5 * this.m23)._m22(double3 * (this.m22 - double6 * this.m23) + double6 * this.m23)._m23(this.m23)._m30(double1 * (this.m30 - double4 * this.m33) + double4 * this.m33)._m31(double2 * (this.m31 - double5 * this.m33) + double5 * this.m33)._m32(double3 * (this.m32 - double6 * this.m33) + double6 * this.m33)._m33(this.m33)._properties(this.properties & ~(13 | (boolean1 ? 0 : 16)));
		return matrix4d;
	}

	public Matrix4d scaleAroundLocal(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.scaleAroundLocal(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4d scaleAroundLocal(double double1, double double2, double double3, double double4) {
		return this.scaleAroundLocal(double1, double1, double1, double2, double3, double4, this);
	}

	public Matrix4d scaleAroundLocal(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		return this.scaleAroundLocal(double1, double1, double1, double2, double3, double4, matrix4d);
	}

	public Matrix4d rotate(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.rotation(double1, double2, double3, double4);
		} else if ((this.properties & 8) != 0) {
			return this.rotateTranslation(double1, double2, double3, double4, matrix4d);
		} else {
			return (this.properties & 2) != 0 ? this.rotateAffine(double1, double2, double3, double4, matrix4d) : this.rotateGeneric(double1, double2, double3, double4, matrix4d);
		}
	}

	private Matrix4d rotateGeneric(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		if (double3 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double2)) {
			return this.rotateX(double2 * double1, matrix4d);
		} else if (double2 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double3)) {
			return this.rotateY(double3 * double1, matrix4d);
		} else {
			return double2 == 0.0 && double3 == 0.0 && Math.absEqualsOne(double4) ? this.rotateZ(double4 * double1, matrix4d) : this.rotateGenericInternal(double1, double2, double3, double4, matrix4d);
		}
	}

	private Matrix4d rotateGenericInternal(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
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
		double double26 = this.m03 * double14 + this.m13 * double15 + this.m23 * double16;
		double double27 = this.m00 * double17 + this.m10 * double18 + this.m20 * double19;
		double double28 = this.m01 * double17 + this.m11 * double18 + this.m21 * double19;
		double double29 = this.m02 * double17 + this.m12 * double18 + this.m22 * double19;
		double double30 = this.m03 * double17 + this.m13 * double18 + this.m23 * double19;
		matrix4d._m20(this.m00 * double20 + this.m10 * double21 + this.m20 * double22)._m21(this.m01 * double20 + this.m11 * double21 + this.m21 * double22)._m22(this.m02 * double20 + this.m12 * double21 + this.m22 * double22)._m23(this.m03 * double20 + this.m13 * double21 + this.m23 * double22)._m00(double23)._m01(double24)._m02(double25)._m03(double26)._m10(double27)._m11(double28)._m12(double29)._m13(double30)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotate(double double1, double double2, double double3, double double4) {
		return this.rotate(double1, double2, double3, double4, this);
	}

	public Matrix4d rotateTranslation(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		double double5 = this.m30;
		double double6 = this.m31;
		double double7 = this.m32;
		if (double3 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double2)) {
			return matrix4d.rotationX(double2 * double1).setTranslation(double5, double6, double7);
		} else if (double2 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double3)) {
			return matrix4d.rotationY(double3 * double1).setTranslation(double5, double6, double7);
		} else {
			return double2 == 0.0 && double3 == 0.0 && Math.absEqualsOne(double4) ? matrix4d.rotationZ(double4 * double1).setTranslation(double5, double6, double7) : this.rotateTranslationInternal(double1, double2, double3, double4, matrix4d);
		}
	}

	private Matrix4d rotateTranslationInternal(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
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
		matrix4d._m20(double20)._m21(double21)._m22(double22)._m00(double14)._m01(double15)._m02(double16)._m03(0.0)._m10(double17)._m11(double18)._m12(double19)._m13(0.0)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateAffine(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		if (double3 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double2)) {
			return this.rotateX(double2 * double1, matrix4d);
		} else if (double2 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double3)) {
			return this.rotateY(double3 * double1, matrix4d);
		} else {
			return double2 == 0.0 && double3 == 0.0 && Math.absEqualsOne(double4) ? this.rotateZ(double4 * double1, matrix4d) : this.rotateAffineInternal(double1, double2, double3, double4, matrix4d);
		}
	}

	private Matrix4d rotateAffineInternal(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
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
		matrix4d._m20(this.m00 * double20 + this.m10 * double21 + this.m20 * double22)._m21(this.m01 * double20 + this.m11 * double21 + this.m21 * double22)._m22(this.m02 * double20 + this.m12 * double21 + this.m22 * double22)._m23(0.0)._m00(double23)._m01(double24)._m02(double25)._m03(0.0)._m10(double26)._m11(double27)._m12(double28)._m13(0.0)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateAffine(double double1, double double2, double double3, double double4) {
		return this.rotateAffine(double1, double2, double3, double4, this);
	}

	public Matrix4d rotateAround(Quaterniondc quaterniondc, double double1, double double2, double double3) {
		return this.rotateAround(quaterniondc, double1, double2, double3, this);
	}

	public Matrix4d rotateAroundAffine(Quaterniondc quaterniondc, double double1, double double2, double double3, Matrix4d matrix4d) {
		double double4 = quaterniondc.w() * quaterniondc.w();
		double double5 = quaterniondc.x() * quaterniondc.x();
		double double6 = quaterniondc.y() * quaterniondc.y();
		double double7 = quaterniondc.z() * quaterniondc.z();
		double double8 = quaterniondc.z() * quaterniondc.w();
		double double9 = double8 + double8;
		double double10 = quaterniondc.x() * quaterniondc.y();
		double double11 = double10 + double10;
		double double12 = quaterniondc.x() * quaterniondc.z();
		double double13 = double12 + double12;
		double double14 = quaterniondc.y() * quaterniondc.w();
		double double15 = double14 + double14;
		double double16 = quaterniondc.y() * quaterniondc.z();
		double double17 = double16 + double16;
		double double18 = quaterniondc.x() * quaterniondc.w();
		double double19 = double18 + double18;
		double double20 = double4 + double5 - double7 - double6;
		double double21 = double11 + double9;
		double double22 = double13 - double15;
		double double23 = -double9 + double11;
		double double24 = double6 - double7 + double4 - double5;
		double double25 = double17 + double19;
		double double26 = double15 + double13;
		double double27 = double17 - double19;
		double double28 = double7 - double6 - double5 + double4;
		double double29 = this.m00 * double1 + this.m10 * double2 + this.m20 * double3 + this.m30;
		double double30 = this.m01 * double1 + this.m11 * double2 + this.m21 * double3 + this.m31;
		double double31 = this.m02 * double1 + this.m12 * double2 + this.m22 * double3 + this.m32;
		double double32 = this.m00 * double20 + this.m10 * double21 + this.m20 * double22;
		double double33 = this.m01 * double20 + this.m11 * double21 + this.m21 * double22;
		double double34 = this.m02 * double20 + this.m12 * double21 + this.m22 * double22;
		double double35 = this.m00 * double23 + this.m10 * double24 + this.m20 * double25;
		double double36 = this.m01 * double23 + this.m11 * double24 + this.m21 * double25;
		double double37 = this.m02 * double23 + this.m12 * double24 + this.m22 * double25;
		matrix4d._m20(this.m00 * double26 + this.m10 * double27 + this.m20 * double28)._m21(this.m01 * double26 + this.m11 * double27 + this.m21 * double28)._m22(this.m02 * double26 + this.m12 * double27 + this.m22 * double28)._m23(0.0)._m00(double32)._m01(double33)._m02(double34)._m03(0.0)._m10(double35)._m11(double36)._m12(double37)._m13(0.0)._m30(-double32 * double1 - double35 * double2 - this.m20 * double3 + double29)._m31(-double33 * double1 - double36 * double2 - this.m21 * double3 + double30)._m32(-double34 * double1 - double37 * double2 - this.m22 * double3 + double31)._m33(1.0)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateAround(Quaterniondc quaterniondc, double double1, double double2, double double3, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return this.rotationAround(quaterniondc, double1, double2, double3);
		} else {
			return (this.properties & 2) != 0 ? this.rotateAroundAffine(quaterniondc, double1, double2, double3, this) : this.rotateAroundGeneric(quaterniondc, double1, double2, double3, this);
		}
	}

	private Matrix4d rotateAroundGeneric(Quaterniondc quaterniondc, double double1, double double2, double double3, Matrix4d matrix4d) {
		double double4 = quaterniondc.w() * quaterniondc.w();
		double double5 = quaterniondc.x() * quaterniondc.x();
		double double6 = quaterniondc.y() * quaterniondc.y();
		double double7 = quaterniondc.z() * quaterniondc.z();
		double double8 = quaterniondc.z() * quaterniondc.w();
		double double9 = double8 + double8;
		double double10 = quaterniondc.x() * quaterniondc.y();
		double double11 = double10 + double10;
		double double12 = quaterniondc.x() * quaterniondc.z();
		double double13 = double12 + double12;
		double double14 = quaterniondc.y() * quaterniondc.w();
		double double15 = double14 + double14;
		double double16 = quaterniondc.y() * quaterniondc.z();
		double double17 = double16 + double16;
		double double18 = quaterniondc.x() * quaterniondc.w();
		double double19 = double18 + double18;
		double double20 = double4 + double5 - double7 - double6;
		double double21 = double11 + double9;
		double double22 = double13 - double15;
		double double23 = -double9 + double11;
		double double24 = double6 - double7 + double4 - double5;
		double double25 = double17 + double19;
		double double26 = double15 + double13;
		double double27 = double17 - double19;
		double double28 = double7 - double6 - double5 + double4;
		double double29 = this.m00 * double1 + this.m10 * double2 + this.m20 * double3 + this.m30;
		double double30 = this.m01 * double1 + this.m11 * double2 + this.m21 * double3 + this.m31;
		double double31 = this.m02 * double1 + this.m12 * double2 + this.m22 * double3 + this.m32;
		double double32 = this.m00 * double20 + this.m10 * double21 + this.m20 * double22;
		double double33 = this.m01 * double20 + this.m11 * double21 + this.m21 * double22;
		double double34 = this.m02 * double20 + this.m12 * double21 + this.m22 * double22;
		double double35 = this.m03 * double20 + this.m13 * double21 + this.m23 * double22;
		double double36 = this.m00 * double23 + this.m10 * double24 + this.m20 * double25;
		double double37 = this.m01 * double23 + this.m11 * double24 + this.m21 * double25;
		double double38 = this.m02 * double23 + this.m12 * double24 + this.m22 * double25;
		double double39 = this.m03 * double23 + this.m13 * double24 + this.m23 * double25;
		matrix4d._m20(this.m00 * double26 + this.m10 * double27 + this.m20 * double28)._m21(this.m01 * double26 + this.m11 * double27 + this.m21 * double28)._m22(this.m02 * double26 + this.m12 * double27 + this.m22 * double28)._m23(this.m03 * double26 + this.m13 * double27 + this.m23 * double28)._m00(double32)._m01(double33)._m02(double34)._m03(double35)._m10(double36)._m11(double37)._m12(double38)._m13(double39)._m30(-double32 * double1 - double36 * double2 - this.m20 * double3 + double29)._m31(-double33 * double1 - double37 * double2 - this.m21 * double3 + double30)._m32(-double34 * double1 - double38 * double2 - this.m22 * double3 + double31)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotationAround(Quaterniondc quaterniondc, double double1, double double2, double double3) {
		double double4 = quaterniondc.w() * quaterniondc.w();
		double double5 = quaterniondc.x() * quaterniondc.x();
		double double6 = quaterniondc.y() * quaterniondc.y();
		double double7 = quaterniondc.z() * quaterniondc.z();
		double double8 = quaterniondc.z() * quaterniondc.w();
		double double9 = double8 + double8;
		double double10 = quaterniondc.x() * quaterniondc.y();
		double double11 = double10 + double10;
		double double12 = quaterniondc.x() * quaterniondc.z();
		double double13 = double12 + double12;
		double double14 = quaterniondc.y() * quaterniondc.w();
		double double15 = double14 + double14;
		double double16 = quaterniondc.y() * quaterniondc.z();
		double double17 = double16 + double16;
		double double18 = quaterniondc.x() * quaterniondc.w();
		double double19 = double18 + double18;
		this._m20(double15 + double13);
		this._m21(double17 - double19);
		this._m22(double7 - double6 - double5 + double4);
		this._m23(0.0);
		this._m00(double4 + double5 - double7 - double6);
		this._m01(double11 + double9);
		this._m02(double13 - double15);
		this._m03(0.0);
		this._m10(-double9 + double11);
		this._m11(double6 - double7 + double4 - double5);
		this._m12(double17 + double19);
		this._m13(0.0);
		this._m30(-this.m00 * double1 - this.m10 * double2 - this.m20 * double3 + double1);
		this._m31(-this.m01 * double1 - this.m11 * double2 - this.m21 * double3 + double2);
		this._m32(-this.m02 * double1 - this.m12 * double2 - this.m22 * double3 + double3);
		this._m33(1.0);
		this.properties = 18;
		return this;
	}

	public Matrix4d rotateLocal(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.rotation(double1, double2, double3, double4) : this.rotateLocalGeneric(double1, double2, double3, double4, matrix4d);
	}

	private Matrix4d rotateLocalGeneric(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		if (double3 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double2)) {
			return this.rotateLocalX(double2 * double1, matrix4d);
		} else if (double2 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double3)) {
			return this.rotateLocalY(double3 * double1, matrix4d);
		} else {
			return double2 == 0.0 && double3 == 0.0 && Math.absEqualsOne(double4) ? this.rotateLocalZ(double4 * double1, matrix4d) : this.rotateLocalGenericInternal(double1, double2, double3, double4, matrix4d);
		}
	}

	private Matrix4d rotateLocalGenericInternal(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
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
		double double32 = double14 * this.m30 + double17 * this.m31 + double20 * this.m32;
		double double33 = double15 * this.m30 + double18 * this.m31 + double21 * this.m32;
		double double34 = double16 * this.m30 + double19 * this.m31 + double22 * this.m32;
		matrix4d._m00(double23)._m01(double24)._m02(double25)._m03(this.m03)._m10(double26)._m11(double27)._m12(double28)._m13(this.m13)._m20(double29)._m21(double30)._m22(double31)._m23(this.m23)._m30(double32)._m31(double33)._m32(double34)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateLocal(double double1, double double2, double double3, double double4) {
		return this.rotateLocal(double1, double2, double3, double4, this);
	}

	public Matrix4d rotateAroundLocal(Quaterniondc quaterniondc, double double1, double double2, double double3, Matrix4d matrix4d) {
		double double4 = quaterniondc.w() * quaterniondc.w();
		double double5 = quaterniondc.x() * quaterniondc.x();
		double double6 = quaterniondc.y() * quaterniondc.y();
		double double7 = quaterniondc.z() * quaterniondc.z();
		double double8 = quaterniondc.z() * quaterniondc.w();
		double double9 = quaterniondc.x() * quaterniondc.y();
		double double10 = quaterniondc.x() * quaterniondc.z();
		double double11 = quaterniondc.y() * quaterniondc.w();
		double double12 = quaterniondc.y() * quaterniondc.z();
		double double13 = quaterniondc.x() * quaterniondc.w();
		double double14 = double4 + double5 - double7 - double6;
		double double15 = double9 + double8 + double8 + double9;
		double double16 = double10 - double11 + double10 - double11;
		double double17 = -double8 + double9 - double8 + double9;
		double double18 = double6 - double7 + double4 - double5;
		double double19 = double12 + double12 + double13 + double13;
		double double20 = double11 + double10 + double10 + double11;
		double double21 = double12 + double12 - double13 - double13;
		double double22 = double7 - double6 - double5 + double4;
		double double23 = this.m00 - double1 * this.m03;
		double double24 = this.m01 - double2 * this.m03;
		double double25 = this.m02 - double3 * this.m03;
		double double26 = this.m10 - double1 * this.m13;
		double double27 = this.m11 - double2 * this.m13;
		double double28 = this.m12 - double3 * this.m13;
		double double29 = this.m20 - double1 * this.m23;
		double double30 = this.m21 - double2 * this.m23;
		double double31 = this.m22 - double3 * this.m23;
		double double32 = this.m30 - double1 * this.m33;
		double double33 = this.m31 - double2 * this.m33;
		double double34 = this.m32 - double3 * this.m33;
		matrix4d._m00(double14 * double23 + double17 * double24 + double20 * double25 + double1 * this.m03)._m01(double15 * double23 + double18 * double24 + double21 * double25 + double2 * this.m03)._m02(double16 * double23 + double19 * double24 + double22 * double25 + double3 * this.m03)._m03(this.m03)._m10(double14 * double26 + double17 * double27 + double20 * double28 + double1 * this.m13)._m11(double15 * double26 + double18 * double27 + double21 * double28 + double2 * this.m13)._m12(double16 * double26 + double19 * double27 + double22 * double28 + double3 * this.m13)._m13(this.m13)._m20(double14 * double29 + double17 * double30 + double20 * double31 + double1 * this.m23)._m21(double15 * double29 + double18 * double30 + double21 * double31 + double2 * this.m23)._m22(double16 * double29 + double19 * double30 + double22 * double31 + double3 * this.m23)._m23(this.m23)._m30(double14 * double32 + double17 * double33 + double20 * double34 + double1 * this.m33)._m31(double15 * double32 + double18 * double33 + double21 * double34 + double2 * this.m33)._m32(double16 * double32 + double19 * double33 + double22 * double34 + double3 * this.m33)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateAroundLocal(Quaterniondc quaterniondc, double double1, double double2, double double3) {
		return this.rotateAroundLocal(quaterniondc, double1, double2, double3, this);
	}

	public Matrix4d translate(Vector3dc vector3dc) {
		return this.translate(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4d translate(Vector3dc vector3dc, Matrix4d matrix4d) {
		return this.translate(vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix4d);
	}

	public Matrix4d translate(Vector3fc vector3fc) {
		return this.translate((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Matrix4d translate(Vector3fc vector3fc, Matrix4d matrix4d) {
		return this.translate((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), matrix4d);
	}

	public Matrix4d translate(double double1, double double2, double double3, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.translation(double1, double2, double3) : this.translateGeneric(double1, double2, double3, matrix4d);
	}

	private Matrix4d translateGeneric(double double1, double double2, double double3, Matrix4d matrix4d) {
		matrix4d._m00(this.m00)._m01(this.m01)._m02(this.m02)._m03(this.m03)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m13(this.m13)._m20(this.m20)._m21(this.m21)._m22(this.m22)._m23(this.m23)._m30(Math.fma(this.m00, double1, Math.fma(this.m10, double2, Math.fma(this.m20, double3, this.m30))))._m31(Math.fma(this.m01, double1, Math.fma(this.m11, double2, Math.fma(this.m21, double3, this.m31))))._m32(Math.fma(this.m02, double1, Math.fma(this.m12, double2, Math.fma(this.m22, double3, this.m32))))._m33(Math.fma(this.m03, double1, Math.fma(this.m13, double2, Math.fma(this.m23, double3, this.m33))))._properties(this.properties & -6);
		return matrix4d;
	}

	public Matrix4d translate(double double1, double double2, double double3) {
		if ((this.properties & 4) != 0) {
			return this.translation(double1, double2, double3);
		} else {
			this._m30(Math.fma(this.m00, double1, Math.fma(this.m10, double2, Math.fma(this.m20, double3, this.m30))));
			this._m31(Math.fma(this.m01, double1, Math.fma(this.m11, double2, Math.fma(this.m21, double3, this.m31))));
			this._m32(Math.fma(this.m02, double1, Math.fma(this.m12, double2, Math.fma(this.m22, double3, this.m32))));
			this._m33(Math.fma(this.m03, double1, Math.fma(this.m13, double2, Math.fma(this.m23, double3, this.m33))));
			this.properties &= -6;
			return this;
		}
	}

	public Matrix4d translateLocal(Vector3fc vector3fc) {
		return this.translateLocal((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Matrix4d translateLocal(Vector3fc vector3fc, Matrix4d matrix4d) {
		return this.translateLocal((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), matrix4d);
	}

	public Matrix4d translateLocal(Vector3dc vector3dc) {
		return this.translateLocal(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4d translateLocal(Vector3dc vector3dc, Matrix4d matrix4d) {
		return this.translateLocal(vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix4d);
	}

	public Matrix4d translateLocal(double double1, double double2, double double3, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.translation(double1, double2, double3) : this.translateLocalGeneric(double1, double2, double3, matrix4d);
	}

	private Matrix4d translateLocalGeneric(double double1, double double2, double double3, Matrix4d matrix4d) {
		double double4 = this.m00 + double1 * this.m03;
		double double5 = this.m01 + double2 * this.m03;
		double double6 = this.m02 + double3 * this.m03;
		double double7 = this.m10 + double1 * this.m13;
		double double8 = this.m11 + double2 * this.m13;
		double double9 = this.m12 + double3 * this.m13;
		double double10 = this.m20 + double1 * this.m23;
		double double11 = this.m21 + double2 * this.m23;
		double double12 = this.m22 + double3 * this.m23;
		double double13 = this.m30 + double1 * this.m33;
		double double14 = this.m31 + double2 * this.m33;
		double double15 = this.m32 + double3 * this.m33;
		return matrix4d._m00(double4)._m01(double5)._m02(double6)._m03(this.m03)._m10(double7)._m11(double8)._m12(double9)._m13(this.m13)._m20(double10)._m21(double11)._m22(double12)._m23(this.m23)._m30(double13)._m31(double14)._m32(double15)._m33(this.m33)._properties(this.properties & -6);
	}

	public Matrix4d translateLocal(double double1, double double2, double double3) {
		return this.translateLocal(double1, double2, double3, this);
	}

	public Matrix4d rotateLocalX(double double1, Matrix4d matrix4d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = double2 * this.m01 + double3 * this.m02;
		double double5 = double2 * this.m11 + double3 * this.m12;
		double double6 = double2 * this.m21 + double3 * this.m22;
		double double7 = double2 * this.m31 + double3 * this.m32;
		matrix4d._m00(this.m00)._m01(double3 * this.m01 - double2 * this.m02)._m02(double4)._m03(this.m03)._m10(this.m10)._m11(double3 * this.m11 - double2 * this.m12)._m12(double5)._m13(this.m13)._m20(this.m20)._m21(double3 * this.m21 - double2 * this.m22)._m22(double6)._m23(this.m23)._m30(this.m30)._m31(double3 * this.m31 - double2 * this.m32)._m32(double7)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateLocalX(double double1) {
		return this.rotateLocalX(double1, this);
	}

	public Matrix4d rotateLocalY(double double1, Matrix4d matrix4d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = -double2 * this.m00 + double3 * this.m02;
		double double5 = -double2 * this.m10 + double3 * this.m12;
		double double6 = -double2 * this.m20 + double3 * this.m22;
		double double7 = -double2 * this.m30 + double3 * this.m32;
		matrix4d._m00(double3 * this.m00 + double2 * this.m02)._m01(this.m01)._m02(double4)._m03(this.m03)._m10(double3 * this.m10 + double2 * this.m12)._m11(this.m11)._m12(double5)._m13(this.m13)._m20(double3 * this.m20 + double2 * this.m22)._m21(this.m21)._m22(double6)._m23(this.m23)._m30(double3 * this.m30 + double2 * this.m32)._m31(this.m31)._m32(double7)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateLocalY(double double1) {
		return this.rotateLocalY(double1, this);
	}

	public Matrix4d rotateLocalZ(double double1, Matrix4d matrix4d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = double2 * this.m00 + double3 * this.m01;
		double double5 = double2 * this.m10 + double3 * this.m11;
		double double6 = double2 * this.m20 + double3 * this.m21;
		double double7 = double2 * this.m30 + double3 * this.m31;
		matrix4d._m00(double3 * this.m00 - double2 * this.m01)._m01(double4)._m02(this.m02)._m03(this.m03)._m10(double3 * this.m10 - double2 * this.m11)._m11(double5)._m12(this.m12)._m13(this.m13)._m20(double3 * this.m20 - double2 * this.m21)._m21(double6)._m22(this.m22)._m23(this.m23)._m30(double3 * this.m30 - double2 * this.m31)._m31(double7)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateLocalZ(double double1) {
		return this.rotateLocalZ(double1, this);
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeDouble(this.m00);
		objectOutput.writeDouble(this.m01);
		objectOutput.writeDouble(this.m02);
		objectOutput.writeDouble(this.m03);
		objectOutput.writeDouble(this.m10);
		objectOutput.writeDouble(this.m11);
		objectOutput.writeDouble(this.m12);
		objectOutput.writeDouble(this.m13);
		objectOutput.writeDouble(this.m20);
		objectOutput.writeDouble(this.m21);
		objectOutput.writeDouble(this.m22);
		objectOutput.writeDouble(this.m23);
		objectOutput.writeDouble(this.m30);
		objectOutput.writeDouble(this.m31);
		objectOutput.writeDouble(this.m32);
		objectOutput.writeDouble(this.m33);
	}

	public void readExternal(ObjectInput objectInput) throws IOException {
		this._m00(objectInput.readDouble())._m01(objectInput.readDouble())._m02(objectInput.readDouble())._m03(objectInput.readDouble())._m10(objectInput.readDouble())._m11(objectInput.readDouble())._m12(objectInput.readDouble())._m13(objectInput.readDouble())._m20(objectInput.readDouble())._m21(objectInput.readDouble())._m22(objectInput.readDouble())._m23(objectInput.readDouble())._m30(objectInput.readDouble())._m31(objectInput.readDouble())._m32(objectInput.readDouble())._m33(objectInput.readDouble()).determineProperties();
	}

	public Matrix4d rotateX(double double1, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.rotationX(double1);
		} else if ((this.properties & 8) != 0) {
			double double2 = this.m30;
			double double3 = this.m31;
			double double4 = this.m32;
			return matrix4d.rotationX(double1).setTranslation(double2, double3, double4);
		} else {
			return this.rotateXInternal(double1, matrix4d);
		}
	}

	private Matrix4d rotateXInternal(double double1, Matrix4d matrix4d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = -double2;
		double double5 = this.m10 * double3 + this.m20 * double2;
		double double6 = this.m11 * double3 + this.m21 * double2;
		double double7 = this.m12 * double3 + this.m22 * double2;
		double double8 = this.m13 * double3 + this.m23 * double2;
		matrix4d._m20(this.m10 * double4 + this.m20 * double3)._m21(this.m11 * double4 + this.m21 * double3)._m22(this.m12 * double4 + this.m22 * double3)._m23(this.m13 * double4 + this.m23 * double3)._m10(double5)._m11(double6)._m12(double7)._m13(double8)._m00(this.m00)._m01(this.m01)._m02(this.m02)._m03(this.m03)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateX(double double1) {
		return this.rotateX(double1, this);
	}

	public Matrix4d rotateY(double double1, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.rotationY(double1);
		} else if ((this.properties & 8) != 0) {
			double double2 = this.m30;
			double double3 = this.m31;
			double double4 = this.m32;
			return matrix4d.rotationY(double1).setTranslation(double2, double3, double4);
		} else {
			return this.rotateYInternal(double1, matrix4d);
		}
	}

	private Matrix4d rotateYInternal(double double1, Matrix4d matrix4d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = -double2;
		double double5 = this.m00 * double3 + this.m20 * double4;
		double double6 = this.m01 * double3 + this.m21 * double4;
		double double7 = this.m02 * double3 + this.m22 * double4;
		double double8 = this.m03 * double3 + this.m23 * double4;
		matrix4d._m20(this.m00 * double2 + this.m20 * double3)._m21(this.m01 * double2 + this.m21 * double3)._m22(this.m02 * double2 + this.m22 * double3)._m23(this.m03 * double2 + this.m23 * double3)._m00(double5)._m01(double6)._m02(double7)._m03(double8)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m13(this.m13)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateY(double double1) {
		return this.rotateY(double1, this);
	}

	public Matrix4d rotateZ(double double1, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.rotationZ(double1);
		} else if ((this.properties & 8) != 0) {
			double double2 = this.m30;
			double double3 = this.m31;
			double double4 = this.m32;
			return matrix4d.rotationZ(double1).setTranslation(double2, double3, double4);
		} else {
			return this.rotateZInternal(double1, matrix4d);
		}
	}

	private Matrix4d rotateZInternal(double double1, Matrix4d matrix4d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		return this.rotateTowardsXY(double2, double3, matrix4d);
	}

	public Matrix4d rotateZ(double double1) {
		return this.rotateZ(double1, this);
	}

	public Matrix4d rotateTowardsXY(double double1, double double2) {
		return this.rotateTowardsXY(double1, double2, this);
	}

	public Matrix4d rotateTowardsXY(double double1, double double2, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.rotationTowardsXY(double1, double2);
		} else {
			double double3 = -double1;
			double double4 = this.m00 * double2 + this.m10 * double1;
			double double5 = this.m01 * double2 + this.m11 * double1;
			double double6 = this.m02 * double2 + this.m12 * double1;
			double double7 = this.m03 * double2 + this.m13 * double1;
			matrix4d._m10(this.m00 * double3 + this.m10 * double2)._m11(this.m01 * double3 + this.m11 * double2)._m12(this.m02 * double3 + this.m12 * double2)._m13(this.m03 * double3 + this.m13 * double2)._m00(double4)._m01(double5)._m02(double6)._m03(double7)._m20(this.m20)._m21(this.m21)._m22(this.m22)._m23(this.m23)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
			return matrix4d;
		}
	}

	public Matrix4d rotateXYZ(Vector3d vector3d) {
		return this.rotateXYZ(vector3d.x, vector3d.y, vector3d.z);
	}

	public Matrix4d rotateXYZ(double double1, double double2, double double3) {
		return this.rotateXYZ(double1, double2, double3, this);
	}

	public Matrix4d rotateXYZ(double double1, double double2, double double3, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.rotationXYZ(double1, double2, double3);
		} else if ((this.properties & 8) != 0) {
			double double4 = this.m30;
			double double5 = this.m31;
			double double6 = this.m32;
			return matrix4d.rotationXYZ(double1, double2, double3).setTranslation(double4, double5, double6);
		} else {
			return (this.properties & 2) != 0 ? matrix4d.rotateAffineXYZ(double1, double2, double3) : this.rotateXYZInternal(double1, double2, double3, matrix4d);
		}
	}

	private Matrix4d rotateXYZInternal(double double1, double double2, double double3, Matrix4d matrix4d) {
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
		double double16 = this.m13 * double5 + this.m23 * double4;
		double double17 = this.m10 * double10 + this.m20 * double5;
		double double18 = this.m11 * double10 + this.m21 * double5;
		double double19 = this.m12 * double10 + this.m22 * double5;
		double double20 = this.m13 * double10 + this.m23 * double5;
		double double21 = this.m00 * double7 + double17 * double11;
		double double22 = this.m01 * double7 + double18 * double11;
		double double23 = this.m02 * double7 + double19 * double11;
		double double24 = this.m03 * double7 + double20 * double11;
		matrix4d._m20(this.m00 * double6 + double17 * double7)._m21(this.m01 * double6 + double18 * double7)._m22(this.m02 * double6 + double19 * double7)._m23(this.m03 * double6 + double20 * double7)._m00(double21 * double9 + double13 * double8)._m01(double22 * double9 + double14 * double8)._m02(double23 * double9 + double15 * double8)._m03(double24 * double9 + double16 * double8)._m10(double21 * double12 + double13 * double9)._m11(double22 * double12 + double14 * double9)._m12(double23 * double12 + double15 * double9)._m13(double24 * double12 + double16 * double9)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateAffineXYZ(double double1, double double2, double double3) {
		return this.rotateAffineXYZ(double1, double2, double3, this);
	}

	public Matrix4d rotateAffineXYZ(double double1, double double2, double double3, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.rotationXYZ(double1, double2, double3);
		} else if ((this.properties & 8) != 0) {
			double double4 = this.m30;
			double double5 = this.m31;
			double double6 = this.m32;
			return matrix4d.rotationXYZ(double1, double2, double3).setTranslation(double4, double5, double6);
		} else {
			return this.rotateAffineXYZInternal(double1, double2, double3, matrix4d);
		}
	}

	private Matrix4d rotateAffineXYZInternal(double double1, double double2, double double3, Matrix4d matrix4d) {
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
		matrix4d._m20(this.m00 * double6 + double16 * double7)._m21(this.m01 * double6 + double17 * double7)._m22(this.m02 * double6 + double18 * double7)._m23(0.0)._m00(double19 * double9 + double13 * double8)._m01(double20 * double9 + double14 * double8)._m02(double21 * double9 + double15 * double8)._m03(0.0)._m10(double19 * double12 + double13 * double9)._m11(double20 * double12 + double14 * double9)._m12(double21 * double12 + double15 * double9)._m13(0.0)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateZYX(Vector3d vector3d) {
		return this.rotateZYX(vector3d.z, vector3d.y, vector3d.x);
	}

	public Matrix4d rotateZYX(double double1, double double2, double double3) {
		return this.rotateZYX(double1, double2, double3, this);
	}

	public Matrix4d rotateZYX(double double1, double double2, double double3, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.rotationZYX(double1, double2, double3);
		} else if ((this.properties & 8) != 0) {
			double double4 = this.m30;
			double double5 = this.m31;
			double double6 = this.m32;
			return matrix4d.rotationZYX(double1, double2, double3).setTranslation(double4, double5, double6);
		} else {
			return (this.properties & 2) != 0 ? matrix4d.rotateAffineZYX(double1, double2, double3) : this.rotateZYXInternal(double1, double2, double3, matrix4d);
		}
	}

	private Matrix4d rotateZYXInternal(double double1, double double2, double double3, Matrix4d matrix4d) {
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
		double double16 = this.m03 * double9 + this.m13 * double8;
		double double17 = this.m00 * double10 + this.m10 * double9;
		double double18 = this.m01 * double10 + this.m11 * double9;
		double double19 = this.m02 * double10 + this.m12 * double9;
		double double20 = this.m03 * double10 + this.m13 * double9;
		double double21 = double13 * double6 + this.m20 * double7;
		double double22 = double14 * double6 + this.m21 * double7;
		double double23 = double15 * double6 + this.m22 * double7;
		double double24 = double16 * double6 + this.m23 * double7;
		matrix4d._m00(double13 * double7 + this.m20 * double11)._m01(double14 * double7 + this.m21 * double11)._m02(double15 * double7 + this.m22 * double11)._m03(double16 * double7 + this.m23 * double11)._m10(double17 * double5 + double21 * double4)._m11(double18 * double5 + double22 * double4)._m12(double19 * double5 + double23 * double4)._m13(double20 * double5 + double24 * double4)._m20(double17 * double12 + double21 * double5)._m21(double18 * double12 + double22 * double5)._m22(double19 * double12 + double23 * double5)._m23(double20 * double12 + double24 * double5)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateAffineZYX(double double1, double double2, double double3) {
		return this.rotateAffineZYX(double1, double2, double3, this);
	}

	public Matrix4d rotateAffineZYX(double double1, double double2, double double3, Matrix4d matrix4d) {
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
		matrix4d._m00(double13 * double7 + this.m20 * double11)._m01(double14 * double7 + this.m21 * double11)._m02(double15 * double7 + this.m22 * double11)._m03(0.0)._m10(double16 * double5 + double19 * double4)._m11(double17 * double5 + double20 * double4)._m12(double18 * double5 + double21 * double4)._m13(0.0)._m20(double16 * double12 + double19 * double5)._m21(double17 * double12 + double20 * double5)._m22(double18 * double12 + double21 * double5)._m23(0.0)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateYXZ(Vector3d vector3d) {
		return this.rotateYXZ(vector3d.y, vector3d.x, vector3d.z);
	}

	public Matrix4d rotateYXZ(double double1, double double2, double double3) {
		return this.rotateYXZ(double1, double2, double3, this);
	}

	public Matrix4d rotateYXZ(double double1, double double2, double double3, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.rotationYXZ(double1, double2, double3);
		} else if ((this.properties & 8) != 0) {
			double double4 = this.m30;
			double double5 = this.m31;
			double double6 = this.m32;
			return matrix4d.rotationYXZ(double1, double2, double3).setTranslation(double4, double5, double6);
		} else {
			return (this.properties & 2) != 0 ? matrix4d.rotateAffineYXZ(double1, double2, double3) : this.rotateYXZInternal(double1, double2, double3, matrix4d);
		}
	}

	private Matrix4d rotateYXZInternal(double double1, double double2, double double3, Matrix4d matrix4d) {
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
		double double16 = this.m03 * double6 + this.m23 * double7;
		double double17 = this.m00 * double7 + this.m20 * double10;
		double double18 = this.m01 * double7 + this.m21 * double10;
		double double19 = this.m02 * double7 + this.m22 * double10;
		double double20 = this.m03 * double7 + this.m23 * double10;
		double double21 = this.m10 * double5 + double13 * double4;
		double double22 = this.m11 * double5 + double14 * double4;
		double double23 = this.m12 * double5 + double15 * double4;
		double double24 = this.m13 * double5 + double16 * double4;
		matrix4d._m20(this.m10 * double11 + double13 * double5)._m21(this.m11 * double11 + double14 * double5)._m22(this.m12 * double11 + double15 * double5)._m23(this.m13 * double11 + double16 * double5)._m00(double17 * double9 + double21 * double8)._m01(double18 * double9 + double22 * double8)._m02(double19 * double9 + double23 * double8)._m03(double20 * double9 + double24 * double8)._m10(double17 * double12 + double21 * double9)._m11(double18 * double12 + double22 * double9)._m12(double19 * double12 + double23 * double9)._m13(double20 * double12 + double24 * double9)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateAffineYXZ(double double1, double double2, double double3) {
		return this.rotateAffineYXZ(double1, double2, double3, this);
	}

	public Matrix4d rotateAffineYXZ(double double1, double double2, double double3, Matrix4d matrix4d) {
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
		matrix4d._m20(this.m10 * double11 + double13 * double5)._m21(this.m11 * double11 + double14 * double5)._m22(this.m12 * double11 + double15 * double5)._m23(0.0)._m00(double16 * double9 + double19 * double8)._m01(double17 * double9 + double20 * double8)._m02(double18 * double9 + double21 * double8)._m03(0.0)._m10(double16 * double12 + double19 * double9)._m11(double17 * double12 + double20 * double9)._m12(double18 * double12 + double21 * double9)._m13(0.0)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotation(AxisAngle4f axisAngle4f) {
		return this.rotation((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z);
	}

	public Matrix4d rotation(AxisAngle4d axisAngle4d) {
		return this.rotation(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z);
	}

	public Matrix4d rotation(Quaterniondc quaterniondc) {
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
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00(double1 + double2 - double4 - double3)._m01(double8 + double6)._m02(double10 - double12)._m10(-double6 + double8)._m11(double3 - double4 + double1 - double2)._m12(double14 + double16)._m20(double12 + double10)._m21(double14 - double16)._m22(double4 - double3 - double2 + double1)._properties(18);
		return this;
	}

	public Matrix4d rotation(Quaternionfc quaternionfc) {
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
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00(double1 + double2 - double4 - double3)._m01(double8 + double6)._m02(double10 - double12)._m10(-double6 + double8)._m11(double3 - double4 + double1 - double2)._m12(double14 + double16)._m20(double12 + double10)._m21(double14 - double16)._m22(double4 - double3 - double2 + double1)._properties(18);
		return this;
	}

	public Matrix4d translationRotateScale(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10) {
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
		boolean boolean1 = Math.absEqualsOne(double8) && Math.absEqualsOne(double9) && Math.absEqualsOne(double10);
		this._m00(double8 - (double15 + double16) * double8)._m01((double17 + double22) * double8)._m02((double18 - double21) * double8)._m03(0.0)._m10((double17 - double22) * double9)._m11(double9 - (double16 + double14) * double9)._m12((double20 + double19) * double9)._m13(0.0)._m20((double18 + double21) * double10)._m21((double20 - double19) * double10)._m22(double10 - (double15 + double14) * double10)._m23(0.0)._m30(double1)._m31(double2)._m32(double3)._m33(1.0).properties = 2 | (boolean1 ? 16 : 0);
		return this;
	}

	public Matrix4d translationRotateScale(Vector3fc vector3fc, Quaternionfc quaternionfc, Vector3fc vector3fc2) {
		return this.translationRotateScale((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), (double)quaternionfc.x(), (double)quaternionfc.y(), (double)quaternionfc.z(), (double)quaternionfc.w(), (double)vector3fc2.x(), (double)vector3fc2.y(), (double)vector3fc2.z());
	}

	public Matrix4d translationRotateScale(Vector3dc vector3dc, Quaterniondc quaterniondc, Vector3dc vector3dc2) {
		return this.translationRotateScale(vector3dc.x(), vector3dc.y(), vector3dc.z(), quaterniondc.x(), quaterniondc.y(), quaterniondc.z(), quaterniondc.w(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix4d translationRotateScale(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		return this.translationRotateScale(double1, double2, double3, double4, double5, double6, double7, double8, double8, double8);
	}

	public Matrix4d translationRotateScale(Vector3dc vector3dc, Quaterniondc quaterniondc, double double1) {
		return this.translationRotateScale(vector3dc.x(), vector3dc.y(), vector3dc.z(), quaterniondc.x(), quaterniondc.y(), quaterniondc.z(), quaterniondc.w(), double1, double1, double1);
	}

	public Matrix4d translationRotateScale(Vector3fc vector3fc, Quaternionfc quaternionfc, double double1) {
		return this.translationRotateScale((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), (double)quaternionfc.x(), (double)quaternionfc.y(), (double)quaternionfc.z(), (double)quaternionfc.w(), double1, double1, double1);
	}

	public Matrix4d translationRotateScaleInvert(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10) {
		boolean boolean1 = Math.absEqualsOne(double8) && Math.absEqualsOne(double9) && Math.absEqualsOne(double10);
		if (boolean1) {
			return this.translationRotateScale(double1, double2, double3, double4, double5, double6, double7, double8, double9, double10).invertOrthonormal(this);
		} else {
			double double11 = -double4;
			double double12 = -double5;
			double double13 = -double6;
			double double14 = double11 + double11;
			double double15 = double12 + double12;
			double double16 = double13 + double13;
			double double17 = double14 * double11;
			double double18 = double15 * double12;
			double double19 = double16 * double13;
			double double20 = double14 * double12;
			double double21 = double14 * double13;
			double double22 = double14 * double7;
			double double23 = double15 * double13;
			double double24 = double15 * double7;
			double double25 = double16 * double7;
			double double26 = 1.0 / double8;
			double double27 = 1.0 / double9;
			double double28 = 1.0 / double10;
			this._m00(double26 * (1.0 - double18 - double19))._m01(double27 * (double20 + double25))._m02(double28 * (double21 - double24))._m03(0.0)._m10(double26 * (double20 - double25))._m11(double27 * (1.0 - double19 - double17))._m12(double28 * (double23 + double22))._m13(0.0)._m20(double26 * (double21 + double24))._m21(double27 * (double23 - double22))._m22(double28 * (1.0 - double18 - double17))._m23(0.0)._m30(-this.m00 * double1 - this.m10 * double2 - this.m20 * double3)._m31(-this.m01 * double1 - this.m11 * double2 - this.m21 * double3)._m32(-this.m02 * double1 - this.m12 * double2 - this.m22 * double3)._m33(1.0).properties = 2;
			return this;
		}
	}

	public Matrix4d translationRotateScaleInvert(Vector3dc vector3dc, Quaterniondc quaterniondc, Vector3dc vector3dc2) {
		return this.translationRotateScaleInvert(vector3dc.x(), vector3dc.y(), vector3dc.z(), quaterniondc.x(), quaterniondc.y(), quaterniondc.z(), quaterniondc.w(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix4d translationRotateScaleInvert(Vector3fc vector3fc, Quaternionfc quaternionfc, Vector3fc vector3fc2) {
		return this.translationRotateScaleInvert((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), (double)quaternionfc.x(), (double)quaternionfc.y(), (double)quaternionfc.z(), (double)quaternionfc.w(), (double)vector3fc2.x(), (double)vector3fc2.y(), (double)vector3fc2.z());
	}

	public Matrix4d translationRotateScaleInvert(Vector3dc vector3dc, Quaterniondc quaterniondc, double double1) {
		return this.translationRotateScaleInvert(vector3dc.x(), vector3dc.y(), vector3dc.z(), quaterniondc.x(), quaterniondc.y(), quaterniondc.z(), quaterniondc.w(), double1, double1, double1);
	}

	public Matrix4d translationRotateScaleInvert(Vector3fc vector3fc, Quaternionfc quaternionfc, double double1) {
		return this.translationRotateScaleInvert((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), (double)quaternionfc.x(), (double)quaternionfc.y(), (double)quaternionfc.z(), (double)quaternionfc.w(), double1, double1, double1);
	}

	public Matrix4d translationRotateScaleMulAffine(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, Matrix4d matrix4d) {
		double double11 = double7 * double7;
		double double12 = double4 * double4;
		double double13 = double5 * double5;
		double double14 = double6 * double6;
		double double15 = double6 * double7;
		double double16 = double4 * double5;
		double double17 = double4 * double6;
		double double18 = double5 * double7;
		double double19 = double5 * double6;
		double double20 = double4 * double7;
		double double21 = double11 + double12 - double14 - double13;
		double double22 = double16 + double15 + double15 + double16;
		double double23 = double17 - double18 + double17 - double18;
		double double24 = -double15 + double16 - double15 + double16;
		double double25 = double13 - double14 + double11 - double12;
		double double26 = double19 + double19 + double20 + double20;
		double double27 = double18 + double17 + double17 + double18;
		double double28 = double19 + double19 - double20 - double20;
		double double29 = double14 - double13 - double12 + double11;
		double double30 = double21 * matrix4d.m00 + double24 * matrix4d.m01 + double27 * matrix4d.m02;
		double double31 = double22 * matrix4d.m00 + double25 * matrix4d.m01 + double28 * matrix4d.m02;
		this.m02 = double23 * matrix4d.m00 + double26 * matrix4d.m01 + double29 * matrix4d.m02;
		this.m00 = double30;
		this.m01 = double31;
		this.m03 = 0.0;
		double double32 = double21 * matrix4d.m10 + double24 * matrix4d.m11 + double27 * matrix4d.m12;
		double double33 = double22 * matrix4d.m10 + double25 * matrix4d.m11 + double28 * matrix4d.m12;
		this.m12 = double23 * matrix4d.m10 + double26 * matrix4d.m11 + double29 * matrix4d.m12;
		this.m10 = double32;
		this.m11 = double33;
		this.m13 = 0.0;
		double double34 = double21 * matrix4d.m20 + double24 * matrix4d.m21 + double27 * matrix4d.m22;
		double double35 = double22 * matrix4d.m20 + double25 * matrix4d.m21 + double28 * matrix4d.m22;
		this.m22 = double23 * matrix4d.m20 + double26 * matrix4d.m21 + double29 * matrix4d.m22;
		this.m20 = double34;
		this.m21 = double35;
		this.m23 = 0.0;
		double double36 = double21 * matrix4d.m30 + double24 * matrix4d.m31 + double27 * matrix4d.m32 + double1;
		double double37 = double22 * matrix4d.m30 + double25 * matrix4d.m31 + double28 * matrix4d.m32 + double2;
		this.m32 = double23 * matrix4d.m30 + double26 * matrix4d.m31 + double29 * matrix4d.m32 + double3;
		this.m30 = double36;
		this.m31 = double37;
		this.m33 = 1.0;
		boolean boolean1 = Math.absEqualsOne(double8) && Math.absEqualsOne(double9) && Math.absEqualsOne(double10);
		this.properties = 2 | (boolean1 && (matrix4d.properties & 16) != 0 ? 16 : 0);
		return this;
	}

	public Matrix4d translationRotateScaleMulAffine(Vector3fc vector3fc, Quaterniondc quaterniondc, Vector3fc vector3fc2, Matrix4d matrix4d) {
		return this.translationRotateScaleMulAffine((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), quaterniondc.x(), quaterniondc.y(), quaterniondc.z(), quaterniondc.w(), (double)vector3fc2.x(), (double)vector3fc2.y(), (double)vector3fc2.z(), matrix4d);
	}

	public Matrix4d translationRotate(double double1, double double2, double double3, double double4, double double5, double double6, double double7) {
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
		this.m00 = double8 + double9 - double11 - double10;
		this.m01 = double13 + double12 + double12 + double13;
		this.m02 = double14 - double15 + double14 - double15;
		this.m10 = -double12 + double13 - double12 + double13;
		this.m11 = double10 - double11 + double8 - double9;
		this.m12 = double16 + double16 + double17 + double17;
		this.m20 = double15 + double14 + double14 + double15;
		this.m21 = double16 + double16 - double17 - double17;
		this.m22 = double11 - double10 - double9 + double8;
		this.m30 = double1;
		this.m31 = double2;
		this.m32 = double3;
		this.m33 = 1.0;
		this.properties = 18;
		return this;
	}

	public Matrix4d translationRotate(double double1, double double2, double double3, Quaterniondc quaterniondc) {
		return this.translationRotate(double1, double2, double3, quaterniondc.x(), quaterniondc.y(), quaterniondc.z(), quaterniondc.w());
	}

	public Matrix4d rotate(Quaterniondc quaterniondc, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.rotation(quaterniondc);
		} else if ((this.properties & 8) != 0) {
			return this.rotateTranslation(quaterniondc, matrix4d);
		} else {
			return (this.properties & 2) != 0 ? this.rotateAffine(quaterniondc, matrix4d) : this.rotateGeneric(quaterniondc, matrix4d);
		}
	}

	private Matrix4d rotateGeneric(Quaterniondc quaterniondc, Matrix4d matrix4d) {
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
		double double20 = -double6 + double8;
		double double21 = double3 - double4 + double1 - double2;
		double double22 = double14 + double16;
		double double23 = double12 + double10;
		double double24 = double14 - double16;
		double double25 = double4 - double3 - double2 + double1;
		double double26 = this.m00 * double17 + this.m10 * double18 + this.m20 * double19;
		double double27 = this.m01 * double17 + this.m11 * double18 + this.m21 * double19;
		double double28 = this.m02 * double17 + this.m12 * double18 + this.m22 * double19;
		double double29 = this.m03 * double17 + this.m13 * double18 + this.m23 * double19;
		double double30 = this.m00 * double20 + this.m10 * double21 + this.m20 * double22;
		double double31 = this.m01 * double20 + this.m11 * double21 + this.m21 * double22;
		double double32 = this.m02 * double20 + this.m12 * double21 + this.m22 * double22;
		double double33 = this.m03 * double20 + this.m13 * double21 + this.m23 * double22;
		matrix4d._m20(this.m00 * double23 + this.m10 * double24 + this.m20 * double25)._m21(this.m01 * double23 + this.m11 * double24 + this.m21 * double25)._m22(this.m02 * double23 + this.m12 * double24 + this.m22 * double25)._m23(this.m03 * double23 + this.m13 * double24 + this.m23 * double25)._m00(double26)._m01(double27)._m02(double28)._m03(double29)._m10(double30)._m11(double31)._m12(double32)._m13(double33)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotate(Quaternionfc quaternionfc, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.rotation(quaternionfc);
		} else if ((this.properties & 8) != 0) {
			return this.rotateTranslation(quaternionfc, matrix4d);
		} else {
			return (this.properties & 2) != 0 ? this.rotateAffine(quaternionfc, matrix4d) : this.rotateGeneric(quaternionfc, matrix4d);
		}
	}

	private Matrix4d rotateGeneric(Quaternionfc quaternionfc, Matrix4d matrix4d) {
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
		double double23 = this.m03 * double11 + this.m13 * double12 + this.m23 * double13;
		double double24 = this.m00 * double14 + this.m10 * double15 + this.m20 * double16;
		double double25 = this.m01 * double14 + this.m11 * double15 + this.m21 * double16;
		double double26 = this.m02 * double14 + this.m12 * double15 + this.m22 * double16;
		double double27 = this.m03 * double14 + this.m13 * double15 + this.m23 * double16;
		matrix4d._m20(this.m00 * double17 + this.m10 * double18 + this.m20 * double19)._m21(this.m01 * double17 + this.m11 * double18 + this.m21 * double19)._m22(this.m02 * double17 + this.m12 * double18 + this.m22 * double19)._m23(this.m03 * double17 + this.m13 * double18 + this.m23 * double19)._m00(double20)._m01(double21)._m02(double22)._m03(double23)._m10(double24)._m11(double25)._m12(double26)._m13(double27)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotate(Quaterniondc quaterniondc) {
		return this.rotate(quaterniondc, this);
	}

	public Matrix4d rotate(Quaternionfc quaternionfc) {
		return this.rotate(quaternionfc, this);
	}

	public Matrix4d rotateAffine(Quaterniondc quaterniondc, Matrix4d matrix4d) {
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
		double double20 = -double6 + double8;
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
		matrix4d._m20(this.m00 * double23 + this.m10 * double24 + this.m20 * double25)._m21(this.m01 * double23 + this.m11 * double24 + this.m21 * double25)._m22(this.m02 * double23 + this.m12 * double24 + this.m22 * double25)._m23(0.0)._m00(double26)._m01(double27)._m02(double28)._m03(0.0)._m10(double29)._m11(double30)._m12(double31)._m13(0.0)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateAffine(Quaterniondc quaterniondc) {
		return this.rotateAffine(quaterniondc, this);
	}

	public Matrix4d rotateTranslation(Quaterniondc quaterniondc, Matrix4d matrix4d) {
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
		double double20 = -double6 + double8;
		double double21 = double3 - double4 + double1 - double2;
		double double22 = double14 + double16;
		double double23 = double12 + double10;
		double double24 = double14 - double16;
		double double25 = double4 - double3 - double2 + double1;
		matrix4d._m20(double23)._m21(double24)._m22(double25)._m23(0.0)._m00(double17)._m01(double18)._m02(double19)._m03(0.0)._m10(double20)._m11(double21)._m12(double22)._m13(0.0)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(1.0)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateTranslation(Quaternionfc quaternionfc, Matrix4d matrix4d) {
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
		matrix4d._m20(double17)._m21(double18)._m22(double19)._m23(0.0)._m00(double11)._m01(double12)._m02(double13)._m03(0.0)._m10(double14)._m11(double15)._m12(double16)._m13(0.0)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(1.0)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateLocal(Quaterniondc quaterniondc, Matrix4d matrix4d) {
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
		double double20 = -double6 + double8;
		double double21 = double3 - double4 + double1 - double2;
		double double22 = double14 + double16;
		double double23 = double12 + double10;
		double double24 = double14 - double16;
		double double25 = double4 - double3 - double2 + double1;
		double double26 = double17 * this.m00 + double20 * this.m01 + double23 * this.m02;
		double double27 = double18 * this.m00 + double21 * this.m01 + double24 * this.m02;
		double double28 = double19 * this.m00 + double22 * this.m01 + double25 * this.m02;
		double double29 = this.m03;
		double double30 = double17 * this.m10 + double20 * this.m11 + double23 * this.m12;
		double double31 = double18 * this.m10 + double21 * this.m11 + double24 * this.m12;
		double double32 = double19 * this.m10 + double22 * this.m11 + double25 * this.m12;
		double double33 = this.m13;
		double double34 = double17 * this.m20 + double20 * this.m21 + double23 * this.m22;
		double double35 = double18 * this.m20 + double21 * this.m21 + double24 * this.m22;
		double double36 = double19 * this.m20 + double22 * this.m21 + double25 * this.m22;
		double double37 = this.m23;
		double double38 = double17 * this.m30 + double20 * this.m31 + double23 * this.m32;
		double double39 = double18 * this.m30 + double21 * this.m31 + double24 * this.m32;
		double double40 = double19 * this.m30 + double22 * this.m31 + double25 * this.m32;
		matrix4d._m00(double26)._m01(double27)._m02(double28)._m03(double29)._m10(double30)._m11(double31)._m12(double32)._m13(double33)._m20(double34)._m21(double35)._m22(double36)._m23(double37)._m30(double38)._m31(double39)._m32(double40)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateLocal(Quaterniondc quaterniondc) {
		return this.rotateLocal(quaterniondc, this);
	}

	public Matrix4d rotateAffine(Quaternionfc quaternionfc, Matrix4d matrix4d) {
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
		matrix4d._m20(this.m00 * double17 + this.m10 * double18 + this.m20 * double19)._m21(this.m01 * double17 + this.m11 * double18 + this.m21 * double19)._m22(this.m02 * double17 + this.m12 * double18 + this.m22 * double19)._m23(0.0)._m00(double20)._m01(double21)._m02(double22)._m03(0.0)._m10(double23)._m11(double24)._m12(double25)._m13(0.0)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateAffine(Quaternionfc quaternionfc) {
		return this.rotateAffine(quaternionfc, this);
	}

	public Matrix4d rotateLocal(Quaternionfc quaternionfc, Matrix4d matrix4d) {
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
		double double23 = this.m03;
		double double24 = double11 * this.m10 + double14 * this.m11 + double17 * this.m12;
		double double25 = double12 * this.m10 + double15 * this.m11 + double18 * this.m12;
		double double26 = double13 * this.m10 + double16 * this.m11 + double19 * this.m12;
		double double27 = this.m13;
		double double28 = double11 * this.m20 + double14 * this.m21 + double17 * this.m22;
		double double29 = double12 * this.m20 + double15 * this.m21 + double18 * this.m22;
		double double30 = double13 * this.m20 + double16 * this.m21 + double19 * this.m22;
		double double31 = this.m23;
		double double32 = double11 * this.m30 + double14 * this.m31 + double17 * this.m32;
		double double33 = double12 * this.m30 + double15 * this.m31 + double18 * this.m32;
		double double34 = double13 * this.m30 + double16 * this.m31 + double19 * this.m32;
		matrix4d._m00(double20)._m01(double21)._m02(double22)._m03(double23)._m10(double24)._m11(double25)._m12(double26)._m13(double27)._m20(double28)._m21(double29)._m22(double30)._m23(double31)._m30(double32)._m31(double33)._m32(double34)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotateLocal(Quaternionfc quaternionfc) {
		return this.rotateLocal(quaternionfc, this);
	}

	public Matrix4d rotate(AxisAngle4f axisAngle4f) {
		return this.rotate((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z);
	}

	public Matrix4d rotate(AxisAngle4f axisAngle4f, Matrix4d matrix4d) {
		return this.rotate((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z, matrix4d);
	}

	public Matrix4d rotate(AxisAngle4d axisAngle4d) {
		return this.rotate(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z);
	}

	public Matrix4d rotate(AxisAngle4d axisAngle4d, Matrix4d matrix4d) {
		return this.rotate(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z, matrix4d);
	}

	public Matrix4d rotate(double double1, Vector3dc vector3dc) {
		return this.rotate(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4d rotate(double double1, Vector3dc vector3dc, Matrix4d matrix4d) {
		return this.rotate(double1, vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix4d);
	}

	public Matrix4d rotate(double double1, Vector3fc vector3fc) {
		return this.rotate(double1, (double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z());
	}

	public Matrix4d rotate(double double1, Vector3fc vector3fc, Matrix4d matrix4d) {
		return this.rotate(double1, (double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), matrix4d);
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
		
		case 3: 
			vector4d.x = this.m03;
			vector4d.y = this.m13;
			vector4d.z = this.m23;
			vector4d.w = this.m33;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector4d;
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
		
		case 3: 
			vector3d.x = this.m03;
			vector3d.y = this.m13;
			vector3d.z = this.m23;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector3d;
	}

	public Matrix4d setRow(int int1, Vector4dc vector4dc) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			return this._m00(vector4dc.x())._m10(vector4dc.y())._m20(vector4dc.z())._m30(vector4dc.w())._properties(0);
		
		case 1: 
			return this._m01(vector4dc.x())._m11(vector4dc.y())._m21(vector4dc.z())._m31(vector4dc.w())._properties(0);
		
		case 2: 
			return this._m02(vector4dc.x())._m12(vector4dc.y())._m22(vector4dc.z())._m32(vector4dc.w())._properties(0);
		
		case 3: 
			return this._m03(vector4dc.x())._m13(vector4dc.y())._m23(vector4dc.z())._m33(vector4dc.w())._properties(0);
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
	}

	public Vector4d getColumn(int int1, Vector4d vector4d) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			vector4d.x = this.m00;
			vector4d.y = this.m01;
			vector4d.z = this.m02;
			vector4d.w = this.m03;
			break;
		
		case 1: 
			vector4d.x = this.m10;
			vector4d.y = this.m11;
			vector4d.z = this.m12;
			vector4d.w = this.m13;
			break;
		
		case 2: 
			vector4d.x = this.m20;
			vector4d.y = this.m21;
			vector4d.z = this.m22;
			vector4d.w = this.m23;
			break;
		
		case 3: 
			vector4d.x = this.m30;
			vector4d.y = this.m31;
			vector4d.z = this.m32;
			vector4d.w = this.m33;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector4d;
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

	public Matrix4d setColumn(int int1, Vector4dc vector4dc) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			return this._m00(vector4dc.x())._m01(vector4dc.y())._m02(vector4dc.z())._m03(vector4dc.w())._properties(0);
		
		case 1: 
			return this._m10(vector4dc.x())._m11(vector4dc.y())._m12(vector4dc.z())._m13(vector4dc.w())._properties(0);
		
		case 2: 
			return this._m20(vector4dc.x())._m21(vector4dc.y())._m22(vector4dc.z())._m23(vector4dc.w())._properties(0);
		
		case 3: 
			return this._m30(vector4dc.x())._m31(vector4dc.y())._m32(vector4dc.z())._m33(vector4dc.w())._properties(0);
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
	}

	public double get(int int1, int int2) {
		return MemUtil.INSTANCE.get(this, int1, int2);
	}

	public Matrix4d set(int int1, int int2, double double1) {
		return MemUtil.INSTANCE.set(this, int1, int2, double1);
	}

	public double getRowColumn(int int1, int int2) {
		return MemUtil.INSTANCE.get(this, int2, int1);
	}

	public Matrix4d setRowColumn(int int1, int int2, double double1) {
		return MemUtil.INSTANCE.set(this, int2, int1, double1);
	}

	public Matrix4d normal() {
		return this.normal(this);
	}

	public Matrix4d normal(Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.identity();
		} else {
			return (this.properties & 16) != 0 ? this.normalOrthonormal(matrix4d) : this.normalGeneric(matrix4d);
		}
	}

	private Matrix4d normalOrthonormal(Matrix4d matrix4d) {
		if (matrix4d != this) {
			matrix4d.set((Matrix4dc)this);
		}

		return matrix4d._properties(18);
	}

	private Matrix4d normalGeneric(Matrix4d matrix4d) {
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
		return matrix4d._m00(double9)._m01(double10)._m02(double11)._m03(0.0)._m10(double12)._m11(double13)._m12(double14)._m13(0.0)._m20(double15)._m21(double16)._m22(double17)._m23(0.0)._m30(0.0)._m31(0.0)._m32(0.0)._m33(1.0)._properties((this.properties | 2) & -10);
	}

	public Matrix3d normal(Matrix3d matrix3d) {
		return (this.properties & 16) != 0 ? this.normalOrthonormal(matrix3d) : this.normalGeneric(matrix3d);
	}

	private Matrix3d normalOrthonormal(Matrix3d matrix3d) {
		matrix3d.set((Matrix4dc)this);
		return matrix3d;
	}

	private Matrix3d normalGeneric(Matrix3d matrix3d) {
		double double1 = this.m00 * this.m11;
		double double2 = this.m01 * this.m10;
		double double3 = this.m02 * this.m10;
		double double4 = this.m00 * this.m12;
		double double5 = this.m01 * this.m12;
		double double6 = this.m02 * this.m11;
		double double7 = (double1 - double2) * this.m22 + (double3 - double4) * this.m21 + (double5 - double6) * this.m20;
		double double8 = 1.0 / double7;
		return matrix3d._m00((this.m11 * this.m22 - this.m21 * this.m12) * double8)._m01((this.m20 * this.m12 - this.m10 * this.m22) * double8)._m02((this.m10 * this.m21 - this.m20 * this.m11) * double8)._m10((this.m21 * this.m02 - this.m01 * this.m22) * double8)._m11((this.m00 * this.m22 - this.m20 * this.m02) * double8)._m12((this.m20 * this.m01 - this.m00 * this.m21) * double8)._m20((double5 - double6) * double8)._m21((double3 - double4) * double8)._m22((double1 - double2) * double8);
	}

	public Matrix4d cofactor3x3() {
		return this.cofactor3x3(this);
	}

	public Matrix3d cofactor3x3(Matrix3d matrix3d) {
		return matrix3d._m00(this.m11 * this.m22 - this.m21 * this.m12)._m01(this.m20 * this.m12 - this.m10 * this.m22)._m02(this.m10 * this.m21 - this.m20 * this.m11)._m10(this.m21 * this.m02 - this.m01 * this.m22)._m11(this.m00 * this.m22 - this.m20 * this.m02)._m12(this.m20 * this.m01 - this.m00 * this.m21)._m20(this.m01 * this.m12 - this.m02 * this.m11)._m21(this.m02 * this.m10 - this.m00 * this.m12)._m22(this.m00 * this.m11 - this.m01 * this.m10);
	}

	public Matrix4d cofactor3x3(Matrix4d matrix4d) {
		double double1 = this.m21 * this.m02 - this.m01 * this.m22;
		double double2 = this.m00 * this.m22 - this.m20 * this.m02;
		double double3 = this.m20 * this.m01 - this.m00 * this.m21;
		double double4 = this.m01 * this.m12 - this.m11 * this.m02;
		double double5 = this.m02 * this.m10 - this.m12 * this.m00;
		double double6 = this.m00 * this.m11 - this.m10 * this.m01;
		return matrix4d._m00(this.m11 * this.m22 - this.m21 * this.m12)._m01(this.m20 * this.m12 - this.m10 * this.m22)._m02(this.m10 * this.m21 - this.m20 * this.m11)._m03(0.0)._m10(double1)._m11(double2)._m12(double3)._m13(0.0)._m20(double4)._m21(double5)._m22(double6)._m23(0.0)._m30(0.0)._m31(0.0)._m32(0.0)._m33(1.0)._properties((this.properties | 2) & -10);
	}

	public Matrix4d normalize3x3() {
		return this.normalize3x3(this);
	}

	public Matrix4d normalize3x3(Matrix4d matrix4d) {
		double double1 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
		double double2 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
		double double3 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
		matrix4d._m00(this.m00 * double1)._m01(this.m01 * double1)._m02(this.m02 * double1)._m10(this.m10 * double2)._m11(this.m11 * double2)._m12(this.m12 * double2)._m20(this.m20 * double3)._m21(this.m21 * double3)._m22(this.m22 * double3)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties);
		return matrix4d;
	}

	public Matrix3d normalize3x3(Matrix3d matrix3d) {
		double double1 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
		double double2 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
		double double3 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
		matrix3d.m00(this.m00 * double1);
		matrix3d.m01(this.m01 * double1);
		matrix3d.m02(this.m02 * double1);
		matrix3d.m10(this.m10 * double2);
		matrix3d.m11(this.m11 * double2);
		matrix3d.m12(this.m12 * double2);
		matrix3d.m20(this.m20 * double3);
		matrix3d.m21(this.m21 * double3);
		matrix3d.m22(this.m22 * double3);
		return matrix3d;
	}

	public Vector4d unproject(double double1, double double2, double double3, int[] intArray, Vector4d vector4d) {
		double double4 = this.m00 * this.m11 - this.m01 * this.m10;
		double double5 = this.m00 * this.m12 - this.m02 * this.m10;
		double double6 = this.m00 * this.m13 - this.m03 * this.m10;
		double double7 = this.m01 * this.m12 - this.m02 * this.m11;
		double double8 = this.m01 * this.m13 - this.m03 * this.m11;
		double double9 = this.m02 * this.m13 - this.m03 * this.m12;
		double double10 = this.m20 * this.m31 - this.m21 * this.m30;
		double double11 = this.m20 * this.m32 - this.m22 * this.m30;
		double double12 = this.m20 * this.m33 - this.m23 * this.m30;
		double double13 = this.m21 * this.m32 - this.m22 * this.m31;
		double double14 = this.m21 * this.m33 - this.m23 * this.m31;
		double double15 = this.m22 * this.m33 - this.m23 * this.m32;
		double double16 = double4 * double15 - double5 * double14 + double6 * double13 + double7 * double12 - double8 * double11 + double9 * double10;
		double16 = 1.0 / double16;
		double double17 = (this.m11 * double15 - this.m12 * double14 + this.m13 * double13) * double16;
		double double18 = (-this.m01 * double15 + this.m02 * double14 - this.m03 * double13) * double16;
		double double19 = (this.m31 * double9 - this.m32 * double8 + this.m33 * double7) * double16;
		double double20 = (-this.m21 * double9 + this.m22 * double8 - this.m23 * double7) * double16;
		double double21 = (-this.m10 * double15 + this.m12 * double12 - this.m13 * double11) * double16;
		double double22 = (this.m00 * double15 - this.m02 * double12 + this.m03 * double11) * double16;
		double double23 = (-this.m30 * double9 + this.m32 * double6 - this.m33 * double5) * double16;
		double double24 = (this.m20 * double9 - this.m22 * double6 + this.m23 * double5) * double16;
		double double25 = (this.m10 * double14 - this.m11 * double12 + this.m13 * double10) * double16;
		double double26 = (-this.m00 * double14 + this.m01 * double12 - this.m03 * double10) * double16;
		double double27 = (this.m30 * double8 - this.m31 * double6 + this.m33 * double4) * double16;
		double double28 = (-this.m20 * double8 + this.m21 * double6 - this.m23 * double4) * double16;
		double double29 = (-this.m10 * double13 + this.m11 * double11 - this.m12 * double10) * double16;
		double double30 = (this.m00 * double13 - this.m01 * double11 + this.m02 * double10) * double16;
		double double31 = (-this.m30 * double7 + this.m31 * double5 - this.m32 * double4) * double16;
		double double32 = (this.m20 * double7 - this.m21 * double5 + this.m22 * double4) * double16;
		double double33 = (double1 - (double)intArray[0]) / (double)intArray[2] * 2.0 - 1.0;
		double double34 = (double2 - (double)intArray[1]) / (double)intArray[3] * 2.0 - 1.0;
		double double35 = double3 + double3 - 1.0;
		double double36 = 1.0 / (double20 * double33 + double24 * double34 + double28 * double35 + double32);
		vector4d.x = (double17 * double33 + double21 * double34 + double25 * double35 + double29) * double36;
		vector4d.y = (double18 * double33 + double22 * double34 + double26 * double35 + double30) * double36;
		vector4d.z = (double19 * double33 + double23 * double34 + double27 * double35 + double31) * double36;
		vector4d.w = 1.0;
		return vector4d;
	}

	public Vector3d unproject(double double1, double double2, double double3, int[] intArray, Vector3d vector3d) {
		double double4 = this.m00 * this.m11 - this.m01 * this.m10;
		double double5 = this.m00 * this.m12 - this.m02 * this.m10;
		double double6 = this.m00 * this.m13 - this.m03 * this.m10;
		double double7 = this.m01 * this.m12 - this.m02 * this.m11;
		double double8 = this.m01 * this.m13 - this.m03 * this.m11;
		double double9 = this.m02 * this.m13 - this.m03 * this.m12;
		double double10 = this.m20 * this.m31 - this.m21 * this.m30;
		double double11 = this.m20 * this.m32 - this.m22 * this.m30;
		double double12 = this.m20 * this.m33 - this.m23 * this.m30;
		double double13 = this.m21 * this.m32 - this.m22 * this.m31;
		double double14 = this.m21 * this.m33 - this.m23 * this.m31;
		double double15 = this.m22 * this.m33 - this.m23 * this.m32;
		double double16 = double4 * double15 - double5 * double14 + double6 * double13 + double7 * double12 - double8 * double11 + double9 * double10;
		double16 = 1.0 / double16;
		double double17 = (this.m11 * double15 - this.m12 * double14 + this.m13 * double13) * double16;
		double double18 = (-this.m01 * double15 + this.m02 * double14 - this.m03 * double13) * double16;
		double double19 = (this.m31 * double9 - this.m32 * double8 + this.m33 * double7) * double16;
		double double20 = (-this.m21 * double9 + this.m22 * double8 - this.m23 * double7) * double16;
		double double21 = (-this.m10 * double15 + this.m12 * double12 - this.m13 * double11) * double16;
		double double22 = (this.m00 * double15 - this.m02 * double12 + this.m03 * double11) * double16;
		double double23 = (-this.m30 * double9 + this.m32 * double6 - this.m33 * double5) * double16;
		double double24 = (this.m20 * double9 - this.m22 * double6 + this.m23 * double5) * double16;
		double double25 = (this.m10 * double14 - this.m11 * double12 + this.m13 * double10) * double16;
		double double26 = (-this.m00 * double14 + this.m01 * double12 - this.m03 * double10) * double16;
		double double27 = (this.m30 * double8 - this.m31 * double6 + this.m33 * double4) * double16;
		double double28 = (-this.m20 * double8 + this.m21 * double6 - this.m23 * double4) * double16;
		double double29 = (-this.m10 * double13 + this.m11 * double11 - this.m12 * double10) * double16;
		double double30 = (this.m00 * double13 - this.m01 * double11 + this.m02 * double10) * double16;
		double double31 = (-this.m30 * double7 + this.m31 * double5 - this.m32 * double4) * double16;
		double double32 = (this.m20 * double7 - this.m21 * double5 + this.m22 * double4) * double16;
		double double33 = (double1 - (double)intArray[0]) / (double)intArray[2] * 2.0 - 1.0;
		double double34 = (double2 - (double)intArray[1]) / (double)intArray[3] * 2.0 - 1.0;
		double double35 = double3 + double3 - 1.0;
		double double36 = 1.0 / (double20 * double33 + double24 * double34 + double28 * double35 + double32);
		vector3d.x = (double17 * double33 + double21 * double34 + double25 * double35 + double29) * double36;
		vector3d.y = (double18 * double33 + double22 * double34 + double26 * double35 + double30) * double36;
		vector3d.z = (double19 * double33 + double23 * double34 + double27 * double35 + double31) * double36;
		return vector3d;
	}

	public Vector4d unproject(Vector3dc vector3dc, int[] intArray, Vector4d vector4d) {
		return this.unproject(vector3dc.x(), vector3dc.y(), vector3dc.z(), intArray, vector4d);
	}

	public Vector3d unproject(Vector3dc vector3dc, int[] intArray, Vector3d vector3d) {
		return this.unproject(vector3dc.x(), vector3dc.y(), vector3dc.z(), intArray, vector3d);
	}

	public Matrix4d unprojectRay(double double1, double double2, int[] intArray, Vector3d vector3d, Vector3d vector3d2) {
		double double3 = this.m00 * this.m11 - this.m01 * this.m10;
		double double4 = this.m00 * this.m12 - this.m02 * this.m10;
		double double5 = this.m00 * this.m13 - this.m03 * this.m10;
		double double6 = this.m01 * this.m12 - this.m02 * this.m11;
		double double7 = this.m01 * this.m13 - this.m03 * this.m11;
		double double8 = this.m02 * this.m13 - this.m03 * this.m12;
		double double9 = this.m20 * this.m31 - this.m21 * this.m30;
		double double10 = this.m20 * this.m32 - this.m22 * this.m30;
		double double11 = this.m20 * this.m33 - this.m23 * this.m30;
		double double12 = this.m21 * this.m32 - this.m22 * this.m31;
		double double13 = this.m21 * this.m33 - this.m23 * this.m31;
		double double14 = this.m22 * this.m33 - this.m23 * this.m32;
		double double15 = double3 * double14 - double4 * double13 + double5 * double12 + double6 * double11 - double7 * double10 + double8 * double9;
		double15 = 1.0 / double15;
		double double16 = (this.m11 * double14 - this.m12 * double13 + this.m13 * double12) * double15;
		double double17 = (-this.m01 * double14 + this.m02 * double13 - this.m03 * double12) * double15;
		double double18 = (this.m31 * double8 - this.m32 * double7 + this.m33 * double6) * double15;
		double double19 = (-this.m21 * double8 + this.m22 * double7 - this.m23 * double6) * double15;
		double double20 = (-this.m10 * double14 + this.m12 * double11 - this.m13 * double10) * double15;
		double double21 = (this.m00 * double14 - this.m02 * double11 + this.m03 * double10) * double15;
		double double22 = (-this.m30 * double8 + this.m32 * double5 - this.m33 * double4) * double15;
		double double23 = (this.m20 * double8 - this.m22 * double5 + this.m23 * double4) * double15;
		double double24 = (this.m10 * double13 - this.m11 * double11 + this.m13 * double9) * double15;
		double double25 = (-this.m00 * double13 + this.m01 * double11 - this.m03 * double9) * double15;
		double double26 = (this.m30 * double7 - this.m31 * double5 + this.m33 * double3) * double15;
		double double27 = (-this.m20 * double7 + this.m21 * double5 - this.m23 * double3) * double15;
		double double28 = (-this.m10 * double12 + this.m11 * double10 - this.m12 * double9) * double15;
		double double29 = (this.m00 * double12 - this.m01 * double10 + this.m02 * double9) * double15;
		double double30 = (-this.m30 * double6 + this.m31 * double4 - this.m32 * double3) * double15;
		double double31 = (this.m20 * double6 - this.m21 * double4 + this.m22 * double3) * double15;
		double double32 = (double1 - (double)intArray[0]) / (double)intArray[2] * 2.0 - 1.0;
		double double33 = (double2 - (double)intArray[1]) / (double)intArray[3] * 2.0 - 1.0;
		double double34 = double16 * double32 + double20 * double33 + double28;
		double double35 = double17 * double32 + double21 * double33 + double29;
		double double36 = double18 * double32 + double22 * double33 + double30;
		double double37 = 1.0 / (double19 * double32 + double23 * double33 - double27 + double31);
		double double38 = (double34 - double24) * double37;
		double double39 = (double35 - double25) * double37;
		double double40 = (double36 - double26) * double37;
		double double41 = 1.0 / (double19 * double32 + double23 * double33 + double31);
		double double42 = double34 * double41;
		double double43 = double35 * double41;
		double double44 = double36 * double41;
		vector3d.x = double38;
		vector3d.y = double39;
		vector3d.z = double40;
		vector3d2.x = double42 - double38;
		vector3d2.y = double43 - double39;
		vector3d2.z = double44 - double40;
		return this;
	}

	public Matrix4d unprojectRay(Vector2dc vector2dc, int[] intArray, Vector3d vector3d, Vector3d vector3d2) {
		return this.unprojectRay(vector2dc.x(), vector2dc.y(), intArray, vector3d, vector3d2);
	}

	public Vector4d unprojectInv(Vector3dc vector3dc, int[] intArray, Vector4d vector4d) {
		return this.unprojectInv(vector3dc.x(), vector3dc.y(), vector3dc.z(), intArray, vector4d);
	}

	public Vector4d unprojectInv(double double1, double double2, double double3, int[] intArray, Vector4d vector4d) {
		double double4 = (double1 - (double)intArray[0]) / (double)intArray[2] * 2.0 - 1.0;
		double double5 = (double2 - (double)intArray[1]) / (double)intArray[3] * 2.0 - 1.0;
		double double6 = double3 + double3 - 1.0;
		double double7 = 1.0 / (this.m03 * double4 + this.m13 * double5 + this.m23 * double6 + this.m33);
		vector4d.x = (this.m00 * double4 + this.m10 * double5 + this.m20 * double6 + this.m30) * double7;
		vector4d.y = (this.m01 * double4 + this.m11 * double5 + this.m21 * double6 + this.m31) * double7;
		vector4d.z = (this.m02 * double4 + this.m12 * double5 + this.m22 * double6 + this.m32) * double7;
		vector4d.w = 1.0;
		return vector4d;
	}

	public Vector3d unprojectInv(Vector3dc vector3dc, int[] intArray, Vector3d vector3d) {
		return this.unprojectInv(vector3dc.x(), vector3dc.y(), vector3dc.z(), intArray, vector3d);
	}

	public Vector3d unprojectInv(double double1, double double2, double double3, int[] intArray, Vector3d vector3d) {
		double double4 = (double1 - (double)intArray[0]) / (double)intArray[2] * 2.0 - 1.0;
		double double5 = (double2 - (double)intArray[1]) / (double)intArray[3] * 2.0 - 1.0;
		double double6 = double3 + double3 - 1.0;
		double double7 = 1.0 / (this.m03 * double4 + this.m13 * double5 + this.m23 * double6 + this.m33);
		vector3d.x = (this.m00 * double4 + this.m10 * double5 + this.m20 * double6 + this.m30) * double7;
		vector3d.y = (this.m01 * double4 + this.m11 * double5 + this.m21 * double6 + this.m31) * double7;
		vector3d.z = (this.m02 * double4 + this.m12 * double5 + this.m22 * double6 + this.m32) * double7;
		return vector3d;
	}

	public Matrix4d unprojectInvRay(Vector2dc vector2dc, int[] intArray, Vector3d vector3d, Vector3d vector3d2) {
		return this.unprojectInvRay(vector2dc.x(), vector2dc.y(), intArray, vector3d, vector3d2);
	}

	public Matrix4d unprojectInvRay(double double1, double double2, int[] intArray, Vector3d vector3d, Vector3d vector3d2) {
		double double3 = (double1 - (double)intArray[0]) / (double)intArray[2] * 2.0 - 1.0;
		double double4 = (double2 - (double)intArray[1]) / (double)intArray[3] * 2.0 - 1.0;
		double double5 = this.m00 * double3 + this.m10 * double4 + this.m30;
		double double6 = this.m01 * double3 + this.m11 * double4 + this.m31;
		double double7 = this.m02 * double3 + this.m12 * double4 + this.m32;
		double double8 = 1.0 / (this.m03 * double3 + this.m13 * double4 - this.m23 + this.m33);
		double double9 = (double5 - this.m20) * double8;
		double double10 = (double6 - this.m21) * double8;
		double double11 = (double7 - this.m22) * double8;
		double double12 = 1.0 / (this.m03 * double3 + this.m13 * double4 + this.m33);
		double double13 = double5 * double12;
		double double14 = double6 * double12;
		double double15 = double7 * double12;
		vector3d.x = double9;
		vector3d.y = double10;
		vector3d.z = double11;
		vector3d2.x = double13 - double9;
		vector3d2.y = double14 - double10;
		vector3d2.z = double15 - double11;
		return this;
	}

	public Vector4d project(double double1, double double2, double double3, int[] intArray, Vector4d vector4d) {
		double double4 = 1.0 / (this.m03 * double1 + this.m13 * double2 + this.m23 * double3 + this.m33);
		double double5 = (this.m00 * double1 + this.m10 * double2 + this.m20 * double3 + this.m30) * double4;
		double double6 = (this.m01 * double1 + this.m11 * double2 + this.m21 * double3 + this.m31) * double4;
		double double7 = (this.m02 * double1 + this.m12 * double2 + this.m22 * double3 + this.m32) * double4;
		vector4d.x = (double5 * 0.5 + 0.5) * (double)intArray[2] + (double)intArray[0];
		vector4d.y = (double6 * 0.5 + 0.5) * (double)intArray[3] + (double)intArray[1];
		vector4d.z = (1.0 + double7) * 0.5;
		vector4d.w = 1.0;
		return vector4d;
	}

	public Vector3d project(double double1, double double2, double double3, int[] intArray, Vector3d vector3d) {
		double double4 = 1.0 / (this.m03 * double1 + this.m13 * double2 + this.m23 * double3 + this.m33);
		double double5 = (this.m00 * double1 + this.m10 * double2 + this.m20 * double3 + this.m30) * double4;
		double double6 = (this.m01 * double1 + this.m11 * double2 + this.m21 * double3 + this.m31) * double4;
		double double7 = (this.m02 * double1 + this.m12 * double2 + this.m22 * double3 + this.m32) * double4;
		vector3d.x = (double5 * 0.5 + 0.5) * (double)intArray[2] + (double)intArray[0];
		vector3d.y = (double6 * 0.5 + 0.5) * (double)intArray[3] + (double)intArray[1];
		vector3d.z = (1.0 + double7) * 0.5;
		return vector3d;
	}

	public Vector4d project(Vector3dc vector3dc, int[] intArray, Vector4d vector4d) {
		return this.project(vector3dc.x(), vector3dc.y(), vector3dc.z(), intArray, vector4d);
	}

	public Vector3d project(Vector3dc vector3dc, int[] intArray, Vector3d vector3d) {
		return this.project(vector3dc.x(), vector3dc.y(), vector3dc.z(), intArray, vector3d);
	}

	public Matrix4d reflect(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.reflection(double1, double2, double3, double4);
		} else if ((this.properties & 4) != 0) {
			return matrix4d.reflection(double1, double2, double3, double4);
		} else {
			return (this.properties & 2) != 0 ? this.reflectAffine(double1, double2, double3, double4, matrix4d) : this.reflectGeneric(double1, double2, double3, double4, matrix4d);
		}
	}

	private Matrix4d reflectAffine(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
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
		double double21 = this.m00 * double9 + this.m10 * double10 + this.m20 * double11;
		double double22 = this.m01 * double9 + this.m11 * double10 + this.m21 * double11;
		double double23 = this.m02 * double9 + this.m12 * double10 + this.m22 * double11;
		double double24 = this.m00 * double12 + this.m10 * double13 + this.m20 * double14;
		double double25 = this.m01 * double12 + this.m11 * double13 + this.m21 * double14;
		double double26 = this.m02 * double12 + this.m12 * double13 + this.m22 * double14;
		matrix4d._m30(this.m00 * double18 + this.m10 * double19 + this.m20 * double20 + this.m30)._m31(this.m01 * double18 + this.m11 * double19 + this.m21 * double20 + this.m31)._m32(this.m02 * double18 + this.m12 * double19 + this.m22 * double20 + this.m32)._m33(this.m33)._m20(this.m00 * double15 + this.m10 * double16 + this.m20 * double17)._m21(this.m01 * double15 + this.m11 * double16 + this.m21 * double17)._m22(this.m02 * double15 + this.m12 * double16 + this.m22 * double17)._m23(0.0)._m00(double21)._m01(double22)._m02(double23)._m03(0.0)._m10(double24)._m11(double25)._m12(double26)._m13(0.0)._properties(this.properties & -14);
		return matrix4d;
	}

	private Matrix4d reflectGeneric(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
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
		double double21 = this.m00 * double9 + this.m10 * double10 + this.m20 * double11;
		double double22 = this.m01 * double9 + this.m11 * double10 + this.m21 * double11;
		double double23 = this.m02 * double9 + this.m12 * double10 + this.m22 * double11;
		double double24 = this.m03 * double9 + this.m13 * double10 + this.m23 * double11;
		double double25 = this.m00 * double12 + this.m10 * double13 + this.m20 * double14;
		double double26 = this.m01 * double12 + this.m11 * double13 + this.m21 * double14;
		double double27 = this.m02 * double12 + this.m12 * double13 + this.m22 * double14;
		double double28 = this.m03 * double12 + this.m13 * double13 + this.m23 * double14;
		matrix4d._m30(this.m00 * double18 + this.m10 * double19 + this.m20 * double20 + this.m30)._m31(this.m01 * double18 + this.m11 * double19 + this.m21 * double20 + this.m31)._m32(this.m02 * double18 + this.m12 * double19 + this.m22 * double20 + this.m32)._m33(this.m03 * double18 + this.m13 * double19 + this.m23 * double20 + this.m33)._m20(this.m00 * double15 + this.m10 * double16 + this.m20 * double17)._m21(this.m01 * double15 + this.m11 * double16 + this.m21 * double17)._m22(this.m02 * double15 + this.m12 * double16 + this.m22 * double17)._m23(this.m03 * double15 + this.m13 * double16 + this.m23 * double17)._m00(double21)._m01(double22)._m02(double23)._m03(double24)._m10(double25)._m11(double26)._m12(double27)._m13(double28)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d reflect(double double1, double double2, double double3, double double4) {
		return this.reflect(double1, double2, double3, double4, this);
	}

	public Matrix4d reflect(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.reflect(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4d reflect(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
		double double7 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = double1 * double7;
		double double9 = double2 * double7;
		double double10 = double3 * double7;
		return this.reflect(double8, double9, double10, -double8 * double4 - double9 * double5 - double10 * double6, matrix4d);
	}

	public Matrix4d reflect(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.reflect(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix4d reflect(Quaterniondc quaterniondc, Vector3dc vector3dc) {
		return this.reflect(quaterniondc, vector3dc, this);
	}

	public Matrix4d reflect(Quaterniondc quaterniondc, Vector3dc vector3dc, Matrix4d matrix4d) {
		double double1 = quaterniondc.x() + quaterniondc.x();
		double double2 = quaterniondc.y() + quaterniondc.y();
		double double3 = quaterniondc.z() + quaterniondc.z();
		double double4 = quaterniondc.x() * double3 + quaterniondc.w() * double2;
		double double5 = quaterniondc.y() * double3 - quaterniondc.w() * double1;
		double double6 = 1.0 - (quaterniondc.x() * double1 + quaterniondc.y() * double2);
		return this.reflect(double4, double5, double6, vector3dc.x(), vector3dc.y(), vector3dc.z(), matrix4d);
	}

	public Matrix4d reflect(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4d matrix4d) {
		return this.reflect(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), matrix4d);
	}

	public Matrix4d reflection(double double1, double double2, double double3, double double4) {
		double double5 = double1 + double1;
		double double6 = double2 + double2;
		double double7 = double3 + double3;
		double double8 = double4 + double4;
		this._m00(1.0 - double5 * double1)._m01(-double5 * double2)._m02(-double5 * double3)._m03(0.0)._m10(-double6 * double1)._m11(1.0 - double6 * double2)._m12(-double6 * double3)._m13(0.0)._m20(-double7 * double1)._m21(-double7 * double2)._m22(1.0 - double7 * double3)._m23(0.0)._m30(-double8 * double1)._m31(-double8 * double2)._m32(-double8 * double3)._m33(1.0).properties = 18;
		return this;
	}

	public Matrix4d reflection(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = double1 * double7;
		double double9 = double2 * double7;
		double double10 = double3 * double7;
		return this.reflection(double8, double9, double10, -double8 * double4 - double9 * double5 - double10 * double6);
	}

	public Matrix4d reflection(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.reflection(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix4d reflection(Quaterniondc quaterniondc, Vector3dc vector3dc) {
		double double1 = quaterniondc.x() + quaterniondc.x();
		double double2 = quaterniondc.y() + quaterniondc.y();
		double double3 = quaterniondc.z() + quaterniondc.z();
		double double4 = quaterniondc.x() * double3 + quaterniondc.w() * double2;
		double double5 = quaterniondc.y() * double3 - quaterniondc.w() * double1;
		double double6 = 1.0 - (quaterniondc.x() * double1 + quaterniondc.y() * double2);
		return this.reflection(double4, double5, double6, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4d ortho(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setOrtho(double1, double2, double3, double4, double5, double6, boolean1) : this.orthoGeneric(double1, double2, double3, double4, double5, double6, boolean1, matrix4d);
	}

	private Matrix4d orthoGeneric(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d) {
		double double7 = 2.0 / (double2 - double1);
		double double8 = 2.0 / (double4 - double3);
		double double9 = (boolean1 ? 1.0 : 2.0) / (double5 - double6);
		double double10 = (double1 + double2) / (double1 - double2);
		double double11 = (double4 + double3) / (double3 - double4);
		double double12 = (boolean1 ? double5 : double6 + double5) / (double5 - double6);
		matrix4d._m30(this.m00 * double10 + this.m10 * double11 + this.m20 * double12 + this.m30)._m31(this.m01 * double10 + this.m11 * double11 + this.m21 * double12 + this.m31)._m32(this.m02 * double10 + this.m12 * double11 + this.m22 * double12 + this.m32)._m33(this.m03 * double10 + this.m13 * double11 + this.m23 * double12 + this.m33)._m00(this.m00 * double7)._m01(this.m01 * double7)._m02(this.m02 * double7)._m03(this.m03 * double7)._m10(this.m10 * double8)._m11(this.m11 * double8)._m12(this.m12 * double8)._m13(this.m13 * double8)._m20(this.m20 * double9)._m21(this.m21 * double9)._m22(this.m22 * double9)._m23(this.m23 * double9)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d ortho(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
		return this.ortho(double1, double2, double3, double4, double5, double6, false, matrix4d);
	}

	public Matrix4d ortho(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		return this.ortho(double1, double2, double3, double4, double5, double6, boolean1, this);
	}

	public Matrix4d ortho(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.ortho(double1, double2, double3, double4, double5, double6, false);
	}

	public Matrix4d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setOrthoLH(double1, double2, double3, double4, double5, double6, boolean1) : this.orthoLHGeneric(double1, double2, double3, double4, double5, double6, boolean1, matrix4d);
	}

	private Matrix4d orthoLHGeneric(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d) {
		double double7 = 2.0 / (double2 - double1);
		double double8 = 2.0 / (double4 - double3);
		double double9 = (boolean1 ? 1.0 : 2.0) / (double6 - double5);
		double double10 = (double1 + double2) / (double1 - double2);
		double double11 = (double4 + double3) / (double3 - double4);
		double double12 = (boolean1 ? double5 : double6 + double5) / (double5 - double6);
		matrix4d._m30(this.m00 * double10 + this.m10 * double11 + this.m20 * double12 + this.m30)._m31(this.m01 * double10 + this.m11 * double11 + this.m21 * double12 + this.m31)._m32(this.m02 * double10 + this.m12 * double11 + this.m22 * double12 + this.m32)._m33(this.m03 * double10 + this.m13 * double11 + this.m23 * double12 + this.m33)._m00(this.m00 * double7)._m01(this.m01 * double7)._m02(this.m02 * double7)._m03(this.m03 * double7)._m10(this.m10 * double8)._m11(this.m11 * double8)._m12(this.m12 * double8)._m13(this.m13 * double8)._m20(this.m20 * double9)._m21(this.m21 * double9)._m22(this.m22 * double9)._m23(this.m23 * double9)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
		return this.orthoLH(double1, double2, double3, double4, double5, double6, false, matrix4d);
	}

	public Matrix4d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		return this.orthoLH(double1, double2, double3, double4, double5, double6, boolean1, this);
	}

	public Matrix4d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.orthoLH(double1, double2, double3, double4, double5, double6, false);
	}

	public Matrix4d setOrtho(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00(2.0 / (double2 - double1))._m11(2.0 / (double4 - double3))._m22((boolean1 ? 1.0 : 2.0) / (double5 - double6))._m30((double2 + double1) / (double1 - double2))._m31((double4 + double3) / (double3 - double4))._m32((boolean1 ? double5 : double6 + double5) / (double5 - double6)).properties = 2;
		return this;
	}

	public Matrix4d setOrtho(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.setOrtho(double1, double2, double3, double4, double5, double6, false);
	}

	public Matrix4d setOrthoLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00(2.0 / (double2 - double1))._m11(2.0 / (double4 - double3))._m22((boolean1 ? 1.0 : 2.0) / (double6 - double5))._m30((double2 + double1) / (double1 - double2))._m31((double4 + double3) / (double3 - double4))._m32((boolean1 ? double5 : double6 + double5) / (double5 - double6)).properties = 2;
		return this;
	}

	public Matrix4d setOrthoLH(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.setOrthoLH(double1, double2, double3, double4, double5, double6, false);
	}

	public Matrix4d orthoSymmetric(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setOrthoSymmetric(double1, double2, double3, double4, boolean1) : this.orthoSymmetricGeneric(double1, double2, double3, double4, boolean1, matrix4d);
	}

	private Matrix4d orthoSymmetricGeneric(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d) {
		double double5 = 2.0 / double1;
		double double6 = 2.0 / double2;
		double double7 = (boolean1 ? 1.0 : 2.0) / (double3 - double4);
		double double8 = (boolean1 ? double3 : double4 + double3) / (double3 - double4);
		matrix4d._m30(this.m20 * double8 + this.m30)._m31(this.m21 * double8 + this.m31)._m32(this.m22 * double8 + this.m32)._m33(this.m23 * double8 + this.m33)._m00(this.m00 * double5)._m01(this.m01 * double5)._m02(this.m02 * double5)._m03(this.m03 * double5)._m10(this.m10 * double6)._m11(this.m11 * double6)._m12(this.m12 * double6)._m13(this.m13 * double6)._m20(this.m20 * double7)._m21(this.m21 * double7)._m22(this.m22 * double7)._m23(this.m23 * double7)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d orthoSymmetric(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		return this.orthoSymmetric(double1, double2, double3, double4, false, matrix4d);
	}

	public Matrix4d orthoSymmetric(double double1, double double2, double double3, double double4, boolean boolean1) {
		return this.orthoSymmetric(double1, double2, double3, double4, boolean1, this);
	}

	public Matrix4d orthoSymmetric(double double1, double double2, double double3, double double4) {
		return this.orthoSymmetric(double1, double2, double3, double4, false, this);
	}

	public Matrix4d orthoSymmetricLH(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setOrthoSymmetricLH(double1, double2, double3, double4, boolean1) : this.orthoSymmetricLHGeneric(double1, double2, double3, double4, boolean1, matrix4d);
	}

	private Matrix4d orthoSymmetricLHGeneric(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d) {
		double double5 = 2.0 / double1;
		double double6 = 2.0 / double2;
		double double7 = (boolean1 ? 1.0 : 2.0) / (double4 - double3);
		double double8 = (boolean1 ? double3 : double4 + double3) / (double3 - double4);
		matrix4d._m30(this.m20 * double8 + this.m30)._m31(this.m21 * double8 + this.m31)._m32(this.m22 * double8 + this.m32)._m33(this.m23 * double8 + this.m33)._m00(this.m00 * double5)._m01(this.m01 * double5)._m02(this.m02 * double5)._m03(this.m03 * double5)._m10(this.m10 * double6)._m11(this.m11 * double6)._m12(this.m12 * double6)._m13(this.m13 * double6)._m20(this.m20 * double7)._m21(this.m21 * double7)._m22(this.m22 * double7)._m23(this.m23 * double7)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d orthoSymmetricLH(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		return this.orthoSymmetricLH(double1, double2, double3, double4, false, matrix4d);
	}

	public Matrix4d orthoSymmetricLH(double double1, double double2, double double3, double double4, boolean boolean1) {
		return this.orthoSymmetricLH(double1, double2, double3, double4, boolean1, this);
	}

	public Matrix4d orthoSymmetricLH(double double1, double double2, double double3, double double4) {
		return this.orthoSymmetricLH(double1, double2, double3, double4, false, this);
	}

	public Matrix4d setOrthoSymmetric(double double1, double double2, double double3, double double4, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00(2.0 / double1)._m11(2.0 / double2)._m22((boolean1 ? 1.0 : 2.0) / (double3 - double4))._m32((boolean1 ? double3 : double4 + double3) / (double3 - double4)).properties = 2;
		return this;
	}

	public Matrix4d setOrthoSymmetric(double double1, double double2, double double3, double double4) {
		return this.setOrthoSymmetric(double1, double2, double3, double4, false);
	}

	public Matrix4d setOrthoSymmetricLH(double double1, double double2, double double3, double double4, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00(2.0 / double1)._m11(2.0 / double2)._m22((boolean1 ? 1.0 : 2.0) / (double4 - double3))._m32((boolean1 ? double3 : double4 + double3) / (double3 - double4)).properties = 2;
		return this;
	}

	public Matrix4d setOrthoSymmetricLH(double double1, double double2, double double3, double double4) {
		return this.setOrthoSymmetricLH(double1, double2, double3, double4, false);
	}

	public Matrix4d ortho2D(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setOrtho2D(double1, double2, double3, double4) : this.ortho2DGeneric(double1, double2, double3, double4, matrix4d);
	}

	private Matrix4d ortho2DGeneric(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		double double5 = 2.0 / (double2 - double1);
		double double6 = 2.0 / (double4 - double3);
		double double7 = (double2 + double1) / (double1 - double2);
		double double8 = (double4 + double3) / (double3 - double4);
		matrix4d._m30(this.m00 * double7 + this.m10 * double8 + this.m30)._m31(this.m01 * double7 + this.m11 * double8 + this.m31)._m32(this.m02 * double7 + this.m12 * double8 + this.m32)._m33(this.m03 * double7 + this.m13 * double8 + this.m33)._m00(this.m00 * double5)._m01(this.m01 * double5)._m02(this.m02 * double5)._m03(this.m03 * double5)._m10(this.m10 * double6)._m11(this.m11 * double6)._m12(this.m12 * double6)._m13(this.m13 * double6)._m20(-this.m20)._m21(-this.m21)._m22(-this.m22)._m23(-this.m23)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d ortho2D(double double1, double double2, double double3, double double4) {
		return this.ortho2D(double1, double2, double3, double4, this);
	}

	public Matrix4d ortho2DLH(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setOrtho2DLH(double1, double2, double3, double4) : this.ortho2DLHGeneric(double1, double2, double3, double4, matrix4d);
	}

	private Matrix4d ortho2DLHGeneric(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		double double5 = 2.0 / (double2 - double1);
		double double6 = 2.0 / (double4 - double3);
		double double7 = (double2 + double1) / (double1 - double2);
		double double8 = (double4 + double3) / (double3 - double4);
		matrix4d._m30(this.m00 * double7 + this.m10 * double8 + this.m30)._m31(this.m01 * double7 + this.m11 * double8 + this.m31)._m32(this.m02 * double7 + this.m12 * double8 + this.m32)._m33(this.m03 * double7 + this.m13 * double8 + this.m33)._m00(this.m00 * double5)._m01(this.m01 * double5)._m02(this.m02 * double5)._m03(this.m03 * double5)._m10(this.m10 * double6)._m11(this.m11 * double6)._m12(this.m12 * double6)._m13(this.m13 * double6)._m20(this.m20)._m21(this.m21)._m22(this.m22)._m23(this.m23)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d ortho2DLH(double double1, double double2, double double3, double double4) {
		return this.ortho2DLH(double1, double2, double3, double4, this);
	}

	public Matrix4d setOrtho2D(double double1, double double2, double double3, double double4) {
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00(2.0 / (double2 - double1))._m11(2.0 / (double4 - double3))._m22(-1.0)._m30((double2 + double1) / (double1 - double2))._m31((double4 + double3) / (double3 - double4)).properties = 2;
		return this;
	}

	public Matrix4d setOrtho2DLH(double double1, double double2, double double3, double double4) {
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00(2.0 / (double2 - double1))._m11(2.0 / (double4 - double3))._m30((double2 + double1) / (double1 - double2))._m31((double4 + double3) / (double3 - double4)).properties = 2;
		return this;
	}

	public Matrix4d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.lookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), this);
	}

	public Matrix4d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4d matrix4d) {
		return this.lookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), matrix4d);
	}

	public Matrix4d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setLookAlong(double1, double2, double3, double4, double5, double6) : this.lookAlongGeneric(double1, double2, double3, double4, double5, double6, matrix4d);
	}

	private Matrix4d lookAlongGeneric(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
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
		double double18 = this.m03 * double8 + this.m13 * double12 + this.m23 * double1;
		double double19 = this.m00 * double9 + this.m10 * double13 + this.m20 * double2;
		double double20 = this.m01 * double9 + this.m11 * double13 + this.m21 * double2;
		double double21 = this.m02 * double9 + this.m12 * double13 + this.m22 * double2;
		double double22 = this.m03 * double9 + this.m13 * double13 + this.m23 * double2;
		matrix4d._m20(this.m00 * double10 + this.m10 * double14 + this.m20 * double3)._m21(this.m01 * double10 + this.m11 * double14 + this.m21 * double3)._m22(this.m02 * double10 + this.m12 * double14 + this.m22 * double3)._m23(this.m03 * double10 + this.m13 * double14 + this.m23 * double3)._m00(double15)._m01(double16)._m02(double17)._m03(double18)._m10(double19)._m11(double20)._m12(double21)._m13(double22)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.lookAlong(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4d setLookAlong(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.setLookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix4d setLookAlong(double double1, double double2, double double3, double double4, double double5, double double6) {
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
		this._m00(double8)._m01(double12)._m02(double1)._m03(0.0)._m10(double9)._m11(double13)._m12(double2)._m13(0.0)._m20(double10)._m21(double14)._m22(double3)._m23(0.0)._m30(0.0)._m31(0.0)._m32(0.0)._m33(1.0).properties = 18;
		return this;
	}

	public Matrix4d setLookAt(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		return this.setLookAt(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z());
	}

	public Matrix4d setLookAt(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = double1 - double4;
		double double11 = double2 - double5;
		double double12 = double3 - double6;
		double double13 = Math.invsqrt(double10 * double10 + double11 * double11 + double12 * double12);
		double10 *= double13;
		double11 *= double13;
		double12 *= double13;
		double double14 = double8 * double12 - double9 * double11;
		double double15 = double9 * double10 - double7 * double12;
		double double16 = double7 * double11 - double8 * double10;
		double double17 = Math.invsqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double11 * double16 - double12 * double15;
		double double19 = double12 * double14 - double10 * double16;
		double double20 = double10 * double15 - double11 * double14;
		return this._m00(double14)._m01(double18)._m02(double10)._m03(0.0)._m10(double15)._m11(double19)._m12(double11)._m13(0.0)._m20(double16)._m21(double20)._m22(double12)._m23(0.0)._m30(-(double14 * double1 + double15 * double2 + double16 * double3))._m31(-(double18 * double1 + double19 * double2 + double20 * double3))._m32(-(double10 * double1 + double11 * double2 + double12 * double3))._m33(1.0)._properties(18);
	}

	public Matrix4d lookAt(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Matrix4d matrix4d) {
		return this.lookAt(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), matrix4d);
	}

	public Matrix4d lookAt(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		return this.lookAt(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), this);
	}

	public Matrix4d lookAt(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.setLookAt(double1, double2, double3, double4, double5, double6, double7, double8, double9);
		} else {
			return (this.properties & 1) != 0 ? this.lookAtPerspective(double1, double2, double3, double4, double5, double6, double7, double8, double9, matrix4d) : this.lookAtGeneric(double1, double2, double3, double4, double5, double6, double7, double8, double9, matrix4d);
		}
	}

	private Matrix4d lookAtGeneric(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d) {
		double double10 = double1 - double4;
		double double11 = double2 - double5;
		double double12 = double3 - double6;
		double double13 = Math.invsqrt(double10 * double10 + double11 * double11 + double12 * double12);
		double10 *= double13;
		double11 *= double13;
		double12 *= double13;
		double double14 = double8 * double12 - double9 * double11;
		double double15 = double9 * double10 - double7 * double12;
		double double16 = double7 * double11 - double8 * double10;
		double double17 = Math.invsqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double11 * double16 - double12 * double15;
		double double19 = double12 * double14 - double10 * double16;
		double double20 = double10 * double15 - double11 * double14;
		double double21 = -(double14 * double1 + double15 * double2 + double16 * double3);
		double double22 = -(double18 * double1 + double19 * double2 + double20 * double3);
		double double23 = -(double10 * double1 + double11 * double2 + double12 * double3);
		double double24 = this.m00 * double14 + this.m10 * double18 + this.m20 * double10;
		double double25 = this.m01 * double14 + this.m11 * double18 + this.m21 * double10;
		double double26 = this.m02 * double14 + this.m12 * double18 + this.m22 * double10;
		double double27 = this.m03 * double14 + this.m13 * double18 + this.m23 * double10;
		double double28 = this.m00 * double15 + this.m10 * double19 + this.m20 * double11;
		double double29 = this.m01 * double15 + this.m11 * double19 + this.m21 * double11;
		double double30 = this.m02 * double15 + this.m12 * double19 + this.m22 * double11;
		double double31 = this.m03 * double15 + this.m13 * double19 + this.m23 * double11;
		matrix4d._m30(this.m00 * double21 + this.m10 * double22 + this.m20 * double23 + this.m30)._m31(this.m01 * double21 + this.m11 * double22 + this.m21 * double23 + this.m31)._m32(this.m02 * double21 + this.m12 * double22 + this.m22 * double23 + this.m32)._m33(this.m03 * double21 + this.m13 * double22 + this.m23 * double23 + this.m33)._m20(this.m00 * double16 + this.m10 * double20 + this.m20 * double12)._m21(this.m01 * double16 + this.m11 * double20 + this.m21 * double12)._m22(this.m02 * double16 + this.m12 * double20 + this.m22 * double12)._m23(this.m03 * double16 + this.m13 * double20 + this.m23 * double12)._m00(double24)._m01(double25)._m02(double26)._m03(double27)._m10(double28)._m11(double29)._m12(double30)._m13(double31)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d lookAt(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		return this.lookAt(double1, double2, double3, double4, double5, double6, double7, double8, double9, this);
	}

	public Matrix4d lookAtPerspective(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d) {
		double double10 = double1 - double4;
		double double11 = double2 - double5;
		double double12 = double3 - double6;
		double double13 = Math.invsqrt(double10 * double10 + double11 * double11 + double12 * double12);
		double10 *= double13;
		double11 *= double13;
		double12 *= double13;
		double double14 = double8 * double12 - double9 * double11;
		double double15 = double9 * double10 - double7 * double12;
		double double16 = double7 * double11 - double8 * double10;
		double double17 = Math.invsqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double11 * double16 - double12 * double15;
		double double19 = double12 * double14 - double10 * double16;
		double double20 = double10 * double15 - double11 * double14;
		double double21 = -(double14 * double1 + double15 * double2 + double16 * double3);
		double double22 = -(double18 * double1 + double19 * double2 + double20 * double3);
		double double23 = -(double10 * double1 + double11 * double2 + double12 * double3);
		double double24 = this.m00 * double15;
		double double25 = this.m00 * double16;
		double double26 = this.m11 * double20;
		double double27 = this.m00 * double21;
		double double28 = this.m11 * double22;
		double double29 = this.m22 * double23 + this.m32;
		double double30 = this.m23 * double23;
		return matrix4d._m00(this.m00 * double14)._m01(this.m11 * double18)._m02(this.m22 * double10)._m03(this.m23 * double10)._m10(double24)._m11(this.m11 * double19)._m12(this.m22 * double11)._m13(this.m23 * double11)._m20(double25)._m21(double26)._m22(this.m22 * double12)._m23(this.m23 * double12)._m30(double27)._m31(double28)._m32(double29)._m33(double30)._properties(0);
	}

	public Matrix4d setLookAtLH(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		return this.setLookAtLH(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z());
	}

	public Matrix4d setLookAtLH(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = double4 - double1;
		double double11 = double5 - double2;
		double double12 = double6 - double3;
		double double13 = Math.invsqrt(double10 * double10 + double11 * double11 + double12 * double12);
		double10 *= double13;
		double11 *= double13;
		double12 *= double13;
		double double14 = double8 * double12 - double9 * double11;
		double double15 = double9 * double10 - double7 * double12;
		double double16 = double7 * double11 - double8 * double10;
		double double17 = Math.invsqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double11 * double16 - double12 * double15;
		double double19 = double12 * double14 - double10 * double16;
		double double20 = double10 * double15 - double11 * double14;
		this._m00(double14)._m01(double18)._m02(double10)._m03(0.0)._m10(double15)._m11(double19)._m12(double11)._m13(0.0)._m20(double16)._m21(double20)._m22(double12)._m23(0.0)._m30(-(double14 * double1 + double15 * double2 + double16 * double3))._m31(-(double18 * double1 + double19 * double2 + double20 * double3))._m32(-(double10 * double1 + double11 * double2 + double12 * double3))._m33(1.0).properties = 18;
		return this;
	}

	public Matrix4d lookAtLH(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Matrix4d matrix4d) {
		return this.lookAtLH(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), matrix4d);
	}

	public Matrix4d lookAtLH(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		return this.lookAtLH(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), this);
	}

	public Matrix4d lookAtLH(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d) {
		if ((this.properties & 4) != 0) {
			return matrix4d.setLookAtLH(double1, double2, double3, double4, double5, double6, double7, double8, double9);
		} else {
			return (this.properties & 1) != 0 ? this.lookAtPerspectiveLH(double1, double2, double3, double4, double5, double6, double7, double8, double9, matrix4d) : this.lookAtLHGeneric(double1, double2, double3, double4, double5, double6, double7, double8, double9, matrix4d);
		}
	}

	private Matrix4d lookAtLHGeneric(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d) {
		double double10 = double4 - double1;
		double double11 = double5 - double2;
		double double12 = double6 - double3;
		double double13 = Math.invsqrt(double10 * double10 + double11 * double11 + double12 * double12);
		double10 *= double13;
		double11 *= double13;
		double12 *= double13;
		double double14 = double8 * double12 - double9 * double11;
		double double15 = double9 * double10 - double7 * double12;
		double double16 = double7 * double11 - double8 * double10;
		double double17 = Math.invsqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double11 * double16 - double12 * double15;
		double double19 = double12 * double14 - double10 * double16;
		double double20 = double10 * double15 - double11 * double14;
		double double21 = -(double14 * double1 + double15 * double2 + double16 * double3);
		double double22 = -(double18 * double1 + double19 * double2 + double20 * double3);
		double double23 = -(double10 * double1 + double11 * double2 + double12 * double3);
		double double24 = this.m00 * double14 + this.m10 * double18 + this.m20 * double10;
		double double25 = this.m01 * double14 + this.m11 * double18 + this.m21 * double10;
		double double26 = this.m02 * double14 + this.m12 * double18 + this.m22 * double10;
		double double27 = this.m03 * double14 + this.m13 * double18 + this.m23 * double10;
		double double28 = this.m00 * double15 + this.m10 * double19 + this.m20 * double11;
		double double29 = this.m01 * double15 + this.m11 * double19 + this.m21 * double11;
		double double30 = this.m02 * double15 + this.m12 * double19 + this.m22 * double11;
		double double31 = this.m03 * double15 + this.m13 * double19 + this.m23 * double11;
		matrix4d._m30(this.m00 * double21 + this.m10 * double22 + this.m20 * double23 + this.m30)._m31(this.m01 * double21 + this.m11 * double22 + this.m21 * double23 + this.m31)._m32(this.m02 * double21 + this.m12 * double22 + this.m22 * double23 + this.m32)._m33(this.m03 * double21 + this.m13 * double22 + this.m23 * double23 + this.m33)._m20(this.m00 * double16 + this.m10 * double20 + this.m20 * double12)._m21(this.m01 * double16 + this.m11 * double20 + this.m21 * double12)._m22(this.m02 * double16 + this.m12 * double20 + this.m22 * double12)._m23(this.m03 * double16 + this.m13 * double20 + this.m23 * double12)._m00(double24)._m01(double25)._m02(double26)._m03(double27)._m10(double28)._m11(double29)._m12(double30)._m13(double31)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d lookAtLH(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		return this.lookAtLH(double1, double2, double3, double4, double5, double6, double7, double8, double9, this);
	}

	public Matrix4d lookAtPerspectiveLH(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d) {
		double double10 = double4 - double1;
		double double11 = double5 - double2;
		double double12 = double6 - double3;
		double double13 = Math.invsqrt(double10 * double10 + double11 * double11 + double12 * double12);
		double10 *= double13;
		double11 *= double13;
		double12 *= double13;
		double double14 = double8 * double12 - double9 * double11;
		double double15 = double9 * double10 - double7 * double12;
		double double16 = double7 * double11 - double8 * double10;
		double double17 = Math.invsqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double11 * double16 - double12 * double15;
		double double19 = double12 * double14 - double10 * double16;
		double double20 = double10 * double15 - double11 * double14;
		double double21 = -(double14 * double1 + double15 * double2 + double16 * double3);
		double double22 = -(double18 * double1 + double19 * double2 + double20 * double3);
		double double23 = -(double10 * double1 + double11 * double2 + double12 * double3);
		double double24 = this.m00 * double14;
		double double25 = this.m11 * double18;
		double double26 = this.m22 * double10;
		double double27 = this.m23 * double10;
		double double28 = this.m00 * double15;
		double double29 = this.m11 * double19;
		double double30 = this.m22 * double11;
		double double31 = this.m23 * double11;
		double double32 = this.m00 * double16;
		double double33 = this.m11 * double20;
		double double34 = this.m22 * double12;
		double double35 = this.m23 * double12;
		double double36 = this.m00 * double21;
		double double37 = this.m11 * double22;
		double double38 = this.m22 * double23 + this.m32;
		double double39 = this.m23 * double23;
		matrix4d._m00(double24)._m01(double25)._m02(double26)._m03(double27)._m10(double28)._m11(double29)._m12(double30)._m13(double31)._m20(double32)._m21(double33)._m22(double34)._m23(double35)._m30(double36)._m31(double37)._m32(double38)._m33(double39)._properties(0);
		return matrix4d;
	}

	public Matrix4d perspective(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setPerspective(double1, double2, double3, double4, boolean1) : this.perspectiveGeneric(double1, double2, double3, double4, boolean1, matrix4d);
	}

	private Matrix4d perspectiveGeneric(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d) {
		double double5 = Math.tan(double1 * 0.5);
		double double6 = 1.0 / (double5 * double2);
		double double7 = 1.0 / double5;
		boolean boolean2 = double4 > 0.0 && Double.isInfinite(double4);
		boolean boolean3 = double3 > 0.0 && Double.isInfinite(double3);
		double double8;
		double double9;
		double double10;
		if (boolean2) {
			double10 = 1.0E-6;
			double8 = double10 - 1.0;
			double9 = (double10 - (boolean1 ? 1.0 : 2.0)) * double3;
		} else if (boolean3) {
			double10 = 1.0E-6;
			double8 = (boolean1 ? 0.0 : 1.0) - double10;
			double9 = ((boolean1 ? 1.0 : 2.0) - double10) * double4;
		} else {
			double8 = (boolean1 ? double4 : double4 + double3) / (double3 - double4);
			double9 = (boolean1 ? double4 : double4 + double4) * double3 / (double3 - double4);
		}

		double10 = this.m20 * double8 - this.m30;
		double double11 = this.m21 * double8 - this.m31;
		double double12 = this.m22 * double8 - this.m32;
		double double13 = this.m23 * double8 - this.m33;
		matrix4d._m00(this.m00 * double6)._m01(this.m01 * double6)._m02(this.m02 * double6)._m03(this.m03 * double6)._m10(this.m10 * double7)._m11(this.m11 * double7)._m12(this.m12 * double7)._m13(this.m13 * double7)._m30(this.m20 * double9)._m31(this.m21 * double9)._m32(this.m22 * double9)._m33(this.m23 * double9)._m20(double10)._m21(double11)._m22(double12)._m23(double13)._properties(this.properties & -31);
		return matrix4d;
	}

	public Matrix4d perspective(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		return this.perspective(double1, double2, double3, double4, false, matrix4d);
	}

	public Matrix4d perspective(double double1, double double2, double double3, double double4, boolean boolean1) {
		return this.perspective(double1, double2, double3, double4, boolean1, this);
	}

	public Matrix4d perspective(double double1, double double2, double double3, double double4) {
		return this.perspective(double1, double2, double3, double4, this);
	}

	public Matrix4d perspectiveRect(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setPerspectiveRect(double1, double2, double3, double4, boolean1) : this.perspectiveRectGeneric(double1, double2, double3, double4, boolean1, matrix4d);
	}

	private Matrix4d perspectiveRectGeneric(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d) {
		double double5 = (double3 + double3) / double1;
		double double6 = (double3 + double3) / double2;
		boolean boolean2 = double4 > 0.0 && Double.isInfinite(double4);
		boolean boolean3 = double3 > 0.0 && Double.isInfinite(double3);
		double double7;
		double double8;
		double double9;
		if (boolean2) {
			double9 = 9.999999974752427E-7;
			double7 = double9 - 1.0;
			double8 = (double9 - (boolean1 ? 1.0 : 2.0)) * double3;
		} else if (boolean3) {
			double9 = 9.999999974752427E-7;
			double7 = (boolean1 ? 0.0 : 1.0) - double9;
			double8 = ((boolean1 ? 1.0 : 2.0) - double9) * double4;
		} else {
			double7 = (boolean1 ? double4 : double4 + double3) / (double3 - double4);
			double8 = (boolean1 ? double4 : double4 + double4) * double3 / (double3 - double4);
		}

		double9 = this.m20 * double7 - this.m30;
		double double10 = this.m21 * double7 - this.m31;
		double double11 = this.m22 * double7 - this.m32;
		double double12 = this.m23 * double7 - this.m33;
		matrix4d._m00(this.m00 * double5)._m01(this.m01 * double5)._m02(this.m02 * double5)._m03(this.m03 * double5)._m10(this.m10 * double6)._m11(this.m11 * double6)._m12(this.m12 * double6)._m13(this.m13 * double6)._m30(this.m20 * double8)._m31(this.m21 * double8)._m32(this.m22 * double8)._m33(this.m23 * double8)._m20(double9)._m21(double10)._m22(double11)._m23(double12)._properties(this.properties & -31);
		return matrix4d;
	}

	public Matrix4d perspectiveRect(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		return this.perspectiveRect(double1, double2, double3, double4, false, matrix4d);
	}

	public Matrix4d perspectiveRect(double double1, double double2, double double3, double double4, boolean boolean1) {
		return this.perspectiveRect(double1, double2, double3, double4, boolean1, this);
	}

	public Matrix4d perspectiveRect(double double1, double double2, double double3, double double4) {
		return this.perspectiveRect(double1, double2, double3, double4, this);
	}

	public Matrix4d perspectiveOffCenter(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setPerspectiveOffCenter(double1, double2, double3, double4, double5, double6, boolean1) : this.perspectiveOffCenterGeneric(double1, double2, double3, double4, double5, double6, boolean1, matrix4d);
	}

	private Matrix4d perspectiveOffCenterGeneric(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d) {
		double double7 = Math.tan(double1 * 0.5);
		double double8 = 1.0 / (double7 * double4);
		double double9 = 1.0 / double7;
		double double10 = Math.tan(double2);
		double double11 = Math.tan(double3);
		double double12 = double10 * double8;
		double double13 = double11 * double9;
		boolean boolean2 = double6 > 0.0 && Double.isInfinite(double6);
		boolean boolean3 = double5 > 0.0 && Double.isInfinite(double5);
		double double14;
		double double15;
		double double16;
		if (boolean2) {
			double16 = 1.0E-6;
			double14 = double16 - 1.0;
			double15 = (double16 - (boolean1 ? 1.0 : 2.0)) * double5;
		} else if (boolean3) {
			double16 = 1.0E-6;
			double14 = (boolean1 ? 0.0 : 1.0) - double16;
			double15 = ((boolean1 ? 1.0 : 2.0) - double16) * double6;
		} else {
			double14 = (boolean1 ? double6 : double6 + double5) / (double5 - double6);
			double15 = (boolean1 ? double6 : double6 + double6) * double5 / (double5 - double6);
		}

		double16 = this.m00 * double12 + this.m10 * double13 + this.m20 * double14 - this.m30;
		double double17 = this.m01 * double12 + this.m11 * double13 + this.m21 * double14 - this.m31;
		double double18 = this.m02 * double12 + this.m12 * double13 + this.m22 * double14 - this.m32;
		double double19 = this.m03 * double12 + this.m13 * double13 + this.m23 * double14 - this.m33;
		matrix4d._m00(this.m00 * double8)._m01(this.m01 * double8)._m02(this.m02 * double8)._m03(this.m03 * double8)._m10(this.m10 * double9)._m11(this.m11 * double9)._m12(this.m12 * double9)._m13(this.m13 * double9)._m30(this.m20 * double15)._m31(this.m21 * double15)._m32(this.m22 * double15)._m33(this.m23 * double15)._m20(double16)._m21(double17)._m22(double18)._m23(double19)._properties(this.properties & ~(30 | (double12 == 0.0 && double13 == 0.0 ? 0 : 1)));
		return matrix4d;
	}

	public Matrix4d perspectiveOffCenter(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
		return this.perspectiveOffCenter(double1, double2, double3, double4, double5, double6, false, matrix4d);
	}

	public Matrix4d perspectiveOffCenter(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		return this.perspectiveOffCenter(double1, double2, double3, double4, double5, double6, boolean1, this);
	}

	public Matrix4d perspectiveOffCenter(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.perspectiveOffCenter(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4d setPerspective(double double1, double double2, double double3, double double4, boolean boolean1) {
		double double5 = Math.tan(double1 * 0.5);
		this._m00(1.0 / (double5 * double2))._m01(0.0)._m02(0.0)._m03(0.0)._m10(0.0)._m11(1.0 / double5)._m12(0.0)._m13(0.0)._m20(0.0)._m21(0.0);
		boolean boolean2 = double4 > 0.0 && Double.isInfinite(double4);
		boolean boolean3 = double3 > 0.0 && Double.isInfinite(double3);
		double double6;
		if (boolean2) {
			double6 = 1.0E-6;
			this._m22(double6 - 1.0)._m32((double6 - (boolean1 ? 1.0 : 2.0)) * double3);
		} else if (boolean3) {
			double6 = 1.0E-6;
			this._m22((boolean1 ? 0.0 : 1.0) - double6)._m32(((boolean1 ? 1.0 : 2.0) - double6) * double4);
		} else {
			this._m22((boolean1 ? double4 : double4 + double3) / (double3 - double4))._m32((boolean1 ? double4 : double4 + double4) * double3 / (double3 - double4));
		}

		this._m23(-1.0)._m30(0.0)._m31(0.0)._m33(0.0).properties = 1;
		return this;
	}

	public Matrix4d setPerspective(double double1, double double2, double double3, double double4) {
		return this.setPerspective(double1, double2, double3, double4, false);
	}

	public Matrix4d setPerspectiveRect(double double1, double double2, double double3, double double4, boolean boolean1) {
		this.zero();
		this._m00((double3 + double3) / double1);
		this._m11((double3 + double3) / double2);
		boolean boolean2 = double4 > 0.0 && Double.isInfinite(double4);
		boolean boolean3 = double3 > 0.0 && Double.isInfinite(double3);
		double double5;
		if (boolean2) {
			double5 = 1.0E-6;
			this._m22(double5 - 1.0);
			this._m32((double5 - (boolean1 ? 1.0 : 2.0)) * double3);
		} else if (boolean3) {
			double5 = 9.999999974752427E-7;
			this._m22((boolean1 ? 0.0 : 1.0) - double5);
			this._m32(((boolean1 ? 1.0 : 2.0) - double5) * double4);
		} else {
			this._m22((boolean1 ? double4 : double4 + double3) / (double3 - double4));
			this._m32((boolean1 ? double4 : double4 + double4) * double3 / (double3 - double4));
		}

		this._m23(-1.0);
		this.properties = 1;
		return this;
	}

	public Matrix4d setPerspectiveRect(double double1, double double2, double double3, double double4) {
		return this.setPerspectiveRect(double1, double2, double3, double4, false);
	}

	public Matrix4d setPerspectiveOffCenter(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.setPerspectiveOffCenter(double1, double2, double3, double4, double5, double6, false);
	}

	public Matrix4d setPerspectiveOffCenter(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		this.zero();
		double double7 = Math.tan(double1 * 0.5);
		double double8 = 1.0 / (double7 * double4);
		double double9 = 1.0 / double7;
		this._m00(double8)._m11(double9);
		double double10 = Math.tan(double2);
		double double11 = Math.tan(double3);
		this._m20(double10 * double8)._m21(double11 * double9);
		boolean boolean2 = double6 > 0.0 && Double.isInfinite(double6);
		boolean boolean3 = double5 > 0.0 && Double.isInfinite(double5);
		double double12;
		if (boolean2) {
			double12 = 1.0E-6;
			this._m22(double12 - 1.0)._m32((double12 - (boolean1 ? 1.0 : 2.0)) * double5);
		} else if (boolean3) {
			double12 = 1.0E-6;
			this._m22((boolean1 ? 0.0 : 1.0) - double12)._m32(((boolean1 ? 1.0 : 2.0) - double12) * double6);
		} else {
			this._m22((boolean1 ? double6 : double6 + double5) / (double5 - double6))._m32((boolean1 ? double6 : double6 + double6) * double5 / (double5 - double6));
		}

		this._m23(-1.0)._m30(0.0)._m31(0.0)._m33(0.0).properties = double2 == 0.0 && double3 == 0.0 ? 1 : 0;
		return this;
	}

	public Matrix4d perspectiveLH(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setPerspectiveLH(double1, double2, double3, double4, boolean1) : this.perspectiveLHGeneric(double1, double2, double3, double4, boolean1, matrix4d);
	}

	private Matrix4d perspectiveLHGeneric(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d) {
		double double5 = Math.tan(double1 * 0.5);
		double double6 = 1.0 / (double5 * double2);
		double double7 = 1.0 / double5;
		boolean boolean2 = double4 > 0.0 && Double.isInfinite(double4);
		boolean boolean3 = double3 > 0.0 && Double.isInfinite(double3);
		double double8;
		double double9;
		double double10;
		if (boolean2) {
			double10 = 1.0E-6;
			double8 = 1.0 - double10;
			double9 = (double10 - (boolean1 ? 1.0 : 2.0)) * double3;
		} else if (boolean3) {
			double10 = 1.0E-6;
			double8 = (boolean1 ? 0.0 : 1.0) - double10;
			double9 = ((boolean1 ? 1.0 : 2.0) - double10) * double4;
		} else {
			double8 = (boolean1 ? double4 : double4 + double3) / (double4 - double3);
			double9 = (boolean1 ? double4 : double4 + double4) * double3 / (double3 - double4);
		}

		double10 = this.m20 * double8 + this.m30;
		double double11 = this.m21 * double8 + this.m31;
		double double12 = this.m22 * double8 + this.m32;
		double double13 = this.m23 * double8 + this.m33;
		matrix4d._m00(this.m00 * double6)._m01(this.m01 * double6)._m02(this.m02 * double6)._m03(this.m03 * double6)._m10(this.m10 * double7)._m11(this.m11 * double7)._m12(this.m12 * double7)._m13(this.m13 * double7)._m30(this.m20 * double9)._m31(this.m21 * double9)._m32(this.m22 * double9)._m33(this.m23 * double9)._m20(double10)._m21(double11)._m22(double12)._m23(double13)._properties(this.properties & -31);
		return matrix4d;
	}

	public Matrix4d perspectiveLH(double double1, double double2, double double3, double double4, boolean boolean1) {
		return this.perspectiveLH(double1, double2, double3, double4, boolean1, this);
	}

	public Matrix4d perspectiveLH(double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		return this.perspectiveLH(double1, double2, double3, double4, false, matrix4d);
	}

	public Matrix4d perspectiveLH(double double1, double double2, double double3, double double4) {
		return this.perspectiveLH(double1, double2, double3, double4, this);
	}

	public Matrix4d setPerspectiveLH(double double1, double double2, double double3, double double4, boolean boolean1) {
		double double5 = Math.tan(double1 * 0.5);
		this._m00(1.0 / (double5 * double2))._m01(0.0)._m02(0.0)._m03(0.0)._m10(0.0)._m11(1.0 / double5)._m12(0.0)._m13(0.0)._m20(0.0)._m21(0.0);
		boolean boolean2 = double4 > 0.0 && Double.isInfinite(double4);
		boolean boolean3 = double3 > 0.0 && Double.isInfinite(double3);
		double double6;
		if (boolean2) {
			double6 = 1.0E-6;
			this._m22(1.0 - double6)._m32((double6 - (boolean1 ? 1.0 : 2.0)) * double3);
		} else if (boolean3) {
			double6 = 1.0E-6;
			this._m22((boolean1 ? 0.0 : 1.0) - double6)._m32(((boolean1 ? 1.0 : 2.0) - double6) * double4);
		} else {
			this._m22((boolean1 ? double4 : double4 + double3) / (double4 - double3))._m32((boolean1 ? double4 : double4 + double4) * double3 / (double3 - double4));
		}

		this._m23(1.0)._m30(0.0)._m31(0.0)._m33(0.0).properties = 1;
		return this;
	}

	public Matrix4d setPerspectiveLH(double double1, double double2, double double3, double double4) {
		return this.setPerspectiveLH(double1, double2, double3, double4, false);
	}

	public Matrix4d frustum(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setFrustum(double1, double2, double3, double4, double5, double6, boolean1) : this.frustumGeneric(double1, double2, double3, double4, double5, double6, boolean1, matrix4d);
	}

	private Matrix4d frustumGeneric(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d) {
		double double7 = (double5 + double5) / (double2 - double1);
		double double8 = (double5 + double5) / (double4 - double3);
		double double9 = (double2 + double1) / (double2 - double1);
		double double10 = (double4 + double3) / (double4 - double3);
		boolean boolean2 = double6 > 0.0 && Double.isInfinite(double6);
		boolean boolean3 = double5 > 0.0 && Double.isInfinite(double5);
		double double11;
		double double12;
		double double13;
		if (boolean2) {
			double13 = 1.0E-6;
			double11 = double13 - 1.0;
			double12 = (double13 - (boolean1 ? 1.0 : 2.0)) * double5;
		} else if (boolean3) {
			double13 = 1.0E-6;
			double11 = (boolean1 ? 0.0 : 1.0) - double13;
			double12 = ((boolean1 ? 1.0 : 2.0) - double13) * double6;
		} else {
			double11 = (boolean1 ? double6 : double6 + double5) / (double5 - double6);
			double12 = (boolean1 ? double6 : double6 + double6) * double5 / (double5 - double6);
		}

		double13 = this.m00 * double9 + this.m10 * double10 + this.m20 * double11 - this.m30;
		double double14 = this.m01 * double9 + this.m11 * double10 + this.m21 * double11 - this.m31;
		double double15 = this.m02 * double9 + this.m12 * double10 + this.m22 * double11 - this.m32;
		double double16 = this.m03 * double9 + this.m13 * double10 + this.m23 * double11 - this.m33;
		matrix4d._m00(this.m00 * double7)._m01(this.m01 * double7)._m02(this.m02 * double7)._m03(this.m03 * double7)._m10(this.m10 * double8)._m11(this.m11 * double8)._m12(this.m12 * double8)._m13(this.m13 * double8)._m30(this.m20 * double12)._m31(this.m21 * double12)._m32(this.m22 * double12)._m33(this.m23 * double12)._m20(double13)._m21(double14)._m22(double15)._m23(double16)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(0);
		return matrix4d;
	}

	public Matrix4d frustum(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
		return this.frustum(double1, double2, double3, double4, double5, double6, false, matrix4d);
	}

	public Matrix4d frustum(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		return this.frustum(double1, double2, double3, double4, double5, double6, boolean1, this);
	}

	public Matrix4d frustum(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.frustum(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4d setFrustum(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00((double5 + double5) / (double2 - double1))._m11((double5 + double5) / (double4 - double3))._m20((double2 + double1) / (double2 - double1))._m21((double4 + double3) / (double4 - double3));
		boolean boolean2 = double6 > 0.0 && Double.isInfinite(double6);
		boolean boolean3 = double5 > 0.0 && Double.isInfinite(double5);
		double double7;
		if (boolean2) {
			double7 = 1.0E-6;
			this._m22(double7 - 1.0)._m32((double7 - (boolean1 ? 1.0 : 2.0)) * double5);
		} else if (boolean3) {
			double7 = 1.0E-6;
			this._m22((boolean1 ? 0.0 : 1.0) - double7)._m32(((boolean1 ? 1.0 : 2.0) - double7) * double6);
		} else {
			this._m22((boolean1 ? double6 : double6 + double5) / (double5 - double6))._m32((boolean1 ? double6 : double6 + double6) * double5 / (double5 - double6));
		}

		this._m23(-1.0)._m33(0.0).properties = this.m20 == 0.0 && this.m21 == 0.0 ? 1 : 0;
		return this;
	}

	public Matrix4d setFrustum(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.setFrustum(double1, double2, double3, double4, double5, double6, false);
	}

	public Matrix4d frustumLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d) {
		return (this.properties & 4) != 0 ? matrix4d.setFrustumLH(double1, double2, double3, double4, double5, double6, boolean1) : this.frustumLHGeneric(double1, double2, double3, double4, double5, double6, boolean1, matrix4d);
	}

	private Matrix4d frustumLHGeneric(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d) {
		double double7 = (double5 + double5) / (double2 - double1);
		double double8 = (double5 + double5) / (double4 - double3);
		double double9 = (double2 + double1) / (double2 - double1);
		double double10 = (double4 + double3) / (double4 - double3);
		boolean boolean2 = double6 > 0.0 && Double.isInfinite(double6);
		boolean boolean3 = double5 > 0.0 && Double.isInfinite(double5);
		double double11;
		double double12;
		double double13;
		if (boolean2) {
			double13 = 1.0E-6;
			double11 = 1.0 - double13;
			double12 = (double13 - (boolean1 ? 1.0 : 2.0)) * double5;
		} else if (boolean3) {
			double13 = 1.0E-6;
			double11 = (boolean1 ? 0.0 : 1.0) - double13;
			double12 = ((boolean1 ? 1.0 : 2.0) - double13) * double6;
		} else {
			double11 = (boolean1 ? double6 : double6 + double5) / (double6 - double5);
			double12 = (boolean1 ? double6 : double6 + double6) * double5 / (double5 - double6);
		}

		double13 = this.m00 * double9 + this.m10 * double10 + this.m20 * double11 + this.m30;
		double double14 = this.m01 * double9 + this.m11 * double10 + this.m21 * double11 + this.m31;
		double double15 = this.m02 * double9 + this.m12 * double10 + this.m22 * double11 + this.m32;
		double double16 = this.m03 * double9 + this.m13 * double10 + this.m23 * double11 + this.m33;
		matrix4d._m00(this.m00 * double7)._m01(this.m01 * double7)._m02(this.m02 * double7)._m03(this.m03 * double7)._m10(this.m10 * double8)._m11(this.m11 * double8)._m12(this.m12 * double8)._m13(this.m13 * double8)._m30(this.m20 * double12)._m31(this.m21 * double12)._m32(this.m22 * double12)._m33(this.m23 * double12)._m20(double13)._m21(double14)._m22(double15)._m23(double16)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(0);
		return matrix4d;
	}

	public Matrix4d frustumLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		return this.frustumLH(double1, double2, double3, double4, double5, double6, boolean1, this);
	}

	public Matrix4d frustumLH(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
		return this.frustumLH(double1, double2, double3, double4, double5, double6, false, matrix4d);
	}

	public Matrix4d frustumLH(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.frustumLH(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4d setFrustumLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this._m00((double5 + double5) / (double2 - double1))._m11((double5 + double5) / (double4 - double3))._m20((double2 + double1) / (double2 - double1))._m21((double4 + double3) / (double4 - double3));
		boolean boolean2 = double6 > 0.0 && Double.isInfinite(double6);
		boolean boolean3 = double5 > 0.0 && Double.isInfinite(double5);
		double double7;
		if (boolean2) {
			double7 = 1.0E-6;
			this._m22(1.0 - double7)._m32((double7 - (boolean1 ? 1.0 : 2.0)) * double5);
		} else if (boolean3) {
			double7 = 1.0E-6;
			this._m22((boolean1 ? 0.0 : 1.0) - double7)._m32(((boolean1 ? 1.0 : 2.0) - double7) * double6);
		} else {
			this._m22((boolean1 ? double6 : double6 + double5) / (double6 - double5))._m32((boolean1 ? double6 : double6 + double6) * double5 / (double5 - double6));
		}

		this._m23(1.0)._m33(0.0).properties = this.m20 == 0.0 && this.m21 == 0.0 ? 1 : 0;
		return this;
	}

	public Matrix4d setFrustumLH(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.setFrustumLH(double1, double2, double3, double4, double5, double6, false);
	}

	public Matrix4d setFromIntrinsic(double double1, double double2, double double3, double double4, double double5, int int1, int int2, double double6, double double7) {
		double double8 = 2.0 / (double)int1;
		double double9 = 2.0 / (double)int2;
		double double10 = 2.0 / (double6 - double7);
		this.m00 = double8 * double1;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m03 = 0.0;
		this.m10 = double8 * double3;
		this.m11 = double9 * double2;
		this.m12 = 0.0;
		this.m13 = 0.0;
		this.m20 = double8 * double4 - 1.0;
		this.m21 = double9 * double5 - 1.0;
		this.m22 = double10 * -(double6 + double7) + (double7 + double6) / (double6 - double7);
		this.m23 = -1.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = double10 * -double6 * double7;
		this.m33 = 0.0;
		this.properties = 1;
		return this;
	}

	public Vector4d frustumPlane(int int1, Vector4d vector4d) {
		switch (int1) {
		case 0: 
			vector4d.set(this.m03 + this.m00, this.m13 + this.m10, this.m23 + this.m20, this.m33 + this.m30).normalize3();
			break;
		
		case 1: 
			vector4d.set(this.m03 - this.m00, this.m13 - this.m10, this.m23 - this.m20, this.m33 - this.m30).normalize3();
			break;
		
		case 2: 
			vector4d.set(this.m03 + this.m01, this.m13 + this.m11, this.m23 + this.m21, this.m33 + this.m31).normalize3();
			break;
		
		case 3: 
			vector4d.set(this.m03 - this.m01, this.m13 - this.m11, this.m23 - this.m21, this.m33 - this.m31).normalize3();
			break;
		
		case 4: 
			vector4d.set(this.m03 + this.m02, this.m13 + this.m12, this.m23 + this.m22, this.m33 + this.m32).normalize3();
			break;
		
		case 5: 
			vector4d.set(this.m03 - this.m02, this.m13 - this.m12, this.m23 - this.m22, this.m33 - this.m32).normalize3();
			break;
		
		default: 
			throw new IllegalArgumentException("dest");
		
		}
		return vector4d;
	}

	public Vector3d frustumCorner(int int1, Vector3d vector3d) {
		double double1;
		double double2;
		double double3;
		double double4;
		double double5;
		double double6;
		double double7;
		double double8;
		double double9;
		double double10;
		double double11;
		double double12;
		switch (int1) {
		case 0: 
			double4 = this.m03 + this.m00;
			double5 = this.m13 + this.m10;
			double6 = this.m23 + this.m20;
			double1 = this.m33 + this.m30;
			double7 = this.m03 + this.m01;
			double8 = this.m13 + this.m11;
			double9 = this.m23 + this.m21;
			double2 = this.m33 + this.m31;
			double10 = this.m03 + this.m02;
			double11 = this.m13 + this.m12;
			double12 = this.m23 + this.m22;
			double3 = this.m33 + this.m32;
			break;
		
		case 1: 
			double4 = this.m03 - this.m00;
			double5 = this.m13 - this.m10;
			double6 = this.m23 - this.m20;
			double1 = this.m33 - this.m30;
			double7 = this.m03 + this.m01;
			double8 = this.m13 + this.m11;
			double9 = this.m23 + this.m21;
			double2 = this.m33 + this.m31;
			double10 = this.m03 + this.m02;
			double11 = this.m13 + this.m12;
			double12 = this.m23 + this.m22;
			double3 = this.m33 + this.m32;
			break;
		
		case 2: 
			double4 = this.m03 - this.m00;
			double5 = this.m13 - this.m10;
			double6 = this.m23 - this.m20;
			double1 = this.m33 - this.m30;
			double7 = this.m03 - this.m01;
			double8 = this.m13 - this.m11;
			double9 = this.m23 - this.m21;
			double2 = this.m33 - this.m31;
			double10 = this.m03 + this.m02;
			double11 = this.m13 + this.m12;
			double12 = this.m23 + this.m22;
			double3 = this.m33 + this.m32;
			break;
		
		case 3: 
			double4 = this.m03 + this.m00;
			double5 = this.m13 + this.m10;
			double6 = this.m23 + this.m20;
			double1 = this.m33 + this.m30;
			double7 = this.m03 - this.m01;
			double8 = this.m13 - this.m11;
			double9 = this.m23 - this.m21;
			double2 = this.m33 - this.m31;
			double10 = this.m03 + this.m02;
			double11 = this.m13 + this.m12;
			double12 = this.m23 + this.m22;
			double3 = this.m33 + this.m32;
			break;
		
		case 4: 
			double4 = this.m03 - this.m00;
			double5 = this.m13 - this.m10;
			double6 = this.m23 - this.m20;
			double1 = this.m33 - this.m30;
			double7 = this.m03 + this.m01;
			double8 = this.m13 + this.m11;
			double9 = this.m23 + this.m21;
			double2 = this.m33 + this.m31;
			double10 = this.m03 - this.m02;
			double11 = this.m13 - this.m12;
			double12 = this.m23 - this.m22;
			double3 = this.m33 - this.m32;
			break;
		
		case 5: 
			double4 = this.m03 + this.m00;
			double5 = this.m13 + this.m10;
			double6 = this.m23 + this.m20;
			double1 = this.m33 + this.m30;
			double7 = this.m03 + this.m01;
			double8 = this.m13 + this.m11;
			double9 = this.m23 + this.m21;
			double2 = this.m33 + this.m31;
			double10 = this.m03 - this.m02;
			double11 = this.m13 - this.m12;
			double12 = this.m23 - this.m22;
			double3 = this.m33 - this.m32;
			break;
		
		case 6: 
			double4 = this.m03 + this.m00;
			double5 = this.m13 + this.m10;
			double6 = this.m23 + this.m20;
			double1 = this.m33 + this.m30;
			double7 = this.m03 - this.m01;
			double8 = this.m13 - this.m11;
			double9 = this.m23 - this.m21;
			double2 = this.m33 - this.m31;
			double10 = this.m03 - this.m02;
			double11 = this.m13 - this.m12;
			double12 = this.m23 - this.m22;
			double3 = this.m33 - this.m32;
			break;
		
		case 7: 
			double4 = this.m03 - this.m00;
			double5 = this.m13 - this.m10;
			double6 = this.m23 - this.m20;
			double1 = this.m33 - this.m30;
			double7 = this.m03 - this.m01;
			double8 = this.m13 - this.m11;
			double9 = this.m23 - this.m21;
			double2 = this.m33 - this.m31;
			double10 = this.m03 - this.m02;
			double11 = this.m13 - this.m12;
			double12 = this.m23 - this.m22;
			double3 = this.m33 - this.m32;
			break;
		
		default: 
			throw new IllegalArgumentException("corner");
		
		}
		double double13 = double8 * double12 - double9 * double11;
		double double14 = double9 * double10 - double7 * double12;
		double double15 = double7 * double11 - double8 * double10;
		double double16 = double11 * double6 - double12 * double5;
		double double17 = double12 * double4 - double10 * double6;
		double double18 = double10 * double5 - double11 * double4;
		double double19 = double5 * double9 - double6 * double8;
		double double20 = double6 * double7 - double4 * double9;
		double double21 = double4 * double8 - double5 * double7;
		double double22 = 1.0 / (double4 * double13 + double5 * double14 + double6 * double15);
		vector3d.x = (-double13 * double1 - double16 * double2 - double19 * double3) * double22;
		vector3d.y = (-double14 * double1 - double17 * double2 - double20 * double3) * double22;
		vector3d.z = (-double15 * double1 - double18 * double2 - double21 * double3) * double22;
		return vector3d;
	}

	public Vector3d perspectiveOrigin(Vector3d vector3d) {
		double double1 = this.m03 + this.m00;
		double double2 = this.m13 + this.m10;
		double double3 = this.m23 + this.m20;
		double double4 = this.m33 + this.m30;
		double double5 = this.m03 - this.m00;
		double double6 = this.m13 - this.m10;
		double double7 = this.m23 - this.m20;
		double double8 = this.m33 - this.m30;
		double double9 = this.m03 - this.m01;
		double double10 = this.m13 - this.m11;
		double double11 = this.m23 - this.m21;
		double double12 = this.m33 - this.m31;
		double double13 = double6 * double11 - double7 * double10;
		double double14 = double7 * double9 - double5 * double11;
		double double15 = double5 * double10 - double6 * double9;
		double double16 = double10 * double3 - double11 * double2;
		double double17 = double11 * double1 - double9 * double3;
		double double18 = double9 * double2 - double10 * double1;
		double double19 = double2 * double7 - double3 * double6;
		double double20 = double3 * double5 - double1 * double7;
		double double21 = double1 * double6 - double2 * double5;
		double double22 = 1.0 / (double1 * double13 + double2 * double14 + double3 * double15);
		vector3d.x = (-double13 * double4 - double16 * double8 - double19 * double12) * double22;
		vector3d.y = (-double14 * double4 - double17 * double8 - double20 * double12) * double22;
		vector3d.z = (-double15 * double4 - double18 * double8 - double21 * double12) * double22;
		return vector3d;
	}

	public Vector3d perspectiveInvOrigin(Vector3d vector3d) {
		double double1 = 1.0 / this.m23;
		vector3d.x = this.m20 * double1;
		vector3d.y = this.m21 * double1;
		vector3d.z = this.m22 * double1;
		return vector3d;
	}

	public double perspectiveFov() {
		double double1 = this.m03 + this.m01;
		double double2 = this.m13 + this.m11;
		double double3 = this.m23 + this.m21;
		double double4 = this.m01 - this.m03;
		double double5 = this.m11 - this.m13;
		double double6 = this.m21 - this.m23;
		double double7 = Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = Math.sqrt(double4 * double4 + double5 * double5 + double6 * double6);
		return Math.acos((double1 * double4 + double2 * double5 + double3 * double6) / (double7 * double8));
	}

	public double perspectiveNear() {
		return this.m32 / (this.m23 + this.m22);
	}

	public double perspectiveFar() {
		return this.m32 / (this.m22 - this.m23);
	}

	public Vector3d frustumRayDir(double double1, double double2, Vector3d vector3d) {
		double double3 = this.m10 * this.m23;
		double double4 = this.m13 * this.m21;
		double double5 = this.m10 * this.m21;
		double double6 = this.m11 * this.m23;
		double double7 = this.m13 * this.m20;
		double double8 = this.m11 * this.m20;
		double double9 = this.m03 * this.m20;
		double double10 = this.m01 * this.m23;
		double double11 = this.m01 * this.m20;
		double double12 = this.m03 * this.m21;
		double double13 = this.m00 * this.m23;
		double double14 = this.m00 * this.m21;
		double double15 = this.m00 * this.m13;
		double double16 = this.m03 * this.m11;
		double double17 = this.m00 * this.m11;
		double double18 = this.m01 * this.m13;
		double double19 = this.m03 * this.m10;
		double double20 = this.m01 * this.m10;
		double double21 = (double6 + double7 + double8 - double3 - double4 - double5) * (1.0 - double2) + (double3 - double4 - double5 + double6 - double7 + double8) * double2;
		double double22 = (double12 + double13 + double14 - double9 - double10 - double11) * (1.0 - double2) + (double9 - double10 - double11 + double12 - double13 + double14) * double2;
		double double23 = (double18 + double19 + double20 - double15 - double16 - double17) * (1.0 - double2) + (double15 - double16 - double17 + double18 - double19 + double20) * double2;
		double double24 = (double4 - double5 - double6 + double7 + double8 - double3) * (1.0 - double2) + (double3 + double4 - double5 - double6 - double7 + double8) * double2;
		double double25 = (double10 - double11 - double12 + double13 + double14 - double9) * (1.0 - double2) + (double9 + double10 - double11 - double12 - double13 + double14) * double2;
		double double26 = (double16 - double17 - double18 + double19 + double20 - double15) * (1.0 - double2) + (double15 + double16 - double17 - double18 - double19 + double20) * double2;
		vector3d.x = double21 * (1.0 - double1) + double24 * double1;
		vector3d.y = double22 * (1.0 - double1) + double25 * double1;
		vector3d.z = double23 * (1.0 - double1) + double26 * double1;
		return vector3d.normalize(vector3d);
	}

	public Vector3d positiveZ(Vector3d vector3d) {
		return (this.properties & 16) != 0 ? this.normalizedPositiveZ(vector3d) : this.positiveZGeneric(vector3d);
	}

	private Vector3d positiveZGeneric(Vector3d vector3d) {
		return vector3d.set(this.m10 * this.m21 - this.m11 * this.m20, this.m20 * this.m01 - this.m21 * this.m00, this.m00 * this.m11 - this.m01 * this.m10).normalize();
	}

	public Vector3d normalizedPositiveZ(Vector3d vector3d) {
		return vector3d.set(this.m02, this.m12, this.m22);
	}

	public Vector3d positiveX(Vector3d vector3d) {
		return (this.properties & 16) != 0 ? this.normalizedPositiveX(vector3d) : this.positiveXGeneric(vector3d);
	}

	private Vector3d positiveXGeneric(Vector3d vector3d) {
		return vector3d.set(this.m11 * this.m22 - this.m12 * this.m21, this.m02 * this.m21 - this.m01 * this.m22, this.m01 * this.m12 - this.m02 * this.m11).normalize();
	}

	public Vector3d normalizedPositiveX(Vector3d vector3d) {
		return vector3d.set(this.m00, this.m10, this.m20);
	}

	public Vector3d positiveY(Vector3d vector3d) {
		return (this.properties & 16) != 0 ? this.normalizedPositiveY(vector3d) : this.positiveYGeneric(vector3d);
	}

	private Vector3d positiveYGeneric(Vector3d vector3d) {
		return vector3d.set(this.m12 * this.m20 - this.m10 * this.m22, this.m00 * this.m22 - this.m02 * this.m20, this.m02 * this.m10 - this.m00 * this.m12).normalize();
	}

	public Vector3d normalizedPositiveY(Vector3d vector3d) {
		return vector3d.set(this.m01, this.m11, this.m21);
	}

	public Vector3d originAffine(Vector3d vector3d) {
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

	public Vector3d origin(Vector3d vector3d) {
		return (this.properties & 2) != 0 ? this.originAffine(vector3d) : this.originGeneric(vector3d);
	}

	private Vector3d originGeneric(Vector3d vector3d) {
		double double1 = this.m00 * this.m11 - this.m01 * this.m10;
		double double2 = this.m00 * this.m12 - this.m02 * this.m10;
		double double3 = this.m00 * this.m13 - this.m03 * this.m10;
		double double4 = this.m01 * this.m12 - this.m02 * this.m11;
		double double5 = this.m01 * this.m13 - this.m03 * this.m11;
		double double6 = this.m02 * this.m13 - this.m03 * this.m12;
		double double7 = this.m20 * this.m31 - this.m21 * this.m30;
		double double8 = this.m20 * this.m32 - this.m22 * this.m30;
		double double9 = this.m20 * this.m33 - this.m23 * this.m30;
		double double10 = this.m21 * this.m32 - this.m22 * this.m31;
		double double11 = this.m21 * this.m33 - this.m23 * this.m31;
		double double12 = this.m22 * this.m33 - this.m23 * this.m32;
		double double13 = double1 * double12 - double2 * double11 + double3 * double10 + double4 * double9 - double5 * double8 + double6 * double7;
		double double14 = 1.0 / double13;
		double double15 = (-this.m10 * double10 + this.m11 * double8 - this.m12 * double7) * double14;
		double double16 = (this.m00 * double10 - this.m01 * double8 + this.m02 * double7) * double14;
		double double17 = (-this.m30 * double4 + this.m31 * double2 - this.m32 * double1) * double14;
		double double18 = double13 / (this.m20 * double4 - this.m21 * double2 + this.m22 * double1);
		double double19 = double15 * double18;
		double double20 = double16 * double18;
		double double21 = double17 * double18;
		return vector3d.set(double19, double20, double21);
	}

	public Matrix4d shadow(Vector4dc vector4dc, double double1, double double2, double double3, double double4) {
		return this.shadow(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4dc.w(), double1, double2, double3, double4, this);
	}

	public Matrix4d shadow(Vector4dc vector4dc, double double1, double double2, double double3, double double4, Matrix4d matrix4d) {
		return this.shadow(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4dc.w(), double1, double2, double3, double4, matrix4d);
	}

	public Matrix4d shadow(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		return this.shadow(double1, double2, double3, double4, double5, double6, double7, double8, this);
	}

	public Matrix4d shadow(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Matrix4d matrix4d) {
		double double9 = Math.invsqrt(double5 * double5 + double6 * double6 + double7 * double7);
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
		double double34 = this.m03 * double15 + this.m13 * double16 + this.m23 * double17 + this.m33 * double18;
		double double35 = this.m00 * double19 + this.m10 * double20 + this.m20 * double21 + this.m30 * double22;
		double double36 = this.m01 * double19 + this.m11 * double20 + this.m21 * double21 + this.m31 * double22;
		double double37 = this.m02 * double19 + this.m12 * double20 + this.m22 * double21 + this.m32 * double22;
		double double38 = this.m03 * double19 + this.m13 * double20 + this.m23 * double21 + this.m33 * double22;
		double double39 = this.m00 * double23 + this.m10 * double24 + this.m20 * double25 + this.m30 * double26;
		double double40 = this.m01 * double23 + this.m11 * double24 + this.m21 * double25 + this.m31 * double26;
		double double41 = this.m02 * double23 + this.m12 * double24 + this.m22 * double25 + this.m32 * double26;
		double double42 = this.m03 * double23 + this.m13 * double24 + this.m23 * double25 + this.m33 * double26;
		matrix4d._m30(this.m00 * double27 + this.m10 * double28 + this.m20 * double29 + this.m30 * double30)._m31(this.m01 * double27 + this.m11 * double28 + this.m21 * double29 + this.m31 * double30)._m32(this.m02 * double27 + this.m12 * double28 + this.m22 * double29 + this.m32 * double30)._m33(this.m03 * double27 + this.m13 * double28 + this.m23 * double29 + this.m33 * double30)._m00(double31)._m01(double32)._m02(double33)._m03(double34)._m10(double35)._m11(double36)._m12(double37)._m13(double38)._m20(double39)._m21(double40)._m22(double41)._m23(double42)._properties(this.properties & -30);
		return matrix4d;
	}

	public Matrix4d shadow(Vector4dc vector4dc, Matrix4dc matrix4dc, Matrix4d matrix4d) {
		double double1 = matrix4dc.m10();
		double double2 = matrix4dc.m11();
		double double3 = matrix4dc.m12();
		double double4 = -double1 * matrix4dc.m30() - double2 * matrix4dc.m31() - double3 * matrix4dc.m32();
		return this.shadow(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4dc.w(), double1, double2, double3, double4, matrix4d);
	}

	public Matrix4d shadow(Vector4d vector4d, Matrix4d matrix4d) {
		return this.shadow(vector4d, matrix4d, this);
	}

	public Matrix4d shadow(double double1, double double2, double double3, double double4, Matrix4dc matrix4dc, Matrix4d matrix4d) {
		double double5 = matrix4dc.m10();
		double double6 = matrix4dc.m11();
		double double7 = matrix4dc.m12();
		double double8 = -double5 * matrix4dc.m30() - double6 * matrix4dc.m31() - double7 * matrix4dc.m32();
		return this.shadow(double1, double2, double3, double4, double5, double6, double7, double8, matrix4d);
	}

	public Matrix4d shadow(double double1, double double2, double double3, double double4, Matrix4dc matrix4dc) {
		return this.shadow(double1, double2, double3, double4, matrix4dc, this);
	}

	public Matrix4d billboardCylindrical(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		double double1 = vector3dc2.x() - vector3dc.x();
		double double2 = vector3dc2.y() - vector3dc.y();
		double double3 = vector3dc2.z() - vector3dc.z();
		double double4 = vector3dc3.y() * double3 - vector3dc3.z() * double2;
		double double5 = vector3dc3.z() * double1 - vector3dc3.x() * double3;
		double double6 = vector3dc3.x() * double2 - vector3dc3.y() * double1;
		double double7 = Math.invsqrt(double4 * double4 + double5 * double5 + double6 * double6);
		double4 *= double7;
		double5 *= double7;
		double6 *= double7;
		double1 = double5 * vector3dc3.z() - double6 * vector3dc3.y();
		double2 = double6 * vector3dc3.x() - double4 * vector3dc3.z();
		double3 = double4 * vector3dc3.y() - double5 * vector3dc3.x();
		double double8 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double1 *= double8;
		double2 *= double8;
		double3 *= double8;
		this._m00(double4)._m01(double5)._m02(double6)._m03(0.0)._m10(vector3dc3.x())._m11(vector3dc3.y())._m12(vector3dc3.z())._m13(0.0)._m20(double1)._m21(double2)._m22(double3)._m23(0.0)._m30(vector3dc.x())._m31(vector3dc.y())._m32(vector3dc.z())._m33(1.0).properties = 18;
		return this;
	}

	public Matrix4d billboardSpherical(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		double double1 = vector3dc2.x() - vector3dc.x();
		double double2 = vector3dc2.y() - vector3dc.y();
		double double3 = vector3dc2.z() - vector3dc.z();
		double double4 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double1 *= double4;
		double2 *= double4;
		double3 *= double4;
		double double5 = vector3dc3.y() * double3 - vector3dc3.z() * double2;
		double double6 = vector3dc3.z() * double1 - vector3dc3.x() * double3;
		double double7 = vector3dc3.x() * double2 - vector3dc3.y() * double1;
		double double8 = Math.invsqrt(double5 * double5 + double6 * double6 + double7 * double7);
		double5 *= double8;
		double6 *= double8;
		double7 *= double8;
		double double9 = double2 * double7 - double3 * double6;
		double double10 = double3 * double5 - double1 * double7;
		double double11 = double1 * double6 - double2 * double5;
		this._m00(double5)._m01(double6)._m02(double7)._m03(0.0)._m10(double9)._m11(double10)._m12(double11)._m13(0.0)._m20(double1)._m21(double2)._m22(double3)._m23(0.0)._m30(vector3dc.x())._m31(vector3dc.y())._m32(vector3dc.z())._m33(1.0).properties = 18;
		return this;
	}

	public Matrix4d billboardSpherical(Vector3dc vector3dc, Vector3dc vector3dc2) {
		double double1 = vector3dc2.x() - vector3dc.x();
		double double2 = vector3dc2.y() - vector3dc.y();
		double double3 = vector3dc2.z() - vector3dc.z();
		double double4 = -double2;
		double double5 = Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3) + double3;
		double double6 = Math.invsqrt(double4 * double4 + double1 * double1 + double5 * double5);
		double4 *= double6;
		double double7 = double1 * double6;
		double5 *= double6;
		double double8 = (double4 + double4) * double4;
		double double9 = (double7 + double7) * double7;
		double double10 = (double4 + double4) * double7;
		double double11 = (double4 + double4) * double5;
		double double12 = (double7 + double7) * double5;
		this._m00(1.0 - double9)._m01(double10)._m02(-double12)._m03(0.0)._m10(double10)._m11(1.0 - double8)._m12(double11)._m13(0.0)._m20(double12)._m21(-double11)._m22(1.0 - double9 - double8)._m23(0.0)._m30(vector3dc.x())._m31(vector3dc.y())._m32(vector3dc.z())._m33(1.0).properties = 18;
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
		long1 = Double.doubleToLongBits(this.m03);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m10);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m11);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m12);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m13);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m20);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m21);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m22);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m23);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m30);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m31);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m32);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m33);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		return int1;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object == null) {
			return false;
		} else if (!(object instanceof Matrix4d)) {
			return false;
		} else {
			Matrix4d matrix4d = (Matrix4d)object;
			if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(matrix4d.m00)) {
				return false;
			} else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(matrix4d.m01)) {
				return false;
			} else if (Double.doubleToLongBits(this.m02) != Double.doubleToLongBits(matrix4d.m02)) {
				return false;
			} else if (Double.doubleToLongBits(this.m03) != Double.doubleToLongBits(matrix4d.m03)) {
				return false;
			} else if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(matrix4d.m10)) {
				return false;
			} else if (Double.doubleToLongBits(this.m11) != Double.doubleToLongBits(matrix4d.m11)) {
				return false;
			} else if (Double.doubleToLongBits(this.m12) != Double.doubleToLongBits(matrix4d.m12)) {
				return false;
			} else if (Double.doubleToLongBits(this.m13) != Double.doubleToLongBits(matrix4d.m13)) {
				return false;
			} else if (Double.doubleToLongBits(this.m20) != Double.doubleToLongBits(matrix4d.m20)) {
				return false;
			} else if (Double.doubleToLongBits(this.m21) != Double.doubleToLongBits(matrix4d.m21)) {
				return false;
			} else if (Double.doubleToLongBits(this.m22) != Double.doubleToLongBits(matrix4d.m22)) {
				return false;
			} else if (Double.doubleToLongBits(this.m23) != Double.doubleToLongBits(matrix4d.m23)) {
				return false;
			} else if (Double.doubleToLongBits(this.m30) != Double.doubleToLongBits(matrix4d.m30)) {
				return false;
			} else if (Double.doubleToLongBits(this.m31) != Double.doubleToLongBits(matrix4d.m31)) {
				return false;
			} else if (Double.doubleToLongBits(this.m32) != Double.doubleToLongBits(matrix4d.m32)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.m33) == Double.doubleToLongBits(matrix4d.m33);
			}
		}
	}

	public boolean equals(Matrix4dc matrix4dc, double double1) {
		if (this == matrix4dc) {
			return true;
		} else if (matrix4dc == null) {
			return false;
		} else if (!(matrix4dc instanceof Matrix4d)) {
			return false;
		} else if (!Runtime.equals(this.m00, matrix4dc.m00(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m01, matrix4dc.m01(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m02, matrix4dc.m02(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m03, matrix4dc.m03(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m10, matrix4dc.m10(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m11, matrix4dc.m11(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m12, matrix4dc.m12(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m13, matrix4dc.m13(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m20, matrix4dc.m20(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m21, matrix4dc.m21(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m22, matrix4dc.m22(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m23, matrix4dc.m23(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m30, matrix4dc.m30(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m31, matrix4dc.m31(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m32, matrix4dc.m32(), double1)) {
			return false;
		} else {
			return Runtime.equals(this.m33, matrix4dc.m33(), double1);
		}
	}

	public Matrix4d pick(double double1, double double2, double double3, double double4, int[] intArray, Matrix4d matrix4d) {
		double double5 = (double)intArray[2] / double3;
		double double6 = (double)intArray[3] / double4;
		double double7 = ((double)intArray[2] + 2.0 * ((double)intArray[0] - double1)) / double3;
		double double8 = ((double)intArray[3] + 2.0 * ((double)intArray[1] - double2)) / double4;
		matrix4d._m30(this.m00 * double7 + this.m10 * double8 + this.m30)._m31(this.m01 * double7 + this.m11 * double8 + this.m31)._m32(this.m02 * double7 + this.m12 * double8 + this.m32)._m33(this.m03 * double7 + this.m13 * double8 + this.m33)._m00(this.m00 * double5)._m01(this.m01 * double5)._m02(this.m02 * double5)._m03(this.m03 * double5)._m10(this.m10 * double6)._m11(this.m11 * double6)._m12(this.m12 * double6)._m13(this.m13 * double6)._properties(0);
		return matrix4d;
	}

	public Matrix4d pick(double double1, double double2, double double3, double double4, int[] intArray) {
		return this.pick(double1, double2, double3, double4, intArray, this);
	}

	public boolean isAffine() {
		return this.m03 == 0.0 && this.m13 == 0.0 && this.m23 == 0.0 && this.m33 == 1.0;
	}

	public Matrix4d swap(Matrix4d matrix4d) {
		double double1 = this.m00;
		this.m00 = matrix4d.m00;
		matrix4d.m00 = double1;
		double1 = this.m01;
		this.m01 = matrix4d.m01;
		matrix4d.m01 = double1;
		double1 = this.m02;
		this.m02 = matrix4d.m02;
		matrix4d.m02 = double1;
		double1 = this.m03;
		this.m03 = matrix4d.m03;
		matrix4d.m03 = double1;
		double1 = this.m10;
		this.m10 = matrix4d.m10;
		matrix4d.m10 = double1;
		double1 = this.m11;
		this.m11 = matrix4d.m11;
		matrix4d.m11 = double1;
		double1 = this.m12;
		this.m12 = matrix4d.m12;
		matrix4d.m12 = double1;
		double1 = this.m13;
		this.m13 = matrix4d.m13;
		matrix4d.m13 = double1;
		double1 = this.m20;
		this.m20 = matrix4d.m20;
		matrix4d.m20 = double1;
		double1 = this.m21;
		this.m21 = matrix4d.m21;
		matrix4d.m21 = double1;
		double1 = this.m22;
		this.m22 = matrix4d.m22;
		matrix4d.m22 = double1;
		double1 = this.m23;
		this.m23 = matrix4d.m23;
		matrix4d.m23 = double1;
		double1 = this.m30;
		this.m30 = matrix4d.m30;
		matrix4d.m30 = double1;
		double1 = this.m31;
		this.m31 = matrix4d.m31;
		matrix4d.m31 = double1;
		double1 = this.m32;
		this.m32 = matrix4d.m32;
		matrix4d.m32 = double1;
		double1 = this.m33;
		this.m33 = matrix4d.m33;
		matrix4d.m33 = double1;
		int int1 = this.properties;
		this.properties = matrix4d.properties;
		matrix4d.properties = int1;
		return this;
	}

	public Matrix4d arcball(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
		double double7 = this.m20 * -double1 + this.m30;
		double double8 = this.m21 * -double1 + this.m31;
		double double9 = this.m22 * -double1 + this.m32;
		double double10 = this.m23 * -double1 + this.m33;
		double double11 = Math.sin(double5);
		double double12 = Math.cosFromSin(double11, double5);
		double double13 = this.m10 * double12 + this.m20 * double11;
		double double14 = this.m11 * double12 + this.m21 * double11;
		double double15 = this.m12 * double12 + this.m22 * double11;
		double double16 = this.m13 * double12 + this.m23 * double11;
		double double17 = this.m20 * double12 - this.m10 * double11;
		double double18 = this.m21 * double12 - this.m11 * double11;
		double double19 = this.m22 * double12 - this.m12 * double11;
		double double20 = this.m23 * double12 - this.m13 * double11;
		double11 = Math.sin(double6);
		double12 = Math.cosFromSin(double11, double6);
		double double21 = this.m00 * double12 - double17 * double11;
		double double22 = this.m01 * double12 - double18 * double11;
		double double23 = this.m02 * double12 - double19 * double11;
		double double24 = this.m03 * double12 - double20 * double11;
		double double25 = this.m00 * double11 + double17 * double12;
		double double26 = this.m01 * double11 + double18 * double12;
		double double27 = this.m02 * double11 + double19 * double12;
		double double28 = this.m03 * double11 + double20 * double12;
		matrix4d._m30(-double21 * double2 - double13 * double3 - double25 * double4 + double7)._m31(-double22 * double2 - double14 * double3 - double26 * double4 + double8)._m32(-double23 * double2 - double15 * double3 - double27 * double4 + double9)._m33(-double24 * double2 - double16 * double3 - double28 * double4 + double10)._m20(double25)._m21(double26)._m22(double27)._m23(double28)._m10(double13)._m11(double14)._m12(double15)._m13(double16)._m00(double21)._m01(double22)._m02(double23)._m03(double24)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d arcball(double double1, Vector3dc vector3dc, double double2, double double3, Matrix4d matrix4d) {
		return this.arcball(double1, vector3dc.x(), vector3dc.y(), vector3dc.z(), double2, double3, matrix4d);
	}

	public Matrix4d arcball(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.arcball(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4d arcball(double double1, Vector3dc vector3dc, double double2, double double3) {
		return this.arcball(double1, vector3dc.x(), vector3dc.y(), vector3dc.z(), double2, double3, this);
	}

	public Matrix4d frustumAabb(Vector3d vector3d, Vector3d vector3d2) {
		double double1 = Double.POSITIVE_INFINITY;
		double double2 = Double.POSITIVE_INFINITY;
		double double3 = Double.POSITIVE_INFINITY;
		double double4 = Double.NEGATIVE_INFINITY;
		double double5 = Double.NEGATIVE_INFINITY;
		double double6 = Double.NEGATIVE_INFINITY;
		for (int int1 = 0; int1 < 8; ++int1) {
			double double7 = (double)((int1 & 1) << 1) - 1.0;
			double double8 = (double)((int1 >>> 1 & 1) << 1) - 1.0;
			double double9 = (double)((int1 >>> 2 & 1) << 1) - 1.0;
			double double10 = 1.0 / (this.m03 * double7 + this.m13 * double8 + this.m23 * double9 + this.m33);
			double double11 = (this.m00 * double7 + this.m10 * double8 + this.m20 * double9 + this.m30) * double10;
			double double12 = (this.m01 * double7 + this.m11 * double8 + this.m21 * double9 + this.m31) * double10;
			double double13 = (this.m02 * double7 + this.m12 * double8 + this.m22 * double9 + this.m32) * double10;
			double1 = double1 < double11 ? double1 : double11;
			double2 = double2 < double12 ? double2 : double12;
			double3 = double3 < double13 ? double3 : double13;
			double4 = double4 > double11 ? double4 : double11;
			double5 = double5 > double12 ? double5 : double12;
			double6 = double6 > double13 ? double6 : double13;
		}

		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		vector3d2.x = double4;
		vector3d2.y = double5;
		vector3d2.z = double6;
		return this;
	}

	public Matrix4d projectedGridRange(Matrix4dc matrix4dc, double double1, double double2, Matrix4d matrix4d) {
		double double3 = Double.POSITIVE_INFINITY;
		double double4 = Double.POSITIVE_INFINITY;
		double double5 = Double.NEGATIVE_INFINITY;
		double double6 = Double.NEGATIVE_INFINITY;
		boolean boolean1 = false;
		for (int int1 = 0; int1 < 12; ++int1) {
			double double7;
			double double8;
			double double9;
			double double10;
			double double11;
			double double12;
			if (int1 < 4) {
				double7 = -1.0;
				double10 = 1.0;
				double8 = double11 = (double)((int1 & 1) << 1) - 1.0;
				double9 = double12 = (double)((int1 >>> 1 & 1) << 1) - 1.0;
			} else if (int1 < 8) {
				double8 = -1.0;
				double11 = 1.0;
				double7 = double10 = (double)((int1 & 1) << 1) - 1.0;
				double9 = double12 = (double)((int1 >>> 1 & 1) << 1) - 1.0;
			} else {
				double9 = -1.0;
				double12 = 1.0;
				double7 = double10 = (double)((int1 & 1) << 1) - 1.0;
				double8 = double11 = (double)((int1 >>> 1 & 1) << 1) - 1.0;
			}

			double double13 = 1.0 / (this.m03 * double7 + this.m13 * double8 + this.m23 * double9 + this.m33);
			double double14 = (this.m00 * double7 + this.m10 * double8 + this.m20 * double9 + this.m30) * double13;
			double double15 = (this.m01 * double7 + this.m11 * double8 + this.m21 * double9 + this.m31) * double13;
			double double16 = (this.m02 * double7 + this.m12 * double8 + this.m22 * double9 + this.m32) * double13;
			double13 = 1.0 / (this.m03 * double10 + this.m13 * double11 + this.m23 * double12 + this.m33);
			double double17 = (this.m00 * double10 + this.m10 * double11 + this.m20 * double12 + this.m30) * double13;
			double double18 = (this.m01 * double10 + this.m11 * double11 + this.m21 * double12 + this.m31) * double13;
			double double19 = (this.m02 * double10 + this.m12 * double11 + this.m22 * double12 + this.m32) * double13;
			double double20 = double17 - double14;
			double double21 = double18 - double15;
			double double22 = double19 - double16;
			double double23 = 1.0 / double21;
			for (int int2 = 0; int2 < 2; ++int2) {
				double double24 = -(double15 + (int2 == 0 ? double1 : double2)) * double23;
				if (double24 >= 0.0 && double24 <= 1.0) {
					boolean1 = true;
					double double25 = double14 + double24 * double20;
					double double26 = double16 + double24 * double22;
					double13 = 1.0 / (matrix4dc.m03() * double25 + matrix4dc.m23() * double26 + matrix4dc.m33());
					double double27 = (matrix4dc.m00() * double25 + matrix4dc.m20() * double26 + matrix4dc.m30()) * double13;
					double double28 = (matrix4dc.m01() * double25 + matrix4dc.m21() * double26 + matrix4dc.m31()) * double13;
					double3 = double3 < double27 ? double3 : double27;
					double4 = double4 < double28 ? double4 : double28;
					double5 = double5 > double27 ? double5 : double27;
					double6 = double6 > double28 ? double6 : double28;
				}
			}
		}

		if (!boolean1) {
			return null;
		} else {
			matrix4d.set(double5 - double3, 0.0, 0.0, 0.0, 0.0, double6 - double4, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, double3, double4, 0.0, 1.0)._properties(2);
			return matrix4d;
		}
	}

	public Matrix4d perspectiveFrustumSlice(double double1, double double2, Matrix4d matrix4d) {
		double double3 = (this.m23 + this.m22) / this.m32;
		double double4 = 1.0 / (double1 - double2);
		matrix4d._m00(this.m00 * double3 * double1)._m01(this.m01)._m02(this.m02)._m03(this.m03)._m10(this.m10)._m11(this.m11 * double3 * double1)._m12(this.m12)._m13(this.m13)._m20(this.m20)._m21(this.m21)._m22((double2 + double1) * double4)._m23(this.m23)._m30(this.m30)._m31(this.m31)._m32((double2 + double2) * double1 * double4)._m33(this.m33)._properties(this.properties & -29);
		return matrix4d;
	}

	public Matrix4d orthoCrop(Matrix4dc matrix4dc, Matrix4d matrix4d) {
		double double1 = Double.POSITIVE_INFINITY;
		double double2 = Double.NEGATIVE_INFINITY;
		double double3 = Double.POSITIVE_INFINITY;
		double double4 = Double.NEGATIVE_INFINITY;
		double double5 = Double.POSITIVE_INFINITY;
		double double6 = Double.NEGATIVE_INFINITY;
		for (int int1 = 0; int1 < 8; ++int1) {
			double double7 = (double)((int1 & 1) << 1) - 1.0;
			double double8 = (double)((int1 >>> 1 & 1) << 1) - 1.0;
			double double9 = (double)((int1 >>> 2 & 1) << 1) - 1.0;
			double double10 = 1.0 / (this.m03 * double7 + this.m13 * double8 + this.m23 * double9 + this.m33);
			double double11 = (this.m00 * double7 + this.m10 * double8 + this.m20 * double9 + this.m30) * double10;
			double double12 = (this.m01 * double7 + this.m11 * double8 + this.m21 * double9 + this.m31) * double10;
			double double13 = (this.m02 * double7 + this.m12 * double8 + this.m22 * double9 + this.m32) * double10;
			double10 = 1.0 / (matrix4dc.m03() * double11 + matrix4dc.m13() * double12 + matrix4dc.m23() * double13 + matrix4dc.m33());
			double double14 = matrix4dc.m00() * double11 + matrix4dc.m10() * double12 + matrix4dc.m20() * double13 + matrix4dc.m30();
			double double15 = matrix4dc.m01() * double11 + matrix4dc.m11() * double12 + matrix4dc.m21() * double13 + matrix4dc.m31();
			double double16 = (matrix4dc.m02() * double11 + matrix4dc.m12() * double12 + matrix4dc.m22() * double13 + matrix4dc.m32()) * double10;
			double1 = double1 < double14 ? double1 : double14;
			double2 = double2 > double14 ? double2 : double14;
			double3 = double3 < double15 ? double3 : double15;
			double4 = double4 > double15 ? double4 : double15;
			double5 = double5 < double16 ? double5 : double16;
			double6 = double6 > double16 ? double6 : double16;
		}

		return matrix4d.setOrtho(double1, double2, double3, double4, -double6, -double5);
	}

	public Matrix4d trapezoidCrop(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		double double9 = double4 - double2;
		double double10 = double1 - double3;
		double double11 = -double9;
		double double12 = double9 * double2 - double10 * double1;
		double double13 = -(double9 * double1 + double10 * double2);
		double double14 = double10 * double7 + double11 * double8 + double12;
		double double15 = double9 * double7 + double10 * double8 + double13;
		double double16 = -double14 / double15;
		double double17 = double10 + double16 * double9;
		double11 += double16 * double10;
		double12 += double16 * double13;
		double double18 = double17 * double3 + double11 * double4 + double12;
		double double19 = double17 * double5 + double11 * double6 + double12;
		double double20 = double18 * double15 / (double19 - double18);
		double13 += double20;
		double double21 = 2.0 / double19;
		double double22 = 1.0 / (double15 + double20);
		double double23 = (double22 + double22) * double20 / (1.0 - double22 * double20);
		double double24 = double9 * double22;
		double double25 = double10 * double22;
		double double26 = double13 * double22;
		double double27 = (double23 + 1.0) * double24;
		double double28 = (double23 + 1.0) * double25;
		double13 = (double23 + 1.0) * double26 - double23;
		double17 = double21 * double17 - double24;
		double11 = double21 * double11 - double25;
		double12 = double21 * double12 - double26;
		this.set(double17, double27, 0.0, double24, double11, double28, 0.0, double25, 0.0, 0.0, 1.0, 0.0, double12, double13, 0.0, double26);
		this.properties = 0;
		return this;
	}

	public Matrix4d transformAab(double double1, double double2, double double3, double double4, double double5, double double6, Vector3d vector3d, Vector3d vector3d2) {
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

	public Matrix4d transformAab(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3d vector3d, Vector3d vector3d2) {
		return this.transformAab(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3d, vector3d2);
	}

	public Matrix4d lerp(Matrix4dc matrix4dc, double double1) {
		return this.lerp(matrix4dc, double1, this);
	}

	public Matrix4d lerp(Matrix4dc matrix4dc, double double1, Matrix4d matrix4d) {
		matrix4d._m00(Math.fma(matrix4dc.m00() - this.m00, double1, this.m00))._m01(Math.fma(matrix4dc.m01() - this.m01, double1, this.m01))._m02(Math.fma(matrix4dc.m02() - this.m02, double1, this.m02))._m03(Math.fma(matrix4dc.m03() - this.m03, double1, this.m03))._m10(Math.fma(matrix4dc.m10() - this.m10, double1, this.m10))._m11(Math.fma(matrix4dc.m11() - this.m11, double1, this.m11))._m12(Math.fma(matrix4dc.m12() - this.m12, double1, this.m12))._m13(Math.fma(matrix4dc.m13() - this.m13, double1, this.m13))._m20(Math.fma(matrix4dc.m20() - this.m20, double1, this.m20))._m21(Math.fma(matrix4dc.m21() - this.m21, double1, this.m21))._m22(Math.fma(matrix4dc.m22() - this.m22, double1, this.m22))._m23(Math.fma(matrix4dc.m23() - this.m23, double1, this.m23))._m30(Math.fma(matrix4dc.m30() - this.m30, double1, this.m30))._m31(Math.fma(matrix4dc.m31() - this.m31, double1, this.m31))._m32(Math.fma(matrix4dc.m32() - this.m32, double1, this.m32))._m33(Math.fma(matrix4dc.m33() - this.m33, double1, this.m33))._properties(this.properties & matrix4dc.properties());
		return matrix4d;
	}

	public Matrix4d rotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4d matrix4d) {
		return this.rotateTowards(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), matrix4d);
	}

	public Matrix4d rotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.rotateTowards(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), this);
	}

	public Matrix4d rotateTowards(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.rotateTowards(double1, double2, double3, double4, double5, double6, this);
	}

	public Matrix4d rotateTowards(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d) {
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
		double double21 = this.m03 * double11 + this.m13 * double12 + this.m23 * double13;
		double double22 = this.m00 * double15 + this.m10 * double16 + this.m20 * double17;
		double double23 = this.m01 * double15 + this.m11 * double16 + this.m21 * double17;
		double double24 = this.m02 * double15 + this.m12 * double16 + this.m22 * double17;
		double double25 = this.m03 * double15 + this.m13 * double16 + this.m23 * double17;
		matrix4d._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._m20(this.m00 * double8 + this.m10 * double9 + this.m20 * double10)._m21(this.m01 * double8 + this.m11 * double9 + this.m21 * double10)._m22(this.m02 * double8 + this.m12 * double9 + this.m22 * double10)._m23(this.m03 * double8 + this.m13 * double9 + this.m23 * double10)._m00(double18)._m01(double19)._m02(double20)._m03(double21)._m10(double22)._m11(double23)._m12(double24)._m13(double25)._properties(this.properties & -14);
		return matrix4d;
	}

	public Matrix4d rotationTowards(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.rotationTowards(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Matrix4d rotationTowards(double double1, double double2, double double3, double double4, double double5, double double6) {
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
		if ((this.properties & 4) == 0) {
			this._identity();
		}

		this.m00 = double11;
		this.m01 = double12;
		this.m02 = double13;
		this.m10 = double15;
		this.m11 = double16;
		this.m12 = double17;
		this.m20 = double8;
		this.m21 = double9;
		this.m22 = double10;
		this.properties = 18;
		return this;
	}

	public Matrix4d translationRotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3) {
		return this.translationRotateTowards(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z());
	}

	public Matrix4d translationRotateTowards(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = Math.invsqrt(double4 * double4 + double5 * double5 + double6 * double6);
		double double11 = double4 * double10;
		double double12 = double5 * double10;
		double double13 = double6 * double10;
		double double14 = double8 * double13 - double9 * double12;
		double double15 = double9 * double11 - double7 * double13;
		double double16 = double7 * double12 - double8 * double11;
		double double17 = Math.invsqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double17;
		double15 *= double17;
		double16 *= double17;
		double double18 = double12 * double16 - double13 * double15;
		double double19 = double13 * double14 - double11 * double16;
		double double20 = double11 * double15 - double12 * double14;
		this.m00 = double14;
		this.m01 = double15;
		this.m02 = double16;
		this.m03 = 0.0;
		this.m10 = double18;
		this.m11 = double19;
		this.m12 = double20;
		this.m13 = 0.0;
		this.m20 = double11;
		this.m21 = double12;
		this.m22 = double13;
		this.m23 = 0.0;
		this.m30 = double1;
		this.m31 = double2;
		this.m32 = double3;
		this.m33 = 1.0;
		this.properties = 18;
		return this;
	}

	public Vector3d getEulerAnglesZYX(Vector3d vector3d) {
		vector3d.x = Math.atan2(this.m12, this.m22);
		vector3d.y = Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22));
		vector3d.z = Math.atan2(this.m01, this.m00);
		return vector3d;
	}

	public Matrix4d affineSpan(Vector3d vector3d, Vector3d vector3d2, Vector3d vector3d3, Vector3d vector3d4) {
		double double1 = this.m10 * this.m22;
		double double2 = this.m10 * this.m21;
		double double3 = this.m10 * this.m02;
		double double4 = this.m10 * this.m01;
		double double5 = this.m11 * this.m22;
		double double6 = this.m11 * this.m20;
		double double7 = this.m11 * this.m02;
		double double8 = this.m11 * this.m00;
		double double9 = this.m12 * this.m21;
		double double10 = this.m12 * this.m20;
		double double11 = this.m12 * this.m01;
		double double12 = this.m12 * this.m00;
		double double13 = this.m20 * this.m02;
		double double14 = this.m20 * this.m01;
		double double15 = this.m21 * this.m02;
		double double16 = this.m21 * this.m00;
		double double17 = this.m22 * this.m01;
		double double18 = this.m22 * this.m00;
		double double19 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
		double double20 = (double5 - double9) * double19;
		double double21 = (double15 - double17) * double19;
		double double22 = (double11 - double7) * double19;
		double double23 = (double10 - double1) * double19;
		double double24 = (double18 - double13) * double19;
		double double25 = (double3 - double12) * double19;
		double double26 = (double2 - double6) * double19;
		double double27 = (double14 - double16) * double19;
		double double28 = (double8 - double4) * double19;
		vector3d.x = -double20 - double23 - double26 + (double1 * this.m31 - double2 * this.m32 + double6 * this.m32 - double5 * this.m30 + double9 * this.m30 - double10 * this.m31) * double19;
		vector3d.y = -double21 - double24 - double27 + (double13 * this.m31 - double14 * this.m32 + double16 * this.m32 - double15 * this.m30 + double17 * this.m30 - double18 * this.m31) * double19;
		vector3d.z = -double22 - double25 - double28 + (double7 * this.m30 - double11 * this.m30 + double12 * this.m31 - double3 * this.m31 + double4 * this.m32 - double8 * this.m32) * double19;
		vector3d2.x = 2.0 * double20;
		vector3d2.y = 2.0 * double21;
		vector3d2.z = 2.0 * double22;
		vector3d3.x = 2.0 * double23;
		vector3d3.y = 2.0 * double24;
		vector3d3.z = 2.0 * double25;
		vector3d4.x = 2.0 * double26;
		vector3d4.y = 2.0 * double27;
		vector3d4.z = 2.0 * double28;
		return this;
	}

	public boolean testPoint(double double1, double double2, double double3) {
		double double4 = this.m03 + this.m00;
		double double5 = this.m13 + this.m10;
		double double6 = this.m23 + this.m20;
		double double7 = this.m33 + this.m30;
		double double8 = this.m03 - this.m00;
		double double9 = this.m13 - this.m10;
		double double10 = this.m23 - this.m20;
		double double11 = this.m33 - this.m30;
		double double12 = this.m03 + this.m01;
		double double13 = this.m13 + this.m11;
		double double14 = this.m23 + this.m21;
		double double15 = this.m33 + this.m31;
		double double16 = this.m03 - this.m01;
		double double17 = this.m13 - this.m11;
		double double18 = this.m23 - this.m21;
		double double19 = this.m33 - this.m31;
		double double20 = this.m03 + this.m02;
		double double21 = this.m13 + this.m12;
		double double22 = this.m23 + this.m22;
		double double23 = this.m33 + this.m32;
		double double24 = this.m03 - this.m02;
		double double25 = this.m13 - this.m12;
		double double26 = this.m23 - this.m22;
		double double27 = this.m33 - this.m32;
		return double4 * double1 + double5 * double2 + double6 * double3 + double7 >= 0.0 && double8 * double1 + double9 * double2 + double10 * double3 + double11 >= 0.0 && double12 * double1 + double13 * double2 + double14 * double3 + double15 >= 0.0 && double16 * double1 + double17 * double2 + double18 * double3 + double19 >= 0.0 && double20 * double1 + double21 * double2 + double22 * double3 + double23 >= 0.0 && double24 * double1 + double25 * double2 + double26 * double3 + double27 >= 0.0;
	}

	public boolean testSphere(double double1, double double2, double double3, double double4) {
		double double5 = this.m03 + this.m00;
		double double6 = this.m13 + this.m10;
		double double7 = this.m23 + this.m20;
		double double8 = this.m33 + this.m30;
		double double9 = Math.invsqrt(double5 * double5 + double6 * double6 + double7 * double7);
		double5 *= double9;
		double6 *= double9;
		double7 *= double9;
		double8 *= double9;
		double double10 = this.m03 - this.m00;
		double double11 = this.m13 - this.m10;
		double double12 = this.m23 - this.m20;
		double double13 = this.m33 - this.m30;
		double9 = Math.invsqrt(double10 * double10 + double11 * double11 + double12 * double12);
		double10 *= double9;
		double11 *= double9;
		double12 *= double9;
		double13 *= double9;
		double double14 = this.m03 + this.m01;
		double double15 = this.m13 + this.m11;
		double double16 = this.m23 + this.m21;
		double double17 = this.m33 + this.m31;
		double9 = Math.invsqrt(double14 * double14 + double15 * double15 + double16 * double16);
		double14 *= double9;
		double15 *= double9;
		double16 *= double9;
		double17 *= double9;
		double double18 = this.m03 - this.m01;
		double double19 = this.m13 - this.m11;
		double double20 = this.m23 - this.m21;
		double double21 = this.m33 - this.m31;
		double9 = Math.invsqrt(double18 * double18 + double19 * double19 + double20 * double20);
		double18 *= double9;
		double19 *= double9;
		double20 *= double9;
		double21 *= double9;
		double double22 = this.m03 + this.m02;
		double double23 = this.m13 + this.m12;
		double double24 = this.m23 + this.m22;
		double double25 = this.m33 + this.m32;
		double9 = Math.invsqrt(double22 * double22 + double23 * double23 + double24 * double24);
		double22 *= double9;
		double23 *= double9;
		double24 *= double9;
		double25 *= double9;
		double double26 = this.m03 - this.m02;
		double double27 = this.m13 - this.m12;
		double double28 = this.m23 - this.m22;
		double double29 = this.m33 - this.m32;
		double9 = Math.invsqrt(double26 * double26 + double27 * double27 + double28 * double28);
		double26 *= double9;
		double27 *= double9;
		double28 *= double9;
		double29 *= double9;
		return double5 * double1 + double6 * double2 + double7 * double3 + double8 >= -double4 && double10 * double1 + double11 * double2 + double12 * double3 + double13 >= -double4 && double14 * double1 + double15 * double2 + double16 * double3 + double17 >= -double4 && double18 * double1 + double19 * double2 + double20 * double3 + double21 >= -double4 && double22 * double1 + double23 * double2 + double24 * double3 + double25 >= -double4 && double26 * double1 + double27 * double2 + double28 * double3 + double29 >= -double4;
	}

	public boolean testAab(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = this.m03 + this.m00;
		double double8 = this.m13 + this.m10;
		double double9 = this.m23 + this.m20;
		double double10 = this.m33 + this.m30;
		double double11 = this.m03 - this.m00;
		double double12 = this.m13 - this.m10;
		double double13 = this.m23 - this.m20;
		double double14 = this.m33 - this.m30;
		double double15 = this.m03 + this.m01;
		double double16 = this.m13 + this.m11;
		double double17 = this.m23 + this.m21;
		double double18 = this.m33 + this.m31;
		double double19 = this.m03 - this.m01;
		double double20 = this.m13 - this.m11;
		double double21 = this.m23 - this.m21;
		double double22 = this.m33 - this.m31;
		double double23 = this.m03 + this.m02;
		double double24 = this.m13 + this.m12;
		double double25 = this.m23 + this.m22;
		double double26 = this.m33 + this.m32;
		double double27 = this.m03 - this.m02;
		double double28 = this.m13 - this.m12;
		double double29 = this.m23 - this.m22;
		double double30 = this.m33 - this.m32;
		return double7 * (double7 < 0.0 ? double1 : double4) + double8 * (double8 < 0.0 ? double2 : double5) + double9 * (double9 < 0.0 ? double3 : double6) >= -double10 && double11 * (double11 < 0.0 ? double1 : double4) + double12 * (double12 < 0.0 ? double2 : double5) + double13 * (double13 < 0.0 ? double3 : double6) >= -double14 && double15 * (double15 < 0.0 ? double1 : double4) + double16 * (double16 < 0.0 ? double2 : double5) + double17 * (double17 < 0.0 ? double3 : double6) >= -double18 && double19 * (double19 < 0.0 ? double1 : double4) + double20 * (double20 < 0.0 ? double2 : double5) + double21 * (double21 < 0.0 ? double3 : double6) >= -double22 && double23 * (double23 < 0.0 ? double1 : double4) + double24 * (double24 < 0.0 ? double2 : double5) + double25 * (double25 < 0.0 ? double3 : double6) >= -double26 && double27 * (double27 < 0.0 ? double1 : double4) + double28 * (double28 < 0.0 ? double2 : double5) + double29 * (double29 < 0.0 ? double3 : double6) >= -double30;
	}

	public Matrix4d obliqueZ(double double1, double double2) {
		this.m20 += this.m00 * double1 + this.m10 * double2;
		this.m21 += this.m01 * double1 + this.m11 * double2;
		this.m22 += this.m02 * double1 + this.m12 * double2;
		this.properties &= 2;
		return this;
	}

	public Matrix4d obliqueZ(double double1, double double2, Matrix4d matrix4d) {
		matrix4d._m00(this.m00)._m01(this.m01)._m02(this.m02)._m03(this.m03)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m13(this.m13)._m20(this.m00 * double1 + this.m10 * double2 + this.m20)._m21(this.m01 * double1 + this.m11 * double2 + this.m21)._m22(this.m02 * double1 + this.m12 * double2 + this.m22)._m23(this.m23)._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33)._properties(this.properties & 2);
		return matrix4d;
	}

	public static void projViewFromRectangle(Vector3d vector3d, Vector3d vector3d2, Vector3d vector3d3, Vector3d vector3d4, double double1, boolean boolean1, Matrix4d matrix4d, Matrix4d matrix4d2) {
		double double2 = vector3d4.y * vector3d3.z - vector3d4.z * vector3d3.y;
		double double3 = vector3d4.z * vector3d3.x - vector3d4.x * vector3d3.z;
		double double4 = vector3d4.x * vector3d3.y - vector3d4.y * vector3d3.x;
		double double5 = double2 * (vector3d2.x - vector3d.x) + double3 * (vector3d2.y - vector3d.y) + double4 * (vector3d2.z - vector3d.z);
		double double6 = double5 >= 0.0 ? 1.0 : -1.0;
		double2 *= double6;
		double3 *= double6;
		double4 *= double6;
		double5 *= double6;
		matrix4d2.setLookAt(vector3d.x, vector3d.y, vector3d.z, vector3d.x + double2, vector3d.y + double3, vector3d.z + double4, vector3d4.x, vector3d4.y, vector3d4.z);
		double double7 = matrix4d2.m00 * vector3d2.x + matrix4d2.m10 * vector3d2.y + matrix4d2.m20 * vector3d2.z + matrix4d2.m30;
		double double8 = matrix4d2.m01 * vector3d2.x + matrix4d2.m11 * vector3d2.y + matrix4d2.m21 * vector3d2.z + matrix4d2.m31;
		double double9 = matrix4d2.m00 * vector3d3.x + matrix4d2.m10 * vector3d3.y + matrix4d2.m20 * vector3d3.z;
		double double10 = matrix4d2.m01 * vector3d4.x + matrix4d2.m11 * vector3d4.y + matrix4d2.m21 * vector3d4.z;
		double double11 = Math.sqrt(double2 * double2 + double3 * double3 + double4 * double4);
		double double12 = double5 / double11;
		double double13;
		if (Double.isInfinite(double1) && double1 < 0.0) {
			double13 = double12;
			double12 = Double.POSITIVE_INFINITY;
		} else if (Double.isInfinite(double1) && double1 > 0.0) {
			double13 = Double.POSITIVE_INFINITY;
		} else if (double1 < 0.0) {
			double13 = double12;
			double12 += double1;
		} else {
			double13 = double12 + double1;
		}

		matrix4d.setFrustum(double7, double7 + double9, double8, double8 + double10, double12, double13, boolean1);
	}

	public Matrix4d withLookAtUp(Vector3dc vector3dc) {
		return this.withLookAtUp(vector3dc.x(), vector3dc.y(), vector3dc.z(), this);
	}

	public Matrix4d withLookAtUp(Vector3dc vector3dc, Matrix4d matrix4d) {
		return this.withLookAtUp(vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public Matrix4d withLookAtUp(double double1, double double2, double double3) {
		return this.withLookAtUp(double1, double2, double3, this);
	}

	public Matrix4d withLookAtUp(double double1, double double2, double double3, Matrix4d matrix4d) {
		double double4 = (double2 * this.m21 - double3 * this.m11) * this.m02 + (double3 * this.m01 - double1 * this.m21) * this.m12 + (double1 * this.m11 - double2 * this.m01) * this.m22;
		double double5 = double1 * this.m01 + double2 * this.m11 + double3 * this.m21;
		if ((this.properties & 16) == 0) {
			double5 *= Math.sqrt(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21);
		}

		double double6 = Math.invsqrt(double4 * double4 + double5 * double5);
		double double7 = double5 * double6;
		double double8 = double4 * double6;
		double double9 = double7 * this.m00 - double8 * this.m01;
		double double10 = double7 * this.m10 - double8 * this.m11;
		double double11 = double7 * this.m20 - double8 * this.m21;
		double double12 = double8 * this.m30 + double7 * this.m31;
		double double13 = double8 * this.m00 + double7 * this.m01;
		double double14 = double8 * this.m10 + double7 * this.m11;
		double double15 = double8 * this.m20 + double7 * this.m21;
		double double16 = double7 * this.m30 - double8 * this.m31;
		matrix4d._m00(double9)._m10(double10)._m20(double11)._m30(double16)._m01(double13)._m11(double14)._m21(double15)._m31(double12);
		if (matrix4d != this) {
			matrix4d._m02(this.m02)._m12(this.m12)._m22(this.m22)._m32(this.m32)._m03(this.m03)._m13(this.m13)._m23(this.m23)._m33(this.m33);
		}

		matrix4d._properties(this.properties & -14);
		return matrix4d;
	}

	public boolean isFinite() {
		return Math.isFinite(this.m00) && Math.isFinite(this.m01) && Math.isFinite(this.m02) && Math.isFinite(this.m03) && Math.isFinite(this.m10) && Math.isFinite(this.m11) && Math.isFinite(this.m12) && Math.isFinite(this.m13) && Math.isFinite(this.m20) && Math.isFinite(this.m21) && Math.isFinite(this.m22) && Math.isFinite(this.m23) && Math.isFinite(this.m30) && Math.isFinite(this.m31) && Math.isFinite(this.m32) && Math.isFinite(this.m33);
	}
}
