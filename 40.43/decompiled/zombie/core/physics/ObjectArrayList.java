package zombie.core.physics;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.RandomAccess;

public final class ObjectArrayList extends AbstractList implements RandomAccess, Externalizable {
   private Object[] array;
   private int size;

   public ObjectArrayList() {
      this(16);
   }

   public ObjectArrayList(int var1) {
      this.array = (Object[])(new Object[var1]);
   }

   public boolean add(Object var1) {
      if (this.size == this.array.length) {
         this.expand();
      }

      this.array[this.size++] = var1;
      return true;
   }

   public void add(int var1, Object var2) {
      if (this.size == this.array.length) {
         this.expand();
      }

      int var3 = this.size - var1;
      if (var3 > 0) {
         System.arraycopy(this.array, var1, this.array, var1 + 1, var3);
      }

      this.array[var1] = var2;
      ++this.size;
   }

   public Object remove(int var1) {
      if (var1 >= 0 && var1 < this.size) {
         Object var2 = this.array[var1];
         System.arraycopy(this.array, var1 + 1, this.array, var1, this.size - var1 - 1);
         this.array[this.size - 1] = null;
         --this.size;
         return var2;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private void expand() {
      Object[] var1 = (Object[])(new Object[this.array.length << 1]);
      System.arraycopy(this.array, 0, var1, 0, this.array.length);
      this.array = var1;
   }

   public void removeQuick(int var1) {
      System.arraycopy(this.array, var1 + 1, this.array, var1, this.size - var1 - 1);
      this.array[this.size - 1] = null;
      --this.size;
   }

   public Object get(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.array[var1];
      }
   }

   public Object getQuick(int var1) {
      return this.array[var1];
   }

   public Object set(int var1, Object var2) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException();
      } else {
         Object var3 = this.array[var1];
         this.array[var1] = var2;
         return var3;
      }
   }

   public void setQuick(int var1, Object var2) {
      this.array[var1] = var2;
   }

   public int size() {
      return this.size;
   }

   public int capacity() {
      return this.array.length;
   }

   public void clear() {
      this.size = 0;
   }

   public int indexOf(Object var1) {
      int var2 = this.size;
      Object[] var3 = this.array;
      int var4 = 0;

      while(true) {
         if (var4 >= var2) {
            return -1;
         }

         if (var1 == null) {
            if (var3[var4] == null) {
               break;
            }
         } else if (var1.equals(var3[var4])) {
            break;
         }

         ++var4;
      }

      return var4;
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeInt(this.size);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeObject(this.array[var2]);
      }

   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.size = var1.readInt();

      int var2;
      for(var2 = 16; var2 < this.size; var2 <<= 1) {
      }

      this.array = (Object[])(new Object[var2]);

      for(int var3 = 0; var3 < this.size; ++var3) {
         this.array[var3] = var1.readObject();
      }

   }
}
