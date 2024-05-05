package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.PacketValidator;
import zombie.network.ServerOptions;
import zombie.network.packets.hit.Perk;


public class AddXp implements INetworkPacket {
	public final PlayerID target = new PlayerID();
	protected Perk perk = new Perk();
	protected int amount = 0;

	public void set(IsoPlayer player, PerkFactory.Perk perk, int int1) {
		this.target.set(player);
		this.perk.set(perk);
		this.amount = int1;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.target.parse(byteBuffer, udpConnection);
		this.target.parsePlayer(udpConnection);
		this.perk.parse(byteBuffer, udpConnection);
		this.amount = byteBuffer.getInt();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		this.target.write(byteBufferWriter);
		this.perk.write(byteBufferWriter);
		byteBufferWriter.putInt(this.amount);
	}

	public void process() {
		if (this.target.player != null && !this.target.player.isDead()) {
			if (this.target.getCharacter() != null && !this.target.getCharacter().isDead()) {
				this.target.getCharacter().getXp().AddXP(this.perk.getPerk(), (float)this.amount, false, false, true);
			}
		}
	}

	public boolean isConsistent() {
		return this.target.isConsistent() && this.perk.isConsistent();
	}

	public boolean validate(UdpConnection udpConnection) {
		if (udpConnection.accessLevel != 1 && udpConnection.accessLevel != 2) {
			return true;
		} else if (!udpConnection.havePlayer(this.target.getCharacter())) {
			if (ServerOptions.instance.AntiCheatProtectionType14.getValue() && PacketValidator.doKickUser(udpConnection, this.getClass().getSimpleName(), "UI_ValidationFailed_Type14")) {
				LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\"", udpConnection.username, "UI_ValidationFailed_Type14", this.getClass().getSimpleName()));
			}

			return false;
		} else if ((double)this.amount > 1000.0 * SandboxOptions.instance.XpMultiplier.getValue() * ServerOptions.instance.AntiCheatProtectionType15ThresholdMultiplier.getValue()) {
			if (ServerOptions.instance.AntiCheatProtectionType15.getValue() && PacketValidator.doKickUser(udpConnection, this.getClass().getSimpleName(), "UI_ValidationFailed_Type15")) {
				LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\"", udpConnection.username, "UI_ValidationFailed_Type15", this.getClass().getSimpleName()));
			}

			return false;
		} else {
			if ((double)this.amount > 1000.0 * SandboxOptions.instance.XpMultiplier.getValue() * ServerOptions.instance.AntiCheatProtectionType15ThresholdMultiplier.getValue() / 2.0) {
				LoggerManager.getLogger("user").write(String.format("Warning: player=\"%s\" type=\"%s\" issuer=\"%s\"", udpConnection.username, "UI_ValidationFailed_Type15", this.getClass().getSimpleName()));
			}

			return true;
		}
	}

	public String getDescription() {
		String string = "\n\t" + this.getClass().getSimpleName() + " [";
		string = string + "target=" + this.target.getDescription() + " | ";
		string = string + "perk=" + this.perk.getDescription() + " | ";
		string = string + "amount=" + this.amount + "] ";
		return string;
	}
}
