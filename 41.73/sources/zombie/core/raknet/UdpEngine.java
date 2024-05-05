package zombie.core.raknet;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.secure.PZcrypt;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.network.ConnectionManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.PacketValidator;
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;


public class UdpEngine {
	private int maxConnections = 0;
	private final Map connectionMap = new HashMap();
	public final List connections = new ArrayList();
	protected final RakNetPeerInterface peer;
	final boolean bServer;
	Lock bufferLock = new ReentrantLock();
	private ByteBuffer bb = ByteBuffer.allocate(500000);
	private ByteBufferWriter bbw;
	public int port;
	private final Thread thread;
	private boolean bQuit;
	UdpConnection[] connectionArray;
	ByteBuffer buf;

	public UdpEngine(int int1, int int2, String string, boolean boolean1) throws ConnectException {
		this.bbw = new ByteBufferWriter(this.bb);
		this.port = 0;
		this.connectionArray = new UdpConnection[256];
		this.buf = ByteBuffer.allocate(1000000);
		this.port = int1;
		this.peer = new RakNetPeerInterface();
		DebugLog.Network.println("Initialising RakNet...");
		this.peer.Init(SteamUtils.isSteamModeEnabled());
		this.peer.SetMaximumIncomingConnections(int2);
		this.bServer = boolean1;
		if (this.bServer) {
			if (GameServer.IPCommandline != null) {
				this.peer.SetServerIP(GameServer.IPCommandline);
			}

			this.peer.SetServerPort(int1);
			this.peer.SetIncomingPassword(this.hashServerPassword(string));
		} else {
			this.peer.SetClientPort(GameServer.DEFAULT_PORT + Rand.Next(10000) + 1234);
		}

		this.peer.SetOccasionalPing(true);
		this.maxConnections = int2;
		int int3 = this.peer.Startup(int2);
		DebugLog.Network.println("RakNet.Startup() return code: %s (0 means success)", int3);
		if (int3 != 0) {
			throw new ConnectException("Connection Startup Failed. Code: " + int3);
		} else {
			if (boolean1) {
				VoiceManager.instance.InitVMServer();
			}

			this.thread = new Thread(ThreadGroups.Network, this::threadRun, "UdpEngine");
			this.thread.setDaemon(true);
			this.thread.start();
		}
	}

	private void threadRun() {
		while (true) {
			if (!this.bQuit) {
				ByteBuffer byteBuffer = this.Receive();
				if (!this.bQuit) {
					try {
						this.decode(byteBuffer);
					} catch (Exception exception) {
						DebugLog.Network.printException(exception, "Exception thrown during decode.", LogSeverity.Error);
					}

					continue;
				}
			}

			return;
		}
	}

	public void Shutdown() {
		DebugLog.log("waiting for UdpEngine thread termination");
		this.bQuit = true;
		while (this.thread.isAlive()) {
			try {
				Thread.sleep(10L);
			} catch (InterruptedException interruptedException) {
			}
		}

		this.peer.Shutdown();
	}

	public void SetServerPassword(String string) {
		if (this.peer != null) {
			this.peer.SetIncomingPassword(string);
		}
	}

	public String hashServerPassword(String string) {
		return PZcrypt.hash(string, true);
	}

	public String getServerIP() {
		return this.peer.GetServerIP();
	}

	public long getClientSteamID(long long1) {
		return this.peer.GetClientSteamID(long1);
	}

	public long getClientOwnerSteamID(long long1) {
		return this.peer.GetClientOwnerSteamID(long1);
	}

	public ByteBufferWriter startPacket() {
		this.bufferLock.lock();
		this.bb.clear();
		return this.bbw;
	}

	public void endPacketBroadcast(PacketTypes.PacketType packetType) {
		this.bb.flip();
		this.peer.Send(this.bb, packetType.PacketPriority, packetType.PacketPriority, (byte)0, -1L, true);
		this.bufferLock.unlock();
	}

