package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;


public final class GlobalObject {
	protected SGlobalObjectSystem system;
	protected int x;
	protected int y;
	protected int z;
	protected final KahluaTable modData;
	private static KahluaTable tempTable;

	GlobalObject(SGlobalObjectSystem sGlobalObjectSystem, int int1, int int2, int int3) {
		this.system = sGlobalObjectSystem;
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.modData = LuaManager.platform.newTable();
	}

	public SGlobalObjectSystem getSystem() {
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

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		boolean boolean1 = byteBuffer.get() == 0;
		if (!boolean1) {
			this.modData.load(byteBuffer, int1);
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.putInt(this.x);
		byteBuffer.putInt(this.y);
		byteBuffer.put((byte)this.z);
		if (tempTable == null) {
			tempTable = LuaManager.platform.newTable();
		}

		tempTable.wipe();
		KahluaTableIterator kahluaTableIterator = this.modData.iterator();
		while (kahluaTableIterator.advance()) {
			Object object = kahluaTableIterator.getKey();
			if (this.system.objectModDataKeys.contains(object)) {
				tempTable.rawset(object, this.modData.rawget(object));
			}
		}

		if (tempTable.isEmpty()) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			tempTable.save(byteBuffer);
			tempTable.wipe();
		}
	}

	public void Reset() {
		this.system = null;
		this.modData.wipe();
	}
}
