package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.packets.hit.Instance;


public class PlayerID extends Instance implements INetworkPacket {
	protected IsoPlayer player;
	protected byte playerIndex;

	public void set(IsoPlayer player) {
		super.set(player.OnlineID);
		this.playerIndex = player.isLocal() ? (byte)player.getPlayerNum() : -1;
		this.player = player;
	}

	public void parsePlayer(UdpConnection udpConnection) {
		if (GameServer.bServer) {
			if (udpConnection != null && this.playerIndex != -1) {
				this.player = GameServer.getPlayerFromConnection(udpConnection, this.playerIndex);
			} else {
				this.player = (IsoPlayer)GameServer.IDToPlayerMap.get(this.ID);
			}
		} else if (GameClient.bClient) {
			this.player = (IsoPlayer)GameClient.IDToPlayerMap.get(this.ID);
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.playerIndex = byteBuffer.get();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		byteBufferWriter.putByte(this.playerIndex);
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.getCharacter() != null;
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tPlayer [ player " + (this.player == null ? "?" : "\"" + this.player.getUsername() + "\"") + " ]";
	}

	public IsoPlayer getCharacter() {
		return this.player;
	}
}
