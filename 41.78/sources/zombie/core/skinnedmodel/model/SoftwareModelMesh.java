package zombie.core.skinnedmodel.model;

import zombie.core.skinnedmodel.Vector3;
import zombie.iso.Vector2;


public final class SoftwareModelMesh {
	public int[] indicesUnskinned;
	public VertexPositionNormalTangentTextureSkin[] verticesUnskinned;
	public String Texture;
	public VertexBufferObject vb;

	public SoftwareModelMesh(VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray, int[] intArray) {
		this.indicesUnskinned = intArray;
		this.verticesUnskinned = vertexPositionNormalTangentTextureSkinArray;
	}

	public SoftwareModelMesh(VertexPositionNormalTangentTexture[] vertexPositionNormalTangentTextureArray, int[] intArray) {
		this.indicesUnskinned = intArray;
		this.verticesUnskinned = new VertexPositionNormalTangentTextureSkin[vertexPositionNormalTangentTextureArray.length];
		for (int int1 = 0; int1 < vertexPositionNormalTangentTextureArray.length; ++int1) {
			VertexPositionNormalTangentTexture vertexPositionNormalTangentTexture = vertexPositionNormalTangentTextureArray[int1];
			this.verticesUnskinned[int1] = new VertexPositionNormalTangentTextureSkin();
			this.verticesUnskinned[int1].Position = new Vector3(vertexPositionNormalTangentTexture.Position.x(), vertexPositionNormalTangentTexture.Position.y(), vertexPositionNormalTangentTexture.Position.z());
			this.verticesUnskinned[int1].Normal = new Vector3(vertexPositionNormalTangentTexture.Normal.x(), vertexPositionNormalTangentTexture.Normal.y(), vertexPositionNormalTangentTexture.Normal.z());
			this.verticesUnskinned[int1].TextureCoordinates = new Vector2(vertexPositionNormalTangentTexture.TextureCoordinates.x, vertexPositionNormalTangentTexture.TextureCoordinates.y);
		}
	}
}
