package zombie.iso.areas.isoregion.regions;

import java.util.ArrayDeque;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.data.DataRoot;


public final class IsoRegionManager {
	private final ArrayDeque poolIsoWorldRegion = new ArrayDeque();
	private final ArrayDeque poolIsoChunkRegion = new ArrayDeque();
	private final DataRoot dataRoot;
	private final ArrayDeque regionIdStack = new ArrayDeque();
	private int nextID = 0;
	private int colorIndex = 0;
	private int worldRegionCount = 0;
	private int chunkRegionCount = 0;

	public IsoRegionManager(DataRoot dataRoot) {
		this.dataRoot = dataRoot;
	}

	public IsoWorldRegion allocIsoWorldRegion() {
		IsoWorldRegion worldRegion = !this.poolIsoWorldRegion.isEmpty() ? (IsoWorldRegion)this.poolIsoWorldRegion.pop() : new IsoWorldRegion(this);
		int int1;
		if (this.regionIdStack.isEmpty()) {
			int int2 = this.nextID;
			int1 = int2;
			this.nextID = int2 + 1;
		} else {
			int1 = (Integer)this.regionIdStack.pop();
		}

		int int3 = int1;
		worldRegion.init(int3);
		++this.worldRegionCount;
		return worldRegion;
	}

	public void releaseIsoWorldRegion(IsoWorldRegion worldRegion) {
		this.dataRoot.DequeueDirtyIsoWorldRegion(worldRegion);
		if (!worldRegion.isInPool()) {
			this.regionIdStack.push(worldRegion.getID());
			worldRegion.reset();
			this.poolIsoWorldRegion.push(worldRegion);
			--this.worldRegionCount;
		} else {
			IsoRegions.warn("IsoRegionManager -> Trying to release a MasterRegion twice.");
		}
	}

	public IsoChunkRegion allocIsoChunkRegion(int int1) {
		IsoChunkRegion chunkRegion = !this.poolIsoChunkRegion.isEmpty() ? (IsoChunkRegion)this.poolIsoChunkRegion.pop() : new IsoChunkRegion(this);
		int int2;
		if (this.regionIdStack.isEmpty()) {
			int int3 = this.nextID;
			int2 = int3;
			this.nextID = int3 + 1;
		} else {
			int2 = (Integer)this.regionIdStack.pop();
		}

		int int4 = int2;
		chunkRegion.init(int4, int1);
		++this.chunkRegionCount;
		return chunkRegion;
	}

	public void releaseIsoChunkRegion(IsoChunkRegion chunkRegion) {
		if (!chunkRegion.isInPool()) {
			this.regionIdStack.push(chunkRegion.getID());
			chunkRegion.reset();
			this.poolIsoChunkRegion.push(chunkRegion);
			--this.chunkRegionCount;
		} else {
			IsoRegions.warn("IsoRegionManager -> Trying to release a ChunkRegion twice.");
		}
	}

	public Color getColor() {
		Color color = Colors.GetColorFromIndex(this.colorIndex++);
		if (this.colorIndex >= Colors.GetColorsCount()) {
			this.colorIndex = 0;
		}

		return color;
	}

	public int getWorldRegionCount() {
		return this.worldRegionCount;
	}

	public int getChunkRegionCount() {
		return this.chunkRegionCount;
	}
}
