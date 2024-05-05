package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;


public class Vector2f implements Externalizable,Vector2fc {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;

	public Vector2f() {
	}

	public Vector2f(float float1) {
		this.x = float1;
		this.y = float1;
	}

	public Vector2f(float float1, float float2) {
		this.x = float1;
		this.y = float2;
	}

	public Vector2f(Vector2fc vector2fc) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
	}

	public Vector2f(Vector2ic vector2ic) {
		this.x = (float)vector2ic.x();
		this.y = (float)vector2ic.y();
	}

	public Vector2f(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
	}

	public Vector2f(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
	}

	public Vector2f(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector2f(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
	}

	public Vector2f(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, int1, floatBuffer);
	}

	public float x() {
		return this.x;
	}

	public float y() {
		return this.y;
	}

	public Vector2f set(float float1) {
		this.x = float1;
		this.y = float1;
		return this;
	}

	public Vector2f set(float float1, float float2) {
		this.x = float1;
		this.y = float2;
		return this;
	}

	public Vector2f set(double double1) {
		this.x = (float)double1;
		this.y = (float)double1;
		return this;
	}

	public Vector2f set(double double1, double double2) {
		this.x = (float)double1;
		this.y = (float)double2;
		return this;
	}

	public Vector2f set(Vector2fc vector2fc) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
		return this;
	}

	public Vector2f set(Vector2ic vector2ic) {
		this.x = (float)vector2ic.x();
		this.y = (float)vector2ic.y();
		return this;
	}

	public Vector2f set(Vector2dc vector2dc) {
		this.x = (float)vector2dc.x();
		this.y = (float)vector2dc.y();
		return this;
	}

	public Vector2f set(float[] floatArray) {
		this.x = floatArray[0];
		this.y = floatArray[1];
		return this;
	}

	public Vector2f set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Vector2f set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector2f set(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
		return this;
	}

	public Vector2f set(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, int1, floatBuffer);
		return this;
	}

	public Vector2f setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
	}

	public float get(int int1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			return this.x;
		
		case 1: 
			return this.y;
		
		default: 
			throw new IllegalArgumentException();
		
		}
	}

	public Vector2i get(int int1, Vector2i vector2i) {
		vector2i.x = Math.roundUsing(this.x(), int1);
		vector2i.y = Math.roundUsing(this.y(), int1);
		return vector2i;
	}

	public Vector2f get(Vector2f vector2f) {
		vector2f.x = this.x();
		vector2f.y = this.y();
		return vector2f;
	}

	public Vector2d get(Vector2d vector2d) {
		vector2d.x = (double)this.x();
		vector2d.y = (double)this.y();
		return vector2d;
	}

	public Vector2f setComponent(int int1, float float1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			this.x = float1;
			break;
		
		case 1: 
			this.y = float1;
			break;
		
		default: 
			throw new IllegalArgumentException();
		
		}
		return this;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer get(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, floatBuffer.position(), floatBuffer);
		return floatBuffer;
	}

	public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, int1, floatBuffer);
		return floatBuffer;
	}

	public Vector2fc getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public Vector2f perpendicular() {
		float float1 = this.y;
		this.y = this.x * -1.0F;
		this.x = float1;
		return this;
	}

	public Vector2f sub(Vector2fc vector2fc) {
		this.x -= vector2fc.x();
		this.y -= vector2fc.y();
		return this;
	}

	public Vector2f sub(Vector2fc vector2fc, Vector2f vector2f) {
		vector2f.x = this.x - vector2fc.x();
		vector2f.y = this.y - vector2fc.y();
		return vector2f;
	}

	public Vector2f sub(float float1, float float2) {
		this.x -= float1;
		this.y -= float2;
		return this;
	}

	public Vector2f sub(float float1, float float2, Vector2f vector2f) {
		vector2f.x = this.x - float1;
		vector2f.y = this.y - float2;
		return vector2f;
	}

	public float dot(Vector2fc vector2fc) {
		return this.x * vector2fc.x() + this.y * vector2fc.y();
	}

	public float angle(Vector2fc vector2fc) {
		float float1 = this.x * vector2fc.x() + this.y * vector2fc.y();
		float float2 = this.x * vector2fc.y() - this.y * vector2fc.x();
		return Math.atan2(float2, float1);
	}

	public float lengthSquared() {
		return this.x * this.x + this.y * this.y;
	}

	public static float lengthSquared(float float1, float float2) {
		return float1 * float1 + float2 * float2;
	}

	public float length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public static float length(float float1, float float2) {
		return Math.sqrt(float1 * float1 + float2 * float2);
	}

	public float distance(Vector2fc vector2fc) {
		float float1 = this.x - vector2fc.x();
		float float2 = this.y - vector2fc.y();
		return Math.sqrt(float1 * float1 + float2 * float2);
	}

	public float distanceSquared(Vector2fc vector2fc) {
		float float1 = this.x - vector2fc.x();
		float float2 = this.y - vector2fc.y();
		return float1 * float1 + float2 * float2;
	}

	public float distance(float float1, float float2) {
		float float3 = this.x - float1;
		float float4 = this.y - float2;
		return Math.sqrt(float3 * float3 + float4 * float4);
	}

	public float distanceSquared(float float1, float float2) {
		float float3 = this.x - float1;
		float float4 = this.y - float2;
		return float3 * float3 + float4 * float4;
	}

	public static float distance(float float1, float float2, float float3, float float4) {
		float float5 = float1 - float3;
		float float6 = float2 - float4;
		return Math.sqrt(float5 * float5 + float6 * float6);
	}

	public static float distanceSquared(float float1, float float2, float float3, float float4) {
		float float5 = float1 - float3;
		float float6 = float2 - float4;
		return float5 * float5 + float6 * float6;
	}

	public Vector2f normalize() {
		float float1 = Math.invsqrt(this.x * this.x + this.y * this.y);
		this.x *= float1;
		this.y *= float1;
		return this;
	}

	public Vector2f normalize(Vector2f vector2f) {
		float float1 = Math.invsqrt(this.x * this.x + this.y * this.y);
		vector2f.x = this.x * float1;
		vector2f.y = this.y * float1;
		return vector2f;
	}

	public Vector2f normalize(float float1) {
		float float2 = Math.invsqrt(this.x * this.x + this.y * this.y) * float1;
		this.x *= float2;
		this.y *= float2;
		return this;
	}

	public Vector2f normalize(float float1, Vector2f vector2f) {
		float float2 = Math.invsqrt(this.x * this.x + this.y * this.y) * float1;
		vector2f.x = this.x * float2;
		vector2f.y = this.y * float2;
		return vector2f;
	}

	public Vector2f add(Vector2fc vector2fc) {
		this.x += vector2fc.x();
		this.y += vector2fc.y();
		return this;
	}

	public Vector2f add(Vector2fc vector2fc, Vector2f vector2f) {
		vector2f.x = this.x + vector2fc.x();
		vector2f.y = this.y + vector2fc.y();
		return vector2f;
	}

	public Vector2f add(float float1, float float2) {
		return this.add(float1, float2, this);
	}

	public Vector2f add(float float1, float float2, Vector2f vector2f) {
		vector2f.x = this.x + float1;
		vector2f.y = this.y + float2;
		return vector2f;
	}

	public Vector2f zero() {
		this.x = 0.0F;
		this.y = 0.0F;
		return this;
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeFloat(this.x);
		objectOutput.writeFloat(this.y);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readFloat();
		this.y = objectInput.readFloat();
	}

	public Vector2f negate() {
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}

	public Vector2f negate(Vector2f vector2f) {
		vector2f.x = -this.x;
		vector2f.y = -this.y;
		return vector2f;
	}

	public Vector2f mul(float float1) {
		this.x *= float1;
		this.y *= float1;
		return this;
	}

	public Vector2f mul(float float1, Vector2f vector2f) {
		vector2f.x = this.x * float1;
		vector2f.y = this.y * float1;
		return vector2f;
	}

	public Vector2f mul(float float1, float float2) {
		this.x *= float1;
		this.y *= float2;
		return this;
	}

	public Vector2f mul(float float1, float float2, Vector2f vector2f) {
		vector2f.x = this.x * float1;
		vector2f.y = this.y * float2;
		return vector2f;
	}

	public Vector2f mul(Vector2fc vector2fc) {
		this.x *= vector2fc.x();
		this.y *= vector2fc.y();
		return this;
	}

	public Vector2f mul(Vector2fc vector2fc, Vector2f vector2f) {
		vector2f.x = this.x * vector2fc.x();
		vector2f.y = this.y * vector2fc.y();
		return vector2f;
	}

	public Vector2f div(Vector2fc vector2fc) {
		this.x /= vector2fc.x();
		this.y /= vector2fc.y();
		return this;
	}

	public Vector2f div(Vector2fc vector2fc, Vector2f vector2f) {
		vector2f.x = this.x / vector2fc.x();
		vector2f.y = this.y / vector2fc.y();
		return vector2f;
	}

	public Vector2f div(float float1) {
		float float2 = 1.0F / float1;
		this.x *= float2;
		this.y *= float2;
		return this;
	}

	public Vector2f div(float float1, Vector2f vector2f) {
		float float2 = 1.0F / float1;
		vector2f.x = this.x * float2;
		vector2f.y = this.y * float2;
		return vector2f;
	}

	public Vector2f div(float float1, float float2) {
		this.x /= float1;
		this.y /= float2;
		return this;
	}

	public Vector2f div(float float1, float float2, Vector2f vector2f) {
		vector2f.x = this.x / float1;
		vector2f.y = this.y / float2;
		return vector2f;
	}

	public Vector2f mul(Matrix2fc matrix2fc) {
		float float1 = matrix2fc.m00() * this.x + matrix2fc.m10() * this.y;
		float float2 = matrix2fc.m01() * this.x + matrix2fc.m11() * this.y;
		this.x = float1;
		this.y = float2;
		return this;
	}

	public Vector2f mul(Matrix2fc matrix2fc, Vector2f vector2f) {
		float float1 = matrix2fc.m00() * this.x + matrix2fc.m10() * this.y;
		float float2 = matrix2fc.m01() * this.x + matrix2fc.m11() * this.y;
		vector2f.x = float1;
		vector2f.y = float2;
		return vector2f;
	}

	public Vector2f mul(Matrix2dc matrix2dc) {
		double double1 = matrix2dc.m00() * (double)this.x + matrix2dc.m10() * (double)this.y;
		double double2 = matrix2dc.m01() * (double)this.x + matrix2dc.m11() * (double)this.y;
		this.x = (float)double1;
		this.y = (float)double2;
		return this;
	}

	public Vector2f mul(Matrix2dc matrix2dc, Vector2f vector2f) {
		double double1 = matrix2dc.m00() * (double)this.x + matrix2dc.m10() * (double)this.y;
		double double2 = matrix2dc.m01() * (double)this.x + matrix2dc.m11() * (double)this.y;
		vector2f.x = (float)double1;
		vector2f.y = (float)double2;
		return vector2f;
	}

	public Vector2f mulTranspose(Matrix2fc matrix2fc) {
		float float1 = matrix2fc.m00() * this.x + matrix2fc.m01() * this.y;
		float float2 = matrix2fc.m10() * this.x + matrix2fc.m11() * this.y;
		this.x = float1;
		this.y = float2;
		return this;
	}

	public Vector2f mulTranspose(Matrix2fc matrix2fc, Vector2f vector2f) {
		float float1 = matrix2fc.m00() * this.x + matrix2fc.m01() * this.y;
		float float2 = matrix2fc.m10() * this.x + matrix2fc.m11() * this.y;
		vector2f.x = float1;
		vector2f.y = float2;
		return vector2f;
	}

	public Vector2f mulPosition(Matrix3x2fc matrix3x2fc) {
		this.x = matrix3x2fc.m00() * this.x + matrix3x2fc.m10() * this.y + matrix3x2fc.m20();
		this.y = matrix3x2fc.m01() * this.x + matrix3x2fc.m11() * this.y + matrix3x2fc.m21();
		return this;
	}

	public Vector2f mulPosition(Matrix3x2fc matrix3x2fc, Vector2f vector2f) {
		vector2f.x = matrix3x2fc.m00() * this.x + matrix3x2fc.m10() * this.y + matrix3x2fc.m20();
		vector2f.y = matrix3x2fc.m01() * this.x + matrix3x2fc.m11() * this.y + matrix3x2fc.m21();
		return vector2f;
	}

	public Vector2f mulDirection(Matrix3x2fc matrix3x2fc) {
		this.x = matrix3x2fc.m00() * this.x + matrix3x2fc.m10() * this.y;
		this.y = matrix3x2fc.m01() * this.x + matrix3x2fc.m11() * this.y;
		return this;
	}

	public Vector2f mulDirection(Matrix3x2fc matrix3x2fc, Vector2f vector2f) {
		vector2f.x = matrix3x2fc.m00() * this.x + matrix3x2fc.m10() * this.y;
		vector2f.y = matrix3x2fc.m01() * this.x + matrix3x2fc.m11() * this.y;
		return vector2f;
	}

	public Vector2f lerp(Vector2fc vector2fc, float float1) {
		this.x += (vector2fc.x() - this.x) * float1;
		this.y += (vector2fc.y() - this.y) * float1;
		return this;
	}

	public Vector2f lerp(Vector2fc vector2fc, float float1, Vector2f vector2f) {
		vector2f.x = this.x + (vector2fc.x() - this.x) * float1;
		vector2f.y = this.y + (vector2fc.y() - this.y) * float1;
		return vector2f;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + Float.floatToIntBits(this.x);
		int1 = 31 * int1 + Float.floatToIntBits(this.y);
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
			Vector2f vector2f = (Vector2f)object;
			if (Float.floatToIntBits(this.x) != Float.floatToIntBits(vector2f.x)) {
				return false;
			} else {
				return Float.floatToIntBits(this.y) == Float.floatToIntBits(vector2f.y);
			}
		}
	}

	public boolean equals(Vector2fc vector2fc, float float1) {
		if (this == vector2fc) {
			return true;
		} else if (vector2fc == null) {
			return false;
		} else if (!(vector2fc instanceof Vector2fc)) {
			return false;
		} else if (!Runtime.equals(this.x, vector2fc.x(), float1)) {
			return false;
		} else {
			return Runtime.equals(this.y, vector2fc.y(), float1);
		}
	}

	public boolean equals(float float1, float float2) {
		if (Float.floatToIntBits(this.x) != Float.floatToIntBits(float1)) {
			return false;
		} else {
			return Float.floatToIntBits(this.y) == Float.floatToIntBits(float2);
		}
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format((double)this.x, numberFormat);
		return "(" + string + " " + Runtime.format((double)this.y, numberFormat) + ")";
	}

	public Vector2f fma(Vector2fc vector2fc, Vector2fc vector2fc2) {
		this.x += vector2fc.x() * vector2fc2.x();
		this.y += vector2fc.y() * vector2fc2.y();
		return this;
	}

	public Vector2f fma(float float1, Vector2fc vector2fc) {
		this.x += float1 * vector2fc.x();
		this.y += float1 * vector2fc.y();
		return this;
	}

	public Vector2f fma(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2f vector2f) {
		vector2f.x = this.x + vector2fc.x() * vector2fc2.x();
		vector2f.y = this.y + vector2fc.y() * vector2fc2.y();
		return vector2f;
	}

	public Vector2f fma(float float1, Vector2fc vector2fc, Vector2f vector2f) {
		vector2f.x = this.x + float1 * vector2fc.x();
		vector2f.y = this.y + float1 * vector2fc.y();
		return vector2f;
	}

	public Vector2f min(Vector2fc vector2fc) {
		this.x = this.x < vector2fc.x() ? this.x : vector2fc.x();
		this.y = this.y < vector2fc.y() ? this.y : vector2fc.y();
		return this;
	}

	public Vector2f min(Vector2fc vector2fc, Vector2f vector2f) {
		vector2f.x = this.x < vector2fc.x() ? this.x : vector2fc.x();
		vector2f.y = this.y < vector2fc.y() ? this.y : vector2fc.y();
		return vector2f;
	}

	public Vector2f max(Vector2fc vector2fc) {
		this.x = this.x > vector2fc.x() ? this.x : vector2fc.x();
		this.y = this.y > vector2fc.y() ? this.y : vector2fc.y();
		return this;
	}

	public Vector2f max(Vector2fc vector2fc, Vector2f vector2f) {
		vector2f.x = this.x > vector2fc.x() ? this.x : vector2fc.x();
		vector2f.y = this.y > vector2fc.y() ? this.y : vector2fc.y();
		return vector2f;
	}

	public int maxComponent() {
		float float1 = Math.abs(this.x);
		float float2 = Math.abs(this.y);
		return float1 >= float2 ? 0 : 1;
	}

	public int minComponent() {
		float float1 = Math.abs(this.x);
		float float2 = Math.abs(this.y);
		return float1 < float2 ? 0 : 1;
	}

	public Vector2f floor() {
		this.x = Math.floor(this.x);
		this.y = Math.floor(this.y);
		return this;
	}

	public Vector2f floor(Vector2f vector2f) {
		vector2f.x = Math.floor(this.x);
		vector2f.y = Math.floor(this.y);
		return vector2f;
	}

	public Vector2f ceil() {
		this.x = Math.ceil(this.x);
		this.y = Math.ceil(this.y);
		return this;
	}

	public Vector2f ceil(Vector2f vector2f) {
		vector2f.x = Math.ceil(this.x);
		vector2f.y = Math.ceil(this.y);
		return vector2f;
	}

	public Vector2f round() {
		this.x = Math.ceil(this.x);
		this.y = Math.ceil(this.y);
		return this;
	}

	public Vector2f round(Vector2f vector2f) {
		vector2f.x = (float)Math.round(this.x);
		vector2f.y = (float)Math.round(this.y);
		return vector2f;
	}

	public boolean isFinite() {
		return Math.isFinite(this.x) && Math.isFinite(this.y);
	}

	public Vector2f absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		return this;
	}

	public Vector2f absolute(Vector2f vector2f) {
		vector2f.x = Math.abs(this.x);
		vector2f.y = Math.abs(this.y);
		return vector2f;
	}
}
