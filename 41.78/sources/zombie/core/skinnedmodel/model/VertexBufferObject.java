package zombie.core.skinnedmodel.model;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;
import org.lwjglx.BufferUtils;
import zombie.core.VBO.IGLBufferObject;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.debug.DebugOptions;
import zombie.util.list.PZArrayUtil;


public final class VertexBufferObject {
	public static IGLBufferObject funcs;
	int[] elements;
	VertexBufferObject.Vbo _handle;
	private final VertexBufferObject.VertexFormat m_vertexFormat;
	private VertexBufferObject.BeginMode _beginMode;
	public boolean bStatic = false;

	public VertexBufferObject() {
		this.bStatic = false;
		this.m_vertexFormat = new VertexBufferObject.VertexFormat(4);
		this.m_vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
		this.m_vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
		this.m_vertexFormat.setElement(2, VertexBufferObject.VertexType.ColorArray, 4);
		this.m_vertexFormat.setElement(3, VertexBufferObject.VertexType.TextureCoordArray, 8);
		this.m_vertexFormat.calculate();
		this._beginMode = VertexBufferObject.BeginMode.Triangles;
	}

	@Deprecated
	public VertexBufferObject(VertexPositionNormalTangentTexture[] vertexPositionNormalTangentTextureArray, int[] intArray) {
		this.elements = intArray;
		this.bStatic = true;
		RenderThread.invokeOnRenderContext(this, vertexPositionNormalTangentTextureArray, intArray, (vertexPositionNormalTangentTextureArrayx,intArrayx,var3)->{
			vertexPositionNormalTangentTextureArrayx._handle = this.LoadVBO(intArrayx, var3);
		});
		this.m_vertexFormat = new VertexBufferObject.VertexFormat(4);
		this.m_vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
		this.m_vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
		this.m_vertexFormat.setElement(2, VertexBufferObject.VertexType.TangentArray, 12);
		this.m_vertexFormat.setElement(3, VertexBufferObject.VertexType.TextureCoordArray, 8);
		this.m_vertexFormat.calculate();
		this._beginMode = VertexBufferObject.BeginMode.Triangles;
	}

	@Deprecated
	public VertexBufferObject(VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray, int[] intArray, boolean boolean1) {
		this.elements = intArray;
		if (boolean1) {
			int[] intArray2 = new int[intArray.length];
			int int1 = 0;
			for (int int2 = intArray.length - 1 - 2; int2 >= 0; int2 -= 3) {
				intArray2[int1] = intArray[int2];
				intArray2[int1 + 1] = intArray[int2 + 1];
				intArray2[int1 + 2] = intArray[int2 + 2];
				int1 += 3;
			}

			intArray = intArray2;
		}

		this.bStatic = false;
		this._handle = this.LoadVBO(vertexPositionNormalTangentTextureSkinArray, intArray);
		this.m_vertexFormat = new VertexBufferObject.VertexFormat(6);
		this.m_vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
		this.m_vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
		this.m_vertexFormat.setElement(2, VertexBufferObject.VertexType.TangentArray, 12);
		this.m_vertexFormat.setElement(3, VertexBufferObject.VertexType.TextureCoordArray, 8);
		this.m_vertexFormat.setElement(4, VertexBufferObject.VertexType.BlendWeightArray, 16);
		this.m_vertexFormat.setElement(5, VertexBufferObject.VertexType.BlendIndexArray, 16);
		this.m_vertexFormat.calculate();
		this._beginMode = VertexBufferObject.BeginMode.Triangles;
	}

	public VertexBufferObject(VertexBufferObject.VertexArray vertexArray, int[] intArray) {
		this.m_vertexFormat = vertexArray.m_format;
		this.elements = intArray;
		this.bStatic = true;
		RenderThread.invokeOnRenderContext(this, vertexArray, intArray, (vertexArrayx,intArrayx,var3)->{
			vertexArrayx._handle = this.LoadVBO(intArrayx, var3);
		});
		this._beginMode = VertexBufferObject.BeginMode.Triangles;
	}

