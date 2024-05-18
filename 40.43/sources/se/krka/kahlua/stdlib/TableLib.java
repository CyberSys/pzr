package se.krka.kahlua.stdlib;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaArray;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;


public final class TableLib implements JavaFunction {
	private static final int CONCAT = 0;
	private static final int INSERT = 1;
	private static final int REMOVE = 2;
	private static final int NEWARRAY = 3;
	private static final int PAIRS = 4;
	private static final int ISEMPTY = 5;
	private static final int WIPE = 6;
	private static final int NUM_FUNCTIONS = 7;
	private static final String[] names = new String[7];
	private static final TableLib[] functions;
	private final int index;

	public TableLib(int int1) {
		this.index = int1;
	}

	public static void register(Platform platform, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		for (int int1 = 0; int1 < 7; ++int1) {
			kahluaTable2.rawset(names[int1], functions[int1]);
		}

		kahluaTable.rawset("table", kahluaTable2);
	}

	public String toString() {
		return this.index < names.length ? "table." + names[this.index] : super.toString();
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		switch (this.index) {
		case 0: 
			return concat(luaCallFrame, int1);
		
		case 1: 
			return insert(luaCallFrame, int1);
		
		case 2: 
			return remove(luaCallFrame, int1);
		
		case 3: 
			return this.newarray(luaCallFrame, int1);
		
		case 4: 
			return this.pairs(luaCallFrame, int1);
		
		case 5: 
			return this.isempty(luaCallFrame, int1);
		
		case 6: 
			return this.wipe(luaCallFrame, int1);
		
		default: 
			return 0;
		
		}
	}

	private int wipe(LuaCallFrame luaCallFrame, int int1) {
		KahluaTable kahluaTable = getTable(luaCallFrame, int1);
		kahluaTable.wipe();
		return 0;
	}

	private int isempty(LuaCallFrame luaCallFrame, int int1) {
		KahluaTable kahluaTable = getTable(luaCallFrame, int1);
		return luaCallFrame.push(KahluaUtil.toBoolean(kahluaTable.isEmpty()));
	}

	private int pairs(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		Object object = luaCallFrame.get(0);
		KahluaUtil.luaAssert(object instanceof KahluaTable, "Expected a table");
		KahluaTable kahluaTable = (KahluaTable)object;
		return luaCallFrame.push(kahluaTable.iterator());
	}

	private int newarray(LuaCallFrame luaCallFrame, int int1) {
		Object object = KahluaUtil.getOptionalArg(luaCallFrame, 1);
		KahluaArray kahluaArray = new KahluaArray();
		if (object instanceof KahluaTable && int1 == 1) {
			KahluaTable kahluaTable = (KahluaTable)object;
			int int2 = kahluaTable.len();
			for (int int3 = int2; int3 >= 1; --int3) {
				kahluaArray.rawset(int3, kahluaTable.rawget(int3));
			}
		} else {
			for (int int4 = int1; int4 >= 1; --int4) {
				kahluaArray.rawset(int4, luaCallFrame.get(int4 - 1));
			}
		}

		return luaCallFrame.push(kahluaArray);
	}

	private static int concat(LuaCallFrame luaCallFrame, int int1) {
		KahluaTable kahluaTable = getTable(luaCallFrame, int1);
		String string = "";
		if (int1 >= 2) {
			string = KahluaUtil.rawTostring(luaCallFrame.get(1));
		}

		int int2 = 1;
		if (int1 >= 3) {
			Double Double1 = KahluaUtil.rawTonumber(luaCallFrame.get(2));
			int2 = Double1.intValue();
		}

		int int3;
		if (int1 >= 4) {
			Double Double2 = KahluaUtil.rawTonumber(luaCallFrame.get(3));
			int3 = Double2.intValue();
		} else {
			int3 = kahluaTable.len();
		}

		StringBuffer stringBuffer = new StringBuffer();
		for (int int4 = int2; int4 <= int3; ++int4) {
			if (int4 > int2) {
				stringBuffer.append(string);
			}

			Double Double3 = KahluaUtil.toDouble((long)int4);
			Object object = kahluaTable.rawget(Double3);
			stringBuffer.append(KahluaUtil.rawTostring(object));
		}

		return luaCallFrame.push(stringBuffer.toString());
	}

