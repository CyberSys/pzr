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
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.globalObjects.CGlobalObjects;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.TableNetworkUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;


public class SinglePlayerClient {
	private static final ArrayList MainLoopNetData = new ArrayList();
	public static final UdpEngine udpEngine = new SinglePlayerClient.UdpEngineClient();
	public static final UdpConnection connection;

	public static void addIncoming(short short1, ByteBuffer byteBuffer) {
		ZomboidNetData zomboidNetData;
		if (byteBuffer.remaining() > 2048) {
			zomboidNetData = ZomboidNetDataPool.instance.getLong(byteBuffer.remaining());
		} else {
			zomboidNetData = ZomboidNetDataPool.instance.get();
		}

		zomboidNetData.read(short1, byteBuffer, connection);
		synchronized (MainLoopNetData) {
			MainLoopNetData.add(zomboidNetData);
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
				receiveServerCommand(byteBuffer);
				break;
			
			case 59: 
				receiveObjectChange(byteBuffer);
			
			}
		} finally {
			ZomboidNetDataPool.instance.discard(zomboidNetData);
		}
	}

	private static void delayPacket(int int1, int int2, int int3) {
	}

	private static IsoPlayer getPlayerByID(int int1) {
		return IsoPlayer.players[int1];
	}

	private static void receiveObjectChange(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		int int1;
		String string;
		if (byte1 == 1) {
			int1 = byteBuffer.getInt();
			string = GameWindow.ReadString(byteBuffer);
			if (Core.bDebug) {
				DebugLog.log("receiveObjectChange " + string);
			}

			IsoPlayer player = getPlayerByID(int1);
			if (player != null) {
				player.loadChange(string, byteBuffer);
			}
		} else if (byte1 == 2) {
			short short1 = byteBuffer.getShort();
			string = GameWindow.ReadString(byteBuffer);
			if (Core.bDebug) {
				DebugLog.log("receiveObjectChange " + string);
			}

			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short1);
			if (baseVehicle != null) {
				baseVehicle.loadChange(string, byteBuffer);
			} else if (Core.bDebug) {
				DebugLog.log("receiveObjectChange: unknown vehicle id=" + short1);
			}
		} else {
			int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			int int4 = byteBuffer.getInt();
			String string2 = GameWindow.ReadString(byteBuffer);
			if (Core.bDebug) {
				DebugLog.log("receiveObjectChange " + string2);
			}

			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square == null) {
				delayPacket(int1, int2, int3);
				return;
			}

			if (square != null && int4 >= 0 && int4 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(int4);
				object.loadChange(string2, byteBuffer);
			} else if (square != null) {
				if (Core.bDebug) {
					DebugLog.log("receiveObjectChange: index=" + int4 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
				}
			} else if (Core.bDebug) {
				DebugLog.log("receiveObjectChange: sq is null x,y,z=" + int1 + "," + int2 + "," + int3);
			}
		}
	}

	public static void sendClientCommand(IsoPlayer player, String string, String string2, KahluaTable kahluaTable) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)57, byteBufferWriter);
		byteBufferWriter.putByte((byte)(player != null ? player.PlayerIndex : -1));
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putUTF(string2);
		if (kahluaTable != null && !kahluaTable.isEmpty()) {
			byteBufferWriter.putByte((byte)1);
			try {
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					if (!TableNetworkUtils.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
						DebugLog.log("ERROR: sendClientCommand: can\'t save key,value=" + kahluaTableIterator.getKey() + "," + kahluaTableIterator.getValue());
					}
				}

				TableNetworkUtils.save(kahluaTable, byteBufferWriter.bb);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else {
			byteBufferWriter.putByte((byte)0);
		}

		connection.endPacketImmediate();
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
				exception.printStackTrace();
				return;
			}
		}

		if (!CGlobalObjects.receiveServerCommand(string, string2, kahluaTable)) {
			LuaEventManager.triggerEvent("OnServerCommand", string, string2, kahluaTable);
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

	static  {
		connection = new UdpConnection(udpEngine);
	}

	private static final class UdpEngineClient extends UdpEngine {

		private UdpEngineClient() {
		}

		public void Send(ByteBuffer byteBuffer) {
			SinglePlayerServer.udpEngine.Receive(byteBuffer);
		}

		public void Receive(ByteBuffer byteBuffer) {
			int int1 = byteBuffer.get() & 255;
			short short1 = byteBuffer.getShort();
			SinglePlayerClient.addIncoming(short1, byteBuffer);
		}

		UdpEngineClient(Object object) {
			this();
		}
	}
}
