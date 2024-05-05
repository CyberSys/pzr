package zombie.core.secure;

import org.mindrot.jbcrypt.BCrypt;


public class PZcrypt {
	static String salt = "$2a$12$O/BFHoDFPrfFaNPAACmWpu";

	public static String hash(String string, boolean boolean1) {
		return boolean1 && string.isEmpty() ? string : BCrypt.hashpw(string, salt);
	}

	public static String hash(String string) {
		return hash(string, true);
	}

	public static String hashSalt(String string) {
		return BCrypt.hashpw(string, BCrypt.gensalt(12));
	}

	public static boolean checkHashSalt(String string, String string2) {
		return BCrypt.checkpw(string2, string);
	}
}
