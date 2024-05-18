package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Vector4f implements Externalizable,Vector4fc {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;
	public float z;
	public float w;

	public Vector4f() {
		this.w = 1.0F;
	}

	public Vector4f(Vector4fc vector4fc) {
		if (vector4fc instanceof Vector4f) {
			MemUtil.INSTANCE.copy((Vector4f)vector4fc, this);
		} else {
			this.x = vector4fc.x();
			this.y = vector4fc.y();
			this.z = vector4fc.z();
			this.w = vector4fc.w();
		}
	}

	public Vector4f(Vector3fc vector3fc, float float1) {
		this.x = vector3fc.x();
		this.y = vector3fc.y();
		this.z = vector3fc.z();
		this.w = float1;
	}

	public Vector4f(Vector2fc vector2fc, float float1, float float2) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
		this.z = float1;
		this.w = float2;
	}

	public Vector4f(float float1) {
		MemUtil.INSTANCE.broadcast(float1, this);
	}

	public Vector4f(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
	}

	public Vector4f(ByteBuffer byteBuffer) {
		this(byteBuffer.position(), byteBuffer);
	}

	public Vector4f(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector4f(FloatBuffer floatBuffer) {
		this(floatBuffer.position(), floatBuffer);
	}

	public Vector4f(int int1, FloatBuffer floatBuffer) {
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

	public float w() {
		return this.w;
	}

	public Vector4f set(Vector4fc vector4fc) {
		if (vector4fc instanceof Vector4f) {
			MemUtil.INSTANCE.copy((Vector4f)vector4fc, this);
		} else {
			this.x = vector4fc.x();
			this.y = vector4fc.y();
			this.z = vector4fc.z();
			this.w = vector4fc.w();
		}

		return this;
	}

	public Vector4f set(Vector4dc vector4dc) {
		this.x = (float)vector4dc.x();
		this.y = (float)vector4dc.y();
		this.z = (float)vector4dc.z();
		this.w = (float)vector4dc.w();
		return this;
	}

	public Vector4f set(Vector3fc vector3fc, float float1) {
		this.x = vector3fc.x();
		this.y = vector3fc.y();
		this.z = vector3fc.z();
		this.w = float1;
		return this;
	}

	public Vector4f set(Vector2fc vector2fc, float float1, float float2) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
		this.z = float1;
		this.w = float2;
		return this;
	}

	public Vector4f set(float float1) {
		MemUtil.INSTANCE.broadcast(float1, this);
		return this;
	}

	public Vector4f set(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
		return this;
	}

	public Vector4f set(ByteBuffer byteBuffer) {
		return this.set(byteBuffer.position(), byteBuffer);
	}

	public Vector4f set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector4f set(FloatBuffer floatBuffer) {
		return this.set(floatBuffer.position(), floatBuffer);
	}

	public Vector4f set(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, int1, floatBuffer);
		return this;
	}

	public Vector4f setComponent(int int1, float float1) throws IllegalArgumentException {
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
		
		case 3: 
			this.w = float1;
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

	public Vector4f sub(Vector4fc vector4fc) {
		this.x -= vector4fc.x();
		this.y -= vector4fc.y();
		this.z -= vector4fc.z();
		this.w -= vector4fc.w();
		return this;
	}

	public Vector4f sub(float float1, float float2, float float3, float float4) {
		this.x -= float1;
		this.y -= float2;
		this.z -= float3;
		this.w -= float4;
		return this;
	}

	public Vector4f sub(Vector4fc vector4fc, Vector4f vector4f) {
		vector4f.x = this.x - vector4fc.x();
		vector4f.y = this.y - vector4fc.y();
		vector4f.z = this.z - vector4fc.z();
		vector4f.w = this.w - vector4fc.w();
		return vector4f;
	}

	public Vector4f sub(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		vector4f.x = this.x - float1;
		vector4f.y = this.y - float2;
		vector4f.z = this.z - float3;
		vector4f.w = this.w - float4;
		return vector4f;
	}

	public Vector4f add(Vector4fc vector4fc) {
		this.x += vector4fc.x();
		this.y += vector4fc.y();
		this.z += vector4fc.z();
		this.w += vector4fc.w();
		return this;
	}

	public Vector4f add(Vector4fc vector4fc, Vector4f vector4f) {
		vector4f.x = this.x + vector4fc.x();
		vector4f.y = this.y + vector4fc.y();
		vector4f.z = this.z + vector4fc.z();
		vector4f.w = this.w + vector4fc.w();
		return vector4f;
	}

	public Vector4f add(float float1, float float2, float float3, float float4) {
		this.x += float1;
		this.y += float2;
		this.z += float3;
		this.w += float4;
		return this;
	}

	public Vector4f add(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		vector4f.x = this.x + float1;
		vector4f.y = this.y + float2;
		vector4f.z = this.z + float3;
		vector4f.w = this.w + float4;
		return vector4f;
	}

	public Vector4f fma(Vector4fc vector4fc, Vector4fc vector4fc2) {
		this.x += vector4fc.x() * vector4fc2.x();
		this.y += vector4fc.y() * vector4fc2.y();
		this.z += vector4fc.z() * vector4fc2.z();
		this.w += vector4fc.w() * vector4fc2.w();
		return this;
	}

	public Vector4f fma(float float1, Vector4fc vector4fc) {
		this.x += float1 * vector4fc.x();
		this.y += float1 * vector4fc.y();
		this.z += float1 * vector4fc.z();
		this.w += float1 * vector4fc.w();
		return this;
	}

	public Vector4f fma(Vector4fc vector4fc, Vector4fc vector4fc2, Vector4f vector4f) {
		vector4f.x = this.x + vector4fc.x() * vector4fc2.x();
		vector4f.y = this.y + vector4fc.y() * vector4fc2.y();
		vector4f.z = this.z + vector4fc.z() * vector4fc2.z();
		vector4f.w = this.w + vector4fc.w() * vector4fc2.w();
		return vector4f;
	}

	public Vector4f fma(float float1, Vector4fc vector4fc, Vector4f vector4f) {
		vector4f.x = this.x + float1 * vector4fc.x();
		vector4f.y = this.y + float1 * vector4fc.y();
		vector4f.z = this.z + float1 * vector4fc.z();
		vector4f.w = this.w + float1 * vector4fc.w();
		return vector4f;
	}

	public Vector4f mul(Vector4fc vector4fc) {
		this.x *= vector4fc.x();
		this.y *= vector4fc.y();
		this.z *= vector4fc.z();
		this.w *= vector4fc.w();
		return this;
	}

	public Vector4f mul(Vector4fc vector4fc, Vector4f vector4f) {
		vector4f.x = this.x * vector4fc.x();
		vector4f.y = this.y * vector4fc.y();
		vector4f.z = this.z * vector4fc.z();
		vector4f.w = this.w * vector4fc.w();
		return vector4f;
	}

	public Vector4f div(Vector4fc vector4fc) {
		this.x /= vector4fc.x();
		this.y /= vector4fc.y();
		this.z /= vector4fc.z();
		this.w /= vector4fc.w();
		return this;
	}

	public Vector4f div(Vector4fc vector4fc, Vector4f vector4f) {
		vector4f.x = this.x / vector4fc.x();
		vector4f.y = this.y / vector4fc.y();
		vector4f.z = this.z / vector4fc.z();
		vector4f.w = this.w / vector4fc.w();
		return vector4f;
	}

	public Vector4f mul(Matrix4fc matrix4fc) {
		return this.mul(matrix4fc, this);
	}

	public Vector4f mul(Matrix4fc matrix4fc, Vector4f vector4f) {
		vector4f.set(matrix4fc.m00() * this.x + matrix4fc.m10() * this.y + matrix4fc.m20() * this.z + matrix4fc.m30() * this.w, matrix4fc.m01() * this.x + matrix4fc.m11() * this.y + matrix4fc.m21() * this.z + matrix4fc.m31() * this.w, matrix4fc.m02() * this.x + matrix4fc.m12() * this.y + matrix4fc.m22() * this.z + matrix4fc.m32() * this.w, matrix4fc.m03() * this.x + matrix4fc.m13() * this.y + matrix4fc.m23() * this.z + matrix4fc.m33() * this.w);
		return vector4f;
	}

	public Vector4f mul(Matrix4x3fc matrix4x3fc) {
		return this.mul(matrix4x3fc, this);
	}

	public Vector4f mul(Matrix4x3fc matrix4x3fc, Vector4f vector4f) {
		vector4f.set(matrix4x3fc.m00() * this.x + matrix4x3fc.m10() * this.y + matrix4x3fc.m20() * this.z + matrix4x3fc.m30() * this.w, matrix4x3fc.m01() * this.x + matrix4x3fc.m11() * this.y + matrix4x3fc.m21() * this.z + matrix4x3fc.m31() * this.w, matrix4x3fc.m02() * this.x + matrix4x3fc.m12() * this.y + matrix4x3fc.m22() * this.z + matrix4x3fc.m32() * this.w, this.w);
		return vector4f;
	}

	public Vector4f mulProject(Matrix4fc matrix4fc, Vector4f vector4f) {
		float float1 = 1.0F / (matrix4fc.m03() * this.x + matrix4fc.m13() * this.y + matrix4fc.m23() * this.z + matrix4fc.m33() * this.w);
		vector4f.set((matrix4fc.m00() * this.x + matrix4fc.m10() * this.y + matrix4fc.m20() * this.z + matrix4fc.m30() * this.w) * float1, (matrix4fc.m01() * this.x + matrix4fc.m11() * this.y + matrix4fc.m21() * this.z + matrix4fc.m31() * this.w) * float1, (matrix4fc.m02() * this.x + matrix4fc.m12() * this.y + matrix4fc.m22() * this.z + matrix4fc.m32() * this.w) * float1, 1.0F);
		return vector4f;
	}

	public Vector4f mulProject(Matrix4fc matrix4fc) {
		return this.mulProject(matrix4fc, this);
	}

	public Vector4f mul(float float1) {
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		this.w *= float1;
		return this;
	}

	public Vector4f mul(float float1, Vector4f vector4f) {
		vector4f.x = this.x * float1;
		vector4f.y = this.y * float1;
		vector4f.z = this.z * float1;
		vector4f.w = this.w * float1;
		return vector4f;
	}

	public Vector4f mul(float float1, float float2, float float3, float float4) {
		this.x *= float1;
		this.y *= float2;
		this.z *= float3;
		this.w *= float4;
		return this;
	}

	public Vector4f mul(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		vector4f.x = this.x * float1;
		vector4f.y = this.y * float2;
		vector4f.z = this.z * float3;
		vector4f.w = this.w * float4;
		return vector4f;
	}

	public Vector4f div(float float1) {
		this.x /= float1;
		this.y /= float1;
		this.z /= float1;
		this.w /= float1;
		return this;
	}

	public Vector4f div(float float1, Vector4f vector4f) {
		vector4f.x = this.x / float1;
		vector4f.y = this.y / float1;
		vector4f.z = this.z / float1;
		vector4f.w = this.w / float1;
		return vector4f;
	}

	public Vector4f div(float float1, float float2, float float3, float float4) {
		this.x /= float1;
		this.y /= float2;
		this.z /= float3;
		this.w /= float4;
		return this;
	}

	public Vector4f div(float float1, float float2, float float3, float float4, Vector4f vector4f) {
		vector4f.x = this.x / float1;
		vector4f.y = this.y / float2;
		vector4f.z = this.z / float3;
		vector4f.w = this.w / float4;
		return vector4f;
	}

	public Vector4f rotate(Quaternionfc quaternionfc) {
		return this.rotate(quaternionfc, this);
	}

	public Vector4f rotate(Quaternionfc quaternionfc, Vector4f vector4f) {
		return quaternionfc.transform((Vector4fc)this, (Vector4f)vector4f);
	}

	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}

	public float length() {
		return (float)Math.sqrt((double)this.lengthSquared());
	}

	public Vector4f normalize() {
		float float1 = 1.0F / this.length();
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		this.w *= float1;
		return this;
	}

	public Vector4f normalize(Vector4f vector4f) {
		float float1 = 1.0F / this.length();
		vector4f.x = this.x * float1;
		vector4f.y = this.y * float1;
		vector4f.z = this.z * float1;
		vector4f.w = this.w * float1;
		return vector4f;
	}

	public Vector4f normalize3() {
		float float1 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z)));
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		this.w *= float1;
		return this;
	}

	public float distance(Vector4fc vector4fc) {
		float float1 = vector4fc.x() - this.x;
		float float2 = vector4fc.y() - this.y;
		float float3 = vector4fc.z() - this.z;
		float float4 = vector4fc.w() - this.w;
		return (float)Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3 + float4 * float4));
	}

	public float distance(float float1, float float2, float float3, float float4) {
		float float5 = this.x - float1;
		float float6 = this.y - float2;
		float float7 = this.z - float3;
		float float8 = this.w - float4;
		return (float)Math.sqrt((double)(float5 * float5 + float6 * float6 + float7 * float7 + float8 * float8));
	}

	public float dot(Vector4fc vector4fc) {
		return this.x * vector4fc.x() + this.y * vector4fc.y() + this.z * vector4fc.z() + this.w * vector4fc.w();
	}

	public float dot(float float1, float float2, float float3, float float4) {
		return this.x * float1 + this.y * float2 + this.z * float3 + this.w * float4;
	}

	public float angleCos(Vector4fc vector4fc) {
		double double1 = (double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
		double double2 = (double)(vector4fc.x() * vector4fc.x() + vector4fc.y() * vector4fc.y() + vector4fc.z() * vector4fc.z() + vector4fc.w() * vector4fc.w());
		double double3 = (double)(this.x * vector4fc.x() + this.y * vector4fc.y() + this.z * vector4fc.z() + this.w * vector4fc.w());
		return (float)(double3 / Math.sqrt(double1 * double2));
	}

	public float angle(Vector4fc vector4fc) {
		float float1 = this.angleCos(vector4fc);
		float1 = float1 < 1.0F ? float1 : 1.0F;
		float1 = float1 > -1.0F ? float1 : -1.0F;
		return (float)Math.acos((double)float1);
	}

	public Vector4f zero() {
		MemUtil.INSTANCE.zero(this);
		return this;
	}

	public Vector4f negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
		return this;
	}

	public Vector4f negate(Vector4f vector4f) {
		vector4f.x = -this.x;
		vector4f.y = -this.y;
		vector4f.z = -this.z;
		vector4f.w = -this.w;
		return vector4f;
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format((double)this.x) + " " + numberFormat.format((double)this.y) + " " + numberFormat.format((double)this.z) + " " + numberFormat.format((double)this.w) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeFloat(this.x);
		objectOutput.writeFloat(this.y);
		objectOutput.writeFloat(this.z);
		objectOutput.writeFloat(this.w);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readFloat();
		this.y = objectInput.readFloat();
		this.z = objectInput.readFloat();
		this.w = objectInput.readFloat();
	}

	public Vector4f min(Vector4fc vector4fc) {
		this.x = this.x < vector4fc.x() ? this.x : vector4fc.x();
		this.y = this.y < vector4fc.y() ? this.y : vector4fc.y();
		this.z = this.z < vector4fc.z() ? this.z : vector4fc.z();
		this.w = this.w < vector4fc.w() ? this.w : vector4fc.w();
		return this;
	}

	public Vector4f max(Vector4fc vector4fc) {
		this.x = this.x > vector4fc.x() ? this.x : vector4fc.x();
		this.y = this.y > vector4fc.y() ? this.y : vector4fc.y();
		this.z = this.z > vector4fc.z() ? this.z : vector4fc.z();
		this.w = this.w > vector4fc.w() ? this.w : vector4fc.w();
		return this;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + Float.floatToIntBits(this.w);
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
			Vector4f vector4f = (Vector4f)object;
			if (Float.floatToIntBits(this.w) != Float.floatToIntBits(vector4f.w)) {
				return false;
			} else if (Float.floatToIntBits(this.x) != Float.floatToIntBits(vector4f.x)) {
				return false;
			} else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(vector4f.y)) {
				return false;
			} else {
				return Float.floatToIntBits(this.z) == Float.floatToIntBits(vector4f.z);
			}
		}
	}

	public Vector4f smoothStep(Vector4fc vector4fc, float float1, Vector4f vector4f) {
		float float2 = float1 * float1;
		float float3 = float2 * float1;
		vector4f.x = (this.x + this.x - vector4fc.x() - vector4fc.x()) * float3 + (3.0F * vector4fc.x() - 3.0F * this.x) * float2 + this.x * float1 + this.x;
		vector4f.y = (this.y + this.y - vector4fc.y() - vector4fc.y()) * float3 + (3.0F * vector4fc.y() - 3.0F * this.y) * float2 + this.y * float1 + this.y;
		vector4f.z = (this.z + this.z - vector4fc.z() - vector4fc.z()) * float3 + (3.0F * vector4fc.z() - 3.0F * this.z) * float2 + this.z * float1 + this.z;
		vector4f.w = (this.w + this.w - vector4fc.w() - vector4fc.w()) * float3 + (3.0F * vector4fc.w() - 3.0F * this.w) * float2 + this.w * float1 + this.w;
		return vector4f;
	}

	public Vector4f hermite(Vector4fc vector4fc, Vector4fc vector4fc2, Vector4fc vector4fc3, float float1, Vector4f vector4f) {
		float float2 = float1 * float1;
		float float3 = float2 * float1;
		vector4f.x = (this.x + this.x - vector4fc2.x() - vector4fc2.x() + vector4fc3.x() + vector4fc.x()) * float3 + (3.0F * vector4fc2.x() - 3.0F * this.x - vector4fc.x() - vector4fc.x() - vector4fc3.x()) * float2 + this.x * float1 + this.x;
		vector4f.y = (this.y + this.y - vector4fc2.y() - vector4fc2.y() + vector4fc3.y() + vector4fc.y()) * float3 + (3.0F * vector4fc2.y() - 3.0F * this.y - vector4fc.y() - vector4fc.y() - vector4fc3.y()) * float2 + this.y * float1 + this.y;
		vector4f.z = (this.z + this.z - vector4fc2.z() - vector4fc2.z() + vector4fc3.z() + vector4fc.z()) * float3 + (3.0F * vector4fc2.z() - 3.0F * this.z - vector4fc.z() - vector4fc.z() - vector4fc3.z()) * float2 + this.z * float1 + this.z;
		vector4f.w = (this.w + this.w - vector4fc2.w() - vector4fc2.w() + vector4fc3.w() + vector4fc.w()) * float3 + (3.0F * vector4fc2.w() - 3.0F * this.w - vector4fc.w() - vector4fc.w() - vector4fc3.w()) * float2 + this.w * float1 + this.w;
		return vector4f;
	}

	public Vector4f lerp(Vector4fc vector4fc, float float1) {
		return this.lerp(vector4fc, float1, this);
	}

	public Vector4f lerp(Vector4fc vector4fc, float float1, Vector4f vector4f) {
		vector4f.x = this.x + (vector4fc.x() - this.x) * float1;
		vector4f.y = this.y + (vector4fc.y() - this.y) * float1;
		vector4f.z = this.z + (vector4fc.z() - this.z) * float1;
		vector4f.w = this.w + (vector4fc.w() - this.w) * float1;
		return vector4f;
	}

	public Vector4fc toImmutable() {
		return (Vector4fc)(!Options.DEBUG ? this : new Vector4f.Proxy(this));
	}

	private final class Proxy implements Vector4fc {
		private final Vector4fc delegate;

		Proxy(Vector4fc vector4fc) {
			this.delegate = vector4fc;
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

		public float w() {
			return this.delegate.w();
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

		public Vector4f sub(Vector4fc vector4fc, Vector4f vector4f) {
			return this.delegate.sub(vector4fc, vector4f);
		}

		public Vector4f sub(float float1, float float2, float float3, float float4, Vector4f vector4f) {
			return this.delegate.sub(float1, float2, float3, float4, vector4f);
		}

		public Vector4f add(Vector4fc vector4fc, Vector4f vector4f) {
			return this.delegate.add(vector4fc, vector4f);
		}

		public Vector4f add(float float1, float float2, float float3, float float4, Vector4f vector4f) {
			return this.delegate.add(float1, float2, float3, float4, vector4f);
		}

		public Vector4f fma(Vector4fc vector4fc, Vector4fc vector4fc2, Vector4f vector4f) {
			return this.delegate.fma(vector4fc, vector4fc2, vector4f);
		}

		public Vector4f fma(float float1, Vector4fc vector4fc, Vector4f vector4f) {
			return this.delegate.fma(float1, vector4fc, vector4f);
		}

		public Vector4f mul(Vector4fc vector4fc, Vector4f vector4f) {
			return this.delegate.mul(vector4fc, vector4f);
		}

		public Vector4f div(Vector4fc vector4fc, Vector4f vector4f) {
			return this.delegate.div(vector4fc, vector4f);
		}

		public Vector4f mul(Matrix4fc matrix4fc, Vector4f vector4f) {
			return this.delegate.mul(matrix4fc, vector4f);
		}

		public Vector4f mul(Matrix4x3fc matrix4x3fc, Vector4f vector4f) {
			return this.delegate.mul(matrix4x3fc, vector4f);
		}

		public Vector4f mulProject(Matrix4fc matrix4fc, Vector4f vector4f) {
			return this.delegate.mulProject(matrix4fc, vector4f);
		}

		public Vector4f mul(float float1, Vector4f vector4f) {
			return this.delegate.mul(float1, vector4f);
		}

		public Vector4f mul(float float1, float float2, float float3, float float4, Vector4f vector4f) {
			return this.delegate.mul(float1, float2, float3, float4, vector4f);
		}

		public Vector4f div(float float1, Vector4f vector4f) {
			return this.delegate.div(float1, vector4f);
		}

		public Vector4f div(float float1, float float2, float float3, float float4, Vector4f vector4f) {
			return this.delegate.div(float1, float2, float3, float4, vector4f);
		}

		public Vector4f rotate(Quaternionfc quaternionfc, Vector4f vector4f) {
			return this.delegate.rotate(quaternionfc, vector4f);
		}

		public float lengthSquared() {
			return this.delegate.lengthSquared();
		}

		public float length() {
			return this.delegate.length();
		}

		public Vector4f normalize(Vector4f vector4f) {
			return this.delegate.normalize(vector4f);
		}

		public float distance(Vector4fc vector4fc) {
			return this.delegate.distance(vector4fc);
		}

		public float distance(float float1, float float2, float float3, float float4) {
			return this.delegate.distance(float1, float2, float3, float4);
		}

		public float dot(Vector4fc vector4fc) {
			return this.delegate.dot(vector4fc);
		}

		public float dot(float float1, float float2, float float3, float float4) {
			return this.delegate.dot(float1, float2, float3, float4);
		}

		public float angleCos(Vector4fc vector4fc) {
			return this.delegate.angleCos(vector4fc);
		}

		public float angle(Vector4fc vector4fc) {
			return this.delegate.angle(vector4fc);
		}

		public Vector4f negate(Vector4f vector4f) {
			return this.delegate.negate(vector4f);
		}

		public Vector4f lerp(Vector4fc vector4fc, float float1, Vector4f vector4f) {
			return this.delegate.lerp(vector4fc, float1, vector4f);
		}

		public Vector4f smoothStep(Vector4fc vector4fc, float float1, Vector4f vector4f) {
			return this.delegate.smoothStep(vector4fc, float1, vector4f);
		}

		public Vector4f hermite(Vector4fc vector4fc, Vector4fc vector4fc2, Vector4fc vector4fc3, float float1, Vector4f vector4f) {
			return this.delegate.hermite(vector4fc, vector4fc2, vector4fc3, float1, vector4f);
		}
	}
}
