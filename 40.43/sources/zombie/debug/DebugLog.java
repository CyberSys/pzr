package zombie.debug;

import java.util.HashSet;


public class DebugLog {
	public static HashSet Types = new HashSet();

	public static void log(String string) {
		long long1 = System.currentTimeMillis();
		log(DebugType.General, long1 + " " + string);
	}

	public static void log(DebugType debugType, String string) {
		if (Types.contains(debugType)) {
			if (string != null && string.trim().length() != 0) {
				System.out.println(string);
			}
		}
	}

	public static void enableLog(DebugType debugType, boolean boolean1) {
		if (boolean1) {
			Types.add(debugType);
		} else {
			Types.remove(debugType);
		}
	}

	public static void log(Object object) {
		if (object != null) {
			log(object.toString());
		}
	}

	static  {
		Types.add(DebugType.General);
		Types.add(DebugType.Lua);
	}
}
