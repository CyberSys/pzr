package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.iso.IsoDirections;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;


public class DeadBodyPacket implements INetworkPacket {
	public static int DIED_UNDER_VEHICLE = 65536;
	short id;
	public float x;
	public float y;
	public float z;
	public float angle;
	public IsoDirections direction;
	public boolean isFallOnFront;
	public boolean isCrawling;
	public int lastPlayerHit;
	public boolean isServer;
	public IsoZombie zombie;

	public void set(IsoZombie zombie) {
		this.zombie = zombie;
		this.id = (short)zombie.getOnlineID();
		this.x = zombie.getX();
		this.y = zombie.getY();
		this.z = zombie.getZ();
		this.angle = zombie.getAnimAngleRadians();
		this.direction = zombie.getDir();
		this.isFallOnFront = zombie.isFallOnFront();
		this.isCrawling = zombie.isCrawling();
		this.lastPlayerHit = zombie.lastPlayerHit;
		this.isServer = GameServer.bServer;
	}

	public void parse(ByteBuffer byteBuffer) {
		this.id = byteBuffer.getShort();
		this.x = byteBuffer.getFloat();
		this.y = byteBuffer.getFloat();
		this.z = byteBuffer.getFloat();
		this.angle = byteBuffer.getFloat();
		this.direction = IsoDirections.fromIndex(byteBuffer.get());
		this.isFallOnFront = byteBuffer.get() == 1;
		this.isCrawling = byteBuffer.get() == 1;
		this.lastPlayerHit = byteBuffer.getInt();
		this.isServer = byteBuffer.get() == 1;
		if (GameServer.bServer) {
			this.zombie = ServerMap.instance.ZombieMap.get(this.id);
		} else if (GameClient.bClient) {
			this.zombie = (IsoZombie)GameClient.IDToZombieMap.get(this.id);
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.id);
		byteBufferWriter.putFloat(this.x);
		byteBufferWriter.putFloat(this.y);
		byteBufferWriter.putFloat(this.z);
		byteBufferWriter.putFloat(this.angle);
		byteBufferWriter.putByte((byte)this.direction.index());
		byteBufferWriter.putBoolean(this.isFallOnFront);
		byteBufferWriter.putBoolean(this.isCrawling);
		byteBufferWriter.putInt(this.lastPlayerHit);
		byteBufferWriter.putBoolean(this.isServer);
	}

	public int getPacketSizeBytes() {
		return 0;
	}

	public String getDescription() {
		String string = String.format("id=%d, dying=%b, server=%b, wielder=%d, weapon=%b, angle=%f, direction=%s, front=%b, crawling=%b, pos=( %f ; %f ; %f )", this.id, this.zombie == null ? "unknown" : this.zombie.networkAI.deadZombie != null, this.isServer, this.lastPlayerHit & ~DIED_UNDER_VEHICLE, (this.lastPlayerHit & DIED_UNDER_VEHICLE) == 0, this.angle, this.direction, this.isFallOnFront, this.isCrawling, this.x, this.y, this.z);
		if (this.zombie != null) {
			string = string + ", states=( " + this.zombie.getPreviousActionContextStateName() + " > " + this.zombie.getCurrentActionContextStateName() + " )";
		}

		return string;
	}
}
