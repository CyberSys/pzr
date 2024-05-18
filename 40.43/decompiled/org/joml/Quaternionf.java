package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Quaternionf implements Externalizable, Quaternionfc {
   private static final long serialVersionUID = 1L;
   public float x;
   public float y;
   public float z;
   public float w;

   public Quaternionf() {
      MemUtil.INSTANCE.identity(this);
   }

   public Quaternionf(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.w = var4;
   }

   public Quaternionf(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.w = 1.0F;
   }

   public Quaternionf(Quaternionf var1) {
      MemUtil.INSTANCE.copy(var1, this);
   }

   public Quaternionf(AxisAngle4f var1) {
      float var2 = (float)Math.sin((double)var1.angle / 2.0D);
      float var3 = (float)Math.cos((double)var1.angle / 2.0D);
      this.x = var1.x * var2;
      this.y = var1.y * var2;
      this.z = var1.z * var2;
      this.w = var3;
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public float z() {
      return this.z;
   }

   public float w() {
      return this.w;
   }

   public Quaternionf normalize() {
      float var1 = (float)(1.0D / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w)));
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      this.w *= var1;
      return this;
   }

   public Quaternionf normalize(Quaternionf var1) {
      float var2 = (float)(1.0D / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w)));
      var1.x = this.x * var2;
      var1.y = this.y * var2;
      var1.z = this.z * var2;
      var1.w = this.w * var2;
      return var1;
   }

   public Quaternionf add(float var1, float var2, float var3, float var4) {
      return this.add(var1, var2, var3, var4, this);
   }

   public Quaternionf add(float var1, float var2, float var3, float var4, Quaternionf var5) {
      var5.x = this.x + var1;
      var5.y = this.y + var2;
      var5.z = this.z + var3;
      var5.w = this.w + var4;
      return var5;
   }

   public Quaternionf add(Quaternionfc var1) {
      this.x += var1.x();
      this.y += var1.y();
      this.z += var1.z();
      this.w += var1.w();
      return this;
   }

   public Quaternionf add(Quaternionfc var1, Quaternionf var2) {
      var2.x = this.x + var1.x();
      var2.y = this.y + var1.y();
      var2.z = this.z + var1.z();
      var2.w = this.w + var1.w();
      return var2;
   }

   public float dot(Quaternionf var1) {
      return this.x * var1.x + this.y * var1.y + this.z * var1.z + this.w * var1.w;
   }

   public float angle() {
      float var1 = (float)(2.0D * Math.acos((double)this.w));
      return (float)((double)var1 <= 3.141592653589793D ? (double)var1 : 6.283185307179586D - (double)var1);
   }

   public Matrix3f get(Matrix3f var1) {
      return var1.set((Quaternionfc)this);
   }

   public Matrix3d get(Matrix3d var1) {
      return var1.set((Quaternionfc)this);
   }

   public Matrix4f get(Matrix4f var1) {
      return var1.set((Quaternionfc)this);
   }

   public Matrix4d get(Matrix4d var1) {
      return var1.set((Quaternionfc)this);
   }

   public Matrix4x3f get(Matrix4x3f var1) {
      return var1.set((Quaternionfc)this);
   }

   public Matrix4x3d get(Matrix4x3d var1) {
      return var1.set((Quaternionfc)this);
   }

   public AxisAngle4f get(AxisAngle4f var1) {
      float var2 = this.x;
      float var3 = this.y;
      float var4 = this.z;
      float var5 = this.w;
      float var6;
      if (var5 > 1.0F) {
         var6 = (float)(1.0D / Math.sqrt((double)(var2 * var2 + var3 * var3 + var4 * var4 + var5 * var5)));
         var2 *= var6;
         var3 *= var6;
         var4 *= var6;
         var5 *= var6;
      }

      var1.angle = (float)(2.0D * Math.acos((double)var5));
      var6 = (float)Math.sqrt(1.0D - (double)(var5 * var5));
      if (var6 < 0.001F) {
         var1.x = var2;
         var1.y = var3;
         var1.z = var4;
      } else {
         var6 = 1.0F / var6;
         var1.x = var2 * var6;
         var1.y = var3 * var6;
         var1.z = var4 * var6;
      }

      return var1;
   }

   public Quaterniond get(Quaterniond var1) {
      return var1.set((Quaternionfc)this);
   }

   public Quaternionf get(Quaternionf var1) {
      return var1.set((Quaternionfc)this);
   }

   public ByteBuffer getAsMatrix3f(ByteBuffer var1) {
      MemUtil.INSTANCE.putMatrix3f(this, var1.position(), var1);
      return var1;
   }

   public FloatBuffer getAsMatrix3f(FloatBuffer var1) {
      MemUtil.INSTANCE.putMatrix3f(this, var1.position(), var1);
      return var1;
   }

   public ByteBuffer getAsMatrix4f(ByteBuffer var1) {
      MemUtil.INSTANCE.putMatrix4f(this, var1.position(), var1);
      return var1;
   }

   public FloatBuffer getAsMatrix4f(FloatBuffer var1) {
      MemUtil.INSTANCE.putMatrix4f(this, var1.position(), var1);
      return var1;
   }

   public ByteBuffer getAsMatrix4x3f(ByteBuffer var1) {
      MemUtil.INSTANCE.putMatrix4x3f(this, var1.position(), var1);
      return var1;
   }

   public FloatBuffer getAsMatrix4x3f(FloatBuffer var1) {
      MemUtil.INSTANCE.putMatrix4x3f(this, var1.position(), var1);
      return var1;
   }

   public Quaternionf set(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.w = var4;
      return this;
   }

   public Quaternionf set(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      return this;
   }

   public Quaternionf set(Quaternionfc var1) {
      if (var1 instanceof Quaternionf) {
         MemUtil.INSTANCE.copy((Quaternionf)var1, this);
      } else {
         this.x = var1.x();
         this.y = var1.y();
         this.z = var1.z();
         this.w = var1.w();
      }

      return this;
   }

   public Quaternionf set(AxisAngle4f var1) {
      return this.setAngleAxis(var1.angle, var1.x, var1.y, var1.z);
   }

   public Quaternionf set(AxisAngle4d var1) {
      return this.setAngleAxis(var1.angle, var1.x, var1.y, var1.z);
   }

   public Quaternionf setAngleAxis(float var1, float var2, float var3, float var4) {
      float var5 = (float)Math.sin((double)var1 * 0.5D);
      this.x = var2 * var5;
      this.y = var3 * var5;
      this.z = var4 * var5;
      this.w = (float)Math.cos((double)var1 * 0.5D);
      return this;
   }

   public Quaternionf setAngleAxis(double var1, double var3, double var5, double var7) {
      double var9 = Math.sin(var1 * 0.5D);
      this.x = (float)(var3 * var9);
      this.y = (float)(var5 * var9);
      this.z = (float)(var7 * var9);
      this.w = (float)Math.cos(var1 * 0.5D);
      return this;
   }

   public Quaternionf rotationAxis(AxisAngle4f var1) {
      return this.rotationAxis(var1.angle, var1.x, var1.y, var1.z);
   }

   public Quaternionf rotationAxis(float var1, float var2, float var3, float var4) {
      float var5 = var1 / 2.0F;
      float var6 = (float)Math.sin((double)var5);
      float var7 = (float)(1.0D / Math.sqrt((double)(var2 * var2 + var3 * var3 + var4 * var4)));
      this.x = var2 * var7 * var6;
      this.y = var3 * var7 * var6;
      this.z = var4 * var7 * var6;
      this.w = (float)Math.cos((double)var5);
      return this;
   }

   public Quaternionf rotationAxis(float var1, Vector3fc var2) {
      return this.rotationAxis(var1, var2.x(), var2.y(), var2.z());
   }

   public Quaternionf rotation(float var1, float var2, float var3) {
      double var4 = (double)var1 * 0.5D;
      double var6 = (double)var2 * 0.5D;
      double var8 = (double)var3 * 0.5D;
      double var10 = var4 * var4 + var6 * var6 + var8 * var8;
      double var12;
      if (var10 * var10 / 24.0D < 9.99999993922529E-9D) {
         this.w = (float)(1.0D - var10 / 2.0D);
         var12 = 1.0D - var10 / 6.0D;
      } else {
         double var14 = Math.sqrt(var10);
         this.w = (float)Math.cos(var14);
         var12 = Math.sin(var14) / var14;
      }

      this.x = (float)(var4 * var12);
      this.y = (float)(var6 * var12);
      this.z = (float)(var8 * var12);
      return this;
   }

   public Quaternionf rotationX(float var1) {
      float var2 = (float)Math.cos((double)var1 * 0.5D);
      float var3 = (float)Math.sin((double)var1 * 0.5D);
      this.w = var2;
      this.x = var3;
      this.y = 0.0F;
      this.z = 0.0F;
      return this;
   }

   public Quaternionf rotationY(float var1) {
      float var2 = (float)Math.cos((double)var1 * 0.5D);
      float var3 = (float)Math.sin((double)var1 * 0.5D);
      this.w = var2;
      this.x = 0.0F;
      this.y = var3;
      this.z = 0.0F;
      return this;
   }

   public Quaternionf rotationZ(float var1) {
      float var2 = (float)Math.cos((double)var1 * 0.5D);
      float var3 = (float)Math.sin((double)var1 * 0.5D);
      this.w = var2;
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = var3;
      return this;
   }

   private void setFromUnnormalized(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var19 = (float)(1.0D / Math.sqrt((double)(var1 * var1 + var2 * var2 + var3 * var3)));
      float var20 = (float)(1.0D / Math.sqrt((double)(var4 * var4 + var5 * var5 + var6 * var6)));
      float var21 = (float)(1.0D / Math.sqrt((double)(var7 * var7 + var8 * var8 + var9 * var9)));
      float var10 = var1 * var19;
      float var11 = var2 * var19;
      float var12 = var3 * var19;
      float var13 = var4 * var20;
      float var14 = var5 * var20;
      float var15 = var6 * var20;
      float var16 = var7 * var21;
      float var17 = var8 * var21;
      float var18 = var9 * var21;
      this.setFromNormalized(var10, var11, var12, var13, var14, var15, var16, var17, var18);
   }

   private void setFromNormalized(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var11 = var1 + var5 + var9;
      float var10;
      if (var11 >= 0.0F) {
         var10 = (float)Math.sqrt((double)(var11 + 1.0F));
         this.w = var10 * 0.5F;
         var10 = 0.5F / var10;
         this.x = (var6 - var8) * var10;
         this.y = (var7 - var3) * var10;
         this.z = (var2 - var4) * var10;
      } else if (var1 >= var5 && var1 >= var9) {
         var10 = (float)Math.sqrt((double)(var1 - (var5 + var9)) + 1.0D);
         this.x = var10 * 0.5F;
         var10 = 0.5F / var10;
         this.y = (var4 + var2) * var10;
         this.z = (var3 + var7) * var10;
         this.w = (var6 - var8) * var10;
      } else if (var5 > var9) {
         var10 = (float)Math.sqrt((double)(var5 - (var9 + var1)) + 1.0D);
         this.y = var10 * 0.5F;
         var10 = 0.5F / var10;
         this.z = (var8 + var6) * var10;
         this.x = (var4 + var2) * var10;
         this.w = (var7 - var3) * var10;
      } else {
         var10 = (float)Math.sqrt((double)(var9 - (var1 + var5)) + 1.0D);
         this.z = var10 * 0.5F;
         var10 = 0.5F / var10;
         this.x = (var3 + var7) * var10;
         this.y = (var8 + var6) * var10;
         this.w = (var2 - var4) * var10;
      }

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
         this.w = (float)(var19 * 0.5D);
         var19 = 0.5D / var19;
         this.x = (float)((var11 - var15) * var19);
         this.y = (float)((var13 - var5) * var19);
         this.z = (float)((var3 - var7) * var19);
      } else if (var1 >= var9 && var1 >= var17) {
         var19 = Math.sqrt(var1 - (var9 + var17) + 1.0D);
         this.x = (float)(var19 * 0.5D);
         var19 = 0.5D / var19;
         this.y = (float)((var7 + var3) * var19);
         this.z = (float)((var5 + var13) * var19);
         this.w = (float)((var11 - var15) * var19);
      } else if (var9 > var17) {
         var19 = (double)((float)Math.sqrt(var9 - (var17 + var1) + 1.0D));
         this.y = (float)(var19 * 0.5D);
         var19 = 0.5D / var19;
         this.z = (float)((var15 + var11) * var19);
         this.x = (float)((var7 + var3) * var19);
         this.w = (float)((var13 - var5) * var19);
      } else {
         var19 = (double)((float)Math.sqrt(var17 - (var1 + var9) + 1.0D));
         this.z = (float)(var19 * 0.5D);
         var19 = 0.5D / var19;
         this.x = (float)((var5 + var13) * var19);
         this.y = (float)((var15 + var11) * var19);
         this.w = (float)((var3 - var7) * var19);
      }

   }

   public Quaternionf setFromUnnormalized(Matrix4fc var1) {
      this.setFromUnnormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf setFromUnnormalized(Matrix4x3fc var1) {
      this.setFromUnnormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf setFromUnnormalized(Matrix4x3dc var1) {
      this.setFromUnnormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf setFromNormalized(Matrix4fc var1) {
      this.setFromNormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf setFromNormalized(Matrix4x3fc var1) {
      this.setFromNormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf setFromNormalized(Matrix4x3dc var1) {
      this.setFromNormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf setFromUnnormalized(Matrix4dc var1) {
      this.setFromUnnormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf setFromNormalized(Matrix4dc var1) {
      this.setFromNormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf setFromUnnormalized(Matrix3fc var1) {
      this.setFromUnnormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf setFromNormalized(Matrix3fc var1) {
      this.setFromNormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf setFromUnnormalized(Matrix3dc var1) {
      this.setFromUnnormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf setFromNormalized(Matrix3dc var1) {
      this.setFromNormalized(var1.m00(), var1.m01(), var1.m02(), var1.m10(), var1.m11(), var1.m12(), var1.m20(), var1.m21(), var1.m22());
      return this;
   }

   public Quaternionf fromAxisAngleRad(Vector3fc var1, float var2) {
      return this.fromAxisAngleRad(var1.x(), var1.y(), var1.z(), var2);
   }

   public Quaternionf fromAxisAngleRad(float var1, float var2, float var3, float var4) {
      float var5 = var4 / 2.0F;
      float var6 = (float)Math.sin((double)var5);
      float var7 = (float)Math.sqrt((double)(var1 * var1 + var2 * var2 + var3 * var3));
      this.x = var1 / var7 * var6;
      this.y = var2 / var7 * var6;
      this.z = var3 / var7 * var6;
      this.w = (float)Math.cos((double)var5);
      return this;
   }

   public Quaternionf fromAxisAngleDeg(Vector3fc var1, float var2) {
      return this.fromAxisAngleRad(var1.x(), var1.y(), var1.z(), (float)Math.toRadians((double)var2));
   }

   public Quaternionf fromAxisAngleDeg(float var1, float var2, float var3, float var4) {
      return this.fromAxisAngleRad(var1, var2, var3, (float)Math.toRadians((double)var4));
   }

   public Quaternionf mul(Quaternionfc var1) {
      return this.mul(var1, this);
   }

   public Quaternionf mul(Quaternionfc var1, Quaternionf var2) {
      var2.set(this.w * var1.x() + this.x * var1.w() + this.y * var1.z() - this.z * var1.y(), this.w * var1.y() - this.x * var1.z() + this.y * var1.w() + this.z * var1.x(), this.w * var1.z() + this.x * var1.y() - this.y * var1.x() + this.z * var1.w(), this.w * var1.w() - this.x * var1.x() - this.y * var1.y() - this.z * var1.z());
      return var2;
   }

   public Quaternionf mul(float var1, float var2, float var3, float var4) {
      this.set(this.w * var1 + this.x * var4 + this.y * var3 - this.z * var2, this.w * var2 - this.x * var3 + this.y * var4 + this.z * var1, this.w * var3 + this.x * var2 - this.y * var1 + this.z * var4, this.w * var4 - this.x * var1 - this.y * var2 - this.z * var3);
      return this;
   }

   public Quaternionf mul(float var1, float var2, float var3, float var4, Quaternionf var5) {
      var5.set(this.w * var1 + this.x * var4 + this.y * var3 - this.z * var2, this.w * var2 - this.x * var3 + this.y * var4 + this.z * var1, this.w * var3 + this.x * var2 - this.y * var1 + this.z * var4, this.w * var4 - this.x * var1 - this.y * var2 - this.z * var3);
      return var5;
   }

   public Quaternionf premul(Quaternionfc var1) {
      return this.premul(var1, this);
   }

   public Quaternionf premul(Quaternionfc var1, Quaternionf var2) {
      var2.set(var1.w() * this.x + var1.x() * this.w + var1.y() * this.z - var1.z() * this.y, var1.w() * this.y - var1.x() * this.z + var1.y() * this.w + var1.z() * this.x, var1.w() * this.z + var1.x() * this.y - var1.y() * this.x + var1.z() * this.w, var1.w() * this.w - var1.x() * this.x - var1.y() * this.y - var1.z() * this.z);
      return var2;
   }

   public Quaternionf premul(float var1, float var2, float var3, float var4) {
      return this.premul(var1, var2, var3, var4, this);
   }

   public Quaternionf premul(float var1, float var2, float var3, float var4, Quaternionf var5) {
      var5.set(var4 * this.x + var1 * this.w + var2 * this.z - var3 * this.y, var4 * this.y - var1 * this.z + var2 * this.w + var3 * this.x, var4 * this.z + var1 * this.y - var2 * this.x + var3 * this.w, var4 * this.w - var1 * this.x - var2 * this.y - var3 * this.z);
      return var5;
   }

   public Vector3f transform(Vector3f var1) {
      return this.transform(var1.x, var1.y, var1.z, var1);
   }

   public Vector4f transform(Vector4f var1) {
      return this.transform((Vector4fc)var1, (Vector4f)var1);
   }

   public Vector3f transform(Vector3fc var1, Vector3f var2) {
      return this.transform(var1.x(), var1.y(), var1.z(), var2);
   }

   public Vector3f transform(float var1, float var2, float var3, Vector3f var4) {
      float var5 = this.w * this.w;
      float var6 = this.x * this.x;
      float var7 = this.y * this.y;
      float var8 = this.z * this.z;
      float var9 = this.z * this.w;
      float var10 = this.x * this.y;
      float var11 = this.x * this.z;
      float var12 = this.y * this.w;
      float var13 = this.y * this.z;
      float var14 = this.x * this.w;
      float var15 = var5 + var6 - var8 - var7;
      float var16 = var10 + var9 + var9 + var10;
      float var17 = var11 - var12 + var11 - var12;
      float var18 = -var9 + var10 - var9 + var10;
      float var19 = var7 - var8 + var5 - var6;
      float var20 = var13 + var13 + var14 + var14;
      float var21 = var12 + var11 + var11 + var12;
      float var22 = var13 + var13 - var14 - var14;
      float var23 = var8 - var7 - var6 + var5;
      var4.x = var15 * var1 + var18 * var2 + var21 * var3;
      var4.y = var16 * var1 + var19 * var2 + var22 * var3;
      var4.z = var17 * var1 + var20 * var2 + var23 * var3;
      return var4;
   }

   public Vector4f transform(Vector4fc var1, Vector4f var2) {
      return this.transform(var1.x(), var1.y(), var1.z(), var2);
   }

   public Vector4f transform(float var1, float var2, float var3, Vector4f var4) {
      float var5 = this.w * this.w;
      float var6 = this.x * this.x;
      float var7 = this.y * this.y;
      float var8 = this.z * this.z;
      float var9 = this.z * this.w;
      float var10 = this.x * this.y;
      float var11 = this.x * this.z;
      float var12 = this.y * this.w;
      float var13 = this.y * this.z;
      float var14 = this.x * this.w;
      float var15 = var5 + var6 - var8 - var7;
      float var16 = var10 + var9 + var9 + var10;
      float var17 = var11 - var12 + var11 - var12;
      float var18 = -var9 + var10 - var9 + var10;
      float var19 = var7 - var8 + var5 - var6;
      float var20 = var13 + var13 + var14 + var14;
      float var21 = var12 + var11 + var11 + var12;
      float var22 = var13 + var13 - var14 - var14;
      float var23 = var8 - var7 - var6 + var5;
      var4.x = var15 * var1 + var18 * var2 + var21 * var3;
      var4.y = var16 * var1 + var19 * var2 + var22 * var3;
      var4.z = var17 * var1 + var20 * var2 + var23 * var3;
      return var4;
   }

   public Quaternionf invert(Quaternionf var1) {
      float var2 = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      var1.x = -this.x * var2;
      var1.y = -this.y * var2;
      var1.z = -this.z * var2;
      var1.w = this.w * var2;
      return var1;
   }

   public Quaternionf invert() {
      return this.invert(this);
   }

   public Quaternionf div(Quaternionfc var1, Quaternionf var2) {
      float var3 = 1.0F / (var1.x() * var1.x() + var1.y() * var1.y() + var1.z() * var1.z() + var1.w() * var1.w());
      float var4 = -var1.x() * var3;
      float var5 = -var1.y() * var3;
      float var6 = -var1.z() * var3;
      float var7 = var1.w() * var3;
      var2.set(this.w * var4 + this.x * var7 + this.y * var6 - this.z * var5, this.w * var5 - this.x * var6 + this.y * var7 + this.z * var4, this.w * var6 + this.x * var5 - this.y * var4 + this.z * var7, this.w * var7 - this.x * var4 - this.y * var5 - this.z * var6);
      return var2;
   }

   public Quaternionf div(Quaternionfc var1) {
      return this.div(var1, this);
   }

   public Quaternionf conjugate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
      return this;
   }

   public Quaternionf conjugate(Quaternionf var1) {
      var1.x = -this.x;
      var1.y = -this.y;
      var1.z = -this.z;
      var1.w = this.w;
      return var1;
   }

   public Quaternionf identity() {
      MemUtil.INSTANCE.identity(this);
      return this;
   }

   public Quaternionf rotateXYZ(float var1, float var2, float var3) {
      return this.rotateXYZ(var1, var2, var3, this);
   }

   public Quaternionf rotateXYZ(float var1, float var2, float var3, Quaternionf var4) {
      float var5 = (float)Math.sin((double)var1 * 0.5D);
      float var6 = (float)Math.cos((double)var1 * 0.5D);
      float var7 = (float)Math.sin((double)var2 * 0.5D);
      float var8 = (float)Math.cos((double)var2 * 0.5D);
      float var9 = (float)Math.sin((double)var3 * 0.5D);
      float var10 = (float)Math.cos((double)var3 * 0.5D);
      float var11 = var8 * var10;
      float var12 = var7 * var9;
      float var13 = var7 * var10;
      float var14 = var8 * var9;
      float var15 = var6 * var11 - var5 * var12;
      float var16 = var5 * var11 + var6 * var12;
      float var17 = var6 * var13 - var5 * var14;
      float var18 = var6 * var14 + var5 * var13;
      var4.set(this.w * var16 + this.x * var15 + this.y * var18 - this.z * var17, this.w * var17 - this.x * var18 + this.y * var15 + this.z * var16, this.w * var18 + this.x * var17 - this.y * var16 + this.z * var15, this.w * var15 - this.x * var16 - this.y * var17 - this.z * var18);
      return var4;
   }

   public Quaternionf rotateZYX(float var1, float var2, float var3) {
      return this.rotateZYX(var1, var2, var3, this);
   }

   public Quaternionf rotateZYX(float var1, float var2, float var3, Quaternionf var4) {
      float var5 = (float)Math.sin((double)var3 * 0.5D);
      float var6 = (float)Math.cos((double)var3 * 0.5D);
      float var7 = (float)Math.sin((double)var2 * 0.5D);
      float var8 = (float)Math.cos((double)var2 * 0.5D);
      float var9 = (float)Math.sin((double)var1 * 0.5D);
      float var10 = (float)Math.cos((double)var1 * 0.5D);
      float var11 = var8 * var10;
      float var12 = var7 * var9;
      float var13 = var7 * var10;
      float var14 = var8 * var9;
      float var15 = var6 * var11 + var5 * var12;
      float var16 = var5 * var11 - var6 * var12;
      float var17 = var6 * var13 + var5 * var14;
      float var18 = var6 * var14 - var5 * var13;
      var4.set(this.w * var16 + this.x * var15 + this.y * var18 - this.z * var17, this.w * var17 - this.x * var18 + this.y * var15 + this.z * var16, this.w * var18 + this.x * var17 - this.y * var16 + this.z * var15, this.w * var15 - this.x * var16 - this.y * var17 - this.z * var18);
      return var4;
   }

   public Quaternionf rotateYXZ(float var1, float var2, float var3) {
      return this.rotateYXZ(var1, var2, var3, this);
   }

   public Quaternionf rotateYXZ(float var1, float var2, float var3, Quaternionf var4) {
      float var5 = (float)Math.sin((double)var2 * 0.5D);
      float var6 = (float)Math.cos((double)var2 * 0.5D);
      float var7 = (float)Math.sin((double)var1 * 0.5D);
      float var8 = (float)Math.cos((double)var1 * 0.5D);
      float var9 = (float)Math.sin((double)var3 * 0.5D);
      float var10 = (float)Math.cos((double)var3 * 0.5D);
      float var11 = var8 * var5;
      float var12 = var7 * var6;
      float var13 = var7 * var5;
      float var14 = var8 * var6;
      float var15 = var11 * var10 + var12 * var9;
      float var16 = var12 * var10 - var11 * var9;
      float var17 = var14 * var9 - var13 * var10;
      float var18 = var14 * var10 + var13 * var9;
      var4.set(this.w * var15 + this.x * var18 + this.y * var17 - this.z * var16, this.w * var16 - this.x * var17 + this.y * var18 + this.z * var15, this.w * var17 + this.x * var16 - this.y * var15 + this.z * var18, this.w * var18 - this.x * var15 - this.y * var16 - this.z * var17);
      return var4;
   }

   public Vector3f getEulerAnglesXYZ(Vector3f var1) {
      var1.x = (float)Math.atan2(2.0D * (double)(this.x * this.w - this.y * this.z), 1.0D - 2.0D * (double)(this.x * this.x + this.y * this.y));
      var1.y = (float)Math.asin(2.0D * (double)(this.x * this.z + this.y * this.w));
      var1.z = (float)Math.atan2(2.0D * (double)(this.z * this.w - this.x * this.y), 1.0D - 2.0D * (double)(this.y * this.y + this.z * this.z));
      return var1;
   }

   public float lengthSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
   }

   public Quaternionf rotationXYZ(float var1, float var2, float var3) {
      float var4 = (float)Math.sin((double)var1 * 0.5D);
      float var5 = (float)Math.cos((double)var1 * 0.5D);
      float var6 = (float)Math.sin((double)var2 * 0.5D);
      float var7 = (float)Math.cos((double)var2 * 0.5D);
      float var8 = (float)Math.sin((double)var3 * 0.5D);
      float var9 = (float)Math.cos((double)var3 * 0.5D);
      float var10 = var7 * var9;
      float var11 = var6 * var8;
      float var12 = var6 * var9;
      float var13 = var7 * var8;
      this.w = var5 * var10 - var4 * var11;
      this.x = var4 * var10 + var5 * var11;
      this.y = var5 * var12 - var4 * var13;
      this.z = var5 * var13 + var4 * var12;
      return this;
   }

   public Quaternionf rotationZYX(float var1, float var2, float var3) {
      float var4 = (float)Math.sin((double)var3 * 0.5D);
      float var5 = (float)Math.cos((double)var3 * 0.5D);
      float var6 = (float)Math.sin((double)var2 * 0.5D);
      float var7 = (float)Math.cos((double)var2 * 0.5D);
      float var8 = (float)Math.sin((double)var1 * 0.5D);
      float var9 = (float)Math.cos((double)var1 * 0.5D);
      float var10 = var7 * var9;
      float var11 = var6 * var8;
      float var12 = var6 * var9;
      float var13 = var7 * var8;
      this.w = var5 * var10 + var4 * var11;
      this.x = var4 * var10 - var5 * var11;
      this.y = var5 * var12 + var4 * var13;
      this.z = var5 * var13 - var4 * var12;
      return this;
   }

   public Quaternionf rotationYXZ(float var1, float var2, float var3) {
      float var4 = (float)Math.sin((double)var2 * 0.5D);
      float var5 = (float)Math.cos((double)var2 * 0.5D);
      float var6 = (float)Math.sin((double)var1 * 0.5D);
      float var7 = (float)Math.cos((double)var1 * 0.5D);
      float var8 = (float)Math.sin((double)var3 * 0.5D);
      float var9 = (float)Math.cos((double)var3 * 0.5D);
      float var10 = var7 * var4;
      float var11 = var6 * var5;
      float var12 = var6 * var4;
      float var13 = var7 * var5;
      this.x = var10 * var9 + var11 * var8;
      this.y = var11 * var9 - var10 * var8;
      this.z = var13 * var8 - var12 * var9;
      this.w = var13 * var9 + var12 * var8;
      return this;
   }

   public Quaternionf slerp(Quaternionfc var1, float var2) {
      return this.slerp(var1, var2, this);
   }

   public Quaternionf slerp(Quaternionfc var1, float var2, Quaternionf var3) {
      float var4 = this.x * var1.x() + this.y * var1.y() + this.z * var1.z() + this.w * var1.w();
      float var5 = Math.abs(var4);
      float var6;
      float var7;
      if (1.0F - var5 > 1.0E-6F) {
         float var8 = 1.0F - var5 * var5;
         float var9 = (float)(1.0D / Math.sqrt((double)var8));
         float var10 = (float)Math.atan2((double)(var8 * var9), (double)var5);
         var6 = (float)(Math.sin((1.0D - (double)var2) * (double)var10) * (double)var9);
         var7 = (float)(Math.sin((double)(var2 * var10)) * (double)var9);
      } else {
         var6 = 1.0F - var2;
         var7 = var2;
      }

      var7 = var4 >= 0.0F ? var7 : -var7;
      var3.x = var6 * this.x + var7 * var1.x();
      var3.y = var6 * this.y + var7 * var1.y();
      var3.z = var6 * this.z + var7 * var1.z();
      var3.w = var6 * this.w + var7 * var1.w();
      return var3;
   }

   public static Quaternionfc slerp(Quaternionf[] var0, float[] var1, Quaternionf var2) {
      var2.set((Quaternionfc)var0[0]);
      float var3 = var1[0];

      for(int var4 = 1; var4 < var0.length; ++var4) {
         float var6 = var1[var4];
         float var7 = var6 / (var3 + var6);
         var3 += var6;
         var2.slerp(var0[var4], var7);
      }

      return var2;
   }

   public Quaternionf scale(float var1) {
      return this.scale(var1, this);
   }

   public Quaternionf scale(float var1, Quaternionf var2) {
      float var3 = (float)Math.sqrt((double)var1);
      var2.x = var3 * this.x;
      var2.y = var3 * this.y;
      var2.z = var3 * this.z;
      var2.w = var3 * this.w;
      return this;
   }

   public Quaternionf scaling(float var1) {
      float var2 = (float)Math.sqrt((double)var1);
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = 0.0F;
      this.w = var2;
      return this;
   }

   public Quaternionf integrate(float var1, float var2, float var3, float var4) {
      return this.integrate(var1, var2, var3, var4, this);
   }

   public Quaternionf integrate(float var1, float var2, float var3, float var4, Quaternionf var5) {
      return this.rotateLocal(var1 * var2, var1 * var3, var1 * var4, var5);
   }

   public Quaternionf nlerp(Quaternionfc var1, float var2) {
      return this.nlerp(var1, var2, this);
   }

   public Quaternionf nlerp(Quaternionfc var1, float var2, Quaternionf var3) {
      float var4 = this.x * var1.x() + this.y * var1.y() + this.z * var1.z() + this.w * var1.w();
      float var5 = 1.0F - var2;
      float var6 = var4 >= 0.0F ? var2 : -var2;
      var3.x = var5 * this.x + var6 * var1.x();
      var3.y = var5 * this.y + var6 * var1.y();
      var3.z = var5 * this.z + var6 * var1.z();
      var3.w = var5 * this.w + var6 * var1.w();
      float var7 = (float)(1.0D / Math.sqrt((double)(var3.x * var3.x + var3.y * var3.y + var3.z * var3.z + var3.w * var3.w)));
      var3.x *= var7;
      var3.y *= var7;
      var3.z *= var7;
      var3.w *= var7;
      return var3;
   }

   public static Quaternionfc nlerp(Quaternionfc[] var0, float[] var1, Quaternionf var2) {
      var2.set(var0[0]);
      float var3 = var1[0];

      for(int var4 = 1; var4 < var0.length; ++var4) {
         float var6 = var1[var4];
         float var7 = var6 / (var3 + var6);
         var3 += var6;
         var2.nlerp(var0[var4], var7);
      }

      return var2;
   }

   public Quaternionf nlerpIterative(Quaternionfc var1, float var2, float var3, Quaternionf var4) {
      float var5 = this.x;
      float var6 = this.y;
      float var7 = this.z;
      float var8 = this.w;
      float var9 = var1.x();
      float var10 = var1.y();
      float var11 = var1.z();
      float var12 = var1.w();
      float var13 = var5 * var9 + var6 * var10 + var7 * var11 + var8 * var12;
      float var14 = Math.abs(var13);
      if (0.999999F < var14) {
         return var4.set((Quaternionfc)this);
      } else {
         float var15;
         float var16;
         float var17;
         float var18;
         for(var15 = var2; var14 < var3; var14 = Math.abs(var13)) {
            var16 = 0.5F;
            var17 = var13 >= 0.0F ? 0.5F : -0.5F;
            if (var15 < 0.5F) {
               var9 = var16 * var9 + var17 * var5;
               var10 = var16 * var10 + var17 * var6;
               var11 = var16 * var11 + var17 * var7;
               var12 = var16 * var12 + var17 * var8;
               var18 = (float)(1.0D / Math.sqrt((double)(var9 * var9 + var10 * var10 + var11 * var11 + var12 * var12)));
               var9 *= var18;
               var10 *= var18;
               var11 *= var18;
               var12 *= var18;
               var15 += var15;
            } else {
               var5 = var16 * var5 + var17 * var9;
               var6 = var16 * var6 + var17 * var10;
               var7 = var16 * var7 + var17 * var11;
               var8 = var16 * var8 + var17 * var12;
               var18 = (float)(1.0D / Math.sqrt((double)(var5 * var5 + var6 * var6 + var7 * var7 + var8 * var8)));
               var5 *= var18;
               var6 *= var18;
               var7 *= var18;
               var8 *= var18;
               var15 = var15 + var15 - 1.0F;
            }

            var13 = var5 * var9 + var6 * var10 + var7 * var11 + var8 * var12;
         }

         var16 = 1.0F - var15;
         var17 = var13 >= 0.0F ? var15 : -var15;
         var18 = var16 * var5 + var17 * var9;
         float var19 = var16 * var6 + var17 * var10;
         float var20 = var16 * var7 + var17 * var11;
         float var21 = var16 * var8 + var17 * var12;
         float var22 = (float)(1.0D / Math.sqrt((double)(var18 * var18 + var19 * var19 + var20 * var20 + var21 * var21)));
         var4.x = var18 * var22;
         var4.y = var19 * var22;
         var4.z = var20 * var22;
         var4.w = var21 * var22;
         return var4;
      }
   }

   public Quaternionf nlerpIterative(Quaternionfc var1, float var2, float var3) {
      return this.nlerpIterative(var1, var2, var3, this);
   }

   public static Quaternionfc nlerpIterative(Quaternionf[] var0, float[] var1, float var2, Quaternionf var3) {
      var3.set((Quaternionfc)var0[0]);
      float var4 = var1[0];

      for(int var5 = 1; var5 < var0.length; ++var5) {
         float var7 = var1[var5];
         float var8 = var7 / (var4 + var7);
         var4 += var7;
         var3.nlerpIterative(var0[var5], var8, var2);
      }

      return var3;
   }

   public Quaternionf lookAlong(Vector3fc var1, Vector3fc var2) {
      return this.lookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), this);
   }

   public Quaternionf lookAlong(Vector3fc var1, Vector3fc var2, Quaternionf var3) {
      return this.lookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public Quaternionf lookAlong(float var1, float var2, float var3, float var4, float var5, float var6) {
      return this.lookAlong(var1, var2, var3, var4, var5, var6, this);
   }

   public Quaternionf lookAlong(float var1, float var2, float var3, float var4, float var5, float var6, Quaternionf var7) {
      float var8 = (float)(1.0D / Math.sqrt((double)(var1 * var1 + var2 * var2 + var3 * var3)));
      float var9 = var1 * var8;
      float var10 = var2 * var8;
      float var11 = var3 * var8;
      float var12 = var5 * var11 - var6 * var10;
      float var13 = var6 * var9 - var4 * var11;
      float var14 = var4 * var10 - var5 * var9;
      float var15 = (float)(1.0D / Math.sqrt((double)(var12 * var12 + var13 * var13 + var14 * var14)));
      var12 *= var15;
      var13 *= var15;
      var14 *= var15;
      float var16 = var10 * var14 - var11 * var13;
      float var17 = var11 * var12 - var9 * var14;
      float var18 = var9 * var13 - var10 * var12;
      double var25 = (double)(var12 + var17 + var11);
      float var19;
      float var20;
      float var21;
      float var22;
      double var23;
      if (var25 >= 0.0D) {
         var23 = Math.sqrt(var25 + 1.0D);
         var22 = (float)(var23 * 0.5D);
         var23 = 0.5D / var23;
         var19 = (float)((double)(var10 - var18) * var23);
         var20 = (float)((double)(var14 - var9) * var23);
         var21 = (float)((double)(var16 - var13) * var23);
      } else if (var12 > var17 && var12 > var11) {
         var23 = Math.sqrt(1.0D + (double)var12 - (double)var17 - (double)var11);
         var19 = (float)(var23 * 0.5D);
         var23 = 0.5D / var23;
         var20 = (float)((double)(var13 + var16) * var23);
         var21 = (float)((double)(var9 + var14) * var23);
         var22 = (float)((double)(var10 - var18) * var23);
      } else if (var17 > var11) {
         var23 = Math.sqrt(1.0D + (double)var17 - (double)var12 - (double)var11);
         var20 = (float)(var23 * 0.5D);
         var23 = 0.5D / var23;
         var19 = (float)((double)(var13 + var16) * var23);
         var21 = (float)((double)(var18 + var10) * var23);
         var22 = (float)((double)(var14 - var9) * var23);
      } else {
         var23 = Math.sqrt(1.0D + (double)var11 - (double)var12 - (double)var17);
         var21 = (float)(var23 * 0.5D);
         var23 = 0.5D / var23;
         var19 = (float)((double)(var9 + var14) * var23);
         var20 = (float)((double)(var18 + var10) * var23);
         var22 = (float)((double)(var16 - var13) * var23);
      }

      var7.set(this.w * var19 + this.x * var22 + this.y * var21 - this.z * var20, this.w * var20 - this.x * var21 + this.y * var22 + this.z * var19, this.w * var21 + this.x * var20 - this.y * var19 + this.z * var22, this.w * var22 - this.x * var19 - this.y * var20 - this.z * var21);
      return var7;
   }

   public Quaternionf rotationTo(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.x = var2 * var6 - var3 * var5;
      this.y = var3 * var4 - var1 * var6;
      this.z = var1 * var5 - var2 * var4;
      this.w = (float)Math.sqrt((double)((var1 * var1 + var2 * var2 + var3 * var3) * (var4 * var4 + var5 * var5 + var6 * var6))) + var1 * var4 + var2 * var5 + var3 * var6;
      float var7 = (float)(1.0D / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w)));
      if (Float.isInfinite(var7)) {
         this.x = var5;
         this.y = -var4;
         this.z = 0.0F;
         this.w = 0.0F;
         var7 = (float)(1.0D / Math.sqrt((double)(this.x * this.x + this.y * this.y)));
         if (Float.isInfinite(var7)) {
            this.x = 0.0F;
            this.y = var6;
            this.z = -var5;
            this.w = 0.0F;
            var7 = (float)(1.0D / Math.sqrt((double)(this.y * this.y + this.z * this.z)));
         }
      }

      this.x *= var7;
      this.y *= var7;
      this.z *= var7;
      this.w *= var7;
      return this;
   }

   public Quaternionf rotationTo(Vector3fc var1, Vector3fc var2) {
      return this.rotationTo(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z());
   }

   public Quaternionf rotateTo(float var1, float var2, float var3, float var4, float var5, float var6, Quaternionf var7) {
      float var8 = var2 * var6 - var3 * var5;
      float var9 = var3 * var4 - var1 * var6;
      float var10 = var1 * var5 - var2 * var4;
      float var11 = (float)Math.sqrt((double)((var1 * var1 + var2 * var2 + var3 * var3) * (var4 * var4 + var5 * var5 + var6 * var6))) + var1 * var4 + var2 * var5 + var3 * var6;
      float var12 = (float)(1.0D / Math.sqrt((double)(var8 * var8 + var9 * var9 + var10 * var10 + var11 * var11)));
      if (Float.isInfinite(var12)) {
         var8 = var5;
         var9 = -var4;
         var10 = 0.0F;
         var11 = 0.0F;
         var12 = (float)(1.0D / Math.sqrt((double)(var5 * var5 + var9 * var9)));
         if (Float.isInfinite(var12)) {
            var8 = 0.0F;
            var9 = var6;
            var10 = -var5;
            var11 = 0.0F;
            var12 = (float)(1.0D / Math.sqrt((double)(var6 * var6 + var10 * var10)));
         }
      }

      var8 *= var12;
      var9 *= var12;
      var10 *= var12;
      var11 *= var12;
      var7.set(this.w * var8 + this.x * var11 + this.y * var10 - this.z * var9, this.w * var9 - this.x * var10 + this.y * var11 + this.z * var8, this.w * var10 + this.x * var9 - this.y * var8 + this.z * var11, this.w * var11 - this.x * var8 - this.y * var9 - this.z * var10);
      return var7;
   }

   public Quaternionf rotateTo(float var1, float var2, float var3, float var4, float var5, float var6) {
      return this.rotateTo(var1, var2, var3, var4, var5, var6, this);
   }

   public Quaternionf rotateTo(Vector3fc var1, Vector3fc var2, Quaternionf var3) {
      return this.rotateTo(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public Quaternionf rotateTo(Vector3fc var1, Vector3fc var2) {
      return this.rotateTo(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), this);
   }

   public Quaternionf rotate(float var1, float var2, float var3) {
      return this.rotate(var1, var2, var3, this);
   }

   public Quaternionf rotate(float var1, float var2, float var3, Quaternionf var4) {
      double var5 = (double)var1 * 0.5D;
      double var7 = (double)var2 * 0.5D;
      double var9 = (double)var3 * 0.5D;
      double var11 = var5 * var5 + var7 * var7 + var9 * var9;
      double var13;
      double var21;
      if (var11 * var11 / 24.0D < 9.99999993922529E-9D) {
         var21 = 1.0D - var11 / 2.0D;
         var13 = 1.0D - var11 / 6.0D;
      } else {
         double var23 = Math.sqrt(var11);
         var21 = Math.cos(var23);
         var13 = Math.sin(var23) / var23;
      }

      double var15 = var5 * var13;
      double var17 = var7 * var13;
      double var19 = var9 * var13;
      var4.set((float)((double)this.w * var15 + (double)this.x * var21 + (double)this.y * var19 - (double)this.z * var17), (float)((double)this.w * var17 - (double)this.x * var19 + (double)this.y * var21 + (double)this.z * var15), (float)((double)this.w * var19 + (double)this.x * var17 - (double)this.y * var15 + (double)this.z * var21), (float)((double)this.w * var21 - (double)this.x * var15 - (double)this.y * var17 - (double)this.z * var19));
      return var4;
   }

   public Quaternionf rotateLocal(float var1, float var2, float var3) {
      return this.rotateLocal(var1, var2, var3, this);
   }

   public Quaternionf rotateLocal(float var1, float var2, float var3, Quaternionf var4) {
      float var5 = var1 * 0.5F;
      float var6 = var2 * 0.5F;
      float var7 = var3 * 0.5F;
      float var8 = var5 * var5 + var6 * var6 + var7 * var7;
      float var9;
      float var13;
      if (var8 * var8 / 24.0F < 1.0E-8F) {
         var13 = 1.0F - var8 * 0.5F;
         var9 = 1.0F - var8 / 6.0F;
      } else {
         float var14 = (float)Math.sqrt((double)var8);
         var13 = (float)Math.cos((double)var14);
         var9 = (float)(Math.sin((double)var14) / (double)var14);
      }

      float var10 = var5 * var9;
      float var11 = var6 * var9;
      float var12 = var7 * var9;
      var4.set(var13 * this.x + var10 * this.w + var11 * this.z - var12 * this.y, var13 * this.y - var10 * this.z + var11 * this.w + var12 * this.x, var13 * this.z + var10 * this.y - var11 * this.x + var12 * this.w, var13 * this.w - var10 * this.x - var11 * this.y - var12 * this.z);
      return var4;
   }

   public Quaternionf rotateX(float var1) {
      return this.rotateX(var1, this);
   }

   public Quaternionf rotateX(float var1, Quaternionf var2) {
      float var3 = (float)Math.cos((double)var1 * 0.5D);
      float var4 = (float)Math.sin((double)var1 * 0.5D);
      var2.set(this.w * var4 + this.x * var3, this.y * var3 + this.z * var4, this.z * var3 - this.y * var4, this.w * var3 - this.x * var4);
      return var2;
   }

   public Quaternionf rotateY(float var1) {
      return this.rotateY(var1, this);
   }

   public Quaternionf rotateY(float var1, Quaternionf var2) {
      float var3 = (float)Math.cos((double)var1 * 0.5D);
      float var4 = (float)Math.sin((double)var1 * 0.5D);
      var2.set(this.x * var3 - this.z * var4, this.w * var4 + this.y * var3, this.x * var4 + this.z * var3, this.w * var3 - this.y * var4);
      return var2;
   }

   public Quaternionf rotateZ(float var1) {
      return this.rotateZ(var1, this);
   }

   public Quaternionf rotateZ(float var1, Quaternionf var2) {
      float var3 = (float)Math.cos((double)var1 * 0.5D);
      float var4 = (float)Math.sin((double)var1 * 0.5D);
      var2.set(this.x * var3 + this.y * var4, this.y * var3 - this.x * var4, this.w * var4 + this.z * var3, this.w * var3 - this.z * var4);
      return var2;
   }

   public Quaternionf rotateLocalX(float var1) {
      return this.rotateLocalX(var1, this);
   }

   public Quaternionf rotateLocalX(float var1, Quaternionf var2) {
      float var3 = var1 * 0.5F;
      float var4 = (float)Math.sin((double)var3);
      float var5 = (float)Math.cos((double)var3);
      var2.set(var5 * this.x + var4 * this.w, var5 * this.y - var4 * this.z, var5 * this.z + var4 * this.y, var5 * this.w - var4 * this.x);
      return var2;
   }

   public Quaternionf rotateLocalY(float var1) {
      return this.rotateLocalY(var1, this);
   }

   public Quaternionf rotateLocalY(float var1, Quaternionf var2) {
      float var3 = var1 * 0.5F;
      float var4 = (float)Math.sin((double)var3);
      float var5 = (float)Math.cos((double)var3);
      var2.set(var5 * this.x + var4 * this.z, var5 * this.y + var4 * this.w, var5 * this.z - var4 * this.x, var5 * this.w - var4 * this.y);
      return var2;
   }

   public Quaternionf rotateLocalZ(float var1) {
      return this.rotateLocalZ(var1, this);
   }

   public Quaternionf rotateLocalZ(float var1, Quaternionf var2) {
      float var3 = var1 * 0.5F;
      float var4 = (float)Math.sin((double)var3);
      float var5 = (float)Math.cos((double)var3);
      var2.set(var5 * this.x - var4 * this.y, var5 * this.y + var4 * this.x, var5 * this.z + var4 * this.w, var5 * this.w - var4 * this.z);
      return var2;
   }

   public Quaternionf rotateAxis(float var1, float var2, float var3, float var4, Quaternionf var5) {
      double var6 = (double)var1 / 2.0D;
      double var8 = Math.sin(var6);
      double var10 = 1.0D / Math.sqrt((double)(var2 * var2 + var3 * var3 + var4 * var4));
      double var12 = (double)var2 * var10 * var8;
      double var14 = (double)var3 * var10 * var8;
      double var16 = (double)var4 * var10 * var8;
      double var18 = Math.cos(var6);
      var5.set((float)((double)this.w * var12 + (double)this.x * var18 + (double)this.y * var16 - (double)this.z * var14), (float)((double)this.w * var14 - (double)this.x * var16 + (double)this.y * var18 + (double)this.z * var12), (float)((double)this.w * var16 + (double)this.x * var14 - (double)this.y * var12 + (double)this.z * var18), (float)((double)this.w * var18 - (double)this.x * var12 - (double)this.y * var14 - (double)this.z * var16));
      return var5;
   }

   public Quaternionf rotateAxis(float var1, Vector3fc var2, Quaternionf var3) {
      return this.rotateAxis(var1, var2.x(), var2.y(), var2.z(), var3);
   }

   public Quaternionf rotateAxis(float var1, Vector3fc var2) {
      return this.rotateAxis(var1, var2.x(), var2.y(), var2.z(), this);
   }

   public Quaternionf rotateAxis(float var1, float var2, float var3, float var4) {
      return this.rotateAxis(var1, var2, var3, var4, this);
   }

   public String toString() {
      DecimalFormat var1 = new DecimalFormat(" 0.000E0;-");
      return this.toString(var1).replaceAll("E(\\d+)", "E+$1");
   }

   public String toString(NumberFormat var1) {
      return "(" + var1.format((double)this.x) + " " + var1.format((double)this.y) + " " + var1.format((double)this.z) + " " + var1.format((double)this.w) + ")";
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeFloat(this.x);
      var1.writeFloat(this.y);
      var1.writeFloat(this.z);
      var1.writeFloat(this.w);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.x = var1.readFloat();
      this.y = var1.readFloat();
      this.z = var1.readFloat();
      this.w = var1.readFloat();
   }

   public int hashCode() {
      byte var2 = 1;
      int var3 = 31 * var2 + Float.floatToIntBits(this.w);
      var3 = 31 * var3 + Float.floatToIntBits(this.x);
      var3 = 31 * var3 + Float.floatToIntBits(this.y);
      var3 = 31 * var3 + Float.floatToIntBits(this.z);
      return var3;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         Quaternionf var2 = (Quaternionf)var1;
         if (Float.floatToIntBits(this.w) != Float.floatToIntBits(var2.w)) {
            return false;
         } else if (Float.floatToIntBits(this.x) != Float.floatToIntBits(var2.x)) {
            return false;
         } else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(var2.y)) {
            return false;
         } else {
            return Float.floatToIntBits(this.z) == Float.floatToIntBits(var2.z);
         }
      }
   }

   public Quaternionf difference(Quaternionf var1) {
      return this.difference(var1, this);
   }

   public Quaternionf difference(Quaternionf var1, Quaternionf var2) {
      float var3 = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      float var4 = -this.x * var3;
      float var5 = -this.y * var3;
      float var6 = -this.z * var3;
      float var7 = this.w * var3;
      var2.set(var7 * var1.x + var4 * var1.w + var5 * var1.z - var6 * var1.y, var7 * var1.y - var4 * var1.z + var5 * var1.w + var6 * var1.x, var7 * var1.z + var4 * var1.y - var5 * var1.x + var6 * var1.w, var7 * var1.w - var4 * var1.x - var5 * var1.y - var6 * var1.z);
      return var2;
   }

   public Vector3f positiveX(Vector3f var1) {
      float var2 = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      float var3 = -this.x * var2;
      float var4 = -this.y * var2;
      float var5 = -this.z * var2;
      float var6 = this.w * var2;
      float var7 = var4 + var4;
      float var8 = var5 + var5;
      var1.x = -var4 * var7 - var5 * var8 + 1.0F;
      var1.y = var3 * var7 + var6 * var8;
      var1.z = var3 * var8 - var6 * var7;
      return var1;
   }

   public Vector3f normalizedPositiveX(Vector3f var1) {
      float var2 = this.y + this.y;
      float var3 = this.z + this.z;
      var1.x = -this.y * var2 - this.z * var3 + 1.0F;
      var1.y = this.x * var2 - this.w * var3;
      var1.z = this.x * var3 + this.w * var2;
      return var1;
   }

   public Vector3f positiveY(Vector3f var1) {
      float var2 = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      float var3 = -this.x * var2;
      float var4 = -this.y * var2;
      float var5 = -this.z * var2;
      float var6 = this.w * var2;
      float var7 = var3 + var3;
      float var8 = var4 + var4;
      float var9 = var5 + var5;
      var1.x = var3 * var8 - var6 * var9;
      var1.y = -var3 * var7 - var5 * var9 + 1.0F;
      var1.z = var4 * var9 + var6 * var7;
      return var1;
   }

   public Vector3f normalizedPositiveY(Vector3f var1) {
      float var2 = this.x + this.x;
      float var3 = this.y + this.y;
      float var4 = this.z + this.z;
      var1.x = this.x * var3 + this.w * var4;
      var1.y = -this.x * var2 - this.z * var4 + 1.0F;
      var1.z = this.y * var4 - this.w * var2;
      return var1;
   }

   public Vector3f positiveZ(Vector3f var1) {
      float var2 = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      float var3 = -this.x * var2;
      float var4 = -this.y * var2;
      float var5 = -this.z * var2;
      float var6 = this.w * var2;
      float var7 = var3 + var3;
      float var8 = var4 + var4;
      float var9 = var5 + var5;
      var1.x = var3 * var9 + var6 * var8;
      var1.y = var4 * var9 - var6 * var7;
      var1.z = -var3 * var7 - var4 * var8 + 1.0F;
      return var1;
   }

   public Vector3f normalizedPositiveZ(Vector3f var1) {
      float var2 = this.x + this.x;
      float var3 = this.y + this.y;
      float var4 = this.z + this.z;
      var1.x = this.x * var4 - this.w * var3;
      var1.y = this.y * var4 + this.w * var2;
      var1.z = -this.x * var2 - this.y * var3 + 1.0F;
      return var1;
   }

   public Quaternionfc toImmutable() {
      return (Quaternionfc)(!Options.DEBUG ? this : new Quaternionf.Proxy(this));
   }

   private final class Proxy implements Quaternionfc {
      private final Quaternionfc delegate;

      Proxy(Quaternionfc var2) {
         this.delegate = var2;
      }

      public float x() {
         return this.delegate.x();
      }

      public float y() {
         return this.delegate.y();
      }

      public float z() {
         return this.delegate.z();
      }

      public float w() {
         return this.delegate.w();
      }

      public Quaternionf normalize(Quaternionf var1) {
         return this.delegate.normalize(var1);
      }

      public Quaternionf add(float var1, float var2, float var3, float var4, Quaternionf var5) {
         return this.delegate.add(var1, var2, var3, var4, var5);
      }

      public Quaternionf add(Quaternionfc var1, Quaternionf var2) {
         return this.delegate.add(var1, var2);
      }

      public float angle() {
         return this.delegate.angle();
      }

      public Matrix3f get(Matrix3f var1) {
         return this.delegate.get(var1);
      }

      public Matrix3d get(Matrix3d var1) {
         return this.delegate.get(var1);
      }

      public Matrix4f get(Matrix4f var1) {
         return this.delegate.get(var1);
      }

      public Matrix4d get(Matrix4d var1) {
         return this.delegate.get(var1);
      }

      public Matrix4x3f get(Matrix4x3f var1) {
         return this.delegate.get(var1);
      }

      public Matrix4x3d get(Matrix4x3d var1) {
         return this.delegate.get(var1);
      }

      public AxisAngle4f get(AxisAngle4f var1) {
         return this.delegate.get(var1);
      }

      public Quaterniond get(Quaterniond var1) {
         return this.delegate.get(var1);
      }

      public Quaternionf get(Quaternionf var1) {
         return this.delegate.get(var1);
      }

      public ByteBuffer getAsMatrix3f(ByteBuffer var1) {
         return this.delegate.getAsMatrix3f(var1);
      }

      public FloatBuffer getAsMatrix3f(FloatBuffer var1) {
         return this.delegate.getAsMatrix3f(var1);
      }

      public ByteBuffer getAsMatrix4f(ByteBuffer var1) {
         return this.delegate.getAsMatrix4f(var1);
      }

      public FloatBuffer getAsMatrix4f(FloatBuffer var1) {
         return this.delegate.getAsMatrix4f(var1);
      }

      public ByteBuffer getAsMatrix4x3f(ByteBuffer var1) {
         return this.delegate.getAsMatrix4x3f(var1);
      }

      public FloatBuffer getAsMatrix4x3f(FloatBuffer var1) {
         return this.delegate.getAsMatrix4x3f(var1);
      }

      public Quaternionf mul(Quaternionfc var1, Quaternionf var2) {
         return this.delegate.mul(var1, var2);
      }

      public Quaternionf mul(float var1, float var2, float var3, float var4, Quaternionf var5) {
         return this.delegate.mul(var1, var2, var3, var4, var5);
      }

      public Quaternionf premul(Quaternionfc var1, Quaternionf var2) {
         return this.delegate.premul(var1, var2);
      }

      public Quaternionf premul(float var1, float var2, float var3, float var4, Quaternionf var5) {
         return this.delegate.premul(var1, var2, var3, var4, var5);
      }

      public Vector3f transform(Vector3f var1) {
         return this.delegate.transform(var1);
      }

      public Vector4f transform(Vector4f var1) {
         return this.delegate.transform(var1);
      }

      public Vector3f transform(Vector3fc var1, Vector3f var2) {
         return this.delegate.transform(var1, var2);
      }

      public Vector3f transform(float var1, float var2, float var3, Vector3f var4) {
         return this.delegate.transform(var1, var2, var3, var4);
      }

      public Vector4f transform(Vector4fc var1, Vector4f var2) {
         return this.delegate.transform(var1, var2);
      }

      public Vector4f transform(float var1, float var2, float var3, Vector4f var4) {
         return this.delegate.transform(var1, var2, var3, var4);
      }

      public Quaternionf invert(Quaternionf var1) {
         return this.delegate.invert(var1);
      }

      public Quaternionf div(Quaternionfc var1, Quaternionf var2) {
         return this.delegate.div(var1, var2);
      }

      public Quaternionf conjugate(Quaternionf var1) {
         return this.delegate.conjugate(var1);
      }

      public Quaternionf rotateXYZ(float var1, float var2, float var3, Quaternionf var4) {
         return this.delegate.rotateXYZ(var1, var2, var3, var4);
      }

      public Quaternionf rotateZYX(float var1, float var2, float var3, Quaternionf var4) {
         return this.delegate.rotateZYX(var1, var2, var3, var4);
      }

      public Quaternionf rotateYXZ(float var1, float var2, float var3, Quaternionf var4) {
         return this.delegate.rotateYXZ(var1, var2, var3, var4);
      }

      public Vector3f getEulerAnglesXYZ(Vector3f var1) {
         return this.delegate.getEulerAnglesXYZ(var1);
      }

      public float lengthSquared() {
         return this.delegate.lengthSquared();
      }

      public Quaternionf slerp(Quaternionfc var1, float var2, Quaternionf var3) {
         return this.delegate.slerp(var1, var2, var3);
      }

      public Quaternionf scale(float var1, Quaternionf var2) {
         return this.delegate.scale(var1, var2);
      }

      public Quaternionf integrate(float var1, float var2, float var3, float var4, Quaternionf var5) {
         return this.delegate.integrate(var1, var2, var3, var4, var5);
      }

      public Quaternionf nlerp(Quaternionfc var1, float var2, Quaternionf var3) {
         return this.delegate.nlerp(var1, var2, var3);
      }

      public Quaternionf nlerpIterative(Quaternionfc var1, float var2, float var3, Quaternionf var4) {
         return this.delegate.nlerpIterative(var1, var2, var3, var4);
      }

      public Quaternionf lookAlong(Vector3fc var1, Vector3fc var2, Quaternionf var3) {
         return this.delegate.lookAlong(var1, var2, var3);
      }

      public Quaternionf lookAlong(float var1, float var2, float var3, float var4, float var5, float var6, Quaternionf var7) {
         return this.delegate.lookAlong(var1, var2, var3, var4, var5, var6, var7);
      }

      public Quaternionf rotateTo(float var1, float var2, float var3, float var4, float var5, float var6, Quaternionf var7) {
         return this.delegate.rotateTo(var1, var2, var3, var4, var5, var6, var7);
      }

      public Quaternionf rotateTo(Vector3fc var1, Vector3fc var2, Quaternionf var3) {
         return this.delegate.rotateTo(var1, var2, var3);
      }

      public Quaternionf rotate(float var1, float var2, float var3, Quaternionf var4) {
         return this.delegate.rotate(var1, var2, var3, var4);
      }

      public Quaternionf rotateLocal(float var1, float var2, float var3, Quaternionf var4) {
         return this.delegate.rotateLocal(var1, var2, var3, var4);
      }

      public Quaternionf rotateX(float var1, Quaternionf var2) {
         return this.delegate.rotateX(var1, var2);
      }

      public Quaternionf rotateY(float var1, Quaternionf var2) {
         return this.delegate.rotateY(var1, var2);
      }

      public Quaternionf rotateZ(float var1, Quaternionf var2) {
         return this.delegate.rotateZ(var1, var2);
      }

      public Quaternionf rotateLocalX(float var1, Quaternionf var2) {
         return this.delegate.rotateLocalX(var1, var2);
      }

      public Quaternionf rotateLocalY(float var1, Quaternionf var2) {
         return this.delegate.rotateLocalY(var1, var2);
      }

      public Quaternionf rotateLocalZ(float var1, Quaternionf var2) {
         return this.delegate.rotateLocalZ(var1, var2);
      }

      public Quaternionf rotateAxis(float var1, float var2, float var3, float var4, Quaternionf var5) {
         return this.delegate.rotateAxis(var1, var2, var3, var4, var5);
      }

      public Quaternionf rotateAxis(float var1, Vector3fc var2, Quaternionf var3) {
         return this.delegate.rotateAxis(var1, var2, var3);
      }

      public Quaternionf difference(Quaternionf var1, Quaternionf var2) {
         return this.delegate.difference(var1, var2);
      }

      public Vector3f positiveX(Vector3f var1) {
         return this.delegate.positiveX(var1);
      }

      public Vector3f normalizedPositiveX(Vector3f var1) {
         return this.delegate.normalizedPositiveX(var1);
      }

      public Vector3f positiveY(Vector3f var1) {
         return this.delegate.positiveY(var1);
      }

      public Vector3f normalizedPositiveY(Vector3f var1) {
         return this.delegate.normalizedPositiveY(var1);
      }

      public Vector3f positiveZ(Vector3f var1) {
         return this.delegate.positiveZ(var1);
      }

      public Vector3f normalizedPositiveZ(Vector3f var1) {
         return this.delegate.normalizedPositiveZ(var1);
      }
   }
}
