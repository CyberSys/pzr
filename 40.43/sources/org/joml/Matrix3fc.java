package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;


public interface Matrix3fc {

	float m00();

	float m01();

	float m02();

	float m10();

	float m11();

	float m12();

	float m20();

	float m21();

	float m22();

	Matrix3f mul(Matrix3fc matrix3fc, Matrix3f matrix3f);

	float determinant();

	Matrix3f invert(Matrix3f matrix3f);

	Matrix3f transpose(Matrix3f matrix3f);

	Matrix3f get(Matrix3f matrix3f);

	Matrix4f get(Matrix4f matrix4f);

	AxisAngle4f getRotation(AxisAngle4f axisAngle4f);

	Quaternionf getUnnormalizedRotation(Quaternionf quaternionf);

	Quaternionf getNormalizedRotation(Quaternionf quaternionf);

	Quaterniond getUnnormalizedRotation(Quaterniond quaterniond);

	Quaterniond getNormalizedRotation(Quaterniond quaterniond);

	FloatBuffer get(FloatBuffer floatBuffer);

	FloatBuffer get(int int1, FloatBuffer floatBuffer);

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	FloatBuffer getTransposed(FloatBuffer floatBuffer);

	FloatBuffer getTransposed(int int1, FloatBuffer floatBuffer);

	ByteBuffer getTransposed(ByteBuffer byteBuffer);

	ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer);

	float[] get(float[] floatArray, int int1);

	float[] get(float[] floatArray);

	Matrix3f scale(Vector3fc vector3fc, Matrix3f matrix3f);

	Matrix3f scale(float float1, float float2, float float3, Matrix3f matrix3f);

	Matrix3f scale(float float1, Matrix3f matrix3f);

	Matrix3f scaleLocal(float float1, float float2, float float3, Matrix3f matrix3f);

	Vector3f transform(Vector3f vector3f);

	Vector3f transform(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transform(float float1, float float2, float float3, Vector3f vector3f);

	Matrix3f rotateX(float float1, Matrix3f matrix3f);

	Matrix3f rotateY(float float1, Matrix3f matrix3f);

	Matrix3f rotateZ(float float1, Matrix3f matrix3f);

	Matrix3f rotateXYZ(float float1, float float2, float float3, Matrix3f matrix3f);

	Matrix3f rotateZYX(float float1, float float2, float float3, Matrix3f matrix3f);

	Matrix3f rotateYXZ(float float1, float float2, float float3, Matrix3f matrix3f);

	Matrix3f rotate(float float1, float float2, float float3, float float4, Matrix3f matrix3f);

	Matrix3f rotateLocal(float float1, float float2, float float3, float float4, Matrix3f matrix3f);

	Matrix3f rotate(Quaternionfc quaternionfc, Matrix3f matrix3f);

	Matrix3f rotateLocal(Quaternionfc quaternionfc, Matrix3f matrix3f);

	Matrix3f rotate(AxisAngle4f axisAngle4f, Matrix3f matrix3f);

	Matrix3f rotate(float float1, Vector3fc vector3fc, Matrix3f matrix3f);

	Matrix3f lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix3f matrix3f);

	Matrix3f lookAlong(float float1, float float2, float float3, float float4, float float5, float float6, Matrix3f matrix3f);

	Vector3f getRow(int int1, Vector3f vector3f) throws IndexOutOfBoundsException;

	Vector3f getColumn(int int1, Vector3f vector3f) throws IndexOutOfBoundsException;

	Matrix3f normal(Matrix3f matrix3f);

	Vector3f getScale(Vector3f vector3f);

	Vector3f positiveZ(Vector3f vector3f);

	Vector3f normalizedPositiveZ(Vector3f vector3f);

	Vector3f positiveX(Vector3f vector3f);

	Vector3f normalizedPositiveX(Vector3f vector3f);

	Vector3f positiveY(Vector3f vector3f);

	Vector3f normalizedPositiveY(Vector3f vector3f);

	Matrix3f add(Matrix3fc matrix3fc, Matrix3f matrix3f);

	Matrix3f sub(Matrix3fc matrix3fc, Matrix3f matrix3f);

	Matrix3f mulComponentWise(Matrix3fc matrix3fc, Matrix3f matrix3f);

	Matrix3f lerp(Matrix3fc matrix3fc, float float1, Matrix3f matrix3f);

	Matrix3f rotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix3f matrix3f);

	Matrix3f rotateTowards(float float1, float float2, float float3, float float4, float float5, float float6, Matrix3f matrix3f);

	Vector3f getEulerAnglesZYX(Vector3f vector3f);
}
