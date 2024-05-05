package zombie.iso.areas.isoregion.regions;

import java.util.ArrayList;
import zombie.core.Color;
import zombie.core.Core;
import zombie.iso.areas.isoregion.IsoRegions;


public final class IsoWorldRegion implements IWorldRegion {
	private final IsoRegionManager manager;
	private boolean isInPool = false;
	private int ID;
	private Color color;
	private boolean enclosed = true;
	private ArrayList isoChunkRegions = new ArrayList();
	private int squareSize = 0;
	private int roofCnt = 0;
	private boolean isDirtyEnclosed = false;
	private boolean isDirtyRoofed = false;
	private ArrayList neighbors = new ArrayList();

	public int getID() {
		return this.ID;
	}

	public Color getColor() {
		return this.color;
	}

	public int size() {
		return this.isoChunkRegions.size();
	}

	public int getSquareSize() {
		return this.squareSize;
	}

	protected boolean isInPool() {
		return this.isInPool;
	}

	protected IsoWorldRegion(IsoRegionManager regionManager) {
		this.manager = regionManager;
	}

	protected void init(int int1) {
		this.isInPool = false;
		this.ID = int1;
		if (this.color == null) {
			this.color = this.manager.getColor();
		}

		this.squareSize = 0;
		this.roofCnt = 0;
		this.enclosed = true;
		this.isDirtyEnclosed = false;
		this.isDirtyRoofed = false;
	}

	protected IsoWorldRegion reset() {
		this.isInPool = true;
		this.ID = -1;
		this.squareSize = 0;
		this.roofCnt = 0;
		this.enclosed = true;
		this.isDirtyRoofed = false;
		this.isDirtyEnclosed = false;
		this.unlinkNeighbors();
		if (this.isoChunkRegions.size() > 0) {
			if (Core.bDebug) {
				throw new RuntimeException("MasterRegion.reset Resetting master region which still has chunk regions");
			}

			IsoRegions.warn("MasterRegion.reset Resetting master region which still has chunk regions");
			for (int int1 = 0; int1 < this.isoChunkRegions.size(); ++int1) {
				IsoChunkRegion chunkRegion = (IsoChunkRegion)this.isoChunkRegions.get(int1);
				chunkRegion.setIsoWorldRegion((IsoWorldRegion)null);
			}

			this.isoChunkRegions.clear();
		}

		return this;
	}

	public void unlinkNeighbors() {
		for (int int1 = 0; int1 < this.neighbors.size(); ++int1) {
			IsoWorldRegion worldRegion = (IsoWorldRegion)this.neighbors.get(int1);
			worldRegion.removeNeighbor(this);
		}

		this.neighbors.clear();
	}

	public void linkNeighbors() {
		for (int int1 = 0; int1 < this.isoChunkRegions.size(); ++int1) {
			IsoChunkRegion chunkRegion = (IsoChunkRegion)this.isoChunkRegions.get(int1);
			for (int int2 = 0; int2 < chunkRegion.getAllNeighbors().size(); ++int2) {
				IsoChunkRegion chunkRegion2 = (IsoChunkRegion)chunkRegion.getAllNeighbors().get(int2);
				if (chunkRegion2.getIsoWorldRegion() != null && chunkRegion2.getIsoWorldRegion() != this) {
					this.addNeighbor(chunkRegion2.getIsoWorldRegion());
					chunkRegion2.getIsoWorldRegion().addNeighbor(this);
				}
			}
		}
	}

	private void addNeighbor(IsoWorldRegion worldRegion) {
		if (!this.neighbors.contains(worldRegion)) {
			this.neighbors.add(worldRegion);
		}
	}

	private void removeNeighbor(IsoWorldRegion worldRegion) {
		this.neighbors.remove(worldRegion);
	}

	public ArrayList getNeighbors() {
		return this.neighbors;
	}

	public ArrayList getDebugConnectedNeighborCopy() {
		ArrayList arrayList = new ArrayList();
		if (this.neighbors.size() == 0) {
			return arrayList;
		} else {
			arrayList.addAll(this.neighbors);
			return arrayList;
		}
	}

	public boolean isFogMask() {
		return this.isEnclosed() && this.isFullyRoofed();
	}

