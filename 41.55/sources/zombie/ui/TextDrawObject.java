package zombie.ui;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatElement;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.textures.Texture;
import zombie.network.GameServer;


public final class TextDrawObject {
	private String[] validImages;
	private String[] validFonts;
	private final ArrayList lines;
	private int width;
	private int height;
	private int maxCharsLine;
	private UIFont defaultFontEnum;
	private AngelCodeFont defaultFont;
	private String original;
	private String unformatted;
	private TextDrawObject.DrawLine currentLine;
	private TextDrawObject.DrawElement currentElement;
	private boolean hasOpened;
	private boolean drawBackground;
	private boolean allowImages;
	private boolean allowChatIcons;
	private boolean allowColors;
	private boolean allowFonts;
	private boolean allowBBcode;
	private boolean allowAnyImage;
	private boolean allowLineBreaks;
	private boolean equalizeLineHeights;
	private boolean enabled;
	private int visibleRadius;
	private float scrambleVal;
	private float outlineR;
	private float outlineG;
	private float outlineB;
	private float outlineA;
	private float defaultR;
	private float defaultG;
	private float defaultB;
	private float defaultA;
	private int hearRange;
	private float internalClock;
	private String customTag;
	private int customImageMaxDim;
	private TextDrawHorizontal defaultHorz;
	private int drawMode;
	private static ArrayList renderBatch = new ArrayList();
	private static ArrayDeque renderBatchPool = new ArrayDeque();
	private String elemText;

	public TextDrawObject() {
		this(255, 255, 255, true, true, true, true, true, false);
	}

	public TextDrawObject(int int1, int int2, int int3, boolean boolean1) {
		this(int1, int2, int3, boolean1, true, true, true, true, false);
	}

	public TextDrawObject(int int1, int int2, int int3, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, boolean boolean6) {
		this.validImages = new String[]{"Icon_music_notes", "media/ui/CarKey.png", "media/ui/ArrowUp.png", "media/ui/ArrowDown.png"};
		this.validFonts = new String[]{"Small", "Dialogue", "Medium", "Code", "Large", "Massive"};
		this.lines = new ArrayList();
		this.width = 0;
		this.height = 0;
		this.maxCharsLine = -1;
		this.defaultFontEnum = UIFont.Dialogue;
		this.defaultFont = null;
		this.original = "";
		this.unformatted = "";
		this.hasOpened = false;
		this.drawBackground = false;
		this.allowImages = true;
		this.allowChatIcons = true;
		this.allowColors = true;
		this.allowFonts = true;
		this.allowBBcode = true;
		this.allowAnyImage = false;
		this.allowLineBreaks = true;
		this.equalizeLineHeights = false;
		this.enabled = true;
		this.visibleRadius = -1;
		this.scrambleVal = 0.0F;
		this.outlineR = 0.0F;
		this.outlineG = 0.0F;
		this.outlineB = 0.0F;
		this.outlineA = 1.0F;
		this.defaultR = 1.0F;
		this.defaultG = 1.0F;
		this.defaultB = 1.0F;
		this.defaultA = 1.0F;
		this.hearRange = -1;
		this.internalClock = 0.0F;
		this.customTag = "default";
		this.customImageMaxDim = 18;
		this.defaultHorz = TextDrawHorizontal.Center;
		this.drawMode = 0;
		this.setSettings(boolean1, boolean2, boolean3, boolean4, boolean5, boolean6);
		this.setDefaultColors(int1, int2, int3);
	}

	public void setEnabled(boolean boolean1) {
		this.enabled = boolean1;
	}

	public boolean getEnabled() {
		return this.enabled;
	}

	public void setVisibleRadius(int int1) {
		this.visibleRadius = int1;
	}

	public int getVisibleRadius() {
		return this.visibleRadius;
	}

	public void setDrawBackground(boolean boolean1) {
		this.drawBackground = boolean1;
	}

	public void setAllowImages(boolean boolean1) {
		this.allowImages = boolean1;
	}

	public void setAllowChatIcons(boolean boolean1) {
		this.allowChatIcons = boolean1;
	}

	public void setAllowColors(boolean boolean1) {
		this.allowColors = boolean1;
	}

	public void setAllowFonts(boolean boolean1) {
		this.allowFonts = boolean1;
	}

	public void setAllowBBcode(boolean boolean1) {
		this.allowBBcode = boolean1;
	}

