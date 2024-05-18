package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Quaterniond implements Externalizable,Quaterniondc {
	private static final long serialVersionUID = 1L;
	public double x;
	public double y;
	public double z;
	public double w;

	public Quaterniond() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
		this.w = 1.0;
	}

	public Quaterniond(double double1, double double2, double double3, double double4) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		this.w = double4;
	}

	public Quaterniond(double double1, double double2, double double3) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		this.w = 1.0;
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
		this.w = Math.cos((double)axisAngle4f.angle * 0.5);
	}

	public Quaterniond(AxisAngle4d axisAngle4d) {
		double double1 = Math.sin(axisAngle4d.angle * 0.5);
		this.x = axisAngle4d.x * double1;
		this.y = axisAngle4d.y * double1;
		this.z = axisAngle4d.z * double1;
		this.w = Math.cos(axisAngle4d.angle * 0.5);
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
		double double1 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		this.w *= double1;
		return this;
	}

	public Quaterniond normalize(Quaterniond quaterniond) {
		double double1 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
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
		double double1 = 2.0 * Math.acos(this.w);
		return double1 <= 3.141592653589793 ? double1 : 6.283185307179586 - double1;
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

	public Quaterniond get(Quaterniond quaterniond) {
		return quaterniond.set((Quaterniondc)this);
	}

	public Quaterniond set(double double1, double double2, double double3, double double4) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
		this.w = double4;
		return this;
	}

	public Quaterniond set(double double1, double double2, double double3) {
		this.x = double1;
		this.y = double2;
		this.z = double3;
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
		this.w = Math.cos(double1 * 0.5);
		return this;
	}

	public Quaterniond setAngleAxis(double double1, Vector3dc vector3dc) {
		return this.setAngleAxis(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	private void setFromUnnormalized(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double11 = 1.0 / Math.sqrt(double4 * double4 + double5 * double5 + double6 * double6);
		double double12 = 1.0 / Math.sqrt(double7 * double7 + double8 * double8 + double9 * double9);
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
		this.w = Math.cos(double5);
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
		quaterniond.set(this.w * quaterniondc.x() + this.x * quaterniondc.w() + this.y * quaterniondc.z() - this.z * quaterniondc.y(), this.w * quaterniondc.y() - this.x * quaterniondc.z() + this.y * quaterniondc.w() + this.z * quaterniondc.x(), this.w * quaterniondc.z() + this.x * quaterniondc.y() - this.y * quaterniondc.x() + this.z * quaterniondc.w(), this.w * quaterniondc.w() - this.x * quaterniondc.x() - this.y * quaterniondc.y() - this.z * quaterniondc.z());
		return quaterniond;
	}

	public Quaterniond mul(double double1, double double2, double double3, double double4) {
		this.set(this.w * double1 + this.x * double4 + this.y * double3 - this.z * double2, this.w * double2 - this.x * double3 + this.y * double4 + this.z * double1, this.w * double3 + this.x * double2 - this.y * double1 + this.z * double4, this.w * double4 - this.x * double1 - this.y * double2 - this.z * double3);
		return this;
	}

	public Quaterniond mul(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
		quaterniond.set(this.w * double1 + this.x * double4 + this.y * double3 - this.z * double2, this.w * double2 - this.x * double3 + this.y * double4 + this.z * double1, this.w * double3 + this.x * double2 - this.y * double1 + this.z * double4, this.w * double4 - this.x * double1 - this.y * double2 - this.z * double3);
		return quaterniond;
	}

	public Quaterniond premul(Quaterniondc quaterniondc) {
		return this.premul(quaterniondc, this);
	}

	public Quaterniond premul(Quaterniondc quaterniondc, Quaterniond quaterniond) {
		quaterniond.set(quaterniondc.w() * this.x + quaterniondc.x() * this.w + quaterniondc.y() * this.z - quaterniondc.z() * this.y, quaterniondc.w() * this.y - quaterniondc.x() * this.z + quaterniondc.y() * this.w + quaterniondc.z() * this.x, quaterniondc.w() * this.z + quaterniondc.x() * this.y - quaterniondc.y() * this.x + quaterniondc.z() * this.w, quaterniondc.w() * this.w - quaterniondc.x() * this.x - quaterniondc.y() * this.y - quaterniondc.z() * this.z);
		return quaterniond;
	}

	public Quaterniond premul(double double1, double double2, double double3, double double4) {
		return this.premul(double1, double2, double3, double4, this);
	}

	public Quaterniond premul(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
		quaterniond.set(double4 * this.x + double1 * this.w + double2 * this.z - double3 * this.y, double4 * this.y - double1 * this.z + double2 * this.w + double3 * this.x, double4 * this.z + double1 * this.y - double2 * this.x + double3 * this.w, double4 * this.w - double1 * this.x - double2 * this.y - double3 * this.z);
		return quaterniond;
	}

	public Vector3d transform(Vector3d vector3d) {
		return this.transform(vector3d.x, vector3d.y, vector3d.z, vector3d);
	}

	public Vector4d transform(Vector4d vector4d) {
		return this.transform((Vector4dc)vector4d, (Vector4d)vector4d);
	}

	public Vector3d transform(Vector3dc vector3dc, Vector3d vector3d) {
		return this.transform(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3d);
	}

	public Vector3d transform(double double1, double double2, double double3, Vector3d vector3d) {
		double double4 = this.w * this.w;
		double double5 = this.x * this.x;
		double double6 = this.y * this.y;
		double double7 = this.z * this.z;
		double double8 = this.z * this.w;
		double double9 = this.x * this.y;
		double double10 = this.x * this.z;
		double double11 = this.y * this.w;
		double double12 = this.y * this.z;
		double double13 = this.x * this.w;
		double double14 = double4 + double5 - double7 - double6;
		double double15 = double9 + double8 + double8 + double9;
		double double16 = double10 - double11 + double10 - double11;
		double double17 = -double8 + double9 - double8 + double9;
		double double18 = double6 - double7 + double4 - double5;
		double double19 = double12 + double12 + double13 + double13;
		double double20 = double11 + double10 + double10 + double11;
		double double21 = double12 + double12 - double13 - double13;
		double double22 = double7 - double6 - double5 + double4;
		vector3d.x = double14 * double1 + double17 * double2 + double20 * double3;
		vector3d.y = double15 * double1 + double18 * double2 + double21 * double3;
		vector3d.z = double16 * double1 + double19 * double2 + double22 * double3;
		return vector3d;
	}

	public Vector4d transform(Vector4dc vector4dc, Vector4d vector4d) {
		return this.transform(vector4dc.x(), vector4dc.y(), vector4dc.z(), vector4d);
	}

	public Vector4d transform(double double1, double double2, double double3, Vector4d vector4d) {
		double double4 = this.w * this.w;
		double double5 = this.x * this.x;
		double double6 = this.y * this.y;
		double double7 = this.z * this.z;
		double double8 = this.z * this.w;
		double double9 = this.x * this.y;
		double double10 = this.x * this.z;
		double double11 = this.y * this.w;
		double double12 = this.y * this.z;
		double double13 = this.x * this.w;
		double double14 = double4 + double5 - double7 - double6;
		double double15 = double9 + double8 + double8 + double9;
		double double16 = double10 - double11 + double10 - double11;
		double double17 = -double8 + double9 - double8 + double9;
		double double18 = double6 - double7 + double4 - double5;
		double double19 = double12 + double12 + double13 + double13;
		double double20 = double11 + double10 + double10 + double11;
		double double21 = double12 + double12 - double13 - double13;
		double double22 = double7 - double6 - double5 + double4;
		vector4d.x = double14 * double1 + double17 * double2 + double20 * double3;
		vector4d.y = double15 * double1 + double18 * double2 + double21 * double3;
		vector4d.z = double16 * double1 + double19 * double2 + double22 * double3;
		return vector4d;
	}

	public Quaterniond invert(Quaterniond quaterniond) {
		double double1 = 1.0 / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
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
		double double1 = 1.0 / (quaterniondc.x() * quaterniondc.x() + quaterniondc.y() * quaterniondc.y() + quaterniondc.z() * quaterniondc.z() + quaterniondc.w() * quaterniondc.w());
		double double2 = -quaterniondc.x() * double1;
		double double3 = -quaterniondc.y() * double1;
		double double4 = -quaterniondc.z() * double1;
		double double5 = quaterniondc.w() * double1;
		quaterniond.set(this.w * double2 + this.x * double5 + this.y * double4 - this.z * double3, this.w * double3 - this.x * double4 + this.y * double5 + this.z * double2, this.w * double4 + this.x * double3 - this.y * double2 + this.z * double5, this.w * double5 - this.x * double2 - this.y * double3 - this.z * double4);
		return quaterniond;
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
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}

	public Quaterniond rotationXYZ(double double1, double double2, double double3) {
		double double4 = Math.sin(double1 * 0.5);
		double double5 = Math.cos(double1 * 0.5);
		double double6 = Math.sin(double2 * 0.5);
		double double7 = Math.cos(double2 * 0.5);
		double double8 = Math.sin(double3 * 0.5);
		double double9 = Math.cos(double3 * 0.5);
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
		double double5 = Math.cos(double3 * 0.5);
		double double6 = Math.sin(double2 * 0.5);
		double double7 = Math.cos(double2 * 0.5);
		double double8 = Math.sin(double1 * 0.5);
		double double9 = Math.cos(double1 * 0.5);
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
		double double5 = Math.cos(double2 * 0.5);
		double double6 = Math.sin(double1 * 0.5);
		double double7 = Math.cos(double1 * 0.5);
		double double8 = Math.sin(double3 * 0.5);
		double double9 = Math.cos(double3 * 0.5);
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
		double double2 = this.x * quaterniondc.x() + this.y * quaterniondc.y() + this.z * quaterniondc.z() + this.w * quaterniondc.w();
		double double3 = Math.abs(double2);
		double double4;
		double double5;
		if (1.0 - double3 > 1.0E-6) {
			double double6 = 1.0 - double3 * double3;
			double double7 = 1.0 / Math.sqrt(double6);
			double double8 = Math.atan2(double6 * double7, double3);
			double4 = Math.sin((1.0 - double1) * double8) * double7;
			double5 = Math.sin(double1 * double8) * double7;
		} else {
			double4 = 1.0 - double1;
			double5 = double1;
		}

		double5 = double2 >= 0.0 ? double5 : -double5;
		quaterniond.x = double4 * this.x + double5 * quaterniondc.x();
		quaterniond.y = double4 * this.y + double5 * quaterniondc.y();
		quaterniond.z = double4 * this.z + double5 * quaterniondc.z();
		quaterniond.w = double4 * this.w + double5 * quaterniondc.w();
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
		return this;
	}

	public Quaterniond scaling(float float1) {
		double double1 = Math.sqrt((double)float1);
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
		this.w = double1;
		return this;
	}

	public Quaterniond integrate(double double1, double double2, double double3, double double4) {
		return this.integrate(double1, double2, double3, double4, this);
	}

	public Quaterniond integrate(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
		return this.rotateLocal(double1 * double2, double1 * double3, double1 * double4, quaterniond);
	}

	public Quaterniond nlerp(Quaterniondc quaterniondc, double double1) {
		return this.nlerp(quaterniondc, double1, this);
	}

	public Quaterniond nlerp(Quaterniondc quaterniondc, double double1, Quaterniond quaterniond) {
		double double2 = this.x * quaterniondc.x() + this.y * quaterniondc.y() + this.z * quaterniondc.z() + this.w * quaterniondc.w();
		double double3 = 1.0 - double1;
		double double4 = double2 >= 0.0 ? double1 : -double1;
		quaterniond.x = double3 * this.x + double4 * quaterniondc.x();
		quaterniond.y = double3 * this.y + double4 * quaterniondc.y();
		quaterniond.z = double3 * this.z + double4 * quaterniondc.z();
		quaterniond.w = double3 * this.w + double4 * quaterniondc.w();
		double double5 = 1.0 / Math.sqrt(quaterniond.x * quaterniond.x + quaterniond.y * quaterniond.y + quaterniond.z * quaterniond.z + quaterniond.w * quaterniond.w);
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
		double double11 = double3 * double7 + double4 * double8 + double5 * double9 + double6 * double10;
		double double12 = Math.abs(double11);
		if (0.999999 < double12) {
			return quaterniond.set((Quaterniondc)this);
		} else {
			double double13;
			double double14;
			double double15;
			double double16;
			for (double14 = double1; double12 < double2; double12 = Math.abs(double11)) {
				double15 = 0.5;
				double16 = double11 >= 0.0 ? 0.5 : -0.5;
				if (double14 < 0.5) {
					double7 = double15 * double7 + double16 * double3;
					double8 = double15 * double8 + double16 * double4;
					double9 = double15 * double9 + double16 * double5;
					double10 = double15 * double10 + double16 * double6;
					double13 = 1.0 / Math.sqrt(double7 * double7 + double8 * double8 + double9 * double9 + double10 * double10);
					double7 *= double13;
					double8 *= double13;
					double9 *= double13;
					double10 *= double13;
					double14 += double14;
				} else {
					double3 = double15 * double3 + double16 * double7;
					double4 = double15 * double4 + double16 * double8;
					double5 = double15 * double5 + double16 * double9;
					double6 = double15 * double6 + double16 * double10;
					double13 = 1.0 / Math.sqrt(double3 * double3 + double4 * double4 + double5 * double5 + double6 * double6);
					double3 *= double13;
					double4 *= double13;
					double5 *= double13;
					double6 *= double13;
					double14 = double14 + double14 - 1.0;
				}

				double11 = double3 * double7 + double4 * double8 + double5 * double9 + double6 * double10;
			}

			double15 = 1.0 - double14;
			double16 = double11 >= 0.0 ? double14 : -double14;
			double13 = double15 * double3 + double16 * double7;
			double double17 = double15 * double4 + double16 * double8;
			double double18 = double15 * double5 + double16 * double9;
			double double19 = double15 * double6 + double16 * double10;
			double double20 = 1.0 / Math.sqrt(double13 * double13 + double17 * double17 + double18 * double18 + double19 * double19);
			quaterniond.x *= double20;
			quaterniond.y *= double20;
			quaterniond.z *= double20;
			quaterniond.w *= double20;
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
		double double7 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double8 = double1 * double7;
		double double9 = double2 * double7;
		double double10 = double3 * double7;
		double double11 = double5 * double10 - double6 * double9;
		double double12 = double6 * double8 - double4 * double10;
		double double13 = double4 * double9 - double5 * double8;
		double double14 = 1.0 / Math.sqrt(double11 * double11 + double12 * double12 + double13 * double13);
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

		quaterniond.set(this.w * double19 + this.x * double22 + this.y * double21 - this.z * double20, this.w * double20 - this.x * double21 + this.y * double22 + this.z * double19, this.w * double21 + this.x * double20 - this.y * double19 + this.z * double22, this.w * double22 - this.x * double19 - this.y * double20 - this.z * double21);
		return quaterniond;
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format(this.x) + " " + numberFormat.format(this.y) + " " + numberFormat.format(this.z) + " " + numberFormat.format(this.w) + ")";
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
		double double1 = 1.0 / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
		double double2 = -this.x * double1;
		double double3 = -this.y * double1;
		double double4 = -this.z * double1;
		double double5 = this.w * double1;
		quaterniond.set(double5 * quaterniondc.x() + double2 * quaterniondc.w() + double3 * quaterniondc.z() - double4 * quaterniondc.y(), double5 * quaterniondc.y() - double2 * quaterniondc.z() + double3 * quaterniondc.w() + double4 * quaterniondc.x(), double5 * quaterniondc.z() + double2 * quaterniondc.y() - double3 * quaterniondc.x() + double4 * quaterniondc.w(), double5 * quaterniondc.w() - double2 * quaterniondc.x() - double3 * quaterniondc.y() - double4 * quaterniondc.z());
		return quaterniond;
	}

	public Quaterniond rotationTo(double double1, double double2, double double3, double double4, double double5, double double6) {
		this.x = double2 * double6 - double3 * double5;
		this.y = double3 * double4 - double1 * double6;
		this.z = double1 * double5 - double2 * double4;
		this.w = Math.sqrt((double1 * double1 + double2 * double2 + double3 * double3) * (double4 * double4 + double5 * double5 + double6 * double6)) + double1 * double4 + double2 * double5 + double3 * double6;
		double double7 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
		if (Double.isInfinite(double7)) {
			this.x = double5;
			this.y = -double4;
			this.z = 0.0;
			this.w = 0.0;
			double7 = (double)((float)(1.0 / Math.sqrt(this.x * this.x + this.y * this.y)));
			if (Double.isInfinite(double7)) {
				this.x = 0.0;
				this.y = double6;
				this.z = -double5;
				this.w = 0.0;
				double7 = (double)((float)(1.0 / Math.sqrt(this.y * this.y + this.z * this.z)));
			}
		}

		this.x *= double7;
		this.y *= double7;
		this.z *= double7;
		this.w *= double7;
		return this;
	}

	public Quaterniond rotationTo(Vector3dc vector3dc, Vector3dc vector3dc2) {
		return this.rotationTo(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z());
	}

	public Quaterniond rotateTo(double double1, double double2, double double3, double double4, double double5, double double6, Quaterniond quaterniond) {
		double double7 = double2 * double6 - double3 * double5;
		double double8 = double3 * double4 - double1 * double6;
		double double9 = double1 * double5 - double2 * double4;
		double double10 = Math.sqrt((double1 * double1 + double2 * double2 + double3 * double3) * (double4 * double4 + double5 * double5 + double6 * double6)) + double1 * double4 + double2 * double5 + double3 * double6;
		double double11 = 1.0 / Math.sqrt(double7 * double7 + double8 * double8 + double9 * double9 + double10 * double10);
		if (Double.isInfinite(double11)) {
			double7 = double5;
			double8 = -double4;
			double9 = 0.0;
			double10 = 0.0;
			double11 = (double)((float)(1.0 / Math.sqrt(double5 * double5 + double8 * double8)));
			if (Double.isInfinite(double11)) {
				double7 = 0.0;
				double8 = double6;
				double9 = -double5;
				double10 = 0.0;
				double11 = (double)((float)(1.0 / Math.sqrt(double6 * double6 + double9 * double9)));
			}
		}

		double7 *= double11;
		double8 *= double11;
		double9 *= double11;
		double10 *= double11;
		quaterniond.set(this.w * double7 + this.x * double10 + this.y * double9 - this.z * double8, this.w * double8 - this.x * double9 + this.y * double10 + this.z * double7, this.w * double9 + this.x * double8 - this.y * double7 + this.z * double10, this.w * double10 - this.x * double7 - this.y * double8 - this.z * double9);
		return quaterniond;
	}

	public Quaterniond rotationAxis(AxisAngle4f axisAngle4f) {
		return this.rotationAxis((double)axisAngle4f.angle, (double)axisAngle4f.x, (double)axisAngle4f.y, (double)axisAngle4f.z);
	}

	public Quaterniond rotationAxis(double double1, double double2, double double3, double double4) {
		double double5 = double1 / 2.0;
		double double6 = Math.sin(double5);
		double double7 = 1.0 / Math.sqrt(double2 * double2 + double3 * double3 + double4 * double4);
		this.x = double2 * double7 * double6;
		this.y = double3 * double7 * double6;
		this.z = double4 * double7 * double6;
		this.w = (double)((float)Math.cos(double5));
		return this;
	}

	public Quaterniond rotation(double double1, double double2, double double3) {
		double double4 = double1 * 0.5;
		double double5 = double2 * 0.5;
		double double6 = double3 * 0.5;
		double double7 = double4 * double4 + double5 * double5 + double6 * double6;
		double double8;
		if (double7 * double7 / 24.0 < 9.99999993922529E-9) {
			this.w = 1.0 - double7 / 2.0;
			double8 = 1.0 - double7 / 6.0;
		} else {
			double double9 = Math.sqrt(double7);
			this.w = Math.cos(double9);
			double8 = Math.sin(double9) / double9;
		}

		this.x = double4 * double8;
		this.y = double5 * double8;
		this.z = double6 * double8;
		return this;
	}

	public Quaterniond rotationX(double double1) {
		double double2 = Math.cos(double1 * 0.5);
		double double3 = Math.sin(double1 * 0.5);
		this.w = double2;
		this.x = double3;
		this.y = 0.0;
		this.z = 0.0;
		return this;
	}

	public Quaterniond rotationY(double double1) {
		double double2 = Math.cos(double1 * 0.5);
		double double3 = Math.sin(double1 * 0.5);
		this.w = double2;
		this.x = 0.0;
		this.y = double3;
		this.z = 0.0;
		return this;
	}

	public Quaterniond rotationZ(double double1) {
		double double2 = Math.cos(double1 * 0.5);
		double double3 = Math.sin(double1 * 0.5);
		this.w = double2;
		this.x = 0.0;
		this.y = 0.0;
		this.z = double3;
		return this;
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

	public Quaterniond rotate(Vector3dc vector3dc, Quaterniond quaterniond) {
		return this.rotate(vector3dc.x(), vector3dc.y(), vector3dc.z(), quaterniond);
	}

	public Quaterniond rotate(Vector3dc vector3dc) {
		return this.rotate(vector3dc.x(), vector3dc.y(), vector3dc.z(), this);
	}

	public Quaterniond rotate(double double1, double double2, double double3) {
		return this.rotate(double1, double2, double3, this);
	}

	public Quaterniond rotate(double double1, double double2, double double3, Quaterniond quaterniond) {
		double double4 = double1 * 0.5;
		double double5 = double2 * 0.5;
		double double6 = double3 * 0.5;
		double double7 = double4 * double4 + double5 * double5 + double6 * double6;
		double double8;
		double double9;
		if (double7 * double7 / 24.0 < 9.99999993922529E-9) {
			double9 = 1.0 - double7 / 2.0;
			double8 = 1.0 - double7 / 6.0;
		} else {
			double double10 = Math.sqrt(double7);
			double9 = Math.cos(double10);
			double8 = Math.sin(double10) / double10;
		}

		double double11 = double4 * double8;
		double double12 = double5 * double8;
		double double13 = double6 * double8;
		quaterniond.set(this.w * double11 + this.x * double9 + this.y * double13 - this.z * double12, this.w * double12 - this.x * double13 + this.y * double9 + this.z * double11, this.w * double13 + this.x * double12 - this.y * double11 + this.z * double9, this.w * double9 - this.x * double11 - this.y * double12 - this.z * double13);
		return quaterniond;
	}

	public Quaterniond rotateLocal(double double1, double double2, double double3) {
		return this.rotateLocal(double1, double2, double3, this);
	}

	public Quaterniond rotateLocal(double double1, double double2, double double3, Quaterniond quaterniond) {
		double double4 = double1 * 0.5;
		double double5 = double2 * 0.5;
		double double6 = double3 * 0.5;
		double double7 = double4 * double4 + double5 * double5 + double6 * double6;
		double double8;
		double double9;
		if (double7 * double7 / 24.0 < 1.0E-8) {
			double9 = 1.0 - double7 * 0.5;
			double8 = 1.0 - double7 / 6.0;
		} else {
			double double10 = Math.sqrt(double7);
			double9 = Math.cos(double10);
			double8 = Math.sin(double10) / double10;
		}

		double double11 = double4 * double8;
		double double12 = double5 * double8;
		double double13 = double6 * double8;
		quaterniond.set(double9 * this.x + double11 * this.w + double12 * this.z - double13 * this.y, double9 * this.y - double11 * this.z + double12 * this.w + double13 * this.x, double9 * this.z + double11 * this.y - double12 * this.x + double13 * this.w, double9 * this.w - double11 * this.x - double12 * this.y - double13 * this.z);
		return quaterniond;
	}

	public Quaterniond rotateX(double double1) {
		return this.rotateX(double1, this);
	}

	public Quaterniond rotateX(double double1, Quaterniond quaterniond) {
		double double2 = Math.cos(double1 * 0.5);
		double double3 = Math.sin(double1 * 0.5);
		quaterniond.set(this.w * double3 + this.x * double2, this.y * double2 + this.z * double3, this.z * double2 - this.y * double3, this.w * double2 - this.x * double3);
		return quaterniond;
	}

	public Quaterniond rotateY(double double1) {
		return this.rotateY(double1, this);
	}

	public Quaterniond rotateY(double double1, Quaterniond quaterniond) {
		double double2 = Math.cos(double1 * 0.5);
		double double3 = Math.sin(double1 * 0.5);
		quaterniond.set(this.x * double2 - this.z * double3, this.w * double3 + this.y * double2, this.x * double3 + this.z * double2, this.w * double2 - this.y * double3);
		return quaterniond;
	}

	public Quaterniond rotateZ(double double1) {
		return this.rotateZ(double1, this);
	}

	public Quaterniond rotateZ(double double1, Quaterniond quaterniond) {
		double double2 = Math.cos(double1 * 0.5);
		double double3 = Math.sin(double1 * 0.5);
		quaterniond.set(this.x * double2 + this.y * double3, this.y * double2 - this.x * double3, this.w * double3 + this.z * double2, this.w * double2 - this.z * double3);
		return quaterniond;
	}

	public Quaterniond rotateLocalX(double double1) {
		return this.rotateLocalX(double1, this);
	}

	public Quaterniond rotateLocalX(double double1, Quaterniond quaterniond) {
		double double2 = double1 * 0.5;
		double double3 = Math.sin(double2);
		double double4 = Math.cos(double2);
		quaterniond.set(double4 * this.x + double3 * this.w, double4 * this.y - double3 * this.z, double4 * this.z + double3 * this.y, double4 * this.w - double3 * this.x);
		return quaterniond;
	}

	public Quaterniond rotateLocalY(double double1) {
		return this.rotateLocalY(double1, this);
	}

	public Quaterniond rotateLocalY(double double1, Quaterniond quaterniond) {
		double double2 = double1 * 0.5;
		double double3 = Math.sin(double2);
		double double4 = Math.cos(double2);
		quaterniond.set(double4 * this.x + double3 * this.z, double4 * this.y + double3 * this.w, double4 * this.z - double3 * this.x, double4 * this.w - double3 * this.y);
		return quaterniond;
	}

	public Quaterniond rotateLocalZ(double double1) {
		return this.rotateLocalZ(double1, this);
	}

	public Quaterniond rotateLocalZ(double double1, Quaterniond quaterniond) {
		double double2 = double1 * 0.5;
		double double3 = Math.sin(double2);
		double double4 = Math.cos(double2);
		quaterniond.set(double4 * this.x - double3 * this.y, double4 * this.y + double3 * this.x, double4 * this.z + double3 * this.w, double4 * this.w - double3 * this.z);
		return quaterniond;
	}

	public Quaterniond rotateXYZ(double double1, double double2, double double3) {
		return this.rotateXYZ(double1, double2, double3, this);
	}

	public Quaterniond rotateXYZ(double double1, double double2, double double3, Quaterniond quaterniond) {
		double double4 = Math.sin(double1 * 0.5);
		double double5 = Math.cos(double1 * 0.5);
		double double6 = Math.sin(double2 * 0.5);
		double double7 = Math.cos(double2 * 0.5);
		double double8 = Math.sin(double3 * 0.5);
		double double9 = Math.cos(double3 * 0.5);
		double double10 = double7 * double9;
		double double11 = double6 * double8;
		double double12 = double6 * double9;
		double double13 = double7 * double8;
		double double14 = double5 * double10 - double4 * double11;
		double double15 = double4 * double10 + double5 * double11;
		double double16 = double5 * double12 - double4 * double13;
		double double17 = double5 * double13 + double4 * double12;
		quaterniond.set(this.w * double15 + this.x * double14 + this.y * double17 - this.z * double16, this.w * double16 - this.x * double17 + this.y * double14 + this.z * double15, this.w * double17 + this.x * double16 - this.y * double15 + this.z * double14, this.w * double14 - this.x * double15 - this.y * double16 - this.z * double17);
		return quaterniond;
	}

	public Quaterniond rotateZYX(double double1, double double2, double double3) {
		return this.rotateZYX(double1, double2, double3, this);
	}

	public Quaterniond rotateZYX(double double1, double double2, double double3, Quaterniond quaterniond) {
		double double4 = Math.sin(double3 * 0.5);
		double double5 = Math.cos(double3 * 0.5);
		double double6 = Math.sin(double2 * 0.5);
		double double7 = Math.cos(double2 * 0.5);
		double double8 = Math.sin(double1 * 0.5);
		double double9 = Math.cos(double1 * 0.5);
		double double10 = double7 * double9;
		double double11 = double6 * double8;
		double double12 = double6 * double9;
		double double13 = double7 * double8;
		double double14 = double5 * double10 + double4 * double11;
		double double15 = double4 * double10 - double5 * double11;
		double double16 = double5 * double12 + double4 * double13;
		double double17 = double5 * double13 - double4 * double12;
		quaterniond.set(this.w * double15 + this.x * double14 + this.y * double17 - this.z * double16, this.w * double16 - this.x * double17 + this.y * double14 + this.z * double15, this.w * double17 + this.x * double16 - this.y * double15 + this.z * double14, this.w * double14 - this.x * double15 - this.y * double16 - this.z * double17);
		return quaterniond;
	}

	public Quaterniond rotateYXZ(double double1, double double2, double double3) {
		return this.rotateYXZ(double1, double2, double3, this);
	}

	public Quaterniond rotateYXZ(double double1, double double2, double double3, Quaterniond quaterniond) {
		double double4 = Math.sin(double2 * 0.5);
		double double5 = Math.cos(double2 * 0.5);
		double double6 = Math.sin(double1 * 0.5);
		double double7 = Math.cos(double1 * 0.5);
		double double8 = Math.sin(double3 * 0.5);
		double double9 = Math.cos(double3 * 0.5);
		double double10 = double7 * double4;
		double double11 = double6 * double5;
		double double12 = double6 * double4;
		double double13 = double7 * double5;
		double double14 = double10 * double9 + double11 * double8;
		double double15 = double11 * double9 - double10 * double8;
		double double16 = double13 * double8 - double12 * double9;
		double double17 = double13 * double9 + double12 * double8;
		quaterniond.set(this.w * double14 + this.x * double17 + this.y * double16 - this.z * double15, this.w * double15 - this.x * double16 + this.y * double17 + this.z * double14, this.w * double16 + this.x * double15 - this.y * double14 + this.z * double17, this.w * double17 - this.x * double14 - this.y * double15 - this.z * double16);
		return quaterniond;
	}

	public Vector3d getEulerAnglesXYZ(Vector3d vector3d) {
		vector3d.x = Math.atan2(2.0 * (this.x * this.w - this.y * this.z), 1.0 - 2.0 * (this.x * this.x + this.y * this.y));
		vector3d.y = Math.asin(2.0 * (this.x * this.z + this.y * this.w));
		vector3d.z = Math.atan2(2.0 * (this.z * this.w - this.x * this.y), 1.0 - 2.0 * (this.y * this.y + this.z * this.z));
		return vector3d;
	}

	public Quaterniond rotateAxis(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
		double double5 = double1 / 2.0;
		double double6 = Math.sin(double5);
		double double7 = 1.0 / Math.sqrt(double2 * double2 + double3 * double3 + double4 * double4);
		double double8 = double2 * double7 * double6;
		double double9 = double3 * double7 * double6;
		double double10 = double4 * double7 * double6;
		double double11 = Math.cos(double5);
		quaterniond.set(this.w * double8 + this.x * double11 + this.y * double10 - this.z * double9, this.w * double9 - this.x * double10 + this.y * double11 + this.z * double8, this.w * double10 + this.x * double9 - this.y * double8 + this.z * double11, this.w * double11 - this.x * double8 - this.y * double9 - this.z * double10);
		return quaterniond;
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
		double double1 = 1.0 / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
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
		double double1 = 1.0 / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
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
		double double1 = 1.0 / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
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

	public Quaterniondc toImmutable() {
		return (Quaterniondc)(!Options.DEBUG ? this : new Quaterniond.Proxy(this));
	}

	private final class Proxy implements Quaterniondc {
		private final Quaterniondc delegate;

		Proxy(Quaterniondc quaterniondc) {
			this.delegate = quaterniondc;
		}

		public double x() {
			return this.delegate.x();
		}

		public double y() {
			return this.delegate.y();
		}

		public double z() {
			return this.delegate.z();
		}

		public double w() {
			return this.delegate.w();
		}

		public Quaterniond normalize(Quaterniond quaterniond) {
			return this.delegate.normalize(quaterniond);
		}

		public Quaterniond add(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
			return this.delegate.add(double1, double2, double3, double4, quaterniond);
		}

		public Quaterniond add(Quaterniondc quaterniondc, Quaterniond quaterniond) {
			return this.delegate.add(quaterniondc, quaterniond);
		}

		public double dot(Quaterniondc quaterniondc) {
			return this.delegate.dot(quaterniondc);
		}

		public double angle() {
			return this.delegate.angle();
		}

		public Matrix3d get(Matrix3d matrix3d) {
			return this.delegate.get(matrix3d);
		}

		public Matrix3f get(Matrix3f matrix3f) {
			return this.delegate.get(matrix3f);
		}

		public Matrix4d get(Matrix4d matrix4d) {
			return this.delegate.get(matrix4d);
		}

		public Matrix4f get(Matrix4f matrix4f) {
			return this.delegate.get(matrix4f);
		}

		public Quaterniond get(Quaterniond quaterniond) {
			return this.delegate.get(quaterniond);
		}

		public Quaterniond mul(Quaterniondc quaterniondc, Quaterniond quaterniond) {
			return this.delegate.mul(quaterniondc, quaterniond);
		}

		public Quaterniond mul(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
			return this.delegate.mul(double1, double2, double3, double4, quaterniond);
		}

		public Quaterniond premul(Quaterniondc quaterniondc, Quaterniond quaterniond) {
			return this.delegate.premul(quaterniondc, quaterniond);
		}

		public Quaterniond premul(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
			return this.delegate.premul(double1, double2, double3, double4, quaterniond);
		}

		public Vector3d transform(Vector3d vector3d) {
			return this.delegate.transform(vector3d);
		}

		public Vector4d transform(Vector4d vector4d) {
			return this.delegate.transform(vector4d);
		}

		public Vector3d transform(Vector3dc vector3dc, Vector3d vector3d) {
			return this.delegate.transform(vector3dc, vector3d);
		}

		public Vector3d transform(double double1, double double2, double double3, Vector3d vector3d) {
			return this.delegate.transform(double1, double2, double3, vector3d);
		}

		public Vector4d transform(Vector4dc vector4dc, Vector4d vector4d) {
			return this.delegate.transform(vector4dc, vector4d);
		}

		public Vector4d transform(double double1, double double2, double double3, Vector4d vector4d) {
			return this.delegate.transform(double1, double2, double3, vector4d);
		}

		public Quaterniond invert(Quaterniond quaterniond) {
			return this.delegate.invert(quaterniond);
		}

		public Quaterniond div(Quaterniondc quaterniondc, Quaterniond quaterniond) {
			return this.delegate.div(quaterniondc, quaterniond);
		}

		public Quaterniond conjugate(Quaterniond quaterniond) {
			return this.delegate.conjugate(quaterniond);
		}

		public double lengthSquared() {
			return this.delegate.lengthSquared();
		}

		public Quaterniond slerp(Quaterniondc quaterniondc, double double1, Quaterniond quaterniond) {
			return this.delegate.slerp(quaterniondc, double1, quaterniond);
		}

		public Quaterniond scale(double double1, Quaterniond quaterniond) {
			return this.delegate.scale(double1, quaterniond);
		}

		public Quaterniond integrate(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
			return this.delegate.integrate(double1, double2, double3, double4, quaterniond);
		}

		public Quaterniond nlerp(Quaterniondc quaterniondc, double double1, Quaterniond quaterniond) {
			return this.delegate.nlerp(quaterniondc, double1, quaterniond);
		}

		public Quaterniond nlerpIterative(Quaterniondc quaterniondc, double double1, double double2, Quaterniond quaterniond) {
			return this.delegate.nlerpIterative(quaterniondc, double1, double2, quaterniond);
		}

		public Quaterniond lookAlong(Vector3dc vector3dc, Vector3dc vector3dc2, Quaterniond quaterniond) {
			return this.delegate.lookAlong(vector3dc, vector3dc2, quaterniond);
		}

		public Quaterniond lookAlong(double double1, double double2, double double3, double double4, double double5, double double6, Quaterniond quaterniond) {
			return this.delegate.lookAlong(double1, double2, double3, double4, double5, double6, quaterniond);
		}

		public Quaterniond difference(Quaterniondc quaterniondc, Quaterniond quaterniond) {
			return this.delegate.difference(quaterniondc, quaterniond);
		}

		public Quaterniond rotateTo(double double1, double double2, double double3, double double4, double double5, double double6, Quaterniond quaterniond) {
			return this.delegate.rotateTo(double1, double2, double3, double4, double5, double6, quaterniond);
		}

		public Quaterniond rotateTo(Vector3dc vector3dc, Vector3dc vector3dc2, Quaterniond quaterniond) {
			return this.delegate.rotateTo(vector3dc, vector3dc2, quaterniond);
		}

		public Quaterniond rotate(Vector3dc vector3dc, Quaterniond quaterniond) {
			return this.delegate.rotate(vector3dc, quaterniond);
		}

		public Quaterniond rotate(double double1, double double2, double double3, Quaterniond quaterniond) {
			return this.delegate.rotate(double1, double2, double3, quaterniond);
		}

		public Quaterniond rotateLocal(double double1, double double2, double double3, Quaterniond quaterniond) {
			return this.delegate.rotateLocal(double1, double2, double3, quaterniond);
		}

		public Quaterniond rotateX(double double1, Quaterniond quaterniond) {
			return this.delegate.rotateX(double1, quaterniond);
		}

		public Quaterniond rotateY(double double1, Quaterniond quaterniond) {
			return this.delegate.rotateY(double1, quaterniond);
		}

		public Quaterniond rotateZ(double double1, Quaterniond quaterniond) {
			return this.delegate.rotateZ(double1, quaterniond);
		}

		public Quaterniond rotateLocalX(double double1, Quaterniond quaterniond) {
			return this.delegate.rotateLocalX(double1, quaterniond);
		}

		public Quaterniond rotateLocalY(double double1, Quaterniond quaterniond) {
			return this.delegate.rotateLocalY(double1, quaterniond);
		}

		public Quaterniond rotateLocalZ(double double1, Quaterniond quaterniond) {
			return this.delegate.rotateLocalZ(double1, quaterniond);
		}

		public Quaterniond rotateXYZ(double double1, double double2, double double3, Quaterniond quaterniond) {
			return this.delegate.rotateXYZ(double1, double2, double3, quaterniond);
		}

		public Quaterniond rotateZYX(double double1, double double2, double double3, Quaterniond quaterniond) {
			return this.delegate.rotateZYX(double1, double2, double3, quaterniond);
		}

		public Quaterniond rotateYXZ(double double1, double double2, double double3, Quaterniond quaterniond) {
			return this.delegate.rotateYXZ(double1, double2, double3, quaterniond);
		}

		public Vector3d getEulerAnglesXYZ(Vector3d vector3d) {
			return this.delegate.getEulerAnglesXYZ(vector3d);
		}

		public Quaterniond rotateAxis(double double1, double double2, double double3, double double4, Quaterniond quaterniond) {
			return this.delegate.rotateAxis(double1, double2, double3, double4, quaterniond);
		}

		public Quaterniond rotateAxis(double double1, Vector3dc vector3dc, Quaterniond quaterniond) {
			return this.delegate.rotateAxis(double1, vector3dc, quaterniond);
		}

		public Vector3d positiveX(Vector3d vector3d) {
			return this.delegate.positiveX(vector3d);
		}

		public Vector3d normalizedPositiveX(Vector3d vector3d) {
			return this.delegate.normalizedPositiveX(vector3d);
		}

		public Vector3d positiveY(Vector3d vector3d) {
			return this.delegate.positiveY(vector3d);
		}

		public Vector3d normalizedPositiveY(Vector3d vector3d) {
			return this.delegate.normalizedPositiveY(vector3d);
		}

		public Vector3d positiveZ(Vector3d vector3d) {
			return this.delegate.positiveZ(vector3d);
		}

		public Vector3d normalizedPositiveZ(Vector3d vector3d) {
			return this.delegate.normalizedPositiveZ(vector3d);
		}
	}
}
