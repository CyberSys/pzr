package zombie.network;

import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;


public class ZomboidNetData implements IZomboidPacket {
	public PacketTypes.PacketType type;
	public short length;
	public ByteBuffer buffer;
	public long connection;
	public long time;

	public ZomboidNetData() {
		this.buffer = ByteBuffer.allocate(2048);
	}

	public ZomboidNetData(int int1) {
		this.buffer = ByteBuffer.allocate(int1);
	}

	public void reset() {
		this.type = null;
		this.length = 0;
		this.connection = 0L;
		this.buffer.clear();
	}

	public void read(short short1, ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.type = (PacketTypes.PacketType)PacketTypes.packetTypes.get(short1);
		if (this.type == null) {
			DebugLog.Multiplayer.error("Received unknown packet id=%d", short1);
		}

		this.connection = udpConnection.getConnectedGUID();
		this.buffer.put(byteBuffer);
		this.buffer.flip();
	}

	public boolean isConnect() {
		return false;
	}

	public boolean isDisconnect() {
		return false;
	}
}
