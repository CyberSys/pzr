package zombie.core.textures;

import java.util.Objects;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetTask_RunFileTask;
import zombie.asset.FileTask_LoadImageData;
import zombie.asset.FileTask_LoadPackImage;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.DirectBufferAllocator;
import zombie.fileSystem.FileSystem;


public final class TextureIDAssetManager extends AssetManager {
	public static final TextureIDAssetManager instance = new TextureIDAssetManager();

	protected void startLoading(Asset asset) {
		TextureID textureID = (TextureID)asset;
		FileSystem fileSystem = this.getOwner().getFileSystem();
		if (textureID.assetParams != null && textureID.assetParams.subTexture != null) {
			FileSystem.SubTexture subTexture = textureID.assetParams.subTexture;
			FileTask_LoadPackImage fileTask_LoadPackImage = new FileTask_LoadPackImage(subTexture.m_pack_name, subTexture.m_page_name, fileSystem, (textureIDx)->{
				this.onFileTaskFinished(asset, textureIDx);
			});

			fileTask_LoadPackImage.setPriority(7);
			AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask_LoadPackImage, asset);
			this.setTask(asset, assetTask_RunFileTask);
			assetTask_RunFileTask.execute();
		} else {
			FileTask_LoadImageData fileTask_LoadImageData = new FileTask_LoadImageData(asset.getPath().getPath(), fileSystem, (textureIDx)->{
				this.onFileTaskFinished(asset, textureIDx);
			});

			fileTask_LoadImageData.setPriority(7);
			AssetTask_RunFileTask assetTask_RunFileTask2 = new AssetTask_RunFileTask(fileTask_LoadImageData, asset);
			this.setTask(asset, assetTask_RunFileTask2);
			assetTask_RunFileTask2.execute();
		}
	}

	protected void unloadData(Asset asset) {
		TextureID textureID = (TextureID)asset;
		if (!textureID.isDestroyed()) {
			Objects.requireNonNull(textureID);
			RenderThread.invokeOnRenderContext(textureID::destroy);
		}
	}

	protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
		return new TextureID(assetPath, this, (TextureID.TextureIDAssetParams)assetParams);
	}

	protected void destroyAsset(Asset asset) {
	}

	private void onFileTaskFinished(Asset asset, Object object) {
		TextureID textureID = (TextureID)asset;
		if (object instanceof ImageData) {
			textureID.setImageData((ImageData)object);
			this.onLoadingSucceeded(asset);
		} else {
			this.onLoadingFailed(asset);
		}
	}

	public void waitFileTask() {
		while (DirectBufferAllocator.getBytesAllocated() > 52428800L) {
			try {
				Thread.sleep(20L);
			} catch (InterruptedException interruptedException) {
			}
		}
	}
}
