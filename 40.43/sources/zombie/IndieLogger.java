package zombie;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;


public class IndieLogger {
	public static Logger logger;
	private static FileWriter fwrite;

	public static void init() throws IOException {
	}

	private static String getCacheDir() {
		String string = System.getProperty("deployment.user.cachedir");
		if (string == null || System.getProperty("os.name").startsWith("Win")) {
			string = System.getProperty("java.io.tmpdir");
		}

		return string + File.separator + "lwjglcache";
	}

	public static void Log(String string) {
	}
}
