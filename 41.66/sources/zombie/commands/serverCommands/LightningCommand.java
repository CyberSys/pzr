package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameServer;


@CommandName(name = "lightning")
@CommandArgs(optional = "(.+)")
@CommandHelp(helpText = "UI_ServerOptionDesc_Lightning")
@RequiredRight(requiredRights = 60)
public class LightningCommand extends CommandBase {

	public LightningCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string;
		if (this.getCommandArgsCount() == 0) {
			if (this.connection == null) {
				return "Pass a username";
			}

			string = this.getExecutorUsername();
		} else {
			string = this.getCommandArg(0);
		}

		IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string);
		if (player == null) {
			return "User \"" + string + "\" not found";
		} else {
			int int1 = PZMath.fastfloor(player.getX());
			int int2 = PZMath.fastfloor(player.getY());
			ClimateManager.getInstance().transmitServerTriggerLightning(int1, int2, false, true, true);
			LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " thunder start");
			return "Lightning triggered";
		}
	}
}
