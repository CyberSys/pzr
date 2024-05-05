package zombie.worldMap;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import zombie.DebugFileWatcher;
import zombie.PredicatedFileWatcher;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetTask_RunFileTask;
import zombie.debug.DebugLog;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;


public final class WorldMapDataAssetManager extends AssetManager {
	public static final WorldMapDataAssetManager instance = new WorldMapDataAssetManager();

	protected void startLoading(Asset asset) {
		WorldMapData worldMapData = (WorldMapData)asset;
		FileSystem fileSystem = this.getOwner().getFileSystem();
		String string = asset.getPath().getPath();
		Object object;
		if (Files.exists(Paths.get(string + ".bin"), new LinkOption[0])) {
			object = new FileTask_LoadWorldMapBinary(worldMapData, string + ".bin", fileSystem, (worldMapDatax)->{
				this.loadCallback(worldMapData, worldMapDatax);
			});
		} else {
			object = new FileTask_LoadWorldMapXML(worldMapData, string, fileSystem, (worldMapDatax)->{
				this.loadCallback(worldMapData, worldMapDatax);
			});
		}

		((FileTask)object).setPriority(4);
		AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask((FileTask)object, asset);
		this.setTask(asset, assetTask_RunFileTask);
		assetTask_RunFileTask.execute();
	}

	private void loadCallback(WorldMapData worldMapData, Object object) {
		if (object == Boolean.TRUE) {
			worldMapData.onLoaded();
			this.onLoadingSucceeded(worldMapData);
		} else {
			DebugLog.General.warn("Failed to load asset: " + worldMapData.getPath());
			this.onLoadingFailed(worldMapData);
		}
	}

	protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
		WorldMapData worldMapData = new WorldMapData(assetPath, this, assetParams);
		DebugFileWatcher.instance.add(new PredicatedFileWatcher(assetPath.getPath(), (worldMapDatax)->{
			this.reload(worldMapData, assetParams);
		}));
		return worldMapData;
	}

	protected void destroyAsset(Asset asset) {
	}
}
