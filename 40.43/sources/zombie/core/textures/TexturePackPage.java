package zombie.core.textures;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;
import zombie.core.Core;
import zombie.scripting.commands.LoadTexturePage;
import zombie.ui.TextManager;


public class TexturePackPage {
	public static HashMap FoundTextures = new HashMap();
	public static HashMap subTextureMap = new HashMap();
	public static HashMap subTextureMap2 = new HashMap();
	public static HashMap texturePackPageMap = new HashMap();
	public static HashMap TexturePackPageNameMap = new HashMap();
	public HashMap subTextures = new HashMap();
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

	public static void LoadDirListing(String string) throws URISyntaxException {
		bHasCache = false;
		File file = new File("media/" + string);
		Object object = null;
		String[] stringArray = file.list();
		int int1 = stringArray.length;
		int int2 = -1;
		for (int int3 = 0; int3 < stringArray.length; ++int3) {
			if (percent > int2) {
				Core.getInstance().StartFrame();
				Core.getInstance().EndFrame();
				Core.getInstance().StartFrameUI();
				TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), "Loading... " + percent + "%", 1.0, 1.0, 1.0, 1.0);
				Core.getInstance().EndFrameUI();
				Display.update();
				int2 = percent;
			}

			String string2 = stringArray[int3];
			File file2 = new File(string2);
			searchFoldersListing(file2);
			float float1 = (float)int3 / (float)int1 * 100.0F;
			percent = (int)float1;
			++percent;
			if (percent > 100) {
				percent = 100;
			}

			if (percent < 1) {
				percent = 1;
			}

