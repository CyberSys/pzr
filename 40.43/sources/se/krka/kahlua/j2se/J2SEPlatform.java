package se.krka.kahlua.j2se;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.stdlib.BaseLib;
import se.krka.kahlua.stdlib.CoroutineLib;
import se.krka.kahlua.stdlib.OsLib;
import se.krka.kahlua.stdlib.RandomLib;
import se.krka.kahlua.stdlib.StringLib;
import se.krka.kahlua.stdlib.TableLib;
import se.krka.kahlua.test.UserdataArray;
import se.krka.kahlua.threading.BlockingKahluaThread;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;


public class J2SEPlatform implements Platform {
	private static J2SEPlatform INSTANCE = new J2SEPlatform();

	public static J2SEPlatform getInstance() {
		return INSTANCE;
	}

	public double pow(double double1, double double2) {
		return Math.pow(double1, double2);
	}

	public KahluaTable newTable() {
		return new KahluaTableImpl(new LinkedHashMap());
	}

	public KahluaTable newEnvironment() {
		KahluaTable kahluaTable = this.newTable();
		this.setupEnvironment(kahluaTable);
		return kahluaTable;
	}

	public void setupEnvironment(KahluaTable kahluaTable) {
		kahluaTable.wipe();
		kahluaTable.rawset("_G", kahluaTable);
		kahluaTable.rawset("_VERSION", "Kahlua kahlua.major.kahlua.minor.kahlua.fix for Lua lua.version (J2SE)");
		MathLib.register(this, kahluaTable);
		BaseLib.register(kahluaTable);
		RandomLib.register(this, kahluaTable);
		UserdataArray.register(this, kahluaTable);
		StringLib.register(this, kahluaTable);
		CoroutineLib.register(this, kahluaTable);
		OsLib.register(this, kahluaTable);
		TableLib.register(this, kahluaTable);
		LuaCompiler.register(kahluaTable);
		KahluaThread kahluaThread = this.setupWorkerThread(kahluaTable);
		KahluaUtil.setupLibrary(kahluaTable, kahluaThread, "/stdlib");
		File file = new File("serialize.lua");
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			Throwable throwable = null;
			try {
				LuaClosure luaClosure = LuaCompiler.loadis((InputStream)fileInputStream, "serialize.lua", kahluaTable);
				kahluaThread.call(luaClosure, (Object)null, (Object)null, (Object)null);
			} catch (Throwable throwable2) {
				throwable = throwable2;
				throw throwable2;
			} finally {
				if (fileInputStream != null) {
					if (throwable != null) {
						try {
							fileInputStream.close();
						} catch (Throwable throwable3) {
							throwable.addSuppressed(throwable3);
						}
					} else {
						fileInputStream.close();
					}
				}
			}
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private KahluaThread setupWorkerThread(KahluaTable kahluaTable) {
		BlockingKahluaThread blockingKahluaThread = new BlockingKahluaThread(this, kahluaTable);
		KahluaUtil.setWorkerThread(kahluaTable, blockingKahluaThread);
		return blockingKahluaThread;
	}
}
