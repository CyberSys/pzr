package zombie.iso.areas.isoregion.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import zombie.core.Colors;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import zombie.iso.areas.isoregion.regions.IsoRegionManager;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;


public final class DataRoot {
	private final Map cellMap = new HashMap();
	public final DataRoot.SelectInfo select = new DataRoot.SelectInfo(this);
	private final DataRoot.SelectInfo selectInternal = new DataRoot.SelectInfo(this);
	public final IsoRegionManager regionManager = new IsoRegionManager(this);
	private final ArrayList dirtyIsoWorldRegions = new ArrayList();
	private final ArrayList dirtyChunks = new ArrayList();
	protected static int recalcs;
	protected static int floodFills;
	protected static int merges;
	private static final long[] t_start = new long[5];
	private static final long[] t_end = new long[5];
	private static final long[] t_time = new long[5];

	public void getAllChunks(List list) {
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
		DataCell dataCell = new DataCell(this, int1, int2, int3);
		this.cellMap.put(int3, dataCell);
		return dataCell;
	}

	public DataChunk getDataChunk(int int1, int int2) {
		int int3 = IsoRegions.hash(int1 / 30, int2 / 30);
		DataCell dataCell = (DataCell)this.cellMap.get(int3);
		if (dataCell != null) {
			int int4 = IsoRegions.hash(int1, int2);
			return dataCell.getChunk(int4);
		} else {
			return null;
		}
	}

	private void setDataChunk(DataChunk dataChunk) {
		int int1 = IsoRegions.hash(dataChunk.getChunkX() / 30, dataChunk.getChunkY() / 30);
		DataCell dataCell = (DataCell)this.cellMap.get(int1);
		if (dataCell == null) {
			dataCell = this.addCell(dataChunk.getChunkX() / 30, dataChunk.getChunkY() / 30, int1);
		}

		dataCell.setChunk(dataChunk);
	}

	public IsoWorldRegion getIsoWorldRegion(int int1, int int2, int int3) {
		this.selectInternal.reset(int1, int2, int3, false);
		if (this.selectInternal.chunk != null) {
			IsoChunkRegion chunkRegion = this.selectInternal.chunk.getIsoChunkRegion(this.selectInternal.chunkSquareX, this.selectInternal.chunkSquareY, int3);
			if (chunkRegion != null) {
				return chunkRegion.getIsoWorldRegion();
			}
		}

		return null;
	}

	public byte getSquareFlags(int int1, int int2, int int3) {
		this.selectInternal.reset(int1, int2, int3, false);
		return this.selectInternal.square;
	}

	public IsoChunkRegion getIsoChunkRegion(int int1, int int2, int int3) {
		this.selectInternal.reset(int1, int2, int3, false);
		return this.selectInternal.chunk != null ? this.selectInternal.chunk.getIsoChunkRegion(this.selectInternal.chunkSquareX, this.selectInternal.chunkSquareY, int3) : null;
	}

