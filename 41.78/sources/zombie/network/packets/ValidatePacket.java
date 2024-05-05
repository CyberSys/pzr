package zombie.network.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.zip.CRC32;
import zombie.GameWindow;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LogSeverity;
import zombie.gameStates.GameLoadingState;
import zombie.network.ConnectionManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketValidator;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;


public class ValidatePacket implements INetworkPacket {
	private long checksum;
	private long checksumFromClient;
	private int salt;
	private byte flags;

	public void setSalt(int int1, boolean boolean1, boolean boolean2, boolean boolean3) {
		this.salt = int1;
		this.flags = 0;
		this.flags = (byte)(this.flags | (boolean1 ? 1 : 0));
		this.flags = (byte)(this.flags | (boolean2 ? 2 : 0));
		this.flags = (byte)(this.flags | (boolean3 ? 4 : 0));
	}

	public void process(UdpConnection udpConnection) {
		if (GameClient.bClient) {
			this.checksum = this.calculateChecksum(udpConnection, this.salt);
			GameClient.sendValidatePacket(this);
			if (DebugOptions.instance.MultiplayerFailChecksum.getValue() && (this.flags & 1) != 0) {
				ArrayList arrayList = ScriptManager.instance.getAllRecipes();
				Recipe recipe = (Recipe)arrayList.get(Rand.Next(arrayList.size()));
				recipe.TimeToMake = (float)Rand.Next(32767);
				DebugLog.Multiplayer.debugln("Failed recipe \"%s\"", recipe.getOriginalname());
			}

			if ((this.flags & 2) != 0) {
				GameLoadingState.Done();
			}
		} else if (GameServer.bServer) {
			this.salt = udpConnection.validator.getSalt();
			this.checksum = this.calculateChecksum(udpConnection, this.salt);
			if ((this.flags & 4) == 0) {
				if (this.checksumFromClient != this.checksum) {
					udpConnection.validator.failChecksum();
				}

				if (udpConnection.validator.isFailed()) {
					udpConnection.validator.sendChecksum(false, false, true);
				} else {
					udpConnection.validator.successChecksum();
					if ((this.flags & 1) != 0) {
						udpConnection.validator.sendChecksum(false, true, false);
					}
				}
			}
		}
	}

	private long calculateChecksum(UdpConnection udpConnection, int int1) {
		if ((this.flags & 4) != 0) {
			udpConnection.validator.details.clear();
		}

		CRC32 cRC32 = new CRC32();
		CRC32 cRC322 = new CRC32();
		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		cRC32.update(int1);
		ArrayList arrayList = ScriptManager.instance.getAllRecipes();
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			Recipe recipe = (Recipe)iterator.next();
			cRC322.reset();
			byteBuffer.clear();
			cRC322.update(recipe.getOriginalname().getBytes());
			cRC322.update((int)recipe.TimeToMake);
			Iterator iterator2;
			if (recipe.skillRequired != null) {
				iterator2 = recipe.skillRequired.iterator();
				while (iterator2.hasNext()) {
					Recipe.RequiredSkill requiredSkill = (Recipe.RequiredSkill)iterator2.next();
					cRC322.update(requiredSkill.getPerk().index());
					cRC322.update(requiredSkill.getLevel());
				}
			}

			iterator2 = recipe.getSource().iterator();
			while (iterator2.hasNext()) {
				Recipe.Source source = (Recipe.Source)iterator2.next();
				Iterator iterator3 = source.getItems().iterator();
				while (iterator3.hasNext()) {
					String string = (String)iterator3.next();
					cRC322.update(string.getBytes());
				}
			}

			cRC322.update(recipe.getResult().getType().getBytes());
			cRC322.update(recipe.getResult().getModule().getBytes());
			cRC322.update(recipe.getResult().getCount());
			long long1 = cRC322.getValue();
			byteBuffer.putLong(long1);
			byteBuffer.position(0);
			cRC32.update(byteBuffer);
			if ((this.flags & 4) != 0) {
				udpConnection.validator.details.put(recipe.getOriginalname(), new PacketValidator.RecipeDetails(recipe.getOriginalname(), long1, (int)recipe.TimeToMake, recipe.skillRequired, recipe.getSource(), recipe.getResult().getType(), recipe.getResult().getModule(), recipe.getResult().getCount()));
			}
		}

		return cRC32.getValue();
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		try {
			this.flags = byteBuffer.get();
			if (GameClient.bClient) {
				this.salt = byteBuffer.getInt();
			} else if (GameServer.bServer) {
				this.checksumFromClient = byteBuffer.getLong();
				if ((this.flags & 4) != 0) {
					udpConnection.validator.detailsFromClient.clear();
					int int1 = byteBuffer.getInt();
					for (int int2 = 0; int2 < int1; ++int2) {
						udpConnection.validator.detailsFromClient.put(GameWindow.ReadString(byteBuffer), new PacketValidator.RecipeDetails(byteBuffer));
					}
				}
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "Parse error. Probably, \"" + udpConnection.username + "\" client is outdated", LogSeverity.Error);
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putByte(this.flags);
		if (GameServer.bServer) {
			byteBufferWriter.putInt(this.salt);
		} else if (GameClient.bClient) {
			byteBufferWriter.putLong(this.checksum);
			if ((this.flags & 4) != 0) {
				int int1 = GameClient.connection.validator.details.size();
				byteBufferWriter.putInt(int1);
				Iterator iterator = GameClient.connection.validator.details.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry)iterator.next();
					byteBufferWriter.putUTF((String)entry.getKey());
					((PacketValidator.RecipeDetails)entry.getValue()).write(byteBufferWriter);
				}
			}
		}
	}

	public void log(UdpConnection udpConnection, String string) {
		if (this.flags != 0) {
			ConnectionManager.log(string, String.format("checksum-packet-%d", this.flags), udpConnection);
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
