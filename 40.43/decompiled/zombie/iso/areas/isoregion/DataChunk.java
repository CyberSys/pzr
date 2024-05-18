package zombie.iso.areas.isoregion;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;

public class DataChunk {
   private static final ArrayList tmpSquares = new ArrayList();
   private static final ArrayList tmpSquaresDone = new ArrayList();
   private DataCell cell;
   private int hashId;
   private int chunkX;
   private int chunkY;
   protected int highestZ = 0;
   protected long lastUpdateStamp = 0L;
   private final boolean[] activeZLayers = new boolean[8];
   private final boolean[] dirtyZLayers = new boolean[8];
   private byte[] squareFlags;
   private byte[] regionIDs;
   private final ArrayList chunkRegions = new ArrayList(8);
   private int squareArraySize;
   private static byte selectedFlags;
   private int tmpx;
   private int tmpy;
   private static ArrayList tmpMasters = new ArrayList();
   private static ArrayList tmpChunks = new ArrayList();
   private static ArrayList oldList = new ArrayList();

   protected DataChunk(DataChunk var1) {
      this.hashId = var1.hashId;
      this.chunkX = var1.chunkX;
      this.chunkY = var1.chunkY;

      int var2;
      for(var2 = 0; var2 < var1.activeZLayers.length; ++var2) {
         this.activeZLayers[var2] = var1.activeZLayers[var2];
      }

      for(var2 = 0; var2 < var1.dirtyZLayers.length; ++var2) {
         this.dirtyZLayers[var2] = var1.dirtyZLayers[var2];
      }

      for(var2 = 0; var2 < 8; ++var2) {
         this.chunkRegions.add(new ArrayList());
      }

   }

   protected DataChunk(int var1, int var2, int var3) {
      this.hashId = var3 < 0 ? IsoRegion.hash(var1, var2) : var3;
      this.chunkX = var1;
      this.chunkY = var2;

      for(int var4 = 0; var4 < 8; ++var4) {
         this.chunkRegions.add(new ArrayList());
      }

   }

   protected DataChunk(int var1, int var2, DataCell var3, int var4) {
      this.cell = var3;
      this.hashId = var4 < 0 ? IsoRegion.hash(var1, var2) : var4;
      this.chunkX = var1;
      this.chunkY = var2;

      for(int var5 = 0; var5 < 8; ++var5) {
         this.chunkRegions.add(new ArrayList());
      }

   }

   protected DataChunk(int var1, int var2, DataCell var3, int var4, int var5) {
      this.cell = var3;
      this.hashId = var4 < 0 ? IsoRegion.hash(var1, var2) : var4;
      this.chunkX = var1;
      this.chunkY = var2;

      for(int var6 = 0; var6 < 8; ++var6) {
         this.chunkRegions.add(new ArrayList());
      }

      this.ensureSquares(var5);
   }

   protected int getHashId() {
      return this.hashId;
   }

   protected int getChunkX() {
      return this.chunkX;
   }

   protected int getChunkY() {
      return this.chunkY;
   }

   protected ArrayList getChunkRegions(int var1) {
      return (ArrayList)this.chunkRegions.get(var1);
   }

   protected boolean isDirty(int var1) {
      return this.activeZLayers[var1] ? this.dirtyZLayers[var1] : false;
   }

   protected void setDirty(int var1) {
      if (this.activeZLayers[var1]) {
         this.dirtyZLayers[var1] = true;
         IsoRegionWorker.EnqueueDirtyChunk(this);
      }

   }

   protected void setDirtyAllActive() {
      boolean var1 = false;

      for(int var2 = 0; var2 < 8; ++var2) {
         if (this.activeZLayers[var2]) {
            this.dirtyZLayers[var2] = true;
            if (!var1) {
               IsoRegionWorker.EnqueueDirtyChunk(this);
               var1 = true;
            }
         }
      }

   }

   protected void unsetDirtyAndFloodAll() {
      for(int var1 = 0; var1 < 8; ++var1) {
         this.dirtyZLayers[var1] = false;
      }

   }

