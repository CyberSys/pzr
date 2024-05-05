package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.objects.IsoDeadBody;


public class RemoveCorpseFromMap implements INetworkPacket {
	private short id;
	private IsoDeadBody deadBody = null;

	public void set(IsoDeadBody deadBody) {
		this.id = deadBody.getObjectID();
		this.deadBody = deadBody;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.id = byteBuffer.getShort();
		this.deadBody = IsoDeadBody.getDeadBody(this.id);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.id);
	}

	public void process() {
		if (this.isConsistent()) {
			IsoDeadBody.removeDeadBody(this.id);
		}
	}

	public String getDescription() {
		return String.format(this.getClass().getSimpleName() + " id=%d", this.id);
	}

	public boolean isConsistent() {
		return this.deadBody != null && this.deadBody.getSquare() != null;
	}

	public boolean isRelevant(UdpConnection udpConnection) {
		return udpConnection.RelevantTo(this.deadBody.getX(), this.deadBody.getY());
	}
}
