package se.krka.kahlua.vm;

import java.io.IOException;
import java.io.InputStream;
import se.krka.kahlua.integration.expose.LuaJavaInvoker;
import se.krka.kahlua.integration.expose.MethodDebugInformation;
import zombie.Lua.LuaManager;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.ui.UIManager;


public class KahluaUtil {
	private static final Object WORKER_THREAD_KEY = new Object();
	private static final String TYPE_NIL = "nil";
	private static final String TYPE_STRING = "string";
	private static final String TYPE_NUMBER = "number";
	private static final String TYPE_BOOLEAN = "boolean";
	private static final String TYPE_FUNCTION = "function";
	private static final String TYPE_TABLE = "table";
	private static final String TYPE_COROUTINE = "coroutine";
	private static final String TYPE_USERDATA = "userdata";

	public static double fromDouble(Object object) {
		return (Double)object;
	}

	public static Double toDouble(double double1) {
		return BoxedStaticValues.toDouble(double1);
	}

	public static Double toDouble(long long1) {
		return BoxedStaticValues.toDouble((double)long1);
	}

	public static Boolean toBoolean(boolean boolean1) {
		return boolean1 ? Boolean.TRUE : Boolean.FALSE;
	}

	public static boolean boolEval(Object object) {
		return object != null && object != Boolean.FALSE;
	}

	public static LuaClosure loadByteCodeFromResource(String string, KahluaTable kahluaTable) {
		InputStream inputStream = kahluaTable.getClass().getResourceAsStream(string + ".lbc");
		if (inputStream == null) {
			return null;
		} else {
			try {
				return Prototype.loadByteCode(inputStream, kahluaTable);
			} catch (IOException ioException) {
				throw new RuntimeException(ioException.getMessage());
			}
		}
	}

	public static void luaAssert(boolean boolean1, String string) {
		if (!boolean1) {
			fail(string);
		}
	}

	public static void fail(String string) {
		if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
			DebugLog.log(string);
			UIManager.debugBreakpoint(LuaManager.thread.currentfile, (long)(LuaManager.thread.currentLine - 1));
		}

