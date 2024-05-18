package zombie.iso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import zombie.GameTime;
import zombie.TileAccessibilityWorker;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.bucket.BucketManager;
import zombie.core.physics.WorldSimulation;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.iso.areas.IsoRoom;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.TextManager;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleCache;
import zombie.vehicles.VehicleManager;

public class IsoChunkMap {
   public static int ChunkDiv = 10;
   public static final int ChunksPerWidth = 10;
   public static int StartChunkGridWidth = 13;
   public static int ChunkGridWidth;
   public static int MPWorldXA;
   public static int MPWorldYA;
   public static int MPWorldZA;
   public static int ChunkWidthInTiles;
   public int PlayerID = 0;
   public boolean ignore = false;
   static int WorldCellX;
   static int WorldCellY;
   static int PosX;
   static int PosY;
   public static int WorldXA;
   public static int WorldYA;
   public static int WorldZA;
   public static int[] SWorldX;
   public int WorldX;
   public int WorldY;
   public static int[] SWorldY;
   IsoCell cell;
   boolean bReadBufferA;
   protected IsoChunk[] chunksSwapB;
   protected IsoChunk[] chunksSwapA;
   private UpdateLimit checkVehiclesFrequency;
   public static ConcurrentLinkedQueue chunkStore;
   public static ReentrantLock bSettingChunk;
   public static ReentrantLock bSettingChunkLighting;
   public static HashMap SharedChunks;
   int MovedInform;
   boolean bMovingPos;
   public ArrayList filenameServerRequests;
   int XMinTiles;
   int YMinTiles;
   int XMaxTiles;
   int YMaxTiles;
   static ArrayList saveList;
   protected static ColorInfo inf;
   private static ArrayList splatByType;

   public static void CalcChunkWidth() {
      float var0 = (float)Core.getInstance().getScreenWidth();
      float var1 = (float)Core.getInstance().getScreenHeight();
      float var2 = var0 / 1920.0F;
      if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      ChunkGridWidth = (int)((double)((float)StartChunkGridWidth * var2) * 1.5D);
      if (ChunkGridWidth / 2 * 2 == ChunkGridWidth) {
         ++ChunkGridWidth;
      }

      ChunkWidthInTiles = ChunkGridWidth * 10;
   }

   public IsoChunkMap(IsoCell var1) {
      this.WorldX = WorldXA / 10;
      this.WorldY = WorldYA / 10;
      this.bReadBufferA = true;
      this.checkVehiclesFrequency = new UpdateLimit(3000L);
      this.MovedInform = 0;
      this.bMovingPos = false;
      this.filenameServerRequests = new ArrayList();
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      this.cell = var1;
      WorldReuserThread.instance.finished = false;
      this.chunksSwapB = new IsoChunk[ChunkGridWidth * ChunkGridWidth];
      this.chunksSwapA = new IsoChunk[ChunkGridWidth * ChunkGridWidth];
   }

   public void Dispose() {
      WorldReuserThread.instance.finished = true;
      IsoChunk.loadGridSquare.clear();
      this.chunksSwapA = null;
      this.chunksSwapB = null;
   }

   public static void setWorldStartPos(int var0, int var1) {
      SWorldX[IsoPlayer.getPlayerIndex()] = var0 / 10;
      SWorldY[IsoPlayer.getPlayerIndex()] = var1 / 10;
   }

   public void setInitialPos(int var1, int var2) {
      this.WorldX = var1;
      this.WorldY = var2;
      this.XMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMinTiles = -1;
      this.YMaxTiles = -1;
   }

   public void processAllLoadGridSquare() {
      for(IsoChunk var1 = (IsoChunk)IsoChunk.loadGridSquare.poll(); var1 != null; var1 = (IsoChunk)IsoChunk.loadGridSquare.poll()) {
         bSettingChunk.lock();

         try {
            boolean var2 = false;

            for(int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
               IsoChunkMap var4 = IsoWorld.instance.CurrentCell.ChunkMap[var3];
               if (!var4.ignore && var4.setChunkDirect(var1, false)) {
                  var2 = true;
               }
            }

            if (!var2) {
               WorldReuserThread.instance.addReuseChunk(var1);
            } else {
               var1.doLoadGridsquare();
            }
         } finally {
            bSettingChunk.unlock();
         }
      }

   }

