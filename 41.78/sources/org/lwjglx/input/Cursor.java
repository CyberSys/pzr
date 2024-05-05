package org.lwjglx.input;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjglx.BufferUtils;
import org.lwjglx.LWJGLException;


public class Cursor {
	public static final int CURSOR_ONE_BIT_TRANSPARENCY = 1;
	public static final int CURSOR_8_BIT_ALPHA = 2;
	public static final int CURSOR_ANIMATION = 4;
	private long cursorHandle;

	public Cursor(int int1, int int2, int int3, int int4, int int5, IntBuffer intBuffer, IntBuffer intBuffer2) throws LWJGLException {
		if (int5 != 1) {
			System.out.println("ANIMATED CURSORS NOT YET SUPPORTED IN LWJGLX");
		} else {
			IntBuffer intBuffer3 = BufferUtils.createIntBuffer(intBuffer.limit());
			flipImages(int1, int2, int5, intBuffer, intBuffer3);
			ByteBuffer byteBuffer = convertARGBIntBuffertoRGBAByteBuffer(int1, int2, intBuffer3);
			GLFWImage gLFWImage = GLFWImage.malloc();
			gLFWImage.width(int1);
			gLFWImage.height(int2);
			gLFWImage.pixels(byteBuffer);
			this.cursorHandle = GLFW.glfwCreateCursor(gLFWImage, int3, int4);
			if (this.cursorHandle == 0L) {
				throw new RuntimeException("Error creating GLFW cursor");
			}
		}
	}

	private static ByteBuffer convertARGBIntBuffertoRGBAByteBuffer(int int1, int int2, IntBuffer intBuffer) {
		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(int1 * int2 * 4);
		for (int int3 = 0; int3 < intBuffer.limit(); ++int3) {
			int int4 = intBuffer.get(int3);
			byte byte1 = (byte)(int4 >>> 24);
			byte byte2 = (byte)(int4 >>> 16);
			byte byte3 = (byte)(int4 >>> 8);
			byte byte4 = (byte)int4;
			byteBuffer.put(byte4);
			byteBuffer.put(byte3);
			byteBuffer.put(byte2);
			byteBuffer.put(byte1);
		}

		byteBuffer.flip();
		return byteBuffer;
	}

	public static int getMinCursorSize() {
		return 1;
	}

	public static int getMaxCursorSize() {
		return 512;
	}

	public static int getCapabilities() {
		return 2;
	}

	private static void flipImages(int int1, int int2, int int3, IntBuffer intBuffer, IntBuffer intBuffer2) {
		for (int int4 = 0; int4 < int3; ++int4) {
			int int5 = int4 * int1 * int2;
			flipImage(int1, int2, int5, intBuffer, intBuffer2);
		}
	}

	private static void flipImage(int int1, int int2, int int3, IntBuffer intBuffer, IntBuffer intBuffer2) {
		for (int int4 = 0; int4 < int2 >> 1; ++int4) {
			int int5 = int4 * int1 + int3;
			int int6 = (int2 - int4 - 1) * int1 + int3;
			for (int int7 = 0; int7 < int1; ++int7) {
				int int8 = int5 + int7;
				int int9 = int6 + int7;
				int int10 = intBuffer.get(int8 + intBuffer.position());
				intBuffer2.put(int8, intBuffer.get(int9 + intBuffer.position()));
				intBuffer2.put(int9, int10);
			}
		}
	}

	public long getHandle() {
		return this.cursorHandle;
	}

	public void destroy() {
		GLFW.glfwDestroyCursor(this.cursorHandle);
	}
}
