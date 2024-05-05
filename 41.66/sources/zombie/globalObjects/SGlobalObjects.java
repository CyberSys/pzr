package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.IsoObject;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.TableNetworkUtils;


public final class SGlobalObjects {
	protected static final ArrayList systems = new ArrayList();

	public static void noise(String string) {
		if (Core.bDebug) {
			DebugLog.log("SGlobalObjects: " + string);
		}
	}

	public static SGlobalObjectSystem registerSystem(String string) {
		SGlobalObjectSystem sGlobalObjectSystem = getSystemByName(string);
		if (sGlobalObjectSystem == null) {
			sGlobalObjectSystem = newSystem(string);
			sGlobalObjectSystem.load();
		}

		return sGlobalObjectSystem;
	}

	public static SGlobalObjectSystem newSystem(String string) throws IllegalStateException {
		if (getSystemByName(string) != null) {
			throw new IllegalStateException("system with that name already exists");
		} else {
			noise("newSystem " + string);
			SGlobalObjectSystem sGlobalObjectSystem = new SGlobalObjectSystem(string);
			systems.add(sGlobalObjectSystem);
			return sGlobalObjectSystem;
		}
	}

	public static int getSystemCount() {
		return systems.size();
	}

	public static SGlobalObjectSystem getSystemByIndex(int int1) {
		return int1 >= 0 && int1 < systems.size() ? (SGlobalObjectSystem)systems.get(int1) : null;
	}

	public static SGlobalObjectSystem getSystemByName(String string) {
		for (int int1 = 0; int1 < systems.size(); ++int1) {
			SGlobalObjectSystem sGlobalObjectSystem = (SGlobalObjectSystem)systems.get(int1);
			if (sGlobalObjectSystem.name.equals(string)) {
				return sGlobalObjectSystem;
			}
		}

		return null;
	}

	public static void update() {
		for (int int1 = 0; int1 < systems.size(); ++int1) {
			SGlobalObjectSystem sGlobalObjectSystem = (SGlobalObjectSystem)systems.get(int1);
			sGlobalObjectSystem.update();
		}
	}

	public static void chunkLoaded(int int1, int int2) {
		for (int int3 = 0; int3 < systems.size(); ++int3) {
			SGlobalObjectSystem sGlobalObjectSystem = (SGlobalObjectSystem)systems.get(int3);
			sGlobalObjectSystem.chunkLoaded(int1, int2);
		}
	}

	public static void initSystems() {
		if (!GameClient.bClient) {
			LuaEventManager.triggerEvent("OnSGlobalObjectSystemInit");
			if (!GameServer.bServer) {
				try {
					synchronized (SliceY.SliceBufferLock) {
						SliceY.SliceBuffer.clear();
						saveInitialStateForClient(SliceY.SliceBuffer);
						SliceY.SliceBuffer.flip();
						CGlobalObjects.loadInitialState(SliceY.SliceBuffer);
					}
				} catch (Throwable throwable) {
					ExceptionLogger.logException(throwable);
				}
			}
		}
	}

	public static void saveInitialStateForClient(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)systems.size());
		for (int int1 = 0; int1 < systems.size(); ++int1) {
			SGlobalObjectSystem sGlobalObjectSystem = (SGlobalObjectSystem)systems.get(int1);
			GameWindow.WriteStringUTF(byteBuffer, sGlobalObjectSystem.name);
			KahluaTable kahluaTable = sGlobalObjectSystem.getInitialStateForClient();
			if (kahluaTable == null) {
				kahluaTable = LuaManager.platform.newTable();
			}

			KahluaTable kahluaTable2 = LuaManager.platform.newTable();
			kahluaTable.rawset("_objects", kahluaTable2);
			for (int int2 = 0; int2 < sGlobalObjectSystem.getObjectCount(); ++int2) {
				GlobalObject globalObject = sGlobalObjectSystem.getObjectByIndex(int2);
				KahluaTable kahluaTable3 = LuaManager.platform.newTable();
				kahluaTable3.rawset("x", BoxedStaticValues.toDouble((double)globalObject.getX()));
				kahluaTable3.rawset("y", BoxedStaticValues.toDouble((double)globalObject.getY()));
				kahluaTable3.rawset("z", BoxedStaticValues.toDouble((double)globalObject.getZ()));
				Iterator iterator = sGlobalObjectSystem.objectSyncKeys.iterator();
				while (iterator.hasNext()) {
					String string = (String)iterator.next();
					kahluaTable3.rawset(string, globalObject.getModData().rawget(string));
				}

				kahluaTable2.rawset(int2 + 1, kahluaTable3);
			}

			if (kahluaTable != null && !kahluaTable.isEmpty()) {
				byteBuffer.put((byte)1);
				TableNetworkUtils.save(kahluaTable, byteBuffer);
			} else {
				byteBuffer.put((byte)0);
			}
		}
	}

	public static boolean receiveClientCommand(String string, String string2, IsoPlayer player, KahluaTable kahluaTable) {
		noise("receiveClientCommand " + string + " " + string2 + " OnlineID=" + player.getOnlineID());
		SGlobalObjectSystem sGlobalObjectSystem = getSystemByName(string);
		if (sGlobalObjectSystem == null) {
			throw new IllegalStateException("system \'" + string + "\' not found");
		} else {
			sGlobalObjectSystem.receiveClientCommand(string2, player, kahluaTable);
			return true;
		}
	}

	public static void load() {
	}

	public static void save() {
		for (int int1 = 0; int1 < systems.size(); ++int1) {
			SGlobalObjectSystem sGlobalObjectSystem = (SGlobalObjectSystem)systems.get(int1);
			sGlobalObjectSystem.save();
		}
	}

	public static void OnIsoObjectChangedItself(String string, IsoObject object) {
		if (!GameClient.bClient) {
			SGlobalObjectSystem sGlobalObjectSystem = getSystemByName(string);
			if (sGlobalObjectSystem != null) {
				sGlobalObjectSystem.OnIsoObjectChangedItself(object);
			}
		}
	}

	public static void Reset() {
		for (int int1 = 0; int1 < systems.size(); ++int1) {
			SGlobalObjectSystem sGlobalObjectSystem = (SGlobalObjectSystem)systems.get(int1);
			sGlobalObjectSystem.Reset();
		}

		systems.clear();
		GlobalObjectLookup.Reset();
	}
}
