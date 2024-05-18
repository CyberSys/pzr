package javax.vecmath;

import java.io.Serializable;


public abstract class Tuple3b implements Serializable,Cloneable {
	static final long serialVersionUID = -483782685323607044L;
	public byte x;
	public byte y;
	public byte z;

	public Tuple3b(byte byte1, byte byte2, byte byte3) {
		this.x = byte1;
		this.y = byte2;
		this.z = byte3;
	}

	public Tuple3b(byte[] byteArray) {
		this.x = byteArray[0];
		this.y = byteArray[1];
		this.z = byteArray[2];
	}

	public Tuple3b(Tuple3b tuple3b) {
		this.x = tuple3b.x;
		this.y = tuple3b.y;
		this.z = tuple3b.z;
	}

	public Tuple3b() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public String toString() {
		return "(" + (this.x & 255) + ", " + (this.y & 255) + ", " + (this.z & 255) + ")";
	}

	public final void get(byte[] byteArray) {
		byteArray[0] = this.x;
		byteArray[1] = this.y;
		byteArray[2] = this.z;
	}

	public final void get(Tuple3b tuple3b) {
		tuple3b.x = this.x;
		tuple3b.y = this.y;
		tuple3b.z = this.z;
	}

	public final void set(Tuple3b tuple3b) {
		this.x = tuple3b.x;
		this.y = tuple3b.y;
		this.z = tuple3b.z;
	}

	public final void set(byte[] byteArray) {
		this.x = byteArray[0];
		this.y = byteArray[1];
		this.z = byteArray[2];
	}

	public boolean equals(Tuple3b tuple3b) {
		try {
			return this.x == tuple3b.x && this.y == tuple3b.y && this.z == tuple3b.z;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Tuple3b tuple3b = (Tuple3b)object;
			return this.x == tuple3b.x && this.y == tuple3b.y && this.z == tuple3b.z;
		} catch (NullPointerException nullPointerException) {
			return false;
		} catch (ClassCastException classCastException) {
			return false;
		}
	}

	public int hashCode() {
		return (this.x & 255) << 0 | (this.y & 255) << 8 | (this.z & 255) << 16;
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
}
