package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.SoundManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;


public class PlayWorldSoundPacket implements INetworkPacket {
	String name;
	int x;
	int y;
	byte z;

	public void set(String string, int int1, int int2, byte byte1) {
		this.name = string;
		this.x = int1;
		this.y = int2;
		this.z = byte1;
	}

	public void process() {
		SoundManager.instance.PlayWorldSoundImpl(this.name, false, this.x, this.y, this.z, 1.0F, 20.0F, 2.0F, false);
	}

	public String getName() {
		return this.name;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.x = byteBuffer.getInt();
		this.y = byteBuffer.getInt();
		this.z = byteBuffer.get();
		this.name = GameWindow.ReadString(byteBuffer);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.x);
		byteBufferWriter.putInt(this.y);
		byteBufferWriter.putByte(this.z);
		byteBufferWriter.putUTF(this.name);
	}

	public boolean isConsistent() {
		return this.name != null && !this.name.isEmpty();
	}

	public int getPacketSizeBytes() {
		return 12 + this.name.length();
	}

	public String getDescription() {
		return "\n\tPlayWorldSoundPacket [name=" + this.name + " | x=" + this.x + " | y=" + this.y + " | z=" + this.z + " ]";
	}
}
