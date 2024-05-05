package se.krka.kahlua.vm;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.stdlib.BaseLib;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.gameStates.IngameState;
import zombie.ui.UIManager;


public class KahluaThread {
	private static final int FIELDS_PER_FLUSH = 50;
	private static final int OP_MOVE = 0;
	private static final int OP_LOADK = 1;
	private static final int OP_LOADBOOL = 2;
	private static final int OP_LOADNIL = 3;
	private static final int OP_GETUPVAL = 4;
	private static final int OP_GETGLOBAL = 5;
	private static final int OP_GETTABLE = 6;
	private static final int OP_SETGLOBAL = 7;
	private static final int OP_SETUPVAL = 8;
	private static final int OP_SETTABLE = 9;
	private static final int OP_NEWTABLE = 10;
	private static final int OP_SELF = 11;
	private static final int OP_ADD = 12;
	private static final int OP_SUB = 13;
	private static final int OP_MUL = 14;
	private static final int OP_DIV = 15;
	private static final int OP_MOD = 16;
	private static final int OP_POW = 17;
	private static final int OP_UNM = 18;
	private static final int OP_NOT = 19;
	private static final int OP_LEN = 20;
	private static final int OP_CONCAT = 21;
	private static final int OP_JMP = 22;
	private static final int OP_EQ = 23;
	private static final int OP_LT = 24;
	private static final int OP_LE = 25;
	private static final int OP_TEST = 26;
	private static final int OP_TESTSET = 27;
	private static final int OP_CALL = 28;
	private static final int OP_TAILCALL = 29;
	private static final int OP_RETURN = 30;
	private static final int OP_FORLOOP = 31;
	private static final int OP_FORPREP = 32;
	private static final int OP_TFORLOOP = 33;
	private static final int OP_SETLIST = 34;
	private static final int OP_CLOSE = 35;
	private static final int OP_CLOSURE = 36;
	private static final int OP_VARARG = 37;
	private static final int MAX_INDEX_RECURSION = 100;
	private static final String[] meta_ops = new String[38];
	public static LuaCallFrame LastCallFrame;
	private final Coroutine rootCoroutine;
	public Coroutine currentCoroutine;
	private boolean doProfiling;
	private final PrintStream out;
	private final Platform platform;
	public boolean bStep;
	public String currentfile;
	public int currentLine;
	public int lastLine;
	public int lastCallFrame;
	public boolean bReset;
	public ArrayList profileEntries;
	public HashMap profileEntryMap;
	public static int m_error_count;
	public static final ArrayList m_errors_list;
	private final StringBuilder m_stringBuilder;
	private final StringWriter m_stringWriter;
	private final PrintWriter m_printWriter;
	HashMap BreakpointMap;
	HashMap BreakpointDataMap;
	HashMap BreakpointReadDataMap;
	public boolean bStepInto;

	public Coroutine getCurrentCoroutine() {
		return this.currentCoroutine;
	}

	public KahluaThread(Platform platform, KahluaTable kahluaTable) {
		this(System.out, platform, kahluaTable);
	}

	public KahluaThread(PrintStream printStream, Platform platform, KahluaTable kahluaTable) {
		this.doProfiling = false;
		this.bStep = false;
		this.bReset = false;
		this.profileEntries = new ArrayList();
		this.profileEntryMap = new HashMap();
		this.m_stringBuilder = new StringBuilder();
		this.m_stringWriter = new StringWriter();
		this.m_printWriter = new PrintWriter(this.m_stringWriter);
		this.BreakpointMap = new HashMap();
		this.BreakpointDataMap = new HashMap();
		this.BreakpointReadDataMap = new HashMap();
		this.bStepInto = false;
		this.platform = platform;
		this.out = printStream;
		this.rootCoroutine = new Coroutine(platform, kahluaTable, this);
		this.currentCoroutine = this.rootCoroutine;
	}

	public int call(int int1) {
		int int2 = this.currentCoroutine.getTop();
		int int3 = int2 - int1 - 1;
		Object object = this.currentCoroutine.objectStack[int3];
		if (object == null) {
			throw new RuntimeException("tried to call nil");
		} else {
			try {
				if (object instanceof JavaFunction) {
					return this.callJava((JavaFunction)object, int3 + 1, int3, int1);
				}
			} catch (Exception exception) {
				String string = exception.getClass().getName();
				throw new RuntimeException(string + " " + exception.getMessage() + " in " + (JavaFunction)object);
			}

			if (!(object instanceof LuaClosure)) {
				throw new RuntimeException("tried to call a non-function");
			} else {
				LuaCallFrame luaCallFrame = this.currentCoroutine.pushNewCallFrame((LuaClosure)object, (JavaFunction)null, int3 + 1, int3, int1, false, false);
				luaCallFrame.init();
				this.luaMainloop();
				int int4 = this.currentCoroutine.getTop() - int3;
				this.currentCoroutine.stackTrace = "";
				return int4;
			}
		}
	}

	private int callJava(JavaFunction javaFunction, int int1, int int2, int int3) {
		Coroutine coroutine = this.currentCoroutine;
		LuaCallFrame luaCallFrame = coroutine.pushNewCallFrame((LuaClosure)null, javaFunction, int1, int2, int3, false, false);
		int int4 = javaFunction.call(luaCallFrame, int3);
		int int5 = luaCallFrame.getTop();
		int int6 = int5 - int4;
		int int7 = int2 - int1;
		luaCallFrame.stackCopy(int6, int7, int4);
		luaCallFrame.setTop(int4 + int7);
		coroutine.popCallFrame();
		return int4;
	}

	private final Object prepareMetatableCall(Object object) {
		if (!(object instanceof JavaFunction) && !(object instanceof LuaClosure)) {
			Object object2 = this.getMetaOp(object, "__call");
			return object2;
		} else {
			return object;
		}
	}

	public boolean isCurrent(String string, int int1) {
		return int1 == this.currentLine;
	}

