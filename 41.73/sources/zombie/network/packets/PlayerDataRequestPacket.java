package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;


public class PlayerDataRequestPacket implements INetworkPacket {
	short playerId = -1;

	public void set(short short1) {
		this.playerId = short1;
	}

	public void process(UdpConnection udpConnection) {
		IsoPlayer player = (IsoPlayer)GameServer.IDToPlayerMap.get(this.playerId);
		if (udpConnection.RelevantTo(player.x, player.y) && !player.isInvisible() || udpConnection.accessLevel >= 1) {
			GameServer.sendPlayerConnect(player, udpConnection);
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.playerId = byteBuffer.getShort();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.playerId);
	}
}