	public static void insert(KahluaThread kahluaThread, KahluaTable kahluaTable, Object object) {
		append(kahluaThread, kahluaTable, object);
	}

	public static void append(KahluaThread kahluaThread, KahluaTable kahluaTable, Object object) {
		int int1 = 1 + kahluaTable.len();
		kahluaThread.tableSet(kahluaTable, KahluaUtil.toDouble((long)int1), object);
	}

	public static void rawappend(KahluaTable kahluaTable, Object object) {
		int int1 = 1 + kahluaTable.len();
		kahluaTable.rawset(KahluaUtil.toDouble((long)int1), object);
	}

	public static void insert(KahluaThread kahluaThread, KahluaTable kahluaTable, int int1, Object object) {
		int int2 = kahluaTable.len();
		for (int int3 = int2; int3 >= int1; --int3) {
			kahluaThread.tableSet(kahluaTable, KahluaUtil.toDouble((long)(int3 + 1)), kahluaThread.tableget(kahluaTable, KahluaUtil.toDouble((long)int3)));
		}

		kahluaThread.tableSet(kahluaTable, KahluaUtil.toDouble((long)int1), object);
	}

	public static void rawinsert(KahluaTable kahluaTable, int int1, Object object) {
		int int2 = kahluaTable.len();
		if (int1 <= int2) {
			Double Double1 = KahluaUtil.toDouble((long)(int2 + 1));
			for (int int3 = int2; int3 >= int1; --int3) {
				Double Double2 = KahluaUtil.toDouble((long)int3);
				kahluaTable.rawset(Double1, kahluaTable.rawget(Double2));
				Double1 = Double2;
			}

			kahluaTable.rawset(Double1, object);
		} else {
			kahluaTable.rawset(KahluaUtil.toDouble((long)int1), object);
		}
	}

	private static int insert(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "Not enough arguments");
		KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(0);
		int int2 = kahluaTable.len() + 1;
		Object object;
		if (int1 > 2) {
			int2 = KahluaUtil.rawTonumber(luaCallFrame.get(1)).intValue();
			object = luaCallFrame.get(2);
		} else {
			object = luaCallFrame.get(1);
		}

		insert(luaCallFrame.getThread(), kahluaTable, int2, object);
		return 0;
	}

	public static Object remove(KahluaThread kahluaThread, KahluaTable kahluaTable) {
		return remove(kahluaThread, kahluaTable, kahluaTable.len());
	}

	public static Object remove(KahluaThread kahluaThread, KahluaTable kahluaTable, int int1) {
		Object object = kahluaThread.tableget(kahluaTable, KahluaUtil.toDouble((long)int1));
		int int2 = kahluaTable.len();
		for (int int3 = int1; int3 < int2; ++int3) {
			kahluaThread.tableSet(kahluaTable, KahluaUtil.toDouble((long)int3), kahluaThread.tableget(kahluaTable, KahluaUtil.toDouble((long)(int3 + 1))));
		}

		kahluaThread.tableSet(kahluaTable, KahluaUtil.toDouble((long)int2), (Object)null);
		return object;
	}

	private static int remove(LuaCallFrame luaCallFrame, int int1) {
		KahluaTable kahluaTable = getTable(luaCallFrame, int1);
		int int2 = kahluaTable.len();
		if (int1 > 1) {
			int2 = KahluaUtil.rawTonumber(luaCallFrame.get(1)).intValue();
		}

		luaCallFrame.push(remove(luaCallFrame.getThread(), kahluaTable, int2));
		return 1;
	}

	private static KahluaTable getTable(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "expected table, got no arguments");
		KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(0);
		return kahluaTable;
	}

	static  {
		names[0] = "concat";
		names[1] = "insert";
		names[2] = "remove";
		names[3] = "newarray";
		names[4] = "pairs";
		names[5] = "isempty";
		names[6] = "wipe";
		functions = new TableLib[7];
	for (int var0 = 0; var0 < 7; ++var0) {
		functions[var0] = new TableLib(var0);
	}
	}
}
