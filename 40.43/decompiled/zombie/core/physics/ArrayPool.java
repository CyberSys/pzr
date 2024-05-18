package zombie.core.physics;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ArrayPool {
   private Class componentType;
   private ObjectArrayList list = new ObjectArrayList();
   private Comparator comparator;
   private ArrayPool.IntValue key = new ArrayPool.IntValue();
   private static Comparator floatComparator = new Comparator() {
      public int compare(Object var1, Object var2) {
         int var3 = var1 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var1).value : ((float[])((float[])var1)).length;
         int var4 = var2 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var2).value : ((float[])((float[])var2)).length;
         return var3 > var4 ? 1 : (var3 < var4 ? -1 : 0);
      }
   };
   private static Comparator intComparator = new Comparator() {
      public int compare(Object var1, Object var2) {
         int var3 = var1 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var1).value : ((int[])((int[])var1)).length;
         int var4 = var2 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var2).value : ((int[])((int[])var2)).length;
         return var3 > var4 ? 1 : (var3 < var4 ? -1 : 0);
      }
   };
   private static Comparator objectComparator = new Comparator() {
      public int compare(Object var1, Object var2) {
         int var3 = var1 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var1).value : ((Object[])((Object[])var1)).length;
         int var4 = var2 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var2).value : ((Object[])((Object[])var2)).length;
         return var3 > var4 ? 1 : (var3 < var4 ? -1 : 0);
      }
   };
   private static ThreadLocal threadLocal = new ThreadLocal() {
      protected Map initialValue() {
         return new HashMap();
      }
   };

   public ArrayPool(Class var1) {
      this.componentType = var1;
      if (var1 == Float.TYPE) {
         this.comparator = floatComparator;
      } else if (var1 == Integer.TYPE) {
         this.comparator = intComparator;
      } else {
         if (var1.isPrimitive()) {
            throw new UnsupportedOperationException("unsupported type " + var1);
         }

         this.comparator = objectComparator;
      }

   }

   private Object create(int var1) {
      return Array.newInstance(this.componentType, var1);
   }

   public Object getFixed(int var1) {
      this.key.value = var1;
      int var2 = Collections.binarySearch(this.list, this.key, this.comparator);
      return var2 < 0 ? this.create(var1) : this.list.remove(var2);
   }

   public Object getAtLeast(int var1) {
      this.key.value = var1;
      int var2 = Collections.binarySearch(this.list, this.key, this.comparator);
      if (var2 < 0) {
         var2 = -var2 - 1;
         return var2 < this.list.size() ? this.list.remove(var2) : this.create(var1);
      } else {
         return this.list.remove(var2);
      }
   }

   public void release(Object var1) {
      int var2 = Collections.binarySearch(this.list, var1, this.comparator);
      if (var2 < 0) {
         var2 = -var2 - 1;
      }

      this.list.add(var2, var1);
      if (this.comparator == objectComparator) {
         Object[] var3 = (Object[])((Object[])var1);

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = null;
         }
      }

   }

   public static ArrayPool get(Class var0) {
      Map var1 = (Map)threadLocal.get();
      ArrayPool var2 = (ArrayPool)var1.get(var0);
      if (var2 == null) {
         var2 = new ArrayPool(var0);
         var1.put(var0, var2);
      }

      return var2;
   }

   public static void cleanCurrentThread() {
      threadLocal.remove();
   }

   private static class IntValue {
      public int value;

      private IntValue() {
      }

      // $FF: synthetic method
      IntValue(Object var1) {
         this();
      }
   }
}
