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

	AxisAngle4f get(AxisAngle4f axisAngle4f);

	AxisAngle4d get(AxisAngle4d axisAngle4d);

	Quaterniond get(Quaterniond quaterniond);

	Quaternionf get(Quaternionf quaternionf);

	Quaterniond mul(Quaterniondc quaterniondc, Quaterniond quaterniond);

	Quaterniond mul(double double1, double double2, double double3, double double4, Quaterniond quaterniond);

	Quaterniond premul(Quaterniondc quaterniondc, Quaterniond quaterniond);

	Quaterniond premul(double double1, double double2, double double3, double double4, Quaterniond quaterniond);

	Vector3d transform(Vector3d vector3d);

	Vector3d transformInverse(Vector3d vector3d);

	Vector3d transformUnit(Vector3d vector3d);

	Vector3d transformInverseUnit(Vector3d vector3d);

	Vector3d transformPositiveX(Vector3d vector3d);

	Vector4d transformPositiveX(Vector4d vector4d);

	Vector3d transformUnitPositiveX(Vector3d vector3d);

	Vector4d transformUnitPositiveX(Vector4d vector4d);

	Vector3d transformPositiveY(Vector3d vector3d);

	Vector4d transformPositiveY(Vector4d vector4d);

	Vector3d transformUnitPositiveY(Vector3d vector3d);

	Vector4d transformUnitPositiveY(Vector4d vector4d);

	Vector3d transformPositiveZ(Vector3d vector3d);

	Vector4d transformPositiveZ(Vector4d vector4d);

	Vector3d transformUnitPositiveZ(Vector3d vector3d);

	Vector4d transformUnitPositiveZ(Vector4d vector4d);

	Vector4d transform(Vector4d vector4d);

	Vector4d transformInverse(Vector4d vector4d);

	Vector3d transform(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transformInverse(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transform(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d transformInverse(double double1, double double2, double double3, Vector3d vector3d);

	Vector4d transform(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transformInverse(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transform(double double1, double double2, double double3, Vector4d vector4d);

	Vector4d transformInverse(double double1, double double2, double double3, Vector4d vector4d);

	Vector3f transform(Vector3f vector3f);

	Vector3f transformInverse(Vector3f vector3f);

	Vector4d transformUnit(Vector4d vector4d);

	Vector4d transformInverseUnit(Vector4d vector4d);

	Vector3d transformUnit(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transformInverseUnit(Vector3dc vector3dc, Vector3d vector3d);

	Vector3d transformUnit(double double1, double double2, double double3, Vector3d vector3d);

	Vector3d transformInverseUnit(double double1, double double2, double double3, Vector3d vector3d);

	Vector4d transformUnit(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transformInverseUnit(Vector4dc vector4dc, Vector4d vector4d);

	Vector4d transformUnit(double double1, double double2, double double3, Vector4d vector4d);

	Vector4d transformInverseUnit(double double1, double double2, double double3, Vector4d vector4d);

	Vector3f transformUnit(Vector3f vector3f);

	Vector3f transformInverseUnit(Vector3f vector3f);

	Vector3f transformPositiveX(Vector3f vector3f);

	Vector4f transformPositiveX(Vector4f vector4f);

	Vector3f transformUnitPositiveX(Vector3f vector3f);

	Vector4f transformUnitPositiveX(Vector4f vector4f);

	Vector3f transformPositiveY(Vector3f vector3f);

	Vector4f transformPositiveY(Vector4f vector4f);

	Vector3f transformUnitPositiveY(Vector3f vector3f);

	Vector4f transformUnitPositiveY(Vector4f vector4f);

	Vector3f transformPositiveZ(Vector3f vector3f);

	Vector4f transformPositiveZ(Vector4f vector4f);

	Vector3f transformUnitPositiveZ(Vector3f vector3f);

	Vector4f transformUnitPositiveZ(Vector4f vector4f);

	Vector4f transform(Vector4f vector4f);

	Vector4f transformInverse(Vector4f vector4f);

	Vector3f transform(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transformInverse(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transform(double double1, double double2, double double3, Vector3f vector3f);

	Vector3f transformInverse(double double1, double double2, double double3, Vector3f vector3f);

	Vector4f transform(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f transformInverse(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f transform(double double1, double double2, double double3, Vector4f vector4f);

	Vector4f transformInverse(double double1, double double2, double double3, Vector4f vector4f);

	Vector4f transformUnit(Vector4f vector4f);

	Vector4f transformInverseUnit(Vector4f vector4f);

	Vector3f transformUnit(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transformInverseUnit(Vector3fc vector3fc, Vector3f vector3f);

	Vector3f transformUnit(double double1, double double2, double double3, Vector3f vector3f);

	Vector3f transformInverseUnit(double double1, double double2, double double3, Vector3f vector3f);

	Vector4f transformUnit(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f transformInverseUnit(Vector4fc vector4fc, Vector4f vector4f);

	Vector4f transformUnit(double double1, double double2, double double3, Vector4f vector4f);

	Vector4f transformInverseUnit(double double1, double double2, double double3, Vector4f vector4f);

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

	Quaterniond conjugateBy(Quaterniondc quaterniondc, Quaterniond quaterniond);

	boolean isFinite();

	boolean equals(Quaterniondc quaterniondc, double double1);

	boolean equals(double double1, double double2, double double3, double double4);
}
