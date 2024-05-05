package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.HitReactionNetworkAI;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.packets.INetworkPacket;


public class Fall implements INetworkPacket {
	protected float dropPositionX;
	protected float dropPositionY;
	protected byte dropPositionZ;
	protected float dropDirection;

	public void set(HitReactionNetworkAI hitReactionNetworkAI) {
		this.dropPositionX = hitReactionNetworkAI.finalPosition.x;
		this.dropPositionY = hitReactionNetworkAI.finalPosition.y;
		this.dropPositionZ = hitReactionNetworkAI.finalPositionZ;
		this.dropDirection = hitReactionNetworkAI.finalDirection.getDirection();
	}

	public void set(float float1, float float2, byte byte1, float float3) {
		this.dropPositionX = float1;
		this.dropPositionY = float2;
		this.dropPositionZ = byte1;
		this.dropDirection = float3;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.dropPositionX = byteBuffer.getFloat();
		this.dropPositionY = byteBuffer.getFloat();
		this.dropPositionZ = byteBuffer.get();
		this.dropDirection = byteBuffer.getFloat();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putFloat(this.dropPositionX);
		byteBufferWriter.putFloat(this.dropPositionY);
		byteBufferWriter.putByte(this.dropPositionZ);
		byteBufferWriter.putFloat(this.dropDirection);
	}

	public String getDescription() {
		return "\n\tFall [ direction=" + this.dropDirection + " | position=( " + this.dropPositionX + " ; " + this.dropPositionY + " ; " + this.dropPositionZ + " ) ]";
	}

	public void process(IsoGameCharacter gameCharacter) {
		if (this.isSetup() && gameCharacter.getHitReactionNetworkAI() != null) {
			gameCharacter.getHitReactionNetworkAI().process(this.dropPositionX, this.dropPositionY, (float)this.dropPositionZ, this.dropDirection);
		}
	}

	boolean isSetup() {
		return this.dropPositionX != 0.0F && this.dropPositionY != 0.0F;
	}
}
