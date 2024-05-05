package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;


@CommandName(name = "debugplayer")
@CommandArgs(required = {"(.+)"})
@RequiredRight(requiredRights = 32)
public class DebugPlayerCommand extends CommandBase {

	public DebugPlayerCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		if (this.getCommandArgsCount() != 1) {
			return "/debugplayer \"username\"";
		} else {
			String string = this.getCommandArg(0);
			IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string);
			if (player == null) {
				return "no such user";
			} else {
				UdpConnection udpConnection = GameServer.getConnectionByPlayerOnlineID(player.OnlineID);
				if (udpConnection == null) {
					return "no connection for user";
				} else if (GameServer.DebugPlayer.contains(udpConnection)) {
					GameServer.DebugPlayer.remove(udpConnection);
					return "debug off";
				} else {
					GameServer.DebugPlayer.add(udpConnection);
					return "debug on";
				}
			}
		}
	}
}
