package zombie.core.skinnedmodel.model;

import java.util.HashSet;
import zombie.DebugFileWatcher;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetTask_RunFileTask;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.debug.DebugLog;
import zombie.fileSystem.FileSystem;
import zombie.util.StringUtils;


public final class MeshAssetManager extends AssetManager {
	public static final MeshAssetManager instance = new MeshAssetManager();
	private final HashSet m_watchedFiles = new HashSet();
	private final PredicatedFileWatcher m_watcher = new PredicatedFileWatcher(MeshAssetManager::isWatched, MeshAssetManager::watchedFileChanged);

	private MeshAssetManager() {
		DebugFileWatcher.instance.add(this.m_watcher);
	}

	protected void startLoading(Asset asset) {
		ModelMesh modelMesh = (ModelMesh)asset;
		FileSystem fileSystem = this.getOwner().getFileSystem();
		FileTask_LoadMesh fileTask_LoadMesh = new FileTask_LoadMesh(modelMesh, fileSystem, (modelMeshx)->{
    this.loadCallback(modelMesh, modelMeshx);
});
		fileTask_LoadMesh.setPriority(6);
		AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask_LoadMesh, asset);
		this.setTask(asset, assetTask_RunFileTask);
		assetTask_RunFileTask.execute();
	}

	private void loadCallback(ModelMesh modelMesh, Object object) {
		if (object instanceof ProcessedAiScene) {
			modelMesh.onLoadedX((ProcessedAiScene)object);
			this.onLoadingSucceeded(modelMesh);
		} else if (object instanceof ModelTxt) {
			modelMesh.onLoadedTxt((ModelTxt)object);
			this.onLoadingSucceeded(modelMesh);
		} else {
			DebugLog.General.warn("Failed to load asset: " + modelMesh.getPath());
			this.onLoadingFailed(modelMesh);
		}
	}

	protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
		return new ModelMesh(assetPath, this, (ModelMesh.MeshAssetParams)assetParams);
	}

	protected void destroyAsset(Asset asset) {
	}

	private static boolean isWatched(String string) {
		if (!StringUtils.endsWithIgnoreCase(string, ".fbx") && !StringUtils.endsWithIgnoreCase(string, ".x")) {
			return false;
		} else {
			String string2 = ZomboidFileSystem.instance.getString(string);
			return instance.m_watchedFiles.contains(string2);
		}
	}

	private static void watchedFileChanged(String string) {
		DebugLog.Asset.printf("%s changed\n", string);
		String string2 = ZomboidFileSystem.instance.getString(string);
		instance.getAssetTable().forEachValue((string2x)->{
			ModelMesh modelMesh = (ModelMesh)string2x;
			if (!modelMesh.isEmpty() && string2.equalsIgnoreCase(modelMesh.m_fullPath)) {
				ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
				meshAssetParams.animationsMesh = modelMesh.m_animationsMesh;
				meshAssetParams.bStatic = modelMesh.bStatic;
				instance.reload(string2x, meshAssetParams);
			}

			return true;
		});
	}

	public void addWatchedFile(String string) {
		this.m_watchedFiles.add(string);
	}
}
