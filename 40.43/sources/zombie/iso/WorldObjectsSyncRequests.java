package zombie.iso;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.PacketTypes;


public class WorldObjectsSyncRequests {
	public ArrayList requests = new ArrayList();
	public long timeout = 1000L;

	public void putRequest(IsoChunk chunk) {
		WorldObjectsSyncRequests.SyncData syncData = new WorldObjectsSyncRequests.SyncData();
		syncData.x = chunk.wx;
		syncData.y = chunk.wy;
		syncData.hashCodeWorldObjects = chunk.getHashCodeObjects();
		syncData.reqTime = 0L;
		syncData.reqCount = 0;
		synchronized (this.requests) {
			this.requests.add(syncData);
		}
	}

	public void sendRequests(UdpConnection udpConnection) {
		if (this.requests.size() != 0) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)163, byteBufferWriter);
			ByteBuffer byteBuffer = byteBufferWriter.bb;
			int int1 = byteBuffer.position();
			byteBufferWriter.putShort((short)0);
			int int2 = 0;
			synchronized (this.requests) {
				for (int int3 = 0; int3 < this.requests.size(); ++int3) {
					WorldObjectsSyncRequests.SyncData syncData = (WorldObjectsSyncRequests.SyncData)this.requests.get(int3);
					if (syncData.reqCount > 2) {
						this.requests.remove(int3);
						--int3;
					} else {
						if (syncData.reqTime == 0L) {
							syncData.reqTime = System.currentTimeMillis();
							++int2;
							byteBuffer.putInt(syncData.x);
							byteBuffer.putInt(syncData.y);
							byteBuffer.putLong(syncData.hashCodeWorldObjects);
							++syncData.reqCount;
						}

						if (System.currentTimeMillis() - syncData.reqTime >= this.timeout) {
							syncData.reqTime = System.currentTimeMillis();
							++int2;
							byteBuffer.putInt(syncData.x);
							byteBuffer.putInt(syncData.y);
							byteBuffer.putLong(syncData.hashCodeWorldObjects);
							++syncData.reqCount;
						}

						if (int2 >= 50) {
							break;
						}
					}
				}
			}

			if (int2 == 0) {
				GameClient.connection.cancelPacket();
			} else {
				int int4 = byteBuffer.position();
				byteBuffer.position(int1);
				byteBuffer.putShort((short)int2);
				byteBuffer.position(int4);
				GameClient.connection.endPacketImmediate();
			}
		}
	}

	public void receiveIsoSync(int int1, int int2) {
		synchronized (this.requests) {
			for (int int3 = 0; int3 < this.requests.size(); ++int3) {
				WorldObjectsSyncRequests.SyncData syncData = (WorldObjectsSyncRequests.SyncData)this.requests.get(int3);
				if (syncData.x == int1 && syncData.y == int2) {
					this.requests.remove(int3);
				}
			}
		}
	}

	private class SyncData {
		int x;
		int y;
		long hashCodeWorldObjects;
		long reqTime;
		int reqCount;

		private SyncData() {
		}

		SyncData(Object object) {
			this();
		}
	}
}