			if (int2 == 100) {
				percent = 100;
			}
		}

		Core.getInstance().StartFrame();
		Core.getInstance().EndFrame();
		Core.getInstance().StartFrameUI();
		TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), "Loading completed.", 1.0, 1.0, 1.0, 1.0);
		Core.getInstance().EndFrameUI();
		File file3 = new File(Core.getMyDocumentFolder() + File.separator + "mods" + string);
		searchFoldersListing(file3);
	}

	public static void searchFoldersListing(File file) {
		if (file.isDirectory()) {
			String[] stringArray = file.list();
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				searchFoldersListing(new File(file.getAbsolutePath() + "\\" + stringArray[int1]));
			}
		} else if (file.getAbsolutePath().toLowerCase().contains(".txt")) {
			getPackPageListing(file.getName().replace(".txt", ""));
		}
	}

	public static void getPackPageListing(String string) {
		TexturePackPage texturePackPage = new TexturePackPage();
		try {
			texturePackPage.loadlisting("media/texturepacks/" + string + ".txt", string, (Stack)null, SliceBuffer, bHasCache);
		} catch (IOException ioException) {
			Logger.getLogger(TexturePackPage.class.getName()).log(Level.SEVERE, (String)null, ioException);
		}
	}

	public static TexturePackPage getPackPage(String string, Stack stack) {
		return texturePackPageMap.containsKey(string) ? (TexturePackPage)texturePackPageMap.get(string) : null;
	}

	public static TexturePackPage getPackPage(String string) {
		if (!string.equals("ui")) {
			TextureID.UseFiltering = true;
		} else {
			TextureID.UseFiltering = false;
		}

		return getPackPage(string, (Stack)null);
	}

	public static Texture getTexture(String string) {
		if (string.contains(".png")) {
			return Texture.getSharedTexture(string);
		} else {
			return subTextureMap.containsKey(string) ? (Texture)subTextureMap.get(string) : null;
		}
	}

	public static int readInt(BufferedInputStream bufferedInputStream) throws EOFException, IOException {
		int int1 = bufferedInputStream.read();
		int int2 = bufferedInputStream.read();
		int int3 = bufferedInputStream.read();
		int int4 = bufferedInputStream.read();
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

	public static int readIntByte(BufferedInputStream bufferedInputStream) throws EOFException, IOException {
		int int1 = chl2;
		int int2 = chl3;
		int int3 = chl4;
		int int4 = bufferedInputStream.read();
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

	public static String ReadString(BufferedInputStream bufferedInputStream) throws IOException {
		v.setLength(0);
		int int1 = readInt(bufferedInputStream);
		for (int int2 = 0; int2 < int1; ++int2) {
			v.append((char)bufferedInputStream.read());
		}

		return v.toString();
	}

	public void loadFromPackFileDDS(BufferedInputStream bufferedInputStream) throws IOException {
		String string = ReadString(bufferedInputStream);
		tempFilenameCheck.add(string);
		int int1 = readInt(bufferedInputStream);
		boolean boolean1 = readInt(bufferedInputStream) != 0;
		TempSubTextureInfo.clear();
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = ReadString(bufferedInputStream);
			int int3 = readInt(bufferedInputStream);
			int int4 = readInt(bufferedInputStream);
			int int5 = readInt(bufferedInputStream);
			int int6 = readInt(bufferedInputStream);
			int int7 = readInt(bufferedInputStream);
			int int8 = readInt(bufferedInputStream);
			int int9 = readInt(bufferedInputStream);
			int int10 = readInt(bufferedInputStream);
			TempSubTextureInfo.add(new TexturePackPage.SubTextureInfo(int3, int4, int5, int6, int7, int8, int9, int10, string2));
		}

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
		boolean boolean2 = false;
		do {
			int11 = readIntByte(bufferedInputStream);
		} while (int11 != -559038737);
	}

	public void loadFromPackFile(BufferedInputStream bufferedInputStream) throws IOException {
		String string = ReadString(bufferedInputStream);
		tempFilenameCheck.add(string);
		int int1 = readInt(bufferedInputStream);
		boolean boolean1 = readInt(bufferedInputStream) != 0;
		if (boolean1) {
			boolean boolean2 = false;
		}

		TempSubTextureInfo.clear();
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = ReadString(bufferedInputStream);
			int int3 = readInt(bufferedInputStream);
			int int4 = readInt(bufferedInputStream);
			int int5 = readInt(bufferedInputStream);
			int int6 = readInt(bufferedInputStream);
			int int7 = readInt(bufferedInputStream);
			int int8 = readInt(bufferedInputStream);
			int int9 = readInt(bufferedInputStream);
			int int10 = readInt(bufferedInputStream);
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

	public void load(String string, Stack stack, ByteBuffer byteBuffer, boolean boolean1) throws IOException, FileNotFoundException {
		this.tex = Texture.getSharedTexture(string.replace(".txt", ".png"), false);
		if (this.tex != null) {
			FileInputStream fileInputStream = new FileInputStream(string);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			boolean boolean2 = false;
			while (true) {
				String string2;
				while ((string2 = bufferedReader.readLine()) != null) {
					if (string2.contains("##nomask")) {
						boolean2 = true;
						this.tex.dataid.data = null;
					} else {
						if (!boolean2 && this.tex.getMask() == null) {
							if (!boolean1) {
							}

							this.tex.dataid.data = null;
						}

						String[] stringArray = string2.split("=");
						if (stringArray.length != 1) {
							String string3 = stringArray[0].trim();
							String string4 = stringArray[1].trim();
							String[] stringArray2 = string4.split(" ");
							if (string3 != null) {
								int int1 = Integer.parseInt(stringArray2[0]);
								int int2 = Integer.parseInt(stringArray2[1]);
								int int3 = Integer.parseInt(stringArray2[2]);
								int int4 = Integer.parseInt(stringArray2[3]);
								int int5 = Integer.parseInt(stringArray2[4]);
								int int6 = Integer.parseInt(stringArray2[5]);
								int int7 = Integer.parseInt(stringArray2[6]);
								int int8 = Integer.parseInt(stringArray2[7]);
								Texture texture = new Texture(this.tex.dataid, string3);
								texture.offsetX = (float)int5;
								texture.offsetY = (float)int6;
								texture.widthOrig = int7;
								texture.heightOrig = int8;
								texture.width = int3;
								texture.height = int4;
								texture.xStart = (float)int1 / (float)this.tex.getWidthHW();
								texture.yStart = (float)int2 / (float)this.tex.getHeightHW();
								texture.xEnd = (float)(int1 + int3) / (float)this.tex.getWidthHW();
								texture.yEnd = (float)(int2 + int4) / (float)this.tex.getHeightHW();
								this.subTextures.put(string3, texture);
								Integer integer = texture.dataid.id;
								if (stack != null) {
									for (int int9 = 0; int9 < stack.size(); ++int9) {
										LoadTexturePage.WatchPair watchPair = (LoadTexturePage.WatchPair)stack.get(int9);
										if (string3.contains(watchPair.token)) {
											if (FoundTextures.containsKey(watchPair.name)) {
												((Stack)FoundTextures.get(watchPair.name)).add(string3);
											} else {
												FoundTextures.put(watchPair.name, new Stack());
												((Stack)FoundTextures.get(watchPair.name)).add(string3);
											}
										}
									}
								}

								subTextureMap.put(string3, texture);
								subTextureMap2.put(string3 + "_" + integer.toString(), texture);
								if (byteBuffer == null) {
									boolean1 = false;
								}

								if (!boolean2) {
									if (!boolean1) {
										texture.copyMaskRegion(this.tex, int1, int2, int3, int4);
										texture.saveMaskRegion(byteBuffer);
									} else {
										texture.loadMaskRegion(byteBuffer);
									}
								}
							}
						}
					}
				}

				bufferedReader.close();
				inputStreamReader.close();
				fileInputStream.close();
				return;
			}
		}
	}

	public void loadlisting(String string, String string2, Stack stack, ByteBuffer byteBuffer, boolean boolean1) throws IOException, FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(string);
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		boolean boolean2 = false;
		String string3;
		while ((string3 = bufferedReader.readLine()) != null) {
			if (!string3.contains("##nomask")) {
				String[] stringArray = string3.split("=");
				if (stringArray.length != 1) {
					String string4 = stringArray[0].trim();
					TexturePackPageNameMap.put(string4, string2);
				}
			}
		}

		bufferedReader.close();
		inputStreamReader.close();
		fileInputStream.close();
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
