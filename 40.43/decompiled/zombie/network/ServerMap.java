package zombie.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import zombie.GameTime;
import zombie.MapCollisionData;
import zombie.ReanimatedPlayers;
import zombie.VirtualZombieManager;
import zombie.ai.astar.Mover;
import zombie.ai.astar.TileBasedMap;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.core.stash.StashSystem;
import zombie.core.utils.OnceEvery;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.globalObjects.SGlobalObjects;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.popman.ZombiePopulationManager;

public class ServerMap implements TileBasedMap {
   public boolean bUpdateLOSThisFrame = false;
   public static OnceEvery LOSTick = new OnceEvery(1.0F);
   public static OnceEvery TimeTick = new OnceEvery(600.0F);
   public static final int CellSize = 70;
   public static final int ChunksPerCellWidth = 7;
   public long LastSaved = 0L;
   private static boolean MapLoading;
   public final ServerMap.ZombieIDMap ZombieMap = new ServerMap.ZombieIDMap();
   public boolean bQueuedSaveAll = false;
   public boolean bQueuedQuit = false;
   public static ServerMap instance = new ServerMap();
   public ServerMap.ServerCell[] cellMap;
   public ArrayList LoadedCells = new ArrayList();
   public ArrayList ReleventNow = new ArrayList();
   int width;
   int height;
   IsoMetaGrid grid;
   ArrayList ToLoad = new ArrayList();
   static final ServerMap.DistToCellComparator distToCellComparator = new ServerMap.DistToCellComparator();
   private final ArrayList tempCells = new ArrayList();
   Vector2 start;
   Vector2 tempo = new Vector2();

   public short getUniqueZombieId() {
      return this.ZombieMap.allocateID();
   }

   public Vector3 getStartLocation(ServerWorldDatabase.LogonResult var1) {
      short var2 = 9412;
      short var3 = 10745;
      byte var4 = 0;
      return new Vector3((float)var3, (float)var2, (float)var4);
   }

   public void SaveAll() {
      long var1 = System.nanoTime();

      for(int var3 = 0; var3 < this.LoadedCells.size(); ++var3) {
         ((ServerMap.ServerCell)this.LoadedCells.get(var3)).Save();
      }

      this.grid.save();
      DebugLog.log("SaveAll took " + (double)(System.nanoTime() - var1) / 1000000.0D + " ms");
   }

   public void QueueSaveAll() {
      this.bQueuedSaveAll = true;
   }

   public void QueueQuit() {
      this.bQueuedSaveAll = true;
      this.bQueuedQuit = true;
   }

   public int toServerCellX(int var1) {
      var1 *= 300;
      var1 /= 70;
      return var1;
   }

   public int toServerCellY(int var1) {
      var1 *= 300;
      var1 /= 70;
      return var1;
   }

   public int toWorldCellX(int var1) {
      var1 *= 70;
      var1 /= 300;
      return var1;
   }

   public int toWorldCellY(int var1) {
      var1 *= 70;
      var1 /= 300;
      return var1;
   }

   public int getMaxX() {
      int var1 = this.toServerCellX(this.grid.maxX + 1);
      if ((this.grid.maxX + 1) * 300 % 70 == 0) {
         --var1;
      }

      return var1;
   }

   public int getMaxY() {
      int var1 = this.toServerCellY(this.grid.maxY + 1);
      if ((this.grid.maxY + 1) * 300 % 70 == 0) {
         --var1;
      }

      return var1;
   }

   public int getMinX() {
      int var1 = this.toServerCellX(this.grid.minX);
      return var1;
   }

   public int getMinY() {
      int var1 = this.toServerCellY(this.grid.minY);
      return var1;
   }

   public void init(IsoMetaGrid var1) {
      this.grid = var1;
      this.width = this.getMaxX() - this.getMinX() + 1;
      this.height = this.getMaxY() - this.getMinY() + 1;

      assert this.width * 70 >= var1.getWidth() * 300;

      assert this.height * 70 >= var1.getHeight() * 300;

      assert this.getMaxX() * 70 < (var1.getMaxX() + 1) * 300;

      assert this.getMaxY() * 70 < (var1.getMaxY() + 1) * 300;

      int var2 = this.width * this.height;
      this.cellMap = new ServerMap.ServerCell[var2];
      StashSystem.init();
   }

   public ServerMap.ServerCell getCell(int var1, int var2) {
      return !this.isValidCell(var1, var2) ? null : this.cellMap[var2 * this.width + var1];
   }

