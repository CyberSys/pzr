package se.krka.kahlua.integration.expose;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


public class AnnotationUtil {

	public static Annotation getAnnotation(Method method, Class javaClass) {
		return getAnnotation(method.getDeclaringClass(), method.getName(), method.getParameterTypes(), javaClass);
	}

	private static Annotation getAnnotation(Class javaClass, String string, Class[] classArray, Class javaClass2) {
		if (javaClass == null) {
			return null;
		} else {
			try {
				Method method = javaClass.getMethod(string, classArray);
				Annotation annotation = method.getAnnotation(javaClass2);
				if (annotation != null) {
					return annotation;
				} else {
					Class[] classArray2 = javaClass.getInterfaces();
					int int1 = classArray2.length;
					for (int int2 = 0; int2 < int1; ++int2) {
						Class javaClass3 = classArray2[int2];
						annotation = getAnnotation(javaClass3, string, classArray, javaClass2);
						if (annotation != null) {
							return annotation;
						}
					}

					return getAnnotation(javaClass.getSuperclass(), string, classArray, javaClass2);
				}
			} catch (NoSuchMethodException noSuchMethodException) {
				return null;
			}
		}
	}
}
