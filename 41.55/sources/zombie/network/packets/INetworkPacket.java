package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;


public interface INetworkPacket {

	void parse(ByteBuffer byteBuffer);

	void write(ByteBufferWriter byteBufferWriter);

	int getPacketSizeBytes();
}
