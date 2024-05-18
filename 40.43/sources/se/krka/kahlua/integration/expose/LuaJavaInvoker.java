package se.krka.kahlua.integration.expose;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.expose.caller.Caller;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.LuaCallFrame;


public class LuaJavaInvoker implements JavaFunction {
	private final LuaJavaClassExposer exposer;
	private final KahluaConverterManager manager;
	private final Class clazz;
	private final String name;
	private final Caller caller;
	private final Class[] parameterTypes;
	private final int numMethodParams;
	private final Class varargType;
	private final boolean hasSelf;
	private final boolean needsReturnValues;
	private final boolean hasVarargs;

	public LuaJavaInvoker(LuaJavaClassExposer luaJavaClassExposer, KahluaConverterManager kahluaConverterManager, Class javaClass, String string, Caller caller) {
		this.exposer = luaJavaClassExposer;
		this.manager = kahluaConverterManager;
		this.clazz = javaClass;
		this.name = string;
		this.caller = caller;
		this.parameterTypes = caller.getParameterTypes();
		this.varargType = caller.getVarargType();
		this.hasSelf = caller.hasSelf();
		this.needsReturnValues = caller.needsMultipleReturnValues();
		this.hasVarargs = caller.hasVararg();
		this.numMethodParams = this.parameterTypes.length + this.toInt(this.needsReturnValues) + this.toInt(this.hasVarargs);
	}

	private int toInt(boolean boolean1) {
		return boolean1 ? 1 : 0;
	}

	public MethodArguments prepareCall(LuaCallFrame luaCallFrame, int int1) {
		MethodArguments methodArguments = MethodArguments.get(this.numMethodParams);
		int int2 = 0;
		int int3 = 0;
		int int4 = this.toInt(this.hasSelf);
		if (this.hasSelf) {
			Object object = int1 <= 0 ? null : luaCallFrame.get(0);
			if (object == null || !this.clazz.isInstance(object)) {
				methodArguments.fail(this.syntaxErrorMessage(this.name + ": Expected a method call but got a function call."));
				return methodArguments;
			}

			methodArguments.setSelf(object);
			++int3;
		}

		ReturnValues returnValues = ReturnValues.get(this.manager, luaCallFrame);
		methodArguments.setReturnValues(returnValues);
		if (this.needsReturnValues) {
			methodArguments.getParams()[int2] = returnValues;
			++int2;
		}

		int int5;
		int int6;
		if (int1 - int3 < this.parameterTypes.length) {
			int5 = this.parameterTypes.length;
			int6 = int1 - int4;
			methodArguments.fail((String)null);
			return methodArguments;
		} else if (int3 != 0 && this.parameterTypes.length < int1 - int3) {
			int5 = this.parameterTypes.length;
			int6 = int1 - int4;
			methodArguments.fail((String)null);
			return methodArguments;
		} else {
			int int7;
			for (int5 = 0; int5 < this.parameterTypes.length; ++int5) {
				Object object2 = luaCallFrame.get(int3 + int5);
				int7 = int3 + int5 - int4;
				Class javaClass = this.parameterTypes[int5];
				Object object3 = object2;
				if (!javaClass.isInstance(object2)) {
					object3 = this.convert(object2, javaClass);
				}

				if (object2 != null && object3 == null) {
					methodArguments.fail("");
					return methodArguments;
				}

				methodArguments.getParams()[int2 + int5] = object3;
			}

			int2 += this.parameterTypes.length;
			int3 += this.parameterTypes.length;
			if (this.hasVarargs) {
				int5 = int1 - int3;
				if (int5 < 0) {
				}

				Object[] objectArray = (Object[])((Object[])Array.newInstance(this.varargType, int5));
				for (int7 = 0; int7 < int5; ++int7) {
					Object object4 = luaCallFrame.get(int3 + int7);
					int int8 = int3 + int7 - int4;
					Object object5 = this.convert(object4, this.varargType);
					objectArray[int7] = object5;
					if (object4 != null && object5 == null) {
						methodArguments.fail("");
						return methodArguments;
					}
				}

				methodArguments.getParams()[int2] = objectArray;
				++int2;
				int6 = int3 + int5;
			}

			return methodArguments;
		}
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		MethodArguments methodArguments = this.prepareCall(luaCallFrame, int1);
		methodArguments.assertValid();
		int int2 = this.call(methodArguments);
		ReturnValues.put(methodArguments.getReturnValues());
		MethodArguments.put(methodArguments);
		return int2;
	}

