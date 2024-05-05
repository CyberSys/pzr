package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclesDB2;


@CommandName(name = "addvehicle")
@CommandArgs(required = {"([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9.-]*)"}, optional = "(.+)")
@CommandHelp(helpText = "UI_ServerOptionDesc_AddVehicle")
@RequiredRight(requiredRights = 60)
public class AddVehicleCommand extends CommandBase {

	public AddVehicleCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getCommandArg(0);
		String string2;
		if (this.getCommandArgsCount() == 2) {
			string2 = this.getCommandArg(1);
		} else {
			if (this.connection == null) {
				return "Pass a username";
			}

			string2 = this.getExecutorUsername();
		}

		IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string2);
		if (player == null) {
			return "User \"" + string2 + "\" not found";
		} else {
			int int1 = (int)player.getX();
			int int2 = (int)player.getY();
			int int3 = (int)player.getZ();
			if (int3 > 0) {
				return "Z coordinate must be 0 for now";
			} else {
				VehicleScript vehicleScript = ScriptManager.instance.getVehicle(string);
				if (vehicleScript == null) {
					return "Unknown vehicle script \"" + string + "\"";
				} else {
					String string3 = vehicleScript.getModule().getName();
					string = string3 + "." + vehicleScript.getName();
					WorldSimulation.instance.create();
					if (!WorldSimulation.instance.created) {
						return "Physics couldn\'t be created";
					} else {
						IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
						if (square == null) {
							return "Invalid location " + int1 + "," + int2 + "," + int3;
						} else {
							BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
							baseVehicle.setScriptName(string);
							baseVehicle.setX((float)int1 + 0.5F);
							baseVehicle.setY((float)int2 + 0.5F);
							baseVehicle.setZ((float)int3 + 0.2F);
							if (IsoChunk.doSpawnedVehiclesInInvalidPosition(baseVehicle)) {
								baseVehicle.setSquare(square);
								baseVehicle.square.chunk.vehicles.add(baseVehicle);
								baseVehicle.chunk = baseVehicle.square.chunk;
								baseVehicle.addToWorld();
								VehiclesDB2.instance.addVehicle(baseVehicle);
								return "Vehicle spawned";
							} else {
								return "ERROR: I can not spawn the vehicle. Invalid position. Try to change position.";
							}
						}
					}
				}
			}
		}
	}
}
