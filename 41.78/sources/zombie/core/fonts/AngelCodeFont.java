package zombie.core.fonts;

import gnu.trove.list.array.TShortArrayList;
import gnu.trove.map.hash.TShortObjectHashMap;
import gnu.trove.procedure.TShortObjectProcedure;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.lwjgl.opengl.GL11;
import zombie.ZomboidFileSystem;
import zombie.asset.Asset;
import zombie.asset.AssetStateObserver;
import zombie.core.Color;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.util.StringUtils;


public final class AngelCodeFont implements Font,AssetStateObserver {
	private static final int DISPLAY_LIST_CACHE_SIZE = 200;
	private static final int MAX_CHAR = 255;
	private int baseDisplayListID = -1;
	public AngelCodeFont.CharDef[] chars;
	private boolean displayListCaching = false;
	private AngelCodeFont.DisplayList eldestDisplayList;
	private int eldestDisplayListID;
	private final LinkedHashMap displayLists = new LinkedHashMap(200, 1.0F, true){
    
    protected boolean removeEldestEntry(Entry var1) {
        AngelCodeFont.this.eldestDisplayList = (AngelCodeFont.DisplayList)var1.getValue();
        AngelCodeFont.this.eldestDisplayListID = AngelCodeFont.this.eldestDisplayList.id;
        return false;
    }
};
	private Texture fontImage;
	private int lineHeight;
	private HashMap pages = new HashMap();
	private File fntFile;
	public static int xoff = 0;
	public static int yoff = 0;
	public static Color curCol = null;
	public static float curR = 0.0F;
	public static float curG = 0.0F;
	public static float curB = 0.0F;
	public static float curA = 0.0F;
	private static float s_scale = 0.0F;
	private static char[] data = new char[256];

	public AngelCodeFont(String string, Texture texture) throws FileNotFoundException {
		this.fontImage = texture;
		String string2 = string;
		FileInputStream fileInputStream = new FileInputStream(new File(string));
		if (string.startsWith("/")) {
			string2 = string.substring(1);
		}

		int int1;
		while ((int1 = string2.indexOf("\\")) != -1) {
			String string3 = string2.substring(0, int1);
			string2 = string3 + "/" + string2.substring(int1 + 1);
		}

		this.parseFnt(fileInputStream);
	}

	public AngelCodeFont(String string, String string2) throws FileNotFoundException {
		if (!StringUtils.isNullOrWhitespace(string2)) {
			byte byte1 = 0;
			int int1 = byte1 | (TextureID.bUseCompression ? 4 : 0);
			this.fontImage = Texture.getSharedTexture(string2, int1);
			if (this.fontImage != null && !this.fontImage.isReady()) {
				this.fontImage.getObserverCb().add(this);
			}
		}

		String string3 = string;
		FileInputStream fileInputStream = null;
		if (string.startsWith("/")) {
			string3 = string.substring(1);
		}

		int int2;
		while ((int2 = string3.indexOf("\\")) != -1) {
			String string4 = string3.substring(0, int2);
			string3 = string4 + "/" + string3.substring(int2 + 1);
		}

		this.fntFile = new File(ZomboidFileSystem.instance.getString(string3));
		fileInputStream = new FileInputStream(ZomboidFileSystem.instance.getString(string3));
		this.parseFnt(fileInputStream);
	}

	public void drawString(float float1, float float2, String string) {
		this.drawString(float1, float2, string, Color.white);
	}

	public void drawString(float float1, float float2, String string, Color color) {
		this.drawString(float1, float2, string, color, 0, string.length() - 1);
	}

	public void drawString(float float1, float float2, String string, float float3, float float4, float float5, float float6) {
		this.drawString(float1, float2, string, float3, float4, float5, float6, 0, string.length() - 1);
	}

	public void drawString(float float1, float float2, float float3, String string, float float4, float float5, float float6, float float7) {
		this.drawString(float1, float2, float3, string, float4, float5, float6, float7, 0, string.length() - 1);
	}

