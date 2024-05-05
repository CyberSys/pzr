package zombie.asset;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.fileSystem.IFile;


public abstract class AssetManager implements AssetStateObserver {
	private final AssetManager.AssetTable m_assets = new AssetManager.AssetTable();
	private AssetManagers m_owner;
	private boolean m_is_unload_enabled = false;

	public void create(AssetType assetType, AssetManagers assetManagers) {
		assetManagers.add(assetType, this);
		this.m_owner = assetManagers;
	}

	public void destroy() {
		this.m_assets.forEachValue((var1)->{
			if (!var1.isEmpty()) {
				DebugLog.Asset.println("Leaking asset " + var1.getPath());
			}

			this.destroyAsset(var1);
			return true;
		});
	}

	public void removeUnreferenced() {
		if (this.m_is_unload_enabled) {
			ArrayList arrayList = new ArrayList();
			this.m_assets.forEachValue((arrayListx)->{
				if (arrayListx.getRefCount() == 0) {
					arrayList.add(arrayListx);
				}

				return true;
			});

			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				Asset asset = (Asset)iterator.next();
				this.m_assets.remove(asset.getPath());
				this.destroyAsset(asset);
			}
		}
	}

	public Asset load(AssetPath assetPath) {
		return this.load(assetPath, (AssetManager.AssetParams)null);
	}

	public Asset load(AssetPath assetPath, AssetManager.AssetParams assetParams) {
		if (!assetPath.isValid()) {
			return null;
		} else {
			Asset asset = this.get(assetPath);
			if (asset == null) {
				asset = this.createAsset(assetPath, assetParams);
				this.m_assets.put(assetPath.getPath(), asset);
			}

			if (asset.isEmpty() && asset.m_priv.m_desired_state == Asset.State.EMPTY) {
				this.doLoad(asset, assetParams);
			}

			asset.addRef();
			return asset;
		}
	}

	public void load(Asset asset) {
		if (asset.isEmpty() && asset.m_priv.m_desired_state == Asset.State.EMPTY) {
			this.doLoad(asset, (AssetManager.AssetParams)null);
		}

		asset.addRef();
	}

	public void unload(AssetPath assetPath) {
		Asset asset = this.get(assetPath);
		if (asset != null) {
			this.unload(asset);
		}
	}

	public void unload(Asset asset) {
		int int1 = asset.rmRef();
		assert int1 >= 0;
		if (int1 == 0 && this.m_is_unload_enabled) {
			this.doUnload(asset);
		}
	}

	public void reload(AssetPath assetPath) {
		Asset asset = this.get(assetPath);
		if (asset != null) {
			this.reload(asset);
		}
	}

	public void reload(Asset asset) {
		this.reload(asset, (AssetManager.AssetParams)null);
	}

	public void reload(Asset asset, AssetManager.AssetParams assetParams) {
		this.doUnload(asset);
		this.doLoad(asset, assetParams);
	}

	public void enableUnload(boolean boolean1) {
		this.m_is_unload_enabled = boolean1;
		if (boolean1) {
			this.m_assets.forEachValue((boolean1x)->{
				if (boolean1x.getRefCount() == 0) {
					this.doUnload(boolean1x);
				}

				return true;
			});
		}
	}

	private void doLoad(Asset asset, AssetManager.AssetParams assetParams) {
		if (asset.m_priv.m_desired_state != Asset.State.READY) {
			asset.m_priv.m_desired_state = Asset.State.READY;
			asset.setAssetParams(assetParams);
			this.startLoading(asset);
		}
	}

	private void doUnload(Asset asset) {
		if (asset.m_priv.m_task != null) {
			asset.m_priv.m_task.cancel();
			asset.m_priv.m_task = null;
		}

		asset.m_priv.m_desired_state = Asset.State.EMPTY;
		this.unloadData(asset);
		assert asset.m_priv.m_empty_dep_count <= 1;
		asset.m_priv.m_empty_dep_count = 1;
		asset.m_priv.m_failed_dep_count = 0;
		asset.m_priv.checkState();
	}

	public void onStateChanged(Asset.State state, Asset.State state2, Asset asset) {
	}

	protected void startLoading(Asset asset) {
		if (asset.m_priv.m_task == null) {
			asset.m_priv.m_task = new AssetTask_LoadFromFileAsync(asset, false);
			asset.m_priv.m_task.execute();
		}
	}

	protected final void onLoadingSucceeded(Asset asset) {
		asset.m_priv.onLoadingSucceeded();
	}

	protected final void onLoadingFailed(Asset asset) {
		asset.m_priv.onLoadingFailed();
	}

	protected final void setTask(Asset asset, AssetTask assetTask) {
		if (asset.m_priv.m_task != null) {
			if (assetTask == null) {
				asset.m_priv.m_task = null;
			}
		} else {
			asset.m_priv.m_task = assetTask;
		}
	}

	protected boolean loadDataFromFile(Asset asset, IFile iFile) {
		throw new RuntimeException("not implemented");
	}

	protected void unloadData(Asset asset) {
	}

	public AssetManager.AssetTable getAssetTable() {
		return this.m_assets;
	}

	public AssetManagers getOwner() {
		return this.m_owner;
	}

	protected abstract Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams);

	protected abstract void destroyAsset(Asset asset);

	protected Asset get(AssetPath assetPath) {
		return (Asset)this.m_assets.get(assetPath.getPath());
	}

	public static final class AssetTable extends THashMap {
	}

	public static class AssetParams {
	}
}
