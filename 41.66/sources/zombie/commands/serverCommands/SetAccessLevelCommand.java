package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.characters.IsoPlayer;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.PlayerType;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;
import zombie.network.chat.ChatServer;


@CommandName(name = "setaccesslevel")
@CommandArgs(required = {"(.+)", "(\\w+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_SetAccessLevel")
@RequiredRight(requiredRights = 48)
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
			byte byte1 = PlayerType.fromString(string3.trim().toLowerCase());
			if (udpConnection != null && udpConnection.accessLevel == 16 && byte1 == 32) {
				return "Moderators can\'t set Admin access level";
			} else if (byte1 == 0) {
				return "Access Level \'" + byte1 + "\' unknown, list of access level: player, admin, moderator, overseer, gm, observer";
			} else {
				if (player != null) {
					UdpConnection udpConnection2 = GameServer.getConnectionFromPlayer(player);
					byte byte2;
					if (udpConnection2 != null) {
						byte2 = udpConnection2.accessLevel;
					} else {
						byte2 = PlayerType.fromString(player.accessLevel.toLowerCase());
					}

					if (byte2 != byte1) {
						if (byte1 == 32) {
							ChatServer.getInstance().joinAdminChat(player.OnlineID);
						} else if (byte2 == 32 && byte1 != 32) {
							ChatServer.getInstance().leaveAdminChat(player.OnlineID);
						}
					}

					player.accessLevel = PlayerType.toString(byte1);
					if (udpConnection2 != null) {
						udpConnection2.accessLevel = byte1;
					}

					if ((byte1 & 62) != 0) {
						player.setGodMod(true);
						player.setGhostMode(true);
						player.setInvisible(true);
					} else {
						player.setGodMod(false);
						player.setGhostMode(false);
						player.setInvisible(false);
					}

					GameServer.sendPlayerExtraInfo(player, (UdpConnection)null);
				}

				LoggerManager.getLogger("admin").write(string + " granted " + byte1 + " access level on " + string2);
				return udpConnection != null && udpConnection.isCoopHost ? "Your access level is now: " + byte1 : ServerWorldDatabase.instance.setAccessLevel(string2, PlayerType.toString(byte1));
			}
		}
	}
}