	public void drawString(float float1, float float2, String string, Color color, int int1, int int2) {
		xoff = (int)float1;
		yoff = (int)float2;
		curR = color.r;
		curG = color.g;
		curB = color.b;
		curA = color.a;
		s_scale = 0.0F;
		Texture.lr = color.r;
		Texture.lg = color.g;
		Texture.lb = color.b;
		Texture.la = color.a;
		if (this.displayListCaching && int1 == 0 && int2 == string.length() - 1) {
			AngelCodeFont.DisplayList displayList = (AngelCodeFont.DisplayList)this.displayLists.get(string);
			if (displayList != null) {
				GL11.glCallList(displayList.id);
			} else {
				displayList = new AngelCodeFont.DisplayList();
				displayList.text = string;
				int int3 = this.displayLists.size();
				if (int3 < 200) {
					displayList.id = this.baseDisplayListID + int3;
				} else {
					displayList.id = this.eldestDisplayListID;
					this.displayLists.remove(this.eldestDisplayList.text);
				}

				this.displayLists.put(string, displayList);
				GL11.glNewList(displayList.id, 4865);
				this.render(string, int1, int2);
				GL11.glEndList();
			}
		} else {
			this.render(string, int1, int2);
		}
	}

	public void drawString(float float1, float float2, String string, float float3, float float4, float float5, float float6, int int1, int int2) {
		this.drawString(float1, float2, 0.0F, string, float3, float4, float5, float6, int1, int2);
	}

	public void drawString(float float1, float float2, float float3, String string, float float4, float float5, float float6, float float7, int int1, int int2) {
		xoff = (int)float1;
		yoff = (int)float2;
		curR = float4;
		curG = float5;
		curB = float6;
		curA = float7;
		s_scale = float3;
		Texture.lr = float4;
		Texture.lg = float5;
		Texture.lb = float6;
		Texture.la = float7;
		if (this.displayListCaching && int1 == 0 && int2 == string.length() - 1) {
			AngelCodeFont.DisplayList displayList = (AngelCodeFont.DisplayList)this.displayLists.get(string);
			if (displayList != null) {
				GL11.glCallList(displayList.id);
			} else {
				displayList = new AngelCodeFont.DisplayList();
				displayList.text = string;
				int int3 = this.displayLists.size();
				if (int3 < 200) {
					displayList.id = this.baseDisplayListID + int3;
				} else {
					displayList.id = this.eldestDisplayListID;
					this.displayLists.remove(this.eldestDisplayList.text);
				}

				this.displayLists.put(string, displayList);
				GL11.glNewList(displayList.id, 4865);
				this.render(string, int1, int2);
				GL11.glEndList();
			}
		} else {
			this.render(string, int1, int2);
		}
	}

	public int getHeight(String string) {
		AngelCodeFont.DisplayList displayList = null;
		if (this.displayListCaching) {
			displayList = (AngelCodeFont.DisplayList)this.displayLists.get(string);
			if (displayList != null && displayList.height != null) {
				return displayList.height.intValue();
			}
		}

		int int1 = 1;
		int int2 = 0;
		for (int int3 = 0; int3 < string.length(); ++int3) {
			char char1 = string.charAt(int3);
			if (char1 == '\n') {
				++int1;
				int2 = 0;
			} else if (char1 != ' ' && char1 < this.chars.length) {
				AngelCodeFont.CharDef charDef = this.chars[char1];
				if (charDef != null) {
					int2 = Math.max(charDef.height + charDef.yoffset, int2);
				}
			}
		}

		int2 = int1 * this.getLineHeight();
		if (displayList != null) {
			displayList.height = new Short((short)int2);
		}

		return int2;
	}

	public int getLineHeight() {
		return this.lineHeight;
	}

	public int getWidth(String string) {
		return this.getWidth(string, 0, string.length() - 1, false);
	}

	public int getWidth(String string, boolean boolean1) {
		return this.getWidth(string, 0, string.length() - 1, boolean1);
	}

	public int getWidth(String string, int int1, int int2) {
		return this.getWidth(string, int1, int2, false);
	}

	public int getWidth(String string, int int1, int int2, boolean boolean1) {
		AngelCodeFont.DisplayList displayList = null;
		if (this.displayListCaching && int1 == 0 && int2 == string.length() - 1) {
			displayList = (AngelCodeFont.DisplayList)this.displayLists.get(string);
			if (displayList != null && displayList.width != null) {
				return displayList.width.intValue();
			}
		}

		int int3 = int2 - int1 + 1;
		int int4 = 0;
		int int5 = 0;
		AngelCodeFont.CharDef charDef = null;
		for (int int6 = 0; int6 < int3; ++int6) {
			char char1 = string.charAt(int1 + int6);
			if (char1 == '\n') {
				int5 = 0;
			} else if (char1 < this.chars.length) {
				AngelCodeFont.CharDef charDef2 = this.chars[char1];
				if (charDef2 != null) {
					if (charDef != null) {
						int5 += charDef.getKerning(char1);
					}

					charDef = charDef2;
					if (!boolean1 && int6 >= int3 - 1) {
						int5 += charDef2.width;
					} else {
						int5 += charDef2.xadvance;
					}

					int4 = Math.max(int4, int5);
				}
			}
		}

		if (displayList != null) {
			displayList.width = new Short((short)int4);
		}

		return int4;
	}

