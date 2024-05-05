package zombie.asset;

import zombie.fileSystem.FileSystem;
import zombie.fileSystem.IFile;
import zombie.fileSystem.IFileTask2Callback;


final class AssetTask_LoadFromFileAsync extends AssetTask implements IFileTask2Callback {
	int m_async_op = -1;
	boolean bStream;

	AssetTask_LoadFromFileAsync(Asset asset, boolean boolean1) {
		super(asset);
		this.bStream = boolean1;
	}

	public void execute() {
		FileSystem fileSystem = this.m_asset.getAssetManager().getOwner().getFileSystem();
		int int1 = 4 | (this.bStream ? 16 : 1);
		this.m_async_op = fileSystem.openAsync(fileSystem.getDefaultDevice(), this.m_asset.getPath().m_path, int1, this);
	}

	public void cancel() {
		FileSystem fileSystem = this.m_asset.getAssetManager().getOwner().getFileSystem();
		fileSystem.cancelAsync(this.m_async_op);
		this.m_async_op = -1;
	}

	public void onFileTaskFinished(IFile iFile, Object object) {
		this.m_async_op = -1;
		if (this.m_asset.m_priv.m_desired_state == Asset.State.READY) {
			if (object != Boolean.TRUE) {
				this.m_asset.m_priv.onLoadingFailed();
			} else if (!this.m_asset.getAssetManager().loadDataFromFile(this.m_asset, iFile)) {
				this.m_asset.m_priv.onLoadingFailed();
			} else {
				this.m_asset.m_priv.onLoadingSucceeded();
			}
		}
	}
}
