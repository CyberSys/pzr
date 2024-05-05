package zombie.worldMap;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.core.math.PZMath;


public final class WorldMapData extends Asset {
	public static final HashMap s_fileNameToData = new HashMap();
	public String m_relativeFileName;
	public final ArrayList m_cells = new ArrayList();
	public final HashMap m_cellLookup = new HashMap();
	public int m_minX;
	public int m_minY;
	public int m_maxX;
	public int m_maxY;
	public static final AssetType ASSET_TYPE = new AssetType("WorldMapData");

	public static WorldMapData getOrCreateData(String string) {
		WorldMapData worldMapData = (WorldMapData)s_fileNameToData.get(string);
		if (worldMapData == null && Files.exists(Paths.get(string), new LinkOption[0])) {
			worldMapData = (WorldMapData)WorldMapDataAssetManager.instance.load(new AssetPath(string));
			s_fileNameToData.put(string, worldMapData);
		}

		return worldMapData;
	}

	public WorldMapData(AssetPath assetPath, AssetManager assetManager) {
		super(assetPath, assetManager);
	}

	public WorldMapData(AssetPath assetPath, AssetManager assetManager, AssetManager.AssetParams assetParams) {
		super(assetPath, assetManager);
	}

	public void clear() {
		Iterator iterator = this.m_cells.iterator();
		while (iterator.hasNext()) {
			WorldMapCell worldMapCell = (WorldMapCell)iterator.next();
			worldMapCell.dispose();
		}

		this.m_cells.clear();
		this.m_cellLookup.clear();
		this.m_minX = 0;
		this.m_minY = 0;
		this.m_maxX = 0;
		this.m_maxY = 0;
	}

	public int getWidthInCells() {
		return this.m_maxX - this.m_minX + 1;
	}

	public int getHeightInCells() {
		return this.m_maxY - this.m_minY + 1;
	}

	public int getWidthInSquares() {
		return this.getWidthInCells() * 300;
	}

	public int getHeightInSquares() {
		return this.getHeightInCells() * 300;
	}

	public void onLoaded() {
		this.m_minX = Integer.MAX_VALUE;
		this.m_minY = Integer.MAX_VALUE;
		this.m_maxX = Integer.MIN_VALUE;
		this.m_maxY = Integer.MIN_VALUE;
		this.m_cellLookup.clear();
		WorldMapCell worldMapCell;
		for (Iterator iterator = this.m_cells.iterator(); iterator.hasNext(); this.m_maxY = Math.max(this.m_maxY, worldMapCell.m_y)) {
			worldMapCell = (WorldMapCell)iterator.next();
			Integer integer = this.getCellKey(worldMapCell.m_x, worldMapCell.m_y);
			this.m_cellLookup.put(integer, worldMapCell);
			this.m_minX = Math.min(this.m_minX, worldMapCell.m_x);
			this.m_minY = Math.min(this.m_minY, worldMapCell.m_y);
			this.m_maxX = Math.max(this.m_maxX, worldMapCell.m_x);
		}
	}

	public WorldMapCell getCell(int int1, int int2) {
		Integer integer = this.getCellKey(int1, int2);
		return (WorldMapCell)this.m_cellLookup.get(integer);
	}

	private Integer getCellKey(int int1, int int2) {
		return int1 + int2 * 1000;
	}

	public void hitTest(float float1, float float2, ArrayList arrayList) {
		int int1 = (int)PZMath.floor(float1 / 300.0F);
		int int2 = (int)PZMath.floor(float2 / 300.0F);
		if (int1 >= this.m_minX && int1 <= this.m_maxX && int2 >= this.m_minY && int2 <= this.m_maxY) {
			WorldMapCell worldMapCell = this.getCell(int1, int2);
			if (worldMapCell != null) {
				worldMapCell.hitTest(float1, float2, arrayList);
			}
		}
	}

	public static void Reset() {
	}

	public AssetType getType() {
		return ASSET_TYPE;
	}

	protected void onBeforeEmpty() {
		super.onBeforeEmpty();
		this.clear();
	}
}
