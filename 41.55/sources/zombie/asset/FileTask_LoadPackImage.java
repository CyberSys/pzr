package zombie.asset;

import java.io.InputStream;
import zombie.core.textures.ImageData;
import zombie.core.textures.TextureIDAssetManager;
import zombie.fileSystem.DeviceList;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;


public final class FileTask_LoadPackImage extends FileTask {
	String m_pack_name;
	String m_image_name;
	boolean bMask;
	int m_flags;

	public FileTask_LoadPackImage(String string, String string2, FileSystem fileSystem, IFileTaskCallback iFileTaskCallback) {
		super(fileSystem, iFileTaskCallback);
		this.m_pack_name = string;
		this.m_image_name = string2;
		this.bMask = fileSystem.getTexturePackAlpha(string, string2);
		this.m_flags = fileSystem.getTexturePackFlags(string);
	}

	public void done() {
	}

	public Object call() throws Exception {
		TextureIDAssetManager.instance.waitFileTask();
		DeviceList deviceList = this.m_file_system.getTexturePackDevice(this.m_pack_name);
		InputStream inputStream = this.m_file_system.openStream(deviceList, this.m_image_name);
		ImageData imageData;
		try {
			ImageData imageData2 = new ImageData(inputStream, this.bMask);
			if ((this.m_flags & 64) != 0) {
				imageData2.initMipMaps();
			}

			imageData = imageData2;
		} catch (Throwable throwable) {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}
			}

			throw throwable;
		}

		if (inputStream != null) {
			inputStream.close();
		}

		return imageData;
	}
}
