package zombie.commands.serverCommands;

import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.iso.weather.ClimateManager;


@CommandName(name = "startstorm")
@CommandArgs(optional = "(\\d+)")
@CommandHelp(helpText = "UI_ServerOptionDesc_StartStorm")
@RequiredRight(requiredRights = 60)
public class StartStormCommand extends CommandBase {

	public StartStormCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		float float1 = 24.0F;
		if (this.getCommandArgsCount() == 1) {
			try {
				float1 = Float.parseFloat(this.getCommandArg(0));
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
				return "Invalid duration value";
			}
		}

		ClimateManager.getInstance().transmitServerStopWeather();
		ClimateManager.getInstance().transmitServerTriggerStorm(float1);
		LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " started thunderstorm");
		return "Thunderstorm started";
	}
}
