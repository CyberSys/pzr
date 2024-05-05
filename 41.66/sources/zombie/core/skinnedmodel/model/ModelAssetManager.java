package zombie.core.skinnedmodel.model;

import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;


public final class ModelAssetManager extends AssetManager {
	public static final ModelAssetManager instance = new ModelAssetManager();

	protected void startLoading(Asset asset) {
	}

	protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
		return new Model(assetPath, this, (Model.ModelAssetParams)assetParams);
	}

	protected void destroyAsset(Asset asset) {
	}
}
