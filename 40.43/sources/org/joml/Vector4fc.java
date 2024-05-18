package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;


public interface Vector4fc {

	float x();

	float y();

	float z();

	float w();

	FloatBuffer get(FloatBuffer floatBuffer);

	FloatBuffer get(int int1, FloatBuffer floatBuffer);

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	Vector4f sub(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f sub(float float1, float float2, float float3, float float4, Vector4f vector4f);

	Vector4f add(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f add(float float1, float float2, float float3, float float4, Vector4f vector4f);

	Vector4f fma(Vector4fc vector4fc, Vector4fc vector4fc2, Vector4f vector4f);

	Vector4f fma(float float1, Vector4fc vector4fc, Vector4f vector4f);

	Vector4f mul(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f div(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f mul(Matrix4fc matrix4fc, Vector4f vector4f);

	Vector4f mul(Matrix4x3fc matrix4x3fc, Vector4f vector4f);

	Vector4f mulProject(Matrix4fc matrix4fc, Vector4f vector4f);

	Vector4f mul(float float1, Vector4f vector4f);

	Vector4f mul(float float1, float float2, float float3, float float4, Vector4f vector4f);

	Vector4f div(float float1, Vector4f vector4f);

	Vector4f div(float float1, float float2, float float3, float float4, Vector4f vector4f);

	Vector4f rotate(Quaternionfc quaternionfc, Vector4f vector4f);

	float lengthSquared();

	float length();

	Vector4f normalize(Vector4f vector4f);

	float distance(Vector4fc vector4fc);

	float distance(float float1, float float2, float float3, float float4);

	float dot(Vector4fc vector4fc);

	float dot(float float1, float float2, float float3, float float4);

	float angleCos(Vector4fc vector4fc);

	float angle(Vector4fc vector4fc);

	Vector4f negate(Vector4f vector4f);

	Vector4f lerp(Vector4fc vector4fc, float float1, Vector4f vector4f);

	Vector4f smoothStep(Vector4fc vector4fc, float float1, Vector4f vector4f);

	Vector4f hermite(Vector4fc vector4fc, Vector4fc vector4fc2, Vector4fc vector4fc3, float float1, Vector4f vector4f);
}
