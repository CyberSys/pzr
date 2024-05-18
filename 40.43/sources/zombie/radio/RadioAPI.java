package zombie.radio;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;


public class RadioAPI {
	private static RadioAPI instance;

	public static int timeToTimeStamp(int int1, int int2, int int3) {
		return int1 * 24 + int2 * 60 + int3;
	}

	public static int timeStampToDays(int int1) {
		return int1 / 1440;
	}

	public static int timeStampToHours(int int1) {
		return int1 / 60 % 24;
	}

	public static int timeStampToMinutes(int int1) {
		return int1 % 60;
	}

	public static boolean hasInstance() {
		return instance != null;
	}

	public static RadioAPI getInstance() {
		if (instance == null) {
			instance = new RadioAPI();
		}

		return instance;
	}

	private RadioAPI() {
	}

	public KahluaTable getChannels(String string) {
		Map map = ZomboidRadio.getInstance().GetChannelList(string);
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		if (map != null) {
			Iterator iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				kahluaTable.rawset(entry.getKey(), entry.getValue());
			}
		}

		return kahluaTable;
	}
}