   public boolean isValidCell(int var1, int var2) {
      return var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height;
   }

   public void loadOrKeepRelevent(int var1, int var2) {
      if (this.isValidCell(var1, var2)) {
         ServerMap.ServerCell var3 = this.getCell(var1, var2);
         if (var3 == null) {
            var3 = new ServerMap.ServerCell();
            var3.WX = var1 + this.getMinX();
            var3.WY = var2 + this.getMinY();
            if (MapLoading) {
               DebugLog.log(DebugType.MapLoading, "Loading cell: " + var3.WX + ", " + var3.WY + " (" + this.toWorldCellX(var3.WX) + ", " + this.toWorldCellX(var3.WY) + ")");
            }

            this.cellMap[var2 * this.width + var1] = var3;
            this.ToLoad.add(var3);
            this.LoadedCells.add(var3);
            this.ReleventNow.add(var3);
         } else if (!this.ReleventNow.contains(var3)) {
            this.ReleventNow.add(var3);
         }

      }
   }

   public void characterIn(IsoPlayer var1) {
      while(this.grid == null) {
         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var15) {
            var15.printStackTrace();
         }
      }

      int var2 = (int)var1.getX();
      int var3 = (int)var1.getY();
      var2 = (int)((float)var2 / 70.0F);
      var3 = (int)((float)var3 / 70.0F);
      var2 -= this.getMinX();
      var3 -= this.getMinY();
      int var6 = (int)var1.getX() % 70;
      int var7 = (int)var1.getY() % 70;
      int var8 = var1.OnlineChunkGridWidth / 2 * 10;
      int var9 = var2;
      int var10 = var3;
      int var11 = var2;
      int var12 = var3;
      if (var6 < var8) {
         var9 = var2 - 1;
      }

      if (var6 > 70 - var8) {
         var11 = var2 + 1;
      }

      if (var7 < var8) {
         var10 = var3 - 1;
      }

      if (var7 > 70 - var8) {
         var12 = var3 + 1;
      }