	private final void luaMainloop() {
		LuaCallFrame luaCallFrame = this.currentCoroutine.currentCallFrame();
		LuaClosure luaClosure = luaCallFrame.closure;
		Prototype prototype = luaClosure.prototype;
		int[] intArray = prototype.code;
		int int1 = luaCallFrame.returnBase;
		String string = "";
		long long1 = System.nanoTime();
		if (this.doProfiling && Core.bDebug && this == LuaManager.thread) {
			Coroutine coroutine = this.getCurrentCoroutine();
			String string2 = coroutine.objectStack[0].toString();
			String string3 = coroutine.getThread().currentfile;
			String string4 = string3 + " " + string2.substring(0, string2.indexOf(":"));
			string = string4;
		}

		boolean boolean1 = true;
		label938: do {
			if (this.bReset) {
				long long2 = System.nanoTime();
				this.DoProfileTiming(string, long1, long2);
				return;
			}

			if (Core.bDebug && this == LuaManager.thread) {
				Coroutine coroutine2 = this.getCurrentCoroutine();
				if (coroutine2 != null) {
					this.lastLine = this.currentLine;
					LuaCallFrame luaCallFrame2 = coroutine2.currentCallFrame();
					if (luaCallFrame2.closure != null) {
						this.currentfile = luaCallFrame2.closure.prototype.filename;
						this.currentLine = luaCallFrame2.closure.prototype.lines[luaCallFrame2.pc];
						if (this.bStep && this.currentLine != this.lastLine) {
							if (this.bStepInto) {
								this.bStep = false;
								UIManager.debugBreakpoint(luaCallFrame2.closure.prototype.filename, (long)this.currentLine - 1L);
								this.lastCallFrame = coroutine2.getCallframeTop();
								boolean1 = true;
							} else if (coroutine2.getCallframeTop() <= this.lastCallFrame) {
								this.bStep = false;
								this.lastCallFrame = coroutine2.getCallframeTop();
								UIManager.debugBreakpoint(luaCallFrame2.closure.prototype.filename, (long)this.currentLine - 1L);
								boolean1 = true;
							}
						}

						if (this.BreakpointMap.containsKey(luaCallFrame2.closure.prototype.filename)) {
							ArrayList arrayList = (ArrayList)this.BreakpointMap.get(luaCallFrame2.closure.prototype.filename);
							if (arrayList.contains((long)luaCallFrame2.closure.prototype.lines[luaCallFrame2.pc]) && (luaCallFrame2.pc == 0 || luaCallFrame2.closure.prototype.lines[luaCallFrame2.pc - 1] != luaCallFrame2.closure.prototype.lines[luaCallFrame2.pc])) {
								UIManager.debugBreakpoint(luaCallFrame2.closure.prototype.filename, (long)luaCallFrame2.closure.prototype.lines[luaCallFrame2.pc]);
							}
						}
					}
				}
			}

			boolean1 = true;
			try {
				if (this.bStep) {
					boolean boolean2 = false;
				}

				int int2 = intArray[luaCallFrame.pc++];
				int int3 = int2 & 63;
				int int4;
				int int5;
				Object object;
				int int6;
				int int7;
				int int8;
				int int9;
				int int10;
				Object object2;
				double double1;
				int int11;
				double double2;
				Object object3;
				Double Double1;
				Object object4;
				Object object5;
				double double3;
				int int12;
				boolean boolean3;
				boolean boolean4;
				String string5;
				Object object6;
				long long3;
				int int13;
				String string6;
				String string7;
				Object object7;
				UpValue upValue;
				boolean boolean5;
				switch (int3) {
				case 0: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					luaCallFrame.set(int7, luaCallFrame.get(int9));
					boolean1 = false;
					break;
				
				case 1: 
					int7 = getA8(int2);
					int9 = getBx(int2);
					if (Core.bDebug) {
						int10 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
						boolean5 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != int10;
						if (this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
							while (int10 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
								++luaCallFrame.localsAssigned;
							}
						}

						if (boolean5 && this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == int10) {
							int4 = luaCallFrame.localsAssigned++;
							string5 = luaCallFrame.closure.prototype.locvars[int4];
							luaCallFrame.setLocalVarToStack(string5, luaCallFrame.localBase + int7);
						}
					}

					luaCallFrame.set(int7, prototype.constants[int9]);
					break;
				
				case 2: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					int8 = getC9(int2);
					Boolean Boolean1 = int9 == 0 ? Boolean.FALSE : Boolean.TRUE;
					if (Core.bDebug) {
						int11 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
						boolean4 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != int11;
						if (this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
							while (int11 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
								++luaCallFrame.localsAssigned;
							}
						}

						if (boolean4 && this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == int11) {
							int5 = luaCallFrame.localsAssigned++;
							string6 = luaCallFrame.closure.prototype.locvars[int5];
							if (string6.equals("group")) {
								boolean3 = false;
							}

							luaCallFrame.setLocalVarToStack(string6, luaCallFrame.localBase + int7);
						}
					}

					luaCallFrame.set(int7, Boolean1);
					if (int8 != 0) {
						++luaCallFrame.pc;
					}

					break;
				
				case 3: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					if (Core.bDebug) {
						int10 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
						boolean5 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != int10;
						if (this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
							while (int10 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
								++luaCallFrame.localsAssigned;
							}
						}

						if (boolean5 && this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == int10) {
							int4 = luaCallFrame.localsAssigned++;
							string5 = luaCallFrame.closure.prototype.locvars[int4];
							luaCallFrame.setLocalVarToStack(string5, luaCallFrame.localBase + int7);
						}
					}

					luaCallFrame.stackClear(int7, int9);
					break;
				
				case 4: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					upValue = luaClosure.upvalues[int9];
					if (Core.bDebug) {
						int11 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
						boolean4 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != int11;
						if (this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
							while (int11 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
								++luaCallFrame.localsAssigned;
							}
						}

						if (boolean4 && this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == int11) {
							int5 = luaCallFrame.localsAssigned++;
							string6 = luaCallFrame.closure.prototype.locvars[int5];
							if (string6.equals("group")) {
								boolean3 = false;
							}

							luaCallFrame.setLocalVarToStack(string6, luaCallFrame.localBase + int7);
						}
					}

					luaCallFrame.set(int7, upValue.getValue());
					break;
				
				case 5: 
					int7 = getA8(int2);
					int9 = getBx(int2);
					object2 = this.tableget(luaClosure.env, prototype.constants[int9]);
					if (Core.bDebug) {
						int11 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
						boolean4 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != int11;
						if (this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
							while (int11 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
								++luaCallFrame.localsAssigned;
							}
						}

						if (boolean4 && this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == int11) {
							int5 = luaCallFrame.localsAssigned++;
							string6 = luaCallFrame.closure.prototype.locvars[int5];
							if (string6.equals("group")) {
								boolean3 = false;
							}

							luaCallFrame.setLocalVarToStack(string6, luaCallFrame.localBase + int7);
						}
					}

					luaCallFrame.set(int7, object2);
					break;
				
				case 6: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					int8 = getC9(int2);
					object2 = luaCallFrame.get(int9);
					object5 = this.getRegisterOrConstant(luaCallFrame, int8, prototype);
					object3 = this.tableget(object2, object5);
					if (Core.bDebug) {
						int5 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
						boolean boolean6 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != int5;
						if (this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
							while (int5 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
								++luaCallFrame.localsAssigned;
							}
						}

						if (boolean6 && this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == int5) {
							int13 = luaCallFrame.localsAssigned++;
							String string8 = luaCallFrame.closure.prototype.locvars[int13];
							luaCallFrame.setLocalVarToStack(string8, luaCallFrame.localBase + int7);
						}
					}

					luaCallFrame.set(int7, object3);
					break;
				
				case 7: 
					int7 = getA8(int2);
					int9 = getBx(int2);
					object2 = luaCallFrame.get(int7);
					object5 = prototype.constants[int9];
					if (object2 instanceof LuaClosure && object5 instanceof String) {
						((LuaClosure)object2).debugName = object5.toString();
					}

					if (LuaCompiler.rewriteEvents) {
						object3 = luaClosure.env.rawget(object5);
						if (object3 instanceof KahluaTable && object3 != object2) {
							KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)object3;
							kahluaTableImpl.setRewriteTable(object2);
						}

						this.tableSet(luaClosure.env, object5, object2);
					} else {
						this.tableSet(luaClosure.env, object5, object2);
					}

					break;
				
				case 8: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					upValue = luaClosure.upvalues[int9];
					if (Core.bDebug) {
						int11 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
						boolean4 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != int11;
						if (this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
							while (int11 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
								++luaCallFrame.localsAssigned;
							}
						}

						if (boolean4 && this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == int11) {
							int5 = luaCallFrame.localsAssigned++;
							string6 = luaCallFrame.closure.prototype.locvars[int5];
							luaCallFrame.setLocalVarToStack(string6, luaCallFrame.localBase + int7);
						}
					}

					upValue.setValue(luaCallFrame.get(int7));
					break;
				
				case 9: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					int8 = getC9(int2);
					object2 = luaCallFrame.get(int7);
					object5 = this.getRegisterOrConstant(luaCallFrame, int9, prototype);
					object3 = this.getRegisterOrConstant(luaCallFrame, int8, prototype);
					this.tableSet(object2, object5, object3);
					break;
				
				case 10: 
					int7 = getA8(int2);
					KahluaTable kahluaTable = this.platform.newTable();
					if (Core.bDebug) {
						int11 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
						boolean4 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != int11;
						if (this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
							while (int11 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
								++luaCallFrame.localsAssigned;
							}
						}

						if (boolean4 && this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == int11) {
							int5 = luaCallFrame.localsAssigned++;
							string6 = luaCallFrame.closure.prototype.locvars[int5];
							luaCallFrame.setLocalVarToStack(string6, luaCallFrame.localBase + int7);
						}
					}

					luaCallFrame.set(int7, kahluaTable);
					break;
				
				case 11: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					int8 = getC9(int2);
					object2 = this.getRegisterOrConstant(luaCallFrame, int8, prototype);
					object5 = luaCallFrame.get(int9);
					LastCallFrame = luaCallFrame;
					object3 = this.tableget(object5, object2);
					luaCallFrame.set(int7, object3);
					luaCallFrame.set(int7 + 1, object5);
					boolean1 = false;
					break;
				
				case 12: 
				
				case 13: 
				
				case 14: 
				
				case 15: 
				
				case 16: 
				
				case 17: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					int8 = getC9(int2);
					object2 = this.getRegisterOrConstant(luaCallFrame, int9, prototype);
					object5 = this.getRegisterOrConstant(luaCallFrame, int8, prototype);
					object3 = null;
					Double1 = null;
					object = null;
					Double Double2;
					if ((Double2 = KahluaUtil.rawTonumber(object2)) != null && (Double1 = KahluaUtil.rawTonumber(object5)) != null) {
						object = this.primitiveMath(Double2, Double1, int3);
					} else {
						String string9 = meta_ops[int3];
						object7 = this.getBinMetaOp(object2, object5, string9);
						if (object7 == null) {
							this.doStacktraceProper(luaCallFrame);
							String string10 = "unknown";
							if (luaClosure.debugName != null) {
								string10 = luaClosure.debugName;
							} else if (prototype.name != null) {
								string10 = prototype.name;
							}

							KahluaUtil.fail(string9 + " not defined for operands in " + string10);
						}

						object = this.call(object7, object2, object5, (Object)null);
					}

					if (Core.bDebug) {
						int13 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
						boolean boolean7 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != int13;
						if (this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
							while (int13 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
								++luaCallFrame.localsAssigned;
							}
						}

						if (boolean7 && this == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == int13) {
							int6 = luaCallFrame.localsAssigned++;
							String string11 = luaCallFrame.closure.prototype.locvars[int6];
							luaCallFrame.setLocalVarToStack(string11, luaCallFrame.localBase + int7);
						}
					}

					luaCallFrame.set(int7, object);
					break;
				
				case 18: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					object2 = luaCallFrame.get(int9);
					Double Double3 = KahluaUtil.rawTonumber(object2);
					if (Double3 != null) {
						object3 = KahluaUtil.toDouble(-KahluaUtil.fromDouble(Double3));
					} else {
						object4 = this.getMetaOp(object2, "__unm");
						object3 = this.call(object4, object2, (Object)null, (Object)null);
					}

					luaCallFrame.set(int7, object3);
					break;
				
				case 19: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					object2 = luaCallFrame.get(int9);
					luaCallFrame.set(int7, KahluaUtil.toBoolean(!KahluaUtil.boolEval(object2)));
					boolean1 = false;
					break;
				
				case 20: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					object2 = luaCallFrame.get(int9);
					if (object2 instanceof KahluaTable) {
						KahluaTable kahluaTable2 = (KahluaTable)object2;
						object5 = KahluaUtil.toDouble((long)kahluaTable2.len());
					} else if (object2 instanceof String) {
						string7 = (String)object2;
						object5 = KahluaUtil.toDouble((long)string7.length());
					} else {
						object3 = this.getMetaOp(object2, "__len");
						if (object3 == null) {
							this.doStacktraceProper(luaCallFrame);
						}

						KahluaUtil.luaAssert(object3 != null, "__len not defined for operand");
						object5 = this.call(object3, object2, (Object)null, (Object)null);
					}

					luaCallFrame.set(int7, object5);
					boolean1 = false;
					break;
				
				case 21: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					int8 = getC9(int2);
					int10 = int9;
					object3 = luaCallFrame.get(int8);
					int11 = int8 - 1;
					while (int10 <= int11) {
						string5 = KahluaUtil.rawTostring(object3);
						if (string5 != null) {
							int12 = 0;
							for (int13 = int11; int10 <= int13; ++int12) {
								object7 = luaCallFrame.get(int13);
								--int13;
								if (KahluaUtil.rawTostring(object7) == null) {
									break;
								}
							}

							if (int12 > 0) {
								StringBuilder stringBuilder = new StringBuilder();
								for (int6 = int11 - int12 + 1; int6 <= int11; ++int6) {
									stringBuilder.append(KahluaUtil.rawTostring(luaCallFrame.get(int6)));
								}

								stringBuilder.append(string5);
								object3 = stringBuilder.toString();
								int11 -= int12;
							}
						}

						if (int10 <= int11) {
							object4 = luaCallFrame.get(int11);
							object = this.getBinMetaOp(object4, object3, "__concat");
							if (object == null) {
								KahluaUtil.fail("__concat not defined for operands: " + object4 + " and " + object3);
							}

							object3 = this.call(object, object4, object3, (Object)null);
							--int11;
						}
					}

					luaCallFrame.set(int7, object3);
					boolean1 = false;
					break;
				
				case 22: 
					luaCallFrame.pc += getSBx(int2);
					break;
				
				case 23: 
				
				case 24: 
				
				case 25: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					int8 = getC9(int2);
					object2 = this.getRegisterOrConstant(luaCallFrame, int9, prototype);
					object5 = this.getRegisterOrConstant(luaCallFrame, int8, prototype);
					if (object2 instanceof Double && object5 instanceof Double) {
						double2 = KahluaUtil.fromDouble(object2);
						double3 = KahluaUtil.fromDouble(object5);
						if (int3 == 23) {
							if (double2 == double3 == (int7 == 0)) {
								++luaCallFrame.pc;
							}
						} else if (int3 == 24) {
							if (double2 < double3 == (int7 == 0)) {
								++luaCallFrame.pc;
							}
						} else if (double2 <= double3 == (int7 == 0)) {
							++luaCallFrame.pc;
						}
					} else if (object2 instanceof String && object5 instanceof String) {
						if (int3 == 23) {
							if (object2.equals(object5) == (int7 == 0)) {
								++luaCallFrame.pc;
							}
						} else {
							string7 = (String)object2;
							string5 = (String)object5;
							int12 = string7.compareTo(string5);
							if (int3 == 24) {
								if (int12 < 0 == (int7 == 0)) {
									++luaCallFrame.pc;
								}
							} else if (int12 <= 0 == (int7 == 0)) {
								++luaCallFrame.pc;
							}
						}
					} else {
						if (object2 == object5 && int3 == 23) {
							boolean4 = true;
						} else {
							boolean boolean8 = false;
							string6 = meta_ops[int3];
							object6 = this.getCompMetaOp(object2, object5, string6);
							if (object6 == null && int3 == 25) {
								object6 = this.getCompMetaOp(object2, object5, "__lt");
								object7 = object2;
								object2 = object5;
								object5 = object7;
								boolean8 = true;
							}

							if (object6 == null && int3 == 23) {
								boolean4 = BaseLib.luaEquals(object2, object5);
							} else {
								if (object6 == null) {
									this.doStacktraceProper(luaCallFrame);
									KahluaUtil.fail(string6 + " not defined for operand");
								}

								object7 = this.call(object6, object2, object5, (Object)null);
								boolean4 = KahluaUtil.boolEval(object7);
							}

							if (boolean8) {
								boolean4 = !boolean4;
							}
						}

						if (boolean4 == (int7 == 0)) {
							++luaCallFrame.pc;
						}
					}

					boolean1 = false;
					break;
				
				case 26: 
					int7 = getA8(int2);
					int8 = getC9(int2);
					object2 = luaCallFrame.get(int7);
					if (KahluaUtil.boolEval(object2) == (int8 == 0)) {
						++luaCallFrame.pc;
					}

					break;
				
				case 27: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					int8 = getC9(int2);
					object2 = luaCallFrame.get(int9);
					if (KahluaUtil.boolEval(object2) != (int8 == 0)) {
						luaCallFrame.set(int7, object2);
					} else {
						++luaCallFrame.pc;
					}

					break;
				
				case 28: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					int8 = getC9(int2);
					int10 = int9 - 1;
					if (int10 != -1) {
						luaCallFrame.setTop(int7 + int10 + 1);
					} else {
						int10 = luaCallFrame.getTop() - int7 - 1;
					}

					luaCallFrame.restoreTop = int8 != 0;
					int11 = luaCallFrame.localBase;
					int4 = int11 + int7 + 1;
					int5 = int11 + int7;
					object = luaCallFrame.get(int7);
					if (object == null) {
						boolean3 = false;
						object = luaCallFrame.get(int7);
					}

					if (object == null) {
						this.doStacktraceProper(luaCallFrame);
						if (luaCallFrame.getClosure().debugName != null) {
							KahluaUtil.fail("Object tried to call nil in " + luaCallFrame.getClosure().debugName);
						} else if (luaCallFrame.getClosure().prototype != null && luaCallFrame.getClosure().prototype.name != null) {
							KahluaUtil.fail("Object tried to call nil in " + luaCallFrame.getClosure().prototype.name);
						} else {
							KahluaUtil.fail("Object tried to call nil in unknown");
						}
					}

					object6 = this.prepareMetatableCall(object);
					if (object6 == null) {
						KahluaUtil.fail("Object " + object + " did not have __call metatable set");
					}

					if (object6 != object) {
						int4 = int5;
						++int10;
					}

					if (object6 instanceof LuaClosure) {
						LuaCallFrame luaCallFrame3 = this.currentCoroutine.pushNewCallFrame((LuaClosure)object6, (JavaFunction)null, int4, int5, int10, true, luaCallFrame.canYield);
						luaCallFrame3.init();
						luaCallFrame = luaCallFrame3;
						luaClosure = luaCallFrame3.closure;
						prototype = luaClosure.prototype;
						intArray = prototype.code;
						int1 = luaCallFrame3.returnBase;
						break;
					} else {
						if (!(object6 instanceof JavaFunction)) {
							throw new RuntimeException("Tried to call a non-function: " + object6);
						}

						this.callJava((JavaFunction)object6, int4, int5, int10);
						luaCallFrame = this.currentCoroutine.currentCallFrame();
						if (luaCallFrame != null && !luaCallFrame.isJava()) {
							luaClosure = luaCallFrame.closure;
							prototype = luaClosure.prototype;
							intArray = prototype.code;
							int1 = luaCallFrame.returnBase;
							if (luaCallFrame.restoreTop) {
								luaCallFrame.setTop(prototype.maxStacksize);
							}

							break;
						}

						long3 = System.nanoTime();
						return;
					}

				
				case 29: 
					int10 = luaCallFrame.localBase;
					this.currentCoroutine.closeUpvalues(int10);
					int7 = getA8(int2);
					int9 = getB9(int2);
					int11 = int9 - 1;
					if (int11 == -1) {
						int11 = luaCallFrame.getTop() - int7 - 1;
					}

					luaCallFrame.restoreTop = false;
					object3 = luaCallFrame.get(int7);
					try {
						KahluaUtil.luaAssert(object3 != null, "Tried to call nil");
					} catch (Exception exception) {
						if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
							UIManager.debugBreakpoint(LuaManager.thread.currentfile, (long)(LuaManager.thread.currentLine - 1));
						}

						this.debugException(exception);
						this.doStacktraceProper(luaCallFrame);
						KahluaUtil.fail("");
					}

					object4 = this.prepareMetatableCall(object3);
					if (object4 == null) {
						KahluaUtil.fail("Object did not have __call metatable set");
					}

					int12 = int1 + 1;
					if (object4 != object3) {
						int12 = int1;
						++int11;
					}

					this.currentCoroutine.stackCopy(int10 + int7, int1, int11 + 1);
					this.currentCoroutine.setTop(int1 + int11 + 1);
					if (object4 instanceof LuaClosure) {
						luaCallFrame.localBase = int12;
						luaCallFrame.nArguments = int11;
						luaCallFrame.closure = (LuaClosure)object4;
						luaCallFrame.init();
					} else {
						if (!(object4 instanceof JavaFunction)) {
							KahluaUtil.fail("Tried to call a non-function: " + object4);
						}

						Coroutine coroutine3 = this.currentCoroutine;
						this.callJava((JavaFunction)object4, int12, int1, int11);
						luaCallFrame = this.currentCoroutine.currentCallFrame();
						coroutine3.popCallFrame();
						if (coroutine3 != this.currentCoroutine) {
							if (coroutine3.isDead() && coroutine3 != this.rootCoroutine && this.currentCoroutine.getParent() == coroutine3) {
								this.currentCoroutine.resume(coroutine3.getParent());
								coroutine3.destroy();
								this.currentCoroutine.getParent().currentCallFrame().push(Boolean.TRUE);
							}

							luaCallFrame = this.currentCoroutine.currentCallFrame();
							if (luaCallFrame.isJava()) {
								long3 = System.nanoTime();
								return;
							}
						} else {
							if (!luaCallFrame.fromLua) {
								long3 = System.nanoTime();
								return;
							}

							luaCallFrame = this.currentCoroutine.currentCallFrame();
							if (luaCallFrame.restoreTop) {
								luaCallFrame.setTop(luaCallFrame.closure.prototype.maxStacksize);
							}
						}
					}

					luaClosure = luaCallFrame.closure;
					prototype = luaClosure.prototype;
					intArray = prototype.code;
					int1 = luaCallFrame.returnBase;
					break;
				
				case 30: 
					int7 = getA8(int2);
					int9 = getB9(int2) - 1;
					int10 = luaCallFrame.localBase;
					this.currentCoroutine.closeUpvalues(int10);
					if (int9 == -1) {
						int9 = luaCallFrame.getTop() - int7;
					}

					this.currentCoroutine.stackCopy(luaCallFrame.localBase + int7, int1, int9);
					this.currentCoroutine.setTop(int1 + int9);
					if (!luaCallFrame.fromLua) {
						this.currentCoroutine.popCallFrame();
						long long4 = System.nanoTime();
						return;
					}

					if (luaCallFrame.canYield && this.currentCoroutine.atBottom()) {
						luaCallFrame.localBase = luaCallFrame.returnBase;
						Coroutine coroutine4 = this.currentCoroutine;
						Coroutine.yieldHelper(luaCallFrame, luaCallFrame, int9);
						coroutine4.popCallFrame();
						luaCallFrame = this.currentCoroutine.currentCallFrame();
						if (luaCallFrame == null || luaCallFrame.isJava()) {
							return;
						}
					} else {
						this.currentCoroutine.popCallFrame();
					}

					luaCallFrame = this.currentCoroutine.currentCallFrame();
					luaClosure = luaCallFrame.closure;
					prototype = luaClosure.prototype;
					intArray = prototype.code;
					int1 = luaCallFrame.returnBase;
					if (luaCallFrame.restoreTop) {
						luaCallFrame.setTop(prototype.maxStacksize);
					}

					break;
				
				case 31: 
					Double Double4;
					label728: {
						int7 = getA8(int2);
						double1 = KahluaUtil.fromDouble(luaCallFrame.get(int7));
						double2 = KahluaUtil.fromDouble(luaCallFrame.get(int7 + 1));
						double3 = KahluaUtil.fromDouble(luaCallFrame.get(int7 + 2));
						double1 += double3;
						Double4 = KahluaUtil.toDouble(double1);
						luaCallFrame.set(int7, Double4);
						if (double3 > 0.0) {
							if (double1 <= double2) {
								break label728;
							}
						} else if (double1 >= double2) {
							break label728;
						}

						luaCallFrame.clearFromIndex(int7);
						break;
					}

					int9 = getSBx(int2);
					luaCallFrame.pc += int9;
					luaCallFrame.set(int7 + 3, Double4);
					break;
				
				case 32: 
					int7 = getA8(int2);
					int9 = getSBx(int2);
					double1 = KahluaUtil.fromDouble(luaCallFrame.get(int7));
					double2 = KahluaUtil.fromDouble(luaCallFrame.get(int7 + 2));
					luaCallFrame.set(int7, KahluaUtil.toDouble(double1 - double2));
					luaCallFrame.pc += int9;
					break;
				
				case 33: 
					int7 = getA8(int2);
					int8 = getC9(int2);
					luaCallFrame.setTop(int7 + 6);
					luaCallFrame.stackCopy(int7, int7 + 3, 3);
					this.call(2);
					luaCallFrame.clearFromIndex(int7 + 3 + int8);
					luaCallFrame.setPrototypeStacksize();
					object2 = luaCallFrame.get(int7 + 3);
					if (object2 != null) {
						luaCallFrame.set(int7 + 2, object2);
					} else {
						++luaCallFrame.pc;
					}

					break;
				
				case 34: 
					int7 = getA8(int2);
					int9 = getB9(int2);
					int8 = getC9(int2);
					if (int9 == 0) {
						int9 = luaCallFrame.getTop() - int7 - 1;
					}

					if (int8 == 0) {
						int8 = intArray[luaCallFrame.pc++];
					}

					int10 = (int8 - 1) * 50;
					KahluaTable kahluaTable3 = (KahluaTable)luaCallFrame.get(int7);
					int4 = 1;
					while (true) {
						if (int4 > int9) {
							continue label938;
						}

						Double1 = KahluaUtil.toDouble((long)(int10 + int4));
						object = luaCallFrame.get(int7 + int4);
						kahluaTable3.rawset(Double1, object);
						++int4;
					}

				
				case 35: 
					int7 = getA8(int2);
					luaCallFrame.closeUpvalues(int7);
					break;
				
				case 36: 
					int7 = getA8(int2);
					int9 = getBx(int2);
					Prototype prototype2 = prototype.prototypes[int9];
					LuaClosure luaClosure2 = new LuaClosure(prototype2, luaClosure.env);
					luaCallFrame.set(int7, luaClosure2);
					int4 = prototype2.numUpvalues;
					int5 = 0;
					while (true) {
						if (int5 >= int4) {
							continue label938;
						}

						int2 = intArray[luaCallFrame.pc++];
						int3 = int2 & 63;
						int9 = getB9(int2);
						switch (int3) {
						case 0: 
							luaClosure2.upvalues[int5] = luaCallFrame.findUpvalue(int9);
							break;
						
						case 4: 
							luaClosure2.upvalues[int5] = luaClosure.upvalues[int9];
						
						}

						++int5;
					}

				
				case 37: 
					int7 = getA8(int2);
					int9 = getB9(int2) - 1;
					luaCallFrame.pushVarargs(int7, int9);
				
				}
			} catch (RuntimeException runtimeException) {
				if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
				}

