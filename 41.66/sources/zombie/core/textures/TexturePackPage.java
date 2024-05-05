package zombie.core.textures;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;


public final class TexturePackPage {
	public static HashMap FoundTextures = new HashMap();
	public static final HashMap subTextureMap = new HashMap();
	public static final HashMap subTextureMap2 = new HashMap();
	public static final HashMap texturePackPageMap = new HashMap();
	public static final HashMap TexturePackPageNameMap = new HashMap();
	public final HashMap subTextures = new HashMap();
	public Texture tex = null;
	static ByteBuffer SliceBuffer = null;
	static boolean bHasCache = false;
	static int percent = 0;
	public static int chl1 = 0;
	public static int chl2 = 0;
	public static int chl3 = 0;
	public static int chl4 = 0;
	static StringBuilder v = new StringBuilder(50);
	public static ArrayList TempSubTextureInfo = new ArrayList();
	public static ArrayList tempFilenameCheck = new ArrayList();
	public static boolean bIgnoreWorldItemTextures = true;

	public static void LoadDir(String string) throws URISyntaxException {
	}

	public static void searchFolders(File file) {
	}

	public static Texture getTexture(String string) {
		if (string.contains(".png")) {
			return Texture.getSharedTexture(string);
		} else {
			return subTextureMap.containsKey(string) ? (Texture)subTextureMap.get(string) : null;
		}
	}

	public static int readInt(InputStream inputStream) throws EOFException, IOException {
		int int1 = inputStream.read();
		int int2 = inputStream.read();
		int int3 = inputStream.read();
		int int4 = inputStream.read();
		chl1 = int1;
		chl2 = int2;
		chl3 = int3;
		chl4 = int4;
		if ((int1 | int2 | int3 | int4) < 0) {
			throw new EOFException();
		} else {
			return (int1 << 0) + (int2 << 8) + (int3 << 16) + (int4 << 24);
		}
	}

	public static int readInt(ByteBuffer byteBuffer) throws EOFException, IOException {
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		byte byte3 = byteBuffer.get();
		byte byte4 = byteBuffer.get();
		chl1 = byte1;
		chl2 = byte2;
		chl3 = byte3;
		chl4 = byte4;
		return (byte1 << 0) + (byte2 << 8) + (byte3 << 16) + (byte4 << 24);
	}

	public static int readIntByte(InputStream inputStream) throws EOFException, IOException {
		int int1 = chl2;
		int int2 = chl3;
		int int3 = chl4;
		int int4 = inputStream.read();
		chl1 = int1;
		chl2 = int2;
		chl3 = int3;
		chl4 = int4;
		if ((int1 | int2 | int3 | int4) < 0) {
			throw new EOFException();
		} else {
			return (int1 << 0) + (int2 << 8) + (int3 << 16) + (int4 << 24);
		}
	}

	public static String ReadString(InputStream inputStream) throws IOException {
		v.setLength(0);
		int int1 = readInt(inputStream);
		for (int int2 = 0; int2 < int1; ++int2) {
			v.append((char)inputStream.read());
		}

		return v.toString();
	}

	public void loadFromPackFileDDS(BufferedInputStream bufferedInputStream) throws IOException {
		String string = ReadString(bufferedInputStream);
		tempFilenameCheck.add(string);
		int int1 = readInt((InputStream)bufferedInputStream);
		boolean boolean1 = readInt((InputStream)bufferedInputStream) != 0;
		TempSubTextureInfo.clear();
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			String string2 = ReadString(bufferedInputStream);
			int int3 = readInt((InputStream)bufferedInputStream);
			int int4 = readInt((InputStream)bufferedInputStream);
			int int5 = readInt((InputStream)bufferedInputStream);
			int int6 = readInt((InputStream)bufferedInputStream);
			int int7 = readInt((InputStream)bufferedInputStream);
			int int8 = readInt((InputStream)bufferedInputStream);
			int int9 = readInt((InputStream)bufferedInputStream);
			int int10 = readInt((InputStream)bufferedInputStream);
			if (string2.contains("ZombieWalk") && string2.contains("BobZ_")) {
				TempSubTextureInfo.add(new TexturePackPage.SubTextureInfo(int3, int4, int5, int6, int7, int8, int9, int10, string2));
			}
		}

