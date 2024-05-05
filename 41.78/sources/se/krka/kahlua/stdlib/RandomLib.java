package se.krka.kahlua.stdlib;

import java.util.Random;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;


public class RandomLib implements JavaFunction {
	private static final Class RANDOM_CLASS = (new Random()).getClass();
	private static final int RANDOM = 0;
	private static final int RANDOMSEED = 1;
	private static final int NEWRANDOM = 2;
	private static final int NUM_FUNCTIONS = 3;
	private static final String[] names = new String[3];
	private static final RandomLib[] functions;
	private static final RandomLib NEWRANDOM_FUN;
	private final int index;

	public RandomLib(int int1) {
		this.index = int1;
	}

	public static void register(Platform platform, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		for (int int1 = 0; int1 < 2; ++int1) {
			kahluaTable2.rawset(names[int1], functions[int1]);
		}

		kahluaTable2.rawset("__index", kahluaTable2);
		KahluaTable kahluaTable3 = KahluaUtil.getClassMetatables(platform, kahluaTable);
		kahluaTable3.rawset(RANDOM_CLASS, kahluaTable2);
		kahluaTable.rawset("newrandom", NEWRANDOM_FUN);
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		switch (this.index) {
		case 0: 
			return this.random(luaCallFrame, int1);
		
		case 1: 
			return this.randomSeed(luaCallFrame, int1);
		
		case 2: 
			return this.newRandom(luaCallFrame);
		
		default: 
			return 0;
		
		}
	}

	private int randomSeed(LuaCallFrame luaCallFrame, int int1) {
		Random random = this.getRandom(luaCallFrame, "seed");
		Object object = luaCallFrame.get(1);
		int int2 = object == null ? 0 : object.hashCode();
		random.setSeed((long)int2);
		return 0;
	}

	private int random(LuaCallFrame luaCallFrame, int int1) {
		Random random = this.getRandom(luaCallFrame, "random");
		Double Double1 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 2);
		Double Double2 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 3);
		if (Double1 == null) {
			return luaCallFrame.push(KahluaUtil.toDouble(random.nextDouble()));
		} else {
			int int2 = Double1.intValue();
			int int3;
			if (Double2 == null) {
				int3 = int2;
				int2 = 1;
			} else {
				int3 = Double2.intValue();
			}

			return luaCallFrame.push(KahluaUtil.toDouble((long)(int2 + random.nextInt(int3 - int2 + 1))));
		}
	}

	private Random getRandom(LuaCallFrame luaCallFrame, String string) {
		Object object = KahluaUtil.getArg(luaCallFrame, 1, string);
		if (!(object instanceof Random)) {
			KahluaUtil.fail("First argument to " + string + " must be an object of type random.");
		}

		return (Random)object;
	}

	private int newRandom(LuaCallFrame luaCallFrame) {
		return luaCallFrame.push(new Random());
	}

	static  {
		names[0] = "random";
		names[1] = "seed";
		names[2] = "newrandom";
		functions = new RandomLib[3];
	for (int var0 = 0; var0 < 3; ++var0) {
		functions[var0] = new RandomLib(var0);
	}

		NEWRANDOM_FUN = new RandomLib(2);
	}
}