	public VertexBufferObject(VertexBufferObject.VertexArray vertexArray, int[] intArray, boolean boolean1) {
		this.m_vertexFormat = vertexArray.m_format;
		if (boolean1) {
			int[] intArray2 = new int[intArray.length];
			int int1 = 0;
			for (int int2 = intArray.length - 1 - 2; int2 >= 0; int2 -= 3) {
				intArray2[int1] = intArray[int2];
				intArray2[int1 + 1] = intArray[int2 + 1];
				intArray2[int1 + 2] = intArray[int2 + 2];
				int1 += 3;
			}

			intArray = intArray2;
		}

		this.elements = intArray;
		this.bStatic = false;
		this._handle = this.LoadVBO(vertexArray, intArray);
		this._beginMode = VertexBufferObject.BeginMode.Triangles;
	}

	@Deprecated
	private VertexBufferObject.Vbo LoadVBO(VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray, int[] intArray) {
		VertexBufferObject.Vbo vbo = new VertexBufferObject.Vbo();
		boolean boolean1 = false;
		byte byte1 = 76;
		vbo.FaceDataOnly = false;
		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(vertexPositionNormalTangentTextureSkinArray.length * byte1);
		ByteBuffer byteBuffer2 = BufferUtils.createByteBuffer(intArray.length * 4);
		int int1;
		for (int1 = 0; int1 < vertexPositionNormalTangentTextureSkinArray.length; ++int1) {
			vertexPositionNormalTangentTextureSkinArray[int1].put(byteBuffer);
		}

		for (int1 = 0; int1 < intArray.length; ++int1) {
			byteBuffer2.putInt(intArray[int1]);
		}

		byteBuffer.flip();
		byteBuffer2.flip();
		vbo.VboID = funcs.glGenBuffers();
		funcs.glBindBuffer(funcs.GL_ARRAY_BUFFER(), vbo.VboID);
		funcs.glBufferData(funcs.GL_ARRAY_BUFFER(), byteBuffer, funcs.GL_STATIC_DRAW());
		funcs.glGetBufferParameter(funcs.GL_ARRAY_BUFFER(), funcs.GL_BUFFER_SIZE(), vbo.b);
		int int2 = vbo.b.get();
		if (vertexPositionNormalTangentTextureSkinArray.length * byte1 != int2) {
			throw new RuntimeException("Vertex data not uploaded correctly");
		} else {
			vbo.EboID = funcs.glGenBuffers();
			funcs.glBindBuffer(funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
			funcs.glBufferData(funcs.GL_ELEMENT_ARRAY_BUFFER(), byteBuffer2, funcs.GL_STATIC_DRAW());
			vbo.b.clear();
			funcs.glGetBufferParameter(funcs.GL_ELEMENT_ARRAY_BUFFER(), funcs.GL_BUFFER_SIZE(), vbo.b);
			int2 = vbo.b.get();
			if (intArray.length * 4 != int2) {
				throw new RuntimeException("Element data not uploaded correctly");
			} else {
				vbo.NumElements = intArray.length;
				vbo.VertexStride = byte1;
				return vbo;
			}
		}
	}

	public VertexBufferObject.Vbo LoadSoftwareVBO(ByteBuffer byteBuffer, VertexBufferObject.Vbo vbo, int[] intArray) {
		VertexBufferObject.Vbo vbo2 = vbo;
		boolean boolean1 = false;
		ByteBuffer byteBuffer2 = null;
		if (vbo == null) {
			boolean1 = true;
			vbo2 = new VertexBufferObject.Vbo();
			vbo2.VboID = funcs.glGenBuffers();
			ByteBuffer byteBuffer3 = BufferUtils.createByteBuffer(intArray.length * 4);
			for (int int1 = 0; int1 < intArray.length; ++int1) {
				byteBuffer3.putInt(intArray[int1]);
			}

			byteBuffer3.flip();
			byteBuffer2 = byteBuffer3;
			vbo2.VertexStride = 36;
			vbo2.NumElements = intArray.length;
		} else {
			vbo.b.clear();
		}

		vbo2.FaceDataOnly = false;
		funcs.glBindBuffer(funcs.GL_ARRAY_BUFFER(), vbo2.VboID);
		funcs.glBufferData(funcs.GL_ARRAY_BUFFER(), byteBuffer, funcs.GL_STATIC_DRAW());
		funcs.glGetBufferParameter(funcs.GL_ARRAY_BUFFER(), funcs.GL_BUFFER_SIZE(), vbo2.b);
		if (byteBuffer2 != null) {
			vbo2.EboID = funcs.glGenBuffers();
			funcs.glBindBuffer(funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo2.EboID);
			funcs.glBufferData(funcs.GL_ELEMENT_ARRAY_BUFFER(), byteBuffer2, funcs.GL_STATIC_DRAW());
		}

		return vbo2;
	}

	@Deprecated
	private VertexBufferObject.Vbo LoadVBO(VertexPositionNormalTangentTexture[] vertexPositionNormalTangentTextureArray, int[] intArray) {
		VertexBufferObject.Vbo vbo = new VertexBufferObject.Vbo();
		boolean boolean1 = false;
		byte byte1 = 44;
		vbo.FaceDataOnly = false;
		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(vertexPositionNormalTangentTextureArray.length * byte1);
		ByteBuffer byteBuffer2 = BufferUtils.createByteBuffer(intArray.length * 4);
		int int1;
		for (int1 = 0; int1 < vertexPositionNormalTangentTextureArray.length; ++int1) {
			vertexPositionNormalTangentTextureArray[int1].put(byteBuffer);
		}

		for (int1 = 0; int1 < intArray.length; ++int1) {
			byteBuffer2.putInt(intArray[int1]);
		}

		byteBuffer.flip();
		byteBuffer2.flip();
		vbo.VboID = funcs.glGenBuffers();
		funcs.glBindBuffer(funcs.GL_ARRAY_BUFFER(), vbo.VboID);
		funcs.glBufferData(funcs.GL_ARRAY_BUFFER(), byteBuffer, funcs.GL_STATIC_DRAW());
		funcs.glGetBufferParameter(funcs.GL_ARRAY_BUFFER(), funcs.GL_BUFFER_SIZE(), vbo.b);
		int int2 = vbo.b.get();
		if (vertexPositionNormalTangentTextureArray.length * byte1 != int2) {
			throw new RuntimeException("Vertex data not uploaded correctly");
		} else {
			vbo.EboID = funcs.glGenBuffers();
			funcs.glBindBuffer(funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
			funcs.glBufferData(funcs.GL_ELEMENT_ARRAY_BUFFER(), byteBuffer2, funcs.GL_STATIC_DRAW());
			vbo.b.clear();
			funcs.glGetBufferParameter(funcs.GL_ELEMENT_ARRAY_BUFFER(), funcs.GL_BUFFER_SIZE(), vbo.b);
			int2 = vbo.b.get();
			if (intArray.length * 4 != int2) {
				throw new RuntimeException("Element data not uploaded correctly");
			} else {
				vbo.NumElements = intArray.length;
				vbo.VertexStride = byte1;
				return vbo;
			}
		}
	}

	private VertexBufferObject.Vbo LoadVBO(VertexBufferObject.VertexArray vertexArray, int[] intArray) {
		VertexBufferObject.Vbo vbo = new VertexBufferObject.Vbo();
		vbo.FaceDataOnly = false;
		ByteBuffer byteBuffer = MemoryUtil.memAlloc(intArray.length * 4);
		int int1;
		for (int1 = 0; int1 < intArray.length; ++int1) {
			byteBuffer.putInt(intArray[int1]);
		}

		vertexArray.m_buffer.position(0);
		vertexArray.m_buffer.limit(vertexArray.m_numVertices * vertexArray.m_format.m_stride);
		byteBuffer.flip();
		vbo.VboID = funcs.glGenBuffers();
		funcs.glBindBuffer(funcs.GL_ARRAY_BUFFER(), vbo.VboID);
		funcs.glBufferData(funcs.GL_ARRAY_BUFFER(), vertexArray.m_buffer, funcs.GL_STATIC_DRAW());
		funcs.glGetBufferParameter(funcs.GL_ARRAY_BUFFER(), funcs.GL_BUFFER_SIZE(), vbo.b);
		int1 = vbo.b.get();
		if (vertexArray.m_numVertices * vertexArray.m_format.m_stride != int1) {
			throw new RuntimeException("Vertex data not uploaded correctly");
		} else {
			vbo.EboID = funcs.glGenBuffers();
			funcs.glBindBuffer(funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
			funcs.glBufferData(funcs.GL_ELEMENT_ARRAY_BUFFER(), byteBuffer, funcs.GL_STATIC_DRAW());
			MemoryUtil.memFree(byteBuffer);
			vbo.b.clear();
			funcs.glGetBufferParameter(funcs.GL_ELEMENT_ARRAY_BUFFER(), funcs.GL_BUFFER_SIZE(), vbo.b);
			int1 = vbo.b.get();
			if (intArray.length * 4 != int1) {
				throw new RuntimeException("Element data not uploaded correctly");
			} else {
				vbo.NumElements = intArray.length;
				vbo.VertexStride = vertexArray.m_format.m_stride;
				return vbo;
			}
		}
	}

	public void clear() {
		if (this._handle != null) {
			if (this._handle.VboID > 0) {
				funcs.glDeleteBuffers(this._handle.VboID);
				this._handle.VboID = -1;
			}

			if (this._handle.EboID > 0) {
				funcs.glDeleteBuffers(this._handle.EboID);
				this._handle.EboID = -1;
			}

			this._handle = null;
		}
	}

	public void Draw(Shader shader) {
		Draw(this._handle, this.m_vertexFormat, shader, 4);
	}

	public void DrawStrip(Shader shader) {
		Draw(this._handle, this.m_vertexFormat, shader, 5);
	}

	private static void Draw(VertexBufferObject.Vbo vbo, VertexBufferObject.VertexFormat vertexFormat, Shader shader, int int1) {
		if (vbo != null) {
			if (!DebugOptions.instance.DebugDraw_SkipVBODraw.getValue()) {
				int int2 = 33984;
				boolean boolean1 = false;
				int int3;
				if (!vbo.FaceDataOnly) {
					funcs.glBindBuffer(funcs.GL_ARRAY_BUFFER(), vbo.VboID);
					for (int3 = 0; int3 < vertexFormat.m_elements.length; ++int3) {
						VertexBufferObject.VertexElement vertexElement = vertexFormat.m_elements[int3];
						switch (vertexElement.m_type) {
						case VertexArray: 
							GL20.glVertexPointer(3, 5126, vbo.VertexStride, (long)vertexElement.m_byteOffset);
							GL20.glEnableClientState(32884);
							break;
						
						case NormalArray: 
							GL20.glNormalPointer(5126, vbo.VertexStride, (long)vertexElement.m_byteOffset);
							GL20.glEnableClientState(32885);
							break;
						
						case ColorArray: 
							GL20.glColorPointer(3, 5121, vbo.VertexStride, (long)vertexElement.m_byteOffset);
							GL20.glEnableClientState(32886);
							break;
						
						case TextureCoordArray: 
							GL20.glActiveTexture(int2);
							GL20.glClientActiveTexture(int2);
							GL20.glTexCoordPointer(2, 5126, vbo.VertexStride, (long)vertexElement.m_byteOffset);
							++int2;
							GL20.glEnableClientState(32888);
						
						case TangentArray: 
						
						default: 
							break;
						
						case BlendWeightArray: 
							int int4 = shader.BoneWeightsAttrib;
							GL20.glVertexAttribPointer(int4, 4, 5126, false, vbo.VertexStride, (long)vertexElement.m_byteOffset);
							GL20.glEnableVertexAttribArray(int4);
							boolean1 = true;
							break;
						
						case BlendIndexArray: 
							int int5 = shader.BoneIndicesAttrib;
							GL20.glVertexAttribPointer(int5, 4, 5126, false, vbo.VertexStride, (long)vertexElement.m_byteOffset);
							GL20.glEnableVertexAttribArray(int5);
						
						}
					}
				}

				funcs.glBindBuffer(funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
				GL20.glDrawElements(int1, vbo.NumElements, 5125, 0L);
				GL20.glDisableClientState(32885);
				if (boolean1 && shader != null) {
					int3 = shader.BoneWeightsAttrib;
					GL20.glDisableVertexAttribArray(int3);
					int3 = shader.BoneIndicesAttrib;
					GL20.glDisableVertexAttribArray(int3);
				}
			}
		}
	}

	public static final class VertexFormat {
		final VertexBufferObject.VertexElement[] m_elements;
		int m_stride;

		public VertexFormat(int int1) {
			this.m_elements = (VertexBufferObject.VertexElement[])PZArrayUtil.newInstance(VertexBufferObject.VertexElement.class, int1, VertexBufferObject.VertexElement::new);
		}

		public void setElement(int int1, VertexBufferObject.VertexType vertexType, int int2) {
			this.m_elements[int1].m_type = vertexType;
			this.m_elements[int1].m_byteSize = int2;
		}

		public void calculate() {
			this.m_stride = 0;
			for (int int1 = 0; int1 < this.m_elements.length; ++int1) {
				this.m_elements[int1].m_byteOffset = this.m_stride;
				this.m_stride += this.m_elements[int1].m_byteSize;
			}
		}
	}

	public static enum VertexType {

		VertexArray,
		NormalArray,
		ColorArray,
		IndexArray,
		TextureCoordArray,
		TangentArray,
		BlendWeightArray,
		BlendIndexArray;

		private static VertexBufferObject.VertexType[] $values() {
			return new VertexBufferObject.VertexType[]{VertexArray, NormalArray, ColorArray, IndexArray, TextureCoordArray, TangentArray, BlendWeightArray, BlendIndexArray};
		}
	}
	public static enum BeginMode {

		Triangles;

		private static VertexBufferObject.BeginMode[] $values() {
			return new VertexBufferObject.BeginMode[]{Triangles};
		}
	}

	public static final class Vbo {
		public final IntBuffer b = BufferUtils.createIntBuffer(4);
		public int VboID;
		public int EboID;
		public int NumElements;
		public int VertexStride;
		public boolean FaceDataOnly;
	}

	public static final class VertexArray {
		public final VertexBufferObject.VertexFormat m_format;
		public final int m_numVertices;
		public final ByteBuffer m_buffer;

		public VertexArray(VertexBufferObject.VertexFormat vertexFormat, int int1) {
			this.m_format = vertexFormat;
			this.m_numVertices = int1;
			this.m_buffer = BufferUtils.createByteBuffer(this.m_numVertices * this.m_format.m_stride);
		}

		public void setElement(int int1, int int2, float float1, float float2) {
			int int3 = int1 * this.m_format.m_stride + this.m_format.m_elements[int2].m_byteOffset;
			this.m_buffer.putFloat(int3, float1);
			int3 += 4;
			this.m_buffer.putFloat(int3, float2);
		}

		public void setElement(int int1, int int2, float float1, float float2, float float3) {
			int int3 = int1 * this.m_format.m_stride + this.m_format.m_elements[int2].m_byteOffset;
			this.m_buffer.putFloat(int3, float1);
			int3 += 4;
			this.m_buffer.putFloat(int3, float2);
			int3 += 4;
			this.m_buffer.putFloat(int3, float3);
		}

		public void setElement(int int1, int int2, float float1, float float2, float float3, float float4) {
			int int3 = int1 * this.m_format.m_stride + this.m_format.m_elements[int2].m_byteOffset;
			this.m_buffer.putFloat(int3, float1);
			int3 += 4;
			this.m_buffer.putFloat(int3, float2);
			int3 += 4;
			this.m_buffer.putFloat(int3, float3);
			int3 += 4;
			this.m_buffer.putFloat(int3, float4);
		}

		float getElementFloat(int int1, int int2, int int3) {
			int int4 = int1 * this.m_format.m_stride + this.m_format.m_elements[int2].m_byteOffset + int3 * 4;
			return this.m_buffer.getFloat(int4);
		}
	}

	public static final class VertexElement {
		public VertexBufferObject.VertexType m_type;
		public int m_byteSize;
		public int m_byteOffset;
	}
}
