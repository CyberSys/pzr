package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.packets.INetworkPacket;


public class Square implements IPositional,INetworkPacket {
	protected float positionX;
	protected float positionY;
	protected float positionZ;
	protected IsoGridSquare square;

	public void set(IsoGameCharacter gameCharacter) {
		this.square = gameCharacter.getAttackTargetSquare();
		if (this.square != null) {
			this.positionX = (float)this.square.getX();
			this.positionY = (float)this.square.getY();
			this.positionZ = (float)this.square.getZ();
		} else {
			this.positionX = 0.0F;
			this.positionY = 0.0F;
			this.positionZ = 0.0F;
		}
	}

	public void set(IsoGridSquare square) {
		this.square = square;
		if (this.square != null) {
			this.positionX = (float)this.square.getX();
			this.positionY = (float)this.square.getY();
			this.positionZ = (float)this.square.getZ();
		} else {
			this.positionX = 0.0F;
			this.positionY = 0.0F;
			this.positionZ = 0.0F;
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.positionX = byteBuffer.getFloat();
		this.positionY = byteBuffer.getFloat();
		this.positionZ = byteBuffer.getFloat();
		if (GameServer.bServer) {
			this.square = ServerMap.instance.getGridSquare((int)Math.floor((double)this.positionX), (int)Math.floor((double)this.positionY), (int)Math.floor((double)this.positionZ));
		}

		if (GameClient.bClient) {
			this.square = IsoWorld.instance.CurrentCell.getGridSquare((int)Math.floor((double)this.positionX), (int)Math.floor((double)this.positionY), (int)Math.floor((double)this.positionZ));
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putFloat(this.positionX);
		byteBufferWriter.putFloat(this.positionY);
		byteBufferWriter.putFloat(this.positionZ);
	}

	public String getDescription() {
		return "\n\tSquare [ pos=( " + this.positionX + " ; " + this.positionY + " ; " + this.positionZ + " ) ]";
	}

	void process(IsoGameCharacter gameCharacter) {
		gameCharacter.setAttackTargetSquare(gameCharacter.getCell().getGridSquare((double)this.positionX, (double)this.positionY, (double)this.positionZ));
	}

	public float getX() {
		return this.positionX;
	}

	public float getY() {
		return this.positionY;
	}

	public float getZ() {
		return this.positionZ;
	}

	public IsoGridSquare getSquare() {
		return this.square;
	}

	public boolean isConsistent() {
		return this.square != null;
	}
}
