package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Vector2f implements Externalizable,Vector2fc {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;

	public Vector2f() {
	}

	public Vector2f(float float1) {
		this(float1, float1);
	}

	public Vector2f(float float1, float float2) {
		this.x = float1;
		this.y = float2;
	}

	public Vector2f(Vector2fc vector2fc) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
	}

	public Vector2f(ByteBuffer byteBuffer) {
		this(byteBuffer.position(), byteBuffer);
	}

	public Vector2f(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector2f(FloatBuffer floatBuffer) {
		this(floatBuffer.position(), floatBuffer);
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
		return this.set(float1, float1);
	}

	public Vector2f set(float float1, float float2) {
		this.x = float1;
		this.y = float2;
		return this;
	}

	public Vector2f set(Vector2fc vector2fc) {
		this.x = vector2fc.x();
		this.y = vector2fc.y();
		return this;
	}

	public Vector2f set(Vector2dc vector2dc) {
		this.x = (float)vector2dc.x();
		this.y = (float)vector2dc.y();
		return this;
	}

	public Vector2f set(ByteBuffer byteBuffer) {
		return this.set(byteBuffer.position(), byteBuffer);
	}

	public Vector2f set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector2f set(FloatBuffer floatBuffer) {
		return this.set(floatBuffer.position(), floatBuffer);
	}

	public Vector2f set(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, int1, floatBuffer);
		return this;
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
		return this.get(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer get(FloatBuffer floatBuffer) {
		return this.get(floatBuffer.position(), floatBuffer);
	}

	public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, int1, floatBuffer);
		return floatBuffer;
	}

	public Vector2f perpendicular() {
		return this.set(this.y, this.x * -1.0F);
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
		return (float)Math.atan2((double)float2, (double)float1);
	}

	public float length() {
		return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y));
	}

	public float lengthSquared() {
		return this.x * this.x + this.y * this.y;
	}

	public float distance(Vector2fc vector2fc) {
		return this.distance(vector2fc.x(), vector2fc.y());
	}

	public float distanceSquared(Vector2fc vector2fc) {
		return this.distanceSquared(vector2fc.x(), vector2fc.y());
	}

	public float distance(float float1, float float2) {
		float float3 = this.x - float1;
		float float4 = this.y - float2;
		return (float)Math.sqrt((double)(float3 * float3 + float4 * float4));
	}

	public float distanceSquared(float float1, float float2) {
		float float3 = this.x - float1;
		float float4 = this.y - float2;
		return float3 * float3 + float4 * float4;
	}

	public Vector2f normalize() {
		float float1 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y)));
		this.x *= float1;
		this.y *= float1;
		return this;
	}

	public Vector2f normalize(Vector2f vector2f) {
		float float1 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y)));
		vector2f.x = this.x * float1;
		vector2f.y = this.y * float1;
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
		this.x += float1;
		this.y += float2;
		return this;
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

	public Vector2f lerp(Vector2fc vector2fc, float float1) {
		return this.lerp(vector2fc, float1, this);
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

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format((double)this.x) + " " + numberFormat.format((double)this.y) + ")";
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

	public Vector2fc toImmutable() {
		return (Vector2fc)(!Options.DEBUG ? this : new Vector2f.Proxy(this));
	}

	private final class Proxy implements Vector2fc {
		private final Vector2fc delegate;

		Proxy(Vector2fc vector2fc) {
			this.delegate = vector2fc;
		}

		public float x() {
			return this.delegate.x();
		}

		public float y() {
			return this.delegate.y();
		}

		public ByteBuffer get(ByteBuffer byteBuffer) {
			return this.delegate.get(byteBuffer);
		}

		public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
			return this.delegate.get(int1, byteBuffer);
		}

		public FloatBuffer get(FloatBuffer floatBuffer) {
			return this.delegate.get(floatBuffer);
		}

		public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
			return this.delegate.get(int1, floatBuffer);
		}

		public Vector2f sub(Vector2fc vector2fc, Vector2f vector2f) {
			return this.delegate.sub(vector2fc, vector2f);
		}

		public Vector2f sub(float float1, float float2, Vector2f vector2f) {
			return this.delegate.sub(float1, float2, vector2f);
		}

		public float dot(Vector2fc vector2fc) {
			return this.delegate.dot(vector2fc);
		}

		public float angle(Vector2fc vector2fc) {
			return this.delegate.angle(vector2fc);
		}

		public float length() {
			return this.delegate.length();
		}

		public float lengthSquared() {
			return this.delegate.lengthSquared();
		}

		public float distance(Vector2fc vector2fc) {
			return this.delegate.distance(vector2fc);
		}

		public float distanceSquared(Vector2fc vector2fc) {
			return this.delegate.distanceSquared(vector2fc);
		}

		public float distance(float float1, float float2) {
			return this.delegate.distance(float1, float2);
		}

		public float distanceSquared(float float1, float float2) {
			return this.delegate.distanceSquared(float1, float2);
		}

		public Vector2f normalize(Vector2f vector2f) {
			return this.delegate.normalize(vector2f);
		}

		public Vector2f add(Vector2fc vector2fc, Vector2f vector2f) {
			return this.delegate.add(vector2fc, vector2f);
		}

		public Vector2f add(float float1, float float2, Vector2f vector2f) {
			return this.delegate.add(float1, float2, vector2f);
		}

		public Vector2f negate(Vector2f vector2f) {
			return this.delegate.negate(vector2f);
		}

		public Vector2f mul(float float1, Vector2f vector2f) {
			return this.delegate.mul(float1, vector2f);
		}

		public Vector2f mul(float float1, float float2, Vector2f vector2f) {
			return this.delegate.mul(float1, float2, vector2f);
		}

		public Vector2f mul(Vector2fc vector2fc, Vector2f vector2f) {
			return this.delegate.mul(vector2fc, vector2f);
		}

		public Vector2f lerp(Vector2fc vector2fc, float float1, Vector2f vector2f) {
			return this.delegate.lerp(vector2fc, float1, vector2f);
		}

		public Vector2f fma(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2f vector2f) {
			return this.delegate.fma(vector2fc, vector2fc2, vector2f);
		}

		public Vector2f fma(float float1, Vector2fc vector2fc, Vector2f vector2f) {
			return this.delegate.fma(float1, vector2fc, vector2f);
		}
	}
}
