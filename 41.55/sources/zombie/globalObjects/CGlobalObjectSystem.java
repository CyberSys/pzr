package zombie.globalObjects;

import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;


public final class CGlobalObjectSystem extends GlobalObjectSystem {

	public CGlobalObjectSystem(String string) {
		super(string);
	}

	protected GlobalObject makeObject(int int1, int int2, int int3) {
		return new CGlobalObject(this, int1, int2, int3);
	}

	public void sendCommand(String string, IsoPlayer player, KahluaTable kahluaTable) {
		CGlobalObjectNetwork.sendClientCommand(player, this.name, string, kahluaTable);
	}

	public void receiveServerCommand(String string, KahluaTable kahluaTable) {
		Object object = this.modData.rawget("OnServerCommand");
		if (object == null) {
			throw new IllegalStateException("OnServerCommand method undefined for system \'" + this.name + "\'");
		} else {
			LuaManager.caller.pcallvoid(LuaManager.thread, object, this.modData, string, kahluaTable);
		}
	}

	public void receiveNewLuaObjectAt(int int1, int int2, int int3, KahluaTable kahluaTable) {
		Object object = this.modData.rawget("newLuaObjectAt");
		if (object == null) {
			throw new IllegalStateException("newLuaObjectAt method undefined for system \'" + this.name + "\'");
		} else {
			LuaManager.caller.pcall(LuaManager.thread, object, this.modData, BoxedStaticValues.toDouble((double)int1), BoxedStaticValues.toDouble((double)int2), BoxedStaticValues.toDouble((double)int3));
			GlobalObject globalObject = this.getObjectAt(int1, int2, int3);
			if (globalObject != null) {
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					globalObject.getModData().rawset(kahluaTableIterator.getKey(), kahluaTableIterator.getValue());
				}
			}
		}
	}

	public void receiveRemoveLuaObjectAt(int int1, int int2, int int3) {
		Object object = this.modData.rawget("removeLuaObjectAt");
		if (object == null) {
			throw new IllegalStateException("removeLuaObjectAt method undefined for system \'" + this.name + "\'");
		} else {
			LuaManager.caller.pcall(LuaManager.thread, object, this.modData, BoxedStaticValues.toDouble((double)int1), BoxedStaticValues.toDouble((double)int2), BoxedStaticValues.toDouble((double)int3));
		}
	}

	public void receiveUpdateLuaObjectAt(int int1, int int2, int int3, KahluaTable kahluaTable) {
		GlobalObject globalObject = this.getObjectAt(int1, int2, int3);
		if (globalObject != null) {
			KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
			while (kahluaTableIterator.advance()) {
				globalObject.getModData().rawset(kahluaTableIterator.getKey(), kahluaTableIterator.getValue());
			}

			Object object = this.modData.rawget("OnLuaObjectUpdated");
			if (object == null) {
				throw new IllegalStateException("OnLuaObjectUpdated method undefined for system \'" + this.name + "\'");
			} else {
				LuaManager.caller.pcall(LuaManager.thread, object, this.modData, globalObject.getModData());
			}
		}
	}

	public void Reset() {
		super.Reset();
		this.modData.wipe();
	}
}
