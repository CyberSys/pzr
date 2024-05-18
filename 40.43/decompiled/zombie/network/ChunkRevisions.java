package zombie.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.iso.CellLoader;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLot;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LotHeader;
import zombie.iso.RoomDef;
import zombie.iso.SliceY;
import zombie.vehicles.BaseVehicle;

public class ChunkRevisions {
   public static boolean USE_CHUNK_REVISIONS = false;
   public static ChunkRevisions instance;
   public Object FileLock = new Object();
   public static int UpdateArea = 30;
   static boolean debug = true;
   static byte[] CTBL = new byte[]{67, 84, 66, 76};
   static byte[] SQRE = new byte[]{83, 81, 82, 69};
   static byte[] BEEF = new byte[]{66, 69, 69, 70};
   static byte[] CARS = new byte[]{67, 65, 82, 83};
   public ChunkRevisions.Chunk[][] chunks;
   public int minX;
   public int minY;
   public int width;
   public int height;
   public ArrayList revisedSquares = new ArrayList();
   public ArrayList revisedSquares2 = new ArrayList();
   public ArrayList revisedChunks = new ArrayList();
   public int[] lastRequestX = new int[4];
   public int[] lastRequestY = new int[4];
   public IsoPlayer[] lastRequestInit = new IsoPlayer[4];
   private ArrayList deadPatchJobs = new ArrayList();
   public OnceEvery updateTimer = new OnceEvery(2.0F);
   public ExecutorService executor = Executors.newCachedThreadPool();
   public ChunkRevisions.ClientChunkRevisionRequest clientChunkRequest = null;
   private IsoPlayer[] AddCoopPlayerRequests = new IsoPlayer[4];
   public ArrayList serverChunkRequest = new ArrayList();

   private static void noise(String var0) {
      DebugLog.log("CHUNK: " + var0);
   }

