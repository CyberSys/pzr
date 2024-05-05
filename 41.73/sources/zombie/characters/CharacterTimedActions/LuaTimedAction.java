package zombie.characters.CharacterTimedActions;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;


public final class LuaTimedAction extends BaseAction {
	KahluaTable table;
	public static Object[] statObj = new Object[6];

	public LuaTimedAction(KahluaTable kahluaTable, IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.table = kahluaTable;
		Object object = kahluaTable.rawget("maxTime");
		this.MaxTime = (Integer)LuaManager.converterManager.fromLuaToJava(object, Integer.class);
		Object object2 = kahluaTable.rawget("stopOnWalk");
		Object object3 = kahluaTable.rawget("stopOnRun");
		Object object4 = kahluaTable.rawget("stopOnAim");
		Object object5 = kahluaTable.rawget("onUpdateFunc");
		if (object2 != null) {
			this.StopOnWalk = (Boolean)LuaManager.converterManager.fromLuaToJava(object2, Boolean.class);
		}

		if (object3 != null) {
			this.StopOnRun = (Boolean)LuaManager.converterManager.fromLuaToJava(object3, Boolean.class);
		}

		if (object4 != null) {
			this.StopOnAim = (Boolean)LuaManager.converterManager.fromLuaToJava(object4, Boolean.class);
		}
	}

	public void update() {
		statObj[0] = this.table.rawget("character");
		statObj[1] = this.table.rawget("param1");
		statObj[2] = this.table.rawget("param2");
		statObj[3] = this.table.rawget("param3");
		statObj[4] = this.table.rawget("param4");
		statObj[5] = this.table.rawget("param5");
		LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget("onUpdateFunc"), statObj);
		super.update();
	}

	public boolean valid() {
		Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("isValidFunc"), this.table.rawget("character"), this.table.rawget("param1"), this.table.rawget("param2"), this.table.rawget("param3"), this.table.rawget("param4"), this.table.rawget("param5"));
		return objectArray.length > 0 && (Boolean)objectArray[0];
	}

	public void start() {
		super.start();
		this.CurrentTime = 0.0F;
		LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("startFunc"), this.table.rawget("character"), this.table.rawget("param1"), this.table.rawget("param2"), this.table.rawget("param3"), this.table.rawget("param4"), this.table.rawget("param5"));
	}

	public void stop() {
		super.stop();
		LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("onStopFunc"), this.table.rawget("character"), this.table.rawget("param1"), this.table.rawget("param2"), this.table.rawget("param3"), this.table.rawget("param4"), this.table.rawget("param5"));
	}

	public void perform() {
		super.perform();
		LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("performFunc"), this.table.rawget("character"), this.table.rawget("param1"), this.table.rawget("param2"), this.table.rawget("param3"), this.table.rawget("param4"), this.table.rawget("param5"));
	}
}
