package zombie.iso.areas.isoregion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;

public class IsoRegionWorker {
   private Thread thread;
   private boolean bFinished;
   protected static AtomicBoolean isRequestingBufferSwap = new AtomicBoolean(false);
   private static IsoRegionWorker instance;
   private DataRoot rootBuffer = new DataRoot();
   private List discoveredChunks = new ArrayList();
   private List threadDiscoveredChunks = new ArrayList();
   private int lastThreadDiscoveredChunksSize = 0;
   private final ConcurrentLinkedQueue jobQueue = new ConcurrentLinkedQueue();
   private final ConcurrentLinkedQueue jobOutgoingQueue = new ConcurrentLinkedQueue();
   private List jobProcessingList = new ArrayList();
   private final ConcurrentLinkedQueue finishedJobQueue = new ConcurrentLinkedQueue();
   protected static final int SINGLE_CHUNK_PACKET_SIZE = 1024;
   protected static final int CHUNKS_DATA_PACKET_SIZE = 65536;
   private static final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
   private String cacheDir;
   private File cacheDirFile;
   private File headDataFile;
   private final Map chunkFileNames = new HashMap();

   protected IsoRegionWorker() {
      instance = this;
   }

   protected void create() {
      if (this.thread == null) {
         this.bFinished = false;
         this.thread = new Thread(new Runnable() {
            public void run() {
               while(!IsoRegionWorker.this.bFinished) {
                  try {
                     IsoRegionWorker.this.threadRun();
                  } catch (Exception var2) {
                     var2.printStackTrace();
                  }
               }

            }
         });
         this.thread.setPriority(5);
         this.thread.setDaemon(true);
         this.thread.setName("IsoRegionWorker");
         this.thread.start();
      }
   }

   protected void stop() {
      if (this.thread != null) {
         if (this.thread != null) {
            this.bFinished = true;

            while(true) {
               if (!this.thread.isAlive()) {
                  this.thread = null;
                  break;
               }
            }
         }

         this.jobQueue.clear();
         this.jobOutgoingQueue.clear();
         this.jobProcessingList.clear();
         this.finishedJobQueue.clear();
         this.rootBuffer = null;
         this.discoveredChunks = null;
      }
   }

   protected void EnqueueJob(RegionJob var1) {
      this.jobQueue.add(var1);
   }

   protected void ApplyChunkChanges() {
      this.ApplyChunkChanges(true);
   }

   protected void ApplyChunkChanges(boolean var1) {
      RegionJob var2 = RegionJob.allocApplyChunkChanges(var1);
      this.jobQueue.add(var2);
   }

   protected static void EnqueueDirtyChunk(DataChunk var0) {
      instance.rootBuffer.EnqueueDirtyChunk(var0);
   }

   protected static void EnqueueDirtyMasterRegion(MasterRegion var0) {
      instance.rootBuffer.EnqueueDirtyMasterRegion(var0);
   }

   protected static void DequeueDirtyMasterRegion(MasterRegion var0) {
      instance.rootBuffer.DequeueDirtyMasterRegion(var0);
   }

   protected File getDirectory() {
      if (this.cacheDir == null) {
         String var1 = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "isoregiondata" + File.separator;
         File var2 = new File(var1);
         if (!var2.exists()) {
            var2.mkdir();
         }

         this.cacheDirFile = var2;
         this.cacheDir = var1;
      }

      return this.cacheDirFile;
   }

   protected File getChunkFile(int var1, int var2) {
      int var3 = IsoRegion.hash(var1, var2);
      if (this.chunkFileNames.containsKey(var3)) {
         return (File)this.chunkFileNames.get(var3);
      } else {
         if (this.cacheDir == null) {
            this.getDirectory();
         }

         String var4 = this.cacheDir + "datachunk_" + var1 + "_" + var2 + ".bin";
         File var5 = new File(var4);
         this.chunkFileNames.put(var3, var5);
         return var5;
      }
   }

   protected File getHeaderFile() {
      if (this.headDataFile == null) {
         if (this.cacheDir == null) {
            this.getDirectory();
         }

         String var1 = this.cacheDir + "RegionHeader" + ".bin";
         this.headDataFile = new File(var1);
      }

      return this.headDataFile;
   }

