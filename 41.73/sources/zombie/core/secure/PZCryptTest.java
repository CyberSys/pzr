package zombie.core.secure;

import org.junit.Assert;
import org.junit.Test;


public class PZCryptTest extends Assert {

	@Test
	public void hash() {
		String string = PZcrypt.hash("123456");
		assertEquals("$2a$12$O/BFHoDFPrfFaNPAACmWpuPkOtwkznuRQ7saS6/ouHjTT9KuVcKfq", string);
	}

	@Test
	public void hashSalt() {
		String string = PZcrypt.hashSalt("1234567");
		String string2 = PZcrypt.hashSalt("1234567");
		assertNotEquals(string, string2);
		boolean boolean1 = PZcrypt.checkHashSalt(string, "1234567");
		assertEquals(true, boolean1);
		boolean1 = PZcrypt.checkHashSalt(string, "1238567");
		assertEquals(false, boolean1);
		boolean1 = PZcrypt.checkHashSalt(string2, "1234567");
		assertEquals(true, boolean1);
		boolean1 = PZcrypt.checkHashSalt(string2, "dnfgdf;godf;ogdogi;");
		assertEquals(false, boolean1);
	}
}
