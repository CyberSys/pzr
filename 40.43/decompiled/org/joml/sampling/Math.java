package org.joml.sampling;

class Math {
   static final double PI = 3.141592653589793D;
   static final double PI2 = 6.283185307179586D;
   static final double PIHalf = 1.5707963267948966D;
   static final double PI_INV = 0.3183098861837907D;
   private static final double s5 = Double.longBitsToDouble(4523227044276562163L);
   private static final double s4 = Double.longBitsToDouble(-4671934770969572232L);
   private static final double s3 = Double.longBitsToDouble(4575957211482072852L);
   private static final double s2 = Double.longBitsToDouble(-4628199223918090387L);
   private static final double s1 = Double.longBitsToDouble(4607182418589157889L);

   static double sin_roquen_9(double var0) {
      double var2 = java.lang.Math.rint(var0 * 0.3183098861837907D);
      double var4 = var0 - var2 * 3.141592653589793D;
      double var6 = (double)(1 - 2 * ((int)var2 & 1));
      double var8 = var4 * var4;
      var4 = var6 * var4;
      double var10 = s5;
      var10 = var10 * var8 + s4;
      var10 = var10 * var8 + s3;
      var10 = var10 * var8 + s2;
      var10 = var10 * var8 + s1;
      return var4 * var10;
   }

   static double acos(double var0) {
      return (-0.6981317007977321D * var0 * var0 - 0.8726646259971648D) * var0 + 1.5707963267948966D;
   }

   static double sqrt(double var0) {
      return java.lang.Math.sqrt(var0);
   }

   static float min(float var0, float var1) {
      return var0 < var1 ? var0 : var1;
   }

   static int min(int var0, int var1) {
      return var0 < var1 ? var0 : var1;
   }

   static int max(int var0, int var1) {
      return var0 > var1 ? var0 : var1;
   }

   static float max(float var0, float var1) {
      return var0 > var1 ? var0 : var1;
   }

   static float abs(float var0) {
      return java.lang.Math.abs(var0);
   }
}
