package zombie.fileSystem;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import zombie.ZomboidFileSystem;
import zombie.core.textures.TexturePackPage;


public final class TexturePackDevice implements IFileDevice {
	String m_name;
	String m_filename;
	final ArrayList m_pages = new ArrayList();
	final HashMap m_pagemap = new HashMap();
	final HashMap m_submap = new HashMap();
	int m_textureFlags;

	public TexturePackDevice(String string, int int1) {
		this.m_name = string;
		this.m_filename = ZomboidFileSystem.instance.getString("media/texturepacks/" + string + ".pack");
		this.m_textureFlags = int1;
	}

	public IFile createFile(IFile iFile) {
		return null;
	}

	public void destroyFile(IFile iFile) {
	}

	public InputStream createStream(String string, InputStream inputStream) throws IOException {
		this.initMetaData();
		return new TexturePackDevice.TexturePackInputStream(string, this);
	}

	public void destroyStream(InputStream inputStream) {
		if (inputStream instanceof TexturePackDevice.TexturePackInputStream) {
		}
	}

	public String name() {
		return this.m_name;
	}

	public void getSubTextureInfo(FileSystem.TexturePackTextures texturePackTextures) throws IOException {
		this.initMetaData();
		Iterator iterator = this.m_submap.values().iterator();
		while (iterator.hasNext()) {
			TexturePackDevice.SubTexture subTexture = (TexturePackDevice.SubTexture)iterator.next();
			FileSystem.SubTexture subTexture2 = new FileSystem.SubTexture(this.name(), subTexture.m_page.m_name, subTexture.m_info);
			texturePackTextures.put(subTexture.m_info.name, subTexture2);
		}
	}

	private void initMetaData() throws IOException {
		if (this.m_pages.isEmpty()) {
			FileInputStream fileInputStream = new FileInputStream(this.m_filename);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					TexturePackDevice.PositionInputStream positionInputStream = new TexturePackDevice.PositionInputStream(bufferedInputStream);
					try {
						int int1 = TexturePackPage.readInt((InputStream)positionInputStream);
						for (int int2 = 0; int2 < int1; ++int2) {
							TexturePackDevice.Page page = this.readPage(positionInputStream);
							this.m_pages.add(page);
							this.m_pagemap.put(page.m_name, page);
							Iterator iterator = page.m_sub.iterator();
							while (iterator.hasNext()) {
								TexturePackPage.SubTextureInfo subTextureInfo = (TexturePackPage.SubTextureInfo)iterator.next();
								this.m_submap.put(subTextureInfo.name, new TexturePackDevice.SubTexture(page, subTextureInfo));
							}
						}
					} catch (Throwable throwable) {
						try {
							positionInputStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					positionInputStream.close();
				} catch (Throwable throwable3) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable5) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable6) {
					throwable5.addSuppressed(throwable6);
				}

				throw throwable5;
			}

			fileInputStream.close();
		}
	}

	private TexturePackDevice.Page readPage(TexturePackDevice.PositionInputStream positionInputStream) throws IOException {
		TexturePackDevice.Page page = new TexturePackDevice.Page();
		String string = TexturePackPage.ReadString(positionInputStream);
		int int1 = TexturePackPage.readInt((InputStream)positionInputStream);
		boolean boolean1 = TexturePackPage.readInt((InputStream)positionInputStream) != 0;
		page.m_name = string;
		page.m_has_alpha = boolean1;
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			String string2 = TexturePackPage.ReadString(positionInputStream);
			int int3 = TexturePackPage.readInt((InputStream)positionInputStream);
			int int4 = TexturePackPage.readInt((InputStream)positionInputStream);
			int int5 = TexturePackPage.readInt((InputStream)positionInputStream);
			int int6 = TexturePackPage.readInt((InputStream)positionInputStream);
			int int7 = TexturePackPage.readInt((InputStream)positionInputStream);
			int int8 = TexturePackPage.readInt((InputStream)positionInputStream);
			int int9 = TexturePackPage.readInt((InputStream)positionInputStream);
			int int10 = TexturePackPage.readInt((InputStream)positionInputStream);
			page.m_sub.add(new TexturePackPage.SubTextureInfo(int3, int4, int5, int6, int7, int8, int9, int10, string2));
		}

		page.m_png_start = positionInputStream.getPosition();
		boolean boolean2 = false;
		do {
			int2 = TexturePackPage.readIntByte(positionInputStream);
		} while (int2 != -559038737);

		return page;
	}

	public boolean isAlpha(String string) {
		TexturePackDevice.Page page = (TexturePackDevice.Page)this.m_pagemap.get(string);
		return page.m_has_alpha;
	}

	public int getTextureFlags() {
		return this.m_textureFlags;
	}

	static class TexturePackInputStream extends FileInputStream {
		TexturePackDevice m_device;

		TexturePackInputStream(String string, TexturePackDevice texturePackDevice) throws IOException {
			super(texturePackDevice.m_filename);
			this.m_device = texturePackDevice;
			TexturePackDevice.Page page = (TexturePackDevice.Page)this.m_device.m_pagemap.get(string);
			if (page == null) {
				throw new FileNotFoundException();
			} else {
				this.skip(page.m_png_start);
			}
		}
	}

	static final class SubTexture {
		final TexturePackDevice.Page m_page;
		final TexturePackPage.SubTextureInfo m_info;

		SubTexture(TexturePackDevice.Page page, TexturePackPage.SubTextureInfo subTextureInfo) {
			this.m_page = page;
			this.m_info = subTextureInfo;
		}
	}

	static final class Page {
		String m_name;
		boolean m_has_alpha = false;
		long m_png_start = -1L;
		final ArrayList m_sub = new ArrayList();
	}

	public final class PositionInputStream extends FilterInputStream {
		private long pos = 0L;
		private long mark = 0L;

		public PositionInputStream(InputStream inputStream) {
			super(inputStream);
		}

		public synchronized long getPosition() {
			return this.pos;
		}

		public synchronized int read() throws IOException {
			int int1 = super.read();
			if (int1 >= 0) {
				++this.pos;
			}

			return int1;
		}

		public synchronized int read(byte[] byteArray, int int1, int int2) throws IOException {
			int int3 = super.read(byteArray, int1, int2);
			if (int3 > 0) {
				this.pos += (long)int3;
			}

			return int3;
		}

		public synchronized long skip(long long1) throws IOException {
			long long2 = super.skip(long1);
			if (long2 > 0L) {
				this.pos += long2;
			}

			return long2;
		}

		public synchronized void mark(int int1) {
			super.mark(int1);
			this.mark = this.pos;
		}

		public synchronized void reset() throws IOException {
			if (!this.markSupported()) {
				throw new IOException("Mark not supported.");
			} else {
				super.reset();
				this.pos = this.mark;
			}
		}
	}
}
