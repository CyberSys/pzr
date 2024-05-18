package zombie.spnetwork;

import java.nio.ByteBuffer;
import zombie.network.IZomboidPacket;


public final class ZomboidNetData implements IZomboidPacket {
	public short type;
	public short length;
	public ByteBuffer buffer;
	public UdpConnection connection;

	public ZomboidNetData() {
		this.buffer = ByteBuffer.allocate(2048);
	}

	public ZomboidNetData(int int1) {
		this.buffer = ByteBuffer.allocate(int1);
	}

	public void reset() {
		this.type = 0;
		this.length = 0;
		this.buffer.clear();
		this.connection = null;
	}

	public void read(short short1, ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.type = short1;
		this.connection = udpConnection;
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
