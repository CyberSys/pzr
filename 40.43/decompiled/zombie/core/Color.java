package zombie.core;

import java.io.Serializable;

public class Color implements Serializable {
   private static final long serialVersionUID = 1393939L;
   public static final Color transparent = new Color(0.0F, 0.0F, 0.0F, 0.0F);
   public static final Color white = new Color(1.0F, 1.0F, 1.0F, 1.0F);
   public static final Color yellow = new Color(1.0F, 1.0F, 0.0F, 1.0F);
   public static final Color red = new Color(1.0F, 0.0F, 0.0F, 1.0F);
   public static final Color purple = new Color(196.0F, 0.0F, 171.0F);
   public static final Color blue = new Color(0.0F, 0.0F, 1.0F, 1.0F);
   public static final Color green = new Color(0.0F, 1.0F, 0.0F, 1.0F);
   public static final Color black = new Color(0.0F, 0.0F, 0.0F, 1.0F);
   public static final Color gray = new Color(0.5F, 0.5F, 0.5F, 1.0F);
   public static final Color cyan = new Color(0.0F, 1.0F, 1.0F, 1.0F);
   public static final Color darkGray = new Color(0.3F, 0.3F, 0.3F, 1.0F);
   public static final Color lightGray = new Color(0.7F, 0.7F, 0.7F, 1.0F);
   public static final Color pink = new Color(255, 175, 175, 255);
   public static final Color orange = new Color(255, 200, 0, 255);
   public static final Color magenta = new Color(255, 0, 255, 255);
   public static final Color darkGreen = new Color(22, 113, 20, 255);
   public static final Color lightGreen = new Color(55, 148, 53, 255);
   public float a = 1.0F;
   public float b;
   public float g;
   public float r;

   public Color(Color var1) {
      if (var1 == null) {
         this.r = 0.0F;
         this.g = 0.0F;
         this.b = 0.0F;
         this.a = 1.0F;
      } else {
         this.r = var1.r;
         this.g = var1.g;
         this.b = var1.b;
         this.a = var1.a;
      }
   }

   public Color(float var1, float var2, float var3) {
      this.r = var1;
      this.g = var2;
      this.b = var3;
      this.a = 1.0F;
   }

   public Color(float var1, float var2, float var3, float var4) {
      this.r = Math.min(var1, 1.0F);
      this.g = Math.min(var2, 1.0F);
      this.b = Math.min(var3, 1.0F);
      this.a = Math.min(var4, 1.0F);
   }

   public Color(Color var1, Color var2, float var3) {
      float var4 = (var2.r - var1.r) * var3;
      float var5 = (var2.g - var1.g) * var3;
      float var6 = (var2.b - var1.b) * var3;
      float var7 = (var2.a - var1.a) * var3;
      this.r = var1.r + var4;
      this.g = var1.g + var5;
      this.b = var1.b + var6;
      this.a = var1.a + var7;
   }

   public void setColor(Color var1, Color var2, float var3) {
      float var4 = (var2.r - var1.r) * var3;
      float var5 = (var2.g - var1.g) * var3;
      float var6 = (var2.b - var1.b) * var3;
      float var7 = (var2.a - var1.a) * var3;
      this.r = var1.r + var4;
      this.g = var1.g + var5;
      this.b = var1.b + var6;
      this.a = var1.a + var7;
   }

   public Color(int var1, int var2, int var3) {
      this.r = (float)var1 / 255.0F;
      this.g = (float)var2 / 255.0F;
      this.b = (float)var3 / 255.0F;
      this.a = 1.0F;
   }

   public Color(int var1, int var2, int var3, int var4) {
      this.r = (float)var1 / 255.0F;
      this.g = (float)var2 / 255.0F;
      this.b = (float)var3 / 255.0F;
      this.a = (float)var4 / 255.0F;
   }

   public Color(int var1) {
      int var2 = (var1 & 16711680) >> 16;
      int var3 = (var1 & '\uff00') >> 8;
      int var4 = var1 & 255;
      int var5 = (var1 & -16777216) >> 24;
      if (var5 < 0) {
         var5 += 256;
      }

      if (var5 == 0) {
         var5 = 255;
      }

      this.r = (float)var4 / 255.0F;
      this.g = (float)var3 / 255.0F;
      this.b = (float)var2 / 255.0F;
      this.a = (float)var5 / 255.0F;
   }

   public void fromColor(int var1) {
      int var2 = (var1 & 16711680) >> 16;
      int var3 = (var1 & '\uff00') >> 8;
      int var4 = var1 & 255;
      int var5 = (var1 & -16777216) >> 24;
      if (var5 < 0) {
         var5 += 256;
      }

      if (var5 == 0) {
         var5 = 255;
      }

      this.r = (float)var4 / 255.0F;
      this.g = (float)var3 / 255.0F;
      this.b = (float)var2 / 255.0F;
      this.a = (float)var5 / 255.0F;
   }

   public static Color decode(String var0) {
      return new Color(Integer.decode(var0));
   }

   public void add(Color var1) {
      this.r += var1.r;
      this.g += var1.g;
      this.b += var1.b;
      this.a += var1.a;
   }

