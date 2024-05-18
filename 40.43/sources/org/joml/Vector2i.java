package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;


public class Vector2i implements Externalizable,Vector2ic {
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;

	public Vector2i() {
	}

	public Vector2i(int int1) {
		this.x = int1;
		this.y = int1;
	}

	public Vector2i(int int1, int int2) {
		this.x = int1;
		this.y = int2;
	}

	public Vector2i(Vector2ic vector2ic) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
	}

	public Vector2i(ByteBuffer byteBuffer) {
		this(byteBuffer.position(), byteBuffer);
	}

	public Vector2i(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector2i(IntBuffer intBuffer) {
		this(intBuffer.position(), intBuffer);
	}

	public Vector2i(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, int1, intBuffer);
	}

	public int x() {
		return this.x;
	}

	public int y() {
		return this.y;
	}

	public Vector2i set(int int1) {
		this.x = int1;
		this.y = int1;
		return this;
	}

	public Vector2i set(int int1, int int2) {
		this.x = int1;
		this.y = int2;
		return this;
	}

	public Vector2i set(Vector2ic vector2ic) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
		return this;
	}

	public Vector2i set(Vector2dc vector2dc) {
		this.x = (int)vector2dc.x();
		this.y = (int)vector2dc.y();
		return this;
	}

	public Vector2i set(ByteBuffer byteBuffer) {
		return this.set(byteBuffer.position(), byteBuffer);
	}

	public Vector2i set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector2i set(IntBuffer intBuffer) {
		return this.set(intBuffer.position(), intBuffer);
	}

	public Vector2i set(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, int1, intBuffer);
		return this;
	}

	public Vector2i setComponent(int int1, int int2) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			this.x = int2;
			break;
		
		case 1: 
			this.y = int2;
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

	public IntBuffer get(IntBuffer intBuffer) {
		return this.get(intBuffer.position(), intBuffer);
	}

	public IntBuffer get(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.put(this, int1, intBuffer);
		return intBuffer;
	}

	public Vector2i sub(Vector2ic vector2ic) {
		this.x -= vector2ic.x();
		this.y -= vector2ic.y();
		return this;
	}

	public Vector2i sub(Vector2ic vector2ic, Vector2i vector2i) {
		vector2i.x = this.x - vector2ic.x();
		vector2i.y = this.y - vector2ic.y();
		return vector2i;
	}

	public Vector2i sub(int int1, int int2) {
		this.x -= int1;
		this.y -= int2;
		return this;
	}

	public Vector2i sub(int int1, int int2, Vector2i vector2i) {
		vector2i.x = this.x - int1;
		vector2i.y = this.y - int2;
		return vector2i;
	}

	public long lengthSquared() {
		return (long)(this.x * this.x + this.y * this.y);
	}

	public double length() {
		return Math.sqrt((double)this.lengthSquared());
	}

	public double distance(Vector2ic vector2ic) {
		return Math.sqrt((double)this.distanceSquared(vector2ic));
	}

	public double distance(int int1, int int2) {
		return Math.sqrt((double)this.distanceSquared(int1, int2));
	}

	public long distanceSquared(Vector2ic vector2ic) {
		int int1 = this.x - vector2ic.x();
		int int2 = this.y - vector2ic.y();
		return (long)(int1 * int1 + int2 * int2);
	}

	public long distanceSquared(int int1, int int2) {
		int int3 = this.x - int1;
		int int4 = this.y - int2;
		return (long)(int3 * int3 + int4 * int4);
	}

	public Vector2i add(Vector2ic vector2ic) {
		this.x += vector2ic.x();
		this.y += vector2ic.y();
		return this;
	}

	public Vector2i add(Vector2ic vector2ic, Vector2i vector2i) {
		vector2i.x = this.x + vector2ic.x();
		vector2i.y = this.y + vector2ic.y();
		return vector2i;
	}

	public Vector2i add(int int1, int int2) {
		this.x += int1;
		this.y += int2;
		return this;
	}

	public Vector2i add(int int1, int int2, Vector2i vector2i) {
		vector2i.x = this.x + int1;
		vector2i.y = this.y + int2;
		return vector2i;
	}

	public Vector2i mul(int int1) {
		this.x *= int1;
		this.y *= int1;
		return this;
	}

	public Vector2i mul(int int1, Vector2i vector2i) {
		vector2i.x = this.x * int1;
		vector2i.y = this.y * int1;
		return vector2i;
	}

	public Vector2i mul(Vector2ic vector2ic) {
		this.x += vector2ic.x();
		this.y += vector2ic.y();
		return this;
	}

	public Vector2i mul(Vector2ic vector2ic, Vector2i vector2i) {
		vector2i.x = this.x * vector2ic.x();
		vector2i.y = this.y * vector2ic.y();
		return vector2i;
	}

	public Vector2i mul(int int1, int int2) {
		this.x *= int1;
		this.y *= int2;
		return this;
	}

	public Vector2i mul(int int1, int int2, Vector2i vector2i) {
		vector2i.x = this.x * int1;
		vector2i.y = this.y * int2;
		return vector2i;
	}

	public Vector2i zero() {
		this.x = 0;
		this.y = 0;
		return this;
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeInt(this.x);
		objectOutput.writeInt(this.y);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readInt();
		this.y = objectInput.readInt();
	}

	public Vector2i negate() {
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}

	public Vector2i negate(Vector2i vector2i) {
		vector2i.x = -this.x;
		vector2i.y = -this.y;
		return vector2i;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + this.x;
		int1 = 31 * int1 + this.y;
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
			Vector2i vector2i = (Vector2i)object;
			if (this.x != vector2i.x) {
				return false;
			} else {
				return this.y == vector2i.y;
			}
		}
	}

	public String toString() {
		return "(" + this.x + " " + this.y + ")";
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format((long)this.x) + " " + numberFormat.format((long)this.y) + ")";
	}

	public Vector2ic toImmutable() {
		return (Vector2ic)(!Options.DEBUG ? this : new Vector2i.Proxy(this));
	}

	private final class Proxy implements Vector2ic {
		private final Vector2ic delegate;

		Proxy(Vector2ic vector2ic) {
			this.delegate = vector2ic;
		}

		public int x() {
			return this.delegate.x();
		}

		public int y() {
			return this.delegate.y();
		}

		public ByteBuffer get(ByteBuffer byteBuffer) {
			return this.delegate.get(byteBuffer);
		}

		public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
			return this.delegate.get(int1, byteBuffer);
		}

		public IntBuffer get(IntBuffer intBuffer) {
			return this.delegate.get(intBuffer);
		}

		public IntBuffer get(int int1, IntBuffer intBuffer) {
			return this.delegate.get(int1, intBuffer);
		}

		public Vector2i sub(Vector2ic vector2ic, Vector2i vector2i) {
			return this.delegate.sub(vector2ic, vector2i);
		}

		public Vector2i sub(int int1, int int2, Vector2i vector2i) {
			return this.delegate.sub(int1, int2, vector2i);
		}

		public long lengthSquared() {
			return this.delegate.lengthSquared();
		}

		public double length() {
			return this.delegate.length();
		}

		public double distance(Vector2ic vector2ic) {
			return this.delegate.distance(vector2ic);
		}

		public double distance(int int1, int int2) {
			return this.delegate.distance(int1, int2);
		}

		public long distanceSquared(Vector2ic vector2ic) {
			return this.delegate.distanceSquared(vector2ic);
		}

		public long distanceSquared(int int1, int int2) {
			return this.delegate.distanceSquared(int1, int2);
		}

		public Vector2i add(Vector2ic vector2ic, Vector2i vector2i) {
			return this.delegate.add(vector2ic, vector2i);
		}

		public Vector2i add(int int1, int int2, Vector2i vector2i) {
			return this.delegate.add(int1, int2, vector2i);
		}

		public Vector2i mul(int int1, Vector2i vector2i) {
			return this.delegate.mul(int1, vector2i);
		}

		public Vector2i mul(Vector2ic vector2ic, Vector2i vector2i) {
			return this.delegate.mul(vector2ic, vector2i);
		}

		public Vector2i mul(int int1, int int2, Vector2i vector2i) {
			return this.delegate.mul(int1, int2, vector2i);
		}

		public Vector2i negate(Vector2i vector2i) {
			return this.delegate.negate(vector2i);
		}
	}
}
