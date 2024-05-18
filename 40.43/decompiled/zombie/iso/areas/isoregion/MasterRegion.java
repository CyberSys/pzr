package zombie.iso.areas.isoregion;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.debug.DebugLog;

public class MasterRegion {
   private static int totalCreated = 0;
   private static int totalReleased = 0;
   private static int totalReused = 0;
   private static int nextID = 0;
   private static ArrayDeque idStack = new ArrayDeque();
   private static ArrayDeque pool = new ArrayDeque();
   private boolean isInPool = false;
   private int ID;
   private Color color;
   private boolean enclosed = true;
   private ArrayList chunkRegions = new ArrayList();
   private int squareSize = 0;
   private int roofCnt = 0;
   private boolean isDirtyEnclosed = false;
   private boolean isDirtyRoofed = false;
   private ArrayList neighbors = new ArrayList();

   protected static void printStats() {
      DebugLog.log("MasterRegion: Created: " + totalCreated + ", Re-used: " + totalReused + ", Released: " + totalReleased + ", InPool: " + pool.size() + ", nextID: " + nextID);
   }

   static MasterRegion alloc() {
      MasterRegion var0 = !pool.isEmpty() ? (MasterRegion)pool.pop() : null;
      if (var0 == null) {
         var0 = new MasterRegion();
         ++totalCreated;
      } else {
         ++totalReused;
      }

      var0.isInPool = false;
      var0.ID = idStack.isEmpty() ? nextID++ : (Integer)idStack.pop();
      if (var0.color == null) {
         var0.color = Colors.GetRandomColor();
      }

      var0.squareSize = 0;
      var0.roofCnt = 0;
      var0.enclosed = true;
      var0.isDirtyEnclosed = false;
      var0.isDirtyRoofed = false;
      return var0;
   }

   static void release(MasterRegion var0) {
      assert !pool.contains(var0);

      if (IsoRegion.PRINT_D && pool.contains(var0)) {
         DebugLog.log("Warning: MasterRegion.release Trying to release a MasterRegion twice.");
      } else {
         IsoRegionWorker.DequeueDirtyMasterRegion(var0);
         if (!var0.isInPool) {
            pool.push(var0.reset());
            ++totalReleased;
         }

      }
   }

   private MasterRegion() {
   }

   private MasterRegion reset() {
      this.isInPool = true;
      if (this.ID >= 0) {
         idStack.push(this.ID);
      }

      this.ID = -1;
      this.squareSize = 0;
      this.roofCnt = 0;
      this.enclosed = true;
      this.isDirtyRoofed = false;
      this.isDirtyEnclosed = false;
      this.unlinkNeighbors();
      if (this.chunkRegions.size() > 0) {
         if (IsoRegion.PRINT_D) {
            DebugLog.log("WARNING: MasterRegion.reset Resetting master region with chunkregions set");
         }

         Iterator var1 = this.chunkRegions.iterator();

         while(var1.hasNext()) {
            ChunkRegion var2 = (ChunkRegion)var1.next();
            var2.setMasterRegion((MasterRegion)null);
         }

         this.chunkRegions.clear();
      }

      return this;
   }

   public int getID() {
      return this.ID;
   }

   public Color getColor() {
      return this.color;
   }

   public int size() {
      return this.chunkRegions.size();
   }

   public int getSquareSize() {
      return this.squareSize;
   }

   protected void unlinkNeighbors() {
      Iterator var1 = this.neighbors.iterator();

      while(var1.hasNext()) {
         MasterRegion var2 = (MasterRegion)var1.next();
         var2.removeNeighbor(this);
      }

      this.neighbors.clear();
   }

   protected void linkNeighbors() {
      Iterator var1 = this.chunkRegions.iterator();

      while(var1.hasNext()) {
         ChunkRegion var2 = (ChunkRegion)var1.next();
         Iterator var3 = var2.getAllNeighbors().iterator();

         while(var3.hasNext()) {
            ChunkRegion var4 = (ChunkRegion)var3.next();
            if (var4.getMasterRegion() != null && var4.getMasterRegion() != this) {
               this.addNeighbor(var4.getMasterRegion());
               var4.getMasterRegion().addNeighbor(this);
            }
         }
      }

   }

