package zombie.core.znet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import zombie.Lua.LuaEventManager;
import zombie.core.textures.Texture;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class SteamFriends {
	public static final int k_EPersonaStateOffline = 0;
	public static final int k_EPersonaStateOnline = 1;
	public static final int k_EPersonaStateBusy = 2;
	public static final int k_EPersonaStateAway = 3;
	public static final int k_EPersonaStateSnooze = 4;
	public static final int k_EPersonaStateLookingToTrade = 5;
	public static final int k_EPersonaStateLookingToPlay = 6;

	public static void init() {
		if (SteamUtils.isSteamModeEnabled()) {
			n_Init();
		}
	}

	public static void shutdown() {
		if (SteamUtils.isSteamModeEnabled()) {
			n_Shutdown();
		}
	}

	public static native void n_Init();

	public static native void n_Shutdown();

	public static native String GetPersonaName();

	public static native int GetFriendCount();

	public static native long GetFriendByIndex(int int1);

	public static native String GetFriendPersonaName(long long1);

	public static native int GetFriendPersonaState(long long1);

	public static native boolean InviteUserToGame(long long1, String string);

	public static native void ActivateGameOverlay(String string);

	public static native void ActivateGameOverlayToUser(String string, long long1);

	public static native void ActivateGameOverlayToWebPage(String string);

	public static native void SetPlayedWith(long long1);

	public static native void UpdateRichPresenceConnectionInfo(String string, String string2);

	public static List GetFriendList() {
		ArrayList arrayList = new ArrayList();
		int int1 = GetFriendCount();
		for (int int2 = 0; int2 < int1; ++int2) {
			long long1 = GetFriendByIndex(int2);
			String string = GetFriendPersonaName(long1);
			arrayList.add(new SteamFriend(string, long1));
		}

		return arrayList;
	}

	public static native int CreateSteamAvatar(long long1, ByteBuffer byteBuffer);

	private static void onStatusChangedCallback(long long1) {
		if (GameClient.bClient || GameServer.bServer) {
			LuaEventManager.triggerEvent("OnSteamFriendStatusChanged", Long.toString(long1));
		}
	}

	private static void onAvatarChangedCallback(long long1) {
		Texture.steamAvatarChanged(long1);
	}

	private static void onProfileNameChanged(long long1) {
		if (GameClient.bClient) {
			GameClient.instance.sendSteamProfileName(long1);
		}
	}
}
