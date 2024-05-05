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


@CommandNames({@CommandName(name = "teleportto"), @CommandName(name = "tpto")})
@AltCommandArgs({@CommandArgs(required = {"(.+)", "(\\d+),(\\d+),(\\d+)"}, argName = "Teleport user"), @CommandArgs(required = {"(\\d+),(\\d+),(\\d+)"}, argName = "teleport me")})
@CommandHelp(helpText = "UI_ServerOptionDesc_TeleportTo")
@RequiredRight(requiredRights = 62)
public class TeleportToCommand extends CommandBase {
	public static final String teleportMe = "teleport me";
	public static final String teleportUser = "Teleport user";
	private String username;
	private Float[] coords;

	public TeleportToCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	protected String Command() {
		String string = this.argsName;
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1131489408: 
			if (string.equals("Teleport user")) {
				byte1 = 1;
			}

			break;
		
		case 1240447661: 
			if (string.equals("teleport me")) {
				byte1 = 0;
			}

		
		}
		int int1;
		switch (byte1) {
		case 0: 
			this.coords = new Float[3];
			for (int1 = 0; int1 < 3; ++int1) {
				this.coords[int1] = Float.parseFloat(this.getCommandArg(int1));
			}

			return this.TeleportMeToCoords();
		
		case 1: 
			this.username = this.getCommandArg(0);
			this.coords = new Float[3];
			for (int1 = 0; int1 < 3; ++int1) {
				this.coords[int1] = Float.parseFloat(this.getCommandArg(int1 + 1));
			}

			return this.TeleportUserToCoords();
		
		default: 
			return this.CommandArgumentsNotMatch();
		
		}
	}

	private String TeleportMeToCoords() {
		float float1 = this.coords[0];
		float float2 = this.coords[1];
		float float3 = this.coords[2];
		if (this.connection == null) {
			return "Error";
		} else {
			ByteBufferWriter byteBufferWriter = this.connection.startPacket();
			PacketTypes.PacketType.Teleport.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)0);
			byteBufferWriter.putFloat(float1);
			byteBufferWriter.putFloat(float2);
			byteBufferWriter.putFloat(float3);
			PacketTypes.PacketType.Teleport.send(this.connection);
			if (this.connection.players[0] != null && this.connection.players[0].getNetworkCharacterAI() != null) {
				this.connection.players[0].getNetworkCharacterAI().resetSpeedLimiter();
			}

			ZLogger zLogger = LoggerManager.getLogger("admin");
			String string = this.getExecutorUsername();
			zLogger.write(string + " teleported to " + (int)float1 + "," + (int)float2 + "," + (int)float3);
			return "teleported to " + (int)float1 + "," + (int)float2 + "," + (int)float3 + " please wait two seconds to show the map around you.";
		}
	}

	private String TeleportUserToCoords() {
		float float1 = this.coords[0];
		float float2 = this.coords[1];
		float float3 = this.coords[2];
		if (this.connection != null && this.connection.accessLevel == 2 && !this.username.equals(this.getExecutorUsername())) {
			return "An Observer can only teleport himself";
		} else {
			IsoPlayer player = GameServer.getPlayerByUserNameForCommand(this.username);
			if (player == null) {
				return "Can\'t find player " + this.username;
			} else {
				UdpConnection udpConnection = GameServer.getConnectionFromPlayer(player);
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.Teleport.doPacket(byteBufferWriter);
				byteBufferWriter.putByte((byte)0);
				byteBufferWriter.putFloat(float1);
				byteBufferWriter.putFloat(float2);
				byteBufferWriter.putFloat(float3);
				PacketTypes.PacketType.Teleport.send(udpConnection);
				if (player.getNetworkCharacterAI() != null) {
					player.getNetworkCharacterAI().resetSpeedLimiter();
				}

				ZLogger zLogger = LoggerManager.getLogger("admin");
				String string = this.getExecutorUsername();
				zLogger.write(string + " teleported to " + (int)float1 + "," + (int)float2 + "," + (int)float3);
				return this.username + " teleported to " + (int)float1 + "," + (int)float2 + "," + (int)float3 + " please wait two seconds to show the map around you.";
			}
		}
	}

	private String CommandArgumentsNotMatch() {
		return this.getHelp();
	}
}
