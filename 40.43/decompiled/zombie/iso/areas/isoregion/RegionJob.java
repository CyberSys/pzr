package zombie.iso.areas.isoregion;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.network.GameClient;

public class RegionJob {
   private static int totalCreated = 0;
   private static int totalReleased = 0;
   private static int totalReused = 0;
   private static ConcurrentLinkedQueue pool = new ConcurrentLinkedQueue();
   private RegionJobType type;
   private int worldSquareX;
   private int worldSquareY;
   private int worldSquareZ;
   private byte newSquareFlags;
   private ByteBuffer buffer;
   private int chunkCount;
   private boolean saveToDisk;
   private int bufferMaxBytes;
   private UdpConnection targetConn;
   private long netTimeStamp;
   private int bufferStartPos;

   protected static void printStats() {
      DebugLog.log("RegionJob: Created: " + totalCreated + ", Re-used: " + totalReused + ", Released: " + totalReleased + ", InPool: " + pool.size());
   }

   protected static RegionJob allocSquareUpdate(int var0, int var1, int var2, byte var3) {
      RegionJob var4 = alloc(RegionJobType.SquareUpdate);
      var4.worldSquareX = var0;
      var4.worldSquareY = var1;
      var4.worldSquareZ = var2;
      var4.newSquareFlags = var3;
      return var4;
   }

   protected static RegionJob allocReadChunksPacket() {
      RegionJob var0 = alloc(RegionJobType.ReadChunksPacket);
      return var0;
   }

   protected static RegionJob allocApplyChunkChanges(boolean var0) {
      RegionJob var1 = alloc(RegionJobType.ApplyChunkChanges);
      var1.saveToDisk = var0;
      return var1;
   }

   protected static RegionJob allocServerSendFullData(UdpConnection var0) {
      RegionJob var1 = alloc(RegionJobType.ServerSendFullData);
      var1.targetConn = var0;
      return var1;
   }

   protected static RegionJob allocDebugResetAllData() {
      return alloc(RegionJobType.DebugResetAllData);
   }

   private static RegionJob alloc(RegionJobType var0) {
      RegionJob var1 = (RegionJob)pool.poll();
      if (var1 == null) {
         var1 = new RegionJob();
         ++totalCreated;
      } else {
         ++totalReused;
      }

      var1.type = var0;
      var1.buffer.rewind();
      var1.buffer.clear();
      return var1;
   }

   protected static void release(RegionJob var0) {
      assert !pool.contains(var0);

      if (IsoRegion.PRINT_D && pool.contains(var0)) {
         DebugLog.log("Warning: RegionJob.release Trying to release a RegionJob twice.");
      } else {
         pool.add(var0.reset());
         ++totalReleased;
      }
   }

   private RegionJob() {
      this.type = RegionJobType.None;
      this.buffer = ByteBuffer.allocate(65536);
      this.chunkCount = 0;
      this.bufferMaxBytes = 0;
      this.netTimeStamp = -1L;
   }

   private RegionJob reset() {
      this.type = RegionJobType.None;
      this.saveToDisk = false;
      this.chunkCount = 0;
      this.bufferMaxBytes = 0;
      this.targetConn = null;
      this.netTimeStamp = -1L;
      return this;
   }

   protected RegionJobType getJobType() {
      return this.type;
   }

   protected boolean getSaveToDisk() {
      return this.saveToDisk;
   }

   protected int getChunkCount() {
      return this.chunkCount;
   }

   protected int getWorldSquareX() {
      return this.worldSquareX;
   }

   protected int getWorldSquareY() {
      return this.worldSquareY;
   }

   protected int getWorldSquareZ() {
      return this.worldSquareZ;
   }

   protected byte getNewSquareFlags() {
      return this.newSquareFlags;
   }

   protected UdpConnection getTargetConn() {
      return this.targetConn;
   }

   private boolean testJob(RegionJobType var1) {
      if (this.type != var1) {
         DebugLog.log("This job=" + this.type.toString() + ", required=" + var1.toString());
         return false;
      } else {
         return true;
      }
   }

