package se.krka.kahlua.integration.expose.caller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.krka.kahlua.integration.processor.DescriptorUtil;
import zombie.core.logger.ExceptionLogger;
import zombie.ui.UIManager;


public class MethodCaller extends AbstractCaller {
	private final Method method;
	private final Object owner;
	private final boolean hasSelf;
	private final boolean hasReturnValue;

	public MethodCaller(Method method, Object object, boolean boolean1) {
		super(method.getParameterTypes(), method.isVarArgs());
		this.method = method;
		this.owner = object;
		this.hasSelf = boolean1;
		method.setAccessible(true);
		this.hasReturnValue = !method.getReturnType().equals(Void.TYPE);
		if (this.hasReturnValue && this.needsMultipleReturnValues()) {
			throw new IllegalArgumentException("Must have a void return type if first argument is a ReturnValues: got: " + method.getReturnType());
		}
	}

	public void call(Object object, ReturnValues returnValues, Object[] objectArray) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (!this.hasSelf) {
			object = this.owner;
		}

		try {
			Object object2 = this.method.invoke(object, objectArray);
			if (this.hasReturnValue) {
				returnValues.push(object2);
			}
		} catch (Exception exception) {
			UIManager.defaultthread.doStacktraceProper();
			UIManager.defaultthread.debugException(exception);
			ExceptionLogger.logException(exception);
		}
	}

	public boolean hasSelf() {
		return this.hasSelf;
	}

	public String getDescriptor() {
		return DescriptorUtil.getDescriptor(this.method);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			MethodCaller methodCaller = (MethodCaller)object;
			if (!this.method.equals(methodCaller.method)) {
				return false;
			} else {
				if (this.owner != null) {
					if (!this.owner.equals(methodCaller.owner)) {
						return false;
					}
				} else if (methodCaller.owner != null) {
					return false;
				}

				return true;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int int1 = this.method.hashCode();
		int1 = 31 * int1 + (this.owner != null ? this.owner.hashCode() : 0);
		return int1;
	}
}