   public Color addToCopy(Color var1) {
      Color var2 = new Color(this.r, this.g, this.b, this.a);
      var2.r += var1.r;
      var2.g += var1.g;
      var2.b += var1.b;
      var2.a += var1.a;
      return var2;
   }

   public Color brighter() {
      return this.brighter(0.2F);
   }

   public Color brighter(float var1) {
      this.r = this.r += var1;
      this.g = this.g += var1;
      this.b = this.b += var1;
      return this;
   }

   public Color darker() {
      return this.darker(0.5F);
   }

   public Color darker(float var1) {
      this.r = this.r -= var1;
      this.g = this.g -= var1;
      this.b = this.b -= var1;
      return this;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Color)) {
         return false;
      } else {
         Color var2 = (Color)var1;
         return var2.r == this.r && var2.g == this.g && var2.b == this.b && var2.a == this.a;
      }
   }

   public void set(Color var1) {
      this.r = var1.r;
      this.g = var1.g;
      this.b = var1.b;
      this.a = var1.a;
   }

   public int getAlpha() {
      return (int)(this.a * 255.0F);
   }

   public float getAlphaFloat() {
      return this.a;
   }

   public float getRedFloat() {
      return this.r;
   }

   public float getGreenFloat() {
      return this.g;
   }

   public float getBlueFloat() {
      return this.b;
   }

   public int getAlphaByte() {
      return (int)(this.a * 255.0F);
   }

   public int getBlue() {
      return (int)(this.b * 255.0F);
   }

   public int getBlueByte() {
      return (int)(this.b * 255.0F);
   }

   public int getGreen() {
      return (int)(this.g * 255.0F);
   }

   public int getGreenByte() {
      return (int)(this.g * 255.0F);
   }

   public int getRed() {
      return (int)(this.r * 255.0F);
   }

   public int getRedByte() {
      return (int)(this.r * 255.0F);
   }

   public int hashCode() {
      return (int)(this.r + this.g + this.b + this.a) * 255;
   }

   public Color multiply(Color var1) {
      return new Color(this.r * var1.r, this.g * var1.g, this.b * var1.b, this.a * var1.a);
   }

   public void scale(float var1) {
      this.r *= var1;
      this.g *= var1;
      this.b *= var1;
      this.a *= var1;
   }

   public Color scaleCopy(float var1) {
      Color var2 = new Color(this.r, this.g, this.b, this.a);
      var2.r *= var1;
      var2.g *= var1;
      var2.b *= var1;
      var2.a *= var1;
      return var2;
   }

   public String toString() {
      return "Color (" + this.r + "," + this.g + "," + this.b + "," + this.a + ")";
   }

   public void interp(Color var1, float var2, Color var3) {
      float var4 = var1.r - this.r;
      float var5 = var1.g - this.g;
      float var6 = var1.b - this.b;
      float var7 = var1.a - this.a;
      var4 *= var2;
      var5 *= var2;
      var6 *= var2;
      var7 *= var2;
      var3.r = this.r + var4;
      var3.g = this.g + var5;
      var3.b = this.b + var6;
      var3.a = this.a + var7;
   }

   public static Integer[] HSBtoRGB(float var0, float var1, float var2) {
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      if (var1 == 0.0F) {
         var3 = var4 = var5 = (int)(var2 * 255.0F + 0.5F);
      } else {
         float var6 = (var0 - (float)Math.floor((double)var0)) * 6.0F;
         float var7 = var6 - (float)Math.floor((double)var6);
         float var8 = var2 * (1.0F - var1);
         float var9 = var2 * (1.0F - var1 * var7);
         float var10 = var2 * (1.0F - var1 * (1.0F - var7));
         switch((int)var6) {
         case 0:
            var3 = (int)(var2 * 255.0F + 0.5F);
            var4 = (int)(var10 * 255.0F + 0.5F);
            var5 = (int)(var8 * 255.0F + 0.5F);
            break;
         case 1:
            var3 = (int)(var9 * 255.0F + 0.5F);
            var4 = (int)(var2 * 255.0F + 0.5F);
            var5 = (int)(var8 * 255.0F + 0.5F);
            break;
         case 2:
            var3 = (int)(var8 * 255.0F + 0.5F);
            var4 = (int)(var2 * 255.0F + 0.5F);
            var5 = (int)(var10 * 255.0F + 0.5F);
            break;
         case 3:
            var3 = (int)(var8 * 255.0F + 0.5F);
            var4 = (int)(var9 * 255.0F + 0.5F);
            var5 = (int)(var2 * 255.0F + 0.5F);
            break;
         case 4:
            var3 = (int)(var10 * 255.0F + 0.5F);
            var4 = (int)(var8 * 255.0F + 0.5F);
            var5 = (int)(var2 * 255.0F + 0.5F);
            break;
         case 5:
            var3 = (int)(var2 * 255.0F + 0.5F);
            var4 = (int)(var8 * 255.0F + 0.5F);
            var5 = (int)(var9 * 255.0F + 0.5F);
         }
      }

      Integer[] var11 = new Integer[]{var3, var4, var5};
      return var11;
   }
}