		if (TempSubTextureInfo.isEmpty()) {
			boolean boolean2 = false;
			do {
				int2 = readIntByte(bufferedInputStream);
			}	 while (int2 != -559038737);
		} else {
			Texture texture = new Texture(string, bufferedInputStream, boolean1, Texture.PZFileformat.DDS);
			int int11;
			for (int11 = 0; int11 < TempSubTextureInfo.size(); ++int11) {
				TexturePackPage.SubTextureInfo subTextureInfo = (TexturePackPage.SubTextureInfo)TempSubTextureInfo.get(int11);
				Texture texture2 = texture.split(subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
				texture2.copyMaskRegion(texture, subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
				texture2.setName(subTextureInfo.name);
				this.subTextures.put(subTextureInfo.name, texture2);
				subTextureMap.put(subTextureInfo.name, texture2);
				texture2.offsetX = (float)subTextureInfo.ox;
				texture2.offsetY = (float)subTextureInfo.oy;
				texture2.widthOrig = subTextureInfo.fx;
				texture2.heightOrig = subTextureInfo.fy;
			}

			texture.mask = null;
			texturePackPageMap.put(string, this);
			boolean boolean3 = false;
			do {
				int11 = readIntByte(bufferedInputStream);
			}	 while (int11 != -559038737);
		}
	}

	public void loadFromPackFile(BufferedInputStream bufferedInputStream) throws Exception {
		String string = ReadString(bufferedInputStream);
		tempFilenameCheck.add(string);
		int int1 = readInt((InputStream)bufferedInputStream);
		boolean boolean1 = readInt((InputStream)bufferedInputStream) != 0;
		if (boolean1) {
			boolean boolean2 = false;
		}

		TempSubTextureInfo.clear();
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = ReadString(bufferedInputStream);
			int int3 = readInt((InputStream)bufferedInputStream);
			int int4 = readInt((InputStream)bufferedInputStream);
			int int5 = readInt((InputStream)bufferedInputStream);
			int int6 = readInt((InputStream)bufferedInputStream);
			int int7 = readInt((InputStream)bufferedInputStream);
			int int8 = readInt((InputStream)bufferedInputStream);
			int int9 = readInt((InputStream)bufferedInputStream);
			int int10 = readInt((InputStream)bufferedInputStream);
			if (!bIgnoreWorldItemTextures || !string2.startsWith("WItem_")) {
				TempSubTextureInfo.add(new TexturePackPage.SubTextureInfo(int3, int4, int5, int6, int7, int8, int9, int10, string2));
			}
		}

		Texture texture = new Texture(string, bufferedInputStream, boolean1);
		int int11;
		for (int11 = 0; int11 < TempSubTextureInfo.size(); ++int11) {
			TexturePackPage.SubTextureInfo subTextureInfo = (TexturePackPage.SubTextureInfo)TempSubTextureInfo.get(int11);
			Texture texture2 = texture.split(subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
			texture2.copyMaskRegion(texture, subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
			texture2.setName(subTextureInfo.name);
			this.subTextures.put(subTextureInfo.name, texture2);
			subTextureMap.put(subTextureInfo.name, texture2);
			texture2.offsetX = (float)subTextureInfo.ox;
			texture2.offsetY = (float)subTextureInfo.oy;
			texture2.widthOrig = subTextureInfo.fx;
			texture2.heightOrig = subTextureInfo.fy;
		}

		texture.mask = null;
		texturePackPageMap.put(string, this);
		boolean boolean3 = false;
		do {
			int11 = readIntByte(bufferedInputStream);
		} while (int11 != -559038737);
	}

	public static class SubTextureInfo {
		public int w;
		public int h;
		public int x;
		public int y;
		public int ox;
		public int oy;
		public int fx;
		public int fy;
		public String name;

		public SubTextureInfo(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, String string) {
			this.x = int1;
			this.y = int2;
			this.w = int3;
			this.h = int4;
			this.ox = int5;
			this.oy = int6;
			this.fx = int7;
			this.fy = int8;
			this.name = string;
		}
	}
}
