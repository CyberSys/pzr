package zombie.commands.serverCommands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.ServerOptions;


@CommandName(name = "help")
@CommandArgs(optional = "(\\w+)")
@CommandHelp(helpText = "UI_ServerOptionDesc_Help")
@RequiredRight(requiredRights = 32)
public class HelpCommand extends CommandBase {

	public HelpCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getCommandArg(0);
		if (string != null) {
			Class javaClass = findCommandCls(string);
			return javaClass != null ? getHelp(javaClass) : "Unknown command /" + string;
		} else {
			String string2 = " <LINE> ";
			StringBuilder stringBuilder = new StringBuilder();
			if (this.connection == null) {
				string2 = "\n";
			}

			if (!GameServer.bServer) {
				ArrayList arrayList = ServerOptions.getClientCommandList(this.connection != null);
				Iterator iterator = arrayList.iterator();
				while (iterator.hasNext()) {
					String string3 = (String)iterator.next();
					stringBuilder.append(string3);
				}
			}

			stringBuilder.append("List of ").append("server").append(" commands : ");
			String string4 = "";
			TreeMap treeMap = new TreeMap();
			Class[] classArray = getSubClasses();
			int int1 = classArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				Class javaClass2 = classArray[int2];
				if (!isDisabled(javaClass2)) {
					string4 = getHelp(javaClass2);
					if (string4 != null) {
						treeMap.put(getCommandName(javaClass2), string4);
					}
				}
			}

			Iterator iterator2 = treeMap.entrySet().iterator();
			while (iterator2.hasNext()) {
				Entry entry = (Entry)iterator2.next();
				stringBuilder.append(string2).append("* ").append((String)entry.getKey()).append(" : ").append((String)entry.getValue());
			}

			return stringBuilder.toString();
		}
	}
}