      for(int var13 = var10; var13 <= var12; ++var13) {
         for(int var14 = var9; var14 <= var11; ++var14) {
            this.loadOrKeepRelevent(var14, var13);
         }
      }

   }

   public void characterIn(int var1, int var2, int var3) {
      while(this.grid == null) {
         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var17) {
            var17.printStackTrace();
         }
      }

      int var4 = var1 * 10;
      int var5 = var2 * 10;
      var4 = (int)((float)var4 / 70.0F);
      var5 = (int)((float)var5 / 70.0F);
      var4 -= this.getMinX();
      var5 -= this.getMinY();
      int var8 = var1 * 10 % 70;
      int var9 = var2 * 10 % 70;
      int var10 = var3 / 2 * 10;
      int var11 = var4;
      int var12 = var5;
      int var13 = var4;
      int var14 = var5;
      if (var8 < var10) {
         var11 = var4 - 1;
      }

      if (var8 > 70 - var10) {
         var13 = var4 + 1;
      }

      if (var9 < var10) {
         var12 = var5 - 1;
      }

      if (var9 > 70 - var10) {
         var14 = var5 + 1;
      }

      for(int var15 = var12; var15 <= var14; ++var15) {
         for(int var16 = var11; var16 <= var13; ++var16) {
            this.loadOrKeepRelevent(var16, var15);
         }
      }

   }

   public void loadMapChunk(int var1, int var2) {
      while(this.grid == null) {
         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var5) {
            var5.printStackTrace();
         }
      }

      int var3 = (int)((float)var1 / 70.0F);
      int var4 = (int)((float)var2 / 70.0F);
      var3 -= this.getMinX();
      var4 -= this.getMinY();
      this.loadOrKeepRelevent(var3, var4);
   }

   public void preupdate() {
      MapLoading = DebugType.Do(DebugType.MapLoading);

      int var1;
      ServerMap.ServerCell var2;
      for(var1 = 0; var1 < this.ToLoad.size(); ++var1) {
         var2 = (ServerMap.ServerCell)this.ToLoad.get(var1);
         if (var2.bLoadingWasCancelled) {
            if (MapLoading) {
               DebugLog.log(DebugType.MapLoading, "MainThread: forgetting cancelled " + var2.WX + "," + var2.WY);
            }

            int var3 = var2.WX - this.getMinX();
            int var4 = var2.WY - this.getMinY();

            assert this.cellMap[var3 + var4 * this.width] == var2;

            this.cellMap[var3 + var4 * this.width] = null;
            this.LoadedCells.remove(var2);
            this.ReleventNow.remove(var2);
            this.ToLoad.remove(var1--);
         }
      }

      if (!this.ToLoad.isEmpty()) {
         this.tempCells.clear();

         for(var1 = 0; var1 < this.ToLoad.size(); ++var1) {
            var2 = (ServerMap.ServerCell)this.ToLoad.get(var1);
            if (!var2.bCancelLoading && !var2.startedLoading) {
               this.tempCells.add(var2);
            }
         }

         if (!this.tempCells.isEmpty()) {
            distToCellComparator.init();
            Collections.sort(this.tempCells, distToCellComparator);

            for(var1 = 0; var1 < this.tempCells.size(); ++var1) {
               var2 = (ServerMap.ServerCell)this.tempCells.get(var1);
               ServerMap.ServerCell.chunkLoader.addJob(var2);
               var2.startedLoading = true;
            }
         }

         ServerMap.ServerCell.chunkLoader.getLoaded(ServerMap.ServerCell.loaded);

         for(var1 = 0; var1 < ServerMap.ServerCell.loaded.size(); ++var1) {
            var2 = (ServerMap.ServerCell)ServerMap.ServerCell.loaded.get(var1);
            if (!var2.doingRecalc) {
               ServerMap.ServerCell.chunkLoader.addRecalcJob(var2);
               var2.doingRecalc = true;
            }
         }

         ServerMap.ServerCell.loaded.clear();
         ServerMap.ServerCell.chunkLoader.getRecalc(ServerMap.ServerCell.loaded2);
         if (!ServerMap.ServerCell.loaded2.isEmpty()) {
            ServerMap.ServerCell var8 = (ServerMap.ServerCell)ServerMap.ServerCell.loaded2.get(0);
            if (var8.Load2()) {
               this.ToLoad.remove(var8);
            }
         }
      }

      var1 = ServerOptions.instance.SaveWorldEveryMinutes.getValue();
      long var6;
      if (var1 > 0) {
         var6 = System.currentTimeMillis();
         if (var6 > this.LastSaved + (long)(var1 * 60 * 1000)) {
            this.bQueuedSaveAll = true;
            this.LastSaved = var6;
         }
      }

      if (this.bQueuedSaveAll) {
         this.bQueuedSaveAll = false;
         var6 = System.nanoTime();
         this.SaveAll();
         ServerMap.ServerCell.chunkLoader.saveLater(GameTime.instance);
         ReanimatedPlayers.instance.saveReanimatedPlayers();
         MapCollisionData.instance.save();
         SGlobalObjects.save();
         GameServer.UnPauseAllClients();
         System.out.println("Saving finish");
         DebugLog.log("Saving took " + (double)(System.nanoTime() - var6) / 1000000.0D + " ms");
      }

      if (this.bQueuedQuit) {
         ByteBufferWriter var7 = GameServer.udpEngine.startPacket();
         PacketTypes.doPacket((short)52, var7);
         GameServer.udpEngine.endPacketBroadcast();

         try {
            Thread.sleep(5000L);
         } catch (InterruptedException var5) {
            var5.printStackTrace();
         }

         MapCollisionData.instance.stop();
         ZombiePopulationManager.instance.stop();
         RCONServer.shutdown();
         ServerMap.ServerCell.chunkLoader.quit();
         ServerWorldDatabase.instance.close();
         GameServer.udpEngine.Shutdown();
         ServerGUI.shutdown();
         SteamUtils.shutdown();
         System.exit(0);
      }

      ZombieUpdatePacker.instance.clearZombies();
      this.ReleventNow.clear();
      this.bUpdateLOSThisFrame = LOSTick.Check();
      if (TimeTick.Check()) {
         ServerMap.ServerCell.chunkLoader.saveLater(GameTime.instance);
      }

   }

   private IsoGridSquare getRandomSquareFromCell(int var1, int var2) {
      this.loadOrKeepRelevent(var1, var2);
      int var3 = var1;
      int var4 = var2;
      ServerMap.ServerCell var5 = this.getCell(var1, var2);
      if (var5 == null) {
         throw new RuntimeException("Cannot find a random square.");
      } else {
         var1 = (var1 + this.getMinX()) * 70;
         var2 = (var2 + this.getMinY()) * 70;
         IsoGridSquare var6 = null;
         int var7 = 100;

         do {
            var6 = this.getGridSquare(Rand.Next(var1, var1 + 50), Rand.Next(var2, var2 + 50), 0);
            --var7;
            if (var6 == null) {
               this.loadOrKeepRelevent(var3, var4);
            }
         } while(var6 == null && var7 > 0);

         return var6;
      }
   }

   public void postupdate() {
      boolean var1 = false;

      try {
         for(int var2 = 0; var2 < this.LoadedCells.size(); ++var2) {
            ServerMap.ServerCell var3 = (ServerMap.ServerCell)this.LoadedCells.get(var2);
            boolean var4 = this.ReleventNow.contains(var3) || !this.outsidePlayerInfluence(var3);
            if (!var3.bLoaded) {
               if (!var4 && !var3.bCancelLoading) {
                  if (MapLoading) {
                     DebugLog.log(DebugType.MapLoading, "MainThread: cancelling " + var3.WX + "," + var3.WY + " cell.startedLoading=" + var3.startedLoading);
                  }

                  if (!var3.startedLoading) {
                     var3.bLoadingWasCancelled = true;
                  }

                  var3.bCancelLoading = true;
               }
            } else {
               int var5;
               int var6;
               if (!var4) {
                  var5 = var3.WX - this.getMinX();
                  var6 = var3.WY - this.getMinY();
                  if (!var1) {
                     ServerLOS.instance.suspend();
                     var1 = true;
                  }

                  this.cellMap[var6 * this.width + var5].Unload();
                  this.cellMap[var6 * this.width + var5] = null;
                  this.LoadedCells.remove(var3);
                  --var2;
               } else if (var3.bPhysicsCheck) {
                  for(var5 = 0; var5 < 7; ++var5) {
                     for(var6 = 0; var6 < 7; ++var6) {
                        if (var3.chunks[var6][var5] != null) {
                           var3.chunks[var6][var5].update();
                        }
                     }
                  }

                  var3.bPhysicsCheck = false;
               }
            }
         }
      } catch (Exception var12) {
         var12.printStackTrace();
      } finally {
         if (var1) {
            ServerLOS.instance.resume();
         }

      }

      try {
         ZombieUpdatePacker.instance.packZombiesIntoPackets();
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      ServerMap.ServerCell.chunkLoader.updateSaved();
   }

   public void physicsCheck(int var1, int var2) {
      int var3 = var1 / 70;
      int var4 = var2 / 70;
      var3 -= this.getMinX();
      var4 -= this.getMinY();
      ServerMap.ServerCell var5 = this.getCell(var3, var4);
      if (var5 != null && var5.bLoaded) {
         var5.bPhysicsCheck = true;
      }

   }

   private boolean outsidePlayerInfluence(ServerMap.ServerCell var1) {
      int var2 = var1.WX * 70;
      int var3 = var1.WY * 70;
      int var4 = (var1.WX + 1) * 70;
      int var5 = (var1.WY + 1) * 70;

      for(int var6 = 0; var6 < GameServer.udpEngine.connections.size(); ++var6) {
         UdpConnection var7 = (UdpConnection)GameServer.udpEngine.connections.get(var6);
         if (var7.ReleventTo((float)var2, (float)var3)) {
            return false;
         }

         if (var7.ReleventTo((float)var4, (float)var3)) {
            return false;
         }

         if (var7.ReleventTo((float)var4, (float)var5)) {
            return false;
         }

         if (var7.ReleventTo((float)var2, (float)var5)) {
            return false;
         }
      }

      return true;
   }

   public void saveZoneInsidePlayerInfluence(int var1) {
      for(int var2 = 0; var2 < GameServer.udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)GameServer.udpEngine.connections.get(var2);

         for(int var4 = 0; var4 < var3.players.length; ++var4) {
            if (var3.players[var4] != null && var3.players[var4].OnlineID == var1) {
               IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare((double)var3.players[var4].x, (double)var3.players[var4].y, (double)var3.players[var4].z);
               if (var5 != null) {
                  ServerMap.ServerCell.chunkLoader.addSaveLoadedJob(var5.chunk);
                  return;
               }
            }
         }
      }

      ServerMap.ServerCell.chunkLoader.updateSaved();
   }

   private boolean InsideThePlayerInfluence(ServerMap.ServerCell var1, int var2) {
      int var3 = var1.WX * 70;
      int var4 = var1.WY * 70;
      int var5 = (var1.WX + 1) * 70;
      int var6 = (var1.WY + 1) * 70;

      for(int var7 = 0; var7 < GameServer.udpEngine.connections.size(); ++var7) {
         UdpConnection var8 = (UdpConnection)GameServer.udpEngine.connections.get(var7);

         for(int var9 = 0; var9 < var8.players.length; ++var9) {
            if (var8.players[var9] != null && var8.players[var9].OnlineID == var2) {
               if (var8.ReleventToPlayerIndex(var9, (float)var3, (float)var4)) {
                  return true;
               }

               if (var8.ReleventToPlayerIndex(var9, (float)var5, (float)var4)) {
                  return true;
               }

               if (var8.ReleventToPlayerIndex(var9, (float)var5, (float)var6)) {
                  return true;
               }

               if (var8.ReleventToPlayerIndex(var9, (float)var3, (float)var6)) {
                  return true;
               }

               return false;
            }
         }
      }

      return false;
   }

   public boolean blocked(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return false;
   }

   public float getCost(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return 0.0F;
   }

   public int getElevInTiles() {
      return 0;
   }

   public int getHeightInTiles() {
      return 0;
   }

   public int getWidthInTiles() {
      return 0;
   }

   public void pathFinderVisited(int var1, int var2, int var3) {
   }

   public boolean isNull(int var1, int var2, int var3) {
      return false;
   }

   public boolean IsStairsNode(IsoGridSquare var1, IsoGridSquare var2, IsoDirections var3) {
      return false;
   }

   public IsoGridSquare getGridSquare(int var1, int var2, int var3) {
      if (!IsoWorld.instance.isValidSquare(var1, var2, var3)) {
         return null;
      } else {
         int var4 = var1 / 70;
         int var5 = var2 / 70;
         var4 -= this.getMinX();
         var5 -= this.getMinY();
         int var6 = var1 / 10;
         int var7 = var2 / 10;
         int var8 = var6 % 7;
         int var9 = var7 % 7;
         int var10 = var1 % 10;
         int var11 = var2 % 10;
         ServerMap.ServerCell var12 = this.getCell(var4, var5);
         if (var12 != null && var12.bLoaded) {
            IsoChunk var13 = var12.chunks[var8][var9];
            return var13 == null ? null : var13.getGridSquare(var10, var11, var3);
         } else {
            return null;
         }
      }
   }

   public void setGridSquare(int var1, int var2, int var3, IsoGridSquare var4) {
      int var5 = var1 / 70;
      int var6 = var2 / 70;
      var5 -= this.getMinX();
      var6 -= this.getMinY();
      int var7 = var1 / 10;
      int var8 = var2 / 10;
      int var9 = var7 % 7;
      int var10 = var8 % 7;
      int var11 = var1 % 10;
      int var12 = var2 % 10;
      ServerMap.ServerCell var13 = this.getCell(var5, var6);
      if (var13 != null) {
         IsoChunk var14 = var13.chunks[var9][var10];
         if (var14 != null) {
            var14.setSquare(var11, var12, var3, var4);
         }
      }
   }

   public boolean isInLoaded(float var1, float var2) {
      int var3 = (int)var1;
      int var4 = (int)var2;
      var3 /= 70;
      var4 /= 70;
      var3 -= this.getMinX();
      var4 -= this.getMinY();
      if (this.ToLoad.contains(this.getCell(var3, var4))) {
         return false;
      } else {
         return this.getCell(var3, var4) != null;
      }
   }

   public IsoChunk getChunk(int var1, int var2) {
      if (var1 >= 0 && var2 >= 0) {
         int var3 = var1 / 7;
         int var4 = var2 / 7;
         var3 -= this.getMinX();
         var4 -= this.getMinY();
         int var5 = var1 % 7;
         int var6 = var2 % 7;
         ServerMap.ServerCell var7 = this.getCell(var3, var4);
         return var7 != null && var7.bLoaded ? var7.chunks[var5][var6] : null;
      } else {
         return null;
      }
   }

   private static class DistToCellComparator implements Comparator {
      private Vector2[] pos = new Vector2[1024];
      private int posCount;

      public DistToCellComparator() {
         for(int var1 = 0; var1 < this.pos.length; ++var1) {
            this.pos[var1] = new Vector2();
         }

      }

      public void init() {
         this.posCount = 0;

         for(int var1 = 0; var1 < GameServer.udpEngine.connections.size(); ++var1) {
            UdpConnection var2 = (UdpConnection)GameServer.udpEngine.connections.get(var1);
            if (var2.isFullyConnected()) {
               for(int var3 = 0; var3 < 4; ++var3) {
                  if (var2.players[var3] != null) {
                     this.pos[this.posCount].set(var2.players[var3].x, var2.players[var3].y);
                     ++this.posCount;
                  }
               }
            }
         }

      }

      public int compare(ServerMap.ServerCell var1, ServerMap.ServerCell var2) {
         float var3 = Float.MAX_VALUE;
         float var4 = Float.MAX_VALUE;

         for(int var5 = 0; var5 < this.posCount; ++var5) {
            float var6 = this.pos[var5].x;
            float var7 = this.pos[var5].y;
            var3 = Math.min(var3, this.distToCell(var6, var7, var1));
            var4 = Math.min(var4, this.distToCell(var6, var7, var2));
         }

         if (var3 < var4) {
            return -1;
         } else {
            return var3 > var4 ? 1 : 0;
         }
      }

      private float distToCell(float var1, float var2, ServerMap.ServerCell var3) {
         int var4 = var3.WX * 70;
         int var5 = var3.WY * 70;
         int var6 = var4 + 70;
         int var7 = var5 + 70;
         float var8 = var1;
         float var9 = var2;
         if (var1 < (float)var4) {
            var8 = (float)var4;
         } else if (var1 > (float)var6) {
            var8 = (float)var6;
         }

         if (var2 < (float)var5) {
            var9 = (float)var5;
         } else if (var2 > (float)var7) {
            var9 = (float)var7;
         }

         return IsoUtils.DistanceToSquared(var1, var2, var8, var9);
      }
   }

   public static class ServerCell {
      public int WX;
      public int WY;
      public boolean bLoaded = false;
      public boolean bPhysicsCheck = false;
      public final IsoChunk[][] chunks = new IsoChunk[7][7];
      private HashSet UnexploredRooms = new HashSet();
      private static ServerChunkLoader chunkLoader = new ServerChunkLoader();
      private static ArrayList loaded = new ArrayList();
      private boolean startedLoading = false;
      public boolean bCancelLoading = false;
      public boolean bLoadingWasCancelled = false;
      private static ArrayList loaded2 = new ArrayList();
      private boolean doingRecalc = false;

      public boolean Load2() {
         chunkLoader.getRecalc(loaded2);

         for(int var1 = 0; var1 < loaded2.size(); ++var1) {
            if (loaded2.get(var1) == this) {
               long var2 = System.nanoTime();

               try {
                  ServerLOS.instance.suspend();
                  this.RecalcAll2();
               } finally {
                  ServerLOS.instance.resume();
               }

               loaded2.remove(var1);
               if (ServerMap.MapLoading) {
                  DebugLog.log(DebugType.MapLoading, "loaded2=" + loaded2);
               }

               float var4 = (float)(System.nanoTime() - var2) / 1000000.0F;
               if (ServerMap.MapLoading) {
                  DebugLog.log(DebugType.MapLoading, "finish loading cell " + this.WX + "," + this.WY + " ms=" + var4);
               }

               return true;
            }
         }

         return false;
      }

      public void RecalcAll2() {
         int var1 = this.WX * 7 * 10;
         int var2 = this.WY * 7 * 10;
         int var3 = var1 + 70;
         int var4 = var2 + 70;

         RoomDef var6;
         for(Iterator var5 = this.UnexploredRooms.iterator(); var5.hasNext(); --var6.IndoorZombies) {
            var6 = (RoomDef)var5.next();
         }

         this.UnexploredRooms.clear();
         this.bLoaded = true;

         IsoGridSquare var7;
         int var13;
         int var14;
         for(var13 = 1; var13 < 8; ++var13) {
            for(var14 = -1; var14 < 71; ++var14) {
               var7 = ServerMap.instance.getGridSquare(var1 + var14, var2 - 1, var13);
               if (var7 != null && !var7.getObjects().isEmpty()) {
                  IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(var7.x, var7.y, var13);
               } else if (var14 >= 0 && var14 < 70) {
                  var7 = ServerMap.instance.getGridSquare(var1 + var14, var2, var13);
                  if (var7 != null && !var7.getObjects().isEmpty()) {
                     IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(var7.x, var7.y, var13);
                  }
               }

               var7 = ServerMap.instance.getGridSquare(var1 + var14, var2 + 70, var13);
               if (var7 != null && !var7.getObjects().isEmpty()) {
                  IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(var7.x, var7.y, var13);
               } else if (var14 >= 0 && var14 < 70) {
                  ServerMap.instance.getGridSquare(var1 + var14, var2 + 70 - 1, var13);
                  if (var7 != null && !var7.getObjects().isEmpty()) {
                     IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(var7.x, var7.y, var13);
                  }
               }
            }

            for(var14 = 0; var14 < 70; ++var14) {
               var7 = ServerMap.instance.getGridSquare(var1 - 1, var2 + var14, var13);
               if (var7 != null && !var7.getObjects().isEmpty()) {
                  IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(var7.x, var7.y, var13);
               } else {
                  var7 = ServerMap.instance.getGridSquare(var1, var2 + var14, var13);
                  if (var7 != null && !var7.getObjects().isEmpty()) {
                     IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(var7.x, var7.y, var13);
                  }
               }

               var7 = ServerMap.instance.getGridSquare(var1 + 70, var2 + var14, var13);
               if (var7 != null && !var7.getObjects().isEmpty()) {
                  IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(var7.x, var7.y, var13);
               } else {
                  var7 = ServerMap.instance.getGridSquare(var1 + 70 - 1, var2 + var14, var13);
                  if (var7 != null && !var7.getObjects().isEmpty()) {
                     IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(var7.x, var7.y, var13);
                  }
               }
            }
         }

         for(var13 = 0; var13 < 8; ++var13) {
            for(var14 = 0; var14 < 70; ++var14) {
               var7 = ServerMap.instance.getGridSquare(var1 + var14, var2 + 0, var13);
               if (var7 != null) {
                  var7.RecalcAllWithNeighbours(true);
               }

               var7 = ServerMap.instance.getGridSquare(var1 + var14, var4 - 1, var13);
               if (var7 != null) {
                  var7.RecalcAllWithNeighbours(true);
               }
            }

            for(var14 = 0; var14 < 70; ++var14) {
               var7 = ServerMap.instance.getGridSquare(var1 + 0, var2 + var14, var13);
               if (var7 != null) {
                  var7.RecalcAllWithNeighbours(true);
               }

               var7 = ServerMap.instance.getGridSquare(var3 - 1, var2 + var14, var13);
               if (var7 != null) {
                  var7.RecalcAllWithNeighbours(true);
               }
            }
         }

         byte var15 = 100;

         int var17;
         for(var14 = 0; var14 < 7; ++var14) {
            for(var17 = 0; var17 < 7; ++var17) {
               IsoChunk var8 = this.chunks[var14][var17];
               if (var8 != null) {
                  var8.bLoaded = true;

                  for(int var9 = 0; var9 < var15; ++var9) {
                     for(int var10 = 0; var10 <= var8.maxLevel; ++var10) {
                        IsoGridSquare var11 = var8.squares[var10][var9];
                        if (var11 != null) {
                           if (var11.getRoom() != null && !var11.getRoom().def.bExplored) {
                              this.UnexploredRooms.add(var11.getRoom().def);
                           }

                           var11.propertiesDirty = true;
                        }
                     }
                  }
               }
            }
         }

         WorldSimulation.instance.createServerCell(this);

         for(var14 = 0; var14 < 7; ++var14) {
            for(var17 = 0; var17 < 7; ++var17) {
               if (this.chunks[var14][var17] != null) {
                  this.chunks[var14][var17].doLoadGridsquare();
               }
            }
         }

         Iterator var16 = this.UnexploredRooms.iterator();

         while(var16.hasNext()) {
            RoomDef var18 = (RoomDef)var16.next();
            ++var18.IndoorZombies;
            if (var18.IndoorZombies == 1) {
               try {
                  VirtualZombieManager.instance.tryAddIndoorZombies(var18, false);
               } catch (Exception var12) {
                  var12.printStackTrace();
               }
            }
         }

         this.bLoaded = true;
      }

      public void Unload() {
         if (this.bLoaded) {
            if (ServerMap.MapLoading) {
               DebugLog.log(DebugType.MapLoading, "Unloading cell: " + this.WX + ", " + this.WY + " (" + ServerMap.instance.toWorldCellX(this.WX) + ", " + ServerMap.instance.toWorldCellX(this.WY) + ")");
            }

            if (ChunkRevisions.USE_CHUNK_REVISIONS) {
               ChunkRevisions.instance.processRevisedSquares();
            }

            for(int var1 = 0; var1 < 7; ++var1) {
               for(int var2 = 0; var2 < 7; ++var2) {
                  if (this.chunks[var1][var2] != null) {
                     this.chunks[var1][var2].removeFromWorld();
                     chunkLoader.addSaveUnloadedJob(this.chunks[var1][var2]);
                     this.chunks[var1][var2] = null;
                  }
               }
            }

            RoomDef var4;
            for(Iterator var3 = this.UnexploredRooms.iterator(); var3.hasNext(); --var4.IndoorZombies) {
               var4 = (RoomDef)var3.next();
               if (var4.IndoorZombies == 1) {
               }
            }

            WorldSimulation.instance.removeServerCell(this);
         }
      }

      public void Save() {
         if (this.bLoaded) {
            for(int var1 = 0; var1 < 7; ++var1) {
               for(int var2 = 0; var2 < 7; ++var2) {
                  if (this.chunks[var1][var2] != null) {
                     try {
                        chunkLoader.addSaveLoadedJob(this.chunks[var1][var2]);
                     } catch (Exception var4) {
                        var4.printStackTrace();
                        LoggerManager.getLogger("map").write(var4);
                     }
                  }
               }
            }

            chunkLoader.updateSaved();
         }
      }
   }

   public static class ZombieIDMap {
      private static int MAX_ZOMBIES = 32767;
      private static int RESIZE_COUNT = 1024;
      private int capacity = 1024;
      private IsoZombie[] idToZombie;
      private short[] freeID;
      private short freeIDSize;
      private int warnCount = 0;

      ZombieIDMap() {
         this.idToZombie = new IsoZombie[this.capacity];
         this.freeID = new short[this.capacity];

         for(int var1 = 0; var1 < this.capacity; ++var1) {
            short[] var10000 = this.freeID;
            short var10003 = this.freeIDSize;
            this.freeIDSize = (short)(var10003 + 1);
            var10000[var10003] = (short)var1;
         }

      }

      public void put(short var1, IsoZombie var2) {
         if (var1 >= 0 && var1 < this.capacity) {
            if (this.idToZombie[var1] != null) {
               throw new IllegalArgumentException("duplicate zombie with id " + var1);
            } else if (var2 == null) {
               throw new IllegalArgumentException("zombie is null");
            } else {
               this.idToZombie[var1] = var2;
            }
         } else {
            throw new IllegalArgumentException("invalid zombie id " + var1 + " max=" + this.capacity);
         }
      }

      public void remove(short var1) {
         if (var1 >= 0 && var1 < this.capacity) {
            if (this.idToZombie[var1] == null) {
               throw new IllegalArgumentException("no zombie with id " + var1 + "");
            } else {
               this.idToZombie[var1] = null;
               short[] var10000 = this.freeID;
               short var10003 = this.freeIDSize;
               this.freeIDSize = (short)(var10003 + 1);
               var10000[var10003] = var1;
            }
         } else {
            throw new IllegalArgumentException("invalid zombie id=" + var1 + " max=" + this.capacity);
         }
      }

      public IsoZombie get(short var1) {
         return this.idToZombie[var1];
      }

      private short allocateID() {
         if (this.freeIDSize > 0) {
            return this.freeID[--this.freeIDSize];
         } else if (this.capacity >= MAX_ZOMBIES) {
            if (this.warnCount < 100) {
               DebugLog.log("warning: ran out of unique zombie ids");
               ++this.warnCount;
            }

            return -1;
         } else {
            this.resize(this.capacity + RESIZE_COUNT);
            return this.allocateID();
         }
      }

      private void resize(int var1) {
         int var2 = this.capacity;
         this.capacity = Math.min(var1, MAX_ZOMBIES);
         this.capacity = Math.min(var1, 32767);
         this.idToZombie = (IsoZombie[])Arrays.copyOf(this.idToZombie, this.capacity);
         this.freeID = Arrays.copyOf(this.freeID, this.capacity);

         for(int var3 = var2; var3 < this.capacity; ++var3) {
            short[] var10000 = this.freeID;
            short var10003 = this.freeIDSize;
            this.freeIDSize = (short)(var10003 + 1);
            var10000[var10003] = (short)var3;
         }

      }
   }
}
