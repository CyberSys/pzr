package zombie.iso.areas.isoregion;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.debug.DebugLog;


public class MasterRegion {
	private static int totalCreated = 0;
	private static int totalReleased = 0;
	private static int totalReused = 0;
	private static int nextID = 0;
	private static ArrayDeque idStack = new ArrayDeque();
	private static ArrayDeque pool = new ArrayDeque();
	private boolean isInPool = false;
	private int ID;
	private Color color;
	private boolean enclosed = true;
	private ArrayList chunkRegions = new ArrayList();
	private int squareSize = 0;
	private int roofCnt = 0;
	private boolean isDirtyEnclosed = false;
	private boolean isDirtyRoofed = false;
	private ArrayList neighbors = new ArrayList();

	protected static void printStats() {
		DebugLog.log("MasterRegion: Created: " + totalCreated + ", Re-used: " + totalReused + ", Released: " + totalReleased + ", InPool: " + pool.size() + ", nextID: " + nextID);
	}

	static MasterRegion alloc() {
		MasterRegion masterRegion = !pool.isEmpty() ? (MasterRegion)pool.pop() : null;
		if (masterRegion == null) {
			masterRegion = new MasterRegion();
			++totalCreated;
		} else {
			++totalReused;
		}

		masterRegion.isInPool = false;
		masterRegion.ID = idStack.isEmpty() ? nextID++ : (Integer)idStack.pop();
		if (masterRegion.color == null) {
			masterRegion.color = Colors.GetRandomColor();
		}

		masterRegion.squareSize = 0;
		masterRegion.roofCnt = 0;
		masterRegion.enclosed = true;
		masterRegion.isDirtyEnclosed = false;
		masterRegion.isDirtyRoofed = false;
		return masterRegion;
	}

	static void release(MasterRegion masterRegion) {
		assert !pool.contains(masterRegion);
		if (IsoRegion.PRINT_D && pool.contains(masterRegion)) {
			DebugLog.log("Warning: MasterRegion.release Trying to release a MasterRegion twice.");
		} else {
			IsoRegionWorker.DequeueDirtyMasterRegion(masterRegion);
			if (!masterRegion.isInPool) {
				pool.push(masterRegion.reset());
				++totalReleased;
			}
		}
	}

	private MasterRegion() {
	}

	private MasterRegion reset() {
		this.isInPool = true;
		if (this.ID >= 0) {
			idStack.push(this.ID);
		}

		this.ID = -1;
		this.squareSize = 0;
		this.roofCnt = 0;
		this.enclosed = true;
		this.isDirtyRoofed = false;
		this.isDirtyEnclosed = false;
		this.unlinkNeighbors();
		if (this.chunkRegions.size() > 0) {
			if (IsoRegion.PRINT_D) {
				DebugLog.log("WARNING: MasterRegion.reset Resetting master region with chunkregions set");
			}

			Iterator iterator = this.chunkRegions.iterator();
			while (iterator.hasNext()) {
				ChunkRegion chunkRegion = (ChunkRegion)iterator.next();
				chunkRegion.setMasterRegion((MasterRegion)null);
			}

			this.chunkRegions.clear();
		}

		return this;
	}

	public int getID() {
		return this.ID;
	}

	public Color getColor() {
		return this.color;
	}

	public int size() {
		return this.chunkRegions.size();
	}

	public int getSquareSize() {
		return this.squareSize;
	}

	protected void unlinkNeighbors() {
		Iterator iterator = this.neighbors.iterator();
		while (iterator.hasNext()) {
			MasterRegion masterRegion = (MasterRegion)iterator.next();
			masterRegion.removeNeighbor(this);
		}

		this.neighbors.clear();
	}

	protected void linkNeighbors() {
		Iterator iterator = this.chunkRegions.iterator();
		while (iterator.hasNext()) {
			ChunkRegion chunkRegion = (ChunkRegion)iterator.next();
			Iterator iterator2 = chunkRegion.getAllNeighbors().iterator();
			while (iterator2.hasNext()) {
				ChunkRegion chunkRegion2 = (ChunkRegion)iterator2.next();
				if (chunkRegion2.getMasterRegion() != null && chunkRegion2.getMasterRegion() != this) {
					this.addNeighbor(chunkRegion2.getMasterRegion());
					chunkRegion2.getMasterRegion().addNeighbor(this);
				}
			}
		}
	}