				if (runtimeException.getMessage() != null) {
					ExceptionLogger.logException(runtimeException);
					this.debugException(runtimeException);
				}

				this.doStacktraceProper(luaCallFrame);
				KahluaUtil.fail("");
				boolean boolean9 = true;
				do {
					luaCallFrame = this.currentCoroutine.currentCallFrame();
					if (luaCallFrame == null) {
						Coroutine coroutine5 = this.currentCoroutine.getParent();
						if (coroutine5 != null) {
							LuaCallFrame luaCallFrame4 = coroutine5.currentCallFrame();
							luaCallFrame4.push(Boolean.FALSE);
							luaCallFrame4.push(runtimeException.getMessage());
							luaCallFrame4.push(this.currentCoroutine.stackTrace);
							this.currentCoroutine.destroy();
							this.currentCoroutine = coroutine5;
							luaCallFrame = this.currentCoroutine.currentCallFrame();
							luaClosure = luaCallFrame.closure;
							prototype = luaClosure.prototype;
							intArray = prototype.code;
							int1 = luaCallFrame.returnBase;
							boolean9 = false;
						}

						break;
					}

					this.currentCoroutine.addStackTrace(luaCallFrame);
					this.currentCoroutine.popCallFrame();
				}		 while (luaCallFrame.fromLua);

