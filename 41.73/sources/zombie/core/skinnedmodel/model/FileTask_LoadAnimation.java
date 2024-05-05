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


public class FileTask_LoadAnimation extends FileTask_AbstractLoadModel {
	private AnimationAsset m_anim;

	public FileTask_LoadAnimation(AnimationAsset animationAsset, FileSystem fileSystem, IFileTaskCallback iFileTaskCallback) {
		super(fileSystem, iFileTaskCallback, "media/anims", "media/anims_x");
		this.m_anim = animationAsset;
	}

	public String getRawFileName() {
		return this.m_anim.getPath().getPath();
	}

	public String getErrorMessage() {
		return this.m_fileName;
	}

	public void done() {
	}

	public ProcessedAiScene loadX() throws IOException {
		DebugLog.Animation.debugln("Loading: %s", this.m_fileName);
		EnumSet enumSet = EnumSet.of(AiPostProcessSteps.MAKE_LEFT_HANDED, AiPostProcessSteps.REMOVE_REDUNDANT_MATERIALS);
		AiScene aiScene = Jassimp.importFile(this.m_fileName, enumSet);
		JAssImpImporter.LoadMode loadMode = JAssImpImporter.LoadMode.AnimationOnly;
		ModelMesh modelMesh = this.m_anim.assetParams.animationsMesh;
		SkinningData skinningData = modelMesh == null ? null : modelMesh.skinningData;
		ProcessedAiSceneParams processedAiSceneParams = ProcessedAiSceneParams.create();
		processedAiSceneParams.scene = aiScene;
		processedAiSceneParams.mode = loadMode;
		processedAiSceneParams.skinnedTo = skinningData;
		ProcessedAiScene processedAiScene = ProcessedAiScene.process(processedAiSceneParams);
		JAssImpImporter.takeOutTheTrash(aiScene);
		return processedAiScene;
	}

	public ProcessedAiScene loadFBX() throws IOException {
		DebugLog.Animation.debugln("Loading: %s", this.m_fileName);
		EnumSet enumSet = EnumSet.of(AiPostProcessSteps.MAKE_LEFT_HANDED, AiPostProcessSteps.REMOVE_REDUNDANT_MATERIALS);
		AiScene aiScene = Jassimp.importFile(this.m_fileName, enumSet);
		JAssImpImporter.LoadMode loadMode = JAssImpImporter.LoadMode.AnimationOnly;
		ModelMesh modelMesh = this.m_anim.assetParams.animationsMesh;
		SkinningData skinningData = modelMesh == null ? null : modelMesh.skinningData;
		Quaternion quaternion = new Quaternion();
		Vector4f vector4f = new Vector4f(1.0F, 0.0F, 0.0F, -1.5707964F);
		quaternion.setFromAxisAngle(vector4f);
		ProcessedAiSceneParams processedAiSceneParams = ProcessedAiSceneParams.create();
		processedAiSceneParams.scene = aiScene;
		processedAiSceneParams.mode = loadMode;
		processedAiSceneParams.skinnedTo = skinningData;
		processedAiSceneParams.animBonesScaleModifier = 0.01F;
		processedAiSceneParams.animBonesRotateModifier = quaternion;
		ProcessedAiScene processedAiScene = ProcessedAiScene.process(processedAiSceneParams);
		JAssImpImporter.takeOutTheTrash(aiScene);
		return processedAiScene;
	}

	public ModelTxt loadTxt() throws IOException {
		boolean boolean1 = false;
		boolean boolean2 = false;
		ModelMesh modelMesh = this.m_anim.assetParams.animationsMesh;
		SkinningData skinningData = modelMesh == null ? null : modelMesh.skinningData;
		return ModelLoader.instance.loadTxt(this.m_fileName, boolean1, boolean2, skinningData);
	}
}
