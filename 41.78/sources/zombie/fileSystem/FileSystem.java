package zombie.fileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import zombie.core.textures.TexturePackPage;


public abstract class FileSystem {
	public static final int INVALID_ASYNC = -1;

	public abstract boolean mount(IFileDevice iFileDevice);

	public abstract boolean unMount(IFileDevice iFileDevice);

	public abstract IFile open(DeviceList deviceList, String string, int int1);

	public abstract void close(IFile iFile);

	public abstract int openAsync(DeviceList deviceList, String string, int int1, IFileTask2Callback iFileTask2Callback);

	public abstract void closeAsync(IFile iFile, IFileTask2Callback iFileTask2Callback);

	public abstract void cancelAsync(int int1);

	public abstract InputStream openStream(DeviceList deviceList, String string) throws IOException;

	public abstract void closeStream(InputStream inputStream);

	public abstract int runAsync(FileTask fileTask);

	public abstract void updateAsyncTransactions();

	public abstract boolean hasWork();

	public abstract DeviceList getDefaultDevice();

	public abstract void mountTexturePack(String string, FileSystem.TexturePackTextures texturePackTextures, int int1);

	public abstract DeviceList getTexturePackDevice(String string);

	public abstract int getTexturePackFlags(String string);

	public abstract boolean getTexturePackAlpha(String string, String string2);

	public static final class TexturePackTextures extends HashMap {
	}

	public static final class SubTexture {
		public String m_pack_name;
		public String m_page_name;
		public TexturePackPage.SubTextureInfo m_info;

		public SubTexture(String string, String string2, TexturePackPage.SubTextureInfo subTextureInfo) {
			this.m_pack_name = string;
			this.m_page_name = string2;
			this.m_info = subTextureInfo;
		}
	}
}
