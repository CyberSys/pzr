package se.krka.kahlua.integration.expose;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.integration.annotations.Desc;
import se.krka.kahlua.integration.annotations.LuaConstructor;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.processor.ClassParameterInformation;
import se.krka.kahlua.integration.processor.DescriptorUtil;
import se.krka.kahlua.integration.processor.MethodParameterInformation;


public class ClassDebugInformation {
	private final Map methods = new HashMap();

	public ClassDebugInformation(Class javaClass, ClassParameterInformation classParameterInformation) {
		this.addContent(javaClass, classParameterInformation);
		this.addConstructors(javaClass, classParameterInformation);
	}

	private void addContent(Class javaClass, ClassParameterInformation classParameterInformation) {
		if (javaClass != null) {
			this.addContent(javaClass.getSuperclass(), classParameterInformation);
			Class[] classArray = javaClass.getInterfaces();
			int int1 = classArray.length;
			int int2;
			for (int2 = 0; int2 < int1; ++int2) {
				Class javaClass2 = classArray[int2];
				this.addContent(javaClass2, classParameterInformation);
			}

			Method[] methodArray = javaClass.getDeclaredMethods();
			int1 = methodArray.length;
			for (int2 = 0; int2 < int1; ++int2) {
				Method method = methodArray[int2];
				LuaMethod luaMethod = (LuaMethod)AnnotationUtil.getAnnotation(method, LuaMethod.class);
				String string = method.getName();
				int int3 = method.getModifiers();
				Type[] typeArray = method.getGenericParameterTypes();
				String string2 = DescriptorUtil.getDescriptor(method);
				Type type = method.getGenericReturnType();
				Annotation[][] annotationArrayArray = method.getParameterAnnotations();
				Desc desc = (Desc)AnnotationUtil.getAnnotation(method, Desc.class);
				this.addMethod(classParameterInformation, typeArray, string2, type, annotationArrayArray, getName(luaMethod, string), !isGlobal(luaMethod, isStatic(int3)), desc);
			}
		}
	}

	private void addConstructors(Class javaClass, ClassParameterInformation classParameterInformation) {
		Constructor[] constructorArray = javaClass.getConstructors();
		int int1 = constructorArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			Constructor constructor = constructorArray[int2];
			LuaConstructor luaConstructor = (LuaConstructor)constructor.getAnnotation(LuaConstructor.class);
			String string = "new";
			Type[] typeArray = constructor.getGenericParameterTypes();
			String string2 = DescriptorUtil.getDescriptor(constructor);
			Annotation[][] annotationArrayArray = constructor.getParameterAnnotations();
			Desc desc = (Desc)constructor.getAnnotation(Desc.class);
			this.addMethod(classParameterInformation, typeArray, string2, javaClass, annotationArrayArray, getName(luaConstructor, string), true, desc);
		}
	}

	private void addMethod(ClassParameterInformation classParameterInformation, Type[] typeArray, String string, Type type, Annotation[][] annotationArrayArray, String string2, boolean boolean1, Desc desc) {
		MethodParameterInformation methodParameterInformation = (MethodParameterInformation)classParameterInformation.methods.get(string);
		if (!this.methods.containsKey(string)) {
			if (methodParameterInformation != null) {
				ArrayList arrayList = new ArrayList();
				for (int int1 = 0; int1 < typeArray.length; ++int1) {
					Type type2 = typeArray[int1];
					String string3 = methodParameterInformation.getName(int1);
					String string4 = TypeUtil.getClassName(type2);
					String string5 = this.getDescription(annotationArrayArray[int1]);
					arrayList.add(new MethodParameter(string3, string4, string5));
				}

				String string6 = TypeUtil.getClassName(type);
				String string7 = getDescription(desc);
				MethodDebugInformation methodDebugInformation = new MethodDebugInformation(string2, boolean1, arrayList, string6, string7);
				this.methods.put(string, methodDebugInformation);
			}
		}
	}

	private String getDescription(Annotation[] annotationArray) {
		Annotation[] annotationArray2 = annotationArray;
		int int1 = annotationArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			Annotation annotation = annotationArray2[int2];
			if (annotation != null && annotation instanceof Desc) {
				return getDescription((Desc)annotation);
			}
		}

		return null;
	}

	private static String getDescription(Desc desc) {
		return desc != null ? desc.value() : null;
	}

	private static boolean isStatic(int int1) {
		return (int1 & 8) != 0;
	}

	private static boolean isGlobal(LuaMethod luaMethod, boolean boolean1) {
		return luaMethod != null ? luaMethod.global() : boolean1;
	}

	private static String getName(LuaMethod luaMethod, String string) {
		if (luaMethod != null) {
			String string2 = luaMethod.name();
			if (string2 != null && string2.length() > 0) {
				return string2;
			}
		}

		return string;
	}

	private static String getName(LuaConstructor luaConstructor, String string) {
		if (luaConstructor != null) {
			String string2 = luaConstructor.name();
			if (string2 != null && string2.length() > 0) {
				return string2;
			}
		}

		return string;
	}

	public Map getMethods() {
		return this.methods;
	}
}
