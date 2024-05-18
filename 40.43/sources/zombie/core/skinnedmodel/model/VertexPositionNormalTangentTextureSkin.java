package zombie.core.skinnedmodel.model;

import java.nio.ByteBuffer;
import zombie.core.skinnedmodel.Vector3;
import zombie.core.skinnedmodel.Vector4;
import zombie.iso.Vector2;


class VertexPositionNormalTangentTextureSkin {
	public Vector3 Position;
	public Vector3 Normal;
	public Vector3 Tangent;
	public Vector2 TextureCoordinates;
	public Vector4 BlendWeights;
	public UInt4 BlendIndices;

	public VertexPositionNormalTangentTextureSkin(Vector3 vector3, Vector3 vector32, Vector3 vector33, Vector2 vector2, Vector4 vector4, UInt4 uInt4) {
		this.Position = vector3;
		this.Normal = vector32;
		this.Tangent = vector33;
		this.TextureCoordinates = vector2;
		this.BlendWeights = vector4;
		this.BlendIndices = uInt4;
	}

	public void put(ByteBuffer byteBuffer) {
		byteBuffer.putFloat(this.Position.x());
		byteBuffer.putFloat(this.Position.y());
		byteBuffer.putFloat(this.Position.z());
		byteBuffer.putFloat(this.Normal.x());
		byteBuffer.putFloat(this.Normal.y());
		byteBuffer.putFloat(this.Normal.z());
		byteBuffer.putFloat(this.Tangent.x());
		byteBuffer.putFloat(this.Tangent.y());
		byteBuffer.putFloat(this.Tangent.z());
		byteBuffer.putFloat(this.TextureCoordinates.x);
		byteBuffer.putFloat(this.TextureCoordinates.y);
		byteBuffer.putFloat(this.BlendWeights.x);
		byteBuffer.putFloat(this.BlendWeights.y);
		byteBuffer.putFloat(this.BlendWeights.z);
		byteBuffer.putFloat(this.BlendWeights.w);
		byteBuffer.putFloat((float)this.BlendIndices.X);
		byteBuffer.putFloat((float)this.BlendIndices.Y);
		byteBuffer.putFloat((float)this.BlendIndices.Z);
		byteBuffer.putFloat((float)this.BlendIndices.W);
	}
}