	public void setAllowAnyImage(boolean boolean1) {
		this.allowAnyImage = boolean1;
	}

	public void setAllowLineBreaks(boolean boolean1) {
		this.allowLineBreaks = boolean1;
	}

	public void setEqualizeLineHeights(boolean boolean1) {
		this.equalizeLineHeights = boolean1;
		this.calculateDimensions();
	}

	public void setSettings(boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, boolean boolean6) {
		this.allowImages = boolean2;
		this.allowChatIcons = boolean3;
		this.allowColors = boolean4;
		this.allowFonts = boolean5;
		this.allowBBcode = boolean1;
		this.equalizeLineHeights = boolean6;
	}

	public void setCustomTag(String string) {
		this.customTag = string;
	}

	public String getCustomTag() {
		return this.customTag;
	}

	public void setValidImages(String[] stringArray) {
		this.validImages = stringArray;
	}

	public void setValidFonts(String[] stringArray) {
		this.validFonts = stringArray;
	}

	public void setMaxCharsPerLine(int int1) {
		if (int1 > 0) {
			this.ReadString(this.original, int1);
		}
	}

	public void setCustomImageMaxDimensions(int int1) {
		if (int1 >= 1) {
			this.customImageMaxDim = int1;
			this.calculateDimensions();
		}
	}

	public void setOutlineColors(int int1, int int2, int int3) {
		this.setOutlineColors((float)int1 / 255.0F, (float)int2 / 255.0F, (float)int3 / 255.0F, 1.0F);
	}

	public void setOutlineColors(int int1, int int2, int int3, int int4) {
		this.setOutlineColors((float)int1 / 255.0F, (float)int2 / 255.0F, (float)int3 / 255.0F, (float)int4 / 255.0F);
	}

	public void setOutlineColors(float float1, float float2, float float3) {
		this.setOutlineColors(float1, float2, float3, 1.0F);
	}

	public void setOutlineColors(float float1, float float2, float float3, float float4) {
		this.outlineR = float1;
		this.outlineG = float2;
		this.outlineB = float3;
		this.outlineA = float4;
	}

	public void setDefaultColors(int int1, int int2, int int3) {
		this.setDefaultColors((float)int1 / 255.0F, (float)int2 / 255.0F, (float)int3 / 255.0F, 1.0F);
	}

	public void setDefaultColors(int int1, int int2, int int3, int int4) {
		this.setDefaultColors((float)int1 / 255.0F, (float)int2 / 255.0F, (float)int3 / 255.0F, (float)int4 / 255.0F);
	}

	public void setDefaultColors(float float1, float float2, float float3) {
		this.setDefaultColors(float1, float2, float3, 1.0F);
	}

	public void setDefaultColors(float float1, float float2, float float3, float float4) {
		this.defaultR = float1;
		this.defaultG = float2;
		this.defaultB = float3;
		this.defaultA = float4;
	}

	public void setHorizontalAlign(String string) {
		if (string.equals("left")) {
			this.defaultHorz = TextDrawHorizontal.Left;
		} else if (string.equals("center")) {
			this.defaultHorz = TextDrawHorizontal.Center;
		}

		if (string.equals("right")) {
			this.defaultHorz = TextDrawHorizontal.Right;
		}
	}

	public void setHorizontalAlign(TextDrawHorizontal textDrawHorizontal) {
		this.defaultHorz = textDrawHorizontal;
	}

	public TextDrawHorizontal getHorizontalAlign() {
		return this.defaultHorz;
	}

	public String getOriginal() {
		return this.original;
	}

