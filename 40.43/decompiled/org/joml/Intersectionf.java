package org.joml;

public class Intersectionf {
   public static final int POINT_ON_TRIANGLE_VERTEX = 0;
   public static final int POINT_ON_TRIANGLE_EDGE = 1;
   public static final int POINT_ON_TRIANGLE_FACE = 2;
   public static final int AAR_SIDE_MINX = 0;
   public static final int AAR_SIDE_MINY = 1;
   public static final int AAR_SIDE_MAXX = 2;
   public static final int AAR_SIDE_MAXY = 3;
   public static final int OUTSIDE = -1;
   public static final int ONE_INTERSECTION = 1;
   public static final int TWO_INTERSECTION = 2;
   public static final int INSIDE = 3;

   public static boolean testPlaneSphere(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = (float)Math.sqrt((double)(var0 * var0 + var1 * var1 + var2 * var2));
      float var9 = (var0 * var4 + var1 * var5 + var2 * var6 + var3) / var8;
      return -var7 <= var9 && var9 <= var7;
   }

   public static boolean intersectPlaneSphere(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, Vector4f var8) {
      float var9 = 1.0F / (float)Math.sqrt((double)(var0 * var0 + var1 * var1 + var2 * var2));
      float var10 = (var0 * var4 + var1 * var5 + var2 * var6 + var3) * var9;
      if (-var7 <= var10 && var10 <= var7) {
         var8.x = var4 + var10 * var0 * var9;
         var8.y = var5 + var10 * var1 * var9;
         var8.z = var6 + var10 * var2 * var9;
         var8.w = (float)Math.sqrt((double)(var7 * var7 - var10 * var10));
         return true;
      } else {
         return false;
      }
   }

   public static boolean testAabPlane(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10;
      float var13;
      if (var6 > 0.0F) {
         var10 = var3;
         var13 = var0;
      } else {
         var10 = var0;
         var13 = var3;
      }

      float var11;
      float var14;
      if (var7 > 0.0F) {
         var11 = var4;
         var14 = var1;
      } else {
         var11 = var1;
         var14 = var4;
      }

      float var12;
      float var15;
      if (var8 > 0.0F) {
         var12 = var5;
         var15 = var2;
      } else {
         var12 = var2;
         var15 = var5;
      }

      float var16 = var9 + var6 * var13 + var7 * var14 + var8 * var15;
      float var17 = var9 + var6 * var10 + var7 * var11 + var8 * var12;
      return var16 <= 0.0F && var17 >= 0.0F;
   }

   public static boolean testAabPlane(Vector3fc var0, Vector3fc var1, float var2, float var3, float var4, float var5) {
      return testAabPlane(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2, var3, var4, var5);
   }

   public static boolean testAabAab(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11) {
      return var3 >= var6 && var4 >= var7 && var5 >= var8 && var0 <= var9 && var1 <= var10 && var2 <= var11;
   }

   public static boolean testAabAab(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3) {
      return testAabAab(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z());
   }

   public static boolean intersectSphereSphere(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, Vector4f var8) {
      float var9 = var4 - var0;
      float var10 = var5 - var1;
      float var11 = var6 - var2;
      float var12 = var9 * var9 + var10 * var10 + var11 * var11;
      float var13 = 0.5F + (var3 - var7) / var12;
      float var14 = var3 - var13 * var13 * var12;
      if (var14 >= 0.0F) {
         var8.x = var0 + var13 * var9;
         var8.y = var1 + var13 * var10;
         var8.z = var2 + var13 * var11;
         var8.w = (float)Math.sqrt((double)var14);
         return true;
      } else {
         return false;
      }
   }

   public static boolean intersectSphereSphere(Vector3fc var0, float var1, Vector3fc var2, float var3, Vector4f var4) {
      return intersectSphereSphere(var0.x(), var0.y(), var0.z(), var1, var2.x(), var2.y(), var2.z(), var3, var4);
   }

   public static boolean testSphereSphere(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = var4 - var0;
      float var9 = var5 - var1;
      float var10 = var6 - var2;
      float var11 = var8 * var8 + var9 * var9 + var10 * var10;
      float var12 = 0.5F + (var3 - var7) / var11;
      float var13 = var3 - var12 * var12 * var11;
      return var13 >= 0.0F;
   }

   public static boolean testSphereSphere(Vector3fc var0, float var1, Vector3fc var2, float var3) {
      return testSphereSphere(var0.x(), var0.y(), var0.z(), var1, var2.x(), var2.y(), var2.z(), var3);
   }

   public static float distancePointPlane(float var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = (float)Math.sqrt((double)(var3 * var3 + var4 * var4 + var5 * var5));
      return (var3 * var0 + var4 * var1 + var5 * var2 + var6) / var7;
   }

   public static float distancePointPlane(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11) {
      float var12 = var7 - var4;
      float var13 = var11 - var5;
      float var14 = var10 - var4;
      float var15 = var8 - var5;
      float var16 = var9 - var3;
      float var17 = var6 - var3;
      float var18 = var12 * var13 - var14 * var15;
      float var19 = var15 * var16 - var13 * var17;
      float var20 = var17 * var14 - var16 * var12;
      float var21 = -(var18 * var3 + var19 * var4 + var20 * var5);
      return distancePointPlane(var0, var1, var2, var18, var19, var20, var21);
   }

   public static float intersectRayPlane(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12) {
      float var13 = var9 * var3 + var10 * var4 + var11 * var5;
      if (var13 < var12) {
         float var14 = ((var6 - var0) * var9 + (var7 - var1) * var10 + (var8 - var2) * var11) / var13;
         if (var14 >= 0.0F) {
            return var14;
         }
      }

      return -1.0F;
   }

   public static float intersectRayPlane(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3, float var4) {
      return intersectRayPlane(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4);
   }

   public static float intersectRayPlane(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      float var11 = var6 * var3 + var7 * var4 + var8 * var5;
      if (var11 < 0.0F) {
         float var12 = -(var6 * var0 + var7 * var1 + var8 * var2 + var9) / var11;
         if (var12 >= 0.0F) {
            return var12;
         }
      }

      return -1.0F;
   }

   public static boolean testAabSphere(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = var9;
      float var11;
      if (var6 < var0) {
         var11 = var6 - var0;
         var10 = var9 - var11 * var11;
      } else if (var6 > var3) {
         var11 = var6 - var3;
         var10 = var9 - var11 * var11;
      }

      if (var7 < var1) {
         var11 = var7 - var1;
         var10 -= var11 * var11;
      } else if (var7 > var4) {
         var11 = var7 - var4;
         var10 -= var11 * var11;
      }

      if (var8 < var2) {
         var11 = var8 - var2;
         var10 -= var11 * var11;
      } else if (var8 > var5) {
         var11 = var8 - var5;
         var10 -= var11 * var11;
      }

      return var10 >= 0.0F;
   }

