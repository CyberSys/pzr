package se.krka.kahlua.require;

import java.io.Reader;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;


public class Loadfile implements JavaFunction {
	private final LuaSourceProvider luaSourceProvider;

	public void install(KahluaTable kahluaTable) {
		kahluaTable.rawset("loadfile", this);
	}

	public Loadfile(LuaSourceProvider luaSourceProvider) {
		this.luaSourceProvider = luaSourceProvider;
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		String string = KahluaUtil.getStringArg(luaCallFrame, 1, "loadfile");
		Reader reader = this.luaSourceProvider.getLuaSource(string);
		if (reader == null) {
			luaCallFrame.pushNil();
			luaCallFrame.push("Does not exist: " + string);
			return 2;
		} else {
			luaCallFrame.setTop(2);
			luaCallFrame.set(0, reader);
			luaCallFrame.set(1, string);
			return LuaCompiler.loadstream(luaCallFrame, 2);
		}
	}
}
