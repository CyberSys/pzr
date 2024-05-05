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
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.TableNetworkUtils;
import zombie.spnetwork.SinglePlayerClient;


public final class CGlobalObjectNetwork {
	private static final ByteBuffer BYTE_BUFFER = ByteBuffer.allocate(1048576);
	private static final ByteBufferWriter BYTE_BUFFER_WRITER;
	private static KahluaTable tempTable;

	public static void receive(ByteBuffer byteBuffer) throws IOException {
		byte byte1 = byteBuffer.get();
		switch (byte1) {
		case 1: 
			receiveServerCommand(byteBuffer);
		
		case 2: 
		
		default: 
			break;
		
		case 3: 
			receiveNewLuaObjectAt(byteBuffer);
			break;
		
		case 4: 
			receiveRemoveLuaObjectAt(byteBuffer);
			break;
		
		case 5: 
			receiveUpdateLuaObjectAt(byteBuffer);
		
		}
	}

	private static void receiveServerCommand(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		boolean boolean1 = byteBuffer.get() == 1;
		KahluaTable kahluaTable = null;
		if (boolean1) {
			kahluaTable = LuaManager.platform.newTable();
			try {
				TableNetworkUtils.load(kahluaTable, byteBuffer);
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return;
			}
		}

		CGlobalObjects.receiveServerCommand(string, string2, kahluaTable);
	}

	private static void receiveNewLuaObjectAt(ByteBuffer byteBuffer) throws IOException {
		String string = GameWindow.ReadStringUTF(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		if (tempTable == null) {
			tempTable = LuaManager.platform.newTable();
		}

		TableNetworkUtils.load(tempTable, byteBuffer);
		CGlobalObjectSystem cGlobalObjectSystem = CGlobalObjects.getSystemByName(string);
		if (cGlobalObjectSystem != null) {
			cGlobalObjectSystem.receiveNewLuaObjectAt(int1, int2, byte1, tempTable);
		}
	}

	private static void receiveRemoveLuaObjectAt(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadStringUTF(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		CGlobalObjectSystem cGlobalObjectSystem = CGlobalObjects.getSystemByName(string);
		if (cGlobalObjectSystem != null) {
			cGlobalObjectSystem.receiveRemoveLuaObjectAt(int1, int2, byte1);
		}
	}

	private static void receiveUpdateLuaObjectAt(ByteBuffer byteBuffer) throws IOException {
		String string = GameWindow.ReadStringUTF(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		if (tempTable == null) {
			tempTable = LuaManager.platform.newTable();
		}

		TableNetworkUtils.load(tempTable, byteBuffer);
		CGlobalObjectSystem cGlobalObjectSystem = CGlobalObjects.getSystemByName(string);
		if (cGlobalObjectSystem != null) {
			cGlobalObjectSystem.receiveUpdateLuaObjectAt(int1, int2, byte1, tempTable);
		}
	}

	private static void sendPacket(ByteBuffer byteBuffer) {
		if (GameServer.bServer) {
			throw new IllegalStateException("can\'t call this method on the server");
		} else {
			ByteBufferWriter byteBufferWriter;
			if (GameClient.bClient) {
				byteBufferWriter = GameClient.connection.startPacket();
				byteBuffer.flip();
				byteBufferWriter.bb.put(byteBuffer);
				PacketTypes.PacketType.GlobalObjects.send(GameClient.connection);
			} else {
				byteBufferWriter = SinglePlayerClient.connection.startPacket();
				byteBuffer.flip();
				byteBufferWriter.bb.put(byteBuffer);
				SinglePlayerClient.connection.endPacketImmediate();
			}
		}
	}

	public static void sendClientCommand(IsoPlayer player, String string, String string2, KahluaTable kahluaTable) {
		BYTE_BUFFER.clear();
		writeClientCommand(player, string, string2, kahluaTable, BYTE_BUFFER_WRITER);
		sendPacket(BYTE_BUFFER);
	}

	private static void writeClientCommand(IsoPlayer player, String string, String string2, KahluaTable kahluaTable, ByteBufferWriter byteBufferWriter) {
		PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)(player != null ? player.PlayerIndex : -1));
		byteBufferWriter.putByte((byte)2);
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putUTF(string2);
		if (kahluaTable != null && !kahluaTable.isEmpty()) {
			byteBufferWriter.putByte((byte)1);
			try {
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					if (!TableNetworkUtils.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
						Object object = kahluaTableIterator.getKey();
						DebugLog.log("ERROR: sendClientCommand: can\'t save key,value=" + object + "," + kahluaTableIterator.getValue());
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

	public static void Reset() {
		if (tempTable != null) {
			tempTable.wipe();
			tempTable = null;
		}
	}

	static  {
		BYTE_BUFFER_WRITER = new ByteBufferWriter(BYTE_BUFFER);
	}
}
