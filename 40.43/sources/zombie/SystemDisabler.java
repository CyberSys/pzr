package zombie;


public class SystemDisabler {
	public static boolean doCharacterStats = true;
	public static boolean doZombieCreation = true;
	public static boolean doSurvivorCreation = false;
	public static boolean doPlayerCreation = true;
	public static boolean doOverridePOVCharacters = true;
	public static boolean doVehiclesEverywhere = false;
	public static boolean doWorldSyncEnable = false;
	public static boolean doObjectStateSyncEnable = false;
	private static boolean doAllowDebugConnections = false;
	private static boolean doOverrideServerConnectDebugCheck = false;

	public static void setDoCharacterStats(boolean boolean1) {
		doCharacterStats = boolean1;
	}

	public static void setDoZombieCreation(boolean boolean1) {
		doZombieCreation = boolean1;
	}

	public static void setDoSurvivorCreation(boolean boolean1) {
		doSurvivorCreation = boolean1;
	}

	public static void setDoPlayerCreation(boolean boolean1) {
		doPlayerCreation = boolean1;
	}

	public static void setOverridePOVCharacters(boolean boolean1) {
		doOverridePOVCharacters = boolean1;
	}

	public static void setVehiclesEverywhere(boolean boolean1) {
		doVehiclesEverywhere = boolean1;
	}

	public static void setWorldSyncEnable(boolean boolean1) {
		doWorldSyncEnable = boolean1;
	}

	public static void setObjectStateSyncEnable(boolean boolean1) {
		doObjectStateSyncEnable = boolean1;
	}

	public static boolean getAllowDebugConnections() {
		return doAllowDebugConnections;
	}

	public static boolean getOverrideServerConnectDebugCheck() {
		return doOverrideServerConnectDebugCheck;
	}

	public static void Reset() {
		doCharacterStats = true;
		doZombieCreation = true;
		doSurvivorCreation = false;
		doPlayerCreation = true;
		doOverridePOVCharacters = true;
		doVehiclesEverywhere = false;
		doAllowDebugConnections = false;
		doWorldSyncEnable = false;
		doObjectStateSyncEnable = false;
	}
}
