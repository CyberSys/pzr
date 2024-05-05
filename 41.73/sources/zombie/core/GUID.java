package zombie.core;

import java.util.UUID;


public class GUID {

	public static String generateGUID() {
		UUID uUID = UUID.randomUUID();
		return uUID.toString();
	}
}
