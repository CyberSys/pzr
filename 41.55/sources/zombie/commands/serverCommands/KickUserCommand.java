package zombie.commands.serverCommands;

import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandNames;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;
import zombie.network.Userlog;


@CommandNames({@CommandName(name = "kickuser"), @CommandName(name = "disconnect")})
@CommandArgs(required = {"(.+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_Kick")
@RequiredRight(requiredRights = 44)
public class KickUserCommand extends CommandBase {

	public KickUserCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getCommandArg(0);
		ZLogger zLogger = LoggerManager.getLogger("admin");
		String string2 = this.getExecutorUsername();
		zLogger.write(string2 + " kicked user " + string);
		ServerWorldDatabase.instance.addUserlog(string, Userlog.UserlogType.Kicked, "", "server", 1);
		boolean boolean1 = false;
		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			for (int int2 = 0; int2 < 4; ++int2) {
				if (string.equals(udpConnection.usernames[int2])) {
					boolean1 = true;
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.doPacket((short)83, byteBufferWriter);
					byteBufferWriter.putUTF("You have been kicked from this server.");
					udpConnection.endPacketImmediate();
					udpConnection.forceDisconnect();
					GameServer.addDisconnect(udpConnection);
					break;
				}
			}
		}

		if (boolean1 && ServerOptions.instance.BanKickGlobalSound.getValue()) {
			GameServer.PlaySoundAtEveryPlayer("RumbleThunder");
		}

		if (boolean1) {
			return "User " + string + " kicked.";
		} else {
			return "User " + string + " doesn\'t exist.";
		}
	}
}
