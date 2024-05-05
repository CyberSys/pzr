package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;


public interface Quaternionfc {

	float x();

	float y();

	float z();

	float w();

	Quaternionf normalize(Quaternionf quaternionf);

	Quaternionf add(float float1, float float2, float float3, float float4, Quaternionf quaternionf);

	Quaternionf add(Quaternionfc quaternionfc, Quaternionf quaternionf);

	float angle();

	Matrix3f get(Matrix3f matrix3f);

	Matrix3d get(Matrix3d matrix3d);

	Matrix4f get(Matrix4f matrix4f);

	Matrix4d get(Matrix4d matrix4d);

	Matrix4x3f get(Matrix4x3f matrix4x3f);

	Matrix4x3d get(Matrix4x3d matrix4x3d);

	AxisAngle4f get(AxisAngle4f axisAngle4f);

	AxisAngle4d get(AxisAngle4d axisAngle4d);

	Quaterniond get(Quaterniond quaterniond);

	Quaternionf get(Quaternionf quaternionf);

	ByteBuffer getAsMatrix3f(ByteBuffer byteBuffer);

	FloatBuffer getAsMatrix3f(FloatBuffer floatBuffer);

	ByteBuffer getAsMatrix4f(ByteBuffer byteBuffer);

	FloatBuffer getAsMatrix4f(FloatBuffer floatBuffer);

	ByteBuffer getAsMatrix4x3f(ByteBuffer byteBuffer);

	FloatBuffer getAsMatrix4x3f(FloatBuffer floatBuffer);

	Quaternionf mul(Quaternionfc quaternionfc, Quaternionf quaternionf);

	Quaternionf mul(float float1, float float2, float float3, float float4, Quaternionf quaternionf);

	Quaternionf premul(Quaternionfc quaternionfc, Quaternionf quaternionf);

	Quaternionf premul(float float1, float float2, float float3, float float4, Quaternionf quaternionf);

	Vector3f transform(Vector3f vector3f);

	Vector3f transformInverse(Vector3f vector3f);

	Vector3f transformUnit(Vector3f vector3f);

	Vector3f transformPositiveX(Vector3f vector3f);

	Vector4f transformPositiveX(Vector4f vector4f);

	Vector3f transformUnitPositiveX(Vector3f vector3f);

	Vector4f transformUnitPositiveX(Vector4f vector4f);

	Vector3f transformPositiveY(Vector3f vector3f);

	Vector4f transformPositiveY(Vector4f vector4f);

	Vector3f transformUnitPositiveY(Vector3f vector3f);

	Vector4f transformUnitPositiveY(Vector4f vector4f);

	Vector3f transformPositiveZ(Vector3f vector3f);

	Vector4f transformPositiveZ(Vector4f vector4f);

	Vector3f transformUnitPositiveZ(Vector3f vector3f);

	Vector4f transformUnitPositiveZ(Vector4f vector4f);

	Vector4f transform(Vector4f vector4f);

	Vector4f transformInverse(Vector4f vector4f);

