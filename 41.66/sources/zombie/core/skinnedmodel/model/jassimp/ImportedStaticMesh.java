package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiMesh;
import zombie.core.skinnedmodel.model.VertexBufferObject;


public final class ImportedStaticMesh {
	VertexBufferObject.VertexArray verticesUnskinned = null;
	int[] elements = null;

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
		int int4;
		for (int4 = 0; int4 < int2; ++int4) {
			vertexFormat.setElement(3 + int4, VertexBufferObject.VertexType.TextureCoordArray, 8);
		}

		vertexFormat.calculate();
		this.verticesUnskinned = new VertexBufferObject.VertexArray(vertexFormat, int1);
		int int5;
		for (int4 = 0; int4 < int1; ++int4) {
			this.verticesUnskinned.setElement(int4, 0, aiMesh.getPositionX(int4), aiMesh.getPositionY(int4), aiMesh.getPositionZ(int4));
			if (aiMesh.hasNormals()) {
				this.verticesUnskinned.setElement(int4, 1, aiMesh.getNormalX(int4), aiMesh.getNormalY(int4), aiMesh.getNormalZ(int4));
			} else {
				this.verticesUnskinned.setElement(int4, 1, 0.0F, 1.0F, 0.0F);
			}

			if (aiMesh.hasTangentsAndBitangents()) {
				this.verticesUnskinned.setElement(int4, 2, aiMesh.getTangentX(int4), aiMesh.getTangentY(int4), aiMesh.getTangentZ(int4));
			} else {
				this.verticesUnskinned.setElement(int4, 2, 0.0F, 0.0F, 1.0F);
			}

			if (int2 > 0) {
				int5 = 0;
				for (int int6 = 0; int6 < 8; ++int6) {
					if (aiMesh.hasTexCoords(int6)) {
						this.verticesUnskinned.setElement(int4, 3 + int5, aiMesh.getTexCoordU(int4, int6), 1.0F - aiMesh.getTexCoordV(int4, int6));
						++int5;
					}
				}
			}
		}

		int4 = aiMesh.getNumFaces();
		this.elements = new int[int4 * 3];
		for (int5 = 0; int5 < int4; ++int5) {
			this.elements[int5 * 3 + 2] = aiMesh.getFaceVertex(int5, 0);
			this.elements[int5 * 3 + 1] = aiMesh.getFaceVertex(int5, 1);
			this.elements[int5 * 3 + 0] = aiMesh.getFaceVertex(int5, 2);
		}
	}
}
