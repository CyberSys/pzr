package zombie.debug;



public enum DebugType {

	NetworkPacketDebug,
	NetworkFileDebug,
	Network,
	General,
	Lua,
	Sound,
	Zombie,
	Combat,
	Objects,
	Fireplace,
	Radio,
	MapLoading;

	public static boolean Do(DebugType debugType) {
		return DebugLog.Types.contains(debugType);
	}
}
