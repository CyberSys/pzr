package zombie.core.znet;


public interface IServerBrowserCallback {

	void OnServerResponded(int int1);

	void OnServerFailedToRespond(int int1);

	void OnRefreshComplete();

	void OnServerResponded(String string, int int1);

	void OnServerFailedToRespond(String string, int int1);

	void OnSteamRulesRefreshComplete(String string, int int1);
}
