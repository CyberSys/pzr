package se.krka.kahlua.integration.expose;

import java.util.ArrayList;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.vm.LuaCallFrame;


public class ReturnValues {
	private KahluaConverterManager manager;
	private LuaCallFrame callFrame;
	private int args;
	static ArrayList[] Lists = new ArrayList[1];

	public static ReturnValues get(KahluaConverterManager kahluaConverterManager, LuaCallFrame luaCallFrame) {
		ReturnValues returnValues = null;
		if (Lists[0].isEmpty()) {
			returnValues = new ReturnValues(kahluaConverterManager, luaCallFrame);
		} else {
			returnValues = (ReturnValues)Lists[0].get(0);
			Lists[0].remove(returnValues);
		}

		returnValues.manager = kahluaConverterManager;
		returnValues.callFrame = luaCallFrame;
		return returnValues;
	}

	public static void put(ReturnValues returnValues) {
		returnValues.callFrame = null;
		returnValues.manager = null;
		returnValues.args = 0;
		if (!Lists[0].contains(returnValues)) {
			Lists[0].add(returnValues);
		}
	}

	ReturnValues(KahluaConverterManager kahluaConverterManager, LuaCallFrame luaCallFrame) {
		this.manager = kahluaConverterManager;
		this.callFrame = luaCallFrame;
	}

	public ReturnValues push(Object object) {
		this.args += this.callFrame.push(this.manager.fromJavaToLua(object));
		return this;
	}

	public ReturnValues push(Object[] objectArray) {
		Object[] objectArray2 = objectArray;
		int int1 = objectArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			Object object = objectArray2[int2];
			this.push(object);
		}

		return this;
	}

	int getNArguments() {
		return this.args;
	}

	static  {
	for (int var0 = 0; var0 < 1; ++var0) {
		Lists[var0] = new ArrayList(100);
		for (int var1 = 0; var1 < 100; ++var1) {
			Lists[var0].add(new ReturnValues((KahluaConverterManager)null, (LuaCallFrame)null));
		}
	}
	}
}
