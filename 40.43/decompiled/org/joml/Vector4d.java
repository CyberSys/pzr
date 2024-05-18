package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Vector4d implements Externalizable, Vector4dc {
   private static final long serialVersionUID = 1L;
   public double x;
   public double y;
   public double z;
   public double w;

   public Vector4d() {
      this.w = 1.0D;
   }

   public Vector4d(Vector4dc var1) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      this.w = var1.w();
   }

   public Vector4d(Vector3dc var1, double var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      this.w = var2;
   }

   public Vector4d(Vector2dc var1, double var2, double var4) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
      this.w = var4;
   }

   public Vector4d(double var1) {
      this(var1, var1, var1, var1);
   }

   public Vector4d(Vector4fc var1) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = (double)var1.z();
      this.w = (double)var1.w();
   }

   public Vector4d(Vector3fc var1, double var2) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = (double)var1.z();
      this.w = var2;
   }

   public Vector4d(Vector2fc var1, double var2, double var4) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = var2;
      this.w = var4;
   }

   public Vector4d(double var1, double var3, double var5, double var7) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.w = var7;
   }

   public Vector4d(ByteBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector4d(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public Vector4d(DoubleBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector4d(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
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

   public Vector4d set(Vector4dc var1) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      this.w = var1.w();
      return this;
   }

   public Vector4d set(Vector4fc var1) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = (double)var1.z();
      this.w = (double)var1.w();
      return this;
   }

   public Vector4d set(Vector3dc var1, double var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      this.w = var2;
      return this;
   }

   public Vector4d set(Vector3fc var1, double var2) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = (double)var1.z();
      this.w = var2;
      return this;
   }

   public Vector4d set(Vector2dc var1, double var2, double var4) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
      this.w = var4;
      return this;
   }

   public Vector4d set(double var1) {
      return this.set(var1, var1, var1, var1);
   }

   public Vector4d set(Vector2fc var1, double var2, double var4) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = var2;
      this.w = var4;
      return this;
   }

   public Vector4d set(double var1, double var3, double var5, double var7) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.w = var7;
      return this;
   }

   public Vector4d set(ByteBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector4d set(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector4d set(DoubleBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector4d set(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector4d setComponent(int var1, double var2) throws IllegalArgumentException {
      switch(var1) {
      case 0:
         this.x = var2;
         break;
      case 1:
         this.y = var2;
         break;
      case 2:
         this.z = var2;
         break;
      case 3:
         this.w = var2;
         break;
      default:
         throw new IllegalArgumentException();
      }

      return this;
   }

   public ByteBuffer get(ByteBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public ByteBuffer get(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.put(this, var1, var2);
      return var2;
   }

   public DoubleBuffer get(DoubleBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public DoubleBuffer get(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.put(this, var1, var2);
      return var2;
   }

   public Vector4d sub(Vector4dc var1) {
      this.x -= var1.x();
      this.y -= var1.y();
      this.z -= var1.z();
      this.w -= var1.w();
      return this;
   }

   public Vector4d sub(Vector4fc var1) {
      this.x -= (double)var1.x();
      this.y -= (double)var1.y();
      this.z -= (double)var1.z();
      this.w -= (double)var1.w();
      return this;
   }

   public Vector4d sub(double var1, double var3, double var5, double var7) {
      this.x -= var1;
      this.y -= var3;
      this.z -= var5;
      this.w -= var7;
      return this;
   }

   public Vector4d sub(double var1, double var3, double var5, double var7, Vector4d var9) {
      var9.x = this.x - var1;
      var9.y = this.y - var3;
      var9.z = this.z - var5;
      var9.w = this.w - var7;
      return var9;
   }

   public Vector4d add(Vector4dc var1) {
      this.x += var1.x();
      this.y += var1.y();
      this.z += var1.z();
      this.w += var1.w();
      return this;
   }

   public Vector4d add(double var1, double var3, double var5, double var7) {
      this.x += var1;
      this.y += var3;
      this.z += var5;
      this.w += var7;
      return this;
   }

   public Vector4d add(double var1, double var3, double var5, double var7, Vector4d var9) {
      var9.x = this.x - var1;
      var9.y = this.y - var3;
      var9.z = this.z - var5;
      var9.w = this.w - var7;
      return var9;
   }

   public Vector4d add(Vector4fc var1) {
      this.x += (double)var1.x();
      this.y += (double)var1.y();
      this.z += (double)var1.z();
      this.w += (double)var1.w();
      return this;
   }

   public Vector4d fma(Vector4dc var1, Vector4dc var2) {
      this.x += var1.x() * var2.x();
      this.y += var1.y() * var2.y();
      this.z += var1.z() * var2.z();
      this.w += var1.w() * var2.w();
      return this;
   }

   public Vector4d fma(double var1, Vector4dc var3) {
      this.x += var1 * var3.x();
      this.y += var1 * var3.y();
      this.z += var1 * var3.z();
      this.w += var1 * var3.w();
      return this;
   }

   public Vector4d fma(Vector4dc var1, Vector4dc var2, Vector4d var3) {
      var3.x = this.x + var1.x() * var2.x();
      var3.y = this.y + var1.y() * var2.y();
      var3.z = this.z + var1.z() * var2.z();
      var3.w = this.w + var1.w() * var2.w();
      return var3;
   }

   public Vector4d fma(double var1, Vector4dc var3, Vector4d var4) {
      var4.x = this.x + var1 * var3.x();
      var4.y = this.y + var1 * var3.y();
      var4.z = this.z + var1 * var3.z();
      var4.w = this.w + var1 * var3.w();
      return var4;
   }

   public Vector4d mul(Vector4dc var1) {
      this.x *= var1.x();
      this.y *= var1.y();
      this.z *= var1.z();
      this.z *= var1.w();
      return this;
   }

   public Vector4d mul(Vector4dc var1, Vector4d var2) {
      var2.x = this.x * var1.x();
      var2.y = this.y * var1.y();
      var2.z = this.z * var1.z();
      var2.w = this.w * var1.w();
      return var2;
   }

   public Vector4d div(Vector4dc var1) {
      this.x /= var1.x();
      this.y /= var1.y();
      this.z /= var1.z();
      this.z /= var1.w();
      return this;
   }

   public Vector4d div(Vector4dc var1, Vector4d var2) {
      var2.x = this.x / var1.x();
      var2.y = this.y / var1.y();
      var2.z = this.z / var1.z();
      var2.w = this.w / var1.w();
      return var2;
   }

   public Vector4d mul(Vector4fc var1) {
      this.x *= (double)var1.x();
      this.y *= (double)var1.y();
      this.z *= (double)var1.z();
      this.z *= (double)var1.w();
      return this;
   }

   public Vector4d mul(Matrix4dc var1) {
      return this.mul(var1, this);
   }

   public Vector4d mul(Matrix4dc var1, Vector4d var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30() * this.w, var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31() * this.w, var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32() * this.w, var1.m03() * this.x + var1.m13() * this.y + var1.m23() * this.z + var1.m33() * this.w);
      return var2;
   }

   public Vector4d mul(Matrix4x3dc var1) {
      return this.mul(var1, this);
   }

   public Vector4d mul(Matrix4x3dc var1, Vector4d var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30() * this.w, var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31() * this.w, var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32() * this.w, this.w);
      return var2;
   }

   public Vector4d mul(Matrix4x3fc var1) {
      return this.mul(var1, this);
   }

   public Vector4d mul(Matrix4x3fc var1, Vector4d var2) {
      var2.set((double)var1.m00() * this.x + (double)var1.m10() * this.y + (double)var1.m20() * this.z + (double)var1.m30() * this.w, (double)var1.m01() * this.x + (double)var1.m11() * this.y + (double)var1.m21() * this.z + (double)var1.m31() * this.w, (double)var1.m02() * this.x + (double)var1.m12() * this.y + (double)var1.m22() * this.z + (double)var1.m32() * this.w, this.w);
      return var2;
   }

   public Vector4d mul(Matrix4fc var1) {
      return this.mul(var1, this);
   }

   public Vector4d mul(Matrix4fc var1, Vector4d var2) {
      var2.set((double)var1.m00() * this.x + (double)var1.m10() * this.y + (double)var1.m20() * this.z + (double)var1.m30() * this.w, (double)var1.m01() * this.x + (double)var1.m11() * this.y + (double)var1.m21() * this.z + (double)var1.m31() * this.w, (double)var1.m02() * this.x + (double)var1.m12() * this.y + (double)var1.m22() * this.z + (double)var1.m32() * this.w, (double)var1.m03() * this.x + (double)var1.m13() * this.y + (double)var1.m23() * this.z + (double)var1.m33() * this.w);
      return var2;
   }

   public Vector4d mulProject(Matrix4dc var1, Vector4d var2) {
      double var3 = 1.0D / (var1.m03() * this.x + var1.m13() * this.y + var1.m23() * this.z + var1.m33() * this.w);
      var2.set((var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30() * this.w) * var3, (var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31() * this.w) * var3, (var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32() * this.w) * var3, 1.0D);
      return var2;
   }

   public Vector4d mulProject(Matrix4dc var1) {
      return this.mulProject(var1, this);
   }

   public Vector4d mul(double var1) {
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      this.w *= var1;
      return this;
   }

   public Vector4d mul(double var1, Vector4d var3) {
      var3.x = this.x * var1;
      var3.y = this.y * var1;
      var3.z = this.z * var1;
      var3.w = this.w * var1;
      return var3;
   }

   public Vector4d div(double var1) {
      this.x /= var1;
      this.y /= var1;
      this.z /= var1;
      this.w /= var1;
      return this;
   }

   public Vector4d div(double var1, Vector4d var3) {
      var3.x = this.x / var1;
      var3.y = this.y / var1;
      var3.z = this.z / var1;
      var3.w = this.w / var1;
      return var3;
   }

   public Vector4d rotate(Quaterniondc var1) {
      return this.rotate(var1, this);
   }

   public Vector4d rotate(Quaterniondc var1, Vector4d var2) {
      var1.transform((Vector4dc)this, (Vector4d)var2);
      return var2;
   }

   public double lengthSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
   }

   public double length() {
      return Math.sqrt(this.lengthSquared());
   }

   public Vector4d normalize() {
      double var1 = 1.0D / this.length();
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      this.w *= var1;
      return this;
   }

   public Vector4d normalize(Vector4d var1) {
      double var2 = 1.0D / this.length();
      var1.x = this.x * var2;
      var1.y = this.y * var2;
      var1.z = this.z * var2;
      var1.w = this.w * var2;
      return var1;
   }

   public Vector4d normalize3() {
      double var1 = 1.0D / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      this.w *= var1;
      return this;
   }

   public Vector4d normalize3(Vector4d var1) {
      double var2 = 1.0D / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      var1.x = this.x * var2;
      var1.y = this.y * var2;
      var1.z = this.z * var2;
      var1.w = this.w * var2;
      return var1;
   }

   public double distance(Vector4dc var1) {
      double var2 = var1.x() - this.x;
      double var4 = var1.y() - this.y;
      double var6 = var1.z() - this.z;
      double var8 = var1.w() - this.w;
      return Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6 + var8 * var8);
   }

   public double distance(double var1, double var3, double var5, double var7) {
      double var9 = this.x - var1;
      double var11 = this.y - var3;
      double var13 = this.z - var5;
      double var15 = this.w - var7;
      return Math.sqrt(var9 * var9 + var11 * var11 + var13 * var13 + var15 * var15);
   }

   public double dot(Vector4dc var1) {
      return this.x * var1.x() + this.y * var1.y() + this.z * var1.z() + this.w * var1.w();
   }

   public double dot(double var1, double var3, double var5, double var7) {
      return this.x * var1 + this.y * var3 + this.z * var5 + this.w * var7;
   }

   public double angleCos(Vector4dc var1) {
      double var2 = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
      double var4 = var1.x() * var1.x() + var1.y() * var1.y() + var1.z() * var1.z() + var1.w() * var1.w();
      double var6 = this.x * var1.x() + this.y * var1.y() + this.z * var1.z() + this.w * var1.w();
      return var6 / Math.sqrt(var2 * var4);
   }

   public double angle(Vector4dc var1) {
      double var2 = this.angleCos(var1);
      var2 = var2 < 1.0D ? var2 : 1.0D;
      var2 = var2 > -1.0D ? var2 : -1.0D;
      return Math.acos(var2);
   }

   public Vector4d zero() {
      this.x = 0.0D;
      this.y = 0.0D;
      this.z = 0.0D;
      this.w = 0.0D;
      return this;
   }

   public Vector4d negate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
      this.w = -this.w;
      return this;
   }

   public Vector4d negate(Vector4d var1) {
      var1.x = -this.x;
      var1.y = -this.y;
      var1.z = -this.z;
      var1.w = -this.w;
      return var1;
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
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
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
         Vector4d var2 = (Vector4d)var1;
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

   public Vector4d smoothStep(Vector4dc var1, double var2, Vector4d var4) {
      double var5 = var2 * var2;
      double var7 = var5 * var2;
      var4.x = (this.x + this.x - var1.x() - var1.x()) * var7 + (3.0D * var1.x() - 3.0D * this.x) * var5 + this.x * var2 + this.x;
      var4.y = (this.y + this.y - var1.y() - var1.y()) * var7 + (3.0D * var1.y() - 3.0D * this.y) * var5 + this.y * var2 + this.y;
      var4.z = (this.z + this.z - var1.z() - var1.z()) * var7 + (3.0D * var1.z() - 3.0D * this.z) * var5 + this.z * var2 + this.z;
      var4.w = (this.w + this.w - var1.w() - var1.w()) * var7 + (3.0D * var1.w() - 3.0D * this.w) * var5 + this.w * var2 + this.w;
      return var4;
   }

   public Vector4d hermite(Vector4dc var1, Vector4dc var2, Vector4dc var3, double var4, Vector4d var6) {
      double var7 = var4 * var4;
      double var9 = var7 * var4;
      var6.x = (this.x + this.x - var2.x() - var2.x() + var3.x() + var1.x()) * var9 + (3.0D * var2.x() - 3.0D * this.x - var1.x() - var1.x() - var3.x()) * var7 + this.x * var4 + this.x;
      var6.y = (this.y + this.y - var2.y() - var2.y() + var3.y() + var1.y()) * var9 + (3.0D * var2.y() - 3.0D * this.y - var1.y() - var1.y() - var3.y()) * var7 + this.y * var4 + this.y;
      var6.z = (this.z + this.z - var2.z() - var2.z() + var3.z() + var1.z()) * var9 + (3.0D * var2.z() - 3.0D * this.z - var1.z() - var1.z() - var3.z()) * var7 + this.z * var4 + this.z;
      var6.w = (this.w + this.w - var2.w() - var2.w() + var3.w() + var1.w()) * var9 + (3.0D * var2.w() - 3.0D * this.w - var1.w() - var1.w() - var3.w()) * var7 + this.w * var4 + this.w;
      return var6;
   }

   public Vector4d lerp(Vector4dc var1, double var2) {
      return this.lerp(var1, var2, this);
   }

   public Vector4d lerp(Vector4dc var1, double var2, Vector4d var4) {
      var4.x = this.x + (var1.x() - this.x) * var2;
      var4.y = this.y + (var1.y() - this.y) * var2;
      var4.z = this.z + (var1.z() - this.z) * var2;
      var4.w = this.w + (var1.w() - this.w) * var2;
      return var4;
   }

   public Vector4dc toImmutable() {
      return (Vector4dc)(!Options.DEBUG ? this : new Vector4d.Proxy(this));
   }

   private final class Proxy implements Vector4dc {
      private final Vector4dc delegate;

      Proxy(Vector4dc var2) {
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

      public ByteBuffer get(ByteBuffer var1) {
         return this.delegate.get(var1);
      }

      public ByteBuffer get(int var1, ByteBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public DoubleBuffer get(DoubleBuffer var1) {
         return this.delegate.get(var1);
      }

      public DoubleBuffer get(int var1, DoubleBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public Vector4d sub(double var1, double var3, double var5, double var7, Vector4d var9) {
         return this.delegate.sub(var1, var3, var5, var7, var9);
      }

      public Vector4d add(double var1, double var3, double var5, double var7, Vector4d var9) {
         return this.delegate.add(var1, var3, var5, var7, var9);
      }

      public Vector4d fma(Vector4dc var1, Vector4dc var2, Vector4d var3) {
         return this.delegate.fma(var1, var2, var3);
      }

      public Vector4d fma(double var1, Vector4dc var3, Vector4d var4) {
         return this.delegate.fma(var1, var3, var4);
      }

      public Vector4d mul(Vector4dc var1, Vector4d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector4d div(Vector4dc var1, Vector4d var2) {
         return this.delegate.div(var1, var2);
      }

      public Vector4d mul(Matrix4dc var1, Vector4d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector4d mul(Matrix4x3dc var1, Vector4d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector4d mul(Matrix4x3fc var1, Vector4d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector4d mul(Matrix4fc var1, Vector4d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector4d mulProject(Matrix4dc var1, Vector4d var2) {
         return this.delegate.mulProject(var1, var2);
      }

      public Vector4d mul(double var1, Vector4d var3) {
         return this.delegate.mul(var1, var3);
      }

      public Vector4d div(double var1, Vector4d var3) {
         return this.delegate.div(var1, var3);
      }

      public Vector4d rotate(Quaterniondc var1, Vector4d var2) {
         return this.delegate.rotate(var1, var2);
      }

      public double lengthSquared() {
         return this.delegate.lengthSquared();
      }

      public double length() {
         return this.delegate.length();
      }

      public Vector4d normalize(Vector4d var1) {
         return this.delegate.normalize(var1);
      }

      public Vector4d normalize3(Vector4d var1) {
         return this.delegate.normalize3(var1);
      }

      public double distance(Vector4dc var1) {
         return this.delegate.distance(var1);
      }

      public double distance(double var1, double var3, double var5, double var7) {
         return this.delegate.distance(var1, var3, var5, var7);
      }

      public double dot(Vector4dc var1) {
         return this.delegate.dot(var1);
      }

      public double dot(double var1, double var3, double var5, double var7) {
         return this.delegate.dot(var1, var3, var5, var7);
      }

      public double angleCos(Vector4dc var1) {
         return this.delegate.angleCos(var1);
      }

      public double angle(Vector4dc var1) {
         return this.delegate.angle(var1);
      }

      public Vector4d negate(Vector4d var1) {
         return this.delegate.negate(var1);
      }

      public Vector4d smoothStep(Vector4dc var1, double var2, Vector4d var4) {
         return this.delegate.smoothStep(var1, var2, var4);
      }

      public Vector4d hermite(Vector4dc var1, Vector4dc var2, Vector4dc var3, double var4, Vector4d var6) {
         return this.delegate.hermite(var1, var2, var3, var4, var6);
      }

      public Vector4d lerp(Vector4dc var1, double var2, Vector4d var4) {
         return this.delegate.lerp(var1, var2, var4);
      }
   }
}
