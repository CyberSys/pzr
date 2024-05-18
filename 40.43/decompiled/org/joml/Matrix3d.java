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

public class Matrix3d implements Externalizable, Matrix3dc {
   private static final long serialVersionUID = 1L;
   public double m00;
   public double m01;
   public double m02;
   public double m10;
   public double m11;
   public double m12;
   public double m20;
   public double m21;
   public double m22;

   public Matrix3d() {
      this.m00 = 1.0D;
      this.m11 = 1.0D;
      this.m22 = 1.0D;
   }

   public Matrix3d(Matrix3dc var1) {
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

   public Matrix3d(Matrix3fc var1) {
      this.m00 = (double)var1.m00();
      this.m01 = (double)var1.m01();
      this.m02 = (double)var1.m02();
      this.m10 = (double)var1.m10();
      this.m11 = (double)var1.m11();
      this.m12 = (double)var1.m12();
      this.m20 = (double)var1.m20();
      this.m21 = (double)var1.m21();
      this.m22 = (double)var1.m22();
   }

   public Matrix3d(Matrix4fc var1) {
      this.m00 = (double)var1.m00();
      this.m01 = (double)var1.m01();
      this.m02 = (double)var1.m02();
      this.m10 = (double)var1.m10();
      this.m11 = (double)var1.m11();
      this.m12 = (double)var1.m12();
      this.m20 = (double)var1.m20();
      this.m21 = (double)var1.m21();
      this.m22 = (double)var1.m22();
   }

   public Matrix3d(Matrix4dc var1) {
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

   public Matrix3d(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17) {
      this.m00 = var1;
      this.m01 = var3;
      this.m02 = var5;
      this.m10 = var7;
      this.m11 = var9;
      this.m12 = var11;
      this.m20 = var13;
      this.m21 = var15;
      this.m22 = var17;
   }

   public Matrix3d(DoubleBuffer var1) {
      MemUtil.INSTANCE.get(this, var1.position(), var1);
   }

   public Matrix3d(Vector3dc var1, Vector3dc var2, Vector3dc var3) {
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

   public double m00() {
      return this.m00;
   }

   public double m01() {
      return this.m01;
   }

   public double m02() {
      return this.m02;
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

   public double m20() {
      return this.m20;
   }

   public double m21() {
      return this.m21;
   }

   public double m22() {
      return this.m22;
   }

   public Matrix3d m00(double var1) {
      this.m00 = var1;
      return this;
   }

   public Matrix3d m01(double var1) {
      this.m01 = var1;
      return this;
   }

   public Matrix3d m02(double var1) {
      this.m02 = var1;
      return this;
   }

   public Matrix3d m10(double var1) {
      this.m10 = var1;
      return this;
   }

   public Matrix3d m11(double var1) {
      this.m11 = var1;
      return this;
   }

   public Matrix3d m12(double var1) {
      this.m12 = var1;
      return this;
   }

   public Matrix3d m20(double var1) {
      this.m20 = var1;
      return this;
   }

   public Matrix3d m21(double var1) {
      this.m21 = var1;
      return this;
   }

   public Matrix3d m22(double var1) {
      this.m22 = var1;
      return this;
   }

   public Matrix3d set(Matrix3dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      return this;
   }

   public Matrix3d set(Matrix3fc var1) {
      this.m00 = (double)var1.m00();
      this.m01 = (double)var1.m01();
      this.m02 = (double)var1.m02();
      this.m10 = (double)var1.m10();
      this.m11 = (double)var1.m11();
      this.m12 = (double)var1.m12();
      this.m20 = (double)var1.m20();
      this.m21 = (double)var1.m21();
      this.m22 = (double)var1.m22();
      return this;
   }

   public Matrix3d set(Matrix4fc var1) {
      this.m00 = (double)var1.m00();
      this.m01 = (double)var1.m01();
      this.m02 = (double)var1.m02();
      this.m10 = (double)var1.m10();
      this.m11 = (double)var1.m11();
      this.m12 = (double)var1.m12();
      this.m20 = (double)var1.m20();
      this.m21 = (double)var1.m21();
      this.m22 = (double)var1.m22();
      return this;
   }

   public Matrix3d set(Matrix4dc var1) {
      this.m00 = var1.m00();
      this.m01 = var1.m01();
      this.m02 = var1.m02();
      this.m10 = var1.m10();
      this.m11 = var1.m11();
      this.m12 = var1.m12();
      this.m20 = var1.m20();
      this.m21 = var1.m21();
      this.m22 = var1.m22();
      return this;
   }

   public Matrix3d set(AxisAngle4f var1) {
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
      return this;
   }

   public Matrix3d set(AxisAngle4d var1) {
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
      return this;
   }

   public Matrix3d set(Quaternionfc var1) {
      return this.rotation(var1);
   }

   public Matrix3d set(Quaterniondc var1) {
      return this.rotation(var1);
   }

   public Matrix3d mul(Matrix3dc var1) {
      return this.mul(var1, this);
   }

   public Matrix3d mul(Matrix3dc var1, Matrix3d var2) {
      double var3 = this.m00 * var1.m00() + this.m10 * var1.m01() + this.m20 * var1.m02();
      double var5 = this.m01 * var1.m00() + this.m11 * var1.m01() + this.m21 * var1.m02();
      double var7 = this.m02 * var1.m00() + this.m12 * var1.m01() + this.m22 * var1.m02();
      double var9 = this.m00 * var1.m10() + this.m10 * var1.m11() + this.m20 * var1.m12();
      double var11 = this.m01 * var1.m10() + this.m11 * var1.m11() + this.m21 * var1.m12();
      double var13 = this.m02 * var1.m10() + this.m12 * var1.m11() + this.m22 * var1.m12();
      double var15 = this.m00 * var1.m20() + this.m10 * var1.m21() + this.m20 * var1.m22();
      double var17 = this.m01 * var1.m20() + this.m11 * var1.m21() + this.m21 * var1.m22();
      double var19 = this.m02 * var1.m20() + this.m12 * var1.m21() + this.m22 * var1.m22();
      var2.m00 = var3;
      var2.m01 = var5;
      var2.m02 = var7;
      var2.m10 = var9;
      var2.m11 = var11;
      var2.m12 = var13;
      var2.m20 = var15;
      var2.m21 = var17;
      var2.m22 = var19;
      return var2;
   }

   public Matrix3d mul(Matrix3fc var1) {
      return this.mul(var1, this);
   }

   public Matrix3d mul(Matrix3fc var1, Matrix3d var2) {
      double var3 = this.m00 * (double)var1.m00() + this.m10 * (double)var1.m01() + this.m20 * (double)var1.m02();
      double var5 = this.m01 * (double)var1.m00() + this.m11 * (double)var1.m01() + this.m21 * (double)var1.m02();
      double var7 = this.m02 * (double)var1.m00() + this.m12 * (double)var1.m01() + this.m22 * (double)var1.m02();
      double var9 = this.m00 * (double)var1.m10() + this.m10 * (double)var1.m11() + this.m20 * (double)var1.m12();
      double var11 = this.m01 * (double)var1.m10() + this.m11 * (double)var1.m11() + this.m21 * (double)var1.m12();
      double var13 = this.m02 * (double)var1.m10() + this.m12 * (double)var1.m11() + this.m22 * (double)var1.m12();
      double var15 = this.m00 * (double)var1.m20() + this.m10 * (double)var1.m21() + this.m20 * (double)var1.m22();
      double var17 = this.m01 * (double)var1.m20() + this.m11 * (double)var1.m21() + this.m21 * (double)var1.m22();
      double var19 = this.m02 * (double)var1.m20() + this.m12 * (double)var1.m21() + this.m22 * (double)var1.m22();
      var2.m00 = var3;
      var2.m01 = var5;
      var2.m02 = var7;
      var2.m10 = var9;
      var2.m11 = var11;
      var2.m12 = var13;
      var2.m20 = var15;
      var2.m21 = var17;
      var2.m22 = var19;
      return var2;
   }

   public Matrix3d set(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17) {
      this.m00 = var1;
      this.m01 = var3;
      this.m02 = var5;
      this.m10 = var7;
      this.m11 = var9;
      this.m12 = var11;
      this.m20 = var13;
      this.m21 = var15;
      this.m22 = var17;
      return this;
   }

   public Matrix3d set(double[] var1) {
      this.m00 = var1[0];
      this.m01 = var1[1];
      this.m02 = var1[2];
      this.m10 = var1[3];
      this.m11 = var1[4];
      this.m12 = var1[5];
      this.m20 = var1[6];
      this.m21 = var1[7];
      this.m22 = var1[8];
      return this;
   }

   public Matrix3d set(float[] var1) {
      this.m00 = (double)var1[0];
      this.m01 = (double)var1[1];
      this.m02 = (double)var1[2];
      this.m10 = (double)var1[3];
      this.m11 = (double)var1[4];
      this.m12 = (double)var1[5];
      this.m20 = (double)var1[6];
      this.m21 = (double)var1[7];
      this.m22 = (double)var1[8];
      return this;
   }

   public double determinant() {
      return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
   }

   public Matrix3d invert() {
      return this.invert(this);
   }

   public Matrix3d invert(Matrix3d var1) {
      double var2 = this.determinant();
      var2 = 1.0D / var2;
      double var4 = (this.m11 * this.m22 - this.m21 * this.m12) * var2;
      double var6 = (this.m21 * this.m02 - this.m01 * this.m22) * var2;
      double var8 = (this.m01 * this.m12 - this.m11 * this.m02) * var2;
      double var10 = (this.m20 * this.m12 - this.m10 * this.m22) * var2;
      double var12 = (this.m00 * this.m22 - this.m20 * this.m02) * var2;
      double var14 = (this.m10 * this.m02 - this.m00 * this.m12) * var2;
      double var16 = (this.m10 * this.m21 - this.m20 * this.m11) * var2;
      double var18 = (this.m20 * this.m01 - this.m00 * this.m21) * var2;
      double var20 = (this.m00 * this.m11 - this.m10 * this.m01) * var2;
      var1.m00 = var4;
      var1.m01 = var6;
      var1.m02 = var8;
      var1.m10 = var10;
      var1.m11 = var12;
      var1.m12 = var14;
      var1.m20 = var16;
      var1.m21 = var18;
      var1.m22 = var20;
      return var1;
   }

   public Matrix3d transpose() {
      return this.transpose(this);
   }

   public Matrix3d transpose(Matrix3d var1) {
      var1.set(this.m00, this.m10, this.m20, this.m01, this.m11, this.m21, this.m02, this.m12, this.m22);
      return var1;
   }

   public String toString() {
      DecimalFormat var1 = new DecimalFormat("  0.000E0; -");
      return this.toString(var1).replaceAll("E(\\d+)", "E+$1");
   }

   public String toString(NumberFormat var1) {
      return var1.format(this.m00) + var1.format(this.m10) + var1.format(this.m20) + "\n" + var1.format(this.m01) + var1.format(this.m11) + var1.format(this.m21) + "\n" + var1.format(this.m02) + var1.format(this.m12) + var1.format(this.m22) + "\n";
   }

   public Matrix3d get(Matrix3d var1) {
      return var1.set((Matrix3dc)this);
   }

   public AxisAngle4f getRotation(AxisAngle4f var1) {
      return var1.set((Matrix3dc)this);
   }

   public Quaternionf getUnnormalizedRotation(Quaternionf var1) {
      return var1.setFromUnnormalized((Matrix3dc)this);
   }

   public Quaternionf getNormalizedRotation(Quaternionf var1) {
      return var1.setFromNormalized((Matrix3dc)this);
   }

   public Quaterniond getUnnormalizedRotation(Quaterniond var1) {
      return var1.setFromUnnormalized((Matrix3dc)this);
   }

   public Quaterniond getNormalizedRotation(Quaterniond var1) {
      return var1.setFromNormalized((Matrix3dc)this);
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
      var1[var2 + 3] = this.m10;
      var1[var2 + 4] = this.m11;
      var1[var2 + 5] = this.m12;
      var1[var2 + 6] = this.m20;
      var1[var2 + 7] = this.m21;
      var1[var2 + 8] = this.m22;
      return var1;
   }

   public double[] get(double[] var1) {
      return this.get((double[])var1, 0);
   }

   public float[] get(float[] var1, int var2) {
      var1[var2 + 0] = (float)this.m00;
      var1[var2 + 1] = (float)this.m01;
      var1[var2 + 2] = (float)this.m02;
      var1[var2 + 3] = (float)this.m10;
      var1[var2 + 4] = (float)this.m11;
      var1[var2 + 5] = (float)this.m12;
      var1[var2 + 6] = (float)this.m20;
      var1[var2 + 7] = (float)this.m21;
      var1[var2 + 8] = (float)this.m22;
      return var1;
   }

   public float[] get(float[] var1) {
      return this.get((float[])var1, 0);
   }

   public Matrix3d set(DoubleBuffer var1) {
      MemUtil.INSTANCE.get(this, var1.position(), var1);
      return this;
   }

   public Matrix3d set(FloatBuffer var1) {
      MemUtil.INSTANCE.getf(this, var1.position(), var1);
      return this;
   }

   public Matrix3d set(ByteBuffer var1) {
      MemUtil.INSTANCE.get(this, var1.position(), var1);
      return this;
   }

   public Matrix3d setFloats(ByteBuffer var1) {
      MemUtil.INSTANCE.getf(this, var1.position(), var1);
      return this;
   }

   public Matrix3d set(Vector3dc var1, Vector3dc var2, Vector3dc var3) {
      this.m00 = var1.x();
      this.m01 = var1.y();
      this.m02 = var1.z();
      this.m10 = var2.x();
      this.m11 = var2.y();
      this.m12 = var2.z();
      this.m20 = var3.x();
      this.m21 = var3.y();
      this.m22 = var3.z();
      return this;
   }

   public Matrix3d zero() {
      this.m00 = 0.0D;
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 0.0D;
      this.m12 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = 0.0D;
      return this;
   }

   public Matrix3d identity() {
      this.m00 = 1.0D;
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = 1.0D;
      this.m12 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = 1.0D;
      return this;
   }

   public Matrix3d scaling(double var1) {
      this.m00 = var1;
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = var1;
      this.m12 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = var1;
      return this;
   }

   public Matrix3d scaling(double var1, double var3, double var5) {
      this.m00 = var1;
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = var3;
      this.m12 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = var5;
      return this;
   }

   public Matrix3d scaling(Vector3dc var1) {
      this.m00 = var1.x();
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = var1.y();
      this.m12 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = var1.z();
      return this;
   }

   public Matrix3d scale(Vector3dc var1, Matrix3d var2) {
      return this.scale(var1.x(), var1.y(), var1.z(), var2);
   }

   public Matrix3d scale(Vector3dc var1) {
      return this.scale(var1.x(), var1.y(), var1.z(), this);
   }

   public Matrix3d scale(double var1, double var3, double var5, Matrix3d var7) {
      var7.m00 = this.m00 * var1;
      var7.m01 = this.m01 * var1;
      var7.m02 = this.m02 * var1;
      var7.m10 = this.m10 * var3;
      var7.m11 = this.m11 * var3;
      var7.m12 = this.m12 * var3;
      var7.m20 = this.m20 * var5;
      var7.m21 = this.m21 * var5;
      var7.m22 = this.m22 * var5;
      return var7;
   }

   public Matrix3d scale(double var1, double var3, double var5) {
      return this.scale(var1, var3, var5, this);
   }

   public Matrix3d scale(double var1, Matrix3d var3) {
      return this.scale(var1, var1, var1, var3);
   }

   public Matrix3d scale(double var1) {
      return this.scale(var1, var1, var1);
   }

   public Matrix3d scaleLocal(double var1, double var3, double var5, Matrix3d var7) {
      double var8 = var1 * this.m00;
      double var10 = var3 * this.m01;
      double var12 = var5 * this.m02;
      double var14 = var1 * this.m10;
      double var16 = var3 * this.m11;
      double var18 = var5 * this.m12;
      double var20 = var1 * this.m20;
      double var22 = var3 * this.m21;
      double var24 = var5 * this.m22;
      var7.m00 = var8;
      var7.m01 = var10;
      var7.m02 = var12;
      var7.m10 = var14;
      var7.m11 = var16;
      var7.m12 = var18;
      var7.m20 = var20;
      var7.m21 = var22;
      var7.m22 = var24;
      return var7;
   }

   public Matrix3d scaleLocal(double var1, double var3, double var5) {
      return this.scaleLocal(var1, var3, var5, this);
   }

   public Matrix3d rotation(double var1, Vector3dc var3) {
      return this.rotation(var1, var3.x(), var3.y(), var3.z());
   }

   public Matrix3d rotation(double var1, Vector3fc var3) {
      return this.rotation(var1, (double)var3.x(), (double)var3.y(), (double)var3.z());
   }

   public Matrix3d rotation(AxisAngle4f var1) {
      return this.rotation((double)var1.angle, (double)var1.x, (double)var1.y, (double)var1.z);
   }

   public Matrix3d rotation(AxisAngle4d var1) {
      return this.rotation(var1.angle, var1.x, var1.y, var1.z);
   }

   public Matrix3d rotation(double var1, double var3, double var5, double var7) {
      double var9 = Math.cos(var1);
      double var11 = Math.sin(var1);
      double var13 = 1.0D - var9;
      double var15 = var3 * var5;
      double var17 = var3 * var7;
      double var19 = var5 * var7;
      this.m00 = var9 + var3 * var3 * var13;
      this.m10 = var15 * var13 - var7 * var11;
      this.m20 = var17 * var13 + var5 * var11;
      this.m01 = var15 * var13 + var7 * var11;
      this.m11 = var9 + var5 * var5 * var13;
      this.m21 = var19 * var13 - var3 * var11;
      this.m02 = var17 * var13 - var5 * var11;
      this.m12 = var19 * var13 + var3 * var11;
      this.m22 = var9 + var7 * var7 * var13;
      return this;
   }

   public Matrix3d rotationX(double var1) {
      double var3;
      double var5;
      if (var1 != 3.141592653589793D && var1 != -3.141592653589793D) {
         if (var1 != 1.5707963267948966D && var1 != -4.71238898038469D) {
            if (var1 != -1.5707963267948966D && var1 != 4.71238898038469D) {
               var5 = Math.cos(var1);
               var3 = Math.sin(var1);
            } else {
               var5 = 0.0D;
               var3 = -1.0D;
            }
         } else {
            var5 = 0.0D;
            var3 = 1.0D;
         }
      } else {
         var5 = -1.0D;
         var3 = 0.0D;
      }

      this.m00 = 1.0D;
      this.m01 = 0.0D;
      this.m02 = 0.0D;
      this.m10 = 0.0D;
      this.m11 = var5;
      this.m12 = var3;
      this.m20 = 0.0D;
      this.m21 = -var3;
      this.m22 = var5;
      return this;
   }

   public Matrix3d rotationY(double var1) {
      double var3;
      double var5;
      if (var1 != 3.141592653589793D && var1 != -3.141592653589793D) {
         if (var1 != 1.5707963267948966D && var1 != -4.71238898038469D) {
            if (var1 != -1.5707963267948966D && var1 != 4.71238898038469D) {
               var5 = Math.cos(var1);
               var3 = Math.sin(var1);
            } else {
               var5 = 0.0D;
               var3 = -1.0D;
            }
         } else {
            var5 = 0.0D;
            var3 = 1.0D;
         }
      } else {
         var5 = -1.0D;
         var3 = 0.0D;
      }

      this.m00 = var5;
      this.m01 = 0.0D;
      this.m02 = -var3;
      this.m10 = 0.0D;
      this.m11 = 1.0D;
      this.m12 = 0.0D;
      this.m20 = var3;
      this.m21 = 0.0D;
      this.m22 = var5;
      return this;
   }

   public Matrix3d rotationZ(double var1) {
      double var3;
      double var5;
      if (var1 != 3.141592653589793D && var1 != -3.141592653589793D) {
         if (var1 != 1.5707963267948966D && var1 != -4.71238898038469D) {
            if (var1 != -1.5707963267948966D && var1 != 4.71238898038469D) {
               var5 = Math.cos(var1);
               var3 = Math.sin(var1);
            } else {
               var5 = 0.0D;
               var3 = -1.0D;
            }
         } else {
            var5 = 0.0D;
            var3 = 1.0D;
         }
      } else {
         var5 = -1.0D;
         var3 = 0.0D;
      }

      this.m00 = var5;
      this.m01 = var3;
      this.m02 = 0.0D;
      this.m10 = -var3;
      this.m11 = var5;
      this.m12 = 0.0D;
      this.m20 = 0.0D;
      this.m21 = 0.0D;
      this.m22 = 1.0D;
      return this;
   }

   public Matrix3d rotationXYZ(double var1, double var3, double var5) {
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
      return this;
   }

   public Matrix3d rotationZYX(double var1, double var3, double var5) {
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
      return this;
   }

   public Matrix3d rotationYXZ(double var1, double var3, double var5) {
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
      return this;
   }

   public Matrix3d rotation(Quaterniondc var1) {
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
      this.m10 = -var10 + var12 - var10 + var12;
      this.m11 = var6 - var8 + var2 - var4;
      this.m12 = var18 + var18 + var20 + var20;
      this.m20 = var16 + var14 + var14 + var16;
      this.m21 = var18 + var18 - var20 - var20;
      this.m22 = var8 - var6 - var4 + var2;
      return this;
   }

   public Matrix3d rotation(Quaternionfc var1) {
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
      this.m10 = -var10 + var12 - var10 + var12;
      this.m11 = var6 - var8 + var2 - var4;
      this.m12 = var18 + var18 + var20 + var20;
      this.m20 = var16 + var14 + var14 + var16;
      this.m21 = var18 + var18 - var20 - var20;
      this.m22 = var8 - var6 - var4 + var2;
      return this;
   }

   public Vector3d transform(Vector3d var1) {
      return var1.mul((Matrix3dc)this);
   }

   public Vector3d transform(Vector3dc var1, Vector3d var2) {
      var1.mul((Matrix3dc)this, var2);
      return var2;
   }

   public Vector3d transform(double var1, double var3, double var5, Vector3d var7) {
      var7.set(this.m00 * var1 + this.m10 * var3 + this.m20 * var5, this.m01 * var1 + this.m11 * var3 + this.m21 * var5, this.m02 * var1 + this.m12 * var3 + this.m22 * var5);
      return var7;
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeDouble(this.m00);
      var1.writeDouble(this.m01);
      var1.writeDouble(this.m02);
      var1.writeDouble(this.m10);
      var1.writeDouble(this.m11);
      var1.writeDouble(this.m12);
      var1.writeDouble(this.m20);
      var1.writeDouble(this.m21);
      var1.writeDouble(this.m22);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.m00 = var1.readDouble();
      this.m01 = var1.readDouble();
      this.m02 = var1.readDouble();
      this.m10 = var1.readDouble();
      this.m11 = var1.readDouble();
      this.m12 = var1.readDouble();
      this.m20 = var1.readDouble();
      this.m21 = var1.readDouble();
      this.m22 = var1.readDouble();
   }

   public Matrix3d rotateX(double var1, Matrix3d var3) {
      double var4;
      double var6;
      if (var1 != 3.141592653589793D && var1 != -3.141592653589793D) {
         if (var1 != 1.5707963267948966D && var1 != -4.71238898038469D) {
            if (var1 != -1.5707963267948966D && var1 != 4.71238898038469D) {
               var6 = Math.cos(var1);
               var4 = Math.sin(var1);
            } else {
               var6 = 0.0D;
               var4 = -1.0D;
            }
         } else {
            var6 = 0.0D;
            var4 = 1.0D;
         }
      } else {
         var6 = -1.0D;
         var4 = 0.0D;
      }

      double var10 = -var4;
      double var16 = this.m10 * var6 + this.m20 * var4;
      double var18 = this.m11 * var6 + this.m21 * var4;
      double var20 = this.m12 * var6 + this.m22 * var4;
      var3.m20 = this.m10 * var10 + this.m20 * var6;
      var3.m21 = this.m11 * var10 + this.m21 * var6;
      var3.m22 = this.m12 * var10 + this.m22 * var6;
      var3.m10 = var16;
      var3.m11 = var18;
      var3.m12 = var20;
      var3.m00 = this.m00;
      var3.m01 = this.m01;
      var3.m02 = this.m02;
      return var3;
   }

   public Matrix3d rotateX(double var1) {
      return this.rotateX(var1, this);
   }

   public Matrix3d rotateY(double var1, Matrix3d var3) {
      double var4;
      double var6;
      if (var1 != 3.141592653589793D && var1 != -3.141592653589793D) {
         if (var1 != 1.5707963267948966D && var1 != -4.71238898038469D) {
            if (var1 != -1.5707963267948966D && var1 != 4.71238898038469D) {
               var6 = Math.cos(var1);
               var4 = Math.sin(var1);
            } else {
               var6 = 0.0D;
               var4 = -1.0D;
            }
         } else {
            var6 = 0.0D;
            var4 = 1.0D;
         }
      } else {
         var6 = -1.0D;
         var4 = 0.0D;
      }

      double var12 = -var4;
      double var16 = this.m00 * var6 + this.m20 * var12;
      double var18 = this.m01 * var6 + this.m21 * var12;
      double var20 = this.m02 * var6 + this.m22 * var12;
      var3.m20 = this.m00 * var4 + this.m20 * var6;
      var3.m21 = this.m01 * var4 + this.m21 * var6;
      var3.m22 = this.m02 * var4 + this.m22 * var6;
      var3.m00 = var16;
      var3.m01 = var18;
      var3.m02 = var20;
      var3.m10 = this.m10;
      var3.m11 = this.m11;
      var3.m12 = this.m12;
      return var3;
   }

   public Matrix3d rotateY(double var1) {
      return this.rotateY(var1, this);
   }

   public Matrix3d rotateZ(double var1, Matrix3d var3) {
      double var4;
      double var6;
      if (var1 != 3.141592653589793D && var1 != -3.141592653589793D) {
         if (var1 != 1.5707963267948966D && var1 != -4.71238898038469D) {
            if (var1 != -1.5707963267948966D && var1 != 4.71238898038469D) {
               var6 = Math.cos(var1);
               var4 = Math.sin(var1);
            } else {
               var6 = 0.0D;
               var4 = -1.0D;
            }
         } else {
            var6 = 0.0D;
            var4 = 1.0D;
         }
      } else {
         var6 = -1.0D;
         var4 = 0.0D;
      }

      double var10 = -var4;
      double var16 = this.m00 * var6 + this.m10 * var4;
      double var18 = this.m01 * var6 + this.m11 * var4;
      double var20 = this.m02 * var6 + this.m12 * var4;
      var3.m10 = this.m00 * var10 + this.m10 * var6;
      var3.m11 = this.m01 * var10 + this.m11 * var6;
      var3.m12 = this.m02 * var10 + this.m12 * var6;
      var3.m00 = var16;
      var3.m01 = var18;
      var3.m02 = var20;
      var3.m20 = this.m20;
      var3.m21 = this.m21;
      var3.m22 = this.m22;
      return var3;
   }

   public Matrix3d rotateZ(double var1) {
      return this.rotateZ(var1, this);
   }

   public Matrix3d rotateXYZ(double var1, double var3, double var5) {
      return this.rotateXYZ(var1, var3, var5, this);
   }

   public Matrix3d rotateXYZ(double var1, double var3, double var5, Matrix3d var7) {
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
      var7.m00 = var38 * var16 + var26 * var18;
      var7.m01 = var40 * var16 + var28 * var18;
      var7.m02 = var42 * var16 + var30 * var18;
      var7.m10 = var38 * var24 + var26 * var16;
      var7.m11 = var40 * var24 + var28 * var16;
      var7.m12 = var42 * var24 + var30 * var16;
      return var7;
   }

   public Matrix3d rotateZYX(double var1, double var3, double var5) {
      return this.rotateZYX(var1, var3, var5, this);
   }

   public Matrix3d rotateZYX(double var1, double var3, double var5, Matrix3d var7) {
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
      var7.m10 = var32 * var16 + var38 * var18;
      var7.m11 = var34 * var16 + var40 * var18;
      var7.m12 = var36 * var16 + var42 * var18;
      var7.m20 = var32 * var24 + var38 * var16;
      var7.m21 = var34 * var24 + var40 * var16;
      var7.m22 = var36 * var24 + var42 * var16;
      return var7;
   }

   public Matrix3d rotateYXZ(Vector3d var1) {
      return this.rotateYXZ(var1.y, var1.x, var1.z);
   }

   public Matrix3d rotateYXZ(double var1, double var3, double var5) {
      return this.rotateYXZ(var1, var3, var5, this);
   }

   public Matrix3d rotateYXZ(double var1, double var3, double var5, Matrix3d var7) {
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
      var7.m00 = var32 * var16 + var38 * var18;
      var7.m01 = var34 * var16 + var40 * var18;
      var7.m02 = var36 * var16 + var42 * var18;
      var7.m10 = var32 * var24 + var38 * var16;
      var7.m11 = var34 * var24 + var40 * var16;
      var7.m12 = var36 * var24 + var42 * var16;
      return var7;
   }

   public Matrix3d rotate(double var1, double var3, double var5, double var7) {
      return this.rotate(var1, var3, var5, var7, this);
   }

   public Matrix3d rotate(double var1, double var3, double var5, double var7, Matrix3d var9) {
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
      var9.m00 = var46;
      var9.m01 = var48;
      var9.m02 = var50;
      var9.m10 = var52;
      var9.m11 = var54;
      var9.m12 = var56;
      return var9;
   }

   public Matrix3d rotateLocal(double var1, double var3, double var5, double var7, Matrix3d var9) {
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
      double var52 = var28 * this.m10 + var34 * this.m11 + var40 * this.m12;
      double var54 = var30 * this.m10 + var36 * this.m11 + var42 * this.m12;
      double var56 = var32 * this.m10 + var38 * this.m11 + var44 * this.m12;
      double var58 = var28 * this.m20 + var34 * this.m21 + var40 * this.m22;
      double var60 = var30 * this.m20 + var36 * this.m21 + var42 * this.m22;
      double var62 = var32 * this.m20 + var38 * this.m21 + var44 * this.m22;
      var9.m00 = var46;
      var9.m01 = var48;
      var9.m02 = var50;
      var9.m10 = var52;
      var9.m11 = var54;
      var9.m12 = var56;
      var9.m20 = var58;
      var9.m21 = var60;
      var9.m22 = var62;
      return var9;
   }

   public Matrix3d rotateLocal(double var1, double var3, double var5, double var7) {
      return this.rotateLocal(var1, var3, var5, var7, this);
   }

   public Matrix3d rotateLocal(Quaterniondc var1, Matrix3d var2) {
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
      double var47 = var23 * this.m10 + var29 * this.m11 + var35 * this.m12;
      double var49 = var25 * this.m10 + var31 * this.m11 + var37 * this.m12;
      double var51 = var27 * this.m10 + var33 * this.m11 + var39 * this.m12;
      double var53 = var23 * this.m20 + var29 * this.m21 + var35 * this.m22;
      double var55 = var25 * this.m20 + var31 * this.m21 + var37 * this.m22;
      double var57 = var27 * this.m20 + var33 * this.m21 + var39 * this.m22;
      var2.m00 = var41;
      var2.m01 = var43;
      var2.m02 = var45;
      var2.m10 = var47;
      var2.m11 = var49;
      var2.m12 = var51;
      var2.m20 = var53;
      var2.m21 = var55;
      var2.m22 = var57;
      return var2;
   }

   public Matrix3d rotateLocal(Quaterniondc var1) {
      return this.rotateLocal(var1, this);
   }

   public Matrix3d rotateLocal(Quaternionfc var1, Matrix3d var2) {
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
      double var47 = var23 * this.m10 + var29 * this.m11 + var35 * this.m12;
      double var49 = var25 * this.m10 + var31 * this.m11 + var37 * this.m12;
      double var51 = var27 * this.m10 + var33 * this.m11 + var39 * this.m12;
      double var53 = var23 * this.m20 + var29 * this.m21 + var35 * this.m22;
      double var55 = var25 * this.m20 + var31 * this.m21 + var37 * this.m22;
      double var57 = var27 * this.m20 + var33 * this.m21 + var39 * this.m22;
      var2.m00 = var41;
      var2.m01 = var43;
      var2.m02 = var45;
      var2.m10 = var47;
      var2.m11 = var49;
      var2.m12 = var51;
      var2.m20 = var53;
      var2.m21 = var55;
      var2.m22 = var57;
      return var2;
   }

   public Matrix3d rotateLocal(Quaternionfc var1) {
      return this.rotateLocal(var1, this);
   }

   public Matrix3d rotate(Quaterniondc var1) {
      return this.rotate(var1, this);
   }

   public Matrix3d rotate(Quaterniondc var1, Matrix3d var2) {
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
      var2.m00 = var41;
      var2.m01 = var43;
      var2.m02 = var45;
      var2.m10 = var47;
      var2.m11 = var49;
      var2.m12 = var51;
      return var2;
   }

   public Matrix3d rotate(Quaternionfc var1) {
      return this.rotate(var1, this);
   }

   public Matrix3d rotate(Quaternionfc var1, Matrix3d var2) {
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
      var2.m00 = var41;
      var2.m01 = var43;
      var2.m02 = var45;
      var2.m10 = var47;
      var2.m11 = var49;
      var2.m12 = var51;
      return var2;
   }

   public Matrix3d rotate(AxisAngle4f var1) {
      return this.rotate((double)var1.angle, (double)var1.x, (double)var1.y, (double)var1.z);
   }

   public Matrix3d rotate(AxisAngle4f var1, Matrix3d var2) {
      return this.rotate((double)var1.angle, (double)var1.x, (double)var1.y, (double)var1.z, var2);
   }

   public Matrix3d rotate(AxisAngle4d var1) {
      return this.rotate(var1.angle, var1.x, var1.y, var1.z);
   }

   public Matrix3d rotate(AxisAngle4d var1, Matrix3d var2) {
      return this.rotate(var1.angle, var1.x, var1.y, var1.z, var2);
   }

   public Matrix3d rotate(double var1, Vector3dc var3) {
      return this.rotate(var1, var3.x(), var3.y(), var3.z());
   }

   public Matrix3d rotate(double var1, Vector3dc var3, Matrix3d var4) {
      return this.rotate(var1, var3.x(), var3.y(), var3.z(), var4);
   }

   public Matrix3d rotate(double var1, Vector3fc var3) {
      return this.rotate(var1, (double)var3.x(), (double)var3.y(), (double)var3.z());
   }

   public Matrix3d rotate(double var1, Vector3fc var3, Matrix3d var4) {
      return this.rotate(var1, (double)var3.x(), (double)var3.y(), (double)var3.z(), var4);
   }

   public Vector3d getRow(int var1, Vector3d var2) throws IndexOutOfBoundsException {
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

   public Matrix3d setRow(int var1, Vector3dc var2) throws IndexOutOfBoundsException {
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

   public Vector3d getColumn(int var1, Vector3d var2) throws IndexOutOfBoundsException {
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

   public Matrix3d setColumn(int var1, Vector3dc var2) throws IndexOutOfBoundsException {
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

   public Matrix3d normal() {
      return this.normal(this);
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
      var1.m10 = var24;
      var1.m11 = var26;
      var1.m12 = var28;
      var1.m20 = var30;
      var1.m21 = var32;
      var1.m22 = var34;
      return var1;
   }

   public Matrix3d lookAlong(Vector3dc var1, Vector3dc var2) {
      return this.lookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), this);
   }

   public Matrix3d lookAlong(Vector3dc var1, Vector3dc var2, Matrix3d var3) {
      return this.lookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public Matrix3d lookAlong(double var1, double var3, double var5, double var7, double var9, double var11, Matrix3d var13) {
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
      double var60 = this.m00 * var24 + this.m10 * var32 + this.m20 * var46;
      double var62 = this.m01 * var24 + this.m11 * var32 + this.m21 * var46;
      double var64 = this.m02 * var24 + this.m12 * var32 + this.m22 * var46;
      var13.m20 = this.m00 * var26 + this.m10 * var34 + this.m20 * var52;
      var13.m21 = this.m01 * var26 + this.m11 * var34 + this.m21 * var52;
      var13.m22 = this.m02 * var26 + this.m12 * var34 + this.m22 * var52;
      var13.m00 = var54;
      var13.m01 = var56;
      var13.m02 = var58;
      var13.m10 = var60;
      var13.m11 = var62;
      var13.m12 = var64;
      return var13;
   }

   public Matrix3d lookAlong(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.lookAlong(var1, var3, var5, var7, var9, var11, this);
   }

   public Matrix3d setLookAlong(Vector3dc var1, Vector3dc var2) {
      return this.setLookAlong(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z());
   }

   public Matrix3d setLookAlong(double var1, double var3, double var5, double var7, double var9, double var11) {
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
      this.m10 = var23;
      this.m11 = var31;
      this.m12 = -var17;
      this.m20 = var25;
      this.m21 = var33;
      this.m22 = -var19;
      return this;
   }

   public Vector3d getScale(Vector3d var1) {
      var1.x = Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
      var1.y = Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
      var1.z = Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
      return var1;
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

   public int hashCode() {
      byte var2 = 1;
      long var3 = Double.doubleToLongBits(this.m00);
      int var5 = 31 * var2 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m01);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m02);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m10);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m11);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m12);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m20);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m21);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.m22);
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
         Matrix3d var2 = (Matrix3d)var1;
         if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(var2.m00)) {
            return false;
         } else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(var2.m01)) {
            return false;
         } else if (Double.doubleToLongBits(this.m02) != Double.doubleToLongBits(var2.m02)) {
            return false;
         } else if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(var2.m10)) {
            return false;
         } else if (Double.doubleToLongBits(this.m11) != Double.doubleToLongBits(var2.m11)) {
            return false;
         } else if (Double.doubleToLongBits(this.m12) != Double.doubleToLongBits(var2.m12)) {
            return false;
         } else if (Double.doubleToLongBits(this.m20) != Double.doubleToLongBits(var2.m20)) {
            return false;
         } else if (Double.doubleToLongBits(this.m21) != Double.doubleToLongBits(var2.m21)) {
            return false;
         } else {
            return Double.doubleToLongBits(this.m22) == Double.doubleToLongBits(var2.m22);
         }
      }
   }

   public Matrix3d swap(Matrix3d var1) {
      double var2 = this.m00;
      this.m00 = var1.m00;
      var1.m00 = var2;
      var2 = this.m01;
      this.m01 = var1.m01;
      var1.m01 = var2;
      var2 = this.m02;
      this.m02 = var1.m02;
      var1.m02 = var2;
      var2 = this.m10;
      this.m10 = var1.m10;
      var1.m10 = var2;
      var2 = this.m11;
      this.m11 = var1.m11;
      var1.m11 = var2;
      var2 = this.m12;
      this.m12 = var1.m12;
      var1.m12 = var2;
      var2 = this.m20;
      this.m20 = var1.m20;
      var1.m20 = var2;
      var2 = this.m21;
      this.m21 = var1.m21;
      var1.m21 = var2;
      var2 = this.m22;
      this.m22 = var1.m22;
      var1.m22 = var2;
      return this;
   }

   public Matrix3d add(Matrix3dc var1) {
      return this.add(var1, this);
   }

   public Matrix3d add(Matrix3dc var1, Matrix3d var2) {
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

   public Matrix3d sub(Matrix3dc var1) {
      return this.sub(var1, this);
   }

   public Matrix3d sub(Matrix3dc var1, Matrix3d var2) {
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

   public Matrix3d mulComponentWise(Matrix3dc var1) {
      return this.mulComponentWise(var1, this);
   }

   public Matrix3d mulComponentWise(Matrix3dc var1, Matrix3d var2) {
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

   public Matrix3d setSkewSymmetric(double var1, double var3, double var5) {
      this.m00 = this.m11 = this.m22 = 0.0D;
      this.m01 = -var1;
      this.m02 = var3;
      this.m10 = var1;
      this.m12 = -var5;
      this.m20 = -var3;
      this.m21 = var5;
      return this;
   }

   public Matrix3d lerp(Matrix3dc var1, double var2) {
      return this.lerp(var1, var2, this);
   }

   public Matrix3d lerp(Matrix3dc var1, double var2, Matrix3d var4) {
      var4.m00 = this.m00 + (var1.m00() - this.m00) * var2;
      var4.m01 = this.m01 + (var1.m01() - this.m01) * var2;
      var4.m02 = this.m02 + (var1.m02() - this.m02) * var2;
      var4.m10 = this.m10 + (var1.m10() - this.m10) * var2;
      var4.m11 = this.m11 + (var1.m11() - this.m11) * var2;
      var4.m12 = this.m12 + (var1.m12() - this.m12) * var2;
      var4.m20 = this.m20 + (var1.m20() - this.m20) * var2;
      var4.m21 = this.m21 + (var1.m21() - this.m21) * var2;
      var4.m22 = this.m22 + (var1.m22() - this.m22) * var2;
      return var4;
   }

   public Matrix3d rotateTowards(Vector3dc var1, Vector3dc var2, Matrix3d var3) {
      return this.rotateTowards(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), var3);
   }

   public Matrix3d rotateTowards(Vector3dc var1, Vector3dc var2) {
      return this.rotateTowards(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z(), this);
   }

   public Matrix3d rotateTowards(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.rotateTowards(var1, var3, var5, var7, var9, var11, this);
   }

   public Matrix3d rotateTowards(double var1, double var3, double var5, double var7, double var9, double var11, Matrix3d var13) {
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
      double var54 = this.m00 * var22 + this.m10 * var24 + this.m20 * var26;
      double var56 = this.m01 * var22 + this.m11 * var24 + this.m21 * var26;
      double var58 = this.m02 * var22 + this.m12 * var24 + this.m22 * var26;
      double var60 = this.m00 * var30 + this.m10 * var32 + this.m20 * var34;
      double var62 = this.m01 * var30 + this.m11 * var32 + this.m21 * var34;
      double var64 = this.m02 * var30 + this.m12 * var32 + this.m22 * var34;
      var13.m20 = this.m00 * var16 + this.m10 * var18 + this.m20 * var20;
      var13.m21 = this.m01 * var16 + this.m11 * var18 + this.m21 * var20;
      var13.m22 = this.m02 * var16 + this.m12 * var18 + this.m22 * var20;
      var13.m00 = var54;
      var13.m01 = var56;
      var13.m02 = var58;
      var13.m10 = var60;
      var13.m11 = var62;
      var13.m12 = var64;
      return var13;
   }

   public Matrix3d rotationTowards(Vector3dc var1, Vector3dc var2) {
      return this.rotationTowards(var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z());
   }

   public Matrix3d rotationTowards(double var1, double var3, double var5, double var7, double var9, double var11) {
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
      this.m10 = var29;
      this.m11 = var31;
      this.m12 = var33;
      this.m20 = var15;
      this.m21 = var17;
      this.m22 = var19;
      return this;
   }

   public Vector3d getEulerAnglesZYX(Vector3d var1) {
      var1.x = (double)((float)Math.atan2(this.m12, this.m22));
      var1.y = (double)((float)Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22)));
      var1.z = (double)((float)Math.atan2(this.m01, this.m00));
      return var1;
   }

   public Matrix3dc toImmutable() {
      return (Matrix3dc)(!Options.DEBUG ? this : new Matrix3d.Proxy(this));
   }

   private final class Proxy implements Matrix3dc {
      private final Matrix3dc delegate;

      Proxy(Matrix3dc var2) {
         this.delegate = var2;
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

      public double m10() {
         return this.delegate.m10();
      }

      public double m11() {
         return this.delegate.m11();
      }

      public double m12() {
         return this.delegate.m12();
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

      public Matrix3d mul(Matrix3dc var1, Matrix3d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Matrix3d mul(Matrix3fc var1, Matrix3d var2) {
         return this.delegate.mul(var1, var2);
      }

      public double determinant() {
         return this.delegate.determinant();
      }

      public Matrix3d invert(Matrix3d var1) {
         return this.delegate.invert(var1);
      }

      public Matrix3d transpose(Matrix3d var1) {
         return this.delegate.transpose(var1);
      }

      public Matrix3d get(Matrix3d var1) {
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

      public Matrix3d scale(Vector3dc var1, Matrix3d var2) {
         return this.delegate.scale(var1, var2);
      }

      public Matrix3d scale(double var1, double var3, double var5, Matrix3d var7) {
         return this.delegate.scale(var1, var3, var5, var7);
      }

      public Matrix3d scale(double var1, Matrix3d var3) {
         return this.delegate.scale(var1, var3);
      }

      public Matrix3d scaleLocal(double var1, double var3, double var5, Matrix3d var7) {
         return this.delegate.scaleLocal(var1, var3, var5, var7);
      }

      public Vector3d transform(Vector3d var1) {
         return this.delegate.transform(var1);
      }

      public Vector3d transform(Vector3dc var1, Vector3d var2) {
         return this.delegate.transform(var1, var2);
      }

      public Vector3d transform(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.transform(var1, var3, var5, var7);
      }

      public Matrix3d rotateX(double var1, Matrix3d var3) {
         return this.delegate.rotateX(var1, var3);
      }

      public Matrix3d rotateY(double var1, Matrix3d var3) {
         return this.delegate.rotateY(var1, var3);
      }

      public Matrix3d rotateZ(double var1, Matrix3d var3) {
         return this.delegate.rotateZ(var1, var3);
      }

      public Matrix3d rotateXYZ(double var1, double var3, double var5, Matrix3d var7) {
         return this.delegate.rotateXYZ(var1, var3, var5, var7);
      }

      public Matrix3d rotateZYX(double var1, double var3, double var5, Matrix3d var7) {
         return this.delegate.rotateZYX(var1, var3, var5, var7);
      }

      public Matrix3d rotateYXZ(double var1, double var3, double var5, Matrix3d var7) {
         return this.delegate.rotateYXZ(var1, var3, var5, var7);
      }

      public Matrix3d rotate(double var1, double var3, double var5, double var7, Matrix3d var9) {
         return this.delegate.rotate(var1, var3, var5, var7, var9);
      }

      public Matrix3d rotateLocal(double var1, double var3, double var5, double var7, Matrix3d var9) {
         return this.delegate.rotateLocal(var1, var3, var5, var7, var9);
      }

      public Matrix3d rotateLocal(Quaterniondc var1, Matrix3d var2) {
         return this.delegate.rotateLocal(var1, var2);
      }

      public Matrix3d rotateLocal(Quaternionfc var1, Matrix3d var2) {
         return this.delegate.rotateLocal(var1, var2);
      }

      public Matrix3d rotate(Quaterniondc var1, Matrix3d var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Matrix3d rotate(Quaternionfc var1, Matrix3d var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Matrix3d rotate(AxisAngle4f var1, Matrix3d var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Matrix3d rotate(AxisAngle4d var1, Matrix3d var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Matrix3d rotate(double var1, Vector3dc var3, Matrix3d var4) {
         return this.delegate.rotate(var1, var3, var4);
      }

      public Matrix3d rotate(double var1, Vector3fc var3, Matrix3d var4) {
         return this.delegate.rotate(var1, var3, var4);
      }

      public Vector3d getRow(int var1, Vector3d var2) throws IndexOutOfBoundsException {
         return this.delegate.getRow(var1, var2);
      }

      public Vector3d getColumn(int var1, Vector3d var2) throws IndexOutOfBoundsException {
         return this.delegate.getColumn(var1, var2);
      }

      public Matrix3d normal(Matrix3d var1) {
         return this.delegate.normal(var1);
      }

      public Matrix3d lookAlong(Vector3dc var1, Vector3dc var2, Matrix3d var3) {
         return this.delegate.lookAlong(var1, var2, var3);
      }

      public Matrix3d lookAlong(double var1, double var3, double var5, double var7, double var9, double var11, Matrix3d var13) {
         return this.delegate.lookAlong(var1, var3, var5, var7, var9, var11, var13);
      }

      public Vector3d getScale(Vector3d var1) {
         return this.delegate.getScale(var1);
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

      public Matrix3d add(Matrix3dc var1, Matrix3d var2) {
         return this.delegate.add(var1, var2);
      }

      public Matrix3d sub(Matrix3dc var1, Matrix3d var2) {
         return this.delegate.sub(var1, var2);
      }

      public Matrix3d mulComponentWise(Matrix3dc var1, Matrix3d var2) {
         return this.delegate.mulComponentWise(var1, var2);
      }

      public Matrix3d lerp(Matrix3dc var1, double var2, Matrix3d var4) {
         return this.delegate.lerp(var1, var2, var4);
      }

      public Matrix3d rotateTowards(Vector3dc var1, Vector3dc var2, Matrix3d var3) {
         return this.delegate.rotateTowards(var1, var2, var3);
      }

      public Matrix3d rotateTowards(double var1, double var3, double var5, double var7, double var9, double var11, Matrix3d var13) {
         return this.delegate.rotateTowards(var1, var3, var5, var7, var9, var11, var13);
      }

      public Vector3d getEulerAnglesZYX(Vector3d var1) {
         return this.delegate.getEulerAnglesZYX(var1);
      }
   }
}
