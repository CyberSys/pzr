package zombie.core.input;

import zombie.debug.DebugLog;


public class XInput {

	public static void init() {
		String string = "";
		if ("1".equals(System.getProperty("zomboid.debuglibs"))) {
			string = "d";
		}

		try {
			if (System.getProperty("os.name").startsWith("Win")) {
				if (System.getProperty("sun.arch.data.model").equals("64")) {
					System.loadLibrary("PZ_XInput64" + string);
				} else {
					System.loadLibrary("PZ_XInput32" + string);
				}

				System.setProperty("jinput.plugins", "zombie.core.input.XInputEnvironmentPlugin");
			}
		} catch (UnsatisfiedLinkError unsatisfiedLinkError) {
			DebugLog.log("Failed to load XInput library");
		}
	}
}
