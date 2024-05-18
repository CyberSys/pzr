package zombie.iso;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import zombie.ChunkMapFilenames;
import zombie.IntArrayCache;

public class IsoLot {
   public static HashMap InfoHeaders = new HashMap();
   public static ArrayList InfoHeaderNames = new ArrayList();
   public static HashMap InfoFileNames = new HashMap();
   Integer[][][][] data = (Integer[][][][])null;
   RandomAccessFile in = null;
   LotHeader info;
   public int wx = 0;
   public int wy = 0;
   static String lastUsedPath = "";
   public static Stack pool = new Stack();
   public ArrayList arrays = new ArrayList();

   public static void Dispose() {
      InfoHeaders.clear();
      InfoHeaderNames.clear();
      InfoFileNames.clear();
   }

   public static String readString(RandomAccessFile var0) throws EOFException, IOException {
      String var1 = var0.readLine();
      return var1;
   }

   public static int readInt(RandomAccessFile var0) throws EOFException, IOException {
      int var1 = var0.read();
      int var2 = var0.read();
      int var3 = var0.read();
      int var4 = var0.read();
      if ((var1 | var2 | var3 | var4) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 0) + (var2 << 8) + (var3 << 16) + (var4 << 24);
      }
   }

   public static int readShort(RandomAccessFile var0) throws EOFException, IOException {
      int var1 = var0.read();
      int var2 = var0.read();
      if ((var1 | var2) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 0) + (var2 << 8);
      }
   }

   public static void put(IsoLot var0) {
      var0.info = null;
      ArrayList var1 = var0.arrays;

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         Integer[] var3 = (Integer[])var1.get(var2);

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = 0;
         }

         IntArrayCache.instance.put(var3);
      }

      var0.arrays.clear();
      pool.push(var0);
   }

   public static IsoLot get(Integer var0, Integer var1, Integer var2, Integer var3, IsoChunk var4) {
      IsoLot var5;
      if (pool.isEmpty()) {
         var5 = new IsoLot(var0, var1, var2, var3, var4);
         var5.load(var0, var1, var2, var3, var4);
         return var5;
      } else {
         var5 = (IsoLot)pool.pop();
         var5.arrays.clear();
         var5.load(var0, var1, var2, var3, var4);
         return var5;
      }
   }

   public void load(Integer var1, Integer var2, Integer var3, Integer var4, IsoChunk var5) {
      String var6 = ChunkMapFilenames.instance.getHeader(var1, var2);
      this.info = (LotHeader)InfoHeaders.get(var6);
      this.wx = var3;
      this.wy = var4;
      var5.lotheader = this.info;
      if (this.data == null) {
         this.data = new Integer[10][10][this.info.levels][];
      }

      try {
         var6 = "world_" + var1 + "_" + var2 + ".lotpack";
         File var7 = new File((String)InfoFileNames.get(var6));
         if (var7.exists()) {
         }

         if (this.in == null || !lastUsedPath.equals(var7.getAbsolutePath())) {
            if (this.in != null) {
               this.in.close();
            }

            this.in = new RandomAccessFile(var7.getAbsolutePath(), "r");
            lastUsedPath = var7.getAbsolutePath();
         }

         int var8 = 0;
         int var9 = this.wx - var1 * 30;
         int var10 = this.wy - var2 * 30;
         int var11 = var9 * 30 + var10;
         this.in.seek((long)(4 + var11 * 8));
         int var12 = readInt(this.in);
         this.in.seek((long)var12);

         for(int var13 = 0; var13 < this.info.levels; ++var13) {
            for(int var14 = 0; var14 < 10; ++var14) {
               for(int var15 = 0; var15 < 10; ++var15) {
                  if (var8 > 0) {
                     --var8;
                     this.data[var14][var15][var13] = null;
                  } else {
                     int var16 = readInt(this.in);
                     if (var16 == -1) {
                        var8 = readInt(this.in);
                        if (var8 > 0) {
                           --var8;
                           this.data[var14][var15][var13] = null;
                           continue;
                        }
                     }

                     if (var16 > 1) {
                        this.data[var14][var15][var13] = IntArrayCache.instance.get(var16 - 1);
                        this.arrays.add(this.data[var14][var15][var13]);
                        int var17 = readInt(this.in);

                        for(int var18 = 1; var18 < var16; ++var18) {
                           int var19 = readInt(this.in);
                           this.data[var14][var15][var13][var18 - 1] = var19;
                        }
                     } else {
                        this.data[var14][var15][var13] = null;
                     }
                  }
               }
            }
         }
      } catch (Exception var20) {
      }

   }

   public IsoLot(Integer var1, Integer var2, Integer var3, Integer var4, IsoChunk var5) {
   }

   public class Zone {
      public String name;
      public String val;
      public int x;
      public int y;
      public int z;
      public int w;
      public int h;
   }
}
