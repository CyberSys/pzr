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


@CommandName(name = "noclip")
@AltCommandArgs({@CommandArgs(required = {"(.+)"}, optional = "(-true|-false)"), @CommandArgs(optional = "(-true|-false)")})
@CommandHelp(helpText = "UI_ServerOptionDesc_NoClip")
@RequiredRight(requiredRights = 62)
public class NoClipCommand extends CommandBase {

	public NoClipCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getExecutorUsername();
		String string2 = this.getCommandArg(0);
		String string3 = this.getCommandArg(1);
		if (this.getCommandArgsCount() == 2 || this.getCommandArgsCount() == 1 && !string2.equals("-true") && !string2.equals("-false")) {
			string = string2;
			if (this.connection.accessLevel == 2 && !string2.equals(this.getExecutorUsername())) {
				return "An Observer can only toggle noclip on himself";
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
			string = player.getDisplayName();
			if (boolean1) {
				player.setNoClip(boolean2);
			} else {
				player.setNoClip(!player.isNoClip());
				boolean2 = player.isNoClip();
			}

			GameServer.sendPlayerExtraInfo(player, this.connection);
			ZLogger zLogger;
			String string4;
			if (boolean2) {
				zLogger = LoggerManager.getLogger("admin");
				string4 = this.getExecutorUsername();
				zLogger.write(string4 + " enabled noclip on " + string);
				return "User " + string + " won\'t collide.";
			} else {
				zLogger = LoggerManager.getLogger("admin");
				string4 = this.getExecutorUsername();
				zLogger.write(string4 + " disabled noclip on " + string);
				return "User " + string + " will collide.";
			}
		} else {
			return "User " + string + " not found.";
		}
	}
}
