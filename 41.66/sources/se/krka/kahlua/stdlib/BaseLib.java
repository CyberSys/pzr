package se.krka.kahlua.stdlib;

import java.util.function.Consumer;
import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaException;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import zombie.debug.DebugLog;


public final class BaseLib implements JavaFunction {
	private static final Runtime RUNTIME = Runtime.getRuntime();
	private static final int PCALL = 0;
	private static final int PRINT = 1;
	private static final int SELECT = 2;
	private static final int TYPE = 3;
	private static final int TOSTRING = 4;
	private static final int TONUMBER = 5;
	private static final int GETMETATABLE = 6;
	private static final int SETMETATABLE = 7;
	private static final int ERROR = 8;
	private static final int UNPACK = 9;
	private static final int SETFENV = 10;
	private static final int GETFENV = 11;
	private static final int RAWEQUAL = 12;
	private static final int RAWSET = 13;
	private static final int RAWGET = 14;
	private static final int COLLECTGARBAGE = 15;
	private static final int DEBUGSTACKTRACE = 16;
	private static final int BYTECODELOADER = 17;
	private static final int NUM_FUNCTIONS = 18;
	private static final String[] names = new String[18];
	private static final Object DOUBLE_ONE = new Double(1.0);
	private static final BaseLib[] functions;
	private final int index;
	private static Consumer PRINT_CALLBACK;

	public BaseLib(int int1) {
		this.index = int1;
	}

	public static void register(KahluaTable kahluaTable) {
		for (int int1 = 0; int1 < 18; ++int1) {
			kahluaTable.rawset(names[int1], functions[int1]);
		}
	}

	public String toString() {
		return names[this.index];
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		switch (this.index) {
		case 0: 
			return pcall(luaCallFrame, int1);
		
		case 1: 
			return print(luaCallFrame, int1);
		
		case 2: 
			return select(luaCallFrame, int1);
		
		case 3: 
			return type(luaCallFrame, int1);
		
		case 4: 
			return tostring(luaCallFrame, int1);
		
		case 5: 
			return tonumber(luaCallFrame, int1);
		
		case 6: 
			return getmetatable(luaCallFrame, int1);
		
		case 7: 
			return setmetatable(luaCallFrame, int1);
		
		case 8: 
			return this.error(luaCallFrame, int1);
		
		case 9: 
			return this.unpack(luaCallFrame, int1);
		
		case 10: 
			return this.setfenv(luaCallFrame, int1);
		
		case 11: 
			return this.getfenv(luaCallFrame, int1);
		
		case 12: 
			return this.rawequal(luaCallFrame, int1);
		
		case 13: 
			return this.rawset(luaCallFrame, int1);
		
		case 14: 
			return this.rawget(luaCallFrame, int1);
		
		case 15: 
			return collectgarbage(luaCallFrame, int1);
		
		case 16: 
			return this.debugstacktrace(luaCallFrame, int1);
		
		case 17: 
			return bytecodeloader(luaCallFrame, int1);
		
		default: 
			return 0;
		
		}
	}

	private int debugstacktrace(LuaCallFrame luaCallFrame, int int1) {
		Coroutine coroutine = (Coroutine)KahluaUtil.getOptionalArg(luaCallFrame, 1);
		if (coroutine == null) {
			coroutine = luaCallFrame.coroutine;
		}

		Double Double1 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 2);
		int int2 = 0;
		if (Double1 != null) {
			int2 = Double1.intValue();
		}

