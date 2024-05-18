package se.krka.kahlua.threading;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;


public class BlockingKahluaThread extends KahluaThread {
	private final Lock lock = new ReentrantLock();

	public BlockingKahluaThread(Platform platform, KahluaTable kahluaTable) {
		super(platform, kahluaTable);
	}

	public BlockingKahluaThread(PrintStream printStream, Platform platform, KahluaTable kahluaTable) {
		super(printStream, platform, kahluaTable);
	}

	private void lock() {
		this.lock.lock();
	}

	private void unlock() {
		this.lock.unlock();
	}

	public int call(int int1) {
		this.lock();
		int int2;
		try {
			int2 = super.call(int1);
		} finally {
			this.unlock();
		}

		return int2;
	}

	public int pcall(int int1) {
		this.lock();
		int int2;
		try {
			int2 = super.pcall(int1);
		} finally {
			this.unlock();
		}

		return int2;
	}

	public Object[] pcall(Object object) {
		this.lock();
		Object[] objectArray;
		try {
			objectArray = super.pcall(object);
		} finally {
			this.unlock();
		}

		return objectArray;
	}

	public final Object[] pcall(Object object, Object[] objectArray) {
		this.lock();
		Object[] objectArray2;
		try {
			objectArray2 = super.pcall(object, objectArray);
		} finally {
			this.unlock();
		}

		return objectArray2;
	}

	public Object call(Object object, Object object2, Object object3, Object object4) {
		this.lock();
		Object object5;
		try {
			object5 = super.call(object, object2, object3, object4);
		} finally {
			this.unlock();
		}

		return object5;
	}

	public Object call(Object object, Object[] objectArray) {
		this.lock();
		Object object2;
		try {
			object2 = super.call(object, objectArray);
		} finally {
			this.unlock();
		}

		return object2;
	}

	public KahluaTable getEnvironment() {
		this.lock();
		KahluaTable kahluaTable;
		try {
			kahluaTable = super.getEnvironment();
		} finally {
			this.unlock();
		}

		return kahluaTable;
	}

	public Object getMetaOp(Object object, String string) {
		this.lock();
		Object object2;
		try {
			object2 = super.getMetaOp(object, string);
		} finally {
			this.unlock();
		}

		return object2;
	}

	public Object getmetatable(Object object, boolean boolean1) {
		this.lock();
		Object object2;
		try {
			object2 = super.getmetatable(object, boolean1);
		} finally {
			this.unlock();
		}

		return object2;
	}

	public void setmetatable(Object object, KahluaTable kahluaTable) {
		this.lock();
		try {
			super.setmetatable(object, kahluaTable);
		} finally {
			this.unlock();
		}
	}

	public Object tableget(Object object, Object object2) {
		this.lock();
		Object object3;
		try {
			object3 = super.tableget(object, object2);
		} finally {
			this.unlock();
		}

		return object3;
	}

	public void tableSet(Object object, Object object2, Object object3) {
		this.lock();
		try {
			super.tableSet(object, object2, object3);
		} finally {
			this.unlock();
		}
	}

	public static void main(String[] stringArray) throws IOException, InterruptedException {
		J2SEPlatform j2SEPlatform = new J2SEPlatform();
		KahluaTable kahluaTable = j2SEPlatform.newEnvironment();
		final BlockingKahluaThread blockingKahluaThread = new BlockingKahluaThread(j2SEPlatform, kahluaTable);
		final LuaClosure luaClosure = LuaCompiler.loadstring("x = (x or 0) + 1", "", blockingKahluaThread.getEnvironment());
		final AtomicInteger atomicInteger = new AtomicInteger(0);
		for (int int1 = 0; int1 < 100; ++int1) {
			(new Thread(new Runnable(){
				
				public void run() {
					for (int j2SEPlatform = 0; j2SEPlatform < 100; ++j2SEPlatform) {
						try {
							Thread.sleep((long)(Math.random() * 10.0));
						} catch (InterruptedException blockingKahluaThreadx) {
							blockingKahluaThreadx.printStackTrace();
						}

						blockingKahluaThread.pcall(luaClosure);
					}

					atomicInteger.incrementAndGet();
				}
			})).start();
		}

		while (atomicInteger.get() != 100) {
			Thread.sleep(100L);
		}

		LuaClosure luaClosure2 = LuaCompiler.loadstring("print(\'x=\'..x)", "", blockingKahluaThread.getEnvironment());
		blockingKahluaThread.pcall(luaClosure2);
	}
}
