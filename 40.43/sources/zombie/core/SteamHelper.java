package zombie.core;


public class SteamHelper {

	public static String getSteamInstallDirectory() {
		return WinReqistry.getSteamDirectory();
	}
}
