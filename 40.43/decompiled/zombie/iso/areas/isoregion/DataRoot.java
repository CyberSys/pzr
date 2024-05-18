package zombie.iso.areas.isoregion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import zombie.debug.DebugLog;

public class DataRoot {
   private final Map cellMap = new HashMap();
   protected final DataRoot.SelectInfo select = new DataRoot.SelectInfo(this);
   private final ArrayList dirtyMasterRegions = new ArrayList();
   private final ArrayList dirtyChunks = new ArrayList();
   protected static int recalcs;
   protected static int floodFills;
   protected static int merges;

   protected void getAllChunks(List var1) {
      Iterator var2 = this.cellMap.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         ((DataCell)var3.getValue()).getAllChunks(var1);
      }

   }

   private DataCell getCell(int var1) {
      return (DataCell)this.cellMap.get(var1);
   }

   private DataCell addCell(int var1, int var2, int var3) {
      DataCell var4 = new DataCell(var1, var2, var3);
      this.cellMap.put(var3, var4);
      return var4;
   }

   public DataChunk getDataChunk(int var1, int var2) {
      int var3 = IsoRegion.hash(var1 / 30, var2 / 30);
      DataCell var4 = (DataCell)this.cellMap.get(var3);
      if (var4 != null) {
         int var5 = IsoRegion.hash(var1, var2);
         return var4.getChunk(var5);
      } else {
         return null;
      }
   }

   public void setDataChunk(DataChunk var1) {
      int var2 = IsoRegion.hash(var1.getChunkX() / 30, var1.getChunkY() / 30);
      DataCell var3 = (DataCell)this.cellMap.get(var2);
      if (var3 == null) {
         var3 = this.addCell(var1.getChunkX() / 30, var1.getChunkY() / 30, var2);
      }

      if (var3 != null) {
         var3.setChunk(var1);
      }

   }

   public byte getDataSquare(int var1, int var2, int var3) {
      this.select.reset(var1, var2, var3, true, false);
      return this.select.square;
   }

   public MasterRegion getMasterRegion(int var1, int var2, int var3) {
      this.select.reset(var1, var2, var3, true, false);
      if (this.select.chunk != null) {
         ChunkRegion var4 = this.select.chunk.getRegion(this.select.chunkSquareX, this.select.chunkSquareY, var3);
         if (var4 != null) {
            return var4.getMasterRegion();
         }
      }

      return null;
   }

   public byte getSquareFlags(int var1, int var2, int var3) {
      this.select.reset(var1, var2, var3, true, false);
      return this.select.square;
   }

   public ChunkRegion getChunkRegion(int var1, int var2, int var3) {
      this.select.reset(var1, var2, var3, true, false);
      return this.select.chunk != null ? this.select.chunk.getRegion(this.select.chunkSquareX, this.select.chunkSquareY, var3) : null;
   }

   protected void resetAllData() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.cellMap.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         DataCell var4 = (DataCell)var3.getValue();
         Iterator var5 = var4.dataChunks.entrySet().iterator();

         while(var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            DataChunk var7 = (DataChunk)var6.getValue();

            for(int var8 = 0; var8 < 8; ++var8) {
               Iterator var9 = var7.getChunkRegions(var8).iterator();

               while(var9.hasNext()) {
                  ChunkRegion var10 = (ChunkRegion)var9.next();
                  if (var10.getMasterRegion() != null && !var1.contains(var10.getMasterRegion())) {
                     var1.add(var10.getMasterRegion());
                  }

                  var10.setMasterRegion((MasterRegion)null);
                  ChunkRegion.release(var10);
               }
            }
         }

         var4.dataChunks.clear();
      }

      this.cellMap.clear();
      var2 = var1.iterator();

      while(var2.hasNext()) {
         MasterRegion var11 = (MasterRegion)var2.next();
         MasterRegion.release(var11);
      }

   }

   protected void EnqueueDirtyChunk(DataChunk var1) {
      if (!this.dirtyChunks.contains(var1)) {
         this.dirtyChunks.add(var1);
      }

   }

   protected void EnqueueDirtyMasterRegion(MasterRegion var1) {
      if (!this.dirtyMasterRegions.contains(var1)) {
         this.dirtyMasterRegions.add(var1);
      }

   }

   protected void DequeueDirtyMasterRegion(MasterRegion var1) {
      if (this.dirtyMasterRegions.contains(var1)) {
         this.dirtyMasterRegions.remove(var1);
      }

   }

   protected void processDirtyChunks() {
      if (this.dirtyChunks.size() > 0) {
         long var1 = System.nanoTime();
         recalcs = 0;
         floodFills = 0;
         merges = 0;

         Iterator var3;
         DataChunk var4;
         for(var3 = this.dirtyChunks.iterator(); var3.hasNext(); ++recalcs) {
            var4 = (DataChunk)var3.next();
            var4.recalculate();
         }

         var3 = this.dirtyChunks.iterator();

         while(var3.hasNext()) {
            var4 = (DataChunk)var3.next();
            DataChunk var5 = this.getDataChunk(var4.getChunkX(), var4.getChunkY() - 1);
            DataChunk var6 = this.getDataChunk(var4.getChunkX() - 1, var4.getChunkY());
            DataChunk var7 = this.getDataChunk(var4.getChunkX(), var4.getChunkY() + 1);
            DataChunk var8 = this.getDataChunk(var4.getChunkX() + 1, var4.getChunkY());
            var4.link(var5, var6, var7, var8);
         }

         var3 = this.dirtyChunks.iterator();

         while(var3.hasNext()) {
            var4 = (DataChunk)var3.next();
            var4.interConnect();
         }

         var3 = this.dirtyChunks.iterator();

         while(var3.hasNext()) {
            var4 = (DataChunk)var3.next();
            var4.recalcRoofs();
            var4.unsetDirtyAndFloodAll();
         }

         if (this.dirtyMasterRegions.size() > 0) {
            var3 = this.dirtyMasterRegions.iterator();

            MasterRegion var11;
            while(var3.hasNext()) {
               var11 = (MasterRegion)var3.next();
               var11.unlinkNeighbors();
            }

            var3 = this.dirtyMasterRegions.iterator();

            while(var3.hasNext()) {
               var11 = (MasterRegion)var3.next();
               var11.linkNeighbors();
            }

            this.dirtyMasterRegions.clear();
         }

         this.dirtyChunks.clear();
         long var9 = System.nanoTime();
         long var10 = var9 - var1;
         if (IsoRegion.PRINT_D) {
            DebugLog.log("--- IsoRegion update: " + String.format("%.6f", (double)var10 / 1000000.0D) + "ms, recalcs = " + recalcs + ", merges = " + merges + ", floodfills = " + floodFills);
         }
      }

   }

   protected static class SelectInfo {
      public int x;
      public int y;
      public int z;
      public int chunkSquareX;
      public int chunkSquareY;
      public int chunkx;
      public int chunky;
      public int cellx;
      public int celly;
      public int chunkID;
      public int cellID;
      public DataCell cell;
      public DataChunk chunk;
      public byte square;
      private final DataRoot root;

      private SelectInfo(DataRoot var1) {
         this.root = var1;
      }

      public void reset(int var1, int var2, int var3, boolean var4, boolean var5) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
         this.chunkSquareX = var1 % 10;
         this.chunkSquareY = var2 % 10;
         this.chunkx = var1 / 10;
         this.chunky = var2 / 10;
         this.cellx = var1 / 300;
         this.celly = var2 / 300;
         this.chunkID = IsoRegion.hash(this.chunkx, this.chunky);
         this.cellID = IsoRegion.hash(this.cellx, this.celly);
         this.cell = null;
         this.chunk = null;
         this.square = -1;
         if (var4) {
            this.ensureSquare(var5);
         }

      }

      public void ensureCell(boolean var1) {
         if (this.cell == null) {
            this.cell = this.root.getCell(this.cellID);
         }

         if (this.cell == null && var1) {
            this.cell = this.root.addCell(this.cellx, this.celly, this.cellID);
         }

      }

      public void ensureChunk(boolean var1) {
         this.ensureCell(var1);
         if (this.cell != null) {
            if (this.chunk == null) {
               this.chunk = this.cell.getChunk(this.chunkID);
            }

            if (this.chunk == null && var1) {
               this.chunk = this.cell.addChunk(this.chunkx, this.chunky, this.chunkID);
            }

         }
      }

      public void ensureSquare(boolean var1) {
         this.ensureCell(var1);
         if (this.cell != null) {
            this.ensureChunk(var1);
            if (this.chunk != null) {
               if (this.square == -1) {
                  this.square = this.chunk.getSquare(this.chunkSquareX, this.chunkSquareY, this.z, true);
               }

               if (this.square == -1 && var1) {
                  this.square = this.chunk.setOrAddSquare(this.chunkSquareX, this.chunkSquareY, this.z, (byte)0, true);
               }

            }
         }
      }

      // $FF: synthetic method
      SelectInfo(DataRoot var1, Object var2) {
         this(var1);
      }
   }
}
