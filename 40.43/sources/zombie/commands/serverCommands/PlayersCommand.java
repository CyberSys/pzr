package zombie.commands.serverCommands;

import java.util.ArrayList;
import java.util.Iterator;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;


@CommandName(name = "players")
@CommandHelp(helpText = "UI_ServerOptionDesc_Players")
@RequiredRight(requiredRights = 61)
public class PlayersCommand extends CommandBase {

	public PlayersCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			for (int int2 = 0; int2 < 4; ++int2) {
				if (udpConnection.usernames[int2] != null) {
					arrayList.add(udpConnection.usernames[int2]);
				}
			}
		}

		StringBuilder stringBuilder = new StringBuilder("Players connected (" + arrayList.size() + "): ");
		String string = " <LINE> ";
		if (this.connection == null) {
			string = "\n";
		}

		stringBuilder.append(string);
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			String string2 = (String)iterator.next();
			stringBuilder.append("-").append(string2).append(string);
		}

		return stringBuilder.toString();
	}
}
