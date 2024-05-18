package zombie.spnetwork;

import java.nio.ByteBuffer;
import sun.misc.Lock;
import zombie.characters.IsoPlayer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;


final class UdpConnection {
	final UdpEngine engine;
	private final Lock bufferLock = new Lock();
	private final ByteBuffer bb = ByteBuffer.allocate(1000000);
	private final ByteBufferWriter bbw;
	public final IsoPlayer[] players;

	public UdpConnection(UdpEngine udpEngine) {
		this.bbw = new ByteBufferWriter(this.bb);
		this.players = IsoPlayer.players;
		this.engine = udpEngine;
	}

	public boolean ReleventTo(float float1, float float2) {
		return true;
	}

	public ByteBufferWriter startPacket() {
		try {
			this.bufferLock.lock();
		} catch (InterruptedException interruptedException) {
			ExceptionLogger.logException(interruptedException);
		}

		this.bb.clear();
		return this.bbw;
	}

	public void endPacketImmediate() {
		this.bb.flip();
		this.engine.Send(this.bb);
		this.bufferLock.unlock();
	}

	public void cancelPacket() {
		this.bufferLock.unlock();
	}
}
