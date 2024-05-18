package zombie.iso.areas.isoregion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import zombie.debug.DebugLog;


public class DataRoot {
	private final Map cellMap = new HashMap();
	protected final DataRoot.SelectInfo select = new DataRoot.SelectInfo(this);
	private final ArrayList dirtyMasterRegions = new ArrayList();
	private final ArrayList dirtyChunks = new ArrayList();
	protected static int recalcs;
	protected static int floodFills;
	protected static int merges;

	protected void getAllChunks(List list) {
		Iterator iterator = this.cellMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			((DataCell)entry.getValue()).getAllChunks(list);
		}
	}

	private DataCell getCell(int int1) {
		return (DataCell)this.cellMap.get(int1);
	}

	private DataCell addCell(int int1, int int2, int int3) {
		DataCell dataCell = new DataCell(int1, int2, int3);
		this.cellMap.put(int3, dataCell);
		return dataCell;
	}

	public DataChunk getDataChunk(int int1, int int2) {
		int int3 = IsoRegion.hash(int1 / 30, int2 / 30);
		DataCell dataCell = (DataCell)this.cellMap.get(int3);
		if (dataCell != null) {
			int int4 = IsoRegion.hash(int1, int2);
			return dataCell.getChunk(int4);
		} else {
			return null;
		}
	}

	public void setDataChunk(DataChunk dataChunk) {
		int int1 = IsoRegion.hash(dataChunk.getChunkX() / 30, dataChunk.getChunkY() / 30);
		DataCell dataCell = (DataCell)this.cellMap.get(int1);
		if (dataCell == null) {
			dataCell = this.addCell(dataChunk.getChunkX() / 30, dataChunk.getChunkY() / 30, int1);
		}

		if (dataCell != null) {
			dataCell.setChunk(dataChunk);
		}
	}

	public byte getDataSquare(int int1, int int2, int int3) {
		this.select.reset(int1, int2, int3, true, false);
		return this.select.square;
	}

	public MasterRegion getMasterRegion(int int1, int int2, int int3) {
		this.select.reset(int1, int2, int3, true, false);
		if (this.select.chunk != null) {
			ChunkRegion chunkRegion = this.select.chunk.getRegion(this.select.chunkSquareX, this.select.chunkSquareY, int3);
			if (chunkRegion != null) {
				return chunkRegion.getMasterRegion();
			}
		}

		return null;
	}

	public byte getSquareFlags(int int1, int int2, int int3) {
		this.select.reset(int1, int2, int3, true, false);
		return this.select.square;
	}

	public ChunkRegion getChunkRegion(int int1, int int2, int int3) {
		this.select.reset(int1, int2, int3, true, false);
		return this.select.chunk != null ? this.select.chunk.getRegion(this.select.chunkSquareX, this.select.chunkSquareY, int3) : null;
	}

	protected void resetAllData() {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.cellMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			DataCell dataCell = (DataCell)entry.getValue();
			Iterator iterator2 = dataCell.dataChunks.entrySet().iterator();
			while (iterator2.hasNext()) {
				Entry entry2 = (Entry)iterator2.next();
				DataChunk dataChunk = (DataChunk)entry2.getValue();
				for (int int1 = 0; int1 < 8; ++int1) {
					Iterator iterator3 = dataChunk.getChunkRegions(int1).iterator();
					while (iterator3.hasNext()) {
						ChunkRegion chunkRegion = (ChunkRegion)iterator3.next();
						if (chunkRegion.getMasterRegion() != null && !arrayList.contains(chunkRegion.getMasterRegion())) {
							arrayList.add(chunkRegion.getMasterRegion());
						}

						chunkRegion.setMasterRegion((MasterRegion)null);
						ChunkRegion.release(chunkRegion);
					}
				}
			}

			dataCell.dataChunks.clear();
		}

		this.cellMap.clear();
		iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			MasterRegion masterRegion = (MasterRegion)iterator.next();
			MasterRegion.release(masterRegion);
		}
	}

	protected void EnqueueDirtyChunk(DataChunk dataChunk) {
		if (!this.dirtyChunks.contains(dataChunk)) {
			this.dirtyChunks.add(dataChunk);
		}
	}

	protected void EnqueueDirtyMasterRegion(MasterRegion masterRegion) {
		if (!this.dirtyMasterRegions.contains(masterRegion)) {
			this.dirtyMasterRegions.add(masterRegion);
		}
	}

	protected void DequeueDirtyMasterRegion(MasterRegion masterRegion) {
		if (this.dirtyMasterRegions.contains(masterRegion)) {
			this.dirtyMasterRegions.remove(masterRegion);
		}
	}

	protected void processDirtyChunks() {
		if (this.dirtyChunks.size() > 0) {
			long long1 = System.nanoTime();
			recalcs = 0;
			floodFills = 0;
			merges = 0;
			Iterator iterator;
			DataChunk dataChunk;
			for (iterator = this.dirtyChunks.iterator(); iterator.hasNext(); ++recalcs) {
				dataChunk = (DataChunk)iterator.next();
				dataChunk.recalculate();
			}

			iterator = this.dirtyChunks.iterator();
			while (iterator.hasNext()) {
				dataChunk = (DataChunk)iterator.next();
				DataChunk dataChunk2 = this.getDataChunk(dataChunk.getChunkX(), dataChunk.getChunkY() - 1);
				DataChunk dataChunk3 = this.getDataChunk(dataChunk.getChunkX() - 1, dataChunk.getChunkY());
				DataChunk dataChunk4 = this.getDataChunk(dataChunk.getChunkX(), dataChunk.getChunkY() + 1);
				DataChunk dataChunk5 = this.getDataChunk(dataChunk.getChunkX() + 1, dataChunk.getChunkY());
				dataChunk.link(dataChunk2, dataChunk3, dataChunk4, dataChunk5);
			}

			iterator = this.dirtyChunks.iterator();
			while (iterator.hasNext()) {
				dataChunk = (DataChunk)iterator.next();
				dataChunk.interConnect();
			}

			iterator = this.dirtyChunks.iterator();
			while (iterator.hasNext()) {
				dataChunk = (DataChunk)iterator.next();
				dataChunk.recalcRoofs();
				dataChunk.unsetDirtyAndFloodAll();
			}

			if (this.dirtyMasterRegions.size() > 0) {
				iterator = this.dirtyMasterRegions.iterator();
				MasterRegion masterRegion;
				while (iterator.hasNext()) {
					masterRegion = (MasterRegion)iterator.next();
					masterRegion.unlinkNeighbors();
				}

				iterator = this.dirtyMasterRegions.iterator();
				while (iterator.hasNext()) {
					masterRegion = (MasterRegion)iterator.next();
					masterRegion.linkNeighbors();
				}

				this.dirtyMasterRegions.clear();
			}

			this.dirtyChunks.clear();
			long long2 = System.nanoTime();
			long long3 = long2 - long1;
			if (IsoRegion.PRINT_D) {
				DebugLog.log("--- IsoRegion update: " + String.format("%.6f", (double)long3 / 1000000.0) + "ms, recalcs = " + recalcs + ", merges = " + merges + ", floodfills = " + floodFills);
			}
		}
	}

	protected static class SelectInfo {
		public int x;
		public int y;
		public int z;
		public int chunkSquareX;
		public int chunkSquareY;
		public int chunkx;
		public int chunky;
		public int cellx;
		public int celly;
		public int chunkID;
		public int cellID;
		public DataCell cell;
		public DataChunk chunk;
		public byte square;
		private final DataRoot root;

		private SelectInfo(DataRoot dataRoot) {
			this.root = dataRoot;
		}

		public void reset(int int1, int int2, int int3, boolean boolean1, boolean boolean2) {
			this.x = int1;
			this.y = int2;
			this.z = int3;
			this.chunkSquareX = int1 % 10;
			this.chunkSquareY = int2 % 10;
			this.chunkx = int1 / 10;
			this.chunky = int2 / 10;
			this.cellx = int1 / 300;
			this.celly = int2 / 300;
			this.chunkID = IsoRegion.hash(this.chunkx, this.chunky);
			this.cellID = IsoRegion.hash(this.cellx, this.celly);
			this.cell = null;
			this.chunk = null;
			this.square = -1;
			if (boolean1) {
				this.ensureSquare(boolean2);
			}
		}

		public void ensureCell(boolean boolean1) {
			if (this.cell == null) {
				this.cell = this.root.getCell(this.cellID);
			}

			if (this.cell == null && boolean1) {
				this.cell = this.root.addCell(this.cellx, this.celly, this.cellID);
			}
		}

		public void ensureChunk(boolean boolean1) {
			this.ensureCell(boolean1);
			if (this.cell != null) {
				if (this.chunk == null) {
					this.chunk = this.cell.getChunk(this.chunkID);
				}

				if (this.chunk == null && boolean1) {
					this.chunk = this.cell.addChunk(this.chunkx, this.chunky, this.chunkID);
				}
			}
		}

		public void ensureSquare(boolean boolean1) {
			this.ensureCell(boolean1);
			if (this.cell != null) {
				this.ensureChunk(boolean1);
				if (this.chunk != null) {
					if (this.square == -1) {
						this.square = this.chunk.getSquare(this.chunkSquareX, this.chunkSquareY, this.z, true);
					}

					if (this.square == -1 && boolean1) {
						this.square = this.chunk.setOrAddSquare(this.chunkSquareX, this.chunkSquareY, this.z, (byte)0, true);
					}
				}
			}
		}

		SelectInfo(DataRoot dataRoot, Object object) {
			this(dataRoot);
		}
	}
}
