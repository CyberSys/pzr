package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;


public interface Vector3fc {

	float x();

	float y();

	float z();

	FloatBuffer get(FloatBuffer floatBuffer);

	FloatBuffer get(int int1, FloatBuffer floatBuffer);

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	Vector3fc getToAddress(long long1);

	Vector3f sub(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f sub(float float1, float float2, float float3, Vector3f vector3f);

	Vector3f add(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f add(float float1, float float2, float float3, Vector3f vector3f);

	Vector3f fma(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3f vector3f);

	Vector3f fma(float float1, Vector3fc vector3fc, Vector3f vector3f);

	Vector3f mulAdd(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3f vector3f);

	Vector3f mulAdd(float float1, Vector3fc vector3fc, Vector3f vector3f);

	Vector3f mul(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f div(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f mulProject(Matrix4fc matrix4fc, Vector3f vector3f);

	Vector3f mulProject(Matrix4fc matrix4fc, float float1, Vector3f vector3f);

	Vector3f mul(Matrix3fc matrix3fc, Vector3f vector3f);

	Vector3f mul(Matrix3dc matrix3dc, Vector3f vector3f);

	Vector3f mul(Matrix3x2fc matrix3x2fc, Vector3f vector3f);

	Vector3f mulTranspose(Matrix3fc matrix3fc, Vector3f vector3f);

	Vector3f mulPosition(Matrix4fc matrix4fc, Vector3f vector3f);

	Vector3f mulPosition(Matrix4x3fc matrix4x3fc, Vector3f vector3f);

	Vector3f mulTransposePosition(Matrix4fc matrix4fc, Vector3f vector3f);

	float mulPositionW(Matrix4fc matrix4fc, Vector3f vector3f);

	Vector3f mulDirection(Matrix4dc matrix4dc, Vector3f vector3f);

	Vector3f mulDirection(Matrix4fc matrix4fc, Vector3f vector3f);

	Vector3f mulDirection(Matrix4x3fc matrix4x3fc, Vector3f vector3f);

	Vector3f mulTransposeDirection(Matrix4fc matrix4fc, Vector3f vector3f);

	Vector3f mul(float float1, Vector3f vector3f);

	Vector3f mul(float float1, float float2, float float3, Vector3f vector3f);

	Vector3f div(float float1, Vector3f vector3f);

	Vector3f div(float float1, float float2, float float3, Vector3f vector3f);

	Vector3f rotate(Quaternionfc quaternionfc, Vector3f vector3f);

	Quaternionf rotationTo(Vector3fc vector3fc, Quaternionf quaternionf);

	Quaternionf rotationTo(float float1, float float2, float float3, Quaternionf quaternionf);

	Vector3f rotateAxis(float float1, float float2, float float3, float float4, Vector3f vector3f);

	Vector3f rotateX(float float1, Vector3f vector3f);

	Vector3f rotateY(float float1, Vector3f vector3f);

	Vector3f rotateZ(float float1, Vector3f vector3f);

	float lengthSquared();

	float length();

	Vector3f normalize(Vector3f vector3f);

	Vector3f normalize(float float1, Vector3f vector3f);

	Vector3f cross(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f cross(float float1, float float2, float float3, Vector3f vector3f);

	float distance(Vector3fc vector3fc);

	float distance(float float1, float float2, float float3);

	float distanceSquared(Vector3fc vector3fc);

	float distanceSquared(float float1, float float2, float float3);

	float dot(Vector3fc vector3fc);

	float dot(float float1, float float2, float float3);

	float angleCos(Vector3fc vector3fc);

	float angle(Vector3fc vector3fc);

	float angleSigned(Vector3fc vector3fc, Vector3fc vector3fc2);

	float angleSigned(float float1, float float2, float float3, float float4, float float5, float float6);

	Vector3f min(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f max(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f negate(Vector3f vector3f);

	Vector3f absolute(Vector3f vector3f);

	Vector3f reflect(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f reflect(float float1, float float2, float float3, Vector3f vector3f);

	Vector3f half(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f half(float float1, float float2, float float3, Vector3f vector3f);

	Vector3f smoothStep(Vector3fc vector3fc, float float1, Vector3f vector3f);

	Vector3f hermite(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, float float1, Vector3f vector3f);

	Vector3f lerp(Vector3fc vector3fc, float float1, Vector3f vector3f);

	float get(int int1) throws IllegalArgumentException;

	Vector3i get(int int1, Vector3i vector3i);

	Vector3f get(Vector3f vector3f);

	Vector3d get(Vector3d vector3d);

	int maxComponent();

	int minComponent();

	Vector3f orthogonalize(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f orthogonalizeUnit(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f floor(Vector3f vector3f);

	Vector3f ceil(Vector3f vector3f);

	Vector3f round(Vector3f vector3f);

	boolean isFinite();

	boolean equals(Vector3fc vector3fc, float float1);

	boolean equals(float float1, float float2, float float3);
}
