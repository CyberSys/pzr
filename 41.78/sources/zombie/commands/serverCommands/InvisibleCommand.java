package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;


@CommandName(name = "invisible")
@AltCommandArgs({@CommandArgs(required = {"(.+)"}, optional = "(-true|-false)"), @CommandArgs(optional = "(-true|-false)")})
@CommandHelp(helpText = "UI_ServerOptionDesc_Invisible")
@RequiredRight(requiredRights = 62)
public class InvisibleCommand extends CommandBase {

	public InvisibleCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getExecutorUsername();
		String string2 = this.getCommandArg(0);
		String string3 = this.getCommandArg(1);
		if (this.getCommandArgsCount() == 2 || this.getCommandArgsCount() == 1 && !string2.equals("-true") && !string2.equals("-false")) {
			string = string2;
			if (this.connection.accessLevel == 2 && !string2.equals(this.getExecutorUsername())) {
				return "An Observer can only toggle invisible on himself";
			}
		}

		boolean boolean1 = false;
		boolean boolean2 = true;
		if ("-false".equals(string3)) {
			boolean2 = false;
			boolean1 = true;
		} else if ("-true".equals(string3)) {
			boolean1 = true;
		}

		IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string);
		if (player != null) {
			if (!boolean1) {
				boolean2 = !player.isInvisible();
			}

			string = player.getDisplayName();
			if (boolean1) {
				player.setInvisible(boolean2);
			} else {
				player.setInvisible(!player.isInvisible());
				boolean2 = player.isInvisible();
			}

			player.setGhostMode(boolean2);
			GameServer.sendPlayerExtraInfo(player, this.connection);
			ZLogger zLogger;
			String string4;
			if (boolean2) {
				zLogger = LoggerManager.getLogger("admin");
				string4 = this.getExecutorUsername();
				zLogger.write(string4 + " enabled invisibility on " + string);
				return "User " + string + " is now invisible.";
			} else {
				zLogger = LoggerManager.getLogger("admin");
				string4 = this.getExecutorUsername();
				zLogger.write(string4 + " disabled invisibility on " + string);
				return "User " + string + " is no more invisible.";
			}
		} else {
			return "User " + string + " not found.";
		}
	}
}