	public String getUnformatted() {
		if (!(this.scrambleVal > 0.0F)) {
			return this.unformatted;
		} else {
			String string = "";
			Iterator iterator = this.lines.iterator();
			while (iterator.hasNext()) {
				TextDrawObject.DrawLine drawLine = (TextDrawObject.DrawLine)iterator.next();
				Iterator iterator2 = drawLine.elements.iterator();
				while (iterator2.hasNext()) {
					TextDrawObject.DrawElement drawElement = (TextDrawObject.DrawElement)iterator2.next();
					if (!drawElement.isImage) {
						string = string + drawElement.scrambleText;
					}
				}
			}

			return string;
		}
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public UIFont getDefaultFontEnum() {
		return this.defaultFontEnum;
	}

	public boolean isNullOrZeroLength() {
		return this.original == null || this.original.length() == 0;
	}

	public float getInternalClock() {
		return this.internalClock;
	}

	public void setInternalTickClock(float float1) {
		if (float1 > 0.0F) {
			this.internalClock = float1;
		}
	}

	public float updateInternalTickClock() {
		return this.updateInternalTickClock(1.25F * GameTime.getInstance().getMultiplier());
	}

	public float updateInternalTickClock(float float1) {
		if (this.internalClock <= 0.0F) {
			return 0.0F;
		} else {
			this.internalClock -= float1;
			if (this.internalClock <= 0.0F) {
				this.internalClock = 0.0F;
			}

			return this.internalClock;
		}
	}

	public void setScrambleVal(float float1) {
		if (this.scrambleVal != float1) {
			this.scrambleVal = float1;
			if (this.scrambleVal > 0.0F) {
				Iterator iterator = this.lines.iterator();
				while (iterator.hasNext()) {
					TextDrawObject.DrawLine drawLine = (TextDrawObject.DrawLine)iterator.next();
					Iterator iterator2 = drawLine.elements.iterator();
					while (iterator2.hasNext()) {
						TextDrawObject.DrawElement drawElement = (TextDrawObject.DrawElement)iterator2.next();
						if (!drawElement.isImage) {
							drawElement.scrambleText(this.scrambleVal);
						}
					}
				}
			}
		}
	}

	public float getScrambleVal() {
		return this.scrambleVal;
	}

	public void setHearRange(int int1) {
		if (int1 < 0) {
			this.hearRange = 0;
		} else {
			this.hearRange = int1;
		}
	}

	public int getHearRange() {
		return this.hearRange;
	}

	private boolean isValidFont(String string) {
		String[] stringArray = this.validFonts;
		int int1 = stringArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = stringArray[int2];
			if (string.equals(string2) && UIFont.FromString(string) != null) {
				return true;
			}
		}

		return false;
	}

	private boolean isValidImage(String string) {
		String[] stringArray = this.validImages;
		int int1 = stringArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = stringArray[int2];
			if (string.equals(string2)) {
				return true;
			}
		}

