package zombie.asset;

import gnu.trove.map.hash.TLongObjectHashMap;
import zombie.fileSystem.FileSystem;


public final class AssetManagers {
	private final AssetManagers.AssetManagerTable m_managers = new AssetManagers.AssetManagerTable();
	private final FileSystem m_file_system;

	public AssetManagers(FileSystem fileSystem) {
		this.m_file_system = fileSystem;
	}

	public AssetManager get(AssetType assetType) {
		return (AssetManager)this.m_managers.get(assetType.type);
	}

	public void add(AssetType assetType, AssetManager assetManager) {
		this.m_managers.put(assetType.type, assetManager);
	}

	public FileSystem getFileSystem() {
		return this.m_file_system;
	}

	public static final class AssetManagerTable extends TLongObjectHashMap {
	}
}