   private void threadRun() throws InterruptedException {
      for(RegionJob var1 = (RegionJob)this.jobQueue.poll(); var1 != null; var1 = (RegionJob)this.jobQueue.poll()) {
         switch(var1.getJobType()) {
         case ServerSendFullData:
            if (var1.getTargetConn() == null) {
               if (IsoRegion.PRINT_D) {
                  DebugLog.log("IsoRegion: Server send full data target connection == null");
               }
               break;
            }

            ArrayList var2 = new ArrayList();
            this.rootBuffer.getAllChunks(var2);
            RegionJob var3 = RegionJob.allocReadChunksPacket();
            Iterator var6 = var2.iterator();

            DataChunk var5;
            for(; var6.hasNext(); var3.addChunkFromDataChunk(var5)) {
               var5 = (DataChunk)var6.next();
               if (var3 == null || !var3.canAddChunk()) {
                  if (var3 != null) {
                     this.jobOutgoingQueue.add(var3);
                  }

                  var3 = RegionJob.allocReadChunksPacket();
               }
            }

            if (var3 != null) {
               if (var3.getChunkCount() > 0) {
                  this.jobOutgoingQueue.add(var3);
               } else {
                  RegionJob.release(var3);
               }
            }

            this.finishedJobQueue.add(var1);
            break;
         case DebugResetAllData:
            for(int var4 = 0; var4 < 2; ++var4) {
               this.rootBuffer.resetAllData();
               if (var4 == 0) {
                  isRequestingBufferSwap.set(true);

                  while(isRequestingBufferSwap.get() && !this.bFinished) {
                     Thread.sleep(5L);
                  }
               }
            }

            this.finishedJobQueue.add(var1);
            break;
         case SquareUpdate:
         case ReadChunksPacket:
         case ApplyChunkChanges:
            this.jobProcessingList.add(var1);
            if (var1.getJobType() == RegionJobType.ApplyChunkChanges) {
               this.runJobsList();
               this.jobProcessingList.clear();
            }
            break;
         default:
            this.finishedJobQueue.add(var1);
         }
      }

      Thread.sleep(20L);
   }

   private void runJobsList() throws InterruptedException {
      for(int var1 = 0; var1 < 2; ++var1) {
         for(int var2 = 0; var2 < this.jobProcessingList.size(); ++var2) {
            RegionJob var3 = (RegionJob)this.jobProcessingList.get(var2);
            switch(var3.getJobType()) {
            case SquareUpdate:
               this.rootBuffer.select.reset(var3.getWorldSquareX(), var3.getWorldSquareY(), var3.getWorldSquareZ(), true, false);
               if (this.rootBuffer.select.chunk != null) {
                  byte var11 = -1;
                  if (this.rootBuffer.select.square != -1) {
                     var11 = this.rootBuffer.select.square;
                  }

                  if (var3.getNewSquareFlags() != var11) {
                     this.rootBuffer.select.chunk.setOrAddSquare(this.rootBuffer.select.chunkSquareX, this.rootBuffer.select.chunkSquareY, this.rootBuffer.select.z, var3.getNewSquareFlags(), true);
                     this.rootBuffer.select.chunk.setDirty(this.rootBuffer.select.z);
                  }
               } else if (IsoRegion.PRINT_D) {
                  DebugLog.log("IsoRegion: trying to change a square on a unknown chunk");
               }
               break;
            case ReadChunksPacket:
               var3.readChunksPacket(this.rootBuffer, this.threadDiscoveredChunks);
               break;
            case ApplyChunkChanges:
               this.rootBuffer.processDirtyChunks();
               if (var1 == 0) {
                  isRequestingBufferSwap.set(true);

                  while(isRequestingBufferSwap.get()) {
                     Thread.sleep(5L);
                  }
               } else {
                  RegionJob.printStats();
                  ChunkRegion.printStats();
                  MasterRegion.printStats();
                  RegionJob var5;
                  if (!GameClient.bClient && var3.getSaveToDisk()) {
                     for(int var4 = this.jobProcessingList.size() - 1; var4 >= 0; --var4) {
                        var5 = (RegionJob)this.jobProcessingList.get(var4);
                        if (var5.getJobType() == RegionJobType.ReadChunksPacket || var5.getJobType() == RegionJobType.SquareUpdate) {
                           if (var5.getJobType() == RegionJobType.SquareUpdate) {
                              this.rootBuffer.select.reset(var5.getWorldSquareX(), var5.getWorldSquareY(), var5.getWorldSquareZ(), true, false);
                              var5 = RegionJob.allocReadChunksPacket();
                              var5.addChunkFromDataChunk(this.rootBuffer.select.chunk);
                           } else if (var5.getJobType() == RegionJobType.ReadChunksPacket) {
                              this.jobProcessingList.remove(var4);
                           }

                           var5.saveChunksToDisk(this);
                           if (GameServer.bServer) {
                              this.jobOutgoingQueue.add(var5);
                           }
                        }
                     }

                     if (this.threadDiscoveredChunks.size() > 0 && this.threadDiscoveredChunks.size() > this.lastThreadDiscoveredChunksSize) {
                        File var9 = this.getHeaderFile();

                        try {
                           DataOutputStream var12 = new DataOutputStream(new FileOutputStream(var9));
                           var12.writeInt(143);
                           var12.writeInt(this.threadDiscoveredChunks.size());
                           Iterator var6 = this.threadDiscoveredChunks.iterator();

                           while(var6.hasNext()) {
                              Integer var7 = (Integer)var6.next();
                              var12.writeInt(var7);
                           }

                           var12.flush();
                           var12.close();
                           this.lastThreadDiscoveredChunksSize = this.threadDiscoveredChunks.size();
                        } catch (Exception var8) {
                           DebugLog.log(var8.getMessage());
                           var8.printStackTrace();
                        }
                     }
                  }

                  Iterator var10 = this.jobProcessingList.iterator();

                  while(var10.hasNext()) {
                     var5 = (RegionJob)var10.next();
                     this.finishedJobQueue.add(var5);
                  }
               }
            }
         }
      }

   }

