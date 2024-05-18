package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Quaterniond implements Externalizable, Quaterniondc {
   private static final long serialVersionUID = 1L;
   public double x;
   public double y;
   public double z;
   public double w;

   public Quaterniond() {
      this.x = 0.0D;
      this.y = 0.0D;
      this.z = 0.0D;
      this.w = 1.0D;
   }

   public Quaterniond(double var1, double var3, double var5, double var7) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.w = var7;
   }

   public Quaterniond(double var1, double var3, double var5) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.w = 1.0D;
   }

   public Quaterniond(Quaterniondc var1) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      this.w = var1.w();
   }

   public Quaterniond(Quaternionfc var1) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = (double)var1.z();
      this.w = (double)var1.w();
   }

   public Quaterniond(AxisAngle4f var1) {
      double var2 = Math.sin((double)var1.angle * 0.5D);
      this.x = (double)var1.x * var2;
      this.y = (double)var1.y * var2;
      this.z = (double)var1.z * var2;
      this.w = Math.cos((double)var1.angle * 0.5D);
   }

   public Quaterniond(AxisAngle4d var1) {
      double var2 = Math.sin(var1.angle * 0.5D);
      this.x = var1.x * var2;
      this.y = var1.y * var2;
      this.z = var1.z * var2;
      this.w = Math.cos(var1.angle * 0.5D);
   }

   public double x() {
      return this.x;
   }

   public double y() {
      return this.y;
   }

   public double z() {
      return this.z;
   }

   public double w() {
      return this.w;
   }

   public Quaterniond normalize() {
      double var1 = 1.0D / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      this.w *= var1;
      return this;
   }

   public Quaterniond normalize(Quaterniond var1) {
      double var2 = 1.0D / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      var1.x = this.x * var2;
      var1.y = this.y * var2;
      var1.z = this.z * var2;
      var1.w = this.w * var2;
      return var1;
   }

   public Quaterniond add(double var1, double var3, double var5, double var7) {
      return this.add(var1, var3, var5, var7, this);
   }

   public Quaterniond add(double var1, double var3, double var5, double var7, Quaterniond var9) {
      var9.x = this.x + var1;
      var9.y = this.y + var3;
      var9.z = this.z + var5;
      var9.w = this.w + var7;
      return var9;
   }

   public Quaterniond add(Quaterniondc var1) {
      this.x += var1.x();
      this.y += var1.y();
      this.z += var1.z();
      this.w += var1.w();
      return this;
   }

   public Quaterniond add(Quaterniondc var1, Quaterniond var2) {
      var2.x = this.x + var1.x();
      var2.y = this.y + var1.y();
      var2.z = this.z + var1.z();
      var2.w = this.w + var1.w();
      return var2;
   }

   public double dot(Quaterniondc var1) {
      return this.x * var1.x() + this.y * var1.y() + this.z * var1.z() + this.w * var1.w();
   }

   public double angle() {
      double var1 = 2.0D * Math.acos(this.w);
      return var1 <= 3.141592653589793D ? var1 : 6.283185307179586D - var1;
   }

   public Matrix3d get(Matrix3d var1) {
      return var1.set((Quaterniondc)this);
   }

   public Matrix3f get(Matrix3f var1) {
      return var1.set((Quaterniondc)this);
   }

   public Matrix4d get(Matrix4d var1) {
      return var1.set((Quaterniondc)this);
   }

   public Matrix4f get(Matrix4f var1) {
      return var1.set((Quaterniondc)this);
   }

   public Quaterniond get(Quaterniond var1) {
      return var1.set((Quaterniondc)this);
   }

   public Quaterniond set(double var1, double var3, double var5, double var7) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.w = var7;
      return this;
   }

   public Quaterniond set(double var1, double var3, double var5) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      return this;
   }

   public Quaterniond set(Quaterniondc var1) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      this.w = var1.w();
      return this;
   }

   public Quaterniond set(Quaternionfc var1) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = (double)var1.z();
      this.w = (double)var1.w();
      return this;
   }

   public Quaterniond set(AxisAngle4f var1) {
      return this.setAngleAxis((double)var1.angle, (double)var1.x, (double)var1.y, (double)var1.z);
   }

   public Quaterniond set(AxisAngle4d var1) {
      return this.setAngleAxis(var1.angle, var1.x, var1.y, var1.z);
   }

   public Quaterniond setAngleAxis(double var1, double var3, double var5, double var7) {
      double var9 = Math.sin(var1 * 0.5D);
      this.x = var3 * var9;
      this.y = var5 * var9;
      this.z = var7 * var9;
      this.w = Math.cos(var1 * 0.5D);
      return this;
   }

   public Quaterniond setAngleAxis(double var1, Vector3dc var3) {
      return this.setAngleAxis(var1, var3.x(), var3.y(), var3.z());
   }

   private void setFromUnnormalized(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17) {
      double var37 = 1.0D / Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
      double var39 = 1.0D / Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
      double var41 = 1.0D / Math.sqrt(var13 * var13 + var15 * var15 + var17 * var17);
      double var19 = var1 * var37;
      double var21 = var3 * var37;
      double var23 = var5 * var37;
      double var25 = var7 * var39;
      double var27 = var9 * var39;
      double var29 = var11 * var39;
      double var31 = var13 * var41;
      double var33 = var15 * var41;
      double var35 = var17 * var41;
      this.setFromNormalized(var19, var21, var23, var25, var27, var29, var31, var33, var35);
   }

   private void setFromNormalized(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17) {
      double var21 = var1 + var9 + var17;
      double var19;
      if (var21 >= 0.0D) {
         var19 = Math.sqrt(var21 + 1.0D);
         this.w = var19 * 0.5D;
         var19 = 0.5D / var19;
         this.x = (var11 - var15) * var19;
         this.y = (var13 - var5) * var19;
         this.z = (var3 - var7) * var19;
      } else if (var1 >= var9 && var1 >= var17) {
         var19 = Math.sqrt(var1 - (var9 + var17) + 1.0D);
         this.x = var19 * 0.5D;
         var19 = 0.5D / var19;
         this.y = (var7 + var3) * var19;
         this.z = (var5 + var13) * var19;
         this.w = (var11 - var15) * var19;
      } else if (var9 > var17) {
         var19 = Math.sqrt(var9 - (var17 + var1) + 1.0D);
         this.y = var19 * 0.5D;
         var19 = 0.5D / var19;
         this.z = (var15 + var11) * var19;
         this.x = (var7 + var3) * var19;
         this.w = (var13 - var5) * var19;
      } else {
         var19 = Math.sqrt(var17 - (var1 + var9) + 1.0D);
         this.z = var19 * 0.5D;
         var19 = 0.5D / var19;
         this.x = (var5 + var13) * var19;
         this.y = (var15 + var11) * var19;
         this.w = (var3 - var7) * var19;
      }

   }

   public Quaterniond setFromUnnormalized(Matrix4fc var1) {
      this.setFromUnnormalized((double)var1.m00(), (double)var1.m01(), (double)var1.m02(), (double)var1.m10(), (double)var1.m11(), (double)var1.m12(), (double)var1.m20(), (double)var1.m21(), (double)var1.m22());
      return this;
   }

   public Quaterniond setFromUnnormalized(Matrix4x3fc var1) {
      this.setFromUnnormalized((double)var1.m00(), (double)var1.m01(), (double)var1.m02(), (double)var1.m10(), (double)var1.m11(), (double)var1.m12(), (double)var1.m20(), (double)var1.m21(), (double)var1.m22());
      return this;
   }

   public Quaterniond setFromUnnormalized(Matrix4x3dc var1) {
      this.setFromUnnormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaterniond setFromNormalized(Matrix4fc var1) {
      this.setFromNormalized((double)var1.m00(), (double)var1.m01(), (double)var1.m02(), (double)var1.m10(), (double)var1.m11(), (double)var1.m12(), (double)var1.m20(), (double)var1.m21(), (double)var1.m22());
      return this;
   }

   public Quaterniond setFromNormalized(Matrix4x3fc var1) {
      this.setFromNormalized((double)var1.m00(), (double)var1.m01(), (double)var1.m02(), (double)var1.m10(), (double)var1.m11(), (double)var1.m12(), (double)var1.m20(), (double)var1.m21(), (double)var1.m22());
      return this;
   }

   public Quaterniond setFromNormalized(Matrix4x3dc var1) {
      this.setFromNormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaterniond setFromUnnormalized(Matrix4dc var1) {
      this.setFromUnnormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaterniond setFromNormalized(Matrix4dc var1) {
      this.setFromNormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaterniond setFromUnnormalized(Matrix3fc var1) {
      this.setFromUnnormalized((double)var1.m00(), (double)var1.m01(), (double)var1.m02(), (double)var1.m10(), (double)var1.m11(), (double)var1.m12(), (double)var1.m20(), (double)var1.m21(), (double)var1.m22());
      return this;
   }

   public Quaterniond setFromNormalized(Matrix3fc var1) {
      this.setFromNormalized((double)var1.m00(), (double)var1.m01(), (double)var1.m02(), (double)var1.m10(), (double)var1.m11(), (double)var1.m12(), (double)var1.m20(), (double)var1.m21(), (double)var1.m22());
      return this;
   }

   public Quaterniond setFromUnnormalized(Matrix3dc var1) {
      this.setFromUnnormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaterniond setFromNormalized(Matrix3dc var1) {
      this.setFromNormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaterniond fromAxisAngleRad(Vector3dc var1, double var2) {
      return this.fromAxisAngleRad(var1.x(), var1.y(), var1.z(), var2);
   }

   public Quaterniond fromAxisAngleRad(double var1, double var3, double var5, double var7) {
      double var9 = var7 / 2.0D;
      double var11 = Math.sin(var9);
      double var13 = Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
      this.x = var1 / var13 * var11;
      this.y = var3 / var13 * var11;
      this.z = var5 / var13 * var11;
      this.w = Math.cos(var9);
      return this;
   }

   public Quaterniond fromAxisAngleDeg(Vector3dc var1, double var2) {
      return this.fromAxisAngleRad(var1.x(), var1.y(), var1.z(), Math.toRadians(var2));
   }

   public Quaterniond fromAxisAngleDeg(double var1, double var3, double var5, double var7) {
      return this.fromAxisAngleRad(var1, var3, var5, Math.toRadians(var7));
   }

   public Quaterniond mul(Quaterniondc var1) {
      return this.mul(var1, this);
   }

   public Quaterniond mul(Quaterniondc var1, Quaterniond var2) {
      var2.set(this.w * var1.x() + this.x * var1.w() + this.y * var1.z() - this.z * var1.y(), this.w * var1.y() - this.x * var1.z() + this.y * var1.w() + this.z * var1.x(), this.w * var1.z() + this.x * var1.y() - this.y * var1.x() + this.z * var1.w(), this.w * var1.w() - this.x * var1.x() - this.y * var1.y() - this.z * var1.z());
      return var2;
   }

   public Quaterniond mul(double var1, double var3, double var5, double var7) {
      this.set(this.w * var1 + this.x * var7 + this.y * var5 - this.z * var3, this.w * var3 - this.x * var5 + this.y * var7 + this.z * var1, this.w * var5 + this.x * var3 - this.y * var1 + this.z * var7, this.w * var7 - this.x * var1 - this.y * var3 - this.z * var5);
      return this;
   }

   public Quaterniond mul(double var1, double var3, double var5, double var7, Quaterniond var9) {
      var9.set(this.w * var1 + this.x * var7 + this.y * var5 - this.z * var3, this.w * var3 - this.x * var5 + this.y * var7 + this.z * var1, this.w * var5 + this.x * var3 - this.y * var1 + this.z * var7, this.w * var7 - this.x * var1 - this.y * var3 - this.z * var5);
      return var9;
   }

   public Quaterniond premul(Quaterniondc var1) {
      return this.premul(var1, this);
   }

   public Quaterniond premul(Quaterniondc var1, Quaterniond var2) {
      var2.set(var1.w() * this.x + var1.x() * this.w + var1.y() * this.z - var1.z() * this.y, var1.w() * this.y - var1.x() * this.z + var1.y() * this.w + var1.z() * this.x, var1.w() * this.z + var1.x() * this.y - var1.y() * this.x + var1.z() * this.w, var1.w() * this.w - var1.x() * this.x - var1.y() * this.y - var1.z() * this.z);
      return var2;
   }

   public Quaterniond premul(double var1, double var3, double var5, double var7) {
      return this.premul(var1, var3, var5, var7, this);
   }

   public Quaterniond premul(double var1, double var3, double var5, double var7, Quaterniond var9) {
      var9.set(var7 * this.x + var1 * this.w + var3 * this.z - var5 * this.y, var7 * this.y - var1 * this.z + var3 * this.w + var5 * this.x, var7 * this.z + var1 * this.y - var3 * this.x + var5 * this.w, var7 * this.w - var1 * this.x - var3 * this.y - var5 * this.z);
      return var9;
   }

   public Vector3d transform(Vector3d var1) {
      return this.transform(var1.x, var1.y, var1.z, var1);
   }

   public Vector4d transform(Vector4d var1) {
      return this.transform((Vector4dc)var1, (Vector4d)var1);
   }

   public Vector3d transform(Vector3dc var1, Vector3d var2) {
      return this.transform(var1.x(), var1.y(), var1.z(), var2);
   }

   public Vector3d transform(double var1, double var3, double var5, Vector3d var7) {
      double var8 = this.w * this.w;
      double var10 = this.x * this.x;
      double var12 = this.y * this.y;
      double var14 = this.z * this.z;
      double var16 = this.z * this.w;
      double var18 = this.x * this.y;
      double var20 = this.x * this.z;
      double var22 = this.y * this.w;
      double var24 = this.y * this.z;
      double var26 = this.x * this.w;
      double var28 = var8 + var10 - var14 - var12;
      double var30 = var18 + var16 + var16 + var18;
      double var32 = var20 - var22 + var20 - var22;
      double var34 = -var16 + var18 - var16 + var18;
      double var36 = var12 - var14 + var8 - var10;
      double var38 = var24 + var24 + var26 + var26;
      double var40 = var22 + var20 + var20 + var22;
      double var42 = var24 + var24 - var26 - var26;
      double var44 = var14 - var12 - var10 + var8;
      var7.x = var28 * var1 + var34 * var3 + var40 * var5;
      var7.y = var30 * var1 + var36 * var3 + var42 * var5;
      var7.z = var32 * var1 + var38 * var3 + var44 * var5;
      return var7;
   }

   public Vector4d transform(Vector4dc var1, Vector4d var2) {
      return this.transform(var1.x(), var1.y(), var1.z(), var2);
   }

   public Vector4d transform(double var1, double var3, double var5, Vector4d var7) {
      double var8 = this.w * this.w;
      double var10 = this.x * this.x;
      double var12 = this.y * this.y;
      double var14 = this.z * this.z;
      double var16 = this.z * this.w;
      double var18 = this.x * this.y;
      double var20 = this.x * this.z;
      double var22 = this.y * this.w;
      double var24 = this.y * this.z;
      double var26 = this.x * this.w;
      double var28 = var8 + var10 - var14 - var12;
      double var30 = var18 + var16 + var16 + var18;
      double var32 = var20 - var22 + var20 - var22;
      double var34 = -var16 + var18 - var16 + var18;
      double var36 = var12 - var14 + var8 - var10;
      double var38 = var24 + var24 + var26 + var26;
      double var40 = var22 + var20 + var20 + var22;
      double var42 = var24 + var24 - var26 - var26;
      double var44 = var14 - var12 - var10 + var8;
      var7.x = var28 * var1 + var34 * var3 + var40 * var5;
      var7.y = var30 * var1 + var36 * var3 + var42 * var5;
      var7.z = var32 * var1 + var38 * var3 + var44 * var5;
      return var7;
   }

   public Quaterniond invert(Quaterniond var1) {
      double var2 = 1.0D / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      var1.x = -this.x * var2;
      var1.y = -this.y * var2;
      var1.z = -this.z * var2;
      var1.w = this.w * var2;
      return var1;
   }

   public Quaterniond invert() {
      return this.invert(this);
   }

   public Quaterniond div(Quaterniondc var1, Quaterniond var2) {
      double var3 = 1.0D / (var1.x() * var1.x() + var1.y() * var1.y() + var1.z() * var1.z() + var1.w() * var1.w());
      double var5 = -var1.x() * var3;
      double var7 = -var1.y() * var3;
      double var9 = -var1.z() * var3;
      double var11 = var1.w() * var3;
      var2.set(this.w * var5 + this.x * var11 + this.y * var9 - this.z * var7, this.w * var7 - this.x * var9 + this.y * var11 + this.z * var5, this.w * var9 + this.x * var7 - this.y * var5 + this.z * var11, this.w * var11 - this.x * var5 - this.y * var7 - this.z * var9);
      return var2;
   }

   public Quaterniond div(Quaterniondc var1) {
      return this.div(var1, this);
   }

   public Quaterniond conjugate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
      return this;
   }

   public Quaterniond conjugate(Quaterniond var1) {
      var1.x = -this.x;
      var1.y = -this.y;
      var1.z = -this.z;
      var1.w = this.w;
      return var1;
   }

   public Quaterniond identity() {
      this.x = 0.0D;
      this.y = 0.0D;
      this.z = 0.0D;
      this.w = 1.0D;
      return this;
   }

   public double lengthSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
   }

   public Quaterniond rotationXYZ(double var1, double var3, double var5) {
      double var7 = Math.sin(var1 * 0.5D);
      double var9 = Math.cos(var1 * 0.5D);
      double var11 = Math.sin(var3 * 0.5D);
      double var13 = Math.cos(var3 * 0.5D);
      double var15 = Math.sin(var5 * 0.5D);
      double var17 = Math.cos(var5 * 0.5D);
      double var19 = var13 * var17;
      double var21 = var11 * var15;
      double var23 = var11 * var17;
      double var25 = var13 * var15;
      this.w = var9 * var19 - var7 * var21;
      this.x = var7 * var19 + var9 * var21;
      this.y = var9 * var23 - var7 * var25;
      this.z = var9 * var25 + var7 * var23;
      return this;
   }

   public Quaterniond rotationZYX(double var1, double var3, double var5) {
      double var7 = Math.sin(var5 * 0.5D);
      double var9 = Math.cos(var5 * 0.5D);
      double var11 = Math.sin(var3 * 0.5D);
      double var13 = Math.cos(var3 * 0.5D);
      double var15 = Math.sin(var1 * 0.5D);
      double var17 = Math.cos(var1 * 0.5D);
      double var19 = var13 * var17;
      double var21 = var11 * var15;
      double var23 = var11 * var17;
      double var25 = var13 * var15;
      this.w = var9 * var19 + var7 * var21;
      this.x = var7 * var19 - var9 * var21;
      this.y = var9 * var23 + var7 * var25;
      this.z = var9 * var25 - var7 * var23;
      return this;
   }

   public Quaterniond rotationYXZ(double var1, double var3, double var5) {
      double var7 = Math.sin(var3 * 0.5D);
      double var9 = Math.cos(var3 * 0.5D);
      double var11 = Math.sin(var1 * 0.5D);
      double var13 = Math.cos(var1 * 0.5D);
      double var15 = Math.sin(var5 * 0.5D);
      double var17 = Math.cos(var5 * 0.5D);
      double var19 = var13 * var7;
      double var21 = var11 * var9;
      double var23 = var11 * var7;
      double var25 = var13 * var9;
      this.x = var19 * var17 + var21 * var15;
      this.y = var21 * var17 - var19 * var15;
      this.z = var25 * var15 - var23 * var17;
      this.w = var25 * var17 + var23 * var15;
      return this;
   }

   public Quaterniond slerp(Quaterniondc var1, double var2) {
      return this.slerp(var1, var2, this);
   }

   public Quaterniond slerp(Quaterniondc var1, double var2, Quaterniond var4) {
      double var5 = this.x * var1.x() + this.y * var1.y() + this.z * var1.z() + this.w * var1.w();
      double var7 = Math.abs(var5);
      double var9;
      double var11;
      if (1.0D - var7 > 1.0E-6D) {
         double var13 = 1.0D - var7 * var7;
         double var15 = 1.0D / Math.sqrt(var13);
         double var17 = Math.atan2(var13 * var15, var7);
         var9 = Math.sin((1.0D - var2) * var17) * var15;
         var11 = Math.sin(var2 * var17) * var15;
      } else {
         var9 = 1.0D - var2;
         var11 = var2;
      }

      var11 = var5 >= 0.0D ? var11 : -var11;
      var4.x = var9 * this.x + var11 * var1.x();
      var4.y = var9 * this.y + var11 * var1.y();
      var4.z = var9 * this.z + var11 * var1.z();
      var4.w = var9 * this.w + var11 * var1.w();
      return var4;
   }

   public static Quaterniondc slerp(Quaterniond[] var0, double[] var1, Quaterniond var2) {
      var2.set((Quaterniondc)var0[0]);
      double var3 = var1[0];

      for(int var5 = 1; var5 < var0.length; ++var5) {
         double var8 = var1[var5];
         double var10 = var8 / (var3 + var8);
         var3 += var8;
         var2.slerp(var0[var5], var10);
      }

      return var2;
   }

   public Quaterniond scale(double var1) {
      return this.scale(var1, this);
   }

   public Quaterniond scale(double var1, Quaterniond var3) {
      double var4 = Math.sqrt(var1);
      var3.x = var4 * this.x;
      var3.y = var4 * this.y;
      var3.z = var4 * this.z;
      var3.w = var4 * this.w;
      return this;
   }

   public Quaterniond scaling(float var1) {
      double var2 = Math.sqrt((double)var1);
      this.x = 0.0D;
      this.y = 0.0D;
      this.z = 0.0D;
      this.w = var2;
      return this;
   }

   public Quaterniond integrate(double var1, double var3, double var5, double var7) {
      return this.integrate(var1, var3, var5, var7, this);
   }

   public Quaterniond integrate(double var1, double var3, double var5, double var7, Quaterniond var9) {
      return this.rotateLocal(var1 * var3, var1 * var5, var1 * var7, var9);
   }

   public Quaterniond nlerp(Quaterniondc var1, double var2) {
      return this.nlerp(var1, var2, this);
   }

   public Quaterniond nlerp(Quaterniondc var1, double var2, Quaterniond var4) {
      double var5 = this.x * var1.x() + this.y * var1.y() + this.z * var1.z() + this.w * var1.w();
      double var7 = 1.0D - var2;
      double var9 = var5 >= 0.0D ? var2 : -var2;
      var4.x = var7 * this.x + var9 * var1.x();
      var4.y = var7 * this.y + var9 * var1.y();
      var4.z = var7 * this.z + var9 * var1.z();
      var4.w = var7 * this.w + var9 * var1.w();
      double var11 = 1.0D / Math.sqrt(var4.x * var4.x + var4.y * var4.y + var4.z * var4.z + var4.w * var4.w);
      var4.x *= var11;
      var4.y *= var11;
      var4.z *= var11;
      var4.w *= var11;
      return var4;
   }

   public static Quaterniondc nlerp(Quaterniond[] var0, double[] var1, Quaterniond var2) {
      var2.set((Quaterniondc)var0[0]);
      double var3 = var1[0];

      for(int var5 = 1; var5 < var0.length; ++var5) {
         double var8 = var1[var5];
         double var10 = var8 / (var3 + var8);
         var3 += var8;
         var2.nlerp(var0[var5], var10);
      }

      return var2;
   }

   public Quaterniond nlerpIterative(Quaterniondc var1, double var2, double var4, Quaterniond var6) {
      double var7 = this.x;
      double var9 = this.y;
      double var11 = this.z;
      double var13 = this.w;
      double var15 = var1.x();
      double var17 = var1.y();
      double var19 = var1.z();
      double var21 = var1.w();
      double var23 = var7 * var15 + var9 * var17 + var11 * var19 + var13 * var21;
      double var25 = Math.abs(var23);
      if (0.999999D < var25) {
         return var6.set((Quaterniondc)this);
      } else {
         double var33;
         double var27;
         double var29;
         double var31;
         for(var27 = var2; var25 < var4; var25 = Math.abs(var23)) {
            var29 = 0.5D;
            var31 = var23 >= 0.0D ? 0.5D : -0.5D;
            if (var27 < 0.5D) {
               var15 = var29 * var15 + var31 * var7;
               var17 = var29 * var17 + var31 * var9;
               var19 = var29 * var19 + var31 * var11;
               var21 = var29 * var21 + var31 * var13;
               var33 = 1.0D / Math.sqrt(var15 * var15 + var17 * var17 + var19 * var19 + var21 * var21);
               var15 *= var33;
               var17 *= var33;
               var19 *= var33;
               var21 *= var33;
               var27 += var27;
            } else {
               var7 = var29 * var7 + var31 * var15;
               var9 = var29 * var9 + var31 * var17;
               var11 = var29 * var11 + var31 * var19;
               var13 = var29 * var13 + var31 * var21;
               var33 = 1.0D / Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11 + var13 * var13);
               var7 *= var33;
               var9 *= var33;
               var11 *= var33;
               var13 *= var33;
               var27 = var27 + var27 - 1.0D;
            }

            var23 = var7 * var15 + var9 * var17 + var11 * var19 + var13 * var21;
         }

         var29 = 1.0D - var27;
         var31 = var23 >= 0.0D ? var27 : -var27;
         var33 = var29 * var7 + var31 * var15;
         double var35 = var29 * var9 + var31 * var17;
         double var37 = var29 * var11 + var31 * var19;
         double var39 = var29 * var13 + var31 * var21;
         double var41 = 1.0D / Math.sqrt(var33 * var33 + var35 * var35 + var37 * var37 + var39 * var39);
         var6.x *= var41;
         var6.y *= var41;
         var6.z *= var41;
         var6.w *= var41;
         return var6;
      }
   }

   public Quaterniond nlerpIterative(Quaterniondc var1, double var2, double var4) {
      return this.nlerpIterative(var1, var2, var4, this);
   }

   public static Quaterniond nlerpIterative(Quaterniondc[] var0, double[] var1, double var2, Quaterniond var4) {
      var4.set(var0[0]);
      double var5 = var1[0];

      for(int var7 = 1; var7 < var0.length; ++var7) {
         double var10 = var1[var7];
         double var12 = var10 / (var5 + var10);
         var5 += var10;
         var4.nlerpIterative(var0[var7], var12, var2);
      }

      return var4;
   }

   public Quaterniond lookAlong(Vector3dc var1, Vector3dc var2) {
      return this.lookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), this);
   }

   public Quaterniond lookAlong(Vector3dc var1, Vector3dc var2, Quaterniond var3) {
      return this.lookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public Quaterniond lookAlong(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.lookAlong(var1, var3, var5, var7, var9, var11, this);
   }

   public Quaterniond lookAlong(double var1, double var3, double var5, double var7, double var9, double var11, Quaterniond var13) {
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
      double var46 = var22 + var32 + var20;
      double var36;
      double var38;
      double var40;
      double var42;
      double var44;
      if (var46 >= 0.0D) {
         var44 = Math.sqrt(var46 + 1.0D);
         var42 = var44 * 0.5D;
         var44 = 0.5D / var44;
         var36 = (var18 - var34) * var44;
         var38 = (var26 - var16) * var44;
         var40 = (var30 - var24) * var44;
      } else if (var22 > var32 && var22 > var20) {
         var44 = Math.sqrt(1.0D + var22 - var32 - var20);
         var36 = var44 * 0.5D;
         var44 = 0.5D / var44;
         var38 = (var24 + var30) * var44;
         var40 = (var16 + var26) * var44;
         var42 = (var18 - var34) * var44;
      } else if (var32 > var20) {
         var44 = Math.sqrt(1.0D + var32 - var22 - var20);
         var38 = var44 * 0.5D;
         var44 = 0.5D / var44;
         var36 = (var24 + var30) * var44;
         var40 = (var34 + var18) * var44;
         var42 = (var26 - var16) * var44;
      } else {
         var44 = Math.sqrt(1.0D + var20 - var22 - var32);
         var40 = var44 * 0.5D;
         var44 = 0.5D / var44;
         var36 = (var16 + var26) * var44;
         var38 = (var34 + var18) * var44;
         var42 = (var30 - var24) * var44;
      }

      var13.set(this.w * var36 + this.x * var42 + this.y * var40 - this.z * var38, this.w * var38 - this.x * var40 + this.y * var42 + this.z * var36, this.w * var40 + this.x * var38 - this.y * var36 + this.z * var42, this.w * var42 - this.x * var36 - this.y * var38 - this.z * var40);
      return var13;
   }

   public String toString() {
      DecimalFormat var1 = new DecimalFormat(" 0.000E0;-");
      return this.toString(var1).replaceAll("E(\\d+)", "E+$1");
   }

   public String toString(NumberFormat var1) {
      return "(" + var1.format(this.x) + " " + var1.format(this.y) + " " + var1.format(this.z) + " " + var1.format(this.w) + ")";
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeDouble(this.w);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.w = var1.readDouble();
   }

   public int hashCode() {
      byte var2 = 1;
      long var3 = Double.doubleToLongBits(this.w);
      int var5 = 31 * var2 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.x);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.y);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.z);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      return var5;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         Quaterniond var2 = (Quaterniond)var1;
         if (Double.doubleToLongBits(this.w) != Double.doubleToLongBits(var2.w)) {
            return false;
         } else if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(var2.x)) {
            return false;
         } else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(var2.y)) {
            return false;
         } else {
            return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(var2.z);
         }
      }
   }

   public Quaterniond difference(Quaterniondc var1) {
      return this.difference(var1, this);
   }

   public Quaterniond difference(Quaterniondc var1, Quaterniond var2) {
      double var3 = 1.0D / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      double var5 = -this.x * var3;
      double var7 = -this.y * var3;
      double var9 = -this.z * var3;
      double var11 = this.w * var3;
      var2.set(var11 * var1.x() + var5 * var1.w() + var7 * var1.z() - var9 * var1.y(), var11 * var1.y() - var5 * var1.z() + var7 * var1.w() + var9 * var1.x(), var11 * var1.z() + var5 * var1.y() - var7 * var1.x() + var9 * var1.w(), var11 * var1.w() - var5 * var1.x() - var7 * var1.y() - var9 * var1.z());
      return var2;
   }

   public Quaterniond rotationTo(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.x = var3 * var11 - var5 * var9;
      this.y = var5 * var7 - var1 * var11;
      this.z = var1 * var9 - var3 * var7;
      this.w = Math.sqrt((var1 * var1 + var3 * var3 + var5 * var5) * (var7 * var7 + var9 * var9 + var11 * var11)) + var1 * var7 + var3 * var9 + var5 * var11;
      double var13 = 1.0D / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      if (Double.isInfinite(var13)) {
         this.x = var9;
         this.y = -var7;
         this.z = 0.0D;
         this.w = 0.0D;
         var13 = (double)((float)(1.0D / Math.sqrt(this.x * this.x + this.y * this.y)));
         if (Double.isInfinite(var13)) {
            this.x = 0.0D;
            this.y = var11;
            this.z = -var9;
            this.w = 0.0D;
            var13 = (double)((float)(1.0D / Math.sqrt(this.y * this.y + this.z * this.z)));
         }
      }

      this.x *= var13;
      this.y *= var13;
      this.z *= var13;
      this.w *= var13;
      return this;
   }

   public Quaterniond rotationTo(Vector3dc var1, Vector3dc var2) {
      return this.rotationTo(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z());
   }

   public Quaterniond rotateTo(double var1, double var3, double var5, double var7, double var9, double var11, Quaterniond var13) {
      double var14 = var3 * var11 - var5 * var9;
      double var16 = var5 * var7 - var1 * var11;
      double var18 = var1 * var9 - var3 * var7;
      double var20 = Math.sqrt((var1 * var1 + var3 * var3 + var5 * var5) * (var7 * var7 + var9 * var9 + var11 * var11)) + var1 * var7 + var3 * var9 + var5 * var11;
      double var22 = 1.0D / Math.sqrt(var14 * var14 + var16 * var16 + var18 * var18 + var20 * var20);
      if (Double.isInfinite(var22)) {
         var14 = var9;
         var16 = -var7;
         var18 = 0.0D;
         var20 = 0.0D;
         var22 = (double)((float)(1.0D / Math.sqrt(var9 * var9 + var16 * var16)));
         if (Double.isInfinite(var22)) {
            var14 = 0.0D;
            var16 = var11;
            var18 = -var9;
            var20 = 0.0D;
            var22 = (double)((float)(1.0D / Math.sqrt(var11 * var11 + var18 * var18)));
         }
      }

      var14 *= var22;
      var16 *= var22;
      var18 *= var22;
      var20 *= var22;
      var13.set(this.w * var14 + this.x * var20 + this.y * var18 - this.z * var16, this.w * var16 - this.x * var18 + this.y * var20 + this.z * var14, this.w * var18 + this.x * var16 - this.y * var14 + this.z * var20, this.w * var20 - this.x * var14 - this.y * var16 - this.z * var18);
      return var13;
   }

   public Quaterniond rotationAxis(AxisAngle4f var1) {
      return this.rotationAxis((double)var1.angle, (double)var1.x, (double)var1.y, (double)var1.z);
   }

   public Quaterniond rotationAxis(double var1, double var3, double var5, double var7) {
      double var9 = var1 / 2.0D;
      double var11 = Math.sin(var9);
      double var13 = 1.0D / Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
      this.x = var3 * var13 * var11;
      this.y = var5 * var13 * var11;
      this.z = var7 * var13 * var11;
      this.w = (double)((float)Math.cos(var9));
      return this;
   }

   public Quaterniond rotation(double var1, double var3, double var5) {
      double var7 = var1 * 0.5D;
      double var9 = var3 * 0.5D;
      double var11 = var5 * 0.5D;
      double var13 = var7 * var7 + var9 * var9 + var11 * var11;
      double var15;
      if (var13 * var13 / 24.0D < 9.99999993922529E-9D) {
         this.w = 1.0D - var13 / 2.0D;
         var15 = 1.0D - var13 / 6.0D;
      } else {
         double var17 = Math.sqrt(var13);
         this.w = Math.cos(var17);
         var15 = Math.sin(var17) / var17;
      }

      this.x = var7 * var15;
      this.y = var9 * var15;
      this.z = var11 * var15;
      return this;
   }

   public Quaterniond rotationX(double var1) {
      double var3 = Math.cos(var1 * 0.5D);
      double var5 = Math.sin(var1 * 0.5D);
      this.w = var3;
      this.x = var5;
      this.y = 0.0D;
      this.z = 0.0D;
      return this;
   }

   public Quaterniond rotationY(double var1) {
      double var3 = Math.cos(var1 * 0.5D);
      double var5 = Math.sin(var1 * 0.5D);
      this.w = var3;
      this.x = 0.0D;
      this.y = var5;
      this.z = 0.0D;
      return this;
   }

   public Quaterniond rotationZ(double var1) {
      double var3 = Math.cos(var1 * 0.5D);
      double var5 = Math.sin(var1 * 0.5D);
      this.w = var3;
      this.x = 0.0D;
      this.y = 0.0D;
      this.z = var5;
      return this;
   }

   public Quaterniond rotateTo(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.rotateTo(var1, var3, var5, var7, var9, var11, this);
   }

   public Quaterniond rotateTo(Vector3dc var1, Vector3dc var2, Quaterniond var3) {
      return this.rotateTo(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public Quaterniond rotateTo(Vector3dc var1, Vector3dc var2) {
      return this.rotateTo(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), this);
   }

   public Quaterniond rotate(Vector3dc var1, Quaterniond var2) {
      return this.rotate(var1.x(), var1.y(), var1.z(), var2);
   }

   public Quaterniond rotate(Vector3dc var1) {
      return this.rotate(var1.x(), var1.y(), var1.z(), this);
   }

   public Quaterniond rotate(double var1, double var3, double var5) {
      return this.rotate(var1, var3, var5, this);
   }

   public Quaterniond rotate(double var1, double var3, double var5, Quaterniond var7) {
      double var8 = var1 * 0.5D;
      double var10 = var3 * 0.5D;
      double var12 = var5 * 0.5D;
      double var14 = var8 * var8 + var10 * var10 + var12 * var12;
      double var16;
      double var24;
      if (var14 * var14 / 24.0D < 9.99999993922529E-9D) {
         var24 = 1.0D - var14 / 2.0D;
         var16 = 1.0D - var14 / 6.0D;
      } else {
         double var26 = Math.sqrt(var14);
         var24 = Math.cos(var26);
         var16 = Math.sin(var26) / var26;
      }

      double var18 = var8 * var16;
      double var20 = var10 * var16;
      double var22 = var12 * var16;
      var7.set(this.w * var18 + this.x * var24 + this.y * var22 - this.z * var20, this.w * var20 - this.x * var22 + this.y * var24 + this.z * var18, this.w * var22 + this.x * var20 - this.y * var18 + this.z * var24, this.w * var24 - this.x * var18 - this.y * var20 - this.z * var22);
      return var7;
   }

   public Quaterniond rotateLocal(double var1, double var3, double var5) {
      return this.rotateLocal(var1, var3, var5, this);
   }

   public Quaterniond rotateLocal(double var1, double var3, double var5, Quaterniond var7) {
      double var8 = var1 * 0.5D;
      double var10 = var3 * 0.5D;
      double var12 = var5 * 0.5D;
      double var14 = var8 * var8 + var10 * var10 + var12 * var12;
      double var16;
      double var24;
      if (var14 * var14 / 24.0D < 1.0E-8D) {
         var24 = 1.0D - var14 * 0.5D;
         var16 = 1.0D - var14 / 6.0D;
      } else {
         double var26 = Math.sqrt(var14);
         var24 = Math.cos(var26);
         var16 = Math.sin(var26) / var26;
      }

      double var18 = var8 * var16;
      double var20 = var10 * var16;
      double var22 = var12 * var16;
      var7.set(var24 * this.x + var18 * this.w + var20 * this.z - var22 * this.y, var24 * this.y - var18 * this.z + var20 * this.w + var22 * this.x, var24 * this.z + var18 * this.y - var20 * this.x + var22 * this.w, var24 * this.w - var18 * this.x - var20 * this.y - var22 * this.z);
      return var7;
   }

   public Quaterniond rotateX(double var1) {
      return this.rotateX(var1, this);
   }

   public Quaterniond rotateX(double var1, Quaterniond var3) {
      double var4 = Math.cos(var1 * 0.5D);
      double var6 = Math.sin(var1 * 0.5D);
      var3.set(this.w * var6 + this.x * var4, this.y * var4 + this.z * var6, this.z * var4 - this.y * var6, this.w * var4 - this.x * var6);
      return var3;
   }

   public Quaterniond rotateY(double var1) {
      return this.rotateY(var1, this);
   }

   public Quaterniond rotateY(double var1, Quaterniond var3) {
      double var4 = Math.cos(var1 * 0.5D);
      double var6 = Math.sin(var1 * 0.5D);
      var3.set(this.x * var4 - this.z * var6, this.w * var6 + this.y * var4, this.x * var6 + this.z * var4, this.w * var4 - this.y * var6);
      return var3;
   }

   public Quaterniond rotateZ(double var1) {
      return this.rotateZ(var1, this);
   }

   public Quaterniond rotateZ(double var1, Quaterniond var3) {
      double var4 = Math.cos(var1 * 0.5D);
      double var6 = Math.sin(var1 * 0.5D);
      var3.set(this.x * var4 + this.y * var6, this.y * var4 - this.x * var6, this.w * var6 + this.z * var4, this.w * var4 - this.z * var6);
      return var3;
   }

   public Quaterniond rotateLocalX(double var1) {
      return this.rotateLocalX(var1, this);
   }

   public Quaterniond rotateLocalX(double var1, Quaterniond var3) {
      double var4 = var1 * 0.5D;
      double var6 = Math.sin(var4);
      double var8 = Math.cos(var4);
      var3.set(var8 * this.x + var6 * this.w, var8 * this.y - var6 * this.z, var8 * this.z + var6 * this.y, var8 * this.w - var6 * this.x);
      return var3;
   }

   public Quaterniond rotateLocalY(double var1) {
      return this.rotateLocalY(var1, this);
   }

   public Quaterniond rotateLocalY(double var1, Quaterniond var3) {
      double var4 = var1 * 0.5D;
      double var6 = Math.sin(var4);
      double var8 = Math.cos(var4);
      var3.set(var8 * this.x + var6 * this.z, var8 * this.y + var6 * this.w, var8 * this.z - var6 * this.x, var8 * this.w - var6 * this.y);
      return var3;
   }

   public Quaterniond rotateLocalZ(double var1) {
      return this.rotateLocalZ(var1, this);
   }

   public Quaterniond rotateLocalZ(double var1, Quaterniond var3) {
      double var4 = var1 * 0.5D;
      double var6 = Math.sin(var4);
      double var8 = Math.cos(var4);
      var3.set(var8 * this.x - var6 * this.y, var8 * this.y + var6 * this.x, var8 * this.z + var6 * this.w, var8 * this.w - var6 * this.z);
      return var3;
   }

   public Quaterniond rotateXYZ(double var1, double var3, double var5) {
      return this.rotateXYZ(var1, var3, var5, this);
   }

   public Quaterniond rotateXYZ(double var1, double var3, double var5, Quaterniond var7) {
      double var8 = Math.sin(var1 * 0.5D);
      double var10 = Math.cos(var1 * 0.5D);
      double var12 = Math.sin(var3 * 0.5D);
      double var14 = Math.cos(var3 * 0.5D);
      double var16 = Math.sin(var5 * 0.5D);
      double var18 = Math.cos(var5 * 0.5D);
      double var20 = var14 * var18;
      double var22 = var12 * var16;
      double var24 = var12 * var18;
      double var26 = var14 * var16;
      double var28 = var10 * var20 - var8 * var22;
      double var30 = var8 * var20 + var10 * var22;
      double var32 = var10 * var24 - var8 * var26;
      double var34 = var10 * var26 + var8 * var24;
      var7.set(this.w * var30 + this.x * var28 + this.y * var34 - this.z * var32, this.w * var32 - this.x * var34 + this.y * var28 + this.z * var30, this.w * var34 + this.x * var32 - this.y * var30 + this.z * var28, this.w * var28 - this.x * var30 - this.y * var32 - this.z * var34);
      return var7;
   }

   public Quaterniond rotateZYX(double var1, double var3, double var5) {
      return this.rotateZYX(var1, var3, var5, this);
   }

   public Quaterniond rotateZYX(double var1, double var3, double var5, Quaterniond var7) {
      double var8 = Math.sin(var5 * 0.5D);
      double var10 = Math.cos(var5 * 0.5D);
      double var12 = Math.sin(var3 * 0.5D);
      double var14 = Math.cos(var3 * 0.5D);
      double var16 = Math.sin(var1 * 0.5D);
      double var18 = Math.cos(var1 * 0.5D);
      double var20 = var14 * var18;
      double var22 = var12 * var16;
      double var24 = var12 * var18;
      double var26 = var14 * var16;
      double var28 = var10 * var20 + var8 * var22;
      double var30 = var8 * var20 - var10 * var22;
      double var32 = var10 * var24 + var8 * var26;
      double var34 = var10 * var26 - var8 * var24;
      var7.set(this.w * var30 + this.x * var28 + this.y * var34 - this.z * var32, this.w * var32 - this.x * var34 + this.y * var28 + this.z * var30, this.w * var34 + this.x * var32 - this.y * var30 + this.z * var28, this.w * var28 - this.x * var30 - this.y * var32 - this.z * var34);
      return var7;
   }

   public Quaterniond rotateYXZ(double var1, double var3, double var5) {
      return this.rotateYXZ(var1, var3, var5, this);
   }

   public Quaterniond rotateYXZ(double var1, double var3, double var5, Quaterniond var7) {
      double var8 = Math.sin(var3 * 0.5D);
      double var10 = Math.cos(var3 * 0.5D);
      double var12 = Math.sin(var1 * 0.5D);
      double var14 = Math.cos(var1 * 0.5D);
      double var16 = Math.sin(var5 * 0.5D);
      double var18 = Math.cos(var5 * 0.5D);
      double var20 = var14 * var8;
      double var22 = var12 * var10;
      double var24 = var12 * var8;
      double var26 = var14 * var10;
      double var28 = var20 * var18 + var22 * var16;
      double var30 = var22 * var18 - var20 * var16;
      double var32 = var26 * var16 - var24 * var18;
      double var34 = var26 * var18 + var24 * var16;
      var7.set(this.w * var28 + this.x * var34 + this.y * var32 - this.z * var30, this.w * var30 - this.x * var32 + this.y * var34 + this.z * var28, this.w * var32 + this.x * var30 - this.y * var28 + this.z * var34, this.w * var34 - this.x * var28 - this.y * var30 - this.z * var32);
      return var7;
   }

   public Vector3d getEulerAnglesXYZ(Vector3d var1) {
      var1.x = Math.atan2(2.0D * (this.x * this.w - this.y * this.z), 1.0D - 2.0D * (this.x * this.x + this.y * this.y));
      var1.y = Math.asin(2.0D * (this.x * this.z + this.y * this.w));
      var1.z = Math.atan2(2.0D * (this.z * this.w - this.x * this.y), 1.0D - 2.0D * (this.y * this.y + this.z * this.z));
      return var1;
   }

   public Quaterniond rotateAxis(double var1, double var3, double var5, double var7, Quaterniond var9) {
      double var10 = var1 / 2.0D;
      double var12 = Math.sin(var10);
      double var14 = 1.0D / Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
      double var16 = var3 * var14 * var12;
      double var18 = var5 * var14 * var12;
      double var20 = var7 * var14 * var12;
      double var22 = Math.cos(var10);
      var9.set(this.w * var16 + this.x * var22 + this.y * var20 - this.z * var18, this.w * var18 - this.x * var20 + this.y * var22 + this.z * var16, this.w * var20 + this.x * var18 - this.y * var16 + this.z * var22, this.w * var22 - this.x * var16 - this.y * var18 - this.z * var20);
      return var9;
   }

   public Quaterniond rotateAxis(double var1, Vector3dc var3, Quaterniond var4) {
      return this.rotateAxis(var1, var3.x(), var3.y(), var3.z(), var4);
   }

   public Quaterniond rotateAxis(double var1, Vector3dc var3) {
      return this.rotateAxis(var1, var3.x(), var3.y(), var3.z(), this);
   }

   public Quaterniond rotateAxis(double var1, double var3, double var5, double var7) {
      return this.rotateAxis(var1, var3, var5, var7, this);
   }

   public Vector3d positiveX(Vector3d var1) {
      double var2 = 1.0D / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      double var4 = -this.x * var2;
      double var6 = -this.y * var2;
      double var8 = -this.z * var2;
      double var10 = this.w * var2;
      double var12 = var6 + var6;
      double var14 = var8 + var8;
      var1.x = -var6 * var12 - var8 * var14 + 1.0D;
      var1.y = var4 * var12 + var10 * var14;
      var1.z = var4 * var14 - var10 * var12;
      return var1;
   }

   public Vector3d normalizedPositiveX(Vector3d var1) {
      double var2 = this.y + this.y;
      double var4 = this.z + this.z;
      var1.x = -this.y * var2 - this.z * var4 + 1.0D;
      var1.y = this.x * var2 - this.w * var4;
      var1.z = this.x * var4 + this.w * var2;
      return var1;
   }

   public Vector3d positiveY(Vector3d var1) {
      double var2 = 1.0D / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      double var4 = -this.x * var2;
      double var6 = -this.y * var2;
      double var8 = -this.z * var2;
      double var10 = this.w * var2;
      double var12 = var4 + var4;
      double var14 = var6 + var6;
      double var16 = var8 + var8;
      var1.x = var4 * var14 - var10 * var16;
      var1.y = -var4 * var12 - var8 * var16 + 1.0D;
      var1.z = var6 * var16 + var10 * var12;
      return var1;
   }

   public Vector3d normalizedPositiveY(Vector3d var1) {
      double var2 = this.x + this.x;
      double var4 = this.y + this.y;
      double var6 = this.z + this.z;
      var1.x = this.x * var4 + this.w * var6;
      var1.y = -this.x * var2 - this.z * var6 + 1.0D;
      var1.z = this.y * var6 - this.w * var2;
      return var1;
   }

   public Vector3d positiveZ(Vector3d var1) {
      double var2 = 1.0D / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      double var4 = -this.x * var2;
      double var6 = -this.y * var2;
      double var8 = -this.z * var2;
      double var10 = this.w * var2;
      double var12 = var4 + var4;
      double var14 = var6 + var6;
      double var16 = var8 + var8;
      var1.x = var4 * var16 + var10 * var14;
      var1.y = var6 * var16 - var10 * var12;
      var1.z = -var4 * var12 - var6 * var14 + 1.0D;
      return var1;
   }

   public Vector3d normalizedPositiveZ(Vector3d var1) {
      double var2 = this.x + this.x;
      double var4 = this.y + this.y;
      double var6 = this.z + this.z;
      var1.x = this.x * var6 - this.w * var4;
      var1.y = this.y * var6 + this.w * var2;
      var1.z = -this.x * var2 - this.y * var4 + 1.0D;
      return var1;
   }

   public Quaterniondc toImmutable() {
      return (Quaterniondc)(!Options.DEBUG ? this : new Quaterniond.Proxy(this));
   }

   private final class Proxy implements Quaterniondc {
      private final Quaterniondc delegate;

      Proxy(Quaterniondc var2) {
         this.delegate = var2;
      }

      public double x() {
         return this.delegate.x();
      }

      public double y() {
         return this.delegate.y();
      }

      public double z() {
         return this.delegate.z();
      }

      public double w() {
         return this.delegate.w();
      }

      public Quaterniond normalize(Quaterniond var1) {
         return this.delegate.normalize(var1);
      }

      public Quaterniond add(double var1, double var3, double var5, double var7, Quaterniond var9) {
         return this.delegate.add(var1, var3, var5, var7, var9);
      }

      public Quaterniond add(Quaterniondc var1, Quaterniond var2) {
         return this.delegate.add(var1, var2);
      }

      public double dot(Quaterniondc var1) {
         return this.delegate.dot(var1);
      }

      public double angle() {
         return this.delegate.angle();
      }

      public Matrix3d get(Matrix3d var1) {
         return this.delegate.get(var1);
      }

      public Matrix3f get(Matrix3f var1) {
         return this.delegate.get(var1);
      }

      public Matrix4d get(Matrix4d var1) {
         return this.delegate.get(var1);
      }

      public Matrix4f get(Matrix4f var1) {
         return this.delegate.get(var1);
      }

      public Quaterniond get(Quaterniond var1) {
         return this.delegate.get(var1);
      }

      public Quaterniond mul(Quaterniondc var1, Quaterniond var2) {
         return this.delegate.mul(var1, var2);
      }

      public Quaterniond mul(double var1, double var3, double var5, double var7, Quaterniond var9) {
         return this.delegate.mul(var1, var3, var5, var7, var9);
      }

      public Quaterniond premul(Quaterniondc var1, Quaterniond var2) {
         return this.delegate.premul(var1, var2);
      }

      public Quaterniond premul(double var1, double var3, double var5, double var7, Quaterniond var9) {
         return this.delegate.premul(var1, var3, var5, var7, var9);
      }

      public Vector3d transform(Vector3d var1) {
         return this.delegate.transform(var1);
      }

      public Vector4d transform(Vector4d var1) {
         return this.delegate.transform(var1);
      }

      public Vector3d transform(Vector3dc var1, Vector3d var2) {
         return this.delegate.transform(var1, var2);
      }

      public Vector3d transform(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.transform(var1, var3, var5, var7);
      }

      public Vector4d transform(Vector4dc var1, Vector4d var2) {
         return this.delegate.transform(var1, var2);
      }

      public Vector4d transform(double var1, double var3, double var5, Vector4d var7) {
         return this.delegate.transform(var1, var3, var5, var7);
      }

      public Quaterniond invert(Quaterniond var1) {
         return this.delegate.invert(var1);
      }

      public Quaterniond div(Quaterniondc var1, Quaterniond var2) {
         return this.delegate.div(var1, var2);
      }

      public Quaterniond conjugate(Quaterniond var1) {
         return this.delegate.conjugate(var1);
      }

      public double lengthSquared() {
         return this.delegate.lengthSquared();
      }

      public Quaterniond slerp(Quaterniondc var1, double var2, Quaterniond var4) {
         return this.delegate.slerp(var1, var2, var4);
      }

      public Quaterniond scale(double var1, Quaterniond var3) {
         return this.delegate.scale(var1, var3);
      }

      public Quaterniond integrate(double var1, double var3, double var5, double var7, Quaterniond var9) {
         return this.delegate.integrate(var1, var3, var5, var7, var9);
      }

      public Quaterniond nlerp(Quaterniondc var1, double var2, Quaterniond var4) {
         return this.delegate.nlerp(var1, var2, var4);
      }

      public Quaterniond nlerpIterative(Quaterniondc var1, double var2, double var4, Quaterniond var6) {
         return this.delegate.nlerpIterative(var1, var2, var4, var6);
      }

      public Quaterniond lookAlong(Vector3dc var1, Vector3dc var2, Quaterniond var3) {
         return this.delegate.lookAlong(var1, var2, var3);
      }

      public Quaterniond lookAlong(double var1, double var3, double var5, double var7, double var9, double var11, Quaterniond var13) {
         return this.delegate.lookAlong(var1, var3, var5, var7, var9, var11, var13);
      }

      public Quaterniond difference(Quaterniondc var1, Quaterniond var2) {
         return this.delegate.difference(var1, var2);
      }

      public Quaterniond rotateTo(double var1, double var3, double var5, double var7, double var9, double var11, Quaterniond var13) {
         return this.delegate.rotateTo(var1, var3, var5, var7, var9, var11, var13);
      }

      public Quaterniond rotateTo(Vector3dc var1, Vector3dc var2, Quaterniond var3) {
         return this.delegate.rotateTo(var1, var2, var3);
      }

      public Quaterniond rotate(Vector3dc var1, Quaterniond var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Quaterniond rotate(double var1, double var3, double var5, Quaterniond var7) {
         return this.delegate.rotate(var1, var3, var5, var7);
      }

      public Quaterniond rotateLocal(double var1, double var3, double var5, Quaterniond var7) {
         return this.delegate.rotateLocal(var1, var3, var5, var7);
      }

      public Quaterniond rotateX(double var1, Quaterniond var3) {
         return this.delegate.rotateX(var1, var3);
      }

      public Quaterniond rotateY(double var1, Quaterniond var3) {
         return this.delegate.rotateY(var1, var3);
      }

      public Quaterniond rotateZ(double var1, Quaterniond var3) {
         return this.delegate.rotateZ(var1, var3);
      }

      public Quaterniond rotateLocalX(double var1, Quaterniond var3) {
         return this.delegate.rotateLocalX(var1, var3);
      }

      public Quaterniond rotateLocalY(double var1, Quaterniond var3) {
         return this.delegate.rotateLocalY(var1, var3);
      }

      public Quaterniond rotateLocalZ(double var1, Quaterniond var3) {
         return this.delegate.rotateLocalZ(var1, var3);
      }

      public Quaterniond rotateXYZ(double var1, double var3, double var5, Quaterniond var7) {
         return this.delegate.rotateXYZ(var1, var3, var5, var7);
      }

      public Quaterniond rotateZYX(double var1, double var3, double var5, Quaterniond var7) {
         return this.delegate.rotateZYX(var1, var3, var5, var7);
      }

      public Quaterniond rotateYXZ(double var1, double var3, double var5, Quaterniond var7) {
         return this.delegate.rotateYXZ(var1, var3, var5, var7);
      }

      public Vector3d getEulerAnglesXYZ(Vector3d var1) {
         return this.delegate.getEulerAnglesXYZ(var1);
      }

      public Quaterniond rotateAxis(double var1, double var3, double var5, double var7, Quaterniond var9) {
         return this.delegate.rotateAxis(var1, var3, var5, var7, var9);
      }

      public Quaterniond rotateAxis(double var1, Vector3dc var3, Quaterniond var4) {
         return this.delegate.rotateAxis(var1, var3, var4);
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

      public Vector3d positiveZ(Vector3d var1) {
         return this.delegate.positiveZ(var1);
      }

      public Vector3d normalizedPositiveZ(Vector3d var1) {
         return this.delegate.normalizedPositiveZ(var1);
      }
   }
}
