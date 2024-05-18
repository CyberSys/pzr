package se.krka.kahlua.integration.expose.caller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.krka.kahlua.integration.processor.DescriptorUtil;


public class ConstructorCaller extends AbstractCaller {
	private final Constructor constructor;

	public ConstructorCaller(Constructor constructor) {
		super(constructor.getParameterTypes());
		this.constructor = constructor;
		constructor.setAccessible(true);
		if (this.needsMultipleReturnValues()) {
			throw new RuntimeException("Constructor can not return multiple values");
		}
	}

	public void call(Object object, ReturnValues returnValues, Object[] objectArray) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		returnValues.push(this.constructor.newInstance(objectArray));
	}

	public boolean hasSelf() {
		return false;
	}

	public String getDescriptor() {
		return DescriptorUtil.getDescriptor(this.constructor);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			ConstructorCaller constructorCaller = (ConstructorCaller)object;
			return this.constructor.equals(constructorCaller.constructor);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.constructor.hashCode();
	}
}
