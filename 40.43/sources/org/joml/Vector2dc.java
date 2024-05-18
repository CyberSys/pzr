package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;


public interface Vector2dc {

	double x();

	double y();

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	DoubleBuffer get(DoubleBuffer doubleBuffer);

	DoubleBuffer get(int int1, DoubleBuffer doubleBuffer);

	Vector2d sub(double double1, double double2, Vector2d vector2d);

	Vector2d sub(Vector2dc vector2dc, Vector2d vector2d);

	Vector2d sub(Vector2fc vector2fc, Vector2d vector2d);

	Vector2d mul(double double1, Vector2d vector2d);

	Vector2d mul(double double1, double double2, Vector2d vector2d);

	Vector2d mul(Vector2dc vector2dc, Vector2d vector2d);

	double dot(Vector2dc vector2dc);

	double angle(Vector2dc vector2dc);

	double length();

	double distance(Vector2dc vector2dc);

	double distance(Vector2fc vector2fc);

	double distance(double double1, double double2);

	Vector2d normalize(Vector2d vector2d);

	Vector2d add(double double1, double double2, Vector2d vector2d);

	Vector2d add(Vector2dc vector2dc, Vector2d vector2d);

	Vector2d add(Vector2fc vector2fc, Vector2d vector2d);

	Vector2d negate(Vector2d vector2d);

	Vector2d lerp(Vector2dc vector2dc, double double1, Vector2d vector2d);

	Vector2d fma(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2d vector2d);

	Vector2d fma(double double1, Vector2dc vector2dc, Vector2d vector2d);
}
