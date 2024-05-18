package se.krka.kahlua.test;

import java.util.Vector;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;


public class UserdataArray implements JavaFunction {
	private static final int LENGTH = 0;
	private static final int INDEX = 1;
	private static final int NEWINDEX = 2;
	private static final int NEW = 3;
	private static final int PUSH = 4;
	private static final Class VECTOR_CLASS = (new Vector()).getClass();
	private static KahluaTable metatable;
	private int index;

	public static synchronized void register(Platform platform, KahluaTable kahluaTable) {
		if (metatable == null) {
			metatable = platform.newTable();
			metatable.rawset("__metatable", "restricted");
			metatable.rawset("__len", new UserdataArray(0));
			metatable.rawset("__index", new UserdataArray(1));
			metatable.rawset("__newindex", new UserdataArray(2));
			metatable.rawset("new", new UserdataArray(3));
			metatable.rawset("push", new UserdataArray(4));
		}

		KahluaTable kahluaTable2 = KahluaUtil.getClassMetatables(platform, kahluaTable);
		kahluaTable2.rawset(VECTOR_CLASS, metatable);
		kahluaTable.rawset("array", metatable);
	}

	private UserdataArray(int int1) {
		this.index = int1;
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		switch (this.index) {
		case 0: 
			return this.length(luaCallFrame, int1);
		
		case 1: 
			return this.index(luaCallFrame, int1);
		
		case 2: 
			return this.newindex(luaCallFrame, int1);
		
		case 3: 
			return this.newVector(luaCallFrame, int1);
		
		case 4: 
			return this.push(luaCallFrame, int1);
		
		default: 
			return 0;
		
		}
	}

	private int push(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "not enough parameters");
		Vector vector = (Vector)luaCallFrame.get(0);
		Object object = luaCallFrame.get(1);
		vector.addElement(object);
		luaCallFrame.push(vector);
		return 1;
	}

	private int newVector(LuaCallFrame luaCallFrame, int int1) {
		luaCallFrame.push(new Vector());
		return 1;
	}

	private int newindex(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 3, "not enough parameters");
		Vector vector = (Vector)luaCallFrame.get(0);
		Object object = luaCallFrame.get(1);
		Object object2 = luaCallFrame.get(2);
		vector.setElementAt(object2, (int)KahluaUtil.fromDouble(object));
		return 0;
	}

	private int index(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "not enough parameters");
		Object object = luaCallFrame.get(0);
		if (object != null && object instanceof Vector) {
			Vector vector = (Vector)object;
			Object object2 = luaCallFrame.get(1);
			Object object3;
			if (object2 instanceof Double) {
				object3 = vector.elementAt((int)KahluaUtil.fromDouble(object2));
			} else {
				object3 = metatable.rawget(object2);
			}

			luaCallFrame.push(object3);
			return 1;
		} else {
			return 0;
		}
	}

	private int length(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "not enough parameters");
		Vector vector = (Vector)luaCallFrame.get(0);
		double double1 = (double)vector.size();
		luaCallFrame.push(KahluaUtil.toDouble(double1));
		return 1;
	}
}
