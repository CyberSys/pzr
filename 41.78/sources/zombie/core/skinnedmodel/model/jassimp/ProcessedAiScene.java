package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiBuiltInWrapperProvider;
import jassimp.AiMatrix4f;
import jassimp.AiMesh;
import jassimp.AiNode;
import jassimp.AiScene;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.joml.Vector3fc;
import org.lwjgl.util.vector.Matrix4f;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.model.VertexBufferObject;
import zombie.debug.DebugLog;
import zombie.util.StringUtils;


public final class ProcessedAiScene {
	private ImportedSkeleton skeleton;
	private ImportedSkinnedMesh skinnedMesh;
	private ImportedStaticMesh staticMesh;
	private Matrix4f transform = null;

	private ProcessedAiScene() {
	}

	public static ProcessedAiScene process(ProcessedAiSceneParams processedAiSceneParams) {
		ProcessedAiScene processedAiScene = new ProcessedAiScene();
		processedAiScene.processAiScene(processedAiSceneParams);
		return processedAiScene;
	}

	private void processAiScene(ProcessedAiSceneParams processedAiSceneParams) {
		AiScene aiScene = processedAiSceneParams.scene;
		JAssImpImporter.LoadMode loadMode = processedAiSceneParams.mode;
		String string = processedAiSceneParams.meshName;
		AiMesh aiMesh = this.findMesh(aiScene, string);
		if (aiMesh == null) {
			DebugLog.General.error("No such mesh \"%s\"", string);
		} else {
			if (loadMode != JAssImpImporter.LoadMode.StaticMesh && aiMesh.hasBones()) {
				ImportedSkeletonParams importedSkeletonParams = ImportedSkeletonParams.create(processedAiSceneParams, aiMesh);
				this.skeleton = ImportedSkeleton.process(importedSkeletonParams);
				if (loadMode != JAssImpImporter.LoadMode.AnimationOnly) {
					this.skinnedMesh = new ImportedSkinnedMesh(this.skeleton, aiMesh);
				}
			} else {
				this.staticMesh = new ImportedStaticMesh(aiMesh);
			}

			if (this.staticMesh != null || this.skinnedMesh != null) {
				AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
				AiNode aiNode = (AiNode)aiScene.getSceneRoot(aiBuiltInWrapperProvider);
				AiNode aiNode2 = this.findParentNodeForMesh(aiScene.getMeshes().indexOf(aiMesh), aiNode);
				if (aiNode2 != null) {
					this.transform = JAssImpImporter.getMatrixFromAiMatrix((AiMatrix4f)aiNode2.getTransform(aiBuiltInWrapperProvider));
					for (AiNode aiNode3 = aiNode2.getParent(); aiNode3 != null; aiNode3 = aiNode3.getParent()) {
						Matrix4f matrix4f = JAssImpImporter.getMatrixFromAiMatrix((AiMatrix4f)aiNode3.getTransform(aiBuiltInWrapperProvider));
						Matrix4f.mul(matrix4f, this.transform, this.transform);
					}
				}
			}
		}
	}

	private AiMesh findMesh(AiScene aiScene, String string) {
		if (aiScene.getNumMeshes() == 0) {
			return null;
		} else {
			Iterator iterator;
			AiMesh aiMesh;
			if (StringUtils.isNullOrWhitespace(string)) {
				iterator = aiScene.getMeshes().iterator();
				do {
					if (!iterator.hasNext()) {
						return (AiMesh)aiScene.getMeshes().get(0);
					}

					aiMesh = (AiMesh)iterator.next();
				}		 while (!aiMesh.hasBones());

				return aiMesh;
			} else {
				iterator = aiScene.getMeshes().iterator();
				do {
					if (!iterator.hasNext()) {
						AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
						AiNode aiNode = (AiNode)aiScene.getSceneRoot(aiBuiltInWrapperProvider);
						AiNode aiNode2 = JAssImpImporter.FindNode(string, aiNode);
						if (aiNode2 != null && aiNode2.getNumMeshes() == 1) {
							int int1 = aiNode2.getMeshes()[0];
							return (AiMesh)aiScene.getMeshes().get(int1);
						}

						return null;
					}

					aiMesh = (AiMesh)iterator.next();
				}		 while (!aiMesh.getName().equalsIgnoreCase(string));

				return aiMesh;
			}
		}
	}

