package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;


public class Vector3f implements Externalizable,Vector3fc {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;
	public float z;

	public Vector3f() {
	}

	public Vector3f(float float1) {
		this.x = float1;
		this.y = float1;
		this.z = float1;
	}

	public Vector3f(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
	}

	public Vector3f(Vector3fc vector3fc) {
		this.x = vector3fc.x();
		this.y = vector3fc.y();
		this.z = vector3fc.z();
	}

	public Vector3f(Vector3ic vector3ic) {
		this.x = (float)vector3ic.x();
		this.y = (float)vector3ic.y();
		this.z = (float)vector3ic.z();
	}

	public Vector3f(Vector2fc vector2fc, float float1) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
		this.z = float1;
	}

	public Vector3f(Vector2ic vector2ic, float float1) {
		this.x = (float)vector2ic.x();
		this.y = (float)vector2ic.y();
		this.z = float1;
	}

	public Vector3f(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
		this.z = floatArray[2];
	}

	public Vector3f(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
	}

	public Vector3f(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector3f(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
	}

	public Vector3f(int int1, FloatBuffer floatBuffer) {
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

	public Vector3f set(Vector3fc vector3fc) {
		this.x = vector3fc.x();
		this.y = vector3fc.y();
		this.z = vector3fc.z();
		return this;
	}

	public Vector3f set(Vector3dc vector3dc) {
		this.x = (float)vector3dc.x();
		this.y = (float)vector3dc.y();
		this.z = (float)vector3dc.z();
		return this;
	}

	public Vector3f set(Vector3ic vector3ic) {
		this.x = (float)vector3ic.x();
		this.y = (float)vector3ic.y();
		this.z = (float)vector3ic.z();
		return this;
	}

	public Vector3f set(Vector2fc vector2fc, float float1) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
		this.z = float1;
		return this;
	}

	public Vector3f set(Vector2dc vector2dc, float float1) {
		this.x = (float)vector2dc.x();
		this.y = (float)vector2dc.y();
		this.z = float1;
		return this;
	}

	public Vector3f set(Vector2ic vector2ic, float float1) {
		this.x = (float)vector2ic.x();
		this.y = (float)vector2ic.y();
		this.z = float1;
		return this;
	}

	public Vector3f set(float float1) {
		this.x = float1;
		this.y = float1;
		this.z = float1;
		return this;
	}

	public Vector3f set(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		return this;
	}

	public Vector3f set(double double1) {
		this.x = (float)double1;
		this.y = (float)double1;
		this.z = (float)double1;
		return this;
	}

	public Vector3f set(double double1, double double2, double double3) {
		this.x = (float)double1;
		this.y = (float)double2;
		this.z = (float)double3;
		return this;
	}

	public Vector3f set(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
		this.z = floatArray[2];
		return this;
	}

	public Vector3f set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Vector3f set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector3f set(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
		return this;
	}

	public Vector3f set(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, int1, floatBuffer);
		return this;
	}

	public Vector3f setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
	}

	public Vector3f setComponent(int int1, float float1) throws IllegalArgumentException {
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

	public Vector3fc getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public Vector3f sub(Vector3fc vector3fc) {
		this.x -= vector3fc.x();
		this.y -= vector3fc.y();
		this.z -= vector3fc.z();
		return this;
	}

	public Vector3f sub(Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.x = this.x - vector3fc.x();
		vector3f.y = this.y - vector3fc.y();
		vector3f.z = this.z - vector3fc.z();
		return vector3f;
	}

	public Vector3f sub(float float1, float float2, float float3) {
		this.x -= float1;
		this.y -= float2;
		this.z -= float3;
		return this;
	}

	public Vector3f sub(float float1, float float2, float float3, Vector3f vector3f) {
		vector3f.x = this.x - float1;
		vector3f.y = this.y - float2;
		vector3f.z = this.z - float3;
		return vector3f;
	}

	public Vector3f add(Vector3fc vector3fc) {
		this.x += vector3fc.x();
		this.y += vector3fc.y();
		this.z += vector3fc.z();
		return this;
	}

	public Vector3f add(Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.x = this.x + vector3fc.x();
		vector3f.y = this.y + vector3fc.y();
		vector3f.z = this.z + vector3fc.z();
		return vector3f;
	}

	public Vector3f add(float float1, float float2, float float3) {
		this.x += float1;
		this.y += float2;
		this.z += float3;
		return this;
	}

	public Vector3f add(float float1, float float2, float float3, Vector3f vector3f) {
		vector3f.x = this.x + float1;
		vector3f.y = this.y + float2;
		vector3f.z = this.z + float3;
		return vector3f;
	}

	public Vector3f fma(Vector3fc vector3fc, Vector3fc vector3fc2) {
		this.x = Math.fma(vector3fc.x(), vector3fc2.x(), this.x);
		this.y = Math.fma(vector3fc.y(), vector3fc2.y(), this.y);
		this.z = Math.fma(vector3fc.z(), vector3fc2.z(), this.z);
		return this;
	}

	public Vector3f fma(float float1, Vector3fc vector3fc) {
		this.x = Math.fma(float1, vector3fc.x(), this.x);
		this.y = Math.fma(float1, vector3fc.y(), this.y);
		this.z = Math.fma(float1, vector3fc.z(), this.z);
		return this;
	}

	public Vector3f fma(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3f vector3f) {
		vector3f.x = Math.fma(vector3fc.x(), vector3fc2.x(), this.x);
		vector3f.y = Math.fma(vector3fc.y(), vector3fc2.y(), this.y);
		vector3f.z = Math.fma(vector3fc.z(), vector3fc2.z(), this.z);
		return vector3f;
	}

	public Vector3f fma(float float1, Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.x = Math.fma(float1, vector3fc.x(), this.x);
		vector3f.y = Math.fma(float1, vector3fc.y(), this.y);
		vector3f.z = Math.fma(float1, vector3fc.z(), this.z);
		return vector3f;
	}

	public Vector3f mulAdd(Vector3fc vector3fc, Vector3fc vector3fc2) {
		this.x = Math.fma(this.x, vector3fc.x(), vector3fc2.x());
		this.y = Math.fma(this.y, vector3fc.y(), vector3fc2.y());
		this.z = Math.fma(this.z, vector3fc.z(), vector3fc2.z());
		return this;
	}

	public Vector3f mulAdd(float float1, Vector3fc vector3fc) {
		this.x = Math.fma(this.x, float1, vector3fc.x());
		this.y = Math.fma(this.y, float1, vector3fc.y());
		this.z = Math.fma(this.z, float1, vector3fc.z());
		return this;
	}

	public Vector3f mulAdd(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3f vector3f) {
		vector3f.x = Math.fma(this.x, vector3fc.x(), vector3fc2.x());
		vector3f.y = Math.fma(this.y, vector3fc.y(), vector3fc2.y());
		vector3f.z = Math.fma(this.z, vector3fc.z(), vector3fc2.z());
		return vector3f;
	}

	public Vector3f mulAdd(float float1, Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.x = Math.fma(this.x, float1, vector3fc.x());
		vector3f.y = Math.fma(this.y, float1, vector3fc.y());
		vector3f.z = Math.fma(this.z, float1, vector3fc.z());
		return vector3f;
	}

	public Vector3f mul(Vector3fc vector3fc) {
		this.x *= vector3fc.x();
		this.y *= vector3fc.y();
		this.z *= vector3fc.z();
		return this;
	}

	public Vector3f mul(Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.x = this.x * vector3fc.x();
		vector3f.y = this.y * vector3fc.y();
		vector3f.z = this.z * vector3fc.z();
		return vector3f;
	}

	public Vector3f div(Vector3fc vector3fc) {
		this.x /= vector3fc.x();
		this.y /= vector3fc.y();
		this.z /= vector3fc.z();
		return this;
	}

	public Vector3f div(Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.x = this.x / vector3fc.x();
		vector3f.y = this.y / vector3fc.y();
		vector3f.z = this.z / vector3fc.z();
		return vector3f;
	}

	public Vector3f mulProject(Matrix4fc matrix4fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = 1.0F / Math.fma(matrix4fc.m03(), float1, Math.fma(matrix4fc.m13(), float2, Math.fma(matrix4fc.m23(), float3, matrix4fc.m33())));
		vector3f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, Math.fma(matrix4fc.m20(), float3, matrix4fc.m30()))) * float4;
		vector3f.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m21(), float3, matrix4fc.m31()))) * float4;
		vector3f.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m32()))) * float4;
		return vector3f;
	}

	public Vector3f mulProject(Matrix4fc matrix4fc, float float1, Vector3f vector3f) {
		float float2 = this.x;
		float float3 = this.y;
		float float4 = this.z;
		float float5 = 1.0F / Math.fma(matrix4fc.m03(), float2, Math.fma(matrix4fc.m13(), float3, Math.fma(matrix4fc.m23(), float4, matrix4fc.m33() * float1)));
		vector3f.x = Math.fma(matrix4fc.m00(), float2, Math.fma(matrix4fc.m10(), float3, Math.fma(matrix4fc.m20(), float4, matrix4fc.m30() * float1))) * float5;
		vector3f.y = Math.fma(matrix4fc.m01(), float2, Math.fma(matrix4fc.m11(), float3, Math.fma(matrix4fc.m21(), float4, matrix4fc.m31() * float1))) * float5;
		vector3f.z = Math.fma(matrix4fc.m02(), float2, Math.fma(matrix4fc.m12(), float3, Math.fma(matrix4fc.m22(), float4, matrix4fc.m32() * float1))) * float5;
		return vector3f;
	}

	public Vector3f mulProject(Matrix4fc matrix4fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = 1.0F / Math.fma(matrix4fc.m03(), float1, Math.fma(matrix4fc.m13(), float2, Math.fma(matrix4fc.m23(), float3, matrix4fc.m33())));
		this.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, Math.fma(matrix4fc.m20(), float3, matrix4fc.m30()))) * float4;
		this.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m21(), float3, matrix4fc.m31()))) * float4;
		this.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m32()))) * float4;
		return this;
	}

	public Vector3f mul(Matrix3fc matrix3fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = Math.fma(matrix3fc.m00(), float1, Math.fma(matrix3fc.m10(), float2, matrix3fc.m20() * float3));
		this.y = Math.fma(matrix3fc.m01(), float1, Math.fma(matrix3fc.m11(), float2, matrix3fc.m21() * float3));
		this.z = Math.fma(matrix3fc.m02(), float1, Math.fma(matrix3fc.m12(), float2, matrix3fc.m22() * float3));
		return this;
	}

	public Vector3f mul(Matrix3fc matrix3fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = Math.fma(matrix3fc.m00(), float1, Math.fma(matrix3fc.m10(), float2, matrix3fc.m20() * float3));
		vector3f.y = Math.fma(matrix3fc.m01(), float1, Math.fma(matrix3fc.m11(), float2, matrix3fc.m21() * float3));
		vector3f.z = Math.fma(matrix3fc.m02(), float1, Math.fma(matrix3fc.m12(), float2, matrix3fc.m22() * float3));
		return vector3f;
	}

	public Vector3f mul(Matrix3dc matrix3dc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = (float)Math.fma(matrix3dc.m00(), (double)float1, Math.fma(matrix3dc.m10(), (double)float2, matrix3dc.m20() * (double)float3));
		this.y = (float)Math.fma(matrix3dc.m01(), (double)float1, Math.fma(matrix3dc.m11(), (double)float2, matrix3dc.m21() * (double)float3));
		this.z = (float)Math.fma(matrix3dc.m02(), (double)float1, Math.fma(matrix3dc.m12(), (double)float2, matrix3dc.m22() * (double)float3));
		return this;
	}

	public Vector3f mul(Matrix3dc matrix3dc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = (float)Math.fma(matrix3dc.m00(), (double)float1, Math.fma(matrix3dc.m10(), (double)float2, matrix3dc.m20() * (double)float3));
		vector3f.y = (float)Math.fma(matrix3dc.m01(), (double)float1, Math.fma(matrix3dc.m11(), (double)float2, matrix3dc.m21() * (double)float3));
		vector3f.z = (float)Math.fma(matrix3dc.m02(), (double)float1, Math.fma(matrix3dc.m12(), (double)float2, matrix3dc.m22() * (double)float3));
		return vector3f;
	}

	public Vector3f mul(Matrix3x2fc matrix3x2fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = Math.fma(matrix3x2fc.m00(), float1, Math.fma(matrix3x2fc.m10(), float2, matrix3x2fc.m20() * float3));
		this.y = Math.fma(matrix3x2fc.m01(), float1, Math.fma(matrix3x2fc.m11(), float2, matrix3x2fc.m21() * float3));
		this.z = float3;
		return this;
	}

	public Vector3f mul(Matrix3x2fc matrix3x2fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = Math.fma(matrix3x2fc.m00(), float1, Math.fma(matrix3x2fc.m10(), float2, matrix3x2fc.m20() * float3));
		vector3f.y = Math.fma(matrix3x2fc.m01(), float1, Math.fma(matrix3x2fc.m11(), float2, matrix3x2fc.m21() * float3));
		vector3f.z = float3;
		return vector3f;
	}

	public Vector3f mulTranspose(Matrix3fc matrix3fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = Math.fma(matrix3fc.m00(), float1, Math.fma(matrix3fc.m01(), float2, matrix3fc.m02() * float3));
		this.y = Math.fma(matrix3fc.m10(), float1, Math.fma(matrix3fc.m11(), float2, matrix3fc.m12() * float3));
		this.z = Math.fma(matrix3fc.m20(), float1, Math.fma(matrix3fc.m21(), float2, matrix3fc.m22() * float3));
		return this;
	}

	public Vector3f mulTranspose(Matrix3fc matrix3fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = Math.fma(matrix3fc.m00(), float1, Math.fma(matrix3fc.m01(), float2, matrix3fc.m02() * float3));
		vector3f.y = Math.fma(matrix3fc.m10(), float1, Math.fma(matrix3fc.m11(), float2, matrix3fc.m12() * float3));
		vector3f.z = Math.fma(matrix3fc.m20(), float1, Math.fma(matrix3fc.m21(), float2, matrix3fc.m22() * float3));
		return vector3f;
	}

	public Vector3f mulPosition(Matrix4fc matrix4fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, Math.fma(matrix4fc.m20(), float3, matrix4fc.m30())));
		this.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m21(), float3, matrix4fc.m31())));
		this.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m32())));
		return this;
	}

	public Vector3f mulPosition(Matrix4x3fc matrix4x3fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = Math.fma(matrix4x3fc.m00(), float1, Math.fma(matrix4x3fc.m10(), float2, Math.fma(matrix4x3fc.m20(), float3, matrix4x3fc.m30())));
		this.y = Math.fma(matrix4x3fc.m01(), float1, Math.fma(matrix4x3fc.m11(), float2, Math.fma(matrix4x3fc.m21(), float3, matrix4x3fc.m31())));
		this.z = Math.fma(matrix4x3fc.m02(), float1, Math.fma(matrix4x3fc.m12(), float2, Math.fma(matrix4x3fc.m22(), float3, matrix4x3fc.m32())));
		return this;
	}

	public Vector3f mulPosition(Matrix4fc matrix4fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, Math.fma(matrix4fc.m20(), float3, matrix4fc.m30())));
		vector3f.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m21(), float3, matrix4fc.m31())));
		vector3f.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m32())));
		return vector3f;
	}

	public Vector3f mulPosition(Matrix4x3fc matrix4x3fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = Math.fma(matrix4x3fc.m00(), float1, Math.fma(matrix4x3fc.m10(), float2, Math.fma(matrix4x3fc.m20(), float3, matrix4x3fc.m30())));
		vector3f.y = Math.fma(matrix4x3fc.m01(), float1, Math.fma(matrix4x3fc.m11(), float2, Math.fma(matrix4x3fc.m21(), float3, matrix4x3fc.m31())));
		vector3f.z = Math.fma(matrix4x3fc.m02(), float1, Math.fma(matrix4x3fc.m12(), float2, Math.fma(matrix4x3fc.m22(), float3, matrix4x3fc.m32())));
		return vector3f;
	}

	public Vector3f mulTransposePosition(Matrix4fc matrix4fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m01(), float2, Math.fma(matrix4fc.m02(), float3, matrix4fc.m03())));
		this.y = Math.fma(matrix4fc.m10(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m12(), float3, matrix4fc.m13())));
		this.z = Math.fma(matrix4fc.m20(), float1, Math.fma(matrix4fc.m21(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m23())));
		return this;
	}

	public Vector3f mulTransposePosition(Matrix4fc matrix4fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m01(), float2, Math.fma(matrix4fc.m02(), float3, matrix4fc.m03())));
		vector3f.y = Math.fma(matrix4fc.m10(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m12(), float3, matrix4fc.m13())));
		vector3f.z = Math.fma(matrix4fc.m20(), float1, Math.fma(matrix4fc.m21(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m23())));
		return vector3f;
	}

	public float mulPositionW(Matrix4fc matrix4fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = Math.fma(matrix4fc.m03(), float1, Math.fma(matrix4fc.m13(), float2, Math.fma(matrix4fc.m23(), float3, matrix4fc.m33())));
		this.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, Math.fma(matrix4fc.m20(), float3, matrix4fc.m30())));
		this.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m21(), float3, matrix4fc.m31())));
		this.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m32())));
		return float4;
	}

	public float mulPositionW(Matrix4fc matrix4fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = Math.fma(matrix4fc.m03(), float1, Math.fma(matrix4fc.m13(), float2, Math.fma(matrix4fc.m23(), float3, matrix4fc.m33())));
		vector3f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, Math.fma(matrix4fc.m20(), float3, matrix4fc.m30())));
		vector3f.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, Math.fma(matrix4fc.m21(), float3, matrix4fc.m31())));
		vector3f.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, Math.fma(matrix4fc.m22(), float3, matrix4fc.m32())));
		return float4;
	}

	public Vector3f mulDirection(Matrix4dc matrix4dc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = (float)Math.fma(matrix4dc.m00(), (double)float1, Math.fma(matrix4dc.m10(), (double)float2, matrix4dc.m20() * (double)float3));
		this.y = (float)Math.fma(matrix4dc.m01(), (double)float1, Math.fma(matrix4dc.m11(), (double)float2, matrix4dc.m21() * (double)float3));
		this.z = (float)Math.fma(matrix4dc.m02(), (double)float1, Math.fma(matrix4dc.m12(), (double)float2, matrix4dc.m22() * (double)float3));
		return this;
	}

	public Vector3f mulDirection(Matrix4fc matrix4fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, matrix4fc.m20() * float3));
		this.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, matrix4fc.m21() * float3));
		this.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, matrix4fc.m22() * float3));
		return this;
	}

	public Vector3f mulDirection(Matrix4x3fc matrix4x3fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = Math.fma(matrix4x3fc.m00(), float1, Math.fma(matrix4x3fc.m10(), float2, matrix4x3fc.m20() * float3));
		this.y = Math.fma(matrix4x3fc.m01(), float1, Math.fma(matrix4x3fc.m11(), float2, matrix4x3fc.m21() * float3));
		this.z = Math.fma(matrix4x3fc.m02(), float1, Math.fma(matrix4x3fc.m12(), float2, matrix4x3fc.m22() * float3));
		return this;
	}

	public Vector3f mulDirection(Matrix4dc matrix4dc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = (float)Math.fma(matrix4dc.m00(), (double)float1, Math.fma(matrix4dc.m10(), (double)float2, matrix4dc.m20() * (double)float3));
		vector3f.y = (float)Math.fma(matrix4dc.m01(), (double)float1, Math.fma(matrix4dc.m11(), (double)float2, matrix4dc.m21() * (double)float3));
		vector3f.z = (float)Math.fma(matrix4dc.m02(), (double)float1, Math.fma(matrix4dc.m12(), (double)float2, matrix4dc.m22() * (double)float3));
		return vector3f;
	}

	public Vector3f mulDirection(Matrix4fc matrix4fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m10(), float2, matrix4fc.m20() * float3));
		vector3f.y = Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m11(), float2, matrix4fc.m21() * float3));
		vector3f.z = Math.fma(matrix4fc.m02(), float1, Math.fma(matrix4fc.m12(), float2, matrix4fc.m22() * float3));
		return vector3f;
	}

	public Vector3f mulDirection(Matrix4x3fc matrix4x3fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = Math.fma(matrix4x3fc.m00(), float1, Math.fma(matrix4x3fc.m10(), float2, matrix4x3fc.m20() * float3));
		vector3f.y = Math.fma(matrix4x3fc.m01(), float1, Math.fma(matrix4x3fc.m11(), float2, matrix4x3fc.m21() * float3));
		vector3f.z = Math.fma(matrix4x3fc.m02(), float1, Math.fma(matrix4x3fc.m12(), float2, matrix4x3fc.m22() * float3));
		return vector3f;
	}

	public Vector3f mulTransposeDirection(Matrix4fc matrix4fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m01(), float2, matrix4fc.m02() * float3));
		this.y = Math.fma(matrix4fc.m10(), float1, Math.fma(matrix4fc.m11(), float2, matrix4fc.m12() * float3));
		this.z = Math.fma(matrix4fc.m20(), float1, Math.fma(matrix4fc.m21(), float2, matrix4fc.m22() * float3));
		return this;
	}

	public Vector3f mulTransposeDirection(Matrix4fc matrix4fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = Math.fma(matrix4fc.m00(), float1, Math.fma(matrix4fc.m01(), float2, matrix4fc.m02() * float3));
		vector3f.y = Math.fma(matrix4fc.m10(), float1, Math.fma(matrix4fc.m11(), float2, matrix4fc.m12() * float3));
		vector3f.z = Math.fma(matrix4fc.m20(), float1, Math.fma(matrix4fc.m21(), float2, matrix4fc.m22() * float3));
		return vector3f;
	}

	public Vector3f mul(float float1) {
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		return this;
	}

	public Vector3f mul(float float1, Vector3f vector3f) {
		vector3f.x = this.x * float1;
		vector3f.y = this.y * float1;
		vector3f.z = this.z * float1;
		return vector3f;
	}

	public Vector3f mul(float float1, float float2, float float3) {
		this.x *= float1;
		this.y *= float2;
		this.z *= float3;
		return this;
	}

	public Vector3f mul(float float1, float float2, float float3, Vector3f vector3f) {
		vector3f.x = this.x * float1;
		vector3f.y = this.y * float2;
		vector3f.z = this.z * float3;
		return vector3f;
	}

	public Vector3f div(float float1) {
		float float2 = 1.0F / float1;
		this.x *= float2;
		this.y *= float2;
		this.z *= float2;
		return this;
	}

	public Vector3f div(float float1, Vector3f vector3f) {
		float float2 = 1.0F / float1;
		vector3f.x = this.x * float2;
		vector3f.y = this.y * float2;
		vector3f.z = this.z * float2;
		return vector3f;
	}

	public Vector3f div(float float1, float float2, float float3) {
		this.x /= float1;
		this.y /= float2;
		this.z /= float3;
		return this;
	}

	public Vector3f div(float float1, float float2, float float3, Vector3f vector3f) {
		vector3f.x = this.x / float1;
		vector3f.y = this.y / float2;
		vector3f.z = this.z / float3;
		return vector3f;
	}

	public Vector3f rotate(Quaternionfc quaternionfc) {
		return quaternionfc.transform((Vector3fc)this, (Vector3f)this);
	}

	public Vector3f rotate(Quaternionfc quaternionfc, Vector3f vector3f) {
		return quaternionfc.transform((Vector3fc)this, (Vector3f)vector3f);
	}

	public Quaternionf rotationTo(Vector3fc vector3fc, Quaternionf quaternionf) {
		return quaternionf.rotationTo(this, vector3fc);
	}

	public Quaternionf rotationTo(float float1, float float2, float float3, Quaternionf quaternionf) {
		return quaternionf.rotationTo(this.x, this.y, this.z, float1, float2, float3);
	}

	public Vector3f rotateAxis(float float1, float float2, float float3, float float4) {
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return this.rotateX(float2 * float1, this);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return this.rotateY(float3 * float1, this);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? this.rotateZ(float4 * float1, this) : this.rotateAxisInternal(float1, float2, float3, float4, this);
		}
	}

	public Vector3f rotateAxis(float float1, float float2, float float3, float float4, Vector3f vector3f) {
		if (float3 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float2)) {
			return this.rotateX(float2 * float1, vector3f);
		} else if (float2 == 0.0F && float4 == 0.0F && Math.absEqualsOne(float3)) {
			return this.rotateY(float3 * float1, vector3f);
		} else {
			return float2 == 0.0F && float3 == 0.0F && Math.absEqualsOne(float4) ? this.rotateZ(float4 * float1, vector3f) : this.rotateAxisInternal(float1, float2, float3, float4, vector3f);
		}
	}

	private Vector3f rotateAxisInternal(float float1, float float2, float float3, float float4, Vector3f vector3f) {
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
		vector3f.x = (float11 + float12 - float14 - float13) * float21 + (-float15 + float16 - float15 + float16) * float22 + (float18 + float17 + float17 + float18) * float23;
		vector3f.y = (float16 + float15 + float15 + float16) * float21 + (float13 - float14 + float11 - float12) * float22 + (float19 + float19 - float20 - float20) * float23;
		vector3f.z = (float17 - float18 + float17 - float18) * float21 + (float19 + float19 + float20 + float20) * float22 + (float14 - float13 - float12 + float11) * float23;
		return vector3f;
	}

	public Vector3f rotateX(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.y * float3 - this.z * float2;
		float float5 = this.y * float2 + this.z * float3;
		this.y = float4;
		this.z = float5;
		return this;
	}

	public Vector3f rotateX(float float1, Vector3f vector3f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.y * float3 - this.z * float2;
		float float5 = this.y * float2 + this.z * float3;
		vector3f.x = this.x;
		vector3f.y = float4;
		vector3f.z = float5;
		return vector3f;
	}

	public Vector3f rotateY(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.x * float3 + this.z * float2;
		float float5 = -this.x * float2 + this.z * float3;
		this.x = float4;
		this.z = float5;
		return this;
	}

	public Vector3f rotateY(float float1, Vector3f vector3f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.x * float3 + this.z * float2;
		float float5 = -this.x * float2 + this.z * float3;
		vector3f.x = float4;
		vector3f.y = this.y;
		vector3f.z = float5;
		return vector3f;
	}

	public Vector3f rotateZ(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.x * float3 - this.y * float2;
		float float5 = this.x * float2 + this.y * float3;
		this.x = float4;
		this.y = float5;
		return this;
	}

	public Vector3f rotateZ(float float1, Vector3f vector3f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.x * float3 - this.y * float2;
		float float5 = this.x * float2 + this.y * float3;
		vector3f.x = float4;
		vector3f.y = float5;
		vector3f.z = this.z;
		return vector3f;
	}

	public float lengthSquared() {
		return Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z));
	}

	public static float lengthSquared(float float1, float float2, float float3) {
		return Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3));
	}

	public float length() {
		return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
	}

	public static float length(float float1, float float2, float float3) {
		return Math.sqrt(Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3)));
	}

	public Vector3f normalize() {
		float float1 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		return this;
	}

	public Vector3f normalize(Vector3f vector3f) {
		float float1 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
		vector3f.x = this.x * float1;
		vector3f.y = this.y * float1;
		vector3f.z = this.z * float1;
		return vector3f;
	}

	public Vector3f normalize(float float1) {
		float float2 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z))) * float1;
		this.x *= float2;
		this.y *= float2;
		this.z *= float2;
		return this;
	}

	public Vector3f normalize(float float1, Vector3f vector3f) {
		float float2 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z))) * float1;
		vector3f.x = this.x * float2;
		vector3f.y = this.y * float2;
		vector3f.z = this.z * float2;
		return vector3f;
	}

	public Vector3f cross(Vector3fc vector3fc) {
		float float1 = Math.fma(this.y, vector3fc.z(), -this.z * vector3fc.y());
		float float2 = Math.fma(this.z, vector3fc.x(), -this.x * vector3fc.z());
		float float3 = Math.fma(this.x, vector3fc.y(), -this.y * vector3fc.x());
		this.x = float1;
		this.y = float2;
		this.z = float3;
		return this;
	}

	public Vector3f cross(float float1, float float2, float float3) {
		float float4 = Math.fma(this.y, float3, -this.z * float2);
		float float5 = Math.fma(this.z, float1, -this.x * float3);
		float float6 = Math.fma(this.x, float2, -this.y * float1);
		this.x = float4;
		this.y = float5;
		this.z = float6;
		return this;
	}

	public Vector3f cross(Vector3fc vector3fc, Vector3f vector3f) {
		float float1 = Math.fma(this.y, vector3fc.z(), -this.z * vector3fc.y());
		float float2 = Math.fma(this.z, vector3fc.x(), -this.x * vector3fc.z());
		float float3 = Math.fma(this.x, vector3fc.y(), -this.y * vector3fc.x());
		vector3f.x = float1;
		vector3f.y = float2;
		vector3f.z = float3;
		return vector3f;
	}

	public Vector3f cross(float float1, float float2, float float3, Vector3f vector3f) {
		float float4 = Math.fma(this.y, float3, -this.z * float2);
		float float5 = Math.fma(this.z, float1, -this.x * float3);
		float float6 = Math.fma(this.x, float2, -this.y * float1);
		vector3f.x = float4;
		vector3f.y = float5;
		vector3f.z = float6;
		return vector3f;
	}

	public float distance(Vector3fc vector3fc) {
		float float1 = this.x - vector3fc.x();
		float float2 = this.y - vector3fc.y();
		float float3 = this.z - vector3fc.z();
		return Math.sqrt(Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3)));
	}

	public float distance(float float1, float float2, float float3) {
		float float4 = this.x - float1;
		float float5 = this.y - float2;
		float float6 = this.z - float3;
		return Math.sqrt(Math.fma(float4, float4, Math.fma(float5, float5, float6 * float6)));
	}

	public float distanceSquared(Vector3fc vector3fc) {
		float float1 = this.x - vector3fc.x();
		float float2 = this.y - vector3fc.y();
		float float3 = this.z - vector3fc.z();
		return Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3));
	}

	public float distanceSquared(float float1, float float2, float float3) {
		float float4 = this.x - float1;
		float float5 = this.y - float2;
		float float6 = this.z - float3;
		return Math.fma(float4, float4, Math.fma(float5, float5, float6 * float6));
	}

	public static float distance(float float1, float float2, float float3, float float4, float float5, float float6) {
		return Math.sqrt(distanceSquared(float1, float2, float3, float4, float5, float6));
	}

	public static float distanceSquared(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = float1 - float4;
		float float8 = float2 - float5;
		float float9 = float3 - float6;
		return Math.fma(float7, float7, Math.fma(float8, float8, float9 * float9));
	}

	public float dot(Vector3fc vector3fc) {
		return Math.fma(this.x, vector3fc.x(), Math.fma(this.y, vector3fc.y(), this.z * vector3fc.z()));
	}

	public float dot(float float1, float float2, float float3) {
		return Math.fma(this.x, float1, Math.fma(this.y, float2, this.z * float3));
	}

	public float angleCos(Vector3fc vector3fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		float float4 = Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3));
		float float5 = Math.fma(vector3fc.x(), vector3fc.x(), Math.fma(vector3fc.y(), vector3fc.y(), vector3fc.z() * vector3fc.z()));
		float float6 = Math.fma(float1, vector3fc.x(), Math.fma(float2, vector3fc.y(), float3 * vector3fc.z()));
		return float6 / Math.sqrt(float4 * float5);
	}

	public float angle(Vector3fc vector3fc) {
		float float1 = this.angleCos(vector3fc);
		float1 = float1 < 1.0F ? float1 : 1.0F;
		float1 = float1 > -1.0F ? float1 : -1.0F;
		return Math.acos(float1);
	}

	public float angleSigned(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.angleSigned(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public float angleSigned(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = this.x;
		float float8 = this.y;
		float float9 = this.z;
		return Math.atan2((float8 * float3 - float9 * float2) * float4 + (float9 * float1 - float7 * float3) * float5 + (float7 * float2 - float8 * float1) * float6, float7 * float1 + float8 * float2 + float9 * float3);
	}

	public Vector3f min(Vector3fc vector3fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = float1 < vector3fc.x() ? float1 : vector3fc.x();
		this.y = float2 < vector3fc.y() ? float2 : vector3fc.y();
		this.z = float3 < vector3fc.z() ? float3 : vector3fc.z();
		return this;
	}

	public Vector3f min(Vector3fc vector3fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = float1 < vector3fc.x() ? float1 : vector3fc.x();
		vector3f.y = float2 < vector3fc.y() ? float2 : vector3fc.y();
		vector3f.z = float3 < vector3fc.z() ? float3 : vector3fc.z();
		return vector3f;
	}

	public Vector3f max(Vector3fc vector3fc) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x = float1 > vector3fc.x() ? float1 : vector3fc.x();
		this.y = float2 > vector3fc.y() ? float2 : vector3fc.y();
		this.z = float3 > vector3fc.z() ? float3 : vector3fc.z();
		return this;
	}

	public Vector3f max(Vector3fc vector3fc, Vector3f vector3f) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		vector3f.x = float1 > vector3fc.x() ? float1 : vector3fc.x();
		vector3f.y = float2 > vector3fc.y() ? float2 : vector3fc.y();
		vector3f.z = float3 > vector3fc.z() ? float3 : vector3fc.z();
		return vector3f;
	}

	public Vector3f zero() {
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
		return this;
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format((double)this.x, numberFormat);
		return "(" + string + " " + Runtime.format((double)this.y, numberFormat) + " " + Runtime.format((double)this.z, numberFormat) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeFloat(this.x);
		objectOutput.writeFloat(this.y);
		objectOutput.writeFloat(this.z);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.set(objectInput.readFloat(), objectInput.readFloat(), objectInput.readFloat());
	}

	public Vector3f negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	public Vector3f negate(Vector3f vector3f) {
		vector3f.x = -this.x;
		vector3f.y = -this.y;
		vector3f.z = -this.z;
		return vector3f;
	}

	public Vector3f absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
		return this;
	}

	public Vector3f absolute(Vector3f vector3f) {
		vector3f.x = Math.abs(this.x);
		vector3f.y = Math.abs(this.y);
		vector3f.z = Math.abs(this.z);
		return vector3f;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + Float.floatToIntBits(this.x);
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
			Vector3f vector3f = (Vector3f)object;
			if (Float.floatToIntBits(this.x) != Float.floatToIntBits(vector3f.x)) {
				return false;
			} else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(vector3f.y)) {
				return false;
			} else {
				return Float.floatToIntBits(this.z) == Float.floatToIntBits(vector3f.z);
			}
		}
	}

	public boolean equals(Vector3fc vector3fc, float float1) {
		if (this == vector3fc) {
			return true;
		} else if (vector3fc == null) {
			return false;
		} else if (!(vector3fc instanceof Vector3fc)) {
			return false;
		} else if (!Runtime.equals(this.x, vector3fc.x(), float1)) {
			return false;
		} else if (!Runtime.equals(this.y, vector3fc.y(), float1)) {
			return false;
		} else {
			return Runtime.equals(this.z, vector3fc.z(), float1);
		}
	}

	public boolean equals(float float1, float float2, float float3) {
		if (Float.floatToIntBits(this.x) != Float.floatToIntBits(float1)) {
			return false;
		} else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(float2)) {
			return false;
		} else {
			return Float.floatToIntBits(this.z) == Float.floatToIntBits(float3);
		}
	}

	public Vector3f reflect(Vector3fc vector3fc) {
		float float1 = vector3fc.x();
		float float2 = vector3fc.y();
		float float3 = vector3fc.z();
		float float4 = Math.fma(this.x, float1, Math.fma(this.y, float2, this.z * float3));
		this.x -= (float4 + float4) * float1;
		this.y -= (float4 + float4) * float2;
		this.z -= (float4 + float4) * float3;
		return this;
	}

	public Vector3f reflect(float float1, float float2, float float3) {
		float float4 = Math.fma(this.x, float1, Math.fma(this.y, float2, this.z * float3));
		this.x -= (float4 + float4) * float1;
		this.y -= (float4 + float4) * float2;
		this.z -= (float4 + float4) * float3;
		return this;
	}

	public Vector3f reflect(Vector3fc vector3fc, Vector3f vector3f) {
		return this.reflect(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3f);
	}

	public Vector3f reflect(float float1, float float2, float float3, Vector3f vector3f) {
		float float4 = this.dot(float1, float2, float3);
		vector3f.x = this.x - (float4 + float4) * float1;
		vector3f.y = this.y - (float4 + float4) * float2;
		vector3f.z = this.z - (float4 + float4) * float3;
		return vector3f;
	}

	public Vector3f half(Vector3fc vector3fc) {
		return this.set((Vector3fc)this).add(vector3fc.x(), vector3fc.y(), vector3fc.z()).normalize();
	}

	public Vector3f half(float float1, float float2, float float3) {
		return this.half(float1, float2, float3, this);
	}

	public Vector3f half(Vector3fc vector3fc, Vector3f vector3f) {
		return this.half(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3f);
	}

	public Vector3f half(float float1, float float2, float float3, Vector3f vector3f) {
		return vector3f.set((Vector3fc)this).add(float1, float2, float3).normalize();
	}

	public Vector3f smoothStep(Vector3fc vector3fc, float float1, Vector3f vector3f) {
		float float2 = this.x;
		float float3 = this.y;
		float float4 = this.z;
		float float5 = float1 * float1;
		float float6 = float5 * float1;
		vector3f.x = (float2 + float2 - vector3fc.x() - vector3fc.x()) * float6 + (3.0F * vector3fc.x() - 3.0F * float2) * float5 + float2 * float1 + float2;
		vector3f.y = (float3 + float3 - vector3fc.y() - vector3fc.y()) * float6 + (3.0F * vector3fc.y() - 3.0F * float3) * float5 + float3 * float1 + float3;
		vector3f.z = (float4 + float4 - vector3fc.z() - vector3fc.z()) * float6 + (3.0F * vector3fc.z() - 3.0F * float4) * float5 + float4 * float1 + float4;
		return vector3f;
	}

	public Vector3f hermite(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, float float1, Vector3f vector3f) {
		float float2 = this.x;
		float float3 = this.y;
		float float4 = this.z;
		float float5 = float1 * float1;
		float float6 = float5 * float1;
		vector3f.x = (float2 + float2 - vector3fc2.x() - vector3fc2.x() + vector3fc3.x() + vector3fc.x()) * float6 + (3.0F * vector3fc2.x() - 3.0F * float2 - vector3fc.x() - vector3fc.x() - vector3fc3.x()) * float5 + float2 * float1 + float2;
		vector3f.y = (float3 + float3 - vector3fc2.y() - vector3fc2.y() + vector3fc3.y() + vector3fc.y()) * float6 + (3.0F * vector3fc2.y() - 3.0F * float3 - vector3fc.y() - vector3fc.y() - vector3fc3.y()) * float5 + float3 * float1 + float3;
		vector3f.z = (float4 + float4 - vector3fc2.z() - vector3fc2.z() + vector3fc3.z() + vector3fc.z()) * float6 + (3.0F * vector3fc2.z() - 3.0F * float4 - vector3fc.z() - vector3fc.z() - vector3fc3.z()) * float5 + float4 * float1 + float4;
		return vector3f;
	}

	public Vector3f lerp(Vector3fc vector3fc, float float1) {
		return this.lerp(vector3fc, float1, this);
	}

	public Vector3f lerp(Vector3fc vector3fc, float float1, Vector3f vector3f) {
		vector3f.x = Math.fma(vector3fc.x() - this.x, float1, this.x);
		vector3f.y = Math.fma(vector3fc.y() - this.y, float1, this.y);
		vector3f.z = Math.fma(vector3fc.z() - this.z, float1, this.z);
		return vector3f;
	}

	public float get(int int1) throws IllegalArgumentException {
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
		vector3f.x = this.x();
		vector3f.y = this.y();
		vector3f.z = this.z();
		return vector3f;
	}

	public Vector3d get(Vector3d vector3d) {
		vector3d.x = (double)this.x();
		vector3d.y = (double)this.y();
		vector3d.z = (double)this.z();
		return vector3d;
	}

	public int maxComponent() {
		float float1 = Math.abs(this.x);
		float float2 = Math.abs(this.y);
		float float3 = Math.abs(this.z);
		if (float1 >= float2 && float1 >= float3) {
			return 0;
		} else {
			return float2 >= float3 ? 1 : 2;
		}
	}

	public int minComponent() {
		float float1 = Math.abs(this.x);
		float float2 = Math.abs(this.y);
		float float3 = Math.abs(this.z);
		if (float1 < float2 && float1 < float3) {
			return 0;
		} else {
			return float2 < float3 ? 1 : 2;
		}
	}

	public Vector3f orthogonalize(Vector3fc vector3fc, Vector3f vector3f) {
		float float1;
		float float2;
		float float3;
		if (Math.abs(vector3fc.x()) > Math.abs(vector3fc.z())) {
			float1 = -vector3fc.y();
			float2 = vector3fc.x();
			float3 = 0.0F;
		} else {
			float1 = 0.0F;
			float2 = -vector3fc.z();
			float3 = vector3fc.y();
		}

		float float4 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
		vector3f.x = float1 * float4;
		vector3f.y = float2 * float4;
		vector3f.z = float3 * float4;
		return vector3f;
	}

	public Vector3f orthogonalize(Vector3fc vector3fc) {
		return this.orthogonalize(vector3fc, this);
	}

	public Vector3f orthogonalizeUnit(Vector3fc vector3fc, Vector3f vector3f) {
		return this.orthogonalize(vector3fc, vector3f);
	}

	public Vector3f orthogonalizeUnit(Vector3fc vector3fc) {
		return this.orthogonalizeUnit(vector3fc, this);
	}

	public Vector3f floor() {
		return this.floor(this);
	}

	public Vector3f floor(Vector3f vector3f) {
		vector3f.x = Math.floor(this.x);
		vector3f.y = Math.floor(this.y);
		vector3f.z = Math.floor(this.z);
		return vector3f;
	}

	public Vector3f ceil() {
		return this.ceil(this);
	}

	public Vector3f ceil(Vector3f vector3f) {
		vector3f.x = Math.ceil(this.x);
		vector3f.y = Math.ceil(this.y);
		vector3f.z = Math.ceil(this.z);
		return vector3f;
	}

	public Vector3f round() {
		return this.round(this);
	}

	public Vector3f round(Vector3f vector3f) {
		vector3f.x = (float)Math.round(this.x);
		vector3f.y = (float)Math.round(this.y);
		vector3f.z = (float)Math.round(this.z);
		return vector3f;
	}

	public boolean isFinite() {
		return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z);
	}
}