	public int getYOffset(String string) {
		AngelCodeFont.DisplayList displayList = null;
		if (this.displayListCaching) {
			displayList = (AngelCodeFont.DisplayList)this.displayLists.get(string);
			if (displayList != null && displayList.yOffset != null) {
				return displayList.yOffset.intValue();
			}
		}

		int int1 = string.indexOf(10);
		if (int1 == -1) {
			int1 = string.length();
		}

		int int2 = 10000;
		for (int int3 = 0; int3 < int1; ++int3) {
			char char1 = string.charAt(int3);
			AngelCodeFont.CharDef charDef = this.chars[char1];
			if (charDef != null) {
				int2 = Math.min(charDef.yoffset, int2);
			}
		}

		if (displayList != null) {
			displayList.yOffset = new Short((short)int2);
		}

		return int2;
	}

	private AngelCodeFont.CharDef parseChar(String string) {
		AngelCodeFont.CharDef charDef = new AngelCodeFont.CharDef();
		StringTokenizer stringTokenizer = new StringTokenizer(string, " =");
		stringTokenizer.nextToken();
		stringTokenizer.nextToken();
		charDef.id = Integer.parseInt(stringTokenizer.nextToken());
		if (charDef.id < 0) {
			return null;
		} else {
			if (charDef.id > 255) {
			}

			stringTokenizer.nextToken();
			charDef.x = Short.parseShort(stringTokenizer.nextToken());
			stringTokenizer.nextToken();
			charDef.y = Short.parseShort(stringTokenizer.nextToken());
			stringTokenizer.nextToken();
			charDef.width = Short.parseShort(stringTokenizer.nextToken());
			stringTokenizer.nextToken();
			charDef.height = Short.parseShort(stringTokenizer.nextToken());
			stringTokenizer.nextToken();
			charDef.xoffset = Short.parseShort(stringTokenizer.nextToken());
			stringTokenizer.nextToken();
			charDef.yoffset = Short.parseShort(stringTokenizer.nextToken());
			stringTokenizer.nextToken();
			charDef.xadvance = Short.parseShort(stringTokenizer.nextToken());
			stringTokenizer.nextToken();
			charDef.page = Short.parseShort(stringTokenizer.nextToken());
			Texture texture = this.fontImage;
			if (this.pages.containsKey(charDef.page)) {
				texture = (Texture)this.pages.get(charDef.page);
			}

			if (texture != null && texture.isReady()) {
				charDef.init();
			}

			if (charDef.id != 32) {
				this.lineHeight = Math.max(charDef.height + charDef.yoffset, this.lineHeight);
			}

			return charDef;
		}
	}

