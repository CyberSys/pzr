package zombie.network;

import gnu.trove.map.hash.TIntLongHashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import zombie.GameWindow;
import zombie.core.Core;

public class ChunkChecksum {
   private static final TIntLongHashMap checksumCache = new TIntLongHashMap();
   private static final StringBuilder stringBuilder = new StringBuilder(128);
   private static final CRC32 crc32 = new CRC32();
   private static final byte[] bytes = new byte[1024];

   private static void noise(String var0) {
      if (Core.bDebug) {
      }

   }

   public static long getChecksum(int var0, int var1) throws IOException {
      synchronized(checksumCache) {
         int var3 = var0 + var1 * 30 * 1000;
         if (checksumCache.containsKey(var3)) {
            noise(var0 + "," + var1 + " found in cache crc=" + checksumCache.get(var3));
            return checksumCache.get(var3);
         } else {
            stringBuilder.setLength(0);
            stringBuilder.append(GameWindow.getGameModeCacheDir());
            stringBuilder.append(File.separator);
            stringBuilder.append(Core.GameSaveWorld);
            stringBuilder.append(File.separator);
            stringBuilder.append("map_");
            stringBuilder.append(var0);
            stringBuilder.append("_");
            stringBuilder.append(var1);
            stringBuilder.append(".bin");
            long var4 = createChecksum(stringBuilder.toString());
            checksumCache.put(var3, var4);
            noise(var0 + "," + var1 + " read from disk crc=" + var4);
            return var4;
         }
      }
   }

   public static long getChecksumIfExists(int var0, int var1) throws IOException {
      synchronized(checksumCache) {
         int var3 = var0 + var1 * 30 * 1000;
         return checksumCache.containsKey(var3) ? checksumCache.get(var3) : 0L;
      }
   }

   public static void setChecksum(int var0, int var1, long var2) {
      synchronized(checksumCache) {
         int var5 = var0 + var1 * 30 * 1000;
         checksumCache.put(var5, var2);
         noise(var0 + "," + var1 + " set crc=" + var2);
      }
   }

   public static long createChecksum(String var0) throws IOException {
      File var1 = new File(var0);
      if (!var1.exists()) {
         return 0L;
      } else {
         FileInputStream var2 = new FileInputStream(var0);
         Throwable var3 = null;

         try {
            crc32.reset();

            int var4;
            while((var4 = var2.read(bytes)) != -1) {
               crc32.update(bytes, 0, var4);
            }

            long var5 = crc32.getValue();
            return var5;
         } catch (Throwable var15) {
            var3 = var15;
            throw var15;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var14) {
                     var3.addSuppressed(var14);
                  }
               } else {
                  var2.close();
               }
            }

         }
      }
   }

   public static void Reset() {
      checksumCache.clear();
   }
}
