package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;


public interface Matrix3dc {

	double m00();

	double m01();

	double m02();

	double m10();

	double m11();

	double m12();

	double m20();

	double m21();

	double m22();

	Matrix3d mul(Matrix3dc matrix3dc, Matrix3d matrix3d);

	Matrix3d mulLocal(Matrix3dc matrix3dc, Matrix3d matrix3d);

	Matrix3d mul(Matrix3fc matrix3fc, Matrix3d matrix3d);

	double determinant();

	Matrix3d invert(Matrix3d matrix3d);

	Matrix3d transpose(Matrix3d matrix3d);

	Matrix3d get(Matrix3d matrix3d);

	AxisAngle4f getRotation(AxisAngle4f axisAngle4f);

	Quaternionf getUnnormalizedRotation(Quaternionf quaternionf);

	Quaternionf getNormalizedRotation(Quaternionf quaternionf);

	Quaterniond getUnnormalizedRotation(Quaterniond quaterniond);

	Quaterniond getNormalizedRotation(Quaterniond quaterniond);

	DoubleBuffer get(DoubleBuffer doubleBuffer);

	DoubleBuffer get(int int1, DoubleBuffer doubleBuffer);

	FloatBuffer get(FloatBuffer floatBuffer);

	FloatBuffer get(int int1, FloatBuffer floatBuffer);

	ByteBuffer get(ByteBuffer byteBuffer);

	ByteBuffer get(int int1, ByteBuffer byteBuffer);

	ByteBuffer getFloats(ByteBuffer byteBuffer);

	ByteBuffer getFloats(int int1, ByteBuffer byteBuffer);

	Matrix3dc getToAddress(long long1);

	double[] get(double[] doubleArray, int int1);

	double[] get(double[] doubleArray);

	float[] get(float[] floatArray, int int1);

	float[] get(float[] floatArray);

	Matrix3d scale(Vector3dc vector3dc, Matrix3d matrix3d);

	Matrix3d scale(double double1, double double2, double double3, Matrix3d matrix3d);

	Matrix3d scale(double double1, Matrix3d matrix3d);

	Matrix3d scaleLocal(double double1, double double2, double double3, Matrix3d matrix3d);

	Vector3d transform(Vector3d vector3d);

	Vector3d transform(Vector3dc vector3dc, Vector3d vector3d);

	Vector3f transform(Vector3f vector3f);

	Vector3f transform(Vector3fc vector3fc, Vector3f vector3f);

	Vector3d transform(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d transformTranspose(Vector3d vector3d);

	Vector3d transformTranspose(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transformTranspose(double double1, double double2, double double3, Vector3d vector3d);

	Matrix3d rotateX(double double1, Matrix3d matrix3d);

	Matrix3d rotateY(double double1, Matrix3d matrix3d);

	Matrix3d rotateZ(double double1, Matrix3d matrix3d);

	Matrix3d rotateXYZ(double double1, double double2, double double3, Matrix3d matrix3d);

	Matrix3d rotateZYX(double double1, double double2, double double3, Matrix3d matrix3d);

	Matrix3d rotateYXZ(double double1, double double2, double double3, Matrix3d matrix3d);

	Matrix3d rotate(double double1, double double2, double double3, double double4, Matrix3d matrix3d);

	Matrix3d rotateLocal(double double1, double double2, double double3, double double4, Matrix3d matrix3d);

	Matrix3d rotateLocalX(double double1, Matrix3d matrix3d);

	Matrix3d rotateLocalY(double double1, Matrix3d matrix3d);

	Matrix3d rotateLocalZ(double double1, Matrix3d matrix3d);

	Matrix3d rotateLocal(Quaterniondc quaterniondc, Matrix3d matrix3d);

	Matrix3d rotateLocal(Quaternionfc quaternionfc, Matrix3d matrix3d);

	Matrix3d rotate(Quaterniondc quaterniondc, Matrix3d matrix3d);

	Matrix3d rotate(Quaternionfc quaternionfc, Matrix3d matrix3d);

	Matrix3d rotate(AxisAngle4f axisAngle4f, Matrix3d matrix3d);

	Matrix3d rotate(AxisAngle4d axisAngle4d, Matrix3d matrix3d);

	Matrix3d rotate(double double1, Vector3dc vector3dc, Matrix3d matrix3d);

	Matrix3d rotate(double double1, Vector3fc vector3fc, Matrix3d matrix3d);

	Vector3d getRow(int int1, Vector3d vector3d) throws IndexOutOfBoundsException;

	Vector3d getColumn(int int1, Vector3d vector3d) throws IndexOutOfBoundsException;

	double get(int int1, int int2);

	double getRowColumn(int int1, int int2);

	Matrix3d normal(Matrix3d matrix3d);

	Matrix3d cofactor(Matrix3d matrix3d);

	Matrix3d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix3d matrix3d);

	Matrix3d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Matrix3d matrix3d);

	Vector3d getScale(Vector3d vector3d);

	Vector3d positiveZ(Vector3d vector3d);

	Vector3d normalizedPositiveZ(Vector3d vector3d);

	Vector3d positiveX(Vector3d vector3d);

	Vector3d normalizedPositiveX(Vector3d vector3d);

	Vector3d positiveY(Vector3d vector3d);

	Vector3d normalizedPositiveY(Vector3d vector3d);

	Matrix3d add(Matrix3dc matrix3dc, Matrix3d matrix3d);

	Matrix3d sub(Matrix3dc matrix3dc, Matrix3d matrix3d);

	Matrix3d mulComponentWise(Matrix3dc matrix3dc, Matrix3d matrix3d);

	Matrix3d lerp(Matrix3dc matrix3dc, double double1, Matrix3d matrix3d);

	Matrix3d rotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix3d matrix3d);

	Matrix3d rotateTowards(double double1, double double2, double double3, double double4, double double5, double double6, Matrix3d matrix3d);

	Vector3d getEulerAnglesZYX(Vector3d vector3d);

	Matrix3d obliqueZ(double double1, double double2, Matrix3d matrix3d);

	boolean equals(Matrix3dc matrix3dc, double double1);

	Matrix3d reflect(double double1, double double2, double double3, Matrix3d matrix3d);

	Matrix3d reflect(Quaterniondc quaterniondc, Matrix3d matrix3d);

	Matrix3d reflect(Vector3dc vector3dc, Matrix3d matrix3d);

	boolean isFinite();

	double quadraticFormProduct(double double1, double double2, double double3);

	double quadraticFormProduct(Vector3dc vector3dc);

	double quadraticFormProduct(Vector3fc vector3fc);
}
