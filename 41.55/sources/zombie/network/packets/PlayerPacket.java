package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;


public class PlayerPacket implements INetworkPacket {
	public static final int PACKET_SIZE_BYTES = 43;
	public short id;
	public float x;
	public float y;
	public byte z;
	public int t;
	public float direction;
	public boolean usePathFinder;
	public short VehicleID;
	public short VehicleSeat;
	public int booleanVariables;
	public byte footstepSoundRadius;
	public float realx;
	public float realy;
	public byte realz;
	public byte realdir;
	public int realt;

	public void parse(ByteBuffer byteBuffer) {
		this.id = byteBuffer.getShort();
		this.x = byteBuffer.getFloat();
		this.y = byteBuffer.getFloat();
		this.z = byteBuffer.get();
		this.t = byteBuffer.getInt();
		this.direction = byteBuffer.getFloat();
		this.usePathFinder = byteBuffer.get() == 1;
		this.VehicleID = byteBuffer.getShort();
		this.VehicleSeat = byteBuffer.getShort();
		this.booleanVariables = byteBuffer.getInt();
		this.footstepSoundRadius = byteBuffer.get();
		this.realx = byteBuffer.getFloat();
		this.realy = byteBuffer.getFloat();
		this.realz = byteBuffer.get();
		this.realdir = byteBuffer.get();
		this.realt = byteBuffer.getInt();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.id);
		byteBufferWriter.putFloat(this.x);
		byteBufferWriter.putFloat(this.y);
		byteBufferWriter.putByte(this.z);
		byteBufferWriter.putInt(this.t);
		byteBufferWriter.putFloat(this.direction);
		byteBufferWriter.putBoolean(this.usePathFinder);
		byteBufferWriter.putShort(this.VehicleID);
		byteBufferWriter.putShort(this.VehicleSeat);
		byteBufferWriter.putInt(this.booleanVariables);
		byteBufferWriter.putByte(this.footstepSoundRadius);
		byteBufferWriter.putFloat(this.realx);
		byteBufferWriter.putFloat(this.realy);
		byteBufferWriter.putByte(this.realz);
		byteBufferWriter.putByte(this.realdir);
		byteBufferWriter.putInt(this.realt);
	}

	public int getPacketSizeBytes() {
		return 43;
	}

	public boolean set(IsoPlayer player) {
		this.id = (short)player.OnlineID;
		return player.networkAI.set(this);
	}

	public static class l_send {
		public static PlayerPacket playerPacket = new PlayerPacket();
	}

	public static class l_receive {
		public static PlayerPacket playerPacket = new PlayerPacket();
	}
}
