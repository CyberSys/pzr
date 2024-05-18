package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;

public class Vector4i implements Externalizable, Vector4ic {
   private static final long serialVersionUID = 1L;
   public int x;
   public int y;
   public int z;
   public int w;

   public Vector4i() {
      this.w = 1;
   }

   public Vector4i(Vector4ic var1) {
      if (var1 instanceof Vector4i) {
         MemUtil.INSTANCE.copy((Vector4i)var1, this);
      } else {
         this.x = var1.x();
         this.y = var1.y();
         this.z = var1.z();
         this.w = var1.w();
      }

   }

   public Vector4i(Vector3ic var1, int var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      this.w = var2;
   }

   public Vector4i(Vector2ic var1, int var2, int var3) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
      this.w = var3;
   }

   public Vector4i(int var1) {
      MemUtil.INSTANCE.broadcast(var1, this);
   }

   public Vector4i(int var1, int var2, int var3, int var4) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.w = var4;
   }

   public Vector4i(ByteBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector4i(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public Vector4i(IntBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector4i(int var1, IntBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public int x() {
      return this.x;
   }

   public int y() {
      return this.y;
   }

   public int z() {
      return this.z;
   }

   public int w() {
      return this.w;
   }

   public Vector4i set(Vector4ic var1) {
      if (var1 instanceof Vector4i) {
         MemUtil.INSTANCE.copy((Vector4i)var1, this);
      } else {
         this.x = var1.x();
         this.y = var1.y();
         this.z = var1.z();
         this.w = var1.w();
      }

      return this;
   }

   public Vector4i set(Vector3ic var1, int var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      this.w = var2;
      return this;
   }

   public Vector4i set(Vector2ic var1, int var2, int var3) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
      this.w = var3;
      return this;
   }

   public Vector4i set(int var1) {
      MemUtil.INSTANCE.broadcast(var1, this);
      return this;
   }

   public Vector4i set(int var1, int var2, int var3, int var4) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.w = var4;
      return this;
   }

   public Vector4i set(ByteBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector4i set(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector4i set(IntBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector4i set(int var1, IntBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector4i setComponent(int var1, int var2) throws IllegalArgumentException {
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

   public IntBuffer get(IntBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public IntBuffer get(int var1, IntBuffer var2) {
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

   public Vector4i sub(Vector4ic var1) {
      this.x -= var1.x();
      this.y -= var1.y();
      this.z -= var1.z();
      this.w -= var1.w();
      return this;
   }

   public Vector4i sub(int var1, int var2, int var3, int var4) {
      this.x -= var1;
      this.y -= var2;
      this.z -= var3;
      this.w -= var4;
      return this;
   }

   public Vector4i sub(Vector4ic var1, Vector4i var2) {
      var2.x = this.x - var1.x();
      var2.y = this.y - var1.y();
      var2.z = this.z - var1.z();
      var2.w = this.w - var1.w();
      return var2;
   }

   public Vector4i sub(int var1, int var2, int var3, int var4, Vector4i var5) {
      var5.x = this.x - var1;
      var5.y = this.y - var2;
      var5.z = this.z - var3;
      var5.w = this.w - var4;
      return var5;
   }

   public Vector4i add(Vector4ic var1) {
      this.x += var1.x();
      this.y += var1.y();
      this.z += var1.z();
      this.w += var1.w();
      return this;
   }

   public Vector4i add(Vector4ic var1, Vector4i var2) {
      var2.x = this.x + var1.x();
      var2.y = this.y + var1.y();
      var2.z = this.z + var1.z();
      var2.w = this.w + var1.w();
      return var2;
   }

   public Vector4i add(int var1, int var2, int var3, int var4) {
      this.x += var1;
      this.y += var2;
      this.z += var3;
      this.w += var4;
      return this;
   }

   public Vector4i add(int var1, int var2, int var3, int var4, Vector4i var5) {
      var5.x = this.x + var1;
      var5.y = this.y + var2;
      var5.z = this.z + var3;
      var5.w = this.w + var4;
      return var5;
   }

   public Vector4i mul(Vector4ic var1) {
      this.x *= var1.x();
      this.y *= var1.y();
      this.z *= var1.z();
      this.w *= var1.w();
      return this;
   }

   public Vector4i mul(Vector4ic var1, Vector4i var2) {
      var2.x = this.x * var1.x();
      var2.y = this.y * var1.y();
      var2.z = this.z * var1.z();
      var2.w = this.w * var1.w();
      return var2;
   }

   public Vector4i div(Vector4ic var1) {
      this.x /= var1.x();
      this.y /= var1.y();
      this.z /= var1.z();
      this.w /= var1.w();
      return this;
   }

   public Vector4i div(Vector4ic var1, Vector4i var2) {
      var2.x = this.x / var1.x();
      var2.y = this.y / var1.y();
      var2.z = this.z / var1.z();
      var2.w = this.w / var1.w();
      return var2;
   }

   public Vector4i mul(float var1) {
      this.x = (int)((float)this.x * var1);
      this.y = (int)((float)this.y * var1);
      this.z = (int)((float)this.z * var1);
      this.w = (int)((float)this.w * var1);
      return this;
   }

   public Vector4i mul(float var1, Vector4i var2) {
      var2.x = (int)((float)this.x * var1);
      var2.y = (int)((float)this.y * var1);
      var2.z = (int)((float)this.z * var1);
      var2.w = (int)((float)this.w * var1);
      return var2;
   }

   public Vector4i div(int var1) {
      this.x /= var1;
      this.y /= var1;
      this.z /= var1;
      this.w /= var1;
      return this;
   }

   public Vector4i div(float var1, Vector4i var2) {
      var2.x = (int)((float)this.x / var1);
      var2.y = (int)((float)this.y / var1);
      var2.z = (int)((float)this.z / var1);
      var2.w = (int)((float)this.w / var1);
      return var2;
   }

   public long lengthSquared() {
      return (long)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
   }

   public double length() {
      return Math.sqrt((double)this.lengthSquared());
   }

   public double distance(Vector4ic var1) {
      return Math.sqrt((double)this.distanceSquared(var1));
   }

   public double distance(int var1, int var2, int var3, int var4) {
      return Math.sqrt((double)this.distanceSquared(var1, var2, var3, var4));
   }

   public int distanceSquared(Vector4ic var1) {
      int var2 = this.x - var1.x();
      int var3 = this.y - var1.y();
      int var4 = this.z - var1.z();
      int var5 = this.w - var1.w();
      return var2 * var2 + var3 * var3 + var4 * var4 + var5 * var5;
   }

   public int distanceSquared(int var1, int var2, int var3, int var4) {
      int var5 = this.x - var1;
      int var6 = this.y - var2;
      int var7 = this.z - var3;
      int var8 = this.w - var4;
      return var5 * var5 + var6 * var6 + var7 * var7 + var8 * var8;
   }

   public int dot(Vector4ic var1) {
      return this.x * var1.x() + this.y * var1.y() + this.z * var1.z() + this.w * var1.w();
   }

   public Vector4i zero() {
      MemUtil.INSTANCE.zero(this);
      return this;
   }

   public Vector4i negate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
      this.w = -this.w;
      return this;
   }

   public Vector4i negate(Vector4i var1) {
      var1.x = -this.x;
      var1.y = -this.y;
      var1.z = -this.z;
      var1.w = -this.w;
      return var1;
   }

   public String toString() {
      return "(" + this.x + " " + this.y + " " + this.z + " " + this.w + ")";
   }

   public String toString(NumberFormat var1) {
      return "(" + var1.format((long)this.x) + " " + var1.format((long)this.y) + " " + var1.format((long)this.z) + " " + var1.format((long)this.w) + ")";
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeInt(this.x);
      var1.writeInt(this.y);
      var1.writeInt(this.z);
      var1.writeInt(this.w);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.x = var1.readInt();
      this.y = var1.readInt();
      this.z = var1.readInt();
      this.w = var1.readInt();
   }

   public Vector4i min(Vector4ic var1) {
      this.x = Math.min(this.x, var1.x());
      this.y = Math.min(this.y, var1.y());
      this.z = Math.min(this.z, var1.z());
      this.w = Math.min(this.w, var1.w());
      return this;
   }

   public Vector4i max(Vector4ic var1) {
      this.x = Math.max(this.x, var1.x());
      this.y = Math.max(this.y, var1.y());
      this.z = Math.max(this.z, var1.z());
      this.w = Math.min(this.w, var1.w());
      return this;
   }

   public int hashCode() {
      byte var2 = 1;
      int var3 = 31 * var2 + this.x;
      var3 = 31 * var3 + this.y;
      var3 = 31 * var3 + this.z;
      var3 = 31 * var3 + this.w;
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
         Vector4i var2 = (Vector4i)var1;
         if (this.x != var2.x) {
            return false;
         } else if (this.y != var2.y) {
            return false;
         } else if (this.z != var2.z) {
            return false;
         } else {
            return this.w == var2.w;
         }
      }
   }

   public Vector4ic toImmutable() {
      return (Vector4ic)(!Options.DEBUG ? this : new Vector4i.Proxy(this));
   }

   private final class Proxy implements Vector4ic {
      private final Vector4ic delegate;

      Proxy(Vector4ic var2) {
         this.delegate = var2;
      }

      public int x() {
         return this.delegate.x();
      }

      public int y() {
         return this.delegate.y();
      }

      public int z() {
         return this.delegate.z();
      }

      public int w() {
         return this.delegate.w();
      }

      public IntBuffer get(IntBuffer var1) {
         return this.delegate.get(var1);
      }

      public IntBuffer get(int var1, IntBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public ByteBuffer get(ByteBuffer var1) {
         return this.delegate.get(var1);
      }

      public ByteBuffer get(int var1, ByteBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public Vector4i sub(Vector4ic var1, Vector4i var2) {
         return this.delegate.sub(var1, var2);
      }

      public Vector4i sub(int var1, int var2, int var3, int var4, Vector4i var5) {
         return this.delegate.sub(var1, var2, var3, var4, var5);
      }

      public Vector4i add(Vector4ic var1, Vector4i var2) {
         return this.delegate.add(var1, var2);
      }

      public Vector4i add(int var1, int var2, int var3, int var4, Vector4i var5) {
         return this.delegate.add(var1, var2, var3, var4, var5);
      }

      public Vector4i mul(Vector4ic var1, Vector4i var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector4i div(Vector4ic var1, Vector4i var2) {
         return this.delegate.div(var1, var2);
      }

      public Vector4i mul(float var1, Vector4i var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector4i div(float var1, Vector4i var2) {
         return this.delegate.div(var1, var2);
      }

      public long lengthSquared() {
         return this.delegate.lengthSquared();
      }

      public double length() {
         return this.delegate.length();
      }

      public double distance(Vector4ic var1) {
         return this.delegate.distance(var1);
      }

      public double distance(int var1, int var2, int var3, int var4) {
         return this.delegate.distance(var1, var2, var3, var4);
      }

      public int distanceSquared(Vector4ic var1) {
         return this.delegate.distanceSquared(var1);
      }

      public int distanceSquared(int var1, int var2, int var3, int var4) {
         return this.delegate.distanceSquared(var1, var2, var3, var4);
      }

      public int dot(Vector4ic var1) {
         return this.delegate.dot(var1);
      }

      public Vector4i negate(Vector4i var1) {
         return this.delegate.negate(var1);
      }
   }
}
