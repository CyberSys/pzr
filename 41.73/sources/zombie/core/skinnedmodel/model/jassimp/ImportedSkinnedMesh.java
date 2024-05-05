package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiBone;
import jassimp.AiBoneWeight;
import jassimp.AiMesh;
import java.util.List;
import zombie.core.skinnedmodel.model.VertexBufferObject;


public final class ImportedSkinnedMesh {
	final ImportedSkeleton skeleton;
	String name;
	VertexBufferObject.VertexArray vertices = null;
	int[] elements = null;

	public ImportedSkinnedMesh(ImportedSkeleton importedSkeleton, AiMesh aiMesh) {
		this.skeleton = importedSkeleton;
		this.processAiScene(aiMesh);
	}

	private void processAiScene(AiMesh aiMesh) {
		this.name = aiMesh.getName();
		int int1 = aiMesh.getNumVertices();
		int int2 = int1 * 4;
		int[] intArray = new int[int2];
		float[] floatArray = new float[int2];
		for (int int3 = 0; int3 < int2; ++int3) {
			floatArray[int3] = 0.0F;
		}

		List list = aiMesh.getBones();
		int int4 = list.size();
		int int5;
		int int6;
		for (int5 = 0; int5 < int4; ++int5) {
			AiBone aiBone = (AiBone)list.get(int5);
			String string = aiBone.getName();
			int6 = (Integer)this.skeleton.boneIndices.get(string);
			List list2 = aiBone.getBoneWeights();
			for (int int7 = 0; int7 < aiBone.getNumWeights(); ++int7) {
				AiBoneWeight aiBoneWeight = (AiBoneWeight)list2.get(int7);
				int int8 = aiBoneWeight.getVertexId() * 4;
				for (int int9 = 0; int9 < 4; ++int9) {
					if (floatArray[int8 + int9] == 0.0F) {
						floatArray[int8 + int9] = aiBoneWeight.getWeight();
						intArray[int8 + int9] = int6;
						break;
					}
				}
			}
		}

		int5 = getNumUVs(aiMesh);
		VertexBufferObject.VertexFormat vertexFormat = new VertexBufferObject.VertexFormat(5 + int5);
		vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
		vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
		vertexFormat.setElement(2, VertexBufferObject.VertexType.TangentArray, 12);
		vertexFormat.setElement(3, VertexBufferObject.VertexType.BlendWeightArray, 16);
		vertexFormat.setElement(4, VertexBufferObject.VertexType.BlendIndexArray, 16);
		int int10;
		for (int10 = 0; int10 < int5; ++int10) {
			vertexFormat.setElement(5 + int10, VertexBufferObject.VertexType.TextureCoordArray, 8);
		}

		vertexFormat.calculate();
		this.vertices = new VertexBufferObject.VertexArray(vertexFormat, int1);
		for (int10 = 0; int10 < int1; ++int10) {
			this.vertices.setElement(int10, 0, aiMesh.getPositionX(int10), aiMesh.getPositionY(int10), aiMesh.getPositionZ(int10));
			if (aiMesh.hasNormals()) {
				this.vertices.setElement(int10, 1, aiMesh.getNormalX(int10), aiMesh.getNormalY(int10), aiMesh.getNormalZ(int10));
			} else {
				this.vertices.setElement(int10, 1, 0.0F, 1.0F, 0.0F);
			}

			if (aiMesh.hasTangentsAndBitangents()) {
				this.vertices.setElement(int10, 2, aiMesh.getTangentX(int10), aiMesh.getTangentY(int10), aiMesh.getTangentZ(int10));
			} else {
				this.vertices.setElement(int10, 2, 0.0F, 0.0F, 1.0F);
			}

			this.vertices.setElement(int10, 3, floatArray[int10 * 4], floatArray[int10 * 4 + 1], floatArray[int10 * 4 + 2], floatArray[int10 * 4 + 3]);
			this.vertices.setElement(int10, 4, (float)intArray[int10 * 4], (float)intArray[int10 * 4 + 1], (float)intArray[int10 * 4 + 2], (float)intArray[int10 * 4 + 3]);
			if (int5 > 0) {
				int6 = 0;
				for (int int11 = 0; int11 < 8; ++int11) {
					if (aiMesh.hasTexCoords(int11)) {
						this.vertices.setElement(int10, 5 + int6, aiMesh.getTexCoordU(int10, int11), 1.0F - aiMesh.getTexCoordV(int10, int11));
						++int6;
					}
				}
			}
		}

		int10 = aiMesh.getNumFaces();
		this.elements = new int[int10 * 3];
		for (int6 = 0; int6 < int10; ++int6) {
			this.elements[int6 * 3 + 2] = aiMesh.getFaceVertex(int6, 0);
			this.elements[int6 * 3 + 1] = aiMesh.getFaceVertex(int6, 1);
			this.elements[int6 * 3 + 0] = aiMesh.getFaceVertex(int6, 2);
		}
	}

	private static int getNumUVs(AiMesh aiMesh) {
		int int1 = 0;
		for (int int2 = 0; int2 < 8; ++int2) {
			if (aiMesh.hasTexCoords(int2)) {
				++int1;
			}
		}

		return int1;
	}
}