	private void parseFnt(InputStream inputStream) {
		if (this.displayListCaching) {
			this.baseDisplayListID = GL11.glGenLists(200);
			if (this.baseDisplayListID == 0) {
				this.displayListCaching = false;
			}
		}

		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String string = bufferedReader.readLine();
			String string2 = bufferedReader.readLine();
			TShortObjectHashMap tShortObjectHashMap = new TShortObjectHashMap(64);
			ArrayList arrayList = new ArrayList(255);
			int int1 = 0;
			boolean boolean1 = false;
			AngelCodeFont.CharDef charDef;
			while (!boolean1) {
				String string3 = bufferedReader.readLine();
				if (string3 == null) {
					boolean1 = true;
				} else {
					StringTokenizer stringTokenizer;
					short short1;
					int int2;
					if (string3.startsWith("page")) {
						stringTokenizer = new StringTokenizer(string3, " =");
						stringTokenizer.nextToken();
						stringTokenizer.nextToken();
						short1 = Short.parseShort(stringTokenizer.nextToken());
						stringTokenizer.nextToken();
						String string4 = stringTokenizer.nextToken().replace("\"", "");
						String string5 = this.fntFile.getParent();
						string4 = string5 + File.separatorChar + string4;
						string4 = string4.replace("\\", "/");
						byte byte1 = 0;
						int2 = byte1 | (TextureID.bUseCompression ? 4 : 0);
						Texture texture = Texture.getSharedTexture(string4, int2);
						if (texture == null) {
							System.out.println("AngelCodeFont failed to load page " + short1 + " texture " + string4);
						} else {
							this.pages.put(short1, texture);
							if (!texture.isReady()) {
								texture.getObserverCb().add(this);
							}
						}
					}

					if (!string3.startsWith("chars c") && string3.startsWith("char")) {
						charDef = this.parseChar(string3);
						if (charDef != null) {
							int1 = Math.max(int1, charDef.id);
							arrayList.add(charDef);
						}
					}

					if (!string3.startsWith("kernings c") && string3.startsWith("kerning")) {
						stringTokenizer = new StringTokenizer(string3, " =");
						stringTokenizer.nextToken();
						stringTokenizer.nextToken();
						short1 = Short.parseShort(stringTokenizer.nextToken());
						stringTokenizer.nextToken();
						int int3 = Integer.parseInt(stringTokenizer.nextToken());
						stringTokenizer.nextToken();
						int2 = Integer.parseInt(stringTokenizer.nextToken());
						TShortArrayList tShortArrayList = (TShortArrayList)tShortObjectHashMap.get(short1);
						if (tShortArrayList == null) {
							tShortArrayList = new TShortArrayList();
							tShortObjectHashMap.put(short1, tShortArrayList);
						}

						tShortArrayList.add((short)int3);
						tShortArrayList.add((short)int2);
					}
				}
			}

			this.chars = new AngelCodeFont.CharDef[int1 + 1];
			for (Iterator iterator = arrayList.iterator(); iterator.hasNext(); this.chars[charDef.id] = charDef) {
				charDef = (AngelCodeFont.CharDef)iterator.next();
			}

			tShortObjectHashMap.forEachEntry(new TShortObjectProcedure(){
				
				public boolean execute(short inputStream, TShortArrayList bufferedReader) {
					AngelCodeFont.CharDef string = AngelCodeFont.this.chars[inputStream];
					string.kerningSecond = new short[bufferedReader.size() / 2];
					string.kerningAmount = new short[bufferedReader.size() / 2];
					int string2 = 0;
					for (int tShortObjectHashMap = 0; tShortObjectHashMap < bufferedReader.size(); tShortObjectHashMap += 2) {
						string.kerningSecond[string2] = bufferedReader.get(tShortObjectHashMap);
						string.kerningAmount[string2] = bufferedReader.get(tShortObjectHashMap + 1);
						++string2;
					}

					short[] string3 = Arrays.copyOf(string.kerningSecond, string.kerningSecond.length);
					short[] arrayList = Arrays.copyOf(string.kerningAmount, string.kerningAmount.length);
					Arrays.sort(string3);
					for (int int1 = 0; int1 < string3.length; ++int1) {
						for (int boolean1 = 0; boolean1 < string.kerningSecond.length; ++boolean1) {
							if (string.kerningSecond[boolean1] == string3[int1]) {
								string.kerningAmount[int1] = arrayList[boolean1];
								break;
							}
						}
					}

					string.kerningSecond = string3;
					return true;
				}
			});

			bufferedReader.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void render(String string, int int1, int int2) {
		++int2;
		int int3 = int2 - int1;
		float float1 = 0.0F;
		float float2 = 0.0F;
		AngelCodeFont.CharDef charDef = null;
		if (data.length < int3) {
			data = new char[(int3 + 128 - 1) / 128 * 128];
		}

		string.getChars(int1, int2, data, 0);
		for (int int4 = 0; int4 < int3; ++int4) {
			char char1 = data[int4];
			if (char1 == '\n') {
				float1 = 0.0F;
				float2 += (float)this.getLineHeight();
			} else if (char1 < this.chars.length) {
				AngelCodeFont.CharDef charDef2 = this.chars[char1];
				if (charDef2 != null) {
					if (charDef != null) {
						if (s_scale > 0.0F) {
							float1 += (float)charDef.getKerning(char1) * s_scale;
						} else {
							float1 += (float)charDef.getKerning(char1);
						}
					}

					charDef = charDef2;
					charDef2.draw(float1, float2);
					if (s_scale > 0.0F) {
						float1 += (float)charDef2.xadvance * s_scale;
					} else {
						float1 += (float)charDef2.xadvance;
					}
				}
			}
		}
	}

	public void onStateChanged(Asset.State state, Asset.State state2, Asset asset) {
		if (asset == this.fontImage || this.pages.containsValue(asset)) {
			if (state2 == Asset.State.READY) {
				AngelCodeFont.CharDef[] charDefArray = this.chars;
				int int1 = charDefArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					AngelCodeFont.CharDef charDef = charDefArray[int2];
					if (charDef != null && charDef.image == null) {
						Texture texture = this.fontImage;
						if (this.pages.containsKey(charDef.page)) {
							texture = (Texture)this.pages.get(charDef.page);
						}

						if (asset == texture) {
							charDef.init();
						}
					}
				}
			}
		}
	}

