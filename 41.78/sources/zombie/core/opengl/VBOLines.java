package zombie.core.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import zombie.core.SpriteRenderer;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.VBO.IGLBufferObject;
import zombie.core.math.PZMath;


public final class VBOLines {
	private final int VERTEX_SIZE = 12;
	private final int COLOR_SIZE = 16;
	private final int ELEMENT_SIZE = 28;
	private final int COLOR_OFFSET = 12;
	private final int NUM_LINES = 128;
	private final int NUM_ELEMENTS = 256;
	private final int INDEX_SIZE = 2;
	private GLVertexBufferObject m_vbo;
	private GLVertexBufferObject m_ibo;
	private ByteBuffer m_elements;
	private ByteBuffer m_indices;
	private float m_lineWidth = 1.0F;
	private float m_dx = 0.0F;
	private float m_dy = 0.0F;
	private float m_dz = 0.0F;
	private int m_mode = 1;
	private boolean m_depth_test = false;

	private void create() {
		this.m_elements = BufferUtils.createByteBuffer(7168);
		this.m_indices = BufferUtils.createByteBuffer(512);
		IGLBufferObject iGLBufferObject = GLVertexBufferObject.funcs;
		this.m_vbo = new GLVertexBufferObject(7168L, iGLBufferObject.GL_ARRAY_BUFFER(), iGLBufferObject.GL_STREAM_DRAW());
		this.m_vbo.create();
		this.m_ibo = new GLVertexBufferObject(512L, iGLBufferObject.GL_ELEMENT_ARRAY_BUFFER(), iGLBufferObject.GL_STREAM_DRAW());
		this.m_ibo.create();
	}

	public void setOffset(float float1, float float2, float float3) {
		this.m_dx = float1;
		this.m_dy = float2;
		this.m_dz = float3;
	}

	public void addElement(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		if (this.isFull()) {
			this.flush();
		}

		if (this.m_elements == null) {
			this.create();
		}

		this.m_elements.putFloat(this.m_dx + float1);
		this.m_elements.putFloat(this.m_dy + float2);
		this.m_elements.putFloat(this.m_dz + float3);
		this.m_elements.putFloat(float4);
		this.m_elements.putFloat(float5);
		this.m_elements.putFloat(float6);
		this.m_elements.putFloat(float7);
		short short1 = (short)(this.m_indices.position() / 2);
		this.m_indices.putShort(short1);
	}

	public void addLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
		this.reserve(2);
		this.addElement(float1, float2, float3, float7, float8, float9, float10);
		this.addElement(float4, float5, float6, float7, float8, float9, float10);
	}

	public void addLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14) {
		this.reserve(2);
		this.addElement(float1, float2, float3, float7, float8, float9, float10);
		this.addElement(float4, float5, float6, float11, float12, float13, float14);
	}

	public void addTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13) {
		this.reserve(3);
		this.addElement(float1, float2, float3, float10, float11, float12, float13);
		this.addElement(float4, float5, float6, float10, float11, float12, float13);
		this.addElement(float7, float8, float9, float10, float11, float12, float13);
	}

	public void addQuad(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		this.reserve(6);
		this.addTriangle(float1, float2, float5, float3, float2, float5, float1, float4, float5, float6, float7, float8, float9);
		this.addTriangle(float3, float2, float5, float3, float4, float5, float1, float4, float5, float6, float7, float8, float9);
	}

	boolean isFull() {
		if (this.m_elements == null) {
			return false;
		} else if (this.m_mode == 4 && this.m_elements.position() % 84 == 0 && this.m_elements.position() + 84 > 7168) {
			return true;
		} else {
			return this.m_elements.position() == 7168;
		}
	}

	public void reserve(int int1) {
		if (!this.hasRoomFor(int1)) {
			this.flush();
		}
	}

	boolean hasRoomFor(int int1) {
		return this.m_elements == null || this.m_elements.position() / 28 + int1 <= 256;
	}

	public void flush() {
		if (this.m_elements != null && this.m_elements.position() != 0) {
			this.m_elements.flip();
			this.m_indices.flip();
			GL13.glClientActiveTexture(33984);
			GL11.glDisableClientState(32888);
			this.m_vbo.bind();
			this.m_vbo.bufferData(this.m_elements);
			this.m_ibo.bind();
			this.m_ibo.bufferData(this.m_indices);
			GL11.glEnableClientState(32884);
			GL11.glEnableClientState(32886);
			GL11.glVertexPointer(3, 5126, 28, 0L);
			GL11.glColorPointer(4, 5126, 28, 12L);
			for (int int1 = 7; int1 >= 0; --int1) {
				GL13.glActiveTexture('蓀' + int1);
				GL11.glDisable(3553);
			}

			if (this.m_depth_test) {
				GL11.glEnable(2929);
			} else {
				GL11.glDisable(2929);
			}

			GL11.glEnable(2848);
			GL11.glLineWidth(this.m_lineWidth);
			byte byte1 = 0;
			int int2 = this.m_elements.limit() / 28;
			byte byte2 = 0;
			int int3 = this.m_indices.limit() / 2;
			GL12.glDrawRangeElements(this.m_mode, byte1, byte1 + int2, int3 - byte2, 5123, (long)(byte2 * 2));
			this.m_vbo.bindNone();
			this.m_ibo.bindNone();
			this.m_elements.clear();
			this.m_indices.clear();
			GL11.glEnable(2929);
			GL11.glEnable(3553);
			GL11.glDisable(2848);
			GL13.glClientActiveTexture(33984);
			GL11.glEnableClientState(32888);
			SpriteRenderer.ringBuffer.restoreVBOs = true;
		}
	}

	public void setLineWidth(float float1) {
		if (!PZMath.equal(this.m_lineWidth, float1, 0.01F)) {
			this.flush();
			this.m_lineWidth = float1;
		}
	}

	public void setMode(int int1) {
		assert int1 == 1 || int1 == 4;
		if (int1 != this.m_mode) {
			this.flush();
			this.m_mode = int1;
		}
	}

	public void setDepthTest(boolean boolean1) {
		if (boolean1 != this.m_depth_test) {
			this.flush();
			this.m_depth_test = boolean1;
		}
	}
}
