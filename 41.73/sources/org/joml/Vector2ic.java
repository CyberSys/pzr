package org.joml;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;


public interface Vector2ic {

	int x();

	int y();

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	IntBuffer get(IntBuffer intBuffer);

	IntBuffer get(int int1, IntBuffer intBuffer);

	Vector2ic getToAddress(long long1);

	Vector2i sub(Vector2ic vector2ic, Vector2i vector2i);

	Vector2i sub(int int1, int int2, Vector2i vector2i);

	long lengthSquared();

	double length();

	double distance(Vector2ic vector2ic);

	double distance(int int1, int int2);

	long distanceSquared(Vector2ic vector2ic);

	long distanceSquared(int int1, int int2);

	long gridDistance(Vector2ic vector2ic);

	long gridDistance(int int1, int int2);

	Vector2i add(Vector2ic vector2ic, Vector2i vector2i);

	Vector2i add(int int1, int int2, Vector2i vector2i);

	Vector2i mul(int int1, Vector2i vector2i);

	Vector2i mul(Vector2ic vector2ic, Vector2i vector2i);

	Vector2i mul(int int1, int int2, Vector2i vector2i);

	Vector2i div(float float1, Vector2i vector2i);

	Vector2i div(int int1, Vector2i vector2i);

	Vector2i negate(Vector2i vector2i);

	Vector2i min(Vector2ic vector2ic, Vector2i vector2i);

	Vector2i max(Vector2ic vector2ic, Vector2i vector2i);

	int maxComponent();

	int minComponent();

	Vector2i absolute(Vector2i vector2i);

	int get(int int1) throws IllegalArgumentException;

	boolean equals(int int1, int int2);
}
