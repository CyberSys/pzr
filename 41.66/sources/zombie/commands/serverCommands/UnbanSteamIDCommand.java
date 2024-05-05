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
import zombie.network.ServerWorldDatabase;


@CommandName(name = "unbanid")
@CommandArgs(required = {"(.+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_UnBanSteamId")
@RequiredRight(requiredRights = 48)
public class UnbanSteamIDCommand extends CommandBase {

	public UnbanSteamIDCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() throws SQLException {
		String string = this.getCommandArg(0);
		if (!SteamUtils.isSteamModeEnabled()) {
			return "Server is not in Steam mode";
		} else if (!SteamUtils.isValidSteamID(string)) {
			return "Expected SteamID but got \"" + string + "\"";
		} else {
			LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " unbanned steamid " + string, "IMPORTANT");
			ServerWorldDatabase.instance.banSteamID(string, "", false);
			return "SteamID " + string + " is now unbanned";
		}
	}
}
