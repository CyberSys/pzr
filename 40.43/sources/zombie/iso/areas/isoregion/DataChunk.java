package zombie.iso.areas.isoregion;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;


public class DataChunk {
	private static final ArrayList tmpSquares = new ArrayList();
	private static final ArrayList tmpSquaresDone = new ArrayList();
	private DataCell cell;
	private int hashId;
	private int chunkX;
	private int chunkY;
	protected int highestZ = 0;
	protected long lastUpdateStamp = 0L;
	private final boolean[] activeZLayers = new boolean[8];
	private final boolean[] dirtyZLayers = new boolean[8];
	private byte[] squareFlags;
	private byte[] regionIDs;
	private final ArrayList chunkRegions = new ArrayList(8);
	private int squareArraySize;
	private static byte selectedFlags;
	private int tmpx;
	private int tmpy;
	private static ArrayList tmpMasters = new ArrayList();
	private static ArrayList tmpChunks = new ArrayList();
	private static ArrayList oldList = new ArrayList();

	protected DataChunk(DataChunk dataChunk) {
		this.hashId = dataChunk.hashId;
		this.chunkX = dataChunk.chunkX;
		this.chunkY = dataChunk.chunkY;
		int int1;
		for (int1 = 0; int1 < dataChunk.activeZLayers.length; ++int1) {
			this.activeZLayers[int1] = dataChunk.activeZLayers[int1];
		}

		for (int1 = 0; int1 < dataChunk.dirtyZLayers.length; ++int1) {
			this.dirtyZLayers[int1] = dataChunk.dirtyZLayers[int1];
		}

		for (int1 = 0; int1 < 8; ++int1) {
			this.chunkRegions.add(new ArrayList());
		}
	}

	protected DataChunk(int int1, int int2, int int3) {
		this.hashId = int3 < 0 ? IsoRegion.hash(int1, int2) : int3;
		this.chunkX = int1;
		this.chunkY = int2;
		for (int int4 = 0; int4 < 8; ++int4) {
			this.chunkRegions.add(new ArrayList());
		}
	}

	protected DataChunk(int int1, int int2, DataCell dataCell, int int3) {
		this.cell = dataCell;
		this.hashId = int3 < 0 ? IsoRegion.hash(int1, int2) : int3;
		this.chunkX = int1;
		this.chunkY = int2;
		for (int int4 = 0; int4 < 8; ++int4) {
			this.chunkRegions.add(new ArrayList());
		}
	}

	protected DataChunk(int int1, int int2, DataCell dataCell, int int3, int int4) {
		this.cell = dataCell;
		this.hashId = int3 < 0 ? IsoRegion.hash(int1, int2) : int3;
		this.chunkX = int1;
		this.chunkY = int2;
		for (int int5 = 0; int5 < 8; ++int5) {
			this.chunkRegions.add(new ArrayList());
		}

		this.ensureSquares(int4);
	}

	protected int getHashId() {
		return this.hashId;
	}

	protected int getChunkX() {
		return this.chunkX;
	}

	protected int getChunkY() {
		return this.chunkY;
	}

	protected ArrayList getChunkRegions(int int1) {
		return (ArrayList)this.chunkRegions.get(int1);
	}

	protected boolean isDirty(int int1) {
		return this.activeZLayers[int1] ? this.dirtyZLayers[int1] : false;
	}

	protected void setDirty(int int1) {
		if (this.activeZLayers[int1]) {
			this.dirtyZLayers[int1] = true;
			IsoRegionWorker.EnqueueDirtyChunk(this);
		}
	}

	protected void setDirtyAllActive() {
		boolean boolean1 = false;
		for (int int1 = 0; int1 < 8; ++int1) {
			if (this.activeZLayers[int1]) {
				this.dirtyZLayers[int1] = true;
				if (!boolean1) {
					IsoRegionWorker.EnqueueDirtyChunk(this);
					boolean1 = true;
				}
			}
		}
	}

	protected void unsetDirtyAndFloodAll() {
		for (int int1 = 0; int1 < 8; ++int1) {
			this.dirtyZLayers[int1] = false;
		}
	}

	private boolean validCoords(int int1, int int2, int int3) {
		return int1 >= 0 && int1 < 10 && int2 >= 0 && int2 < 10 && int3 >= 0 && int3 < this.highestZ + 1;
	}

	private int getCoord1D(int int1, int int2, int int3) {
		return int3 * 10 * 10 + int2 * 10 + int1;
	}

