package se.krka.kahlua.integration.processor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.VariableElement;


public class DescriptorUtil {

	public static String getDescriptor(String string, List list) {
		String string2 = "";
		VariableElement variableElement;
		for (Iterator iterator = list.iterator(); iterator.hasNext(); string2 = string2 + ":" + variableElement.asType().toString()) {
			variableElement = (VariableElement)iterator.next();
		}

		return string + string2;
	}

	public static String getDescriptor(Constructor constructor) {
		String string = getParameters(constructor.getParameterTypes());
		return "new" + string;
	}

	public static String getDescriptor(Method method) {
		String string = getParameters(method.getParameterTypes());
		String string2 = method.getName();
		return string2 + string;
	}

	private static String getParameters(Class[] classArray) {
		String string = "";
		Class[] classArray2 = classArray;
		int int1 = classArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			Class javaClass = classArray2[int2];
			string = string + ":" + javaClass.getName();
		}

		return string;
	}
}
