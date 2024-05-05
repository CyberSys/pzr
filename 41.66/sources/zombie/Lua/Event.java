package zombie.Lua;

import java.util.ArrayList;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;


public final class Event {
	public static final int ADD = 0;
	public static final int NUM_FUNCTIONS = 1;
	private final Event.Add add;
	private final Event.Remove remove;
	public final ArrayList callbacks = new ArrayList();
	public String name;
	private int index = 0;

	public boolean trigger(KahluaTable kahluaTable, LuaCaller luaCaller, Object[] objectArray) {
		if (this.callbacks.isEmpty()) {
			return false;
		} else {
			int int1;
			if (DebugOptions.instance.Checks.SlowLuaEvents.getValue()) {
				for (int1 = 0; int1 < this.callbacks.size(); ++int1) {
					try {
						LuaClosure luaClosure = (LuaClosure)this.callbacks.get(int1);
						long long1 = System.nanoTime();
						luaCaller.protectedCallVoid(LuaManager.thread, luaClosure, (Object[])objectArray);
						double double1 = (double)(System.nanoTime() - long1) / 1000000.0;
						if (double1 > 250.0) {
							DebugLog.Lua.warn("SLOW Lua event callback %s %s %dms", luaClosure.prototype.file, luaClosure, (int)double1);
						}
					} catch (Exception exception) {
						ExceptionLogger.logException(exception);
					}
				}

				return true;
			} else {
				for (int1 = 0; int1 < this.callbacks.size(); ++int1) {
					try {
						luaCaller.protectedCallVoid(LuaManager.thread, this.callbacks.get(int1), objectArray);
					} catch (Exception exception2) {
						ExceptionLogger.logException(exception2);
					}
				}

				return true;
			}
		}
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

	public static final class Add implements JavaFunction {
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

	public static final class Remove implements JavaFunction {
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
}
