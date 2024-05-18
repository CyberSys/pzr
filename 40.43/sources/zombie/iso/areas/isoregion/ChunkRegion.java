package zombie.iso.areas.isoregion;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.debug.DebugLog;


public class ChunkRegion {
	private static int totalCreated = 0;
	private static int totalReleased = 0;
	private static int totalReused = 0;
	private static int nextID = 0;
	private static ArrayDeque idStack = new ArrayDeque();
	private static ArrayDeque pool = new ArrayDeque();
	private boolean isInPool = false;
	private Color color;
	private int ID;
	private int zLayer;
	private int squareSize = 0;
	private int roofCnt = 0;
	private int chunkBorderSquaresCnt = 0;
	private boolean[] enclosed = new boolean[4];
	private boolean enclosedCache = true;
	private ArrayList connectedNeighbors = new ArrayList();
	private ArrayList allNeighbors = new ArrayList();
	private boolean isDirtyEnclosed = false;
	private boolean isDirtyRoofed = false;
	private MasterRegion masterRegion;

	protected static void printStats() {
		DebugLog.log("ChunkRegion: Created: " + totalCreated + ", Re-used: " + totalReused + ", Released: " + totalReleased + ", InPool: " + pool.size() + ", nextID: " + nextID);
	}

	static ChunkRegion alloc(int int1) {
		ChunkRegion chunkRegion = !pool.isEmpty() ? (ChunkRegion)pool.pop() : null;
		if (chunkRegion == null) {
			chunkRegion = new ChunkRegion();
			++totalCreated;
		} else {
			++totalReused;
		}

		chunkRegion.isInPool = false;
		chunkRegion.ID = idStack.isEmpty() ? nextID++ : (Integer)idStack.pop();
		chunkRegion.zLayer = int1;
		chunkRegion.resetChunkBorderSquaresCnt();
		if (chunkRegion.color == null) {
			chunkRegion.color = Colors.GetRandomColor();
		}

		chunkRegion.squareSize = 0;
		chunkRegion.roofCnt = 0;
		chunkRegion.resetEnclosed();
		chunkRegion.isDirtyRoofed = false;
		return chunkRegion;
	}

	static void release(ChunkRegion chunkRegion) {
		assert !pool.contains(chunkRegion);
		if (IsoRegion.PRINT_D && pool.contains(chunkRegion)) {
			DebugLog.log("Warning: ChunkRegion.release Trying to release a ChunkRegion twice.");
		} else {
			if (!chunkRegion.isInPool) {
				pool.push(chunkRegion.reset());
				++totalReleased;
			}
		}
	}

	private ChunkRegion() {
	}

	private ChunkRegion reset() {
		this.isInPool = true;
		this.unlinkNeighbors();
		MasterRegion masterRegion = this.unlinkFromMaster();
		if (masterRegion != null && masterRegion.size() <= 0) {
			MasterRegion.release(masterRegion);
			if (IsoRegion.PRINT_D) {
				DebugLog.log("Warning: ChunkRegion.reset MasterRegion with 0 members.");
			}
		}

		this.resetChunkBorderSquaresCnt();
		if (this.ID >= 0) {
			idStack.push(this.ID);
		}

		this.ID = -1;
		this.squareSize = 0;
		this.roofCnt = 0;
		this.resetEnclosed();
		this.isDirtyRoofed = false;
		return this;
	}

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

	public MasterRegion getMasterRegion() {
		return this.masterRegion;
	}

	protected void setMasterRegion(MasterRegion masterRegion) {
		this.masterRegion = masterRegion;
	}

	protected MasterRegion unlinkFromMaster() {
		if (this.masterRegion != null) {
			MasterRegion masterRegion = this.masterRegion;
			this.masterRegion.removeChunkRegion(this);
			this.masterRegion = null;
			return masterRegion;
		} else {
			return null;
		}
	}

	public int getRoofCnt() {
		return this.roofCnt > this.squareSize ? this.squareSize : this.roofCnt;
	}

	protected void addRoof() {
		++this.roofCnt;
		if (this.roofCnt > this.squareSize) {
			this.roofCnt = this.squareSize;
		}

		if (this.masterRegion != null) {
			this.masterRegion.addRoof();
		}
	}

	protected void resetRoofCnt() {
		if (this.masterRegion != null) {
			this.masterRegion.removeRoofs(this.roofCnt);
		}

		this.roofCnt = 0;
	}

	protected void addSquareCount() {
		++this.squareSize;
	}

	public int getChunkBorderSquaresCnt() {
		return this.chunkBorderSquaresCnt;
	}

