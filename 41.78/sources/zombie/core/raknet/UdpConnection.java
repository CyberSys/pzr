package zombie.core.raknet;

import gnu.trove.list.array.TShortArrayList;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import zombie.SystemDisabler;
import zombie.characters.IsoPlayer;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.utils.UpdateTimer;
import zombie.core.znet.ZNetStatistics;
import zombie.iso.IsoUtils;
import zombie.iso.Vector3;
import zombie.network.ClientServerMap;
import zombie.network.ConnectionManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.network.PacketValidator;
import zombie.network.PlayerDownloadServer;


public class UdpConnection {
	Lock bufferLock = new ReentrantLock();
	private ByteBuffer bb = ByteBuffer.allocate(1000000);
	private ByteBufferWriter bbw;
	Lock bufferLockPing;
	private ByteBuffer bbPing;
	private ByteBufferWriter bbwPing;
	long connectedGUID;
	UdpEngine engine;
	public int index;
	public boolean allChatMuted;
	public String username;
	public String[] usernames;
	public byte ReleventRange;
	public byte accessLevel;
	public long lastUnauthorizedPacket;
	public String ip;
	public boolean preferredInQueue;
	public boolean wasInLoadingQueue;
	public String password;
	public boolean ping;
	public Vector3[] ReleventPos;
	public short[] playerIDs;
	public IsoPlayer[] players;
	public Vector3[] connectArea;
	public int ChunkGridWidth;
	public ClientServerMap[] loadedCells;
	public PlayerDownloadServer playerDownloadServer;
	public UdpConnection.ChecksumState checksumState;
	public long checksumTime;
	public boolean awaitingCoopApprove;
	public long steamID;
	public long ownerID;
	public String idStr;
	public boolean isCoopHost;
	public int maxPlayers;
	public final TShortArrayList chunkObjectState;
	public UdpConnection.MPClientStatistic statistic;
	public ZNetStatistics netStatistics;
	public final Deque pingHistory;
	public final PacketValidator validator;
	private static final long CONNECTION_ATTEMPT_TIMEOUT = 5000L;
	public static final long CONNECTION_GRACE_INTERVAL = 60000L;
	public long connectionTimestamp;
	public UpdateTimer timerSendZombie;
	private boolean bFullyConnected;
	public boolean isNeighborPlayer;

	public UdpConnection(UdpEngine udpEngine, long long1, int int1) {
		this.bbw = new ByteBufferWriter(this.bb);
		this.bufferLockPing = new ReentrantLock();
		this.bbPing = ByteBuffer.allocate(50);
		this.bbwPing = new ByteBufferWriter(this.bbPing);
		this.connectedGUID = 0L;
		this.allChatMuted = false;
		this.usernames = new String[4];
		this.accessLevel = 1;
		this.lastUnauthorizedPacket = 0L;
		this.ping = false;
		this.ReleventPos = new Vector3[4];
		this.playerIDs = new short[4];
		this.players = new IsoPlayer[4];
		this.connectArea = new Vector3[4];
		this.loadedCells = new ClientServerMap[4];
		this.checksumState = UdpConnection.ChecksumState.Init;
		this.awaitingCoopApprove = false;
		this.chunkObjectState = new TShortArrayList();
		this.statistic = new UdpConnection.MPClientStatistic();
		this.pingHistory = new ArrayDeque();
		this.validator = new PacketValidator(this);
		this.timerSendZombie = new UpdateTimer();
		this.bFullyConnected = false;
		this.isNeighborPlayer = false;
		this.engine = udpEngine;
		this.connectedGUID = long1;
		this.index = int1;
		this.ReleventPos[0] = new Vector3();
		for (int int2 = 0; int2 < 4; ++int2) {
			this.playerIDs[int2] = -1;
		}

		this.connectionTimestamp = System.currentTimeMillis();
		this.wasInLoadingQueue = false;
	}

	public RakNetPeerInterface getPeer() {
		return this.engine.peer;
	}

	public long getConnectedGUID() {
		return this.connectedGUID;
	}

	public String getServerIP() {
		return this.engine.getServerIP();
	}

