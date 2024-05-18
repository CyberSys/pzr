package zombie.iso.areas.isoregion;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.debug.DebugLog;

public class ChunkRegion {
   private static int totalCreated = 0;
   private static int totalReleased = 0;
   private static int totalReused = 0;
   private static int nextID = 0;
   private static ArrayDeque idStack = new ArrayDeque();
   private static ArrayDeque pool = new ArrayDeque();
   private boolean isInPool = false;
   private Color color;
   private int ID;
   private int zLayer;
   private int squareSize = 0;
   private int roofCnt = 0;
   private int chunkBorderSquaresCnt = 0;
   private boolean[] enclosed = new boolean[4];
   private boolean enclosedCache = true;
   private ArrayList connectedNeighbors = new ArrayList();
   private ArrayList allNeighbors = new ArrayList();
   private boolean isDirtyEnclosed = false;
   private boolean isDirtyRoofed = false;
   private MasterRegion masterRegion;

   protected static void printStats() {
      DebugLog.log("ChunkRegion: Created: " + totalCreated + ", Re-used: " + totalReused + ", Released: " + totalReleased + ", InPool: " + pool.size() + ", nextID: " + nextID);
   }

   static ChunkRegion alloc(int var0) {
      ChunkRegion var1 = !pool.isEmpty() ? (ChunkRegion)pool.pop() : null;
      if (var1 == null) {
         var1 = new ChunkRegion();
         ++totalCreated;
      } else {
         ++totalReused;
      }

      var1.isInPool = false;
      var1.ID = idStack.isEmpty() ? nextID++ : (Integer)idStack.pop();
      var1.zLayer = var0;
      var1.resetChunkBorderSquaresCnt();
      if (var1.color == null) {
         var1.color = Colors.GetRandomColor();
      }

      var1.squareSize = 0;
      var1.roofCnt = 0;
      var1.resetEnclosed();
      var1.isDirtyRoofed = false;
      return var1;
   }

   static void release(ChunkRegion var0) {
      assert !pool.contains(var0);

      if (IsoRegion.PRINT_D && pool.contains(var0)) {
         DebugLog.log("Warning: ChunkRegion.release Trying to release a ChunkRegion twice.");
      } else {
         if (!var0.isInPool) {
            pool.push(var0.reset());
            ++totalReleased;
         }

      }
   }

   private ChunkRegion() {
   }

   private ChunkRegion reset() {
      this.isInPool = true;
      this.unlinkNeighbors();
      MasterRegion var1 = this.unlinkFromMaster();
      if (var1 != null && var1.size() <= 0) {
         MasterRegion.release(var1);
         if (IsoRegion.PRINT_D) {
            DebugLog.log("Warning: ChunkRegion.reset MasterRegion with 0 members.");
         }
      }

      this.resetChunkBorderSquaresCnt();
      if (this.ID >= 0) {
         idStack.push(this.ID);
      }

      this.ID = -1;
      this.squareSize = 0;
      this.roofCnt = 0;
      this.resetEnclosed();
      this.isDirtyRoofed = false;
      return this;
   }

   public int getID() {
      return this.ID;
   }

   public int getSquareSize() {
      return this.squareSize;
   }

   public Color getColor() {
      return this.color;
   }

   public int getzLayer() {
      return this.zLayer;
   }

   public MasterRegion getMasterRegion() {
      return this.masterRegion;
   }

   protected void setMasterRegion(MasterRegion var1) {
      this.masterRegion = var1;
   }

   protected MasterRegion unlinkFromMaster() {
      if (this.masterRegion != null) {
         MasterRegion var1 = this.masterRegion;
         this.masterRegion.removeChunkRegion(this);
         this.masterRegion = null;
         return var1;
      } else {
         return null;
      }
   }

   public int getRoofCnt() {
      return this.roofCnt > this.squareSize ? this.squareSize : this.roofCnt;
   }

   protected void addRoof() {
      ++this.roofCnt;
      if (this.roofCnt > this.squareSize) {
         this.roofCnt = this.squareSize;
      }

      if (this.masterRegion != null) {
         this.masterRegion.addRoof();
      }

   }

   protected void resetRoofCnt() {
      if (this.masterRegion != null) {
         this.masterRegion.removeRoofs(this.roofCnt);
      }

      this.roofCnt = 0;
   }

   protected void addSquareCount() {
      ++this.squareSize;
   }

   public int getChunkBorderSquaresCnt() {
      return this.chunkBorderSquaresCnt;
   }

   protected void addChunkBorderSquaresCnt() {
      ++this.chunkBorderSquaresCnt;
   }

   protected void removeChunkBorderSquaresCnt() {
      --this.chunkBorderSquaresCnt;
      if (this.chunkBorderSquaresCnt < 0) {
         this.chunkBorderSquaresCnt = 0;
      }

   }

