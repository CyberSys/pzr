package se.krka.kahlua.vm;

import java.util.ArrayList;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.utils.HashMap;


public class LuaCallFrame {
	private final Platform platform;
	public final Coroutine coroutine;
	public LuaClosure closure;
	public JavaFunction javaFunction;
	public int pc;
	public int localBase;
	int returnBase;
	public int nArguments;
	boolean fromLua;
	public boolean canYield;
	boolean restoreTop;
	public int localsAssigned = 0;
	public HashMap LocalVarToStackMap = new HashMap();
	public HashMap LocalStackToVarMap = new HashMap();
	public ArrayList LocalVarNames = new ArrayList();

	public LuaCallFrame(Coroutine coroutine) {
		this.coroutine = coroutine;
		this.platform = coroutine.getPlatform();
	}

	public String getFilename() {
		return this.closure != null ? this.closure.prototype.filename : null;
	}

	public final void set(int int1, Object object) {
		this.coroutine.objectStack[this.localBase + int1] = object;
	}

	public final Object get(int int1) {
		return this.coroutine.objectStack[this.localBase + int1];
	}

	public int push(Object object) {
		int int1 = this.getTop();
		this.setTop(int1 + 1);
		this.set(int1, object);
		return 1;
	}

	public int push(Object object, Object object2) {
		int int1 = this.getTop();
		this.setTop(int1 + 2);
		this.set(int1, object);
		this.set(int1 + 1, object2);
		return 2;
	}

	public int pushNil() {
		return this.push((Object)null);
	}

	public final void stackCopy(int int1, int int2, int int3) {
		this.coroutine.stackCopy(this.localBase + int1, this.localBase + int2, int3);
	}

	public void stackClear(int int1, int int2) {
		while (int1 <= int2) {
			this.coroutine.objectStack[this.localBase + int1] = null;
			++int1;
		}
	}

	public void clearFromIndex(int int1) {
		if (this.getTop() < int1) {
			this.setTop(int1);
		}

		this.stackClear(int1, this.getTop() - 1);
	}

	public final void setTop(int int1) {
		this.coroutine.setTop(this.localBase + int1);
	}

	public void closeUpvalues(int int1) {
		this.coroutine.closeUpvalues(this.localBase + int1);
	}

	public UpValue findUpvalue(int int1) {
		return this.coroutine.findUpvalue(this.localBase + int1);
	}

	public int getTop() {
		return this.coroutine.getTop() - this.localBase;
	}

	public void init() {
		if (this.isLua()) {
			this.pc = 0;
			if (this.closure.prototype.isVararg) {
				this.localBase += this.nArguments;
				this.setTop(this.closure.prototype.maxStacksize);
				int int1 = Math.min(this.nArguments, this.closure.prototype.numParams);
				this.stackCopy(-this.nArguments, 0, int1);
			} else {
				this.setTop(this.closure.prototype.maxStacksize);
				this.stackClear(this.closure.prototype.numParams, this.nArguments);
			}
		}
	}

	public void setPrototypeStacksize() {
		if (this.isLua()) {
			this.setTop(this.closure.prototype.maxStacksize);
		}
	}

	public void pushVarargs(int int1, int int2) {
		int int3 = this.closure.prototype.numParams;
		int int4 = this.nArguments - int3;
		if (int4 < 0) {
			int4 = 0;
		}

		if (int2 == -1) {
			int2 = int4;
			this.setTop(int1 + int4);
		}

		if (int4 > int2) {
			int4 = int2;
		}

		this.stackCopy(-this.nArguments + int3, int1, int4);
		int int5 = int2 - int4;
		if (int5 > 0) {
			this.stackClear(int1 + int4, int1 + int2 - 1);
		}
	}

	public KahluaTable getEnvironment() {
		return this.isLua() ? this.closure.env : this.coroutine.environment;
	}

	public boolean isJava() {
		return !this.isLua();
	}

	public boolean isLua() {
		return this.closure != null;
	}

	public String toString2() {
		if (this.closure != null) {
			return this.closure.toString2(this.pc);
		} else {
			return this.javaFunction != null ? "Callframe at: " + this.javaFunction.toString() : super.toString();
		}
	}

	public String toString() {
		if (this.closure != null) {
			return "Callframe at: " + this.closure.toString();
		} else {
			return this.javaFunction != null ? "Callframe at: " + this.javaFunction.toString() : super.toString();
		}
	}

	public Platform getPlatform() {
		return this.platform;
	}

	void setup(LuaClosure luaClosure, JavaFunction javaFunction, int int1, int int2, int int3, boolean boolean1, boolean boolean2) {
		this.localBase = int1;
		this.returnBase = int2;
		this.nArguments = int3;
		this.fromLua = boolean1;
		this.canYield = boolean2;
		this.closure = luaClosure;
		this.javaFunction = javaFunction;
		LuaCallFrame luaCallFrame = this;
		this.localsAssigned = 0;
		this.LocalVarToStackMap.clear();
		this.LocalStackToVarMap.clear();
		this.LocalVarNames.clear();
		if (Core.bDebug && this != null && this.closure != null && this.getThread() == LuaManager.thread) {
			for (int int4 = int1; int4 < int1 + int3; ++int4) {
				int int5 = luaCallFrame.closure.prototype.lines[0];
				if (luaCallFrame.closure.prototype.locvarlines != null && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] < int5 && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
					int int6 = luaCallFrame.localsAssigned++;
					String string = luaCallFrame.closure.prototype.locvars[int6];
					if (string.equals("group")) {
						boolean boolean3 = false;
					}

					luaCallFrame.setLocalVarToStack(string, int4);
				}
			}
		}
	}

	public KahluaThread getThread() {
		return this.coroutine.getThread();
	}

	public LuaClosure getClosure() {
		return this.closure;
	}

	public void setLocalVarToStack(String string, int int1) {
		this.LocalVarToStackMap.put(string, int1);
		this.LocalStackToVarMap.put(int1, string);
		this.LocalVarNames.add(string);
	}

	public String getNameOfStack(int int1) {
		return this.LocalStackToVarMap.get(int1) instanceof String ? (String)this.LocalStackToVarMap.get(int1) : "";
	}

	public void printoutLocalVars() {
	}
}
