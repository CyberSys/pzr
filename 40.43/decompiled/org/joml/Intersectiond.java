package org.joml;

public class Intersectiond {
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

   public static boolean testPlaneSphere(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      double var16 = Math.sqrt(var0 * var0 + var2 * var2 + var4 * var4);
      double var18 = (var0 * var8 + var2 * var10 + var4 * var12 + var6) / var16;
      return -var14 <= var18 && var18 <= var14;
   }

   public static boolean intersectPlaneSphere(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, Vector4d var16) {
      double var17 = 1.0D / Math.sqrt(var0 * var0 + var2 * var2 + var4 * var4);
      double var19 = (var0 * var8 + var2 * var10 + var4 * var12 + var6) * var17;
      if (-var14 <= var19 && var19 <= var14) {
         var16.x = var8 + var19 * var0 * var17;
         var16.y = var10 + var19 * var2 * var17;
         var16.z = var12 + var19 * var4 * var17;
         var16.w = Math.sqrt(var14 * var14 - var19 * var19);
         return true;
      } else {
         return false;
      }
   }

   public static boolean testAabPlane(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18) {
      double var20;
      double var26;
      if (var12 > 0.0D) {
         var20 = var6;
         var26 = var0;
      } else {
         var20 = var0;
         var26 = var6;
      }

      double var22;
      double var28;
      if (var14 > 0.0D) {
         var22 = var8;
         var28 = var2;
      } else {
         var22 = var2;
         var28 = var8;
      }

      double var24;
      double var30;
      if (var16 > 0.0D) {
         var24 = var10;
         var30 = var4;
      } else {
         var24 = var4;
         var30 = var10;
      }

      double var32 = var18 + var12 * var26 + var14 * var28 + var16 * var30;
      double var34 = var18 + var12 * var20 + var14 * var22 + var16 * var24;
      return var32 <= 0.0D && var34 >= 0.0D;
   }

   public static boolean testAabPlane(Vector3dc var0, Vector3dc var1, double var2, double var4, double var6, double var8) {
      return testAabPlane(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2, var4, var6, var8);
   }

   public static boolean testAabAab(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22) {
      return var6 >= var12 && var8 >= var14 && var10 >= var16 && var0 <= var18 && var2 <= var20 && var4 <= var22;
   }

   public static boolean testAabAab(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3) {
      return testAabAab(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z());
   }

   public static boolean intersectSphereSphere(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, Vector4d var16) {
      double var17 = var8 - var0;
      double var19 = var10 - var2;
      double var21 = var12 - var4;
      double var23 = var17 * var17 + var19 * var19 + var21 * var21;
      double var25 = 0.5D + (var6 - var14) / var23;
      double var27 = var6 - var25 * var25 * var23;
      if (var27 >= 0.0D) {
         var16.x = var0 + var25 * var17;
         var16.y = var2 + var25 * var19;
         var16.z = var4 + var25 * var21;
         var16.w = Math.sqrt(var27);
         return true;
      } else {
         return false;
      }
   }

   public static boolean intersectSphereSphere(Vector3dc var0, double var1, Vector3dc var3, double var4, Vector4d var6) {
      return intersectSphereSphere(var0.x(), var0.y(), var0.z(), var1, var3.x(), var3.y(), var3.z(), var4, var6);
   }

   public static boolean testSphereSphere(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      double var16 = var8 - var0;
      double var18 = var10 - var2;
      double var20 = var12 - var4;
      double var22 = var16 * var16 + var18 * var18 + var20 * var20;
      double var24 = 0.5D + (var6 - var14) / var22;
      double var26 = var6 - var24 * var24 * var22;
      return var26 >= 0.0D;
   }

   public static boolean testSphereSphere(Vector3dc var0, double var1, Vector3dc var3, double var4) {
      return testSphereSphere(var0.x(), var0.y(), var0.z(), var1, var3.x(), var3.y(), var3.z(), var4);
   }

   public static double distancePointPlane(double var0, double var2, double var4, double var6, double var8, double var10, double var12) {
      double var14 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
      return (var6 * var0 + var8 * var2 + var10 * var4 + var12) / var14;
   }

   public static double distancePointPlane(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22) {
      double var24 = var14 - var8;
      double var26 = var22 - var10;
      double var28 = var20 - var8;
      double var30 = var16 - var10;
      double var32 = var18 - var6;
      double var34 = var12 - var6;
      double var36 = var24 * var26 - var28 * var30;
      double var38 = var30 * var32 - var26 * var34;
      double var40 = var34 * var28 - var32 * var24;
      double var42 = -(var36 * var6 + var38 * var8 + var40 * var10);
      return distancePointPlane(var0, var2, var4, var36, var38, var40, var42);
   }

   public static double intersectRayPlane(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24) {
      double var26 = var18 * var6 + var20 * var8 + var22 * var10;
      if (var26 < var24) {
         double var28 = ((var12 - var0) * var18 + (var14 - var2) * var20 + (var16 - var4) * var22) / var26;
         if (var28 >= 0.0D) {
            return var28;
         }
      }

      return -1.0D;
   }

   public static double intersectRayPlane(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3, double var4) {
      return intersectRayPlane(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4);
   }

   public static double intersectRayPlane(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
      double var22 = var12 * var6 + var14 * var8 + var16 * var10;
      if (var22 < 0.0D) {
         double var24 = -(var12 * var0 + var14 * var2 + var16 * var4 + var18) / var22;
         if (var24 >= 0.0D) {
            return var24;
         }
      }

      return -1.0D;
   }

   public static boolean testAabSphere(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18) {
      double var20 = var18;
      double var22;
      if (var12 < var0) {
         var22 = var12 - var0;
         var20 = var18 - var22 * var22;
      } else if (var12 > var6) {
         var22 = var12 - var6;
         var20 = var18 - var22 * var22;
      }

      if (var14 < var2) {
         var22 = var14 - var2;
         var20 -= var22 * var22;
      } else if (var14 > var8) {
         var22 = var14 - var8;
         var20 -= var22 * var22;
      }

      if (var16 < var4) {
         var22 = var16 - var4;
         var20 -= var22 * var22;
      } else if (var16 > var10) {
         var22 = var16 - var10;
         var20 -= var22 * var22;
      }

      return var20 >= 0.0D;
   }

