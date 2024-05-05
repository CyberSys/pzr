package se.krka.kahlua.require;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;


public class Require implements JavaFunction {
	private final LuaSourceProvider luaSourceProvider;

	public void install(KahluaTable kahluaTable) {
		kahluaTable.rawset("require", this);
		kahluaTable.rawset(this, new HashMap());
	}

	public Require(LuaSourceProvider luaSourceProvider) {
		this.luaSourceProvider = luaSourceProvider;
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		KahluaTable kahluaTable = luaCallFrame.getEnvironment();
		Map map = (Map)luaCallFrame.getThread().tableget(kahluaTable, this);
		String string = KahluaUtil.getStringArg(luaCallFrame, 1, "require");
		Require.Result result = (Require.Result)map.get(string);
		if (result == null) {
			this.setState(map, string, Require.Result.LOADING);
			Reader reader = this.luaSourceProvider.getLuaSource(string);
			if (reader == null) {
				this.error(map, string, "Does not exist: " + string);
			}

			try {
				LuaClosure luaClosure = LuaCompiler.loadis(reader, string, kahluaTable);
				this.setState(map, string, Require.Result.LOADING);
				luaCallFrame.getThread().call(luaClosure, (Object)null, (Object)null, (Object)null);
				this.setState(map, string, Require.Result.LOADED);
				return 0;
			} catch (IOException ioException) {
				this.error(map, string, "Error in: " + string + ": " + ioException.getMessage());
			} catch (RuntimeException runtimeException) {
				String string2 = "Error in: " + string + ": " + runtimeException.getMessage();
				this.setState(map, string, Require.Result.error(string2));
				throw new RuntimeException(string2, runtimeException);
			}
		}

		if (result == Require.Result.LOADING) {
			this.error(map, string, "Circular dependency found for: " + string);
		}

		if (result.state == Require.State.BROKEN) {
			KahluaUtil.fail(result.errorMessage);
		}

		return 0;
	}

	private void error(Map map, String string, String string2) {
		this.setState(map, string, Require.Result.error(string2));
		KahluaUtil.fail(string2);
	}

	private void setState(Map map, String string, Require.Result result) {
		map.put(string, result);
	}

	private static class Result {
		public final String errorMessage;
		public final Require.State state;
		public static final Require.Result LOADING;
		public static final Require.Result LOADED;

		private Result(String string, Require.State state) {
			this.errorMessage = string;
			this.state = state;
		}

		public static Require.Result error(String string) {
			return new Require.Result(string, Require.State.BROKEN);
		}

		static  {
			LOADING = new Require.Result((String)null, Require.State.LOADING);
			LOADED = new Require.Result((String)null, Require.State.LOADED);
		}
	}

	private static enum State {

		LOADING,
		LOADED,
		BROKEN;

		private static Require.State[] $values() {
			return new Require.State[]{LOADING, LOADED, BROKEN};
		}
	}
}
