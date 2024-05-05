package zombie.core.znet;

import java.util.ArrayList;
import java.util.List;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.network.Server;


public class ServerBrowser {
	private static IServerBrowserCallback m_callbackInterface = null;

	public static boolean init() {
		boolean boolean1 = false;
		if (SteamUtils.isSteamModeEnabled()) {
			boolean1 = n_Init();
		}

		return boolean1;
	}

	public static void shutdown() {
		if (SteamUtils.isSteamModeEnabled()) {
			n_Shutdown();
		}
	}

	public static void RefreshInternetServers() {
		if (SteamUtils.isSteamModeEnabled()) {
			n_RefreshInternetServers();
		}
	}

	public static int GetServerCount() {
		int int1 = 0;
		if (SteamUtils.isSteamModeEnabled()) {
			int1 = n_GetServerCount();
		}

		return int1;
	}

	public static GameServerDetails GetServerDetails(int int1) {
		GameServerDetails gameServerDetails = null;
		if (SteamUtils.isSteamModeEnabled()) {
			gameServerDetails = n_GetServerDetails(int1);
		}

		return gameServerDetails;
	}

	public static void Release() {
		if (SteamUtils.isSteamModeEnabled()) {
			n_Release();
		}
	}

	public static boolean IsRefreshing() {
		boolean boolean1 = false;
		if (SteamUtils.isSteamModeEnabled()) {
			boolean1 = n_IsRefreshing();
		}

		return boolean1;
	}

	public static boolean QueryServer(String string, int int1) {
		boolean boolean1 = false;
		if (SteamUtils.isSteamModeEnabled()) {
			boolean1 = n_QueryServer(string, int1);
		}

		return boolean1;
	}

	public static GameServerDetails GetServerDetails(String string, int int1) {
		GameServerDetails gameServerDetails = null;
		if (SteamUtils.isSteamModeEnabled()) {
			gameServerDetails = n_GetServerDetails(string, int1);
		}

		return gameServerDetails;
	}

	public static void ReleaseServerQuery(String string, int int1) {
		if (SteamUtils.isSteamModeEnabled()) {
			n_ReleaseServerQuery(string, int1);
		}
	}

	public static List GetServerList() {
		ArrayList arrayList = new ArrayList();
		if (SteamUtils.isSteamModeEnabled()) {
			while (true) {
				try {
					if (IsRefreshing()) {
						Thread.sleep(100L);
						SteamUtils.runLoop();
						continue;
					}
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}

				for (int int1 = 0; int1 < GetServerCount(); ++int1) {
					GameServerDetails gameServerDetails = GetServerDetails(int1);
					if (gameServerDetails.steamId != 0L) {
						arrayList.add(gameServerDetails);
					}
				}

				return arrayList;
			}
		} else {
			return arrayList;
		}
	}

	public static GameServerDetails GetServerDetailsSync(String string, int int1) {
		GameServerDetails gameServerDetails = null;
		if (SteamUtils.isSteamModeEnabled()) {
			gameServerDetails = GetServerDetails(string, int1);
			if (gameServerDetails == null) {
				QueryServer(string, int1);
				try {
					while (gameServerDetails == null) {
						Thread.sleep(100L);
						SteamUtils.runLoop();
						gameServerDetails = GetServerDetails(string, int1);
					}
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}
		}

		return gameServerDetails;
	}

	public static boolean RequestServerRules(String string, int int1) {
		return n_RequestServerRules(string, int1);
	}

	public static void setCallbackInterface(IServerBrowserCallback iServerBrowserCallback) {
		m_callbackInterface = iServerBrowserCallback;
	}

	private static native boolean n_Init();

	private static native void n_Shutdown();

	private static native void n_RefreshInternetServers();

	private static native int n_GetServerCount();

	private static native GameServerDetails n_GetServerDetails(int int1);

	private static native void n_Release();

	private static native boolean n_IsRefreshing();

	private static native boolean n_QueryServer(String string, int int1);

	private static native GameServerDetails n_GetServerDetails(String string, int int1);

	private static native void n_ReleaseServerQuery(String string, int int1);

	private static native boolean n_RequestServerRules(String string, int int1);

	private static void onServerRespondedCallback(int int1) {
		if (m_callbackInterface != null) {
			m_callbackInterface.OnServerResponded(int1);
		}

		LuaEventManager.triggerEvent("OnSteamServerResponded", int1);
	}

	private static void onServerFailedToRespondCallback(int int1) {
		if (m_callbackInterface != null) {
			m_callbackInterface.OnServerFailedToRespond(int1);
		}
	}

	private static void onRefreshCompleteCallback() {
		if (m_callbackInterface != null) {
			m_callbackInterface.OnRefreshComplete();
		}

		LuaEventManager.triggerEvent("OnSteamRefreshInternetServers");
	}

	private static void onServerRespondedCallback(String string, int int1) {
		if (m_callbackInterface != null) {
			m_callbackInterface.OnServerResponded(string, int1);
		}

		GameServerDetails gameServerDetails = GetServerDetails(string, int1);
		if (gameServerDetails != null) {
			Server server = new Server();
			server.setName(gameServerDetails.name);
			server.setDescription("");
			server.setSteamId(Long.toString(gameServerDetails.steamId));
			server.setPing(Integer.toString(gameServerDetails.ping));
			server.setPlayers(Integer.toString(gameServerDetails.numPlayers));
			server.setMaxPlayers(Integer.toString(gameServerDetails.maxPlayers));
			server.setOpen(true);
			server.setIp(gameServerDetails.address);
			server.setPort(Integer.toString(gameServerDetails.port));
			server.setMods(gameServerDetails.tags.replace(";hosted", "").replace("hidden", ""));
			server.setHosted(gameServerDetails.tags.endsWith(";hosted"));
			server.setVersion("");
			server.setLastUpdate(1);
			server.setPasswordProtected(gameServerDetails.passwordProtected);
			ReleaseServerQuery(string, int1);
			LuaEventManager.triggerEvent("OnSteamServerResponded2", string, (double)int1, server);
		}
	}

	private static void onServerFailedToRespondCallback(String string, int int1) {
		if (m_callbackInterface != null) {
			m_callbackInterface.OnServerFailedToRespond(string, int1);
		}

		LuaEventManager.triggerEvent("OnSteamServerFailedToRespond2", string, (double)int1);
	}

	private static void onRulesRefreshComplete(String string, int int1, String[] stringArray) {
		if (m_callbackInterface != null) {
			m_callbackInterface.OnSteamRulesRefreshComplete(string, int1);
		}

		KahluaTable kahluaTable = LuaManager.platform.newTable();
		for (int int2 = 0; int2 < stringArray.length; int2 += 2) {
			kahluaTable.rawset(stringArray[int2], stringArray[int2 + 1]);
		}

		LuaEventManager.triggerEvent("OnSteamRulesRefreshComplete", string, (double)int1, kahluaTable);
	}
}
