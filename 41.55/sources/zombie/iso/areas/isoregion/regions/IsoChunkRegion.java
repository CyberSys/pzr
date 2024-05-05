package zombie.iso.areas.isoregion.regions;

import java.util.ArrayList;
import zombie.core.Color;
import zombie.core.Core;
import zombie.iso.areas.isoregion.IsoRegions;


public final class IsoChunkRegion implements IChunkRegion {
	private final IsoRegionManager manager;
	private boolean isInPool = false;
	private Color color;
	private int ID;
	private byte zLayer;
	private byte squareSize = 0;
	private byte roofCnt = 0;
	private byte chunkBorderSquaresCnt = 0;
	private final boolean[] enclosed = new boolean[4];
	private boolean enclosedCache = true;
	private final ArrayList connectedNeighbors = new ArrayList();
	private final ArrayList allNeighbors = new ArrayList();
	private boolean isDirtyEnclosed = false;
	private IsoWorldRegion isoWorldRegion;

	public int getID() {
		return this.ID;
	}

	public int getSquareSize() {
		return this.squareSize;
	}

	public Color getColor() {
		return this.color;
	}

	public int getzLayer() {
		return this.zLayer;
	}

	public IsoWorldRegion getIsoWorldRegion() {
		return this.isoWorldRegion;
	}

	public void setIsoWorldRegion(IsoWorldRegion worldRegion) {
		this.isoWorldRegion = worldRegion;
	}

	protected boolean isInPool() {
		return this.isInPool;
	}

	protected IsoChunkRegion(IsoRegionManager regionManager) {
		this.manager = regionManager;
	}

	protected void init(int int1, int int2) {
		this.isInPool = false;
		this.ID = int1;
		this.zLayer = (byte)int2;
		this.resetChunkBorderSquaresCnt();
		if (this.color == null) {
			this.color = this.manager.getColor();
		}

		this.squareSize = 0;
		this.roofCnt = 0;
		this.resetEnclosed();
	}

	protected IsoChunkRegion reset() {
		this.isInPool = true;
		this.unlinkNeighbors();
		IsoWorldRegion worldRegion = this.unlinkFromIsoWorldRegion();
		if (worldRegion != null && worldRegion.size() <= 0) {
			if (Core.bDebug) {
				throw new RuntimeException("ChunkRegion.reset IsoChunkRegion has IsoWorldRegion with 0 members.");
			}

			this.manager.releaseIsoWorldRegion(worldRegion);
			IsoRegions.warn("ChunkRegion.reset IsoChunkRegion has IsoWorldRegion with 0 members.");
		}

		this.resetChunkBorderSquaresCnt();
		this.ID = -1;
		this.squareSize = 0;
		this.roofCnt = 0;
		this.resetEnclosed();
		return this;
	}

	public IsoWorldRegion unlinkFromIsoWorldRegion() {
		if (this.isoWorldRegion != null) {
			IsoWorldRegion worldRegion = this.isoWorldRegion;
			this.isoWorldRegion.removeIsoChunkRegion(this);
			this.isoWorldRegion = null;
			return worldRegion;
		} else {
			return null;
		}
	}

	public int getRoofCnt() {
		return this.roofCnt;
	}

	public void addRoof() {
		++this.roofCnt;
		if (this.roofCnt > this.squareSize) {
			IsoRegions.warn("ChunkRegion.addRoof roofCount exceed squareSize.");
			this.roofCnt = this.squareSize;
		} else {
			if (this.isoWorldRegion != null) {
				this.isoWorldRegion.addRoof();
			}
		}
	}

	public void resetRoofCnt() {
		if (this.isoWorldRegion != null) {
			this.isoWorldRegion.removeRoofs(this.roofCnt);
		}

		this.roofCnt = 0;
	}

	public void addSquareCount() {
		++this.squareSize;
	}

	public int getChunkBorderSquaresCnt() {
		return this.chunkBorderSquaresCnt;
	}

	public void addChunkBorderSquaresCnt() {
		++this.chunkBorderSquaresCnt;
	}

	protected void removeChunkBorderSquaresCnt() {
		--this.chunkBorderSquaresCnt;
		if (this.chunkBorderSquaresCnt < 0) {
			this.chunkBorderSquaresCnt = 0;
		}
	}

	protected void resetChunkBorderSquaresCnt() {
		this.chunkBorderSquaresCnt = 0;
	}

	private void resetEnclosed() {
		for (byte byte1 = 0; byte1 < 4; ++byte1) {
			this.enclosed[byte1] = true;
		}

		this.isDirtyEnclosed = false;
		this.enclosedCache = true;
	}

	public void setEnclosed(byte byte1, boolean boolean1) {
		this.isDirtyEnclosed = true;
		this.enclosed[byte1] = boolean1;
	}

