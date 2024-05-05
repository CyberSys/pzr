package zombie.commands.serverCommands;

import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.chat.ChatServer;


@CommandName(name = "servermsg")
@CommandArgs(required = {"(.+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_ServerMsg")
@RequiredRight(requiredRights = 56)
public class ServerMessageCommand extends CommandBase {

	public ServerMessageCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getCommandArg(0);
		if (this.connection == null) {
			ChatServer.getInstance().sendServerAlertMessageToServerChat(string);
		} else {
			String string2 = this.getExecutorUsername();
			ChatServer.getInstance().sendServerAlertMessageToServerChat(string2, string);
		}

		return "Message sent.";
	}
}