   public static boolean testAabSphere(Vector3fc var0, Vector3fc var1, Vector3fc var2, float var3) {
      return testAabSphere(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public static int findClosestPointOnTriangle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, Vector3f var12) {
      float var13 = var0 - var9;
      float var14 = var1 - var10;
      float var15 = var2 - var11;
      float var16 = var3 - var9;
      float var17 = var4 - var10;
      float var18 = var5 - var11;
      float var19 = var6 - var9;
      float var20 = var7 - var10;
      float var21 = var8 - var11;
      float var22 = var16 - var13;
      float var23 = var17 - var14;
      float var24 = var18 - var15;
      float var25 = var19 - var13;
      float var26 = var20 - var14;
      float var27 = var21 - var15;
      float var28 = -(var22 * var13 + var23 * var14 + var24 * var15);
      float var29 = -(var25 * var13 + var26 * var14 + var27 * var15);
      if (var28 <= 0.0F && var29 <= 0.0F) {
         var12.set(var0, var1, var2);
         return 0;
      } else {
         float var30 = -(var22 * var16 + var23 * var17 + var24 * var18);
         float var31 = -(var25 * var16 + var26 * var17 + var27 * var18);
         if (var30 >= 0.0F && var31 <= var30) {
            var12.set(var3, var4, var5);
            return 0;
         } else {
            float var32 = var28 * var31 - var30 * var29;
            float var33;
            if (var32 <= 0.0F && var28 >= 0.0F && var30 <= 0.0F) {
               var33 = var28 / (var28 - var30);
               var12.set(var0 + var22 * var33, var1 + var23 * var33, var2 * var24 * var33);
               return 1;
            } else {
               var33 = -(var22 * var19 + var23 * var20 + var24 * var21);
               float var34 = -(var25 * var19 + var26 * var20 + var27 * var21);
               if (var34 >= 0.0F && var33 <= var34) {
                  var12.set(var6, var7, var8);
                  return 0;
               } else {
                  float var35 = var33 * var29 - var28 * var34;
                  float var36;
                  if (var35 <= 0.0F && var29 >= 0.0F && var34 <= 0.0F) {
                     var36 = var29 / (var29 - var34);
                     var12.set(var0 + var25 * var36, var1 + var26 * var36, var2 + var27 * var36);
                     return 1;
                  } else {
                     var36 = var30 * var34 - var33 * var31;
                     float var37;
                     if (var36 <= 0.0F && var31 - var30 >= 0.0F && var33 - var34 >= 0.0F) {
                        var37 = (var31 - var30) / (var31 - var30 + var33 - var34);
                        var12.set(var3 + (var19 - var16) * var37, var4 + (var20 - var17) * var37, var5 + (var21 - var18) * var37);
                        return 1;
                     } else {
                        var37 = 1.0F / (var36 + var35 + var32);
                        float var38 = var35 * var37;
                        float var39 = var32 * var37;
                        var12.set(var0 + var22 * var38 + var25 * var39, var1 + var23 * var38 + var26 * var39, var2 + var24 * var38 + var27 * var39);
                        return 2;
                     }
                  }
               }
            }
         }
      }
   }

   public static int findClosestPointOnTriangle(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3, Vector3f var4) {
      return findClosestPointOnTriangle(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4);
   }

   public static int intersectSweptSphereTriangle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17, Vector4f var18) {
      float var19 = var10 - var7;
      float var20 = var11 - var8;
      float var21 = var12 - var9;
      float var22 = var13 - var7;
      float var23 = var14 - var8;
      float var24 = var15 - var9;
      float var25 = var20 * var24 - var23 * var21;
      float var26 = var21 * var22 - var24 * var19;
      float var27 = var19 * var23 - var22 * var20;
      float var28 = -(var25 * var7 + var26 * var8 + var27 * var9);
      float var29 = (float)(1.0D / Math.sqrt((double)(var25 * var25 + var26 * var26 + var27 * var27)));
      float var30 = (var25 * var0 + var26 * var1 + var27 * var2 + var28) * var29;
      float var31 = (var25 * var4 + var26 * var5 + var27 * var6) * var29;
      if (var31 < var16 && var31 > -var16) {
         return -1;
      } else {
         float var32 = (var3 - var30) / var31;
         if (var32 > var17) {
            return -1;
         } else {
            float var33 = (-var3 - var30) / var31;
            float var34 = var0 - var3 * var25 * var29 + var4 * var32;
            float var35 = var1 - var3 * var26 * var29 + var5 * var32;
            float var36 = var2 - var3 * var27 * var29 + var6 * var32;
            boolean var37 = testPointInTriangle(var34, var35, var36, var7, var8, var9, var10, var11, var12, var13, var14, var15);
            if (var37) {
               var18.x = var34;
               var18.y = var35;
               var18.z = var36;
               var18.w = var32;
               return 2;
            } else {
               byte var38 = -1;
               float var39 = var17;
               float var40 = var4 * var4 + var5 * var5 + var6 * var6;
               float var41 = var3 * var3;
               float var42 = var0 - var7;
               float var43 = var1 - var8;
               float var44 = var2 - var9;
               float var45 = 2.0F * (var4 * var42 + var5 * var43 + var6 * var44);
               float var46 = var42 * var42 + var43 * var43 + var44 * var44 - var41;
               float var47 = computeLowestRoot(var40, var45, var46, var17);
               if (var47 < var17) {
                  var18.x = var7;
                  var18.y = var8;
                  var18.z = var9;
                  var18.w = var47;
                  var39 = var47;
                  var38 = 0;
               }

               float var48 = var0 - var10;
               float var49 = var1 - var11;
               float var50 = var2 - var12;
               float var51 = var48 * var48 + var49 * var49 + var50 * var50;
               float var52 = 2.0F * (var4 * var48 + var5 * var49 + var6 * var50);
               float var53 = var51 - var41;
               float var54 = computeLowestRoot(var40, var52, var53, var39);
               if (var54 < var39) {
                  var18.x = var10;
                  var18.y = var11;
                  var18.z = var12;
                  var18.w = var54;
                  var39 = var54;
                  var38 = 0;
               }

               float var55 = var0 - var13;
               float var56 = var1 - var14;
               float var57 = var2 - var15;
               float var58 = 2.0F * (var4 * var55 + var5 * var56 + var6 * var57);
               float var59 = var55 * var55 + var56 * var56 + var57 * var57 - var41;
               float var60 = computeLowestRoot(var40, var58, var59, var39);
               if (var60 < var39) {
                  var18.x = var13;
                  var18.y = var14;
                  var18.z = var15;
                  var18.w = var60;
                  var39 = var60;
                  var38 = 0;
               }

               float var61 = var4 * var4 + var5 * var5 + var6 * var6;
               float var62 = var19 * var19 + var20 * var20 + var21 * var21;
               float var63 = var42 * var42 + var43 * var43 + var44 * var44;
               float var64 = var19 * var4 + var20 * var5 + var21 * var6;
               float var65 = var62 * -var61 + var64 * var64;
               float var66 = var19 * -var42 + var20 * -var43 + var21 * -var44;
               float var67 = var4 * -var42 + var5 * -var43 + var6 * -var44;
               float var68 = var62 * 2.0F * var67 - 2.0F * var64 * var66;
               float var69 = var62 * (var41 - var63) + var66 * var66;
               float var70 = computeLowestRoot(var65, var68, var69, var39);
               float var71 = (var64 * var70 - var66) / var62;
               if (var71 >= 0.0F && var71 <= 1.0F && var70 < var39) {
                  var18.x = var7 + var71 * var19;
                  var18.y = var8 + var71 * var20;
                  var18.z = var9 + var71 * var21;
                  var18.w = var70;
                  var39 = var70;
                  var38 = 1;
               }

               float var72 = var22 * var22 + var23 * var23 + var24 * var24;
               float var73 = var22 * var4 + var23 * var5 + var24 * var6;
               float var74 = var72 * -var61 + var73 * var73;
               float var75 = var22 * -var42 + var23 * -var43 + var24 * -var44;
               float var76 = var72 * 2.0F * var67 - 2.0F * var73 * var75;
               float var77 = var72 * (var41 - var63) + var75 * var75;
               float var78 = computeLowestRoot(var74, var76, var77, var39);
               float var79 = (var73 * var78 - var75) / var72;
               if (var79 >= 0.0F && var79 <= 1.0F && var78 < var33) {
                  var18.x = var7 + var79 * var22;
                  var18.y = var8 + var79 * var23;
                  var18.z = var9 + var79 * var24;
                  var18.w = var78;
                  var39 = var78;
                  var38 = 1;
               }

               float var80 = var13 - var10;
               float var81 = var14 - var11;
               float var82 = var15 - var12;
               float var83 = var80 * var80 + var81 * var81 + var82 * var82;
               float var85 = var80 * var4 + var81 * var5 + var82 * var6;
               float var86 = var83 * -var61 + var85 * var85;
               float var87 = var80 * -var48 + var81 * -var49 + var82 * -var50;
               float var88 = var4 * -var48 + var5 * -var49 + var6 * -var50;
               float var89 = var83 * 2.0F * var88 - 2.0F * var85 * var87;
               float var90 = var83 * (var41 - var51) + var87 * var87;
               float var91 = computeLowestRoot(var86, var89, var90, var39);
               float var92 = (var85 * var91 - var87) / var83;
               if (var92 >= 0.0F && var92 <= 1.0F && var91 < var39) {
                  var18.x = var10 + var92 * var80;
                  var18.y = var11 + var92 * var81;
                  var18.z = var12 + var92 * var82;
                  var18.w = var91;
                  var38 = 1;
               }

               return var38;
            }
         }
      }
   }