	protected void setDirtyEnclosed() {
		this.isDirtyEnclosed = true;
		if (this.isoWorldRegion != null) {
			this.isoWorldRegion.setDirtyEnclosed();
		}
	}

	public boolean getIsEnclosed() {
		if (!this.isDirtyEnclosed) {
			return this.enclosedCache;
		} else {
			this.isDirtyEnclosed = false;
			this.enclosedCache = true;
			for (byte byte1 = 0; byte1 < 4; ++byte1) {
				if (!this.enclosed[byte1]) {
					this.enclosedCache = false;
				}
			}

			if (this.isoWorldRegion != null) {
				this.isoWorldRegion.setDirtyEnclosed();
			}

			return this.enclosedCache;
		}
	}

	public ArrayList getConnectedNeighbors() {
		return this.connectedNeighbors;
	}

	public void addConnectedNeighbor(IsoChunkRegion chunkRegion) {
		if (chunkRegion != null) {
			if (!this.connectedNeighbors.contains(chunkRegion)) {
				this.connectedNeighbors.add(chunkRegion);
			}
		}
	}

	protected void removeConnectedNeighbor(IsoChunkRegion chunkRegion) {
		this.connectedNeighbors.remove(chunkRegion);
	}

	public int getNeighborCount() {
		return this.allNeighbors.size();
	}

	protected ArrayList getAllNeighbors() {
		return this.allNeighbors;
	}

	public void addNeighbor(IsoChunkRegion chunkRegion) {
		if (chunkRegion != null) {
			if (!this.allNeighbors.contains(chunkRegion)) {
				this.allNeighbors.add(chunkRegion);
			}
		}
	}

	protected void removeNeighbor(IsoChunkRegion chunkRegion) {
		this.allNeighbors.remove(chunkRegion);
	}

	protected void unlinkNeighbors() {
		IsoChunkRegion chunkRegion;
		int int1;
		for (int1 = 0; int1 < this.connectedNeighbors.size(); ++int1) {
			chunkRegion = (IsoChunkRegion)this.connectedNeighbors.get(int1);
			chunkRegion.removeConnectedNeighbor(this);
		}

		this.connectedNeighbors.clear();
		for (int1 = 0; int1 < this.allNeighbors.size(); ++int1) {
			chunkRegion = (IsoChunkRegion)this.allNeighbors.get(int1);
			chunkRegion.removeNeighbor(this);
		}

		this.allNeighbors.clear();
	}

	public ArrayList getDebugConnectedNeighborCopy() {
		ArrayList arrayList = new ArrayList();
		if (this.connectedNeighbors.size() == 0) {
			return arrayList;
		} else {
			arrayList.addAll(this.connectedNeighbors);
			return arrayList;
		}
	}

	public boolean containsConnectedNeighbor(IsoChunkRegion chunkRegion) {
		return this.connectedNeighbors.contains(chunkRegion);
	}

	public boolean containsConnectedNeighborID(int int1) {
		if (this.connectedNeighbors.size() == 0) {
			return false;
		} else {
			for (int int2 = 0; int2 < this.connectedNeighbors.size(); ++int2) {
				IsoChunkRegion chunkRegion = (IsoChunkRegion)this.connectedNeighbors.get(int2);
				if (chunkRegion.getID() == int1) {
					return true;
				}
			}

			return false;
		}
	}

	public IsoChunkRegion getConnectedNeighborWithLargestIsoWorldRegion() {
		if (this.connectedNeighbors.size() == 0) {
			return null;
		} else {
			IsoWorldRegion worldRegion = null;
			IsoChunkRegion chunkRegion = null;
			for (int int1 = 0; int1 < this.connectedNeighbors.size(); ++int1) {
				IsoChunkRegion chunkRegion2 = (IsoChunkRegion)this.connectedNeighbors.get(int1);
				IsoWorldRegion worldRegion2 = chunkRegion2.getIsoWorldRegion();
				if (worldRegion2 != null && (worldRegion == null || worldRegion2.getSquareSize() > worldRegion.getSquareSize())) {
					worldRegion = worldRegion2;
					chunkRegion = chunkRegion2;
				}
			}

			return chunkRegion;
		}
	}

	protected IsoChunkRegion getFirstNeighborWithIsoWorldRegion() {
		if (this.connectedNeighbors.size() == 0) {
			return null;
		} else {
			for (int int1 = 0; int1 < this.connectedNeighbors.size(); ++int1) {
				IsoChunkRegion chunkRegion = (IsoChunkRegion)this.connectedNeighbors.get(int1);
				IsoWorldRegion worldRegion = chunkRegion.getIsoWorldRegion();
				if (worldRegion != null) {
					return chunkRegion;
				}
			}

			return null;
		}
	}
}
