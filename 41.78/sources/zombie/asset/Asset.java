package zombie.asset;

import java.util.ArrayList;


public abstract class Asset {
	protected final AssetManager m_asset_manager;
	private AssetPath m_path;
	private int m_ref_count = 0;
	final Asset.PRIVATE m_priv = new Asset.PRIVATE();

	protected Asset(AssetPath assetPath, AssetManager assetManager) {
		this.m_path = assetPath;
		this.m_asset_manager = assetManager;
	}

	public abstract AssetType getType();

	public Asset.State getState() {
		return this.m_priv.m_current_state;
	}

	public boolean isEmpty() {
		return this.m_priv.m_current_state == Asset.State.EMPTY;
	}

	public boolean isReady() {
		return this.m_priv.m_current_state == Asset.State.READY;
	}

	public boolean isFailure() {
		return this.m_priv.m_current_state == Asset.State.FAILURE;
	}

	public void onCreated(Asset.State state) {
		this.m_priv.onCreated(state);
	}

	public int getRefCount() {
		return this.m_ref_count;
	}

	public Asset.ObserverCallback getObserverCb() {
		if (this.m_priv.m_cb == null) {
			this.m_priv.m_cb = new Asset.ObserverCallback();
		}

		return this.m_priv.m_cb;
	}

	public AssetPath getPath() {
		return this.m_path;
	}

	public AssetManager getAssetManager() {
		return this.m_asset_manager;
	}

	protected void onBeforeReady() {
	}

	protected void onBeforeEmpty() {
	}

	public void addDependency(Asset asset) {
		this.m_priv.addDependency(asset);
	}

	public void removeDependency(Asset asset) {
		this.m_priv.removeDependency(asset);
	}

	int addRef() {
		return ++this.m_ref_count;
	}

	int rmRef() {
		return --this.m_ref_count;
	}

	public void setAssetParams(AssetManager.AssetParams assetParams) {
	}

	final class PRIVATE implements AssetStateObserver {
		Asset.State m_current_state;
		Asset.State m_desired_state;
		int m_empty_dep_count;
		int m_failed_dep_count;
		Asset.ObserverCallback m_cb;
		AssetTask m_task;

		PRIVATE() {
			this.m_current_state = Asset.State.EMPTY;
			this.m_desired_state = Asset.State.EMPTY;
			this.m_empty_dep_count = 1;
			this.m_failed_dep_count = 0;
			this.m_task = null;
		}

		void onCreated(Asset.State state) {
			assert this.m_empty_dep_count == 1;
			assert this.m_failed_dep_count == 0;
			this.m_current_state = state;
			this.m_desired_state = Asset.State.READY;
			this.m_failed_dep_count = state == Asset.State.FAILURE ? 1 : 0;
			this.m_empty_dep_count = 0;
		}

		void addDependency(Asset asset) {
			assert this.m_desired_state != Asset.State.EMPTY;
			asset.getObserverCb().add(this);
			if (asset.isEmpty()) {
				++this.m_empty_dep_count;
			}

			if (asset.isFailure()) {
				++this.m_failed_dep_count;
			}

			this.checkState();
		}

		void removeDependency(Asset asset) {
			asset.getObserverCb().remove(this);
			if (asset.isEmpty()) {
				assert this.m_empty_dep_count > 0;
				--this.m_empty_dep_count;
			}

			if (asset.isFailure()) {
				assert this.m_failed_dep_count > 0;
				--this.m_failed_dep_count;
			}

			this.checkState();
		}

		public void onStateChanged(Asset.State state, Asset.State state2, Asset asset) {
			assert state != state2;
			assert this.m_current_state != Asset.State.EMPTY || this.m_desired_state != Asset.State.EMPTY;
			if (state == Asset.State.EMPTY) {
				assert this.m_empty_dep_count > 0;
				--this.m_empty_dep_count;
			}

			if (state == Asset.State.FAILURE) {
				assert this.m_failed_dep_count > 0;
				--this.m_failed_dep_count;
			}

			if (state2 == Asset.State.EMPTY) {
				++this.m_empty_dep_count;
			}

			if (state2 == Asset.State.FAILURE) {
				++this.m_failed_dep_count;
			}

			this.checkState();
		}

		void onLoadingSucceeded() {
			assert this.m_current_state != Asset.State.READY;
			assert this.m_empty_dep_count == 1;
			--this.m_empty_dep_count;
			this.m_task = null;
			this.checkState();
		}

		void onLoadingFailed() {
			assert this.m_current_state != Asset.State.READY;
			assert this.m_empty_dep_count == 1;
			++this.m_failed_dep_count;
			--this.m_empty_dep_count;
			this.m_task = null;
			this.checkState();
		}

		void checkState() {
			Asset.State state = this.m_current_state;
			if (this.m_failed_dep_count > 0 && this.m_current_state != Asset.State.FAILURE) {
				this.m_current_state = Asset.State.FAILURE;
				Asset.this.getAssetManager().onStateChanged(state, this.m_current_state, Asset.this);
				if (this.m_cb != null) {
					this.m_cb.invoke(state, this.m_current_state, Asset.this);
				}
			}

			if (this.m_failed_dep_count == 0) {
				if (this.m_empty_dep_count == 0 && this.m_current_state != Asset.State.READY && this.m_desired_state != Asset.State.EMPTY) {
					Asset.this.onBeforeReady();
					this.m_current_state = Asset.State.READY;
					Asset.this.getAssetManager().onStateChanged(state, this.m_current_state, Asset.this);
					if (this.m_cb != null) {
						this.m_cb.invoke(state, this.m_current_state, Asset.this);
					}
				}

				if (this.m_empty_dep_count > 0 && this.m_current_state != Asset.State.EMPTY) {
					Asset.this.onBeforeEmpty();
					this.m_current_state = Asset.State.EMPTY;
					Asset.this.getAssetManager().onStateChanged(state, this.m_current_state, Asset.this);
					if (this.m_cb != null) {
						this.m_cb.invoke(state, this.m_current_state, Asset.this);
					}
				}
			}
		}
	}

	public static enum State {

		EMPTY,
		READY,
		FAILURE;

		private static Asset.State[] $values() {
			return new Asset.State[]{EMPTY, READY, FAILURE};
		}
	}

	public static final class ObserverCallback extends ArrayList {
		public void invoke(Asset.State state, Asset.State state2, Asset asset) {
			int int1 = this.size();
			for (int int2 = 0; int2 < int1; ++int2) {
				((AssetStateObserver)this.get(int2)).onStateChanged(state, state2, asset);
			}
		}
	}
}
