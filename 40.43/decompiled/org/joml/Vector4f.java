package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Vector4f implements Externalizable, Vector4fc {
   private static final long serialVersionUID = 1L;
   public float x;
   public float y;
   public float z;
   public float w;

   public Vector4f() {
      this.w = 1.0F;
   }

   public Vector4f(Vector4fc var1) {
      if (var1 instanceof Vector4f) {
         MemUtil.INSTANCE.copy((Vector4f)var1, this);
      } else {
         this.x = var1.x();
         this.y = var1.y();
         this.z = var1.z();
         this.w = var1.w();
      }

   }

   public Vector4f(Vector3fc var1, float var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      this.w = var2;
   }

   public Vector4f(Vector2fc var1, float var2, float var3) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
      this.w = var3;
   }

   public Vector4f(float var1) {
      MemUtil.INSTANCE.broadcast(var1, this);
   }

   public Vector4f(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.w = var4;
   }

   public Vector4f(ByteBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector4f(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public Vector4f(FloatBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector4f(int var1, FloatBuffer var2) {
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

   public float w() {
      return this.w;
   }

   public Vector4f set(Vector4fc var1) {
      if (var1 instanceof Vector4f) {
         MemUtil.INSTANCE.copy((Vector4f)var1, this);
      } else {
         this.x = var1.x();
         this.y = var1.y();
         this.z = var1.z();
         this.w = var1.w();
      }

      return this;
   }

   public Vector4f set(Vector4dc var1) {
      this.x = (float)var1.x();
      this.y = (float)var1.y();
      this.z = (float)var1.z();
      this.w = (float)var1.w();
      return this;
   }

   public Vector4f set(Vector3fc var1, float var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      this.w = var2;
      return this;
   }

   public Vector4f set(Vector2fc var1, float var2, float var3) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
      this.w = var3;
      return this;
   }

   public Vector4f set(float var1) {
      MemUtil.INSTANCE.broadcast(var1, this);
      return this;
   }

   public Vector4f set(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.w = var4;
      return this;
   }

   public Vector4f set(ByteBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector4f set(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector4f set(FloatBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector4f set(int var1, FloatBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector4f setComponent(int var1, float var2) throws IllegalArgumentException {
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

   public Vector4f sub(Vector4fc var1) {
      this.x -= var1.x();
      this.y -= var1.y();
      this.z -= var1.z();
      this.w -= var1.w();
      return this;
   }

   public Vector4f sub(float var1, float var2, float var3, float var4) {
      this.x -= var1;
      this.y -= var2;
      this.z -= var3;
      this.w -= var4;
      return this;
   }

   public Vector4f sub(Vector4fc var1, Vector4f var2) {
      var2.x = this.x - var1.x();
      var2.y = this.y - var1.y();
      var2.z = this.z - var1.z();
      var2.w = this.w - var1.w();
      return var2;
   }

   public Vector4f sub(float var1, float var2, float var3, float var4, Vector4f var5) {
      var5.x = this.x - var1;
      var5.y = this.y - var2;
      var5.z = this.z - var3;
      var5.w = this.w - var4;
      return var5;
   }

   public Vector4f add(Vector4fc var1) {
      this.x += var1.x();
      this.y += var1.y();
      this.z += var1.z();
      this.w += var1.w();
      return this;
   }

   public Vector4f add(Vector4fc var1, Vector4f var2) {
      var2.x = this.x + var1.x();
      var2.y = this.y + var1.y();
      var2.z = this.z + var1.z();
      var2.w = this.w + var1.w();
      return var2;
   }

   public Vector4f add(float var1, float var2, float var3, float var4) {
      this.x += var1;
      this.y += var2;
      this.z += var3;
      this.w += var4;
      return this;
   }

   public Vector4f add(float var1, float var2, float var3, float var4, Vector4f var5) {
      var5.x = this.x + var1;
      var5.y = this.y + var2;
      var5.z = this.z + var3;
      var5.w = this.w + var4;
      return var5;
   }

   public Vector4f fma(Vector4fc var1, Vector4fc var2) {
      this.x += var1.x() * var2.x();
      this.y += var1.y() * var2.y();
      this.z += var1.z() * var2.z();
      this.w += var1.w() * var2.w();
      return this;
   }

   public Vector4f fma(float var1, Vector4fc var2) {
      this.x += var1 * var2.x();
      this.y += var1 * var2.y();
      this.z += var1 * var2.z();
      this.w += var1 * var2.w();
      return this;
   }

   public Vector4f fma(Vector4fc var1, Vector4fc var2, Vector4f var3) {
      var3.x = this.x + var1.x() * var2.x();
      var3.y = this.y + var1.y() * var2.y();
      var3.z = this.z + var1.z() * var2.z();
      var3.w = this.w + var1.w() * var2.w();
      return var3;
   }

   public Vector4f fma(float var1, Vector4fc var2, Vector4f var3) {
      var3.x = this.x + var1 * var2.x();
      var3.y = this.y + var1 * var2.y();
      var3.z = this.z + var1 * var2.z();
      var3.w = this.w + var1 * var2.w();
      return var3;
   }

   public Vector4f mul(Vector4fc var1) {
      this.x *= var1.x();
      this.y *= var1.y();
      this.z *= var1.z();
      this.w *= var1.w();
      return this;
   }

   public Vector4f mul(Vector4fc var1, Vector4f var2) {
      var2.x = this.x * var1.x();
      var2.y = this.y * var1.y();
      var2.z = this.z * var1.z();
      var2.w = this.w * var1.w();
      return var2;
   }

   public Vector4f div(Vector4fc var1) {
      this.x /= var1.x();
      this.y /= var1.y();
      this.z /= var1.z();
      this.w /= var1.w();
      return this;
   }

   public Vector4f div(Vector4fc var1, Vector4f var2) {
      var2.x = this.x / var1.x();
      var2.y = this.y / var1.y();
      var2.z = this.z / var1.z();
      var2.w = this.w / var1.w();
      return var2;
   }

   public Vector4f mul(Matrix4fc var1) {
      return this.mul(var1, this);
   }

   public Vector4f mul(Matrix4fc var1, Vector4f var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30() * this.w, var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31() * this.w, var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32() * this.w, var1.m03() * this.x + var1.m13() * this.y + var1.m23() * this.z + var1.m33() * this.w);
      return var2;
   }

   public Vector4f mul(Matrix4x3fc var1) {
      return this.mul(var1, this);
   }

   public Vector4f mul(Matrix4x3fc var1, Vector4f var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30() * this.w, var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31() * this.w, var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32() * this.w, this.w);
      return var2;
   }

   public Vector4f mulProject(Matrix4fc var1, Vector4f var2) {
      float var3 = 1.0F / (var1.m03() * this.x + var1.m13() * this.y + var1.m23() * this.z + var1.m33() * this.w);
      var2.set((var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30() * this.w) * var3, (var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31() * this.w) * var3, (var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32() * this.w) * var3, 1.0F);
      return var2;
   }

   public Vector4f mulProject(Matrix4fc var1) {
      return this.mulProject(var1, this);
   }

   public Vector4f mul(float var1) {
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      this.w *= var1;
      return this;
   }

   public Vector4f mul(float var1, Vector4f var2) {
      var2.x = this.x * var1;
      var2.y = this.y * var1;
      var2.z = this.z * var1;
      var2.w = this.w * var1;
      return var2;
   }

   public Vector4f mul(float var1, float var2, float var3, float var4) {
      this.x *= var1;
      this.y *= var2;
      this.z *= var3;
      this.w *= var4;
      return this;
   }

   public Vector4f mul(float var1, float var2, float var3, float var4, Vector4f var5) {
      var5.x = this.x * var1;
      var5.y = this.y * var2;
      var5.z = this.z * var3;
      var5.w = this.w * var4;
      return var5;
   }

   public Vector4f div(float var1) {
      this.x /= var1;
      this.y /= var1;
      this.z /= var1;
      this.w /= var1;
      return this;
   }

   public Vector4f div(float var1, Vector4f var2) {
      var2.x = this.x / var1;
      var2.y = this.y / var1;
      var2.z = this.z / var1;
      var2.w = this.w / var1;
      return var2;
   }

   public Vector4f div(float var1, float var2, float var3, float var4) {
      this.x /= var1;
      this.y /= var2;
      this.z /= var3;
      this.w /= var4;
      return this;
   }

   public Vector4f div(float var1, float var2, float var3, float var4, Vector4f var5) {
      var5.x = this.x / var1;
      var5.y = this.y / var2;
      var5.z = this.z / var3;
      var5.w = this.w / var4;
      return var5;
   }

   public Vector4f rotate(Quaternionfc var1) {
      return this.rotate(var1, this);
   }

   public Vector4f rotate(Quaternionfc var1, Vector4f var2) {
      return var1.transform((Vector4fc)this, (Vector4f)var2);
   }

   public float lengthSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
   }

   public float length() {
      return (float)Math.sqrt((double)this.lengthSquared());
   }

   public Vector4f normalize() {
      float var1 = 1.0F / this.length();
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      this.w *= var1;
      return this;
   }

   public Vector4f normalize(Vector4f var1) {
      float var2 = 1.0F / this.length();
      var1.x = this.x * var2;
      var1.y = this.y * var2;
      var1.z = this.z * var2;
      var1.w = this.w * var2;
      return var1;
   }

   public Vector4f normalize3() {
      float var1 = (float)(1.0D / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z)));
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      this.w *= var1;
      return this;
   }

   public float distance(Vector4fc var1) {
      float var2 = var1.x() - this.x;
      float var3 = var1.y() - this.y;
      float var4 = var1.z() - this.z;
      float var5 = var1.w() - this.w;
      return (float)Math.sqrt((double)(var2 * var2 + var3 * var3 + var4 * var4 + var5 * var5));
   }

   public float distance(float var1, float var2, float var3, float var4) {
      float var5 = this.x - var1;
      float var6 = this.y - var2;
      float var7 = this.z - var3;
      float var8 = this.w - var4;
      return (float)Math.sqrt((double)(var5 * var5 + var6 * var6 + var7 * var7 + var8 * var8));
   }

   public float dot(Vector4fc var1) {
      return this.x * var1.x() + this.y * var1.y() + this.z * var1.z() + this.w * var1.w();
   }

   public float dot(float var1, float var2, float var3, float var4) {
      return this.x * var1 + this.y * var2 + this.z * var3 + this.w * var4;
   }

   public float angleCos(Vector4fc var1) {
      double var2 = (double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
      double var4 = (double)(var1.x() * var1.x() + var1.y() * var1.y() + var1.z() * var1.z() + var1.w() * var1.w());
      double var6 = (double)(this.x * var1.x() + this.y * var1.y() + this.z * var1.z() + this.w * var1.w());
      return (float)(var6 / Math.sqrt(var2 * var4));
   }

   public float angle(Vector4fc var1) {
      float var2 = this.angleCos(var1);
      var2 = var2 < 1.0F ? var2 : 1.0F;
      var2 = var2 > -1.0F ? var2 : -1.0F;
      return (float)Math.acos((double)var2);
   }

   public Vector4f zero() {
      MemUtil.INSTANCE.zero(this);
      return this;
   }

   public Vector4f negate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
      this.w = -this.w;
      return this;
   }

   public Vector4f negate(Vector4f var1) {
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

   public Vector4f min(Vector4fc var1) {
      this.x = this.x < var1.x() ? this.x : var1.x();
      this.y = this.y < var1.y() ? this.y : var1.y();
      this.z = this.z < var1.z() ? this.z : var1.z();
      this.w = this.w < var1.w() ? this.w : var1.w();
      return this;
   }

   public Vector4f max(Vector4fc var1) {
      this.x = this.x > var1.x() ? this.x : var1.x();
      this.y = this.y > var1.y() ? this.y : var1.y();
      this.z = this.z > var1.z() ? this.z : var1.z();
      this.w = this.w > var1.w() ? this.w : var1.w();
      return this;
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
         Vector4f var2 = (Vector4f)var1;
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

   public Vector4f smoothStep(Vector4fc var1, float var2, Vector4f var3) {
      float var4 = var2 * var2;
      float var5 = var4 * var2;
      var3.x = (this.x + this.x - var1.x() - var1.x()) * var5 + (3.0F * var1.x() - 3.0F * this.x) * var4 + this.x * var2 + this.x;
      var3.y = (this.y + this.y - var1.y() - var1.y()) * var5 + (3.0F * var1.y() - 3.0F * this.y) * var4 + this.y * var2 + this.y;
      var3.z = (this.z + this.z - var1.z() - var1.z()) * var5 + (3.0F * var1.z() - 3.0F * this.z) * var4 + this.z * var2 + this.z;
      var3.w = (this.w + this.w - var1.w() - var1.w()) * var5 + (3.0F * var1.w() - 3.0F * this.w) * var4 + this.w * var2 + this.w;
      return var3;
   }

   public Vector4f hermite(Vector4fc var1, Vector4fc var2, Vector4fc var3, float var4, Vector4f var5) {
      float var6 = var4 * var4;
      float var7 = var6 * var4;
      var5.x = (this.x + this.x - var2.x() - var2.x() + var3.x() + var1.x()) * var7 + (3.0F * var2.x() - 3.0F * this.x - var1.x() - var1.x() - var3.x()) * var6 + this.x * var4 + this.x;
      var5.y = (this.y + this.y - var2.y() - var2.y() + var3.y() + var1.y()) * var7 + (3.0F * var2.y() - 3.0F * this.y - var1.y() - var1.y() - var3.y()) * var6 + this.y * var4 + this.y;
      var5.z = (this.z + this.z - var2.z() - var2.z() + var3.z() + var1.z()) * var7 + (3.0F * var2.z() - 3.0F * this.z - var1.z() - var1.z() - var3.z()) * var6 + this.z * var4 + this.z;
      var5.w = (this.w + this.w - var2.w() - var2.w() + var3.w() + var1.w()) * var7 + (3.0F * var2.w() - 3.0F * this.w - var1.w() - var1.w() - var3.w()) * var6 + this.w * var4 + this.w;
      return var5;
   }

   public Vector4f lerp(Vector4fc var1, float var2) {
      return this.lerp(var1, var2, this);
   }

   public Vector4f lerp(Vector4fc var1, float var2, Vector4f var3) {
      var3.x = this.x + (var1.x() - this.x) * var2;
      var3.y = this.y + (var1.y() - this.y) * var2;
      var3.z = this.z + (var1.z() - this.z) * var2;
      var3.w = this.w + (var1.w() - this.w) * var2;
      return var3;
   }

   public Vector4fc toImmutable() {
      return (Vector4fc)(!Options.DEBUG ? this : new Vector4f.Proxy(this));
   }

   private final class Proxy implements Vector4fc {
      private final Vector4fc delegate;

      Proxy(Vector4fc var2) {
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

      public Vector4f sub(Vector4fc var1, Vector4f var2) {
         return this.delegate.sub(var1, var2);
      }

      public Vector4f sub(float var1, float var2, float var3, float var4, Vector4f var5) {
         return this.delegate.sub(var1, var2, var3, var4, var5);
      }

      public Vector4f add(Vector4fc var1, Vector4f var2) {
         return this.delegate.add(var1, var2);
      }

      public Vector4f add(float var1, float var2, float var3, float var4, Vector4f var5) {
         return this.delegate.add(var1, var2, var3, var4, var5);
      }

      public Vector4f fma(Vector4fc var1, Vector4fc var2, Vector4f var3) {
         return this.delegate.fma(var1, var2, var3);
      }

      public Vector4f fma(float var1, Vector4fc var2, Vector4f var3) {
         return this.delegate.fma(var1, var2, var3);
      }

      public Vector4f mul(Vector4fc var1, Vector4f var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector4f div(Vector4fc var1, Vector4f var2) {
         return this.delegate.div(var1, var2);
      }

      public Vector4f mul(Matrix4fc var1, Vector4f var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector4f mul(Matrix4x3fc var1, Vector4f var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector4f mulProject(Matrix4fc var1, Vector4f var2) {
         return this.delegate.mulProject(var1, var2);
      }

      public Vector4f mul(float var1, Vector4f var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector4f mul(float var1, float var2, float var3, float var4, Vector4f var5) {
         return this.delegate.mul(var1, var2, var3, var4, var5);
      }

      public Vector4f div(float var1, Vector4f var2) {
         return this.delegate.div(var1, var2);
      }

      public Vector4f div(float var1, float var2, float var3, float var4, Vector4f var5) {
         return this.delegate.div(var1, var2, var3, var4, var5);
      }

      public Vector4f rotate(Quaternionfc var1, Vector4f var2) {
         return this.delegate.rotate(var1, var2);
      }

      public float lengthSquared() {
         return this.delegate.lengthSquared();
      }

      public float length() {
         return this.delegate.length();
      }

      public Vector4f normalize(Vector4f var1) {
         return this.delegate.normalize(var1);
      }

      public float distance(Vector4fc var1) {
         return this.delegate.distance(var1);
      }

      public float distance(float var1, float var2, float var3, float var4) {
         return this.delegate.distance(var1, var2, var3, var4);
      }

      public float dot(Vector4fc var1) {
         return this.delegate.dot(var1);
      }

      public float dot(float var1, float var2, float var3, float var4) {
         return this.delegate.dot(var1, var2, var3, var4);
      }

      public float angleCos(Vector4fc var1) {
         return this.delegate.angleCos(var1);
      }

      public float angle(Vector4fc var1) {
         return this.delegate.angle(var1);
      }

      public Vector4f negate(Vector4f var1) {
         return this.delegate.negate(var1);
      }

      public Vector4f lerp(Vector4fc var1, float var2, Vector4f var3) {
         return this.delegate.lerp(var1, var2, var3);
      }

      public Vector4f smoothStep(Vector4fc var1, float var2, Vector4f var3) {
         return this.delegate.smoothStep(var1, var2, var3);
      }

      public Vector4f hermite(Vector4fc var1, Vector4fc var2, Vector4fc var3, float var4, Vector4f var5) {
         return this.delegate.hermite(var1, var2, var3, var4, var5);
      }
   }
}
