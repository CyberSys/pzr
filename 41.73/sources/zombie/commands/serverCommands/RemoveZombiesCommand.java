package zombie.commands.serverCommands;

import zombie.characters.IsoZombie;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;
import zombie.popman.NetworkZombiePacker;
import zombie.util.StringUtils;
import zombie.util.Type;


@CommandName(name = "removezombies")
@CommandArgs(varArgs = true)
@CommandHelp(helpText = "UI_ServerOptionDesc_RemoveZombies")
@RequiredRight(requiredRights = 56)
public class RemoveZombiesCommand extends CommandBase {

	public RemoveZombiesCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		int int1 = -1;
		int int2 = -1;
		int int3 = -1;
		int int4 = -1;
		boolean boolean1 = false;
		boolean boolean2 = false;
		boolean boolean3 = false;
		int int5;
		for (int5 = 0; int5 < this.getCommandArgsCount() - 1; int5 += 2) {
			String string = this.getCommandArg(int5);
			String string2 = this.getCommandArg(int5 + 1);
			byte byte1 = -1;
			switch (string.hashCode()) {
			case 1515: 
				if (string.equals("-x")) {
					byte1 = 2;
				}

				break;
			
			case 1516: 
				if (string.equals("-y")) {
					byte1 = 3;
				}

				break;
			
			case 1517: 
				if (string.equals("-z")) {
					byte1 = 4;
				}

				break;
			
			case 344381183: 
				if (string.equals("-radius")) {
					byte1 = 0;
				}

				break;
			
			case 348349169: 
				if (string.equals("-remove")) {
					byte1 = 5;
				}

				break;
			
			case 1149354179: 
				if (string.equals("-reanimated")) {
					byte1 = 1;
				}

				break;
			
			case 1383057984: 
				if (string.equals("-clear")) {
					byte1 = 6;
				}

			
			}

			switch (byte1) {
			case 0: 
				int1 = PZMath.tryParseInt(string2, -1);
				break;
			
			case 1: 
				boolean1 = StringUtils.tryParseBoolean(string2);
				break;
			
			case 2: 
				int2 = PZMath.tryParseInt(string2, -1);
				break;
			
			case 3: 
				int3 = PZMath.tryParseInt(string2, -1);
				break;
			
			case 4: 
				int4 = PZMath.tryParseInt(string2, -1);
				break;
			
			case 5: 
				boolean2 = StringUtils.tryParseBoolean(string2);
				break;
			
			case 6: 
				boolean3 = StringUtils.tryParseBoolean(string2);
				break;
			
			default: 
				return this.getHelp();
			
			}
		}

		if (boolean2) {
			GameServer.removeZombiesConnection = this.connection;
			return "Zombies removed.";
		} else if (int4 >= 0 && int4 < 8) {
			for (int5 = int3 - int1; int5 <= int3 + int1; ++int5) {
				for (int int6 = int2 - int1; int6 <= int2 + int1; ++int6) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int6, int5, int4);
					if (square != null) {
						int int7;
						if (boolean3) {
							if (!square.getStaticMovingObjects().isEmpty()) {
								for (int7 = square.getStaticMovingObjects().size() - 1; int7 >= 0; --int7) {
									IsoDeadBody deadBody = (IsoDeadBody)Type.tryCastTo((IsoMovingObject)square.getStaticMovingObjects().get(int7), IsoDeadBody.class);
									if (deadBody != null) {
										GameServer.sendRemoveCorpseFromMap(deadBody);
										deadBody.removeFromWorld();
										deadBody.removeFromSquare();
									}
								}
							}
						} else if (!square.getMovingObjects().isEmpty()) {
							for (int7 = square.getMovingObjects().size() - 1; int7 >= 0; --int7) {
								IsoZombie zombie = (IsoZombie)Type.tryCastTo((IsoMovingObject)square.getMovingObjects().get(int7), IsoZombie.class);
								if (zombie != null && (boolean1 || !zombie.isReanimatedPlayer())) {
									NetworkZombiePacker.getInstance().deleteZombie(zombie);
									zombie.removeFromWorld();
									zombie.removeFromSquare();
								}
							}
						}
					}
				}
			}

			LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " removed zombies near " + int2 + "," + int3, "IMPORTANT");
			return "Zombies removed.";
		} else {
			return "invalid z";
		}
	}
}
