package zombie.core.skinnedmodel.model;

import jassimp.AiScene;
import java.util.EnumSet;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;


@Deprecated
public final class AiSceneAsset extends Asset {
	AiScene m_scene;
	EnumSet m_post_process_step_set;
	AiSceneAsset.AiSceneAssetParams assetParams;
	public static final AssetType ASSET_TYPE = new AssetType("AiScene");

	protected AiSceneAsset(AssetPath assetPath, AssetManager assetManager, AiSceneAsset.AiSceneAssetParams aiSceneAssetParams) {
		super(assetPath, assetManager);
		this.assetParams = aiSceneAssetParams;
		this.m_scene = null;
		this.m_post_process_step_set = aiSceneAssetParams.post_process_step_set;
	}

	public AssetType getType() {
		return ASSET_TYPE;
	}

	public static final class AiSceneAssetParams extends AssetManager.AssetParams {
		EnumSet post_process_step_set;
	}
}
