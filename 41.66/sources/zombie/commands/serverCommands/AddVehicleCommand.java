package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.math.PZMath;
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
@AltCommandArgs({@CommandArgs(required = {"([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)"}, argName = "Script Only"), @CommandArgs(required = {"([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)", "(\\d+),(\\d+),(\\d+)"}, argName = "Script And Coordinate"), @CommandArgs(required = {"([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)", "(.+)"}, argName = "Script And Player")})
@CommandHelp(helpText = "UI_ServerOptionDesc_AddVehicle")
@RequiredRight(requiredRights = 60)
public class AddVehicleCommand extends CommandBase {
	public static final String scriptOnly = "Script Only";
	public static final String scriptPlayer = "Script And Player";
	public static final String scriptCoordinate = "Script And Coordinate";

	public AddVehicleCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getCommandArg(0);
		VehicleScript vehicleScript = ScriptManager.instance.getVehicle(string);
		if (vehicleScript == null) {
			return "Unknown vehicle script \"" + string + "\"";
		} else {
			String string2 = vehicleScript.getModule().getName();
			string = string2 + "." + vehicleScript.getName();
			String string3;
			int int1;
			int int2;
			int int3;
			IsoPlayer player;
			if (this.argsName.equals("Script And Player")) {
				string3 = this.getCommandArg(1);
				player = GameServer.getPlayerByUserNameForCommand(string3);
				if (player == null) {
					return "User \"" + string3 + "\" not found";
				}

				int1 = PZMath.fastfloor(player.getX());
				int2 = PZMath.fastfloor(player.getY());
				int3 = PZMath.fastfloor(player.getZ());
			} else if (this.argsName.equals("Script And Coordinate")) {
				int1 = PZMath.fastfloor(Float.parseFloat(this.getCommandArg(1)));
				int2 = PZMath.fastfloor(Float.parseFloat(this.getCommandArg(2)));
				int3 = PZMath.fastfloor(Float.parseFloat(this.getCommandArg(3)));
			} else {
				if (this.connection == null) {
					return "Pass a username or coordinate";
				}

				string3 = this.getExecutorUsername();
				player = GameServer.getPlayerByUserNameForCommand(string3);
				if (player == null) {
					return "User \"" + string3 + "\" not found";
				}

				int1 = PZMath.fastfloor(player.getX());
				int2 = PZMath.fastfloor(player.getY());
				int3 = PZMath.fastfloor(player.getZ());
			}

			if (int3 > 0) {
				return "Z coordinate must be 0 for now";
			} else {
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
						baseVehicle.setX((float)int1 - 1.0F);
						baseVehicle.setY((float)int2 - 0.1F);
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