		Double Double2 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 3);
		int int3 = Integer.MAX_VALUE;
		if (Double2 != null) {
			int3 = Double2.intValue();
		}

		Double Double3 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 4);
		int int4 = 0;
		if (Double3 != null) {
			int4 = Double3.intValue();
		}

		return luaCallFrame.push(coroutine.getCurrentStackTrace(int2, int3, int4));
	}

	private int rawget(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "Not enough arguments");
		KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(0);
		Object object = luaCallFrame.get(1);
		luaCallFrame.push(kahluaTable.rawget(object));
		return 1;
	}

	private int rawset(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 3, "Not enough arguments");
		KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(0);
		Object object = luaCallFrame.get(1);
		Object object2 = luaCallFrame.get(2);
		kahluaTable.rawset(object, object2);
		luaCallFrame.setTop(1);
		return 1;
	}

	private int rawequal(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "Not enough arguments");
		Object object = luaCallFrame.get(0);
		Object object2 = luaCallFrame.get(1);
		luaCallFrame.push(KahluaUtil.toBoolean(luaEquals(object, object2)));
		return 1;
	}

	private int setfenv(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "Not enough arguments");
		KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(1);
		KahluaUtil.luaAssert(kahluaTable != null, "expected a table");
		LuaClosure luaClosure = null;
		Object object = luaCallFrame.get(0);
		if (object instanceof LuaClosure) {
			luaClosure = (LuaClosure)object;
		} else {
			Double Double1 = KahluaUtil.rawTonumber(object);
			KahluaUtil.luaAssert(Double1 != null, "expected a lua function or a number");
			int int2 = ((Double)Double1).intValue();
			if (int2 == 0) {
				luaCallFrame.coroutine.environment = kahluaTable;
				return 0;
			}

			LuaCallFrame luaCallFrame2 = luaCallFrame.coroutine.getParent(int2);
			if (!luaCallFrame2.isLua()) {
				KahluaUtil.fail("No closure found at this level: " + int2);
			}

			luaClosure = luaCallFrame2.closure;
		}

		luaClosure.env = kahluaTable;
		luaCallFrame.setTop(1);
		return 1;
	}

	private int getfenv(LuaCallFrame luaCallFrame, int int1) {
		Object object = DOUBLE_ONE;
		if (int1 >= 1) {
			object = luaCallFrame.get(0);
		}

		KahluaTable kahluaTable = null;
		if (object != null && !(object instanceof JavaFunction)) {
			if (object instanceof LuaClosure) {
				LuaClosure luaClosure = (LuaClosure)object;
				kahluaTable = luaClosure.env;
			} else {
				Double Double1 = KahluaUtil.rawTonumber(object);
				KahluaUtil.luaAssert(Double1 != null, "Expected number");
				int int2 = Double1.intValue();
				KahluaUtil.luaAssert(int2 >= 0, "level must be non-negative");
				LuaCallFrame luaCallFrame2 = luaCallFrame.coroutine.getParent(int2);
				kahluaTable = luaCallFrame2.getEnvironment();
			}
		} else {
			kahluaTable = luaCallFrame.coroutine.environment;
		}

		luaCallFrame.push(kahluaTable);
		return 1;
	}

	private int unpack(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(0);
		Object object = null;
		Object object2 = null;
		if (int1 >= 2) {
			object = luaCallFrame.get(1);
		}

		if (int1 >= 3) {
			object2 = luaCallFrame.get(2);
		}

		int int2;
		if (object != null) {
			int2 = (int)KahluaUtil.fromDouble(object);
		} else {
			int2 = 1;
		}

		int int3;
		if (object2 != null) {
			int3 = (int)KahluaUtil.fromDouble(object2);
		} else {
			int3 = kahluaTable.len();
		}

		int int4 = 1 + int3 - int2;
		if (int4 <= 0) {
			luaCallFrame.setTop(0);
			return 0;
		} else {
			luaCallFrame.setTop(int4);
			for (int int5 = 0; int5 < int4; ++int5) {
				luaCallFrame.set(int5, kahluaTable.rawget(KahluaUtil.toDouble((long)(int2 + int5))));
			}

			return int4;
		}
	}

	private int error(LuaCallFrame luaCallFrame, int int1) {
		if (int1 >= 1) {
			String string = KahluaUtil.getOptionalStringArg(luaCallFrame, 2);
			if (string == null) {
				string = "";
			}

			luaCallFrame.coroutine.stackTrace = string;
			throw new KahluaException(luaCallFrame.get(0));
		} else {
			return 0;
		}
	}

	public static int pcall(LuaCallFrame luaCallFrame, int int1) {
		return luaCallFrame.getThread().pcall(int1 - 1);
	}

	private static int print(LuaCallFrame luaCallFrame, int int1) {
		KahluaThread kahluaThread = luaCallFrame.getThread();
		KahluaTable kahluaTable = kahluaThread.getEnvironment();
		Object object = kahluaThread.tableget(kahluaTable, "tostring");
		StringBuilder stringBuilder = new StringBuilder();
		for (int int2 = 0; int2 < int1; ++int2) {
			if (int2 > 0) {
				stringBuilder.append("\t");
			}

			Object object2 = kahluaThread.call(object, luaCallFrame.get(int2), (Object)null, (Object)null);
			stringBuilder.append(object2);
		}

		String string = stringBuilder.toString();
		DebugLog.log(string);
		if (PRINT_CALLBACK != null) {
			PRINT_CALLBACK.accept(string);
		}

		return 0;
	}

	public static void setPrintCallback(Consumer consumer) {
		PRINT_CALLBACK = consumer;
	}

	private static int select(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		Object object = luaCallFrame.get(0);
		if (object instanceof String && ((String)object).startsWith("#")) {
			luaCallFrame.push(KahluaUtil.toDouble((long)(int1 - 1)));
			return 1;
		} else {
			Double Double1 = KahluaUtil.rawTonumber(object);
			double double1 = KahluaUtil.fromDouble(Double1);
			int int2 = (int)double1;
			if (int2 >= 1 && int2 <= int1 - 1) {
				int int3 = int1 - int2;
				return int3;
			} else {
				return 0;
			}
		}
	}

	private static int getmetatable(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		Object object = luaCallFrame.get(0);
		Object object2 = luaCallFrame.getThread().getmetatable(object, false);
		luaCallFrame.push(object2);
		return 1;
	}

	private static int setmetatable(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "Not enough arguments");
		Object object = luaCallFrame.get(0);
		KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(1);
		setmetatable(luaCallFrame.getThread(), object, kahluaTable, false);
		luaCallFrame.setTop(1);
		return 1;
	}

	public static void setmetatable(KahluaThread kahluaThread, Object object, KahluaTable kahluaTable, boolean boolean1) {
		KahluaUtil.luaAssert(object != null, "Expected table, got nil");
		Object object2 = kahluaThread.getmetatable(object, true);
		if (!boolean1 && object2 != null && kahluaThread.tableget(object2, "__metatable") != null) {
			throw new RuntimeException("cannot change a protected metatable");
		} else {
			kahluaThread.setmetatable(object, kahluaTable);
		}
	}

	private static int type(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		Object object = luaCallFrame.get(0);
		luaCallFrame.push(KahluaUtil.type(object));
		return 1;
	}

	private static int tostring(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		Object object = luaCallFrame.get(0);
		String string = KahluaUtil.tostring(object, luaCallFrame.getThread());
		luaCallFrame.push(string);
		return 1;
	}

	private static int tonumber(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "Not enough arguments");
		Object object = luaCallFrame.get(0);
		if (int1 == 1) {
			luaCallFrame.push(KahluaUtil.rawTonumber(object));
			return 1;
		} else {
			String string = (String)object;
			Object object2 = luaCallFrame.get(1);
			Double Double1 = KahluaUtil.rawTonumber(object2);
			if (Double1 == null) {
				luaCallFrame.push((Object)null);
				return 1;
			} else {
				KahluaUtil.luaAssert(Double1 != null, "Argument 2 must be a number");
				double double1 = KahluaUtil.fromDouble(Double1);
				int int2 = (int)double1;
				if ((double)int2 != double1) {
					luaCallFrame.push((Object)null);
					return 1;
				} else if ((double)int2 != double1) {
					throw new RuntimeException("base is not an integer");
				} else {
					Double Double2 = KahluaUtil.tonumber(string, int2);
					luaCallFrame.push(Double2);
					return 1;
				}
			}
		}
	}

	public static int collectgarbage(LuaCallFrame luaCallFrame, int int1) {
		Object object = null;
		if (int1 > 0) {
			object = luaCallFrame.get(0);
		}

		if (object != null && !object.equals("step") && !object.equals("collect")) {
			if (object.equals("count")) {
				long long1 = RUNTIME.freeMemory();
				long long2 = RUNTIME.totalMemory();
				luaCallFrame.setTop(3);
				luaCallFrame.set(0, toKiloBytes(long2 - long1));
				luaCallFrame.set(1, toKiloBytes(long1));
				luaCallFrame.set(2, toKiloBytes(long2));
				return 3;
			} else {
				throw new RuntimeException("invalid option: " + object);
			}
		} else {
			System.gc();
			return 0;
		}
	}

	private static Double toKiloBytes(long long1) {
		return KahluaUtil.toDouble((double)long1 / 1024.0);
	}

	private static int bytecodeloader(LuaCallFrame luaCallFrame, int int1) {
		String string = KahluaUtil.getStringArg(luaCallFrame, 1, "loader");
		KahluaTable kahluaTable = (KahluaTable)luaCallFrame.getEnvironment().rawget("package");
		String string2 = (String)kahluaTable.rawget("classpath");
		int int2;
		for (int int3 = 0; int3 < string2.length(); int3 = int2) {
			int2 = string2.indexOf(";", int3);
			if (int2 == -1) {
				int2 = string2.length();
			}

			String string3 = string2.substring(int3, int2);
			if (string3.length() > 0) {
				if (!string3.endsWith("/")) {
					string3 = string3 + "/";
				}

				LuaClosure luaClosure = KahluaUtil.loadByteCodeFromResource(string3 + string, luaCallFrame.getEnvironment());
				if (luaClosure != null) {
					return luaCallFrame.push(luaClosure);
				}
			}
		}

		return luaCallFrame.push("Could not find the bytecode for \'" + string + "\' in classpath");
	}

	public static boolean luaEquals(Object object, Object object2) {
		if (object != null && object2 != null) {
			if (object instanceof Double && object2 instanceof Double) {
				Double Double1 = (Double)object;
				Double Double2 = (Double)object2;
				return Double1 == Double2;
			} else {
				return object == object2;
			}
		} else {
			return object == object2;
		}
	}

	static  {
		names[0] = "pcall";
		names[1] = "print";
		names[2] = "select";
		names[3] = "type";
		names[4] = "tostring";
		names[5] = "tonumber";
		names[6] = "getmetatable";
		names[7] = "setmetatable";
		names[8] = "error";
		names[9] = "unpack";
		names[10] = "setfenv";
		names[11] = "getfenv";
		names[12] = "rawequal";
		names[13] = "rawset";
		names[14] = "rawget";
		names[15] = "collectgarbage";
		names[16] = "debugstacktrace";
		names[17] = "bytecodeloader";
		functions = new BaseLib[18];
	for (int var0 = 0; var0 < 18; ++var0) {
		functions[var0] = new BaseLib(var0);
	}

		PRINT_CALLBACK = null;
	}
}
