package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Matrix3x2d implements Matrix3x2dc,Externalizable {
	private static final long serialVersionUID = 1L;
	public double m00;
	public double m01;
	public double m10;
	public double m11;
	public double m20;
	public double m21;

	public Matrix3x2d() {
		this.m00 = 1.0;
		this.m11 = 1.0;
	}

	public Matrix3x2d(Matrix2dc matrix2dc) {
		if (matrix2dc instanceof Matrix2d) {
			MemUtil.INSTANCE.copy((Matrix2d)matrix2dc, this);
		} else {
			this.setMatrix2dc(matrix2dc);
		}
	}

	public Matrix3x2d(Matrix2fc matrix2fc) {
		this.m00 = (double)matrix2fc.m00();
		this.m01 = (double)matrix2fc.m01();
		this.m10 = (double)matrix2fc.m10();
		this.m11 = (double)matrix2fc.m11();
	}

	public Matrix3x2d(Matrix3x2dc matrix3x2dc) {
		if (matrix3x2dc instanceof Matrix3x2d) {
			MemUtil.INSTANCE.copy((Matrix3x2d)matrix3x2dc, this);
		} else {
			this.setMatrix3x2dc(matrix3x2dc);
		}
	}

	public Matrix3x2d(double double1, double double2, double double3, double double4, double double5, double double6) {
		this.m00 = double1;
		this.m01 = double2;
		this.m10 = double3;
		this.m11 = double4;
		this.m20 = double5;
		this.m21 = double6;
	}

	public Matrix3x2d(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
	}

	public double m00() {
		return this.m00;
	}

	public double m01() {
		return this.m01;
	}

	public double m10() {
		return this.m10;
	}

	public double m11() {
		return this.m11;
	}

	public double m20() {
		return this.m20;
	}

	public double m21() {
		return this.m21;
	}

	Matrix3x2d _m00(double double1) {
		this.m00 = double1;
		return this;
	}

	Matrix3x2d _m01(double double1) {
		this.m01 = double1;
		return this;
	}

	Matrix3x2d _m10(double double1) {
		this.m10 = double1;
		return this;
	}

	Matrix3x2d _m11(double double1) {
		this.m11 = double1;
		return this;
	}

	Matrix3x2d _m20(double double1) {
		this.m20 = double1;
		return this;
	}

	Matrix3x2d _m21(double double1) {
		this.m21 = double1;
		return this;
	}

	public Matrix3x2d set(Matrix3x2dc matrix3x2dc) {
		if (matrix3x2dc instanceof Matrix3x2d) {
			MemUtil.INSTANCE.copy((Matrix3x2d)matrix3x2dc, this);
		} else {
			this.setMatrix3x2dc(matrix3x2dc);
		}

		return this;
	}

	private void setMatrix3x2dc(Matrix3x2dc matrix3x2dc) {
		this.m00 = matrix3x2dc.m00();
		this.m01 = matrix3x2dc.m01();
		this.m10 = matrix3x2dc.m10();
		this.m11 = matrix3x2dc.m11();
		this.m20 = matrix3x2dc.m20();
		this.m21 = matrix3x2dc.m21();
	}

	public Matrix3x2d set(Matrix2dc matrix2dc) {
		if (matrix2dc instanceof Matrix2d) {
			MemUtil.INSTANCE.copy((Matrix2d)matrix2dc, this);
		} else {
			this.setMatrix2dc(matrix2dc);
		}

		return this;
	}

	private void setMatrix2dc(Matrix2dc matrix2dc) {
		this.m00 = matrix2dc.m00();
		this.m01 = matrix2dc.m01();
		this.m10 = matrix2dc.m10();
		this.m11 = matrix2dc.m11();
	}

	public Matrix3x2d set(Matrix2fc matrix2fc) {
		this.m00 = (double)matrix2fc.m00();
		this.m01 = (double)matrix2fc.m01();
		this.m10 = (double)matrix2fc.m10();
		this.m11 = (double)matrix2fc.m11();
		return this;
	}

	public Matrix3x2d mul(Matrix3x2dc matrix3x2dc) {
		return this.mul(matrix3x2dc, this);
	}

	public Matrix3x2d mul(Matrix3x2dc matrix3x2dc, Matrix3x2d matrix3x2d) {
		double double1 = this.m00 * matrix3x2dc.m00() + this.m10 * matrix3x2dc.m01();
		double double2 = this.m01 * matrix3x2dc.m00() + this.m11 * matrix3x2dc.m01();
		double double3 = this.m00 * matrix3x2dc.m10() + this.m10 * matrix3x2dc.m11();
		double double4 = this.m01 * matrix3x2dc.m10() + this.m11 * matrix3x2dc.m11();
		double double5 = this.m00 * matrix3x2dc.m20() + this.m10 * matrix3x2dc.m21() + this.m20;
		double double6 = this.m01 * matrix3x2dc.m20() + this.m11 * matrix3x2dc.m21() + this.m21;
		matrix3x2d.m00 = double1;
		matrix3x2d.m01 = double2;
		matrix3x2d.m10 = double3;
		matrix3x2d.m11 = double4;
		matrix3x2d.m20 = double5;
		matrix3x2d.m21 = double6;
		return matrix3x2d;
	}

	public Matrix3x2d mulLocal(Matrix3x2dc matrix3x2dc) {
		return this.mulLocal(matrix3x2dc, this);
	}

	public Matrix3x2d mulLocal(Matrix3x2dc matrix3x2dc, Matrix3x2d matrix3x2d) {
		double double1 = matrix3x2dc.m00() * this.m00 + matrix3x2dc.m10() * this.m01;
		double double2 = matrix3x2dc.m01() * this.m00 + matrix3x2dc.m11() * this.m01;
		double double3 = matrix3x2dc.m00() * this.m10 + matrix3x2dc.m10() * this.m11;
		double double4 = matrix3x2dc.m01() * this.m10 + matrix3x2dc.m11() * this.m11;
		double double5 = matrix3x2dc.m00() * this.m20 + matrix3x2dc.m10() * this.m21 + matrix3x2dc.m20();
		double double6 = matrix3x2dc.m01() * this.m20 + matrix3x2dc.m11() * this.m21 + matrix3x2dc.m21();
		matrix3x2d.m00 = double1;
		matrix3x2d.m01 = double2;
		matrix3x2d.m10 = double3;
		matrix3x2d.m11 = double4;
		matrix3x2d.m20 = double5;
		matrix3x2d.m21 = double6;
		return matrix3x2d;
	}

	public Matrix3x2d set(double double1, double double2, double double3, double double4, double double5, double double6) {
		this.m00 = double1;
		this.m01 = double2;
		this.m10 = double3;
		this.m11 = double4;
		this.m20 = double5;
		this.m21 = double6;
		return this;
	}

	public Matrix3x2d set(double[] doubleArray) {
		MemUtil.INSTANCE.copy((double[])doubleArray, 0, (Matrix3x2d)this);
		return this;
	}

	public double determinant() {
		return this.m00 * this.m11 - this.m01 * this.m10;
	}

	public Matrix3x2d invert() {
		return this.invert(this);
	}

	public Matrix3x2d invert(Matrix3x2d matrix3x2d) {
		double double1 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10);
		double double2 = this.m11 * double1;
		double double3 = -this.m01 * double1;
		double double4 = -this.m10 * double1;
		double double5 = this.m00 * double1;
		double double6 = (this.m10 * this.m21 - this.m20 * this.m11) * double1;
		double double7 = (this.m20 * this.m01 - this.m00 * this.m21) * double1;
		matrix3x2d.m00 = double2;
		matrix3x2d.m01 = double3;
		matrix3x2d.m10 = double4;
		matrix3x2d.m11 = double5;
		matrix3x2d.m20 = double6;
		matrix3x2d.m21 = double7;
		return matrix3x2d;
	}

	public Matrix3x2d translation(double double1, double double2) {
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m10 = 0.0;
		this.m11 = 1.0;
		this.m20 = double1;
		this.m21 = double2;
		return this;
	}

	public Matrix3x2d translation(Vector2dc vector2dc) {
		return this.translation(vector2dc.x(), vector2dc.y());
	}

	public Matrix3x2d setTranslation(double double1, double double2) {
		this.m20 = double1;
		this.m21 = double2;
		return this;
	}

	public Matrix3x2d setTranslation(Vector2dc vector2dc) {
		return this.setTranslation(vector2dc.x(), vector2dc.y());
	}

	public Matrix3x2d translate(double double1, double double2, Matrix3x2d matrix3x2d) {
		matrix3x2d.m20 = this.m00 * double1 + this.m10 * double2 + this.m20;
		matrix3x2d.m21 = this.m01 * double1 + this.m11 * double2 + this.m21;
		matrix3x2d.m00 = this.m00;
		matrix3x2d.m01 = this.m01;
		matrix3x2d.m10 = this.m10;
		matrix3x2d.m11 = this.m11;
		return matrix3x2d;
	}

	public Matrix3x2d translate(double double1, double double2) {
		return this.translate(double1, double2, this);
	}

	public Matrix3x2d translate(Vector2dc vector2dc, Matrix3x2d matrix3x2d) {
		return this.translate(vector2dc.x(), vector2dc.y(), matrix3x2d);
	}

	public Matrix3x2d translate(Vector2dc vector2dc) {
		return this.translate(vector2dc.x(), vector2dc.y(), this);
	}

	public Matrix3x2d translateLocal(Vector2dc vector2dc) {
		return this.translateLocal(vector2dc.x(), vector2dc.y());
	}

	public Matrix3x2d translateLocal(Vector2dc vector2dc, Matrix3x2d matrix3x2d) {
		return this.translateLocal(vector2dc.x(), vector2dc.y(), matrix3x2d);
	}

	public Matrix3x2d translateLocal(double double1, double double2, Matrix3x2d matrix3x2d) {
		matrix3x2d.m00 = this.m00;
		matrix3x2d.m01 = this.m01;
		matrix3x2d.m10 = this.m10;
		matrix3x2d.m11 = this.m11;
		matrix3x2d.m20 = this.m20 + double1;
		matrix3x2d.m21 = this.m21 + double2;
		return matrix3x2d;
	}

	public Matrix3x2d translateLocal(double double1, double double2) {
		return this.translateLocal(double1, double2, this);
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
		return string + " " + Runtime.format(this.m10, numberFormat) + " " + Runtime.format(this.m20, numberFormat) + "\n" + Runtime.format(this.m01, numberFormat) + " " + Runtime.format(this.m11, numberFormat) + " " + Runtime.format(this.m21, numberFormat) + "\n";
	}

	public Matrix3x2d get(Matrix3x2d matrix3x2d) {
		return matrix3x2d.set((Matrix3x2dc)this);
	}

	public DoubleBuffer get(DoubleBuffer doubleBuffer) {
		return this.get(doubleBuffer.position(), doubleBuffer);
	}

	public DoubleBuffer get(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		return this.get(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public DoubleBuffer get3x3(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put3x3((Matrix3x2d)this, 0, (DoubleBuffer)doubleBuffer);
		return doubleBuffer;
	}

	public DoubleBuffer get3x3(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put3x3(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public ByteBuffer get3x3(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put3x3((Matrix3x2d)this, 0, (ByteBuffer)byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get3x3(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put3x3(this, int1, byteBuffer);
		return byteBuffer;
	}

	public DoubleBuffer get4x4(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put4x4((Matrix3x2d)this, 0, (DoubleBuffer)doubleBuffer);
		return doubleBuffer;
	}

	public DoubleBuffer get4x4(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put4x4(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public ByteBuffer get4x4(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x4((Matrix3x2d)this, 0, (ByteBuffer)byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get4x4(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x4(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Matrix3x2dc getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public double[] get(double[] doubleArray, int int1) {
		MemUtil.INSTANCE.copy(this, doubleArray, int1);
		return doubleArray;
	}

	public double[] get(double[] doubleArray) {
		return this.get(doubleArray, 0);
	}

	public double[] get3x3(double[] doubleArray, int int1) {
		MemUtil.INSTANCE.copy3x3(this, doubleArray, int1);
		return doubleArray;
	}

	public double[] get3x3(double[] doubleArray) {
		return this.get3x3(doubleArray, 0);
	}

	public double[] get4x4(double[] doubleArray, int int1) {
		MemUtil.INSTANCE.copy4x4(this, doubleArray, int1);
		return doubleArray;
	}

	public double[] get4x4(double[] doubleArray) {
		return this.get4x4(doubleArray, 0);
	}

	public Matrix3x2d set(DoubleBuffer doubleBuffer) {
		int int1 = doubleBuffer.position();
		MemUtil.INSTANCE.get(this, int1, doubleBuffer);
		return this;
	}

	public Matrix3x2d set(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.position();
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Matrix3x2d setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
	}

	public Matrix3x2d zero() {
		MemUtil.INSTANCE.zero(this);
		return this;
	}

	public Matrix3x2d identity() {
		MemUtil.INSTANCE.identity(this);
		return this;
	}

	public Matrix3x2d scale(double double1, double double2, Matrix3x2d matrix3x2d) {
		matrix3x2d.m00 = this.m00 * double1;
		matrix3x2d.m01 = this.m01 * double1;
		matrix3x2d.m10 = this.m10 * double2;
		matrix3x2d.m11 = this.m11 * double2;
		matrix3x2d.m20 = this.m20;
		matrix3x2d.m21 = this.m21;
		return matrix3x2d;
	}

	public Matrix3x2d scale(double double1, double double2) {
		return this.scale(double1, double2, this);
	}

	public Matrix3x2d scale(Vector2dc vector2dc) {
		return this.scale(vector2dc.x(), vector2dc.y(), this);
	}

	public Matrix3x2d scale(Vector2dc vector2dc, Matrix3x2d matrix3x2d) {
		return this.scale(vector2dc.x(), vector2dc.y(), matrix3x2d);
	}

	public Matrix3x2d scale(Vector2fc vector2fc) {
		return this.scale((double)vector2fc.x(), (double)vector2fc.y(), this);
	}

	public Matrix3x2d scale(Vector2fc vector2fc, Matrix3x2d matrix3x2d) {
		return this.scale((double)vector2fc.x(), (double)vector2fc.y(), matrix3x2d);
	}

	public Matrix3x2d scale(double double1, Matrix3x2d matrix3x2d) {
		return this.scale(double1, double1, matrix3x2d);
	}

	public Matrix3x2d scale(double double1) {
		return this.scale(double1, double1);
	}

	public Matrix3x2d scaleLocal(double double1, double double2, Matrix3x2d matrix3x2d) {
		matrix3x2d.m00 = double1 * this.m00;
		matrix3x2d.m01 = double2 * this.m01;
		matrix3x2d.m10 = double1 * this.m10;
		matrix3x2d.m11 = double2 * this.m11;
		matrix3x2d.m20 = double1 * this.m20;
		matrix3x2d.m21 = double2 * this.m21;
		return matrix3x2d;
	}

	public Matrix3x2d scaleLocal(double double1, double double2) {
		return this.scaleLocal(double1, double2, this);
	}

	public Matrix3x2d scaleLocal(double double1, Matrix3x2d matrix3x2d) {
		return this.scaleLocal(double1, double1, matrix3x2d);
	}

	public Matrix3x2d scaleLocal(double double1) {
		return this.scaleLocal(double1, double1, this);
	}

	public Matrix3x2d scaleAround(double double1, double double2, double double3, double double4, Matrix3x2d matrix3x2d) {
		double double5 = this.m00 * double3 + this.m10 * double4 + this.m20;
		double double6 = this.m01 * double3 + this.m11 * double4 + this.m21;
		matrix3x2d.m00 = this.m00 * double1;
		matrix3x2d.m01 = this.m01 * double1;
		matrix3x2d.m10 = this.m10 * double2;
		matrix3x2d.m11 = this.m11 * double2;
		matrix3x2d.m20 = matrix3x2d.m00 * -double3 + matrix3x2d.m10 * -double4 + double5;
		matrix3x2d.m21 = matrix3x2d.m01 * -double3 + matrix3x2d.m11 * -double4 + double6;
		return matrix3x2d;
	}

	public Matrix3x2d scaleAround(double double1, double double2, double double3, double double4) {
		return this.scaleAround(double1, double2, double3, double4, this);
	}

	public Matrix3x2d scaleAround(double double1, double double2, double double3, Matrix3x2d matrix3x2d) {
		return this.scaleAround(double1, double1, double2, double3, this);
	}

	public Matrix3x2d scaleAround(double double1, double double2, double double3) {
		return this.scaleAround(double1, double1, double2, double3, this);
	}

	public Matrix3x2d scaleAroundLocal(double double1, double double2, double double3, double double4, Matrix3x2d matrix3x2d) {
		matrix3x2d.m00 = double1 * this.m00;
		matrix3x2d.m01 = double2 * this.m01;
		matrix3x2d.m10 = double1 * this.m10;
		matrix3x2d.m11 = double2 * this.m11;
		matrix3x2d.m20 = double1 * this.m20 - double1 * double3 + double3;
		matrix3x2d.m21 = double2 * this.m21 - double2 * double4 + double4;
		return matrix3x2d;
	}

	public Matrix3x2d scaleAroundLocal(double double1, double double2, double double3, Matrix3x2d matrix3x2d) {
		return this.scaleAroundLocal(double1, double1, double2, double3, matrix3x2d);
	}

	public Matrix3x2d scaleAroundLocal(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.scaleAroundLocal(double1, double2, double4, double5, this);
	}

	public Matrix3x2d scaleAroundLocal(double double1, double double2, double double3) {
		return this.scaleAroundLocal(double1, double1, double2, double3, this);
	}

	public Matrix3x2d scaling(double double1) {
		return this.scaling(double1, double1);
	}

	public Matrix3x2d scaling(double double1, double double2) {
		this.m00 = double1;
		this.m01 = 0.0;
		this.m10 = 0.0;
		this.m11 = double2;
		this.m20 = 0.0;
		this.m21 = 0.0;
		return this;
	}

	public Matrix3x2d rotation(double double1) {
		double double2 = Math.cos(double1);
		double double3 = Math.sin(double1);
		this.m00 = double2;
		this.m10 = -double3;
		this.m20 = 0.0;
		this.m01 = double3;
		this.m11 = double2;
		this.m21 = 0.0;
		return this;
	}

	public Vector3d transform(Vector3d vector3d) {
		return vector3d.mul((Matrix3x2dc)this);
	}

	public Vector3d transform(Vector3dc vector3dc, Vector3d vector3d) {
		return vector3dc.mul((Matrix3x2dc)this, (Vector3d)vector3d);
	}

	public Vector3d transform(double double1, double double2, double double3, Vector3d vector3d) {
		return vector3d.set(this.m00 * double1 + this.m10 * double2 + this.m20 * double3, this.m01 * double1 + this.m11 * double2 + this.m21 * double3, double3);
	}

	public Vector2d transformPosition(Vector2d vector2d) {
		vector2d.set(this.m00 * vector2d.x + this.m10 * vector2d.y + this.m20, this.m01 * vector2d.x + this.m11 * vector2d.y + this.m21);
		return vector2d;
	}

	public Vector2d transformPosition(Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.set(this.m00 * vector2dc.x() + this.m10 * vector2dc.y() + this.m20, this.m01 * vector2dc.x() + this.m11 * vector2dc.y() + this.m21);
		return vector2d;
	}

	public Vector2d transformPosition(double double1, double double2, Vector2d vector2d) {
		return vector2d.set(this.m00 * double1 + this.m10 * double2 + this.m20, this.m01 * double1 + this.m11 * double2 + this.m21);
	}

	public Vector2d transformDirection(Vector2d vector2d) {
		vector2d.set(this.m00 * vector2d.x + this.m10 * vector2d.y, this.m01 * vector2d.x + this.m11 * vector2d.y);
		return vector2d;
	}

	public Vector2d transformDirection(Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.set(this.m00 * vector2dc.x() + this.m10 * vector2dc.y(), this.m01 * vector2dc.x() + this.m11 * vector2dc.y());
		return vector2d;
	}

	public Vector2d transformDirection(double double1, double double2, Vector2d vector2d) {
		return vector2d.set(this.m00 * double1 + this.m10 * double2, this.m01 * double1 + this.m11 * double2);
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeDouble(this.m00);
		objectOutput.writeDouble(this.m01);
		objectOutput.writeDouble(this.m10);
		objectOutput.writeDouble(this.m11);
		objectOutput.writeDouble(this.m20);
		objectOutput.writeDouble(this.m21);
	}

	public void readExternal(ObjectInput objectInput) throws IOException {
		this.m00 = objectInput.readDouble();
		this.m01 = objectInput.readDouble();
		this.m10 = objectInput.readDouble();
		this.m11 = objectInput.readDouble();
		this.m20 = objectInput.readDouble();
		this.m21 = objectInput.readDouble();
	}

	public Matrix3x2d rotate(double double1) {
		return this.rotate(double1, this);
	}

	public Matrix3x2d rotate(double double1, Matrix3x2d matrix3x2d) {
		double double2 = Math.cos(double1);
		double double3 = Math.sin(double1);
		double double4 = -double3;
		double double5 = this.m00 * double2 + this.m10 * double3;
		double double6 = this.m01 * double2 + this.m11 * double3;
		matrix3x2d.m10 = this.m00 * double4 + this.m10 * double2;
		matrix3x2d.m11 = this.m01 * double4 + this.m11 * double2;
		matrix3x2d.m00 = double5;
		matrix3x2d.m01 = double6;
		matrix3x2d.m20 = this.m20;
		matrix3x2d.m21 = this.m21;
		return matrix3x2d;
	}

	public Matrix3x2d rotateLocal(double double1, Matrix3x2d matrix3x2d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = double3 * this.m00 - double2 * this.m01;
		double double5 = double2 * this.m00 + double3 * this.m01;
		double double6 = double3 * this.m10 - double2 * this.m11;
		double double7 = double2 * this.m10 + double3 * this.m11;
		double double8 = double3 * this.m20 - double2 * this.m21;
		double double9 = double2 * this.m20 + double3 * this.m21;
		matrix3x2d.m00 = double4;
		matrix3x2d.m01 = double5;
		matrix3x2d.m10 = double6;
		matrix3x2d.m11 = double7;
		matrix3x2d.m20 = double8;
		matrix3x2d.m21 = double9;
		return matrix3x2d;
	}

	public Matrix3x2d rotateLocal(double double1) {
		return this.rotateLocal(double1, this);
	}

	public Matrix3x2d rotateAbout(double double1, double double2, double double3) {
		return this.rotateAbout(double1, double2, double3, this);
	}

	public Matrix3x2d rotateAbout(double double1, double double2, double double3, Matrix3x2d matrix3x2d) {
		double double4 = this.m00 * double2 + this.m10 * double3 + this.m20;
		double double5 = this.m01 * double2 + this.m11 * double3 + this.m21;
		double double6 = Math.cos(double1);
		double double7 = Math.sin(double1);
		double double8 = this.m00 * double6 + this.m10 * double7;
		double double9 = this.m01 * double6 + this.m11 * double7;
		matrix3x2d.m10 = this.m00 * -double7 + this.m10 * double6;
		matrix3x2d.m11 = this.m01 * -double7 + this.m11 * double6;
		matrix3x2d.m00 = double8;
		matrix3x2d.m01 = double9;
		matrix3x2d.m20 = matrix3x2d.m00 * -double2 + matrix3x2d.m10 * -double3 + double4;
		matrix3x2d.m21 = matrix3x2d.m01 * -double2 + matrix3x2d.m11 * -double3 + double5;
		return matrix3x2d;
	}

	public Matrix3x2d rotateTo(Vector2dc vector2dc, Vector2dc vector2dc2, Matrix3x2d matrix3x2d) {
		double double1 = vector2dc.x() * vector2dc2.x() + vector2dc.y() * vector2dc2.y();
		double double2 = vector2dc.x() * vector2dc2.y() - vector2dc.y() * vector2dc2.x();
		double double3 = -double2;
		double double4 = this.m00 * double1 + this.m10 * double2;
		double double5 = this.m01 * double1 + this.m11 * double2;
		matrix3x2d.m10 = this.m00 * double3 + this.m10 * double1;
		matrix3x2d.m11 = this.m01 * double3 + this.m11 * double1;
		matrix3x2d.m00 = double4;
		matrix3x2d.m01 = double5;
		matrix3x2d.m20 = this.m20;
		matrix3x2d.m21 = this.m21;
		return matrix3x2d;
	}

	public Matrix3x2d rotateTo(Vector2dc vector2dc, Vector2dc vector2dc2) {
		return this.rotateTo(vector2dc, vector2dc2, this);
	}

	public Matrix3x2d view(double double1, double double2, double double3, double double4, Matrix3x2d matrix3x2d) {
		double double5 = 2.0 / (double2 - double1);
		double double6 = 2.0 / (double4 - double3);
		double double7 = (double1 + double2) / (double1 - double2);
		double double8 = (double3 + double4) / (double3 - double4);
		matrix3x2d.m20 = this.m00 * double7 + this.m10 * double8 + this.m20;
		matrix3x2d.m21 = this.m01 * double7 + this.m11 * double8 + this.m21;
		matrix3x2d.m00 = this.m00 * double5;
		matrix3x2d.m01 = this.m01 * double5;
		matrix3x2d.m10 = this.m10 * double6;
		matrix3x2d.m11 = this.m11 * double6;
		return matrix3x2d;
	}

	public Matrix3x2d view(double double1, double double2, double double3, double double4) {
		return this.view(double1, double2, double3, double4, this);
	}

	public Matrix3x2d setView(double double1, double double2, double double3, double double4) {
		this.m00 = 2.0 / (double2 - double1);
		this.m01 = 0.0;
		this.m10 = 0.0;
		this.m11 = 2.0 / (double4 - double3);
		this.m20 = (double1 + double2) / (double1 - double2);
		this.m21 = (double3 + double4) / (double3 - double4);
		return this;
	}

	public Vector2d origin(Vector2d vector2d) {
		double double1 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10);
		vector2d.x = (this.m10 * this.m21 - this.m20 * this.m11) * double1;
		vector2d.y = (this.m20 * this.m01 - this.m00 * this.m21) * double1;
		return vector2d;
	}

	public double[] viewArea(double[] doubleArray) {
		double double1 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10);
		double double2 = this.m11 * double1;
		double double3 = -this.m01 * double1;
		double double4 = -this.m10 * double1;
		double double5 = this.m00 * double1;
		double double6 = (this.m10 * this.m21 - this.m20 * this.m11) * double1;
		double double7 = (this.m20 * this.m01 - this.m00 * this.m21) * double1;
		double double8 = -double2 - double4;
		double double9 = -double3 - double5;
		double double10 = double2 - double4;
		double double11 = double3 - double5;
		double double12 = -double2 + double4;
		double double13 = -double3 + double5;
		double double14 = double2 + double4;
		double double15 = double3 + double5;
		double double16 = double8 < double12 ? double8 : double12;
		double16 = double16 < double10 ? double16 : double10;
		double16 = double16 < double14 ? double16 : double14;
		double double17 = double9 < double13 ? double9 : double13;
		double17 = double17 < double11 ? double17 : double11;
		double17 = double17 < double15 ? double17 : double15;
		double double18 = double8 > double12 ? double8 : double12;
		double18 = double18 > double10 ? double18 : double10;
		double18 = double18 > double14 ? double18 : double14;
		double double19 = double9 > double13 ? double9 : double13;
		double19 = double19 > double11 ? double19 : double11;
		double19 = double19 > double15 ? double19 : double15;
		doubleArray[0] = double16 + double6;
		doubleArray[1] = double17 + double7;
		doubleArray[2] = double18 + double6;
		doubleArray[3] = double19 + double7;
		return doubleArray;
	}

	public Vector2d positiveX(Vector2d vector2d) {
		double double1 = this.m00 * this.m11 - this.m01 * this.m10;
		double1 = 1.0 / double1;
		vector2d.x = this.m11 * double1;
		vector2d.y = -this.m01 * double1;
		return vector2d.normalize(vector2d);
	}

	public Vector2d normalizedPositiveX(Vector2d vector2d) {
		vector2d.x = this.m11;
		vector2d.y = -this.m01;
		return vector2d;
	}

	public Vector2d positiveY(Vector2d vector2d) {
		double double1 = this.m00 * this.m11 - this.m01 * this.m10;
		double1 = 1.0 / double1;
		vector2d.x = -this.m10 * double1;
		vector2d.y = this.m00 * double1;
		return vector2d.normalize(vector2d);
	}

	public Vector2d normalizedPositiveY(Vector2d vector2d) {
		vector2d.x = -this.m10;
		vector2d.y = this.m00;
		return vector2d;
	}

	public Vector2d unproject(double double1, double double2, int[] intArray, Vector2d vector2d) {
		double double3 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10);
		double double4 = this.m11 * double3;
		double double5 = -this.m01 * double3;
		double double6 = -this.m10 * double3;
		double double7 = this.m00 * double3;
		double double8 = (this.m10 * this.m21 - this.m20 * this.m11) * double3;
		double double9 = (this.m20 * this.m01 - this.m00 * this.m21) * double3;
		double double10 = (double1 - (double)intArray[0]) / (double)intArray[2] * 2.0 - 1.0;
		double double11 = (double2 - (double)intArray[1]) / (double)intArray[3] * 2.0 - 1.0;
		vector2d.x = double4 * double10 + double6 * double11 + double8;
		vector2d.y = double5 * double10 + double7 * double11 + double9;
		return vector2d;
	}

	public Vector2d unprojectInv(double double1, double double2, int[] intArray, Vector2d vector2d) {
		double double3 = (double1 - (double)intArray[0]) / (double)intArray[2] * 2.0 - 1.0;
		double double4 = (double2 - (double)intArray[1]) / (double)intArray[3] * 2.0 - 1.0;
		vector2d.x = this.m00 * double3 + this.m10 * double4 + this.m20;
		vector2d.y = this.m01 * double3 + this.m11 * double4 + this.m21;
		return vector2d;
	}

	public Matrix3x2d span(Vector2d vector2d, Vector2d vector2d2, Vector2d vector2d3) {
		double double1 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10);
		double double2 = this.m11 * double1;
		double double3 = -this.m01 * double1;
		double double4 = -this.m10 * double1;
		double double5 = this.m00 * double1;
		vector2d.x = -double2 - double4 + (this.m10 * this.m21 - this.m20 * this.m11) * double1;
		vector2d.y = -double3 - double5 + (this.m20 * this.m01 - this.m00 * this.m21) * double1;
		vector2d2.x = 2.0 * double2;
		vector2d2.y = 2.0 * double3;
		vector2d3.x = 2.0 * double4;
		vector2d3.y = 2.0 * double5;
		return this;
	}

	public boolean testPoint(double double1, double double2) {
		double double3 = this.m00;
		double double4 = this.m10;
		double double5 = 1.0 + this.m20;
		double double6 = -this.m00;
		double double7 = -this.m10;
		double double8 = 1.0 - this.m20;
		double double9 = this.m01;
		double double10 = this.m11;
		double double11 = 1.0 + this.m21;
		double double12 = -this.m01;
		double double13 = -this.m11;
		double double14 = 1.0 - this.m21;
		return double3 * double1 + double4 * double2 + double5 >= 0.0 && double6 * double1 + double7 * double2 + double8 >= 0.0 && double9 * double1 + double10 * double2 + double11 >= 0.0 && double12 * double1 + double13 * double2 + double14 >= 0.0;
	}

	public boolean testCircle(double double1, double double2, double double3) {
		double double4 = this.m00;
		double double5 = this.m10;
		double double6 = 1.0 + this.m20;
		double double7 = Math.invsqrt(double4 * double4 + double5 * double5);
		double4 *= double7;
		double5 *= double7;
		double6 *= double7;
		double double8 = -this.m00;
		double double9 = -this.m10;
		double double10 = 1.0 - this.m20;
		double7 = Math.invsqrt(double8 * double8 + double9 * double9);
		double8 *= double7;
		double9 *= double7;
		double10 *= double7;
		double double11 = this.m01;
		double double12 = this.m11;
		double double13 = 1.0 + this.m21;
		double7 = Math.invsqrt(double11 * double11 + double12 * double12);
		double11 *= double7;
		double12 *= double7;
		double13 *= double7;
		double double14 = -this.m01;
		double double15 = -this.m11;
		double double16 = 1.0 - this.m21;
		double7 = Math.invsqrt(double14 * double14 + double15 * double15);
		double14 *= double7;
		double15 *= double7;
		double16 *= double7;
		return double4 * double1 + double5 * double2 + double6 >= -double3 && double8 * double1 + double9 * double2 + double10 >= -double3 && double11 * double1 + double12 * double2 + double13 >= -double3 && double14 * double1 + double15 * double2 + double16 >= -double3;
	}

	public boolean testAar(double double1, double double2, double double3, double double4) {
		double double5 = this.m00;
		double double6 = this.m10;
		double double7 = 1.0 + this.m20;
		double double8 = -this.m00;
		double double9 = -this.m10;
		double double10 = 1.0 - this.m20;
		double double11 = this.m01;
		double double12 = this.m11;
		double double13 = 1.0 + this.m21;
		double double14 = -this.m01;
		double double15 = -this.m11;
		double double16 = 1.0 - this.m21;
		return double5 * (double5 < 0.0 ? double1 : double3) + double6 * (double6 < 0.0 ? double2 : double4) >= -double7 && double8 * (double8 < 0.0 ? double1 : double3) + double9 * (double9 < 0.0 ? double2 : double4) >= -double10 && double11 * (double11 < 0.0 ? double1 : double3) + double12 * (double12 < 0.0 ? double2 : double4) >= -double13 && double14 * (double14 < 0.0 ? double1 : double3) + double15 * (double15 < 0.0 ? double2 : double4) >= -double16;
	}

	public int hashCode() {
		byte byte1 = 1;
		long long1 = Double.doubleToLongBits(this.m00);
		int int1 = 31 * byte1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m01);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m10);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m11);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m20);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.m21);
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
			Matrix3x2d matrix3x2d = (Matrix3x2d)object;
			if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(matrix3x2d.m00)) {
				return false;
			} else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(matrix3x2d.m01)) {
				return false;
			} else if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(matrix3x2d.m10)) {
				return false;
			} else if (Double.doubleToLongBits(this.m11) != Double.doubleToLongBits(matrix3x2d.m11)) {
				return false;
			} else if (Double.doubleToLongBits(this.m20) != Double.doubleToLongBits(matrix3x2d.m20)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.m21) == Double.doubleToLongBits(matrix3x2d.m21);
			}
		}
	}

	public boolean equals(Matrix3x2dc matrix3x2dc, double double1) {
		if (this == matrix3x2dc) {
			return true;
		} else if (matrix3x2dc == null) {
			return false;
		} else if (!(matrix3x2dc instanceof Matrix3x2d)) {
			return false;
		} else if (!Runtime.equals(this.m00, matrix3x2dc.m00(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m01, matrix3x2dc.m01(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m10, matrix3x2dc.m10(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m11, matrix3x2dc.m11(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m20, matrix3x2dc.m20(), double1)) {
			return false;
		} else {
			return Runtime.equals(this.m21, matrix3x2dc.m21(), double1);
		}
	}

	public boolean isFinite() {
		return Math.isFinite(this.m00) && Math.isFinite(this.m01) && Math.isFinite(this.m10) && Math.isFinite(this.m11) && Math.isFinite(this.m20) && Math.isFinite(this.m21);
	}
}
