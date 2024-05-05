package zombie.core.skinnedmodel.model.jassimp;

import gnu.trove.map.hash.TObjectIntHashMap;
import jassimp.AiAnimation;
import jassimp.AiBone;
import jassimp.AiBuiltInWrapperProvider;
import jassimp.AiMaterial;
import jassimp.AiMatrix4f;
import jassimp.AiMesh;
import jassimp.AiNode;
import jassimp.AiNodeAnim;
import jassimp.AiScene;
import jassimp.Jassimp;
import jassimp.JassimpLibraryLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.Core;
import zombie.core.skinnedmodel.model.VertexPositionNormalTangentTexture;
import zombie.core.skinnedmodel.model.VertexPositionNormalTangentTextureSkin;
import zombie.util.SharedStrings;
import zombie.util.list.PZArrayUtil;


public final class JAssImpImporter {
	private static final TObjectIntHashMap sharedStringCounts = new TObjectIntHashMap();
	private static final SharedStrings sharedStrings = new SharedStrings();
	private static final HashMap tempHashMap = new HashMap();

	public static void Init() {
		Jassimp.setLibraryLoader(new JAssImpImporter.LibraryLoader());
	}

	static AiNode FindNode(String string, AiNode aiNode) {
		List list = aiNode.getChildren();
		for (int int1 = 0; int1 < list.size(); ++int1) {
			AiNode aiNode2 = (AiNode)list.get(int1);
			if (aiNode2.getName().equals(string)) {
				return aiNode2;
			}

			AiNode aiNode3 = FindNode(string, aiNode2);
			if (aiNode3 != null) {
				return aiNode3;
			}
		}

		return null;
	}

	static Matrix4f getMatrixFromAiMatrix(AiMatrix4f aiMatrix4f) {
		return getMatrixFromAiMatrix(aiMatrix4f, new Matrix4f());
	}

	static Matrix4f getMatrixFromAiMatrix(AiMatrix4f aiMatrix4f, Matrix4f matrix4f) {
		matrix4f.m00 = aiMatrix4f.get(0, 0);
		matrix4f.m01 = aiMatrix4f.get(0, 1);
		matrix4f.m02 = aiMatrix4f.get(0, 2);
		matrix4f.m03 = aiMatrix4f.get(0, 3);
		matrix4f.m10 = aiMatrix4f.get(1, 0);
		matrix4f.m11 = aiMatrix4f.get(1, 1);
		matrix4f.m12 = aiMatrix4f.get(1, 2);
		matrix4f.m13 = aiMatrix4f.get(1, 3);
		matrix4f.m20 = aiMatrix4f.get(2, 0);
		matrix4f.m21 = aiMatrix4f.get(2, 1);
		matrix4f.m22 = aiMatrix4f.get(2, 2);
		matrix4f.m23 = aiMatrix4f.get(2, 3);
		matrix4f.m30 = aiMatrix4f.get(3, 0);
		matrix4f.m31 = aiMatrix4f.get(3, 1);
		matrix4f.m32 = aiMatrix4f.get(3, 2);
		matrix4f.m33 = aiMatrix4f.get(3, 3);
		return matrix4f;
	}

	static void CollectBoneNodes(ArrayList arrayList, AiNode aiNode) {
		arrayList.add(aiNode);
		for (int int1 = 0; int1 < aiNode.getNumChildren(); ++int1) {
			CollectBoneNodes(arrayList, (AiNode)aiNode.getChildren().get(int1));
		}
	}

