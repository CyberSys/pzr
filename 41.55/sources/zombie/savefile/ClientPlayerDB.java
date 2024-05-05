package zombie.savefile;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.PacketTypes;


public final class ClientPlayerDB {
	private static ClientPlayerDB instance = null;
	private static boolean allow = false;
	private ClientPlayerDB.NetworkCharacterProfile networkProfile = null;
	private UpdateLimit saveToDBPeriod4Network = new UpdateLimit(30000L);
	private static ByteBuffer SliceBuffer4NetworkPlayer = ByteBuffer.allocate(65536);
	private boolean forceSavePlayers;
	public boolean canSavePlayers = false;
	private int serverPlayerIndex = 1;

	public static void setAllow(boolean boolean1) {
		allow = boolean1;
	}

	public static boolean isAllow() {
		return allow;
	}

	public static synchronized ClientPlayerDB getInstance() {
		if (instance == null && allow) {
			instance = new ClientPlayerDB();
		}

		return instance;
	}

	public static boolean isAvailable() {
		return instance != null;
	}

	public void updateMain() {
		this.saveNetworkPlayersToDB();
	}

	public void close() {
		instance = null;
		allow = false;
	}

	private void saveNetworkPlayersToDB() {
		if (this.canSavePlayers && (this.forceSavePlayers || this.saveToDBPeriod4Network.Check())) {
			this.forceSavePlayers = false;
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null) {
					this.clientSendNetworkPlayerInt(player);
				}
			}
		}
	}

	public ArrayList getAllNetworkPlayers() {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 1; int1 < this.networkProfile.playerCount; ++int1) {
			byte[] byteArray = this.getClientLoadNetworkPlayerData(int1 + 1);
			ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
			byteBuffer.rewind();
			byteBuffer.put(byteArray);
			byteBuffer.rewind();
			try {
				IsoPlayer player = new IsoPlayer(IsoWorld.instance.CurrentCell);
				player.serverPlayerIndex = int1 + 1;
				player.load(byteBuffer, this.networkProfile.worldVersion);
				arrayList.add(player);
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}

		return arrayList;
	}

	public void clientLoadNetworkCharacter(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		boolean boolean1 = byteBuffer.get() == 1;
		int int1 = byteBuffer.getInt();
		if (boolean1) {
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			int int2 = byteBuffer.getInt();
			boolean boolean2 = byteBuffer.get() == 1;
			int int3 = byteBuffer.getInt();
			byte[] byteArray = new byte[int3];
			byteBuffer.get(byteArray);
			if (this.networkProfile != null) {
				++this.networkProfile.playerCount;
				switch (this.networkProfile.playerCount) {
				case 2: 
					this.networkProfile.character2 = byteArray;
					this.networkProfile.x[1] = float1;
					this.networkProfile.y[1] = float2;
					this.networkProfile.z[1] = float3;
					this.networkProfile.isDead[1] = boolean2;
					break;
				
				case 3: 
					this.networkProfile.character3 = byteArray;
					this.networkProfile.x[2] = float1;
					this.networkProfile.y[2] = float2;
					this.networkProfile.z[2] = float3;
					this.networkProfile.isDead[2] = boolean2;
					break;
				
				case 4: 
					this.networkProfile.character4 = byteArray;
					this.networkProfile.x[3] = float1;
					this.networkProfile.y[3] = float2;
					this.networkProfile.z[3] = float3;
					this.networkProfile.isDead[3] = boolean2;
				
				}
			} else {
				this.networkProfile = new ClientPlayerDB.NetworkCharacterProfile();
				this.networkProfile.playerCount = 1;
				this.networkProfile.username = GameClient.username;
				this.networkProfile.server = GameClient.ip;
				this.networkProfile.character1 = byteArray;
				this.networkProfile.worldVersion = int2;
				this.networkProfile.x[0] = float1;
				this.networkProfile.y[0] = float2;
				this.networkProfile.z[0] = float3;
				this.networkProfile.isDead[0] = boolean2;
			}

			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)167, byteBufferWriter);
			byteBufferWriter.putByte((byte)(int1 + 1));
			GameClient.connection.endPacketImmediate();
		} else if (this.networkProfile != null) {
			this.networkProfile.isLoaded = true;
			this.serverPlayerIndex = this.networkProfile.playerCount;
		} else {
			this.networkProfile = new ClientPlayerDB.NetworkCharacterProfile();
			this.networkProfile.isLoaded = true;
			this.networkProfile.playerCount = 0;
			this.networkProfile.username = GameClient.username;
			this.networkProfile.server = GameClient.ip;
			this.networkProfile.character1 = null;
			this.networkProfile.worldVersion = IsoWorld.getWorldVersion();
		}
	}

	private boolean isClientLoadNetworkCharacterCompleted() {
		return this.networkProfile != null && this.networkProfile.isLoaded;
	}

	public void clientSendNetworkPlayerInt(IsoPlayer player) {
		if (GameClient.connection != null) {
			try {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)166, byteBufferWriter);
				byteBufferWriter.putByte((byte)(player.serverPlayerIndex - 1));
				byteBufferWriter.putInt(player.OnlineID);
				byteBufferWriter.putFloat(player.x);
				byteBufferWriter.putFloat(player.y);
				byteBufferWriter.putFloat(player.z);
				byteBufferWriter.putByte((byte)(player.isDead() ? 1 : 0));
				SliceBuffer4NetworkPlayer.rewind();
				player.save(SliceBuffer4NetworkPlayer);
				byte[] byteArray = new byte[SliceBuffer4NetworkPlayer.position()];
				SliceBuffer4NetworkPlayer.rewind();
				SliceBuffer4NetworkPlayer.get(byteArray);
				byteBufferWriter.putInt(IsoWorld.getWorldVersion());
				byteBufferWriter.putInt(SliceBuffer4NetworkPlayer.position());
				byteBufferWriter.bb.put(byteArray);
				GameClient.connection.endPacketImmediate();
			} catch (IOException ioException) {
				ExceptionLogger.logException(ioException);
			} catch (BufferOverflowException bufferOverflowException) {
				GameClient.connection.cancelPacket();
				int int1 = SliceBuffer4NetworkPlayer.capacity();
				if (int1 > 2097152) {
					DebugLog.log("FATAL ERROR: The player " + player.getUsername() + " cannot be saved");
					ExceptionLogger.logException(bufferOverflowException);
					return;
				}

				SliceBuffer4NetworkPlayer = ByteBuffer.allocate(int1 * 2);
				this.clientSendNetworkPlayerInt(player);
			}
		}
	}

	public boolean isAliveMainNetworkPlayer() {
		return !this.networkProfile.isDead[0];
	}

	public boolean clientLoadNetworkPlayer() {
		if (this.networkProfile != null && this.networkProfile.isLoaded && this.networkProfile.username.equals(GameClient.username) && this.networkProfile.server.equals(GameClient.ip)) {
			return this.networkProfile.playerCount > 0;
		} else if (GameClient.connection == null) {
			return false;
		} else {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)167, byteBufferWriter);
			byteBufferWriter.putByte((byte)0);
			GameClient.connection.endPacketImmediate();
			int int1 = 200;
			while (int1-- > 0) {
				if (this.isClientLoadNetworkCharacterCompleted()) {
					return this.networkProfile.playerCount > 0;
				}

				try {
					Thread.sleep(50L);
				} catch (InterruptedException interruptedException) {
					ExceptionLogger.logException(interruptedException);
				}
			}

			return false;
		}
	}

	public byte[] getClientLoadNetworkPlayerData(int int1) {
		if (this.networkProfile != null && this.networkProfile.isLoaded && this.networkProfile.username.equals(GameClient.username) && this.networkProfile.server.equals(GameClient.ip)) {
			switch (int1) {
			case 1: 
				return this.networkProfile.character1;
			
			case 2: 
				return this.networkProfile.character2;
			
			case 3: 
				return this.networkProfile.character3;
			
			case 4: 
				return this.networkProfile.character4;
			
			default: 
				return null;
			
			}
		} else if (!this.clientLoadNetworkPlayer()) {
			return null;
		} else {
			switch (int1) {
			case 1: 
				return this.networkProfile.character1;
			
			case 2: 
				return this.networkProfile.character2;
			
			case 3: 
				return this.networkProfile.character3;
			
			case 4: 
				return this.networkProfile.character4;
			
			default: 
				return null;
			
			}
		}
	}

	public boolean loadNetworkPlayer(int int1) {
		try {
			byte[] byteArray = this.getClientLoadNetworkPlayerData(int1);
			if (byteArray != null) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
				byteBuffer.rewind();
				byteBuffer.put(byteArray);
				byteBuffer.rewind();
				if (int1 == 1) {
					if (IsoPlayer.getInstance() == null) {
						IsoPlayer.setInstance(new IsoPlayer(IsoCell.getInstance()));
						IsoPlayer.players[0] = IsoPlayer.getInstance();
					}

					IsoPlayer.getInstance().serverPlayerIndex = 1;
					IsoPlayer.getInstance().load(byteBuffer, this.networkProfile.worldVersion);
				} else {
					IsoPlayer.players[int1 - 1] = new IsoPlayer(IsoCell.getInstance());
					IsoPlayer.getInstance().serverPlayerIndex = int1;
					IsoPlayer.getInstance().load(byteBuffer, this.networkProfile.worldVersion);
				}

				return true;
			}
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

		return false;
	}

	public boolean loadNetworkPlayerInfo(int int1) {
		if (this.networkProfile != null && this.networkProfile.isLoaded && this.networkProfile.username.equals(GameClient.username) && this.networkProfile.server.equals(GameClient.ip) && int1 >= 1 && int1 <= 4 && int1 <= this.networkProfile.playerCount) {
			int int2 = (int)(this.networkProfile.x[int1 - 1] / 10.0F) + IsoWorld.saveoffsetx * 30;
			int int3 = (int)(this.networkProfile.y[int1 - 1] / 10.0F) + IsoWorld.saveoffsety * 30;
			IsoChunkMap.WorldXA = (int)this.networkProfile.x[int1 - 1];
			IsoChunkMap.WorldYA = (int)this.networkProfile.y[int1 - 1];
			IsoChunkMap.WorldZA = (int)this.networkProfile.z[int1 - 1];
			IsoChunkMap.WorldXA += 300 * IsoWorld.saveoffsetx;
			IsoChunkMap.WorldYA += 300 * IsoWorld.saveoffsety;
			IsoChunkMap.SWorldX[0] = int2;
			IsoChunkMap.SWorldY[0] = int3;
			int[] intArray = IsoChunkMap.SWorldX;
			intArray[0] += 30 * IsoWorld.saveoffsetx;
			intArray = IsoChunkMap.SWorldY;
			intArray[0] += 30 * IsoWorld.saveoffsety;
			return true;
		} else {
			return false;
		}
	}

	public int getNextServerPlayerIndex() {
		return ++this.serverPlayerIndex;
	}

	private final class NetworkCharacterProfile {
		boolean isLoaded = false;
		byte[] character1;
		byte[] character2;
		byte[] character3;
		byte[] character4;
		String username;
		String server;
		int playerCount = 0;
		int worldVersion;
		float[] x = new float[4];
		float[] y = new float[4];
		float[] z = new float[4];
		boolean[] isDead = new boolean[4];

		public NetworkCharacterProfile() {
		}
	}
}
