package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;


public interface Matrix2fc {

	float m00();

	float m01();

	float m10();

	float m11();

	Matrix2f mul(Matrix2fc matrix2fc, Matrix2f matrix2f);

	Matrix2f mulLocal(Matrix2fc matrix2fc, Matrix2f matrix2f);

	float determinant();

	Matrix2f invert(Matrix2f matrix2f);

	Matrix2f transpose(Matrix2f matrix2f);

	Matrix2f get(Matrix2f matrix2f);

	Matrix3x2f get(Matrix3x2f matrix3x2f);

	Matrix3f get(Matrix3f matrix3f);

	float getRotation();

	FloatBuffer get(FloatBuffer floatBuffer);

	FloatBuffer get(int int1, FloatBuffer floatBuffer);

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	FloatBuffer getTransposed(FloatBuffer floatBuffer);

	FloatBuffer getTransposed(int int1, FloatBuffer floatBuffer);

	ByteBuffer getTransposed(ByteBuffer byteBuffer);

	ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer);

	Matrix2fc getToAddress(long long1);

	float[] get(float[] floatArray, int int1);

	float[] get(float[] floatArray);

	Matrix2f scale(Vector2fc vector2fc, Matrix2f matrix2f);

	Matrix2f scale(float float1, float float2, Matrix2f matrix2f);

	Matrix2f scale(float float1, Matrix2f matrix2f);

	Matrix2f scaleLocal(float float1, float float2, Matrix2f matrix2f);

	Vector2f transform(Vector2f vector2f);

	Vector2f transform(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f transform(float float1, float float2, Vector2f vector2f);

	Vector2f transformTranspose(Vector2f vector2f);

	Vector2f transformTranspose(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f transformTranspose(float float1, float float2, Vector2f vector2f);

	Matrix2f rotate(float float1, Matrix2f matrix2f);

	Matrix2f rotateLocal(float float1, Matrix2f matrix2f);

	Vector2f getRow(int int1, Vector2f vector2f) throws IndexOutOfBoundsException;

	Vector2f getColumn(int int1, Vector2f vector2f) throws IndexOutOfBoundsException;

	float get(int int1, int int2);

	Matrix2f normal(Matrix2f matrix2f);

	Vector2f getScale(Vector2f vector2f);

	Vector2f positiveX(Vector2f vector2f);

	Vector2f normalizedPositiveX(Vector2f vector2f);

	Vector2f positiveY(Vector2f vector2f);

	Vector2f normalizedPositiveY(Vector2f vector2f);

	Matrix2f add(Matrix2fc matrix2fc, Matrix2f matrix2f);

	Matrix2f sub(Matrix2fc matrix2fc, Matrix2f matrix2f);

	Matrix2f mulComponentWise(Matrix2fc matrix2fc, Matrix2f matrix2f);

	Matrix2f lerp(Matrix2fc matrix2fc, float float1, Matrix2f matrix2f);

	boolean equals(Matrix2fc matrix2fc, float float1);

	boolean isFinite();
}
