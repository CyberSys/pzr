package zombie.core.znet;


public class SteamGameServer {
	public static int STEAM_SERVERS_DISCONNECTED = 0;
	public static int STEAM_SERVERS_CONNECTED = 1;
	public static int STEAM_SERVERS_CONNECTFAILURE = 2;

	public static native boolean Init(String string, int int1, int int2, int int3, String string2);

	public static native void SetProduct(String string);

	public static native void SetGameDescription(String string);

	public static native void SetModDir(String string);

	public static native void SetDedicatedServer(boolean boolean1);

	public static native void LogOnAnonymous();

	public static native void EnableHeartBeats(boolean boolean1);

	public static native void SetMaxPlayerCount(int int1);

	public static native void SetServerName(String string);

	public static native void SetMapName(String string);

	public static native void SetKeyValue(String string, String string2);

	public static native void SetGameTags(String string);

	public static native void SetRegion(String string);

	public static native boolean BUpdateUserData(long long1, String string, int int1);

	public static native int GetSteamServersConnectState();

	public static native long GetSteamID();
}