   private boolean validCoords(int var1, int var2, int var3) {
      return var1 >= 0 && var1 < 10 && var2 >= 0 && var2 < 10 && var3 >= 0 && var3 < this.highestZ + 1;
   }

   private int getCoord1D(int var1, int var2, int var3) {
      return var3 * 10 * 10 + var2 * 10 + var1;
   }

   public byte getSquare(int var1, int var2, int var3) {
      return this.getSquare(var1, var2, var3, false);
   }

   public byte getSquare(int var1, int var2, int var3, boolean var4) {
      if (this.squareFlags != null && (var4 || this.validCoords(var1, var2, var3))) {
         return this.activeZLayers[var3] ? this.squareFlags[this.getCoord1D(var1, var2, var3)] : -1;
      } else {
         return -1;
      }
   }

   protected byte setOrAddSquare(int var1, int var2, int var3, byte var4) {
      return this.setOrAddSquare(var1, var2, var3, var4, false);
   }

   protected byte setOrAddSquare(int var1, int var2, int var3, byte var4, boolean var5) {
      if (!var5 && !this.validCoords(var1, var2, var3)) {
         return -1;
      } else {
         this.ensureSquares(var3);
         int var6 = this.getCoord1D(var1, var2, var3);
         this.squareFlags[var6] = var4;
         return var4;
      }
   }

   private void ensureSquares(int var1) {
      if (var1 >= 0 && var1 < 8) {
         if (!this.activeZLayers[var1]) {
            this.ensureSquareArray(var1);
            this.activeZLayers[var1] = true;
            if (var1 > this.highestZ) {
               this.highestZ = var1;
            }

            for(int var3 = 0; var3 < 10; ++var3) {
               for(int var4 = 0; var4 < 10; ++var4) {
                  int var2 = this.getCoord1D(var4, var3, var1);
                  this.squareFlags[var2] = (byte)(var1 == 0 ? 16 : 0);
               }
            }
         }

      }
   }

   private void ensureSquareArray(int var1) {
      this.squareArraySize = (var1 + 1) * 10 * 10;
      if (this.squareFlags == null || this.squareFlags.length < this.squareArraySize) {
         byte[] var2 = this.squareFlags;
         byte[] var3 = this.regionIDs;
         this.squareFlags = new byte[this.squareArraySize];
         this.regionIDs = new byte[this.squareArraySize];
         if (var2 != null) {
            for(int var4 = 0; var4 < var2.length; ++var4) {
               this.squareFlags[var4] = var2[var4];
               this.regionIDs[var4] = var3[var4];
            }
         }
      }

   }

   protected static void readChunkDataIntoBuffer(IsoChunk var0, ByteBuffer var1) {
      if (var0 != null) {
         int var4 = var1.position();
         var1.putInt(0);
         var1.putInt(var0.maxLevel);
         int var5 = (var0.maxLevel + 1) * 100;
         var1.putInt(var5);

         int var6;
         for(var6 = 0; var6 <= var0.maxLevel; ++var6) {
            for(int var7 = 0; var7 < var0.squares[0].length; ++var7) {
               IsoGridSquare var2 = var0.squares[var6][var7];
               byte var3 = IsoRegion.calculateSquareFlags(var2);
               var1.put(var3);
            }
         }

         var6 = var1.position();
         var1.position(var4);
         var1.putInt(var6 - var4);
         var1.position(var6);
      } else {
         var1.putInt(-1);
      }

   }