   private void addNeighbor(MasterRegion var1) {
      if (!this.neighbors.contains(var1)) {
         this.neighbors.add(var1);
      }

   }

   private void removeNeighbor(MasterRegion var1) {
      this.neighbors.remove(var1);
   }

   public ArrayList getNeighbors() {
      return this.neighbors;
   }

   public ArrayList getDebugConnectedNeighborCopy() {
      ArrayList var1 = new ArrayList();
      if (this.neighbors.size() == 0) {
         return var1;
      } else {
         Iterator var2 = this.neighbors.iterator();

         while(var2.hasNext()) {
            MasterRegion var3 = (MasterRegion)var2.next();
            var1.add(var3);
         }

         return var1;
      }
   }

   public boolean isFogMask() {
      return this.isEnclosed() && this.isFullyRoofed();
   }

   public boolean isPlayerRoom() {
      return this.isFogMask();
   }

   public boolean isFullyRoofed() {
      return this.roofCnt == this.squareSize;
   }

   public int getRoofCnt() {
      return this.roofCnt;
   }

   protected void addRoof() {
      ++this.roofCnt;
      if (this.roofCnt > this.squareSize) {
         this.roofCnt = this.squareSize;
      }

   }

   protected void removeRoofs(int var1) {
      if (var1 > 0) {
         this.roofCnt -= var1;
         if (this.roofCnt < 0) {
            if (IsoRegion.PRINT_D) {
               DebugLog.log("WARNING: MasterRegion.removeRoofs Roofcount managed to get below zero.");
            }

            this.roofCnt = 0;
         }

      }
   }

   protected void addChunkRegion(ChunkRegion var1) {
      if (!this.chunkRegions.contains(var1)) {
         this.squareSize += var1.getSquareSize();
         this.roofCnt += var1.getRoofCnt();
         this.isDirtyEnclosed = true;
         this.chunkRegions.add(var1);
         var1.setMasterRegion(this);
      }

   }

   protected void removeChunkRegion(ChunkRegion var1) {
      if (this.chunkRegions.remove(var1)) {
         this.squareSize -= var1.getSquareSize();
         this.roofCnt -= var1.getRoofCnt();
         this.isDirtyEnclosed = true;
         var1.setMasterRegion((MasterRegion)null);
      }

   }

   protected boolean containsChunkRegion(ChunkRegion var1) {
      return this.chunkRegions.contains(var1);
   }

   protected ArrayList swapChunkRegions(ArrayList var1) {
      ArrayList var2 = this.chunkRegions;
      this.chunkRegions = var1;
      return var2;
   }

   protected void resetSquareSize() {
      this.squareSize = 0;
   }

   protected void setDirtyEnclosed() {
      this.isDirtyEnclosed = true;
   }

   public boolean isEnclosed() {
      if (this.isDirtyEnclosed) {
         this.recalcEnclosed();
      }

      return this.enclosed;
   }

   private void recalcEnclosed() {
      this.isDirtyEnclosed = false;
      this.enclosed = true;
      Iterator var1 = this.chunkRegions.iterator();

      while(var1.hasNext()) {
         ChunkRegion var2 = (ChunkRegion)var1.next();
         if (!var2.getIsEnclosed()) {
            this.enclosed = false;
         }
      }

   }

   protected void merge(MasterRegion var1) {
      int var2;
      if (var1.chunkRegions.size() > 0) {
         for(var2 = var1.chunkRegions.size() - 1; var2 >= 0; --var2) {
            ChunkRegion var3 = (ChunkRegion)var1.chunkRegions.get(var2);
            var1.removeChunkRegion(var3);
            this.addChunkRegion(var3);
         }

         this.isDirtyEnclosed = true;
         var1.chunkRegions.clear();
      }

      if (var1.neighbors.size() > 0) {
         for(var2 = var1.neighbors.size() - 1; var2 >= 0; --var2) {
            MasterRegion var4 = (MasterRegion)var1.neighbors.get(var2);
            var4.removeNeighbor(var1);
            this.addNeighbor(var4);
         }

         var1.neighbors.clear();
      }

      release(var1);
   }

   public ArrayList getDebugChunkRegionCopy() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.chunkRegions.iterator();

      while(var2.hasNext()) {
         ChunkRegion var3 = (ChunkRegion)var2.next();
         var1.add(var3);
      }

      return var1;
   }
}
