package zombie.globalObjects;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;


public abstract class GlobalObject {
	protected GlobalObjectSystem system;
	protected int x;
	protected int y;
	protected int z;
	protected final KahluaTable modData;

	GlobalObject(GlobalObjectSystem globalObjectSystem, int int1, int int2, int int3) {
		this.system = globalObjectSystem;
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.modData = LuaManager.platform.newTable();
	}

	public GlobalObjectSystem getSystem() {
		return this.system;
	}

	public void setLocation(int int1, int int2, int int3) {
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public KahluaTable getModData() {
		return this.modData;
	}

	public void Reset() {
		this.system = null;
		this.modData.wipe();
	}
}
