package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;


public interface Vector2fc {

	float x();

	float y();

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	FloatBuffer get(FloatBuffer floatBuffer);

	FloatBuffer get(int int1, FloatBuffer floatBuffer);

	Vector2f sub(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f sub(float float1, float float2, Vector2f vector2f);

	float dot(Vector2fc vector2fc);

	float angle(Vector2fc vector2fc);

	float length();

	float lengthSquared();

	float distance(Vector2fc vector2fc);

	float distanceSquared(Vector2fc vector2fc);

	float distance(float float1, float float2);

	float distanceSquared(float float1, float float2);

	Vector2f normalize(Vector2f vector2f);

	Vector2f add(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f add(float float1, float float2, Vector2f vector2f);

	Vector2f negate(Vector2f vector2f);

	Vector2f mul(float float1, Vector2f vector2f);

	Vector2f mul(float float1, float float2, Vector2f vector2f);

	Vector2f mul(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f lerp(Vector2fc vector2fc, float float1, Vector2f vector2f);

	Vector2f fma(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2f vector2f);

	Vector2f fma(float float1, Vector2fc vector2fc, Vector2f vector2f);
}
