package zombie.core;

public enum Language {
   EN(0, "English", "UTF-8"),
   FR(1, "Francais", "Cp1252"),
   DE(2, "Deutsch", "Cp1252"),
   ES(3, "Espanol (ES)", "Cp1252"),
   NO(4, "Norsk", "Cp1252"),
   RU(5, "Russian", "Cp1251"),
   PL(6, "Polish", "Cp1250"),
   IT(7, "Italiano", "Cp1252"),
   NL(8, "Nederlands", "Cp1252"),
   AF(9, "Afrikaans", "Cp1252"),
   CS(10, "Czech", "Cp1250"),
   DA(11, "Danish", "Cp1252"),
   PT(12, "Portuguese", "Cp1252"),
   TR(13, "Turkish", "Cp1254"),
   HU(14, "Hungarian", "Cp1250"),
   KO(15, "Korean", "UTF-16"),
   JP(16, "Japanese", "UTF-8"),
   CH(17, "Chinese", "Big5"),
   CN(18, "Simplified Chinese", "UTF-8"),
   AR(19, "Espanol (AR)", "Cp1252"),
   PTBR(20, "Brazilian Portuguese", "Cp1252"),
   TH(21, "Thai", "UTF-8"),
   EE(22, "Estonian", "Cp1252");

   private int index;
   private String text;
   private String charset;

   private Language(int var3, String var4, String var5) {
      this.index = var3;
      this.text = var4;
      this.charset = var5;
   }

   public int index() {
      return this.index;
   }

   public String text() {
      return this.text;
   }

   public String charset() {
      return this.charset;
   }

   public static Language fromIndex(int var0) {
      return ((Language[])Language.class.getEnumConstants())[var0];
   }

   public static Language FromString(String var0) {
      try {
         return valueOf(var0);
      } catch (Exception var2) {
         return EN;
      }
   }
}
