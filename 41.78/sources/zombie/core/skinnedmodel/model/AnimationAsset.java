package zombie.core.skinnedmodel.model;

import java.util.HashMap;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;


public final class AnimationAsset extends Asset {
	public HashMap AnimationClips;
	public AnimationAsset.AnimationAssetParams assetParams;
	public SkinningData skinningData;
	public String modelManagerKey;
	public ModelManager.ModAnimations modAnimations;
	public static final AssetType ASSET_TYPE = new AssetType("Animation");

	public AnimationAsset(AssetPath assetPath, AssetManager assetManager, AnimationAsset.AnimationAssetParams animationAssetParams) {
		super(assetPath, assetManager);
		this.assetParams = animationAssetParams;
	}

	protected void onLoadedX(ProcessedAiScene processedAiScene) {
		processedAiScene.applyToAnimation(this);
	}

	protected void onLoadedTxt(ModelTxt modelTxt) {
		ModelLoader.instance.applyToAnimation(modelTxt, this);
	}

	public void onBeforeReady() {
		super.onBeforeReady();
		if (this.assetParams != null) {
			this.assetParams.animationsMesh = null;
			this.assetParams = null;
		}
	}

	public void setAssetParams(AssetManager.AssetParams assetParams) {
		this.assetParams = (AnimationAsset.AnimationAssetParams)assetParams;
	}

	public AssetType getType() {
		return ASSET_TYPE;
	}

	public static final class AnimationAssetParams extends AssetManager.AssetParams {
		public ModelMesh animationsMesh;
	}
}
