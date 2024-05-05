package zombie.core.Collections;

import java.lang.reflect.Field;
import sun.misc.Unsafe;


class UtilUnsafe {

	private UtilUnsafe() {
	}

	public static Unsafe getUnsafe() {
		if (UtilUnsafe.class.getClassLoader() == null) {
			return Unsafe.getUnsafe();
		} else {
			try {
				Field field = Unsafe.class.getDeclaredField("theUnsafe");
				field.setAccessible(true);
				return (Unsafe)field.get(UtilUnsafe.class);
			} catch (Exception exception) {
				throw new RuntimeException("Could not obtain access to sun.misc.Unsafe", exception);
			}
		}
	}
}
