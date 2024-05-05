package zombie.commands.serverCommands;

import java.util.Iterator;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.ServerOptions;


@CommandName(name = "showoptions")
@CommandHelp(helpText = "UI_ServerOptionDesc_ShowOptions")
@RequiredRight(requiredRights = 63)
public class ShowOptionsCommand extends CommandBase {

	public ShowOptionsCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		Iterator iterator = ServerOptions.instance.getPublicOptions().iterator();
		String string = null;
		String string2 = " <LINE> ";
		if (this.connection == null) {
			string2 = "\n";
		}

		String string3 = "List of Server Options:" + string2;
		while (iterator.hasNext()) {
			string = (String)iterator.next();
			if (!string.equals("ServerWelcomeMessage")) {
				string3 = string3 + "* " + string + "=" + ServerOptions.instance.getOptionByName(string).asConfigOption().getValueAsString() + string2;
			}
		}

		string3 = string3 + "* ServerWelcomeMessage=" + ServerOptions.instance.ServerWelcomeMessage.getValue();
		return string3;
	}
}
