package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiMesh;


public class ImportedSkeletonParams extends ProcessedAiSceneParams {
	AiMesh mesh = null;

	ImportedSkeletonParams() {
	}

	public static ImportedSkeletonParams create(ProcessedAiSceneParams processedAiSceneParams, AiMesh aiMesh) {
		ImportedSkeletonParams importedSkeletonParams = new ImportedSkeletonParams();
		importedSkeletonParams.set(processedAiSceneParams);
		importedSkeletonParams.mesh = aiMesh;
		return importedSkeletonParams;
	}
}
