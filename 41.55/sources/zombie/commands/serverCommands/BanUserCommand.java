package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;
import zombie.network.Userlog;


@CommandName(name = "banuser")
@AltCommandArgs({@CommandArgs(required = {"(.+)"}), @CommandArgs(required = {"(.+)", "-r", "(.+)"})})
@CommandHelp(helpText = "UI_ServerOptionDesc_BanUser")
@RequiredRight(requiredRights = 36)
public class BanUserCommand extends CommandBase {
	private String reason = "";

	public BanUserCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() throws SQLException {
		String string = this.getCommandArg(0);
		if (this.hasOptionalArg(1)) {
			this.reason = this.getCommandArg(1);
		}

		String string2 = ServerWorldDatabase.instance.banUser(string, true);
		ServerWorldDatabase.instance.addUserlog(string, Userlog.UserlogType.Banned, this.reason, this.getExecutorUsername(), 1);
		ZLogger zLogger = LoggerManager.getLogger("admin");
		String string3 = this.getExecutorUsername();
		zLogger.write(string3 + " banned user " + string + (this.reason != null ? this.reason : ""), "IMPORTANT");
		boolean boolean1 = false;
		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			if (udpConnection.username.equals(string)) {
				boolean1 = true;
				if (SteamUtils.isSteamModeEnabled()) {
					LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " banned steamid " + udpConnection.steamID + "(" + udpConnection.username + ")" + (this.reason != null ? this.reason : ""), "IMPORTANT");
					String string4 = SteamUtils.convertSteamIDToString(udpConnection.steamID);
					ServerWorldDatabase.instance.banSteamID(string4, this.reason, true);
				} else {
					LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " banned ip " + udpConnection.ip + "(" + udpConnection.username + ")" + (this.reason != null ? this.reason : ""), "IMPORTANT");
					ServerWorldDatabase.instance.banIp(udpConnection.ip, string, this.reason, true);
				}

				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)83, byteBufferWriter);
				if ("".equals(this.reason)) {
					byteBufferWriter.putUTF("You have been banned from this server.");
				} else {
					byteBufferWriter.putUTF("You have been banned from this server by reason: " + this.reason);
				}

				udpConnection.endPacketImmediate();
				udpConnection.forceDisconnect();
				break;
			}
		}

		if (boolean1 && ServerOptions.instance.BanKickGlobalSound.getValue()) {
			GameServer.PlaySoundAtEveryPlayer("Thunder");
		}

		return string2;
	}
}
