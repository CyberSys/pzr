package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.characters.IsoPlayer;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;
import zombie.network.chat.ChatServer;


@CommandName(name = "setaccesslevel")
@CommandArgs(required = {"(.+)", "(\\w+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_SetAccessLevel")
@RequiredRight(requiredRights = 36)
public class SetAccessLevelCommand extends CommandBase {

	public SetAccessLevelCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() throws SQLException {
		String string = this.getCommandArg(0);
		String string2 = "none".equals(this.getCommandArg(1)) ? "" : this.getCommandArg(1);
		return update(this.getExecutorUsername(), this.connection, string, string2);
	}

	static String update(String string, UdpConnection udpConnection, String string2, String string3) throws SQLException {
		if ((udpConnection == null || !udpConnection.isCoopHost) && !ServerWorldDatabase.instance.containsUser(string2) && udpConnection != null) {
			return "User \"" + string2 + "\" is not in the whitelist, use /adduser first";
		} else {
			IsoPlayer player = GameServer.getPlayerByUserName(string2);
			if (udpConnection != null && udpConnection.accessLevel.equals("moderator") && string3.equals("admin")) {
				return "Moderators can\'t set Admin access level";
			} else if (!string3.equals("") && !string3.equals("admin") && !string3.equals("moderator") && !string3.equals("overseer") && !string3.equals("gm") && !string3.equals("observer")) {
				return "Access Level \'" + string3 + "\' unknown, list of access level: none, admin, moderator, overseer, gm, observer";
			} else {
				if (player != null) {
					UdpConnection udpConnection2 = GameServer.getConnectionFromPlayer(player);
					String string4 = "";
					if (udpConnection2 != null) {
						string4 = udpConnection2.accessLevel;
					} else {
						string4 = player.accessLevel;
					}

					if (!string4.equals(string3)) {
						if (string3.equals("admin")) {
							ChatServer.getInstance().joinAdminChat(player.OnlineID);
						} else if (string4.equals("admin") && !string3.equals("admin")) {
							ChatServer.getInstance().leaveAdminChat(player.OnlineID);
						}
					}

					player.accessLevel = string3;
					if (udpConnection2 != null) {
						udpConnection2.accessLevel = string3;
					}

					if (!string3.equals("admin") && !string3.equals("moderator") && !string3.equals("overseer") && !string3.equals("gm") && !string3.equals("observer")) {
						player.setGodMod(false);
						player.setGhostMode(false);
						player.setInvisible(false);
					} else {
						player.setGodMod(true);
						player.setGhostMode(true);
						player.setInvisible(true);
					}

					GameServer.sendPlayerExtraInfo(player, (UdpConnection)null);
				}

				LoggerManager.getLogger("admin").write(string + " granted " + string3 + " access level on " + string2);
				return udpConnection != null && udpConnection.isCoopHost ? "Your access level is now: " + string3 : ServerWorldDatabase.instance.setAccessLevel(string2, string3);
			}
		}
	}
}
