package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;


public class Vector2f extends Vector implements Serializable,ReadableVector2f,WritableVector2f {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;

	public Vector2f() {
	}

	public Vector2f(ReadableVector2f readableVector2f) {
		this.set(readableVector2f);
	}

	public Vector2f(float float1, float float2) {
		this.set(float1, float2);
	}

	public void set(float float1, float float2) {
		this.x = float1;
		this.y = float2;
	}

	public Vector2f set(ReadableVector2f readableVector2f) {
		this.x = readableVector2f.getX();
		this.y = readableVector2f.getY();
		return this;
	}

	public float lengthSquared() {
		return this.x * this.x + this.y * this.y;
	}

	public Vector2f translate(float float1, float float2) {
		this.x += float1;
		this.y += float2;
		return this;
	}

	public Vector negate() {
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}

	public Vector2f negate(Vector2f vector2f) {
		if (vector2f == null) {
			vector2f = new Vector2f();
		}

		vector2f.x = -this.x;
		vector2f.y = -this.y;
		return vector2f;
	}

	public Vector2f normalise(Vector2f vector2f) {
		float float1 = this.length();
		if (vector2f == null) {
			vector2f = new Vector2f(this.x / float1, this.y / float1);
		} else {
			vector2f.set(this.x / float1, this.y / float1);
		}

		return vector2f;
	}

	public static float dot(Vector2f vector2f, Vector2f vector2f2) {
		return vector2f.x * vector2f2.x + vector2f.y * vector2f2.y;
	}

	public static float angle(Vector2f vector2f, Vector2f vector2f2) {
		float float1 = dot(vector2f, vector2f2) / (vector2f.length() * vector2f2.length());
		if (float1 < -1.0F) {
			float1 = -1.0F;
		} else if (float1 > 1.0F) {
			float1 = 1.0F;
		}

		return (float)Math.acos((double)float1);
	}

	public static Vector2f add(Vector2f vector2f, Vector2f vector2f2, Vector2f vector2f3) {
		if (vector2f3 == null) {
			return new Vector2f(vector2f.x + vector2f2.x, vector2f.y + vector2f2.y);
		} else {
			vector2f3.set(vector2f.x + vector2f2.x, vector2f.y + vector2f2.y);
			return vector2f3;
		}
	}

	public static Vector2f sub(Vector2f vector2f, Vector2f vector2f2, Vector2f vector2f3) {
		if (vector2f3 == null) {
			return new Vector2f(vector2f.x - vector2f2.x, vector2f.y - vector2f2.y);
		} else {
			vector2f3.set(vector2f.x - vector2f2.x, vector2f.y - vector2f2.y);
			return vector2f3;
		}
	}

	public Vector store(FloatBuffer floatBuffer) {
		floatBuffer.put(this.x);
		floatBuffer.put(this.y);
		return this;
	}

	public Vector load(FloatBuffer floatBuffer) {
		this.x = floatBuffer.get();
		this.y = floatBuffer.get();
		return this;
	}

	public Vector scale(float float1) {
		this.x *= float1;
		this.y *= float1;
		return this;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(64);
		stringBuilder.append("Vector2f[");
		stringBuilder.append(this.x);
		stringBuilder.append(", ");
		stringBuilder.append(this.y);
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
}
