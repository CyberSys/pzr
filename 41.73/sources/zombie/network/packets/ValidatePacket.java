package zombie.network.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.CRC32;
import zombie.characters.IsoPlayer;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.gameStates.GameLoadingState;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.PacketValidator;
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;
import zombie.network.Userlog;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;


public class ValidatePacket implements INetworkPacket {
	private static final Class[] resources = new Class[]{IsoPlayer.class, GameClient.class};
	private int salt;
	private long checksum;
	private long checksumClient;
	private ValidatePacket.ValidateState state;

	public void setSalt(UdpConnection udpConnection) {
		udpConnection.sessionSalt = Rand.Next(Integer.MAX_VALUE);
		this.salt = udpConnection.sessionSalt;
		this.state = ValidatePacket.ValidateState.Request;
	}

	public void process(UdpConnection udpConnection) {
		if (GameClient.bClient) {
			switch (this.state) {
			case Request: 
				this.calculateChecksum();
				GameClient.sendValidatePacket(this);
				break;
			
			case Success: 
				udpConnection.checkState = UdpConnection.CheckState.Success;
				GameLoadingState.Done();
			
			}
		} else if (GameServer.bServer) {
			this.salt = udpConnection.sessionSalt;
			this.calculateChecksum();
			if (this.checksumClient != this.checksum) {
				DebugLog.Multiplayer.trace("Invalid");
			}

			if (this.checksumClient != this.checksum && !isUntouchable(udpConnection) && PacketValidator.doAntiCheatProtection() && ServerOptions.instance.AntiCheatProtectionType21.getValue()) {
				ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.Kicked, "UI_ValidationFailed_Type21", ValidatePacket.class.getSimpleName(), 1);
				GameServer.kick(udpConnection, "UI_Policy_Kick", "UI_ValidationFailed_Type21");
				udpConnection.forceDisconnect(ValidatePacket.class.getSimpleName());
				GameServer.addDisconnect(udpConnection);
				udpConnection.checkState = UdpConnection.CheckState.None;
				udpConnection.checkLimit.Reset(10000L);
				udpConnection.timeSyncLimit.Reset(15000L);
			} else {
				udpConnection.checkState = UdpConnection.CheckState.Success;
				this.state = ValidatePacket.ValidateState.Success;
				if (udpConnection.isFullyConnected()) {
					udpConnection.checkLimit.Reset(Rand.Next(1000L, 10000L));
				} else {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.Validate.doPacket(byteBufferWriter);
					this.write(byteBufferWriter);
					PacketTypes.PacketType.Validate.send(udpConnection);
				}

				DebugLog.Multiplayer.trace("Ok %d", udpConnection.checkLimit.getDelay());
			}
		}
	}

	private void calculateChecksum() {
		DebugLog.Multiplayer.trace("in");
		CRC32 cRC32 = new CRC32();
		cRC32.update(this.salt);
		ArrayList arrayList = ScriptManager.instance.getAllRecipes();
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			Recipe recipe = (Recipe)iterator.next();
			cRC32.update(recipe.getOriginalname().getBytes());
			cRC32.update((int)recipe.TimeToMake);
			Iterator iterator2;
			if (recipe.skillRequired != null) {
				iterator2 = recipe.skillRequired.iterator();
				while (iterator2.hasNext()) {
					Recipe.RequiredSkill requiredSkill = (Recipe.RequiredSkill)iterator2.next();
					cRC32.update(requiredSkill.getPerk().index());
					cRC32.update(requiredSkill.getLevel());
				}
			}

			iterator2 = recipe.getSource().iterator();
			while (iterator2.hasNext()) {
				Recipe.Source source = (Recipe.Source)iterator2.next();
				Iterator iterator3 = source.getItems().iterator();
				while (iterator3.hasNext()) {
					String string = (String)iterator3.next();
					cRC32.update(string.getBytes());
				}
			}

			cRC32.update(recipe.getResult().getType().getBytes());
			cRC32.update(recipe.getResult().getModule().getBytes());
			cRC32.update(recipe.getResult().getCount());
		}

		try {
			Class[] classArray = resources;
			int int1 = classArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				Class javaClass = classArray[int2];
				cRC32.update(javaClass.getResourceAsStream(javaClass.getSimpleName() + ".class").readAllBytes());
			}
		} catch (Exception exception) {
		}

		this.checksum = cRC32.getValue();
		DebugLog.Multiplayer.trace("out %d", this.checksum);
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (GameClient.bClient) {
			this.state = ValidatePacket.ValidateState.values()[byteBuffer.get()];
			this.salt = byteBuffer.getInt();
		} else if (GameServer.bServer) {
			this.checksumClient = byteBuffer.getLong();
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		if (GameServer.bServer) {
			byteBufferWriter.putByte((byte)this.state.ordinal());
			byteBufferWriter.putInt(this.salt);
		} else if (GameClient.bClient) {
			byteBufferWriter.putLong(this.checksum);
		}
	}

	public boolean isConsistent() {
		return true;
	}

	public String getDescription() {
		return null;
	}

	public static boolean isUntouchable(UdpConnection udpConnection) {
		return Core.bDebug || PlayerType.isPrivileged(udpConnection.accessLevel);
	}

	public static void update(UdpConnection udpConnection) {
		if (GameServer.bServer && udpConnection.isFullyConnected()) {
			switch (udpConnection.checkState) {
			case Sent: 
				if (udpConnection.checkLimit.Check()) {
					DebugLog.Multiplayer.trace("Timeout");
					if (ServerOptions.instance.AntiCheatProtectionType22.getValue() && PacketValidator.doKickUser(udpConnection, ValidatePacket.class.getSimpleName(), "UI_ValidationFailed_Type22")) {
						LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\"", udpConnection.username, "UI_ValidationFailed_Type22", ValidatePacket.class.getSimpleName()));
					}

					udpConnection.checkState = UdpConnection.CheckState.None;
					udpConnection.checkLimit.Reset(10000L);
				}

				break;
			
			case None: 
			
			case Success: 
				if (udpConnection.checkLimit.Check()) {
					DebugLog.Multiplayer.trace("Request");
					GameServer.sendValidatePacket(udpConnection);
					udpConnection.checkState = UdpConnection.CheckState.Sent;
					udpConnection.checkLimit.Reset(2000L);
				}

			
			}
		}
	}

	public static enum ValidateState {

		Request,
		Success;

		private static ValidatePacket.ValidateState[] $values() {
			return new ValidatePacket.ValidateState[]{Request, Success};
		}
	}
}
