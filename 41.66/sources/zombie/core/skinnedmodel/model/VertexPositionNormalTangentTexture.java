package zombie.core.skinnedmodel.model;

import java.nio.ByteBuffer;
import zombie.core.skinnedmodel.Vector3;
import zombie.iso.Vector2;


public final class VertexPositionNormalTangentTexture {
	public Vector3 Position;
	public Vector3 Normal;
	public Vector3 Tangent;
	public Vector2 TextureCoordinates;

	public VertexPositionNormalTangentTexture(Vector3 vector3, Vector3 vector32, Vector3 vector33, Vector2 vector2) {
		this.Position = vector3;
		this.Normal = vector32;
		this.Tangent = vector33;
		this.TextureCoordinates = vector2;
	}

	public VertexPositionNormalTangentTexture() {
		this.Position = new Vector3(0.0F, 0.0F, 0.0F);
		this.Normal = new Vector3(0.0F, 0.0F, 1.0F);
		this.Tangent = new Vector3(0.0F, 1.0F, 0.0F);
		this.TextureCoordinates = new Vector2(0.0F, 0.0F);
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
	}
}
