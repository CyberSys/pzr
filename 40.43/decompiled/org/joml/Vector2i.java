package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;

public class Vector2i implements Externalizable, Vector2ic {
   private static final long serialVersionUID = 1L;
   public int x;
   public int y;

   public Vector2i() {
   }

   public Vector2i(int var1) {
      this.x = var1;
      this.y = var1;
   }

   public Vector2i(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public Vector2i(Vector2ic var1) {
      this.x = var1.x();
      this.y = var1.y();
   }

   public Vector2i(ByteBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector2i(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public Vector2i(IntBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector2i(int var1, IntBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public int x() {
      return this.x;
   }

   public int y() {
      return this.y;
   }

   public Vector2i set(int var1) {
      this.x = var1;
      this.y = var1;
      return this;
   }

   public Vector2i set(int var1, int var2) {
      this.x = var1;
      this.y = var2;
      return this;
   }

   public Vector2i set(Vector2ic var1) {
      this.x = var1.x();
      this.y = var1.y();
      return this;
   }

   public Vector2i set(Vector2dc var1) {
      this.x = (int)var1.x();
      this.y = (int)var1.y();
      return this;
   }

   public Vector2i set(ByteBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector2i set(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector2i set(IntBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector2i set(int var1, IntBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector2i setComponent(int var1, int var2) throws IllegalArgumentException {
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

   public IntBuffer get(IntBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public IntBuffer get(int var1, IntBuffer var2) {
      MemUtil.INSTANCE.put(this, var1, var2);
      return var2;
   }

   public Vector2i sub(Vector2ic var1) {
      this.x -= var1.x();
      this.y -= var1.y();
      return this;
   }

   public Vector2i sub(Vector2ic var1, Vector2i var2) {
      var2.x = this.x - var1.x();
      var2.y = this.y - var1.y();
      return var2;
   }

   public Vector2i sub(int var1, int var2) {
      this.x -= var1;
      this.y -= var2;
      return this;
   }

   public Vector2i sub(int var1, int var2, Vector2i var3) {
      var3.x = this.x - var1;
      var3.y = this.y - var2;
      return var3;
   }

   public long lengthSquared() {
      return (long)(this.x * this.x + this.y * this.y);
   }

   public double length() {
      return Math.sqrt((double)this.lengthSquared());
   }

   public double distance(Vector2ic var1) {
      return Math.sqrt((double)this.distanceSquared(var1));
   }

   public double distance(int var1, int var2) {
      return Math.sqrt((double)this.distanceSquared(var1, var2));
   }

   public long distanceSquared(Vector2ic var1) {
      int var2 = this.x - var1.x();
      int var3 = this.y - var1.y();
      return (long)(var2 * var2 + var3 * var3);
   }

   public long distanceSquared(int var1, int var2) {
      int var3 = this.x - var1;
      int var4 = this.y - var2;
      return (long)(var3 * var3 + var4 * var4);
   }

   public Vector2i add(Vector2ic var1) {
      this.x += var1.x();
      this.y += var1.y();
      return this;
   }

   public Vector2i add(Vector2ic var1, Vector2i var2) {
      var2.x = this.x + var1.x();
      var2.y = this.y + var1.y();
      return var2;
   }

   public Vector2i add(int var1, int var2) {
      this.x += var1;
      this.y += var2;
      return this;
   }

   public Vector2i add(int var1, int var2, Vector2i var3) {
      var3.x = this.x + var1;
      var3.y = this.y + var2;
      return var3;
   }

   public Vector2i mul(int var1) {
      this.x *= var1;
      this.y *= var1;
      return this;
   }

   public Vector2i mul(int var1, Vector2i var2) {
      var2.x = this.x * var1;
      var2.y = this.y * var1;
      return var2;
   }

   public Vector2i mul(Vector2ic var1) {
      this.x += var1.x();
      this.y += var1.y();
      return this;
   }

   public Vector2i mul(Vector2ic var1, Vector2i var2) {
      var2.x = this.x * var1.x();
      var2.y = this.y * var1.y();
      return var2;
   }

   public Vector2i mul(int var1, int var2) {
      this.x *= var1;
      this.y *= var2;
      return this;
   }

   public Vector2i mul(int var1, int var2, Vector2i var3) {
      var3.x = this.x * var1;
      var3.y = this.y * var2;
      return var3;
   }

   public Vector2i zero() {
      this.x = 0;
      this.y = 0;
      return this;
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeInt(this.x);
      var1.writeInt(this.y);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.x = var1.readInt();
      this.y = var1.readInt();
   }

   public Vector2i negate() {
      this.x = -this.x;
      this.y = -this.y;
      return this;
   }

   public Vector2i negate(Vector2i var1) {
      var1.x = -this.x;
      var1.y = -this.y;
      return var1;
   }

   public int hashCode() {
      byte var2 = 1;
      int var3 = 31 * var2 + this.x;
      var3 = 31 * var3 + this.y;
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
         Vector2i var2 = (Vector2i)var1;
         if (this.x != var2.x) {
            return false;
         } else {
            return this.y == var2.y;
         }
      }
   }

   public String toString() {
      return "(" + this.x + " " + this.y + ")";
   }

   public String toString(NumberFormat var1) {
      return "(" + var1.format((long)this.x) + " " + var1.format((long)this.y) + ")";
   }

   public Vector2ic toImmutable() {
      return (Vector2ic)(!Options.DEBUG ? this : new Vector2i.Proxy(this));
   }

   private final class Proxy implements Vector2ic {
      private final Vector2ic delegate;

      Proxy(Vector2ic var2) {
         this.delegate = var2;
      }

      public int x() {
         return this.delegate.x();
      }

      public int y() {
         return this.delegate.y();
      }

      public ByteBuffer get(ByteBuffer var1) {
         return this.delegate.get(var1);
      }

      public ByteBuffer get(int var1, ByteBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public IntBuffer get(IntBuffer var1) {
         return this.delegate.get(var1);
      }

      public IntBuffer get(int var1, IntBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public Vector2i sub(Vector2ic var1, Vector2i var2) {
         return this.delegate.sub(var1, var2);
      }

      public Vector2i sub(int var1, int var2, Vector2i var3) {
         return this.delegate.sub(var1, var2, var3);
      }

      public long lengthSquared() {
         return this.delegate.lengthSquared();
      }

      public double length() {
         return this.delegate.length();
      }

      public double distance(Vector2ic var1) {
         return this.delegate.distance(var1);
      }

      public double distance(int var1, int var2) {
         return this.delegate.distance(var1, var2);
      }

      public long distanceSquared(Vector2ic var1) {
         return this.delegate.distanceSquared(var1);
      }

      public long distanceSquared(int var1, int var2) {
         return this.delegate.distanceSquared(var1, var2);
      }

      public Vector2i add(Vector2ic var1, Vector2i var2) {
         return this.delegate.add(var1, var2);
      }

      public Vector2i add(int var1, int var2, Vector2i var3) {
         return this.delegate.add(var1, var2, var3);
      }

      public Vector2i mul(int var1, Vector2i var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector2i mul(Vector2ic var1, Vector2i var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector2i mul(int var1, int var2, Vector2i var3) {
         return this.delegate.mul(var1, var2, var3);
      }

      public Vector2i negate(Vector2i var1) {
         return this.delegate.negate(var1);
      }
   }
}
