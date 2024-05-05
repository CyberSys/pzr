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
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;
import zombie.network.Userlog;


@CommandName(name = "banuser")
@AltCommandArgs({@CommandArgs(required = {"(.+)"}, argName = "Ban User Only"), @CommandArgs(required = {"(.+)", "-ip"}, argName = "Ban User And IP"), @CommandArgs(required = {"(.+)", "-r", "(.+)"}, argName = "Ban User And Supply Reason"), @CommandArgs(required = {"(.+)", "-ip", "-r", "(.+)"}, argName = "Ban User And IP And Supply Reason")})
@CommandHelp(helpText = "UI_ServerOptionDesc_BanUser")
@RequiredRight(requiredRights = 48)
public class BanUserCommand extends CommandBase {
	private String reason = "";
	public static final String banUser = "Ban User Only";
	public static final String banWithIP = "Ban User And IP";
	public static final String banWithReason = "Ban User And Supply Reason";
	public static final String banWithReasonIP = "Ban User And IP And Supply Reason";

	public BanUserCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() throws SQLException {
		String string = this.getCommandArg(0);
		if (this.hasOptionalArg(1)) {
			this.reason = this.getCommandArg(1);
		}

		boolean boolean1 = false;
		String string2 = this.argsName;
		byte byte1 = -1;
		switch (string2.hashCode()) {
		case 842902356: 
			if (string2.equals("Ban User And IP")) {
				byte1 = 0;
			}

			break;
		
		case 1229258272: 
			if (string2.equals("Ban User And IP And Supply Reason")) {
				byte1 = 1;
			}

		
		}
		switch (byte1) {
		case 0: 
		
		case 1: 
			boolean1 = true;
		
		default: 
			string2 = ServerWorldDatabase.instance.banUser(string, true);
			ServerWorldDatabase.instance.addUserlog(string, Userlog.UserlogType.Banned, this.reason, this.getExecutorUsername(), 1);
			ZLogger zLogger = LoggerManager.getLogger("admin");
			String string3 = this.getExecutorUsername();
			zLogger.write(string3 + " banned user " + string + (this.reason != null ? this.reason : ""), "IMPORTANT");
			boolean boolean2 = false;
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection.username.equals(string)) {
					boolean2 = true;
					if (SteamUtils.isSteamModeEnabled()) {
						LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " banned steamid " + udpConnection.steamID + "(" + udpConnection.username + ")" + (this.reason != null ? this.reason : ""), "IMPORTANT");
						String string4 = SteamUtils.convertSteamIDToString(udpConnection.steamID);
						ServerWorldDatabase.instance.banSteamID(string4, this.reason, true);
					}

					if (boolean1) {
						LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " banned ip " + udpConnection.ip + "(" + udpConnection.username + ")" + (this.reason != null ? this.reason : ""), "IMPORTANT");
						ServerWorldDatabase.instance.banIp(udpConnection.ip, string, this.reason, true);
					}

					if ("".equals(this.reason)) {
						GameServer.kick(udpConnection, "UI_Policy_Ban", (String)null);
					} else {
						GameServer.kick(udpConnection, "You have been banned from this server for the following reason: " + this.reason, (String)null);
					}

					udpConnection.forceDisconnect("command-ban-ip");
					break;
				}
			}

			if (boolean2 && ServerOptions.instance.BanKickGlobalSound.getValue()) {
				GameServer.PlaySoundAtEveryPlayer("Thunder");
			}

			return string2;
		
		}
	}
}
