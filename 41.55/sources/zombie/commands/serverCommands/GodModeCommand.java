package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandNames;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;


@CommandNames({@CommandName(name = "godmod"), @CommandName(name = "godmode")})
@AltCommandArgs({@CommandArgs(required = {"(.+)"}, optional = "(-true|-false)"), @CommandArgs(optional = "(-true|-false)")})
@CommandHelp(helpText = "UI_ServerOptionDesc_GodMod")
@RequiredRight(requiredRights = 61)
public class GodModeCommand extends CommandBase {

	public GodModeCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getExecutorUsername();
		String string2 = this.getCommandArg(0);
		String string3 = this.getCommandArg(1);
		if (this.getCommandArgsCount() == 2 || this.getCommandArgsCount() == 1 && !string2.equals("-true") && !string2.equals("-false")) {
			string = string2;
			if (this.connection != null && this.connection.accessLevel.equals("observer") && !string2.equals(string2)) {
				return "An Observer can only toggle god mode on himself";
			}
		}

		IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string);
		if (player != null) {
			string = player.getDisplayName();
			if (string3 != null) {
				player.setGodMod("-true".equals(string3));
			} else {
				player.setGodMod(!player.isGodMod());
			}

			GameServer.sendPlayerExtraInfo(player, this.connection);
			ZLogger zLogger;
			String string4;
			if (player.isGodMod()) {
				zLogger = LoggerManager.getLogger("admin");
				string4 = this.getExecutorUsername();
				zLogger.write(string4 + " enabled godmode on " + string);
				return "User " + string + " is now invincible.";
			} else {
				zLogger = LoggerManager.getLogger("admin");
				string4 = this.getExecutorUsername();
				zLogger.write(string4 + " disabled godmode on " + string);
				return "User " + string + " is no more invincible.";
			}
		} else {
			return "User " + string + " not found.";
		}
	}
}