	public ByteBufferWriter startPacket() {
		this.bufferLock.lock();
		this.bb.clear();
		return this.bbw;
	}

	public ByteBufferWriter startPingPacket() {
		this.bufferLockPing.lock();
		this.bbPing.clear();
		return this.bbwPing;
	}

	public boolean RelevantTo(float float1, float float2) {
		for (int int1 = 0; int1 < 4; ++int1) {
			if (this.connectArea[int1] != null) {
				int int2 = (int)this.connectArea[int1].z;
				int int3 = (int)(this.connectArea[int1].x - (float)(int2 / 2)) * 10;
				int int4 = (int)(this.connectArea[int1].y - (float)(int2 / 2)) * 10;
				int int5 = int3 + int2 * 10;
				int int6 = int4 + int2 * 10;
				if (float1 >= (float)int3 && float1 < (float)int5 && float2 >= (float)int4 && float2 < (float)int6) {
					return true;
				}
			}

			if (this.ReleventPos[int1] != null && Math.abs(this.ReleventPos[int1].x - float1) <= (float)(this.ReleventRange * 10) && Math.abs(this.ReleventPos[int1].y - float2) <= (float)(this.ReleventRange * 10)) {
				return true;
			}
		}

		return false;
	}

	public float getRelevantAndDistance(float float1, float float2, float float3) {
		for (int int1 = 0; int1 < 4; ++int1) {
			if (this.ReleventPos[int1] != null && Math.abs(this.ReleventPos[int1].x - float1) <= (float)(this.ReleventRange * 10) && Math.abs(this.ReleventPos[int1].y - float2) <= (float)(this.ReleventRange * 10)) {
				return IsoUtils.DistanceTo(this.ReleventPos[int1].x, this.ReleventPos[int1].y, float1, float2);
			}
		}

		return Float.POSITIVE_INFINITY;
	}

	public boolean RelevantToPlayerIndex(int int1, float float1, float float2) {
		if (this.connectArea[int1] != null) {
			int int2 = (int)this.connectArea[int1].z;
			int int3 = (int)(this.connectArea[int1].x - (float)(int2 / 2)) * 10;
			int int4 = (int)(this.connectArea[int1].y - (float)(int2 / 2)) * 10;
			int int5 = int3 + int2 * 10;
			int int6 = int4 + int2 * 10;
			if (float1 >= (float)int3 && float1 < (float)int5 && float2 >= (float)int4 && float2 < (float)int6) {
				return true;
			}
		}

		return this.ReleventPos[int1] != null && Math.abs(this.ReleventPos[int1].x - float1) <= (float)(this.ReleventRange * 10) && Math.abs(this.ReleventPos[int1].y - float2) <= (float)(this.ReleventRange * 10);
	}

	public boolean RelevantTo(float float1, float float2, float float3) {
		for (int int1 = 0; int1 < 4; ++int1) {
			if (this.connectArea[int1] != null) {
				int int2 = (int)this.connectArea[int1].z;
				int int3 = (int)(this.connectArea[int1].x - (float)(int2 / 2)) * 10;
				int int4 = (int)(this.connectArea[int1].y - (float)(int2 / 2)) * 10;
				int int5 = int3 + int2 * 10;
				int int6 = int4 + int2 * 10;
				if (float1 >= (float)int3 && float1 < (float)int5 && float2 >= (float)int4 && float2 < (float)int6) {
					return true;
				}
			}

			if (this.ReleventPos[int1] != null && Math.abs(this.ReleventPos[int1].x - float1) <= float3 && Math.abs(this.ReleventPos[int1].y - float2) <= float3) {
				return true;
			}
		}

		return false;
	}

	public void cancelPacket() {
		this.bufferLock.unlock();
	}

	public int getBufferPosition() {
		return this.bb.position();
	}

	public void endPacket(int int1, int int2, byte byte1) {
		if (GameServer.bServer) {
			int int3 = this.bb.position();
			this.bb.position(1);
			MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int3);
			this.bb.position(int3);
		}