	private void addNeighbor(MasterRegion masterRegion) {
		if (!this.neighbors.contains(masterRegion)) {
			this.neighbors.add(masterRegion);
		}
	}

	private void removeNeighbor(MasterRegion masterRegion) {
		this.neighbors.remove(masterRegion);
	}

	public ArrayList getNeighbors() {
		return this.neighbors;
	}

	public ArrayList getDebugConnectedNeighborCopy() {
		ArrayList arrayList = new ArrayList();
		if (this.neighbors.size() == 0) {
			return arrayList;
		} else {
			Iterator iterator = this.neighbors.iterator();
			while (iterator.hasNext()) {
				MasterRegion masterRegion = (MasterRegion)iterator.next();
				arrayList.add(masterRegion);
			}

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

	public int getRoofCnt() {
		return this.roofCnt;
	}

	protected void addRoof() {
		++this.roofCnt;
		if (this.roofCnt > this.squareSize) {
			this.roofCnt = this.squareSize;
		}
	}

	protected void removeRoofs(int int1) {
		if (int1 > 0) {
			this.roofCnt -= int1;
			if (this.roofCnt < 0) {
				if (IsoRegion.PRINT_D) {
					DebugLog.log("WARNING: MasterRegion.removeRoofs Roofcount managed to get below zero.");
				}

				this.roofCnt = 0;
			}
		}
	}

	protected void addChunkRegion(ChunkRegion chunkRegion) {
		if (!this.chunkRegions.contains(chunkRegion)) {
			this.squareSize += chunkRegion.getSquareSize();
			this.roofCnt += chunkRegion.getRoofCnt();
			this.isDirtyEnclosed = true;
			this.chunkRegions.add(chunkRegion);
			chunkRegion.setMasterRegion(this);
		}
	}

	protected void removeChunkRegion(ChunkRegion chunkRegion) {
		if (this.chunkRegions.remove(chunkRegion)) {
			this.squareSize -= chunkRegion.getSquareSize();
			this.roofCnt -= chunkRegion.getRoofCnt();
			this.isDirtyEnclosed = true;
			chunkRegion.setMasterRegion((MasterRegion)null);
		}
	}

	protected boolean containsChunkRegion(ChunkRegion chunkRegion) {
		return this.chunkRegions.contains(chunkRegion);
	}

	protected ArrayList swapChunkRegions(ArrayList arrayList) {
		ArrayList arrayList2 = this.chunkRegions;
		this.chunkRegions = arrayList;
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
		Iterator iterator = this.chunkRegions.iterator();
		while (iterator.hasNext()) {
			ChunkRegion chunkRegion = (ChunkRegion)iterator.next();
			if (!chunkRegion.getIsEnclosed()) {
				this.enclosed = false;
			}
		}
	}

	protected void merge(MasterRegion masterRegion) {
		int int1;
		if (masterRegion.chunkRegions.size() > 0) {
			for (int1 = masterRegion.chunkRegions.size() - 1; int1 >= 0; --int1) {
				ChunkRegion chunkRegion = (ChunkRegion)masterRegion.chunkRegions.get(int1);
				masterRegion.removeChunkRegion(chunkRegion);
				this.addChunkRegion(chunkRegion);
			}

			this.isDirtyEnclosed = true;
			masterRegion.chunkRegions.clear();
		}

		if (masterRegion.neighbors.size() > 0) {
			for (int1 = masterRegion.neighbors.size() - 1; int1 >= 0; --int1) {
				MasterRegion masterRegion2 = (MasterRegion)masterRegion.neighbors.get(int1);
				masterRegion2.removeNeighbor(masterRegion);
				this.addNeighbor(masterRegion2);
			}

			masterRegion.neighbors.clear();
		}

		release(masterRegion);
	}

	public ArrayList getDebugChunkRegionCopy() {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.chunkRegions.iterator();
		while (iterator.hasNext()) {
			ChunkRegion chunkRegion = (ChunkRegion)iterator.next();
			arrayList.add(chunkRegion);
		}

		return arrayList;
	}
}
