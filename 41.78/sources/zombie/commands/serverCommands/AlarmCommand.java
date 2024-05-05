package zombie.commands.serverCommands;

import zombie.AmbientStreamManager;
import zombie.characters.IsoPlayer;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;


@CommandName(name = "alarm")
@CommandHelp(helpText = "UI_ServerOptionDesc_Alarm")
@RequiredRight(requiredRights = 60)
public class AlarmCommand extends CommandBase {

	public AlarmCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		IsoPlayer player = GameServer.getPlayerByUserName(this.getExecutorUsername());
		if (player != null && player.getSquare() != null && player.getSquare().getBuilding() != null) {
			player.getSquare().getBuilding().getDef().bAlarmed = true;
			AmbientStreamManager.instance.doAlarm(player.getSquare().getRoom().def);
			return "Alarm sounded";
		} else {
			return "Not in a room";
		}
	}
}
