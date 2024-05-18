package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Vector3f implements Externalizable,Vector3fc {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;
	public float z;

	public Vector3f() {
	}

	public Vector3f(float float1) {
		this(float1, float1, float1);
	}

	public Vector3f(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
	}

	public Vector3f(Vector3fc vector3fc) {
		this.x = vector3fc.x();
		this.y = vector3fc.y();
		this.z = vector3fc.z();
	}

	public Vector3f(Vector2fc vector2fc, float float1) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
		this.z = float1;
	}

	public Vector3f(ByteBuffer byteBuffer) {
		this(byteBuffer.position(), byteBuffer);
	}

	public Vector3f(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector3f(FloatBuffer floatBuffer) {
		this(floatBuffer.position(), floatBuffer);
	}

	public Vector3f(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, int1, floatBuffer);
	}

	public float x() {
		return this.x;
	}

	public float y() {
		return this.y;
	}

	public float z() {
		return this.z;
	}

	public Vector3f set(Vector3fc vector3fc) {
		this.x = vector3fc.x();
		this.y = vector3fc.y();
		this.z = vector3fc.z();
		return this;
	}

	public Vector3f set(Vector3dc vector3dc) {
		this.x = (float)vector3dc.x();
		this.y = (float)vector3dc.y();
		this.z = (float)vector3dc.z();
		return this;
	}

	public Vector3f set(Vector2fc vector2fc, float float1) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
		this.z = float1;
		return this;
	}

	public Vector3f set(float float1) {
		return this.set(float1, float1, float1);
	}

	public Vector3f set(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		return this;
	}

	public Vector3f set(ByteBuffer byteBuffer) {
		return this.set(byteBuffer.position(), byteBuffer);
	}

	public Vector3f set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector3f set(FloatBuffer floatBuffer) {
		return this.set(floatBuffer.position(), floatBuffer);
	}

	public Vector3f set(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, int1, floatBuffer);
		return this;
	}

	public Vector3f setComponent(int int1, float float1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			this.x = float1;
			break;
		
		case 1: 
			this.y = float1;
			break;
		
		case 2: 
			this.z = float1;
			break;
		
		default: 
			throw new IllegalArgumentException();
		
		}
		return this;
	}

	public FloatBuffer get(FloatBuffer floatBuffer) {
		return this.get(floatBuffer.position(), floatBuffer);
	}

	public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		return this.get(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Vector3f sub(Vector3fc vector3fc) {
		this.x -= vector3fc.x();
		this.y -= vector3fc.y();
		this.z -= vector3fc.z();
		return this;
	}

	public Vector3f sub(Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.x = this.x - vector3fc.x();
		vector3f.y = this.y - vector3fc.y();
		vector3f.z = this.z - vector3fc.z();
		return vector3f;
	}

	public Vector3f sub(float float1, float float2, float float3) {
		this.x -= float1;
		this.y -= float2;
		this.z -= float3;
		return this;
	}

	public Vector3f sub(float float1, float float2, float float3, Vector3f vector3f) {
		vector3f.x = this.x - float1;
		vector3f.y = this.y - float2;
		vector3f.z = this.z - float3;
		return vector3f;
	}

	public Vector3f add(Vector3fc vector3fc) {
		this.x += vector3fc.x();
		this.y += vector3fc.y();
		this.z += vector3fc.z();
		return this;
	}

	public Vector3f add(Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.x = this.x + vector3fc.x();
		vector3f.y = this.y + vector3fc.y();
		vector3f.z = this.z + vector3fc.z();
		return vector3f;
	}

	public Vector3f add(float float1, float float2, float float3) {
		this.x += float1;
		this.y += float2;
		this.z += float3;
		return this;
	}

	public Vector3f add(float float1, float float2, float float3, Vector3f vector3f) {
		vector3f.x = this.x + float1;
		vector3f.y = this.y + float2;
		vector3f.z = this.z + float3;
		return vector3f;
	}

	public Vector3f fma(Vector3fc vector3fc, Vector3fc vector3fc2) {
		this.x += vector3fc.x() * vector3fc2.x();
		this.y += vector3fc.y() * vector3fc2.y();
		this.z += vector3fc.z() * vector3fc2.z();
		return this;
	}

	public Vector3f fma(float float1, Vector3fc vector3fc) {
		this.x += float1 * vector3fc.x();
		this.y += float1 * vector3fc.y();
		this.z += float1 * vector3fc.z();
		return this;
	}

	public Vector3f fma(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3f vector3f) {
		vector3f.x = this.x + vector3fc.x() * vector3fc2.x();
		vector3f.y = this.y + vector3fc.y() * vector3fc2.y();
		vector3f.z = this.z + vector3fc.z() * vector3fc2.z();
		return vector3f;
	}

	public Vector3f fma(float float1, Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.x = this.x + float1 * vector3fc.x();
		vector3f.y = this.y + float1 * vector3fc.y();
		vector3f.z = this.z + float1 * vector3fc.z();
		return vector3f;
	}

	public Vector3f mul(Vector3fc vector3fc) {
		this.x *= vector3fc.x();
		this.y *= vector3fc.y();
		this.z *= vector3fc.z();
		return this;
	}

	public Vector3f mul(Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.x = this.x * vector3fc.x();
		vector3f.y = this.y * vector3fc.y();
		vector3f.z = this.z * vector3fc.z();
		return vector3f;
	}

	public Vector3f div(Vector3fc vector3fc) {
		this.x /= vector3fc.x();
		this.y /= vector3fc.y();
		this.z /= vector3fc.z();
		return this;
	}

	public Vector3f div(Vector3fc vector3fc, Vector3f vector3f) {
		vector3f.x = this.x / vector3fc.x();
		vector3f.y = this.y / vector3fc.y();
		vector3f.z = this.z / vector3fc.z();
		return vector3f;
	}

	public Vector3f mulProject(Matrix4fc matrix4fc, Vector3f vector3f) {
		float float1 = 1.0F / (matrix4fc.m03() * this.x + matrix4fc.m13() * this.y + matrix4fc.m23() * this.z + matrix4fc.m33());
		vector3f.set((matrix4fc.m00() * this.x + matrix4fc.m10() * this.y + matrix4fc.m20() * this.z + matrix4fc.m30()) * float1, (matrix4fc.m01() * this.x + matrix4fc.m11() * this.y + matrix4fc.m21() * this.z + matrix4fc.m31()) * float1, (matrix4fc.m02() * this.x + matrix4fc.m12() * this.y + matrix4fc.m22() * this.z + matrix4fc.m32()) * float1);
		return vector3f;
	}

	public Vector3f mulProject(Matrix4fc matrix4fc) {
		return this.mulProject(matrix4fc, this);
	}

	public Vector3f mul(Matrix3fc matrix3fc) {
		return this.mul(matrix3fc, this);
	}

	public Vector3f mul(Matrix3fc matrix3fc, Vector3f vector3f) {
		vector3f.set(matrix3fc.m00() * this.x + matrix3fc.m10() * this.y + matrix3fc.m20() * this.z, matrix3fc.m01() * this.x + matrix3fc.m11() * this.y + matrix3fc.m21() * this.z, matrix3fc.m02() * this.x + matrix3fc.m12() * this.y + matrix3fc.m22() * this.z);
		return vector3f;
	}

	public Vector3f mulTranspose(Matrix3fc matrix3fc) {
		return this.mul(matrix3fc, this);
	}

	public Vector3f mulTranspose(Matrix3fc matrix3fc, Vector3f vector3f) {
		vector3f.set(matrix3fc.m00() * this.x + matrix3fc.m01() * this.y + matrix3fc.m02() * this.z, matrix3fc.m10() * this.x + matrix3fc.m11() * this.y + matrix3fc.m12() * this.z, matrix3fc.m20() * this.x + matrix3fc.m21() * this.y + matrix3fc.m22() * this.z);
		return vector3f;
	}

	public Vector3f mulPosition(Matrix4fc matrix4fc) {
		return this.mulPosition(matrix4fc, this);
	}

	public Vector3f mulPosition(Matrix4x3fc matrix4x3fc) {
		return this.mulPosition(matrix4x3fc, this);
	}

	public Vector3f mulPosition(Matrix4fc matrix4fc, Vector3f vector3f) {
		vector3f.set(matrix4fc.m00() * this.x + matrix4fc.m10() * this.y + matrix4fc.m20() * this.z + matrix4fc.m30(), matrix4fc.m01() * this.x + matrix4fc.m11() * this.y + matrix4fc.m21() * this.z + matrix4fc.m31(), matrix4fc.m02() * this.x + matrix4fc.m12() * this.y + matrix4fc.m22() * this.z + matrix4fc.m32());
		return vector3f;
	}

	public Vector3f mulPosition(Matrix4x3fc matrix4x3fc, Vector3f vector3f) {
		vector3f.set(matrix4x3fc.m00() * this.x + matrix4x3fc.m10() * this.y + matrix4x3fc.m20() * this.z + matrix4x3fc.m30(), matrix4x3fc.m01() * this.x + matrix4x3fc.m11() * this.y + matrix4x3fc.m21() * this.z + matrix4x3fc.m31(), matrix4x3fc.m02() * this.x + matrix4x3fc.m12() * this.y + matrix4x3fc.m22() * this.z + matrix4x3fc.m32());
		return vector3f;
	}

	public Vector3f mulTransposePosition(Matrix4fc matrix4fc) {
		return this.mulTransposePosition(matrix4fc, this);
	}

	public Vector3f mulTransposePosition(Matrix4fc matrix4fc, Vector3f vector3f) {
		vector3f.set(matrix4fc.m00() * this.x + matrix4fc.m01() * this.y + matrix4fc.m02() * this.z + matrix4fc.m03(), matrix4fc.m10() * this.x + matrix4fc.m11() * this.y + matrix4fc.m12() * this.z + matrix4fc.m13(), matrix4fc.m20() * this.x + matrix4fc.m21() * this.y + matrix4fc.m22() * this.z + matrix4fc.m23());
		return vector3f;
	}

	public float mulPositionW(Matrix4fc matrix4fc) {
		return this.mulPositionW(matrix4fc, this);
	}

	public float mulPositionW(Matrix4fc matrix4fc, Vector3f vector3f) {
		float float1 = matrix4fc.m03() * this.x + matrix4fc.m13() * this.y + matrix4fc.m23() * this.z + matrix4fc.m33();
		vector3f.set(matrix4fc.m00() * this.x + matrix4fc.m10() * this.y + matrix4fc.m20() * this.z + matrix4fc.m30(), matrix4fc.m01() * this.x + matrix4fc.m11() * this.y + matrix4fc.m21() * this.z + matrix4fc.m31(), matrix4fc.m02() * this.x + matrix4fc.m12() * this.y + matrix4fc.m22() * this.z + matrix4fc.m32());
		return float1;
	}

	public Vector3f mulDirection(Matrix4fc matrix4fc) {
		return this.mulDirection(matrix4fc, this);
	}

	public Vector3f mulDirection(Matrix4x3fc matrix4x3fc) {
		return this.mulDirection(matrix4x3fc, this);
	}

	public Vector3f mulDirection(Matrix4fc matrix4fc, Vector3f vector3f) {
		vector3f.set(matrix4fc.m00() * this.x + matrix4fc.m10() * this.y + matrix4fc.m20() * this.z, matrix4fc.m01() * this.x + matrix4fc.m11() * this.y + matrix4fc.m21() * this.z, matrix4fc.m02() * this.x + matrix4fc.m12() * this.y + matrix4fc.m22() * this.z);
		return vector3f;
	}

	public Vector3f mulDirection(Matrix4x3fc matrix4x3fc, Vector3f vector3f) {
		vector3f.set(matrix4x3fc.m00() * this.x + matrix4x3fc.m10() * this.y + matrix4x3fc.m20() * this.z, matrix4x3fc.m01() * this.x + matrix4x3fc.m11() * this.y + matrix4x3fc.m21() * this.z, matrix4x3fc.m02() * this.x + matrix4x3fc.m12() * this.y + matrix4x3fc.m22() * this.z);
		return vector3f;
	}

	public Vector3f mulTransposeDirection(Matrix4fc matrix4fc) {
		return this.mulTransposeDirection(matrix4fc, this);
	}

	public Vector3f mulTransposeDirection(Matrix4fc matrix4fc, Vector3f vector3f) {
		vector3f.set(matrix4fc.m00() * this.x + matrix4fc.m01() * this.y + matrix4fc.m02() * this.z, matrix4fc.m10() * this.x + matrix4fc.m11() * this.y + matrix4fc.m12() * this.z, matrix4fc.m20() * this.x + matrix4fc.m21() * this.y + matrix4fc.m22() * this.z);
		return vector3f;
	}

	public Vector3f mul(float float1) {
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		return this;
	}

	public Vector3f mul(float float1, Vector3f vector3f) {
		vector3f.x = this.x * float1;
		vector3f.y = this.y * float1;
		vector3f.z = this.z * float1;
		return vector3f;
	}

	public Vector3f mul(float float1, float float2, float float3) {
		this.x *= float1;
		this.y *= float2;
		this.z *= float3;
		return this;
	}

	public Vector3f mul(float float1, float float2, float float3, Vector3f vector3f) {
		vector3f.x = this.x * float1;
		vector3f.y = this.y * float2;
		vector3f.z = this.z * float3;
		return vector3f;
	}

	public Vector3f div(float float1) {
		this.x /= float1;
		this.y /= float1;
		this.z /= float1;
		return this;
	}

	public Vector3f div(float float1, Vector3f vector3f) {
		vector3f.x = this.x / float1;
		vector3f.y = this.y / float1;
		vector3f.z = this.z / float1;
		return vector3f;
	}

	public Vector3f div(float float1, float float2, float float3) {
		this.x /= float1;
		this.y /= float2;
		this.z /= float3;
		return this;
	}

	public Vector3f div(float float1, float float2, float float3, Vector3f vector3f) {
		vector3f.x = this.x / float1;
		vector3f.y = this.y / float2;
		vector3f.z = this.z / float3;
		return vector3f;
	}

	public Vector3f rotate(Quaternionfc quaternionfc) {
		quaternionfc.transform((Vector3fc)this, (Vector3f)this);
		return this;
	}

	public Vector3f rotate(Quaternionfc quaternionfc, Vector3f vector3f) {
		quaternionfc.transform((Vector3fc)this, (Vector3f)vector3f);
		return vector3f;
	}

	public Quaternionf rotationTo(Vector3fc vector3fc, Quaternionf quaternionf) {
		return quaternionf.rotationTo(this, vector3fc);
	}

	public Quaternionf rotationTo(float float1, float float2, float float3, Quaternionf quaternionf) {
		return quaternionf.rotationTo(this.x, this.y, this.z, float1, float2, float3);
	}

	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	public float length() {
		return (float)Math.sqrt((double)this.lengthSquared());
	}

	public Vector3f normalize() {
		float float1 = 1.0F / this.length();
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		return this;
	}

	public Vector3f normalize(Vector3f vector3f) {
		float float1 = 1.0F / this.length();
		vector3f.x = this.x * float1;
		vector3f.y = this.y * float1;
		vector3f.z = this.z * float1;
		return vector3f;
	}

	public Vector3f cross(Vector3fc vector3fc) {
		return this.set(this.y * vector3fc.z() - this.z * vector3fc.y(), this.z * vector3fc.x() - this.x * vector3fc.z(), this.x * vector3fc.y() - this.y * vector3fc.x());
	}

	public Vector3f cross(float float1, float float2, float float3) {
		return this.set(this.y * float3 - this.z * float2, this.z * float1 - this.x * float3, this.x * float2 - this.y * float1);
	}

	public Vector3f cross(Vector3fc vector3fc, Vector3f vector3f) {
		return vector3f.set(this.y * vector3fc.z() - this.z * vector3fc.y(), this.z * vector3fc.x() - this.x * vector3fc.z(), this.x * vector3fc.y() - this.y * vector3fc.x());
	}

	public Vector3f cross(float float1, float float2, float float3, Vector3f vector3f) {
		return vector3f.set(this.y * float3 - this.z * float2, this.z * float1 - this.x * float3, this.x * float2 - this.y * float1);
	}

	public float distance(Vector3fc vector3fc) {
		float float1 = vector3fc.x() - this.x;
		float float2 = vector3fc.y() - this.y;
		float float3 = vector3fc.z() - this.z;
		return (float)Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3));
	}

	public float distance(float float1, float float2, float float3) {
		float float4 = this.x - float1;
		float float5 = this.y - float2;
		float float6 = this.z - float3;
		return (float)Math.sqrt((double)(float4 * float4 + float5 * float5 + float6 * float6));
	}

	public float distanceSquared(Vector3fc vector3fc) {
		float float1 = vector3fc.x() - this.x;
		float float2 = vector3fc.y() - this.y;
		float float3 = vector3fc.z() - this.z;
		return float1 * float1 + float2 * float2 + float3 * float3;
	}

	public float distanceSquared(float float1, float float2, float float3) {
		float float4 = this.x - float1;
		float float5 = this.y - float2;
		float float6 = this.z - float3;
		return float4 * float4 + float5 * float5 + float6 * float6;
	}

	public float dot(Vector3fc vector3fc) {
		return this.x * vector3fc.x() + this.y * vector3fc.y() + this.z * vector3fc.z();
	}

	public float dot(float float1, float float2, float float3) {
		return this.x * float1 + this.y * float2 + this.z * float3;
	}

	public float angleCos(Vector3fc vector3fc) {
		double double1 = (double)(this.x * this.x + this.y * this.y + this.z * this.z);
		double double2 = (double)(vector3fc.x() * vector3fc.x() + vector3fc.y() * vector3fc.y() + vector3fc.z() * vector3fc.z());
		double double3 = (double)(this.x * vector3fc.x() + this.y * vector3fc.y() + this.z * vector3fc.z());
		return (float)(double3 / Math.sqrt(double1 * double2));
	}

	public float angle(Vector3fc vector3fc) {
		float float1 = this.angleCos(vector3fc);
		float1 = float1 < 1.0F ? float1 : 1.0F;
		float1 = float1 > -1.0F ? float1 : -1.0F;
		return (float)Math.acos((double)float1);
	}

	public Vector3f min(Vector3fc vector3fc) {
		this.x = this.x < vector3fc.x() ? this.x : vector3fc.x();
		this.y = this.y < vector3fc.y() ? this.y : vector3fc.y();
		this.z = this.z < vector3fc.z() ? this.z : vector3fc.z();
		return this;
	}

	public Vector3f max(Vector3fc vector3fc) {
		this.x = this.x > vector3fc.x() ? this.x : vector3fc.x();
		this.y = this.y > vector3fc.y() ? this.y : vector3fc.y();
		this.z = this.z > vector3fc.z() ? this.z : vector3fc.z();
		return this;
	}

	public Vector3f zero() {
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
		return this;
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format((double)this.x) + " " + numberFormat.format((double)this.y) + " " + numberFormat.format((double)this.z) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeFloat(this.x);
		objectOutput.writeFloat(this.y);
		objectOutput.writeFloat(this.z);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readFloat();
		this.y = objectInput.readFloat();
		this.z = objectInput.readFloat();
	}

	public Vector3f negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	public Vector3f negate(Vector3f vector3f) {
		vector3f.x = -this.x;
		vector3f.y = -this.y;
		vector3f.z = -this.z;
		return vector3f;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + Float.floatToIntBits(this.x);
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
			Vector3f vector3f = (Vector3f)object;
			if (Float.floatToIntBits(this.x) != Float.floatToIntBits(vector3f.x)) {
				return false;
			} else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(vector3f.y)) {
				return false;
			} else {
				return Float.floatToIntBits(this.z) == Float.floatToIntBits(vector3f.z);
			}
		}
	}

	public Vector3f reflect(Vector3fc vector3fc) {
		float float1 = this.dot(vector3fc);
		this.x -= (float1 + float1) * vector3fc.x();
		this.y -= (float1 + float1) * vector3fc.y();
		this.z -= (float1 + float1) * vector3fc.z();
		return this;
	}

	public Vector3f reflect(float float1, float float2, float float3) {
		float float4 = this.dot(float1, float2, float3);
		this.x -= (float4 + float4) * float1;
		this.y -= (float4 + float4) * float2;
		this.z -= (float4 + float4) * float3;
		return this;
	}

	public Vector3f reflect(Vector3fc vector3fc, Vector3f vector3f) {
		float float1 = this.dot(vector3fc);
		vector3f.x = this.x - (float1 + float1) * vector3fc.x();
		vector3f.y = this.y - (float1 + float1) * vector3fc.y();
		vector3f.z = this.z - (float1 + float1) * vector3fc.z();
		return vector3f;
	}

	public Vector3f reflect(float float1, float float2, float float3, Vector3f vector3f) {
		float float4 = this.dot(float1, float2, float3);
		vector3f.x = this.x - (float4 + float4) * float1;
		vector3f.y = this.y - (float4 + float4) * float2;
		vector3f.z = this.z - (float4 + float4) * float3;
		return vector3f;
	}

	public Vector3f half(Vector3fc vector3fc) {
		return this.add(vector3fc).normalize();
	}

	public Vector3f half(float float1, float float2, float float3) {
		return this.add(float1, float2, float3).normalize();
	}

	public Vector3f half(Vector3fc vector3fc, Vector3f vector3f) {
		return vector3f.set((Vector3fc)this).add(vector3fc).normalize();
	}

	public Vector3f half(float float1, float float2, float float3, Vector3f vector3f) {
		return vector3f.set((Vector3fc)this).add(float1, float2, float3).normalize();
	}

	public Vector3f smoothStep(Vector3fc vector3fc, float float1, Vector3f vector3f) {
		float float2 = float1 * float1;
		float float3 = float2 * float1;
		vector3f.x = (this.x + this.x - vector3fc.x() - vector3fc.x()) * float3 + (3.0F * vector3fc.x() - 3.0F * this.x) * float2 + this.x * float1 + this.x;
		vector3f.y = (this.y + this.y - vector3fc.y() - vector3fc.y()) * float3 + (3.0F * vector3fc.y() - 3.0F * this.y) * float2 + this.y * float1 + this.y;
		vector3f.z = (this.z + this.z - vector3fc.z() - vector3fc.z()) * float3 + (3.0F * vector3fc.z() - 3.0F * this.z) * float2 + this.z * float1 + this.z;
		return vector3f;
	}

	public Vector3f hermite(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, float float1, Vector3f vector3f) {
		float float2 = float1 * float1;
		float float3 = float2 * float1;
		vector3f.x = (this.x + this.x - vector3fc2.x() - vector3fc2.x() + vector3fc3.x() + vector3fc.x()) * float3 + (3.0F * vector3fc2.x() - 3.0F * this.x - vector3fc.x() - vector3fc.x() - vector3fc3.x()) * float2 + this.x * float1 + this.x;
		vector3f.y = (this.y + this.y - vector3fc2.y() - vector3fc2.y() + vector3fc3.y() + vector3fc.y()) * float3 + (3.0F * vector3fc2.y() - 3.0F * this.y - vector3fc.y() - vector3fc.y() - vector3fc3.y()) * float2 + this.y * float1 + this.y;
		vector3f.z = (this.z + this.z - vector3fc2.z() - vector3fc2.z() + vector3fc3.z() + vector3fc.z()) * float3 + (3.0F * vector3fc2.z() - 3.0F * this.z - vector3fc.z() - vector3fc.z() - vector3fc3.z()) * float2 + this.z * float1 + this.z;
		return vector3f;
	}

	public Vector3f lerp(Vector3fc vector3fc, float float1) {
		return this.lerp(vector3fc, float1, this);
	}

	public Vector3f lerp(Vector3fc vector3fc, float float1, Vector3f vector3f) {
		vector3f.x = this.x + (vector3fc.x() - this.x) * float1;
		vector3f.y = this.y + (vector3fc.y() - this.y) * float1;
		vector3f.z = this.z + (vector3fc.z() - this.z) * float1;
		return vector3f;
	}

	public float get(int int1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			return this.x;
		
		case 1: 
			return this.y;
		
		case 2: 
			return this.z;
		
		default: 
			throw new IllegalArgumentException();
		
		}
	}

	public int maxComponent() {
		float float1 = Math.abs(this.x);
		float float2 = Math.abs(this.y);
		float float3 = Math.abs(this.z);
		if (float1 >= float2 && float1 >= float3) {
			return 0;
		} else {
			return float2 >= float3 ? 1 : 2;
		}
	}

	public int minComponent() {
		float float1 = Math.abs(this.x);
		float float2 = Math.abs(this.y);
		float float3 = Math.abs(this.z);
		if (float1 < float2 && float1 < float3) {
			return 0;
		} else {
			return float2 < float3 ? 1 : 2;
		}
	}

	public Vector3f orthogonalize(Vector3fc vector3fc, Vector3f vector3f) {
		float float1 = 1.0F / (float)Math.sqrt((double)(vector3fc.x() * vector3fc.x() + vector3fc.y() * vector3fc.y() + vector3fc.z() * vector3fc.z()));
		float float2 = vector3fc.x() * float1;
		float float3 = vector3fc.y() * float1;
		float float4 = vector3fc.z() * float1;
		float float5 = float2 * this.x + float3 * this.y + float4 * this.z;
		float float6 = this.x - float5 * float2;
		float float7 = this.y - float5 * float3;
		float float8 = this.z - float5 * float4;
		float float9 = 1.0F / (float)Math.sqrt((double)(float6 * float6 + float7 * float7 + float8 * float8));
		vector3f.x = float6 * float9;
		vector3f.y = float7 * float9;
		vector3f.z = float8 * float9;
		return vector3f;
	}

	public Vector3f orthogonalize(Vector3fc vector3fc) {
		return this.orthogonalize(vector3fc, this);
	}

	public Vector3f orthogonalizeUnit(Vector3fc vector3fc, Vector3f vector3f) {
		float float1 = vector3fc.x();
		float float2 = vector3fc.y();
		float float3 = vector3fc.z();
		float float4 = float1 * this.x + float2 * this.y + float3 * this.z;
		float float5 = this.x - float4 * float1;
		float float6 = this.y - float4 * float2;
		float float7 = this.z - float4 * float3;
		float float8 = 1.0F / (float)Math.sqrt((double)(float5 * float5 + float6 * float6 + float7 * float7));
		vector3f.x = float5 * float8;
		vector3f.y = float6 * float8;
		vector3f.z = float7 * float8;
		return vector3f;
	}

	public Vector3f orthogonalizeUnit(Vector3fc vector3fc) {
		return this.orthogonalizeUnit(vector3fc, this);
	}

	public Vector3fc toImmutable() {
		return (Vector3fc)(!Options.DEBUG ? this : new Vector3f.Proxy(this));
	}

	private final class Proxy implements Vector3fc {
		private final Vector3fc delegate;

		Proxy(Vector3fc vector3fc) {
			this.delegate = vector3fc;
		}

		public float x() {
			return this.delegate.x();
		}

		public float y() {
			return this.delegate.y();
		}

		public float z() {
			return this.delegate.z();
		}

		public FloatBuffer get(FloatBuffer floatBuffer) {
			return this.delegate.get(floatBuffer);
		}

		public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
			return this.delegate.get(int1, floatBuffer);
		}

		public ByteBuffer get(ByteBuffer byteBuffer) {
			return this.delegate.get(byteBuffer);
		}

		public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
			return this.delegate.get(int1, byteBuffer);
		}

		public Vector3f sub(Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.sub(vector3fc, vector3f);
		}

		public Vector3f sub(float float1, float float2, float float3, Vector3f vector3f) {
			return this.delegate.sub(float1, float2, float3, vector3f);
		}

		public Vector3f add(Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.add(vector3fc, vector3f);
		}

		public Vector3f add(float float1, float float2, float float3, Vector3f vector3f) {
			return this.delegate.add(float1, float2, float3, vector3f);
		}

		public Vector3f fma(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3f vector3f) {
			return this.delegate.fma(vector3fc, vector3fc2, vector3f);
		}

		public Vector3f fma(float float1, Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.fma(float1, vector3fc, vector3f);
		}

		public Vector3f mul(Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.mul(vector3fc, vector3f);
		}

		public Vector3f div(Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.div(vector3fc, vector3f);
		}

		public Vector3f mulProject(Matrix4fc matrix4fc, Vector3f vector3f) {
			return this.delegate.mulProject(matrix4fc, vector3f);
		}

		public Vector3f mul(Matrix3fc matrix3fc, Vector3f vector3f) {
			return this.delegate.mul(matrix3fc, vector3f);
		}

		public Vector3f mulTranspose(Matrix3fc matrix3fc, Vector3f vector3f) {
			return this.delegate.mulTranspose(matrix3fc, vector3f);
		}

		public Vector3f mulPosition(Matrix4fc matrix4fc, Vector3f vector3f) {
			return this.delegate.mulPosition(matrix4fc, vector3f);
		}

		public Vector3f mulPosition(Matrix4x3fc matrix4x3fc, Vector3f vector3f) {
			return this.delegate.mulPosition(matrix4x3fc, vector3f);
		}

		public Vector3f mulTransposePosition(Matrix4fc matrix4fc, Vector3f vector3f) {
			return this.delegate.mulTransposePosition(matrix4fc, vector3f);
		}

		public float mulPositionW(Matrix4fc matrix4fc, Vector3f vector3f) {
			return this.delegate.mulPositionW(matrix4fc, vector3f);
		}

		public Vector3f mulDirection(Matrix4fc matrix4fc, Vector3f vector3f) {
			return this.delegate.mulDirection(matrix4fc, vector3f);
		}

		public Vector3f mulDirection(Matrix4x3fc matrix4x3fc, Vector3f vector3f) {
			return this.delegate.mulDirection(matrix4x3fc, vector3f);
		}

		public Vector3f mulTransposeDirection(Matrix4fc matrix4fc, Vector3f vector3f) {
			return this.delegate.mulTransposeDirection(matrix4fc, vector3f);
		}

		public Vector3f mul(float float1, Vector3f vector3f) {
			return this.delegate.mul(float1, vector3f);
		}

		public Vector3f mul(float float1, float float2, float float3, Vector3f vector3f) {
			return this.delegate.mul(float1, float2, float3, vector3f);
		}

		public Vector3f div(float float1, Vector3f vector3f) {
			return this.delegate.div(float1, vector3f);
		}

		public Vector3f div(float float1, float float2, float float3, Vector3f vector3f) {
			return this.delegate.div(float1, float2, float3, vector3f);
		}

		public Vector3f rotate(Quaternionfc quaternionfc, Vector3f vector3f) {
			return this.delegate.rotate(quaternionfc, vector3f);
		}

		public Quaternionf rotationTo(Vector3fc vector3fc, Quaternionf quaternionf) {
			return this.delegate.rotationTo(vector3fc, quaternionf);
		}

		public Quaternionf rotationTo(float float1, float float2, float float3, Quaternionf quaternionf) {
			return this.delegate.rotationTo(float1, float2, float3, quaternionf);
		}

		public float lengthSquared() {
			return this.delegate.lengthSquared();
		}

		public float length() {
			return this.delegate.length();
		}

		public Vector3f normalize(Vector3f vector3f) {
			return this.delegate.normalize(vector3f);
		}

		public Vector3f cross(Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.cross(vector3fc, vector3f);
		}

		public Vector3f cross(float float1, float float2, float float3, Vector3f vector3f) {
			return this.delegate.cross(float1, float2, float3, vector3f);
		}

		public float distance(Vector3fc vector3fc) {
			return this.delegate.distance(vector3fc);
		}

		public float distance(float float1, float float2, float float3) {
			return this.delegate.distance(float1, float2, float3);
		}

		public float distanceSquared(Vector3fc vector3fc) {
			return this.delegate.distanceSquared(vector3fc);
		}

		public float distanceSquared(float float1, float float2, float float3) {
			return this.delegate.distanceSquared(float1, float2, float3);
		}

		public float dot(Vector3fc vector3fc) {
			return this.delegate.dot(vector3fc);
		}

		public float dot(float float1, float float2, float float3) {
			return this.delegate.dot(float1, float2, float3);
		}

		public float angleCos(Vector3fc vector3fc) {
			return this.delegate.angleCos(vector3fc);
		}

		public float angle(Vector3fc vector3fc) {
			return this.delegate.angle(vector3fc);
		}

		public Vector3f negate(Vector3f vector3f) {
			return this.delegate.negate(vector3f);
		}

		public Vector3f reflect(Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.reflect(vector3fc, vector3f);
		}

		public Vector3f reflect(float float1, float float2, float float3, Vector3f vector3f) {
			return this.delegate.reflect(float1, float2, float3, vector3f);
		}

		public Vector3f half(Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.half(vector3fc, vector3f);
		}

		public Vector3f half(float float1, float float2, float float3, Vector3f vector3f) {
			return this.delegate.half(float1, float2, float3, vector3f);
		}

		public Vector3f smoothStep(Vector3fc vector3fc, float float1, Vector3f vector3f) {
			return this.delegate.smoothStep(vector3fc, float1, vector3f);
		}

		public Vector3f hermite(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, float float1, Vector3f vector3f) {
			return this.delegate.hermite(vector3fc, vector3fc2, vector3fc3, float1, vector3f);
		}

		public Vector3f lerp(Vector3fc vector3fc, float float1, Vector3f vector3f) {
			return this.delegate.lerp(vector3fc, float1, vector3f);
		}

		public float get(int int1) throws IllegalArgumentException {
			return this.delegate.get(int1);
		}

		public int maxComponent() {
			return this.delegate.maxComponent();
		}

		public int minComponent() {
			return this.delegate.minComponent();
		}

		public Vector3f orthogonalize(Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.orthogonalize(vector3fc, vector3f);
		}

		public Vector3f orthogonalizeUnit(Vector3fc vector3fc, Vector3f vector3f) {
			return this.delegate.orthogonalizeUnit(vector3fc, vector3f);
		}
	}
}