	public int call(MethodArguments methodArguments) {
		try {
			ReturnValues returnValues = methodArguments.getReturnValues();
			this.caller.call(methodArguments.getSelf(), returnValues, methodArguments.getParams());
			return returnValues.getNArguments();
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new RuntimeException(illegalArgumentException);
		} catch (IllegalAccessException illegalAccessException) {
			throw new RuntimeException(illegalAccessException);
		} catch (InvocationTargetException invocationTargetException) {
			throw new RuntimeException(invocationTargetException.getCause());
		} catch (InstantiationException instantiationException) {
			throw new RuntimeException(instantiationException);
		}
	}

	private Object convert(Object object, Class javaClass) {
		if (object == null) {
			return null;
		} else {
			Object object2 = this.manager.fromLuaToJava(object, javaClass);
			return object2;
		}
	}

	private String syntaxErrorMessage(String string) {
		String string2 = this.getFunctionSyntax();
		if (string2 != null) {
			string = string + " Correct syntax: " + string2;
		}

		return string;
	}

	private String newError(int int1, String string) {
		int int2 = int1 + 1;
		String string2 = string + " at argument #" + int2;
		String string3 = this.getParameterName(int1);
		if (string3 != null) {
			string2 = string2 + ", " + string3;
		}

		return string2;
	}

	private String getFunctionSyntax() {
		MethodDebugInformation methodDebugInformation = this.getMethodDebugData();
		return methodDebugInformation != null ? methodDebugInformation.getLuaDescription() : null;
	}

	public MethodDebugInformation getMethodDebugData() {
		ClassDebugInformation classDebugInformation = this.exposer.getDebugdata(this.clazz);
		return classDebugInformation == null ? null : (MethodDebugInformation)classDebugInformation.getMethods().get(this.caller.getDescriptor());
	}

	private String getParameterName(int int1) {
		MethodDebugInformation methodDebugInformation = this.getMethodDebugData();
		return methodDebugInformation != null ? ((MethodParameter)methodDebugInformation.getParameters().get(int1)).getName() : null;
	}

	public String toString() {
		return this.name;
	}

	public int getNumMethodParams() {
		return this.numMethodParams;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			LuaJavaInvoker luaJavaInvoker = (LuaJavaInvoker)object;
			if (!this.caller.equals(luaJavaInvoker.caller)) {
				return false;
			} else if (!this.clazz.equals(luaJavaInvoker.clazz)) {
				return false;
			} else {
				return this.name.equals(luaJavaInvoker.name);
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int int1 = this.clazz.hashCode();
		int1 = 31 * int1 + this.name.hashCode();
		int1 = 31 * int1 + this.caller.hashCode();
		return int1;
	}

	public boolean matchesArgumentTypes(LuaCallFrame luaCallFrame, int int1) {
		int int2 = 0;
		if (this.hasSelf) {
			Object object = int1 <= 0 ? null : luaCallFrame.get(0);
			if (object == null || !this.clazz.isInstance(object)) {
				return false;
			}

			++int2;
		}

		if (this.parameterTypes.length != int1 - int2) {
			return false;
		} else {
			for (int int3 = 0; int3 < this.parameterTypes.length; ++int3) {
				Object object2 = luaCallFrame.get(int2 + int3);
				Class javaClass = this.parameterTypes[int3];
				if (!javaClass.isInstance(object2)) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean matchesArgumentTypesOrPrimitives(LuaCallFrame luaCallFrame, int int1) {
		int int2 = 0;
		if (this.hasSelf) {
			Object object = int1 <= 0 ? null : luaCallFrame.get(0);
			if (object == null || !this.clazz.isInstance(object)) {
				return false;
			}

			++int2;
		}

		if (this.parameterTypes.length != int1 - int2) {
			return false;
		} else {
			for (int int3 = 0; int3 < this.parameterTypes.length; ++int3) {
				Object object2 = luaCallFrame.get(int2 + int3);
				Class javaClass = this.parameterTypes[int3];
				if (!javaClass.isInstance(object2)) {
					if (javaClass.isPrimitive()) {
						if (object2 == null) {
							return false;
						}

						if (object2 instanceof Double) {
							if (javaClass == Void.TYPE || javaClass == Boolean.TYPE) {
								return false;
							}
						} else if (!(object2 instanceof Boolean) || javaClass != Boolean.TYPE) {
							return false;
						}
					} else if (object2 != null) {
						return false;
					}
				}
			}

			return true;
		}
	}
}
