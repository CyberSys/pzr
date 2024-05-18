package zombie.iso.areas.isoregion;

import java.util.ArrayDeque;
import zombie.debug.DebugLog;

public class DataSquarePos {
   private static ArrayDeque pool = new ArrayDeque();
   public int x;
   public int y;
   public int z;

   static DataSquarePos alloc(int var0, int var1, int var2) {
      DataSquarePos var3 = !pool.isEmpty() ? (DataSquarePos)pool.pop() : new DataSquarePos();
      var3.set(var0, var1, var2);
      return var3;
   }

   static void release(DataSquarePos var0) {
      assert !pool.contains(var0);

      if (IsoRegion.PRINT_D && pool.contains(var0)) {
         DebugLog.log("Warning: DataSquarePos.release Trying to release a ChunkRegion twice.");
      } else {
         pool.push(var0.reset());
      }
   }

   private DataSquarePos() {
   }

   private DataSquarePos reset() {
      return this;
   }

   public void set(int var1, int var2, int var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }
}