	public boolean isEmpty() {
		if (this.fontImage != null && this.fontImage.isEmpty()) {
			return true;
		} else {
			Iterator iterator = this.pages.values().iterator();
			Texture texture;
			do {
				if (!iterator.hasNext()) {
					return false;
				}

				texture = (Texture)iterator.next();
			}	 while (!texture.isEmpty());

			return true;
		}
	}

	public void destroy() {
		AngelCodeFont.CharDef[] charDefArray = this.chars;
		int int1 = charDefArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			AngelCodeFont.CharDef charDef = charDefArray[int2];
			if (charDef != null) {
				charDef.destroy();
			}
		}

		Arrays.fill(this.chars, (Object)null);
		this.pages.clear();
	}

	private static class DisplayList {
		Short height;
		int id;
		String text;
		Short width;
		Short yOffset;
	}

	public class CharDef {
		public short dlIndex;
		public short height;
		public int id;
		public Texture image;
		public short[] kerningSecond;
		public short[] kerningAmount;
		public short width;
		public short x;
		public short xadvance;
		public short xoffset;
		public short y;
		public short yoffset;
		public short page;

		public void draw(float float1, float float2) {
			Texture texture = this.image;
			if (AngelCodeFont.s_scale > 0.0F) {
				SpriteRenderer.instance.m_states.getPopulatingActiveState().render(texture, float1 + (float)this.xoffset * AngelCodeFont.s_scale + (float)AngelCodeFont.xoff, float2 + (float)this.yoffset * AngelCodeFont.s_scale + (float)AngelCodeFont.yoff, (float)this.width * AngelCodeFont.s_scale, (float)this.height * AngelCodeFont.s_scale, AngelCodeFont.curR, AngelCodeFont.curG, AngelCodeFont.curB, AngelCodeFont.curA, (Consumer)null);
			} else {
				SpriteRenderer.instance.renderi(texture, (int)(float1 + (float)this.xoffset + (float)AngelCodeFont.xoff), (int)(float2 + (float)this.yoffset + (float)AngelCodeFont.yoff), this.width, this.height, AngelCodeFont.curR, AngelCodeFont.curG, AngelCodeFont.curB, AngelCodeFont.curA, (Consumer)null);
			}
		}

		public int getKerning(int int1) {
			if (this.kerningSecond == null) {
				return 0;
			} else {
				int int2 = 0;
				int int3 = this.kerningSecond.length - 1;
				while (int2 <= int3) {
					int int4 = int2 + int3 >>> 1;
					if (this.kerningSecond[int4] < int1) {
						int2 = int4 + 1;
					} else {
						if (this.kerningSecond[int4] <= int1) {
							return this.kerningAmount[int4];
						}

						int3 = int4 - 1;
					}
				}

				return 0;
			}
		}

		public void init() {
			Texture texture = AngelCodeFont.this.fontImage;
			if (AngelCodeFont.this.pages.containsKey(this.page)) {
				texture = (Texture)AngelCodeFont.this.pages.get(this.page);
			}

			TextureID textureID = texture.getTextureId();
			String string = texture.getName();
			this.image = new AngelCodeFont.CharDefTexture(textureID, string + "_" + this.x + "_" + this.y);
			this.image.setRegion(this.x + (int)(texture.xStart * (float)texture.getWidthHW()), this.y + (int)(texture.yStart * (float)texture.getHeightHW()), this.width, this.height);
		}

		public void destroy() {
			if (this.image != null && this.image.getTextureId() != null) {
				((AngelCodeFont.CharDefTexture)this.image).releaseCharDef();
				this.image = null;
			}
		}

		public String toString() {
			return "[CharDef id=" + this.id + " x=" + this.x + " y=" + this.y + "]";
		}
	}

	public static final class CharDefTexture extends Texture {

		public CharDefTexture(TextureID textureID, String string) {
			super(textureID, string);
		}

		public void releaseCharDef() {
			this.removeDependency(this.dataid);
		}
	}
}
