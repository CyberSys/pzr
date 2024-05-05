package org.lwjglx;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;


public final class BufferUtils {

	public static ByteBuffer createByteBuffer(int int1) {
		return ByteBuffer.allocateDirect(int1).order(ByteOrder.nativeOrder());
	}

	public static ShortBuffer createShortBuffer(int int1) {
		return createByteBuffer(int1 << 1).asShortBuffer();
	}

	public static CharBuffer createCharBuffer(int int1) {
		return createByteBuffer(int1 << 1).asCharBuffer();
	}

	public static IntBuffer createIntBuffer(int int1) {
		return createByteBuffer(int1 << 2).asIntBuffer();
	}

	public static LongBuffer createLongBuffer(int int1) {
		return createByteBuffer(int1 << 3).asLongBuffer();
	}

	public static FloatBuffer createFloatBuffer(int int1) {
		return createByteBuffer(int1 << 2).asFloatBuffer();
	}

	public static DoubleBuffer createDoubleBuffer(int int1) {
		return createByteBuffer(int1 << 3).asDoubleBuffer();
	}

	public static int getElementSizeExponent(Buffer buffer) {
		if (buffer instanceof ByteBuffer) {
			return 0;
		} else if (!(buffer instanceof ShortBuffer) && !(buffer instanceof CharBuffer)) {
			if (!(buffer instanceof FloatBuffer) && !(buffer instanceof IntBuffer)) {
				if (!(buffer instanceof LongBuffer) && !(buffer instanceof DoubleBuffer)) {
					throw new IllegalStateException("Unsupported buffer type: " + buffer);
				} else {
					return 3;
				}
			} else {
				return 2;
			}
		} else {
			return 1;
		}
	}

	public static int getOffset(Buffer buffer) {
		return buffer.position() << getElementSizeExponent(buffer);
	}

	public static void zeroBuffer(ByteBuffer byteBuffer) {
		zeroBuffer0(byteBuffer, (long)byteBuffer.position(), (long)byteBuffer.remaining());
	}

	public static void zeroBuffer(ShortBuffer shortBuffer) {
		zeroBuffer0(shortBuffer, (long)shortBuffer.position() * 2L, (long)shortBuffer.remaining() * 2L);
	}

	public static void zeroBuffer(CharBuffer charBuffer) {
		zeroBuffer0(charBuffer, (long)charBuffer.position() * 2L, (long)charBuffer.remaining() * 2L);
	}

	public static void zeroBuffer(IntBuffer intBuffer) {
		zeroBuffer0(intBuffer, (long)intBuffer.position() * 4L, (long)intBuffer.remaining() * 4L);
	}

	public static void zeroBuffer(FloatBuffer floatBuffer) {
		zeroBuffer0(floatBuffer, (long)floatBuffer.position() * 4L, (long)floatBuffer.remaining() * 4L);
	}

	public static void zeroBuffer(LongBuffer longBuffer) {
		zeroBuffer0(longBuffer, (long)longBuffer.position() * 8L, (long)longBuffer.remaining() * 8L);
	}

	public static void zeroBuffer(DoubleBuffer doubleBuffer) {
		zeroBuffer0(doubleBuffer, (long)doubleBuffer.position() * 8L, (long)doubleBuffer.remaining() * 8L);
	}

	private static native void zeroBuffer0(Buffer buffer, long long1, long long2);

	static native long getBufferAddress(Buffer buffer);
}