   protected DataRoot getRootBuffer() {
      return this.rootBuffer;
   }

   protected void setRootBuffer(DataRoot var1) {
      this.rootBuffer = var1;
   }

   protected void load() {
      if (IsoRegion.PRINT_D) {
         DebugLog.log("IsoRegion: Load save map.");
      }

      if (!GameClient.bClient) {
         this.loadSaveMap();
      } else {
         GameClient.sendIsoRegionDataRequest();
      }

   }

   protected void update() {
      RegionJob var1;
      for(var1 = (RegionJob)this.finishedJobQueue.poll(); var1 != null; var1 = (RegionJob)this.finishedJobQueue.poll()) {
         RegionJob.release(var1);
      }

      for(var1 = (RegionJob)this.jobOutgoingQueue.poll(); var1 != null; var1 = (RegionJob)this.jobOutgoingQueue.poll()) {
         if (GameServer.bServer) {
            if (IsoRegion.PRINT_D) {
               DebugLog.log("IsoRegion: sending changed datachunks packet.");
            }

            try {
               for(int var2 = 0; var2 < GameServer.udpEngine.connections.size(); ++var2) {
                  UdpConnection var3 = (UdpConnection)GameServer.udpEngine.connections.get(var2);
                  if (var1.getTargetConn() == null || var1.getTargetConn() == var3) {
                     ByteBufferWriter var4 = var3.startPacket();
                     PacketTypes.doPacket((short)201, var4);
                     ByteBuffer var5 = var4.bb;
                     var5.putLong(System.nanoTime());
                     var1.saveChunksToNetBuffer(var5);
                     var3.endPacketImmediate();
                  }
               }
            } catch (Exception var6) {
               DebugLog.log(var6.getMessage());
               var6.printStackTrace();
            }
         }

         RegionJob.release(var1);
      }

   }

   protected void readServerUpdatePacket(ByteBuffer var1) {
      if (GameClient.bClient) {
         if (IsoRegion.PRINT_D) {
            DebugLog.log("IsoRegion: Receiving changed datachunk packet from server");
         }

         try {
            RegionJob var2 = RegionJob.allocReadChunksPacket();
            long var3 = var1.getLong();
            var2.readChunksFromNetBuffer(var1, var3);
            this.EnqueueJob(var2);
            this.ApplyChunkChanges();
         } catch (Exception var5) {
            DebugLog.log(var5.getMessage());
            var5.printStackTrace();
         }
      }

   }

   protected void readClientRequestFullUpdatePacket(ByteBuffer var1, UdpConnection var2) {
      if (GameServer.bServer && var2 != null) {
         if (IsoRegion.PRINT_D) {
            DebugLog.log("IsoRegion: Receiving request full data packet from client");
         }

         try {
            RegionJob var3 = RegionJob.allocServerSendFullData(var2);
            this.EnqueueJob(var3);
         } catch (Exception var4) {
            DebugLog.log(var4.getMessage());
            var4.printStackTrace();
         }
      }

   }

   protected void addDebugResetJob() {
      if (!GameServer.bServer && !GameClient.bClient) {
         this.EnqueueJob(RegionJob.allocDebugResetAllData());
      }

   }

   protected void addSquareChangedJob(int var1, int var2, int var3, boolean var4, byte var5) {
      int var6 = var1 / 10;
      int var7 = var2 / 10;
      int var8 = IsoRegion.hash(var6, var7);
      if (this.discoveredChunks.contains(var8)) {
         if (IsoRegion.PRINT_D) {
            DebugLog.log("Update square only, plus any unprocessed chunks in a 7x7 grid.");
         }

         RegionJob var9 = RegionJob.allocSquareUpdate(var1, var2, var3, var5);
         this.EnqueueJob(var9);
         this.readSurroundingChunks(var6, var7, 7, false);
         this.ApplyChunkChanges();
      } else {
         if (var4) {
            return;
         }

         if (IsoRegion.PRINT_D) {
            DebugLog.log("Adding new chunk, plus any unprocessed chunks in a 7x7 grid.");
         }

         this.readSurroundingChunks(var6, var7, 7, true);
      }

   }

