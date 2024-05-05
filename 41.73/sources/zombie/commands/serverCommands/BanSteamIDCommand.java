package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;


@CommandName(name = "banid")
@CommandArgs(required = {"(.+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_BanSteamId")
@RequiredRight(requiredRights = 48)
public class BanSteamIDCommand extends CommandBase {

	public BanSteamIDCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() throws SQLException {
		String string = this.getCommandArg(0);
		if (!SteamUtils.isSteamModeEnabled()) {
			return "Server is not in Steam mode";
		} else if (!SteamUtils.isValidSteamID(string)) {
			return "Expected SteamID but got \"" + string + "\"";
		} else {
			LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " banned SteamID " + string, "IMPORTANT");
			ServerWorldDatabase.instance.banSteamID(string, "", true);
			long long1 = SteamUtils.convertStringToSteamID(string);
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection.steamID == long1) {
					GameServer.kick(udpConnection, "UI_Policy_Ban", (String)null);
					udpConnection.forceDisconnect("command-ban-sid");
					break;
				}
			}

			return "SteamID " + string + " is now banned";
		}
	}
}
