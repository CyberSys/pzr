package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;


public class DeadZombiePacket extends DeadCharacterPacket implements INetworkPacket {
	private byte zombieFlags;
	private IsoZombie zombie;

	public void set(IsoGameCharacter gameCharacter) {
		super.set(gameCharacter);
		this.zombie = (IsoZombie)gameCharacter;
		this.zombieFlags |= (byte)(this.zombie.isCrawling() ? 1 : 0);
	}

	public void process() {
		if (this.zombie != null) {
			this.zombie.setCrawler((this.zombieFlags & 1) != 0);
			super.process();
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		if (GameServer.bServer) {
			this.zombie = ServerMap.instance.ZombieMap.get(this.id);
		} else if (GameClient.bClient) {
			this.zombie = (IsoZombie)GameClient.IDToZombieMap.get(this.id);
		}

		if (this.zombie != null) {
			this.character = this.zombie;
			if (!GameServer.bServer || !this.zombie.isReanimatedPlayer()) {
				this.parseCharacterInventory(byteBuffer);
			}

			this.character.setHealth(0.0F);
			this.character.getHitReactionNetworkAI().process(this.x, this.y, this.z, this.angle);
			this.character.getNetworkCharacterAI().setDeadBody(this);
		} else {
			this.parseDeadBodyInventory(byteBuffer);
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		this.writeCharacterInventory(byteBufferWriter);
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + String.format(" | isCrawling=%b", (this.zombieFlags & 1) != 0);
	}

	public IsoZombie getZombie() {
		return this.zombie;
	}
}
