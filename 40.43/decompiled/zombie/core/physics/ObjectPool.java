package zombie.core.physics;

import java.util.HashMap;
import java.util.Map;

public class ObjectPool {
   private Class cls;
   private ObjectArrayList list = new ObjectArrayList();
   private static ThreadLocal threadLocal = new ThreadLocal() {
      protected Map initialValue() {
         return new HashMap();
      }
   };

   public ObjectPool(Class var1) {
      this.cls = var1;
   }

   private Object create() {
      try {
         return this.cls.newInstance();
      } catch (InstantiationException var2) {
         throw new IllegalStateException(var2);
      } catch (IllegalAccessException var3) {
         throw new IllegalStateException(var3);
      }
   }

   public Object get() {
      return this.list.size() > 0 ? this.list.remove(this.list.size() - 1) : this.create();
   }

   public void release(Object var1) {
      this.list.add(var1);
   }

   public static ObjectPool get(Class var0) {
      Map var1 = (Map)threadLocal.get();
      ObjectPool var2 = (ObjectPool)var1.get(var0);
      if (var2 == null) {
         var2 = new ObjectPool(var0);
         var1.put(var0, var2);
      }

      return var2;
   }

   public static void cleanCurrentThread() {
      threadLocal.remove();
   }
}
