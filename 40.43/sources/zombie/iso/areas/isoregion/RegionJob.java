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

	protected static RegionJob allocSquareUpdate(int int1, int int2, int int3, byte byte1) {
		RegionJob regionJob = alloc(RegionJobType.SquareUpdate);
		regionJob.worldSquareX = int1;
		regionJob.worldSquareY = int2;
		regionJob.worldSquareZ = int3;
		regionJob.newSquareFlags = byte1;
		return regionJob;
	}

	protected static RegionJob allocReadChunksPacket() {
		RegionJob regionJob = alloc(RegionJobType.ReadChunksPacket);
		return regionJob;
	}

	protected static RegionJob allocApplyChunkChanges(boolean boolean1) {
		RegionJob regionJob = alloc(RegionJobType.ApplyChunkChanges);
		regionJob.saveToDisk = boolean1;
		return regionJob;
	}

	protected static RegionJob allocServerSendFullData(UdpConnection udpConnection) {
		RegionJob regionJob = alloc(RegionJobType.ServerSendFullData);
		regionJob.targetConn = udpConnection;
		return regionJob;
	}

	protected static RegionJob allocDebugResetAllData() {
		return alloc(RegionJobType.DebugResetAllData);
	}

	private static RegionJob alloc(RegionJobType regionJobType) {
		RegionJob regionJob = (RegionJob)pool.poll();
		if (regionJob == null) {
			regionJob = new RegionJob();
			++totalCreated;
		} else {
			++totalReused;
		}

		regionJob.type = regionJobType;
		regionJob.buffer.rewind();
		regionJob.buffer.clear();
		return regionJob;
	}

	protected static void release(RegionJob regionJob) {
		assert !pool.contains(regionJob);
		if (IsoRegion.PRINT_D && pool.contains(regionJob)) {
			DebugLog.log("Warning: RegionJob.release Trying to release a RegionJob twice.");
		} else {
			pool.add(regionJob.reset());
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

	private boolean testJob(RegionJobType regionJobType) {
		if (this.type != regionJobType) {
			DebugLog.log("This job=" + this.type.toString() + ", required=" + regionJobType.toString());
			return false;
		} else {
			return true;
		}
	}

	protected boolean readChunksPacket(DataRoot dataRoot, List list) {
		if (!this.testJob(RegionJobType.ReadChunksPacket)) {
			return false;
		} else {
			this.buffer.position(0);
			int int1 = this.buffer.getInt();
			int int2 = this.buffer.getInt();
			for (int int3 = 0; int3 < int2; ++int3) {
				int int4 = this.buffer.getInt();
				int int5 = this.buffer.getInt();
				int int6 = this.buffer.getInt();
				int int7 = this.buffer.getInt();
				dataRoot.select.reset(int6 * 10, int7 * 10, 0, true, false);
				dataRoot.select.ensureChunk(true);
				int int8;
				if (GameClient.bClient) {
					if (this.netTimeStamp != -1L && this.netTimeStamp < dataRoot.select.chunk.lastUpdateStamp) {
						int8 = this.buffer.position();
						int int9 = this.buffer.getInt();
						this.buffer.position(int8 + int9);
						continue;
					}

					dataRoot.select.chunk.lastUpdateStamp = this.netTimeStamp;
				} else {
					int8 = IsoRegion.hash(int6, int7);
					if (!list.contains(int8)) {
						list.add(int8);
					}
				}

				dataRoot.select.chunk.load(this.buffer, 143, true);
				dataRoot.select.chunk.setDirtyAllActive();
			}

			return true;
		}
	}

	protected boolean saveChunksToDisk(IsoRegionWorker regionWorker) {
		if (this.chunkCount <= 0) {
			return false;
		} else {
			this.buffer.position(0);
			int int1 = this.buffer.getInt();
			int int2 = this.buffer.getInt();
			for (int int3 = 0; int3 < int2; ++int3) {
				this.buffer.mark();
				int int4 = this.buffer.getInt();
				int int5 = this.buffer.getInt();
				int int6 = this.buffer.getInt();
				int int7 = this.buffer.getInt();
				this.buffer.reset();
				File file = regionWorker.getChunkFile(int6, int7);
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					fileOutputStream.getChannel().truncate(0L);
					fileOutputStream.write(this.buffer.array(), this.buffer.position(), int4);
					fileOutputStream.flush();
					fileOutputStream.close();
				} catch (Exception exception) {
					DebugLog.log(exception.getMessage());
					exception.printStackTrace();
				}

				this.buffer.position(this.buffer.position() + int4);
			}

			return true;
		}
	}

	protected boolean saveChunksToNetBuffer(ByteBuffer byteBuffer) {
		DebugLog.log("Server max bytes buffer = " + this.bufferMaxBytes + ", chunks = " + this.chunkCount);
		byteBuffer.put(this.buffer.array(), 0, this.bufferMaxBytes);
		return true;
	}

	protected boolean readChunksFromNetBuffer(ByteBuffer byteBuffer, long long1) {
		this.netTimeStamp = long1;
		byteBuffer.mark();
		this.bufferMaxBytes = byteBuffer.getInt();
		this.chunkCount = byteBuffer.getInt();
		byteBuffer.reset();
		DebugLog.log("Client max bytes buffer = " + this.bufferMaxBytes + ", chunks = " + this.chunkCount);
		this.buffer.position(0);
		this.buffer.put(byteBuffer.array(), byteBuffer.position(), this.bufferMaxBytes);
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

	protected boolean addChunkFromDataChunk(DataChunk dataChunk) {
		if (!this.testJob(RegionJobType.ReadChunksPacket)) {
			return false;
		} else if (this.buffer.position() + 1024 >= this.buffer.capacity()) {
			return false;
		} else {
			this.startBufferBlock();
			this.buffer.putInt(143);
			this.buffer.putInt(dataChunk.getChunkX());
			this.buffer.putInt(dataChunk.getChunkY());
			dataChunk.save(this.buffer);
			this.endBufferBlock();
			return true;
		}
	}

	protected boolean addChunkFromIsoChunk(IsoChunk chunk) {
		if (!this.testJob(RegionJobType.ReadChunksPacket)) {
			return false;
		} else if (this.buffer.position() + 1024 >= this.buffer.capacity()) {
			return false;
		} else {
			this.startBufferBlock();
			this.buffer.putInt(143);
			this.buffer.putInt(chunk.wx);
			this.buffer.putInt(chunk.wy);
			DataChunk.readChunkDataIntoBuffer(chunk, this.buffer);
			this.endBufferBlock();
			return true;
		}
	}

	protected boolean addChunkFromFile(ByteBuffer byteBuffer) {
		if (!this.testJob(RegionJobType.ReadChunksPacket)) {
			return false;
		} else if (this.buffer.position() + byteBuffer.limit() >= this.buffer.capacity()) {
			return false;
		} else {
			byteBuffer.getInt();
			this.startBufferBlock();
			this.buffer.putInt(byteBuffer.getInt());
			this.buffer.putInt(byteBuffer.getInt());
			this.buffer.putInt(byteBuffer.getInt());
			byteBuffer.mark();
			int int1 = byteBuffer.getInt();
			byteBuffer.reset();
			this.buffer.put(byteBuffer.array(), byteBuffer.position(), int1);
			this.endBufferBlock();
			return true;
		}
	}
}
