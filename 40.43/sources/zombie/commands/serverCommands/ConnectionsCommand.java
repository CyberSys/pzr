package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandNames;
import zombie.commands.DisabledCommand;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;


@DisabledCommand
@CommandNames({@CommandName(name = "connections"), @CommandName(name = "list")})
@CommandHelp(helpText = "UI_ServerOptionDesc_Connections")
@RequiredRight(requiredRights = 44)
public class ConnectionsCommand extends CommandBase {

	public ConnectionsCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = "";
		String string2 = " <LINE> ";
		if (this.connection == null) {
			string2 = "\n";
		}

		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			for (int int2 = 0; int2 < 4; ++int2) {
				if (udpConnection.usernames[int2] != null) {
					string = string + "connection=" + (int1 + 1) + "/" + GameServer.udpEngine.connections.size() + " " + udpConnection.idStr + " player=" + (int2 + 1) + "/" + 4 + " id=" + udpConnection.playerIDs[int2] + " username=\"" + udpConnection.usernames[int2] + "\" fullyConnected=" + udpConnection.isFullyConnected() + string2;
				}
			}
		}

		return string + "Players listed";
	}
}
