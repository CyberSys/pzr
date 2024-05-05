package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class DeadPlayerPacket extends DeadCharacterPacket implements INetworkPacket {
	private byte playerFlags;
	private float infectionLevel;
	private IsoPlayer player;

	public void set(IsoGameCharacter gameCharacter) {
		super.set(gameCharacter);
		this.player = (IsoPlayer)gameCharacter;
		if (GameClient.bClient) {
			this.id = (short)this.player.getPlayerNum();
		}

		this.infectionLevel = this.player.getBodyDamage().getInfectionLevel();
		this.playerFlags |= (byte)(this.player.getBodyDamage().isInfected() ? 1 : 0);
	}

	public void process() {
		if (this.player != null) {
			this.character.setHealth(0.0F);
			this.player.getBodyDamage().setOverallBodyHealth(0.0F);
			this.player.getBodyDamage().setInfected((this.playerFlags & 1) != 0);
			this.player.getBodyDamage().setInfectionLevel(this.infectionLevel);
			super.process();
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.infectionLevel = byteBuffer.getFloat();
		if (GameServer.bServer) {
			this.player = GameServer.getPlayerFromConnection(udpConnection, this.id);
		} else if (GameClient.bClient) {
			this.player = (IsoPlayer)GameClient.IDToPlayerMap.get(this.id);
		}

		if (this.player != null) {
			this.character = this.player;
			this.parseCharacterInventory(byteBuffer);
			this.character.setHealth(0.0F);
			this.character.getBodyDamage().setOverallBodyHealth(0.0F);
			this.character.getNetworkCharacterAI().setDeadBody(this);
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		byteBufferWriter.putFloat(this.infectionLevel);
		this.writeCharacterInventory(byteBufferWriter);
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + String.format(" | isInfected=%b infectionLevel=%f", (this.playerFlags & 1) != 0, this.infectionLevel);
	}

	public IsoPlayer getPlayer() {
		return this.player;
	}
}
