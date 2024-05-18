package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.iso.weather.ClimateManager;


@CommandName(name = "startrain")
@CommandHelp(helpText = "UI_ServerOptionDesc_StartRain")
@RequiredRight(requiredRights = 60)
public class StartRainCommand extends CommandBase {

	public StartRainCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		ClimateManager.getInstance().transmitServerStartRain(1.0F);
		LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " started rain");
		return "Rain started";
	}
}
