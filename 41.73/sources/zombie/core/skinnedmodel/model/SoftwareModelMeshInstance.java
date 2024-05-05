package zombie.core.skinnedmodel.model;


public final class SoftwareModelMeshInstance {
	public SoftwareModelMesh softwareMesh;
	public VertexBufferObject vb;
	public String name;

	public SoftwareModelMeshInstance(String string, SoftwareModelMesh softwareModelMesh) {
		this.name = string;
		this.softwareMesh = softwareModelMesh;
		this.vb = new VertexBufferObject();
		this.vb.elements = softwareModelMesh.indicesUnskinned;
	}
}