   protected void resetChunkBorderSquaresCnt() {
      this.chunkBorderSquaresCnt = 0;
   }

   private void resetEnclosed() {
      for(byte var1 = 0; var1 < 4; ++var1) {
         this.enclosed[var1] = true;
      }

      this.isDirtyEnclosed = false;
      this.enclosedCache = true;
   }

   protected void setEnclosed(byte var1, boolean var2) {
      this.isDirtyEnclosed = true;
      this.enclosed[var1] = var2;
   }

   protected void setDirtyEnclosed() {
      this.isDirtyEnclosed = true;
      if (this.masterRegion != null) {
         this.masterRegion.setDirtyEnclosed();
      }

   }

   public boolean getIsEnclosed() {
      if (!this.isDirtyEnclosed) {
         return this.enclosedCache;
      } else {
         this.isDirtyEnclosed = false;
         this.enclosedCache = true;

         for(byte var1 = 0; var1 < 4; ++var1) {
            if (!this.enclosed[var1]) {
               this.enclosedCache = false;
            }
         }

         if (this.masterRegion != null) {
            this.masterRegion.setDirtyEnclosed();
         }

         return this.enclosedCache;
      }
   }

   protected ArrayList getConnectedNeighbors() {
      return this.connectedNeighbors;
   }

   protected void addConnectedNeighbor(ChunkRegion var1) {
      if (var1 != null) {
         if (!this.connectedNeighbors.contains(var1)) {
            this.connectedNeighbors.add(var1);
         }

      }
   }

   protected void removeConnectedNeighbor(ChunkRegion var1) {
      this.connectedNeighbors.remove(var1);
   }

   protected ArrayList getAllNeighbors() {
      return this.allNeighbors;
   }

   protected void addNeighbor(ChunkRegion var1) {
      if (var1 != null) {
         if (!this.allNeighbors.contains(var1)) {
            this.allNeighbors.add(var1);
         }

      }
   }

   protected void removeNeighbor(ChunkRegion var1) {
      this.allNeighbors.remove(var1);
   }

   protected void unlinkNeighbors() {
      Iterator var1 = this.connectedNeighbors.iterator();

      ChunkRegion var2;
      while(var1.hasNext()) {
         var2 = (ChunkRegion)var1.next();
         var2.removeConnectedNeighbor(this);
      }

      this.connectedNeighbors.clear();
      var1 = this.allNeighbors.iterator();

      while(var1.hasNext()) {
         var2 = (ChunkRegion)var1.next();
         var2.removeNeighbor(this);
      }

      this.allNeighbors.clear();
   }

   public ArrayList getDebugConnectedNeighborCopy() {
      ArrayList var1 = new ArrayList();
      if (this.connectedNeighbors.size() == 0) {
         return var1;
      } else {
         Iterator var2 = this.connectedNeighbors.iterator();

         while(var2.hasNext()) {
            ChunkRegion var3 = (ChunkRegion)var2.next();
            var1.add(var3);
         }

         return var1;
      }
   }

   public boolean containsConnectedNeighbor(ChunkRegion var1) {
      return this.connectedNeighbors.contains(var1);
   }

   public boolean containsConnectedNeighborID(int var1) {
      if (this.connectedNeighbors.size() == 0) {
         return false;
      } else {
         Iterator var2 = this.connectedNeighbors.iterator();

         ChunkRegion var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (ChunkRegion)var2.next();
         } while(var3.getID() != var1);

         return true;
      }
   }

   protected ChunkRegion getConnectedNeighborWithLargestMaster() {
      if (this.connectedNeighbors.size() == 0) {
         return null;
      } else {
         MasterRegion var1 = null;
         ChunkRegion var2 = null;
         Iterator var3 = this.connectedNeighbors.iterator();

         while(true) {
            ChunkRegion var4;
            MasterRegion var5;
            do {
               do {
                  if (!var3.hasNext()) {
                     return var2;
                  }

                  var4 = (ChunkRegion)var3.next();
                  var5 = var4.getMasterRegion();
               } while(var5 == null);
            } while(var1 != null && var5.getSquareSize() <= var1.getSquareSize());

            var1 = var5;
            var2 = var4;
         }
      }
   }

   protected ChunkRegion getFirstNeighborWithMaster() {
      if (this.connectedNeighbors.size() == 0) {
         return null;
      } else {
         Iterator var1 = this.connectedNeighbors.iterator();

         ChunkRegion var2;
         MasterRegion var3;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            var2 = (ChunkRegion)var1.next();
            var3 = var2.getMasterRegion();
         } while(var3 == null);

         return var2;
      }
   }
}
