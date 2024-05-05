package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.ServerMap;


@CommandName(name = "save")
@CommandHelp(helpText = "UI_ServerOptionDesc_Save")
@RequiredRight(requiredRights = 32)
public class SaveCommand extends CommandBase {

	public SaveCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		ServerMap.instance.QueueSaveAll();
		GameServer.PauseAllClients();
		return "World saved";
	}
}
