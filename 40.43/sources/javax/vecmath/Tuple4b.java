package javax.vecmath;

import java.io.Serializable;


public abstract class Tuple4b implements Serializable,Cloneable {
	static final long serialVersionUID = -8226727741811898211L;
	public byte x;
	public byte y;
	public byte z;
	public byte w;

	public Tuple4b(byte byte1, byte byte2, byte byte3, byte byte4) {
		this.x = byte1;
		this.y = byte2;
		this.z = byte3;
		this.w = byte4;
	}

	public Tuple4b(byte[] byteArray) {
		this.x = byteArray[0];
		this.y = byteArray[1];
		this.z = byteArray[2];
		this.w = byteArray[3];
	}

	public Tuple4b(Tuple4b tuple4b) {
		this.x = tuple4b.x;
		this.y = tuple4b.y;
		this.z = tuple4b.z;
		this.w = tuple4b.w;
	}

	public Tuple4b() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
	}

	public String toString() {
		return "(" + (this.x & 255) + ", " + (this.y & 255) + ", " + (this.z & 255) + ", " + (this.w & 255) + ")";
	}

	public final void get(byte[] byteArray) {
		byteArray[0] = this.x;
		byteArray[1] = this.y;
		byteArray[2] = this.z;
		byteArray[3] = this.w;
	}

	public final void get(Tuple4b tuple4b) {
		tuple4b.x = this.x;
		tuple4b.y = this.y;
		tuple4b.z = this.z;
		tuple4b.w = this.w;
	}

	public final void set(Tuple4b tuple4b) {
		this.x = tuple4b.x;
		this.y = tuple4b.y;
		this.z = tuple4b.z;
		this.w = tuple4b.w;
	}

	public final void set(byte[] byteArray) {
		this.x = byteArray[0];
		this.y = byteArray[1];
		this.z = byteArray[2];
		this.w = byteArray[3];
	}

	public boolean equals(Tuple4b tuple4b) {
		try {
			return this.x == tuple4b.x && this.y == tuple4b.y && this.z == tuple4b.z && this.w == tuple4b.w;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Tuple4b tuple4b = (Tuple4b)object;
			return this.x == tuple4b.x && this.y == tuple4b.y && this.z == tuple4b.z && this.w == tuple4b.w;
		} catch (NullPointerException nullPointerException) {
			return false;
		} catch (ClassCastException classCastException) {
			return false;
		}
	}

	public int hashCode() {
		return (this.x & 255) << 0 | (this.y & 255) << 8 | (this.z & 255) << 16 | (this.w & 255) << 24;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}
	}

	public final byte getX() {
		return this.x;
	}

	public final void setX(byte byte1) {
		this.x = byte1;
	}

	public final byte getY() {
		return this.y;
	}

	public final void setY(byte byte1) {
		this.y = byte1;
	}

	public final byte getZ() {
		return this.z;
	}

	public final void setZ(byte byte1) {
		this.z = byte1;
	}

	public final byte getW() {
		return this.w;
	}

	public final void setW(byte byte1) {
		this.w = byte1;
	}
}
