package zombie.core.raknet;

import java.nio.ByteBuffer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.network.GameServer;


public class VoiceTest {
	protected static boolean bQuit = false;
	protected static ByteBuffer serverBuf = ByteBuffer.allocate(500000);
	protected static ByteBuffer clientBuf = ByteBuffer.allocate(500000);
	protected static RakNetPeerInterface rnclientPeer;
	protected static RakNetPeerInterface rnserverPeer;

	protected static void rakNetServer(int int1) {
		byte byte1 = 2;
		String string = "test";
		rnserverPeer = new RakNetPeerInterface();
		DebugLog.log("Initialising RakNet...");
		rnserverPeer.Init(false);
		rnserverPeer.SetMaximumIncomingConnections(byte1);
		if (GameServer.IPCommandline != null) {
			rnserverPeer.SetServerIP(GameServer.IPCommandline);
		}

		rnserverPeer.SetServerPort(int1, int1 + 1);
		rnserverPeer.SetIncomingPassword(string);
		rnserverPeer.SetOccasionalPing(true);
		int int2 = rnserverPeer.Startup(byte1);
		System.out.println("RakNet.Startup() return code: " + int2 + " (0 means success)");
	}

	public static ByteBuffer rakNetServerReceive() {
		boolean boolean1 = false;
		do {
			try {
				Thread.sleep(1L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}

			boolean1 = rnserverPeer.Receive(serverBuf);
		} while (!bQuit && !boolean1);

		return serverBuf;
	}

	private static void rakNetServerDecode(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.get() & 255;
		int int2;
		long long1;
		switch (int1) {
		case 0: 
		
		case 1: 
			System.out.println("PING");
			break;
		
		case 16: 
			System.out.println("Connection Request Accepted");
			int2 = byteBuffer.get() & 255;
			long1 = rnserverPeer.getGuidOfPacket();
			VoiceManager.instance.VoiceConnectReq(long1);
			break;
		
		case 19: 
			System.out.println("ID_NEW_INCOMING_CONNECTION");
			int2 = byteBuffer.get() & 255;
			long1 = rnserverPeer.getGuidOfPacket();
			System.out.println("id=" + int2 + " guid=" + long1);
			VoiceManager.instance.VoiceConnectReq(long1);
			break;
		
		default: 
			System.out.println("Received: " + int1);
		
		}
	}

	protected static void rakNetClient() {
		byte byte1 = 2;
		String string = "test";
		rnclientPeer = new RakNetPeerInterface();
		DebugLog.log("Initialising RakNet...");
		rnclientPeer.Init(false);
		rnclientPeer.SetMaximumIncomingConnections(byte1);
		rnclientPeer.SetClientPort(GameServer.DEFAULT_PORT + Rand.Next(10000) + 1234);
		rnclientPeer.SetOccasionalPing(true);
		int int1 = rnclientPeer.Startup(byte1);
		System.out.println("RakNet.Startup() return code: " + int1 + " (0 means success)");
	}

	public static ByteBuffer rakNetClientReceive() {
		boolean boolean1 = false;
		do {
			try {
				Thread.sleep(1L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}

			boolean1 = rnclientPeer.Receive(clientBuf);
		} while (!bQuit && !boolean1);

		return clientBuf;
	}

	private static void rakNetClientDecode(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.get() & 255;
		int int2;
		long long1;
		switch (int1) {
		case 0: 
		
		case 1: 
			System.out.println("PING");
			break;
		
		case 16: 
			System.out.println("Connection Request Accepted");
			int2 = byteBuffer.get() & 255;
			long1 = rnclientPeer.getGuidOfPacket();
			VoiceManager.instance.VoiceConnectReq(long1);
			break;
		
		case 19: 
			System.out.println("ID_NEW_INCOMING_CONNECTION");
			int2 = byteBuffer.get() & 255;
			long1 = rnclientPeer.getGuidOfPacket();
			System.out.println("id=" + int2 + " guid=" + long1);
			VoiceManager.instance.VoiceConnectReq(long1);
			break;
		
		default: 
			System.out.println("Received: " + int1);
		
		}
	}

	public static void main(String[] stringArray) {
		DebugLog.log("VoiceTest: START");
		DebugLog.log("version=" + Core.getInstance().getVersion() + " demo=false");
		DebugLog.log("VoiceTest: SteamUtils.init - EXEC");
		SteamUtils.init();
		DebugLog.log("VoiceTest: SteamUtils.init - OK");
		DebugLog.log("VoiceTest: RakNetPeerInterface - EXEC");
		RakNetPeerInterface.init();
		DebugLog.log("VoiceTest: RakNetPeerInterface - OK");
		DebugLog.log("VoiceTest: VoiceManager.InitVMServer - EXEC");
		VoiceManager.instance.InitVMServer();
		DebugLog.log("VoiceTest: VoiceManager.InitVMServer - OK");
		DebugLog.log("VoiceTest: rakNetServer - EXEC");
		rakNetServer(16000);
		DebugLog.log("VoiceTest: rakNetServer - OK");
		DebugLog.log("VoiceTest: rakNetClient - EXEC");
		rakNetClient();
		DebugLog.log("VoiceTest: rakNetClient - OK");
		DebugLog.log("VoiceTest: rnclientPeer.Connect - EXEC");
		rnclientPeer.Connect("127.0.0.1", 16000, "test", false);
		DebugLog.log("VoiceTest: rnclientPeer.Connect - OK");
		Thread thread = new Thread(){
    
    public void run() {
        while (!VoiceTest.bQuit && !VoiceTest.bQuit) {
            ByteBuffer thread = VoiceTest.rakNetServerReceive();
            try {
                VoiceTest.rakNetServerDecode(thread);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }
    }
};
		thread.setName("serverThread");
		thread.start();
		Thread thread2 = new Thread(){
    
    public void run() {
        while (!VoiceTest.bQuit && !VoiceTest.bQuit) {
            ByteBuffer thread = VoiceTest.rakNetClientReceive();
            try {
                VoiceTest.rakNetClientDecode(thread);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }
    }
};
		thread2.setName("clientThread");
		thread2.start();
		DebugLog.log("VoiceTest: sleep 10 sec");
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}
}
