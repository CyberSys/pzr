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
@RequiredRight(requiredRights = 56)
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
		boolean boolean1 = false;
		boolean boolean2 = false;
		boolean boolean3 = false;
		boolean boolean4 = false;
		float float1 = 1.0F;
		String string = null;
		for (int int6 = 0; int6 < this.getCommandArgsCount() - 1; int6 += 2) {
			String string2 = this.getCommandArg(int6);
			String string3 = this.getCommandArg(int6 + 1);
			byte byte1 = -1;
			switch (string2.hashCode()) {
			case -1248644872: 
				if (string2.equals("-isFallOnFront")) {
					byte1 = 7;
				}

				break;
			
			case -782900888: 
				if (string2.equals("-knockedDown")) {
					byte1 = 9;
				}

				break;
			
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
			
			case 61697225: 
				if (string2.equals("-health")) {
					byte1 = 10;
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
			
			case 673556624: 
				if (string2.equals("-isFakeDead")) {
					byte1 = 8;
				}

				break;
			
			case 1383163138: 
				if (string2.equals("-count")) {
					byte1 = 0;
				}

				break;
			
			case 2142561863: 
				if (string2.equals("-crawler")) {
					byte1 = 6;
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
			
			case 6: 
				boolean1 = !"false".equals(string3);
				break;
			
			case 7: 
				boolean2 = !"false".equals(string3);
				break;
			
			case 8: 
				boolean3 = !"false".equals(string3);
				break;
			
			case 9: 
				boolean4 = !"false".equals(string3);
				break;
			
			case 10: 
				float1 = Float.valueOf(string3);
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
			Integer integer = null;
			if (string != null) {
				if (OutfitManager.instance.FindFemaleOutfit(string) == null) {
					integer = Integer.MIN_VALUE;
				} else if (OutfitManager.instance.FindMaleOutfit(string) == null) {
					integer = Integer.MAX_VALUE;
				}
			}

			for (int int7 = 0; int7 < int1; ++int7) {
				int int8 = int2 <= 0 ? int3 : Rand.Next(int3 - int2, int3 + int2 + 1);
				int int9 = int2 <= 0 ? int4 : Rand.Next(int4 - int2, int4 + int2 + 1);
				LuaManager.GlobalObject.addZombiesInOutfit(int8, int9, int5, 1, string, integer, boolean1, boolean2, boolean3, boolean4, float1);
			}

			LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " created a horde of " + int1 + " zombies near " + int3 + "," + int4, "IMPORTANT");
			return "Horde spawned.";
		}
	}
}