	public byte getSquare(int int1, int int2, int int3) {
		return this.getSquare(int1, int2, int3, false);
	}

	public byte getSquare(int int1, int int2, int int3, boolean boolean1) {
		if (this.squareFlags != null && (boolean1 || this.validCoords(int1, int2, int3))) {
			return this.activeZLayers[int3] ? this.squareFlags[this.getCoord1D(int1, int2, int3)] : -1;
		} else {
			return -1;
		}
	}

	protected byte setOrAddSquare(int int1, int int2, int int3, byte byte1) {
		return this.setOrAddSquare(int1, int2, int3, byte1, false);
	}

	protected byte setOrAddSquare(int int1, int int2, int int3, byte byte1, boolean boolean1) {
		if (!boolean1 && !this.validCoords(int1, int2, int3)) {
			return -1;
		} else {
			this.ensureSquares(int3);
			int int4 = this.getCoord1D(int1, int2, int3);
			this.squareFlags[int4] = byte1;
			return byte1;
		}
	}

	private void ensureSquares(int int1) {
		if (int1 >= 0 && int1 < 8) {
			if (!this.activeZLayers[int1]) {
				this.ensureSquareArray(int1);
				this.activeZLayers[int1] = true;
				if (int1 > this.highestZ) {
					this.highestZ = int1;
				}

				for (int int2 = 0; int2 < 10; ++int2) {
					for (int int3 = 0; int3 < 10; ++int3) {
						int int4 = this.getCoord1D(int3, int2, int1);
						this.squareFlags[int4] = (byte)(int1 == 0 ? 16 : 0);
					}
				}
			}
		}
	}

	private void ensureSquareArray(int int1) {
		this.squareArraySize = (int1 + 1) * 10 * 10;
		if (this.squareFlags == null || this.squareFlags.length < this.squareArraySize) {
			byte[] byteArray = this.squareFlags;
			byte[] byteArray2 = this.regionIDs;
			this.squareFlags = new byte[this.squareArraySize];
			this.regionIDs = new byte[this.squareArraySize];
			if (byteArray != null) {
				for (int int2 = 0; int2 < byteArray.length; ++int2) {
					this.squareFlags[int2] = byteArray[int2];
					this.regionIDs[int2] = byteArray2[int2];
				}
			}
		}
	}

	protected static void readChunkDataIntoBuffer(IsoChunk chunk, ByteBuffer byteBuffer) {
		if (chunk != null) {
			int int1 = byteBuffer.position();
			byteBuffer.putInt(0);
			byteBuffer.putInt(chunk.maxLevel);
			int int2 = (chunk.maxLevel + 1) * 100;
			byteBuffer.putInt(int2);
			int int3;
			for (int3 = 0; int3 <= chunk.maxLevel; ++int3) {
				for (int int4 = 0; int4 < chunk.squares[0].length; ++int4) {
					IsoGridSquare square = chunk.squares[int3][int4];
					byte byte1 = IsoRegion.calculateSquareFlags(square);
					byteBuffer.put(byte1);
				}
			}

			int3 = byteBuffer.position();
			byteBuffer.position(int1);
			byteBuffer.putInt(int3 - int1);
			byteBuffer.position(int3);
		} else {
			byteBuffer.putInt(-1);
		}
	}

	protected void save(ByteBuffer byteBuffer) {
		try {
			int int1 = byteBuffer.position();
			byteBuffer.putInt(0);
			byteBuffer.putInt(this.highestZ);
			int int2 = (this.highestZ + 1) * 100;
			byteBuffer.putInt(int2);
			int int3;
			for (int3 = 0; int3 < int2; ++int3) {
				byteBuffer.put(this.squareFlags[int3]);
			}

			int3 = byteBuffer.position();
			byteBuffer.position(int1);
			byteBuffer.putInt(int3 - int1);
			byteBuffer.position(int3);
		} catch (Exception exception) {
			DebugLog.log(exception.getMessage());
			exception.printStackTrace();
		}
	}

	protected void load(ByteBuffer byteBuffer, int int1, boolean boolean1) {
		try {
			if (boolean1) {
				byteBuffer.getInt();
			}

			this.highestZ = byteBuffer.getInt();
			int int2;
			for (int2 = this.highestZ; int2 >= 0; --int2) {
				this.ensureSquares(int2);
			}

			int2 = byteBuffer.getInt();
			for (int int3 = 0; int3 < int2; ++int3) {
				this.squareFlags[int3] = byteBuffer.get();
			}
		} catch (Exception exception) {
			DebugLog.log(exception.getMessage());
			exception.printStackTrace();
		}
	}

