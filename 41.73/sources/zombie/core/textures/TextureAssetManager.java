package zombie.core.textures;

import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;


public final class TextureAssetManager extends AssetManager {
	public static final TextureAssetManager instance = new TextureAssetManager();

	protected void startLoading(Asset asset) {
	}

	protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
		return new Texture(assetPath, this, (Texture.TextureAssetParams)assetParams);
	}

	protected void destroyAsset(Asset asset) {
	}
}