	static String DumpAiMatrix(AiMatrix4f aiMatrix4f) {
		String string = "";
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(0, 0));
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(0, 1));
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(0, 2));
		string = string + String.format("%1$.8f\n ", aiMatrix4f.get(0, 3));
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(1, 0));
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(1, 1));
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(1, 2));
		string = string + String.format("%1$.8f\n ", aiMatrix4f.get(1, 3));
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(2, 0));
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(2, 1));
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(2, 2));
		string = string + String.format("%1$.8f\n ", aiMatrix4f.get(2, 3));
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(3, 0));
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(3, 1));
		string = string + String.format("%1$.8f, ", aiMatrix4f.get(3, 2));
		string = string + String.format("%1$.8f\n ", aiMatrix4f.get(3, 3));
		return string;
	}

	static String DumpMatrix(Matrix4f matrix4f) {
		String string = "";
		string = string + String.format("%1$.8f, ", matrix4f.m00);
		string = string + String.format("%1$.8f, ", matrix4f.m01);
		string = string + String.format("%1$.8f, ", matrix4f.m02);
		string = string + String.format("%1$.8f\n ", matrix4f.m03);
		string = string + String.format("%1$.8f, ", matrix4f.m10);
		string = string + String.format("%1$.8f, ", matrix4f.m11);
		string = string + String.format("%1$.8f, ", matrix4f.m12);
		string = string + String.format("%1$.8f\n ", matrix4f.m13);
		string = string + String.format("%1$.8f, ", matrix4f.m20);
		string = string + String.format("%1$.8f, ", matrix4f.m21);
		string = string + String.format("%1$.8f, ", matrix4f.m22);
		string = string + String.format("%1$.8f\n ", matrix4f.m23);
		string = string + String.format("%1$.8f, ", matrix4f.m30);
		string = string + String.format("%1$.8f, ", matrix4f.m31);
		string = string + String.format("%1$.8f, ", matrix4f.m32);
		string = string + String.format("%1$.8f\n ", matrix4f.m33);
		return string;
	}

	static AiBone FindAiBone(String string, List list) {
		int int1 = list.size();
		for (int int2 = 0; int2 < int1; ++int2) {
			AiBone aiBone = (AiBone)list.get(int2);
			String string2 = aiBone.getName();
			if (string2.equals(string)) {
				return aiBone;
			}
		}

		return null;
	}

	private static void DumpMesh(VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray) {
		StringBuilder stringBuilder = new StringBuilder();
		VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray2 = vertexPositionNormalTangentTextureSkinArray;
		int int1 = vertexPositionNormalTangentTextureSkinArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin = vertexPositionNormalTangentTextureSkinArray2[int2];
			stringBuilder.append(vertexPositionNormalTangentTextureSkin.Position.x()).append('\t').append(vertexPositionNormalTangentTextureSkin.Position.y()).append('\t').append(vertexPositionNormalTangentTextureSkin.Position.z()).append('\t').append('\n');
		}

		String string = stringBuilder.toString();
		vertexPositionNormalTangentTextureSkinArray2 = null;
	}

	static Vector3f GetKeyFramePosition(AiNodeAnim aiNodeAnim, float float1) {
		int int1 = -1;
		float float2;
		for (int int2 = 0; int2 < aiNodeAnim.getNumPosKeys(); ++int2) {
			float2 = (float)aiNodeAnim.getPosKeyTime(int2);
			if (float2 > float1) {
				break;
			}

			int1 = int2;
			if (float2 == float1) {
				return new Vector3f(aiNodeAnim.getPosKeyX(int2), aiNodeAnim.getPosKeyY(int2), aiNodeAnim.getPosKeyZ(int2));
			}
		}

		if (int1 < 0) {
			return new Vector3f();
		} else if (aiNodeAnim.getNumPosKeys() > int1) {
			float float3 = (float)aiNodeAnim.getPosKeyTime(int1);
			float2 = (float)aiNodeAnim.getPosKeyTime(int1 + 1);
			float float4 = float2 - float3;
			float float5 = float1 - float3;
			float5 /= float4;
			float float6 = aiNodeAnim.getPosKeyX(int1);
			float float7 = aiNodeAnim.getPosKeyX(int1 + 1);
			float float8 = float6 + float5 * (float7 - float6);
			float float9 = aiNodeAnim.getPosKeyY(int1);
			float float10 = aiNodeAnim.getPosKeyY(int1 + 1);
			float float11 = float9 + float5 * (float10 - float9);
			float float12 = aiNodeAnim.getPosKeyZ(int1);
			float float13 = aiNodeAnim.getPosKeyZ(int1 + 1);
			float float14 = float12 + float5 * (float13 - float12);
			return new Vector3f(float8, float11, float14);
		} else {
			return new Vector3f(aiNodeAnim.getPosKeyX(int1), aiNodeAnim.getPosKeyY(int1), aiNodeAnim.getPosKeyZ(int1));
		}
	}

	static Quaternion GetKeyFrameRotation(AiNodeAnim aiNodeAnim, float float1) {
		boolean boolean1 = false;
		Quaternion quaternion = new Quaternion();
		int int1 = -1;
		float float2;
		for (int int2 = 0; int2 < aiNodeAnim.getNumRotKeys(); ++int2) {
			float2 = (float)aiNodeAnim.getRotKeyTime(int2);
			if (float2 > float1) {
				break;
			}

			int1 = int2;
			if (float2 == float1) {
				quaternion.set(aiNodeAnim.getRotKeyX(int2), aiNodeAnim.getRotKeyY(int2), aiNodeAnim.getRotKeyZ(int2), aiNodeAnim.getRotKeyW(int2));
				boolean1 = true;
				break;
			}
		}

		if (!boolean1 && int1 < 0) {
			return new Quaternion();
		} else {
			if (!boolean1 && aiNodeAnim.getNumRotKeys() > int1 + 1) {
				float float3 = (float)aiNodeAnim.getRotKeyTime(int1);
				float2 = (float)aiNodeAnim.getRotKeyTime(int1 + 1);
				float float4 = float2 - float3;
				float float5 = float1 - float3;
				float5 /= float4;
				float float6 = aiNodeAnim.getRotKeyX(int1);
				float float7 = aiNodeAnim.getRotKeyX(int1 + 1);
				float float8 = float6 + float5 * (float7 - float6);
				float float9 = aiNodeAnim.getRotKeyY(int1);
				float float10 = aiNodeAnim.getRotKeyY(int1 + 1);
				float float11 = float9 + float5 * (float10 - float9);
				float float12 = aiNodeAnim.getRotKeyZ(int1);
				float float13 = aiNodeAnim.getRotKeyZ(int1 + 1);
				float float14 = float12 + float5 * (float13 - float12);
				float float15 = aiNodeAnim.getRotKeyW(int1);
				float float16 = aiNodeAnim.getRotKeyW(int1 + 1);
				float float17 = float15 + float5 * (float16 - float15);
				quaternion.set(float8, float11, float14, float17);
				boolean1 = true;
			}

			if (!boolean1 && aiNodeAnim.getNumRotKeys() > int1) {
				quaternion.set(aiNodeAnim.getRotKeyX(int1), aiNodeAnim.getRotKeyY(int1), aiNodeAnim.getRotKeyZ(int1), aiNodeAnim.getRotKeyW(int1));
				boolean1 = true;
			}

			return quaternion;
		}
	}

	static Vector3f GetKeyFrameScale(AiNodeAnim aiNodeAnim, float float1) {
		int int1 = -1;
		float float2;
		for (int int2 = 0; int2 < aiNodeAnim.getNumScaleKeys(); ++int2) {
			float2 = (float)aiNodeAnim.getScaleKeyTime(int2);
			if (float2 > float1) {
				break;
			}

			int1 = int2;
			if (float2 == float1) {
				return new Vector3f(aiNodeAnim.getScaleKeyX(int2), aiNodeAnim.getScaleKeyY(int2), aiNodeAnim.getScaleKeyZ(int2));
			}
		}

		if (int1 < 0) {
			return new Vector3f(1.0F, 1.0F, 1.0F);
		} else if (aiNodeAnim.getNumScaleKeys() > int1) {
			float float3 = (float)aiNodeAnim.getScaleKeyTime(int1);
			float2 = (float)aiNodeAnim.getScaleKeyTime(int1 + 1);
			float float4 = float2 - float3;
			float float5 = float1 - float3;
			float5 /= float4;
			float float6 = aiNodeAnim.getScaleKeyX(int1);
			float float7 = aiNodeAnim.getScaleKeyX(int1 + 1);
			float float8 = float6 + float5 * (float7 - float6);
			float float9 = aiNodeAnim.getScaleKeyY(int1);
			float float10 = aiNodeAnim.getScaleKeyY(int1 + 1);
			float float11 = float9 + float5 * (float10 - float9);
			float float12 = aiNodeAnim.getScaleKeyZ(int1);
			float float13 = aiNodeAnim.getScaleKeyZ(int1 + 1);
			float float14 = float12 + float5 * (float13 - float12);
			return new Vector3f(float8, float11, float14);
		} else {
			return new Vector3f(aiNodeAnim.getScaleKeyX(int1), aiNodeAnim.getScaleKeyY(int1), aiNodeAnim.getScaleKeyZ(int1));
		}
	}

	static void replaceHashMapKeys(HashMap hashMap, String string) {
		tempHashMap.clear();
		tempHashMap.putAll(hashMap);
		hashMap.clear();
		Iterator iterator = tempHashMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			String string2 = getSharedString((String)entry.getKey(), string);
			hashMap.put(string2, (Integer)entry.getValue());
		}

		tempHashMap.clear();
	}

	public static String getSharedString(String string, String string2) {
		String string3 = sharedStrings.get(string);
		if (Core.bDebug && string != string3) {
			sharedStringCounts.adjustOrPutValue(string2, 1, 0);
		}

		return string3;
	}

	private static void takeOutTheTrash(VertexPositionNormalTangentTexture[] vertexPositionNormalTangentTextureArray) {
		PZArrayUtil.forEach((Object[])vertexPositionNormalTangentTextureArray, JAssImpImporter::takeOutTheTrash);
		Arrays.fill(vertexPositionNormalTangentTextureArray, (Object)null);
	}

	private static void takeOutTheTrash(VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray) {
		PZArrayUtil.forEach((Object[])vertexPositionNormalTangentTextureSkinArray, JAssImpImporter::takeOutTheTrash);
		Arrays.fill(vertexPositionNormalTangentTextureSkinArray, (Object)null);
	}

	private static void takeOutTheTrash(VertexPositionNormalTangentTexture vertexPositionNormalTangentTexture) {
		vertexPositionNormalTangentTexture.Normal = null;
		vertexPositionNormalTangentTexture.Position = null;
		vertexPositionNormalTangentTexture.TextureCoordinates = null;
		vertexPositionNormalTangentTexture.Tangent = null;
	}

	private static void takeOutTheTrash(VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin) {
		vertexPositionNormalTangentTextureSkin.Normal = null;
		vertexPositionNormalTangentTextureSkin.Position = null;
		vertexPositionNormalTangentTextureSkin.TextureCoordinates = null;
		vertexPositionNormalTangentTextureSkin.Tangent = null;
		vertexPositionNormalTangentTextureSkin.BlendWeights = null;
		vertexPositionNormalTangentTextureSkin.BlendIndices = null;
	}

	public static void takeOutTheTrash(AiScene aiScene) {
		Iterator iterator = aiScene.getAnimations().iterator();
		while (iterator.hasNext()) {
			AiAnimation aiAnimation = (AiAnimation)iterator.next();
			aiAnimation.getChannels().clear();
		}

		aiScene.getAnimations().clear();
		aiScene.getCameras().clear();
		aiScene.getLights().clear();
		iterator = aiScene.getMaterials().iterator();
		while (iterator.hasNext()) {
			AiMaterial aiMaterial = (AiMaterial)iterator.next();
			aiMaterial.getProperties().clear();
		}

		aiScene.getMaterials().clear();
		iterator = aiScene.getMeshes().iterator();
		while (iterator.hasNext()) {
			AiMesh aiMesh = (AiMesh)iterator.next();
			Iterator iterator2 = aiMesh.getBones().iterator();
			while (iterator2.hasNext()) {
				AiBone aiBone = (AiBone)iterator2.next();
				aiBone.getBoneWeights().clear();
			}

			aiMesh.getBones().clear();
		}

		aiScene.getMeshes().clear();
		AiNode aiNode = (AiNode)aiScene.getSceneRoot(new AiBuiltInWrapperProvider());
		takeOutTheTrash(aiNode);
	}

	private static void takeOutTheTrash(AiNode aiNode) {
		Iterator iterator = aiNode.getChildren().iterator();
		while (iterator.hasNext()) {
			AiNode aiNode2 = (AiNode)iterator.next();
			takeOutTheTrash(aiNode2);
		}

		aiNode.getChildren().clear();
	}

	private static class LibraryLoader extends JassimpLibraryLoader {

		public void loadLibrary() {
			if (System.getProperty("os.name").contains("OS X")) {
				System.loadLibrary("jassimp");
			} else if (System.getProperty("os.name").startsWith("Win")) {
				if (System.getProperty("sun.arch.data.model").equals("64")) {
					System.loadLibrary("jassimp64");
				} else {
					System.loadLibrary("jassimp32");
				}
			} else if (System.getProperty("sun.arch.data.model").equals("64")) {
				System.loadLibrary("jassimp64");
			} else {
				System.loadLibrary("jassimp32");
			}
		}
	}

	public static enum LoadMode {

		Normal,
		StaticMesh,
		AnimationOnly;

		private static JAssImpImporter.LoadMode[] $values() {
			return new JAssImpImporter.LoadMode[]{Normal, StaticMesh, AnimationOnly};
		}
	}
}