		this.bb.flip();
		this.engine.peer.Send(this.bb, int1, int2, byte1, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPacket() {
		int int1;
		if (GameServer.bServer) {
			int1 = this.bb.position();
			this.bb.position(1);
			MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int1);
			this.bb.position(int1);
		}

		this.bb.flip();
		int1 = this.engine.peer.Send(this.bb, 1, 3, (byte)0, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPacketImmediate() {
		int int1;
		if (GameServer.bServer) {
			int1 = this.bb.position();
			this.bb.position(1);
			MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int1);
			this.bb.position(int1);
		}

		this.bb.flip();
		int1 = this.engine.peer.Send(this.bb, 0, 3, (byte)0, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPacketUnordered() {
		int int1;
		if (GameServer.bServer) {
			int1 = this.bb.position();
			this.bb.position(1);
			MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int1);
			this.bb.position(int1);
		}

		this.bb.flip();
		int1 = this.engine.peer.Send(this.bb, 2, 2, (byte)0, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPacketUnreliable() {
		this.bb.flip();
		int int1 = this.engine.peer.Send(this.bb, 2, 1, (byte)0, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPacketSuperHighUnreliable() {
		int int1;
		if (GameServer.bServer) {
			int1 = this.bb.position();
			this.bb.position(1);
			MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int1);
			this.bb.position(int1);
		}

		this.bb.flip();
		int1 = this.engine.peer.Send(this.bb, 0, 1, (byte)0, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPingPacket() {
		if (GameServer.bServer) {
			int int1 = this.bb.position();
			this.bb.position(1);
			MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int1);
			this.bb.position(int1);
		}

		this.bbPing.flip();
		this.engine.peer.Send(this.bbPing, 0, 1, (byte)0, this.connectedGUID, false);
		this.bufferLockPing.unlock();
	}

	public InetSocketAddress getInetSocketAddress() {
		String string = this.engine.peer.getIPFromGUID(this.connectedGUID);
		if ("UNASSIGNED_SYSTEM_ADDRESS".equals(string)) {
			return null;
		} else {
			string = string.replace("|", "\u00c2\u00a3");
			String[] stringArray = string.split("\u00c2\u00a3");
			InetSocketAddress inetSocketAddress = new InetSocketAddress(stringArray[0], Integer.parseInt(stringArray[1]));
			return inetSocketAddress;
		}
	}

	public void forceDisconnect(String string) {
		if (!GameServer.bServer) {
			GameClient.instance.disconnect();
		}

		this.engine.forceDisconnect(this.getConnectedGUID(), string);
		ConnectionManager.log("force-disconnect", string, this);
	}

	public void setFullyConnected() {
		this.validator.reset();
		this.bFullyConnected = true;
		this.setConnectionTimestamp();
		ConnectionManager.log("fully-connected", "", this);
	}

	public void setConnectionTimestamp() {
		this.connectionTimestamp = System.currentTimeMillis();
	}

	public boolean isConnectionAttemptTimeout() {
		return System.currentTimeMillis() > this.connectionTimestamp + 5000L;
	}

	public boolean isConnectionGraceIntervalTimeout() {
		return System.currentTimeMillis() > this.connectionTimestamp + 60000L || Core.bDebug && SystemDisabler.doKickInDebug;
	}

	public boolean isFullyConnected() {
		return this.bFullyConnected;
	}

	public void calcCountPlayersInRelevantPosition() {
		if (this.isFullyConnected()) {
			boolean boolean1 = false;
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection.isFullyConnected() && udpConnection != this) {
					for (int int2 = 0; int2 < udpConnection.players.length; ++int2) {
						IsoPlayer player = udpConnection.players[int2];
						if (player != null && this.RelevantTo(player.x, player.y, 120.0F)) {
							boolean1 = true;
						}
					}

					if (boolean1) {
						break;
					}
				}
			}

			this.isNeighborPlayer = boolean1;
		}
	}

	public ZNetStatistics getStatistics() {
		try {
			this.netStatistics = this.engine.peer.GetNetStatistics(this.connectedGUID);
		} catch (Exception exception) {
			this.netStatistics = null;
		} finally {
			return this.netStatistics;
		}
	}

	public int getAveragePing() {
		return this.engine.peer.GetAveragePing(this.connectedGUID);
	}

	public int getLastPing() {
		return this.engine.peer.GetLastPing(this.connectedGUID);
	}

	public int getLowestPing() {
		return this.engine.peer.GetLowestPing(this.connectedGUID);
	}

	public int getMTUSize() {
		return this.engine.peer.GetMTUSize(this.connectedGUID);
	}

	public UdpConnection.ConnectionType getConnectionType() {
		return UdpConnection.ConnectionType.values()[this.engine.peer.GetConnectionType(this.connectedGUID)];
	}

	public String toString() {
		return GameClient.bClient ? String.format("guid=%s ip=%s steam-id=%s access=\"%s\" username=\"%s\" connection-type=\"%s\"", this.connectedGUID, this.ip == null ? GameClient.ip : this.ip, this.steamID == 0L ? GameClient.steamID : this.steamID, PlayerType.toString(this.accessLevel), this.username == null ? GameClient.username : this.username, this.getConnectionType().name()) : String.format("guid=%s ip=%s steam-id=%s access=%s username=\"%s\" connection-type=\"%s\"", this.connectedGUID, this.ip, this.steamID, PlayerType.toString(this.accessLevel), this.username, this.getConnectionType().name());
	}

	public boolean havePlayer(IsoPlayer player) {
		if (player == null) {
			return false;
		} else {
			for (int int1 = 0; int1 < this.players.length; ++int1) {
				if (this.players[int1] == player) {
					return true;
				}
			}

			return false;
		}
	}

	public static enum ChecksumState {

		Init,
		Different,
		Done;

		private static UdpConnection.ChecksumState[] $values() {
			return new UdpConnection.ChecksumState[]{Init, Different, Done};
		}
	}

	public class MPClientStatistic {
		public byte enable = 0;
		public int diff = 0;
		public float pingAVG = 0.0F;
		public int zombiesCount = 0;
		public int zombiesLocalOwnership = 0;
		public float zombiesDesyncAVG = 0.0F;
		public float zombiesDesyncMax = 0.0F;
		public int zombiesTeleports = 0;
		public int remotePlayersCount = 0;
		public float remotePlayersDesyncAVG = 0.0F;
		public float remotePlayersDesyncMax = 0.0F;
		public int remotePlayersTeleports = 0;
		public float FPS = 0.0F;
		public float FPSMin = 0.0F;
		public float FPSAvg = 0.0F;
		public float FPSMax = 0.0F;
		public short[] FPSHistogramm = new short[32];

		public void parse(ByteBuffer byteBuffer) {
			long long1 = byteBuffer.getLong();
			long long2 = System.currentTimeMillis();
			this.diff = (int)(long2 - long1);
			this.pingAVG += ((float)this.diff * 0.5F - this.pingAVG) * 0.1F;
			this.zombiesCount = byteBuffer.getInt();
			this.zombiesLocalOwnership = byteBuffer.getInt();
			this.zombiesDesyncAVG = byteBuffer.getFloat();
			this.zombiesDesyncMax = byteBuffer.getFloat();
			this.zombiesTeleports = byteBuffer.getInt();
			this.remotePlayersCount = byteBuffer.getInt();
			this.remotePlayersDesyncAVG = byteBuffer.getFloat();
			this.remotePlayersDesyncMax = byteBuffer.getFloat();
			this.remotePlayersTeleports = byteBuffer.getInt();
			this.FPS = byteBuffer.getFloat();
			this.FPSMin = byteBuffer.getFloat();
			this.FPSAvg = byteBuffer.getFloat();
			this.FPSMax = byteBuffer.getFloat();
			for (int int1 = 0; int1 < 32; ++int1) {
				this.FPSHistogramm[int1] = byteBuffer.getShort();
			}
		}
	}

	public static enum ConnectionType {

		Disconnected,
		UDPRakNet,
		Steam;

		private static UdpConnection.ConnectionType[] $values() {
			return new UdpConnection.ConnectionType[]{Disconnected, UDPRakNet, Steam};
		}
	}
}