   static void checkBytes(ByteBuffer var0, byte[] var1) {
      if (debug) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            byte var3 = var0.get();
            if (var3 != var1[var2] && var2 == 0) {
               noise("bytes don't match");
            }
         }

      }
   }

   public ChunkRevisions() {
      this.minX = IsoWorld.instance.MetaGrid.getMinX() * 30;
      this.minY = IsoWorld.instance.MetaGrid.getMinY() * 30;
      this.width = IsoWorld.instance.MetaGrid.getWidth() * 30;
      this.height = IsoWorld.instance.MetaGrid.getHeight() * 30;
      this.chunks = new ChunkRevisions.Chunk[this.width][this.height];

      for(int var1 = 0; var1 < this.height; ++var1) {
         for(int var2 = 0; var2 < this.width; ++var2) {
            this.chunks[var2][var1] = new ChunkRevisions.Chunk(this.minX + var2, this.minY + var1);
         }
      }

      ChunkRevisions.MemoryFile.test();
   }

   public ChunkRevisions.Chunk getChunk(int var1, int var2) {
      int var3 = var1 - this.minX;
      int var4 = var2 - this.minY;
      return var3 >= 0 && var3 < this.width && var4 >= 0 && var4 < this.height ? this.chunks[var3][var4] : null;
   }

   public void revisionUp(IsoGridSquare var1) {
      if (GameServer.bServer) {
         if (var1.chunk != null) {
            noise("square " + var1.getX() + "," + var1.getY() + "," + var1.getZ() + " revision " + var1.revision + " -> " + (var1.chunk.revision + 1L));
            var1.revision = ++var1.chunk.revision;
            if (!this.revisedSquares.contains(var1)) {
               this.revisedSquares.add(var1);
            }

         }
      }
   }

   public void clientPacket(short var1, ByteBuffer var2) {
      switch(var1) {
      case 3:
         byte var3 = var2.get();
         if (var3 == 0) {
            short var4 = var2.getShort();

            int var5;
            for(var5 = 0; var5 < var4; ++var5) {
               short var6 = var2.getShort();
               short var7 = var2.getShort();
               ChunkRevisions.Chunk var8 = this.getChunk(var6, var7);
               if (var8 != null) {
                  ++var8.serverUpdates;
               }
            }

            if (this.clientChunkRequest == null) {
               this.clientChunkRequest = new ChunkRevisions.ClientChunkRevisionRequest();

               for(var5 = 0; var5 < 4; ++var5) {
                  IsoPlayer var9 = IsoPlayer.players[var5];
                  if (var9 != null) {
                     this.clientChunkRequest.setArea(var5, (int)var9.getX() / 10 - UpdateArea / 2, (int)var9.getY() / 10 - UpdateArea / 2, UpdateArea, UpdateArea);
                  }
               }

               this.clientChunkRequest.start();
            }
         } else if (var3 == 1) {
            if (this.clientChunkRequest != null) {
               this.clientChunkRequest.acknowledge();
            }
         } else if (var3 == 2 && this.clientChunkRequest != null) {
            this.clientChunkRequest.emptyZip();
         }
      default:
      }
   }

   public void serverPacket(short var1, ByteBuffer var2, UdpConnection var3) {
      switch(var1) {
      case 3:
         this.receiveChunkRevisionRequest(var2, var3);
      default:
      }
   }

   public void updateClient() {
      int var1;
      if (this.clientChunkRequest != null) {
         var1 = this.clientChunkRequest.coopRequest;
         if (!this.clientChunkRequest.isFailed() && !this.clientChunkRequest.isFinished()) {
            if (this.clientChunkRequest.state == ChunkRevisions.ClientChunkRevisionRequest.State.RUNTHREAD && System.currentTimeMillis() - this.clientChunkRequest.threadStartTime > 200L) {
               noise("request thread start() glitch??? state=" + this.clientChunkRequest.state);
               this.clientChunkRequest = null;
            } else if (this.clientChunkRequest.thread != null && this.clientChunkRequest.thread.getState() == java.lang.Thread.State.TERMINATED) {
               noise("request thread terminated??? state=" + this.clientChunkRequest.state);
               this.clientChunkRequest = null;
            }
         } else {
            this.clientChunkRequest = null;
         }

         if (this.clientChunkRequest == null && var1 != -1) {
            noise("finished coop request player=" + var1 + "/" + 4);
            this.AddCoopPlayerRequests[var1] = null;
         }
      }

      IsoPlayer var2;
      int var3;
      int var4;
      if (this.clientChunkRequest == null) {
         for(var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
            var2 = IsoPlayer.players[var1];
            if (var2 != null) {
               var3 = (int)var2.getX() / 10;
               var4 = (int)var2.getY() / 10;
               if (this.lastRequestInit[var1] != var2) {
                  this.lastRequestInit[var1] = var2;
                  this.lastRequestX[var1] = var3;
                  this.lastRequestY[var1] = var4;
               }

               if (var3 != this.lastRequestX[var1] || var4 != this.lastRequestY[var1]) {
                  if (this.clientChunkRequest == null) {
                     this.clientChunkRequest = new ChunkRevisions.ClientChunkRevisionRequest();
                  }

                  this.clientChunkRequest.setArea(var1, var3 - UpdateArea / 2, var4 - UpdateArea / 2, UpdateArea, UpdateArea);
                  this.lastRequestX[var1] = var3;
                  this.lastRequestY[var1] = var4;
               }
            }
         }

         if (this.clientChunkRequest != null) {
            this.clientChunkRequest.start();
         }
      }

      if (this.clientChunkRequest == null) {
         for(var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
            var2 = this.AddCoopPlayerRequests[var1];
            if (var2 != null) {
               var3 = (int)var2.getX() / 10;
               var4 = (int)var2.getY() / 10;
               noise("starting coop request player=" + var1 + "/" + 4);
               this.clientChunkRequest = new ChunkRevisions.ClientChunkRevisionRequest(var1, var3 - UpdateArea / 2, var4 - UpdateArea / 2, UpdateArea, UpdateArea);
               this.clientChunkRequest.coopRequest = var1;
               this.clientChunkRequest.start();
               break;
            }
         }
      }

   }

   public void updateServer() {
      for(int var1 = 0; var1 < this.serverChunkRequest.size(); ++var1) {
         ChunkRevisions.ServerChunkRevisionRequest var2 = (ChunkRevisions.ServerChunkRevisionRequest)this.serverChunkRequest.get(var1);
         if (!var2.isFailed() && !var2.isFinished()) {
            if (var2.state == ChunkRevisions.ServerChunkRevisionRequest.State.RUNTHREAD && System.currentTimeMillis() - var2.threadStartTime > 200L) {
               noise("request thread start() glitch??? state=" + var2.state);
               ChunkRevisions.ServerChunkRevisionRequestInfo.release(var2.chunks);
               this.serverChunkRequest.remove(var1--);
            } else if (var2.thread != null && var2.thread.getState() == java.lang.Thread.State.TERMINATED) {
               noise("request thread terminated??? state=" + var2.state);
               ChunkRevisions.ServerChunkRevisionRequestInfo.release(var2.chunks);
               this.serverChunkRequest.remove(var1--);
            }
         } else {
            ChunkRevisions.ServerChunkRevisionRequestInfo.release(var2.chunks);
            this.serverChunkRequest.remove(var1--);
         }
      }

      if (this.updateTimer.Check()) {
         this.processRevisedSquares();
      }

   }

   public void patchChunkIfNeeded(IsoChunk var1) {
      if (GameClient.bClient) {
         if (System.currentTimeMillis() - var1.modificationTime >= 20000L) {
            ChunkRevisions.Chunk var2 = this.getChunk(var1.wx, var1.wy);
            if (var2 != null) {
               if (var2.patchJob != null) {
                  if (var2.patchJob.status == -1) {
                     var2.patchJob.release();
                     var2.patchJob = null;
                  } else if (var2.patchJob.status == 1) {
                     var2.patchJob.patch();
                     var2.patchJob.release();
                     var2.patchJob = null;
                  }
               } else {
                  if (var1.revision < var2.patchRevision) {
                     var2.patchJob = new ChunkRevisions.PatchJob(var1);
                     instance.executor.submit(var2.patchJob);
                  }

               }
            }
         }
      }
   }

   public void chunkRemovedFromWorld(IsoChunk var1) {
      ChunkRevisions.Chunk var2 = this.getChunk(var1.wx, var1.wy);
      if (var2 != null) {
         if (var2.patchJob != null) {
            if (var2.patchJob.status != 0) {
               var2.patchJob.release();
            } else {
               this.deadPatchJobs.add(var2.patchJob);
            }

            var2.patchJob = null;
         }

      }
   }

   public void processRevisedSquares() {
      if (!this.revisedSquares.isEmpty()) {
         this.revisedChunks.clear();

         while(!this.revisedSquares.isEmpty()) {
            IsoGridSquare var1 = (IsoGridSquare)this.revisedSquares.get(0);
            this.revisedSquares2.clear();

            for(int var2 = 0; var2 < this.revisedSquares.size(); ++var2) {
               IsoGridSquare var3 = (IsoGridSquare)this.revisedSquares.get(var2);
               if (var3.chunk == var1.chunk) {
                  this.revisedSquares2.add(var3);
                  this.revisedSquares.remove(var2--);
               }
            }

            noise(this.revisedSquares2.size() + " squares revised @ " + var1.chunk.wx + "," + var1.chunk.wy);
            ChunkRevisions.ChunkRevisionFile var8 = new ChunkRevisions.ChunkRevisionFile(var1.chunk.wx, var1.chunk.wy);
            if (var8.addSquares(this.revisedSquares2)) {
               this.revisedChunks.add(var1.chunk);
            }

            ChunkRevisions.Chunk var9 = this.getChunk(var1.chunk.wx, var1.chunk.wy);
            if (var9 != null) {
               synchronized(var9) {
                  var9.patchRevision = -1L;
               }
            }
         }

         this.revisedSquares.clear();

         for(int var7 = 0; var7 < GameServer.udpEngine.connections.size(); ++var7) {
            UdpConnection var10 = (UdpConnection)GameServer.udpEngine.connections.get(var7);
            ByteBufferWriter var11 = var10.startPacket();
            PacketTypes.doPacket((short)3, var11);
            var11.putByte((byte)0);
            var11.putShort((short)this.revisedChunks.size());

            for(int var4 = 0; var4 < this.revisedChunks.size(); ++var4) {
               var11.putShort((short)((IsoChunk)this.revisedChunks.get(var4)).wx);
               var11.putShort((short)((IsoChunk)this.revisedChunks.get(var4)).wy);
            }

            var10.endPacket();
         }

      }
   }

   public void loadChunkRevision(int var1, int var2) {
      ByteBuffer var3 = ChunkRevisions.Buffers.get();

      try {
         ChunkRevisions.Chunk var4 = this.getChunk(var1, var2);
         if (var4 == null) {
            return;
         }

         synchronized(var4) {
            var4.chunkRandomID = 0;
            IsoChunk var6;
            if (GameServer.bServer) {
               var6 = ServerMap.instance.getChunk(var1, var2);
               if (var6 != null) {
                  var4.chunkRandomID = var6.randomID;
               }
            } else {
               IsoChunkMap.bSettingChunk.lock();
               var6 = IsoWorld.instance.CurrentCell.getChunkForGridSquare(var1 * 10, var2 * 10, 0);
               if (var6 != null) {
                  var4.chunkRandomID = var6.randomID;
               }

               IsoChunkMap.bSettingChunk.unlock();
            }

            if (var4.fileRevision == -1L) {
               ChunkRevisions.ChunkFile var13 = new ChunkRevisions.ChunkFile(var1, var2);
               if (var13.loadChunkRevision(var3)) {
                  var4.fileRandomID = var13.randomID;
                  var4.fileRevision = var13.revision;
               } else {
                  var4.fileRandomID = 0;
                  var4.fileRevision = -1L;
               }
            }

            if (var4.patchRevision == -1L) {
               ChunkRevisions.ChunkRevisionFile var14 = new ChunkRevisions.ChunkRevisionFile(var1, var2);
               if (var14.loadChunkRevision(var3)) {
                  var4.patchRandomID = var14.randomID;
                  var4.patchRevision = var14.revision;
               } else {
                  var4.patchRandomID = 0;
                  var4.patchRevision = -1L;
               }
            }
         }
      } finally {
         ChunkRevisions.Buffers.release(var3);
      }

   }

   public void requestStartupChunkRevisions(int var1, int var2, int var3, int var4) {
      if (this.clientChunkRequest != null) {
         noise("already have a request");
      } else {
         this.clientChunkRequest = new ChunkRevisions.ClientChunkRevisionRequest(0, var1, var2, var3, var4);
         this.clientChunkRequest.timeout = 20000;
         this.clientChunkRequest.start();

         while(!this.clientChunkRequest.isFailed() && !this.clientChunkRequest.isFinished()) {
            try {
               Thread.sleep(200L);
            } catch (InterruptedException var6) {
               var6.printStackTrace();
            }
         }

         this.clientChunkRequest = null;
      }
   }

   public void requestCoopStartupChunkRevisions(IsoPlayer var1) {
      this.AddCoopPlayerRequests[var1.PlayerIndex] = var1;
   }

   public boolean isCoopRequestComplete(IsoPlayer var1) {
      return this.AddCoopPlayerRequests[var1.PlayerIndex] == null;
   }

   public void receiveChunkRevisionRequest(ByteBuffer var1, UdpConnection var2) {
      try {
         ArrayList var3 = new ArrayList();

         for(int var4 = 0; var4 < this.serverChunkRequest.size(); ++var4) {
            if (((ChunkRevisions.ServerChunkRevisionRequest)this.serverChunkRequest.get(var4)).connection == var2) {
               noise(var2.username + " request ignored because another request exists for this client");
               return;
            }
         }

         short var13 = var1.getShort();

         for(int var5 = 0; var5 < var13; ++var5) {
            short var6 = var1.getShort();
            short var7 = var1.getShort();
            int var8 = var1.getInt();
            long var9 = var1.getLong();
            ChunkRevisions.ServerChunkRevisionRequestInfo var11 = ChunkRevisions.ServerChunkRevisionRequestInfo.get();
            var11.wx = var6;
            var11.wy = var7;
            var11.randomID = var8;
            var11.revision = var9;
            var3.add(var11);
         }

         noise(var2.username + " requested " + var3.size() + " chunks to be checked (" + var1.position() + " bytes)");
         this.serverChunkRequest.add(new ChunkRevisions.ServerChunkRevisionRequest(var2, var3));
      } catch (Exception var12) {
         var12.printStackTrace();
      }

   }

   private static class MemoryFile {
      private static final int BLOCK_SIZE = 1024;
      private byte[] buf;
      private int position;
      private int size;

      private MemoryFile() {
      }

      public ChunkRevisions.MemoryFile position(int var1) {
         if (var1 <= this.size && var1 >= 0) {
            this.position = var1;
            return this;
         } else {
            throw new IllegalArgumentException();
         }
      }

      public int read(byte[] var1, int var2, int var3) {
         int var4 = this.bytesToRead(this.position, var3);
         if (var4 == -1) {
            return var4;
         } else {
            System.arraycopy(this.buf, this.position, var1, var2, var4);
            return var4;
         }
      }

      public int write(byte[] var1, int var2, int var3) {
         this.resize(this.position + var3);
         System.arraycopy(var1, var2, this.buf, this.position, var3);
         this.size = Math.max(this.size, this.position + var3);
         this.position += var3;
         return var3;
      }

      public int length() {
         return this.size;
      }

      private void resize(int var1) {
         int var2 = 1024 * ((var1 + 1024 - 1) / 1024);
         if (this.buf == null || var1 > this.buf.length) {
            byte[] var3 = new byte[var2];
            if (this.buf != null) {
               System.arraycopy(this.buf, 0, var3, 0, this.size);
            }

            this.buf = var3;
         }
      }

      private int bytesToRead(int var1, int var2) {
         int var3 = this.size - var1;
         return var3 <= 0 ? -1 : Math.min(var3, var2);
      }

      public static void test() {
         ChunkRevisions.MemoryFile var0 = new ChunkRevisions.MemoryFile();
         ChunkRevisions.MemoryFile var1 = new ChunkRevisions.MemoryFile();
         ByteBuffer var2 = ChunkRevisions.Buffers.get();
         var2.rewind();
         var2.putInt(1234);
         var2.putInt(5678);
         var0.write(var2.array(), 0, var2.position());
         ChunkRevisions.Buffers.release(var2);
         var2 = ChunkRevisions.Buffers.get();
         var2.rewind();
         var0.position(0);
         var0.read(var2.array(), 0, var2.limit());
         var1.write(var2.array(), 4, 4);
         var1.write(var2.array(), 0, 4);
         var1.position(0);
         var1.read(var2.array(), 0, var2.limit());
         int var4 = var2.getInt();
         int var5 = var2.getInt();
         ChunkRevisions.Buffers.release(var2);
      }

      // $FF: synthetic method
      MemoryFile(Object var1) {
         this();
      }
   }

   private static class PatchJob implements Runnable {
      public int status = 0;
      public IsoChunk chunk;
      public IsoChunk resetChunk;
      public ByteBuffer bb;
      public ChunkRevisions.ChunkTable chunkTable;
      public ChunkRevisions.ChunkRevisionFile revFile;
      public short worldVersion;

      public PatchJob(IsoChunk var1) {
         this.chunk = var1;
      }

      public void release() {
         if (this.chunkTable != null) {
            ChunkRevisions.ChunkTableEntry.release(this.chunkTable.entries);
            ChunkRevisions.ChunkTable.release(this.chunkTable);
            this.chunkTable = null;
         }

         if (this.bb != null) {
            ChunkRevisions.Buffers.release(this.bb);
            this.bb = null;
         }

      }

      private void fail() {
         this.status = -1;
      }

      private void succeed() {
         this.status = 1;
      }

      public void run() {
         this.bb = ChunkRevisions.Buffers.get();
         this.chunkTable = ChunkRevisions.ChunkTable.get();
         this.revFile = new ChunkRevisions.ChunkRevisionFile(this.chunk.wx, this.chunk.wy);

         try {
            if (this.revFile.read(this.bb.array()) == -1) {
               this.fail();
               return;
            }

            this.bb.rewind();
            this.worldVersion = this.bb.getShort();
            this.revFile.randomID = this.bb.getInt();
            this.revFile.revision = this.bb.getLong();
            if (!this.chunkTable.read(this.bb)) {
               this.fail();
               return;
            }

            if (this.revFile.randomID != this.chunk.randomID) {
               this.resetChunk = new IsoChunk(IsoWorld.instance.CurrentCell);
               if (!CellLoader.LoadCellBinaryChunk(IsoWorld.instance.CurrentCell, this.chunk.wx, this.chunk.wy, this.resetChunk)) {
                  this.fail();
                  return;
               }
            }

            this.succeed();
         } catch (Exception var2) {
            var2.printStackTrace();
            this.fail();
         }

      }

      public boolean patch() {
         this.status = 2;

         try {
            if (GameClient.bClient && this.chunk.randomID == 0) {
               this.chunk.randomID = this.revFile.randomID;
            }

            int var1;
            if (this.resetChunk != null) {
               ChunkRevisions.noise("randomID mismatch, resetting chunk " + this.chunk.wx + "," + this.chunk.wy + " before patching (was " + this.chunk.randomID + " now " + this.revFile.randomID + ")");
               this.chunk.randomID = this.revFile.randomID;
               IsoChunkMap.bSettingChunk.lock();

               try {
                  this.chunk.removeFromWorld();
                  this.chunk.doReuseGridsquares();
                  this.chunk.getErosionData().init = false;

                  for(var1 = 0; var1 < 8; ++var1) {
                     for(int var2 = 0; var2 < 10; ++var2) {
                        for(int var3 = 0; var3 < 10; ++var3) {
                           this.chunk.setSquare(var3, var2, var1, this.resetChunk.getGridSquare(var3, var2, var1));
                        }
                     }
                  }

                  this.chunk.setCacheIncludingNull();
                  this.chunk.updateBuildings();
                  this.chunk.recalcNeighboursNow();
                  this.chunk.doLoadGridsquare();
               } finally {
                  IsoChunkMap.bSettingChunk.unlock();
               }
            }

            ChunkRevisions.noise("patching " + this.chunk.wx + "," + this.chunk.wy + " randomID=" + this.chunk.randomID + " from revision " + this.chunk.revision + " to " + this.revFile.revision + " with " + this.chunkTable.entries.size() + " squares");

            ChunkRevisions.ChunkTableEntry var14;
            IsoGridSquare var15;
            try {
               for(var1 = 0; var1 < this.chunkTable.entries.size(); ++var1) {
                  var14 = (ChunkRevisions.ChunkTableEntry)this.chunkTable.entries.get(var1);
                  var15 = this.chunk.getGridSquare(var14.x, var14.y, var14.z);
                  int var4;
                  IsoObject var5;
                  if (var15 == null) {
                     var15 = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, (SliceY)null, this.chunk.wx * 10 + var14.x, this.chunk.wy * 10 + var14.y, var14.z);
                     var15.chunk = this.chunk;
                  } else {
                     for(var4 = var15.getObjects().size() - 1; var4 >= 0; --var4) {
                        var5 = (IsoObject)var15.getObjects().get(var4);
                        var5.removeFromWorld();
                        var5.removeFromSquare();
                     }

                     for(var4 = var15.getStaticMovingObjects().size() - 1; var4 >= 0; --var4) {
                        IsoMovingObject var17 = (IsoMovingObject)var15.getStaticMovingObjects().get(var4);
                        var17.removeFromWorld();
                        var17.removeFromSquare();
                     }
                  }

                  var15.revision = var14.revision;
                  if (this.chunk.lotheader != null) {
                     RoomDef var16 = IsoWorld.instance.getMetaChunkFromTile(var15.x, var15.y).getRoomAt(var15.x, var15.y, var15.z);
                     int var18 = var16 != null ? var16.ID : -1;
                     var15.setRoomID(var18);
                  }

                  var15.ResetMasterRegion();
                  ChunkRevisions.checkBytes(this.bb, ChunkRevisions.SQRE);
                  var15.load(this.bb, this.worldVersion);
                  if (this.bb.position() != var14.position + var14.length) {
                     ChunkRevisions.noise("***** square didn't read as much as it wrote");
                  }

                  this.chunk.setSquare(var14.x, var14.y, var14.z, var15);

                  for(var4 = 0; var4 < var15.getObjects().size(); ++var4) {
                     var5 = (IsoObject)var15.getObjects().get(var4);
                     var5.addToWorld();
                  }
               }
            } catch (Exception var11) {
               var11.printStackTrace();
               return false;
            }

            this.chunk.revision = this.revFile.revision;

            for(var1 = 0; var1 < this.chunkTable.entries.size(); ++var1) {
               var14 = (ChunkRevisions.ChunkTableEntry)this.chunkTable.entries.get(var1);
               var15 = this.chunk.getGridSquare(var14.x, var14.y, var14.z);
               var15.RecalcAllWithNeighbours(true);
            }

            try {
               LuaEventManager.triggerEvent("OnContainerUpdate", this);
            } catch (Exception var10) {
               var10.printStackTrace();
            }

            return true;
         } catch (Exception var13) {
            var13.printStackTrace();
            return false;
         }
      }
   }

   public static class ChunkRevisionFile {
      public int wx;
      public int wy;
      public String fileName;
      public ChunkRevisions.MemoryFile memoryFile;
      public int randomID;
      public long revision;

      public ChunkRevisionFile(int var1, int var2) {
         this.wx = var1;
         this.wy = var2;
         this.fileName = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_" + var1 + "_" + var2 + "_rev.bin";
         if (GameServer.bServer) {
            ChunkRevisions.Chunk var3 = ChunkRevisions.instance.getChunk(var1, var2);
            if (var3 != null) {
               synchronized(var3) {
                  if (var3.memoryFile == null) {
                     var3.memoryFile = new ChunkRevisions.MemoryFile();
                  }

                  this.memoryFile = var3.memoryFile;
               }
            }
         }

      }

      public boolean exists() {
         synchronized(ChunkRevisions.instance.FileLock) {
            if (this.memoryFile != null) {
               return true;
            }
         }

         return (new File(this.fileName)).exists();
      }

      private int read(byte[] var1, int var2, int var3) {
         if (!this.exists()) {
            return -1;
         } else {
            int var4 = -1;
            if (this.memoryFile != null) {
               synchronized(ChunkRevisions.instance.FileLock) {
                  try {
                     this.memoryFile.position(0);
                     var4 = this.memoryFile.read(var1, var2, var3);
                  } catch (Exception var9) {
                     var9.printStackTrace();
                  }

                  return var4;
               }
            } else {
               synchronized(ChunkRevisions.instance.FileLock) {
                  try {
                     FileInputStream var6 = new FileInputStream(this.fileName);
                     var4 = var6.read(var1, var2, var3);
                     var6.close();
                  } catch (Exception var11) {
                     var11.printStackTrace();
                  }

                  return var4;
               }
            }
         }
      }

      private int read(byte[] var1) {
         return this.read(var1, 0, var1.length);
      }

      private boolean write(byte[] var1, int var2, int var3) {
         boolean var10000;
         if (this.memoryFile != null) {
            synchronized(ChunkRevisions.instance.FileLock) {
               try {
                  this.memoryFile.position(0);
                  this.memoryFile.write(var1, var2, var3);
                  var10000 = true;
               } catch (Exception var8) {
                  var8.printStackTrace();
                  return false;
               }

               return var10000;
            }
         } else {
            synchronized(ChunkRevisions.instance.FileLock) {
               try {
                  FileOutputStream var5 = new FileOutputStream(this.fileName);
                  var5.write(var1, var2, var3);
                  var5.close();
                  var10000 = true;
               } catch (Exception var10) {
                  var10.printStackTrace();
                  return false;
               }

               return var10000;
            }
         }
      }

      public boolean loadChunkRevision(ByteBuffer var1) {
         var1.rewind();
         if (this.read(var1.array(), 0, 128) == -1) {
            return false;
         } else {
            short var2 = var1.getShort();
            this.randomID = var1.getInt();
            this.revision = var1.getLong();
            return true;
         }
      }

      public ChunkRevisions.ChunkTable loadChunkTable(ByteBuffer var1, long var2) {
         var1.rewind();
         if (this.read(var1.array()) == -1) {
            return null;
         } else {
            short var4 = var1.getShort();
            int var5 = var1.getInt();
            long var6 = var1.getLong();
            ChunkRevisions.ChunkTable var8 = ChunkRevisions.ChunkTable.get();
            if (!var8.read(var1, var2, var6)) {
               ChunkRevisions.ChunkTable.release(var8);
               return null;
            } else {
               return var8;
            }
         }
      }

      public boolean addNewRevisions(int var1, ByteBuffer var2) {
         ByteBuffer var3 = ChunkRevisions.Buffers.get();
         ByteBuffer var4 = ChunkRevisions.Buffers.get();
         ChunkRevisions.ChunkTable var5 = ChunkRevisions.ChunkTable.get();
         ChunkRevisions.ChunkTable var6 = ChunkRevisions.ChunkTable.get();
         ChunkRevisions.ChunkTable var7 = ChunkRevisions.ChunkTable.get();

         boolean var13;
         try {
            boolean var8 = true;
            short var9;
            int var10;
            if (this.read(var3.array()) != -1) {
               var3.rewind();
               var9 = var3.getShort();
               var10 = var3.getInt();
               var8 = var9 != 143 || var10 != var1;
            }

            var3.rewind();
            if (var8) {
               var3.putShort((short)143);
               var3.putInt(var1);
               var3.putLong(0L);
               if (ChunkRevisions.debug) {
                  var3.put(ChunkRevisions.CTBL);
               }

               var3.putShort((short)0);
               var3.rewind();
            }

            var9 = var3.getShort();
            var10 = var3.getInt();
            long var11 = var3.getLong();
            if (var5.read(var3)) {
               if (!var6.read(var2)) {
                  var13 = false;
                  return var13;
               }

               var7.merge(var5, var6);
               long var20 = var7.entries.isEmpty() ? 0L : ((ChunkRevisions.ChunkTableEntry)var7.entries.get(0)).revision;
               var4.rewind();
               var4.putShort(var9);
               var4.putInt(var1);
               var4.putLong(var20);
               var7.write(var4);

               for(int var15 = 0; var15 < var7.entries.size(); ++var15) {
                  ChunkRevisions.ChunkTableEntry var16 = (ChunkRevisions.ChunkTableEntry)var7.entries.get(var15);
                  if (var5.entries.contains(var16)) {
                     var4.put(var3.array(), var16.position, var16.length);
                  } else {
                     var4.put(var2.array(), var16.position, var16.length);
                  }
               }

               if (!this.write(var4.array(), 0, var4.position())) {
                  boolean var21 = false;
                  return var21;
               }

               ChunkRevisions.noise("patch-file " + this.wx + "," + this.wy + " randomID=" + var1 + " (was " + (var8 ? 0 : var10) + ") revision " + var11 + " -> " + var20 + ", #squares " + var5.entries.size() + " -> " + var7.entries.size());
               this.randomID = var1;
               this.revision = var20;
               return true;
            }

            var13 = false;
         } finally {
            if (var5 != null) {
               ChunkRevisions.ChunkTableEntry.release(var5.entries);
               ChunkRevisions.ChunkTable.release(var5);
            }

            if (var6 != null) {
               ChunkRevisions.ChunkTableEntry.release(var6.entries);
               ChunkRevisions.ChunkTable.release(var6);
            }

            ChunkRevisions.ChunkTable.release(var7);
            ChunkRevisions.Buffers.release(var3);
            ChunkRevisions.Buffers.release(var4);
         }

         return var13;
      }

      public boolean addSquares(ArrayList var1) {
         ByteBuffer var2 = ChunkRevisions.Buffers.get();
         ByteBuffer var3 = ChunkRevisions.Buffers.get();
         ChunkRevisions.ChunkTable var4 = ChunkRevisions.ChunkTable.get();

         boolean var6;
         try {
            var2.rewind();

            int var5;
            for(var5 = 0; var5 < var1.size(); ++var5) {
               IsoGridSquare var15 = (IsoGridSquare)var1.get(var5);
               int var7 = var2.position();
               if (ChunkRevisions.debug) {
                  var2.put(ChunkRevisions.SQRE);
               }

               var15.save(var2, (ObjectOutputStream)null);
               ChunkRevisions.ChunkTableEntry var8 = ChunkRevisions.ChunkTableEntry.get();
               var8.x = (byte)(var15.x - var15.chunk.wx * 10);
               var8.y = (byte)(var15.y - var15.chunk.wy * 10);
               var8.z = (byte)var15.z;
               var8.position = var7;
               var8.length = (short)(var2.position() - var7);
               var8.revision = var15.revision;
               var4.entries.add(var8);
            }

            var3.rewind();
            var4.write(var3);

            for(var5 = 0; var5 < var4.entries.size(); ++var5) {
               ChunkRevisions.ChunkTableEntry var16 = (ChunkRevisions.ChunkTableEntry)var4.entries.get(var5);
               var3.put(var2.array(), var16.position, var16.length);
            }

            var3.rewind();
            boolean var14 = this.addNewRevisions(((IsoGridSquare)var1.get(0)).chunk.randomID, var3);
            return var14;
         } catch (Exception var12) {
            var12.printStackTrace();
            var6 = false;
         } finally {
            ChunkRevisions.ChunkTableEntry.release(var4.entries);
            ChunkRevisions.ChunkTable.release(var4);
            ChunkRevisions.Buffers.release(var2);
            ChunkRevisions.Buffers.release(var3);
         }

         return var6;
      }

      public boolean patchChunk(IsoChunk var1) {
         if (!GameClient.bClient) {
            return true;
         } else if (!this.exists()) {
            return true;
         } else {
            ByteBuffer var2 = ChunkRevisions.Buffers.get();
            ChunkRevisions.ChunkTable var3 = ChunkRevisions.ChunkTable.get();

            try {
               if (this.read(var2.array()) == -1) {
                  boolean var18 = false;
                  return var18;
               } else {
                  var2.rewind();
                  short var4 = var2.getShort();
                  int var5 = var2.getInt();
                  long var6 = var2.getLong();
                  boolean var19;
                  if (!var3.read(var2)) {
                     var19 = false;
                     return var19;
                  } else {
                     if (GameClient.bClient && var1.randomID == 0) {
                        var1.randomID = var5;
                     }

                     int var8;
                     if (var5 != var1.randomID) {
                        ChunkRevisions.noise("randomID mismatch, resetting chunk " + var1.wx + "," + var1.wy + " before patching (was " + var1.randomID + " now " + var5);
                        var1.randomID = var5;
                        var8 = 0;

                        while(true) {
                           if (var8 >= var1.squares.length) {
                              var1.getErosionData().init = false;
                              if (!CellLoader.LoadCellBinaryChunk(IsoWorld.instance.CurrentCell, this.wx, this.wy, var1)) {
                                 var19 = false;
                                 return var19;
                              }
                              break;
                           }

                           for(int var9 = 0; var9 < var1.squares[var8].length; ++var9) {
                              var1.squares[var8][var9] = null;
                           }

                           ++var8;
                        }
                     }

                     ChunkRevisions.noise("patching " + var1.wx + "," + var1.wy + " randomID=" + var1.randomID + " from revision " + var1.revision + " to " + var6 + " with " + var3.entries.size() + " squares");

                     try {
                        for(var8 = 0; var8 < var3.entries.size(); ++var8) {
                           ChunkRevisions.ChunkTableEntry var21 = (ChunkRevisions.ChunkTableEntry)var3.entries.get(var8);
                           IsoGridSquare var10 = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, (SliceY)null, this.wx * 10 + var21.x, this.wy * 10 + var21.y, var21.z);
                           var10.chunk = var1;
                           var10.revision = var21.revision;
                           if (var1.lotheader != null) {
                              RoomDef var11 = IsoWorld.instance.getMetaChunkFromTile(var10.x, var10.y).getRoomAt(var10.x, var10.y, var10.z);
                              int var12 = var11 != null ? var11.ID : -1;
                              var10.setRoomID(var12);
                           }

                           var10.ResetMasterRegion();
                           ChunkRevisions.checkBytes(var2, ChunkRevisions.SQRE);
                           var10.load(var2, var4);
                           if (var2.position() != var21.position + var21.length) {
                              ChunkRevisions.noise("***** square didn't read as much as it wrote");
                           }

                           IsoGridSquare var22 = var1.getGridSquare(var21.x, var21.y, var21.z);
                           if (var22 != null) {
                           }

                           var1.setSquare(var21.x, var21.y, var21.z, var10);
                        }
                     } catch (Exception var16) {
                        var16.printStackTrace();
                        boolean var20 = false;
                        return var20;
                     }

                     var1.revision = var6;
                     var19 = true;
                     return var19;
                  }
               }
            } finally {
               ChunkRevisions.ChunkTableEntry.release(var3.entries);
               ChunkRevisions.ChunkTable.release(var3);
               ChunkRevisions.Buffers.release(var2);
            }
         }
      }

      public boolean removeFile() {
         ChunkRevisions.Chunk var1 = ChunkRevisions.instance.getChunk(this.wx, this.wy);
         if (var1 != null) {
            synchronized(var1) {
               var1.patchRandomID = 0;
               var1.patchRevision = -1L;
            }
         }

         synchronized(ChunkRevisions.instance.FileLock) {
            boolean var10000;
            if (this.memoryFile != null) {
               try {
                  if (this.exists()) {
                     if (this.memoryFile.length() > 0) {
                        ChunkRevisions.noise("removing patch-file " + this.wx + "," + this.wy);
                     }

                     this.memoryFile = null;
                     if (var1 != null) {
                        synchronized(var1) {
                           var1.memoryFile = null;
                        }
                     }
                  }

                  var10000 = true;
               } catch (Exception var8) {
                  var8.printStackTrace();
                  return false;
               }

               return var10000;
            } else {
               try {
                  File var3 = new File(this.fileName);
                  if (var3.exists()) {
                     ChunkRevisions.noise("removing patch-file " + this.wx + "," + this.wy);
                     var10000 = var3.delete();
                     return var10000;
                  }

                  var10000 = true;
               } catch (Exception var9) {
                  var9.printStackTrace();
                  return false;
               }

               return var10000;
            }
         }
      }
   }

   public static class ChunkFile {
      public int wx;
      public int wy;
      public String fileName;
      public int randomID;
      public long revision;

      public ChunkFile(int var1, int var2) {
         this.wx = var1;
         this.wy = var2;
         this.fileName = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_" + var1 + "_" + var2 + "_new.bin";
      }

      public boolean saveChunk(IsoChunk var1) {
         ByteBuffer var2 = ChunkRevisions.Buffers.get();
         ByteBuffer var3 = ChunkRevisions.Buffers.get();
         ChunkRevisions.ChunkTable var4 = ChunkRevisions.ChunkTable.get();

         try {
            var2.rewind();

            int var9;
            ChunkRevisions.ChunkTableEntry var27;
            for(int var5 = 0; var5 < 8; ++var5) {
               for(int var6 = 0; var6 < 10; ++var6) {
                  for(int var7 = 0; var7 < 10; ++var7) {
                     IsoGridSquare var8 = var1.getGridSquare(var7, var6, var5);
                     if (var8 != null && var8.shouldSave()) {
                        try {
                           var9 = var2.position();
                           if (ChunkRevisions.debug) {
                              var2.put(ChunkRevisions.SQRE);
                           }

                           var8.save(var2, (ObjectOutputStream)null);
                           var27 = ChunkRevisions.ChunkTableEntry.get();
                           var27.x = (byte)var7;
                           var27.y = (byte)var6;
                           var27.z = (byte)var5;
                           var27.position = var9;
                           var27.length = (short)(var2.position() - var9);
                           var27.revision = var8.revision;
                           var4.entries.add(var27);
                        } catch (Exception var17) {
                           var17.printStackTrace();
                           boolean var10 = false;
                           return var10;
                        }
                     }
                  }
               }
            }

            var3.rewind();
            var3.putShort((short)143);
            var3.putInt(var1.randomID);
            var3.putLong(var1.revision);
            var4.write(var3);
            synchronized(ChunkRevisions.instance.FileLock) {
               try {
                  File var22 = new File(this.fileName);
                  FileOutputStream var25 = new FileOutputStream(var22);
                  BufferedOutputStream var26 = new BufferedOutputStream(var25);
                  var26.write(var3.array(), 0, var3.position());

                  for(var9 = 0; var9 < var4.entries.size(); ++var9) {
                     var27 = (ChunkRevisions.ChunkTableEntry)var4.entries.get(var9);
                     var26.write(var2.array(), var27.position, var27.length);
                  }

                  if (!GameClient.bClient) {
                     var3.rewind();
                     var3.put(ChunkRevisions.CARS);
                     var3.putShort((short)var1.vehicles.size());

                     for(var9 = 0; var9 < var1.vehicles.size(); ++var9) {
                        BaseVehicle var28 = (BaseVehicle)var1.vehicles.get(var9);
                        var3.put((byte)((int)var28.getX() - this.wx * 10));
                        var3.put((byte)((int)var28.getY() - this.wy * 10));
                        var3.put((byte)((int)var28.getZ()));
                        var28.save(var3);
                     }

                     var26.write(var3.array(), 0, var3.position());
                  }

                  var26.close();
               } catch (Exception var18) {
                  var18.printStackTrace();
                  boolean var24 = false;
                  return var24;
               }
            }

            ChunkRevisions.Chunk var21 = ChunkRevisions.instance.getChunk(var1.wx, var1.wy);
            if (var21 != null) {
               var21.fileRandomID = var1.randomID;
               var21.fileRevision = var1.revision;
            }

            boolean var23 = true;
            return var23;
         } finally {
            ChunkRevisions.ChunkTableEntry.release(var4.entries);
            ChunkRevisions.ChunkTable.release(var4);
            ChunkRevisions.Buffers.release(var2);
            ChunkRevisions.Buffers.release(var3);
         }
      }

      public IsoChunk loadChunk() {
         File var1 = new File(this.fileName);
         if (!var1.exists()) {
            return null;
         } else {
            ByteBuffer var2 = ChunkRevisions.Buffers.get();
            ChunkRevisions.ChunkTable var3 = ChunkRevisions.ChunkTable.get();

            try {
               synchronized(ChunkRevisions.instance.FileLock) {
                  BufferedInputStream var6;
                  try {
                     FileInputStream var5 = new FileInputStream(var1);
                     var6 = new BufferedInputStream(var5);
                     var2.rewind();
                     var6.read(var2.array());
                     var6.close();
                  } catch (Exception var26) {
                     var26.printStackTrace();
                     var6 = null;
                     return var6;
                  }
               }

               int var4 = this.wx * 10 / 300;
               int var29 = this.wy * 10 / 300;
               String var30 = var4 + "_" + var29 + ".lotheader";
               LotHeader var7 = null;
               if (IsoLot.InfoHeaders.containsKey(var30)) {
                  var7 = (LotHeader)IsoLot.InfoHeaders.get(var30);
               }

               IsoChunk var8 = new IsoChunk(IsoWorld.instance.CurrentCell);
               var8.wx = this.wx;
               var8.wy = this.wy;
               var8.lotheader = var7;
               short var9 = var2.getShort();
               int var10 = var2.getInt();
               long var11 = var2.getLong();
               var8.randomID = var10;
               var8.revision = var11;
               IsoChunk var32;
               if (!var3.read(var2)) {
                  var32 = null;
                  return var32;
               } else {
                  ChunkRevisions.ChunkTableEntry var14;
                  try {
                     for(int var13 = 0; var13 < var3.entries.size(); ++var13) {
                        var14 = (ChunkRevisions.ChunkTableEntry)var3.entries.get(var13);
                        IsoGridSquare var15 = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, (SliceY)null, this.wx * 10 + var14.x, this.wy * 10 + var14.y, var14.z);
                        var15.chunk = var8;
                        var15.revision = var14.revision;
                        if (var7 != null) {
                           RoomDef var16 = IsoWorld.instance.getMetaChunkFromTile(var15.x, var15.y).getRoomAt(var15.x, var15.y, var15.z);
                           int var17 = var16 != null ? var16.ID : -1;
                           var15.setRoomID(var17);
                        }

                        var15.ResetMasterRegion();
                        var8.setSquare(var14.x, var14.y, var14.z, var15);
                        ChunkRevisions.checkBytes(var2, ChunkRevisions.SQRE);
                        var15.load(var2, var9);
                     }

                     if (!GameClient.bClient) {
                        ChunkRevisions.checkBytes(var2, ChunkRevisions.CARS);
                        var8.vehicles.clear();
                        short var31 = var2.getShort();

                        for(int var33 = 0; var33 < var31; ++var33) {
                           byte var34 = var2.get();
                           byte var35 = var2.get();
                           byte var36 = var2.get();
                           IsoObject var18 = IsoObject.factoryFromFileInput(IsoWorld.instance.CurrentCell, var2);
                           if (var18 != null && var18 instanceof BaseVehicle) {
                              IsoGridSquare var19 = var8.getGridSquare(var34, var35, var36);
                              var18.square = var19;
                              ((IsoMovingObject)var18).setCurrent(var19);
                              var18.load(var2, var9);
                              var8.vehicles.add((BaseVehicle)var18);
                              IsoChunk.addFromCheckedVehicles((BaseVehicle)var18);
                           }
                        }
                     }
                  } catch (Exception var25) {
                     var25.printStackTrace();
                     var14 = null;
                     return var14;
                  }

                  var32 = var8;
                  return var32;
               }
            } finally {
               ChunkRevisions.ChunkTableEntry.release(var3.entries);
               ChunkRevisions.ChunkTable.release(var3);
               ChunkRevisions.Buffers.release(var2);
            }
         }
      }

      public boolean loadChunkRevision(ByteBuffer var1) {
         File var2 = new File(this.fileName);
         if (!var2.exists()) {
            return false;
         } else {
            synchronized(ChunkRevisions.instance.FileLock) {
               try {
                  FileInputStream var4 = new FileInputStream(var2);
                  BufferedInputStream var5 = new BufferedInputStream(var4);
                  var1.rewind();
                  var5.read(var1.array(), 0, 128);
                  var5.close();
               } catch (Exception var7) {
                  var7.printStackTrace();
                  return false;
               }
            }

            short var3 = var1.getShort();
            this.randomID = var1.getInt();
            this.revision = var1.getLong();
            return true;
         }
      }

      public ChunkRevisions.ChunkTable loadChunkTable(ByteBuffer var1, long var2) {
         File var4 = new File(this.fileName);
         if (!var4.exists()) {
            return null;
         } else {
            synchronized(ChunkRevisions.instance.FileLock) {
               try {
                  FileInputStream var6 = new FileInputStream(var4);
                  BufferedInputStream var7 = new BufferedInputStream(var6);
                  var1.rewind();
                  var7.read(var1.array());
                  var7.close();
               } catch (Exception var10) {
                  var10.printStackTrace();
                  return null;
               }
            }

            short var5 = var1.getShort();
            int var12 = var1.getInt();
            long var13 = var1.getLong();
            ChunkRevisions.ChunkTable var9 = ChunkRevisions.ChunkTable.get();
            if (!var9.read(var1, var2, var13)) {
               ChunkRevisions.ChunkTable.release(var9);
               return null;
            } else {
               return var9;
            }
         }
      }
   }

   public static class ChunkTable {
      public ArrayList entries = new ArrayList();
      public static final ThreadLocal pool = new ThreadLocal() {
         protected Stack initialValue() {
            return new Stack();
         }
      };

      public boolean read(ByteBuffer var1) {
         ChunkRevisions.checkBytes(var1, ChunkRevisions.CTBL);
         short var2 = var1.getShort();
         this.entries.ensureCapacity(var2);

         try {
            if (var2 > 0) {
               int var3;
               for(var3 = 0; var3 < var2; ++var3) {
                  byte var4 = var1.get();
                  byte var5 = var1.get();
                  byte var6 = var1.get();
                  short var7 = var1.getShort();
                  long var8 = var1.getLong();
                  ChunkRevisions.ChunkTableEntry var10 = ChunkRevisions.ChunkTableEntry.get();
                  var10.x = var4;
                  var10.y = var5;
                  var10.z = var6;
                  var10.length = var7;
                  var10.revision = var8;
                  this.entries.add(var10);
               }

               ChunkRevisions.checkBytes(var1, ChunkRevisions.BEEF);
               var3 = var1.position();

               for(int var12 = 0; var12 < this.entries.size(); ++var12) {
                  ChunkRevisions.ChunkTableEntry var13 = (ChunkRevisions.ChunkTableEntry)this.entries.get(var12);
                  var13.position = var3;
                  var3 += var13.length;
               }
            }

            return true;
         } catch (Exception var11) {
            var11.printStackTrace();
            ChunkRevisions.ChunkTableEntry.release(this.entries);
            this.entries.clear();
            return false;
         }
      }

      public boolean read(ByteBuffer var1, long var2, long var4) {
         ChunkRevisions.checkBytes(var1, ChunkRevisions.CTBL);
         short var6 = var1.getShort();
         this.entries.ensureCapacity(var6);

         try {
            if (var6 > 0) {
               int var7 = var1.position();

               int var8;
               for(var8 = 0; var8 < var6; ++var8) {
                  byte var9 = var1.get();
                  byte var10 = var1.get();
                  byte var11 = var1.get();
                  short var12 = var1.getShort();
                  long var13 = var1.getLong();
                  if (var13 <= var2) {
                     break;
                  }

                  if (var13 != var2 || var2 >= var4) {
                     ChunkRevisions.ChunkTableEntry var15 = ChunkRevisions.ChunkTableEntry.get();
                     var15.x = var9;
                     var15.y = var10;
                     var15.z = var11;
                     var15.length = var12;
                     var15.revision = var13;
                     this.entries.add(var15);
                  }
               }

               var7 += var6 * 13;
               if (ChunkRevisions.debug) {
                  var7 += 4;
               }

               for(var8 = 0; var8 < this.entries.size(); ++var8) {
                  ChunkRevisions.ChunkTableEntry var17 = (ChunkRevisions.ChunkTableEntry)this.entries.get(var8);
                  var17.position = var7;
                  var7 += var17.length;
               }
            }

            return true;
         } catch (Exception var16) {
            var16.printStackTrace();
            ChunkRevisions.ChunkTableEntry.release(this.entries);
            this.entries.clear();
            return false;
         }
      }

      public boolean write(ByteBuffer var1) {
         if (ChunkRevisions.debug) {
            var1.put(ChunkRevisions.CTBL);
         }

         if (this.entries == null) {
            var1.putShort((short)0);
            return true;
         } else {
            var1.putShort((short)this.entries.size());
            if (!this.entries.isEmpty()) {
               Collections.sort(this.entries);

               for(int var2 = 0; var2 < this.entries.size(); ++var2) {
                  ChunkRevisions.ChunkTableEntry var3 = (ChunkRevisions.ChunkTableEntry)this.entries.get(var2);
                  var1.put(var3.x);
                  var1.put(var3.y);
                  var1.put(var3.z);
                  var1.putShort(var3.length);
                  var1.putLong(var3.revision);
               }

               if (ChunkRevisions.debug) {
                  var1.put(ChunkRevisions.BEEF);
               }
            }

            return true;
         }
      }

      public void merge(ChunkRevisions.ChunkTable var1, ChunkRevisions.ChunkTable var2) {
         HashMap var3 = new HashMap();

         int var4;
         ChunkRevisions.ChunkTableEntry var5;
         for(var4 = 0; var4 < var1.entries.size(); ++var4) {
            var5 = (ChunkRevisions.ChunkTableEntry)var1.entries.get(var4);
            var3.put(var5.x + "_" + var5.y + "_" + var5.z, var5);
         }

         for(var4 = 0; var4 < var2.entries.size(); ++var4) {
            var5 = (ChunkRevisions.ChunkTableEntry)var2.entries.get(var4);
            var3.put(var5.x + "_" + var5.y + "_" + var5.z, var5);
         }

         this.entries.addAll(var3.values());
         Collections.sort(this.entries);
      }

      public static ChunkRevisions.ChunkTable get() {
         Stack var0 = (Stack)pool.get();
         return var0.isEmpty() ? new ChunkRevisions.ChunkTable() : (ChunkRevisions.ChunkTable)var0.pop();
      }

      public static void release(ChunkRevisions.ChunkTable var0) {
         if (var0 != null) {
            assert !((Stack)pool.get()).contains(var0);

            var0.entries.clear();
            ((Stack)pool.get()).push(var0);
         }
      }
   }

   public static class ChunkTableEntry implements Comparable {
      public byte x;
      public byte y;
      public byte z;
      public int position;
      public short length;
      public long revision;
      public static final ThreadLocal pool = new ThreadLocal() {
         protected Stack initialValue() {
            return new Stack();
         }
      };

      public int compareTo(ChunkRevisions.ChunkTableEntry var1) {
         if (this.revision < var1.revision) {
            return 1;
         } else {
            return this.revision > var1.revision ? -1 : 0;
         }
      }

      public static ChunkRevisions.ChunkTableEntry get() {
         Stack var0 = (Stack)pool.get();
         return var0.isEmpty() ? new ChunkRevisions.ChunkTableEntry() : (ChunkRevisions.ChunkTableEntry)var0.pop();
      }

      public static void release(ChunkRevisions.ChunkTableEntry var0) {
         ((Stack)pool.get()).push(var0);
      }

      public static void release(ArrayList var0) {
         if (var0 != null) {
            ((Stack)pool.get()).addAll(var0);
         }
      }
   }

   public static class UploadFileToClient {
      public String fileName;
      public UdpConnection connection;
      public int port;
      public ServerSocket serverSocket;
      public Socket connectionSocket;
      public BufferedOutputStream outputStream;

      public UploadFileToClient(UdpConnection var1, int var2, String var3) {
         this.connection = var1;
         this.port = var2;
         this.fileName = var3;
      }

      public boolean init() {
         ChunkRevisions.noise("creating socket on port " + this.port);

         try {
            this.serverSocket = new ServerSocket();
            this.serverSocket.setSoTimeout(8000);
            this.serverSocket.setReuseAddress(true);
            this.serverSocket.bind(new InetSocketAddress(this.port));
            return true;
         } catch (Exception var2) {
            var2.printStackTrace();
            return false;
         }
      }

      public boolean connect() {
         if (this.serverSocket == null) {
            return false;
         } else {
            ChunkRevisions.noise("waiting for client to connect to upload port " + this.port);

            try {
               this.connectionSocket = this.serverSocket.accept();
               this.outputStream = new BufferedOutputStream(this.connectionSocket.getOutputStream());
               return true;
            } catch (SocketTimeoutException var2) {
               ChunkRevisions.noise(this.connection.username + ": " + var2.getMessage());
               return false;
            } catch (Exception var3) {
               var3.printStackTrace();
               return false;
            }
         }
      }

      public boolean upload() {
         if (this.outputStream == null) {
            return false;
         } else {
            ChunkRevisions.noise("uploading on port " + this.port);
            File var1 = new File(this.fileName);
            if (!var1.exists()) {
               return false;
            } else {
               int var2 = (int)var1.length();
               byte[] var3 = new byte[var2];
               FileInputStream var4 = null;

               label91: {
                  boolean var6;
                  try {
                     var4 = new FileInputStream(var1);
                     BufferedInputStream var5 = new BufferedInputStream(var4);
                     var2 = var5.read(var3);
                     break label91;
                  } catch (Exception var18) {
                     var18.printStackTrace();
                     var6 = false;
                  } finally {
                     if (var4 != null) {
                        try {
                           var4.close();
                        } catch (Exception var16) {
                           var16.printStackTrace();
                           return false;
                        }
                     }

                  }

                  return var6;
               }

               try {
                  this.outputStream.write(var2 >>> 24 & 255);
                  this.outputStream.write(var2 >>> 16 & 255);
                  this.outputStream.write(var2 >>> 8 & 255);
                  this.outputStream.write(var2 >>> 0 & 255);
                  this.outputStream.write(var3);
                  this.outputStream.flush();
                  this.outputStream.close();
                  this.connectionSocket.close();
                  return true;
               } catch (Exception var17) {
                  var17.printStackTrace();
                  return false;
               }
            }
         }
      }

      public void cleanup() {
         try {
            if (this.connectionSocket != null) {
               this.connectionSocket.close();
               this.connectionSocket = null;
               this.outputStream = null;
            }
         } catch (Exception var3) {
            var3.printStackTrace();
         }

         try {
            if (this.serverSocket != null) {
               this.serverSocket.close();
               this.serverSocket = null;
            }
         } catch (Exception var2) {
            var2.printStackTrace();
         }

      }
   }

   public static class ServerChunkRevisionRequest {
      public UdpConnection connection;
      public int port;
      public String fileName;
      public ArrayList chunks;
      public Thread thread;
      public ChunkRevisions.UploadFileToClient uftc;
      public long threadStartTime;
      public ChunkRevisions.ServerChunkRevisionRequest.State state;

      public ServerChunkRevisionRequest(UdpConnection var1, ArrayList var2) {
         this.state = ChunkRevisions.ServerChunkRevisionRequest.State.RUNTHREAD;
         this.connection = var1;
         this.port = var1.playerDownloadServer.port;
         this.fileName = GameWindow.getCacheDir() + File.separator + "tmp" + this.port + ".zip";
         this.chunks = var2;
         ChunkRevisions.instance.executor.submit(new Runnable() {
            public void run() {
               while(!ServerChunkRevisionRequest.this.isFailed() && !ServerChunkRevisionRequest.this.isFinished()) {
                  ServerChunkRevisionRequest.this.update();
               }

            }
         });
         this.threadStartTime = System.currentTimeMillis();
      }

      public void update() {
         switch(this.state) {
         case RUNTHREAD:
            this.state = ChunkRevisions.ServerChunkRevisionRequest.State.INIT;
            break;
         case INIT:
            this.createFile();
            break;
         case CREATEDFILE:
            this.uftc = new ChunkRevisions.UploadFileToClient(this.connection, this.port, this.fileName);

            try {
               if (!this.uftc.init()) {
                  this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
                  return;
               }

               this.acknowledge();
               if (!this.uftc.connect()) {
                  this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
                  return;
               }

               if (!this.uftc.upload()) {
                  this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
                  return;
               }
            } finally {
               this.uftc.cleanup();
            }

            this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FINISHED;
         case FINISHED:
         case FAILED:
         }

      }

      public void createFile() {
         ZipOutputStream var1 = null;
         boolean var2 = true;

         label263: {
            try {
               File var3 = new File(this.fileName);
               var1 = new ZipOutputStream(new FileOutputStream(var3));
               int var4 = 0;

               while(true) {
                  if (var4 >= this.chunks.size()) {
                     break label263;
                  }

                  ChunkRevisions.ServerChunkRevisionRequestInfo var5 = (ChunkRevisions.ServerChunkRevisionRequestInfo)this.chunks.get(var4);
                  ChunkRevisions.Chunk var6 = ChunkRevisions.instance.getChunk(var5.wx, var5.wy);
                  if (var6 != null) {
                     ChunkRevisions.instance.loadChunkRevision(var5.wx, var5.wy);
                     int var7 = 0;
                     long var8 = 0L;
                     synchronized(var6) {
                        if (var6.patchRevision != -1L) {
                           var7 = var6.patchRandomID;
                           var8 = var6.patchRevision;
                        } else if (var6.fileRevision != -1L) {
                           var7 = var6.fileRandomID;
                           var8 = var6.fileRevision;
                        } else if (var6.chunkRandomID != 0) {
                           var7 = var6.chunkRandomID;
                        }
                     }

                     if ((var7 != 0 || var5.randomID != 0) && (var7 != var5.randomID || var8 > var5.revision)) {
                        ByteBuffer var10 = ChunkRevisions.Buffers.get();

                        try {
                           var10.rewind();
                           int var11 = this.addChunkRevisionsToBuffer(var10, var6, var5);
                           if (var11 != -1) {
                              ChunkRevisions.noise("adding " + var11 + " squares from chunk " + var6.wx + "," + var6.wy + " randomID=" + var7 + " (client=" + var5.randomID + ") rev=" + var8 + " (client=" + var5.revision + ") to zip");
                              var1.putNextEntry(new ZipEntry(var6.wx + "_" + var6.wy));
                              var1.write(var10.array(), 0, var10.position());
                              var2 = false;
                           }
                        } finally {
                           ChunkRevisions.Buffers.release(var10);
                        }
                     }
                  }

                  ++var4;
               }
            } catch (Exception var30) {
               var30.printStackTrace();
               this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
            } finally {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (Exception var27) {
                     var27.printStackTrace();
                     this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
                     return;
                  }
               }

            }

            return;
         }

         if (var2) {
            this.notifyEmpty();
            ChunkRevisions.noise("nothing to send");
            this.state = ChunkRevisions.ServerChunkRevisionRequest.State.FINISHED;
         } else {
            this.state = ChunkRevisions.ServerChunkRevisionRequest.State.CREATEDFILE;
         }
      }

      private int addChunkRevisionsToBuffer(ByteBuffer var1, ChunkRevisions.Chunk var2, ChunkRevisions.ServerChunkRevisionRequestInfo var3) {
         ByteBuffer var4 = ChunkRevisions.Buffers.get();
         ByteBuffer var5 = ChunkRevisions.Buffers.get();
         ChunkRevisions.ChunkTable var6 = ChunkRevisions.ChunkTable.get();
         ChunkRevisions.ChunkTable var7 = ChunkRevisions.ChunkTable.get();
         ChunkRevisions.ChunkTable var8 = ChunkRevisions.ChunkTable.get();

         try {
            int var9 = 0;
            synchronized(var2) {
               if (var2.chunkRandomID != 0) {
                  var9 = var2.chunkRandomID;
               } else if (var2.fileRandomID != 0) {
                  var9 = var2.fileRandomID;
               }
            }

            if (var9 == 0 && var3.randomID == 0) {
               byte var23 = -1;
               return var23;
            } else {
               ChunkRevisions.ChunkFile var10 = new ChunkRevisions.ChunkFile(var2.wx, var2.wy);
               ChunkRevisions.ChunkRevisionFile var11 = new ChunkRevisions.ChunkRevisionFile(var2.wx, var2.wy);
               long var12 = var3.revision;
               if (var9 != var3.randomID) {
                  var12 = 0L;
               }

               var6 = var10.loadChunkTable(var4, var12);
               var7 = var11.loadChunkTable(var5, var12);
               if (var6 != null && var2.fileRandomID != var9) {
                  ChunkRevisions.ChunkTableEntry.release(var6.entries);
                  ChunkRevisions.ChunkTable.release(var6);
                  var6 = null;
               }

               if (var7 != null && var2.patchRandomID != var9) {
                  ChunkRevisions.ChunkTableEntry.release(var7.entries);
                  ChunkRevisions.ChunkTable.release(var7);
                  var7 = null;
               }

               if (var6 == null || var7 == null) {
                  if (var6 == null && var7 == null) {
                     var1.rewind();
                     var1.putInt(var9);
                     var8.write(var1);
                     byte var25 = -1;
                     return var25;
                  } else {
                     ChunkRevisions.ChunkTable var24 = var6 != null ? var6 : var7;
                     ByteBuffer var26 = var6 != null ? var4 : var5;
                     var1.rewind();
                     var1.putInt(var9);
                     var24.write(var1);

                     int var16;
                     for(var16 = 0; var16 < var24.entries.size(); ++var16) {
                        ChunkRevisions.ChunkTableEntry var17 = (ChunkRevisions.ChunkTableEntry)var24.entries.get(var16);
                        var1.put(var26.array(), var17.position, var17.length);
                     }

                     var16 = var24.entries.size();
                     return var16;
                  }
               } else {
                  var8.merge(var6, var7);
                  var1.rewind();
                  var1.putInt(var9);
                  var8.write(var1);

                  int var14;
                  for(var14 = 0; var14 < var8.entries.size(); ++var14) {
                     ChunkRevisions.ChunkTableEntry var15 = (ChunkRevisions.ChunkTableEntry)var8.entries.get(var14);
                     if (var6.entries.contains(var15)) {
                        var1.put(var4.array(), var15.position, var15.length);
                     } else {
                        var1.put(var5.array(), var15.position, var15.length);
                     }
                  }

                  var14 = var8.entries.size();
                  return var14;
               }
            }
         } finally {
            if (var6 != null) {
               ChunkRevisions.ChunkTableEntry.release(var6.entries);
               ChunkRevisions.ChunkTable.release(var6);
            }

            if (var7 != null) {
               ChunkRevisions.ChunkTableEntry.release(var7.entries);
               ChunkRevisions.ChunkTable.release(var7);
            }

            ChunkRevisions.ChunkTable.release(var8);
            ChunkRevisions.Buffers.release(var4);
            ChunkRevisions.Buffers.release(var5);
         }
      }

      public void acknowledge() {
         ByteBufferWriter var1 = this.connection.startPacket();
         PacketTypes.doPacket((short)3, var1);
         var1.putByte((byte)1);
         this.connection.endPacket();
      }

      public void notifyEmpty() {
         ByteBufferWriter var1 = this.connection.startPacket();
         PacketTypes.doPacket((short)3, var1);
         var1.putByte((byte)2);
         this.connection.endPacket();
      }

      public boolean isFinished() {
         return this.state == ChunkRevisions.ServerChunkRevisionRequest.State.FINISHED;
      }

      public boolean isFailed() {
         return this.state == ChunkRevisions.ServerChunkRevisionRequest.State.FAILED;
      }

      public static enum State {
         RUNTHREAD,
         INIT,
         CREATEDFILE,
         FINISHED,
         FAILED;
      }
   }

   public static class ServerChunkRevisionRequestInfo {
      short wx;
      short wy;
      int randomID;
      long revision;
      public static final ThreadLocal pool = new ThreadLocal() {
         protected Stack initialValue() {
            return new Stack();
         }
      };

      public static ChunkRevisions.ServerChunkRevisionRequestInfo get() {
         Stack var0 = (Stack)pool.get();
         return var0.isEmpty() ? new ChunkRevisions.ServerChunkRevisionRequestInfo() : (ChunkRevisions.ServerChunkRevisionRequestInfo)var0.pop();
      }

      public static void release(ChunkRevisions.ServerChunkRevisionRequestInfo var0) {
         ((Stack)pool.get()).push(var0);
      }

      public static void release(ArrayList var0) {
         if (var0 != null) {
            ((Stack)pool.get()).addAll(var0);
         }
      }
   }

   public static class DownloadFileFromServer {
      public UdpConnection connection;
      public int port;
      public String fileName;
      public Socket socket;
      public InputStream socketInputStream;
      public ByteBuffer data;

      public DownloadFileFromServer(UdpConnection var1, String var2) {
         this.connection = var1;
         this.fileName = var2;
      }

      public boolean connect() {
         ChunkRevisions.noise("connecting to download port");

         try {
            InetAddress var1 = this.connection.getInetSocketAddress().getAddress();
            this.socket = new Socket();
            if (this.connection.getInetSocketAddress().toString().contains("127.0.0.1")) {
               InetSocketAddress var2 = new InetSocketAddress("localhost", 111);
               InetAddress var3 = var2.getAddress();
               int var4 = Rand.Next(10000) + 23456;
               ChunkRevisions.noise("using random local port " + var4);
               this.socket.bind(new InetSocketAddress(var3, var4));
               this.socket.connect(new InetSocketAddress(var1, this.port), 8000);
            } else {
               this.socket.connect(new InetSocketAddress(var1, this.port), 8000);
            }

            this.socketInputStream = this.socket.getInputStream();
            return true;
         } catch (Exception var6) {
            var6.printStackTrace();
            if (this.socket != null) {
               try {
                  this.socket.close();
                  this.socket = null;
               } catch (Exception var5) {
                  var5.printStackTrace();
               }
            }

            return false;
         }
      }

      public boolean download() {
         ChunkRevisions.noise("downloading file");
         FileOutputStream var1 = null;
         BufferedOutputStream var2 = null;

         boolean var4;
         try {
            int var3 = this.socketInputStream.read();
            int var21 = this.socketInputStream.read();
            int var5 = this.socketInputStream.read();
            int var6 = this.socketInputStream.read();
            if ((var3 | var21 | var5 | var6) < 0) {
               throw new IOException();
            }

            int var7 = (var3 << 24) + (var21 << 16) + (var5 << 8) + (var6 << 0);
            if (this.fileName != null) {
               File var8 = new File(this.fileName);
               var1 = new FileOutputStream(var8.getAbsoluteFile());
               var2 = new BufferedOutputStream(var1, var7);
            } else {
               this.data = ByteBuffer.wrap(new byte[var7]);
            }

            int var22 = var7;

            int var10;
            for(byte[] var9 = new byte[1]; var22 > 0; var22 -= var10) {
               var10 = this.socketInputStream.read(var9);
               if (var10 == -1) {
                  throw new IOException();
               }

               if (this.fileName != null) {
                  var2.write(var9[0]);
               } else {
                  this.data.put(var9[0]);
               }
            }

            return true;
         } catch (Exception var19) {
            var19.printStackTrace();
            var4 = false;
         } finally {
            try {
               if (var2 != null) {
                  var2.flush();
                  var2.close();
               }
            } catch (Exception var18) {
               var18.printStackTrace();
               return false;
            }

         }

         return var4;
      }
   }

   public static class ClientChunkRevisionRequest {
      public UdpConnection connection;
      public String fileName;
      public int[] area = new int[16];
      public Thread thread;
      public int timeout = 8000;
      public long requestTime;
      public ArrayList chunks = new ArrayList();
      public int coopRequest = -1;
      public ChunkRevisions.ClientChunkRevisionRequest.State state;
      public long threadStartTime;

      public ClientChunkRevisionRequest() {
         this.state = ChunkRevisions.ClientChunkRevisionRequest.State.RUNTHREAD;
         this.connection = GameClient.connection;
      }

      public ClientChunkRevisionRequest(int var1, int var2, int var3, int var4, int var5) {
         this.state = ChunkRevisions.ClientChunkRevisionRequest.State.RUNTHREAD;
         this.connection = GameClient.connection;
         this.setArea(var1, var2, var3, var4, var5);
      }

      public void setArea(int var1, int var2, int var3, int var4, int var5) {
         this.area[var1 * 4 + 0] = var2;
         this.area[var1 * 4 + 1] = var3;
         this.area[var1 * 4 + 2] = var4;
         this.area[var1 * 4 + 3] = var5;
      }

      public void start() {
         ChunkRevisions.instance.executor.submit(new Runnable() {
            public void run() {
               while(!ClientChunkRevisionRequest.this.isFailed() && !ClientChunkRevisionRequest.this.isFinished()) {
                  ClientChunkRevisionRequest.this.update();
               }

            }
         });
         this.threadStartTime = System.currentTimeMillis();
      }

      public void update() {
         switch(this.state) {
         case RUNTHREAD:
            this.state = ChunkRevisions.ClientChunkRevisionRequest.State.INIT;
            break;
         case INIT:
            this.request();
            break;
         case REQUEST:
            try {
               Thread.sleep(10L);
            } catch (InterruptedException var2) {
            }

            if (System.currentTimeMillis() - this.requestTime > (long)this.timeout) {
               ChunkRevisions.noise("request timeout, aborting");
               this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FAILED;
            }
            break;
         case ACKNOWLEDGED:
            this.download();
         case DOWNLOAD:
         case FINISHED:
         case FAILED:
         }

      }

      public void request() {
         int var2;
         int var4;
         for(int var1 = 0; var1 < 4; ++var1) {
            var2 = this.area[var1 * 4 + 0];
            int var3 = this.area[var1 * 4 + 1];
            var4 = this.area[var1 * 4 + 2];
            int var5 = this.area[var1 * 4 + 3];

            for(int var6 = 0; var6 < var5; ++var6) {
               for(int var7 = 0; var7 < var4; ++var7) {
                  ChunkRevisions.Chunk var8 = ChunkRevisions.instance.getChunk(var2 + var7, var3 + var6);
                  if (var8 != null && !this.chunks.contains(var8) && var8.clientUpdates != var8.serverUpdates) {
                     var8.clientRequest = var8.serverUpdates;
                     ChunkRevisions.instance.loadChunkRevision(var2 + var7, var3 + var6);
                     this.chunks.add(var8);
                  }
               }
            }
         }

         if (this.chunks.isEmpty()) {
            ChunkRevisions.noise("no chunks need to be checked");
            this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FINISHED;
         } else {
            ByteBufferWriter var11 = this.connection.startPacket();
            PacketTypes.doPacket((short)3, var11);
            var11.putShort((short)this.chunks.size());

            for(var2 = 0; var2 < this.chunks.size(); ++var2) {
               ChunkRevisions.Chunk var12 = (ChunkRevisions.Chunk)this.chunks.get(var2);
               var4 = 0;
               long var13 = 0L;
               synchronized(var12) {
                  if (var12.chunkRandomID != 0) {
                     var4 = var12.chunkRandomID;
                  } else if (var12.patchRandomID != 0) {
                     var4 = var12.patchRandomID;
                  } else if (var12.fileRandomID != 0) {
                     var4 = var12.fileRandomID;
                  }

                  if (var12.patchRevision != -1L && var12.patchRandomID == var4) {
                     var13 = var12.patchRevision;
                  } else if (var12.fileRevision != -1L && var12.fileRandomID == var4) {
                     var13 = var12.fileRevision;
                  }
               }

               var11.putShort((short)var12.wx);
               var11.putShort((short)var12.wy);
               var11.putInt(var4);
               var11.putLong(var13);
            }

            this.connection.endPacket();
            ChunkRevisions.noise("requesting " + this.chunks.size() + " chunks to be checked");
            this.requestTime = System.currentTimeMillis();
            this.state = ChunkRevisions.ClientChunkRevisionRequest.State.REQUEST;
         }
      }

      public void acknowledge() {
         this.state = ChunkRevisions.ClientChunkRevisionRequest.State.ACKNOWLEDGED;
      }

      public void emptyZip() {
         ChunkRevisions.noise("nothing to download");

         for(int var1 = 0; var1 < this.chunks.size(); ++var1) {
            ((ChunkRevisions.Chunk)this.chunks.get(var1)).clientUpdates = ((ChunkRevisions.Chunk)this.chunks.get(var1)).clientRequest;
         }

         this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FINISHED;
      }

      public void download() {
         this.state = ChunkRevisions.ClientChunkRevisionRequest.State.DOWNLOAD;
         ChunkRevisions.DownloadFileFromServer var1 = new ChunkRevisions.DownloadFileFromServer(this.connection, (String)null);
         if (var1.connect() && var1.download()) {
            this.unzip(var1.data);
         } else {
            this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FAILED;
         }

      }

      public void unzip(ByteBuffer var1) {
         ChunkRevisions.noise("downloaded, now unzipping");
         ByteBuffer var2 = ChunkRevisions.Buffers.get();

         label99: {
            try {
               byte[] var3 = new byte[1024];
               ZipInputStream var4 = new ZipInputStream(new ByteArrayInputStream(var1.array()));

               for(ZipEntry var5 = var4.getNextEntry(); var5 != null; var5 = var4.getNextEntry()) {
                  String[] var6 = var5.getName().split("_");
                  int var7 = Integer.parseInt(var6[0]);
                  int var8 = Integer.parseInt(var6[1]);
                  var2.rewind();

                  int var9;
                  while((var9 = var4.read(var3)) > 0) {
                     var2.put(var3, 0, var9);
                  }

                  var2.rewind();
                  int var10 = var2.getInt();
                  ChunkRevisions.ChunkRevisionFile var11 = new ChunkRevisions.ChunkRevisionFile(var7, var8);
                  boolean var12 = var11.addNewRevisions(var10, var2);
                  if (var12) {
                     ChunkRevisions.Chunk var13 = ChunkRevisions.instance.getChunk(var7, var8);
                     var13.patchRandomID = var11.randomID;
                     var13.patchRevision = var11.revision;
                  } else {
                     ChunkRevisions.noise("***** failed to add new revisions");
                  }
               }

               var4.closeEntry();
               var4.close();
               int var19 = 0;

               while(true) {
                  if (var19 >= this.chunks.size()) {
                     break label99;
                  }

                  ((ChunkRevisions.Chunk)this.chunks.get(var19)).clientUpdates = ((ChunkRevisions.Chunk)this.chunks.get(var19)).clientRequest;
                  ++var19;
               }
            } catch (Exception var17) {
               var17.printStackTrace();
               this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FAILED;
            } finally {
               ChunkRevisions.Buffers.release(var2);
            }

            return;
         }

         this.state = ChunkRevisions.ClientChunkRevisionRequest.State.FINISHED;
      }

      public boolean isFinished() {
         return this.state == ChunkRevisions.ClientChunkRevisionRequest.State.FINISHED;
      }

      public boolean isFailed() {
         return this.state == ChunkRevisions.ClientChunkRevisionRequest.State.FAILED;
      }

      public static enum State {
         RUNTHREAD,
         INIT,
         REQUEST,
         ACKNOWLEDGED,
         DOWNLOAD,
         FINISHED,
         FAILED;
      }
   }

   public static class Buffers {
      public static Stack buffers = new Stack();

      public static synchronized ByteBuffer get() {
         ByteBuffer var0;
         if (buffers.isEmpty()) {
            var0 = ByteBuffer.allocate(102400);
         } else {
            var0 = (ByteBuffer)buffers.pop();
         }

         if (ChunkRevisions.debug) {
            for(int var1 = 0; var1 < var0.capacity(); ++var1) {
               var0.array()[var1] = 66;
            }
         }

         return var0;
      }

      public static synchronized void release(ByteBuffer var0) {
         buffers.push(var0);
      }
   }

   public class Chunk {
      public int wx;
      public int wy;
      public int chunkRandomID;
      public int fileRandomID;
      public long fileRevision = -1L;
      public int patchRandomID;
      public long patchRevision = -1L;
      public ChunkRevisions.MemoryFile memoryFile;
      public short serverUpdates = 1;
      public short clientRequest;
      public short clientUpdates;
      public ChunkRevisions.PatchJob patchJob;

      public Chunk(int var2, int var3) {
         this.wx = var2;
         this.wy = var3;
      }
   }
}
