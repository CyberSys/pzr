package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
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


@CommandName(name = "addxp")
@CommandArgs(required = {"(.+)", "(\\S+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_AddXp")
@RequiredRight(requiredRights = 60)
public class AddXPCommand extends CommandBase {

	public AddXPCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.getCommandArg(0);
		String string2 = this.getCommandArg(1);
		IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string);
		if (player == null) {
			return "No such user";
		} else {
			String string3 = player.getDisplayName();
			String string4 = null;
			boolean boolean1 = false;
			String[] stringArray = string2.split("=", 2);
			if (stringArray.length != 2) {
				return this.getHelp();
			} else {
				string4 = stringArray[0].trim();
				if (PerkFactory.Perks.FromString(string4) == PerkFactory.Perks.MAX) {
					String string5 = this.connection == null ? "\n" : " LINE ";
					StringBuilder stringBuilder = new StringBuilder();
					for (int int1 = 0; int1 < PerkFactory.PerkList.size(); ++int1) {
						if (((PerkFactory.Perk)PerkFactory.PerkList.get(int1)).type != PerkFactory.Perks.Passiv) {
							stringBuilder.append(((PerkFactory.Perk)PerkFactory.PerkList.get(int1)).type);
							if (int1 < PerkFactory.PerkList.size()) {
								stringBuilder.append(string5);
							}
						}
					}

					return "List of available perks :" + string5 + stringBuilder.toString();
				} else {
					int int2;
					try {
						int2 = Integer.parseInt(stringArray[1]);
					} catch (NumberFormatException numberFormatException) {
						return this.getHelp();
					}

					IsoPlayer player2 = GameServer.getPlayerByUserNameForCommand(string3);
					if (player2 != null) {
						string3 = player2.getDisplayName();
						UdpConnection udpConnection = GameServer.getConnectionFromPlayer(player2);
						if (udpConnection != null) {
							ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
							PacketTypes.doPacket((short)107, byteBufferWriter);
							byteBufferWriter.putShort((short)player2.OnlineID);
							byteBufferWriter.putInt(PerkFactory.Perks.FromString(string4).index());
							byteBufferWriter.putInt(int2);
							udpConnection.endPacketImmediate();
							LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " added " + int2 + " " + string4 + " xp\'s to " + string3);
							return "Added " + int2 + " " + string4 + " xp\'s to " + string3;
						}
					}

					return "User " + string3 + " not found.";
				}
			}
		}
	}
}
