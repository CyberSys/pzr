package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;


public interface Matrix4dc {
	int PLANE_NX = 0;
	int PLANE_PX = 1;
	int PLANE_NY = 2;
	int PLANE_PY = 3;
	int PLANE_NZ = 4;
	int PLANE_PZ = 5;
	int CORNER_NXNYNZ = 0;
	int CORNER_PXNYNZ = 1;
	int CORNER_PXPYNZ = 2;
	int CORNER_NXPYNZ = 3;
	int CORNER_PXNYPZ = 4;
	int CORNER_NXNYPZ = 5;
	int CORNER_NXPYPZ = 6;
	int CORNER_PXPYPZ = 7;
	byte PROPERTY_PERSPECTIVE = 1;
	byte PROPERTY_AFFINE = 2;
	byte PROPERTY_IDENTITY = 4;
	byte PROPERTY_TRANSLATION = 8;
	byte PROPERTY_ORTHONORMAL = 16;

	int properties();

	double m00();

	double m01();

	double m02();

	double m03();

	double m10();

	double m11();

	double m12();

	double m13();

	double m20();

	double m21();

	double m22();

	double m23();

	double m30();

	double m31();

	double m32();

	double m33();

	Matrix4d mul(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d mul0(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d mul(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16, Matrix4d matrix4d);

	Matrix4d mul3x3(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d);

	Matrix4d mulLocal(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d mulLocalAffine(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d mul(Matrix3x2dc matrix3x2dc, Matrix4d matrix4d);

	Matrix4d mul(Matrix3x2fc matrix3x2fc, Matrix4d matrix4d);

	Matrix4d mul(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d);

	Matrix4d mul(Matrix4x3fc matrix4x3fc, Matrix4d matrix4d);

	Matrix4d mul(Matrix4fc matrix4fc, Matrix4d matrix4d);

	Matrix4d mulPerspectiveAffine(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d mulPerspectiveAffine(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d);

	Matrix4d mulAffineR(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d mulAffine(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d mulTranslationAffine(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d mulOrthoAffine(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d fma4x3(Matrix4dc matrix4dc, double double1, Matrix4d matrix4d);

	Matrix4d add(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d sub(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d mulComponentWise(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d add4x3(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d add4x3(Matrix4fc matrix4fc, Matrix4d matrix4d);

	Matrix4d sub4x3(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d mul4x3ComponentWise(Matrix4dc matrix4dc, Matrix4d matrix4d);

	double determinant();

	double determinant3x3();

	double determinantAffine();

	Matrix4d invert(Matrix4d matrix4d);

	Matrix4d invertPerspective(Matrix4d matrix4d);

	Matrix4d invertFrustum(Matrix4d matrix4d);

	Matrix4d invertOrtho(Matrix4d matrix4d);

	Matrix4d invertPerspectiveView(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d invertPerspectiveView(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d);

	Matrix4d invertAffine(Matrix4d matrix4d);

	Matrix4d transpose(Matrix4d matrix4d);

	Matrix4d transpose3x3(Matrix4d matrix4d);

	Matrix3d transpose3x3(Matrix3d matrix3d);

	Vector3d getTranslation(Vector3d vector3d);

	Vector3d getScale(Vector3d vector3d);

	Matrix4d get(Matrix4d matrix4d);

	Matrix4x3d get4x3(Matrix4x3d matrix4x3d);

	Matrix3d get3x3(Matrix3d matrix3d);

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

	Matrix4dc getToAddress(long long1);

	ByteBuffer getFloats(ByteBuffer byteBuffer);

	ByteBuffer getFloats(int int1, ByteBuffer byteBuffer);

	double[] get(double[] doubleArray, int int1);

	double[] get(double[] doubleArray);

	float[] get(float[] floatArray, int int1);

	float[] get(float[] floatArray);

	DoubleBuffer getTransposed(DoubleBuffer doubleBuffer);

	DoubleBuffer getTransposed(int int1, DoubleBuffer doubleBuffer);

	ByteBuffer getTransposed(ByteBuffer byteBuffer);

	ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer);

	DoubleBuffer get4x3Transposed(DoubleBuffer doubleBuffer);

	DoubleBuffer get4x3Transposed(int int1, DoubleBuffer doubleBuffer);

	ByteBuffer get4x3Transposed(ByteBuffer byteBuffer);

	ByteBuffer get4x3Transposed(int int1, ByteBuffer byteBuffer);

	Vector4d transform(Vector4d vector4d);

	Vector4d transform(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transform(double double1, double double2, double double3, double double4, Vector4d vector4d);

	Vector4d transformTranspose(Vector4d vector4d);

	Vector4d transformTranspose(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transformTranspose(double double1, double double2, double double3, double double4, Vector4d vector4d);

	Vector4d transformProject(Vector4d vector4d);

	Vector4d transformProject(Vector4dc vector4dc, Vector4d vector4d);

	Vector3d transformProject(Vector4dc vector4dc, Vector3d vector3d);

	Vector4d transformProject(double double1, double double2, double double3, double double4, Vector4d vector4d);

	Vector3d transformProject(Vector3d vector3d);

	Vector3d transformProject(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transformProject(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d transformProject(double double1, double double2, double double3, double double4, Vector3d vector3d);

	Vector3d transformPosition(Vector3d vector3d);

	Vector3d transformPosition(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transformPosition(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d transformDirection(Vector3d vector3d);

	Vector3d transformDirection(Vector3dc vector3dc, Vector3d vector3d);

	Vector3f transformDirection(Vector3f vector3f);

	Vector3f transformDirection(Vector3fc vector3fc, Vector3f vector3f);

	Vector3d transformDirection(double double1, double double2, double double3, Vector3d vector3d);

	Vector3f transformDirection(double double1, double double2, double double3, Vector3f vector3f);

	Vector4d transformAffine(Vector4d vector4d);

	Vector4d transformAffine(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transformAffine(double double1, double double2, double double3, double double4, Vector4d vector4d);

	Matrix4d scale(Vector3dc vector3dc, Matrix4d matrix4d);

	Matrix4d scale(double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d scale(double double1, Matrix4d matrix4d);

	Matrix4d scaleXY(double double1, double double2, Matrix4d matrix4d);

	Matrix4d scaleAround(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d);

	Matrix4d scaleAround(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d scaleLocal(double double1, Matrix4d matrix4d);

	Matrix4d scaleLocal(double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d scaleAroundLocal(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d);

	Matrix4d scaleAroundLocal(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d rotate(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d rotateTranslation(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d rotateAffine(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d rotateAroundAffine(Quaterniondc quaterniondc, double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d rotateAround(Quaterniondc quaterniondc, double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d rotateLocal(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d rotateLocalX(double double1, Matrix4d matrix4d);

	Matrix4d rotateLocalY(double double1, Matrix4d matrix4d);

	Matrix4d rotateLocalZ(double double1, Matrix4d matrix4d);

	Matrix4d rotateAroundLocal(Quaterniondc quaterniondc, double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d translate(Vector3dc vector3dc, Matrix4d matrix4d);

	Matrix4d translate(Vector3fc vector3fc, Matrix4d matrix4d);

	Matrix4d translate(double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d translateLocal(Vector3fc vector3fc, Matrix4d matrix4d);

	Matrix4d translateLocal(Vector3dc vector3dc, Matrix4d matrix4d);

	Matrix4d translateLocal(double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d rotateX(double double1, Matrix4d matrix4d);

	Matrix4d rotateY(double double1, Matrix4d matrix4d);

	Matrix4d rotateZ(double double1, Matrix4d matrix4d);

	Matrix4d rotateTowardsXY(double double1, double double2, Matrix4d matrix4d);

	Matrix4d rotateXYZ(double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d rotateAffineXYZ(double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d rotateZYX(double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d rotateAffineZYX(double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d rotateYXZ(double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d rotateAffineYXZ(double double1, double double2, double double3, Matrix4d matrix4d);

	Matrix4d rotate(Quaterniondc quaterniondc, Matrix4d matrix4d);

	Matrix4d rotate(Quaternionfc quaternionfc, Matrix4d matrix4d);

	Matrix4d rotateAffine(Quaterniondc quaterniondc, Matrix4d matrix4d);

	Matrix4d rotateTranslation(Quaterniondc quaterniondc, Matrix4d matrix4d);

	Matrix4d rotateTranslation(Quaternionfc quaternionfc, Matrix4d matrix4d);

	Matrix4d rotateLocal(Quaterniondc quaterniondc, Matrix4d matrix4d);

	Matrix4d rotateAffine(Quaternionfc quaternionfc, Matrix4d matrix4d);

	Matrix4d rotateLocal(Quaternionfc quaternionfc, Matrix4d matrix4d);

	Matrix4d rotate(AxisAngle4f axisAngle4f, Matrix4d matrix4d);

	Matrix4d rotate(AxisAngle4d axisAngle4d, Matrix4d matrix4d);

	Matrix4d rotate(double double1, Vector3dc vector3dc, Matrix4d matrix4d);

	Matrix4d rotate(double double1, Vector3fc vector3fc, Matrix4d matrix4d);

	Vector4d getRow(int int1, Vector4d vector4d) throws IndexOutOfBoundsException;

	Vector3d getRow(int int1, Vector3d vector3d) throws IndexOutOfBoundsException;

	Vector4d getColumn(int int1, Vector4d vector4d) throws IndexOutOfBoundsException;

	Vector3d getColumn(int int1, Vector3d vector3d) throws IndexOutOfBoundsException;

	double get(int int1, int int2);

	double getRowColumn(int int1, int int2);

	Matrix4d normal(Matrix4d matrix4d);

	Matrix3d normal(Matrix3d matrix3d);

	Matrix3d cofactor3x3(Matrix3d matrix3d);

	Matrix4d cofactor3x3(Matrix4d matrix4d);

	Matrix4d normalize3x3(Matrix4d matrix4d);

	Matrix3d normalize3x3(Matrix3d matrix3d);

	Vector4d unproject(double double1, double double2, double double3, int[] intArray, Vector4d vector4d);

	Vector3d unproject(double double1, double double2, double double3, int[] intArray, Vector3d vector3d);

	Vector4d unproject(Vector3dc vector3dc, int[] intArray, Vector4d vector4d);

	Vector3d unproject(Vector3dc vector3dc, int[] intArray, Vector3d vector3d);

	Matrix4d unprojectRay(double double1, double double2, int[] intArray, Vector3d vector3d, Vector3d vector3d2);

	Matrix4d unprojectRay(Vector2dc vector2dc, int[] intArray, Vector3d vector3d, Vector3d vector3d2);

	Vector4d unprojectInv(Vector3dc vector3dc, int[] intArray, Vector4d vector4d);

	Vector4d unprojectInv(double double1, double double2, double double3, int[] intArray, Vector4d vector4d);

	Vector3d unprojectInv(Vector3dc vector3dc, int[] intArray, Vector3d vector3d);

	Vector3d unprojectInv(double double1, double double2, double double3, int[] intArray, Vector3d vector3d);

	Matrix4d unprojectInvRay(Vector2dc vector2dc, int[] intArray, Vector3d vector3d, Vector3d vector3d2);

	Matrix4d unprojectInvRay(double double1, double double2, int[] intArray, Vector3d vector3d, Vector3d vector3d2);

	Vector4d project(double double1, double double2, double double3, int[] intArray, Vector4d vector4d);

	Vector3d project(double double1, double double2, double double3, int[] intArray, Vector3d vector3d);

	Vector4d project(Vector3dc vector3dc, int[] intArray, Vector4d vector4d);

	Vector3d project(Vector3dc vector3dc, int[] intArray, Vector3d vector3d);

	Matrix4d reflect(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d reflect(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d);

	Matrix4d reflect(Quaterniondc quaterniondc, Vector3dc vector3dc, Matrix4d matrix4d);

	Matrix4d reflect(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4d matrix4d);

	Matrix4d ortho(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d);

	Matrix4d ortho(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d);

	Matrix4d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d);

	Matrix4d orthoLH(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d);

	Matrix4d orthoSymmetric(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d);

	Matrix4d orthoSymmetric(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d orthoSymmetricLH(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d);

	Matrix4d orthoSymmetricLH(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d ortho2D(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d ortho2DLH(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4d matrix4d);

	Matrix4d lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d);

	Matrix4d lookAt(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Matrix4d matrix4d);

	Matrix4d lookAt(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d);

	Matrix4d lookAtPerspective(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d);

	Matrix4d lookAtLH(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Matrix4d matrix4d);

	Matrix4d lookAtLH(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d);

	Matrix4d lookAtPerspectiveLH(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, Matrix4d matrix4d);

	Matrix4d perspective(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d);

	Matrix4d perspective(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d perspectiveRect(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d);

	Matrix4d perspectiveRect(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d perspectiveRect(double double1, double double2, double double3, double double4, boolean boolean1);

	Matrix4d perspectiveRect(double double1, double double2, double double3, double double4);

	Matrix4d perspectiveOffCenter(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d);

	Matrix4d perspectiveOffCenter(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d);

	Matrix4d perspectiveOffCenter(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1);

	Matrix4d perspectiveOffCenter(double double1, double double2, double double3, double double4, double double5, double double6);

	Matrix4d perspectiveLH(double double1, double double2, double double3, double double4, boolean boolean1, Matrix4d matrix4d);

	Matrix4d perspectiveLH(double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d frustum(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d);

	Matrix4d frustum(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d);

	Matrix4d frustumLH(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1, Matrix4d matrix4d);

	Matrix4d frustumLH(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d);

	Vector4d frustumPlane(int int1, Vector4d vector4d);

	Vector3d frustumCorner(int int1, Vector3d vector3d);

	Vector3d perspectiveOrigin(Vector3d vector3d);

	Vector3d perspectiveInvOrigin(Vector3d vector3d);

	double perspectiveFov();

	double perspectiveNear();

	double perspectiveFar();

	Vector3d frustumRayDir(double double1, double double2, Vector3d vector3d);

	Vector3d positiveZ(Vector3d vector3d);

	Vector3d normalizedPositiveZ(Vector3d vector3d);

	Vector3d positiveX(Vector3d vector3d);

	Vector3d normalizedPositiveX(Vector3d vector3d);

	Vector3d positiveY(Vector3d vector3d);

	Vector3d normalizedPositiveY(Vector3d vector3d);

	Vector3d originAffine(Vector3d vector3d);

	Vector3d origin(Vector3d vector3d);

	Matrix4d shadow(Vector4dc vector4dc, double double1, double double2, double double3, double double4, Matrix4d matrix4d);

	Matrix4d shadow(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Matrix4d matrix4d);

	Matrix4d shadow(Vector4dc vector4dc, Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d shadow(double double1, double double2, double double3, double double4, Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d pick(double double1, double double2, double double3, double double4, int[] intArray, Matrix4d matrix4d);

	boolean isAffine();

	Matrix4d arcball(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d);

	Matrix4d arcball(double double1, Vector3dc vector3dc, double double2, double double3, Matrix4d matrix4d);

	Matrix4d projectedGridRange(Matrix4dc matrix4dc, double double1, double double2, Matrix4d matrix4d);

	Matrix4d perspectiveFrustumSlice(double double1, double double2, Matrix4d matrix4d);

	Matrix4d orthoCrop(Matrix4dc matrix4dc, Matrix4d matrix4d);

	Matrix4d transformAab(double double1, double double2, double double3, double double4, double double5, double double6, Vector3d vector3d, Vector3d vector3d2);

	Matrix4d transformAab(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3d vector3d, Vector3d vector3d2);

	Matrix4d lerp(Matrix4dc matrix4dc, double double1, Matrix4d matrix4d);

	Matrix4d rotateTowards(Vector3dc vector3dc, Vector3dc vector3dc2, Matrix4d matrix4d);

	Matrix4d rotateTowards(double double1, double double2, double double3, double double4, double double5, double double6, Matrix4d matrix4d);

	Vector3d getEulerAnglesZYX(Vector3d vector3d);

	boolean testPoint(double double1, double double2, double double3);

	boolean testSphere(double double1, double double2, double double3, double double4);

	boolean testAab(double double1, double double2, double double3, double double4, double double5, double double6);

	Matrix4d obliqueZ(double double1, double double2, Matrix4d matrix4d);

	Matrix4d withLookAtUp(Vector3dc vector3dc, Matrix4d matrix4d);

	Matrix4d withLookAtUp(double double1, double double2, double double3, Matrix4d matrix4d);

	boolean equals(Matrix4dc matrix4dc, double double1);

	boolean isFinite();
}
