package org.joml;

public class Math {
   public static final double PI = 3.141592653589793D;
   static final double PI2 = 6.283185307179586D;
   static final double PIHalf = 1.5707963267948966D;
   static final double PI_4 = 0.7853981633974483D;
   static final double PI_INV = 0.3183098861837907D;
   private static final int lookupBits;
   private static final int lookupTableSize;
   private static final int lookupTableSizeMinus1;
   private static final int lookupTableSizeWithMargin;
   private static final double pi2OverLookupSize;
   private static final double lookupSizeOverPi2;
   private static final float[] sinTable;
   private static final double c1;
   private static final double c2;
   private static final double c3;
   private static final double c4;
   private static final double c5;
   private static final double c6;
   private static final double c7;
   private static final double s5;
   private static final double s4;
   private static final double s3;
   private static final double s2;
   private static final double s1;
   private static final double k1;
   private static final double k2;
   private static final double k3;
   private static final double k4;
   private static final double k5;
   private static final double k6;
   private static final double k7;

   static double sin_theagentd_arith(double var0) {
      double var2 = floor((var0 + 0.7853981633974483D) * 0.3183098861837907D);
      double var4 = var0 - var2 * 3.141592653589793D;
      double var6 = (double)(((int)var2 & 1) * -2 + 1);
      double var8 = var4 * var4;
      double var12 = var4 * var8;
      double var10 = var4 + var12 * c1;
      var12 *= var8;
      var10 += var12 * c2;
      var12 *= var8;
      var10 += var12 * c3;
      var12 *= var8;
      var10 += var12 * c4;
      var12 *= var8;
      var10 += var12 * c5;
      var12 *= var8;
      var10 += var12 * c6;
      var12 *= var8;
      var10 += var12 * c7;
      return var6 * var10;
   }

   static double sin_roquen_arith(double var0) {
      double var2 = floor((var0 + 0.7853981633974483D) * 0.3183098861837907D);
      double var4 = var0 - var2 * 3.141592653589793D;
      double var6 = (double)(((int)var2 & 1) * -2 + 1);
      double var8 = var4 * var4;
      var4 = var6 * var4;
      double var10 = c7;
      var10 = var10 * var8 + c6;
      var10 = var10 * var8 + c5;
      var10 = var10 * var8 + c4;
      var10 = var10 * var8 + c3;
      var10 = var10 * var8 + c2;
      var10 = var10 * var8 + c1;
      return var4 + var4 * var8 * var10;
   }

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

   static double sin_roquen_newk(double var0) {
      double var2 = java.lang.Math.rint(var0 * 0.3183098861837907D);
      double var4 = var0 - var2 * 3.141592653589793D;
      double var6 = (double)(1 - 2 * ((int)var2 & 1));
      double var8 = var4 * var4;
      var4 = var6 * var4;
      double var10 = k7;
      var10 = var10 * var8 + k6;
      var10 = var10 * var8 + k5;
      var10 = var10 * var8 + k4;
      var10 = var10 * var8 + k3;
      var10 = var10 * var8 + k2;
      var10 = var10 * var8 + k1;
      return var4 + var4 * var8 * var10;
   }

   static double sin_theagentd_lookup(double var0) {
      float var2 = (float)(var0 * lookupSizeOverPi2);
      int var3 = (int)java.lang.Math.floor((double)var2);
      float var4 = var2 - (float)var3;
      int var5 = var3 & lookupTableSizeMinus1;
      float var6 = sinTable[var5];
      float var7 = sinTable[var5 + 1];
      return (double)(var6 + (var7 - var6) * var4);
   }

   public static double sin(double var0) {
      if (Options.FASTMATH) {
         return Options.SIN_LOOKUP ? sin_theagentd_lookup(var0) : sin_roquen_newk(var0);
      } else {
         return java.lang.Math.sin(var0);
      }
   }

   public static double cos(double var0) {
      return Options.FASTMATH ? sin(var0 + 1.5707963267948966D) : java.lang.Math.cos(var0);
   }

   public static double sqrt(double var0) {
      return java.lang.Math.sqrt(var0);
   }

   public static double tan(double var0) {
      return java.lang.Math.tan(var0);
   }

   public static double acos(double var0) {
      return java.lang.Math.acos(var0);
   }

   public static double atan2(double var0, double var2) {
      return java.lang.Math.atan2(var0, var2);
   }

   public static double asin(double var0) {
      return java.lang.Math.asin(var0);
   }

   public static double abs(double var0) {
      return java.lang.Math.abs(var0);
   }

   public static float abs(float var0) {
      return java.lang.Math.abs(var0);
   }

   public static int max(int var0, int var1) {
      return java.lang.Math.max(var0, var1);
   }

   public static int min(int var0, int var1) {
      return java.lang.Math.min(var0, var1);
   }

   public static float min(float var0, float var1) {
      return var0 < var1 ? var0 : var1;
   }

   public static float max(float var0, float var1) {
      return var0 > var1 ? var0 : var1;
   }

   public static double min(double var0, double var2) {
      return var0 < var2 ? var0 : var2;
   }

   public static double max(double var0, double var2) {
      return var0 > var2 ? var0 : var2;
   }

   public static double toRadians(double var0) {
      return java.lang.Math.toRadians(var0);
   }

   public static double toDegrees(double var0) {
      return java.lang.Math.toDegrees(var0);
   }

   public static double floor(double var0) {
      return java.lang.Math.floor(var0);
   }

   public static double exp(double var0) {
      return java.lang.Math.exp(var0);
   }

   static {
      lookupBits = Options.SIN_LOOKUP_BITS;
      lookupTableSize = 1 << lookupBits;
      lookupTableSizeMinus1 = lookupTableSize - 1;
      lookupTableSizeWithMargin = lookupTableSize + 1;
      pi2OverLookupSize = 6.283185307179586D / (double)lookupTableSize;
      lookupSizeOverPi2 = (double)lookupTableSize / 6.283185307179586D;
      if (Options.FASTMATH && Options.SIN_LOOKUP) {
         sinTable = new float[lookupTableSizeWithMargin];

         for(int var0 = 0; var0 < lookupTableSizeWithMargin; ++var0) {
            double var1 = (double)var0 * pi2OverLookupSize;
            sinTable[var0] = (float)java.lang.Math.sin(var1);
         }
      } else {
         sinTable = null;
      }

      c1 = Double.longBitsToDouble(-4628199217061079772L);
      c2 = Double.longBitsToDouble(4575957461383582011L);
      c3 = Double.longBitsToDouble(-4671919876300759001L);
      c4 = Double.longBitsToDouble(4523617214285661942L);
      c5 = Double.longBitsToDouble(-4730215272828025532L);
      c6 = Double.longBitsToDouble(4460272573143870633L);
      c7 = Double.longBitsToDouble(-4797767418267846529L);
      s5 = Double.longBitsToDouble(4523227044276562163L);
      s4 = Double.longBitsToDouble(-4671934770969572232L);
      s3 = Double.longBitsToDouble(4575957211482072852L);
      s2 = Double.longBitsToDouble(-4628199223918090387L);
      s1 = Double.longBitsToDouble(4607182418589157889L);
      k1 = Double.longBitsToDouble(-4628199217061079959L);
      k2 = Double.longBitsToDouble(4575957461383549981L);
      k3 = Double.longBitsToDouble(-4671919876307284301L);
      k4 = Double.longBitsToDouble(4523617213632129738L);
      k5 = Double.longBitsToDouble(-4730215344060517252L);
      k6 = Double.longBitsToDouble(4460268259291226124L);
      k7 = Double.longBitsToDouble(-4798040743777455072L);
   }
}
