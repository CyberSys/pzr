package zombie.commands.serverCommands;

import zombie.Lua.LuaManager;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.Rand;
import zombie.core.logger.LoggerManager;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;


@CommandName(name = "createhorde2")
@CommandArgs(varArgs = true)
@CommandHelp(helpText = "UI_ServerOptionDesc_CreateHorde2")
@RequiredRight(requiredRights = 44)
public class CreateHorde2Command extends CommandBase {

	public CreateHorde2Command(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		int int1 = -1;
		int int2 = -1;
		int int3 = -1;
		int int4 = -1;
		int int5 = -1;
		String string = null;
		for (int int6 = 0; int6 < this.getCommandArgsCount() - 1; int6 += 2) {
			String string2 = this.getCommandArg(int6);
			String string3 = this.getCommandArg(int6 + 1);
			byte byte1 = -1;
			switch (string2.hashCode()) {
			case 1515: 
				if (string2.equals("-x")) {
					byte1 = 2;
				}

				break;
			
			case 1516: 
				if (string2.equals("-y")) {
					byte1 = 3;
				}

				break;
			
			case 1517: 
				if (string2.equals("-z")) {
					byte1 = 4;
				}

				break;
			
			case 277437552: 
				if (string2.equals("-outfit")) {
					byte1 = 5;
				}

				break;
			
			case 344381183: 
				if (string2.equals("-radius")) {
					byte1 = 1;
				}

				break;
			
			case 1383163138: 
				if (string2.equals("-count")) {
					byte1 = 0;
				}

			
			}

			switch (byte1) {
			case 0: 
				int1 = PZMath.tryParseInt(string3, -1);
				break;
			
			case 1: 
				int2 = PZMath.tryParseInt(string3, -1);
				break;
			
			case 2: 
				int3 = PZMath.tryParseInt(string3, -1);
				break;
			
			case 3: 
				int4 = PZMath.tryParseInt(string3, -1);
				break;
			
			case 4: 
				int5 = PZMath.tryParseInt(string3, -1);
				break;
			
			case 5: 
				string = StringUtils.discardNullOrWhitespace(string3);
				break;
			
			default: 
				return this.getHelp();
			
			}
		}

		int1 = PZMath.clamp(int1, 1, 500);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, int5);
		if (square == null) {
			return "invalid location";
		} else if (string != null && OutfitManager.instance.FindMaleOutfit(string) == null && OutfitManager.instance.FindFemaleOutfit(string) == null) {
			return "invalid outfit";
		} else {
			for (int int7 = 0; int7 < int1; ++int7) {
				int int8 = int2 <= 0 ? int3 : Rand.Next(int3 - int2, int3 + int2 + 1);
				int int9 = int2 <= 0 ? int4 : Rand.Next(int4 - int2, int4 + int2 + 1);
				LuaManager.GlobalObject.addZombiesInOutfit(int8, int9, int5, 1, string, (Integer)null);
			}

			LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " created a horde of " + int1 + " zombies near " + int3 + "," + int4, "IMPORTANT");
			return "Horde spawned.";
		}
	}
}
