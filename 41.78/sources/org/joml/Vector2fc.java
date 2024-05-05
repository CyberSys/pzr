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

	Vector2fc getToAddress(long long1);

	Vector2f sub(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f sub(float float1, float float2, Vector2f vector2f);

	float dot(Vector2fc vector2fc);

	float angle(Vector2fc vector2fc);

	float lengthSquared();

	float length();

	float distance(Vector2fc vector2fc);

	float distanceSquared(Vector2fc vector2fc);

	float distance(float float1, float float2);

	float distanceSquared(float float1, float float2);

	Vector2f normalize(Vector2f vector2f);

	Vector2f normalize(float float1, Vector2f vector2f);

	Vector2f add(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f add(float float1, float float2, Vector2f vector2f);

	Vector2f negate(Vector2f vector2f);

	Vector2f mul(float float1, Vector2f vector2f);

	Vector2f mul(float float1, float float2, Vector2f vector2f);

	Vector2f mul(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f div(float float1, Vector2f vector2f);

	Vector2f div(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f div(float float1, float float2, Vector2f vector2f);

	Vector2f mul(Matrix2fc matrix2fc, Vector2f vector2f);

	Vector2f mul(Matrix2dc matrix2dc, Vector2f vector2f);

	Vector2f mulTranspose(Matrix2fc matrix2fc, Vector2f vector2f);

	Vector2f mulPosition(Matrix3x2fc matrix3x2fc, Vector2f vector2f);

	Vector2f mulDirection(Matrix3x2fc matrix3x2fc, Vector2f vector2f);

	Vector2f lerp(Vector2fc vector2fc, float float1, Vector2f vector2f);

	Vector2f fma(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2f vector2f);

	Vector2f fma(float float1, Vector2fc vector2fc, Vector2f vector2f);

	Vector2f min(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f max(Vector2fc vector2fc, Vector2f vector2f);

	int maxComponent();

	int minComponent();

	float get(int int1) throws IllegalArgumentException;

	Vector2i get(int int1, Vector2i vector2i);

	Vector2f get(Vector2f vector2f);

	Vector2d get(Vector2d vector2d);

	Vector2f floor(Vector2f vector2f);

	Vector2f ceil(Vector2f vector2f);

	Vector2f round(Vector2f vector2f);

	boolean isFinite();

	Vector2f absolute(Vector2f vector2f);

	boolean equals(Vector2fc vector2fc, float float1);

	boolean equals(float float1, float float2);
}
