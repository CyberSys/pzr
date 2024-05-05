package zombie.spnetwork;

import java.nio.ByteBuffer;


public abstract class UdpEngine {

	public abstract void Send(ByteBuffer byteBuffer);

	public abstract void Receive(ByteBuffer byteBuffer);
}
