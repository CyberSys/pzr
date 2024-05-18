package zombie.Quests;

import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.Lua.LuaManager;


public class QuestTask_LuaCondition extends QuestTask {
	KahluaTable table;
	LuaClosure ArbAction;

	public QuestTask_LuaCondition(String string, String string2, LuaClosure luaClosure, KahluaTable kahluaTable) {
		super(QuestTaskType.Custom, string, string2);
		this.table = kahluaTable;
		this.ArbAction = luaClosure;
	}

	public void Update() {
		if (!this.Complete && this.ArbActionCheck()) {
			this.Complete = true;
		}

		super.Update();
	}

	private boolean ArbActionCheck() {
		Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.ArbAction, (Object)this.table);
		return objectArray.length > 1 ? (Boolean)objectArray[1] : false;
	}
}
