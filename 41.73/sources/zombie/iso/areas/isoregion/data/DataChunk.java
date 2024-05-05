package zombie.iso.areas.isoregion.data;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import zombie.debug.DebugLog;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;


public final class DataChunk {
	private final DataCell cell;
	private final int hashId;
	private final int chunkX;
	private final int chunkY;
	protected int highestZ = 0;
	protected long lastUpdateStamp = 0L;
	private final boolean[] activeZLayers = new boolean[8];
	private final boolean[] dirtyZLayers = new boolean[8];
	private byte[] squareFlags;
	private byte[] regionIDs;
	private final ArrayList chunkRegions = new ArrayList(8);
	private static byte selectedFlags;
	private static final ArrayDeque tmpSquares = new ArrayDeque();
	private static final HashSet tmpLinkedChunks = new HashSet();
	private static final boolean[] exploredPositions = new boolean[100];
	private static IsoChunkRegion lastCurRegion;
	private static IsoChunkRegion lastOtherRegionFullConnect;
	private static ArrayList oldList = new ArrayList();
	private static final ArrayDeque chunkQueue = new ArrayDeque();

	protected DataChunk(int int1, int int2, DataCell dataCell, int int3) {
		this.cell = dataCell;
		this.hashId = int3 < 0 ? IsoRegions.hash(int1, int2) : int3;
		this.chunkX = int1;
		this.chunkY = int2;
		for (int int4 = 0; int4 < 8; ++int4) {
			this.chunkRegions.add(new ArrayList());
		}
	}