		return false;
	}

	private int tryColorInt(String string) {
		if (string.length() > 0 && string.length() <= 3) {
			try {
				int int1 = Integer.parseInt(string);
				return int1 >= 0 && int1 < 256 ? int1 : -1;
			} catch (NumberFormatException numberFormatException) {
				return -1;
			}
		} else {
			return -1;
		}
	}

	private String readTagValue(char[] charArray, int int1) {
		if (charArray[int1] == '=') {
			String string = "";
			for (int int2 = int1 + 1; int2 < charArray.length; ++int2) {
				char char1 = charArray[int2];
				if (char1 == ']') {
					return string;
				}

				string = string + char1;
			}
		}

		return null;
	}

	public void Clear() {
		this.original = "";
		this.unformatted = "";
		this.reset();
	}

	private void reset() {
		this.lines.clear();
		this.currentLine = new TextDrawObject.DrawLine();
		this.lines.add(this.currentLine);
		this.currentElement = new TextDrawObject.DrawElement();
		this.currentLine.addElement(this.currentElement);
		this.enabled = true;
		this.scrambleVal = 0.0F;
	}

	private void addNewLine() {
		this.currentLine = new TextDrawObject.DrawLine();
		this.lines.add(this.currentLine);
		this.currentElement = this.currentElement.softclone();
		this.currentLine.addElement(this.currentElement);
	}

	private void addText(String string) {
		this.currentElement.addText(string);
		TextDrawObject.DrawLine drawLine = this.currentLine;
		drawLine.charW += string.length();
	}

	private void addWord(String string) {
		if (this.maxCharsLine > 0 && this.currentLine.charW + string.length() >= this.maxCharsLine) {
			for (int int1 = 0; int1 < string.length() / this.maxCharsLine + 1; ++int1) {
				int int2 = int1 * this.maxCharsLine;
				int int3 = int2 + this.maxCharsLine < string.length() ? int2 + this.maxCharsLine : string.length();
				if (string.substring(int2, int3).length() > 0) {
					if (int1 > 0 || this.currentLine.charW != 0) {
						this.addNewLine();
					}

					this.addText(string.substring(int2, int3));
				}
			}
		} else {
			this.addText(string);
		}
	}

	private void addNewElement() {
		if (this.currentElement.text.length() == 0) {
			this.currentElement.reset();
		} else {
			this.currentElement = new TextDrawObject.DrawElement();
			this.currentLine.addElement(this.currentElement);
		}
	}

	private int readTag(char[] charArray, int int1, String string) {
		String string2;
		if (this.allowFonts && string.equals("fnt")) {
			string2 = this.readTagValue(charArray, int1);
			if (string2 != null && this.isValidFont(string2)) {
				this.addNewElement();
				this.currentElement.f = UIFont.FromString(string2);
				this.currentElement.useFont = true;
				this.currentElement.font = TextManager.instance.getFontFromEnum(this.currentElement.f);
				this.hasOpened = true;
				return int1 + string2.length() + 1;
			}
		} else {
			int int2;
			int int3;
			if ((this.allowImages || this.allowChatIcons) && string.equals("img")) {
				string2 = this.readTagValue(charArray, int1);
				if (string2 != null && string2.trim().length() > 0) {
					this.addNewElement();
					int int4 = string2.length();
					String[] stringArray = string2.split(",");
					if (stringArray.length > 1) {
						string2 = stringArray[0];
					}

					this.currentElement.isImage = true;
					this.currentElement.text = string2.trim();
					if (this.currentElement.text.equals("music")) {
						this.currentElement.text = "Icon_music_notes";
					}

					if (this.allowChatIcons && this.isValidImage(this.currentElement.text)) {
						this.currentElement.tex = Texture.getSharedTexture(this.currentElement.text);
						this.currentElement.isTextImage = true;
					} else if (this.allowImages) {
						this.currentElement.tex = Texture.getSharedTexture("Item_" + this.currentElement.text);
						if (this.currentElement.tex == null) {
							this.currentElement.tex = Texture.getSharedTexture("media/ui/Container_" + this.currentElement.text);
						}

						if (this.currentElement.tex != null) {
							this.currentElement.isTextImage = false;
							this.currentElement.text = "Item_" + this.currentElement.text;
						}
					}

					if (this.allowAnyImage && this.currentElement.tex == null) {
						this.currentElement.tex = Texture.getSharedTexture(this.currentElement.text);
						if (this.currentElement.tex != null) {
							this.currentElement.isTextImage = false;
						}
					}

					if (stringArray.length == 4) {
						int2 = this.tryColorInt(stringArray[1]);
						int3 = this.tryColorInt(stringArray[2]);
						int int5 = this.tryColorInt(stringArray[3]);
						if (int2 != -1 && int3 != -1 && int5 != -1) {
							this.currentElement.useColor = true;
							this.currentElement.R = (float)int2 / 255.0F;
							this.currentElement.G = (float)int3 / 255.0F;
							this.currentElement.B = (float)int5 / 255.0F;
						}
					}

					this.addNewElement();
					return int1 + int4 + 1;
				}
			} else if (this.allowColors && string.equals("col")) {
				string2 = this.readTagValue(charArray, int1);
				if (string2 != null) {
					String[] stringArray2 = string2.split(",");
					if (stringArray2.length == 3) {
						int int6 = this.tryColorInt(stringArray2[0]);
						int2 = this.tryColorInt(stringArray2[1]);
						int3 = this.tryColorInt(stringArray2[2]);
						if (int6 != -1 && int2 != -1 && int3 != -1) {
							this.addNewElement();
							this.currentElement.useColor = true;
							this.currentElement.R = (float)int6 / 255.0F;
							this.currentElement.G = (float)int2 / 255.0F;
							this.currentElement.B = (float)int3 / 255.0F;
							this.hasOpened = true;
							return int1 + string2.length() + 1;
						}
					}
				}
			} else if (string.equals("cdt")) {
				string2 = this.readTagValue(charArray, int1);
				if (string2 != null) {
					float float1 = this.internalClock;
					try {
						float1 = Float.parseFloat(string2);
						float1 *= 60.0F;
					} catch (NumberFormatException numberFormatException) {
						numberFormatException.printStackTrace();
					}

					this.internalClock = float1;
					return int1 + string2.length() + 1;
				}
			}
		}

		return -1;
	}

	public void setDefaultFont(UIFont uIFont) {
		if (!uIFont.equals(this.defaultFontEnum)) {
			this.ReadString(uIFont, this.original, this.maxCharsLine);
		}
	}

	private void setDefaultFontInternal(UIFont uIFont) {
		if (this.defaultFont == null || !uIFont.equals(this.defaultFontEnum)) {
			this.defaultFontEnum = uIFont;
			this.defaultFont = TextManager.instance.getFontFromEnum(uIFont);
		}
	}

	public void ReadString(String string) {
		this.ReadString(this.defaultFontEnum, string, this.maxCharsLine);
	}

	public void ReadString(String string, int int1) {
		this.ReadString(this.defaultFontEnum, string, int1);
	}

	public void ReadString(UIFont uIFont, String string, int int1) {
		if (string == null) {
			string = "";
		}

		this.reset();
		this.setDefaultFontInternal(uIFont);
		if (this.defaultFont != null) {
			this.maxCharsLine = int1;
			this.original = string;
			char[] charArray = string.toCharArray();
			this.hasOpened = false;
			String string2 = "";
			for (int int2 = 0; int2 < charArray.length; ++int2) {
				char char1 = charArray[int2];
				if (this.allowBBcode && char1 == '[') {
					if (string2.length() > 0) {
						this.addWord(string2);
						string2 = "";
					}

					if (int2 + 4 < charArray.length) {
						String string3 = (charArray[int2 + 1] + charArray[int2 + 2] + charArray[int2 + 3]).toLowerCase();
						if (this.allowLineBreaks && string3.equals("br/")) {
							this.addNewLine();
							int2 += 4;
							continue;
						}

						if (!this.hasOpened) {
							int int3 = this.readTag(charArray, int2 + 4, string3);
							if (int3 >= 0) {
								int2 = int3;
								continue;
							}
						}
					}

					if (this.hasOpened && int2 + 2 < charArray.length && charArray[int2 + 1] == '/' && charArray[int2 + 2] == ']') {
						this.hasOpened = false;
						this.addNewElement();
						int2 += 2;
						continue;
					}
				}

				if (Character.isWhitespace(char1) && int2 > 0 && !Character.isWhitespace(charArray[int2 - 1])) {
					this.addWord(string2);
					string2 = "";
				}

				string2 = string2 + char1;
				this.unformatted = this.unformatted + char1;
			}

			if (string2.length() > 0) {
				this.addWord(string2);
			}

			this.calculateDimensions();
		}
	}

	public void calculateDimensions() {
		this.width = 0;
		this.height = 0;
		int int1 = 0;
		int int2;
		TextDrawObject.DrawLine drawLine;
		for (int2 = 0; int2 < this.lines.size(); ++int2) {
			drawLine = (TextDrawObject.DrawLine)this.lines.get(int2);
			drawLine.h = 0;
			drawLine.w = 0;
			for (int int3 = 0; int3 < drawLine.elements.size(); ++int3) {
				TextDrawObject.DrawElement drawElement = (TextDrawObject.DrawElement)drawLine.elements.get(int3);
				drawElement.w = 0;
				drawElement.h = 0;
				if (drawElement.isImage && drawElement.tex != null) {
					if (drawElement.isTextImage) {
						drawElement.w = drawElement.tex.getWidth();
						drawElement.h = drawElement.tex.getHeight();
					} else {
						drawElement.w = (int)((float)drawElement.tex.getWidth() * 0.75F);
						drawElement.h = (int)((float)drawElement.tex.getHeight() * 0.75F);
					}
				} else if (drawElement.useFont && drawElement.font != null) {
					drawElement.w = drawElement.font.getWidth(drawElement.text);
					drawElement.h = drawElement.font.getHeight(drawElement.text);
				} else if (this.defaultFont != null) {
					drawElement.w = this.defaultFont.getWidth(drawElement.text);
					drawElement.h = this.defaultFont.getHeight(drawElement.text);
				}

				drawLine.w += drawElement.w;
				if (drawElement.h > drawLine.h) {
					drawLine.h = drawElement.h;
				}
			}

			if (drawLine.w > this.width) {
				this.width = drawLine.w;
			}

			this.height += drawLine.h;
			if (drawLine.h > int1) {
				int1 = drawLine.h;
			}
		}

		if (this.equalizeLineHeights) {
			this.height = 0;
			for (int2 = 0; int2 < this.lines.size(); ++int2) {
				drawLine = (TextDrawObject.DrawLine)this.lines.get(int2);
				drawLine.h = int1;
				this.height += int1;
			}
		}
	}

	public void Draw(double double1, double double2) {
		this.Draw(this.defaultHorz, double1, double2, (double)this.defaultR, (double)this.defaultG, (double)this.defaultB, (double)this.defaultA, false);
	}

	public void Draw(double double1, double double2, boolean boolean1) {
		this.Draw(this.defaultHorz, double1, double2, (double)this.defaultR, (double)this.defaultG, (double)this.defaultB, (double)this.defaultA, boolean1);
	}

	public void Draw(double double1, double double2, boolean boolean1, float float1) {
		this.Draw(this.defaultHorz, double1, double2, (double)this.defaultR, (double)this.defaultG, (double)this.defaultB, (double)float1, boolean1);
	}

	public void Draw(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		this.Draw(this.defaultHorz, double1, double2, double3, double4, double5, double6, boolean1);
	}

	public void Draw(TextDrawHorizontal textDrawHorizontal, double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		this.DrawRaw(textDrawHorizontal, double1, double2, (float)double3, (float)double4, (float)double5, (float)double6, boolean1);
	}

	public void AddBatchedDraw(double double1, double double2) {
		this.AddBatchedDraw(this.defaultHorz, double1, double2, (double)this.defaultR, (double)this.defaultG, (double)this.defaultB, (double)this.defaultA, false);
	}

	public void AddBatchedDraw(double double1, double double2, boolean boolean1) {
		this.AddBatchedDraw(this.defaultHorz, double1, double2, (double)this.defaultR, (double)this.defaultG, (double)this.defaultB, (double)this.defaultA, boolean1);
	}

	public void AddBatchedDraw(double double1, double double2, boolean boolean1, float float1) {
		this.AddBatchedDraw(this.defaultHorz, double1, double2, (double)this.defaultR, (double)this.defaultG, (double)this.defaultB, (double)float1, boolean1);
	}

	public void AddBatchedDraw(double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		this.AddBatchedDraw(this.defaultHorz, double1, double2, double3, double4, double5, double6, boolean1);
	}

	public void AddBatchedDraw(TextDrawHorizontal textDrawHorizontal, double double1, double double2, double double3, double double4, double double5, double double6, boolean boolean1) {
		if (!GameServer.bServer) {
			TextDrawObject.RenderBatch renderBatch = renderBatchPool.isEmpty() ? new TextDrawObject.RenderBatch() : (TextDrawObject.RenderBatch)renderBatchPool.pop();
			renderBatch.playerNum = IsoPlayer.getPlayerIndex();
			renderBatch.element = this;
			renderBatch.horz = textDrawHorizontal;
			renderBatch.x = double1;
			renderBatch.y = double2;
			renderBatch.r = (float)double3;
			renderBatch.g = (float)double4;
			renderBatch.b = (float)double5;
			renderBatch.a = (float)double6;
			renderBatch.drawOutlines = boolean1;
			renderBatch.add(renderBatch);
		}
	}

	public static void RenderBatch(int int1) {
		if (renderBatch.size() > 0) {
			for (int int2 = 0; int2 < renderBatch.size(); ++int2) {
				TextDrawObject.RenderBatch renderBatch = (TextDrawObject.RenderBatch)renderBatch.get(int2);
				if (renderBatch.playerNum == int1) {
					renderBatch.element.DrawRaw(renderBatch.horz, renderBatch.x, renderBatch.y, renderBatch.r, renderBatch.g, renderBatch.b, renderBatch.a, renderBatch.drawOutlines);
					renderBatchPool.add(renderBatch);
					renderBatch.remove(int2--);
				}
			}
		}
	}

	public static void NoRender(int int1) {
		for (int int2 = 0; int2 < renderBatch.size(); ++int2) {
			TextDrawObject.RenderBatch renderBatch = (TextDrawObject.RenderBatch)renderBatch.get(int2);
			if (renderBatch.playerNum == int1) {
				renderBatchPool.add(renderBatch);
				renderBatch.remove(int2--);
			}
		}
	}

	public void DrawRaw(TextDrawHorizontal textDrawHorizontal, double double1, double double2, float float1, float float2, float float3, float float4, boolean boolean1) {
		double double3 = double1;
		double double4 = double2;
		double double5 = 0.0;
		int int1 = Core.getInstance().getScreenWidth();
		int int2 = Core.getInstance().getScreenHeight();
		byte byte1 = 20;
		if (textDrawHorizontal == TextDrawHorizontal.Center) {
			double3 = double1 - (double)(this.getWidth() / 2);
		} else if (textDrawHorizontal == TextDrawHorizontal.Right) {
			double3 = double1 - (double)this.getWidth();
		}

		if (!(double3 - (double)byte1 >= (double)int1) && !(double3 + (double)this.getWidth() + (double)byte1 <= 0.0) && !(double2 - (double)byte1 >= (double)int2) && !(double2 + (double)this.getHeight() + (double)byte1 <= 0.0)) {
			if (this.drawBackground && ChatElement.backdropTexture != null) {
				ChatElement.backdropTexture.renderInnerBased((int)double3, (int)double2, this.getWidth(), this.getHeight(), 0.0F, 0.0F, 0.0F, 0.4F * float4);
			}

			float float5 = this.outlineA;
			if (boolean1 && float4 < 1.0F) {
				float5 = this.outlineA * float4;
			}

			for (int int3 = 0; int3 < this.lines.size(); ++int3) {
				TextDrawObject.DrawLine drawLine = (TextDrawObject.DrawLine)this.lines.get(int3);
				double3 = double1;
				if (textDrawHorizontal == TextDrawHorizontal.Center) {
					double3 = double1 - (double)(drawLine.w / 2);
				} else if (textDrawHorizontal == TextDrawHorizontal.Right) {
					double3 = double1 - (double)drawLine.w;
				}

				for (int int4 = 0; int4 < drawLine.elements.size(); ++int4) {
					TextDrawObject.DrawElement drawElement = (TextDrawObject.DrawElement)drawLine.elements.get(int4);
					double5 = (double)(drawLine.h / 2 - drawElement.h / 2);
					this.elemText = this.scrambleVal > 0.0F ? drawElement.scrambleText : drawElement.text;
					if (drawElement.isImage && drawElement.tex != null) {
						if (boolean1 && drawElement.isTextImage) {
							SpriteRenderer.instance.renderi(drawElement.tex, (int)(double3 - 1.0), (int)(double4 + double5 - 1.0), drawElement.w, drawElement.h, this.outlineR, this.outlineG, this.outlineB, float5, (Consumer)null);
							SpriteRenderer.instance.renderi(drawElement.tex, (int)(double3 + 1.0), (int)(double4 + double5 + 1.0), drawElement.w, drawElement.h, this.outlineR, this.outlineG, this.outlineB, float5, (Consumer)null);
							SpriteRenderer.instance.renderi(drawElement.tex, (int)(double3 - 1.0), (int)(double4 + double5 + 1.0), drawElement.w, drawElement.h, this.outlineR, this.outlineG, this.outlineB, float5, (Consumer)null);
							SpriteRenderer.instance.renderi(drawElement.tex, (int)(double3 + 1.0), (int)(double4 + double5 - 1.0), drawElement.w, drawElement.h, this.outlineR, this.outlineG, this.outlineB, float5, (Consumer)null);
						}

						if (drawElement.useColor) {
							SpriteRenderer.instance.renderi(drawElement.tex, (int)double3, (int)(double4 + double5), drawElement.w, drawElement.h, drawElement.R, drawElement.G, drawElement.B, float4, (Consumer)null);
						} else if (drawElement.isTextImage) {
							SpriteRenderer.instance.renderi(drawElement.tex, (int)double3, (int)(double4 + double5), drawElement.w, drawElement.h, float1, float2, float3, float4, (Consumer)null);
						} else {
							SpriteRenderer.instance.renderi(drawElement.tex, (int)double3, (int)(double4 + double5), drawElement.w, drawElement.h, 1.0F, 1.0F, 1.0F, float4, (Consumer)null);
						}
					} else if (drawElement.useFont && drawElement.font != null) {
						if (boolean1) {
							drawElement.font.drawString((float)(double3 - 1.0), (float)(double4 + double5 - 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, float5);
							drawElement.font.drawString((float)(double3 + 1.0), (float)(double4 + double5 + 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, float5);
							drawElement.font.drawString((float)(double3 - 1.0), (float)(double4 + double5 + 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, float5);
							drawElement.font.drawString((float)(double3 + 1.0), (float)(double4 + double5 - 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, float5);
						}

						drawElement.font.drawString((float)double3, (float)(double4 + double5), this.elemText, float1, float2, float3, float4);
					} else if (this.defaultFont != null) {
						if (boolean1) {
							this.defaultFont.drawString((float)(double3 - 1.0), (float)(double4 + double5 - 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, float5);
							this.defaultFont.drawString((float)(double3 + 1.0), (float)(double4 + double5 + 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, float5);
							this.defaultFont.drawString((float)(double3 - 1.0), (float)(double4 + double5 + 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, float5);
							this.defaultFont.drawString((float)(double3 + 1.0), (float)(double4 + double5 - 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, float5);
						}

						if (drawElement.useColor) {
							this.defaultFont.drawString((float)double3, (float)(double4 + double5), this.elemText, drawElement.R, drawElement.G, drawElement.B, float4);
						} else {
							this.defaultFont.drawString((float)double3, (float)(double4 + double5), this.elemText, float1, float2, float3, float4);
						}
					}

					double3 += (double)drawElement.w;
				}

				double4 += (double)drawLine.h;
			}
		}
	}

	private static final class DrawLine {
		private final ArrayList elements = new ArrayList();
		private int h = 0;
		private int w = 0;
		private int charW = 0;

		private void addElement(TextDrawObject.DrawElement drawElement) {
			this.elements.add(drawElement);
		}
	}

	private static final class DrawElement {
		private String text = "";
		private String scrambleText = "";
		private float currentScrambleVal = 0.0F;
		private UIFont f;
		private AngelCodeFont font;
		private float R;
		private float G;
		private float B;
		private int w;
		private int h;
		private boolean isImage;
		private boolean useFont;
		private boolean useColor;
		private Texture tex;
		private boolean isTextImage;
		private int charWidth;

		private DrawElement() {
			this.f = UIFont.AutoNormSmall;
			this.font = null;
			this.R = 1.0F;
			this.G = 1.0F;
			this.B = 1.0F;
			this.w = 0;
			this.h = 0;
			this.isImage = false;
			this.useFont = false;
			this.useColor = false;
			this.tex = null;
			this.isTextImage = false;
			this.charWidth = 0;
		}

		private void reset() {
			this.text = "";
			this.scrambleText = "";
			this.f = UIFont.AutoNormSmall;
			this.font = null;
			this.R = 1.0F;
			this.G = 1.0F;
			this.B = 1.0F;
			this.w = 0;
			this.h = 0;
			this.isImage = false;
			this.useFont = false;
			this.useColor = false;
			this.tex = null;
			this.isTextImage = false;
			this.charWidth = 0;
		}

		private void addText(String string) {
			this.text = this.text + string;
			this.charWidth = this.text.length();
		}

		private void scrambleText(float float1) {
			if (float1 != this.currentScrambleVal) {
				this.currentScrambleVal = float1;
				int int1 = (int)(float1 * 100.0F);
				String[] stringArray = this.text.split("\\s+");
				this.scrambleText = "";
				String[] stringArray2 = stringArray;
				int int2 = stringArray.length;
				for (int int3 = 0; int3 < int2; ++int3) {
					String string = stringArray2[int3];
					int int4 = Rand.Next(100);
					if (int4 > int1) {
						this.scrambleText = this.scrambleText + string + " ";
					} else {
						char[] charArray = new char[string.length()];
						Arrays.fill(charArray, ".".charAt(0));
						String string2 = this.scrambleText;
						this.scrambleText = string2 + new String(charArray) + " ";
					}
				}
			}
		}

		private void trim() {
			this.text = this.text.trim();
		}

		private TextDrawObject.DrawElement softclone() {
			TextDrawObject.DrawElement drawElement = new TextDrawObject.DrawElement();
			if (this.useColor) {
				drawElement.R = this.R;
				drawElement.G = this.G;
				drawElement.B = this.B;
				drawElement.useColor = this.useColor;
			}

			if (this.useFont) {
				drawElement.f = this.f;
				drawElement.font = this.font;
				drawElement.useFont = this.useFont;
			}

			return drawElement;
		}
	}

	private static final class RenderBatch {
		int playerNum;
		TextDrawObject element;
		TextDrawHorizontal horz;
		double x;
		double y;
		float r;
		float g;
		float b;
		float a;
		boolean drawOutlines;
	}
}
