package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiMesh;
import org.joml.Vector3f;
import zombie.core.skinnedmodel.model.VertexBufferObject;


public final class ImportedStaticMesh {
	VertexBufferObject.VertexArray verticesUnskinned = null;
	int[] elements = null;
	final Vector3f minXYZ = new Vector3f(Float.MAX_VALUE);
	final Vector3f maxXYZ = new Vector3f(-3.4028235E38F);

	public ImportedStaticMesh(AiMesh aiMesh) {
		this.processAiScene(aiMesh);
	}

	private void processAiScene(AiMesh aiMesh) {
		int int1 = aiMesh.getNumVertices();
		int int2 = 0;
		for (int int3 = 0; int3 < 8; ++int3) {
			if (aiMesh.hasTexCoords(int3)) {
				++int2;
			}
		}

		VertexBufferObject.VertexFormat vertexFormat = new VertexBufferObject.VertexFormat(3 + int2);
		vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
		vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
		vertexFormat.setElement(2, VertexBufferObject.VertexType.TangentArray, 12);
		for (int int4 = 0; int4 < int2; ++int4) {
			vertexFormat.setElement(3 + int4, VertexBufferObject.VertexType.TextureCoordArray, 8);
		}

		vertexFormat.calculate();
		this.verticesUnskinned = new VertexBufferObject.VertexArray(vertexFormat, int1);
		Vector3f vector3f = new Vector3f();
		int int5;
		for (int5 = 0; int5 < int1; ++int5) {
			float float1 = aiMesh.getPositionX(int5);
			float float2 = aiMesh.getPositionY(int5);
			float float3 = aiMesh.getPositionZ(int5);
			this.minXYZ.min(vector3f.set(float1, float2, float3));
			this.maxXYZ.max(vector3f.set(float1, float2, float3));
			this.verticesUnskinned.setElement(int5, 0, aiMesh.getPositionX(int5), aiMesh.getPositionY(int5), aiMesh.getPositionZ(int5));
			if (aiMesh.hasNormals()) {
				this.verticesUnskinned.setElement(int5, 1, aiMesh.getNormalX(int5), aiMesh.getNormalY(int5), aiMesh.getNormalZ(int5));
			} else {
				this.verticesUnskinned.setElement(int5, 1, 0.0F, 1.0F, 0.0F);
			}

			if (aiMesh.hasTangentsAndBitangents()) {
				this.verticesUnskinned.setElement(int5, 2, aiMesh.getTangentX(int5), aiMesh.getTangentY(int5), aiMesh.getTangentZ(int5));
			} else {
				this.verticesUnskinned.setElement(int5, 2, 0.0F, 0.0F, 1.0F);
			}

			if (int2 > 0) {
				int int6 = 0;
				for (int int7 = 0; int7 < 8; ++int7) {
					if (aiMesh.hasTexCoords(int7)) {
						this.verticesUnskinned.setElement(int5, 3 + int6, aiMesh.getTexCoordU(int5, int7), 1.0F - aiMesh.getTexCoordV(int5, int7));
						++int6;
					}
				}
			}
		}

		int5 = aiMesh.getNumFaces();
		this.elements = new int[int5 * 3];
		for (int int8 = 0; int8 < int5; ++int8) {
			this.elements[int8 * 3 + 2] = aiMesh.getFaceVertex(int8, 0);
			this.elements[int8 * 3 + 1] = aiMesh.getFaceVertex(int8, 1);
			this.elements[int8 * 3 + 0] = aiMesh.getFaceVertex(int8, 2);
		}
	}
}
