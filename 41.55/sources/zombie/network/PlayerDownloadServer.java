package zombie.network;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import org.lwjglx.BufferUtils;
import zombie.ChunkMapFilenames;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoChunk;


public class PlayerDownloadServer {
	private PlayerDownloadServer.WorkerThread workerThread;
	public int port;
	private UdpConnection connection;
	private boolean NetworkFileDebug;
	private final CRC32 crc32 = new CRC32();
	private ByteBuffer bb = ByteBuffer.allocate(1000000);
	private ByteBuffer sb = BufferUtils.createByteBuffer(1000000);
	private ByteBufferWriter bbw;
	private final ArrayList ccrWaiting;

	public PlayerDownloadServer(UdpConnection udpConnection, int int1) {
		this.bbw = new ByteBufferWriter(this.bb);
		this.ccrWaiting = new ArrayList();
		this.connection = udpConnection;
		this.port = int1;
		this.workerThread = new PlayerDownloadServer.WorkerThread();
		this.workerThread.setDaemon(true);
		this.workerThread.setName("PlayerDownloadServer" + int1);
		this.workerThread.start();
	}

	public void destroy() {
		this.workerThread.putCommand(PlayerDownloadServer.EThreadCommand.Quit, (ClientChunkRequest)null);
		while (this.workerThread.isAlive()) {
			try {
				Thread.sleep(10L);
			} catch (InterruptedException interruptedException) {
			}
		}

		this.workerThread = null;
	}

	public void startConnectionTest() {
	}

