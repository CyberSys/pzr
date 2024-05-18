package zombie.core.raknet;

import gnu.trove.list.array.TShortArrayList;
import gnu.trove.set.hash.TShortHashSet;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import sun.misc.Lock;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.iso.Vector3;
import zombie.network.ClientServerMap;
import zombie.network.PlayerDownloadServer;


public class UdpConnection {
	Lock bufferLock = new Lock();
	private ByteBuffer bb = ByteBuffer.allocate(1000000);
	private ByteBufferWriter bbw;
	Lock bufferLockPing;
	private ByteBuffer bbPing;
	private ByteBufferWriter bbwPing;
	long connectedGUID;
	UdpEngine engine;
	public boolean connected;
	public int index;
	public boolean allChatMuted;
	public String username;
	public String[] usernames;
	public byte ReleventRange;
	public String accessLevel;
	public String ip;
	public String password;
	public boolean ping;
	public Vector3[] ReleventPos;
	public int[] playerIDs;
	public IsoPlayer[] players;
	public Vector3[] connectArea;
	public int ChunkGridWidth;
	public ClientServerMap[] loadedCells;
	public PlayerDownloadServer playerDownloadServer;
	public UdpConnection.ChecksumState checksumState;
	public long checksumTime;
	public boolean sendPulse;
	public boolean awaitingCoopApprove;
	public long steamID;
	public long ownerID;
	public String idStr;
	public boolean isCoopHost;
	public TShortHashSet vehicles;
	public final TShortArrayList chunkObjectState;
	public final long[] packetCounts;
	private boolean bFullyConnected;

	public UdpConnection(UdpEngine udpEngine, long long1, int int1) {
		this.bbw = new ByteBufferWriter(this.bb);
		this.bufferLockPing = new Lock();
		this.bbPing = ByteBuffer.allocate(50);
		this.bbwPing = new ByteBufferWriter(this.bbPing);
		this.connectedGUID = 0L;
		this.connected = true;
		this.allChatMuted = false;
		this.usernames = new String[4];
		this.accessLevel = "";
		this.ping = false;
		this.ReleventPos = new Vector3[4];
		this.playerIDs = new int[4];
		this.players = new IsoPlayer[4];
		this.connectArea = new Vector3[4];
		this.loadedCells = new ClientServerMap[4];
		this.checksumState = UdpConnection.ChecksumState.Init;
		this.sendPulse = false;
		this.awaitingCoopApprove = false;
		this.vehicles = new TShortHashSet();
		this.chunkObjectState = new TShortArrayList();
		this.packetCounts = new long[256];
		this.bFullyConnected = false;
		this.engine = udpEngine;
		this.connectedGUID = long1;
		this.index = int1;
		this.ReleventPos[0] = new Vector3();
		for (int int2 = 0; int2 < 4; ++int2) {
			this.playerIDs[int2] = -1;
		}
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
		try {
			this.bufferLock.lock();
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}

		this.bb.clear();
		return this.bbw;
	}

	public ByteBufferWriter startPingPacket() {
		try {
			this.bufferLockPing.lock();
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}

		this.bbPing.clear();
		return this.bbwPing;
	}

	public boolean ReleventTo(float float1, float float2) {
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

	public boolean ReleventToPlayers(double double1, double double2, double double3) {
		for (int int1 = 0; int1 < 4; ++int1) {
			if (this.players[int1] != null) {
				double double4 = (double)this.players[int1].x - double1;
				double double5 = (double)this.players[int1].y - double2;
				double4 *= double4;
				double5 *= double5;
				if (Math.sqrt(double4 + double5) < double3) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean ReleventToPlayerIndex(int int1, float float1, float float2) {
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

	public void endPacket(int int1, int int2) {
		this.bb.flip();
		this.engine.peer.Send(this.bb, int1, int2, (byte)0, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPacket() {
		this.bb.flip();
		int int1 = this.engine.peer.Send(this.bb, 1, 3, (byte)0, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPacketImmediate() {
		this.bb.flip();
		int int1 = this.engine.peer.Send(this.bb, 0, 3, (byte)0, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPacketUnordered() {
		this.bb.flip();
		int int1 = this.engine.peer.Send(this.bb, 2, 2, (byte)0, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPacketUnreliable() {
		this.bb.flip();
		int int1 = this.engine.peer.Send(this.bb, 2, 1, (byte)0, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPacketSuperHighUnreliable() {
		this.bb.flip();
		int int1 = this.engine.peer.Send(this.bb, 0, 1, (byte)0, this.connectedGUID, false);
		this.bufferLock.unlock();
	}

	public void endPingPacket() {
		this.bbPing.flip();
		this.engine.peer.Send(this.bbPing, 0, 1, (byte)0, this.connectedGUID, false);
		this.bufferLockPing.unlock();
	}

	public void close() {
	}

	public void disconnect(String string) {
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

	public void forceDisconnect() {
		this.engine.forceDisconnect(this.getConnectedGUID());
	}

	public void setFullyConnected() {
		this.bFullyConnected = true;
	}

	public boolean isFullyConnected() {
		return this.bFullyConnected;
	}
	public static enum ChecksumState {

		Init,
		Different,
		Done;
	}
}
