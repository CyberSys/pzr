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
import zombie.core.secure.PZcrypt;
import zombie.network.ServerWorldDatabase;


@CommandName(name = "adduser")
@CommandArgs(required = {"(.+)", "(.+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_AddUser")
@RequiredRight(requiredRights = 48)
public class AddUserCommand extends CommandBase {

	public AddUserCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getCommandArg(0);
		String string2 = PZcrypt.hash(ServerWorldDatabase.encrypt(this.getCommandArg(1)));
		if (!ServerWorldDatabase.isValidUserName(string)) {
			return "Invalid username \"" + string + "\"";
		} else {
			ZLogger zLogger = LoggerManager.getLogger("admin");
			String string3 = this.getExecutorUsername();
			zLogger.write(string3 + " created user " + string.trim() + " with password " + string2.trim());
			try {
				return ServerWorldDatabase.instance.addUser(string.trim(), string2.trim());
			} catch (SQLException sQLException) {
				sQLException.printStackTrace();
				return "exception occurs";
			}
		}
	}
}