	private AiNode findParentNodeForMesh(int int1, AiNode aiNode) {
		for (int int2 = 0; int2 < aiNode.getNumMeshes(); ++int2) {
			if (aiNode.getMeshes()[int2] == int1) {
				return aiNode;
			}
		}

		Iterator iterator = aiNode.getChildren().iterator();
		AiNode aiNode2;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			AiNode aiNode3 = (AiNode)iterator.next();
			aiNode2 = this.findParentNodeForMesh(int1, aiNode3);
		} while (aiNode2 == null);

		return aiNode2;
	}

	public void applyToMesh(ModelMesh modelMesh, JAssImpImporter.LoadMode loadMode, boolean boolean1, SkinningData skinningData) {
		modelMesh.m_transform = null;
		if (this.transform != null) {
			modelMesh.m_transform = PZMath.convertMatrix(this.transform, new org.joml.Matrix4f());
		}

		VertexBufferObject.VertexArray vertexArray;
		int[] intArray;
		if (this.staticMesh != null && !ModelManager.NoOpenGL) {
			modelMesh.minXYZ.set((Vector3fc)this.staticMesh.minXYZ);
			modelMesh.maxXYZ.set((Vector3fc)this.staticMesh.maxXYZ);
			modelMesh.m_bHasVBO = true;
			vertexArray = this.staticMesh.verticesUnskinned;
			intArray = this.staticMesh.elements;
			RenderThread.queueInvokeOnRenderContext(()->{
				modelMesh.SetVertexBuffer(new VertexBufferObject(vertexArray, intArray));
				if (ModelManager.instance.bCreateSoftwareMeshes) {
					modelMesh.softwareMesh.vb = modelMesh.vb;
				}
			});
		}

		if (modelMesh.skinningData != null) {
			if (skinningData == null || modelMesh.skinningData.AnimationClips != skinningData.AnimationClips) {
				modelMesh.skinningData.AnimationClips.clear();
			}

			modelMesh.skinningData.InverseBindPose.clear();
			modelMesh.skinningData.BindPose.clear();
			modelMesh.skinningData.BoneOffset.clear();
			modelMesh.skinningData.BoneIndices.clear();
			modelMesh.skinningData.SkeletonHierarchy.clear();
			modelMesh.skinningData = null;
		}

		if (this.skeleton != null) {
			ImportedSkeleton importedSkeleton = this.skeleton;
			HashMap hashMap = importedSkeleton.clips;
			if (skinningData != null) {
				importedSkeleton.clips.clear();
				hashMap = skinningData.AnimationClips;
			}

			JAssImpImporter.replaceHashMapKeys(importedSkeleton.boneIndices, "SkinningData.boneIndices");
			modelMesh.skinningData = new SkinningData(hashMap, importedSkeleton.bindPose, importedSkeleton.invBindPose, importedSkeleton.skinOffsetMatrices, importedSkeleton.SkeletonHierarchy, importedSkeleton.boneIndices);
		}

		if (this.skinnedMesh != null && !ModelManager.NoOpenGL) {
			modelMesh.m_bHasVBO = true;
			vertexArray = this.skinnedMesh.vertices;
			intArray = this.skinnedMesh.elements;
			RenderThread.queueInvokeOnRenderContext(()->{
				modelMesh.SetVertexBuffer(new VertexBufferObject(vertexArray, intArray, boolean1));
				if (ModelManager.instance.bCreateSoftwareMeshes) {
					modelMesh.softwareMesh.vb = modelMesh.vb;
				}
			});
		}

		this.skeleton = null;
		this.skinnedMesh = null;
		this.staticMesh = null;
	}

	public void applyToAnimation(AnimationAsset animationAsset) {
		Iterator iterator = this.skeleton.clips.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			Keyframe[] keyframeArray = ((AnimationClip)entry.getValue()).getKeyframes();
			int int1 = keyframeArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				Keyframe keyframe = keyframeArray[int2];
				keyframe.BoneName = JAssImpImporter.getSharedString(keyframe.BoneName, "Keyframe.BoneName");
			}
		}

		animationAsset.AnimationClips = this.skeleton.clips;
		this.skeleton = null;
	}
}