   public void update() {
      int var2 = IsoChunk.loadGridSquare.size();
      if (var2 != 0) {
         var2 = 1 + var2 * 3 / ChunkGridWidth;
      }

      while(true) {
         IsoChunk var1;
         int var4;
         while(var2 > 0) {
            var1 = null;
            var1 = (IsoChunk)IsoChunk.loadGridSquare.poll();
            if (var1 != null) {
               boolean var3 = false;

               for(var4 = 0; var4 < IsoPlayer.numPlayers; ++var4) {
                  IsoChunkMap var5 = IsoWorld.instance.CurrentCell.ChunkMap[var4];
                  if (!var5.ignore && var5.setChunkDirect(var1, false)) {
                     var3 = true;
                  }
               }

               if (!var3) {
                  WorldReuserThread.instance.addReuseChunk(var1);
                  --var2;
                  continue;
               }

               var1.bLoaded = true;
               if (bSettingChunk.isLocked()) {
               }

               bSettingChunk.lock();

               try {
                  var1.doLoadGridsquare();
                  if (GameClient.bClient) {
                     List var10 = VehicleCache.vehicleGet(var1.wx, var1.wy);
                     VehicleManager.instance.sendReqestGetFull(var10);
                  }
               } finally {
                  bSettingChunk.unlock();
               }

               for(var4 = 0; var4 < IsoPlayer.numPlayers; ++var4) {
                  IsoPlayer var11 = IsoPlayer.players[var4];
                  if (var11 != null) {
                     var11.dirtyRecalcGridStackTime = 20.0F;
                  }
               }
            }

            --var2;
         }

         for(int var9 = 0; var9 < ChunkGridWidth; ++var9) {
            for(var4 = 0; var4 < ChunkGridWidth; ++var4) {
               var1 = this.getChunk(var4, var9);
               if (var1 != null) {
                  var1.update();
               }
            }
         }

         if (GameClient.bClient && this.checkVehiclesFrequency.Check()) {
            this.checkVehicles();
         }

         return;
      }
   }

   public void checkVehicles() {
      for(int var1 = 0; var1 < ChunkGridWidth; ++var1) {
         for(int var2 = 0; var2 < ChunkGridWidth; ++var2) {
            IsoChunk var3 = this.getChunk(var2, var1);
            if (var3 != null && var3.bLoaded) {
               List var4 = VehicleCache.vehicleGet(var3.wx, var3.wy);
               if (var4 != null && var3.vehicles.size() != var4.size()) {
                  for(int var5 = 0; var5 < var4.size(); ++var5) {
                     short var6 = ((VehicleCache)var4.get(var5)).id;
                     boolean var7 = false;

                     for(int var8 = 0; var8 < var3.vehicles.size(); ++var8) {
                        if (((BaseVehicle)var3.vehicles.get(var8)).getId() == var6) {
                           var7 = true;
                           break;
                        }
                     }

                     if (!var7 && VehicleManager.instance.getVehicleByID(var6) == null) {
                        VehicleManager.instance.sendReqestGetFull(var6);
                     }
                  }
               }
            }
         }
      }

   }