   protected void readSurroundingChunks(int var1, int var2, int var3, boolean var4) {
      int var5 = 1;
      if (var3 > 0 && var3 <= IsoChunkMap.ChunkGridWidth) {
         var5 = var3 / 2;
         if (var5 + var5 >= IsoChunkMap.ChunkGridWidth) {
            --var5;
         }
      }

      int var6 = var1 - var5;
      int var7 = var2 - var5;
      int var8 = var1 + var5;
      int var9 = var2 + var5;
      RegionJob var10 = null;

      for(int var12 = var6; var12 <= var8; ++var12) {
         for(int var13 = var7; var13 <= var9; ++var13) {
            IsoChunk var11 = GameServer.bServer ? ServerMap.instance.getChunk(var12, var13) : IsoWorld.instance.getCell().getChunk(var12, var13);
            if (var11 != null) {
               int var14 = IsoRegion.hash(var11.wx, var11.wy);
               if (!this.discoveredChunks.contains(var14)) {
                  this.discoveredChunks.add(var14);
                  if (var10 == null || !var10.canAddChunk()) {
                     if (var10 != null) {
                        this.EnqueueJob(var10);
                     }

                     var10 = RegionJob.allocReadChunksPacket();
                  }

                  var10.addChunkFromIsoChunk(var11);
               }
            }
         }
      }

      if (var10 != null) {
         if (var10.getChunkCount() > 0) {
            this.EnqueueJob(var10);
            if (var4) {
               this.ApplyChunkChanges();
            }
         } else {
            RegionJob.release(var10);
         }
      }

   }

   private void loadSaveMap() {
      try {
         boolean var1 = false;
         ArrayList var2 = new ArrayList();
         File var3 = this.getHeaderFile();
         if (var3.exists()) {
            DataInputStream var4 = new DataInputStream(new FileInputStream(var3));
            var1 = true;
            int var5 = var4.readInt();
            int var6 = var4.readInt();
            int var8 = 0;

            while(true) {
               if (var8 >= var6) {
                  var4.close();
                  break;
               }

               int var7 = var4.readInt();
               var2.add(var7);
               ++var8;
            }
         }

         File var32 = this.getDirectory();
         File[] var33 = var32.listFiles(new FilenameFilter() {
            public boolean accept(File var1, String var2) {
               return var2.startsWith("datachunk_") && var2.endsWith(".bin");
            }
         });
         RegionJob var34 = null;
         ByteBuffer var35 = byteBuffer;
         boolean var36 = false;
         if (var33 != null) {
            File[] var9 = var33;
            int var10 = var33.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               File var12 = var9[var11];
               FileInputStream var13 = new FileInputStream(var12);
               Throwable var14 = null;

               try {
                  var35.clear();
                  int var15 = var13.read(var35.array());
                  var35.limit(var15);
                  var35.mark();
                  int var16 = var35.getInt();
                  int var17 = var35.getInt();
                  int var18 = var35.getInt();
                  int var19 = var35.getInt();
                  var35.reset();
                  int var20 = IsoRegion.hash(var18, var19);
                  if (!this.discoveredChunks.contains(var20)) {
                     this.discoveredChunks.add(var20);
                  }

                  if (var2.contains(var20)) {
                     var2.remove(var2.indexOf(var20));
                  } else {
                     DebugLog.log("IsoRegion: A chunk save has been found that was not in header known chunks list.");
                  }

                  if (var34 == null || !var34.canAddChunk()) {
                     if (var34 != null) {
                        this.EnqueueJob(var34);
                     }

                     var34 = RegionJob.allocReadChunksPacket();
                  }

                  var34.addChunkFromFile(var35);
                  var36 = true;
               } catch (Throwable var29) {
                  var14 = var29;
                  throw var29;
               } finally {
                  if (var13 != null) {
                     if (var14 != null) {
                        try {
                           var13.close();
                        } catch (Throwable var28) {
                           var14.addSuppressed(var28);
                        }
                     } else {
                        var13.close();
                     }
                  }

               }
            }
         }

         if (var34 != null) {
            if (var34.getChunkCount() > 0) {
               this.EnqueueJob(var34);
            } else {
               RegionJob.release(var34);
            }
         }

         if (var36) {
            this.ApplyChunkChanges(false);
         }

         if (var1 && var2.size() > 0) {
            DebugLog.log("IsoRegion: " + var2.size() + " previously discovered chunks have not been loaded.");
         }
      } catch (Exception var31) {
         DebugLog.log(var31.getMessage());
         var31.printStackTrace();
      }

   }
}
