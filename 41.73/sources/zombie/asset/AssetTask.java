package zombie.asset;


public abstract class AssetTask {
	public Asset m_asset;

	public AssetTask(Asset asset) {
		this.m_asset = asset;
	}

	public abstract void execute();

	public abstract void cancel();
}
