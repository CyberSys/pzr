package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.DisabledCommand;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;


@DisabledCommand
@CommandName(name = "addusertowhitelist")
@CommandArgs(required = {"(.+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_AddWhitelist")
@RequiredRight(requiredRights = 48)
public class AddUserToWhiteListCommand extends CommandBase {

	public AddUserToWhiteListCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() throws SQLException {
		String string = this.getCommandArg(0);
		if (!ServerWorldDatabase.isValidUserName(string)) {
			return "Invalid username \"" + string + "\"";
		} else {
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection.username.equals(string)) {
					if (udpConnection.password != null && !udpConnection.password.equals("")) {
						ZLogger zLogger = LoggerManager.getLogger("admin");
						String string2 = this.getExecutorUsername();
						zLogger.write(string2 + " created user " + udpConnection.username + " with password " + udpConnection.password);
						return ServerWorldDatabase.instance.addUser(udpConnection.username, udpConnection.password);
					}

					return "User " + string + " doesn\'t have a password.";
				}
			}

			return "User " + string + " not found.";
		}
	}
}
