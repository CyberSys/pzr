package zombie.worldMap;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.VBO.IGLBufferObject;


public final class WorldMapVBOs {
	private static final int VERTEX_SIZE = 12;
	private static final int COLOR_SIZE = 16;
	private static final int ELEMENT_SIZE = 28;
	private static final int COLOR_OFFSET = 12;
	public static final int NUM_ELEMENTS = 2340;
	private static final int INDEX_SIZE = 2;
	private static final WorldMapVBOs instance = new WorldMapVBOs();
	private final ArrayList m_vbos = new ArrayList();
	private ByteBuffer m_elements;
	private ByteBuffer m_indices;

	public static WorldMapVBOs getInstance() {
		return instance;
	}

	public void create() {
		this.m_elements = BufferUtils.createByteBuffer(65520);
		this.m_indices = BufferUtils.createByteBuffer(4680);
	}

	private void flush() {
		if (this.m_vbos.isEmpty()) {
			WorldMapVBOs.WorldMapVBO worldMapVBO = new WorldMapVBOs.WorldMapVBO();
			worldMapVBO.create();
			this.m_vbos.add(worldMapVBO);
		}

		this.m_elements.flip();
		this.m_indices.flip();
		((WorldMapVBOs.WorldMapVBO)this.m_vbos.get(this.m_vbos.size() - 1)).flush(this.m_elements, this.m_indices);
		this.m_elements.position(this.m_elements.limit());
		this.m_elements.limit(this.m_elements.capacity());
		this.m_indices.position(this.m_indices.limit());
		this.m_indices.limit(this.m_indices.capacity());
	}

	private void addVBO() {
		WorldMapVBOs.WorldMapVBO worldMapVBO = new WorldMapVBOs.WorldMapVBO();
		worldMapVBO.create();
		this.m_vbos.add(worldMapVBO);
		this.m_elements.clear();
		this.m_indices.clear();
	}

	public void reserveVertices(int int1, int[] intArray) {
		if (this.m_indices == null) {
			this.create();
		}

		int int2 = this.m_indices.position() / 2;
		if (int2 + int1 > 2340) {
			this.flush();
			this.addVBO();
		}

		intArray[0] = this.m_vbos.isEmpty() ? 0 : this.m_vbos.size() - 1;
		intArray[1] = this.m_indices.position() / 2;
	}

	public void addElement(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		this.m_elements.putFloat(float1);
		this.m_elements.putFloat(float2);
		this.m_elements.putFloat(float3);
		this.m_elements.putFloat(float4);
		this.m_elements.putFloat(float5);
		this.m_elements.putFloat(float6);
		this.m_elements.putFloat(float7);
		short short1 = (short)(this.m_indices.position() / 2);
		this.m_indices.putShort(short1);
	}

	public void drawElements(int int1, int int2, int int3, int int4) {
		if (int2 >= 0 && int2 < this.m_vbos.size()) {
			WorldMapVBOs.WorldMapVBO worldMapVBO = (WorldMapVBOs.WorldMapVBO)this.m_vbos.get(int2);
			if (int3 >= 0 && int3 + int4 <= worldMapVBO.m_elementCount) {
				worldMapVBO.m_vbo.bind();
				worldMapVBO.m_ibo.bind();
				GL11.glEnableClientState(32884);
				GL11.glDisableClientState(32886);
				GL11.glVertexPointer(3, 5126, 28, 0L);
				for (int int5 = 7; int5 >= 0; --int5) {
					GL13.glActiveTexture('蓀' + int5);
					GL11.glDisable(3553);
				}

				GL11.glDisable(2929);
				GL12.glDrawRangeElements(int1, int3, int3 + int4, int4, 5123, (long)(int3 * 2));
				worldMapVBO.m_vbo.bindNone();
				worldMapVBO.m_ibo.bindNone();
			}
		}
	}

	public void reset() {
	}

	private static final class WorldMapVBO {
		GLVertexBufferObject m_vbo;
		GLVertexBufferObject m_ibo;
		int m_elementCount = 0;

		void create() {
			IGLBufferObject iGLBufferObject = GLVertexBufferObject.funcs;
			this.m_vbo = new GLVertexBufferObject(65520L, iGLBufferObject.GL_ARRAY_BUFFER(), iGLBufferObject.GL_STREAM_DRAW());
			this.m_vbo.create();
			this.m_ibo = new GLVertexBufferObject(4680L, iGLBufferObject.GL_ELEMENT_ARRAY_BUFFER(), iGLBufferObject.GL_STREAM_DRAW());
			this.m_ibo.create();
		}

		void flush(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
			this.m_vbo.bind();
			this.m_vbo.bufferData(byteBuffer);
			this.m_ibo.bind();
			this.m_ibo.bufferData(byteBuffer2);
			this.m_elementCount = byteBuffer2.limit() / 2;
		}
	}
}
