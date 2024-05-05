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
import zombie.core.raknet.VoiceManager;
import zombie.network.GameServer;


@CommandName(name = "voiceban")
@AltCommandArgs({@CommandArgs(required = {"(.+)"}, optional = "(-true|-false)"), @CommandArgs(optional = "(-true|-false)")})
@CommandHelp(helpText = "UI_ServerOptionDesc_VoiceBan")
@RequiredRight(requiredRights = 48)
public class VoiceBanCommand extends CommandBase {

	public VoiceBanCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getExecutorUsername();
		if (this.getCommandArgsCount() == 2 || this.getCommandArgsCount() == 1 && !this.getCommandArg(0).equals("-true") && !this.getCommandArg(0).equals("-false")) {
			string = this.getCommandArg(0);
		}

		boolean boolean1 = true;
		if (this.getCommandArgsCount() > 0) {
			boolean1 = !this.getCommandArg(this.getCommandArgsCount() - 1).equals("-false");
		}

		IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string);
		if (player != null) {
			string = player.getDisplayName();
			VoiceManager.instance.VMServerBan(player.OnlineID, boolean1);
			ZLogger zLogger;
			String string2;
			if (boolean1) {
				zLogger = LoggerManager.getLogger("admin");
				string2 = this.getExecutorUsername();
				zLogger.write(string2 + " ban voice " + string);
				return "User " + string + " voice is banned.";
			} else {
				zLogger = LoggerManager.getLogger("admin");
				string2 = this.getExecutorUsername();
				zLogger.write(string2 + " unban voice " + string);
				return "User " + string + " voice is unbanned.";
			}
		} else {
			return "User " + string + " not found.";
		}
	}
}
