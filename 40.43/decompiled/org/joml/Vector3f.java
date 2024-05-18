package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Vector3f implements Externalizable, Vector3fc {
   private static final long serialVersionUID = 1L;
   public float x;
   public float y;
   public float z;

   public Vector3f() {
   }

   public Vector3f(float var1) {
      this(var1, var1, var1);
   }

   public Vector3f(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public Vector3f(Vector3fc var1) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
   }

   public Vector3f(Vector2fc var1, float var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
   }

   public Vector3f(ByteBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector3f(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public Vector3f(FloatBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector3f(int var1, FloatBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
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

   public Vector3f set(Vector3fc var1) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      return this;
   }

   public Vector3f set(Vector3dc var1) {
      this.x = (float)var1.x();
      this.y = (float)var1.y();
      this.z = (float)var1.z();
      return this;
   }

   public Vector3f set(Vector2fc var1, float var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
      return this;
   }

   public Vector3f set(float var1) {
      return this.set(var1, var1, var1);
   }

   public Vector3f set(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      return this;
   }

   public Vector3f set(ByteBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector3f set(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector3f set(FloatBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector3f set(int var1, FloatBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector3f setComponent(int var1, float var2) throws IllegalArgumentException {
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
      default:
         throw new IllegalArgumentException();
      }

      return this;
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

   public Vector3f sub(Vector3fc var1) {
      this.x -= var1.x();
      this.y -= var1.y();
      this.z -= var1.z();
      return this;
   }

   public Vector3f sub(Vector3fc var1, Vector3f var2) {
      var2.x = this.x - var1.x();
      var2.y = this.y - var1.y();
      var2.z = this.z - var1.z();
      return var2;
   }

   public Vector3f sub(float var1, float var2, float var3) {
      this.x -= var1;
      this.y -= var2;
      this.z -= var3;
      return this;
   }

   public Vector3f sub(float var1, float var2, float var3, Vector3f var4) {
      var4.x = this.x - var1;
      var4.y = this.y - var2;
      var4.z = this.z - var3;
      return var4;
   }

   public Vector3f add(Vector3fc var1) {
      this.x += var1.x();
      this.y += var1.y();
      this.z += var1.z();
      return this;
   }

   public Vector3f add(Vector3fc var1, Vector3f var2) {
      var2.x = this.x + var1.x();
      var2.y = this.y + var1.y();
      var2.z = this.z + var1.z();
      return var2;
   }

   public Vector3f add(float var1, float var2, float var3) {
      this.x += var1;
      this.y += var2;
      this.z += var3;
      return this;
   }

   public Vector3f add(float var1, float var2, float var3, Vector3f var4) {
      var4.x = this.x + var1;
      var4.y = this.y + var2;
      var4.z = this.z + var3;
      return var4;
   }

   public Vector3f fma(Vector3fc var1, Vector3fc var2) {
      this.x += var1.x() * var2.x();
      this.y += var1.y() * var2.y();
      this.z += var1.z() * var2.z();
      return this;
   }

   public Vector3f fma(float var1, Vector3fc var2) {
      this.x += var1 * var2.x();
      this.y += var1 * var2.y();
      this.z += var1 * var2.z();
      return this;
   }

   public Vector3f fma(Vector3fc var1, Vector3fc var2, Vector3f var3) {
      var3.x = this.x + var1.x() * var2.x();
      var3.y = this.y + var1.y() * var2.y();
      var3.z = this.z + var1.z() * var2.z();
      return var3;
   }

   public Vector3f fma(float var1, Vector3fc var2, Vector3f var3) {
      var3.x = this.x + var1 * var2.x();
      var3.y = this.y + var1 * var2.y();
      var3.z = this.z + var1 * var2.z();
      return var3;
   }

   public Vector3f mul(Vector3fc var1) {
      this.x *= var1.x();
      this.y *= var1.y();
      this.z *= var1.z();
      return this;
   }

   public Vector3f mul(Vector3fc var1, Vector3f var2) {
      var2.x = this.x * var1.x();
      var2.y = this.y * var1.y();
      var2.z = this.z * var1.z();
      return var2;
   }

   public Vector3f div(Vector3fc var1) {
      this.x /= var1.x();
      this.y /= var1.y();
      this.z /= var1.z();
      return this;
   }

   public Vector3f div(Vector3fc var1, Vector3f var2) {
      var2.x = this.x / var1.x();
      var2.y = this.y / var1.y();
      var2.z = this.z / var1.z();
      return var2;
   }

   public Vector3f mulProject(Matrix4fc var1, Vector3f var2) {
      float var3 = 1.0F / (var1.m03() * this.x + var1.m13() * this.y + var1.m23() * this.z + var1.m33());
      var2.set((var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30()) * var3, (var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31()) * var3, (var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32()) * var3);
      return var2;
   }

   public Vector3f mulProject(Matrix4fc var1) {
      return this.mulProject(var1, this);
   }

   public Vector3f mul(Matrix3fc var1) {
      return this.mul(var1, this);
   }

   public Vector3f mul(Matrix3fc var1, Vector3f var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z, var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z, var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z);
      return var2;
   }

   public Vector3f mulTranspose(Matrix3fc var1) {
      return this.mul(var1, this);
   }

   public Vector3f mulTranspose(Matrix3fc var1, Vector3f var2) {
      var2.set(var1.m00() * this.x + var1.m01() * this.y + var1.m02() * this.z, var1.m10() * this.x + var1.m11() * this.y + var1.m12() * this.z, var1.m20() * this.x + var1.m21() * this.y + var1.m22() * this.z);
      return var2;
   }

   public Vector3f mulPosition(Matrix4fc var1) {
      return this.mulPosition(var1, this);
   }

   public Vector3f mulPosition(Matrix4x3fc var1) {
      return this.mulPosition(var1, this);
   }

   public Vector3f mulPosition(Matrix4fc var1, Vector3f var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30(), var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31(), var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32());
      return var2;
   }

   public Vector3f mulPosition(Matrix4x3fc var1, Vector3f var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30(), var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31(), var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32());
      return var2;
   }

   public Vector3f mulTransposePosition(Matrix4fc var1) {
      return this.mulTransposePosition(var1, this);
   }

   public Vector3f mulTransposePosition(Matrix4fc var1, Vector3f var2) {
      var2.set(var1.m00() * this.x + var1.m01() * this.y + var1.m02() * this.z + var1.m03(), var1.m10() * this.x + var1.m11() * this.y + var1.m12() * this.z + var1.m13(), var1.m20() * this.x + var1.m21() * this.y + var1.m22() * this.z + var1.m23());
      return var2;
   }

   public float mulPositionW(Matrix4fc var1) {
      return this.mulPositionW(var1, this);
   }

   public float mulPositionW(Matrix4fc var1, Vector3f var2) {
      float var3 = var1.m03() * this.x + var1.m13() * this.y + var1.m23() * this.z + var1.m33();
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30(), var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31(), var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32());
      return var3;
   }

   public Vector3f mulDirection(Matrix4fc var1) {
      return this.mulDirection(var1, this);
   }

   public Vector3f mulDirection(Matrix4x3fc var1) {
      return this.mulDirection(var1, this);
   }

   public Vector3f mulDirection(Matrix4fc var1, Vector3f var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z, var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z, var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z);
      return var2;
   }

   public Vector3f mulDirection(Matrix4x3fc var1, Vector3f var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z, var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z, var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z);
      return var2;
   }

   public Vector3f mulTransposeDirection(Matrix4fc var1) {
      return this.mulTransposeDirection(var1, this);
   }

   public Vector3f mulTransposeDirection(Matrix4fc var1, Vector3f var2) {
      var2.set(var1.m00() * this.x + var1.m01() * this.y + var1.m02() * this.z, var1.m10() * this.x + var1.m11() * this.y + var1.m12() * this.z, var1.m20() * this.x + var1.m21() * this.y + var1.m22() * this.z);
      return var2;
   }

   public Vector3f mul(float var1) {
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      return this;
   }

   public Vector3f mul(float var1, Vector3f var2) {
      var2.x = this.x * var1;
      var2.y = this.y * var1;
      var2.z = this.z * var1;
      return var2;
   }

   public Vector3f mul(float var1, float var2, float var3) {
      this.x *= var1;
      this.y *= var2;
      this.z *= var3;
      return this;
   }

   public Vector3f mul(float var1, float var2, float var3, Vector3f var4) {
      var4.x = this.x * var1;
      var4.y = this.y * var2;
      var4.z = this.z * var3;
      return var4;
   }

   public Vector3f div(float var1) {
      this.x /= var1;
      this.y /= var1;
      this.z /= var1;
      return this;
   }

   public Vector3f div(float var1, Vector3f var2) {
      var2.x = this.x / var1;
      var2.y = this.y / var1;
      var2.z = this.z / var1;
      return var2;
   }

   public Vector3f div(float var1, float var2, float var3) {
      this.x /= var1;
      this.y /= var2;
      this.z /= var3;
      return this;
   }

   public Vector3f div(float var1, float var2, float var3, Vector3f var4) {
      var4.x = this.x / var1;
      var4.y = this.y / var2;
      var4.z = this.z / var3;
      return var4;
   }

   public Vector3f rotate(Quaternionfc var1) {
      var1.transform((Vector3fc)this, (Vector3f)this);
      return this;
   }

   public Vector3f rotate(Quaternionfc var1, Vector3f var2) {
      var1.transform((Vector3fc)this, (Vector3f)var2);
      return var2;
   }

   public Quaternionf rotationTo(Vector3fc var1, Quaternionf var2) {
      return var2.rotationTo(this, var1);
   }

   public Quaternionf rotationTo(float var1, float var2, float var3, Quaternionf var4) {
      return var4.rotationTo(this.x, this.y, this.z, var1, var2, var3);
   }

   public float lengthSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public float length() {
      return (float)Math.sqrt((double)this.lengthSquared());
   }

   public Vector3f normalize() {
      float var1 = 1.0F / this.length();
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      return this;
   }

   public Vector3f normalize(Vector3f var1) {
      float var2 = 1.0F / this.length();
      var1.x = this.x * var2;
      var1.y = this.y * var2;
      var1.z = this.z * var2;
      return var1;
   }

   public Vector3f cross(Vector3fc var1) {
      return this.set(this.y * var1.z() - this.z * var1.y(), this.z * var1.x() - this.x * var1.z(), this.x * var1.y() - this.y * var1.x());
   }

   public Vector3f cross(float var1, float var2, float var3) {
      return this.set(this.y * var3 - this.z * var2, this.z * var1 - this.x * var3, this.x * var2 - this.y * var1);
   }

   public Vector3f cross(Vector3fc var1, Vector3f var2) {
      return var2.set(this.y * var1.z() - this.z * var1.y(), this.z * var1.x() - this.x * var1.z(), this.x * var1.y() - this.y * var1.x());
   }

   public Vector3f cross(float var1, float var2, float var3, Vector3f var4) {
      return var4.set(this.y * var3 - this.z * var2, this.z * var1 - this.x * var3, this.x * var2 - this.y * var1);
   }

   public float distance(Vector3fc var1) {
      float var2 = var1.x() - this.x;
      float var3 = var1.y() - this.y;
      float var4 = var1.z() - this.z;
      return (float)Math.sqrt((double)(var2 * var2 + var3 * var3 + var4 * var4));
   }

   public float distance(float var1, float var2, float var3) {
      float var4 = this.x - var1;
      float var5 = this.y - var2;
      float var6 = this.z - var3;
      return (float)Math.sqrt((double)(var4 * var4 + var5 * var5 + var6 * var6));
   }

   public float distanceSquared(Vector3fc var1) {
      float var2 = var1.x() - this.x;
      float var3 = var1.y() - this.y;
      float var4 = var1.z() - this.z;
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   public float distanceSquared(float var1, float var2, float var3) {
      float var4 = this.x - var1;
      float var5 = this.y - var2;
      float var6 = this.z - var3;
      return var4 * var4 + var5 * var5 + var6 * var6;
   }

   public float dot(Vector3fc var1) {
      return this.x * var1.x() + this.y * var1.y() + this.z * var1.z();
   }

   public float dot(float var1, float var2, float var3) {
      return this.x * var1 + this.y * var2 + this.z * var3;
   }

   public float angleCos(Vector3fc var1) {
      double var2 = (double)(this.x * this.x + this.y * this.y + this.z * this.z);
      double var4 = (double)(var1.x() * var1.x() + var1.y() * var1.y() + var1.z() * var1.z());
      double var6 = (double)(this.x * var1.x() + this.y * var1.y() + this.z * var1.z());
      return (float)(var6 / Math.sqrt(var2 * var4));
   }

   public float angle(Vector3fc var1) {
      float var2 = this.angleCos(var1);
      var2 = var2 < 1.0F ? var2 : 1.0F;
      var2 = var2 > -1.0F ? var2 : -1.0F;
      return (float)Math.acos((double)var2);
   }

   public Vector3f min(Vector3fc var1) {
      this.x = this.x < var1.x() ? this.x : var1.x();
      this.y = this.y < var1.y() ? this.y : var1.y();
      this.z = this.z < var1.z() ? this.z : var1.z();
      return this;
   }

   public Vector3f max(Vector3fc var1) {
      this.x = this.x > var1.x() ? this.x : var1.x();
      this.y = this.y > var1.y() ? this.y : var1.y();
      this.z = this.z > var1.z() ? this.z : var1.z();
      return this;
   }

   public Vector3f zero() {
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = 0.0F;
      return this;
   }

   public String toString() {
      DecimalFormat var1 = new DecimalFormat(" 0.000E0;-");
      return this.toString(var1).replaceAll("E(\\d+)", "E+$1");
   }

   public String toString(NumberFormat var1) {
      return "(" + var1.format((double)this.x) + " " + var1.format((double)this.y) + " " + var1.format((double)this.z) + ")";
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeFloat(this.x);
      var1.writeFloat(this.y);
      var1.writeFloat(this.z);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.x = var1.readFloat();
      this.y = var1.readFloat();
      this.z = var1.readFloat();
   }

   public Vector3f negate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
      return this;
   }

   public Vector3f negate(Vector3f var1) {
      var1.x = -this.x;
      var1.y = -this.y;
      var1.z = -this.z;
      return var1;
   }

   public int hashCode() {
      byte var2 = 1;
      int var3 = 31 * var2 + Float.floatToIntBits(this.x);
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
         Vector3f var2 = (Vector3f)var1;
         if (Float.floatToIntBits(this.x) != Float.floatToIntBits(var2.x)) {
            return false;
         } else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(var2.y)) {
            return false;
         } else {
            return Float.floatToIntBits(this.z) == Float.floatToIntBits(var2.z);
         }
      }
   }

   public Vector3f reflect(Vector3fc var1) {
      float var2 = this.dot(var1);
      this.x -= (var2 + var2) * var1.x();
      this.y -= (var2 + var2) * var1.y();
      this.z -= (var2 + var2) * var1.z();
      return this;
   }

   public Vector3f reflect(float var1, float var2, float var3) {
      float var4 = this.dot(var1, var2, var3);
      this.x -= (var4 + var4) * var1;
      this.y -= (var4 + var4) * var2;
      this.z -= (var4 + var4) * var3;
      return this;
   }

   public Vector3f reflect(Vector3fc var1, Vector3f var2) {
      float var3 = this.dot(var1);
      var2.x = this.x - (var3 + var3) * var1.x();
      var2.y = this.y - (var3 + var3) * var1.y();
      var2.z = this.z - (var3 + var3) * var1.z();
      return var2;
   }

   public Vector3f reflect(float var1, float var2, float var3, Vector3f var4) {
      float var5 = this.dot(var1, var2, var3);
      var4.x = this.x - (var5 + var5) * var1;
      var4.y = this.y - (var5 + var5) * var2;
      var4.z = this.z - (var5 + var5) * var3;
      return var4;
   }

   public Vector3f half(Vector3fc var1) {
      return this.add(var1).normalize();
   }

   public Vector3f half(float var1, float var2, float var3) {
      return this.add(var1, var2, var3).normalize();
   }

   public Vector3f half(Vector3fc var1, Vector3f var2) {
      return var2.set((Vector3fc)this).add(var1).normalize();
   }

   public Vector3f half(float var1, float var2, float var3, Vector3f var4) {
      return var4.set((Vector3fc)this).add(var1, var2, var3).normalize();
   }

   public Vector3f smoothStep(Vector3fc var1, float var2, Vector3f var3) {
      float var4 = var2 * var2;
      float var5 = var4 * var2;
      var3.x = (this.x + this.x - var1.x() - var1.x()) * var5 + (3.0F * var1.x() - 3.0F * this.x) * var4 + this.x * var2 + this.x;
      var3.y = (this.y + this.y - var1.y() - var1.y()) * var5 + (3.0F * var1.y() - 3.0F * this.y) * var4 + this.y * var2 + this.y;
      var3.z = (this.z + this.z - var1.z() - var1.z()) * var5 + (3.0F * var1.z() - 3.0F * this.z) * var4 + this.z * var2 + this.z;
      return var3;
   }

   public Vector3f hermite(Vector3fc var1, Vector3fc var2, Vector3fc var3, float var4, Vector3f var5) {
      float var6 = var4 * var4;
      float var7 = var6 * var4;
      var5.x = (this.x + this.x - var2.x() - var2.x() + var3.x() + var1.x()) * var7 + (3.0F * var2.x() - 3.0F * this.x - var1.x() - var1.x() - var3.x()) * var6 + this.x * var4 + this.x;
      var5.y = (this.y + this.y - var2.y() - var2.y() + var3.y() + var1.y()) * var7 + (3.0F * var2.y() - 3.0F * this.y - var1.y() - var1.y() - var3.y()) * var6 + this.y * var4 + this.y;
      var5.z = (this.z + this.z - var2.z() - var2.z() + var3.z() + var1.z()) * var7 + (3.0F * var2.z() - 3.0F * this.z - var1.z() - var1.z() - var3.z()) * var6 + this.z * var4 + this.z;
      return var5;
   }

   public Vector3f lerp(Vector3fc var1, float var2) {
      return this.lerp(var1, var2, this);
   }

   public Vector3f lerp(Vector3fc var1, float var2, Vector3f var3) {
      var3.x = this.x + (var1.x() - this.x) * var2;
      var3.y = this.y + (var1.y() - this.y) * var2;
      var3.z = this.z + (var1.z() - this.z) * var2;
      return var3;
   }

   public float get(int var1) throws IllegalArgumentException {
      switch(var1) {
      case 0:
         return this.x;
      case 1:
         return this.y;
      case 2:
         return this.z;
      default:
         throw new IllegalArgumentException();
      }
   }

   public int maxComponent() {
      float var1 = Math.abs(this.x);
      float var2 = Math.abs(this.y);
      float var3 = Math.abs(this.z);
      if (var1 >= var2 && var1 >= var3) {
         return 0;
      } else {
         return var2 >= var3 ? 1 : 2;
      }
   }

   public int minComponent() {
      float var1 = Math.abs(this.x);
      float var2 = Math.abs(this.y);
      float var3 = Math.abs(this.z);
      if (var1 < var2 && var1 < var3) {
         return 0;
      } else {
         return var2 < var3 ? 1 : 2;
      }
   }

   public Vector3f orthogonalize(Vector3fc var1, Vector3f var2) {
      float var3 = 1.0F / (float)Math.sqrt((double)(var1.x() * var1.x() + var1.y() * var1.y() + var1.z() * var1.z()));
      float var4 = var1.x() * var3;
      float var5 = var1.y() * var3;
      float var6 = var1.z() * var3;
      float var7 = var4 * this.x + var5 * this.y + var6 * this.z;
      float var8 = this.x - var7 * var4;
      float var9 = this.y - var7 * var5;
      float var10 = this.z - var7 * var6;
      float var11 = 1.0F / (float)Math.sqrt((double)(var8 * var8 + var9 * var9 + var10 * var10));
      var2.x = var8 * var11;
      var2.y = var9 * var11;
      var2.z = var10 * var11;
      return var2;
   }

   public Vector3f orthogonalize(Vector3fc var1) {
      return this.orthogonalize(var1, this);
   }

   public Vector3f orthogonalizeUnit(Vector3fc var1, Vector3f var2) {
      float var3 = var1.x();
      float var4 = var1.y();
      float var5 = var1.z();
      float var6 = var3 * this.x + var4 * this.y + var5 * this.z;
      float var7 = this.x - var6 * var3;
      float var8 = this.y - var6 * var4;
      float var9 = this.z - var6 * var5;
      float var10 = 1.0F / (float)Math.sqrt((double)(var7 * var7 + var8 * var8 + var9 * var9));
      var2.x = var7 * var10;
      var2.y = var8 * var10;
      var2.z = var9 * var10;
      return var2;
   }

   public Vector3f orthogonalizeUnit(Vector3fc var1) {
      return this.orthogonalizeUnit(var1, this);
   }

   public Vector3fc toImmutable() {
      return (Vector3fc)(!Options.DEBUG ? this : new Vector3f.Proxy(this));
   }

   private final class Proxy implements Vector3fc {
      private final Vector3fc delegate;

      Proxy(Vector3fc var2) {
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

      public Vector3f sub(Vector3fc var1, Vector3f var2) {
         return this.delegate.sub(var1, var2);
      }

      public Vector3f sub(float var1, float var2, float var3, Vector3f var4) {
         return this.delegate.sub(var1, var2, var3, var4);
      }

      public Vector3f add(Vector3fc var1, Vector3f var2) {
         return this.delegate.add(var1, var2);
      }

      public Vector3f add(float var1, float var2, float var3, Vector3f var4) {
         return this.delegate.add(var1, var2, var3, var4);
      }

      public Vector3f fma(Vector3fc var1, Vector3fc var2, Vector3f var3) {
         return this.delegate.fma(var1, var2, var3);
      }

      public Vector3f fma(float var1, Vector3fc var2, Vector3f var3) {
         return this.delegate.fma(var1, var2, var3);
      }

      public Vector3f mul(Vector3fc var1, Vector3f var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector3f div(Vector3fc var1, Vector3f var2) {
         return this.delegate.div(var1, var2);
      }

      public Vector3f mulProject(Matrix4fc var1, Vector3f var2) {
         return this.delegate.mulProject(var1, var2);
      }

      public Vector3f mul(Matrix3fc var1, Vector3f var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector3f mulTranspose(Matrix3fc var1, Vector3f var2) {
         return this.delegate.mulTranspose(var1, var2);
      }

      public Vector3f mulPosition(Matrix4fc var1, Vector3f var2) {
         return this.delegate.mulPosition(var1, var2);
      }

      public Vector3f mulPosition(Matrix4x3fc var1, Vector3f var2) {
         return this.delegate.mulPosition(var1, var2);
      }

      public Vector3f mulTransposePosition(Matrix4fc var1, Vector3f var2) {
         return this.delegate.mulTransposePosition(var1, var2);
      }

      public float mulPositionW(Matrix4fc var1, Vector3f var2) {
         return this.delegate.mulPositionW(var1, var2);
      }

      public Vector3f mulDirection(Matrix4fc var1, Vector3f var2) {
         return this.delegate.mulDirection(var1, var2);
      }

      public Vector3f mulDirection(Matrix4x3fc var1, Vector3f var2) {
         return this.delegate.mulDirection(var1, var2);
      }

      public Vector3f mulTransposeDirection(Matrix4fc var1, Vector3f var2) {
         return this.delegate.mulTransposeDirection(var1, var2);
      }

      public Vector3f mul(float var1, Vector3f var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector3f mul(float var1, float var2, float var3, Vector3f var4) {
         return this.delegate.mul(var1, var2, var3, var4);
      }

      public Vector3f div(float var1, Vector3f var2) {
         return this.delegate.div(var1, var2);
      }

      public Vector3f div(float var1, float var2, float var3, Vector3f var4) {
         return this.delegate.div(var1, var2, var3, var4);
      }

      public Vector3f rotate(Quaternionfc var1, Vector3f var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Quaternionf rotationTo(Vector3fc var1, Quaternionf var2) {
         return this.delegate.rotationTo(var1, var2);
      }

      public Quaternionf rotationTo(float var1, float var2, float var3, Quaternionf var4) {
         return this.delegate.rotationTo(var1, var2, var3, var4);
      }

      public float lengthSquared() {
         return this.delegate.lengthSquared();
      }

      public float length() {
         return this.delegate.length();
      }

      public Vector3f normalize(Vector3f var1) {
         return this.delegate.normalize(var1);
      }

      public Vector3f cross(Vector3fc var1, Vector3f var2) {
         return this.delegate.cross(var1, var2);
      }

      public Vector3f cross(float var1, float var2, float var3, Vector3f var4) {
         return this.delegate.cross(var1, var2, var3, var4);
      }

      public float distance(Vector3fc var1) {
         return this.delegate.distance(var1);
      }

      public float distance(float var1, float var2, float var3) {
         return this.delegate.distance(var1, var2, var3);
      }

      public float distanceSquared(Vector3fc var1) {
         return this.delegate.distanceSquared(var1);
      }

      public float distanceSquared(float var1, float var2, float var3) {
         return this.delegate.distanceSquared(var1, var2, var3);
      }

      public float dot(Vector3fc var1) {
         return this.delegate.dot(var1);
      }

      public float dot(float var1, float var2, float var3) {
         return this.delegate.dot(var1, var2, var3);
      }

      public float angleCos(Vector3fc var1) {
         return this.delegate.angleCos(var1);
      }

      public float angle(Vector3fc var1) {
         return this.delegate.angle(var1);
      }

      public Vector3f negate(Vector3f var1) {
         return this.delegate.negate(var1);
      }

      public Vector3f reflect(Vector3fc var1, Vector3f var2) {
         return this.delegate.reflect(var1, var2);
      }

      public Vector3f reflect(float var1, float var2, float var3, Vector3f var4) {
         return this.delegate.reflect(var1, var2, var3, var4);
      }

      public Vector3f half(Vector3fc var1, Vector3f var2) {
         return this.delegate.half(var1, var2);
      }

      public Vector3f half(float var1, float var2, float var3, Vector3f var4) {
         return this.delegate.half(var1, var2, var3, var4);
      }

      public Vector3f smoothStep(Vector3fc var1, float var2, Vector3f var3) {
         return this.delegate.smoothStep(var1, var2, var3);
      }

      public Vector3f hermite(Vector3fc var1, Vector3fc var2, Vector3fc var3, float var4, Vector3f var5) {
         return this.delegate.hermite(var1, var2, var3, var4, var5);
      }

      public Vector3f lerp(Vector3fc var1, float var2, Vector3f var3) {
         return this.delegate.lerp(var1, var2, var3);
      }

      public float get(int var1) throws IllegalArgumentException {
         return this.delegate.get(var1);
      }

      public int maxComponent() {
         return this.delegate.maxComponent();
      }

      public int minComponent() {
         return this.delegate.minComponent();
      }

      public Vector3f orthogonalize(Vector3fc var1, Vector3f var2) {
         return this.delegate.orthogonalize(var1, var2);
      }

      public Vector3f orthogonalizeUnit(Vector3fc var1, Vector3f var2) {
         return this.delegate.orthogonalizeUnit(var1, var2);
      }
   }
}
