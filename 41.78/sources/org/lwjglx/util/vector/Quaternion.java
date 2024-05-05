package org.lwjglx.util.vector;

import java.nio.FloatBuffer;


public class Quaternion extends Vector implements ReadableVector4f {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;
	public float z;
	public float w;

	public Quaternion() {
		this.setIdentity();
	}

	public Quaternion(ReadableVector4f readableVector4f) {
		this.set(readableVector4f);
	}

	public Quaternion(float float1, float float2, float float3, float float4) {
		this.set(float1, float2, float3, float4);
	}

	public void set(float float1, float float2) {
		this.x = float1;
		this.y = float2;
	}

	public void set(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
	}

	public void set(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
	}

	public Quaternion set(ReadableVector4f readableVector4f) {
		this.x = readableVector4f.getX();
		this.y = readableVector4f.getY();
		this.z = readableVector4f.getZ();
		this.w = readableVector4f.getW();
		return this;
	}

	public Quaternion setIdentity() {
		return setIdentity(this);
	}

	public static Quaternion setIdentity(Quaternion quaternion) {
		quaternion.x = 0.0F;
		quaternion.y = 0.0F;
		quaternion.z = 0.0F;
		quaternion.w = 1.0F;
		return quaternion;
	}

	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}

	public static Quaternion normalise(Quaternion quaternion, Quaternion quaternion2) {
		float float1 = 1.0F / quaternion.length();
		if (quaternion2 == null) {
			quaternion2 = new Quaternion();
		}

		quaternion2.set(quaternion.x * float1, quaternion.y * float1, quaternion.z * float1, quaternion.w * float1);
		return quaternion2;
	}

	public Quaternion normalise(Quaternion quaternion) {
		return normalise(this, quaternion);
	}

	public static float dot(Quaternion quaternion, Quaternion quaternion2) {
		return quaternion.x * quaternion2.x + quaternion.y * quaternion2.y + quaternion.z * quaternion2.z + quaternion.w * quaternion2.w;
	}

	public Quaternion negate(Quaternion quaternion) {
		return negate(this, quaternion);
	}

	public static Quaternion negate(Quaternion quaternion, Quaternion quaternion2) {
		if (quaternion2 == null) {
			quaternion2 = new Quaternion();
		}

		quaternion2.x = -quaternion.x;
		quaternion2.y = -quaternion.y;
		quaternion2.z = -quaternion.z;
		quaternion2.w = quaternion.w;
		return quaternion2;
	}

	public Vector negate() {
		return negate(this, this);
	}

	public Vector load(FloatBuffer floatBuffer) {
		this.x = floatBuffer.get();
		this.y = floatBuffer.get();
		this.z = floatBuffer.get();
		this.w = floatBuffer.get();
		return this;
	}

	public Vector scale(float float1) {
		return scale(float1, this, this);
	}

	public static Quaternion scale(float float1, Quaternion quaternion, Quaternion quaternion2) {
		if (quaternion2 == null) {
			quaternion2 = new Quaternion();
		}

		quaternion2.x = quaternion.x * float1;
		quaternion2.y = quaternion.y * float1;
		quaternion2.z = quaternion.z * float1;
		quaternion2.w = quaternion.w * float1;
		return quaternion2;
	}

	public Vector store(FloatBuffer floatBuffer) {
		floatBuffer.put(this.x);
		floatBuffer.put(this.y);
		floatBuffer.put(this.z);
		floatBuffer.put(this.w);
		return this;
	}

	public final float getX() {
		return this.x;
	}

	public final float getY() {
		return this.y;
	}

	public final void setX(float float1) {
		this.x = float1;
	}

	public final void setY(float float1) {
		this.y = float1;
	}

	public void setZ(float float1) {
		this.z = float1;
	}

	public float getZ() {
		return this.z;
	}

	public void setW(float float1) {
		this.w = float1;
	}

	public float getW() {
		return this.w;
	}

	public String toString() {
		return "Quaternion: " + this.x + " " + this.y + " " + this.z + " " + this.w;
	}

	public static Quaternion mul(Quaternion quaternion, Quaternion quaternion2, Quaternion quaternion3) {
		if (quaternion3 == null) {
			quaternion3 = new Quaternion();
		}

		quaternion3.set(quaternion.x * quaternion2.w + quaternion.w * quaternion2.x + quaternion.y * quaternion2.z - quaternion.z * quaternion2.y, quaternion.y * quaternion2.w + quaternion.w * quaternion2.y + quaternion.z * quaternion2.x - quaternion.x * quaternion2.z, quaternion.z * quaternion2.w + quaternion.w * quaternion2.z + quaternion.x * quaternion2.y - quaternion.y * quaternion2.x, quaternion.w * quaternion2.w - quaternion.x * quaternion2.x - quaternion.y * quaternion2.y - quaternion.z * quaternion2.z);
		return quaternion3;
	}

	public static Quaternion mulInverse(Quaternion quaternion, Quaternion quaternion2, Quaternion quaternion3) {
		float float1 = quaternion2.lengthSquared();
		float1 = (double)float1 == 0.0 ? float1 : 1.0F / float1;
		if (quaternion3 == null) {
			quaternion3 = new Quaternion();
		}

		quaternion3.set((quaternion.x * quaternion2.w - quaternion.w * quaternion2.x - quaternion.y * quaternion2.z + quaternion.z * quaternion2.y) * float1, (quaternion.y * quaternion2.w - quaternion.w * quaternion2.y - quaternion.z * quaternion2.x + quaternion.x * quaternion2.z) * float1, (quaternion.z * quaternion2.w - quaternion.w * quaternion2.z - quaternion.x * quaternion2.y + quaternion.y * quaternion2.x) * float1, (quaternion.w * quaternion2.w + quaternion.x * quaternion2.x + quaternion.y * quaternion2.y + quaternion.z * quaternion2.z) * float1);
		return quaternion3;
	}

	public final void setFromAxisAngle(Vector4f vector4f) {
		this.x = vector4f.x;
		this.y = vector4f.y;
		this.z = vector4f.z;
		float float1 = (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
		float float2 = (float)(Math.sin(0.5 * (double)vector4f.w) / (double)float1);
		this.x *= float2;
		this.y *= float2;
		this.z *= float2;
		this.w = (float)Math.cos(0.5 * (double)vector4f.w);
	}

	public final Quaternion setFromMatrix(Matrix4f matrix4f) {
		return setFromMatrix(matrix4f, this);
	}

	public static Quaternion setFromMatrix(Matrix4f matrix4f, Quaternion quaternion) {
		return quaternion.setFromMat(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m20, matrix4f.m21, matrix4f.m22);
	}

	public final Quaternion setFromMatrix(Matrix3f matrix3f) {
		return setFromMatrix(matrix3f, this);
	}

	public static Quaternion setFromMatrix(Matrix3f matrix3f, Quaternion quaternion) {
		return quaternion.setFromMat(matrix3f.m00, matrix3f.m01, matrix3f.m02, matrix3f.m10, matrix3f.m11, matrix3f.m12, matrix3f.m20, matrix3f.m21, matrix3f.m22);
	}

	private Quaternion setFromMat(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		float float10 = float1 + float5 + float9;
		float float11;
		if ((double)float10 >= 0.0) {
			float11 = (float)Math.sqrt((double)float10 + 1.0);
			this.w = float11 * 0.5F;
			float11 = 0.5F / float11;
			this.x = (float8 - float6) * float11;
			this.y = (float3 - float7) * float11;
			this.z = (float4 - float2) * float11;
		} else {
			float float12 = Math.max(Math.max(float1, float5), float9);
			if (float12 == float1) {
				float11 = (float)Math.sqrt((double)(float1 - (float5 + float9)) + 1.0);
				this.x = float11 * 0.5F;
				float11 = 0.5F / float11;
				this.y = (float2 + float4) * float11;
				this.z = (float7 + float3) * float11;
				this.w = (float8 - float6) * float11;
			} else if (float12 == float5) {
				float11 = (float)Math.sqrt((double)(float5 - (float9 + float1)) + 1.0);
				this.y = float11 * 0.5F;
				float11 = 0.5F / float11;
				this.z = (float6 + float8) * float11;
				this.x = (float2 + float4) * float11;
				this.w = (float3 - float7) * float11;
			} else {
				float11 = (float)Math.sqrt((double)(float9 - (float1 + float5)) + 1.0);
				this.z = float11 * 0.5F;
				float11 = 0.5F / float11;
				this.x = (float7 + float3) * float11;
				this.y = (float6 + float8) * float11;
				this.w = (float4 - float2) * float11;
			}
		}

		return this;
	}
}
