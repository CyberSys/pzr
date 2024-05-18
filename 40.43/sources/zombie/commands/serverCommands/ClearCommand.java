package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;


@CommandName(name = "clear")
@RequiredRight(requiredRights = 32)
public class ClearCommand extends CommandBase {

	public ClearCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = "Console cleared";
		if (this.connection == null) {
			for (int int1 = 0; int1 < 100; ++int1) {
				System.out.println();
			}
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			for (int int2 = 0; int2 < 50; ++int2) {
				stringBuilder.append("<LINE>");
			}

			string = stringBuilder.toString() + string;
		}

		return string;
	}
}
