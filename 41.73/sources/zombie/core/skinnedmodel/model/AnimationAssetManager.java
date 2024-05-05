package zombie.core.skinnedmodel.model;

import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetTask_RunFileTask;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.debug.DebugLog;
import zombie.fileSystem.FileSystem;


public final class AnimationAssetManager extends AssetManager {
	public static final AnimationAssetManager instance = new AnimationAssetManager();

	protected void startLoading(Asset asset) {
		AnimationAsset animationAsset = (AnimationAsset)asset;
		FileSystem fileSystem = this.getOwner().getFileSystem();
		FileTask_LoadAnimation fileTask_LoadAnimation = new FileTask_LoadAnimation(animationAsset, fileSystem, (animationAssetx)->{
    this.loadCallback(animationAsset, animationAssetx);
});
		fileTask_LoadAnimation.setPriority(4);
		String string = asset.getPath().getPath().toLowerCase();
		if (string.endsWith("bob_idle") || string.endsWith("bob_walk") || string.endsWith("bob_run")) {
			fileTask_LoadAnimation.setPriority(6);
		}

		AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask_LoadAnimation, asset);
		this.setTask(asset, assetTask_RunFileTask);
		assetTask_RunFileTask.execute();
	}

	private void loadCallback(AnimationAsset animationAsset, Object object) {
		if (object instanceof ProcessedAiScene) {
			animationAsset.onLoadedX((ProcessedAiScene)object);
			this.onLoadingSucceeded(animationAsset);
			ModelManager.instance.animationAssetLoaded(animationAsset);
		} else if (object instanceof ModelTxt) {
			animationAsset.onLoadedTxt((ModelTxt)object);
			this.onLoadingSucceeded(animationAsset);
			ModelManager.instance.animationAssetLoaded(animationAsset);
		} else {
			DebugLog.General.warn("Failed to load asset: " + animationAsset.getPath());
			this.onLoadingFailed(animationAsset);
		}
	}

	protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
		return new AnimationAsset(assetPath, this, (AnimationAsset.AnimationAssetParams)assetParams);
	}

	protected void destroyAsset(Asset asset) {
	}
}
