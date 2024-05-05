package zombie.commands.serverCommands;

import zombie.VirtualZombieManager;
import zombie.ZombieSpawnRecorder;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.Rand;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;


@CommandName(name = "createhorde")
@CommandArgs(required = {"(\\d+)"}, optional = "(.+)")
@CommandHelp(helpText = "UI_ServerOptionDesc_CreateHorde")
@RequiredRight(requiredRights = 56)
public class CreateHordeCommand extends CommandBase {

	public CreateHordeCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		Integer integer = Integer.parseInt(this.getCommandArg(0));
		String string = this.getCommandArg(1);
		IsoPlayer player = null;
		if (this.getCommandArgsCount() == 2) {
			player = GameServer.getPlayerByUserNameForCommand(string);
			if (player == null) {
				return "User \"" + string + "\" not found";
			}
		} else if (this.connection != null) {
			player = GameServer.getAnyPlayerFromConnection(this.connection);
		}

		if (integer == null) {
			return this.getHelp();
		} else {
			integer = Math.min(integer, 500);
			if (player != null) {
				for (int int1 = 0; int1 < integer; ++int1) {
					VirtualZombieManager.instance.choices.clear();
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)Rand.Next(player.getX() - 10.0F, player.getX() + 10.0F), (double)Rand.Next(player.getY() - 10.0F, player.getY() + 10.0F), (double)player.getZ());
					VirtualZombieManager.instance.choices.add(square);
					IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(IsoDirections.Max.index())).index(), false);
					if (zombie != null) {
						ZombieSpawnRecorder.instance.record(zombie, this.getClass().getSimpleName());
					}
				}

				ZLogger zLogger = LoggerManager.getLogger("admin");
				String string2 = this.getExecutorUsername();
				zLogger.write(string2 + " created a horde of " + integer + " zombies near " + player.getX() + "," + player.getY(), "IMPORTANT");
				return "Horde spawned.";
			} else {
				return "Specify a player to create the horde near to.";
			}
		}
	}
}
