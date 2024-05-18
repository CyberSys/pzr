package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Vector2f implements Externalizable, Vector2fc {
   private static final long serialVersionUID = 1L;
   public float x;
   public float y;

   public Vector2f() {
   }

   public Vector2f(float var1) {
      this(var1, var1);
   }

   public Vector2f(float var1, float var2) {
      this.x = var1;
      this.y = var2;
   }

   public Vector2f(Vector2fc var1) {
      this.x = var1.x();
      this.y = var1.y();
   }

   public Vector2f(ByteBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector2f(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public Vector2f(FloatBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector2f(int var1, FloatBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public Vector2f set(float var1) {
      return this.set(var1, var1);
   }

   public Vector2f set(float var1, float var2) {
      this.x = var1;
      this.y = var2;
      return this;
   }

   public Vector2f set(Vector2fc var1) {
      this.x = var1.x();
      this.y = var1.y();
      return this;
   }

   public Vector2f set(Vector2dc var1) {
      this.x = (float)var1.x();
      this.y = (float)var1.y();
      return this;
   }

   public Vector2f set(ByteBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector2f set(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector2f set(FloatBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector2f set(int var1, FloatBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector2f setComponent(int var1, float var2) throws IllegalArgumentException {
      switch(var1) {
      case 0:
         this.x = var2;
         break;
      case 1:
         this.y = var2;
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

   public FloatBuffer get(FloatBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public FloatBuffer get(int var1, FloatBuffer var2) {
      MemUtil.INSTANCE.put(this, var1, var2);
      return var2;
   }

   public Vector2f perpendicular() {
      return this.set(this.y, this.x * -1.0F);
   }

   public Vector2f sub(Vector2fc var1) {
      this.x -= var1.x();
      this.y -= var1.y();
      return this;
   }

   public Vector2f sub(Vector2fc var1, Vector2f var2) {
      var2.x = this.x - var1.x();
      var2.y = this.y - var1.y();
      return var2;
   }

   public Vector2f sub(float var1, float var2) {
      this.x -= var1;
      this.y -= var2;
      return this;
   }

   public Vector2f sub(float var1, float var2, Vector2f var3) {
      var3.x = this.x - var1;
      var3.y = this.y - var2;
      return var3;
   }

   public float dot(Vector2fc var1) {
      return this.x * var1.x() + this.y * var1.y();
   }

   public float angle(Vector2fc var1) {
      float var2 = this.x * var1.x() + this.y * var1.y();
      float var3 = this.x * var1.y() - this.y * var1.x();
      return (float)Math.atan2((double)var3, (double)var2);
   }

   public float length() {
      return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y));
   }

   public float lengthSquared() {
      return this.x * this.x + this.y * this.y;
   }

   public float distance(Vector2fc var1) {
      return this.distance(var1.x(), var1.y());
   }

   public float distanceSquared(Vector2fc var1) {
      return this.distanceSquared(var1.x(), var1.y());
   }

   public float distance(float var1, float var2) {
      float var3 = this.x - var1;
      float var4 = this.y - var2;
      return (float)Math.sqrt((double)(var3 * var3 + var4 * var4));
   }

   public float distanceSquared(float var1, float var2) {
      float var3 = this.x - var1;
      float var4 = this.y - var2;
      return var3 * var3 + var4 * var4;
   }

   public Vector2f normalize() {
      float var1 = (float)(1.0D / Math.sqrt((double)(this.x * this.x + this.y * this.y)));
      this.x *= var1;
      this.y *= var1;
      return this;
   }

   public Vector2f normalize(Vector2f var1) {
      float var2 = (float)(1.0D / Math.sqrt((double)(this.x * this.x + this.y * this.y)));
      var1.x = this.x * var2;
      var1.y = this.y * var2;
      return var1;
   }

   public Vector2f add(Vector2fc var1) {
      this.x += var1.x();
      this.y += var1.y();
      return this;
   }

   public Vector2f add(Vector2fc var1, Vector2f var2) {
      var2.x = this.x + var1.x();
      var2.y = this.y + var1.y();
      return var2;
   }

   public Vector2f add(float var1, float var2) {
      this.x += var1;
      this.y += var2;
      return this;
   }

   public Vector2f add(float var1, float var2, Vector2f var3) {
      var3.x = this.x + var1;
      var3.y = this.y + var2;
      return var3;
   }

   public Vector2f zero() {
      this.x = 0.0F;
      this.y = 0.0F;
      return this;
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeFloat(this.x);
      var1.writeFloat(this.y);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.x = var1.readFloat();
      this.y = var1.readFloat();
   }

   public Vector2f negate() {
      this.x = -this.x;
      this.y = -this.y;
      return this;
   }

   public Vector2f negate(Vector2f var1) {
      var1.x = -this.x;
      var1.y = -this.y;
      return var1;
   }

   public Vector2f mul(float var1) {
      this.x *= var1;
      this.y *= var1;
      return this;
   }

   public Vector2f mul(float var1, Vector2f var2) {
      var2.x = this.x * var1;
      var2.y = this.y * var1;
      return var2;
   }

   public Vector2f mul(float var1, float var2) {
      this.x *= var1;
      this.y *= var2;
      return this;
   }

   public Vector2f mul(float var1, float var2, Vector2f var3) {
      var3.x = this.x * var1;
      var3.y = this.y * var2;
      return var3;
   }

   public Vector2f mul(Vector2fc var1) {
      this.x *= var1.x();
      this.y *= var1.y();
      return this;
   }

   public Vector2f mul(Vector2fc var1, Vector2f var2) {
      var2.x = this.x * var1.x();
      var2.y = this.y * var1.y();
      return var2;
   }

   public Vector2f lerp(Vector2fc var1, float var2) {
      return this.lerp(var1, var2, this);
   }

   public Vector2f lerp(Vector2fc var1, float var2, Vector2f var3) {
      var3.x = this.x + (var1.x() - this.x) * var2;
      var3.y = this.y + (var1.y() - this.y) * var2;
      return var3;
   }

   public int hashCode() {
      byte var2 = 1;
      int var3 = 31 * var2 + Float.floatToIntBits(this.x);
      var3 = 31 * var3 + Float.floatToIntBits(this.y);
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
         Vector2f var2 = (Vector2f)var1;
         if (Float.floatToIntBits(this.x) != Float.floatToIntBits(var2.x)) {
            return false;
         } else {
            return Float.floatToIntBits(this.y) == Float.floatToIntBits(var2.y);
         }
      }
   }

   public String toString() {
      DecimalFormat var1 = new DecimalFormat(" 0.000E0;-");
      return this.toString(var1).replaceAll("E(\\d+)", "E+$1");
   }

   public String toString(NumberFormat var1) {
      return "(" + var1.format((double)this.x) + " " + var1.format((double)this.y) + ")";
   }

   public Vector2f fma(Vector2fc var1, Vector2fc var2) {
      this.x += var1.x() * var2.x();
      this.y += var1.y() * var2.y();
      return this;
   }

   public Vector2f fma(float var1, Vector2fc var2) {
      this.x += var1 * var2.x();
      this.y += var1 * var2.y();
      return this;
   }

   public Vector2f fma(Vector2fc var1, Vector2fc var2, Vector2f var3) {
      var3.x = this.x + var1.x() * var2.x();
      var3.y = this.y + var1.y() * var2.y();
      return var3;
   }

   public Vector2f fma(float var1, Vector2fc var2, Vector2f var3) {
      var3.x = this.x + var1 * var2.x();
      var3.y = this.y + var1 * var2.y();
      return var3;
   }

   public Vector2fc toImmutable() {
      return (Vector2fc)(!Options.DEBUG ? this : new Vector2f.Proxy(this));
   }

   private final class Proxy implements Vector2fc {
      private final Vector2fc delegate;

      Proxy(Vector2fc var2) {
         this.delegate = var2;
      }

      public float x() {
         return this.delegate.x();
      }

      public float y() {
         return this.delegate.y();
      }

      public ByteBuffer get(ByteBuffer var1) {
         return this.delegate.get(var1);
      }

      public ByteBuffer get(int var1, ByteBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public FloatBuffer get(FloatBuffer var1) {
         return this.delegate.get(var1);
      }

      public FloatBuffer get(int var1, FloatBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public Vector2f sub(Vector2fc var1, Vector2f var2) {
         return this.delegate.sub(var1, var2);
      }

      public Vector2f sub(float var1, float var2, Vector2f var3) {
         return this.delegate.sub(var1, var2, var3);
      }

      public float dot(Vector2fc var1) {
         return this.delegate.dot(var1);
      }

      public float angle(Vector2fc var1) {
         return this.delegate.angle(var1);
      }

      public float length() {
         return this.delegate.length();
      }

      public float lengthSquared() {
         return this.delegate.lengthSquared();
      }

      public float distance(Vector2fc var1) {
         return this.delegate.distance(var1);
      }

      public float distanceSquared(Vector2fc var1) {
         return this.delegate.distanceSquared(var1);
      }

      public float distance(float var1, float var2) {
         return this.delegate.distance(var1, var2);
      }

      public float distanceSquared(float var1, float var2) {
         return this.delegate.distanceSquared(var1, var2);
      }

      public Vector2f normalize(Vector2f var1) {
         return this.delegate.normalize(var1);
      }

      public Vector2f add(Vector2fc var1, Vector2f var2) {
         return this.delegate.add(var1, var2);
      }

      public Vector2f add(float var1, float var2, Vector2f var3) {
         return this.delegate.add(var1, var2, var3);
      }

      public Vector2f negate(Vector2f var1) {
         return this.delegate.negate(var1);
      }

      public Vector2f mul(float var1, Vector2f var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector2f mul(float var1, float var2, Vector2f var3) {
         return this.delegate.mul(var1, var2, var3);
      }

      public Vector2f mul(Vector2fc var1, Vector2f var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector2f lerp(Vector2fc var1, float var2, Vector2f var3) {
         return this.delegate.lerp(var1, var2, var3);
      }

      public Vector2f fma(Vector2fc var1, Vector2fc var2, Vector2f var3) {
         return this.delegate.fma(var1, var2, var3);
      }

      public Vector2f fma(float var1, Vector2fc var2, Vector2f var3) {
         return this.delegate.fma(var1, var2, var3);
      }
   }
}
