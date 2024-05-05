package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;


public class Vector3d implements Externalizable,Vector3dc {
	private static final long serialVersionUID = 1L;
	public double x;
	public double y;
	public double z;

	public Vector3d() {
	}

	public Vector3d(double double1) {
		this.x = double1;
		this.y = double1;
		this.z = double1;
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

	public Vector3d(Vector3ic vector3ic) {
		this.x = (double)vector3ic.x();
		this.y = (double)vector3ic.y();
		this.z = (double)vector3ic.z();
	}

	public Vector3d(Vector2fc vector2fc, double double1) {
		this.x = (double)vector2fc.x();
		this.y = (double)vector2fc.y();
		this.z = double1;
	}

	public Vector3d(Vector2ic vector2ic, double double1) {
		this.x = (double)vector2ic.x();
		this.y = (double)vector2ic.y();
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

	public Vector3d(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
		this.z = doubleArray[2];
	}

	public Vector3d(float[] floatArray) {
		this.x = (double)floatArray[0];
		this.y = (double)floatArray[1];
		this.z = (double)floatArray[2];
	}

	public Vector3d(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
	}

	public Vector3d(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector3d(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
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

	public Vector3d set(Vector3ic vector3ic) {
		this.x = (double)vector3ic.x();
		this.y = (double)vector3ic.y();
		this.z = (double)vector3ic.z();
		return this;
	}

	public Vector3d set(Vector2dc vector2dc, double double1) {
		this.x = vector2dc.x();
		this.y = vector2dc.y();
		this.z = double1;
		return this;
	}

	public Vector3d set(Vector2ic vector2ic, double double1) {
		this.x = (double)vector2ic.x();
		this.y = (double)vector2ic.y();
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
		this.x = double1;
		this.y = double1;
		this.z = double1;
		return this;
	}

	public Vector3d set(double double1, double double2, double double3) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d set(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
		this.z = doubleArray[2];
		return this;
	}

	public Vector3d set(float[] floatArray) {
		this.x = (double)floatArray[0];
		this.y = (double)floatArray[1];
		this.z = (double)floatArray[2];
		return this;
	}

	public Vector3d set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Vector3d set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector3d set(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
		return this;
	}

	public Vector3d set(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, int1, doubleBuffer);
		return this;
	}

	public Vector3d setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
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

	public ByteBuffer getf(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putf(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer getf(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putf(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer get(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, int1, floatBuffer);
		return floatBuffer;
	}

	public Vector3dc getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
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
		this.x = Math.fma(vector3dc.x(), vector3dc2.x(), this.x);
		this.y = Math.fma(vector3dc.y(), vector3dc2.y(), this.y);
		this.z = Math.fma(vector3dc.z(), vector3dc2.z(), this.z);
		return this;
	}

	public Vector3d fma(double double1, Vector3dc vector3dc) {
		this.x = Math.fma(double1, vector3dc.x(), this.x);
		this.y = Math.fma(double1, vector3dc.y(), this.y);
		this.z = Math.fma(double1, vector3dc.z(), this.z);
		return this;
	}

	public Vector3d fma(Vector3fc vector3fc, Vector3fc vector3fc2) {
		this.x = Math.fma((double)vector3fc.x(), (double)vector3fc2.x(), this.x);
		this.y = Math.fma((double)vector3fc.y(), (double)vector3fc2.y(), this.y);
		this.z = Math.fma((double)vector3fc.z(), (double)vector3fc2.z(), this.z);
		return this;
	}

	public Vector3d fma(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3d vector3d) {
		vector3d.x = Math.fma((double)vector3fc.x(), (double)vector3fc2.x(), this.x);
		vector3d.y = Math.fma((double)vector3fc.y(), (double)vector3fc2.y(), this.y);
		vector3d.z = Math.fma((double)vector3fc.z(), (double)vector3fc2.z(), this.z);
		return vector3d;
	}

	public Vector3d fma(double double1, Vector3fc vector3fc) {
		this.x = Math.fma(double1, (double)vector3fc.x(), this.x);
		this.y = Math.fma(double1, (double)vector3fc.y(), this.y);
		this.z = Math.fma(double1, (double)vector3fc.z(), this.z);
		return this;
	}

	public Vector3d fma(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3d vector3d) {
		vector3d.x = Math.fma(vector3dc.x(), vector3dc2.x(), this.x);
		vector3d.y = Math.fma(vector3dc.y(), vector3dc2.y(), this.y);
		vector3d.z = Math.fma(vector3dc.z(), vector3dc2.z(), this.z);
		return vector3d;
	}

	public Vector3d fma(double double1, Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.x = Math.fma(double1, vector3dc.x(), this.x);
		vector3d.y = Math.fma(double1, vector3dc.y(), this.y);
		vector3d.z = Math.fma(double1, vector3dc.z(), this.z);
		return vector3d;
	}

	public Vector3d fma(Vector3dc vector3dc, Vector3fc vector3fc, Vector3d vector3d) {
		vector3d.x = Math.fma(vector3dc.x(), (double)vector3fc.x(), this.x);
		vector3d.y = Math.fma(vector3dc.y(), (double)vector3fc.y(), this.y);
		vector3d.z = Math.fma(vector3dc.z(), (double)vector3fc.z(), this.z);
		return vector3d;
	}

	public Vector3d fma(double double1, Vector3fc vector3fc, Vector3d vector3d) {
		vector3d.x = Math.fma(double1, (double)vector3fc.x(), this.x);
		vector3d.y = Math.fma(double1, (double)vector3fc.y(), this.y);
		vector3d.z = Math.fma(double1, (double)vector3fc.z(), this.z);
		return vector3d;
	}

	public Vector3d mulAdd(Vector3dc vector3dc, Vector3dc vector3dc2) {
		this.x = Math.fma(this.x, vector3dc.x(), vector3dc2.x());
		this.y = Math.fma(this.y, vector3dc.y(), vector3dc2.y());
		this.z = Math.fma(this.z, vector3dc.z(), vector3dc2.z());
		return this;
	}

	public Vector3d mulAdd(double double1, Vector3dc vector3dc) {
		this.x = Math.fma(this.x, double1, vector3dc.x());
		this.y = Math.fma(this.y, double1, vector3dc.y());
		this.z = Math.fma(this.z, double1, vector3dc.z());
		return this;
	}

	public Vector3d mulAdd(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3d vector3d) {
		vector3d.x = Math.fma(this.x, vector3dc.x(), vector3dc2.x());
		vector3d.y = Math.fma(this.y, vector3dc.y(), vector3dc2.y());
		vector3d.z = Math.fma(this.z, vector3dc.z(), vector3dc2.z());
		return vector3d;
	}

	public Vector3d mulAdd(double double1, Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.x = Math.fma(this.x, double1, vector3dc.x());
		vector3d.y = Math.fma(this.y, double1, vector3dc.y());
		vector3d.z = Math.fma(this.z, double1, vector3dc.z());
		return vector3d;
	}

	public Vector3d mulAdd(Vector3fc vector3fc, Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.x = Math.fma(this.x, (double)vector3fc.x(), vector3dc.x());
		vector3d.y = Math.fma(this.y, (double)vector3fc.y(), vector3dc.y());
		vector3d.z = Math.fma(this.z, (double)vector3fc.z(), vector3dc.z());
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

	public Vector3d mulProject(Matrix4dc matrix4dc, double double1, Vector3d vector3d) {
		double double2 = 1.0 / Math.fma(matrix4dc.m03(), this.x, Math.fma(matrix4dc.m13(), this.y, Math.fma(matrix4dc.m23(), this.z, matrix4dc.m33() * double1)));
		double double3 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30() * double1))) * double2;
		double double4 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31() * double1))) * double2;
		double double5 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32() * double1))) * double2;
		vector3d.x = double3;
		vector3d.y = double4;
		vector3d.z = double5;
		return vector3d;
	}

	public Vector3d mulProject(Matrix4dc matrix4dc, Vector3d vector3d) {
		double double1 = 1.0 / Math.fma(matrix4dc.m03(), this.x, Math.fma(matrix4dc.m13(), this.y, Math.fma(matrix4dc.m23(), this.z, matrix4dc.m33())));
		double double2 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30()))) * double1;
		double double3 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31()))) * double1;
		double double4 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32()))) * double1;
		vector3d.x = double2;
		vector3d.y = double3;
		vector3d.z = double4;
		return vector3d;
	}

	public Vector3d mulProject(Matrix4dc matrix4dc) {
		double double1 = 1.0 / Math.fma(matrix4dc.m03(), this.x, Math.fma(matrix4dc.m13(), this.y, Math.fma(matrix4dc.m23(), this.z, matrix4dc.m33())));
		double double2 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30()))) * double1;
		double double3 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31()))) * double1;
		double double4 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32()))) * double1;
		this.x = double2;
		this.y = double3;
		this.z = double4;
		return this;
	}

	public Vector3d mulProject(Matrix4fc matrix4fc, Vector3d vector3d) {
		double double1 = 1.0 / Math.fma((double)matrix4fc.m03(), this.x, Math.fma((double)matrix4fc.m13(), this.y, Math.fma((double)matrix4fc.m23(), this.z, (double)matrix4fc.m33())));
		double double2 = ((double)matrix4fc.m00() * this.x + (double)matrix4fc.m10() * this.y + (double)matrix4fc.m20() * this.z + (double)matrix4fc.m30()) * double1;
		double double3 = ((double)matrix4fc.m01() * this.x + (double)matrix4fc.m11() * this.y + (double)matrix4fc.m21() * this.z + (double)matrix4fc.m31()) * double1;
		double double4 = ((double)matrix4fc.m02() * this.x + (double)matrix4fc.m12() * this.y + (double)matrix4fc.m22() * this.z + (double)matrix4fc.m32()) * double1;
		vector3d.x = double2;
		vector3d.y = double3;
		vector3d.z = double4;
		return vector3d;
	}

	public Vector3d mulProject(Matrix4fc matrix4fc) {
		double double1 = 1.0 / Math.fma((double)matrix4fc.m03(), this.x, Math.fma((double)matrix4fc.m13(), this.y, Math.fma((double)matrix4fc.m23(), this.z, (double)matrix4fc.m33())));
		double double2 = ((double)matrix4fc.m00() * this.x + (double)matrix4fc.m10() * this.y + (double)matrix4fc.m20() * this.z + (double)matrix4fc.m30()) * double1;
		double double3 = ((double)matrix4fc.m01() * this.x + (double)matrix4fc.m11() * this.y + (double)matrix4fc.m21() * this.z + (double)matrix4fc.m31()) * double1;
		double double4 = ((double)matrix4fc.m02() * this.x + (double)matrix4fc.m12() * this.y + (double)matrix4fc.m22() * this.z + (double)matrix4fc.m32()) * double1;
		this.x = double2;
		this.y = double3;
		this.z = double4;
		return this;
	}

	public Vector3d mul(Matrix3fc matrix3fc) {
		double double1 = Math.fma((double)matrix3fc.m00(), this.x, Math.fma((double)matrix3fc.m10(), this.y, (double)matrix3fc.m20() * this.z));
		double double2 = Math.fma((double)matrix3fc.m01(), this.x, Math.fma((double)matrix3fc.m11(), this.y, (double)matrix3fc.m21() * this.z));
		double double3 = Math.fma((double)matrix3fc.m02(), this.x, Math.fma((double)matrix3fc.m12(), this.y, (double)matrix3fc.m22() * this.z));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mul(Matrix3dc matrix3dc) {
		double double1 = Math.fma(matrix3dc.m00(), this.x, Math.fma(matrix3dc.m10(), this.y, matrix3dc.m20() * this.z));
		double double2 = Math.fma(matrix3dc.m01(), this.x, Math.fma(matrix3dc.m11(), this.y, matrix3dc.m21() * this.z));
		double double3 = Math.fma(matrix3dc.m02(), this.x, Math.fma(matrix3dc.m12(), this.y, matrix3dc.m22() * this.z));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mul(Matrix3dc matrix3dc, Vector3d vector3d) {
		double double1 = Math.fma(matrix3dc.m00(), this.x, Math.fma(matrix3dc.m10(), this.y, matrix3dc.m20() * this.z));
		double double2 = Math.fma(matrix3dc.m01(), this.x, Math.fma(matrix3dc.m11(), this.y, matrix3dc.m21() * this.z));
		double double3 = Math.fma(matrix3dc.m02(), this.x, Math.fma(matrix3dc.m12(), this.y, matrix3dc.m22() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3f mul(Matrix3dc matrix3dc, Vector3f vector3f) {
		double double1 = Math.fma(matrix3dc.m00(), this.x, Math.fma(matrix3dc.m10(), this.y, matrix3dc.m20() * this.z));
		double double2 = Math.fma(matrix3dc.m01(), this.x, Math.fma(matrix3dc.m11(), this.y, matrix3dc.m21() * this.z));
		double double3 = Math.fma(matrix3dc.m02(), this.x, Math.fma(matrix3dc.m12(), this.y, matrix3dc.m22() * this.z));
		vector3f.x = (float)double1;
		vector3f.y = (float)double2;
		vector3f.z = (float)double3;
		return vector3f;
	}

	public Vector3d mul(Matrix3fc matrix3fc, Vector3d vector3d) {
		double double1 = Math.fma((double)matrix3fc.m00(), this.x, Math.fma((double)matrix3fc.m10(), this.y, (double)matrix3fc.m20() * this.z));
		double double2 = Math.fma((double)matrix3fc.m01(), this.x, Math.fma((double)matrix3fc.m11(), this.y, (double)matrix3fc.m21() * this.z));
		double double3 = Math.fma((double)matrix3fc.m02(), this.x, Math.fma((double)matrix3fc.m12(), this.y, (double)matrix3fc.m22() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mul(Matrix3x2dc matrix3x2dc) {
		double double1 = Math.fma(matrix3x2dc.m00(), this.x, Math.fma(matrix3x2dc.m10(), this.y, matrix3x2dc.m20() * this.z));
		double double2 = Math.fma(matrix3x2dc.m01(), this.x, Math.fma(matrix3x2dc.m11(), this.y, matrix3x2dc.m21() * this.z));
		this.x = double1;
		this.y = double2;
		return this;
	}

	public Vector3d mul(Matrix3x2dc matrix3x2dc, Vector3d vector3d) {
		double double1 = Math.fma(matrix3x2dc.m00(), this.x, Math.fma(matrix3x2dc.m10(), this.y, matrix3x2dc.m20() * this.z));
		double double2 = Math.fma(matrix3x2dc.m01(), this.x, Math.fma(matrix3x2dc.m11(), this.y, matrix3x2dc.m21() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = this.z;
		return vector3d;
	}

	public Vector3d mul(Matrix3x2fc matrix3x2fc) {
		double double1 = Math.fma((double)matrix3x2fc.m00(), this.x, Math.fma((double)matrix3x2fc.m10(), this.y, (double)matrix3x2fc.m20() * this.z));
		double double2 = Math.fma((double)matrix3x2fc.m01(), this.x, Math.fma((double)matrix3x2fc.m11(), this.y, (double)matrix3x2fc.m21() * this.z));
		this.x = double1;
		this.y = double2;
		return this;
	}

	public Vector3d mul(Matrix3x2fc matrix3x2fc, Vector3d vector3d) {
		double double1 = Math.fma((double)matrix3x2fc.m00(), this.x, Math.fma((double)matrix3x2fc.m10(), this.y, (double)matrix3x2fc.m20() * this.z));
		double double2 = Math.fma((double)matrix3x2fc.m01(), this.x, Math.fma((double)matrix3x2fc.m11(), this.y, (double)matrix3x2fc.m21() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = this.z;
		return vector3d;
	}

	public Vector3d mulTranspose(Matrix3dc matrix3dc) {
		double double1 = Math.fma(matrix3dc.m00(), this.x, Math.fma(matrix3dc.m01(), this.y, matrix3dc.m02() * this.z));
		double double2 = Math.fma(matrix3dc.m10(), this.x, Math.fma(matrix3dc.m11(), this.y, matrix3dc.m12() * this.z));
		double double3 = Math.fma(matrix3dc.m20(), this.x, Math.fma(matrix3dc.m21(), this.y, matrix3dc.m22() * this.z));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulTranspose(Matrix3dc matrix3dc, Vector3d vector3d) {
		double double1 = Math.fma(matrix3dc.m00(), this.x, Math.fma(matrix3dc.m01(), this.y, matrix3dc.m02() * this.z));
		double double2 = Math.fma(matrix3dc.m10(), this.x, Math.fma(matrix3dc.m11(), this.y, matrix3dc.m12() * this.z));
		double double3 = Math.fma(matrix3dc.m20(), this.x, Math.fma(matrix3dc.m21(), this.y, matrix3dc.m22() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulTranspose(Matrix3fc matrix3fc) {
		double double1 = Math.fma((double)matrix3fc.m00(), this.x, Math.fma((double)matrix3fc.m01(), this.y, (double)matrix3fc.m02() * this.z));
		double double2 = Math.fma((double)matrix3fc.m10(), this.x, Math.fma((double)matrix3fc.m11(), this.y, (double)matrix3fc.m12() * this.z));
		double double3 = Math.fma((double)matrix3fc.m20(), this.x, Math.fma((double)matrix3fc.m21(), this.y, (double)matrix3fc.m22() * this.z));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulTranspose(Matrix3fc matrix3fc, Vector3d vector3d) {
		double double1 = Math.fma((double)matrix3fc.m00(), this.x, Math.fma((double)matrix3fc.m01(), this.y, (double)matrix3fc.m02() * this.z));
		double double2 = Math.fma((double)matrix3fc.m10(), this.x, Math.fma((double)matrix3fc.m11(), this.y, (double)matrix3fc.m12() * this.z));
		double double3 = Math.fma((double)matrix3fc.m20(), this.x, Math.fma((double)matrix3fc.m21(), this.y, (double)matrix3fc.m22() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulPosition(Matrix4fc matrix4fc) {
		double double1 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m10(), this.y, Math.fma((double)matrix4fc.m20(), this.z, (double)matrix4fc.m30())));
		double double2 = Math.fma((double)matrix4fc.m01(), this.x, Math.fma((double)matrix4fc.m11(), this.y, Math.fma((double)matrix4fc.m21(), this.z, (double)matrix4fc.m31())));
		double double3 = Math.fma((double)matrix4fc.m02(), this.x, Math.fma((double)matrix4fc.m12(), this.y, Math.fma((double)matrix4fc.m22(), this.z, (double)matrix4fc.m32())));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulPosition(Matrix4dc matrix4dc) {
		double double1 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30())));
		double double2 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31())));
		double double3 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32())));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulPosition(Matrix4x3dc matrix4x3dc) {
		double double1 = Math.fma(matrix4x3dc.m00(), this.x, Math.fma(matrix4x3dc.m10(), this.y, Math.fma(matrix4x3dc.m20(), this.z, matrix4x3dc.m30())));
		double double2 = Math.fma(matrix4x3dc.m01(), this.x, Math.fma(matrix4x3dc.m11(), this.y, Math.fma(matrix4x3dc.m21(), this.z, matrix4x3dc.m31())));
		double double3 = Math.fma(matrix4x3dc.m02(), this.x, Math.fma(matrix4x3dc.m12(), this.y, Math.fma(matrix4x3dc.m22(), this.z, matrix4x3dc.m32())));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulPosition(Matrix4x3fc matrix4x3fc) {
		double double1 = Math.fma((double)matrix4x3fc.m00(), this.x, Math.fma((double)matrix4x3fc.m10(), this.y, Math.fma((double)matrix4x3fc.m20(), this.z, (double)matrix4x3fc.m30())));
		double double2 = Math.fma((double)matrix4x3fc.m01(), this.x, Math.fma((double)matrix4x3fc.m11(), this.y, Math.fma((double)matrix4x3fc.m21(), this.z, (double)matrix4x3fc.m31())));
		double double3 = Math.fma((double)matrix4x3fc.m02(), this.x, Math.fma((double)matrix4x3fc.m12(), this.y, Math.fma((double)matrix4x3fc.m22(), this.z, (double)matrix4x3fc.m32())));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulPosition(Matrix4dc matrix4dc, Vector3d vector3d) {
		double double1 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30())));
		double double2 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31())));
		double double3 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32())));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulPosition(Matrix4fc matrix4fc, Vector3d vector3d) {
		double double1 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m10(), this.y, Math.fma((double)matrix4fc.m20(), this.z, (double)matrix4fc.m30())));
		double double2 = Math.fma((double)matrix4fc.m01(), this.x, Math.fma((double)matrix4fc.m11(), this.y, Math.fma((double)matrix4fc.m21(), this.z, (double)matrix4fc.m31())));
		double double3 = Math.fma((double)matrix4fc.m02(), this.x, Math.fma((double)matrix4fc.m12(), this.y, Math.fma((double)matrix4fc.m22(), this.z, (double)matrix4fc.m32())));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulPosition(Matrix4x3dc matrix4x3dc, Vector3d vector3d) {
		double double1 = Math.fma(matrix4x3dc.m00(), this.x, Math.fma(matrix4x3dc.m10(), this.y, Math.fma(matrix4x3dc.m20(), this.z, matrix4x3dc.m30())));
		double double2 = Math.fma(matrix4x3dc.m01(), this.x, Math.fma(matrix4x3dc.m11(), this.y, Math.fma(matrix4x3dc.m21(), this.z, matrix4x3dc.m31())));
		double double3 = Math.fma(matrix4x3dc.m02(), this.x, Math.fma(matrix4x3dc.m12(), this.y, Math.fma(matrix4x3dc.m22(), this.z, matrix4x3dc.m32())));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulPosition(Matrix4x3fc matrix4x3fc, Vector3d vector3d) {
		double double1 = Math.fma((double)matrix4x3fc.m00(), this.x, Math.fma((double)matrix4x3fc.m10(), this.y, Math.fma((double)matrix4x3fc.m20(), this.z, (double)matrix4x3fc.m30())));
		double double2 = Math.fma((double)matrix4x3fc.m01(), this.x, Math.fma((double)matrix4x3fc.m11(), this.y, Math.fma((double)matrix4x3fc.m21(), this.z, (double)matrix4x3fc.m31())));
		double double3 = Math.fma((double)matrix4x3fc.m02(), this.x, Math.fma((double)matrix4x3fc.m12(), this.y, Math.fma((double)matrix4x3fc.m22(), this.z, (double)matrix4x3fc.m32())));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulTransposePosition(Matrix4dc matrix4dc) {
		double double1 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m01(), this.y, Math.fma(matrix4dc.m02(), this.z, matrix4dc.m03())));
		double double2 = Math.fma(matrix4dc.m10(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m12(), this.z, matrix4dc.m13())));
		double double3 = Math.fma(matrix4dc.m20(), this.x, Math.fma(matrix4dc.m21(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m23())));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulTransposePosition(Matrix4dc matrix4dc, Vector3d vector3d) {
		double double1 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m01(), this.y, Math.fma(matrix4dc.m02(), this.z, matrix4dc.m03())));
		double double2 = Math.fma(matrix4dc.m10(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m12(), this.z, matrix4dc.m13())));
		double double3 = Math.fma(matrix4dc.m20(), this.x, Math.fma(matrix4dc.m21(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m23())));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulTransposePosition(Matrix4fc matrix4fc) {
		double double1 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m01(), this.y, Math.fma((double)matrix4fc.m02(), this.z, (double)matrix4fc.m03())));
		double double2 = Math.fma((double)matrix4fc.m10(), this.x, Math.fma((double)matrix4fc.m11(), this.y, Math.fma((double)matrix4fc.m12(), this.z, (double)matrix4fc.m13())));
		double double3 = Math.fma((double)matrix4fc.m20(), this.x, Math.fma((double)matrix4fc.m21(), this.y, Math.fma((double)matrix4fc.m22(), this.z, (double)matrix4fc.m23())));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulTransposePosition(Matrix4fc matrix4fc, Vector3d vector3d) {
		double double1 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m01(), this.y, Math.fma((double)matrix4fc.m02(), this.z, (double)matrix4fc.m03())));
		double double2 = Math.fma((double)matrix4fc.m10(), this.x, Math.fma((double)matrix4fc.m11(), this.y, Math.fma((double)matrix4fc.m12(), this.z, (double)matrix4fc.m13())));
		double double3 = Math.fma((double)matrix4fc.m20(), this.x, Math.fma((double)matrix4fc.m21(), this.y, Math.fma((double)matrix4fc.m22(), this.z, (double)matrix4fc.m23())));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public double mulPositionW(Matrix4fc matrix4fc) {
		double double1 = Math.fma((double)matrix4fc.m03(), this.x, Math.fma((double)matrix4fc.m13(), this.y, Math.fma((double)matrix4fc.m23(), this.z, (double)matrix4fc.m33())));
		double double2 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m10(), this.y, Math.fma((double)matrix4fc.m20(), this.z, (double)matrix4fc.m30())));
		double double3 = Math.fma((double)matrix4fc.m01(), this.x, Math.fma((double)matrix4fc.m11(), this.y, Math.fma((double)matrix4fc.m21(), this.z, (double)matrix4fc.m31())));
		double double4 = Math.fma((double)matrix4fc.m02(), this.x, Math.fma((double)matrix4fc.m12(), this.y, Math.fma((double)matrix4fc.m22(), this.z, (double)matrix4fc.m32())));
		this.x = double2;
		this.y = double3;
		this.z = double4;
		return double1;
	}

	public double mulPositionW(Matrix4fc matrix4fc, Vector3d vector3d) {
		double double1 = Math.fma((double)matrix4fc.m03(), this.x, Math.fma((double)matrix4fc.m13(), this.y, Math.fma((double)matrix4fc.m23(), this.z, (double)matrix4fc.m33())));
		double double2 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m10(), this.y, Math.fma((double)matrix4fc.m20(), this.z, (double)matrix4fc.m30())));
		double double3 = Math.fma((double)matrix4fc.m01(), this.x, Math.fma((double)matrix4fc.m11(), this.y, Math.fma((double)matrix4fc.m21(), this.z, (double)matrix4fc.m31())));
		double double4 = Math.fma((double)matrix4fc.m02(), this.x, Math.fma((double)matrix4fc.m12(), this.y, Math.fma((double)matrix4fc.m22(), this.z, (double)matrix4fc.m32())));
		vector3d.x = double2;
		vector3d.y = double3;
		vector3d.z = double4;
		return double1;
	}

	public double mulPositionW(Matrix4dc matrix4dc) {
		double double1 = Math.fma(matrix4dc.m03(), this.x, Math.fma(matrix4dc.m13(), this.y, Math.fma(matrix4dc.m23(), this.z, matrix4dc.m33())));
		double double2 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30())));
		double double3 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31())));
		double double4 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32())));
		this.x = double2;
		this.y = double3;
		this.z = double4;
		return double1;
	}

	public double mulPositionW(Matrix4dc matrix4dc, Vector3d vector3d) {
		double double1 = Math.fma(matrix4dc.m03(), this.x, Math.fma(matrix4dc.m13(), this.y, Math.fma(matrix4dc.m23(), this.z, matrix4dc.m33())));
		double double2 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30())));
		double double3 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31())));
		double double4 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32())));
		vector3d.x = double2;
		vector3d.y = double3;
		vector3d.z = double4;
		return double1;
	}

	public Vector3d mulDirection(Matrix4fc matrix4fc) {
		double double1 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m10(), this.y, (double)matrix4fc.m20() * this.z));
		double double2 = Math.fma((double)matrix4fc.m01(), this.x, Math.fma((double)matrix4fc.m11(), this.y, (double)matrix4fc.m21() * this.z));
		double double3 = Math.fma((double)matrix4fc.m02(), this.x, Math.fma((double)matrix4fc.m12(), this.y, (double)matrix4fc.m22() * this.z));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulDirection(Matrix4dc matrix4dc) {
		double double1 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, matrix4dc.m20() * this.z));
		double double2 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, matrix4dc.m21() * this.z));
		double double3 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, matrix4dc.m22() * this.z));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulDirection(Matrix4x3dc matrix4x3dc) {
		double double1 = Math.fma(matrix4x3dc.m00(), this.x, Math.fma(matrix4x3dc.m10(), this.y, matrix4x3dc.m20() * this.z));
		double double2 = Math.fma(matrix4x3dc.m01(), this.x, Math.fma(matrix4x3dc.m11(), this.y, matrix4x3dc.m21() * this.z));
		double double3 = Math.fma(matrix4x3dc.m02(), this.x, Math.fma(matrix4x3dc.m12(), this.y, matrix4x3dc.m22() * this.z));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulDirection(Matrix4x3fc matrix4x3fc) {
		double double1 = Math.fma((double)matrix4x3fc.m00(), this.x, Math.fma((double)matrix4x3fc.m10(), this.y, (double)matrix4x3fc.m20() * this.z));
		double double2 = Math.fma((double)matrix4x3fc.m01(), this.x, Math.fma((double)matrix4x3fc.m11(), this.y, (double)matrix4x3fc.m21() * this.z));
		double double3 = Math.fma((double)matrix4x3fc.m02(), this.x, Math.fma((double)matrix4x3fc.m12(), this.y, (double)matrix4x3fc.m22() * this.z));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulDirection(Matrix4dc matrix4dc, Vector3d vector3d) {
		double double1 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, matrix4dc.m20() * this.z));
		double double2 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, matrix4dc.m21() * this.z));
		double double3 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, matrix4dc.m22() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulDirection(Matrix4fc matrix4fc, Vector3d vector3d) {
		double double1 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m10(), this.y, (double)matrix4fc.m20() * this.z));
		double double2 = Math.fma((double)matrix4fc.m01(), this.x, Math.fma((double)matrix4fc.m11(), this.y, (double)matrix4fc.m21() * this.z));
		double double3 = Math.fma((double)matrix4fc.m02(), this.x, Math.fma((double)matrix4fc.m12(), this.y, (double)matrix4fc.m22() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulDirection(Matrix4x3dc matrix4x3dc, Vector3d vector3d) {
		double double1 = Math.fma(matrix4x3dc.m00(), this.x, Math.fma(matrix4x3dc.m10(), this.y, matrix4x3dc.m20() * this.z));
		double double2 = Math.fma(matrix4x3dc.m01(), this.x, Math.fma(matrix4x3dc.m11(), this.y, matrix4x3dc.m21() * this.z));
		double double3 = Math.fma(matrix4x3dc.m02(), this.x, Math.fma(matrix4x3dc.m12(), this.y, matrix4x3dc.m22() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulDirection(Matrix4x3fc matrix4x3fc, Vector3d vector3d) {
		double double1 = Math.fma((double)matrix4x3fc.m00(), this.x, Math.fma((double)matrix4x3fc.m10(), this.y, (double)matrix4x3fc.m20() * this.z));
		double double2 = Math.fma((double)matrix4x3fc.m01(), this.x, Math.fma((double)matrix4x3fc.m11(), this.y, (double)matrix4x3fc.m21() * this.z));
		double double3 = Math.fma((double)matrix4x3fc.m02(), this.x, Math.fma((double)matrix4x3fc.m12(), this.y, (double)matrix4x3fc.m22() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulTransposeDirection(Matrix4dc matrix4dc) {
		double double1 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m01(), this.y, matrix4dc.m02() * this.z));
		double double2 = Math.fma(matrix4dc.m10(), this.x, Math.fma(matrix4dc.m11(), this.y, matrix4dc.m12() * this.z));
		double double3 = Math.fma(matrix4dc.m20(), this.x, Math.fma(matrix4dc.m21(), this.y, matrix4dc.m22() * this.z));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulTransposeDirection(Matrix4dc matrix4dc, Vector3d vector3d) {
		double double1 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m01(), this.y, matrix4dc.m02() * this.z));
		double double2 = Math.fma(matrix4dc.m10(), this.x, Math.fma(matrix4dc.m11(), this.y, matrix4dc.m12() * this.z));
		double double3 = Math.fma(matrix4dc.m20(), this.x, Math.fma(matrix4dc.m21(), this.y, matrix4dc.m22() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d mulTransposeDirection(Matrix4fc matrix4fc) {
		double double1 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m01(), this.y, (double)matrix4fc.m02() * this.z));
		double double2 = Math.fma((double)matrix4fc.m10(), this.x, Math.fma((double)matrix4fc.m11(), this.y, (double)matrix4fc.m12() * this.z));
		double double3 = Math.fma((double)matrix4fc.m20(), this.x, Math.fma((double)matrix4fc.m21(), this.y, (double)matrix4fc.m22() * this.z));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d mulTransposeDirection(Matrix4fc matrix4fc, Vector3d vector3d) {
		double double1 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m01(), this.y, (double)matrix4fc.m02() * this.z));
		double double2 = Math.fma((double)matrix4fc.m10(), this.x, Math.fma((double)matrix4fc.m11(), this.y, (double)matrix4fc.m12() * this.z));
		double double3 = Math.fma((double)matrix4fc.m20(), this.x, Math.fma((double)matrix4fc.m21(), this.y, (double)matrix4fc.m22() * this.z));
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
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
		return quaterniondc.transform((Vector3dc)this, (Vector3d)this);
	}

	public Vector3d rotate(Quaterniondc quaterniondc, Vector3d vector3d) {
		return quaterniondc.transform((Vector3dc)this, (Vector3d)vector3d);
	}

	public Quaterniond rotationTo(Vector3dc vector3dc, Quaterniond quaterniond) {
		return quaterniond.rotationTo(this, vector3dc);
	}

	public Quaterniond rotationTo(double double1, double double2, double double3, Quaterniond quaterniond) {
		return quaterniond.rotationTo(this.x, this.y, this.z, double1, double2, double3);
	}

	public Vector3d rotateAxis(double double1, double double2, double double3, double double4) {
		if (double3 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double2)) {
			return this.rotateX(double2 * double1, this);
		} else if (double2 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double3)) {
			return this.rotateY(double3 * double1, this);
		} else {
			return double2 == 0.0 && double3 == 0.0 && Math.absEqualsOne(double4) ? this.rotateZ(double4 * double1, this) : this.rotateAxisInternal(double1, double2, double3, double4, this);
		}
	}

	public Vector3d rotateAxis(double double1, double double2, double double3, double double4, Vector3d vector3d) {
		if (double3 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double2)) {
			return this.rotateX(double2 * double1, vector3d);
		} else if (double2 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double3)) {
			return this.rotateY(double3 * double1, vector3d);
		} else {
			return double2 == 0.0 && double3 == 0.0 && Math.absEqualsOne(double4) ? this.rotateZ(double4 * double1, vector3d) : this.rotateAxisInternal(double1, double2, double3, double4, vector3d);
		}
	}

	private Vector3d rotateAxisInternal(double double1, double double2, double double3, double double4, Vector3d vector3d) {
		double double5 = double1 * 0.5;
		double double6 = Math.sin(double5);
		double double7 = double2 * double6;
		double double8 = double3 * double6;
		double double9 = double4 * double6;
		double double10 = Math.cosFromSin(double6, double5);
		double double11 = double10 * double10;
		double double12 = double7 * double7;
		double double13 = double8 * double8;
		double double14 = double9 * double9;
		double double15 = double9 * double10;
		double double16 = double7 * double8;
		double double17 = double7 * double9;
		double double18 = double8 * double10;
		double double19 = double8 * double9;
		double double20 = double7 * double10;
		double double21 = (double11 + double12 - double14 - double13) * this.x + (-double15 + double16 - double15 + double16) * this.y + (double18 + double17 + double17 + double18) * this.z;
		double double22 = (double16 + double15 + double15 + double16) * this.x + (double13 - double14 + double11 - double12) * this.y + (double19 + double19 - double20 - double20) * this.z;
		double double23 = (double17 - double18 + double17 - double18) * this.x + (double19 + double19 + double20 + double20) * this.y + (double14 - double13 - double12 + double11) * this.z;
		vector3d.x = double21;
		vector3d.y = double22;
		vector3d.z = double23;
		return vector3d;
	}

	public Vector3d rotateX(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.y * double3 - this.z * double2;
		double double5 = this.y * double2 + this.z * double3;
		this.y = double4;
		this.z = double5;
		return this;
	}

	public Vector3d rotateX(double double1, Vector3d vector3d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.y * double3 - this.z * double2;
		double double5 = this.y * double2 + this.z * double3;
		vector3d.x = this.x;
		vector3d.y = double4;
		vector3d.z = double5;
		return vector3d;
	}

	public Vector3d rotateY(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.x * double3 + this.z * double2;
		double double5 = -this.x * double2 + this.z * double3;
		this.x = double4;
		this.z = double5;
		return this;
	}

	public Vector3d rotateY(double double1, Vector3d vector3d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.x * double3 + this.z * double2;
		double double5 = -this.x * double2 + this.z * double3;
		vector3d.x = double4;
		vector3d.y = this.y;
		vector3d.z = double5;
		return vector3d;
	}

	public Vector3d rotateZ(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.x * double3 - this.y * double2;
		double double5 = this.x * double2 + this.y * double3;
		this.x = double4;
		this.y = double5;
		return this;
	}

	public Vector3d rotateZ(double double1, Vector3d vector3d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.x * double3 - this.y * double2;
		double double5 = this.x * double2 + this.y * double3;
		vector3d.x = double4;
		vector3d.y = double5;
		vector3d.z = this.z;
		return vector3d;
	}

	public Vector3d div(double double1) {
		double double2 = 1.0 / double1;
		this.x *= double2;
		this.y *= double2;
		this.z *= double2;
		return this;
	}

	public Vector3d div(double double1, Vector3d vector3d) {
		double double2 = 1.0 / double1;
		vector3d.x = this.x * double2;
		vector3d.y = this.y * double2;
		vector3d.z = this.z * double2;
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
		return Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z));
	}

	public static double lengthSquared(double double1, double double2, double double3) {
		return Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3));
	}

	public double length() {
		return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
	}

	public static double length(double double1, double double2, double double3) {
		return Math.sqrt(Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3)));
	}

	public Vector3d normalize() {
		double double1 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		return this;
	}

	public Vector3d normalize(Vector3d vector3d) {
		double double1 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
		vector3d.x = this.x * double1;
		vector3d.y = this.y * double1;
		vector3d.z = this.z * double1;
		return vector3d;
	}

	public Vector3d normalize(double double1) {
		double double2 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z))) * double1;
		this.x *= double2;
		this.y *= double2;
		this.z *= double2;
		return this;
	}

	public Vector3d normalize(double double1, Vector3d vector3d) {
		double double2 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z))) * double1;
		vector3d.x = this.x * double2;
		vector3d.y = this.y * double2;
		vector3d.z = this.z * double2;
		return vector3d;
	}

	public Vector3d cross(Vector3dc vector3dc) {
		double double1 = Math.fma(this.y, vector3dc.z(), -this.z * vector3dc.y());
		double double2 = Math.fma(this.z, vector3dc.x(), -this.x * vector3dc.z());
		double double3 = Math.fma(this.x, vector3dc.y(), -this.y * vector3dc.x());
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector3d cross(double double1, double double2, double double3) {
		double double4 = Math.fma(this.y, double3, -this.z * double2);
		double double5 = Math.fma(this.z, double1, -this.x * double3);
		double double6 = Math.fma(this.x, double2, -this.y * double1);
		this.x = double4;
		this.y = double5;
		this.z = double6;
		return this;
	}

	public Vector3d cross(Vector3dc vector3dc, Vector3d vector3d) {
		double double1 = Math.fma(this.y, vector3dc.z(), -this.z * vector3dc.y());
		double double2 = Math.fma(this.z, vector3dc.x(), -this.x * vector3dc.z());
		double double3 = Math.fma(this.x, vector3dc.y(), -this.y * vector3dc.x());
		vector3d.x = double1;
		vector3d.y = double2;
		vector3d.z = double3;
		return vector3d;
	}

	public Vector3d cross(double double1, double double2, double double3, Vector3d vector3d) {
		double double4 = Math.fma(this.y, double3, -this.z * double2);
		double double5 = Math.fma(this.z, double1, -this.x * double3);
		double double6 = Math.fma(this.x, double2, -this.y * double1);
		vector3d.x = double4;
		vector3d.y = double5;
		vector3d.z = double6;
		return vector3d;
	}

	public double distance(Vector3dc vector3dc) {
		double double1 = this.x - vector3dc.x();
		double double2 = this.y - vector3dc.y();
		double double3 = this.z - vector3dc.z();
		return Math.sqrt(Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3)));
	}

	public double distance(double double1, double double2, double double3) {
		double double4 = this.x - double1;
		double double5 = this.y - double2;
		double double6 = this.z - double3;
		return Math.sqrt(Math.fma(double4, double4, Math.fma(double5, double5, double6 * double6)));
	}

	public double distanceSquared(Vector3dc vector3dc) {
		double double1 = this.x - vector3dc.x();
		double double2 = this.y - vector3dc.y();
		double double3 = this.z - vector3dc.z();
		return Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3));
	}

	public double distanceSquared(double double1, double double2, double double3) {
		double double4 = this.x - double1;
		double double5 = this.y - double2;
		double double6 = this.z - double3;
		return Math.fma(double4, double4, Math.fma(double5, double5, double6 * double6));
	}

	public static double distance(double double1, double double2, double double3, double double4, double double5, double double6) {
		return Math.sqrt(distanceSquared(double1, double2, double3, double4, double5, double6));
	}

	public static double distanceSquared(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = double1 - double4;
		double double8 = double2 - double5;
		double double9 = double3 - double6;
		return Math.fma(double7, double7, Math.fma(double8, double8, double9 * double9));
	}

	public double dot(Vector3dc vector3dc) {
		return Math.fma(this.x, vector3dc.x(), Math.fma(this.y, vector3dc.y(), this.z * vector3dc.z()));
	}

	public double dot(double double1, double double2, double double3) {
		return Math.fma(this.x, double1, Math.fma(this.y, double2, this.z * double3));
	}

	public double angleCos(Vector3dc vector3dc) {
		double double1 = Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z));
		double double2 = Math.fma(vector3dc.x(), vector3dc.x(), Math.fma(vector3dc.y(), vector3dc.y(), vector3dc.z() * vector3dc.z()));
		double double3 = Math.fma(this.x, vector3dc.x(), Math.fma(this.y, vector3dc.y(), this.z * vector3dc.z()));
		return double3 / Math.sqrt(double1 * double2);
	}

	public double angle(Vector3dc vector3dc) {
		double double1 = this.angleCos(vector3dc);
		double1 = double1 < 1.0 ? double1 : 1.0;
		double1 = double1 > -1.0 ? double1 : -1.0;
		return Math.acos(double1);
	}

	public double angleSigned(Vector3dc vector3dc, Vector3dc vector3dc2) {
		double double1 = vector3dc.x();
		double double2 = vector3dc.y();
		double double3 = vector3dc.z();
		return Math.atan2((this.y * double3 - this.z * double2) * vector3dc2.x() + (this.z * double1 - this.x * double3) * vector3dc2.y() + (this.x * double2 - this.y * double1) * vector3dc2.z(), this.x * double1 + this.y * double2 + this.z * double3);
	}

	public double angleSigned(double double1, double double2, double double3, double double4, double double5, double double6) {
		return Math.atan2((this.y * double3 - this.z * double2) * double4 + (this.z * double1 - this.x * double3) * double5 + (this.x * double2 - this.y * double1) * double6, this.x * double1 + this.y * double2 + this.z * double3);
	}

	public Vector3d min(Vector3dc vector3dc) {
		this.x = this.x < vector3dc.x() ? this.x : vector3dc.x();
		this.y = this.y < vector3dc.y() ? this.y : vector3dc.y();
		this.z = this.z < vector3dc.z() ? this.z : vector3dc.z();
		return this;
	}

	public Vector3d min(Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.x = this.x < vector3dc.x() ? this.x : vector3dc.x();
		vector3d.y = this.y < vector3dc.y() ? this.y : vector3dc.y();
		vector3d.z = this.z < vector3dc.z() ? this.z : vector3dc.z();
		return vector3d;
	}

	public Vector3d max(Vector3dc vector3dc) {
		this.x = this.x > vector3dc.x() ? this.x : vector3dc.x();
		this.y = this.y > vector3dc.y() ? this.y : vector3dc.y();
		this.z = this.z > vector3dc.z() ? this.z : vector3dc.z();
		return this;
	}

	public Vector3d max(Vector3dc vector3dc, Vector3d vector3d) {
		vector3d.x = this.x > vector3dc.x() ? this.x : vector3dc.x();
		vector3d.y = this.y > vector3dc.y() ? this.y : vector3dc.y();
		vector3d.z = this.z > vector3dc.z() ? this.z : vector3dc.z();
		return vector3d;
	}

	public Vector3d zero() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
		return this;
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format(this.x, numberFormat);
		return "(" + string + " " + Runtime.format(this.y, numberFormat) + " " + Runtime.format(this.z, numberFormat) + ")";
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

	public Vector3d absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
		return this;
	}

	public Vector3d absolute(Vector3d vector3d) {
		vector3d.x = Math.abs(this.x);
		vector3d.y = Math.abs(this.y);
		vector3d.z = Math.abs(this.z);
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

	public boolean equals(Vector3dc vector3dc, double double1) {
		if (this == vector3dc) {
			return true;
		} else if (vector3dc == null) {
			return false;
		} else if (!(vector3dc instanceof Vector3dc)) {
			return false;
		} else if (!Runtime.equals(this.x, vector3dc.x(), double1)) {
			return false;
		} else if (!Runtime.equals(this.y, vector3dc.y(), double1)) {
			return false;
		} else {
			return Runtime.equals(this.z, vector3dc.z(), double1);
		}
	}

	public boolean equals(double double1, double double2, double double3) {
		if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(double1)) {
			return false;
		} else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(double2)) {
			return false;
		} else {
			return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(double3);
		}
	}

	public Vector3d reflect(Vector3dc vector3dc) {
		double double1 = vector3dc.x();
		double double2 = vector3dc.y();
		double double3 = vector3dc.z();
		double double4 = Math.fma(this.x, double1, Math.fma(this.y, double2, this.z * double3));
		this.x -= (double4 + double4) * double1;
		this.y -= (double4 + double4) * double2;
		this.z -= (double4 + double4) * double3;
		return this;
	}

	public Vector3d reflect(double double1, double double2, double double3) {
		double double4 = Math.fma(this.x, double1, Math.fma(this.y, double2, this.z * double3));
		this.x -= (double4 + double4) * double1;
		this.y -= (double4 + double4) * double2;
		this.z -= (double4 + double4) * double3;
		return this;
	}

	public Vector3d reflect(Vector3dc vector3dc, Vector3d vector3d) {
		double double1 = vector3dc.x();
		double double2 = vector3dc.y();
		double double3 = vector3dc.z();
		double double4 = Math.fma(this.x, double1, Math.fma(this.y, double2, this.z * double3));
		vector3d.x = this.x - (double4 + double4) * double1;
		vector3d.y = this.y - (double4 + double4) * double2;
		vector3d.z = this.z - (double4 + double4) * double3;
		return vector3d;
	}

	public Vector3d reflect(double double1, double double2, double double3, Vector3d vector3d) {
		double double4 = Math.fma(this.x, double1, Math.fma(this.y, double2, this.z * double3));
		vector3d.x = this.x - (double4 + double4) * double1;
		vector3d.y = this.y - (double4 + double4) * double2;
		vector3d.z = this.z - (double4 + double4) * double3;
		return vector3d;
	}

	public Vector3d half(Vector3dc vector3dc) {
		return this.set((Vector3dc)this).add(vector3dc.x(), vector3dc.y(), vector3dc.z()).normalize();
	}

	public Vector3d half(double double1, double double2, double double3) {
		return this.set((Vector3dc)this).add(double1, double2, double3).normalize();
	}

	public Vector3d half(Vector3dc vector3dc, Vector3d vector3d) {
		return vector3d.set((Vector3dc)this).add(vector3dc.x(), vector3dc.y(), vector3dc.z()).normalize();
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
		this.x = Math.fma(vector3dc.x() - this.x, double1, this.x);
		this.y = Math.fma(vector3dc.y() - this.y, double1, this.y);
		this.z = Math.fma(vector3dc.z() - this.z, double1, this.z);
		return this;
	}

	public Vector3d lerp(Vector3dc vector3dc, double double1, Vector3d vector3d) {
		vector3d.x = Math.fma(vector3dc.x() - this.x, double1, this.x);
		vector3d.y = Math.fma(vector3dc.y() - this.y, double1, this.y);
		vector3d.z = Math.fma(vector3dc.z() - this.z, double1, this.z);
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

	public Vector3i get(int int1, Vector3i vector3i) {
		vector3i.x = Math.roundUsing(this.x(), int1);
		vector3i.y = Math.roundUsing(this.y(), int1);
		vector3i.z = Math.roundUsing(this.z(), int1);
		return vector3i;
	}

	public Vector3f get(Vector3f vector3f) {
		vector3f.x = (float)this.x();
		vector3f.y = (float)this.y();
		vector3f.z = (float)this.z();
		return vector3f;
	}

	public Vector3d get(Vector3d vector3d) {
		vector3d.x = this.x();
		vector3d.y = this.y();
		vector3d.z = this.z();
		return vector3d;
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
		double double1;
		double double2;
		double double3;
		if (Math.abs(vector3dc.x()) > Math.abs(vector3dc.z())) {
			double1 = -vector3dc.y();
			double2 = vector3dc.x();
			double3 = 0.0;
		} else {
			double1 = 0.0;
			double2 = -vector3dc.z();
			double3 = vector3dc.y();
		}

		double double4 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		vector3d.x = double1 * double4;
		vector3d.y = double2 * double4;
		vector3d.z = double3 * double4;
		return vector3d;
	}

	public Vector3d orthogonalize(Vector3dc vector3dc) {
		return this.orthogonalize(vector3dc, this);
	}

	public Vector3d orthogonalizeUnit(Vector3dc vector3dc, Vector3d vector3d) {
		return this.orthogonalize(vector3dc, vector3d);
	}

	public Vector3d orthogonalizeUnit(Vector3dc vector3dc) {
		return this.orthogonalizeUnit(vector3dc, this);
	}

	public Vector3d floor() {
		this.x = Math.floor(this.x);
		this.y = Math.floor(this.y);
		this.z = Math.floor(this.z);
		return this;
	}

	public Vector3d floor(Vector3d vector3d) {
		vector3d.x = Math.floor(this.x);
		vector3d.y = Math.floor(this.y);
		vector3d.z = Math.floor(this.z);
		return vector3d;
	}

	public Vector3d ceil() {
		this.x = Math.ceil(this.x);
		this.y = Math.ceil(this.y);
		this.z = Math.ceil(this.z);
		return this;
	}

	public Vector3d ceil(Vector3d vector3d) {
		vector3d.x = Math.ceil(this.x);
		vector3d.y = Math.ceil(this.y);
		vector3d.z = Math.ceil(this.z);
		return vector3d;
	}

	public Vector3d round() {
		this.x = (double)Math.round(this.x);
		this.y = (double)Math.round(this.y);
		this.z = (double)Math.round(this.z);
		return this;
	}

	public Vector3d round(Vector3d vector3d) {
		vector3d.x = (double)Math.round(this.x);
		vector3d.y = (double)Math.round(this.y);
		vector3d.z = (double)Math.round(this.z);
		return vector3d;
	}

	public boolean isFinite() {
		return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z);
	}
}
