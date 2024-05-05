package zombie.worldMap;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import zombie.core.SpriteRenderer;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.VBO.IGLBufferObject;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.popman.ObjectPool;


public final class VBOLinesUV {
	private final int VERTEX_SIZE = 12;
	private final int COLOR_SIZE = 16;
	private final int UV_SIZE = 8;
	private final int ELEMENT_SIZE = 36;
	private final int COLOR_OFFSET = 12;
	private final int UV_OFFSET = 28;
	private final int NUM_ELEMENTS = 128;
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
	private final ObjectPool m_runPool = new ObjectPool(VBOLinesUV.Run::new);
	private final ArrayList m_runs = new ArrayList();

	private VBOLinesUV.Run currentRun() {
		return this.m_runs.isEmpty() ? null : (VBOLinesUV.Run)this.m_runs.get(this.m_runs.size() - 1);
	}

	private void create() {
		this.m_elements = BufferUtils.createByteBuffer(4608);
		this.m_indices = BufferUtils.createByteBuffer(256);
		IGLBufferObject iGLBufferObject = GLVertexBufferObject.funcs;
		this.m_vbo = new GLVertexBufferObject(4608L, iGLBufferObject.GL_ARRAY_BUFFER(), iGLBufferObject.GL_STREAM_DRAW());
		this.m_vbo.create();
		this.m_ibo = new GLVertexBufferObject(256L, iGLBufferObject.GL_ELEMENT_ARRAY_BUFFER(), iGLBufferObject.GL_STREAM_DRAW());
		this.m_ibo.create();
	}

	public void setOffset(float float1, float float2, float float3) {
		this.m_dx = float1;
		this.m_dy = float2;
		this.m_dz = float3;
	}

	public void addElement(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		if (this.isFull()) {
			TextureID textureID = this.currentRun().textureID;
			this.flush();
			this.startRun(textureID);
		}

		if (this.m_elements == null) {
			this.create();
		}

		this.m_elements.putFloat(this.m_dx + float1);
		this.m_elements.putFloat(this.m_dy + float2);
		this.m_elements.putFloat(this.m_dz + float3);
		this.m_elements.putFloat(float6);
		this.m_elements.putFloat(float7);
		this.m_elements.putFloat(float8);
		this.m_elements.putFloat(float9);
		this.m_elements.putFloat(float4);
		this.m_elements.putFloat(float5);
		short short1 = (short)(this.m_indices.position() / 2);
		this.m_indices.putShort(short1);
		++this.currentRun().count;
	}

	public void addElement(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		this.addElement(float1, float2, float3, 0.0F, 0.0F, float4, float5, float6, float7);
	}

