package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;


@CommandName(name = "additem")
@AltCommandArgs({@CommandArgs(required = {"(.+)", "([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)"}, optional = "(\\d+)", argName = "add item to player"), @CommandArgs(required = {"([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)"}, optional = "(\\d+)", argName = "add item to me")})
@CommandHelp(helpText = "UI_ServerOptionDesc_AddItem")
@RequiredRight(requiredRights = 60)
public class AddItemCommand extends CommandBase {
	public static final String toMe = "add item to me";
	public static final String toPlayer = "add item to player";

	public AddItemCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		int int1 = 1;
		if (this.argsName.equals("add item to me") && this.connection == null) {
			return "Pass username";
		} else {
			if (this.getCommandArgsCount() > 1) {
				int int2 = this.getCommandArgsCount();
				if (this.argsName.equals("add item to me") && int2 == 2 || this.argsName.equals("add item to player") && int2 == 3) {
					int1 = Integer.parseInt(this.getCommandArg(this.getCommandArgsCount() - 1));
				}
			}

			IsoPlayer player;
			String string;
			if (this.argsName.equals("add item to player")) {
				player = GameServer.getPlayerByUserNameForCommand(this.getCommandArg(0));
				if (player == null) {
					return "No such user";
				}

				string = player.getDisplayName();
			} else {
				player = GameServer.getPlayerByRealUserName(this.getExecutorUsername());
				if (player == null) {
					return "No such user";
				}

				string = player.getDisplayName();
			}

			String string2;
			if (this.argsName.equals("add item to me")) {
				string2 = this.getCommandArg(0);
			} else {
				string2 = this.getCommandArg(1);
			}

			Item item = ScriptManager.instance.FindItem(string2);
			if (item == null) {
				return "Item " + string2 + " doesn\'t exist.";
			} else {
				IsoPlayer player2 = GameServer.getPlayerByUserNameForCommand(string);
				if (player2 != null) {
					string = player2.getDisplayName();
					UdpConnection udpConnection = GameServer.getConnectionByPlayerOnlineID(player2.OnlineID);
					if (udpConnection != null) {
						ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
						PacketTypes.PacketType.AddItemInInventory.doPacket(byteBufferWriter);
						byteBufferWriter.putShort(player2.OnlineID);
						byteBufferWriter.putUTF(string2);
						byteBufferWriter.putInt(int1);
						PacketTypes.PacketType.AddItemInInventory.send(udpConnection);
						LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " added item " + string2 + " in " + string + "\'s inventory");
						ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
						PacketTypes.PacketType.RequestInventory.doPacket(byteBufferWriter2);
						byteBufferWriter2.putShort(player2.OnlineID);
						PacketTypes.PacketType.RequestInventory.send(udpConnection);
						return "Item " + string2 + " Added in " + string + "\'s inventory.";
					}
				}

				return "User " + string + " not found.";
			}
		}
	}
}