   protected boolean readChunksPacket(DataRoot var1, List var2) {
      if (!this.testJob(RegionJobType.ReadChunksPacket)) {
         return false;
      } else {
         this.buffer.position(0);
         int var3 = this.buffer.getInt();
         int var4 = this.buffer.getInt();

         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = this.buffer.getInt();
            int var7 = this.buffer.getInt();
            int var8 = this.buffer.getInt();
            int var9 = this.buffer.getInt();
            var1.select.reset(var8 * 10, var9 * 10, 0, true, false);
            var1.select.ensureChunk(true);
            int var10;
            if (GameClient.bClient) {
               if (this.netTimeStamp != -1L && this.netTimeStamp < var1.select.chunk.lastUpdateStamp) {
                  var10 = this.buffer.position();
                  int var11 = this.buffer.getInt();
                  this.buffer.position(var10 + var11);
                  continue;
               }

               var1.select.chunk.lastUpdateStamp = this.netTimeStamp;
            } else {
               var10 = IsoRegion.hash(var8, var9);
               if (!var2.contains(var10)) {
                  var2.add(var10);
               }
            }

            var1.select.chunk.load(this.buffer, 143, true);
            var1.select.chunk.setDirtyAllActive();
         }

         return true;
      }
   }

   protected boolean saveChunksToDisk(IsoRegionWorker var1) {
      if (this.chunkCount <= 0) {
         return false;
      } else {
         this.buffer.position(0);
         int var2 = this.buffer.getInt();
         int var3 = this.buffer.getInt();

         for(int var4 = 0; var4 < var3; ++var4) {
            this.buffer.mark();
            int var5 = this.buffer.getInt();
            int var6 = this.buffer.getInt();
            int var7 = this.buffer.getInt();
            int var8 = this.buffer.getInt();
            this.buffer.reset();
            File var9 = var1.getChunkFile(var7, var8);

            try {
               FileOutputStream var10 = new FileOutputStream(var9);
               var10.getChannel().truncate(0L);
               var10.write(this.buffer.array(), this.buffer.position(), var5);
               var10.flush();
               var10.close();
            } catch (Exception var11) {
               DebugLog.log(var11.getMessage());
               var11.printStackTrace();
            }

            this.buffer.position(this.buffer.position() + var5);
         }

         return true;
      }
   }

   protected boolean saveChunksToNetBuffer(ByteBuffer var1) {
      DebugLog.log("Server max bytes buffer = " + this.bufferMaxBytes + ", chunks = " + this.chunkCount);
      var1.put(this.buffer.array(), 0, this.bufferMaxBytes);
      return true;
   }

   protected boolean readChunksFromNetBuffer(ByteBuffer var1, long var2) {
      this.netTimeStamp = var2;
      var1.mark();
      this.bufferMaxBytes = var1.getInt();
      this.chunkCount = var1.getInt();
      var1.reset();
      DebugLog.log("Client max bytes buffer = " + this.bufferMaxBytes + ", chunks = " + this.chunkCount);
      this.buffer.position(0);
      this.buffer.put(var1.array(), var1.position(), this.bufferMaxBytes);
      return true;
   }

   protected boolean canAddChunk() {
      return this.buffer.position() + 1024 < this.buffer.capacity();
   }

   private void startBufferBlock() {
      if (this.chunkCount == 0) {
         this.buffer.position(0);
         this.buffer.putInt(0);
         this.buffer.putInt(0);
      }

      this.bufferStartPos = this.buffer.position();
      this.buffer.putInt(0);
   }

   private void endBufferBlock() {
      this.bufferMaxBytes = this.buffer.position();
      this.buffer.position(this.bufferStartPos);
      this.buffer.putInt(this.bufferMaxBytes - this.bufferStartPos);
      ++this.chunkCount;
      this.buffer.position(0);
      this.buffer.putInt(this.bufferMaxBytes);
      this.buffer.putInt(this.chunkCount);
      this.buffer.position(this.bufferMaxBytes);
   }

   protected boolean addChunkFromDataChunk(DataChunk var1) {
      if (!this.testJob(RegionJobType.ReadChunksPacket)) {
         return false;
      } else if (this.buffer.position() + 1024 >= this.buffer.capacity()) {
         return false;
      } else {
         this.startBufferBlock();
         this.buffer.putInt(143);
         this.buffer.putInt(var1.getChunkX());
         this.buffer.putInt(var1.getChunkY());
         var1.save(this.buffer);
         this.endBufferBlock();
         return true;
      }
   }

   protected boolean addChunkFromIsoChunk(IsoChunk var1) {
      if (!this.testJob(RegionJobType.ReadChunksPacket)) {
         return false;
      } else if (this.buffer.position() + 1024 >= this.buffer.capacity()) {
         return false;
      } else {
         this.startBufferBlock();
         this.buffer.putInt(143);
         this.buffer.putInt(var1.wx);
         this.buffer.putInt(var1.wy);
         DataChunk.readChunkDataIntoBuffer(var1, this.buffer);
         this.endBufferBlock();
         return true;
      }
   }

   protected boolean addChunkFromFile(ByteBuffer var1) {
      if (!this.testJob(RegionJobType.ReadChunksPacket)) {
         return false;
      } else if (this.buffer.position() + var1.limit() >= this.buffer.capacity()) {
         return false;
      } else {
         var1.getInt();
         this.startBufferBlock();
         this.buffer.putInt(var1.getInt());
         this.buffer.putInt(var1.getInt());
         this.buffer.putInt(var1.getInt());
         var1.mark();
         int var2 = var1.getInt();
         var1.reset();
         this.buffer.put(var1.array(), var1.position(), var2);
         this.endBufferBlock();
         return true;
      }
   }
}
