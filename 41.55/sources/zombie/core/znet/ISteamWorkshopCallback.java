package zombie.core.znet;


public interface ISteamWorkshopCallback {

	void onItemCreated(long long1, boolean boolean1);

	void onItemNotCreated(int int1);

	void onItemUpdated(boolean boolean1);

	void onItemNotUpdated(int int1);

	void onItemSubscribed(long long1);

	void onItemNotSubscribed(long long1, int int1);

	void onItemDownloaded(long long1);

	void onItemNotDownloaded(long long1, int int1);

	void onItemQueryCompleted(long long1, int int1);

	void onItemQueryNotCompleted(long long1, int int1);
}
