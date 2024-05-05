package zombie.commands.serverCommands;

import java.sql.SQLException;
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
import zombie.network.ServerWorldDatabase;


@CommandName(name = "unbanuser")
@CommandArgs(required = {"(.+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_UnBanUser")
@RequiredRight(requiredRights = 48)
public class UnbanUserCommand extends CommandBase {

	public UnbanUserCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() throws SQLException {
		String string = this.getCommandArg(0);
		String string2 = ServerWorldDatabase.instance.banUser(string, false);
		ZLogger zLogger = LoggerManager.getLogger("admin");
		String string3 = this.getExecutorUsername();
		zLogger.write(string3 + " unbanned user " + string);
		if (!SteamUtils.isSteamModeEnabled()) {
			ServerWorldDatabase.instance.banIp((String)null, string, (String)null, false);
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection.username.equals(string)) {
					ServerWorldDatabase.instance.banIp(udpConnection.ip, string, (String)null, false);
					break;
				}
			}
		}

		return string2;
	}
}
