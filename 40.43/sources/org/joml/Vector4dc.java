package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;


public interface Vector4dc {

	double x();

	double y();

	double z();

	double w();

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	DoubleBuffer get(DoubleBuffer doubleBuffer);

	DoubleBuffer get(int int1, DoubleBuffer doubleBuffer);

	Vector4d sub(double double1, double double2, double double3, double double4, Vector4d vector4d);

	Vector4d add(double double1, double double2, double double3, double double4, Vector4d vector4d);

	Vector4d fma(Vector4dc vector4dc, Vector4dc vector4dc2, Vector4d vector4d);

	Vector4d fma(double double1, Vector4dc vector4dc, Vector4d vector4d);

	Vector4d mul(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d div(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d mul(Matrix4dc matrix4dc, Vector4d vector4d);

	Vector4d mul(Matrix4x3dc matrix4x3dc, Vector4d vector4d);

	Vector4d mul(Matrix4x3fc matrix4x3fc, Vector4d vector4d);

	Vector4d mul(Matrix4fc matrix4fc, Vector4d vector4d);

	Vector4d mulProject(Matrix4dc matrix4dc, Vector4d vector4d);

	Vector4d mul(double double1, Vector4d vector4d);

	Vector4d div(double double1, Vector4d vector4d);

	Vector4d rotate(Quaterniondc quaterniondc, Vector4d vector4d);

	double lengthSquared();

	double length();

	Vector4d normalize(Vector4d vector4d);

	Vector4d normalize3(Vector4d vector4d);

	double distance(Vector4dc vector4dc);

	double distance(double double1, double double2, double double3, double double4);

	double dot(Vector4dc vector4dc);

	double dot(double double1, double double2, double double3, double double4);

	double angleCos(Vector4dc vector4dc);

	double angle(Vector4dc vector4dc);

	Vector4d negate(Vector4d vector4d);

	Vector4d smoothStep(Vector4dc vector4dc, double double1, Vector4d vector4d);

	Vector4d hermite(Vector4dc vector4dc, Vector4dc vector4dc2, Vector4dc vector4dc3, double double1, Vector4d vector4d);

	Vector4d lerp(Vector4dc vector4dc, double double1, Vector4d vector4d);
}
