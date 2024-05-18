package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;


public class Vector3i implements Externalizable,Vector3ic {
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	public int z;

	public Vector3i() {
	}

	public Vector3i(int int1) {
		this(int1, int1, int1);
	}

	public Vector3i(int int1, int int2, int int3) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
	}

	public Vector3i(Vector3ic vector3ic) {
		this.x = vector3ic.x();
		this.y = vector3ic.y();
		this.z = vector3ic.z();
	}

	public Vector3i(Vector2ic vector2ic, int int1) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
		this.z = int1;
	}

	public Vector3i(ByteBuffer byteBuffer) {
		this(byteBuffer.position(), byteBuffer);
	}

	public Vector3i(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector3i(IntBuffer intBuffer) {
		this(intBuffer.position(), intBuffer);
	}

	public Vector3i(int int1, IntBuffer intBuffer) {
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

	public Vector3i set(Vector3ic vector3ic) {
		this.x = vector3ic.x();
		this.y = vector3ic.y();
		this.z = vector3ic.z();
		return this;
	}

	public Vector3i set(Vector3dc vector3dc) {
		this.x = (int)vector3dc.x();
		this.y = (int)vector3dc.y();
		this.z = (int)vector3dc.z();
		return this;
	}

	public Vector3i set(Vector2ic vector2ic, int int1) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
		this.z = int1;
		return this;
	}

	public Vector3i set(int int1) {
		return this.set(int1, int1, int1);
	}

	public Vector3i set(int int1, int int2, int int3) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		return this;
	}

	public Vector3i set(ByteBuffer byteBuffer) {
		return this.set(byteBuffer.position(), byteBuffer);
	}

	public Vector3i set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector3i set(IntBuffer intBuffer) {
		return this.set(intBuffer.position(), intBuffer);
	}

	public Vector3i set(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, int1, intBuffer);
		return this;
	}

	public Vector3i setComponent(int int1, int int2) throws IllegalArgumentException {
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

	public Vector3i sub(Vector3ic vector3ic) {
		this.x -= vector3ic.x();
		this.y -= vector3ic.y();
		this.z -= vector3ic.z();
		return this;
	}

	public Vector3i sub(Vector3ic vector3ic, Vector3i vector3i) {
		vector3i.x = this.x - vector3ic.x();
		vector3i.y = this.y - vector3ic.y();
		vector3i.z = this.z - vector3ic.z();
		return vector3i;
	}

	public Vector3i sub(int int1, int int2, int int3) {
		this.x -= int1;
		this.y -= int2;
		this.z -= int3;
		return this;
	}

	public Vector3i sub(int int1, int int2, int int3, Vector3i vector3i) {
		vector3i.x = this.x - int1;
		vector3i.y = this.y - int2;
		vector3i.z = this.z - int3;
		return vector3i;
	}

	public Vector3i add(Vector3ic vector3ic) {
		this.x += vector3ic.x();
		this.y += vector3ic.y();
		this.z += vector3ic.z();
		return this;
	}

	public Vector3i add(Vector3ic vector3ic, Vector3i vector3i) {
		vector3i.x = this.x + vector3ic.x();
		vector3i.y = this.y + vector3ic.y();
		vector3i.z = this.z + vector3ic.z();
		return vector3i;
	}

	public Vector3i add(int int1, int int2, int int3) {
		this.x += int1;
		this.y += int2;
		this.z += int3;
		return this;
	}

	public Vector3i add(int int1, int int2, int int3, Vector3i vector3i) {
		vector3i.x = this.x + int1;
		vector3i.y = this.y + int2;
		vector3i.z = this.z + int3;
		return vector3i;
	}

	public Vector3i mul(int int1) {
		this.x *= int1;
		this.y *= int1;
		this.z *= int1;
		return this;
	}

	public Vector3i mul(int int1, Vector3i vector3i) {
		vector3i.x = this.x * int1;
		vector3i.y = this.y * int1;
		vector3i.y = this.z * int1;
		return vector3i;
	}

	public Vector3i mul(Vector3ic vector3ic) {
		this.x += vector3ic.x();
		this.y += vector3ic.y();
		this.z += vector3ic.z();
		return this;
	}

	public Vector3i mul(Vector3ic vector3ic, Vector3i vector3i) {
		vector3i.x = this.x * vector3ic.x();
		vector3i.y = this.y * vector3ic.y();
		vector3i.z = this.z * vector3ic.z();
		return vector3i;
	}

	public Vector3i mul(int int1, int int2, int int3) {
		this.x *= int1;
		this.y *= int2;
		this.z *= int3;
		return this;
	}

	public Vector3i mul(int int1, int int2, int int3, Vector3i vector3i) {
		vector3i.x = this.x * int1;
		vector3i.y = this.y * int2;
		vector3i.z = this.z * int3;
		return vector3i;
	}

	public long lengthSquared() {
		return (long)(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	public double length() {
		return Math.sqrt((double)this.lengthSquared());
	}

	public double distance(Vector3ic vector3ic) {
		return Math.sqrt((double)this.distanceSquared(vector3ic));
	}

	public double distance(int int1, int int2, int int3) {
		return Math.sqrt((double)this.distanceSquared(int1, int2, int3));
	}

	public long distanceSquared(Vector3ic vector3ic) {
		int int1 = this.x - vector3ic.x();
		int int2 = this.y - vector3ic.y();
		int int3 = this.z - vector3ic.z();
		return (long)(int1 * int1 + int2 * int2 + int3 * int3);
	}

	public long distanceSquared(int int1, int int2, int int3) {
		int int4 = this.x - int1;
		int int5 = this.y - int2;
		int int6 = this.z - int3;
		return (long)(int4 * int4 + int5 * int5 + int6 * int6);
	}

	public Vector3i zero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}

	public String toString() {
		return "(" + this.x + " " + this.y + " " + this.z + ")";
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format((long)this.x) + " " + numberFormat.format((long)this.y) + " " + numberFormat.format((long)this.z) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeInt(this.x);
		objectOutput.writeInt(this.y);
		objectOutput.writeInt(this.z);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readInt();
		this.y = objectInput.readInt();
		this.z = objectInput.readInt();
	}

	public Vector3i negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	public Vector3i negate(Vector3i vector3i) {
		vector3i.x = -this.x;
		vector3i.y = -this.y;
		vector3i.z = -this.z;
		return vector3i;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + this.x;
		int1 = 31 * int1 + this.y;
		int1 = 31 * int1 + this.z;
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
			Vector3i vector3i = (Vector3i)object;
			if (this.x != vector3i.x) {
				return false;
			} else if (this.y != vector3i.y) {
				return false;
			} else {
				return this.z == vector3i.z;
			}
		}
	}

	public Vector3ic toImmutable() {
		return (Vector3ic)(!Options.DEBUG ? this : new Vector3i.Proxy(this));
	}

	private final class Proxy implements Vector3ic {
		private final Vector3ic delegate;

		Proxy(Vector3ic vector3ic) {
			this.delegate = vector3ic;
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

		public Vector3i sub(Vector3ic vector3ic, Vector3i vector3i) {
			return this.delegate.sub(vector3ic, vector3i);
		}

		public Vector3i sub(int int1, int int2, int int3, Vector3i vector3i) {
			return this.delegate.sub(int1, int2, int3, vector3i);
		}

		public Vector3i add(Vector3ic vector3ic, Vector3i vector3i) {
			return this.delegate.add(vector3ic, vector3i);
		}

		public Vector3i add(int int1, int int2, int int3, Vector3i vector3i) {
			return this.delegate.add(int1, int2, int3, vector3i);
		}

		public Vector3i mul(int int1, Vector3i vector3i) {
			return this.delegate.mul(int1, vector3i);
		}

		public Vector3i mul(Vector3ic vector3ic, Vector3i vector3i) {
			return this.delegate.mul(vector3ic, vector3i);
		}

		public Vector3i mul(int int1, int int2, int int3, Vector3i vector3i) {
			return this.delegate.mul(int1, int2, int3, vector3i);
		}

		public long lengthSquared() {
			return this.delegate.lengthSquared();
		}

		public double length() {
			return this.delegate.length();
		}

		public double distance(Vector3ic vector3ic) {
			return this.delegate.distance(vector3ic);
		}

		public double distance(int int1, int int2, int int3) {
			return this.delegate.distance(int1, int2, int3);
		}

		public long distanceSquared(Vector3ic vector3ic) {
			return this.delegate.distanceSquared(vector3ic);
		}

		public long distanceSquared(int int1, int int2, int int3) {
			return this.delegate.distanceSquared(int1, int2, int3);
		}

		public Vector3i negate(Vector3i vector3i) {
			return this.delegate.negate(vector3i);
		}
	}
}
