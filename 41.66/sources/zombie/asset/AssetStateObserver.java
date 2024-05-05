package zombie.asset;


public interface AssetStateObserver {

	void onStateChanged(Asset.State state, Asset.State state2, Asset asset);
}
