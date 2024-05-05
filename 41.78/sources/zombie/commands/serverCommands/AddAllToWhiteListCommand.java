package zombie.commands.serverCommands;

import java.sql.SQLException;
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
@CommandName(name = "addalltowhitelist")
@CommandHelp(helpText = "UI_ServerOptionDesc_AddAllWhitelist")
@RequiredRight(requiredRights = 48)
public class AddAllToWhiteListCommand extends CommandBase {

	public AddAllToWhiteListCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		StringBuilder stringBuilder = new StringBuilder("");
		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			if (udpConnection.password != null && !udpConnection.password.equals("")) {
				ZLogger zLogger = LoggerManager.getLogger("admin");
				String string = this.getExecutorUsername();
				zLogger.write(string + " created user " + udpConnection.username + " with password " + udpConnection.password);
				try {
					stringBuilder.append(ServerWorldDatabase.instance.addUser(udpConnection.username, udpConnection.password)).append(" <LINE> ");
				} catch (SQLException sQLException) {
					sQLException.printStackTrace();
				}
			} else {
				stringBuilder.append("User ").append(udpConnection.username).append(" doesn\'t have a password. <LINE> ");
			}
		}

		stringBuilder.append("Done.");
		return stringBuilder.toString();
	}
}
