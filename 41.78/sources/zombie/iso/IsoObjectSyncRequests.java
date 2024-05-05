package zombie.iso;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.SystemDisabler;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.PacketTypes;


public final class IsoObjectSyncRequests {
	public final ArrayList requests = new ArrayList();
	public long timeout = 1000L;

	public void putRequest(IsoGridSquare square, IsoObject object) {
		if (GameClient.bClient) {
			this.putRequest(square.x, square.y, square.z, (byte)square.getObjects().indexOf(object));
		}
	}

	public void putRequestLoad(IsoGridSquare square) {
		if (GameClient.bClient) {
			this.putRequest(square.x, square.y, square.z, (byte)square.getObjects().size());
		}
	}

	public void putRequest(int int1, int int2, int int3, byte byte1) {
		if (SystemDisabler.doObjectStateSyncEnable) {
			IsoObjectSyncRequests.SyncData syncData = new IsoObjectSyncRequests.SyncData();
			syncData.x = int1;
			syncData.y = int2;
			syncData.z = int3;
			syncData.objIndex = byte1;
			syncData.reqTime = 0L;
			syncData.reqCount = 0;
			synchronized (this.requests) {
				this.requests.add(syncData);
			}
		}
	}

	public void sendRequests(UdpConnection udpConnection) {
		if (SystemDisabler.doObjectStateSyncEnable) {
			if (this.requests.size() != 0) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.SyncIsoObjectReq.doPacket(byteBufferWriter);
				ByteBuffer byteBuffer = byteBufferWriter.bb;
				int int1 = byteBuffer.position();
				byteBufferWriter.putShort((short)0);
				int int2 = 0;
				synchronized (this.requests) {
					for (int int3 = 0; int3 < this.requests.size(); ++int3) {
						IsoObjectSyncRequests.SyncData syncData = (IsoObjectSyncRequests.SyncData)this.requests.get(int3);
						if (syncData.reqCount > 4) {
							this.requests.remove(int3);
							--int3;
						} else {
							if (syncData.reqTime == 0L) {
								syncData.reqTime = System.currentTimeMillis();
								++int2;
								byteBuffer.putInt(syncData.x);
								byteBuffer.putInt(syncData.y);
								byteBuffer.putInt(syncData.z);
								byteBuffer.put(syncData.objIndex);
								++syncData.reqCount;
							}

							if (System.currentTimeMillis() - syncData.reqTime >= this.timeout) {
								syncData.reqTime = System.currentTimeMillis();
								++int2;
								byteBuffer.putInt(syncData.x);
								byteBuffer.putInt(syncData.y);
								byteBuffer.putInt(syncData.z);
								byteBuffer.put(syncData.objIndex);
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
					PacketTypes.PacketType.SyncIsoObjectReq.send(GameClient.connection);
				}
			}
		}
	}

	public void receiveIsoSync(int int1, int int2, int int3, byte byte1) {
		synchronized (this.requests) {
			for (int int4 = 0; int4 < this.requests.size(); ++int4) {
				IsoObjectSyncRequests.SyncData syncData = (IsoObjectSyncRequests.SyncData)this.requests.get(int4);
				if (syncData.x == int1 && syncData.y == int2 && syncData.z == int3 && syncData.objIndex == byte1) {
					this.requests.remove(int4);
				}
			}
		}
	}

	private class SyncData {
		int x;
		int y;
		int z;
		byte objIndex;
		long reqTime;
		int reqCount;
	}
}
