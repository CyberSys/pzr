package zombie.core.raknet;

import java.nio.ByteBuffer;


public class TestServer {
	static RakNetPeerInterface server;
	static ByteBuffer buf = ByteBuffer.allocate(2048);

	public static void main(String[] stringArray) {
		server = new RakNetPeerInterface();
		server.SetServerPort(12203);
		server.Init(false);
		int int1 = server.Startup(32);
		System.out.println("Result: " + int1);
		server.SetMaximumIncomingConnections(32);
		server.SetOccasionalPing(true);
		server.SetIncomingPassword("spiffo");
		boolean boolean1 = false;
		while (!boolean1) {
			String string = "This is a test message";
			ByteBuffer byteBuffer = Receive();
			decode(byteBuffer);
		}
	}

	private static void decode(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.get() & 255;
		switch (int1) {
		case 0: 
		
		case 1: 
			System.out.println("PING");
			break;
		
		case 19: 
			int int2 = byteBuffer.get() & 255;
			long long1 = server.getGuidFromIndex(int2);
			break;
		
		case 21: 
			System.out.println("ID_DISCONNECTION_NOTIFICATION");
			break;
		
		case 22: 
			System.out.println("ID_CONNECTION_LOST");
			break;
		
		case 25: 
			System.out.println("ID_INCOMPATIBLE_PROTOCOL_VERSION");
			break;
		
		default: 
			System.out.println("Other: " + int1);
		
		}
	}

	public static ByteBuffer Receive() {
		int int1 = buf.position();
		boolean boolean1 = false;
		do {
			try {
				Thread.sleep(1L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}

			boolean1 = server.Receive(buf);
		} while (!boolean1);

		return buf;
	}
}
