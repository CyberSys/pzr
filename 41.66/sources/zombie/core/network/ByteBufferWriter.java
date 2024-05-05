package zombie.core.network;

import java.nio.ByteBuffer;
import zombie.GameWindow;


public final class ByteBufferWriter {
	public ByteBuffer bb;

	public ByteBufferWriter(ByteBuffer byteBuffer) {
		this.bb = byteBuffer;
	}

	public void putBoolean(boolean boolean1) {
		this.bb.put((byte)(boolean1 ? 1 : 0));
	}

	public void putByte(byte byte1) {
		this.bb.put(byte1);
	}

	public void putChar(char char1) {
		this.bb.putChar(char1);
	}

	public void putDouble(double double1) {
		this.bb.putDouble(double1);
	}

	public void putFloat(float float1) {
		this.bb.putFloat(float1);
	}

	public void putInt(int int1) {
		this.bb.putInt(int1);
	}

	public void putLong(long long1) {
		this.bb.putLong(long1);
	}

	public void putShort(short short1) {
		this.bb.putShort(short1);
	}

	public void putUTF(String string) {
		GameWindow.WriteStringUTF(this.bb, string);
	}
}
