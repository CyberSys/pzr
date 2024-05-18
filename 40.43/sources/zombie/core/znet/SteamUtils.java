package zombie.core.znet;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import zombie.Lua.LuaEventManager;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.network.CoopSlave;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;


public class SteamUtils {
	private static boolean m_steamEnabled;
	private static boolean m_netEnabled;
	private static final BigInteger TWO_64;
	private static final BigInteger MAX_ULONG;
	private static List m_joinRequestCallbacks;

	private static void loadLibrary(String string) {
		DebugLog.log("Loading " + string + "...");
		System.loadLibrary(string);
	}

	public static void init() {
		m_steamEnabled = System.getProperty("zomboid.steam") != null && System.getProperty("zomboid.steam").equals("1");
		DebugLog.log("Loading networking libraries...");
		String string = "";
		if ("1".equals(System.getProperty("zomboid.debuglibs"))) {
			DebugLog.log("***** Loading debug versions of libraries");
			string = "d";
		}

		try {
			if (System.getProperty("os.name").contains("OS X")) {
				if (m_steamEnabled) {
					loadLibrary("steam_api");
					loadLibrary("RakNet");
					loadLibrary("ZNetJNI");
				} else {
					loadLibrary("RakNet");
					loadLibrary("ZNetNoSteam");
				}
			} else if (System.getProperty("os.name").startsWith("Win")) {
				if (System.getProperty("sun.arch.data.model").equals("64")) {
					if (m_steamEnabled) {
						loadLibrary("steam_api64");
						loadLibrary("RakNet64" + string);
						loadLibrary("ZNetJNI64" + string);
					} else {
						loadLibrary("RakNet64" + string);
						loadLibrary("ZNetNoSteam64" + string);
					}
				} else if (m_steamEnabled) {
					loadLibrary("steam_api");
					loadLibrary("RakNet32" + string);
					loadLibrary("ZNetJNI32" + string);
				} else {
					loadLibrary("RakNet32" + string);
					loadLibrary("ZNetNoSteam32" + string);
				}
			} else if (System.getProperty("sun.arch.data.model").equals("64")) {
				if (m_steamEnabled) {
					loadLibrary("steam_api");
					loadLibrary("RakNet64");
					loadLibrary("ZNetJNI64");
				} else {
					loadLibrary("RakNet64");
					loadLibrary("ZNetNoSteam64");
				}
			} else if (m_steamEnabled) {
				loadLibrary("steam_api");
				loadLibrary("RakNet32");
				loadLibrary("ZNetJNI32");
			} else {
				loadLibrary("RakNet32");
				loadLibrary("ZNetNoSteam32");
			}

			m_netEnabled = true;
		} catch (UnsatisfiedLinkError unsatisfiedLinkError) {
			m_steamEnabled = false;
			m_netEnabled = false;
			ExceptionLogger.logException(unsatisfiedLinkError);
			if (System.getProperty("os.name").startsWith("Win")) {
				DebugLog.log("One of the game\'s DLLs could not be loaded.");
				DebugLog.log("  Your system may be missing a DLL needed by the game\'s DLL.");
				DebugLog.log("  You may need to install the Microsoft Visual C++ Redistributable 2013.");
				File file = new File("../_CommonRedist/vcredist/");
				if (file.exists()) {
					DebugLog.log("  This file is provided in " + file.getAbsolutePath());
				}
			}
		}

		String string2 = System.getProperty("zomboid.znetlog");
		if (m_netEnabled && string2 != null) {
			try {
				int int1 = Integer.parseInt(string2);
				ZNet.setLogLevel(int1);
			} catch (NumberFormatException numberFormatException) {
				ExceptionLogger.logException(numberFormatException);
			}
		}

		if (!m_netEnabled) {
			DebugLog.log("Failed to load networking libraries");
		} else {
			ZNet.init();
			if (!m_steamEnabled) {
				DebugLog.log("SteamUtils started without Steam");
			} else if (n_Init(GameServer.bServer)) {
				DebugLog.log("SteamUtils initialised successfully");
			} else {
				DebugLog.log("Could not initialise SteamUtils");
				m_steamEnabled = false;
			}
		}

		m_joinRequestCallbacks = new ArrayList();
	}

	public static void shutdown() {
		if (m_steamEnabled) {
			n_Shutdown();
		}
	}

	public static void runLoop() {
		if (m_steamEnabled) {
			n_RunLoop();
		}
	}

	public static boolean isSteamModeEnabled() {
		return m_steamEnabled;
	}

	public static boolean isOverlayEnabled() {
		return m_steamEnabled && n_IsOverlayEnabled();
	}

	public static String convertSteamIDToString(long long1) {
		BigInteger bigInteger = BigInteger.valueOf(long1);
		if (bigInteger.signum() < 0) {
			bigInteger.add(TWO_64);
		}

		return bigInteger.toString();
	}

	public static boolean isValidSteamID(String string) {
		try {
			BigInteger bigInteger = new BigInteger(string);
			return bigInteger.signum() >= 0 && bigInteger.compareTo(MAX_ULONG) <= 0;
		} catch (NumberFormatException numberFormatException) {
			return false;
		}
	}

	public static long convertStringToSteamID(String string) {
		try {
			BigInteger bigInteger = new BigInteger(string);
			return bigInteger.signum() >= 0 && bigInteger.compareTo(MAX_ULONG) <= 0 ? bigInteger.longValue() : -1L;
		} catch (NumberFormatException numberFormatException) {
			return -1L;
		}
	}

	public static void addJoinRequestCallback(IJoinRequestCallback iJoinRequestCallback) {
		m_joinRequestCallbacks.add(iJoinRequestCallback);
	}

	public static void removeJoinRequestCallback(IJoinRequestCallback iJoinRequestCallback) {
		m_joinRequestCallbacks.remove(iJoinRequestCallback);
	}

	private static native boolean n_Init(boolean boolean1);

	private static native void n_Shutdown();

	private static native void n_RunLoop();

	private static native boolean n_IsOverlayEnabled();

	private static void joinRequestCallback(long long1, String string) {
		DebugLog.log("Got Join Request");
		Iterator iterator = m_joinRequestCallbacks.iterator();
		while (iterator.hasNext()) {
			IJoinRequestCallback iJoinRequestCallback = (IJoinRequestCallback)iterator.next();
			iJoinRequestCallback.onJoinRequest(long1, string);
		}

		if (string.contains("+connect ")) {
			String string2 = string.substring(9);
			System.setProperty("args.server.connect", string2);
			LuaEventManager.triggerEvent("OnSteamGameJoin");
		}
	}

	private static int clientInitiateConnectionCallback(long long1) {
		if (CoopSlave.instance == null) {
			ServerWorldDatabase.LogonResult logonResult = ServerWorldDatabase.instance.authClient(long1);
			return logonResult.bAuthorized ? 0 : 1;
		} else {
			return !CoopSlave.instance.isHost(long1) && !CoopSlave.instance.isInvited(long1) ? 2 : 0;
		}
	}

	private static int validateOwnerCallback(long long1, long long2) {
		if (CoopSlave.instance != null) {
			return 0;
		} else {
			ServerWorldDatabase.LogonResult logonResult = ServerWorldDatabase.instance.authOwner(long1, long2);
			return logonResult.bAuthorized ? 0 : 1;
		}
	}

	static  {
		TWO_64 = BigInteger.ONE.shiftLeft(64);
		MAX_ULONG = new BigInteger("FFFFFFFFFFFFFFFF", 16);
	}
}
