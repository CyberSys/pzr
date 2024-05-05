package zombie.core.skinnedmodel.model;

import jassimp.AiPostProcessSteps;
import jassimp.AiScene;
import jassimp.Jassimp;
import java.io.IOException;
import java.util.EnumSet;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiSceneParams;
import zombie.debug.DebugLog;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.IFileTaskCallback;


public class FileTask_LoadMesh extends FileTask_AbstractLoadModel {
	ModelMesh mesh;

	public FileTask_LoadMesh(ModelMesh modelMesh, FileSystem fileSystem, IFileTaskCallback iFileTaskCallback) {
		super(fileSystem, iFileTaskCallback, "media/models", "media/models_x");
		this.mesh = modelMesh;
	}

	public String getErrorMessage() {
		return this.m_fileName;
	}

	public void done() {
		MeshAssetManager.instance.addWatchedFile(this.m_fileName);
		this.mesh.m_fullPath = this.m_fileName;
		this.m_fileName = null;
		this.mesh = null;
	}

	public String getRawFileName() {
		String string = this.mesh.getPath().getPath();
		int int1 = string.indexOf(124);
		return int1 != -1 ? string.substring(0, int1) : string;
	}

	private String getMeshName() {
		String string = this.mesh.getPath().getPath();
		int int1 = string.indexOf(124);
		return int1 != -1 ? string.substring(int1 + 1) : null;
	}

	public ProcessedAiScene loadX() throws IOException {
		EnumSet enumSet = EnumSet.of(AiPostProcessSteps.FIND_INSTANCES, AiPostProcessSteps.MAKE_LEFT_HANDED, AiPostProcessSteps.LIMIT_BONE_WEIGHTS, AiPostProcessSteps.TRIANGULATE, AiPostProcessSteps.OPTIMIZE_MESHES, AiPostProcessSteps.REMOVE_REDUNDANT_MATERIALS, AiPostProcessSteps.JOIN_IDENTICAL_VERTICES);
		AiScene aiScene = Jassimp.importFile(this.m_fileName, enumSet);
		JAssImpImporter.LoadMode loadMode = this.mesh.assetParams.bStatic ? JAssImpImporter.LoadMode.StaticMesh : JAssImpImporter.LoadMode.Normal;
		ModelMesh modelMesh = this.mesh.assetParams.animationsMesh;
		SkinningData skinningData = modelMesh == null ? null : modelMesh.skinningData;
		ProcessedAiSceneParams processedAiSceneParams = ProcessedAiSceneParams.create();
		processedAiSceneParams.scene = aiScene;
		processedAiSceneParams.mode = loadMode;
		processedAiSceneParams.skinnedTo = skinningData;
		processedAiSceneParams.meshName = this.getMeshName();
		ProcessedAiScene processedAiScene = ProcessedAiScene.process(processedAiSceneParams);
		JAssImpImporter.takeOutTheTrash(aiScene);
		return processedAiScene;
	}

	public ProcessedAiScene loadFBX() throws IOException {
		DebugLog.Animation.debugln("Loading: %s", this.m_fileName);
		EnumSet enumSet = EnumSet.of(AiPostProcessSteps.FIND_INSTANCES, AiPostProcessSteps.MAKE_LEFT_HANDED, AiPostProcessSteps.LIMIT_BONE_WEIGHTS, AiPostProcessSteps.TRIANGULATE, AiPostProcessSteps.OPTIMIZE_MESHES, AiPostProcessSteps.REMOVE_REDUNDANT_MATERIALS, AiPostProcessSteps.JOIN_IDENTICAL_VERTICES);
		AiScene aiScene = Jassimp.importFile(this.m_fileName, enumSet);
		JAssImpImporter.LoadMode loadMode = this.mesh.assetParams.bStatic ? JAssImpImporter.LoadMode.StaticMesh : JAssImpImporter.LoadMode.Normal;
		ModelMesh modelMesh = this.mesh.assetParams.animationsMesh;
		SkinningData skinningData = modelMesh == null ? null : modelMesh.skinningData;
		Quaternion quaternion = new Quaternion();
		Vector4f vector4f = new Vector4f(1.0F, 0.0F, 0.0F, -1.5707964F);
		quaternion.setFromAxisAngle(vector4f);
		ProcessedAiSceneParams processedAiSceneParams = ProcessedAiSceneParams.create();
		processedAiSceneParams.scene = aiScene;
		processedAiSceneParams.mode = loadMode;
		processedAiSceneParams.skinnedTo = skinningData;
		processedAiSceneParams.meshName = this.getMeshName();
		processedAiSceneParams.animBonesScaleModifier = 0.01F;
		processedAiSceneParams.animBonesRotateModifier = quaternion;
		ProcessedAiScene processedAiScene = ProcessedAiScene.process(processedAiSceneParams);
		JAssImpImporter.takeOutTheTrash(aiScene);
		return processedAiScene;
	}

	public ModelTxt loadTxt() throws IOException {
		boolean boolean1 = this.mesh.assetParams.bStatic;
		boolean boolean2 = false;
		ModelMesh modelMesh = this.mesh.assetParams.animationsMesh;
		SkinningData skinningData = modelMesh == null ? null : modelMesh.skinningData;
		return ModelLoader.instance.loadTxt(this.m_fileName, boolean1, boolean2, skinningData);
	}

	static enum LoadMode {

		Assimp,
		Txt,
		Missing;

		private static FileTask_LoadMesh.LoadMode[] $values() {
			return new FileTask_LoadMesh.LoadMode[]{Assimp, Txt, Missing};
		}
	}
}
