package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;


public interface Matrix4fc {
	int PLANE_NX = 0;
	int PLANE_PX = 1;
	int PLANE_NY = 2;
	int PLANE_PY = 3;
	int PLANE_NZ = 4;
	int PLANE_PZ = 5;
	int CORNER_NXNYNZ = 0;
	int CORNER_PXNYNZ = 1;
	int CORNER_PXPYNZ = 2;
	int CORNER_NXPYNZ = 3;
	int CORNER_PXNYPZ = 4;
	int CORNER_NXNYPZ = 5;
	int CORNER_NXPYPZ = 6;
	int CORNER_PXPYPZ = 7;
	byte PROPERTY_PERSPECTIVE = 1;
	byte PROPERTY_AFFINE = 2;
	byte PROPERTY_IDENTITY = 4;
	byte PROPERTY_TRANSLATION = 8;

	byte properties();

	float m00();

	float m01();

	float m02();

	float m03();

	float m10();

	float m11();

	float m12();

	float m13();

	float m20();

	float m21();

	float m22();

	float m23();

	float m30();

	float m31();

	float m32();

	float m33();

	Matrix4f mul(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f mul(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f);

	Matrix4f mulPerspectiveAffine(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f mulPerspectiveAffine(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f);

	Matrix4f mulAffineR(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f mulAffineR(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f);

	Matrix4f mulAffine(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f mulTranslationAffine(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f mulOrthoAffine(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f fma4x3(Matrix4fc matrix4fc, float float1, Matrix4f matrix4f);

	Matrix4f add(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f sub(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f mulComponentWise(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f add4x3(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f sub4x3(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f mul4x3ComponentWise(Matrix4fc matrix4fc, Matrix4f matrix4f);

	float determinant();

	float determinant3x3();

	float determinantAffine();

	Matrix4f invert(Matrix4f matrix4f);

	Matrix4f invertPerspective(Matrix4f matrix4f);

	Matrix4f invertFrustum(Matrix4f matrix4f);

	Matrix4f invertOrtho(Matrix4f matrix4f);

	Matrix4f invertPerspectiveView(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f invertPerspectiveView(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f);

	Matrix4f invertAffine(Matrix4f matrix4f);

	Matrix4f invertAffineUnitScale(Matrix4f matrix4f);

	Matrix4f invertLookAt(Matrix4f matrix4f);

	Matrix4f transpose(Matrix4f matrix4f);

	Matrix4f transpose3x3(Matrix4f matrix4f);

	Matrix3f transpose3x3(Matrix3f matrix3f);

	Vector3f getTranslation(Vector3f vector3f);

	Vector3f getScale(Vector3f vector3f);

	Matrix4f get(Matrix4f matrix4f);

	Matrix4x3f get4x3(Matrix4x3f matrix4x3f);

	Matrix4d get(Matrix4d matrix4d);

	Matrix3f get3x3(Matrix3f matrix3f);

	Matrix3d get3x3(Matrix3d matrix3d);

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

	FloatBuffer getTransposed(FloatBuffer floatBuffer);

	FloatBuffer getTransposed(int int1, FloatBuffer floatBuffer);

	ByteBuffer getTransposed(ByteBuffer byteBuffer);

	ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer);

	FloatBuffer get4x3Transposed(FloatBuffer floatBuffer);

	FloatBuffer get4x3Transposed(int int1, FloatBuffer floatBuffer);

	ByteBuffer get4x3Transposed(ByteBuffer byteBuffer);

	ByteBuffer get4x3Transposed(int int1, ByteBuffer byteBuffer);

	float[] get(float[] floatArray, int int1);

	float[] get(float[] floatArray);

	Vector4f transform(Vector4f vector4f);

	Vector4f transform(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f transform(float float1, float float2, float float3, float float4, Vector4f vector4f);

	Vector4f transformProject(Vector4f vector4f);

	Vector4f transformProject(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f transformProject(float float1, float float2, float float3, float float4, Vector4f vector4f);

	Vector3f transformProject(Vector3f vector3f);

	Vector3f transformProject(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transformProject(float float1, float float2, float float3, Vector3f vector3f);

	Vector3f transformPosition(Vector3f vector3f);

	Vector3f transformPosition(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transformPosition(float float1, float float2, float float3, Vector3f vector3f);

	Vector3f transformDirection(Vector3f vector3f);

	Vector3f transformDirection(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transformDirection(float float1, float float2, float float3, Vector3f vector3f);

	Vector4f transformAffine(Vector4f vector4f);

	Vector4f transformAffine(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f transformAffine(float float1, float float2, float float3, float float4, Vector4f vector4f);

	Matrix4f scale(Vector3fc vector3fc, Matrix4f matrix4f);

	Matrix4f scale(float float1, Matrix4f matrix4f);

	Matrix4f scale(float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f scaleAround(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f);

	Matrix4f scaleAround(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f scaleLocal(float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f scaleAroundLocal(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f);

	Matrix4f scaleAroundLocal(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f rotateX(float float1, Matrix4f matrix4f);

	Matrix4f rotateY(float float1, Matrix4f matrix4f);

	Matrix4f rotateZ(float float1, Matrix4f matrix4f);

	Matrix4f rotateXYZ(float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f rotateAffineXYZ(float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f rotateZYX(float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f rotateAffineZYX(float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f rotateYXZ(float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f rotateAffineYXZ(float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f rotate(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f rotateTranslation(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f rotateAffine(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f rotateLocal(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f translate(Vector3fc vector3fc, Matrix4f matrix4f);

	Matrix4f translate(float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f translateLocal(Vector3fc vector3fc, Matrix4f matrix4f);

	Matrix4f translateLocal(float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f ortho(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f);

	Matrix4f ortho(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f);

	Matrix4f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f);

	Matrix4f orthoLH(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f);

	Matrix4f orthoSymmetric(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f);

	Matrix4f orthoSymmetric(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f orthoSymmetricLH(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f);

	Matrix4f orthoSymmetricLH(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f ortho2D(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f ortho2DLH(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4f matrix4f);

	Matrix4f lookAlong(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f);

	Matrix4f lookAt(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Matrix4f matrix4f);

	Matrix4f lookAt(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f);

	Matrix4f lookAtPerspective(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f);

	Matrix4f lookAtLH(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Matrix4f matrix4f);

	Matrix4f lookAtLH(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f);

	Matrix4f lookAtPerspectiveLH(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Matrix4f matrix4f);

	Matrix4f perspective(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f);

	Matrix4f perspective(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f perspectiveLH(float float1, float float2, float float3, float float4, boolean boolean1, Matrix4f matrix4f);

	Matrix4f perspectiveLH(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f frustum(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f);

	Matrix4f frustum(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f);

	Matrix4f frustumLH(float float1, float float2, float float3, float float4, float float5, float float6, boolean boolean1, Matrix4f matrix4f);

	Matrix4f frustumLH(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f);

	Matrix4f rotate(Quaternionfc quaternionfc, Matrix4f matrix4f);

	Matrix4f rotateAffine(Quaternionfc quaternionfc, Matrix4f matrix4f);

	Matrix4f rotateTranslation(Quaternionfc quaternionfc, Matrix4f matrix4f);

	Matrix4f rotateAround(Quaternionfc quaternionfc, float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f rotateLocal(Quaternionfc quaternionfc, Matrix4f matrix4f);

	Matrix4f rotateAroundLocal(Quaternionfc quaternionfc, float float1, float float2, float float3, Matrix4f matrix4f);

	Matrix4f rotate(AxisAngle4f axisAngle4f, Matrix4f matrix4f);

	Matrix4f rotate(float float1, Vector3fc vector3fc, Matrix4f matrix4f);

	Vector4f unproject(float float1, float float2, float float3, int[] intArray, Vector4f vector4f);

	Vector3f unproject(float float1, float float2, float float3, int[] intArray, Vector3f vector3f);

	Vector4f unproject(Vector3fc vector3fc, int[] intArray, Vector4f vector4f);

	Vector3f unproject(Vector3fc vector3fc, int[] intArray, Vector3f vector3f);

	Matrix4f unprojectRay(float float1, float float2, int[] intArray, Vector3f vector3f, Vector3f vector3f2);

	Matrix4f unprojectRay(Vector2fc vector2fc, int[] intArray, Vector3f vector3f, Vector3f vector3f2);

	Vector4f unprojectInv(Vector3fc vector3fc, int[] intArray, Vector4f vector4f);

	Vector4f unprojectInv(float float1, float float2, float float3, int[] intArray, Vector4f vector4f);

	Matrix4f unprojectInvRay(Vector2fc vector2fc, int[] intArray, Vector3f vector3f, Vector3f vector3f2);

	Matrix4f unprojectInvRay(float float1, float float2, int[] intArray, Vector3f vector3f, Vector3f vector3f2);

	Vector3f unprojectInv(Vector3fc vector3fc, int[] intArray, Vector3f vector3f);

	Vector3f unprojectInv(float float1, float float2, float float3, int[] intArray, Vector3f vector3f);

	Vector4f project(float float1, float float2, float float3, int[] intArray, Vector4f vector4f);

	Vector3f project(float float1, float float2, float float3, int[] intArray, Vector3f vector3f);

	Vector4f project(Vector3fc vector3fc, int[] intArray, Vector4f vector4f);

	Vector3f project(Vector3fc vector3fc, int[] intArray, Vector3f vector3f);

	Matrix4f reflect(float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f reflect(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f);

	Matrix4f reflect(Quaternionfc quaternionfc, Vector3fc vector3fc, Matrix4f matrix4f);

	Matrix4f reflect(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4f matrix4f);

	Vector4f getRow(int int1, Vector4f vector4f) throws IndexOutOfBoundsException;

	Vector4f getColumn(int int1, Vector4f vector4f) throws IndexOutOfBoundsException;

	Matrix4f normal(Matrix4f matrix4f);

	Matrix3f normal(Matrix3f matrix3f);

	Matrix4f normalize3x3(Matrix4f matrix4f);

	Matrix3f normalize3x3(Matrix3f matrix3f);

	Vector4f frustumPlane(int int1, Vector4f vector4f);

	Vector3f frustumCorner(int int1, Vector3f vector3f);

	Vector3f perspectiveOrigin(Vector3f vector3f);

	float perspectiveFov();

	float perspectiveNear();

	float perspectiveFar();

	Vector3f frustumRayDir(float float1, float float2, Vector3f vector3f);

	Vector3f positiveZ(Vector3f vector3f);

	Vector3f normalizedPositiveZ(Vector3f vector3f);

	Vector3f positiveX(Vector3f vector3f);

	Vector3f normalizedPositiveX(Vector3f vector3f);

	Vector3f positiveY(Vector3f vector3f);

	Vector3f normalizedPositiveY(Vector3f vector3f);

	Vector3f originAffine(Vector3f vector3f);

	Vector3f origin(Vector3f vector3f);

	Matrix4f shadow(Vector4f vector4f, float float1, float float2, float float3, float float4, Matrix4f matrix4f);

	Matrix4f shadow(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Matrix4f matrix4f);

	Matrix4f shadow(Vector4f vector4f, Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f shadow(float float1, float float2, float float3, float float4, Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f pick(float float1, float float2, float float3, float float4, int[] intArray, Matrix4f matrix4f);

	boolean isAffine();

	Matrix4f arcball(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f);

	Matrix4f arcball(float float1, Vector3fc vector3fc, float float2, float float3, Matrix4f matrix4f);

	Matrix4f frustumAabb(Vector3f vector3f, Vector3f vector3f2);

	Matrix4f projectedGridRange(Matrix4fc matrix4fc, float float1, float float2, Matrix4f matrix4f);

	Matrix4f perspectiveFrustumSlice(float float1, float float2, Matrix4f matrix4f);

	Matrix4f orthoCrop(Matrix4fc matrix4fc, Matrix4f matrix4f);

	Matrix4f transformAab(float float1, float float2, float float3, float float4, float float5, float float6, Vector3f vector3f, Vector3f vector3f2);

	Matrix4f transformAab(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3f vector3f, Vector3f vector3f2);

	Matrix4f lerp(Matrix4fc matrix4fc, float float1, Matrix4f matrix4f);

	Matrix4f rotateTowards(Vector3fc vector3fc, Vector3fc vector3fc2, Matrix4f matrix4f);

	Matrix4f rotateTowards(float float1, float float2, float float3, float float4, float float5, float float6, Matrix4f matrix4f);

	Vector3f getEulerAnglesZYX(Vector3f vector3f);
}