	protected int getHashId() {
		return this.hashId;
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkY() {
		return this.chunkY;
	}

	protected ArrayList getChunkRegions(int int1) {
		return (ArrayList)this.chunkRegions.get(int1);
	}

	public long getLastUpdateStamp() {
		return this.lastUpdateStamp;
	}

	public void setLastUpdateStamp(long long1) {
		this.lastUpdateStamp = long1;
	}

	protected boolean isDirty(int int1) {
		return this.activeZLayers[int1] ? this.dirtyZLayers[int1] : false;
	}

	protected void setDirty(int int1) {
		if (this.activeZLayers[int1]) {
			this.dirtyZLayers[int1] = true;
			this.cell.dataRoot.EnqueueDirtyDataChunk(this);
		}
	}

	public void setDirtyAllActive() {
		boolean boolean1 = false;
		for (int int1 = 0; int1 < 8; ++int1) {
			if (this.activeZLayers[int1]) {
				this.dirtyZLayers[int1] = true;
				if (!boolean1) {
					this.cell.dataRoot.EnqueueDirtyDataChunk(this);
					boolean1 = true;
				}
			}
		}
	}

	protected void unsetDirtyAll() {
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
			if (this.squareFlags[int4] != byte1) {
				this.setDirty(int3);
			}

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
		int int2 = (int1 + 1) * 10 * 10;
		if (this.squareFlags == null || this.squareFlags.length < int2) {
			byte[] byteArray = this.squareFlags;
			byte[] byteArray2 = this.regionIDs;
			this.squareFlags = new byte[int2];
			this.regionIDs = new byte[int2];
			if (byteArray != null) {
				for (int int3 = 0; int3 < byteArray.length; ++int3) {
					this.squareFlags[int3] = byteArray[int3];
					this.regionIDs[int3] = byteArray2[int3];
				}
			}
		}
	}

	public void save(ByteBuffer byteBuffer) {
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

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) {
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
		return this.squareHasFlags(this.getCoord1D(int1, int2, int3), byte1);
	}

	private boolean squareHasFlags(int int1, byte byte1) {
		byte byte2 = this.squareFlags[int1];
		return (byte2 & byte1) == byte1;
	}

	public byte squareGetFlags(int int1, int int2, int int3) {
		return this.squareGetFlags(this.getCoord1D(int1, int2, int3));
	}

	private byte squareGetFlags(int int1) {
		return this.squareFlags[int1];
	}

	protected void squareAddFlags(int int1, int int2, int int3, byte byte1) {
		this.squareAddFlags(this.getCoord1D(int1, int2, int3), byte1);
	}

	private void squareAddFlags(int int1, byte byte1) {
		byte[] byteArray = this.squareFlags;
		byteArray[int1] |= byte1;
	}

	protected void squareRemoveFlags(int int1, int int2, int int3, byte byte1) {
		this.squareRemoveFlags(this.getCoord1D(int1, int2, int3), byte1);
	}

	private void squareRemoveFlags(int int1, byte byte1) {
		byte[] byteArray = this.squareFlags;
		byteArray[int1] ^= byte1;
	}

	protected boolean squareCanConnect(int int1, int int2, int int3, byte byte1) {
		return this.squareCanConnect(this.getCoord1D(int1, int2, int3), int3, byte1);
	}

	private boolean squareCanConnect(int int1, int int2, byte byte1) {
		if (int2 >= 0 && int2 < this.highestZ + 1) {
			if (byte1 == 0) {
				return !this.squareHasFlags(int1, (byte)1);
			}

			if (byte1 == 1) {
				return !this.squareHasFlags(int1, (byte)2);
			}

			if (byte1 == 2) {
				return true;
			}

			if (byte1 == 3) {
				return true;
			}

			if (byte1 == 4) {
				return !this.squareHasFlags(int1, (byte)64);
			}

			if (byte1 == 5) {
				return !this.squareHasFlags(int1, (byte)16);
			}
		}

		return false;
	}

	public IsoChunkRegion getIsoChunkRegion(int int1, int int2, int int3) {
		return this.getIsoChunkRegion(this.getCoord1D(int1, int2, int3), int3);
	}

	private IsoChunkRegion getIsoChunkRegion(int int1, int int2) {
		if (int2 >= 0 && int2 < this.highestZ + 1) {
			byte byte1 = this.regionIDs[int1];
			if (byte1 >= 0 && byte1 < ((ArrayList)this.chunkRegions.get(int2)).size()) {
				return (IsoChunkRegion)((ArrayList)this.chunkRegions.get(int2)).get(byte1);
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
		for (int int2 = arrayList.size() - 1; int2 >= 0; --int2) {
			IsoChunkRegion chunkRegion = (IsoChunkRegion)arrayList.get(int2);
			IsoWorldRegion worldRegion = chunkRegion.unlinkFromIsoWorldRegion();
			if (worldRegion != null && worldRegion.size() <= 0) {
				this.cell.dataRoot.regionManager.releaseIsoWorldRegion(worldRegion);
			}

			this.cell.dataRoot.regionManager.releaseIsoChunkRegion(chunkRegion);
			arrayList.remove(int2);
		}

		arrayList.clear();
		byte byte1 = 100;
		Arrays.fill(this.regionIDs, int1 * byte1, int1 * byte1 + byte1, (byte)-1);
		for (int int3 = 0; int3 < 10; ++int3) {
			for (int int4 = 0; int4 < 10; ++int4) {
				if (this.regionIDs[this.getCoord1D(int4, int3, int1)] == -1) {
					this.floodFill(int4, int3, int1);
				}
			}
		}
	}

	private IsoChunkRegion floodFill(int int1, int int2, int int3) {
		IsoChunkRegion chunkRegion = this.cell.dataRoot.regionManager.allocIsoChunkRegion(int3);
		byte byte1 = (byte)((ArrayList)this.chunkRegions.get(int3)).size();
		((ArrayList)this.chunkRegions.get(int3)).add(chunkRegion);
		this.clearExploredPositions();
		tmpSquares.clear();
		tmpLinkedChunks.clear();
		tmpSquares.add(DataSquarePos.alloc(int1, int2, int3));
		while (true) {
			DataSquarePos dataSquarePos;
			int int4;
			do {
				if ((dataSquarePos = (DataSquarePos)tmpSquares.poll()) == null) {
					return chunkRegion;
				}

				int4 = this.getCoord1D(dataSquarePos.x, dataSquarePos.y, dataSquarePos.z);
				this.setExploredPosition(int4, dataSquarePos.z);
			}	 while (this.regionIDs[int4] != -1);

			this.regionIDs[int4] = byte1;
			chunkRegion.addSquareCount();
			for (byte byte2 = 0; byte2 < 4; ++byte2) {
				DataSquarePos dataSquarePos2 = this.getNeighbor(dataSquarePos, byte2);
				if (dataSquarePos2 != null) {
					int int5 = this.getCoord1D(dataSquarePos2.x, dataSquarePos2.y, dataSquarePos2.z);
					if (this.isExploredPosition(int5, dataSquarePos2.z)) {
						DataSquarePos.release(dataSquarePos2);
					} else {
						if (this.squareCanConnect(int4, dataSquarePos.z, byte2) && this.squareCanConnect(int5, dataSquarePos2.z, IsoRegions.GetOppositeDir(byte2))) {
							if (this.regionIDs[int5] == -1) {
								tmpSquares.add(dataSquarePos2);
								this.setExploredPosition(int5, dataSquarePos2.z);
								continue;
							}
						} else {
							IsoChunkRegion chunkRegion2 = this.getIsoChunkRegion(int5, dataSquarePos2.z);
							if (chunkRegion2 != null && chunkRegion2 != chunkRegion) {
								if (!tmpLinkedChunks.contains(chunkRegion2.getID())) {
									chunkRegion.addNeighbor(chunkRegion2);
									chunkRegion2.addNeighbor(chunkRegion);
									tmpLinkedChunks.add(chunkRegion2.getID());
								}

								this.setExploredPosition(int5, dataSquarePos2.z);
								DataSquarePos.release(dataSquarePos2);
								continue;
							}
						}

						DataSquarePos.release(dataSquarePos2);
					}
				} else if (this.squareCanConnect(int4, dataSquarePos.z, byte2)) {
					chunkRegion.addChunkBorderSquaresCnt();
				}
			}
		}
	}

	private boolean isExploredPosition(int int1, int int2) {
		int int3 = int1 - int2 * 10 * 10;
		return exploredPositions[int3];
	}

	private void setExploredPosition(int int1, int int2) {
		int int3 = int1 - int2 * 10 * 10;
		exploredPositions[int3] = true;
	}

	private void clearExploredPositions() {
		Arrays.fill(exploredPositions, false);
	}

	private DataSquarePos getNeighbor(DataSquarePos dataSquarePos, byte byte1) {
		int int1 = dataSquarePos.x;
		int int2 = dataSquarePos.y;
		if (byte1 == 1) {
			int1 = dataSquarePos.x - 1;
		} else if (byte1 == 3) {
			int1 = dataSquarePos.x + 1;
		}

		if (byte1 == 0) {
			int2 = dataSquarePos.y - 1;
		} else if (byte1 == 2) {
			int2 = dataSquarePos.y + 1;
		}

		return int1 >= 0 && int1 < 10 && int2 >= 0 && int2 < 10 ? DataSquarePos.alloc(int1, int2, dataSquarePos.z) : null;
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

		if (dataChunk != null && dataChunk.isDirty(int1)) {
			dataChunk.resetEnclosedSide(int1, IsoRegions.GetOppositeDir(byte1));
		}

		lastCurRegion = null;
		lastOtherRegionFullConnect = null;
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

				int int10 = this.getCoord1D(int7, int6, int1);
				int int11 = this.getCoord1D(int8, int9, int1);
				IsoChunkRegion chunkRegion = this.getIsoChunkRegion(int10, int1);
				IsoChunkRegion chunkRegion2 = dataChunk != null ? dataChunk.getIsoChunkRegion(int11, int1) : null;
				if (chunkRegion == null) {
					IsoRegions.warn("ds.getRegion()==null, shouldnt happen at this point.");
				} else {
					if (lastCurRegion != null && lastCurRegion != chunkRegion) {
						lastOtherRegionFullConnect = null;
					}

					if (lastCurRegion == null || lastCurRegion != chunkRegion || chunkRegion2 == null || lastOtherRegionFullConnect != chunkRegion2) {
						if (dataChunk != null && chunkRegion2 != null) {
							if (this.squareCanConnect(int10, int1, byte1) && dataChunk.squareCanConnect(int11, int1, IsoRegions.GetOppositeDir(byte1))) {
								chunkRegion.addConnectedNeighbor(chunkRegion2);
								chunkRegion2.addConnectedNeighbor(chunkRegion);
								chunkRegion.addNeighbor(chunkRegion2);
								chunkRegion2.addNeighbor(chunkRegion);
								if (!chunkRegion2.getIsEnclosed()) {
									chunkRegion2.setEnclosed(IsoRegions.GetOppositeDir(byte1), true);
								}

								lastOtherRegionFullConnect = chunkRegion2;
							} else {
								chunkRegion.addNeighbor(chunkRegion2);
								chunkRegion2.addNeighbor(chunkRegion);
								if (!chunkRegion2.getIsEnclosed()) {
									chunkRegion2.setEnclosed(IsoRegions.GetOppositeDir(byte1), true);
								}

								lastOtherRegionFullConnect = null;
							}
						} else if (this.squareCanConnect(int10, int1, byte1)) {
							chunkRegion.setEnclosed(byte1, false);
						}

						lastCurRegion = chunkRegion;
					}
				}
			}
		}
	}

	private void resetEnclosedSide(int int1, byte byte1) {
		ArrayList arrayList = (ArrayList)this.chunkRegions.get(int1);
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			IsoChunkRegion chunkRegion = (IsoChunkRegion)arrayList.get(int2);
			if (chunkRegion.getzLayer() == int1) {
				chunkRegion.setEnclosed(byte1, true);
			}
		}
	}

