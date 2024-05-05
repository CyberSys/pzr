package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.iso.weather.ClimateManager;


@CommandName(name = "stopweather")
@CommandHelp(helpText = "UI_ServerOptionDesc_StopWeather")
@RequiredRight(requiredRights = 60)
public class StopWeatherCommand extends CommandBase {

	public StopWeatherCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		ClimateManager.getInstance().transmitServerStopRain();
		ClimateManager.getInstance().transmitServerStopWeather();
		LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " stopped weather");
		return "Weather stopped";
	}
}
