package zombie.popman;

import java.util.ArrayDeque;
import java.util.ArrayList;

abstract class ObjectPool {
   private ArrayDeque pool = new ArrayDeque();

   public Object alloc() {
      return this.pool.isEmpty() ? this.makeObject() : this.pool.pop();
   }

   public void release(Object var1) {
      assert var1 != null;

      assert !this.pool.contains(var1);

      this.pool.push(var1);
   }

   public void release(ArrayList var1) {
      if (!var1.isEmpty()) {
         for(int var2 = 0; var2 < var1.size(); ++var2) {
            assert !this.pool.contains(var1.get(var2));
         }

         this.pool.addAll(var1);
      }
   }

   public void clear() {
      this.pool.clear();
   }

   protected abstract Object makeObject();
}
