package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;


public class Vector3f extends Vector implements Serializable,ReadableVector3f,WritableVector3f {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;
	public float z;

	public Vector3f() {
	}

	public Vector3f(ReadableVector3f readableVector3f) {
		this.set(readableVector3f);
	}

	public Vector3f(float float1, float float2, float float3) {
		this.set(float1, float2, float3);
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

	public Vector3f set(ReadableVector3f readableVector3f) {
		this.x = readableVector3f.getX();
		this.y = readableVector3f.getY();
		this.z = readableVector3f.getZ();
		return this;
	}

	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	public Vector3f translate(float float1, float float2, float float3) {
		this.x += float1;
		this.y += float2;
		this.z += float3;
		return this;
	}

	public static Vector3f add(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
		if (vector3f3 == null) {
			return new Vector3f(vector3f.x + vector3f2.x, vector3f.y + vector3f2.y, vector3f.z + vector3f2.z);
		} else {
			vector3f3.set(vector3f.x + vector3f2.x, vector3f.y + vector3f2.y, vector3f.z + vector3f2.z);
			return vector3f3;
		}
	}

	public static Vector3f sub(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
		if (vector3f3 == null) {
			return new Vector3f(vector3f.x - vector3f2.x, vector3f.y - vector3f2.y, vector3f.z - vector3f2.z);
		} else {
			vector3f3.set(vector3f.x - vector3f2.x, vector3f.y - vector3f2.y, vector3f.z - vector3f2.z);
			return vector3f3;
		}
	}

	public static Vector3f cross(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
		if (vector3f3 == null) {
			vector3f3 = new Vector3f();
		}

		vector3f3.set(vector3f.y * vector3f2.z - vector3f.z * vector3f2.y, vector3f2.x * vector3f.z - vector3f2.z * vector3f.x, vector3f.x * vector3f2.y - vector3f.y * vector3f2.x);
		return vector3f3;
	}

	public Vector negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	public Vector3f negate(Vector3f vector3f) {
		if (vector3f == null) {
			vector3f = new Vector3f();
		}

		vector3f.x = -this.x;
		vector3f.y = -this.y;
		vector3f.z = -this.z;
		return vector3f;
	}

	public Vector3f normalise(Vector3f vector3f) {
		float float1 = this.length();
		if (vector3f == null) {
			vector3f = new Vector3f(this.x / float1, this.y / float1, this.z / float1);
		} else {
			vector3f.set(this.x / float1, this.y / float1, this.z / float1);
		}

		return vector3f;
	}

	public static float dot(Vector3f vector3f, Vector3f vector3f2) {
		return vector3f.x * vector3f2.x + vector3f.y * vector3f2.y + vector3f.z * vector3f2.z;
	}

	public static float angle(Vector3f vector3f, Vector3f vector3f2) {
		float float1 = dot(vector3f, vector3f2) / (vector3f.length() * vector3f2.length());
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
		return this;
	}

	public Vector scale(float float1) {
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		return this;
	}

	public Vector store(FloatBuffer floatBuffer) {
		floatBuffer.put(this.x);
		floatBuffer.put(this.y);
		floatBuffer.put(this.z);
		return this;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(64);
		stringBuilder.append("Vector3f[");
		stringBuilder.append(this.x);
		stringBuilder.append(", ");
		stringBuilder.append(this.y);
		stringBuilder.append(", ");
		stringBuilder.append(this.z);
		stringBuilder.append(']');
		return stringBuilder.toString();
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
}
