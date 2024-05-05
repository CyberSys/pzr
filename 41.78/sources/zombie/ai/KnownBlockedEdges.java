package zombie.ai;

import java.util.ArrayList;
import java.util.List;
import zombie.GameWindow;
import zombie.network.GameServer;
import zombie.popman.ObjectPool;


public final class KnownBlockedEdges {
	public int x;
	public int y;
	public int z;
	public boolean w;
	public boolean n;
	static final ObjectPool pool = new ObjectPool(KnownBlockedEdges::new);

	public KnownBlockedEdges init(KnownBlockedEdges knownBlockedEdges) {
		return this.init(knownBlockedEdges.x, knownBlockedEdges.y, knownBlockedEdges.z, knownBlockedEdges.w, knownBlockedEdges.n);
	}

	public KnownBlockedEdges init(int int1, int int2, int int3) {
		return this.init(int1, int2, int3, false, false);
	}

	public KnownBlockedEdges init(int int1, int int2, int int3, boolean boolean1, boolean boolean2) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.w = boolean1;
		this.n = boolean2;
		return this;
	}

	public boolean isBlocked(int int1, int int2) {
		if (this.x > int1 && this.w) {
			return true;
		} else {
			return this.y > int2 && this.n;
		}
	}

	public static KnownBlockedEdges alloc() {
		assert GameServer.bServer || Thread.currentThread() == GameWindow.GameThread;
		return (KnownBlockedEdges)pool.alloc();
	}

	public static void releaseAll(ArrayList arrayList) {
		assert GameServer.bServer || Thread.currentThread() == GameWindow.GameThread;
		pool.release((List)arrayList);
	}

	public void release() {
		assert GameServer.bServer || Thread.currentThread() == GameWindow.GameThread;
		pool.release((Object)this);
	}
}
