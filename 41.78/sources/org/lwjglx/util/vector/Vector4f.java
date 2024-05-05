package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;


public class Vector4f extends Vector implements Serializable,ReadableVector4f,WritableVector4f {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;
	public float z;
	public float w;

	public Vector4f() {
	}

	public Vector4f(ReadableVector4f readableVector4f) {
		this.set(readableVector4f);
	}

	public Vector4f(float float1, float float2, float float3, float float4) {
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

	public Vector4f set(ReadableVector4f readableVector4f) {
		this.x = readableVector4f.getX();
		this.y = readableVector4f.getY();
		this.z = readableVector4f.getZ();
		this.w = readableVector4f.getW();
		return this;
	}

	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}

	public Vector4f translate(float float1, float float2, float float3, float float4) {
		this.x += float1;
		this.y += float2;
		this.z += float3;
		this.w += float4;
		return this;
	}

	public static Vector4f add(Vector4f vector4f, Vector4f vector4f2, Vector4f vector4f3) {
		if (vector4f3 == null) {
			return new Vector4f(vector4f.x + vector4f2.x, vector4f.y + vector4f2.y, vector4f.z + vector4f2.z, vector4f.w + vector4f2.w);
		} else {
			vector4f3.set(vector4f.x + vector4f2.x, vector4f.y + vector4f2.y, vector4f.z + vector4f2.z, vector4f.w + vector4f2.w);
			return vector4f3;
		}
	}

	public static Vector4f sub(Vector4f vector4f, Vector4f vector4f2, Vector4f vector4f3) {
		if (vector4f3 == null) {
			return new Vector4f(vector4f.x - vector4f2.x, vector4f.y - vector4f2.y, vector4f.z - vector4f2.z, vector4f.w - vector4f2.w);
		} else {
			vector4f3.set(vector4f.x - vector4f2.x, vector4f.y - vector4f2.y, vector4f.z - vector4f2.z, vector4f.w - vector4f2.w);
			return vector4f3;
		}
	}

	public Vector negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
		return this;
	}

	public Vector4f negate(Vector4f vector4f) {
		if (vector4f == null) {
			vector4f = new Vector4f();
		}

		vector4f.x = -this.x;
		vector4f.y = -this.y;
		vector4f.z = -this.z;
		vector4f.w = -this.w;
		return vector4f;
	}

	public Vector4f normalise(Vector4f vector4f) {
		float float1 = this.length();
		if (vector4f == null) {
			vector4f = new Vector4f(this.x / float1, this.y / float1, this.z / float1, this.w / float1);
		} else {
			vector4f.set(this.x / float1, this.y / float1, this.z / float1, this.w / float1);
		}

		return vector4f;
	}

	public static float dot(Vector4f vector4f, Vector4f vector4f2) {
		return vector4f.x * vector4f2.x + vector4f.y * vector4f2.y + vector4f.z * vector4f2.z + vector4f.w * vector4f2.w;
	}

	public static float angle(Vector4f vector4f, Vector4f vector4f2) {
		float float1 = dot(vector4f, vector4f2) / (vector4f.length() * vector4f2.length());
		if (float1 < -1.0F) {
			float1 = -1.0F;
		} else if (float1 > 1.0F) {
			float1 = 1.0F;
		}

		return (float)Math.acos((double)float1);
	}

	public Vector load(FloatBuffer floatBuffer) {
		this.x = floatBuffer.get();
		this.y = floatBuffer.get();
		this.z = floatBuffer.get();
		this.w = floatBuffer.get();
		return this;
	}

	public Vector scale(float float1) {
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		this.w *= float1;
		return this;
	}

	public Vector store(FloatBuffer floatBuffer) {
		floatBuffer.put(this.x);
		floatBuffer.put(this.y);
		floatBuffer.put(this.z);
		floatBuffer.put(this.w);
		return this;
	}

	public String toString() {
		return "Vector4f: " + this.x + " " + this.y + " " + this.z + " " + this.w;
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
}
