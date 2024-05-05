package zombie.commands.serverCommands;

import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.MPStatistic;


@CommandName(name = "stats")
@AltCommandArgs({@CommandArgs(required = {"(none|file|console|all)"}, optional = "(\\d+)"), @CommandArgs(optional = "(\\d+)")})
@CommandHelp(helpText = "UI_ServerOptionDesc_SetStatisticsPeriod")
@RequiredRight(requiredRights = 32)
public class StatisticsCommand extends CommandBase {

	public StatisticsCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		if (this.getCommandArgsCount() != 1 && this.getCommandArgsCount() != 2) {
			return this.getHelp();
		} else {
			try {
				String string = this.getCommandArg(0);
				byte byte1 = -1;
				switch (string.hashCode()) {
				case 96673: 
					if (string.equals("all")) {
						byte1 = 1;
					}

					break;
				
				case 3143036: 
					if (string.equals("file")) {
						byte1 = 2;
					}

					break;
				
				case 3387192: 
					if (string.equals("none")) {
						byte1 = 0;
					}

					break;
				
				case 951510359: 
					if (string.equals("console")) {
						byte1 = 3;
					}

				
				}

				boolean boolean1;
				boolean boolean2;
				switch (byte1) {
				case 0: 
					boolean1 = false;
					boolean2 = false;
					break;
				
				case 1: 
					boolean1 = true;
					boolean2 = true;
					break;
				
				case 2: 
					boolean1 = true;
					boolean2 = false;
					break;
				
				case 3: 
					boolean1 = false;
					boolean2 = true;
					break;
				
				default: 
					return this.getHelp();
				
				}

				int int1 = this.getCommandArgsCount() == 2 ? Integer.parseInt(this.getCommandArg(1)) : 10;
				if (int1 < 1) {
					return this.getHelp();
				} else {
					if (!boolean1 && !boolean2) {
						int1 = 0;
					}

					MPStatistic.getInstance().writeEnabled(boolean1);
					MPStatistic.getInstance().printEnabled(boolean2);
					MPStatistic.getInstance().setPeriod(int1);
					return "Server statistics has been cleared and file is set to " + boolean1 + " and console is set to " + boolean2 + " and period is set to " + int1;
				}
			} catch (Exception exception) {
				return this.getHelp();
			}
		}
	}
}
