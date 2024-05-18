package se.krka.kahlua.integration.expose.caller;

import se.krka.kahlua.integration.expose.ReturnValues;


public abstract class AbstractCaller implements Caller {
	protected final Class[] parameters;
	protected final boolean needsMultipleReturnValues;
	protected final Class varargType;

	protected AbstractCaller(Class[] classArray) {
		boolean boolean1 = false;
		Class javaClass = null;
		if (classArray.length > 0) {
			Class javaClass2 = classArray[0];
			if (javaClass2 == ReturnValues.class) {
				boolean1 = true;
			}

			Class javaClass3 = classArray[classArray.length - 1];
			if (javaClass3.isArray()) {
				javaClass = javaClass3.getComponentType();
			}
		}

		this.needsMultipleReturnValues = boolean1;
		this.varargType = javaClass;
		int int1 = boolean1 ? 1 : 0;
		int int2 = classArray.length - (javaClass == null ? 0 : 1);
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
