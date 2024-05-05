package zombie.globalObjects;

import java.util.ArrayList;
import java.util.Arrays;
import zombie.debug.DebugLog;
import zombie.iso.IsoMetaGrid;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class GlobalObjectLookup {
	private static final int SQUARES_PER_CHUNK = 10;
	private static final int SQUARES_PER_CELL = 300;
	private static final int CHUNKS_PER_CELL = 30;
	private static IsoMetaGrid metaGrid;
	private static final GlobalObjectLookup.Shared sharedServer = new GlobalObjectLookup.Shared();
	private static final GlobalObjectLookup.Shared sharedClient = new GlobalObjectLookup.Shared();
	private final GlobalObjectSystem system;
	private final GlobalObjectLookup.Shared shared;
	private final GlobalObjectLookup.Cell[] cells;

	public GlobalObjectLookup(GlobalObjectSystem globalObjectSystem) {
		this.system = globalObjectSystem;
		this.shared = globalObjectSystem instanceof SGlobalObjectSystem ? sharedServer : sharedClient;
		this.cells = this.shared.cells;
	}

	private GlobalObjectLookup.Cell getCellAt(int int1, int int2, boolean boolean1) {
		int int3 = int1 - metaGrid.minX * 300;
		int int4 = int2 - metaGrid.minY * 300;
		if (int3 >= 0 && int4 >= 0 && int3 < metaGrid.getWidth() * 300 && int4 < metaGrid.getHeight() * 300) {
			int int5 = int3 / 300;
			int int6 = int4 / 300;
			int int7 = int5 + int6 * metaGrid.getWidth();
			if (this.cells[int7] == null && boolean1) {
				this.cells[int7] = new GlobalObjectLookup.Cell(metaGrid.minX + int5, metaGrid.minY + int6);
			}

			return this.cells[int7];
		} else {
			DebugLog.log("ERROR: GlobalObjectLookup.getCellForObject object location invalid " + int1 + "," + int2);
			return null;
		}
	}

	private GlobalObjectLookup.Cell getCellForObject(GlobalObject globalObject, boolean boolean1) {
		return this.getCellAt(globalObject.x, globalObject.y, boolean1);
	}

	private GlobalObjectLookup.Chunk getChunkForChunkPos(int int1, int int2, boolean boolean1) {
		GlobalObjectLookup.Cell cell = this.getCellAt(int1 * 10, int2 * 10, boolean1);
		return cell == null ? null : cell.getChunkAt(int1 * 10, int2 * 10, boolean1);
	}

	public void addObject(GlobalObject globalObject) {
		GlobalObjectLookup.Cell cell = this.getCellForObject(globalObject, true);
		if (cell == null) {
			DebugLog.log("ERROR: GlobalObjectLookup.addObject object location invalid " + globalObject.x + "," + globalObject.y);
		} else {
			cell.addObject(globalObject);
		}
	}

	public void removeObject(GlobalObject globalObject) {
		GlobalObjectLookup.Cell cell = this.getCellForObject(globalObject, false);
		if (cell == null) {
			DebugLog.log("ERROR: GlobalObjectLookup.removeObject object location invalid " + globalObject.x + "," + globalObject.y);
		} else {
			cell.removeObject(globalObject);
		}
	}

	public GlobalObject getObjectAt(int int1, int int2, int int3) {
		GlobalObjectLookup.Cell cell = this.getCellAt(int1, int2, false);
		if (cell == null) {
			return null;
		} else {
			GlobalObjectLookup.Chunk chunk = cell.getChunkAt(int1, int2, false);
			if (chunk == null) {
				return null;
			} else {
				for (int int4 = 0; int4 < chunk.objects.size(); ++int4) {
					GlobalObject globalObject = (GlobalObject)chunk.objects.get(int4);
					if (globalObject.system == this.system && globalObject.x == int1 && globalObject.y == int2 && globalObject.z == int3) {
						return globalObject;
					}
				}

				return null;
			}
		}
	}

	public boolean hasObjectsInChunk(int int1, int int2) {
		GlobalObjectLookup.Chunk chunk = this.getChunkForChunkPos(int1, int2, false);
		if (chunk == null) {
			return false;
		} else {
			for (int int3 = 0; int3 < chunk.objects.size(); ++int3) {
				GlobalObject globalObject = (GlobalObject)chunk.objects.get(int3);
				if (globalObject.system == this.system) {
					return true;
				}
			}

			return false;
		}
	}

	public ArrayList getObjectsInChunk(int int1, int int2, ArrayList arrayList) {
		GlobalObjectLookup.Chunk chunk = this.getChunkForChunkPos(int1, int2, false);
		if (chunk == null) {
			return arrayList;
		} else {
			for (int int3 = 0; int3 < chunk.objects.size(); ++int3) {
				GlobalObject globalObject = (GlobalObject)chunk.objects.get(int3);
				if (globalObject.system == this.system) {
					arrayList.add(globalObject);
				}
			}

			return arrayList;
		}
	}

	public ArrayList getObjectsAdjacentTo(int int1, int int2, int int3, ArrayList arrayList) {
		for (int int4 = -1; int4 <= 1; ++int4) {
			for (int int5 = -1; int5 <= 1; ++int5) {
				GlobalObject globalObject = this.getObjectAt(int1 + int5, int2 + int4, int3);
				if (globalObject != null && globalObject.system == this.system) {
					arrayList.add(globalObject);
				}
			}
		}

		return arrayList;
	}

	public static void init(IsoMetaGrid metaGrid) {
		metaGrid = metaGrid;
		if (GameServer.bServer) {
			sharedServer.init(metaGrid);
		} else if (GameClient.bClient) {
			sharedClient.init(metaGrid);
		} else {
			sharedServer.init(metaGrid);
			sharedClient.init(metaGrid);
		}
	}

	public static void Reset() {
		sharedServer.reset();
		sharedClient.reset();
	}

	private static final class Shared {
		GlobalObjectLookup.Cell[] cells;

		void init(IsoMetaGrid metaGrid) {
			this.cells = new GlobalObjectLookup.Cell[metaGrid.getWidth() * metaGrid.getHeight()];
		}

		void reset() {
			if (this.cells != null) {
				for (int int1 = 0; int1 < this.cells.length; ++int1) {
					GlobalObjectLookup.Cell cell = this.cells[int1];
					if (cell != null) {
						cell.Reset();
					}
				}

				this.cells = null;
			}
		}
	}

	private static final class Cell {
		final int cx;
		final int cy;
		final GlobalObjectLookup.Chunk[] chunks = new GlobalObjectLookup.Chunk[900];

		Cell(int int1, int int2) {
			this.cx = int1;
			this.cy = int2;
		}

		GlobalObjectLookup.Chunk getChunkAt(int int1, int int2, boolean boolean1) {
			int int3 = (int1 - this.cx * 300) / 10;
			int int4 = (int2 - this.cy * 300) / 10;
			int int5 = int3 + int4 * 30;
			if (this.chunks[int5] == null && boolean1) {
				this.chunks[int5] = new GlobalObjectLookup.Chunk();
			}

			return this.chunks[int5];
		}

		GlobalObjectLookup.Chunk getChunkForObject(GlobalObject globalObject, boolean boolean1) {
			return this.getChunkAt(globalObject.x, globalObject.y, boolean1);
		}

		void addObject(GlobalObject globalObject) {
			GlobalObjectLookup.Chunk chunk = this.getChunkForObject(globalObject, true);
			if (chunk.objects.contains(globalObject)) {
				throw new IllegalStateException("duplicate object");
			} else {
				chunk.objects.add(globalObject);
			}
		}

		void removeObject(GlobalObject globalObject) {
			GlobalObjectLookup.Chunk chunk = this.getChunkForObject(globalObject, false);
			if (chunk != null && chunk.objects.contains(globalObject)) {
				chunk.objects.remove(globalObject);
			} else {
				throw new IllegalStateException("chunk doesn\'t contain object");
			}
		}

		void Reset() {
			for (int int1 = 0; int1 < this.chunks.length; ++int1) {
				GlobalObjectLookup.Chunk chunk = this.chunks[int1];
				if (chunk != null) {
					chunk.Reset();
				}
			}

			Arrays.fill(this.chunks, (Object)null);
		}
	}

	private static final class Chunk {
		final ArrayList objects = new ArrayList();

		void Reset() {
			this.objects.clear();
		}
	}
}
