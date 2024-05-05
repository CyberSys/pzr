package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.characters.Safety;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.Type;


public class SafetyPacket extends Safety implements INetworkPacket {
	private short id;
	private IsoPlayer player;

	public SafetyPacket(Safety safety) {
		this.enabled = safety.isEnabled();
		this.last = safety.isLast();
		this.cooldown = safety.getCooldown();
		this.toggle = safety.getToggle();
		this.player = (IsoPlayer)Type.tryCastTo(safety.getCharacter(), IsoPlayer.class);
		if (this.player != null) {
			if (GameServer.bServer) {
				this.id = this.player.getOnlineID();
			} else if (GameClient.bClient) {
				this.id = (short)this.player.getPlayerNum();
			}
		}
	}

	public SafetyPacket() {
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.id = byteBuffer.getShort();
		super.load(byteBuffer, IsoWorld.getWorldVersion());
		if (GameServer.bServer) {
			this.player = GameServer.getPlayerFromConnection(udpConnection, this.id);
		} else if (GameClient.bClient) {
			this.player = (IsoPlayer)GameClient.IDToPlayerMap.get(this.id);
		} else {
			this.player = null;
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.id);
		super.save(byteBufferWriter.bb);
	}

	public int getPacketSizeBytes() {
		return 12;
	}

	public boolean isConsistent() {
		return this.player != null;
	}

	public String getDescription() {
		String string = INetworkPacket.super.getDescription();
		return string + (this.player == null ? ":" : ": \"" + this.player.getUsername() + "\"") + " id=" + this.id + " " + super.getDescription();
	}

	public void log(String string) {
		if (this.isConsistent()) {
			if (Core.bDebug) {
				DebugLog.Combat.debugln(string + ": " + this.getDescription());
			}

			if (GameServer.bServer) {
				LoggerManager.getLogger("pvp").write(String.format("user \"%s\" %s %s safety %s", this.player.getUsername(), LoggerManager.getPlayerCoords(this.player), this.player.getSafety().isEnabled() ? "enabled" : "disabled", this.player.getSafety().getDescription()));
			}
		}
	}

	public void process() {
		if (this.isConsistent()) {
			if (GameServer.bServer) {
				this.player.getSafety().toggleSafety();
			} else if (GameClient.bClient) {
				this.player.setSafety(this);
			}
		}
	}
}
