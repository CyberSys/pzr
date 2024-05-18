package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;


public class Vector4i implements Externalizable,Vector4ic {
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	public int z;
	public int w;

	public Vector4i() {
		this.w = 1;
	}

	public Vector4i(Vector4ic vector4ic) {
		if (vector4ic instanceof Vector4i) {
			MemUtil.INSTANCE.copy((Vector4i)vector4ic, this);
		} else {
			this.x = vector4ic.x();
			this.y = vector4ic.y();
			this.z = vector4ic.z();
			this.w = vector4ic.w();
		}
	}

	public Vector4i(Vector3ic vector3ic, int int1) {
		this.x = vector3ic.x();
		this.y = vector3ic.y();
		this.z = vector3ic.z();
		this.w = int1;
	}

	public Vector4i(Vector2ic vector2ic, int int1, int int2) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
		this.z = int1;
		this.w = int2;
	}

	public Vector4i(int int1) {
		MemUtil.INSTANCE.broadcast(int1, this);
	}

	public Vector4i(int int1, int int2, int int3, int int4) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.w = int4;
	}

	public Vector4i(ByteBuffer byteBuffer) {
		this(byteBuffer.position(), byteBuffer);
	}

	public Vector4i(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector4i(IntBuffer intBuffer) {
		this(intBuffer.position(), intBuffer);
	}

	public Vector4i(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, int1, intBuffer);
	}

	public int x() {
		return this.x;
	}

	public int y() {
		return this.y;
	}

	public int z() {
		return this.z;
	}

	public int w() {
		return this.w;
	}

	public Vector4i set(Vector4ic vector4ic) {
		if (vector4ic instanceof Vector4i) {
			MemUtil.INSTANCE.copy((Vector4i)vector4ic, this);
		} else {
			this.x = vector4ic.x();
			this.y = vector4ic.y();
			this.z = vector4ic.z();
			this.w = vector4ic.w();
		}

		return this;
	}

	public Vector4i set(Vector3ic vector3ic, int int1) {
		this.x = vector3ic.x();
		this.y = vector3ic.y();
		this.z = vector3ic.z();
		this.w = int1;
		return this;
	}

	public Vector4i set(Vector2ic vector2ic, int int1, int int2) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
		this.z = int1;
		this.w = int2;
		return this;
	}

	public Vector4i set(int int1) {
		MemUtil.INSTANCE.broadcast(int1, this);
		return this;
	}

	public Vector4i set(int int1, int int2, int int3, int int4) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.w = int4;
		return this;
	}

	public Vector4i set(ByteBuffer byteBuffer) {
		return this.set(byteBuffer.position(), byteBuffer);
	}

	public Vector4i set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector4i set(IntBuffer intBuffer) {
		return this.set(intBuffer.position(), intBuffer);
	}

	public Vector4i set(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, int1, intBuffer);
		return this;
	}

	public Vector4i setComponent(int int1, int int2) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			this.x = int2;
			break;
		
		case 1: 
			this.y = int2;
			break;
		
		case 2: 
			this.z = int2;
			break;
		
		case 3: 
			this.w = int2;
			break;
		
		default: 
			throw new IllegalArgumentException();
		
		}
		return this;
	}

	public IntBuffer get(IntBuffer intBuffer) {
		return this.get(intBuffer.position(), intBuffer);
	}

	public IntBuffer get(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.put(this, int1, intBuffer);
		return intBuffer;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		return this.get(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Vector4i sub(Vector4ic vector4ic) {
		this.x -= vector4ic.x();
		this.y -= vector4ic.y();
		this.z -= vector4ic.z();
		this.w -= vector4ic.w();
		return this;
	}

	public Vector4i sub(int int1, int int2, int int3, int int4) {
		this.x -= int1;
		this.y -= int2;
		this.z -= int3;
		this.w -= int4;
		return this;
	}

	public Vector4i sub(Vector4ic vector4ic, Vector4i vector4i) {
		vector4i.x = this.x - vector4ic.x();
		vector4i.y = this.y - vector4ic.y();
		vector4i.z = this.z - vector4ic.z();
		vector4i.w = this.w - vector4ic.w();
		return vector4i;
	}

	public Vector4i sub(int int1, int int2, int int3, int int4, Vector4i vector4i) {
		vector4i.x = this.x - int1;
		vector4i.y = this.y - int2;
		vector4i.z = this.z - int3;
		vector4i.w = this.w - int4;
		return vector4i;
	}

	public Vector4i add(Vector4ic vector4ic) {
		this.x += vector4ic.x();
		this.y += vector4ic.y();
		this.z += vector4ic.z();
		this.w += vector4ic.w();
		return this;
	}

	public Vector4i add(Vector4ic vector4ic, Vector4i vector4i) {
		vector4i.x = this.x + vector4ic.x();
		vector4i.y = this.y + vector4ic.y();
		vector4i.z = this.z + vector4ic.z();
		vector4i.w = this.w + vector4ic.w();
		return vector4i;
	}

	public Vector4i add(int int1, int int2, int int3, int int4) {
		this.x += int1;
		this.y += int2;
		this.z += int3;
		this.w += int4;
		return this;
	}

	public Vector4i add(int int1, int int2, int int3, int int4, Vector4i vector4i) {
		vector4i.x = this.x + int1;
		vector4i.y = this.y + int2;
		vector4i.z = this.z + int3;
		vector4i.w = this.w + int4;
		return vector4i;
	}

	public Vector4i mul(Vector4ic vector4ic) {
		this.x *= vector4ic.x();
		this.y *= vector4ic.y();
		this.z *= vector4ic.z();
		this.w *= vector4ic.w();
		return this;
	}

	public Vector4i mul(Vector4ic vector4ic, Vector4i vector4i) {
		vector4i.x = this.x * vector4ic.x();
		vector4i.y = this.y * vector4ic.y();
		vector4i.z = this.z * vector4ic.z();
		vector4i.w = this.w * vector4ic.w();
		return vector4i;
	}

	public Vector4i div(Vector4ic vector4ic) {
		this.x /= vector4ic.x();
		this.y /= vector4ic.y();
		this.z /= vector4ic.z();
		this.w /= vector4ic.w();
		return this;
	}

	public Vector4i div(Vector4ic vector4ic, Vector4i vector4i) {
		vector4i.x = this.x / vector4ic.x();
		vector4i.y = this.y / vector4ic.y();
		vector4i.z = this.z / vector4ic.z();
		vector4i.w = this.w / vector4ic.w();
		return vector4i;
	}

	public Vector4i mul(float float1) {
		this.x = (int)((float)this.x * float1);
		this.y = (int)((float)this.y * float1);
		this.z = (int)((float)this.z * float1);
		this.w = (int)((float)this.w * float1);
		return this;
	}

	public Vector4i mul(float float1, Vector4i vector4i) {
		vector4i.x = (int)((float)this.x * float1);
		vector4i.y = (int)((float)this.y * float1);
		vector4i.z = (int)((float)this.z * float1);
		vector4i.w = (int)((float)this.w * float1);
		return vector4i;
	}

	public Vector4i div(int int1) {
		this.x /= int1;
		this.y /= int1;
		this.z /= int1;
		this.w /= int1;
		return this;
	}

	public Vector4i div(float float1, Vector4i vector4i) {
		vector4i.x = (int)((float)this.x / float1);
		vector4i.y = (int)((float)this.y / float1);
		vector4i.z = (int)((float)this.z / float1);
		vector4i.w = (int)((float)this.w / float1);
		return vector4i;
	}

	public long lengthSquared() {
		return (long)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
	}

	public double length() {
		return Math.sqrt((double)this.lengthSquared());
	}

	public double distance(Vector4ic vector4ic) {
		return Math.sqrt((double)this.distanceSquared(vector4ic));
	}

	public double distance(int int1, int int2, int int3, int int4) {
		return Math.sqrt((double)this.distanceSquared(int1, int2, int3, int4));
	}

	public int distanceSquared(Vector4ic vector4ic) {
		int int1 = this.x - vector4ic.x();
		int int2 = this.y - vector4ic.y();
		int int3 = this.z - vector4ic.z();
		int int4 = this.w - vector4ic.w();
		return int1 * int1 + int2 * int2 + int3 * int3 + int4 * int4;
	}

	public int distanceSquared(int int1, int int2, int int3, int int4) {
		int int5 = this.x - int1;
		int int6 = this.y - int2;
		int int7 = this.z - int3;
		int int8 = this.w - int4;
		return int5 * int5 + int6 * int6 + int7 * int7 + int8 * int8;
	}

	public int dot(Vector4ic vector4ic) {
		return this.x * vector4ic.x() + this.y * vector4ic.y() + this.z * vector4ic.z() + this.w * vector4ic.w();
	}

	public Vector4i zero() {
		MemUtil.INSTANCE.zero(this);
		return this;
	}

	public Vector4i negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
		return this;
	}

	public Vector4i negate(Vector4i vector4i) {
		vector4i.x = -this.x;
		vector4i.y = -this.y;
		vector4i.z = -this.z;
		vector4i.w = -this.w;
		return vector4i;
	}

	public String toString() {
		return "(" + this.x + " " + this.y + " " + this.z + " " + this.w + ")";
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format((long)this.x) + " " + numberFormat.format((long)this.y) + " " + numberFormat.format((long)this.z) + " " + numberFormat.format((long)this.w) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeInt(this.x);
		objectOutput.writeInt(this.y);
		objectOutput.writeInt(this.z);
		objectOutput.writeInt(this.w);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readInt();
		this.y = objectInput.readInt();
		this.z = objectInput.readInt();
		this.w = objectInput.readInt();
	}

	public Vector4i min(Vector4ic vector4ic) {
		this.x = Math.min(this.x, vector4ic.x());
		this.y = Math.min(this.y, vector4ic.y());
		this.z = Math.min(this.z, vector4ic.z());
		this.w = Math.min(this.w, vector4ic.w());
		return this;
	}

	public Vector4i max(Vector4ic vector4ic) {
		this.x = Math.max(this.x, vector4ic.x());
		this.y = Math.max(this.y, vector4ic.y());
		this.z = Math.max(this.z, vector4ic.z());
		this.w = Math.min(this.w, vector4ic.w());
		return this;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + this.x;
		int1 = 31 * int1 + this.y;
		int1 = 31 * int1 + this.z;
		int1 = 31 * int1 + this.w;
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
			Vector4i vector4i = (Vector4i)object;
			if (this.x != vector4i.x) {
				return false;
			} else if (this.y != vector4i.y) {
				return false;
			} else if (this.z != vector4i.z) {
				return false;
			} else {
				return this.w == vector4i.w;
			}
		}
	}

	public Vector4ic toImmutable() {
		return (Vector4ic)(!Options.DEBUG ? this : new Vector4i.Proxy(this));
	}

	private final class Proxy implements Vector4ic {
		private final Vector4ic delegate;

		Proxy(Vector4ic vector4ic) {
			this.delegate = vector4ic;
		}

		public int x() {
			return this.delegate.x();
		}

		public int y() {
			return this.delegate.y();
		}

		public int z() {
			return this.delegate.z();
		}

		public int w() {
			return this.delegate.w();
		}

		public IntBuffer get(IntBuffer intBuffer) {
			return this.delegate.get(intBuffer);
		}

		public IntBuffer get(int int1, IntBuffer intBuffer) {
			return this.delegate.get(int1, intBuffer);
		}

		public ByteBuffer get(ByteBuffer byteBuffer) {
			return this.delegate.get(byteBuffer);
		}

		public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
			return this.delegate.get(int1, byteBuffer);
		}

		public Vector4i sub(Vector4ic vector4ic, Vector4i vector4i) {
			return this.delegate.sub(vector4ic, vector4i);
		}

		public Vector4i sub(int int1, int int2, int int3, int int4, Vector4i vector4i) {
			return this.delegate.sub(int1, int2, int3, int4, vector4i);
		}

		public Vector4i add(Vector4ic vector4ic, Vector4i vector4i) {
			return this.delegate.add(vector4ic, vector4i);
		}

		public Vector4i add(int int1, int int2, int int3, int int4, Vector4i vector4i) {
			return this.delegate.add(int1, int2, int3, int4, vector4i);
		}

		public Vector4i mul(Vector4ic vector4ic, Vector4i vector4i) {
			return this.delegate.mul(vector4ic, vector4i);
		}

		public Vector4i div(Vector4ic vector4ic, Vector4i vector4i) {
			return this.delegate.div(vector4ic, vector4i);
		}

		public Vector4i mul(float float1, Vector4i vector4i) {
			return this.delegate.mul(float1, vector4i);
		}

		public Vector4i div(float float1, Vector4i vector4i) {
			return this.delegate.div(float1, vector4i);
		}

		public long lengthSquared() {
			return this.delegate.lengthSquared();
		}

		public double length() {
			return this.delegate.length();
		}

		public double distance(Vector4ic vector4ic) {
			return this.delegate.distance(vector4ic);
		}

		public double distance(int int1, int int2, int int3, int int4) {
			return this.delegate.distance(int1, int2, int3, int4);
		}

		public int distanceSquared(Vector4ic vector4ic) {
			return this.delegate.distanceSquared(vector4ic);
		}

		public int distanceSquared(int int1, int int2, int int3, int int4) {
			return this.delegate.distanceSquared(int1, int2, int3, int4);
		}

		public int dot(Vector4ic vector4ic) {
			return this.delegate.dot(vector4ic);
		}

		public Vector4i negate(Vector4i vector4i) {
			return this.delegate.negate(vector4i);
		}
	}
}