	public void setSelectedFlags(int int1, int int2, int int3) {
		if (int3 >= 0 && int3 <= this.highestZ) {
			selectedFlags = this.squareFlags[this.getCoord1D(int1, int2, int3)];
		} else {
			selectedFlags = -1;
		}
	}

	public boolean selectedHasFlags(byte byte1) {
		return (selectedFlags & byte1) == byte1;
	}

	protected boolean squareHasFlags(int int1, int int2, int int3, byte byte1) {
		byte byte2 = this.squareFlags[this.getCoord1D(int1, int2, int3)];
		return (byte2 & byte1) == byte1;
	}

	public int squareGetFlags(int int1, int int2, int int3) {
		return this.squareFlags[this.getCoord1D(int1, int2, int3)];
	}

	protected void squareAddFlags(int int1, int int2, int int3, byte byte1) {
		byte[] byteArray = this.squareFlags;
		int int4 = this.getCoord1D(int1, int2, int3);
		byteArray[int4] |= byte1;
	}

	protected void squareRemoveFlags(int int1, int int2, int int3, byte byte1) {
		byte[] byteArray = this.squareFlags;
		int int4 = this.getCoord1D(int1, int2, int3);
		byteArray[int4] ^= byte1;
	}

	protected boolean squareCanConnect(int int1, int int2, int int3, byte byte1) {
		if (int3 >= 0 && int3 < this.highestZ + 1) {
			if (byte1 == 0) {
				return !this.squareHasFlags(int1, int2, int3, (byte)1);
			}

			if (byte1 == 1) {
				return !this.squareHasFlags(int1, int2, int3, (byte)2);
			}

			if (byte1 == 2) {
				return true;
			}

			if (byte1 == 3) {
				return true;
			}

			if (byte1 == 4) {
				return !this.squareHasFlags(int1, int2, int3, (byte)64);
			}

			if (byte1 == 5) {
				return !this.squareHasFlags(int1, int2, int3, (byte)16);
			}
		}

		return false;
	}

	public ChunkRegion getRegion(int int1, int int2, int int3) {
		if (int3 >= 0 && int3 < this.highestZ + 1) {
			byte byte1 = this.regionIDs[this.getCoord1D(int1, int2, int3)];
			if (byte1 >= 0) {
				return (ChunkRegion)((ArrayList)this.chunkRegions.get(int3)).get(byte1);
			}
		}

		return null;
	}

	public void setRegion(int int1, int int2, int int3, byte byte1) {
		this.regionIDs[this.getCoord1D(int1, int2, int3)] = byte1;
	}

	protected void recalculate() {
		for (int int1 = 0; int1 <= this.highestZ; ++int1) {
			if (this.dirtyZLayers[int1] && this.activeZLayers[int1]) {
				this.recalculate(int1);
			}
		}
	}

	private void recalculate(int int1) {
		ArrayList arrayList = (ArrayList)this.chunkRegions.get(int1);
		int int2;
		for (int2 = arrayList.size() - 1; int2 >= 0; --int2) {
			ChunkRegion chunkRegion = (ChunkRegion)arrayList.get(int2);
			MasterRegion masterRegion = chunkRegion.unlinkFromMaster();
			if (masterRegion != null && masterRegion.size() <= 0) {
				MasterRegion.release(masterRegion);
			}

			ChunkRegion.release(chunkRegion);
			arrayList.remove(int2);
		}

		arrayList.clear();
		int int3;
		for (int2 = 0; int2 < 10; ++int2) {
			for (int3 = 0; int3 < 10; ++int3) {
				this.regionIDs[this.getCoord1D(int3, int2, int1)] = -1;
			}
		}

		for (int2 = 0; int2 < 10; ++int2) {
			for (int3 = 0; int3 < 10; ++int3) {
				if (this.regionIDs[this.getCoord1D(int3, int2, int1)] == -1) {
					this.floodFill(int3, int2, int1);
				}
			}
		}
	}