	Vector3f transform(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transformInverse(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transform(float float1, float float2, float float3, Vector3f vector3f);

	Vector3d transform(float float1, float float2, float float3, Vector3d vector3d);

	Vector3f transformInverse(float float1, float float2, float float3, Vector3f vector3f);

	Vector3d transformInverse(float float1, float float2, float float3, Vector3d vector3d);

	Vector3f transformInverseUnit(Vector3f vector3f);

	Vector3f transformUnit(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transformInverseUnit(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transformUnit(float float1, float float2, float float3, Vector3f vector3f);

	Vector3d transformUnit(float float1, float float2, float float3, Vector3d vector3d);

	Vector3f transformInverseUnit(float float1, float float2, float float3, Vector3f vector3f);

	Vector3d transformInverseUnit(float float1, float float2, float float3, Vector3d vector3d);

	Vector4f transform(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f transformInverse(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f transform(float float1, float float2, float float3, Vector4f vector4f);

	Vector4f transformInverse(float float1, float float2, float float3, Vector4f vector4f);

	Vector4f transformUnit(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f transformUnit(Vector4f vector4f);

	Vector4f transformInverseUnit(Vector4f vector4f);

	Vector4f transformInverseUnit(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f transformUnit(float float1, float float2, float float3, Vector4f vector4f);

	Vector4f transformInverseUnit(float float1, float float2, float float3, Vector4f vector4f);

	Vector3d transform(Vector3d vector3d);

	Vector3d transformInverse(Vector3d vector3d);

	Vector3d transformPositiveX(Vector3d vector3d);

	Vector4d transformPositiveX(Vector4d vector4d);

	Vector3d transformUnitPositiveX(Vector3d vector3d);

	Vector4d transformUnitPositiveX(Vector4d vector4d);

	Vector3d transformPositiveY(Vector3d vector3d);

	Vector4d transformPositiveY(Vector4d vector4d);

	Vector3d transformUnitPositiveY(Vector3d vector3d);

	Vector4d transformUnitPositiveY(Vector4d vector4d);

	Vector3d transformPositiveZ(Vector3d vector3d);

	Vector4d transformPositiveZ(Vector4d vector4d);

	Vector3d transformUnitPositiveZ(Vector3d vector3d);

	Vector4d transformUnitPositiveZ(Vector4d vector4d);

	Vector4d transform(Vector4d vector4d);

	Vector4d transformInverse(Vector4d vector4d);

	Vector3d transform(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transformInverse(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transform(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d transformInverse(double double1, double double2, double double3, Vector3d vector3d);

	Vector4d transform(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transformInverse(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transform(double double1, double double2, double double3, Vector4d vector4d);

	Vector4d transformInverse(double double1, double double2, double double3, Vector4d vector4d);

	Vector4d transformUnit(Vector4d vector4d);

	Vector4d transformInverseUnit(Vector4d vector4d);

	Vector3d transformUnit(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transformInverseUnit(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transformUnit(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d transformInverseUnit(double double1, double double2, double double3, Vector3d vector3d);

	Vector4d transformUnit(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transformInverseUnit(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transformUnit(double double1, double double2, double double3, Vector4d vector4d);

	Vector4d transformInverseUnit(double double1, double double2, double double3, Vector4d vector4d);

	Quaternionf invert(Quaternionf quaternionf);

	Quaternionf div(Quaternionfc quaternionfc, Quaternionf quaternionf);

	Quaternionf conjugate(Quaternionf quaternionf);

	Quaternionf rotateXYZ(float float1, float float2, float float3, Quaternionf quaternionf);

	Quaternionf rotateZYX(float float1, float float2, float float3, Quaternionf quaternionf);

	Quaternionf rotateYXZ(float float1, float float2, float float3, Quaternionf quaternionf);

	Vector3f getEulerAnglesXYZ(Vector3f vector3f);

	float lengthSquared();

	Quaternionf slerp(Quaternionfc quaternionfc, float float1, Quaternionf quaternionf);

	Quaternionf scale(float float1, Quaternionf quaternionf);

	Quaternionf integrate(float float1, float float2, float float3, float float4, Quaternionf quaternionf);

	Quaternionf nlerp(Quaternionfc quaternionfc, float float1, Quaternionf quaternionf);

	Quaternionf nlerpIterative(Quaternionfc quaternionfc, float float1, float float2, Quaternionf quaternionf);

	Quaternionf lookAlong(Vector3fc vector3fc, Vector3fc vector3fc2, Quaternionf quaternionf);

	Quaternionf lookAlong(float float1, float float2, float float3, float float4, float float5, float float6, Quaternionf quaternionf);

	Quaternionf rotateTo(float float1, float float2, float float3, float float4, float float5, float float6, Quaternionf quaternionf);

	Quaternionf rotateTo(Vector3fc vector3fc, Vector3fc vector3fc2, Quaternionf quaternionf);

	Quaternionf rotateX(float float1, Quaternionf quaternionf);

	Quaternionf rotateY(float float1, Quaternionf quaternionf);

	Quaternionf rotateZ(float float1, Quaternionf quaternionf);

	Quaternionf rotateLocalX(float float1, Quaternionf quaternionf);

	Quaternionf rotateLocalY(float float1, Quaternionf quaternionf);

	Quaternionf rotateLocalZ(float float1, Quaternionf quaternionf);

	Quaternionf rotateAxis(float float1, float float2, float float3, float float4, Quaternionf quaternionf);

	Quaternionf rotateAxis(float float1, Vector3fc vector3fc, Quaternionf quaternionf);

	Quaternionf difference(Quaternionfc quaternionfc, Quaternionf quaternionf);

	Vector3f positiveX(Vector3f vector3f);

	Vector3f normalizedPositiveX(Vector3f vector3f);

	Vector3f positiveY(Vector3f vector3f);

	Vector3f normalizedPositiveY(Vector3f vector3f);

	Vector3f positiveZ(Vector3f vector3f);

	Vector3f normalizedPositiveZ(Vector3f vector3f);

	Quaternionf conjugateBy(Quaternionfc quaternionfc, Quaternionf quaternionf);

	boolean isFinite();

	boolean equals(Quaternionfc quaternionfc, float float1);

	boolean equals(float float1, float float2, float float3, float float4);
}
