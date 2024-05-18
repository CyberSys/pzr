package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Vector4d implements Externalizable,Vector4dc {
	private static final long serialVersionUID = 1L;
	public double x;
	public double y;
	public double z;
	public double w;

	public Vector4d() {
		this.w = 1.0;
	}

	public Vector4d(Vector4dc vector4dc) {
		this.x = vector4dc.x();
		this.y = vector4dc.y();
		this.z = vector4dc.z();
		this.w = vector4dc.w();
	}

	public Vector4d(Vector3dc vector3dc, double double1) {
		this.x = vector3dc.x();
		this.y = vector3dc.y();
		this.z = vector3dc.z();
		this.w = double1;
	}

	public Vector4d(Vector2dc vector2dc, double double1, double double2) {
		this.x = vector2dc.x();
		this.y = vector2dc.y();
		this.z = double1;
		this.w = double2;
	}

	public Vector4d(double double1) {
		this(double1, double1, double1, double1);
	}

	public Vector4d(Vector4fc vector4fc) {
		this.x = (double)vector4fc.x();
		this.y = (double)vector4fc.y();
		this.z = (double)vector4fc.z();
		this.w = (double)vector4fc.w();
	}

	public Vector4d(Vector3fc vector3fc, double double1) {
		this.x = (double)vector3fc.x();
		this.y = (double)vector3fc.y();
		this.z = (double)vector3fc.z();
		this.w = double1;
	}

	public Vector4d(Vector2fc vector2fc, double double1, double double2) {
		this.x = (double)vector2fc.x();
		this.y = (double)vector2fc.y();
		this.z = double1;
		this.w = double2;
	}

	public Vector4d(double double1, double double2, double double3, double double4) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		this.w = double4;
	}

	public Vector4d(ByteBuffer byteBuffer) {
		this(byteBuffer.position(), byteBuffer);
	}

	public Vector4d(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector4d(DoubleBuffer doubleBuffer) {
		this(doubleBuffer.position(), doubleBuffer);
	}

	public Vector4d(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, int1, doubleBuffer);
	}

	public double x() {
		return this.x;
	}

	public double y() {
		return this.y;
	}

	public double z() {
		return this.z;
	}

	public double w() {
		return this.w;
	}

	public Vector4d set(Vector4dc vector4dc) {
		this.x = vector4dc.x();
		this.y = vector4dc.y();
		this.z = vector4dc.z();
		this.w = vector4dc.w();
		return this;
	}

	public Vector4d set(Vector4fc vector4fc) {
		this.x = (double)vector4fc.x();
		this.y = (double)vector4fc.y();
		this.z = (double)vector4fc.z();
		this.w = (double)vector4fc.w();
		return this;
	}

	public Vector4d set(Vector3dc vector3dc, double double1) {
		this.x = vector3dc.x();
		this.y = vector3dc.y();
		this.z = vector3dc.z();
		this.w = double1;
		return this;
	}

	public Vector4d set(Vector3fc vector3fc, double double1) {
		this.x = (double)vector3fc.x();
		this.y = (double)vector3fc.y();
		this.z = (double)vector3fc.z();
		this.w = double1;
		return this;
	}

	public Vector4d set(Vector2dc vector2dc, double double1, double double2) {
		this.x = vector2dc.x();
		this.y = vector2dc.y();
		this.z = double1;
		this.w = double2;
		return this;
	}

	public Vector4d set(double double1) {
		return this.set(double1, double1, double1, double1);
	}

	public Vector4d set(Vector2fc vector2fc, double double1, double double2) {
		this.x = (double)vector2fc.x();
		this.y = (double)vector2fc.y();
		this.z = double1;
		this.w = double2;
		return this;
	}

	public Vector4d set(double double1, double double2, double double3, double double4) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		this.w = double4;
		return this;
	}

	public Vector4d set(ByteBuffer byteBuffer) {
		return this.set(byteBuffer.position(), byteBuffer);
	}

	public Vector4d set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector4d set(DoubleBuffer doubleBuffer) {
		return this.set(doubleBuffer.position(), doubleBuffer);
	}

	public Vector4d set(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, int1, doubleBuffer);
		return this;
	}

	public Vector4d setComponent(int int1, double double1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			this.x = double1;
			break;
		
		case 1: 
			this.y = double1;
			break;
		
		case 2: 
			this.z = double1;
			break;
		
		case 3: 
			this.w = double1;
			break;
		
		default: 
			throw new IllegalArgumentException();
		
		}
		return this;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		return this.get(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public DoubleBuffer get(DoubleBuffer doubleBuffer) {
		return this.get(doubleBuffer.position(), doubleBuffer);
	}

	public DoubleBuffer get(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public Vector4d sub(Vector4dc vector4dc) {
		this.x -= vector4dc.x();
		this.y -= vector4dc.y();
		this.z -= vector4dc.z();
		this.w -= vector4dc.w();
		return this;
	}

	public Vector4d sub(Vector4fc vector4fc) {
		this.x -= (double)vector4fc.x();
		this.y -= (double)vector4fc.y();
		this.z -= (double)vector4fc.z();
		this.w -= (double)vector4fc.w();
		return this;
	}

	public Vector4d sub(double double1, double double2, double double3, double double4) {
		this.x -= double1;
		this.y -= double2;
		this.z -= double3;
		this.w -= double4;
		return this;
	}

	public Vector4d sub(double double1, double double2, double double3, double double4, Vector4d vector4d) {
		vector4d.x = this.x - double1;
		vector4d.y = this.y - double2;
		vector4d.z = this.z - double3;
		vector4d.w = this.w - double4;
		return vector4d;
	}

	public Vector4d add(Vector4dc vector4dc) {
		this.x += vector4dc.x();
		this.y += vector4dc.y();
		this.z += vector4dc.z();
		this.w += vector4dc.w();
		return this;
	}

	public Vector4d add(double double1, double double2, double double3, double double4) {
		this.x += double1;
		this.y += double2;
		this.z += double3;
		this.w += double4;
		return this;
	}

	public Vector4d add(double double1, double double2, double double3, double double4, Vector4d vector4d) {
		vector4d.x = this.x - double1;
		vector4d.y = this.y - double2;
		vector4d.z = this.z - double3;
		vector4d.w = this.w - double4;
		return vector4d;
	}

	public Vector4d add(Vector4fc vector4fc) {
		this.x += (double)vector4fc.x();
		this.y += (double)vector4fc.y();
		this.z += (double)vector4fc.z();
		this.w += (double)vector4fc.w();
		return this;
	}

	public Vector4d fma(Vector4dc vector4dc, Vector4dc vector4dc2) {
		this.x += vector4dc.x() * vector4dc2.x();
		this.y += vector4dc.y() * vector4dc2.y();
		this.z += vector4dc.z() * vector4dc2.z();
		this.w += vector4dc.w() * vector4dc2.w();
		return this;
	}

	public Vector4d fma(double double1, Vector4dc vector4dc) {
		this.x += double1 * vector4dc.x();
		this.y += double1 * vector4dc.y();
		this.z += double1 * vector4dc.z();
		this.w += double1 * vector4dc.w();
		return this;
	}

	public Vector4d fma(Vector4dc vector4dc, Vector4dc vector4dc2, Vector4d vector4d) {
		vector4d.x = this.x + vector4dc.x() * vector4dc2.x();
		vector4d.y = this.y + vector4dc.y() * vector4dc2.y();
		vector4d.z = this.z + vector4dc.z() * vector4dc2.z();
		vector4d.w = this.w + vector4dc.w() * vector4dc2.w();
		return vector4d;
	}

	public Vector4d fma(double double1, Vector4dc vector4dc, Vector4d vector4d) {
		vector4d.x = this.x + double1 * vector4dc.x();
		vector4d.y = this.y + double1 * vector4dc.y();
		vector4d.z = this.z + double1 * vector4dc.z();
		vector4d.w = this.w + double1 * vector4dc.w();
		return vector4d;
	}

	public Vector4d mul(Vector4dc vector4dc) {
		this.x *= vector4dc.x();
		this.y *= vector4dc.y();
		this.z *= vector4dc.z();
		this.z *= vector4dc.w();
		return this;
	}

	public Vector4d mul(Vector4dc vector4dc, Vector4d vector4d) {
		vector4d.x = this.x * vector4dc.x();
		vector4d.y = this.y * vector4dc.y();
		vector4d.z = this.z * vector4dc.z();
		vector4d.w = this.w * vector4dc.w();
		return vector4d;
	}

	public Vector4d div(Vector4dc vector4dc) {
		this.x /= vector4dc.x();
		this.y /= vector4dc.y();
		this.z /= vector4dc.z();
		this.z /= vector4dc.w();
		return this;
	}

	public Vector4d div(Vector4dc vector4dc, Vector4d vector4d) {
		vector4d.x = this.x / vector4dc.x();
		vector4d.y = this.y / vector4dc.y();
		vector4d.z = this.z / vector4dc.z();
		vector4d.w = this.w / vector4dc.w();
		return vector4d;
	}

	public Vector4d mul(Vector4fc vector4fc) {
		this.x *= (double)vector4fc.x();
		this.y *= (double)vector4fc.y();
		this.z *= (double)vector4fc.z();
		this.z *= (double)vector4fc.w();
		return this;
	}

	public Vector4d mul(Matrix4dc matrix4dc) {
		return this.mul(matrix4dc, this);
	}

	public Vector4d mul(Matrix4dc matrix4dc, Vector4d vector4d) {
		vector4d.set(matrix4dc.m00() * this.x + matrix4dc.m10() * this.y + matrix4dc.m20() * this.z + matrix4dc.m30() * this.w, matrix4dc.m01() * this.x + matrix4dc.m11() * this.y + matrix4dc.m21() * this.z + matrix4dc.m31() * this.w, matrix4dc.m02() * this.x + matrix4dc.m12() * this.y + matrix4dc.m22() * this.z + matrix4dc.m32() * this.w, matrix4dc.m03() * this.x + matrix4dc.m13() * this.y + matrix4dc.m23() * this.z + matrix4dc.m33() * this.w);
		return vector4d;
	}

	public Vector4d mul(Matrix4x3dc matrix4x3dc) {
		return this.mul(matrix4x3dc, this);
	}

	public Vector4d mul(Matrix4x3dc matrix4x3dc, Vector4d vector4d) {
		vector4d.set(matrix4x3dc.m00() * this.x + matrix4x3dc.m10() * this.y + matrix4x3dc.m20() * this.z + matrix4x3dc.m30() * this.w, matrix4x3dc.m01() * this.x + matrix4x3dc.m11() * this.y + matrix4x3dc.m21() * this.z + matrix4x3dc.m31() * this.w, matrix4x3dc.m02() * this.x + matrix4x3dc.m12() * this.y + matrix4x3dc.m22() * this.z + matrix4x3dc.m32() * this.w, this.w);
		return vector4d;
	}

	public Vector4d mul(Matrix4x3fc matrix4x3fc) {
		return this.mul(matrix4x3fc, this);
	}

	public Vector4d mul(Matrix4x3fc matrix4x3fc, Vector4d vector4d) {
		vector4d.set((double)matrix4x3fc.m00() * this.x + (double)matrix4x3fc.m10() * this.y + (double)matrix4x3fc.m20() * this.z + (double)matrix4x3fc.m30() * this.w, (double)matrix4x3fc.m01() * this.x + (double)matrix4x3fc.m11() * this.y + (double)matrix4x3fc.m21() * this.z + (double)matrix4x3fc.m31() * this.w, (double)matrix4x3fc.m02() * this.x + (double)matrix4x3fc.m12() * this.y + (double)matrix4x3fc.m22() * this.z + (double)matrix4x3fc.m32() * this.w, this.w);
		return vector4d;
	}

	public Vector4d mul(Matrix4fc matrix4fc) {
		return this.mul(matrix4fc, this);
	}

	public Vector4d mul(Matrix4fc matrix4fc, Vector4d vector4d) {
		vector4d.set((double)matrix4fc.m00() * this.x + (double)matrix4fc.m10() * this.y + (double)matrix4fc.m20() * this.z + (double)matrix4fc.m30() * this.w, (double)matrix4fc.m01() * this.x + (double)matrix4fc.m11() * this.y + (double)matrix4fc.m21() * this.z + (double)matrix4fc.m31() * this.w, (double)matrix4fc.m02() * this.x + (double)matrix4fc.m12() * this.y + (double)matrix4fc.m22() * this.z + (double)matrix4fc.m32() * this.w, (double)matrix4fc.m03() * this.x + (double)matrix4fc.m13() * this.y + (double)matrix4fc.m23() * this.z + (double)matrix4fc.m33() * this.w);
		return vector4d;
	}

	public Vector4d mulProject(Matrix4dc matrix4dc, Vector4d vector4d) {
		double double1 = 1.0 / (matrix4dc.m03() * this.x + matrix4dc.m13() * this.y + matrix4dc.m23() * this.z + matrix4dc.m33() * this.w);
		vector4d.set((matrix4dc.m00() * this.x + matrix4dc.m10() * this.y + matrix4dc.m20() * this.z + matrix4dc.m30() * this.w) * double1, (matrix4dc.m01() * this.x + matrix4dc.m11() * this.y + matrix4dc.m21() * this.z + matrix4dc.m31() * this.w) * double1, (matrix4dc.m02() * this.x + matrix4dc.m12() * this.y + matrix4dc.m22() * this.z + matrix4dc.m32() * this.w) * double1, 1.0);
		return vector4d;
	}

	public Vector4d mulProject(Matrix4dc matrix4dc) {
		return this.mulProject(matrix4dc, this);
	}

	public Vector4d mul(double double1) {
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		this.w *= double1;
		return this;
	}

	public Vector4d mul(double double1, Vector4d vector4d) {
		vector4d.x = this.x * double1;
		vector4d.y = this.y * double1;
		vector4d.z = this.z * double1;
		vector4d.w = this.w * double1;
		return vector4d;
	}

	public Vector4d div(double double1) {
		this.x /= double1;
		this.y /= double1;
		this.z /= double1;
		this.w /= double1;
		return this;
	}

	public Vector4d div(double double1, Vector4d vector4d) {
		vector4d.x = this.x / double1;
		vector4d.y = this.y / double1;
		vector4d.z = this.z / double1;
		vector4d.w = this.w / double1;
		return vector4d;
	}

	public Vector4d rotate(Quaterniondc quaterniondc) {
		return this.rotate(quaterniondc, this);
	}

	public Vector4d rotate(Quaterniondc quaterniondc, Vector4d vector4d) {
		quaterniondc.transform((Vector4dc)this, (Vector4d)vector4d);
		return vector4d;
	}

	public double lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}

	public double length() {
		return Math.sqrt(this.lengthSquared());
	}

	public Vector4d normalize() {
		double double1 = 1.0 / this.length();
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		this.w *= double1;
		return this;
	}

	public Vector4d normalize(Vector4d vector4d) {
		double double1 = 1.0 / this.length();
		vector4d.x = this.x * double1;
		vector4d.y = this.y * double1;
		vector4d.z = this.z * double1;
		vector4d.w = this.w * double1;
		return vector4d;
	}

	public Vector4d normalize3() {
		double double1 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		this.w *= double1;
		return this;
	}

	public Vector4d normalize3(Vector4d vector4d) {
		double double1 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		vector4d.x = this.x * double1;
		vector4d.y = this.y * double1;
		vector4d.z = this.z * double1;
		vector4d.w = this.w * double1;
		return vector4d;
	}

	public double distance(Vector4dc vector4dc) {
		double double1 = vector4dc.x() - this.x;
		double double2 = vector4dc.y() - this.y;
		double double3 = vector4dc.z() - this.z;
		double double4 = vector4dc.w() - this.w;
		return Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3 + double4 * double4);
	}

	public double distance(double double1, double double2, double double3, double double4) {
		double double5 = this.x - double1;
		double double6 = this.y - double2;
		double double7 = this.z - double3;
		double double8 = this.w - double4;
		return Math.sqrt(double5 * double5 + double6 * double6 + double7 * double7 + double8 * double8);
	}

	public double dot(Vector4dc vector4dc) {
		return this.x * vector4dc.x() + this.y * vector4dc.y() + this.z * vector4dc.z() + this.w * vector4dc.w();
	}

	public double dot(double double1, double double2, double double3, double double4) {
		return this.x * double1 + this.y * double2 + this.z * double3 + this.w * double4;
	}

	public double angleCos(Vector4dc vector4dc) {
		double double1 = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
		double double2 = vector4dc.x() * vector4dc.x() + vector4dc.y() * vector4dc.y() + vector4dc.z() * vector4dc.z() + vector4dc.w() * vector4dc.w();
		double double3 = this.x * vector4dc.x() + this.y * vector4dc.y() + this.z * vector4dc.z() + this.w * vector4dc.w();
		return double3 / Math.sqrt(double1 * double2);
	}

	public double angle(Vector4dc vector4dc) {
		double double1 = this.angleCos(vector4dc);
		double1 = double1 < 1.0 ? double1 : 1.0;
		double1 = double1 > -1.0 ? double1 : -1.0;
		return Math.acos(double1);
	}

	public Vector4d zero() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
		this.w = 0.0;
		return this;
	}

	public Vector4d negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
		return this;
	}

	public Vector4d negate(Vector4d vector4d) {
		vector4d.x = -this.x;
		vector4d.y = -this.y;
		vector4d.z = -this.z;
		vector4d.w = -this.w;
		return vector4d;
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format(this.x) + " " + numberFormat.format(this.y) + " " + numberFormat.format(this.z) + " " + numberFormat.format(this.w) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeDouble(this.x);
		objectOutput.writeDouble(this.y);
		objectOutput.writeDouble(this.z);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readDouble();
		this.y = objectInput.readDouble();
		this.z = objectInput.readDouble();
	}

	public int hashCode() {
		byte byte1 = 1;
		long long1 = Double.doubleToLongBits(this.w);
		int int1 = 31 * byte1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.x);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.y);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.z);
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
			Vector4d vector4d = (Vector4d)object;
			if (Double.doubleToLongBits(this.w) != Double.doubleToLongBits(vector4d.w)) {
				return false;
			} else if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(vector4d.x)) {
				return false;
			} else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(vector4d.y)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(vector4d.z);
			}
		}
	}

	public Vector4d smoothStep(Vector4dc vector4dc, double double1, Vector4d vector4d) {
		double double2 = double1 * double1;
		double double3 = double2 * double1;
		vector4d.x = (this.x + this.x - vector4dc.x() - vector4dc.x()) * double3 + (3.0 * vector4dc.x() - 3.0 * this.x) * double2 + this.x * double1 + this.x;
		vector4d.y = (this.y + this.y - vector4dc.y() - vector4dc.y()) * double3 + (3.0 * vector4dc.y() - 3.0 * this.y) * double2 + this.y * double1 + this.y;
		vector4d.z = (this.z + this.z - vector4dc.z() - vector4dc.z()) * double3 + (3.0 * vector4dc.z() - 3.0 * this.z) * double2 + this.z * double1 + this.z;
		vector4d.w = (this.w + this.w - vector4dc.w() - vector4dc.w()) * double3 + (3.0 * vector4dc.w() - 3.0 * this.w) * double2 + this.w * double1 + this.w;
		return vector4d;
	}

	public Vector4d hermite(Vector4dc vector4dc, Vector4dc vector4dc2, Vector4dc vector4dc3, double double1, Vector4d vector4d) {
		double double2 = double1 * double1;
		double double3 = double2 * double1;
		vector4d.x = (this.x + this.x - vector4dc2.x() - vector4dc2.x() + vector4dc3.x() + vector4dc.x()) * double3 + (3.0 * vector4dc2.x() - 3.0 * this.x - vector4dc.x() - vector4dc.x() - vector4dc3.x()) * double2 + this.x * double1 + this.x;
		vector4d.y = (this.y + this.y - vector4dc2.y() - vector4dc2.y() + vector4dc3.y() + vector4dc.y()) * double3 + (3.0 * vector4dc2.y() - 3.0 * this.y - vector4dc.y() - vector4dc.y() - vector4dc3.y()) * double2 + this.y * double1 + this.y;
		vector4d.z = (this.z + this.z - vector4dc2.z() - vector4dc2.z() + vector4dc3.z() + vector4dc.z()) * double3 + (3.0 * vector4dc2.z() - 3.0 * this.z - vector4dc.z() - vector4dc.z() - vector4dc3.z()) * double2 + this.z * double1 + this.z;
		vector4d.w = (this.w + this.w - vector4dc2.w() - vector4dc2.w() + vector4dc3.w() + vector4dc.w()) * double3 + (3.0 * vector4dc2.w() - 3.0 * this.w - vector4dc.w() - vector4dc.w() - vector4dc3.w()) * double2 + this.w * double1 + this.w;
		return vector4d;
	}

	public Vector4d lerp(Vector4dc vector4dc, double double1) {
		return this.lerp(vector4dc, double1, this);
	}

	public Vector4d lerp(Vector4dc vector4dc, double double1, Vector4d vector4d) {
		vector4d.x = this.x + (vector4dc.x() - this.x) * double1;
		vector4d.y = this.y + (vector4dc.y() - this.y) * double1;
		vector4d.z = this.z + (vector4dc.z() - this.z) * double1;
		vector4d.w = this.w + (vector4dc.w() - this.w) * double1;
		return vector4d;
	}

	public Vector4dc toImmutable() {
		return (Vector4dc)(!Options.DEBUG ? this : new Vector4d.Proxy(this));
	}

	private final class Proxy implements Vector4dc {
		private final Vector4dc delegate;

		Proxy(Vector4dc vector4dc) {
			this.delegate = vector4dc;
		}

		public double x() {
			return this.delegate.x();
		}

		public double y() {
			return this.delegate.y();
		}

		public double z() {
			return this.delegate.z();
		}

		public double w() {
			return this.delegate.w();
		}

		public ByteBuffer get(ByteBuffer byteBuffer) {
			return this.delegate.get(byteBuffer);
		}

		public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
			return this.delegate.get(int1, byteBuffer);
		}

		public DoubleBuffer get(DoubleBuffer doubleBuffer) {
			return this.delegate.get(doubleBuffer);
		}

		public DoubleBuffer get(int int1, DoubleBuffer doubleBuffer) {
			return this.delegate.get(int1, doubleBuffer);
		}

		public Vector4d sub(double double1, double double2, double double3, double double4, Vector4d vector4d) {
			return this.delegate.sub(double1, double2, double3, double4, vector4d);
		}

		public Vector4d add(double double1, double double2, double double3, double double4, Vector4d vector4d) {
			return this.delegate.add(double1, double2, double3, double4, vector4d);
		}

		public Vector4d fma(Vector4dc vector4dc, Vector4dc vector4dc2, Vector4d vector4d) {
			return this.delegate.fma(vector4dc, vector4dc2, vector4d);
		}

		public Vector4d fma(double double1, Vector4dc vector4dc, Vector4d vector4d) {
			return this.delegate.fma(double1, vector4dc, vector4d);
		}

		public Vector4d mul(Vector4dc vector4dc, Vector4d vector4d) {
			return this.delegate.mul(vector4dc, vector4d);
		}

		public Vector4d div(Vector4dc vector4dc, Vector4d vector4d) {
			return this.delegate.div(vector4dc, vector4d);
		}

		public Vector4d mul(Matrix4dc matrix4dc, Vector4d vector4d) {
			return this.delegate.mul(matrix4dc, vector4d);
		}

		public Vector4d mul(Matrix4x3dc matrix4x3dc, Vector4d vector4d) {
			return this.delegate.mul(matrix4x3dc, vector4d);
		}

		public Vector4d mul(Matrix4x3fc matrix4x3fc, Vector4d vector4d) {
			return this.delegate.mul(matrix4x3fc, vector4d);
		}

		public Vector4d mul(Matrix4fc matrix4fc, Vector4d vector4d) {
			return this.delegate.mul(matrix4fc, vector4d);
		}

		public Vector4d mulProject(Matrix4dc matrix4dc, Vector4d vector4d) {
			return this.delegate.mulProject(matrix4dc, vector4d);
		}

		public Vector4d mul(double double1, Vector4d vector4d) {
			return this.delegate.mul(double1, vector4d);
		}

		public Vector4d div(double double1, Vector4d vector4d) {
			return this.delegate.div(double1, vector4d);
		}

		public Vector4d rotate(Quaterniondc quaterniondc, Vector4d vector4d) {
			return this.delegate.rotate(quaterniondc, vector4d);
		}

		public double lengthSquared() {
			return this.delegate.lengthSquared();
		}

		public double length() {
			return this.delegate.length();
		}

		public Vector4d normalize(Vector4d vector4d) {
			return this.delegate.normalize(vector4d);
		}

		public Vector4d normalize3(Vector4d vector4d) {
			return this.delegate.normalize3(vector4d);
		}

		public double distance(Vector4dc vector4dc) {
			return this.delegate.distance(vector4dc);
		}

		public double distance(double double1, double double2, double double3, double double4) {
			return this.delegate.distance(double1, double2, double3, double4);
		}

		public double dot(Vector4dc vector4dc) {
			return this.delegate.dot(vector4dc);
		}

		public double dot(double double1, double double2, double double3, double double4) {
			return this.delegate.dot(double1, double2, double3, double4);
		}

		public double angleCos(Vector4dc vector4dc) {
			return this.delegate.angleCos(vector4dc);
		}

		public double angle(Vector4dc vector4dc) {
			return this.delegate.angle(vector4dc);
		}

		public Vector4d negate(Vector4d vector4d) {
			return this.delegate.negate(vector4d);
		}

		public Vector4d smoothStep(Vector4dc vector4dc, double double1, Vector4d vector4d) {
			return this.delegate.smoothStep(vector4dc, double1, vector4d);
		}

		public Vector4d hermite(Vector4dc vector4dc, Vector4dc vector4dc2, Vector4dc vector4dc3, double double1, Vector4d vector4d) {
			return this.delegate.hermite(vector4dc, vector4dc2, vector4dc3, double1, vector4d);
		}

		public Vector4d lerp(Vector4dc vector4dc, double double1, Vector4d vector4d) {
			return this.delegate.lerp(vector4dc, double1, vector4d);
		}
	}
}
