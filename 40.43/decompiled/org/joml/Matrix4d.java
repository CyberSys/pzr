package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Matrix4d implements Externalizable, Matrix4dc {
   private static final long serialVersionUID = 1L;
   double m00;
   double m01;
   double m02;
   double m03;
   double m10;
   double m11;
   double m12;
   double m13;
   double m20;
   double m21;
   double m22;
   double m23;
   double m30;
   double m31;
   double m32;
   double m33;
   byte properties;

   public Matrix4d() {
      this.m00 = 1.0D;
      this.m11 = 1.0D;
      this.m22 = 1.0D;
      this.m33 = 1.0D;
      this.properties = 14;
   }

   public Matrix4d(Matrix4dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m03 = var1.m03();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m13 = var1.m13();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      this.m23 = var1.m23();
      this.m30 = var1.m30();
      this.m31 = var1.m31();
      this.m32 = var1.m32();
      this.m33 = var1.m33();
      this.properties = var1.properties();
   }

   public Matrix4d(Matrix4fc var1) {
      this.m00 = (double)var1.m00();
      this.m01 = (double)var1.m01();
      this.m02 = (double)var1.m02();
      this.m03 = (double)var1.m03();
      this.m10 = (double)var1.m10();
      this.m11 = (double)var1.m11();
      this.m12 = (double)var1.m12();
      this.m13 = (double)var1.m13();
      this.m20 = (double)var1.m20();
      this.m21 = (double)var1.m21();
      this.m22 = (double)var1.m22();
      this.m23 = (double)var1.m23();
      this.m30 = (double)var1.m30();
      this.m31 = (double)var1.m31();
      this.m32 = (double)var1.m32();
      this.m33 = (double)var1.m33();
      this.properties = var1.properties();
   }

   public Matrix4d(Matrix4x3dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      this.m30 = var1.m30();
      this.m31 = var1.m31();
      this.m32 = var1.m32();
      this.m33 = 1.0D;
      this.properties = (byte)(var1.properties() | 2);
   }

   public Matrix4d(Matrix4x3fc var1) {
      this.m00 = (double)var1.m00();
      this.m01 = (double)var1.m01();
      this.m02 = (double)var1.m02();
      this.m10 = (double)var1.m10();
      this.m11 = (double)var1.m11();
      this.m12 = (double)var1.m12();
      this.m20 = (double)var1.m20();
      this.m21 = (double)var1.m21();
      this.m22 = (double)var1.m22();
      this.m30 = (double)var1.m30();
      this.m31 = (double)var1.m31();
      this.m32 = (double)var1.m32();
      this.m33 = 1.0D;
      this.properties = (byte)(var1.properties() | 2);
   }

   public Matrix4d(Matrix3dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      this.m33 = 1.0D;
      this.properties = 2;
   }

   public Matrix4d(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, double var21, double var23, double var25, double var27, double var29, double var31) {
      this.m00 = var1;
      this.m01 = var3;
      this.m02 = var5;
      this.m03 = var7;
      this.m10 = var9;
      this.m11 = var11;
      this.m12 = var13;
      this.m13 = var15;
      this.m20 = var17;
      this.m21 = var19;
      this.m22 = var21;
      this.m23 = var23;
      this.m30 = var25;
      this.m31 = var27;
      this.m32 = var29;
      this.m33 = var31;
      this.properties = 0;
   }

   public Matrix4d(DoubleBuffer var1) {
      MemUtil.INSTANCE.get(this, var1.position(), var1);
   }

   public Matrix4d(Vector4d var1, Vector4d var2, Vector4d var3, Vector4d var4) {
      this.m00 = var1.x;
      this.m01 = var1.y;
      this.m02 = var1.z;
      this.m03 = var1.w;
      this.m10 = var2.x;
      this.m11 = var2.y;
      this.m12 = var2.z;
      this.m13 = var2.w;
      this.m20 = var3.x;
      this.m21 = var3.y;
      this.m22 = var3.z;
      this.m23 = var3.w;
      this.m30 = var4.x;
      this.m31 = var4.y;
      this.m32 = var4.z;
      this.m33 = var4.w;
   }

   public Matrix4d assumeNothing() {
      this.properties = 0;
      return this;
   }

   public Matrix4d assumeAffine() {
      this.properties = 2;
      return this;
   }

   public Matrix4d assumePerspective() {
      this.properties = 1;
      return this;
   }

   public byte properties() {
      return this.properties;
   }

   public double m00() {
      return this.m00;
   }

   public double m01() {
      return this.m01;
   }

   public double m02() {
      return this.m02;
   }

   public double m03() {
      return this.m03;
   }

   public double m10() {
      return this.m10;
   }

   public double m11() {
      return this.m11;
   }

   public double m12() {
      return this.m12;
   }

   public double m13() {
      return this.m13;
   }

   public double m20() {
      return this.m20;
   }

   public double m21() {
      return this.m21;
   }

   public double m22() {
      return this.m22;
   }

   public double m23() {
      return this.m23;
   }

   public double m30() {
      return this.m30;
   }

   public double m31() {
      return this.m31;
   }

   public double m32() {
      return this.m32;
   }

   public double m33() {
      return this.m33;
   }

   public Matrix4d m00(double var1) {
      this.m00 = var1;
      this.properties &= -13;
      return this;
   }

   public Matrix4d m01(double var1) {
      this.m01 = var1;
      this.properties &= -14;
      return this;
   }

   public Matrix4d m02(double var1) {
      this.m02 = var1;
      this.properties &= -14;
      return this;
   }

   public Matrix4d m03(double var1) {
      this.m03 = var1;
      this.properties = 0;
      return this;
   }

   public Matrix4d m10(double var1) {
      this.m10 = var1;
      this.properties &= -14;
      return this;
   }

   public Matrix4d m11(double var1) {
      this.m11 = var1;
      this.properties &= -13;
      return this;
   }

   public Matrix4d m12(double var1) {
      this.m12 = var1;
      this.properties &= -14;
      return this;
   }

   public Matrix4d m13(double var1) {
      this.m13 = var1;
      this.properties = 0;
      return this;
   }

   public Matrix4d m20(double var1) {
      this.m20 = var1;
      this.properties &= -14;
      return this;
   }

   public Matrix4d m21(double var1) {
      this.m21 = var1;
      this.properties &= -14;
      return this;
   }

   public Matrix4d m22(double var1) {
      this.m22 = var1;
      this.properties &= -13;
      return this;
   }

   public Matrix4d m23(double var1) {
      this.m23 = var1;
      this.properties &= -15;
      return this;
   }

   public Matrix4d m30(double var1) {
      this.m30 = var1;
      this.properties &= -6;
      return this;
   }

   public Matrix4d m31(double var1) {
      this.m31 = var1;
      this.properties &= -6;
      return this;
   }

   public Matrix4d m32(double var1) {
      this.m32 = var1;
      this.properties &= -6;
      return this;
   }

   public Matrix4d m33(double var1) {
      this.m33 = var1;
      this.properties = 0;
      return this;
   }

   public Matrix4d identity() {
      if ((this.properties & 4) != 0) {
         return this;
      } else {
         this.m00 = 1.0D;
         this.m10 = 0.0D;
         this.m20 = 0.0D;
         this.m30 = 0.0D;
         this.m01 = 0.0D;
         this.m11 = 1.0D;
         this.m21 = 0.0D;
         this.m31 = 0.0D;
         this.m02 = 0.0D;
         this.m12 = 0.0D;
         this.m22 = 1.0D;
         this.m32 = 0.0D;
         this.m03 = 0.0D;
         this.m13 = 0.0D;
         this.m23 = 0.0D;
         this.m33 = 1.0D;
         this.properties = 14;
         return this;
      }
   }

   public Matrix4d set(Matrix4dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m03 = var1.m03();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m13 = var1.m13();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      this.m23 = var1.m23();
      this.m30 = var1.m30();
      this.m31 = var1.m31();
      this.m32 = var1.m32();
      this.m33 = var1.m33();
      this.properties = var1.properties();
      return this;
   }

   public Matrix4d set(Matrix4fc var1) {
      this.m00 = (double)var1.m00();
      this.m01 = (double)var1.m01();
      this.m02 = (double)var1.m02();
      this.m03 = (double)var1.m03();
      this.m10 = (double)var1.m10();
      this.m11 = (double)var1.m11();
      this.m12 = (double)var1.m12();
      this.m13 = (double)var1.m13();
      this.m20 = (double)var1.m20();
      this.m21 = (double)var1.m21();
      this.m22 = (double)var1.m22();
      this.m23 = (double)var1.m23();
      this.m30 = (double)var1.m30();
      this.m31 = (double)var1.m31();
      this.m32 = (double)var1.m32();
      this.m33 = (double)var1.m33();
      this.properties = var1.properties();
      return this;
   }

   public Matrix4d set(Matrix4x3dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m03 = 0.0D;
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m13 = 0.0D;
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      this.m23 = 0.0D;
      this.m30 = var1.m30();
      this.m31 = var1.m31();
      this.m32 = var1.m32();
      this.m33 = 1.0D;
      this.properties = (byte)(var1.properties() | 2);
      return this;
   }

   public Matrix4d set(Matrix4x3fc var1) {
      this.m00 = (double)var1.m00();
      this.m01 = (double)var1.m01();
      this.m02 = (double)var1.m02();
      this.m03 = 0.0D;
      this.m10 = (double)var1.m10();
      this.m11 = (double)var1.m11();
      this.m12 = (double)var1.m12();
      this.m13 = 0.0D;
      this.m20 = (double)var1.m20();
      this.m21 = (double)var1.m21();
      this.m22 = (double)var1.m22();
      this.m23 = 0.0D;
      this.m30 = (double)var1.m30();
      this.m31 = (double)var1.m31();
      this.m32 = (double)var1.m32();
      this.m33 = 1.0D;
      this.properties = (byte)(var1.properties() | 2);
      return this;
   }

   public Matrix4d set(Matrix3dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m03 = 0.0D;
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m13 = 0.0D;
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d set3x3(Matrix4dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      this.properties = (byte)(this.properties & var1.properties() & -2);
      return this;
   }

   public Matrix4d set4x3(Matrix4x3dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      this.m30 = var1.m30();
      this.m31 = var1.m31();
      this.m32 = var1.m32();
      this.properties = (byte)(this.properties & var1.properties() & -2);
      return this;
   }

   public Matrix4d set4x3(Matrix4x3fc var1) {
      this.m00 = (double)var1.m00();
      this.m01 = (double)var1.m01();
      this.m02 = (double)var1.m02();
      this.m10 = (double)var1.m10();
      this.m11 = (double)var1.m11();
      this.m12 = (double)var1.m12();
      this.m20 = (double)var1.m20();
      this.m21 = (double)var1.m21();
      this.m22 = (double)var1.m22();
      this.m30 = (double)var1.m30();
      this.m31 = (double)var1.m31();
      this.m32 = (double)var1.m32();
      this.properties = (byte)(this.properties & var1.properties() & -2);
      return this;
   }

   public Matrix4d set4x3(Matrix4dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      this.m30 = var1.m30();
      this.m31 = var1.m31();
      this.m32 = var1.m32();
      this.properties = (byte)(this.properties & var1.properties() & -2);
      return this;
   }

   public Matrix4d set(AxisAngle4f var1) {
      double var2 = (double)var1.x;
      double var4 = (double)var1.y;
      double var6 = (double)var1.z;
      double var8 = (double)var1.angle;
      double var10 = 1.0D / Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
      var2 *= var10;
      var4 *= var10;
      var6 *= var10;
      double var12 = Math.cos(var8);
      double var14 = Math.sin(var8);
      double var16 = 1.0D - var12;
      this.m00 = var12 + var2 * var2 * var16;
      this.m11 = var12 + var4 * var4 * var16;
      this.m22 = var12 + var6 * var6 * var16;
      double var18 = var2 * var4 * var16;
      double var20 = var6 * var14;
      this.m10 = var18 - var20;
      this.m01 = var18 + var20;
      var18 = var2 * var6 * var16;
      var20 = var4 * var14;
      this.m20 = var18 + var20;
      this.m02 = var18 - var20;
      var18 = var4 * var6 * var16;
      var20 = var2 * var14;
      this.m21 = var18 - var20;
      this.m12 = var18 + var20;
      this.m03 = 0.0D;
      this.m13 = 0.0D;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d set(AxisAngle4d var1) {
      double var2 = var1.x;
      double var4 = var1.y;
      double var6 = var1.z;
      double var8 = var1.angle;
      double var10 = 1.0D / Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
      var2 *= var10;
      var4 *= var10;
      var6 *= var10;
      double var12 = Math.cos(var8);
      double var14 = Math.sin(var8);
      double var16 = 1.0D - var12;
      this.m00 = var12 + var2 * var2 * var16;
      this.m11 = var12 + var4 * var4 * var16;
      this.m22 = var12 + var6 * var6 * var16;
      double var18 = var2 * var4 * var16;
      double var20 = var6 * var14;
      this.m10 = var18 - var20;
      this.m01 = var18 + var20;
      var18 = var2 * var6 * var16;
      var20 = var4 * var14;
      this.m20 = var18 + var20;
      this.m02 = var18 - var20;
      var18 = var4 * var6 * var16;
      var20 = var2 * var14;
      this.m21 = var18 - var20;
      this.m12 = var18 + var20;
      this.m03 = 0.0D;
      this.m13 = 0.0D;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d set(Quaternionfc var1) {
      return this.rotation(var1);
   }

   public Matrix4d set(Quaterniondc var1) {
      return this.rotation(var1);
   }

   public Matrix4d mul(Matrix4dc var1) {
      return this.mul(var1, this);
   }

   public Matrix4d mul(Matrix4dc var1, Matrix4d var2) {
      if ((this.properties & 4) != 0) {
         return var2.set(var1);
      } else if ((var1.properties() & 4) != 0) {
         return var2.set((Matrix4dc)this);
      } else if ((this.properties & 8) != 0 && (var1.properties() & 2) != 0) {
         return this.mulTranslationAffine(var1, var2);
      } else if ((this.properties & 2) != 0 && (var1.properties() & 2) != 0) {
         return this.mulAffine(var1, var2);
      } else if ((this.properties & 1) != 0 && (var1.properties() & 2) != 0) {
         return this.mulPerspectiveAffine(var1, var2);
      } else {
         return (var1.properties() & 2) != 0 ? this.mulAffineR(var1, var2) : this.mulGeneric(var1, var2);
      }
   }

   private Matrix4d mulGeneric(Matrix4dc var1, Matrix4d var2) {
      double var3 = this.m00 * var1.m00() + this.m10 * var1.m01() + this.m20 * var1.m02() + this.m30 * var1.m03();
      double var5 = this.m01 * var1.m00() + this.m11 * var1.m01() + this.m21 * var1.m02() + this.m31 * var1.m03();
      double var7 = this.m02 * var1.m00() + this.m12 * var1.m01() + this.m22 * var1.m02() + this.m32 * var1.m03();
      double var9 = this.m03 * var1.m00() + this.m13 * var1.m01() + this.m23 * var1.m02() + this.m33 * var1.m03();
      double var11 = this.m00 * var1.m10() + this.m10 * var1.m11() + this.m20 * var1.m12() + this.m30 * var1.m13();
      double var13 = this.m01 * var1.m10() + this.m11 * var1.m11() + this.m21 * var1.m12() + this.m31 * var1.m13();
      double var15 = this.m02 * var1.m10() + this.m12 * var1.m11() + this.m22 * var1.m12() + this.m32 * var1.m13();
      double var17 = this.m03 * var1.m10() + this.m13 * var1.m11() + this.m23 * var1.m12() + this.m33 * var1.m13();
      double var19 = this.m00 * var1.m20() + this.m10 * var1.m21() + this.m20 * var1.m22() + this.m30 * var1.m23();
      double var21 = this.m01 * var1.m20() + this.m11 * var1.m21() + this.m21 * var1.m22() + this.m31 * var1.m23();
      double var23 = this.m02 * var1.m20() + this.m12 * var1.m21() + this.m22 * var1.m22() + this.m32 * var1.m23();
      double var25 = this.m03 * var1.m20() + this.m13 * var1.m21() + this.m23 * var1.m22() + this.m33 * var1.m23();
      double var27 = this.m00 * var1.m30() + this.m10 * var1.m31() + this.m20 * var1.m32() + this.m30 * var1.m33();
      double var29 = this.m01 * var1.m30() + this.m11 * var1.m31() + this.m21 * var1.m32() + this.m31 * var1.m33();
      double var31 = this.m02 * var1.m30() + this.m12 * var1.m31() + this.m22 * var1.m32() + this.m32 * var1.m33();
      double var33 = this.m03 * var1.m30() + this.m13 * var1.m31() + this.m23 * var1.m32() + this.m33 * var1.m33();
      var2.m00 = var3;
      var2.m01 = var5;
      var2.m02 = var7;
      var2.m03 = var9;
      var2.m10 = var11;
      var2.m11 = var13;
      var2.m12 = var15;
      var2.m13 = var17;
      var2.m20 = var19;
      var2.m21 = var21;
      var2.m22 = var23;
      var2.m23 = var25;
      var2.m30 = var27;
      var2.m31 = var29;
      var2.m32 = var31;
      var2.m33 = var33;
      var2.properties = 0;
      return var2;
   }

   public Matrix4d mul(Matrix4x3dc var1, Matrix4d var2) {
      if ((this.properties & 4) != 0) {
         return var2.set(var1);
      } else {
         return (var1.properties() & 4) != 0 ? var2.set((Matrix4dc)this) : this.mulAffineR(var1, var2);
      }
   }

   public Matrix4d mul(Matrix4x3fc var1, Matrix4d var2) {
      if ((this.properties & 4) != 0) {
         return var2.set(var1);
      } else {
         return (var1.properties() & 4) != 0 ? var2.set((Matrix4dc)this) : this.mulAffineR(var1, var2);
      }
   }

   public Matrix4d mul(Matrix4f var1) {
      return this.mul((Matrix4fc)var1, this);
   }

   public Matrix4d mul(Matrix4fc var1, Matrix4d var2) {
      if ((this.properties & 4) != 0) {
         return var2.set(var1);
      } else if ((var1.properties() & 4) != 0) {
         return var2.set((Matrix4dc)this);
      } else {
         double var3 = this.m00 * (double)var1.m00() + this.m10 * (double)var1.m01() + this.m20 * (double)var1.m02() + this.m30 * (double)var1.m03();
         double var5 = this.m01 * (double)var1.m00() + this.m11 * (double)var1.m01() + this.m21 * (double)var1.m02() + this.m31 * (double)var1.m03();
         double var7 = this.m02 * (double)var1.m00() + this.m12 * (double)var1.m01() + this.m22 * (double)var1.m02() + this.m32 * (double)var1.m03();
         double var9 = this.m03 * (double)var1.m00() + this.m13 * (double)var1.m01() + this.m23 * (double)var1.m02() + this.m33 * (double)var1.m03();
         double var11 = this.m00 * (double)var1.m10() + this.m10 * (double)var1.m11() + this.m20 * (double)var1.m12() + this.m30 * (double)var1.m13();
         double var13 = this.m01 * (double)var1.m10() + this.m11 * (double)var1.m11() + this.m21 * (double)var1.m12() + this.m31 * (double)var1.m13();
         double var15 = this.m02 * (double)var1.m10() + this.m12 * (double)var1.m11() + this.m22 * (double)var1.m12() + this.m32 * (double)var1.m13();
         double var17 = this.m03 * (double)var1.m10() + this.m13 * (double)var1.m11() + this.m23 * (double)var1.m12() + this.m33 * (double)var1.m13();
         double var19 = this.m00 * (double)var1.m20() + this.m10 * (double)var1.m21() + this.m20 * (double)var1.m22() + this.m30 * (double)var1.m23();
         double var21 = this.m01 * (double)var1.m20() + this.m11 * (double)var1.m21() + this.m21 * (double)var1.m22() + this.m31 * (double)var1.m23();
         double var23 = this.m02 * (double)var1.m20() + this.m12 * (double)var1.m21() + this.m22 * (double)var1.m22() + this.m32 * (double)var1.m23();
         double var25 = this.m03 * (double)var1.m20() + this.m13 * (double)var1.m21() + this.m23 * (double)var1.m22() + this.m33 * (double)var1.m23();
         double var27 = this.m00 * (double)var1.m30() + this.m10 * (double)var1.m31() + this.m20 * (double)var1.m32() + this.m30 * (double)var1.m33();
         double var29 = this.m01 * (double)var1.m30() + this.m11 * (double)var1.m31() + this.m21 * (double)var1.m32() + this.m31 * (double)var1.m33();
         double var31 = this.m02 * (double)var1.m30() + this.m12 * (double)var1.m31() + this.m22 * (double)var1.m32() + this.m32 * (double)var1.m33();
         double var33 = this.m03 * (double)var1.m30() + this.m13 * (double)var1.m31() + this.m23 * (double)var1.m32() + this.m33 * (double)var1.m33();
         var2.m00 = var3;
         var2.m01 = var5;
         var2.m02 = var7;
         var2.m03 = var9;
         var2.m10 = var11;
         var2.m11 = var13;
         var2.m12 = var15;
         var2.m13 = var17;
         var2.m20 = var19;
         var2.m21 = var21;
         var2.m22 = var23;
         var2.m23 = var25;
         var2.m30 = var27;
         var2.m31 = var29;
         var2.m32 = var31;
         var2.m33 = var33;
         var2.properties = 0;
         return var2;
      }
   }

   public Matrix4d mulPerspectiveAffine(Matrix4dc var1) {
      return this.mulPerspectiveAffine(var1, this);
   }

   public Matrix4d mulPerspectiveAffine(Matrix4dc var1, Matrix4d var2) {
      double var3 = this.m00 * var1.m00();
      double var5 = this.m11 * var1.m01();
      double var7 = this.m22 * var1.m02();
      double var9 = this.m23 * var1.m02();
      double var11 = this.m00 * var1.m10();
      double var13 = this.m11 * var1.m11();
      double var15 = this.m22 * var1.m12();
      double var17 = this.m23 * var1.m12();
      double var19 = this.m00 * var1.m20();
      double var21 = this.m11 * var1.m21();
      double var23 = this.m22 * var1.m22();
      double var25 = this.m23 * var1.m22();
      double var27 = this.m00 * var1.m30();
      double var29 = this.m11 * var1.m31();
      double var31 = this.m22 * var1.m32() + this.m32;
      double var33 = this.m23 * var1.m32();
      var2.m00 = var3;
      var2.m01 = var5;
      var2.m02 = var7;
      var2.m03 = var9;
      var2.m10 = var11;
      var2.m11 = var13;
      var2.m12 = var15;
      var2.m13 = var17;
      var2.m20 = var19;
      var2.m21 = var21;
      var2.m22 = var23;
      var2.m23 = var25;
      var2.m30 = var27;
      var2.m31 = var29;
      var2.m32 = var31;
      var2.m33 = var33;
      var2.properties = 0;
      return var2;
   }

   public Matrix4d mulAffineR(Matrix4dc var1) {
      return this.mulAffineR(var1, this);
   }

   public Matrix4d mulAffineR(Matrix4dc var1, Matrix4d var2) {
      double var3 = this.m00 * var1.m00() + this.m10 * var1.m01() + this.m20 * var1.m02();
      double var5 = this.m01 * var1.m00() + this.m11 * var1.m01() + this.m21 * var1.m02();
      double var7 = this.m02 * var1.m00() + this.m12 * var1.m01() + this.m22 * var1.m02();
      double var9 = this.m03 * var1.m00() + this.m13 * var1.m01() + this.m23 * var1.m02();
      double var11 = this.m00 * var1.m10() + this.m10 * var1.m11() + this.m20 * var1.m12();
      double var13 = this.m01 * var1.m10() + this.m11 * var1.m11() + this.m21 * var1.m12();
      double var15 = this.m02 * var1.m10() + this.m12 * var1.m11() + this.m22 * var1.m12();
      double var17 = this.m03 * var1.m10() + this.m13 * var1.m11() + this.m23 * var1.m12();
      double var19 = this.m00 * var1.m20() + this.m10 * var1.m21() + this.m20 * var1.m22();
      double var21 = this.m01 * var1.m20() + this.m11 * var1.m21() + this.m21 * var1.m22();
      double var23 = this.m02 * var1.m20() + this.m12 * var1.m21() + this.m22 * var1.m22();
      double var25 = this.m03 * var1.m20() + this.m13 * var1.m21() + this.m23 * var1.m22();
      double var27 = this.m00 * var1.m30() + this.m10 * var1.m31() + this.m20 * var1.m32() + this.m30;
      double var29 = this.m01 * var1.m30() + this.m11 * var1.m31() + this.m21 * var1.m32() + this.m31;
      double var31 = this.m02 * var1.m30() + this.m12 * var1.m31() + this.m22 * var1.m32() + this.m32;
      double var33 = this.m03 * var1.m30() + this.m13 * var1.m31() + this.m23 * var1.m32() + this.m33;
      var2.m00 = var3;
      var2.m01 = var5;
      var2.m02 = var7;
      var2.m03 = var9;
      var2.m10 = var11;
      var2.m11 = var13;
      var2.m12 = var15;
      var2.m13 = var17;
      var2.m20 = var19;
      var2.m21 = var21;
      var2.m22 = var23;
      var2.m23 = var25;
      var2.m30 = var27;
      var2.m31 = var29;
      var2.m32 = var31;
      var2.m33 = var33;
      var2.properties = (byte)(this.properties & -14);
      return var2;
   }

   public Matrix4d mulAffineR(Matrix4x3dc var1) {
      return this.mulAffineR(var1, this);
   }

   public Matrix4d mulAffineR(Matrix4x3dc var1, Matrix4d var2) {
      double var3 = this.m00 * var1.m00() + this.m10 * var1.m01() + this.m20 * var1.m02();
      double var5 = this.m01 * var1.m00() + this.m11 * var1.m01() + this.m21 * var1.m02();
      double var7 = this.m02 * var1.m00() + this.m12 * var1.m01() + this.m22 * var1.m02();
      double var9 = this.m03 * var1.m00() + this.m13 * var1.m01() + this.m23 * var1.m02();
      double var11 = this.m00 * var1.m10() + this.m10 * var1.m11() + this.m20 * var1.m12();
      double var13 = this.m01 * var1.m10() + this.m11 * var1.m11() + this.m21 * var1.m12();
      double var15 = this.m02 * var1.m10() + this.m12 * var1.m11() + this.m22 * var1.m12();
      double var17 = this.m03 * var1.m10() + this.m13 * var1.m11() + this.m23 * var1.m12();
      double var19 = this.m00 * var1.m20() + this.m10 * var1.m21() + this.m20 * var1.m22();
      double var21 = this.m01 * var1.m20() + this.m11 * var1.m21() + this.m21 * var1.m22();
      double var23 = this.m02 * var1.m20() + this.m12 * var1.m21() + this.m22 * var1.m22();
      double var25 = this.m03 * var1.m20() + this.m13 * var1.m21() + this.m23 * var1.m22();
      double var27 = this.m00 * var1.m30() + this.m10 * var1.m31() + this.m20 * var1.m32() + this.m30;
      double var29 = this.m01 * var1.m30() + this.m11 * var1.m31() + this.m21 * var1.m32() + this.m31;
      double var31 = this.m02 * var1.m30() + this.m12 * var1.m31() + this.m22 * var1.m32() + this.m32;
      double var33 = this.m03 * var1.m30() + this.m13 * var1.m31() + this.m23 * var1.m32() + this.m33;
      var2.m00 = var3;
      var2.m01 = var5;
      var2.m02 = var7;
      var2.m03 = var9;
      var2.m10 = var11;
      var2.m11 = var13;
      var2.m12 = var15;
      var2.m13 = var17;
      var2.m20 = var19;
      var2.m21 = var21;
      var2.m22 = var23;
      var2.m23 = var25;
      var2.m30 = var27;
      var2.m31 = var29;
      var2.m32 = var31;
      var2.m33 = var33;
      var2.properties = (byte)(this.properties & -14);
      return var2;
   }

   public Matrix4d mulAffineR(Matrix4x3fc var1) {
      return this.mulAffineR(var1, this);
   }

   public Matrix4d mulAffineR(Matrix4x3fc var1, Matrix4d var2) {
      double var3 = this.m00 * (double)var1.m00() + this.m10 * (double)var1.m01() + this.m20 * (double)var1.m02();
      double var5 = this.m01 * (double)var1.m00() + this.m11 * (double)var1.m01() + this.m21 * (double)var1.m02();
      double var7 = this.m02 * (double)var1.m00() + this.m12 * (double)var1.m01() + this.m22 * (double)var1.m02();
      double var9 = this.m03 * (double)var1.m00() + this.m13 * (double)var1.m01() + this.m23 * (double)var1.m02();
      double var11 = this.m00 * (double)var1.m10() + this.m10 * (double)var1.m11() + this.m20 * (double)var1.m12();
      double var13 = this.m01 * (double)var1.m10() + this.m11 * (double)var1.m11() + this.m21 * (double)var1.m12();
      double var15 = this.m02 * (double)var1.m10() + this.m12 * (double)var1.m11() + this.m22 * (double)var1.m12();
      double var17 = this.m03 * (double)var1.m10() + this.m13 * (double)var1.m11() + this.m23 * (double)var1.m12();
      double var19 = this.m00 * (double)var1.m20() + this.m10 * (double)var1.m21() + this.m20 * (double)var1.m22();
      double var21 = this.m01 * (double)var1.m20() + this.m11 * (double)var1.m21() + this.m21 * (double)var1.m22();
      double var23 = this.m02 * (double)var1.m20() + this.m12 * (double)var1.m21() + this.m22 * (double)var1.m22();
      double var25 = this.m03 * (double)var1.m20() + this.m13 * (double)var1.m21() + this.m23 * (double)var1.m22();
      double var27 = this.m00 * (double)var1.m30() + this.m10 * (double)var1.m31() + this.m20 * (double)var1.m32() + this.m30;
      double var29 = this.m01 * (double)var1.m30() + this.m11 * (double)var1.m31() + this.m21 * (double)var1.m32() + this.m31;
      double var31 = this.m02 * (double)var1.m30() + this.m12 * (double)var1.m31() + this.m22 * (double)var1.m32() + this.m32;
      double var33 = this.m03 * (double)var1.m30() + this.m13 * (double)var1.m31() + this.m23 * (double)var1.m32() + this.m33;
      var2.m00 = var3;
      var2.m01 = var5;
      var2.m02 = var7;
      var2.m03 = var9;
      var2.m10 = var11;
      var2.m11 = var13;
      var2.m12 = var15;
      var2.m13 = var17;
      var2.m20 = var19;
      var2.m21 = var21;
      var2.m22 = var23;
      var2.m23 = var25;
      var2.m30 = var27;
      var2.m31 = var29;
      var2.m32 = var31;
      var2.m33 = var33;
      var2.properties = (byte)(this.properties & -14);
      return var2;
   }

   public Matrix4d mulAffine(Matrix4dc var1) {
      return this.mulAffine(var1, this);
   }

   public Matrix4d mulAffine(Matrix4dc var1, Matrix4d var2) {
      double var3 = this.m00 * var1.m00() + this.m10 * var1.m01() + this.m20 * var1.m02();
      double var5 = this.m01 * var1.m00() + this.m11 * var1.m01() + this.m21 * var1.m02();
      double var7 = this.m02 * var1.m00() + this.m12 * var1.m01() + this.m22 * var1.m02();
      double var9 = this.m03;
      double var11 = this.m00 * var1.m10() + this.m10 * var1.m11() + this.m20 * var1.m12();
      double var13 = this.m01 * var1.m10() + this.m11 * var1.m11() + this.m21 * var1.m12();
      double var15 = this.m02 * var1.m10() + this.m12 * var1.m11() + this.m22 * var1.m12();
      double var17 = this.m13;
      double var19 = this.m00 * var1.m20() + this.m10 * var1.m21() + this.m20 * var1.m22();
      double var21 = this.m01 * var1.m20() + this.m11 * var1.m21() + this.m21 * var1.m22();
      double var23 = this.m02 * var1.m20() + this.m12 * var1.m21() + this.m22 * var1.m22();
      double var25 = this.m23;
      double var27 = this.m00 * var1.m30() + this.m10 * var1.m31() + this.m20 * var1.m32() + this.m30;
      double var29 = this.m01 * var1.m30() + this.m11 * var1.m31() + this.m21 * var1.m32() + this.m31;
      double var31 = this.m02 * var1.m30() + this.m12 * var1.m31() + this.m22 * var1.m32() + this.m32;
      double var33 = this.m33;
      var2.m00 = var3;
      var2.m01 = var5;
      var2.m02 = var7;
      var2.m03 = var9;
      var2.m10 = var11;
      var2.m11 = var13;
      var2.m12 = var15;
      var2.m13 = var17;
      var2.m20 = var19;
      var2.m21 = var21;
      var2.m22 = var23;
      var2.m23 = var25;
      var2.m30 = var27;
      var2.m31 = var29;
      var2.m32 = var31;
      var2.m33 = var33;
      var2.properties = 2;
      return var2;
   }

   public Matrix4d mulTranslationAffine(Matrix4dc var1, Matrix4d var2) {
      double var3 = var1.m00();
      double var5 = var1.m01();
      double var7 = var1.m02();
      double var9 = this.m03;
      double var11 = var1.m10();
      double var13 = var1.m11();
      double var15 = var1.m12();
      double var17 = this.m13;
      double var19 = var1.m20();
      double var21 = var1.m21();
      double var23 = var1.m22();
      double var25 = this.m23;
      double var27 = var1.m30() + this.m30;
      double var29 = var1.m31() + this.m31;
      double var31 = var1.m32() + this.m32;
      double var33 = this.m33;
      var2.m00 = var3;
      var2.m01 = var5;
      var2.m02 = var7;
      var2.m03 = var9;
      var2.m10 = var11;
      var2.m11 = var13;
      var2.m12 = var15;
      var2.m13 = var17;
      var2.m20 = var19;
      var2.m21 = var21;
      var2.m22 = var23;
      var2.m23 = var25;
      var2.m30 = var27;
      var2.m31 = var29;
      var2.m32 = var31;
      var2.m33 = var33;
      var2.properties = 2;
      return var2;
   }

   public Matrix4d mulOrthoAffine(Matrix4dc var1) {
      return this.mulOrthoAffine(var1, this);
   }

   public Matrix4d mulOrthoAffine(Matrix4dc var1, Matrix4d var2) {
      double var3 = this.m00 * var1.m00();
      double var5 = this.m11 * var1.m01();
      double var7 = this.m22 * var1.m02();
      double var9 = 0.0D;
      double var11 = this.m00 * var1.m10();
      double var13 = this.m11 * var1.m11();
      double var15 = this.m22 * var1.m12();
      double var17 = 0.0D;
      double var19 = this.m00 * var1.m20();
      double var21 = this.m11 * var1.m21();
      double var23 = this.m22 * var1.m22();
      double var25 = 0.0D;
      double var27 = this.m00 * var1.m30() + this.m30;
      double var29 = this.m11 * var1.m31() + this.m31;
      double var31 = this.m22 * var1.m32() + this.m32;
      double var33 = 1.0D;
      var2.m00 = var3;
      var2.m01 = var5;
      var2.m02 = var7;
      var2.m03 = var9;
      var2.m10 = var11;
      var2.m11 = var13;
      var2.m12 = var15;
      var2.m13 = var17;
      var2.m20 = var19;
      var2.m21 = var21;
      var2.m22 = var23;
      var2.m23 = var25;
      var2.m30 = var27;
      var2.m31 = var29;
      var2.m32 = var31;
      var2.m33 = var33;
      var2.properties = 2;
      return var2;
   }

   public Matrix4d fma4x3(Matrix4dc var1, double var2) {
      return this.fma4x3(var1, var2, this);
   }

   public Matrix4d fma4x3(Matrix4dc var1, double var2, Matrix4d var4) {
      var4.m00 = this.m00 + var1.m00() * var2;
      var4.m01 = this.m01 + var1.m01() * var2;
      var4.m02 = this.m02 + var1.m02() * var2;
      var4.m03 = this.m03;
      var4.m10 = this.m10 + var1.m10() * var2;
      var4.m11 = this.m11 + var1.m11() * var2;
      var4.m12 = this.m12 + var1.m12() * var2;
      var4.m13 = this.m13;
      var4.m20 = this.m20 + var1.m20() * var2;
      var4.m21 = this.m21 + var1.m21() * var2;
      var4.m22 = this.m22 + var1.m22() * var2;
      var4.m23 = this.m23;
      var4.m30 = this.m30 + var1.m30() * var2;
      var4.m31 = this.m31 + var1.m31() * var2;
      var4.m32 = this.m32 + var1.m32() * var2;
      var4.m33 = this.m33;
      var4.properties = 0;
      return var4;
   }

   public Matrix4d add(Matrix4dc var1) {
      return this.add(var1, this);
   }

   public Matrix4d add(Matrix4dc var1, Matrix4d var2) {
      var2.m00 = this.m00 + var1.m00();
      var2.m01 = this.m01 + var1.m01();
      var2.m02 = this.m02 + var1.m02();
      var2.m03 = this.m03 + var1.m03();
      var2.m10 = this.m10 + var1.m10();
      var2.m11 = this.m11 + var1.m11();
      var2.m12 = this.m12 + var1.m12();
      var2.m13 = this.m13 + var1.m13();
      var2.m20 = this.m20 + var1.m20();
      var2.m21 = this.m21 + var1.m21();
      var2.m22 = this.m22 + var1.m22();
      var2.m23 = this.m23 + var1.m23();
      var2.m30 = this.m30 + var1.m30();
      var2.m31 = this.m31 + var1.m31();
      var2.m32 = this.m32 + var1.m32();
      var2.m33 = this.m33 + var1.m33();
      var2.properties = 0;
      return var2;
   }

   public Matrix4d sub(Matrix4dc var1) {
      return this.sub(var1, this);
   }

   public Matrix4d sub(Matrix4dc var1, Matrix4d var2) {
      var2.m00 = this.m00 - var1.m00();
      var2.m01 = this.m01 - var1.m01();
      var2.m02 = this.m02 - var1.m02();
      var2.m03 = this.m03 - var1.m03();
      var2.m10 = this.m10 - var1.m10();
      var2.m11 = this.m11 - var1.m11();
      var2.m12 = this.m12 - var1.m12();
      var2.m13 = this.m13 - var1.m13();
      var2.m20 = this.m20 - var1.m20();
      var2.m21 = this.m21 - var1.m21();
      var2.m22 = this.m22 - var1.m22();
      var2.m23 = this.m23 - var1.m23();
      var2.m30 = this.m30 - var1.m30();
      var2.m31 = this.m31 - var1.m31();
      var2.m32 = this.m32 - var1.m32();
      var2.m33 = this.m33 - var1.m33();
      var2.properties = 0;
      return var2;
   }

   public Matrix4d mulComponentWise(Matrix4dc var1) {
      return this.mulComponentWise(var1, this);
   }

   public Matrix4d mulComponentWise(Matrix4dc var1, Matrix4d var2) {
      var2.m00 = this.m00 * var1.m00();
      var2.m01 = this.m01 * var1.m01();
      var2.m02 = this.m02 * var1.m02();
      var2.m03 = this.m03 * var1.m03();
      var2.m10 = this.m10 * var1.m10();
      var2.m11 = this.m11 * var1.m11();
      var2.m12 = this.m12 * var1.m12();
      var2.m13 = this.m13 * var1.m13();
      var2.m20 = this.m20 * var1.m20();
      var2.m21 = this.m21 * var1.m21();
      var2.m22 = this.m22 * var1.m22();
      var2.m23 = this.m23 * var1.m23();
      var2.m30 = this.m30 * var1.m30();
      var2.m31 = this.m31 * var1.m31();
      var2.m32 = this.m32 * var1.m32();
      var2.m33 = this.m33 * var1.m33();
      var2.properties = 0;
      return var2;
   }

   public Matrix4d add4x3(Matrix4dc var1) {
      return this.add4x3(var1, this);
   }

   public Matrix4d add4x3(Matrix4dc var1, Matrix4d var2) {
      var2.m00 = this.m00 + var1.m00();
      var2.m01 = this.m01 + var1.m01();
      var2.m02 = this.m02 + var1.m02();
      var2.m03 = this.m03;
      var2.m10 = this.m10 + var1.m10();
      var2.m11 = this.m11 + var1.m11();
      var2.m12 = this.m12 + var1.m12();
      var2.m13 = this.m13;
      var2.m20 = this.m20 + var1.m20();
      var2.m21 = this.m21 + var1.m21();
      var2.m22 = this.m22 + var1.m22();
      var2.m23 = this.m23;
      var2.m30 = this.m30 + var1.m30();
      var2.m31 = this.m31 + var1.m31();
      var2.m32 = this.m32 + var1.m32();
      var2.m33 = this.m33;
      var2.properties = 0;
      return var2;
   }

   public Matrix4d add4x3(Matrix4fc var1) {
      return this.add4x3(var1, this);
   }

   public Matrix4d add4x3(Matrix4fc var1, Matrix4d var2) {
      var2.m00 = this.m00 + (double)var1.m00();
      var2.m01 = this.m01 + (double)var1.m01();
      var2.m02 = this.m02 + (double)var1.m02();
      var2.m03 = this.m03;
      var2.m10 = this.m10 + (double)var1.m10();
      var2.m11 = this.m11 + (double)var1.m11();
      var2.m12 = this.m12 + (double)var1.m12();
      var2.m13 = this.m13;
      var2.m20 = this.m20 + (double)var1.m20();
      var2.m21 = this.m21 + (double)var1.m21();
      var2.m22 = this.m22 + (double)var1.m22();
      var2.m23 = this.m23;
      var2.m30 = this.m30 + (double)var1.m30();
      var2.m31 = this.m31 + (double)var1.m31();
      var2.m32 = this.m32 + (double)var1.m32();
      var2.m33 = this.m33;
      var2.properties = 0;
      return var2;
   }

   public Matrix4d sub4x3(Matrix4dc var1) {
      return this.sub4x3(var1, this);
   }

   public Matrix4d sub4x3(Matrix4dc var1, Matrix4d var2) {
      var2.m00 = this.m00 - var1.m00();
      var2.m01 = this.m01 - var1.m01();
      var2.m02 = this.m02 - var1.m02();
      var2.m03 = this.m03;
      var2.m10 = this.m10 - var1.m10();
      var2.m11 = this.m11 - var1.m11();
      var2.m12 = this.m12 - var1.m12();
      var2.m13 = this.m13;
      var2.m20 = this.m20 - var1.m20();
      var2.m21 = this.m21 - var1.m21();
      var2.m22 = this.m22 - var1.m22();
      var2.m23 = this.m23;
      var2.m30 = this.m30 - var1.m30();
      var2.m31 = this.m31 - var1.m31();
      var2.m32 = this.m32 - var1.m32();
      var2.m33 = this.m33;
      var2.properties = 0;
      return var2;
   }

   public Matrix4d mul4x3ComponentWise(Matrix4dc var1) {
      return this.mul4x3ComponentWise(var1, this);
   }

   public Matrix4d mul4x3ComponentWise(Matrix4dc var1, Matrix4d var2) {
      var2.m00 = this.m00 * var1.m00();
      var2.m01 = this.m01 * var1.m01();
      var2.m02 = this.m02 * var1.m02();
      var2.m03 = this.m03;
      var2.m10 = this.m10 * var1.m10();
      var2.m11 = this.m11 * var1.m11();
      var2.m12 = this.m12 * var1.m12();
      var2.m13 = this.m13;
      var2.m20 = this.m20 * var1.m20();
      var2.m21 = this.m21 * var1.m21();
      var2.m22 = this.m22 * var1.m22();
      var2.m23 = this.m23;
      var2.m30 = this.m30 * var1.m30();
      var2.m31 = this.m31 * var1.m31();
      var2.m32 = this.m32 * var1.m32();
      var2.m33 = this.m33;
      var2.properties = 0;
      return var2;
   }

   public Matrix4d set(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, double var21, double var23, double var25, double var27, double var29, double var31) {
      this.m00 = var1;
      this.m10 = var9;
      this.m20 = var17;
      this.m30 = var25;
      this.m01 = var3;
      this.m11 = var11;
      this.m21 = var19;
      this.m31 = var27;
      this.m02 = var5;
      this.m12 = var13;
      this.m22 = var21;
      this.m32 = var29;
      this.m03 = var7;
      this.m13 = var15;
      this.m23 = var23;
      this.m33 = var31;
      this.properties = 0;
      return this;
   }

   public Matrix4d set(double[] var1, int var2) {
      this.m00 = var1[var2 + 0];
      this.m01 = var1[var2 + 1];
      this.m02 = var1[var2 + 2];
      this.m03 = var1[var2 + 3];
      this.m10 = var1[var2 + 4];
      this.m11 = var1[var2 + 5];
      this.m12 = var1[var2 + 6];
      this.m13 = var1[var2 + 7];
      this.m20 = var1[var2 + 8];
      this.m21 = var1[var2 + 9];
      this.m22 = var1[var2 + 10];
      this.m23 = var1[var2 + 11];
      this.m30 = var1[var2 + 12];
      this.m31 = var1[var2 + 13];
      this.m32 = var1[var2 + 14];
      this.m33 = var1[var2 + 15];
      this.properties = 0;
      return this;
   }

   public Matrix4d set(double[] var1) {
      return this.set((double[])var1, 0);
   }

   public Matrix4d set(float[] var1, int var2) {
      this.m00 = (double)var1[var2 + 0];
      this.m01 = (double)var1[var2 + 1];
      this.m02 = (double)var1[var2 + 2];
      this.m03 = (double)var1[var2 + 3];
      this.m10 = (double)var1[var2 + 4];
      this.m11 = (double)var1[var2 + 5];
      this.m12 = (double)var1[var2 + 6];
      this.m13 = (double)var1[var2 + 7];
      this.m20 = (double)var1[var2 + 8];
      this.m21 = (double)var1[var2 + 9];
      this.m22 = (double)var1[var2 + 10];
      this.m23 = (double)var1[var2 + 11];
      this.m30 = (double)var1[var2 + 12];
      this.m31 = (double)var1[var2 + 13];
      this.m32 = (double)var1[var2 + 14];
      this.m33 = (double)var1[var2 + 15];
      this.properties = 0;
      return this;
   }

   public Matrix4d set(float[] var1) {
      return this.set((float[])var1, 0);
   }

   public Matrix4d set(DoubleBuffer var1) {
      MemUtil.INSTANCE.get(this, var1.position(), var1);
      this.properties = 0;
      return this;
   }

   public Matrix4d set(FloatBuffer var1) {
      MemUtil.INSTANCE.getf(this, var1.position(), var1);
      this.properties = 0;
      return this;
   }

   public Matrix4d set(ByteBuffer var1) {
      MemUtil.INSTANCE.get(this, var1.position(), var1);
      this.properties = 0;
      return this;
   }

   public Matrix4d setFloats(ByteBuffer var1) {
      MemUtil.INSTANCE.getf(this, var1.position(), var1);
      this.properties = 0;
      return this;
   }

   public Matrix4d set(Vector4d var1, Vector4d var2, Vector4d var3, Vector4d var4) {
      this.m00 = var1.x;
      this.m01 = var1.y;
      this.m02 = var1.z;
      this.m03 = var1.w;
      this.m10 = var2.x;
      this.m11 = var2.y;
      this.m12 = var2.z;
      this.m13 = var2.w;
      this.m20 = var3.x;
      this.m21 = var3.y;
      this.m22 = var3.z;
      this.m23 = var3.w;
      this.m30 = var4.x;
      this.m31 = var4.y;
      this.m32 = var4.z;
      this.m33 = var4.w;
      this.properties = 0;
      return this;
   }

   public double determinant() {
      return (this.properties & 2) != 0 ? this.determinantAffine() : (this.m00 * this.m11 - this.m01 * this.m10) * (this.m22 * this.m33 - this.m23 * this.m32) + (this.m02 * this.m10 - this.m00 * this.m12) * (this.m21 * this.m33 - this.m23 * this.m31) + (this.m00 * this.m13 - this.m03 * this.m10) * (this.m21 * this.m32 - this.m22 * this.m31) + (this.m01 * this.m12 - this.m02 * this.m11) * (this.m20 * this.m33 - this.m23 * this.m30) + (this.m03 * this.m11 - this.m01 * this.m13) * (this.m20 * this.m32 - this.m22 * this.m30) + (this.m02 * this.m13 - this.m03 * this.m12) * (this.m20 * this.m31 - this.m21 * this.m30);
   }

   public double determinant3x3() {
      return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
   }

   public double determinantAffine() {
      return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
   }

   public Matrix4d invert() {
      return this.invert(this);
   }

   public Matrix4d invert(Matrix4d var1) {
      if ((this.properties & 4) != 0) {
         return var1.identity();
      } else if ((this.properties & 2) != 0) {
         return this.invertAffine(var1);
      } else {
         return (this.properties & 1) != 0 ? this.invertPerspective(var1) : this.invertGeneric(var1);
      }
   }

   private Matrix4d invertGeneric(Matrix4d var1) {
      double var2 = this.m00 * this.m11 - this.m01 * this.m10;
      double var4 = this.m00 * this.m12 - this.m02 * this.m10;
      double var6 = this.m00 * this.m13 - this.m03 * this.m10;
      double var8 = this.m01 * this.m12 - this.m02 * this.m11;
      double var10 = this.m01 * this.m13 - this.m03 * this.m11;
      double var12 = this.m02 * this.m13 - this.m03 * this.m12;
      double var14 = this.m20 * this.m31 - this.m21 * this.m30;
      double var16 = this.m20 * this.m32 - this.m22 * this.m30;
      double var18 = this.m20 * this.m33 - this.m23 * this.m30;
      double var20 = this.m21 * this.m32 - this.m22 * this.m31;
      double var22 = this.m21 * this.m33 - this.m23 * this.m31;
      double var24 = this.m22 * this.m33 - this.m23 * this.m32;
      double var26 = var2 * var24 - var4 * var22 + var6 * var20 + var8 * var18 - var10 * var16 + var12 * var14;
      var26 = 1.0D / var26;
      double var28 = (this.m11 * var24 - this.m12 * var22 + this.m13 * var20) * var26;
      double var30 = (-this.m01 * var24 + this.m02 * var22 - this.m03 * var20) * var26;
      double var32 = (this.m31 * var12 - this.m32 * var10 + this.m33 * var8) * var26;
      double var34 = (-this.m21 * var12 + this.m22 * var10 - this.m23 * var8) * var26;
      double var36 = (-this.m10 * var24 + this.m12 * var18 - this.m13 * var16) * var26;
      double var38 = (this.m00 * var24 - this.m02 * var18 + this.m03 * var16) * var26;
      double var40 = (-this.m30 * var12 + this.m32 * var6 - this.m33 * var4) * var26;
      double var42 = (this.m20 * var12 - this.m22 * var6 + this.m23 * var4) * var26;
      double var44 = (this.m10 * var22 - this.m11 * var18 + this.m13 * var14) * var26;
      double var46 = (-this.m00 * var22 + this.m01 * var18 - this.m03 * var14) * var26;
      double var48 = (this.m30 * var10 - this.m31 * var6 + this.m33 * var2) * var26;
      double var50 = (-this.m20 * var10 + this.m21 * var6 - this.m23 * var2) * var26;
      double var52 = (-this.m10 * var20 + this.m11 * var16 - this.m12 * var14) * var26;
      double var54 = (this.m00 * var20 - this.m01 * var16 + this.m02 * var14) * var26;
      double var56 = (-this.m30 * var8 + this.m31 * var4 - this.m32 * var2) * var26;
      double var58 = (this.m20 * var8 - this.m21 * var4 + this.m22 * var2) * var26;
      var1.m00 = var28;
      var1.m01 = var30;
      var1.m02 = var32;
      var1.m03 = var34;
      var1.m10 = var36;
      var1.m11 = var38;
      var1.m12 = var40;
      var1.m13 = var42;
      var1.m20 = var44;
      var1.m21 = var46;
      var1.m22 = var48;
      var1.m23 = var50;
      var1.m30 = var52;
      var1.m31 = var54;
      var1.m32 = var56;
      var1.m33 = var58;
      var1.properties = 0;
      return var1;
   }

   public Matrix4d invertPerspective(Matrix4d var1) {
      double var2 = 1.0D / (this.m00 * this.m11);
      double var4 = -1.0D / (this.m23 * this.m32);
      var1.set(this.m11 * var2, 0.0D, 0.0D, 0.0D, 0.0D, this.m00 * var2, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, -this.m23 * var4, 0.0D, 0.0D, -this.m32 * var4, this.m22 * var4);
      return var1;
   }

   public Matrix4d invertPerspective() {
      return this.invertPerspective(this);
   }

   public Matrix4d invertFrustum(Matrix4d var1) {
      double var2 = 1.0D / this.m00;
      double var4 = 1.0D / this.m11;
      double var6 = 1.0D / this.m23;
      double var8 = 1.0D / this.m32;
      var1.set(var2, 0.0D, 0.0D, 0.0D, 0.0D, var4, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, var8, -this.m20 * var2 * var6, -this.m21 * var4 * var6, var6, -this.m22 * var6 * var8);
      var1.properties = 0;
      return var1;
   }

   public Matrix4d invertFrustum() {
      return this.invertFrustum(this);
   }

   public Matrix4d invertOrtho(Matrix4d var1) {
      double var2 = 1.0D / this.m00;
      double var4 = 1.0D / this.m11;
      double var6 = 1.0D / this.m22;
      var1.set(var2, 0.0D, 0.0D, 0.0D, 0.0D, var4, 0.0D, 0.0D, 0.0D, 0.0D, var6, 0.0D, -this.m30 * var2, -this.m31 * var4, -this.m32 * var6, 1.0D);
      var1.properties = 2;
      return var1;
   }

   public Matrix4d invertOrtho() {
      return this.invertOrtho(this);
   }

   public Matrix4d invertPerspectiveView(Matrix4dc var1, Matrix4d var2) {
      double var3 = 1.0D / (this.m00 * this.m11);
      double var5 = -1.0D / (this.m23 * this.m32);
      double var7 = this.m11 * var3;
      double var9 = this.m00 * var3;
      double var11 = -this.m23 * var5;
      double var13 = -this.m32 * var5;
      double var15 = this.m22 * var5;
      double var17 = -var1.m00() * var1.m30() - var1.m01() * var1.m31() - var1.m02() * var1.m32();
      double var19 = -var1.m10() * var1.m30() - var1.m11() * var1.m31() - var1.m12() * var1.m32();
      double var21 = -var1.m20() * var1.m30() - var1.m21() * var1.m31() - var1.m22() * var1.m32();
      var2.set(var1.m00() * var7, var1.m10() * var7, var1.m20() * var7, 0.0D, var1.m01() * var9, var1.m11() * var9, var1.m21() * var9, 0.0D, var17 * var11, var19 * var11, var21 * var11, var11, var1.m02() * var13 + var17 * var15, var1.m12() * var13 + var19 * var15, var1.m22() * var13 + var21 * var15, var15);
      var2.properties = 0;
      return var2;
   }

   public Matrix4d invertAffine(Matrix4d var1) {
      double var2 = this.determinantAffine();
      var2 = 1.0D / var2;
      double var4 = this.m10 * this.m22;
      double var6 = this.m10 * this.m21;
      double var8 = this.m10 * this.m02;
      double var10 = this.m10 * this.m01;
      double var12 = this.m11 * this.m22;
      double var14 = this.m11 * this.m20;
      double var16 = this.m11 * this.m02;
      double var18 = this.m11 * this.m00;
      double var20 = this.m12 * this.m21;
      double var22 = this.m12 * this.m20;
      double var24 = this.m12 * this.m01;
      double var26 = this.m12 * this.m00;
      double var28 = this.m20 * this.m02;
      double var30 = this.m20 * this.m01;
      double var32 = this.m21 * this.m02;
      double var34 = this.m21 * this.m00;
      double var36 = this.m22 * this.m01;
      double var38 = this.m22 * this.m00;
      double var40 = (var12 - var20) * var2;
      double var42 = (var32 - var36) * var2;
      double var44 = (var24 - var16) * var2;
      double var46 = 0.0D;
      double var48 = (var22 - var4) * var2;
      double var50 = (var38 - var28) * var2;
      double var52 = (var8 - var26) * var2;
      double var54 = 0.0D;
      double var56 = (var6 - var14) * var2;
      double var58 = (var30 - var34) * var2;
      double var60 = (var18 - var10) * var2;
      double var62 = 0.0D;
      double var64 = (var4 * this.m31 - var6 * this.m32 + var14 * this.m32 - var12 * this.m30 + var20 * this.m30 - var22 * this.m31) * var2;
      double var66 = (var28 * this.m31 - var30 * this.m32 + var34 * this.m32 - var32 * this.m30 + var36 * this.m30 - var38 * this.m31) * var2;
      double var68 = (var16 * this.m30 - var24 * this.m30 + var26 * this.m31 - var8 * this.m31 + var10 * this.m32 - var18 * this.m32) * var2;
      double var70 = 1.0D;
      var1.m00 = var40;
      var1.m01 = var42;
      var1.m02 = var44;
      var1.m03 = var46;
      var1.m10 = var48;
      var1.m11 = var50;
      var1.m12 = var52;
      var1.m13 = var54;
      var1.m20 = var56;
      var1.m21 = var58;
      var1.m22 = var60;
      var1.m23 = var62;
      var1.m30 = var64;
      var1.m31 = var66;
      var1.m32 = var68;
      var1.m33 = var70;
      var1.properties = 2;
      return var1;
   }

   public Matrix4d invertAffine() {
      return this.invertAffine(this);
   }

   public Matrix4d invertAffineUnitScale(Matrix4d var1) {
      var1.set(this.m00, this.m10, this.m20, 0.0D, this.m01, this.m11, this.m21, 0.0D, this.m02, this.m12, this.m22, 0.0D, -this.m00 * this.m30 - this.m01 * this.m31 - this.m02 * this.m32, -this.m10 * this.m30 - this.m11 * this.m31 - this.m12 * this.m32, -this.m20 * this.m30 - this.m21 * this.m31 - this.m22 * this.m32, 1.0D);
      var1.properties = 2;
      return var1;
   }

   public Matrix4d invertAffineUnitScale() {
      return this.invertAffineUnitScale(this);
   }

   public Matrix4d invertLookAt(Matrix4d var1) {
      return this.invertAffineUnitScale(var1);
   }

   public Matrix4d invertLookAt() {
      return this.invertAffineUnitScale(this);
   }

   public Matrix4d transpose() {
      return this.transpose(this);
   }

   public Matrix4d transpose(Matrix4d var1) {
      double var2 = this.m00;
      double var4 = this.m10;
      double var6 = this.m20;
      double var8 = this.m30;
      double var10 = this.m01;
      double var12 = this.m11;
      double var14 = this.m21;
      double var16 = this.m31;
      double var18 = this.m02;
      double var20 = this.m12;
      double var22 = this.m22;
      double var24 = this.m32;
      double var26 = this.m03;
      double var28 = this.m13;
      double var30 = this.m23;
      double var32 = this.m33;
      var1.m00 = var2;
      var1.m01 = var4;
      var1.m02 = var6;
      var1.m03 = var8;
      var1.m10 = var10;
      var1.m11 = var12;
      var1.m12 = var14;
      var1.m13 = var16;
      var1.m20 = var18;
      var1.m21 = var20;
      var1.m22 = var22;
      var1.m23 = var24;
      var1.m30 = var26;
      var1.m31 = var28;
      var1.m32 = var30;
      var1.m33 = var32;
      var1.properties = (byte)(this.properties & -2);
      return var1;
   }

   public Matrix4d transpose3x3() {
      return this.transpose3x3(this);
   }

   public Matrix4d transpose3x3(Matrix4d var1) {
      double var2 = this.m00;
      double var4 = this.m10;
      double var6 = this.m20;
      double var8 = this.m01;
      double var10 = this.m11;
      double var12 = this.m21;
      double var14 = this.m02;
      double var16 = this.m12;
      double var18 = this.m22;
      var1.m00 = var2;
      var1.m01 = var4;
      var1.m02 = var6;
      var1.m10 = var8;
      var1.m11 = var10;
      var1.m12 = var12;
      var1.m20 = var14;
      var1.m21 = var16;
      var1.m22 = var18;
      var1.properties = 0;
      return var1;
   }

   public Matrix3d transpose3x3(Matrix3d var1) {
      var1.m00 = this.m00;
      var1.m01 = this.m10;
      var1.m02 = this.m20;
      var1.m10 = this.m01;
      var1.m11 = this.m11;
      var1.m12 = this.m21;
      var1.m20 = this.m02;
      var1.m21 = this.m12;
      var1.m22 = this.m22;
      return var1;
   }

   public Matrix4d translation(double var1, double var3, double var5) {
      this.m00 = 1.0D;
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 1.0D;
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = 1.0D;
      this.m23 = 0.0D;
      this.m30 = var1;
      this.m31 = var3;
      this.m32 = var5;
      this.m33 = 1.0D;
      this.properties = 10;
      return this;
   }

   public Matrix4d translation(Vector3fc var1) {
      return this.translation((double)var1.x(), (double)var1.y(), (double)var1.z());
   }

   public Matrix4d translation(Vector3dc var1) {
      return this.translation(var1.x(), var1.y(), var1.z());
   }

   public Matrix4d setTranslation(double var1, double var3, double var5) {
      this.m30 = var1;
      this.m31 = var3;
      this.m32 = var5;
      this.properties &= -6;
      return this;
   }

   public Matrix4d setTranslation(Vector3dc var1) {
      return this.setTranslation(var1.x(), var1.y(), var1.z());
   }

   public Vector3d getTranslation(Vector3d var1) {
      var1.x = this.m30;
      var1.y = this.m31;
      var1.z = this.m32;
      return var1;
   }

   public Vector3d getScale(Vector3d var1) {
      var1.x = Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
      var1.y = Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
      var1.z = Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
      return var1;
   }

   public String toString() {
      DecimalFormat var1 = new DecimalFormat("  0.000E0; -");
      return this.toString(var1).replaceAll("E(\\d+)", "E+$1");
   }

   public String toString(NumberFormat var1) {
      return var1.format(this.m00) + var1.format(this.m10) + var1.format(this.m20) + var1.format(this.m30) + "\n" + var1.format(this.m01) + var1.format(this.m11) + var1.format(this.m21) + var1.format(this.m31) + "\n" + var1.format(this.m02) + var1.format(this.m12) + var1.format(this.m22) + var1.format(this.m32) + "\n" + var1.format(this.m03) + var1.format(this.m13) + var1.format(this.m23) + var1.format(this.m33) + "\n";
   }

   public Matrix4d get(Matrix4d var1) {
      return var1.set((Matrix4dc)this);
   }

   public Matrix4x3d get4x3(Matrix4x3d var1) {
      return var1.set((Matrix4dc)this);
   }

   public Matrix3d get3x3(Matrix3d var1) {
      return var1.set((Matrix4dc)this);
   }

   public Quaternionf getUnnormalizedRotation(Quaternionf var1) {
      return var1.setFromUnnormalized((Matrix4dc)this);
   }

   public Quaternionf getNormalizedRotation(Quaternionf var1) {
      return var1.setFromNormalized((Matrix4dc)this);
   }

   public Quaterniond getUnnormalizedRotation(Quaterniond var1) {
      return var1.setFromUnnormalized((Matrix4dc)this);
   }

   public Quaterniond getNormalizedRotation(Quaterniond var1) {
      return var1.setFromNormalized((Matrix4dc)this);
   }

   public DoubleBuffer get(DoubleBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public DoubleBuffer get(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.put(this, var1, var2);
      return var2;
   }

   public FloatBuffer get(FloatBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public FloatBuffer get(int var1, FloatBuffer var2) {
      MemUtil.INSTANCE.putf(this, var1, var2);
      return var2;
   }

   public ByteBuffer get(ByteBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public ByteBuffer get(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.put(this, var1, var2);
      return var2;
   }

   public ByteBuffer getFloats(ByteBuffer var1) {
      return this.getFloats(var1.position(), var1);
   }

   public ByteBuffer getFloats(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.putf(this, var1, var2);
      return var2;
   }

   public double[] get(double[] var1, int var2) {
      var1[var2 + 0] = this.m00;
      var1[var2 + 1] = this.m01;
      var1[var2 + 2] = this.m02;
      var1[var2 + 3] = this.m03;
      var1[var2 + 4] = this.m10;
      var1[var2 + 5] = this.m11;
      var1[var2 + 6] = this.m12;
      var1[var2 + 7] = this.m13;
      var1[var2 + 8] = this.m20;
      var1[var2 + 9] = this.m21;
      var1[var2 + 10] = this.m22;
      var1[var2 + 11] = this.m23;
      var1[var2 + 12] = this.m30;
      var1[var2 + 13] = this.m31;
      var1[var2 + 14] = this.m32;
      var1[var2 + 15] = this.m33;
      return var1;
   }

   public double[] get(double[] var1) {
      return this.get((double[])var1, 0);
   }

   public float[] get(float[] var1, int var2) {
      var1[var2 + 0] = (float)this.m00;
      var1[var2 + 1] = (float)this.m01;
      var1[var2 + 2] = (float)this.m02;
      var1[var2 + 3] = (float)this.m03;
      var1[var2 + 4] = (float)this.m10;
      var1[var2 + 5] = (float)this.m11;
      var1[var2 + 6] = (float)this.m12;
      var1[var2 + 7] = (float)this.m13;
      var1[var2 + 8] = (float)this.m20;
      var1[var2 + 9] = (float)this.m21;
      var1[var2 + 10] = (float)this.m22;
      var1[var2 + 11] = (float)this.m23;
      var1[var2 + 12] = (float)this.m30;
      var1[var2 + 13] = (float)this.m31;
      var1[var2 + 14] = (float)this.m32;
      var1[var2 + 15] = (float)this.m33;
      return var1;
   }

   public float[] get(float[] var1) {
      return this.get((float[])var1, 0);
   }

   public DoubleBuffer getTransposed(DoubleBuffer var1) {
      return this.getTransposed(var1.position(), var1);
   }

   public DoubleBuffer getTransposed(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.putTransposed(this, var1, var2);
      return var2;
   }

   public ByteBuffer getTransposed(ByteBuffer var1) {
      return this.getTransposed(var1.position(), var1);
   }

   public ByteBuffer getTransposed(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.putTransposed(this, var1, var2);
      return var2;
   }

   public DoubleBuffer get4x3Transposed(DoubleBuffer var1) {
      return this.get4x3Transposed(var1.position(), var1);
   }

   public DoubleBuffer get4x3Transposed(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.put4x3Transposed(this, var1, var2);
      return var2;
   }

   public ByteBuffer get4x3Transposed(ByteBuffer var1) {
      return this.get4x3Transposed(var1.position(), var1);
   }

   public ByteBuffer get4x3Transposed(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.put4x3Transposed(this, var1, var2);
      return var2;
   }

   public Matrix4d zero() {
      this.m00 = 0.0D;
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 0.0D;
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = 0.0D;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 0.0D;
      this.properties = 0;
      return this;
   }

   public Matrix4d scaling(double var1) {
      return this.scaling(var1, var1, var1);
   }

   public Matrix4d scaling(double var1, double var3, double var5) {
      this.m00 = var1;
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = var3;
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = var5;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d scaling(Vector3dc var1) {
      return this.scaling(var1.x(), var1.y(), var1.z());
   }

   public Matrix4d rotation(double var1, double var3, double var5, double var7) {
      double var9 = Math.cos(var1);
      double var11 = Math.sin(var1);
      double var13 = 1.0D - var9;
      double var15 = var3 * var5;
      double var17 = var3 * var7;
      double var19 = var5 * var7;
      this.m00 = var9 + var3 * var3 * var13;
      this.m10 = var15 * var13 - var7 * var11;
      this.m20 = var17 * var13 + var5 * var11;
      this.m30 = 0.0D;
      this.m01 = var15 * var13 + var7 * var11;
      this.m11 = var9 + var5 * var5 * var13;
      this.m21 = var19 * var13 - var3 * var11;
      this.m31 = 0.0D;
      this.m02 = var17 * var13 - var5 * var11;
      this.m12 = var19 * var13 + var3 * var11;
      this.m22 = var9 + var7 * var7 * var13;
      this.m32 = 0.0D;
      this.m03 = 0.0D;
      this.m13 = 0.0D;
      this.m23 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d rotationX(double var1) {
      double var3 = Math.sin(var1);
      double var5 = Math.cos(var1);
      this.m00 = 1.0D;
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = var5;
      this.m12 = var3;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = -var3;
      this.m22 = var5;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d rotationY(double var1) {
      double var3 = Math.sin(var1);
      double var5 = Math.cos(var1);
      this.m00 = var5;
      this.m01 = 0.0D;
      this.m02 = -var3;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 1.0D;
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = var3;
      this.m21 = 0.0D;
      this.m22 = var5;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d rotationZ(double var1) {
      double var3 = Math.sin(var1);
      double var5 = Math.cos(var1);
      this.m00 = var5;
      this.m01 = var3;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = -var3;
      this.m11 = var5;
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = 1.0D;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d rotationXYZ(double var1, double var3, double var5) {
      double var7 = Math.cos(var1);
      double var9 = Math.sin(var1);
      double var11 = Math.cos(var3);
      double var13 = Math.sin(var3);
      double var15 = Math.cos(var5);
      double var17 = Math.sin(var5);
      double var19 = -var9;
      double var21 = -var13;
      double var23 = -var17;
      double var35 = var19 * var21;
      double var37 = var7 * var21;
      this.m20 = var13;
      this.m21 = var19 * var11;
      this.m22 = var7 * var11;
      this.m23 = 0.0D;
      this.m00 = var11 * var15;
      this.m01 = var35 * var15 + var7 * var17;
      this.m02 = var37 * var15 + var9 * var17;
      this.m03 = 0.0D;
      this.m10 = var11 * var23;
      this.m11 = var35 * var23 + var7 * var15;
      this.m12 = var37 * var23 + var9 * var15;
      this.m13 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d rotationZYX(double var1, double var3, double var5) {
      double var7 = Math.cos(var1);
      double var9 = Math.sin(var1);
      double var11 = Math.cos(var3);
      double var13 = Math.sin(var3);
      double var15 = Math.cos(var5);
      double var17 = Math.sin(var5);
      double var19 = -var9;
      double var21 = -var13;
      double var23 = -var17;
      double var33 = var7 * var13;
      double var35 = var9 * var13;
      this.m00 = var7 * var11;
      this.m01 = var9 * var11;
      this.m02 = var21;
      this.m03 = 0.0D;
      this.m10 = var19 * var15 + var33 * var17;
      this.m11 = var7 * var15 + var35 * var17;
      this.m12 = var11 * var17;
      this.m13 = 0.0D;
      this.m20 = var19 * var23 + var33 * var15;
      this.m21 = var7 * var23 + var35 * var15;
      this.m22 = var11 * var15;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d rotationYXZ(double var1, double var3, double var5) {
      double var7 = Math.cos(var1);
      double var9 = Math.sin(var1);
      double var11 = Math.cos(var3);
      double var13 = Math.sin(var3);
      double var15 = Math.cos(var5);
      double var17 = Math.sin(var5);
      double var19 = -var9;
      double var21 = -var13;
      double var23 = -var17;
      double var33 = var9 * var13;
      double var37 = var7 * var13;
      this.m20 = var9 * var11;
      this.m21 = var21;
      this.m22 = var7 * var11;
      this.m23 = 0.0D;
      this.m00 = var7 * var15 + var33 * var17;
      this.m01 = var11 * var17;
      this.m02 = var19 * var15 + var37 * var17;
      this.m03 = 0.0D;
      this.m10 = var7 * var23 + var33 * var15;
      this.m11 = var11 * var15;
      this.m12 = var19 * var23 + var37 * var15;
      this.m13 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d setRotationXYZ(double var1, double var3, double var5) {
      double var7 = Math.cos(var1);
      double var9 = Math.sin(var1);
      double var11 = Math.cos(var3);
      double var13 = Math.sin(var3);
      double var15 = Math.cos(var5);
      double var17 = Math.sin(var5);
      double var19 = -var9;
      double var21 = -var13;
      double var23 = -var17;
      double var35 = var19 * var21;
      double var37 = var7 * var21;
      this.m20 = var13;
      this.m21 = var19 * var11;
      this.m22 = var7 * var11;
      this.m00 = var11 * var15;
      this.m01 = var35 * var15 + var7 * var17;
      this.m02 = var37 * var15 + var9 * var17;
      this.m10 = var11 * var23;
      this.m11 = var35 * var23 + var7 * var15;
      this.m12 = var37 * var23 + var9 * var15;
      this.properties &= -14;
      return this;
   }

   public Matrix4d setRotationZYX(double var1, double var3, double var5) {
      double var7 = Math.cos(var1);
      double var9 = Math.sin(var1);
      double var11 = Math.cos(var3);
      double var13 = Math.sin(var3);
      double var15 = Math.cos(var5);
      double var17 = Math.sin(var5);
      double var19 = -var9;
      double var21 = -var13;
      double var23 = -var17;
      double var33 = var7 * var13;
      double var35 = var9 * var13;
      this.m00 = var7 * var11;
      this.m01 = var9 * var11;
      this.m02 = var21;
      this.m10 = var19 * var15 + var33 * var17;
      this.m11 = var7 * var15 + var35 * var17;
      this.m12 = var11 * var17;
      this.m20 = var19 * var23 + var33 * var15;
      this.m21 = var7 * var23 + var35 * var15;
      this.m22 = var11 * var15;
      this.properties &= -14;
      return this;
   }

   public Matrix4d setRotationYXZ(double var1, double var3, double var5) {
      double var7 = Math.cos(var1);
      double var9 = Math.sin(var1);
      double var11 = Math.cos(var3);
      double var13 = Math.sin(var3);
      double var15 = Math.cos(var5);
      double var17 = Math.sin(var5);
      double var19 = -var9;
      double var21 = -var13;
      double var23 = -var17;
      double var33 = var9 * var13;
      double var37 = var7 * var13;
      this.m20 = var9 * var11;
      this.m21 = var21;
      this.m22 = var7 * var11;
      this.m00 = var7 * var15 + var33 * var17;
      this.m01 = var11 * var17;
      this.m02 = var19 * var15 + var37 * var17;
      this.m10 = var7 * var23 + var33 * var15;
      this.m11 = var11 * var15;
      this.m12 = var19 * var23 + var37 * var15;
      this.properties &= -14;
      return this;
   }

   public Matrix4d rotation(double var1, Vector3dc var3) {
      return this.rotation(var1, var3.x(), var3.y(), var3.z());
   }

   public Matrix4d rotation(double var1, Vector3fc var3) {
      return this.rotation(var1, (double)var3.x(), (double)var3.y(), (double)var3.z());
   }

   public Vector4d transform(Vector4d var1) {
      return var1.mul((Matrix4dc)this);
   }

   public Vector4d transform(Vector4dc var1, Vector4d var2) {
      return var1.mul((Matrix4dc)this, var2);
   }

   public Vector4d transform(double var1, double var3, double var5, double var7, Vector4d var9) {
      var9.set(this.m00 * var1 + this.m10 * var3 + this.m20 * var5 + this.m30 * var7, this.m01 * var1 + this.m11 * var3 + this.m21 * var5 + this.m31 * var7, this.m02 * var1 + this.m12 * var3 + this.m22 * var5 + this.m32 * var7, this.m03 * var1 + this.m13 * var3 + this.m23 * var5 + this.m33 * var7);
      return var9;
   }

   public Vector4d transformProject(Vector4d var1) {
      return var1.mulProject(this);
   }

   public Vector4d transformProject(Vector4dc var1, Vector4d var2) {
      return var1.mulProject(this, var2);
   }

   public Vector4d transformProject(double var1, double var3, double var5, double var7, Vector4d var9) {
      double var10 = 1.0D / (this.m03 * var1 + this.m13 * var3 + this.m23 * var5 + this.m33 * var7);
      var9.set((this.m00 * var1 + this.m10 * var3 + this.m20 * var5 + this.m30 * var7) * var10, (this.m01 * var1 + this.m11 * var3 + this.m21 * var5 + this.m31 * var7) * var10, (this.m02 * var1 + this.m12 * var3 + this.m22 * var5 + this.m32 * var7) * var10, 1.0D);
      return var9;
   }

   public Vector3d transformProject(Vector3d var1) {
      return var1.mulProject((Matrix4dc)this);
   }

   public Vector3d transformProject(Vector3dc var1, Vector3d var2) {
      return var1.mulProject((Matrix4dc)this, var2);
   }

   public Vector3d transformProject(double var1, double var3, double var5, Vector3d var7) {
      double var8 = 1.0D / (this.m03 * var1 + this.m13 * var3 + this.m23 * var5 + this.m33);
      var7.set((this.m00 * var1 + this.m10 * var3 + this.m20 * var5 + this.m30) * var8, (this.m01 * var1 + this.m11 * var3 + this.m21 * var5 + this.m31) * var8, (this.m02 * var1 + this.m12 * var3 + this.m22 * var5 + this.m32) * var8);
      return var7;
   }

   public Vector3d transformPosition(Vector3d var1) {
      var1.set(this.m00 * var1.x + this.m10 * var1.y + this.m20 * var1.z + this.m30, this.m01 * var1.x + this.m11 * var1.y + this.m21 * var1.z + this.m31, this.m02 * var1.x + this.m12 * var1.y + this.m22 * var1.z + this.m32);
      return var1;
   }

   public Vector3d transformPosition(Vector3dc var1, Vector3d var2) {
      return this.transformPosition(var1.x(), var1.y(), var1.z(), var2);
   }

   public Vector3d transformPosition(double var1, double var3, double var5, Vector3d var7) {
      var7.set(this.m00 * var1 + this.m10 * var3 + this.m20 * var5 + this.m30, this.m01 * var1 + this.m11 * var3 + this.m21 * var5 + this.m31, this.m02 * var1 + this.m12 * var3 + this.m22 * var5 + this.m32);
      return var7;
   }

   public Vector3d transformDirection(Vector3d var1) {
      var1.set(this.m00 * var1.x + this.m10 * var1.y + this.m20 * var1.z, this.m01 * var1.x + this.m11 * var1.y + this.m21 * var1.z, this.m02 * var1.x + this.m12 * var1.y + this.m22 * var1.z);
      return var1;
   }

   public Vector3d transformDirection(Vector3dc var1, Vector3d var2) {
      var2.set(this.m00 * var1.x() + this.m10 * var1.y() + this.m20 * var1.z(), this.m01 * var1.x() + this.m11 * var1.y() + this.m21 * var1.z(), this.m02 * var1.x() + this.m12 * var1.y() + this.m22 * var1.z());
      return var2;
   }

   public Vector3d transformDirection(double var1, double var3, double var5, Vector3d var7) {
      var7.set(this.m00 * var1 + this.m10 * var3 + this.m20 * var5, this.m01 * var1 + this.m11 * var3 + this.m21 * var5, this.m02 * var1 + this.m12 * var3 + this.m22 * var5);
      return var7;
   }

   public Vector4d transformAffine(Vector4d var1) {
      var1.set(this.m00 * var1.x + this.m10 * var1.y + this.m20 * var1.z + this.m30 * var1.w, this.m01 * var1.x + this.m11 * var1.y + this.m21 * var1.z + this.m31 * var1.w, this.m02 * var1.x + this.m12 * var1.y + this.m22 * var1.z + this.m32 * var1.w, var1.w);
      return var1;
   }

   public Vector4d transformAffine(Vector4dc var1, Vector4d var2) {
      return this.transformAffine(var1.x(), var1.y(), var1.z(), var1.w(), var2);
   }

   public Vector4d transformAffine(double var1, double var3, double var5, double var7, Vector4d var9) {
      var9.set(this.m00 * var1 + this.m10 * var3 + this.m20 * var5 + this.m30 * var7, this.m01 * var1 + this.m11 * var3 + this.m21 * var5 + this.m31 * var7, this.m02 * var1 + this.m12 * var3 + this.m22 * var5 + this.m32 * var7, var7);
      return var9;
   }

   public Matrix4d set3x3(Matrix3dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      this.properties &= -14;
      return this;
   }

   public Matrix4d scale(Vector3dc var1, Matrix4d var2) {
      return this.scale(var1.x(), var1.y(), var1.z(), var2);
   }

   public Matrix4d scale(Vector3dc var1) {
      return this.scale(var1.x(), var1.y(), var1.z(), this);
   }

   public Matrix4d scale(double var1, double var3, double var5, Matrix4d var7) {
      return (this.properties & 4) != 0 ? var7.scaling(var1, var3, var5) : this.scaleGeneric(var1, var3, var5, var7);
   }

   private Matrix4d scaleGeneric(double var1, double var3, double var5, Matrix4d var7) {
      var7.m00 = this.m00 * var1;
      var7.m01 = this.m01 * var1;
      var7.m02 = this.m02 * var1;
      var7.m03 = this.m03 * var1;
      var7.m10 = this.m10 * var3;
      var7.m11 = this.m11 * var3;
      var7.m12 = this.m12 * var3;
      var7.m13 = this.m13 * var3;
      var7.m20 = this.m20 * var5;
      var7.m21 = this.m21 * var5;
      var7.m22 = this.m22 * var5;
      var7.m23 = this.m23 * var5;
      var7.m30 = this.m30;
      var7.m31 = this.m31;
      var7.m32 = this.m32;
      var7.m33 = this.m33;
      var7.properties = (byte)(this.properties & -14);
      return var7;
   }

   public Matrix4d scale(double var1, double var3, double var5) {
      return this.scale(var1, var3, var5, this);
   }

   public Matrix4d scale(double var1, Matrix4d var3) {
      return this.scale(var1, var1, var1, var3);
   }

   public Matrix4d scale(double var1) {
      return this.scale(var1, var1, var1);
   }

   public Matrix4d scaleAround(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
      double var14 = this.m00 * var7 + this.m10 * var9 + this.m20 * var11 + this.m30;
      double var16 = this.m01 * var7 + this.m11 * var9 + this.m21 * var11 + this.m31;
      double var18 = this.m02 * var7 + this.m12 * var9 + this.m22 * var11 + this.m32;
      double var20 = this.m03 * var7 + this.m13 * var9 + this.m23 * var11 + this.m33;
      var13.m00 = this.m00 * var1;
      var13.m01 = this.m01 * var1;
      var13.m02 = this.m02 * var1;
      var13.m03 = this.m03 * var1;
      var13.m10 = this.m10 * var3;
      var13.m11 = this.m11 * var3;
      var13.m12 = this.m12 * var3;
      var13.m13 = this.m13 * var3;
      var13.m20 = this.m20 * var5;
      var13.m21 = this.m21 * var5;
      var13.m22 = this.m22 * var5;
      var13.m23 = this.m23 * var5;
      var13.m30 = -this.m00 * var7 - this.m10 * var9 - this.m20 * var11 + var14;
      var13.m31 = -this.m01 * var7 - this.m11 * var9 - this.m21 * var11 + var16;
      var13.m32 = -this.m02 * var7 - this.m12 * var9 - this.m22 * var11 + var18;
      var13.m33 = -this.m03 * var7 - this.m13 * var9 - this.m23 * var11 + var20;
      var13.properties = (byte)(this.properties & -14);
      return var13;
   }

   public Matrix4d scaleAround(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.scaleAround(var1, var3, var5, var7, var9, var11, this);
   }

   public Matrix4d scaleAround(double var1, double var3, double var5, double var7) {
      return this.scaleAround(var1, var1, var1, var3, var5, var7, this);
   }

   public Matrix4d scaleAround(double var1, double var3, double var5, double var7, Matrix4d var9) {
      return this.scaleAround(var1, var1, var1, var3, var5, var7, var9);
   }

   public Matrix4d scaleLocal(double var1, double var3, double var5, Matrix4d var7) {
      if ((this.properties & 4) != 0) {
         return var7.scaling(var1, var3, var5);
      } else {
         double var8 = var1 * this.m00;
         double var10 = var3 * this.m01;
         double var12 = var5 * this.m02;
         double var14 = this.m03;
         double var16 = var1 * this.m10;
         double var18 = var3 * this.m11;
         double var20 = var5 * this.m12;
         double var22 = this.m13;
         double var24 = var1 * this.m20;
         double var26 = var3 * this.m21;
         double var28 = var5 * this.m22;
         double var30 = this.m23;
         double var32 = var1 * this.m30;
         double var34 = var3 * this.m31;
         double var36 = var5 * this.m32;
         double var38 = this.m33;
         var7.m00 = var8;
         var7.m01 = var10;
         var7.m02 = var12;
         var7.m03 = var14;
         var7.m10 = var16;
         var7.m11 = var18;
         var7.m12 = var20;
         var7.m13 = var22;
         var7.m20 = var24;
         var7.m21 = var26;
         var7.m22 = var28;
         var7.m23 = var30;
         var7.m30 = var32;
         var7.m31 = var34;
         var7.m32 = var36;
         var7.m33 = var38;
         var7.properties = (byte)(this.properties & -14);
         return var7;
      }
   }

   public Matrix4d scaleLocal(double var1, double var3, double var5) {
      return this.scaleLocal(var1, var3, var5, this);
   }

   public Matrix4d scaleAroundLocal(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
      var13.m00 = var1 * (this.m00 - var7 * this.m03) + var7 * this.m03;
      var13.m01 = var3 * (this.m01 - var9 * this.m03) + var9 * this.m03;
      var13.m02 = var5 * (this.m02 - var11 * this.m03) + var11 * this.m03;
      var13.m03 = this.m03;
      var13.m10 = var1 * (this.m10 - var7 * this.m13) + var7 * this.m13;
      var13.m11 = var3 * (this.m11 - var9 * this.m13) + var9 * this.m13;
      var13.m12 = var5 * (this.m12 - var11 * this.m13) + var11 * this.m13;
      var13.m13 = this.m13;
      var13.m20 = var1 * (this.m20 - var7 * this.m23) + var7 * this.m23;
      var13.m21 = var3 * (this.m21 - var9 * this.m23) + var9 * this.m23;
      var13.m22 = var5 * (this.m22 - var11 * this.m23) + var11 * this.m23;
      var13.m23 = this.m23;
      var13.m30 = var1 * (this.m30 - var7 * this.m33) + var7 * this.m33;
      var13.m31 = var3 * (this.m31 - var9 * this.m33) + var9 * this.m33;
      var13.m32 = var5 * (this.m32 - var11 * this.m33) + var11 * this.m33;
      var13.m33 = this.m33;
      var13.properties = (byte)(this.properties & -14);
      return var13;
   }

   public Matrix4d scaleAroundLocal(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.scaleAroundLocal(var1, var3, var5, var7, var9, var11, this);
   }

   public Matrix4d scaleAroundLocal(double var1, double var3, double var5, double var7) {
      return this.scaleAroundLocal(var1, var1, var1, var3, var5, var7, this);
   }

   public Matrix4d scaleAroundLocal(double var1, double var3, double var5, double var7, Matrix4d var9) {
      return this.scaleAroundLocal(var1, var1, var1, var3, var5, var7, var9);
   }

   public Matrix4d rotate(double var1, double var3, double var5, double var7, Matrix4d var9) {
      if ((this.properties & 4) != 0) {
         return var9.rotation(var1, var3, var5, var7);
      } else if ((this.properties & 8) != 0) {
         return this.rotateTranslation(var1, var3, var5, var7, var9);
      } else {
         return (this.properties & 2) != 0 ? this.rotateAffine(var1, var3, var5, var7, var9) : this.rotateGeneric(var1, var3, var5, var7, var9);
      }
   }

   private Matrix4d rotateGeneric(double var1, double var3, double var5, double var7, Matrix4d var9) {
      double var10 = Math.sin(var1);
      double var12 = Math.cos(var1);
      double var14 = 1.0D - var12;
      double var16 = var3 * var3;
      double var18 = var3 * var5;
      double var20 = var3 * var7;
      double var22 = var5 * var5;
      double var24 = var5 * var7;
      double var26 = var7 * var7;
      double var28 = var16 * var14 + var12;
      double var30 = var18 * var14 + var7 * var10;
      double var32 = var20 * var14 - var5 * var10;
      double var34 = var18 * var14 - var7 * var10;
      double var36 = var22 * var14 + var12;
      double var38 = var24 * var14 + var3 * var10;
      double var40 = var20 * var14 + var5 * var10;
      double var42 = var24 * var14 - var3 * var10;
      double var44 = var26 * var14 + var12;
      double var46 = this.m00 * var28 + this.m10 * var30 + this.m20 * var32;
      double var48 = this.m01 * var28 + this.m11 * var30 + this.m21 * var32;
      double var50 = this.m02 * var28 + this.m12 * var30 + this.m22 * var32;
      double var52 = this.m03 * var28 + this.m13 * var30 + this.m23 * var32;
      double var54 = this.m00 * var34 + this.m10 * var36 + this.m20 * var38;
      double var56 = this.m01 * var34 + this.m11 * var36 + this.m21 * var38;
      double var58 = this.m02 * var34 + this.m12 * var36 + this.m22 * var38;
      double var60 = this.m03 * var34 + this.m13 * var36 + this.m23 * var38;
      var9.m20 = this.m00 * var40 + this.m10 * var42 + this.m20 * var44;
      var9.m21 = this.m01 * var40 + this.m11 * var42 + this.m21 * var44;
      var9.m22 = this.m02 * var40 + this.m12 * var42 + this.m22 * var44;
      var9.m23 = this.m03 * var40 + this.m13 * var42 + this.m23 * var44;
      var9.m00 = var46;
      var9.m01 = var48;
      var9.m02 = var50;
      var9.m03 = var52;
      var9.m10 = var54;
      var9.m11 = var56;
      var9.m12 = var58;
      var9.m13 = var60;
      var9.m30 = this.m30;
      var9.m31 = this.m31;
      var9.m32 = this.m32;
      var9.m33 = this.m33;
      var9.properties = (byte)(this.properties & -14);
      return var9;
   }

   public Matrix4d rotate(double var1, double var3, double var5, double var7) {
      return this.rotate(var1, var3, var5, var7, this);
   }

   public Matrix4d rotateTranslation(double var1, double var3, double var5, double var7, Matrix4d var9) {
      double var10 = Math.sin(var1);
      double var12 = Math.cos(var1);
      double var14 = 1.0D - var12;
      double var16 = var3 * var3;
      double var18 = var3 * var5;
      double var20 = var3 * var7;
      double var22 = var5 * var5;
      double var24 = var5 * var7;
      double var26 = var7 * var7;
      double var28 = var16 * var14 + var12;
      double var30 = var18 * var14 + var7 * var10;
      double var32 = var20 * var14 - var5 * var10;
      double var34 = var18 * var14 - var7 * var10;
      double var36 = var22 * var14 + var12;
      double var38 = var24 * var14 + var3 * var10;
      double var40 = var20 * var14 + var5 * var10;
      double var42 = var24 * var14 - var3 * var10;
      double var44 = var26 * var14 + var12;
      var9.m20 = var40;
      var9.m21 = var42;
      var9.m22 = var44;
      var9.m00 = var28;
      var9.m01 = var30;
      var9.m02 = var32;
      var9.m03 = 0.0D;
      var9.m10 = var34;
      var9.m11 = var36;
      var9.m12 = var38;
      var9.m13 = 0.0D;
      var9.m30 = this.m30;
      var9.m31 = this.m31;
      var9.m32 = this.m32;
      var9.m33 = this.m33;
      var9.properties = (byte)(this.properties & -14);
      return var9;
   }

   public Matrix4d rotateAffine(double var1, double var3, double var5, double var7, Matrix4d var9) {
      double var10 = Math.sin(var1);
      double var12 = Math.cos(var1);
      double var14 = 1.0D - var12;
      double var16 = var3 * var3;
      double var18 = var3 * var5;
      double var20 = var3 * var7;
      double var22 = var5 * var5;
      double var24 = var5 * var7;
      double var26 = var7 * var7;
      double var28 = var16 * var14 + var12;
      double var30 = var18 * var14 + var7 * var10;
      double var32 = var20 * var14 - var5 * var10;
      double var34 = var18 * var14 - var7 * var10;
      double var36 = var22 * var14 + var12;
      double var38 = var24 * var14 + var3 * var10;
      double var40 = var20 * var14 + var5 * var10;
      double var42 = var24 * var14 - var3 * var10;
      double var44 = var26 * var14 + var12;
      double var46 = this.m00 * var28 + this.m10 * var30 + this.m20 * var32;
      double var48 = this.m01 * var28 + this.m11 * var30 + this.m21 * var32;
      double var50 = this.m02 * var28 + this.m12 * var30 + this.m22 * var32;
      double var52 = this.m00 * var34 + this.m10 * var36 + this.m20 * var38;
      double var54 = this.m01 * var34 + this.m11 * var36 + this.m21 * var38;
      double var56 = this.m02 * var34 + this.m12 * var36 + this.m22 * var38;
      var9.m20 = this.m00 * var40 + this.m10 * var42 + this.m20 * var44;
      var9.m21 = this.m01 * var40 + this.m11 * var42 + this.m21 * var44;
      var9.m22 = this.m02 * var40 + this.m12 * var42 + this.m22 * var44;
      var9.m23 = 0.0D;
      var9.m00 = var46;
      var9.m01 = var48;
      var9.m02 = var50;
      var9.m03 = 0.0D;
      var9.m10 = var52;
      var9.m11 = var54;
      var9.m12 = var56;
      var9.m13 = 0.0D;
      var9.m30 = this.m30;
      var9.m31 = this.m31;
      var9.m32 = this.m32;
      var9.m33 = this.m33;
      var9.properties = (byte)(this.properties & -14);
      return var9;
   }

   public Matrix4d rotateAffine(double var1, double var3, double var5, double var7) {
      return this.rotateAffine(var1, var3, var5, var7, this);
   }

   public Matrix4d rotateAround(Quaterniondc var1, double var2, double var4, double var6) {
      return this.rotateAround(var1, var2, var4, var6, this);
   }

   public Matrix4d rotateAround(Quaterniondc var1, double var2, double var4, double var6, Matrix4d var8) {
      double var9 = var1.w() * var1.w();
      double var11 = var1.x() * var1.x();
      double var13 = var1.y() * var1.y();
      double var15 = var1.z() * var1.z();
      double var17 = var1.z() * var1.w();
      double var19 = var1.x() * var1.y();
      double var21 = var1.x() * var1.z();
      double var23 = var1.y() * var1.w();
      double var25 = var1.y() * var1.z();
      double var27 = var1.x() * var1.w();
      double var29 = var9 + var11 - var15 - var13;
      double var31 = var19 + var17 + var17 + var19;
      double var33 = var21 - var23 + var21 - var23;
      double var35 = -var17 + var19 - var17 + var19;
      double var37 = var13 - var15 + var9 - var11;
      double var39 = var25 + var25 + var27 + var27;
      double var41 = var23 + var21 + var21 + var23;
      double var43 = var25 + var25 - var27 - var27;
      double var45 = var15 - var13 - var11 + var9;
      double var47 = this.m00 * var2 + this.m10 * var4 + this.m20 * var6 + this.m30;
      double var49 = this.m01 * var2 + this.m11 * var4 + this.m21 * var6 + this.m31;
      double var51 = this.m02 * var2 + this.m12 * var4 + this.m22 * var6 + this.m32;
      double var53 = this.m00 * var29 + this.m10 * var31 + this.m20 * var33;
      double var55 = this.m01 * var29 + this.m11 * var31 + this.m21 * var33;
      double var57 = this.m02 * var29 + this.m12 * var31 + this.m22 * var33;
      double var59 = this.m03 * var29 + this.m13 * var31 + this.m23 * var33;
      double var61 = this.m00 * var35 + this.m10 * var37 + this.m20 * var39;
      double var63 = this.m01 * var35 + this.m11 * var37 + this.m21 * var39;
      double var65 = this.m02 * var35 + this.m12 * var37 + this.m22 * var39;
      double var67 = this.m03 * var35 + this.m13 * var37 + this.m23 * var39;
      var8.m20 = this.m00 * var41 + this.m10 * var43 + this.m20 * var45;
      var8.m21 = this.m01 * var41 + this.m11 * var43 + this.m21 * var45;
      var8.m22 = this.m02 * var41 + this.m12 * var43 + this.m22 * var45;
      var8.m23 = this.m03 * var41 + this.m13 * var43 + this.m23 * var45;
      var8.m00 = var53;
      var8.m01 = var55;
      var8.m02 = var57;
      var8.m03 = var59;
      var8.m10 = var61;
      var8.m11 = var63;
      var8.m12 = var65;
      var8.m13 = var67;
      var8.m30 = -var53 * var2 - var61 * var4 - this.m20 * var6 + var47;
      var8.m31 = -var55 * var2 - var63 * var4 - this.m21 * var6 + var49;
      var8.m32 = -var57 * var2 - var65 * var4 - this.m22 * var6 + var51;
      var8.m33 = this.m33;
      var8.properties = (byte)(this.properties & -14);
      return var8;
   }

   public Matrix4d rotateLocal(double var1, double var3, double var5, double var7, Matrix4d var9) {
      double var10 = Math.sin(var1);
      double var12 = Math.cos(var1);
      double var14 = 1.0D - var12;
      double var16 = var3 * var3;
      double var18 = var3 * var5;
      double var20 = var3 * var7;
      double var22 = var5 * var5;
      double var24 = var5 * var7;
      double var26 = var7 * var7;
      double var28 = var16 * var14 + var12;
      double var30 = var18 * var14 + var7 * var10;
      double var32 = var20 * var14 - var5 * var10;
      double var34 = var18 * var14 - var7 * var10;
      double var36 = var22 * var14 + var12;
      double var38 = var24 * var14 + var3 * var10;
      double var40 = var20 * var14 + var5 * var10;
      double var42 = var24 * var14 - var3 * var10;
      double var44 = var26 * var14 + var12;
      double var46 = var28 * this.m00 + var34 * this.m01 + var40 * this.m02;
      double var48 = var30 * this.m00 + var36 * this.m01 + var42 * this.m02;
      double var50 = var32 * this.m00 + var38 * this.m01 + var44 * this.m02;
      double var52 = this.m03;
      double var54 = var28 * this.m10 + var34 * this.m11 + var40 * this.m12;
      double var56 = var30 * this.m10 + var36 * this.m11 + var42 * this.m12;
      double var58 = var32 * this.m10 + var38 * this.m11 + var44 * this.m12;
      double var60 = this.m13;
      double var62 = var28 * this.m20 + var34 * this.m21 + var40 * this.m22;
      double var64 = var30 * this.m20 + var36 * this.m21 + var42 * this.m22;
      double var66 = var32 * this.m20 + var38 * this.m21 + var44 * this.m22;
      double var68 = this.m23;
      double var70 = var28 * this.m30 + var34 * this.m31 + var40 * this.m32;
      double var72 = var30 * this.m30 + var36 * this.m31 + var42 * this.m32;
      double var74 = var32 * this.m30 + var38 * this.m31 + var44 * this.m32;
      double var76 = this.m33;
      var9.m00 = var46;
      var9.m01 = var48;
      var9.m02 = var50;
      var9.m03 = var52;
      var9.m10 = var54;
      var9.m11 = var56;
      var9.m12 = var58;
      var9.m13 = var60;
      var9.m20 = var62;
      var9.m21 = var64;
      var9.m22 = var66;
      var9.m23 = var68;
      var9.m30 = var70;
      var9.m31 = var72;
      var9.m32 = var74;
      var9.m33 = var76;
      var9.properties = (byte)(this.properties & -14);
      return var9;
   }

   public Matrix4d rotateLocal(double var1, double var3, double var5, double var7) {
      return this.rotateLocal(var1, var3, var5, var7, this);
   }

   public Matrix4d rotateAroundLocal(Quaterniondc var1, double var2, double var4, double var6, Matrix4d var8) {
      double var9 = var1.w() * var1.w();
      double var11 = var1.x() * var1.x();
      double var13 = var1.y() * var1.y();
      double var15 = var1.z() * var1.z();
      double var17 = var1.z() * var1.w();
      double var19 = var1.x() * var1.y();
      double var21 = var1.x() * var1.z();
      double var23 = var1.y() * var1.w();
      double var25 = var1.y() * var1.z();
      double var27 = var1.x() * var1.w();
      double var29 = var9 + var11 - var15 - var13;
      double var31 = var19 + var17 + var17 + var19;
      double var33 = var21 - var23 + var21 - var23;
      double var35 = -var17 + var19 - var17 + var19;
      double var37 = var13 - var15 + var9 - var11;
      double var39 = var25 + var25 + var27 + var27;
      double var41 = var23 + var21 + var21 + var23;
      double var43 = var25 + var25 - var27 - var27;
      double var45 = var15 - var13 - var11 + var9;
      double var47 = this.m00 - var2 * this.m03;
      double var49 = this.m01 - var4 * this.m03;
      double var51 = this.m02 - var6 * this.m03;
      double var53 = this.m10 - var2 * this.m13;
      double var55 = this.m11 - var4 * this.m13;
      double var57 = this.m12 - var6 * this.m13;
      double var59 = this.m20 - var2 * this.m23;
      double var61 = this.m21 - var4 * this.m23;
      double var63 = this.m22 - var6 * this.m23;
      double var65 = this.m30 - var2 * this.m33;
      double var67 = this.m31 - var4 * this.m33;
      double var69 = this.m32 - var6 * this.m33;
      var8.m00 = var29 * var47 + var35 * var49 + var41 * var51 + var2 * this.m03;
      var8.m01 = var31 * var47 + var37 * var49 + var43 * var51 + var4 * this.m03;
      var8.m02 = var33 * var47 + var39 * var49 + var45 * var51 + var6 * this.m03;
      var8.m03 = this.m03;
      var8.m10 = var29 * var53 + var35 * var55 + var41 * var57 + var2 * this.m13;
      var8.m11 = var31 * var53 + var37 * var55 + var43 * var57 + var4 * this.m13;
      var8.m12 = var33 * var53 + var39 * var55 + var45 * var57 + var6 * this.m13;
      var8.m13 = this.m13;
      var8.m20 = var29 * var59 + var35 * var61 + var41 * var63 + var2 * this.m23;
      var8.m21 = var31 * var59 + var37 * var61 + var43 * var63 + var4 * this.m23;
      var8.m22 = var33 * var59 + var39 * var61 + var45 * var63 + var6 * this.m23;
      var8.m23 = this.m23;
      var8.m30 = var29 * var65 + var35 * var67 + var41 * var69 + var2 * this.m33;
      var8.m31 = var31 * var65 + var37 * var67 + var43 * var69 + var4 * this.m33;
      var8.m32 = var33 * var65 + var39 * var67 + var45 * var69 + var6 * this.m33;
      var8.m33 = this.m33;
      var8.properties = (byte)(this.properties & -14);
      return var8;
   }

   public Matrix4d rotateAroundLocal(Quaterniondc var1, double var2, double var4, double var6) {
      return this.rotateAroundLocal(var1, var2, var4, var6, this);
   }

   public Matrix4d translate(Vector3dc var1) {
      return this.translate(var1.x(), var1.y(), var1.z());
   }

   public Matrix4d translate(Vector3dc var1, Matrix4d var2) {
      return this.translate(var1.x(), var1.y(), var1.z(), var2);
   }

   public Matrix4d translate(Vector3fc var1) {
      return this.translate((double)var1.x(), (double)var1.y(), (double)var1.z());
   }

   public Matrix4d translate(Vector3fc var1, Matrix4d var2) {
      return this.translate((double)var1.x(), (double)var1.y(), (double)var1.z(), var2);
   }

   public Matrix4d translate(double var1, double var3, double var5, Matrix4d var7) {
      return (this.properties & 4) != 0 ? var7.translation(var1, var3, var5) : this.translateGeneric(var1, var3, var5, var7);
   }

   private Matrix4d translateGeneric(double var1, double var3, double var5, Matrix4d var7) {
      var7.m00 = this.m00;
      var7.m01 = this.m01;
      var7.m02 = this.m02;
      var7.m03 = this.m03;
      var7.m10 = this.m10;
      var7.m11 = this.m11;
      var7.m12 = this.m12;
      var7.m13 = this.m13;
      var7.m20 = this.m20;
      var7.m21 = this.m21;
      var7.m22 = this.m22;
      var7.m23 = this.m23;
      var7.m30 = this.m00 * var1 + this.m10 * var3 + this.m20 * var5 + this.m30;
      var7.m31 = this.m01 * var1 + this.m11 * var3 + this.m21 * var5 + this.m31;
      var7.m32 = this.m02 * var1 + this.m12 * var3 + this.m22 * var5 + this.m32;
      var7.m33 = this.m03 * var1 + this.m13 * var3 + this.m23 * var5 + this.m33;
      var7.properties = (byte)(this.properties & -6);
      return var7;
   }

   public Matrix4d translate(double var1, double var3, double var5) {
      if ((this.properties & 4) != 0) {
         return this.translation(var1, var3, var5);
      } else {
         this.m30 += this.m00 * var1 + this.m10 * var3 + this.m20 * var5;
         this.m31 += this.m01 * var1 + this.m11 * var3 + this.m21 * var5;
         this.m32 += this.m02 * var1 + this.m12 * var3 + this.m22 * var5;
         this.m33 += this.m03 * var1 + this.m13 * var3 + this.m23 * var5;
         this.properties &= -6;
         return this;
      }
   }

   public Matrix4d translateLocal(Vector3fc var1) {
      return this.translateLocal((double)var1.x(), (double)var1.y(), (double)var1.z());
   }

   public Matrix4d translateLocal(Vector3fc var1, Matrix4d var2) {
      return this.translateLocal((double)var1.x(), (double)var1.y(), (double)var1.z(), var2);
   }

   public Matrix4d translateLocal(Vector3dc var1) {
      return this.translateLocal(var1.x(), var1.y(), var1.z());
   }

   public Matrix4d translateLocal(Vector3dc var1, Matrix4d var2) {
      return this.translateLocal(var1.x(), var1.y(), var1.z(), var2);
   }

   public Matrix4d translateLocal(double var1, double var3, double var5, Matrix4d var7) {
      double var8 = this.m00 + var1 * this.m03;
      double var10 = this.m01 + var3 * this.m03;
      double var12 = this.m02 + var5 * this.m03;
      double var14 = this.m03;
      double var16 = this.m10 + var1 * this.m13;
      double var18 = this.m11 + var3 * this.m13;
      double var20 = this.m12 + var5 * this.m13;
      double var22 = this.m13;
      double var24 = this.m20 + var1 * this.m23;
      double var26 = this.m21 + var3 * this.m23;
      double var28 = this.m22 + var5 * this.m23;
      double var30 = this.m23;
      double var32 = this.m30 + var1 * this.m33;
      double var34 = this.m31 + var3 * this.m33;
      double var36 = this.m32 + var5 * this.m33;
      double var38 = this.m33;
      var7.m00 = var8;
      var7.m01 = var10;
      var7.m02 = var12;
      var7.m03 = var14;
      var7.m10 = var16;
      var7.m11 = var18;
      var7.m12 = var20;
      var7.m13 = var22;
      var7.m20 = var24;
      var7.m21 = var26;
      var7.m22 = var28;
      var7.m23 = var30;
      var7.m30 = var32;
      var7.m31 = var34;
      var7.m32 = var36;
      var7.m33 = var38;
      var7.properties = (byte)(this.properties & -6);
      return var7;
   }

   public Matrix4d translateLocal(double var1, double var3, double var5) {
      return this.translateLocal(var1, var3, var5, this);
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeDouble(this.m00);
      var1.writeDouble(this.m01);
      var1.writeDouble(this.m02);
      var1.writeDouble(this.m03);
      var1.writeDouble(this.m10);
      var1.writeDouble(this.m11);
      var1.writeDouble(this.m12);
      var1.writeDouble(this.m13);
      var1.writeDouble(this.m20);
      var1.writeDouble(this.m21);
      var1.writeDouble(this.m22);
      var1.writeDouble(this.m23);
      var1.writeDouble(this.m30);
      var1.writeDouble(this.m31);
      var1.writeDouble(this.m32);
      var1.writeDouble(this.m33);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.m00 = var1.readDouble();
      this.m01 = var1.readDouble();
      this.m02 = var1.readDouble();
      this.m03 = var1.readDouble();
      this.m10 = var1.readDouble();
      this.m11 = var1.readDouble();
      this.m12 = var1.readDouble();
      this.m13 = var1.readDouble();
      this.m20 = var1.readDouble();
      this.m21 = var1.readDouble();
      this.m22 = var1.readDouble();
      this.m23 = var1.readDouble();
      this.m30 = var1.readDouble();
      this.m31 = var1.readDouble();
      this.m32 = var1.readDouble();
      this.m33 = var1.readDouble();
   }

   public Matrix4d rotateX(double var1, Matrix4d var3) {
      if ((this.properties & 4) != 0) {
         return var3.rotationX(var1);
      } else {
         double var4 = Math.sin(var1);
         double var6 = Math.cos(var1);
         double var12 = -var4;
         double var16 = this.m10 * var6 + this.m20 * var4;
         double var18 = this.m11 * var6 + this.m21 * var4;
         double var20 = this.m12 * var6 + this.m22 * var4;
         double var22 = this.m13 * var6 + this.m23 * var4;
         var3.m20 = this.m10 * var12 + this.m20 * var6;
         var3.m21 = this.m11 * var12 + this.m21 * var6;
         var3.m22 = this.m12 * var12 + this.m22 * var6;
         var3.m23 = this.m13 * var12 + this.m23 * var6;
         var3.m10 = var16;
         var3.m11 = var18;
         var3.m12 = var20;
         var3.m13 = var22;
         var3.m00 = this.m00;
         var3.m01 = this.m01;
         var3.m02 = this.m02;
         var3.m03 = this.m03;
         var3.m30 = this.m30;
         var3.m31 = this.m31;
         var3.m32 = this.m32;
         var3.m33 = this.m33;
         var3.properties = (byte)(this.properties & -14);
         return var3;
      }
   }

   public Matrix4d rotateX(double var1) {
      return this.rotateX(var1, this);
   }

   public Matrix4d rotateY(double var1, Matrix4d var3) {
      if ((this.properties & 4) != 0) {
         return var3.rotationY(var1);
      } else {
         double var4 = Math.sin(var1);
         double var6 = Math.cos(var1);
         double var10 = -var4;
         double var16 = this.m00 * var6 + this.m20 * var10;
         double var18 = this.m01 * var6 + this.m21 * var10;
         double var20 = this.m02 * var6 + this.m22 * var10;
         double var22 = this.m03 * var6 + this.m23 * var10;
         var3.m20 = this.m00 * var4 + this.m20 * var6;
         var3.m21 = this.m01 * var4 + this.m21 * var6;
         var3.m22 = this.m02 * var4 + this.m22 * var6;
         var3.m23 = this.m03 * var4 + this.m23 * var6;
         var3.m00 = var16;
         var3.m01 = var18;
         var3.m02 = var20;
         var3.m03 = var22;
         var3.m10 = this.m10;
         var3.m11 = this.m11;
         var3.m12 = this.m12;
         var3.m13 = this.m13;
         var3.m30 = this.m30;
         var3.m31 = this.m31;
         var3.m32 = this.m32;
         var3.m33 = this.m33;
         var3.properties = (byte)(this.properties & -14);
         return var3;
      }
   }

   public Matrix4d rotateY(double var1) {
      return this.rotateY(var1, this);
   }

   public Matrix4d rotateZ(double var1, Matrix4d var3) {
      if ((this.properties & 4) != 0) {
         return var3.rotationZ(var1);
      } else {
         double var4 = Math.sin(var1);
         double var6 = Math.cos(var1);
         double var12 = -var4;
         double var16 = this.m00 * var6 + this.m10 * var4;
         double var18 = this.m01 * var6 + this.m11 * var4;
         double var20 = this.m02 * var6 + this.m12 * var4;
         double var22 = this.m03 * var6 + this.m13 * var4;
         var3.m10 = this.m00 * var12 + this.m10 * var6;
         var3.m11 = this.m01 * var12 + this.m11 * var6;
         var3.m12 = this.m02 * var12 + this.m12 * var6;
         var3.m13 = this.m03 * var12 + this.m13 * var6;
         var3.m00 = var16;
         var3.m01 = var18;
         var3.m02 = var20;
         var3.m03 = var22;
         var3.m20 = this.m20;
         var3.m21 = this.m21;
         var3.m22 = this.m22;
         var3.m23 = this.m23;
         var3.m30 = this.m30;
         var3.m31 = this.m31;
         var3.m32 = this.m32;
         var3.m33 = this.m33;
         var3.properties = (byte)(this.properties & -14);
         return var3;
      }
   }

   public Matrix4d rotateZ(double var1) {
      return this.rotateZ(var1, this);
   }

   public Matrix4d rotateXYZ(Vector3d var1) {
      return this.rotateXYZ(var1.x, var1.y, var1.z);
   }

   public Matrix4d rotateXYZ(double var1, double var3, double var5) {
      return this.rotateXYZ(var1, var3, var5, this);
   }

   public Matrix4d rotateXYZ(double var1, double var3, double var5, Matrix4d var7) {
      if ((this.properties & 4) != 0) {
         return var7.rotationXYZ(var1, var3, var5);
      } else {
         double var8 = Math.cos(var1);
         double var10 = Math.sin(var1);
         double var12 = Math.cos(var3);
         double var14 = Math.sin(var3);
         double var16 = Math.cos(var5);
         double var18 = Math.sin(var5);
         double var20 = -var10;
         double var22 = -var14;
         double var24 = -var18;
         double var26 = this.m10 * var8 + this.m20 * var10;
         double var28 = this.m11 * var8 + this.m21 * var10;
         double var30 = this.m12 * var8 + this.m22 * var10;
         double var32 = this.m13 * var8 + this.m23 * var10;
         double var34 = this.m10 * var20 + this.m20 * var8;
         double var36 = this.m11 * var20 + this.m21 * var8;
         double var38 = this.m12 * var20 + this.m22 * var8;
         double var40 = this.m13 * var20 + this.m23 * var8;
         double var42 = this.m00 * var12 + var34 * var22;
         double var44 = this.m01 * var12 + var36 * var22;
         double var46 = this.m02 * var12 + var38 * var22;
         double var48 = this.m03 * var12 + var40 * var22;
         var7.m20 = this.m00 * var14 + var34 * var12;
         var7.m21 = this.m01 * var14 + var36 * var12;
         var7.m22 = this.m02 * var14 + var38 * var12;
         var7.m23 = this.m03 * var14 + var40 * var12;
         var7.m00 = var42 * var16 + var26 * var18;
         var7.m01 = var44 * var16 + var28 * var18;
         var7.m02 = var46 * var16 + var30 * var18;
         var7.m03 = var48 * var16 + var32 * var18;
         var7.m10 = var42 * var24 + var26 * var16;
         var7.m11 = var44 * var24 + var28 * var16;
         var7.m12 = var46 * var24 + var30 * var16;
         var7.m13 = var48 * var24 + var32 * var16;
         var7.m30 = this.m30;
         var7.m31 = this.m31;
         var7.m32 = this.m32;
         var7.m33 = this.m33;
         var7.properties = (byte)(this.properties & -14);
         return var7;
      }
   }

   public Matrix4d rotateAffineXYZ(double var1, double var3, double var5) {
      return this.rotateAffineXYZ(var1, var3, var5, this);
   }

   public Matrix4d rotateAffineXYZ(double var1, double var3, double var5, Matrix4d var7) {
      double var8 = Math.cos(var1);
      double var10 = Math.sin(var1);
      double var12 = Math.cos(var3);
      double var14 = Math.sin(var3);
      double var16 = Math.cos(var5);
      double var18 = Math.sin(var5);
      double var20 = -var10;
      double var22 = -var14;
      double var24 = -var18;
      double var26 = this.m10 * var8 + this.m20 * var10;
      double var28 = this.m11 * var8 + this.m21 * var10;
      double var30 = this.m12 * var8 + this.m22 * var10;
      double var32 = this.m10 * var20 + this.m20 * var8;
      double var34 = this.m11 * var20 + this.m21 * var8;
      double var36 = this.m12 * var20 + this.m22 * var8;
      double var38 = this.m00 * var12 + var32 * var22;
      double var40 = this.m01 * var12 + var34 * var22;
      double var42 = this.m02 * var12 + var36 * var22;
      var7.m20 = this.m00 * var14 + var32 * var12;
      var7.m21 = this.m01 * var14 + var34 * var12;
      var7.m22 = this.m02 * var14 + var36 * var12;
      var7.m23 = 0.0D;
      var7.m00 = var38 * var16 + var26 * var18;
      var7.m01 = var40 * var16 + var28 * var18;
      var7.m02 = var42 * var16 + var30 * var18;
      var7.m03 = 0.0D;
      var7.m10 = var38 * var24 + var26 * var16;
      var7.m11 = var40 * var24 + var28 * var16;
      var7.m12 = var42 * var24 + var30 * var16;
      var7.m13 = 0.0D;
      var7.m30 = this.m30;
      var7.m31 = this.m31;
      var7.m32 = this.m32;
      var7.m33 = this.m33;
      var7.properties = (byte)(this.properties & -14);
      return var7;
   }

   public Matrix4d rotateZYX(Vector3d var1) {
      return this.rotateZYX(var1.z, var1.y, var1.x);
   }

   public Matrix4d rotateZYX(double var1, double var3, double var5) {
      return this.rotateZYX(var1, var3, var5, this);
   }

   public Matrix4d rotateZYX(double var1, double var3, double var5, Matrix4d var7) {
      if ((this.properties & 4) != 0) {
         return var7.rotationZYX(var1, var3, var5);
      } else {
         double var8 = Math.cos(var1);
         double var10 = Math.sin(var1);
         double var12 = Math.cos(var3);
         double var14 = Math.sin(var3);
         double var16 = Math.cos(var5);
         double var18 = Math.sin(var5);
         double var20 = -var10;
         double var22 = -var14;
         double var24 = -var18;
         double var26 = this.m00 * var8 + this.m10 * var10;
         double var28 = this.m01 * var8 + this.m11 * var10;
         double var30 = this.m02 * var8 + this.m12 * var10;
         double var32 = this.m03 * var8 + this.m13 * var10;
         double var34 = this.m00 * var20 + this.m10 * var8;
         double var36 = this.m01 * var20 + this.m11 * var8;
         double var38 = this.m02 * var20 + this.m12 * var8;
         double var40 = this.m03 * var20 + this.m13 * var8;
         double var42 = var26 * var14 + this.m20 * var12;
         double var44 = var28 * var14 + this.m21 * var12;
         double var46 = var30 * var14 + this.m22 * var12;
         double var48 = var32 * var14 + this.m23 * var12;
         var7.m00 = var26 * var12 + this.m20 * var22;
         var7.m01 = var28 * var12 + this.m21 * var22;
         var7.m02 = var30 * var12 + this.m22 * var22;
         var7.m03 = var32 * var12 + this.m23 * var22;
         var7.m10 = var34 * var16 + var42 * var18;
         var7.m11 = var36 * var16 + var44 * var18;
         var7.m12 = var38 * var16 + var46 * var18;
         var7.m13 = var40 * var16 + var48 * var18;
         var7.m20 = var34 * var24 + var42 * var16;
         var7.m21 = var36 * var24 + var44 * var16;
         var7.m22 = var38 * var24 + var46 * var16;
         var7.m23 = var40 * var24 + var48 * var16;
         var7.m30 = this.m30;
         var7.m31 = this.m31;
         var7.m32 = this.m32;
         var7.m33 = this.m33;
         var7.properties = (byte)(this.properties & -14);
         return var7;
      }
   }

   public Matrix4d rotateAffineZYX(double var1, double var3, double var5) {
      return this.rotateAffineZYX(var1, var3, var5, this);
   }

   public Matrix4d rotateAffineZYX(double var1, double var3, double var5, Matrix4d var7) {
      double var8 = Math.cos(var1);
      double var10 = Math.sin(var1);
      double var12 = Math.cos(var3);
      double var14 = Math.sin(var3);
      double var16 = Math.cos(var5);
      double var18 = Math.sin(var5);
      double var20 = -var10;
      double var22 = -var14;
      double var24 = -var18;
      double var26 = this.m00 * var8 + this.m10 * var10;
      double var28 = this.m01 * var8 + this.m11 * var10;
      double var30 = this.m02 * var8 + this.m12 * var10;
      double var32 = this.m00 * var20 + this.m10 * var8;
      double var34 = this.m01 * var20 + this.m11 * var8;
      double var36 = this.m02 * var20 + this.m12 * var8;
      double var38 = var26 * var14 + this.m20 * var12;
      double var40 = var28 * var14 + this.m21 * var12;
      double var42 = var30 * var14 + this.m22 * var12;
      var7.m00 = var26 * var12 + this.m20 * var22;
      var7.m01 = var28 * var12 + this.m21 * var22;
      var7.m02 = var30 * var12 + this.m22 * var22;
      var7.m03 = 0.0D;
      var7.m10 = var32 * var16 + var38 * var18;
      var7.m11 = var34 * var16 + var40 * var18;
      var7.m12 = var36 * var16 + var42 * var18;
      var7.m13 = 0.0D;
      var7.m20 = var32 * var24 + var38 * var16;
      var7.m21 = var34 * var24 + var40 * var16;
      var7.m22 = var36 * var24 + var42 * var16;
      var7.m23 = 0.0D;
      var7.m30 = this.m30;
      var7.m31 = this.m31;
      var7.m32 = this.m32;
      var7.m33 = this.m33;
      var7.properties = (byte)(this.properties & -14);
      return var7;
   }

   public Matrix4d rotateYXZ(Vector3d var1) {
      return this.rotateYXZ(var1.y, var1.x, var1.z);
   }

   public Matrix4d rotateYXZ(double var1, double var3, double var5) {
      return this.rotateYXZ(var1, var3, var5, this);
   }

   public Matrix4d rotateYXZ(double var1, double var3, double var5, Matrix4d var7) {
      if ((this.properties & 4) != 0) {
         return var7.rotationYXZ(var1, var3, var5);
      } else {
         double var8 = Math.cos(var1);
         double var10 = Math.sin(var1);
         double var12 = Math.cos(var3);
         double var14 = Math.sin(var3);
         double var16 = Math.cos(var5);
         double var18 = Math.sin(var5);
         double var20 = -var10;
         double var22 = -var14;
         double var24 = -var18;
         double var26 = this.m00 * var10 + this.m20 * var8;
         double var28 = this.m01 * var10 + this.m21 * var8;
         double var30 = this.m02 * var10 + this.m22 * var8;
         double var32 = this.m03 * var10 + this.m23 * var8;
         double var34 = this.m00 * var8 + this.m20 * var20;
         double var36 = this.m01 * var8 + this.m21 * var20;
         double var38 = this.m02 * var8 + this.m22 * var20;
         double var40 = this.m03 * var8 + this.m23 * var20;
         double var42 = this.m10 * var12 + var26 * var14;
         double var44 = this.m11 * var12 + var28 * var14;
         double var46 = this.m12 * var12 + var30 * var14;
         double var48 = this.m13 * var12 + var32 * var14;
         var7.m20 = this.m10 * var22 + var26 * var12;
         var7.m21 = this.m11 * var22 + var28 * var12;
         var7.m22 = this.m12 * var22 + var30 * var12;
         var7.m23 = this.m13 * var22 + var32 * var12;
         var7.m00 = var34 * var16 + var42 * var18;
         var7.m01 = var36 * var16 + var44 * var18;
         var7.m02 = var38 * var16 + var46 * var18;
         var7.m03 = var40 * var16 + var48 * var18;
         var7.m10 = var34 * var24 + var42 * var16;
         var7.m11 = var36 * var24 + var44 * var16;
         var7.m12 = var38 * var24 + var46 * var16;
         var7.m13 = var40 * var24 + var48 * var16;
         var7.m30 = this.m30;
         var7.m31 = this.m31;
         var7.m32 = this.m32;
         var7.m33 = this.m33;
         var7.properties = (byte)(this.properties & -14);
         return var7;
      }
   }

   public Matrix4d rotateAffineYXZ(double var1, double var3, double var5) {
      return this.rotateAffineYXZ(var1, var3, var5, this);
   }

   public Matrix4d rotateAffineYXZ(double var1, double var3, double var5, Matrix4d var7) {
      double var8 = Math.cos(var1);
      double var10 = Math.sin(var1);
      double var12 = Math.cos(var3);
      double var14 = Math.sin(var3);
      double var16 = Math.cos(var5);
      double var18 = Math.sin(var5);
      double var20 = -var10;
      double var22 = -var14;
      double var24 = -var18;
      double var26 = this.m00 * var10 + this.m20 * var8;
      double var28 = this.m01 * var10 + this.m21 * var8;
      double var30 = this.m02 * var10 + this.m22 * var8;
      double var32 = this.m00 * var8 + this.m20 * var20;
      double var34 = this.m01 * var8 + this.m21 * var20;
      double var36 = this.m02 * var8 + this.m22 * var20;
      double var38 = this.m10 * var12 + var26 * var14;
      double var40 = this.m11 * var12 + var28 * var14;
      double var42 = this.m12 * var12 + var30 * var14;
      var7.m20 = this.m10 * var22 + var26 * var12;
      var7.m21 = this.m11 * var22 + var28 * var12;
      var7.m22 = this.m12 * var22 + var30 * var12;
      var7.m23 = 0.0D;
      var7.m00 = var32 * var16 + var38 * var18;
      var7.m01 = var34 * var16 + var40 * var18;
      var7.m02 = var36 * var16 + var42 * var18;
      var7.m03 = 0.0D;
      var7.m10 = var32 * var24 + var38 * var16;
      var7.m11 = var34 * var24 + var40 * var16;
      var7.m12 = var36 * var24 + var42 * var16;
      var7.m13 = 0.0D;
      var7.m30 = this.m30;
      var7.m31 = this.m31;
      var7.m32 = this.m32;
      var7.m33 = this.m33;
      var7.properties = (byte)(this.properties & -14);
      return var7;
   }

   public Matrix4d rotation(AxisAngle4f var1) {
      return this.rotation((double)var1.angle, (double)var1.x, (double)var1.y, (double)var1.z);
   }

   public Matrix4d rotation(AxisAngle4d var1) {
      return this.rotation(var1.angle, var1.x, var1.y, var1.z);
   }

   public Matrix4d rotation(Quaterniondc var1) {
      double var2 = var1.w() * var1.w();
      double var4 = var1.x() * var1.x();
      double var6 = var1.y() * var1.y();
      double var8 = var1.z() * var1.z();
      double var10 = var1.z() * var1.w();
      double var12 = var1.x() * var1.y();
      double var14 = var1.x() * var1.z();
      double var16 = var1.y() * var1.w();
      double var18 = var1.y() * var1.z();
      double var20 = var1.x() * var1.w();
      this.m00 = var2 + var4 - var8 - var6;
      this.m01 = var12 + var10 + var10 + var12;
      this.m02 = var14 - var16 + var14 - var16;
      this.m03 = 0.0D;
      this.m10 = -var10 + var12 - var10 + var12;
      this.m11 = var6 - var8 + var2 - var4;
      this.m12 = var18 + var18 + var20 + var20;
      this.m13 = 0.0D;
      this.m20 = var16 + var14 + var14 + var16;
      this.m21 = var18 + var18 - var20 - var20;
      this.m22 = var8 - var6 - var4 + var2;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d rotation(Quaternionfc var1) {
      double var2 = (double)(var1.w() * var1.w());
      double var4 = (double)(var1.x() * var1.x());
      double var6 = (double)(var1.y() * var1.y());
      double var8 = (double)(var1.z() * var1.z());
      double var10 = (double)(var1.z() * var1.w());
      double var12 = (double)(var1.x() * var1.y());
      double var14 = (double)(var1.x() * var1.z());
      double var16 = (double)(var1.y() * var1.w());
      double var18 = (double)(var1.y() * var1.z());
      double var20 = (double)(var1.x() * var1.w());
      this.m00 = var2 + var4 - var8 - var6;
      this.m01 = var12 + var10 + var10 + var12;
      this.m02 = var14 - var16 + var14 - var16;
      this.m03 = 0.0D;
      this.m10 = -var10 + var12 - var10 + var12;
      this.m11 = var6 - var8 + var2 - var4;
      this.m12 = var18 + var18 + var20 + var20;
      this.m13 = 0.0D;
      this.m20 = var16 + var14 + var14 + var16;
      this.m21 = var18 + var18 - var20 - var20;
      this.m22 = var8 - var6 - var4 + var2;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d translationRotateScale(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19) {
      double var21 = var7 + var7;
      double var23 = var9 + var9;
      double var25 = var11 + var11;
      double var27 = var21 * var7;
      double var29 = var23 * var9;
      double var31 = var25 * var11;
      double var33 = var21 * var9;
      double var35 = var21 * var11;
      double var37 = var21 * var13;
      double var39 = var23 * var11;
      double var41 = var23 * var13;
      double var43 = var25 * var13;
      this.m00 = var15 - (var29 + var31) * var15;
      this.m01 = (var33 + var43) * var15;
      this.m02 = (var35 - var41) * var15;
      this.m03 = 0.0D;
      this.m10 = (var33 - var43) * var17;
      this.m11 = var17 - (var31 + var27) * var17;
      this.m12 = (var39 + var37) * var17;
      this.m13 = 0.0D;
      this.m20 = (var35 + var41) * var19;
      this.m21 = (var39 - var37) * var19;
      this.m22 = var19 - (var29 + var27) * var19;
      this.m23 = 0.0D;
      this.m30 = var1;
      this.m31 = var3;
      this.m32 = var5;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d translationRotateScale(Vector3fc var1, Quaternionfc var2, Vector3fc var3) {
      return this.translationRotateScale((double)var1.x(), (double)var1.y(), (double)var1.z(), (double)var2.x(), (double)var2.y(), (double)var2.z(), (double)var2.w(), (double)var3.x(), (double)var3.y(), (double)var3.z());
   }

   public Matrix4d translationRotateScale(Vector3dc var1, Quaterniondc var2, Vector3dc var3) {
      return this.translationRotateScale(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var2.w(), var3.x(), var3.y(), var3.z());
   }

   public Matrix4d translationRotateScale(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15) {
      return this.translationRotateScale(var1, var3, var5, var7, var9, var11, var13, var15, var15, var15);
   }

   public Matrix4d translationRotateScale(Vector3dc var1, Quaterniondc var2, double var3) {
      return this.translationRotateScale(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var2.w(), var3, var3, var3);
   }

   public Matrix4d translationRotateScale(Vector3fc var1, Quaternionfc var2, double var3) {
      return this.translationRotateScale((double)var1.x(), (double)var1.y(), (double)var1.z(), (double)var2.x(), (double)var2.y(), (double)var2.z(), (double)var2.w(), var3, var3, var3);
   }

   public Matrix4d translationRotateScaleInvert(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19) {
      double var21 = -var7;
      double var23 = -var9;
      double var25 = -var11;
      double var27 = var21 + var21;
      double var29 = var23 + var23;
      double var31 = var25 + var25;
      double var33 = var27 * var21;
      double var35 = var29 * var23;
      double var37 = var31 * var25;
      double var39 = var27 * var23;
      double var41 = var27 * var25;
      double var43 = var27 * var13;
      double var45 = var29 * var25;
      double var47 = var29 * var13;
      double var49 = var31 * var13;
      double var51 = 1.0D / var15;
      double var53 = 1.0D / var17;
      double var55 = 1.0D / var19;
      this.m00 = var51 * (1.0D - var35 - var37);
      this.m01 = var53 * (var39 + var49);
      this.m02 = var55 * (var41 - var47);
      this.m03 = 0.0D;
      this.m10 = var51 * (var39 - var49);
      this.m11 = var53 * (1.0D - var37 - var33);
      this.m12 = var55 * (var45 + var43);
      this.m13 = 0.0D;
      this.m20 = var51 * (var41 + var47);
      this.m21 = var53 * (var45 - var43);
      this.m22 = var55 * (1.0D - var35 - var33);
      this.m23 = 0.0D;
      this.m30 = -this.m00 * var1 - this.m10 * var3 - this.m20 * var5;
      this.m31 = -this.m01 * var1 - this.m11 * var3 - this.m21 * var5;
      this.m32 = -this.m02 * var1 - this.m12 * var3 - this.m22 * var5;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d translationRotateScaleInvert(Vector3dc var1, Quaterniondc var2, Vector3dc var3) {
      return this.translationRotateScaleInvert(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var2.w(), var3.x(), var3.y(), var3.z());
   }

   public Matrix4d translationRotateScaleInvert(Vector3fc var1, Quaternionfc var2, Vector3fc var3) {
      return this.translationRotateScaleInvert((double)var1.x(), (double)var1.y(), (double)var1.z(), (double)var2.x(), (double)var2.y(), (double)var2.z(), (double)var2.w(), (double)var3.x(), (double)var3.y(), (double)var3.z());
   }

   public Matrix4d translationRotateScaleInvert(Vector3dc var1, Quaterniondc var2, double var3) {
      return this.translationRotateScaleInvert(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var2.w(), var3, var3, var3);
   }

   public Matrix4d translationRotateScaleInvert(Vector3fc var1, Quaternionfc var2, double var3) {
      return this.translationRotateScaleInvert((double)var1.x(), (double)var1.y(), (double)var1.z(), (double)var2.x(), (double)var2.y(), (double)var2.z(), (double)var2.w(), var3, var3, var3);
   }

   public Matrix4d translationRotateScaleMulAffine(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, Matrix4d var21) {
      double var22 = var13 * var13;
      double var24 = var7 * var7;
      double var26 = var9 * var9;
      double var28 = var11 * var11;
      double var30 = var11 * var13;
      double var32 = var7 * var9;
      double var34 = var7 * var11;
      double var36 = var9 * var13;
      double var38 = var9 * var11;
      double var40 = var7 * var13;
      double var42 = var22 + var24 - var28 - var26;
      double var44 = var32 + var30 + var30 + var32;
      double var46 = var34 - var36 + var34 - var36;
      double var48 = -var30 + var32 - var30 + var32;
      double var50 = var26 - var28 + var22 - var24;
      double var52 = var38 + var38 + var40 + var40;
      double var54 = var36 + var34 + var34 + var36;
      double var56 = var38 + var38 - var40 - var40;
      double var58 = var28 - var26 - var24 + var22;
      double var60 = var42 * var21.m00 + var48 * var21.m01 + var54 * var21.m02;
      double var62 = var44 * var21.m00 + var50 * var21.m01 + var56 * var21.m02;
      this.m02 = var46 * var21.m00 + var52 * var21.m01 + var58 * var21.m02;
      this.m00 = var60;
      this.m01 = var62;
      this.m03 = 0.0D;
      double var64 = var42 * var21.m10 + var48 * var21.m11 + var54 * var21.m12;
      double var66 = var44 * var21.m10 + var50 * var21.m11 + var56 * var21.m12;
      this.m12 = var46 * var21.m10 + var52 * var21.m11 + var58 * var21.m12;
      this.m10 = var64;
      this.m11 = var66;
      this.m13 = 0.0D;
      double var68 = var42 * var21.m20 + var48 * var21.m21 + var54 * var21.m22;
      double var70 = var44 * var21.m20 + var50 * var21.m21 + var56 * var21.m22;
      this.m22 = var46 * var21.m20 + var52 * var21.m21 + var58 * var21.m22;
      this.m20 = var68;
      this.m21 = var70;
      this.m23 = 0.0D;
      double var72 = var42 * var21.m30 + var48 * var21.m31 + var54 * var21.m32 + var1;
      double var74 = var44 * var21.m30 + var50 * var21.m31 + var56 * var21.m32 + var3;
      this.m32 = var46 * var21.m30 + var52 * var21.m31 + var58 * var21.m32 + var5;
      this.m30 = var72;
      this.m31 = var74;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d translationRotateScaleMulAffine(Vector3fc var1, Quaterniondc var2, Vector3fc var3, Matrix4d var4) {
      return this.translationRotateScaleMulAffine((double)var1.x(), (double)var1.y(), (double)var1.z(), var2.x(), var2.y(), var2.z(), var2.w(), (double)var3.x(), (double)var3.y(), (double)var3.z(), var4);
   }

   public Matrix4d translationRotate(double var1, double var3, double var5, double var7, double var9, double var11, double var13) {
      double var15 = var13 * var13;
      double var17 = var7 * var7;
      double var19 = var9 * var9;
      double var21 = var11 * var11;
      double var23 = var11 * var13;
      double var25 = var7 * var9;
      double var27 = var7 * var11;
      double var29 = var9 * var13;
      double var31 = var9 * var11;
      double var33 = var7 * var13;
      this.m00 = var15 + var17 - var21 - var19;
      this.m01 = var25 + var23 + var23 + var25;
      this.m02 = var27 - var29 + var27 - var29;
      this.m10 = -var23 + var25 - var23 + var25;
      this.m11 = var19 - var21 + var15 - var17;
      this.m12 = var31 + var31 + var33 + var33;
      this.m20 = var29 + var27 + var27 + var29;
      this.m21 = var31 + var31 - var33 - var33;
      this.m22 = var21 - var19 - var17 + var15;
      this.m30 = var1;
      this.m31 = var3;
      this.m32 = var5;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d translationRotate(double var1, double var3, double var5, Quaterniondc var7) {
      return this.translationRotate(var1, var3, var5, var7.x(), var7.y(), var7.z(), var7.w());
   }

   public Matrix4d rotate(Quaterniondc var1, Matrix4d var2) {
      if ((this.properties & 4) != 0) {
         return var2.rotation(var1);
      } else if ((this.properties & 8) != 0) {
         return this.rotateTranslation(var1, var2);
      } else {
         return (this.properties & 2) != 0 ? this.rotateAffine(var1, var2) : this.rotateGeneric(var1, var2);
      }
   }

   private Matrix4d rotateGeneric(Quaterniondc var1, Matrix4d var2) {
      double var3 = var1.w() * var1.w();
      double var5 = var1.x() * var1.x();
      double var7 = var1.y() * var1.y();
      double var9 = var1.z() * var1.z();
      double var11 = var1.z() * var1.w();
      double var13 = var1.x() * var1.y();
      double var15 = var1.x() * var1.z();
      double var17 = var1.y() * var1.w();
      double var19 = var1.y() * var1.z();
      double var21 = var1.x() * var1.w();
      double var23 = var3 + var5 - var9 - var7;
      double var25 = var13 + var11 + var11 + var13;
      double var27 = var15 - var17 + var15 - var17;
      double var29 = -var11 + var13 - var11 + var13;
      double var31 = var7 - var9 + var3 - var5;
      double var33 = var19 + var19 + var21 + var21;
      double var35 = var17 + var15 + var15 + var17;
      double var37 = var19 + var19 - var21 - var21;
      double var39 = var9 - var7 - var5 + var3;
      double var41 = this.m00 * var23 + this.m10 * var25 + this.m20 * var27;
      double var43 = this.m01 * var23 + this.m11 * var25 + this.m21 * var27;
      double var45 = this.m02 * var23 + this.m12 * var25 + this.m22 * var27;
      double var47 = this.m03 * var23 + this.m13 * var25 + this.m23 * var27;
      double var49 = this.m00 * var29 + this.m10 * var31 + this.m20 * var33;
      double var51 = this.m01 * var29 + this.m11 * var31 + this.m21 * var33;
      double var53 = this.m02 * var29 + this.m12 * var31 + this.m22 * var33;
      double var55 = this.m03 * var29 + this.m13 * var31 + this.m23 * var33;
      var2.m20 = this.m00 * var35 + this.m10 * var37 + this.m20 * var39;
      var2.m21 = this.m01 * var35 + this.m11 * var37 + this.m21 * var39;
      var2.m22 = this.m02 * var35 + this.m12 * var37 + this.m22 * var39;
      var2.m23 = this.m03 * var35 + this.m13 * var37 + this.m23 * var39;
      var2.m00 = var41;
      var2.m01 = var43;
      var2.m02 = var45;
      var2.m03 = var47;
      var2.m10 = var49;
      var2.m11 = var51;
      var2.m12 = var53;
      var2.m13 = var55;
      var2.m30 = this.m30;
      var2.m31 = this.m31;
      var2.m32 = this.m32;
      var2.m33 = this.m33;
      var2.properties = (byte)(this.properties & -14);
      return var2;
   }

   public Matrix4d rotate(Quaternionfc var1, Matrix4d var2) {
      if ((this.properties & 4) != 0) {
         return var2.rotation(var1);
      } else if ((this.properties & 8) != 0) {
         return this.rotateTranslation(var1, var2);
      } else {
         return (this.properties & 2) != 0 ? this.rotateAffine(var1, var2) : this.rotateGeneric(var1, var2);
      }
   }

   private Matrix4d rotateGeneric(Quaternionfc var1, Matrix4d var2) {
      double var3 = (double)(var1.w() * var1.w());
      double var5 = (double)(var1.x() * var1.x());
      double var7 = (double)(var1.y() * var1.y());
      double var9 = (double)(var1.z() * var1.z());
      double var11 = (double)(var1.z() * var1.w());
      double var13 = (double)(var1.x() * var1.y());
      double var15 = (double)(var1.x() * var1.z());
      double var17 = (double)(var1.y() * var1.w());
      double var19 = (double)(var1.y() * var1.z());
      double var21 = (double)(var1.x() * var1.w());
      double var23 = var3 + var5 - var9 - var7;
      double var25 = var13 + var11 + var11 + var13;
      double var27 = var15 - var17 + var15 - var17;
      double var29 = -var11 + var13 - var11 + var13;
      double var31 = var7 - var9 + var3 - var5;
      double var33 = var19 + var19 + var21 + var21;
      double var35 = var17 + var15 + var15 + var17;
      double var37 = var19 + var19 - var21 - var21;
      double var39 = var9 - var7 - var5 + var3;
      double var41 = this.m00 * var23 + this.m10 * var25 + this.m20 * var27;
      double var43 = this.m01 * var23 + this.m11 * var25 + this.m21 * var27;
      double var45 = this.m02 * var23 + this.m12 * var25 + this.m22 * var27;
      double var47 = this.m03 * var23 + this.m13 * var25 + this.m23 * var27;
      double var49 = this.m00 * var29 + this.m10 * var31 + this.m20 * var33;
      double var51 = this.m01 * var29 + this.m11 * var31 + this.m21 * var33;
      double var53 = this.m02 * var29 + this.m12 * var31 + this.m22 * var33;
      double var55 = this.m03 * var29 + this.m13 * var31 + this.m23 * var33;
      var2.m20 = this.m00 * var35 + this.m10 * var37 + this.m20 * var39;
      var2.m21 = this.m01 * var35 + this.m11 * var37 + this.m21 * var39;
      var2.m22 = this.m02 * var35 + this.m12 * var37 + this.m22 * var39;
      var2.m23 = this.m03 * var35 + this.m13 * var37 + this.m23 * var39;
      var2.m00 = var41;
      var2.m01 = var43;
      var2.m02 = var45;
      var2.m03 = var47;
      var2.m10 = var49;
      var2.m11 = var51;
      var2.m12 = var53;
      var2.m13 = var55;
      var2.m30 = this.m30;
      var2.m31 = this.m31;
      var2.m32 = this.m32;
      var2.m33 = this.m33;
      var2.properties = (byte)(this.properties & -14);
      return var2;
   }

   public Matrix4d rotate(Quaterniondc var1) {
      return this.rotate(var1, this);
   }

   public Matrix4d rotate(Quaternionfc var1) {
      return this.rotate(var1, this);
   }

   public Matrix4d rotateAffine(Quaterniondc var1, Matrix4d var2) {
      double var3 = var1.w() * var1.w();
      double var5 = var1.x() * var1.x();
      double var7 = var1.y() * var1.y();
      double var9 = var1.z() * var1.z();
      double var11 = var1.z() * var1.w();
      double var13 = var1.x() * var1.y();
      double var15 = var1.x() * var1.z();
      double var17 = var1.y() * var1.w();
      double var19 = var1.y() * var1.z();
      double var21 = var1.x() * var1.w();
      double var23 = var3 + var5 - var9 - var7;
      double var25 = var13 + var11 + var11 + var13;
      double var27 = var15 - var17 + var15 - var17;
      double var29 = -var11 + var13 - var11 + var13;
      double var31 = var7 - var9 + var3 - var5;
      double var33 = var19 + var19 + var21 + var21;
      double var35 = var17 + var15 + var15 + var17;
      double var37 = var19 + var19 - var21 - var21;
      double var39 = var9 - var7 - var5 + var3;
      double var41 = this.m00 * var23 + this.m10 * var25 + this.m20 * var27;
      double var43 = this.m01 * var23 + this.m11 * var25 + this.m21 * var27;
      double var45 = this.m02 * var23 + this.m12 * var25 + this.m22 * var27;
      double var47 = this.m00 * var29 + this.m10 * var31 + this.m20 * var33;
      double var49 = this.m01 * var29 + this.m11 * var31 + this.m21 * var33;
      double var51 = this.m02 * var29 + this.m12 * var31 + this.m22 * var33;
      var2.m20 = this.m00 * var35 + this.m10 * var37 + this.m20 * var39;
      var2.m21 = this.m01 * var35 + this.m11 * var37 + this.m21 * var39;
      var2.m22 = this.m02 * var35 + this.m12 * var37 + this.m22 * var39;
      var2.m23 = 0.0D;
      var2.m00 = var41;
      var2.m01 = var43;
      var2.m02 = var45;
      var2.m03 = 0.0D;
      var2.m10 = var47;
      var2.m11 = var49;
      var2.m12 = var51;
      var2.m13 = 0.0D;
      var2.m30 = this.m30;
      var2.m31 = this.m31;
      var2.m32 = this.m32;
      var2.m33 = this.m33;
      var2.properties = (byte)(this.properties & -14);
      return var2;
   }

   public Matrix4d rotateAffine(Quaterniondc var1) {
      return this.rotateAffine(var1, this);
   }

   public Matrix4d rotateTranslation(Quaterniondc var1, Matrix4d var2) {
      double var3 = var1.w() * var1.w();
      double var5 = var1.x() * var1.x();
      double var7 = var1.y() * var1.y();
      double var9 = var1.z() * var1.z();
      double var11 = var1.z() * var1.w();
      double var13 = var1.x() * var1.y();
      double var15 = var1.x() * var1.z();
      double var17 = var1.y() * var1.w();
      double var19 = var1.y() * var1.z();
      double var21 = var1.x() * var1.w();
      double var23 = var3 + var5 - var9 - var7;
      double var25 = var13 + var11 + var11 + var13;
      double var27 = var15 - var17 + var15 - var17;
      double var29 = -var11 + var13 - var11 + var13;
      double var31 = var7 - var9 + var3 - var5;
      double var33 = var19 + var19 + var21 + var21;
      double var35 = var17 + var15 + var15 + var17;
      double var37 = var19 + var19 - var21 - var21;
      double var39 = var9 - var7 - var5 + var3;
      var2.m20 = var35;
      var2.m21 = var37;
      var2.m22 = var39;
      var2.m23 = 0.0D;
      var2.m00 = var23;
      var2.m01 = var25;
      var2.m02 = var27;
      var2.m03 = 0.0D;
      var2.m10 = var29;
      var2.m11 = var31;
      var2.m12 = var33;
      var2.m13 = 0.0D;
      var2.m30 = this.m30;
      var2.m31 = this.m31;
      var2.m32 = this.m32;
      var2.m33 = this.m33;
      var2.properties = (byte)(this.properties & -14);
      return var2;
   }

   public Matrix4d rotateTranslation(Quaternionfc var1, Matrix4d var2) {
      double var3 = (double)(var1.w() * var1.w());
      double var5 = (double)(var1.x() * var1.x());
      double var7 = (double)(var1.y() * var1.y());
      double var9 = (double)(var1.z() * var1.z());
      double var11 = (double)(var1.z() * var1.w());
      double var13 = (double)(var1.x() * var1.y());
      double var15 = (double)(var1.x() * var1.z());
      double var17 = (double)(var1.y() * var1.w());
      double var19 = (double)(var1.y() * var1.z());
      double var21 = (double)(var1.x() * var1.w());
      double var23 = var3 + var5 - var9 - var7;
      double var25 = var13 + var11 + var11 + var13;
      double var27 = var15 - var17 + var15 - var17;
      double var29 = -var11 + var13 - var11 + var13;
      double var31 = var7 - var9 + var3 - var5;
      double var33 = var19 + var19 + var21 + var21;
      double var35 = var17 + var15 + var15 + var17;
      double var37 = var19 + var19 - var21 - var21;
      double var39 = var9 - var7 - var5 + var3;
      var2.m20 = var35;
      var2.m21 = var37;
      var2.m22 = var39;
      var2.m23 = 0.0D;
      var2.m00 = var23;
      var2.m01 = var25;
      var2.m02 = var27;
      var2.m03 = 0.0D;
      var2.m10 = var29;
      var2.m11 = var31;
      var2.m12 = var33;
      var2.m13 = 0.0D;
      var2.m30 = this.m30;
      var2.m31 = this.m31;
      var2.m32 = this.m32;
      var2.m33 = this.m33;
      var2.properties = (byte)(this.properties & -14);
      return var2;
   }

   public Matrix4d rotateLocal(Quaterniondc var1, Matrix4d var2) {
      double var3 = var1.w() * var1.w();
      double var5 = var1.x() * var1.x();
      double var7 = var1.y() * var1.y();
      double var9 = var1.z() * var1.z();
      double var11 = var1.z() * var1.w();
      double var13 = var1.x() * var1.y();
      double var15 = var1.x() * var1.z();
      double var17 = var1.y() * var1.w();
      double var19 = var1.y() * var1.z();
      double var21 = var1.x() * var1.w();
      double var23 = var3 + var5 - var9 - var7;
      double var25 = var13 + var11 + var11 + var13;
      double var27 = var15 - var17 + var15 - var17;
      double var29 = -var11 + var13 - var11 + var13;
      double var31 = var7 - var9 + var3 - var5;
      double var33 = var19 + var19 + var21 + var21;
      double var35 = var17 + var15 + var15 + var17;
      double var37 = var19 + var19 - var21 - var21;
      double var39 = var9 - var7 - var5 + var3;
      double var41 = var23 * this.m00 + var29 * this.m01 + var35 * this.m02;
      double var43 = var25 * this.m00 + var31 * this.m01 + var37 * this.m02;
      double var45 = var27 * this.m00 + var33 * this.m01 + var39 * this.m02;
      double var47 = this.m03;
      double var49 = var23 * this.m10 + var29 * this.m11 + var35 * this.m12;
      double var51 = var25 * this.m10 + var31 * this.m11 + var37 * this.m12;
      double var53 = var27 * this.m10 + var33 * this.m11 + var39 * this.m12;
      double var55 = this.m13;
      double var57 = var23 * this.m20 + var29 * this.m21 + var35 * this.m22;
      double var59 = var25 * this.m20 + var31 * this.m21 + var37 * this.m22;
      double var61 = var27 * this.m20 + var33 * this.m21 + var39 * this.m22;
      double var63 = this.m23;
      double var65 = var23 * this.m30 + var29 * this.m31 + var35 * this.m32;
      double var67 = var25 * this.m30 + var31 * this.m31 + var37 * this.m32;
      double var69 = var27 * this.m30 + var33 * this.m31 + var39 * this.m32;
      double var71 = this.m33;
      var2.m00 = var41;
      var2.m01 = var43;
      var2.m02 = var45;
      var2.m03 = var47;
      var2.m10 = var49;
      var2.m11 = var51;
      var2.m12 = var53;
      var2.m13 = var55;
      var2.m20 = var57;
      var2.m21 = var59;
      var2.m22 = var61;
      var2.m23 = var63;
      var2.m30 = var65;
      var2.m31 = var67;
      var2.m32 = var69;
      var2.m33 = var71;
      var2.properties = (byte)(this.properties & -14);
      return var2;
   }

   public Matrix4d rotateLocal(Quaterniondc var1) {
      return this.rotateLocal(var1, this);
   }

   public Matrix4d rotateAffine(Quaternionfc var1, Matrix4d var2) {
      double var3 = (double)(var1.w() * var1.w());
      double var5 = (double)(var1.x() * var1.x());
      double var7 = (double)(var1.y() * var1.y());
      double var9 = (double)(var1.z() * var1.z());
      double var11 = (double)(var1.z() * var1.w());
      double var13 = (double)(var1.x() * var1.y());
      double var15 = (double)(var1.x() * var1.z());
      double var17 = (double)(var1.y() * var1.w());
      double var19 = (double)(var1.y() * var1.z());
      double var21 = (double)(var1.x() * var1.w());
      double var23 = var3 + var5 - var9 - var7;
      double var25 = var13 + var11 + var11 + var13;
      double var27 = var15 - var17 + var15 - var17;
      double var29 = -var11 + var13 - var11 + var13;
      double var31 = var7 - var9 + var3 - var5;
      double var33 = var19 + var19 + var21 + var21;
      double var35 = var17 + var15 + var15 + var17;
      double var37 = var19 + var19 - var21 - var21;
      double var39 = var9 - var7 - var5 + var3;
      double var41 = this.m00 * var23 + this.m10 * var25 + this.m20 * var27;
      double var43 = this.m01 * var23 + this.m11 * var25 + this.m21 * var27;
      double var45 = this.m02 * var23 + this.m12 * var25 + this.m22 * var27;
      double var47 = this.m00 * var29 + this.m10 * var31 + this.m20 * var33;
      double var49 = this.m01 * var29 + this.m11 * var31 + this.m21 * var33;
      double var51 = this.m02 * var29 + this.m12 * var31 + this.m22 * var33;
      var2.m20 = this.m00 * var35 + this.m10 * var37 + this.m20 * var39;
      var2.m21 = this.m01 * var35 + this.m11 * var37 + this.m21 * var39;
      var2.m22 = this.m02 * var35 + this.m12 * var37 + this.m22 * var39;
      var2.m23 = 0.0D;
      var2.m00 = var41;
      var2.m01 = var43;
      var2.m02 = var45;
      var2.m03 = 0.0D;
      var2.m10 = var47;
      var2.m11 = var49;
      var2.m12 = var51;
      var2.m13 = 0.0D;
      var2.m30 = this.m30;
      var2.m31 = this.m31;
      var2.m32 = this.m32;
      var2.m33 = this.m33;
      var2.properties = (byte)(this.properties & -14);
      return var2;
   }

   public Matrix4d rotateAffine(Quaternionfc var1) {
      return this.rotateAffine(var1, this);
   }

   public Matrix4d rotateLocal(Quaternionfc var1, Matrix4d var2) {
      double var3 = (double)(var1.w() * var1.w());
      double var5 = (double)(var1.x() * var1.x());
      double var7 = (double)(var1.y() * var1.y());
      double var9 = (double)(var1.z() * var1.z());
      double var11 = (double)(var1.z() * var1.w());
      double var13 = (double)(var1.x() * var1.y());
      double var15 = (double)(var1.x() * var1.z());
      double var17 = (double)(var1.y() * var1.w());
      double var19 = (double)(var1.y() * var1.z());
      double var21 = (double)(var1.x() * var1.w());
      double var23 = var3 + var5 - var9 - var7;
      double var25 = var13 + var11 + var11 + var13;
      double var27 = var15 - var17 + var15 - var17;
      double var29 = -var11 + var13 - var11 + var13;
      double var31 = var7 - var9 + var3 - var5;
      double var33 = var19 + var19 + var21 + var21;
      double var35 = var17 + var15 + var15 + var17;
      double var37 = var19 + var19 - var21 - var21;
      double var39 = var9 - var7 - var5 + var3;
      double var41 = var23 * this.m00 + var29 * this.m01 + var35 * this.m02;
      double var43 = var25 * this.m00 + var31 * this.m01 + var37 * this.m02;
      double var45 = var27 * this.m00 + var33 * this.m01 + var39 * this.m02;
      double var47 = this.m03;
      double var49 = var23 * this.m10 + var29 * this.m11 + var35 * this.m12;
      double var51 = var25 * this.m10 + var31 * this.m11 + var37 * this.m12;
      double var53 = var27 * this.m10 + var33 * this.m11 + var39 * this.m12;
      double var55 = this.m13;
      double var57 = var23 * this.m20 + var29 * this.m21 + var35 * this.m22;
      double var59 = var25 * this.m20 + var31 * this.m21 + var37 * this.m22;
      double var61 = var27 * this.m20 + var33 * this.m21 + var39 * this.m22;
      double var63 = this.m23;
      double var65 = var23 * this.m30 + var29 * this.m31 + var35 * this.m32;
      double var67 = var25 * this.m30 + var31 * this.m31 + var37 * this.m32;
      double var69 = var27 * this.m30 + var33 * this.m31 + var39 * this.m32;
      double var71 = this.m33;
      var2.m00 = var41;
      var2.m01 = var43;
      var2.m02 = var45;
      var2.m03 = var47;
      var2.m10 = var49;
      var2.m11 = var51;
      var2.m12 = var53;
      var2.m13 = var55;
      var2.m20 = var57;
      var2.m21 = var59;
      var2.m22 = var61;
      var2.m23 = var63;
      var2.m30 = var65;
      var2.m31 = var67;
      var2.m32 = var69;
      var2.m33 = var71;
      var2.properties = (byte)(this.properties & -14);
      return var2;
   }

   public Matrix4d rotateLocal(Quaternionfc var1) {
      return this.rotateLocal(var1, this);
   }

   public Matrix4d rotate(AxisAngle4f var1) {
      return this.rotate((double)var1.angle, (double)var1.x, (double)var1.y, (double)var1.z);
   }

   public Matrix4d rotate(AxisAngle4f var1, Matrix4d var2) {
      return this.rotate((double)var1.angle, (double)var1.x, (double)var1.y, (double)var1.z, var2);
   }

   public Matrix4d rotate(AxisAngle4d var1) {
      return this.rotate(var1.angle, var1.x, var1.y, var1.z);
   }

   public Matrix4d rotate(AxisAngle4d var1, Matrix4d var2) {
      return this.rotate(var1.angle, var1.x, var1.y, var1.z, var2);
   }

   public Matrix4d rotate(double var1, Vector3dc var3) {
      return this.rotate(var1, var3.x(), var3.y(), var3.z());
   }

   public Matrix4d rotate(double var1, Vector3dc var3, Matrix4d var4) {
      return this.rotate(var1, var3.x(), var3.y(), var3.z(), var4);
   }

   public Matrix4d rotate(double var1, Vector3fc var3) {
      return this.rotate(var1, (double)var3.x(), (double)var3.y(), (double)var3.z());
   }

   public Matrix4d rotate(double var1, Vector3fc var3, Matrix4d var4) {
      return this.rotate(var1, (double)var3.x(), (double)var3.y(), (double)var3.z(), var4);
   }

   public Vector4d getRow(int var1, Vector4d var2) throws IndexOutOfBoundsException {
      switch(var1) {
      case 0:
         var2.x = this.m00;
         var2.y = this.m10;
         var2.z = this.m20;
         var2.w = this.m30;
         break;
      case 1:
         var2.x = this.m01;
         var2.y = this.m11;
         var2.z = this.m21;
         var2.w = this.m31;
         break;
      case 2:
         var2.x = this.m02;
         var2.y = this.m12;
         var2.z = this.m22;
         var2.w = this.m32;
         break;
      case 3:
         var2.x = this.m03;
         var2.y = this.m13;
         var2.z = this.m23;
         var2.w = this.m33;
         break;
      default:
         throw new IndexOutOfBoundsException();
      }

      return var2;
   }

   public Matrix4d setRow(int var1, Vector4dc var2) throws IndexOutOfBoundsException {
      switch(var1) {
      case 0:
         this.m00 = var2.x();
         this.m10 = var2.y();
         this.m20 = var2.z();
         this.m30 = var2.w();
         break;
      case 1:
         this.m01 = var2.x();
         this.m11 = var2.y();
         this.m21 = var2.z();
         this.m31 = var2.w();
         break;
      case 2:
         this.m02 = var2.x();
         this.m12 = var2.y();
         this.m22 = var2.z();
         this.m32 = var2.w();
         break;
      case 3:
         this.m03 = var2.x();
         this.m13 = var2.y();
         this.m23 = var2.z();
         this.m33 = var2.w();
         break;
      default:
         throw new IndexOutOfBoundsException();
      }

      return this;
   }

   public Vector4d getColumn(int var1, Vector4d var2) throws IndexOutOfBoundsException {
      switch(var1) {
      case 0:
         var2.x = this.m00;
         var2.y = this.m01;
         var2.z = this.m02;
         var2.w = this.m03;
         break;
      case 1:
         var2.x = this.m10;
         var2.y = this.m11;
         var2.z = this.m12;
         var2.w = this.m13;
         break;
      case 2:
         var2.x = this.m20;
         var2.y = this.m21;
         var2.z = this.m22;
         var2.w = this.m23;
         break;
      case 3:
         var2.x = this.m30;
         var2.y = this.m31;
         var2.z = this.m32;
         var2.w = this.m33;
         break;
      default:
         throw new IndexOutOfBoundsException();
      }

      return var2;
   }

   public Matrix4d setColumn(int var1, Vector4dc var2) throws IndexOutOfBoundsException {
      switch(var1) {
      case 0:
         this.m00 = var2.x();
         this.m01 = var2.y();
         this.m02 = var2.z();
         this.m03 = var2.w();
         break;
      case 1:
         this.m10 = var2.x();
         this.m11 = var2.y();
         this.m12 = var2.z();
         this.m13 = var2.w();
         break;
      case 2:
         this.m20 = var2.x();
         this.m21 = var2.y();
         this.m22 = var2.z();
         this.m23 = var2.w();
         break;
      case 3:
         this.m30 = var2.x();
         this.m31 = var2.y();
         this.m32 = var2.z();
         this.m33 = var2.w();
         break;
      default:
         throw new IndexOutOfBoundsException();
      }

      return this;
   }

   public Matrix4d normal() {
      return this.normal(this);
   }

   public Matrix4d normal(Matrix4d var1) {
      double var2 = this.m00 * this.m11;
      double var4 = this.m01 * this.m10;
      double var6 = this.m02 * this.m10;
      double var8 = this.m00 * this.m12;
      double var10 = this.m01 * this.m12;
      double var12 = this.m02 * this.m11;
      double var14 = (var2 - var4) * this.m22 + (var6 - var8) * this.m21 + (var10 - var12) * this.m20;
      double var16 = 1.0D / var14;
      double var18 = (this.m11 * this.m22 - this.m21 * this.m12) * var16;
      double var20 = (this.m20 * this.m12 - this.m10 * this.m22) * var16;
      double var22 = (this.m10 * this.m21 - this.m20 * this.m11) * var16;
      double var24 = (this.m21 * this.m02 - this.m01 * this.m22) * var16;
      double var26 = (this.m00 * this.m22 - this.m20 * this.m02) * var16;
      double var28 = (this.m20 * this.m01 - this.m00 * this.m21) * var16;
      double var30 = (var10 - var12) * var16;
      double var32 = (var6 - var8) * var16;
      double var34 = (var2 - var4) * var16;
      var1.m00 = var18;
      var1.m01 = var20;
      var1.m02 = var22;
      var1.m03 = 0.0D;
      var1.m10 = var24;
      var1.m11 = var26;
      var1.m12 = var28;
      var1.m13 = 0.0D;
      var1.m20 = var30;
      var1.m21 = var32;
      var1.m22 = var34;
      var1.m23 = 0.0D;
      var1.m30 = 0.0D;
      var1.m31 = 0.0D;
      var1.m32 = 0.0D;
      var1.m33 = 1.0D;
      var1.properties = 2;
      return var1;
   }

   public Matrix3d normal(Matrix3d var1) {
      double var2 = this.m00 * this.m11;
      double var4 = this.m01 * this.m10;
      double var6 = this.m02 * this.m10;
      double var8 = this.m00 * this.m12;
      double var10 = this.m01 * this.m12;
      double var12 = this.m02 * this.m11;
      double var14 = (var2 - var4) * this.m22 + (var6 - var8) * this.m21 + (var10 - var12) * this.m20;
      double var16 = 1.0D / var14;
      var1.m00 = (this.m11 * this.m22 - this.m21 * this.m12) * var16;
      var1.m01 = (this.m20 * this.m12 - this.m10 * this.m22) * var16;
      var1.m02 = (this.m10 * this.m21 - this.m20 * this.m11) * var16;
      var1.m10 = (this.m21 * this.m02 - this.m01 * this.m22) * var16;
      var1.m11 = (this.m00 * this.m22 - this.m20 * this.m02) * var16;
      var1.m12 = (this.m20 * this.m01 - this.m00 * this.m21) * var16;
      var1.m20 = (var10 - var12) * var16;
      var1.m21 = (var6 - var8) * var16;
      var1.m22 = (var2 - var4) * var16;
      return var1;
   }

   public Matrix4d normalize3x3() {
      return this.normalize3x3(this);
   }

   public Matrix4d normalize3x3(Matrix4d var1) {
      double var2 = 1.0D / Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
      double var4 = 1.0D / Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
      double var6 = 1.0D / Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
      var1.m00 = this.m00 * var2;
      var1.m01 = this.m01 * var2;
      var1.m02 = this.m02 * var2;
      var1.m10 = this.m10 * var4;
      var1.m11 = this.m11 * var4;
      var1.m12 = this.m12 * var4;
      var1.m20 = this.m20 * var6;
      var1.m21 = this.m21 * var6;
      var1.m22 = this.m22 * var6;
      return var1;
   }

   public Matrix3d normalize3x3(Matrix3d var1) {
      double var2 = 1.0D / Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
      double var4 = 1.0D / Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
      double var6 = 1.0D / Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
      var1.m00 = this.m00 * var2;
      var1.m01 = this.m01 * var2;
      var1.m02 = this.m02 * var2;
      var1.m10 = this.m10 * var4;
      var1.m11 = this.m11 * var4;
      var1.m12 = this.m12 * var4;
      var1.m20 = this.m20 * var6;
      var1.m21 = this.m21 * var6;
      var1.m22 = this.m22 * var6;
      return var1;
   }

   public Vector4d unproject(double var1, double var3, double var5, int[] var7, Vector4d var8) {
      double var9 = this.m00 * this.m11 - this.m01 * this.m10;
      double var11 = this.m00 * this.m12 - this.m02 * this.m10;
      double var13 = this.m00 * this.m13 - this.m03 * this.m10;
      double var15 = this.m01 * this.m12 - this.m02 * this.m11;
      double var17 = this.m01 * this.m13 - this.m03 * this.m11;
      double var19 = this.m02 * this.m13 - this.m03 * this.m12;
      double var21 = this.m20 * this.m31 - this.m21 * this.m30;
      double var23 = this.m20 * this.m32 - this.m22 * this.m30;
      double var25 = this.m20 * this.m33 - this.m23 * this.m30;
      double var27 = this.m21 * this.m32 - this.m22 * this.m31;
      double var29 = this.m21 * this.m33 - this.m23 * this.m31;
      double var31 = this.m22 * this.m33 - this.m23 * this.m32;
      double var33 = var9 * var31 - var11 * var29 + var13 * var27 + var15 * var25 - var17 * var23 + var19 * var21;
      var33 = 1.0D / var33;
      double var35 = (this.m11 * var31 - this.m12 * var29 + this.m13 * var27) * var33;
      double var37 = (-this.m01 * var31 + this.m02 * var29 - this.m03 * var27) * var33;
      double var39 = (this.m31 * var19 - this.m32 * var17 + this.m33 * var15) * var33;
      double var41 = (-this.m21 * var19 + this.m22 * var17 - this.m23 * var15) * var33;
      double var43 = (-this.m10 * var31 + this.m12 * var25 - this.m13 * var23) * var33;
      double var45 = (this.m00 * var31 - this.m02 * var25 + this.m03 * var23) * var33;
      double var47 = (-this.m30 * var19 + this.m32 * var13 - this.m33 * var11) * var33;
      double var49 = (this.m20 * var19 - this.m22 * var13 + this.m23 * var11) * var33;
      double var51 = (this.m10 * var29 - this.m11 * var25 + this.m13 * var21) * var33;
      double var53 = (-this.m00 * var29 + this.m01 * var25 - this.m03 * var21) * var33;
      double var55 = (this.m30 * var17 - this.m31 * var13 + this.m33 * var9) * var33;
      double var57 = (-this.m20 * var17 + this.m21 * var13 - this.m23 * var9) * var33;
      double var59 = (-this.m10 * var27 + this.m11 * var23 - this.m12 * var21) * var33;
      double var61 = (this.m00 * var27 - this.m01 * var23 + this.m02 * var21) * var33;
      double var63 = (-this.m30 * var15 + this.m31 * var11 - this.m32 * var9) * var33;
      double var65 = (this.m20 * var15 - this.m21 * var11 + this.m22 * var9) * var33;
      double var67 = (var1 - (double)var7[0]) / (double)var7[2] * 2.0D - 1.0D;
      double var69 = (var3 - (double)var7[1]) / (double)var7[3] * 2.0D - 1.0D;
      double var71 = var5 + var5 - 1.0D;
      var8.x = var35 * var67 + var43 * var69 + var51 * var71 + var59;
      var8.y = var37 * var67 + var45 * var69 + var53 * var71 + var61;
      var8.z = var39 * var67 + var47 * var69 + var55 * var71 + var63;
      var8.w = var41 * var67 + var49 * var69 + var57 * var71 + var65;
      var8.div(var8.w);
      return var8;
   }

   public Vector3d unproject(double var1, double var3, double var5, int[] var7, Vector3d var8) {
      double var9 = this.m00 * this.m11 - this.m01 * this.m10;
      double var11 = this.m00 * this.m12 - this.m02 * this.m10;
      double var13 = this.m00 * this.m13 - this.m03 * this.m10;
      double var15 = this.m01 * this.m12 - this.m02 * this.m11;
      double var17 = this.m01 * this.m13 - this.m03 * this.m11;
      double var19 = this.m02 * this.m13 - this.m03 * this.m12;
      double var21 = this.m20 * this.m31 - this.m21 * this.m30;
      double var23 = this.m20 * this.m32 - this.m22 * this.m30;
      double var25 = this.m20 * this.m33 - this.m23 * this.m30;
      double var27 = this.m21 * this.m32 - this.m22 * this.m31;
      double var29 = this.m21 * this.m33 - this.m23 * this.m31;
      double var31 = this.m22 * this.m33 - this.m23 * this.m32;
      double var33 = var9 * var31 - var11 * var29 + var13 * var27 + var15 * var25 - var17 * var23 + var19 * var21;
      var33 = 1.0D / var33;
      double var35 = (this.m11 * var31 - this.m12 * var29 + this.m13 * var27) * var33;
      double var37 = (-this.m01 * var31 + this.m02 * var29 - this.m03 * var27) * var33;
      double var39 = (this.m31 * var19 - this.m32 * var17 + this.m33 * var15) * var33;
      double var41 = (-this.m21 * var19 + this.m22 * var17 - this.m23 * var15) * var33;
      double var43 = (-this.m10 * var31 + this.m12 * var25 - this.m13 * var23) * var33;
      double var45 = (this.m00 * var31 - this.m02 * var25 + this.m03 * var23) * var33;
      double var47 = (-this.m30 * var19 + this.m32 * var13 - this.m33 * var11) * var33;
      double var49 = (this.m20 * var19 - this.m22 * var13 + this.m23 * var11) * var33;
      double var51 = (this.m10 * var29 - this.m11 * var25 + this.m13 * var21) * var33;
      double var53 = (-this.m00 * var29 + this.m01 * var25 - this.m03 * var21) * var33;
      double var55 = (this.m30 * var17 - this.m31 * var13 + this.m33 * var9) * var33;
      double var57 = (-this.m20 * var17 + this.m21 * var13 - this.m23 * var9) * var33;
      double var59 = (-this.m10 * var27 + this.m11 * var23 - this.m12 * var21) * var33;
      double var61 = (this.m00 * var27 - this.m01 * var23 + this.m02 * var21) * var33;
      double var63 = (-this.m30 * var15 + this.m31 * var11 - this.m32 * var9) * var33;
      double var65 = (this.m20 * var15 - this.m21 * var11 + this.m22 * var9) * var33;
      double var67 = (var1 - (double)var7[0]) / (double)var7[2] * 2.0D - 1.0D;
      double var69 = (var3 - (double)var7[1]) / (double)var7[3] * 2.0D - 1.0D;
      double var71 = var5 + var5 - 1.0D;
      var8.x = var35 * var67 + var43 * var69 + var51 * var71 + var59;
      var8.y = var37 * var67 + var45 * var69 + var53 * var71 + var61;
      var8.z = var39 * var67 + var47 * var69 + var55 * var71 + var63;
      double var73 = var41 * var67 + var49 * var69 + var57 * var71 + var65;
      var8.div(var73);
      return var8;
   }

   public Vector4d unproject(Vector3dc var1, int[] var2, Vector4d var3) {
      return this.unproject(var1.x(), var1.y(), var1.z(), var2, var3);
   }

   public Vector3d unproject(Vector3dc var1, int[] var2, Vector3d var3) {
      return this.unproject(var1.x(), var1.y(), var1.z(), var2, var3);
   }

   public Matrix4d unprojectRay(double var1, double var3, int[] var5, Vector3d var6, Vector3d var7) {
      double var8 = this.m00 * this.m11 - this.m01 * this.m10;
      double var10 = this.m00 * this.m12 - this.m02 * this.m10;
      double var12 = this.m00 * this.m13 - this.m03 * this.m10;
      double var14 = this.m01 * this.m12 - this.m02 * this.m11;
      double var16 = this.m01 * this.m13 - this.m03 * this.m11;
      double var18 = this.m02 * this.m13 - this.m03 * this.m12;
      double var20 = this.m20 * this.m31 - this.m21 * this.m30;
      double var22 = this.m20 * this.m32 - this.m22 * this.m30;
      double var24 = this.m20 * this.m33 - this.m23 * this.m30;
      double var26 = this.m21 * this.m32 - this.m22 * this.m31;
      double var28 = this.m21 * this.m33 - this.m23 * this.m31;
      double var30 = this.m22 * this.m33 - this.m23 * this.m32;
      double var32 = var8 * var30 - var10 * var28 + var12 * var26 + var14 * var24 - var16 * var22 + var18 * var20;
      var32 = 1.0D / var32;
      double var34 = (this.m11 * var30 - this.m12 * var28 + this.m13 * var26) * var32;
      double var36 = (-this.m01 * var30 + this.m02 * var28 - this.m03 * var26) * var32;
      double var38 = (this.m31 * var18 - this.m32 * var16 + this.m33 * var14) * var32;
      double var40 = (-this.m21 * var18 + this.m22 * var16 - this.m23 * var14) * var32;
      double var42 = (-this.m10 * var30 + this.m12 * var24 - this.m13 * var22) * var32;
      double var44 = (this.m00 * var30 - this.m02 * var24 + this.m03 * var22) * var32;
      double var46 = (-this.m30 * var18 + this.m32 * var12 - this.m33 * var10) * var32;
      double var48 = (this.m20 * var18 - this.m22 * var12 + this.m23 * var10) * var32;
      double var50 = (this.m10 * var28 - this.m11 * var24 + this.m13 * var20) * var32;
      double var52 = (-this.m00 * var28 + this.m01 * var24 - this.m03 * var20) * var32;
      double var54 = (this.m30 * var16 - this.m31 * var12 + this.m33 * var8) * var32;
      double var56 = (-this.m20 * var16 + this.m21 * var12 - this.m23 * var8) * var32;
      double var58 = (-this.m10 * var26 + this.m11 * var22 - this.m12 * var20) * var32;
      double var60 = (this.m00 * var26 - this.m01 * var22 + this.m02 * var20) * var32;
      double var62 = (-this.m30 * var14 + this.m31 * var10 - this.m32 * var8) * var32;
      double var64 = (this.m20 * var14 - this.m21 * var10 + this.m22 * var8) * var32;
      double var66 = (var1 - (double)var5[0]) / (double)var5[2] * 2.0D - 1.0D;
      double var68 = (var3 - (double)var5[1]) / (double)var5[3] * 2.0D - 1.0D;
      double var70 = var34 * var66 + var42 * var68 - var50 + var58;
      double var72 = var36 * var66 + var44 * var68 - var52 + var60;
      double var74 = var38 * var66 + var46 * var68 - var54 + var62;
      double var76 = 1.0D / (var40 * var66 + var48 * var68 - var56 + var64);
      var70 *= var76;
      var72 *= var76;
      var74 *= var76;
      double var78 = var34 * var66 + var42 * var68 + var50 + var58;
      double var80 = var36 * var66 + var44 * var68 + var52 + var60;
      double var82 = var38 * var66 + var46 * var68 + var54 + var62;
      double var84 = 1.0D / (var40 * var66 + var48 * var68 + var56 + var64);
      var78 *= var84;
      var80 *= var84;
      var82 *= var84;
      var6.x = var70;
      var6.y = var72;
      var6.z = var74;
      var7.x = var78 - var70;
      var7.y = var80 - var72;
      var7.z = var82 - var74;
      return this;
   }

   public Matrix4d unprojectRay(Vector2dc var1, int[] var2, Vector3d var3, Vector3d var4) {
      return this.unprojectRay(var1.x(), var1.y(), var2, var3, var4);
   }

   public Vector4d unprojectInv(Vector3dc var1, int[] var2, Vector4d var3) {
      return this.unprojectInv(var1.x(), var1.y(), var1.z(), var2, var3);
   }

   public Vector4d unprojectInv(double var1, double var3, double var5, int[] var7, Vector4d var8) {
      double var9 = (var1 - (double)var7[0]) / (double)var7[2] * 2.0D - 1.0D;
      double var11 = (var3 - (double)var7[1]) / (double)var7[3] * 2.0D - 1.0D;
      double var13 = var5 + var5 - 1.0D;
      var8.x = this.m00 * var9 + this.m10 * var11 + this.m20 * var13 + this.m30;
      var8.y = this.m01 * var9 + this.m11 * var11 + this.m21 * var13 + this.m31;
      var8.z = this.m02 * var9 + this.m12 * var11 + this.m22 * var13 + this.m32;
      var8.w = this.m03 * var9 + this.m13 * var11 + this.m23 * var13 + this.m33;
      var8.div(var8.w);
      return var8;
   }

   public Vector3d unprojectInv(Vector3dc var1, int[] var2, Vector3d var3) {
      return this.unprojectInv(var1.x(), var1.y(), var1.z(), var2, var3);
   }

   public Vector3d unprojectInv(double var1, double var3, double var5, int[] var7, Vector3d var8) {
      double var9 = (var1 - (double)var7[0]) / (double)var7[2] * 2.0D - 1.0D;
      double var11 = (var3 - (double)var7[1]) / (double)var7[3] * 2.0D - 1.0D;
      double var13 = var5 + var5 - 1.0D;
      var8.x = this.m00 * var9 + this.m10 * var11 + this.m20 * var13 + this.m30;
      var8.y = this.m01 * var9 + this.m11 * var11 + this.m21 * var13 + this.m31;
      var8.z = this.m02 * var9 + this.m12 * var11 + this.m22 * var13 + this.m32;
      double var15 = this.m03 * var9 + this.m13 * var11 + this.m23 * var13 + this.m33;
      var8.div(var15);
      return var8;
   }

   public Matrix4d unprojectInvRay(Vector2dc var1, int[] var2, Vector3d var3, Vector3d var4) {
      return this.unprojectInvRay(var1.x(), var1.y(), var2, var3, var4);
   }

   public Matrix4d unprojectInvRay(double var1, double var3, int[] var5, Vector3d var6, Vector3d var7) {
      double var8 = (var1 - (double)var5[0]) / (double)var5[2] * 2.0D - 1.0D;
      double var10 = (var3 - (double)var5[1]) / (double)var5[3] * 2.0D - 1.0D;
      double var12 = this.m00 * var8 + this.m10 * var10 - this.m20 + this.m30;
      double var14 = this.m01 * var8 + this.m11 * var10 - this.m21 + this.m31;
      double var16 = this.m02 * var8 + this.m12 * var10 - this.m22 + this.m32;
      double var18 = 1.0D / (this.m03 * var8 + this.m13 * var10 - this.m23 + this.m33);
      var12 *= var18;
      var14 *= var18;
      var16 *= var18;
      double var20 = this.m00 * var8 + this.m10 * var10 + this.m20 + this.m30;
      double var22 = this.m01 * var8 + this.m11 * var10 + this.m21 + this.m31;
      double var24 = this.m02 * var8 + this.m12 * var10 + this.m22 + this.m32;
      double var26 = 1.0D / (this.m03 * var8 + this.m13 * var10 + this.m23 + this.m33);
      var20 *= var26;
      var22 *= var26;
      var24 *= var26;
      var6.x = var12;
      var6.y = var14;
      var6.z = var16;
      var7.x = var20 - var12;
      var7.y = var22 - var14;
      var7.z = var24 - var16;
      return this;
   }

   public Vector4d project(double var1, double var3, double var5, int[] var7, Vector4d var8) {
      var8.x = this.m00 * var1 + this.m10 * var3 + this.m20 * var5 + this.m30;
      var8.y = this.m01 * var1 + this.m11 * var3 + this.m21 * var5 + this.m31;
      var8.z = this.m02 * var1 + this.m12 * var3 + this.m22 * var5 + this.m32;
      var8.w = this.m03 * var1 + this.m13 * var3 + this.m23 * var5 + this.m33;
      var8.div(var8.w);
      var8.x = (var8.x * 0.5D + 0.5D) * (double)var7[2] + (double)var7[0];
      var8.y = (var8.y * 0.5D + 0.5D) * (double)var7[3] + (double)var7[1];
      var8.z = (1.0D + var8.z) * 0.5D;
      return var8;
   }

   public Vector3d project(double var1, double var3, double var5, int[] var7, Vector3d var8) {
      var8.x = this.m00 * var1 + this.m10 * var3 + this.m20 * var5 + this.m30;
      var8.y = this.m01 * var1 + this.m11 * var3 + this.m21 * var5 + this.m31;
      var8.z = this.m02 * var1 + this.m12 * var3 + this.m22 * var5 + this.m32;
      double var9 = this.m03 * var1 + this.m13 * var3 + this.m23 * var5 + this.m33;
      var8.div(var9);
      var8.x = (var8.x * 0.5D + 0.5D) * (double)var7[2] + (double)var7[0];
      var8.y = (var8.y * 0.5D + 0.5D) * (double)var7[3] + (double)var7[1];
      var8.z = (1.0D + var8.z) * 0.5D;
      return var8;
   }

   public Vector4d project(Vector3dc var1, int[] var2, Vector4d var3) {
      return this.project(var1.x(), var1.y(), var1.z(), var2, var3);
   }

   public Vector3d project(Vector3dc var1, int[] var2, Vector3d var3) {
      return this.project(var1.x(), var1.y(), var1.z(), var2, var3);
   }

   public Matrix4d reflect(double var1, double var3, double var5, double var7, Matrix4d var9) {
      if ((this.properties & 4) != 0) {
         return var9.reflection(var1, var3, var5, var7);
      } else {
         double var10 = var1 + var1;
         double var12 = var3 + var3;
         double var14 = var5 + var5;
         double var16 = var7 + var7;
         double var18 = 1.0D - var10 * var1;
         double var20 = -var10 * var3;
         double var22 = -var10 * var5;
         double var24 = -var12 * var1;
         double var26 = 1.0D - var12 * var3;
         double var28 = -var12 * var5;
         double var30 = -var14 * var1;
         double var32 = -var14 * var3;
         double var34 = 1.0D - var14 * var5;
         double var36 = -var16 * var1;
         double var38 = -var16 * var3;
         double var40 = -var16 * var5;
         var9.m30 = this.m00 * var36 + this.m10 * var38 + this.m20 * var40 + this.m30;
         var9.m31 = this.m01 * var36 + this.m11 * var38 + this.m21 * var40 + this.m31;
         var9.m32 = this.m02 * var36 + this.m12 * var38 + this.m22 * var40 + this.m32;
         var9.m33 = this.m03 * var36 + this.m13 * var38 + this.m23 * var40 + this.m33;
         double var42 = this.m00 * var18 + this.m10 * var20 + this.m20 * var22;
         double var44 = this.m01 * var18 + this.m11 * var20 + this.m21 * var22;
         double var46 = this.m02 * var18 + this.m12 * var20 + this.m22 * var22;
         double var48 = this.m03 * var18 + this.m13 * var20 + this.m23 * var22;
         double var50 = this.m00 * var24 + this.m10 * var26 + this.m20 * var28;
         double var52 = this.m01 * var24 + this.m11 * var26 + this.m21 * var28;
         double var54 = this.m02 * var24 + this.m12 * var26 + this.m22 * var28;
         double var56 = this.m03 * var24 + this.m13 * var26 + this.m23 * var28;
         var9.m20 = this.m00 * var30 + this.m10 * var32 + this.m20 * var34;
         var9.m21 = this.m01 * var30 + this.m11 * var32 + this.m21 * var34;
         var9.m22 = this.m02 * var30 + this.m12 * var32 + this.m22 * var34;
         var9.m23 = this.m03 * var30 + this.m13 * var32 + this.m23 * var34;
         var9.m00 = var42;
         var9.m01 = var44;
         var9.m02 = var46;
         var9.m03 = var48;
         var9.m10 = var50;
         var9.m11 = var52;
         var9.m12 = var54;
         var9.m13 = var56;
         var9.properties = (byte)(this.properties & -14);
         return var9;
      }
   }

   public Matrix4d reflect(double var1, double var3, double var5, double var7) {
      return this.reflect(var1, var3, var5, var7, this);
   }

   public Matrix4d reflect(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.reflect(var1, var3, var5, var7, var9, var11, this);
   }

   public Matrix4d reflect(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
      double var14 = 1.0D / Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
      double var16 = var1 * var14;
      double var18 = var3 * var14;
      double var20 = var5 * var14;
      return this.reflect(var16, var18, var20, -var16 * var7 - var18 * var9 - var20 * var11, var13);
   }

   public Matrix4d reflect(Vector3dc var1, Vector3dc var2) {
      return this.reflect(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z());
   }

   public Matrix4d reflect(Quaterniondc var1, Vector3dc var2) {
      return this.reflect(var1, var2, this);
   }

   public Matrix4d reflect(Quaterniondc var1, Vector3dc var2, Matrix4d var3) {
      double var4 = var1.x() + var1.x();
      double var6 = var1.y() + var1.y();
      double var8 = var1.z() + var1.z();
      double var10 = var1.x() * var8 + var1.w() * var6;
      double var12 = var1.y() * var8 - var1.w() * var4;
      double var14 = 1.0D - (var1.x() * var4 + var1.y() * var6);
      return this.reflect(var10, var12, var14, var2.x(), var2.y(), var2.z(), var3);
   }

   public Matrix4d reflect(Vector3dc var1, Vector3dc var2, Matrix4d var3) {
      return this.reflect(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public Matrix4d reflection(double var1, double var3, double var5, double var7) {
      double var9 = var1 + var1;
      double var11 = var3 + var3;
      double var13 = var5 + var5;
      double var15 = var7 + var7;
      this.m00 = 1.0D - var9 * var1;
      this.m01 = -var9 * var3;
      this.m02 = -var9 * var5;
      this.m03 = 0.0D;
      this.m10 = -var11 * var1;
      this.m11 = 1.0D - var11 * var3;
      this.m12 = -var11 * var5;
      this.m13 = 0.0D;
      this.m20 = -var13 * var1;
      this.m21 = -var13 * var3;
      this.m22 = 1.0D - var13 * var5;
      this.m23 = 0.0D;
      this.m30 = -var15 * var1;
      this.m31 = -var15 * var3;
      this.m32 = -var15 * var5;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d reflection(double var1, double var3, double var5, double var7, double var9, double var11) {
      double var13 = 1.0D / Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
      double var15 = var1 * var13;
      double var17 = var3 * var13;
      double var19 = var5 * var13;
      return this.reflection(var15, var17, var19, -var15 * var7 - var17 * var9 - var19 * var11);
   }

   public Matrix4d reflection(Vector3dc var1, Vector3dc var2) {
      return this.reflection(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z());
   }

   public Matrix4d reflection(Quaterniondc var1, Vector3dc var2) {
      double var3 = var1.x() + var1.x();
      double var5 = var1.y() + var1.y();
      double var7 = var1.z() + var1.z();
      double var9 = var1.x() * var7 + var1.w() * var5;
      double var11 = var1.y() * var7 - var1.w() * var3;
      double var13 = 1.0D - (var1.x() * var3 + var1.y() * var5);
      return this.reflection(var9, var11, var13, var2.x(), var2.y(), var2.z());
   }

   public Matrix4d ortho(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14) {
      double var15 = 2.0D / (var3 - var1);
      double var17 = 2.0D / (var7 - var5);
      double var19 = (var13 ? 1.0D : 2.0D) / (var9 - var11);
      double var21 = (var1 + var3) / (var1 - var3);
      double var23 = (var7 + var5) / (var5 - var7);
      double var25 = (var13 ? var9 : var11 + var9) / (var9 - var11);
      var14.m30 = this.m00 * var21 + this.m10 * var23 + this.m20 * var25 + this.m30;
      var14.m31 = this.m01 * var21 + this.m11 * var23 + this.m21 * var25 + this.m31;
      var14.m32 = this.m02 * var21 + this.m12 * var23 + this.m22 * var25 + this.m32;
      var14.m33 = this.m03 * var21 + this.m13 * var23 + this.m23 * var25 + this.m33;
      var14.m00 = this.m00 * var15;
      var14.m01 = this.m01 * var15;
      var14.m02 = this.m02 * var15;
      var14.m03 = this.m03 * var15;
      var14.m10 = this.m10 * var17;
      var14.m11 = this.m11 * var17;
      var14.m12 = this.m12 * var17;
      var14.m13 = this.m13 * var17;
      var14.m20 = this.m20 * var19;
      var14.m21 = this.m21 * var19;
      var14.m22 = this.m22 * var19;
      var14.m23 = this.m23 * var19;
      var14.properties = (byte)(this.properties & -14);
      return var14;
   }

   public Matrix4d ortho(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
      return this.ortho(var1, var3, var5, var7, var9, var11, false, var13);
   }

   public Matrix4d ortho(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13) {
      return this.ortho(var1, var3, var5, var7, var9, var11, var13, this);
   }

   public Matrix4d ortho(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.ortho(var1, var3, var5, var7, var9, var11, false);
   }

   public Matrix4d orthoLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14) {
      double var15 = 2.0D / (var3 - var1);
      double var17 = 2.0D / (var7 - var5);
      double var19 = (var13 ? 1.0D : 2.0D) / (var11 - var9);
      double var21 = (var1 + var3) / (var1 - var3);
      double var23 = (var7 + var5) / (var5 - var7);
      double var25 = (var13 ? var9 : var11 + var9) / (var9 - var11);
      var14.m30 = this.m00 * var21 + this.m10 * var23 + this.m20 * var25 + this.m30;
      var14.m31 = this.m01 * var21 + this.m11 * var23 + this.m21 * var25 + this.m31;
      var14.m32 = this.m02 * var21 + this.m12 * var23 + this.m22 * var25 + this.m32;
      var14.m33 = this.m03 * var21 + this.m13 * var23 + this.m23 * var25 + this.m33;
      var14.m00 = this.m00 * var15;
      var14.m01 = this.m01 * var15;
      var14.m02 = this.m02 * var15;
      var14.m03 = this.m03 * var15;
      var14.m10 = this.m10 * var17;
      var14.m11 = this.m11 * var17;
      var14.m12 = this.m12 * var17;
      var14.m13 = this.m13 * var17;
      var14.m20 = this.m20 * var19;
      var14.m21 = this.m21 * var19;
      var14.m22 = this.m22 * var19;
      var14.m23 = this.m23 * var19;
      var14.properties = (byte)(this.properties & -14);
      return var14;
   }

   public Matrix4d orthoLH(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
      return this.orthoLH(var1, var3, var5, var7, var9, var11, false, var13);
   }

   public Matrix4d orthoLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13) {
      return this.orthoLH(var1, var3, var5, var7, var9, var11, var13, this);
   }

   public Matrix4d orthoLH(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.orthoLH(var1, var3, var5, var7, var9, var11, false);
   }

   public Matrix4d setOrtho(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13) {
      this.m00 = 2.0D / (var3 - var1);
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 2.0D / (var7 - var5);
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = (var13 ? 1.0D : 2.0D) / (var9 - var11);
      this.m23 = 0.0D;
      this.m30 = (var3 + var1) / (var1 - var3);
      this.m31 = (var7 + var5) / (var5 - var7);
      this.m32 = (var13 ? var9 : var11 + var9) / (var9 - var11);
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d setOrtho(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.setOrtho(var1, var3, var5, var7, var9, var11, false);
   }

   public Matrix4d setOrthoLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13) {
      this.m00 = 2.0D / (var3 - var1);
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 2.0D / (var7 - var5);
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = (var13 ? 1.0D : 2.0D) / (var11 - var9);
      this.m23 = 0.0D;
      this.m30 = (var3 + var1) / (var1 - var3);
      this.m31 = (var7 + var5) / (var5 - var7);
      this.m32 = (var13 ? var9 : var11 + var9) / (var9 - var11);
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d setOrthoLH(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.setOrthoLH(var1, var3, var5, var7, var9, var11, false);
   }

   public Matrix4d orthoSymmetric(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10) {
      double var11 = 2.0D / var1;
      double var13 = 2.0D / var3;
      double var15 = (var9 ? 1.0D : 2.0D) / (var5 - var7);
      double var17 = (var9 ? var5 : var7 + var5) / (var5 - var7);
      var10.m30 = this.m20 * var17 + this.m30;
      var10.m31 = this.m21 * var17 + this.m31;
      var10.m32 = this.m22 * var17 + this.m32;
      var10.m33 = this.m23 * var17 + this.m33;
      var10.m00 = this.m00 * var11;
      var10.m01 = this.m01 * var11;
      var10.m02 = this.m02 * var11;
      var10.m03 = this.m03 * var11;
      var10.m10 = this.m10 * var13;
      var10.m11 = this.m11 * var13;
      var10.m12 = this.m12 * var13;
      var10.m13 = this.m13 * var13;
      var10.m20 = this.m20 * var15;
      var10.m21 = this.m21 * var15;
      var10.m22 = this.m22 * var15;
      var10.m23 = this.m23 * var15;
      var10.properties = (byte)(this.properties & -14);
      return var10;
   }

   public Matrix4d orthoSymmetric(double var1, double var3, double var5, double var7, Matrix4d var9) {
      return this.orthoSymmetric(var1, var3, var5, var7, false, var9);
   }

   public Matrix4d orthoSymmetric(double var1, double var3, double var5, double var7, boolean var9) {
      return this.orthoSymmetric(var1, var3, var5, var7, var9, this);
   }

   public Matrix4d orthoSymmetric(double var1, double var3, double var5, double var7) {
      return this.orthoSymmetric(var1, var3, var5, var7, false, this);
   }

   public Matrix4d orthoSymmetricLH(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10) {
      double var11 = 2.0D / var1;
      double var13 = 2.0D / var3;
      double var15 = (var9 ? 1.0D : 2.0D) / (var7 - var5);
      double var17 = (var9 ? var5 : var7 + var5) / (var5 - var7);
      var10.m30 = this.m20 * var17 + this.m30;
      var10.m31 = this.m21 * var17 + this.m31;
      var10.m32 = this.m22 * var17 + this.m32;
      var10.m33 = this.m23 * var17 + this.m33;
      var10.m00 = this.m00 * var11;
      var10.m01 = this.m01 * var11;
      var10.m02 = this.m02 * var11;
      var10.m03 = this.m03 * var11;
      var10.m10 = this.m10 * var13;
      var10.m11 = this.m11 * var13;
      var10.m12 = this.m12 * var13;
      var10.m13 = this.m13 * var13;
      var10.m20 = this.m20 * var15;
      var10.m21 = this.m21 * var15;
      var10.m22 = this.m22 * var15;
      var10.m23 = this.m23 * var15;
      var10.properties = (byte)(this.properties & -14);
      return var10;
   }

   public Matrix4d orthoSymmetricLH(double var1, double var3, double var5, double var7, Matrix4d var9) {
      return this.orthoSymmetricLH(var1, var3, var5, var7, false, var9);
   }

   public Matrix4d orthoSymmetricLH(double var1, double var3, double var5, double var7, boolean var9) {
      return this.orthoSymmetricLH(var1, var3, var5, var7, var9, this);
   }

   public Matrix4d orthoSymmetricLH(double var1, double var3, double var5, double var7) {
      return this.orthoSymmetricLH(var1, var3, var5, var7, false, this);
   }

   public Matrix4d setOrthoSymmetric(double var1, double var3, double var5, double var7, boolean var9) {
      this.m00 = 2.0D / var1;
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 2.0D / var3;
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = (var9 ? 1.0D : 2.0D) / (var5 - var7);
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = (var9 ? var5 : var7 + var5) / (var5 - var7);
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d setOrthoSymmetric(double var1, double var3, double var5, double var7) {
      return this.setOrthoSymmetric(var1, var3, var5, var7, false);
   }

   public Matrix4d setOrthoSymmetricLH(double var1, double var3, double var5, double var7, boolean var9) {
      this.m00 = 2.0D / var1;
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 2.0D / var3;
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = (var9 ? 1.0D : 2.0D) / (var7 - var5);
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = (var9 ? var5 : var7 + var5) / (var5 - var7);
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d setOrthoSymmetricLH(double var1, double var3, double var5, double var7) {
      return this.setOrthoSymmetricLH(var1, var3, var5, var7, false);
   }

   public Matrix4d ortho2D(double var1, double var3, double var5, double var7, Matrix4d var9) {
      double var10 = 2.0D / (var3 - var1);
      double var12 = 2.0D / (var7 - var5);
      double var14 = -(var3 + var1) / (var3 - var1);
      double var16 = -(var7 + var5) / (var7 - var5);
      var9.m30 = this.m00 * var14 + this.m10 * var16 + this.m30;
      var9.m31 = this.m01 * var14 + this.m11 * var16 + this.m31;
      var9.m32 = this.m02 * var14 + this.m12 * var16 + this.m32;
      var9.m33 = this.m03 * var14 + this.m13 * var16 + this.m33;
      var9.m00 = this.m00 * var10;
      var9.m01 = this.m01 * var10;
      var9.m02 = this.m02 * var10;
      var9.m03 = this.m03 * var10;
      var9.m10 = this.m10 * var12;
      var9.m11 = this.m11 * var12;
      var9.m12 = this.m12 * var12;
      var9.m13 = this.m13 * var12;
      var9.m20 = -this.m20;
      var9.m21 = -this.m21;
      var9.m22 = -this.m22;
      var9.m23 = -this.m23;
      var9.properties = (byte)(this.properties & -14);
      return var9;
   }

   public Matrix4d ortho2D(double var1, double var3, double var5, double var7) {
      return this.ortho2D(var1, var3, var5, var7, this);
   }

   public Matrix4d ortho2DLH(double var1, double var3, double var5, double var7, Matrix4d var9) {
      double var10 = 2.0D / (var3 - var1);
      double var12 = 2.0D / (var7 - var5);
      double var14 = -(var3 + var1) / (var3 - var1);
      double var16 = -(var7 + var5) / (var7 - var5);
      var9.m30 = this.m00 * var14 + this.m10 * var16 + this.m30;
      var9.m31 = this.m01 * var14 + this.m11 * var16 + this.m31;
      var9.m32 = this.m02 * var14 + this.m12 * var16 + this.m32;
      var9.m33 = this.m03 * var14 + this.m13 * var16 + this.m33;
      var9.m00 = this.m00 * var10;
      var9.m01 = this.m01 * var10;
      var9.m02 = this.m02 * var10;
      var9.m03 = this.m03 * var10;
      var9.m10 = this.m10 * var12;
      var9.m11 = this.m11 * var12;
      var9.m12 = this.m12 * var12;
      var9.m13 = this.m13 * var12;
      var9.m20 = this.m20;
      var9.m21 = this.m21;
      var9.m22 = this.m22;
      var9.m23 = this.m23;
      var9.properties = (byte)(this.properties & -14);
      return var9;
   }

   public Matrix4d ortho2DLH(double var1, double var3, double var5, double var7) {
      return this.ortho2DLH(var1, var3, var5, var7, this);
   }

   public Matrix4d setOrtho2D(double var1, double var3, double var5, double var7) {
      this.m00 = 2.0D / (var3 - var1);
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 2.0D / (var7 - var5);
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = -1.0D;
      this.m23 = 0.0D;
      this.m30 = -(var3 + var1) / (var3 - var1);
      this.m31 = -(var7 + var5) / (var7 - var5);
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d setOrtho2DLH(double var1, double var3, double var5, double var7) {
      this.m00 = 2.0D / (var3 - var1);
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 2.0D / (var7 - var5);
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = 1.0D;
      this.m23 = 0.0D;
      this.m30 = -(var3 + var1) / (var3 - var1);
      this.m31 = -(var7 + var5) / (var7 - var5);
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d lookAlong(Vector3dc var1, Vector3dc var2) {
      return this.lookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), this);
   }

   public Matrix4d lookAlong(Vector3dc var1, Vector3dc var2, Matrix4d var3) {
      return this.lookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public Matrix4d lookAlong(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
      if ((this.properties & 4) != 0) {
         return this.setLookAlong(var1, var3, var5, var7, var9, var11);
      } else {
         double var14 = 1.0D / Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
         double var16 = var1 * var14;
         double var18 = var3 * var14;
         double var20 = var5 * var14;
         double var22 = var18 * var11 - var20 * var9;
         double var24 = var20 * var7 - var16 * var11;
         double var26 = var16 * var9 - var18 * var7;
         double var28 = 1.0D / Math.sqrt(var22 * var22 + var24 * var24 + var26 * var26);
         var22 *= var28;
         var24 *= var28;
         var26 *= var28;
         double var30 = var24 * var20 - var26 * var18;
         double var32 = var26 * var16 - var22 * var20;
         double var34 = var22 * var18 - var24 * var16;
         double var40 = -var16;
         double var46 = -var18;
         double var52 = -var20;
         double var54 = this.m00 * var22 + this.m10 * var30 + this.m20 * var40;
         double var56 = this.m01 * var22 + this.m11 * var30 + this.m21 * var40;
         double var58 = this.m02 * var22 + this.m12 * var30 + this.m22 * var40;
         double var60 = this.m03 * var22 + this.m13 * var30 + this.m23 * var40;
         double var62 = this.m00 * var24 + this.m10 * var32 + this.m20 * var46;
         double var64 = this.m01 * var24 + this.m11 * var32 + this.m21 * var46;
         double var66 = this.m02 * var24 + this.m12 * var32 + this.m22 * var46;
         double var68 = this.m03 * var24 + this.m13 * var32 + this.m23 * var46;
         var13.m20 = this.m00 * var26 + this.m10 * var34 + this.m20 * var52;
         var13.m21 = this.m01 * var26 + this.m11 * var34 + this.m21 * var52;
         var13.m22 = this.m02 * var26 + this.m12 * var34 + this.m22 * var52;
         var13.m23 = this.m03 * var26 + this.m13 * var34 + this.m23 * var52;
         var13.m00 = var54;
         var13.m01 = var56;
         var13.m02 = var58;
         var13.m03 = var60;
         var13.m10 = var62;
         var13.m11 = var64;
         var13.m12 = var66;
         var13.m13 = var68;
         var13.m30 = this.m30;
         var13.m31 = this.m31;
         var13.m32 = this.m32;
         var13.m33 = this.m33;
         var13.properties = (byte)(this.properties & -14);
         return var13;
      }
   }

   public Matrix4d lookAlong(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.lookAlong(var1, var3, var5, var7, var9, var11, this);
   }

   public Matrix4d setLookAlong(Vector3dc var1, Vector3dc var2) {
      return this.setLookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z());
   }

   public Matrix4d setLookAlong(double var1, double var3, double var5, double var7, double var9, double var11) {
      double var13 = 1.0D / Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
      double var15 = var1 * var13;
      double var17 = var3 * var13;
      double var19 = var5 * var13;
      double var21 = var17 * var11 - var19 * var9;
      double var23 = var19 * var7 - var15 * var11;
      double var25 = var15 * var9 - var17 * var7;
      double var27 = 1.0D / Math.sqrt(var21 * var21 + var23 * var23 + var25 * var25);
      var21 *= var27;
      var23 *= var27;
      var25 *= var27;
      double var29 = var23 * var19 - var25 * var17;
      double var31 = var25 * var15 - var21 * var19;
      double var33 = var21 * var17 - var23 * var15;
      this.m00 = var21;
      this.m01 = var29;
      this.m02 = -var15;
      this.m03 = 0.0D;
      this.m10 = var23;
      this.m11 = var31;
      this.m12 = -var17;
      this.m13 = 0.0D;
      this.m20 = var25;
      this.m21 = var33;
      this.m22 = -var19;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d setLookAt(Vector3dc var1, Vector3dc var2, Vector3dc var3) {
      return this.setLookAt(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z());
   }

   public Matrix4d setLookAt(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17) {
      double var19 = var1 - var7;
      double var21 = var3 - var9;
      double var23 = var5 - var11;
      double var25 = 1.0D / Math.sqrt(var19 * var19 + var21 * var21 + var23 * var23);
      var19 *= var25;
      var21 *= var25;
      var23 *= var25;
      double var27 = var15 * var23 - var17 * var21;
      double var29 = var17 * var19 - var13 * var23;
      double var31 = var13 * var21 - var15 * var19;
      double var33 = 1.0D / Math.sqrt(var27 * var27 + var29 * var29 + var31 * var31);
      var27 *= var33;
      var29 *= var33;
      var31 *= var33;
      double var35 = var21 * var31 - var23 * var29;
      double var37 = var23 * var27 - var19 * var31;
      double var39 = var19 * var29 - var21 * var27;
      this.m00 = var27;
      this.m01 = var35;
      this.m02 = var19;
      this.m03 = 0.0D;
      this.m10 = var29;
      this.m11 = var37;
      this.m12 = var21;
      this.m13 = 0.0D;
      this.m20 = var31;
      this.m21 = var39;
      this.m22 = var23;
      this.m23 = 0.0D;
      this.m30 = -(var27 * var1 + var29 * var3 + var31 * var5);
      this.m31 = -(var35 * var1 + var37 * var3 + var39 * var5);
      this.m32 = -(var19 * var1 + var21 * var3 + var23 * var5);
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d lookAt(Vector3dc var1, Vector3dc var2, Vector3dc var3, Matrix4d var4) {
      return this.lookAt(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4);
   }

   public Matrix4d lookAt(Vector3dc var1, Vector3dc var2, Vector3dc var3) {
      return this.lookAt(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), this);
   }

   public Matrix4d lookAt(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19) {
      if ((this.properties & 4) != 0) {
         return var19.setLookAt(var1, var3, var5, var7, var9, var11, var13, var15, var17);
      } else {
         return (this.properties & 1) != 0 ? this.lookAtPerspective(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19) : this.lookAtGeneric(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19);
      }
   }

   private Matrix4d lookAtGeneric(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19) {
      double var20 = var1 - var7;
      double var22 = var3 - var9;
      double var24 = var5 - var11;
      double var26 = 1.0D / Math.sqrt(var20 * var20 + var22 * var22 + var24 * var24);
      var20 *= var26;
      var22 *= var26;
      var24 *= var26;
      double var28 = var15 * var24 - var17 * var22;
      double var30 = var17 * var20 - var13 * var24;
      double var32 = var13 * var22 - var15 * var20;
      double var34 = 1.0D / Math.sqrt(var28 * var28 + var30 * var30 + var32 * var32);
      var28 *= var34;
      var30 *= var34;
      var32 *= var34;
      double var36 = var22 * var32 - var24 * var30;
      double var38 = var24 * var28 - var20 * var32;
      double var40 = var20 * var30 - var22 * var28;
      double var60 = -(var28 * var1 + var30 * var3 + var32 * var5);
      double var62 = -(var36 * var1 + var38 * var3 + var40 * var5);
      double var64 = -(var20 * var1 + var22 * var3 + var24 * var5);
      var19.m30 = this.m00 * var60 + this.m10 * var62 + this.m20 * var64 + this.m30;
      var19.m31 = this.m01 * var60 + this.m11 * var62 + this.m21 * var64 + this.m31;
      var19.m32 = this.m02 * var60 + this.m12 * var62 + this.m22 * var64 + this.m32;
      var19.m33 = this.m03 * var60 + this.m13 * var62 + this.m23 * var64 + this.m33;
      double var66 = this.m00 * var28 + this.m10 * var36 + this.m20 * var20;
      double var68 = this.m01 * var28 + this.m11 * var36 + this.m21 * var20;
      double var70 = this.m02 * var28 + this.m12 * var36 + this.m22 * var20;
      double var72 = this.m03 * var28 + this.m13 * var36 + this.m23 * var20;
      double var74 = this.m00 * var30 + this.m10 * var38 + this.m20 * var22;
      double var76 = this.m01 * var30 + this.m11 * var38 + this.m21 * var22;
      double var78 = this.m02 * var30 + this.m12 * var38 + this.m22 * var22;
      double var80 = this.m03 * var30 + this.m13 * var38 + this.m23 * var22;
      var19.m20 = this.m00 * var32 + this.m10 * var40 + this.m20 * var24;
      var19.m21 = this.m01 * var32 + this.m11 * var40 + this.m21 * var24;
      var19.m22 = this.m02 * var32 + this.m12 * var40 + this.m22 * var24;
      var19.m23 = this.m03 * var32 + this.m13 * var40 + this.m23 * var24;
      var19.m00 = var66;
      var19.m01 = var68;
      var19.m02 = var70;
      var19.m03 = var72;
      var19.m10 = var74;
      var19.m11 = var76;
      var19.m12 = var78;
      var19.m13 = var80;
      var19.properties = (byte)(this.properties & -14);
      return var19;
   }

   public Matrix4d lookAt(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17) {
      return this.lookAt(var1, var3, var5, var7, var9, var11, var13, var15, var17, this);
   }

   public Matrix4d lookAtPerspective(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19) {
      double var20 = var1 - var7;
      double var22 = var3 - var9;
      double var24 = var5 - var11;
      double var26 = 1.0D / Math.sqrt(var20 * var20 + var22 * var22 + var24 * var24);
      var20 *= var26;
      var22 *= var26;
      var24 *= var26;
      double var28 = var15 * var24 - var17 * var22;
      double var30 = var17 * var20 - var13 * var24;
      double var32 = var13 * var22 - var15 * var20;
      double var34 = 1.0D / Math.sqrt(var28 * var28 + var30 * var30 + var32 * var32);
      var28 *= var34;
      var30 *= var34;
      var32 *= var34;
      double var36 = var22 * var32 - var24 * var30;
      double var38 = var24 * var28 - var20 * var32;
      double var40 = var20 * var30 - var22 * var28;
      double var60 = -(var28 * var1 + var30 * var3 + var32 * var5);
      double var62 = -(var36 * var1 + var38 * var3 + var40 * var5);
      double var64 = -(var20 * var1 + var22 * var3 + var24 * var5);
      double var66 = this.m00 * var28;
      double var68 = this.m11 * var36;
      double var70 = this.m22 * var20;
      double var72 = this.m23 * var20;
      double var74 = this.m00 * var30;
      double var76 = this.m11 * var38;
      double var78 = this.m22 * var22;
      double var80 = this.m23 * var22;
      double var82 = this.m00 * var32;
      double var84 = this.m11 * var40;
      double var86 = this.m22 * var24;
      double var88 = this.m23 * var24;
      double var90 = this.m00 * var60;
      double var92 = this.m11 * var62;
      double var94 = this.m22 * var64 + this.m32;
      double var96 = this.m23 * var64;
      var19.m00 = var66;
      var19.m01 = var68;
      var19.m02 = var70;
      var19.m03 = var72;
      var19.m10 = var74;
      var19.m11 = var76;
      var19.m12 = var78;
      var19.m13 = var80;
      var19.m20 = var82;
      var19.m21 = var84;
      var19.m22 = var86;
      var19.m23 = var88;
      var19.m30 = var90;
      var19.m31 = var92;
      var19.m32 = var94;
      var19.m33 = var96;
      var19.properties = 0;
      return var19;
   }

   public Matrix4d setLookAtLH(Vector3dc var1, Vector3dc var2, Vector3dc var3) {
      return this.setLookAtLH(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z());
   }

   public Matrix4d setLookAtLH(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17) {
      double var19 = var7 - var1;
      double var21 = var9 - var3;
      double var23 = var11 - var5;
      double var25 = 1.0D / Math.sqrt(var19 * var19 + var21 * var21 + var23 * var23);
      var19 *= var25;
      var21 *= var25;
      var23 *= var25;
      double var27 = var15 * var23 - var17 * var21;
      double var29 = var17 * var19 - var13 * var23;
      double var31 = var13 * var21 - var15 * var19;
      double var33 = 1.0D / Math.sqrt(var27 * var27 + var29 * var29 + var31 * var31);
      var27 *= var33;
      var29 *= var33;
      var31 *= var33;
      double var35 = var21 * var31 - var23 * var29;
      double var37 = var23 * var27 - var19 * var31;
      double var39 = var19 * var29 - var21 * var27;
      this.m00 = var27;
      this.m01 = var35;
      this.m02 = var19;
      this.m03 = 0.0D;
      this.m10 = var29;
      this.m11 = var37;
      this.m12 = var21;
      this.m13 = 0.0D;
      this.m20 = var31;
      this.m21 = var39;
      this.m22 = var23;
      this.m23 = 0.0D;
      this.m30 = -(var27 * var1 + var29 * var3 + var31 * var5);
      this.m31 = -(var35 * var1 + var37 * var3 + var39 * var5);
      this.m32 = -(var19 * var1 + var21 * var3 + var23 * var5);
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d lookAtLH(Vector3dc var1, Vector3dc var2, Vector3dc var3, Matrix4d var4) {
      return this.lookAtLH(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), var4);
   }

   public Matrix4d lookAtLH(Vector3dc var1, Vector3dc var2, Vector3dc var3) {
      return this.lookAtLH(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z(), this);
   }

   public Matrix4d lookAtLH(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19) {
      if ((this.properties & 4) != 0) {
         return var19.setLookAtLH(var1, var3, var5, var7, var9, var11, var13, var15, var17);
      } else {
         return (this.properties & 1) != 0 ? this.lookAtPerspectiveLH(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19) : this.lookAtLHGeneric(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19);
      }
   }

   private Matrix4d lookAtLHGeneric(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19) {
      double var20 = var7 - var1;
      double var22 = var9 - var3;
      double var24 = var11 - var5;
      double var26 = 1.0D / Math.sqrt(var20 * var20 + var22 * var22 + var24 * var24);
      var20 *= var26;
      var22 *= var26;
      var24 *= var26;
      double var28 = var15 * var24 - var17 * var22;
      double var30 = var17 * var20 - var13 * var24;
      double var32 = var13 * var22 - var15 * var20;
      double var34 = 1.0D / Math.sqrt(var28 * var28 + var30 * var30 + var32 * var32);
      var28 *= var34;
      var30 *= var34;
      var32 *= var34;
      double var36 = var22 * var32 - var24 * var30;
      double var38 = var24 * var28 - var20 * var32;
      double var40 = var20 * var30 - var22 * var28;
      double var60 = -(var28 * var1 + var30 * var3 + var32 * var5);
      double var62 = -(var36 * var1 + var38 * var3 + var40 * var5);
      double var64 = -(var20 * var1 + var22 * var3 + var24 * var5);
      var19.m30 = this.m00 * var60 + this.m10 * var62 + this.m20 * var64 + this.m30;
      var19.m31 = this.m01 * var60 + this.m11 * var62 + this.m21 * var64 + this.m31;
      var19.m32 = this.m02 * var60 + this.m12 * var62 + this.m22 * var64 + this.m32;
      var19.m33 = this.m03 * var60 + this.m13 * var62 + this.m23 * var64 + this.m33;
      double var66 = this.m00 * var28 + this.m10 * var36 + this.m20 * var20;
      double var68 = this.m01 * var28 + this.m11 * var36 + this.m21 * var20;
      double var70 = this.m02 * var28 + this.m12 * var36 + this.m22 * var20;
      double var72 = this.m03 * var28 + this.m13 * var36 + this.m23 * var20;
      double var74 = this.m00 * var30 + this.m10 * var38 + this.m20 * var22;
      double var76 = this.m01 * var30 + this.m11 * var38 + this.m21 * var22;
      double var78 = this.m02 * var30 + this.m12 * var38 + this.m22 * var22;
      double var80 = this.m03 * var30 + this.m13 * var38 + this.m23 * var22;
      var19.m20 = this.m00 * var32 + this.m10 * var40 + this.m20 * var24;
      var19.m21 = this.m01 * var32 + this.m11 * var40 + this.m21 * var24;
      var19.m22 = this.m02 * var32 + this.m12 * var40 + this.m22 * var24;
      var19.m23 = this.m03 * var32 + this.m13 * var40 + this.m23 * var24;
      var19.m00 = var66;
      var19.m01 = var68;
      var19.m02 = var70;
      var19.m03 = var72;
      var19.m10 = var74;
      var19.m11 = var76;
      var19.m12 = var78;
      var19.m13 = var80;
      var19.properties = (byte)(this.properties & -14);
      return var19;
   }

   public Matrix4d lookAtLH(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17) {
      return this.lookAtLH(var1, var3, var5, var7, var9, var11, var13, var15, var17, this);
   }

   public Matrix4d lookAtPerspectiveLH(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19) {
      double var20 = var7 - var1;
      double var22 = var9 - var3;
      double var24 = var11 - var5;
      double var26 = 1.0D / Math.sqrt(var20 * var20 + var22 * var22 + var24 * var24);
      var20 *= var26;
      var22 *= var26;
      var24 *= var26;
      double var28 = var15 * var24 - var17 * var22;
      double var30 = var17 * var20 - var13 * var24;
      double var32 = var13 * var22 - var15 * var20;
      double var34 = 1.0D / Math.sqrt(var28 * var28 + var30 * var30 + var32 * var32);
      var28 *= var34;
      var30 *= var34;
      var32 *= var34;
      double var36 = var22 * var32 - var24 * var30;
      double var38 = var24 * var28 - var20 * var32;
      double var40 = var20 * var30 - var22 * var28;
      double var60 = -(var28 * var1 + var30 * var3 + var32 * var5);
      double var62 = -(var36 * var1 + var38 * var3 + var40 * var5);
      double var64 = -(var20 * var1 + var22 * var3 + var24 * var5);
      double var66 = this.m00 * var28;
      double var68 = this.m11 * var36;
      double var70 = this.m22 * var20;
      double var72 = this.m23 * var20;
      double var74 = this.m00 * var30;
      double var76 = this.m11 * var38;
      double var78 = this.m22 * var22;
      double var80 = this.m23 * var22;
      double var82 = this.m00 * var32;
      double var84 = this.m11 * var40;
      double var86 = this.m22 * var24;
      double var88 = this.m23 * var24;
      double var90 = this.m00 * var60;
      double var92 = this.m11 * var62;
      double var94 = this.m22 * var64 + this.m32;
      double var96 = this.m23 * var64;
      var19.m00 = var66;
      var19.m01 = var68;
      var19.m02 = var70;
      var19.m03 = var72;
      var19.m10 = var74;
      var19.m11 = var76;
      var19.m12 = var78;
      var19.m13 = var80;
      var19.m20 = var82;
      var19.m21 = var84;
      var19.m22 = var86;
      var19.m23 = var88;
      var19.m30 = var90;
      var19.m31 = var92;
      var19.m32 = var94;
      var19.m33 = var96;
      var19.properties = 0;
      return var19;
   }

   public Matrix4d perspective(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10) {
      return (this.properties & 4) != 0 ? var10.setPerspective(var1, var3, var5, var7, var9) : this.perspectiveGeneric(var1, var3, var5, var7, var9, var10);
   }

   private Matrix4d perspectiveGeneric(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10) {
      double var11 = Math.tan(var1 * 0.5D);
      double var13 = 1.0D / (var11 * var3);
      double var15 = 1.0D / var11;
      boolean var21 = var7 > 0.0D && Double.isInfinite(var7);
      boolean var22 = var5 > 0.0D && Double.isInfinite(var5);
      double var17;
      double var19;
      double var23;
      if (var21) {
         var23 = 1.0E-6D;
         var17 = var23 - 1.0D;
         var19 = (var23 - (var9 ? 1.0D : 2.0D)) * var5;
      } else if (var22) {
         var23 = 1.0E-6D;
         var17 = (var9 ? 0.0D : 1.0D) - var23;
         var19 = ((var9 ? 1.0D : 2.0D) - var23) * var7;
      } else {
         var17 = (var9 ? var7 : var7 + var5) / (var5 - var7);
         var19 = (var9 ? var7 : var7 + var7) * var5 / (var5 - var7);
      }

      var23 = this.m20 * var17 - this.m30;
      double var25 = this.m21 * var17 - this.m31;
      double var27 = this.m22 * var17 - this.m32;
      double var29 = this.m23 * var17 - this.m33;
      var10.m00 = this.m00 * var13;
      var10.m01 = this.m01 * var13;
      var10.m02 = this.m02 * var13;
      var10.m03 = this.m03 * var13;
      var10.m10 = this.m10 * var15;
      var10.m11 = this.m11 * var15;
      var10.m12 = this.m12 * var15;
      var10.m13 = this.m13 * var15;
      var10.m30 = this.m20 * var19;
      var10.m31 = this.m21 * var19;
      var10.m32 = this.m22 * var19;
      var10.m33 = this.m23 * var19;
      var10.m20 = var23;
      var10.m21 = var25;
      var10.m22 = var27;
      var10.m23 = var29;
      var10.properties = (byte)(this.properties & -15);
      return var10;
   }

   public Matrix4d perspective(double var1, double var3, double var5, double var7, Matrix4d var9) {
      return this.perspective(var1, var3, var5, var7, false, var9);
   }

   public Matrix4d perspective(double var1, double var3, double var5, double var7, boolean var9) {
      return this.perspective(var1, var3, var5, var7, var9, this);
   }

   public Matrix4d perspective(double var1, double var3, double var5, double var7) {
      return this.perspective(var1, var3, var5, var7, this);
   }

   public Matrix4d setPerspective(double var1, double var3, double var5, double var7, boolean var9) {
      double var10 = Math.tan(var1 * 0.5D);
      this.m00 = 1.0D / (var10 * var3);
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 1.0D / var10;
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      boolean var12 = var7 > 0.0D && Double.isInfinite(var7);
      boolean var13 = var5 > 0.0D && Double.isInfinite(var5);
      double var14;
      if (var12) {
         var14 = 1.0E-6D;
         this.m22 = var14 - 1.0D;
         this.m32 = (var14 - (var9 ? 1.0D : 2.0D)) * var5;
      } else if (var13) {
         var14 = 1.0E-6D;
         this.m22 = (var9 ? 0.0D : 1.0D) - var14;
         this.m32 = ((var9 ? 1.0D : 2.0D) - var14) * var7;
      } else {
         this.m22 = (var9 ? var7 : var7 + var5) / (var5 - var7);
         this.m32 = (var9 ? var7 : var7 + var7) * var5 / (var5 - var7);
      }

      this.m23 = -1.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m33 = 0.0D;
      this.properties = 1;
      return this;
   }

   public Matrix4d setPerspective(double var1, double var3, double var5, double var7) {
      return this.setPerspective(var1, var3, var5, var7, false);
   }

   public Matrix4d perspectiveLH(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10) {
      return (this.properties & 4) != 0 ? var10.setPerspectiveLH(var1, var3, var5, var7, var9) : this.perspectiveLHGeneric(var1, var3, var5, var7, var9, var10);
   }

   private Matrix4d perspectiveLHGeneric(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10) {
      double var11 = Math.tan(var1 * 0.5D);
      double var13 = 1.0D / (var11 * var3);
      double var15 = 1.0D / var11;
      boolean var21 = var7 > 0.0D && Double.isInfinite(var7);
      boolean var22 = var5 > 0.0D && Double.isInfinite(var5);
      double var17;
      double var19;
      double var23;
      if (var21) {
         var23 = 1.0E-6D;
         var17 = 1.0D - var23;
         var19 = (var23 - (var9 ? 1.0D : 2.0D)) * var5;
      } else if (var22) {
         var23 = 1.0E-6D;
         var17 = (var9 ? 0.0D : 1.0D) - var23;
         var19 = ((var9 ? 1.0D : 2.0D) - var23) * var7;
      } else {
         var17 = (var9 ? var7 : var7 + var5) / (var7 - var5);
         var19 = (var9 ? var7 : var7 + var7) * var5 / (var5 - var7);
      }

      var23 = this.m20 * var17 + this.m30;
      double var25 = this.m21 * var17 + this.m31;
      double var27 = this.m22 * var17 + this.m32;
      double var29 = this.m23 * var17 + this.m33;
      var10.m00 = this.m00 * var13;
      var10.m01 = this.m01 * var13;
      var10.m02 = this.m02 * var13;
      var10.m03 = this.m03 * var13;
      var10.m10 = this.m10 * var15;
      var10.m11 = this.m11 * var15;
      var10.m12 = this.m12 * var15;
      var10.m13 = this.m13 * var15;
      var10.m30 = this.m20 * var19;
      var10.m31 = this.m21 * var19;
      var10.m32 = this.m22 * var19;
      var10.m33 = this.m23 * var19;
      var10.m20 = var23;
      var10.m21 = var25;
      var10.m22 = var27;
      var10.m23 = var29;
      var10.properties = (byte)(this.properties & -15);
      return var10;
   }

   public Matrix4d perspectiveLH(double var1, double var3, double var5, double var7, boolean var9) {
      return this.perspectiveLH(var1, var3, var5, var7, var9, this);
   }

   public Matrix4d perspectiveLH(double var1, double var3, double var5, double var7, Matrix4d var9) {
      return this.perspectiveLH(var1, var3, var5, var7, false, var9);
   }

   public Matrix4d perspectiveLH(double var1, double var3, double var5, double var7) {
      return this.perspectiveLH(var1, var3, var5, var7, this);
   }

   public Matrix4d setPerspectiveLH(double var1, double var3, double var5, double var7, boolean var9) {
      double var10 = Math.tan(var1 * 0.5D);
      this.m00 = 1.0D / (var10 * var3);
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 1.0D / var10;
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      boolean var12 = var7 > 0.0D && Double.isInfinite(var7);
      boolean var13 = var5 > 0.0D && Double.isInfinite(var5);
      double var14;
      if (var12) {
         var14 = 1.0E-6D;
         this.m22 = 1.0D - var14;
         this.m32 = (var14 - (var9 ? 1.0D : 2.0D)) * var5;
      } else if (var13) {
         var14 = 1.0E-6D;
         this.m22 = (var9 ? 0.0D : 1.0D) - var14;
         this.m32 = ((var9 ? 1.0D : 2.0D) - var14) * var7;
      } else {
         this.m22 = (var9 ? var7 : var7 + var5) / (var7 - var5);
         this.m32 = (var9 ? var7 : var7 + var7) * var5 / (var5 - var7);
      }

      this.m23 = 1.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m33 = 0.0D;
      this.properties = 1;
      return this;
   }

   public Matrix4d setPerspectiveLH(double var1, double var3, double var5, double var7) {
      return this.setPerspectiveLH(var1, var3, var5, var7, false);
   }

   public Matrix4d frustum(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14) {
      double var15 = (var9 + var9) / (var3 - var1);
      double var17 = (var9 + var9) / (var7 - var5);
      double var19 = (var3 + var1) / (var3 - var1);
      double var21 = (var7 + var5) / (var7 - var5);
      boolean var27 = var11 > 0.0D && Double.isInfinite(var11);
      boolean var28 = var9 > 0.0D && Double.isInfinite(var9);
      double var23;
      double var25;
      double var29;
      if (var27) {
         var29 = 1.0E-6D;
         var23 = var29 - 1.0D;
         var25 = (var29 - (var13 ? 1.0D : 2.0D)) * var9;
      } else if (var28) {
         var29 = 1.0E-6D;
         var23 = (var13 ? 0.0D : 1.0D) - var29;
         var25 = ((var13 ? 1.0D : 2.0D) - var29) * var11;
      } else {
         var23 = (var13 ? var11 : var11 + var9) / (var9 - var11);
         var25 = (var13 ? var11 : var11 + var11) * var9 / (var9 - var11);
      }

      var29 = this.m00 * var19 + this.m10 * var21 + this.m20 * var23 - this.m30;
      double var31 = this.m01 * var19 + this.m11 * var21 + this.m21 * var23 - this.m31;
      double var33 = this.m02 * var19 + this.m12 * var21 + this.m22 * var23 - this.m32;
      double var35 = this.m03 * var19 + this.m13 * var21 + this.m23 * var23 - this.m33;
      var14.m00 = this.m00 * var15;
      var14.m01 = this.m01 * var15;
      var14.m02 = this.m02 * var15;
      var14.m03 = this.m03 * var15;
      var14.m10 = this.m10 * var17;
      var14.m11 = this.m11 * var17;
      var14.m12 = this.m12 * var17;
      var14.m13 = this.m13 * var17;
      var14.m30 = this.m20 * var25;
      var14.m31 = this.m21 * var25;
      var14.m32 = this.m22 * var25;
      var14.m33 = this.m23 * var25;
      var14.m20 = var29;
      var14.m21 = var31;
      var14.m22 = var33;
      var14.m23 = var35;
      var14.m30 = this.m30;
      var14.m31 = this.m31;
      var14.m32 = this.m32;
      var14.m33 = this.m33;
      var14.properties = 0;
      return var14;
   }

   public Matrix4d frustum(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
      return this.frustum(var1, var3, var5, var7, var9, var11, false, var13);
   }

   public Matrix4d frustum(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13) {
      return this.frustum(var1, var3, var5, var7, var9, var11, var13, this);
   }

   public Matrix4d frustum(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.frustum(var1, var3, var5, var7, var9, var11, this);
   }

   public Matrix4d setFrustum(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13) {
      this.m00 = (var9 + var9) / (var3 - var1);
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = (var9 + var9) / (var7 - var5);
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = (var3 + var1) / (var3 - var1);
      this.m21 = (var7 + var5) / (var7 - var5);
      boolean var14 = var11 > 0.0D && Double.isInfinite(var11);
      boolean var15 = var9 > 0.0D && Double.isInfinite(var9);
      double var16;
      if (var14) {
         var16 = 1.0E-6D;
         this.m22 = var16 - 1.0D;
         this.m32 = (var16 - (var13 ? 1.0D : 2.0D)) * var9;
      } else if (var15) {
         var16 = 1.0E-6D;
         this.m22 = (var13 ? 0.0D : 1.0D) - var16;
         this.m32 = ((var13 ? 1.0D : 2.0D) - var16) * var11;
      } else {
         this.m22 = (var13 ? var11 : var11 + var9) / (var9 - var11);
         this.m32 = (var13 ? var11 : var11 + var11) * var9 / (var9 - var11);
      }

      this.m23 = -1.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m33 = 0.0D;
      this.properties = 0;
      return this;
   }

   public Matrix4d setFrustum(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.setFrustum(var1, var3, var5, var7, var9, var11, false);
   }

   public Matrix4d frustumLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14) {
      double var15 = (var9 + var9) / (var3 - var1);
      double var17 = (var9 + var9) / (var7 - var5);
      double var19 = (var3 + var1) / (var3 - var1);
      double var21 = (var7 + var5) / (var7 - var5);
      boolean var27 = var11 > 0.0D && Double.isInfinite(var11);
      boolean var28 = var9 > 0.0D && Double.isInfinite(var9);
      double var23;
      double var25;
      double var29;
      if (var27) {
         var29 = 1.0E-6D;
         var23 = 1.0D - var29;
         var25 = (var29 - (var13 ? 1.0D : 2.0D)) * var9;
      } else if (var28) {
         var29 = 1.0E-6D;
         var23 = (var13 ? 0.0D : 1.0D) - var29;
         var25 = ((var13 ? 1.0D : 2.0D) - var29) * var11;
      } else {
         var23 = (var13 ? var11 : var11 + var9) / (var11 - var9);
         var25 = (var13 ? var11 : var11 + var11) * var9 / (var9 - var11);
      }

      var29 = this.m00 * var19 + this.m10 * var21 + this.m20 * var23 + this.m30;
      double var31 = this.m01 * var19 + this.m11 * var21 + this.m21 * var23 + this.m31;
      double var33 = this.m02 * var19 + this.m12 * var21 + this.m22 * var23 + this.m32;
      double var35 = this.m03 * var19 + this.m13 * var21 + this.m23 * var23 + this.m33;
      var14.m00 = this.m00 * var15;
      var14.m01 = this.m01 * var15;
      var14.m02 = this.m02 * var15;
      var14.m03 = this.m03 * var15;
      var14.m10 = this.m10 * var17;
      var14.m11 = this.m11 * var17;
      var14.m12 = this.m12 * var17;
      var14.m13 = this.m13 * var17;
      var14.m30 = this.m20 * var25;
      var14.m31 = this.m21 * var25;
      var14.m32 = this.m22 * var25;
      var14.m33 = this.m23 * var25;
      var14.m20 = var29;
      var14.m21 = var31;
      var14.m22 = var33;
      var14.m23 = var35;
      var14.m30 = this.m30;
      var14.m31 = this.m31;
      var14.m32 = this.m32;
      var14.m33 = this.m33;
      var14.properties = 0;
      return var14;
   }

   public Matrix4d frustumLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13) {
      return this.frustumLH(var1, var3, var5, var7, var9, var11, var13, this);
   }

   public Matrix4d frustumLH(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
      return this.frustumLH(var1, var3, var5, var7, var9, var11, false, var13);
   }

   public Matrix4d frustumLH(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.frustumLH(var1, var3, var5, var7, var9, var11, this);
   }

   public Matrix4d setFrustumLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13) {
      this.m00 = (var9 + var9) / (var3 - var1);
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m03 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = (var9 + var9) / (var7 - var5);
      this.m12 = 0.0D;
      this.m13 = 0.0D;
      this.m20 = (var3 + var1) / (var3 - var1);
      this.m21 = (var7 + var5) / (var7 - var5);
      boolean var14 = var11 > 0.0D && Double.isInfinite(var11);
      boolean var15 = var9 > 0.0D && Double.isInfinite(var9);
      double var16;
      if (var14) {
         var16 = 1.0E-6D;
         this.m22 = 1.0D - var16;
         this.m32 = (var16 - (var13 ? 1.0D : 2.0D)) * var9;
      } else if (var15) {
         var16 = 1.0E-6D;
         this.m22 = (var13 ? 0.0D : 1.0D) - var16;
         this.m32 = ((var13 ? 1.0D : 2.0D) - var16) * var11;
      } else {
         this.m22 = (var13 ? var11 : var11 + var9) / (var11 - var9);
         this.m32 = (var13 ? var11 : var11 + var11) * var9 / (var9 - var11);
      }

      this.m23 = 1.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m33 = 0.0D;
      this.properties = 0;
      return this;
   }

   public Matrix4d setFrustumLH(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.setFrustumLH(var1, var3, var5, var7, var9, var11, false);
   }

   public Vector4d frustumPlane(int var1, Vector4d var2) {
      switch(var1) {
      case 0:
         var2.set(this.m03 + this.m00, this.m13 + this.m10, this.m23 + this.m20, this.m33 + this.m30).normalize3();
         break;
      case 1:
         var2.set(this.m03 - this.m00, this.m13 - this.m10, this.m23 - this.m20, this.m33 - this.m30).normalize3();
         break;
      case 2:
         var2.set(this.m03 + this.m01, this.m13 + this.m11, this.m23 + this.m21, this.m33 + this.m31).normalize3();
         break;
      case 3:
         var2.set(this.m03 - this.m01, this.m13 - this.m11, this.m23 - this.m21, this.m33 - this.m31).normalize3();
         break;
      case 4:
         var2.set(this.m03 + this.m02, this.m13 + this.m12, this.m23 + this.m22, this.m33 + this.m32).normalize3();
         break;
      case 5:
         var2.set(this.m03 - this.m02, this.m13 - this.m12, this.m23 - this.m22, this.m33 - this.m32).normalize3();
         break;
      default:
         throw new IllegalArgumentException("plane");
      }

      return var2;
   }

   public Vector3d frustumCorner(int var1, Vector3d var2) {
      double var3;
      double var5;
      double var7;
      double var9;
      double var11;
      double var13;
      double var15;
      double var17;
      double var19;
      double var21;
      double var23;
      double var25;
      switch(var1) {
      case 0:
         var9 = this.m03 + this.m00;
         var11 = this.m13 + this.m10;
         var13 = this.m23 + this.m20;
         var3 = this.m33 + this.m30;
         var15 = this.m03 + this.m01;
         var17 = this.m13 + this.m11;
         var19 = this.m23 + this.m21;
         var5 = this.m33 + this.m31;
         var21 = this.m03 + this.m02;
         var23 = this.m13 + this.m12;
         var25 = this.m23 + this.m22;
         var7 = this.m33 + this.m32;
         break;
      case 1:
         var9 = this.m03 - this.m00;
         var11 = this.m13 - this.m10;
         var13 = this.m23 - this.m20;
         var3 = this.m33 - this.m30;
         var15 = this.m03 + this.m01;
         var17 = this.m13 + this.m11;
         var19 = this.m23 + this.m21;
         var5 = this.m33 + this.m31;
         var21 = this.m03 + this.m02;
         var23 = this.m13 + this.m12;
         var25 = this.m23 + this.m22;
         var7 = this.m33 + this.m32;
         break;
      case 2:
         var9 = this.m03 - this.m00;
         var11 = this.m13 - this.m10;
         var13 = this.m23 - this.m20;
         var3 = this.m33 - this.m30;
         var15 = this.m03 - this.m01;
         var17 = this.m13 - this.m11;
         var19 = this.m23 - this.m21;
         var5 = this.m33 - this.m31;
         var21 = this.m03 + this.m02;
         var23 = this.m13 + this.m12;
         var25 = this.m23 + this.m22;
         var7 = this.m33 + this.m32;
         break;
      case 3:
         var9 = this.m03 + this.m00;
         var11 = this.m13 + this.m10;
         var13 = this.m23 + this.m20;
         var3 = this.m33 + this.m30;
         var15 = this.m03 - this.m01;
         var17 = this.m13 - this.m11;
         var19 = this.m23 - this.m21;
         var5 = this.m33 - this.m31;
         var21 = this.m03 + this.m02;
         var23 = this.m13 + this.m12;
         var25 = this.m23 + this.m22;
         var7 = this.m33 + this.m32;
         break;
      case 4:
         var9 = this.m03 - this.m00;
         var11 = this.m13 - this.m10;
         var13 = this.m23 - this.m20;
         var3 = this.m33 - this.m30;
         var15 = this.m03 + this.m01;
         var17 = this.m13 + this.m11;
         var19 = this.m23 + this.m21;
         var5 = this.m33 + this.m31;
         var21 = this.m03 - this.m02;
         var23 = this.m13 - this.m12;
         var25 = this.m23 - this.m22;
         var7 = this.m33 - this.m32;
         break;
      case 5:
         var9 = this.m03 + this.m00;
         var11 = this.m13 + this.m10;
         var13 = this.m23 + this.m20;
         var3 = this.m33 + this.m30;
         var15 = this.m03 + this.m01;
         var17 = this.m13 + this.m11;
         var19 = this.m23 + this.m21;
         var5 = this.m33 + this.m31;
         var21 = this.m03 - this.m02;
         var23 = this.m13 - this.m12;
         var25 = this.m23 - this.m22;
         var7 = this.m33 - this.m32;
         break;
      case 6:
         var9 = this.m03 + this.m00;
         var11 = this.m13 + this.m10;
         var13 = this.m23 + this.m20;
         var3 = this.m33 + this.m30;
         var15 = this.m03 - this.m01;
         var17 = this.m13 - this.m11;
         var19 = this.m23 - this.m21;
         var5 = this.m33 - this.m31;
         var21 = this.m03 - this.m02;
         var23 = this.m13 - this.m12;
         var25 = this.m23 - this.m22;
         var7 = this.m33 - this.m32;
         break;
      case 7:
         var9 = this.m03 - this.m00;
         var11 = this.m13 - this.m10;
         var13 = this.m23 - this.m20;
         var3 = this.m33 - this.m30;
         var15 = this.m03 - this.m01;
         var17 = this.m13 - this.m11;
         var19 = this.m23 - this.m21;
         var5 = this.m33 - this.m31;
         var21 = this.m03 - this.m02;
         var23 = this.m13 - this.m12;
         var25 = this.m23 - this.m22;
         var7 = this.m33 - this.m32;
         break;
      default:
         throw new IllegalArgumentException("corner");
      }

      double var27 = var17 * var25 - var19 * var23;
      double var29 = var19 * var21 - var15 * var25;
      double var31 = var15 * var23 - var17 * var21;
      double var33 = var23 * var13 - var25 * var11;
      double var35 = var25 * var9 - var21 * var13;
      double var37 = var21 * var11 - var23 * var9;
      double var39 = var11 * var19 - var13 * var17;
      double var41 = var13 * var15 - var9 * var19;
      double var43 = var9 * var17 - var11 * var15;
      double var45 = 1.0D / (var9 * var27 + var11 * var29 + var13 * var31);
      var2.x = (-var27 * var3 - var33 * var5 - var39 * var7) * var45;
      var2.y = (-var29 * var3 - var35 * var5 - var41 * var7) * var45;
      var2.z = (-var31 * var3 - var37 * var5 - var43 * var7) * var45;
      return var2;
   }

   public Vector3d perspectiveOrigin(Vector3d var1) {
      double var8 = this.m03 + this.m00;
      double var10 = this.m13 + this.m10;
      double var12 = this.m23 + this.m20;
      double var2 = this.m33 + this.m30;
      double var14 = this.m03 - this.m00;
      double var16 = this.m13 - this.m10;
      double var18 = this.m23 - this.m20;
      double var4 = this.m33 - this.m30;
      double var20 = this.m03 - this.m01;
      double var22 = this.m13 - this.m11;
      double var24 = this.m23 - this.m21;
      double var6 = this.m33 - this.m31;
      double var26 = var16 * var24 - var18 * var22;
      double var28 = var18 * var20 - var14 * var24;
      double var30 = var14 * var22 - var16 * var20;
      double var32 = var22 * var12 - var24 * var10;
      double var34 = var24 * var8 - var20 * var12;
      double var36 = var20 * var10 - var22 * var8;
      double var38 = var10 * var18 - var12 * var16;
      double var40 = var12 * var14 - var8 * var18;
      double var42 = var8 * var16 - var10 * var14;
      double var44 = 1.0D / (var8 * var26 + var10 * var28 + var12 * var30);
      var1.x = (-var26 * var2 - var32 * var4 - var38 * var6) * var44;
      var1.y = (-var28 * var2 - var34 * var4 - var40 * var6) * var44;
      var1.z = (-var30 * var2 - var36 * var4 - var42 * var6) * var44;
      return var1;
   }

   public double perspectiveFov() {
      double var1 = this.m03 + this.m01;
      double var3 = this.m13 + this.m11;
      double var5 = this.m23 + this.m21;
      double var7 = this.m01 - this.m03;
      double var9 = this.m11 - this.m13;
      double var11 = this.m21 - this.m23;
      double var13 = Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
      double var15 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
      return Math.acos((var1 * var7 + var3 * var9 + var5 * var11) / (var13 * var15));
   }

   public double perspectiveNear() {
      return this.m32 / (this.m23 + this.m22);
   }

   public double perspectiveFar() {
      return this.m32 / (this.m22 - this.m23);
   }

   public Vector3d frustumRayDir(double var1, double var3, Vector3d var5) {
      double var6 = this.m10 * this.m23;
      double var8 = this.m13 * this.m21;
      double var10 = this.m10 * this.m21;
      double var12 = this.m11 * this.m23;
      double var14 = this.m13 * this.m20;
      double var16 = this.m11 * this.m20;
      double var18 = this.m03 * this.m20;
      double var20 = this.m01 * this.m23;
      double var22 = this.m01 * this.m20;
      double var24 = this.m03 * this.m21;
      double var26 = this.m00 * this.m23;
      double var28 = this.m00 * this.m21;
      double var30 = this.m00 * this.m13;
      double var32 = this.m03 * this.m11;
      double var34 = this.m00 * this.m11;
      double var36 = this.m01 * this.m13;
      double var38 = this.m03 * this.m10;
      double var40 = this.m01 * this.m10;
      double var42 = (var12 + var14 + var16 - var6 - var8 - var10) * (1.0D - var3) + (var6 - var8 - var10 + var12 - var14 + var16) * var3;
      double var44 = (var24 + var26 + var28 - var18 - var20 - var22) * (1.0D - var3) + (var18 - var20 - var22 + var24 - var26 + var28) * var3;
      double var46 = (var36 + var38 + var40 - var30 - var32 - var34) * (1.0D - var3) + (var30 - var32 - var34 + var36 - var38 + var40) * var3;
      double var48 = (var8 - var10 - var12 + var14 + var16 - var6) * (1.0D - var3) + (var6 + var8 - var10 - var12 - var14 + var16) * var3;
      double var50 = (var20 - var22 - var24 + var26 + var28 - var18) * (1.0D - var3) + (var18 + var20 - var22 - var24 - var26 + var28) * var3;
      double var52 = (var32 - var34 - var36 + var38 + var40 - var30) * (1.0D - var3) + (var30 + var32 - var34 - var36 - var38 + var40) * var3;
      var5.x = var42 * (1.0D - var1) + var48 * var1;
      var5.y = var44 * (1.0D - var1) + var50 * var1;
      var5.z = var46 * (1.0D - var1) + var52 * var1;
      var5.normalize();
      return var5;
   }

   public Vector3d positiveZ(Vector3d var1) {
      var1.x = this.m10 * this.m21 - this.m11 * this.m20;
      var1.y = this.m20 * this.m01 - this.m21 * this.m00;
      var1.z = this.m00 * this.m11 - this.m01 * this.m10;
      var1.normalize();
      return var1;
   }

   public Vector3d normalizedPositiveZ(Vector3d var1) {
      var1.x = this.m02;
      var1.y = this.m12;
      var1.z = this.m22;
      return var1;
   }

   public Vector3d positiveX(Vector3d var1) {
      var1.x = this.m11 * this.m22 - this.m12 * this.m21;
      var1.y = this.m02 * this.m21 - this.m01 * this.m22;
      var1.z = this.m01 * this.m12 - this.m02 * this.m11;
      var1.normalize();
      return var1;
   }

   public Vector3d normalizedPositiveX(Vector3d var1) {
      var1.x = this.m00;
      var1.y = this.m10;
      var1.z = this.m20;
      return var1;
   }

   public Vector3d positiveY(Vector3d var1) {
      var1.x = this.m12 * this.m20 - this.m10 * this.m22;
      var1.y = this.m00 * this.m22 - this.m02 * this.m20;
      var1.z = this.m02 * this.m10 - this.m00 * this.m12;
      var1.normalize();
      return var1;
   }

   public Vector3d normalizedPositiveY(Vector3d var1) {
      var1.x = this.m01;
      var1.y = this.m11;
      var1.z = this.m21;
      return var1;
   }

   public Vector3d originAffine(Vector3d var1) {
      double var2 = this.m00 * this.m11 - this.m01 * this.m10;
      double var4 = this.m00 * this.m12 - this.m02 * this.m10;
      double var6 = this.m01 * this.m12 - this.m02 * this.m11;
      double var8 = this.m20 * this.m31 - this.m21 * this.m30;
      double var10 = this.m20 * this.m32 - this.m22 * this.m30;
      double var12 = this.m21 * this.m32 - this.m22 * this.m31;
      var1.x = -this.m10 * var12 + this.m11 * var10 - this.m12 * var8;
      var1.y = this.m00 * var12 - this.m01 * var10 + this.m02 * var8;
      var1.z = -this.m30 * var6 + this.m31 * var4 - this.m32 * var2;
      return var1;
   }

   public Vector3d origin(Vector3d var1) {
      double var2 = this.m00 * this.m11 - this.m01 * this.m10;
      double var4 = this.m00 * this.m12 - this.m02 * this.m10;
      double var6 = this.m00 * this.m13 - this.m03 * this.m10;
      double var8 = this.m01 * this.m12 - this.m02 * this.m11;
      double var10 = this.m01 * this.m13 - this.m03 * this.m11;
      double var12 = this.m02 * this.m13 - this.m03 * this.m12;
      double var14 = this.m20 * this.m31 - this.m21 * this.m30;
      double var16 = this.m20 * this.m32 - this.m22 * this.m30;
      double var18 = this.m20 * this.m33 - this.m23 * this.m30;
      double var20 = this.m21 * this.m32 - this.m22 * this.m31;
      double var22 = this.m21 * this.m33 - this.m23 * this.m31;
      double var24 = this.m22 * this.m33 - this.m23 * this.m32;
      double var26 = var2 * var24 - var4 * var22 + var6 * var20 + var8 * var18 - var10 * var16 + var12 * var14;
      double var28 = 1.0D / var26;
      double var30 = (-this.m10 * var20 + this.m11 * var16 - this.m12 * var14) * var28;
      double var32 = (this.m00 * var20 - this.m01 * var16 + this.m02 * var14) * var28;
      double var34 = (-this.m30 * var8 + this.m31 * var4 - this.m32 * var2) * var28;
      double var36 = var26 / (this.m20 * var8 - this.m21 * var4 + this.m22 * var2);
      double var38 = var30 * var36;
      double var40 = var32 * var36;
      double var42 = var34 * var36;
      return var1.set(var38, var40, var42);
   }

   public Matrix4d shadow(Vector4dc var1, double var2, double var4, double var6, double var8) {
      return this.shadow(var1.x(), var1.y(), var1.z(), var1.w(), var2, var4, var6, var8, this);
   }

   public Matrix4d shadow(Vector4dc var1, double var2, double var4, double var6, double var8, Matrix4d var10) {
      return this.shadow(var1.x(), var1.y(), var1.z(), var1.w(), var2, var4, var6, var8, var10);
   }

   public Matrix4d shadow(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15) {
      return this.shadow(var1, var3, var5, var7, var9, var11, var13, var15, this);
   }

   public Matrix4d shadow(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, Matrix4d var17) {
      double var18 = 1.0D / Math.sqrt(var9 * var9 + var11 * var11 + var13 * var13);
      double var20 = var9 * var18;
      double var22 = var11 * var18;
      double var24 = var13 * var18;
      double var26 = var15 * var18;
      double var28 = var20 * var1 + var22 * var3 + var24 * var5 + var26 * var7;
      double var30 = var28 - var20 * var1;
      double var32 = -var20 * var3;
      double var34 = -var20 * var5;
      double var36 = -var20 * var7;
      double var38 = -var22 * var1;
      double var40 = var28 - var22 * var3;
      double var42 = -var22 * var5;
      double var44 = -var22 * var7;
      double var46 = -var24 * var1;
      double var48 = -var24 * var3;
      double var50 = var28 - var24 * var5;
      double var52 = -var24 * var7;
      double var54 = -var26 * var1;
      double var56 = -var26 * var3;
      double var58 = -var26 * var5;
      double var60 = var28 - var26 * var7;
      double var62 = this.m00 * var30 + this.m10 * var32 + this.m20 * var34 + this.m30 * var36;
      double var64 = this.m01 * var30 + this.m11 * var32 + this.m21 * var34 + this.m31 * var36;
      double var66 = this.m02 * var30 + this.m12 * var32 + this.m22 * var34 + this.m32 * var36;
      double var68 = this.m03 * var30 + this.m13 * var32 + this.m23 * var34 + this.m33 * var36;
      double var70 = this.m00 * var38 + this.m10 * var40 + this.m20 * var42 + this.m30 * var44;
      double var72 = this.m01 * var38 + this.m11 * var40 + this.m21 * var42 + this.m31 * var44;
      double var74 = this.m02 * var38 + this.m12 * var40 + this.m22 * var42 + this.m32 * var44;
      double var76 = this.m03 * var38 + this.m13 * var40 + this.m23 * var42 + this.m33 * var44;
      double var78 = this.m00 * var46 + this.m10 * var48 + this.m20 * var50 + this.m30 * var52;
      double var80 = this.m01 * var46 + this.m11 * var48 + this.m21 * var50 + this.m31 * var52;
      double var82 = this.m02 * var46 + this.m12 * var48 + this.m22 * var50 + this.m32 * var52;
      double var84 = this.m03 * var46 + this.m13 * var48 + this.m23 * var50 + this.m33 * var52;
      var17.m30 = this.m00 * var54 + this.m10 * var56 + this.m20 * var58 + this.m30 * var60;
      var17.m31 = this.m01 * var54 + this.m11 * var56 + this.m21 * var58 + this.m31 * var60;
      var17.m32 = this.m02 * var54 + this.m12 * var56 + this.m22 * var58 + this.m32 * var60;
      var17.m33 = this.m03 * var54 + this.m13 * var56 + this.m23 * var58 + this.m33 * var60;
      var17.m00 = var62;
      var17.m01 = var64;
      var17.m02 = var66;
      var17.m03 = var68;
      var17.m10 = var70;
      var17.m11 = var72;
      var17.m12 = var74;
      var17.m13 = var76;
      var17.m20 = var78;
      var17.m21 = var80;
      var17.m22 = var82;
      var17.m23 = var84;
      var17.properties = (byte)(this.properties & -14);
      return var17;
   }

   public Matrix4d shadow(Vector4dc var1, Matrix4dc var2, Matrix4d var3) {
      double var4 = var2.m10();
      double var6 = var2.m11();
      double var8 = var2.m12();
      double var10 = -var4 * var2.m30() - var6 * var2.m31() - var8 * var2.m32();
      return this.shadow(var1.x(), var1.y(), var1.z(), var1.w(), var4, var6, var8, var10, var3);
   }

   public Matrix4d shadow(Vector4d var1, Matrix4d var2) {
      return this.shadow(var1, var2, this);
   }

   public Matrix4d shadow(double var1, double var3, double var5, double var7, Matrix4dc var9, Matrix4d var10) {
      double var11 = var9.m10();
      double var13 = var9.m11();
      double var15 = var9.m12();
      double var17 = -var11 * var9.m30() - var13 * var9.m31() - var15 * var9.m32();
      return this.shadow(var1, var3, var5, var7, var11, var13, var15, var17, var10);
   }

   public Matrix4d shadow(double var1, double var3, double var5, double var7, Matrix4dc var9) {
      return this.shadow(var1, var3, var5, var7, var9, this);
   }

   public Matrix4d billboardCylindrical(Vector3dc var1, Vector3dc var2, Vector3dc var3) {
      double var4 = var2.x() - var1.x();
      double var6 = var2.y() - var1.y();
      double var8 = var2.z() - var1.z();
      double var10 = var3.y() * var8 - var3.z() * var6;
      double var12 = var3.z() * var4 - var3.x() * var8;
      double var14 = var3.x() * var6 - var3.y() * var4;
      double var16 = 1.0D / Math.sqrt(var10 * var10 + var12 * var12 + var14 * var14);
      var10 *= var16;
      var12 *= var16;
      var14 *= var16;
      var4 = var12 * var3.z() - var14 * var3.y();
      var6 = var14 * var3.x() - var10 * var3.z();
      var8 = var10 * var3.y() - var12 * var3.x();
      double var18 = 1.0D / Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
      var4 *= var18;
      var6 *= var18;
      var8 *= var18;
      this.m00 = var10;
      this.m01 = var12;
      this.m02 = var14;
      this.m03 = 0.0D;
      this.m10 = var3.x();
      this.m11 = var3.y();
      this.m12 = var3.z();
      this.m13 = 0.0D;
      this.m20 = var4;
      this.m21 = var6;
      this.m22 = var8;
      this.m23 = 0.0D;
      this.m30 = var1.x();
      this.m31 = var1.y();
      this.m32 = var1.z();
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d billboardSpherical(Vector3dc var1, Vector3dc var2, Vector3dc var3) {
      double var4 = var2.x() - var1.x();
      double var6 = var2.y() - var1.y();
      double var8 = var2.z() - var1.z();
      double var10 = 1.0D / Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
      var4 *= var10;
      var6 *= var10;
      var8 *= var10;
      double var12 = var3.y() * var8 - var3.z() * var6;
      double var14 = var3.z() * var4 - var3.x() * var8;
      double var16 = var3.x() * var6 - var3.y() * var4;
      double var18 = 1.0D / Math.sqrt(var12 * var12 + var14 * var14 + var16 * var16);
      var12 *= var18;
      var14 *= var18;
      var16 *= var18;
      double var20 = var6 * var16 - var8 * var14;
      double var22 = var8 * var12 - var4 * var16;
      double var24 = var4 * var14 - var6 * var12;
      this.m00 = var12;
      this.m01 = var14;
      this.m02 = var16;
      this.m03 = 0.0D;
      this.m10 = var20;
      this.m11 = var22;
      this.m12 = var24;
      this.m13 = 0.0D;
      this.m20 = var4;
      this.m21 = var6;
      this.m22 = var8;
      this.m23 = 0.0D;
      this.m30 = var1.x();
      this.m31 = var1.y();
      this.m32 = var1.z();
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d billboardSpherical(Vector3dc var1, Vector3dc var2) {
      double var3 = var2.x() - var1.x();
      double var5 = var2.y() - var1.y();
      double var7 = var2.z() - var1.z();
      double var9 = -var5;
      double var13 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7) + var7;
      double var15 = 1.0D / Math.sqrt(var9 * var9 + var3 * var3 + var13 * var13);
      var9 *= var15;
      double var11 = var3 * var15;
      var13 *= var15;
      double var17 = (var9 + var9) * var9;
      double var19 = (var11 + var11) * var11;
      double var21 = (var9 + var9) * var11;
      double var23 = (var9 + var9) * var13;
      double var25 = (var11 + var11) * var13;
      this.m00 = 1.0D - var19;
      this.m01 = var21;
      this.m02 = -var25;
      this.m03 = 0.0D;
      this.m10 = var21;
      this.m11 = 1.0D - var17;
      this.m12 = var23;
      this.m13 = 0.0D;
      this.m20 = var25;
      this.m21 = -var23;
      this.m22 = 1.0D - var19 - var17;
      this.m23 = 0.0D;
      this.m30 = var1.x();
      this.m31 = var1.y();
      this.m32 = var1.z();
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public int hashCode() {
      byte var2 = 1;
      long var3 = Double.doubleToLongBits(this.m00);
      int var5 = 31 * var2 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m01);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m02);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m03);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m10);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m11);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m12);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m13);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m20);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m21);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m22);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m23);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m30);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m31);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m32);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m33);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      return var5;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof Matrix4d)) {
         return false;
      } else {
         Matrix4d var2 = (Matrix4d)var1;
         if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(var2.m00)) {
            return false;
         } else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(var2.m01)) {
            return false;
         } else if (Double.doubleToLongBits(this.m02) != Double.doubleToLongBits(var2.m02)) {
            return false;
         } else if (Double.doubleToLongBits(this.m03) != Double.doubleToLongBits(var2.m03)) {
            return false;
         } else if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(var2.m10)) {
            return false;
         } else if (Double.doubleToLongBits(this.m11) != Double.doubleToLongBits(var2.m11)) {
            return false;
         } else if (Double.doubleToLongBits(this.m12) != Double.doubleToLongBits(var2.m12)) {
            return false;
         } else if (Double.doubleToLongBits(this.m13) != Double.doubleToLongBits(var2.m13)) {
            return false;
         } else if (Double.doubleToLongBits(this.m20) != Double.doubleToLongBits(var2.m20)) {
            return false;
         } else if (Double.doubleToLongBits(this.m21) != Double.doubleToLongBits(var2.m21)) {
            return false;
         } else if (Double.doubleToLongBits(this.m22) != Double.doubleToLongBits(var2.m22)) {
            return false;
         } else if (Double.doubleToLongBits(this.m23) != Double.doubleToLongBits(var2.m23)) {
            return false;
         } else if (Double.doubleToLongBits(this.m30) != Double.doubleToLongBits(var2.m30)) {
            return false;
         } else if (Double.doubleToLongBits(this.m31) != Double.doubleToLongBits(var2.m31)) {
            return false;
         } else if (Double.doubleToLongBits(this.m32) != Double.doubleToLongBits(var2.m32)) {
            return false;
         } else {
            return Double.doubleToLongBits(this.m33) == Double.doubleToLongBits(var2.m33);
         }
      }
   }

   public Matrix4d pick(double var1, double var3, double var5, double var7, int[] var9, Matrix4d var10) {
      double var11 = (double)var9[2] / var5;
      double var13 = (double)var9[3] / var7;
      double var15 = ((double)var9[2] + 2.0D * ((double)var9[0] - var1)) / var5;
      double var17 = ((double)var9[3] + 2.0D * ((double)var9[1] - var3)) / var7;
      var10.m30 = this.m00 * var15 + this.m10 * var17 + this.m30;
      var10.m31 = this.m01 * var15 + this.m11 * var17 + this.m31;
      var10.m32 = this.m02 * var15 + this.m12 * var17 + this.m32;
      var10.m33 = this.m03 * var15 + this.m13 * var17 + this.m33;
      var10.m00 = this.m00 * var11;
      var10.m01 = this.m01 * var11;
      var10.m02 = this.m02 * var11;
      var10.m03 = this.m03 * var11;
      var10.m10 = this.m10 * var13;
      var10.m11 = this.m11 * var13;
      var10.m12 = this.m12 * var13;
      var10.m13 = this.m13 * var13;
      var10.properties = 0;
      return var10;
   }

   public Matrix4d pick(double var1, double var3, double var5, double var7, int[] var9) {
      return this.pick(var1, var3, var5, var7, var9, this);
   }

   public boolean isAffine() {
      return this.m03 == 0.0D && this.m13 == 0.0D && this.m23 == 0.0D && this.m33 == 1.0D;
   }

   public Matrix4d swap(Matrix4d var1) {
      double var2 = this.m00;
      this.m00 = var1.m00;
      var1.m00 = var2;
      var2 = this.m01;
      this.m01 = var1.m01;
      var1.m01 = var2;
      var2 = this.m02;
      this.m02 = var1.m02;
      var1.m02 = var2;
      var2 = this.m03;
      this.m03 = var1.m03;
      var1.m03 = var2;
      var2 = this.m10;
      this.m10 = var1.m10;
      var1.m10 = var2;
      var2 = this.m11;
      this.m11 = var1.m11;
      var1.m11 = var2;
      var2 = this.m12;
      this.m12 = var1.m12;
      var1.m12 = var2;
      var2 = this.m13;
      this.m13 = var1.m13;
      var1.m13 = var2;
      var2 = this.m20;
      this.m20 = var1.m20;
      var1.m20 = var2;
      var2 = this.m21;
      this.m21 = var1.m21;
      var1.m21 = var2;
      var2 = this.m22;
      this.m22 = var1.m22;
      var1.m22 = var2;
      var2 = this.m23;
      this.m23 = var1.m23;
      var1.m23 = var2;
      var2 = this.m30;
      this.m30 = var1.m30;
      var1.m30 = var2;
      var2 = this.m31;
      this.m31 = var1.m31;
      var1.m31 = var2;
      var2 = this.m32;
      this.m32 = var1.m32;
      var1.m32 = var2;
      var2 = this.m33;
      this.m33 = var1.m33;
      var1.m33 = var2;
      byte var4 = this.properties;
      this.properties = var1.properties;
      var1.properties = var4;
      return this;
   }

   public Matrix4d arcball(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
      double var14 = this.m20 * -var1 + this.m30;
      double var16 = this.m21 * -var1 + this.m31;
      double var18 = this.m22 * -var1 + this.m32;
      double var20 = this.m23 * -var1 + this.m33;
      double var22 = Math.cos(var9);
      double var24 = Math.sin(var9);
      double var26 = this.m10 * var22 + this.m20 * var24;
      double var28 = this.m11 * var22 + this.m21 * var24;
      double var30 = this.m12 * var22 + this.m22 * var24;
      double var32 = this.m13 * var22 + this.m23 * var24;
      double var34 = this.m20 * var22 - this.m10 * var24;
      double var36 = this.m21 * var22 - this.m11 * var24;
      double var38 = this.m22 * var22 - this.m12 * var24;
      double var40 = this.m23 * var22 - this.m13 * var24;
      var22 = Math.cos(var11);
      var24 = Math.sin(var11);
      double var42 = this.m00 * var22 - var34 * var24;
      double var44 = this.m01 * var22 - var36 * var24;
      double var46 = this.m02 * var22 - var38 * var24;
      double var48 = this.m03 * var22 - var40 * var24;
      double var50 = this.m00 * var24 + var34 * var22;
      double var52 = this.m01 * var24 + var36 * var22;
      double var54 = this.m02 * var24 + var38 * var22;
      double var56 = this.m03 * var24 + var40 * var22;
      var13.m30 = -var42 * var3 - var26 * var5 - var50 * var7 + var14;
      var13.m31 = -var44 * var3 - var28 * var5 - var52 * var7 + var16;
      var13.m32 = -var46 * var3 - var30 * var5 - var54 * var7 + var18;
      var13.m33 = -var48 * var3 - var32 * var5 - var56 * var7 + var20;
      var13.m20 = var50;
      var13.m21 = var52;
      var13.m22 = var54;
      var13.m23 = var56;
      var13.m10 = var26;
      var13.m11 = var28;
      var13.m12 = var30;
      var13.m13 = var32;
      var13.m00 = var42;
      var13.m01 = var44;
      var13.m02 = var46;
      var13.m03 = var48;
      var13.properties = (byte)(this.properties & -14);
      return var13;
   }

   public Matrix4d arcball(double var1, Vector3dc var3, double var4, double var6, Matrix4d var8) {
      return this.arcball(var1, var3.x(), var3.y(), var3.z(), var4, var6, var8);
   }

   public Matrix4d arcball(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.arcball(var1, var3, var5, var7, var9, var11, this);
   }

   public Matrix4d arcball(double var1, Vector3dc var3, double var4, double var6) {
      return this.arcball(var1, var3.x(), var3.y(), var3.z(), var4, var6, this);
   }

   public Matrix4d frustumAabb(Vector3d var1, Vector3d var2) {
      double var3 = Double.MAX_VALUE;
      double var5 = Double.MAX_VALUE;
      double var7 = Double.MAX_VALUE;
      double var9 = -1.7976931348623157E308D;
      double var11 = -1.7976931348623157E308D;
      double var13 = -1.7976931348623157E308D;

      for(int var15 = 0; var15 < 8; ++var15) {
         double var16 = (double)((var15 & 1) << 1) - 1.0D;
         double var18 = (double)((var15 >>> 1 & 1) << 1) - 1.0D;
         double var20 = (double)((var15 >>> 2 & 1) << 1) - 1.0D;
         double var22 = 1.0D / (this.m03 * var16 + this.m13 * var18 + this.m23 * var20 + this.m33);
         double var24 = (this.m00 * var16 + this.m10 * var18 + this.m20 * var20 + this.m30) * var22;
         double var26 = (this.m01 * var16 + this.m11 * var18 + this.m21 * var20 + this.m31) * var22;
         double var28 = (this.m02 * var16 + this.m12 * var18 + this.m22 * var20 + this.m32) * var22;
         var3 = var3 < var24 ? var3 : var24;
         var5 = var5 < var26 ? var5 : var26;
         var7 = var7 < var28 ? var7 : var28;
         var9 = var9 > var24 ? var9 : var24;
         var11 = var11 > var26 ? var11 : var26;
         var13 = var13 > var28 ? var13 : var28;
      }

      var1.x = var3;
      var1.y = var5;
      var1.z = var7;
      var2.x = var9;
      var2.y = var11;
      var2.z = var13;
      return this;
   }

   public Matrix4d projectedGridRange(Matrix4dc var1, double var2, double var4, Matrix4d var6) {
      double var7 = Double.MAX_VALUE;
      double var9 = Double.MAX_VALUE;
      double var11 = -1.7976931348623157E308D;
      double var13 = -1.7976931348623157E308D;
      boolean var15 = false;

      for(int var16 = 0; var16 < 12; ++var16) {
         double var17;
         double var19;
         double var21;
         double var23;
         double var25;
         double var27;
         if (var16 < 4) {
            var17 = -1.0D;
            var23 = 1.0D;
            var19 = var25 = (double)((var16 & 1) << 1) - 1.0D;
            var21 = var27 = (double)((var16 >>> 1 & 1) << 1) - 1.0D;
         } else if (var16 < 8) {
            var19 = -1.0D;
            var25 = 1.0D;
            var17 = var23 = (double)((var16 & 1) << 1) - 1.0D;
            var21 = var27 = (double)((var16 >>> 1 & 1) << 1) - 1.0D;
         } else {
            var21 = -1.0D;
            var27 = 1.0D;
            var17 = var23 = (double)((var16 & 1) << 1) - 1.0D;
            var19 = var25 = (double)((var16 >>> 1 & 1) << 1) - 1.0D;
         }

         double var29 = 1.0D / (this.m03 * var17 + this.m13 * var19 + this.m23 * var21 + this.m33);
         double var31 = (this.m00 * var17 + this.m10 * var19 + this.m20 * var21 + this.m30) * var29;
         double var33 = (this.m01 * var17 + this.m11 * var19 + this.m21 * var21 + this.m31) * var29;
         double var35 = (this.m02 * var17 + this.m12 * var19 + this.m22 * var21 + this.m32) * var29;
         var29 = 1.0D / (this.m03 * var23 + this.m13 * var25 + this.m23 * var27 + this.m33);
         double var37 = (this.m00 * var23 + this.m10 * var25 + this.m20 * var27 + this.m30) * var29;
         double var39 = (this.m01 * var23 + this.m11 * var25 + this.m21 * var27 + this.m31) * var29;
         double var41 = (this.m02 * var23 + this.m12 * var25 + this.m22 * var27 + this.m32) * var29;
         double var43 = var37 - var31;
         double var45 = var39 - var33;
         double var47 = var41 - var35;
         double var49 = 1.0D / var45;

         for(int var51 = 0; var51 < 2; ++var51) {
            double var52 = -(var33 + (var51 == 0 ? var2 : var4)) * var49;
            if (var52 >= 0.0D && var52 <= 1.0D) {
               var15 = true;
               double var54 = var31 + var52 * var43;
               double var56 = var35 + var52 * var47;
               var29 = 1.0D / (var1.m03() * var54 + var1.m23() * var56 + var1.m33());
               double var58 = (var1.m00() * var54 + var1.m20() * var56 + var1.m30()) * var29;
               double var60 = (var1.m01() * var54 + var1.m21() * var56 + var1.m31()) * var29;
               var7 = var7 < var58 ? var7 : var58;
               var9 = var9 < var60 ? var9 : var60;
               var11 = var11 > var58 ? var11 : var58;
               var13 = var13 > var60 ? var13 : var60;
            }
         }
      }

      if (!var15) {
         return null;
      } else {
         var6.set(var11 - var7, 0.0D, 0.0D, 0.0D, 0.0D, var13 - var9, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, var7, var9, 0.0D, 1.0D);
         var6.properties = 2;
         return var6;
      }
   }

   public Matrix4d perspectiveFrustumSlice(double var1, double var3, Matrix4d var5) {
      double var6 = (this.m23 + this.m22) / this.m32;
      double var8 = 1.0D / (var1 - var3);
      var5.m00 = this.m00 * var6 * var1;
      var5.m01 = this.m01;
      var5.m02 = this.m02;
      var5.m03 = this.m03;
      var5.m10 = this.m10;
      var5.m11 = this.m11 * var6 * var1;
      var5.m12 = this.m12;
      var5.m13 = this.m13;
      var5.m20 = this.m20;
      var5.m21 = this.m21;
      var5.m22 = (var3 + var1) * var8;
      var5.m23 = this.m23;
      var5.m30 = this.m30;
      var5.m31 = this.m31;
      var5.m32 = (var3 + var3) * var1 * var8;
      var5.m33 = this.m33;
      var5.properties = (byte)(this.properties & -13);
      return var5;
   }

   public Matrix4d orthoCrop(Matrix4dc var1, Matrix4d var2) {
      double var3 = Double.MAX_VALUE;
      double var5 = -1.7976931348623157E308D;
      double var7 = Double.MAX_VALUE;
      double var9 = -1.7976931348623157E308D;
      double var11 = Double.MAX_VALUE;
      double var13 = -1.7976931348623157E308D;

      for(int var15 = 0; var15 < 8; ++var15) {
         double var16 = (double)((var15 & 1) << 1) - 1.0D;
         double var18 = (double)((var15 >>> 1 & 1) << 1) - 1.0D;
         double var20 = (double)((var15 >>> 2 & 1) << 1) - 1.0D;
         double var22 = 1.0D / (this.m03 * var16 + this.m13 * var18 + this.m23 * var20 + this.m33);
         double var24 = (this.m00 * var16 + this.m10 * var18 + this.m20 * var20 + this.m30) * var22;
         double var26 = (this.m01 * var16 + this.m11 * var18 + this.m21 * var20 + this.m31) * var22;
         double var28 = (this.m02 * var16 + this.m12 * var18 + this.m22 * var20 + this.m32) * var22;
         var22 = 1.0D / (var1.m03() * var24 + var1.m13() * var26 + var1.m23() * var28 + var1.m33());
         double var30 = var1.m00() * var24 + var1.m10() * var26 + var1.m20() * var28 + var1.m30();
         double var32 = var1.m01() * var24 + var1.m11() * var26 + var1.m21() * var28 + var1.m31();
         double var34 = (var1.m02() * var24 + var1.m12() * var26 + var1.m22() * var28 + var1.m32()) * var22;
         var3 = var3 < var30 ? var3 : var30;
         var5 = var5 > var30 ? var5 : var30;
         var7 = var7 < var32 ? var7 : var32;
         var9 = var9 > var32 ? var9 : var32;
         var11 = var11 < var34 ? var11 : var34;
         var13 = var13 > var34 ? var13 : var34;
      }

      return var2.setOrtho(var3, var5, var7, var9, -var13, -var11);
   }

   public Matrix4d trapezoidCrop(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15) {
      double var17 = var7 - var3;
      double var19 = var1 - var5;
      double var23 = -var17;
      double var25 = var17 * var3 - var19 * var1;
      double var31 = -(var17 * var1 + var19 * var3);
      double var33 = var19 * var13 + var23 * var15 + var25;
      double var35 = var17 * var13 + var19 * var15 + var31;
      double var37 = -var33 / var35;
      double var21 = var19 + var37 * var17;
      var23 += var37 * var19;
      var25 += var37 * var31;
      double var39 = var21 * var5 + var23 * var7 + var25;
      double var41 = var21 * var9 + var23 * var11 + var25;
      double var43 = var39 * var35 / (var41 - var39);
      var31 += var43;
      double var45 = 2.0D / var41;
      double var47 = 1.0D / (var35 + var43);
      double var49 = (var47 + var47) * var43 / (1.0D - var47 * var43);
      double var51 = var17 * var47;
      double var53 = var19 * var47;
      double var55 = var31 * var47;
      double var27 = (var49 + 1.0D) * var51;
      double var29 = (var49 + 1.0D) * var53;
      var31 = (var49 + 1.0D) * var55 - var49;
      var21 = var45 * var21 - var51;
      var23 = var45 * var23 - var53;
      var25 = var45 * var25 - var55;
      this.set(var21, var27, 0.0D, var51, var23, var29, 0.0D, var53, 0.0D, 0.0D, 1.0D, 0.0D, var25, var31, 0.0D, var55);
      this.properties = 0;
      return this;
   }

   public Matrix4d transformAab(double var1, double var3, double var5, double var7, double var9, double var11, Vector3d var13, Vector3d var14) {
      double var15 = this.m00 * var1;
      double var17 = this.m01 * var1;
      double var19 = this.m02 * var1;
      double var21 = this.m00 * var7;
      double var23 = this.m01 * var7;
      double var25 = this.m02 * var7;
      double var27 = this.m10 * var3;
      double var29 = this.m11 * var3;
      double var31 = this.m12 * var3;
      double var33 = this.m10 * var9;
      double var35 = this.m11 * var9;
      double var37 = this.m12 * var9;
      double var39 = this.m20 * var5;
      double var41 = this.m21 * var5;
      double var43 = this.m22 * var5;
      double var45 = this.m20 * var11;
      double var47 = this.m21 * var11;
      double var49 = this.m22 * var11;
      double var69;
      double var51;
      if (var15 < var21) {
         var51 = var15;
         var69 = var21;
      } else {
         var51 = var21;
         var69 = var15;
      }

      double var71;
      double var53;
      if (var17 < var23) {
         var53 = var17;
         var71 = var23;
      } else {
         var53 = var23;
         var71 = var17;
      }

      double var73;
      double var55;
      if (var19 < var25) {
         var55 = var19;
         var73 = var25;
      } else {
         var55 = var25;
         var73 = var19;
      }

      double var75;
      double var57;
      if (var27 < var33) {
         var57 = var27;
         var75 = var33;
      } else {
         var57 = var33;
         var75 = var27;
      }

      double var77;
      double var59;
      if (var29 < var35) {
         var59 = var29;
         var77 = var35;
      } else {
         var59 = var35;
         var77 = var29;
      }

      double var79;
      double var61;
      if (var31 < var37) {
         var61 = var31;
         var79 = var37;
      } else {
         var61 = var37;
         var79 = var31;
      }

      double var81;
      double var63;
      if (var39 < var45) {
         var63 = var39;
         var81 = var45;
      } else {
         var63 = var45;
         var81 = var39;
      }

      double var65;
      double var83;
      if (var41 < var47) {
         var65 = var41;
         var83 = var47;
      } else {
         var65 = var47;
         var83 = var41;
      }

      double var67;
      double var85;
      if (var43 < var49) {
         var67 = var43;
         var85 = var49;
      } else {
         var67 = var49;
         var85 = var43;
      }

      var13.x = var51 + var57 + var63 + this.m30;
      var13.y = var53 + var59 + var65 + this.m31;
      var13.z = var55 + var61 + var67 + this.m32;
      var14.x = var69 + var75 + var81 + this.m30;
      var14.y = var71 + var77 + var83 + this.m31;
      var14.z = var73 + var79 + var85 + this.m32;
      return this;
   }

   public Matrix4d transformAab(Vector3dc var1, Vector3dc var2, Vector3d var3, Vector3d var4) {
      return this.transformAab(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3, var4);
   }

   public Matrix4d lerp(Matrix4dc var1, double var2) {
      return this.lerp(var1, var2, this);
   }

   public Matrix4d lerp(Matrix4dc var1, double var2, Matrix4d var4) {
      var4.m00 = this.m00 + (var1.m00() - this.m00) * var2;
      var4.m01 = this.m01 + (var1.m01() - this.m01) * var2;
      var4.m02 = this.m02 + (var1.m02() - this.m02) * var2;
      var4.m03 = this.m03 + (var1.m03() - this.m03) * var2;
      var4.m10 = this.m10 + (var1.m10() - this.m10) * var2;
      var4.m11 = this.m11 + (var1.m11() - this.m11) * var2;
      var4.m12 = this.m12 + (var1.m12() - this.m12) * var2;
      var4.m13 = this.m13 + (var1.m13() - this.m13) * var2;
      var4.m20 = this.m20 + (var1.m20() - this.m20) * var2;
      var4.m21 = this.m21 + (var1.m21() - this.m21) * var2;
      var4.m22 = this.m22 + (var1.m22() - this.m22) * var2;
      var4.m23 = this.m23 + (var1.m23() - this.m23) * var2;
      var4.m30 = this.m30 + (var1.m30() - this.m30) * var2;
      var4.m31 = this.m31 + (var1.m31() - this.m31) * var2;
      var4.m32 = this.m32 + (var1.m32() - this.m32) * var2;
      var4.m33 = this.m33 + (var1.m33() - this.m33) * var2;
      return var4;
   }

   public Matrix4d rotateTowards(Vector3dc var1, Vector3dc var2, Matrix4d var3) {
      return this.rotateTowards(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public Matrix4d rotateTowards(Vector3dc var1, Vector3dc var2) {
      return this.rotateTowards(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), this);
   }

   public Matrix4d rotateTowards(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.rotateTowards(var1, var3, var5, var7, var9, var11, this);
   }

   public Matrix4d rotateTowards(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
      double var14 = 1.0D / Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
      double var16 = var1 * var14;
      double var18 = var3 * var14;
      double var20 = var5 * var14;
      double var22 = var9 * var20 - var11 * var18;
      double var24 = var11 * var16 - var7 * var20;
      double var26 = var7 * var18 - var9 * var16;
      double var28 = 1.0D / Math.sqrt(var22 * var22 + var24 * var24 + var26 * var26);
      var22 *= var28;
      var24 *= var28;
      var26 *= var28;
      double var30 = var18 * var26 - var20 * var24;
      double var32 = var20 * var22 - var16 * var26;
      double var34 = var16 * var24 - var18 * var22;
      var13.m30 = this.m30;
      var13.m31 = this.m31;
      var13.m32 = this.m32;
      var13.m33 = this.m33;
      double var54 = this.m00 * var22 + this.m10 * var24 + this.m20 * var26;
      double var56 = this.m01 * var22 + this.m11 * var24 + this.m21 * var26;
      double var58 = this.m02 * var22 + this.m12 * var24 + this.m22 * var26;
      double var60 = this.m03 * var22 + this.m13 * var24 + this.m23 * var26;
      double var62 = this.m00 * var30 + this.m10 * var32 + this.m20 * var34;
      double var64 = this.m01 * var30 + this.m11 * var32 + this.m21 * var34;
      double var66 = this.m02 * var30 + this.m12 * var32 + this.m22 * var34;
      double var68 = this.m03 * var30 + this.m13 * var32 + this.m23 * var34;
      var13.m20 = this.m00 * var16 + this.m10 * var18 + this.m20 * var20;
      var13.m21 = this.m01 * var16 + this.m11 * var18 + this.m21 * var20;
      var13.m22 = this.m02 * var16 + this.m12 * var18 + this.m22 * var20;
      var13.m23 = this.m03 * var16 + this.m13 * var18 + this.m23 * var20;
      var13.m00 = var54;
      var13.m01 = var56;
      var13.m02 = var58;
      var13.m03 = var60;
      var13.m10 = var62;
      var13.m11 = var64;
      var13.m12 = var66;
      var13.m13 = var68;
      var13.properties = (byte)(this.properties & -14);
      return var13;
   }

   public Matrix4d rotationTowards(Vector3dc var1, Vector3dc var2) {
      return this.rotationTowards(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z());
   }

   public Matrix4d rotationTowards(double var1, double var3, double var5, double var7, double var9, double var11) {
      double var13 = 1.0D / Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
      double var15 = var1 * var13;
      double var17 = var3 * var13;
      double var19 = var5 * var13;
      double var21 = var9 * var19 - var11 * var17;
      double var23 = var11 * var15 - var7 * var19;
      double var25 = var7 * var17 - var9 * var15;
      double var27 = 1.0D / Math.sqrt(var21 * var21 + var23 * var23 + var25 * var25);
      var21 *= var27;
      var23 *= var27;
      var25 *= var27;
      double var29 = var17 * var25 - var19 * var23;
      double var31 = var19 * var21 - var15 * var25;
      double var33 = var15 * var23 - var17 * var21;
      this.m00 = var21;
      this.m01 = var23;
      this.m02 = var25;
      this.m03 = 0.0D;
      this.m10 = var29;
      this.m11 = var31;
      this.m12 = var33;
      this.m13 = 0.0D;
      this.m20 = var15;
      this.m21 = var17;
      this.m22 = var19;
      this.m23 = 0.0D;
      this.m30 = 0.0D;
      this.m31 = 0.0D;
      this.m32 = 0.0D;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Matrix4d translationRotateTowards(Vector3dc var1, Vector3dc var2, Vector3dc var3) {
      return this.translationRotateTowards(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3.x(), var3.y(), var3.z());
   }

   public Matrix4d translationRotateTowards(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17) {
      double var19 = 1.0D / Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
      double var21 = var7 * var19;
      double var23 = var9 * var19;
      double var25 = var11 * var19;
      double var27 = var15 * var25 - var17 * var23;
      double var29 = var17 * var21 - var13 * var25;
      double var31 = var13 * var23 - var15 * var21;
      double var33 = 1.0D / Math.sqrt(var27 * var27 + var29 * var29 + var31 * var31);
      var27 *= var33;
      var29 *= var33;
      var31 *= var33;
      double var35 = var23 * var31 - var25 * var29;
      double var37 = var25 * var27 - var21 * var31;
      double var39 = var21 * var29 - var23 * var27;
      this.m00 = var27;
      this.m01 = var29;
      this.m02 = var31;
      this.m03 = 0.0D;
      this.m10 = var35;
      this.m11 = var37;
      this.m12 = var39;
      this.m13 = 0.0D;
      this.m20 = var21;
      this.m21 = var23;
      this.m22 = var25;
      this.m23 = 0.0D;
      this.m30 = var1;
      this.m31 = var3;
      this.m32 = var5;
      this.m33 = 1.0D;
      this.properties = 2;
      return this;
   }

   public Vector3d getEulerAnglesZYX(Vector3d var1) {
      var1.x = Math.atan2(this.m12, this.m22);
      var1.y = Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22));
      var1.z = Math.atan2(this.m01, this.m00);
      return var1;
   }

   public Matrix4dc toImmutable() {
      return (Matrix4dc)(!Options.DEBUG ? this : new Matrix4d.Proxy(this));
   }

   private final class Proxy implements Matrix4dc {
      private final Matrix4dc delegate;

      Proxy(Matrix4dc var2) {
         this.delegate = var2;
      }

      public byte properties() {
         return this.delegate.properties();
      }

      public double m00() {
         return this.delegate.m00();
      }

      public double m01() {
         return this.delegate.m01();
      }

      public double m02() {
         return this.delegate.m02();
      }

      public double m03() {
         return this.delegate.m03();
      }

      public double m10() {
         return this.delegate.m10();
      }

      public double m11() {
         return this.delegate.m11();
      }

      public double m12() {
         return this.delegate.m12();
      }

      public double m13() {
         return this.delegate.m13();
      }

      public double m20() {
         return this.delegate.m20();
      }

      public double m21() {
         return this.delegate.m21();
      }

      public double m22() {
         return this.delegate.m22();
      }

      public double m23() {
         return this.delegate.m23();
      }

      public double m30() {
         return this.delegate.m30();
      }

      public double m31() {
         return this.delegate.m31();
      }

      public double m32() {
         return this.delegate.m32();
      }

      public double m33() {
         return this.delegate.m33();
      }

      public Matrix4d mul(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Matrix4d mul(Matrix4x3dc var1, Matrix4d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Matrix4d mul(Matrix4x3fc var1, Matrix4d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Matrix4d mul(Matrix4fc var1, Matrix4d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Matrix4d mulPerspectiveAffine(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.mulPerspectiveAffine(var1, var2);
      }

      public Matrix4d mulAffineR(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.mulAffineR(var1, var2);
      }

      public Matrix4d mulAffineR(Matrix4x3dc var1, Matrix4d var2) {
         return this.delegate.mulAffineR(var1, var2);
      }

      public Matrix4d mulAffine(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.mulAffine(var1, var2);
      }

      public Matrix4d mulTranslationAffine(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.mulTranslationAffine(var1, var2);
      }

      public Matrix4d mulOrthoAffine(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.mulOrthoAffine(var1, var2);
      }

      public Matrix4d fma4x3(Matrix4dc var1, double var2, Matrix4d var4) {
         return this.delegate.fma4x3(var1, var2, var4);
      }

      public Matrix4d add(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.add(var1, var2);
      }

      public Matrix4d sub(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.sub(var1, var2);
      }

      public Matrix4d mulComponentWise(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.mulComponentWise(var1, var2);
      }

      public Matrix4d add4x3(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.add4x3(var1, var2);
      }

      public Matrix4d add4x3(Matrix4fc var1, Matrix4d var2) {
         return this.delegate.add4x3(var1, var2);
      }

      public Matrix4d sub4x3(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.sub4x3(var1, var2);
      }

      public Matrix4d mul4x3ComponentWise(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.mul4x3ComponentWise(var1, var2);
      }

      public double determinant() {
         return this.delegate.determinant();
      }

      public double determinant3x3() {
         return this.delegate.determinant3x3();
      }

      public double determinantAffine() {
         return this.delegate.determinantAffine();
      }

      public Matrix4d invert(Matrix4d var1) {
         return this.delegate.invert(var1);
      }

      public Matrix4d invertPerspective(Matrix4d var1) {
         return this.delegate.invertPerspective(var1);
      }

      public Matrix4d invertFrustum(Matrix4d var1) {
         return this.delegate.invertFrustum(var1);
      }

      public Matrix4d invertOrtho(Matrix4d var1) {
         return this.delegate.invertOrtho(var1);
      }

      public Matrix4d invertPerspectiveView(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.invertPerspectiveView(var1, var2);
      }

      public Matrix4d invertAffine(Matrix4d var1) {
         return this.delegate.invertAffine(var1);
      }

      public Matrix4d invertAffineUnitScale(Matrix4d var1) {
         return this.delegate.invertAffineUnitScale(var1);
      }

      public Matrix4d invertLookAt(Matrix4d var1) {
         return this.delegate.invertLookAt(var1);
      }

      public Matrix4d transpose(Matrix4d var1) {
         return this.delegate.transpose(var1);
      }

      public Matrix4d transpose3x3(Matrix4d var1) {
         return this.delegate.transpose3x3(var1);
      }

      public Matrix3d transpose3x3(Matrix3d var1) {
         return this.delegate.transpose3x3(var1);
      }

      public Vector3d getTranslation(Vector3d var1) {
         return this.delegate.getTranslation(var1);
      }

      public Vector3d getScale(Vector3d var1) {
         return this.delegate.getScale(var1);
      }

      public Matrix4d get(Matrix4d var1) {
         return this.delegate.get(var1);
      }

      public Matrix4x3d get4x3(Matrix4x3d var1) {
         return this.delegate.get4x3(var1);
      }

      public Matrix3d get3x3(Matrix3d var1) {
         return this.delegate.get3x3(var1);
      }

      public Quaternionf getUnnormalizedRotation(Quaternionf var1) {
         return this.delegate.getUnnormalizedRotation(var1);
      }

      public Quaternionf getNormalizedRotation(Quaternionf var1) {
         return this.delegate.getNormalizedRotation(var1);
      }

      public Quaterniond getUnnormalizedRotation(Quaterniond var1) {
         return this.delegate.getUnnormalizedRotation(var1);
      }

      public Quaterniond getNormalizedRotation(Quaterniond var1) {
         return this.delegate.getNormalizedRotation(var1);
      }

      public DoubleBuffer get(DoubleBuffer var1) {
         return this.delegate.get(var1);
      }

      public DoubleBuffer get(int var1, DoubleBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public FloatBuffer get(FloatBuffer var1) {
         return this.delegate.get(var1);
      }

      public FloatBuffer get(int var1, FloatBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public ByteBuffer get(ByteBuffer var1) {
         return this.delegate.get(var1);
      }

      public ByteBuffer get(int var1, ByteBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public ByteBuffer getFloats(ByteBuffer var1) {
         return this.delegate.getFloats(var1);
      }

      public ByteBuffer getFloats(int var1, ByteBuffer var2) {
         return this.delegate.getFloats(var1, var2);
      }

      public double[] get(double[] var1, int var2) {
         return this.delegate.get(var1, var2);
      }

      public double[] get(double[] var1) {
         return this.delegate.get(var1);
      }

      public float[] get(float[] var1, int var2) {
         return this.delegate.get(var1, var2);
      }

      public float[] get(float[] var1) {
         return this.delegate.get(var1);
      }

      public DoubleBuffer getTransposed(DoubleBuffer var1) {
         return this.delegate.getTransposed(var1);
      }

      public DoubleBuffer getTransposed(int var1, DoubleBuffer var2) {
         return this.delegate.getTransposed(var1, var2);
      }

      public ByteBuffer getTransposed(ByteBuffer var1) {
         return this.delegate.getTransposed(var1);
      }

      public ByteBuffer getTransposed(int var1, ByteBuffer var2) {
         return this.delegate.getTransposed(var1, var2);
      }

      public DoubleBuffer get4x3Transposed(DoubleBuffer var1) {
         return this.delegate.get4x3Transposed(var1);
      }

      public DoubleBuffer get4x3Transposed(int var1, DoubleBuffer var2) {
         return this.delegate.get4x3Transposed(var1, var2);
      }

      public ByteBuffer get4x3Transposed(ByteBuffer var1) {
         return this.delegate.get4x3Transposed(var1);
      }

      public ByteBuffer get4x3Transposed(int var1, ByteBuffer var2) {
         return this.delegate.get4x3Transposed(var1, var2);
      }

      public Vector4d transform(Vector4d var1) {
         return this.delegate.transform(var1);
      }

      public Vector4d transform(Vector4dc var1, Vector4d var2) {
         return this.delegate.transform(var1, var2);
      }

      public Vector4d transform(double var1, double var3, double var5, double var7, Vector4d var9) {
         return this.delegate.transform(var1, var3, var5, var7, var9);
      }

      public Vector4d transformProject(Vector4d var1) {
         return this.delegate.transformProject(var1);
      }

      public Vector4d transformProject(Vector4dc var1, Vector4d var2) {
         return this.delegate.transformProject(var1, var2);
      }

      public Vector4d transformProject(double var1, double var3, double var5, double var7, Vector4d var9) {
         return this.delegate.transformProject(var1, var3, var5, var7, var9);
      }

      public Vector3d transformProject(Vector3d var1) {
         return this.delegate.transformProject(var1);
      }

      public Vector3d transformProject(Vector3dc var1, Vector3d var2) {
         return this.delegate.transformProject(var1, var2);
      }

      public Vector3d transformProject(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.transformProject(var1, var3, var5, var7);
      }

      public Vector3d transformPosition(Vector3d var1) {
         return this.delegate.transformPosition(var1);
      }

      public Vector3d transformPosition(Vector3dc var1, Vector3d var2) {
         return this.delegate.transformPosition(var1, var2);
      }

      public Vector3d transformPosition(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.transformPosition(var1, var3, var5, var7);
      }

      public Vector3d transformDirection(Vector3d var1) {
         return this.delegate.transformDirection(var1);
      }

      public Vector3d transformDirection(Vector3dc var1, Vector3d var2) {
         return this.delegate.transformDirection(var1, var2);
      }

      public Vector3d transformDirection(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.transformDirection(var1, var3, var5, var7);
      }

      public Vector4d transformAffine(Vector4d var1) {
         return this.delegate.transformAffine(var1);
      }

      public Vector4d transformAffine(Vector4dc var1, Vector4d var2) {
         return this.delegate.transformAffine(var1, var2);
      }

      public Vector4d transformAffine(double var1, double var3, double var5, double var7, Vector4d var9) {
         return this.delegate.transformAffine(var1, var3, var5, var7, var9);
      }

      public Matrix4d scale(Vector3dc var1, Matrix4d var2) {
         return this.delegate.scale(var1, var2);
      }

      public Matrix4d scale(double var1, double var3, double var5, Matrix4d var7) {
         return this.delegate.scale(var1, var3, var5, var7);
      }

      public Matrix4d scale(double var1, Matrix4d var3) {
         return this.delegate.scale(var1, var3);
      }

      public Matrix4d scaleAround(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
         return this.delegate.scaleAround(var1, var3, var5, var7, var9, var11, var13);
      }

      public Matrix4d scaleAround(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.scaleAround(var1, var3, var5, var7, var9);
      }

      public Matrix4d scaleLocal(double var1, double var3, double var5, Matrix4d var7) {
         return this.delegate.scaleLocal(var1, var3, var5, var7);
      }

      public Matrix4d scaleAroundLocal(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
         return this.delegate.scaleAroundLocal(var1, var3, var5, var7, var9, var11, var13);
      }

      public Matrix4d scaleAroundLocal(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.scaleAroundLocal(var1, var3, var5, var7, var9);
      }

      public Matrix4d rotate(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.rotate(var1, var3, var5, var7, var9);
      }

      public Matrix4d rotateTranslation(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.rotateTranslation(var1, var3, var5, var7, var9);
      }

      public Matrix4d rotateAffine(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.rotateAffine(var1, var3, var5, var7, var9);
      }

      public Matrix4d rotateAround(Quaterniondc var1, double var2, double var4, double var6, Matrix4d var8) {
         return this.delegate.rotateAround(var1, var2, var4, var6, var8);
      }

      public Matrix4d rotateLocal(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.rotateLocal(var1, var3, var5, var7, var9);
      }

      public Matrix4d rotateAroundLocal(Quaterniondc var1, double var2, double var4, double var6, Matrix4d var8) {
         return this.delegate.rotateAroundLocal(var1, var2, var4, var6, var8);
      }

      public Matrix4d translate(Vector3dc var1, Matrix4d var2) {
         return this.delegate.translate(var1, var2);
      }

      public Matrix4d translate(Vector3fc var1, Matrix4d var2) {
         return this.delegate.translate(var1, var2);
      }

      public Matrix4d translate(double var1, double var3, double var5, Matrix4d var7) {
         return this.delegate.translate(var1, var3, var5, var7);
      }

      public Matrix4d translateLocal(Vector3fc var1, Matrix4d var2) {
         return this.delegate.translateLocal(var1, var2);
      }

      public Matrix4d translateLocal(Vector3dc var1, Matrix4d var2) {
         return this.delegate.translateLocal(var1, var2);
      }

      public Matrix4d translateLocal(double var1, double var3, double var5, Matrix4d var7) {
         return this.delegate.translateLocal(var1, var3, var5, var7);
      }

      public Matrix4d rotateX(double var1, Matrix4d var3) {
         return this.delegate.rotateX(var1, var3);
      }

      public Matrix4d rotateY(double var1, Matrix4d var3) {
         return this.delegate.rotateY(var1, var3);
      }

      public Matrix4d rotateZ(double var1, Matrix4d var3) {
         return this.delegate.rotateZ(var1, var3);
      }

      public Matrix4d rotateXYZ(double var1, double var3, double var5, Matrix4d var7) {
         return this.delegate.rotateXYZ(var1, var3, var5, var7);
      }

      public Matrix4d rotateAffineXYZ(double var1, double var3, double var5, Matrix4d var7) {
         return this.delegate.rotateAffineXYZ(var1, var3, var5, var7);
      }

      public Matrix4d rotateZYX(double var1, double var3, double var5, Matrix4d var7) {
         return this.delegate.rotateZYX(var1, var3, var5, var7);
      }

      public Matrix4d rotateAffineZYX(double var1, double var3, double var5, Matrix4d var7) {
         return this.delegate.rotateAffineZYX(var1, var3, var5, var7);
      }

      public Matrix4d rotateYXZ(double var1, double var3, double var5, Matrix4d var7) {
         return this.delegate.rotateYXZ(var1, var3, var5, var7);
      }

      public Matrix4d rotateAffineYXZ(double var1, double var3, double var5, Matrix4d var7) {
         return this.delegate.rotateAffineYXZ(var1, var3, var5, var7);
      }

      public Matrix4d rotate(Quaterniondc var1, Matrix4d var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Matrix4d rotate(Quaternionfc var1, Matrix4d var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Matrix4d rotateAffine(Quaterniondc var1, Matrix4d var2) {
         return this.delegate.rotateAffine(var1, var2);
      }

      public Matrix4d rotateTranslation(Quaterniondc var1, Matrix4d var2) {
         return this.delegate.rotateTranslation(var1, var2);
      }

      public Matrix4d rotateTranslation(Quaternionfc var1, Matrix4d var2) {
         return this.delegate.rotateTranslation(var1, var2);
      }

      public Matrix4d rotateLocal(Quaterniondc var1, Matrix4d var2) {
         return this.delegate.rotateLocal(var1, var2);
      }

      public Matrix4d rotateAffine(Quaternionfc var1, Matrix4d var2) {
         return this.delegate.rotateAffine(var1, var2);
      }

      public Matrix4d rotateLocal(Quaternionfc var1, Matrix4d var2) {
         return this.delegate.rotateLocal(var1, var2);
      }

      public Matrix4d rotate(AxisAngle4f var1, Matrix4d var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Matrix4d rotate(AxisAngle4d var1, Matrix4d var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Matrix4d rotate(double var1, Vector3dc var3, Matrix4d var4) {
         return this.delegate.rotate(var1, var3, var4);
      }

      public Matrix4d rotate(double var1, Vector3fc var3, Matrix4d var4) {
         return this.delegate.rotate(var1, var3, var4);
      }

      public Vector4d getRow(int var1, Vector4d var2) throws IndexOutOfBoundsException {
         return this.delegate.getRow(var1, var2);
      }

      public Vector4d getColumn(int var1, Vector4d var2) throws IndexOutOfBoundsException {
         return this.delegate.getColumn(var1, var2);
      }

      public Matrix4d normal(Matrix4d var1) {
         return this.delegate.normal(var1);
      }

      public Matrix3d normal(Matrix3d var1) {
         return this.delegate.normal(var1);
      }

      public Matrix4d normalize3x3(Matrix4d var1) {
         return this.delegate.normalize3x3(var1);
      }

      public Matrix3d normalize3x3(Matrix3d var1) {
         return this.delegate.normalize3x3(var1);
      }

      public Vector4d unproject(double var1, double var3, double var5, int[] var7, Vector4d var8) {
         return this.delegate.unproject(var1, var3, var5, var7, var8);
      }

      public Vector3d unproject(double var1, double var3, double var5, int[] var7, Vector3d var8) {
         return this.delegate.unproject(var1, var3, var5, var7, var8);
      }

      public Vector4d unproject(Vector3dc var1, int[] var2, Vector4d var3) {
         return this.delegate.unproject(var1, var2, var3);
      }

      public Vector3d unproject(Vector3dc var1, int[] var2, Vector3d var3) {
         return this.delegate.unproject(var1, var2, var3);
      }

      public Matrix4d unprojectRay(double var1, double var3, int[] var5, Vector3d var6, Vector3d var7) {
         return this.delegate.unprojectRay(var1, var3, var5, var6, var7);
      }

      public Matrix4d unprojectRay(Vector2dc var1, int[] var2, Vector3d var3, Vector3d var4) {
         return this.delegate.unprojectRay(var1, var2, var3, var4);
      }

      public Vector4d unprojectInv(Vector3dc var1, int[] var2, Vector4d var3) {
         return this.delegate.unprojectInv(var1, var2, var3);
      }

      public Vector4d unprojectInv(double var1, double var3, double var5, int[] var7, Vector4d var8) {
         return this.delegate.unprojectInv(var1, var3, var5, var7, var8);
      }

      public Vector3d unprojectInv(Vector3dc var1, int[] var2, Vector3d var3) {
         return this.delegate.unprojectInv(var1, var2, var3);
      }

      public Vector3d unprojectInv(double var1, double var3, double var5, int[] var7, Vector3d var8) {
         return this.delegate.unprojectInv(var1, var3, var5, var7, var8);
      }

      public Matrix4d unprojectInvRay(Vector2dc var1, int[] var2, Vector3d var3, Vector3d var4) {
         return this.delegate.unprojectInvRay(var1, var2, var3, var4);
      }

      public Matrix4d unprojectInvRay(double var1, double var3, int[] var5, Vector3d var6, Vector3d var7) {
         return this.delegate.unprojectInvRay(var1, var3, var5, var6, var7);
      }

      public Vector4d project(double var1, double var3, double var5, int[] var7, Vector4d var8) {
         return this.delegate.project(var1, var3, var5, var7, var8);
      }

      public Vector3d project(double var1, double var3, double var5, int[] var7, Vector3d var8) {
         return this.delegate.project(var1, var3, var5, var7, var8);
      }

      public Vector4d project(Vector3dc var1, int[] var2, Vector4d var3) {
         return this.delegate.project(var1, var2, var3);
      }

      public Vector3d project(Vector3dc var1, int[] var2, Vector3d var3) {
         return this.delegate.project(var1, var2, var3);
      }

      public Matrix4d reflect(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.reflect(var1, var3, var5, var7, var9);
      }

      public Matrix4d reflect(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
         return this.delegate.reflect(var1, var3, var5, var7, var9, var11, var13);
      }

      public Matrix4d reflect(Quaterniondc var1, Vector3dc var2, Matrix4d var3) {
         return this.delegate.reflect(var1, var2, var3);
      }

      public Matrix4d reflect(Vector3dc var1, Vector3dc var2, Matrix4d var3) {
         return this.delegate.reflect(var1, var2, var3);
      }

      public Matrix4d ortho(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14) {
         return this.delegate.ortho(var1, var3, var5, var7, var9, var11, var13, var14);
      }

      public Matrix4d ortho(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
         return this.delegate.ortho(var1, var3, var5, var7, var9, var11, var13);
      }

      public Matrix4d orthoLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14) {
         return this.delegate.orthoLH(var1, var3, var5, var7, var9, var11, var13, var14);
      }

      public Matrix4d orthoLH(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
         return this.delegate.orthoLH(var1, var3, var5, var7, var9, var11, var13);
      }

      public Matrix4d orthoSymmetric(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10) {
         return this.delegate.orthoSymmetric(var1, var3, var5, var7, var9, var10);
      }

      public Matrix4d orthoSymmetric(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.orthoSymmetric(var1, var3, var5, var7, var9);
      }

      public Matrix4d orthoSymmetricLH(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10) {
         return this.delegate.orthoSymmetricLH(var1, var3, var5, var7, var9, var10);
      }

      public Matrix4d orthoSymmetricLH(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.orthoSymmetricLH(var1, var3, var5, var7, var9);
      }

      public Matrix4d ortho2D(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.ortho2D(var1, var3, var5, var7, var9);
      }

      public Matrix4d ortho2DLH(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.ortho2DLH(var1, var3, var5, var7, var9);
      }

      public Matrix4d lookAlong(Vector3dc var1, Vector3dc var2, Matrix4d var3) {
         return this.delegate.lookAlong(var1, var2, var3);
      }

      public Matrix4d lookAlong(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
         return this.delegate.lookAlong(var1, var3, var5, var7, var9, var11, var13);
      }

      public Matrix4d lookAt(Vector3dc var1, Vector3dc var2, Vector3dc var3, Matrix4d var4) {
         return this.delegate.lookAt(var1, var2, var3, var4);
      }

      public Matrix4d lookAt(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19) {
         return this.delegate.lookAt(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19);
      }

      public Matrix4d lookAtPerspective(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19) {
         return this.delegate.lookAtPerspective(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19);
      }

      public Matrix4d lookAtLH(Vector3dc var1, Vector3dc var2, Vector3dc var3, Matrix4d var4) {
         return this.delegate.lookAtLH(var1, var2, var3, var4);
      }

      public Matrix4d lookAtLH(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19) {
         return this.delegate.lookAtLH(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19);
      }

      public Matrix4d lookAtPerspectiveLH(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19) {
         return this.delegate.lookAtPerspectiveLH(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19);
      }

      public Matrix4d perspective(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10) {
         return this.delegate.perspective(var1, var3, var5, var7, var9, var10);
      }

      public Matrix4d perspective(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.perspective(var1, var3, var5, var7, var9);
      }

      public Matrix4d perspectiveLH(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10) {
         return this.delegate.perspectiveLH(var1, var3, var5, var7, var9, var10);
      }

      public Matrix4d perspectiveLH(double var1, double var3, double var5, double var7, Matrix4d var9) {
         return this.delegate.perspectiveLH(var1, var3, var5, var7, var9);
      }

      public Matrix4d frustum(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14) {
         return this.delegate.frustum(var1, var3, var5, var7, var9, var11, var13, var14);
      }

      public Matrix4d frustum(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
         return this.delegate.frustum(var1, var3, var5, var7, var9, var11, var13);
      }

      public Matrix4d frustumLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14) {
         return this.delegate.frustumLH(var1, var3, var5, var7, var9, var11, var13, var14);
      }

      public Matrix4d frustumLH(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
         return this.delegate.frustumLH(var1, var3, var5, var7, var9, var11, var13);
      }

      public Vector4d frustumPlane(int var1, Vector4d var2) {
         return this.delegate.frustumPlane(var1, var2);
      }

      public Vector3d frustumCorner(int var1, Vector3d var2) {
         return this.delegate.frustumCorner(var1, var2);
      }

      public Vector3d perspectiveOrigin(Vector3d var1) {
         return this.delegate.perspectiveOrigin(var1);
      }

      public double perspectiveFov() {
         return this.delegate.perspectiveFov();
      }

      public double perspectiveNear() {
         return this.delegate.perspectiveNear();
      }

      public double perspectiveFar() {
         return this.delegate.perspectiveFar();
      }

      public Vector3d frustumRayDir(double var1, double var3, Vector3d var5) {
         return this.delegate.frustumRayDir(var1, var3, var5);
      }

      public Vector3d positiveZ(Vector3d var1) {
         return this.delegate.positiveZ(var1);
      }

      public Vector3d normalizedPositiveZ(Vector3d var1) {
         return this.delegate.normalizedPositiveZ(var1);
      }

      public Vector3d positiveX(Vector3d var1) {
         return this.delegate.positiveX(var1);
      }

      public Vector3d normalizedPositiveX(Vector3d var1) {
         return this.delegate.normalizedPositiveX(var1);
      }

      public Vector3d positiveY(Vector3d var1) {
         return this.delegate.positiveY(var1);
      }

      public Vector3d normalizedPositiveY(Vector3d var1) {
         return this.delegate.normalizedPositiveY(var1);
      }

      public Vector3d originAffine(Vector3d var1) {
         return this.delegate.originAffine(var1);
      }

      public Vector3d origin(Vector3d var1) {
         return this.delegate.origin(var1);
      }

      public Matrix4d shadow(Vector4dc var1, double var2, double var4, double var6, double var8, Matrix4d var10) {
         return this.delegate.shadow(var1, var2, var4, var6, var8, var10);
      }

      public Matrix4d shadow(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, Matrix4d var17) {
         return this.delegate.shadow(var1, var3, var5, var7, var9, var11, var13, var15, var17);
      }

      public Matrix4d shadow(Vector4dc var1, Matrix4dc var2, Matrix4d var3) {
         return this.delegate.shadow(var1, var2, var3);
      }

      public Matrix4d shadow(double var1, double var3, double var5, double var7, Matrix4dc var9, Matrix4d var10) {
         return this.delegate.shadow(var1, var3, var5, var7, var9, var10);
      }

      public Matrix4d pick(double var1, double var3, double var5, double var7, int[] var9, Matrix4d var10) {
         return this.delegate.pick(var1, var3, var5, var7, var9, var10);
      }

      public boolean isAffine() {
         return this.delegate.isAffine();
      }

      public Matrix4d arcball(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
         return this.delegate.arcball(var1, var3, var5, var7, var9, var11, var13);
      }

      public Matrix4d arcball(double var1, Vector3dc var3, double var4, double var6, Matrix4d var8) {
         return this.delegate.arcball(var1, var3, var4, var6, var8);
      }

      public Matrix4d projectedGridRange(Matrix4dc var1, double var2, double var4, Matrix4d var6) {
         return this.delegate.projectedGridRange(var1, var2, var4, var6);
      }

      public Matrix4d perspectiveFrustumSlice(double var1, double var3, Matrix4d var5) {
         return this.delegate.perspectiveFrustumSlice(var1, var3, var5);
      }

      public Matrix4d orthoCrop(Matrix4dc var1, Matrix4d var2) {
         return this.delegate.orthoCrop(var1, var2);
      }

      public Matrix4d transformAab(double var1, double var3, double var5, double var7, double var9, double var11, Vector3d var13, Vector3d var14) {
         return this.delegate.transformAab(var1, var3, var5, var7, var9, var11, var13, var14);
      }

      public Matrix4d transformAab(Vector3dc var1, Vector3dc var2, Vector3d var3, Vector3d var4) {
         return this.delegate.transformAab(var1, var2, var3, var4);
      }

      public Matrix4d lerp(Matrix4dc var1, double var2, Matrix4d var4) {
         return this.delegate.lerp(var1, var2, var4);
      }

      public Matrix4d rotateTowards(Vector3dc var1, Vector3dc var2, Matrix4d var3) {
         return this.delegate.rotateTowards(var1, var2, var3);
      }

      public Matrix4d rotateTowards(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13) {
         return this.delegate.rotateTowards(var1, var3, var5, var7, var9, var11, var13);
      }

      public Vector3d getEulerAnglesZYX(Vector3d var1) {
         return this.delegate.getEulerAnglesZYX(var1);
      }
   }
}
