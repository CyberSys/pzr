package zombie.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.core.fonts.AngelCodeFont;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.network.ServerGUI;


public final class TextManager {
	public AngelCodeFont font;
	public AngelCodeFont font2;
	public AngelCodeFont font3;
	public AngelCodeFont font4;
	public AngelCodeFont main1;
	public AngelCodeFont main2;
	public AngelCodeFont zombiefontcredits1;
	public AngelCodeFont zombiefontcredits2;
	public AngelCodeFont zombienew1;
	public AngelCodeFont zombienew2;
	public AngelCodeFont zomboidDialogue;
	public AngelCodeFont codetext;
	public AngelCodeFont debugConsole;
	public AngelCodeFont intro;
	public AngelCodeFont handwritten;
	public final AngelCodeFont[] normal = new AngelCodeFont[14];
	public AngelCodeFont zombienew3;
	public final AngelCodeFont[] enumToFont = new AngelCodeFont[UIFont.values().length];
	public static final TextManager instance = new TextManager();
	public ArrayList todoTextList = new ArrayList();

	public void DrawString(double double1, double double2, String string) {
		this.font.drawString((float)double1, (float)double2, string, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void DrawString(double double1, double double2, String string, double double3, double double4, double double5, double double6) {
		this.font.drawString((float)double1, (float)double2, string, (float)double3, (float)double4, (float)double5, (float)double6);
	}

	public void DrawString(UIFont uIFont, double double1, double double2, double double3, String string, double double4, double double5, double double6, double double7) {
		AngelCodeFont angelCodeFont = this.getFontFromEnum(uIFont);
		angelCodeFont.drawString((float)double1, (float)double2, (float)double3, string, (float)double4, (float)double5, (float)double6, (float)double7);
	}

	public void DrawString(UIFont uIFont, double double1, double double2, String string, double double3, double double4, double double5, double double6) {
		AngelCodeFont angelCodeFont = this.getFontFromEnum(uIFont);
		angelCodeFont.drawString((float)double1, (float)double2, string, (float)double3, (float)double4, (float)double5, (float)double6);
	}

	public void DrawStringUntrimmed(UIFont uIFont, double double1, double double2, String string, double double3, double double4, double double5, double double6) {
		AngelCodeFont angelCodeFont = this.getFontFromEnum(uIFont);
		angelCodeFont.drawString((float)double1, (float)double2, string, (float)double3, (float)double4, (float)double5, (float)double6);
	}

	public void DrawStringCentre(double double1, double double2, String string, double double3, double double4, double double5, double double6) {
		double1 -= (double)(this.font.getWidth(string) / 2);
		this.font.drawString((float)double1, (float)double2, string, (float)double3, (float)double4, (float)double5, (float)double6);
	}

	public void DrawStringCentre(UIFont uIFont, double double1, double double2, String string, double double3, double double4, double double5, double double6) {
		AngelCodeFont angelCodeFont = this.getFontFromEnum(uIFont);
		double1 -= (double)(angelCodeFont.getWidth(string) / 2);
		angelCodeFont.drawString((float)double1, (float)double2, string, (float)double3, (float)double4, (float)double5, (float)double6);
	}

	public void DrawStringCentreDefered(UIFont uIFont, double double1, double double2, String string, double double3, double double4, double double5, double double6) {
		this.todoTextList.add(new TextManager.DeferedTextDraw(uIFont, double1, double2, string, double3, double4, double5, double6));
	}

	public void DrawTextFromGameWorld() {
		for (int int1 = 0; int1 < this.todoTextList.size(); ++int1) {
			TextManager.DeferedTextDraw deferedTextDraw = (TextManager.DeferedTextDraw)this.todoTextList.get(int1);
			this.DrawStringCentre(deferedTextDraw.font, deferedTextDraw.x, deferedTextDraw.y, deferedTextDraw.str, deferedTextDraw.r, deferedTextDraw.g, deferedTextDraw.b, deferedTextDraw.a);
		}

		this.todoTextList.clear();
	}

	public void DrawStringRight(double double1, double double2, String string, double double3, double double4, double double5, double double6) {
		double1 -= (double)this.font.getWidth(string);
		this.font.drawString((float)double1, (float)double2, string, (float)double3, (float)double4, (float)double5, (float)double6);
	}

	public TextDrawObject GetDrawTextObject(String string, int int1, boolean boolean1) {
		TextDrawObject textDrawObject = new TextDrawObject();
		return textDrawObject;
	}

	public void DrawTextObject(double double1, double double2, TextDrawObject textDrawObject) {
	}

	public void DrawStringBBcode(UIFont uIFont, double double1, double double2, String string, double double3, double double4, double double5, double double6) {
	}

	public AngelCodeFont getNormalFromFontSize(int int1) {
		return this.normal[int1 - 11];
	}

	public AngelCodeFont getFontFromEnum(UIFont uIFont) {
		if (uIFont == null) {
			return this.font;
		} else {
			AngelCodeFont angelCodeFont = this.enumToFont[uIFont.ordinal()];
			return angelCodeFont == null ? this.font : angelCodeFont;
		}
	}

	public int getFontHeight(UIFont uIFont) {
		AngelCodeFont angelCodeFont = this.getFontFromEnum(uIFont);
		return angelCodeFont.getLineHeight();
	}

	public void DrawStringRight(UIFont uIFont, double double1, double double2, String string, double double3, double double4, double double5, double double6) {
		AngelCodeFont angelCodeFont = this.getFontFromEnum(uIFont);
		double1 -= (double)angelCodeFont.getWidth(string);
		angelCodeFont.drawString((float)double1, (float)double2, string, (float)double3, (float)double4, (float)double5, (float)double6);
	}

	private String getFontFilePath(String string, String string2, String string3) {
		String string4;
		if (string2 != null) {
			string4 = "media/fonts/" + string + "/" + string2 + "/" + string3;
			if (ZomboidFileSystem.instance.getString(string4) != string4) {
				return string4;
			}
		}

		string4 = "media/fonts/" + string + "/" + string3;
		if (ZomboidFileSystem.instance.getString(string4) != string4) {
			return string4;
		} else {
			if (!"EN".equals(string)) {
				if (string2 != null) {
					string4 = "media/fonts/EN/" + string2 + "/" + string3;
					if (ZomboidFileSystem.instance.getString(string4) != string4) {
						return string4;
					}
				}

				string4 = "media/fonts/EN/" + string3;
				if (ZomboidFileSystem.instance.getString(string4) != string4) {
					return string4;
				}
			}

			string4 = "media/fonts/" + string3;
			return ZomboidFileSystem.instance.getString(string4) != string4 ? string4 : "media/" + string3;
		}
	}

	public void Init() throws FileNotFoundException {
		String string = ZomboidFileSystem.instance.getString("media/fonts/EN/fonts.txt");
		FontsFile fontsFile = new FontsFile();
		HashMap hashMap = new HashMap();
		fontsFile.read(string, hashMap);
		String string2 = Translator.getLanguage().name();
		if (!"EN".equals(string2)) {
			string = ZomboidFileSystem.instance.getString("media/fonts/" + string2 + "/fonts.txt");
			fontsFile.read(string, hashMap);
		}

		HashMap hashMap2 = new HashMap();
		String string3 = null;
		if (Core.OptionFontSize == 2) {
			string3 = "1x";
		} else if (Core.OptionFontSize == 3) {
			string3 = "2x";
		} else if (Core.OptionFontSize == 4) {
			string3 = "3x";
		} else if (Core.OptionFontSize == 5) {
			string3 = "4x";
		}

		AngelCodeFont[] angelCodeFontArray = this.enumToFont;
		int int1 = angelCodeFontArray.length;
		int int2;
		AngelCodeFont angelCodeFont;
		for (int2 = 0; int2 < int1; ++int2) {
			angelCodeFont = angelCodeFontArray[int2];
			if (angelCodeFont != null) {
				angelCodeFont.destroy();
			}
		}

		Arrays.fill(this.enumToFont, (Object)null);
		angelCodeFontArray = this.normal;
		int1 = angelCodeFontArray.length;
		for (int2 = 0; int2 < int1; ++int2) {
			angelCodeFont = angelCodeFontArray[int2];
			if (angelCodeFont != null) {
				angelCodeFont.destroy();
			}
		}

		Arrays.fill(this.normal, (Object)null);
		UIFont[] uIFontArray = UIFont.values();
		int1 = uIFontArray.length;
		for (int2 = 0; int2 < int1; ++int2) {
			UIFont uIFont = uIFontArray[int2];
			FontsFileFont fontsFileFont = (FontsFileFont)hashMap.get(uIFont.name());
			if (fontsFileFont == null) {
				DebugLog.General.warn("font \"%s\" not found in fonts.txt", uIFont.name());
			} else {
				String string4 = this.getFontFilePath(string2, string3, fontsFileFont.fnt);
				String string5 = null;
				if (fontsFileFont.img != null) {
					string5 = this.getFontFilePath(string2, string3, fontsFileFont.img);
				}

				String string6 = string4 + "|" + string5;
				if (hashMap2.get(string6) != null) {
					this.enumToFont[uIFont.ordinal()] = (AngelCodeFont)hashMap2.get(string6);
				} else {
					AngelCodeFont angelCodeFont2 = new AngelCodeFont(string4, string5);
					this.enumToFont[uIFont.ordinal()] = angelCodeFont2;
					hashMap2.put(string6, angelCodeFont2);
				}
			}
		}

		try {
			ZomboidFileSystem.instance.IgnoreActiveFileMap.set(true);
			String string7 = (new File("")).getAbsolutePath().replaceAll("\\\\", "/");
			String string8 = string7 + "/media/fonts/zomboidSmall.fnt";
			String string9 = string7 + "/media/fonts/zomboidSmall_0.png";
			if (string8.startsWith("/")) {
				string8 = "/" + string8;
			}

			this.enumToFont[UIFont.DebugConsole.ordinal()] = new AngelCodeFont(string8, string9);
		} finally {
			ZomboidFileSystem.instance.IgnoreActiveFileMap.set(false);
		}

		for (int int3 = 0; int3 < this.normal.length; ++int3) {
			this.normal[int3] = new AngelCodeFont("media/fonts/zomboidNormal" + (int3 + 11) + ".fnt", "media/fonts/zomboidNormal" + (int3 + 11) + "_0");
		}

		this.font = this.enumToFont[UIFont.Small.ordinal()];
		this.font2 = this.enumToFont[UIFont.Medium.ordinal()];
		this.font3 = this.enumToFont[UIFont.Large.ordinal()];
		this.font4 = this.enumToFont[UIFont.Massive.ordinal()];
		this.main1 = this.enumToFont[UIFont.MainMenu1.ordinal()];
		this.main2 = this.enumToFont[UIFont.MainMenu2.ordinal()];
		this.zombiefontcredits1 = this.enumToFont[UIFont.Cred1.ordinal()];
		this.zombiefontcredits2 = this.enumToFont[UIFont.Cred2.ordinal()];
		this.zombienew1 = this.enumToFont[UIFont.NewSmall.ordinal()];
		this.zombienew2 = this.enumToFont[UIFont.NewMedium.ordinal()];
		this.zombienew3 = this.enumToFont[UIFont.NewLarge.ordinal()];
		this.codetext = this.enumToFont[UIFont.Code.ordinal()];
		this.enumToFont[UIFont.MediumNew.ordinal()] = null;
		this.enumToFont[UIFont.AutoNormSmall.ordinal()] = null;
		this.enumToFont[UIFont.AutoNormMedium.ordinal()] = null;
		this.enumToFont[UIFont.AutoNormLarge.ordinal()] = null;
		this.zomboidDialogue = this.enumToFont[UIFont.Dialogue.ordinal()];
		this.intro = this.enumToFont[UIFont.Intro.ordinal()];
		this.handwritten = this.enumToFont[UIFont.Handwritten.ordinal()];
		this.debugConsole = this.enumToFont[UIFont.DebugConsole.ordinal()];
	}

	public int MeasureStringX(UIFont uIFont, String string) {
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			return 0;
		} else if (string == null) {
			return 0;
		} else {
			AngelCodeFont angelCodeFont = this.getFontFromEnum(uIFont);
			return angelCodeFont.getWidth(string);
		}
	}

	public int MeasureStringY(UIFont uIFont, String string) {
		if (uIFont != null && string != null) {
			if (GameServer.bServer && !ServerGUI.isCreated()) {
				return 0;
			} else {
				AngelCodeFont angelCodeFont = this.getFontFromEnum(uIFont);
				return angelCodeFont.getHeight(string);
			}
		} else {
			return 0;
		}
	}

	public int MeasureFont(UIFont uIFont) {
		if (uIFont == UIFont.Small) {
			return 10;
		} else if (uIFont == UIFont.Dialogue) {
			return 20;
		} else if (uIFont == UIFont.Medium) {
			return 20;
		} else if (uIFont == UIFont.Large) {
			return 24;
		} else if (uIFont == UIFont.Massive) {
			return 30;
		} else if (uIFont == UIFont.MainMenu1) {
			return 30;
		} else {
			return uIFont == UIFont.MainMenu2 ? 30 : this.getFontFromEnum(uIFont).getLineHeight();
		}
	}

	public static class DeferedTextDraw {
		public double x;
		public double y;
		public UIFont font;
		public String str;
		public double r;
		public double g;
		public double b;
		public double a;

		public DeferedTextDraw(UIFont uIFont, double double1, double double2, String string, double double3, double double4, double double5, double double6) {
			this.font = uIFont;
			this.x = double1;
			this.y = double2;
			this.str = string;
			this.r = double3;
			this.g = double4;
			this.b = double5;
			this.a = double6;
		}
	}

	public interface StringDrawer {

		void draw(UIFont uIFont, double double1, double double2, String string, double double3, double double4, double double5, double double6);
	}
}
