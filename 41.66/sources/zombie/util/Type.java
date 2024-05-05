package zombie.util;


public class Type {

	public static Object tryCastTo(Object object, Class javaClass) {
		return javaClass.isInstance(object) ? javaClass.cast(object) : null;
	}

	public static boolean asBoolean(Object object) {
		return asBoolean(object, false);
	}

	public static boolean asBoolean(Object object, boolean boolean1) {
		if (object == null) {
			return boolean1;
		} else {
			Boolean Boolean1 = (Boolean)tryCastTo(object, Boolean.class);
			return Boolean1 == null ? boolean1 : Boolean1;
		}
	}
}
