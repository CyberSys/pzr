package se.krka.kahlua.integration.expose;

import java.util.ArrayList;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.ui.UIManager;


public class MethodArguments {
	private ReturnValues returnValues;
	private Object self;
	private Object[] params;
	private String failure;
	private boolean bValid = true;
	static ArrayList[] Lists = new ArrayList[30];

	public static MethodArguments get(int int1) {
		MethodArguments methodArguments = null;
		if (Lists[int1].isEmpty()) {
			methodArguments = new MethodArguments(int1);
		} else {
			methodArguments = (MethodArguments)Lists[int1].get(0);
			Lists[int1].remove(methodArguments);
		}

		return methodArguments;
	}

	public static void put(MethodArguments methodArguments) {
		if (!Lists[methodArguments.params.length].contains(methodArguments)) {
			Lists[methodArguments.params.length].add(methodArguments);
			methodArguments.bValid = true;
			methodArguments.self = null;
			methodArguments.failure = null;
			methodArguments.returnValues = null;
			for (int int1 = 0; int1 < methodArguments.params.length; ++int1) {
				methodArguments.params[int1] = null;
			}
		}
	}

	public MethodArguments(int int1) {
		this.params = new Object[int1];
	}

	public ReturnValues getReturnValues() {
		return this.returnValues;
	}

	public Object getSelf() {
		return this.self;
	}

	public Object[] getParams() {
		return this.params;
	}

	public void fail(String string) {
		this.failure = string;
		this.bValid = false;
	}

	public void setSelf(Object object) {
		this.self = object;
	}

	public String getFailure() {
		return this.failure;
	}

	public void setReturnValues(ReturnValues returnValues) {
		this.returnValues = returnValues;
	}

	public void assertValid() {
		if (!this.isValid()) {
			if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
				UIManager.debugBreakpoint(LuaManager.thread.currentfile, (long)(LuaManager.thread.currentLine - 1));
			}

			throw new RuntimeException(this.failure);
		}
	}

	public boolean isValid() {
		return this.bValid;
	}

	static  {
	for (int var0 = 0; var0 < 30; ++var0) {
		Lists[var0] = new ArrayList(1000);
		for (int var1 = 0; var1 < 1000; ++var1) {
			Lists[var0].add(new MethodArguments(var0));
		}
	}
	}
}
