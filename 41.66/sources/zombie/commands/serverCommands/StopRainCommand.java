package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.iso.weather.ClimateManager;


@CommandName(name = "stoprain")
@CommandHelp(helpText = "UI_ServerOptionDesc_StopRain")
@RequiredRight(requiredRights = 60)
public class StopRainCommand extends CommandBase {

	public StopRainCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		ClimateManager.getInstance().transmitServerStopRain();
		LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " stopped rain");
		return "Rain stopped";
	}
}