	public void resetAllData() {
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
						IsoChunkRegion chunkRegion = (IsoChunkRegion)iterator3.next();
						if (chunkRegion.getIsoWorldRegion() != null && !arrayList.contains(chunkRegion.getIsoWorldRegion())) {
							arrayList.add(chunkRegion.getIsoWorldRegion());
						}

						chunkRegion.setIsoWorldRegion((IsoWorldRegion)null);
						this.regionManager.releaseIsoChunkRegion(chunkRegion);
					}
				}
			}

			dataCell.dataChunks.clear();
		}

		this.cellMap.clear();
		iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			IsoWorldRegion worldRegion = (IsoWorldRegion)iterator.next();
			this.regionManager.releaseIsoWorldRegion(worldRegion);
		}
	}

	public void EnqueueDirtyDataChunk(DataChunk dataChunk) {
		if (!this.dirtyChunks.contains(dataChunk)) {
			this.dirtyChunks.add(dataChunk);
		}
	}

	public void EnqueueDirtyIsoWorldRegion(IsoWorldRegion worldRegion) {
		if (!this.dirtyIsoWorldRegions.contains(worldRegion)) {
			this.dirtyIsoWorldRegions.add(worldRegion);
		}
	}

	public void DequeueDirtyIsoWorldRegion(IsoWorldRegion worldRegion) {
		this.dirtyIsoWorldRegions.remove(worldRegion);
	}

	public void updateExistingSquare(int int1, int int2, int int3, byte byte1) {
		this.select.reset(int1, int2, int3, false);
		if (this.select.chunk != null) {
			byte byte2 = -1;
			if (this.select.square != -1) {
				byte2 = this.select.square;
			}

			if (byte1 == byte2) {
				return;
			}

			this.select.chunk.setOrAddSquare(this.select.chunkSquareX, this.select.chunkSquareY, this.select.z, byte1, true);
		} else {
			IsoRegions.warn("DataRoot.updateExistingSquare -> trying to change a square on a unknown chunk");
		}
	}

	public void processDirtyChunks() {
		if (this.dirtyChunks.size() > 0) {
			long long1 = System.nanoTime();
			recalcs = 0;
			floodFills = 0;
			merges = 0;
			t_start[0] = System.nanoTime();
			DataChunk dataChunk;
			int int1;
			for (int1 = 0; int1 < this.dirtyChunks.size(); ++int1) {
				dataChunk = (DataChunk)this.dirtyChunks.get(int1);
				dataChunk.recalculate();
				++recalcs;
			}

			t_end[0] = System.nanoTime();
			t_start[1] = System.nanoTime();
			for (int1 = 0; int1 < this.dirtyChunks.size(); ++int1) {
				dataChunk = (DataChunk)this.dirtyChunks.get(int1);
				DataChunk dataChunk2 = this.getDataChunk(dataChunk.getChunkX(), dataChunk.getChunkY() - 1);
				DataChunk dataChunk3 = this.getDataChunk(dataChunk.getChunkX() - 1, dataChunk.getChunkY());
				DataChunk dataChunk4 = this.getDataChunk(dataChunk.getChunkX(), dataChunk.getChunkY() + 1);
				DataChunk dataChunk5 = this.getDataChunk(dataChunk.getChunkX() + 1, dataChunk.getChunkY());
				dataChunk.link(dataChunk2, dataChunk3, dataChunk4, dataChunk5);
			}

			t_end[1] = System.nanoTime();
			t_start[2] = System.nanoTime();
			for (int1 = 0; int1 < this.dirtyChunks.size(); ++int1) {
				dataChunk = (DataChunk)this.dirtyChunks.get(int1);
				dataChunk.interConnect();
			}

			t_end[2] = System.nanoTime();
			t_start[3] = System.nanoTime();
			for (int1 = 0; int1 < this.dirtyChunks.size(); ++int1) {
				dataChunk = (DataChunk)this.dirtyChunks.get(int1);
				dataChunk.recalcRoofs();
				dataChunk.unsetDirtyAll();
			}

			t_end[3] = System.nanoTime();
			t_start[4] = System.nanoTime();
			if (this.dirtyIsoWorldRegions.size() > 0) {
				int int2;
				IsoWorldRegion worldRegion;
				for (int2 = 0; int2 < this.dirtyIsoWorldRegions.size(); ++int2) {
					worldRegion = (IsoWorldRegion)this.dirtyIsoWorldRegions.get(int2);
					worldRegion.unlinkNeighbors();
				}

				for (int2 = 0; int2 < this.dirtyIsoWorldRegions.size(); ++int2) {
					worldRegion = (IsoWorldRegion)this.dirtyIsoWorldRegions.get(int2);
					worldRegion.linkNeighbors();
				}

				this.dirtyIsoWorldRegions.clear();
			}

			t_end[4] = System.nanoTime();
			this.dirtyChunks.clear();
			long long2 = System.nanoTime();
			long long3 = long2 - long1;
			if (IsoRegions.PRINT_D) {
				t_time[0] = t_end[0] - t_start[0];
				t_time[1] = t_end[1] - t_start[1];
				t_time[2] = t_end[2] - t_start[2];
				t_time[3] = t_end[3] - t_start[3];
				t_time[4] = t_end[4] - t_start[4];
				String string = String.format("%.6f", (double)long3 / 1000000.0);
				IsoRegions.log("--- IsoRegion update: " + string + " ms, recalc: " + String.format("%.6f", (double)t_time[0] / 1000000.0) + " ms, link: " + String.format("%.6f", (double)t_time[1] / 1000000.0) + " ms, interconnect: " + String.format("%.6f", (double)t_time[2] / 1000000.0) + " ms, roofs: " + String.format("%.6f", (double)t_time[3] / 1000000.0) + " ms, worldRegion: " + String.format("%.6f", (double)t_time[4] / 1000000.0) + " ms, recalcs = " + recalcs + ", merges = " + merges + ", floodfills = " + floodFills, Colors.CornFlowerBlue);
			}
		}
	}

	public static final class SelectInfo {
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

		public void reset(int int1, int int2, int int3, boolean boolean1) {
			this.reset(int1, int2, int3, boolean1, boolean1);
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
			this.chunkID = IsoRegions.hash(this.chunkx, this.chunky);
			this.cellID = IsoRegions.hash(this.cellx, this.celly);
			this.cell = null;
			this.chunk = null;
			this.square = -1;
			this.ensureSquare(boolean2);
			if (this.chunk == null && boolean1) {
				this.ensureChunk(boolean1);
			}
		}

		private void ensureCell(boolean boolean1) {
			if (this.cell == null) {
				this.cell = this.root.getCell(this.cellID);
			}

			if (this.cell == null && boolean1) {
				this.cell = this.root.addCell(this.cellx, this.celly, this.cellID);
			}
		}

		private void ensureChunk(boolean boolean1) {
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

		private void ensureSquare(boolean boolean1) {
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
	}
}