   private static float computeLowestRoot(float var0, float var1, float var2, float var3) {
      float var4 = var1 * var1 - 4.0F * var0 * var2;
      if (var4 < 0.0F) {
         return Float.MAX_VALUE;
      } else {
         float var5 = (float)Math.sqrt((double)var4);
         float var6 = (-var1 - var5) / (2.0F * var0);
         float var7 = (-var1 + var5) / (2.0F * var0);
         if (var6 > var7) {
            float var8 = var7;
            var7 = var6;
            var6 = var8;
         }

         if (var6 > 0.0F && var6 < var3) {
            return var6;
         } else {
            return var7 > 0.0F && var7 < var3 ? var7 : Float.MAX_VALUE;
         }
      }
   }

   public static boolean testPointInTriangle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11) {
      float var12 = var6 - var3;
      float var13 = var7 - var4;
      float var14 = var8 - var5;
      float var15 = var9 - var3;
      float var16 = var10 - var4;
      float var17 = var11 - var5;
      float var18 = var12 * var12 + var13 * var13 + var14 * var14;
      float var19 = var12 * var15 + var13 * var16 + var14 * var17;
      float var20 = var15 * var15 + var16 * var16 + var17 * var17;
      float var21 = var18 * var20 - var19 * var19;
      float var22 = var0 - var3;
      float var23 = var1 - var4;
      float var24 = var2 - var5;
      float var25 = var22 * var12 + var23 * var13 + var24 * var14;
      float var26 = var22 * var15 + var23 * var16 + var24 * var17;
      float var27 = var25 * var20 - var26 * var19;
      float var28 = var26 * var18 - var25 * var19;
      float var29 = var27 + var28 - var21;
      return (Float.floatToRawIntBits(var29) & ~(Float.floatToRawIntBits(var27) | Float.floatToRawIntBits(var28)) & Integer.MIN_VALUE) != 0;
   }

   public static boolean intersectRaySphere(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, Vector2f var10) {
      float var11 = var6 - var0;
      float var12 = var7 - var1;
      float var13 = var8 - var2;
      float var14 = var11 * var3 + var12 * var4 + var13 * var5;
      float var15 = var11 * var11 + var12 * var12 + var13 * var13 - var14 * var14;
      if (var15 > var9) {
         return false;
      } else {
         float var16 = (float)Math.sqrt((double)(var9 - var15));
         float var17 = var14 - var16;
         float var18 = var14 + var16;
         if (var17 < var18 && var18 >= 0.0F) {
            var10.x = var17;
            var10.y = var18;
            return true;
         } else {
            return false;
         }
      }
   }

   public static boolean intersectRaySphere(Vector3fc var0, Vector3fc var1, Vector3fc var2, float var3, Vector2f var4) {
      return intersectRaySphere(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3, var4);
   }

   public static boolean testRaySphere(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = var6 - var0;
      float var11 = var7 - var1;
      float var12 = var8 - var2;
      float var13 = var10 * var3 + var11 * var4 + var12 * var5;
      float var14 = var10 * var10 + var11 * var11 + var12 * var12 - var13 * var13;
      if (var14 > var9) {
         return false;
      } else {
         float var15 = (float)Math.sqrt((double)(var9 - var14));
         float var16 = var13 - var15;
         float var17 = var13 + var15;
         return var16 < var17 && var17 >= 0.0F;
      }
   }

   public static boolean testRaySphere(Vector3fc var0, Vector3fc var1, Vector3fc var2, float var3) {
      return testRaySphere(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public static boolean testLineSegmentSphere(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = var3 - var0;
      float var11 = var4 - var1;
      float var12 = var5 - var2;
      float var13 = (var6 - var0) * var10 + (var7 - var1) * var11 + (var8 - var2) * var12;
      float var14 = var10 * var10 + var11 * var11 + var12 * var12;
      float var15 = var13 / var14;
      float var16;
      if (var15 < 0.0F) {
         var10 = var0 - var6;
         var11 = var1 - var7;
         var12 = var2 - var8;
      } else if (var15 > 1.0F) {
         var10 = var3 - var6;
         var11 = var4 - var7;
         var12 = var5 - var8;
      } else {
         var16 = var0 + var15 * var10;
         float var17 = var1 + var15 * var11;
         float var18 = var2 + var15 * var12;
         var10 = var16 - var6;
         var11 = var17 - var7;
         var12 = var18 - var8;
      }

      var16 = var10 * var10 + var11 * var11 + var12 * var12;
      return var16 <= var9;
   }

   public static boolean testLineSegmentSphere(Vector3fc var0, Vector3fc var1, Vector3fc var2, float var3) {
      return testLineSegmentSphere(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public static boolean intersectRayAab(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, Vector2f var12) {
      float var13 = 1.0F / var3;
      float var14 = 1.0F / var4;
      float var15 = 1.0F / var5;
      float var16;
      float var17;
      if (var13 >= 0.0F) {
         var16 = (var6 - var0) * var13;
         var17 = (var9 - var0) * var13;
      } else {
         var16 = (var9 - var0) * var13;
         var17 = (var6 - var0) * var13;
      }

      float var18;
      float var19;
      if (var14 >= 0.0F) {
         var18 = (var7 - var1) * var14;
         var19 = (var10 - var1) * var14;
      } else {
         var18 = (var10 - var1) * var14;
         var19 = (var7 - var1) * var14;
      }

      if (!(var16 > var19) && !(var18 > var17)) {
         float var20;
         float var21;
         if (var15 >= 0.0F) {
            var20 = (var8 - var2) * var15;
            var21 = (var11 - var2) * var15;
         } else {
            var20 = (var11 - var2) * var15;
            var21 = (var8 - var2) * var15;
         }

         if (!(var16 > var21) && !(var20 > var17)) {
            var16 = !(var18 > var16) && !Float.isNaN(var16) ? var16 : var18;
            var17 = !(var19 < var17) && !Float.isNaN(var17) ? var17 : var19;
            var16 = var20 > var16 ? var20 : var16;
            var17 = var21 < var17 ? var21 : var17;
            if (var16 < var17 && var17 >= 0.0F) {
               var12.x = var16;
               var12.y = var17;
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean intersectRayAab(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3, Vector2f var4) {
      return intersectRayAab(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4);
   }

   public static int intersectLineSegmentAab(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, Vector2f var12) {
      float var13 = var3 - var0;
      float var14 = var4 - var1;
      float var15 = var5 - var2;
      float var16 = 1.0F / var13;
      float var17 = 1.0F / var14;
      float var18 = 1.0F / var15;
      float var19;
      float var20;
      if (var16 >= 0.0F) {
         var19 = (var6 - var0) * var16;
         var20 = (var9 - var0) * var16;
      } else {
         var19 = (var9 - var0) * var16;
         var20 = (var6 - var0) * var16;
      }

      float var21;
      float var22;
      if (var17 >= 0.0F) {
         var21 = (var7 - var1) * var17;
         var22 = (var10 - var1) * var17;
      } else {
         var21 = (var10 - var1) * var17;
         var22 = (var7 - var1) * var17;
      }

      if (!(var19 > var22) && !(var21 > var20)) {
         float var23;
         float var24;
         if (var18 >= 0.0F) {
            var23 = (var8 - var2) * var18;
            var24 = (var11 - var2) * var18;
         } else {
            var23 = (var11 - var2) * var18;
            var24 = (var8 - var2) * var18;
         }

         if (!(var19 > var24) && !(var23 > var20)) {
            var19 = !(var21 > var19) && !Float.isNaN(var19) ? var19 : var21;
            var20 = !(var22 < var20) && !Float.isNaN(var20) ? var20 : var22;
            var19 = var23 > var19 ? var23 : var19;
            var20 = var24 < var20 ? var24 : var20;
            byte var25 = -1;
            if (var19 < var20 && var19 <= 1.0F && var20 >= 0.0F) {
               if (var19 > 0.0F && var20 > 1.0F) {
                  var20 = var19;
                  var25 = 1;
               } else if (var19 < 0.0F && var20 < 1.0F) {
                  var19 = var20;
                  var25 = 1;
               } else if (var19 < 0.0F && var20 > 1.0F) {
                  var25 = 3;
               } else {
                  var25 = 2;
               }

               var12.x = var19;
               var12.y = var20;
            }

            return var25;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static int intersectLineSegmentAab(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3, Vector2f var4) {
      return intersectLineSegmentAab(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4);
   }

   public static boolean testRayAab(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11) {
      float var12 = 1.0F / var3;
      float var13 = 1.0F / var4;
      float var14 = 1.0F / var5;
      float var15;
      float var16;
      if (var12 >= 0.0F) {
         var15 = (var6 - var0) * var12;
         var16 = (var9 - var0) * var12;
      } else {
         var15 = (var9 - var0) * var12;
         var16 = (var6 - var0) * var12;
      }

      float var17;
      float var18;
      if (var13 >= 0.0F) {
         var17 = (var7 - var1) * var13;
         var18 = (var10 - var1) * var13;
      } else {
         var17 = (var10 - var1) * var13;
         var18 = (var7 - var1) * var13;
      }

      if (!(var15 > var18) && !(var17 > var16)) {
         float var19;
         float var20;
         if (var14 >= 0.0F) {
            var19 = (var8 - var2) * var14;
            var20 = (var11 - var2) * var14;
         } else {
            var19 = (var11 - var2) * var14;
            var20 = (var8 - var2) * var14;
         }

         if (!(var15 > var20) && !(var19 > var16)) {
            var15 = !(var17 > var15) && !Float.isNaN(var15) ? var15 : var17;
            var16 = !(var18 < var16) && !Float.isNaN(var16) ? var16 : var18;
            var15 = var19 > var15 ? var19 : var15;
            var16 = var20 < var16 ? var20 : var16;
            return var15 < var16 && var16 >= 0.0F;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean testRayAab(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3) {
      return testRayAab(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z());
   }

   public static boolean testRayTriangleFront(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15) {
      float var16 = var9 - var6;
      float var17 = var10 - var7;
      float var18 = var11 - var8;
      float var19 = var12 - var6;
      float var20 = var13 - var7;
      float var21 = var14 - var8;
      float var22 = var4 * var21 - var5 * var20;
      float var23 = var5 * var19 - var3 * var21;
      float var24 = var3 * var20 - var4 * var19;
      float var25 = var16 * var22 + var17 * var23 + var18 * var24;
      if (var25 < var15) {
         return false;
      } else {
         float var26 = var0 - var6;
         float var27 = var1 - var7;
         float var28 = var2 - var8;
         float var29 = var26 * var22 + var27 * var23 + var28 * var24;
         if (!(var29 < 0.0F) && !(var29 > var25)) {
            float var30 = var27 * var18 - var28 * var17;
            float var31 = var28 * var16 - var26 * var18;
            float var32 = var26 * var17 - var27 * var16;
            float var33 = var3 * var30 + var4 * var31 + var5 * var32;
            if (!(var33 < 0.0F) && !(var29 + var33 > var25)) {
               float var34 = 1.0F / var25;
               float var35 = (var19 * var30 + var20 * var31 + var21 * var32) * var34;
               return var35 >= var15;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static boolean testRayTriangleFront(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3, Vector3fc var4, float var5) {
      return testRayTriangleFront(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5);
   }

   public static boolean testRayTriangle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15) {
      float var16 = var9 - var6;
      float var17 = var10 - var7;
      float var18 = var11 - var8;
      float var19 = var12 - var6;
      float var20 = var13 - var7;
      float var21 = var14 - var8;
      float var22 = var4 * var21 - var5 * var20;
      float var23 = var5 * var19 - var3 * var21;
      float var24 = var3 * var20 - var4 * var19;
      float var25 = var16 * var22 + var17 * var23 + var18 * var24;
      if (var25 > -var15 && var25 < var15) {
         return false;
      } else {
         float var26 = var0 - var6;
         float var27 = var1 - var7;
         float var28 = var2 - var8;
         float var29 = 1.0F / var25;
         float var30 = (var26 * var22 + var27 * var23 + var28 * var24) * var29;
         if (!(var30 < 0.0F) && !(var30 > 1.0F)) {
            float var31 = var27 * var18 - var28 * var17;
            float var32 = var28 * var16 - var26 * var18;
            float var33 = var26 * var17 - var27 * var16;
            float var34 = (var3 * var31 + var4 * var32 + var5 * var33) * var29;
            if (!(var34 < 0.0F) && !(var30 + var34 > 1.0F)) {
               float var35 = (var19 * var31 + var20 * var32 + var21 * var33) * var29;
               return var35 >= var15;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static boolean testRayTriangle(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3, Vector3fc var4, float var5) {
      return testRayTriangleFront(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5);
   }

   public static float intersectRayTriangleFront(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15) {
      float var16 = var9 - var6;
      float var17 = var10 - var7;
      float var18 = var11 - var8;
      float var19 = var12 - var6;
      float var20 = var13 - var7;
      float var21 = var14 - var8;
      float var22 = var4 * var21 - var5 * var20;
      float var23 = var5 * var19 - var3 * var21;
      float var24 = var3 * var20 - var4 * var19;
      float var25 = var16 * var22 + var17 * var23 + var18 * var24;
      if (var25 <= var15) {
         return -1.0F;
      } else {
         float var26 = var0 - var6;
         float var27 = var1 - var7;
         float var28 = var2 - var8;
         float var29 = var26 * var22 + var27 * var23 + var28 * var24;
         if (!(var29 < 0.0F) && !(var29 > var25)) {
            float var30 = var27 * var18 - var28 * var17;
            float var31 = var28 * var16 - var26 * var18;
            float var32 = var26 * var17 - var27 * var16;
            float var33 = var3 * var30 + var4 * var31 + var5 * var32;
            if (!(var33 < 0.0F) && !(var29 + var33 > var25)) {
               float var34 = 1.0F / var25;
               float var35 = (var19 * var30 + var20 * var31 + var21 * var32) * var34;
               return var35;
            } else {
               return -1.0F;
            }
         } else {
            return -1.0F;
         }
      }
   }

   public static float intersectRayTriangleFront(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3, Vector3fc var4, float var5) {
      return intersectRayTriangleFront(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5);
   }

   public static float intersectRayTriangle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15) {
      float var16 = var9 - var6;
      float var17 = var10 - var7;
      float var18 = var11 - var8;
      float var19 = var12 - var6;
      float var20 = var13 - var7;
      float var21 = var14 - var8;
      float var22 = var4 * var21 - var5 * var20;
      float var23 = var5 * var19 - var3 * var21;
      float var24 = var3 * var20 - var4 * var19;
      float var25 = var16 * var22 + var17 * var23 + var18 * var24;
      if (var25 > -var15 && var25 < var15) {
         return -1.0F;
      } else {
         float var26 = var0 - var6;
         float var27 = var1 - var7;
         float var28 = var2 - var8;
         float var29 = 1.0F / var25;
         float var30 = (var26 * var22 + var27 * var23 + var28 * var24) * var29;
         if (!(var30 < 0.0F) && !(var30 > 1.0F)) {
            float var31 = var27 * var18 - var28 * var17;
            float var32 = var28 * var16 - var26 * var18;
            float var33 = var26 * var17 - var27 * var16;
            float var34 = (var3 * var31 + var4 * var32 + var5 * var33) * var29;
            if (!(var34 < 0.0F) && !(var30 + var34 > 1.0F)) {
               float var35 = (var19 * var31 + var20 * var32 + var21 * var33) * var29;
               return var35;
            } else {
               return -1.0F;
            }
         } else {
            return -1.0F;
         }
      }
   }

   public static float intersectRayTriangle(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3, Vector3fc var4, float var5) {
      return intersectRayTriangle(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5);
   }

   public static boolean testLineSegmentTriangle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15) {
      float var16 = var3 - var0;
      float var17 = var4 - var1;
      float var18 = var5 - var2;
      float var19 = intersectRayTriangle(var0, var1, var2, var16, var17, var18, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15);
      return var19 >= 0.0F && var19 <= 1.0F;
   }

   public static boolean testLineSegmentTriangle(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3, Vector3fc var4, float var5) {
      return testLineSegmentTriangle(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5);
   }

   public static boolean intersectLineSegmentTriangle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, Vector3f var16) {
      float var17 = var3 - var0;
      float var18 = var4 - var1;
      float var19 = var5 - var2;
      float var20 = intersectRayTriangle(var0, var1, var2, var17, var18, var19, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15);
      if (var20 >= 0.0F && var20 <= 1.0F) {
         var16.x = var0 + var17 * var20;
         var16.y = var1 + var18 * var20;
         var16.z = var2 + var19 * var20;
         return true;
      } else {
         return false;
      }
   }

   public static boolean intersectLineSegmentTriangle(Vector3fc var0, Vector3fc var1, Vector3fc var2, Vector3fc var3, Vector3fc var4, float var5, Vector3f var6) {
      return intersectLineSegmentTriangle(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5, var6);
   }

   public static boolean intersectLineSegmentPlane(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, Vector3f var10) {
      float var11 = var3 - var0;
      float var12 = var4 - var1;
      float var13 = var5 - var2;
      float var14 = var6 * var11 + var7 * var12 + var8 * var13;
      float var15 = -(var6 * var0 + var7 * var1 + var8 * var2 + var9) / var14;
      if (var15 >= 0.0F && var15 <= 1.0F) {
         var10.x = var0 + var15 * var11;
         var10.y = var1 + var15 * var12;
         var10.z = var2 + var15 * var13;
         return true;
      } else {
         return false;
      }
   }

   public static boolean testLineCircle(float var0, float var1, float var2, float var3, float var4, float var5) {
      float var6 = (float)Math.sqrt((double)(var0 * var0 + var1 * var1));
      float var7 = (var0 * var3 + var1 * var4 + var2) / var6;
      return -var5 <= var7 && var7 <= var5;
   }

   public static boolean intersectLineCircle(float var0, float var1, float var2, float var3, float var4, float var5, Vector3f var6) {
      float var7 = 1.0F / (float)Math.sqrt((double)(var0 * var0 + var1 * var1));
      float var8 = (var0 * var3 + var1 * var4 + var2) * var7;
      if (-var5 <= var8 && var8 <= var5) {
         var6.x = var3 + var8 * var0 * var7;
         var6.y = var4 + var8 * var1 * var7;
         var6.z = (float)Math.sqrt((double)(var5 * var5 - var8 * var8));
         return true;
      } else {
         return false;
      }
   }

   public static boolean intersectLineCircle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, Vector3f var7) {
      return intersectLineCircle(var1 - var3, var2 - var0, (var0 - var2) * var1 + (var3 - var1) * var0, var4, var5, var6, var7);
   }

   public static boolean testAarLine(float var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7;
      float var9;
      if (var4 > 0.0F) {
         var7 = var2;
         var9 = var0;
      } else {
         var7 = var0;
         var9 = var2;
      }

      float var8;
      float var10;
      if (var5 > 0.0F) {
         var8 = var3;
         var10 = var1;
      } else {
         var8 = var1;
         var10 = var3;
      }

      float var11 = var6 + var4 * var9 + var5 * var10;
      float var12 = var6 + var4 * var7 + var5 * var8;
      return var11 <= 0.0F && var12 >= 0.0F;
   }

   public static boolean testAarLine(Vector2fc var0, Vector2fc var1, float var2, float var3, float var4) {
      return testAarLine(var0.x(), var0.y(), var1.x(), var1.y(), var2, var3, var4);
   }

   public static boolean testAarLine(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = var5 - var7;
      float var9 = var6 - var4;
      float var10 = -var9 * var5 - var8 * var4;
      return testAarLine(var0, var1, var2, var3, var8, var9, var10);
   }

   public static boolean testAarAar(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      return var2 >= var4 && var3 >= var5 && var0 <= var6 && var1 <= var7;
   }

   public static boolean testAarAar(Vector2fc var0, Vector2fc var1, Vector2fc var2, Vector2fc var3) {
      return testAarAar(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y());
   }

   public static boolean intersectCircleCircle(float var0, float var1, float var2, float var3, float var4, float var5, Vector3f var6) {
      float var7 = var3 - var0;
      float var8 = var4 - var1;
      float var9 = var7 * var7 + var8 * var8;
      float var10 = 0.5F + (var2 - var5) / var9;
      float var11 = (float)Math.sqrt((double)(var2 - var10 * var10 * var9));
      if (var11 >= 0.0F) {
         var6.x = var0 + var10 * var7;
         var6.y = var1 + var10 * var8;
         var6.z = var11;
         return true;
      } else {
         return false;
      }
   }

   public static boolean intersectCircleCircle(Vector2fc var0, float var1, Vector2fc var2, float var3, Vector3f var4) {
      return intersectCircleCircle(var0.x(), var0.y(), var1, var2.x(), var2.y(), var3, var4);
   }

   public static boolean testCircleCircle(float var0, float var1, float var2, float var3, float var4, float var5) {
      float var6 = (var0 - var3) * (var0 - var3) + (var1 - var4) * (var1 - var4);
      return var6 <= (var2 + var5) * (var2 + var5);
   }

   public static boolean testCircleCircle(Vector2fc var0, float var1, Vector2fc var2, float var3) {
      return testCircleCircle(var0.x(), var0.y(), var1, var2.x(), var2.y(), var3);
   }

   public static float distancePointLine(float var0, float var1, float var2, float var3, float var4) {
      float var5 = (float)Math.sqrt((double)(var2 * var2 + var3 * var3));
      return (var2 * var0 + var3 * var1 + var4) / var5;
   }

   public static float distancePointLine(float var0, float var1, float var2, float var3, float var4, float var5) {
      float var6 = var4 - var2;
      float var7 = var5 - var3;
      float var8 = (float)Math.sqrt((double)(var6 * var6 + var7 * var7));
      return (var6 * (var3 - var1) - (var2 - var0) * var7) / var8;
   }

   public static float intersectRayLine(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = var6 * var2 + var7 * var3;
      if (var9 < var8) {
         float var10 = ((var4 - var0) * var6 + (var5 - var1) * var7) / var9;
         if (var10 >= 0.0F) {
            return var10;
         }
      }

      return -1.0F;
   }

   public static float intersectRayLine(Vector2fc var0, Vector2fc var1, Vector2fc var2, Vector2fc var3, float var4) {
      return intersectRayLine(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y(), var4);
   }

   public static float intersectRayLineSegment(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = var0 - var4;
      float var9 = var1 - var5;
      float var10 = var6 - var4;
      float var11 = var7 - var5;
      float var12 = 1.0F / (var11 * var2 - var10 * var3);
      float var13 = (var10 * var9 - var11 * var8) * var12;
      float var14 = (var9 * var2 - var8 * var3) * var12;
      return var13 >= 0.0F && var14 >= 0.0F && var14 <= 1.0F ? var13 : -1.0F;
   }

   public static float intersectRayLineSegment(Vector2fc var0, Vector2fc var1, Vector2fc var2, Vector2fc var3) {
      return intersectRayLineSegment(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y());
   }

   public static boolean testAarCircle(float var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var6;
      float var8;
      if (var4 < var0) {
         var8 = var4 - var0;
         var7 = var6 - var8 * var8;
      } else if (var4 > var2) {
         var8 = var4 - var2;
         var7 = var6 - var8 * var8;
      }

      if (var5 < var1) {
         var8 = var5 - var1;
         var7 -= var8 * var8;
      } else if (var5 > var3) {
         var8 = var5 - var3;
         var7 -= var8 * var8;
      }

      return var7 >= 0.0F;
   }

   public static boolean testAarCircle(Vector2fc var0, Vector2fc var1, Vector2fc var2, float var3) {
      return testAarCircle(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3);
   }

   public static int findClosestPointOnTriangle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, Vector2f var8) {
      float var9 = var0 - var6;
      float var10 = var1 - var7;
      float var11 = var2 - var6;
      float var12 = var3 - var7;
      float var13 = var4 - var6;
      float var14 = var5 - var7;
      float var15 = var11 - var9;
      float var16 = var12 - var10;
      float var17 = var13 - var9;
      float var18 = var14 - var10;
      float var19 = -(var15 * var9 + var16 * var10);
      float var20 = -(var17 * var9 + var18 * var10);
      if (var19 <= 0.0F && var20 <= 0.0F) {
         var8.set(var0, var1);
         return 0;
      } else {
         float var21 = -(var15 * var11 + var16 * var12);
         float var22 = -(var17 * var11 + var18 * var12);
         if (var21 >= 0.0F && var22 <= var21) {
            var8.set(var2, var3);
            return 0;
         } else {
            float var23 = var19 * var22 - var21 * var20;
            float var24;
            if (var23 <= 0.0F && var19 >= 0.0F && var21 <= 0.0F) {
               var24 = var19 / (var19 - var21);
               var8.set(var0 + var15 * var24, var1 + var16 * var24);
               return 1;
            } else {
               var24 = -(var15 * var13 + var16 * var14);
               float var25 = -(var17 * var13 + var18 * var14);
               if (var25 >= 0.0F && var24 <= var25) {
                  var8.set(var4, var5);
                  return 0;
               } else {
                  float var26 = var24 * var20 - var19 * var25;
                  float var27;
                  if (var26 <= 0.0F && var20 >= 0.0F && var25 <= 0.0F) {
                     var27 = var20 / (var20 - var25);
                     var8.set(var0 + var17 * var27, var1 + var18 * var27);
                     return 1;
                  } else {
                     var27 = var21 * var25 - var24 * var22;
                     float var28;
                     if (var27 <= 0.0F && var22 - var21 >= 0.0F && var24 - var25 >= 0.0F) {
                        var28 = (var22 - var21) / (var22 - var21 + var24 - var25);
                        var8.set(var2 + (var13 - var11) * var28, var3 + (var14 - var12) * var28);
                        return 1;
                     } else {
                        var28 = 1.0F / (var27 + var26 + var23);
                        float var29 = var26 * var28;
                        float var30 = var23 * var28;
                        var8.set(var0 + var15 * var29 + var17 * var30, var1 + var16 * var29 + var18 * var30);
                        return 2;
                     }
                  }
               }
            }
         }
      }
   }

   public static int findClosestPointOnTriangle(Vector2fc var0, Vector2fc var1, Vector2fc var2, Vector2fc var3, Vector2f var4) {
      return findClosestPointOnTriangle(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y(), var4);
   }

   public static boolean intersectRayCircle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, Vector2f var7) {
      float var8 = var4 - var0;
      float var9 = var5 - var1;
      float var10 = var8 * var2 + var9 * var3;
      float var11 = var8 * var8 + var9 * var9 - var10 * var10;
      if (var11 > var6) {
         return false;
      } else {
         float var12 = (float)Math.sqrt((double)(var6 - var11));
         float var13 = var10 - var12;
         float var14 = var10 + var12;
         if (var13 < var14 && var14 >= 0.0F) {
            var7.x = var13;
            var7.y = var14;
            return true;
         } else {
            return false;
         }
      }
   }

   public static boolean intersectRayCircle(Vector2fc var0, Vector2fc var1, Vector2fc var2, float var3, Vector2f var4) {
      return intersectRayCircle(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3, var4);
   }

   public static boolean testRayCircle(float var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var4 - var0;
      float var8 = var5 - var1;
      float var9 = var7 * var2 + var8 * var3;
      float var10 = var7 * var7 + var8 * var8 - var9 * var9;
      if (var10 > var6) {
         return false;
      } else {
         float var11 = (float)Math.sqrt((double)(var6 - var10));
         float var12 = var9 - var11;
         float var13 = var9 + var11;
         return var12 < var13 && var13 >= 0.0F;
      }
   }

   public static boolean testRayCircle(Vector2fc var0, Vector2fc var1, Vector2fc var2, float var3) {
      return testRayCircle(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3);
   }

   public static int intersectRayAar(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, Vector2f var8) {
      float var9 = 1.0F / var2;
      float var10 = 1.0F / var3;
      float var11;
      float var12;
      if (var9 >= 0.0F) {
         var11 = (var4 - var0) * var9;
         var12 = (var6 - var0) * var9;
      } else {
         var11 = (var6 - var0) * var9;
         var12 = (var4 - var0) * var9;
      }

      float var13;
      float var14;
      if (var10 >= 0.0F) {
         var13 = (var5 - var1) * var10;
         var14 = (var7 - var1) * var10;
      } else {
         var13 = (var7 - var1) * var10;
         var14 = (var5 - var1) * var10;
      }

      if (!(var11 > var14) && !(var13 > var12)) {
         var11 = !(var13 > var11) && !Float.isNaN(var11) ? var11 : var13;
         var12 = !(var14 < var12) && !Float.isNaN(var12) ? var12 : var14;
         byte var15 = -1;
         if (var11 < var12 && var12 >= 0.0F) {
            float var16 = var0 + var11 * var2;
            float var17 = var1 + var11 * var3;
            var8.x = var11;
            var8.y = var12;
            float var18 = Math.abs(var16 - var4);
            float var19 = Math.abs(var17 - var5);
            float var20 = Math.abs(var16 - var6);
            float var21 = Math.abs(var17 - var7);
            var15 = 0;
            float var22 = var18;
            if (var19 < var18) {
               var22 = var19;
               var15 = 1;
            }

            if (var20 < var22) {
               var22 = var20;
               var15 = 2;
            }

            if (var21 < var22) {
               var15 = 3;
            }
         }

         return var15;
      } else {
         return -1;
      }
   }

   public static int intersectRayAar(Vector2fc var0, Vector2fc var1, Vector2fc var2, Vector2fc var3, Vector2f var4) {
      return intersectRayAar(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y(), var4);
   }

   public static int intersectLineSegmentAar(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, Vector2f var8) {
      float var9 = var2 - var0;
      float var10 = var3 - var1;
      float var11 = 1.0F / var9;
      float var12 = 1.0F / var10;
      float var13;
      float var14;
      if (var11 >= 0.0F) {
         var13 = (var4 - var0) * var11;
         var14 = (var6 - var0) * var11;
      } else {
         var13 = (var6 - var0) * var11;
         var14 = (var4 - var0) * var11;
      }

      float var15;
      float var16;
      if (var12 >= 0.0F) {
         var15 = (var5 - var1) * var12;
         var16 = (var7 - var1) * var12;
      } else {
         var15 = (var7 - var1) * var12;
         var16 = (var5 - var1) * var12;
      }

      if (!(var13 > var16) && !(var15 > var14)) {
         var13 = !(var15 > var13) && !Float.isNaN(var13) ? var13 : var15;
         var14 = !(var16 < var14) && !Float.isNaN(var14) ? var14 : var16;
         byte var17 = -1;
         if (var13 < var14 && var13 <= 1.0F && var14 >= 0.0F) {
            if (var13 > 0.0F && var14 > 1.0F) {
               var14 = var13;
               var17 = 1;
            } else if (var13 < 0.0F && var14 < 1.0F) {
               var13 = var14;
               var17 = 1;
            } else if (var13 < 0.0F && var14 > 1.0F) {
               var17 = 3;
            } else {
               var17 = 2;
            }

            var8.x = var13;
            var8.y = var14;
         }

         return var17;
      } else {
         return -1;
      }
   }

   public static int intersectLineSegmentAar(Vector2fc var0, Vector2fc var1, Vector2fc var2, Vector2fc var3, Vector2f var4) {
      return intersectLineSegmentAar(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y(), var4);
   }

   public static boolean testRayAar(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = 1.0F / var2;
      float var9 = 1.0F / var3;
      float var10;
      float var11;
      if (var8 >= 0.0F) {
         var10 = (var4 - var0) * var8;
         var11 = (var6 - var0) * var8;
      } else {
         var10 = (var6 - var0) * var8;
         var11 = (var4 - var0) * var8;
      }

      float var12;
      float var13;
      if (var9 >= 0.0F) {
         var12 = (var5 - var1) * var9;
         var13 = (var7 - var1) * var9;
      } else {
         var12 = (var7 - var1) * var9;
         var13 = (var5 - var1) * var9;
      }

      if (!(var10 > var13) && !(var12 > var11)) {
         var10 = !(var12 > var10) && !Float.isNaN(var10) ? var10 : var12;
         var11 = !(var13 < var11) && !Float.isNaN(var11) ? var11 : var13;
         return var10 < var11 && var11 >= 0.0F;
      } else {
         return false;
      }
   }

   public static boolean testRayAar(Vector2fc var0, Vector2fc var1, Vector2fc var2, Vector2fc var3) {
      return testRayAar(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y());
   }

   public static boolean testPointTriangle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      boolean var8 = (var0 - var4) * (var3 - var5) - (var2 - var4) * (var1 - var5) < 0.0F;
      boolean var9 = (var0 - var6) * (var5 - var7) - (var4 - var6) * (var1 - var7) < 0.0F;
      if (var8 != var9) {
         return false;
      } else {
         boolean var10 = (var0 - var2) * (var7 - var3) - (var6 - var2) * (var1 - var3) < 0.0F;
         return var9 == var10;
      }
   }

   public static boolean testPointTriangle(Vector2fc var0, Vector2fc var1, Vector2fc var2, Vector2fc var3) {
      return testPointTriangle(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y());
   }

   public static boolean testPointAar(float var0, float var1, float var2, float var3, float var4, float var5) {
      return var0 >= var2 && var1 >= var3 && var0 <= var4 && var1 <= var5;
   }

   public static boolean testPointCircle(float var0, float var1, float var2, float var3, float var4) {
      float var5 = var0 - var2;
      float var6 = var1 - var3;
      float var7 = var5 * var5;
      float var8 = var6 * var6;
      return var7 + var8 <= var4;
   }

   public static boolean testCircleTriangle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = var0 - var3;
      float var10 = var1 - var4;
      float var11 = var9 * var9 + var10 * var10 - var2;
      if (var11 <= 0.0F) {
         return true;
      } else {
         float var12 = var0 - var5;
         float var13 = var1 - var6;
         float var14 = var12 * var12 + var13 * var13 - var2;
         if (var14 <= 0.0F) {
            return true;
         } else {
            float var15 = var0 - var7;
            float var16 = var1 - var8;
            float var17 = var15 * var15 + var16 * var16 - var2;
            if (var17 <= 0.0F) {
               return true;
            } else {
               float var18 = var5 - var3;
               float var19 = var6 - var4;
               float var20 = var7 - var5;
               float var21 = var8 - var6;
               float var22 = var3 - var7;
               float var23 = var4 - var8;
               if (var18 * var10 - var19 * var9 >= 0.0F && var20 * var13 - var21 * var12 >= 0.0F && var22 * var16 - var23 * var15 >= 0.0F) {
                  return true;
               } else {
                  float var24 = var9 * var18 + var10 * var19;
                  float var25;
                  if (var24 >= 0.0F) {
                     var25 = var18 * var18 + var19 * var19;
                     if (var24 <= var25 && var11 * var25 <= var24 * var24) {
                        return true;
                     }
                  }

                  var24 = var12 * var20 + var13 * var21;
                  if (var24 > 0.0F) {
                     var25 = var20 * var20 + var21 * var21;
                     if (var24 <= var25 && var14 * var25 <= var24 * var24) {
                        return true;
                     }
                  }

                  var24 = var15 * var22 + var16 * var23;
                  if (var24 >= 0.0F) {
                     var25 = var22 * var22 + var23 * var23;
                     if (var24 < var25 && var17 * var25 <= var24 * var24) {
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      }
   }

   public static boolean testCircleTriangle(Vector2fc var0, float var1, Vector2fc var2, Vector2fc var3, Vector2fc var4) {
      return testCircleTriangle(var0.x(), var0.y(), var1, var2.x(), var2.y(), var3.x(), var3.y(), var4.x(), var4.y());
   }

   public static int intersectPolygonRay(float[] var0, float var1, float var2, float var3, float var4, Vector2f var5) {
      float var6 = Float.MAX_VALUE;
      int var7 = var0.length >> 1;
      int var8 = -1;
      float var9 = var0[var7 - 1 << 1];
      float var10 = var0[(var7 - 1 << 1) + 1];

      for(int var11 = 0; var11 < var7; ++var11) {
         float var12 = var0[var11 << 1];
         float var13 = var0[(var11 << 1) + 1];
         float var14 = var1 - var9;
         float var15 = var2 - var10;
         float var16 = var12 - var9;
         float var17 = var13 - var10;
         float var18 = 1.0F / (var17 * var3 - var16 * var4);
         float var19 = (var16 * var15 - var17 * var14) * var18;
         if (var19 >= 0.0F && var19 < var6) {
            float var20 = (var15 * var3 - var14 * var4) * var18;
            if (var20 >= 0.0F && var20 <= 1.0F) {
               var8 = (var11 - 1 + var7) % var7;
               var6 = var19;
               var5.x = var1 + var19 * var3;
               var5.y = var2 + var19 * var4;
            }
         }

         var9 = var12;
         var10 = var13;
      }

      return var8;
   }

   public static int intersectPolygonRay(Vector2fc[] var0, float var1, float var2, float var3, float var4, Vector2f var5) {
      float var6 = Float.MAX_VALUE;
      int var7 = var0.length;
      int var8 = -1;
      float var9 = var0[var7 - 1].x();
      float var10 = var0[var7 - 1].y();

      for(int var11 = 0; var11 < var7; ++var11) {
         Vector2fc var12 = var0[var11];
         float var13 = var12.x();
         float var14 = var12.y();
         float var15 = var1 - var9;
         float var16 = var2 - var10;
         float var17 = var13 - var9;
         float var18 = var14 - var10;
         float var19 = 1.0F / (var18 * var3 - var17 * var4);
         float var20 = (var17 * var16 - var18 * var15) * var19;
         if (var20 >= 0.0F && var20 < var6) {
            float var21 = (var16 * var3 - var15 * var4) * var19;
            if (var21 >= 0.0F && var21 <= 1.0F) {
               var8 = (var11 - 1 + var7) % var7;
               var6 = var20;
               var5.x = var1 + var20 * var3;
               var5.y = var2 + var20 * var4;
            }
         }

         var9 = var13;
         var10 = var14;
      }

      return var8;
   }

   public static boolean intersectLineLine(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, Vector2f var8) {
      float var9 = var0 - var2;
      float var10 = var3 - var1;
      float var11 = var10 * var0 + var9 * var1;
      float var12 = var4 - var6;
      float var13 = var7 - var5;
      float var14 = var13 * var4 + var12 * var5;
      float var15 = var10 * var12 - var13 * var9;
      if (var15 == 0.0F) {
         return false;
      } else {
         var8.x = (var12 * var11 - var9 * var14) / var15;
         var8.y = (var10 * var14 - var13 * var11) / var15;
         return true;
      }
   }
}
