package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Matrix3f implements Externalizable, Matrix3fc {
   private static final long serialVersionUID = 1L;
   public float m00;
   public float m01;
   public float m02;
   public float m10;
   public float m11;
   public float m12;
   public float m20;
   public float m21;
   public float m22;

   public Matrix3f() {
      this.m00 = 1.0F;
      this.m11 = 1.0F;
      this.m22 = 1.0F;
   }

   public Matrix3f(Matrix3fc var1) {
      if (var1 instanceof Matrix3f) {
         MemUtil.INSTANCE.copy((Matrix3f)var1, this);
      } else {
         this.setMatrix3fc(var1);
      }

   }

   public Matrix3f(Matrix4fc var1) {
      if (var1 instanceof Matrix4f) {
         MemUtil.INSTANCE.copy((Matrix4f)var1, this);
      } else {
         this.setMatrix4fc(var1);
      }

   }

   public Matrix3f(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      this.m00 = var1;
      this.m01 = var2;
      this.m02 = var3;
      this.m10 = var4;
      this.m11 = var5;
      this.m12 = var6;
      this.m20 = var7;
      this.m21 = var8;
      this.m22 = var9;
   }

   public Matrix3f(FloatBuffer var1) {
      MemUtil.INSTANCE.get(this, var1.position(), var1);
   }

   public Matrix3f(Vector3fc var1, Vector3fc var2, Vector3fc var3) {
      if (var1 instanceof Vector3f && var2 instanceof Vector3f && var3 instanceof Vector3f) {
         MemUtil.INSTANCE.set(this, (Vector3f)var1, (Vector3f)var2, (Vector3f)var3);
      } else {
         this.setVector3fc(var1, var2, var3);
      }

   }

   public float m00() {
      return this.m00;
   }

   public float m01() {
      return this.m01;
   }

   public float m02() {
      return this.m02;
   }

   public float m10() {
      return this.m10;
   }

   public float m11() {
      return this.m11;
   }

   public float m12() {
      return this.m12;
   }

   public float m20() {
      return this.m20;
   }

   public float m21() {
      return this.m21;
   }

   public float m22() {
      return this.m22;
   }

   public Matrix3f m00(float var1) {
      this.m00 = var1;
      return this;
   }

   public Matrix3f m01(float var1) {
      this.m01 = var1;
      return this;
   }

   public Matrix3f m02(float var1) {
      this.m02 = var1;
      return this;
   }

   public Matrix3f m10(float var1) {
      this.m10 = var1;
      return this;
   }

   public Matrix3f m11(float var1) {
      this.m11 = var1;
      return this;
   }

   public Matrix3f m12(float var1) {
      this.m12 = var1;
      return this;
   }

   public Matrix3f m20(float var1) {
      this.m20 = var1;
      return this;
   }

   public Matrix3f m21(float var1) {
      this.m21 = var1;
      return this;
   }

   public Matrix3f m22(float var1) {
      this.m22 = var1;
      return this;
   }

   public Matrix3f set(Matrix3fc var1) {
      if (var1 instanceof Matrix3f) {
         MemUtil.INSTANCE.copy((Matrix3f)var1, this);
      } else {
         this.setMatrix3fc(var1);
      }

      return this;
   }

   private void setMatrix3fc(Matrix3fc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
   }

   public Matrix3f set(Matrix4fc var1) {
      if (var1 instanceof Matrix4f) {
         MemUtil.INSTANCE.copy((Matrix4f)var1, this);
      } else {
         this.setMatrix4fc(var1);
      }

      return this;
   }

   private void setMatrix4fc(Matrix4fc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
   }

   public Matrix3f set(AxisAngle4f var1) {
      float var2 = var1.x;
      float var3 = var1.y;
      float var4 = var1.z;
      float var5 = var1.angle;
      float var6 = (float)(1.0D / Math.sqrt((double)(var2 * var2 + var3 * var3 + var4 * var4)));
      var2 *= var6;
      var3 *= var6;
      var4 *= var6;
      float var7 = (float)Math.cos((double)var5);
      float var8 = (float)Math.sin((double)var5);
      float var9 = 1.0F - var7;
      this.m00 = var7 + var2 * var2 * var9;
      this.m11 = var7 + var3 * var3 * var9;
      this.m22 = var7 + var4 * var4 * var9;
      float var10 = var2 * var3 * var9;
      float var11 = var4 * var8;
      this.m10 = var10 - var11;
      this.m01 = var10 + var11;
      var10 = var2 * var4 * var9;
      var11 = var3 * var8;
      this.m20 = var10 + var11;
      this.m02 = var10 - var11;
      var10 = var3 * var4 * var9;
      var11 = var2 * var8;
      this.m21 = var10 - var11;
      this.m12 = var10 + var11;
      return this;
   }

   public Matrix3f set(AxisAngle4d var1) {
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
      this.m00 = (float)(var12 + var2 * var2 * var16);
      this.m11 = (float)(var12 + var4 * var4 * var16);
      this.m22 = (float)(var12 + var6 * var6 * var16);
      double var18 = var2 * var4 * var16;
      double var20 = var6 * var14;
      this.m10 = (float)(var18 - var20);
      this.m01 = (float)(var18 + var20);
      var18 = var2 * var6 * var16;
      var20 = var4 * var14;
      this.m20 = (float)(var18 + var20);
      this.m02 = (float)(var18 - var20);
      var18 = var4 * var6 * var16;
      var20 = var2 * var14;
      this.m21 = (float)(var18 - var20);
      this.m12 = (float)(var18 + var20);
      return this;
   }

   public Matrix3f set(Quaternionfc var1) {
      return this.rotation(var1);
   }

   public Matrix3f set(Quaterniondc var1) {
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
      this.m00 = (float)(var2 + var4 - var8 - var6);
      this.m01 = (float)(var12 + var10 + var10 + var12);
      this.m02 = (float)(var14 - var16 + var14 - var16);
      this.m10 = (float)(-var10 + var12 - var10 + var12);
      this.m11 = (float)(var6 - var8 + var2 - var4);
      this.m12 = (float)(var18 + var18 + var20 + var20);
      this.m20 = (float)(var16 + var14 + var14 + var16);
      this.m21 = (float)(var18 + var18 - var20 - var20);
      this.m22 = (float)(var8 - var6 - var4 + var2);
      return this;
   }

   public Matrix3f mul(Matrix3fc var1) {
      return this.mul(var1, this);
   }

   public Matrix3f mul(Matrix3fc var1, Matrix3f var2) {
      float var3 = this.m00 * var1.m00() + this.m10 * var1.m01() + this.m20 * var1.m02();
      float var4 = this.m01 * var1.m00() + this.m11 * var1.m01() + this.m21 * var1.m02();
      float var5 = this.m02 * var1.m00() + this.m12 * var1.m01() + this.m22 * var1.m02();
      float var6 = this.m00 * var1.m10() + this.m10 * var1.m11() + this.m20 * var1.m12();
      float var7 = this.m01 * var1.m10() + this.m11 * var1.m11() + this.m21 * var1.m12();
      float var8 = this.m02 * var1.m10() + this.m12 * var1.m11() + this.m22 * var1.m12();
      float var9 = this.m00 * var1.m20() + this.m10 * var1.m21() + this.m20 * var1.m22();
      float var10 = this.m01 * var1.m20() + this.m11 * var1.m21() + this.m21 * var1.m22();
      float var11 = this.m02 * var1.m20() + this.m12 * var1.m21() + this.m22 * var1.m22();
      var2.m00 = var3;
      var2.m01 = var4;
      var2.m02 = var5;
      var2.m10 = var6;
      var2.m11 = var7;
      var2.m12 = var8;
      var2.m20 = var9;
      var2.m21 = var10;
      var2.m22 = var11;
      return var2;
   }

   public Matrix3f set(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      this.m00 = var1;
      this.m01 = var2;
      this.m02 = var3;
      this.m10 = var4;
      this.m11 = var5;
      this.m12 = var6;
      this.m20 = var7;
      this.m21 = var8;
      this.m22 = var9;
      return this;
   }

   public Matrix3f set(float[] var1) {
      MemUtil.INSTANCE.copy(var1, 0, (Matrix3f)this);
      return this;
   }

   public Matrix3f set(Vector3fc var1, Vector3fc var2, Vector3fc var3) {
      if (var1 instanceof Vector3f && var2 instanceof Vector3f && var3 instanceof Vector3f) {
         MemUtil.INSTANCE.set(this, (Vector3f)var1, (Vector3f)var2, (Vector3f)var3);
      } else {
         this.setVector3fc(var1, var2, var3);
      }

      return this;
   }

   private void setVector3fc(Vector3fc var1, Vector3fc var2, Vector3fc var3) {
      this.m00 = var1.x();
      this.m01 = var1.y();
      this.m02 = var1.z();
      this.m10 = var2.x();
      this.m11 = var2.y();
      this.m12 = var2.z();
      this.m20 = var3.x();
      this.m21 = var3.y();
      this.m22 = var3.z();
   }

   public float determinant() {
      return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
   }

   public Matrix3f invert() {
      return this.invert(this);
   }

   public Matrix3f invert(Matrix3f var1) {
      float var2 = this.determinant();
      var2 = 1.0F / var2;
      float var3 = (this.m11 * this.m22 - this.m21 * this.m12) * var2;
      float var4 = (this.m21 * this.m02 - this.m01 * this.m22) * var2;
      float var5 = (this.m01 * this.m12 - this.m11 * this.m02) * var2;
      float var6 = (this.m20 * this.m12 - this.m10 * this.m22) * var2;
      float var7 = (this.m00 * this.m22 - this.m20 * this.m02) * var2;
      float var8 = (this.m10 * this.m02 - this.m00 * this.m12) * var2;
      float var9 = (this.m10 * this.m21 - this.m20 * this.m11) * var2;
      float var10 = (this.m20 * this.m01 - this.m00 * this.m21) * var2;
      float var11 = (this.m00 * this.m11 - this.m10 * this.m01) * var2;
      var1.m00 = var3;
      var1.m01 = var4;
      var1.m02 = var5;
      var1.m10 = var6;
      var1.m11 = var7;
      var1.m12 = var8;
      var1.m20 = var9;
      var1.m21 = var10;
      var1.m22 = var11;
      return var1;
   }

   public Matrix3f transpose() {
      return this.transpose(this);
   }

   public Matrix3f transpose(Matrix3f var1) {
      var1.set(this.m00, this.m10, this.m20, this.m01, this.m11, this.m21, this.m02, this.m12, this.m22);
      return var1;
   }

   public String toString() {
      DecimalFormat var1 = new DecimalFormat("  0.000E0; -");
      return this.toString(var1).replaceAll("E(\\d+)", "E+$1");
   }

   public String toString(NumberFormat var1) {
      return var1.format((double)this.m00) + var1.format((double)this.m10) + var1.format((double)this.m20) + "\n" + var1.format((double)this.m01) + var1.format((double)this.m11) + var1.format((double)this.m21) + "\n" + var1.format((double)this.m02) + var1.format((double)this.m12) + var1.format((double)this.m22) + "\n";
   }

   public Matrix3f get(Matrix3f var1) {
      return var1.set((Matrix3fc)this);
   }

   public Matrix4f get(Matrix4f var1) {
      return var1.set((Matrix3fc)this);
   }

   public AxisAngle4f getRotation(AxisAngle4f var1) {
      return var1.set((Matrix3fc)this);
   }

   public Quaternionf getUnnormalizedRotation(Quaternionf var1) {
      return var1.setFromUnnormalized((Matrix3fc)this);
   }

   public Quaternionf getNormalizedRotation(Quaternionf var1) {
      return var1.setFromNormalized((Matrix3fc)this);
   }

   public Quaterniond getUnnormalizedRotation(Quaterniond var1) {
      return var1.setFromUnnormalized((Matrix3fc)this);
   }

   public Quaterniond getNormalizedRotation(Quaterniond var1) {
      return var1.setFromNormalized((Matrix3fc)this);
   }

   public FloatBuffer get(FloatBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public FloatBuffer get(int var1, FloatBuffer var2) {
      MemUtil.INSTANCE.put(this, var1, var2);
      return var2;
   }

   public ByteBuffer get(ByteBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public ByteBuffer get(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.put(this, var1, var2);
      return var2;
   }

   public FloatBuffer getTransposed(FloatBuffer var1) {
      return this.getTransposed(var1.position(), var1);
   }

   public FloatBuffer getTransposed(int var1, FloatBuffer var2) {
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

   public float[] get(float[] var1, int var2) {
      MemUtil.INSTANCE.copy(this, var1, var2);
      return var1;
   }

   public float[] get(float[] var1) {
      return this.get(var1, 0);
   }

   public Matrix3f set(FloatBuffer var1) {
      MemUtil.INSTANCE.get(this, var1.position(), var1);
      return this;
   }

   public Matrix3f set(ByteBuffer var1) {
      MemUtil.INSTANCE.get(this, var1.position(), var1);
      return this;
   }

   public Matrix3f zero() {
      MemUtil.INSTANCE.zero(this);
      return this;
   }

   public Matrix3f identity() {
      MemUtil.INSTANCE.identity(this);
      return this;
   }

   public Matrix3f scale(Vector3fc var1, Matrix3f var2) {
      return this.scale(var1.x(), var1.y(), var1.z(), var2);
   }

   public Matrix3f scale(Vector3fc var1) {
      return this.scale(var1.x(), var1.y(), var1.z(), this);
   }

   public Matrix3f scale(float var1, float var2, float var3, Matrix3f var4) {
      var4.m00 = this.m00 * var1;
      var4.m01 = this.m01 * var1;
      var4.m02 = this.m02 * var1;
      var4.m10 = this.m10 * var2;
      var4.m11 = this.m11 * var2;
      var4.m12 = this.m12 * var2;
      var4.m20 = this.m20 * var3;
      var4.m21 = this.m21 * var3;
      var4.m22 = this.m22 * var3;
      return var4;
   }

   public Matrix3f scale(float var1, float var2, float var3) {
      return this.scale(var1, var2, var3, this);
   }

   public Matrix3f scale(float var1, Matrix3f var2) {
      return this.scale(var1, var1, var1, var2);
   }

   public Matrix3f scale(float var1) {
      return this.scale(var1, var1, var1);
   }

   public Matrix3f scaleLocal(float var1, float var2, float var3, Matrix3f var4) {
      float var5 = var1 * this.m00;
      float var6 = var2 * this.m01;
      float var7 = var3 * this.m02;
      float var8 = var1 * this.m10;
      float var9 = var2 * this.m11;
      float var10 = var3 * this.m12;
      float var11 = var1 * this.m20;
      float var12 = var2 * this.m21;
      float var13 = var3 * this.m22;
      var4.m00 = var5;
      var4.m01 = var6;
      var4.m02 = var7;
      var4.m10 = var8;
      var4.m11 = var9;
      var4.m12 = var10;
      var4.m20 = var11;
      var4.m21 = var12;
      var4.m22 = var13;
      return var4;
   }

   public Matrix3f scaleLocal(float var1, float var2, float var3) {
      return this.scaleLocal(var1, var2, var3, this);
   }

   public Matrix3f scaling(float var1) {
      MemUtil.INSTANCE.zero(this);
      this.m00 = var1;
      this.m11 = var1;
      this.m22 = var1;
      return this;
   }

   public Matrix3f scaling(float var1, float var2, float var3) {
      MemUtil.INSTANCE.zero(this);
      this.m00 = var1;
      this.m11 = var2;
      this.m22 = var3;
      return this;
   }

   public Matrix3f scaling(Vector3fc var1) {
      return this.scaling(var1.x(), var1.y(), var1.z());
   }

   public Matrix3f rotation(float var1, Vector3fc var2) {
      return this.rotation(var1, var2.x(), var2.y(), var2.z());
   }

   public Matrix3f rotation(AxisAngle4f var1) {
      return this.rotation(var1.angle, var1.x, var1.y, var1.z);
   }

   public Matrix3f rotation(float var1, float var2, float var3, float var4) {
      float var5 = (float)Math.cos((double)var1);
      float var6 = (float)Math.sin((double)var1);
      float var7 = 1.0F - var5;
      float var8 = var2 * var3;
      float var9 = var2 * var4;
      float var10 = var3 * var4;
      this.m00 = var5 + var2 * var2 * var7;
      this.m10 = var8 * var7 - var4 * var6;
      this.m20 = var9 * var7 + var3 * var6;
      this.m01 = var8 * var7 + var4 * var6;
      this.m11 = var5 + var3 * var3 * var7;
      this.m21 = var10 * var7 - var2 * var6;
      this.m02 = var9 * var7 - var3 * var6;
      this.m12 = var10 * var7 + var2 * var6;
      this.m22 = var5 + var4 * var4 * var7;
      return this;
   }

   public Matrix3f rotationX(float var1) {
      float var2;
      float var3;
      if (var1 != 3.1415927F && var1 != -3.1415927F) {
         if (var1 != 1.5707964F && var1 != -4.712389F) {
            if (var1 != -1.5707964F && var1 != 4.712389F) {
               var3 = (float)Math.cos((double)var1);
               var2 = (float)Math.sin((double)var1);
            } else {
               var3 = 0.0F;
               var2 = -1.0F;
            }
         } else {
            var3 = 0.0F;
            var2 = 1.0F;
         }
      } else {
         var3 = -1.0F;
         var2 = 0.0F;
      }

      this.m00 = 1.0F;
      this.m01 = 0.0F;
      this.m02 = 0.0F;
      this.m10 = 0.0F;
      this.m11 = var3;
      this.m12 = var2;
      this.m20 = 0.0F;
      this.m21 = -var2;
      this.m22 = var3;
      return this;
   }

   public Matrix3f rotationY(float var1) {
      float var2;
      float var3;
      if (var1 != 3.1415927F && var1 != -3.1415927F) {
         if (var1 != 1.5707964F && var1 != -4.712389F) {
            if (var1 != -1.5707964F && var1 != 4.712389F) {
               var3 = (float)Math.cos((double)var1);
               var2 = (float)Math.sin((double)var1);
            } else {
               var3 = 0.0F;
               var2 = -1.0F;
            }
         } else {
            var3 = 0.0F;
            var2 = 1.0F;
         }
      } else {
         var3 = -1.0F;
         var2 = 0.0F;
      }

      this.m00 = var3;
      this.m01 = 0.0F;
      this.m02 = -var2;
      this.m10 = 0.0F;
      this.m11 = 1.0F;
      this.m12 = 0.0F;
      this.m20 = var2;
      this.m21 = 0.0F;
      this.m22 = var3;
      return this;
   }

   public Matrix3f rotationZ(float var1) {
      float var2;
      float var3;
      if (var1 != 3.1415927F && var1 != -3.1415927F) {
         if (var1 != 1.5707964F && var1 != -4.712389F) {
            if (var1 != -1.5707964F && var1 != 4.712389F) {
               var3 = (float)Math.cos((double)var1);
               var2 = (float)Math.sin((double)var1);
            } else {
               var3 = 0.0F;
               var2 = -1.0F;
            }
         } else {
            var3 = 0.0F;
            var2 = 1.0F;
         }
      } else {
         var3 = -1.0F;
         var2 = 0.0F;
      }

      this.m00 = var3;
      this.m01 = var2;
      this.m02 = 0.0F;
      this.m10 = -var2;
      this.m11 = var3;
      this.m12 = 0.0F;
      this.m20 = 0.0F;
      this.m21 = 0.0F;
      this.m22 = 1.0F;
      return this;
   }

   public Matrix3f rotationXYZ(float var1, float var2, float var3) {
      float var4 = (float)Math.cos((double)var1);
      float var5 = (float)Math.sin((double)var1);
      float var6 = (float)Math.cos((double)var2);
      float var7 = (float)Math.sin((double)var2);
      float var8 = (float)Math.cos((double)var3);
      float var9 = (float)Math.sin((double)var3);
      float var10 = -var5;
      float var11 = -var7;
      float var12 = -var9;
      float var18 = var10 * var11;
      float var19 = var4 * var11;
      this.m20 = var7;
      this.m21 = var10 * var6;
      this.m22 = var4 * var6;
      this.m00 = var6 * var8;
      this.m01 = var18 * var8 + var4 * var9;
      this.m02 = var19 * var8 + var5 * var9;
      this.m10 = var6 * var12;
      this.m11 = var18 * var12 + var4 * var8;
      this.m12 = var19 * var12 + var5 * var8;
      return this;
   }

   public Matrix3f rotationZYX(float var1, float var2, float var3) {
      float var4 = (float)Math.cos((double)var1);
      float var5 = (float)Math.sin((double)var1);
      float var6 = (float)Math.cos((double)var2);
      float var7 = (float)Math.sin((double)var2);
      float var8 = (float)Math.cos((double)var3);
      float var9 = (float)Math.sin((double)var3);
      float var10 = -var5;
      float var11 = -var7;
      float var12 = -var9;
      float var17 = var4 * var7;
      float var18 = var5 * var7;
      this.m00 = var4 * var6;
      this.m01 = var5 * var6;
      this.m02 = var11;
      this.m10 = var10 * var8 + var17 * var9;
      this.m11 = var4 * var8 + var18 * var9;
      this.m12 = var6 * var9;
      this.m20 = var10 * var12 + var17 * var8;
      this.m21 = var4 * var12 + var18 * var8;
      this.m22 = var6 * var8;
      return this;
   }

   public Matrix3f rotationYXZ(float var1, float var2, float var3) {
      float var4 = (float)Math.cos((double)var1);
      float var5 = (float)Math.sin((double)var1);
      float var6 = (float)Math.cos((double)var2);
      float var7 = (float)Math.sin((double)var2);
      float var8 = (float)Math.cos((double)var3);
      float var9 = (float)Math.sin((double)var3);
      float var10 = -var5;
      float var11 = -var7;
      float var12 = -var9;
      float var17 = var5 * var7;
      float var19 = var4 * var7;
      this.m20 = var5 * var6;
      this.m21 = var11;
      this.m22 = var4 * var6;
      this.m00 = var4 * var8 + var17 * var9;
      this.m01 = var6 * var9;
      this.m02 = var10 * var8 + var19 * var9;
      this.m10 = var4 * var12 + var17 * var8;
      this.m11 = var6 * var8;
      this.m12 = var10 * var12 + var19 * var8;
      return this;
   }

   public Matrix3f rotation(Quaternionfc var1) {
      float var2 = var1.w() * var1.w();
      float var3 = var1.x() * var1.x();
      float var4 = var1.y() * var1.y();
      float var5 = var1.z() * var1.z();
      float var6 = var1.z() * var1.w();
      float var7 = var1.x() * var1.y();
      float var8 = var1.x() * var1.z();
      float var9 = var1.y() * var1.w();
      float var10 = var1.y() * var1.z();
      float var11 = var1.x() * var1.w();
      this.m00 = var2 + var3 - var5 - var4;
      this.m01 = var7 + var6 + var6 + var7;
      this.m02 = var8 - var9 + var8 - var9;
      this.m10 = -var6 + var7 - var6 + var7;
      this.m11 = var4 - var5 + var2 - var3;
      this.m12 = var10 + var10 + var11 + var11;
      this.m20 = var9 + var8 + var8 + var9;
      this.m21 = var10 + var10 - var11 - var11;
      this.m22 = var5 - var4 - var3 + var2;
      return this;
   }

   public Vector3f transform(Vector3f var1) {
      return var1.mul((Matrix3fc)this);
   }

   public Vector3f transform(Vector3fc var1, Vector3f var2) {
      var1.mul((Matrix3fc)this, var2);
      return var2;
   }

   public Vector3f transform(float var1, float var2, float var3, Vector3f var4) {
      var4.set(this.m00 * var1 + this.m10 * var2 + this.m20 * var3, this.m01 * var1 + this.m11 * var2 + this.m21 * var3, this.m02 * var1 + this.m12 * var2 + this.m22 * var3);
      return var4;
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeFloat(this.m00);
      var1.writeFloat(this.m01);
      var1.writeFloat(this.m02);
      var1.writeFloat(this.m10);
      var1.writeFloat(this.m11);
      var1.writeFloat(this.m12);
      var1.writeFloat(this.m20);
      var1.writeFloat(this.m21);
      var1.writeFloat(this.m22);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.m00 = var1.readFloat();
      this.m01 = var1.readFloat();
      this.m02 = var1.readFloat();
      this.m10 = var1.readFloat();
      this.m11 = var1.readFloat();
      this.m12 = var1.readFloat();
      this.m20 = var1.readFloat();
      this.m21 = var1.readFloat();
      this.m22 = var1.readFloat();
   }

   public Matrix3f rotateX(float var1, Matrix3f var2) {
      float var3;
      float var4;
      if (var1 != 3.1415927F && var1 != -3.1415927F) {
         if (var1 != 1.5707964F && var1 != -4.712389F) {
            if (var1 != -1.5707964F && var1 != 4.712389F) {
               var4 = (float)Math.cos((double)var1);
               var3 = (float)Math.sin((double)var1);
            } else {
               var4 = 0.0F;
               var3 = -1.0F;
            }
         } else {
            var4 = 0.0F;
            var3 = 1.0F;
         }
      } else {
         var4 = -1.0F;
         var3 = 0.0F;
      }

      float var6 = -var3;
      float var9 = this.m10 * var4 + this.m20 * var3;
      float var10 = this.m11 * var4 + this.m21 * var3;
      float var11 = this.m12 * var4 + this.m22 * var3;
      var2.m20 = this.m10 * var6 + this.m20 * var4;
      var2.m21 = this.m11 * var6 + this.m21 * var4;
      var2.m22 = this.m12 * var6 + this.m22 * var4;
      var2.m10 = var9;
      var2.m11 = var10;
      var2.m12 = var11;
      var2.m00 = this.m00;
      var2.m01 = this.m01;
      var2.m02 = this.m02;
      return var2;
   }

   public Matrix3f rotateX(float var1) {
      return this.rotateX(var1, this);
   }

   public Matrix3f rotateY(float var1, Matrix3f var2) {
      float var3;
      float var4;
      if (var1 != 3.1415927F && var1 != -3.1415927F) {
         if (var1 != 1.5707964F && var1 != -4.712389F) {
            if (var1 != -1.5707964F && var1 != 4.712389F) {
               var4 = (float)Math.cos((double)var1);
               var3 = (float)Math.sin((double)var1);
            } else {
               var4 = 0.0F;
               var3 = -1.0F;
            }
         } else {
            var4 = 0.0F;
            var3 = 1.0F;
         }
      } else {
         var4 = -1.0F;
         var3 = 0.0F;
      }

      float var7 = -var3;
      float var9 = this.m00 * var4 + this.m20 * var7;
      float var10 = this.m01 * var4 + this.m21 * var7;
      float var11 = this.m02 * var4 + this.m22 * var7;
      var2.m20 = this.m00 * var3 + this.m20 * var4;
      var2.m21 = this.m01 * var3 + this.m21 * var4;
      var2.m22 = this.m02 * var3 + this.m22 * var4;
      var2.m00 = var9;
      var2.m01 = var10;
      var2.m02 = var11;
      var2.m10 = this.m10;
      var2.m11 = this.m11;
      var2.m12 = this.m12;
      return var2;
   }

   public Matrix3f rotateY(float var1) {
      return this.rotateY(var1, this);
   }

   public Matrix3f rotateZ(float var1, Matrix3f var2) {
      float var3;
      float var4;
      if (var1 != 3.1415927F && var1 != -3.1415927F) {
         if (var1 != 1.5707964F && var1 != -4.712389F) {
            if (var1 != -1.5707964F && var1 != 4.712389F) {
               var4 = (float)Math.cos((double)var1);
               var3 = (float)Math.sin((double)var1);
            } else {
               var4 = 0.0F;
               var3 = -1.0F;
            }
         } else {
            var4 = 0.0F;
            var3 = 1.0F;
         }
      } else {
         var4 = -1.0F;
         var3 = 0.0F;
      }

      float var6 = -var3;
      float var9 = this.m00 * var4 + this.m10 * var3;
      float var10 = this.m01 * var4 + this.m11 * var3;
      float var11 = this.m02 * var4 + this.m12 * var3;
      var2.m10 = this.m00 * var6 + this.m10 * var4;
      var2.m11 = this.m01 * var6 + this.m11 * var4;
      var2.m12 = this.m02 * var6 + this.m12 * var4;
      var2.m00 = var9;
      var2.m01 = var10;
      var2.m02 = var11;
      var2.m20 = this.m20;
      var2.m21 = this.m21;
      var2.m22 = this.m22;
      return var2;
   }

   public Matrix3f rotateZ(float var1) {
      return this.rotateZ(var1, this);
   }

   public Matrix3f rotateXYZ(Vector3f var1) {
      return this.rotateXYZ(var1.x, var1.y, var1.z);
   }

   public Matrix3f rotateXYZ(float var1, float var2, float var3) {
      return this.rotateXYZ(var1, var2, var3, this);
   }

   public Matrix3f rotateXYZ(float var1, float var2, float var3, Matrix3f var4) {
      float var5 = (float)Math.cos((double)var1);
      float var6 = (float)Math.sin((double)var1);
      float var7 = (float)Math.cos((double)var2);
      float var8 = (float)Math.sin((double)var2);
      float var9 = (float)Math.cos((double)var3);
      float var10 = (float)Math.sin((double)var3);
      float var11 = -var6;
      float var12 = -var8;
      float var13 = -var10;
      float var14 = this.m10 * var5 + this.m20 * var6;
      float var15 = this.m11 * var5 + this.m21 * var6;
      float var16 = this.m12 * var5 + this.m22 * var6;
      float var17 = this.m10 * var11 + this.m20 * var5;
      float var18 = this.m11 * var11 + this.m21 * var5;
      float var19 = this.m12 * var11 + this.m22 * var5;
      float var20 = this.m00 * var7 + var17 * var12;
      float var21 = this.m01 * var7 + var18 * var12;
      float var22 = this.m02 * var7 + var19 * var12;
      var4.m20 = this.m00 * var8 + var17 * var7;
      var4.m21 = this.m01 * var8 + var18 * var7;
      var4.m22 = this.m02 * var8 + var19 * var7;
      var4.m00 = var20 * var9 + var14 * var10;
      var4.m01 = var21 * var9 + var15 * var10;
      var4.m02 = var22 * var9 + var16 * var10;
      var4.m10 = var20 * var13 + var14 * var9;
      var4.m11 = var21 * var13 + var15 * var9;
      var4.m12 = var22 * var13 + var16 * var9;
      return var4;
   }

   public Matrix3f rotateZYX(Vector3f var1) {
      return this.rotateZYX(var1.z, var1.y, var1.x);
   }

   public Matrix3f rotateZYX(float var1, float var2, float var3) {
      return this.rotateZYX(var1, var2, var3, this);
   }

   public Matrix3f rotateZYX(float var1, float var2, float var3, Matrix3f var4) {
      float var5 = (float)Math.cos((double)var1);
      float var6 = (float)Math.sin((double)var1);
      float var7 = (float)Math.cos((double)var2);
      float var8 = (float)Math.sin((double)var2);
      float var9 = (float)Math.cos((double)var3);
      float var10 = (float)Math.sin((double)var3);
      float var11 = -var6;
      float var12 = -var8;
      float var13 = -var10;
      float var14 = this.m00 * var5 + this.m10 * var6;
      float var15 = this.m01 * var5 + this.m11 * var6;
      float var16 = this.m02 * var5 + this.m12 * var6;
      float var17 = this.m00 * var11 + this.m10 * var5;
      float var18 = this.m01 * var11 + this.m11 * var5;
      float var19 = this.m02 * var11 + this.m12 * var5;
      float var20 = var14 * var8 + this.m20 * var7;
      float var21 = var15 * var8 + this.m21 * var7;
      float var22 = var16 * var8 + this.m22 * var7;
      var4.m00 = var14 * var7 + this.m20 * var12;
      var4.m01 = var15 * var7 + this.m21 * var12;
      var4.m02 = var16 * var7 + this.m22 * var12;
      var4.m10 = var17 * var9 + var20 * var10;
      var4.m11 = var18 * var9 + var21 * var10;
      var4.m12 = var19 * var9 + var22 * var10;
      var4.m20 = var17 * var13 + var20 * var9;
      var4.m21 = var18 * var13 + var21 * var9;
      var4.m22 = var19 * var13 + var22 * var9;
      return var4;
   }

   public Matrix3f rotateYXZ(Vector3f var1) {
      return this.rotateYXZ(var1.y, var1.x, var1.z);
   }

   public Matrix3f rotateYXZ(float var1, float var2, float var3) {
      return this.rotateYXZ(var1, var2, var3, this);
   }

   public Matrix3f rotateYXZ(float var1, float var2, float var3, Matrix3f var4) {
      float var5 = (float)Math.cos((double)var1);
      float var6 = (float)Math.sin((double)var1);
      float var7 = (float)Math.cos((double)var2);
      float var8 = (float)Math.sin((double)var2);
      float var9 = (float)Math.cos((double)var3);
      float var10 = (float)Math.sin((double)var3);
      float var11 = -var6;
      float var12 = -var8;
      float var13 = -var10;
      float var14 = this.m00 * var6 + this.m20 * var5;
      float var15 = this.m01 * var6 + this.m21 * var5;
      float var16 = this.m02 * var6 + this.m22 * var5;
      float var17 = this.m00 * var5 + this.m20 * var11;
      float var18 = this.m01 * var5 + this.m21 * var11;
      float var19 = this.m02 * var5 + this.m22 * var11;
      float var20 = this.m10 * var7 + var14 * var8;
      float var21 = this.m11 * var7 + var15 * var8;
      float var22 = this.m12 * var7 + var16 * var8;
      var4.m20 = this.m10 * var12 + var14 * var7;
      var4.m21 = this.m11 * var12 + var15 * var7;
      var4.m22 = this.m12 * var12 + var16 * var7;
      var4.m00 = var17 * var9 + var20 * var10;
      var4.m01 = var18 * var9 + var21 * var10;
      var4.m02 = var19 * var9 + var22 * var10;
      var4.m10 = var17 * var13 + var20 * var9;
      var4.m11 = var18 * var13 + var21 * var9;
      var4.m12 = var19 * var13 + var22 * var9;
      return var4;
   }

   public Matrix3f rotate(float var1, float var2, float var3, float var4) {
      return this.rotate(var1, var2, var3, var4, this);
   }

   public Matrix3f rotate(float var1, float var2, float var3, float var4, Matrix3f var5) {
      float var6 = (float)Math.sin((double)var1);
      float var7 = (float)Math.cos((double)var1);
      float var8 = 1.0F - var7;
      float var9 = var2 * var2;
      float var10 = var2 * var3;
      float var11 = var2 * var4;
      float var12 = var3 * var3;
      float var13 = var3 * var4;
      float var14 = var4 * var4;
      float var15 = var9 * var8 + var7;
      float var16 = var10 * var8 + var4 * var6;
      float var17 = var11 * var8 - var3 * var6;
      float var18 = var10 * var8 - var4 * var6;
      float var19 = var12 * var8 + var7;
      float var20 = var13 * var8 + var2 * var6;
      float var21 = var11 * var8 + var3 * var6;
      float var22 = var13 * var8 - var2 * var6;
      float var23 = var14 * var8 + var7;
      float var24 = this.m00 * var15 + this.m10 * var16 + this.m20 * var17;
      float var25 = this.m01 * var15 + this.m11 * var16 + this.m21 * var17;
      float var26 = this.m02 * var15 + this.m12 * var16 + this.m22 * var17;
      float var27 = this.m00 * var18 + this.m10 * var19 + this.m20 * var20;
      float var28 = this.m01 * var18 + this.m11 * var19 + this.m21 * var20;
      float var29 = this.m02 * var18 + this.m12 * var19 + this.m22 * var20;
      var5.m20 = this.m00 * var21 + this.m10 * var22 + this.m20 * var23;
      var5.m21 = this.m01 * var21 + this.m11 * var22 + this.m21 * var23;
      var5.m22 = this.m02 * var21 + this.m12 * var22 + this.m22 * var23;
      var5.m00 = var24;
      var5.m01 = var25;
      var5.m02 = var26;
      var5.m10 = var27;
      var5.m11 = var28;
      var5.m12 = var29;
      return var5;
   }

   public Matrix3f rotateLocal(float var1, float var2, float var3, float var4, Matrix3f var5) {
      float var6 = (float)Math.sin((double)var1);
      float var7 = (float)Math.cos((double)var1);
      float var8 = 1.0F - var7;
      float var9 = var2 * var2;
      float var10 = var2 * var3;
      float var11 = var2 * var4;
      float var12 = var3 * var3;
      float var13 = var3 * var4;
      float var14 = var4 * var4;
      float var15 = var9 * var8 + var7;
      float var16 = var10 * var8 + var4 * var6;
      float var17 = var11 * var8 - var3 * var6;
      float var18 = var10 * var8 - var4 * var6;
      float var19 = var12 * var8 + var7;
      float var20 = var13 * var8 + var2 * var6;
      float var21 = var11 * var8 + var3 * var6;
      float var22 = var13 * var8 - var2 * var6;
      float var23 = var14 * var8 + var7;
      float var24 = var15 * this.m00 + var18 * this.m01 + var21 * this.m02;
      float var25 = var16 * this.m00 + var19 * this.m01 + var22 * this.m02;
      float var26 = var17 * this.m00 + var20 * this.m01 + var23 * this.m02;
      float var27 = var15 * this.m10 + var18 * this.m11 + var21 * this.m12;
      float var28 = var16 * this.m10 + var19 * this.m11 + var22 * this.m12;
      float var29 = var17 * this.m10 + var20 * this.m11 + var23 * this.m12;
      float var30 = var15 * this.m20 + var18 * this.m21 + var21 * this.m22;
      float var31 = var16 * this.m20 + var19 * this.m21 + var22 * this.m22;
      float var32 = var17 * this.m20 + var20 * this.m21 + var23 * this.m22;
      var5.m00 = var24;
      var5.m01 = var25;
      var5.m02 = var26;
      var5.m10 = var27;
      var5.m11 = var28;
      var5.m12 = var29;
      var5.m20 = var30;
      var5.m21 = var31;
      var5.m22 = var32;
      return var5;
   }

   public Matrix3f rotateLocal(float var1, float var2, float var3, float var4) {
      return this.rotateLocal(var1, var2, var3, var4, this);
   }

   public Matrix3f rotate(Quaternionfc var1) {
      return this.rotate(var1, this);
   }

   public Matrix3f rotate(Quaternionfc var1, Matrix3f var2) {
      float var3 = var1.w() * var1.w();
      float var4 = var1.x() * var1.x();
      float var5 = var1.y() * var1.y();
      float var6 = var1.z() * var1.z();
      float var7 = var1.z() * var1.w();
      float var8 = var1.x() * var1.y();
      float var9 = var1.x() * var1.z();
      float var10 = var1.y() * var1.w();
      float var11 = var1.y() * var1.z();
      float var12 = var1.x() * var1.w();
      float var13 = var3 + var4 - var6 - var5;
      float var14 = var8 + var7 + var7 + var8;
      float var15 = var9 - var10 + var9 - var10;
      float var16 = -var7 + var8 - var7 + var8;
      float var17 = var5 - var6 + var3 - var4;
      float var18 = var11 + var11 + var12 + var12;
      float var19 = var10 + var9 + var9 + var10;
      float var20 = var11 + var11 - var12 - var12;
      float var21 = var6 - var5 - var4 + var3;
      float var22 = this.m00 * var13 + this.m10 * var14 + this.m20 * var15;
      float var23 = this.m01 * var13 + this.m11 * var14 + this.m21 * var15;
      float var24 = this.m02 * var13 + this.m12 * var14 + this.m22 * var15;
      float var25 = this.m00 * var16 + this.m10 * var17 + this.m20 * var18;
      float var26 = this.m01 * var16 + this.m11 * var17 + this.m21 * var18;
      float var27 = this.m02 * var16 + this.m12 * var17 + this.m22 * var18;
      var2.m20 = this.m00 * var19 + this.m10 * var20 + this.m20 * var21;
      var2.m21 = this.m01 * var19 + this.m11 * var20 + this.m21 * var21;
      var2.m22 = this.m02 * var19 + this.m12 * var20 + this.m22 * var21;
      var2.m00 = var22;
      var2.m01 = var23;
      var2.m02 = var24;
      var2.m10 = var25;
      var2.m11 = var26;
      var2.m12 = var27;
      return var2;
   }

   public Matrix3f rotateLocal(Quaternionfc var1, Matrix3f var2) {
      float var3 = var1.w() * var1.w();
      float var4 = var1.x() * var1.x();
      float var5 = var1.y() * var1.y();
      float var6 = var1.z() * var1.z();
      float var7 = var1.z() * var1.w();
      float var8 = var1.x() * var1.y();
      float var9 = var1.x() * var1.z();
      float var10 = var1.y() * var1.w();
      float var11 = var1.y() * var1.z();
      float var12 = var1.x() * var1.w();
      float var13 = var3 + var4 - var6 - var5;
      float var14 = var8 + var7 + var7 + var8;
      float var15 = var9 - var10 + var9 - var10;
      float var16 = -var7 + var8 - var7 + var8;
      float var17 = var5 - var6 + var3 - var4;
      float var18 = var11 + var11 + var12 + var12;
      float var19 = var10 + var9 + var9 + var10;
      float var20 = var11 + var11 - var12 - var12;
      float var21 = var6 - var5 - var4 + var3;
      float var22 = var13 * this.m00 + var16 * this.m01 + var19 * this.m02;
      float var23 = var14 * this.m00 + var17 * this.m01 + var20 * this.m02;
      float var24 = var15 * this.m00 + var18 * this.m01 + var21 * this.m02;
      float var25 = var13 * this.m10 + var16 * this.m11 + var19 * this.m12;
      float var26 = var14 * this.m10 + var17 * this.m11 + var20 * this.m12;
      float var27 = var15 * this.m10 + var18 * this.m11 + var21 * this.m12;
      float var28 = var13 * this.m20 + var16 * this.m21 + var19 * this.m22;
      float var29 = var14 * this.m20 + var17 * this.m21 + var20 * this.m22;
      float var30 = var15 * this.m20 + var18 * this.m21 + var21 * this.m22;
      var2.m00 = var22;
      var2.m01 = var23;
      var2.m02 = var24;
      var2.m10 = var25;
      var2.m11 = var26;
      var2.m12 = var27;
      var2.m20 = var28;
      var2.m21 = var29;
      var2.m22 = var30;
      return var2;
   }

   public Matrix3f rotateLocal(Quaternionfc var1) {
      return this.rotateLocal(var1, this);
   }

   public Matrix3f rotate(AxisAngle4f var1) {
      return this.rotate(var1.angle, var1.x, var1.y, var1.z);
   }

   public Matrix3f rotate(AxisAngle4f var1, Matrix3f var2) {
      return this.rotate(var1.angle, var1.x, var1.y, var1.z, var2);
   }

   public Matrix3f rotate(float var1, Vector3fc var2) {
      return this.rotate(var1, var2.x(), var2.y(), var2.z());
   }

   public Matrix3f rotate(float var1, Vector3fc var2, Matrix3f var3) {
      return this.rotate(var1, var2.x(), var2.y(), var2.z(), var3);
   }

   public Matrix3f lookAlong(Vector3fc var1, Vector3fc var2) {
      return this.lookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), this);
   }

   public Matrix3f lookAlong(Vector3fc var1, Vector3fc var2, Matrix3f var3) {
      return this.lookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public Matrix3f lookAlong(float var1, float var2, float var3, float var4, float var5, float var6, Matrix3f var7) {
      float var8 = (float)(1.0D / Math.sqrt((double)(var1 * var1 + var2 * var2 + var3 * var3)));
      float var9 = var1 * var8;
      float var10 = var2 * var8;
      float var11 = var3 * var8;
      float var12 = var10 * var6 - var11 * var5;
      float var13 = var11 * var4 - var9 * var6;
      float var14 = var9 * var5 - var10 * var4;
      float var15 = (float)(1.0D / Math.sqrt((double)(var12 * var12 + var13 * var13 + var14 * var14)));
      var12 *= var15;
      var13 *= var15;
      var14 *= var15;
      float var16 = var13 * var11 - var14 * var10;
      float var17 = var14 * var9 - var12 * var11;
      float var18 = var12 * var10 - var13 * var9;
      float var21 = -var9;
      float var24 = -var10;
      float var27 = -var11;
      float var28 = this.m00 * var12 + this.m10 * var16 + this.m20 * var21;
      float var29 = this.m01 * var12 + this.m11 * var16 + this.m21 * var21;
      float var30 = this.m02 * var12 + this.m12 * var16 + this.m22 * var21;
      float var31 = this.m00 * var13 + this.m10 * var17 + this.m20 * var24;
      float var32 = this.m01 * var13 + this.m11 * var17 + this.m21 * var24;
      float var33 = this.m02 * var13 + this.m12 * var17 + this.m22 * var24;
      var7.m20 = this.m00 * var14 + this.m10 * var18 + this.m20 * var27;
      var7.m21 = this.m01 * var14 + this.m11 * var18 + this.m21 * var27;
      var7.m22 = this.m02 * var14 + this.m12 * var18 + this.m22 * var27;
      var7.m00 = var28;
      var7.m01 = var29;
      var7.m02 = var30;
      var7.m10 = var31;
      var7.m11 = var32;
      var7.m12 = var33;
      return var7;
   }

   public Matrix3f lookAlong(float var1, float var2, float var3, float var4, float var5, float var6) {
      return this.lookAlong(var1, var2, var3, var4, var5, var6, this);
   }

   public Matrix3f setLookAlong(Vector3fc var1, Vector3fc var2) {
      return this.setLookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z());
   }

   public Matrix3f setLookAlong(float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = (float)(1.0D / Math.sqrt((double)(var1 * var1 + var2 * var2 + var3 * var3)));
      float var8 = var1 * var7;
      float var9 = var2 * var7;
      float var10 = var3 * var7;
      float var11 = var9 * var6 - var10 * var5;
      float var12 = var10 * var4 - var8 * var6;
      float var13 = var8 * var5 - var9 * var4;
      float var14 = (float)(1.0D / Math.sqrt((double)(var11 * var11 + var12 * var12 + var13 * var13)));
      var11 *= var14;
      var12 *= var14;
      var13 *= var14;
      float var15 = var12 * var10 - var13 * var9;
      float var16 = var13 * var8 - var11 * var10;
      float var17 = var11 * var9 - var12 * var8;
      this.m00 = var11;
      this.m01 = var15;
      this.m02 = -var8;
      this.m10 = var12;
      this.m11 = var16;
      this.m12 = -var9;
      this.m20 = var13;
      this.m21 = var17;
      this.m22 = -var10;
      return this;
   }

   public Vector3f getRow(int var1, Vector3f var2) throws IndexOutOfBoundsException {
      switch(var1) {
      case 0:
         var2.x = this.m00;
         var2.y = this.m10;
         var2.z = this.m20;
         break;
      case 1:
         var2.x = this.m01;
         var2.y = this.m11;
         var2.z = this.m21;
         break;
      case 2:
         var2.x = this.m02;
         var2.y = this.m12;
         var2.z = this.m22;
         break;
      default:
         throw new IndexOutOfBoundsException();
      }

      return var2;
   }

   public Matrix3f setRow(int var1, Vector3fc var2) throws IndexOutOfBoundsException {
      switch(var1) {
      case 0:
         this.m00 = var2.x();
         this.m01 = var2.y();
         this.m02 = var2.z();
         break;
      case 1:
         this.m10 = var2.x();
         this.m11 = var2.y();
         this.m12 = var2.z();
         break;
      case 2:
         this.m20 = var2.x();
         this.m21 = var2.y();
         this.m22 = var2.z();
         break;
      default:
         throw new IndexOutOfBoundsException();
      }

      return this;
   }

   public Vector3f getColumn(int var1, Vector3f var2) throws IndexOutOfBoundsException {
      switch(var1) {
      case 0:
         var2.x = this.m00;
         var2.y = this.m01;
         var2.z = this.m02;
         break;
      case 1:
         var2.x = this.m10;
         var2.y = this.m11;
         var2.z = this.m12;
         break;
      case 2:
         var2.x = this.m20;
         var2.y = this.m21;
         var2.z = this.m22;
         break;
      default:
         throw new IndexOutOfBoundsException();
      }

      return var2;
   }

   public Matrix3f setColumn(int var1, Vector3fc var2) throws IndexOutOfBoundsException {
      switch(var1) {
      case 0:
         this.m00 = var2.x();
         this.m01 = var2.y();
         this.m02 = var2.z();
         break;
      case 1:
         this.m10 = var2.x();
         this.m11 = var2.y();
         this.m12 = var2.z();
         break;
      case 2:
         this.m20 = var2.x();
         this.m21 = var2.y();
         this.m22 = var2.z();
         break;
      default:
         throw new IndexOutOfBoundsException();
      }

      return this;
   }

   public Matrix3f normal() {
      return this.normal(this);
   }

   public Matrix3f normal(Matrix3f var1) {
      float var2 = this.m00 * this.m11;
      float var3 = this.m01 * this.m10;
      float var4 = this.m02 * this.m10;
      float var5 = this.m00 * this.m12;
      float var6 = this.m01 * this.m12;
      float var7 = this.m02 * this.m11;
      float var8 = (var2 - var3) * this.m22 + (var4 - var5) * this.m21 + (var6 - var7) * this.m20;
      float var9 = 1.0F / var8;
      float var10 = (this.m11 * this.m22 - this.m21 * this.m12) * var9;
      float var11 = (this.m20 * this.m12 - this.m10 * this.m22) * var9;
      float var12 = (this.m10 * this.m21 - this.m20 * this.m11) * var9;
      float var13 = (this.m21 * this.m02 - this.m01 * this.m22) * var9;
      float var14 = (this.m00 * this.m22 - this.m20 * this.m02) * var9;
      float var15 = (this.m20 * this.m01 - this.m00 * this.m21) * var9;
      float var16 = (var6 - var7) * var9;
      float var17 = (var4 - var5) * var9;
      float var18 = (var2 - var3) * var9;
      var1.m00 = var10;
      var1.m01 = var11;
      var1.m02 = var12;
      var1.m10 = var13;
      var1.m11 = var14;
      var1.m12 = var15;
      var1.m20 = var16;
      var1.m21 = var17;
      var1.m22 = var18;
      return var1;
   }

   public Vector3f getScale(Vector3f var1) {
      var1.x = (float)Math.sqrt((double)(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02));
      var1.y = (float)Math.sqrt((double)(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12));
      var1.z = (float)Math.sqrt((double)(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22));
      return var1;
   }

   public Vector3f positiveZ(Vector3f var1) {
      var1.x = this.m10 * this.m21 - this.m11 * this.m20;
      var1.y = this.m20 * this.m01 - this.m21 * this.m00;
      var1.z = this.m00 * this.m11 - this.m01 * this.m10;
      var1.normalize();
      return var1;
   }

   public Vector3f normalizedPositiveZ(Vector3f var1) {
      var1.x = this.m02;
      var1.y = this.m12;
      var1.z = this.m22;
      return var1;
   }

   public Vector3f positiveX(Vector3f var1) {
      var1.x = this.m11 * this.m22 - this.m12 * this.m21;
      var1.y = this.m02 * this.m21 - this.m01 * this.m22;
      var1.z = this.m01 * this.m12 - this.m02 * this.m11;
      var1.normalize();
      return var1;
   }

   public Vector3f normalizedPositiveX(Vector3f var1) {
      var1.x = this.m00;
      var1.y = this.m10;
      var1.z = this.m20;
      return var1;
   }

   public Vector3f positiveY(Vector3f var1) {
      var1.x = this.m12 * this.m20 - this.m10 * this.m22;
      var1.y = this.m00 * this.m22 - this.m02 * this.m20;
      var1.z = this.m02 * this.m10 - this.m00 * this.m12;
      var1.normalize();
      return var1;
   }

   public Vector3f normalizedPositiveY(Vector3f var1) {
      var1.x = this.m01;
      var1.y = this.m11;
      var1.z = this.m21;
      return var1;
   }

   public int hashCode() {
      byte var2 = 1;
      int var3 = 31 * var2 + Float.floatToIntBits(this.m00);
      var3 = 31 * var3 + Float.floatToIntBits(this.m01);
      var3 = 31 * var3 + Float.floatToIntBits(this.m02);
      var3 = 31 * var3 + Float.floatToIntBits(this.m10);
      var3 = 31 * var3 + Float.floatToIntBits(this.m11);
      var3 = 31 * var3 + Float.floatToIntBits(this.m12);
      var3 = 31 * var3 + Float.floatToIntBits(this.m20);
      var3 = 31 * var3 + Float.floatToIntBits(this.m21);
      var3 = 31 * var3 + Float.floatToIntBits(this.m22);
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
         Matrix3f var2 = (Matrix3f)var1;
         if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(var2.m00)) {
            return false;
         } else if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(var2.m01)) {
            return false;
         } else if (Float.floatToIntBits(this.m02) != Float.floatToIntBits(var2.m02)) {
            return false;
         } else if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(var2.m10)) {
            return false;
         } else if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(var2.m11)) {
            return false;
         } else if (Float.floatToIntBits(this.m12) != Float.floatToIntBits(var2.m12)) {
            return false;
         } else if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(var2.m20)) {
            return false;
         } else if (Float.floatToIntBits(this.m21) != Float.floatToIntBits(var2.m21)) {
            return false;
         } else {
            return Float.floatToIntBits(this.m22) == Float.floatToIntBits(var2.m22);
         }
      }
   }

   public Matrix3f swap(Matrix3f var1) {
      MemUtil.INSTANCE.swap(this, var1);
      return this;
   }

   public Matrix3f add(Matrix3fc var1) {
      return this.add(var1, this);
   }

   public Matrix3f add(Matrix3fc var1, Matrix3f var2) {
      var2.m00 = this.m00 + var1.m00();
      var2.m01 = this.m01 + var1.m01();
      var2.m02 = this.m02 + var1.m02();
      var2.m10 = this.m10 + var1.m10();
      var2.m11 = this.m11 + var1.m11();
      var2.m12 = this.m12 + var1.m12();
      var2.m20 = this.m20 + var1.m20();
      var2.m21 = this.m21 + var1.m21();
      var2.m22 = this.m22 + var1.m22();
      return var2;
   }

   public Matrix3f sub(Matrix3fc var1) {
      return this.sub(var1, this);
   }

   public Matrix3f sub(Matrix3fc var1, Matrix3f var2) {
      var2.m00 = this.m00 - var1.m00();
      var2.m01 = this.m01 - var1.m01();
      var2.m02 = this.m02 - var1.m02();
      var2.m10 = this.m10 - var1.m10();
      var2.m11 = this.m11 - var1.m11();
      var2.m12 = this.m12 - var1.m12();
      var2.m20 = this.m20 - var1.m20();
      var2.m21 = this.m21 - var1.m21();
      var2.m22 = this.m22 - var1.m22();
      return var2;
   }

   public Matrix3f mulComponentWise(Matrix3fc var1) {
      return this.mulComponentWise(var1, this);
   }

   public Matrix3f mulComponentWise(Matrix3fc var1, Matrix3f var2) {
      var2.m00 = this.m00 * var1.m00();
      var2.m01 = this.m01 * var1.m01();
      var2.m02 = this.m02 * var1.m02();
      var2.m10 = this.m10 * var1.m10();
      var2.m11 = this.m11 * var1.m11();
      var2.m12 = this.m12 * var1.m12();
      var2.m20 = this.m20 * var1.m20();
      var2.m21 = this.m21 * var1.m21();
      var2.m22 = this.m22 * var1.m22();
      return var2;
   }

   public Matrix3f setSkewSymmetric(float var1, float var2, float var3) {
      this.m00 = this.m11 = this.m22 = 0.0F;
      this.m01 = -var1;
      this.m02 = var2;
      this.m10 = var1;
      this.m12 = -var3;
      this.m20 = -var2;
      this.m21 = var3;
      return this;
   }

   public Matrix3f lerp(Matrix3fc var1, float var2) {
      return this.lerp(var1, var2, this);
   }

   public Matrix3f lerp(Matrix3fc var1, float var2, Matrix3f var3) {
      var3.m00 = this.m00 + (var1.m00() - this.m00) * var2;
      var3.m01 = this.m01 + (var1.m01() - this.m01) * var2;
      var3.m02 = this.m02 + (var1.m02() - this.m02) * var2;
      var3.m10 = this.m10 + (var1.m10() - this.m10) * var2;
      var3.m11 = this.m11 + (var1.m11() - this.m11) * var2;
      var3.m12 = this.m12 + (var1.m12() - this.m12) * var2;
      var3.m20 = this.m20 + (var1.m20() - this.m20) * var2;
      var3.m21 = this.m21 + (var1.m21() - this.m21) * var2;
      var3.m22 = this.m22 + (var1.m22() - this.m22) * var2;
      return var3;
   }

   public Matrix3f rotateTowards(Vector3fc var1, Vector3fc var2, Matrix3f var3) {
      return this.rotateTowards(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public Matrix3f rotateTowards(Vector3fc var1, Vector3fc var2) {
      return this.rotateTowards(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), this);
   }

   public Matrix3f rotateTowards(float var1, float var2, float var3, float var4, float var5, float var6) {
      return this.rotateTowards(var1, var2, var3, var4, var5, var6, this);
   }

   public Matrix3f rotateTowards(float var1, float var2, float var3, float var4, float var5, float var6, Matrix3f var7) {
      float var8 = 1.0F / (float)Math.sqrt((double)(var1 * var1 + var2 * var2 + var3 * var3));
      float var9 = var1 * var8;
      float var10 = var2 * var8;
      float var11 = var3 * var8;
      float var12 = var5 * var11 - var6 * var10;
      float var13 = var6 * var9 - var4 * var11;
      float var14 = var4 * var10 - var5 * var9;
      float var15 = 1.0F / (float)Math.sqrt((double)(var12 * var12 + var13 * var13 + var14 * var14));
      var12 *= var15;
      var13 *= var15;
      var14 *= var15;
      float var16 = var10 * var14 - var11 * var13;
      float var17 = var11 * var12 - var9 * var14;
      float var18 = var9 * var13 - var10 * var12;
      float var28 = this.m00 * var12 + this.m10 * var13 + this.m20 * var14;
      float var29 = this.m01 * var12 + this.m11 * var13 + this.m21 * var14;
      float var30 = this.m02 * var12 + this.m12 * var13 + this.m22 * var14;
      float var31 = this.m00 * var16 + this.m10 * var17 + this.m20 * var18;
      float var32 = this.m01 * var16 + this.m11 * var17 + this.m21 * var18;
      float var33 = this.m02 * var16 + this.m12 * var17 + this.m22 * var18;
      var7.m20 = this.m00 * var9 + this.m10 * var10 + this.m20 * var11;
      var7.m21 = this.m01 * var9 + this.m11 * var10 + this.m21 * var11;
      var7.m22 = this.m02 * var9 + this.m12 * var10 + this.m22 * var11;
      var7.m00 = var28;
      var7.m01 = var29;
      var7.m02 = var30;
      var7.m10 = var31;
      var7.m11 = var32;
      var7.m12 = var33;
      return var7;
   }

   public Matrix3f rotationTowards(Vector3fc var1, Vector3fc var2) {
      return this.rotationTowards(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z());
   }

   public Matrix3f rotationTowards(float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = 1.0F / (float)Math.sqrt((double)(var1 * var1 + var2 * var2 + var3 * var3));
      float var8 = var1 * var7;
      float var9 = var2 * var7;
      float var10 = var3 * var7;
      float var11 = var5 * var10 - var6 * var9;
      float var12 = var6 * var8 - var4 * var10;
      float var13 = var4 * var9 - var5 * var8;
      float var14 = 1.0F / (float)Math.sqrt((double)(var11 * var11 + var12 * var12 + var13 * var13));
      var11 *= var14;
      var12 *= var14;
      var13 *= var14;
      float var15 = var9 * var13 - var10 * var12;
      float var16 = var10 * var11 - var8 * var13;
      float var17 = var8 * var12 - var9 * var11;
      this.m00 = var11;
      this.m01 = var12;
      this.m02 = var13;
      this.m10 = var15;
      this.m11 = var16;
      this.m12 = var17;
      this.m20 = var8;
      this.m21 = var9;
      this.m22 = var10;
      return this;
   }

   public Vector3f getEulerAnglesZYX(Vector3f var1) {
      var1.x = (float)Math.atan2((double)this.m12, (double)this.m22);
      var1.y = (float)Math.atan2((double)(-this.m02), (double)((float)Math.sqrt((double)(this.m12 * this.m12 + this.m22 * this.m22))));
      var1.z = (float)Math.atan2((double)this.m01, (double)this.m00);
      return var1;
   }

   public Matrix3fc toImmutable() {
      return (Matrix3fc)(!Options.DEBUG ? this : new Matrix3f.Proxy(this));
   }

   private final class Proxy implements Matrix3fc {
      private final Matrix3fc delegate;

      Proxy(Matrix3fc var2) {
         this.delegate = var2;
      }

      public float m00() {
         return this.delegate.m00();
      }

      public float m01() {
         return this.delegate.m01();
      }

      public float m02() {
         return this.delegate.m02();
      }

      public float m10() {
         return this.delegate.m10();
      }

      public float m11() {
         return this.delegate.m11();
      }

      public float m12() {
         return this.delegate.m12();
      }

      public float m20() {
         return this.delegate.m20();
      }

      public float m21() {
         return this.delegate.m21();
      }

      public float m22() {
         return this.delegate.m22();
      }

      public Matrix3f mul(Matrix3fc var1, Matrix3f var2) {
         return this.delegate.mul(var1, var2);
      }

      public float determinant() {
         return this.delegate.determinant();
      }

      public Matrix3f invert(Matrix3f var1) {
         return this.delegate.invert(var1);
      }

      public Matrix3f transpose(Matrix3f var1) {
         return this.delegate.transpose(var1);
      }

      public Matrix3f get(Matrix3f var1) {
         return this.delegate.get(var1);
      }

      public Matrix4f get(Matrix4f var1) {
         return this.delegate.get(var1);
      }

      public AxisAngle4f getRotation(AxisAngle4f var1) {
         return this.delegate.getRotation(var1);
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

      public FloatBuffer getTransposed(FloatBuffer var1) {
         return this.delegate.getTransposed(var1);
      }

      public FloatBuffer getTransposed(int var1, FloatBuffer var2) {
         return this.delegate.getTransposed(var1, var2);
      }

      public ByteBuffer getTransposed(ByteBuffer var1) {
         return this.delegate.getTransposed(var1);
      }

      public ByteBuffer getTransposed(int var1, ByteBuffer var2) {
         return this.delegate.getTransposed(var1, var2);
      }

      public float[] get(float[] var1, int var2) {
         return this.delegate.get(var1, var2);
      }

      public float[] get(float[] var1) {
         return this.delegate.get(var1);
      }

      public Matrix3f scale(Vector3fc var1, Matrix3f var2) {
         return this.delegate.scale(var1, var2);
      }

      public Matrix3f scale(float var1, float var2, float var3, Matrix3f var4) {
         return this.delegate.scale(var1, var2, var3, var4);
      }

      public Matrix3f scale(float var1, Matrix3f var2) {
         return this.delegate.scale(var1, var2);
      }

      public Matrix3f scaleLocal(float var1, float var2, float var3, Matrix3f var4) {
         return this.delegate.scaleLocal(var1, var2, var3, var4);
      }

      public Vector3f transform(Vector3f var1) {
         return this.delegate.transform(var1);
      }

      public Vector3f transform(Vector3fc var1, Vector3f var2) {
         return this.delegate.transform(var1, var2);
      }

      public Vector3f transform(float var1, float var2, float var3, Vector3f var4) {
         return this.delegate.transform(var1, var2, var3, var4);
      }

      public Matrix3f rotateX(float var1, Matrix3f var2) {
         return this.delegate.rotateX(var1, var2);
      }

      public Matrix3f rotateY(float var1, Matrix3f var2) {
         return this.delegate.rotateY(var1, var2);
      }

      public Matrix3f rotateZ(float var1, Matrix3f var2) {
         return this.delegate.rotateZ(var1, var2);
      }

      public Matrix3f rotateXYZ(float var1, float var2, float var3, Matrix3f var4) {
         return this.delegate.rotateXYZ(var1, var2, var3, var4);
      }

      public Matrix3f rotateZYX(float var1, float var2, float var3, Matrix3f var4) {
         return this.delegate.rotateZYX(var1, var2, var3, var4);
      }

      public Matrix3f rotateYXZ(float var1, float var2, float var3, Matrix3f var4) {
         return this.delegate.rotateYXZ(var1, var2, var3, var4);
      }

      public Matrix3f rotate(float var1, float var2, float var3, float var4, Matrix3f var5) {
         return this.delegate.rotate(var1, var2, var3, var4, var5);
      }

      public Matrix3f rotateLocal(float var1, float var2, float var3, float var4, Matrix3f var5) {
         return this.delegate.rotateLocal(var1, var2, var3, var4, var5);
      }

      public Matrix3f rotate(Quaternionfc var1, Matrix3f var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Matrix3f rotateLocal(Quaternionfc var1, Matrix3f var2) {
         return this.delegate.rotateLocal(var1, var2);
      }

      public Matrix3f rotate(AxisAngle4f var1, Matrix3f var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Matrix3f rotate(float var1, Vector3fc var2, Matrix3f var3) {
         return this.delegate.rotate(var1, var2, var3);
      }

      public Matrix3f lookAlong(Vector3fc var1, Vector3fc var2, Matrix3f var3) {
         return this.delegate.lookAlong(var1, var2, var3);
      }

      public Matrix3f lookAlong(float var1, float var2, float var3, float var4, float var5, float var6, Matrix3f var7) {
         return this.delegate.lookAlong(var1, var2, var3, var4, var5, var6, var7);
      }

      public Vector3f getRow(int var1, Vector3f var2) throws IndexOutOfBoundsException {
         return this.delegate.getRow(var1, var2);
      }

      public Vector3f getColumn(int var1, Vector3f var2) throws IndexOutOfBoundsException {
         return this.delegate.getColumn(var1, var2);
      }

      public Matrix3f normal(Matrix3f var1) {
         return this.delegate.normal(var1);
      }

      public Vector3f getScale(Vector3f var1) {
         return this.delegate.getScale(var1);
      }

      public Vector3f positiveZ(Vector3f var1) {
         return this.delegate.positiveZ(var1);
      }

      public Vector3f normalizedPositiveZ(Vector3f var1) {
         return this.delegate.normalizedPositiveZ(var1);
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

      public Matrix3f add(Matrix3fc var1, Matrix3f var2) {
         return this.delegate.add(var1, var2);
      }

      public Matrix3f sub(Matrix3fc var1, Matrix3f var2) {
         return this.delegate.sub(var1, var2);
      }

      public Matrix3f mulComponentWise(Matrix3fc var1, Matrix3f var2) {
         return this.delegate.mulComponentWise(var1, var2);
      }

      public Matrix3f lerp(Matrix3fc var1, float var2, Matrix3f var3) {
         return this.delegate.lerp(var1, var2, var3);
      }

      public Matrix3f rotateTowards(Vector3fc var1, Vector3fc var2, Matrix3f var3) {
         return this.delegate.rotateTowards(var1, var2, var3);
      }

      public Matrix3f rotateTowards(float var1, float var2, float var3, float var4, float var5, float var6, Matrix3f var7) {
         return this.delegate.rotateTowards(var1, var2, var3, var4, var5, var6, var7);
      }

      public Vector3f getEulerAnglesZYX(Vector3f var1) {
         return this.delegate.getEulerAnglesZYX(var1);
      }
   }
}