	private ChunkRegion floodFill(int int1, int int2, int int3) {
		ChunkRegion chunkRegion = ChunkRegion.alloc(int3);
		byte byte1 = (byte)((ArrayList)this.chunkRegions.get(int3)).size();
		((ArrayList)this.chunkRegions.get(int3)).add(chunkRegion);
		tmpSquares.clear();
		tmpSquaresDone.clear();
		tmpSquares.add(DataSquarePos.alloc(int1, int2, int3));
		while (true) {
			DataSquarePos dataSquarePos;
			do {
				if (tmpSquares.size() <= 0) {
					Iterator iterator = tmpSquaresDone.iterator();
					while (iterator.hasNext()) {
						DataSquarePos dataSquarePos2 = (DataSquarePos)iterator.next();
						DataSquarePos.release(dataSquarePos2);
					}

					tmpSquares.clear();
					tmpSquaresDone.clear();
					return chunkRegion;
				}

				dataSquarePos = (DataSquarePos)tmpSquares.remove(0);
				tmpSquaresDone.add(dataSquarePos);
			}	 while (this.getRegion(dataSquarePos.x, dataSquarePos.y, dataSquarePos.z) != null);

			this.setRegion(dataSquarePos.x, dataSquarePos.y, dataSquarePos.z, byte1);
			chunkRegion.addSquareCount();
			for (byte byte2 = 0; byte2 < 4; ++byte2) {
				DataSquarePos dataSquarePos3 = this.getNeighbor(dataSquarePos, byte2);
				if (dataSquarePos3 == null) {
					if (this.squareCanConnect(dataSquarePos.x, dataSquarePos.y, dataSquarePos.z, byte2)) {
						chunkRegion.addChunkBorderSquaresCnt();
					}
				} else {
					if (this.squareCanConnect(dataSquarePos.x, dataSquarePos.y, dataSquarePos.z, byte2) && this.squareCanConnect(dataSquarePos3.x, dataSquarePos3.y, dataSquarePos3.z, IsoRegion.GetOppositeDir(byte2))) {
						if (this.getRegion(dataSquarePos3.x, dataSquarePos3.y, dataSquarePos3.z) == null) {
							if (this.isExploredPos(dataSquarePos3.x, dataSquarePos3.y, dataSquarePos3.z)) {
								DataSquarePos.release(dataSquarePos3);
							} else {
								tmpSquares.add(dataSquarePos3);
							}

							continue;
						}
					} else {
						ChunkRegion chunkRegion2 = this.getRegion(dataSquarePos3.x, dataSquarePos3.y, dataSquarePos3.z);
						if (chunkRegion2 != null && chunkRegion2 != chunkRegion) {
							if (this.isExploredPos(dataSquarePos3.x, dataSquarePos3.y, dataSquarePos3.z)) {
								DataSquarePos.release(dataSquarePos3);
							} else {
								chunkRegion.addNeighbor(chunkRegion2);
								chunkRegion2.addNeighbor(chunkRegion);
								tmpSquaresDone.add(dataSquarePos3);
							}

							continue;
						}
					}

					DataSquarePos.release(dataSquarePos3);
				}
			}
		}
	}

	private boolean isExploredPos(int int1, int int2, int int3) {
		Iterator iterator = tmpSquares.iterator();
		DataSquarePos dataSquarePos;
		do {
			if (!iterator.hasNext()) {
				iterator = tmpSquaresDone.iterator();
				do {
					if (!iterator.hasNext()) {
						return false;
					}

					dataSquarePos = (DataSquarePos)iterator.next();
				}		 while (dataSquarePos.x != int1 || dataSquarePos.y != int2 || dataSquarePos.z != int3);

				return true;
			}

			dataSquarePos = (DataSquarePos)iterator.next();
		} while (dataSquarePos.x != int1 || dataSquarePos.y != int2 || dataSquarePos.z != int3);

		return true;
	}

	private DataSquarePos getNeighbor(DataSquarePos dataSquarePos, byte byte1) {
		if (byte1 == 1) {
			this.tmpx = dataSquarePos.x - 1;
		} else if (byte1 == 3) {
			this.tmpx = dataSquarePos.x + 1;
		} else {
			this.tmpx = dataSquarePos.x;
		}

		if (byte1 == 0) {
			this.tmpy = dataSquarePos.y - 1;
		} else if (byte1 == 2) {
			this.tmpy = dataSquarePos.y + 1;
		} else {
			this.tmpy = dataSquarePos.y;
		}

		if (this.tmpx >= 0 && this.tmpx < 10 && this.tmpy >= 0 && this.tmpy < 10) {
			DataSquarePos dataSquarePos2 = DataSquarePos.alloc(this.tmpx, this.tmpy, dataSquarePos.z);
			return dataSquarePos2;
		} else {
			return null;
		}
	}

