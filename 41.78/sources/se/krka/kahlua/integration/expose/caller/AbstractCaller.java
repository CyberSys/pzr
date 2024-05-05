package se.krka.kahlua.integration.expose.caller;

import se.krka.kahlua.integration.expose.ReturnValues;


public abstract class AbstractCaller implements Caller {
	protected final Class[] parameters;
	protected final boolean needsMultipleReturnValues;
	protected final Class varargType;

	protected AbstractCaller(Class[] classArray, boolean boolean1) {
		boolean boolean2 = false;
		Class javaClass;
		if (classArray.length > 0) {
			javaClass = classArray[0];
			if (javaClass == ReturnValues.class) {
				boolean2 = true;
			}
		}

		if (boolean1) {
			javaClass = classArray[classArray.length - 1];
			this.varargType = javaClass.getComponentType();
		} else {
			this.varargType = null;
		}

		this.needsMultipleReturnValues = boolean2;
		int int1 = boolean2 ? 1 : 0;
		int int2 = classArray.length - (this.varargType == null ? 0 : 1);
		int int3 = int2 - int1;
		this.parameters = new Class[int3];
		System.arraycopy(classArray, int1, this.parameters, 0, int3);
	}

	public final Class[] getParameterTypes() {
		return this.parameters;
	}

	public final Class getVarargType() {
		return this.varargType;
	}

	public final boolean hasVararg() {
		return this.varargType != null;
	}

	public final boolean needsMultipleReturnValues() {
		return this.needsMultipleReturnValues;
	}
}
