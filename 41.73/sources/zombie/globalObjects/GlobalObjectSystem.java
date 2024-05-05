package zombie.globalObjects;

import java.util.ArrayDeque;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;


public abstract class GlobalObjectSystem {
	private static final ArrayDeque objectListPool = new ArrayDeque();
	protected final String name;
	protected final KahluaTable modData;
	protected final ArrayList objects = new ArrayList();
	protected final GlobalObjectLookup lookup = new GlobalObjectLookup(this);

	GlobalObjectSystem(String string) {
		this.name = string;
		this.modData = LuaManager.platform.newTable();
	}

	public String getName() {
		return this.name;
	}

	public final KahluaTable getModData() {
		return this.modData;
	}

	protected abstract GlobalObject makeObject(int int1, int int2, int int3);

	public final GlobalObject newObject(int int1, int int2, int int3) {
		if (this.getObjectAt(int1, int2, int3) != null) {
			throw new IllegalStateException("already an object at " + int1 + "," + int2 + "," + int3);
		} else {
			GlobalObject globalObject = this.makeObject(int1, int2, int3);
			this.objects.add(globalObject);
			this.lookup.addObject(globalObject);
			return globalObject;
		}
	}

	public final void removeObject(GlobalObject globalObject) throws IllegalArgumentException, IllegalStateException {
		if (globalObject == null) {
			throw new NullPointerException("object is null");
		} else if (globalObject.system != this) {
			throw new IllegalStateException("object not in this system");
		} else {
			this.objects.remove(globalObject);
			this.lookup.removeObject(globalObject);
			globalObject.Reset();
		}
	}

	public final GlobalObject getObjectAt(int int1, int int2, int int3) {
		return this.lookup.getObjectAt(int1, int2, int3);
	}

	public final boolean hasObjectsInChunk(int int1, int int2) {
		return this.lookup.hasObjectsInChunk(int1, int2);
	}

	public final ArrayList getObjectsInChunk(int int1, int int2) {
		return this.lookup.getObjectsInChunk(int1, int2, this.allocList());
	}

	public final ArrayList getObjectsAdjacentTo(int int1, int int2, int int3) {
		return this.lookup.getObjectsAdjacentTo(int1, int2, int3, this.allocList());
	}

	public final int getObjectCount() {
		return this.objects.size();
	}

	public final GlobalObject getObjectByIndex(int int1) {
		return int1 >= 0 && int1 < this.objects.size() ? (GlobalObject)this.objects.get(int1) : null;
	}

	public final ArrayList allocList() {
		return objectListPool.isEmpty() ? new ArrayList() : (ArrayList)objectListPool.pop();
	}

	public final void finishedWithList(ArrayList arrayList) {
		if (arrayList != null && !objectListPool.contains(arrayList)) {
			arrayList.clear();
			objectListPool.add(arrayList);
		}
	}

	public void Reset() {
		for (int int1 = 0; int1 < this.objects.size(); ++int1) {
			GlobalObject globalObject = (GlobalObject)this.objects.get(int1);
			globalObject.Reset();
		}

		this.objects.clear();
		this.modData.wipe();
	}
}
