package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;


public class ZombiePacket implements INetworkPacket {
	public static final int PACKET_SIZE_BYTES = 57;
	public short id;
	public float x;
	public float y;
	public byte z;
	public int t;
	public int descriptorID;
	public int owner;
	public byte type;
	public int booleanVariables;
	public int target;
	public int eatBodyTarget;
	public int smParamTargetAngle;
	public float speedMod;
	public String walkType;
	public float realx;
	public float realy;
	public byte realz;
	public byte realdir;
	public float realHealth;

	public void parse(ByteBuffer byteBuffer) {
		this.id = byteBuffer.getShort();
		this.x = byteBuffer.getFloat();
		this.y = byteBuffer.getFloat();
		this.z = byteBuffer.get();
		this.t = byteBuffer.getInt();
		this.descriptorID = byteBuffer.getInt();
		this.owner = byteBuffer.getInt();
		this.type = byteBuffer.get();
		this.booleanVariables = byteBuffer.getInt();
		this.target = byteBuffer.getInt();
		this.eatBodyTarget = byteBuffer.getInt();
		this.smParamTargetAngle = byteBuffer.getInt();
		this.speedMod = byteBuffer.getFloat();
		this.walkType = GameWindow.ReadString(byteBuffer);
		this.realx = byteBuffer.getFloat();
		this.realy = byteBuffer.getFloat();
		this.realz = byteBuffer.get();
		this.realdir = byteBuffer.get();
		this.realHealth = byteBuffer.getFloat();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		long long1 = (long)byteBufferWriter.bb.position();
		byteBufferWriter.putShort(this.id);
		byteBufferWriter.putFloat(this.x);
		byteBufferWriter.putFloat(this.y);
		byteBufferWriter.putByte(this.z);
		byteBufferWriter.putInt(this.t);
		byteBufferWriter.putInt(this.descriptorID);
		byteBufferWriter.putInt(this.owner);
		byteBufferWriter.putByte(this.type);
		byteBufferWriter.putInt(this.booleanVariables);
		byteBufferWriter.putInt(this.target);
		byteBufferWriter.putInt(this.eatBodyTarget);
		byteBufferWriter.putInt(this.smParamTargetAngle);
		byteBufferWriter.putFloat(this.speedMod);
		byteBufferWriter.putUTF(this.walkType);
		byteBufferWriter.putFloat(this.realx);
		byteBufferWriter.putFloat(this.realy);
		byteBufferWriter.putByte(this.realz);
		byteBufferWriter.putByte(this.realdir);
		byteBufferWriter.putFloat(this.realHealth);
	}

	public int getPacketSizeBytes() {
		return 57;
	}

	public void set(IsoZombie zombie, int int1) {
		this.id = zombie.OnlineID;
		this.descriptorID = zombie.getPersistentOutfitID();
		zombie.networkAI.set(this, int1);
		zombie.thumpSent = true;
	}
}
