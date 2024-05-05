package zombie.core.skinnedmodel.model;

import jassimp.AiScene;
import jassimp.Jassimp;
import java.util.EnumSet;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetTask_RunFileTask;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;


@Deprecated
public final class AiSceneAssetManager extends AssetManager {
	public static final AiSceneAssetManager instance = new AiSceneAssetManager();

	protected void startLoading(Asset asset) {
		FileSystem fileSystem = asset.getAssetManager().getOwner().getFileSystem();
		AiSceneAssetManager.FileTask_LoadAiScene fileTask_LoadAiScene = new AiSceneAssetManager.FileTask_LoadAiScene(asset.getPath().getPath(), ((AiSceneAsset)asset).m_post_process_step_set, (fileSystemx)->{
    this.onFileTaskFinished((AiSceneAsset)asset, fileSystemx);
}, fileSystem);
		AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask_LoadAiScene, asset);
		this.setTask(asset, assetTask_RunFileTask);
		assetTask_RunFileTask.execute();
	}

	public void onFileTaskFinished(AiSceneAsset aiSceneAsset, Object object) {
		if (object instanceof AiScene) {
			aiSceneAsset.m_scene = (AiScene)object;
			this.onLoadingSucceeded(aiSceneAsset);
		} else {
			this.onLoadingFailed(aiSceneAsset);
		}
	}

	protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
		return new AiSceneAsset(assetPath, this, (AiSceneAsset.AiSceneAssetParams)assetParams);
	}

	protected void destroyAsset(Asset asset) {
	}

	static class FileTask_LoadAiScene extends FileTask {
		String m_filename;
		EnumSet m_post_process_step_set;

		public FileTask_LoadAiScene(String string, EnumSet enumSet, IFileTaskCallback iFileTaskCallback, FileSystem fileSystem) {
			super(fileSystem, iFileTaskCallback);
			this.m_filename = string;
			this.m_post_process_step_set = enumSet;
		}

		public String getErrorMessage() {
			return this.m_filename;
		}

		public void done() {
			this.m_filename = null;
			this.m_post_process_step_set = null;
		}

		public Object call() throws Exception {
			return Jassimp.importFile(this.m_filename, this.m_post_process_step_set);
		}
	}
}
