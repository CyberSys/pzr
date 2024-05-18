package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class AxisAngle4d implements Externalizable {
	private static final long serialVersionUID = 1L;
	public double angle;
	public double x;
	public double y;
	public double z;

	public AxisAngle4d() {
		this.z = 1.0;
	}

	public AxisAngle4d(AxisAngle4d axisAngle4d) {
		this.x = axisAngle4d.x;
		this.y = axisAngle4d.y;
		this.z = axisAngle4d.z;
		this.angle = (axisAngle4d.angle < 0.0 ? 6.283185307179586 + axisAngle4d.angle % 6.283185307179586 : axisAngle4d.angle) % 6.283185307179586;
	}

	public AxisAngle4d(AxisAngle4f axisAngle4f) {
		this.x = (double)axisAngle4f.x;
		this.y = (double)axisAngle4f.y;
		this.z = (double)axisAngle4f.z;
		this.angle = ((double)axisAngle4f.angle < 0.0 ? 6.283185307179586 + (double)axisAngle4f.angle % 6.283185307179586 : (double)axisAngle4f.angle) % 6.283185307179586;
	}

	public AxisAngle4d(Quaternionfc quaternionfc) {
		double double1 = Math.acos((double)quaternionfc.w());
		double double2 = 1.0 / Math.sqrt(1.0 - (double)(quaternionfc.w() * quaternionfc.w()));
		this.x = (double)quaternionfc.x() * double2;
		this.y = (double)quaternionfc.y() * double2;
		this.z = (double)quaternionfc.z() * double2;
		this.angle = double1 + double1;
	}

	public AxisAngle4d(Quaterniondc quaterniondc) {
		double double1 = Math.acos(quaterniondc.w());
		double double2 = 1.0 / Math.sqrt(1.0 - quaterniondc.w() * quaterniondc.w());
		this.x = quaterniondc.x() * double2;
		this.y = quaterniondc.y() * double2;
		this.z = quaterniondc.z() * double2;
		this.angle = double1 + double1;
	}

	public AxisAngle4d(double double1, double double2, double double3, double double4) {
		this.x = double2;
		this.y = double3;
		this.z = double4;
		this.angle = (double1 < 0.0 ? 6.283185307179586 + double1 % 6.283185307179586 : double1) % 6.283185307179586;
	}

	public AxisAngle4d(double double1, Vector3dc vector3dc) {
		this(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public AxisAngle4d(double double1, Vector3f vector3f) {
		this(double1, (double)vector3f.x, (double)vector3f.y, (double)vector3f.z);
	}

	public AxisAngle4d set(AxisAngle4d axisAngle4d) {
		this.x = axisAngle4d.x;
		this.y = axisAngle4d.y;
		this.z = axisAngle4d.z;
		this.angle = (axisAngle4d.angle < 0.0 ? 6.283185307179586 + axisAngle4d.angle % 6.283185307179586 : axisAngle4d.angle) % 6.283185307179586;
		return this;
	}

	public AxisAngle4d set(AxisAngle4f axisAngle4f) {
		this.x = (double)axisAngle4f.x;
		this.y = (double)axisAngle4f.y;
		this.z = (double)axisAngle4f.z;
		this.angle = ((double)axisAngle4f.angle < 0.0 ? 6.283185307179586 + (double)axisAngle4f.angle % 6.283185307179586 : (double)axisAngle4f.angle) % 6.283185307179586;
		return this;
	}

	public AxisAngle4d set(double double1, double double2, double double3, double double4) {
		this.x = double2;
		this.y = double3;
		this.z = double4;
		this.angle = (double1 < 0.0 ? 6.283185307179586 + double1 % 6.283185307179586 : double1) % 6.283185307179586;
		return this;
	}

	public AxisAngle4d set(double double1, Vector3dc vector3dc) {
		return this.set(double1, vector3dc.x(), vector3dc.y(), vector3dc.z());
	}

	public AxisAngle4d set(double double1, Vector3f vector3f) {
		return this.set(double1, (double)vector3f.x, (double)vector3f.y, (double)vector3f.z);
	}

	public AxisAngle4d set(Quaternionfc quaternionfc) {
		double double1 = Math.acos((double)quaternionfc.w());
		double double2 = 1.0 / Math.sqrt(1.0 - (double)(quaternionfc.w() * quaternionfc.w()));
		this.x = (double)quaternionfc.x() * double2;
		this.y = (double)quaternionfc.y() * double2;
		this.z = (double)quaternionfc.z() * double2;
		this.angle = double1 + double1;
		return this;
	}

	public AxisAngle4d set(Quaterniondc quaterniondc) {
		double double1 = Math.acos(quaterniondc.w());
		double double2 = 1.0 / Math.sqrt(1.0 - quaterniondc.w() * quaterniondc.w());
		this.x = quaterniondc.x() * double2;
		this.y = quaterniondc.y() * double2;
		this.z = quaterniondc.z() * double2;
		this.angle = double1 + double1;
		return this;
	}

	public AxisAngle4d set(Matrix3fc matrix3fc) {
		double double1 = ((double)(matrix3fc.m00() + matrix3fc.m11() + matrix3fc.m22()) - 1.0) * 0.5;
		this.x = (double)(matrix3fc.m12() - matrix3fc.m21());
		this.y = (double)(matrix3fc.m20() - matrix3fc.m02());
		this.z = (double)(matrix3fc.m01() - matrix3fc.m10());
		double double2 = 0.5 * Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		this.angle = Math.atan2(double2, double1);
		return this;
	}

	public AxisAngle4d set(Matrix3dc matrix3dc) {
		double double1 = (matrix3dc.m00() + matrix3dc.m11() + matrix3dc.m22() - 1.0) * 0.5;
		this.x = matrix3dc.m12() - matrix3dc.m21();
		this.y = matrix3dc.m20() - matrix3dc.m02();
		this.z = matrix3dc.m01() - matrix3dc.m10();
		double double2 = 0.5 * Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		this.angle = Math.atan2(double2, double1);
		return this;
	}

	public AxisAngle4d set(Matrix4fc matrix4fc) {
		double double1 = ((double)(matrix4fc.m00() + matrix4fc.m11() + matrix4fc.m22()) - 1.0) * 0.5;
		this.x = (double)(matrix4fc.m12() - matrix4fc.m21());
		this.y = (double)(matrix4fc.m20() - matrix4fc.m02());
		this.z = (double)(matrix4fc.m01() - matrix4fc.m10());
		double double2 = 0.5 * Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		this.angle = Math.atan2(double2, double1);
		return this;
	}

	public AxisAngle4d set(Matrix4x3fc matrix4x3fc) {
		double double1 = ((double)(matrix4x3fc.m00() + matrix4x3fc.m11() + matrix4x3fc.m22()) - 1.0) * 0.5;
		this.x = (double)(matrix4x3fc.m12() - matrix4x3fc.m21());
		this.y = (double)(matrix4x3fc.m20() - matrix4x3fc.m02());
		this.z = (double)(matrix4x3fc.m01() - matrix4x3fc.m10());
		double double2 = 0.5 * Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		this.angle = Math.atan2(double2, double1);
		return this;
	}

	public AxisAngle4d set(Matrix4dc matrix4dc) {
		double double1 = (matrix4dc.m00() + matrix4dc.m11() + matrix4dc.m22() - 1.0) * 0.5;
		this.x = matrix4dc.m12() - matrix4dc.m21();
		this.y = matrix4dc.m20() - matrix4dc.m02();
		this.z = matrix4dc.m01() - matrix4dc.m10();
		double double2 = 0.5 * Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		this.angle = Math.atan2(double2, double1);
		return this;
	}

	public Quaternionf get(Quaternionf quaternionf) {
		return quaternionf.set(this);
	}

	public Quaterniond get(Quaterniond quaterniond) {
		return quaterniond.set(this);
	}

	public Matrix4f get(Matrix4f matrix4f) {
		return matrix4f.set(this);
	}

	public Matrix3f get(Matrix3f matrix3f) {
		return matrix3f.set(this);
	}

	public Matrix4d get(Matrix4d matrix4d) {
		return matrix4d.set(this);
	}

	public Matrix3d get(Matrix3d matrix3d) {
		return matrix3d.set(this);
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeDouble(this.angle);
		objectOutput.writeDouble(this.x);
		objectOutput.writeDouble(this.y);
		objectOutput.writeDouble(this.z);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.angle = objectInput.readDouble();
		this.x = objectInput.readDouble();
		this.y = objectInput.readDouble();
		this.z = objectInput.readDouble();
	}

	public AxisAngle4d normalize() {
		double double1 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		this.x *= double1;
		this.y *= double1;
		this.z *= double1;
		return this;
	}

	public AxisAngle4d rotate(double double1) {
		this.angle += double1;
		this.angle = (this.angle < 0.0 ? 6.283185307179586 + this.angle % 6.283185307179586 : this.angle) % 6.283185307179586;
		return this;
	}

	public Vector3d transform(Vector3d vector3d) {
		return this.transform((Vector3dc)vector3d, (Vector3d)vector3d);
	}

	public Vector3d transform(Vector3dc vector3dc, Vector3d vector3d) {
		double double1 = Math.cos(this.angle);
		double double2 = Math.sin(this.angle);
		double double3 = this.x * vector3dc.x() + this.y * vector3dc.y() + this.z * vector3dc.z();
		vector3d.set(vector3dc.x() * double1 + double2 * (this.y * vector3dc.z() - this.z * vector3dc.y()) + (1.0 - double1) * double3 * this.x, vector3dc.y() * double1 + double2 * (this.z * vector3dc.x() - this.x * vector3dc.z()) + (1.0 - double1) * double3 * this.y, vector3dc.z() * double1 + double2 * (this.x * vector3dc.y() - this.y * vector3dc.x()) + (1.0 - double1) * double3 * this.z);
		return vector3d;
	}

	public Vector4d transform(Vector4d vector4d) {
		return this.transform(vector4d, vector4d);
	}

	public Vector4d transform(Vector4d vector4d, Vector4d vector4d2) {
		double double1 = Math.cos(this.angle);
		double double2 = Math.sin(this.angle);
		double double3 = this.x * vector4d.x + this.y * vector4d.y + this.z * vector4d.z;
		vector4d2.set(vector4d.x * double1 + double2 * (this.y * vector4d.z - this.z * vector4d.y) + (1.0 - double1) * double3 * this.x, vector4d.y * double1 + double2 * (this.z * vector4d.x - this.x * vector4d.z) + (1.0 - double1) * double3 * this.y, vector4d.z * double1 + double2 * (this.x * vector4d.y - this.y * vector4d.x) + (1.0 - double1) * double3 * this.z, vector4d2.w);
		return vector4d2;
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format(this.x) + numberFormat.format(this.y) + numberFormat.format(this.z) + " <|" + numberFormat.format(this.angle) + " )";
	}

	public int hashCode() {
		byte byte1 = 1;
		long long1 = Double.doubleToLongBits((this.angle < 0.0 ? 6.283185307179586 + this.angle % 6.283185307179586 : this.angle) % 6.283185307179586);
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
			AxisAngle4d axisAngle4d = (AxisAngle4d)object;
			if (Double.doubleToLongBits((this.angle < 0.0 ? 6.283185307179586 + this.angle % 6.283185307179586 : this.angle) % 6.283185307179586) != Double.doubleToLongBits((axisAngle4d.angle < 0.0 ? 6.283185307179586 + axisAngle4d.angle % 6.283185307179586 : axisAngle4d.angle) % 6.283185307179586)) {
				return false;
			} else if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(axisAngle4d.x)) {
				return false;
			} else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(axisAngle4d.y)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(axisAngle4d.z);
			}
		}
	}
}