   protected void save(ByteBuffer var1) {
      try {
         int var2 = var1.position();
         var1.putInt(0);
         var1.putInt(this.highestZ);
         int var3 = (this.highestZ + 1) * 100;
         var1.putInt(var3);

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            var1.put(this.squareFlags[var4]);
         }

         var4 = var1.position();
         var1.position(var2);
         var1.putInt(var4 - var2);
         var1.position(var4);
      } catch (Exception var5) {
         DebugLog.log(var5.getMessage());
         var5.printStackTrace();
      }

   }

   protected void load(ByteBuffer var1, int var2, boolean var3) {
      try {
         if (var3) {
            var1.getInt();
         }

         this.highestZ = var1.getInt();

         int var4;
         for(var4 = this.highestZ; var4 >= 0; --var4) {
            this.ensureSquares(var4);
         }

         var4 = var1.getInt();

         for(int var5 = 0; var5 < var4; ++var5) {
            this.squareFlags[var5] = var1.get();
         }
      } catch (Exception var6) {
         DebugLog.log(var6.getMessage());
         var6.printStackTrace();
      }

   }

   public void setSelectedFlags(int var1, int var2, int var3) {
      if (var3 >= 0 && var3 <= this.highestZ) {
         selectedFlags = this.squareFlags[this.getCoord1D(var1, var2, var3)];
      } else {
         selectedFlags = -1;
      }

   }

   public boolean selectedHasFlags(byte var1) {
      return (selectedFlags & var1) == var1;
   }

   protected boolean squareHasFlags(int var1, int var2, int var3, byte var4) {
      byte var5 = this.squareFlags[this.getCoord1D(var1, var2, var3)];
      return (var5 & var4) == var4;
   }

   public int squareGetFlags(int var1, int var2, int var3) {
      return this.squareFlags[this.getCoord1D(var1, var2, var3)];
   }

   protected void squareAddFlags(int var1, int var2, int var3, byte var4) {
      byte[] var10000 = this.squareFlags;
      int var10001 = this.getCoord1D(var1, var2, var3);
      var10000[var10001] |= var4;
   }

   protected void squareRemoveFlags(int var1, int var2, int var3, byte var4) {
      byte[] var10000 = this.squareFlags;
      int var10001 = this.getCoord1D(var1, var2, var3);
      var10000[var10001] ^= var4;
   }

   protected boolean squareCanConnect(int var1, int var2, int var3, byte var4) {
      if (var3 >= 0 && var3 < this.highestZ + 1) {
         if (var4 == 0) {
            return !this.squareHasFlags(var1, var2, var3, (byte)1);
         }

         if (var4 == 1) {
            return !this.squareHasFlags(var1, var2, var3, (byte)2);
         }

         if (var4 == 2) {
            return true;
         }

         if (var4 == 3) {
            return true;
         }

         if (var4 == 4) {
            return !this.squareHasFlags(var1, var2, var3, (byte)64);
         }

         if (var4 == 5) {
            return !this.squareHasFlags(var1, var2, var3, (byte)16);
         }
      }

      return false;
   }

   public ChunkRegion getRegion(int var1, int var2, int var3) {
      if (var3 >= 0 && var3 < this.highestZ + 1) {
         byte var4 = this.regionIDs[this.getCoord1D(var1, var2, var3)];
         if (var4 >= 0) {
            return (ChunkRegion)((ArrayList)this.chunkRegions.get(var3)).get(var4);
         }
      }

      return null;
   }

   public void setRegion(int var1, int var2, int var3, byte var4) {
      this.regionIDs[this.getCoord1D(var1, var2, var3)] = var4;
   }

   protected void recalculate() {
      for(int var1 = 0; var1 <= this.highestZ; ++var1) {
         if (this.dirtyZLayers[var1] && this.activeZLayers[var1]) {
            this.recalculate(var1);
         }
      }

   }

   private void recalculate(int var1) {
      ArrayList var2 = (ArrayList)this.chunkRegions.get(var1);

      int var3;
      for(var3 = var2.size() - 1; var3 >= 0; --var3) {
         ChunkRegion var4 = (ChunkRegion)var2.get(var3);
         MasterRegion var5 = var4.unlinkFromMaster();
         if (var5 != null && var5.size() <= 0) {
            MasterRegion.release(var5);
         }

         ChunkRegion.release(var4);
         var2.remove(var3);
      }

      var2.clear();

      int var6;
      for(var3 = 0; var3 < 10; ++var3) {
         for(var6 = 0; var6 < 10; ++var6) {
            this.regionIDs[this.getCoord1D(var6, var3, var1)] = -1;
         }
      }

      for(var3 = 0; var3 < 10; ++var3) {
         for(var6 = 0; var6 < 10; ++var6) {
            if (this.regionIDs[this.getCoord1D(var6, var3, var1)] == -1) {
               this.floodFill(var6, var3, var1);
            }
         }
      }

   }

   private ChunkRegion floodFill(int var1, int var2, int var3) {
      ChunkRegion var4 = ChunkRegion.alloc(var3);
      byte var5 = (byte)((ArrayList)this.chunkRegions.get(var3)).size();
      ((ArrayList)this.chunkRegions.get(var3)).add(var4);
      tmpSquares.clear();
      tmpSquaresDone.clear();
      tmpSquares.add(DataSquarePos.alloc(var1, var2, var3));

      while(true) {
         DataSquarePos var7;
         do {
            if (tmpSquares.size() <= 0) {
               Iterator var10 = tmpSquaresDone.iterator();

               while(var10.hasNext()) {
                  DataSquarePos var11 = (DataSquarePos)var10.next();
                  DataSquarePos.release(var11);
               }

               tmpSquares.clear();
               tmpSquaresDone.clear();
               return var4;
            }

            var7 = (DataSquarePos)tmpSquares.remove(0);
            tmpSquaresDone.add(var7);
         } while(this.getRegion(var7.x, var7.y, var7.z) != null);

         this.setRegion(var7.x, var7.y, var7.z, var5);
         var4.addSquareCount();

         for(byte var8 = 0; var8 < 4; ++var8) {
            DataSquarePos var6 = this.getNeighbor(var7, var8);
            if (var6 == null) {
               if (this.squareCanConnect(var7.x, var7.y, var7.z, var8)) {
                  var4.addChunkBorderSquaresCnt();
               }
            } else {
               if (this.squareCanConnect(var7.x, var7.y, var7.z, var8) && this.squareCanConnect(var6.x, var6.y, var6.z, IsoRegion.GetOppositeDir(var8))) {
                  if (this.getRegion(var6.x, var6.y, var6.z) == null) {
                     if (this.isExploredPos(var6.x, var6.y, var6.z)) {
                        DataSquarePos.release(var6);
                     } else {
                        tmpSquares.add(var6);
                     }
                     continue;
                  }
               } else {
                  ChunkRegion var9 = this.getRegion(var6.x, var6.y, var6.z);
                  if (var9 != null && var9 != var4) {
                     if (this.isExploredPos(var6.x, var6.y, var6.z)) {
                        DataSquarePos.release(var6);
                     } else {
                        var4.addNeighbor(var9);
                        var9.addNeighbor(var4);
                        tmpSquaresDone.add(var6);
                     }
                     continue;
                  }
               }

               DataSquarePos.release(var6);
            }
         }
      }
   }

   private boolean isExploredPos(int var1, int var2, int var3) {
      Iterator var4 = tmpSquares.iterator();

      DataSquarePos var5;
      do {
         if (!var4.hasNext()) {
            var4 = tmpSquaresDone.iterator();

            do {
               if (!var4.hasNext()) {
                  return false;
               }

               var5 = (DataSquarePos)var4.next();
            } while(var5.x != var1 || var5.y != var2 || var5.z != var3);

            return true;
         }

         var5 = (DataSquarePos)var4.next();
      } while(var5.x != var1 || var5.y != var2 || var5.z != var3);

      return true;
   }

   private DataSquarePos getNeighbor(DataSquarePos var1, byte var2) {
      if (var2 == 1) {
         this.tmpx = var1.x - 1;
      } else if (var2 == 3) {
         this.tmpx = var1.x + 1;
      } else {
         this.tmpx = var1.x;
      }

      if (var2 == 0) {
         this.tmpy = var1.y - 1;
      } else if (var2 == 2) {
         this.tmpy = var1.y + 1;
      } else {
         this.tmpy = var1.y;
      }

      if (this.tmpx >= 0 && this.tmpx < 10 && this.tmpy >= 0 && this.tmpy < 10) {
         DataSquarePos var3 = DataSquarePos.alloc(this.tmpx, this.tmpy, var1.z);
         return var3;
      } else {
         return null;
      }
   }

   protected void link(DataChunk var1, DataChunk var2, DataChunk var3, DataChunk var4) {
      for(int var5 = 0; var5 <= this.highestZ; ++var5) {
         if (this.dirtyZLayers[var5] && this.activeZLayers[var5]) {
            this.linkRegionsOnSide(var5, var1, (byte)0);
            this.linkRegionsOnSide(var5, var2, (byte)1);
            this.linkRegionsOnSide(var5, var3, (byte)2);
            this.linkRegionsOnSide(var5, var4, (byte)3);
         }
      }

   }

   private void linkRegionsOnSide(int var1, DataChunk var2, byte var3) {
      int var4;
      int var5;
      int var6;
      int var7;
      if (var3 != 0 && var3 != 2) {
         var4 = var3 == 1 ? 0 : 9;
         var5 = var4 + 1;
         var6 = 0;
         var7 = 10;
      } else {
         var4 = 0;
         var5 = 10;
         var6 = var3 == 0 ? 0 : 9;
         var7 = var6 + 1;
      }

      this.resetEnclosedSide(var1, var3);
      if (var2 != null) {
         var2.resetEnclosedSide(var1, IsoRegion.GetOppositeDir(var3));
      }

      for(int var12 = var6; var12 < var7; ++var12) {
         for(int var13 = var4; var13 < var5; ++var13) {
            int var8;
            int var9;
            if (var3 != 0 && var3 != 2) {
               var8 = var3 == 1 ? 9 : 0;
               var9 = var12;
            } else {
               var8 = var13;
               var9 = var3 == 0 ? 9 : 0;
            }

            ChunkRegion var10 = this.getRegion(var13, var12, var1);
            ChunkRegion var11 = var2 != null ? var2.getRegion(var8, var9, var1) : null;
            if (var10 == null) {
               if (IsoRegion.PRINT_D) {
                  DebugLog.log("WARNING: ds.getRegion()==null, shouldnt happen at this point.");
               }
            } else if (var2 != null && var11 != null) {
               if (this.squareCanConnect(var13, var12, var1, var3) && var2.squareCanConnect(var8, var9, var1, IsoRegion.GetOppositeDir(var3))) {
                  var10.addConnectedNeighbor(var11);
                  var11.addConnectedNeighbor(var10);
                  var10.addNeighbor(var11);
                  var11.addNeighbor(var10);
               } else {
                  var10.addNeighbor(var11);
                  var11.addNeighbor(var10);
               }
            } else if (this.squareCanConnect(var13, var12, var1, var3)) {
               var10.setEnclosed(var3, false);
            }
         }
      }

   }

   private void resetEnclosedSide(int var1, byte var2) {
      Iterator var3 = ((ArrayList)this.chunkRegions.get(var1)).iterator();

      while(var3.hasNext()) {
         ChunkRegion var4 = (ChunkRegion)var3.next();
         if (var4.getzLayer() == var1) {
            var4.setEnclosed(var2, true);
         }
      }

   }

   protected void interConnect() {
      label74:
      for(int var1 = 0; var1 <= this.highestZ; ++var1) {
         if (this.dirtyZLayers[var1] && this.activeZLayers[var1]) {
            Iterator var2 = ((ArrayList)this.chunkRegions.get(var1)).iterator();

            while(true) {
               while(true) {
                  while(true) {
                     ChunkRegion var3;
                     do {
                        do {
                           if (!var2.hasNext()) {
                              continue label74;
                           }

                           var3 = (ChunkRegion)var2.next();
                        } while(var3.getzLayer() != var1);
                     } while(var3.getMasterRegion() != null);

                     if (var3.getConnectedNeighbors() != null && var3.getConnectedNeighbors().size() != 0) {
                        ChunkRegion var10 = var3.getConnectedNeighborWithLargestMaster();
                        MasterRegion var5;
                        if (var10 != null) {
                           var5 = var10.getMasterRegion();
                           oldList.clear();
                           tmpMasters.clear();
                           oldList = var5.swapChunkRegions(oldList);
                           Iterator var6 = oldList.iterator();

                           while(var6.hasNext()) {
                              ChunkRegion var7 = (ChunkRegion)var6.next();
                              var7.setMasterRegion((MasterRegion)null);
                           }

                           MasterRegion.release(var5);
                           MasterRegion var11 = MasterRegion.alloc();
                           IsoRegionWorker.EnqueueDirtyMasterRegion(var11);
                           this.floodFillExpandMaster(var3, var11);
                           tmpMasters.add(var11);
                           Iterator var8 = oldList.iterator();

                           while(var8.hasNext()) {
                              ChunkRegion var9 = (ChunkRegion)var8.next();
                              if (var9.getMasterRegion() == null) {
                                 MasterRegion var12 = MasterRegion.alloc();
                                 IsoRegionWorker.EnqueueDirtyMasterRegion(var12);
                                 this.floodFillExpandMaster(var9, var12);
                                 tmpMasters.add(var12);
                              }
                           }

                           ++DataRoot.floodFills;
                        } else {
                           var5 = MasterRegion.alloc();
                           IsoRegionWorker.EnqueueDirtyMasterRegion(var5);
                           this.floodFillExpandMaster(var3, var5);
                           ++DataRoot.floodFills;
                        }
                     } else {
                        MasterRegion var4 = MasterRegion.alloc();
                        IsoRegionWorker.EnqueueDirtyMasterRegion(var4);
                        var4.addChunkRegion(var3);
                     }
                  }
               }
            }
         }
      }

   }

   private void floodFillExpandMaster(ChunkRegion var1, MasterRegion var2) {
      tmpChunks.clear();
      tmpChunks.add(var1);

      while(true) {
         ChunkRegion var3;
         do {
            if (tmpChunks.size() <= 0) {
               return;
            }

            var3 = (ChunkRegion)tmpChunks.remove(0);
            var2.addChunkRegion(var3);
         } while(var3.getConnectedNeighbors() == null);

         Iterator var4 = var3.getConnectedNeighbors().iterator();

         while(var4.hasNext()) {
            ChunkRegion var5 = (ChunkRegion)var4.next();
            if (!tmpChunks.contains(var5) && !var2.containsChunkRegion(var5)) {
               if (var5.getMasterRegion() == null) {
                  tmpChunks.add(var5);
               } else if (var5.getMasterRegion() != var2) {
                  var2.merge(var5.getMasterRegion());
               }
            }
         }
      }
   }

   protected void recalcRoofs() {
      int var1;
      for(var1 = 0; var1 < this.chunkRegions.size(); ++var1) {
         for(int var2 = 0; var2 < ((ArrayList)this.chunkRegions.get(var1)).size(); ++var2) {
            ChunkRegion var3 = (ChunkRegion)((ArrayList)this.chunkRegions.get(var1)).get(var2);
            var3.resetRoofCnt();
         }
      }

      var1 = this.highestZ;

      for(int var5 = 0; var5 < 10; ++var5) {
         for(int var6 = 0; var6 < 10; ++var6) {
            byte var8 = this.getSquare(var6, var5, var1);
            boolean var9 = false;
            if (var8 > 0) {
               var9 = this.squareHasFlags(var6, var5, var1, (byte)16);
            }

            if (var1 >= 1) {
               for(int var7 = var1 - 1; var7 >= 0; --var7) {
                  var8 = this.getSquare(var6, var5, var7);
                  if (var8 <= 0) {
                     var9 = false;
                  } else {
                     var9 = var9 || this.squareHasFlags(var6, var5, var7, (byte)32);
                     if (var9) {
                        ChunkRegion var4 = this.getRegion(var6, var5, var7);
                        if (var4 != null) {
                           var4.addRoof();
                           if (var4.getMasterRegion() != null && !var4.getMasterRegion().isEnclosed()) {
                              var9 = false;
                           }
                        } else {
                           var9 = false;
                        }
                     }

                     if (!var9) {
                        var9 = this.squareHasFlags(var6, var5, var7, (byte)16);
                     }
                  }
               }
            }
         }
      }

   }
}