   public static boolean testAabSphere(Vector3dc var0, Vector3dc var1, Vector3dc var2, double var3) {
      return testAabSphere(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public static int findClosestPointOnTriangle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, Vector3d var24) {
      double var25 = var0 - var18;
      double var27 = var2 - var20;
      double var29 = var4 - var22;
      double var31 = var6 - var18;
      double var33 = var8 - var20;
      double var35 = var10 - var22;
      double var37 = var12 - var18;
      double var39 = var14 - var20;
      double var41 = var16 - var22;
      double var43 = var31 - var25;
      double var45 = var33 - var27;
      double var47 = var35 - var29;
      double var49 = var37 - var25;
      double var51 = var39 - var27;
      double var53 = var41 - var29;
      double var55 = -(var43 * var25 + var45 * var27 + var47 * var29);
      double var57 = -(var49 * var25 + var51 * var27 + var53 * var29);
      if (var55 <= 0.0D && var57 <= 0.0D) {
         var24.set(var0, var2, var4);
         return 0;
      } else {
         double var59 = -(var43 * var31 + var45 * var33 + var47 * var35);
         double var61 = -(var49 * var31 + var51 * var33 + var53 * var35);
         if (var59 >= 0.0D && var61 <= var59) {
            var24.set(var6, var8, var10);
            return 0;
         } else {
            double var63 = var55 * var61 - var59 * var57;
            double var65;
            if (var63 <= 0.0D && var55 >= 0.0D && var59 <= 0.0D) {
               var65 = var55 / (var55 - var59);
               var24.set(var0 + var43 * var65, var2 + var45 * var65, var4 * var47 * var65);
               return 1;
            } else {
               var65 = -(var43 * var37 + var45 * var39 + var47 * var41);
               double var67 = -(var49 * var37 + var51 * var39 + var53 * var41);
               if (var67 >= 0.0D && var65 <= var67) {
                  var24.set(var12, var14, var16);
                  return 0;
               } else {
                  double var69 = var65 * var57 - var55 * var67;
                  double var71;
                  if (var69 <= 0.0D && var57 >= 0.0D && var67 <= 0.0D) {
                     var71 = var57 / (var57 - var67);
                     var24.set(var0 + var49 * var71, var2 + var51 * var71, var4 + var53 * var71);
                     return 1;
                  } else {
                     var71 = var59 * var67 - var65 * var61;
                     double var73;
                     if (var71 <= 0.0D && var61 - var59 >= 0.0D && var65 - var67 >= 0.0D) {
                        var73 = (var61 - var59) / (var61 - var59 + var65 - var67);
                        var24.set(var6 + (var37 - var31) * var73, var8 + (var39 - var33) * var73, var10 + (var41 - var35) * var73);
                        return 1;
                     } else {
                        var73 = 1.0D / (var71 + var69 + var63);
                        double var75 = var69 * var73;
                        double var77 = var63 * var73;
                        var24.set(var0 + var43 * var75 + var49 * var77, var2 + var45 * var75 + var51 * var77, var4 + var47 * var75 + var53 * var77);
                        return 2;
                     }
                  }
               }
            }
         }
      }
   }

   public static int findClosestPointOnTriangle(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3, Vector3d var4) {
      return findClosestPointOnTriangle(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4);
   }

   public static int intersectSweptSphereTriangle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24, double var26, double var28, double var30, double var32, double var34, Vector4d var36) {
      double var37 = var20 - var14;
      double var39 = var22 - var16;
      double var41 = var24 - var18;
      double var43 = var26 - var14;
      double var45 = var28 - var16;
      double var47 = var30 - var18;
      double var49 = var39 * var47 - var45 * var41;
      double var51 = var41 * var43 - var47 * var37;
      double var53 = var37 * var45 - var43 * var39;
      double var55 = -(var49 * var14 + var51 * var16 + var53 * var18);
      double var57 = 1.0D / Math.sqrt(var49 * var49 + var51 * var51 + var53 * var53);
      double var59 = (var49 * var0 + var51 * var2 + var53 * var4 + var55) * var57;
      double var61 = (var49 * var8 + var51 * var10 + var53 * var12) * var57;
      if (var61 < var32 && var61 > -var32) {
         return -1;
      } else {
         double var63 = (var6 - var59) / var61;
         if (var63 > var34) {
            return -1;
         } else {
            double var65 = (-var6 - var59) / var61;
            double var67 = var0 - var6 * var49 * var57 + var8 * var63;
            double var69 = var2 - var6 * var51 * var57 + var10 * var63;
            double var71 = var4 - var6 * var53 * var57 + var12 * var63;
            boolean var73 = testPointInTriangle(var67, var69, var71, var14, var16, var18, var20, var22, var24, var26, var28, var30);
            if (var73) {
               var36.x = var67;
               var36.y = var69;
               var36.z = var71;
               var36.w = var63;
               return 2;
            } else {
               byte var74 = -1;
               double var75 = var34;
               double var77 = var8 * var8 + var10 * var10 + var12 * var12;
               double var79 = var6 * var6;
               double var81 = var0 - var14;
               double var83 = var2 - var16;
               double var85 = var4 - var18;
               double var87 = 2.0D * (var8 * var81 + var10 * var83 + var12 * var85);
               double var89 = var81 * var81 + var83 * var83 + var85 * var85 - var79;
               double var91 = computeLowestRoot(var77, var87, var89, var34);
               if (var91 < var34) {
                  var36.x = var14;
                  var36.y = var16;
                  var36.z = var18;
                  var36.w = var91;
                  var75 = var91;
                  var74 = 0;
               }

               double var93 = var0 - var20;
               double var95 = var2 - var22;
               double var97 = var4 - var24;
               double var99 = var93 * var93 + var95 * var95 + var97 * var97;
               double var101 = 2.0D * (var8 * var93 + var10 * var95 + var12 * var97);
               double var103 = var99 - var79;
               double var105 = computeLowestRoot(var77, var101, var103, var75);
               if (var105 < var75) {
                  var36.x = var20;
                  var36.y = var22;
                  var36.z = var24;
                  var36.w = var105;
                  var75 = var105;
                  var74 = 0;
               }

               double var107 = var0 - var26;
               double var109 = var2 - var28;
               double var111 = var4 - var30;
               double var113 = 2.0D * (var8 * var107 + var10 * var109 + var12 * var111);
               double var115 = var107 * var107 + var109 * var109 + var111 * var111 - var79;
               double var117 = computeLowestRoot(var77, var113, var115, var75);
               if (var117 < var75) {
                  var36.x = var26;
                  var36.y = var28;
                  var36.z = var30;
                  var36.w = var117;
                  var75 = var117;
                  var74 = 0;
               }

               double var119 = var8 * var8 + var10 * var10 + var12 * var12;
               double var121 = var37 * var37 + var39 * var39 + var41 * var41;
               double var123 = var81 * var81 + var83 * var83 + var85 * var85;
               double var125 = var37 * var8 + var39 * var10 + var41 * var12;
               double var127 = var121 * -var119 + var125 * var125;
               double var129 = var37 * -var81 + var39 * -var83 + var41 * -var85;
               double var131 = var8 * -var81 + var10 * -var83 + var12 * -var85;
               double var133 = var121 * 2.0D * var131 - 2.0D * var125 * var129;
               double var135 = var121 * (var79 - var123) + var129 * var129;
               double var137 = computeLowestRoot(var127, var133, var135, var75);
               double var139 = (var125 * var137 - var129) / var121;
               if (var139 >= 0.0D && var139 <= 1.0D && var137 < var75) {
                  var36.x = var14 + var139 * var37;
                  var36.y = var16 + var139 * var39;
                  var36.z = var18 + var139 * var41;
                  var36.w = var137;
                  var75 = var137;
                  var74 = 1;
               }

               double var141 = var43 * var43 + var45 * var45 + var47 * var47;
               double var143 = var43 * var8 + var45 * var10 + var47 * var12;
               double var145 = var141 * -var119 + var143 * var143;
               double var147 = var43 * -var81 + var45 * -var83 + var47 * -var85;
               double var149 = var141 * 2.0D * var131 - 2.0D * var143 * var147;
               double var151 = var141 * (var79 - var123) + var147 * var147;
               double var153 = computeLowestRoot(var145, var149, var151, var75);
               double var155 = (var143 * var153 - var147) / var141;
               if (var155 >= 0.0D && var155 <= 1.0D && var153 < var65) {
                  var36.x = var14 + var155 * var43;
                  var36.y = var16 + var155 * var45;
                  var36.z = var18 + var155 * var47;
                  var36.w = var153;
                  var75 = var153;
                  var74 = 1;
               }

               double var157 = var26 - var20;
               double var159 = var28 - var22;
               double var161 = var30 - var24;
               double var163 = var157 * var157 + var159 * var159 + var161 * var161;
               double var167 = var157 * var8 + var159 * var10 + var161 * var12;
               double var169 = var163 * -var119 + var167 * var167;
               double var171 = var157 * -var93 + var159 * -var95 + var161 * -var97;
               double var173 = var8 * -var93 + var10 * -var95 + var12 * -var97;
               double var175 = var163 * 2.0D * var173 - 2.0D * var167 * var171;
               double var177 = var163 * (var79 - var99) + var171 * var171;
               double var179 = computeLowestRoot(var169, var175, var177, var75);
               double var181 = (var167 * var179 - var171) / var163;
               if (var181 >= 0.0D && var181 <= 1.0D && var179 < var75) {
                  var36.x = var20 + var181 * var157;
                  var36.y = var22 + var181 * var159;
                  var36.z = var24 + var181 * var161;
                  var36.w = var179;
                  var74 = 1;
               }

               return var74;
            }
         }
      }
   }

