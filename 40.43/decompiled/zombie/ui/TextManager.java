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

   public void DrawString(double var1, double var3, String var5) {
      this.font.drawString((float)var1, (float)var3, var5, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void DrawString(double var1, double var3, String var5, double var6, double var8, double var10, double var12) {
      this.font.drawString((float)var1, (float)var3, var5, (float)var6, (float)var8, (float)var10, (float)var12);
   }

   public void DrawString(UIFont var1, double var2, double var4, float var6, String var7, double var8, double var10, double var12, double var14) {
      AngelCodeFont var16 = this.getFontFromEnum(var1);
      var16.zoom = var6;
      var16.drawString((float)var2, (float)var4, var7, (float)var8, (float)var10, (float)var12, (float)var14);
   }

   public void DrawString(UIFont var1, double var2, double var4, String var6, double var7, double var9, double var11, double var13) {
      AngelCodeFont var15 = this.getFontFromEnum(var1);
      var15.drawString((float)var2, (float)var4, var6, (float)var7, (float)var9, (float)var11, (float)var13);
   }

   public void DrawStringUntrimmed(UIFont var1, double var2, double var4, String var6, double var7, double var9, double var11, double var13) {
      AngelCodeFont var15 = this.getFontFromEnum(var1);
      var15.drawString((float)var2, (float)var4, var6, (float)var7, (float)var9, (float)var11, (float)var13);
   }

   public void DrawStringCentre(double var1, double var3, String var5, double var6, double var8, double var10, double var12) {
      var1 -= (double)(this.font.getWidth(var5) / 2);
      this.font.drawString((float)var1, (float)var3, var5, (float)var6, (float)var8, (float)var10, (float)var12);
   }

   public void DrawStringCentre(UIFont var1, double var2, double var4, String var6, double var7, double var9, double var11, double var13) {
      AngelCodeFont var15 = this.getFontFromEnum(var1);
      var2 -= (double)(var15.getWidth(var6) / 2);
      var15.drawString((float)var2, (float)var4, var6, (float)var7, (float)var9, (float)var11, (float)var13);
   }

   public void DrawStringCentreDefered(UIFont var1, double var2, double var4, String var6, double var7, double var9, double var11, double var13) {
      this.todoTextList.add(new TextManager.DeferedTextDraw(var1, var2, var4, var6, var7, var9, var11, var13));
   }

   public void DrawTextFromGameWorld() {
      for(int var1 = 0; var1 < this.todoTextList.size(); ++var1) {
         TextManager.DeferedTextDraw var2 = (TextManager.DeferedTextDraw)this.todoTextList.get(var1);
         this.DrawStringCentre(var2.font, var2.x, var2.y, var2.str, var2.r, var2.g, var2.b, var2.a);
      }

      this.todoTextList.clear();
   }

   public void DrawStringRight(double var1, double var3, String var5, double var6, double var8, double var10, double var12) {
      var1 -= (double)this.font.getWidth(var5);
      this.font.drawString((float)var1, (float)var3, var5, (float)var6, (float)var8, (float)var10, (float)var12);
   }

   public TextDrawObject GetDrawTextObject(String var1, int var2, boolean var3) {
      TextDrawObject var4 = new TextDrawObject();
      return var4;
   }

   public void DrawTextObject(double var1, double var3, TextDrawObject var5) {
   }

   public void DrawStringBBcode(UIFont var1, double var2, double var4, String var6, double var7, double var9, double var11, double var13) {
   }

   public AngelCodeFont getNormalFromFontSize(int var1) {
      return this.normal[var1 - 11];
   }

   public AngelCodeFont getFontFromEnum(UIFont var1) {
      if (var1 == null) {
         return this.font;
      } else {
         AngelCodeFont var2 = this.enumToFont[var1.ordinal()];
         return var2 == null ? this.font : var2;
      }
   }

   public int getFontHeight(UIFont var1) {
      AngelCodeFont var2 = this.getFontFromEnum(var1);
      return var2.getLineHeight();
   }

   public void DrawStringRight(UIFont var1, double var2, double var4, String var6, double var7, double var9, double var11, double var13) {
      AngelCodeFont var15 = this.getFontFromEnum(var1);
      var2 -= (double)var15.getWidth(var6);
      var15.drawString((float)var2, (float)var4, var6, (float)var7, (float)var9, (float)var11, (float)var13);
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
         String var1 = (new File("")).getAbsolutePath().replaceAll("\\\\", "/");
         String var2 = var1 + "/media/fonts/zomboidSmall.fnt";
         String var3 = var1 + "/media/fonts/zomboidSmall_0.png";
         if (var2.startsWith("/")) {
            var2 = "/" + var2;
         }

         this.debugConsole = new AngelCodeFont(var2, var3);
      } finally {
         ZomboidFileSystem.instance.IgnoreActiveFileMap = false;
      }

      for(int var7 = 0; var7 < this.normal.length; ++var7) {
         this.normal[var7] = new AngelCodeFont("media/fonts/zomboidNormal" + (var7 + 11) + ".fnt", "media/fonts/zomboidNormal" + (var7 + 11) + "_0");
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

   public int MeasureStringX(UIFont var1, String var2) {
      if (GameServer.bServer && !ServerGUI.isCreated()) {
         return 0;
      } else if (var2 == null) {
         return 0;
      } else {
         AngelCodeFont var3 = this.getFontFromEnum(var1);
         return var3.getWidth(var2);
      }
   }

   public int MeasureStringY(UIFont var1, String var2) {
      if (GameServer.bServer && !ServerGUI.isCreated()) {
         return 0;
      } else {
         AngelCodeFont var3 = this.getFontFromEnum(var1);
         return var3.getHeight(var2);
      }
   }

   public int MeasureFont(UIFont var1) {
      if (var1 == UIFont.Small) {
         return 10;
      } else if (var1 == UIFont.Dialogue) {
         return 20;
      } else if (var1 == UIFont.Medium) {
         return 20;
      } else if (var1 == UIFont.Large) {
         return 24;
      } else if (var1 == UIFont.Massive) {
         return 30;
      } else if (var1 == UIFont.MainMenu1) {
         return 30;
      } else {
         return var1 == UIFont.MainMenu2 ? 30 : this.getFontFromEnum(var1).getLineHeight();
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

      public DeferedTextDraw(UIFont var1, double var2, double var4, String var6, double var7, double var9, double var11, double var13) {
         this.font = var1;
         this.x = var2;
         this.y = var4;
         this.str = var6;
         this.r = var7;
         this.g = var9;
         this.b = var11;
         this.a = var13;
      }
   }
}
