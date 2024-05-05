package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;


public interface Matrix3x2fc {

	float m00();

	float m01();

	float m10();

	float m11();

	float m20();

	float m21();

	Matrix3x2f mul(Matrix3x2fc matrix3x2fc, Matrix3x2f matrix3x2f);

	Matrix3x2f mulLocal(Matrix3x2fc matrix3x2fc, Matrix3x2f matrix3x2f);

	float determinant();

	Matrix3x2f invert(Matrix3x2f matrix3x2f);

	Matrix3x2f translate(float float1, float float2, Matrix3x2f matrix3x2f);

	Matrix3x2f translate(Vector2fc vector2fc, Matrix3x2f matrix3x2f);

	Matrix3x2f translateLocal(Vector2fc vector2fc, Matrix3x2f matrix3x2f);

	Matrix3x2f translateLocal(float float1, float float2, Matrix3x2f matrix3x2f);

	Matrix3x2f get(Matrix3x2f matrix3x2f);

	FloatBuffer get(FloatBuffer floatBuffer);

	FloatBuffer get(int int1, FloatBuffer floatBuffer);

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	FloatBuffer get3x3(FloatBuffer floatBuffer);

	FloatBuffer get3x3(int int1, FloatBuffer floatBuffer);

	ByteBuffer get3x3(ByteBuffer byteBuffer);

	ByteBuffer get3x3(int int1, ByteBuffer byteBuffer);

	FloatBuffer get4x4(FloatBuffer floatBuffer);

	FloatBuffer get4x4(int int1, FloatBuffer floatBuffer);

	ByteBuffer get4x4(ByteBuffer byteBuffer);

	ByteBuffer get4x4(int int1, ByteBuffer byteBuffer);

	Matrix3x2fc getToAddress(long long1);

	float[] get(float[] floatArray, int int1);

	float[] get(float[] floatArray);

	float[] get3x3(float[] floatArray, int int1);

	float[] get3x3(float[] floatArray);

	float[] get4x4(float[] floatArray, int int1);

	float[] get4x4(float[] floatArray);

	Matrix3x2f scale(float float1, float float2, Matrix3x2f matrix3x2f);

	Matrix3x2f scale(Vector2fc vector2fc, Matrix3x2f matrix3x2f);

	Matrix3x2f scaleAroundLocal(float float1, float float2, float float3, float float4, Matrix3x2f matrix3x2f);

	Matrix3x2f scaleAroundLocal(float float1, float float2, float float3, Matrix3x2f matrix3x2f);

	Matrix3x2f scale(float float1, Matrix3x2f matrix3x2f);

	Matrix3x2f scaleLocal(float float1, Matrix3x2f matrix3x2f);

	Matrix3x2f scaleLocal(float float1, float float2, Matrix3x2f matrix3x2f);

	Matrix3x2f scaleAround(float float1, float float2, float float3, float float4, Matrix3x2f matrix3x2f);

	Matrix3x2f scaleAround(float float1, float float2, float float3, Matrix3x2f matrix3x2f);

	Vector3f transform(Vector3f vector3f);

	Vector3f transform(Vector3f vector3f, Vector3f vector3f2);

	Vector3f transform(float float1, float float2, float float3, Vector3f vector3f);

	Vector2f transformPosition(Vector2f vector2f);

	Vector2f transformPosition(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f transformPosition(float float1, float float2, Vector2f vector2f);

	Vector2f transformDirection(Vector2f vector2f);

	Vector2f transformDirection(Vector2fc vector2fc, Vector2f vector2f);

	Vector2f transformDirection(float float1, float float2, Vector2f vector2f);

	Matrix3x2f rotate(float float1, Matrix3x2f matrix3x2f);

	Matrix3x2f rotateLocal(float float1, Matrix3x2f matrix3x2f);

	Matrix3x2f rotateAbout(float float1, float float2, float float3, Matrix3x2f matrix3x2f);

	Matrix3x2f rotateTo(Vector2fc vector2fc, Vector2fc vector2fc2, Matrix3x2f matrix3x2f);

	Matrix3x2f view(float float1, float float2, float float3, float float4, Matrix3x2f matrix3x2f);

	Vector2f origin(Vector2f vector2f);

	float[] viewArea(float[] floatArray);

	Vector2f positiveX(Vector2f vector2f);

	Vector2f normalizedPositiveX(Vector2f vector2f);

	Vector2f positiveY(Vector2f vector2f);

	Vector2f normalizedPositiveY(Vector2f vector2f);

	Vector2f unproject(float float1, float float2, int[] intArray, Vector2f vector2f);

	Vector2f unprojectInv(float float1, float float2, int[] intArray, Vector2f vector2f);

	boolean testPoint(float float1, float float2);

	boolean testCircle(float float1, float float2, float float3);

	boolean testAar(float float1, float float2, float float3, float float4);

	boolean equals(Matrix3x2fc matrix3x2fc, float float1);

	boolean isFinite();
}
