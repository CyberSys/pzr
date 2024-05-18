package zombie.Lua;

import java.util.Stack;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import zombie.debug.DebugLog;


public class Event {
	public static final int ADD = 0;
	public static final int NUM_FUNCTIONS = 1;
	private final Event.Add add;
	private final Event.Remove remove;
	public Stack callbacks = new Stack();
	public String name;
	private int index = 0;

	public boolean trigger(KahluaTable kahluaTable, LuaCaller luaCaller, Object[] objectArray) {
		for (int int1 = 0; int1 < this.callbacks.size(); ++int1) {
			try {
				luaCaller.protectedCallVoid(LuaManager.thread, this.callbacks.get(int1), objectArray);
			} catch (RuntimeException runtimeException) {
				DebugLog.log(runtimeException.getMessage());
			}
		}

		return !this.callbacks.isEmpty();
	}

	public Event(String string, int int1) {
		this.index = int1;
		this.name = string;
		this.add = new Event.Add(this);
		this.remove = new Event.Remove(this);
	}

	public void register(Platform platform, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		kahluaTable2.rawset("Add", this.add);
		kahluaTable2.rawset("Remove", this.remove);
		kahluaTable.rawset(this.name, kahluaTable2);
	}

	public class Remove implements JavaFunction {
		Event e;

		public Remove(Event event) {
			this.e = event;
		}

		public int call(LuaCallFrame luaCallFrame, int int1) {
			if (LuaCompiler.rewriteEvents) {
				return 0;
			} else {
				Object object = luaCallFrame.get(0);
				if (object instanceof LuaClosure) {
					LuaClosure luaClosure = (LuaClosure)object;
					this.e.callbacks.remove(luaClosure);
				}

				return 0;
			}
		}
	}

	public class Add implements JavaFunction {
		Event e;

		public Add(Event event) {
			this.e = event;
		}

		public int call(LuaCallFrame luaCallFrame, int int1) {
			if (LuaCompiler.rewriteEvents) {
				return 0;
			} else {
				Object object = luaCallFrame.get(0);
				if (this.e.name.contains("CreateUI")) {
					boolean boolean1 = false;
				}

				if (object instanceof LuaClosure) {
					LuaClosure luaClosure = (LuaClosure)object;
					this.e.callbacks.add(luaClosure);
				}

				return 0;
			}
		}
	}
}
