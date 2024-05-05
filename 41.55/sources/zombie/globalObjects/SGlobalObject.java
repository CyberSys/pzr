package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;


public final class SGlobalObject extends GlobalObject {
	private static KahluaTable tempTable;

	SGlobalObject(SGlobalObjectSystem sGlobalObjectSystem, int int1, int int2, int int3) {
		super(sGlobalObjectSystem, int1, int2, int3);
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
			if (((SGlobalObjectSystem)this.system).objectModDataKeys.contains(object)) {
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
}
