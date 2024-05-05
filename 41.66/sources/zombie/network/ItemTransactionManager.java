package zombie.network;

import java.nio.ByteBuffer;
import java.util.HashSet;
import zombie.GameTime;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;


public class ItemTransactionManager {
	private static final HashSet requests = new HashSet();

	public static void update() {
		requests.removeIf(ItemTransactionManager.ItemRequest::isTimeout);
	}

	public static boolean isConsistent(int int1, int int2, int int3) {
		boolean boolean1 = requests.stream().filter((boolean1x)->{
    return int1 == boolean1x.itemID || int2 == boolean1x.itemID || int3 == boolean1x.itemID || int1 == boolean1x.srcID || int1 == boolean1x.dstID;
}).noneMatch((int1x)->{
    return int1x.state == 1;
});
		return boolean1;
	}

	public static void receiveOnClient(ByteBuffer byteBuffer, short short1) {
		try {
			byte byte1 = byteBuffer.get();
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			DebugLog.Multiplayer.debugln("%d [ %d : %d => %d ]", byte1, int1, int2, int3);
			requests.stream().filter((int1x)->{
				return byteBuffer == int1x.itemID && short1 == int1x.srcID && byte1 == int1x.dstID;
			}).forEach((short1x)->{
				short1x.setState(byte1);
			});
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveOnClient: failed", LogSeverity.Error);
		}
	}

	public static void receiveOnServer(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			byte byte1 = byteBuffer.get();
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			if (0 == byte1) {
				if (isConsistent(int1, int2, int3)) {
					requests.add(new ItemTransactionManager.ItemRequest(int1, int2, int3));
					sendItemTransaction(udpConnection, (byte)2, int1, int2, int3);
					DebugLog.Multiplayer.debugln("set accepted [ %d : %d => %d ]", int1, int2, int3);
				} else {
					sendItemTransaction(udpConnection, (byte)1, int1, int2, int3);
					DebugLog.Multiplayer.debugln("set rejected [ %d : %d => %d ]", int1, int2, int3);
				}
			} else {
				requests.removeIf((byte1x)->{
					return byteBuffer == byte1x.itemID && udpConnection == byte1x.srcID && short1 == byte1x.dstID;
				});

				DebugLog.Multiplayer.debugln("remove processed [ %d : %d => %d ]", int1, int2, int3);
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveOnClient: failed", LogSeverity.Error);
		}
	}

	public static void createItemTransaction(int int1, int int2, int int3) {
		if (isConsistent(int1, int2, int3)) {
			requests.add(new ItemTransactionManager.ItemRequest(int1, int2, int3));
			sendItemTransaction(GameClient.connection, (byte)0, int1, int2, int3);
		}
	}

	public static void removeItemTransaction(int int1, int int2, int int3) {
		if (requests.removeIf((var3x)->{
			return int1 == var3x.itemID && int2 == var3x.srcID && int3 == var3x.dstID;
		})) {
			sendItemTransaction(GameClient.connection, (byte)2, int1, int2, int3);
		}
	}

	private static void sendItemTransaction(UdpConnection udpConnection, byte byte1, int int1, int int2, int int3) {
		if (udpConnection != null) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			try {
				PacketTypes.PacketType.ItemTransaction.doPacket(byteBufferWriter);
				byteBufferWriter.putByte(byte1);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putInt(int3);
				PacketTypes.PacketType.ItemTransaction.send(udpConnection);
			} catch (Exception exception) {
				udpConnection.cancelPacket();
				DebugLog.Multiplayer.printException(exception, "SendItemTransaction: failed", LogSeverity.Error);
			}
		}
	}

	private static class ItemRequest {
		private static final byte StateUnknown = 0;
		private static final byte StateRejected = 1;
		private static final byte StateAccepted = 2;
		private final int itemID;
		private final int srcID;
		private final int dstID;
		private final long timestamp;
		private byte state;

		private ItemRequest(int int1, int int2, int int3) {
			this.itemID = int1;
			this.srcID = int2;
			this.dstID = int3;
			this.timestamp = GameTime.getServerTimeMills() + 5000L;
			this.state = (byte)(GameServer.bServer ? 1 : 0);
		}

		private void setState(byte byte1) {
			this.state = byte1;
		}

		private boolean isTimeout() {
			return GameTime.getServerTimeMills() > this.timestamp;
		}
	}
}
