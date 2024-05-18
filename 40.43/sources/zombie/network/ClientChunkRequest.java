package zombie.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.raknet.UdpConnection;


public class ClientChunkRequest {
	public ArrayList chunks = new ArrayList(20);
	private static final ConcurrentLinkedQueue freeChunks = new ConcurrentLinkedQueue();
	public static final ConcurrentLinkedQueue freeBuffers = new ConcurrentLinkedQueue();
	public boolean largeArea = false;
	int minX;
	int maxX;
	int minY;
	int maxY;

	public ClientChunkRequest.Chunk getChunk() {
		ClientChunkRequest.Chunk chunk = (ClientChunkRequest.Chunk)freeChunks.poll();
		if (chunk == null) {
			chunk = new ClientChunkRequest.Chunk();
		}

		return chunk;
	}

	public void releaseChunk(ClientChunkRequest.Chunk chunk) {
		this.releaseBuffer(chunk);
		freeChunks.add(chunk);
	}

	public void getByteBuffer(ClientChunkRequest.Chunk chunk) {
		chunk.bb = (ByteBuffer)freeBuffers.poll();
		if (chunk.bb == null) {
			chunk.bb = ByteBuffer.allocate(16384);
		} else {
			chunk.bb.clear();
		}
	}

	public void releaseBuffer(ClientChunkRequest.Chunk chunk) {
		if (chunk.bb != null) {
			freeBuffers.add(chunk.bb);
			chunk.bb = null;
		}
	}

	public void releaseBuffers() {
		for (int int1 = 0; int1 < this.chunks.size(); ++int1) {
			((ClientChunkRequest.Chunk)this.chunks.get(int1)).bb = null;
		}
	}

	public void unpack(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1;
		for (int1 = 0; int1 < this.chunks.size(); ++int1) {
			this.releaseBuffer((ClientChunkRequest.Chunk)this.chunks.get(int1));
		}

		freeChunks.addAll(this.chunks);
		this.chunks.clear();
		int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			ClientChunkRequest.Chunk chunk = this.getChunk();
			chunk.requestNumber = byteBuffer.getInt();
			chunk.wx = byteBuffer.getInt();
			chunk.wy = byteBuffer.getInt();
			chunk.crc = byteBuffer.getLong();
			this.chunks.add(chunk);
		}

		this.largeArea = false;
	}

	public void unpackLargeArea(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1;
		for (int1 = 0; int1 < this.chunks.size(); ++int1) {
			this.releaseBuffer((ClientChunkRequest.Chunk)this.chunks.get(int1));
		}

		freeChunks.addAll(this.chunks);
		this.chunks.clear();
		this.minX = byteBuffer.getInt();
		this.minY = byteBuffer.getInt();
		this.maxX = byteBuffer.getInt();
		this.maxY = byteBuffer.getInt();
		for (int1 = this.minX; int1 < this.maxX; ++int1) {
			for (int int2 = this.minY; int2 < this.maxY; ++int2) {
				ClientChunkRequest.Chunk chunk = this.getChunk();
				chunk.requestNumber = byteBuffer.getInt();
				chunk.wx = int1;
				chunk.wy = int2;
				chunk.crc = 0L;
				this.releaseBuffer(chunk);
				this.chunks.add(chunk);
			}
		}

		this.largeArea = true;
	}

	public static final class Chunk {
		public int requestNumber;
		public int wx;
		public int wy;
		public long crc;
		public ByteBuffer bb;
	}
}