	public void endPacketBroadcastExcept(int int1, int int2, UdpConnection udpConnection) {
		this.bb.flip();
		this.peer.Send(this.bb, int1, int2, (byte)0, udpConnection.connectedGUID, true);
		this.bufferLock.unlock();
	}

	private void decode(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.get() & 255;
		long long1;
		UdpConnection udpConnection;
		long long2;
		int int2;
		switch (int1) {
		case 0: 
		
		case 1: 
		
		case 20: 
		
		case 25: 
		
		case 31: 
		
		case 33: 
			break;
		
		case 16: 
			DebugLog.Network.println("Connection Request Accepted");
			int2 = byteBuffer.get() & 255;
			long1 = this.peer.getGuidOfPacket();
			udpConnection = this.addConnection(int2, long1);
			VoiceManager.instance.VoiceConnectReq(long1);
			ByteBufferWriter byteBufferWriter;
			if (GameClient.bClient && !GameClient.askPing) {
				GameClient.startAuth = Calendar.getInstance();
				GameClient.connection = udpConnection;
				byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.Login.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF(GameClient.username);
				byteBufferWriter.putUTF(PZcrypt.hash(ServerWorldDatabase.encrypt(GameClient.password)));
				byteBufferWriter.putUTF(Core.getInstance().getVersionNumber());
				PacketTypes.PacketType.Login.send(udpConnection);
			} else if (GameClient.bClient && GameClient.askPing) {
				GameClient.connection = udpConnection;
				byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.Ping.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF(GameClient.ip);
				PacketTypes.PacketType.Ping.send(udpConnection);
			}

			break;
		
		case 17: 
			if (GameClient.bClient) {
				GameClient.instance.addDisconnectPacket(int1);
			}

			break;
		
		case 18: 
			DebugLog.Network.println("User Already Connected");
			if (GameClient.bClient) {
				GameClient.instance.addDisconnectPacket(int1);
			}

			break;
		
		case 19: 
			int2 = byteBuffer.get() & 255;
			long1 = this.peer.getGuidOfPacket();
			this.addConnection(int2, long1);
			break;
		
		case 21: 
			int2 = byteBuffer.get() & 255;
			long1 = this.peer.getGuidOfPacket();
			this.removeConnection(int2);
			ConnectionManager.log("Receive Disconnect [" + int1 + "]", (UdpConnection)null);
			if (GameClient.bClient) {
				GameClient.instance.addDisconnectPacket(int1);
			}

			break;
		
		case 22: 
			int2 = byteBuffer.get() & 255;
			if (GameServer.bServer && this.connectionArray[int2] != null) {
				DebugLog.log("Connection Lost for id=" + int2 + " username=" + this.connectionArray[int2].username);
			} else {
				DebugLog.log("Connection Lost");
			}

			ConnectionManager.log("Receive Disconnect [" + int1 + "]", (UdpConnection)null);
			this.removeConnection(int2);
			break;
		
		case 23: 
			DebugLog.Network.println("User Banned");
			if (GameClient.bClient) {
				GameClient.instance.addDisconnectPacket(int1);
			}

			break;
		
		case 24: 
			if (GameClient.bClient) {
				GameClient.instance.addDisconnectPacket(int1);
			}

			break;
		
		case 32: 
			if (GameClient.bClient) {
				GameClient.instance.addDisconnectPacket(int1);
			}

			break;
		
		case 44: 
			long2 = this.peer.getGuidOfPacket();
			VoiceManager.instance.VoiceConnectAccept(long2);
			break;
		
		case 45: 
			long2 = this.peer.getGuidOfPacket();
			VoiceManager.instance.VoiceOpenChannelReply(long2, byteBuffer);
			break;
		
		case 134: 
			short short1 = byteBuffer.getShort();
			if (GameServer.bServer) {
				long1 = this.peer.getGuidOfPacket();
				udpConnection = (UdpConnection)this.connectionMap.get(long1);
				if (udpConnection == null) {
					DebugLog.Network.warn("GOT PACKET FROM UNKNOWN CONNECTION guid=%s packetId=%s", long1, Integer.valueOf(short1));
					return;
				}

				GameServer.addIncoming((short)short1, byteBuffer, udpConnection);
			} else {
				GameClient.instance.addIncoming((short)short1, byteBuffer);
			}

			break;
		
		default: 
			DebugLog.Network.warn("Received unknown packet: %s", int1);
			if (GameServer.bServer) {
				long2 = this.peer.getGuidOfPacket();
				UdpConnection udpConnection2 = (UdpConnection)this.connectionMap.get(long2);
				try {
					if (ServerOptions.instance.AntiCheatProtectionType10.getValue() && PacketValidator.doKickUser(udpConnection2, String.valueOf(int1), "UI_ValidationFailed_Type10")) {
						LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" description=\"%s\"", udpConnection2.username, "UI_ValidationFailed_Type10", String.valueOf(int1), this.getDescription()));
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

		
		}
	}

	private void removeConnection(int int1) {
		UdpConnection udpConnection = this.connectionArray[int1];
		if (udpConnection != null) {
			this.connectionArray[int1] = null;
			this.connectionMap.remove(udpConnection.getConnectedGUID());
			if (GameClient.bClient) {
				GameClient.instance.connectionLost();
			}

			if (GameServer.bServer) {
				GameServer.addDisconnect(udpConnection);
			}
		}
	}

	private UdpConnection addConnection(int int1, long long1) {
		UdpConnection udpConnection = new UdpConnection(this, long1, int1);
		this.connectionMap.put(long1, udpConnection);
		this.connectionArray[int1] = udpConnection;
		if (GameServer.bServer) {
			GameServer.addConnection(udpConnection);
		}

		return udpConnection;
	}

	public ByteBuffer Receive() {
		boolean boolean1 = false;
		do {
			boolean1 = this.peer.Receive(this.buf);
			if (boolean1) {
				return this.buf;
			}

			try {
				Thread.sleep(1L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		} while (!this.bQuit && !boolean1);

		return this.buf;
	}

	public UdpConnection getActiveConnection(long long1) {
		return !this.connectionMap.containsKey(long1) ? null : (UdpConnection)this.connectionMap.get(long1);
	}

	public void Connect(String string, int int1, String string2) {
		if (int1 == 0 && SteamUtils.isSteamModeEnabled()) {
			long long1 = 0L;
			try {
				long1 = SteamUtils.convertStringToSteamID(string);
			} catch (NumberFormatException numberFormatException) {
				numberFormatException.printStackTrace();
				LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_UnknownHost"));
				return;
			}

			this.peer.ConnectToSteamServer(long1, this.hashServerPassword(string2));
		} else {
			String string3;
			try {
				InetAddress inetAddress = InetAddress.getByName(string);
				string3 = inetAddress.getHostAddress();
			} catch (UnknownHostException unknownHostException) {
				unknownHostException.printStackTrace();
				LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_UnknownHost"));
				return;
			}

			this.peer.Connect(string3, int1, this.hashServerPassword(string2));
		}
	}

	public void Connect(long long1, String string) {
		this.peer.ConnectToSteamServer(long1, string);
	}

	public void forceDisconnect(long long1) {
		this.peer.disconnect(long1);
		this.removeConnection(long1);
	}

	private void removeConnection(long long1) {
		UdpConnection udpConnection = (UdpConnection)this.connectionMap.remove(long1);
		if (udpConnection != null) {
			this.removeConnection(udpConnection.index);
		}
	}

	public RakNetPeerInterface getPeer() {
		return this.peer;
	}

	public int getMaxConnections() {
		return this.maxConnections;
	}

	public String getDescription() {
		int int1 = this.connections.size();
		return "connections=[" + int1 + "/" + this.connectionMap.size() + "/" + Arrays.stream(this.connectionArray).filter(Objects::nonNull).count() + "/" + this.peer.GetConnectionsNumber() + "]";
	}
}
