package zombie.commands.serverCommands;

import java.util.Iterator;
import zombie.Lua.LuaManager;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;


@CommandName(name = "reloadlua")
@CommandArgs(required = {"(\\S+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_ReloadLua")
@RequiredRight(requiredRights = 32)
public class ReloadLuaCommand extends CommandBase {

	public ReloadLuaCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getCommandArg(0);
		Iterator iterator = LuaManager.loaded.iterator();
		String string2;
		do {
			if (!iterator.hasNext()) {
				return "Unknown Lua file";
			}

			string2 = (String)iterator.next();
		} while (!string2.endsWith(string));

		LuaManager.loaded.remove(string2);
		LuaManager.RunLua(string2, true);
		return "Lua file reloaded";
	}
}
