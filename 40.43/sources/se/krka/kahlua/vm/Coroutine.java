package se.krka.kahlua.vm;

import java.util.Vector;
import zombie.Lua.LuaManager;
import zombie.core.Core;


public class Coroutine {
	private final Platform platform;
	private KahluaThread thread;
	private Coroutine parent;
	public KahluaTable environment;
	public String stackTrace;
	private final Vector liveUpvalues;
	private static final int MAX_STACK_SIZE = 3000;
	private static final int INITIAL_STACK_SIZE = 1000;
	private static final int MAX_CALL_FRAME_STACK_SIZE = 1000;
	private static final int INITIAL_CALL_FRAME_STACK_SIZE = 200;
	public Object[] objectStack;
	private int top;
	private LuaCallFrame[] callFrameStack;
	private int callFrameTop;

	public Coroutine() {
		this.stackTrace = "";
		this.liveUpvalues = new Vector();
		this.platform = null;
	}

	public Coroutine getParent() {
		return this.parent;
	}

	public Coroutine(Platform platform, KahluaTable kahluaTable, KahluaThread kahluaThread) {
		this.stackTrace = "";
		this.liveUpvalues = new Vector();
		this.platform = platform;
		this.environment = kahluaTable;
		this.thread = kahluaThread;
		this.objectStack = new Object[1000];
		this.callFrameStack = new LuaCallFrame[200];
	}

	public Coroutine(Platform platform, KahluaTable kahluaTable) {
		this(platform, kahluaTable, (KahluaThread)null);
	}

	public final LuaCallFrame pushNewCallFrame(LuaClosure luaClosure, JavaFunction javaFunction, int int1, int int2, int int3, boolean boolean1, boolean boolean2) {
		this.setCallFrameStackTop(this.callFrameTop + 1);
		LuaCallFrame luaCallFrame = this.currentCallFrame();
		luaCallFrame.setup(luaClosure, javaFunction, int1, int2, int3, boolean1, boolean2);
		return luaCallFrame;
	}

	public void popCallFrame() {
		if (this.isDead()) {
			throw new RuntimeException("Stack underflow");
		} else {
			this.setCallFrameStackTop(this.callFrameTop - 1);
		}
	}

	private final void ensureCallFrameStackSize(int int1) {
		if (int1 > 1000) {
			throw new RuntimeException("Stack overflow");
		} else {
			int int2 = this.callFrameStack.length;
			int int3;
			for (int3 = int2; int3 <= int1; int3 = 2 * int3) {
			}

			if (int3 > int2) {
				LuaCallFrame[] luaCallFrameArray = new LuaCallFrame[int3];
				System.arraycopy(this.callFrameStack, 0, luaCallFrameArray, 0, int2);
				this.callFrameStack = luaCallFrameArray;
			}
		}
	}

	public final void setCallFrameStackTop(int int1) {
		if (int1 > this.callFrameTop) {
			this.ensureCallFrameStackSize(int1);
		} else {
			this.callFrameStackClear(int1, this.callFrameTop - 1);
		}

		this.callFrameTop = int1;
	}

	private void callFrameStackClear(int int1, int int2) {
		for (; int1 <= int2; ++int1) {
			LuaCallFrame luaCallFrame = this.callFrameStack[int1];
			if (luaCallFrame != null) {
				this.callFrameStack[int1].closure = null;
				this.callFrameStack[int1].javaFunction = null;
			}
		}
	}

	private final void ensureStacksize(int int1) {
		if (int1 > 3000) {
			throw new RuntimeException("Stack overflow");
		} else {
			int int2 = this.objectStack.length;
			int int3;
			for (int3 = int2; int3 <= int1; int3 = 2 * int3) {
			}

			if (int3 > int2) {
				Object[] objectArray = new Object[int3];
				System.arraycopy(this.objectStack, 0, objectArray, 0, int2);
				this.objectStack = objectArray;
			}
		}
	}

	public final void setTop(int int1) {
		if (this.top < int1) {
			this.ensureStacksize(int1);
		} else {
			this.stackClear(int1, this.top - 1);
		}

		this.top = int1;
	}

	public final void stackCopy(int int1, int int2, int int3) {
		if (int3 > 0 && int1 != int2) {
			System.arraycopy(this.objectStack, int1, this.objectStack, int2, int3);
			LuaCallFrame luaCallFrame = this.getParentNoAssert(1);
			if (Core.bDebug && luaCallFrame != null && luaCallFrame.closure != null) {
				for (int int4 = int2; int4 < int2 + int3; ++int4) {
					int int5 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
					boolean boolean1 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != int5;
					if (this.thread == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
						while (int5 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
							++luaCallFrame.localsAssigned;
						}
					}

					if (boolean1 && this.thread == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == int5) {
						int int6 = luaCallFrame.localsAssigned++;
						String string = luaCallFrame.closure.prototype.locvars[int6];
						luaCallFrame.setLocalVarToStack(string, int4);
					}
				}
			}
		}
	}

	public final void stackClear(int int1, int int2) {
		while (int1 <= int2) {
			this.objectStack[int1] = null;
			++int1;
		}
	}

	public final void closeUpvalues(int int1) {
		int int2 = this.liveUpvalues.size();
		while (true) {
			--int2;
			if (int2 < 0) {
				return;
			}

			UpValue upValue = (UpValue)this.liveUpvalues.elementAt(int2);
			if (upValue.getIndex() < int1) {
				return;
			}

			upValue.close();
			this.liveUpvalues.removeElementAt(int2);
		}
	}

