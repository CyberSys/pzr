package zombie.ai;

import java.util.ArrayList;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;


public final class MapKnowledge {
	private final ArrayList knownBlockedEdges = new ArrayList();

	public ArrayList getKnownBlockedEdges() {
		return this.knownBlockedEdges;
	}

	public KnownBlockedEdges getKnownBlockedEdges(int int1, int int2, int int3) {
		for (int int4 = 0; int4 < this.knownBlockedEdges.size(); ++int4) {
			KnownBlockedEdges knownBlockedEdges = (KnownBlockedEdges)this.knownBlockedEdges.get(int4);
			if (knownBlockedEdges.x == int1 && knownBlockedEdges.y == int2 && knownBlockedEdges.z == int3) {
				return knownBlockedEdges;
			}
		}

		return null;
	}

	private KnownBlockedEdges createKnownBlockedEdges(int int1, int int2, int int3) {
		assert this.getKnownBlockedEdges(int1, int2, int3) == null;
		KnownBlockedEdges knownBlockedEdges = KnownBlockedEdges.alloc();
		knownBlockedEdges.init(int1, int2, int3);
		this.knownBlockedEdges.add(knownBlockedEdges);
		return knownBlockedEdges;
	}

	public KnownBlockedEdges getOrCreateKnownBlockedEdges(int int1, int int2, int int3) {
		KnownBlockedEdges knownBlockedEdges = this.getKnownBlockedEdges(int1, int2, int3);
		if (knownBlockedEdges == null) {
			knownBlockedEdges = this.createKnownBlockedEdges(int1, int2, int3);
		}

		return knownBlockedEdges;
	}

	private void releaseIfEmpty(KnownBlockedEdges knownBlockedEdges) {
		if (!knownBlockedEdges.n && !knownBlockedEdges.w) {
			this.knownBlockedEdges.remove(knownBlockedEdges);
			knownBlockedEdges.release();
		}
	}

	public void setKnownBlockedEdgeW(int int1, int int2, int int3, boolean boolean1) {
		KnownBlockedEdges knownBlockedEdges = this.getOrCreateKnownBlockedEdges(int1, int2, int3);
		knownBlockedEdges.w = boolean1;
		this.releaseIfEmpty(knownBlockedEdges);
	}

	public void setKnownBlockedEdgeN(int int1, int int2, int int3, boolean boolean1) {
		KnownBlockedEdges knownBlockedEdges = this.getOrCreateKnownBlockedEdges(int1, int2, int3);
		knownBlockedEdges.n = boolean1;
		this.releaseIfEmpty(knownBlockedEdges);
	}

	public void setKnownBlockedDoor(IsoDoor door, boolean boolean1) {
		IsoGridSquare square = door.getSquare();
		if (door.getNorth()) {
			this.setKnownBlockedEdgeN(square.x, square.y, square.z, boolean1);
		} else {
			this.setKnownBlockedEdgeW(square.x, square.y, square.z, boolean1);
		}
	}

	public void setKnownBlockedDoor(IsoThumpable thumpable, boolean boolean1) {
		if (thumpable.isDoor()) {
			IsoGridSquare square = thumpable.getSquare();
			if (thumpable.getNorth()) {
				this.setKnownBlockedEdgeN(square.x, square.y, square.z, boolean1);
			} else {
				this.setKnownBlockedEdgeW(square.x, square.y, square.z, boolean1);
			}
		}
	}

	public void setKnownBlockedWindow(IsoWindow window, boolean boolean1) {
		IsoGridSquare square = window.getSquare();
		if (window.getNorth()) {
			this.setKnownBlockedEdgeN(square.x, square.y, square.z, boolean1);
		} else {
			this.setKnownBlockedEdgeW(square.x, square.y, square.z, boolean1);
		}
	}

	public void setKnownBlockedWindowFrame(IsoObject object, boolean boolean1) {
		IsoGridSquare square = object.getSquare();
		if (IsoWindowFrame.isWindowFrame(object, true)) {
			this.setKnownBlockedEdgeN(square.x, square.y, square.z, boolean1);
		} else if (IsoWindowFrame.isWindowFrame(object, false)) {
			this.setKnownBlockedEdgeW(square.x, square.y, square.z, boolean1);
		}
	}

	public void forget() {
		KnownBlockedEdges.releaseAll(this.knownBlockedEdges);
		this.knownBlockedEdges.clear();
	}
}
