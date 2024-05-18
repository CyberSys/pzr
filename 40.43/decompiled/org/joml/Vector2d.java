package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Vector2d implements Externalizable, Vector2dc {
   private static final long serialVersionUID = 1L;
   public double x;
   public double y;

   public Vector2d() {
   }

   public Vector2d(double var1) {
      this(var1, var1);
   }

   public Vector2d(double var1, double var3) {
      this.x = var1;
      this.y = var3;
   }

   public Vector2d(Vector2dc var1) {
      this.x = var1.x();
      this.y = var1.y();
   }

   public Vector2d(Vector2fc var1) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
   }

   public Vector2d(ByteBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector2d(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public Vector2d(DoubleBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector2d(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public double x() {
      return this.x;
   }

   public double y() {
      return this.y;
   }

   public Vector2d set(double var1) {
      return this.set(var1, var1);
   }

   public Vector2d set(double var1, double var3) {
      this.x = var1;
      this.y = var3;
      return this;
   }

   public Vector2d set(Vector2dc var1) {
      this.x = var1.x();
      this.y = var1.y();
      return this;
   }

   public Vector2d set(Vector2fc var1) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      return this;
   }

   public Vector2d set(ByteBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector2d set(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector2d set(DoubleBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector2d set(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector2d setComponent(int var1, double var2) throws IllegalArgumentException {
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

   public DoubleBuffer get(DoubleBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public DoubleBuffer get(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.put(this, var1, var2);
      return var2;
   }

   public Vector2d perpendicular() {
      return this.set(this.y, this.x * -1.0D);
   }

   public Vector2d sub(Vector2dc var1) {
      this.x -= var1.x();
      this.y -= var1.y();
      return this;
   }

   public Vector2d sub(double var1, double var3) {
      this.x -= var1;
      this.y -= var3;
      return this;
   }

   public Vector2d sub(double var1, double var3, Vector2d var5) {
      var5.x = this.x - var1;
      var5.y = this.y - var3;
      return var5;
   }

   public Vector2d sub(Vector2fc var1) {
      this.x -= (double)var1.x();
      this.y -= (double)var1.y();
      return this;
   }

   public Vector2d sub(Vector2dc var1, Vector2d var2) {
      var2.x = this.x - var1.x();
      var2.y = this.y - var1.y();
      return var2;
   }

   public Vector2d sub(Vector2fc var1, Vector2d var2) {
      var2.x = this.x + (double)var1.x();
      var2.y = this.y + (double)var1.y();
      return var2;
   }

   public Vector2d mul(double var1) {
      this.x *= var1;
      this.y *= var1;
      return this;
   }

   public Vector2d mul(double var1, Vector2d var3) {
      var3.x = this.x * var1;
      var3.y = this.y * var1;
      return var3;
   }

   public Vector2d mul(double var1, double var3) {
      this.x *= var1;
      this.y *= var3;
      return this;
   }

   public Vector2d mul(double var1, double var3, Vector2d var5) {
      var5.x = this.x * var1;
      var5.y = this.y * var3;
      return var5;
   }

   public Vector2d mul(Vector2dc var1) {
      this.x *= var1.x();
      this.y *= var1.y();
      return this;
   }

   public Vector2d mul(Vector2dc var1, Vector2d var2) {
      var2.x = this.x * var1.x();
      var2.y = this.y * var1.y();
      return var2;
   }

   public double dot(Vector2dc var1) {
      return this.x * var1.x() + this.y * var1.y();
   }

   public double angle(Vector2dc var1) {
      double var2 = this.x * var1.x() + this.y * var1.y();
      double var4 = this.x * var1.y() - this.y * var1.x();
      return Math.atan2(var4, var2);
   }

   public double length() {
      return Math.sqrt(this.x * this.x + this.y * this.y);
   }

   public double distance(Vector2dc var1) {
      return this.distance(var1.x(), var1.y());
   }

   public double distance(Vector2fc var1) {
      return this.distance((double)var1.x(), (double)var1.y());
   }

   public double distance(double var1, double var3) {
      double var5 = this.x - var1;
      double var7 = this.y - var3;
      return Math.sqrt(var5 * var5 + var7 * var7);
   }

   public Vector2d normalize() {
      double var1 = 1.0D / Math.sqrt(this.x * this.x + this.y * this.y);
      this.x *= var1;
      this.y *= var1;
      return this;
   }

   public Vector2d normalize(Vector2d var1) {
      double var2 = 1.0D / Math.sqrt(this.x * this.x + this.y * this.y);
      var1.x = this.x * var2;
      var1.y = this.y * var2;
      return var1;
   }

   public Vector2d add(Vector2dc var1) {
      this.x += var1.x();
      this.y += var1.y();
      return this;
   }

   public Vector2d add(double var1, double var3) {
      this.x += var1;
      this.y += var3;
      return this;
   }

   public Vector2d add(double var1, double var3, Vector2d var5) {
      var5.x = this.x + var1;
      var5.y = this.y + var3;
      return var5;
   }

   public Vector2d add(Vector2fc var1) {
      this.x += (double)var1.x();
      this.y += (double)var1.y();
      return this;
   }

   public Vector2d add(Vector2dc var1, Vector2d var2) {
      var2.x = this.x + var1.x();
      var2.y = this.y + var1.y();
      return var2;
   }

   public Vector2d add(Vector2fc var1, Vector2d var2) {
      var2.x = this.x + (double)var1.x();
      var2.y = this.y + (double)var1.y();
      return var2;
   }

   public Vector2d zero() {
      this.x = 0.0D;
      this.y = 0.0D;
      return this;
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.x = var1.readDouble();
      this.y = var1.readDouble();
   }

   public Vector2d negate() {
      this.x = -this.x;
      this.y = -this.y;
      return this;
   }

   public Vector2d negate(Vector2d var1) {
      var1.x = -this.x;
      var1.y = -this.y;
      return var1;
   }

   public Vector2d lerp(Vector2dc var1, double var2) {
      return this.lerp(var1, var2, this);
   }

   public Vector2d lerp(Vector2dc var1, double var2, Vector2d var4) {
      var4.x = this.x + (var1.x() - this.x) * var2;
      var4.y = this.y + (var1.y() - this.y) * var2;
      return var4;
   }

   public int hashCode() {
      byte var2 = 1;
      long var3 = Double.doubleToLongBits(this.x);
      int var5 = 31 * var2 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.y);
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
         Vector2d var2 = (Vector2d)var1;
         if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(var2.x)) {
            return false;
         } else {
            return Double.doubleToLongBits(this.y) == Double.doubleToLongBits(var2.y);
         }
      }
   }

   public String toString() {
      DecimalFormat var1 = new DecimalFormat(" 0.000E0;-");
      return this.toString(var1).replaceAll("E(\\d+)", "E+$1");
   }

   public String toString(NumberFormat var1) {
      return "(" + var1.format(this.x) + " " + var1.format(this.y) + ")";
   }

   public Vector2d fma(Vector2dc var1, Vector2dc var2) {
      this.x += var1.x() * var2.x();
      this.y += var1.y() * var2.y();
      return this;
   }

   public Vector2d fma(double var1, Vector2dc var3) {
      this.x += var1 * var3.x();
      this.y += var1 * var3.y();
      return this;
   }

   public Vector2d fma(Vector2dc var1, Vector2dc var2, Vector2d var3) {
      var3.x = this.x + var1.x() * var2.x();
      var3.y = this.y + var1.y() * var2.y();
      return var3;
   }

   public Vector2d fma(double var1, Vector2dc var3, Vector2d var4) {
      var4.x = this.x + var1 * var3.x();
      var4.y = this.y + var1 * var3.y();
      return var4;
   }

   public Vector2dc toImmutable() {
      return (Vector2dc)(!Options.DEBUG ? this : new Vector2d.Proxy(this));
   }

   private final class Proxy implements Vector2dc {
      private final Vector2dc delegate;

      Proxy(Vector2dc var2) {
         this.delegate = var2;
      }

      public double x() {
         return this.delegate.x();
      }

      public double y() {
         return this.delegate.y();
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

      public Vector2d sub(double var1, double var3, Vector2d var5) {
         return this.delegate.sub(var1, var3, var5);
      }

      public Vector2d sub(Vector2dc var1, Vector2d var2) {
         return this.delegate.sub(var1, var2);
      }

      public Vector2d sub(Vector2fc var1, Vector2d var2) {
         return this.delegate.sub(var1, var2);
      }

      public Vector2d mul(double var1, Vector2d var3) {
         return this.delegate.mul(var1, var3);
      }

      public Vector2d mul(double var1, double var3, Vector2d var5) {
         return this.delegate.mul(var1, var3, var5);
      }

      public Vector2d mul(Vector2dc var1, Vector2d var2) {
         return this.delegate.mul(var1, var2);
      }

      public double dot(Vector2dc var1) {
         return this.delegate.dot(var1);
      }

      public double angle(Vector2dc var1) {
         return this.delegate.angle(var1);
      }

      public double length() {
         return this.delegate.length();
      }

      public double distance(Vector2dc var1) {
         return this.delegate.distance(var1);
      }

      public double distance(Vector2fc var1) {
         return this.delegate.distance(var1);
      }

      public double distance(double var1, double var3) {
         return this.delegate.distance(var1, var3);
      }

      public Vector2d normalize(Vector2d var1) {
         return this.delegate.normalize(var1);
      }

      public Vector2d add(double var1, double var3, Vector2d var5) {
         return this.delegate.add(var1, var3, var5);
      }

      public Vector2d add(Vector2dc var1, Vector2d var2) {
         return this.delegate.add(var1, var2);
      }

      public Vector2d add(Vector2fc var1, Vector2d var2) {
         return this.delegate.add(var1, var2);
      }

      public Vector2d negate(Vector2d var1) {
         return this.delegate.negate(var1);
      }

      public Vector2d lerp(Vector2dc var1, double var2, Vector2d var4) {
         return this.delegate.lerp(var1, var2, var4);
      }

      public Vector2d fma(Vector2dc var1, Vector2dc var2, Vector2d var3) {
         return this.delegate.fma(var1, var2, var3);
      }

      public Vector2d fma(double var1, Vector2dc var3, Vector2d var4) {
         return this.delegate.fma(var1, var3, var4);
      }
   }
}