	public final UpValue findUpvalue(int int1) {
		int int2 = this.liveUpvalues.size();
		UpValue upValue;
		int int3;
		do {
			--int2;
			if (int2 < 0) {
				break;
			}

			upValue = (UpValue)this.liveUpvalues.elementAt(int2);
			int3 = upValue.getIndex();
			if (int3 == int1) {
				return upValue;
			}
		} while (int3 >= int1);

		upValue = new UpValue(this, int1);
		this.liveUpvalues.insertElementAt(upValue, int2 + 1);
		return upValue;
	}

	public Object getObjectFromStack(int int1) {
		return this.objectStack[int1];
	}

	public int getObjectStackSize() {
		return this.top;
	}

	public LuaCallFrame getParentCallframe() {
		int int1 = this.callFrameTop - 1;
		return int1 < 0 ? null : this.callFrameStack[int1];
	}

	public final LuaCallFrame currentCallFrame() {
		if (this.isDead()) {
			return null;
		} else {
			LuaCallFrame luaCallFrame = this.callFrameStack[this.callFrameTop - 1];
			if (luaCallFrame == null) {
				luaCallFrame = new LuaCallFrame(this);
				this.callFrameStack[this.callFrameTop - 1] = luaCallFrame;
			}

			return luaCallFrame;
		}
	}

	public int getTop() {
		return this.top;
	}

	public LuaCallFrame getParent(int int1) {
		KahluaUtil.luaAssert(int1 >= 0, "Level must be non-negative");
		int int2 = this.callFrameTop - int1 - 1;
		KahluaUtil.luaAssert(int2 >= 0, "Level too high");
		return this.callFrameStack[int2];
	}

	public LuaCallFrame getParentNoAssert(int int1) {
		int int2 = this.callFrameTop - int1 - 1;
		return int2 < 0 ? null : this.callFrameStack[int2];
	}

	public String getCurrentStackTrace(int int1, int int2, int int3) {
		if (int1 < 0) {
			int1 = 0;
		}

		if (int2 < 0) {
			int2 = 0;
		}

		StringBuffer stringBuffer = new StringBuffer();
		for (int int4 = this.callFrameTop - 1 - int1; int4 >= int3 && int2-- > 0; --int4) {
			stringBuffer.append(this.getStackTrace(this.callFrameStack[int4]));
		}

		return stringBuffer.toString();
	}

	public void cleanCallFrames(LuaCallFrame luaCallFrame) {
		while (true) {
			LuaCallFrame luaCallFrame2 = this.currentCallFrame();
			if (luaCallFrame2 == null || luaCallFrame2 == luaCallFrame) {
				return;
			}

			this.addStackTrace(luaCallFrame2);
			this.popCallFrame();
		}
	}

	public void addStackTrace(LuaCallFrame luaCallFrame) {
		this.stackTrace = this.stackTrace + this.getStackTrace(luaCallFrame);
	}

	private String getStackTrace(LuaCallFrame luaCallFrame) {
		if (luaCallFrame.isLua()) {
			int[] intArray = luaCallFrame.closure.prototype.lines;
			if (intArray != null) {
				int int1 = luaCallFrame.pc - 1;
				if (int1 >= 0 && int1 < intArray.length) {
					return "at " + luaCallFrame.closure.prototype + ":" + intArray[int1] + "\n";
				}
			}

			return "";
		} else {
			return "at " + luaCallFrame.javaFunction + "\n";
		}
	}

	public boolean isDead() {
		return this.callFrameTop == 0;
	}

	public Platform getPlatform() {
		return this.platform;
	}

	public String getStatus() {
		if (this.parent == null) {
			return this.isDead() ? "dead" : "suspended";
		} else {
			return "normal";
		}
	}

	public boolean atBottom() {
		return this.callFrameTop == 1;
	}

	public int getCallframeTop() {
		return this.callFrameTop;
	}

	public LuaCallFrame[] getCallframeStack() {
		return this.callFrameStack;
	}

	public LuaCallFrame getCallFrame(int int1) {
		if (int1 < 0) {
			int1 += this.callFrameTop;
		}

		return this.callFrameStack[int1];
	}

	public static void yieldHelper(LuaCallFrame luaCallFrame, LuaCallFrame luaCallFrame2, int int1) {
		KahluaUtil.luaAssert(luaCallFrame.canYield, "Can not yield outside of a coroutine");
		Coroutine coroutine = luaCallFrame.coroutine;
		KahluaThread kahluaThread = coroutine.getThread();
		Coroutine coroutine2 = coroutine.parent;
		KahluaUtil.luaAssert(coroutine2 != null, "Internal error, coroutine must be running");
		KahluaUtil.luaAssert(coroutine == kahluaThread.currentCoroutine, "Internal error, must yield current thread");
		coroutine.destroy();
		LuaCallFrame luaCallFrame3 = coroutine2.currentCallFrame();
		int int2;
		if (luaCallFrame3 == null) {
			coroutine2.setTop(int1 + 1);
			coroutine2.objectStack[0] = Boolean.TRUE;
			for (int2 = 0; int2 < int1; ++int2) {
				coroutine2.objectStack[int2 + 1] = luaCallFrame2.get(int2);
			}
		} else {
			luaCallFrame3.push(Boolean.TRUE);
			for (int2 = 0; int2 < int1; ++int2) {
				Object object = luaCallFrame2.get(int2);
				luaCallFrame3.push(object);
			}
		}

		kahluaThread.currentCoroutine = coroutine2;
	}

	public void resume(Coroutine coroutine) {
		this.parent = coroutine;
		this.thread = coroutine.thread;
	}

	public KahluaThread getThread() {
		return this.thread;
	}

	public void destroy() {
		this.parent = null;
		this.thread = null;
	}
}
