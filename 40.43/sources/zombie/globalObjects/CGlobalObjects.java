package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.network.TableNetworkUtils;


public final class CGlobalObjects {
	protected static final ArrayList systems = new ArrayList();
	protected static final HashMap initialState = new HashMap();
	public static final String PREFIX = "gos_";

	public static void noise(String string) {
		if (Core.bDebug) {
			DebugLog.log("CGlobalObjects: " + string);
		}
	}

	public static CGlobalObjectSystem registerSystem(String string) {
		CGlobalObjectSystem cGlobalObjectSystem = getSystemByName(string);
		if (cGlobalObjectSystem == null) {
			cGlobalObjectSystem = newSystem(string);
			KahluaTable kahluaTable = (KahluaTable)initialState.get(string);
			if (kahluaTable != null) {
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					cGlobalObjectSystem.modData.rawset(kahluaTableIterator.getKey(), kahluaTableIterator.getValue());
				}
			}
		}

		return cGlobalObjectSystem;
	}

	public static CGlobalObjectSystem newSystem(String string) throws IllegalStateException {
		if (getSystemByName(string) != null) {
			throw new IllegalStateException("system with that name already exists");
		} else {
			noise("newSystem " + string);
			CGlobalObjectSystem cGlobalObjectSystem = new CGlobalObjectSystem(string);
			systems.add(cGlobalObjectSystem);
			return cGlobalObjectSystem;
		}
	}

	public static int getSystemCount() {
		return systems.size();
	}

	public static CGlobalObjectSystem getSystemByIndex(int int1) {
		return int1 >= 0 && int1 < systems.size() ? (CGlobalObjectSystem)systems.get(int1) : null;
	}

	public static CGlobalObjectSystem getSystemByName(String string) {
		for (int int1 = 0; int1 < systems.size(); ++int1) {
			CGlobalObjectSystem cGlobalObjectSystem = (CGlobalObjectSystem)systems.get(int1);
			if (cGlobalObjectSystem.name.equals(string)) {
				return cGlobalObjectSystem;
			}
		}

		return null;
	}

	public static void initSystems() {
		LuaEventManager.triggerEvent("OnCGlobalObjectSystemInit");
	}

	public static void loadInitialState(ByteBuffer byteBuffer) throws IOException {
		byte byte1 = byteBuffer.get();
		for (int int1 = 0; int1 < byte1; ++int1) {
			String string = GameWindow.ReadStringUTF(byteBuffer);
			if (byteBuffer.get() != 0) {
				KahluaTable kahluaTable = LuaManager.platform.newTable();
				initialState.put(string, kahluaTable);
				TableNetworkUtils.load(kahluaTable, byteBuffer);
			}
		}
	}

	public static boolean receiveServerCommand(String string, String string2, KahluaTable kahluaTable) {
		if (!string.startsWith("gos_")) {
			return false;
		} else {
			noise("receiveServerCommand " + string + " " + string2);
			String string3 = string.substring(4);
			CGlobalObjectSystem cGlobalObjectSystem = getSystemByName(string3);
			if (cGlobalObjectSystem == null) {
				throw new IllegalStateException("system \'" + string3 + "\' not found");
			} else {
				cGlobalObjectSystem.receiveServerCommand(string2, kahluaTable);
				return true;
			}
		}
	}

	public static void Reset() {
		for (int int1 = 0; int1 < systems.size(); ++int1) {
			CGlobalObjectSystem cGlobalObjectSystem = (CGlobalObjectSystem)systems.get(int1);
			cGlobalObjectSystem.Reset();
		}

		systems.clear();
		initialState.clear();
	}
}
