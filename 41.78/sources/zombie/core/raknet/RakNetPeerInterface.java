package zombie.core.raknet;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.lwjglx.BufferUtils;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.znet.ZNetStatistics;
import zombie.debug.DebugLog;
import zombie.network.GameClient;


public class RakNetPeerInterface {
	private static Thread mainThread;
	public static final int ID_NEW_INCOMING_CONNECTION = 19;
	public static final int ID_DISCONNECTION_NOTIFICATION = 21;
	public static final int ID_INCOMPATIBLE_PROTOCOL_VERSION = 25;
	public static final int ID_CONNECTED_PING = 0;
	public static final int ID_UNCONNECTED_PING = 1;
	public static final int ID_CONNECTION_LOST = 22;
	public static final int ID_ALREADY_CONNECTED = 18;
	public static final int ID_REMOTE_DISCONNECTION_NOTIFICATION = 31;
	public static final int ID_REMOTE_CONNECTION_LOST = 32;
	public static final int ID_REMOTE_NEW_INCOMING_CONNECTION = 33;
	public static final int ID_CONNECTION_BANNED = 23;
	public static final int ID_CONNECTION_ATTEMPT_FAILED = 17;
	public static final int ID_NO_FREE_INCOMING_CONNECTIONS = 20;
	public static final int ID_CONNECTION_REQUEST_ACCEPTED = 16;
	public static final int ID_INVALID_PASSWORD = 24;
	public static final int ID_PING = 28;
	public static final int ID_RAKVOICE_OPEN_CHANNEL_REQUEST = 44;
	public static final int ID_RAKVOICE_OPEN_CHANNEL_REPLY = 45;
	public static final int ID_RAKVOICE_CLOSE_CHANNEL = 46;
	public static final int ID_USER_PACKET_ENUM = 134;
	public static final int PacketPriority_IMMEDIATE = 0;
	public static final int PacketPriority_HIGH = 1;
	public static final int PacketPriority_MEDIUM = 2;
	public static final int PacketPriority_LOW = 3;
	public static final int PacketReliability_UNRELIABLE = 0;
	public static final int PacketReliability_UNRELIABLE_SEQUENCED = 1;
	public static final int PacketReliability_RELIABLE = 2;
	public static final int PacketReliability_RELIABLE_ORDERED = 3;
	public static final int PacketReliability_RELIABLE_SEQUENCED = 4;
	public static final int PacketReliability_UNRELIABLE_WITH_ACK_RECEIPT = 5;
	public static final int PacketReliability_RELIABLE_WITH_ACK_RECEIPT = 6;
	public static final int PacketReliability_RELIABLE_ORDERED_WITH_ACK_RECEIPT = 7;
	public static final byte ConnectionType_Disconnected = 0;
	public static final byte ConnectionType_UDPRakNet = 1;
	public static final byte ConnectionType_Steam = 2;
	ByteBuffer receiveBuf = BufferUtils.createByteBuffer(1000000);
	ByteBuffer sendBuf = BufferUtils.createByteBuffer(1000000);
	Lock sendLock = new ReentrantLock();

	public static void init() {
		mainThread = Thread.currentThread();
	}

	public native void Init(boolean boolean1);

	private native int Startup(int int1, String string);

	public int Startup(int int1) {
		return this.Startup(int1, Core.getInstance().getVersion());
	}

	public native void Shutdown();

	public native void SetServerIP(String string);

	public native void SetServerPort(int int1, int int2);

	public native void SetClientPort(int int1);

	public native int Connect(String string, int int1, String string2, boolean boolean1);

	public native int ConnectToSteamServer(long long1, String string, boolean boolean1);

	public native String GetServerIP();

	public native long GetClientSteamID(long long1);

	public native long GetClientOwnerSteamID(long long1);

	public native void SetIncomingPassword(String string);

	public native void SetTimeoutTime(int int1);

	public native void SetMaximumIncomingConnections(int int1);

	public native void SetOccasionalPing(boolean boolean1);

	public native void SetUnreliableTimeout(int int1);

	private native boolean TryReceive();

	private native int nativeGetData(ByteBuffer byteBuffer);

	public boolean Receive(ByteBuffer byteBuffer) {
		if (this.TryReceive()) {
			try {
				byteBuffer.clear();
				this.receiveBuf.clear();
				int int1 = this.nativeGetData(this.receiveBuf);
				byteBuffer.put(this.receiveBuf);
				byteBuffer.flip();
				return true;
			} catch (Exception exception) {
				exception.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public int Send(ByteBuffer byteBuffer, int int1, int int2, byte byte1, long long1, boolean boolean1) {
		this.sendLock.lock();
		this.sendBuf.clear();
		if (byteBuffer.remaining() > this.sendBuf.remaining()) {
			System.out.println("Packet data too big.");
			this.sendLock.unlock();
			return 0;
		} else {
			try {
				this.sendBuf.put(byteBuffer);
				this.sendBuf.flip();
				int int3 = this.sendNative(this.sendBuf, this.sendBuf.remaining(), int1, int2, byte1, long1, boolean1);
				this.sendLock.unlock();
				return int3;
			} catch (Exception exception) {
				System.out.println("Other weird packet data error.");
				exception.printStackTrace();
				this.sendLock.unlock();
				return 0;
			}
		}
	}

	public int SendRaw(ByteBuffer byteBuffer, int int1, int int2, byte byte1, long long1, boolean boolean1) {
		try {
			int int3 = this.sendNative(byteBuffer, byteBuffer.remaining(), int1, int2, byte1, long1, boolean1);
			return int3;
		} catch (Exception exception) {
			System.out.println("Other weird packet data error.");
			exception.printStackTrace();
			return 0;
		}
	}

	private native int sendNative(ByteBuffer byteBuffer, int int1, int int2, int int3, byte byte1, long long1, boolean boolean1);

	public native long getGuidFromIndex(int int1);

	public native long getGuidOfPacket();

	public native String getIPFromGUID(long long1);

	public native void disconnect(long long1, String string);

	private void connectionStateChangedCallback(String string, String string2) {
		Thread thread = Thread.currentThread();
		if (thread == mainThread) {
			LuaEventManager.triggerEvent("OnConnectionStateChanged", string, string2);
		} else {
			DebugLog.Multiplayer.debugln("state=\"%s\", message=\"%s\", thread=%s", string, string2, thread);
		}

		if (GameClient.bClient && "Connected".equals(string)) {
			GameClient.instance.udpEngine.connected();
		}
	}

	public native ZNetStatistics GetNetStatistics(long long1);

	public native int GetAveragePing(long long1);

	public native int GetLastPing(long long1);

	public native int GetLowestPing(long long1);

	public native int GetMTUSize(long long1);

	public native int GetConnectionsNumber();

	public native byte GetConnectionType(long long1);
}
