package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.NumberFormat;


public class Vector2d implements Externalizable,Vector2dc {
	private static final long serialVersionUID = 1L;
	public double x;
	public double y;

	public Vector2d() {
	}

	public Vector2d(double double1) {
		this.x = double1;
		this.y = double1;
	}

	public Vector2d(double double1, double double2) {
		this.x = double1;
		this.y = double2;
	}

	public Vector2d(Vector2dc vector2dc) {
		this.x = vector2dc.x();
		this.y = vector2dc.y();
	}

	public Vector2d(Vector2fc vector2fc) {
		this.x = (double)vector2fc.x();
		this.y = (double)vector2fc.y();
	}

	public Vector2d(Vector2ic vector2ic) {
		this.x = (double)vector2ic.x();
		this.y = (double)vector2ic.y();
	}

	public Vector2d(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
	}

	public Vector2d(float[] floatArray) {
		this.x = (double)floatArray[0];
		this.y = (double)floatArray[1];
	}

	public Vector2d(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
	}

	public Vector2d(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector2d(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
	}

	public Vector2d(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, int1, doubleBuffer);
	}

	public double x() {
		return this.x;
	}

	public double y() {
		return this.y;
	}

	public Vector2d set(double double1) {
		this.x = double1;
		this.y = double1;
		return this;
	}

	public Vector2d set(double double1, double double2) {
		this.x = double1;
		this.y = double2;
		return this;
	}

	public Vector2d set(Vector2dc vector2dc) {
		this.x = vector2dc.x();
		this.y = vector2dc.y();
		return this;
	}

	public Vector2d set(Vector2fc vector2fc) {
		this.x = (double)vector2fc.x();
		this.y = (double)vector2fc.y();
		return this;
	}

	public Vector2d set(Vector2ic vector2ic) {
		this.x = (double)vector2ic.x();
		this.y = (double)vector2ic.y();
		return this;
	}

	public Vector2d set(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
		return this;
	}

	public Vector2d set(float[] floatArray) {
		this.x = (double)floatArray[0];
		this.y = (double)floatArray[1];
		return this;
	}

	public Vector2d set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Vector2d set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector2d set(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
		return this;
	}

