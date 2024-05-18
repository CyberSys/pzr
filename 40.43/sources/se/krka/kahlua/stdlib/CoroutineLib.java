package se.krka.kahlua.stdlib;

import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;


public class CoroutineLib implements JavaFunction {
	private static final int CREATE = 0;
	private static final int RESUME = 1;
	private static final int YIELD = 2;
	private static final int STATUS = 3;
	private static final int RUNNING = 4;
	private static final int NUM_FUNCTIONS = 5;
	private static final String[] names = new String[5];
	private static final Class COROUTINE_CLASS = (new Coroutine()).getClass();
	private final int index;
	private static final CoroutineLib[] functions;

	public String toString() {
		return "coroutine." + names[this.index];
	}

	public CoroutineLib(int int1) {
		this.index = int1;
	}

	public static void register(Platform platform, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		for (int int1 = 0; int1 < 5; ++int1) {
			kahluaTable2.rawset(names[int1], functions[int1]);
		}

		kahluaTable2.rawset("__index", kahluaTable2);
		KahluaTable kahluaTable3 = KahluaUtil.getClassMetatables(platform, kahluaTable);
		kahluaTable3.rawset(COROUTINE_CLASS, kahluaTable2);
		kahluaTable.rawset("coroutine", kahluaTable2);
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		switch (this.index) {
		case 0: 
			return this.create(luaCallFrame, int1);
		
		case 1: 
			return this.resume(luaCallFrame, int1);
		
		case 2: 
			return this.yield(luaCallFrame, int1);
		
		case 3: 
			return this.status(luaCallFrame, int1);
		
		case 4: 
			return this.running(luaCallFrame, int1);
		
		default: 
			return 0;
		
		}
	}

	private int running(LuaCallFrame luaCallFrame, int int1) {
		Coroutine coroutine = luaCallFrame.coroutine;
		if (coroutine.getStatus() != "normal") {
			coroutine = null;
		}

		return luaCallFrame.push(coroutine);
	}

	private int status(LuaCallFrame luaCallFrame, int int1) {
		Coroutine coroutine = this.getCoroutine(luaCallFrame, "status");
		return luaCallFrame.coroutine == coroutine ? luaCallFrame.push("running") : luaCallFrame.push(coroutine.getStatus());
	}

	private int resume(LuaCallFrame luaCallFrame, int int1) {
		Coroutine coroutine = this.getCoroutine(luaCallFrame, "resume");
		String string = coroutine.getStatus();
		if (string != "suspended") {
			KahluaUtil.fail("Can not resume coroutine that is in status: " + string);
		}

		Coroutine coroutine2 = luaCallFrame.coroutine;
		coroutine.resume(coroutine2);
		LuaCallFrame luaCallFrame2 = coroutine.currentCallFrame();
		if (luaCallFrame2.nArguments == -1) {
			luaCallFrame2.setTop(0);
		}

		for (int int2 = 1; int2 < int1; ++int2) {
			luaCallFrame2.push(luaCallFrame.get(int2));
		}

		if (luaCallFrame2.nArguments == -1) {
			luaCallFrame2.nArguments = int1 - 1;
			luaCallFrame2.init();
		}

		luaCallFrame.getThread().currentCoroutine = coroutine;
		return 0;
	}

	private static int yield(LuaCallFrame luaCallFrame, int int1) {
		Coroutine coroutine = luaCallFrame.coroutine;
		Coroutine coroutine2 = coroutine.getParent();
		KahluaUtil.luaAssert(coroutine2 != null, "Can not yield outside of a coroutine");
		LuaCallFrame luaCallFrame2 = coroutine.getCallFrame(-2);
		Coroutine.yieldHelper(luaCallFrame2, luaCallFrame, int1);
		return 0;
	}

	private int create(LuaCallFrame luaCallFrame, int int1) {
		LuaClosure luaClosure = this.getFunction(luaCallFrame, "create");
		Coroutine coroutine = new Coroutine(luaCallFrame.getPlatform(), luaCallFrame.getEnvironment());
		coroutine.pushNewCallFrame(luaClosure, (JavaFunction)null, 0, 0, -1, true, true);
		luaCallFrame.push(coroutine);
		return 1;
	}

	private LuaClosure getFunction(LuaCallFrame luaCallFrame, String string) {
		Object object = KahluaUtil.getArg(luaCallFrame, 1, string);
		KahluaUtil.luaAssert(object instanceof LuaClosure, "argument must be a lua function");
		LuaClosure luaClosure = (LuaClosure)object;
		return luaClosure;
	}

	private Coroutine getCoroutine(LuaCallFrame luaCallFrame, String string) {
		Object object = KahluaUtil.getArg(luaCallFrame, 1, string);
		KahluaUtil.luaAssert(object instanceof Coroutine, "argument must be a coroutine");
		Coroutine coroutine = (Coroutine)object;
		return coroutine;
	}

	static  {
		names[0] = "create";
		names[1] = "resume";
		names[2] = "yield";
		names[3] = "status";
		names[4] = "running";
		functions = new CoroutineLib[5];
	for (int var0 = 0; var0 < 5; ++var0) {
		functions[var0] = new CoroutineLib(var0);
	}
	}
}
