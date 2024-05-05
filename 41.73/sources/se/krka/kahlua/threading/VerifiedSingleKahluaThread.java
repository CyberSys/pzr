package se.krka.kahlua.threading;

import java.io.PrintStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.Platform;


public class VerifiedSingleKahluaThread extends KahluaThread {
	private final Lock lock = new ReentrantLock();

	public VerifiedSingleKahluaThread(Platform platform, KahluaTable kahluaTable) {
		super(platform, kahluaTable);
	}

	public VerifiedSingleKahluaThread(PrintStream printStream, Platform platform, KahluaTable kahluaTable) {
		super(printStream, platform, kahluaTable);
	}

	private void lock() {
		if (!this.lock.tryLock()) {
			throw new IllegalStateException("Multiple threads may not access the same lua thread");
		}
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

	public void setmetatable(Object object, KahluaTable kahluaTable) {
		this.lock();
		try {
			super.setmetatable(object, kahluaTable);
		} finally {
			this.unlock();
		}
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
}
