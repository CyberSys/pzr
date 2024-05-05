package zombie.util;

import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;


public class ByteBufferOutputStream extends OutputStream {
	private ByteBuffer wrappedBuffer;
	private final boolean autoEnlarge;

	public ByteBufferOutputStream(ByteBuffer byteBuffer, boolean boolean1) {
		this.wrappedBuffer = byteBuffer;
		this.autoEnlarge = boolean1;
	}

	public ByteBuffer toByteBuffer() {
		ByteBuffer byteBuffer = this.wrappedBuffer.duplicate();
		byteBuffer.flip();
		return byteBuffer.asReadOnlyBuffer();
	}

	public ByteBuffer getWrappedBuffer() {
		return this.wrappedBuffer;
	}

	public void clear() {
		this.wrappedBuffer.clear();
	}

	public void flip() {
		this.wrappedBuffer.flip();
	}

	private void growTo(int int1) {
		int int2 = this.wrappedBuffer.capacity();
		int int3 = int2 << 1;
		if (int3 - int1 < 0) {
			int3 = int1;
		}

		if (int3 < 0) {
			if (int1 < 0) {
				throw new OutOfMemoryError();
			}

			int3 = Integer.MAX_VALUE;
		}

		ByteBuffer byteBuffer = this.wrappedBuffer;
		if (this.wrappedBuffer.isDirect()) {
			this.wrappedBuffer = ByteBuffer.allocateDirect(int3);
		} else {
			this.wrappedBuffer = ByteBuffer.allocate(int3);
		}

		byteBuffer.flip();
		this.wrappedBuffer.put(byteBuffer);
	}

	public void write(int int1) {
		try {
			this.wrappedBuffer.put((byte)int1);
		} catch (BufferOverflowException bufferOverflowException) {
			if (!this.autoEnlarge) {
				throw bufferOverflowException;
			}

			int int2 = this.wrappedBuffer.capacity() * 2;
			this.growTo(int2);
			this.write(int1);
		}
	}

	public void write(byte[] byteArray) {
		byte byte1 = 0;
		try {
			int int1 = this.wrappedBuffer.position();
			this.wrappedBuffer.put(byteArray);
		} catch (BufferOverflowException bufferOverflowException) {
			if (!this.autoEnlarge) {
				throw bufferOverflowException;
			}

			int int2 = Math.max(this.wrappedBuffer.capacity() * 2, byte1 + byteArray.length);
			this.growTo(int2);
			this.write(byteArray);
		}
	}

	public void write(byte[] byteArray, int int1, int int2) {
		byte byte1 = 0;
		try {
			int int3 = this.wrappedBuffer.position();
			this.wrappedBuffer.put(byteArray, int1, int2);
		} catch (BufferOverflowException bufferOverflowException) {
			if (!this.autoEnlarge) {
				throw bufferOverflowException;
			}

			int int4 = Math.max(this.wrappedBuffer.capacity() * 2, byte1 + int2);
			this.growTo(int4);
			this.write(byteArray, int1, int2);
		}
	}
}
