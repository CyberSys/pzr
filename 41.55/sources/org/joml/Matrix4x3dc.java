package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;


public interface Matrix4x3dc {
	int PLANE_NX = 0;
	int PLANE_PX = 1;
	int PLANE_NY = 2;
	int PLANE_PY = 3;
	int PLANE_NZ = 4;
	int PLANE_PZ = 5;
	byte PROPERTY_IDENTITY = 4;
	byte PROPERTY_TRANSLATION = 8;
	byte PROPERTY_ORTHONORMAL = 16;

	int properties();

	double m00();

	double m01();

	double m02();

	double m10();

	double m11();

	double m12();

	double m20();

	double m21();

	double m22();

	double m30();

	double m31();

	double m32();

	Matrix4d get(Matrix4d matrix4d);

	Matrix4x3d mul(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d mul(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d);

	Matrix4x3d mulTranslation(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d mulTranslation(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d);

	Matrix4x3d mulOrtho(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d fma(Matrix4x3dc matrix4x3dc, double double1, Matrix4x3d matrix4x3d);

	Matrix4x3d fma(Matrix4x3fc matrix4x3fc, double double1, Matrix4x3d matrix4x3d);

	Matrix4x3d add(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d add(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d);

	Matrix4x3d sub(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d sub(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d);

	Matrix4x3d mulComponentWise(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d);

	double determinant();

	Matrix4x3d invert(Matrix4x3d matrix4x3d);

	Matrix4x3d invertOrtho(Matrix4x3d matrix4x3d);

	Matrix4x3d transpose3x3(Matrix4x3d matrix4x3d);

	Matrix3d transpose3x3(Matrix3d matrix3d);

	Vector3d getTranslation(Vector3d vector3d);

	Vector3d getScale(Vector3d vector3d);

	Matrix4x3d get(Matrix4x3d matrix4x3d);

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

	Matrix4x3dc getToAddress(long long1);

	double[] get(double[] doubleArray, int int1);

	double[] get(double[] doubleArray);

	float[] get(float[] floatArray, int int1);

	float[] get(float[] floatArray);

	double[] get4x4(double[] doubleArray, int int1);

	double[] get4x4(double[] doubleArray);

	float[] get4x4(float[] floatArray, int int1);

	float[] get4x4(float[] floatArray);

	DoubleBuffer get4x4(DoubleBuffer doubleBuffer);

	DoubleBuffer get4x4(int int1, DoubleBuffer doubleBuffer);

	ByteBuffer get4x4(ByteBuffer byteBuffer);

	ByteBuffer get4x4(int int1, ByteBuffer byteBuffer);

	DoubleBuffer getTransposed(DoubleBuffer doubleBuffer);

	DoubleBuffer getTransposed(int int1, DoubleBuffer doubleBuffer);

	ByteBuffer getTransposed(ByteBuffer byteBuffer);

	ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer);

	FloatBuffer getTransposed(FloatBuffer floatBuffer);

	FloatBuffer getTransposed(int int1, FloatBuffer floatBuffer);

	ByteBuffer getTransposedFloats(ByteBuffer byteBuffer);

	ByteBuffer getTransposedFloats(int int1, ByteBuffer byteBuffer);

	double[] getTransposed(double[] doubleArray, int int1);

	double[] getTransposed(double[] doubleArray);

	Vector4d transform(Vector4d vector4d);

	Vector4d transform(Vector4dc vector4dc, Vector4d vector4d);

	Vector3d transformPosition(Vector3d vector3d);

	Vector3d transformPosition(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transformDirection(Vector3d vector3d);

	Vector3d transformDirection(Vector3dc vector3dc, Vector3d vector3d);

	Matrix4x3d scale(Vector3dc vector3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d scale(double double1, double double2, double double3, Matrix4x3d matrix4x3d);

	Matrix4x3d scale(double double1, Matrix4x3d matrix4x3d);

	Matrix4x3d scaleXY(double double1, double double2, Matrix4x3d matrix4x3d);

	Matrix4x3d scaleLocal(double double1, double double2, double double3, Matrix4x3d matrix4x3d);

	Matrix4x3d rotate(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateTranslation(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateAround(Quaterniondc quaterniondc, double double1, double double2, double double3, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateLocal(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d);

	Matrix4x3d translate(Vector3dc vector3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d translate(Vector3fc vector3fc, Matrix4x3d matrix4x3d);

	Matrix4x3d translate(double double1, double double2, double double3, Matrix4x3d matrix4x3d);

	Matrix4x3d translateLocal(Vector3fc vector3fc, Matrix4x3d matrix4x3d);

	Matrix4x3d translateLocal(Vector3dc vector3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d translateLocal(double double1, double double2, double double3, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateX(double double1, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateY(double double1, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateZ(double double1, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateXYZ(double double1, double double2, double double3, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateZYX(double double1, double double2, double double3, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateYXZ(double double1, double double2, double double3, Matrix4x3d matrix4x3d);

	Matrix4x3d rotate(Quaterniondc quaterniondc, Matrix4x3d matrix4x3d);

	Matrix4x3d rotate(Quaternionfc quaternionfc, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateTranslation(Quaterniondc quaterniondc, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateTranslation(Quaternionfc quaternionfc, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateLocal(Quaterniondc quaterniondc, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateLocal(Quaternionfc quaternionfc, Matrix4x3d matrix4x3d);

	Matrix4x3d rotate(AxisAngle4f axisAngle4f, Matrix4x3d matrix4x3d);

	Matrix4x3d rotate(AxisAngle4d axisAngle4d, Matrix4x3d matrix4x3d);

	Matrix4x3d rotate(double double1, Vector3dc vector3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d rotate(double double1, Vector3fc vector3fc, Matrix4x3d matrix4x3d);

	Vector4d getRow(int int1, Vector4d vector4d) throws IndexOutOfBoundsException;

	Vector3d getColumn(int int1, Vector3d vector3d) throws IndexOutOfBoundsException;

	Matrix4x3d normal(Matrix4x3d matrix4x3d);

	Matrix3d normal(Matrix3d matrix3d);

	Matrix3d cofactor3x3(Matrix3d matrix3d);

	Matrix4x3d cofactor3x3(Matrix4x3d matrix4x3d);

	Matrix4x3d normalize3x3(Matrix4x3d matrix4x3d);

	Matrix3d normalize3x3(Matrix3d matrix3d);

	Matrix4x3d reflect(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d);

	Matrix4x3d reflect(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d);

	Matrix4x3d reflect(Quaterniondc quaterniondc, Vector3dc vector3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d reflect(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4x3d matrix4x3d);

	Matrix4x3d ortho(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4x3d matrix4x3d);

	Matrix4x3d ortho(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d);

	Matrix4x3d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4x3d matrix4x3d);

	Matrix4x3d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d);

	Matrix4x3d orthoSymmetric(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4x3d matrix4x3d);

	Matrix4x3d orthoSymmetric(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d);

	Matrix4x3d orthoSymmetricLH(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4x3d matrix4x3d);

	Matrix4x3d orthoSymmetricLH(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d);

	Matrix4x3d ortho2D(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d);

	Matrix4x3d ortho2DLH(double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d);

	Matrix4x3d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4x3d matrix4x3d);

	Matrix4x3d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d);

	Matrix4x3d lookAt(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Matrix4x3d matrix4x3d);

	Matrix4x3d lookAt(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4x3d matrix4x3d);

	Matrix4x3d lookAtLH(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Matrix4x3d matrix4x3d);

	Matrix4x3d lookAtLH(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4x3d matrix4x3d);

	Vector4d frustumPlane(int int1, Vector4d vector4d);

	Vector3d positiveZ(Vector3d vector3d);

	Vector3d normalizedPositiveZ(Vector3d vector3d);

	Vector3d positiveX(Vector3d vector3d);

	Vector3d normalizedPositiveX(Vector3d vector3d);

	Vector3d positiveY(Vector3d vector3d);

	Vector3d normalizedPositiveY(Vector3d vector3d);

	Vector3d origin(Vector3d vector3d);

	Matrix4x3d shadow(Vector4dc vector4dc, double double1, double double2, double double3, double double4, Matrix4x3d matrix4x3d);

	Matrix4x3d shadow(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Matrix4x3d matrix4x3d);

	Matrix4x3d shadow(Vector4dc vector4dc, Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d shadow(double double1, double double2, double double3, double double4, Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d);

	Matrix4x3d pick(double double1, double double2, double double3, double double4, int[] intArray, Matrix4x3d matrix4x3d);

	Matrix4x3d arcball(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d);

	Matrix4x3d arcball(double double1, Vector3dc vector3dc, double double2, double double3, Matrix4x3d matrix4x3d);

	Matrix4x3d transformAab(double double1, double double2, double double3, double double4, double double5, double double6, Vector3d vector3d, Vector3d vector3d2);

	Matrix4x3d transformAab(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3d vector3d, Vector3d vector3d2);

	Matrix4x3d lerp(Matrix4x3dc matrix4x3dc, double double1, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4x3d matrix4x3d);

	Matrix4x3d rotateTowards(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4x3d matrix4x3d);

	Vector3d getEulerAnglesZYX(Vector3d vector3d);

	Matrix4x3d obliqueZ(double double1, double double2, Matrix4x3d matrix4x3d);

	boolean equals(Matrix4x3dc matrix4x3dc, double double1);

	boolean isFinite();
}