	public void receiveRequestArray(ByteBuffer byteBuffer) throws Exception {
		ClientChunkRequest clientChunkRequest = (ClientChunkRequest)this.workerThread.freeRequests.poll();
		if (clientChunkRequest == null) {
			clientChunkRequest = new ClientChunkRequest();
		}

		clientChunkRequest.largeArea = false;
		this.ccrWaiting.add(clientChunkRequest);
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			if (clientChunkRequest.chunks.size() >= 20) {
				clientChunkRequest = (ClientChunkRequest)this.workerThread.freeRequests.poll();
				if (clientChunkRequest == null) {
					clientChunkRequest = new ClientChunkRequest();
				}

				clientChunkRequest.largeArea = false;
				this.ccrWaiting.add(clientChunkRequest);
			}

			ClientChunkRequest.Chunk chunk = clientChunkRequest.getChunk();
			chunk.requestNumber = byteBuffer.getInt();
			chunk.wx = byteBuffer.getInt();
			chunk.wy = byteBuffer.getInt();
			chunk.crc = byteBuffer.getLong();
			clientChunkRequest.chunks.add(chunk);
		}
	}

	public void receiveRequestLargeArea(ByteBuffer byteBuffer) {
		ClientChunkRequest clientChunkRequest = new ClientChunkRequest();
		clientChunkRequest.unpackLargeArea(byteBuffer, this.connection);
		for (int int1 = 0; int1 < clientChunkRequest.chunks.size(); ++int1) {
			ClientChunkRequest.Chunk chunk = (ClientChunkRequest.Chunk)clientChunkRequest.chunks.get(int1);
			IsoChunk chunk2 = ServerMap.instance.getChunk(chunk.wx, chunk.wy);
			if (chunk2 != null) {
				clientChunkRequest.getByteBuffer(chunk);
				try {
					chunk2.SaveLoadedChunk(chunk, this.crc32);
				} catch (Exception exception) {
					exception.printStackTrace();
					LoggerManager.getLogger("map").write(exception);
					clientChunkRequest.releaseBuffer(chunk);
				}
			}
		}

		this.workerThread.putCommand(PlayerDownloadServer.EThreadCommand.RequestLargeArea, clientChunkRequest);
	}

	public void receiveCancelRequest(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			int int3 = byteBuffer.getInt();
			this.workerThread.cancelQ.add(int3);
		}
	}

	public void update() {
		this.NetworkFileDebug = DebugType.Do(DebugType.NetworkFileDebug);
		if (this.workerThread.bReady) {
			if (this.ccrWaiting.isEmpty()) {
				if (this.workerThread.cancelQ.isEmpty() && !this.workerThread.cancelled.isEmpty()) {
					this.workerThread.cancelled.clear();
				}
			} else {
				ClientChunkRequest clientChunkRequest = (ClientChunkRequest)this.ccrWaiting.remove(0);
				for (int int1 = 0; int1 < clientChunkRequest.chunks.size(); ++int1) {
					ClientChunkRequest.Chunk chunk = (ClientChunkRequest.Chunk)clientChunkRequest.chunks.get(int1);
					if (this.workerThread.isRequestCancelled(chunk)) {
						clientChunkRequest.chunks.remove(int1--);
						clientChunkRequest.releaseChunk(chunk);
					} else {
						IsoChunk chunk2 = ServerMap.instance.getChunk(chunk.wx, chunk.wy);
						if (chunk2 != null) {
							try {
								clientChunkRequest.getByteBuffer(chunk);
								chunk2.SaveLoadedChunk(chunk, this.crc32);
							} catch (Exception exception) {
								exception.printStackTrace();
								LoggerManager.getLogger("map").write(exception);
								this.workerThread.sendNotRequired(chunk, false);
								clientChunkRequest.chunks.remove(int1--);
								clientChunkRequest.releaseChunk(chunk);
							}
						}
					}
				}

				if (clientChunkRequest.chunks.isEmpty()) {
					this.workerThread.freeRequests.add(clientChunkRequest);
				} else {
					this.workerThread.bReady = false;
					this.workerThread.putCommand(PlayerDownloadServer.EThreadCommand.RequestZipArray, clientChunkRequest);
				}
			}
		}
	}

	private void sendPacket() {
		this.bb.flip();
		this.sb.put(this.bb);
		this.sb.flip();
		this.connection.getPeer().SendRaw(this.sb, 1, 3, (byte)0, this.connection.getConnectedGUID(), false);
		this.sb.clear();
	}

	private ByteBufferWriter startPacket() {
		this.bb.clear();
		return this.bbw;
	}

	private final class WorkerThread extends Thread {
		boolean bQuit;
		volatile boolean bReady = true;
		final LinkedBlockingQueue commandQ = new LinkedBlockingQueue();
		final ConcurrentLinkedQueue freeRequests = new ConcurrentLinkedQueue();
		final ConcurrentLinkedQueue cancelQ = new ConcurrentLinkedQueue();
		final ArrayList cancelled = new ArrayList();
		final CRC32 crcMaker = new CRC32();
		static final int chunkSize = 1000;
		private byte[] inMemoryZip = new byte[20480];
		private final Deflater compressor = new Deflater();

		public void run() {
			while (!this.bQuit) {
				try {
					this.runInner();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		private void runInner() throws InterruptedException, IOException {
			MPStatistic.getInstance().PlayerDownloadServer.End();
			PlayerDownloadServer.WorkerThreadCommand workerThreadCommand = (PlayerDownloadServer.WorkerThreadCommand)this.commandQ.take();
			MPStatistic.getInstance().PlayerDownloadServer.Start();
			switch (workerThreadCommand.e) {
			case RequestLargeArea: 
				try {
					this.sendLargeArea(workerThreadCommand.ccr);
					break;
				} finally {
					this.bReady = true;
				}

			
			case RequestZipArray: 
				try {
					this.sendArray(workerThreadCommand.ccr);
					break;
				} finally {
					this.bReady = true;
				}

			
			case Quit: 
				this.bQuit = true;
			
			}
		}

		void putCommand(PlayerDownloadServer.EThreadCommand eThreadCommand, ClientChunkRequest clientChunkRequest) {
			PlayerDownloadServer.WorkerThreadCommand workerThreadCommand = new PlayerDownloadServer.WorkerThreadCommand();
			workerThreadCommand.e = eThreadCommand;
			workerThreadCommand.ccr = clientChunkRequest;
			while (true) {
				try {
					this.commandQ.put(workerThreadCommand);
					return;
				} catch (InterruptedException interruptedException) {
				}
			}
		}

		private int compressChunk(ClientChunkRequest.Chunk chunk) {
			this.compressor.reset();
			this.compressor.setInput(chunk.bb.array(), 0, chunk.bb.limit());
			this.compressor.finish();
			if ((double)this.inMemoryZip.length < (double)chunk.bb.limit() * 1.5) {
				this.inMemoryZip = new byte[(int)((double)chunk.bb.limit() * 1.5)];
			}

			return this.compressor.deflate(this.inMemoryZip, 0, this.inMemoryZip.length, 3);
		}

		private void sendChunk(ClientChunkRequest.Chunk chunk) {
			try {
				long long1 = (long)this.compressChunk(chunk);
				long long2 = long1 / 1000L;
				if (long1 % 1000L != 0L) {
					++long2;
				}

				long long3 = 0L;
				for (int int1 = 0; (long)int1 < long2; ++int1) {
					long long4 = long1 - long3 > 1000L ? 1000L : long1 - long3;
					ByteBufferWriter byteBufferWriter = PlayerDownloadServer.this.startPacket();
					PacketTypes.doPacket((short)18, byteBufferWriter);
					byteBufferWriter.putInt(chunk.requestNumber);
					byteBufferWriter.putInt((int)long2);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt((int)long1);
					byteBufferWriter.putInt((int)long3);
					byteBufferWriter.putInt((int)long4);
					byteBufferWriter.bb.put(this.inMemoryZip, (int)long3, (int)long4);
					PlayerDownloadServer.this.sendPacket();
					long3 += long4;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				this.sendNotRequired(chunk, false);
			}
		}

		private void sendNotRequired(ClientChunkRequest.Chunk chunk, boolean boolean1) {
			ByteBufferWriter byteBufferWriter = PlayerDownloadServer.this.startPacket();
			PacketTypes.doPacket((short)36, byteBufferWriter);
			byteBufferWriter.putInt(1);
			byteBufferWriter.putInt(chunk.requestNumber);
			byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
			PlayerDownloadServer.this.sendPacket();
		}

		private void sendLargeArea(ClientChunkRequest clientChunkRequest) throws IOException {
			for (int int1 = 0; int1 < clientChunkRequest.chunks.size(); ++int1) {
				ClientChunkRequest.Chunk chunk = (ClientChunkRequest.Chunk)clientChunkRequest.chunks.get(int1);
				int int2 = chunk.wx;
				int int3 = chunk.wy;
				if (chunk.bb != null) {
					chunk.bb.limit(chunk.bb.position());
					chunk.bb.position(0);
					this.sendChunk(chunk);
					clientChunkRequest.releaseBuffer(chunk);
				} else {
					File file = ChunkMapFilenames.instance.getFilename(int2, int3);
					if (file.exists()) {
						clientChunkRequest.getByteBuffer(chunk);
						chunk.bb = IsoChunk.SafeRead("map_", int2, int3, chunk.bb);
						this.sendChunk(chunk);
						clientChunkRequest.releaseBuffer(chunk);
					}
				}
			}

			ClientChunkRequest.freeBuffers.clear();
			clientChunkRequest.chunks.clear();
		}

		private void sendArray(ClientChunkRequest clientChunkRequest) throws IOException {
			int int1;
			for (int1 = 0; int1 < clientChunkRequest.chunks.size(); ++int1) {
				ClientChunkRequest.Chunk chunk = (ClientChunkRequest.Chunk)clientChunkRequest.chunks.get(int1);
				if (!this.isRequestCancelled(chunk)) {
					int int2 = chunk.wx;
					int int3 = chunk.wy;
					long long1 = chunk.crc;
					if (chunk.bb != null) {
						boolean boolean1 = true;
						if (chunk.crc != 0L) {
							this.crcMaker.reset();
							this.crcMaker.update(chunk.bb.array(), 0, chunk.bb.position());
							boolean1 = chunk.crc != this.crcMaker.getValue();
							if (boolean1 && PlayerDownloadServer.this.NetworkFileDebug) {
								DebugLog.log(DebugType.NetworkFileDebug, int2 + "," + int3 + ": crc server=" + this.crcMaker.getValue() + " client=" + chunk.crc);
							}
						}

						if (boolean1) {
							if (PlayerDownloadServer.this.NetworkFileDebug) {
								DebugLog.log(DebugType.NetworkFileDebug, int2 + "," + int3 + ": send=true loaded=true");
							}

							chunk.bb.limit(chunk.bb.position());
							chunk.bb.position(0);
							this.sendChunk(chunk);
						} else {
							if (PlayerDownloadServer.this.NetworkFileDebug) {
								DebugLog.log(DebugType.NetworkFileDebug, int2 + "," + int3 + ": send=false loaded=true");
							}

							this.sendNotRequired(chunk, true);
						}

						clientChunkRequest.releaseBuffer(chunk);
					} else {
						File file = ChunkMapFilenames.instance.getFilename(int2, int3);
						if (file.exists()) {
							long long2 = ChunkChecksum.getChecksum(int2, int3);
							if (long2 != 0L && long2 == chunk.crc) {
								if (PlayerDownloadServer.this.NetworkFileDebug) {
									DebugLog.log(DebugType.NetworkFileDebug, int2 + "," + int3 + ": send=false loaded=false file=true");
								}

								this.sendNotRequired(chunk, true);
							} else {
								clientChunkRequest.getByteBuffer(chunk);
								chunk.bb = IsoChunk.SafeRead("map_", int2, int3, chunk.bb);
								boolean boolean2 = true;
								if (chunk.crc != 0L) {
									this.crcMaker.reset();
									this.crcMaker.update(chunk.bb.array(), 0, chunk.bb.limit());
									boolean2 = chunk.crc != this.crcMaker.getValue();
								}

								if (boolean2) {
									if (PlayerDownloadServer.this.NetworkFileDebug) {
										DebugLog.log(DebugType.NetworkFileDebug, int2 + "," + int3 + ": send=true loaded=false file=true");
									}

									this.sendChunk(chunk);
								} else {
									if (PlayerDownloadServer.this.NetworkFileDebug) {
										DebugLog.log(DebugType.NetworkFileDebug, int2 + "," + int3 + ": send=false loaded=false file=true");
									}

									this.sendNotRequired(chunk, true);
								}

								clientChunkRequest.releaseBuffer(chunk);
							}
						} else {
							if (PlayerDownloadServer.this.NetworkFileDebug) {
								DebugLog.log(DebugType.NetworkFileDebug, int2 + "," + int3 + ": send=false loaded=false file=false");
							}

							this.sendNotRequired(chunk, long1 == 0L);
						}
					}
				}
			}

			for (int1 = 0; int1 < clientChunkRequest.chunks.size(); ++int1) {
				clientChunkRequest.releaseChunk((ClientChunkRequest.Chunk)clientChunkRequest.chunks.get(int1));
			}

			clientChunkRequest.chunks.clear();
			this.freeRequests.add(clientChunkRequest);
		}

		private boolean isRequestCancelled(ClientChunkRequest.Chunk chunk) {
			for (Integer integer = (Integer)this.cancelQ.poll(); integer != null; integer = (Integer)this.cancelQ.poll()) {
				this.cancelled.add(integer);
			}

			for (int int1 = 0; int1 < this.cancelled.size(); ++int1) {
				Integer integer2 = (Integer)this.cancelled.get(int1);
				if (integer2 == chunk.requestNumber) {
					if (PlayerDownloadServer.this.NetworkFileDebug) {
						DebugLog.log(DebugType.NetworkFileDebug, "cancelled request #" + integer2);
					}

					this.cancelled.remove(int1);
					return true;
				}
			}

			return false;
		}
	}

	private static enum EThreadCommand {

		RequestLargeArea,
		RequestZipArray,
		Quit;

		private static PlayerDownloadServer.EThreadCommand[] $values() {
			return new PlayerDownloadServer.EThreadCommand[]{RequestLargeArea, RequestZipArray, Quit};
		}
	}

	private static final class WorkerThreadCommand {
		PlayerDownloadServer.EThreadCommand e;
		ClientChunkRequest ccr;
	}
}
