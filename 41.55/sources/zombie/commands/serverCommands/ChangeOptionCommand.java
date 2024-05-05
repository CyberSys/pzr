package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamGameServer;
import zombie.core.znet.SteamUtils;
import zombie.network.CoopSlave;
import zombie.network.GameServer;
import zombie.network.ServerOptions;


@CommandName(name = "changeoption")
@CommandArgs(required = {"(\\w+)", "(.*)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_ChangeOptions")
@RequiredRight(requiredRights = 32)
public class ChangeOptionCommand extends CommandBase {

	public ChangeOptionCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() throws SQLException {
		String string = this.getCommandArg(0);
		String string2 = this.getCommandArg(1);
		String string3 = ServerOptions.instance.changeOption(string, string2);
		if (string.equals("Password")) {
			GameServer.udpEngine.SetServerPassword(GameServer.udpEngine.hashServerPassword(ServerOptions.instance.Password.getValue()));
		}

		if (string.equals("ClientCommandFilter")) {
			GameServer.initClientCommandFilter();
		}

		if (SteamUtils.isSteamModeEnabled()) {
			SteamGameServer.SetServerName(ServerOptions.instance.PublicName.getValue());
			SteamGameServer.SetKeyValue("description", ServerOptions.instance.PublicDescription.getValue());
			SteamGameServer.SetKeyValue("open", ServerOptions.instance.Open.getValue() ? "1" : "0");
			SteamGameServer.SetKeyValue("public", ServerOptions.instance.Public.getValue() ? "1" : "0");
			if (ServerOptions.instance.Public.getValue()) {
				String string4 = ServerOptions.instance.Mods.getValue();
				SteamGameServer.SetGameTags(string4 + (CoopSlave.instance != null ? ";hosted" : ""));
			} else {
				SteamGameServer.SetGameTags("hidden" + (CoopSlave.instance != null ? ";hosted" : ""));
			}
		}

		LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " changed option " + string + "=" + string2);
		return string3;
	}
}
