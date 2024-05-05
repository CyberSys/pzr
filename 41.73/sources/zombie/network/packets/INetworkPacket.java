package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;


public interface INetworkPacket {

	void parse(ByteBuffer byteBuffer, UdpConnection udpConnection);

	void write(ByteBufferWriter byteBufferWriter);

	default int getPacketSizeBytes() {
		return 0;
	}

	default boolean isConsistent() {
		return true;
	}

	default String getDescription() {
		return this.getClass().getSimpleName();
	}

	default void log(String string) {
	}
}
