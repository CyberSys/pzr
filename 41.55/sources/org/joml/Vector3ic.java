package org.joml;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;


public interface Vector3ic {

	int x();

	int y();

	int z();

	IntBuffer get(IntBuffer intBuffer);

	IntBuffer get(int int1, IntBuffer intBuffer);

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	Vector3ic getToAddress(long long1);

	Vector3i sub(Vector3ic vector3ic, Vector3i vector3i);

	Vector3i sub(int int1, int int2, int int3, Vector3i vector3i);

	Vector3i add(Vector3ic vector3ic, Vector3i vector3i);

	Vector3i add(int int1, int int2, int int3, Vector3i vector3i);

	Vector3i mul(int int1, Vector3i vector3i);

	Vector3i mul(Vector3ic vector3ic, Vector3i vector3i);

	Vector3i mul(int int1, int int2, int int3, Vector3i vector3i);

	Vector3i div(float float1, Vector3i vector3i);

	Vector3i div(int int1, Vector3i vector3i);

	long lengthSquared();

	double length();

	double distance(Vector3ic vector3ic);

	double distance(int int1, int int2, int int3);

	long gridDistance(Vector3ic vector3ic);

	long gridDistance(int int1, int int2, int int3);

	long distanceSquared(Vector3ic vector3ic);

	long distanceSquared(int int1, int int2, int int3);

	Vector3i negate(Vector3i vector3i);

	Vector3i min(Vector3ic vector3ic, Vector3i vector3i);

	Vector3i max(Vector3ic vector3ic, Vector3i vector3i);

	int get(int int1) throws IllegalArgumentException;

	int maxComponent();

	int minComponent();

	Vector3i absolute(Vector3i vector3i);

	boolean equals(int int1, int int2, int int3);
}