	protected void interConnect() {
		for (int int1 = 0; int1 <= this.highestZ; ++int1) {
			if (this.dirtyZLayers[int1] && this.activeZLayers[int1]) {
				ArrayList arrayList = (ArrayList)this.chunkRegions.get(int1);
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					IsoChunkRegion chunkRegion = (IsoChunkRegion)arrayList.get(int2);
					if (chunkRegion.getzLayer() == int1 && chunkRegion.getIsoWorldRegion() == null) {
						if (chunkRegion.getConnectedNeighbors().size() == 0) {
							IsoWorldRegion worldRegion = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
							this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(worldRegion);
							worldRegion.addIsoChunkRegion(chunkRegion);
						} else {
							IsoChunkRegion chunkRegion2 = chunkRegion.getConnectedNeighborWithLargestIsoWorldRegion();
							IsoWorldRegion worldRegion2;
							if (chunkRegion2 == null) {
								worldRegion2 = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
								this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(worldRegion2);
								this.floodFillExpandWorldRegion(chunkRegion, worldRegion2);
								++DataRoot.floodFills;
							} else {
								worldRegion2 = chunkRegion2.getIsoWorldRegion();
								oldList.clear();
								oldList = worldRegion2.swapIsoChunkRegions(oldList);
								IsoChunkRegion chunkRegion3;
								for (int int3 = 0; int3 < oldList.size(); ++int3) {
									chunkRegion3 = (IsoChunkRegion)oldList.get(int3);
									chunkRegion3.setIsoWorldRegion((IsoWorldRegion)null);
								}

								this.cell.dataRoot.regionManager.releaseIsoWorldRegion(worldRegion2);
								IsoWorldRegion worldRegion3 = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
								this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(worldRegion3);
								this.floodFillExpandWorldRegion(chunkRegion, worldRegion3);
								for (int int4 = 0; int4 < oldList.size(); ++int4) {
									chunkRegion3 = (IsoChunkRegion)oldList.get(int4);
									if (chunkRegion3.getIsoWorldRegion() == null) {
										IsoWorldRegion worldRegion4 = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
										this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(worldRegion4);
										this.floodFillExpandWorldRegion(chunkRegion3, worldRegion4);
									}
								}

								++DataRoot.floodFills;
							}
						}
					}
				}
			}
		}
	}

	private void floodFillExpandWorldRegion(IsoChunkRegion chunkRegion, IsoWorldRegion worldRegion) {
		chunkQueue.add(chunkRegion);
		while (true) {
			IsoChunkRegion chunkRegion2;
			do {
				if ((chunkRegion2 = (IsoChunkRegion)chunkQueue.poll()) == null) {
					return;
				}

				worldRegion.addIsoChunkRegion(chunkRegion2);
			}	 while (chunkRegion2.getConnectedNeighbors().size() == 0);

			for (int int1 = 0; int1 < chunkRegion2.getConnectedNeighbors().size(); ++int1) {
				IsoChunkRegion chunkRegion3 = (IsoChunkRegion)chunkRegion2.getConnectedNeighbors().get(int1);
				if (!chunkQueue.contains(chunkRegion3)) {
					if (chunkRegion3.getIsoWorldRegion() == null) {
						chunkQueue.add(chunkRegion3);
					} else if (chunkRegion3.getIsoWorldRegion() != worldRegion) {
						worldRegion.merge(chunkRegion3.getIsoWorldRegion());
					}
				}
			}
		}
	}

	protected void recalcRoofs() {
		if (this.highestZ >= 1) {
			int int1;
			for (int1 = 0; int1 < this.chunkRegions.size(); ++int1) {
				for (int int2 = 0; int2 < ((ArrayList)this.chunkRegions.get(int1)).size(); ++int2) {
					IsoChunkRegion chunkRegion = (IsoChunkRegion)((ArrayList)this.chunkRegions.get(int1)).get(int2);
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
									IsoChunkRegion chunkRegion2 = this.getIsoChunkRegion(int4, int3, int5);
									if (chunkRegion2 != null) {
										chunkRegion2.addRoof();
										if (chunkRegion2.getIsoWorldRegion() != null && !chunkRegion2.getIsoWorldRegion().isEnclosed()) {
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
}
