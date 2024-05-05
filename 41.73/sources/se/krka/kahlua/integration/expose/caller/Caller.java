package se.krka.kahlua.integration.expose.caller;

import java.lang.reflect.InvocationTargetException;
import se.krka.kahlua.integration.expose.ReturnValues;


public interface Caller {

	void call(Object object, ReturnValues returnValues, Object[] objectArray) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

	Class[] getParameterTypes();

	boolean needsMultipleReturnValues();

	boolean hasSelf();

	Class getVarargType();

	boolean hasVararg();

	String getDescriptor();
}
