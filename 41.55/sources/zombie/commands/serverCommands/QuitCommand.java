package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.network.ServerMap;


@CommandName(name = "quit")
@CommandHelp(helpText = "UI_ServerOptionDesc_Quit")
@RequiredRight(requiredRights = 32)
public class QuitCommand extends CommandBase {

	public QuitCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		ServerMap.instance.QueueSaveAll();
		ServerMap.instance.QueueQuit();
		LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " closed server");
		return "Quit";
	}
}
