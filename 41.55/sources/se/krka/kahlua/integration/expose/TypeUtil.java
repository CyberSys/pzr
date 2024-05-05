package se.krka.kahlua.integration.expose;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.debug.DebugLog;


public class TypeUtil {
	private static final Pattern pattern = Pattern.compile("([\\.a-z0-9]*)\\.([A-Za-z][A-Za-z0-9_]*)");

	public static String removePackages(String string) {
		Matcher matcher = pattern.matcher(string);
		return matcher.replaceAll("$2");
	}

	public static String getClassName(Type type) {
		if (type instanceof Class) {
			Class javaClass = (Class)type;
			return javaClass.isArray() ? getClassName(javaClass.getComponentType()) + "[]" : javaClass.getName();
		} else {
			Type[] typeArray;
			if (type instanceof WildcardType) {
				WildcardType wildcardType = (WildcardType)type;
				typeArray = wildcardType.getUpperBounds();
				Type[] typeArray2 = wildcardType.getLowerBounds();
				return handleBounds("?", typeArray, typeArray2);
			} else if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType)type;
				typeArray = parameterizedType.getActualTypeArguments();
				String string = getClassName(parameterizedType.getRawType());
				if (typeArray.length == 0) {
					return string;
				} else {
					StringBuilder stringBuilder = new StringBuilder(string);
					stringBuilder.append("<");
					for (int int1 = 0; int1 < typeArray.length; ++int1) {
						if (int1 > 0) {
							stringBuilder.append(", ");
						}

						stringBuilder.append(getClassName(typeArray[int1]));
					}

					stringBuilder.append(">");
					return stringBuilder.toString();
				}
			} else if (type instanceof TypeVariable) {
				TypeVariable typeVariable = (TypeVariable)type;
				return typeVariable.getName();
			} else if (type instanceof GenericArrayType) {
				GenericArrayType genericArrayType = (GenericArrayType)type;
				return getClassName(genericArrayType.getGenericComponentType()) + "[]";
			} else {
				DebugLog.log("got unknown: " + type + ", " + type.getClass());
				return "unknown";
			}
		}
	}

	static String handleBounds(String string, Type[] typeArray, Type[] typeArray2) {
		StringBuilder stringBuilder;
		boolean boolean1;
		Type[] typeArray3;
		int int1;
		int int2;
		Type type;
		if (typeArray != null) {
			if (typeArray.length == 1 && typeArray[0] == Object.class) {
				return string;
			}

			if (typeArray.length >= 1) {
				stringBuilder = new StringBuilder();
				boolean1 = true;
				typeArray3 = typeArray;
				int1 = typeArray.length;
				for (int2 = 0; int2 < int1; ++int2) {
					type = typeArray3[int2];
					if (boolean1) {
						boolean1 = false;
					} else {
						stringBuilder.append(", ");
					}

					stringBuilder.append(getClassName(type));
				}

				return string + " extends " + stringBuilder.toString();
			}
		}

		if (typeArray2 != null && typeArray2.length > 0) {
			stringBuilder = new StringBuilder();
			boolean1 = true;
			typeArray3 = typeArray2;
			int1 = typeArray2.length;
			for (int2 = 0; int2 < int1; ++int2) {
				type = typeArray3[int2];
				if (boolean1) {
					boolean1 = false;
				} else {
					stringBuilder.append(", ");
				}

				stringBuilder.append(getClassName(type));
			}

			return string + " super " + stringBuilder.toString();
		} else {
			return "unknown type";
		}
	}
}
