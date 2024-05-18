package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Vector3d implements Externalizable,Vector3dc {
	private static final long serialVersionUID = 1L;
	public double x;
	public double y;
	public double z;

	public Vector3d() {
	}

	public Vector3d(double double1) {
		this(double1, double1, double1);
	}

	public Vector3d(double double1, double double2, double double3) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
	}

	public Vector3d(Vector3fc vector3fc) {
		this.x = (double)vector3fc.x();
		this.y = (double)vector3fc.y();
		this.z = (double)vector3fc.z();
	}

	public Vector3d(Vector2fc vector2fc, double double1) {
		this.x = (double)vector2fc.x();
		this.y = (double)vector2fc.y();
		this.z = double1;
	}

	public Vector3d(Vector3dc vector3dc) {
		this.x = vector3dc.x();
		this.y = vector3dc.y();
		this.z = vector3dc.z();
	}

	public Vector3d(Vector2dc vector2dc, double double1) {
		this.x = vector2dc.x();
		this.y = vector2dc.y();
		this.z = double1;
	}

	public Vector3d(ByteBuffer byteBuffer) {
		this(byteBuffer.position(), byteBuffer);
	}

	public Vector3d(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector3d(DoubleBuffer doubleBuffer) {
		this(doubleBuffer.position(), doubleBuffer);
	}

	public Vector3d(int int1, DoubleBuffer doubleBuffer) {
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

	public Vector3d set(Vector3dc vector3dc) {
		this.x = vector3dc.x();
		this.y = vector3dc.y();
		this.z = vector3dc.z();
		return this;
	}

	public Vector3d set(Vector2dc vector2dc, double double1) {
		this.x = vector2dc.x();
		this.y = vector2dc.y();
		this.z = double1;
		return this;
	}

	public Vector3d set(Vector3fc vector3fc) {
		this.x = (double)vector3fc.x();
		this.y = (double)vector3fc.y();
		this.z = (double)vector3fc.z();
		return this;
	}

	public Vector3d set(Vector2fc vector2fc, double double1) {
		this.x = (double)vector2fc.x();
		this.y = (double)vector2fc.y();
		this.z = double1;
		return this;
	}

	public Vector3d set(double double1) {
		return this.set(double1, double1, double1);
	}

	public Vector3d set(double double1, double double2, double double3) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d set(ByteBuffer byteBuffer) {
		return this.set(byteBuffer.position(), byteBuffer);
	}

	public Vector3d set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector3d set(DoubleBuffer doubleBuffer) {
		return this.set(doubleBuffer.position(), doubleBuffer);
	}

	public Vector3d set(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, int1, doubleBuffer);
		return this;
	}

	public Vector3d setComponent(int int1, double double1) throws IllegalArgumentException {
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

	public Vector3d sub(Vector3dc vector3dc) {
		this.x -= vector3dc.x();
		this.y -= vector3dc.y();
		this.z -= vector3dc.z();
		return this;
	}

	public Vector3d sub(Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.x = this.x - vector3dc.x();
		vector3d.y = this.y - vector3dc.y();
		vector3d.z = this.z - vector3dc.z();
		return vector3d;
	}

	public Vector3d sub(Vector3fc vector3fc) {
		this.x -= (double)vector3fc.x();
		this.y -= (double)vector3fc.y();
		this.z -= (double)vector3fc.z();
		return this;
	}

	public Vector3d sub(Vector3fc vector3fc, Vector3d vector3d) {
		vector3d.x = this.x - (double)vector3fc.x();
		vector3d.y = this.y - (double)vector3fc.y();
		vector3d.z = this.z - (double)vector3fc.z();
		return vector3d;
	}

	public Vector3d sub(double double1, double double2, double double3) {
		this.x -= double1;
		this.y -= double2;
		this.z -= double3;
		return this;
	}

	public Vector3d sub(double double1, double double2, double double3, Vector3d vector3d) {
		vector3d.x = this.x - double1;
		vector3d.y = this.y - double2;
		vector3d.z = this.z - double3;
		return vector3d;
	}

	public Vector3d add(Vector3dc vector3dc) {
		this.x += vector3dc.x();
		this.y += vector3dc.y();
		this.z += vector3dc.z();
		return this;
	}

	public Vector3d add(Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.x = this.x + vector3dc.x();
		vector3d.y = this.y + vector3dc.y();
		vector3d.z = this.z + vector3dc.z();
		return vector3d;
	}

	public Vector3d add(Vector3fc vector3fc) {
		this.x += (double)vector3fc.x();
		this.y += (double)vector3fc.y();
		this.z += (double)vector3fc.z();
		return this;
	}

	public Vector3d add(Vector3fc vector3fc, Vector3d vector3d) {
		vector3d.x = this.x + (double)vector3fc.x();
		vector3d.y = this.y + (double)vector3fc.y();
		vector3d.z = this.z + (double)vector3fc.z();
		return vector3d;
	}

	public Vector3d add(double double1, double double2, double double3) {
		this.x += double1;
		this.y += double2;
		this.z += double3;
		return this;
	}

	public Vector3d add(double double1, double double2, double double3, Vector3d vector3d) {
		vector3d.x = this.x + double1;
		vector3d.y = this.y + double2;
		vector3d.z = this.z + double3;
		return vector3d;
	}

	public Vector3d fma(Vector3dc vector3dc, Vector3dc vector3dc2) {
		this.x += vector3dc.x() * vector3dc2.x();
		this.y += vector3dc.y() * vector3dc2.y();
		this.z += vector3dc.z() * vector3dc2.z();
		return this;
	}

	public Vector3d fma(double double1, Vector3dc vector3dc) {
		this.x += double1 * vector3dc.x();
		this.y += double1 * vector3dc.y();
		this.z += double1 * vector3dc.z();
		return this;
	}

	public Vector3d fma(Vector3fc vector3fc, Vector3fc vector3fc2) {
		this.x += (double)(vector3fc.x() * vector3fc2.x());
		this.y += (double)(vector3fc.y() * vector3fc2.y());
		this.z += (double)(vector3fc.z() * vector3fc2.z());
		return this;
	}

	public Vector3d fma(double double1, Vector3fc vector3fc) {
		this.x += double1 * (double)vector3fc.x();
		this.y += double1 * (double)vector3fc.y();
		this.z += double1 * (double)vector3fc.z();
		return this;
	}

	public Vector3d fma(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3d vector3d) {
		vector3d.x = this.x + vector3dc.x() * vector3dc2.x();
		vector3d.y = this.y + vector3dc.y() * vector3dc2.y();
		vector3d.z = this.z + vector3dc.z() * vector3dc2.z();
		return vector3d;
	}

	public Vector3d fma(double double1, Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.x = this.x + double1 * vector3dc.x();
		vector3d.y = this.y + double1 * vector3dc.y();
		vector3d.z = this.z + double1 * vector3dc.z();
		return vector3d;
	}

	public Vector3d fma(Vector3dc vector3dc, Vector3fc vector3fc, Vector3d vector3d) {
		vector3d.x = this.x + vector3dc.x() * (double)vector3fc.x();
		vector3d.y = this.y + vector3dc.y() * (double)vector3fc.y();
		vector3d.z = this.z + vector3dc.z() * (double)vector3fc.z();
		return vector3d;
	}

	public Vector3d fma(double double1, Vector3fc vector3fc, Vector3d vector3d) {
		vector3d.x = this.x + double1 * (double)vector3fc.x();
		vector3d.y = this.y + double1 * (double)vector3fc.y();
		vector3d.z = this.z + double1 * (double)vector3fc.z();
		return vector3d;
	}

	public Vector3d mul(Vector3dc vector3dc) {
		this.x *= vector3dc.x();
		this.y *= vector3dc.y();
		this.z *= vector3dc.z();
		return this;
	}

	public Vector3d mul(Vector3fc vector3fc) {
		this.x *= (double)vector3fc.x();
		this.y *= (double)vector3fc.y();
		this.z *= (double)vector3fc.z();
		return this;
	}

	public Vector3d mul(Vector3fc vector3fc, Vector3d vector3d) {
		vector3d.x = this.x * (double)vector3fc.x();
		vector3d.y = this.y * (double)vector3fc.y();
		vector3d.z = this.z * (double)vector3fc.z();
		return vector3d;
	}

	public Vector3d mul(Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.x = this.x * vector3dc.x();
		vector3d.y = this.y * vector3dc.y();
		vector3d.z = this.z * vector3dc.z();
		return vector3d;
	}

	public Vector3d div(Vector3d vector3d) {
		this.x /= vector3d.x();
		this.y /= vector3d.y();
		this.z /= vector3d.z();
		return this;
	}

	public Vector3d div(Vector3fc vector3fc) {
		this.x /= (double)vector3fc.x();
		this.y /= (double)vector3fc.y();
		this.z /= (double)vector3fc.z();
		return this;
	}

	public Vector3d div(Vector3fc vector3fc, Vector3d vector3d) {
		vector3d.x = this.x / (double)vector3fc.x();
		vector3d.y = this.y / (double)vector3fc.y();
		vector3d.z = this.z / (double)vector3fc.z();
		return vector3d;
	}

	public Vector3d div(Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.x = this.x / vector3dc.x();
		vector3d.y = this.y / vector3dc.y();
		vector3d.z = this.z / vector3dc.z();
		return vector3d;
	}

	public Vector3d mulProject(Matrix4dc matrix4dc, Vector3d vector3d) {
		double double1 = 1.0 / (matrix4dc.m03() * this.x + matrix4dc.m13() * this.y + matrix4dc.m23() * this.z + matrix4dc.m33());
		vector3d.set((matrix4dc.m00() * this.x + matrix4dc.m10() * this.y + matrix4dc.m20() * this.z + matrix4dc.m30()) * double1, (matrix4dc.m01() * this.x + matrix4dc.m11() * this.y + matrix4dc.m21() * this.z + matrix4dc.m31()) * double1, (matrix4dc.m02() * this.x + matrix4dc.m12() * this.y + matrix4dc.m22() * this.z + matrix4dc.m32()) * double1);
		return vector3d;
	}

	public Vector3d mulProject(Matrix4dc matrix4dc) {
		return this.mulProject(matrix4dc, this);
	}

	public Vector3d mulProject(Matrix4fc matrix4fc, Vector3d vector3d) {
		double double1 = 1.0 / ((double)matrix4fc.m03() * this.x + (double)matrix4fc.m13() * this.y + (double)matrix4fc.m23() * this.z + (double)matrix4fc.m33());
		vector3d.set(((double)matrix4fc.m00() * this.x + (double)matrix4fc.m10() * this.y + (double)matrix4fc.m20() * this.z + (double)matrix4fc.m30()) * double1, ((double)matrix4fc.m01() * this.x + (double)matrix4fc.m11() * this.y + (double)matrix4fc.m21() * this.z + (double)matrix4fc.m31()) * double1, ((double)matrix4fc.m02() * this.x + (double)matrix4fc.m12() * this.y + (double)matrix4fc.m22() * this.z + (double)matrix4fc.m32()) * double1);
		return vector3d;
	}

	public Vector3d mulProject(Matrix4fc matrix4fc) {
		return this.mulProject(matrix4fc, this);
	}

	public Vector3d mul(Matrix3fc matrix3fc) {
		return this.mul(matrix3fc, this);
	}

	public Vector3d mul(Matrix3dc matrix3dc) {
		return this.mul(matrix3dc, this);
	}

	public Vector3d mul(Matrix3dc matrix3dc, Vector3d vector3d) {
		vector3d.set(matrix3dc.m00() * this.x + matrix3dc.m10() * this.y + matrix3dc.m20() * this.z, matrix3dc.m01() * this.x + matrix3dc.m11() * this.y + matrix3dc.m21() * this.z, matrix3dc.m02() * this.x + matrix3dc.m12() * this.y + matrix3dc.m22() * this.z);
		return vector3d;
	}

	public Vector3d mul(Matrix3fc matrix3fc, Vector3d vector3d) {
		vector3d.set((double)matrix3fc.m00() * this.x + (double)matrix3fc.m10() * this.y + (double)matrix3fc.m20() * this.z, (double)matrix3fc.m01() * this.x + (double)matrix3fc.m11() * this.y + (double)matrix3fc.m21() * this.z, (double)matrix3fc.m02() * this.x + (double)matrix3fc.m12() * this.y + (double)matrix3fc.m22() * this.z);
		return vector3d;
	}

	public Vector3d mulTranspose(Matrix3dc matrix3dc) {
		return this.mul(matrix3dc, this);
	}

	public Vector3d mulTranspose(Matrix3dc matrix3dc, Vector3d vector3d) {
		vector3d.set(matrix3dc.m00() * this.x + matrix3dc.m01() * this.y + matrix3dc.m02() * this.z, matrix3dc.m10() * this.x + matrix3dc.m11() * this.y + matrix3dc.m12() * this.z, matrix3dc.m20() * this.x + matrix3dc.m21() * this.y + matrix3dc.m22() * this.z);
		return vector3d;
	}

	public Vector3d mulTranspose(Matrix3fc matrix3fc) {
		return this.mul(matrix3fc, this);
	}

	public Vector3d mulTranspose(Matrix3fc matrix3fc, Vector3d vector3d) {
		vector3d.set((double)matrix3fc.m00() * this.x + (double)matrix3fc.m01() * this.y + (double)matrix3fc.m02() * this.z, (double)matrix3fc.m10() * this.x + (double)matrix3fc.m11() * this.y + (double)matrix3fc.m12() * this.z, (double)matrix3fc.m20() * this.x + (double)matrix3fc.m21() * this.y + (double)matrix3fc.m22() * this.z);
		return vector3d;
	}

	public Vector3d mulPosition(Matrix4fc matrix4fc) {
		return this.mulPosition(matrix4fc, this);
	}

	public Vector3d mulPosition(Matrix4dc matrix4dc) {
		return this.mulPosition(matrix4dc, this);
	}

	public Vector3d mulPosition(Matrix4x3dc matrix4x3dc) {
		return this.mulPosition(matrix4x3dc, this);
	}

	public Vector3d mulPosition(Matrix4x3fc matrix4x3fc) {
		return this.mulPosition(matrix4x3fc, this);
	}

	public Vector3d mulPosition(Matrix4dc matrix4dc, Vector3d vector3d) {
		vector3d.set(matrix4dc.m00() * this.x + matrix4dc.m10() * this.y + matrix4dc.m20() * this.z + matrix4dc.m30(), matrix4dc.m01() * this.x + matrix4dc.m11() * this.y + matrix4dc.m21() * this.z + matrix4dc.m31(), matrix4dc.m02() * this.x + matrix4dc.m12() * this.y + matrix4dc.m22() * this.z + matrix4dc.m32());
		return vector3d;
	}

	public Vector3d mulPosition(Matrix4fc matrix4fc, Vector3d vector3d) {
		vector3d.set((double)matrix4fc.m00() * this.x + (double)matrix4fc.m10() * this.y + (double)matrix4fc.m20() * this.z + (double)matrix4fc.m30(), (double)matrix4fc.m01() * this.x + (double)matrix4fc.m11() * this.y + (double)matrix4fc.m21() * this.z + (double)matrix4fc.m31(), (double)matrix4fc.m02() * this.x + (double)matrix4fc.m12() * this.y + (double)matrix4fc.m22() * this.z + (double)matrix4fc.m32());
		return vector3d;
	}

	public Vector3d mulPosition(Matrix4x3dc matrix4x3dc, Vector3d vector3d) {
		vector3d.set(matrix4x3dc.m00() * this.x + matrix4x3dc.m10() * this.y + matrix4x3dc.m20() * this.z + matrix4x3dc.m30(), matrix4x3dc.m01() * this.x + matrix4x3dc.m11() * this.y + matrix4x3dc.m21() * this.z + matrix4x3dc.m31(), matrix4x3dc.m02() * this.x + matrix4x3dc.m12() * this.y + matrix4x3dc.m22() * this.z + matrix4x3dc.m32());
		return vector3d;
	}

	public Vector3d mulPosition(Matrix4x3fc matrix4x3fc, Vector3d vector3d) {
		vector3d.set((double)matrix4x3fc.m00() * this.x + (double)matrix4x3fc.m10() * this.y + (double)matrix4x3fc.m20() * this.z + (double)matrix4x3fc.m30(), (double)matrix4x3fc.m01() * this.x + (double)matrix4x3fc.m11() * this.y + (double)matrix4x3fc.m21() * this.z + (double)matrix4x3fc.m31(), (double)matrix4x3fc.m02() * this.x + (double)matrix4x3fc.m12() * this.y + (double)matrix4x3fc.m22() * this.z + (double)matrix4x3fc.m32());
		return vector3d;
	}

	public Vector3d mulTransposePosition(Matrix4dc matrix4dc) {
		return this.mulTransposePosition(matrix4dc, this);
	}

	public Vector3d mulTransposePosition(Matrix4dc matrix4dc, Vector3d vector3d) {
		vector3d.set(matrix4dc.m00() * this.x + matrix4dc.m01() * this.y + matrix4dc.m02() * this.z + matrix4dc.m03(), matrix4dc.m10() * this.x + matrix4dc.m11() * this.y + matrix4dc.m12() * this.z + matrix4dc.m13(), matrix4dc.m20() * this.x + matrix4dc.m21() * this.y + matrix4dc.m22() * this.z + matrix4dc.m23());
		return vector3d;
	}

	public Vector3d mulTransposePosition(Matrix4fc matrix4fc) {
		return this.mulTransposePosition(matrix4fc, this);
	}

	public Vector3d mulTransposePosition(Matrix4fc matrix4fc, Vector3d vector3d) {
		vector3d.set((double)matrix4fc.m00() * this.x + (double)matrix4fc.m01() * this.y + (double)matrix4fc.m02() * this.z + (double)matrix4fc.m03(), (double)matrix4fc.m10() * this.x + (double)matrix4fc.m11() * this.y + (double)matrix4fc.m12() * this.z + (double)matrix4fc.m13(), (double)matrix4fc.m20() * this.x + (double)matrix4fc.m21() * this.y + (double)matrix4fc.m22() * this.z + (double)matrix4fc.m23());
		return vector3d;
	}

	public double mulPositionW(Matrix4fc matrix4fc) {
		return this.mulPositionW(matrix4fc, this);
	}

	public double mulPositionW(Matrix4fc matrix4fc, Vector3d vector3d) {
		double double1 = (double)matrix4fc.m03() * this.x + (double)matrix4fc.m13() * this.y + (double)matrix4fc.m23() * this.z + (double)matrix4fc.m33();
		vector3d.set((double)matrix4fc.m00() * this.x + (double)matrix4fc.m10() * this.y + (double)matrix4fc.m20() * this.z + (double)matrix4fc.m30(), (double)matrix4fc.m01() * this.x + (double)matrix4fc.m11() * this.y + (double)matrix4fc.m21() * this.z + (double)matrix4fc.m31(), (double)matrix4fc.m02() * this.x + (double)matrix4fc.m12() * this.y + (double)matrix4fc.m22() * this.z + (double)matrix4fc.m32());
		return double1;
	}

	public double mulPositionW(Matrix4dc matrix4dc) {
		return this.mulPositionW(matrix4dc, this);
	}

	public double mulPositionW(Matrix4dc matrix4dc, Vector3d vector3d) {
		double double1 = matrix4dc.m03() * this.x + matrix4dc.m13() * this.y + matrix4dc.m23() * this.z + matrix4dc.m33();
		vector3d.set(matrix4dc.m00() * this.x + matrix4dc.m10() * this.y + matrix4dc.m20() * this.z + matrix4dc.m30(), matrix4dc.m01() * this.x + matrix4dc.m11() * this.y + matrix4dc.m21() * this.z + matrix4dc.m31(), matrix4dc.m02() * this.x + matrix4dc.m12() * this.y + matrix4dc.m22() * this.z + matrix4dc.m32());
		return double1;
	}

	public Vector3d mulDirection(Matrix4fc matrix4fc) {
		return this.mulDirection(matrix4fc, this);
	}

	public Vector3d mulDirection(Matrix4dc matrix4dc) {
		return this.mulDirection(matrix4dc, this);
	}

	public Vector3d mulDirection(Matrix4x3dc matrix4x3dc) {
		return this.mulDirection(matrix4x3dc, this);
	}

	public Vector3d mulDirection(Matrix4x3fc matrix4x3fc) {
		return this.mulDirection(matrix4x3fc, this);
	}

	public Vector3d mulDirection(Matrix4dc matrix4dc, Vector3d vector3d) {
		vector3d.set(matrix4dc.m00() * this.x + matrix4dc.m10() * this.y + matrix4dc.m20() * this.z, matrix4dc.m01() * this.x + matrix4dc.m11() * this.y + matrix4dc.m21() * this.z, matrix4dc.m02() * this.x + matrix4dc.m12() * this.y + matrix4dc.m22() * this.z);
		return vector3d;
	}

	public Vector3d mulDirection(Matrix4fc matrix4fc, Vector3d vector3d) {
		vector3d.set((double)matrix4fc.m00() * this.x + (double)matrix4fc.m10() * this.y + (double)matrix4fc.m20() * this.z, (double)matrix4fc.m01() * this.x + (double)matrix4fc.m11() * this.y + (double)matrix4fc.m21() * this.z, (double)matrix4fc.m02() * this.x + (double)matrix4fc.m12() * this.y + (double)matrix4fc.m22() * this.z);
		return vector3d;
	}

	public Vector3d mulDirection(Matrix4x3dc matrix4x3dc, Vector3d vector3d) {
		vector3d.set(matrix4x3dc.m00() * this.x + matrix4x3dc.m10() * this.y + matrix4x3dc.m20() * this.z, matrix4x3dc.m01() * this.x + matrix4x3dc.m11() * this.y + matrix4x3dc.m21() * this.z, matrix4x3dc.m02() * this.x + matrix4x3dc.m12() * this.y + matrix4x3dc.m22() * this.z);
		return vector3d;
	}

	public Vector3d mulDirection(Matrix4x3fc matrix4x3fc, Vector3d vector3d) {
		vector3d.set((double)matrix4x3fc.m00() * this.x + (double)matrix4x3fc.m10() * this.y + (double)matrix4x3fc.m20() * this.z, (double)matrix4x3fc.m01() * this.x + (double)matrix4x3fc.m11() * this.y + (double)matrix4x3fc.m21() * this.z, (double)matrix4x3fc.m02() * this.x + (double)matrix4x3fc.m12() * this.y + (double)matrix4x3fc.m22() * this.z);
		return vector3d;
	}

	public Vector3d mulTransposeDirection(Matrix4dc matrix4dc) {
		return this.mulTransposeDirection(matrix4dc, this);
	}

	public Vector3d mulTransposeDirection(Matrix4dc matrix4dc, Vector3d vector3d) {
		vector3d.set(matrix4dc.m00() * this.x + matrix4dc.m01() * this.y + matrix4dc.m02() * this.z, matrix4dc.m10() * this.x + matrix4dc.m11() * this.y + matrix4dc.m12() * this.z, matrix4dc.m20() * this.x + matrix4dc.m21() * this.y + matrix4dc.m22() * this.z);
		return vector3d;
	}

	public Vector3d mulTransposeDirection(Matrix4fc matrix4fc) {
		return this.mulTransposeDirection(matrix4fc, this);
	}

	public Vector3d mulTransposeDirection(Matrix4fc matrix4fc, Vector3d vector3d) {
		vector3d.set((double)matrix4fc.m00() * this.x + (double)matrix4fc.m01() * this.y + (double)matrix4fc.m02() * this.z, (double)matrix4fc.m10() * this.x + (double)matrix4fc.m11() * this.y + (double)matrix4fc.m12() * this.z, (double)matrix4fc.m20() * this.x + (double)matrix4fc.m21() * this.y + (double)matrix4fc.m22() * this.z);
		return vector3d;
	}

	public Vector3d mul(double double1) {
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		return this;
	}

	public Vector3d mul(double double1, Vector3d vector3d) {
		vector3d.x = this.x * double1;
		vector3d.y = this.y * double1;
		vector3d.z = this.z * double1;
		return vector3d;
	}

	public Vector3d mul(double double1, double double2, double double3) {
		this.x *= double1;
		this.y *= double2;
		this.z *= double3;
		return this;
	}

	public Vector3d mul(double double1, double double2, double double3, Vector3d vector3d) {
		vector3d.x = this.x * double1;
		vector3d.y = this.y * double2;
		vector3d.z = this.z * double3;
		return vector3d;
	}

	public Vector3d rotate(Quaterniondc quaterniondc) {
		quaterniondc.transform((Vector3dc)this, (Vector3d)this);
		return this;
	}

	public Vector3d rotate(Quaterniondc quaterniondc, Vector3d vector3d) {
		quaterniondc.transform((Vector3dc)this, (Vector3d)vector3d);
		return vector3d;
	}

	public Quaterniond rotationTo(Vector3dc vector3dc, Quaterniond quaterniond) {
		return quaterniond.rotationTo(this, vector3dc);
	}

	public Quaterniond rotationTo(double double1, double double2, double double3, Quaterniond quaterniond) {
		return quaterniond.rotationTo(this.x, this.y, this.z, double1, double2, double3);
	}

	public Vector3d div(double double1) {
		this.x /= double1;
		this.y /= double1;
		this.z /= double1;
		return this;
	}

	public Vector3d div(double double1, Vector3d vector3d) {
		vector3d.x = this.x / double1;
		vector3d.y = this.y / double1;
		vector3d.z = this.z / double1;
		return vector3d;
	}

	public Vector3d div(double double1, double double2, double double3) {
		this.x /= double1;
		this.y /= double2;
		this.z /= double3;
		return this;
	}

	public Vector3d div(double double1, double double2, double double3, Vector3d vector3d) {
		vector3d.x = this.x / double1;
		vector3d.y = this.y / double2;
		vector3d.z = this.z / double3;
		return vector3d;
	}

	public double lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	public double length() {
		return Math.sqrt(this.lengthSquared());
	}

	public Vector3d normalize() {
		double double1 = 1.0 / this.length();
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		return this;
	}

	public Vector3d normalize(Vector3d vector3d) {
		double double1 = 1.0 / this.length();
		vector3d.x = this.x * double1;
		vector3d.y = this.y * double1;
		vector3d.z = this.z * double1;
		return vector3d;
	}

	public Vector3d cross(Vector3dc vector3dc) {
		this.set(this.y * vector3dc.z() - this.z * vector3dc.y(), this.z * vector3dc.x() - this.x * vector3dc.z(), this.x * vector3dc.y() - this.y * vector3dc.x());
		return this;
	}

	public Vector3d cross(double double1, double double2, double double3) {
		return this.set(this.y * double3 - this.z * double2, this.z * double1 - this.x * double3, this.x * double2 - this.y * double1);
	}

	public Vector3d cross(Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.set(this.y * vector3dc.z() - this.z * vector3dc.y(), this.z * vector3dc.x() - this.x * vector3dc.z(), this.x * vector3dc.y() - this.y * vector3dc.x());
		return vector3d;
	}

	public Vector3d cross(double double1, double double2, double double3, Vector3d vector3d) {
		return vector3d.set(this.y * double3 - this.z * double2, this.z * double1 - this.x * double3, this.x * double2 - this.y * double1);
	}

	public double distance(Vector3dc vector3dc) {
		double double1 = vector3dc.x() - this.x;
		double double2 = vector3dc.y() - this.y;
		double double3 = vector3dc.z() - this.z;
		return Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
	}

	public double distance(double double1, double double2, double double3) {
		double double4 = this.x - double1;
		double double5 = this.y - double2;
		double double6 = this.z - double3;
		return Math.sqrt(double4 * double4 + double5 * double5 + double6 * double6);
	}

	public double distanceSquared(Vector3dc vector3dc) {
		double double1 = vector3dc.x() - this.x;
		double double2 = vector3dc.y() - this.y;
		double double3 = vector3dc.z() - this.z;
		return double1 * double1 + double2 * double2 + double3 * double3;
	}

	public double distanceSquared(double double1, double double2, double double3) {
		double double4 = this.x - double1;
		double double5 = this.y - double2;
		double double6 = this.z - double3;
		return double4 * double4 + double5 * double5 + double6 * double6;
	}

	public double dot(Vector3dc vector3dc) {
		return this.x * vector3dc.x() + this.y * vector3dc.y() + this.z * vector3dc.z();
	}

	public double dot(double double1, double double2, double double3) {
		return this.x * double1 + this.y * double2 + this.z * double3;
	}

	public double angleCos(Vector3dc vector3dc) {
		double double1 = this.x * this.x + this.y * this.y + this.z * this.z;
		double double2 = vector3dc.x() * vector3dc.x() + vector3dc.y() * vector3dc.y() + vector3dc.z() * vector3dc.z();
		double double3 = this.x * vector3dc.x() + this.y * vector3dc.y() + this.z * vector3dc.z();
		return double3 / Math.sqrt(double1 * double2);
	}

	public double angle(Vector3dc vector3dc) {
		double double1 = this.angleCos(vector3dc);
		double1 = double1 < 1.0 ? double1 : 1.0;
		double1 = double1 > -1.0 ? double1 : -1.0;
		return Math.acos(double1);
	}

	public Vector3d zero() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
		return this;
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format(this.x) + " " + numberFormat.format(this.y) + " " + numberFormat.format(this.z) + ")";
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

	public Vector3d negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	public Vector3d negate(Vector3d vector3d) {
		vector3d.x = -this.x;
		vector3d.y = -this.y;
		vector3d.z = -this.z;
		return vector3d;
	}

	public int hashCode() {
		byte byte1 = 1;
		long long1 = Double.doubleToLongBits(this.x);
		int int1 = 31 * byte1 + (int)(long1 ^ long1 >>> 32);
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
			Vector3d vector3d = (Vector3d)object;
			if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(vector3d.x)) {
				return false;
			} else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(vector3d.y)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(vector3d.z);
			}
		}
	}

	public Vector3d reflect(Vector3dc vector3dc) {
		double double1 = this.dot(vector3dc);
		this.x -= (double1 + double1) * vector3dc.x();
		this.y -= (double1 + double1) * vector3dc.y();
		this.z -= (double1 + double1) * vector3dc.z();
		return this;
	}

	public Vector3d reflect(double double1, double double2, double double3) {
		double double4 = this.dot(double1, double2, double3);
		this.x -= (double4 + double4) * double1;
		this.y -= (double4 + double4) * double2;
		this.z -= (double4 + double4) * double3;
		return this;
	}

	public Vector3d reflect(Vector3dc vector3dc, Vector3d vector3d) {
		double double1 = this.dot(vector3dc);
		vector3d.x = this.x - (double1 + double1) * vector3dc.x();
		vector3d.y = this.y - (double1 + double1) * vector3dc.y();
		vector3d.z = this.z - (double1 + double1) * vector3dc.z();
		return vector3d;
	}

	public Vector3d reflect(double double1, double double2, double double3, Vector3d vector3d) {
		double double4 = this.dot(double1, double2, double3);
		vector3d.x = this.x - (double4 + double4) * double1;
		vector3d.y = this.y - (double4 + double4) * double2;
		vector3d.z = this.z - (double4 + double4) * double3;
		return vector3d;
	}

	public Vector3d half(Vector3dc vector3dc) {
		return this.add(vector3dc).normalize();
	}

	public Vector3d half(double double1, double double2, double double3) {
		return this.add(double1, double2, double3).normalize();
	}

	public Vector3d half(Vector3dc vector3dc, Vector3d vector3d) {
		return vector3d.set((Vector3dc)this).add(vector3dc).normalize();
	}

	public Vector3d half(double double1, double double2, double double3, Vector3d vector3d) {
		return vector3d.set((Vector3dc)this).add(double1, double2, double3).normalize();
	}

	public Vector3d smoothStep(Vector3dc vector3dc, double double1, Vector3d vector3d) {
		double double2 = double1 * double1;
		double double3 = double2 * double1;
		vector3d.x = (this.x + this.x - vector3dc.x() - vector3dc.x()) * double3 + (3.0 * vector3dc.x() - 3.0 * this.x) * double2 + this.x * double1 + this.x;
		vector3d.y = (this.y + this.y - vector3dc.y() - vector3dc.y()) * double3 + (3.0 * vector3dc.y() - 3.0 * this.y) * double2 + this.y * double1 + this.y;
		vector3d.z = (this.z + this.z - vector3dc.z() - vector3dc.z()) * double3 + (3.0 * vector3dc.z() - 3.0 * this.z) * double2 + this.z * double1 + this.z;
		return vector3d;
	}

	public Vector3d hermite(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, double double1, Vector3d vector3d) {
		double double2 = double1 * double1;
		double double3 = double2 * double1;
		vector3d.x = (this.x + this.x - vector3dc2.x() - vector3dc2.x() + vector3dc3.x() + vector3dc.x()) * double3 + (3.0 * vector3dc2.x() - 3.0 * this.x - vector3dc.x() - vector3dc.x() - vector3dc3.x()) * double2 + this.x * double1 + this.x;
		vector3d.y = (this.y + this.y - vector3dc2.y() - vector3dc2.y() + vector3dc3.y() + vector3dc.y()) * double3 + (3.0 * vector3dc2.y() - 3.0 * this.y - vector3dc.y() - vector3dc.y() - vector3dc3.y()) * double2 + this.y * double1 + this.y;
		vector3d.z = (this.z + this.z - vector3dc2.z() - vector3dc2.z() + vector3dc3.z() + vector3dc.z()) * double3 + (3.0 * vector3dc2.z() - 3.0 * this.z - vector3dc.z() - vector3dc.z() - vector3dc3.z()) * double2 + this.z * double1 + this.z;
		return vector3d;
	}

	public Vector3d lerp(Vector3dc vector3dc, double double1) {
		return this.lerp(vector3dc, double1, this);
	}

	public Vector3d lerp(Vector3dc vector3dc, double double1, Vector3d vector3d) {
		vector3d.x = this.x + (vector3dc.x() - this.x) * double1;
		vector3d.y = this.y + (vector3dc.y() - this.y) * double1;
		vector3d.z = this.z + (vector3dc.z() - this.z) * double1;
		return vector3d;
	}

	public double get(int int1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			return this.x;
		
		case 1: 
			return this.y;
		
		case 2: 
			return this.z;
		
		default: 
			throw new IllegalArgumentException();
		
		}
	}

	public int maxComponent() {
		double double1 = Math.abs(this.x);
		double double2 = Math.abs(this.y);
		double double3 = Math.abs(this.z);
		if (double1 >= double2 && double1 >= double3) {
			return 0;
		} else {
			return double2 >= double3 ? 1 : 2;
		}
	}

	public int minComponent() {
		double double1 = Math.abs(this.x);
		double double2 = Math.abs(this.y);
		double double3 = Math.abs(this.z);
		if (double1 < double2 && double1 < double3) {
			return 0;
		} else {
			return double2 < double3 ? 1 : 2;
		}
	}

	public Vector3d orthogonalize(Vector3dc vector3dc, Vector3d vector3d) {
		double double1 = 1.0 / Math.sqrt(vector3dc.x() * vector3dc.x() + vector3dc.y() * vector3dc.y() + vector3dc.z() * vector3dc.z());
		double double2 = vector3dc.x() * double1;
		double double3 = vector3dc.y() * double1;
		double double4 = vector3dc.z() * double1;
		double double5 = double2 * this.x + double3 * this.y + double4 * this.z;
		double double6 = this.x - double5 * double2;
		double double7 = this.y - double5 * double3;
		double double8 = this.z - double5 * double4;
		double double9 = 1.0 / Math.sqrt(double6 * double6 + double7 * double7 + double8 * double8);
		vector3d.x = double6 * double9;
		vector3d.y = double7 * double9;
		vector3d.z = double8 * double9;
		return vector3d;
	}

	public Vector3d orthogonalize(Vector3dc vector3dc) {
		return this.orthogonalize(vector3dc, this);
	}

	public Vector3d orthogonalizeUnit(Vector3dc vector3dc, Vector3d vector3d) {
		double double1 = vector3dc.x();
		double double2 = vector3dc.y();
		double double3 = vector3dc.z();
		double double4 = double1 * this.x + double2 * this.y + double3 * this.z;
		double double5 = this.x - double4 * double1;
		double double6 = this.y - double4 * double2;
		double double7 = this.z - double4 * double3;
		double double8 = 1.0 / Math.sqrt(double5 * double5 + double6 * double6 + double7 * double7);
		vector3d.x = double5 * double8;
		vector3d.y = double6 * double8;
		vector3d.z = double7 * double8;
		return vector3d;
	}

	public Vector3d orthogonalizeUnit(Vector3dc vector3dc) {
		return this.orthogonalizeUnit(vector3dc, this);
	}

	public Vector3dc toImmutable() {
		return (Vector3dc)(!Options.DEBUG ? this : new Vector3d.Proxy(this));
	}

	private final class Proxy implements Vector3dc {
		private final Vector3dc delegate;

		Proxy(Vector3dc vector3dc) {
			this.delegate = vector3dc;
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

		public Vector3d sub(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.sub(vector3dc, vector3d);
		}

		public Vector3d sub(Vector3fc vector3fc, Vector3d vector3d) {
			return this.delegate.sub(vector3fc, vector3d);
		}

		public Vector3d sub(double double1, double double2, double double3, Vector3d vector3d) {
			return this.delegate.sub(double1, double2, double3, vector3d);
		}

		public Vector3d add(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.add(vector3dc, vector3d);
		}

		public Vector3d add(Vector3fc vector3fc, Vector3d vector3d) {
			return this.delegate.add(vector3fc, vector3d);
		}

		public Vector3d add(double double1, double double2, double double3, Vector3d vector3d) {
			return this.delegate.add(double1, double2, double3, vector3d);
		}

		public Vector3d fma(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3d vector3d) {
			return this.delegate.fma(vector3dc, vector3dc2, vector3d);
		}

		public Vector3d fma(double double1, Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.fma(double1, vector3dc, vector3d);
		}

		public Vector3d fma(Vector3dc vector3dc, Vector3fc vector3fc, Vector3d vector3d) {
			return this.delegate.fma(vector3dc, vector3fc, vector3d);
		}

		public Vector3d fma(double double1, Vector3fc vector3fc, Vector3d vector3d) {
			return this.delegate.fma(double1, vector3fc, vector3d);
		}

		public Vector3d mul(Vector3fc vector3fc, Vector3d vector3d) {
			return this.delegate.mul(vector3fc, vector3d);
		}

		public Vector3d mul(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.mul(vector3dc, vector3d);
		}

		public Vector3d div(Vector3fc vector3fc, Vector3d vector3d) {
			return this.delegate.div(vector3fc, vector3d);
		}

		public Vector3d div(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.div(vector3dc, vector3d);
		}

		public Vector3d mulProject(Matrix4dc matrix4dc, Vector3d vector3d) {
			return this.delegate.mulProject(matrix4dc, vector3d);
		}

		public Vector3d mulProject(Matrix4fc matrix4fc, Vector3d vector3d) {
			return this.delegate.mulProject(matrix4fc, vector3d);
		}

		public Vector3d mul(Matrix3dc matrix3dc, Vector3d vector3d) {
			return this.delegate.mul(matrix3dc, vector3d);
		}

		public Vector3d mul(Matrix3fc matrix3fc, Vector3d vector3d) {
			return this.delegate.mul(matrix3fc, vector3d);
		}

		public Vector3d mulTranspose(Matrix3dc matrix3dc, Vector3d vector3d) {
			return this.delegate.mulTranspose(matrix3dc, vector3d);
		}

		public Vector3d mulTranspose(Matrix3fc matrix3fc, Vector3d vector3d) {
			return this.delegate.mulTranspose(matrix3fc, vector3d);
		}

		public Vector3d mulPosition(Matrix4dc matrix4dc, Vector3d vector3d) {
			return this.delegate.mulPosition(matrix4dc, vector3d);
		}

		public Vector3d mulPosition(Matrix4fc matrix4fc, Vector3d vector3d) {
			return this.delegate.mulPosition(matrix4fc, vector3d);
		}

		public Vector3d mulPosition(Matrix4x3dc matrix4x3dc, Vector3d vector3d) {
			return this.delegate.mulPosition(matrix4x3dc, vector3d);
		}

		public Vector3d mulPosition(Matrix4x3fc matrix4x3fc, Vector3d vector3d) {
			return this.delegate.mulPosition(matrix4x3fc, vector3d);
		}

		public Vector3d mulTransposePosition(Matrix4dc matrix4dc, Vector3d vector3d) {
			return this.delegate.mulTransposePosition(matrix4dc, vector3d);
		}

		public Vector3d mulTransposePosition(Matrix4fc matrix4fc, Vector3d vector3d) {
			return this.delegate.mulTransposePosition(matrix4fc, vector3d);
		}

		public double mulPositionW(Matrix4fc matrix4fc, Vector3d vector3d) {
			return this.delegate.mulPositionW(matrix4fc, vector3d);
		}

		public double mulPositionW(Matrix4dc matrix4dc, Vector3d vector3d) {
			return this.delegate.mulPositionW(matrix4dc, vector3d);
		}

		public Vector3d mulDirection(Matrix4dc matrix4dc, Vector3d vector3d) {
			return this.delegate.mulDirection(matrix4dc, vector3d);
		}

		public Vector3d mulDirection(Matrix4fc matrix4fc, Vector3d vector3d) {
			return this.delegate.mulDirection(matrix4fc, vector3d);
		}

		public Vector3d mulDirection(Matrix4x3dc matrix4x3dc, Vector3d vector3d) {
			return this.delegate.mulDirection(matrix4x3dc, vector3d);
		}

		public Vector3d mulDirection(Matrix4x3fc matrix4x3fc, Vector3d vector3d) {
			return this.delegate.mulDirection(matrix4x3fc, vector3d);
		}

		public Vector3d mulTransposeDirection(Matrix4dc matrix4dc, Vector3d vector3d) {
			return this.delegate.mulTransposeDirection(matrix4dc, vector3d);
		}

		public Vector3d mulTransposeDirection(Matrix4fc matrix4fc, Vector3d vector3d) {
			return this.delegate.mulTransposeDirection(matrix4fc, vector3d);
		}

		public Vector3d mul(double double1, Vector3d vector3d) {
			return this.delegate.mul(double1, vector3d);
		}

		public Vector3d mul(double double1, double double2, double double3, Vector3d vector3d) {
			return this.delegate.mul(double1, double2, double3, vector3d);
		}

		public Vector3d rotate(Quaterniondc quaterniondc, Vector3d vector3d) {
			return this.delegate.rotate(quaterniondc, vector3d);
		}

		public Vector3d div(double double1, Vector3d vector3d) {
			return this.delegate.div(double1, vector3d);
		}

		public Vector3d div(double double1, double double2, double double3, Vector3d vector3d) {
			return this.delegate.div(double1, double2, double3, vector3d);
		}

		public double lengthSquared() {
			return this.delegate.lengthSquared();
		}

		public double length() {
			return this.delegate.length();
		}

		public Vector3d normalize(Vector3d vector3d) {
			return this.delegate.normalize(vector3d);
		}

		public Vector3d cross(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.cross(vector3dc, vector3d);
		}

		public Vector3d cross(double double1, double double2, double double3, Vector3d vector3d) {
			return this.delegate.cross(double1, double2, double3, vector3d);
		}

		public double distance(Vector3dc vector3dc) {
			return this.delegate.distance(vector3dc);
		}

		public double distance(double double1, double double2, double double3) {
			return this.delegate.distance(double1, double2, double3);
		}

		public double distanceSquared(Vector3dc vector3dc) {
			return this.delegate.distanceSquared(vector3dc);
		}

		public double distanceSquared(double double1, double double2, double double3) {
			return this.delegate.distanceSquared(double1, double2, double3);
		}

		public double dot(Vector3dc vector3dc) {
			return this.delegate.dot(vector3dc);
		}

		public double dot(double double1, double double2, double double3) {
			return this.delegate.dot(double1, double2, double3);
		}

		public double angleCos(Vector3dc vector3dc) {
			return this.delegate.angleCos(vector3dc);
		}

		public double angle(Vector3dc vector3dc) {
			return this.delegate.angle(vector3dc);
		}

		public Vector3d negate(Vector3d vector3d) {
			return this.delegate.negate(vector3d);
		}

		public Vector3d reflect(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.reflect(vector3dc, vector3d);
		}

		public Vector3d reflect(double double1, double double2, double double3, Vector3d vector3d) {
			return this.delegate.reflect(double1, double2, double3, vector3d);
		}

		public Vector3d half(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.half(vector3dc, vector3d);
		}

		public Vector3d half(double double1, double double2, double double3, Vector3d vector3d) {
			return this.delegate.half(double1, double2, double3, vector3d);
		}

		public Vector3d smoothStep(Vector3dc vector3dc, double double1, Vector3d vector3d) {
			return this.delegate.smoothStep(vector3dc, double1, vector3d);
		}

		public Vector3d hermite(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, double double1, Vector3d vector3d) {
			return this.delegate.hermite(vector3dc, vector3dc2, vector3dc3, double1, vector3d);
		}

		public Vector3d lerp(Vector3dc vector3dc, double double1, Vector3d vector3d) {
			return this.delegate.lerp(vector3dc, double1, vector3d);
		}

		public double get(int int1) throws IllegalArgumentException {
			return this.delegate.get(int1);
		}

		public int maxComponent() {
			return this.delegate.maxComponent();
		}

		public int minComponent() {
			return this.delegate.minComponent();
		}

		public Vector3d orthogonalize(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.orthogonalize(vector3dc, vector3d);
		}

		public Vector3d orthogonalizeUnit(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.orthogonalizeUnit(vector3dc, vector3d);
		}
	}
}
