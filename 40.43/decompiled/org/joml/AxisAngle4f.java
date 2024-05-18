package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AxisAngle4f implements Externalizable {
   private static final long serialVersionUID = 1L;
   public float angle;
   public float x;
   public float y;
   public float z;

   public AxisAngle4f() {
      this.z = 1.0F;
   }

   public AxisAngle4f(AxisAngle4f var1) {
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
      this.angle = (float)(((double)var1.angle < 0.0D ? 6.283185307179586D + (double)var1.angle % 6.283185307179586D : (double)var1.angle) % 6.283185307179586D);
   }

   public AxisAngle4f(Quaternionfc var1) {
      float var2 = (float)Math.acos((double)var1.w());
      float var3 = (float)(1.0D / Math.sqrt(1.0D - (double)(var1.w() * var1.w())));
      this.x = var1.x() * var3;
      this.y = var1.y() * var3;
      this.z = var1.z() * var3;
      this.angle = var2 + var2;
   }

   public AxisAngle4f(float var1, float var2, float var3, float var4) {
      this.x = var2;
      this.y = var3;
      this.z = var4;
      this.angle = (float)(((double)var1 < 0.0D ? 6.283185307179586D + (double)var1 % 6.283185307179586D : (double)var1) % 6.283185307179586D);
   }

   public AxisAngle4f(float var1, Vector3fc var2) {
      this(var1, var2.x(), var2.y(), var2.z());
   }

   public AxisAngle4f set(AxisAngle4f var1) {
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
      this.angle = var1.angle;
      this.angle = (float)(((double)this.angle < 0.0D ? 6.283185307179586D + (double)this.angle % 6.283185307179586D : (double)this.angle) % 6.283185307179586D);
      return this;
   }

   public AxisAngle4f set(float var1, float var2, float var3, float var4) {
      this.x = var2;
      this.y = var3;
      this.z = var4;
      this.angle = (float)(((double)var1 < 0.0D ? 6.283185307179586D + (double)var1 % 6.283185307179586D : (double)var1) % 6.283185307179586D);
      return this;
   }

   public AxisAngle4f set(float var1, Vector3fc var2) {
      return this.set(var1, var2.x(), var2.y(), var2.z());
   }

   public AxisAngle4f set(Quaternionfc var1) {
      double var2 = Math.acos((double)var1.w());
      double var4 = 1.0D / Math.sqrt(1.0D - (double)(var1.w() * var1.w()));
      this.x = (float)((double)var1.x() * var4);
      this.y = (float)((double)var1.y() * var4);
      this.z = (float)((double)var1.z() * var4);
      this.angle = (float)(var2 + var2);
      return this;
   }

   public AxisAngle4f set(Quaterniondc var1) {
      double var2 = Math.acos(var1.w());
      double var4 = 1.0D / Math.sqrt(1.0D - var1.w() * var1.w());
      this.x = (float)(var1.x() * var4);
      this.y = (float)(var1.y() * var4);
      this.z = (float)(var1.z() * var4);
      this.angle = (float)(var2 + var2);
      return this;
   }

   public AxisAngle4f set(Matrix3fc var1) {
      double var2 = ((double)(var1.m00() + var1.m11() + var1.m22()) - 1.0D) * 0.5D;
      this.x = var1.m12() - var1.m21();
      this.y = var1.m20() - var1.m02();
      this.z = var1.m01() - var1.m10();
      double var4 = 0.5D * Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
      this.angle = (float)Math.atan2(var4, var2);
      return this;
   }

   public AxisAngle4f set(Matrix3dc var1) {
      double var2 = (var1.m00() + var1.m11() + var1.m22() - 1.0D) * 0.5D;
      this.x = (float)(var1.m12() - var1.m21());
      this.y = (float)(var1.m20() - var1.m02());
      this.z = (float)(var1.m01() - var1.m10());
      double var4 = 0.5D * Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
      this.angle = (float)Math.atan2(var4, var2);
      return this;
   }

   public AxisAngle4f set(Matrix4fc var1) {
      double var2 = ((double)(var1.m00() + var1.m11() + var1.m22()) - 1.0D) * 0.5D;
      this.x = var1.m12() - var1.m21();
      this.y = var1.m20() - var1.m02();
      this.z = var1.m01() - var1.m10();
      double var4 = 0.5D * Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
      this.angle = (float)Math.atan2(var4, var2);
      return this;
   }

   public AxisAngle4f set(Matrix4x3fc var1) {
      double var2 = ((double)(var1.m00() + var1.m11() + var1.m22()) - 1.0D) * 0.5D;
      this.x = var1.m12() - var1.m21();
      this.y = var1.m20() - var1.m02();
      this.z = var1.m01() - var1.m10();
      double var4 = 0.5D * Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
      this.angle = (float)Math.atan2(var4, var2);
      return this;
   }

   public AxisAngle4f set(Matrix4dc var1) {
      double var2 = (var1.m00() + var1.m11() + var1.m22() - 1.0D) * 0.5D;
      this.x = (float)(var1.m12() - var1.m21());
      this.y = (float)(var1.m20() - var1.m02());
      this.z = (float)(var1.m01() - var1.m10());
      double var4 = 0.5D * Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
      this.angle = (float)Math.atan2(var4, var2);
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
      var1.writeFloat(this.angle);
      var1.writeFloat(this.x);
      var1.writeFloat(this.y);
      var1.writeFloat(this.z);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.angle = var1.readFloat();
      this.x = var1.readFloat();
      this.y = var1.readFloat();
      this.z = var1.readFloat();
   }

   public AxisAngle4f normalize() {
      float var1 = (float)(1.0D / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z)));
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      return this;
   }

   public AxisAngle4f rotate(float var1) {
      this.angle += var1;
      this.angle = (float)(((double)this.angle < 0.0D ? 6.283185307179586D + (double)this.angle % 6.283185307179586D : (double)this.angle) % 6.283185307179586D);
      return this;
   }

   public Vector3f transform(Vector3f var1) {
      return this.transform((Vector3fc)var1, (Vector3f)var1);
   }

   public Vector3f transform(Vector3fc var1, Vector3f var2) {
      double var3 = Math.cos((double)this.angle);
      double var5 = Math.sin((double)this.angle);
      float var7 = this.x * var1.x() + this.y * var1.y() + this.z * var1.z();
      var2.set((float)((double)var1.x() * var3 + var5 * (double)(this.y * var1.z() - this.z * var1.y()) + (1.0D - var3) * (double)var7 * (double)this.x), (float)((double)var1.y() * var3 + var5 * (double)(this.z * var1.x() - this.x * var1.z()) + (1.0D - var3) * (double)var7 * (double)this.y), (float)((double)var1.z() * var3 + var5 * (double)(this.x * var1.y() - this.y * var1.x()) + (1.0D - var3) * (double)var7 * (double)this.z));
      return var2;
   }

   public Vector4f transform(Vector4f var1) {
      return this.transform((Vector4fc)var1, (Vector4f)var1);
   }

   public Vector4f transform(Vector4fc var1, Vector4f var2) {
      double var3 = Math.cos((double)this.angle);
      double var5 = Math.sin((double)this.angle);
      float var7 = this.x * var1.x() + this.y * var1.y() + this.z * var1.z();
      var2.set((float)((double)var1.x() * var3 + var5 * (double)(this.y * var1.z() - this.z * var1.y()) + (1.0D - var3) * (double)var7 * (double)this.x), (float)((double)var1.y() * var3 + var5 * (double)(this.z * var1.x() - this.x * var1.z()) + (1.0D - var3) * (double)var7 * (double)this.y), (float)((double)var1.z() * var3 + var5 * (double)(this.x * var1.y() - this.y * var1.x()) + (1.0D - var3) * (double)var7 * (double)this.z), var2.w);
      return var2;
   }

   public String toString() {
      DecimalFormat var1 = new DecimalFormat(" 0.000E0;-");
      return this.toString(var1).replaceAll("E(\\d+)", "E+$1");
   }

   public String toString(NumberFormat var1) {
      return "(" + var1.format((double)this.x) + var1.format((double)this.y) + var1.format((double)this.z) + " <|" + var1.format((double)this.angle) + " )";
   }

   public int hashCode() {
      byte var2 = 1;
      float var3 = (float)(((double)this.angle < 0.0D ? 6.283185307179586D + (double)this.angle % 6.283185307179586D : (double)this.angle) % 6.283185307179586D);
      int var4 = 31 * var2 + Float.floatToIntBits(var3);
      var4 = 31 * var4 + Float.floatToIntBits(this.x);
      var4 = 31 * var4 + Float.floatToIntBits(this.y);
      var4 = 31 * var4 + Float.floatToIntBits(this.z);
      return var4;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         AxisAngle4f var2 = (AxisAngle4f)var1;
         float var3 = (float)(((double)this.angle < 0.0D ? 6.283185307179586D + (double)this.angle % 6.283185307179586D : (double)this.angle) % 6.283185307179586D);
         float var4 = (float)(((double)var2.angle < 0.0D ? 6.283185307179586D + (double)var2.angle % 6.283185307179586D : (double)var2.angle) % 6.283185307179586D);
         if (Float.floatToIntBits(var3) != Float.floatToIntBits(var4)) {
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
}
