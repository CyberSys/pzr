package org.joml;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;


public interface Vector4ic {

	int x();

	int y();

	int z();

	int w();

	IntBuffer get(IntBuffer intBuffer);

	IntBuffer get(int int1, IntBuffer intBuffer);

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	Vector4i sub(Vector4ic vector4ic, Vector4i vector4i);

	Vector4i sub(int int1, int int2, int int3, int int4, Vector4i vector4i);

	Vector4i add(Vector4ic vector4ic, Vector4i vector4i);

	Vector4i add(int int1, int int2, int int3, int int4, Vector4i vector4i);

	Vector4i mul(Vector4ic vector4ic, Vector4i vector4i);

	Vector4i div(Vector4ic vector4ic, Vector4i vector4i);

	Vector4i mul(float float1, Vector4i vector4i);

	Vector4i div(float float1, Vector4i vector4i);

	long lengthSquared();

	double length();

	double distance(Vector4ic vector4ic);

	double distance(int int1, int int2, int int3, int int4);

	int distanceSquared(Vector4ic vector4ic);

	int distanceSquared(int int1, int int2, int int3, int int4);

	int dot(Vector4ic vector4ic);

	Vector4i negate(Vector4i vector4i);
}
