package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;


public interface Matrix2dc {

	double m00();

	double m01();

	double m10();

	double m11();

	Matrix2d mul(Matrix2dc matrix2dc, Matrix2d matrix2d);

	Matrix2d mul(Matrix2fc matrix2fc, Matrix2d matrix2d);

	Matrix2d mulLocal(Matrix2dc matrix2dc, Matrix2d matrix2d);

	double determinant();

	Matrix2d invert(Matrix2d matrix2d);

	Matrix2d transpose(Matrix2d matrix2d);

	Matrix2d get(Matrix2d matrix2d);

	Matrix3x2d get(Matrix3x2d matrix3x2d);

	Matrix3d get(Matrix3d matrix3d);

	double getRotation();

	DoubleBuffer get(DoubleBuffer doubleBuffer);

	DoubleBuffer get(int int1, DoubleBuffer doubleBuffer);

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	DoubleBuffer getTransposed(DoubleBuffer doubleBuffer);

	DoubleBuffer getTransposed(int int1, DoubleBuffer doubleBuffer);

	ByteBuffer getTransposed(ByteBuffer byteBuffer);

	ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer);

	Matrix2dc getToAddress(long long1);

	double[] get(double[] doubleArray, int int1);

	double[] get(double[] doubleArray);

	Matrix2d scale(Vector2dc vector2dc, Matrix2d matrix2d);

	Matrix2d scale(double double1, double double2, Matrix2d matrix2d);

	Matrix2d scale(double double1, Matrix2d matrix2d);

	Matrix2d scaleLocal(double double1, double double2, Matrix2d matrix2d);

	Vector2d transform(Vector2d vector2d);

	Vector2d transform(Vector2dc vector2dc, Vector2d vector2d);

	Vector2d transform(double double1, double double2, Vector2d vector2d);

	Vector2d transformTranspose(Vector2d vector2d);

	Vector2d transformTranspose(Vector2dc vector2dc, Vector2d vector2d);

	Vector2d transformTranspose(double double1, double double2, Vector2d vector2d);

	Matrix2d rotate(double double1, Matrix2d matrix2d);

	Matrix2d rotateLocal(double double1, Matrix2d matrix2d);

	Vector2d getRow(int int1, Vector2d vector2d) throws IndexOutOfBoundsException;

	Vector2d getColumn(int int1, Vector2d vector2d) throws IndexOutOfBoundsException;

	double get(int int1, int int2);

	Matrix2d normal(Matrix2d matrix2d);

	Vector2d getScale(Vector2d vector2d);

	Vector2d positiveX(Vector2d vector2d);

	Vector2d normalizedPositiveX(Vector2d vector2d);

	Vector2d positiveY(Vector2d vector2d);

	Vector2d normalizedPositiveY(Vector2d vector2d);

	Matrix2d add(Matrix2dc matrix2dc, Matrix2d matrix2d);

	Matrix2d sub(Matrix2dc matrix2dc, Matrix2d matrix2d);

	Matrix2d mulComponentWise(Matrix2dc matrix2dc, Matrix2d matrix2d);

	Matrix2d lerp(Matrix2dc matrix2dc, double double1, Matrix2d matrix2d);

	boolean equals(Matrix2dc matrix2dc, double double1);

	boolean isFinite();
}
