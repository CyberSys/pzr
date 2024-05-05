package zombie.network;

import java.io.IOException;
import zombie.core.logger.ExceptionLogger;


public final class DesktopBrowser {
	private static final String[] browsers = new String[]{"google-chrome", "firefox", "mozilla", "epiphany", "konqueror", "netscape", "opera", "links", "lynx", "chromium", "brave-browser"};

	public static boolean openURL(String string) {
		try {
			if (System.getProperty("os.name").contains("OS X")) {
				Runtime.getRuntime().exec(String.format("open %s", string));
				return true;
			}

			if (System.getProperty("os.name").startsWith("Win")) {
				Runtime.getRuntime().exec(String.format("rundll32 url.dll,FileProtocolHandler %s", string));
				return true;
			}

			String[] stringArray = browsers;
			int int1 = stringArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				String string2 = stringArray[int2];
				Process process = Runtime.getRuntime().exec(new String[]{"which", string2});
				if (process.getInputStream().read() != -1) {
					Runtime.getRuntime().exec(new String[]{string2, string});
					return true;
				}
			}
		} catch (IOException ioException) {
			ExceptionLogger.logException(ioException);
		}

		return false;
	}
}
