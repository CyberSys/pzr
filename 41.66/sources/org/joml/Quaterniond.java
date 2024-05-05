package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;


public class Quaterniond implements Externalizable,Quaterniondc {
	private static final long serialVersionUID = 1L;
	public double x;
	public double y;
	public double z;
	public double w;

	public Quaterniond() {
		this.w = 1.0;
	}

	public Quaterniond(double double1, double double2, double double3, double double4) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		this.w = double4;
	}

	public Quaterniond(Quaterniondc quaterniondc) {
		this.x = quaterniondc.x();
		this.y = quaterniondc.y();
		this.z = quaterniondc.z();
		this.w = quaterniondc.w();
	}

	public Quaterniond(Quaternionfc quaternionfc) {
		this.x = (double)quaternionfc.x();
		this.y = (double)quaternionfc.y();
		this.z = (double)quaternionfc.z();
		this.w = (double)quaternionfc.w();
	}

	public Quaterniond(AxisAngle4f axisAngle4f) {
		double double1 = Math.sin((double)axisAngle4f.angle * 0.5);
		this.x = (double)axisAngle4f.x * double1;
		this.y = (double)axisAngle4f.y * double1;
		this.z = (double)axisAngle4f.z * double1;
		this.w = Math.cosFromSin(double1, (double)axisAngle4f.angle * 0.5);
	}

	public Quaterniond(AxisAngle4d axisAngle4d) {
		double double1 = Math.sin(axisAngle4d.angle * 0.5);
		this.x = axisAngle4d.x * double1;
		this.y = axisAngle4d.y * double1;
		this.z = axisAngle4d.z * double1;
		this.w = Math.cosFromSin(double1, axisAngle4d.angle * 0.5);
	}

	public double x() {
		return this.x;
	}

	public double y() {
		return this.y;
	}

	public double z() {
		return this.z;
	}

	public double w() {
		return this.w;
	}

	public Quaterniond normalize() {
		double double1 = Math.invsqrt(this.lengthSquared());
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		this.w *= double1;
		return this;
	}

	public Quaterniond normalize(Quaterniond quaterniond) {
		double double1 = Math.invsqrt(this.lengthSquared());
		quaterniond.x = this.x * double1;
		quaterniond.y = this.y * double1;
		quaterniond.z = this.z * double1;
		quaterniond.w = this.w * double1;
		return quaterniond;
	}

	public Quaterniond add(double double1, double double2, double double3, double double4) {
		return this.add(double1, double2, double3, double4, this);
	}

	public Quaterniond add(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
		quaterniond.x = this.x + double1;
		quaterniond.y = this.y + double2;
		quaterniond.z = this.z + double3;
		quaterniond.w = this.w + double4;
		return quaterniond;
	}

	public Quaterniond add(Quaterniondc quaterniondc) {
		this.x += quaterniondc.x();
		this.y += quaterniondc.y();
		this.z += quaterniondc.z();
		this.w += quaterniondc.w();
		return this;
	}

	public Quaterniond add(Quaterniondc quaterniondc, Quaterniond quaterniond) {
		quaterniond.x = this.x + quaterniondc.x();
		quaterniond.y = this.y + quaterniondc.y();
		quaterniond.z = this.z + quaterniondc.z();
		quaterniond.w = this.w + quaterniondc.w();
		return quaterniond;
	}

	public double dot(Quaterniondc quaterniondc) {
		return this.x * quaterniondc.x() + this.y * quaterniondc.y() + this.z * quaterniondc.z() + this.w * quaterniondc.w();
	}

	public double angle() {
		return 2.0 * Math.safeAcos(this.w);
	}

	public Matrix3d get(Matrix3d matrix3d) {
		return matrix3d.set((Quaterniondc)this);
	}

	public Matrix3f get(Matrix3f matrix3f) {
		return matrix3f.set((Quaterniondc)this);
	}

	public Matrix4d get(Matrix4d matrix4d) {
		return matrix4d.set((Quaterniondc)this);
	}

	public Matrix4f get(Matrix4f matrix4f) {
		return matrix4f.set((Quaterniondc)this);
	}

	public AxisAngle4f get(AxisAngle4f axisAngle4f) {
		double double1 = this.x;
		double double2 = this.y;
		double double3 = this.z;
		double double4 = this.w;
		double double5;
		if (double4 > 1.0) {
			double5 = Math.invsqrt(this.lengthSquared());
			double1 *= double5;
			double2 *= double5;
			double3 *= double5;
			double4 *= double5;
		}

		axisAngle4f.angle = (float)(2.0 * Math.acos(double4));
		double5 = Math.sqrt(1.0 - double4 * double4);
		if (double5 < 0.001) {
			axisAngle4f.x = (float)double1;
			axisAngle4f.y = (float)double2;
			axisAngle4f.z = (float)double3;
		} else {
			double5 = 1.0 / double5;
			axisAngle4f.x = (float)(double1 * double5);
			axisAngle4f.y = (float)(double2 * double5);
			axisAngle4f.z = (float)(double3 * double5);
		}

		return axisAngle4f;
	}

	public AxisAngle4d get(AxisAngle4d axisAngle4d) {
		double double1 = this.x;
		double double2 = this.y;
		double double3 = this.z;
		double double4 = this.w;
		double double5;
		if (double4 > 1.0) {
			double5 = Math.invsqrt(this.lengthSquared());
			double1 *= double5;
			double2 *= double5;
			double3 *= double5;
			double4 *= double5;
		}

		axisAngle4d.angle = 2.0 * Math.acos(double4);
		double5 = Math.sqrt(1.0 - double4 * double4);
		if (double5 < 0.001) {
			axisAngle4d.x = double1;
			axisAngle4d.y = double2;
			axisAngle4d.z = double3;
		} else {
			double5 = 1.0 / double5;
			axisAngle4d.x = double1 * double5;
			axisAngle4d.y = double2 * double5;
			axisAngle4d.z = double3 * double5;
		}

		return axisAngle4d;
	}

	public Quaterniond get(Quaterniond quaterniond) {
		return quaterniond.set((Quaterniondc)this);
	}

	public Quaternionf get(Quaternionf quaternionf) {
		return quaternionf.set((Quaterniondc)this);
	}

	public Quaterniond set(double double1, double double2, double double3, double double4) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		this.w = double4;
		return this;
	}

	public Quaterniond set(Quaterniondc quaterniondc) {
		this.x = quaterniondc.x();
		this.y = quaterniondc.y();
		this.z = quaterniondc.z();
		this.w = quaterniondc.w();
		return this;
	}

	public Quaterniond set(Quaternionfc quaternionfc) {
		this.x = (double)quaternionfc.x();
		this.y = (double)quaternionfc.y();
		this.z = (double)quaternionfc.z();
		this.w = (double)quaternionfc.w();
		return this;
	}

	public Quaterniond set(AxisAngle4f axisAngle4f) {
		return this.setAngleAxis((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z);
	}

	public Quaterniond set(AxisAngle4d axisAngle4d) {
		return this.setAngleAxis(axisAngle4d.angle, axisAngle4d.x, axisAngle4d.y, axisAngle4d.z);
	}

	public Quaterniond setAngleAxis(double double1, double double2, double double3, double double4) {
		double double5 = Math.sin(double1 * 0.5);
		this.x = double2 * double5;
		this.y = double3 * double5;
		this.z = double4 * double5;
		this.w = Math.cosFromSin(double5, double1 * 0.5);
		return this;
	}

	public Quaterniond setAngleAxis(double double1, Vector3dc vector3dc) {
		return this.setAngleAxis(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	private void setFromUnnormalized(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double11 = Math.invsqrt(double4 * double4 + double5 * double5 + double6 * double6);
		double double12 = Math.invsqrt(double7 * double7 + double8 * double8 + double9 * double9);
		double double13 = double1 * double10;
		double double14 = double2 * double10;
		double double15 = double3 * double10;
		double double16 = double4 * double11;
		double double17 = double5 * double11;
		double double18 = double6 * double11;
		double double19 = double7 * double12;
		double double20 = double8 * double12;
		double double21 = double9 * double12;
		this.setFromNormalized(double13, double14, double15, double16, double17, double18, double19, double20, double21);
	}

	private void setFromNormalized(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = double1 + double5 + double9;
		double double11;
		if (double10 >= 0.0) {
			double11 = Math.sqrt(double10 + 1.0);
			this.w = double11 * 0.5;
			double11 = 0.5 / double11;
			this.x = (double6 - double8) * double11;
			this.y = (double7 - double3) * double11;
			this.z = (double2 - double4) * double11;
		} else if (double1 >= double5 && double1 >= double9) {
			double11 = Math.sqrt(double1 - (double5 + double9) + 1.0);
			this.x = double11 * 0.5;
			double11 = 0.5 / double11;
			this.y = (double4 + double2) * double11;
			this.z = (double3 + double7) * double11;
			this.w = (double6 - double8) * double11;
		} else if (double5 > double9) {
			double11 = Math.sqrt(double5 - (double9 + double1) + 1.0);
			this.y = double11 * 0.5;
			double11 = 0.5 / double11;
			this.z = (double8 + double6) * double11;
			this.x = (double4 + double2) * double11;
			this.w = (double7 - double3) * double11;
		} else {
			double11 = Math.sqrt(double9 - (double1 + double5) + 1.0);
			this.z = double11 * 0.5;
			double11 = 0.5 / double11;
			this.x = (double3 + double7) * double11;
			this.y = (double8 + double6) * double11;
			this.w = (double2 - double4) * double11;
		}
	}

	public Quaterniond setFromUnnormalized(Matrix4fc matrix4fc) {
		this.setFromUnnormalized((double)matrix4fc.m00(), (double)matrix4fc.m01(), (double)matrix4fc.m02(), (double)matrix4fc.m10(), (double)matrix4fc.m11(), (double)matrix4fc.m12(), (double)matrix4fc.m20(), (double)matrix4fc.m21(), (double)matrix4fc.m22());
		return this;
	}

	public Quaterniond setFromUnnormalized(Matrix4x3fc matrix4x3fc) {
		this.setFromUnnormalized((double)matrix4x3fc.m00(), (double)matrix4x3fc.m01(), (double)matrix4x3fc.m02(), (double)matrix4x3fc.m10(), (double)matrix4x3fc.m11(), (double)matrix4x3fc.m12(), (double)matrix4x3fc.m20(), (double)matrix4x3fc.m21(), (double)matrix4x3fc.m22());
		return this;
	}

	public Quaterniond setFromUnnormalized(Matrix4x3dc matrix4x3dc) {
		this.setFromUnnormalized(matrix4x3dc.m00(), matrix4x3dc.m01(), matrix4x3dc.m02(), matrix4x3dc.m10(), matrix4x3dc.m11(), matrix4x3dc.m12(), matrix4x3dc.m20(), matrix4x3dc.m21(), matrix4x3dc.m22());
		return this;
	}

	public Quaterniond setFromNormalized(Matrix4fc matrix4fc) {
		this.setFromNormalized((double)matrix4fc.m00(), (double)matrix4fc.m01(), (double)matrix4fc.m02(), (double)matrix4fc.m10(), (double)matrix4fc.m11(), (double)matrix4fc.m12(), (double)matrix4fc.m20(), (double)matrix4fc.m21(), (double)matrix4fc.m22());
		return this;
	}

	public Quaterniond setFromNormalized(Matrix4x3fc matrix4x3fc) {
		this.setFromNormalized((double)matrix4x3fc.m00(), (double)matrix4x3fc.m01(), (double)matrix4x3fc.m02(), (double)matrix4x3fc.m10(), (double)matrix4x3fc.m11(), (double)matrix4x3fc.m12(), (double)matrix4x3fc.m20(), (double)matrix4x3fc.m21(), (double)matrix4x3fc.m22());
		return this;
	}

	public Quaterniond setFromNormalized(Matrix4x3dc matrix4x3dc) {
		this.setFromNormalized(matrix4x3dc.m00(), matrix4x3dc.m01(), matrix4x3dc.m02(), matrix4x3dc.m10(), matrix4x3dc.m11(), matrix4x3dc.m12(), matrix4x3dc.m20(), matrix4x3dc.m21(), matrix4x3dc.m22());
		return this;
	}

	public Quaterniond setFromUnnormalized(Matrix4dc matrix4dc) {
		this.setFromUnnormalized(matrix4dc.m00(), matrix4dc.m01(), matrix4dc.m02(), matrix4dc.m10(), matrix4dc.m11(), matrix4dc.m12(), matrix4dc.m20(), matrix4dc.m21(), matrix4dc.m22());
		return this;
	}

	public Quaterniond setFromNormalized(Matrix4dc matrix4dc) {
		this.setFromNormalized(matrix4dc.m00(), matrix4dc.m01(), matrix4dc.m02(), matrix4dc.m10(), matrix4dc.m11(), matrix4dc.m12(), matrix4dc.m20(), matrix4dc.m21(), matrix4dc.m22());
		return this;
	}

	public Quaterniond setFromUnnormalized(Matrix3fc matrix3fc) {
		this.setFromUnnormalized((double)matrix3fc.m00(), (double)matrix3fc.m01(), (double)matrix3fc.m02(), (double)matrix3fc.m10(), (double)matrix3fc.m11(), (double)matrix3fc.m12(), (double)matrix3fc.m20(), (double)matrix3fc.m21(), (double)matrix3fc.m22());
		return this;
	}

	public Quaterniond setFromNormalized(Matrix3fc matrix3fc) {
		this.setFromNormalized((double)matrix3fc.m00(), (double)matrix3fc.m01(), (double)matrix3fc.m02(), (double)matrix3fc.m10(), (double)matrix3fc.m11(), (double)matrix3fc.m12(), (double)matrix3fc.m20(), (double)matrix3fc.m21(), (double)matrix3fc.m22());
		return this;
	}

	public Quaterniond setFromUnnormalized(Matrix3dc matrix3dc) {
		this.setFromUnnormalized(matrix3dc.m00(), matrix3dc.m01(), matrix3dc.m02(), matrix3dc.m10(), matrix3dc.m11(), matrix3dc.m12(), matrix3dc.m20(), matrix3dc.m21(), matrix3dc.m22());
		return this;
	}

	public Quaterniond setFromNormalized(Matrix3dc matrix3dc) {
		this.setFromNormalized(matrix3dc.m00(), matrix3dc.m01(), matrix3dc.m02(), matrix3dc.m10(), matrix3dc.m11(), matrix3dc.m12(), matrix3dc.m20(), matrix3dc.m21(), matrix3dc.m22());
		return this;
	}

	public Quaterniond fromAxisAngleRad(Vector3dc vector3dc, double double1) {
		return this.fromAxisAngleRad(vector3dc.x(), vector3dc.y(), vector3dc.z(), double1);
	}

	public Quaterniond fromAxisAngleRad(double double1, double double2, double double3, double double4) {
		double double5 = double4 / 2.0;
		double double6 = Math.sin(double5);
		double double7 = Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		this.x = double1 / double7 * double6;
		this.y = double2 / double7 * double6;
		this.z = double3 / double7 * double6;
		this.w = Math.cosFromSin(double6, double5);
		return this;
	}

	public Quaterniond fromAxisAngleDeg(Vector3dc vector3dc, double double1) {
		return this.fromAxisAngleRad(vector3dc.x(), vector3dc.y(), vector3dc.z(), Math.toRadians(double1));
	}

	public Quaterniond fromAxisAngleDeg(double double1, double double2, double double3, double double4) {
		return this.fromAxisAngleRad(double1, double2, double3, Math.toRadians(double4));
	}

	public Quaterniond mul(Quaterniondc quaterniondc) {
		return this.mul(quaterniondc, this);
	}

	public Quaterniond mul(Quaterniondc quaterniondc, Quaterniond quaterniond) {
		return this.mul(quaterniondc.x(), quaterniondc.y(), quaterniondc.z(), quaterniondc.w(), quaterniond);
	}

	public Quaterniond mul(double double1, double double2, double double3, double double4) {
		return this.mul(double1, double2, double3, double4, this);
	}

	public Quaterniond mul(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
		return quaterniond.set(Math.fma(this.w, double1, Math.fma(this.x, double4, Math.fma(this.y, double3, -this.z * double2))), Math.fma(this.w, double2, Math.fma(-this.x, double3, Math.fma(this.y, double4, this.z * double1))), Math.fma(this.w, double3, Math.fma(this.x, double2, Math.fma(-this.y, double1, this.z * double4))), Math.fma(this.w, double4, Math.fma(-this.x, double1, Math.fma(-this.y, double2, -this.z * double3))));
	}

	public Quaterniond premul(Quaterniondc quaterniondc) {
		return this.premul(quaterniondc, this);
	}

	public Quaterniond premul(Quaterniondc quaterniondc, Quaterniond quaterniond) {
		return this.premul(quaterniondc.x(), quaterniondc.y(), quaterniondc.z(), quaterniondc.w(), quaterniond);
	}

	public Quaterniond premul(double double1, double double2, double double3, double double4) {
		return this.premul(double1, double2, double3, double4, this);
	}

	public Quaterniond premul(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
		return quaterniond.set(Math.fma(double4, this.x, Math.fma(double1, this.w, Math.fma(double2, this.z, -double3 * this.y))), Math.fma(double4, this.y, Math.fma(-double1, this.z, Math.fma(double2, this.w, double3 * this.x))), Math.fma(double4, this.z, Math.fma(double1, this.y, Math.fma(-double2, this.x, double3 * this.w))), Math.fma(double4, this.w, Math.fma(-double1, this.x, Math.fma(-double2, this.y, -double3 * this.z))));
	}

	public Vector3d transform(Vector3d vector3d) {
		return this.transform(vector3d.x, vector3d.y, vector3d.z, vector3d);
	}

	public Vector3d transformInverse(Vector3d vector3d) {
		return this.transformInverse(vector3d.x, vector3d.y, vector3d.z, vector3d);
	}

	public Vector3d transformUnit(Vector3d vector3d) {
		return this.transformUnit(vector3d.x, vector3d.y, vector3d.z, vector3d);
	}

	public Vector3d transformInverseUnit(Vector3d vector3d) {
		return this.transformInverseUnit(vector3d.x, vector3d.y, vector3d.z, vector3d);
	}

	public Vector3d transformPositiveX(Vector3d vector3d) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.z * this.w;
		double double6 = this.x * this.y;
		double double7 = this.x * this.z;
		double double8 = this.y * this.w;
		vector3d.x = double1 + double2 - double4 - double3;
		vector3d.y = double6 + double5 + double5 + double6;
		vector3d.z = double7 - double8 + double7 - double8;
		return vector3d;
	}

	public Vector4d transformPositiveX(Vector4d vector4d) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.z * this.w;
		double double6 = this.x * this.y;
		double double7 = this.x * this.z;
		double double8 = this.y * this.w;
		vector4d.x = double1 + double2 - double4 - double3;
		vector4d.y = double6 + double5 + double5 + double6;
		vector4d.z = double7 - double8 + double7 - double8;
		return vector4d;
	}

	public Vector3d transformUnitPositiveX(Vector3d vector3d) {
		double double1 = this.y * this.y;
		double double2 = this.z * this.z;
		double double3 = this.x * this.y;
		double double4 = this.x * this.z;
		double double5 = this.y * this.w;
		double double6 = this.z * this.w;
		vector3d.x = 1.0 - double1 - double1 - double2 - double2;
		vector3d.y = double3 + double6 + double3 + double6;
		vector3d.z = double4 - double5 + double4 - double5;
		return vector3d;
	}

	public Vector4d transformUnitPositiveX(Vector4d vector4d) {
		double double1 = this.y * this.y;
		double double2 = this.z * this.z;
		double double3 = this.x * this.y;
		double double4 = this.x * this.z;
		double double5 = this.y * this.w;
		double double6 = this.z * this.w;
		vector4d.x = 1.0 - double1 - double1 - double2 - double2;
		vector4d.y = double3 + double6 + double3 + double6;
		vector4d.z = double4 - double5 + double4 - double5;
		return vector4d;
	}

	public Vector3d transformPositiveY(Vector3d vector3d) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.z * this.w;
		double double6 = this.x * this.y;
		double double7 = this.y * this.z;
		double double8 = this.x * this.w;
		vector3d.x = -double5 + double6 - double5 + double6;
		vector3d.y = double3 - double4 + double1 - double2;
		vector3d.z = double7 + double7 + double8 + double8;
		return vector3d;
	}

	public Vector4d transformPositiveY(Vector4d vector4d) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.z * this.w;
		double double6 = this.x * this.y;
		double double7 = this.y * this.z;
		double double8 = this.x * this.w;
		vector4d.x = -double5 + double6 - double5 + double6;
		vector4d.y = double3 - double4 + double1 - double2;
		vector4d.z = double7 + double7 + double8 + double8;
		return vector4d;
	}

	public Vector4d transformUnitPositiveY(Vector4d vector4d) {
		double double1 = this.x * this.x;
		double double2 = this.z * this.z;
		double double3 = this.x * this.y;
		double double4 = this.y * this.z;
		double double5 = this.x * this.w;
		double double6 = this.z * this.w;
		vector4d.x = double3 - double6 + double3 - double6;
		vector4d.y = 1.0 - double1 - double1 - double2 - double2;
		vector4d.z = double4 + double4 + double5 + double5;
		return vector4d;
	}

	public Vector3d transformUnitPositiveY(Vector3d vector3d) {
		double double1 = this.x * this.x;
		double double2 = this.z * this.z;
		double double3 = this.x * this.y;
		double double4 = this.y * this.z;
		double double5 = this.x * this.w;
		double double6 = this.z * this.w;
		vector3d.x = double3 - double6 + double3 - double6;
		vector3d.y = 1.0 - double1 - double1 - double2 - double2;
		vector3d.z = double4 + double4 + double5 + double5;
		return vector3d;
	}

	public Vector3d transformPositiveZ(Vector3d vector3d) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.x * this.z;
		double double6 = this.y * this.w;
		double double7 = this.y * this.z;
		double double8 = this.x * this.w;
		vector3d.x = double6 + double5 + double5 + double6;
		vector3d.y = double7 + double7 - double8 - double8;
		vector3d.z = double4 - double3 - double2 + double1;
		return vector3d;
	}

	public Vector4d transformPositiveZ(Vector4d vector4d) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.x * this.z;
		double double6 = this.y * this.w;
		double double7 = this.y * this.z;
		double double8 = this.x * this.w;
		vector4d.x = double6 + double5 + double5 + double6;
		vector4d.y = double7 + double7 - double8 - double8;
		vector4d.z = double4 - double3 - double2 + double1;
		return vector4d;
	}

	public Vector4d transformUnitPositiveZ(Vector4d vector4d) {
		double double1 = this.x * this.x;
		double double2 = this.y * this.y;
		double double3 = this.x * this.z;
		double double4 = this.y * this.z;
		double double5 = this.x * this.w;
		double double6 = this.y * this.w;
		vector4d.x = double3 + double6 + double3 + double6;
		vector4d.y = double4 + double4 - double5 - double5;
		vector4d.z = 1.0 - double1 - double1 - double2 - double2;
		return vector4d;
	}

	public Vector3d transformUnitPositiveZ(Vector3d vector3d) {
		double double1 = this.x * this.x;
		double double2 = this.y * this.y;
		double double3 = this.x * this.z;
		double double4 = this.y * this.z;
		double double5 = this.x * this.w;
		double double6 = this.y * this.w;
		vector3d.x = double3 + double6 + double3 + double6;
		vector3d.y = double4 + double4 - double5 - double5;
		vector3d.z = 1.0 - double1 - double1 - double2 - double2;
		return vector3d;
	}

	public Vector4d transform(Vector4d vector4d) {
		return this.transform((Vector4dc)vector4d, (Vector4d)vector4d);
	}

	public Vector4d transformInverse(Vector4d vector4d) {
		return this.transformInverse((Vector4dc)vector4d, (Vector4d)vector4d);
	}

	public Vector3d transform(Vector3dc vector3dc, Vector3d vector3d) {
		return this.transform(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3d);
	}

	public Vector3d transformInverse(Vector3dc vector3dc, Vector3d vector3d) {
		return this.transformInverse(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3d);
	}

	public Vector3d transform(double double1, double double2, double double3, Vector3d vector3d) {
		double double4 = this.x * this.x;
		double double5 = this.y * this.y;
		double double6 = this.z * this.z;
		double double7 = this.w * this.w;
		double double8 = this.x * this.y;
		double double9 = this.x * this.z;
		double double10 = this.y * this.z;
		double double11 = this.x * this.w;
		double double12 = this.z * this.w;
		double double13 = this.y * this.w;
		double double14 = 1.0 / (double4 + double5 + double6 + double7);
		return vector3d.set(Math.fma((double4 - double5 - double6 + double7) * double14, double1, Math.fma(2.0 * (double8 - double12) * double14, double2, 2.0 * (double9 + double13) * double14 * double3)), Math.fma(2.0 * (double8 + double12) * double14, double1, Math.fma((double5 - double4 - double6 + double7) * double14, double2, 2.0 * (double10 - double11) * double14 * double3)), Math.fma(2.0 * (double9 - double13) * double14, double1, Math.fma(2.0 * (double10 + double11) * double14, double2, (double6 - double4 - double5 + double7) * double14 * double3)));
	}

	public Vector3d transformInverse(double double1, double double2, double double3, Vector3d vector3d) {
		double double4 = 1.0 / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
		double double5 = this.x * double4;
		double double6 = this.y * double4;
		double double7 = this.z * double4;
		double double8 = this.w * double4;
		double double9 = double5 * double5;
		double double10 = double6 * double6;
		double double11 = double7 * double7;
		double double12 = double8 * double8;
		double double13 = double5 * double6;
		double double14 = double5 * double7;
		double double15 = double6 * double7;
		double double16 = double5 * double8;
		double double17 = double7 * double8;
		double double18 = double6 * double8;
		double double19 = 1.0 / (double9 + double10 + double11 + double12);
		return vector3d.set(Math.fma((double9 - double10 - double11 + double12) * double19, double1, Math.fma(2.0 * (double13 + double17) * double19, double2, 2.0 * (double14 - double18) * double19 * double3)), Math.fma(2.0 * (double13 - double17) * double19, double1, Math.fma((double10 - double9 - double11 + double12) * double19, double2, 2.0 * (double15 + double16) * double19 * double3)), Math.fma(2.0 * (double14 + double18) * double19, double1, Math.fma(2.0 * (double15 - double16) * double19, double2, (double11 - double9 - double10 + double12) * double19 * double3)));
	}

	public Vector4d transform(Vector4dc vector4dc, Vector4d vector4d) {
		return this.transform(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4d);
	}

	public Vector4d transformInverse(Vector4dc vector4dc, Vector4d vector4d) {
		return this.transformInverse(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4d);
	}

	public Vector4d transform(double double1, double double2, double double3, Vector4d vector4d) {
		double double4 = this.x * this.x;
		double double5 = this.y * this.y;
		double double6 = this.z * this.z;
		double double7 = this.w * this.w;
		double double8 = this.x * this.y;
		double double9 = this.x * this.z;
		double double10 = this.y * this.z;
		double double11 = this.x * this.w;
		double double12 = this.z * this.w;
		double double13 = this.y * this.w;
		double double14 = 1.0 / (double4 + double5 + double6 + double7);
		return vector4d.set(Math.fma((double4 - double5 - double6 + double7) * double14, double1, Math.fma(2.0 * (double8 - double12) * double14, double2, 2.0 * (double9 + double13) * double14 * double3)), Math.fma(2.0 * (double8 + double12) * double14, double1, Math.fma((double5 - double4 - double6 + double7) * double14, double2, 2.0 * (double10 - double11) * double14 * double3)), Math.fma(2.0 * (double9 - double13) * double14, double1, Math.fma(2.0 * (double10 + double11) * double14, double2, (double6 - double4 - double5 + double7) * double14 * double3)), vector4d.w);
	}

	public Vector4d transformInverse(double double1, double double2, double double3, Vector4d vector4d) {
		double double4 = 1.0 / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
		double double5 = this.x * double4;
		double double6 = this.y * double4;
		double double7 = this.z * double4;
		double double8 = this.w * double4;
		double double9 = double5 * double5;
		double double10 = double6 * double6;
		double double11 = double7 * double7;
		double double12 = double8 * double8;
		double double13 = double5 * double6;
		double double14 = double5 * double7;
		double double15 = double6 * double7;
		double double16 = double5 * double8;
		double double17 = double7 * double8;
		double double18 = double6 * double8;
		double double19 = 1.0 / (double9 + double10 + double11 + double12);
		return vector4d.set(Math.fma((double9 - double10 - double11 + double12) * double19, double1, Math.fma(2.0 * (double13 + double17) * double19, double2, 2.0 * (double14 - double18) * double19 * double3)), Math.fma(2.0 * (double13 - double17) * double19, double1, Math.fma((double10 - double9 - double11 + double12) * double19, double2, 2.0 * (double15 + double16) * double19 * double3)), Math.fma(2.0 * (double14 + double18) * double19, double1, Math.fma(2.0 * (double15 - double16) * double19, double2, (double11 - double9 - double10 + double12) * double19 * double3)));
	}

	public Vector3f transform(Vector3f vector3f) {
		return this.transform((double)vector3f.x, (double)vector3f.y, (double)vector3f.z, vector3f);
	}

	public Vector3f transformInverse(Vector3f vector3f) {
		return this.transformInverse((double)vector3f.x, (double)vector3f.y, (double)vector3f.z, vector3f);
	}

	public Vector4d transformUnit(Vector4d vector4d) {
		return this.transformUnit((Vector4dc)vector4d, (Vector4d)vector4d);
	}

	public Vector4d transformInverseUnit(Vector4d vector4d) {
		return this.transformInverseUnit((Vector4dc)vector4d, (Vector4d)vector4d);
	}

	public Vector3d transformUnit(Vector3dc vector3dc, Vector3d vector3d) {
		return this.transformUnit(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3d);
	}

	public Vector3d transformInverseUnit(Vector3dc vector3dc, Vector3d vector3d) {
		return this.transformInverseUnit(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3d);
	}

	public Vector3d transformUnit(double double1, double double2, double double3, Vector3d vector3d) {
		double double4 = this.x * this.x;
		double double5 = this.x * this.y;
		double double6 = this.x * this.z;
		double double7 = this.x * this.w;
		double double8 = this.y * this.y;
		double double9 = this.y * this.z;
		double double10 = this.y * this.w;
		double double11 = this.z * this.z;
		double double12 = this.z * this.w;
		return vector3d.set(Math.fma(Math.fma(-2.0, double8 + double11, 1.0), double1, Math.fma(2.0 * (double5 - double12), double2, 2.0 * (double6 + double10) * double3)), Math.fma(2.0 * (double5 + double12), double1, Math.fma(Math.fma(-2.0, double4 + double11, 1.0), double2, 2.0 * (double9 - double7) * double3)), Math.fma(2.0 * (double6 - double10), double1, Math.fma(2.0 * (double9 + double7), double2, Math.fma(-2.0, double4 + double8, 1.0) * double3)));
	}

	public Vector3d transformInverseUnit(double double1, double double2, double double3, Vector3d vector3d) {
		double double4 = this.x * this.x;
		double double5 = this.x * this.y;
		double double6 = this.x * this.z;
		double double7 = this.x * this.w;
		double double8 = this.y * this.y;
		double double9 = this.y * this.z;
		double double10 = this.y * this.w;
		double double11 = this.z * this.z;
		double double12 = this.z * this.w;
		return vector3d.set(Math.fma(Math.fma(-2.0, double8 + double11, 1.0), double1, Math.fma(2.0 * (double5 + double12), double2, 2.0 * (double6 - double10) * double3)), Math.fma(2.0 * (double5 - double12), double1, Math.fma(Math.fma(-2.0, double4 + double11, 1.0), double2, 2.0 * (double9 + double7) * double3)), Math.fma(2.0 * (double6 + double10), double1, Math.fma(2.0 * (double9 - double7), double2, Math.fma(-2.0, double4 + double8, 1.0) * double3)));
	}

	public Vector4d transformUnit(Vector4dc vector4dc, Vector4d vector4d) {
		return this.transformUnit(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4d);
	}

	public Vector4d transformInverseUnit(Vector4dc vector4dc, Vector4d vector4d) {
		return this.transformInverseUnit(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4d);
	}

	public Vector4d transformUnit(double double1, double double2, double double3, Vector4d vector4d) {
		double double4 = this.x * this.x;
		double double5 = this.x * this.y;
		double double6 = this.x * this.z;
		double double7 = this.x * this.w;
		double double8 = this.y * this.y;
		double double9 = this.y * this.z;
		double double10 = this.y * this.w;
		double double11 = this.z * this.z;
		double double12 = this.z * this.w;
		return vector4d.set(Math.fma(Math.fma(-2.0, double8 + double11, 1.0), double1, Math.fma(2.0 * (double5 - double12), double2, 2.0 * (double6 + double10) * double3)), Math.fma(2.0 * (double5 + double12), double1, Math.fma(Math.fma(-2.0, double4 + double11, 1.0), double2, 2.0 * (double9 - double7) * double3)), Math.fma(2.0 * (double6 - double10), double1, Math.fma(2.0 * (double9 + double7), double2, Math.fma(-2.0, double4 + double8, 1.0) * double3)), vector4d.w);
	}

	public Vector4d transformInverseUnit(double double1, double double2, double double3, Vector4d vector4d) {
		double double4 = this.x * this.x;
		double double5 = this.x * this.y;
		double double6 = this.x * this.z;
		double double7 = this.x * this.w;
		double double8 = this.y * this.y;
		double double9 = this.y * this.z;
		double double10 = this.y * this.w;
		double double11 = this.z * this.z;
		double double12 = this.z * this.w;
		return vector4d.set(Math.fma(Math.fma(-2.0, double8 + double11, 1.0), double1, Math.fma(2.0 * (double5 + double12), double2, 2.0 * (double6 - double10) * double3)), Math.fma(2.0 * (double5 - double12), double1, Math.fma(Math.fma(-2.0, double4 + double11, 1.0), double2, 2.0 * (double9 + double7) * double3)), Math.fma(2.0 * (double6 + double10), double1, Math.fma(2.0 * (double9 - double7), double2, Math.fma(-2.0, double4 + double8, 1.0) * double3)), vector4d.w);
	}

	public Vector3f transformUnit(Vector3f vector3f) {
		return this.transformUnit((double)vector3f.x, (double)vector3f.y, (double)vector3f.z, vector3f);
	}

	public Vector3f transformInverseUnit(Vector3f vector3f) {
		return this.transformInverseUnit((double)vector3f.x, (double)vector3f.y, (double)vector3f.z, vector3f);
	}

	public Vector3f transformPositiveX(Vector3f vector3f) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.z * this.w;
		double double6 = this.x * this.y;
		double double7 = this.x * this.z;
		double double8 = this.y * this.w;
		vector3f.x = (float)(double1 + double2 - double4 - double3);
		vector3f.y = (float)(double6 + double5 + double5 + double6);
		vector3f.z = (float)(double7 - double8 + double7 - double8);
		return vector3f;
	}

	public Vector4f transformPositiveX(Vector4f vector4f) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.z * this.w;
		double double6 = this.x * this.y;
		double double7 = this.x * this.z;
		double double8 = this.y * this.w;
		vector4f.x = (float)(double1 + double2 - double4 - double3);
		vector4f.y = (float)(double6 + double5 + double5 + double6);
		vector4f.z = (float)(double7 - double8 + double7 - double8);
		return vector4f;
	}

	public Vector3f transformUnitPositiveX(Vector3f vector3f) {
		double double1 = this.y * this.y;
		double double2 = this.z * this.z;
		double double3 = this.x * this.y;
		double double4 = this.x * this.z;
		double double5 = this.y * this.w;
		double double6 = this.z * this.w;
		vector3f.x = (float)(1.0 - double1 - double1 - double2 - double2);
		vector3f.y = (float)(double3 + double6 + double3 + double6);
		vector3f.z = (float)(double4 - double5 + double4 - double5);
		return vector3f;
	}

	public Vector4f transformUnitPositiveX(Vector4f vector4f) {
		double double1 = this.y * this.y;
		double double2 = this.z * this.z;
		double double3 = this.x * this.y;
		double double4 = this.x * this.z;
		double double5 = this.y * this.w;
		double double6 = this.z * this.w;
		vector4f.x = (float)(1.0 - double1 - double1 - double2 - double2);
		vector4f.y = (float)(double3 + double6 + double3 + double6);
		vector4f.z = (float)(double4 - double5 + double4 - double5);
		return vector4f;
	}

	public Vector3f transformPositiveY(Vector3f vector3f) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.z * this.w;
		double double6 = this.x * this.y;
		double double7 = this.y * this.z;
		double double8 = this.x * this.w;
		vector3f.x = (float)(-double5 + double6 - double5 + double6);
		vector3f.y = (float)(double3 - double4 + double1 - double2);
		vector3f.z = (float)(double7 + double7 + double8 + double8);
		return vector3f;
	}

	public Vector4f transformPositiveY(Vector4f vector4f) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.z * this.w;
		double double6 = this.x * this.y;
		double double7 = this.y * this.z;
		double double8 = this.x * this.w;
		vector4f.x = (float)(-double5 + double6 - double5 + double6);
		vector4f.y = (float)(double3 - double4 + double1 - double2);
		vector4f.z = (float)(double7 + double7 + double8 + double8);
		return vector4f;
	}

	public Vector4f transformUnitPositiveY(Vector4f vector4f) {
		double double1 = this.x * this.x;
		double double2 = this.z * this.z;
		double double3 = this.x * this.y;
		double double4 = this.y * this.z;
		double double5 = this.x * this.w;
		double double6 = this.z * this.w;
		vector4f.x = (float)(double3 - double6 + double3 - double6);
		vector4f.y = (float)(1.0 - double1 - double1 - double2 - double2);
		vector4f.z = (float)(double4 + double4 + double5 + double5);
		return vector4f;
	}

	public Vector3f transformUnitPositiveY(Vector3f vector3f) {
		double double1 = this.x * this.x;
		double double2 = this.z * this.z;
		double double3 = this.x * this.y;
		double double4 = this.y * this.z;
		double double5 = this.x * this.w;
		double double6 = this.z * this.w;
		vector3f.x = (float)(double3 - double6 + double3 - double6);
		vector3f.y = (float)(1.0 - double1 - double1 - double2 - double2);
		vector3f.z = (float)(double4 + double4 + double5 + double5);
		return vector3f;
	}

	public Vector3f transformPositiveZ(Vector3f vector3f) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.x * this.z;
		double double6 = this.y * this.w;
		double double7 = this.y * this.z;
		double double8 = this.x * this.w;
		vector3f.x = (float)(double6 + double5 + double5 + double6);
		vector3f.y = (float)(double7 + double7 - double8 - double8);
		vector3f.z = (float)(double4 - double3 - double2 + double1);
		return vector3f;
	}

	public Vector4f transformPositiveZ(Vector4f vector4f) {
		double double1 = this.w * this.w;
		double double2 = this.x * this.x;
		double double3 = this.y * this.y;
		double double4 = this.z * this.z;
		double double5 = this.x * this.z;
		double double6 = this.y * this.w;
		double double7 = this.y * this.z;
		double double8 = this.x * this.w;
		vector4f.x = (float)(double6 + double5 + double5 + double6);
		vector4f.y = (float)(double7 + double7 - double8 - double8);
		vector4f.z = (float)(double4 - double3 - double2 + double1);
		return vector4f;
	}

	public Vector4f transformUnitPositiveZ(Vector4f vector4f) {
		double double1 = this.x * this.x;
		double double2 = this.y * this.y;
		double double3 = this.x * this.z;
		double double4 = this.y * this.z;
		double double5 = this.x * this.w;
		double double6 = this.y * this.w;
		vector4f.x = (float)(double3 + double6 + double3 + double6);
		vector4f.y = (float)(double4 + double4 - double5 - double5);
		vector4f.z = (float)(1.0 - double1 - double1 - double2 - double2);
		return vector4f;
	}

	public Vector3f transformUnitPositiveZ(Vector3f vector3f) {
		double double1 = this.x * this.x;
		double double2 = this.y * this.y;
		double double3 = this.x * this.z;
		double double4 = this.y * this.z;
		double double5 = this.x * this.w;
		double double6 = this.y * this.w;
		vector3f.x = (float)(double3 + double6 + double3 + double6);
		vector3f.y = (float)(double4 + double4 - double5 - double5);
		vector3f.z = (float)(1.0 - double1 - double1 - double2 - double2);
		return vector3f;
	}

	public Vector4f transform(Vector4f vector4f) {
		return this.transform((Vector4fc)vector4f, (Vector4f)vector4f);
	}

	public Vector4f transformInverse(Vector4f vector4f) {
		return this.transformInverse((Vector4fc)vector4f, (Vector4f)vector4f);
	}

	public Vector3f transform(Vector3fc vector3fc, Vector3f vector3f) {
		return this.transform((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), vector3f);
	}

	public Vector3f transformInverse(Vector3fc vector3fc, Vector3f vector3f) {
		return this.transformInverse((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), vector3f);
	}

	public Vector3f transform(double double1, double double2, double double3, Vector3f vector3f) {
		double double4 = this.x * this.x;
		double double5 = this.y * this.y;
		double double6 = this.z * this.z;
		double double7 = this.w * this.w;
		double double8 = this.x * this.y;
		double double9 = this.x * this.z;
		double double10 = this.y * this.z;
		double double11 = this.x * this.w;
		double double12 = this.z * this.w;
		double double13 = this.y * this.w;
		double double14 = 1.0 / (double4 + double5 + double6 + double7);
		return vector3f.set(Math.fma((double4 - double5 - double6 + double7) * double14, double1, Math.fma(2.0 * (double8 - double12) * double14, double2, 2.0 * (double9 + double13) * double14 * double3)), Math.fma(2.0 * (double8 + double12) * double14, double1, Math.fma((double5 - double4 - double6 + double7) * double14, double2, 2.0 * (double10 - double11) * double14 * double3)), Math.fma(2.0 * (double9 - double13) * double14, double1, Math.fma(2.0 * (double10 + double11) * double14, double2, (double6 - double4 - double5 + double7) * double14 * double3)));
	}

	public Vector3f transformInverse(double double1, double double2, double double3, Vector3f vector3f) {
		double double4 = 1.0 / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
		double double5 = this.x * double4;
		double double6 = this.y * double4;
		double double7 = this.z * double4;
		double double8 = this.w * double4;
		double double9 = double5 * double5;
		double double10 = double6 * double6;
		double double11 = double7 * double7;
		double double12 = double8 * double8;
		double double13 = double5 * double6;
		double double14 = double5 * double7;
		double double15 = double6 * double7;
		double double16 = double5 * double8;
		double double17 = double7 * double8;
		double double18 = double6 * double8;
		double double19 = 1.0 / (double9 + double10 + double11 + double12);
		return vector3f.set(Math.fma((double9 - double10 - double11 + double12) * double19, double1, Math.fma(2.0 * (double13 + double17) * double19, double2, 2.0 * (double14 - double18) * double19 * double3)), Math.fma(2.0 * (double13 - double17) * double19, double1, Math.fma((double10 - double9 - double11 + double12) * double19, double2, 2.0 * (double15 + double16) * double19 * double3)), Math.fma(2.0 * (double14 + double18) * double19, double1, Math.fma(2.0 * (double15 - double16) * double19, double2, (double11 - double9 - double10 + double12) * double19 * double3)));
	}

	public Vector4f transform(Vector4fc vector4fc, Vector4f vector4f) {
		return this.transform((double)vector4fc.x(), (double)vector4fc.y(), (double)vector4fc.z(), vector4f);
	}

	public Vector4f transformInverse(Vector4fc vector4fc, Vector4f vector4f) {
		return this.transformInverse((double)vector4fc.x(), (double)vector4fc.y(), (double)vector4fc.z(), vector4f);
	}

	public Vector4f transform(double double1, double double2, double double3, Vector4f vector4f) {
		double double4 = this.x * this.x;
		double double5 = this.y * this.y;
		double double6 = this.z * this.z;
		double double7 = this.w * this.w;
		double double8 = this.x * this.y;
		double double9 = this.x * this.z;
		double double10 = this.y * this.z;
		double double11 = this.x * this.w;
		double double12 = this.z * this.w;
		double double13 = this.y * this.w;
		double double14 = 1.0 / (double4 + double5 + double6 + double7);
		return vector4f.set((float)Math.fma((double4 - double5 - double6 + double7) * double14, double1, Math.fma(2.0 * (double8 - double12) * double14, double2, 2.0 * (double9 + double13) * double14 * double3)), (float)Math.fma(2.0 * (double8 + double12) * double14, double1, Math.fma((double5 - double4 - double6 + double7) * double14, double2, 2.0 * (double10 - double11) * double14 * double3)), (float)Math.fma(2.0 * (double9 - double13) * double14, double1, Math.fma(2.0 * (double10 + double11) * double14, double2, (double6 - double4 - double5 + double7) * double14 * double3)), vector4f.w);
	}

	public Vector4f transformInverse(double double1, double double2, double double3, Vector4f vector4f) {
		double double4 = 1.0 / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
		double double5 = this.x * double4;
		double double6 = this.y * double4;
		double double7 = this.z * double4;
		double double8 = this.w * double4;
		double double9 = double5 * double5;
		double double10 = double6 * double6;
		double double11 = double7 * double7;
		double double12 = double8 * double8;
		double double13 = double5 * double6;
		double double14 = double5 * double7;
		double double15 = double6 * double7;
		double double16 = double5 * double8;
		double double17 = double7 * double8;
		double double18 = double6 * double8;
		double double19 = 1.0 / (double9 + double10 + double11 + double12);
		return vector4f.set(Math.fma((double9 - double10 - double11 + double12) * double19, double1, Math.fma(2.0 * (double13 + double17) * double19, double2, 2.0 * (double14 - double18) * double19 * double3)), Math.fma(2.0 * (double13 - double17) * double19, double1, Math.fma((double10 - double9 - double11 + double12) * double19, double2, 2.0 * (double15 + double16) * double19 * double3)), Math.fma(2.0 * (double14 + double18) * double19, double1, Math.fma(2.0 * (double15 - double16) * double19, double2, (double11 - double9 - double10 + double12) * double19 * double3)), (double)vector4f.w);
	}

	public Vector4f transformUnit(Vector4f vector4f) {
		return this.transformUnit((Vector4fc)vector4f, (Vector4f)vector4f);
	}

	public Vector4f transformInverseUnit(Vector4f vector4f) {
		return this.transformInverseUnit((Vector4fc)vector4f, (Vector4f)vector4f);
	}

	public Vector3f transformUnit(Vector3fc vector3fc, Vector3f vector3f) {
		return this.transformUnit((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), vector3f);
	}

	public Vector3f transformInverseUnit(Vector3fc vector3fc, Vector3f vector3f) {
		return this.transformInverseUnit((double)vector3fc.x(), (double)vector3fc.y(), (double)vector3fc.z(), vector3f);
	}

	public Vector3f transformUnit(double double1, double double2, double double3, Vector3f vector3f) {
		double double4 = this.x * this.x;
		double double5 = this.x * this.y;
		double double6 = this.x * this.z;
		double double7 = this.x * this.w;
		double double8 = this.y * this.y;
		double double9 = this.y * this.z;
		double double10 = this.y * this.w;
		double double11 = this.z * this.z;
		double double12 = this.z * this.w;
		return vector3f.set((float)Math.fma(Math.fma(-2.0, double8 + double11, 1.0), double1, Math.fma(2.0 * (double5 - double12), double2, 2.0 * (double6 + double10) * double3)), (float)Math.fma(2.0 * (double5 + double12), double1, Math.fma(Math.fma(-2.0, double4 + double11, 1.0), double2, 2.0 * (double9 - double7) * double3)), (float)Math.fma(2.0 * (double6 - double10), double1, Math.fma(2.0 * (double9 + double7), double2, Math.fma(-2.0, double4 + double8, 1.0) * double3)));
	}

	public Vector3f transformInverseUnit(double double1, double double2, double double3, Vector3f vector3f) {
		double double4 = this.x * this.x;
		double double5 = this.x * this.y;
		double double6 = this.x * this.z;
		double double7 = this.x * this.w;
		double double8 = this.y * this.y;
		double double9 = this.y * this.z;
		double double10 = this.y * this.w;
		double double11 = this.z * this.z;
		double double12 = this.z * this.w;
		return vector3f.set((float)Math.fma(Math.fma(-2.0, double8 + double11, 1.0), double1, Math.fma(2.0 * (double5 + double12), double2, 2.0 * (double6 - double10) * double3)), (float)Math.fma(2.0 * (double5 - double12), double1, Math.fma(Math.fma(-2.0, double4 + double11, 1.0), double2, 2.0 * (double9 + double7) * double3)), (float)Math.fma(2.0 * (double6 + double10), double1, Math.fma(2.0 * (double9 - double7), double2, Math.fma(-2.0, double4 + double8, 1.0) * double3)));
	}

	public Vector4f transformUnit(Vector4fc vector4fc, Vector4f vector4f) {
		return this.transformUnit((double)vector4fc.x(), (double)vector4fc.y(), (double)vector4fc.z(), vector4f);
	}

	public Vector4f transformInverseUnit(Vector4fc vector4fc, Vector4f vector4f) {
		return this.transformInverseUnit((double)vector4fc.x(), (double)vector4fc.y(), (double)vector4fc.z(), vector4f);
	}

	public Vector4f transformUnit(double double1, double double2, double double3, Vector4f vector4f) {
		double double4 = this.x * this.x;
		double double5 = this.x * this.y;
		double double6 = this.x * this.z;
		double double7 = this.x * this.w;
		double double8 = this.y * this.y;
		double double9 = this.y * this.z;
		double double10 = this.y * this.w;
		double double11 = this.z * this.z;
		double double12 = this.z * this.w;
		return vector4f.set((float)Math.fma(Math.fma(-2.0, double8 + double11, 1.0), double1, Math.fma(2.0 * (double5 - double12), double2, 2.0 * (double6 + double10) * double3)), (float)Math.fma(2.0 * (double5 + double12), double1, Math.fma(Math.fma(-2.0, double4 + double11, 1.0), double2, 2.0 * (double9 - double7) * double3)), (float)Math.fma(2.0 * (double6 - double10), double1, Math.fma(2.0 * (double9 + double7), double2, Math.fma(-2.0, double4 + double8, 1.0) * double3)));
	}

	public Vector4f transformInverseUnit(double double1, double double2, double double3, Vector4f vector4f) {
		double double4 = this.x * this.x;
		double double5 = this.x * this.y;
		double double6 = this.x * this.z;
		double double7 = this.x * this.w;
		double double8 = this.y * this.y;
		double double9 = this.y * this.z;
		double double10 = this.y * this.w;
		double double11 = this.z * this.z;
		double double12 = this.z * this.w;
		return vector4f.set((float)Math.fma(Math.fma(-2.0, double8 + double11, 1.0), double1, Math.fma(2.0 * (double5 + double12), double2, 2.0 * (double6 - double10) * double3)), (float)Math.fma(2.0 * (double5 - double12), double1, Math.fma(Math.fma(-2.0, double4 + double11, 1.0), double2, 2.0 * (double9 + double7) * double3)), (float)Math.fma(2.0 * (double6 + double10), double1, Math.fma(2.0 * (double9 - double7), double2, Math.fma(-2.0, double4 + double8, 1.0) * double3)));
	}

	public Quaterniond invert(Quaterniond quaterniond) {
		double double1 = 1.0 / this.lengthSquared();
		quaterniond.x = -this.x * double1;
		quaterniond.y = -this.y * double1;
		quaterniond.z = -this.z * double1;
		quaterniond.w = this.w * double1;
		return quaterniond;
	}

	public Quaterniond invert() {
		return this.invert(this);
	}

	public Quaterniond div(Quaterniondc quaterniondc, Quaterniond quaterniond) {
		double double1 = 1.0 / Math.fma(quaterniondc.x(), quaterniondc.x(), Math.fma(quaterniondc.y(), quaterniondc.y(), Math.fma(quaterniondc.z(), quaterniondc.z(), quaterniondc.w() * quaterniondc.w())));
		double double2 = -quaterniondc.x() * double1;
		double double3 = -quaterniondc.y() * double1;
		double double4 = -quaterniondc.z() * double1;
		double double5 = quaterniondc.w() * double1;
		return quaterniond.set(Math.fma(this.w, double2, Math.fma(this.x, double5, Math.fma(this.y, double4, -this.z * double3))), Math.fma(this.w, double3, Math.fma(-this.x, double4, Math.fma(this.y, double5, this.z * double2))), Math.fma(this.w, double4, Math.fma(this.x, double3, Math.fma(-this.y, double2, this.z * double5))), Math.fma(this.w, double5, Math.fma(-this.x, double2, Math.fma(-this.y, double3, -this.z * double4))));
	}

	public Quaterniond div(Quaterniondc quaterniondc) {
		return this.div(quaterniondc, this);
	}

	public Quaterniond conjugate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	public Quaterniond conjugate(Quaterniond quaterniond) {
		quaterniond.x = -this.x;
		quaterniond.y = -this.y;
		quaterniond.z = -this.z;
		quaterniond.w = this.w;
		return quaterniond;
	}

	public Quaterniond identity() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
		this.w = 1.0;
		return this;
	}

	public double lengthSquared() {
		return Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
	}

	public Quaterniond rotationXYZ(double double1, double double2, double double3) {
		double double4 = Math.sin(double1 * 0.5);
		double double5 = Math.cosFromSin(double4, double1 * 0.5);
		double double6 = Math.sin(double2 * 0.5);
		double double7 = Math.cosFromSin(double6, double2 * 0.5);
		double double8 = Math.sin(double3 * 0.5);
		double double9 = Math.cosFromSin(double8, double3 * 0.5);
		double double10 = double7 * double9;
		double double11 = double6 * double8;
		double double12 = double6 * double9;
		double double13 = double7 * double8;
		this.w = double5 * double10 - double4 * double11;
		this.x = double4 * double10 + double5 * double11;
		this.y = double5 * double12 - double4 * double13;
		this.z = double5 * double13 + double4 * double12;
		return this;
	}

	public Quaterniond rotationZYX(double double1, double double2, double double3) {
		double double4 = Math.sin(double3 * 0.5);
		double double5 = Math.cosFromSin(double4, double3 * 0.5);
		double double6 = Math.sin(double2 * 0.5);
		double double7 = Math.cosFromSin(double6, double2 * 0.5);
		double double8 = Math.sin(double1 * 0.5);
		double double9 = Math.cosFromSin(double8, double1 * 0.5);
		double double10 = double7 * double9;
		double double11 = double6 * double8;
		double double12 = double6 * double9;
		double double13 = double7 * double8;
		this.w = double5 * double10 + double4 * double11;
		this.x = double4 * double10 - double5 * double11;
		this.y = double5 * double12 + double4 * double13;
		this.z = double5 * double13 - double4 * double12;
		return this;
	}

	public Quaterniond rotationYXZ(double double1, double double2, double double3) {
		double double4 = Math.sin(double2 * 0.5);
		double double5 = Math.cosFromSin(double4, double2 * 0.5);
		double double6 = Math.sin(double1 * 0.5);
		double double7 = Math.cosFromSin(double6, double1 * 0.5);
		double double8 = Math.sin(double3 * 0.5);
		double double9 = Math.cosFromSin(double8, double3 * 0.5);
		double double10 = double7 * double4;
		double double11 = double6 * double5;
		double double12 = double6 * double4;
		double double13 = double7 * double5;
		this.x = double10 * double9 + double11 * double8;
		this.y = double11 * double9 - double10 * double8;
		this.z = double13 * double8 - double12 * double9;
		this.w = double13 * double9 + double12 * double8;
		return this;
	}

	public Quaterniond slerp(Quaterniondc quaterniondc, double double1) {
		return this.slerp(quaterniondc, double1, this);
	}

	public Quaterniond slerp(Quaterniondc quaterniondc, double double1, Quaterniond quaterniond) {
		double double2 = Math.fma(this.x, quaterniondc.x(), Math.fma(this.y, quaterniondc.y(), Math.fma(this.z, quaterniondc.z(), this.w * quaterniondc.w())));
		double double3 = Math.abs(double2);
		double double4;
		double double5;
		if (1.0 - double3 > 1.0E-6) {
			double double6 = 1.0 - double3 * double3;
			double double7 = Math.invsqrt(double6);
			double double8 = Math.atan2(double6 * double7, double3);
			double4 = Math.sin((1.0 - double1) * double8) * double7;
			double5 = Math.sin(double1 * double8) * double7;
		} else {
			double4 = 1.0 - double1;
			double5 = double1;
		}

		double5 = double2 >= 0.0 ? double5 : -double5;
		quaterniond.x = Math.fma(double4, this.x, double5 * quaterniondc.x());
		quaterniond.y = Math.fma(double4, this.y, double5 * quaterniondc.y());
		quaterniond.z = Math.fma(double4, this.z, double5 * quaterniondc.z());
		quaterniond.w = Math.fma(double4, this.w, double5 * quaterniondc.w());
		return quaterniond;
	}

	public static Quaterniondc slerp(Quaterniond[] quaterniondArray, double[] doubleArray, Quaterniond quaterniond) {
		quaterniond.set((Quaterniondc)quaterniondArray[0]);
		double double1 = doubleArray[0];
		for (int int1 = 1; int1 < quaterniondArray.length; ++int1) {
			double double2 = doubleArray[int1];
			double double3 = double2 / (double1 + double2);
			double1 += double2;
			quaterniond.slerp(quaterniondArray[int1], double3);
		}

		return quaterniond;
	}

	public Quaterniond scale(double double1) {
		return this.scale(double1, this);
	}

	public Quaterniond scale(double double1, Quaterniond quaterniond) {
		double double2 = Math.sqrt(double1);
		quaterniond.x = double2 * this.x;
		quaterniond.y = double2 * this.y;
		quaterniond.z = double2 * this.z;
		quaterniond.w = double2 * this.w;
		return quaterniond;
	}

	public Quaterniond scaling(double double1) {
		double double2 = Math.sqrt(double1);
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
		this.w = double2;
		return this;
	}

	public Quaterniond integrate(double double1, double double2, double double3, double double4) {
		return this.integrate(double1, double2, double3, double4, this);
	}

	public Quaterniond integrate(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
		double double5 = double1 * double2 * 0.5;
		double double6 = double1 * double3 * 0.5;
		double double7 = double1 * double4 * 0.5;
		double double8 = double5 * double5 + double6 * double6 + double7 * double7;
		double double9;
		double double10;
		if (double8 * double8 / 24.0 < 1.0E-8) {
			double10 = 1.0 - double8 * 0.5;
			double9 = 1.0 - double8 / 6.0;
		} else {
			double double11 = Math.sqrt(double8);
			double double12 = Math.sin(double11);
			double9 = double12 / double11;
			double10 = Math.cosFromSin(double12, double11);
		}

		double double13 = double5 * double9;
		double double14 = double6 * double9;
		double double15 = double7 * double9;
		return quaterniond.set(Math.fma(double10, this.x, Math.fma(double13, this.w, Math.fma(double14, this.z, -double15 * this.y))), Math.fma(double10, this.y, Math.fma(-double13, this.z, Math.fma(double14, this.w, double15 * this.x))), Math.fma(double10, this.z, Math.fma(double13, this.y, Math.fma(-double14, this.x, double15 * this.w))), Math.fma(double10, this.w, Math.fma(-double13, this.x, Math.fma(-double14, this.y, -double15 * this.z))));
	}

	public Quaterniond nlerp(Quaterniondc quaterniondc, double double1) {
		return this.nlerp(quaterniondc, double1, this);
	}

	public Quaterniond nlerp(Quaterniondc quaterniondc, double double1, Quaterniond quaterniond) {
		double double2 = Math.fma(this.x, quaterniondc.x(), Math.fma(this.y, quaterniondc.y(), Math.fma(this.z, quaterniondc.z(), this.w * quaterniondc.w())));
		double double3 = 1.0 - double1;
		double double4 = double2 >= 0.0 ? double1 : -double1;
		quaterniond.x = Math.fma(double3, this.x, double4 * quaterniondc.x());
		quaterniond.y = Math.fma(double3, this.y, double4 * quaterniondc.y());
		quaterniond.z = Math.fma(double3, this.z, double4 * quaterniondc.z());
		quaterniond.w = Math.fma(double3, this.w, double4 * quaterniondc.w());
		double double5 = Math.invsqrt(Math.fma(quaterniond.x, quaterniond.x, Math.fma(quaterniond.y, quaterniond.y, Math.fma(quaterniond.z, quaterniond.z, quaterniond.w * quaterniond.w))));
		quaterniond.x *= double5;
		quaterniond.y *= double5;
		quaterniond.z *= double5;
		quaterniond.w *= double5;
		return quaterniond;
	}

	public static Quaterniondc nlerp(Quaterniond[] quaterniondArray, double[] doubleArray, Quaterniond quaterniond) {
		quaterniond.set((Quaterniondc)quaterniondArray[0]);
		double double1 = doubleArray[0];
		for (int int1 = 1; int1 < quaterniondArray.length; ++int1) {
			double double2 = doubleArray[int1];
			double double3 = double2 / (double1 + double2);
			double1 += double2;
			quaterniond.nlerp(quaterniondArray[int1], double3);
		}

		return quaterniond;
	}

	public Quaterniond nlerpIterative(Quaterniondc quaterniondc, double double1, double double2, Quaterniond quaterniond) {
		double double3 = this.x;
		double double4 = this.y;
		double double5 = this.z;
		double double6 = this.w;
		double double7 = quaterniondc.x();
		double double8 = quaterniondc.y();
		double double9 = quaterniondc.z();
		double double10 = quaterniondc.w();
		double double11 = Math.fma(double3, double7, Math.fma(double4, double8, Math.fma(double5, double9, double6 * double10)));
		double double12 = Math.abs(double11);
		if (0.999999 < double12) {
			return quaterniond.set((Quaterniondc)this);
		} else {
			double double13;
			double double14;
			double double15;
			for (double13 = double1; double12 < double2; double12 = Math.abs(double11)) {
				double14 = 0.5;
				double15 = double11 >= 0.0 ? 0.5 : -0.5;
				float float1;
				if (double13 < 0.5) {
					double7 = Math.fma(double14, double7, double15 * double3);
					double8 = Math.fma(double14, double8, double15 * double4);
					double9 = Math.fma(double14, double9, double15 * double5);
					double10 = Math.fma(double14, double10, double15 * double6);
					float1 = (float)Math.invsqrt(Math.fma(double7, double7, Math.fma(double8, double8, Math.fma(double9, double9, double10 * double10))));
					double7 *= (double)float1;
					double8 *= (double)float1;
					double9 *= (double)float1;
					double10 *= (double)float1;
					double13 += double13;
				} else {
					double3 = Math.fma(double14, double3, double15 * double7);
					double4 = Math.fma(double14, double4, double15 * double8);
					double5 = Math.fma(double14, double5, double15 * double9);
					double6 = Math.fma(double14, double6, double15 * double10);
					float1 = (float)Math.invsqrt(Math.fma(double3, double3, Math.fma(double4, double4, Math.fma(double5, double5, double6 * double6))));
					double3 *= (double)float1;
					double4 *= (double)float1;
					double5 *= (double)float1;
					double6 *= (double)float1;
					double13 = double13 + double13 - 1.0;
				}

				double11 = Math.fma(double3, double7, Math.fma(double4, double8, Math.fma(double5, double9, double6 * double10)));
			}

			double14 = 1.0 - double13;
			double15 = double11 >= 0.0 ? double13 : -double13;
			double double16 = Math.fma(double14, double3, double15 * double7);
			double double17 = Math.fma(double14, double4, double15 * double8);
			double double18 = Math.fma(double14, double5, double15 * double9);
			double double19 = Math.fma(double14, double6, double15 * double10);
			double double20 = Math.invsqrt(Math.fma(double16, double16, Math.fma(double17, double17, Math.fma(double18, double18, double19 * double19))));
			quaterniond.x = double16 * double20;
			quaterniond.y = double17 * double20;
			quaterniond.z = double18 * double20;
			quaterniond.w = double19 * double20;
			return quaterniond;
		}
	}

	public Quaterniond nlerpIterative(Quaterniondc quaterniondc, double double1, double double2) {
		return this.nlerpIterative(quaterniondc, double1, double2, this);
	}

	public static Quaterniond nlerpIterative(Quaterniondc[] quaterniondcArray, double[] doubleArray, double double1, Quaterniond quaterniond) {
		quaterniond.set(quaterniondcArray[0]);
		double double2 = doubleArray[0];
		for (int int1 = 1; int1 < quaterniondcArray.length; ++int1) {
			double double3 = doubleArray[int1];
			double double4 = double3 / (double2 + double3);
			double2 += double3;
			quaterniond.nlerpIterative(quaterniondcArray[int1], double4, double1);
		}

		return quaterniond;
	}

	public Quaterniond lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.lookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), this);
	}

	public Quaterniond lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Quaterniond quaterniond) {
		return this.lookAlong(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), quaterniond);
	}

	public Quaterniond lookAlong(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.lookAlong(double1, double2, double3, double4, double5, double6, this);
	}

	public Quaterniond lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Quaterniond quaterniond) {
		double double7 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = -double1 * double7;
		double double9 = -double2 * double7;
		double double10 = -double3 * double7;
		double double11 = double5 * double10 - double6 * double9;
		double double12 = double6 * double8 - double4 * double10;
		double double13 = double4 * double9 - double5 * double8;
		double double14 = Math.invsqrt(double11 * double11 + double12 * double12 + double13 * double13);
		double11 *= double14;
		double12 *= double14;
		double13 *= double14;
		double double15 = double9 * double13 - double10 * double12;
		double double16 = double10 * double11 - double8 * double13;
		double double17 = double8 * double12 - double9 * double11;
		double double18 = double11 + double16 + double10;
		double double19;
		double double20;
		double double21;
		double double22;
		double double23;
		if (double18 >= 0.0) {
			double23 = Math.sqrt(double18 + 1.0);
			double22 = double23 * 0.5;
			double23 = 0.5 / double23;
			double19 = (double9 - double17) * double23;
			double20 = (double13 - double8) * double23;
			double21 = (double15 - double12) * double23;
		} else if (double11 > double16 && double11 > double10) {
			double23 = Math.sqrt(1.0 + double11 - double16 - double10);
			double19 = double23 * 0.5;
			double23 = 0.5 / double23;
			double20 = (double12 + double15) * double23;
			double21 = (double8 + double13) * double23;
			double22 = (double9 - double17) * double23;
		} else if (double16 > double10) {
			double23 = Math.sqrt(1.0 + double16 - double11 - double10);
			double20 = double23 * 0.5;
			double23 = 0.5 / double23;
			double19 = (double12 + double15) * double23;
			double21 = (double17 + double9) * double23;
			double22 = (double13 - double8) * double23;
		} else {
			double23 = Math.sqrt(1.0 + double10 - double11 - double16);
			double21 = double23 * 0.5;
			double23 = 0.5 / double23;
			double19 = (double8 + double13) * double23;
			double20 = (double17 + double9) * double23;
			double22 = (double15 - double12) * double23;
		}

		return quaterniond.set(Math.fma(this.w, double19, Math.fma(this.x, double22, Math.fma(this.y, double21, -this.z * double20))), Math.fma(this.w, double20, Math.fma(-this.x, double21, Math.fma(this.y, double22, this.z * double19))), Math.fma(this.w, double21, Math.fma(this.x, double20, Math.fma(-this.y, double19, this.z * double22))), Math.fma(this.w, double22, Math.fma(-this.x, double19, Math.fma(-this.y, double20, -this.z * double21))));
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format(this.x, numberFormat);
		return "(" + string + " " + Runtime.format(this.y, numberFormat) + " " + Runtime.format(this.z, numberFormat) + " " + Runtime.format(this.w, numberFormat) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeDouble(this.x);
		objectOutput.writeDouble(this.y);
		objectOutput.writeDouble(this.z);
		objectOutput.writeDouble(this.w);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readDouble();
		this.y = objectInput.readDouble();
		this.z = objectInput.readDouble();
		this.w = objectInput.readDouble();
	}

	public int hashCode() {
		byte byte1 = 1;
		long long1 = Double.doubleToLongBits(this.w);
		int int1 = 31 * byte1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.x);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.y);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.z);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		return int1;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object == null) {
			return false;
		} else if (this.getClass() != object.getClass()) {
			return false;
		} else {
			Quaterniond quaterniond = (Quaterniond)object;
			if (Double.doubleToLongBits(this.w) != Double.doubleToLongBits(quaterniond.w)) {
				return false;
			} else if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(quaterniond.x)) {
				return false;
			} else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(quaterniond.y)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(quaterniond.z);
			}
		}
	}

	public Quaterniond difference(Quaterniondc quaterniondc) {
		return this.difference(quaterniondc, this);
	}

	public Quaterniond difference(Quaterniondc quaterniondc, Quaterniond quaterniond) {
		double double1 = 1.0 / this.lengthSquared();
		double double2 = -this.x * double1;
		double double3 = -this.y * double1;
		double double4 = -this.z * double1;
		double double5 = this.w * double1;
		quaterniond.set(Math.fma(double5, quaterniondc.x(), Math.fma(double2, quaterniondc.w(), Math.fma(double3, quaterniondc.z(), -double4 * quaterniondc.y()))), Math.fma(double5, quaterniondc.y(), Math.fma(-double2, quaterniondc.z(), Math.fma(double3, quaterniondc.w(), double4 * quaterniondc.x()))), Math.fma(double5, quaterniondc.z(), Math.fma(double2, quaterniondc.y(), Math.fma(-double3, quaterniondc.x(), double4 * quaterniondc.w()))), Math.fma(double5, quaterniondc.w(), Math.fma(-double2, quaterniondc.x(), Math.fma(-double3, quaterniondc.y(), -double4 * quaterniondc.z()))));
		return quaterniond;
	}

	public Quaterniond rotationTo(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = Math.invsqrt(Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3)));
		double double8 = Math.invsqrt(Math.fma(double4, double4, Math.fma(double5, double5, double6 * double6)));
		double double9 = double1 * double7;
		double double10 = double2 * double7;
		double double11 = double3 * double7;
		double double12 = double4 * double8;
		double double13 = double5 * double8;
		double double14 = double6 * double8;
		double double15 = double9 * double12 + double10 * double13 + double11 * double14;
		double double16;
		double double17;
		double double18;
		double double19;
		if (double15 < -0.999999) {
			double16 = double10;
			double17 = -double9;
			double18 = 0.0;
			double19 = 0.0;
			if (double10 * double10 + double17 * double17 == 0.0) {
				double16 = 0.0;
				double17 = double11;
				double18 = -double10;
				double19 = 0.0;
			}

			this.x = double16;
			this.y = double17;
			this.z = double18;
			this.w = 0.0;
		} else {
			double double20 = Math.sqrt((1.0 + double15) * 2.0);
			double double21 = 1.0 / double20;
			double double22 = double10 * double14 - double11 * double13;
			double double23 = double11 * double12 - double9 * double14;
			double double24 = double9 * double13 - double10 * double12;
			double16 = double22 * double21;
			double17 = double23 * double21;
			double18 = double24 * double21;
			double19 = double20 * 0.5;
			double double25 = Math.invsqrt(Math.fma(double16, double16, Math.fma(double17, double17, Math.fma(double18, double18, double19 * double19))));
			this.x = double16 * double25;
			this.y = double17 * double25;
			this.z = double18 * double25;
			this.w = double19 * double25;
		}

		return this;
	}

	public Quaterniond rotationTo(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.rotationTo(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Quaterniond rotateTo(double double1, double double2, double double3, double double4, double double5, double double6, Quaterniond quaterniond) {
		double double7 = Math.invsqrt(Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3)));
		double double8 = Math.invsqrt(Math.fma(double4, double4, Math.fma(double5, double5, double6 * double6)));
		double double9 = double1 * double7;
		double double10 = double2 * double7;
		double double11 = double3 * double7;
		double double12 = double4 * double8;
		double double13 = double5 * double8;
		double double14 = double6 * double8;
		double double15 = double9 * double12 + double10 * double13 + double11 * double14;
		double double16;
		double double17;
		double double18;
		double double19;
		if (double15 < -0.999999) {
			double16 = double10;
			double17 = -double9;
			double18 = 0.0;
			double19 = 0.0;
			if (double10 * double10 + double17 * double17 == 0.0) {
				double16 = 0.0;
				double17 = double11;
				double18 = -double10;
				double19 = 0.0;
			}
		} else {
			double double20 = Math.sqrt((1.0 + double15) * 2.0);
			double double21 = 1.0 / double20;
			double double22 = double10 * double14 - double11 * double13;
			double double23 = double11 * double12 - double9 * double14;
			double double24 = double9 * double13 - double10 * double12;
			double16 = double22 * double21;
			double17 = double23 * double21;
			double18 = double24 * double21;
			double19 = double20 * 0.5;
			double double25 = Math.invsqrt(Math.fma(double16, double16, Math.fma(double17, double17, Math.fma(double18, double18, double19 * double19))));
			double16 *= double25;
			double17 *= double25;
			double18 *= double25;
			double19 *= double25;
		}

		return quaterniond.set(Math.fma(this.w, double16, Math.fma(this.x, double19, Math.fma(this.y, double18, -this.z * double17))), Math.fma(this.w, double17, Math.fma(-this.x, double18, Math.fma(this.y, double19, this.z * double16))), Math.fma(this.w, double18, Math.fma(this.x, double17, Math.fma(-this.y, double16, this.z * double19))), Math.fma(this.w, double19, Math.fma(-this.x, double16, Math.fma(-this.y, double17, -this.z * double18))));
	}

	public Quaterniond rotationAxis(AxisAngle4f axisAngle4f) {
		return this.rotationAxis((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z);
	}

	public Quaterniond rotationAxis(double double1, double double2, double double3, double double4) {
		double double5 = double1 / 2.0;
		double double6 = Math.sin(double5);
		double double7 = Math.invsqrt(double2 * double2 + double3 * double3 + double4 * double4);
		return this.set(double2 * double7 * double6, double3 * double7 * double6, double4 * double7 * double6, Math.cosFromSin(double6, double5));
	}

	public Quaterniond rotationX(double double1) {
		double double2 = Math.sin(double1 * 0.5);
		double double3 = Math.cosFromSin(double2, double1 * 0.5);
		return this.set(double2, 0.0, double3, 0.0);
	}

	public Quaterniond rotationY(double double1) {
		double double2 = Math.sin(double1 * 0.5);
		double double3 = Math.cosFromSin(double2, double1 * 0.5);
		return this.set(0.0, double2, 0.0, double3);
	}

	public Quaterniond rotationZ(double double1) {
		double double2 = Math.sin(double1 * 0.5);
		double double3 = Math.cosFromSin(double2, double1 * 0.5);
		return this.set(0.0, 0.0, double2, double3);
	}

	public Quaterniond rotateTo(double double1, double double2, double double3, double double4, double double5, double double6) {
		return this.rotateTo(double1, double2, double3, double4, double5, double6, this);
	}

	public Quaterniond rotateTo(Vector3dc vector3dc, Vector3dc vector3dc2, Quaterniond quaterniond) {
		return this.rotateTo(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), quaterniond);
	}

	public Quaterniond rotateTo(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.rotateTo(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), this);
	}

	public Quaterniond rotateX(double double1) {
		return this.rotateX(double1, this);
	}

	public Quaterniond rotateX(double double1, Quaterniond quaterniond) {
		double double2 = Math.sin(double1 * 0.5);
		double double3 = Math.cosFromSin(double2, double1 * 0.5);
		return quaterniond.set(this.w * double2 + this.x * double3, this.y * double3 + this.z * double2, this.z * double3 - this.y * double2, this.w * double3 - this.x * double2);
	}

	public Quaterniond rotateY(double double1) {
		return this.rotateY(double1, this);
	}

	public Quaterniond rotateY(double double1, Quaterniond quaterniond) {
		double double2 = Math.sin(double1 * 0.5);
		double double3 = Math.cosFromSin(double2, double1 * 0.5);
		return quaterniond.set(this.x * double3 - this.z * double2, this.w * double2 + this.y * double3, this.x * double2 + this.z * double3, this.w * double3 - this.y * double2);
	}

	public Quaterniond rotateZ(double double1) {
		return this.rotateZ(double1, this);
	}

	public Quaterniond rotateZ(double double1, Quaterniond quaterniond) {
		double double2 = Math.sin(double1 * 0.5);
		double double3 = Math.cosFromSin(double2, double1 * 0.5);
		return quaterniond.set(this.x * double3 + this.y * double2, this.y * double3 - this.x * double2, this.w * double2 + this.z * double3, this.w * double3 - this.z * double2);
	}

	public Quaterniond rotateLocalX(double double1) {
		return this.rotateLocalX(double1, this);
	}

	public Quaterniond rotateLocalX(double double1, Quaterniond quaterniond) {
		double double2 = double1 * 0.5;
		double double3 = Math.sin(double2);
		double double4 = Math.cosFromSin(double3, double2);
		quaterniond.set(double4 * this.x + double3 * this.w, double4 * this.y - double3 * this.z, double4 * this.z + double3 * this.y, double4 * this.w - double3 * this.x);
		return quaterniond;
	}

	public Quaterniond rotateLocalY(double double1) {
		return this.rotateLocalY(double1, this);
	}

	public Quaterniond rotateLocalY(double double1, Quaterniond quaterniond) {
		double double2 = double1 * 0.5;
		double double3 = Math.sin(double2);
		double double4 = Math.cosFromSin(double3, double2);
		quaterniond.set(double4 * this.x + double3 * this.z, double4 * this.y + double3 * this.w, double4 * this.z - double3 * this.x, double4 * this.w - double3 * this.y);
		return quaterniond;
	}

	public Quaterniond rotateLocalZ(double double1) {
		return this.rotateLocalZ(double1, this);
	}

	public Quaterniond rotateLocalZ(double double1, Quaterniond quaterniond) {
		double double2 = double1 * 0.5;
		double double3 = Math.sin(double2);
		double double4 = Math.cosFromSin(double3, double2);
		quaterniond.set(double4 * this.x - double3 * this.y, double4 * this.y + double3 * this.x, double4 * this.z + double3 * this.w, double4 * this.w - double3 * this.z);
		return quaterniond;
	}

	public Quaterniond rotateXYZ(double double1, double double2, double double3) {
		return this.rotateXYZ(double1, double2, double3, this);
	}

	public Quaterniond rotateXYZ(double double1, double double2, double double3, Quaterniond quaterniond) {
		double double4 = Math.sin(double1 * 0.5);
		double double5 = Math.cosFromSin(double4, double1 * 0.5);
		double double6 = Math.sin(double2 * 0.5);
		double double7 = Math.cosFromSin(double6, double2 * 0.5);
		double double8 = Math.sin(double3 * 0.5);
		double double9 = Math.cosFromSin(double8, double3 * 0.5);
		double double10 = double7 * double9;
		double double11 = double6 * double8;
		double double12 = double6 * double9;
		double double13 = double7 * double8;
		double double14 = double5 * double10 - double4 * double11;
		double double15 = double4 * double10 + double5 * double11;
		double double16 = double5 * double12 - double4 * double13;
		double double17 = double5 * double13 + double4 * double12;
		return quaterniond.set(Math.fma(this.w, double15, Math.fma(this.x, double14, Math.fma(this.y, double17, -this.z * double16))), Math.fma(this.w, double16, Math.fma(-this.x, double17, Math.fma(this.y, double14, this.z * double15))), Math.fma(this.w, double17, Math.fma(this.x, double16, Math.fma(-this.y, double15, this.z * double14))), Math.fma(this.w, double14, Math.fma(-this.x, double15, Math.fma(-this.y, double16, -this.z * double17))));
	}

	public Quaterniond rotateZYX(double double1, double double2, double double3) {
		return this.rotateZYX(double1, double2, double3, this);
	}

	public Quaterniond rotateZYX(double double1, double double2, double double3, Quaterniond quaterniond) {
		double double4 = Math.sin(double3 * 0.5);
		double double5 = Math.cosFromSin(double4, double3 * 0.5);
		double double6 = Math.sin(double2 * 0.5);
		double double7 = Math.cosFromSin(double6, double2 * 0.5);
		double double8 = Math.sin(double1 * 0.5);
		double double9 = Math.cosFromSin(double8, double1 * 0.5);
		double double10 = double7 * double9;
		double double11 = double6 * double8;
		double double12 = double6 * double9;
		double double13 = double7 * double8;
		double double14 = double5 * double10 + double4 * double11;
		double double15 = double4 * double10 - double5 * double11;
		double double16 = double5 * double12 + double4 * double13;
		double double17 = double5 * double13 - double4 * double12;
		return quaterniond.set(Math.fma(this.w, double15, Math.fma(this.x, double14, Math.fma(this.y, double17, -this.z * double16))), Math.fma(this.w, double16, Math.fma(-this.x, double17, Math.fma(this.y, double14, this.z * double15))), Math.fma(this.w, double17, Math.fma(this.x, double16, Math.fma(-this.y, double15, this.z * double14))), Math.fma(this.w, double14, Math.fma(-this.x, double15, Math.fma(-this.y, double16, -this.z * double17))));
	}

	public Quaterniond rotateYXZ(double double1, double double2, double double3) {
		return this.rotateYXZ(double1, double2, double3, this);
	}

	public Quaterniond rotateYXZ(double double1, double double2, double double3, Quaterniond quaterniond) {
		double double4 = Math.sin(double2 * 0.5);
		double double5 = Math.cosFromSin(double4, double2 * 0.5);
		double double6 = Math.sin(double1 * 0.5);
		double double7 = Math.cosFromSin(double6, double1 * 0.5);
		double double8 = Math.sin(double3 * 0.5);
		double double9 = Math.cosFromSin(double8, double3 * 0.5);
		double double10 = double7 * double4;
		double double11 = double6 * double5;
		double double12 = double6 * double4;
		double double13 = double7 * double5;
		double double14 = double10 * double9 + double11 * double8;
		double double15 = double11 * double9 - double10 * double8;
		double double16 = double13 * double8 - double12 * double9;
		double double17 = double13 * double9 + double12 * double8;
		return quaterniond.set(Math.fma(this.w, double14, Math.fma(this.x, double17, Math.fma(this.y, double16, -this.z * double15))), Math.fma(this.w, double15, Math.fma(-this.x, double16, Math.fma(this.y, double17, this.z * double14))), Math.fma(this.w, double16, Math.fma(this.x, double15, Math.fma(-this.y, double14, this.z * double17))), Math.fma(this.w, double17, Math.fma(-this.x, double14, Math.fma(-this.y, double15, -this.z * double16))));
	}

	public Vector3d getEulerAnglesXYZ(Vector3d vector3d) {
		vector3d.x = Math.atan2(2.0 * (this.x * this.w - this.y * this.z), 1.0 - 2.0 * (this.x * this.x + this.y * this.y));
		vector3d.y = Math.safeAsin(2.0 * (this.x * this.z + this.y * this.w));
		vector3d.z = Math.atan2(2.0 * (this.z * this.w - this.x * this.y), 1.0 - 2.0 * (this.y * this.y + this.z * this.z));
		return vector3d;
	}

	public Quaterniond rotateAxis(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
		double double5 = double1 / 2.0;
		double double6 = Math.sin(double5);
		double double7 = Math.invsqrt(Math.fma(double2, double2, Math.fma(double3, double3, double4 * double4)));
		double double8 = double2 * double7 * double6;
		double double9 = double3 * double7 * double6;
		double double10 = double4 * double7 * double6;
		double double11 = Math.cosFromSin(double6, double5);
		return quaterniond.set(Math.fma(this.w, double8, Math.fma(this.x, double11, Math.fma(this.y, double10, -this.z * double9))), Math.fma(this.w, double9, Math.fma(-this.x, double10, Math.fma(this.y, double11, this.z * double8))), Math.fma(this.w, double10, Math.fma(this.x, double9, Math.fma(-this.y, double8, this.z * double11))), Math.fma(this.w, double11, Math.fma(-this.x, double8, Math.fma(-this.y, double9, -this.z * double10))));
	}

	public Quaterniond rotateAxis(double double1, Vector3dc vector3dc, Quaterniond quaterniond) {
		return this.rotateAxis(double1, vector3dc.x(), vector3dc.y(), vector3dc.z(), quaterniond);
	}

	public Quaterniond rotateAxis(double double1, Vector3dc vector3dc) {
		return this.rotateAxis(double1, vector3dc.x(), vector3dc.y(), vector3dc.z(), this);
	}

	public Quaterniond rotateAxis(double double1, double double2, double double3, double double4) {
		return this.rotateAxis(double1, double2, double3, double4, this);
	}

	public Vector3d positiveX(Vector3d vector3d) {
		double double1 = 1.0 / this.lengthSquared();
		double double2 = -this.x * double1;
		double double3 = -this.y * double1;
		double double4 = -this.z * double1;
		double double5 = this.w * double1;
		double double6 = double3 + double3;
		double double7 = double4 + double4;
		vector3d.x = -double3 * double6 - double4 * double7 + 1.0;
		vector3d.y = double2 * double6 + double5 * double7;
		vector3d.z = double2 * double7 - double5 * double6;
		return vector3d;
	}

	public Vector3d normalizedPositiveX(Vector3d vector3d) {
		double double1 = this.y + this.y;
		double double2 = this.z + this.z;
		vector3d.x = -this.y * double1 - this.z * double2 + 1.0;
		vector3d.y = this.x * double1 - this.w * double2;
		vector3d.z = this.x * double2 + this.w * double1;
		return vector3d;
	}

	public Vector3d positiveY(Vector3d vector3d) {
		double double1 = 1.0 / this.lengthSquared();
		double double2 = -this.x * double1;
		double double3 = -this.y * double1;
		double double4 = -this.z * double1;
		double double5 = this.w * double1;
		double double6 = double2 + double2;
		double double7 = double3 + double3;
		double double8 = double4 + double4;
		vector3d.x = double2 * double7 - double5 * double8;
		vector3d.y = -double2 * double6 - double4 * double8 + 1.0;
		vector3d.z = double3 * double8 + double5 * double6;
		return vector3d;
	}

	public Vector3d normalizedPositiveY(Vector3d vector3d) {
		double double1 = this.x + this.x;
		double double2 = this.y + this.y;
		double double3 = this.z + this.z;
		vector3d.x = this.x * double2 + this.w * double3;
		vector3d.y = -this.x * double1 - this.z * double3 + 1.0;
		vector3d.z = this.y * double3 - this.w * double1;
		return vector3d;
	}

	public Vector3d positiveZ(Vector3d vector3d) {
		double double1 = 1.0 / this.lengthSquared();
		double double2 = -this.x * double1;
		double double3 = -this.y * double1;
		double double4 = -this.z * double1;
		double double5 = this.w * double1;
		double double6 = double2 + double2;
		double double7 = double3 + double3;
		double double8 = double4 + double4;
		vector3d.x = double2 * double8 + double5 * double7;
		vector3d.y = double3 * double8 - double5 * double6;
		vector3d.z = -double2 * double6 - double3 * double7 + 1.0;
		return vector3d;
	}

	public Vector3d normalizedPositiveZ(Vector3d vector3d) {
		double double1 = this.x + this.x;
		double double2 = this.y + this.y;
		double double3 = this.z + this.z;
		vector3d.x = this.x * double3 - this.w * double2;
		vector3d.y = this.y * double3 + this.w * double1;
		vector3d.z = -this.x * double1 - this.y * double2 + 1.0;
		return vector3d;
	}

	public Quaterniond conjugateBy(Quaterniondc quaterniondc) {
		return this.conjugateBy(quaterniondc, this);
	}

	public Quaterniond conjugateBy(Quaterniondc quaterniondc, Quaterniond quaterniond) {
		double double1 = 1.0 / quaterniondc.lengthSquared();
		double double2 = -quaterniondc.x() * double1;
		double double3 = -quaterniondc.y() * double1;
		double double4 = -quaterniondc.z() * double1;
		double double5 = quaterniondc.w() * double1;
		double double6 = Math.fma(quaterniondc.w(), this.x, Math.fma(quaterniondc.x(), this.w, Math.fma(quaterniondc.y(), this.z, -quaterniondc.z() * this.y)));
		double double7 = Math.fma(quaterniondc.w(), this.y, Math.fma(-quaterniondc.x(), this.z, Math.fma(quaterniondc.y(), this.w, quaterniondc.z() * this.x)));
		double double8 = Math.fma(quaterniondc.w(), this.z, Math.fma(quaterniondc.x(), this.y, Math.fma(-quaterniondc.y(), this.x, quaterniondc.z() * this.w)));
		double double9 = Math.fma(quaterniondc.w(), this.w, Math.fma(-quaterniondc.x(), this.x, Math.fma(-quaterniondc.y(), this.y, -quaterniondc.z() * this.z)));
		return quaterniond.set(Math.fma(double9, double2, Math.fma(double6, double5, Math.fma(double7, double4, -double8 * double3))), Math.fma(double9, double3, Math.fma(-double6, double4, Math.fma(double7, double5, double8 * double2))), Math.fma(double9, double4, Math.fma(double6, double3, Math.fma(-double7, double2, double8 * double5))), Math.fma(double9, double5, Math.fma(-double6, double2, Math.fma(-double7, double3, -double8 * double4))));
	}

	public boolean isFinite() {
		return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z) && Math.isFinite(this.w);
	}

	public boolean equals(Quaterniondc quaterniondc, double double1) {
		if (this == quaterniondc) {
			return true;
		} else if (quaterniondc == null) {
			return false;
		} else if (!(quaterniondc instanceof Quaterniondc)) {
			return false;
		} else if (!Runtime.equals(this.x, quaterniondc.x(), double1)) {
			return false;
		} else if (!Runtime.equals(this.y, quaterniondc.y(), double1)) {
			return false;
		} else if (!Runtime.equals(this.z, quaterniondc.z(), double1)) {
			return false;
		} else {
			return Runtime.equals(this.w, quaterniondc.w(), double1);
		}
	}

	public boolean equals(double double1, double double2, double double3, double double4) {
		if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(double1)) {
			return false;
		} else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(double2)) {
			return false;
		} else if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(double3)) {
			return false;
		} else {
			return Double.doubleToLongBits(this.w) == Double.doubleToLongBits(double4);
		}
	}
}
