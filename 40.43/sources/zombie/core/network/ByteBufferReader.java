package zombie.core.network;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;


public class ByteBufferReader {
	public ByteBuffer bb;

	public ByteBufferReader(ByteBuffer byteBuffer) {
		this.bb = byteBuffer;
	}

	public boolean getBoolean() {
		return this.bb.get() != 0;
	}

	public byte getByte() {
		return this.bb.get();
	}

	public char getChar() {
		return this.bb.getChar();
	}

	public double getDouble() {
		return this.bb.getDouble();
	}

	public float getFloat() {
		return this.bb.getFloat();
	}

	public int getInt() {
		return this.bb.getInt();
	}

	public long getLong() {
		return this.bb.getLong();
	}

	public short getShort() {
		return this.bb.getShort();
	}

	public String getUTF() {
		short short1 = this.bb.getShort();
		byte[] byteArray = new byte[short1];
		this.bb.get(byteArray);
		try {
			return new String(byteArray, "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new RuntimeException("Bad encoding!");
		}
	}
}
