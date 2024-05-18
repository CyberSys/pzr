package zombie.core.VBO;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;


public class VBOBuffer {
	static int vertexBufferID = -1;
	static int colourBufferID = -1;
	static int indexBufferID = -1;
	static int numQuads = 0;
	static IntBuffer Indices = null;
	static FloatBuffer Vertices = null;
	static int stride = 0;

	public static void init() {
		stride = 32;
		Indices = BufferUtils.createIntBuffer(50000);
		Vertices = BufferUtils.createFloatBuffer('썐' * stride);
		vertexBufferID = createVBOID();
		colourBufferID = createVBOID();
		indexBufferID = createVBOID();
	}

	public static int createVBOID() {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
			ARBVertexBufferObject.glGenBuffersARB(intBuffer);
			return intBuffer.get(0);
		} else {
			return 0;
		}
	}

	public static void bufferData(int int1, FloatBuffer floatBuffer) {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			ARBVertexBufferObject.glBindBufferARB(34962, int1);
			ARBVertexBufferObject.glBufferDataARB(34962, floatBuffer, 35044);
		}
	}

	public static void bufferElementData(int int1, IntBuffer intBuffer) {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			ARBVertexBufferObject.glBindBufferARB(34963, int1);
			ARBVertexBufferObject.glBufferDataARB(34963, intBuffer, 35044);
		}
	}

	public static void draw() {
	}

	public static void render() {
		bufferData(vertexBufferID, Vertices);
		bufferElementData(indexBufferID, Indices);
		GL11.glEnableClientState(32884);
		GL11.glEnableClientState(32886);
		GL11.glEnableClientState(32888);
		byte byte1 = 0;
		GL11.glVertexPointer(2, 5126, stride, (long)byte1);
		byte1 = 8;
		GL11.glColorPointer(4, 5126, stride, (long)byte1);
		byte1 = 24;
		GL11.glTexCoordPointer(2, 5126, stride, (long)byte1);
		GL12.glDrawRangeElements(7, 0, numQuads, Indices);
	}
}