	public Vector2d set(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, int1, doubleBuffer);
		return this;
	}

	public Vector2d setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
	}

	public double get(int int1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			return this.x;
		
		case 1: 
			return this.y;
		
		default: 
			throw new IllegalArgumentException();
		
		}
	}

	public Vector2i get(int int1, Vector2i vector2i) {
		vector2i.x = Math.roundUsing(this.x(), int1);
		vector2i.y = Math.roundUsing(this.y(), int1);
		return vector2i;
	}

	public Vector2f get(Vector2f vector2f) {
		vector2f.x = (float)this.x();
		vector2f.y = (float)this.y();
		return vector2f;
	}

	public Vector2d get(Vector2d vector2d) {
		vector2d.x = this.x();
		vector2d.y = this.y();
		return vector2d;
	}

	public Vector2d setComponent(int int1, double double1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			this.x = double1;
			break;
		
		case 1: 
			this.y = double1;
			break;
		
		default: 
			throw new IllegalArgumentException();
		
		}
		return this;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public DoubleBuffer get(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put(this, doubleBuffer.position(), doubleBuffer);
		return doubleBuffer;
	}

	public DoubleBuffer get(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public Vector2dc getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public Vector2d perpendicular() {
		double double1 = this.y;
		this.y = this.x * -1.0;
		this.x = double1;
		return this;
	}

	public Vector2d sub(Vector2dc vector2dc) {
		this.x -= vector2dc.x();
		this.y -= vector2dc.y();
		return this;
	}

	public Vector2d sub(double double1, double double2) {
		this.x -= double1;
		this.y -= double2;
		return this;
	}

	public Vector2d sub(double double1, double double2, Vector2d vector2d) {
		vector2d.x = this.x - double1;
		vector2d.y = this.y - double2;
		return vector2d;
	}

	public Vector2d sub(Vector2fc vector2fc) {
		this.x -= (double)vector2fc.x();
		this.y -= (double)vector2fc.y();
		return this;
	}

	public Vector2d sub(Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.x = this.x - vector2dc.x();
		vector2d.y = this.y - vector2dc.y();
		return vector2d;
	}

	public Vector2d sub(Vector2fc vector2fc, Vector2d vector2d) {
		vector2d.x = this.x - (double)vector2fc.x();
		vector2d.y = this.y - (double)vector2fc.y();
		return vector2d;
	}

	public Vector2d mul(double double1) {
		this.x *= double1;
		this.y *= double1;
		return this;
	}

	public Vector2d mul(double double1, Vector2d vector2d) {
		vector2d.x = this.x * double1;
		vector2d.y = this.y * double1;
		return vector2d;
	}

	public Vector2d mul(double double1, double double2) {
		this.x *= double1;
		this.y *= double2;
		return this;
	}

	public Vector2d mul(double double1, double double2, Vector2d vector2d) {
		vector2d.x = this.x * double1;
		vector2d.y = this.y * double2;
		return vector2d;
	}

	public Vector2d mul(Vector2dc vector2dc) {
		this.x *= vector2dc.x();
		this.y *= vector2dc.y();
		return this;
	}

	public Vector2d mul(Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.x = this.x * vector2dc.x();
		vector2d.y = this.y * vector2dc.y();
		return vector2d;
	}

	public Vector2d div(double double1) {
		double double2 = 1.0 / double1;
		this.x *= double2;
		this.y *= double2;
		return this;
	}

	public Vector2d div(double double1, Vector2d vector2d) {
		double double2 = 1.0 / double1;
		vector2d.x = this.x * double2;
		vector2d.y = this.y * double2;
		return vector2d;
	}

	public Vector2d div(double double1, double double2) {
		this.x /= double1;
		this.y /= double2;
		return this;
	}

	public Vector2d div(double double1, double double2, Vector2d vector2d) {
		vector2d.x = this.x / double1;
		vector2d.y = this.y / double2;
		return vector2d;
	}

	public Vector2d div(Vector2d vector2d) {
		this.x /= vector2d.x();
		this.y /= vector2d.y();
		return this;
	}

	public Vector2d div(Vector2fc vector2fc) {
		this.x /= (double)vector2fc.x();
		this.y /= (double)vector2fc.y();
		return this;
	}

	public Vector2d div(Vector2fc vector2fc, Vector2d vector2d) {
		vector2d.x = this.x / (double)vector2fc.x();
		vector2d.y = this.y / (double)vector2fc.y();
		return vector2d;
	}

	public Vector2d div(Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.x = this.x / vector2dc.x();
		vector2d.y = this.y / vector2dc.y();
		return vector2d;
	}

	public Vector2d mul(Matrix2fc matrix2fc) {
		double double1 = (double)matrix2fc.m00() * this.x + (double)matrix2fc.m10() * this.y;
		double double2 = (double)matrix2fc.m01() * this.x + (double)matrix2fc.m11() * this.y;
		this.x = double1;
		this.y = double2;
		return this;
	}

	public Vector2d mul(Matrix2dc matrix2dc) {
		double double1 = matrix2dc.m00() * this.x + matrix2dc.m10() * this.y;
		double double2 = matrix2dc.m01() * this.x + matrix2dc.m11() * this.y;
		this.x = double1;
		this.y = double2;
		return this;
	}

	public Vector2d mul(Matrix2dc matrix2dc, Vector2d vector2d) {
		double double1 = matrix2dc.m00() * this.x + matrix2dc.m10() * this.y;
		double double2 = matrix2dc.m01() * this.x + matrix2dc.m11() * this.y;
		vector2d.x = double1;
		vector2d.y = double2;
		return vector2d;
	}

	public Vector2d mul(Matrix2fc matrix2fc, Vector2d vector2d) {
		double double1 = (double)matrix2fc.m00() * this.x + (double)matrix2fc.m10() * this.y;
		double double2 = (double)matrix2fc.m01() * this.x + (double)matrix2fc.m11() * this.y;
		vector2d.x = double1;
		vector2d.y = double2;
		return vector2d;
	}

	public Vector2d mulTranspose(Matrix2dc matrix2dc) {
		double double1 = matrix2dc.m00() * this.x + matrix2dc.m01() * this.y;
		double double2 = matrix2dc.m10() * this.x + matrix2dc.m11() * this.y;
		this.x = double1;
		this.y = double2;
		return this;
	}

	public Vector2d mulTranspose(Matrix2dc matrix2dc, Vector2d vector2d) {
		double double1 = matrix2dc.m00() * this.x + matrix2dc.m01() * this.y;
		double double2 = matrix2dc.m10() * this.x + matrix2dc.m11() * this.y;
		vector2d.x = double1;
		vector2d.y = double2;
		return vector2d;
	}

	public Vector2d mulTranspose(Matrix2fc matrix2fc) {
		double double1 = (double)matrix2fc.m00() * this.x + (double)matrix2fc.m01() * this.y;
		double double2 = (double)matrix2fc.m10() * this.x + (double)matrix2fc.m11() * this.y;
		this.x = double1;
		this.y = double2;
		return this;
	}

	public Vector2d mulTranspose(Matrix2fc matrix2fc, Vector2d vector2d) {
		double double1 = (double)matrix2fc.m00() * this.x + (double)matrix2fc.m01() * this.y;
		double double2 = (double)matrix2fc.m10() * this.x + (double)matrix2fc.m11() * this.y;
		vector2d.x = double1;
		vector2d.y = double2;
		return vector2d;
	}

	public Vector2d mulPosition(Matrix3x2dc matrix3x2dc) {
		double double1 = matrix3x2dc.m00() * this.x + matrix3x2dc.m10() * this.y + matrix3x2dc.m20();
		double double2 = matrix3x2dc.m01() * this.x + matrix3x2dc.m11() * this.y + matrix3x2dc.m21();
		this.x = double1;
		this.y = double2;
		return this;
	}

	public Vector2d mulPosition(Matrix3x2dc matrix3x2dc, Vector2d vector2d) {
		double double1 = matrix3x2dc.m00() * this.x + matrix3x2dc.m10() * this.y + matrix3x2dc.m20();
		double double2 = matrix3x2dc.m01() * this.x + matrix3x2dc.m11() * this.y + matrix3x2dc.m21();
		vector2d.x = double1;
		vector2d.y = double2;
		return vector2d;
	}

	public Vector2d mulDirection(Matrix3x2dc matrix3x2dc) {
		double double1 = matrix3x2dc.m00() * this.x + matrix3x2dc.m10() * this.y;
		double double2 = matrix3x2dc.m01() * this.x + matrix3x2dc.m11() * this.y;
		this.x = double1;
		this.y = double2;
		return this;
	}

	public Vector2d mulDirection(Matrix3x2dc matrix3x2dc, Vector2d vector2d) {
		double double1 = matrix3x2dc.m00() * this.x + matrix3x2dc.m10() * this.y;
		double double2 = matrix3x2dc.m01() * this.x + matrix3x2dc.m11() * this.y;
		vector2d.x = double1;
		vector2d.y = double2;
		return vector2d;
	}

	public double dot(Vector2dc vector2dc) {
		return this.x * vector2dc.x() + this.y * vector2dc.y();
	}

	public double angle(Vector2dc vector2dc) {
		double double1 = this.x * vector2dc.x() + this.y * vector2dc.y();
		double double2 = this.x * vector2dc.y() - this.y * vector2dc.x();
		return Math.atan2(double2, double1);
	}

	public double lengthSquared() {
		return this.x * this.x + this.y * this.y;
	}

	public static double lengthSquared(double double1, double double2) {
		return double1 * double1 + double2 * double2;
	}

	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public static double length(double double1, double double2) {
		return Math.sqrt(double1 * double1 + double2 * double2);
	}

	public double distance(Vector2dc vector2dc) {
		double double1 = this.x - vector2dc.x();
		double double2 = this.y - vector2dc.y();
		return Math.sqrt(double1 * double1 + double2 * double2);
	}

	public double distanceSquared(Vector2dc vector2dc) {
		double double1 = this.x - vector2dc.x();
		double double2 = this.y - vector2dc.y();
		return double1 * double1 + double2 * double2;
	}

	public double distance(Vector2fc vector2fc) {
		double double1 = this.x - (double)vector2fc.x();
		double double2 = this.y - (double)vector2fc.y();
		return Math.sqrt(double1 * double1 + double2 * double2);
	}

	public double distanceSquared(Vector2fc vector2fc) {
		double double1 = this.x - (double)vector2fc.x();
		double double2 = this.y - (double)vector2fc.y();
		return double1 * double1 + double2 * double2;
	}

	public double distance(double double1, double double2) {
		double double3 = this.x - double1;
		double double4 = this.y - double2;
		return Math.sqrt(double3 * double3 + double4 * double4);
	}

	public double distanceSquared(double double1, double double2) {
		double double3 = this.x - double1;
		double double4 = this.y - double2;
		return double3 * double3 + double4 * double4;
	}

	public static double distance(double double1, double double2, double double3, double double4) {
		double double5 = double1 - double3;
		double double6 = double2 - double4;
		return Math.sqrt(double5 * double5 + double6 * double6);
	}

	public static double distanceSquared(double double1, double double2, double double3, double double4) {
		double double5 = double1 - double3;
		double double6 = double2 - double4;
		return double5 * double5 + double6 * double6;
	}

	public Vector2d normalize() {
		double double1 = Math.invsqrt(this.x * this.x + this.y * this.y);
		this.x *= double1;
		this.y *= double1;
		return this;
	}

	public Vector2d normalize(Vector2d vector2d) {
		double double1 = Math.invsqrt(this.x * this.x + this.y * this.y);
		vector2d.x = this.x * double1;
		vector2d.y = this.y * double1;
		return vector2d;
	}

	public Vector2d normalize(double double1) {
		double double2 = Math.invsqrt(this.x * this.x + this.y * this.y) * double1;
		this.x *= double2;
		this.y *= double2;
		return this;
	}

	public Vector2d normalize(double double1, Vector2d vector2d) {
		double double2 = Math.invsqrt(this.x * this.x + this.y * this.y) * double1;
		vector2d.x = this.x * double2;
		vector2d.y = this.y * double2;
		return vector2d;
	}

	public Vector2d add(Vector2dc vector2dc) {
		this.x += vector2dc.x();
		this.y += vector2dc.y();
		return this;
	}

	public Vector2d add(double double1, double double2) {
		this.x += double1;
		this.y += double2;
		return this;
	}

	public Vector2d add(double double1, double double2, Vector2d vector2d) {
		vector2d.x = this.x + double1;
		vector2d.y = this.y + double2;
		return vector2d;
	}

	public Vector2d add(Vector2fc vector2fc) {
		this.x += (double)vector2fc.x();
		this.y += (double)vector2fc.y();
		return this;
	}

	public Vector2d add(Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.x = this.x + vector2dc.x();
		vector2d.y = this.y + vector2dc.y();
		return vector2d;
	}

	public Vector2d add(Vector2fc vector2fc, Vector2d vector2d) {
		vector2d.x = this.x + (double)vector2fc.x();
		vector2d.y = this.y + (double)vector2fc.y();
		return vector2d;
	}

	public Vector2d zero() {
		this.x = 0.0;
		this.y = 0.0;
		return this;
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeDouble(this.x);
		objectOutput.writeDouble(this.y);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readDouble();
		this.y = objectInput.readDouble();
	}

	public Vector2d negate() {
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}

	public Vector2d negate(Vector2d vector2d) {
		vector2d.x = -this.x;
		vector2d.y = -this.y;
		return vector2d;
	}

	public Vector2d lerp(Vector2dc vector2dc, double double1) {
		this.x += (vector2dc.x() - this.x) * double1;
		this.y += (vector2dc.y() - this.y) * double1;
		return this;
	}

	public Vector2d lerp(Vector2dc vector2dc, double double1, Vector2d vector2d) {
		vector2d.x = this.x + (vector2dc.x() - this.x) * double1;
		vector2d.y = this.y + (vector2dc.y() - this.y) * double1;
		return vector2d;
	}

	public int hashCode() {
		byte byte1 = 1;
		long long1 = Double.doubleToLongBits(this.x);
		int int1 = 31 * byte1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.y);
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
			Vector2d vector2d = (Vector2d)object;
			if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(vector2d.x)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.y) == Double.doubleToLongBits(vector2d.y);
			}
		}
	}

	public boolean equals(Vector2dc vector2dc, double double1) {
		if (this == vector2dc) {
			return true;
		} else if (vector2dc == null) {
			return false;
		} else if (!(vector2dc instanceof Vector2dc)) {
			return false;
		} else if (!Runtime.equals(this.x, vector2dc.x(), double1)) {
			return false;
		} else {
			return Runtime.equals(this.y, vector2dc.y(), double1);
		}
	}

	public boolean equals(double double1, double double2) {
		if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(double1)) {
			return false;
		} else {
			return Double.doubleToLongBits(this.y) == Double.doubleToLongBits(double2);
		}
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format(this.x, numberFormat);
		return "(" + string + " " + Runtime.format(this.y, numberFormat) + ")";
	}

	public Vector2d fma(Vector2dc vector2dc, Vector2dc vector2dc2) {
		this.x += vector2dc.x() * vector2dc2.x();
		this.y += vector2dc.y() * vector2dc2.y();
		return this;
	}

	public Vector2d fma(double double1, Vector2dc vector2dc) {
		this.x += double1 * vector2dc.x();
		this.y += double1 * vector2dc.y();
		return this;
	}

	public Vector2d fma(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2d vector2d) {
		vector2d.x = this.x + vector2dc.x() * vector2dc2.x();
		vector2d.y = this.y + vector2dc.y() * vector2dc2.y();
		return vector2d;
	}

	public Vector2d fma(double double1, Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.x = this.x + double1 * vector2dc.x();
		vector2d.y = this.y + double1 * vector2dc.y();
		return vector2d;
	}

	public Vector2d min(Vector2dc vector2dc) {
		this.x = this.x < vector2dc.x() ? this.x : vector2dc.x();
		this.y = this.y < vector2dc.y() ? this.y : vector2dc.y();
		return this;
	}

	public Vector2d min(Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.x = this.x < vector2dc.x() ? this.x : vector2dc.x();
		vector2d.y = this.y < vector2dc.y() ? this.y : vector2dc.y();
		return vector2d;
	}

	public Vector2d max(Vector2dc vector2dc) {
		this.x = this.x > vector2dc.x() ? this.x : vector2dc.x();
		this.y = this.y > vector2dc.y() ? this.y : vector2dc.y();
		return this;
	}

	public Vector2d max(Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.x = this.x > vector2dc.x() ? this.x : vector2dc.x();
		vector2d.y = this.y > vector2dc.y() ? this.y : vector2dc.y();
		return vector2d;
	}

	public int maxComponent() {
		double double1 = Math.abs(this.x);
		double double2 = Math.abs(this.y);
		return double1 >= double2 ? 0 : 1;
	}

	public int minComponent() {
		double double1 = Math.abs(this.x);
		double double2 = Math.abs(this.y);
		return double1 < double2 ? 0 : 1;
	}

	public Vector2d floor() {
		this.x = Math.floor(this.x);
		this.y = Math.floor(this.y);
		return this;
	}

	public Vector2d floor(Vector2d vector2d) {
		vector2d.x = Math.floor(this.x);
		vector2d.y = Math.floor(this.y);
		return vector2d;
	}

	public Vector2d ceil() {
		this.x = Math.ceil(this.x);
		this.y = Math.ceil(this.y);
		return this;
	}

	public Vector2d ceil(Vector2d vector2d) {
		vector2d.x = Math.ceil(this.x);
		vector2d.y = Math.ceil(this.y);
		return vector2d;
	}

	public Vector2d round() {
		this.x = (double)Math.round(this.x);
		this.y = (double)Math.round(this.y);
		return this;
	}

	public Vector2d round(Vector2d vector2d) {
		vector2d.x = (double)Math.round(this.x);
		vector2d.y = (double)Math.round(this.y);
		return vector2d;
	}

	public boolean isFinite() {
		return Math.isFinite(this.x) && Math.isFinite(this.y);
	}

	public Vector2d absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		return this;
	}

	public Vector2d absolute(Vector2d vector2d) {
		vector2d.x = Math.abs(this.x);
		vector2d.y = Math.abs(this.y);
		return vector2d;
	}
}
