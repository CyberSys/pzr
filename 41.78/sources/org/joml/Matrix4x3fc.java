package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;


public interface Matrix4x3fc {
	int PLANE_NX = 0;
	int PLANE_PX = 1;
	int PLANE_NY = 2;
	int PLANE_PY = 3;
	int PLANE_NZ = 4;
	int PLANE_PZ = 5;
	byte PROPERTY_IDENTITY = 4;
	byte PROPERTY_TRANSLATION = 8;
	byte PROPERTY_ORTHONORMAL = 16;

	int properties();

	float m00();

	float m01();

	float m02();

	float m10();

	float m11();

	float m12();

	float m20();

	float m21();

	float m22();

	float m30();

	float m31();

	float m32();

	Matrix4f get(Matrix4f matrix4f);

	Matrix4d get(Matrix4d matrix4d);

	Matrix4x3f mul(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f mulTranslation(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f mulOrtho(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f fma(Matrix4x3fc matrix4x3fc, float float1, Matrix4x3f matrix4x3f);

	Matrix4x3f add(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f sub(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f mulComponentWise(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f);

	float determinant();

	Matrix4x3f invert(Matrix4x3f matrix4x3f);

	Matrix4f invert(Matrix4f matrix4f);

	Matrix4x3f invertOrtho(Matrix4x3f matrix4x3f);

	Matrix4x3f transpose3x3(Matrix4x3f matrix4x3f);

	Matrix3f transpose3x3(Matrix3f matrix3f);

	Vector3f getTranslation(Vector3f vector3f);

	Vector3f getScale(Vector3f vector3f);

	Matrix4x3f get(Matrix4x3f matrix4x3f);

	Matrix4x3d get(Matrix4x3d matrix4x3d);

	AxisAngle4f getRotation(AxisAngle4f axisAngle4f);

	AxisAngle4d getRotation(AxisAngle4d axisAngle4d);

	Quaternionf getUnnormalizedRotation(Quaternionf quaternionf);

	Quaternionf getNormalizedRotation(Quaternionf quaternionf);

	Quaterniond getUnnormalizedRotation(Quaterniond quaterniond);

	Quaterniond getNormalizedRotation(Quaterniond quaterniond);

	FloatBuffer get(FloatBuffer floatBuffer);

	FloatBuffer get(int int1, FloatBuffer floatBuffer);

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	Matrix4x3fc getToAddress(long long1);

	float[] get(float[] floatArray, int int1);

	float[] get(float[] floatArray);

	float[] get4x4(float[] floatArray, int int1);

	float[] get4x4(float[] floatArray);

	FloatBuffer get4x4(FloatBuffer floatBuffer);

	FloatBuffer get4x4(int int1, FloatBuffer floatBuffer);

	ByteBuffer get4x4(ByteBuffer byteBuffer);

	ByteBuffer get4x4(int int1, ByteBuffer byteBuffer);

	FloatBuffer get3x4(FloatBuffer floatBuffer);

	FloatBuffer get3x4(int int1, FloatBuffer floatBuffer);

	ByteBuffer get3x4(ByteBuffer byteBuffer);

	ByteBuffer get3x4(int int1, ByteBuffer byteBuffer);

	FloatBuffer getTransposed(FloatBuffer floatBuffer);

	FloatBuffer getTransposed(int int1, FloatBuffer floatBuffer);

	ByteBuffer getTransposed(ByteBuffer byteBuffer);

	ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer);

	float[] getTransposed(float[] floatArray, int int1);

	float[] getTransposed(float[] floatArray);

	Vector4f transform(Vector4f vector4f);

	Vector4f transform(Vector4fc vector4fc, Vector4f vector4f);

	Vector3f transformPosition(Vector3f vector3f);

	Vector3f transformPosition(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transformDirection(Vector3f vector3f);

	Vector3f transformDirection(Vector3fc vector3fc, Vector3f vector3f);

	Matrix4x3f scale(Vector3fc vector3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f scale(float float1, Matrix4x3f matrix4x3f);

	Matrix4x3f scaleXY(float float1, float float2, Matrix4x3f matrix4x3f);

	Matrix4x3f scale(float float1, float float2, float float3, Matrix4x3f matrix4x3f);

	Matrix4x3f scaleLocal(float float1, float float2, float float3, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateX(float float1, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateY(float float1, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateZ(float float1, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateXYZ(float float1, float float2, float float3, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateZYX(float float1, float float2, float float3, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateYXZ(float float1, float float2, float float3, Matrix4x3f matrix4x3f);

	Matrix4x3f rotate(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateTranslation(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateAround(Quaternionfc quaternionfc, float float1, float float2, float float3, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateLocal(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f);

	Matrix4x3f translate(Vector3fc vector3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f translate(float float1, float float2, float float3, Matrix4x3f matrix4x3f);

	Matrix4x3f translateLocal(Vector3fc vector3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f translateLocal(float float1, float float2, float float3, Matrix4x3f matrix4x3f);

	Matrix4x3f ortho(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4x3f matrix4x3f);

	Matrix4x3f ortho(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f);

	Matrix4x3f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4x3f matrix4x3f);

	Matrix4x3f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f);

	Matrix4x3f orthoSymmetric(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4x3f matrix4x3f);

	Matrix4x3f orthoSymmetric(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f);

	Matrix4x3f orthoSymmetricLH(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4x3f matrix4x3f);

	Matrix4x3f orthoSymmetricLH(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f);

	Matrix4x3f ortho2D(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f);

	Matrix4x3f ortho2DLH(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f);

	Matrix4x3f lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4x3f matrix4x3f);

	Matrix4x3f lookAlong(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f);

	Matrix4x3f lookAt(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Matrix4x3f matrix4x3f);

	Matrix4x3f lookAt(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4x3f matrix4x3f);

	Matrix4x3f lookAtLH(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Matrix4x3f matrix4x3f);

	Matrix4x3f lookAtLH(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4x3f matrix4x3f);

	Matrix4x3f rotate(Quaternionfc quaternionfc, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateTranslation(Quaternionfc quaternionfc, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateLocal(Quaternionfc quaternionfc, Matrix4x3f matrix4x3f);

	Matrix4x3f rotate(AxisAngle4f axisAngle4f, Matrix4x3f matrix4x3f);

	Matrix4x3f rotate(float float1, Vector3fc vector3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f reflect(float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f);

	Matrix4x3f reflect(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f);

	Matrix4x3f reflect(Quaternionfc quaternionfc, Vector3fc vector3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f reflect(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4x3f matrix4x3f);

	Vector4f getRow(int int1, Vector4f vector4f) throws IndexOutOfBoundsException;

	Vector3f getColumn(int int1, Vector3f vector3f) throws IndexOutOfBoundsException;

	Matrix4x3f normal(Matrix4x3f matrix4x3f);

	Matrix3f normal(Matrix3f matrix3f);

	Matrix3f cofactor3x3(Matrix3f matrix3f);

	Matrix4x3f cofactor3x3(Matrix4x3f matrix4x3f);

	Matrix4x3f normalize3x3(Matrix4x3f matrix4x3f);

	Matrix3f normalize3x3(Matrix3f matrix3f);

	Vector4f frustumPlane(int int1, Vector4f vector4f);

	Vector3f positiveZ(Vector3f vector3f);

	Vector3f normalizedPositiveZ(Vector3f vector3f);

	Vector3f positiveX(Vector3f vector3f);

	Vector3f normalizedPositiveX(Vector3f vector3f);

	Vector3f positiveY(Vector3f vector3f);

	Vector3f normalizedPositiveY(Vector3f vector3f);

	Vector3f origin(Vector3f vector3f);

	Matrix4x3f shadow(Vector4fc vector4fc, float float1, float float2, float float3, float float4, Matrix4x3f matrix4x3f);

	Matrix4x3f shadow(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Matrix4x3f matrix4x3f);

	Matrix4x3f shadow(Vector4fc vector4fc, Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f shadow(float float1, float float2, float float3, float float4, Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f pick(float float1, float float2, float float3, float float4, int[] intArray, Matrix4x3f matrix4x3f);

	Matrix4x3f arcball(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f);

	Matrix4x3f arcball(float float1, Vector3fc vector3fc, float float2, float float3, Matrix4x3f matrix4x3f);

	Matrix4x3f transformAab(float float1, float float2, float float3, float float4, float float5, float float6, Vector3f vector3f, Vector3f vector3f2);

	Matrix4x3f transformAab(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3f vector3f, Vector3f vector3f2);

	Matrix4x3f lerp(Matrix4x3fc matrix4x3fc, float float1, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4x3f matrix4x3f);

	Matrix4x3f rotateTowards(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4x3f matrix4x3f);

	Vector3f getEulerAnglesZYX(Vector3f vector3f);

	Matrix4x3f obliqueZ(float float1, float float2, Matrix4x3f matrix4x3f);

	Matrix4x3f withLookAtUp(Vector3fc vector3fc, Matrix4x3f matrix4x3f);

	Matrix4x3f withLookAtUp(float float1, float float2, float float3, Matrix4x3f matrix4x3f);

	boolean equals(Matrix4x3fc matrix4x3fc, float float1);

	boolean isFinite();
}
