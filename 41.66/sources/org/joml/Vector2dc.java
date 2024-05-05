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

	Vector2dc getToAddress(long long1);

	Vector2d sub(double double1, double double2, Vector2d vector2d);

	Vector2d sub(Vector2dc vector2dc, Vector2d vector2d);

	Vector2d sub(Vector2fc vector2fc, Vector2d vector2d);

	Vector2d mul(double double1, Vector2d vector2d);

	Vector2d mul(double double1, double double2, Vector2d vector2d);

	Vector2d mul(Vector2dc vector2dc, Vector2d vector2d);

	Vector2d div(double double1, Vector2d vector2d);

	Vector2d div(double double1, double double2, Vector2d vector2d);

	Vector2d div(Vector2fc vector2fc, Vector2d vector2d);

	Vector2d div(Vector2dc vector2dc, Vector2d vector2d);

	Vector2d mul(Matrix2dc matrix2dc, Vector2d vector2d);

	Vector2d mul(Matrix2fc matrix2fc, Vector2d vector2d);

	Vector2d mulTranspose(Matrix2dc matrix2dc, Vector2d vector2d);

	Vector2d mulTranspose(Matrix2fc matrix2fc, Vector2d vector2d);

	Vector2d mulPosition(Matrix3x2dc matrix3x2dc, Vector2d vector2d);

	Vector2d mulDirection(Matrix3x2dc matrix3x2dc, Vector2d vector2d);

	double dot(Vector2dc vector2dc);

	double angle(Vector2dc vector2dc);

	double lengthSquared();

	double length();

	double distance(Vector2dc vector2dc);

	double distanceSquared(Vector2dc vector2dc);

	double distance(Vector2fc vector2fc);

	double distanceSquared(Vector2fc vector2fc);

	double distance(double double1, double double2);

	double distanceSquared(double double1, double double2);

	Vector2d normalize(Vector2d vector2d);

	Vector2d normalize(double double1, Vector2d vector2d);

	Vector2d add(double double1, double double2, Vector2d vector2d);

	Vector2d add(Vector2dc vector2dc, Vector2d vector2d);

	Vector2d add(Vector2fc vector2fc, Vector2d vector2d);

	Vector2d negate(Vector2d vector2d);

	Vector2d lerp(Vector2dc vector2dc, double double1, Vector2d vector2d);

	Vector2d fma(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2d vector2d);

	Vector2d fma(double double1, Vector2dc vector2dc, Vector2d vector2d);

	Vector2d min(Vector2dc vector2dc, Vector2d vector2d);

	Vector2d max(Vector2dc vector2dc, Vector2d vector2d);

	int maxComponent();

	int minComponent();

	double get(int int1) throws IllegalArgumentException;

	Vector2i get(int int1, Vector2i vector2i);

	Vector2f get(Vector2f vector2f);

	Vector2d get(Vector2d vector2d);

	Vector2d floor(Vector2d vector2d);

	Vector2d ceil(Vector2d vector2d);

	Vector2d round(Vector2d vector2d);

	boolean isFinite();

	Vector2d absolute(Vector2d vector2d);

	boolean equals(Vector2dc vector2dc, double double1);

	boolean equals(double double1, double double2);
}
