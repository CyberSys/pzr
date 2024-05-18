package zombie.spnetwork;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.globalObjects.SGlobalObjects;
import zombie.iso.IsoObject;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.TableNetworkUtils;
import zombie.vehicles.BaseVehicle;


public final class SinglePlayerServer {
	private static final ArrayList MainLoopNetData = new ArrayList();
	public static final SinglePlayerServer.UdpEngineServer udpEngine = new SinglePlayerServer.UdpEngineServer();

	public static void addIncoming(short short1, ByteBuffer byteBuffer, UdpConnection udpConnection) {
		ZomboidNetData zomboidNetData;
		if (byteBuffer.remaining() > 2048) {
			zomboidNetData = ZomboidNetDataPool.instance.getLong(byteBuffer.remaining());
		} else {
			zomboidNetData = ZomboidNetDataPool.instance.get();
		}

		zomboidNetData.read(short1, byteBuffer, udpConnection);
		synchronized (MainLoopNetData) {
			MainLoopNetData.add(zomboidNetData);
		}
	}

	private static void sendObjectChange(IsoObject object, String string, KahluaTable kahluaTable, UdpConnection udpConnection) {
		if (object.getSquare() != null) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)59, byteBufferWriter);
			if (object instanceof IsoPlayer) {
				byteBufferWriter.putByte((byte)1);
				byteBufferWriter.putInt(((IsoPlayer)object).OnlineID);
			} else if (object instanceof BaseVehicle) {
				byteBufferWriter.putByte((byte)2);
				byteBufferWriter.putShort(((BaseVehicle)object).getId());
			} else {
				byteBufferWriter.putByte((byte)0);
				byteBufferWriter.putInt(object.getSquare().getX());
				byteBufferWriter.putInt(object.getSquare().getY());
				byteBufferWriter.putInt(object.getSquare().getZ());
				byteBufferWriter.putInt(object.getSquare().getObjects().indexOf(object));
			}

			byteBufferWriter.putUTF(string);
			object.saveChange(string, kahluaTable, byteBufferWriter.bb);
			udpConnection.endPacketImmediate();
		}
	}

	public static void sendObjectChange(IsoObject object, String string, KahluaTable kahluaTable) {
		if (object != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection.ReleventTo(object.getX(), object.getY())) {
					sendObjectChange(object, string, kahluaTable, udpConnection);
				}
			}
		}
	}

	public static void sendObjectChange(IsoObject object, String string, Object[] objectArray) {
		if (objectArray.length == 0) {
			sendObjectChange(object, string, (KahluaTable)null);
		} else if (objectArray.length % 2 == 0) {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			for (int int1 = 0; int1 < objectArray.length; int1 += 2) {
				Object object2 = objectArray[int1 + 1];
				if (object2 instanceof Float) {
					kahluaTable.rawset(objectArray[int1], ((Float)object2).doubleValue());
				} else if (object2 instanceof Integer) {
					kahluaTable.rawset(objectArray[int1], ((Integer)object2).doubleValue());
				} else if (object2 instanceof Short) {
					kahluaTable.rawset(objectArray[int1], ((Short)object2).doubleValue());
				} else {
					kahluaTable.rawset(objectArray[int1], object2);
				}
			}

			sendObjectChange(object, string, kahluaTable);
		}
	}

	public static void sendServerCommand(String string, String string2, KahluaTable kahluaTable, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)57, byteBufferWriter);
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putUTF(string2);
		if (kahluaTable != null && !kahluaTable.isEmpty()) {
			byteBufferWriter.putByte((byte)1);
			try {
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					if (!TableNetworkUtils.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
						DebugLog.log("ERROR: sendServerCommand: can\'t save key,value=" + kahluaTableIterator.getKey() + "," + kahluaTableIterator.getValue());
					}
				}

				TableNetworkUtils.save(kahluaTable, byteBufferWriter.bb);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else {
			byteBufferWriter.putByte((byte)0);
		}

		udpConnection.endPacketImmediate();
	}

	public static void sendServerCommand(String string, String string2, KahluaTable kahluaTable) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			sendServerCommand(string, string2, kahluaTable, udpConnection);
		}
	}

	public static void update() {
		if (!GameClient.bClient) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] != null) {
					IsoPlayer.players[int1].OnlineID = int1;
				}
			}

			synchronized (MainLoopNetData) {
				for (int int2 = 0; int2 < MainLoopNetData.size(); ++int2) {
					ZomboidNetData zomboidNetData = (ZomboidNetData)MainLoopNetData.get(int2);
					mainLoopDealWithNetData(zomboidNetData);
					MainLoopNetData.remove(int2--);
				}
			}
		}
	}

	private static void mainLoopDealWithNetData(ZomboidNetData zomboidNetData) {
		ByteBuffer byteBuffer = zomboidNetData.buffer;
		try {
			switch (zomboidNetData.type) {
			case 57: 
				receiveClientCommand(byteBuffer, zomboidNetData.connection);
			
			}
		} finally {
			ZomboidNetDataPool.instance.discard(zomboidNetData);
		}
	}

	private static IsoPlayer getAnyPlayerFromConnection(UdpConnection udpConnection) {
		for (int int1 = 0; int1 < 4; ++int1) {
			if (udpConnection.players[int1] != null) {
				return udpConnection.players[int1];
			}
		}

		return null;
	}

	private static IsoPlayer getPlayerFromConnection(UdpConnection udpConnection, int int1) {
		return int1 >= 0 && int1 < 4 ? udpConnection.players[int1] : null;
	}

	private static void receiveClientCommand(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
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

		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (byte1 == -1) {
			player = getAnyPlayerFromConnection(udpConnection);
		}

		if (player == null) {
			DebugLog.log("receiveClientCommand: player is null");
		} else if (!SGlobalObjects.receiveClientCommand(string, string2, player, kahluaTable)) {
			LuaEventManager.triggerEvent("OnClientCommand", string, string2, player, kahluaTable);
		}
	}

	public static void Reset() {
		Iterator iterator = MainLoopNetData.iterator();
		while (iterator.hasNext()) {
			ZomboidNetData zomboidNetData = (ZomboidNetData)iterator.next();
			ZomboidNetDataPool.instance.discard(zomboidNetData);
		}

		MainLoopNetData.clear();
	}

	public static final class UdpEngineServer extends UdpEngine {
		public final ArrayList connections = new ArrayList();

		UdpEngineServer() {
			this.connections.add(new UdpConnection(this));
		}

		public void Send(ByteBuffer byteBuffer) {
			SinglePlayerClient.udpEngine.Receive(byteBuffer);
		}

		public void Receive(ByteBuffer byteBuffer) {
			int int1 = byteBuffer.get() & 255;
			short short1 = byteBuffer.getShort();
			SinglePlayerServer.addIncoming(short1, byteBuffer, (UdpConnection)SinglePlayerServer.udpEngine.connections.get(0));
		}
	}
}
