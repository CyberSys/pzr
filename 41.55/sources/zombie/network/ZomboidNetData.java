package zombie.network;

import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;


public class ZomboidNetData implements IZomboidPacket {
	public short type;
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
		this.type = 0;
		this.length = 0;
		this.connection = 0L;
		this.buffer.clear();
	}

	public void read(short short1, ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.type = short1;
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
