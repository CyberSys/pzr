package zombie.commands.serverCommands;

import zombie.Lua.LuaManager;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;


@CommandName(name = "checkModsNeedUpdate")
@CommandHelp(helpText = "UI_ServerOptionDesc_CheckModsNeedUpdate")
@RequiredRight(requiredRights = 62)
public class CheckModsNeedUpdate extends CommandBase {

	public CheckModsNeedUpdate(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		LuaManager.GlobalObject.checkModsNeedUpdate(this.connection);
		return "Checking started. The answer will be written in the log file and in the chat";
	}
}
