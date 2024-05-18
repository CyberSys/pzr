package zombie.core.skinnedmodel.model;

import zombie.core.skinnedmodel.shader.Shader;


public class ModelMesh {
	VertexBufferObject vb;
	public String Texture;

	public void SetVertexBuffer(VertexBufferObject vertexBufferObject) {
		this.vb = vertexBufferObject;
	}

	public void Draw(Shader shader) {
		if (this.vb != null) {
			this.vb.Draw(shader);
		}
	}
}