	public boolean isPlayerRoom() {
		return this.isFogMask();
	}

	public boolean isFullyRoofed() {
		return this.roofCnt == this.squareSize;
	}

	public float getRoofedPercentage() {
		return this.squareSize == 0 ? 0.0F : (float)this.roofCnt / (float)this.squareSize;
	}

	public int getRoofCnt() {
		return this.roofCnt;
	}

	protected void addRoof() {
		++this.roofCnt;
		if (this.roofCnt > this.squareSize) {
			IsoRegions.warn("WorldRegion.addRoof roofCount exceed squareSize.");
			this.roofCnt = this.squareSize;
		}
	}

	protected void removeRoofs(int int1) {
		if (int1 > 0) {
			this.roofCnt -= int1;
			if (this.roofCnt < 0) {
				IsoRegions.warn("MasterRegion.removeRoofs Roofcount managed to get below zero.");
				this.roofCnt = 0;
			}
		}
	}

	public void addIsoChunkRegion(IsoChunkRegion chunkRegion) {
		if (!this.isoChunkRegions.contains(chunkRegion)) {
			this.squareSize += chunkRegion.getSquareSize();
			this.roofCnt += chunkRegion.getRoofCnt();
			this.isDirtyEnclosed = true;
			this.isoChunkRegions.add(chunkRegion);
			chunkRegion.setIsoWorldRegion(this);
		}
	}

	protected void removeIsoChunkRegion(IsoChunkRegion chunkRegion) {
		if (this.isoChunkRegions.remove(chunkRegion)) {
			this.squareSize -= chunkRegion.getSquareSize();
			this.roofCnt -= chunkRegion.getRoofCnt();
			this.isDirtyEnclosed = true;
			chunkRegion.setIsoWorldRegion((IsoWorldRegion)null);
		}
	}

	public boolean containsIsoChunkRegion(IsoChunkRegion chunkRegion) {
		return this.isoChunkRegions.contains(chunkRegion);
	}

	public ArrayList swapIsoChunkRegions(ArrayList arrayList) {
		ArrayList arrayList2 = this.isoChunkRegions;
		this.isoChunkRegions = arrayList;
		return arrayList2;
	}

	protected void resetSquareSize() {
		this.squareSize = 0;
	}

	protected void setDirtyEnclosed() {
		this.isDirtyEnclosed = true;
	}

	public boolean isEnclosed() {
		if (this.isDirtyEnclosed) {
			this.recalcEnclosed();
		}

		return this.enclosed;
	}

	private void recalcEnclosed() {
		this.isDirtyEnclosed = false;
		this.enclosed = true;
		for (int int1 = 0; int1 < this.isoChunkRegions.size(); ++int1) {
			IsoChunkRegion chunkRegion = (IsoChunkRegion)this.isoChunkRegions.get(int1);
			if (!chunkRegion.getIsEnclosed()) {
				this.enclosed = false;
			}
		}
	}

	public void merge(IsoWorldRegion worldRegion) {
		int int1;
		if (worldRegion.isoChunkRegions.size() > 0) {
			for (int1 = worldRegion.isoChunkRegions.size() - 1; int1 >= 0; --int1) {
				IsoChunkRegion chunkRegion = (IsoChunkRegion)worldRegion.isoChunkRegions.get(int1);
				worldRegion.removeIsoChunkRegion(chunkRegion);
				this.addIsoChunkRegion(chunkRegion);
			}

			this.isDirtyEnclosed = true;
			worldRegion.isoChunkRegions.clear();
		}

		if (worldRegion.neighbors.size() > 0) {
			for (int1 = 0; int1 < worldRegion.neighbors.size(); ++int1) {
				IsoWorldRegion worldRegion2 = (IsoWorldRegion)worldRegion.neighbors.get(int1);
				worldRegion2.removeNeighbor(worldRegion);
				this.addNeighbor(worldRegion2);
			}

			worldRegion.neighbors.clear();
		}

		this.manager.releaseIsoWorldRegion(worldRegion);
	}

	public ArrayList getDebugIsoChunkRegionCopy() {
		ArrayList arrayList = new ArrayList();
		arrayList.addAll(this.isoChunkRegions);
		return arrayList;
	}
}
