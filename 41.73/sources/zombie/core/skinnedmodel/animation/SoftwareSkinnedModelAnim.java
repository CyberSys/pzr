package zombie.core.skinnedmodel.animation;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Point3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjglx.BufferUtils;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.Vector3;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.model.SoftwareModelMesh;
import zombie.core.skinnedmodel.model.UInt4;
import zombie.core.skinnedmodel.model.Vbo;
import zombie.core.skinnedmodel.model.VertexBufferObject;
import zombie.core.skinnedmodel.model.VertexPositionNormalTangentTextureSkin;
import zombie.core.skinnedmodel.model.VertexStride;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.iso.Vector2;


public final class SoftwareSkinnedModelAnim {
	private long animOffset;
	private final VertexBufferObject.BeginMode _beginMode;
	private final VertexStride[] _vertexStride;
	private final Vbo _handle;
	public static Matrix4f[] boneTransforms;
	public static Matrix4f[] worldTransforms;
	public static Matrix4f[] skinTransforms;
	ByteBuffer softwareSkinBufferInt;
	public HashMap AnimationOffset = new HashMap();
	public HashMap AnimationLength = new HashMap();
	public int vertCount = 0;
	private int elementCount;
	static Matrix4f Identity = new Matrix4f();
	private static Vector3f tempVec3f = new Vector3f();
	static javax.vecmath.Matrix4f m = new javax.vecmath.Matrix4f();
	static Point3f tempop = new Point3f();
	static javax.vecmath.Vector3f temponor = new javax.vecmath.Vector3f();
	static Vector3f tot = new Vector3f();
	static Vector3f totn = new Vector3f();
	static Vector3f vec = new Vector3f();

	public void UpdateWorldTransforms(Matrix4f matrix4f, float float1, SkinningData skinningData) {
		Identity.setIdentity();
		tempVec3f.set(0.0F, 1.0F, 0.0F);
		Matrix4f.mul(boneTransforms[0], Identity, worldTransforms[0]);
		for (int int1 = 1; int1 < worldTransforms.length; ++int1) {
			int int2 = (Integer)skinningData.SkeletonHierarchy.get(int1);
			Matrix4f.mul(boneTransforms[int1], worldTransforms[int2], worldTransforms[int1]);
		}
	}

	public void UpdateSkinTransforms(SkinningData skinningData) {
		for (int int1 = 0; int1 < worldTransforms.length; ++int1) {
			Matrix4f.mul((Matrix4f)skinningData.BoneOffset.get(int1), worldTransforms[int1], skinTransforms[int1]);
		}
	}

