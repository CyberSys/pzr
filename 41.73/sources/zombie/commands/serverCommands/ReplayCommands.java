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
import zombie.network.ReplayManager;


@CommandName(name = "replay")
@AltCommandArgs({@CommandArgs(required = {"(.+)", "(-record|-play|-stop)", "(.+)"}), @CommandArgs(required = {"(.+)", "(-stop)"})})
@CommandHelp(helpText = "UI_ServerOptionDesc_Replay")
@RequiredRight(requiredRights = 32)
public class ReplayCommands extends CommandBase {
	public static final String RecordPlay = "(-record|-play|-stop)";
	public static final String Stop = "(-stop)";

	public ReplayCommands(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getCommandArg(0);
		String string2 = this.getCommandArg(1);
		String string3 = this.getCommandArg(2);
		boolean boolean1 = false;
		boolean boolean2 = false;
		if ("-play".equals(string2)) {
			boolean2 = true;
		} else if ("-stop".equals(string2)) {
			boolean1 = true;
		}

		IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string);
		if (player != null) {
			if (player.replay == null) {
				player.replay = new ReplayManager(player);
			}

			ZLogger zLogger;
			String string4;
			if (boolean1) {
				ReplayManager.State state = player.replay.getState();
				if (state == ReplayManager.State.Stop) {
					return "Nothing to stop.";
				} else if (state == ReplayManager.State.Recording) {
					player.replay.stopRecordReplay();
					zLogger = LoggerManager.getLogger("admin");
					string4 = this.getExecutorUsername();
					zLogger.write(string4 + " end record replay for " + string);
					return "Recording replay is stopped  for " + string + ".";
				} else {
					player.replay.stopPlayReplay();
					zLogger = LoggerManager.getLogger("admin");
					string4 = this.getExecutorUsername();
					zLogger.write(string4 + " end play replay for " + string);
					return "Playing replay is stopped  for " + string + ".";
				}
			} else if (boolean2) {
				if (!player.replay.startPlayReplay(player, string3, this.connection)) {
					return "Can\'t play replay";
				} else {
					zLogger = LoggerManager.getLogger("admin");
					string4 = this.getExecutorUsername();
					zLogger.write(string4 + " enabled play replay for " + string);
					return "Replay is playing for " + string + " to file \"" + string3 + "\" now.";
				}
			} else if (!player.replay.startRecordReplay(player, string3)) {
				return "Can\'t record replay";
			} else {
				zLogger = LoggerManager.getLogger("admin");
				string4 = this.getExecutorUsername();
				zLogger.write(string4 + " enabled record replay for " + string);
				return "Replay is recording for " + string + " to file \"" + string3 + "\" now.";
			}
		} else {
			return "User " + string + " not found.";
		}
	}
}
