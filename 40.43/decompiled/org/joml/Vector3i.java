package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;

public class Vector3i implements Externalizable, Vector3ic {
   private static final long serialVersionUID = 1L;
   public int x;
   public int y;
   public int z;

   public Vector3i() {
   }

   public Vector3i(int var1) {
      this(var1, var1, var1);
   }

   public Vector3i(int var1, int var2, int var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public Vector3i(Vector3ic var1) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
   }

   public Vector3i(Vector2ic var1, int var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
   }

   public Vector3i(ByteBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector3i(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public Vector3i(IntBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector3i(int var1, IntBuffer var2) {
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

   public Vector3i set(Vector3ic var1) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      return this;
   }

   public Vector3i set(Vector3dc var1) {
      this.x = (int)var1.x();
      this.y = (int)var1.y();
      this.z = (int)var1.z();
      return this;
   }

   public Vector3i set(Vector2ic var1, int var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
      return this;
   }

   public Vector3i set(int var1) {
      return this.set(var1, var1, var1);
   }

   public Vector3i set(int var1, int var2, int var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      return this;
   }

   public Vector3i set(ByteBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector3i set(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector3i set(IntBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector3i set(int var1, IntBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector3i setComponent(int var1, int var2) throws IllegalArgumentException {
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

   public Vector3i sub(Vector3ic var1) {
      this.x -= var1.x();
      this.y -= var1.y();
      this.z -= var1.z();
      return this;
   }

   public Vector3i sub(Vector3ic var1, Vector3i var2) {
      var2.x = this.x - var1.x();
      var2.y = this.y - var1.y();
      var2.z = this.z - var1.z();
      return var2;
   }

   public Vector3i sub(int var1, int var2, int var3) {
      this.x -= var1;
      this.y -= var2;
      this.z -= var3;
      return this;
   }

   public Vector3i sub(int var1, int var2, int var3, Vector3i var4) {
      var4.x = this.x - var1;
      var4.y = this.y - var2;
      var4.z = this.z - var3;
      return var4;
   }

   public Vector3i add(Vector3ic var1) {
      this.x += var1.x();
      this.y += var1.y();
      this.z += var1.z();
      return this;
   }

   public Vector3i add(Vector3ic var1, Vector3i var2) {
      var2.x = this.x + var1.x();
      var2.y = this.y + var1.y();
      var2.z = this.z + var1.z();
      return var2;
   }

   public Vector3i add(int var1, int var2, int var3) {
      this.x += var1;
      this.y += var2;
      this.z += var3;
      return this;
   }

   public Vector3i add(int var1, int var2, int var3, Vector3i var4) {
      var4.x = this.x + var1;
      var4.y = this.y + var2;
      var4.z = this.z + var3;
      return var4;
   }

   public Vector3i mul(int var1) {
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      return this;
   }

   public Vector3i mul(int var1, Vector3i var2) {
      var2.x = this.x * var1;
      var2.y = this.y * var1;
      var2.y = this.z * var1;
      return var2;
   }

   public Vector3i mul(Vector3ic var1) {
      this.x += var1.x();
      this.y += var1.y();
      this.z += var1.z();
      return this;
   }

   public Vector3i mul(Vector3ic var1, Vector3i var2) {
      var2.x = this.x * var1.x();
      var2.y = this.y * var1.y();
      var2.z = this.z * var1.z();
      return var2;
   }

   public Vector3i mul(int var1, int var2, int var3) {
      this.x *= var1;
      this.y *= var2;
      this.z *= var3;
      return this;
   }

   public Vector3i mul(int var1, int var2, int var3, Vector3i var4) {
      var4.x = this.x * var1;
      var4.y = this.y * var2;
      var4.z = this.z * var3;
      return var4;
   }

   public long lengthSquared() {
      return (long)(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public double length() {
      return Math.sqrt((double)this.lengthSquared());
   }

   public double distance(Vector3ic var1) {
      return Math.sqrt((double)this.distanceSquared(var1));
   }

   public double distance(int var1, int var2, int var3) {
      return Math.sqrt((double)this.distanceSquared(var1, var2, var3));
   }

   public long distanceSquared(Vector3ic var1) {
      int var2 = this.x - var1.x();
      int var3 = this.y - var1.y();
      int var4 = this.z - var1.z();
      return (long)(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public long distanceSquared(int var1, int var2, int var3) {
      int var4 = this.x - var1;
      int var5 = this.y - var2;
      int var6 = this.z - var3;
      return (long)(var4 * var4 + var5 * var5 + var6 * var6);
   }

   public Vector3i zero() {
      this.x = 0;
      this.y = 0;
      this.z = 0;
      return this;
   }

   public String toString() {
      return "(" + this.x + " " + this.y + " " + this.z + ")";
   }

   public String toString(NumberFormat var1) {
      return "(" + var1.format((long)this.x) + " " + var1.format((long)this.y) + " " + var1.format((long)this.z) + ")";
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeInt(this.x);
      var1.writeInt(this.y);
      var1.writeInt(this.z);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.x = var1.readInt();
      this.y = var1.readInt();
      this.z = var1.readInt();
   }

   public Vector3i negate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
      return this;
   }

   public Vector3i negate(Vector3i var1) {
      var1.x = -this.x;
      var1.y = -this.y;
      var1.z = -this.z;
      return var1;
   }

   public int hashCode() {
      byte var2 = 1;
      int var3 = 31 * var2 + this.x;
      var3 = 31 * var3 + this.y;
      var3 = 31 * var3 + this.z;
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
         Vector3i var2 = (Vector3i)var1;
         if (this.x != var2.x) {
            return false;
         } else if (this.y != var2.y) {
            return false;
         } else {
            return this.z == var2.z;
         }
      }
   }

   public Vector3ic toImmutable() {
      return (Vector3ic)(!Options.DEBUG ? this : new Vector3i.Proxy(this));
   }

   private final class Proxy implements Vector3ic {
      private final Vector3ic delegate;

      Proxy(Vector3ic var2) {
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

      public Vector3i sub(Vector3ic var1, Vector3i var2) {
         return this.delegate.sub(var1, var2);
      }

      public Vector3i sub(int var1, int var2, int var3, Vector3i var4) {
         return this.delegate.sub(var1, var2, var3, var4);
      }

      public Vector3i add(Vector3ic var1, Vector3i var2) {
         return this.delegate.add(var1, var2);
      }

      public Vector3i add(int var1, int var2, int var3, Vector3i var4) {
         return this.delegate.add(var1, var2, var3, var4);
      }

      public Vector3i mul(int var1, Vector3i var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector3i mul(Vector3ic var1, Vector3i var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector3i mul(int var1, int var2, int var3, Vector3i var4) {
         return this.delegate.mul(var1, var2, var3, var4);
      }

      public long lengthSquared() {
         return this.delegate.lengthSquared();
      }

      public double length() {
         return this.delegate.length();
      }

      public double distance(Vector3ic var1) {
         return this.delegate.distance(var1);
      }

      public double distance(int var1, int var2, int var3) {
         return this.delegate.distance(var1, var2, var3);
      }

      public long distanceSquared(Vector3ic var1) {
         return this.delegate.distanceSquared(var1);
      }

      public long distanceSquared(int var1, int var2, int var3) {
         return this.delegate.distanceSquared(var1, var2, var3);
      }

      public Vector3i negate(Vector3i var1) {
         return this.delegate.negate(var1);
      }
   }
}