	public void addLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
		this.addElement(float1, float2, float3, float7, float8, float9, float10);
		this.addElement(float4, float5, float6, float7, float8, float9, float10);
	}

	public void addLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14) {
		this.addElement(float1, float2, float3, float7, float8, float9, float10);
		this.addElement(float4, float5, float6, float11, float12, float13, float14);
	}

	public void addTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, float float17, float float18, float float19) {
		this.reserve(3);
		this.addElement(float1, float2, float3, float4, float5, float16, float17, float18, float19);
		this.addElement(float6, float7, float8, float9, float10, float16, float17, float18, float19);
		this.addElement(float11, float12, float13, float14, float15, float16, float17, float18, float19);
	}

	public void addQuad(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13) {
		this.reserve(4);
		this.addElement(float1, float2, float9, float3, float4, float10, float11, float12, float13);
		this.addElement(float5, float2, float9, float7, float4, float10, float11, float12, float13);
		this.addElement(float5, float6, float9, float7, float8, float10, float11, float12, float13);
		this.addElement(float1, float6, float9, float3, float8, float10, float11, float12, float13);
	}

	public void addQuad(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, float float17, float float18, float float19, float float20, float float21) {
		this.reserve(4);
		this.addElement(float1, float2, float17, float3, float4, float18, float19, float20, float21);
		this.addElement(float5, float6, float17, float7, float8, float18, float19, float20, float21);
		this.addElement(float9, float10, float17, float11, float12, float18, float19, float20, float21);
		this.addElement(float13, float14, float17, float15, float16, float18, float19, float20, float21);
	}

	boolean isFull() {
		if (this.m_elements == null) {
			return false;
		} else if (this.m_mode == 4 && this.m_elements.position() % 108 == 0 && this.m_elements.position() + 108 > 4608) {
			return true;
		} else {
			return this.m_elements.position() == 4608;
		}
	}

	public void reserve(int int1) {
		if (!this.hasRoomFor(int1)) {
			TextureID textureID = this.currentRun() == null ? null : this.currentRun().textureID;
			this.flush();
			if (textureID != null) {
				this.startRun(textureID);
			}
		}
	}

	boolean hasRoomFor(int int1) {
		return this.m_elements == null || this.m_elements.position() / 36 + int1 <= 128;
	}

	public void flush() {
		if (this.m_elements != null && this.m_elements.position() != 0) {
			this.m_elements.flip();
			this.m_indices.flip();
			GL13.glClientActiveTexture(33984);
			GL11.glEnableClientState(32888);
			this.m_vbo.bind();
			this.m_vbo.bufferData(this.m_elements);
			this.m_ibo.bind();
			this.m_ibo.bufferData(this.m_indices);
			GL11.glEnableClientState(32884);
			GL11.glEnableClientState(32886);
			GL11.glVertexPointer(3, 5126, 36, 0L);
			GL11.glColorPointer(4, 5126, 36, 12L);
			GL11.glTexCoordPointer(2, 5126, 36, 28L);
			GL11.glEnable(3553);
			GL11.glDisable(2929);
			GL11.glEnable(2848);
			GL11.glLineWidth(this.m_lineWidth);
			for (int int1 = 0; int1 < this.m_runs.size(); ++int1) {
				VBOLinesUV.Run run = (VBOLinesUV.Run)this.m_runs.get(int1);
				int int2 = run.start;
				int int3 = run.count;
				int int4 = run.start;
				int int5 = int4 + run.count;
				if (run.textureID.getID() == -1) {
					run.textureID.bind();
				} else {
					GL11.glBindTexture(3553, Texture.lastTextureID = run.textureID.getID());
					GL11.glTexParameteri(3553, 10241, 9729);
					GL11.glTexParameteri(3553, 10240, 9728);
				}

				GL12.glDrawRangeElements(this.m_mode, int2, int2 + int3, int5 - int4, 5123, (long)int4 * 2L);
			}

			this.m_vbo.bindNone();
			this.m_ibo.bindNone();
			this.m_elements.clear();
			this.m_indices.clear();
			this.m_runPool.releaseAll(this.m_runs);
			this.m_runs.clear();
			GL11.glEnable(2929);
			GL11.glDisable(2848);
			GL13.glClientActiveTexture(33984);
			SpriteRenderer.ringBuffer.restoreVBOs = true;
			SpriteRenderer.ringBuffer.restoreBoundTextures = true;
		}
	}

	public void setLineWidth(float float1) {
		if (!PZMath.equal(this.m_lineWidth, float1, 0.01F)) {
			TextureID textureID = this.currentRun() == null ? null : this.currentRun().textureID;
			this.flush();
			if (textureID != null) {
				this.startRun(textureID);
			}

			this.m_lineWidth = float1;
		}
	}

	public void setMode(int int1) {
		assert int1 == 1 || int1 == 4 || int1 == 7;
		if (int1 != this.m_mode) {
			TextureID textureID = this.currentRun() == null ? null : this.currentRun().textureID;
			this.flush();
			if (textureID != null) {
				this.startRun(textureID);
			}

			this.m_mode = int1;
		}
	}

	public void startRun(TextureID textureID) {
		VBOLinesUV.Run run = (VBOLinesUV.Run)this.m_runPool.alloc();
		run.start = this.m_elements == null ? 0 : this.m_elements.position() / 36;
		run.count = 0;
		run.textureID = textureID;
		this.m_runs.add(run);
	}

	private static final class Run {
		int start;
		int count;
		TextureID textureID;
	}
}