   private static double computeLowestRoot(double var0, double var2, double var4, double var6) {
      double var8 = var2 * var2 - 4.0D * var0 * var4;
      if (var8 < 0.0D) {
         return Double.MAX_VALUE;
      } else {
         double var10 = Math.sqrt(var8);
         double var12 = (-var2 - var10) / (2.0D * var0);
         double var14 = (-var2 + var10) / (2.0D * var0);
         if (var12 > var14) {
            double var16 = var14;
            var14 = var12;
            var12 = var16;
         }

         if (var12 > 0.0D && var12 < var6) {
            return var12;
         } else {
            return var14 > 0.0D && var14 < var6 ? var14 : Double.MAX_VALUE;
         }
      }
   }

   public static boolean testPointInTriangle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22) {
      double var24 = var12 - var6;
      double var26 = var14 - var8;
      double var28 = var16 - var10;
      double var30 = var18 - var6;
      double var32 = var20 - var8;
      double var34 = var22 - var10;
      double var36 = var24 * var24 + var26 * var26 + var28 * var28;
      double var38 = var24 * var30 + var26 * var32 + var28 * var34;
      double var40 = var30 * var30 + var32 * var32 + var34 * var34;
      double var42 = var36 * var40 - var38 * var38;
      double var44 = var0 - var6;
      double var46 = var2 - var8;
      double var48 = var4 - var10;
      double var50 = var44 * var24 + var46 * var26 + var48 * var28;
      double var52 = var44 * var30 + var46 * var32 + var48 * var34;
      double var54 = var50 * var40 - var52 * var38;
      double var56 = var52 * var36 - var50 * var38;
      double var58 = var54 + var56 - var42;
      return (Double.doubleToRawLongBits(var58) & ~(Double.doubleToRawLongBits(var54) | Double.doubleToRawLongBits(var56)) & Long.MIN_VALUE) != 0L;
   }

   public static boolean intersectRaySphere(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, Vector2d var20) {
      double var21 = var12 - var0;
      double var23 = var14 - var2;
      double var25 = var16 - var4;
      double var27 = var21 * var6 + var23 * var8 + var25 * var10;
      double var29 = var21 * var21 + var23 * var23 + var25 * var25 - var27 * var27;
      if (var29 > var18) {
         return false;
      } else {
         double var31 = Math.sqrt(var18 - var29);
         double var33 = var27 - var31;
         double var35 = var27 + var31;
         if (var33 < var35 && var35 >= 0.0D) {
            var20.x = var33;
            var20.y = var35;
            return true;
         } else {
            return false;
         }
      }
   }

   public static boolean intersectRaySphere(Vector3dc var0, Vector3dc var1, Vector3dc var2, double var3, Vector2d var5) {
      return intersectRaySphere(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3, var5);
   }

   public static boolean testRaySphere(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18) {
      double var20 = var12 - var0;
      double var22 = var14 - var2;
      double var24 = var16 - var4;
      double var26 = var20 * var6 + var22 * var8 + var24 * var10;
      double var28 = var20 * var20 + var22 * var22 + var24 * var24 - var26 * var26;
      if (var28 > var18) {
         return false;
      } else {
         double var30 = Math.sqrt(var18 - var28);
         double var32 = var26 - var30;
         double var34 = var26 + var30;
         return var32 < var34 && var34 >= 0.0D;
      }
   }

   public static boolean testRaySphere(Vector3dc var0, Vector3dc var1, Vector3dc var2, double var3) {
      return testRaySphere(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public static boolean testLineSegmentSphere(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18) {
      double var20 = var6 - var0;
      double var22 = var8 - var2;
      double var24 = var10 - var4;
      double var26 = (var12 - var0) * var20 + (var14 - var2) * var22 + (var16 - var4) * var24;
      double var28 = var20 * var20 + var22 * var22 + var24 * var24;
      double var30 = var26 / var28;
      double var32;
      if (var30 < 0.0D) {
         var20 = var0 - var12;
         var22 = var2 - var14;
         var24 = var4 - var16;
      } else if (var30 > 1.0D) {
         var20 = var6 - var12;
         var22 = var8 - var14;
         var24 = var10 - var16;
      } else {
         var32 = var0 + var30 * var20;
         double var34 = var2 + var30 * var22;
         double var36 = var4 + var30 * var24;
         var20 = var32 - var12;
         var22 = var34 - var14;
         var24 = var36 - var16;
      }

      var32 = var20 * var20 + var22 * var22 + var24 * var24;
      return var32 <= var18;
   }

   public static boolean testLineSegmentSphere(Vector3dc var0, Vector3dc var1, Vector3dc var2, double var3) {
      return testLineSegmentSphere(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public static boolean intersectRayAab(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, Vector2d var24) {
      double var25 = 1.0D / var6;
      double var27 = 1.0D / var8;
      double var29 = 1.0D / var10;
      double var33;
      double var31;
      if (var25 >= 0.0D) {
         var31 = (var12 - var0) * var25;
         var33 = (var18 - var0) * var25;
      } else {
         var31 = (var18 - var0) * var25;
         var33 = (var12 - var0) * var25;
      }

      double var35;
      double var37;
      if (var27 >= 0.0D) {
         var35 = (var14 - var2) * var27;
         var37 = (var20 - var2) * var27;
      } else {
         var35 = (var20 - var2) * var27;
         var37 = (var14 - var2) * var27;
      }

      if (!(var31 > var37) && !(var35 > var33)) {
         double var39;
         double var41;
         if (var29 >= 0.0D) {
            var39 = (var16 - var4) * var29;
            var41 = (var22 - var4) * var29;
         } else {
            var39 = (var22 - var4) * var29;
            var41 = (var16 - var4) * var29;
         }

         if (!(var31 > var41) && !(var39 > var33)) {
            var31 = !(var35 > var31) && !Double.isNaN(var31) ? var31 : var35;
            var33 = !(var37 < var33) && !Double.isNaN(var33) ? var33 : var37;
            var31 = var39 > var31 ? var39 : var31;
            var33 = var41 < var33 ? var41 : var33;
            if (var31 < var33 && var33 >= 0.0D) {
               var24.x = var31;
               var24.y = var33;
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

   public static boolean intersectRayAab(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3, Vector2d var4) {
      return intersectRayAab(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4);
   }

   public static int intersectLineSegmentAab(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, Vector2d var24) {
      double var25 = var6 - var0;
      double var27 = var8 - var2;
      double var29 = var10 - var4;
      double var31 = 1.0D / var25;
      double var33 = 1.0D / var27;
      double var35 = 1.0D / var29;
      double var37;
      double var39;
      if (var31 >= 0.0D) {
         var37 = (var12 - var0) * var31;
         var39 = (var18 - var0) * var31;
      } else {
         var37 = (var18 - var0) * var31;
         var39 = (var12 - var0) * var31;
      }

      double var41;
      double var43;
      if (var33 >= 0.0D) {
         var41 = (var14 - var2) * var33;
         var43 = (var20 - var2) * var33;
      } else {
         var41 = (var20 - var2) * var33;
         var43 = (var14 - var2) * var33;
      }

      if (!(var37 > var43) && !(var41 > var39)) {
         double var45;
         double var47;
         if (var35 >= 0.0D) {
            var45 = (var16 - var4) * var35;
            var47 = (var22 - var4) * var35;
         } else {
            var45 = (var22 - var4) * var35;
            var47 = (var16 - var4) * var35;
         }

         if (!(var37 > var47) && !(var45 > var39)) {
            var37 = !(var41 > var37) && !Double.isNaN(var37) ? var37 : var41;
            var39 = !(var43 < var39) && !Double.isNaN(var39) ? var39 : var43;
            var37 = var45 > var37 ? var45 : var37;
            var39 = var47 < var39 ? var47 : var39;
            byte var49 = -1;
            if (var37 < var39 && var37 <= 1.0D && var39 >= 0.0D) {
               if (var37 > 0.0D && var39 > 1.0D) {
                  var39 = var37;
                  var49 = 1;
               } else if (var37 < 0.0D && var39 < 1.0D) {
                  var37 = var39;
                  var49 = 1;
               } else if (var37 < 0.0D && var39 > 1.0D) {
                  var49 = 3;
               } else {
                  var49 = 2;
               }

               var24.x = var37;
               var24.y = var39;
            }

            return var49;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static int intersectLineSegmentAab(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3, Vector2d var4) {
      return intersectLineSegmentAab(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4);
   }

   public static boolean testRayAab(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22) {
      double var24 = 1.0D / var6;
      double var26 = 1.0D / var8;
      double var28 = 1.0D / var10;
      double var32;
      double var30;
      if (var24 >= 0.0D) {
         var30 = (var12 - var0) * var24;
         var32 = (var18 - var0) * var24;
      } else {
         var30 = (var18 - var0) * var24;
         var32 = (var12 - var0) * var24;
      }

      double var34;
      double var36;
      if (var26 >= 0.0D) {
         var34 = (var14 - var2) * var26;
         var36 = (var20 - var2) * var26;
      } else {
         var34 = (var20 - var2) * var26;
         var36 = (var14 - var2) * var26;
      }

      if (!(var30 > var36) && !(var34 > var32)) {
         double var38;
         double var40;
         if (var28 >= 0.0D) {
            var38 = (var16 - var4) * var28;
            var40 = (var22 - var4) * var28;
         } else {
            var38 = (var22 - var4) * var28;
            var40 = (var16 - var4) * var28;
         }

         if (!(var30 > var40) && !(var38 > var32)) {
            var30 = !(var34 > var30) && !Double.isNaN(var30) ? var30 : var34;
            var32 = !(var36 < var32) && !Double.isNaN(var32) ? var32 : var36;
            var30 = var38 > var30 ? var38 : var30;
            var32 = var40 < var32 ? var40 : var32;
            return var30 < var32 && var32 >= 0.0D;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean testRayAab(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3) {
      return testRayAab(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z());
   }

   public static boolean testRayTriangleFront(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24, double var26, double var28, double var30) {
      double var32 = var18 - var12;
      double var34 = var20 - var14;
      double var36 = var22 - var16;
      double var38 = var24 - var12;
      double var40 = var26 - var14;
      double var42 = var28 - var16;
      double var44 = var8 * var42 - var10 * var40;
      double var46 = var10 * var38 - var6 * var42;
      double var48 = var6 * var40 - var8 * var38;
      double var50 = var32 * var44 + var34 * var46 + var36 * var48;
      if (var50 < var30) {
         return false;
      } else {
         double var52 = var0 - var12;
         double var54 = var2 - var14;
         double var56 = var4 - var16;
         double var58 = var52 * var44 + var54 * var46 + var56 * var48;
         if (!(var58 < 0.0D) && !(var58 > var50)) {
            double var60 = var54 * var36 - var56 * var34;
            double var62 = var56 * var32 - var52 * var36;
            double var64 = var52 * var34 - var54 * var32;
            double var66 = var6 * var60 + var8 * var62 + var10 * var64;
            if (!(var66 < 0.0D) && !(var58 + var66 > var50)) {
               double var68 = 1.0D / var50;
               double var70 = (var38 * var60 + var40 * var62 + var42 * var64) * var68;
               return var70 >= var30;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static boolean testRayTriangleFront(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3, Vector3dc var4, double var5) {
      return testRayTriangleFront(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5);
   }

   public static boolean testRayTriangle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24, double var26, double var28, double var30) {
      double var32 = var18 - var12;
      double var34 = var20 - var14;
      double var36 = var22 - var16;
      double var38 = var24 - var12;
      double var40 = var26 - var14;
      double var42 = var28 - var16;
      double var44 = var8 * var42 - var10 * var40;
      double var46 = var10 * var38 - var6 * var42;
      double var48 = var6 * var40 - var8 * var38;
      double var50 = var32 * var44 + var34 * var46 + var36 * var48;
      if (var50 > -var30 && var50 < var30) {
         return false;
      } else {
         double var52 = var0 - var12;
         double var54 = var2 - var14;
         double var56 = var4 - var16;
         double var58 = 1.0D / var50;
         double var60 = (var52 * var44 + var54 * var46 + var56 * var48) * var58;
         if (!(var60 < 0.0D) && !(var60 > 1.0D)) {
            double var62 = var54 * var36 - var56 * var34;
            double var64 = var56 * var32 - var52 * var36;
            double var66 = var52 * var34 - var54 * var32;
            double var68 = (var6 * var62 + var8 * var64 + var10 * var66) * var58;
            if (!(var68 < 0.0D) && !(var60 + var68 > 1.0D)) {
               double var70 = (var38 * var62 + var40 * var64 + var42 * var66) * var58;
               return var70 >= var30;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static boolean testRayTriangle(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3, Vector3dc var4, double var5) {
      return testRayTriangleFront(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5);
   }

   public static double intersectRayTriangleFront(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24, double var26, double var28, double var30) {
      double var32 = var18 - var12;
      double var34 = var20 - var14;
      double var36 = var22 - var16;
      double var38 = var24 - var12;
      double var40 = var26 - var14;
      double var42 = var28 - var16;
      double var44 = var8 * var42 - var10 * var40;
      double var46 = var10 * var38 - var6 * var42;
      double var48 = var6 * var40 - var8 * var38;
      double var50 = var32 * var44 + var34 * var46 + var36 * var48;
      if (var50 <= var30) {
         return -1.0D;
      } else {
         double var52 = var0 - var12;
         double var54 = var2 - var14;
         double var56 = var4 - var16;
         double var58 = var52 * var44 + var54 * var46 + var56 * var48;
         if (!(var58 < 0.0D) && !(var58 > var50)) {
            double var60 = var54 * var36 - var56 * var34;
            double var62 = var56 * var32 - var52 * var36;
            double var64 = var52 * var34 - var54 * var32;
            double var66 = var6 * var60 + var8 * var62 + var10 * var64;
            if (!(var66 < 0.0D) && !(var58 + var66 > var50)) {
               double var68 = 1.0D / var50;
               double var70 = (var38 * var60 + var40 * var62 + var42 * var64) * var68;
               return var70;
            } else {
               return -1.0D;
            }
         } else {
            return -1.0D;
         }
      }
   }

   public static double intersectRayTriangleFront(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3, Vector3dc var4, double var5) {
      return intersectRayTriangleFront(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5);
   }

   public static double intersectRayTriangle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24, double var26, double var28, double var30) {
      double var32 = var18 - var12;
      double var34 = var20 - var14;
      double var36 = var22 - var16;
      double var38 = var24 - var12;
      double var40 = var26 - var14;
      double var42 = var28 - var16;
      double var44 = var8 * var42 - var10 * var40;
      double var46 = var10 * var38 - var6 * var42;
      double var48 = var6 * var40 - var8 * var38;
      double var50 = var32 * var44 + var34 * var46 + var36 * var48;
      if (var50 > -var30 && var50 < var30) {
         return -1.0D;
      } else {
         double var52 = var0 - var12;
         double var54 = var2 - var14;
         double var56 = var4 - var16;
         double var58 = 1.0D / var50;
         double var60 = (var52 * var44 + var54 * var46 + var56 * var48) * var58;
         if (!(var60 < 0.0D) && !(var60 > 1.0D)) {
            double var62 = var54 * var36 - var56 * var34;
            double var64 = var56 * var32 - var52 * var36;
            double var66 = var52 * var34 - var54 * var32;
            double var68 = (var6 * var62 + var8 * var64 + var10 * var66) * var58;
            if (!(var68 < 0.0D) && !(var60 + var68 > 1.0D)) {
               double var70 = (var38 * var62 + var40 * var64 + var42 * var66) * var58;
               return var70;
            } else {
               return -1.0D;
            }
         } else {
            return -1.0D;
         }
      }
   }

   public static double intersectRayTriangle(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3, Vector3dc var4, double var5) {
      return intersectRayTriangle(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5);
   }

   public static boolean testLineSegmentTriangle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24, double var26, double var28, double var30) {
      double var32 = var6 - var0;
      double var34 = var8 - var2;
      double var36 = var10 - var4;
      double var38 = intersectRayTriangle(var0, var2, var4, var32, var34, var36, var12, var14, var16, var18, var20, var22, var24, var26, var28, var30);
      return var38 >= 0.0D && var38 <= 1.0D;
   }

   public static boolean testLineSegmentTriangle(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3, Vector3dc var4, double var5) {
      return testLineSegmentTriangle(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5);
   }

   public static boolean intersectLineSegmentTriangle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24, double var26, double var28, double var30, Vector3d var32) {
      double var33 = var6 - var0;
      double var35 = var8 - var2;
      double var37 = var10 - var4;
      double var39 = intersectRayTriangle(var0, var2, var4, var33, var35, var37, var12, var14, var16, var18, var20, var22, var24, var26, var28, var30);
      if (var39 >= 0.0D && var39 <= 1.0D) {
         var32.x = var0 + var33 * var39;
         var32.y = var2 + var35 * var39;
         var32.z = var4 + var37 * var39;
         return true;
      } else {
         return false;
      }
   }

   public static boolean intersectLineSegmentTriangle(Vector3dc var0, Vector3dc var1, Vector3dc var2, Vector3dc var3, Vector3dc var4, double var5, Vector3d var7) {
      return intersectLineSegmentTriangle(var0.x(), var0.y(), var0.z(), var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4.x(), var4.y(), var4.z(), var5, var7);
   }

   public static boolean intersectLineSegmentPlane(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, Vector3d var20) {
      double var21 = var6 - var0;
      double var23 = var8 - var2;
      double var25 = var10 - var4;
      double var27 = var12 * var21 + var14 * var23 + var16 * var25;
      double var29 = -(var12 * var0 + var14 * var2 + var16 * var4 + var18) / var27;
      if (var29 >= 0.0D && var29 <= 1.0D) {
         var20.x = var0 + var29 * var21;
         var20.y = var2 + var29 * var23;
         var20.z = var4 + var29 * var25;
         return true;
      } else {
         return false;
      }
   }

   public static boolean testLineCircle(double var0, double var2, double var4, double var6, double var8, double var10) {
      double var12 = Math.sqrt(var0 * var0 + var2 * var2);
      double var14 = (var0 * var6 + var2 * var8 + var4) / var12;
      return -var10 <= var14 && var14 <= var10;
   }

   public static boolean intersectLineCircle(double var0, double var2, double var4, double var6, double var8, double var10, Vector3d var12) {
      double var13 = 1.0D / Math.sqrt(var0 * var0 + var2 * var2);
      double var15 = (var0 * var6 + var2 * var8 + var4) * var13;
      if (-var10 <= var15 && var15 <= var10) {
         var12.x = var6 + var15 * var0 * var13;
         var12.y = var8 + var15 * var2 * var13;
         var12.z = Math.sqrt(var10 * var10 - var15 * var15);
         return true;
      } else {
         return false;
      }
   }

   public static boolean intersectLineCircle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, Vector3d var14) {
      return intersectLineCircle(var2 - var6, var4 - var0, (var0 - var4) * var2 + (var6 - var2) * var0, var8, var10, var12, var14);
   }

   public static boolean testAarLine(double var0, double var2, double var4, double var6, double var8, double var10, double var12) {
      double var14;
      double var18;
      if (var8 > 0.0D) {
         var14 = var4;
         var18 = var0;
      } else {
         var14 = var0;
         var18 = var4;
      }

      double var16;
      double var20;
      if (var10 > 0.0D) {
         var16 = var6;
         var20 = var2;
      } else {
         var16 = var2;
         var20 = var6;
      }

      double var22 = var12 + var8 * var18 + var10 * var20;
      double var24 = var12 + var8 * var14 + var10 * var16;
      return var22 <= 0.0D && var24 >= 0.0D;
   }

   public static boolean testAarLine(Vector2dc var0, Vector2dc var1, double var2, double var4, double var6) {
      return testAarLine(var0.x(), var0.y(), var1.x(), var1.y(), var2, var4, var6);
   }

   public static boolean testAarLine(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      double var16 = var10 - var14;
      double var18 = var12 - var8;
      double var20 = -var18 * var10 - var16 * var8;
      return testAarLine(var0, var2, var4, var6, var16, var18, var20);
   }

   public static boolean testAarAar(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      return var4 >= var8 && var6 >= var10 && var0 <= var12 && var2 <= var14;
   }

   public static boolean testAarAar(Vector2dc var0, Vector2dc var1, Vector2dc var2, Vector2dc var3) {
      return testAarAar(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y());
   }

   public static boolean intersectCircleCircle(double var0, double var2, double var4, double var6, double var8, double var10, Vector3d var12) {
      double var13 = var6 - var0;
      double var15 = var8 - var2;
      double var17 = var13 * var13 + var15 * var15;
      double var19 = 0.5D + (var4 - var10) / var17;
      double var21 = Math.sqrt(var4 - var19 * var19 * var17);
      if (var21 >= 0.0D) {
         var12.x = var0 + var19 * var13;
         var12.y = var2 + var19 * var15;
         var12.z = var21;
         return true;
      } else {
         return false;
      }
   }

   public static boolean intersectCircleCircle(Vector2dc var0, double var1, Vector2dc var3, double var4, Vector3d var6) {
      return intersectCircleCircle(var0.x(), var0.y(), var1, var3.x(), var3.y(), var4, var6);
   }

   public static boolean testCircleCircle(double var0, double var2, double var4, double var6, double var8, double var10) {
      double var12 = (var0 - var6) * (var0 - var6) + (var2 - var8) * (var2 - var8);
      return var12 <= (var4 + var10) * (var4 + var10);
   }

   public static boolean testCircleCircle(Vector2dc var0, double var1, Vector2dc var3, double var4) {
      return testCircleCircle(var0.x(), var0.y(), var1, var3.x(), var3.y(), var4);
   }

   public static double distancePointLine(double var0, double var2, double var4, double var6, double var8) {
      double var10 = Math.sqrt(var4 * var4 + var6 * var6);
      return (var4 * var0 + var6 * var2 + var8) / var10;
   }

   public static double distancePointLine(double var0, double var2, double var4, double var6, double var8, double var10) {
      double var12 = var8 - var4;
      double var14 = var10 - var6;
      double var16 = Math.sqrt(var12 * var12 + var14 * var14);
      return (var12 * (var6 - var2) - (var4 - var0) * var14) / var16;
   }

   public static double intersectRayLine(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16) {
      double var18 = var12 * var4 + var14 * var6;
      if (var18 < var16) {
         double var20 = ((var8 - var0) * var12 + (var10 - var2) * var14) / var18;
         if (var20 >= 0.0D) {
            return var20;
         }
      }

      return -1.0D;
   }

   public static double intersectRayLine(Vector2dc var0, Vector2dc var1, Vector2dc var2, Vector2dc var3, double var4) {
      return intersectRayLine(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y(), var4);
   }

   public static double intersectRayLineSegment(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      double var16 = var0 - var8;
      double var18 = var2 - var10;
      double var20 = var12 - var8;
      double var22 = var14 - var10;
      double var24 = 1.0D / (var22 * var4 - var20 * var6);
      double var26 = (var20 * var18 - var22 * var16) * var24;
      double var28 = (var18 * var4 - var16 * var6) * var24;
      return var26 >= 0.0D && var28 >= 0.0D && var28 <= 1.0D ? var26 : -1.0D;
   }

   public static double intersectRayLineSegment(Vector2dc var0, Vector2dc var1, Vector2dc var2, Vector2dc var3) {
      return intersectRayLineSegment(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y());
   }

   public static boolean testAarCircle(double var0, double var2, double var4, double var6, double var8, double var10, double var12) {
      double var14 = var12;
      double var16;
      if (var8 < var0) {
         var16 = var8 - var0;
         var14 = var12 - var16 * var16;
      } else if (var8 > var4) {
         var16 = var8 - var4;
         var14 = var12 - var16 * var16;
      }

      if (var10 < var2) {
         var16 = var10 - var2;
         var14 -= var16 * var16;
      } else if (var10 > var6) {
         var16 = var10 - var6;
         var14 -= var16 * var16;
      }

      return var14 >= 0.0D;
   }

   public static boolean testAarCircle(Vector2dc var0, Vector2dc var1, Vector2dc var2, double var3) {
      return testAarCircle(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3);
   }

   public static int findClosestPointOnTriangle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, Vector2d var16) {
      double var17 = var0 - var12;
      double var19 = var2 - var14;
      double var21 = var4 - var12;
      double var23 = var6 - var14;
      double var25 = var8 - var12;
      double var27 = var10 - var14;
      double var29 = var21 - var17;
      double var31 = var23 - var19;
      double var33 = var25 - var17;
      double var35 = var27 - var19;
      double var37 = -(var29 * var17 + var31 * var19);
      double var39 = -(var33 * var17 + var35 * var19);
      if (var37 <= 0.0D && var39 <= 0.0D) {
         var16.set(var0, var2);
         return 0;
      } else {
         double var41 = -(var29 * var21 + var31 * var23);
         double var43 = -(var33 * var21 + var35 * var23);
         if (var41 >= 0.0D && var43 <= var41) {
            var16.set(var4, var6);
            return 0;
         } else {
            double var45 = var37 * var43 - var41 * var39;
            double var47;
            if (var45 <= 0.0D && var37 >= 0.0D && var41 <= 0.0D) {
               var47 = var37 / (var37 - var41);
               var16.set(var0 + var29 * var47, var2 + var31 * var47);
               return 1;
            } else {
               var47 = -(var29 * var25 + var31 * var27);
               double var49 = -(var33 * var25 + var35 * var27);
               if (var49 >= 0.0D && var47 <= var49) {
                  var16.set(var8, var10);
                  return 0;
               } else {
                  double var51 = var47 * var39 - var37 * var49;
                  double var53;
                  if (var51 <= 0.0D && var39 >= 0.0D && var49 <= 0.0D) {
                     var53 = var39 / (var39 - var49);
                     var16.set(var0 + var33 * var53, var2 + var35 * var53);
                     return 1;
                  } else {
                     var53 = var41 * var49 - var47 * var43;
                     double var55;
                     if (var53 <= 0.0D && var43 - var41 >= 0.0D && var47 - var49 >= 0.0D) {
                        var55 = (var43 - var41) / (var43 - var41 + var47 - var49);
                        var16.set(var4 + (var25 - var21) * var55, var6 + (var27 - var23) * var55);
                        return 1;
                     } else {
                        var55 = 1.0D / (var53 + var51 + var45);
                        double var57 = var51 * var55;
                        double var59 = var45 * var55;
                        var16.set(var0 + var29 * var57 + var33 * var59, var2 + var31 * var57 + var35 * var59);
                        return 2;
                     }
                  }
               }
            }
         }
      }
   }

   public static int findClosestPointOnTriangle(Vector2dc var0, Vector2dc var1, Vector2dc var2, Vector2dc var3, Vector2d var4) {
      return findClosestPointOnTriangle(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y(), var4);
   }

   public static boolean intersectRayCircle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, Vector2d var14) {
      double var15 = var8 - var0;
      double var17 = var10 - var2;
      double var19 = var15 * var4 + var17 * var6;
      double var21 = var15 * var15 + var17 * var17 - var19 * var19;
      if (var21 > var12) {
         return false;
      } else {
         double var23 = Math.sqrt(var12 - var21);
         double var25 = var19 - var23;
         double var27 = var19 + var23;
         if (var25 < var27 && var27 >= 0.0D) {
            var14.x = var25;
            var14.y = var27;
            return true;
         } else {
            return false;
         }
      }
   }

   public static boolean intersectRayCircle(Vector2dc var0, Vector2dc var1, Vector2dc var2, double var3, Vector2d var5) {
      return intersectRayCircle(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3, var5);
   }

   public static boolean testRayCircle(double var0, double var2, double var4, double var6, double var8, double var10, double var12) {
      double var14 = var8 - var0;
      double var16 = var10 - var2;
      double var18 = var14 * var4 + var16 * var6;
      double var20 = var14 * var14 + var16 * var16 - var18 * var18;
      if (var20 > var12) {
         return false;
      } else {
         double var22 = Math.sqrt(var12 - var20);
         double var24 = var18 - var22;
         double var26 = var18 + var22;
         return var24 < var26 && var26 >= 0.0D;
      }
   }

   public static boolean testRayCircle(Vector2dc var0, Vector2dc var1, Vector2dc var2, double var3) {
      return testRayCircle(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3);
   }

   public static int intersectRayAar(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, Vector2d var16) {
      double var17 = 1.0D / var4;
      double var19 = 1.0D / var6;
      double var21;
      double var23;
      if (var17 >= 0.0D) {
         var21 = (var8 - var0) * var17;
         var23 = (var12 - var0) * var17;
      } else {
         var21 = (var12 - var0) * var17;
         var23 = (var8 - var0) * var17;
      }

      double var25;
      double var27;
      if (var19 >= 0.0D) {
         var25 = (var10 - var2) * var19;
         var27 = (var14 - var2) * var19;
      } else {
         var25 = (var14 - var2) * var19;
         var27 = (var10 - var2) * var19;
      }

      if (!(var21 > var27) && !(var25 > var23)) {
         var21 = !(var25 > var21) && !Double.isNaN(var21) ? var21 : var25;
         var23 = !(var27 < var23) && !Double.isNaN(var23) ? var23 : var27;
         byte var29 = -1;
         if (var21 < var23 && var23 >= 0.0D) {
            double var30 = var0 + var21 * var4;
            double var32 = var2 + var21 * var6;
            var16.x = var21;
            var16.y = var23;
            double var34 = Math.abs(var30 - var8);
            double var36 = Math.abs(var32 - var10);
            double var38 = Math.abs(var30 - var12);
            double var40 = Math.abs(var32 - var14);
            var29 = 0;
            double var42 = var34;
            if (var36 < var34) {
               var42 = var36;
               var29 = 1;
            }

            if (var38 < var42) {
               var42 = var38;
               var29 = 2;
            }

            if (var40 < var42) {
               var29 = 3;
            }
         }

         return var29;
      } else {
         return -1;
      }
   }

   public static int intersectRayAar(Vector2dc var0, Vector2dc var1, Vector2dc var2, Vector2dc var3, Vector2d var4) {
      return intersectRayAar(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y(), var4);
   }

   public static int intersectLineSegmentAar(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, Vector2d var16) {
      double var17 = var4 - var0;
      double var19 = var6 - var2;
      double var21 = 1.0D / var17;
      double var23 = 1.0D / var19;
      double var25;
      double var27;
      if (var21 >= 0.0D) {
         var25 = (var8 - var0) * var21;
         var27 = (var12 - var0) * var21;
      } else {
         var25 = (var12 - var0) * var21;
         var27 = (var8 - var0) * var21;
      }

      double var29;
      double var31;
      if (var23 >= 0.0D) {
         var29 = (var10 - var2) * var23;
         var31 = (var14 - var2) * var23;
      } else {
         var29 = (var14 - var2) * var23;
         var31 = (var10 - var2) * var23;
      }

      if (!(var25 > var31) && !(var29 > var27)) {
         var25 = !(var29 > var25) && !Double.isNaN(var25) ? var25 : var29;
         var27 = !(var31 < var27) && !Double.isNaN(var27) ? var27 : var31;
         byte var33 = -1;
         if (var25 < var27 && var25 <= 1.0D && var27 >= 0.0D) {
            if (var25 > 0.0D && var27 > 1.0D) {
               var27 = var25;
               var33 = 1;
            } else if (var25 < 0.0D && var27 < 1.0D) {
               var25 = var27;
               var33 = 1;
            } else if (var25 < 0.0D && var27 > 1.0D) {
               var33 = 3;
            } else {
               var33 = 2;
            }

            var16.x = var25;
            var16.y = var27;
         }

         return var33;
      } else {
         return -1;
      }
   }

   public static int intersectLineSegmentAar(Vector2dc var0, Vector2dc var1, Vector2dc var2, Vector2dc var3, Vector2d var4) {
      return intersectLineSegmentAar(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y(), var4);
   }

   public static boolean testRayAar(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      double var16 = 1.0D / var4;
      double var18 = 1.0D / var6;
      double var20;
      double var22;
      if (var16 >= 0.0D) {
         var20 = (var8 - var0) * var16;
         var22 = (var12 - var0) * var16;
      } else {
         var20 = (var12 - var0) * var16;
         var22 = (var8 - var0) * var16;
      }

      double var24;
      double var26;
      if (var18 >= 0.0D) {
         var24 = (var10 - var2) * var18;
         var26 = (var14 - var2) * var18;
      } else {
         var24 = (var14 - var2) * var18;
         var26 = (var10 - var2) * var18;
      }

      if (!(var20 > var26) && !(var24 > var22)) {
         var20 = !(var24 > var20) && !Double.isNaN(var20) ? var20 : var24;
         var22 = !(var26 < var22) && !Double.isNaN(var22) ? var22 : var26;
         return var20 < var22 && var22 >= 0.0D;
      } else {
         return false;
      }
   }

   public static boolean testRayAar(Vector2dc var0, Vector2dc var1, Vector2dc var2, Vector2dc var3) {
      return testRayAar(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y());
   }

   public static boolean testPointTriangle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      boolean var16 = (var0 - var8) * (var6 - var10) - (var4 - var8) * (var2 - var10) < 0.0D;
      boolean var17 = (var0 - var12) * (var10 - var14) - (var8 - var12) * (var2 - var14) < 0.0D;
      if (var16 != var17) {
         return false;
      } else {
         boolean var18 = (var0 - var4) * (var14 - var6) - (var12 - var4) * (var2 - var6) < 0.0D;
         return var17 == var18;
      }
   }

   public static boolean testPointTriangle(Vector2dc var0, Vector2dc var1, Vector2dc var2, Vector2dc var3) {
      return testPointTriangle(var0.x(), var0.y(), var1.x(), var1.y(), var2.x(), var2.y(), var3.x(), var3.y());
   }

   public static boolean testPointAar(double var0, double var2, double var4, double var6, double var8, double var10) {
      return var0 >= var4 && var2 >= var6 && var0 <= var8 && var2 <= var10;
   }

   public static boolean testPointCircle(double var0, double var2, double var4, double var6, double var8) {
      double var10 = var0 - var4;
      double var12 = var2 - var6;
      double var14 = var10 * var10;
      double var16 = var12 * var12;
      return var14 + var16 <= var8;
   }

   public static boolean testCircleTriangle(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16) {
      double var18 = var0 - var6;
      double var20 = var2 - var8;
      double var22 = var18 * var18 + var20 * var20 - var4;
      if (var22 <= 0.0D) {
         return true;
      } else {
         double var24 = var0 - var10;
         double var26 = var2 - var12;
         double var28 = var24 * var24 + var26 * var26 - var4;
         if (var28 <= 0.0D) {
            return true;
         } else {
            double var30 = var0 - var14;
            double var32 = var2 - var16;
            double var34 = var30 * var30 + var32 * var32 - var4;
            if (var34 <= 0.0D) {
               return true;
            } else {
               double var36 = var10 - var6;
               double var38 = var12 - var8;
               double var40 = var14 - var10;
               double var42 = var16 - var12;
               double var44 = var6 - var14;
               double var46 = var8 - var16;
               if (var36 * var20 - var38 * var18 >= 0.0D && var40 * var26 - var42 * var24 >= 0.0D && var44 * var32 - var46 * var30 >= 0.0D) {
                  return true;
               } else {
                  double var48 = var18 * var36 + var20 * var38;
                  double var50;
                  if (var48 >= 0.0D) {
                     var50 = var36 * var36 + var38 * var38;
                     if (var48 <= var50 && var22 * var50 <= var48 * var48) {
                        return true;
                     }
                  }

                  var48 = var24 * var40 + var26 * var42;
                  if (var48 > 0.0D) {
                     var50 = var40 * var40 + var42 * var42;
                     if (var48 <= var50 && var28 * var50 <= var48 * var48) {
                        return true;
                     }
                  }

                  var48 = var30 * var44 + var32 * var46;
                  if (var48 >= 0.0D) {
                     var50 = var44 * var44 + var46 * var46;
                     if (var48 < var50 && var34 * var50 <= var48 * var48) {
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      }
   }

   public static boolean testCircleTriangle(Vector2dc var0, double var1, Vector2dc var3, Vector2dc var4, Vector2dc var5) {
      return testCircleTriangle(var0.x(), var0.y(), var1, var3.x(), var3.y(), var4.x(), var4.y(), var5.x(), var5.y());
   }

   public static int intersectPolygonRay(double[] var0, double var1, double var3, double var5, double var7, Vector2d var9) {
      double var10 = Double.MAX_VALUE;
      int var12 = var0.length >> 1;
      int var13 = -1;
      double var14 = var0[var12 - 1 << 1];
      double var16 = var0[(var12 - 1 << 1) + 1];

      for(int var18 = 0; var18 < var12; ++var18) {
         double var19 = var0[var18 << 1];
         double var21 = var0[(var18 << 1) + 1];
         double var23 = var1 - var14;
         double var25 = var3 - var16;
         double var27 = var19 - var14;
         double var29 = var21 - var16;
         double var31 = 1.0D / (var29 * var5 - var27 * var7);
         double var33 = (var27 * var25 - var29 * var23) * var31;
         if (var33 >= 0.0D && var33 < var10) {
            double var35 = (var25 * var5 - var23 * var7) * var31;
            if (var35 >= 0.0D && var35 <= 1.0D) {
               var13 = (var18 - 1 + var12) % var12;
               var10 = var33;
               var9.x = var1 + var33 * var5;
               var9.y = var3 + var33 * var7;
            }
         }

         var14 = var19;
         var16 = var21;
      }

      return var13;
   }

   public static int intersectPolygonRay(Vector2dc[] var0, double var1, double var3, double var5, double var7, Vector2d var9) {
      double var10 = Double.MAX_VALUE;
      int var12 = var0.length;
      int var13 = -1;
      double var14 = var0[var12 - 1].x();
      double var16 = var0[var12 - 1].y();

      for(int var18 = 0; var18 < var12; ++var18) {
         Vector2dc var19 = var0[var18];
         double var20 = var19.x();
         double var22 = var19.y();
         double var24 = var1 - var14;
         double var26 = var3 - var16;
         double var28 = var20 - var14;
         double var30 = var22 - var16;
         double var32 = 1.0D / (var30 * var5 - var28 * var7);
         double var34 = (var28 * var26 - var30 * var24) * var32;
         if (var34 >= 0.0D && var34 < var10) {
            double var36 = (var26 * var5 - var24 * var7) * var32;
            if (var36 >= 0.0D && var36 <= 1.0D) {
               var13 = (var18 - 1 + var12) % var12;
               var10 = var34;
               var9.x = var1 + var34 * var5;
               var9.y = var3 + var34 * var7;
            }
         }

         var14 = var20;
         var16 = var22;
      }

      return var13;
   }

   public static boolean intersectLineLine(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, Vector2d var16) {
      double var17 = var0 - var4;
      double var19 = var6 - var2;
      double var21 = var19 * var0 + var17 * var2;
      double var23 = var8 - var12;
      double var25 = var14 - var10;
      double var27 = var25 * var8 + var23 * var10;
      double var29 = var19 * var23 - var25 * var17;
      if (var29 == 0.0D) {
         return false;
      } else {
         var16.x = (var23 * var21 - var17 * var27) / var29;
         var16.y = (var19 * var27 - var25 * var21) / var29;
         return true;
      }
   }
}
