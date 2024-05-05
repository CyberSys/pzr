package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;


public class Vector4f implements Externalizable,Vector4fc {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;
	public float z;
	public float w;

	public Vector4f() {
		this.w = 1.0F;
	}

	public Vector4f(Vector4fc vector4fc) {
		this.x = vector4fc.x();
		this.y = vector4fc.y();
		this.z = vector4fc.z();
		this.w = vector4fc.w();
	}

	public Vector4f(Vector4ic vector4ic) {
		this.x = (float)vector4ic.x();
		this.y = (float)vector4ic.y();
		this.z = (float)vector4ic.z();
		this.w = (float)vector4ic.w();
	}

	public Vector4f(Vector3fc vector3fc, float float1) {
		this.x = vector3fc.x();
		this.y = vector3fc.y();
		this.z = vector3fc.z();
		this.w = float1;
	}

	public Vector4f(Vector3ic vector3ic, float float1) {
		this.x = (float)vector3ic.x();
		this.y = (float)vector3ic.y();
		this.z = (float)vector3ic.z();
		this.w = float1;
	}

	public Vector4f(Vector2fc vector2fc, float float1, float float2) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
		this.z = float1;
		this.w = float2;
	}

	public Vector4f(Vector2ic vector2ic, float float1, float float2) {
		this.x = (float)vector2ic.x();
		this.y = (float)vector2ic.y();
		this.z = float1;
		this.w = float2;
	}

	public Vector4f(float float1) {
		this.x = float1;
		this.y = float1;
		this.z = float1;
		this.w = float1;
	}

	public Vector4f(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
	}

	public Vector4f(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
		this.z = floatArray[2];
		this.w = floatArray[3];
	}

	public Vector4f(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
	}

	public Vector4f(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector4f(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
	}

	public Vector4f(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, int1, floatBuffer);
	}

	public float x() {
		return this.x;
	}

	public float y() {
		return this.y;
	}

	public float z() {
		return this.z;
	}

	public float w() {
		return this.w;
	}

	public Vector4f set(Vector4fc vector4fc) {
		this.x = vector4fc.x();
		this.y = vector4fc.y();
		this.z = vector4fc.z();
		this.w = vector4fc.w();
		return this;
	}

	public Vector4f set(Vector4ic vector4ic) {
		this.x = (float)vector4ic.x();
		this.y = (float)vector4ic.y();
		this.z = (float)vector4ic.z();
		this.w = (float)vector4ic.w();
		return this;
	}

	public Vector4f set(Vector4dc vector4dc) {
		this.x = (float)vector4dc.x();
		this.y = (float)vector4dc.y();
		this.z = (float)vector4dc.z();
		this.w = (float)vector4dc.w();
		return this;
	}

	public Vector4f set(Vector3fc vector3fc, float float1) {
		this.x = vector3fc.x();
		this.y = vector3fc.y();
		this.z = vector3fc.z();
		this.w = float1;
		return this;
	}

	public Vector4f set(Vector3ic vector3ic, float float1) {
		this.x = (float)vector3ic.x();
		this.y = (float)vector3ic.y();
		this.z = (float)vector3ic.z();
		this.w = float1;
		return this;
	}

	public Vector4f set(Vector2fc vector2fc, float float1, float float2) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
		this.z = float1;
		this.w = float2;
		return this;
	}

	public Vector4f set(Vector2ic vector2ic, float float1, float float2) {
		this.x = (float)vector2ic.x();
		this.y = (float)vector2ic.y();
		this.z = float1;
		this.w = float2;
		return this;
	}

	public Vector4f set(float float1) {
		this.x = float1;
		this.y = float1;
		this.z = float1;
		this.w = float1;
		return this;
	}

	public Vector4f set(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
		return this;
	}

	public Vector4f set(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		return this;
	}

	public Vector4f set(double double1) {
		this.x = (float)double1;
		this.y = (float)double1;
		this.z = (float)double1;
		this.w = (float)double1;
		return this;
	}

	public Vector4f set(double double1, double double2, double double3, double double4) {
		this.x = (float)double1;
		this.y = (float)double2;
		this.z = (float)double3;
		this.w = (float)double4;
		return this;
	}

	public Vector4f set(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
		this.z = floatArray[2];
		this.w = floatArray[2];
		return this;
	}

	public Vector4f set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Vector4f set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector4f set(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
		return this;
	}

	public Vector4f set(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, int1, floatBuffer);
		return this;
	}

	public Vector4f setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
	}

	public Vector4f setComponent(int int1, float float1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			this.x = float1;
			break;
		
		case 1: 
			this.y = float1;
			break;
		
		case 2: 
			this.z = float1;
			break;
		
		case 3: 
			this.w = float1;
			break;
		
		default: 
			throw new IllegalArgumentException();
		
		}
		return this;
	}

	public FloatBuffer get(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, int1, floatBuffer);
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

	public Vector4fc getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public Vector4f sub(Vector4fc vector4fc) {
		this.x -= vector4fc.x();
		this.y -= vector4fc.y();
		this.z -= vector4fc.z();
		this.w -= vector4fc.w();
		return this;
	}

	public Vector4f sub(float float1, float float2, float float3, float float4) {
		this.x -= float1;
		this.y -= float2;
		this.z -= float3;
		this.w -= float4;
		return this;
	}

	public Vector4f sub(Vector4fc vector4fc, Vector4f vector4f) {
		vector4f.x = this.x - vector4fc.x();
		vector4f.y = this.y - vector4fc.y();
		vector4f.z = this.z - vector4fc.z();
		vector4f.w = this.w - vector4fc.w();
		return vector4f;
	}

	public Vector4f sub(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		vector4f.x = this.x - float1;
		vector4f.y = this.y - float2;
		vector4f.z = this.z - float3;
		vector4f.w = this.w - float4;
		return vector4f;
	}

	public Vector4f add(Vector4fc vector4fc) {
		this.x += vector4fc.x();
		this.y += vector4fc.y();
		this.z += vector4fc.z();
		this.w += vector4fc.w();
		return this;
	}

	public Vector4f add(Vector4fc vector4fc, Vector4f vector4f) {
		vector4f.x = this.x + vector4fc.x();
		vector4f.y = this.y + vector4fc.y();
		vector4f.z = this.z + vector4fc.z();
		vector4f.w = this.w + vector4fc.w();
		return vector4f;
	}

	public Vector4f add(float float1, float float2, float float3, float float4) {
		this.x += float1;
		this.y += float2;
		this.z += float3;
		this.w += float4;
		return this;
	}

	public Vector4f add(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		vector4f.x = this.x + float1;
		vector4f.y = this.y + float2;
		vector4f.z = this.z + float3;
		vector4f.w = this.w + float4;
		return vector4f;
	}

	public Vector4f fma(Vector4fc vector4fc, Vector4fc vector4fc2) {
		this.x = Math.fma(vector4fc.x(), vector4fc2.x(), this.x);
		this.y = Math.fma(vector4fc.y(), vector4fc2.y(), this.y);
		this.z = Math.fma(vector4fc.z(), vector4fc2.z(), this.z);
		this.w = Math.fma(vector4fc.w(), vector4fc2.w(), this.w);
		return this;
	}

	public Vector4f fma(float float1, Vector4fc vector4fc) {
		this.x = Math.fma(float1, vector4fc.x(), this.x);
		this.y = Math.fma(float1, vector4fc.y(), this.y);
		this.z = Math.fma(float1, vector4fc.z(), this.z);
		this.w = Math.fma(float1, vector4fc.w(), this.w);
		return this;
	}

	public Vector4f fma(Vector4fc vector4fc, Vector4fc vector4fc2, Vector4f vector4f) {
		vector4f.x = Math.fma(vector4fc.x(), vector4fc2.x(), this.x);
		vector4f.y = Math.fma(vector4fc.y(), vector4fc2.y(), this.y);
		vector4f.z = Math.fma(vector4fc.z(), vector4fc2.z(), this.z);
		vector4f.w = Math.fma(vector4fc.w(), vector4fc2.w(), this.w);
		return vector4f;
	}

	public Vector4f fma(float float1, Vector4fc vector4fc, Vector4f vector4f) {
		vector4f.x = Math.fma(float1, vector4fc.x(), this.x);
		vector4f.y = Math.fma(float1, vector4fc.y(), this.y);
		vector4f.z = Math.fma(float1, vector4fc.z(), this.z);
		vector4f.w = Math.fma(float1, vector4fc.w(), this.w);
		return vector4f;
	}

	public Vector4f mulAdd(Vector4fc vector4fc, Vector4fc vector4fc2) {
		this.x = Math.fma(this.x, vector4fc.x(), vector4fc2.x());
		this.y = Math.fma(this.y, vector4fc.y(), vector4fc2.y());
		this.z = Math.fma(this.z, vector4fc.z(), vector4fc2.z());
		return this;
	}

	public Vector4f mulAdd(float float1, Vector4fc vector4fc) {
		this.x = Math.fma(this.x, float1, vector4fc.x());
		this.y = Math.fma(this.y, float1, vector4fc.y());
		this.z = Math.fma(this.z, float1, vector4fc.z());
		return this;
	}

	public Vector4f mulAdd(Vector4fc vector4fc, Vector4fc vector4fc2, Vector4f vector4f) {
		vector4f.x = Math.fma(this.x, vector4fc.x(), vector4fc2.x());
		vector4f.y = Math.fma(this.y, vector4fc.y(), vector4fc2.y());
		vector4f.z = Math.fma(this.z, vector4fc.z(), vector4fc2.z());
		return vector4f;
	}

	public Vector4f mulAdd(float float1, Vector4fc vector4fc, Vector4f vector4f) {
		vector4f.x = Math.fma(this.x, float1, vector4fc.x());
		vector4f.y = Math.fma(this.y, float1, vector4fc.y());
		vector4f.z = Math.fma(this.z, float1, vector4fc.z());
		return vector4f;
	}

	public Vector4f mul(Vector4fc vector4fc) {
		this.x *= vector4fc.x();
		this.y *= vector4fc.y();
		this.z *= vector4fc.z();
		this.w *= vector4fc.w();
		return this;
	}

	public Vector4f mul(Vector4fc vector4fc, Vector4f vector4f) {
		vector4f.x = this.x * vector4fc.x();
		vector4f.y = this.y * vector4fc.y();
		vector4f.z = this.z * vector4fc.z();
		vector4f.w = this.w * vector4fc.w();
		return vector4f;
	}

	public Vector4f div(Vector4fc vector4fc) {
		this.x /= vector4fc.x();
		this.y /= vector4fc.y();
		this.z /= vector4fc.z();
		this.w /= vector4fc.w();
		return this;
	}

	public Vector4f div(Vector4fc vector4fc, Vector4f vector4f) {
		vector4f.x = this.x / vector4fc.x();
		vector4f.y = this.y / vector4fc.y();
		vector4f.z = this.z / vector4fc.z();
		vector4f.w = this.w / vector4fc.w();
		return vector4f;
	}

	public Vector4f mul(Matrix4fc matrix4fc) {
		return (matrix4fc.properties() & 2) != 0 ? this.mulAffine(matrix4fc, this) : this.mulGeneric(matrix4fc, this);
	}

	public Vector4f mul(Matrix4fc matrix4fc, Vector4f vector4f) {
		return (matrix4fc.properties() & 2) != 0 ? this.mulAffine(matrix4fc, vector4f) : this.mulGeneric(matrix4fc, vector4f);
	}

	public Vector4f mulTranspose(Matrix4fc matrix4fc) {
		return (matrix4fc.properties() & 2) != 0 ? this.mulAffineTranspose(matrix4fc, this) : this.mulGenericTranspose(matrix4fc, this);
	}

	public Vector4f mulTranspose(Matrix4fc matrix4fc, Vector4f vector4f) {
		return (matrix4fc.properties() & 2) != 0 ? this.mulAffineTranspose(matrix4fc, vector4f) : this.mulGenericTranspose(matrix4fc, vector4f);
	}

	public Vector4f mulAffine(Matrix4fc matrix4fc, Vector4f vector4f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		vector4f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, Math.fma(matrix4fc.m20(), float3, matrix4fc.m30() * float4)));
		vector4f.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m21(), float3, matrix4fc.m31() * float4)));
		vector4f.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m32() * float4)));
		vector4f.w = float4;
		return vector4f;
	}

	private Vector4f mulGeneric(Matrix4fc matrix4fc, Vector4f vector4f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		vector4f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, Math.fma(matrix4fc.m20(), float3, matrix4fc.m30() * float4)));
		vector4f.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m21(), float3, matrix4fc.m31() * float4)));
		vector4f.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m32() * float4)));
		vector4f.w = Math.fma(matrix4fc.m03(), float1, Math.fma(matrix4fc.m13(), float2, Math.fma(matrix4fc.m23(), float3, matrix4fc.m33() * float4)));
		return vector4f;
	}

	public Vector4f mulAffineTranspose(Matrix4fc matrix4fc, Vector4f vector4f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		vector4f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m01(), float2, matrix4fc.m02() * float3));
		vector4f.y = Math.fma(matrix4fc.m10(), float1, Math.fma(matrix4fc.m11(), float2, matrix4fc.m12() * float3));
		vector4f.z = Math.fma(matrix4fc.m20(), float1, Math.fma(matrix4fc.m21(), float2, matrix4fc.m22() * float3));
		vector4f.w = Math.fma(matrix4fc.m30(), float1, Math.fma(matrix4fc.m31(), float2, matrix4fc.m32() * float3 + float4));
		return vector4f;
	}

	private Vector4f mulGenericTranspose(Matrix4fc matrix4fc, Vector4f vector4f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		vector4f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m01(), float2, Math.fma(matrix4fc.m02(), float3, matrix4fc.m03() * float4)));
		vector4f.y = Math.fma(matrix4fc.m10(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m12(), float3, matrix4fc.m13() * float4)));
		vector4f.z = Math.fma(matrix4fc.m20(), float1, Math.fma(matrix4fc.m21(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m23() * float4)));
		vector4f.w = Math.fma(matrix4fc.m30(), float1, Math.fma(matrix4fc.m31(), float2, Math.fma(matrix4fc.m32(), float3, matrix4fc.m33() * float4)));
		return vector4f;
	}

	public Vector4f mul(Matrix4x3fc matrix4x3fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		this.x = Math.fma(matrix4x3fc.m00(), float1, Math.fma(matrix4x3fc.m10(), float2, Math.fma(matrix4x3fc.m20(), float3, matrix4x3fc.m30() * float4)));
		this.y = Math.fma(matrix4x3fc.m01(), float1, Math.fma(matrix4x3fc.m11(), float2, Math.fma(matrix4x3fc.m21(), float3, matrix4x3fc.m31() * float4)));
		this.z = Math.fma(matrix4x3fc.m02(), float1, Math.fma(matrix4x3fc.m12(), float2, Math.fma(matrix4x3fc.m22(), float3, matrix4x3fc.m32() * float4)));
		this.w = float4;
		return this;
	}

	public Vector4f mul(Matrix4x3fc matrix4x3fc, Vector4f vector4f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		vector4f.x = Math.fma(matrix4x3fc.m00(), float1, Math.fma(matrix4x3fc.m10(), float2, Math.fma(matrix4x3fc.m20(), float3, matrix4x3fc.m30() * float4)));
		vector4f.y = Math.fma(matrix4x3fc.m01(), float1, Math.fma(matrix4x3fc.m11(), float2, Math.fma(matrix4x3fc.m21(), float3, matrix4x3fc.m31() * float4)));
		vector4f.z = Math.fma(matrix4x3fc.m02(), float1, Math.fma(matrix4x3fc.m12(), float2, Math.fma(matrix4x3fc.m22(), float3, matrix4x3fc.m32() * float4)));
		vector4f.w = float4;
		return vector4f;
	}

	public Vector4f mulProject(Matrix4fc matrix4fc, Vector4f vector4f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		float float5 = 1.0F / Math.fma(matrix4fc.m03(), float1, Math.fma(matrix4fc.m13(), float2, Math.fma(matrix4fc.m23(), float3, matrix4fc.m33() * float4)));
		vector4f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, Math.fma(matrix4fc.m20(), float3, matrix4fc.m30() * float4))) * float5;
		vector4f.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m21(), float3, matrix4fc.m31() * float4))) * float5;
		vector4f.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m32() * float4))) * float5;
		vector4f.w = 1.0F;
		return vector4f;
	}

	public Vector4f mulProject(Matrix4fc matrix4fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		float float5 = 1.0F / Math.fma(matrix4fc.m03(), float1, Math.fma(matrix4fc.m13(), float2, Math.fma(matrix4fc.m23(), float3, matrix4fc.m33() * float4)));
		this.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, Math.fma(matrix4fc.m20(), float3, matrix4fc.m30() * float4))) * float5;
		this.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m21(), float3, matrix4fc.m31() * float4))) * float5;
		this.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m32() * float4))) * float5;
		this.w = 1.0F;
		return this;
	}

	public Vector3f mulProject(Matrix4fc matrix4fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		float float5 = 1.0F / Math.fma(matrix4fc.m03(), float1, Math.fma(matrix4fc.m13(), float2, Math.fma(matrix4fc.m23(), float3, matrix4fc.m33() * float4)));
		vector3f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, Math.fma(matrix4fc.m20(), float3, matrix4fc.m30() * float4))) * float5;
		vector3f.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m21(), float3, matrix4fc.m31() * float4))) * float5;
		vector3f.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m32() * float4))) * float5;
		return vector3f;
	}

	public Vector4f mul(float float1) {
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		this.w *= float1;
		return this;
	}

	public Vector4f mul(float float1, Vector4f vector4f) {
		vector4f.x = this.x * float1;
		vector4f.y = this.y * float1;
		vector4f.z = this.z * float1;
		vector4f.w = this.w * float1;
		return vector4f;
	}

	public Vector4f mul(float float1, float float2, float float3, float float4) {
		this.x *= float1;
		this.y *= float2;
		this.z *= float3;
		this.w *= float4;
		return this;
	}

	public Vector4f mul(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		vector4f.x = this.x * float1;
		vector4f.y = this.y * float2;
		vector4f.z = this.z * float3;
		vector4f.w = this.w * float4;
		return vector4f;
	}

	public Vector4f div(float float1) {
		float float2 = 1.0F / float1;
		this.x *= float2;
		this.y *= float2;
		this.z *= float2;
		this.w *= float2;
		return this;
	}

	public Vector4f div(float float1, Vector4f vector4f) {
		float float2 = 1.0F / float1;
		vector4f.x = this.x * float2;
		vector4f.y = this.y * float2;
		vector4f.z = this.z * float2;
		vector4f.w = this.w * float2;
		return vector4f;
	}

	public Vector4f div(float float1, float float2, float float3, float float4) {
		this.x /= float1;
		this.y /= float2;
		this.z /= float3;
		this.w /= float4;
		return this;
	}

	public Vector4f div(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		vector4f.x = this.x / float1;
		vector4f.y = this.y / float2;
		vector4f.z = this.z / float3;
		vector4f.w = this.w / float4;
		return vector4f;
	}

	public Vector4f rotate(Quaternionfc quaternionfc) {
		return quaternionfc.transform((Vector4fc)this, (Vector4f)this);
	}

	public Vector4f rotate(Quaternionfc quaternionfc, Vector4f vector4f) {
		return quaternionfc.transform((Vector4fc)this, (Vector4f)vector4f);
	}

	public Vector4f rotateAbout(float float1, float float2, float float3, float float4) {
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return this.rotateX(float2 * float1, this);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return this.rotateY(float3 * float1, this);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? this.rotateZ(float4 * float1, this) : this.rotateAxisInternal(float1, float2, float3, float4, this);
		}
	}

	public Vector4f rotateAxis(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return this.rotateX(float2 * float1, vector4f);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return this.rotateY(float3 * float1, vector4f);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? this.rotateZ(float4 * float1, vector4f) : this.rotateAxisInternal(float1, float2, float3, float4, vector4f);
		}
	}

	private Vector4f rotateAxisInternal(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		float float5 = float1 * 0.5F;
		float float6 = Math.sin(float5);
		float float7 = float2 * float6;
		float float8 = float3 * float6;
		float float9 = float4 * float6;
		float float10 = Math.cosFromSin(float6, float5);
		float float11 = float10 * float10;
		float float12 = float7 * float7;
		float float13 = float8 * float8;
		float float14 = float9 * float9;
		float float15 = float9 * float10;
		float float16 = float7 * float8;
		float float17 = float7 * float9;
		float float18 = float8 * float10;
		float float19 = float8 * float9;
		float float20 = float7 * float10;
		float float21 = this.x;
		float float22 = this.y;
		float float23 = this.z;
		vector4f.x = (float11 + float12 - float14 - float13) * float21 + (-float15 + float16 - float15 + float16) * float22 + (float18 + float17 + float17 + float18) * float23;
		vector4f.y = (float16 + float15 + float15 + float16) * float21 + (float13 - float14 + float11 - float12) * float22 + (float19 + float19 - float20 - float20) * float23;
		vector4f.z = (float17 - float18 + float17 - float18) * float21 + (float19 + float19 + float20 + float20) * float22 + (float14 - float13 - float12 + float11) * float23;
		return vector4f;
	}

	public Vector4f rotateX(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.y * float3 - this.z * float2;
		float float5 = this.y * float2 + this.z * float3;
		this.y = float4;
		this.z = float5;
		return this;
	}

	public Vector4f rotateX(float float1, Vector4f vector4f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.y * float3 - this.z * float2;
		float float5 = this.y * float2 + this.z * float3;
		vector4f.x = this.x;
		vector4f.y = float4;
		vector4f.z = float5;
		vector4f.w = this.w;
		return vector4f;
	}

	public Vector4f rotateY(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.x * float3 + this.z * float2;
		float float5 = -this.x * float2 + this.z * float3;
		this.x = float4;
		this.z = float5;
		return this;
	}

	public Vector4f rotateY(float float1, Vector4f vector4f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.x * float3 + this.z * float2;
		float float5 = -this.x * float2 + this.z * float3;
		vector4f.x = float4;
		vector4f.y = this.y;
		vector4f.z = float5;
		vector4f.w = this.w;
		return vector4f;
	}

	public Vector4f rotateZ(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.x * float3 - this.y * float2;
		float float5 = this.x * float2 + this.y * float3;
		this.x = float4;
		this.y = float5;
		return this;
	}

	public Vector4f rotateZ(float float1, Vector4f vector4f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.x * float3 - this.y * float2;
		float float5 = this.x * float2 + this.y * float3;
		vector4f.x = float4;
		vector4f.y = float5;
		vector4f.z = this.z;
		vector4f.w = this.w;
		return vector4f;
	}

	public float lengthSquared() {
		return Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
	}

	public static float lengthSquared(float float1, float float2, float float3, float float4) {
		return Math.fma(float1, float1, Math.fma(float2, float2, Math.fma(float3, float3, float4 * float4)));
	}

	public static float lengthSquared(int int1, int int2, int int3, int int4) {
		return Math.fma((float)int1, (float)int1, Math.fma((float)int2, (float)int2, Math.fma((float)int3, (float)int3, (float)(int4 * int4))));
	}

	public float length() {
		return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w))));
	}

	public static float length(float float1, float float2, float float3, float float4) {
		return Math.sqrt(Math.fma(float1, float1, Math.fma(float2, float2, Math.fma(float3, float3, float4 * float4))));
	}

	public Vector4f normalize() {
		float float1 = 1.0F / this.length();
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		this.w *= float1;
		return this;
	}

	public Vector4f normalize(Vector4f vector4f) {
		float float1 = 1.0F / this.length();
		vector4f.x = this.x * float1;
		vector4f.y = this.y * float1;
		vector4f.z = this.z * float1;
		vector4f.w = this.w * float1;
		return vector4f;
	}

	public Vector4f normalize(float float1) {
		float float2 = 1.0F / this.length() * float1;
		this.x *= float2;
		this.y *= float2;
		this.z *= float2;
		this.w *= float2;
		return this;
	}

	public Vector4f normalize(float float1, Vector4f vector4f) {
		float float2 = 1.0F / this.length() * float1;
		vector4f.x = this.x * float2;
		vector4f.y = this.y * float2;
		vector4f.z = this.z * float2;
		vector4f.w = this.w * float2;
		return vector4f;
	}

	public Vector4f normalize3() {
		float float1 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		this.w *= float1;
		return this;
	}

	public Vector4f normalize3(Vector4f vector4f) {
		float float1 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
		vector4f.x = this.x * float1;
		vector4f.y = this.y * float1;
		vector4f.z = this.z * float1;
		vector4f.w = this.w * float1;
		return vector4f;
	}

	public float distance(Vector4fc vector4fc) {
		float float1 = this.x - vector4fc.x();
		float float2 = this.y - vector4fc.y();
		float float3 = this.z - vector4fc.z();
		float float4 = this.w - vector4fc.w();
		return Math.sqrt(Math.fma(float1, float1, Math.fma(float2, float2, Math.fma(float3, float3, float4 * float4))));
	}

	public float distance(float float1, float float2, float float3, float float4) {
		float float5 = this.x - float1;
		float float6 = this.y - float2;
		float float7 = this.z - float3;
		float float8 = this.w - float4;
		return Math.sqrt(Math.fma(float5, float5, Math.fma(float6, float6, Math.fma(float7, float7, float8 * float8))));
	}

	public float distanceSquared(Vector4fc vector4fc) {
		float float1 = this.x - vector4fc.x();
		float float2 = this.y - vector4fc.y();
		float float3 = this.z - vector4fc.z();
		float float4 = this.w - vector4fc.w();
		return Math.fma(float1, float1, Math.fma(float2, float2, Math.fma(float3, float3, float4 * float4)));
	}

	public float distanceSquared(float float1, float float2, float float3, float float4) {
		float float5 = this.x - float1;
		float float6 = this.y - float2;
		float float7 = this.z - float3;
		float float8 = this.w - float4;
		return Math.fma(float5, float5, Math.fma(float6, float6, Math.fma(float7, float7, float8 * float8)));
	}

	public static float distance(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = float1 - float5;
		float float10 = float2 - float6;
		float float11 = float3 - float7;
		float float12 = float4 - float8;
		return Math.sqrt(Math.fma(float9, float9, Math.fma(float10, float10, Math.fma(float11, float11, float12 * float12))));
	}

	public static float distanceSquared(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = float1 - float5;
		float float10 = float2 - float6;
		float float11 = float3 - float7;
		float float12 = float4 - float8;
		return Math.fma(float9, float9, Math.fma(float10, float10, Math.fma(float11, float11, float12 * float12)));
	}

	public float dot(Vector4fc vector4fc) {
		return Math.fma(this.x, vector4fc.x(), Math.fma(this.y, vector4fc.y(), Math.fma(this.z, vector4fc.z(), this.w * vector4fc.w())));
	}

	public float dot(float float1, float float2, float float3, float float4) {
		return Math.fma(this.x, float1, Math.fma(this.y, float2, Math.fma(this.z, float3, this.w * float4)));
	}

	public float angleCos(Vector4fc vector4fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		float float5 = Math.fma(float1, float1, Math.fma(float2, float2, Math.fma(float3, float3, float4 * float4)));
		float float6 = Math.fma(vector4fc.x(), vector4fc.x(), Math.fma(vector4fc.y(), vector4fc.y(), Math.fma(vector4fc.z(), vector4fc.z(), vector4fc.w() * vector4fc.w())));
		float float7 = Math.fma(float1, vector4fc.x(), Math.fma(float2, vector4fc.y(), Math.fma(float3, vector4fc.z(), float4 * vector4fc.w())));
		return float7 / Math.sqrt(float5 * float6);
	}

	public float angle(Vector4fc vector4fc) {
		float float1 = this.angleCos(vector4fc);
		float1 = float1 < 1.0F ? float1 : 1.0F;
		float1 = float1 > -1.0F ? float1 : -1.0F;
		return Math.acos(float1);
	}

	public Vector4f zero() {
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
		this.w = 0.0F;
		return this;
	}

	public Vector4f negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
		return this;
	}

	public Vector4f negate(Vector4f vector4f) {
		vector4f.x = -this.x;
		vector4f.y = -this.y;
		vector4f.z = -this.z;
		vector4f.w = -this.w;
		return vector4f;
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format((double)this.x, numberFormat);
		return "(" + string + " " + Runtime.format((double)this.y, numberFormat) + " " + Runtime.format((double)this.z, numberFormat) + " " + Runtime.format((double)this.w, numberFormat) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeFloat(this.x);
		objectOutput.writeFloat(this.y);
		objectOutput.writeFloat(this.z);
		objectOutput.writeFloat(this.w);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.set(objectInput.readFloat(), objectInput.readFloat(), objectInput.readFloat(), objectInput.readFloat());
	}

	public Vector4f min(Vector4fc vector4fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		this.x = float1 < vector4fc.x() ? float1 : vector4fc.x();
		this.y = float2 < vector4fc.y() ? float2 : vector4fc.y();
		this.z = float3 < vector4fc.z() ? float3 : vector4fc.z();
		this.w = float4 < vector4fc.w() ? float4 : vector4fc.w();
		return this;
	}

	public Vector4f min(Vector4fc vector4fc, Vector4f vector4f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		vector4f.x = float1 < vector4fc.x() ? float1 : vector4fc.x();
		vector4f.y = float2 < vector4fc.y() ? float2 : vector4fc.y();
		vector4f.z = float3 < vector4fc.z() ? float3 : vector4fc.z();
		vector4f.w = float4 < vector4fc.w() ? float4 : vector4fc.w();
		return vector4f;
	}

	public Vector4f max(Vector4fc vector4fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		this.x = float1 > vector4fc.x() ? float1 : vector4fc.x();
		this.y = float2 > vector4fc.y() ? float2 : vector4fc.y();
		this.z = float3 > vector4fc.z() ? float3 : vector4fc.z();
		this.w = float4 > vector4fc.w() ? float4 : vector4fc.w();
		return this;
	}

	public Vector4f max(Vector4fc vector4fc, Vector4f vector4f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = this.w;
		vector4f.x = float1 > vector4fc.x() ? float1 : vector4fc.x();
		vector4f.y = float2 > vector4fc.y() ? float2 : vector4fc.y();
		vector4f.z = float3 > vector4fc.z() ? float3 : vector4fc.z();
		vector4f.w = float4 > vector4fc.w() ? float4 : vector4fc.w();
		return vector4f;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + Float.floatToIntBits(this.w);
		int1 = 31 * int1 + Float.floatToIntBits(this.x);
		int1 = 31 * int1 + Float.floatToIntBits(this.y);
		int1 = 31 * int1 + Float.floatToIntBits(this.z);
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
			Vector4f vector4f = (Vector4f)object;
			if (Float.floatToIntBits(this.w) != Float.floatToIntBits(vector4f.w)) {
				return false;
			} else if (Float.floatToIntBits(this.x) != Float.floatToIntBits(vector4f.x)) {
				return false;
			} else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(vector4f.y)) {
				return false;
			} else {
				return Float.floatToIntBits(this.z) == Float.floatToIntBits(vector4f.z);
			}
		}
	}

	public boolean equals(Vector4fc vector4fc, float float1) {
		if (this == vector4fc) {
			return true;
		} else if (vector4fc == null) {
			return false;
		} else if (!(vector4fc instanceof Vector4fc)) {
			return false;
		} else if (!Runtime.equals(this.x, vector4fc.x(), float1)) {
			return false;
		} else if (!Runtime.equals(this.y, vector4fc.y(), float1)) {
			return false;
		} else if (!Runtime.equals(this.z, vector4fc.z(), float1)) {
			return false;
		} else {
			return Runtime.equals(this.w, vector4fc.w(), float1);
		}
	}

	public boolean equals(float float1, float float2, float float3, float float4) {
		if (Float.floatToIntBits(this.x) != Float.floatToIntBits(float1)) {
			return false;
		} else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(float2)) {
			return false;
		} else if (Float.floatToIntBits(this.z) != Float.floatToIntBits(float3)) {
			return false;
		} else {
			return Float.floatToIntBits(this.w) == Float.floatToIntBits(float4);
		}
	}

	public Vector4f smoothStep(Vector4fc vector4fc, float float1, Vector4f vector4f) {
		float float2 = float1 * float1;
		float float3 = float2 * float1;
		float float4 = this.x;
		float float5 = this.y;
		float float6 = this.z;
		float float7 = this.w;
		vector4f.x = (float4 + float4 - vector4fc.x() - vector4fc.x()) * float3 + (3.0F * vector4fc.x() - 3.0F * float4) * float2 + float4 * float1 + float4;
		vector4f.y = (float5 + float5 - vector4fc.y() - vector4fc.y()) * float3 + (3.0F * vector4fc.y() - 3.0F * float5) * float2 + float5 * float1 + float5;
		vector4f.z = (float6 + float6 - vector4fc.z() - vector4fc.z()) * float3 + (3.0F * vector4fc.z() - 3.0F * float6) * float2 + float6 * float1 + float6;
		vector4f.w = (float7 + float7 - vector4fc.w() - vector4fc.w()) * float3 + (3.0F * vector4fc.w() - 3.0F * float7) * float2 + float7 * float1 + float7;
		return vector4f;
	}

	public Vector4f hermite(Vector4fc vector4fc, Vector4fc vector4fc2, Vector4fc vector4fc3, float float1, Vector4f vector4f) {
		float float2 = float1 * float1;
		float float3 = float2 * float1;
		float float4 = this.x;
		float float5 = this.y;
		float float6 = this.z;
		float float7 = this.w;
		vector4f.x = (float4 + float4 - vector4fc2.x() - vector4fc2.x() + vector4fc3.x() + vector4fc.x()) * float3 + (3.0F * vector4fc2.x() - 3.0F * float4 - vector4fc.x() - vector4fc.x() - vector4fc3.x()) * float2 + float4 * float1 + float4;
		vector4f.y = (float5 + float5 - vector4fc2.y() - vector4fc2.y() + vector4fc3.y() + vector4fc.y()) * float3 + (3.0F * vector4fc2.y() - 3.0F * float5 - vector4fc.y() - vector4fc.y() - vector4fc3.y()) * float2 + float5 * float1 + float5;
		vector4f.z = (float6 + float6 - vector4fc2.z() - vector4fc2.z() + vector4fc3.z() + vector4fc.z()) * float3 + (3.0F * vector4fc2.z() - 3.0F * float6 - vector4fc.z() - vector4fc.z() - vector4fc3.z()) * float2 + float6 * float1 + float6;
		vector4f.w = (float7 + float7 - vector4fc2.w() - vector4fc2.w() + vector4fc3.w() + vector4fc.w()) * float3 + (3.0F * vector4fc2.w() - 3.0F * float7 - vector4fc.w() - vector4fc.w() - vector4fc3.w()) * float2 + float7 * float1 + float7;
		return vector4f;
	}

	public Vector4f lerp(Vector4fc vector4fc, float float1) {
		this.x = Math.fma(vector4fc.x() - this.x, float1, this.x);
		this.y = Math.fma(vector4fc.y() - this.y, float1, this.y);
		this.z = Math.fma(vector4fc.z() - this.z, float1, this.z);
		this.w = Math.fma(vector4fc.w() - this.w, float1, this.w);
		return this;
	}

	public Vector4f lerp(Vector4fc vector4fc, float float1, Vector4f vector4f) {
		vector4f.x = Math.fma(vector4fc.x() - this.x, float1, this.x);
		vector4f.y = Math.fma(vector4fc.y() - this.y, float1, this.y);
		vector4f.z = Math.fma(vector4fc.z() - this.z, float1, this.z);
		vector4f.w = Math.fma(vector4fc.w() - this.w, float1, this.w);
		return vector4f;
	}

	public float get(int int1) throws IllegalArgumentException {
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
		vector4f.x = this.x();
		vector4f.y = this.y();
		vector4f.z = this.z();
		vector4f.w = this.w();
		return vector4f;
	}

	public Vector4d get(Vector4d vector4d) {
		vector4d.x = (double)this.x();
		vector4d.y = (double)this.y();
		vector4d.z = (double)this.z();
		vector4d.w = (double)this.w();
		return vector4d;
	}

	public int maxComponent() {
		float float1 = Math.abs(this.x);
		float float2 = Math.abs(this.y);
		float float3 = Math.abs(this.z);
		float float4 = Math.abs(this.w);
		if (float1 >= float2 && float1 >= float3 && float1 >= float4) {
			return 0;
		} else if (float2 >= float3 && float2 >= float4) {
			return 1;
		} else {
			return float3 >= float4 ? 2 : 3;
		}
	}

	public int minComponent() {
		float float1 = Math.abs(this.x);
		float float2 = Math.abs(this.y);
		float float3 = Math.abs(this.z);
		float float4 = Math.abs(this.w);
		if (float1 < float2 && float1 < float3 && float1 < float4) {
			return 0;
		} else if (float2 < float3 && float2 < float4) {
			return 1;
		} else {
			return float3 < float4 ? 2 : 3;
		}
	}

	public Vector4f floor() {
		this.x = Math.floor(this.x);
		this.y = Math.floor(this.y);
		this.z = Math.floor(this.z);
		this.w = Math.floor(this.w);
		return this;
	}

	public Vector4f floor(Vector4f vector4f) {
		vector4f.x = Math.floor(this.x);
		vector4f.y = Math.floor(this.y);
		vector4f.z = Math.floor(this.z);
		vector4f.w = Math.floor(this.w);
		return vector4f;
	}

	public Vector4f ceil() {
		this.x = Math.ceil(this.x);
		this.y = Math.ceil(this.y);
		this.z = Math.ceil(this.z);
		this.w = Math.ceil(this.w);
		return this;
	}

	public Vector4f ceil(Vector4f vector4f) {
		vector4f.x = Math.ceil(this.x);
		vector4f.y = Math.ceil(this.y);
		vector4f.z = Math.ceil(this.z);
		vector4f.w = Math.ceil(this.w);
		return vector4f;
	}

	public Vector4f round() {
		this.x = (float)Math.round(this.x);
		this.y = (float)Math.round(this.y);
		this.z = (float)Math.round(this.z);
		this.w = (float)Math.round(this.w);
		return this;
	}

	public Vector4f round(Vector4f vector4f) {
		vector4f.x = (float)Math.round(this.x);
		vector4f.y = (float)Math.round(this.y);
		vector4f.z = (float)Math.round(this.z);
		vector4f.w = (float)Math.round(this.w);
		return vector4f;
	}

	public boolean isFinite() {
		return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z) && Math.isFinite(this.w);
	}

	public Vector4f absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
		this.w = Math.abs(this.w);
		return this;
	}

	public Vector4f absolute(Vector4f vector4f) {
		vector4f.x = Math.abs(this.x);
		vector4f.y = Math.abs(this.y);
		vector4f.z = Math.abs(this.z);
		vector4f.w = Math.abs(this.w);
		return vector4f;
	}
}
