package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class AxisAngle4f implements Externalizable {
	private static final long serialVersionUID = 1L;
	public float angle;
	public float x;
	public float y;
	public float z;

	public AxisAngle4f() {
		this.z = 1.0F;
	}

	public AxisAngle4f(AxisAngle4f axisAngle4f) {
		this.x = axisAngle4f.x;
		this.y = axisAngle4f.y;
		this.z = axisAngle4f.z;
		this.angle = (float)(((double)axisAngle4f.angle < 0.0 ? 6.283185307179586 + (double)axisAngle4f.angle % 6.283185307179586 : (double)axisAngle4f.angle) % 6.283185307179586);
	}

	public AxisAngle4f(Quaternionfc quaternionfc) {
		float float1 = (float)Math.acos((double)quaternionfc.w());
		float float2 = (float)(1.0 / Math.sqrt(1.0 - (double)(quaternionfc.w() * quaternionfc.w())));
		this.x = quaternionfc.x() * float2;
		this.y = quaternionfc.y() * float2;
		this.z = quaternionfc.z() * float2;
		this.angle = float1 + float1;
	}

	public AxisAngle4f(float float1, float float2, float float3, float float4) {
		this.x = float2;
		this.y = float3;
		this.z = float4;
		this.angle = (float)(((double)float1 < 0.0 ? 6.283185307179586 + (double)float1 % 6.283185307179586 : (double)float1) % 6.283185307179586);
	}

	public AxisAngle4f(float float1, Vector3fc vector3fc) {
		this(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public AxisAngle4f set(AxisAngle4f axisAngle4f) {
		this.x = axisAngle4f.x;
		this.y = axisAngle4f.y;
		this.z = axisAngle4f.z;
		this.angle = axisAngle4f.angle;
		this.angle = (float)(((double)this.angle < 0.0 ? 6.283185307179586 + (double)this.angle % 6.283185307179586 : (double)this.angle) % 6.283185307179586);
		return this;
	}

	public AxisAngle4f set(float float1, float float2, float float3, float float4) {
		this.x = float2;
		this.y = float3;
		this.z = float4;
		this.angle = (float)(((double)float1 < 0.0 ? 6.283185307179586 + (double)float1 % 6.283185307179586 : (double)float1) % 6.283185307179586);
		return this;
	}

	public AxisAngle4f set(float float1, Vector3fc vector3fc) {
		return this.set(float1, vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public AxisAngle4f set(Quaternionfc quaternionfc) {
		double double1 = Math.acos((double)quaternionfc.w());
		double double2 = 1.0 / Math.sqrt(1.0 - (double)(quaternionfc.w() * quaternionfc.w()));
		this.x = (float)((double)quaternionfc.x() * double2);
		this.y = (float)((double)quaternionfc.y() * double2);
		this.z = (float)((double)quaternionfc.z() * double2);
		this.angle = (float)(double1 + double1);
		return this;
	}

	public AxisAngle4f set(Quaterniondc quaterniondc) {
		double double1 = Math.acos(quaterniondc.w());
		double double2 = 1.0 / Math.sqrt(1.0 - quaterniondc.w() * quaterniondc.w());
		this.x = (float)(quaterniondc.x() * double2);
		this.y = (float)(quaterniondc.y() * double2);
		this.z = (float)(quaterniondc.z() * double2);
		this.angle = (float)(double1 + double1);
		return this;
	}

	public AxisAngle4f set(Matrix3fc matrix3fc) {
		double double1 = ((double)(matrix3fc.m00() + matrix3fc.m11() + matrix3fc.m22()) - 1.0) * 0.5;
		this.x = matrix3fc.m12() - matrix3fc.m21();
		this.y = matrix3fc.m20() - matrix3fc.m02();
		this.z = matrix3fc.m01() - matrix3fc.m10();
		double double2 = 0.5 * Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
		this.angle = (float)Math.atan2(double2, double1);
		return this;
	}

	public AxisAngle4f set(Matrix3dc matrix3dc) {
		double double1 = (matrix3dc.m00() + matrix3dc.m11() + matrix3dc.m22() - 1.0) * 0.5;
		this.x = (float)(matrix3dc.m12() - matrix3dc.m21());
		this.y = (float)(matrix3dc.m20() - matrix3dc.m02());
		this.z = (float)(matrix3dc.m01() - matrix3dc.m10());
		double double2 = 0.5 * Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
		this.angle = (float)Math.atan2(double2, double1);
		return this;
	}

	public AxisAngle4f set(Matrix4fc matrix4fc) {
		double double1 = ((double)(matrix4fc.m00() + matrix4fc.m11() + matrix4fc.m22()) - 1.0) * 0.5;
		this.x = matrix4fc.m12() - matrix4fc.m21();
		this.y = matrix4fc.m20() - matrix4fc.m02();
		this.z = matrix4fc.m01() - matrix4fc.m10();
		double double2 = 0.5 * Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
		this.angle = (float)Math.atan2(double2, double1);
		return this;
	}

	public AxisAngle4f set(Matrix4x3fc matrix4x3fc) {
		double double1 = ((double)(matrix4x3fc.m00() + matrix4x3fc.m11() + matrix4x3fc.m22()) - 1.0) * 0.5;
		this.x = matrix4x3fc.m12() - matrix4x3fc.m21();
		this.y = matrix4x3fc.m20() - matrix4x3fc.m02();
		this.z = matrix4x3fc.m01() - matrix4x3fc.m10();
		double double2 = 0.5 * Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
		this.angle = (float)Math.atan2(double2, double1);
		return this;
	}

	public AxisAngle4f set(Matrix4dc matrix4dc) {
		double double1 = (matrix4dc.m00() + matrix4dc.m11() + matrix4dc.m22() - 1.0) * 0.5;
		this.x = (float)(matrix4dc.m12() - matrix4dc.m21());
		this.y = (float)(matrix4dc.m20() - matrix4dc.m02());
		this.z = (float)(matrix4dc.m01() - matrix4dc.m10());
		double double2 = 0.5 * Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
		this.angle = (float)Math.atan2(double2, double1);
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
		objectOutput.writeFloat(this.angle);
		objectOutput.writeFloat(this.x);
		objectOutput.writeFloat(this.y);
		objectOutput.writeFloat(this.z);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.angle = objectInput.readFloat();
		this.x = objectInput.readFloat();
		this.y = objectInput.readFloat();
		this.z = objectInput.readFloat();
	}

	public AxisAngle4f normalize() {
		float float1 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z)));
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		return this;
	}

	public AxisAngle4f rotate(float float1) {
		this.angle += float1;
		this.angle = (float)(((double)this.angle < 0.0 ? 6.283185307179586 + (double)this.angle % 6.283185307179586 : (double)this.angle) % 6.283185307179586);
		return this;
	}

	public Vector3f transform(Vector3f vector3f) {
		return this.transform((Vector3fc)vector3f, (Vector3f)vector3f);
	}

	public Vector3f transform(Vector3fc vector3fc, Vector3f vector3f) {
		double double1 = Math.cos((double)this.angle);
		double double2 = Math.sin((double)this.angle);
		float float1 = this.x * vector3fc.x() + this.y * vector3fc.y() + this.z * vector3fc.z();
		vector3f.set((float)((double)vector3fc.x() * double1 + double2 * (double)(this.y * vector3fc.z() - this.z * vector3fc.y()) + (1.0 - double1) * (double)float1 * (double)this.x), (float)((double)vector3fc.y() * double1 + double2 * (double)(this.z * vector3fc.x() - this.x * vector3fc.z()) + (1.0 - double1) * (double)float1 * (double)this.y), (float)((double)vector3fc.z() * double1 + double2 * (double)(this.x * vector3fc.y() - this.y * vector3fc.x()) + (1.0 - double1) * (double)float1 * (double)this.z));
		return vector3f;
	}

	public Vector4f transform(Vector4f vector4f) {
		return this.transform((Vector4fc)vector4f, (Vector4f)vector4f);
	}

	public Vector4f transform(Vector4fc vector4fc, Vector4f vector4f) {
		double double1 = Math.cos((double)this.angle);
		double double2 = Math.sin((double)this.angle);
		float float1 = this.x * vector4fc.x() + this.y * vector4fc.y() + this.z * vector4fc.z();
		vector4f.set((float)((double)vector4fc.x() * double1 + double2 * (double)(this.y * vector4fc.z() - this.z * vector4fc.y()) + (1.0 - double1) * (double)float1 * (double)this.x), (float)((double)vector4fc.y() * double1 + double2 * (double)(this.z * vector4fc.x() - this.x * vector4fc.z()) + (1.0 - double1) * (double)float1 * (double)this.y), (float)((double)vector4fc.z() * double1 + double2 * (double)(this.x * vector4fc.y() - this.y * vector4fc.x()) + (1.0 - double1) * (double)float1 * (double)this.z), vector4f.w);
		return vector4f;
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format((double)this.x) + numberFormat.format((double)this.y) + numberFormat.format((double)this.z) + " <|" + numberFormat.format((double)this.angle) + " )";
	}

	public int hashCode() {
		byte byte1 = 1;
		float float1 = (float)(((double)this.angle < 0.0 ? 6.283185307179586 + (double)this.angle % 6.283185307179586 : (double)this.angle) % 6.283185307179586);
		int int1 = 31 * byte1 + Float.floatToIntBits(float1);
		int1 = 31 * int1 + Float.floatToIntBits(this.x);
		int1 = 31 * int1 + Float.floatToIntBits(this.y);
		int1 = 31 * int1 + Float.floatToIntBits(this.z);
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
			AxisAngle4f axisAngle4f = (AxisAngle4f)object;
			float float1 = (float)(((double)this.angle < 0.0 ? 6.283185307179586 + (double)this.angle % 6.283185307179586 : (double)this.angle) % 6.283185307179586);
			float float2 = (float)(((double)axisAngle4f.angle < 0.0 ? 6.283185307179586 + (double)axisAngle4f.angle % 6.283185307179586 : (double)axisAngle4f.angle) % 6.283185307179586);
			if (Float.floatToIntBits(float1) != Float.floatToIntBits(float2)) {
				return false;
			} else if (Float.floatToIntBits(this.x) != Float.floatToIntBits(axisAngle4f.x)) {
				return false;
			} else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(axisAngle4f.y)) {
				return false;
			} else {
				return Float.floatToIntBits(this.z) == Float.floatToIntBits(axisAngle4f.z);
			}
		}
	}
}