		throw new RuntimeException(string);
	}

	public static double round(double double1) {
		if (double1 < 0.0) {
			return -round(-double1);
		} else {
			double1 += 0.5;
			double double2 = Math.floor(double1);
			return double2 == double1 ? double2 - (double)((long)double2 & 1L) : double2;
		}
	}

	public static long ipow(long long1, int int1) {
		if (int1 <= 0) {
			return 1L;
		} else {
			long long2 = 1L;
			long2 = (int1 & 1) != 0 ? long1 : 1L;
			for (int1 >>= 1; int1 != 0; int1 >>= 1) {
				long1 *= long1;
				if ((int1 & 1) != 0) {
					long2 *= long1;
				}
			}

			return long2;
		}
	}

	public static boolean isNegative(double double1) {
		return Double.doubleToLongBits(double1) < 0L;
	}

	public static KahluaTable getClassMetatables(Platform platform, KahluaTable kahluaTable) {
		return getOrCreateTable(platform, kahluaTable, "__classmetatables");
	}

	public static KahluaThread getWorkerThread(Platform platform, KahluaTable kahluaTable) {
		Object object = kahluaTable.rawget(WORKER_THREAD_KEY);
		if (object == null) {
			object = new KahluaThread(platform, kahluaTable);
			kahluaTable.rawset(WORKER_THREAD_KEY, object);
		}

		return (KahluaThread)object;
	}

	public static void setWorkerThread(KahluaTable kahluaTable, KahluaThread kahluaThread) {
		kahluaTable.rawset(WORKER_THREAD_KEY, kahluaThread);
	}

	public static KahluaTable getOrCreateTable(Platform platform, KahluaTable kahluaTable, String string) {
		Object object = kahluaTable.rawget(string);
		if (object == null || !(object instanceof KahluaTable)) {
			object = platform.newTable();
			kahluaTable.rawset(string, object);
		}

		return (KahluaTable)object;
	}

	public static void setupLibrary(KahluaTable kahluaTable, KahluaThread kahluaThread, String string) {
		LuaClosure luaClosure = loadByteCodeFromResource(string, kahluaTable);
		if (luaClosure == null) {
			fail("Could not load " + string + ".lbc");
		}

		kahluaThread.call(luaClosure, (Object)null, (Object)null, (Object)null);
	}

	public static String numberToString(Double Double1) {
		if (Double1.isNaN()) {
			return "nan";
		} else if (Double1.isInfinite()) {
			return isNegative(Double1) ? "-inf" : "inf";
		} else {
			double double1 = Double1;
			return Math.floor(double1) == double1 && Math.abs(double1) < 1.0E14 ? String.valueOf(Double1.longValue()) : Double1.toString();
		}
	}

	public static String type(Object object) {
		if (object == null) {
			return "nil";
		} else if (object instanceof String) {
			return "string";
		} else if (object instanceof Double) {
			return "number";
		} else if (object instanceof Boolean) {
			return "boolean";
		} else if (!(object instanceof JavaFunction) && !(object instanceof LuaClosure)) {
			if (object instanceof KahluaTable) {
				return "table";
			} else {
				return object instanceof Coroutine ? "coroutine" : "userdata";
			}
		} else {
			return "function";
		}
	}

	public static String tostring(Object object, KahluaThread kahluaThread) {
		if (object == null) {
			return "nil";
		} else if (object instanceof String) {
			return (String)object;
		} else if (object instanceof Double) {
			return rawTostring(object);
		} else if (object instanceof Boolean) {
			return object == Boolean.TRUE ? "true" : "false";
		} else if (object instanceof LuaClosure) {
			return "closure 0x" + System.identityHashCode(object);
		} else if (object instanceof JavaFunction) {
			return "function 0x" + System.identityHashCode(object);
		} else {
			if (kahluaThread != null) {
				Object object2 = kahluaThread.getMetaOp(object, "__tostring");
				if (object2 != null) {
					String string = (String)kahluaThread.call(object2, object, (Object)null, (Object)null);
					return string;
				}
			}

			return object.toString();
		}
	}

	public static Double tonumber(String string) {
		return tonumber(string, 10);
	}

	public static Double tonumber(String string, int int1) {
		if (int1 >= 2 && int1 <= 36) {
			try {
				return int1 == 10 ? Double.valueOf(string) : toDouble((long)Integer.parseInt(string, int1));
			} catch (NumberFormatException numberFormatException) {
				string = string.toLowerCase();
				if (string.endsWith("nan")) {
					return toDouble(Double.NaN);
				} else if (string.endsWith("inf")) {
					return string.charAt(0) == '-' ? toDouble(Double.NEGATIVE_INFINITY) : toDouble(Double.POSITIVE_INFINITY);
				} else {
					return null;
				}
			}
		} else {
			throw new RuntimeException("base out of range");
		}
	}

	public static String rawTostring(Object object) {
		if (object instanceof String) {
			return (String)object;
		} else {
			return object instanceof Double ? numberToString((Double)object) : null;
		}
	}

	public static String rawTostring2(Object object) {
		if (object instanceof String) {
			return "\"" + (String)object + "\"";
		} else if (object instanceof Texture) {
			return "Texture: \"" + ((Texture)object).getName() + "\"";
		} else if (object instanceof Double) {
			return numberToString((Double)object);
		} else if (object instanceof LuaClosure) {
			LuaClosure luaClosure = (LuaClosure)object;
			return luaClosure.toString2(0);
		} else if (object instanceof LuaCallFrame) {
			LuaCallFrame luaCallFrame = (LuaCallFrame)object;
			return luaCallFrame.toString2();
		} else if (object instanceof LuaJavaInvoker) {
			if (object.toString().equals("breakpoint")) {
				return null;
			} else {
				LuaJavaInvoker luaJavaInvoker = (LuaJavaInvoker)object;
				MethodDebugInformation methodDebugInformation = luaJavaInvoker.getMethodDebugData();
				String string = "";
				for (int int1 = 0; int1 < methodDebugInformation.getParameters().size(); ++int1) {
					if (methodDebugInformation.getParameters().get(int1) != null) {
						string = string + methodDebugInformation.getParameters().get(int1);
					}
				}

				return "Java: " + methodDebugInformation.getReturnType() + " " + object.toString() + "(" + string + ")";
			}
		} else {
			return object != null ? object.toString() : null;
		}
	}

	public static Double rawTonumber(Object object) {
		if (object instanceof Double) {
			return (Double)object;
		} else {
			return object instanceof String ? tonumber((String)object) : null;
		}
	}

	public static String getStringArg(LuaCallFrame luaCallFrame, int int1, String string) {
		Object object = getArg(luaCallFrame, int1, string);
		String string2 = rawTostring(object);
		if (string2 == null) {
			fail(int1, string, "string", type(string2));
		}

		return string2;
	}

	public static String getOptionalStringArg(LuaCallFrame luaCallFrame, int int1) {
		Object object = getOptionalArg(luaCallFrame, int1);
		return rawTostring(object);
	}

	public static Double getNumberArg(LuaCallFrame luaCallFrame, int int1, String string) {
		Object object = getArg(luaCallFrame, int1, string);
		Double Double1 = rawTonumber(object);
		if (Double1 == null) {
			fail(int1, string, "double", type(Double1));
		}

		return Double1;
	}

	public static Double getOptionalNumberArg(LuaCallFrame luaCallFrame, int int1) {
		Object object = getOptionalArg(luaCallFrame, int1);
		return rawTonumber(object);
	}

	private static void fail(int int1, String string, String string2, String string3) {
		throw new RuntimeException("bad argument #" + int1 + " to \'" + string + "\' (" + string2 + " expected, got " + string3 + ")");
	}

	public static void assertArgNotNull(Object object, int int1, String string, String string2) {
		if (object == null) {
			fail(int1, string2, string, "null");
		}
	}

	public static Object getOptionalArg(LuaCallFrame luaCallFrame, int int1) {
		int int2 = luaCallFrame.getTop();
		int int3 = int1 - 1;
		return int3 >= int2 ? null : luaCallFrame.get(int1 - 1);
	}

	public static Object getArg(LuaCallFrame luaCallFrame, int int1, String string) {
		Object object = getOptionalArg(luaCallFrame, int1);
		if (object == null) {
			throw new RuntimeException("missing argument #" + int1 + "to \'" + string + "\'");
		} else {
			return object;
		}
	}

	public static int len(KahluaTable kahluaTable, int int1, int int2) {
		while (int1 < int2) {
			int int3 = int2 + int1 + 1 >> 1;
			Object object = kahluaTable.rawget(int3);
			if (object == null) {
				int2 = int3 - 1;
			} else {
				int1 = int3;
			}
		}

		while (kahluaTable.rawget(int1 + 1) != null) {
			++int1;
		}

		return int1;
	}

	public static double getDoubleArg(LuaCallFrame luaCallFrame, int int1, String string) {
		return getNumberArg(luaCallFrame, int1, string);
	}
}
