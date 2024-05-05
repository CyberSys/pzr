package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;


public interface Matrix3x2dc {

	double m00();

	double m01();

	double m10();

	double m11();

	double m20();

	double m21();

	Matrix3x2d mul(Matrix3x2dc matrix3x2dc, Matrix3x2d matrix3x2d);

	Matrix3x2d mulLocal(Matrix3x2dc matrix3x2dc, Matrix3x2d matrix3x2d);

	double determinant();

	Matrix3x2d invert(Matrix3x2d matrix3x2d);

	Matrix3x2d translate(double double1, double double2, Matrix3x2d matrix3x2d);

	Matrix3x2d translate(Vector2dc vector2dc, Matrix3x2d matrix3x2d);

	Matrix3x2d translateLocal(Vector2dc vector2dc, Matrix3x2d matrix3x2d);

	Matrix3x2d translateLocal(double double1, double double2, Matrix3x2d matrix3x2d);

	Matrix3x2d get(Matrix3x2d matrix3x2d);

	DoubleBuffer get(DoubleBuffer doubleBuffer);

	DoubleBuffer get(int int1, DoubleBuffer doubleBuffer);

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	DoubleBuffer get3x3(DoubleBuffer doubleBuffer);

	DoubleBuffer get3x3(int int1, DoubleBuffer doubleBuffer);

	ByteBuffer get3x3(ByteBuffer byteBuffer);

	ByteBuffer get3x3(int int1, ByteBuffer byteBuffer);

	DoubleBuffer get4x4(DoubleBuffer doubleBuffer);

	DoubleBuffer get4x4(int int1, DoubleBuffer doubleBuffer);

	ByteBuffer get4x4(ByteBuffer byteBuffer);

	ByteBuffer get4x4(int int1, ByteBuffer byteBuffer);

	Matrix3x2dc getToAddress(long long1);

	double[] get(double[] doubleArray, int int1);

	double[] get(double[] doubleArray);

	double[] get3x3(double[] doubleArray, int int1);

	double[] get3x3(double[] doubleArray);

	double[] get4x4(double[] doubleArray, int int1);

	double[] get4x4(double[] doubleArray);

	Matrix3x2d scale(double double1, double double2, Matrix3x2d matrix3x2d);

	Matrix3x2d scale(Vector2dc vector2dc, Matrix3x2d matrix3x2d);

	Matrix3x2d scale(Vector2fc vector2fc, Matrix3x2d matrix3x2d);

	Matrix3x2d scaleLocal(double double1, Matrix3x2d matrix3x2d);

	Matrix3x2d scaleLocal(double double1, double double2, Matrix3x2d matrix3x2d);

	Matrix3x2d scaleAroundLocal(double double1, double double2, double double3, double double4, Matrix3x2d matrix3x2d);

	Matrix3x2d scaleAroundLocal(double double1, double double2, double double3, Matrix3x2d matrix3x2d);

	Matrix3x2d scale(double double1, Matrix3x2d matrix3x2d);

	Matrix3x2d scaleAround(double double1, double double2, double double3, double double4, Matrix3x2d matrix3x2d);

	Matrix3x2d scaleAround(double double1, double double2, double double3, Matrix3x2d matrix3x2d);

	Vector3d transform(Vector3d vector3d);

	Vector3d transform(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transform(double double1, double double2, double double3, Vector3d vector3d);

	Vector2d transformPosition(Vector2d vector2d);

	Vector2d transformPosition(Vector2dc vector2dc, Vector2d vector2d);

	Vector2d transformPosition(double double1, double double2, Vector2d vector2d);

	Vector2d transformDirection(Vector2d vector2d);

	Vector2d transformDirection(Vector2dc vector2dc, Vector2d vector2d);

	Vector2d transformDirection(double double1, double double2, Vector2d vector2d);

	Matrix3x2d rotate(double double1, Matrix3x2d matrix3x2d);

	Matrix3x2d rotateLocal(double double1, Matrix3x2d matrix3x2d);

	Matrix3x2d rotateAbout(double double1, double double2, double double3, Matrix3x2d matrix3x2d);

	Matrix3x2d rotateTo(Vector2dc vector2dc, Vector2dc vector2dc2, Matrix3x2d matrix3x2d);

	Matrix3x2d view(double double1, double double2, double double3, double double4, Matrix3x2d matrix3x2d);

	Vector2d origin(Vector2d vector2d);

	double[] viewArea(double[] doubleArray);

	Vector2d positiveX(Vector2d vector2d);

	Vector2d normalizedPositiveX(Vector2d vector2d);

	Vector2d positiveY(Vector2d vector2d);

	Vector2d normalizedPositiveY(Vector2d vector2d);

	Vector2d unproject(double double1, double double2, int[] intArray, Vector2d vector2d);

	Vector2d unprojectInv(double double1, double double2, int[] intArray, Vector2d vector2d);

	boolean testPoint(double double1, double double2);

	boolean testCircle(double double1, double double2, double double3);

	boolean testAar(double double1, double double2, double double3, double double4);

	boolean equals(Matrix3x2dc matrix3x2dc, double double1);

	boolean isFinite();
}
