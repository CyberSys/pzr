package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.TableNetworkUtils;
import zombie.spnetwork.SinglePlayerServer;


public final class SGlobalObjectNetwork {
	public static final byte PACKET_ServerCommand = 1;
	public static final byte PACKET_ClientCommand = 2;
	public static final byte PACKET_NewLuaObjectAt = 3;
	public static final byte PACKET_RemoveLuaObjectAt = 4;
	public static final byte PACKET_UpdateLuaObjectAt = 5;
	private static final ByteBuffer BYTE_BUFFER = ByteBuffer.allocate(1048576);
	private static final ByteBufferWriter BYTE_BUFFER_WRITER;

	public static void receive(ByteBuffer byteBuffer, IsoPlayer player) {
		byte byte1 = byteBuffer.get();
		switch (byte1) {
		case 2: 
			receiveClientCommand(byteBuffer, player);
		
		default: 
		
		}
	}

	private static void sendPacket(ByteBuffer byteBuffer) {
		int int1;
		ByteBufferWriter byteBufferWriter;
		if (GameServer.bServer) {
			for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				byteBufferWriter = udpConnection.startPacket();
				byteBuffer.flip();
				byteBufferWriter.bb.put(byteBuffer);
				udpConnection.endPacketImmediate();
			}
		} else {
			if (GameClient.bClient) {
				throw new IllegalStateException("can\'t call this method on the client");
			}

			for (int1 = 0; int1 < SinglePlayerServer.udpEngine.connections.size(); ++int1) {
				zombie.spnetwork.UdpConnection udpConnection2 = (zombie.spnetwork.UdpConnection)SinglePlayerServer.udpEngine.connections.get(int1);
				byteBufferWriter = udpConnection2.startPacket();
				byteBuffer.flip();
				byteBufferWriter.bb.put(byteBuffer);
				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void writeServerCommand(String string, String string2, KahluaTable kahluaTable, ByteBufferWriter byteBufferWriter) {
		PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)1);
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putUTF(string2);
		if (kahluaTable != null && !kahluaTable.isEmpty()) {
			byteBufferWriter.putByte((byte)1);
			try {
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					if (!TableNetworkUtils.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
						Object object = kahluaTableIterator.getKey();
						DebugLog.log("ERROR: sendServerCommand: can\'t save key,value=" + object + "," + kahluaTableIterator.getValue());
					}
				}

				TableNetworkUtils.save(kahluaTable, byteBufferWriter.bb);
			} catch (IOException ioException) {
				ExceptionLogger.logException(ioException);
			}
		} else {
			byteBufferWriter.putByte((byte)0);
		}
	}

	public static void sendServerCommand(String string, String string2, KahluaTable kahluaTable) {
		BYTE_BUFFER.clear();
		writeServerCommand(string, string2, kahluaTable, BYTE_BUFFER_WRITER);
		sendPacket(BYTE_BUFFER);
	}

	public static void addGlobalObjectOnClient(SGlobalObject sGlobalObject) throws IOException {
		BYTE_BUFFER.clear();
		ByteBufferWriter byteBufferWriter = BYTE_BUFFER_WRITER;
		PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)3);
		byteBufferWriter.putUTF(sGlobalObject.system.name);
		byteBufferWriter.putInt(sGlobalObject.getX());
		byteBufferWriter.putInt(sGlobalObject.getY());
		byteBufferWriter.putByte((byte)sGlobalObject.getZ());
		SGlobalObjectSystem sGlobalObjectSystem = (SGlobalObjectSystem)sGlobalObject.system;
		TableNetworkUtils.saveSome(sGlobalObject.getModData(), byteBufferWriter.bb, sGlobalObjectSystem.objectSyncKeys);
		sendPacket(BYTE_BUFFER);
	}

	public static void removeGlobalObjectOnClient(GlobalObject globalObject) {
		BYTE_BUFFER.clear();
		ByteBufferWriter byteBufferWriter = BYTE_BUFFER_WRITER;
		PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)4);
		byteBufferWriter.putUTF(globalObject.system.name);
		byteBufferWriter.putInt(globalObject.getX());
		byteBufferWriter.putInt(globalObject.getY());
		byteBufferWriter.putByte((byte)globalObject.getZ());
		sendPacket(BYTE_BUFFER);
	}

	public static void updateGlobalObjectOnClient(SGlobalObject sGlobalObject) throws IOException {
		BYTE_BUFFER.clear();
		ByteBufferWriter byteBufferWriter = BYTE_BUFFER_WRITER;
		PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)5);
		byteBufferWriter.putUTF(sGlobalObject.system.name);
		byteBufferWriter.putInt(sGlobalObject.getX());
		byteBufferWriter.putInt(sGlobalObject.getY());
		byteBufferWriter.putByte((byte)sGlobalObject.getZ());
		SGlobalObjectSystem sGlobalObjectSystem = (SGlobalObjectSystem)sGlobalObject.system;
		TableNetworkUtils.saveSome(sGlobalObject.getModData(), byteBufferWriter.bb, sGlobalObjectSystem.objectSyncKeys);
		sendPacket(BYTE_BUFFER);
	}

	private static void receiveClientCommand(ByteBuffer byteBuffer, IsoPlayer player) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		boolean boolean1 = byteBuffer.get() == 1;
		KahluaTable kahluaTable = null;
		if (boolean1) {
			kahluaTable = LuaManager.platform.newTable();
			try {
				TableNetworkUtils.load(kahluaTable, byteBuffer);
			} catch (Exception exception) {
				exception.printStackTrace();
				return;
			}
		}

		SGlobalObjects.receiveClientCommand(string, string2, player, kahluaTable);
	}

	static  {
		BYTE_BUFFER_WRITER = new ByteBufferWriter(BYTE_BUFFER);
	}
}
