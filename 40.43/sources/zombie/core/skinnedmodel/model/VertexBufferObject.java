package zombie.core.skinnedmodel.model;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import zombie.core.VBO.IGLBufferObject;
import zombie.core.skinnedmodel.shader.Shader;


public class VertexBufferObject {
	public static IGLBufferObject funcs;
	VertexBufferObject.Vbo _handle;
	VertexBufferObject.VertexStride[] _vertexStride;
	VertexBufferObject.BeginMode _beginMode;
	public boolean bStatic = false;

	public VertexBufferObject(VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray, int[] intArray) {
		this.bStatic = false;
		this._handle = this.LoadVBO(vertexPositionNormalTangentTextureSkinArray, intArray);
		this._vertexStride = new VertexBufferObject.VertexStride[6];
		for (int int1 = 0; int1 < this._vertexStride.length; ++int1) {
			this._vertexStride[int1] = new VertexBufferObject.VertexStride();
		}

		this._vertexStride[0].Type = VertexBufferObject.VertexType.VertexArray;
		this._vertexStride[0].Offset = 0;
		this._vertexStride[1].Type = VertexBufferObject.VertexType.NormalArray;
		this._vertexStride[1].Offset = 12;
		this._vertexStride[2].Type = VertexBufferObject.VertexType.TangentAray;
		this._vertexStride[2].Offset = 24;
		this._vertexStride[3].Type = VertexBufferObject.VertexType.TextureCoordArray;
		this._vertexStride[3].Offset = 36;
		this._vertexStride[4].Type = VertexBufferObject.VertexType.BlendWeightArray;
		this._vertexStride[4].Offset = 44;
		this._vertexStride[5].Type = VertexBufferObject.VertexType.BlendIndexArray;
		this._vertexStride[5].Offset = 60;
		this._beginMode = VertexBufferObject.BeginMode.Triangles;
	}

	public VertexBufferObject(VertexPositionNormalTangentTexture[] vertexPositionNormalTangentTextureArray, int[] intArray) {
		this.bStatic = true;
		this._handle = this.LoadVBO(vertexPositionNormalTangentTextureArray, intArray);
		boolean boolean1 = true;
		byte byte1 = 4;
		this._vertexStride = new VertexBufferObject.VertexStride[byte1];
		for (int int1 = 0; int1 < this._vertexStride.length; ++int1) {
			this._vertexStride[int1] = new VertexBufferObject.VertexStride();
		}

		this._vertexStride[0].Type = VertexBufferObject.VertexType.VertexArray;
		this._vertexStride[0].Offset = 0;
		this._vertexStride[1].Type = VertexBufferObject.VertexType.NormalArray;
		this._vertexStride[1].Offset = 12;
		this._vertexStride[2].Type = VertexBufferObject.VertexType.TangentAray;
		this._vertexStride[2].Offset = 24;
		this._vertexStride[3].Type = VertexBufferObject.VertexType.TextureCoordArray;
		this._vertexStride[3].Offset = 36;
		this._beginMode = VertexBufferObject.BeginMode.Triangles;
	}

	public void SetFaceDataOnly() {
		this._handle.FaceDataOnly = true;
	}

	VertexBufferObject.Vbo LoadVBO(VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray, int[] intArray) {
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

	VertexBufferObject.Vbo LoadVBO(VertexPositionNormalTangentTexture[] vertexPositionNormalTangentTextureArray, int[] intArray) {
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

	public void Draw(Shader shader) {
		Draw(this._handle, this._vertexStride, this._beginMode, shader);
	}

	private static void Draw(VertexBufferObject.Vbo vbo, VertexBufferObject.VertexStride[] vertexStrideArray, VertexBufferObject.BeginMode beginMode, Shader shader) {
		int int1 = 33984;
		int int2;
		if (!vbo.FaceDataOnly) {
			funcs.glBindBuffer(funcs.GL_ARRAY_BUFFER(), vbo.VboID);
			for (int2 = vertexStrideArray.length - 1; int2 >= 0; --int2) {
				switch (vertexStrideArray[int2].Type) {
				case VertexArray: 
					GL11.glVertexPointer(3, 5126, vbo.VertexStride, (long)vertexStrideArray[int2].Offset);
					GL11.glEnableClientState(32884);
					break;
				
				case NormalArray: 
					GL11.glNormalPointer(5126, vbo.VertexStride, (long)vertexStrideArray[int2].Offset);
					GL11.glEnableClientState(32885);
					break;
				
				case ColorArray: 
					GL11.glColorPointer(3, 5121, vbo.VertexStride, (long)vertexStrideArray[int2].Offset);
					GL11.glEnableClientState(32886);
					break;
				
				case TextureCoordArray: 
					GL13.glActiveTexture(int1);
					GL13.glClientActiveTexture(int1);
					GL11.glTexCoordPointer(2, 5126, vbo.VertexStride, (long)vertexStrideArray[int2].Offset);
					++int1;
					GL11.glEnableClientState(32888);
					break;
				
				case TangentAray: 
					GL11.glNormalPointer(5126, vbo.VertexStride, (long)vertexStrideArray[int2].Offset);
					break;
				
				case BlendWeightArray: 
					int int3 = shader.BoneWeightsAttrib;
					GL20.glVertexAttribPointer(int3, 4, 5126, false, vbo.VertexStride, (long)vertexStrideArray[int2].Offset);
					GL20.glEnableVertexAttribArray(int3);
					break;
				
				case BlendIndexArray: 
					int int4 = shader.BoneIndicesAttrib;
					GL20.glVertexAttribPointer(int4, 4, 5126, false, vbo.VertexStride, (long)vertexStrideArray[int2].Offset);
					GL20.glEnableVertexAttribArray(int4);
				
				}
			}
		}

		funcs.glBindBuffer(funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
		GL11.glDrawElements(4, vbo.NumElements, 5125, 0L);
		GL11.glDisableClientState(32885);
		if (vbo.VertexStride > 44) {
			int2 = shader.BoneWeightsAttrib;
			GL20.glDisableVertexAttribArray(int2);
			int2 = shader.BoneIndicesAttrib;
			GL20.glDisableVertexAttribArray(int2);
		}
	}

	public class VertexStride {
		public VertexBufferObject.VertexType Type;
		public int Offset;
	}

	public class Vbo {
		public IntBuffer b = BufferUtils.createIntBuffer(4);
		public int VboID;
		public int EboID;
		public int NumElements;
		public int VertexStride;
		public boolean FaceDataOnly;
	}
	public static enum BeginMode {

		Triangles;
	}
	public static enum VertexType {

		VertexArray,
		NormalArray,
		ColorArray,
		IndexArray,
		TextureCoordArray,
		TangentAray,
		BlendWeightArray,
		BlendIndexArray;
	}
}
