package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AxisAngle4d implements Externalizable {
   private static final long serialVersionUID = 1L;
   public double angle;
   public double x;
   public double y;
   public double z;

   public AxisAngle4d() {
      this.z = 1.0D;
   }

   public AxisAngle4d(AxisAngle4d var1) {
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
      this.angle = (var1.angle < 0.0D ? 6.283185307179586D + var1.angle % 6.283185307179586D : var1.angle) % 6.283185307179586D;
   }

   public AxisAngle4d(AxisAngle4f var1) {
      this.x = (double)var1.x;
      this.y = (double)var1.y;
      this.z = (double)var1.z;
      this.angle = ((double)var1.angle < 0.0D ? 6.283185307179586D + (double)var1.angle % 6.283185307179586D : (double)var1.angle) % 6.283185307179586D;
   }

   public AxisAngle4d(Quaternionfc var1) {
      double var2 = Math.acos((double)var1.w());
      double var4 = 1.0D / Math.sqrt(1.0D - (double)(var1.w() * var1.w()));
      this.x = (double)var1.x() * var4;
      this.y = (double)var1.y() * var4;
      this.z = (double)var1.z() * var4;
      this.angle = var2 + var2;
   }

   public AxisAngle4d(Quaterniondc var1) {
      double var2 = Math.acos(var1.w());
      double var4 = 1.0D / Math.sqrt(1.0D - var1.w() * var1.w());
      this.x = var1.x() * var4;
      this.y = var1.y() * var4;
      this.z = var1.z() * var4;
      this.angle = var2 + var2;
   }

   public AxisAngle4d(double var1, double var3, double var5, double var7) {
      this.x = var3;
      this.y = var5;
      this.z = var7;
      this.angle = (var1 < 0.0D ? 6.283185307179586D + var1 % 6.283185307179586D : var1) % 6.283185307179586D;
   }

   public AxisAngle4d(double var1, Vector3dc var3) {
      this(var1, var3.x(), var3.y(), var3.z());
   }

   public AxisAngle4d(double var1, Vector3f var3) {
      this(var1, (double)var3.x, (double)var3.y, (double)var3.z);
   }

   public AxisAngle4d set(AxisAngle4d var1) {
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
      this.angle = (var1.angle < 0.0D ? 6.283185307179586D + var1.angle % 6.283185307179586D : var1.angle) % 6.283185307179586D;
      return this;
   }

   public AxisAngle4d set(AxisAngle4f var1) {
      this.x = (double)var1.x;
      this.y = (double)var1.y;
      this.z = (double)var1.z;
      this.angle = ((double)var1.angle < 0.0D ? 6.283185307179586D + (double)var1.angle % 6.283185307179586D : (double)var1.angle) % 6.283185307179586D;
      return this;
   }

   public AxisAngle4d set(double var1, double var3, double var5, double var7) {
      this.x = var3;
      this.y = var5;
      this.z = var7;
      this.angle = (var1 < 0.0D ? 6.283185307179586D + var1 % 6.283185307179586D : var1) % 6.283185307179586D;
      return this;
   }

   public AxisAngle4d set(double var1, Vector3dc var3) {
      return this.set(var1, var3.x(), var3.y(), var3.z());
   }

   public AxisAngle4d set(double var1, Vector3f var3) {
      return this.set(var1, (double)var3.x, (double)var3.y, (double)var3.z);
   }

   public AxisAngle4d set(Quaternionfc var1) {
      double var2 = Math.acos((double)var1.w());
      double var4 = 1.0D / Math.sqrt(1.0D - (double)(var1.w() * var1.w()));
      this.x = (double)var1.x() * var4;
      this.y = (double)var1.y() * var4;
      this.z = (double)var1.z() * var4;
      this.angle = var2 + var2;
      return this;
   }

   public AxisAngle4d set(Quaterniondc var1) {
      double var2 = Math.acos(var1.w());
      double var4 = 1.0D / Math.sqrt(1.0D - var1.w() * var1.w());
      this.x = var1.x() * var4;
      this.y = var1.y() * var4;
      this.z = var1.z() * var4;
      this.angle = var2 + var2;
      return this;
   }

   public AxisAngle4d set(Matrix3fc var1) {
      double var2 = ((double)(var1.m00() + var1.m11() + var1.m22()) - 1.0D) * 0.5D;
      this.x = (double)(var1.m12() - var1.m21());
      this.y = (double)(var1.m20() - var1.m02());
      this.z = (double)(var1.m01() - var1.m10());
      double var4 = 0.5D * Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      this.angle = Math.atan2(var4, var2);
      return this;
   }

   public AxisAngle4d set(Matrix3dc var1) {
      double var2 = (var1.m00() + var1.m11() + var1.m22() - 1.0D) * 0.5D;
      this.x = var1.m12() - var1.m21();
      this.y = var1.m20() - var1.m02();
      this.z = var1.m01() - var1.m10();
      double var4 = 0.5D * Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      this.angle = Math.atan2(var4, var2);
      return this;
   }

   public AxisAngle4d set(Matrix4fc var1) {
      double var2 = ((double)(var1.m00() + var1.m11() + var1.m22()) - 1.0D) * 0.5D;
      this.x = (double)(var1.m12() - var1.m21());
      this.y = (double)(var1.m20() - var1.m02());
      this.z = (double)(var1.m01() - var1.m10());
      double var4 = 0.5D * Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      this.angle = Math.atan2(var4, var2);
      return this;
   }

   public AxisAngle4d set(Matrix4x3fc var1) {
      double var2 = ((double)(var1.m00() + var1.m11() + var1.m22()) - 1.0D) * 0.5D;
      this.x = (double)(var1.m12() - var1.m21());
      this.y = (double)(var1.m20() - var1.m02());
      this.z = (double)(var1.m01() - var1.m10());
      double var4 = 0.5D * Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      this.angle = Math.atan2(var4, var2);
      return this;
   }

   public AxisAngle4d set(Matrix4dc var1) {
      double var2 = (var1.m00() + var1.m11() + var1.m22() - 1.0D) * 0.5D;
      this.x = var1.m12() - var1.m21();
      this.y = var1.m20() - var1.m02();
      this.z = var1.m01() - var1.m10();
      double var4 = 0.5D * Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      this.angle = Math.atan2(var4, var2);
      return this;
   }

   public Quaternionf get(Quaternionf var1) {
      return var1.set(this);
   }

   public Quaterniond get(Quaterniond var1) {
      return var1.set(this);
   }

   public Matrix4f get(Matrix4f var1) {
      return var1.set(this);
   }

   public Matrix3f get(Matrix3f var1) {
      return var1.set(this);
   }

   public Matrix4d get(Matrix4d var1) {
      return var1.set(this);
   }

   public Matrix3d get(Matrix3d var1) {
      return var1.set(this);
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeDouble(this.angle);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.angle = var1.readDouble();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
   }

   public AxisAngle4d normalize() {
      double var1 = 1.0D / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      return this;
   }

   public AxisAngle4d rotate(double var1) {
      this.angle += var1;
      this.angle = (this.angle < 0.0D ? 6.283185307179586D + this.angle % 6.283185307179586D : this.angle) % 6.283185307179586D;
      return this;
   }

   public Vector3d transform(Vector3d var1) {
      return this.transform((Vector3dc)var1, (Vector3d)var1);
   }

   public Vector3d transform(Vector3dc var1, Vector3d var2) {
      double var3 = Math.cos(this.angle);
      double var5 = Math.sin(this.angle);
      double var7 = this.x * var1.x() + this.y * var1.y() + this.z * var1.z();
      var2.set(var1.x() * var3 + var5 * (this.y * var1.z() - this.z * var1.y()) + (1.0D - var3) * var7 * this.x, var1.y() * var3 + var5 * (this.z * var1.x() - this.x * var1.z()) + (1.0D - var3) * var7 * this.y, var1.z() * var3 + var5 * (this.x * var1.y() - this.y * var1.x()) + (1.0D - var3) * var7 * this.z);
      return var2;
   }

   public Vector4d transform(Vector4d var1) {
      return this.transform(var1, var1);
   }

   public Vector4d transform(Vector4d var1, Vector4d var2) {
      double var3 = Math.cos(this.angle);
      double var5 = Math.sin(this.angle);
      double var7 = this.x * var1.x + this.y * var1.y + this.z * var1.z;
      var2.set(var1.x * var3 + var5 * (this.y * var1.z - this.z * var1.y) + (1.0D - var3) * var7 * this.x, var1.y * var3 + var5 * (this.z * var1.x - this.x * var1.z) + (1.0D - var3) * var7 * this.y, var1.z * var3 + var5 * (this.x * var1.y - this.y * var1.x) + (1.0D - var3) * var7 * this.z, var2.w);
      return var2;
   }

   public String toString() {
      DecimalFormat var1 = new DecimalFormat(" 0.000E0;-");
      return this.toString(var1).replaceAll("E(\\d+)", "E+$1");
   }

   public String toString(NumberFormat var1) {
      return "(" + var1.format(this.x) + var1.format(this.y) + var1.format(this.z) + " <|" + var1.format(this.angle) + " )";
   }

   public int hashCode() {
      byte var2 = 1;
      long var3 = Double.doubleToLongBits((this.angle < 0.0D ? 6.283185307179586D + this.angle % 6.283185307179586D : this.angle) % 6.283185307179586D);
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
         AxisAngle4d var2 = (AxisAngle4d)var1;
         if (Double.doubleToLongBits((this.angle < 0.0D ? 6.283185307179586D + this.angle % 6.283185307179586D : this.angle) % 6.283185307179586D) != Double.doubleToLongBits((var2.angle < 0.0D ? 6.283185307179586D + var2.angle % 6.283185307179586D : var2.angle) % 6.283185307179586D)) {
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
}