	protected void link(DataChunk dataChunk, DataChunk dataChunk2, DataChunk dataChunk3, DataChunk dataChunk4) {
		for (int int1 = 0; int1 <= this.highestZ; ++int1) {
			if (this.dirtyZLayers[int1] && this.activeZLayers[int1]) {
				this.linkRegionsOnSide(int1, dataChunk, (byte)0);
				this.linkRegionsOnSide(int1, dataChunk2, (byte)1);
				this.linkRegionsOnSide(int1, dataChunk3, (byte)2);
				this.linkRegionsOnSide(int1, dataChunk4, (byte)3);
			}
		}
	}

	private void linkRegionsOnSide(int int1, DataChunk dataChunk, byte byte1) {
		int int2;
		int int3;
		int int4;
		int int5;
		if (byte1 != 0 && byte1 != 2) {
			int2 = byte1 == 1 ? 0 : 9;
			int3 = int2 + 1;
			int4 = 0;
			int5 = 10;
		} else {
			int2 = 0;
			int3 = 10;
			int4 = byte1 == 0 ? 0 : 9;
			int5 = int4 + 1;
		}

		this.resetEnclosedSide(int1, byte1);
		if (dataChunk != null) {
			dataChunk.resetEnclosedSide(int1, IsoRegion.GetOppositeDir(byte1));
		}

		for (int int6 = int4; int6 < int5; ++int6) {
			for (int int7 = int2; int7 < int3; ++int7) {
				int int8;
				int int9;
				if (byte1 != 0 && byte1 != 2) {
					int8 = byte1 == 1 ? 9 : 0;
					int9 = int6;
				} else {
					int8 = int7;
					int9 = byte1 == 0 ? 9 : 0;
				}

				ChunkRegion chunkRegion = this.getRegion(int7, int6, int1);
				ChunkRegion chunkRegion2 = dataChunk != null ? dataChunk.getRegion(int8, int9, int1) : null;
				if (chunkRegion == null) {
					if (IsoRegion.PRINT_D) {
						DebugLog.log("WARNING: ds.getRegion()==null, shouldnt happen at this point.");
					}
				} else if (dataChunk != null && chunkRegion2 != null) {
					if (this.squareCanConnect(int7, int6, int1, byte1) && dataChunk.squareCanConnect(int8, int9, int1, IsoRegion.GetOppositeDir(byte1))) {
						chunkRegion.addConnectedNeighbor(chunkRegion2);
						chunkRegion2.addConnectedNeighbor(chunkRegion);
						chunkRegion.addNeighbor(chunkRegion2);
						chunkRegion2.addNeighbor(chunkRegion);
					} else {
						chunkRegion.addNeighbor(chunkRegion2);
						chunkRegion2.addNeighbor(chunkRegion);
					}
				} else if (this.squareCanConnect(int7, int6, int1, byte1)) {
					chunkRegion.setEnclosed(byte1, false);
				}
			}
		}
	}

	private void resetEnclosedSide(int int1, byte byte1) {
		Iterator iterator = ((ArrayList)this.chunkRegions.get(int1)).iterator();
		while (iterator.hasNext()) {
			ChunkRegion chunkRegion = (ChunkRegion)iterator.next();
			if (chunkRegion.getzLayer() == int1) {
				chunkRegion.setEnclosed(byte1, true);
			}
		}
	}

