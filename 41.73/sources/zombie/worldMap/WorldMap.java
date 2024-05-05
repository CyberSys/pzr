package zombie.worldMap;

import java.util.ArrayList;
import java.util.Iterator;
import zombie.ZomboidFileSystem;
import zombie.asset.Asset;
import zombie.asset.AssetStateObserver;
import zombie.inventory.types.MapItem;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;
import zombie.worldMap.symbols.MapSymbolDefinitions;


public final class WorldMap implements AssetStateObserver {
	public final ArrayList m_data = new ArrayList();
	public final ArrayList m_images = new ArrayList();
	public int m_minDataX;
	public int m_minDataY;
	public int m_maxDataX;
	public int m_maxDataY;
	public int m_minX;
	public int m_minY;
	public int m_maxX;
	public int m_maxY;
	private boolean m_boundsFromData = false;
	public final ArrayList m_lastDataInDirectory = new ArrayList();

	public void setBoundsInCells(int int1, int int2, int int3, int int4) {
		this.setBoundsInSquares(int1 * 300, int2 * 300, int3 * 300 + 299, int4 * 300 + 299);
	}

	public void setBoundsInSquares(int int1, int int2, int int3, int int4) {
		this.m_minX = int1;
		this.m_minY = int2;
		this.m_maxX = int3;
		this.m_maxY = int4;
	}

	public void setBoundsFromData() {
		this.m_boundsFromData = true;
		this.setBoundsInCells(this.m_minDataX, this.m_minDataY, this.m_maxDataX, this.m_maxDataY);
	}

	public void setBoundsFromWorld() {
		IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
		this.setBoundsInCells(metaGrid.getMinX(), metaGrid.getMinY(), metaGrid.getMaxX(), metaGrid.getMaxY());
	}

	public void addData(String string) {
		if (!StringUtils.isNullOrWhitespace(string)) {
			String string2 = ZomboidFileSystem.instance.getString(string);
			WorldMapData worldMapData = WorldMapData.getOrCreateData(string2);
			if (worldMapData != null && !this.m_data.contains(worldMapData)) {
				worldMapData.m_relativeFileName = string;
				this.m_data.add(worldMapData);
				worldMapData.getObserverCb().add(this);
				if (worldMapData.isReady()) {
					this.updateDataBounds();
				}
			}
		}
	}

	public int getDataCount() {
		return this.m_data.size();
	}

	public WorldMapData getDataByIndex(int int1) {
		return (WorldMapData)this.m_data.get(int1);
	}

	public void clearData() {
		Iterator iterator = this.m_data.iterator();
		while (iterator.hasNext()) {
			WorldMapData worldMapData = (WorldMapData)iterator.next();
			worldMapData.getObserverCb().remove(this);
		}

		this.m_data.clear();
		this.m_lastDataInDirectory.clear();
		this.updateDataBounds();
	}

	public void endDirectoryData() {
		if (this.hasData()) {
			WorldMapData worldMapData = this.getDataByIndex(this.getDataCount() - 1);
			if (!this.m_lastDataInDirectory.contains(worldMapData)) {
				this.m_lastDataInDirectory.add(worldMapData);
			}
		}
	}

	public boolean isLastDataInDirectory(WorldMapData worldMapData) {
		return this.m_lastDataInDirectory.contains(worldMapData);
	}

	private void updateDataBounds() {
		this.m_minDataX = Integer.MAX_VALUE;
		this.m_minDataY = Integer.MAX_VALUE;
		this.m_maxDataX = Integer.MIN_VALUE;
		this.m_maxDataY = Integer.MIN_VALUE;
		for (int int1 = 0; int1 < this.m_data.size(); ++int1) {
			WorldMapData worldMapData = (WorldMapData)this.m_data.get(int1);
			if (worldMapData.isReady()) {
				this.m_minDataX = Math.min(this.m_minDataX, worldMapData.m_minX);
				this.m_minDataY = Math.min(this.m_minDataY, worldMapData.m_minY);
				this.m_maxDataX = Math.max(this.m_maxDataX, worldMapData.m_maxX);
				this.m_maxDataY = Math.max(this.m_maxDataY, worldMapData.m_maxY);
			}
		}

		if (this.m_minDataX > this.m_maxDataX) {
			this.m_minDataX = this.m_maxDataX = this.m_minDataY = this.m_maxDataY = 0;
		}
	}

	public boolean hasData() {
		return !this.m_data.isEmpty();
	}

	public void addImages(String string) {
		if (!StringUtils.isNullOrWhitespace(string)) {
			WorldMapImages worldMapImages = WorldMapImages.getOrCreate(string);
			if (worldMapImages != null && !this.m_images.contains(worldMapImages)) {
				this.m_images.add(worldMapImages);
			}
		}
	}

	public boolean hasImages() {
		return !this.m_images.isEmpty();
	}

	public int getImagesCount() {
		return this.m_images.size();
	}

	public WorldMapImages getImagesByIndex(int int1) {
		return (WorldMapImages)this.m_images.get(int1);
	}

	public int getMinXInCells() {
		return this.m_minX / 300;
	}

	public int getMinYInCells() {
		return this.m_minY / 300;
	}

	public int getMaxXInCells() {
		return this.m_maxX / 300;
	}

	public int getMaxYInCells() {
		return this.m_maxY / 300;
	}

	public int getWidthInCells() {
		return this.getMaxXInCells() - this.getMinXInCells() + 1;
	}

	public int getHeightInCells() {
		return this.getMaxYInCells() - this.getMinYInCells() + 1;
	}

	public int getMinXInSquares() {
		return this.m_minX;
	}

	public int getMinYInSquares() {
		return this.m_minY;
	}

	public int getMaxXInSquares() {
		return this.m_maxX;
	}

	public int getMaxYInSquares() {
		return this.m_maxY;
	}

	public int getWidthInSquares() {
		return this.m_maxX - this.m_minX + 1;
	}

	public int getHeightInSquares() {
		return this.m_maxY - this.m_minY + 1;
	}

	public WorldMapCell getCell(int int1, int int2) {
		for (int int3 = 0; int3 < this.m_data.size(); ++int3) {
			WorldMapData worldMapData = (WorldMapData)this.m_data.get(int3);
			if (worldMapData.isReady()) {
				WorldMapCell worldMapCell = worldMapData.getCell(int1, int2);
				if (worldMapCell != null) {
					return worldMapCell;
				}
			}
		}

		return null;
	}

	public int getDataWidthInCells() {
		return this.m_maxDataX - this.m_minDataX + 1;
	}

	public int getDataHeightInCells() {
		return this.m_maxDataY - this.m_minDataY + 1;
	}

	public int getDataWidthInSquares() {
		return this.getDataWidthInCells() * 300;
	}

	public int getDataHeightInSquares() {
		return this.getDataHeightInCells() * 300;
	}

	public static void Reset() {
		WorldMapSettings.Reset();
		WorldMapVisited.Reset();
		WorldMapData.Reset();
		WorldMapImages.Reset();
		MapSymbolDefinitions.Reset();
		MapItem.Reset();
	}

	public void onStateChanged(Asset.State state, Asset.State state2, Asset asset) {
		this.updateDataBounds();
		if (this.m_boundsFromData) {
			this.setBoundsInCells(this.m_minDataX, this.m_minDataY, this.m_maxDataX, this.m_maxDataY);
		}
	}
}
