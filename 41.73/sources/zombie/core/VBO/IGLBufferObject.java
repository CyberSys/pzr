package zombie.core.VBO;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;


public interface IGLBufferObject {

	int GL_ARRAY_BUFFER();

	int GL_ELEMENT_ARRAY_BUFFER();

	int GL_STATIC_DRAW();

	int GL_STREAM_DRAW();

	int GL_BUFFER_SIZE();

	int GL_WRITE_ONLY();

	int glGenBuffers();

	void glBindBuffer(int int1, int int2);

	void glDeleteBuffers(int int1);

	void glBufferData(int int1, ByteBuffer byteBuffer, int int2);

	void glBufferData(int int1, long long1, int int2);

	ByteBuffer glMapBuffer(int int1, int int2, long long1, ByteBuffer byteBuffer);

	boolean glUnmapBuffer(int int1);

	void glGetBufferParameter(int int1, int int2, IntBuffer intBuffer);
}
