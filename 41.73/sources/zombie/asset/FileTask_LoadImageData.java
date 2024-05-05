package zombie.asset;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import zombie.core.textures.ImageData;
import zombie.core.textures.TextureIDAssetManager;
import zombie.debug.DebugOptions;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;


public final class FileTask_LoadImageData extends FileTask {
	String m_image_name;
	boolean bMask = false;

	public FileTask_LoadImageData(String string, FileSystem fileSystem, IFileTaskCallback iFileTaskCallback) {
		super(fileSystem, iFileTaskCallback);
		this.m_image_name = string;
	}

	public String getErrorMessage() {
		return this.m_image_name;
	}

	public void done() {
	}

	public Object call() throws Exception {
		TextureIDAssetManager.instance.waitFileTask();
		if (DebugOptions.instance.AssetSlowLoad.getValue()) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException interruptedException) {
			}
		}

		FileInputStream fileInputStream = new FileInputStream(this.m_image_name);
		ImageData imageData;
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			try {
				imageData = new ImageData(bufferedInputStream, this.bMask);
			} catch (Throwable throwable) {
				try {
					bufferedInputStream.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			bufferedInputStream.close();
		} catch (Throwable throwable3) {
			try {
				fileInputStream.close();
			} catch (Throwable throwable4) {
				throwable3.addSuppressed(throwable4);
			}

			throw throwable3;
		}

		fileInputStream.close();
		return imageData;
	}
}