				if (luaCallFrame != null) {
					luaCallFrame.closeUpvalues(0);
				}

				if (boolean9) {
					throw runtimeException;
				}
			} catch (Exception exception2) {
				if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
					UIManager.debugBreakpoint(LuaManager.thread.currentfile, (long)(LuaManager.thread.currentLine - 1));
				}

				if (exception2.getMessage() != null) {
					System.out.printf(exception2.getMessage());
				}
			}
		} while (!this.bReset);
		throw new RuntimeException("lua was reset");
	}

	private void DoProfileTiming(String string, long long1, long long2) {
		if (this.doProfiling) {
			double double1 = (double)(long2 - long1) / 1000000.0;
			if (GameWindow.states.current == IngameState.instance) {
				KahluaThread.Entry entry = null;
				if (this.profileEntryMap.containsKey(string)) {
					entry = (KahluaThread.Entry)this.profileEntryMap.get(string);
				} else {
					entry = new KahluaThread.Entry();
					this.profileEntryMap.put(string, entry);
					this.profileEntries.add(entry);
					entry.file = string;
				}

				entry.time += double1;
				Collections.sort(this.profileEntries, new KahluaThread.ProfileEntryComparitor());
			}
		}
	}

	public StringBuilder startErrorMessage() {
		this.m_stringBuilder.setLength(0);
		return this.m_stringBuilder;
	}

	public void flushErrorMessage() {
		String string = this.m_stringBuilder.toString();
		DebugLog.log(string);
		while (m_errors_list.size() >= 40) {
			m_errors_list.remove(0);
		}

		m_errors_list.add(string);
		++m_error_count;
	}

	public void doStacktraceProper(LuaCallFrame luaCallFrame) {
		if (luaCallFrame != null) {
			StringBuilder stringBuilder = this.startErrorMessage();
			stringBuilder.append("-----------------------------------------\n");
			stringBuilder.append("STACK TRACE\n");
			stringBuilder.append("-----------------------------------------\n");
			int int1 = luaCallFrame.coroutine.getCallframeTop();
			for (int int2 = int1 - 1; int2 >= 0; --int2) {
				LuaCallFrame luaCallFrame2 = luaCallFrame.coroutine.getCallFrame(int2);
				stringBuilder.append(luaCallFrame2.toString2());
				stringBuilder.append("\n");
			}

			this.flushErrorMessage();
		}
	}

	public void doStacktraceProper() {
		LuaCallFrame luaCallFrame = this.currentCoroutine.currentCallFrame();
		this.doStacktraceProper(luaCallFrame);
	}

	public void debugException(Exception exception) {
		this.m_stringWriter.getBuffer().setLength(0);
		exception.printStackTrace(this.m_printWriter);
		String string = this.m_stringWriter.toString();
		m_errors_list.add(string);
		++m_error_count;
	}

	protected Object getMetaOp(Object object, String string) {
		KahluaTable kahluaTable = (KahluaTable)this.getmetatable(object, true);
		return kahluaTable == null ? null : kahluaTable.rawget(string);
	}

	private final Object getCompMetaOp(Object object, Object object2, String string) {
		KahluaTable kahluaTable = (KahluaTable)this.getmetatable(object, true);
		KahluaTable kahluaTable2 = (KahluaTable)this.getmetatable(object2, true);
		if (kahluaTable != null && kahluaTable2 != null) {
			Object object3 = kahluaTable.rawget(string);
			Object object4 = kahluaTable2.rawget(string);
			return object3 == object4 && object3 != null ? object3 : null;
		} else {
			return null;
		}
	}

	private final Object getBinMetaOp(Object object, Object object2, String string) {
		Object object3 = this.getMetaOp(object, string);
		return object3 != null ? object3 : this.getMetaOp(object2, string);
	}

	private final Object getRegisterOrConstant(LuaCallFrame luaCallFrame, int int1, Prototype prototype) {
		int int2 = int1 - 256;
		return int2 < 0 ? luaCallFrame.get(int1) : prototype.constants[int2];
	}

	private static final int getA8(int int1) {
		return int1 >>> 6 & 255;
	}

	private static final int getC9(int int1) {
		return int1 >>> 14 & 511;
	}

	private static final int getB9(int int1) {
		return int1 >>> 23 & 511;
	}

	private static final int getBx(int int1) {
		return int1 >>> 14;
	}

	private static final int getSBx(int int1) {
		return (int1 >>> 14) - 131071;
	}

	private Double primitiveMath(Double Double1, Double Double2, int int1) {
		double double1 = KahluaUtil.fromDouble(Double1);
		double double2 = KahluaUtil.fromDouble(Double2);
		double double3 = 0.0;
		switch (int1) {
		case 12: 
			double3 = double1 + double2;
			break;
		
		case 13: 
			double3 = double1 - double2;
			break;
		
		case 14: 
			double3 = double1 * double2;
			break;
		
		case 15: 
			double3 = double1 / double2;
			break;
		
		case 16: 
			if (double2 == 0.0) {
				double3 = Double.NaN;
			} else {
				int int2 = (int)(double1 / double2);
				double3 = double1 - (double)int2 * double2;
			}

			break;
		
		case 17: 
			double3 = this.platform.pow(double1, double2);
		
		}
		return KahluaUtil.toDouble(double3);
	}

	public Object call(Object object, Object object2, Object object3, Object object4) {
		int int1 = this.currentCoroutine.getTop();
		this.currentCoroutine.setTop(int1 + 1 + 3);
		this.currentCoroutine.objectStack[int1] = object;
		this.currentCoroutine.objectStack[int1 + 1] = object2;
		this.currentCoroutine.objectStack[int1 + 2] = object3;
		this.currentCoroutine.objectStack[int1 + 3] = object4;
		int int2 = this.call(3);
		Object object5 = null;
		if (int2 >= 1) {
			object5 = this.currentCoroutine.objectStack[int1];
		}

		this.currentCoroutine.setTop(int1);
		return object5;
	}

	public Object call(Object object, Object[] objectArray) {
		int int1 = this.currentCoroutine.getTop();
		int int2 = objectArray == null ? 0 : objectArray.length;
		this.currentCoroutine.setTop(int1 + 1 + int2);
		this.currentCoroutine.objectStack[int1] = object;
		int int3;
		for (int3 = 1; int3 <= int2; ++int3) {
			this.currentCoroutine.objectStack[int1 + int3] = objectArray[int3 - 1];
		}

		int3 = this.call(int2);
		Object object2 = null;
		if (int3 >= 1) {
			object2 = this.currentCoroutine.objectStack[int1];
		}

		this.currentCoroutine.setTop(int1);
		return object2;
	}

	public Object tableget(Object object, Object object2) {
		Object object3 = object;
		for (int int1 = 100; int1 > 0; --int1) {
			boolean boolean1 = object3 instanceof KahluaTable;
			Object object4;
			if (boolean1) {
				KahluaTable kahluaTable = (KahluaTable)object3;
				object4 = kahluaTable.rawget(object2);
				if (object4 != null) {
					return object4;
				}
			}

			Object object5 = this.getMetaOp(object3, "__index");
			if (object5 == null) {
				if (boolean1) {
					return null;
				}

				StringBuilder stringBuilder = this.startErrorMessage();
				stringBuilder.append("-------------------------------------------------------------\n");
				stringBuilder.append("attempted index: " + object2 + " of non-table: " + object3 + "\n");
				this.flushErrorMessage();
				this.doStacktraceProper(this.currentCoroutine.currentCallFrame());
				throw new RuntimeException("attempted index: " + object2 + " of non-table: " + object3);
			}

			if (object5 instanceof JavaFunction || object5 instanceof LuaClosure) {
				object4 = this.call(object5, object, object2, (Object)null);
				return object4;
			}

			object3 = object5;
		}

		throw new RuntimeException("loop in gettable");
	}

	public void tableSet(Object object, Object object2, Object object3) {
		Object object4 = object;
		for (int int1 = 100; int1 > 0; --int1) {
			Object object5;
			if (object4 instanceof KahluaTable) {
				KahluaTable kahluaTable = (KahluaTable)object4;
				if (kahluaTable.rawget(object2) != null) {
					kahluaTable.rawset(object2, object3);
					return;
				}

				object5 = this.getMetaOp(object4, "__newindex");
				if (object5 == null) {
					kahluaTable.rawset(object2, object3);
					return;
				}
			} else {
				object5 = this.getMetaOp(object4, "__newindex");
				if (object5 == null) {
					this.doStacktraceProper(this.currentCoroutine.currentCallFrame());
				}

				KahluaUtil.luaAssert(object5 != null, "attempted index of non-table");
			}

			if (object5 instanceof JavaFunction || object5 instanceof LuaClosure) {
				this.call(object5, object, object2, object3);
				return;
			}

			object4 = object5;
		}

		throw new RuntimeException("loop in settable");
	}

	public void setmetatable(Object object, KahluaTable kahluaTable) {
		KahluaUtil.luaAssert(object != null, "Can\'t set metatable for nil");
		if (object instanceof KahluaTable) {
			KahluaTable kahluaTable2 = (KahluaTable)object;
			kahluaTable2.setMetatable(kahluaTable);
		} else {
			KahluaUtil.fail("Could not set metatable for object");
		}
	}

	public Object getmetatable(Object object, boolean boolean1) {
		if (object == null) {
			return null;
		} else {
			KahluaTable kahluaTable = null;
			KahluaTable kahluaTable2;
			if (object instanceof KahluaTable) {
				kahluaTable2 = (KahluaTable)object;
				kahluaTable = kahluaTable2.getMetatable();
			} else if (kahluaTable == null) {
				kahluaTable2 = KahluaUtil.getClassMetatables(this.platform, this.getEnvironment());
				kahluaTable = (KahluaTable)this.tableget(kahluaTable2, object.getClass());
			}

			if (!boolean1 && kahluaTable != null) {
				Object object2 = kahluaTable.rawget("__metatable");
				if (object2 != null) {
					return object2;
				}
			}

			return kahluaTable;
		}
	}

	public Object[] pcall(Object object, Object[] objectArray) {
		int int1 = objectArray == null ? 0 : objectArray.length;
		Coroutine coroutine = this.currentCoroutine;
		int int2 = coroutine.getTop();
		coroutine.setTop(int2 + 1 + int1);
		coroutine.objectStack[int2] = object;
		if (int1 > 0) {
			System.arraycopy(objectArray, 0, coroutine.objectStack, int2 + 1, int1);
		}

		int int3 = this.pcall(int1);
		KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
		Object[] objectArray2 = null;
		if (objectArray.length == int3) {
			objectArray2 = objectArray;
		} else {
			objectArray2 = new Object[int3];
		}

		System.arraycopy(coroutine.objectStack, int2, objectArray2, 0, int3);
		coroutine.setTop(int2);
		return objectArray2;
	}

	public void pcallvoid(Object object, Object[] objectArray) {
		int int1 = objectArray == null ? 0 : objectArray.length;
		Coroutine coroutine = this.currentCoroutine;
		int int2 = coroutine.getTop();
		coroutine.setTop(int2 + 1 + int1);
		coroutine.objectStack[int2] = object;
		if (int1 > 0) {
			System.arraycopy(objectArray, 0, coroutine.objectStack, int2 + 1, int1);
		}

		this.pcall(int1);
		KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
		coroutine.setTop(int2);
	}

	public void pcallvoid(Object object, Object object2) {
		Coroutine coroutine = this.currentCoroutine;
		int int1 = coroutine.getTop();
		coroutine.setTop(int1 + 1 + 1);
		coroutine.objectStack[int1] = object;
		coroutine.objectStack[int1 + 1] = object2;
		int int2 = this.pcall(1);
		KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
		coroutine.setTop(int1);
	}

	public void pcallvoid(Object object, Object object2, Object object3) {
		Coroutine coroutine = this.currentCoroutine;
		int int1 = coroutine.getTop();
		coroutine.setTop(int1 + 1 + 2);
		coroutine.objectStack[int1] = object;
		coroutine.objectStack[int1 + 1] = object2;
		coroutine.objectStack[int1 + 2] = object3;
		int int2 = this.pcall(2);
		KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
		coroutine.setTop(int1);
	}

	public void pcallvoid(Object object, Object object2, Object object3, Object object4) {
		Coroutine coroutine = this.currentCoroutine;
		int int1 = coroutine.getTop();
		coroutine.setTop(int1 + 1 + 3);
		coroutine.objectStack[int1] = object;
		coroutine.objectStack[int1 + 1] = object2;
		coroutine.objectStack[int1 + 2] = object3;
		coroutine.objectStack[int1 + 3] = object4;
		int int2 = this.pcall(3);
		KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
		coroutine.setTop(int1);
	}

	public Boolean pcallBoolean(Object object, Object object2) {
		Coroutine coroutine = this.currentCoroutine;
		int int1 = coroutine.getTop();
		coroutine.setTop(int1 + 1 + 1);
		coroutine.objectStack[int1] = object;
		coroutine.objectStack[int1 + 1] = object2;
		int int2 = this.pcall(1);
		KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
		Boolean Boolean1 = null;
		if (int2 > 1) {
			Boolean Boolean2 = (Boolean)coroutine.objectStack[int1];
			if (Boolean2) {
				Object object3 = coroutine.objectStack[int1 + 1];
				if (object3 instanceof Boolean) {
					Boolean1 = (Boolean)object3 ? Boolean.TRUE : Boolean.FALSE;
				}
			}
		}

		coroutine.setTop(int1);
		return Boolean1;
	}

	public Boolean pcallBoolean(Object object, Object object2, Object object3) {
		Coroutine coroutine = this.currentCoroutine;
		int int1 = coroutine.getTop();
		coroutine.setTop(int1 + 1 + 2);
		coroutine.objectStack[int1] = object;
		coroutine.objectStack[int1 + 1] = object2;
		coroutine.objectStack[int1 + 2] = object3;
		int int2 = this.pcall(2);
		KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
		Boolean Boolean1 = null;
		if (int2 > 1) {
			Boolean Boolean2 = (Boolean)coroutine.objectStack[int1];
			if (Boolean2) {
				Object object4 = coroutine.objectStack[int1 + 1];
				if (object4 instanceof Boolean) {
					Boolean1 = (Boolean)object4 ? Boolean.TRUE : Boolean.FALSE;
				}
			}
		}

		coroutine.setTop(int1);
		return Boolean1;
	}

	public Boolean pcallBoolean(Object object, Object object2, Object object3, Object object4) {
		Coroutine coroutine = this.currentCoroutine;
		int int1 = coroutine.getTop();
		coroutine.setTop(int1 + 1 + 3);
		coroutine.objectStack[int1] = object;
		coroutine.objectStack[int1 + 1] = object2;
		coroutine.objectStack[int1 + 2] = object3;
		coroutine.objectStack[int1 + 3] = object4;
		int int2 = this.pcall(3);
		KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
		Boolean Boolean1 = null;
		if (int2 > 1) {
			Boolean Boolean2 = (Boolean)coroutine.objectStack[int1];
			if (Boolean2) {
				Object object5 = coroutine.objectStack[int1 + 1];
				if (object5 instanceof Boolean) {
					Boolean1 = (Boolean)object5 ? Boolean.TRUE : Boolean.FALSE;
				}
			}
		}

		coroutine.setTop(int1);
		return Boolean1;
	}

	public Boolean pcallBoolean(Object object, Object[] objectArray) {
		int int1 = objectArray == null ? 0 : objectArray.length;
		Coroutine coroutine = this.currentCoroutine;
		int int2 = coroutine.getTop();
		coroutine.setTop(int2 + 1 + int1);
		coroutine.objectStack[int2] = object;
		if (int1 > 0) {
			System.arraycopy(objectArray, 0, coroutine.objectStack, int2 + 1, int1);
		}

		int int3 = this.pcall(int1);
		KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
		Boolean Boolean1 = null;
		if (int3 > 1) {
			Boolean Boolean2 = (Boolean)coroutine.objectStack[int2];
			if (Boolean2) {
				Object object2 = coroutine.objectStack[int2 + 1];
				if (object2 instanceof Boolean) {
					Boolean1 = (Boolean)object2 ? Boolean.TRUE : Boolean.FALSE;
				}
			}
		}

		coroutine.setTop(int2);
		return Boolean1;
	}

	public Object[] pcall(Object object) {
		return this.pcall(object, (Object[])null);
	}

	public int pcall(int int1) {
		Coroutine coroutine = this.currentCoroutine;
		LuaCallFrame luaCallFrame = coroutine.currentCallFrame();
		coroutine.stackTrace = "";
		int int2 = coroutine.getTop() - int1 - 1;
		Object object;
		Object object2;
		try {
			int int3 = coroutine.getCallframeTop();
			int int4 = this.call(int1);
			int int5 = coroutine.getCallframeTop();
			boolean boolean1;
			if (int3 != int5) {
				boolean1 = false;
			}

			KahluaUtil.luaAssert(int3 == int5, "error - call stack depth changed.");
			if (int3 != int5) {
				boolean1 = false;
			}

			int int6 = int2 + int4 + 1;
			coroutine.setTop(int6);
			coroutine.stackCopy(int2, int2 + 1, int4);
			coroutine.objectStack[int2] = Boolean.TRUE;
			return 1 + int4;
		} catch (KahluaException kahluaException) {
			object2 = kahluaException;
			object = kahluaException.errorMessage;
		} catch (Throwable throwable) {
			object2 = throwable;
			String string = throwable.getMessage();
			object = string + " " + throwable.getClass().getName();
		}

		KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
		if (luaCallFrame != null) {
			luaCallFrame.closeUpvalues(0);
		}

		coroutine.cleanCallFrames(luaCallFrame);
		if (object instanceof String) {
			object = (String)object;
		}

		coroutine.setTop(int2 + 4);
		coroutine.objectStack[int2] = Boolean.FALSE;
		coroutine.objectStack[int2 + 1] = object;
		coroutine.objectStack[int2 + 2] = coroutine.stackTrace;
		coroutine.objectStack[int2 + 3] = object2;
		coroutine.stackTrace = "";
		return 4;
	}

	public KahluaTable getEnvironment() {
		return this.currentCoroutine.environment;
	}

	public PrintStream getOut() {
		return this.out;
	}

	public Platform getPlatform() {
		return this.platform;
	}

	public void breakpointToggle(String string, int int1) {
		ArrayList arrayList;
		if (!this.BreakpointMap.containsKey(string)) {
			arrayList = new ArrayList();
			this.BreakpointMap.put(string, arrayList);
		} else {
			arrayList = (ArrayList)this.BreakpointMap.get(string);
		}

		if (!arrayList.contains((long)int1)) {
			arrayList.add((long)int1);
		} else {
			arrayList.remove((long)int1);
		}
	}

	public boolean hasBreakpoint(String string, int int1) {
		return this.BreakpointMap.containsKey(string) && ((ArrayList)this.BreakpointMap.get(string)).contains((long)int1);
	}

	public void toggleBreakOnChange(KahluaTable kahluaTable, Object object) {
		ArrayList arrayList;
		if (!this.BreakpointDataMap.containsKey(kahluaTable)) {
			arrayList = new ArrayList();
			this.BreakpointDataMap.put(kahluaTable, arrayList);
		} else {
			arrayList = (ArrayList)this.BreakpointDataMap.get(kahluaTable);
		}

		if (!arrayList.contains(object)) {
			arrayList.add(object);
		} else {
			arrayList.remove(object);
		}
	}

	public void toggleBreakOnRead(KahluaTable kahluaTable, Object object) {
		ArrayList arrayList;
		if (!this.BreakpointReadDataMap.containsKey(kahluaTable)) {
			arrayList = new ArrayList();
			this.BreakpointReadDataMap.put(kahluaTable, arrayList);
		} else {
			arrayList = (ArrayList)this.BreakpointReadDataMap.get(kahluaTable);
		}

		if (!arrayList.contains(object)) {
			arrayList.add(object);
		} else {
			arrayList.remove(object);
		}
	}

	public boolean hasDataBreakpoint(KahluaTable kahluaTable, Object object) {
		if (!this.BreakpointDataMap.containsKey(kahluaTable)) {
			return false;
		} else {
			ArrayList arrayList = (ArrayList)this.BreakpointDataMap.get(kahluaTable);
			return arrayList.contains(object);
		}
	}

	public boolean hasReadDataBreakpoint(KahluaTable kahluaTable, Object object) {
		if (!this.BreakpointReadDataMap.containsKey(kahluaTable)) {
			return false;
		} else {
			ArrayList arrayList = (ArrayList)this.BreakpointReadDataMap.get(kahluaTable);
			return arrayList.contains(object);
		}
	}

	static  {
		meta_ops[12] = "__add";
		meta_ops[13] = "__sub";
		meta_ops[14] = "__mul";
		meta_ops[15] = "__div";
		meta_ops[16] = "__mod";
		meta_ops[17] = "__pow";
		meta_ops[23] = "__eq";
		meta_ops[24] = "__lt";
		meta_ops[25] = "__le";
		LastCallFrame = null;
		m_error_count = 0;
		m_errors_list = new ArrayList();
	}

	public static class Entry {
		public String file;
		public double time;
	}

	private static class ProfileEntryComparitor implements Comparator {

		public ProfileEntryComparitor() {
		}

		public int compare(KahluaThread.Entry entry, KahluaThread.Entry entry2) {
			double double1 = entry.time;
			double double2 = entry2.time;
			if (double1 > double2) {
				return -1;
			} else {
				return double2 > double1 ? 1 : 0;
			}
		}
	}
}
