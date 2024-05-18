package zombie.ui;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;


public class LuaUIWindow extends NewWindow {

	public LuaUIWindow(int int1, int int2, int int3, int int4, boolean boolean1, KahluaTable kahluaTable) {
		super(int1, int2, int3, int4, boolean1);
		this.ResizeToFitY = false;
		this.table = kahluaTable;
	}

	public void ButtonClicked(String string) {
		super.ButtonClicked(string);
		if (this.getTable().rawget("onButtonClicked") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onButtonClicked"), this.table, string);
		}
	}
}
