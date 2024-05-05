package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandNames;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.PacketTypes;


@CommandNames({@CommandName(name = "teleport"), @CommandName(name = "tp")})
@AltCommandArgs({@CommandArgs(required = {"(.+)"}, argName = "just port to user"), @CommandArgs(required = {"(.+)", "(.+)"}, argName = "teleport user1 to user 2")})
@CommandHelp(helpText = "UI_ServerOptionDesc_Teleport")
@RequiredRight(requiredRights = 62)
public class TeleportCommand extends CommandBase {
	public static final String justToUser = "just port to user";
	public static final String portUserToUser = "teleport user1 to user 2";
	private String username1;
	private String username2;

	public TeleportCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.argsName;
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1662648219: 
			if (string.equals("just port to user")) {
				byte1 = 0;
			}

			break;
		
		case -1222311533: 
			if (string.equals("teleport user1 to user 2")) {
				byte1 = 1;
			}

		
		}
		switch (byte1) {
		case 0: 
			this.username1 = this.getCommandArg(0);
			return this.TeleportMeToUser();
		
		case 1: 
			this.username1 = this.getCommandArg(0);
			this.username2 = this.getCommandArg(1);
			return this.TeleportUser1ToUser2();
		
		default: 
			return this.CommandArgumentsNotMatch();
		
		}
	}

	private String TeleportMeToUser() {
		if (this.connection == null) {
			return "Need player to teleport to, ex /teleport user1 user2";
		} else {
			IsoPlayer player = GameServer.getPlayerByUserNameForCommand(this.username1);
			if (player != null) {
				this.username1 = player.getDisplayName();
				ByteBufferWriter byteBufferWriter = this.connection.startPacket();
				PacketTypes.PacketType.Teleport.doPacket(byteBufferWriter);
				byteBufferWriter.putByte((byte)0);
				byteBufferWriter.putFloat(player.getX());
				byteBufferWriter.putFloat(player.getY());
				byteBufferWriter.putFloat(player.getZ());
				PacketTypes.PacketType.Teleport.send(this.connection);
				if (this.connection.players[0] != null && this.connection.players[0].getNetworkCharacterAI() != null) {
					this.connection.players[0].getNetworkCharacterAI().resetSpeedLimiter();
				}

				ZLogger zLogger = LoggerManager.getLogger("admin");
				String string = this.getExecutorUsername();
				zLogger.write(string + " teleport to " + this.username1);
				return "teleported to " + this.username1 + " please wait two seconds to show the map around you.";
			} else {
				return "Can\'t find player " + this.username1;
			}
		}
	}

	private String TeleportUser1ToUser2() {
		if (this.getAccessLevel() == 2 && !this.username1.equals(this.getExecutorUsername())) {
			return "An Observer can only teleport himself";
		} else {
			IsoPlayer player = GameServer.getPlayerByUserNameForCommand(this.username1);
			IsoPlayer player2 = GameServer.getPlayerByUserNameForCommand(this.username2);
			if (player == null) {
				return "Can\'t find player " + this.username1;
			} else if (player2 == null) {
				return "Can\'t find player " + this.username2;
			} else {
				this.username1 = player.getDisplayName();
				this.username2 = player2.getDisplayName();
				UdpConnection udpConnection = GameServer.getConnectionFromPlayer(player);
				if (udpConnection == null) {
					return "No connection for player " + this.username1;
				} else {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.Teleport.doPacket(byteBufferWriter);
					byteBufferWriter.putByte((byte)player.PlayerIndex);
					byteBufferWriter.putFloat(player2.getX());
					byteBufferWriter.putFloat(player2.getY());
					byteBufferWriter.putFloat(player2.getZ());
					PacketTypes.PacketType.Teleport.send(udpConnection);
					if (player.getNetworkCharacterAI() != null) {
						player.getNetworkCharacterAI().resetSpeedLimiter();
					}

					ZLogger zLogger = LoggerManager.getLogger("admin");
					String string = this.getExecutorUsername();
					zLogger.write(string + " teleported " + this.username1 + " to " + this.username2);
					return "teleported " + this.username1 + " to " + this.username2;
				}
			}
		}
	}

	private String CommandArgumentsNotMatch() {
		return this.getHelp();
	}
}
