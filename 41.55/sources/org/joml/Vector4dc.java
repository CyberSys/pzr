package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;


public interface Vector4dc {

	double x();

	double y();

	double z();

	double w();

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	DoubleBuffer get(DoubleBuffer doubleBuffer);

	DoubleBuffer get(int int1, DoubleBuffer doubleBuffer);

	FloatBuffer get(FloatBuffer floatBuffer);

	FloatBuffer get(int int1, FloatBuffer floatBuffer);

	ByteBuffer getf(ByteBuffer byteBuffer);

	ByteBuffer getf(int int1, ByteBuffer byteBuffer);

	Vector4dc getToAddress(long long1);

	Vector4d sub(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d sub(Vector4fc vector4fc, Vector4d vector4d);

	Vector4d sub(double double1, double double2, double double3, double double4, Vector4d vector4d);

	Vector4d add(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d add(Vector4fc vector4fc, Vector4d vector4d);

	Vector4d add(double double1, double double2, double double3, double double4, Vector4d vector4d);

	Vector4d fma(Vector4dc vector4dc, Vector4dc vector4dc2, Vector4d vector4d);

	Vector4d fma(double double1, Vector4dc vector4dc, Vector4d vector4d);

	Vector4d mul(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d mul(Vector4fc vector4fc, Vector4d vector4d);

	Vector4d div(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d mul(Matrix4dc matrix4dc, Vector4d vector4d);

	Vector4d mul(Matrix4x3dc matrix4x3dc, Vector4d vector4d);

	Vector4d mul(Matrix4x3fc matrix4x3fc, Vector4d vector4d);

	Vector4d mul(Matrix4fc matrix4fc, Vector4d vector4d);

	Vector4d mulTranspose(Matrix4dc matrix4dc, Vector4d vector4d);

	Vector4d mulAffine(Matrix4dc matrix4dc, Vector4d vector4d);

	Vector4d mulAffineTranspose(Matrix4dc matrix4dc, Vector4d vector4d);

	Vector4d mulProject(Matrix4dc matrix4dc, Vector4d vector4d);

	Vector3d mulProject(Matrix4dc matrix4dc, Vector3d vector3d);

	Vector4d mulAdd(Vector4dc vector4dc, Vector4dc vector4dc2, Vector4d vector4d);

	Vector4d mulAdd(double double1, Vector4dc vector4dc, Vector4d vector4d);

	Vector4d mul(double double1, Vector4d vector4d);

	Vector4d div(double double1, Vector4d vector4d);

	Vector4d rotate(Quaterniondc quaterniondc, Vector4d vector4d);

	Vector4d rotateAxis(double double1, double double2, double double3, double double4, Vector4d vector4d);

	Vector4d rotateX(double double1, Vector4d vector4d);

	Vector4d rotateY(double double1, Vector4d vector4d);

	Vector4d rotateZ(double double1, Vector4d vector4d);

	double lengthSquared();

	double length();

	Vector4d normalize(Vector4d vector4d);

	Vector4d normalize(double double1, Vector4d vector4d);

	Vector4d normalize3(Vector4d vector4d);

	double distance(Vector4dc vector4dc);

	double distance(double double1, double double2, double double3, double double4);

	double distanceSquared(Vector4dc vector4dc);

	double distanceSquared(double double1, double double2, double double3, double double4);

	double dot(Vector4dc vector4dc);

	double dot(double double1, double double2, double double3, double double4);

	double angleCos(Vector4dc vector4dc);

	double angle(Vector4dc vector4dc);

	Vector4d negate(Vector4d vector4d);

	Vector4d min(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d max(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d smoothStep(Vector4dc vector4dc, double double1, Vector4d vector4d);

	Vector4d hermite(Vector4dc vector4dc, Vector4dc vector4dc2, Vector4dc vector4dc3, double double1, Vector4d vector4d);

	Vector4d lerp(Vector4dc vector4dc, double double1, Vector4d vector4d);

	double get(int int1) throws IllegalArgumentException;

	Vector4i get(int int1, Vector4i vector4i);

	Vector4f get(Vector4f vector4f);

	Vector4d get(Vector4d vector4d);

	int maxComponent();

	int minComponent();

	Vector4d floor(Vector4d vector4d);

	Vector4d ceil(Vector4d vector4d);

	Vector4d round(Vector4d vector4d);

	boolean isFinite();

	Vector4d absolute(Vector4d vector4d);

	boolean equals(Vector4dc vector4dc, double double1);

	boolean equals(double double1, double double2, double double3, double double4);
}
