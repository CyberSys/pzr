package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Matrix2d implements Externalizable,Matrix2dc {
	private static final long serialVersionUID = 1L;
	public double m00;
	public double m01;
	public double m10;
	public double m11;

	public Matrix2d() {
		this.m00 = 1.0;
		this.m11 = 1.0;
	}

	public Matrix2d(Matrix2dc matrix2dc) {
		if (matrix2dc instanceof Matrix2d) {
			MemUtil.INSTANCE.copy((Matrix2d)matrix2dc, this);
		} else {
			this.setMatrix2dc(matrix2dc);
		}
	}

	public Matrix2d(Matrix2fc matrix2fc) {
		this.m00 = (double)matrix2fc.m00();
		this.m01 = (double)matrix2fc.m01();
		this.m10 = (double)matrix2fc.m10();
		this.m11 = (double)matrix2fc.m11();
	}

	public Matrix2d(Matrix3dc matrix3dc) {
		if (matrix3dc instanceof Matrix3d) {
			MemUtil.INSTANCE.copy((Matrix3d)matrix3dc, this);
		} else {
			this.setMatrix3dc(matrix3dc);
		}
	}

	public Matrix2d(Matrix3fc matrix3fc) {
		this.m00 = (double)matrix3fc.m00();
		this.m01 = (double)matrix3fc.m01();
		this.m10 = (double)matrix3fc.m10();
		this.m11 = (double)matrix3fc.m11();
	}

	public Matrix2d(double double1, double double2, double double3, double double4) {
		this.m00 = double1;
		this.m01 = double2;
		this.m10 = double3;
		this.m11 = double4;
	}

	public Matrix2d(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
	}

	public Matrix2d(Vector2dc vector2dc, Vector2dc vector2dc2) {
		this.m00 = vector2dc.x();
		this.m01 = vector2dc.y();
		this.m10 = vector2dc2.x();
		this.m11 = vector2dc2.y();
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

	public Matrix2d m00(double double1) {
		this.m00 = double1;
		return this;
	}

	public Matrix2d m01(double double1) {
		this.m01 = double1;
		return this;
	}

	public Matrix2d m10(double double1) {
		this.m10 = double1;
		return this;
	}

	public Matrix2d m11(double double1) {
		this.m11 = double1;
		return this;
	}

	Matrix2d _m00(double double1) {
		this.m00 = double1;
		return this;
	}

	Matrix2d _m01(double double1) {
		this.m01 = double1;
		return this;
	}

	Matrix2d _m10(double double1) {
		this.m10 = double1;
		return this;
	}

	Matrix2d _m11(double double1) {
		this.m11 = double1;
		return this;
	}

	public Matrix2d set(Matrix2dc matrix2dc) {
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

	public Matrix2d set(Matrix2fc matrix2fc) {
		this.m00 = (double)matrix2fc.m00();
		this.m01 = (double)matrix2fc.m01();
		this.m10 = (double)matrix2fc.m10();
		this.m11 = (double)matrix2fc.m11();
		return this;
	}

	public Matrix2d set(Matrix3x2dc matrix3x2dc) {
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
	}

	public Matrix2d set(Matrix3x2fc matrix3x2fc) {
		this.m00 = (double)matrix3x2fc.m00();
		this.m01 = (double)matrix3x2fc.m01();
		this.m10 = (double)matrix3x2fc.m10();
		this.m11 = (double)matrix3x2fc.m11();
		return this;
	}

	public Matrix2d set(Matrix3dc matrix3dc) {
		if (matrix3dc instanceof Matrix3d) {
			MemUtil.INSTANCE.copy((Matrix3d)matrix3dc, this);
		} else {
			this.setMatrix3dc(matrix3dc);
		}

		return this;
	}

	private void setMatrix3dc(Matrix3dc matrix3dc) {
		this.m00 = matrix3dc.m00();
		this.m01 = matrix3dc.m01();
		this.m10 = matrix3dc.m10();
		this.m11 = matrix3dc.m11();
	}

	public Matrix2d set(Matrix3fc matrix3fc) {
		this.m00 = (double)matrix3fc.m00();
		this.m01 = (double)matrix3fc.m01();
		this.m10 = (double)matrix3fc.m10();
		this.m11 = (double)matrix3fc.m11();
		return this;
	}

	public Matrix2d mul(Matrix2dc matrix2dc) {
		return this.mul(matrix2dc, this);
	}

	public Matrix2d mul(Matrix2dc matrix2dc, Matrix2d matrix2d) {
		double double1 = this.m00 * matrix2dc.m00() + this.m10 * matrix2dc.m01();
		double double2 = this.m01 * matrix2dc.m00() + this.m11 * matrix2dc.m01();
		double double3 = this.m00 * matrix2dc.m10() + this.m10 * matrix2dc.m11();
		double double4 = this.m01 * matrix2dc.m10() + this.m11 * matrix2dc.m11();
		matrix2d.m00 = double1;
		matrix2d.m01 = double2;
		matrix2d.m10 = double3;
		matrix2d.m11 = double4;
		return matrix2d;
	}

	public Matrix2d mul(Matrix2fc matrix2fc) {
		return this.mul(matrix2fc, this);
	}

	public Matrix2d mul(Matrix2fc matrix2fc, Matrix2d matrix2d) {
		double double1 = this.m00 * (double)matrix2fc.m00() + this.m10 * (double)matrix2fc.m01();
		double double2 = this.m01 * (double)matrix2fc.m00() + this.m11 * (double)matrix2fc.m01();
		double double3 = this.m00 * (double)matrix2fc.m10() + this.m10 * (double)matrix2fc.m11();
		double double4 = this.m01 * (double)matrix2fc.m10() + this.m11 * (double)matrix2fc.m11();
		matrix2d.m00 = double1;
		matrix2d.m01 = double2;
		matrix2d.m10 = double3;
		matrix2d.m11 = double4;
		return matrix2d;
	}

	public Matrix2d mulLocal(Matrix2dc matrix2dc) {
		return this.mulLocal(matrix2dc, this);
	}

	public Matrix2d mulLocal(Matrix2dc matrix2dc, Matrix2d matrix2d) {
		double double1 = matrix2dc.m00() * this.m00 + matrix2dc.m10() * this.m01;
		double double2 = matrix2dc.m01() * this.m00 + matrix2dc.m11() * this.m01;
		double double3 = matrix2dc.m00() * this.m10 + matrix2dc.m10() * this.m11;
		double double4 = matrix2dc.m01() * this.m10 + matrix2dc.m11() * this.m11;
		matrix2d.m00 = double1;
		matrix2d.m01 = double2;
		matrix2d.m10 = double3;
		matrix2d.m11 = double4;
		return matrix2d;
	}

	public Matrix2d set(double double1, double double2, double double3, double double4) {
		this.m00 = double1;
		this.m01 = double2;
		this.m10 = double3;
		this.m11 = double4;
		return this;
	}

	public Matrix2d set(double[] doubleArray) {
		MemUtil.INSTANCE.copy((double[])doubleArray, 0, (Matrix2d)this);
		return this;
	}

	public Matrix2d set(Vector2dc vector2dc, Vector2dc vector2dc2) {
		this.m00 = vector2dc.x();
		this.m01 = vector2dc.y();
		this.m10 = vector2dc2.x();
		this.m11 = vector2dc2.y();
		return this;
	}

	public double determinant() {
		return this.m00 * this.m11 - this.m10 * this.m01;
	}

	public Matrix2d invert() {
		return this.invert(this);
	}

	public Matrix2d invert(Matrix2d matrix2d) {
		double double1 = 1.0 / this.determinant();
		double double2 = this.m11 * double1;
		double double3 = -this.m01 * double1;
		double double4 = -this.m10 * double1;
		double double5 = this.m00 * double1;
		matrix2d.m00 = double2;
		matrix2d.m01 = double3;
		matrix2d.m10 = double4;
		matrix2d.m11 = double5;
		return matrix2d;
	}

	public Matrix2d transpose() {
		return this.transpose(this);
	}

	public Matrix2d transpose(Matrix2d matrix2d) {
		matrix2d.set(this.m00, this.m10, this.m01, this.m11);
		return matrix2d;
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
		return string + " " + Runtime.format(this.m10, numberFormat) + "\n" + Runtime.format(this.m01, numberFormat) + " " + Runtime.format(this.m11, numberFormat) + "\n";
	}

	public Matrix2d get(Matrix2d matrix2d) {
		return matrix2d.set((Matrix2dc)this);
	}

	public Matrix3x2d get(Matrix3x2d matrix3x2d) {
		return matrix3x2d.set((Matrix2dc)this);
	}

	public Matrix3d get(Matrix3d matrix3d) {
		return matrix3d.set((Matrix2dc)this);
	}

	public double getRotation() {
		return Math.atan2(this.m01, this.m11);
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

	public DoubleBuffer getTransposed(DoubleBuffer doubleBuffer) {
		return this.get(doubleBuffer.position(), doubleBuffer);
	}

	public DoubleBuffer getTransposed(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public ByteBuffer getTransposed(ByteBuffer byteBuffer) {
		return this.get(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Matrix2dc getToAddress(long long1) {
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

	public Matrix2d set(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
		return this;
	}

	public Matrix2d set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Matrix2d setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
	}

	public Matrix2d zero() {
		MemUtil.INSTANCE.zero(this);
		return this;
	}

	public Matrix2d identity() {
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m10 = 0.0;
		this.m11 = 1.0;
		return this;
	}

	public Matrix2d scale(Vector2dc vector2dc, Matrix2d matrix2d) {
		return this.scale(vector2dc.x(), vector2dc.y(), matrix2d);
	}

	public Matrix2d scale(Vector2dc vector2dc) {
		return this.scale(vector2dc.x(), vector2dc.y(), this);
	}

	public Matrix2d scale(double double1, double double2, Matrix2d matrix2d) {
		matrix2d.m00 = this.m00 * double1;
		matrix2d.m01 = this.m01 * double1;
		matrix2d.m10 = this.m10 * double2;
		matrix2d.m11 = this.m11 * double2;
		return matrix2d;
	}

	public Matrix2d scale(double double1, double double2) {
		return this.scale(double1, double2, this);
	}

	public Matrix2d scale(double double1, Matrix2d matrix2d) {
		return this.scale(double1, double1, matrix2d);
	}

	public Matrix2d scale(double double1) {
		return this.scale(double1, double1);
	}

	public Matrix2d scaleLocal(double double1, double double2, Matrix2d matrix2d) {
		matrix2d.m00 = double1 * this.m00;
		matrix2d.m01 = double2 * this.m01;
		matrix2d.m10 = double1 * this.m10;
		matrix2d.m11 = double2 * this.m11;
		return matrix2d;
	}

	public Matrix2d scaleLocal(double double1, double double2) {
		return this.scaleLocal(double1, double2, this);
	}

	public Matrix2d scaling(double double1) {
		MemUtil.INSTANCE.zero(this);
		this.m00 = double1;
		this.m11 = double1;
		return this;
	}

	public Matrix2d scaling(double double1, double double2) {
		MemUtil.INSTANCE.zero(this);
		this.m00 = double1;
		this.m11 = double2;
		return this;
	}

	public Matrix2d scaling(Vector2dc vector2dc) {
		return this.scaling(vector2dc.x(), vector2dc.y());
	}

	public Matrix2d rotation(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		this.m00 = double3;
		this.m01 = double2;
		this.m10 = -double2;
		this.m11 = double3;
		return this;
	}

	public Vector2d transform(Vector2d vector2d) {
		return vector2d.mul((Matrix2dc)this);
	}

	public Vector2d transform(Vector2dc vector2dc, Vector2d vector2d) {
		vector2dc.mul((Matrix2dc)this, vector2d);
		return vector2d;
	}

	public Vector2d transform(double double1, double double2, Vector2d vector2d) {
		vector2d.set(this.m00 * double1 + this.m10 * double2, this.m01 * double1 + this.m11 * double2);
		return vector2d;
	}

	public Vector2d transformTranspose(Vector2d vector2d) {
		return vector2d.mulTranspose((Matrix2dc)this);
	}

	public Vector2d transformTranspose(Vector2dc vector2dc, Vector2d vector2d) {
		vector2dc.mulTranspose((Matrix2dc)this, vector2d);
		return vector2d;
	}

	public Vector2d transformTranspose(double double1, double double2, Vector2d vector2d) {
		vector2d.set(this.m00 * double1 + this.m01 * double2, this.m10 * double1 + this.m11 * double2);
		return vector2d;
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeDouble(this.m00);
		objectOutput.writeDouble(this.m01);
		objectOutput.writeDouble(this.m10);
		objectOutput.writeDouble(this.m11);
	}

	public void readExternal(ObjectInput objectInput) throws IOException {
		this.m00 = objectInput.readDouble();
		this.m01 = objectInput.readDouble();
		this.m10 = objectInput.readDouble();
		this.m11 = objectInput.readDouble();
	}

	public Matrix2d rotate(double double1) {
		return this.rotate(double1, this);
	}

	public Matrix2d rotate(double double1, Matrix2d matrix2d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.m00 * double3 + this.m10 * double2;
		double double5 = this.m01 * double3 + this.m11 * double2;
		double double6 = this.m10 * double3 - this.m00 * double2;
		double double7 = this.m11 * double3 - this.m01 * double2;
		matrix2d.m00 = double4;
		matrix2d.m01 = double5;
		matrix2d.m10 = double6;
		matrix2d.m11 = double7;
		return matrix2d;
	}

	public Matrix2d rotateLocal(double double1) {
		return this.rotateLocal(double1, this);
	}

	public Matrix2d rotateLocal(double double1, Matrix2d matrix2d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = double3 * this.m00 - double2 * this.m01;
		double double5 = double2 * this.m00 + double3 * this.m01;
		double double6 = double3 * this.m10 - double2 * this.m11;
		double double7 = double2 * this.m10 + double3 * this.m11;
		matrix2d.m00 = double4;
		matrix2d.m01 = double5;
		matrix2d.m10 = double6;
		matrix2d.m11 = double7;
		return matrix2d;
	}

	public Vector2d getRow(int int1, Vector2d vector2d) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			vector2d.x = this.m00;
			vector2d.y = this.m10;
			break;
		
		case 1: 
			vector2d.x = this.m01;
			vector2d.y = this.m11;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector2d;
	}

	public Matrix2d setRow(int int1, Vector2dc vector2dc) throws IndexOutOfBoundsException {
		return this.setRow(int1, vector2dc.x(), vector2dc.y());
	}

	public Matrix2d setRow(int int1, double double1, double double2) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			this.m00 = double1;
			this.m10 = double2;
			break;
		
		case 1: 
			this.m01 = double1;
			this.m11 = double2;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return this;
	}

	public Vector2d getColumn(int int1, Vector2d vector2d) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			vector2d.x = this.m00;
			vector2d.y = this.m01;
			break;
		
		case 1: 
			vector2d.x = this.m10;
			vector2d.y = this.m11;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector2d;
	}

	public Matrix2d setColumn(int int1, Vector2dc vector2dc) throws IndexOutOfBoundsException {
		return this.setColumn(int1, vector2dc.x(), vector2dc.y());
	}

	public Matrix2d setColumn(int int1, double double1, double double2) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			this.m00 = double1;
			this.m01 = double2;
			break;
		
		case 1: 
			this.m10 = double1;
			this.m11 = double2;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return this;
	}

	public double get(int int1, int int2) {
		switch (int1) {
		case 0: 
			switch (int2) {
			case 0: 
				return this.m00;
			
			case 1: 
				return this.m01;
			
			default: 
				throw new IndexOutOfBoundsException();
			
			}

		
		case 1: 
			switch (int2) {
			case 0: 
				return this.m10;
			
			case 1: 
				return this.m11;
			
			}

		
		}
		throw new IndexOutOfBoundsException();
	}

	public Matrix2d set(int int1, int int2, double double1) {
		switch (int1) {
		case 0: 
			switch (int2) {
			case 0: 
				this.m00 = double1;
				return this;
			
			case 1: 
				this.m01 = double1;
				return this;
			
			default: 
				throw new IndexOutOfBoundsException();
			
			}

		
		case 1: 
			switch (int2) {
			case 0: 
				this.m10 = double1;
				return this;
			
			case 1: 
				this.m11 = double1;
				return this;
			
			}

		
		}
		throw new IndexOutOfBoundsException();
	}

	public Matrix2d normal() {
		return this.normal(this);
	}

	public Matrix2d normal(Matrix2d matrix2d) {
		double double1 = this.m00 * this.m11 - this.m10 * this.m01;
		double double2 = 1.0 / double1;
		double double3 = this.m11 * double2;
		double double4 = -this.m10 * double2;
		double double5 = -this.m01 * double2;
		double double6 = this.m00 * double2;
		matrix2d.m00 = double3;
		matrix2d.m01 = double4;
		matrix2d.m10 = double5;
		matrix2d.m11 = double6;
		return matrix2d;
	}

	public Vector2d getScale(Vector2d vector2d) {
		vector2d.x = Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01);
		vector2d.y = Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11);
		return vector2d;
	}

	public Vector2d positiveX(Vector2d vector2d) {
		if (this.m00 * this.m11 < this.m01 * this.m10) {
			vector2d.x = -this.m11;
			vector2d.y = this.m01;
		} else {
			vector2d.x = this.m11;
			vector2d.y = -this.m01;
		}

		return vector2d.normalize(vector2d);
	}

	public Vector2d normalizedPositiveX(Vector2d vector2d) {
		if (this.m00 * this.m11 < this.m01 * this.m10) {
			vector2d.x = -this.m11;
			vector2d.y = this.m01;
		} else {
			vector2d.x = this.m11;
			vector2d.y = -this.m01;
		}

		return vector2d;
	}

	public Vector2d positiveY(Vector2d vector2d) {
		if (this.m00 * this.m11 < this.m01 * this.m10) {
			vector2d.x = this.m10;
			vector2d.y = -this.m00;
		} else {
			vector2d.x = -this.m10;
			vector2d.y = this.m00;
		}

		return vector2d.normalize(vector2d);
	}

	public Vector2d normalizedPositiveY(Vector2d vector2d) {
		if (this.m00 * this.m11 < this.m01 * this.m10) {
			vector2d.x = this.m10;
			vector2d.y = -this.m00;
		} else {
			vector2d.x = -this.m10;
			vector2d.y = this.m00;
		}

		return vector2d;
	}

	public int hashCode() {
		byte byte1 = 1;
		long long1 = Double.doubleToLongBits(this.m00);
		int int1 = 31 * byte1 + (int)(long1 >>> 32 ^ long1);
		long1 = Double.doubleToLongBits(this.m01);
		int1 = 31 * int1 + (int)(long1 >>> 32 ^ long1);
		long1 = Double.doubleToLongBits(this.m10);
		int1 = 31 * int1 + (int)(long1 >>> 32 ^ long1);
		long1 = Double.doubleToLongBits(this.m11);
		int1 = 31 * int1 + (int)(long1 >>> 32 ^ long1);
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
			Matrix2d matrix2d = (Matrix2d)object;
			if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(matrix2d.m00)) {
				return false;
			} else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(matrix2d.m01)) {
				return false;
			} else if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(matrix2d.m10)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.m11) == Double.doubleToLongBits(matrix2d.m11);
			}
		}
	}

	public boolean equals(Matrix2dc matrix2dc, double double1) {
		if (this == matrix2dc) {
			return true;
		} else if (matrix2dc == null) {
			return false;
		} else if (!(matrix2dc instanceof Matrix2d)) {
			return false;
		} else if (!Runtime.equals(this.m00, matrix2dc.m00(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m01, matrix2dc.m01(), double1)) {
			return false;
		} else if (!Runtime.equals(this.m10, matrix2dc.m10(), double1)) {
			return false;
		} else {
			return Runtime.equals(this.m11, matrix2dc.m11(), double1);
		}
	}

	public Matrix2d swap(Matrix2d matrix2d) {
		MemUtil.INSTANCE.swap(this, matrix2d);
		return this;
	}

	public Matrix2d add(Matrix2dc matrix2dc) {
		return this.add(matrix2dc, this);
	}

	public Matrix2d add(Matrix2dc matrix2dc, Matrix2d matrix2d) {
		matrix2d.m00 = this.m00 + matrix2dc.m00();
		matrix2d.m01 = this.m01 + matrix2dc.m01();
		matrix2d.m10 = this.m10 + matrix2dc.m10();
		matrix2d.m11 = this.m11 + matrix2dc.m11();
		return matrix2d;
	}

	public Matrix2d sub(Matrix2dc matrix2dc) {
		return this.sub(matrix2dc, this);
	}

	public Matrix2d sub(Matrix2dc matrix2dc, Matrix2d matrix2d) {
		matrix2d.m00 = this.m00 - matrix2dc.m00();
		matrix2d.m01 = this.m01 - matrix2dc.m01();
		matrix2d.m10 = this.m10 - matrix2dc.m10();
		matrix2d.m11 = this.m11 - matrix2dc.m11();
		return matrix2d;
	}

	public Matrix2d mulComponentWise(Matrix2dc matrix2dc) {
		return this.sub(matrix2dc, this);
	}

	public Matrix2d mulComponentWise(Matrix2dc matrix2dc, Matrix2d matrix2d) {
		matrix2d.m00 = this.m00 * matrix2dc.m00();
		matrix2d.m01 = this.m01 * matrix2dc.m01();
		matrix2d.m10 = this.m10 * matrix2dc.m10();
		matrix2d.m11 = this.m11 * matrix2dc.m11();
		return matrix2d;
	}

	public Matrix2d lerp(Matrix2dc matrix2dc, double double1) {
		return this.lerp(matrix2dc, double1, this);
	}

	public Matrix2d lerp(Matrix2dc matrix2dc, double double1, Matrix2d matrix2d) {
		matrix2d.m00 = Math.fma(matrix2dc.m00() - this.m00, double1, this.m00);
		matrix2d.m01 = Math.fma(matrix2dc.m01() - this.m01, double1, this.m01);
		matrix2d.m10 = Math.fma(matrix2dc.m10() - this.m10, double1, this.m10);
		matrix2d.m11 = Math.fma(matrix2dc.m11() - this.m11, double1, this.m11);
		return matrix2d;
	}

	public boolean isFinite() {
		return Math.isFinite(this.m00) && Math.isFinite(this.m01) && Math.isFinite(this.m10) && Math.isFinite(this.m11);
	}
}