   public void checkIntegrity() {
      IsoWorld.instance.CurrentCell.ChunkMap[0].XMinTiles = -1;

      for(int var1 = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles(); var1 < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMaxTiles(); ++var1) {
         for(int var2 = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMinTiles(); var2 < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMaxTiles(); ++var2) {
            IsoGridSquare var3 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, 0);
            if (var3 != null && (var3.getX() != var1 || var3.getY() != var2)) {
               int var4 = var1 / 10;
               int var5 = var2 / 10;
               var4 -= IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMin();
               var5 -= IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMin();
               IsoChunk var6 = null;
               var6 = new IsoChunk(IsoWorld.instance.CurrentCell);
               var6.refs.add(IsoWorld.instance.CurrentCell.ChunkMap[0]);
               WorldStreamer.instance.addJob(var6, var1 / 10, var2 / 10, false);

               while(!var6.bLoaded) {
                  try {
                     Thread.sleep(13L);
                  } catch (InterruptedException var8) {
                     var8.printStackTrace();
                  }
               }
            }
         }
      }

   }

   public void checkIntegrityThread() {
      IsoWorld.instance.CurrentCell.ChunkMap[0].XMinTiles = -1;

      for(int var1 = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles(); var1 < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMaxTiles(); ++var1) {
         for(int var2 = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMinTiles(); var2 < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMaxTiles(); ++var2) {
            IsoGridSquare var3 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, 0);
            if (var3 != null && (var3.getX() != var1 || var3.getY() != var2)) {
               int var4 = var1 / 10;
               int var5 = var2 / 10;
               var4 -= IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMin();
               var5 -= IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMin();
               IsoChunk var6 = new IsoChunk(IsoWorld.instance.CurrentCell);
               var6.refs.add(IsoWorld.instance.CurrentCell.ChunkMap[0]);
               WorldStreamer.instance.addJobInstant(var6, var1, var2, var1 / 10, var2 / 10);
            }

            if (var3 != null) {
            }
         }
      }

   }

   public void LoadChunk(int var1, int var2, int var3, int var4) {
      IsoChunk var5 = null;
      if (SharedChunks.containsKey((var1 << 16) + var2)) {
         var5 = (IsoChunk)SharedChunks.get((var1 << 16) + var2);
         var5.setCache();
         this.setChunk(var3, var4, var5);
         var5.refs.add(this);
      } else {
         var5 = (IsoChunk)chunkStore.poll();
         if (var5 == null) {
            var5 = new IsoChunk(this.cell);
         }

         SharedChunks.put((var1 << 16) + var2, var5);
         var5.refs.add(this);
         WorldStreamer.instance.addJob(var5, var1, var2, false);
      }

   }

   public void LoadChunkForLater(int var1, int var2, int var3, int var4) {
      if (IsoWorld.instance.getMetaGrid().isValidChunk(var1, var2)) {
         IsoChunk var5 = null;
         if (SharedChunks.containsKey((var1 << 16) + var2)) {
            var5 = (IsoChunk)SharedChunks.get((var1 << 16) + var2);
            if (!var5.refs.contains(this)) {
               var5.refs.add(this);
               var5.lightCheck[this.PlayerID] = true;
            }

            if (!var5.bLoaded) {
               return;
            }

            this.setChunk(var3, var4, var5);
         } else {
            var5 = (IsoChunk)chunkStore.poll();
            if (var5 == null) {
               var5 = new IsoChunk(this.cell);
            }

            SharedChunks.put((var1 << 16) + var2, var5);
            var5.refs.add(this);
            WorldStreamer.instance.addJob(var5, var1, var2, true);
         }

      }
   }

   public IsoChunk getChunkForGridSquare(int var1, int var2) {
      var1 -= (this.WorldX - ChunkGridWidth / 2) * 10;
      var2 -= (this.WorldY - ChunkGridWidth / 2) * 10;
      if (var1 >= 0 && var2 >= 0 && var1 < 300 && var2 < 300) {
         IsoChunk var3 = this.getChunk(var1 / 10, var2 / 10);
         return var3;
      } else {
         return null;
      }
   }

   public void setGridSquare(IsoGridSquare var1, int var2, int var3, int var4, int var5, int var6) {
      var4 -= var2 * 10;
      var5 -= var3 * 10;
      if (var4 >= 0 && var5 >= 0 && var4 < 300 && var5 < 300 && var6 >= 0 && var6 <= 16) {
         IsoChunk var7 = this.getChunk(var4 / 10, var5 / 10);
         if (var7 != null) {
            var7.setSquare(var4 % 10, var5 % 10, var6, var1);
         }
      }
   }

   public IsoChunk getChunkCurrent(int var1, int var2) {
      if (var1 >= 0 && var1 < ChunkGridWidth && var2 >= 0 && var2 < ChunkGridWidth) {
         return !this.bReadBufferA ? this.chunksSwapA[ChunkGridWidth * var2 + var1] : this.chunksSwapB[ChunkGridWidth * var2 + var1];
      } else {
         return null;
      }
   }

   public void setGridSquare(IsoGridSquare var1, int var2, int var3, int var4) {
      assert var1 == null || var1.x == var2 && var1.y == var3 && var1.z == var4;

      var2 -= (this.WorldX - ChunkGridWidth / 2) * 10;
      var3 -= (this.WorldY - ChunkGridWidth / 2) * 10;
      if (var2 >= 0 && var3 >= 0 && var2 < this.getWidthInTiles() && var3 < this.getWidthInTiles() && var4 >= 0 && var4 < 8) {
         IsoChunk var5 = this.getChunk(var2 / 10, var3 / 10);
         if (var5 != null) {
            if (var4 > var5.maxLevel) {
               var5.maxLevel = var4;
            }

            var5.setSquare(var2 % 10, var3 % 10, var4, var1);
         }
      }
   }

   public IsoGridSquare getGridSquare(int var1, int var2, int var3) {
      var1 -= (this.WorldX - ChunkGridWidth / 2) * 10;
      var2 -= (this.WorldY - ChunkGridWidth / 2) * 10;
      if (var1 >= 0 && var2 >= 0 && var1 < 300 && var2 < 300 && var3 >= 0 && var3 <= 8) {
         IsoChunk var4 = this.getChunk(var1 / 10, var2 / 10);
         return var4 == null ? null : var4.getGridSquare(var1 % 10, var2 % 10, var3);
      } else {
         return null;
      }
   }

   public IsoGridSquare getGridSquareDirect(int var1, int var2, int var3) {
      if (var1 >= 0 && var1 < this.getWidthInTiles() && var2 >= 0 && var2 < this.getWidthInTiles()) {
         IsoChunk var4 = this.getChunk(var1 / 10, var2 / 10);
         return var4 == null ? null : var4.getGridSquare(var1 % 10, var2 % 10, var3);
      } else {
         return null;
      }
   }

   public IsoChunk getChunk(int var1, int var2) {
      if (var1 >= 0 && var1 < ChunkGridWidth && var2 >= 0 && var2 < ChunkGridWidth) {
         return this.bReadBufferA ? this.chunksSwapA[ChunkGridWidth * var2 + var1] : this.chunksSwapB[ChunkGridWidth * var2 + var1];
      } else {
         return null;
      }
   }

   public void setChunk(int var1, int var2, IsoChunk var3) {
      if (!this.bReadBufferA) {
         this.chunksSwapA[ChunkGridWidth * var2 + var1] = var3;
      } else {
         this.chunksSwapB[ChunkGridWidth * var2 + var1] = var3;
      }

   }

   public boolean setChunkDirect(IsoChunk var1, boolean var2) {
      long var3 = System.nanoTime();
      if (var2) {
         bSettingChunk.lock();
         synchronized(LightingThread.instance.bHasLock) {
            if (LightingThread.instance.bHasLock) {
               LightingThread.instance.Interrupted = true;
               LightingThread.instance.lightingThread.interrupt();
            }

            bSettingChunkLighting.lock();
         }
      }

      long var5 = System.nanoTime();
      int var7 = var1.wx - this.WorldX;
      int var8 = var1.wy - this.WorldY;
      var7 += ChunkGridWidth / 2;
      var8 += ChunkGridWidth / 2;
      if (var1.jobType == IsoChunk.JobType.Convert) {
         var7 = 0;
         var8 = 0;
      }

      if (!var1.refs.isEmpty() && var7 >= 0 && var8 >= 0 && var7 < ChunkGridWidth && var8 < ChunkGridWidth) {
         try {
            if (this.bReadBufferA) {
               this.chunksSwapA[ChunkGridWidth * var8 + var7] = var1;
            } else {
               this.chunksSwapB[ChunkGridWidth * var8 + var7] = var1;
            }

            var1.bLoaded = true;
            if (var1.jobType == IsoChunk.JobType.None) {
               var1.setCache();
               var1.updateBuildings();
            }

            double var9 = (double)(System.nanoTime() - var5) / 1000000.0D;
            double var11 = (double)(System.nanoTime() - var3) / 1000000.0D;
            if (LightingThread.DebugLockTime && var11 > 10.0D) {
               DebugLog.log("setChunkDirect time " + var9 + "/" + var11 + " ms");
            }
         } finally {
            if (var2) {
               bSettingChunkLighting.unlock();
               bSettingChunk.unlock();
            }

         }

         return true;
      } else {
         if (var1.refs.contains(this)) {
            var1.refs.remove(this);
            if (var1.refs.isEmpty()) {
               SharedChunks.remove((var1.wx << 16) + var1.wy);
            }
         }

         if (var2) {
            bSettingChunkLighting.unlock();
            bSettingChunk.unlock();
         }

         return false;
      }
   }

   public void drawDebugChunkMap() {
      int var1 = 64;
      boolean var2 = false;

      for(int var3 = 0; var3 < ChunkGridWidth; ++var3) {
         int var7 = 0;

         for(int var4 = 0; var4 < ChunkGridWidth; ++var4) {
            var7 += 64;
            IsoChunk var5 = this.getChunk(var3, var4);
            if (var5 != null) {
               IsoGridSquare var6 = var5.getGridSquare(0, 0, 0);
               if (var6 == null) {
                  TextManager.instance.DrawString((double)var1, (double)var7, "wx:" + var5.wx + " wy:" + var5.wy);
               }
            }
         }

         var1 += 128;
      }

   }

   public void LoadLeft() {
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      TileAccessibilityWorker.instance.startingNew = true;
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      this.Left();
      WorldSimulation.instance.scrollGroundLeft(this.PlayerID);
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;

      for(int var1 = -(ChunkGridWidth / 2); var1 <= ChunkGridWidth / 2; ++var1) {
         this.LoadChunkForLater(this.WorldX - ChunkGridWidth / 2, this.WorldY + var1, 0, var1 + ChunkGridWidth / 2);
      }

      this.SwapChunkBuffers();
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      this.UpdateCellCache();
      LightingThread.instance.bMovedMap = true;
      LightingThread.instance.scrollLeft(this.PlayerID);
   }

   public void SwapChunkBuffers() {
      for(int var1 = 0; var1 < ChunkGridWidth * ChunkGridWidth; ++var1) {
         if (this.bReadBufferA) {
            this.chunksSwapA[var1] = null;
         } else {
            this.chunksSwapB[var1] = null;
         }
      }

      this.XMinTiles = this.XMaxTiles = -1;
      this.YMinTiles = this.YMaxTiles = -1;
      this.bReadBufferA = !this.bReadBufferA;
   }

   private void setChunk(int var1, IsoChunk var2) {
      if (!this.bReadBufferA) {
         this.chunksSwapA[var1] = var2;
      } else {
         this.chunksSwapB[var1] = var2;
      }

   }

   private IsoChunk getChunk(int var1) {
      return this.bReadBufferA ? this.chunksSwapA[var1] : this.chunksSwapB[var1];
   }

   public void LoadRight() {
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      TileAccessibilityWorker.instance.startingNew = true;
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      this.Right();
      WorldSimulation.instance.scrollGroundRight(this.PlayerID);
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;

      for(int var1 = -(ChunkGridWidth / 2); var1 <= ChunkGridWidth / 2; ++var1) {
         this.LoadChunkForLater(this.WorldX + ChunkGridWidth / 2, this.WorldY + var1, ChunkGridWidth - 1, var1 + ChunkGridWidth / 2);
      }

      this.SwapChunkBuffers();
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      this.UpdateCellCache();
      LightingThread.instance.bMovedMap = true;
      LightingThread.instance.scrollRight(this.PlayerID);
   }

   public void LoadUp() {
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      TileAccessibilityWorker.instance.startingNew = true;
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      this.Up();
      WorldSimulation.instance.scrollGroundUp(this.PlayerID);
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;

      for(int var1 = -(ChunkGridWidth / 2); var1 <= ChunkGridWidth / 2; ++var1) {
         this.LoadChunkForLater(this.WorldX + var1, this.WorldY - ChunkGridWidth / 2, var1 + ChunkGridWidth / 2, 0);
      }

      this.SwapChunkBuffers();
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      this.UpdateCellCache();
      LightingThread.instance.bMovedMap = true;
      LightingThread.instance.scrollUp(this.PlayerID);
   }

   public void LoadDown() {
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      TileAccessibilityWorker.instance.startingNew = true;
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      this.Down();
      WorldSimulation.instance.scrollGroundDown(this.PlayerID);
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;

      for(int var1 = -(ChunkGridWidth / 2); var1 <= ChunkGridWidth / 2; ++var1) {
         this.LoadChunkForLater(this.WorldX + var1, this.WorldY + ChunkGridWidth / 2, var1 + ChunkGridWidth / 2, ChunkGridWidth - 1);
      }

      this.SwapChunkBuffers();
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;
      this.UpdateCellCache();
      LightingThread.instance.bMovedMap = true;
      LightingThread.instance.scrollDown(this.PlayerID);
   }

   public void UpdateCellCache() {
      if (IsoCell.ENABLE_SQUARE_CACHE) {
         int var1 = this.getWidthInTiles();

         for(int var2 = 0; var2 < var1; ++var2) {
            for(int var3 = 0; var3 < var1; ++var3) {
               for(int var4 = 0; var4 < 8; ++var4) {
                  IsoGridSquare var5 = this.getGridSquare(var2 + this.getWorldXMinTiles(), var3 + this.getWorldYMinTiles(), var4);
                  IsoWorld.instance.CurrentCell.setCacheGridSquareLocal(var2, var3, var4, var5, this.PlayerID);
               }
            }
         }

      }
   }

   void Up() {
      for(int var1 = 0; var1 < ChunkGridWidth; ++var1) {
         for(int var2 = ChunkGridWidth - 1; var2 > 0; --var2) {
            IsoChunk var3 = this.getChunk(var1, var2);
            if (var3 == null && var2 == ChunkGridWidth - 1) {
               int var4 = this.WorldX - ChunkGridWidth / 2 + var1;
               int var5 = this.WorldY - ChunkGridWidth / 2 + var2;
               var3 = (IsoChunk)SharedChunks.get((var4 << 16) + var5);
               if (var3 != null) {
                  if (var3.refs.contains(this)) {
                     var3.refs.remove(this);
                     if (var3.refs.isEmpty()) {
                        SharedChunks.remove((var3.wx << 16) + var3.wy);
                     }
                  }

                  var3 = null;
               }
            }

            if (var3 != null && var2 == ChunkGridWidth - 1) {
               var3.refs.remove(this);
               if (var3.refs.isEmpty()) {
                  SharedChunks.remove((var3.wx << 16) + var3.wy);
                  var3.removeFromWorld();
                  ChunkSaveWorker.instance.Add(var3);
               }
            }

            this.setChunk(var1, var2, this.getChunk(var1, var2 - 1));
         }

         this.setChunk(var1, 0, (IsoChunk)null);
      }

      --this.WorldY;
   }

   void Down() {
      for(int var1 = 0; var1 < ChunkGridWidth; ++var1) {
         for(int var2 = 0; var2 < ChunkGridWidth - 1; ++var2) {
            IsoChunk var3 = this.getChunk(var1, var2);
            if (var3 == null && var2 == 0) {
               int var4 = this.WorldX - ChunkGridWidth / 2 + var1;
               int var5 = this.WorldY - ChunkGridWidth / 2 + var2;
               var3 = (IsoChunk)SharedChunks.get((var4 << 16) + var5);
               if (var3 != null) {
                  if (var3.refs.contains(this)) {
                     var3.refs.remove(this);
                     if (var3.refs.isEmpty()) {
                        SharedChunks.remove((var3.wx << 16) + var3.wy);
                     }
                  }

                  var3 = null;
               }
            }

            if (var3 != null && var2 == 0) {
               var3.refs.remove(this);
               if (var3.refs.isEmpty()) {
                  SharedChunks.remove((var3.wx << 16) + var3.wy);
                  var3.removeFromWorld();
                  ChunkSaveWorker.instance.Add(var3);
               }
            }

            this.setChunk(var1, var2, this.getChunk(var1, var2 + 1));
         }

         this.setChunk(var1, ChunkGridWidth - 1, (IsoChunk)null);
      }

      ++this.WorldY;
   }

   void Left() {
      for(int var1 = 0; var1 < ChunkGridWidth; ++var1) {
         for(int var2 = ChunkGridWidth - 1; var2 > 0; --var2) {
            IsoChunk var3 = this.getChunk(var2, var1);
            if (var3 == null && var2 == ChunkGridWidth - 1) {
               int var4 = this.WorldX - ChunkGridWidth / 2 + var2;
               int var5 = this.WorldY - ChunkGridWidth / 2 + var1;
               var3 = (IsoChunk)SharedChunks.get((var4 << 16) + var5);
               if (var3 != null) {
                  if (var3.refs.contains(this)) {
                     var3.refs.remove(this);
                     if (var3.refs.isEmpty()) {
                        SharedChunks.remove((var3.wx << 16) + var3.wy);
                     }
                  }

                  var3 = null;
               }
            }

            if (var3 != null && var2 == ChunkGridWidth - 1) {
               var3.refs.remove(this);
               if (var3.refs.isEmpty()) {
                  SharedChunks.remove((var3.wx << 16) + var3.wy);
                  var3.removeFromWorld();
                  ChunkSaveWorker.instance.Add(var3);
               }
            }

            this.setChunk(var2, var1, this.getChunk(var2 - 1, var1));
         }

         this.setChunk(0, var1, (IsoChunk)null);
      }

      --this.WorldX;
   }

   public void Right() {
      for(int var1 = 0; var1 < ChunkGridWidth; ++var1) {
         for(int var2 = 0; var2 < ChunkGridWidth - 1; ++var2) {
            IsoChunk var3 = this.getChunk(var2, var1);
            if (var3 == null && var2 == 0) {
               int var4 = this.WorldX - ChunkGridWidth / 2 + var2;
               int var5 = this.WorldY - ChunkGridWidth / 2 + var1;
               var3 = (IsoChunk)SharedChunks.get((var4 << 16) + var5);
               if (var3 != null) {
                  if (var3.refs.contains(this)) {
                     var3.refs.remove(this);
                     if (var3.refs.isEmpty()) {
                        SharedChunks.remove((var3.wx << 16) + var3.wy);
                     }
                  }

                  var3 = null;
               }
            }

            if (var3 != null && var2 == 0) {
               var3.refs.remove(this);
               if (var3.refs.isEmpty()) {
                  SharedChunks.remove((var3.wx << 16) + var3.wy);
                  var3.removeFromWorld();
                  ChunkSaveWorker.instance.Add(var3);
               }
            }

            this.setChunk(var2, var1, this.getChunk(var2 + 1, var1));
         }

         this.setChunk(ChunkGridWidth - 1, var1, (IsoChunk)null);
      }

      ++this.WorldX;
   }

   public int getWorldXMin() {
      return this.WorldX - ChunkGridWidth / 2;
   }

   public int getWorldYMin() {
      return this.WorldY - ChunkGridWidth / 2;
   }

   public void ProcessChunkPos(IsoGameCharacter var1) {
      boolean var2 = false;
      int var3 = (int)var1.getX();
      int var4 = (int)var1.getY();
      int var5 = (int)var1.getZ();
      if (IsoPlayer.instance != null && IsoPlayer.instance.getVehicle() != null) {
         IsoPlayer var6 = IsoPlayer.instance;
         BaseVehicle var7 = var6.getVehicle();
         float var8 = var7.getCurrentSpeedKmHour() / 5.0F;
         var3 += Math.round(var6.angle.x * var8);
         var4 += Math.round(var6.angle.y * var8);
      }

      var3 /= 10;
      var4 /= 10;
      if (var3 != this.WorldX || var4 != this.WorldY) {
         long var24 = System.nanoTime();
         double var25 = 0.0D;
         bSettingChunk.lock();
         synchronized(LightingThread.instance.bHasLock) {
            if (LightingThread.instance.bHasLock) {
               LightingThread.instance.Interrupted = true;
               LightingThread.instance.lightingThread.interrupt();
            }

            bSettingChunkLighting.lock();
         }

         long var10 = System.nanoTime();

         try {
            if (Math.abs(var3 - this.WorldX) < ChunkGridWidth && Math.abs(var4 - this.WorldY) < ChunkGridWidth) {
               if (var3 != this.WorldX) {
                  if (var3 < this.WorldX) {
                     this.LoadLeft();
                  } else {
                     this.LoadRight();
                  }

                  this.bMovingPos = false;
               } else if (var4 != this.WorldY) {
                  if (var4 < this.WorldY) {
                     this.LoadUp();
                  } else {
                     this.LoadDown();
                  }

                  this.bMovingPos = false;
               }
            } else {
               if (LightingJNI.init) {
                  LightingJNI.teleport(this.PlayerID, var3 - ChunkGridWidth / 2, var4 - ChunkGridWidth / 2);
               }

               this.Unload();
               IsoPlayer var12 = IsoPlayer.players[this.PlayerID];
               var12.removeFromSquare();
               var12.square = null;
               this.WorldX = var3;
               this.WorldY = var4;
               WorldSimulation.instance.activateChunkMap(this.PlayerID);
               int var13 = this.WorldX - ChunkGridWidth / 2;
               int var14 = this.WorldY - ChunkGridWidth / 2;
               int var15 = this.WorldX + ChunkGridWidth / 2;
               int var16 = this.WorldY + ChunkGridWidth / 2;

               for(int var17 = var13; var17 <= var15; ++var17) {
                  for(int var18 = var14; var18 <= var16; ++var18) {
                     this.LoadChunkForLater(var17, var18, var17 - var13, var18 - var14);
                  }
               }

               this.SwapChunkBuffers();
               this.UpdateCellCache();
               LightingThread.instance.bMovedMap = true;
               if (!IsoWorld.instance.getCell().getObjectList().contains(var12)) {
                  IsoWorld.instance.getCell().getAddList().add(var12);
               }
            }
         } finally {
            bSettingChunkLighting.unlock();
            bSettingChunk.unlock();
         }

         var25 = (double)(System.nanoTime() - var10) / 1000000.0D;
         double var26 = (double)(System.nanoTime() - var24) / 1000000.0D;
         if (LightingThread.DebugLockTime && var26 > 10.0D) {
            DebugLog.log("ProcessChunkPos time " + var25 + "/" + var26 + " ms");
         }

      }
   }

   private void SendRequestForZip(int var1, int var2) {
      if (this.WorldX > var1) {
      }

   }

   public IsoRoom getRoom(int var1) {
      return null;
   }

   public int getWidthInTiles() {
      return ChunkWidthInTiles;
   }

   public int getWorldXMinTiles() {
      if (this.XMinTiles != -1) {
         return this.XMinTiles;
      } else {
         this.XMinTiles = this.getWorldXMin() * 10;
         return this.XMinTiles;
      }
   }

   public int getWorldYMinTiles() {
      if (this.YMinTiles != -1) {
         return this.YMinTiles;
      } else {
         this.YMinTiles = this.getWorldYMin() * 10;
         return this.YMinTiles;
      }
   }

   public int getWorldXMaxTiles() {
      if (this.XMaxTiles != -1) {
         return this.XMaxTiles;
      } else {
         this.XMaxTiles = this.getWorldXMin() * 10 + this.getWidthInTiles();
         return this.XMaxTiles;
      }
   }

   public int getWorldYMaxTiles() {
      if (this.YMaxTiles != -1) {
         return this.YMaxTiles;
      } else {
         this.YMaxTiles = this.getWorldYMin() * 10 + this.getWidthInTiles();
         return this.YMaxTiles;
      }
   }

   public void Save() {
      if (!GameServer.bServer) {
         for(int var1 = 0; var1 < ChunkGridWidth; ++var1) {
            for(int var2 = 0; var2 < ChunkGridWidth; ++var2) {
               IsoChunk var3 = this.getChunk(var1, var2);
               if (var3 != null && !saveList.contains(var3)) {
                  try {
                     var3.Save(true);
                  } catch (IOException var5) {
                     var5.printStackTrace();
                  }
               }
            }
         }

      }
   }

   public static void DoSave() {
      for(int var0 = 0; var0 < saveList.size(); ++var0) {
      }

   }

   public void renderBloodForChunks(int var1) {
      if (!((float)var1 > IsoCamera.CamCharacter.z)) {
         if (Core.OptionBloodDecals != 0) {
            float var2 = (float)GameTime.getInstance().getWorldAgeHours();
            int var3;
            if (splatByType == null) {
               splatByType = new ArrayList();

               for(var3 = 0; var3 < IsoFloorBloodSplat.FloorBloodTypes.length; ++var3) {
                  splatByType.add(new ArrayList());
               }
            }

            for(var3 = 0; var3 < IsoFloorBloodSplat.FloorBloodTypes.length; ++var3) {
               ((ArrayList)splatByType.get(var3)).clear();
            }

            for(var3 = 0; var3 < ChunkGridWidth; ++var3) {
               for(int var4 = 0; var4 < ChunkGridWidth; ++var4) {
                  IsoChunk var5 = this.getChunk(var3, var4);
                  if (var5 != null) {
                     int var6;
                     IsoFloorBloodSplat var7;
                     for(var6 = 0; var6 < var5.FloorBloodSplatsFade.size(); ++var6) {
                        var7 = (IsoFloorBloodSplat)var5.FloorBloodSplatsFade.get(var6);
                        if ((var7.index < 1 || var7.index > 10 || IsoChunk.renderByIndex[Core.OptionBloodDecals - 1][var7.index - 1] != 0) && (int)var7.z == var1 && var7.Type >= 0 && var7.Type < IsoFloorBloodSplat.FloorBloodTypes.length) {
                           var7.chunk = var5;
                           ((ArrayList)splatByType.get(var7.Type)).add(var7);
                        }
                     }

                     if (!var5.FloorBloodSplats.isEmpty()) {
                        for(var6 = 0; var6 < var5.FloorBloodSplats.size(); ++var6) {
                           var7 = (IsoFloorBloodSplat)var5.FloorBloodSplats.get(var6);
                           if ((var7.index < 1 || var7.index > 10 || IsoChunk.renderByIndex[Core.OptionBloodDecals - 1][var7.index - 1] != 0) && (int)var7.z == var1 && var7.Type >= 0 && var7.Type < IsoFloorBloodSplat.FloorBloodTypes.length) {
                              var7.chunk = var5;
                              ((ArrayList)splatByType.get(var7.Type)).add(var7);
                           }
                        }
                     }
                  }
               }
            }

            for(var3 = 0; var3 < splatByType.size(); ++var3) {
               ArrayList var14 = (ArrayList)splatByType.get(var3);
               if (!var14.isEmpty()) {
                  String var15 = IsoFloorBloodSplat.FloorBloodTypes[var3];
                  IsoSprite var16 = null;
                  if (!IsoFloorBloodSplat.SpriteMap.containsKey(var15)) {
                     IsoSprite var17 = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
                     var17.LoadFramesPageSimple(var15, var15, var15, var15);
                     IsoFloorBloodSplat.SpriteMap.put(var15, var17);
                     var16 = var17;
                  } else {
                     var16 = (IsoSprite)IsoFloorBloodSplat.SpriteMap.get(var15);
                  }

                  for(int var18 = 0; var18 < var14.size(); ++var18) {
                     IsoFloorBloodSplat var8 = (IsoFloorBloodSplat)var14.get(var18);
                     inf.r = 1.0F;
                     inf.g = 1.0F;
                     inf.b = 1.0F;
                     inf.a = 0.27F;
                     float var9 = (var8.x + var8.y / var8.x) * (float)(var8.Type + 1);
                     float var10 = var9 * var8.x / var8.y * (float)(var8.Type + 1) / (var9 + var8.y);
                     float var11 = var10 * var9 * var10 * var8.x / (var8.y + 2.0F);
                     var9 *= 42367.543F;
                     var10 *= 6367.123F;
                     var11 *= 23367.133F;
                     var9 %= 1000.0F;
                     var10 %= 1000.0F;
                     var11 %= 1000.0F;
                     var9 /= 1000.0F;
                     var10 /= 1000.0F;
                     var11 /= 1000.0F;
                     if (var9 > 0.25F) {
                        var9 = 0.25F;
                     }

                     ColorInfo var10000 = inf;
                     var10000.r -= var9 * 2.0F;
                     var10000 = inf;
                     var10000.g -= var9 * 2.0F;
                     var10000 = inf;
                     var10000.b -= var9 * 2.0F;
                     var10000 = inf;
                     var10000.r += var10 / 3.0F;
                     var10000 = inf;
                     var10000.g -= var11 / 3.0F;
                     var10000 = inf;
                     var10000.b -= var11 / 3.0F;
                     float var12 = var2 - var8.worldAge;
                     if (var12 >= 0.0F && var12 < 72.0F) {
                        float var13 = 1.0F - var12 / 72.0F;
                        var10000 = inf;
                        var10000.r *= 0.2F + var13 * 0.8F;
                        var10000 = inf;
                        var10000.g *= 0.2F + var13 * 0.8F;
                        var10000 = inf;
                        var10000.b *= 0.2F + var13 * 0.8F;
                        var10000 = inf;
                        var10000.a *= 0.25F + var13 * 0.75F;
                        if (var8.fade > 0) {
                           var10000 = inf;
                           var10000.a *= (float)var8.fade / ((float)PerformanceSettings.LockFPS * 5.0F);
                           if (--var8.fade == 0) {
                              var8.chunk.FloorBloodSplatsFade.remove(var8);
                           }
                        }
                     } else {
                        var10000 = inf;
                        var10000.r *= 0.2F;
                        var10000 = inf;
                        var10000.g *= 0.2F;
                        var10000 = inf;
                        var10000.b *= 0.2F;
                        var10000 = inf;
                        var10000.a *= 0.25F;
                        if (var8.fade > 0) {
                           var10000 = inf;
                           var10000.a *= (float)var8.fade / ((float)PerformanceSettings.LockFPS * 5.0F);
                           if (--var8.fade == 0) {
                              var8.chunk.FloorBloodSplatsFade.remove(var8);
                           }
                        }
                     }

                     var16.renderBloodSplat((float)(var8.chunk.wx * 10) + var8.x, (float)(var8.chunk.wy * 10) + var8.y, var8.z, inf);
                  }
               }
            }

         }
      }
   }

   public void copy(IsoChunkMap var1) {
      IsoChunkMap var2 = this;
      this.WorldX = var1.WorldX;
      this.WorldY = var1.WorldY;
      this.XMinTiles = -1;
      this.YMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMaxTiles = -1;

      for(int var3 = 0; var3 < ChunkGridWidth * ChunkGridWidth; ++var3) {
         var2.bReadBufferA = var1.bReadBufferA;
         if (var2.bReadBufferA) {
            if (var1.chunksSwapA[var3] != null) {
               var1.chunksSwapA[var3].refs.add(var2);
               var2.chunksSwapA[var3] = var1.chunksSwapA[var3];
            }
         } else if (var1.chunksSwapB[var3] != null) {
            var1.chunksSwapB[var3].refs.add(var2);
            var2.chunksSwapB[var3] = var1.chunksSwapB[var3];
         }
      }

   }

   public void Unload() {
      for(int var1 = 0; var1 < ChunkGridWidth; ++var1) {
         for(int var2 = 0; var2 < ChunkGridWidth; ++var2) {
            IsoChunk var3 = this.getChunk(var2, var1);
            if (var3 != null) {
               if (var3.refs.contains(this)) {
                  var3.refs.remove(this);
                  if (var3.refs.isEmpty()) {
                     SharedChunks.remove((var3.wx << 16) + var3.wy);
                     var3.removeFromWorld();
                     ChunkSaveWorker.instance.Add(var3);
                  }
               }

               this.chunksSwapA[var1 * ChunkGridWidth + var2] = null;
               this.chunksSwapB[var1 * ChunkGridWidth + var2] = null;
            }
         }
      }

      WorldSimulation.instance.deactivateChunkMap(this.PlayerID);
      this.XMinTiles = -1;
      this.XMaxTiles = -1;
      this.YMinTiles = -1;
      this.YMaxTiles = -1;
      if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
         IsoWorld.instance.CurrentCell.clearCacheGridSquare(this.PlayerID);
      }

   }

   static {
      ChunkGridWidth = StartChunkGridWidth;
      MPWorldXA = 0;
      MPWorldYA = 0;
      MPWorldZA = 0;
      ChunkWidthInTiles = 10 * ChunkGridWidth;
      WorldCellX = 10;
      WorldCellY = 7;
      PosX = 259;
      PosY = 209;
      WorldXA = 11702;
      WorldYA = 6896;
      WorldZA = 0;
      SWorldX = new int[4];
      SWorldY = new int[4];
      chunkStore = new ConcurrentLinkedQueue();
      bSettingChunk = new ReentrantLock(true);
      bSettingChunkLighting = new ReentrantLock(true);
      SharedChunks = new HashMap();
      saveList = new ArrayList();
      inf = new ColorInfo();
   }
}
