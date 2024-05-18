package zombie.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.core.Translator;
import zombie.core.fonts.AngelCodeFont;
import zombie.network.GameServer;
import zombie.network.ServerGUI;


public class TextManager {
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
	public AngelCodeFont[] normal = new AngelCodeFont[14];
	public AngelCodeFont zombienew3;
	public final AngelCodeFont[] enumToFont = new AngelCodeFont[UIFont.values().length];
	public static TextManager instance = new TextManager();
	public ArrayList todoTextList = new ArrayList();

	public void DrawString(double double1, double double2, String string) {
		this.font.drawString((float)double1, (float)double2, string, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void DrawString(double double1, double double2, String string, double double3, double double4, double double5, double double6) {
		this.font.drawString((float)double1, (float)double2, string, (float)double3, (float)double4, (float)double5, (float)double6);
	}

	public void DrawString(UIFont uIFont, double double1, double double2, float float1, String string, double double3, double double4, double double5, double double6) {
		AngelCodeFont angelCodeFont = this.getFontFromEnum(uIFont);
		angelCodeFont.zoom = float1;
		angelCodeFont.drawString((float)double1, (float)double2, string, (float)double3, (float)double4, (float)double5, (float)double6);
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

	public void Init() throws FileNotFoundException {
		if ("KO".equals(Translator.getLanguage().name())) {
			this.font = new AngelCodeFont("media/fonts/zomboidSmallKorean.fnt", "");
			this.font2 = new AngelCodeFont("media/fonts/zomboidMediumKorean.fnt", "");
			this.font3 = new AngelCodeFont("media/fonts/zomboidLargeKorean.fnt", "");
			this.zombienew1 = new AngelCodeFont("media/fonts/zomboidSmallKorean.fnt", "");
			this.zombienew2 = new AngelCodeFont("media/fonts/zomboidMediumKorean.fnt", "");
			this.zombienew3 = new AngelCodeFont("media/fonts/zomboidLargeKorean.fnt", "");
			this.zomboidDialogue = new AngelCodeFont("media/fonts/zomboidMediumKorean.fnt", "");
			this.intro = new AngelCodeFont("media/fonts/zomboidLargeKorean.fnt", "");
			this.zombiefontcredits1 = new AngelCodeFont("media/zombiefontcredits1.fnt", "media/zombiefontcredits1_0.png");
			this.zombiefontcredits2 = new AngelCodeFont("media/zombiefontcredits2.fnt", "media/zombiefontcredits2_0.png");
			this.handwritten = new AngelCodeFont("media/fonts/zomboidSmallKorean.fnt", "");
		} else if ("JP".equals(Translator.getLanguage().name())) {
			this.font = new AngelCodeFont("media/fonts/zomboidSmallJapanese.fnt", "");
			this.font2 = new AngelCodeFont("media/fonts/zomboidMediumJapanese.fnt", "");
			this.font3 = new AngelCodeFont("media/fonts/zomboidLargeJapanese.fnt", "");
			this.zombienew1 = new AngelCodeFont("media/fonts/zomboidSmallJapanese.fnt", "");
			this.zombienew2 = new AngelCodeFont("media/fonts/zomboidMediumJapanese.fnt", "");
			this.zombienew3 = new AngelCodeFont("media/fonts/zomboidLargeJapanese.fnt", "");
			this.zomboidDialogue = new AngelCodeFont("media/fonts/zomboidMediumJapanese.fnt", "");
			this.intro = new AngelCodeFont("media/fonts/zomboidLargeJapanese.fnt", "");
			this.zombiefontcredits1 = new AngelCodeFont("media/zombiefontcredits1.fnt", "media/zombiefontcredits1_0.png");
			this.zombiefontcredits2 = new AngelCodeFont("media/zombiefontcredits2.fnt", "media/zombiefontcredits2_0.png");
			this.handwritten = new AngelCodeFont("media/fonts/zomboidSmallJapanese.fnt", "");
		} else if ("CH".equals(Translator.getLanguage().name())) {
			this.font = new AngelCodeFont("media/fonts/zomboidSmallChinese.fnt", "");
			this.font2 = new AngelCodeFont("media/fonts/zomboidMediumChinese.fnt", "");
			this.font3 = new AngelCodeFont("media/fonts/zomboidLargeChinese.fnt", "");
			this.zombienew1 = new AngelCodeFont("media/fonts/zomboidSmallChinese.fnt", "");
			this.zombienew2 = new AngelCodeFont("media/fonts/zomboidMediumChinese.fnt", "");
			this.zombienew3 = new AngelCodeFont("media/fonts/zomboidLargeChinese.fnt", "");
			this.zomboidDialogue = new AngelCodeFont("media/fonts/zomboidMediumChinese.fnt", "");
			this.intro = new AngelCodeFont("media/fonts/zomboidLargeChinese.fnt", "");
			this.zombiefontcredits1 = new AngelCodeFont("media/zombiefontcredits1.fnt", "media/zombiefontcredits1_0.png");
			this.zombiefontcredits2 = new AngelCodeFont("media/zombiefontcredits2.fnt", "media/zombiefontcredits2_0.png");
			this.handwritten = new AngelCodeFont("media/fonts/zomboidSmallChinese.fnt", "");
		} else if ("TH".equals(Translator.getLanguage().name())) {
			this.font = new AngelCodeFont("media/fonts/thai/zomboidSmall.fnt", "media/fonts/thai/zomboidSmall_0.png");
			this.font2 = new AngelCodeFont("media/fonts/thai/zomboidMedium.fnt", "media/fonts/thai/zomboidMedium_0.png");
			this.font3 = new AngelCodeFont("media/fonts/thai/zomboidLarge.fnt", "media/fonts/thai/zomboidLarge_0.png");
			this.zombienew1 = new AngelCodeFont("media/fonts/thai/zomboidSmall.fnt", "media/fonts/thai/zomboidSmall_0.png");
			this.zombienew2 = new AngelCodeFont("media/fonts/thai/zomboidMedium.fnt", "media/fonts/thai/zomboidMedium_0.png");
			this.zombienew3 = new AngelCodeFont("media/fonts/thai/zomboidLarge.fnt", "media/fonts/thai/zomboidLarge_0.png");
			this.zomboidDialogue = new AngelCodeFont("media/fonts/thai/zomboidDialogue.fnt", "media/fonts/thai/zomboidDialogue_0.png");
			this.intro = new AngelCodeFont("media/fonts/thai/zomboidIntro.fnt", "media/fonts/thai/zomboidIntro_0.png");
			this.zombiefontcredits1 = new AngelCodeFont("media/zombiefontcredits1.fnt", "media/fonts/thai/zombiefontcredits1_0.png");
			this.zombiefontcredits2 = new AngelCodeFont("media/zombiefontcredits2.fnt", "media/fonts/thai/zombiefontcredits2_0.png");
			this.handwritten = new AngelCodeFont("media/fonts/thai/zomboidSmall.fnt", "media/fonts/thai/zomboidSmall_0.png");
		} else if ("CN".equals(Translator.getLanguage().name())) {
			this.font = new AngelCodeFont("media/fonts/zomboidSmallCN.fnt", "");
			this.font2 = new AngelCodeFont("media/fonts/zomboidMediumCN.fnt", "");
			this.font3 = new AngelCodeFont("media/fonts/zomboidLargeCN.fnt", "");
			this.zombienew1 = new AngelCodeFont("media/fonts/zomboidSmallCN.fnt", "");
			this.zombienew2 = new AngelCodeFont("media/fonts/zomboidMediumCN.fnt", "");
			this.zombienew3 = new AngelCodeFont("media/fonts/zomboidLargeCN.fnt", "");
			this.zomboidDialogue = new AngelCodeFont("media/fonts/zomboidMediumCN.fnt", "");
			this.intro = new AngelCodeFont("media/fonts/zomboidLargeCN.fnt", "");
			this.zombiefontcredits1 = new AngelCodeFont("media/zombiefontcredits1.fnt", "media/zombiefontcredits1_0.png");
			this.zombiefontcredits2 = new AngelCodeFont("media/zombiefontcredits2.fnt", "media/zombiefontcredits2_0.png");
			this.handwritten = new AngelCodeFont("media/fonts/zomboidSmallCN.fnt", "");
		} else if ("RU".equals(Translator.getLanguage().name())) {
			this.font = new AngelCodeFont("media/fonts/zomboidSmallCyrillic.fnt", "media/fonts/zomboidSmallCyrillic_0.png");
			this.font2 = new AngelCodeFont("media/fonts/zomboidMediumCyrillic.fnt", "media/fonts/zomboidMediumCyrillic_0.png");
			this.font3 = new AngelCodeFont("media/fonts/zomboidLargeCyrillic.fnt", "media/fonts/zomboidLargeCyrillic_0.png");
			this.zombienew1 = new AngelCodeFont("media/fonts/zomboidSmallCyrillic.fnt", "media/fonts/zomboidSmallCyrillic_0.png");
			this.zombienew2 = new AngelCodeFont("media/fonts/zomboidMediumCyrillic.fnt", "media/fonts/zomboidMediumCyrillic_0.png");
			this.zombienew3 = new AngelCodeFont("media/fonts/zomboidLargeCyrillic.fnt", "media/fonts/zomboidLargeCyrillic_0.png");
			this.zomboidDialogue = new AngelCodeFont("media/fonts/zomboidDialogueRussian.fnt", "media/fonts/zomboidDialogueRussian_0.png");
			this.intro = new AngelCodeFont("media/fonts/zomboidIntro.fnt", "media/fonts/zomboidIntro_0.png");
			this.zombiefontcredits1 = new AngelCodeFont("media/zombiefontcredits1.fnt", "media/zombiefontcredits1_0.png");
			this.zombiefontcredits2 = new AngelCodeFont("media/fonts/zomboidLargeCyrillic.fnt", "media/fonts/zomboidLargeCyrillic_0.png");
			this.handwritten = new AngelCodeFont("media/fonts/zomboidSmallCyrillic.fnt", "media/fonts/zomboidSmallCyrillic_0.png");
		} else {
			this.font = new AngelCodeFont("media/fonts/zomboidSmall.fnt", "media/fonts/zomboidSmall_0.png");
			this.font2 = new AngelCodeFont("media/fonts/zomboidMedium.fnt", "media/fonts/zomboidMedium_0.png");
			this.font3 = new AngelCodeFont("media/fonts/zomboidLarge.fnt", "media/fonts/zomboidLarge_0.png");
			this.zombienew1 = new AngelCodeFont("media/fonts/zomboidSmall.fnt", "media/fonts/zomboidSmall_0.png");
			this.zombienew2 = new AngelCodeFont("media/fonts/zomboidMedium.fnt", "media/fonts/zomboidMedium_0.png");
			this.zombienew3 = new AngelCodeFont("media/fonts/zomboidLarge.fnt", "media/fonts/zomboidLarge_0.png");
			this.zomboidDialogue = new AngelCodeFont("media/fonts/zomboidDialogue.fnt", "media/fonts/zomboidDialogue_0.png");
			this.intro = new AngelCodeFont("media/fonts/zomboidIntro.fnt", "media/fonts/zomboidIntro_0.png");
			this.zombiefontcredits1 = new AngelCodeFont("media/zombiefontcredits1.fnt", "media/zombiefontcredits1_0.png");
			this.zombiefontcredits2 = new AngelCodeFont("media/zombiefontcredits2.fnt", "media/zombiefontcredits2_0.png");
			this.handwritten = new AngelCodeFont("media/fonts/handwritten.fnt", "media/fonts/handwritten_0.png");
		}

		this.font4 = new AngelCodeFont("media/zombiefont5.fnt", "zombiefont5_0");
		this.main1 = new AngelCodeFont("media/font/mainfont.fnt", "mainfont_0");
		this.main2 = new AngelCodeFont("media/font/mainfont2.fnt", "mainfont2_0");
		this.codetext = new AngelCodeFont("media/fonts/zomboidCode.fnt", "media/fonts/zomboidCode_0.png");
		try {
			ZomboidFileSystem.instance.IgnoreActiveFileMap = true;
			String string = (new File("")).getAbsolutePath().replaceAll("\\\\", "/");
			String string2 = string + "/media/fonts/zomboidSmall.fnt";
			String string3 = string + "/media/fonts/zomboidSmall_0.png";
			if (string2.startsWith("/")) {
				string2 = "/" + string2;
			}

			this.debugConsole = new AngelCodeFont(string2, string3);
		} finally {
			ZomboidFileSystem.instance.IgnoreActiveFileMap = false;
		}

		for (int int1 = 0; int1 < this.normal.length; ++int1) {
			this.normal[int1] = new AngelCodeFont("media/fonts/zomboidNormal" + (int1 + 11) + ".fnt", "media/fonts/zomboidNormal" + (int1 + 11) + "_0");
		}

		this.enumToFont[UIFont.Small.ordinal()] = this.font;
		this.enumToFont[UIFont.Medium.ordinal()] = this.font2;
		this.enumToFont[UIFont.Large.ordinal()] = this.font3;
		this.enumToFont[UIFont.Massive.ordinal()] = this.font4;
		this.enumToFont[UIFont.MainMenu1.ordinal()] = this.main1;
		this.enumToFont[UIFont.MainMenu2.ordinal()] = this.main2;
		this.enumToFont[UIFont.Cred1.ordinal()] = this.zombiefontcredits1;
		this.enumToFont[UIFont.Cred2.ordinal()] = this.zombiefontcredits2;
		this.enumToFont[UIFont.NewSmall.ordinal()] = this.zombienew1;
		this.enumToFont[UIFont.NewMedium.ordinal()] = this.zombienew2;
		this.enumToFont[UIFont.NewLarge.ordinal()] = this.zombienew3;
		this.enumToFont[UIFont.Code.ordinal()] = this.codetext;
		this.enumToFont[UIFont.MediumNew.ordinal()] = null;
		this.enumToFont[UIFont.AutoNormSmall.ordinal()] = null;
		this.enumToFont[UIFont.AutoNormMedium.ordinal()] = null;
		this.enumToFont[UIFont.AutoNormLarge.ordinal()] = null;
		this.enumToFont[UIFont.Dialogue.ordinal()] = this.zomboidDialogue;
		this.enumToFont[UIFont.Intro.ordinal()] = this.intro;
		this.enumToFont[UIFont.Handwritten.ordinal()] = this.handwritten;
		this.enumToFont[UIFont.DebugConsole.ordinal()] = this.debugConsole;
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
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			return 0;
		} else {
			AngelCodeFont angelCodeFont = this.getFontFromEnum(uIFont);
			return angelCodeFont.getHeight(string);
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
}
