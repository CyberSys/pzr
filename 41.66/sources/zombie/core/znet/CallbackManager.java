package zombie.core.znet;

import zombie.Lua.LuaEventManager;


public class CallbackManager implements IJoinRequestCallback {

	public CallbackManager() {
		SteamUtils.addJoinRequestCallback(this);
	}

	public void onJoinRequest(long long1, String string) {
		LuaEventManager.triggerEvent("OnAcceptInvite", string);
	}
}
