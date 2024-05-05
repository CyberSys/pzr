package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.packets.INetworkPacket;


public abstract class Instance implements INetworkPacket {
	protected short ID;

	public void set(short short1) {
		this.ID = short1;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.ID = byteBuffer.getShort();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.ID);
	}

	public boolean isConsistent() {
		return this.ID != -1;
	}

	public String getDescription() {
		return "ID=" + this.ID;
	}
}
