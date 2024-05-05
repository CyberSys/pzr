package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;


public interface Vector3dc {

	double x();

	double y();

	double z();

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	DoubleBuffer get(DoubleBuffer doubleBuffer);

	DoubleBuffer get(int int1, DoubleBuffer doubleBuffer);

	FloatBuffer get(FloatBuffer floatBuffer);

	FloatBuffer get(int int1, FloatBuffer floatBuffer);

	ByteBuffer getf(ByteBuffer byteBuffer);

	ByteBuffer getf(int int1, ByteBuffer byteBuffer);

	Vector3dc getToAddress(long long1);

	Vector3d sub(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d sub(Vector3fc vector3fc, Vector3d vector3d);

	Vector3d sub(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d add(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d add(Vector3fc vector3fc, Vector3d vector3d);

	Vector3d add(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d fma(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3d vector3d);

	Vector3d fma(double double1, Vector3dc vector3dc, Vector3d vector3d);

	Vector3d fma(Vector3dc vector3dc, Vector3fc vector3fc, Vector3d vector3d);

	Vector3d fma(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3d vector3d);

	Vector3d fma(double double1, Vector3fc vector3fc, Vector3d vector3d);

	Vector3d mulAdd(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3d vector3d);

	Vector3d mulAdd(double double1, Vector3dc vector3dc, Vector3d vector3d);

	Vector3d mulAdd(Vector3fc vector3fc, Vector3dc vector3dc, Vector3d vector3d);

	Vector3d mul(Vector3fc vector3fc, Vector3d vector3d);

	Vector3d mul(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d div(Vector3fc vector3fc, Vector3d vector3d);

	Vector3d div(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d mulProject(Matrix4dc matrix4dc, double double1, Vector3d vector3d);

	Vector3d mulProject(Matrix4dc matrix4dc, Vector3d vector3d);

	Vector3d mulProject(Matrix4fc matrix4fc, Vector3d vector3d);

	Vector3d mul(Matrix3dc matrix3dc, Vector3d vector3d);

	Vector3f mul(Matrix3dc matrix3dc, Vector3f vector3f);

	Vector3d mul(Matrix3fc matrix3fc, Vector3d vector3d);

	Vector3d mul(Matrix3x2dc matrix3x2dc, Vector3d vector3d);

	Vector3d mul(Matrix3x2fc matrix3x2fc, Vector3d vector3d);

	Vector3d mulTranspose(Matrix3dc matrix3dc, Vector3d vector3d);

	Vector3d mulTranspose(Matrix3fc matrix3fc, Vector3d vector3d);

	Vector3d mulPosition(Matrix4dc matrix4dc, Vector3d vector3d);

	Vector3d mulPosition(Matrix4fc matrix4fc, Vector3d vector3d);

	Vector3d mulPosition(Matrix4x3dc matrix4x3dc, Vector3d vector3d);

	Vector3d mulPosition(Matrix4x3fc matrix4x3fc, Vector3d vector3d);

	Vector3d mulTransposePosition(Matrix4dc matrix4dc, Vector3d vector3d);

	Vector3d mulTransposePosition(Matrix4fc matrix4fc, Vector3d vector3d);

	double mulPositionW(Matrix4fc matrix4fc, Vector3d vector3d);

	double mulPositionW(Matrix4dc matrix4dc, Vector3d vector3d);

	Vector3d mulDirection(Matrix4dc matrix4dc, Vector3d vector3d);

	Vector3d mulDirection(Matrix4fc matrix4fc, Vector3d vector3d);

	Vector3d mulDirection(Matrix4x3dc matrix4x3dc, Vector3d vector3d);

	Vector3d mulDirection(Matrix4x3fc matrix4x3fc, Vector3d vector3d);

	Vector3d mulTransposeDirection(Matrix4dc matrix4dc, Vector3d vector3d);

	Vector3d mulTransposeDirection(Matrix4fc matrix4fc, Vector3d vector3d);

	Vector3d mul(double double1, Vector3d vector3d);

	Vector3d mul(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d rotate(Quaterniondc quaterniondc, Vector3d vector3d);

	Quaterniond rotationTo(Vector3dc vector3dc, Quaterniond quaterniond);

	Quaterniond rotationTo(double double1, double double2, double double3, Quaterniond quaterniond);

	Vector3d rotateAxis(double double1, double double2, double double3, double double4, Vector3d vector3d);

	Vector3d rotateX(double double1, Vector3d vector3d);

	Vector3d rotateY(double double1, Vector3d vector3d);

	Vector3d rotateZ(double double1, Vector3d vector3d);

	Vector3d div(double double1, Vector3d vector3d);

	Vector3d div(double double1, double double2, double double3, Vector3d vector3d);

	double lengthSquared();

	double length();

	Vector3d normalize(Vector3d vector3d);

	Vector3d normalize(double double1, Vector3d vector3d);

	Vector3d cross(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d cross(double double1, double double2, double double3, Vector3d vector3d);

	double distance(Vector3dc vector3dc);

	double distance(double double1, double double2, double double3);

	double distanceSquared(Vector3dc vector3dc);

	double distanceSquared(double double1, double double2, double double3);

	double dot(Vector3dc vector3dc);

	double dot(double double1, double double2, double double3);

	double angleCos(Vector3dc vector3dc);

	double angle(Vector3dc vector3dc);

	double angleSigned(Vector3dc vector3dc, Vector3dc vector3dc2);

	double angleSigned(double double1, double double2, double double3, double double4, double double5, double double6);

	Vector3d min(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d max(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d negate(Vector3d vector3d);

	Vector3d absolute(Vector3d vector3d);

	Vector3d reflect(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d reflect(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d half(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d half(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d smoothStep(Vector3dc vector3dc, double double1, Vector3d vector3d);

	Vector3d hermite(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, double double1, Vector3d vector3d);

	Vector3d lerp(Vector3dc vector3dc, double double1, Vector3d vector3d);

	double get(int int1) throws IllegalArgumentException;

	Vector3i get(int int1, Vector3i vector3i);

	Vector3f get(Vector3f vector3f);

	Vector3d get(Vector3d vector3d);

	int maxComponent();

	int minComponent();

	Vector3d orthogonalize(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d orthogonalizeUnit(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d floor(Vector3d vector3d);

	Vector3d ceil(Vector3d vector3d);

	Vector3d round(Vector3d vector3d);

	boolean isFinite();

	boolean equals(Vector3dc vector3dc, double double1);

	boolean equals(double double1, double double2, double double3);
}
