package zombie.core.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public final class DirectBufferAllocator {
   private static final boolean DEBUG = false;
   private static final Map MEMMAP = new TreeMap();

   private DirectBufferAllocator() {
   }

   public static WrappedBuffer allocate(int var0) {
      Integer var1 = new Integer(var0);
      Iterator var2 = MEMMAP.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         Integer var4 = (Integer)var3.getKey();
         WrappedBuffer var5 = (WrappedBuffer)var3.getValue();
         if (var5.isDisposed()) {
            if (var4 >= var0) {
               var5.allocate();
               return var5;
            }

            var2.remove();
            var5.clear();
         }
      }

      ByteBuffer var6 = ByteBuffer.allocateDirect(var0).order(ByteOrder.nativeOrder());
      WrappedBuffer var7 = new WrappedBuffer(var6);
      MEMMAP.put(var1, var7);
      return var7;
   }
}
