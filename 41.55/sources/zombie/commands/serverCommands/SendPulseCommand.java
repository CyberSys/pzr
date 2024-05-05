package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.PacketTypes;


@CommandName(name = "sendpulse")
@CommandHelp(helpText = "UI_ServerOptionDesc_SendPulse")
@RequiredRight(requiredRights = 32)
public class SendPulseCommand extends CommandBase {

	public SendPulseCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		if (this.connection != null) {
			this.connection.sendPulse = !this.connection.sendPulse;
			if (!this.connection.sendPulse) {
				ByteBufferWriter byteBufferWriter = this.connection.startPacket();
				PacketTypes.doPacket((short)1, byteBufferWriter);
				byteBufferWriter.putLong(-1L);
				this.connection.endPacket();
			}

			return "Pulse " + (this.connection.sendPulse ? "on" : "off");
		} else {
			return "can\'t do this from the server console";
		}
	}
}