	public SoftwareSkinnedModelAnim(StaticAnimation[] staticAnimationArray, SoftwareModelMesh softwareModelMesh, SkinningData skinningData) {
		this.vertCount = softwareModelMesh.verticesUnskinned.length;
		this.elementCount = softwareModelMesh.indicesUnskinned.length;
		Vbo vbo = new Vbo();
		int int1;
		if (boneTransforms == null) {
			boneTransforms = new Matrix4f[skinningData.BindPose.size()];
			worldTransforms = new Matrix4f[skinningData.BindPose.size()];
			skinTransforms = new Matrix4f[skinningData.BindPose.size()];
			for (int1 = 0; int1 < skinningData.BindPose.size(); ++int1) {
				boneTransforms[int1] = HelperFunctions.getMatrix();
				boneTransforms[int1].setIdentity();
				worldTransforms[int1] = HelperFunctions.getMatrix();
				worldTransforms[int1].setIdentity();
				skinTransforms[int1] = HelperFunctions.getMatrix();
				skinTransforms[int1].setIdentity();
			}
		}

		int1 = 0;
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = new ArrayList();
		int int2 = 0;
		int int3;
		StaticAnimation staticAnimation;
		int int4;
		for (int3 = 0; int3 < staticAnimationArray.length; ++int3) {
			staticAnimation = staticAnimationArray[int3];
			this.AnimationOffset.put(staticAnimation.Clip.Name, int1);
			this.AnimationLength.put(staticAnimation.Clip.Name, staticAnimation.Matrices.length);
			for (int int5 = 0; int5 < staticAnimation.Matrices.length; ++int5) {
				int[] intArray = softwareModelMesh.indicesUnskinned;
				int int6;
				for (int4 = 0; int4 < intArray.length; ++int4) {
					int6 = intArray[int4];
					arrayList2.add(int6 + int2);
				}

				int2 += this.vertCount;
				Matrix4f[] matrix4fArray = staticAnimation.Matrices[int5];
				boneTransforms = matrix4fArray;
				this.UpdateWorldTransforms((Matrix4f)null, 0.0F, skinningData);
				this.UpdateSkinTransforms(skinningData);
				for (int6 = 0; int6 < softwareModelMesh.verticesUnskinned.length; ++int6) {
					VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin = this.updateSkin(skinTransforms, softwareModelMesh.verticesUnskinned, int6);
					arrayList.add(vertexPositionNormalTangentTextureSkin);
				}

				int1 += softwareModelMesh.indicesUnskinned.length;
			}
		}

		this._vertexStride = new VertexStride[4];
		for (int3 = 0; int3 < this._vertexStride.length; ++int3) {
			this._vertexStride[int3] = new VertexStride();
		}

		this._vertexStride[0].Type = VertexBufferObject.VertexType.VertexArray;
		this._vertexStride[0].Offset = 0;
		this._vertexStride[1].Type = VertexBufferObject.VertexType.NormalArray;
		this._vertexStride[1].Offset = 12;
		this._vertexStride[2].Type = VertexBufferObject.VertexType.ColorArray;
		this._vertexStride[2].Offset = 24;
		this._vertexStride[3].Type = VertexBufferObject.VertexType.TextureCoordArray;
		this._vertexStride[3].Offset = 28;
		this._beginMode = VertexBufferObject.BeginMode.Triangles;
		boolean boolean1 = false;
		staticAnimation = null;
		vbo.VboID = VertexBufferObject.funcs.glGenBuffers();
		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(arrayList.size() * 36);
		ByteBuffer byteBuffer2 = BufferUtils.createByteBuffer(arrayList2.size() * 4);
		for (int4 = 0; int4 < arrayList.size(); ++int4) {
			VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin2 = (VertexPositionNormalTangentTextureSkin)arrayList.get(int4);
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Position.x());
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Position.y());
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Position.z());
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Normal.x());
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Normal.y());
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Normal.z());
			byteBuffer.putInt(-1);
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.TextureCoordinates.x);
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.TextureCoordinates.y);
		}

		for (int4 = 0; int4 < arrayList2.size(); ++int4) {
			byteBuffer2.putInt((Integer)arrayList2.get(int4));
		}

		byteBuffer2.flip();
		byteBuffer.flip();
		vbo.VertexStride = 36;
		vbo.NumElements = arrayList2.size();
		boolean boolean2 = false;
		boolean boolean3 = true;
		vbo.FaceDataOnly = false;
		VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), vbo.VboID);
		VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), byteBuffer, VertexBufferObject.funcs.GL_STATIC_DRAW());
		VertexBufferObject.funcs.glGetBufferParameter(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), VertexBufferObject.funcs.GL_BUFFER_SIZE(), vbo.b);
		vbo.EboID = VertexBufferObject.funcs.glGenBuffers();
		VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
		VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), byteBuffer2, VertexBufferObject.funcs.GL_STATIC_DRAW());
		this._handle = vbo;
	}

	public VertexPositionNormalTangentTextureSkin updateSkin(Matrix4f[] matrix4fArray, VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray, int int1) {
		tot.set(0.0F, 0.0F, 0.0F);
		totn.set(0.0F, 0.0F, 0.0F);
		VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin = vertexPositionNormalTangentTextureSkinArray[int1];
		Matrix4f matrix4f = HelperFunctions.getMatrix();
		Matrix4f matrix4f2 = HelperFunctions.getMatrix();
		matrix4f.setIdentity();
		Matrix4f matrix4f3 = HelperFunctions.getMatrix();
		UInt4 uInt4 = vertexPositionNormalTangentTextureSkin.BlendIndices;
		float float1 = 1.0F;
		Point3f point3f;
		javax.vecmath.Vector3f vector3f;
		Vector3f vector3f2;
		if (vertexPositionNormalTangentTextureSkin.BlendWeights.x > 0.0F) {
			matrix4f2.load(matrix4fArray[uInt4.X]);
			set(matrix4f2, m);
			point3f = tempop;
			tempop.set(vertexPositionNormalTangentTextureSkin.Position.x(), vertexPositionNormalTangentTextureSkin.Position.y(), vertexPositionNormalTangentTextureSkin.Position.z());
			m.transform(point3f);
			point3f.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
			point3f.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
			point3f.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
			vector3f2 = tot;
			vector3f2.x += point3f.x;
			vector3f2 = tot;
			vector3f2.y += point3f.y;
			vector3f2 = tot;
			vector3f2.z += point3f.z;
			vector3f = temponor;
			temponor.set(vertexPositionNormalTangentTextureSkin.Normal.x(), vertexPositionNormalTangentTextureSkin.Normal.y(), vertexPositionNormalTangentTextureSkin.Normal.z());
			m.transform(vector3f);
			vector3f.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
			vector3f.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
			vector3f.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
			vector3f2 = totn;
			vector3f2.x += vector3f.x;
			vector3f2 = totn;
			vector3f2.y += vector3f.y;
			vector3f2 = totn;
			vector3f2.z += vector3f.z;
		}

		if (vertexPositionNormalTangentTextureSkin.BlendWeights.y > 0.0F) {
			matrix4f2.load(matrix4fArray[uInt4.Y]);
			set(matrix4f2, m);
			point3f = tempop;
			tempop.set(vertexPositionNormalTangentTextureSkin.Position.x(), vertexPositionNormalTangentTextureSkin.Position.y(), vertexPositionNormalTangentTextureSkin.Position.z());
			m.transform(point3f);
			point3f.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
			point3f.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
			point3f.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
			vector3f2 = tot;
			vector3f2.x += point3f.x;
			vector3f2 = tot;
			vector3f2.y += point3f.y;
			vector3f2 = tot;
			vector3f2.z += point3f.z;
			vector3f = temponor;
			temponor.set(vertexPositionNormalTangentTextureSkin.Normal.x(), vertexPositionNormalTangentTextureSkin.Normal.y(), vertexPositionNormalTangentTextureSkin.Normal.z());
			m.transform(vector3f);
			vector3f.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
			vector3f.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
			vector3f.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
			vector3f2 = totn;
			vector3f2.x += vector3f.x;
			vector3f2 = totn;
			vector3f2.y += vector3f.y;
			vector3f2 = totn;
			vector3f2.z += vector3f.z;
		}

		if (vertexPositionNormalTangentTextureSkin.BlendWeights.z > 0.0F) {
			matrix4f2.load(matrix4fArray[uInt4.Z]);
			set(matrix4f2, m);
			point3f = tempop;
			tempop.set(vertexPositionNormalTangentTextureSkin.Position.x(), vertexPositionNormalTangentTextureSkin.Position.y(), vertexPositionNormalTangentTextureSkin.Position.z());
			m.transform(point3f);
			point3f.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
			point3f.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
			point3f.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
			vector3f2 = tot;
			vector3f2.x += point3f.x;
			vector3f2 = tot;
			vector3f2.y += point3f.y;
			vector3f2 = tot;
			vector3f2.z += point3f.z;
			vector3f = temponor;
			temponor.set(vertexPositionNormalTangentTextureSkin.Normal.x(), vertexPositionNormalTangentTextureSkin.Normal.y(), vertexPositionNormalTangentTextureSkin.Normal.z());
			m.transform(vector3f);
			vector3f.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
			vector3f.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
			vector3f.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
			vector3f2 = totn;
			vector3f2.x += vector3f.x;
			vector3f2 = totn;
			vector3f2.y += vector3f.y;
			vector3f2 = totn;
			vector3f2.z += vector3f.z;
		}

		if (vertexPositionNormalTangentTextureSkin.BlendWeights.w > 0.0F) {
			matrix4f2.load(matrix4fArray[uInt4.W]);
			set(matrix4f2, m);
			point3f = tempop;
			tempop.set(vertexPositionNormalTangentTextureSkin.Position.x(), vertexPositionNormalTangentTextureSkin.Position.y(), vertexPositionNormalTangentTextureSkin.Position.z());
			m.transform(point3f);
			point3f.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
			point3f.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
			point3f.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
			vector3f2 = tot;
			vector3f2.x += point3f.x;
			vector3f2 = tot;
			vector3f2.y += point3f.y;
			vector3f2 = tot;
			vector3f2.z += point3f.z;
			vector3f = temponor;
			temponor.set(vertexPositionNormalTangentTextureSkin.Normal.x(), vertexPositionNormalTangentTextureSkin.Normal.y(), vertexPositionNormalTangentTextureSkin.Normal.z());
			m.transform(vector3f);
			vector3f.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
			vector3f.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
			vector3f.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
			vector3f2 = totn;
			vector3f2.x += vector3f.x;
			vector3f2 = totn;
			vector3f2.y += vector3f.y;
			vector3f2 = totn;
			vector3f2.z += vector3f.z;
		}

		matrix4f3.setIdentity();
		vec.x = tot.x;
		vec.y = tot.y;
		vec.z = tot.z;
		VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin2 = new VertexPositionNormalTangentTextureSkin();
		vertexPositionNormalTangentTextureSkin2.Position = new Vector3();
		vertexPositionNormalTangentTextureSkin2.Position.set(vec.getX(), vec.getY(), vec.getZ());
		vector3f = temponor;
		vector3f.x = totn.x;
		vector3f.y = totn.y;
		vector3f.z = totn.z;
		vector3f.normalize();
		vertexPositionNormalTangentTextureSkin2.Normal = new Vector3();
		vertexPositionNormalTangentTextureSkin2.Normal.set(vector3f.getX(), vector3f.getY(), vector3f.getZ());
		vertexPositionNormalTangentTextureSkin2.TextureCoordinates = new Vector2();
		vertexPositionNormalTangentTextureSkin2.TextureCoordinates.x = vertexPositionNormalTangentTextureSkin.TextureCoordinates.x;
		vertexPositionNormalTangentTextureSkin2.TextureCoordinates.y = vertexPositionNormalTangentTextureSkin.TextureCoordinates.y;
		HelperFunctions.returnMatrix(matrix4f);
		HelperFunctions.returnMatrix(matrix4f3);
		HelperFunctions.returnMatrix(matrix4f2);
		return vertexPositionNormalTangentTextureSkin2;
	}

	public void Draw(int int1, int int2, String string) {
		this.Draw(this._handle, this._vertexStride, this._beginMode, (Shader)null, int1, int2, string);
	}

	static void set(Matrix4f matrix4f, javax.vecmath.Matrix4f matrix4f2) {
		matrix4f2.m00 = matrix4f.m00;
		matrix4f2.m01 = matrix4f.m01;
		matrix4f2.m02 = matrix4f.m02;
		matrix4f2.m03 = matrix4f.m03;
		matrix4f2.m10 = matrix4f.m10;
		matrix4f2.m11 = matrix4f.m11;
		matrix4f2.m12 = matrix4f.m12;
		matrix4f2.m13 = matrix4f.m13;
		matrix4f2.m20 = matrix4f.m20;
		matrix4f2.m21 = matrix4f.m21;
		matrix4f2.m22 = matrix4f.m22;
		matrix4f2.m23 = matrix4f.m23;
		matrix4f2.m30 = matrix4f.m30;
		matrix4f2.m31 = matrix4f.m31;
		matrix4f2.m32 = matrix4f.m32;
		matrix4f2.m33 = matrix4f.m33;
	}

	private void Draw(Vbo vbo, VertexStride[] vertexStrideArray, VertexBufferObject.BeginMode beginMode, Shader shader, int int1, int int2, String string) {
		this.animOffset = (long)(int2 + this.elementCount * int1);
		int int3 = this.elementCount;
		int int4 = 33984;
		if (!vbo.FaceDataOnly) {
			VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), vbo.VboID);
			for (int int5 = vertexStrideArray.length - 1; int5 >= 0; --int5) {
				switch (vertexStrideArray[int5].Type) {
				case VertexArray: 
					GL20.glVertexPointer(3, 5126, vbo.VertexStride, (long)vertexStrideArray[int5].Offset);
					GL20.glEnableClientState(32884);
					break;
				
				case NormalArray: 
					GL20.glNormalPointer(5126, vbo.VertexStride, (long)vertexStrideArray[int5].Offset);
					GL20.glEnableClientState(32885);
					break;
				
				case ColorArray: 
					GL20.glColorPointer(3, 5121, vbo.VertexStride, (long)vertexStrideArray[int5].Offset);
					GL20.glEnableClientState(32886);
					break;
				
				case TextureCoordArray: 
					GL13.glActiveTexture(int4);
					GL13.glClientActiveTexture(int4);
					GL20.glTexCoordPointer(2, 5126, vbo.VertexStride, (long)vertexStrideArray[int5].Offset);
					++int4;
					GL20.glEnableClientState(32888);
					break;
				
				case TangentArray: 
					GL20.glNormalPointer(5126, vbo.VertexStride, (long)vertexStrideArray[int5].Offset);
					break;
				
				case BlendWeightArray: 
					int int6 = GL20.glGetAttribLocation(shader.getID(), "boneWeights");
					GL20.glVertexAttribPointer(int6, 4, 5126, false, vbo.VertexStride, (long)vertexStrideArray[int5].Offset);
					GL20.glEnableVertexAttribArray(int6);
					break;
				
				case BlendIndexArray: 
					int int7 = GL20.glGetAttribLocation(shader.getID(), "boneIndices");
					GL20.glVertexAttribPointer(int7, 4, 5126, false, vbo.VertexStride, (long)vertexStrideArray[int5].Offset);
					GL20.glEnableVertexAttribArray(int7);
				
				}
			}
		}

		VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
		GL20.glDrawElements(4, int3, 5125, this.animOffset * 4L);
		GL20.glDisableClientState(32885);
	}
}