	protected void addChunkBorderSquaresCnt() {
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

	protected void setEnclosed(byte byte1, boolean boolean1) {
		this.isDirtyEnclosed = true;
		this.enclosed[byte1] = boolean1;
	}

	protected void setDirtyEnclosed() {
		this.isDirtyEnclosed = true;
		if (this.masterRegion != null) {
			this.masterRegion.setDirtyEnclosed();
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

			if (this.masterRegion != null) {
				this.masterRegion.setDirtyEnclosed();
			}

			return this.enclosedCache;
		}
	}

	protected ArrayList getConnectedNeighbors() {
		return this.connectedNeighbors;
	}

	protected void addConnectedNeighbor(ChunkRegion chunkRegion) {
		if (chunkRegion != null) {
			if (!this.connectedNeighbors.contains(chunkRegion)) {
				this.connectedNeighbors.add(chunkRegion);
			}
		}
	}

	protected void removeConnectedNeighbor(ChunkRegion chunkRegion) {
		this.connectedNeighbors.remove(chunkRegion);
	}

	protected ArrayList getAllNeighbors() {
		return this.allNeighbors;
	}

	protected void addNeighbor(ChunkRegion chunkRegion) {
		if (chunkRegion != null) {
			if (!this.allNeighbors.contains(chunkRegion)) {
				this.allNeighbors.add(chunkRegion);
			}
		}
	}

	protected void removeNeighbor(ChunkRegion chunkRegion) {
		this.allNeighbors.remove(chunkRegion);
	}

	protected void unlinkNeighbors() {
		Iterator iterator = this.connectedNeighbors.iterator();
		ChunkRegion chunkRegion;
		while (iterator.hasNext()) {
			chunkRegion = (ChunkRegion)iterator.next();
			chunkRegion.removeConnectedNeighbor(this);
		}

		this.connectedNeighbors.clear();
		iterator = this.allNeighbors.iterator();
		while (iterator.hasNext()) {
			chunkRegion = (ChunkRegion)iterator.next();
			chunkRegion.removeNeighbor(this);
		}

		this.allNeighbors.clear();
	}

	public ArrayList getDebugConnectedNeighborCopy() {
		ArrayList arrayList = new ArrayList();
		if (this.connectedNeighbors.size() == 0) {
			return arrayList;
		} else {
			Iterator iterator = this.connectedNeighbors.iterator();
			while (iterator.hasNext()) {
				ChunkRegion chunkRegion = (ChunkRegion)iterator.next();
				arrayList.add(chunkRegion);
			}

			return arrayList;
		}
	}

	public boolean containsConnectedNeighbor(ChunkRegion chunkRegion) {
		return this.connectedNeighbors.contains(chunkRegion);
	}

	public boolean containsConnectedNeighborID(int int1) {
		if (this.connectedNeighbors.size() == 0) {
			return false;
		} else {
			Iterator iterator = this.connectedNeighbors.iterator();
			ChunkRegion chunkRegion;
			do {
				if (!iterator.hasNext()) {
					return false;
				}

				chunkRegion = (ChunkRegion)iterator.next();
			}	 while (chunkRegion.getID() != int1);

			return true;
		}
	}

	protected ChunkRegion getConnectedNeighborWithLargestMaster() {
		if (this.connectedNeighbors.size() == 0) {
			return null;
		} else {
			MasterRegion masterRegion = null;
			ChunkRegion chunkRegion = null;
			Iterator iterator = this.connectedNeighbors.iterator();
			while (true) {
				ChunkRegion chunkRegion2;
				MasterRegion masterRegion2;
				do {
					do {
						if (!iterator.hasNext()) {
							return chunkRegion;
						}

						chunkRegion2 = (ChunkRegion)iterator.next();
						masterRegion2 = chunkRegion2.getMasterRegion();
					}			 while (masterRegion2 == null);
				}		 while (masterRegion != null && masterRegion2.getSquareSize() <= masterRegion.getSquareSize());

				masterRegion = masterRegion2;
				chunkRegion = chunkRegion2;
			}
		}
	}

	protected ChunkRegion getFirstNeighborWithMaster() {
		if (this.connectedNeighbors.size() == 0) {
			return null;
		} else {
			Iterator iterator = this.connectedNeighbors.iterator();
			ChunkRegion chunkRegion;
			MasterRegion masterRegion;
			do {
				if (!iterator.hasNext()) {
					return null;
				}

				chunkRegion = (ChunkRegion)iterator.next();
				masterRegion = chunkRegion.getMasterRegion();
			}	 while (masterRegion == null);

			return chunkRegion;
		}
	}
}
