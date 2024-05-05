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
import zombie.network.ServerWorldDatabase;


@CommandName(name = "removeuserfromwhitelist")
@CommandArgs(required = {"(.+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_RemoveWhitelist")
@RequiredRight(requiredRights = 36)
public class RemoveUserFromWhiteList extends CommandBase {

	public RemoveUserFromWhiteList(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() throws SQLException {
		String string = this.getCommandArg(0);
		ZLogger zLogger = LoggerManager.getLogger("admin");
		String string2 = this.getExecutorUsername();
		zLogger.write(string2 + " removed user " + string + " from whitelist");
		return ServerWorldDatabase.instance.removeUser(string);
	}
}
