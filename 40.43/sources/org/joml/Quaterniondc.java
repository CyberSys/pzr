package org.joml;


public interface Quaterniondc {

	double x();

	double y();

	double z();

	double w();

	Quaterniond normalize(Quaterniond quaterniond);

	Quaterniond add(double double1, double double2, double double3, double double4, Quaterniond quaterniond);

	Quaterniond add(Quaterniondc quaterniondc, Quaterniond quaterniond);

	double dot(Quaterniondc quaterniondc);

	double angle();

	Matrix3d get(Matrix3d matrix3d);

	Matrix3f get(Matrix3f matrix3f);

	Matrix4d get(Matrix4d matrix4d);

	Matrix4f get(Matrix4f matrix4f);

	Quaterniond get(Quaterniond quaterniond);

	Quaterniond mul(Quaterniondc quaterniondc, Quaterniond quaterniond);

	Quaterniond mul(double double1, double double2, double double3, double double4, Quaterniond quaterniond);

	Quaterniond premul(Quaterniondc quaterniondc, Quaterniond quaterniond);

	Quaterniond premul(double double1, double double2, double double3, double double4, Quaterniond quaterniond);

	Vector3d transform(Vector3d vector3d);

	Vector4d transform(Vector4d vector4d);

	Vector3d transform(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transform(double double1, double double2, double double3, Vector3d vector3d);

	Vector4d transform(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transform(double double1, double double2, double double3, Vector4d vector4d);

	Quaterniond invert(Quaterniond quaterniond);

	Quaterniond div(Quaterniondc quaterniondc, Quaterniond quaterniond);

	Quaterniond conjugate(Quaterniond quaterniond);

	double lengthSquared();

	Quaterniond slerp(Quaterniondc quaterniondc, double double1, Quaterniond quaterniond);

	Quaterniond scale(double double1, Quaterniond quaterniond);

	Quaterniond integrate(double double1, double double2, double double3, double double4, Quaterniond quaterniond);

	Quaterniond nlerp(Quaterniondc quaterniondc, double double1, Quaterniond quaterniond);

	Quaterniond nlerpIterative(Quaterniondc quaterniondc, double double1, double double2, Quaterniond quaterniond);

	Quaterniond lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Quaterniond quaterniond);

	Quaterniond lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Quaterniond quaterniond);

	Quaterniond difference(Quaterniondc quaterniondc, Quaterniond quaterniond);

	Quaterniond rotateTo(double double1, double double2, double double3, double double4, double double5, double double6, Quaterniond quaterniond);

	Quaterniond rotateTo(Vector3dc vector3dc, Vector3dc vector3dc2, Quaterniond quaterniond);

	Quaterniond rotate(Vector3dc vector3dc, Quaterniond quaterniond);

	Quaterniond rotate(double double1, double double2, double double3, Quaterniond quaterniond);

	Quaterniond rotateLocal(double double1, double double2, double double3, Quaterniond quaterniond);

	Quaterniond rotateX(double double1, Quaterniond quaterniond);

	Quaterniond rotateY(double double1, Quaterniond quaterniond);

	Quaterniond rotateZ(double double1, Quaterniond quaterniond);

	Quaterniond rotateLocalX(double double1, Quaterniond quaterniond);

	Quaterniond rotateLocalY(double double1, Quaterniond quaterniond);

	Quaterniond rotateLocalZ(double double1, Quaterniond quaterniond);

	Quaterniond rotateXYZ(double double1, double double2, double double3, Quaterniond quaterniond);

	Quaterniond rotateZYX(double double1, double double2, double double3, Quaterniond quaterniond);

	Quaterniond rotateYXZ(double double1, double double2, double double3, Quaterniond quaterniond);

	Vector3d getEulerAnglesXYZ(Vector3d vector3d);

	Quaterniond rotateAxis(double double1, double double2, double double3, double double4, Quaterniond quaterniond);

	Quaterniond rotateAxis(double double1, Vector3dc vector3dc, Quaterniond quaterniond);

	Vector3d positiveX(Vector3d vector3d);

	Vector3d normalizedPositiveX(Vector3d vector3d);

	Vector3d positiveY(Vector3d vector3d);

	Vector3d normalizedPositiveY(Vector3d vector3d);

	Vector3d positiveZ(Vector3d vector3d);

	Vector3d normalizedPositiveZ(Vector3d vector3d);
}
