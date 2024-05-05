package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;

public interface INetworkPacket {
   void parse(ByteBuffer var1);

   void write(ByteBufferWriter var1);

   int getPacketSizeBytes();
}