	protected void interConnect() {
		label74: for (int int1 = 0; int1 <= this.highestZ; ++int1) {
			if (this.dirtyZLayers[int1] && this.activeZLayers[int1]) {
				Iterator iterator = ((ArrayList)this.chunkRegions.get(int1)).iterator();
				while (true) {
					while (true) {
						while (true) {
							ChunkRegion chunkRegion;
							do {
								do {
									if (!iterator.hasNext()) {
										continue label74;
									}

									chunkRegion = (ChunkRegion)iterator.next();
								}						 while (chunkRegion.getzLayer() != int1);
							}					 while (chunkRegion.getMasterRegion() != null);

							if (chunkRegion.getConnectedNeighbors() != null && chunkRegion.getConnectedNeighbors().size() != 0) {
								ChunkRegion chunkRegion2 = chunkRegion.getConnectedNeighborWithLargestMaster();
								MasterRegion masterRegion;
								if (chunkRegion2 != null) {
									masterRegion = chunkRegion2.getMasterRegion();
									oldList.clear();
									tmpMasters.clear();
									oldList = masterRegion.swapChunkRegions(oldList);
									Iterator iterator2 = oldList.iterator();
									while (iterator2.hasNext()) {
										ChunkRegion chunkRegion3 = (ChunkRegion)iterator2.next();
										chunkRegion3.setMasterRegion((MasterRegion)null);
									}

									MasterRegion.release(masterRegion);
									MasterRegion masterRegion2 = MasterRegion.alloc();
									IsoRegionWorker.EnqueueDirtyMasterRegion(masterRegion2);
									this.floodFillExpandMaster(chunkRegion, masterRegion2);
									tmpMasters.add(masterRegion2);
									Iterator iterator3 = oldList.iterator();
									while (iterator3.hasNext()) {
										ChunkRegion chunkRegion4 = (ChunkRegion)iterator3.next();
										if (chunkRegion4.getMasterRegion() == null) {
											MasterRegion masterRegion3 = MasterRegion.alloc();
											IsoRegionWorker.EnqueueDirtyMasterRegion(masterRegion3);
											this.floodFillExpandMaster(chunkRegion4, masterRegion3);
											tmpMasters.add(masterRegion3);
										}
									}

									++DataRoot.floodFills;
								} else {
									masterRegion = MasterRegion.alloc();
									IsoRegionWorker.EnqueueDirtyMasterRegion(masterRegion);
									this.floodFillExpandMaster(chunkRegion, masterRegion);
									++DataRoot.floodFills;
								}
							} else {
								MasterRegion masterRegion4 = MasterRegion.alloc();
								IsoRegionWorker.EnqueueDirtyMasterRegion(masterRegion4);
								masterRegion4.addChunkRegion(chunkRegion);
							}
						}
					}
				}
			}
		}
	}

	private void floodFillExpandMaster(ChunkRegion chunkRegion, MasterRegion masterRegion) {
		tmpChunks.clear();
		tmpChunks.add(chunkRegion);
		while (true) {
			ChunkRegion chunkRegion2;
			do {
				if (tmpChunks.size() <= 0) {
					return;
				}

				chunkRegion2 = (ChunkRegion)tmpChunks.remove(0);
				masterRegion.addChunkRegion(chunkRegion2);
			}	 while (chunkRegion2.getConnectedNeighbors() == null);

			Iterator iterator = chunkRegion2.getConnectedNeighbors().iterator();
			while (iterator.hasNext()) {
				ChunkRegion chunkRegion3 = (ChunkRegion)iterator.next();
				if (!tmpChunks.contains(chunkRegion3) && !masterRegion.containsChunkRegion(chunkRegion3)) {
					if (chunkRegion3.getMasterRegion() == null) {
						tmpChunks.add(chunkRegion3);
					} else if (chunkRegion3.getMasterRegion() != masterRegion) {
						masterRegion.merge(chunkRegion3.getMasterRegion());
					}
				}
			}
		}
	}

	protected void recalcRoofs() {
		int int1;
		for (int1 = 0; int1 < this.chunkRegions.size(); ++int1) {
			for (int int2 = 0; int2 < ((ArrayList)this.chunkRegions.get(int1)).size(); ++int2) {
				ChunkRegion chunkRegion = (ChunkRegion)((ArrayList)this.chunkRegions.get(int1)).get(int2);
				chunkRegion.resetRoofCnt();
			}
		}

		int1 = this.highestZ;
		for (int int3 = 0; int3 < 10; ++int3) {
			for (int int4 = 0; int4 < 10; ++int4) {
				byte byte1 = this.getSquare(int4, int3, int1);
				boolean boolean1 = false;
				if (byte1 > 0) {
					boolean1 = this.squareHasFlags(int4, int3, int1, (byte)16);
				}

				if (int1 >= 1) {
					for (int int5 = int1 - 1; int5 >= 0; --int5) {
						byte1 = this.getSquare(int4, int3, int5);
						if (byte1 <= 0) {
							boolean1 = false;
						} else {
							boolean1 = boolean1 || this.squareHasFlags(int4, int3, int5, (byte)32);
							if (boolean1) {
								ChunkRegion chunkRegion2 = this.getRegion(int4, int3, int5);
								if (chunkRegion2 != null) {
									chunkRegion2.addRoof();
									if (chunkRegion2.getMasterRegion() != null && !chunkRegion2.getMasterRegion().isEnclosed()) {
										boolean1 = false;
									}
								} else {
									boolean1 = false;
								}
							}

							if (!boolean1) {
								boolean1 = this.squareHasFlags(int4, int3, int5, (byte)16);
							}
						}
					}
				}
			}
		}
	}
}
