package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
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

	public Vector4d(Vector4ic vector4ic) {
		this.x = (double)vector4ic.x();
		this.y = (double)vector4ic.y();
		this.z = (double)vector4ic.z();
		this.w = (double)vector4ic.w();
	}

	public Vector4d(Vector3dc vector3dc, double double1) {
		this.x = vector3dc.x();
		this.y = vector3dc.y();
		this.z = vector3dc.z();
		this.w = double1;
	}

	public Vector4d(Vector3ic vector3ic, double double1) {
		this.x = (double)vector3ic.x();
		this.y = (double)vector3ic.y();
		this.z = (double)vector3ic.z();
		this.w = double1;
	}

	public Vector4d(Vector2dc vector2dc, double double1, double double2) {
		this.x = vector2dc.x();
		this.y = vector2dc.y();
		this.z = double1;
		this.w = double2;
	}

	public Vector4d(Vector2ic vector2ic, double double1, double double2) {
		this.x = (double)vector2ic.x();
		this.y = (double)vector2ic.y();
		this.z = double1;
		this.w = double2;
	}

	public Vector4d(double double1) {
		this.x = double1;
		this.y = double1;
		this.z = double1;
		this.w = double1;
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

	public Vector4d(float[] floatArray) {
		this.x = (double)floatArray[0];
		this.y = (double)floatArray[1];
		this.z = (double)floatArray[2];
		this.w = (double)floatArray[3];
	}

	public Vector4d(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
		this.z = doubleArray[2];
		this.w = doubleArray[3];
	}

	public Vector4d(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
	}

	public Vector4d(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector4d(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
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

	public Vector4d set(Vector4ic vector4ic) {
		this.x = (double)vector4ic.x();
		this.y = (double)vector4ic.y();
		this.z = (double)vector4ic.z();
		this.w = (double)vector4ic.w();
		return this;
	}

	public Vector4d set(Vector3dc vector3dc, double double1) {
		this.x = vector3dc.x();
		this.y = vector3dc.y();
		this.z = vector3dc.z();
		this.w = double1;
		return this;
	}

	public Vector4d set(Vector3ic vector3ic, double double1) {
		this.x = (double)vector3ic.x();
		this.y = (double)vector3ic.y();
		this.z = (double)vector3ic.z();
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

	public Vector4d set(Vector2ic vector2ic, double double1, double double2) {
		this.x = (double)vector2ic.x();
		this.y = (double)vector2ic.y();
		this.z = double1;
		this.w = double2;
		return this;
	}

	public Vector4d set(double double1) {
		this.x = double1;
		this.y = double1;
		this.z = double1;
		this.w = double1;
		return this;
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

	public Vector4d set(double double1, double double2, double double3) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector4d set(double[] doubleArray) {
		this.x = doubleArray[0];
		this.y = doubleArray[1];
		this.z = doubleArray[2];
		this.w = doubleArray[2];
		return this;
	}

	public Vector4d set(float[] floatArray) {
		this.x = (double)floatArray[0];
		this.y = (double)floatArray[1];
		this.z = (double)floatArray[2];
		this.w = (double)floatArray[2];
		return this;
	}

	public Vector4d set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Vector4d set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector4d set(DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, doubleBuffer.position(), doubleBuffer);
		return this;
	}

	public Vector4d set(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, int1, doubleBuffer);
		return this;
	}

	public Vector4d setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
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

	public Vector4dc getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public Vector4d sub(Vector4dc vector4dc) {
		this.x -= vector4dc.x();
		this.y -= vector4dc.y();
		this.z -= vector4dc.z();
		this.w -= vector4dc.w();
		return this;
	}

	public Vector4d sub(Vector4dc vector4dc, Vector4d vector4d) {
		vector4d.x = this.x - vector4dc.x();
		vector4d.y = this.y - vector4dc.y();
		vector4d.z = this.z - vector4dc.z();
		vector4d.w = this.w - vector4dc.w();
		return vector4d;
	}

	public Vector4d sub(Vector4fc vector4fc) {
		this.x -= (double)vector4fc.x();
		this.y -= (double)vector4fc.y();
		this.z -= (double)vector4fc.z();
		this.w -= (double)vector4fc.w();
		return this;
	}

	public Vector4d sub(Vector4fc vector4fc, Vector4d vector4d) {
		vector4d.x = this.x - (double)vector4fc.x();
		vector4d.y = this.y - (double)vector4fc.y();
		vector4d.z = this.z - (double)vector4fc.z();
		vector4d.w = this.w - (double)vector4fc.w();
		return vector4d;
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

	public Vector4d add(Vector4dc vector4dc, Vector4d vector4d) {
		vector4d.x = this.x + vector4dc.x();
		vector4d.y = this.y + vector4dc.y();
		vector4d.z = this.z + vector4dc.z();
		vector4d.w = this.w + vector4dc.w();
		return vector4d;
	}

	public Vector4d add(Vector4fc vector4fc, Vector4d vector4d) {
		vector4d.x = this.x + (double)vector4fc.x();
		vector4d.y = this.y + (double)vector4fc.y();
		vector4d.z = this.z + (double)vector4fc.z();
		vector4d.w = this.w + (double)vector4fc.w();
		return vector4d;
	}

	public Vector4d add(double double1, double double2, double double3, double double4) {
		this.x += double1;
		this.y += double2;
		this.z += double3;
		this.w += double4;
		return this;
	}

	public Vector4d add(double double1, double double2, double double3, double double4, Vector4d vector4d) {
		vector4d.x = this.x + double1;
		vector4d.y = this.y + double2;
		vector4d.z = this.z + double3;
		vector4d.w = this.w + double4;
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
		this.x = Math.fma(vector4dc.x(), vector4dc2.x(), this.x);
		this.y = Math.fma(vector4dc.y(), vector4dc2.y(), this.y);
		this.z = Math.fma(vector4dc.z(), vector4dc2.z(), this.z);
		this.w = Math.fma(vector4dc.w(), vector4dc2.w(), this.w);
		return this;
	}

	public Vector4d fma(double double1, Vector4dc vector4dc) {
		this.x = Math.fma(double1, vector4dc.x(), this.x);
		this.y = Math.fma(double1, vector4dc.y(), this.y);
		this.z = Math.fma(double1, vector4dc.z(), this.z);
		this.w = Math.fma(double1, vector4dc.w(), this.w);
		return this;
	}

	public Vector4d fma(Vector4dc vector4dc, Vector4dc vector4dc2, Vector4d vector4d) {
		vector4d.x = Math.fma(vector4dc.x(), vector4dc2.x(), this.x);
		vector4d.y = Math.fma(vector4dc.y(), vector4dc2.y(), this.y);
		vector4d.z = Math.fma(vector4dc.z(), vector4dc2.z(), this.z);
		vector4d.w = Math.fma(vector4dc.w(), vector4dc2.w(), this.w);
		return vector4d;
	}

	public Vector4d fma(double double1, Vector4dc vector4dc, Vector4d vector4d) {
		vector4d.x = Math.fma(double1, vector4dc.x(), this.x);
		vector4d.y = Math.fma(double1, vector4dc.y(), this.y);
		vector4d.z = Math.fma(double1, vector4dc.z(), this.z);
		vector4d.w = Math.fma(double1, vector4dc.w(), this.w);
		return vector4d;
	}

	public Vector4d mulAdd(Vector4dc vector4dc, Vector4dc vector4dc2) {
		this.x = Math.fma(this.x, vector4dc.x(), vector4dc2.x());
		this.y = Math.fma(this.y, vector4dc.y(), vector4dc2.y());
		this.z = Math.fma(this.z, vector4dc.z(), vector4dc2.z());
		return this;
	}

	public Vector4d mulAdd(double double1, Vector4dc vector4dc) {
		this.x = Math.fma(this.x, double1, vector4dc.x());
		this.y = Math.fma(this.y, double1, vector4dc.y());
		this.z = Math.fma(this.z, double1, vector4dc.z());
		return this;
	}

	public Vector4d mulAdd(Vector4dc vector4dc, Vector4dc vector4dc2, Vector4d vector4d) {
		vector4d.x = Math.fma(this.x, vector4dc.x(), vector4dc2.x());
		vector4d.y = Math.fma(this.y, vector4dc.y(), vector4dc2.y());
		vector4d.z = Math.fma(this.z, vector4dc.z(), vector4dc2.z());
		return vector4d;
	}

	public Vector4d mulAdd(double double1, Vector4dc vector4dc, Vector4d vector4d) {
		vector4d.x = Math.fma(this.x, double1, vector4dc.x());
		vector4d.y = Math.fma(this.y, double1, vector4dc.y());
		vector4d.z = Math.fma(this.z, double1, vector4dc.z());
		return vector4d;
	}

	public Vector4d mul(Vector4dc vector4dc) {
		this.x *= vector4dc.x();
		this.y *= vector4dc.y();
		this.z *= vector4dc.z();
		this.w *= vector4dc.w();
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
		this.w /= vector4dc.w();
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
		this.w *= (double)vector4fc.w();
		return this;
	}

	public Vector4d mul(Vector4fc vector4fc, Vector4d vector4d) {
		vector4d.x = this.x * (double)vector4fc.x();
		vector4d.y = this.y * (double)vector4fc.y();
		vector4d.z = this.z * (double)vector4fc.z();
		vector4d.w = this.w * (double)vector4fc.w();
		return vector4d;
	}

	public Vector4d mul(Matrix4dc matrix4dc) {
		return (matrix4dc.properties() & 2) != 0 ? this.mulAffine(matrix4dc, this) : this.mulGeneric(matrix4dc, this);
	}

	public Vector4d mul(Matrix4dc matrix4dc, Vector4d vector4d) {
		return (matrix4dc.properties() & 2) != 0 ? this.mulAffine(matrix4dc, vector4d) : this.mulGeneric(matrix4dc, vector4d);
	}

	public Vector4d mulTranspose(Matrix4dc matrix4dc) {
		return (matrix4dc.properties() & 2) != 0 ? this.mulAffineTranspose(matrix4dc, this) : this.mulGenericTranspose(matrix4dc, this);
	}

	public Vector4d mulTranspose(Matrix4dc matrix4dc, Vector4d vector4d) {
		return (matrix4dc.properties() & 2) != 0 ? this.mulAffineTranspose(matrix4dc, vector4d) : this.mulGenericTranspose(matrix4dc, vector4d);
	}

	public Vector4d mulAffine(Matrix4dc matrix4dc, Vector4d vector4d) {
		double double1 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30() * this.w)));
		double double2 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31() * this.w)));
		double double3 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32() * this.w)));
		vector4d.x = double1;
		vector4d.y = double2;
		vector4d.z = double3;
		vector4d.w = this.w;
		return vector4d;
	}

	private Vector4d mulGeneric(Matrix4dc matrix4dc, Vector4d vector4d) {
		double double1 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30() * this.w)));
		double double2 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31() * this.w)));
		double double3 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32() * this.w)));
		double double4 = Math.fma(matrix4dc.m03(), this.x, Math.fma(matrix4dc.m13(), this.y, Math.fma(matrix4dc.m23(), this.z, matrix4dc.m33() * this.w)));
		vector4d.x = double1;
		vector4d.y = double2;
		vector4d.z = double3;
		vector4d.w = double4;
		return vector4d;
	}

	public Vector4d mulAffineTranspose(Matrix4dc matrix4dc, Vector4d vector4d) {
		double double1 = this.x;
		double double2 = this.y;
		double double3 = this.z;
		double double4 = this.w;
		vector4d.x = Math.fma(matrix4dc.m00(), double1, Math.fma(matrix4dc.m01(), double2, matrix4dc.m02() * double3));
		vector4d.y = Math.fma(matrix4dc.m10(), double1, Math.fma(matrix4dc.m11(), double2, matrix4dc.m12() * double3));
		vector4d.z = Math.fma(matrix4dc.m20(), double1, Math.fma(matrix4dc.m21(), double2, matrix4dc.m22() * double3));
		vector4d.w = Math.fma(matrix4dc.m30(), double1, Math.fma(matrix4dc.m31(), double2, matrix4dc.m32() * double3 + double4));
		return vector4d;
	}

	private Vector4d mulGenericTranspose(Matrix4dc matrix4dc, Vector4d vector4d) {
		double double1 = this.x;
		double double2 = this.y;
		double double3 = this.z;
		double double4 = this.w;
		vector4d.x = Math.fma(matrix4dc.m00(), double1, Math.fma(matrix4dc.m01(), double2, Math.fma(matrix4dc.m02(), double3, matrix4dc.m03() * double4)));
		vector4d.y = Math.fma(matrix4dc.m10(), double1, Math.fma(matrix4dc.m11(), double2, Math.fma(matrix4dc.m12(), double3, matrix4dc.m13() * double4)));
		vector4d.z = Math.fma(matrix4dc.m20(), double1, Math.fma(matrix4dc.m21(), double2, Math.fma(matrix4dc.m22(), double3, matrix4dc.m23() * double4)));
		vector4d.w = Math.fma(matrix4dc.m30(), double1, Math.fma(matrix4dc.m31(), double2, Math.fma(matrix4dc.m32(), double3, matrix4dc.m33() * double4)));
		return vector4d;
	}

	public Vector4d mul(Matrix4x3dc matrix4x3dc) {
		double double1 = Math.fma(matrix4x3dc.m00(), this.x, Math.fma(matrix4x3dc.m10(), this.y, Math.fma(matrix4x3dc.m20(), this.z, matrix4x3dc.m30() * this.w)));
		double double2 = Math.fma(matrix4x3dc.m01(), this.x, Math.fma(matrix4x3dc.m11(), this.y, Math.fma(matrix4x3dc.m21(), this.z, matrix4x3dc.m31() * this.w)));
		double double3 = Math.fma(matrix4x3dc.m02(), this.x, Math.fma(matrix4x3dc.m12(), this.y, Math.fma(matrix4x3dc.m22(), this.z, matrix4x3dc.m32() * this.w)));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector4d mul(Matrix4x3dc matrix4x3dc, Vector4d vector4d) {
		double double1 = Math.fma(matrix4x3dc.m00(), this.x, Math.fma(matrix4x3dc.m10(), this.y, Math.fma(matrix4x3dc.m20(), this.z, matrix4x3dc.m30() * this.w)));
		double double2 = Math.fma(matrix4x3dc.m01(), this.x, Math.fma(matrix4x3dc.m11(), this.y, Math.fma(matrix4x3dc.m21(), this.z, matrix4x3dc.m31() * this.w)));
		double double3 = Math.fma(matrix4x3dc.m02(), this.x, Math.fma(matrix4x3dc.m12(), this.y, Math.fma(matrix4x3dc.m22(), this.z, matrix4x3dc.m32() * this.w)));
		vector4d.x = double1;
		vector4d.y = double2;
		vector4d.z = double3;
		vector4d.w = this.w;
		return vector4d;
	}

	public Vector4d mul(Matrix4x3fc matrix4x3fc) {
		double double1 = Math.fma((double)matrix4x3fc.m00(), this.x, Math.fma((double)matrix4x3fc.m10(), this.y, Math.fma((double)matrix4x3fc.m20(), this.z, (double)matrix4x3fc.m30() * this.w)));
		double double2 = Math.fma((double)matrix4x3fc.m01(), this.x, Math.fma((double)matrix4x3fc.m11(), this.y, Math.fma((double)matrix4x3fc.m21(), this.z, (double)matrix4x3fc.m31() * this.w)));
		double double3 = Math.fma((double)matrix4x3fc.m02(), this.x, Math.fma((double)matrix4x3fc.m12(), this.y, Math.fma((double)matrix4x3fc.m22(), this.z, (double)matrix4x3fc.m32() * this.w)));
		this.x = double1;
		this.y = double2;
		this.z = double3;
		return this;
	}

	public Vector4d mul(Matrix4x3fc matrix4x3fc, Vector4d vector4d) {
		double double1 = Math.fma((double)matrix4x3fc.m00(), this.x, Math.fma((double)matrix4x3fc.m10(), this.y, Math.fma((double)matrix4x3fc.m20(), this.z, (double)matrix4x3fc.m30() * this.w)));
		double double2 = Math.fma((double)matrix4x3fc.m01(), this.x, Math.fma((double)matrix4x3fc.m11(), this.y, Math.fma((double)matrix4x3fc.m21(), this.z, (double)matrix4x3fc.m31() * this.w)));
		double double3 = Math.fma((double)matrix4x3fc.m02(), this.x, Math.fma((double)matrix4x3fc.m12(), this.y, Math.fma((double)matrix4x3fc.m22(), this.z, (double)matrix4x3fc.m32() * this.w)));
		vector4d.x = double1;
		vector4d.y = double2;
		vector4d.z = double3;
		vector4d.w = this.w;
		return vector4d;
	}

	public Vector4d mul(Matrix4fc matrix4fc) {
		return (matrix4fc.properties() & 2) != 0 ? this.mulAffine(matrix4fc, this) : this.mulGeneric(matrix4fc, this);
	}

	public Vector4d mul(Matrix4fc matrix4fc, Vector4d vector4d) {
		return (matrix4fc.properties() & 2) != 0 ? this.mulAffine(matrix4fc, vector4d) : this.mulGeneric(matrix4fc, vector4d);
	}

	private Vector4d mulAffine(Matrix4fc matrix4fc, Vector4d vector4d) {
		double double1 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m10(), this.y, Math.fma((double)matrix4fc.m20(), this.z, (double)matrix4fc.m30() * this.w)));
		double double2 = Math.fma((double)matrix4fc.m01(), this.x, Math.fma((double)matrix4fc.m11(), this.y, Math.fma((double)matrix4fc.m21(), this.z, (double)matrix4fc.m31() * this.w)));
		double double3 = Math.fma((double)matrix4fc.m02(), this.x, Math.fma((double)matrix4fc.m12(), this.y, Math.fma((double)matrix4fc.m22(), this.z, (double)matrix4fc.m32() * this.w)));
		vector4d.x = double1;
		vector4d.y = double2;
		vector4d.z = double3;
		vector4d.w = this.w;
		return vector4d;
	}

	private Vector4d mulGeneric(Matrix4fc matrix4fc, Vector4d vector4d) {
		double double1 = Math.fma((double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m10(), this.y, Math.fma((double)matrix4fc.m20(), this.z, (double)matrix4fc.m30() * this.w)));
		double double2 = Math.fma((double)matrix4fc.m01(), this.x, Math.fma((double)matrix4fc.m11(), this.y, Math.fma((double)matrix4fc.m21(), this.z, (double)matrix4fc.m31() * this.w)));
		double double3 = Math.fma((double)matrix4fc.m02(), this.x, Math.fma((double)matrix4fc.m12(), this.y, Math.fma((double)matrix4fc.m22(), this.z, (double)matrix4fc.m32() * this.w)));
		double double4 = Math.fma((double)matrix4fc.m03(), this.x, Math.fma((double)matrix4fc.m13(), this.y, Math.fma((double)matrix4fc.m23(), this.z, (double)matrix4fc.m33() * this.w)));
		vector4d.x = double1;
		vector4d.y = double2;
		vector4d.z = double3;
		vector4d.w = double4;
		return vector4d;
	}

	public Vector4d mulProject(Matrix4dc matrix4dc, Vector4d vector4d) {
		double double1 = 1.0 / Math.fma(matrix4dc.m03(), this.x, Math.fma(matrix4dc.m13(), this.y, Math.fma(matrix4dc.m23(), this.z, matrix4dc.m33() * this.w)));
		double double2 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30() * this.w))) * double1;
		double double3 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31() * this.w))) * double1;
		double double4 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32() * this.w))) * double1;
		vector4d.x = double2;
		vector4d.y = double3;
		vector4d.z = double4;
		vector4d.w = 1.0;
		return vector4d;
	}

	public Vector4d mulProject(Matrix4dc matrix4dc) {
		double double1 = 1.0 / Math.fma(matrix4dc.m03(), this.x, Math.fma(matrix4dc.m13(), this.y, Math.fma(matrix4dc.m23(), this.z, matrix4dc.m33() * this.w)));
		double double2 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30() * this.w))) * double1;
		double double3 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31() * this.w))) * double1;
		double double4 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32() * this.w))) * double1;
		this.x = double2;
		this.y = double3;
		this.z = double4;
		this.w = 1.0;
		return this;
	}

	public Vector3d mulProject(Matrix4dc matrix4dc, Vector3d vector3d) {
		double double1 = 1.0 / Math.fma(matrix4dc.m03(), this.x, Math.fma(matrix4dc.m13(), this.y, Math.fma(matrix4dc.m23(), this.z, matrix4dc.m33() * this.w)));
		double double2 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30() * this.w))) * double1;
		double double3 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31() * this.w))) * double1;
		double double4 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32() * this.w))) * double1;
		vector3d.x = double2;
		vector3d.y = double3;
		vector3d.z = double4;
		return vector3d;
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
		double double2 = 1.0 / double1;
		this.x *= double2;
		this.y *= double2;
		this.z *= double2;
		this.w *= double2;
		return this;
	}

	public Vector4d div(double double1, Vector4d vector4d) {
		double double2 = 1.0 / double1;
		vector4d.x = this.x * double2;
		vector4d.y = this.y * double2;
		vector4d.z = this.z * double2;
		vector4d.w = this.w * double2;
		return vector4d;
	}

	public Vector4d rotate(Quaterniondc quaterniondc) {
		quaterniondc.transform((Vector4dc)this, (Vector4d)this);
		return this;
	}

	public Vector4d rotate(Quaterniondc quaterniondc, Vector4d vector4d) {
		quaterniondc.transform((Vector4dc)this, (Vector4d)vector4d);
		return vector4d;
	}

	public Vector4d rotateAxis(double double1, double double2, double double3, double double4) {
		if (double3 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double2)) {
			return this.rotateX(double2 * double1, this);
		} else if (double2 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double3)) {
			return this.rotateY(double3 * double1, this);
		} else {
			return double2 == 0.0 && double3 == 0.0 && Math.absEqualsOne(double4) ? this.rotateZ(double4 * double1, this) : this.rotateAxisInternal(double1, double2, double3, double4, this);
		}
	}

	public Vector4d rotateAxis(double double1, double double2, double double3, double double4, Vector4d vector4d) {
		if (double3 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double2)) {
			return this.rotateX(double2 * double1, vector4d);
		} else if (double2 == 0.0 && double4 == 0.0 && Math.absEqualsOne(double3)) {
			return this.rotateY(double3 * double1, vector4d);
		} else {
			return double2 == 0.0 && double3 == 0.0 && Math.absEqualsOne(double4) ? this.rotateZ(double4 * double1, vector4d) : this.rotateAxisInternal(double1, double2, double3, double4, vector4d);
		}
	}

	private Vector4d rotateAxisInternal(double double1, double double2, double double3, double double4, Vector4d vector4d) {
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
		vector4d.x = double21;
		vector4d.y = double22;
		vector4d.z = double23;
		return vector4d;
	}

	public Vector4d rotateX(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.y * double3 - this.z * double2;
		double double5 = this.y * double2 + this.z * double3;
		this.y = double4;
		this.z = double5;
		return this;
	}

	public Vector4d rotateX(double double1, Vector4d vector4d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.y * double3 - this.z * double2;
		double double5 = this.y * double2 + this.z * double3;
		vector4d.x = this.x;
		vector4d.y = double4;
		vector4d.z = double5;
		vector4d.w = this.w;
		return vector4d;
	}

	public Vector4d rotateY(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.x * double3 + this.z * double2;
		double double5 = -this.x * double2 + this.z * double3;
		this.x = double4;
		this.z = double5;
		return this;
	}

	public Vector4d rotateY(double double1, Vector4d vector4d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.x * double3 + this.z * double2;
		double double5 = -this.x * double2 + this.z * double3;
		vector4d.x = double4;
		vector4d.y = this.y;
		vector4d.z = double5;
		vector4d.w = this.w;
		return vector4d;
	}

	public Vector4d rotateZ(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.x * double3 - this.y * double2;
		double double5 = this.x * double2 + this.y * double3;
		this.x = double4;
		this.y = double5;
		return this;
	}

	public Vector4d rotateZ(double double1, Vector4d vector4d) {
		double double2 = Math.sin(double1);
		double double3 = Math.cosFromSin(double2, double1);
		double double4 = this.x * double3 - this.y * double2;
		double double5 = this.x * double2 + this.y * double3;
		vector4d.x = double4;
		vector4d.y = double5;
		vector4d.z = this.z;
		vector4d.w = this.w;
		return vector4d;
	}

	public double lengthSquared() {
		return Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
	}

	public static double lengthSquared(double double1, double double2, double double3, double double4) {
		return Math.fma(double1, double1, Math.fma(double2, double2, Math.fma(double3, double3, double4 * double4)));
	}

	public double length() {
		return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w))));
	}

	public static double length(double double1, double double2, double double3, double double4) {
		return Math.sqrt(Math.fma(double1, double1, Math.fma(double2, double2, Math.fma(double3, double3, double4 * double4))));
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

	public Vector4d normalize(double double1) {
		double double2 = 1.0 / this.length() * double1;
		this.x *= double2;
		this.y *= double2;
		this.z *= double2;
		this.w *= double2;
		return this;
	}

	public Vector4d normalize(double double1, Vector4d vector4d) {
		double double2 = 1.0 / this.length() * double1;
		vector4d.x = this.x * double2;
		vector4d.y = this.y * double2;
		vector4d.z = this.z * double2;
		vector4d.w = this.w * double2;
		return vector4d;
	}

	public Vector4d normalize3() {
		double double1 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		this.w *= double1;
		return this;
	}

	public Vector4d normalize3(Vector4d vector4d) {
		double double1 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
		vector4d.x = this.x * double1;
		vector4d.y = this.y * double1;
		vector4d.z = this.z * double1;
		vector4d.w = this.w * double1;
		return vector4d;
	}

	public double distance(Vector4dc vector4dc) {
		double double1 = this.x - vector4dc.x();
		double double2 = this.y - vector4dc.y();
		double double3 = this.z - vector4dc.z();
		double double4 = this.w - vector4dc.w();
		return Math.sqrt(Math.fma(double1, double1, Math.fma(double2, double2, Math.fma(double3, double3, double4 * double4))));
	}

	public double distance(double double1, double double2, double double3, double double4) {
		double double5 = this.x - double1;
		double double6 = this.y - double2;
		double double7 = this.z - double3;
		double double8 = this.w - double4;
		return Math.sqrt(Math.fma(double5, double5, Math.fma(double6, double6, Math.fma(double7, double7, double8 * double8))));
	}

	public double distanceSquared(Vector4dc vector4dc) {
		double double1 = this.x - vector4dc.x();
		double double2 = this.y - vector4dc.y();
		double double3 = this.z - vector4dc.z();
		double double4 = this.w - vector4dc.w();
		return Math.fma(double1, double1, Math.fma(double2, double2, Math.fma(double3, double3, double4 * double4)));
	}

	public double distanceSquared(double double1, double double2, double double3, double double4) {
		double double5 = this.x - double1;
		double double6 = this.y - double2;
		double double7 = this.z - double3;
		double double8 = this.w - double4;
		return Math.fma(double5, double5, Math.fma(double6, double6, Math.fma(double7, double7, double8 * double8)));
	}

	public static double distance(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		double double9 = double1 - double5;
		double double10 = double2 - double6;
		double double11 = double3 - double7;
		double double12 = double4 - double8;
		return Math.sqrt(Math.fma(double9, double9, Math.fma(double10, double10, Math.fma(double11, double11, double12 * double12))));
	}

	public static double distanceSquared(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		double double9 = double1 - double5;
		double double10 = double2 - double6;
		double double11 = double3 - double7;
		double double12 = double4 - double8;
		return Math.fma(double9, double9, Math.fma(double10, double10, Math.fma(double11, double11, double12 * double12)));
	}

	public double dot(Vector4dc vector4dc) {
		return Math.fma(this.x, vector4dc.x(), Math.fma(this.y, vector4dc.y(), Math.fma(this.z, vector4dc.z(), this.w * vector4dc.w())));
	}

	public double dot(double double1, double double2, double double3, double double4) {
		return Math.fma(this.x, double1, Math.fma(this.y, double2, Math.fma(this.z, double3, this.w * double4)));
	}

	public double angleCos(Vector4dc vector4dc) {
		double double1 = Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
		double double2 = Math.fma(vector4dc.x(), vector4dc.x(), Math.fma(vector4dc.y(), vector4dc.y(), Math.fma(vector4dc.z(), vector4dc.z(), vector4dc.w() * vector4dc.w())));
		double double3 = Math.fma(this.x, vector4dc.x(), Math.fma(this.y, vector4dc.y(), Math.fma(this.z, vector4dc.z(), this.w * vector4dc.w())));
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

	public Vector4d min(Vector4dc vector4dc) {
		this.x = this.x < vector4dc.x() ? this.x : vector4dc.x();
		this.y = this.y < vector4dc.y() ? this.y : vector4dc.y();
		this.z = this.z < vector4dc.z() ? this.z : vector4dc.z();
		this.w = this.w < vector4dc.w() ? this.w : vector4dc.w();
		return this;
	}

	public Vector4d min(Vector4dc vector4dc, Vector4d vector4d) {
		vector4d.x = this.x < vector4dc.x() ? this.x : vector4dc.x();
		vector4d.y = this.y < vector4dc.y() ? this.y : vector4dc.y();
		vector4d.z = this.z < vector4dc.z() ? this.z : vector4dc.z();
		vector4d.w = this.w < vector4dc.w() ? this.w : vector4dc.w();
		return vector4d;
	}

	public Vector4d max(Vector4dc vector4dc) {
		this.x = this.x > vector4dc.x() ? this.x : vector4dc.x();
		this.y = this.y > vector4dc.y() ? this.y : vector4dc.y();
		this.z = this.z > vector4dc.z() ? this.z : vector4dc.z();
		this.w = this.w > vector4dc.w() ? this.w : vector4dc.w();
		return this;
	}

	public Vector4d max(Vector4dc vector4dc, Vector4d vector4d) {
		vector4d.x = this.x > vector4dc.x() ? this.x : vector4dc.x();
		vector4d.y = this.y > vector4dc.y() ? this.y : vector4dc.y();
		vector4d.z = this.z > vector4dc.z() ? this.z : vector4dc.z();
		vector4d.w = this.w > vector4dc.w() ? this.w : vector4dc.w();
		return vector4d;
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format(this.x, numberFormat);
		return "(" + string + " " + Runtime.format(this.y, numberFormat) + " " + Runtime.format(this.z, numberFormat) + " " + Runtime.format(this.w, numberFormat) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeDouble(this.x);
		objectOutput.writeDouble(this.y);
		objectOutput.writeDouble(this.z);
		objectOutput.writeDouble(this.w);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readDouble();
		this.y = objectInput.readDouble();
		this.z = objectInput.readDouble();
		this.w = objectInput.readDouble();
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

	public boolean equals(Vector4dc vector4dc, double double1) {
		if (this == vector4dc) {
			return true;
		} else if (vector4dc == null) {
			return false;
		} else if (!(vector4dc instanceof Vector4dc)) {
			return false;
		} else if (!Runtime.equals(this.x, vector4dc.x(), double1)) {
			return false;
		} else if (!Runtime.equals(this.y, vector4dc.y(), double1)) {
			return false;
		} else if (!Runtime.equals(this.z, vector4dc.z(), double1)) {
			return false;
		} else {
			return Runtime.equals(this.w, vector4dc.w(), double1);
		}
	}

	public boolean equals(double double1, double double2, double double3, double double4) {
		if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(double1)) {
			return false;
		} else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(double2)) {
			return false;
		} else if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(double3)) {
			return false;
		} else {
			return Double.doubleToLongBits(this.w) == Double.doubleToLongBits(double4);
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
		this.x = Math.fma(vector4dc.x() - this.x, double1, this.x);
		this.y = Math.fma(vector4dc.y() - this.y, double1, this.y);
		this.z = Math.fma(vector4dc.z() - this.z, double1, this.z);
		this.w = Math.fma(vector4dc.w() - this.w, double1, this.w);
		return this;
	}

	public Vector4d lerp(Vector4dc vector4dc, double double1, Vector4d vector4d) {
		vector4d.x = Math.fma(vector4dc.x() - this.x, double1, this.x);
		vector4d.y = Math.fma(vector4dc.y() - this.y, double1, this.y);
		vector4d.z = Math.fma(vector4dc.z() - this.z, double1, this.z);
		vector4d.w = Math.fma(vector4dc.w() - this.w, double1, this.w);
		return vector4d;
	}

	public double get(int int1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			return this.x;
		
		case 1: 
			return this.y;
		
		case 2: 
			return this.z;
		
		case 3: 
			return this.w;
		
		default: 
			throw new IllegalArgumentException();
		
		}
	}

	public Vector4i get(int int1, Vector4i vector4i) {
		vector4i.x = Math.roundUsing(this.x(), int1);
		vector4i.y = Math.roundUsing(this.y(), int1);
		vector4i.z = Math.roundUsing(this.z(), int1);
		vector4i.w = Math.roundUsing(this.w(), int1);
		return vector4i;
	}

	public Vector4f get(Vector4f vector4f) {
		vector4f.x = (float)this.x();
		vector4f.y = (float)this.y();
		vector4f.z = (float)this.z();
		vector4f.w = (float)this.w();
		return vector4f;
	}

	public Vector4d get(Vector4d vector4d) {
		vector4d.x = this.x();
		vector4d.y = this.y();
		vector4d.z = this.z();
		vector4d.w = this.w();
		return vector4d;
	}

	public int maxComponent() {
		double double1 = Math.abs(this.x);
		double double2 = Math.abs(this.y);
		double double3 = Math.abs(this.z);
		double double4 = Math.abs(this.w);
		if (double1 >= double2 && double1 >= double3 && double1 >= double4) {
			return 0;
		} else if (double2 >= double3 && double2 >= double4) {
			return 1;
		} else {
			return double3 >= double4 ? 2 : 3;
		}
	}

	public int minComponent() {
		double double1 = Math.abs(this.x);
		double double2 = Math.abs(this.y);
		double double3 = Math.abs(this.z);
		double double4 = Math.abs(this.w);
		if (double1 < double2 && double1 < double3 && double1 < double4) {
			return 0;
		} else if (double2 < double3 && double2 < double4) {
			return 1;
		} else {
			return double3 < double4 ? 2 : 3;
		}
	}

	public Vector4d floor() {
		this.x = Math.floor(this.x);
		this.y = Math.floor(this.y);
		this.z = Math.floor(this.z);
		this.w = Math.floor(this.w);
		return this;
	}

	public Vector4d floor(Vector4d vector4d) {
		vector4d.x = Math.floor(this.x);
		vector4d.y = Math.floor(this.y);
		vector4d.z = Math.floor(this.z);
		vector4d.w = Math.floor(this.w);
		return vector4d;
	}

	public Vector4d ceil() {
		this.x = Math.ceil(this.x);
		this.y = Math.ceil(this.y);
		this.z = Math.ceil(this.z);
		this.w = Math.ceil(this.w);
		return this;
	}

	public Vector4d ceil(Vector4d vector4d) {
		vector4d.x = Math.ceil(this.x);
		vector4d.y = Math.ceil(this.y);
		vector4d.z = Math.ceil(this.z);
		vector4d.w = Math.ceil(this.w);
		return vector4d;
	}

	public Vector4d round() {
		this.x = (double)Math.round(this.x);
		this.y = (double)Math.round(this.y);
		this.z = (double)Math.round(this.z);
		this.w = (double)Math.round(this.w);
		return this;
	}

	public Vector4d round(Vector4d vector4d) {
		vector4d.x = (double)Math.round(this.x);
		vector4d.y = (double)Math.round(this.y);
		vector4d.z = (double)Math.round(this.z);
		vector4d.w = (double)Math.round(this.w);
		return vector4d;
	}

	public boolean isFinite() {
		return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z) && Math.isFinite(this.w);
	}

	public Vector4d absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
		this.w = Math.abs(this.w);
		return this;
	}

	public Vector4d absolute(Vector4d vector4d) {
		vector4d.x = Math.abs(this.x);
		vector4d.y = Math.abs(this.y);
		vector4d.z = Math.abs(this.z);
		vector4d.w = Math.abs(this.w);
		return vector4d;
	}
}
