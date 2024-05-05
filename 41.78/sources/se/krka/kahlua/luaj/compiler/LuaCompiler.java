package se.krka.kahlua.luaj.compiler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.luaj.kahluafork.compiler.LexState;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;


public class LuaCompiler implements JavaFunction {
	public static boolean rewriteEvents = false;
	private final int index;
	private static final int LOADSTRING = 0;
	private static final int LOADSTREAM = 1;
	private static final String[] names = new String[]{"loadstring", "loadstream"};
	private static final LuaCompiler[] functions;

	private LuaCompiler(int int1) {
		this.index = int1;
	}

	public static void register(KahluaTable kahluaTable) {
		for (int int1 = 0; int1 < names.length; ++int1) {
			kahluaTable.rawset(names[int1], functions[int1]);
		}
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		switch (this.index) {
		case 0: 
			return this.loadstring(luaCallFrame, int1);
		
		case 1: 
			return loadstream(luaCallFrame, int1);
		
		default: 
			return 0;
		
		}
	}

	public static int loadstream(LuaCallFrame luaCallFrame, int int1) {
		try {
			KahluaUtil.luaAssert(int1 >= 2, "not enough arguments");
			Object object = luaCallFrame.get(0);
			KahluaUtil.luaAssert(object != null, "No input given");
			String string = (String)luaCallFrame.get(1);
			if (object instanceof Reader) {
				return luaCallFrame.push(loadis((Reader)((Reader)object), string, (String)null, luaCallFrame.getEnvironment()));
			} else if (object instanceof InputStream) {
				return luaCallFrame.push(loadis((InputStream)((InputStream)object), string, (String)null, luaCallFrame.getEnvironment()));
			} else {
				KahluaUtil.fail("Invalid type to loadstream: " + object.getClass());
				return 0;
			}
		} catch (RuntimeException runtimeException) {
			return luaCallFrame.push((Object)null, runtimeException.getMessage());
		} catch (IOException ioException) {
			return luaCallFrame.push((Object)null, ioException.getMessage());
		}
	}

	private int loadstring(LuaCallFrame luaCallFrame, int int1) {
		try {
			KahluaUtil.luaAssert(int1 >= 1, "not enough arguments");
			String string = (String)luaCallFrame.get(0);
			KahluaUtil.luaAssert(string != null, "No source given");
			String string2 = null;
			if (int1 >= 2) {
				string2 = (String)luaCallFrame.get(1);
			}

			return luaCallFrame.push(loadstring(string, string2, luaCallFrame.getEnvironment()));
		} catch (RuntimeException runtimeException) {
			return luaCallFrame.push((Object)null, runtimeException.getMessage());
		} catch (IOException ioException) {
			return luaCallFrame.push((Object)null, ioException.getMessage());
		}
	}

	public static LuaClosure loadis(InputStream inputStream, String string, KahluaTable kahluaTable) throws IOException {
		return loadis((InputStream)inputStream, string, (String)null, kahluaTable);
	}

	public static LuaClosure loadis(Reader reader, String string, KahluaTable kahluaTable) throws IOException {
		return loadis((Reader)reader, string, (String)null, kahluaTable);
	}

	public static LuaClosure loadstring(String string, String string2, KahluaTable kahluaTable) throws IOException {
		return loadis((InputStream)(new ByteArrayInputStream(string.getBytes("UTF-8"))), string2, string, kahluaTable);
	}

	private static LuaClosure loadis(Reader reader, String string, String string2, KahluaTable kahluaTable) throws IOException {
		return new LuaClosure(LexState.compile(reader.read(), reader, string, string2), kahluaTable);
	}

	private static LuaClosure loadis(InputStream inputStream, String string, String string2, KahluaTable kahluaTable) throws IOException {
		return loadis((Reader)(new InputStreamReader(inputStream)), string, string2, kahluaTable);
	}

	static  {
		functions = new LuaCompiler[names.length];
	for (int var0 = 0; var0 < names.length; ++var0) {
		functions[var0] = new LuaCompiler(var0);
	}
	}
}
