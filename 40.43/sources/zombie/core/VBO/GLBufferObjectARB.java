package zombie.core.VBO;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBBufferObject;


public final class GLBufferObjectARB implements IGLBufferObject {

	public int GL_ARRAY_BUFFER() {
		return 34962;
	}

	public int GL_ELEMENT_ARRAY_BUFFER() {
		return 34963;
	}

	public int GL_STATIC_DRAW() {
		return 35044;
	}

	public int GL_STREAM_DRAW() {
		return 35040;
	}

	public int GL_BUFFER_SIZE() {
		return 34660;
	}

	public int GL_WRITE_ONLY() {
		return 35001;
	}

	public int glGenBuffers() {
		return ARBBufferObject.glGenBuffersARB();
	}

	public void glBindBuffer(int int1, int int2) {
		ARBBufferObject.glBindBufferARB(int1, int2);
	}

	public void glDeleteBuffers(int int1) {
		ARBBufferObject.glDeleteBuffersARB(int1);
	}

	public void glBufferData(int int1, ByteBuffer byteBuffer, int int2) {
		ARBBufferObject.glBufferDataARB(int1, byteBuffer, int2);
	}

	public void glBufferData(int int1, long long1, int int2) {
		ARBBufferObject.glBufferDataARB(int1, long1, int2);
	}

	public ByteBuffer glMapBuffer(int int1, int int2, long long1, ByteBuffer byteBuffer) {
		return ARBBufferObject.glMapBufferARB(int1, int2, long1, byteBuffer);
	}

	public boolean glUnmapBuffer(int int1) {
		return ARBBufferObject.glUnmapBufferARB(int1);
	}

	public void glGetBufferParameter(int int1, int int2, IntBuffer intBuffer) {
		ARBBufferObject.glGetBufferParameterARB(int1, int2, intBuffer);
	}
}
