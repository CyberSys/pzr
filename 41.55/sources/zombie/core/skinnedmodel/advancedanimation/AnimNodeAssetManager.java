package zombie.core.skinnedmodel.advancedanimation;

import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;


public class AnimNodeAssetManager extends AssetManager {
	public static final AnimNodeAssetManager instance = new AnimNodeAssetManager();

	protected void startLoading(Asset asset) {
		AnimNodeAsset animNodeAsset = (AnimNodeAsset)asset;
		animNodeAsset.m_animNode = AnimNode.Parse(asset.getPath().getPath());
		if (animNodeAsset.m_animNode == null) {
			this.onLoadingFailed(asset);
		} else {
			this.onLoadingSucceeded(asset);
		}
	}

	public void onStateChanged(Asset.State state, Asset.State state2, Asset asset) {
		super.onStateChanged(state, state2, asset);
		if (state2 == Asset.State.READY) {
		}
	}

	protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
		return new AnimNodeAsset(assetPath, this);
	}

	protected void destroyAsset(Asset asset) {
	}
}
